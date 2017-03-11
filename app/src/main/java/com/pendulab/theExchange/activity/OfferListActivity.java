package com.pendulab.theExchange.activity;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.OfferAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.model.Offer;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 11/16/2015.
 */
public class OfferListActivity extends BaseActivity {

  private SwipeRefreshLayout swipeRefreshLayout;
  private ListView lvOffer;
  private List<Offer> arrOffers;
  private OfferAdapter adapter;
  private TextView tvNoResult;

  private ImageView ivItem, ivUser;
  private TextView tvItem, tvUser, tvCredit;

  private Item currentItem;
  private int RC_VERIFY_TRANSACTION = 1006;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_offer_list);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.list_of_offers));

    Bundle bundle = getIntent().getExtras();
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_ITEM)) {
      currentItem = bundle.getParcelable(GlobalValue.KEY_ITEM);
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

    Glide.with(self).load(R.drawable.ic_credit).into((ImageView) findViewById(R.id.ivCredits));

    swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setColorSchemeResources(R.color.app_primary_color);
    lvOffer = (ListView) findViewById(R.id.lvOffer);
    tvNoResult = (TextView) findViewById(R.id.tvNoResult);

    registerForContextMenu(lvOffer);

  }

  private void initData() {

    fillLayout();

    arrOffers = new ArrayList<>();

    adapter = new OfferAdapter(self, arrOffers, new OfferAdapter.IOfferAdapter() {
      @Override
      public void onClickAction(final int position) {
        DialogUtility.showOptionDialog(self, getString(R.string.accept_or_reject), getString(R.string.reject), getString(R.string.accept), true, new DialogUtility.DialogOptionListener() {
          @Override
          public void onPositive(Dialog dialog) {
            DialogUtility.showOptionDialog(self, getString(R.string.reject_offer_question), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
              @Override
              public void onPositive(Dialog dialog) {
                refuseOffer(arrOffers.get(position));
              }

              @Override
              public void onNegative(Dialog dialog) {

              }
            });
          }

          @Override
          public void onNegative(Dialog dialog) {
            DialogUtility.showOptionDialog(self, getString(R.string.accept_offer_question), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
              @Override
              public void onPositive(Dialog dialog) {
                acceptOffer(arrOffers.get(position));
              }

              @Override
              public void onNegative(Dialog dialog) {

              }
            });
          }
        });
      }

      @Override
      public void onClickAccept(final int position) {
        DialogUtility.showOptionDialog(self, getString(R.string.accept_offer_question), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
          @Override
          public void onPositive(Dialog dialog) {
            acceptOffer(arrOffers.get(position));
          }

          @Override
          public void onNegative(Dialog dialog) {

          }
        });
      }

      @Override
      public void onClickReject(final int position) {
        DialogUtility.showOptionDialog(self, getString(R.string.reject_offer_question), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
          @Override
          public void onPositive(Dialog dialog) {
            refuseOffer(arrOffers.get(position));
          }

          @Override
          public void onNegative(Dialog dialog) {

          }
        });
      }

      @Override
      public void onClickUser(int position) {
        gotoUserProfileActivity(arrOffers.get(position).getUserID(), arrOffers.get(position).getUsername());
      }

      @Override
      public void onClickTradeItem(int position, int subPosition) {
        gotoItemDetailsActivity(arrOffers.get(position).getArrTradeItems().get(subPosition).getId());
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
      }

      @Override
      public void onClickChat(Offer offer) {
        getConversation(offer);
      }
    });

    lvOffer.setAdapter(adapter);

    getListOffer();
  }

  private void initControl() {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        self.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            arrOffers.clear();
            adapter.notifyDataSetChanged();
            getListOffer();
          }
        });
      }
    });

    lvOffer.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {

      }

      @Override
      public void onScroll(AbsListView absListView, int firstVisible, int lastVisible, int totalItemCount) {
        boolean enable = false;

        if (lvOffer != null && lvOffer.getChildCount() > 0) {
          // check if the first item of the list is visible
          boolean firstItemVisible = (firstVisible == 0);
          // check if the top of the first item is visible
          boolean topOfFirstItemVisible = lvOffer.getChildAt(0).getTop() == 0;
//                    Log.i("GVITEMS", lvOffer.getChildAt(0).getTop() + "");
          // enabling or disabling the refresh layout
          enable = firstItemVisible && topOfFirstItemVisible;
        }

        swipeRefreshLayout.setEnabled(enable);

      }
    });

  }

  private void fillLayout() {
    if (currentItem != null && currentItem.getTitle() != null) {
      Glide.with(self).load(currentItem.getImage()).error(R.drawable.image_not_available).placeholder(R.drawable.image_placeholder).into(ivItem);
      tvItem.setText(currentItem.getTitle());
      tvCredit.setText(currentItem.getPrice() + "");
      Glide.with(self).load(currentItem.getOwnerImage()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(ivUser);
      tvUser.setText(currentItem.getOwnerUsername());
    } else {
      getItemDetails();
    }
  }


  @Override
  public void onBackPressed() {
    Intent intent = new Intent();
    intent.putExtra(GlobalValue.KEY_ITEM, currentItem);
    setResult(RESULT_OK, intent);
    super.onBackPressed();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == RC_VERIFY_TRANSACTION) {
        arrOffers.clear();
        adapter.notifyDataSetChanged();
        getListOffer();
      }
    }
  }

  private void getListOffer() {
    List<NameValuePair> params = ParameterFactory.createGetListOffersParam(currentItem.getId());
    AsyncHttpPost asyncGet = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        showRefreshLayout(swipeRefreshLayout);
      }

      @Override
      public void after(int statusCode, String response) {
        hideRefreshLayout(swipeRefreshLayout);
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<Offer> list = CommonParser.parseListOffersFromJson(self, json);
            arrOffers.addAll(list);
            adapter.notifyDataSetChanged();
            if (arrOffers.size() > 0) {
              tvNoResult.setVisibility(View.GONE);
            } else {
              tvNoResult.setVisibility(View.VISIBLE);
            }
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);

    asyncGet.execute(WebServiceConfig.URL_GET_OFFER_LIST);
  }

  private void acceptOffer(final Offer offer) {
    List<NameValuePair> params = ParameterFactory.createAccepRefuseOfferParam(offer.getId());

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
            offer.setStatus(Offer.STATUS_ACCEPTED);
            adapter.notifyDataSetChanged();

            currentItem.setStatus(Item.STATUS_OFFER_ACCEPTED);
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });

      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_ACCEPT_OFFER);
  }

  private void refuseOffer(final Offer offer) {
    List<NameValuePair> params = ParameterFactory.createAccepRefuseOfferParam(offer.getId());

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
            offer.setStatus(Offer.STATUS_REJECT);
            adapter.notifyDataSetChanged();
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_REJECT_OFFER);
  }

  private void getItemDetails() {
    List<NameValuePair> params = ParameterFactory.createGetItemDetailsParams(currentItem.getId(), 0, 0);

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, preferences.getUserInfo().getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        showRefreshLayout(swipeRefreshLayout);
      }

      @Override
      public void after(int statusCode, String response) {
        hideRefreshLayout(swipeRefreshLayout);

        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            currentItem = CommonParser.parseSingleItemFromJson(self, json);
            if (currentItem != null) {
              fillLayout();
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

  private void requestVerify(final int position) {
    List<NameValuePair> params = ParameterFactory.createRequestVerifyTransactionParam(arrOffers.get(position).getItemID());
    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
        DialogUtility.closeProgressDialog();
        parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
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
            _onRequestError(message);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_REQUEST_VERIFY_TRANSACTION);
  }

  private void getConversation(Offer offer) {
    String itemID = offer.getItemID();
    if (itemID == null) {
      return;
    }

    List<NameValuePair> params = ParameterFactory.createMakeConversationParam(itemID);

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
            String conversationID = CommonParser.getConversationIDFromJson(json);
            gotoChatActivity(conversationID);
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_MAKE_CONVERSATION);
  }

  private void gotoChatActivity(String conversationID) {
    Bundle bundle = new Bundle();
    bundle.putString(GlobalValue.KEY_ITEM_ID, currentItem.getId());
    bundle.putString(GlobalValue.KEY_ITEM_NAME, currentItem.getTitle());
    bundle.putString(GlobalValue.KEY_ITEM_IMAGE, currentItem.getImage());
    bundle.putString(GlobalValue.KEY_USERNAME, currentItem.getOwnerUsername());
    bundle.putString(GlobalValue.KEY_USER_ID, currentItem.getOwnerId());
    bundle.putString(GlobalValue.KEY_CONVERSATION_ID, conversationID);

    gotoActivity(self, ChatActivity.class, bundle);
  }
}
