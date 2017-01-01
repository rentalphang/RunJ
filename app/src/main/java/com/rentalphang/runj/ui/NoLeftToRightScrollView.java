package com.rentalphang.runj.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 *
 * 自定义Scrollview,解决Scrollview嵌套viewpager的滑动冲突问题
 *
 *
 */
public class NoLeftToRightScrollView extends ScrollView {


    private float xDistance,yDistance;

    private float xLast,yLast;


    public NoLeftToRightScrollView(Context context) {
        super(context);
    }

    public NoLeftToRightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoLeftToRightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //事件响应
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    //事件拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN: //按下

                xDistance = 0f;
                yDistance = 0f;

                xLast = ev.getX();
                yLast = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE: //滑动
                float xNow = ev.getX();
                float yNow = ev.getY();

                xDistance += Math.abs(xNow - xLast);
                yDistance += Math.abs(yNow - yLast);

                xLast = xNow;
                yLast = yNow;

                if (xDistance > yDistance) {

                    return false;
                }


                break;

            case MotionEvent.ACTION_UP:  //抬起

                break;
        }


        return super.onInterceptTouchEvent(ev);
    }

    //事件分发
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }


}
