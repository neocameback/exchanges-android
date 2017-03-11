package com.pendulab.theExchange.activity;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 5/28/2015.
 */
public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

  private EditText etUsername;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_forgot_password);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.recover_password));

    initUI();
    initControl();
  }

  private void initUI() {
    etUsername = (EditText) findViewById(R.id.etUsername);
  }

  private void initControl() {
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
//            if(validate()){
//                recoverPassword();
//            }
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

  public void onClickDone(View v) {
    if (validate()) {
      recoverPassword();
    }
  }

  @Override
  public void onClick(View view) {

  }

  private boolean validate() {
    if (etUsername.getText().toString().trim().length() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.username_alert), null);
      return false;
    }
    return true;
  }

  private void recoverPassword() {
    List<NameValuePair> params = ParameterFactory.createResetPassword(etUsername.getText().toString().trim());

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
//                        String newPass = CommonParser.getRecoveredPasswordFromJson(json);

            DialogUtility.showMessageDialog(self, getString(R.string.message_reset_pass_successfully), new DialogUtility.DialogListener() {
              @Override
              public void onClose(Dialog dialog) {
                onBackPressed();
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
    asyncPost.execute(WebServiceConfig.URL_RESET_PASSWORD);
  }
}
