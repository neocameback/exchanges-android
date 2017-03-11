package com.pendulab.theExchange.activity;

import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.CreditPackAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.inapppurchase.IabHelper;
import com.pendulab.theExchange.inapppurchase.IabResult;
import com.pendulab.theExchange.inapppurchase.Inventory;
import com.pendulab.theExchange.inapppurchase.Purchase;
import com.pendulab.theExchange.model.CreditPack;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;
import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 12/15/2015.
 */
public class GetCreditActivity extends BaseActivity {

  private TextView tvYourCredit;
  private ListView lvCreditPack;
  private List<CreditPack> arrCreditPack;
  private CreditPackAdapter adapter;
  private int selectedPosition;

  private static final int RC_GOOGLE_PLAY = 10001;
  private IabHelper mHelper;

  private static final int RC_PAYPAL = 10002;
  private static final String CONFIG_ENVIRONMENT = PaymentActivity.ENVIRONMENT_SANDBOX;
  private static final String CONFIG_CLIENT_ID = GlobalValue.PAYPAL_CLIENT_APP_ID;
  private static final String CONFIG_RECEIVER_EMAIL = GlobalValue.PAYPAL_RECEIVE_EMAIL_ID;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_get_more_credit);

    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.get_more_credits));

    initUI();
    initData();
    initControl();
    initInAppPurchase();

    getCreditPacks();
  }

  private void initUI() {
    tvYourCredit = (TextView) findViewById(R.id.tvYourCredit);
    lvCreditPack = (ListView) findViewById(R.id.lvCreditPack);
  }

  private void initData() {
    arrCreditPack = new ArrayList<>();

    adapter = new CreditPackAdapter(self, arrCreditPack);
    lvCreditPack.setAdapter(adapter);

    tvYourCredit.setText(getString(R.string.total_credit) + " " + myAccount.getTradeCredit());
  }

  private void initControl() {
    lvCreditPack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
        adapter.setSelectedPosition(position);
        adapter.notifyDataSetChanged();
        DialogUtility.showPurchaseOptionDialog(self, getString(R.string.chosen_credit_pack) + " " + arrCreditPack.get(position).getName() + " " + getString(R.string.with) + " $" + arrCreditPack.get(position).getPrice() + ". " + getString(R.string.choose_payment_method), new DialogUtility.PurChaseOptionsListener() {
          @Override
          public void onClickGooglePlay(Dialog dialog) {
            purchaseItem(arrCreditPack.get(position).getSkuID(), "");
            selectedPosition = position;
          }

          @Override
          public void onClickPaypal(Dialog dialog) {
            requestPaypalPayment(arrCreditPack.get(position).getPrice(), "Payment", "USD");
            selectedPosition = position;
          }
        });
      }
    });
  }

  @Override
  protected void onDestroy() {
    if (mHelper != null) {
      mHelper.dispose();
      mHelper = null;
    }
    super.onDestroy();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // TODO Auto-generated method stub
    if (requestCode == RC_GOOGLE_PLAY) {
      if (mHelper == null)
        return;

      // Pass on the activity result to the helper for handling
      if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
        super.onActivityResult(requestCode, resultCode, data);
      } else {
        Log.d(TAG, "onActivityResult handled by IABUtil.");
      }
    }
    if (requestCode == RC_PAYPAL) {
      if (resultCode == Activity.RESULT_OK) {
        PaymentConfirmation confirm = data
            .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
        if (confirm != null) {
          try {
            Log.i("paymentExample", "Payment OK. Response :"
                + confirm.toJSONObject().toString(4));
//                        Toast toast = Toast.makeText(self, "Payment successfull",
//                                Toast.LENGTH_LONG);
//                        toast.setGravity(
//                                Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
//                        toast.show();
            updateCreditToServer(arrCreditPack.get(selectedPosition).getName());
//                        submitOrder();
          } catch (JSONException e) {
            Log.e("paymentExample",
                "an extremely unlikely failure occurred: ", e);
          }
        }
      } else if (resultCode == Activity.RESULT_CANCELED) {
        Log.i("paymentExample", "The user canceled.");
      } else if (resultCode == PaymentActivity.RESULT_PAYMENT_INVALID) {
        Log.i("paymentExample",
            "An invalid payment was submitted. Please see the docs.");
      }
    }
  }

  private void getCreditPacks() {
    AsyncHttpGet asyncHttpGet = new AsyncHttpGet(self, myAccount.getToken(), new AsyncHttpResponse() {
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
            List<CreditPack> list = CommonParser.parseListCreditPackFromJson(json);
            arrCreditPack.addAll(list);
            adapter.notifyDataSetChanged();
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, null);
    asyncHttpGet.execute(WebServiceConfig.URL_GET_CREDIT_PACK);
  }

  /*----- Methods for in-app billing -----*/
  private void initInAppPurchase() {
    // Create the helper, passing it our context and the public key to
    // verify signatures with
    Log.d(TAG, "Creating IAB helper.");
    mHelper = new IabHelper(this, GlobalValue.BASE64ENCODEDPUBLICKEY);

    // enable debug logging (for a production application, you should set
    // this to false).
    mHelper.enableDebugLogging(true);

    // Start setup. This is asynchronous and the specified listener
    // will be called once setup completes.
    Log.d(TAG, "Starting setup.");
    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
      public void onIabSetupFinished(IabResult result) {
        Log.d(TAG, "Setup finished.");

        if (!result.isSuccess()) {
          // Oh noes, there was a problem.
          Log.e(TAG, "Problem setting up in-app billing: " + result);
          return;
        }

        // Have we been disposed of in the meantime? If so, quit.
        if (mHelper == null)
          return;

        // IAB is fully set up. Now, let's get an inventory of stuff we
        // own.
        Log.d(TAG, "Setup successful. Querying inventory.");
        mHelper.queryInventoryAsync(mGotInventoryListener);
      }
    });
  }

  private void purchaseItem(String skuId, String payload) {
        /*
         * TODO: for security, generate your payload here for verification. See
		 * the comments on verifyDeveloperPayload() for more info. Since this is
		 * a SAMPLE, we just use an empty string, but on a production app you
		 * should carefully generate this.
		 */
    // mHelper.launchPurchaseFlow(this, skuId, IabHelper.ITEM_TYPE_INAPP,
    // RC_GOOGLE_PLAY, mPurchaseFinishedListener, payload);
    mHelper.launchPurchaseFlow(this, skuId, RC_GOOGLE_PLAY,
        mPurchaseFinishedListener, payload);
  }

  // Listener that's called when we finish querying the items and
  // subscriptions we own
  IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
    public void onQueryInventoryFinished(IabResult result,
                                         Inventory inventory) {
      Log.d(TAG, "Query inventory finished.");

      // Have we been disposed of in the meantime? If so, quit.
      // TODO: IAP: need to change after receving google account of
      // Treasure Trash
      /* temp */
//            if (inventory.hasPurchase(Item.KEY_ADD_3_PHOTOS)) {
//                Log.e(TAG, "Query inventory finished. " + result);
//                mHelper.consumeAsync(
//                        inventory.getPurchase(Item.KEY_ADD_3_PHOTOS), null);
//                return;
//            }
			/* end temp */
      if (mHelper == null)
        return;

      // Is it a failure?
      if (result.isFailure()) {
        Log.e(TAG, "Failed to query inventory: " + result);
        return;
      }

      Log.d(TAG, "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */

      // Do we have the premium upgrade?
//            Purchase addMore = inventory.getPurchase(Item.KEY_ADD_3_PHOTOS);
//            if (addMore != null && verifyDeveloperPayload(addMore)) {
//                Log.d(TAG, "We have gas. Consuming it.");
//                mHelper.consumeAsync(addMore, mConsumeFinishedListener);
//                return;
//            }

      Log.d(TAG, "Initial inventory query finished; enabling main UI.");
    }
  };

  /**
   * Verifies the developer payload of a purchase.
   */
  boolean verifyDeveloperPayload(Purchase p) {
    String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 *
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 *
		 * So a good developer payload has these characteristics:
		 *
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 *
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 *
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */

    return true;
  }

  // Callback for when a purchase is finished
  IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
      Log.d(TAG, "Purchase finished: " + result + ", purchase: "
          + purchase);

      // if we were disposed of in the meantime, quit.
      // TODO: IAP: need to change after receiving google account of
      // Treasure Trash
			/* temp */

      if (purchase != null) {
        Log.e(TAG, "Query inventory finished. " + result);
        mHelper.consumeAsync(purchase, mConsumeFinishedListener);
        return;
      }

			/* end temp */
      if (mHelper == null)
        return;

      if (result.isFailure()) {
        Log.e(TAG, "Error purchasing: " + result);
        return;
      }
      if (!verifyDeveloperPayload(purchase)) {
        Log.e(TAG,
            "Error purchasing. Authenticity verification failed.");
        return;
      }

      Log.d(TAG, "Purchase successful.");

      // Consuming item
//            if (purchase.getSku().equals(Item.KEY_ADD_3_PHOTOS)) {
//                // bought 1/4 tank of gas. So consume it.
//                Log.d(TAG, "Purchase is gas. Starting gas consumption.");
//                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
//            }
    }
  };

  // Called when consumption is complete
  IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
    public void onConsumeFinished(Purchase purchase, IabResult result) {
      Log.d(TAG, "Consumption finished. Purchase: " + purchase
          + ", result: " + result);

      // if we were disposed of in the meantime, quit.
      if (mHelper == null)
        return;

      // We know this is the "gas" sku because it's the only one we
      // consume,
      // so we don't check which sku was consumed. If you have more than
      // one
      // sku, you probably should check...
      if (result.isSuccess()) {
        // successfully consumed, so we apply the effects of the item in
        // our
        // game world's logic, which in our case means filling the gas
        // tank a bit
        Log.d(TAG, "Consumption successful. Provisioning.");
//                if (purchase.getSku().equals(Item.KEY_ADD_3_PHOTOS)) {
//                    sendPurchaseToServer(Item.PURCHASE_ADD_3_PHOTO,
//                            "DemoPurchase", ""
//                                    + Calendar.getInstance().getTimeInMillis());
//                    Log.e("AddAListingActivity", "Add more");
//                }
        updateCreditToServer(arrCreditPack.get(selectedPosition).getName());
      } else {
        Log.e(TAG, "Error while consuming: " + result);
      }
      Log.d(TAG, "End consumption flow.");
    }
  };

  private void requestPaypalPayment(double value, String content,
                                    String currency) {

    PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(value),
        currency, content);

    Intent intent = new Intent(this, PaymentActivity.class);

    intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT,
        CONFIG_ENVIRONMENT);
    intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
    intent.putExtra(PaymentActivity.EXTRA_RECEIVER_EMAIL,
        CONFIG_RECEIVER_EMAIL);

    // It's important to repeat the clientId here so that the SDK has it if
    // Android restarts your
    // app midway through the payment UI flow.
    intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
    intent.putExtra(PaymentActivity.EXTRA_PAYER_ID, myAccount.getUserID());
    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

    startActivityForResult(intent, RC_PAYPAL);
  }


  private void updateCreditToServer(final String credit) {

    final int intCredit = Integer.parseInt(credit);

    List<NameValuePair> params = ParameterFactory.createBuyCreditParam(intCredit);

    AsyncHttpPost asyncHttpPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
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
            DialogUtility.showMessageDialog(self, getString(R.string.message_buy_credit_successfully), new DialogUtility.DialogListener() {
              @Override
              public void onClose(Dialog dialog) {
                myAccount.setTradeCredit(myAccount.getTradeCredit() + intCredit);
                preferences.saveUserInfo(myAccount);
                tvYourCredit.setText(getString(R.string.total_credit) + " " + myAccount.getTradeCredit());
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
    asyncHttpPost.execute(WebServiceConfig.URL_BUY_CREDIT);
  }

}
