package com.pendulab.theExchange.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 5/26/2015.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

  private TextView tvUserName, tvPassword, tvForgotPass, tvCreateAccount;

  private EditText etUsername, etPassword;

  private RelativeLayout rlFacebook, rlGoogle;

  public static final int RC_SIGN_IN = 300;
  public GoogleApiClient mGoogleApiClient;
  public boolean mIntentInProgress;
  private boolean isFetching;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.login));

    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this).addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    mGoogleApiClient.disconnect();

    fbClearToken(self);
    initUI();
    initControl();
  }

  private void initUI() {
    tvForgotPass = (TextView) findViewById(R.id.tvFortgotPass);
    tvCreateAccount = (TextView) findViewById(R.id.tvCreateAccount);

    etUsername = (EditText) findViewById(R.id.etUsername);
    etPassword = (EditText) findViewById(R.id.etPassword);

    rlFacebook = (RelativeLayout) findViewById(R.id.rlFacebook);
    rlGoogle = (RelativeLayout) findViewById(R.id.rlGoogle);
  }

  private void initControl() {
    tvCreateAccount.setOnClickListener(this);
    tvForgotPass.setOnClickListener(this);
    rlFacebook.setOnClickListener(this);
    rlGoogle.setOnClickListener(this);

  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mGoogleApiClient = null;
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (requestCode == RC_SIGN_IN) {
      if (resultCode == RESULT_OK) {
        mIntentInProgress = false;
        if (!mGoogleApiClient.isConnecting()) {
          mGoogleApiClient.connect();
        }
      } else {
        mIntentInProgress = false;
      }
    }

    if (Session.getActiveSession() != null && !Session.getActiveSession().isOpened()) {
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
  }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_activity_login, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_signup) {
//            gotoActivity(self, RegisterActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

  public void onClickDone(View v) {
    onLogin();
  }

  @Override
  public void onClick(View view) {

    if (view == tvForgotPass) {
      gotoActivity(self, ForgotPasswordActivity.class);
      return;
    }
    if (view == tvCreateAccount) {
      gotoActivity(self, RegisterActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
      return;
    }
    if (view == rlFacebook) {
      performFacebookLogin();
      return;
    }
    if (view == rlGoogle) {
      if (!mGoogleApiClient.isConnected()) {
        mIntentInProgress = false;
        mGoogleApiClient.connect();
        DialogUtility.showProgressDialog(self);
      } else {
        mIntentInProgress = false;
        mGoogleApiClient.reconnect();
        DialogUtility.showProgressDialog(self);
      }
      return;
    }
  }

  private void onLogin() {
    if (validateLogin()) {
      doLogin();
    }
  }

  private boolean validateLogin() {
    if (etUsername.getText().length() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.username_alert), null);
      etUsername.requestFocus();
      return false;
    } else if (etPassword.getText().length() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.password_alert), null);
      etPassword.requestFocus();
      return false;
    }
    return true;
  }

  private void doLogin() {

    List<NameValuePair> params = ParameterFactory.createLoginParams(etUsername.getText().toString().trim(), etPassword.getText().toString(), ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, new AsyncHttpResponse() {
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
            Account myAccount = CommonParser.parseUserFromJson(json, "");
            preferences.saveUserInfo(myAccount);
            gotoActivity(self, HomeActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        DialogUtility.showMessageDialog(self, getString(R.string.message_login_successfully), new DialogUtility.DialogListener() {
//                            @Override
//                            public void onClose(Dialog dialog) {
//                                gotoActivity(self, HomeActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                            }
//                        });
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_LOGIN);

  }

  private void performFacebookLogin() {
    if (!isFetching) {
      final Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, Arrays.asList("public_profile", "email", "user_friends"));

      Session openActiveSession = Session.openActiveSession(this, true, new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
          Log.d("FACEBOOK", "call");
          isFetching = false;
          if (session.isOpened()) {
            Log.d("FACEBOOK", "if (session.isOpened() && !isFetching)");

            session.requestNewReadPermissions(newPermissionsRequest);


            DialogUtility.showProgressDialog(self);
            Request getMe = Request.newMeRequest(session, new Request.GraphUserCallback() {
              @Override
              public void onCompleted(GraphUser user, Response response) {
                DialogUtility.closeProgressDialog();
                if (user != null) {
                  String userID = user.getId();
                  Log.i("FACEBOOK", userID);

                  loginSocial(userID);

                }
              }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name, last_name,email,gender, birthday");
            getMe.setParameters(parameters);
            getMe.executeAsync();
          } else {
            if (!session.isOpened())
              Log.i("FACEBOOK", "!session.isOpened()");
            else
              Log.i("FACEBOOK", "isFetching");

          }
        }
      });
    }
  }

  @Override
  public void onConnectionFailed(ConnectionResult result) {
    DialogUtility.closeProgressDialog();
    if (!mIntentInProgress && result.hasResolution()) {
      try {
        mIntentInProgress = true;
        startIntentSenderForResult(result.getResolution()
            .getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
      } catch (IntentSender.SendIntentException e) {
        // The intent was canceled before it was sent. Return to the
        // default
        // state and attempt to connect to get an updated
        // ConnectionResult.
        mIntentInProgress = false;
        mGoogleApiClient.connect();
      }
    }
  }

  @Override
  public void onConnected(Bundle connectionHint) {
    DialogUtility.closeProgressDialog();

    Person myProfile = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
    String userId = myProfile.getId();
    loginSocial(userId);
  }

  @Override
  public void onConnectionSuspended(int arg0) {
    mGoogleApiClient.connect();

  }

  private void loginSocial(String socialID) {
    List<NameValuePair> params = ParameterFactory.createLoginSocialParams(socialID, "", ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
    AsyncHttpPost asyncPost = new AsyncHttpPost(self, new AsyncHttpResponse() {
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
            Account myAccount = CommonParser.parseUserFromJson(json, "");
            preferences.saveUserInfo(myAccount);
            gotoActivity(self, HomeActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                DialogUtility.showMessageDialog(self, getString(R.string.message_login_successfully), new DialogUtility.DialogListener() {
//                                    @Override
//                                    public void onClose(Dialog dialog) {
//                                        gotoActivity(self, HomeActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    }
//                                });
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_LOGIN_SOCIAL);

  }


}
