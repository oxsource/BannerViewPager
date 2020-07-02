package com.zhpan.bannerview.utils

import android.content.res.Resources

/**
 * <pre>
 * Created by zhangpan on 2019-08-14.
 * Description:
</pre> *
 */
object BannerUtils {

    @JvmStatic
    fun dp2px(dpValue: Float): Int {
        return (0.5f + dpValue * Resources.getSystem().displayMetrics.density).toInt()
    }
}