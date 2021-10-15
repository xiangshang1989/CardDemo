package com.sun.carddemo.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sun.carddemo.util.dp
import com.sun.carddemo.util.orDef

/**
 * Author: sunhaitao
 * Date: 2021/10/14 19:32
 * Description:
 */
class GalleryItemDecoration:RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount.orDef()
        //是不是最后一条数据
        val isLastPosition = position == itemCount - 1
        //设置每个item的高度
        val itemHeight: Int = parent.height - 97.dp
        val lp = view.layoutParams as RecyclerView.LayoutParams
        lp.height = itemHeight

        var bottomMargin = 15.dp
        if (isLastPosition) {
            bottomMargin = 97.dp
        }
        lp.setMargins(15.dp, lp.topMargin, 15.dp, bottomMargin)
        view.layoutParams = lp
        super.getItemOffsets(outRect, view, parent, state)
    }
}