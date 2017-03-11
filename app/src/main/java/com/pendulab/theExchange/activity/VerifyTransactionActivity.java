package com.pendulab.theExchange.activity;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Item;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 11/16/2015.
 */
public class VerifyTransactionActivity extends BaseActivity implements View.OnClickListener {

  private TextView tvAccepted, tvResendCode, tvItem, tvUser, tvCredit, tvGetMoreCredit;
  private EditText etVerify;

  private ImageView ivItem, ivUser;

  private Item currentItem;
  private String code, transactionID;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_verify_transaction);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.verify_transaction));

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      if (bundle.containsKey(GlobalValue.KEY_ITEM))
        currentItem = bundle.getParcelable(GlobalValue.KEY_ITEM);

      if (bundle.containsKey(GlobalValue.KEY_CODE)) {
        code = bundle.getString(GlobalValue.KEY_CODE);
      }

      if (bundle.containsKey(GlobalValue.KEY_ITEM_ID)) {
        getItemDetails(bundle.getString(GlobalValue.KEY_ITEM_ID));
      }

      if (bundle.containsKey(GlobalValue.KEY_TRANSACTION_ID)) {
        transactionID = bundle.getString(GlobalValue.KEY_TRANSACTION_ID);
      }
    }

    initUI();
    initData();
    initControl();

  }

  private void initUI() {
    ivItem = (ImageView) findViewById(R.id.ivItem);
    ivUser = (ImageView) findViewById(R.id.ivUser);
    tvItem = (TextView) findViewById(R.id.tvItem);
    tvUser = (TextView) findViewById(R.id.tvUsername);
    tvCredit = (TextView) findViewById(R.id.tvPrice);


    tvResendCode = (TextView) findViewById(R.id.tvResendMessage);
    tvGetMoreCredit = (TextView) findViewById(R.id.tvGetMoreCredits);
    etVerify = (EditText) findViewById(R.id.etVerify);
  }

  private void initData() {

//        etVerify.setText(code);
    if (currentItem != null) {
      fillItemLayout();
    }
  }

  private void initControl() {
    tvGetMoreCredit.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    if (view == tvGetMoreCredit) {
      gotoActivity(self, GetCreditActivity.class);
      return;
    }
  }

  private void fillItemLayout() {
    Glide.with(self).load(currentItem.getImage()).error(R.drawable.image_not_available).placeholder(R.drawable.image_placeholder).into(ivItem);
    Glide.with(self).load(R.drawable.ic_credit).into((ImageView) findViewById(R.id.ivCredits));
    tvItem.setText(currentItem.getTitle());
    tvCredit.setText(currentItem.getPrice() + "");
    Glide.with(self).load(currentItem.getOwnerImage()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(ivUser);
    tvUser.setText(currentItem.getOwnerUsername());
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
//            verifyTransaction();
//        }
//        return super.onOptionsItemSelected(item);
//    }

  public void onClickDone(View v) {
    verifyTransaction();
  }

  private void verifyTransaction() {
    if (etVerify.getText().toString().trim().length() == 0) {
      return;
    }
    String code = etVerify.getText().toString().trim();

    List<NameValuePair> params = ParameterFactory.createVerifyTransactionParam(transactionID, code);

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
            String message = getString(R.string.message_verify_transaction_successfully);

            if (currentItem.getVerificationStatus() == Item.VERIFY_NONE) {
              if (myAccount.getUserID().equalsIgnoreCase(currentItem.getId())) {
                currentItem.setVerificationStatus(Item.VERIFY_SELL);
                message += " " + getString(R.string.message_wait_verify_buyer);

              } else {
                currentItem.setVerificationStatus(Item.VERIFY_BUY);
                message += " " + getString(R.string.message_wait_verify_buyer);
              }
            } else if (currentItem.getVerificationStatus() == Item.VERIFY_BUY || currentItem.getVerificationStatus() == Item.VERIFY_SELL) {
              currentItem.setStatus(Item.STATUS_SOLD);
              message += " " + getString(R.string.message_transaction_completed);
            }

            DialogUtility.showMessageDialog(self, message, new DialogUtility.DialogListener() {
              @Override
              public void onClose(Dialog dialog) {
                Intent intent = new Intent();
                intent.putExtra(GlobalValue.KEY_ITEM, currentItem);
                setResult(RESULT_OK, intent);
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
    asyncPost.execute(WebServiceConfig.URL_VERIFY_TRANSACTION);
  }

  private void getItemDetails(String itemID) {
    List<NameValuePair> params = ParameterFactory.createGetItemDetailsParams(itemID, 0, 0);

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, preferences.getUserInfo().getToken(), new AsyncHttpResponse() {
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
            currentItem = CommonParser.parseSingleItemFromJson(self, json);
            if (currentItem != null) {
              fillItemLayout();
            } else {
              DialogUtility.showMessageDialog(self, getString(R.string.message_server_error), null);
            }
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_GET_ITEM_DETAILS);
  }
}
