package com.pendulab.theExchange.activity;

import com.astuetz.PagerSlidingTabStrip;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.fragment.ArchiveMessageFragment;
import com.pendulab.theExchange.fragment.HistoryBuyFragment;
import com.pendulab.theExchange.fragment.HistorySellFragment;
import com.pendulab.theExchange.widget.CustomViewPager;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Anh Ha Nguyen on 12/7/2015.
 */
public class TransactionHistoryActivity extends BaseActivity {

  private PagerSlidingTabStrip tabs;
  private CustomViewPager pager;
  public MyPagerAdapter adapter;
  private LinearLayout mTabsLinearLayout;

  private int currentPage;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_transaction_history);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.transaction_history));

    initUI();
    initData();
    initControl();
  }

  private void initUI() {
    tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
    pager = (CustomViewPager) findViewById(R.id.pager);
  }

  private void initData() {
    adapter = new MyPagerAdapter(getSupportFragmentManager());
    pager.setAdapter(adapter);
    pager.setOffscreenPageLimit(1);
    tabs.setViewPager(pager);

    tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        currentPage = position;
        for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
          TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);
          if (i == position) {
            tv.setTextColor(Color.WHITE);
          } else {
            tv.setTextColor(Color.GRAY);
          }

          if (position == 2) {
            pager.setPagingEnabled(false);
          } else {
            pager.setPagingEnabled(true);
          }
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    changeColor(getResources().getColor(R.color.app_primary_color));

    mTabsLinearLayout = ((LinearLayout) tabs.getChildAt(0));
    for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
      TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);

      if (i == 0) {
        tv.setTextColor(Color.WHITE);
      } else {
        tv.setTextColor(Color.GRAY);
      }
    }
  }

  private void initControl() {

  }

  private void changeColor(int newColor) {

    tabs.setIndicatorColor(getResources().getColor(R.color.white));
    tabs.setIndicatorHeight((int) getResources().getDimension(R.dimen.margin_padding_very_tiny));
    tabs.setTextColor(getResources().getColor(R.color.white));
    tabs.setTextSize((int) getResources().getDimension(R.dimen.text_size_xxsmall));
    tabs.setDividerColor(getResources().getColor(R.color.transparent));
//        tabs.setTabBackground(getResources().getColor(R.color.app_primary_color));
    tabs.setUnderlineColor(Color.TRANSPARENT);
    Typeface face = Typeface.createFromAsset(getAssets(),
        "fonts/verdana.ttf");
    tabs.setTypeface(face, 0);
  }

  public class MyPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {getString(R.string.bought).toUpperCase(), getString(R.string.sold_out).toUpperCase(), getString(R.string.archive)};

    public MyPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return TITLES[position];
    }

    @Override
    public int getCount() {
      return TITLES.length;
    }

    @Override
    public int getItemPosition(Object object) {
      return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return HistoryBuyFragment.getInstance();
        case 1:
          return HistorySellFragment.getInstance();
        case 2:
          return ArchiveMessageFragment.getInstance();
        default:
          return null;
      }
    }
  }
}
