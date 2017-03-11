package com.pendulab.theExchange.activity;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.ChatAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.database.DatabaseHelper;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.model.Message;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 11/6/2015.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener {

  private RecyclerView rvMessage;
  private List<Message> arrMessages;
  private ChatAdapter adapter;
  public LinearLayoutManager layoutManager;

  private ImageView ivItem;
  private TextView tvItem;

  private String itemID, itemImage, itemName, username, conversationID;
  public static String userID;
  public static boolean isActivityVisible;

  private EditText etMessage;
  private ImageButton btnSend;

  private Account currentUser;

  private IntentFilter intentFilter;

  private final BroadcastReceiver mHandleMessageReceive = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equalsIgnoreCase(GlobalValue.KEY_FILTER_NEW_MESSAGE)) {
        Message newMessage = intent.getParcelableExtra(GlobalValue.KEY_MESSAGE);
        if (newMessage.getUserSend().equalsIgnoreCase(currentUser.getUserID())) {
          arrMessages.add(newMessage);
          notifyDataset(true);
        }
      }
    }
  };


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_chat);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      itemID = bundle.getString(GlobalValue.KEY_ITEM_ID);
      itemName = bundle.getString(GlobalValue.KEY_ITEM_NAME);
      itemImage = bundle.getString(GlobalValue.KEY_ITEM_IMAGE);
      username = bundle.getString(GlobalValue.KEY_USERNAME);
      userID = bundle.getString(GlobalValue.KEY_USER_ID);
      conversationID = bundle.getString(GlobalValue.KEY_CONVERSATION_ID);
    }

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle("@" + username);

    initUI();
    initData();
    initControl();

    getUserInfo();
  }

  private void initUI() {
    rvMessage = (RecyclerView) findViewById(R.id.rvMessages);
    layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    layoutManager.setStackFromEnd(true);

    rvMessage.setLayoutManager(layoutManager);

    ivItem = (ImageView) findViewById(R.id.ivItem);
    tvItem = (TextView) findViewById(R.id.tvItem);

    etMessage = (EditText) findViewById(R.id.etMessage);
    btnSend = (ImageButton) findViewById(R.id.btnSend);

    Drawable drSend = getResources().getDrawable(android.R.drawable.ic_menu_send);
    drSend.setColorFilter(getResources().getColor(R.color.app_primary_color), PorterDuff.Mode.MULTIPLY);
  }

  private void initData() {
    Glide.with(self).load(itemImage).placeholder(R.drawable.image_placeholder).error(R.drawable.image_not_available).into(ivItem);
    tvItem.setText(itemName);

    arrMessages = new ArrayList<>();
    adapter = new ChatAdapter(self, arrMessages, new ChatAdapter.IChatAdapter() {

      @Override
      public void onClickUser(int position) {
        gotoUserProfileActivity(arrMessages.get(position).getUserSend(), arrMessages.get(position).getUsername());
      }

      @Override
      public void onClickItem(int position) {

      }

      @Override
      public void onClickSendAgain(int position) {
        arrMessages.get(position).setStatus(Message.STATUS_UPLOADING);
        notifyDataset(false);
        sendMessage(arrMessages.get(position));
      }
    });

    rvMessage.setAdapter(adapter);

    getMessages();

  }

  @Override
  protected void onResume() {
    super.onResume();
    isActivityVisible = true;
    intentFilter = new IntentFilter();
    intentFilter.addAction(GlobalValue.KEY_FILTER_NEW_MESSAGE);
    LocalBroadcastManager.getInstance(self).registerReceiver(
        mHandleMessageReceive, intentFilter);
  }

  @Override
  protected void onPause() {
    super.onPause();
    isActivityVisible = false;
    LocalBroadcastManager.getInstance(self).unregisterReceiver(mHandleMessageReceive);
  }

  private void initControl() {
    getParentView(ivItem).setOnClickListener(this);
    btnSend.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    if (view == btnSend) {
      if (etMessage.getText().toString().trim().length() != 0) {
        Message message = new Message();
        message.setItemID(itemID);
        message.setAvatar(myAccount.getImage());
        message.setConversationID(conversationID);
        message.setUsername(myAccount.getUsername());
        message.setUserSend(myAccount.getUserID());
        message.setUserReceive(userID);
        message.setMessage(etMessage.getText().toString().trim());
        message.setSeen(1);
        message.setStatus(Message.STATUS_UPLOADING);

        arrMessages.add(message);
        adapter.notifyDataSetChanged();

        sendMessage(message);
        etMessage.setText("");
      }
      return;
    }

    if (view == getParentView(ivItem)) {
      gotoItemDetailsActivity(itemID);
      return;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_activity_chat, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (currentUser != null) {
      if (currentUser.isBlocked()) {
        menu.getItem(1).getSubMenu().getItem(1).setVisible(false);
        menu.getItem(1).getSubMenu().getItem(2).setVisible(true);
      } else {
        menu.getItem(1).getSubMenu().getItem(1).setVisible(true);
        menu.getItem(1).getSubMenu().getItem(2).setVisible(false);
      }
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_refresh) {
      arrMessages.clear();
      getMessages();
    }
    if (item.getItemId() == R.id.action_report) {
      reportUser();
    }
    if (item.getItemId() == R.id.action_block) {
      toggleBlockUser();
    }
    if (item.getItemId() == R.id.action_unblock) {
      toggleBlockUser();
    }
    if (item.getItemId() == R.id.action_delete) {
      DialogUtility.showOptionDialog(self, getString(R.string.delete_warning), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
        @Override
        public void onPositive(Dialog dialog) {
          deleteConversation(conversationID);
        }

        @Override
        public void onNegative(Dialog dialog) {

        }
      });
    }
    return super.onOptionsItemSelected(item);
  }

  private void notifyDataset(boolean isScrollToBottom) {
    adapter.notifyDataSetChanged();
    if (isScrollToBottom)
      rvMessage.smoothScrollToPosition(arrMessages.size() - 1);
  }

  private void sendMessage(final Message message) {
    List<NameValuePair> params = ParameterFactory.createSendMessageParam(message);
    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            Message sentMsg = CommonParser.parseMessageObjectFromJson(json);
            if (sentMsg != null) {
              arrMessages.remove(message);
              arrMessages.add(sentMsg);
              notifyDataset(true);
              DatabaseHelper.insertMessage(sentMsg, self);
            }
          }

          @Override
          public void onRequestError(int _message) {
            message.setStatus(Message.STATUS_FAILED);
            notifyDataset(true);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_SEND_MESSAGE);
  }

  private void getMessages() {
    List<NameValuePair> params = ParameterFactory.createGetListMessageParam(conversationID);

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<Message> list = CommonParser.parseListMessagesFromJson(json);
            arrMessages.clear();
            arrMessages.addAll(list);
            adapter.notifyDataSetChanged();
            DatabaseHelper.insertMessage(list, self);
          }

          @Override
          public void onRequestError(int _message) {
            _onRequestError(_message);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_GET_MESSAGE_BY_CONVERSATION);
  }

  private void getUserInfo() {
    String getUrl = ParameterFactory.createGetUserInfoParam(userID);
    AsyncHttpGet asyncHttpGet = new AsyncHttpGet(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {

        parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            currentUser = CommonParser.parseUserFromJson(json, "");
            if (currentUser != null) {
              invalidateOptionsMenu();
            }

          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });

      }
    }, null);
    asyncHttpGet.execute(getUrl);
  }

  private void reportUser() {
    List<NameValuePair> params = ParameterFactory.createBlockReportUserParam(currentUser.getUserID());
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
            DialogUtility.showMessageDialog(self, getString(R.string.message_report_user_successfully), null);
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_REPORT_USER);
  }

  private void toggleBlockUser() {
    List<NameValuePair> params = ParameterFactory.createBlockReportUserParam(currentUser.getUserID());
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
            if (!currentUser.isBlocked()) {
              currentUser.setBlocked(true);
              DialogUtility.showMessageDialog(self, getString(R.string.message_block_user_successfully), null);
            } else {
              currentUser.setBlocked(false);
              DialogUtility.showMessageDialog(self, getString(R.string.message_unblock_user_successfully), null);
            }
            invalidateOptionsMenu();

          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_BLOCK_USER);
  }

  private void deleteConversation(String conversationID) {
    List<NameValuePair> params = ParameterFactory.createMoveToArchiveParam(conversationID);

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
