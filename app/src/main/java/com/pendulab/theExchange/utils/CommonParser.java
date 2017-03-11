package com.pendulab.theExchange.utils;

import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.model.ActivityFeed;
import com.pendulab.theExchange.model.Category;
import com.pendulab.theExchange.model.Comment;
import com.pendulab.theExchange.model.CreditPack;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.model.LocationObj;
import com.pendulab.theExchange.model.Message;
import com.pendulab.theExchange.model.Offer;
import com.pendulab.theExchange.model.PhoneCode;
import com.pendulab.theExchange.model.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Parsing json data received from API
 */
public class CommonParser {

  public static List<Account> parseListAccountFromJson(String json) {
    List<Account> list = new ArrayList<>();

    try {
      JSONObject entryObj = new JSONObject(json);
      JSONArray array = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);

      for (int i = 0; i < array.length(); i++) {
        JSONObject obj = array.getJSONObject(i);
        Account account = new Account();

        account.setUserID(getStringValue(obj, "id"));
        account.setUsername(getStringValue(obj, "username"));
        account.setPhoneNumber(getStringValue(obj, "phone_number"));
        account.setSocialID(getStringValue(obj, "social_id"));
        if (getStringValue(obj, "social_type").equalsIgnoreCase("")) {
          account.setUserType(Account.USER_TYPE_NORMAL);
        } else {
          account.setUserType(getStringValue(obj, "social_type"));
        }

        account.setFirstname(getStringValue(obj, "firstname"));
        account.setLastname(getStringValue(obj, "lastname"));
        account.setCity(getStringValue(obj, "city"));
        account.setImage(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "image"));
        account.setBio(getStringValue(obj, "bio"));
        account.setEmail(getStringValue(obj, "email"));
        account.setGender(getStringValue(obj, "gender"));
        account.setBirthday(getStringValue(obj, "birthday"));
        account.setRegisterDate(getStringValue(obj, "register_date"));
        account.setTradeCredit(getIntValue(obj, "trade_credit"));

        list.add(account);

      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }


  public static Account parseUserFromJson(String json, String token) {
    Account account = new Account();

    try {
      JSONObject entryObj = new JSONObject(json);
      JSONObject obj = entryObj.getJSONObject(WebServiceConfig.KEY_DATA);

      account.setUserID(getStringValue(obj, "id"));
      account.setUsername(getStringValue(obj, "username"));
      account.setPhoneNumber(getStringValue(obj, "phone_number"));
      account.setSocialID(getStringValue(obj, "social_id"));
      if (getStringValue(obj, "social_type").equalsIgnoreCase("")) {
        account.setUserType(Account.USER_TYPE_NORMAL);
      } else {
        account.setUserType(getStringValue(obj, "social_type"));
      }

      account.setFirstname(getStringValue(obj, "firstname"));
      account.setLastname(getStringValue(obj, "lastname"));
      account.setCity(getStringValue(obj, "city"));
      account.setImage(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "image"));
      account.setBio(getStringValue(obj, "bio"));
      account.setEmail(getStringValue(obj, "email"));
      account.setGender(getStringValue(obj, "gender"));
      account.setBirthday(getStringValue(obj, "birthday"));
      account.setRegisterDate(getStringValue(obj, "register_date"));
      account.setTradeCredit(getIntValue(obj, "trade_credit"));
      account.setVerificationCode(getStringValue(obj, "generate_code"));
      account.setToken(getStringValue(obj, "token"));
      if (!token.equalsIgnoreCase("")) {
        account.setToken(token);
      }

      account.setInboxCount(getIntValue(obj, "unseen"));
      account.setWishlistCount(getIntValue(obj, "wishlist"));
      account.setInventoryCount(getIntValue(obj, "inventory"));
      account.setBlocked(getIntValue(obj, "block") == 1);
      account.setRate(getDoubleValue(obj, "rate").floatValue());
      account.setNumberRate(getIntValue(obj, "number_rate"));


    } catch (Exception e) {
      e.printStackTrace();
    }

    return account;

  }

  public static ArrayList<LocationObj> parseLocations(String json) {
    ArrayList<LocationObj> arrLoc = new ArrayList<LocationObj>();
    try {
      JSONObject jsonObj = new JSONObject(json);
      if (jsonObj != null) {
        JSONArray arrJson = jsonObj.getJSONArray("results");
        LocationObj locObj = null;
        ArrayList<JSONObject> arrCorrectAddress = null;
        JSONArray arrAddress = null;
        JSONObject js = null;
        for (int i = 0; i < arrJson.length(); i++) {
          JSONObject obj = arrJson.getJSONObject(i);
          locObj = new LocationObj();

          // Get latitude and longitude
          JSONObject objGeometry = obj.getJSONObject("geometry");
          JSONObject objLatLong = objGeometry
              .getJSONObject("location");
          locObj.setLatitude(objLatLong.getDouble("lat"));
          locObj.setLongitude(objLatLong.getDouble("lng"));
          locObj.setAddress(obj.getString("formatted_address"));
          // Get city, country
          arrAddress = obj.getJSONArray("address_components");
          arrCorrectAddress = new ArrayList<JSONObject>();
          for (int j = 0; j < arrAddress.length(); j++) {
            js = arrAddress.getJSONObject(j);
            if (js.getString("types").contains("political")) {
              arrCorrectAddress.add(js);
            }
          }

          int levelAddress = arrCorrectAddress.size();

          if (levelAddress >= 3) {

            // get country
            JSONObject objCountry = arrCorrectAddress
                .get(levelAddress - 1);
            locObj.setCountryCode(objCountry
                .getString("short_name"));
            locObj.setCountry(objCountry.getString("long_name"));
            // get state
            JSONObject objState = arrCorrectAddress
                .get(levelAddress - 2);
            locObj.setState(objState.getString("short_name"));
            // get state
            JSONObject objDistrict = arrCorrectAddress
                .get(levelAddress - 3);
            locObj.setDistrict(objDistrict.getString("long_name"));
          } else if (levelAddress == 2) {
            // get country
            JSONObject objCountry = arrCorrectAddress
                .get(levelAddress - 1);
            locObj.setCountryCode(objCountry
                .getString("short_name"));
            locObj.setCountry(objCountry.getString("long_name"));
            // get state
            JSONObject objState = arrCorrectAddress
                .get(levelAddress - 2);
            locObj.setState(objState.getString("long_name"));
          } else {
            // get country
            if (levelAddress >= 1) {
              JSONObject objCountry = arrCorrectAddress
                  .get(levelAddress - 1);
              locObj.setCountryCode(objCountry
                  .getString("short_name"));
              locObj.setCountry(objCountry.getString("long_name"));
            }
          }

          arrLoc.add(locObj);
        }
      }

    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return arrLoc;
  }

  public static List<PhoneCode> parseListPhonecodeFromJson(String json) {
    List<PhoneCode> list = new ArrayList<>();

    try {
      JSONObject entryObj = new JSONObject(json);
      JSONArray dataArr = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);
      for (int i = 0; i < dataArr.length(); i++) {
        JSONObject obj = dataArr.getJSONObject(i);
        PhoneCode code = new PhoneCode();

        code.setCountry(getStringValue(obj, "short_name"));
        code.setLocale(getStringValue(obj, "iso2"));
        code.setCode(getStringValue(obj, "calling_code"));

        list.add(code);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }

  public static List<Category> parseListCategoriesFromJson(String json) {
    List<Category> list = new ArrayList<>();

    try {
      JSONObject entryObj = new JSONObject(json);
      JSONArray dataArr = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);
      for (int i = 0; i < dataArr.length(); i++) {
        JSONObject obj = dataArr.getJSONObject(i);
        Category category = new Category();

        category.setId(getStringValue(obj, "id"));
        category.setName(getStringValue(obj, "name"));
        category.setImage(WebServiceConfig.CATEGORY_IMAGE_PREFIX + getStringValue(obj, "image"));

        list.add(category);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }

  public static List<Item> parseListItemsFromJson(Context context, String json) {
    List<Item> list = new ArrayList<>();

    try {
      JSONObject entryObj = new JSONObject(json);
      JSONArray dataArr = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);
      for (int i = 0; i < dataArr.length(); i++) {
        JSONObject obj = dataArr.getJSONObject(i);
        Item item = new Item();

        item.setId(getStringValue(obj, "id"));
        item.setCategoryId(getStringValue(obj, "category_id"));
        item.setOwnerId(getStringValue(obj, "user_id"));
        item.setOwnerUsername(getStringValue(obj, "username"));
        item.setOwnerImage(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "user_image"));
        item.setTitle(getStringValue(obj, "title"));
        item.setPrice(getIntValue(obj, "price"));
        item.setDate(AppUtil.timeAgo(context, getStringValue(obj, "date")));
        item.setLatitude(getDoubleValue(obj, "lat"));
        item.setLongitude(getDoubleValue(obj, "lng"));
        item.setDescription(getStringValue(obj, "description"));
        item.setImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "item_image"));
        item.setLocation(getStringValue(obj, "location"));
        item.setDistance(getDoubleValue(obj, "distance"));
        item.setLikeCount(getIntValue(obj, "number_bookmark"));
        item.setLiked(getIntValue(obj, "wishlist") == 1);
        item.setStatus(getIntValue(obj, "status"));

        list.add(item);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }

  public static List<Item> parseListWishlistFromJson(Context context, String json) {
    List<Item> list = new ArrayList<>();

    try {
      JSONObject entryObj = new JSONObject(json);

      JSONArray manualArr = entryObj.getJSONArray("manual");
      for (int i = 0; i < manualArr.length(); i++) {
        JSONObject obj = manualArr.getJSONObject(i);
        Item item = new Item();
        item.setId(getStringValue(obj, "id"));
        item.setTitle(getStringValue(obj, "title"));
        item.setImage(getStringValue(obj, "min_price"));
        item.setDate(getStringValue(obj, "max_price"));

        list.add(item);
      }

      JSONArray dataArr = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);
      for (int i = 0; i < dataArr.length(); i++) {
        JSONObject obj = dataArr.getJSONObject(i);
        Item item = new Item();

        item.setId(getStringValue(obj, "id"));
        item.setCategoryId(getStringValue(obj, "category_id"));
        item.setOwnerId(getStringValue(obj, "user_id"));
        item.setOwnerUsername(getStringValue(obj, "username"));
        item.setOwnerImage(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "user_image"));
        item.setTitle(getStringValue(obj, "title"));
        item.setPrice(getIntValue(obj, "price"));
        item.setDate(AppUtil.timeAgo(context, getStringValue(obj, "date")));
        item.setLatitude(getDoubleValue(obj, "lat"));
        item.setLongitude(getDoubleValue(obj, "lng"));
        item.setDescription(getStringValue(obj, "description"));
        item.setImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "item_image"));
        item.setLocation(getStringValue(obj, "location"));
        item.setDistance(getDoubleValue(obj, "distance"));
        item.setLikeCount(getIntValue(obj, "number_bookmark"));
        item.setLiked(getIntValue(obj, "wishlist") == 1);
        item.setStatus(getIntValue(obj, "status"));

        list.add(item);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }

  public static Item parseSingleItemFromJson(Context context, String json) {

    try {
      JSONObject entryObj = new JSONObject(json);

      JSONObject obj = entryObj.getJSONObject(WebServiceConfig.KEY_DATA);
      Item item = new Item();

      item.setId(getStringValue(obj, "id"));
      item.setCategoryId(getStringValue(obj, "category_name"));
      item.setOwnerId(getStringValue(obj, "user_id"));
      item.setOwnerUsername(getStringValue(obj, "username"));
      item.setOwnerImage(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "user_image"));
      item.setTitle(getStringValue(obj, "title"));
      item.setPrice(getIntValue(obj, "price"));
      item.setDate(AppUtil.timeAgo(context, getStringValue(obj, "date")));
      item.setLatitude(getDoubleValue(obj, "lat"));
      item.setLongitude(getDoubleValue(obj, "lng"));
      item.setDescription(getStringValue(obj, "description"));
      item.setImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "item_image"));
      item.setLocation(getStringValue(obj, "location"));
      item.setDistance(getDoubleValue(obj, "distance"));
      item.setLikeCount(getIntValue(obj, "number_bookmark"));
      item.setLiked(getIntValue(obj, "wishlist") == 1);
      item.setReported(getIntValue(obj, "report") == 1);
      item.setOffered(getIntValue(obj, "offer") == 1);
      item.setCommnetCount(getIntValue(obj, "comment_count"));
      item.setStatus(getIntValue(obj, "status"));
      item.setVerificationStatus(getIntValue(obj, "verify"));
      item.setRate(getDoubleValue(obj, "rate"));

      item.getArrImages().add(item.getImage());
      if (hasKey(obj, "sub_img_0")) {
        item.getArrImages().add(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "sub_img_0"));
      }

      if (hasKey(obj, "sub_img_1")) {
        item.getArrImages().add(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "sub_img_1"));
      }

      if (hasKey(obj, "sub_img_2")) {
        item.getArrImages().add(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "sub_img_2"));
      }

      return item;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static List<Comment> parseListCommentsFromJson(Context context, String json) {
    List<Comment> list = new ArrayList<>();

    try {
      JSONObject entryObj = new JSONObject(json);
      JSONArray array = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);

      for (int i = 0; i < array.length(); i++) {
        JSONObject obj = array.getJSONObject(i);
        Comment comment = new Comment();
        comment.setId(getStringValue(obj, "id"));
        comment.setUsername(getStringValue(obj, "username"));
        comment.setUserImage(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "image"));
        comment.setUserId(getStringValue(obj, "user_id"));
        comment.setItemId(getStringValue(obj, "item_id"));
        comment.setText(getStringValue(obj, "text"));
        comment.setDate(AppUtil.timeAgo(context, getStringValue(obj, "date")));
        comment.setStatus(Comment.STATUS_POSTED);

        list.add(comment);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  public static String getConversationIDFromJson(String json) {
    String conversationID = "";

    try {
      JSONObject entryObj = new JSONObject(json);
      conversationID = getStringValue(entryObj, WebServiceConfig.KEY_DATA);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return conversationID;
  }

  public static List<Message> parseListMessagesFromJson(String json) {
    List<Message> list = new ArrayList<>();

    try {
      JSONObject entryObj = new JSONObject(json);

      int offerMoney = 0;
      String tradeItem = "";

      if (isJsonObject(entryObj, "offer")) {
        JSONObject offerObj = entryObj.getJSONObject("offer");
        offerMoney = getIntValue(offerObj, "money");
        tradeItem = getStringValue(offerObj, "trade_item");
      }

      JSONArray array = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);

      for (int i = 0; i < array.length(); i++) {
        JSONObject obj = array.getJSONObject(i);
        Message message = new Message();
        message.setMessageId(getStringValue(obj, "id"));
        message.setConversationID(getStringValue(obj, "conversation_id"));
        message.setItemID(getStringValue(obj, "item_id"));
        message.setUsername(getStringValue(obj, "username_send"));
        message.setUserSend(getStringValue(obj, "user_send"));
        message.setUserReceive(getStringValue(obj, "user_receive"));
        message.setAvatar(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "image_send"));
        message.setMessage(getStringValue(obj, "message"));
        message.setDate(getStringValue(obj, "date"));
        if (hasKey(obj, "seen")) {
          message.setSeen(getIntValue(obj, "seen"));
        } else {
          message.setSeen(1);
        }
        message.setOfferMoney(offerMoney);
        message.setOfferItems(tradeItem);
        message.setStatus(Message.STATUS_SENT);

        if (!message.getMessage().equalsIgnoreCase("")) {
          list.add(message);
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  public static Message parseMessageObjectFromJson(String json) {

    try {
      JSONObject entryObj = new JSONObject(json);
      JSONObject obj = entryObj.getJSONObject(WebServiceConfig.KEY_DATA);


      Message message = new Message();
      message.setMessageId(getStringValue(obj, "id"));
      message.setConversationID(getStringValue(obj, "conversation_id"));
      message.setItemID(getStringValue(obj, "item_id"));
      message.setUsername(getStringValue(obj, "username_send"));
      message.setUserSend(getStringValue(obj, "user_send"));
      message.setUserReceive(getStringValue(obj, "user_receive"));
      message.setAvatar(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "image_send"));
      message.setMessage(getStringValue(obj, "message"));
      message.setDate(getStringValue(obj, "date"));
      message.setStatus(Message.STATUS_SENT);

      if (message.getMessage().equalsIgnoreCase("")) {
        return null;
      }

      return message;


    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Message parseMessageFromJsonObject(JSONObject obj) {
    Message message = new Message();
    message.setMessageId(getStringValue(obj, "id"));
    message.setConversationID(getStringValue(obj, "conversation_id"));
    message.setItemID(getStringValue(obj, "item_id"));
    message.setItemTitle(getStringValue(obj, "item_title"));
    message.setItemImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "item_image"));
    message.setUsername(getStringValue(obj, "username_send"));
    message.setUserSend(getStringValue(obj, "user_send"));
    message.setUserReceive(getStringValue(obj, "user_receive"));
    message.setAvatar(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "image_send"));
    message.setMessage(getStringValue(obj, "message"));
    message.setDate(getStringValue(obj, "date"));
    message.setStatus(Message.STATUS_SENT);

    if (message.getMessage().equalsIgnoreCase("")) {
      return null;
    }

    return message;
  }

  public static List<Message> parseListInboxFromJson(String json, Account myAccount) {
    List<Message> list = new ArrayList<>();

    try {
      JSONObject entryObj = new JSONObject(json);
      JSONArray array = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);

      for (int i = 0; i < array.length(); i++) {
        try {
          JSONObject obj = array.getJSONObject(i);
          Message message = new Message();
          message.setConversationID(getStringValue(obj, "conversation_id"));
          message.setItemID(getStringValue(obj, "item_id"));
          message.setItemTitle(getStringValue(obj, "item_title"));
          message.setItemImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "item_image"));

          if (!getStringValue(obj, "user_send").equalsIgnoreCase(myAccount.getUserID())) {
            message.setUsername(getStringValue(obj, "username_send"));
            message.setAvatar(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "image_send"));
            message.setUserId(getStringValue(obj, "user_send"));
          } else {
            message.setUsername(getStringValue(obj, "username_receive"));
            message.setAvatar(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "image_receive"));
            message.setUserId(getStringValue(obj, "user_receive"));
          }
          message.setUserSend(getStringValue(obj, "user_send"));
          message.setUserReceive(getStringValue(obj, "user_receive"));
          message.setMessage(getStringValue(obj, "last_message"));
          message.setDate(getStringValue(obj, "date"));
          message.setOfferMoney(getIntValue(obj, "offer_money"));
          message.setOfferItems(getStringValue(obj, "offer_trade_item"));
          message.setStatus(Message.STATUS_SENT);

          message.setUnseenCount(getIntValue(obj, "unseen"));

          list.add(message);
        } catch (Exception e) {
          e.printStackTrace();
          continue;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  public static List<Object> parseListInboxFromJson(List<Object> arrObject, String json, Account myAccount) {
    try {
      JSONObject entryObj = new JSONObject(json);
      JSONArray array = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);

      for (int i = 0; i < array.length(); i++) {
        try {
          JSONObject obj = array.getJSONObject(i);
          Message message = new Message();
          message.setConversationID(getStringValue(obj, "conversation_id"));
          message.setItemID(getStringValue(obj, "item_id"));
          message.setItemTitle(getStringValue(obj, "item_title"));
          message.setItemImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "item_image"));

          if (!getStringValue(obj, "user_send").equalsIgnoreCase(myAccount.getUserID())) {
            message.setUsername(getStringValue(obj, "username_send"));
            message.setAvatar(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "image_send"));
            message.setUserId(getStringValue(obj, "user_send"));
          } else {
            message.setUsername(getStringValue(obj, "username_receive"));
            message.setAvatar(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "image_receive"));
            message.setUserId(getStringValue(obj, "user_receive"));
          }
          message.setUserSend(getStringValue(obj, "user_send"));
          message.setUserReceive(getStringValue(obj, "user_receive"));
          message.setMessage(getStringValue(obj, "last_message"));
          message.setDate(getStringValue(obj, "date"));
          message.setOfferMoney(getIntValue(obj, "offer_money"));
          message.setOfferItems(getStringValue(obj, "offer_trade_item"));
          message.setStatus(Message.STATUS_SENT);

          message.setUnseenCount(getIntValue(obj, "unseen"));

          arrObject.add(message);
        } catch (Exception e) {
          e.printStackTrace();
          continue;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return arrObject;
  }

  public static List<Offer> parseListOffersFromJson(Context context, String json) {
    List<Offer> list = new ArrayList<>();
    try {
      JSONObject entryObj = new JSONObject(json);
      JSONArray array = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);

      for (int i = 0; i < array.length(); i++) {
        JSONObject obj = array.getJSONObject(i);
        Offer offer = new Offer();
        offer.setId(getStringValue(obj, "id"));
        offer.setOwnerID(getStringValue(obj, "owner_id"));
        offer.setUsername(getStringValue(obj, "username_buy"));
        offer.setAvatar(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "image_buy"));
        offer.setUserID(getStringValue(obj, "user_buy"));
        offer.setMoney(getIntValue(obj, "money"));
        offer.setDate(AppUtil.timeAgo(context, getStringValue(obj, "date")));
        offer.setStatus(getIntValue(obj, "status"));
        offer.setVerify(getIntValue(obj, "verify"));
        offer.setItemID(getStringValue(obj, "item_id"));
        offer.setItemTitle(getStringValue(obj, "title"));
        offer.setItemImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "image"));

        if (isJsonArray(obj, "trade_item")) {
          JSONArray arrItems = obj.getJSONArray("trade_item");
          for (int j = 0; j < arrItems.length(); j++) {
            JSONObject itemObj = arrItems.getJSONObject(j);
            Item item = new Item();
            item.setId(getStringValue(itemObj, "id"));
            item.setImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(itemObj, "image"));
            offer.getArrTradeItems().add(item);
          }
        }

        list.add(offer);

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  public static List<Offer> parseListOfferSellFromJson(Context context, String json) {
    List<Offer> list = new ArrayList<>();
    try {
      JSONObject entryObj = new JSONObject(json);
      JSONArray array = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);

      for (int i = 0; i < array.length(); i++) {
        JSONObject obj = array.getJSONObject(i);
        Offer offer = new Offer();

        offer.setItemID(getStringValue(obj, "item_id"));
        offer.setItemTitle(getStringValue(obj, "title"));
        offer.setItemImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "image"));
        offer.setMoney(getIntValue(obj, "offer_count"));

        list.add(offer);

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  public static String[] parseVerificationCodeFromJson(String json) {

    try {
      JSONObject entryObj = new JSONObject(json);
      JSONObject obj = entryObj.getJSONObject(WebServiceConfig.KEY_DATA);

      String code = getStringValue(obj, "code");
      String transactionId = getStringValue(obj, "id");

      String[] array = new String[2];
      array[0] = code;
      array[1] = transactionId;

      return array;


    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean isUserRatable(String json) {
    try {
      JSONObject entryObj = new JSONObject(json);
      JSONObject obj = entryObj.getJSONObject(WebServiceConfig.KEY_DATA);

      int status = getIntValue(obj, "status");
      return (status == 1);

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public static Object[] getExistedRate(String json) {
    try {
      Object[] arrObj = new Object[2];
      JSONObject entryObj = new JSONObject(json);
      JSONObject obj = entryObj.getJSONObject(WebServiceConfig.KEY_DATA);

      Float rate = getDoubleValue(obj, "rate").floatValue();
      arrObj[0] = rate;
      String review = getStringValue(obj, "review");
      arrObj[1] = review;

      return arrObj;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static List<ActivityFeed> parseListActivitiesFromJson(Context context, String json) {
    List<ActivityFeed> list = new ArrayList<>();
    try {
      JSONObject entryObj = new JSONObject(json);
      JSONArray array = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);

      for (int i = 0; i < array.length(); i++) {
        JSONObject obj = array.getJSONObject(i);
        ActivityFeed feed = new ActivityFeed();

        feed.setId(getStringValue(obj, "id"));
        feed.setType(getIntValue(obj, "type_id"));
        feed.setUserId(getStringValue(obj, "from_user"));
        feed.setUsername(getStringValue(obj, "from_username"));
        feed.setUserAvatar(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "from_image"));
        feed.setItemId(getStringValue(obj, "item_id"));
        feed.setItemImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "item_image"));
        feed.setRecordId(getStringValue(obj, "record_id"));
        feed.setSeen(getIntValue(obj, "seen"));
        feed.setTimeAgo(AppUtil.timeAgo(context, getStringValue(obj, "date")));
        feed.setDate(getStringValue(obj, "date"));

        list.add(feed);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  public static ActivityFeed parseSingleActivityFromJson(Context context, String json) {
    try {
      JSONObject obj = new JSONObject(json);
      ActivityFeed feed = new ActivityFeed();

      feed.setId(getStringValue(obj, "id"));
      feed.setType(getIntValue(obj, "type_id"));
      feed.setUserId(getStringValue(obj, "from_user"));
      feed.setUsername(getStringValue(obj, "from_name"));
      feed.setUserAvatar(WebServiceConfig.PROFILE_IMAGE_PREFIX + getStringValue(obj, "from_image"));
      feed.setItemId(getStringValue(obj, "item_id"));
      feed.setItemImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "item_image"));
      feed.setRecordId(getStringValue(obj, "record_id"));
      feed.setSeen(getIntValue(obj, "seen"));
      feed.setDate(AppUtil.timeAgo(context, getStringValue(obj, "date")));
      return feed;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static List<Transaction> parseListTransactionsFromJson(Context context, String json) {
    List<Transaction> list = new ArrayList<>();
    try {
      JSONObject entryObj = new JSONObject(json);
      JSONArray array = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);

      for (int i = 0; i < array.length(); i++) {
        JSONObject obj = array.getJSONObject(i);
        Transaction transaction = new Transaction();

        transaction.setItemID(getStringValue(obj, "item_id"));
        transaction.setItemTitle(getStringValue(obj, "item_title"));
        transaction.setItemImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(obj, "item_image"));
        transaction.setMoney(getIntValue(obj, "money"));
//                transaction.setTradeItem(getStringValue(obj, "trade_item"));
        transaction.setDate(getStringValue(obj, "date"));
        if (isJsonArray(obj, "trade_item")) {
          JSONArray arrItems = obj.getJSONArray("trade_item");
          for (int j = 0; j < arrItems.length(); j++) {
            JSONObject itemObj = arrItems.getJSONObject(j);
            Item item = new Item();
            item.setId(getStringValue(itemObj, "id"));
            item.setImage(WebServiceConfig.ITEM_IMAGE_PREFIX + getStringValue(itemObj, "image"));
            transaction.getArrTradeItem().add(item);
          }
        }

        list.add(transaction);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  public static String getRecoveredPasswordFromJson(String json) {
    try {
      JSONObject entryObj = new JSONObject(json);
      String pass = getStringValue(entryObj, WebServiceConfig.KEY_DATA);
      return pass;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static List<CreditPack> parseListCreditPackFromJson(String json) {
    List<CreditPack> list = new ArrayList<>();
    try {
      JSONObject entryObj = new JSONObject(json);
      JSONArray array = entryObj.getJSONArray(WebServiceConfig.KEY_DATA);

      for (int i = 0; i < array.length(); i++) {
        JSONObject obj = array.getJSONObject(i);
        CreditPack pack = new CreditPack();

        pack.setName(getStringValue(obj, "name"));
        pack.setPrice(getIntValue(obj, "value"));
        pack.setSkuID(getStringValue(obj, "skuid"));

        list.add(pack);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  private static boolean getBooleanValue(JSONObject obj, String name) {
    try {
      return obj.getBoolean(name);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private static String getStringValue(JSONObject obj, String key) {
    try {
      return obj.isNull(key) ? "" : obj.getString(key);
    } catch (JSONException e) {
      e.printStackTrace();
      return "";
    }
  }

  private static long getLongValue(JSONObject obj, String key) {
    try {
      return obj.isNull(key) ? 0L : obj.getLong(key);
    } catch (JSONException e) {
      e.printStackTrace();
      return 0L;
    }
  }

  private static int getIntValue(JSONObject obj, String key) {
    try {
      return obj.isNull(key) ? 0 : obj.getInt(key);
    } catch (JSONException e) {
      e.printStackTrace();
      return 0;
    }
  }

  private static Double getDoubleValue(JSONObject obj, String key) {
    double d = 0.0;
    try {
      return obj.isNull(key) ? d : obj.getDouble(key);
    } catch (JSONException e) {
      e.printStackTrace();
      return d;
    }
  }

  public static boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static boolean isJsonObject(JSONObject parent, String key) {
    try {
      JSONObject jObj = parent.getJSONObject(key);
    } catch (JSONException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static boolean isJsonArray(JSONObject parent, String key) {
    try {
      JSONArray jArray = parent.getJSONArray(key);
    } catch (JSONException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static boolean isDouble(String string) {
    try {
      Double.parseDouble(string);
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static boolean hasKey(JSONObject obj, String key) {
    try {
      obj.getString(key);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static String splitString(String string, String expression) {
    if (string == null) {
      return "";
    } else {
      String[] split = string.split(expression);
      return split[1];
    }
  }

}
