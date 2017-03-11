package com.pendulab.theExchange.base;

import com.facebook.Session;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.activity.HomeActivity;
import com.pendulab.theExchange.activity.ItemDetailsActivity;
import com.pendulab.theExchange.activity.LoginActivity;
import com.pendulab.theExchange.activity.UserProfileActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.SharedPreferencesManager;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.net.NetBase;
import com.pendulab.theExchange.utils.AppUtil;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 5/26/2015.
 */
public class BaseActivity extends AppCompatActivity {

  public Activity self;
  public SharedPreferencesManager preferences;
  public Account myAccount;
  public String TAG;
  private TextView titleTextView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    self = this;
    preferences = SharedPreferencesManager.getInstance(self);
    myAccount = preferences.getUserInfo();
    TAG = this.getClass().getSimpleName();

  }

  @Override
  protected void onResume() {
    super.onResume();
    myAccount = preferences.getUserInfo();
    if (getSupportActionBar() != null) {
      getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_bar_back);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    adjustTitleParam();
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;


      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void adjustTitleParam() {
    if (titleTextView != null) {
      LinearLayout.LayoutParams titleParam = (LinearLayout.LayoutParams) titleTextView.getLayoutParams();
      titleParam.setMargins(0, 0, 0, 0);
      titleTextView.setLayoutParams(titleParam);
    }
  }


  @Override
  public void onBackPressed() {
    // TODO Auto-generated method stub
    super.onBackPressed();
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
  }

  /**
   * Go to other activity
   */
  public void gotoActivity(Context context, Class<?> cla) {
    Intent intent = new Intent(context, cla);
    startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_left, R.anim.push_left_out);
  }

  public void gotoActivity(Context context, Class<?> cla, int flag) {
    Intent intent = new Intent(context, cla);
    intent.setFlags(flag);
    startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_left, R.anim.push_left_out);
  }

  /**
   * goto view website
   */
  public void gotoWeb(Uri uri) {
    Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
    startActivity(myIntent);
  }

  /**
   * Go to other activity
   */
  public void gotoActivityForResult(Context context, Class<?> cla,
                                    int requestCode) {
    Intent intent = new Intent(context, cla);
    startActivityForResult(intent, requestCode);
//        overridePendingTransition(R.anim.slide_in_left, R.anim.push_left_out);
  }

  /**
   * Goto activity with bundle
   */
  public void gotoActivity(Context context, Class<?> cla, Bundle bundle) {
    Intent intent = new Intent(context, cla);
    intent.putExtras(bundle);
    startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_left, R.anim.push_left_out);

  }

  public void gotoActivity(Context context, Class<?> cla, Bundle bundle,
                           int flag) {
    Intent intent = new Intent(context, cla);
    intent.putExtras(bundle);
    intent.addFlags(flag);
    startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_left, R.anim.push_left_out);

  }

  public void reloadActivity(Activity activity, Bundle bundle) {
    Intent intent = getIntent();
    intent.putExtras(bundle);
    activity.finish();
    startActivity(intent);
  }

  /**
   * Goto activity with bundle
   */
  public void gotoActivityForResult(Context context, Class<?> cla,
                                    Bundle bundle, int requestCode) {
    Intent intent = new Intent(context, cla);
    intent.putExtras(bundle);
    startActivityForResult(intent, requestCode);
//        overridePendingTransition(R.anim.slide_in_left, R.anim.push_left_out);
  }

  /**
   * Goto web page
   */
  protected void gotoWebPage(String url) {
    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    startActivity(i);
  }

  /**
   * Goto phone call
   */
  protected void gotoPhoneCallPage(String telNumber) {
    Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telNumber));
    startActivity(i);
  }

  public void showSoftKeyboard(View v) {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
  }

  public void hideSoftKeyboard(View v) {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
  }

  private String getStringResourceByName(String identifier) {
    String packageName = getPackageName();
    int resId = getResources().getIdentifier(identifier, "string", packageName);
    return getString(resId);
  }


  public boolean isLoggedIn() {
    if (!preferences.getStringValue(SharedPreferencesManager.MY_ACCOUNT).equals("")) {
      return true;
    }
    return false;
  }

  public void showExitDialog() {
    DialogUtility.showOptionDialog(self, getString(R.string.text_confirm_exit), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
      @Override
      public void onPositive(Dialog dialog) {
        finish();
      }

      @Override
      public void onNegative(Dialog dialog) {

      }
    });
  }

  public void showLogoutDialog() {

    DialogUtility.showOptionDialog(self, getString(R.string.text_confirm_logout), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
      @Override
      public void onPositive(Dialog dialog) {
        onLogout();
      }

      @Override
      public void onNegative(Dialog dialog) {

      }
    });
  }

  public int checkRequestSuccess(String json) {
    try {
      JSONObject entryObj = new JSONObject(json);
      if (entryObj.getString(WebServiceConfig.KEY_STATUS).equalsIgnoreCase(WebServiceConfig.STATUS_SUCCESS)) {
        return 0;
      } else {
        if (CommonParser.isInteger(entryObj.getString(WebServiceConfig.KEY_MESSAGE))) {
          return entryObj.getInt(WebServiceConfig.KEY_MESSAGE);
        }
      }
    } catch (Exception e) {
      return -1;
    }
    return -1;
  }


  public void parseResponseStatus(int statusCode, String response, NetBaseListener listener) {

    switch (statusCode) {
      case NetBase.NETWORK_OFF:
        AppUtil.alertNetworkUnavailableCommon(self);
        break;
      case NetBase.OK:
        int check = checkRequestSuccess(response);
        if (check == 0) {
          listener.onRequestSuccess(response);
        } else {
          listener.onRequestError(check);
        }
        break;
      default:
        AppUtil.alert(self, getString(R.string.server_error));

    }
  }

  public void _onRequestError(int message) {
    if (message > 0) {
      try {
        String errorMsg = getStringResourceByName("error_" + message);
        if (errorMsg != null) {
          DialogUtility.showMessageDialog(self, errorMsg, null);
          return;
        }
      } catch (Exception e) {
        e.printStackTrace();
        DialogUtility.showMessageDialog(self, getString(R.string.server_error), null);
        return;
      }
    }
    DialogUtility.showMessageDialog(self, getString(R.string.server_error), null);

  }

  public interface NetBaseListener {
    void onRequestSuccess(String json);

    void onRequestError(int message);
  }

  public void parseNotStandardResponse(int statusCode, String response, NetBaseListener listener) {

    switch (statusCode) {
      case NetBase.NETWORK_OFF:
        AppUtil.alertNetworkUnavailableCommon(self);
        break;
      case NetBase.OK:
        listener.onRequestSuccess(response);
        break;
      default:
        AppUtil.alert(self, getString(R.string.server_error));

    }
  }

  private void onLogout() {
    AsyncHttpGet asyncGet = new AsyncHttpGet(self, myAccount.getToken(), new AsyncHttpResponse() {
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
            preferences.clearUser();
            fbClearToken(self);
            gotoActivity(self, LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, null);

    asyncGet.execute(WebServiceConfig.URL_LOGOUT);

  }

  public void setMyAccount(Account acc) {
    acc.setToken(myAccount.getToken());
    myAccount = acc;
  }

  public void showRefreshLayout(final SwipeRefreshLayout swipeRefreshLayout) {
    if (swipeRefreshLayout != null) {
      swipeRefreshLayout.post(new Runnable() {
        @Override
        public void run() {
          swipeRefreshLayout.setRefreshing(true);

        }
      });
    }
  }

  public void hideRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
      swipeRefreshLayout.setRefreshing(false);
    }
  }

  public void fbClearToken(Context context) {
    Session session = Session.getActiveSession();
    if (session != null) {

      if (!session.isClosed()) {
        session.closeAndClearTokenInformation();
        //clear your preferences if saved
      }
    } else {
      session = new Session(self);
      Session.setActiveSession(session);

      session.closeAndClearTokenInformation();
      //clear your preferences if saved
    }
  }

  public void gotoUserProfileActivity(String userID, String username) {
    Bundle bundle = new Bundle();
    if (userID.equalsIgnoreCase(myAccount.getUserID())) {
      bundle.putString(GlobalValue.KEY_USER, userID);
      bundle.putString(GlobalValue.KEY_USERNAME, username);
      gotoActivity(self, HomeActivity.class, bundle, Intent.FLAG_ACTIVITY_CLEAR_TOP);
    } else {
      bundle.putString(GlobalValue.KEY_USER, userID);
      bundle.putString(GlobalValue.KEY_USERNAME, username);
      gotoActivity(self, UserProfileActivity.class, bundle);
    }
  }

  public void gotoItemDetailsActivity(String itemID) {
    Bundle bundle = new Bundle();
    bundle.putString(GlobalValue.KEY_ITEM, itemID);
    gotoActivity(self, ItemDetailsActivity.class, bundle);
  }

  public void setActionBarTitle(String title) {
//        SpannableString s = new SpannableString(title);
//        s.setSpan(new TypefaceSpan(this, "verdana.ttf"), 0, s.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    getSupportActionBar().setTitle("");


    this.getSupportActionBar().setDisplayShowCustomEnabled(true);
    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);

    LayoutInflater inflater = LayoutInflater.from(this);
    View v = inflater.inflate(R.layout.action_bar_view, null);

    titleTextView = (TextView) v.findViewById(R.id.tvActionbarTitle);
    titleTextView.setText(title);

    this.getSupportActionBar().setCustomView(v);

    ActionBar.LayoutParams lp = (ActionBar.LayoutParams) v
        .getLayoutParams();
    lp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
    v.setLayoutParams(lp);
  }

  public void updateTutorial(int tutorial) {
    List<NameValuePair> params = ParameterFactory.createUpdateTutorialParam(tutorial);

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {

      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_UPDATE_TUTORIAL);
  }

  public View getParentView(View v) {
    return (View) v.getParent();
  }
}
