package com.pendulab.theExchange.utils;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class DialogUtility {

  private static AlertDialog alert;
  public static ProgressDialog progressDialog;
  public static int progressDialogRequest = 0;
  public static Dialog dialog;


  public static void closeProgressDialog() {
    // synchronized () {
    // progressDialogRequest--;
    // }

//		if (progressDialogRequest == 0) {
//			if (progressDialog != null) {
//				// fix bug force close here
//				try {
//					progressDialog.cancel();
//					progressDialog = null;
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//			}
//		}

    if (dialog != null) {
      try {
        dialog.cancel();
        dialog = null;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Open progress dialog
   */
//    public static void showProgressDialog(Context context) {
//        if (progressDialog == null) {
//            progressDialog = new ProgressDialog(context,
//                    R.style.ProgressDialogTheme);
//            progressDialog.setCancelable(false);
//            progressDialog
//                    .setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//            progressDialog.show();
//        }
//    }
  //
  public static void showProgressDialog(Context activity) {
    closeProgressDialog();

    dialog = new Dialog(activity);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

    dialog.setContentView(R.layout.layout_circular_progress_bar);

    dialog.setCancelable(false);
    try {
      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void showProgressDialog(Context activity, String message) {
    closeProgressDialog();

    dialog = new Dialog(activity);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

    dialog.setContentView(R.layout.layout_circular_progress_bar);
    TextView tvMessage = (TextView) dialog.findViewById(R.id.lblMessage);
    tvMessage.setText(message);

    dialog.setCancelable(false);
    try {
      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Show option dialog
   */
  public static void showOptionDialog(Context context, int titleId,
                                      String[] items, int positiveLabelId,
                                      DialogInterface.OnClickListener itemOnClick,
                                      DialogInterface.OnClickListener positiveOnClick) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(context.getString(titleId));
    builder.setItems(items, itemOnClick);
    builder.setPositiveButton(context.getString(positiveLabelId),
        positiveOnClick);
    alert = builder.create();
    alert.show();
  }

  /**
   * Close dialog
   */
  public static void closeDialog() {
    if (alert != null && alert.isShowing()) {
      alert.dismiss();
      alert = null;
    }
  }

  public static void showDialog(Context context, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(context.getString(R.string.app_name));
    builder.setMessage(message);
    builder.setPositiveButton("OK", null);
    AlertDialog alert = builder.create();
    alert.show();
  }

  /**
   * Show confirm dialog
   */
  public static void showDialog(Context context, String title,
                                String message, String positiveLabel, String negativeLabel,
                                DialogInterface.OnClickListener positiveOnClick,
                                DialogInterface.OnClickListener negativeOnClick) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setPositiveButton(positiveLabel, positiveOnClick);
    builder.setNegativeButton(negativeLabel, negativeOnClick);
    AlertDialog alert = builder.create();
    alert.show();
  }

  /**
   * Show confirm dialog
   */
  public static void showDialog(Context context, int messageId,
                                int positiveLabelId, int negativeLabelId,
                                DialogInterface.OnClickListener positiveOnClick,
                                DialogInterface.OnClickListener negativeOnClick) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(context.getString(R.string.app_name));
    builder.setMessage(context.getString(messageId));
    builder.setPositiveButton(context.getString(positiveLabelId),
        positiveOnClick);
    builder.setNegativeButton(context.getString(negativeLabelId),
        negativeOnClick);
    AlertDialog alert = builder.create();
    alert.show();
  }

  /**
   * Show single option dialog
   */
  public static void showSingleOptionDialog(Context context, int titleId,
                                            String[] items, int positiveLabelId,
                                            DialogInterface.OnClickListener itemOnClick,
                                            DialogInterface.OnClickListener positiveOnClick) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(context.getString(titleId));
    builder.setSingleChoiceItems(items, 0, itemOnClick);
    builder.setPositiveButton(context.getString(positiveLabelId),
        positiveOnClick);
    AlertDialog alert = builder.create();
    alert.show();
  }

  /**
   * Show single option dialog
   */
  public static void showSingleOptionDialog(Context context, int titleId,
                                            String[] items, int selectPosition, int positiveLabelId,
                                            DialogInterface.OnClickListener itemOnClick,
                                            DialogInterface.OnClickListener positiveOnClick) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(context.getString(titleId));
    builder.setSingleChoiceItems(items, selectPosition, itemOnClick);
    builder.setPositiveButton(context.getString(positiveLabelId),
        positiveOnClick);
    AlertDialog alert = builder.create();
    alert.show();
  }

  /**
   * Show simple option dialog
   */
  public static void showSimpleOptionDialog(Context context, int titleId,
                                            String[] items, int positiveLabelId,
                                            DialogInterface.OnClickListener itemOnClick,
                                            DialogInterface.OnClickListener positiveOnClick) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(context.getString(titleId));
    builder.setItems(items, itemOnClick);
    builder.setPositiveButton(context.getString(positiveLabelId),
        positiveOnClick);
    AlertDialog alert = builder.create();
    alert.show();
  }

  public static void showShortToast(Context context, String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }

  public static void showLongToast(Context context, String message) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
  }

  public static void showMessageDialog(Context context, String message, final DialogListener listener) {
    final Dialog dialog = new Dialog(context);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.layout_message_dialog);
//        dialog.getWindow().setBackgroundDrawableResource(
//                android.R.color.transparent);

    dialog.setCancelable(false);
    TextView lblMessage = (TextView) dialog.findViewById(R.id.lblMessage);
    TextView lblClose = (TextView) dialog.findViewById(R.id.lblClose);

    lblMessage.setText(message);
    lblClose.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        if (listener != null) {
          listener.onClose(dialog);
        }
//                handler.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        listener.onClose(dialog);
//
//                    }
//                }, 100);
      }
    });
    try {
      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void showOptionDialog(Context context, String message, String positiveText, String negativeText, boolean cancelable, final DialogOptionListener listener) {
    final Dialog dialog = new Dialog(context);
    final Handler handler = new Handler();

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.layout_option_dialog);
//        dialog.getWindow().setBackgroundDrawableResource(
//                android.R.color.transparent);
    dialog.setCancelable(cancelable);
    TextView lblMessage = (TextView) dialog.findViewById(R.id.lblMessage);
    TextView lblPositive = (TextView) dialog.findViewById(R.id.lblPositive);
    TextView lblNegative = (TextView) dialog.findViewById(R.id.lblNegative);

    lblMessage.setText(message);
    lblPositive.setText(positiveText);
    lblNegative.setText(negativeText);

    lblPositive.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        handler.postDelayed(new Runnable() {

          @Override
          public void run() {
            listener.onPositive(dialog);

          }
        }, 100);
      }
    });

    lblNegative.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        handler.postDelayed(new Runnable() {

          @Override
          public void run() {
            listener.onNegative(dialog);

          }
        }, 100);
      }
    });
    try {
      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void showPurchaseOptionDialog(Context context, String message, final PurChaseOptionsListener listener) {
    final Dialog dialog = new Dialog(context);
    final Handler handler = new Handler();

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialog_purchase_options);
//        dialog.getWindow().setBackgroundDrawableResource(
//                android.R.color.transparent);
    dialog.setCancelable(true);
    TextView lblMessage = (TextView) dialog.findViewById(R.id.lblMessage);
    LinearLayout llGooglePlay = (LinearLayout) dialog.findViewById(R.id.llGooglePlay);
    LinearLayout llPaypal = (LinearLayout) dialog.findViewById(R.id.llPaypal);

    Glide.with(context).load(R.drawable.icon_google_play).into((ImageView) dialog.findViewById(R.id.ivGooglePlay));
    Glide.with(context).load(R.drawable.icon_paypal).into((ImageView) dialog.findViewById(R.id.ivPaypal));

    lblMessage.setText(message);

    llGooglePlay.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        handler.postDelayed(new Runnable() {

          @Override
          public void run() {
            listener.onClickGooglePlay(dialog);

          }
        }, 100);
      }
    });

    llPaypal.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        handler.postDelayed(new Runnable() {

          @Override
          public void run() {
            listener.onClickPaypal(dialog);

          }
        }, 100);
      }
    });
    try {
      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void showLocationOptionsDialog(Context context, final LocationOptionsListener listener) {
    final Dialog dialog = new Dialog(context);
    final Handler handler = new Handler();

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialog_location_options);
//        dialog.getWindow().setBackgroundDrawableResource(
//                android.R.color.transparent);
    dialog.setCancelable(true);
    LinearLayout llAnywhere = (LinearLayout) dialog.findViewById(R.id.llAnywhere);
    LinearLayout llSelectLocation = (LinearLayout) dialog.findViewById(R.id.llSelectLocation);

    llAnywhere.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        handler.postDelayed(new Runnable() {

          @Override
          public void run() {
            listener.onClickAnywhere(dialog);

          }
        }, 100);
      }
    });

    llSelectLocation.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        handler.postDelayed(new Runnable() {

          @Override
          public void run() {
            listener.onClickSelectLocation(dialog);

          }
        }, 100);
      }
    });
    try {
      dialog.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public interface DialogListener {
    void onClose(Dialog dialog);
  }

  public interface DialogOptionListener {
    void onPositive(Dialog dialog);

    void onNegative(Dialog dialog);
  }

  public interface PurChaseOptionsListener {
    void onClickGooglePlay(Dialog dialog);

    void onClickPaypal(Dialog dialog);
  }

  public interface LocationOptionsListener {
    void onClickAnywhere(Dialog dialog);

    void onClickSelectLocation(Dialog dialog);
  }

}
