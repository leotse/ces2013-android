package com.digiflare.ces2013.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class UninterceptableViewPager extends ViewPager {

    public UninterceptableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	getParent().requestDisallowInterceptTouchEvent(true);
    	return true;
    }
}
