package com.pendulab.theExchange.activity;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.astuetz.PagerSlidingTabStrip;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.SharedPreferencesManager;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.database.DatabaseHelper;
import com.pendulab.theExchange.fragment.ActivityFragment;
import com.pendulab.theExchange.fragment.BrowseFragment;
import com.pendulab.theExchange.fragment.ProfileFragment;
import com.pendulab.theExchange.model.Category;
import com.pendulab.theExchange.model.Message;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.ParameterFactory;
import com.pendulab.theExchange.widget.CustomViewPager;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


/**
 * Created by Anh Ha Nguyen on 5/27/2015.
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {

  private Toolbar toolbar;

  private Stack<Fragment> mStackFragments = new Stack<Fragment>();

  private PagerSlidingTabStrip tabs;
  public CustomViewPager pager;
  public MyPagerAdapter adapter;
  private int currentPage = 0;
  private ImageView ivActionInbox, ivActionFindUser, ivActionSearch, ivActionAdd;
  private TextView tvNewMessage;

  private LinearLayout mTabsLinearLayout, llLogo;

  private String device_type = "0", gcmId = "";
  private boolean isFirstTime = true;

  private final String SHOWCASE_ID = "HOME_SHOWCASE";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Handle Toolbar
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setElevation(0);
    getSupportActionBar().setHomeButtonEnabled(false);
    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    getSupportActionBar().setTitle("");

    initUI();
    initData();
    initControl();

    Bundle bundle = getIntent().getExtras();
    if (!preferences.getBooleanValue(SharedPreferencesManager.AFTER_FIRST_TIME) || (bundle != null && bundle.containsKey(GlobalValue.KEY_VIEW_TUTORIAL))) {
      initShowcase();
    }

    updateRegID();
  }

  private void initUI() {
    tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
    pager = (CustomViewPager) findViewById(R.id.pager);

    tvNewMessage = (TextView) findViewById(R.id.tvNewMessage);
    ivActionAdd = (ImageView) toolbar.findViewById(R.id.ivActionAdd);
    ivActionSearch = (ImageView) toolbar.findViewById(R.id.ivActionSearch);
    ivActionFindUser = (ImageView) toolbar.findViewById(R.id.ivActionFindUser);
    ivActionInbox = (ImageView) toolbar.findViewById(R.id.ivActionInbox);

    llLogo = (LinearLayout) findViewById(R.id.llLogo);
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

    updateRegID();
    Bundle bundle = getIntent().getExtras();
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_USER) && bundle.containsKey(GlobalValue.KEY_USERNAME)) {
      if (pager != null) pager.setCurrentItem(2);
    }
  }

  private void initControl() {
    ivActionInbox.setOnClickListener(this);
    ivActionFindUser.setOnClickListener(this);
    ivActionSearch.setOnClickListener(this);
    ivActionAdd.setOnClickListener(this);

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

  private void initShowcase() {
    ShowcaseConfig config = new ShowcaseConfig();
    config.setDelay(200); // half second between each showcase view

    final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

    sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
      @Override
      public void onShow(MaterialShowcaseView itemView, int position) {
//                Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
      }
    });

    sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
      @Override
      public void onDismiss(MaterialShowcaseView itemView, int position) {
        if (sequence.getmShowcaseQueue().size() == 0) {
          Bundle bundle = new Bundle();
          bundle.putString(GlobalValue.KEY_VIEW_TUTORIAL, GlobalValue.KEY_VIEW_TUTORIAL);
          gotoActivity(self, AddListingActivity.class, bundle);
        }
      }
    });

    sequence.setConfig(config);

    sequence.addSequenceItem(llLogo, getString(R.string.tut_welcome), getString(R.string.tut_next));
    sequence.addSequenceItem(ivActionAdd, getString(R.string.tut_upload_item), getString(R.string.tut_next));
    sequence.addSequenceItem(ivActionSearch, getString(R.string.tut_look_for_item), getString(R.string.tut_next));

    sequence.start();

  }

  @Override
  public void onClick(View view) {
    if (view == ivActionAdd) {
      gotoActivity(self, AddListingActivity.class);
      return;
    }

    if (view == ivActionSearch) {
      preferences.putStringValue(SharedPreferencesManager.BROWSE_CATEGORY_FILTER, Category.ALL_CATEGORIES + "");
      gotoActivity(self, ItemBrowsingActivity.class);
      return;
    }

    if (view == ivActionInbox) {
      gotoActivity(self, InboxActivity.class);
      return;
    }

    if (view == ivActionFindUser) {
      gotoActivity(self, FindAndInviteActivity.class);
      return;
    }
  }

  public void refreshAccount() {
    preferences.saveUserInfo(myAccount);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!isFirstTime) {

    } else {
      syncMessage();
    }

  }

  @Override
  public void onBackPressed() {
    showExitDialog();
  }

  public void setTitleCustom(String title) {
//        getSupportActionBar().setTitle(title);
  }

  public class MyPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {getString(R.string.browse_item), getString(R.string.activity), getString(R.string.profile)};

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
          return BrowseFragment.getInstance();
        case 1:
          return ActivityFragment.getInstance();
        case 2:
          return ProfileFragment.getInstance();
        default:
          return null;
      }
    }
  }

  private void updateRegID() {
    if (!preferences.getStringValue(SharedPreferencesManager.GCM_ID).equalsIgnoreCase("")) {
      gcmId = preferences.getStringValue(SharedPreferencesManager.GCM_ID);
      Log.i("GCM", gcmId);
      postRegID();
    } else {
      new RegisteGCMId().execute();
    }
  }

  class RegisteGCMId extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
      try {
        GoogleCloudMessaging gcm = GoogleCloudMessaging
            .getInstance(self);
        gcmId = gcm.register(GlobalValue.SENDER_ID);
        Log.i("GCM", gcmId);
        preferences.putStringValue(SharedPreferencesManager.GCM_ID,
            gcmId);
      } catch (IOException e) {
        e.printStackTrace();
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      postRegID();

    }
  }

  private void postRegID() {
    List<NameValuePair> params = ParameterFactory.createUpdateRegIDParam(gcmId, ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId(), "0");

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {

          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_UPDATE_REG_ID);
  }

  private void syncMessage() {
    isFirstTime = false;
    String lastMessageId = DatabaseHelper.getLastMessageId(myAccount.getUserID());
    Log.i("LAST_MSG_ID", lastMessageId);
    List<NameValuePair> param = ParameterFactory.createSyncMessageParam(lastMessageId);

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<Message> list = CommonParser.parseListMessagesFromJson(json);
            DatabaseHelper.insertMessage(list, self);
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, param);

    asyncPost.execute(WebServiceConfig.URL_SYNC_MESSAGE);
  }


}
