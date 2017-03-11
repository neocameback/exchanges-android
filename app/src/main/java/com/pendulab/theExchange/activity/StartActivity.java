package com.pendulab.theExchange.activity;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.SharedPreferencesManager;
import com.pendulab.theExchange.fragment.ImagesFragment;
import com.pendulab.theExchange.utils.GPSTracker;
import com.pendulab.theExchange.widget.CirclePageIndicator;
import com.splunk.mint.Mint;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Anh Ha Nguyen on 9/23/2015.
 */
public class StartActivity extends BaseActivity implements View.OnClickListener {

  private ViewPager pager;
  private CirclePageIndicator indicator;
  private TextView tvLogin, tvSignup;
  private GPSTracker gpsTracker;

  private final int[] BG_RESOURCES = {R.drawable.bg_welcome_1,
      R.drawable.bg_welcome_2, R.drawable.bg_welcome_3,
      R.drawable.bg_welcome_4, R.drawable.bg_welcome_5};

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_start);

    Mint.initAndStartSession(self, "13a56b24");

    initUI();
    initData();
    initControl();

    if (isLoggedIn()) {
      gotoActivity(self, HomeActivity.class);
      finish();
    }
  }

  private void initUI() {
    pager = (ViewPager) findViewById(R.id.pager);
    indicator = (CirclePageIndicator) findViewById(R.id.indicator);
    tvLogin = (TextView) findViewById(R.id.tvLogin);
    tvSignup = (TextView) findViewById(R.id.tvSignup);
  }

  private void initData() {
    FragmentPagerAdapter pagerAdapter = new ImageFragmentAdapter(
        getSupportFragmentManager());
    pager.setAdapter(pagerAdapter);
    indicator.setViewPager(pager);

    gpsTracker = new GPSTracker(self);
    String countryCode = getUserCountry(self);
//        Log.i("COUNTRY_CODE", countryCode.toString());
    if (countryCode != null) {
      preferences.putStringValue(SharedPreferencesManager.MY_COUNTRY_CODE, countryCode);
    }
  }

  private void initControl() {
    tvLogin.setOnClickListener(this);
    tvSignup.setOnClickListener(this);
  }


  public String getUserCountry(Context context) {
    try {
      final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      final String simCountry = tm.getSimCountryIso();
      if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
        return simCountry.toLowerCase(Locale.US);
      } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
        String networkCountry = tm.getNetworkCountryIso();
        if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
          return networkCountry.toLowerCase(Locale.US);
        }
      }
    } catch (Exception e) {
    }
    return null;
  }

  @Override
  public void onClick(View view) {
    if (view == tvLogin) {
      gotoActivity(self, LoginActivity.class);
      return;
    }

    if (view == tvSignup) {
      gotoActivity(self, RegisterActivity.class);
      return;
    }
  }

  class ImageFragmentAdapter extends FragmentPagerAdapter {

    public ImageFragmentAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {

      return ImagesFragment.newInstance(self, BG_RESOURCES[position]);

    }

    @Override
    public CharSequence getPageTitle(int position) {
      return "";
    }

    @Override
    public int getCount() {
      return (BG_RESOURCES.length);
    }
  }

}
