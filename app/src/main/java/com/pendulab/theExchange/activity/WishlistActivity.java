package com.pendulab.theExchange.activity;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.WishlistAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/29/2015.
 */
public class WishlistActivity extends BaseActivity {

  private SwipeRefreshLayout swipeRefreshLayout;
  private ListView lvItems;
  private List<Item> arrItems;
  private WishlistAdapter itemAdapter;
  private TextView tvNoResult;

  private String userID, username;

  private final int RC_ITEM_DETAILS = 1001;
  private final int RC_ADD_NEW = 1003;
  private final int RC_EDIT = 1004;

  private int contextPosition;
  private final int CONTEXT_MENU_REMOVE = 1;
  private final int CONTEXT_MENU_EDIT = 2;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_wishlist);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_USER) && bundle.containsKey(GlobalValue.KEY_USERNAME)) {
      userID = bundle.getString(GlobalValue.KEY_USER);
      username = bundle.getString(GlobalValue.KEY_USERNAME);
    }

    setActionBarTitle(userID.equalsIgnoreCase(myAccount.getUserID()) ? getString(R.string.my_wishlist) : getString(R.string.wish_list) + " " + getString(R.string.of) + " @" + username);

    initUI();
    initData();
    initControl();

  }

  private void initUI() {
    swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setColorSchemeResources(R.color.app_primary_color);
    lvItems = (ListView) findViewById(R.id.lvItems);
    tvNoResult = (TextView) findViewById(R.id.tvNoResult);

    registerForContextMenu(lvItems);
  }

  private void initData() {
    arrItems = new ArrayList<>();

    itemAdapter = new WishlistAdapter(self, arrItems, userID.equalsIgnoreCase(myAccount.getUserID()), new WishlistAdapter.IWishlistAdapter() {
      @Override
      public void onClickAction(int position) {
        contextPosition = position;
        openContextMenu(lvItems);
      }

      @Override
      public void onClickUser(int position) {

      }

      @Override
      public void onClickItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalValue.KEY_ITEM, arrItems.get(position).getId());
        bundle.putInt(GlobalValue.KEY_POSITION, position);
        gotoActivityForResult(self, ItemDetailsActivity.class, bundle, RC_ITEM_DETAILS);
      }
    });

    lvItems.setAdapter(itemAdapter);

    getWishlist();
  }

  private void initControl() {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        self.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            arrItems.clear();
            getWishlist();
          }
        });
      }
    });

    lvItems.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {

      }

      @Override
      public void onScroll(AbsListView absListView, int firstVisible, int lastVisible, int totalItemCount) {
        boolean enable = false;

        if (lvItems != null && lvItems.getChildCount() > 0) {
          // check if the first item of the list is visible
          boolean firstItemVisible = (firstVisible == 0);
          // check if the top of the first item is visible
          boolean topOfFirstItemVisible = lvItems.getChildAt(0).getTop() == 0;
//                    Log.i("GVITEMS", lvItems.getChildAt(0).getTop() + "");
          // enabling or disabling the refresh layout
          enable = firstItemVisible && topOfFirstItemVisible;
        }

        swipeRefreshLayout.setEnabled(enable);

      }
    });

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (userID.equalsIgnoreCase(myAccount.getUserID())) {
      getMenuInflater().inflate(R.menu.menu_wishlist, menu);
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_new) {
      Bundle bundle = new Bundle();
      bundle.putInt(GlobalValue.KEY_WISHLIST, WishListManualActivity.MODE_NEW);
      gotoActivityForResult(self, WishListManualActivity.class, bundle, RC_ADD_NEW);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuIsnfo) {
    menu.setHeaderTitle(getString(R.string.options));
    menu.add(Menu.NONE, CONTEXT_MENU_REMOVE, Menu.NONE, getString(R.string.remove));
    if (arrItems.get(contextPosition).getOwnerId() == null) {
      menu.add(Menu.NONE, CONTEXT_MENU_EDIT, Menu.NONE, getString(R.string.edit));
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case CONTEXT_MENU_REMOVE:
        if (arrItems.get(contextPosition).getOwnerId() != null) {
          toggleWishList(contextPosition);
        } else {
          deleteWishListManual(contextPosition);
        }
        break;
      case CONTEXT_MENU_EDIT:
        Bundle bundle = new Bundle();
        bundle.putParcelable(GlobalValue.KEY_ITEM, arrItems.get(contextPosition));
        bundle.putInt(GlobalValue.KEY_WISHLIST, WishListManualActivity.MODE_EDIT);
        bundle.putInt(GlobalValue.KEY_POSITION, contextPosition);
        gotoActivityForResult(self, WishListManualActivity.class, bundle, RC_EDIT);
        break;
    }
    return super.onContextItemSelected(item);
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      if (requestCode == RC_ITEM_DETAILS) {

        if (data.getExtras().containsKey(GlobalValue.KEY_ITEM)) {
          Item resultItem = data.getParcelableExtra(GlobalValue.KEY_ITEM);
          int currentPosition = data.getIntExtra(GlobalValue.KEY_POSITION, 0);
          arrItems.get(currentPosition).setLiked(resultItem.isLiked());
          arrItems.get(currentPosition).setLikeCount(resultItem.getLikeCount());
          itemAdapter.notifyDataSetChanged();
        } else if (data.getExtras().containsKey(GlobalValue.KEY_DELETE_ITEM)) {
          int currentPosition = data.getIntExtra(GlobalValue.KEY_POSITION, 0);
          arrItems.remove(currentPosition);
          itemAdapter.notifyDataSetChanged();
        }
      }

      if (requestCode == RC_ADD_NEW) {
        Item resultItem = data.getParcelableExtra(GlobalValue.KEY_ITEM);
        arrItems.add(0, resultItem);
        itemAdapter.notifyDataSetChanged();
      }

      if (requestCode == RC_EDIT) {
        Item resultItem = data.getParcelableExtra(GlobalValue.KEY_ITEM);
        int editPosition = data.getIntExtra(GlobalValue.KEY_POSITION, 0);
        arrItems.get(editPosition).setTitle(resultItem.getTitle());
        itemAdapter.notifyDataSetChanged();

      }
    }
  }

  private void getWishlist() {
    String getURL = ParameterFactory.createGetWishlistParam(userID);
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
            List<Item> list = CommonParser.parseListWishlistFromJson(self, json);
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

  private void toggleWishList(final int position) {

    final Item item = arrItems.get(position);


    List<NameValuePair> param = ParameterFactory.createToggleWishListParams(item.getId());

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
            arrItems.remove(position);
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

  private void deleteWishListManual(final int position) {

    final Item item = arrItems.get(position);


    List<NameValuePair> param = ParameterFactory.createDeleteWishlistManualParams(item.getId());

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
            arrItems.remove(position);
            itemAdapter.notifyDataSetChanged();
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, param);
    asyncPost.execute(WebServiceConfig.URL_DELETE_WISHLIST);

  }
}
