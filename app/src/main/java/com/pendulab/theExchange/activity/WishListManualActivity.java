package com.pendulab.theExchange.activity;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.ActivityKeyboardObserver;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/30/2015.
 */
public class WishListManualActivity extends BaseActivity {

  private EditText etTitle, etMinPrice, etMaxPrice;
  private int mode;
  public static int MODE_NEW = 1;
  public static int MODE_EDIT = 2;

  private int editPosition;
  private Item editItem;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_wishlist_manual);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_WISHLIST)) {
      if (bundle.getInt(GlobalValue.KEY_WISHLIST) == MODE_NEW) mode = MODE_NEW;
      else if (bundle.getInt(GlobalValue.KEY_WISHLIST) == MODE_EDIT) mode = MODE_EDIT;
      if (bundle.containsKey(GlobalValue.KEY_ITEM)) {
        editItem = bundle.getParcelable(GlobalValue.KEY_ITEM);
      }
      if (bundle.containsKey(GlobalValue.KEY_POSITION)) {
        editPosition = bundle.getInt(GlobalValue.KEY_POSITION);
      }
    }

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.add_new_item_to_wishlist));

    initUI();
    initData();
    initControl();
  }

  private void initUI() {
    etTitle = (EditText) findViewById(R.id.etTitle);
    etMinPrice = (EditText) findViewById(R.id.etMinPrice);
    etMaxPrice = (EditText) findViewById(R.id.etMaxPrice);

  }

  private void initData() {
    if (mode == MODE_EDIT) {
      etTitle.setText(editItem.getTitle());
    }
  }

  private void initControl() {
    findViewById(R.id.tvCredits).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        etMinPrice.requestFocus();
        showSoftKeyboard(etMinPrice);
      }
    });

    findViewById(R.id.tvCredits2).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        etMaxPrice.requestFocus();
        showSoftKeyboard(etMaxPrice);
      }
    });

    etMinPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View view, boolean focus) {
        if (focus) {
          if (etMinPrice.getText().toString().trim().equalsIgnoreCase("0")) {
            etMinPrice.setText("");
          }
          showSoftKeyboard(etMinPrice);
        } else {
          if (etMinPrice.getText().toString().trim().length() == 0) {
            etMinPrice.setText("0");
          }
        }
      }
    });


    etMaxPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View view, boolean focus) {
        if (focus) {
          if (etMaxPrice.getText().toString().trim().equalsIgnoreCase("0")) {
            etMaxPrice.setText("");
          }
          showSoftKeyboard(etMaxPrice);
        } else {
          if (etMaxPrice.getText().toString().trim().length() == 0) {
            etMaxPrice.setText("0");
          }
        }
      }
    });

    ActivityKeyboardObserver.assistActivity(self, new ActivityKeyboardObserver.IToggleKeyboard() {
      @Override
      public void onShowing() {

      }

      @Override
      public void onHidden() {
        if (etMinPrice.getText().toString().trim().length() == 0) {
          etMinPrice.setText("0");
        }
        if (etMaxPrice.getText().toString().trim().length() == 0) {
          etMaxPrice.setText("0");
        }
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
//            if (validateAdd()) {
//                addWishList();
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }

  public void onClickDone(View v) {
    if (validateAdd()) {
      if (mode == MODE_NEW)
        addWishList();
      else if (mode == MODE_EDIT)
        editWishlistManual();
    }
  }

  private boolean validateAdd() {
    if (etTitle.getText().toString().trim().length() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.message_alert_wishlist_title), new DialogUtility.DialogListener() {
        @Override
        public void onClose(Dialog dialog) {
          etTitle.requestFocus();
          showSoftKeyboard(etTitle);
        }
      });
      return false;
    }

    if ((!etMaxPrice.getText().toString().trim().equalsIgnoreCase("0") && !etMaxPrice.getText().toString().trim().equalsIgnoreCase("") && !etMinPrice.getText().toString().trim().equalsIgnoreCase(""))) {
      if ((Integer.parseInt(etMaxPrice.getText().toString().trim()) <= Integer.parseInt(etMinPrice.getText().toString().trim()))) {
        DialogUtility.showMessageDialog(self, getString(R.string.message_alert_wishlist_price), new DialogUtility.DialogListener() {
          @Override
          public void onClose(Dialog dialog) {
            etMaxPrice.requestFocus();
            showSoftKeyboard(etMaxPrice);
          }
        });

        return false;
      }
    }
    return true;
  }

  private void addWishList() {
    List<NameValuePair> param = ParameterFactory.createAddWishlistManualParam(etTitle.getText().toString().trim(), "0", "0");

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
            DialogUtility.showMessageDialog(self, getString(R.string.message_add_wishlist_successfully), new DialogUtility.DialogListener() {
              @Override
              public void onClose(Dialog dialog) {
                String minPrice = etMinPrice.getText().toString().trim();
                String maxPrice = etMaxPrice.getText().toString().trim();
                Item item = new Item();
                item.setTitle(etTitle.getText().toString().trim());
                item.setDate("");
                item.setImage("");
                if (!minPrice.equalsIgnoreCase("")) {
                  if (Integer.parseInt(minPrice) > 0) {
                    item.setImage(minPrice);
                  }
                }
                if (!maxPrice.equalsIgnoreCase("")) {
                  if (Integer.parseInt(maxPrice) > 0) {
                    item.setDate(maxPrice);

                  }
                }
                Intent intent = new Intent();
                intent.putExtra(GlobalValue.KEY_ITEM, item);
                setResult(RESULT_OK, intent);
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
    }, param);

    asyncPost.execute(WebServiceConfig.URL_ADD_WISHLIST_MANUAL);
  }

  private void editWishlistManual() {
    String postUrl = ParameterFactory.createEditWishlistManualParam(editItem.getId());
    List<NameValuePair> param = ParameterFactory.createAddWishlistManualParam(etTitle.getText().toString().trim(), "0", "0");

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
            DialogUtility.showMessageDialog(self, getString(R.string.message_add_wishlist_successfully), new DialogUtility.DialogListener() {
              @Override
              public void onClose(Dialog dialog) {
                String minPrice = etMinPrice.getText().toString().trim();
                String maxPrice = etMaxPrice.getText().toString().trim();
                editItem.setTitle(etTitle.getText().toString().trim());
                editItem.setDate("");
                editItem.setImage("");
                if (!minPrice.equalsIgnoreCase("")) {
                  if (Integer.parseInt(minPrice) > 0) {
                    editItem.setImage(minPrice);
                  }
                }
                if (!maxPrice.equalsIgnoreCase("")) {
                  if (Integer.parseInt(maxPrice) > 0) {
                    editItem.setDate(maxPrice);

                  }
                }
                Intent intent = new Intent();
                intent.putExtra(GlobalValue.KEY_ITEM, editItem);
                intent.putExtra(GlobalValue.KEY_POSITION, editPosition);
                setResult(RESULT_OK, intent);
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
    }, param);

    asyncPost.execute(postUrl);
  }
}
