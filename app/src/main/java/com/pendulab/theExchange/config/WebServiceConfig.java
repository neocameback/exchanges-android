package com.pendulab.theExchange.config;

public class WebServiceConfig {
  public static int NETWORK_TIME_OUT = 100000;

  // result code for activity result
  public static int RESULT_OK = 1;

  public static String STRING_INVALID_TOKEN = "AccessToken invalid";

  public static String STRING_EXPIRE_TOKEN = "AccessToken expired";

  // ===================== DOMAIN =====================
  // TODO
  public static String FACEBOOK_AVATAR_URL = "https://graph.facebook.com/%s/picture?width=300&height=300";

  public static String PROTOCOL_HTTP = "http://192.168.100.2/";
//    public static String PROTOCOL_HTTP = "http://exchanges.local/";
//    public static String PROTOCOL_HTTP = "http://dev2.expandvn.com/";

  public static String PROTOCOL_HTTPS = "http://dev2.expandvn.com/";

  public static String TUTORIAL_IMAGE = PROTOCOL_HTTP + "uploads/tutorial/tutorial_item.png";

  public static final String APP_ADDRESS = PROTOCOL_HTTP + "index.php/Inkblot/";

  public static final String PATH_AUTHENTICATION = APP_ADDRESS + "authentication/";
  public static final String PATH_ITEM = APP_ADDRESS + "item/";
  public static final String PATH_USER = APP_ADDRESS + "user/";
  public static final String PATH_MESSAGE = APP_ADDRESS + "message/";
  public static final String PATH_OFFER = APP_ADDRESS + "offer/";
  public static final String PATH_TRANSACTION = APP_ADDRESS + "transaction/";


  public static final String GEOCODE_SEARCH_BY_COORDINATION = "http://maps.googleapis.com/maps/api/geocode/json?latlng= %s, %s &sensor=true";
  public static final String GEOCODE_SEARCH_BY_ADDRESS = "http://maps.google.com/maps/api/geocode/json?address=%s&sensor=false";

  public static final String URL_SHARE_ITEM = APP_ADDRESS + "preview/index/%s";
  public static final String URL_INVITE_FRIENDS = APP_ADDRESS + "preview/app";
  //=====================SYMBOL=============================
  public static final String COLON = ":";


  // ===================== WEB SERVICE LINK =====================

  public static final String PROFILE_IMAGE_PREFIX = PROTOCOL_HTTP + "uploads/users/";
  public static final String CATEGORY_IMAGE_PREFIX = PROTOCOL_HTTP + "uploads/categories/";
  public static final String ITEM_IMAGE_PREFIX = PROTOCOL_HTTP + "uploads/products/";

  public static final String URL_LOGIN = PATH_AUTHENTICATION + "login";
  public static final String URL_LOGIN_SOCIAL = PATH_AUTHENTICATION + "loginSocial";
  public static final String URL_REGISTER = PATH_AUTHENTICATION + "register";
  public static final String URL_REGISTER_SOCIAL = PATH_AUTHENTICATION + "registerSocial";
  public static final String URL_LOGOUT = PATH_AUTHENTICATION + "logout";
  public static final String URL_RESET_PASSWORD = PATH_AUTHENTICATION + "reset";
  public static final String URL_GET_PHONECODE = PATH_AUTHENTICATION + "phone_code";
  public static final String URL_VERIFY_ACCOUNT = PATH_AUTHENTICATION + "active";
  public static final String URL_UPDATE_REG_ID = PATH_AUTHENTICATION + "update_deviceID";

  public static final String URL_GET_CATEGORY = PATH_ITEM + "category";
  public static final String URL_UPLOAD_ITEM = PATH_ITEM + "upload";
  public static final String URL_UPDATE_ITEM = PATH_ITEM + "update";
  public static final String URL_GET_ITEMS = PATH_ITEM + "browser";
  public static final String URL_TOGGLE_WISH_LIST = PATH_ITEM + "wishlist";
  public static final String URL_GET_ITEM_DETAILS = PATH_ITEM + "detail";
  public static final String URL_GET_ITEM_LIKERS = PATH_ITEM + "user_wishlist";
  public static final String URL_GET_ITEM_COMMENTS = PATH_ITEM + "list_comment";
  public static final String URL_POST_ITEM_COMMENTS = PATH_ITEM + "comment";
  public static final String URL_REPORT_ITEM = PATH_ITEM + "report_item";
  public static final String URL_GET_INVENTORY = PATH_ITEM + "inventory/%s";
  public static final String URL_GET_WISHLIST = PATH_ITEM + "get_wishlist/%s";
  public static final String URL_ADD_WISHLIST_MANUAL = PATH_ITEM + "wishlist_manually";
  public static final String URL_DELETE_WISHLIST = PATH_ITEM + "delete_wishlist";
  public static final String URL_EDIT_WISHLIST_MANUAL = PATH_ITEM + "edit_wishlist_manually/%s";
  public static final String URL_DELETE_ITEM = PATH_ITEM + "delete";

  public static final String URL_MAKE_CONVERSATION = PATH_MESSAGE + "conversation";
  public static final String URL_GET_LIST_INBOX = PATH_MESSAGE + "all";
  public static final String URL_GET_MESSAGE_BY_CONVERSATION = PATH_MESSAGE + "get_message";
  public static final String URL_SEND_MESSAGE = PATH_MESSAGE + "send";
  public static final String URL_MOVE_TO_ARCHIVE = PATH_MESSAGE + "archieve";
  public static final String URL_DELETE_CONVERSATION = PATH_MESSAGE + "delete_conversation";
  public static final String URL_SYNC_MESSAGE = PATH_MESSAGE + "sync_message";

  public static final String URL_GET_USER = PATH_USER + "info/%s";
  public static final String URL_REPORT_USER = PATH_USER + "report";
  public static final String URL_BLOCK_USER = PATH_USER + "block";
  public static final String URL_UPDATE_USER = PATH_USER + "update";
  public static final String URL_RATE_USER = PATH_USER + "rate";
  public static final String URL_GET_RATE = PATH_USER + "get_rate";
  public static final String URL_LIST_RATE = PATH_USER + "list_rate";
  public static final String URL_GET_ACTIVITIES = PATH_USER + "activity";
  public static final String URL_SEARCH_USER = PATH_USER + "search";
  public static final String URL_CHANGE_PASS = PATH_USER + "changepass";
  public static final String URL_UPDATE_TUTORIAL = PATH_USER + "tutorial_finish";

  public static final String URL_MAKE_OFFER = PATH_OFFER + "made_offer";
  public static final String URL_ACCEPT_OFFER = PATH_OFFER + "accept_offer";
  public static final String URL_REJECT_OFFER = PATH_OFFER + "reject_offer";
  public static final String URL_GET_OFFER_BUY = PATH_OFFER + "offer_buy";
  public static final String URL_GET_OFFER_LIST = PATH_OFFER + "offer_sell";

  public static final String URL_REQUEST_VERIFY_TRANSACTION = PATH_TRANSACTION + "request_trade";
  public static final String URL_VERIFY_TRANSACTION = PATH_TRANSACTION + "verify_transaction";
  public static final String URL_GET_TRANSACTION_HISTORY = PATH_TRANSACTION + "history";
  public static final String URL_BUY_CREDIT = PATH_TRANSACTION + "buy_tradecredit";
  public static final String URL_GET_CREDIT_PACK = PATH_TRANSACTION + "get_creditpack";

  // ===================== WEB SERVICE KEY =====================
  public static final String KEY_STATUS = "status";
  public static final String KEY_DATA = "data";
  public static final String KEY_MESSAGE = "message";
  public static final String STATUS_SUCCESS = "success";
  public static final String KEY_HEADER = "Cookie";

}
