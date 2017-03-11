/*
 * Name: $RCSfile: SmartLog.java,v $
 * Version: $Revision: 1.1 $
 * Date: $Date: May 27, 2011 3:27:39 PM $
 *
 * Copyright (C) 2011 COMPANY NAME, Inc. All rights reserved.
 */

package com.pendulab.theExchange.utils;

import org.apache.http.NameValuePair;

import android.util.Log;

import java.util.List;


/**
 * Smart Log
 *
 * @author cuongvm6037
 */
public final class SmartLog {

  private static String TAG = "mp3_log";

  private SmartLog() {
  }

  /**
   * Call SmartLog.log
   */
  public static void log(String tag, String msg) {
    Log.d(tag, msg);
  }

  /**
   * Display all POST parameters
   */
  public static void logPostParameters(List<NameValuePair> parameters) {

    for (NameValuePair nv : parameters) {
      log("KeyValue", nv.getName() + " = " + nv.getValue());
    }

  }

  /**
   * Display all POST parameters
   */
  public static void logPostParameters(String tag,
                                       List<NameValuePair> parameters) {
    log(tag, "POST Parameters");
    logPostParameters(parameters);

  }

  /**
   * Display information of a friend
   *
   * @param friend
   */

  // public static void logImgModelInfo(ImgModel img)
  // {
  // if (BffApplication.DEBUG_MODE)
  // {
  // String TAG = "IMG INFORMATION";
  // }
  // }

  /**
   * Call SmartLog.logD
   */
  public static void logD(String msg) {

    Log.d(TAG, msg);

  }

  /**
   * Call SmartLog.logE
   */
  public static void logE(String msg) {
    Log.e(TAG, msg);

  }

  /**
   * Call SmartLog.logI
   */
  public static void logI(String msg) {
    Log.i(TAG, msg);

  }

  public static void logLongString(String TAG, String str) {
    if (str.length() > 4000) {
      Log.i(TAG, str.substring(0, 4000));
      logLongString(TAG, str.substring(4000));
    } else
      Log.i(TAG, str);
  }

}
