package com.zhpan.bannerview.manager

import android.content.Context
import android.util.AttributeSet

/**
 * <pre>
 * Created by zhpan on 2019/11/20.
 * Description:
</pre> *
 */
class BannerManager {
    private var mBannerOptions: BannerOptions?
    private val mAttributeController: AttributeController
    val bannerOptions: BannerOptions
        get() {
            if (mBannerOptions == null) {
                mBannerOptions = BannerOptions()
            }
            return mBannerOptions!!
        }

    fun initAttrs(context: Context?, attrs: AttributeSet?) {
        mAttributeController.init(context!!, attrs)
    }

    init {
        mBannerOptions = BannerOptions()
        mAttributeController = AttributeController(mBannerOptions!!)
    }
}