package com.pendulab.theExchange.activity;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.ReviewSuggestionAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.model.Category;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;
import com.pendulab.theExchange.widget.HorizontalListView;

import org.apache.http.NameValuePair;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.flashbulb.coloredratingbar.ColoredRatingBar;

/**
 * Created by Anh Ha Nguyen on 11/16/2015.
 */
public class RateSellerActivity extends BaseActivity implements View.OnClickListener {

  private ColoredRatingBar rbRating;
  private ImageView ivUser;
  private TextView tvUser;
  private EditText etReview;

  private List<Category> arrPositive;
  private List<Category> arrNegative;

  private Account currentUser;
  private float rate;
  private String review;

  private HorizontalListView lvPositive, lvNegative;
  private ReviewSuggestionAdapter adapterPositive, adapterNegative;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rate_seller);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.rate_seller));

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      currentUser = bundle.getParcelable(GlobalValue.KEY_USER);
      rate = bundle.getFloat(GlobalValue.KEY_RATE);
      review = bundle.getString(GlobalValue.KEY_REVIEW);

    }

    initUI();
    initData();
    initControl();
  }

  private void initUI() {
    rbRating = (ColoredRatingBar) findViewById(R.id.rbRating);
    ivUser = (ImageView) findViewById(R.id.ivUser);
    tvUser = (TextView) findViewById(R.id.tvUser);
    etReview = (EditText) findViewById(R.id.etReview);

    lvPositive = (HorizontalListView) findViewById(R.id.lvPositive);
    lvNegative = (HorizontalListView) findViewById(R.id.lvNegative);
  }

  private void initData() {

    arrPositive = new ArrayList<>();
    arrPositive.add(new Category("1", getString(R.string.great_buyer_to_deal_with), ""));
    arrPositive.add(new Category("2", getString(R.string.punctual), ""));
    arrPositive.add(new Category("3", getString(R.string.pleasant_transaction), ""));
    arrPositive.add(new Category("4", getString(R.string.speedy_replies), ""));
    arrPositive.add(new Category("5", getString(R.string.thank_you), ""));

    arrNegative = new ArrayList<>();
    arrNegative.add(new Category("1", getString(R.string.item_is_not_as_promised), ""));
    arrNegative.add(new Category("2", getString(R.string.item_is_defective), ""));
    arrNegative.add(new Category("3", getString(R.string.late_delivery), ""));

    adapterPositive = new ReviewSuggestionAdapter(self, arrPositive, true, new ReviewSuggestionAdapter.IReViewSuggestionAdapter() {
      @Override
      public void onClickSuggestion(int position) {
      }
    });
    lvPositive.setAdapter(adapterPositive);

    adapterNegative = new ReviewSuggestionAdapter(self, arrNegative, false, new ReviewSuggestionAdapter.IReViewSuggestionAdapter() {
      @Override
      public void onClickSuggestion(int position) {
      }
    });
    lvNegative.setAdapter(adapterNegative);

    rbRating.setRating(rate);
    etReview.setText(review);

    Glide.with(self).load(currentUser.getImage()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(ivUser);
    tvUser.setText(currentUser.getUsername());
  }

  private void initControl() {
    getParentView(ivUser).setOnClickListener(this);
    lvPositive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        etReview.setText(etReview.getText().toString().trim() + arrPositive.get(position).getName() + " ");
        arrPositive.remove(position);
        adapterPositive.notifyDataSetChanged();
      }
    });

    lvNegative.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        etReview.setText(etReview.getText().toString().trim() + arrNegative.get(position).getName() + " ");
        arrNegative.remove(position);
        adapterNegative.notifyDataSetChanged();
      }
    });
  }

  @Override
  public void onClick(View view) {
    if (view == getParentView(ivUser)) {
      gotoUserProfileActivity(currentUser.getUserID(), currentUser.getUsername());
      return;
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
//        if(item.getItemId() == R.id.action_accept){
//            if(validateRating()) {
//                submitRate();
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }

  public void onClickDone(View v) {
    if (validateRating()) {
      submitRate();
    }
  }

  private boolean validateRating() {
    if (rbRating.getRating() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.message_alert_rating_zero), null);
      return false;
    }
    if (etReview.getText().toString().trim().length() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.message_alert_review), null);
      return false;
    }
    return true;
  }

  private void submitRate() {
    float rate = rbRating.getRating();
    String review = etReview.getText().toString().trim();
    List<NameValuePair> param = ParameterFactory.createRateSellerParam(currentUser.getUserID(), rate, review);
    AsyncHttpPost post = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
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
            DialogUtility.showMessageDialog(self, getString(R.string.message_rate_seller_successfully), new DialogUtility.DialogListener() {
              @Override
              public void onClose(Dialog dialog) {
                Intent intent = new Intent();
                intent.putExtra(GlobalValue.KEY_RATE, GlobalValue.KEY_RATE);
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
    }, param);
    post.execute(WebServiceConfig.URL_RATE_USER);
  }
}
