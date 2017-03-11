package com.pendulab.theExchange.activity;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseShareActivity;
import com.pendulab.theExchange.config.GlobalValue;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Anh Ha Nguyen on 12/10/2015.
 */
public class FindAndInviteActivity extends BaseShareActivity implements View.OnClickListener {

  private ImageView ivSearch;
  private EditText etSearch;
  private LinearLayout llFacebook, llGoogle, llEmail;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_find_invite);

    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.find_and_invite_friends));

    initUI();
    initData();
    initControl();
  }

  private void initUI() {
    ivSearch = (ImageView) findViewById(R.id.ivSearch);
    etSearch = (EditText) findViewById(R.id.etSearch);

    llFacebook = (LinearLayout) findViewById(R.id.llFacebook);
    llGoogle = (LinearLayout) findViewById(R.id.llGoogle);
    llEmail = (LinearLayout) findViewById(R.id.llEmail);

    Glide.with(self).load(R.drawable.icon_facebook_blue).into((ImageView) findViewById(R.id.ivFacebook));
    Glide.with(self).load(R.drawable.icon_google_red).into((ImageView) findViewById(R.id.ivGoogle));
    Glide.with(self).load(R.drawable.icon_email_invite).into((ImageView) findViewById(R.id.ivEmail));
  }

  private void initData() {

  }

  private void initControl() {
    ivSearch.setOnClickListener(this);
    llFacebook.setOnClickListener(this);
    llGoogle.setOnClickListener(this);
    llEmail.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    if (view == ivSearch) {
      if (etSearch.getText().toString().trim().length() > 0) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalValue.KEYWORD_SEARCH, etSearch.getText().toString().trim());
        gotoActivity(self, FindUserActivity.class, bundle);
      }
    }

    if (view == llFacebook) {
      onInviteFacebook(view);
      return;
    }
    if (view == llGoogle) {
      onInviteGoogle(view);
      return;
    }
    if (view == llEmail) {
      onInviteEmail(view);
      return;
    }
  }
}
