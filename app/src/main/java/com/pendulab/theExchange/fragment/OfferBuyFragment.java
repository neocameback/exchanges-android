package com.pendulab.theExchange.fragment;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.activity.ChatActivity;
import com.pendulab.theExchange.activity.VerifyTransactionActivity;
import com.pendulab.theExchange.adapter.OfferBuyAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.base.BaseFragment;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Offer;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.AppUtil;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 11/20/2015.
 */
public class OfferBuyFragment extends BaseFragment implements View.OnClickListener {

  public static OfferBuyFragment instance;
  public static boolean firstTime;

  public static OfferBuyFragment getInstance() {
    if (instance == null) {
      instance = new OfferBuyFragment();
    }
    firstTime = true;
    return instance;
  }

  private SwipeRefreshLayout swipeRefreshLayout;
  private ListView lvOffer;
  private List<Offer> arrOffers;
  private OfferBuyAdapter adapter;
  private TextView tvNoResult;

  private int RC_VERIFY_TRANSACTION = 1006;

  @Override
  public int getFragmentResource() {
    return R.layout.fragment_offer_buy;
  }

  @Override
  public void initView(View view) {
    setTitlePage(instance.getString(R.string.all_offers));
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setColorSchemeResources(R.color.app_primary_color);
    lvOffer = (ListView) view.findViewById(R.id.lvOffer);
    tvNoResult = (TextView) view.findViewById(R.id.tvNoResult);

  }

  @Override
  public void initData(View view) {
    if (adapter == null) {
      arrOffers = new ArrayList<>();
    }

    adapter = new OfferBuyAdapter(self, arrOffers, new OfferBuyAdapter.IOfferBuyAdapter() {
      @Override
      public void onClickAction(int position) {

      }

      @Override
      public void onClickUser(int position) {
        getBaseActivity().gotoItemDetailsActivity(arrOffers.get(position).getItemID());
      }

      @Override
      public void onClickTradeItem(int position, int subPosition) {
        getBaseActivity().gotoItemDetailsActivity(arrOffers.get(position).getArrTradeItems().get(subPosition).getId());
      }

      @Override
      public void onClickView(final int position) {
        DialogUtility.showOptionDialog(self, getString(R.string.request_verify_message), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
          @Override
          public void onPositive(Dialog dialog) {
            requestVerify(position);
          }

          @Override
          public void onNegative(Dialog dialog) {

          }
        });
        return;
      }

      @Override
      public void onClickChat(Offer offer) {
        getConversation(offer);
      }
    });

    lvOffer.setAdapter(adapter);
  }


  @Override
  public void initControl(View view) {

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        self.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            arrOffers.clear();
            adapter.notifyDataSetChanged();
            getOfferBuying();
          }
        });
      }
    });

    lvOffer.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView absListView, int i) {

      }

      @Override
      public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        AppUtil.supportSwipeRefreshLayout(lvOffer, i, i1, i2, swipeRefreshLayout);
      }
    });
  }

  @Override
  public void onClick(View view) {

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == RC_VERIFY_TRANSACTION) {
        arrOffers.clear();
        adapter.notifyDataSetChanged();
        getOfferBuying();
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (firstTime) {
      arrOffers.clear();
      firstTime = false;
      getOfferBuying();
    } else {
      if (arrOffers.size() == 0) {
        tvNoResult.setVisibility(View.VISIBLE);
      } else {
        tvNoResult.setVisibility(View.GONE);
      }
    }
  }

  private void getOfferBuying() {
    AsyncHttpGet asyncPost = new AsyncHttpGet(self, getBaseActivity().myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        getBaseActivity().showRefreshLayout(swipeRefreshLayout);

      }

      @Override
      public void after(int statusCode, String response) {
        getBaseActivity().hideRefreshLayout(swipeRefreshLayout);
        getBaseActivity().parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<Offer> list = CommonParser.parseListOffersFromJson(self, json);
            arrOffers.addAll(list);
            adapter.notifyDataSetChanged();
            if (arrOffers.size() == 0) {
              tvNoResult.setVisibility(View.VISIBLE);
            } else {
              tvNoResult.setVisibility(View.GONE);
            }
          }

          @Override
          public void onRequestError(int message) {
            getBaseActivity()._onRequestError(message);
          }
        });


      }
    }, null);
    asyncPost.execute(WebServiceConfig.URL_GET_OFFER_BUY);
  }

  private void requestVerify(final int position) {
    List<NameValuePair> params = ParameterFactory.createRequestVerifyTransactionParam(arrOffers.get(position).getItemID());
    AsyncHttpPost asyncPost = new AsyncHttpPost(self, getBaseActivity().myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
        DialogUtility.closeProgressDialog();
        getBaseActivity().parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            String[] array = CommonParser.parseVerificationCodeFromJson(json);
            String code = array[0];
            String transactionID = array[1];

            Bundle bundle = new Bundle();
            bundle.putString(GlobalValue.KEY_ITEM_ID, arrOffers.get(position).getItemID());
            bundle.putString(GlobalValue.KEY_CODE, code);
            bundle.putString(GlobalValue.KEY_TRANSACTION_ID, transactionID);
            gotoActivityForResult(self, VerifyTransactionActivity.class, bundle, RC_VERIFY_TRANSACTION);
          }

          @Override
          public void onRequestError(int message) {
            getBaseActivity()._onRequestError(message);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_REQUEST_VERIFY_TRANSACTION);
  }

  private void getConversation(final Offer offer) {
    String itemID = offer.getItemID();
    if (itemID == null) {
      return;
    }

    List<NameValuePair> params = ParameterFactory.createMakeConversationParam(itemID);

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, getBaseActivity().myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
        DialogUtility.closeProgressDialog();
        getBaseActivity().parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            String conversationID = CommonParser.getConversationIDFromJson(json);
            gotoChatActivity(conversationID, offer);
          }

          @Override
          public void onRequestError(int message) {
            getBaseActivity()._onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_MAKE_CONVERSATION);
  }

  private void gotoChatActivity(String conversationID, Offer offer) {
    Bundle bundle = new Bundle();
    bundle.putString(GlobalValue.KEY_ITEM_ID, offer.getItemID());
    bundle.putString(GlobalValue.KEY_ITEM_NAME, offer.getItemTitle());
    bundle.putString(GlobalValue.KEY_ITEM_IMAGE, offer.getItemImage());
    bundle.putString(GlobalValue.KEY_USERNAME, offer.getOwnerName());
    bundle.putString(GlobalValue.KEY_USER_ID, offer.getOwnerID());
    bundle.putString(GlobalValue.KEY_CONVERSATION_ID, conversationID);

    gotoActivity(self, ChatActivity.class, bundle);
  }
}
