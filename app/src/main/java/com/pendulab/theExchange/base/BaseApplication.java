package com.pendulab.theExchange.base;

import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by Anh Ha Nguyen on 8/11/2015.
 */
public class BaseApplication extends com.activeandroid.app.Application {

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }
}
