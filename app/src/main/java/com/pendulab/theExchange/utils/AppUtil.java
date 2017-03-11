/*
 * Name: $RCSfile: AppUtil.java,v $
 * Version: $Revision: 1.1 $
 * Date: $Date: Apr 19, 2011 11:05:43 AM $
 *
 * Copyright (C) 2011 COMPANY NAME, Inc. All rights reserved.
 */

package com.pendulab.theExchange.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import com.pendulab.theExchange.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @author cuongvm6037
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class AppUtil {

  private static final long WEEK_MILLISECONDS = 6 * 24 * 60 * 60 * 1000;
  private static SimpleDateFormat sdfDateFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
  private static SimpleDateFormat sdfDateHour = new SimpleDateFormat("yy/MM/dd HH:mm");
  private static SimpleDateFormat sdfNear = new SimpleDateFormat("EEE, HH:mm");
  private static SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");

  /**
   * Check if external storage exists such as SD card
   */
  public static boolean hasExternalStorage() {
    try {
      return android.os.Environment.getExternalStorageState().equals(
          android.os.Environment.MEDIA_MOUNTED);
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  /**
   * Show an alert dialog box
   */
  public static void alert(Context context, String s) {
    if (context != null) {
      // AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
      // alertbox.setTitle(context.getString(R.string.app_full_name));
      // alertbox.setMessage(s);
      // alertbox.setNeutralButton("OK",
      // new DialogInterface.OnClickListener() {
      // public void onClick(DialogInterface arg0, int arg1)
      // {}
      // });
      // alertbox.show();
      DialogUtility.showMessageDialog(context, s, null);
//			Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Show an alert dialog box
   */
  public static void alert(Context context, String s,
                           DialogInterface.OnClickListener okOnclick) {
    if (context != null) {
      AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
      alertbox.setTitle(context.getString(R.string.app_name));
      alertbox.setMessage(s);
      alertbox.setNeutralButton("OK", okOnclick);
      alertbox.show();
    }
  }

  /**
   * Show an alert dialog box
   */
  public static void alert(Context context, String title, String message,
                           DialogInterface.OnClickListener okOnclick) {
    if (context != null) {
      AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
      alertbox.setTitle(title);
      alertbox.setMessage(message);
      alertbox.setNeutralButton("OK", okOnclick);
      alertbox.show();
    }
  }

  /**
   * Show an alert dialog box
   */
  public static void alert(Context context, CharSequence s) {
    AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
    alertbox.setTitle(context.getString(R.string.app_name));
    alertbox.setMessage(s);
    alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface arg0, int arg1) {
      }
    });
    alertbox.show();
  }

  /**
   * Notify network unavailable
   */
  public static void alertNetworkUnavailable(Context context) {
    if (context != null) {
      alert(context, "Network unavailable. Please turn it on.");
    }
  }

  /**
   * Notify network unavailable
   */
  public static void alertNetworkUnavailableCommon(Context context) {
    if (context != null) {

      alert(context, "Network is unavailable. Please try again later.");
    }
  }

  /**
   * Notify network unavailable
   */
  public static void alertNetworkUnavailableShare(Context context) {
    if (context != null) {
      alert(context,
          "Network is unavailable. Unsuccessful sharing, please try again.");
    }
  }

  public static void alertNoOfflineData(Context context) {
    if (context != null) {

      alert(context, "No offline data found!");
    }
  }

  public static void alertViewOfflineData(Context context) {
    if (context != null) {

      alert(context, "You are viewing offline data.");
    }
  }

  public static void alertNoDataFound(Context context) {
    if (context != null) {
      alert(context, "No data found.");
    }
  }

  /**
   * Notify network unavailable
   */
  public static void alertNetworkUnavailableNormal(Context context) {
    if (context != null) {
      alert(context, "Network is unavailable.");
    }
  }

  public static void alertNetworkUnavailableNormal(Context context,
                                                   DialogInterface.OnClickListener okOnclick) {
    if (context != null) {
      alert(context, "Network is unavailable.", okOnclick);
    }
  }

  public static String convertMinutesToHoursMinuteSecond(long minute) {

    long millis = minute * 60 * 1000;

    String hms = String.format(
        "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis)
            % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(millis)
            % TimeUnit.MINUTES.toSeconds(1));

    return hms;
  }

  public static String convertMillisToHoursMinuteSecond(long millis) {

    String hms = String.format(
        "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis)
            % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(millis)
            % TimeUnit.MINUTES.toSeconds(1));

    return hms;
  }

  public static Date convertStringToDate(String strDate, String pattern) {
    Date convertDate = null;
    SimpleDateFormat sf = new SimpleDateFormat(pattern);
    try {
      convertDate = sf.parse(strDate);
      String newDateString = sf.format(strDate);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return convertDate;
  }

  public static long convertTimeToLong(String time) {
    SimpleDateFormat sfHourMinuteSecond = new SimpleDateFormat("HH:mm:ss");

    try {
      return sfHourMinuteSecond.parse(time).getTime();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static String convertLongToTimeString(long dateInMillis) {

    SimpleDateFormat sfHourMinuteSecond = new SimpleDateFormat("HH:mm");

    try {
      return sfHourMinuteSecond.format(new Date(dateInMillis));
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  /**
   * Notify server error
   */
  public static void alertServerError(Context context) {
    if (context != null) {
      alert(context, "There is an error from server, please try again.");
    }
  }

  public static boolean isMyServiceRunning(Class<?> serviceClass,
                                           Context context) {
    ActivityManager manager = (ActivityManager) context
        .getSystemService(Context.ACTIVITY_SERVICE);
    for (RunningServiceInfo service : manager
        .getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  public static void setListViewHeightBasedOnChildren(ListView listView) {
    ListAdapter mAdapter = listView.getAdapter();

    int totalHeight = 0;

    for (int i = 0; i < mAdapter.getCount(); i++) {
      View mView = mAdapter.getView(i, null, listView);

      mView.measure(
          MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),

          MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

      totalHeight += mView.getMeasuredHeight();
      Log.w("LISTVIEW_HEIGHT" + i, String.valueOf(totalHeight));

    }

    ViewGroup.LayoutParams params = listView.getLayoutParams();
    params.height = totalHeight
        + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
    listView.setLayoutParams(params);
    listView.requestLayout();

  }

  public static int getDeviceScreenWidth(Activity context) {
    DisplayMetrics metrics = new DisplayMetrics();
    context.getWindowManager().getDefaultDisplay().getMetrics(metrics);

    return metrics.widthPixels;
  }

  public static int getDeviceScreenHeight(Activity context) {
    DisplayMetrics metrics = new DisplayMetrics();
    context.getWindowManager().getDefaultDisplay().getMetrics(metrics);

    return metrics.heightPixels;
  }

  public static int getDimensionInPixel(Activity context, int demensionId) {
    return (int) (context.getResources().getDimension(demensionId) / context.getResources().getDisplayMetrics().density);
  }

  public static void turnGPSOn(Context context) {
    Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
    intent.putExtra("enabled", true);
    context.sendBroadcast(intent);

    String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    if (!provider.contains("gps")) { //if gps is disabled
      final Intent poke = new Intent();
      poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
      poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
      poke.setData(Uri.parse("3"));
      context.sendBroadcast(poke);
    }
  }

  // automatic turn off the gps
  public static void turnGPSOff(Context context) {
    String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    if (provider.contains("gps")) { //if gps is enabled
      final Intent poke = new Intent();
      poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
      poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
      poke.setData(Uri.parse("3"));
      context.sendBroadcast(poke);
    }
  }

  public static void supportSwipeRefreshLayout(AbsListView lv, int firstVisible, int lastVisible, int totalItemCount, SwipeRefreshLayout refreshLayout) {

    boolean enable = false;

    if (lv != null && lv.getChildCount() > 0) {
      // check if the first item of the list is visible
      boolean firstItemVisible = (firstVisible == 0);
      // check if the top of the first item is visible
      boolean topOfFirstItemVisible = lv.getChildAt(0).getTop() == 0;
      // enabling or disabling the refresh layout
      enable = firstItemVisible && topOfFirstItemVisible;
    }

    refreshLayout.setEnabled(enable);
  }

  public static String timeAgo(Context context, String date) {
    try {
      SimpleDateFormat sdfUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      sdfUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
      SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      sdfLocal.setTimeZone(TimeZone.getDefault());

      long timeInMillis = sdfLocal.parse(sdfLocal.format(sdfUTC.parse(date))).getTime();
      long numSecond = (new Date().getTime() - timeInMillis) / 1000;

      if (numSecond < 60) {
        return context.getString(R.string.just_now);
      }

      long numMinute = numSecond / 60;
      if (numMinute < 60) {
        return (numMinute == 1 ? numMinute + " " + context.getString(R.string.minute) : numMinute + " " + context.getString(R.string.minutes)) + " " + context.getString(R.string.ago);
      }

      long numHour = numMinute / 60;
      if (numHour < 24) {
        return (numHour == 1 ? numHour + " " + context.getString(R.string.hour) : numHour + " " + context.getString(R.string.hours)) + " " + context.getString(R.string.ago);
      }

      long numDay = numHour / 24;
      if (numDay < 7) {
        return (numDay == 1 ? numDay + " " + context.getString(R.string.day) : numDay + " " + context.getString(R.string.days)) + " " + context.getString(R.string.ago);
      }

      if (numDay < 30) {
        long numWeek = numDay / 7;
        return (numWeek == 1 ? numWeek + " " + context.getString(R.string.week) : numWeek + " " + context.getString(R.string.weeks)) + " " + context.getString(R.string.ago);
      }

      long numMonth = numDay / 30;
      if (numMonth < 12) {
        return (numMonth == 1 ? numMonth + " " + context.getString(R.string.month) : numMonth + " " + context.getString(R.string.months)) + " " + context.getString(R.string.ago);
      }

      long numYear = numMonth / 12;
      return (numYear == 1 ? numYear + " " + context.getString(R.string.year) : numYear + " " + context.getString(R.string.years)) + " " + context.getString(R.string.ago);


    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  public static String getDateForMessageItem(Context context, String datetime) {

    try {
      Calendar calendar = Calendar.getInstance();

      sdfDateFull.setTimeZone(TimeZone.getTimeZone("UTC"));
      if (sdfDate.format(sdfDateFull.parse(datetime)).equalsIgnoreCase(sdfDate.format(calendar.getTime()))) {
        return context.getString(R.string.today) + ", " + sdfHour.format(sdfDateFull.parse(datetime));
      } else {
        long currentMilli = Calendar.getInstance().getTimeInMillis();
        calendar.setTimeInMillis(sdfDateFull.parse(datetime).getTime());
        if (currentMilli - calendar.getTimeInMillis() > WEEK_MILLISECONDS) {
          return (sdfDateHour.format(calendar.getTime()));
        } else {
          return (sdfNear.format(calendar.getTime()));
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  public static LatLngBounds boundsWithCenterAndLatLngDistance(LatLng center, float latDistanceInMeters, float lngDistanceInMeters) {

    final double ASSUMED_INIT_LATLNG_DIFF = 1.0;
    final float ACCURACY = 0.01f;

    latDistanceInMeters /= 2;
    lngDistanceInMeters /= 2;
    LatLngBounds.Builder builder = LatLngBounds.builder();
    float[] distance = new float[1];
    {
      boolean foundMax = false;
      double foundMinLngDiff = 0;
      double assumedLngDiff = ASSUMED_INIT_LATLNG_DIFF;
      do {
        Location.distanceBetween(center.latitude, center.longitude, center.latitude, center.longitude + assumedLngDiff, distance);
        float distanceDiff = distance[0] - lngDistanceInMeters;
        if (distanceDiff < 0) {
          if (!foundMax) {
            foundMinLngDiff = assumedLngDiff;
            assumedLngDiff *= 2;
          } else {
            double tmp = assumedLngDiff;
            assumedLngDiff += (assumedLngDiff - foundMinLngDiff) / 2;
            foundMinLngDiff = tmp;
          }
        } else {
          assumedLngDiff -= (assumedLngDiff - foundMinLngDiff) / 2;
          foundMax = true;
        }
      } while (Math.abs(distance[0] - lngDistanceInMeters) > lngDistanceInMeters * ACCURACY);
      LatLng east = new LatLng(center.latitude, center.longitude + assumedLngDiff);
      builder.include(east);
      LatLng west = new LatLng(center.latitude, center.longitude - assumedLngDiff);
      builder.include(west);
    }
    {
      boolean foundMax = false;
      double foundMinLatDiff = 0;
      double assumedLatDiffNorth = ASSUMED_INIT_LATLNG_DIFF;
      do {
        Location.distanceBetween(center.latitude, center.longitude, center.latitude + assumedLatDiffNorth, center.longitude, distance);
        float distanceDiff = distance[0] - latDistanceInMeters;
        if (distanceDiff < 0) {
          if (!foundMax) {
            foundMinLatDiff = assumedLatDiffNorth;
            assumedLatDiffNorth *= 2;
          } else {
            double tmp = assumedLatDiffNorth;
            assumedLatDiffNorth += (assumedLatDiffNorth - foundMinLatDiff) / 2;
            foundMinLatDiff = tmp;
          }
        } else {
          assumedLatDiffNorth -= (assumedLatDiffNorth - foundMinLatDiff) / 2;
          foundMax = true;
        }
      } while (Math.abs(distance[0] - latDistanceInMeters) > latDistanceInMeters * ACCURACY);
      LatLng north = new LatLng(center.latitude + assumedLatDiffNorth, center.longitude);
      builder.include(north);
    }
    {
      boolean foundMax = false;
      double foundMinLatDiff = 0;
      double assumedLatDiffSouth = ASSUMED_INIT_LATLNG_DIFF;
      do {
        Location.distanceBetween(center.latitude, center.longitude, center.latitude - assumedLatDiffSouth, center.longitude, distance);
        float distanceDiff = distance[0] - latDistanceInMeters;
        if (distanceDiff < 0) {
          if (!foundMax) {
            foundMinLatDiff = assumedLatDiffSouth;
            assumedLatDiffSouth *= 2;
          } else {
            double tmp = assumedLatDiffSouth;
            assumedLatDiffSouth += (assumedLatDiffSouth - foundMinLatDiff) / 2;
            foundMinLatDiff = tmp;
          }
        } else {
          assumedLatDiffSouth -= (assumedLatDiffSouth - foundMinLatDiff) / 2;
          foundMax = true;
        }
      } while (Math.abs(distance[0] - latDistanceInMeters) > latDistanceInMeters * ACCURACY);
      LatLng south = new LatLng(center.latitude - assumedLatDiffSouth, center.longitude);
      builder.include(south);
    }
    return builder.build();
  }

  public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
    for (Map.Entry<T, E> entry : map.entrySet()) {
      if (value.equals(entry.getValue())) {
        return entry.getKey();
      }
    }
    return null;
  }
}
