package com.rentalphang.runj.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *
 * 自定义不可滑动的viewpager
 *
 *
 */
public class NoSrollViewPager extends ViewPager {

    private boolean isScrollable=false; //滑动标示，true==滑动，false==不滑动，默认false

    public NoSrollViewPager(Context context) {
        super(context);
    }

    public NoSrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isScrollable==false) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isScrollable==false) {

            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public boolean isScrollable() {
        return isScrollable;
    }

    public void setIsScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }
}
