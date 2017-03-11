package com.pendulab.theExchange.config;


import com.google.gson.Gson;

import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.model.LocationObj;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesManager {
  private String TAG = getClass().getSimpleName();

  private final String MY_PREFERENCES = "com.pendulab.inkblot.preference";

  private static SharedPreferencesManager instance;

  private Context context;

  // =============================================================

  public static final String GCM_ID = "GCM_ID";

  public static final String AFTER_FIRST_TIME = "AFTER_FIRST_TIME";
  public static final String MY_ACCOUNT = "MY_ACCOUNT";
  public static final String SESSION_ID = "SESSION_ID";
  public static final String PHONE_CODES = "PHONE_CODES";
  public static final String MY_COUNTRY_CODE = "MY_COUNTRY_CODE";
  public static final String REGISTER_ACCOUNT = "REGISTER_ACCOUNT";
  public static final String CATEGORIES = "CATEGORIES";
  public static final String BROWSE_CATEGORY_FILTER = "BROWSE_CATEGORY_FILTER";
  public static final String BROWSE_SORT_FILTER = "BROWSE_SORT_FILTER";
  public static final String BROWSE_MIN_PRICE = "BROWSE_MIN_PRICE";
  public static final String BROWSE_MAX_PRICE = "BROWSE_MAX_PRICE";
  public static final String BROWSE_RADIUS = "BROWSE_RADIUS";
  public static final String BROWSE_LOCATION = "BROWSE_LOCATION";
  public static final String BROWSE_KEYWORD = "BROWSE_KEYWORD";

  private SharedPreferencesManager() {
  }

  /**
   * Constructor
   */
  public static SharedPreferencesManager getInstance(Context context) {
    if (instance == null) {
      instance = new SharedPreferencesManager();
      instance.context = context;
    }
    return instance;
  }

  public SharedPreferencesManager(Context context) {
    this.context = context;
  }

  //======================== MANAGER FUNCTIONS ==========================


  public void saveSessionID(String sessionID) {
    putStringValue(SESSION_ID, sessionID);
  }

  public String getSessionID() {
    return getStringValue(SESSION_ID);
  }

  public void clearSessionID() {
    putStringValue(SESSION_ID, "");
  }

  public void saveUserInfo(Account user) {
    Gson gson = new Gson();
    String json = gson.toJson(user);
    putStringValue(MY_ACCOUNT, json);
  }

  public Account getUserInfo() {
    String json = getStringValue(MY_ACCOUNT);

    if (json.equals("")) {
      return null;
    }

    Gson gson = new Gson();

    Account user = gson.fromJson(json, Account.class);
    return user;
  }

  public void clearUser() {
    putStringValue(MY_ACCOUNT, "");

  }

  //======Register Account function
  public void saveRegisterAccount(Account user) {
    Gson gson = new Gson();
    String json = gson.toJson(user);
    putStringValue(REGISTER_ACCOUNT, json);
  }

  public Account getRegisterAccount() {
    String json = getStringValue(REGISTER_ACCOUNT);

    if (json.equals("")) {
      return null;
    }

    Gson gson = new Gson();

    Account user = gson.fromJson(json, Account.class);
    return user;
  }


  public void saveBrowseLocation(LocationObj loc) {
    Gson gson = new Gson();
    String json = gson.toJson(loc);
    putStringValue(BROWSE_LOCATION, json);
  }

  public LocationObj getBrowseLocation() {
    String json = getStringValue(BROWSE_LOCATION);

    if (json.equals("")) {
      return null;
    }

    Gson gson = new Gson();

    LocationObj loc = gson.fromJson(json, LocationObj.class);
    return loc;
  }

  // ======================== UTILITY FUNCTIONS ========================

  /**
   * Save a long integer to SharedPreferences
   */
  public void putLongValue(String key, long n) {
    SharedPreferences pref = context.getSharedPreferences(
        MY_PREFERENCES, 0);
    SharedPreferences.Editor editor = pref.edit();
    editor.putLong(key, n);
    editor.commit();
  }

  /**
   * Read a long integer to SharedPreferences
   */
  public long getLongValue(String key) {
    SharedPreferences pref = context.getSharedPreferences(
        MY_PREFERENCES, 0);
    return pref.getLong(key, 0);
  }

  /**
   * Save an integer to SharedPreferences
   */
  public void putIntValue(String key, int n) {
    SharedPreferences pref = context.getSharedPreferences(
        MY_PREFERENCES, 0);
    SharedPreferences.Editor editor = pref.edit();
    editor.putInt(key, n);
    editor.commit();
  }

  /**
   * Read an integer to SharedPreferences
   */
  public int getIntValue(String key) {
    // SmartLog.log(TAG, "Get integer value");
    SharedPreferences pref = context.getSharedPreferences(
        MY_PREFERENCES, 0);
    return pref.getInt(key, 0);
  }

  /**
   * Save an string to SharedPreferences
   */
  public void putStringValue(String key, String s) {
    // SmartLog.log(TAG, "Set string value");
    SharedPreferences pref = context.getSharedPreferences(
        MY_PREFERENCES, 0);
    SharedPreferences.Editor editor = pref.edit();
    editor.putString(key, s);
    editor.commit();
  }

  /**
   * Read an string to SharedPreferences
   */
  public String getStringValue(String key) {
    SharedPreferences pref = context.getSharedPreferences(
        MY_PREFERENCES, 0);
    return pref.getString(key, "");
  }

  /**
   * Read an string to SharedPreferences
   */
  public String getStringValue(String key, String defaultValue) {
    SharedPreferences pref = context.getSharedPreferences(
        MY_PREFERENCES, 0);
    return pref.getString(key, defaultValue);
  }

  /**
   * Save an boolean to SharedPreferences
   */
  public void putBooleanValue(String key, Boolean b) {
    SharedPreferences pref = context.getSharedPreferences(
        MY_PREFERENCES, 0);
    SharedPreferences.Editor editor = pref.edit();
    editor.putBoolean(key, b);
    editor.commit();
  }

  /**
   * Read an boolean to SharedPreferences
   */
  public boolean getBooleanValue(String key) {
    SharedPreferences pref = context.getSharedPreferences(
        MY_PREFERENCES, 0);
    return pref.getBoolean(key, false);
  }

  /**
   * Save an float to SharedPreferences
   */
  public void putFloatValue(String key, float f) {
    SharedPreferences pref = context.getSharedPreferences(
        MY_PREFERENCES, 0);
    SharedPreferences.Editor editor = pref.edit();
    editor.putFloat(key, f);
    editor.commit();
  }

  /**
   * Read an float to SharedPreferences
   */
  public float getFloatValue(String key) {
    SharedPreferences pref = context.getSharedPreferences(
        MY_PREFERENCES, 0);
    return pref.getFloat(key, 0.0f);
  }

}
