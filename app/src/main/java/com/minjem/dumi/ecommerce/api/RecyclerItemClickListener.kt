package com.mdi.stockin.ApiHelper

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class RecyclerItemClickListener (mContext :Context, private val mListener : OnItemClickListener?) : androidx.recyclerview.widget.RecyclerView.OnItemTouchListener {
    internal var mGestureDetector: GestureDetector
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
    init {
        mGestureDetector = GestureDetector(mContext, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
        })
    }
    override fun onTouchEvent(p0: androidx.recyclerview.widget.RecyclerView, p1: MotionEvent) {
    }
    override fun onInterceptTouchEvent(p0: androidx.recyclerview.widget.RecyclerView, p1: MotionEvent): Boolean {
        val childView = p0!!.findChildViewUnder(p1!!.x,p1.y)
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(p1)){
            mListener.onItemClick(childView,p0.getChildAdapterPosition(childView))
        }
        return false
    }
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}