package com.zhpan.bannerview.manager

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.zhpan.bannerview.constants.PageStyle
import com.zhpan.bannerview.transform.ScaleInTransformer
import com.zhpan.bannerview.utils.BannerUtils
import com.zhpan.indicator.option.IndicatorOptions

/**
 * <pre>
 * Created by zhpan on 2019/11/20.
 * Description:BannerViewPager的配置参数
</pre> *
 */
open class BannerOptions {
    var offScreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
    var interval = 0
    var isCanLoop = false
    var isAutoPlay = false
    var indicatorGravity = 0
    var pageMargin: Int = BannerUtils.dp2px(20f)
    var revealWidth: Int = BannerUtils.dp2px(20f)
    var pageStyle = PageStyle.NORMAL
    var pageScale = ScaleInTransformer.DEFAULT_MIN_SCALE
    var indicatorMargin: IndicatorMargin? = null
        private set
    var indicatorVisibility = View.VISIBLE
    var scrollDuration = 0
    var roundRectRadius = 0
    var isUserInputEnabled = true
    var orientation = ViewPager2.ORIENTATION_HORIZONTAL

    val indicatorOptions: IndicatorOptions = IndicatorOptions()

    val indicatorNormalColor: Int
        get() = indicatorOptions.normalSliderColor

    val indicatorCheckedColor: Int
        get() = indicatorOptions.checkedSliderColor

    val normalIndicatorWidth: Int
        get() = indicatorOptions.normalSliderWidth.toInt()

    fun setIndicatorSliderColor(normalColor: Int, checkedColor: Int) {
        indicatorOptions.setSliderColor(normalColor, checkedColor)
    }

    fun setIndicatorSliderWidth(normalWidth: Int, checkedWidth: Int) {
        indicatorOptions.setSliderWidth(normalWidth.toFloat(), checkedWidth.toFloat())
    }

    val checkedIndicatorWidth: Int
        get() = indicatorOptions.checkedSliderWidth.toInt()

    var indicatorStyle: Int
        get() = indicatorOptions.indicatorStyle
        set(indicatorStyle) {
            indicatorOptions.indicatorStyle = indicatorStyle
        }

    var indicatorSlideMode: Int
        get() = indicatorOptions.slideMode
        set(indicatorSlideMode) {
            indicatorOptions.slideMode = indicatorSlideMode
        }

    var indicatorGap: Float
        get() = indicatorOptions.sliderGap
        set(indicatorGap) {
            indicatorOptions.sliderGap = indicatorGap
        }

    val indicatorHeight: Float
        get() = indicatorOptions.sliderHeight

    fun setIndicatorHeight(indicatorHeight: Int) {
        indicatorOptions.sliderHeight = indicatorHeight.toFloat()
    }

    fun setIndicatorMargin(left: Int, top: Int, right: Int, bottom: Int) {
        indicatorMargin = IndicatorMargin(left, top, right, bottom)
    }

    fun resetIndicatorOptions() {
        indicatorOptions.currentPosition = 0
        indicatorOptions.slideProgress = 0f
    }

    class IndicatorMargin(val left: Int, val top: Int, val right: Int, val bottom: Int)

}