package com.pendulab.theExchange.activity;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.InboxAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.database.DatabaseHelper;
import com.pendulab.theExchange.model.Message;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.AppUtil;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 11/11/2015.
 */
public class ArchiveActivity extends BaseActivity {

  private SwipeMenuListView lvArchive;
  private SwipeRefreshLayout swipeRefreshLayout;
  private List<Message> arrMessages;
  private InboxAdapter adapter;

  private boolean touchStarted;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inbox);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.archive));

    initUI();
    initData();
    initControl();
  }

  private void initUI() {
    swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setColorSchemeResources(R.color.app_primary_color);
    lvArchive = (SwipeMenuListView) findViewById(R.id.lvInbox);
  }

  private void initData() {

    arrMessages = new ArrayList<>();
    adapter = new InboxAdapter(self, arrMessages, new InboxAdapter.IInboxAdapter() {
      @Override
      public void onClickUser(Message message) {
        gotoUserProfileActivity(message.getUserId(), message.getUsername());
      }

      @Override
      public void onClickItem(Message message) {
        gotoItemDetailsActivity(message.getItemID());
      }
    });

    lvArchive.setAdapter(adapter);

    SwipeMenuCreator creator = new SwipeMenuCreator() {

      @Override
      public void create(SwipeMenu menu) {

        SwipeMenuItem inboxItem = new SwipeMenuItem(
            self);
        inboxItem.setBackground(new ColorDrawable(getResources().getColor(R.color.green)));
        inboxItem.setWidth(AppUtil.getDeviceScreenWidth(self) / 4);
        inboxItem.setTitle(getString(R.string.inbox));
        inboxItem.setTitleSize((int) getResources().getDimension(R.dimen.text_size_small));
        inboxItem.setTitleColor(Color.WHITE);

        SwipeMenuItem deleteItem = new SwipeMenuItem(
            self);
        deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red)));
        deleteItem.setWidth(AppUtil.getDeviceScreenWidth(self) / 4);
        deleteItem.setTitle(getString(R.string.delete));
        deleteItem.setTitleSize((int) getResources().getDimension(R.dimen.text_size_small));
        deleteItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(inboxItem);
        menu.addMenuItem(deleteItem);
      }
    };
    lvArchive.setMenuCreator(creator);

    getInbox();
  }

  private void initControl() {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        self.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            arrMessages.clear();
            getInbox();
          }
        });
      }
    });

    lvArchive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalValue.KEY_ITEM_ID, arrMessages.get(position).getItemID());
        bundle.putString(GlobalValue.KEY_ITEM_NAME, arrMessages.get(position).getItemTitle());
        bundle.putString(GlobalValue.KEY_ITEM_IMAGE, arrMessages.get(position).getItemImage());
        bundle.putString(GlobalValue.KEY_USERNAME, arrMessages.get(position).getUsername());
        bundle.putString(GlobalValue.KEY_USER_ID, arrMessages.get(position).getUserId());
        bundle.putString(GlobalValue.KEY_CONVERSATION_ID, arrMessages.get(position).getConversationID());

        gotoActivity(self, ChatActivity.class, bundle);
      }
    });

    lvArchive.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView absListView, int i) {

      }

      @Override
      public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        AppUtil.supportSwipeRefreshLayout(lvArchive, i, i1, i2, swipeRefreshLayout);
      }
    });

    lvArchive.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_MOVE:
            swipeRefreshLayout.setEnabled(false);
            view.getParent().requestDisallowInterceptTouchEvent(true);
            touchStarted = false;
            break;
          case MotionEvent.ACTION_UP:
            swipeRefreshLayout.setEnabled(true);
            view.getParent().requestDisallowInterceptTouchEvent(false);
            if (touchStarted) {

            }
            break;
          case MotionEvent.ACTION_CANCEL:
            swipeRefreshLayout.setEnabled(true);
            view.getParent().requestDisallowInterceptTouchEvent(false);
            break;
          case MotionEvent.ACTION_DOWN:
            swipeRefreshLayout.setEnabled(true);
            view.getParent().requestDisallowInterceptTouchEvent(false);
            touchStarted = true;
            break;
        }
        return false;
      }
    });

    lvArchive.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
        switch (index) {
          case 0:
            DialogUtility.showOptionDialog(self, getString(R.string.move_to_inbox_warning), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
              @Override
              public void onPositive(Dialog dialog) {
                moveToInbox(arrMessages.get(position));
              }

              @Override
              public void onNegative(Dialog dialog) {

              }
            });
            break;
          case 1:
            // delete
            DialogUtility.showOptionDialog(self, getString(R.string.delete_warning), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
              @Override
              public void onPositive(Dialog dialog) {
                deleteConversation(arrMessages.get(position));
              }

              @Override
              public void onNegative(Dialog dialog) {

              }
            });
            break;
        }
        // false : close the menu; true : not close the menu
        return false;
      }
    });
  }

  private void getInbox() {
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
            List<Message> list = CommonParser.parseListInboxFromJson(json, myAccount);
            arrMessages.addAll(list);
            adapter.notifyDataSetChanged();
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, null);
    asyncGet.execute(WebServiceConfig.URL_MOVE_TO_ARCHIVE);
  }

  private void moveToInbox(final Message message) {
    List<NameValuePair> params = ParameterFactory.createMoveToArchiveParam(message.getConversationID());

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

            arrMessages.remove(message);
            adapter.notifyDataSetChanged();
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_MOVE_TO_ARCHIVE);
  }

  private void deleteConversation(final Message message) {
    List<NameValuePair> params = ParameterFactory.createMoveToArchiveParam(message.getConversationID());

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
            arrMessages.remove(message);
            adapter.notifyDataSetChanged();
            DatabaseHelper.deleteMessageByConversation(message.getConversationID());
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_DELETE_CONVERSATION);

  }

}
