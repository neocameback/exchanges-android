package com.pendulab.theExchange.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import com.bumptech.glide.Glide;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.PhoneCodeAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.SharedPreferencesManager;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.model.PhoneCode;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.net.HttpMultiPart;
import com.pendulab.theExchange.utils.CircleTransformation;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.GPSTracker;
import com.pendulab.theExchange.utils.ImageUtility;
import com.pendulab.theExchange.utils.StringUtility;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 5/27/2015.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

  private ImageView ivAvatar;

  private EditText etUsername, etPassword, etPhoneNumber;
  private Spinner spPhoneCode;
  private List<PhoneCode> arrPhoneCode;
  private PhoneCodeAdapter phoneCodeAdapter;

  private RelativeLayout rlFacebook, rlGoogle;

  private static final int SELECT_PICTURE = 101;
  private static final int CAMERA_REQUEST_CODE = 104;

  private MultipartEntity reqEntity;
  private File imageFile;
  private String imagePath;
  private Bitmap bitmap;
  private boolean isRegistering;

  private boolean isFetching;
  private File uploadFile;

  private GPSTracker gpsTracker;

  public static final int RC_SIGN_IN = 300;
  public GoogleApiClient mGoogleApiClient;
  public boolean mIntentInProgress;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_register);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.signup));

    initUI();
    initData();
    initControl();

    gpsTracker = new GPSTracker(self);
    gpsTracker.getLocation();

    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this).addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN).build();


    fbClearToken(self);
    Log.e(this.getClass().getSimpleName(), printKeyHash(self));

  }


  public static String printKeyHash(Activity context) {
    PackageInfo packageInfo;
    String key = null;
    try {

      // getting application package name, as defined in manifest
      String packageName = context.getApplicationContext()
          .getPackageName();

      // Retriving package info
      packageInfo = context.getPackageManager().getPackageInfo(
          packageName, PackageManager.GET_SIGNATURES);

      Log.e("Package Name=", context.getApplicationContext()
          .getPackageName());

      for (Signature signature : packageInfo.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        key = new String(Base64.encode(md.digest(), 0));

        // String key = new String(Base64.encodeBytes(md.digest()));
        Log.e("Key Hash=", key);

      }
    } catch (PackageManager.NameNotFoundException e1) {
      Log.e("Name not found", e1.toString());
    } catch (NoSuchAlgorithmException e) {
      Log.e("No such an algorithm", e.toString());
    } catch (Exception e) {
      Log.e("Exception", e.toString());
    }

    return key;
  }

  private void initUI() {
    ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
    etUsername = (EditText) findViewById(R.id.etUsername);
    etPassword = (EditText) findViewById(R.id.etPassword);
    etPhoneNumber = (EditText) findViewById(R.id.etPhone);
    spPhoneCode = (Spinner) findViewById(R.id.spnPhoneCode);
    rlFacebook = (RelativeLayout) findViewById(R.id.rlFacebook);
    rlGoogle = (RelativeLayout) findViewById(R.id.rlGoogle);
  }

  private void initData() {
    arrPhoneCode = new ArrayList<>();
//        for(int i=0;i<100; i++){
//            PhoneCode p = new PhoneCode();
//            p.setCountry("Vietnam");
//            p.setCode("+84");
//            arrPhoneCode.add(p);
//        }
    phoneCodeAdapter = new PhoneCodeAdapter(self, arrPhoneCode);
    spPhoneCode.setAdapter(phoneCodeAdapter);

    getPhoneCode();
  }

  private void initControl() {
    ivAvatar.setOnClickListener(this);
    rlFacebook.setOnClickListener(this);
    rlGoogle.setOnClickListener(this);

    spPhoneCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });

  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mGoogleApiClient = null;
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_activity_signup, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_login) {
      gotoActivity(self, LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void onClickDone(View v) {
    if (validateSignup()) {
      doSignup();
    }
  }

  @Override
  public void onClick(View view) {

    if (view == ivAvatar) {
      showPhotoOptionsDialog();
      return;
    }
    if (view == rlFacebook) {
      performFacebookLogin();
      return;
    }
    if (view == rlGoogle) {
      if (!mGoogleApiClient.isConnected()) {
        mIntentInProgress = false;
        mGoogleApiClient.connect();
        DialogUtility.showProgressDialog(self);
      } else {
        mIntentInProgress = false;
        mGoogleApiClient.reconnect();
        DialogUtility.showProgressDialog(self);
      }
      return;
    }

  }

  private void getPhoneCode() {
    if (preferences.getStringValue(SharedPreferencesManager.PHONE_CODES).length() > 0) {
      List<PhoneCode> list = CommonParser.parseListPhonecodeFromJson(preferences.getStringValue(SharedPreferencesManager.PHONE_CODES));
      arrPhoneCode.addAll(list);
      phoneCodeAdapter.notifyDataSetChanged();
      setSpinner();
    } else {
      AsyncHttpGet asyncGet = new AsyncHttpGet(self, new AsyncHttpResponse() {
        @Override
        public void before() {

        }

        @Override
        public void after(int statusCode, String response) {
          parseResponseStatus(statusCode, response, new NetBaseListener() {
            @Override
            public void onRequestSuccess(String json) {
              preferences.putStringValue(SharedPreferencesManager.PHONE_CODES, json);
              List<PhoneCode> list = CommonParser.parseListPhonecodeFromJson(json);
              arrPhoneCode.addAll(list);
              phoneCodeAdapter.notifyDataSetChanged();
              setSpinner();
            }

            @Override
            public void onRequestError(int message) {

            }
          });
        }
      }, null);
      asyncGet.execute(WebServiceConfig.URL_GET_PHONECODE);
    }
  }

  private void setSpinner() {
    String myCountry = preferences.getStringValue(SharedPreferencesManager.MY_COUNTRY_CODE);
    for (PhoneCode code : arrPhoneCode) {
      if (code.getLocale().equalsIgnoreCase(myCountry)) {
        spPhoneCode.setSelection(arrPhoneCode.indexOf(code));
      }
    }
  }

  private void showPhotoOptionsDialog() {
    AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
    myAlertDialog.setTitle(getString(R.string.set_pictures_options_title));
    myAlertDialog.setMessage(getString(R.string.set_pictures_options_message));
    myAlertDialog.setPositiveButton(getString(R.string.from_gallery),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface arg0, int arg1) {
            Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_PICTURE);
          }
        });

    myAlertDialog.setNegativeButton(getString(R.string.from_camera),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface arg0, int arg1) {
            Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
          }
        });
    myAlertDialog.show();
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {


    if (resultCode == RESULT_OK) {
      switch (requestCode) {

        case SELECT_PICTURE:
          if (null == data)
            return;
          Uri selectedImage = data.getData();
          String filePath = ImageUtility.getRealPathFromURI(selectedImage,
              this);
          bitmap = null;
          bitmap = ImageUtility.getRotateBitmap(getApplicationContext(), bitmap, filePath, false);
          //save to sdcard and get the file
          uploadFile = ImageUtility.saveImageToSDCard(bitmap);
          Glide.with(self).load(uploadFile.getAbsolutePath()).transform(new CircleTransformation(self)).into(ivAvatar);
          break;

        case CAMERA_REQUEST_CODE:
          filePath = imageFile.getAbsolutePath();
          bitmap = null;
          bitmap = ImageUtility.getRotateBitmap(getApplicationContext(), bitmap, filePath, false);
          uploadFile = ImageUtility.saveImageToSDCard(bitmap);
          Glide.with(self).load(uploadFile.getAbsolutePath()).transform(new CircleTransformation(self)).into(ivAvatar);
          break;

        case RC_SIGN_IN:
          if (resultCode == RESULT_OK) {
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
              mGoogleApiClient.connect();
            }
          } else {
            mIntentInProgress = false;
          }
          break;

        default:
//                    Session.getActiveSession().onActivityResult(self, requestCode, resultCode, data);
          break;
      }
    }

    if (Session.getActiveSession() != null && !Session.getActiveSession().isOpened()) {
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
  }

  private void doSignup() {
    if (!isRegistering) {
      isRegistering = true;
      try {
        reqEntity = new MultipartEntity(
            HttpMultipartMode.BROWSER_COMPATIBLE);

        if (uploadFile != null) {
          reqEntity.addPart("image", new FileBody(uploadFile));
        }

        reqEntity.addPart("username", new StringBody(etUsername.getText().toString().trim()));
        reqEntity.addPart("password", new StringBody(etPassword.getText().toString()));
        reqEntity.addPart("phone_number", new StringBody(arrPhoneCode.get(spPhoneCode.getSelectedItemPosition()).getCode() + etPhoneNumber.getText().toString().trim()));

        HttpMultiPart httpMultiPart = new HttpMultiPart(self, reqEntity, WebServiceConfig.URL_REGISTER, true, "", new HttpMultiPart.HttpMultipartResponse() {
          @Override
          public void onSuccess(int statusCode, String json) {
            isRegistering = false;
            parseResponseStatus(statusCode, json, new NetBaseListener() {
              @Override
              public void onRequestSuccess(String json) {
                Account registerAccount = CommonParser.parseUserFromJson(json, "");
                preferences.saveRegisterAccount(registerAccount);
                DialogUtility.showMessageDialog(self, getString(R.string.message_register_successfully), new DialogUtility.DialogListener() {
                  @Override
                  public void onClose(Dialog dialog) {
                    gotoActivity(self, VerifyAccountActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                  }
                });
              }

              @Override
              public void onRequestError(int message) {
                _onRequestError(message);
              }
            });
          }

          @Override
          public void onFailure(int statusCode, String error) {
            isRegistering = false;
            DialogUtility.showMessageDialog(self, getString(R.string.message_register_failed), null);
          }
        });

        httpMultiPart.execute();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  }

  private boolean validateSignup() {

    if (etUsername.getText().toString().trim().length() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.username_alert), null);
      return false;
    }
    if (etPassword.getText().toString().trim().length() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.password_alert), null);
      return false;
    }
    if (etPhoneNumber.getText().toString().trim().length() == 0) {
      DialogUtility.showMessageDialog(self, getString(R.string.phonenumber_alert), null);
      return false;
    }
    return true;
  }

  private Uri getTempUri() {

    imageFile = null;
    File file = new File(Environment.getExternalStorageDirectory()
        + GlobalValue.ROOT_FOLDER_NAME);
    if (!file.exists()) {
      file.mkdir();
    }
    File file2 = new File(Environment.getExternalStorageDirectory()
        + GlobalValue.ROOT_FOLDER_NAME + "/" + GlobalValue.FOLDER_IMAGE);
    if (!file2.exists()) {
      file2.mkdir();
    }
    imagePath = Environment.getExternalStorageDirectory() + "/"
        + GlobalValue.ROOT_FOLDER_NAME + "/" + GlobalValue.FOLDER_IMAGE + "/"
        + "Camera_" + System.currentTimeMillis() + ".jpg";

    imageFile = new File(imagePath);

    if (imageFile.exists()) {
      imageFile.delete();
    }

    if (!imageFile.exists()) {
      try {
        new File(imageFile.getParent()).mkdirs();
        imageFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    Uri imageUri = Uri.fromFile(imageFile);
    return imageUri;
  }

  private void performFacebookLogin() {

    if (!isFetching) {
      final Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, Arrays.asList("public_profile", "email", "user_friends"));


      Session openActiveSession = Session.openActiveSession(this, true, new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
          Log.d("FACEBOOK", "call");
          isFetching = false;
          if (session.isOpened()) {
            Log.d("FACEBOOK", "if (session.isOpened() && !isFetching)");

            session.requestNewReadPermissions(newPermissionsRequest);


            DialogUtility.showProgressDialog(self);
            Request getMe = Request.newMeRequest(session, new Request.GraphUserCallback() {
              @Override
              public void onCompleted(GraphUser user, Response response) {
                DialogUtility.closeProgressDialog();
                if (user != null) {
                  Account account = new Account();

                  String email = "";
                  if (user.getProperty("email") != null) {
                    email = user.getProperty("email").toString();
                  }
                  if (email != null && email.length() > 0) {
                    account.setUsername(StringUtility.getNameFromEmail(user.getProperty("email").toString()));
                    account.setEmail(user.getProperty("email").toString());
                  }
                  account.setUserID(user.getId());
                  account.setFirstname(user.getFirstName());
                  account.setLastname(user.getLastName());
                  if (user.getProperty("email") != null) {
                    account.setGender(user.getProperty("gender").toString().equalsIgnoreCase("male") ? Account.GENDER_MALE : Account.GENDER_FEMALE);
                  }
                  account.setImage(String.format(WebServiceConfig.FACEBOOK_AVATAR_URL, user.getId()));
                  account.setUserType(Account.USER_TYPE_FACEBOOK);

                  Bundle bundle = new Bundle();
                  bundle.putParcelable(GlobalValue.KEY_USER, account);
                  gotoActivity(self, RegisterSocialActivity.class, bundle);
                }
              }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name, last_name,email,gender, birthday");
            getMe.setParameters(parameters);
            getMe.executeAsync();
          } else {
            if (!session.isOpened())
              Log.i("FACEBOOK", "!session.isOpened()");
            else
              Log.i("FACEBOOK", "isFetching");

          }
        }


      });
    }
  }

  @Override
  public void onConnectionFailed(ConnectionResult result) {
    DialogUtility.closeProgressDialog();
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
    DialogUtility.closeProgressDialog();
    Account account = new Account();

    Person myProfile = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
    account.setFirstname(myProfile.getDisplayName());
    account.setLastname("");
    account.setImage(myProfile.getImage().getUrl());
    account.setUserID(myProfile.getId());
    account.setGender(myProfile.getGender() == 0 ? Account.GENDER_MALE : Account.GENDER_FEMALE);
    String email = Plus.AccountApi
        .getAccountName(mGoogleApiClient);
    if (email != null && email.length() > 0) {
      account.setEmail(email);
      account.setUsername(StringUtility.getNameFromEmail(email));
    }
    account.setUserType(Account.USER_TYPE_GOOGLE);
    Bundle bundle = new Bundle();
    bundle.putParcelable(GlobalValue.KEY_USER, account);
    gotoActivity(self, RegisterSocialActivity.class, bundle);
  }

  @Override
  public void onConnectionSuspended(int arg0) {
    mGoogleApiClient.connect();

  }
}
