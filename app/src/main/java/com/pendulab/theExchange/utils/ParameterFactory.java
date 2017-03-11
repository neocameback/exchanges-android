package com.pendulab.theExchange.utils;


import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Category;
import com.pendulab.theExchange.model.Message;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class ParameterFactory {

//    public static String createGetUserProfile(String userId){
//        String getUrl = WebServiceAPI.GET_EMPLOYEE_PROFILE + "?userId=" + userId;
//        getUrl = getUrl.replaceAll(" ", "%20");
//        getUrl = getUrl.replaceAll("[\\t\\n\\r]", "%20");
//
//        return getUrl;
//
//    }

  public static List<NameValuePair> createLoginParams(String username, String pass, String deviceID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("username", username));
    list.add(new BasicNameValuePair("password", pass));
    list.add(new BasicNameValuePair("device_id", deviceID));

    return list;
  }

  public static List<NameValuePair> createLoginSocialParams(String socialId, String userType, String deviceID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("social_id", socialId));
    list.add(new BasicNameValuePair("device_id", deviceID));

    return list;
  }

  public static List<NameValuePair> createVerifyAccountParams(String userID, String code) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("id", userID));

    list.add(new BasicNameValuePair("generate_code", code));
    return list;
  }

  //
  public static String createGeocodeSearchCoordination(double latitude, double longitude) {
    String getUrl = String.format(WebServiceConfig.GEOCODE_SEARCH_BY_COORDINATION, String.valueOf(latitude), String.valueOf(longitude));
    getUrl = getUrl.replaceAll(" ", "%20");
    getUrl = getUrl.replaceAll("[\\t\\n\\r]", "%20");
    return getUrl;
  }

  public static String createAddressSearchCoordination(String address) {
    String getUrl = String.format(WebServiceConfig.GEOCODE_SEARCH_BY_ADDRESS, address);
    getUrl = getUrl.replaceAll(" ", "%20");
    getUrl = getUrl.replaceAll("[\\t\\n\\r]", "%20");
    return getUrl;
  }

  public static List<NameValuePair> createGetItemsParams(String keyword, String categoryId, int sort, double lat, double lng, int distance, int minPrice, int maxPrice, String start) {
    List<NameValuePair> list = new ArrayList<>();

    if (keyword != null && !keyword.equalsIgnoreCase("")) {
      categoryId = Category.ALL_CATEGORIES + "";
    }

    list.add(new BasicNameValuePair("keyword", keyword));
    list.add(new BasicNameValuePair("category_id", categoryId));
    list.add(new BasicNameValuePair("sort", sort + ""));
    list.add(new BasicNameValuePair("lat", lat + ""));
    list.add(new BasicNameValuePair("lng", lng + ""));
    list.add(new BasicNameValuePair("distance", distance + ""));

    if (minPrice > 0) {
      list.add(new BasicNameValuePair("min_price", minPrice + ""));
    }

    if (maxPrice > 0) {
      list.add(new BasicNameValuePair("max_price", maxPrice + ""));
    }

    list.add(new BasicNameValuePair("start", start));


    return list;
  }

  public static List<NameValuePair> createToggleWishListParams(String itemID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("item_id", itemID));

    return list;
  }

  public static List<NameValuePair> createGetItemDetailsParams(String itemID, double lat, double lng) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("item_id", itemID));
    list.add(new BasicNameValuePair("lat", lat + ""));
    list.add(new BasicNameValuePair("lng", lng + ""));


    return list;
  }

  public static List<NameValuePair> createGetLikersParams(String itemID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("item_id", itemID));

    return list;
  }

  public static List<NameValuePair> createPostCommentParams(String itemID, String commnent) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("item_id", itemID));
    list.add(new BasicNameValuePair("comment", commnent));

    return list;
  }

  public static List<NameValuePair> createReportItemParams(String itemID, String reasonId) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("item_id", itemID));
    list.add(new BasicNameValuePair("reason", reasonId));

    return list;
  }

  public static String createGetUserInfoParam(String userID) {
    String getUrl = String.format(WebServiceConfig.URL_GET_USER, userID);
    getUrl = getUrl.replaceAll(" ", "%20");
    getUrl = getUrl.replaceAll("[\\t\\n\\r]", "%20");
    return getUrl;
  }

  public static String createGetInventoryParam(String userID) {
    String getUrl = String.format(WebServiceConfig.URL_GET_INVENTORY, userID);
    getUrl = getUrl.replaceAll(" ", "%20");
    getUrl = getUrl.replaceAll("[\\t\\n\\r]", "%20");
    return getUrl;
  }

  public static String createGetWishlistParam(String userID) {
    String getUrl = String.format(WebServiceConfig.URL_GET_WISHLIST, userID);
    getUrl = getUrl.replaceAll(" ", "%20");
    getUrl = getUrl.replaceAll("[\\t\\n\\r]", "%20");
    return getUrl;
  }

  public static List<NameValuePair> createBlockReportUserParam(String userID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("user_id", userID));

    return list;
  }

  public static List<NameValuePair> createAddWishlistManualParam(String title, String minPrice, String maxPrice) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("title", title));
//        if (!minPrice.equalsIgnoreCase("")) {
//            if (Integer.parseInt(minPrice) > 0) {
    list.add(new BasicNameValuePair("min_price", minPrice));
//            }
//        }
//        if (!maxPrice.equalsIgnoreCase("")) {
//            if (Integer.parseInt(maxPrice) > 0) {
    list.add(new BasicNameValuePair("max_price", maxPrice));

//            }
//        }

    return list;
  }

  public static String createEditWishlistManualParam(String itemID) {
    String getUrl = String.format(WebServiceConfig.URL_EDIT_WISHLIST_MANUAL, itemID);
    getUrl = getUrl.replaceAll(" ", "%20");
    getUrl = getUrl.replaceAll("[\\t\\n\\r]", "%20");
    return getUrl;
  }

  public static List<NameValuePair> createDeleteWishlistManualParams(String recordID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("id", recordID));

    return list;
  }

  public static List<NameValuePair> createMakeConversationParam(String itemID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("item_id", itemID));

    return list;
  }

  public static List<NameValuePair> createGetListMessageParam(String conversationID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("conversation_id", conversationID));

    return list;
  }

  public static List<NameValuePair> createSendMessageParam(Message message) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("message", message.getMessage()));
    list.add(new BasicNameValuePair("item_id", message.getItemID()));
    list.add(new BasicNameValuePair("user_receive", message.getUserReceive()));
    list.add(new BasicNameValuePair("conversation_id", message.getConversationID()));

    return list;
  }

  public static List<NameValuePair> createMoveToArchiveParam(String conversationID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("conversation_id", conversationID));

    return list;
  }

  public static List<NameValuePair> createUpdateRegIDParam(String gcmID, String deviceID, String deviceType) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("regID", gcmID));
    list.add(new BasicNameValuePair("device_id", deviceID));
    list.add(new BasicNameValuePair("device_type", "0"));

    return list;
  }

  public static List<NameValuePair> createSyncMessageParam(String lastID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("id", lastID));

    return list;
  }

  public static List<NameValuePair> createMakeOfferParam(int money, String tradeItem, String itemID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("money", money + ""));
    list.add(new BasicNameValuePair("trade_item", tradeItem));
    list.add(new BasicNameValuePair("item_id", itemID));

    return list;
  }

  public static List<NameValuePair> createGetListOffersParam(String itemID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("item_id", itemID));

    return list;
  }

  public static List<NameValuePair> createAccepRefuseOfferParam(String id) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("id", id));

    return list;
  }

  public static List<NameValuePair> createRequestVerifyTransactionParam(String itemID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("item_id", itemID));

    return list;
  }

  public static List<NameValuePair> createVerifyTransactionParam(String transactionID, String code) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("id", transactionID));
    list.add(new BasicNameValuePair("code", code));

    return list;
  }

  public static List<NameValuePair> createGetRateParam(String userID) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("user_id", userID));

    return list;
  }

  public static List<NameValuePair> createRateSellerParam(String userID, float rating, String review) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("user_id", userID));
    list.add(new BasicNameValuePair("rate", rating + ""));
    list.add(new BasicNameValuePair("review", review));

    return list;
  }

  public static List<NameValuePair> createGetActivitiesParam(String start) {
    List<NameValuePair> list = new ArrayList<>();

    list.add(new BasicNameValuePair("start", start + ""));

    return list;
  }

  public static List<NameValuePair> createGetTransactionHistoryParam(int type) {
    List<NameValuePair> list = new ArrayList<>();
    list.add(new BasicNameValuePair("type", type + ""));
    return list;
  }

  public static List<NameValuePair> createSearchUserParam(String keyword) {
    List<NameValuePair> list = new ArrayList<>();
    list.add(new BasicNameValuePair("keyword", keyword));
    return list;
  }

  public static List<NameValuePair> createChangePassParam(String oldPass, String newPass) {
    List<NameValuePair> list = new ArrayList<>();
    list.add(new BasicNameValuePair("old_pass", oldPass));
    list.add(new BasicNameValuePair("new_pass", newPass));
    return list;
  }

  public static List<NameValuePair> createResetPassword(String username) {
    List<NameValuePair> list = new ArrayList<>();
    list.add(new BasicNameValuePair("username", username));
    return list;
  }

  public static List<NameValuePair> createBuyCreditParam(int credit) {
    List<NameValuePair> list = new ArrayList<>();
    list.add(new BasicNameValuePair("trade_credit", credit + ""));
    return list;
  }

  public static List<NameValuePair> createUpdateTutorialParam(int tutorial) {
    List<NameValuePair> list = new ArrayList<>();
    list.add(new BasicNameValuePair("tutorial", tutorial + ""));
    return list;
  }


}
