package com.pendulab.theExchange.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by Anh Ha Nguyen on 6/19/2015.
 */
public class DefaultTextSpinner extends Spinner {

  private Object firstItem;
  private boolean firstTimeSelected = false;

  public DefaultTextSpinner(Context context) {
    super(context);
  }

  public DefaultTextSpinner(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DefaultTextSpinner(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public Object getFirstItem() {
    return firstItem;
  }

  public void setFirstItem(Object firstItem) {
    this.firstItem = firstItem;
  }

  public boolean isFirstTimeSelected() {
    return firstTimeSelected;
  }

  public void setFirstTimeSelected(boolean firstTimeSelected) {
    this.firstTimeSelected = firstTimeSelected;
  }
}
