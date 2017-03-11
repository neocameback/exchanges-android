package com.pendulab.theExchange.database;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.pendulab.theExchange.model.Message;
import com.pendulab.theExchange.model.SearchKeyword;
import com.pendulab.theExchange.utils.BadgeUtils;

import android.content.Context;
import android.database.Cursor;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 7/18/2015.
 */
public class DatabaseHelper {

  public static final String TAG = "DatabaseHelper";

//    public static void insertConversations(List<Conversation> list, Account myAccount) {
//        ActiveAndroid.beginTransaction();
//        try {
//            for (Conversation conversation : list) {
//                Conversation existedConversation = getExistedConversation(conversation.getConversationId());
//                if (existedConversation == null) {
//                    Log.i(TAG, "existedConversation - " + conversation.getConversationId() + ": null");
//                    if (myAccount.getUserType().equalsIgnoreCase(Account.USER_TYPE_DANCER)) {
//                        conversation.setLocationPrice(myAccount.getPhotoStd());
//                        conversation.setVideoPrice(myAccount.getVideoStd());
//                        conversation.setPhotoPrice(myAccount.getPhotoStd());
//                        conversation.setAudioPrice(myAccount.getPhotoStd());
//                    }
//                    conversation.save();
//                } else {
//                    Log.i(TAG, "existedConversation - " + conversation.getConversationId() + ": not null");
//                    existedConversation.setLastSession(conversation.getLastSession());
//                    existedConversation.save();
//                }
//            }
//            ActiveAndroid.setTransactionSuccessful();
//        } finally {
//            ActiveAndroid.endTransaction();
//        }
//    }
//
//    public static void insertConversation(Conversation conversation, Account myAccount) {
//
//        ActiveAndroid.beginTransaction();
//        try {
//            Conversation existedConversation = getExistedConversation(conversation.getConversationId());
//            if (existedConversation == null) {
//                Log.i(TAG, "existedConversation - " + conversation.getConversationId() + ": null");
//                if (myAccount.getUserType().equalsIgnoreCase(Account.USER_TYPE_DANCER)) {
//                    conversation.setLocationPrice(myAccount.getPhotoStd());
//                    conversation.setVideoPrice(myAccount.getVideoStd());
//                    conversation.setPhotoPrice(myAccount.getPhotoStd());
//                    conversation.setAudioPrice(myAccount.getPhotoStd());
//                }
//                conversation.save();
//            } else {
//                Log.i(TAG, "existedConversation - " + conversation.getConversationId() + ": not null");
//                existedConversation.setLastSession(conversation.getLastSession());
//                existedConversation.save();
//
//            }
//            ActiveAndroid.setTransactionSuccessful();
//        } finally {
//            ActiveAndroid.endTransaction();
//        }
//    }
//
//    public static void updateConversation(Conversation conversation) {
//
//        ActiveAndroid.beginTransaction();
//        try {
//            conversation.save();
//            ActiveAndroid.setTransactionSuccessful();
//        } finally {
//            ActiveAndroid.endTransaction();
//        }
//    }
//
//    public static Conversation getExistedConversation(String conversationId) {
//        return new Select()
//                .from(Conversation.class)
//                .where("conversationId = ?", conversationId)
//                .executeSingle();
//    }


//    public static List<Message> getListMessageByConversation(String conversationId) {
//        try {
//            List<Message> list =
//                    new Select()
//                            .from(Message.class)
//                            .where("ConversationId = ?", conversationId)
//                            .execute();
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ArrayList<Message>();
//        }
//    }

  public static boolean isKeywordExisted(SearchKeyword sk) {
    From query = new Select()
        .from(SearchKeyword.class)
        .where("_id = ? AND Keyword = '" + sk.getKeyword() + "' AND Type= " + sk.getType(), sk.getUserId());
    return (Cache.openDatabase().rawQuery(query.toSql(), query.getArguments()).getCount() > 0 ? true : false);

  }

  public static boolean insertKeyword(SearchKeyword sk) {
//
    if (!isKeywordExisted(sk)) {
      if (!sk.getKeyword().equalsIgnoreCase("")) {
        ActiveAndroid.beginTransaction();
        try {
          sk.save();
          ActiveAndroid.setTransactionSuccessful();
        } finally {
          ActiveAndroid.endTransaction();
          return true;
        }
      }
    }
    return false;
  }

  public static Cursor getCursorItemKeyword(String userID, String keyword) {
    try {
      From query =
          new Select()
              .from(SearchKeyword.class)
              .where("_id = ? AND Keyword like '%" + keyword + "%' AND Type = " + SearchKeyword.TYPE_SEARCH_ITEM, userID);
//            String query = "SELECT * FROM SearchKeyword WHERE _id = "+userID+" AND Keyword like '%"+keyword+"%'";
      Cursor cursor = Cache.openDatabase().rawQuery(query.toSql(), query.getArguments());
      return cursor;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Cursor getCursorUserKeyword(String userID, String keyword) {
    try {
      From query =
          new Select()
              .from(SearchKeyword.class)
              .where("_id = ? AND Keyword like '%" + keyword + "%' AND Type = " + SearchKeyword.TYPE_SEARCH_USER, userID);
//            String query = "SELECT * FROM SearchKeyword WHERE _id = "+userID+" AND Keyword like '%"+keyword+"%'";
      Cursor cursor = Cache.openDatabase().rawQuery(query.toSql(), query.getArguments());
      return cursor;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void insertMessage(Message message, Context context) {
    ActiveAndroid.beginTransaction();
    try {
      message.save();
      ActiveAndroid.setTransactionSuccessful();
    } finally {
      ActiveAndroid.endTransaction();
      BadgeUtils.showNewMessages(context);
    }
  }

  public static void insertMessage(List<Message> list, Context context) {
    ActiveAndroid.beginTransaction();
    try {
      for (Message message : list) {
        message.save();
      }
      ActiveAndroid.setTransactionSuccessful();
    } finally {
      ActiveAndroid.endTransaction();
      BadgeUtils.showNewMessages(context);
    }
  }

  public static void deleteMessageByConversation(String conversationID) {
    try {
      new Delete().from(Message.class).where("ConversationID = ?", conversationID).execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String getLastMessageId(String myID) {
    try {
      Message lastMessage =
          new Select()
              .from(Message.class)
              .where("UserSend = ? OR UserReceive = ?", myID, myID)
              .orderBy("Id DESC")
              .limit(1)
              .executeSingle();
      return lastMessage.getMessageId();
    } catch (Exception e) {
      e.printStackTrace();
      return "0";
    }
  }

  public static int calculateNewMessage(String myID) {
    From query = new Select()
        .from(Message.class)
        .where("UserReceive = ? AND Seen = 0", myID);
    return (Cache.openDatabase().rawQuery(query.toSql(), query.getArguments()).getCount());
  }


}
