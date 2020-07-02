package com.zhpan.bannerview.constants

/**
 * <pre>
 * Created by zhangpan on 2019-10-18.
 * Description:
</pre> *
 */
interface PageStyle {
    companion object {
        const val NORMAL = 0
        const val MULTI_PAGE = 1 shl 1
        /**
         * Requires Api Version >= 21
         */
        const val MULTI_PAGE_OVERLAP = 1 shl 2
        const val MULTI_PAGE_SCALE = 1 shl 3
    }
}