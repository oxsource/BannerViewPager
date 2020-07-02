package com.zhpan.bannerview.annotation

import androidx.annotation.IntDef
import com.zhpan.bannerview.constants.PageStyle

/**
 * <pre>
 * Created by zhangpan on 2019-11-06.
 * Description:
</pre> *
 */
@IntDef(PageStyle.NORMAL, PageStyle.MULTI_PAGE, PageStyle.MULTI_PAGE_OVERLAP, PageStyle.MULTI_PAGE_SCALE)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class APageStyle