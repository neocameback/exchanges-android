package com.pendulab.theExchange.activity;

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

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/1/2015.
 */
public class VerifyAccountActivity extends BaseActivity {

  private EditText etCode;
  private TextView tvResend, tvSplit;
  private Account registerAccount;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_verify_account);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.verify_your_account));

    registerAccount = preferences.getRegisterAccount();

    initUI();
    initData();
    initControl();
  }

  private void initUI() {
    etCode = (EditText) findViewById(R.id.etCode);
    tvResend = (TextView) findViewById(R.id.tvResendMessage);
    tvSplit = (TextView) findViewById(R.id.tvSplit);

    tvSplit.setText(getString(R.string.verify_message_head) + " (" + registerAccount.getPhoneNumber() + "). " + getString(R.string.verify_message_tail));

    //Auto fill code before integrating sms
//        etCode.setText(registerAccount.getVerificationCode());
  }

  private void initData() {

  }

  private void initControl() {
    tvResend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

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
//            if (validate()) {
//                verifyAccount();
//            }
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

  public void onClickDone(View v) {
    if (validate()) {
      verifyAccount();
    }
  }

  private boolean validate() {
    if (etCode.getText().toString().trim().length() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.message_alert_verification_code), null);
      return false;
    }
    return true;
  }

  private void verifyAccount() {
    List<NameValuePair> params = ParameterFactory.createVerifyAccountParams(registerAccount.getUserID(), registerAccount.getVerificationCode());

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
            Account account = CommonParser.parseUserFromJson(json, "");
            preferences.saveUserInfo(account);
            DialogUtility.showMessageDialog(self, getString(R.string.message_verify_successfully), new DialogUtility.DialogListener() {
              @Override
              public void onClose(Dialog dialog) {
                gotoActivity(self, HomeActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
              }
            });
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_VERIFY_ACCOUNT);
  }
}
