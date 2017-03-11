package com.pendulab.theExchange.activity;

import com.bumptech.glide.Glide;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.net.HttpMultiPart;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ImageUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

/**
 * Created by Anh Ha Nguyen on 11/4/2015.
 */
public class EditProfileActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {


  private EditText etFirstname;
  private EditText etLastname;
  private EditText etCity;
  private EditText etBio;

  //    @RegExp( value = RegExp.EMAIL, messageId = R.string.message_alert_email_not_correct_format)
  private EditText etEmail;

  private EditText etBirthday;
  private ImageView ivAvatar;
  private RadioButton rbMale, rbFemale;

  private static final int SELECT_PICTURE = 101;
  private static final int CAMERA_REQUEST_CODE = 104;

  private MultipartEntity reqEntity;
  private File imageFile;
  private String imagePath;
  private Bitmap bitmap;
  private boolean isUpdating;

  private boolean isFetching;
  private File uploadFile;

  public static final String DATEPICKER_TAG = "datepicker";
  private Calendar calendar = null;
  private DatePickerDialog datePickerDialog;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_profile);


    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    setActionBarTitle(getString(R.string.edit_profile));

    initUI();
    initData();
    initControl();
  }

  private void initUI() {
    etFirstname = (EditText) findViewById(R.id.etFirstname);
    etLastname = (EditText) findViewById(R.id.etLastname);
    etCity = (EditText) findViewById(R.id.etCity);
    etBio = (EditText) findViewById(R.id.etBio);
    etEmail = (EditText) findViewById(R.id.etEmail);
    etBirthday = (EditText) findViewById(R.id.etBirthday);
    ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
    rbFemale = (RadioButton) findViewById(R.id.rbFemale);
    rbMale = (RadioButton) findViewById(R.id.rbMale);
  }

  private void initData() {
    etFirstname.setText(myAccount.getFirstname());
    etLastname.setText(myAccount.getLastname());
    etCity.setText(myAccount.getCity());
    etBio.setText(myAccount.getBio());
    etEmail.setText(myAccount.getEmail());
    etBirthday.setText(myAccount.getBirthday().contains(" ") ? myAccount.getBirthday().split(" ")[0] : myAccount.getBirthday());
    Glide.with(self).load(myAccount.getImage()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(ivAvatar);
    rbMale.setChecked(myAccount.getGender().equalsIgnoreCase("0"));
    rbFemale.setChecked(myAccount.getGender().equalsIgnoreCase("1"));

    calendar = Calendar.getInstance();

    try {
      String yearMonthDay = myAccount.getBirthday().split(" ")[0];
      String[] split = yearMonthDay.split("-");
      calendar.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));
    } catch (Exception e) {
      e.printStackTrace();
      calendar = Calendar.getInstance();
    }


  }

  private void initControl() {
    etBirthday.setOnClickListener(this);
    ivAvatar.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    if (view == etBirthday) {
      datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
      datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
      return;
    }
    if (view == ivAvatar) {
      showPhotoOptionsDialog();
    }
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
          Glide.with(self).load(uploadFile.getAbsolutePath()).into(ivAvatar);
          break;

        case CAMERA_REQUEST_CODE:
          filePath = imageFile.getAbsolutePath();
          bitmap = null;
          bitmap = ImageUtility.getRotateBitmap(getApplicationContext(), bitmap, filePath, false);
          uploadFile = ImageUtility.saveImageToSDCard(bitmap);
          Glide.with(self).load(uploadFile.getAbsolutePath()).into(ivAvatar);
          break;

        default:
//                    Session.getActiveSession().onActivityResult(self, requestCode, resultCode, data);
          break;
      }
    }

  }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_activity_login, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_accept) {
//            if (FormValidator.validate(this, new SimpleErrorPopupCallback(this))) {
//                doUpdate();
//            }
//
//        }
//        return super.onOptionsItemSelected(item);
//    }

  public void onClickDone(View v) {
    if (FormValidator.validate(this, new SimpleErrorPopupCallback(this))) {
      doUpdate();
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

  @Override
  public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
    etBirthday.setText(year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day));
    calendar.set(year, month, day);
  }

  private void doUpdate() {
    if (!isUpdating) {
      isUpdating = true;
      try {
        reqEntity = new MultipartEntity(
            HttpMultipartMode.BROWSER_COMPATIBLE);

        if (uploadFile != null) {
          reqEntity.addPart("image", new FileBody(uploadFile));
        }

        if (etFirstname.getText().toString().trim().length() > 0) {
          reqEntity.addPart("firstname", new StringBody(etFirstname.getText().toString().trim()));
        }
        if (etLastname.getText().toString().trim().length() > 0) {
          reqEntity.addPart("lastname", new StringBody(etLastname.getText().toString().trim()));
        }
        if (etBio.getText().toString().trim().length() > 0) {
          reqEntity.addPart("bio", new StringBody(etBio.getText().toString().trim()));
        }
        if (etCity.getText().toString().trim().length() > 0) {
          reqEntity.addPart("city", new StringBody(etCity.getText().toString().trim()));
        }
        if (rbMale.isChecked() || rbFemale.isChecked()) {
          reqEntity.addPart("gender", new StringBody(rbMale.isChecked() ? "0" : "1"));
        }

        if (etBirthday.getText().toString().trim().length() > 0) {
          reqEntity.addPart("birthday", new StringBody(etBirthday.getText().toString().trim() + " 00:00:00"));
        }

        if (etEmail.getText().toString().trim().length() > 0) {
          reqEntity.addPart("email", new StringBody(etEmail.getText().toString().trim()));
        }

        HttpMultiPart httpMultiPart = new HttpMultiPart(self, reqEntity, WebServiceConfig.URL_UPDATE_USER, true, myAccount.getToken(), new HttpMultiPart.HttpMultipartResponse() {
          @Override
          public void onSuccess(int statusCode, String json) {
            isUpdating = false;
            parseResponseStatus(statusCode, json, new NetBaseListener() {
              @Override
              public void onRequestSuccess(String json) {
                getMyInfo();
              }

              @Override
              public void onRequestError(int message) {
                _onRequestError(message);
              }
            });
          }

          @Override
          public void onFailure(int statusCode, String error) {
            isUpdating = false;
            DialogUtility.showMessageDialog(self, getString(R.string.message_register_failed), null);
          }
        });

        httpMultiPart.execute();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  }

  private void getMyInfo() {
    String getUrl = ParameterFactory.createGetUserInfoParam(myAccount.getUserID());
    AsyncHttpGet asyncHttpGet = new AsyncHttpGet(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
        DialogUtility.closeProgressDialog();
        parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            myAccount = CommonParser.parseUserFromJson(json, myAccount.getToken());
            preferences.saveUserInfo(myAccount);
            DialogUtility.showMessageDialog(self, getString(R.string.message_update_profile_successfully), null);
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });

      }
    }, null);
    asyncHttpGet.execute(getUrl);
  }
}
