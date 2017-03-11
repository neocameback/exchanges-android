package com.pendulab.theExchange.fontWidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ProximaNovaBoldTextView extends TextView {

  public ProximaNovaBoldTextView(Context context) {
    super(context);
    setFont(context);

  }

  public ProximaNovaBoldTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setFont(context);
  }

  public ProximaNovaBoldTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setFont(context);
  }

  private void setFont(Context context) {
    Typeface face = Typeface.createFromAsset(context.getAssets(),
        "fonts/verdanab.ttf");
    this.setTypeface(face);
  }

  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

  }

}

