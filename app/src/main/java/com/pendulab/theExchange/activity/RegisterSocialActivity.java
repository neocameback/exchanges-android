package com.pendulab.theExchange.activity;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.PhoneCodeAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.SharedPreferencesManager;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.model.PhoneCode;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.net.HttpMultiPart;
import com.pendulab.theExchange.utils.CircleTransformation;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 9/30/2015.
 */
public class RegisterSocialActivity extends BaseActivity {

  private ImageView ivAvatar;

  private EditText etUsername, etPassword, etPhoneNumber;
  private TextView tvSplit;
  private Spinner spPhoneCode;
  private List<PhoneCode> arrPhoneCode;
  private PhoneCodeAdapter phoneCodeAdapter;
  private Account registerAccount;

  private MultipartEntity reqEntity;
  private boolean isRegistering;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register_social);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);

    initUI();
    initData();
    initControl();
  }

  private void initUI() {
    ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
    etUsername = (EditText) findViewById(R.id.etUsername);
    etPassword = (EditText) findViewById(R.id.etPassword);
    etPassword.setVisibility(View.GONE);
    etPhoneNumber = (EditText) findViewById(R.id.etPhone);
    spPhoneCode = (Spinner) findViewById(R.id.spnPhoneCode);
    tvSplit = (TextView) findViewById(R.id.tvSplit);
  }

  private void initData() {
    Bundle bundle = getIntent().getExtras();
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_USER)) {
      registerAccount = bundle.getParcelable(GlobalValue.KEY_USER);
      if (registerAccount != null) {
        tvSplit.setText(getString(R.string.one_more_step_to_join_us) + (registerAccount.getFirstname().length() > 0 ? ", " + registerAccount.getFirstname() : (registerAccount.getLastname().length() > 0 ? ", " + registerAccount.getLastname() : "")));
        etUsername.setText(registerAccount.getUsername());
        Glide.with(self).load(registerAccount.getImage()).transform(new CircleTransformation(self)).into(ivAvatar);

        if (registerAccount.getUserType().equalsIgnoreCase(Account.USER_TYPE_FACEBOOK)) {
          setActionBarTitle(getString(R.string.signup_with) + " Facebook");
        } else if (registerAccount.getUserType().equalsIgnoreCase(Account.USER_TYPE_GOOGLE)) {
          setActionBarTitle(getString(R.string.signup_with) + " Google+");
        }
      }
    }

    arrPhoneCode = new ArrayList<>();
    phoneCodeAdapter = new PhoneCodeAdapter(self, arrPhoneCode);
    spPhoneCode.setAdapter(phoneCodeAdapter);

    getPhoneCode();

  }

  private void initControl() {
    spPhoneCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
  }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_activity_login, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_accept) {
//            if (validateSignup()) {
//                doSignup();
//            }
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

  public void onClickDone(View v) {
    if (validateSignup()) {
      doSignup();
    }
  }

  private void getPhoneCode() {
    if (preferences.getStringValue(SharedPreferencesManager.PHONE_CODES).length() > 0) {
      List<PhoneCode> list = CommonParser.parseListPhonecodeFromJson(preferences.getStringValue(SharedPreferencesManager.PHONE_CODES));
      arrPhoneCode.addAll(list);
      phoneCodeAdapter.notifyDataSetChanged();
      setSpinner();
    } else {
      AsyncHttpGet asyncGet = new AsyncHttpGet(self, new AsyncHttpResponse() {
        @Override
        public void before() {

        }

        @Override
        public void after(int statusCode, String response) {
          parseResponseStatus(statusCode, response, new NetBaseListener() {
            @Override
            public void onRequestSuccess(String json) {
              preferences.putStringValue(SharedPreferencesManager.PHONE_CODES, json);
              List<PhoneCode> list = CommonParser.parseListPhonecodeFromJson(json);
              arrPhoneCode.addAll(list);
              phoneCodeAdapter.notifyDataSetChanged();
              setSpinner();
            }

            @Override
            public void onRequestError(int message) {

            }
          });
        }
      }, null);
      asyncGet.execute(WebServiceConfig.URL_GET_PHONECODE);
    }
  }

  private void setSpinner() {
    String myCountry = preferences.getStringValue(SharedPreferencesManager.MY_COUNTRY_CODE);
    for (PhoneCode code : arrPhoneCode) {
      if (code.getLocale().equalsIgnoreCase(myCountry)) {
        spPhoneCode.setSelection(arrPhoneCode.indexOf(code));
      }
    }
  }

  private boolean validateSignup() {

    if (etUsername.getText().toString().trim().length() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.username_alert), null);
      return false;
    }
    if (etPhoneNumber.getText().toString().trim().length() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.phonenumber_alert), null);
      return false;
    }
    return true;
  }

  private void doSignup() {
    if (!isRegistering) {
      isRegistering = true;
      try {
        reqEntity = new MultipartEntity(
            HttpMultipartMode.BROWSER_COMPATIBLE);

        reqEntity.addPart("username", new StringBody(etUsername.getText().toString().trim()));
        reqEntity.addPart("social_id", new StringBody(registerAccount.getUserID()));
        reqEntity.addPart("phone_number", new StringBody(arrPhoneCode.get(spPhoneCode.getSelectedItemPosition()).getCode() + etPhoneNumber.getText().toString().trim()));
        reqEntity.addPart("email", new StringBody(registerAccount.getEmail()));
        reqEntity.addPart("firstname", new StringBody(registerAccount.getFirstname()));
        reqEntity.addPart("lastname", new StringBody(registerAccount.getLastname()));
        reqEntity.addPart("image", new StringBody(registerAccount.getImage()));
        reqEntity.addPart("gender", new StringBody(registerAccount.getGender()));
        reqEntity.addPart("social_type", new StringBody(registerAccount.getUserType()));

        HttpMultiPart httpMultiPart = new HttpMultiPart(self, reqEntity, WebServiceConfig.URL_REGISTER_SOCIAL, true, "", new HttpMultiPart.HttpMultipartResponse() {
          @Override
          public void onSuccess(int statusCode, String json) {
            isRegistering = false;
            Account registerAccount = CommonParser.parseUserFromJson(json, "");
            preferences.saveRegisterAccount(registerAccount);
            parseResponseStatus(statusCode, json, new NetBaseListener() {
              @Override
              public void onRequestSuccess(String json) {
                DialogUtility.showMessageDialog(self, getString(R.string.message_register_successfully), new DialogUtility.DialogListener() {
                  @Override
                  public void onClose(Dialog dialog) {
                    gotoActivity(self, VerifyAccountActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                  }
                });
              }

              @Override
              public void onRequestError(int message) {
                _onRequestError(message);
              }
            });
          }

          @Override
          public void onFailure(int statusCode, String error) {
            isRegistering = false;
            DialogUtility.showMessageDialog(self, getString(R.string.message_register_failed), null);
          }
        });

        httpMultiPart.execute();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  }
}
