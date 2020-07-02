package com.zhpan.bannerview.provider

import android.annotation.TargetApi
import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider

/**
 * <pre>
 * Created by zhangpan on 2018/12/26.
 * Description:圆角效果
</pre> *
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class RoundViewOutlineProvider(//圆角弧度
        private val mRadius: Float) : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        val selfRect = Rect(0, 0, view.width, view.height) // 绘制区域
        outline.setRoundRect(selfRect, mRadius)
    }

}