package com.pendulab.theExchange.activity;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;
import com.pendulab.theExchange.validator.ChangePasswordValidator;

import org.apache.http.NameValuePair;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.Joined;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

/**
 * Created by Anh Ha Nguyen on 12/14/2015.
 */
public class ChangePasswordActivity extends BaseActivity {

  @NotEmpty(messageId = R.string.message_alert_field_empty)
  private EditText etOldPassword;

  @NotEmpty(messageId = R.string.message_alert_field_empty)
  private EditText etNewPassword;

  @NotEmpty(messageId = R.string.message_alert_field_empty)
  @Joined(value = {R.id.etNewPassword, R.id.etConfirmPassword},
      validator = ChangePasswordValidator.class,
      messageId = R.string.message_alert_confirm_password_not_match)
  private EditText etConfirmPassword;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_change_password);

    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.change_password));

    initUI();
    initData();
    initControl();
  }

  private void initUI() {
    etOldPassword = (EditText) findViewById(R.id.etOldPassword);
    etNewPassword = (EditText) findViewById(R.id.etNewPassword);
    etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
  }

  private void initData() {

  }

  private void initControl() {

  }

  public void onClickDone(View v) {
    if (FormValidator.validate(this, new SimpleErrorPopupCallback(this))) {
      changePassword();
    }
  }

  private void changePassword() {
    List<NameValuePair> params = ParameterFactory.createChangePassParam(etOldPassword.getText().toString().trim(), etNewPassword.getText().toString().trim());

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
            DialogUtility.showMessageDialog(self, getString(R.string.message_change_pass_successfully), new DialogUtility.DialogListener() {
              @Override
              public void onClose(Dialog dialog) {
                onBackPressed();
              }
            });
          }

          @Override
          public void onRequestError(int message) {

          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_CHANGE_PASS);
  }
}
