package com.github.waterpeak.sorry

import android.content.Context
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import android.view.GestureDetector



class RecyclerViewOnItemClickListener(recyclerView: RecyclerView,
                                      onItemClickListener: (index: Int)->Unit):
        RecyclerView.OnItemTouchListener{

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(recyclerView.context, object: GestureDetector.SimpleOnGestureListener(){
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                if(e==null){
                    return false
                }
                val childView = recyclerView.findChildViewUnder(e.x, e.y);
                if(childView != null){
                    onItemClickListener(recyclerView.getChildLayoutPosition(childView));
                    return true;
                }
                return false
            }
        })
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(e)
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }

}