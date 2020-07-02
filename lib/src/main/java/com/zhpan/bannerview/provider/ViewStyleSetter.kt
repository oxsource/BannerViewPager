package com.zhpan.bannerview.provider

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi

/**
 * <pre>
 * Created by zhangpan on 2018/12/26.
 * Description:为View设置圆角/圆形效果，支持View及ViewGroup，适用Android 5.1及以上系统。
</pre> *
 */
class ViewStyleSetter(private val mView: View) {
    /**
     * 为View设置圆角效果
     *
     * @param radius 圆角半径
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setRoundRect(radius: Float) {
        mView.clipToOutline = true // 用outline裁剪内容区域
        mView.outlineProvider = RoundViewOutlineProvider(radius)
    }

    /**
     * 设置View为圆形
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setOvalView() {
        mView.clipToOutline = true // 用outline裁剪内容区域
        mView.outlineProvider = OvalViewOutlineProvider()
    }

    /**
     * 清除View的圆角效果
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun clearShapeStyle() {
        mView.clipToOutline = false
    }

}