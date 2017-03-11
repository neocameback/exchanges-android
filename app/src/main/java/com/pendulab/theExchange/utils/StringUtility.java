/*
 * Name: $RCSfile: StringUtility.java,v $
 * Version: $Revision: 1.1 $
 * Date: $Date: Oct 31, 2011 1:54:00 PM $
 *
 * Copyright (C) 2011 COMPANY_NAME, Inc. All rights reserved.
 */

package com.pendulab.theExchange.utils;

import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * StringUtility class
 *
 * @author Lemon
 */
public final class StringUtility {

  private static final int FULL_HOUR_MIN_SEC_STRING_LENGTH = 8; // 00:00:00

  /**
   * Check Edit Text input string
   */
  public static boolean isEmpty(EditText editText) {
    if (editText == null
        || editText.getEditableText() == null
        || editText.getEditableText().toString().trim()
        .equalsIgnoreCase("")) {
      return true;
    }
    return false;
  }

  /**
   * Check input string
   */
  public static boolean isEmpty(String editText) {
    if (editText == null || editText.trim().equalsIgnoreCase("")) {
      return true;
    }
    return false;
  }

  /**
   * Merge all elements of a string array into a string
   */
  public static String join(String[] strings, String separator) {
    StringBuffer sb = new StringBuffer();
    int max = strings.length;
    for (int i = 0; i < max; i++) {
      if (i != 0)
        sb.append(separator);
      sb.append(strings[i]);
    }
    return sb.toString();
  }

  /**
   * Convert current date time to string
   */
  public static String convertNowToFullDateString() {
    SimpleDateFormat dateformat = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");
    dateformat.setTimeZone(TimeZone.getTimeZone("GMT"));

    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    return dateformat.format(calendar.getTime());
  }

  /**
   * Initial sync date string
   */
  public static String initDateString() {
    return "1900-01-01 09:00:00";
  }

  /**
   * Convert a string divided by ";" to multiple xmpp users
   */
  public static String[] convertStringToXmppUsers(String userString) {
    return userString.split(";");
  }

  /**
   * get Unique Random String
   */

  public static String getUniqueRandomString() {

    return String.valueOf(System.currentTimeMillis());

  }

  // public static String getUniqueRandomString() {
  //
  // return UUID.randomUUID().toString();
  //
  // }
  public static String getTimeString(long millis) {
    StringBuffer buf = new StringBuffer();

    int hours = (int) (millis / (1000 * 60 * 60));
    int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
    int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

    buf.append(String.format("%02d", hours)).append(":")
        .append(String.format("%02d", minutes)).append(":")
        .append(String.format("%02d", seconds));

    return buf.toString();
  }

  public static String milliSecondsToTimer(long milliseconds) {
    String finalTimerString = "";
    String secondsString = "";

    // Convert total duration into time
    int hours = (int) (milliseconds / (1000 * 60 * 60));
    int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
    int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
    // Add hours if there
    if (hours > 0) {
      finalTimerString = hours + ":";
    }

    // Prepending 0 to seconds if it is one digit
    if (seconds < 10) {
      secondsString = "0" + seconds;
    } else {
      secondsString = "" + seconds;
    }

    finalTimerString = finalTimerString + minutes + ":" + secondsString;

    // return timer string
    return finalTimerString;
  }

  public static int getProgressPercentage(long currentDuration,
                                          long totalDuration) {
    Double percentage = (double) 0;

    long currentSeconds = (int) (currentDuration / 1000);
    long totalSeconds = (int) (totalDuration / 1000);

    // calculating percentage
    percentage = (((double) currentSeconds) / totalSeconds) * 100;

    // return percentage
    return percentage.intValue();
  }

  public static int progressToTimer(int progress, int totalDuration) {
    int currentDuration = 0;
    totalDuration = (int) (totalDuration / 1000);
    currentDuration = (int) ((((double) progress) / 100) * totalDuration);

    // return current duration in milliseconds
    return currentDuration * 1000;
  }

  public static String hourMinSecondToHourMin(String s) {
    if (s == null || s.length() < 4) {
      return "";
    } else {
      if (s.length() >= FULL_HOUR_MIN_SEC_STRING_LENGTH) {
        return s.substring(0, s.length() - 3);
      } else {
        return s;
      }
    }
  }

  public static String emptyStringIfNull(String s) {
    return (s == null || s.equalsIgnoreCase("null") ? "" : s);
  }

  public static String getNameFromEmail(String email) {
    if (email.contains("@")) {
      return email.split("@")[0];
    }
    return "";
  }

}
