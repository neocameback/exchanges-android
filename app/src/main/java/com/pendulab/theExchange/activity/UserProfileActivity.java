package com.pendulab.theExchange.activity;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import in.flashbulb.coloredratingbar.ColoredRatingBar;

/**
 * Created by Anh Ha Nguyen on 10/28/2015.
 */
public class UserProfileActivity extends BaseActivity implements View.OnClickListener {

  private SwipeRefreshLayout swipeRefreshLayout;
  private ImageView ivUser, ivRanking, ivRateCount;
  private TextView tvUsername, tvAddress, tvNotiInventory, tvNotiWishlist, tvBio, tvNoRating, tvRateSeller, tvRateCount;
  private ColoredRatingBar rbRating;
  private RelativeLayout rlInventory, rlWishlist;

  private Account currentUser;
  private String tempUsername, tempUserID;

  private final int RC_RATE_SELLER = 1001;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_profile);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    getSupportActionBar().setTitle("");

    Bundle bundle = getIntent().getExtras();
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_USER)) {
      tempUserID = bundle.getString(GlobalValue.KEY_USER);
      if (bundle.containsKey(GlobalValue.KEY_USERNAME)) {
        tempUsername = bundle.getString(GlobalValue.KEY_USERNAME);
      }
    }

    if (tempUsername != null) {
      setActionBarTitle("@" + tempUsername);
    }

    initUI();
    initData();
    initControl();
  }

  private void initUI() {
    swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setColorSchemeResources(R.color.app_primary_color);
    ivUser = (ImageView) findViewById(R.id.ivUser);
    ivRanking = (ImageView) findViewById(R.id.ivRanking);
    ivRateCount = (ImageView) findViewById(R.id.ivPerson);

    tvUsername = (TextView) findViewById(R.id.tvUsername);
    tvAddress = (TextView) findViewById(R.id.tvAddress);
    tvBio = (TextView) findViewById(R.id.tvBio);
    tvNoRating = (TextView) findViewById(R.id.tvNoRating);
    tvRateSeller = (TextView) findViewById(R.id.tvRateSeller);
    tvRateCount = (TextView) findViewById(R.id.tvRateCount);

    rbRating = (ColoredRatingBar) findViewById(R.id.rbRating);
    rbRating.setIndicator(true);

    tvNotiInventory = (TextView) findViewById(R.id.tvNotiInventory);
    tvNotiInventory.setVisibility(View.GONE);
    tvNotiWishlist = (TextView) findViewById(R.id.tvNotiWishlist);
    tvNotiWishlist.setVisibility(View.GONE);

    rlInventory = (RelativeLayout) findViewById(R.id.rlInventory);
    rlWishlist = (RelativeLayout) findViewById(R.id.rlWishlist);

    Glide.with(self).load(R.drawable.ic_inventory_dark).into((ImageView) findViewById(R.id.ivInventory));
    Glide.with(self).load(R.drawable.ic_like).into((ImageView) findViewById(R.id.ivWishList));
  }

  private void initData() {
    getUserInfo();
  }

  private void initControl() {
    rlInventory.setOnClickListener(this);
    rlWishlist.setOnClickListener(this);

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getUserInfo();
      }
    });
    tvRateSeller.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    if (view == rlInventory) {
      Bundle bundle = new Bundle();
      bundle.putString(GlobalValue.KEY_USER, tempUserID);
      bundle.putString(GlobalValue.KEY_USERNAME, tempUsername);
      gotoActivity(self, InventoryActivity.class, bundle);
      return;
    }
    if (view == rlWishlist) {
      Bundle bundle = new Bundle();
      bundle.putString(GlobalValue.KEY_USER, tempUserID);
      bundle.putString(GlobalValue.KEY_USERNAME, tempUsername);
      gotoActivity(self, WishlistActivity.class, bundle);
      return;
    }
    if (view == tvRateSeller) {
      checkRate();
      return;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      if (requestCode == RC_RATE_SELLER) {
        Bundle bundle = data.getExtras();
        if (bundle != null && bundle.containsKey(GlobalValue.KEY_RATE)) {
          getUserInfo();
        }
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_user_profile, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (currentUser != null) {
      if (currentUser.isBlocked()) {
        menu.getItem(0).getSubMenu().getItem(1).setVisible(false);
        menu.getItem(0).getSubMenu().getItem(2).setVisible(true);
      } else {
        menu.getItem(0).getSubMenu().getItem(1).setVisible(true);
        menu.getItem(0).getSubMenu().getItem(2).setVisible(false);
      }
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_report) {
      reportUser();
    } else if (item.getItemId() == R.id.action_block) {
      toogleBlockUser();
    } else if (item.getItemId() == R.id.action_unblock) {
      toogleBlockUser();
    }
    return super.onOptionsItemSelected(item);
  }

  private void getUserInfo() {
    String getUrl = ParameterFactory.createGetUserInfoParam(tempUserID);
    AsyncHttpGet asyncHttpGet = new AsyncHttpGet(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {
        swipeRefreshLayout.setRefreshing(false);
        parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            currentUser = CommonParser.parseUserFromJson(json, "");
            if (currentUser != null) {
              fillLayout(currentUser);
            }

          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });

      }
    }, null);
    asyncHttpGet.execute(getUrl);
  }

  private void fillLayout(final Account acc) {

    Glide.with(self).load(currentUser.getImage()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(ivUser);
    tvUsername.setText(currentUser.getFirstname().equalsIgnoreCase("") && currentUser.getLastname().equalsIgnoreCase("") ? currentUser.getUsername() : currentUser.getFirstname() + " " + currentUser.getLastname());
    getSupportActionBar().setTitle("@" + currentUser.getUsername());
    tvAddress.setText(currentUser.getCity().equalsIgnoreCase("") && currentUser.getCountry().equalsIgnoreCase("") ? getString(R.string.unknown_place) : currentUser.getCity());

    tvNotiWishlist.setText(acc.getWishlistCount() + "");
    tvNotiInventory.setText(acc.getInventoryCount() + "");
    tvBio.setText(acc.getBio().equalsIgnoreCase("") ? getString(R.string.nothing_to_show) : acc.getBio());

    rbRating.setRating(acc.getRate());
    getParentView(rbRating).setVisibility(acc.getRate() > 0 ? View.VISIBLE : View.GONE);
    tvNoRating.setVisibility(acc.getRate() > 0 ? View.GONE : View.VISIBLE);
    Glide.with(self).load(R.drawable.icon_person).into(ivRateCount);
    tvRateCount.setText(currentUser.getNumberRate() + "");

    invalidateOptionsMenu();
  }

  private void reportUser() {
    List<NameValuePair> params = ParameterFactory.createBlockReportUserParam(currentUser.getUserID());
    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
        DialogUtility.closeProgressDialog();
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            DialogUtility.showMessageDialog(self, getString(R.string.message_report_user_successfully), null);
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_REPORT_USER);
  }

  private void toogleBlockUser() {
    List<NameValuePair> params = ParameterFactory.createBlockReportUserParam(currentUser.getUserID());
    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
        DialogUtility.closeProgressDialog();
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            if (!currentUser.isBlocked()) {
              currentUser.setBlocked(true);
              DialogUtility.showMessageDialog(self, getString(R.string.message_block_user_successfully), null);
            } else {
              currentUser.setBlocked(false);
              DialogUtility.showMessageDialog(self, getString(R.string.message_unblock_user_successfully), null);
            }
            invalidateOptionsMenu();

          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_BLOCK_USER);
  }

  private void checkRate() {
    List<NameValuePair> params = ParameterFactory.createGetRateParam(currentUser.getUserID());
    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
        DialogUtility.closeProgressDialog();
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            if (CommonParser.isUserRatable(json)) {
              Object[] obj = CommonParser.getExistedRate(json);
              if (obj != null) {
                Float rate = (Float) obj[0];
                String review = (String) obj[1];
                Bundle bundle = new Bundle();
                bundle.putParcelable(GlobalValue.KEY_USER, currentUser);
                bundle.putFloat(GlobalValue.KEY_RATE, rate);
                bundle.putString(GlobalValue.KEY_REVIEW, review);
                gotoActivityForResult(self, RateSellerActivity.class, bundle, RC_RATE_SELLER);
              }
            } else {
              DialogUtility.showMessageDialog(self, getString(R.string.rate_seller_alert), null);
            }
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_GET_RATE);
  }
}
