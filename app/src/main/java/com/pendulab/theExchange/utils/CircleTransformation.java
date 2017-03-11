package com.pendulab.theExchange.utils;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Anh Ha Nguyen on 7/2/2015.
 */

public class CircleTransformation extends BitmapTransformation {
  public CircleTransformation(Context context) {
    super(context);
  }

  @Override
  protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
    return circleCrop(pool, toTransform);
  }

  private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
    if (source == null) return null;

    int size = Math.min(source.getWidth(), source.getHeight());
    int x = (source.getWidth() - size) / 2;
    int y = (source.getHeight() - size) / 2;

    // TODO this could be acquired from the pool too
    Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

    Bitmap result = pool.get(size, size, Bitmap.Config.RGB_565);
    if (result == null) {
      result = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
    }

    Canvas canvas = new Canvas(result);
    Paint paint = new Paint();
    paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
    paint.setAntiAlias(true);
    float r = size / 2f;
    canvas.drawCircle(r, r, r, paint);
    return result;
  }

  @Override
  public String getId() {
    return getClass().getName();
  }
}
