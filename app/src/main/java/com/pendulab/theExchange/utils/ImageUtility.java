package com.pendulab.theExchange.utils;

import com.pendulab.theExchange.config.GlobalValue;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Random;

public final class ImageUtility extends Activity {

  public Bitmap getBitmapFromAssets(Context context, String fileName)
      throws IOException {
    AssetManager assetManager = context.getAssets();
    InputStream istr = assetManager.open(fileName);
    Bitmap bitmap = BitmapFactory.decodeStream(istr);

    return bitmap;
  }

  /**
   * filename need external file
   */
  public static boolean storeImage(String Url, String filename) {
    // get path to external storage (SD card)
    String musicStoragePath = Environment.getExternalStorageDirectory()
        + "/music/";
    ;
    File StorageDir = new File(musicStoragePath);

    // create storage directories, if they don't exist
    if (!StorageDir.exists()) {
      StorageDir.mkdirs();
    }
    try {
      File file = new File(StorageDir.toString(), filename);

      if (file.exists()) {
        file.delete();
      }

      FileOutputStream out = new FileOutputStream(file);
      InputStream is = (InputStream) new URL(Url).getContent();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = is.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }

      out.flush();
      out.close();
      Log.d("asd", "asd: finish ");
    } catch (FileNotFoundException e) {
      Log.w("TAG", "Error saving music file: " + e.getMessage());
      return false;
    } catch (IOException e) {
      Log.w("TAG", "Error saving music file: " + e.getMessage());
      return false;
    }
    return true;
  }

  public static String encodeTobase64(Bitmap image) {
    Bitmap immagex = image;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] b = baos.toByteArray();
    String imageEncoded = Base64.encodeToString(b, Base64.NO_WRAP);


    Log.e("BASE64_ENCODED", imageEncoded);
    return imageEncoded;
  }

  public static Bitmap decodeBase64(String input) {
    byte[] decodedByte = Base64.decode(input, 0);
    return BitmapFactory
        .decodeByteArray(decodedByte, 0, decodedByte.length);
  }

  public static String getRealPathFromURI(Uri contentURI, Activity context) {
    String[] projection = {MediaStore.Images.Media.DATA};
    @SuppressWarnings("deprecation")
    Cursor cursor = context.managedQuery(contentURI, projection, null,
        null, null);
    if (cursor == null)
      return null;
    int column_index = cursor
        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    if (cursor.moveToFirst()) {
      String s = cursor.getString(column_index);
      // cursor.close();
      return s;
    }
    // cursor.close();
    return null;
  }

  public final static Bitmap getRotateBitmap(Context context, Bitmap bm,
                                             String imagePath, boolean isResize) {
    int rotate = 0;
    Matrix matrix = new Matrix();

    int reqWidth = isResize ? 110 : 410;
    int reqHeight = isResize ? 110 : 410;

    try {
      File imageFile = new File(imagePath);
      ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
      int orientation = exif.getAttributeInt(
          ExifInterface.TAG_ORIENTATION,
          ExifInterface.ORIENTATION_NORMAL);

      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_270:
          rotate = 270;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          rotate = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_90:
          rotate = 90;
          break;
      }

      matrix.postRotate(rotate);
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true; //just decode no create bitmap
      options.inPreferredConfig = Bitmap.Config.RGB_565;
//            options.inSampleSize = 2;
      BitmapFactory.decodeFile(imagePath, options); //decode file to options

      int sampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
      options.inSampleSize = sampleSize;
      options.inJustDecodeBounds = false; //now set deocode bounds false to create a bitmap
      bm = BitmapFactory.decodeFile(imagePath, options);

      int width = bm.getWidth();
      int height = bm.getHeight();

      // if rotate != 0 need to re-rotate it
      if (rotate != 0)
        bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

      if (isResize) {
        bm = getResizedBitmap(bm, 200, 200);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return bm;
  }

  public static int calculateInSampleSize(
      BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) > reqHeight
          && (halfWidth / inSampleSize) > reqWidth) {
        inSampleSize *= 2;
      }
    }
    return inSampleSize;
  }

  public final static Bitmap getResizedBitmap(Bitmap bm, int newHeight,
                                              int newWidth) {
    int width = bm.getWidth();
    int height = bm.getHeight();
    Log.d("BITMAP", width + "-" + height + "");
    float scaleWidth = ((float) newWidth) / width;
    float scaleHeight = ((float) newHeight) / height;
    // CREATE A MATRIX FOR THE MANIPULATION
    Matrix matrix = new Matrix();
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight);
    // RECREATE THE NEW BITMAP
    bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

    return bm;

  }

  public static File saveImageToSDCard(Bitmap bitmap) {
    try {
      Random r = new Random();
      long values = new Date().getTime();
      File myDir = new File(Environment.getExternalStorageDirectory()
          + "/" + GlobalValue.FOLDER_IMAGE);
      myDir.mkdirs();
      if (myDir.exists()) {
        Log.d("Exist", "FOLDER EXISTS");
      } else {
        boolean success = myDir.mkdir();
        if (success) {
          Log.d("File", "File Created");
        } else {
          throw new RuntimeException(
              "File Error in writing new folder");
        }
      }
      String filename = "image" + String.valueOf(values) + ".jpg";
      System.out.println("filename: " + filename);
      File file = new File(myDir, filename);
      FileOutputStream fOut = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
      fOut.flush();
      fOut.close();
      return file;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
