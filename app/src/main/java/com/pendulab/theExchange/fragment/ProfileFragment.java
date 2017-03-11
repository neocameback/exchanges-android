package com.pendulab.theExchange.fragment;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.activity.AllOfferActivity;
import com.pendulab.theExchange.activity.EditProfileActivity;
import com.pendulab.theExchange.activity.GetCreditActivity;
import com.pendulab.theExchange.activity.InboxActivity;
import com.pendulab.theExchange.activity.InventoryActivity;
import com.pendulab.theExchange.activity.SettingsActivity;
import com.pendulab.theExchange.activity.TransactionHistoryActivity;
import com.pendulab.theExchange.activity.WishlistActivity;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.base.BaseFragment;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.ParameterFactory;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.flashbulb.coloredratingbar.ColoredRatingBar;

/**
 * Created by Anh Ha Nguyen on 10/2/2015.
 */
public class ProfileFragment extends BaseFragment implements View.OnClickListener {

  public static ProfileFragment instance;

  public static ProfileFragment getInstance() {
    if (instance == null) {
      instance = new ProfileFragment();
    }

    return instance;
  }

  private SwipeRefreshLayout swipeRefreshLayout;
  private ImageView ivUser, ivRanking, ivSetting, ivRateCount;
  private TextView tvUsername, tvAddress, tvNotiInventory, tvNotiInbox, tvNotiWishlist, tvNoRating, tvRateCount, tvTotalCredit, tvGetMoreCredit;
  private ColoredRatingBar rbRating;
  private RelativeLayout rlInventory, rlInbox, rlEditProfile, rlWishlist, rlAllOffers, rlHistory;


  @Override
  public int getFragmentResource() {
    return R.layout.fragment_profile;
  }

  @Override
  public void initView(View view) {
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setColorSchemeResources(R.color.app_primary_color);
    ivUser = (ImageView) view.findViewById(R.id.ivUser);
    ivRanking = (ImageView) view.findViewById(R.id.ivRanking);
    ivSetting = (ImageView) view.findViewById(R.id.ivSetting);
    ivRateCount = (ImageView) view.findViewById(R.id.ivPerson);

    tvUsername = (TextView) view.findViewById(R.id.tvUsername);
    tvAddress = (TextView) view.findViewById(R.id.tvAddress);
    tvRateCount = (TextView) view.findViewById(R.id.tvRateCount);
    tvGetMoreCredit = (TextView) view.findViewById(R.id.tvGetMoreCredits);

    rbRating = (ColoredRatingBar) view.findViewById(R.id.rbRating);
    rbRating.setIndicator(true);

    tvNotiInventory = (TextView) view.findViewById(R.id.tvNotiInventory);
    tvNotiInbox = (TextView) view.findViewById(R.id.tvNotiInbox);
    tvNotiWishlist = (TextView) view.findViewById(R.id.tvNotiWishlist);
    tvNoRating = (TextView) view.findViewById(R.id.tvNoRating);
    tvTotalCredit = (TextView) view.findViewById(R.id.tvTotalCredit);

    rlInventory = (RelativeLayout) view.findViewById(R.id.rlInventory);
    rlInbox = (RelativeLayout) view.findViewById(R.id.rlInbox);
    rlEditProfile = (RelativeLayout) view.findViewById(R.id.rlProfile);
    rlWishlist = (RelativeLayout) view.findViewById(R.id.rlWishlist);
    rlHistory = (RelativeLayout) view.findViewById(R.id.rlHistory);
    rlAllOffers = (RelativeLayout) view.findViewById(R.id.rlAllOffers);

    Glide.with(self).load(R.drawable.ic_settings_pressed).into(ivSetting);

    Glide.with(self).load(R.drawable.ic_inventory_dark).into((ImageView) view.findViewById(R.id.ivInventory));
    Glide.with(self).load(R.drawable.ic_action_inbox_dark).into((ImageView) view.findViewById(R.id.ivInbox));
    Glide.with(self).load(R.drawable.ic_action_edit_dark).into((ImageView) view.findViewById(R.id.ivEditProfile));
    Glide.with(self).load(R.drawable.ic_like).into((ImageView) view.findViewById(R.id.ivWishList));
    Glide.with(self).load(R.drawable.ic_offers_dark).into((ImageView) view.findViewById(R.id.ivAllOffers));
    Glide.with(self).load(R.drawable.ic_history_dark).into((ImageView) view.findViewById(R.id.ivHistory));
  }

  @Override
  public void initData(View view) {
    if (getHomeActivity().myAccount != null) {
      Glide.with(self).load(getHomeActivity().myAccount.getImage()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(ivUser);
      tvUsername.setText(getHomeActivity().myAccount.getUsername());
      tvAddress.setText(getHomeActivity().myAccount.getCity().equalsIgnoreCase("") && getHomeActivity().myAccount.getCountry().equalsIgnoreCase("") ? getString(R.string.unknown_place) : getHomeActivity().myAccount.getCity());
    }
  }

  @Override
  public void initControl(View view) {
    ivSetting.setOnClickListener(this);
    rlInventory.setOnClickListener(this);
    rlInbox.setOnClickListener(this);
    rlEditProfile.setOnClickListener(this);
    rlHistory.setOnClickListener(this);
    rlWishlist.setOnClickListener(this);
    rlAllOffers.setOnClickListener(this);
    tvGetMoreCredit.setOnClickListener(this);

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getUserInfo();
      }
    });
  }

  @Override
  public void onClick(View view) {
    if (view == ivSetting) {
//            getHomeActivity().showLogoutDialog();
      gotoActivity(self, SettingsActivity.class);
      return;
    }
    if (view == rlInbox) {
      gotoActivity(self, InboxActivity.class);
      return;
    }
    if (view == rlInventory) {
      Bundle bundle = new Bundle();
      bundle.putString(GlobalValue.KEY_USER, getHomeActivity().myAccount.getUserID());
      bundle.putString(GlobalValue.KEY_USERNAME, getHomeActivity().myAccount.getUsername());
      gotoActivity(self, InventoryActivity.class, bundle);
      return;
    }
    if (view == rlEditProfile) {
      gotoActivity(self, EditProfileActivity.class);
      return;
    }
    if (view == rlAllOffers) {
      gotoActivity(self, AllOfferActivity.class);
      return;
    }
    if (view == rlWishlist) {
      Bundle bundle = new Bundle();
      bundle.putString(GlobalValue.KEY_USER, getHomeActivity().myAccount.getUserID());
      bundle.putString(GlobalValue.KEY_USERNAME, getHomeActivity().myAccount.getUsername());
      gotoActivity(self, WishlistActivity.class, bundle);
      return;
    }
    if (view == rlHistory) {
      gotoActivity(self, TransactionHistoryActivity.class);
      return;
    }
    if (view == tvGetMoreCredit) {
      gotoActivity(self, GetCreditActivity.class);
      return;
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (getHomeActivity().myAccount.getInventoryCount() == 0 && getHomeActivity().myAccount.getInboxCount() == 0 && getHomeActivity().myAccount.getWishlistCount() == 0) {
      getUserInfo();
    } else {
      fillLayout(getHomeActivity().myAccount);
    }
  }

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    Log.i("ON_HIDDEN", hidden + "");
  }

  private void getUserInfo() {
    String getUrl = ParameterFactory.createGetUserInfoParam(getHomeActivity().myAccount.getUserID());
    AsyncHttpGet asyncHttpGet = new AsyncHttpGet(self, getHomeActivity().myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {
        swipeRefreshLayout.setRefreshing(false);
        getHomeActivity().parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            Account acc = CommonParser.parseUserFromJson(json, getHomeActivity().myAccount.getToken());
            if (acc != null) {
              getHomeActivity().setMyAccount(acc);
              fillLayout(acc);
            }

          }

          @Override
          public void onRequestError(int message) {
            getHomeActivity()._onRequestError(message);
          }
        });

      }
    }, null);
    asyncHttpGet.execute(getUrl);
  }

  private void fillLayout(Account acc) {
    tvNotiInventory.setText(acc.getInventoryCount() + "");

    tvNotiInbox.setText(acc.getInboxCount() + "");

    tvNotiWishlist.setText(acc.getWishlistCount() + "");

    rbRating.setRating(acc.getRate());
    getHomeActivity().getParentView(rbRating).setVisibility(acc.getRate() > 0 ? View.VISIBLE : View.GONE);
    tvNoRating.setVisibility(acc.getRate() > 0 ? View.GONE : View.VISIBLE);
    Glide.with(self).load(R.drawable.icon_person).into(ivRateCount);
    tvRateCount.setText(getHomeActivity().myAccount.getNumberRate() + "");
    tvTotalCredit.setText(getHomeActivity().myAccount.getTradeCredit() + "");

  }

}
