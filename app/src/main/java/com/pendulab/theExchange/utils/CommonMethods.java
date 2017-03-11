package com.pendulab.theExchange.utils;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;


public class CommonMethods {
  //	public static void showDialogue(String message, Context context) {
//		Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);
//		builder.setTitle(context.getString(R.string.app_name));
//		builder.setMessage(message);
//		builder.setCancelable(false);
//		builder.setPositiveButton("OK", null);
//		AlertDialog dialog = builder.create();
//		dialog.show();
//	}
  public static boolean validateEmail(String email) {
    final Pattern EMAIL_ADDRESS_PATTERN = Pattern
        .compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
            + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
            + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
    try {
      return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    } catch (NullPointerException exception) {
      return false;
    }
  }

  public static void BitmapImage(String userimage) {
    URL onLineURL;
    try {
      onLineURL = new URL(userimage.replaceAll(" ", "%20"));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
    int width = bm.getWidth();
    int height = bm.getHeight();
    float scaleWidth = ((float) newWidth) / width;
    float scaleHeight = ((float) newHeight) / height;
    // CREATE A MATRIX FOR THE MANIPULATION
    Matrix matrix = new Matrix();
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight);


    // RECREATE THE NEW BITMAP
    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    return resizedBitmap;
  }

  /**
   * Gets the last image id from the media store
   */
  public static String getLastImageId(Activity activity) {
    final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
    final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
    Cursor imageCursor = activity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
    if (imageCursor.moveToFirst()) {
      int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
      String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
      Log.d("Camera", "getLastImageId::id " + id);
      Log.d("Camera", "getLastImageId::path " + fullPath);
      //imageCursor.close();
      return fullPath;
    } else {
      return "";
    }
  }

}
