package com.zhpan.bannerview.provider

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * source from:https://github.com/youth5201314/banner/blob/master/banner/src/main/java/com/youth/banner/util/ScrollSpeedManger.java
 * thanks:https://github.com/zguop/banner/blob/master/pager2banner/src/main/java/com/to/aboomy/pager2banner/Banner.java
 */
class ScrollDurationManger(viewPager2: ViewPager2, private val scrollDuration: Int, recyclerView: RecyclerView?, linearLayoutManager: LinearLayoutManager?) : LinearLayoutManager(viewPager2.context, linearLayoutManager!!.orientation, false) {
    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateTimeForDeceleration(dx: Int): Int {
                return scrollDuration
            }
        }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    companion object {
        @JvmStatic
        fun reflectLayoutManager(viewPager2: ViewPager2, scrollDuration: Int) {
            try {
                val recyclerView = viewPager2.getChildAt(0) as RecyclerView
                recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val speedManger = ScrollDurationManger(viewPager2, scrollDuration, recyclerView, linearLayoutManager)
                recyclerView.layoutManager = speedManger
                val mRecyclerView = RecyclerView.LayoutManager::class.java.getDeclaredField("mRecyclerView")
                mRecyclerView.isAccessible = true
                mRecyclerView[linearLayoutManager] = recyclerView
                val layoutMangerField = ViewPager2::class.java.getDeclaredField("mLayoutManager")
                layoutMangerField.isAccessible = true
                layoutMangerField[viewPager2] = speedManger
                val pageTransformerAdapterField = ViewPager2::class.java.getDeclaredField("mPageTransformerAdapter")
                pageTransformerAdapterField.isAccessible = true
                val mPageTransformerAdapter = pageTransformerAdapterField[viewPager2]
                if (mPageTransformerAdapter != null) {
                    val aClass: Class<*> = mPageTransformerAdapter.javaClass
                    val layoutManager = aClass.getDeclaredField("mLayoutManager")
                    layoutManager.isAccessible = true
                    layoutManager[mPageTransformerAdapter] = speedManger
                }
                val scrollEventAdapterField = ViewPager2::class.java.getDeclaredField("mScrollEventAdapter")
                scrollEventAdapterField.isAccessible = true
                val mScrollEventAdapter = scrollEventAdapterField[viewPager2]
                if (mScrollEventAdapter != null) {
                    val aClass: Class<*> = mScrollEventAdapter.javaClass
                    val layoutManager = aClass.getDeclaredField("mLayoutManager")
                    layoutManager.isAccessible = true
                    layoutManager[mScrollEventAdapter] = speedManger
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

}