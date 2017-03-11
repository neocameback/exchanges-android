package com.pendulab.theExchange.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Ha Anh on 4/29/2016.
 */
public class CustomViewPager extends ViewPager {

  private boolean enabled;

  public CustomViewPager(Context context) {
    super(context);
    this.enabled = true;
  }

  public CustomViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.enabled = true;
  }

//    public CustomViewPager(Context context, AttributeSet attrs, int defStyle){
//        super(context, attrs, defStyle);
//        this.enabled = true;
//
//    }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (this.enabled) {
      return super.onTouchEvent(event);
    }

    return false;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    if (this.enabled) {
      return super.onInterceptTouchEvent(event);
    }

    return false;
  }

  public void setPagingEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
