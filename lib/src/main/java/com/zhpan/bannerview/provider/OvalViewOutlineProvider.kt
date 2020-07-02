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
 * Description:圆形效果
</pre> *
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class OvalViewOutlineProvider : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        val selfRect: Rect
        val rect = Rect()
        view.getGlobalVisibleRect(rect)
        selfRect = getOvalRect(rect)
        outline.setOval(selfRect)
    }

    /**
     * 以矩形的中心点为圆心,较短的边为直径画圆
     *
     * @param rect
     * @return
     */
    private fun getOvalRect(rect: Rect): Rect {
        val width = rect.right - rect.left
        val height = rect.bottom - rect.top
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int
        val dW = width / 2
        val dH = height / 2
        if (width > height) {
            left = dW - dH
            top = 0
            right = dW + dH
            bottom = dH * 2
        } else {
            left = dH - dW
            top = 0
            right = dH + dW
            bottom = dW * 2
        }
        return Rect(left, top, right, bottom)
    }
}