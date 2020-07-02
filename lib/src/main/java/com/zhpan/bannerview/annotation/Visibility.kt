package com.zhpan.bannerview.annotation

import android.view.View
import androidx.annotation.IntDef

/**
 * <pre>
 * Created by zhangpan on 2019-11-12.
</pre> *
 */
@IntDef(View.VISIBLE, View.INVISIBLE, View.GONE)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Visibility