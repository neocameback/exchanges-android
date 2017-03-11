package com.pendulab.theExchange.activity;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.utils.DialogUtility;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Anh Ha Nguyen on 12/11/2015.
 */
public class SettingsActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_settings);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.settings));

    initUI();
    initData();
    initControl();
  }

  private void initUI() {

  }

  private void initData() {

  }

  private void initControl() {

  }

  public void onLanguage(View v) {
    DialogUtility.showShortToast(self, "Coming soon");
  }

  public void onChangePassword(View v) {
    gotoActivity(self, ChangePasswordActivity.class);
  }

  public void onLogout(View v) {
    showLogoutDialog();
  }

  public void onGettingStarted(View v) {
//        preferences.putBooleanValue(SharedPreferencesManager.AFTER_FIRST_TIME, false);
    Bundle bundle = new Bundle();
    bundle.putString(GlobalValue.KEY_VIEW_TUTORIAL, GlobalValue.KEY_VIEW_TUTORIAL);
    gotoActivity(self, HomeActivity.class, bundle, Intent.FLAG_ACTIVITY_CLEAR_TOP);
  }

  public void onTradeCreditRanking(View v) {
    DialogUtility.showShortToast(self, "Coming soon");
  }

  public void onHelp(View v) {
    DialogUtility.showShortToast(self, "Coming soon");
  }

  public void onEmailSupport(View v) {
    DialogUtility.showShortToast(self, "Coming soon");
  }

  public void onCommunityRule(View v) {
    DialogUtility.showShortToast(self, "Coming soon");
  }

  public void onAbout(View v) {
    DialogUtility.showShortToast(self, "Coming soon");
  }
}
