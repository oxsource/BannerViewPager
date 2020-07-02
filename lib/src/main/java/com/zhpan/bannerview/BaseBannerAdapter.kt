package com.zhpan.bannerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zhpan.bannerview.BannerViewPager.OnPageClickListener

abstract class BaseBannerAdapter<T, VH : BaseViewHolder<T>?> : RecyclerView.Adapter<VH>() {
    companion object {
        const val MAX_VALUE = 500
    }

    private var mList: MutableList<T> = ArrayList()
    private var isCanLoop = false
    private var mPageClickListener: OnPageClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflate: View = LayoutInflater.from(parent.context).inflate(getLayoutId(viewType), parent, false)
        return createViewHolder(inflate, viewType)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder?.itemView?.setOnClickListener {
            mPageClickListener?.onPageClick(position, getRealIndex(position))
        }
        val index: Int = getRealIndex(position)
        onBind(holder, mList[index], index, mList.size)
    }

    override fun getItemViewType(position: Int): Int = getViewType(getRealIndex(position))

    override fun getItemCount(): Int = if (isCanLoop && mList.size > 1) MAX_VALUE else mList.size

    fun getRealIndex(position: Int): Int {
        if (mList.isEmpty()) return 0
        val pageSize = mList.size
        return if (isCanLoop) (position - 1 + pageSize) % pageSize else (position + pageSize) % pageSize
    }


    fun setList(list: List<T>?) {
        list ?: return
        mList.clear()
        mList.addAll(list)
    }

    fun getList(): List<T> = mList

    fun setCanLoop(canLoop: Boolean) {
        isCanLoop = canLoop
    }

    fun setPageClickListener(pageClickListener: OnPageClickListener?) {
        mPageClickListener = pageClickListener
    }

    protected open fun getViewType(position: Int): Int = 0

    protected abstract fun onBind(holder: VH, data: T, position: Int, pageSize: Int)

    abstract fun createViewHolder(itemView: View, viewType: Int): VH

    abstract fun getLayoutId(viewType: Int): Int
}