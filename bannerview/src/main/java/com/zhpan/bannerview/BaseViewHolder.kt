package com.zhpan.bannerview

import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
 * <pre>
 * Created by zhpan on 2020/4/5.
 * Attention:Don't use [RecyclerView.ViewHolder.getAdapterPosition]
 * method to get position,this method will return a fake position.
</pre> *
 */
abstract class BaseViewHolder<T>(itemView: View) : ViewHolder(itemView) {
    private val mViews = SparseArray<View?>()
    abstract fun bindData(data: T, position: Int, pageSize: Int)

    @Suppress("UNCHECKED_CAST")
    protected fun <V : View?> findView(viewId: Int): V? {
        var view = mViews[viewId]
        if (view == null) {
            view = itemView.findViewById(viewId)
            mViews.put(viewId, view)
        }
        return view as V?
    }

    protected fun setText(viewId: Int, text: String?) {
        val view = findView<View>(viewId)
        if (view is TextView) {
            view.text = text
        }
    }

    protected fun setText(viewId: Int, @StringRes textId: Int) {
        val view = findView<View>(viewId)
        if (view is TextView) {
            view.setText(textId)
        }
    }

    protected fun setTextColor(viewId: Int, @ColorInt colorId: Int) {
        val view = findView<View>(viewId)
        if (view is TextView) {
            view.setTextColor(colorId)
        }
    }

    protected fun setOnClickListener(viewId: Int, clickListener:View.OnClickListener) {
        findView<View>(viewId)?.setOnClickListener(clickListener)
    }

    protected fun setBackgroundResource(viewId: Int, @DrawableRes resId: Int) {
        findView<View>(viewId)?.setBackgroundResource(resId)
    }

    protected fun setBackgroundColor(viewId: Int, @ColorInt colorId: Int) {
        findView<View>(viewId)?.setBackgroundColor(colorId)
    }

    protected fun setImageResource(@IdRes viewId: Int, @DrawableRes resId: Int) {
        val view = findView<View>(viewId)
        if (view is ImageView) {
            view.setImageResource(resId)
        }
    }
}