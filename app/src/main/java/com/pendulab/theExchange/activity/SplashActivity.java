package com.pendulab.theExchange.activity;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

/**
 * Created by Anh Ha Nguyen on 5/26/2015.
 */
public class SplashActivity extends BaseActivity {

  private Thread splashTread;
  private int _splashTime = 1000;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_splash);


    showSplashScreen();

  }

  private void showSplashScreen() {

    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {

        if (isLoggedIn()) {
          gotoActivity(self, HomeActivity.class);
        } else {
          gotoActivity(self, LoginActivity.class);
        }

        finish();
      }
    }, _splashTime);

  }

}
