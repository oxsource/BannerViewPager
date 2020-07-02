package com.zhpan.bannerview.annotation

import androidx.annotation.IntDef
import com.zhpan.bannerview.constants.IndicatorGravity

/**
 * <pre>
 * Created by zhangpan on 2019-10-18.
 * Description:指示器显示位置
</pre> *
 */
@IntDef(IndicatorGravity.CENTER, IndicatorGravity.START, IndicatorGravity.END)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class AIndicatorGravity