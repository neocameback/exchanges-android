package com.pendulab.theExchange.service;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.activity.ChatActivity;
import com.pendulab.theExchange.activity.ItemDetailsActivity;
import com.pendulab.theExchange.activity.OfferListActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.SharedPreferencesManager;
import com.pendulab.theExchange.database.DatabaseHelper;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.model.ActivityFeed;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.model.Message;
import com.pendulab.theExchange.utils.CommonParser;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Date;

public class GcmIntentService extends IntentService {

  private static final String TAG = "GcmIntentService";

  public static int NOTIFICATION_ID = 101;
  private NotificationManager mNotificationManager;

  Handler mHandler;
  public SharedPreferencesManager preference;
  private Account myAccount;

  public final int KEY_PN_CHAT = 1, KEY_PN_OFFER = 2, KEY_PN_COMMENT = 3, KEY_PN_ACCEPT_OFFER = 4, KEY_PN_ADD_WISHLIST = 5;

//    12-03 16:17:17.027  18377-28741/com.pendulab.inkblot I/GcmIntentService﹕ for: 5
//            12-03 16:17:17.028  18377-28741/com.pendulab.inkblot I/GcmIntentService﹕ data: {"date":"2015-12-03 09:10:47","to_user":"1","record_id":120,"item_id":"15","type_id":5,"from_image":"1651534260563b2f22a09e6.jpg","from_name":"anhhn.test1","from_user":"3"}
//    12-03 16:17:17.028  18377-28741/com.pendulab.inkblot I/GcmIntentService﹕ from: 433030725661
//            12-03 16:17:17.028  18377-28741/com.pendulab.inkblot I/GcmIntentService﹕ android.support.content.wakelockid: 1
//            12-03 16:17:17.028  18377-28741/com.pendulab.inkblot I/GcmIntentService﹕ collapse_key: do_not_collapse
//    12-03 16:17:48.343  18377-28870/com.pendulab.inkblot I/GcmIntentService﹕ for: 3
//            12-03 16:17:48.344  18377-28870/com.pendulab.inkblot I/GcmIntentService﹕ data: {"date":"2015-12-03 09:11:19","to_user":"1","record_id":14,"item_id":"15","type_id":3,"from_image":"1651534260563b2f22a09e6.jpg","from_name":"anhhn.test1","from_user":"3"}
//    12-03 16:17:48.344  18377-28870/com.pendulab.inkblot I/GcmIntentService﹕ from: 433030725661
//            12-03 16:17:48.344  18377-28870/com.pendulab.inkblot I/GcmIntentService﹕ android.support.content.wakelockid: 2
//            12-03 16:17:48.344  18377-28870/com.pendulab.inkblot I/GcmIntentService﹕ collapse_key: do_not_collapse

  //accept offer
//    for: 4
//            12-03 16:20:38.827  18377-29575/com.pendulab.inkblot I/GcmIntentService﹕ data: {"date":"2015-12-03 09:14:09","to_user":"1","record_id":"6","item_id":"20","type_id":4,"from_image":"1651534260563b2f22a09e6.jpg","from_name":"anhhn.test1","from_user":"3"}

//    data: {"date":"2015-12-03 09:18:22","to_user":"1","record_id":10,"item_id":"15","type_id":2,"from_image":"1651534260563b2f22a09e6.jpg","from_name":"anhhn.test1","from_user":"3"}

  @Override
  public void onCreate() {
    // TODO Auto-generated method stub
    super.onCreate();
    mHandler = new Handler();
    preference = SharedPreferencesManager.getInstance(getApplicationContext());
    myAccount = preference.getUserInfo();
  }

  public GcmIntentService() {
    super(TAG);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
    String gcmMessageType = gcm.getMessageType(intent);

    if (!extras.isEmpty()) {
      // has effect of unparcelling Bundle
      if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
          .equals(gcmMessageType)) {
      } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
          .equals(gcmMessageType)) {
      } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
          .equals(gcmMessageType)) {

        Bundle bundle = intent.getExtras();
        try {
          handleMessage(bundle);
        } catch (Exception e) {
          e.printStackTrace();
        }

      }
    }

    // Release the wake lock provided by the WakefulBroadcastReceiver.
    GcmBroadcastReceiver.completeWakefulIntent(intent);
  }

  private void handleMessage(Bundle bundle) throws JSONException {
    if (bundle == null) {
      return;
    }

    for (String key : bundle.keySet()) {
      Log.i(getClass().getSimpleName(), key + ": " + bundle.get(key).toString());
    }

    if (!bundle.containsKey("for")) {
      return;
    }

    if (Integer.parseInt(bundle.getString("for")) == KEY_PN_CHAT) {
      try {
        JSONObject obj = new JSONObject(bundle.getString("data"));
        Message newMessage = CommonParser.parseMessageFromJsonObject(obj);
        generateMessageNotification(newMessage);

      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      ActivityFeed feed = CommonParser.parseSingleActivityFromJson(getApplicationContext(), bundle.getString("data"));
      if (feed != null) {
        generateActivityNotification(feed);
      }
    }
  }

  /**
   * @param message responsible if user does not exists in database
   */

  private void generateMessageNotification(Message message) {
    if (ChatActivity.isActivityVisible && message.getUserSend().equalsIgnoreCase(ChatActivity.userID)) {
      Intent intent = new Intent(GlobalValue.KEY_FILTER_NEW_MESSAGE);
      intent.putExtra(GlobalValue.KEY_MESSAGE, message);
      LocalBroadcastManager.getInstance(getApplicationContext())
          .sendBroadcast(intent);

      message.setSeen(1);
      DatabaseHelper.insertMessage(message, getApplicationContext());
      return;
    }

    message.setSeen(0);
    DatabaseHelper.insertMessage(message, getApplicationContext());

    String msg = "";
    NOTIFICATION_ID = (int) new Date().getTime() / 1000;

    msg = message.getUsername() + ": " + message.getMessage();


    mNotificationManager = (NotificationManager) this
        .getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
        this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(getResources().getString(R.string.app_name))
        .setLights(Color.GREEN, 500, 500)
        .setStyle(
            new NotificationCompat.BigTextStyle().bigText(msg))
        .setTicker(msg).setContentText(msg).setAutoCancel(true);

//		Play default notification sound
    mBuilder.setDefaults(Notification.DEFAULT_SOUND
        | Notification.FLAG_AUTO_CANCEL);

    long[] pattern = {500, 500, 500, 500, 500};
    mBuilder.setVibrate(pattern);

    Intent toChatIntent = new Intent(this, ChatActivity.class);
    Bundle bundle = new Bundle();
    bundle.putString(GlobalValue.KEY_ITEM_ID, message.getItemID());
    bundle.putString(GlobalValue.KEY_ITEM_NAME, message.getItemTitle());
    bundle.putString(GlobalValue.KEY_ITEM_IMAGE, message.getItemImage());
    bundle.putString(GlobalValue.KEY_USERNAME, message.getUsername());
    bundle.putString(GlobalValue.KEY_USER_ID, message.getUserSend());
    bundle.putString(GlobalValue.KEY_CONVERSATION_ID, message.getConversationID());

    toChatIntent.putExtras(bundle);

    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        toChatIntent, PendingIntent.FLAG_ONE_SHOT);

    mBuilder.setContentIntent(contentIntent);
    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
  }

  private void generateActivityNotification(ActivityFeed feed) {

    NOTIFICATION_ID = (int) new Date().getTime() / 1000;

    String content = "";
    Intent toIntent = null;

    switch (feed.getType()) {
      case ActivityFeed.ACTIVITY_TYPE_ADD_WISH_LIST:
        content += feed.getUsername() + " " + getBaseContext().getString(R.string.activity_add_wishlist);
        toIntent = new Intent(this, ItemDetailsActivity.class);
        toIntent.putExtra(GlobalValue.KEY_ITEM, feed.getItemId());
        toIntent.putExtra(GlobalValue.KEY_VIEW_LIKER, "");
        break;
      case ActivityFeed.ACTIVITY_TYPE_COMMENT:
        content += feed.getUsername() + " " + getBaseContext().getString(R.string.activity_comment);
        toIntent = new Intent(this, ItemDetailsActivity.class);
        toIntent.putExtra(GlobalValue.KEY_ITEM, feed.getItemId());
        toIntent.putExtra(GlobalValue.KEY_VIEW_COMMENT, "");
        break;
      case ActivityFeed.ACTIVITY_TYPE_MAKE_OFFER:
        content += feed.getUsername() + " " + getBaseContext().getString(R.string.sent_you_an_offer).toLowerCase();
        Item item = new Item();
        item.setId(feed.getItemId());
        toIntent = new Intent(this, OfferListActivity.class);
        toIntent.putExtra(GlobalValue.KEY_ITEM, item);
        break;
      case ActivityFeed.ACTIVITY_TYPE_ACCEPT_OFFER:
        content += feed.getUsername() + " " + getBaseContext().getString(R.string.accepted_your_offer).toLowerCase();
        Item item2 = new Item();
        item2.setId(feed.getItemId());
        toIntent = new Intent(this, OfferListActivity.class);
        toIntent.putExtra(GlobalValue.KEY_ITEM, item2);
        break;

      default:
        break;
    }

    mNotificationManager = (NotificationManager) this
        .getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
        this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(getResources().getString(R.string.app_name))
        .setLights(Color.GREEN, 500, 500)
        .setStyle(
            new NotificationCompat.BigTextStyle().bigText(content))
        .setTicker(content).setContentText(content).setAutoCancel(true);

//		Play default notification sound
    mBuilder.setDefaults(Notification.DEFAULT_SOUND
        | Notification.FLAG_AUTO_CANCEL);

    long[] pattern = {500, 500, 500, 500, 500};
    mBuilder.setVibrate(pattern);

    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        toIntent, PendingIntent.FLAG_ONE_SHOT);

    mBuilder.setContentIntent(contentIntent);
    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
  }

  private static String getStringValue(JSONObject obj, String key) {
    try {
      return obj.isNull(key) ? null : obj.getString(key);
    } catch (JSONException e) {
      return null;
    }
  }
}
