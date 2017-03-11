package com.pendulab.theExchange.base;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.utils.DialogUtility;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.net.URL;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Anh Ha Nguyen on 10/21/2015.
 */
public class BaseShareActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

  private UiLifecycleHelper uiHelper;

  /* Request code used to invoke sign in user interactions. */
  public static final int RC_SIGN_IN = 300;

  public GoogleApiClient mGoogleApiClient;

  public boolean mIntentInProgress;

  public String rootFolder;

  public int currentAction;
  public static final int ACTION_SHARE = 1;
  public static final int ACTION_INVITE = 2;
  public static final int SHARE_GOOGLE_REQUEST_CODE = 0;
  public final String KEY_FB_GESTURE = "com.facebook.platform.extra.COMPLETION_GESTURE";

  public Bitmap bitmap = null;

  // 03-16 10:10:25.004: I/ACTIVITY_RESULT(27291): 64207---1

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);

    uiHelper = new UiLifecycleHelper(this, null);
    uiHelper.onCreate(savedInstanceState);

    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this).addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN).build();


    TwitterAuthConfig authConfig = new TwitterAuthConfig("z5al00gGfcaRZiGImUVyMcOlZ", "q2Gy5lM0peXfjOm1kFmZVCM7odOqaerZHGSObUORpjin6d6jxI");
    Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());

    rootFolder = Environment.getExternalStorageDirectory() + "/"
        + getString(R.string.app_name) + "/";
    File folder = new File(rootFolder);
    if (!folder.exists()) {
      folder.mkdirs();
    }
  }

  @Override
  protected void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
  }

  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    uiHelper.onResume();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    // TODO Auto-generated method stub
    super.onSaveInstanceState(outState);
    uiHelper.onSaveInstanceState(outState);
  }

  @Override
  protected void onActivityResult(int requestCode, final int resultCode,
                                  Intent data) {
    // super.onActivityResult(requestCode, resultCode, data);

    Log.i("ACTIVITY_RESULT", requestCode + "--" + resultCode);

    if (requestCode == SHARE_GOOGLE_REQUEST_CODE && resultCode == RESULT_OK) {
//			if (currentAction == ACTION_SHARE) {
//				DialogUtility.showShortToast(self,
//						getString(R.string.message_share_successfully));
//			} else if (currentAction == ACTION_INVITE) {
//				DialogUtility.showShortToast(self,
//						getString(R.string.message_invite_successfully));
//			}
    }

    uiHelper.onActivityResult(requestCode, resultCode, data,
        new FacebookDialog.Callback() {
          @Override
          public void onError(FacebookDialog.PendingCall pendingCall,
                              Exception error, Bundle data) {
            Log.e("Activity",
                String.format("Error: %s", error.toString()));
            DialogUtility.showMessageDialog(self, getString(R.string.message_server_error), null);
          }

          @Override
          public void onComplete(
              FacebookDialog.PendingCall pendingCall, Bundle data) {
            for (String key : data.keySet()) {
              Log.d("FACEBOOK_BUNDLE", key + "--" + data.get(key));
            }
            // com.facebook.platform.extra.COMPLETION_GESTURE--cancel
            // com.facebook.platform.extra.DID_COMPLETE--true

            if (resultCode == RESULT_OK) {
//                            if (data.getString(KEY_FB_GESTURE)
//                                    .equalsIgnoreCase("post")) {
//                                if (currentAction == ACTION_SHARE) {

//									DialogUtility
//											.showShortToast(
//													self,
//													getString(R.string.message_share_successfully));
//                                } else if (currentAction == ACTION_INVITE) {
////									DialogUtility
////											.showShortToast(
////													self,
////													getString(R.string.message_invite_successfully));
//                                }
//                            }
            }
          }
        });

  }

  @Override
  protected void onPause() {
    // TODO Auto-generated method stub
    super.onPause();
    uiHelper.onPause();
  }

  @Override
  protected void onStop() {
    // TODO Auto-generated method stub
    super.onStop();
    if (mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  @Override
  protected void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();
    uiHelper.onDestroy();
    mGoogleApiClient = null;
  }

  @Override
  public void onConnectionFailed(ConnectionResult result) {
    if (!mIntentInProgress && result.hasResolution()) {
      try {
        mIntentInProgress = true;
        startIntentSenderForResult(result.getResolution()
            .getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
      } catch (IntentSender.SendIntentException e) {
        // The intent was canceled before it was sent. Return to the
        // default
        // state and attempt to connect to get an updated
        // ConnectionResult.
        mIntentInProgress = false;
        mGoogleApiClient.connect();
      }
    }
  }

  @Override
  public void onConnected(Bundle connectionHint) {
    // We've resolved any connection errors. mGoogleApiClient can be used to
    // access Google APIs on behalf of the user.
  }

  @Override
  public void onConnectionSuspended(int arg0) {
    mGoogleApiClient.connect();
  }

  public void onShareFacebook(View v, Item item) {

    currentAction = ACTION_SHARE;
    final String name = item.getTitle();
    final String itemLink = String.format(WebServiceConfig.URL_SHARE_ITEM, item.getId());
    final String description = item.getDescription();
    final String caption = getString(R.string.app_name);
    final String imageUrl = item.getImage();

    if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
        FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
      // Publish the post using the Share Dialog
      FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
          self).setName(name).setDescription(description).setPicture(imageUrl)
          .setCaption(caption).setLink(itemLink).build();
      // shareDialog.present();
      uiHelper.trackPendingDialogCall(shareDialog.present());

    } else {

      Session.openActiveSession(self, true, new Session.StatusCallback() {

        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
          if (session != null && session.isOpened()) {
            shareFacebookViaFeedDialog(name, description, caption,
                itemLink, imageUrl);
          }
        }

      });
    }
  }

  private void shareFacebookViaFeedDialog(String name, String description,
                                          String caption, String link, String imageUrl) {
    Bundle params = new Bundle();
    params.putString("name", name);
    params.putString("caption", caption);
    params.putString("picture", imageUrl);
    params.putString("description", description);
    params.putString("link", link);

    WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(self,
        Session.getActiveSession(), params)).setOnCompleteListener(
        new WebDialog.OnCompleteListener() {

          @Override
          public void onComplete(Bundle values,
                                 FacebookException error) {
            if (error == null) {
              // When the story is posted, echo the success
              // and the post Id.
              final String postId = values.getString("post_id");
              if (postId != null) {
                Toast.makeText(self, "Share successfully",
                    Toast.LENGTH_SHORT).show();
              } else {
              }
            } else if (error instanceof FacebookOperationCanceledException) {
              // User clicked the "x" button
              // Toast.makeText(self.getApplicationContext(),
              // "Error sharing", Toast.LENGTH_SHORT).show();
            } else {
              // Generic, ex: network error
              Toast.makeText(self.getApplicationContext(),
                  "Error sharing", Toast.LENGTH_SHORT).show();
            }
          }

        }).build();
    feedDialog.show();
  }

  public void onShareGoogle(View v, Item item) {
    currentAction = ACTION_SHARE;
    try {

      String itemLink = String.format(WebServiceConfig.URL_SHARE_ITEM, item.getId());

      Intent shareIntent = ShareCompat.IntentBuilder.from(this)
          .setSubject(item.getTitle()).setType("text/plain")
          .setText(itemLink).getIntent()
          .setPackage("com.google.android.apps.plus");

      startActivityForResult(shareIntent, SHARE_GOOGLE_REQUEST_CODE);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void onShareTwitter(View v, Item item) {
    try {
      TweetComposer.Builder builder = new TweetComposer.Builder(this)
          .text(item.getTitle())
          .url(new URL(String.format(WebServiceConfig.URL_SHARE_ITEM, item.getId())));
      builder.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void onShareEmail(View v, Item item) {
//        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                "mailto","abc@gmail.com", null));
    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
    emailIntent.setData(Uri.parse("mailto:"));
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
    emailIntent.putExtra(Intent.EXTRA_TEXT, String.format(WebServiceConfig.URL_SHARE_ITEM, item.getId()));
    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{});
    startActivity(Intent.createChooser(emailIntent, getString(R.string.share_via_email)));
  }

  public void onShareLink(View v, Item item) {

//        File shareFile = saveCurrentImageCache(item);
//        Uri uri = Uri.parse("file://" + shareFile);

    String itemLink = String.format(WebServiceConfig.URL_SHARE_ITEM, item.getId());
    // String itemLink = "http://projectemplate.com/content/index.html";

    Intent i = new Intent(Intent.ACTION_SEND);
    i.setType("image/*");
    // i.putExtra(Intent.EXTRA_EMAIL , new
    // String[]{getHomeActivity().myAccount.getmEmail()});
    i.putExtra(Intent.EXTRA_SUBJECT, item.getTitle() + " - sent via "
        + getString(R.string.app_name));
    i.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name)
        + System.getProperty("line.separator") + itemLink);
//        i.putExtra(Intent.EXTRA_STREAM, uri);
    try {
      startActivity(Intent.createChooser(i, "Share using"));
    } catch (android.content.ActivityNotFoundException ex) {
      DialogUtility.showLongToast(self,
          "There are no email clients installed.");
    }
  }

  public void onInviteFacebook(View v) {

    currentAction = ACTION_INVITE;
    final String itemLink = WebServiceConfig.URL_INVITE_FRIENDS;
    final String caption = getString(R.string.app_name);
    final String name = getString(R.string.app_name);


    if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
        FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
      // Publish the post using the Share Dialog
      FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
          self)
          .setCaption(caption).setLink(itemLink).build();
      // shareDialog.present();
      uiHelper.trackPendingDialogCall(shareDialog.present());

    } else {

      Session.openActiveSession(self, true, new Session.StatusCallback() {

        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
          if (session != null && session.isOpened()) {
            inviteFacebookViaFeedDialog(name, caption,
                itemLink);
          }
        }

      });
    }
  }

  private void inviteFacebookViaFeedDialog(String name,
                                           String caption, String link) {
    Bundle params = new Bundle();
    params.putString("name", name);
    params.putString("caption", caption);
    params.putString("link", link);

    WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(self,
        Session.getActiveSession(), params)).setOnCompleteListener(
        new WebDialog.OnCompleteListener() {

          @Override
          public void onComplete(Bundle values,
                                 FacebookException error) {
            if (error == null) {
              // When the story is posted, echo the success
              // and the post Id.
              final String postId = values.getString("post_id");
              if (postId != null) {
                Toast.makeText(self, "Share successfully",
                    Toast.LENGTH_SHORT).show();
              } else {
              }
            } else if (error instanceof FacebookOperationCanceledException) {
              // User clicked the "x" button
              // Toast.makeText(self.getApplicationContext(),
              // "Error sharing", Toast.LENGTH_SHORT).show();
            } else {
              // Generic, ex: network error
              Toast.makeText(self.getApplicationContext(),
                  "Error sharing", Toast.LENGTH_SHORT).show();
            }
          }

        }).build();
    feedDialog.show();
  }

  public void onInviteGoogle(View v) {
    currentAction = ACTION_SHARE;
    try {

      String itemLink = WebServiceConfig.URL_INVITE_FRIENDS;

      Intent shareIntent = ShareCompat.IntentBuilder.from(this)
          .setSubject(getString(R.string.app_name)).setType("text/plain")
          .setText(itemLink).getIntent()
          .setPackage("com.google.android.apps.plus");

      startActivityForResult(shareIntent, SHARE_GOOGLE_REQUEST_CODE);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void onInviteEmail(View v) {
//        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                "mailto","abc@gmail.com", null));
    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
    emailIntent.setData(Uri.parse("mailto:"));
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
    emailIntent.putExtra(Intent.EXTRA_TEXT, WebServiceConfig.URL_SHARE_ITEM);
    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{});
    startActivity(Intent.createChooser(emailIntent, getString(R.string.share_via_email)));
  }


}
