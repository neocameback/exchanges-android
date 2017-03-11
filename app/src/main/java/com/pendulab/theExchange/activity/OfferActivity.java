package com.pendulab.theExchange.activity;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.OfferInventoryAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.SharedPreferencesManager;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.AppUtil;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by Anh Ha Nguyen on 11/15/2015.
 */
public class OfferActivity extends BaseActivity implements View.OnClickListener {

  private ImageView ivItem, ivUser, iv1, iv2, iv3, iv4, ivClose1, ivClose2, ivClose3, ivClose4;
  private TextView tvItem, tvUser, tvCredit, tvGetMoreCredit;
  private EditText etOffer;

  private Item currentItem;

  private LinearLayout llImages;

  private List<ImageView> arrImageViews;
  private List<ImageView> arrCloseIcons;

  private Dialog dialog;
  private ListView lvInventory;
  private TextView tvOK, tvCancel;
  private List<Item> arrInventory;
  private List<Item> arrSelections;
  private OfferInventoryAdapter inventoryAdapter;

  private final String SHOWCASE_ID = "OFFER_SHOW_CASE";

  private Bundle bundle = null;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_offer);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.offer));

    bundle = getIntent().getExtras();
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_ITEM)) {
      currentItem = bundle.getParcelable(GlobalValue.KEY_ITEM);
    }

    initUI();
    initData();
    initControl();
    if (bundle.containsKey(GlobalValue.KEY_VIEW_TUTORIAL)) {
      initShowcase();
    }
  }

  private void initUI() {
    llImages = (LinearLayout) findViewById(R.id.llImages);

    ivItem = (ImageView) findViewById(R.id.ivItem);
    ivUser = (ImageView) findViewById(R.id.ivUser);
    iv1 = (ImageView) findViewById(R.id.iv1);
    iv2 = (ImageView) findViewById(R.id.iv2);
    iv3 = (ImageView) findViewById(R.id.iv3);
    iv4 = (ImageView) findViewById(R.id.iv4);
    ivClose1 = (ImageView) findViewById(R.id.ivClose1);
    ivClose2 = (ImageView) findViewById(R.id.ivClose2);
    ivClose3 = (ImageView) findViewById(R.id.ivClose3);
    ivClose4 = (ImageView) findViewById(R.id.ivClose4);

    etOffer = (EditText) findViewById(R.id.etOffer);

    tvItem = (TextView) findViewById(R.id.tvItem);
    tvUser = (TextView) findViewById(R.id.tvUsername);
    tvCredit = (TextView) findViewById(R.id.tvPrice);
    tvGetMoreCredit = (TextView) findViewById(R.id.tvGetMoreCredits);

    Glide.with(self).load(R.drawable.ic_credit).into((ImageView) findViewById(R.id.ivCredits));

    int layoutHeight = (AppUtil.getDeviceScreenWidth(self) - AppUtil.getDimensionInPixel(self, R.dimen.margin_padding_small) * 5) / 4;

    LinearLayout.LayoutParams layoutParam = (LinearLayout.LayoutParams) llImages.getLayoutParams();
    layoutParam.height = layoutHeight;
    llImages.setLayoutParams(layoutParam);


    RelativeLayout.LayoutParams imageParam = (RelativeLayout.LayoutParams) iv1.getLayoutParams();
    imageParam.height = layoutHeight;
    iv1.setLayoutParams(imageParam);

    RelativeLayout.LayoutParams imageParam2 = (RelativeLayout.LayoutParams) iv2.getLayoutParams();
    imageParam2.height = layoutHeight;
    iv2.setLayoutParams(imageParam2);
    iv3.setLayoutParams(imageParam2);
    iv4.setLayoutParams(imageParam2);
  }

  private void initData() {

    Glide.with(self).load(currentItem.getImage()).error(R.drawable.image_not_available).placeholder(R.drawable.image_placeholder).into(ivItem);
    tvItem.setText(currentItem.getTitle());
    tvCredit.setText(currentItem.getPrice() + "");
    Glide.with(self).load(currentItem.getOwnerImage()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(ivUser);
    tvUser.setText(currentItem.getOwnerUsername());
    etOffer.setText(currentItem.getPrice() + "");

    arrImageViews = new ArrayList<>();
    arrImageViews.add(iv1);
    arrImageViews.add(iv2);
    arrImageViews.add(iv3);
    arrImageViews.add(iv4);

    arrCloseIcons = new ArrayList<>();
    arrCloseIcons.add(ivClose1);
    arrCloseIcons.add(ivClose2);
    arrCloseIcons.add(ivClose3);
    arrCloseIcons.add(ivClose4);
  }

  private void initControl() {
    iv1.setOnClickListener(this);
    iv2.setOnClickListener(this);
    iv3.setOnClickListener(this);
    iv4.setOnClickListener(this);

    ivClose1.setOnClickListener(this);
    ivClose2.setOnClickListener(this);
    ivClose3.setOnClickListener(this);
    ivClose4.setOnClickListener(this);

    tvGetMoreCredit.setOnClickListener(this);
    ivUser.setOnClickListener(this);
    tvUser.setOnClickListener(this);

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
          preferences.putBooleanValue(SharedPreferencesManager.AFTER_FIRST_TIME, true);
          gotoActivity(self, HomeActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
          updateTutorial(GlobalValue.TUTORIAL_OFFER);
        }
      }
    });

    sequence.setConfig(config);


    sequence.addSequenceItem(tvGetMoreCredit, getString(R.string.tut_get_credit), getString(R.string.tut_got_it));

    sequence.start();
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
//            if (validateOffer()) {
//                makeOffer();
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }


  @Override
  public void onBackPressed() {
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_VIEW_TUTORIAL)) {

    } else {
      super.onBackPressed();
    }
  }

  public void onClickDone(View v) {
    if (validateOffer()) {
      makeOffer();
    }
  }

  @Override
  public void onClick(View view) {
    if (view == iv1) {
      showInventoryDialog();
      return;
    }
    if (view == iv2) {
      showInventoryDialog();
      return;
    }
    if (view == iv3) {
      showInventoryDialog();
      return;
    }
    if (view == iv4) {
      showInventoryDialog();
      return;
    }
    if (view == ivClose1) {
      removeItem(0);
      return;
    }
    if (view == ivClose2) {
      removeItem(1);
      return;
    }
    if (view == ivClose3) {
      removeItem(2);
      return;
    }
    if (view == ivClose4) {
      removeItem(3);
      return;
    }
    if (view == tvGetMoreCredit) {
      gotoActivity(self, GetCreditActivity.class);
      return;
    }
  }

  private void setCloseIcons() {
    for (int i = 0; i < arrCloseIcons.size(); i++) {
      arrCloseIcons.get(i).setVisibility(View.GONE);
    }
    for (int i = 0; i < arrSelections.size(); i++) {
      if (arrSelections.get(i) != null) {
        arrCloseIcons.get(i).setVisibility(View.VISIBLE);
      }
    }
  }

  private void removeItem(int position) {
    arrSelections.remove(position);
    Glide.with(self).load(R.drawable.ic_action_camera_dark).into(arrImageViews.get(position));
    fillImages();
  }

  private void fillImages() {
    arrSelections = inventoryAdapter.getSelections();
    for (int i = 0; i < arrImageViews.size(); i++) {
      Glide.with(self).load(R.drawable.ic_action_camera_dark).into(arrImageViews.get(i));
    }
    for (int i = 0; i < arrSelections.size(); i++) {
      Glide.with(self).load(arrSelections.get(i).getImage()).error(R.drawable.image_not_available).into(arrImageViews.get(i));
    }
    setCloseIcons();
  }

  private void showInventoryDialog() {
    if (arrInventory == null) {
      arrInventory = new ArrayList<>();
      getInventory();
      return;
    }

    if (arrSelections == null) {
      arrSelections = new ArrayList<>();
    }

    dialog = new Dialog(self);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(
//                new ColorDrawable(Color.TRANSPARENT));
    dialog.setContentView(R.layout.dialog_offer_inventory);

    lvInventory = (ListView) dialog.findViewById(R.id.lvInventory);
    tvOK = (TextView) dialog.findViewById(R.id.tvOk);
    tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);

    inventoryAdapter = new OfferInventoryAdapter(self, arrInventory, arrSelections);

    lvInventory.setAdapter(inventoryAdapter);

    tvOK.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dialog.cancel();
        fillImages();

      }
    });

    tvCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dialog.cancel();
      }
    });

    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(dialog.getWindow().getAttributes());
    lp.width = AppUtil.getDeviceScreenWidth(self) * 9 / 10;
    lp.height = AppUtil.getDeviceScreenHeight(self) * 8 / 10;

    dialog.setCancelable(true);
    dialog.show();
    dialog.getWindow().setAttributes(lp);
  }

  private void getInventory() {
    String getURL = ParameterFactory.createGetInventoryParam(myAccount.getUserID());
    AsyncHttpGet asyncGet = new AsyncHttpGet(self, myAccount.getToken(), new AsyncHttpResponse() {
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
            List<Item> list = CommonParser.parseListItemsFromJson(self, json);
            for (int i = 0; i < list.size(); i++) {
              if (list.get(i).getStatus() == Item.STATUS_AVAILABLE) {
                arrInventory.add(list.get(i));
              }
            }
//                        if(arrItems.size() > 0){
//                            tvNAoResult.setVisibility(View.GONE);
//                        }
//                        else{
//                            tvNoResult.setVisibility(View.VISIBLE);
//                        }
            showInventoryDialog();
          }

          @Override
          public void onRequestError(int message) {

            _onRequestError(message);
          }
        });
      }
    }, null);

    asyncGet.execute(getURL);
  }

  private boolean validateOffer() {
    if (etOffer.getText().toString().trim().length() == 0 && arrSelections.size() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.message_alert_offer), null);
      return false;
    }
    return true;
  }

  private void makeOffer() {
    int money = etOffer.getText().toString().trim().length() == 0 ? 0 : Integer.parseInt(etOffer.getText().toString().trim());
    String tradeItem = "";
    if (arrSelections != null) {
      for (int i = 0; i < arrSelections.size(); i++) {
        tradeItem += arrSelections.get(i).getId();
        if (i != arrSelections.size() - 1) {
          tradeItem += ",";
        }
      }
    }

    List<NameValuePair> params = ParameterFactory.createMakeOfferParam(money, tradeItem, currentItem.getId());

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
            DialogUtility.showMessageDialog(self, getString(R.string.message_make_offer_successfully), new DialogUtility.DialogListener() {
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
    asyncPost.execute(WebServiceConfig.URL_MAKE_OFFER);
  }


}
