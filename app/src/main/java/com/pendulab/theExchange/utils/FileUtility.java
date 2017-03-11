package com.pendulab.theExchange.utils;

import com.pendulab.theExchange.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;


public final class FileUtility {

  // Enable strict mode/*enable chuc nang doc rss*/
  public static void enableStrictMode() {

    try {
      @SuppressWarnings("rawtypes")
      Class strictModeClass = Class.forName("android.os.StrictMode");
      @SuppressWarnings("rawtypes")
      Class strictModeThreadPolicyClass = Class
          .forName("android.os.StrictMode$ThreadPolicy");
      Object laxPolicy = strictModeThreadPolicyClass.getField("LAX").get(
          null);
      @SuppressWarnings("unchecked")
      Method method_setThreadPolicy = strictModeClass.getMethod(
          "setThreadPolicy", strictModeThreadPolicyClass);
      method_setThreadPolicy.invoke(null, laxPolicy);
    } catch (Exception e) {
    }
  }

  public static Bitmap getBitmapFromAssets(Context context, String fileName)
      throws IOException {

    AssetManager assetManager = context.getAssets();
    InputStream istr = assetManager.open(fileName);
    Bitmap bitmap = BitmapFactory.decodeStream(istr);

    return bitmap;
  }

  public static boolean check_exits_file(Context context, String filename) {
    String path = Environment.getExternalStorageDirectory()
        .getAbsolutePath() + "/BabyMusic/";
    File f = new File(path + "/", filename);
    if (f.exists()) {
      return true;
    }
    return false;
  }

  public int getImageIdFromName(Context context, String imageName)

  {
    String mDrawableName = imageName.substring(0, imageName.indexOf("."));
    int resID = context.getResources().getIdentifier(mDrawableName,
        "drawable", context.getPackageName());
    return resID;

  }

  /**
   * filename need external file
   *
   * @param context, filename
   */
  public static boolean storeMusictoSDcard(Context context, String filename) {
    File file = new File(Environment.getExternalStorageDirectory(),
        "/BabyMusic/");
    if (!file.exists()) {
      file.mkdirs();
    }
    String path = Environment.getExternalStorageDirectory()
        .getAbsolutePath() + "/BabyMusic/";

    File f = new File(path + "/", filename);
    if (f.exists()) {
      f.delete();
    }

    AssetManager assetManager = context.getAssets();
    InputStream istr;
    try {
      istr = assetManager.open(filename);
      FileOutputStream out = new FileOutputStream(f);
      byte[] buffer = new byte[1024];
      int length;
      while ((length = istr.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }
      out.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      return false;
    }
    return true;
  }

  /**
   * read file in asset
   */
  public String readFileTextfromAsset(Context context, String filename) {
    String text = "";
    try {
      AssetManager assetManager = context.getAssets();
      InputStream stream = assetManager.open(filename);
      int size = stream.available();
      byte[] buffer = new byte[size];
      stream.read(buffer);
      stream.close();
      text = new String(buffer);

      // Log.d("bac", "abc " + text);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return text;
  }

  public static Bitmap getBitmapfromUrlImage(String Url, int width, int height) {
    try {
      URL url = new URL(Url);
      HttpURLConnection connection = (HttpURLConnection) url
          .openConnection();
      connection.setDoInput(true);
      connection.connect();
      InputStream input = connection.getInputStream();
      Bitmap myBitmap = BitmapFactory.decodeStream(input);

      return Bitmap.createScaledBitmap(myBitmap, height, width, true);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * set ring tone
   */
  public static boolean setRingtone(Context context, String filename) {
    File file = new File(Environment.getExternalStorageDirectory(),
        "/myRingtoneFolder/Audio/");
    if (!file.exists()) {
      file.mkdirs();
    }

    String path = Environment.getExternalStorageDirectory()
        .getAbsolutePath() + "/myRingtoneFolder/Audio/";

    File f = new File(path + "/", filename);
    if (f.exists()) {
      f.delete();
    }

    AssetManager assetManager = context.getAssets();
    InputStream istr;
    try {
      istr = assetManager.open(filename);
      FileOutputStream out = new FileOutputStream(f);
      byte[] buffer = new byte[1024];
      int length;
      while ((length = istr.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }
      out.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      return false;
    }

    ContentValues values = new ContentValues();
    values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
    values.put(MediaStore.MediaColumns.TITLE, filename);
    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
    values.put(MediaStore.MediaColumns.SIZE, f.length());
    values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
    values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
    values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
    values.put(MediaStore.Audio.Media.IS_ALARM, false);
    values.put(MediaStore.Audio.Media.IS_MUSIC, false);

    Uri uri = MediaStore.Audio.Media.getContentUriForPath(f
        .getAbsolutePath());
    Uri newUri = context.getContentResolver().insert(uri, values);

    try {
      RingtoneManager.setActualDefaultRingtoneUri(context,
          RingtoneManager.TYPE_RINGTONE, newUri);

    } catch (Throwable t) {
      return false;
    }
    return true;
  }
}
