package com.pendulab.theExchange.fontWidget;

import com.pendulab.theExchange.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationTextView extends TextView {

  private Context context;

  public NotificationTextView(Context context) {
    super(context);
    setFont(context);

  }

  public NotificationTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setFont(context);
  }

  public NotificationTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setFont(context);
  }

  private void setFont(Context context) {
//        Typeface face = Typeface.createFromAsset(context.getAssets(),
//                "fonts/ProximaNova-Regular.otf");
//        this.setTypeface(face);
    this.context = context;
    addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {

        if (getText().toString().compareTo("0") >= 1) {
          setVisibility(View.VISIBLE);
        } else {
          setVisibility(View.GONE);
        }

        getViewTreeObserver()
            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
              // Wait until layout to call Picasso
              @Override
              public void onGlobalLayout() {
                // Ensure we call this only once
                getViewTreeObserver()
                    .removeGlobalOnLayoutListener(this);
                configureLayout(getText().toString());
              }
            });
      }
    });
  }


  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

  }

  @Override
  public void setText(final CharSequence text, BufferType type) {
    super.setText(text, type);
//        getViewTreeObserver()
//                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    // Wait until layout to call Picasso
//                    @Override
//                    public void onGlobalLayout() {
//                        // Ensure we call this only once
//                        getViewTreeObserver()
//                                .removeGlobalOnLayoutListener(this);
//                        configureLayout();
//
//                    }
//                });
//        this.post(new Runnable() {
//            @Override
//            public void run() {
//                configureLayout(text.toString());
//            }
//        });
  }

  private void configureLayout(final String text) {
    Rect bounds = new Rect();
    Paint textPaint = getPaint();
    textPaint.getTextBounds(text, 0, text.length(), bounds);
    Log.i(this.getClass().getSimpleName(), bounds.width() + "-" + bounds.height());
    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.getLayoutParams();
    lp.height = bounds.height() + context.getResources().getDimensionPixelOffset(R.dimen.margin_padding_small);
    lp.width = bounds.width() + context.getResources().getDimensionPixelOffset(R.dimen.margin_padding_small);
    if (lp.height > lp.width) {
      lp.width = lp.height;
    } else {
      lp.height = lp.width;
    }
    this.setLayoutParams(lp);

    setGravity(Gravity.CENTER);
    invalidate();

  }


}

