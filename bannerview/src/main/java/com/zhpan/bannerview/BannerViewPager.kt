package com.zhpan.bannerview

import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.zhpan.bannerview.annotation.AIndicatorGravity
import com.zhpan.bannerview.annotation.APageStyle
import com.zhpan.bannerview.annotation.Visibility
import com.zhpan.bannerview.constants.IndicatorGravity
import com.zhpan.bannerview.constants.PageStyle
import com.zhpan.bannerview.manager.BannerManager
import com.zhpan.bannerview.manager.BannerOptions
import com.zhpan.bannerview.provider.ScrollDurationManger.Companion.reflectLayoutManager
import com.zhpan.bannerview.provider.ViewStyleSetter
import com.zhpan.bannerview.transform.OverlapPageTransformer
import com.zhpan.bannerview.transform.ScaleInTransformer
import com.zhpan.bannerview.utils.BannerUtils.dp2px
import com.zhpan.indicator.IndicatorView
import com.zhpan.indicator.annotation.AIndicatorSlideMode
import com.zhpan.indicator.annotation.AIndicatorStyle
import com.zhpan.indicator.base.IIndicator
import com.zhpan.indicator.option.IndicatorOptions

/**
 * Created by zhpan on 2017/3/28.
 */
class BannerViewPager<T, VH : BaseViewHolder<T>?> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    private var currentIndex = 0
    private var isCustomIndicator = false
    private var isLooping = false
    private var mOnPageClickListener: OnPageClickListener? = null
    private var mIndicatorView: IIndicator? = null
    private var mIndicatorLayout: RelativeLayout? = null
    private lateinit var mViewPager: ViewPager2
    private lateinit var mBannerManager: BannerManager
    private val mHandler = Handler()
    private var adapter: BaseBannerAdapter<T, VH>? = null
    private var onPageChangeCallback: OnPageChangeCallback? = null
    private val mRunnable = Runnable { handlePosition() }
    private var startX = 0
    private var startY = 0
    private lateinit var mCompositePageTransformer: CompositePageTransformer
    private var mMarginPageTransformer: MarginPageTransformer? = null
    private var mPageTransformer: ViewPager2.PageTransformer? = null

    private val mOnPageChangeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            val size: Int = getListSize()
            if (size <= 0) return
            val realPosition: Int = adapter?.getRealIndex(position) ?: return
            onPageChangeCallback?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
            mIndicatorView?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val size: Int = getListSize()
            currentIndex = adapter?.getRealIndex(position) ?: -1
            if (size > 0 && isCanLoop && position == 0 || position == BaseBannerAdapter.MAX_VALUE - 1) {
                setCurrentItem(currentIndex, false)
            }
            onPageChangeCallback?.onPageSelected(currentIndex)
            mIndicatorView?.onPageSelected(currentIndex)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            mIndicatorView?.onPageScrollStateChanged(state)
            onPageChangeCallback?.onPageScrollStateChanged(state)
        }
    }

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        mCompositePageTransformer = CompositePageTransformer()
        mBannerManager = BannerManager()
        mBannerManager.initAttrs(context, attrs)
        initView()
    }

    private fun initView() {
        View.inflate(context, R.layout.bvp_layout, this)
        mViewPager = findViewById(R.id.vp_main)
        mIndicatorLayout = findViewById(R.id.bvp_layout_indicator)
        mViewPager.setPageTransformer(mCompositePageTransformer)
    }

    override fun onDetachedFromWindow() {
        stopLoop()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startLoop()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                isLooping = true
                stopLoop()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                isLooping = false
                startLoop()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val size: Int = getListSize()
        if (!mViewPager.isUserInputEnabled || size <= 1) {
            return super.onInterceptTouchEvent(ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x.toInt()
                startY = ev.y.toInt()
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x.toInt()
                val endY = ev.y.toInt()
                val disX = Math.abs(endX - startX)
                val disY = Math.abs(endY - startY)
                val orientation = mBannerManager.bannerOptions.orientation
                if (orientation == ViewPager2.ORIENTATION_VERTICAL) {
                    onVerticalActionMove(endY, disX, disY)
                } else if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                    onHorizontalActionMove(endX, disX, disY)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(false)
            MotionEvent.ACTION_OUTSIDE -> {
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun getListSize(): Int = adapter?.getList()?.size ?: 0

    private fun onVerticalActionMove(endY: Int, disX: Int, disY: Int) {
        if (disY > disX) {
            if (!isCanLoop) {
                if (currentIndex == 0 && endY - startY > 0) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else if (currentIndex == getListSize() - 1 && endY - startY < 0) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            } else {
                parent.requestDisallowInterceptTouchEvent(true)
            }
        } else if (disX > disY) {
            parent.requestDisallowInterceptTouchEvent(false)
        }
    }

    private fun onHorizontalActionMove(endX: Int, disX: Int, disY: Int) {
        if (disX > disY) {
            if (!isCanLoop) {
                if (currentIndex == 0 && endX - startX > 0) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else if (currentIndex == getListSize() - 1 && endX - startX < 0) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            } else {
                parent.requestDisallowInterceptTouchEvent(true)
            }
        } else if (disY > disX) {
            parent.requestDisallowInterceptTouchEvent(false)
        }
    }

    private fun handlePosition() {
        val v: BaseBannerAdapter<T, VH> = adapter ?: return
        if (v.getList().isEmpty()) return
        mViewPager.currentItem = mViewPager.currentItem + 1
        mHandler.postDelayed(mRunnable, interval.toLong())
    }

    private fun initBannerData() {
        val list: List<T> = adapter?.getList() ?: return
        setIndicatorValues(list)
        setupViewPager(list)
        initRoundCorner()
    }

    private fun setIndicatorValues(list: List<T>) {
        val indicatorVisibility: Int = mBannerManager.bannerOptions.indicatorVisibility
        if (indicatorVisibility == View.GONE || indicatorVisibility == View.INVISIBLE) {
            return
        }
        mIndicatorLayout?.visibility = indicatorVisibility
        val bannerOptions: BannerOptions = mBannerManager.bannerOptions
        bannerOptions.resetIndicatorOptions()
        if (isCustomIndicator && null != mIndicatorView) {
            initIndicator(mIndicatorView!!)
        } else {
            initIndicator(IndicatorView(context))
        }
        mIndicatorView?.setIndicatorOptions(bannerOptions.indicatorOptions)
        bannerOptions.indicatorOptions.pageSize = list.size
        mIndicatorView?.notifyDataChanged()
    }

    private fun initIndicator(indicatorView: IIndicator) {
        mIndicatorView = indicatorView
        if ((mIndicatorView as View?)?.parent == null) {
            mIndicatorLayout?.removeAllViews()
            mIndicatorLayout?.addView(mIndicatorView as View?)
            initIndicatorViewMargin()
            initIndicatorGravity()
        }
    }

    private fun initIndicatorGravity() {
        val layoutParams = (mIndicatorView as View?)?.layoutParams as LayoutParams
        when (mBannerManager.bannerOptions.indicatorGravity) {
            IndicatorGravity.CENTER -> layoutParams.addRule(CENTER_HORIZONTAL)
            IndicatorGravity.START -> layoutParams.addRule(ALIGN_PARENT_LEFT)
            IndicatorGravity.END -> layoutParams.addRule(ALIGN_PARENT_RIGHT)
        }
    }

    private fun initIndicatorViewMargin() {
        val layoutParams = (mIndicatorView as View?)?.layoutParams as MarginLayoutParams
        val margin = mBannerManager.bannerOptions.indicatorMargin
        if (margin == null) {
            val dp10 = dp2px(10f)
            layoutParams.setMargins(dp10, dp10, dp10, dp10)
        } else {
            layoutParams.setMargins(margin.left, margin.top, margin.right, margin.bottom)
        }
    }

    private fun initRoundCorner() {
        val roundCorner = mBannerManager.bannerOptions.roundRectRadius
        if (roundCorner > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val viewStyleSetter = ViewStyleSetter(this)
            viewStyleSetter.setRoundRect(roundCorner.toFloat())
        }
    }

    private fun setupViewPager(list: List<T>) {
        if (adapter == null) {
            throw NullPointerException("You must set adapter for BannerViewPager")
        }
        val bannerOptions = mBannerManager.bannerOptions
        if (bannerOptions.scrollDuration != 0) reflectLayoutManager(mViewPager, bannerOptions.scrollDuration)
        currentIndex = 0
        adapter?.setCanLoop(isCanLoop)
        adapter?.setPageClickListener(mOnPageClickListener)
        mViewPager.adapter = adapter
        if (list.size > 1 && isCanLoop) {
            mViewPager.setCurrentItem(BaseBannerAdapter.MAX_VALUE / 2 - BaseBannerAdapter.MAX_VALUE / 2 % list.size + 1, false)
        }
        mViewPager.unregisterOnPageChangeCallback(mOnPageChangeCallback)
        mViewPager.registerOnPageChangeCallback(mOnPageChangeCallback)
        mViewPager.orientation = bannerOptions.orientation
        mViewPager.isUserInputEnabled = bannerOptions.isUserInputEnabled
        mViewPager.offscreenPageLimit = bannerOptions.offScreenPageLimit
        initPageStyle()
        startLoop()
    }

    private fun initPageStyle() {
        when (mBannerManager.bannerOptions.pageStyle) {
            PageStyle.MULTI_PAGE -> setMultiPageStyle(false, ScaleInTransformer.MAX_SCALE)
            PageStyle.MULTI_PAGE_OVERLAP -> setMultiPageStyle(true, mBannerManager.bannerOptions.pageScale)
            PageStyle.MULTI_PAGE_SCALE -> setMultiPageStyle(false, mBannerManager.bannerOptions.pageScale)
        }
    }

    private fun setMultiPageStyle(overlap: Boolean, scale: Float) {
        val recyclerView = mViewPager.getChildAt(0) as RecyclerView
        val bannerOptions = mBannerManager.bannerOptions
        val orientation = bannerOptions.orientation
        val padding = bannerOptions.pageMargin + bannerOptions.revealWidth
        if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) recyclerView.setPadding(padding, 0, padding, 0) else if (orientation == ViewPager2.ORIENTATION_VERTICAL) {
            recyclerView.setPadding(0, padding, 0, padding)
        }
        recyclerView.clipToPadding = false
        if (mPageTransformer != null) {
            mCompositePageTransformer.removeTransformer(mPageTransformer!!)
        }
        mPageTransformer = if (overlap && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            OverlapPageTransformer(orientation, scale, 0f, 1f, 0f)
        } else {
            ScaleInTransformer(scale)
        }
        addPageTransformer(mPageTransformer)
    }

    private val interval: Int = mBannerManager.bannerOptions.interval

    private val isAutoPlay: Boolean = mBannerManager.bannerOptions.isAutoPlay

    private val isCanLoop: Boolean = mBannerManager.bannerOptions.isCanLoop

    fun getAdapter(): BaseBannerAdapter<T, VH>? = adapter

    fun getRealViewPager(): ViewPager2 = mViewPager

    fun indexOf(position: Int): T? {
        val list: List<T> = getAdapter()?.getList() ?: return null
        return if (position in list.indices) list[position] else null
    }

    /**
     * Start loop
     */
    fun startLoop() {
        val v: BaseBannerAdapter<T, VH> = adapter ?: return
        if (!isLooping && isAutoPlay && v.getList().size > 1) {
            mHandler.postDelayed(mRunnable, interval.toLong())
            isLooping = true
        }
    }

    /**
     * stoop loop
     */
    fun stopLoop() {
        if (isLooping) {
            mHandler.removeCallbacks(mRunnable)
            isLooping = false
        }
    }

    fun setAdapter(adapter: BaseBannerAdapter<T, VH>?): BannerViewPager<T, VH> {
        this.adapter = adapter
        return this
    }

    /**
     * Set round rectangle effect for BannerViewPager.
     *
     *
     * Require SDK_INT>=LOLLIPOP(API 21)
     *
     * @param radius round radius
     */
    fun setRoundCorner(radius: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.roundRectRadius = radius
        return this
    }

    /**
     * Set round rectangle effect for BannerViewPager.
     *
     *
     * Require SDK_INT>=LOLLIPOP(API 21)
     *
     * @param radius round radius
     */
    fun setRoundRect(radius: Int): BannerViewPager<T, VH> {
        setRoundCorner(radius)
        return this
    }

    /**
     * Enable/disable auto play
     *
     * @param autoPlay is enable auto play
     */
    fun setAutoPlay(autoPlay: Boolean): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.isAutoPlay = autoPlay
        if (isAutoPlay) {
            mBannerManager.bannerOptions.isCanLoop = true
        }
        return this
    }

    /**
     * Enable/disable loop
     *
     * @param canLoop is can loop
     */
    fun setCanLoop(canLoop: Boolean): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.isCanLoop = canLoop
        if (!canLoop) {
            mBannerManager.bannerOptions.isAutoPlay = false
        }
        return this
    }

    /**
     * Set loop interval
     *
     * @param interval loop interval,unit is millisecond.
     */
    fun setInterval(interval: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.interval = interval
        return this
    }

    /**
     * @param transformer PageTransformer that will modify each page's animation properties
     */
    fun setPageTransformer(transformer: ViewPager2.PageTransformer?): BannerViewPager<T, VH> {
        if (transformer != null) mViewPager.setPageTransformer(transformer)
        return this
    }

    /**
     * @param transformer PageTransformer that will modify each page's animation properties
     */
    fun addPageTransformer(transformer: ViewPager2.PageTransformer?): BannerViewPager<T, VH> {
        if (transformer != null) {
            mCompositePageTransformer.addTransformer(transformer)
        }
        return this
    }

    fun removeTransformer(transformer: ViewPager2.PageTransformer?) {
        if (transformer != null) {
            mCompositePageTransformer.removeTransformer(transformer)
        }
    }

    /**
     * set page margin
     *
     * @param pageMargin page margin
     */
    fun setPageMargin(pageMargin: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.pageMargin = pageMargin
        if (mMarginPageTransformer != null) {
            mCompositePageTransformer.removeTransformer(mMarginPageTransformer!!)
        }
        mMarginPageTransformer = MarginPageTransformer(pageMargin)
        mCompositePageTransformer.addTransformer(mMarginPageTransformer!!)
        return this
    }

    /**
     * set item click listener
     *
     * @param onPageClickListener item click listener
     */
    fun setOnPageClickListener(onPageClickListener: OnPageClickListener?): BannerViewPager<T, VH> {
        mOnPageClickListener = onPageClickListener
        return this
    }

    /**
     * Set page scroll duration
     *
     * @param scrollDuration page scroll duration
     */
    fun setScrollDuration(scrollDuration: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.scrollDuration = scrollDuration
        return this
    }

    /**
     * set indicator color
     *
     * @param checkedColor checked color of indicator
     * @param normalColor  unchecked color of indicator
     */
    fun setIndicatorSliderColor(@ColorInt normalColor: Int,
                                @ColorInt checkedColor: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.setIndicatorSliderColor(normalColor, checkedColor)
        return this
    }

    /**
     * set indicator circle radius
     *
     *
     * if the indicator style is [com.zhpan.indicator.enums.IndicatorStyle.DASH]
     * or [com.zhpan.indicator.enums.IndicatorStyle.ROUND_RECT]
     * the indicator dash width=2*radius
     *
     * @param radius 指示器圆点半径
     */
    fun setIndicatorSliderRadius(radius: Int): BannerViewPager<T, VH> {
        setIndicatorSliderRadius(radius, radius)
        return this
    }

    /**
     * set indicator circle radius
     *
     * @param normalRadius  unchecked circle radius
     * @param checkedRadius checked circle radius
     */
    fun setIndicatorSliderRadius(normalRadius: Int, checkedRadius: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.setIndicatorSliderWidth(normalRadius * 2, checkedRadius * 2)
        return this
    }

    fun setIndicatorSliderWidth(indicatorWidth: Int): BannerViewPager<T, VH> {
        setIndicatorSliderWidth(indicatorWidth, indicatorWidth)
        return this
    }

    /**
     * Set indicator dash width，if indicator style is [com.zhpan.indicator.enums.IndicatorStyle.CIRCLE],
     * the indicator circle radius is indicatorWidth/2.
     *
     * @param normalWidth if the indicator style is [com.zhpan.indicator.enums.IndicatorStyle.DASH] the params means unchecked dash width
     * if the indicator style is [com.zhpan.indicator.enums.IndicatorStyle.ROUND_RECT]  means unchecked round rectangle width
     * if the indicator style is [com.zhpan.indicator.enums.IndicatorStyle.CIRCLE] means unchecked circle diameter
     * @param checkWidth  if the indicator style is [com.zhpan.indicator.enums.IndicatorStyle.DASH] the params means checked dash width
     * if the indicator style is [com.zhpan.indicator.enums.IndicatorStyle.ROUND_RECT] the params means checked round rectangle width
     * if the indicator style is [com.zhpan.indicator.enums.IndicatorStyle.CIRCLE] means checked circle diameter
     */
    fun setIndicatorSliderWidth(normalWidth: Int, checkWidth: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.setIndicatorSliderWidth(normalWidth, checkWidth)
        return this
    }

    fun setIndicatorHeight(indicatorHeight: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.setIndicatorHeight(indicatorHeight)
        return this
    }

    /**
     * Set Indicator gap of dash/circle
     *
     * @param indicatorGap indicator gap
     */
    fun setIndicatorSliderGap(indicatorGap: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.indicatorGap = indicatorGap.toFloat()
        return this
    }

    /**
     * Set the visibility state of indicator view.
     *
     * @param visibility One of [View.VISIBLE], [View.INVISIBLE], or [View.GONE].
     */
    fun setIndicatorVisibility(@Visibility visibility: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.indicatorVisibility = visibility
        return this
    }

    /**
     * set indicator gravity in BannerViewPager
     *
     * @param gravity indicator gravity
     * [com.zhpan.bannerview.constants.IndicatorGravity.CENTER]
     * [com.zhpan.bannerview.constants.IndicatorGravity.START]
     * [com.zhpan.bannerview.constants.IndicatorGravity.END]
     */
    fun setIndicatorGravity(@AIndicatorGravity gravity: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.indicatorGravity = gravity
        return this
    }

    /**
     * Set Indicator slide mode，default value is [com.zhpan.indicator.enums.IndicatorSlideMode.NORMAL]
     *
     * @param slideMode Indicator slide mode
     * @see com.zhpan.indicator.enums.IndicatorSlideMode.NORMAL
     *
     * @see com.zhpan.indicator.enums.IndicatorSlideMode.SMOOTH
     */
    fun setIndicatorSlideMode(@AIndicatorSlideMode slideMode: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.indicatorSlideMode = slideMode
        return this
    }

    /**
     * Set custom indicator.
     * the custom indicator view must extends BaseIndicator or implements IIndicator
     *
     * @param customIndicator custom indicator view
     */
    fun setIndicatorView(customIndicator: IIndicator?): BannerViewPager<T, VH> {
        if (customIndicator is View) {
            isCustomIndicator = true
            mIndicatorView = customIndicator
        }
        return this
    }

    /**
     * Set indicator style
     *
     * @param indicatorStyle indicator style
     * @see com.zhpan.indicator.enums.IndicatorStyle.CIRCLE
     *
     * @see com.zhpan.indicator.enums.IndicatorStyle.DASH
     *
     * @see com.zhpan.indicator.enums.IndicatorStyle.ROUND_RECT
     */
    fun setIndicatorStyle(@AIndicatorStyle indicatorStyle: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.indicatorStyle = indicatorStyle
        return this
    }
    /**
     * Create BannerViewPager with data.
     * If data has fetched when create BannerViewPager,you can call this method.
     */
    /**
     * Create BannerViewPager with no data
     * If there is no data while you create BannerViewPager(for example,The data is from remote server)，you can call this method.
     * Then,while you fetch data successfully,just need call [.refreshData] method to refresh.
     */
    fun create(data: List<T>?) {
        adapter ?: throw NullPointerException("You must set adapter for BannerViewPager")
        adapter?.setList(data)
        initBannerData()
    }

    /**
     * Sets the orientation of the ViewPager2.
     *
     * @param orientation [androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL] or
     * [androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL]
     */
    fun setOrientation(@ViewPager2.Orientation orientation: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.orientation = orientation
        return this
    }

    /**
     * Refresh data.
     * Confirm the [.create] or [.create] method has been called,
     * else the data won't be shown.
     */
    fun refreshData(list: List<T>?) {
        list ?: return
        val v: BaseBannerAdapter<T, VH> = adapter ?: return
        v.setList(list)
        v.notifyDataSetChanged()
        setCurrentItem(currentIndex(), false)
        mIndicatorView?.let {
            val indicatorOptions: IndicatorOptions = mBannerManager.bannerOptions.indicatorOptions
            indicatorOptions.pageSize = list.size
            indicatorOptions.currentPosition = v.getRealIndex(mViewPager.currentItem)
            it.notifyDataChanged()
        }
        startLoop()
    }

    /**
     * @return the currently selected page position.
     */
    /**
     * Set the currently selected page. If the ViewPager has already been through its first
     * layout with its current adapter there will be a smooth animated transition between
     * the current item and the specified item.
     *
     * @param item Item index to select
     */
    fun currentIndex(): Int = currentIndex

    /**
     * Set the currently selected page.
     *
     * @param item         Item index to select
     * @param smoothScroll True to smoothly scroll to the new item, false to transition immediately
     */
    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        val size: Int = getListSize()
        if (isCanLoop && size > 1) {
            mViewPager.setCurrentItem(BaseBannerAdapter.MAX_VALUE / 2 - BaseBannerAdapter.MAX_VALUE / 2 % size + 1 + item, smoothScroll)
        } else {
            mViewPager.setCurrentItem(item, smoothScroll)
        }
    }

    /**
     * Set Page Style for Banner
     * [PageStyle.NORMAL]
     * [PageStyle.MULTI_PAGE]
     *
     * @return BannerViewPager
     */
    fun setPageStyle(@APageStyle pageStyle: Int): BannerViewPager<T, VH> {
        return setPageStyle(pageStyle, ScaleInTransformer.DEFAULT_MIN_SCALE)
    }

    fun setPageStyle(@APageStyle pageStyle: Int, pageScale: Float): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.pageStyle = pageStyle
        mBannerManager.bannerOptions.pageScale = pageScale
        return this
    }

    /**
     * @param revealWidth 一屏多页模式下两边页面显露出来的宽度
     */
    fun setRevealWidth(revealWidth: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.revealWidth = revealWidth
        return this
    }

    /**
     * Suggest to use default offScreenPageLimit.
     */
    fun setOffScreenPageLimit(offScreenPageLimit: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.offScreenPageLimit = offScreenPageLimit
        return this
    }

    fun setIndicatorMargin(left: Int, top: Int, right: Int, bottom: Int): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.setIndicatorMargin(left, top, right, bottom)
        return this
    }

    /**
     * Enable or disable user initiated scrolling
     */
    fun setUserInputEnabled(userInputEnabled: Boolean): BannerViewPager<T, VH> {
        mBannerManager.bannerOptions.isUserInputEnabled = userInputEnabled
        return this
    }

    interface OnPageClickListener {
        fun onPageClick(index: Int, position: Int)
    }

    fun registerOnPageChangeCallback(onPageChangeCallback: OnPageChangeCallback?): BannerViewPager<T, VH> {
        this.onPageChangeCallback = onPageChangeCallback
        return this
    }
}