package com.pendulab.theExchange.base;

import com.pendulab.theExchange.activity.HomeActivity;
import com.pendulab.theExchange.utils.SmartLog;
import com.pendulab.theExchange.utils.StringUtility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseFragment extends Fragment {

  public static final int REQUEST_CODE_NOT_USE = -10000000;
  public static final int RESULT_CODE_NOT_USE = -10000001;
  public String TAG;
  public String titlePage;
  private Bundle mData;
  public Activity self;
  public Handler handler;

  public abstract int getFragmentResource();

  public abstract void initView(View view);

  public abstract void initData(View view);

  public abstract void initControl(View view);

  /**
   * onFragmentBackPressed use key back if return true will execute
   * onBackPressed of activity
   */
  protected boolean onFragmentBackPressed() {
    // will be process in sub fragment
    //
    return true;
  }

  public void setTitlePage(String titlePage) {
    this.titlePage = titlePage;
  }

  public String getTitlePage() {
    return titlePage;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    self = getActivity();
    TAG = this.getClass().getSimpleName();
    handler = new Handler();

  }

  @Override
  public void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    setTile(titlePage);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    int fragmentResource = getFragmentResource();
    if (fragmentResource > 0) {
      View v = inflater.inflate(fragmentResource, container, false);
      initView(v);
      initData(v);
      initControl(v);
      return v;
    }
    return null;
  }

  @Override
  public void setArguments(Bundle args) {
    // super.setArguments(args);
    this.mData = args;
  }

  /**
   * use getArgumentsData() instead getArguments()
   *
   * @return Bundle
   */
  public Bundle getArgumentsData() {
    return this.mData;
  }

  public void setTile(String title) {
    this.titlePage = title;
    getParentActivity().getSupportActionBar().setTitle(title);
  }


  public String createTitleByUser(String nameLook, String byUserName) {
    StringBuilder builder = new StringBuilder();
    if (!StringUtility.isEmpty(nameLook)) {
      builder.append(nameLook + "\n");
    }
    builder.append("Title by" + " " + byUserName);

    SmartLog.logD("builder:" + builder.toString());
    return builder.toString();
  }


  protected View getParentView(View v) {
    View parentView = (View) v.getParent();
    return parentView;
  }

  public void gotoActivity(Context context, Class<?> cla) {
    Intent intent = new Intent(context, cla);
    startActivity(intent);
//        self.overridePendingTransition(R.anim.slide_in_left, R.anim.push_left_out);
  }

  public void gotoActivity(Context context, Class<?> cla, int flag) {
    Intent intent = new Intent(context, cla);
    intent.setFlags(flag);
    startActivity(intent);
//        self.overridePendingTransition(R.anim.slide_in_left, R.anim.push_left_out);
  }

  public void gotoActivity(Context context, Class<?> cla, Bundle b) {
    Intent intent = new Intent(context, cla);
    intent.putExtras(b);
    startActivity(intent);
//        self.overridePendingTransition(R.anim.slide_in_left, R.anim.push_left_out);
  }

  /**
   * Go to other activity
   */
  public void gotoActivityForResult(Context context, Class<?> cla,
                                    int requestCode) {
    Intent intent = new Intent(context, cla);
    startActivityForResult(intent, requestCode);
//        self.overridePendingTransition(R.anim.slide_in_left, R.anim.push_left_out);
  }

  /**
   * Goto activity with bundle
   */
  public void gotoActivityForResult(Context context, Class<?> cla,
                                    Bundle bundle, int requestCode) {
    Intent intent = new Intent(context, cla);
    intent.putExtras(bundle);
    startActivityForResult(intent, requestCode);
//        self.overridePendingTransition(R.anim.slide_in_left, R.anim.push_left_out);
  }

  public HomeActivity getHomeActivity() {
    try {
      return (HomeActivity) self;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public AppCompatActivity getParentActivity() {
    try {
      return (AppCompatActivity) self;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public BaseActivity getBaseActivity() {
    return (BaseActivity) self;
  }


  // @Override
  // public void onDestroy() {
  // // TODO Auto-generated method stub
  // super.onDestroy();
  //
  // }

}