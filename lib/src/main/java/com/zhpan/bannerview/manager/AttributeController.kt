package com.zhpan.bannerview.manager

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import com.zhpan.bannerview.R
import com.zhpan.bannerview.utils.BannerUtils

/**
 * <pre>
 * Created by zhpan on 2019/11/20.
 * Description:初始化xml的自定义属性
</pre> *
 */
class AttributeController(private val mBannerOptions: BannerOptions) {
    fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerViewPager)
            initBannerAttrs(typedArray)
            initIndicatorAttrs(typedArray)
            typedArray.recycle()
        }
    }

    private fun initIndicatorAttrs(typedArray: TypedArray) {
        val indicatorCheckedColor = typedArray.getColor(R.styleable.BannerViewPager_bvp_indicator_checked_color, Color.parseColor("#8C18171C"))
        val indicatorNormalColor = typedArray.getColor(R.styleable.BannerViewPager_bvp_indicator_normal_color, Color.parseColor("#8C6C6D72"))
        val normalIndicatorWidth = typedArray.getDimension(R.styleable.BannerViewPager_bvp_indicator_radius, BannerUtils.dp2px(8f).toFloat()).toInt()
        val indicatorGravity = typedArray.getInt(R.styleable.BannerViewPager_bvp_indicator_gravity, 0)
        val indicatorStyle = typedArray.getInt(R.styleable.BannerViewPager_bvp_indicator_style, 0)
        val indicatorSlideMode = typedArray.getInt(R.styleable.BannerViewPager_bvp_indicator_slide_mode, 0)
        val indicatorVisibility = typedArray.getInt(R.styleable.BannerViewPager_bvp_indicator_visibility, 0)
        mBannerOptions.setIndicatorSliderColor(indicatorNormalColor, indicatorCheckedColor)
        mBannerOptions.setIndicatorSliderWidth(normalIndicatorWidth, normalIndicatorWidth)
        mBannerOptions.indicatorGravity = indicatorGravity
        mBannerOptions.indicatorStyle = indicatorStyle
        mBannerOptions.indicatorSlideMode = indicatorSlideMode
        mBannerOptions.indicatorVisibility = indicatorVisibility
        mBannerOptions.indicatorGap = normalIndicatorWidth.toFloat()
        mBannerOptions.setIndicatorHeight(normalIndicatorWidth / 2)
    }

    private fun initBannerAttrs(typedArray: TypedArray) {
        val interval = typedArray.getInteger(R.styleable.BannerViewPager_bvp_interval, 3000)
        val isAutoPlay = typedArray.getBoolean(R.styleable.BannerViewPager_bvp_auto_play, true)
        val isCanLoop = typedArray.getBoolean(R.styleable.BannerViewPager_bvp_can_loop, true)
        val pageMargin = typedArray.getDimension(R.styleable.BannerViewPager_bvp_page_margin, 0f).toInt()
        val roundCorner = typedArray.getDimension(R.styleable.BannerViewPager_bvp_round_corner, 0f).toInt()
        val revealWidth = typedArray.getDimension(R.styleable.BannerViewPager_bvp_reveal_width, 0f).toInt()
        val pageStyle = typedArray.getInt(R.styleable.BannerViewPager_bvp_page_style, 0)
        val scrollDuration = typedArray.getInt(R.styleable.BannerViewPager_bvp_scroll_duration, 0)
        mBannerOptions.interval = interval
        mBannerOptions.isAutoPlay = isAutoPlay
        mBannerOptions.isCanLoop = isCanLoop
        mBannerOptions.pageMargin = pageMargin
        mBannerOptions.roundRectRadius = roundCorner
        mBannerOptions.revealWidth = revealWidth
        mBannerOptions.pageStyle = pageStyle
        mBannerOptions.scrollDuration = scrollDuration
    }

}