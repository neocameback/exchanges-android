package com.pendulab.theExchange.activity;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.ItemAdapter;
import com.pendulab.theExchange.base.BaseShareActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.ParameterFactory;
import com.pendulab.theExchange.widget.HeaderGridView;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/29/2015.
 */
public class InventoryActivity extends BaseShareActivity {

  private SwipeRefreshLayout swipeRefreshLayout;
  private HeaderGridView gvItems;
  private List<Item> arrItems;
  private ItemAdapter itemAdapter;
  private TextView tvNoResult;

  private String userID, username;

  private final int RC_ITEM_DETAILS = 1001;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inventory);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_USER) && bundle.containsKey(GlobalValue.KEY_USERNAME)) {
      userID = bundle.getString(GlobalValue.KEY_USER);
      username = bundle.getString(GlobalValue.KEY_USERNAME);
    }

    setActionBarTitle(userID.equalsIgnoreCase(myAccount.getUserID()) ? getString(R.string.my_inventory) : getString(R.string.inventory) + " " + getString(R.string.of) + " @" + username);

    initUI();
    initData();
    initControl();

  }

  private void initUI() {
    swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setColorSchemeResources(R.color.app_primary_color);
    gvItems = (HeaderGridView) findViewById(R.id.gvItems);
    tvNoResult = (TextView) findViewById(R.id.tvNoResult);

  }

  private void initData() {
    arrItems = new ArrayList<>();
    itemAdapter = new ItemAdapter(self, arrItems, new ItemAdapter.IItemAdapter() {
      @Override
      public void onClickTransparentView(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalValue.KEY_ITEM, arrItems.get(position).getId());
        bundle.putInt(GlobalValue.KEY_POSITION, position);
        gotoActivityForResult(self, ItemDetailsActivity.class, bundle, RC_ITEM_DETAILS);
      }

      @Override
      public void onClickLike(int position) {
        toggleWishList(position);
      }

      @Override
      public void onClickOptions(int position) {
        onShareLink(gvItems, arrItems.get(position));
      }
    });

    gvItems.setAdapter(itemAdapter);

    getInventory();
  }

  private void initControl() {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        self.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            arrItems.clear();
            getInventory();
          }
        });
      }
    });

    gvItems.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {

      }

      @Override
      public void onScroll(AbsListView absListView, int firstVisible, int lastVisible, int totalItemCount) {
        boolean enable = false;

        if (gvItems != null && gvItems.getChildCount() > 0) {
          // check if the first item of the list is visible
          boolean firstItemVisible = (firstVisible == 0);
          // check if the top of the first item is visible
          boolean topOfFirstItemVisible = gvItems.getChildAt(0).getTop() == 0;
          Log.i("GVITEMS", gvItems.getChildAt(0).getTop() + "");
          // enabling or disabling the refresh layout
          enable = firstItemVisible && topOfFirstItemVisible;
        }

        swipeRefreshLayout.setEnabled(enable);

      }
    });

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      if (requestCode == RC_ITEM_DETAILS) {

        if (data.getExtras().containsKey(GlobalValue.KEY_ITEM)) {
          Item resultItem = data.getParcelableExtra(GlobalValue.KEY_ITEM);
          int currentPosition = data.getIntExtra(GlobalValue.KEY_POSITION, 0);
          arrItems.get(currentPosition).setTitle(resultItem.getTitle());
          arrItems.get(currentPosition).setLiked(resultItem.isLiked());
          arrItems.get(currentPosition).setLikeCount(resultItem.getLikeCount());
          arrItems.get(currentPosition).setImage(resultItem.getImage());
          arrItems.get(currentPosition).setPrice(resultItem.getPrice());
          itemAdapter.notifyDataSetChanged();
        } else if (data.getExtras().containsKey(GlobalValue.KEY_DELETE_ITEM)) {
          int currentPosition = data.getIntExtra(GlobalValue.KEY_POSITION, 0);
          arrItems.remove(currentPosition);
          itemAdapter.notifyDataSetChanged();
        }
      }
    }
  }

  private void getInventory() {
    String getURL = ParameterFactory.createGetInventoryParam(userID);
    AsyncHttpGet asyncGet = new AsyncHttpGet(self, myAccount.getToken(), new AsyncHttpResponse() {
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
            List<Item> list = CommonParser.parseListItemsFromJson(self, json);
            arrItems.addAll(list);
            itemAdapter.notifyDataSetChanged();
            if (arrItems.size() > 0) {
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
    }, null);

    asyncGet.execute(getURL);
  }

  private void toggleWishList(int position) {

    final Item item = arrItems.get(position);


    List<NameValuePair> param = ParameterFactory.createToggleWishListParams(item.getId());

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, preferences.getUserInfo().getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
//                DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
//                DialogUtility.closeProgressDialog();
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            if (item.isLiked()) {
              item.setLiked(false);
              item.setLikeCount(item.getLikeCount() - 1);
            } else {
              item.setLiked(true);
              item.setLikeCount(item.getLikeCount() + 1);
            }
            itemAdapter.notifyDataSetChanged();
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, param);
    asyncPost.execute(WebServiceConfig.URL_TOGGLE_WISH_LIST);


  }

}
