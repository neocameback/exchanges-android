package com.pendulab.theExchange.activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.bumptech.glide.Glide;
import com.melnykov.fab.FloatingActionButton;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.CategoryListAdapter;
import com.pendulab.theExchange.adapter.LocationSearchAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.base.BaseLocationActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.SharedPreferencesManager;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Category;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.model.LocationObj;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.net.HttpMultiPart;
import com.pendulab.theExchange.utils.ActivityKeyboardObserver;
import com.pendulab.theExchange.utils.AppUtil;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ImageUtility;
import com.pendulab.theExchange.utils.ParameterFactory;
import com.pendulab.theExchange.utils.RoundedCornersTransformation;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.soundcloud.android.crop.Crop;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.MinValue;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by Anh Ha Nguyen on 10/6/2015.
 */
public class AddListingActivity extends BaseLocationActivity implements View.OnClickListener {

  private LinearLayout llImages;
  private ImageView iv1, iv2, iv3, iv4, ivSearchLocation;
  private List<ImageView> arrImageViews;
  private File[] arrUploadFiles;
  private int selectedIvPosition;

  private MultipartEntity reqEntity;
  private File outputFile;
  private String outputPath;
  private Bitmap bitmap;

  private Spinner spCategory;
  private CategoryListAdapter categoryAdapter;
  private List<Category> arrCategories;
  private String categoryId = "";

  @NotEmpty(messageId = R.string.title_alert)
  private EditText etTitle;

  private EditText etDescription;
  private EditText etPlace;

  @NotEmpty(messageId = R.string.price_alert)
  @MinValue(value = 0, messageId = R.string.price_alert)
  private EditText etPrice;

  private EditText etSearchLocation;


  private Toolbar toolbar;
  private SlidingUpPanelLayout panelLayout;
  private GoogleMap maps;
  private TextView tvMapLocation;
  private FloatingActionButton btnAcceptLocation, btnCurrentLocation;
  private List<LocationObj> arrSearchLocations;
  private ListView lvLocationSearch;
  private LocationSearchAdapter locationAdapter;

  private LocationObj applyLocationObj, tempLocationObj;

  private boolean isEdit;
  private final int SELECT_PICTURE = 101;
  private final int CAMERA_REQUEST_CODE = 104;

  private Item currentItem;

  private final String SHOWCASE_ID = "ADD_LISTING_SHOWCASE";
  private Bundle bundle = null;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_listing);

    bundle = getIntent().getExtras();
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_ITEM)) {
      currentItem = bundle.getParcelable(GlobalValue.KEY_ITEM);
      isEdit = true;
    }

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setElevation(0);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    setActionBarTitle(getString(R.string.add_new_listing));

    initUI();
    initData();
    initControl();
    if (isEdit) {
      getSupportActionBar().setTitle(getString(R.string.edit_listing));
      initItem();
    }
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_VIEW_TUTORIAL)) {
      initShowcase();
    }
  }

  private void initUI() {
    llImages = (LinearLayout) findViewById(R.id.llImages);
    iv1 = (ImageView) findViewById(R.id.iv1);
    iv2 = (ImageView) findViewById(R.id.iv2);
    iv3 = (ImageView) findViewById(R.id.iv3);
    iv4 = (ImageView) findViewById(R.id.iv4);


    int layoutHeight = (AppUtil.getDeviceScreenWidth(self) - AppUtil.getDimensionInPixel(self, R.dimen.margin_padding_small) * 5) / 4;

    //Arrange layout for 4 images of items (to be a square each image)
    LinearLayout.LayoutParams layoutParam = (LinearLayout.LayoutParams) llImages.getLayoutParams();
    layoutParam.height = layoutHeight;
    llImages.setLayoutParams(layoutParam);

    LinearLayout.LayoutParams imageParam = (LinearLayout.LayoutParams) iv1.getLayoutParams();
    imageParam.height = LinearLayout.LayoutParams.MATCH_PARENT;
    iv1.setLayoutParams(imageParam);

    LinearLayout.LayoutParams imageParam2 = (LinearLayout.LayoutParams) iv2.getLayoutParams();
    imageParam2.height = LinearLayout.LayoutParams.MATCH_PARENT;
    iv2.setLayoutParams(imageParam2);
    iv3.setLayoutParams(imageParam2);
    iv4.setLayoutParams(imageParam2);

    Glide.with(self).load(R.drawable.bg_cover_photo).into(iv1);

    spCategory = (Spinner) findViewById(R.id.spnCategory);
    etTitle = (EditText) findViewById(R.id.etTitle);
    etDescription = (EditText) findViewById(R.id.etDescription);
    etPlace = (EditText) findViewById(R.id.etPlace);
    etPrice = (EditText) findViewById(R.id.etPrice);

    initializeSldingPanel();
  }

  private void initData() {
    //init array of to be uploaded images
    arrUploadFiles = new File[4];
    for (int i = 0; i < 4; i++) {
      arrUploadFiles[i] = null;
    }

    arrImageViews = new ArrayList<>();
    arrImageViews.add(iv1);
    arrImageViews.add(iv2);
    arrImageViews.add(iv3);
    arrImageViews.add(iv4);

    //init categories choice list
    if (!preferences.getStringValue(SharedPreferencesManager.CATEGORIES).equalsIgnoreCase("")) {
      arrCategories = CommonParser.parseListCategoriesFromJson(preferences.getStringValue(SharedPreferencesManager.CATEGORIES));
      categoryAdapter = new CategoryListAdapter(self, arrCategories, getString(R.string.choose_a_category));
      spCategory.setAdapter(categoryAdapter);
      spCategory.setSelection(categoryAdapter.getCount());
    } else {
      getCategories();
    }
  }

  private void initControl() {
    iv1.setOnClickListener(this);
    iv2.setOnClickListener(this);
    iv3.setOnClickListener(this);
    iv4.setOnClickListener(this);

    etPlace.setOnClickListener(this);
    findViewById(R.id.tvCredits).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        etPrice.requestFocus();
        showSoftKeyboard(etPrice);
      }
    });

    spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        categoryId = arrCategories.get(i).getId();

      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });

    etPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View view, boolean focus) {
        if (focus) {
          if (etPrice.getText().toString().trim().equalsIgnoreCase("0")) {
            etPrice.setText("");
          }
          showSoftKeyboard(etPrice);
        }
      }
    });

    lvLocationSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        hideSoftKeyboard(lvLocationSearch);
        lvLocationSearch.setVisibility(View.GONE);
        tempLocationObj = arrSearchLocations.get(i);
        showLocationOnMap(tempLocationObj);
      }
    });

    etSearchLocation.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        if (etSearchLocation.getText().toString().trim().length() >= 3) {
          getLocationByAddress(etSearchLocation.getText().toString().trim());
        } else {
          arrSearchLocations.clear();
          locationAdapter.notifyDataSetChanged();
        }
      }
    });

    ActivityKeyboardObserver.assistActivity(self, new ActivityKeyboardObserver.IToggleKeyboard() {
      @Override
      public void onShowing() {
        tvMapLocation.setVisibility(View.GONE);
        btnCurrentLocation.setVisibility(View.GONE);
        btnAcceptLocation.setVisibility(View.GONE);
        lvLocationSearch.setVisibility(View.VISIBLE);
      }

      @Override
      public void onHidden() {
        tvMapLocation.setVisibility(View.VISIBLE);
        btnCurrentLocation.setVisibility(View.VISIBLE);
        btnAcceptLocation.setVisibility(View.VISIBLE);
        lvLocationSearch.setVisibility(View.GONE);
      }
    });
  }

  private void initShowcase() {
    ShowcaseConfig config = new ShowcaseConfig();
    config.setDelay(200); // half second between each showcase view

    final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

    sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
      @Override
      public void onShow(MaterialShowcaseView itemView, int position) {
//                Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
      }
    });

    sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
      @Override
      public void onDismiss(MaterialShowcaseView itemView, int position) {
        if (sequence.getmShowcaseQueue().size() == 0) {
          gotoActivity(self, TutorialItemBrowsingActivity.class);
        }
      }
    });

    sequence.setConfig(config);

    sequence.addSequenceItem(iv1, getString(R.string.tut_add_item_picture), getString(R.string.tut_next));
    sequence.addSequenceItem(findViewById(R.id.llDone), getString(R.string.tut_done_add_item), getString(R.string.tut_next));

    sequence.start();

  }

  private void initItem() {
    for (String string : currentItem.getArrImages()) {
      if (currentItem.getArrImages().indexOf(string) == 0) {
        Glide.with(self).load(string).centerCrop().error(R.drawable.image_not_available).into(iv1);
      }
      if (currentItem.getArrImages().indexOf(string) == 1) {
        Glide.with(self).load(string).centerCrop().error(R.drawable.image_not_available).into(iv2);
      }
      if (currentItem.getArrImages().indexOf(string) == 2) {
        Glide.with(self).load(string).centerCrop().error(R.drawable.image_not_available).into(iv3);
      }
      if (currentItem.getArrImages().indexOf(string) == 3) {
        Glide.with(self).load(string).centerCrop().error(R.drawable.image_not_available).into(iv4);
      }
    }
    tempLocationObj = new LocationObj();
    tempLocationObj.setLatitude(currentItem.getLatitude());
    tempLocationObj.setLongitude(currentItem.getLongitude());
    tempLocationObj.setAddress(currentItem.getLocation());
    applyLocationObj = tempLocationObj;
//        getLocationFromGeocode(true, applyLocationObj.getLatitude(), applyLocationObj.getLongitude());


    for (int i = 0; i < arrCategories.size(); i++) {
      if (arrCategories.get(i).getName().equalsIgnoreCase(currentItem.getCategoryId())) {
        spCategory.setSelection(i);
      }
    }
    etTitle.setText(currentItem.getTitle());
    etDescription.setText(currentItem.getTitle());
    etPlace.setText(currentItem.getLocation());
    etPrice.setText(currentItem.getPrice() + "");
  }

  private void initializeSldingPanel() {
    panelLayout = (SlidingUpPanelLayout) findViewById(R.id.panelLayout);
//        panelLayout.setTouchEnabled(false);
    panelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
      @Override
      public void onPanelSlide(View view, float v) {

      }

      @Override
      public void onPanelCollapsed(View view) {

      }

      @Override
      public void onPanelExpanded(View view) {
        initializeMap();
      }

      @Override
      public void onPanelAnchored(View view) {

      }

      @Override
      public void onPanelHidden(View view) {

      }
    });

    ivSearchLocation = (ImageView) findViewById(R.id.ivSearch);
//        ivSearchLocation.setOnClickListener(this);
    etSearchLocation = (EditText) findViewById(R.id.etSearchLocation);
    tvMapLocation = (TextView) findViewById(R.id.tvMapLocation);
    btnAcceptLocation = (FloatingActionButton) findViewById(R.id.btnAcceptLocation);
    btnCurrentLocation = (FloatingActionButton) findViewById(R.id.btnCurrentLocation);
    btnAcceptLocation.setOnClickListener(this);
    btnCurrentLocation.setOnClickListener(this);

    lvLocationSearch = (ListView) findViewById(R.id.lvSearchLocation);
    arrSearchLocations = new ArrayList<>();
    locationAdapter = new LocationSearchAdapter(self, arrSearchLocations);
    lvLocationSearch.setAdapter(locationAdapter);
  }

  @Override
  public void onObtainLocation() {
    if (mLastLocation != null && applyLocationObj == null) {
      getLocationFromGeocode(true, mLastLocation.getLatitude(), mLastLocation.getLongitude());
    }
  }

  @Override
  public void onChangeLocation() {
    if (mLastLocation != null && applyLocationObj == null) {
      getLocationFromGeocode(true, mLastLocation.getLatitude(), mLastLocation.getLongitude());
    }
  }

  @Override
  public void onLocationUnavailable() {
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_VIEW_TUTORIAL)) {

    } else {
      showLocationAlertDialog();
    }
  }

  private void initializeMap() {
    if (maps == null) {
      maps = ((SupportMapFragment) getSupportFragmentManager()
          .findFragmentById(R.id.maps)).getMap();
      maps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

        @Override
        public void onMapClick(final LatLng point) {
          // TODO Auto-generated method stub
          maps.clear();
          if (tempLocationObj != null) {
            maps.addMarker(new MarkerOptions().position(new LatLng(point.latitude, point.longitude))).setTitle(
                tempLocationObj.getLocationAddress());
          }
          getLocationFromGeocode(false, point.latitude, point.longitude);

        }
      });
      maps.setMyLocationEnabled(true);

    }

    showLocationOnMap(applyLocationObj);

    if (applyLocationObj != null) {
      tvMapLocation.setText(applyLocationObj.getLocationAddress());
    }
  }

  private void showLocationOnMap(LocationObj obj) {
    if (maps != null) {
      maps.clear();
      if (obj != null) {
        LatLng latLng = new LatLng(obj.getLatitude(),
            obj.getLongitude());

        maps.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16), 1000, new GoogleMap.CancelableCallback() {
          @Override
          public void onFinish() {

          }

          @Override
          public void onCancel() {

          }
        });

        maps.addMarker(new MarkerOptions().position(latLng)).setTitle(
            tempLocationObj.getLocationAddress());
        tvMapLocation.setText(obj.getLocationAddress());
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
//            if (validateItem())
//                postItem();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

  public void onClickDone(View v) {
    if (validateItem())
      postItem();
  }


  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    String path;

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
          File file = ImageUtility.saveImageToSDCard(bitmap);
          Crop.of(Uri.fromFile(file), getTempUri()).asSquare().start(self);
          break;


        case CAMERA_REQUEST_CODE:
          filePath = outputFile.getAbsolutePath();
          bitmap = null;
          bitmap = ImageUtility.getRotateBitmap(getApplicationContext(), bitmap, filePath, false);
          file = ImageUtility.saveImageToSDCard(bitmap);
          Crop.of(Uri.fromFile(file), getTempUri()).asSquare().start(self);
          break;

        case Crop.REQUEST_CROP:

          Glide.with(self).load(outputFile).bitmapTransform(new RoundedCornersTransformation(self, (int) getResources().getDimension(R.dimen.margin_padding_small), 0)).into(arrImageViews.get(selectedIvPosition));
          arrUploadFiles[selectedIvPosition] = outputFile;
          break;

        default:
          break;
      }
    }
  }

  @Override
  public void onClick(View view) {
    if (view == iv1) {
      selectedIvPosition = arrImageViews.indexOf(iv1);
      showPhotoOptionsDialog();
      return;
    }
    if (view == iv2) {
      selectedIvPosition = arrImageViews.indexOf(iv2);
      showPhotoOptionsDialog();
      return;
    }
    if (view == iv3) {
      selectedIvPosition = arrImageViews.indexOf(iv3);
      showPhotoOptionsDialog();
      return;
    }
    if (view == iv4) {
      selectedIvPosition = arrImageViews.indexOf(iv4);
      showPhotoOptionsDialog();
      return;
    }
    if (view == etPlace) {
      if (panelLayout != null && panelLayout.getPanelState() == PanelState.COLLAPSED) {
        panelLayout.setPanelState(PanelState.EXPANDED);
      }
    }
    if (view == ivSearchLocation) {
      if (etSearchLocation.getText().toString().trim().length() > 0) {
        getLocationByAddress(etSearchLocation.getText().toString().trim());
      }
      return;
    }
    if (view == btnAcceptLocation) {
      if (tempLocationObj != null) {
        applyLocationObj = tempLocationObj;
        etPlace.setText(applyLocationObj.getLocationAddress());
        panelLayout.setPanelState(PanelState.COLLAPSED);
      }
      return;
    }
    if (view == btnCurrentLocation) {
      if (mLastLocation != null)
        getLocationFromGeocode(false, mLastLocation.getLatitude(), mLastLocation.getLongitude());
      return;
    }
  }

  @Override
  public void onBackPressed() {
    if (panelLayout != null && panelLayout.getPanelState() == PanelState.EXPANDED) {
      panelLayout.setPanelState(PanelState.COLLAPSED);
    } else {
      if (bundle != null && bundle.containsKey(GlobalValue.KEY_VIEW_TUTORIAL)) {
      } else {
        DialogUtility.showOptionDialog(self, getString(R.string.cancel_listing_alert), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
          @Override
          public void onPositive(Dialog dialog) {
            finish();
          }

          @Override
          public void onNegative(Dialog dialog) {
          }
        });
      }
    }
  }

  private void showPhotoOptionsDialog() {
    DialogUtility.showOptionDialog(self, getString(R.string.set_pictures_options_message), getString(R.string.from_gallery), getString(R.string.from_camera), true, new DialogUtility.DialogOptionListener() {
      @Override
      public void onPositive(Dialog dialog) {
        Intent i = new Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PICTURE);
      }

      @Override
      public void onNegative(Dialog dialog) {
        Intent cameraIntent = new Intent(
            android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
      }
    });
  }

  private Uri getTempUri() {

    outputFile = null;
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
    outputPath = Environment.getExternalStorageDirectory() + "/"
        + GlobalValue.ROOT_FOLDER_NAME + "/" + GlobalValue.FOLDER_IMAGE + "/"
        + "Camera_" + System.currentTimeMillis() + ".jpg";

    outputFile = new File(outputPath);

    if (outputFile.exists()) {
      outputFile.delete();
    }

    if (!outputFile.exists()) {
      try {
        new File(outputFile.getParent()).mkdirs();
        outputFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    Uri imageUri = Uri.fromFile(outputFile);
    return imageUri;
  }


  private void getCategories() {
    AsyncHttpGet asyncGet = new AsyncHttpGet(self, new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {

        parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            arrCategories = CommonParser.parseListCategoriesFromJson(json);
            categoryAdapter = new CategoryListAdapter(self, arrCategories, getString(R.string.choose_a_category));
            spCategory.setAdapter(categoryAdapter);
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, null);
    asyncGet.execute(WebServiceConfig.URL_GET_CATEGORY);
  }

  private void getLocationFromGeocode(final boolean applyForItem, final double latitude, final double longitude) {

    String getUrl = ParameterFactory.createGeocodeSearchCoordination(latitude, longitude);
    AsyncHttpGet asyncHttpGet = new AsyncHttpGet(self, new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {
        parseNotStandardResponse(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<LocationObj> list = CommonParser.parseLocations(json);
            if (list.size() > 0) {
              tempLocationObj = CommonParser.parseLocations(json).get(0);
            } else {
              tempLocationObj = new LocationObj();
              tempLocationObj.setAddress(getString(R.string.unknown_place));
            }

            tempLocationObj.setLatitude(latitude);
            tempLocationObj.setLongitude(longitude);

            if (applyForItem && tempLocationObj != null) {
              applyLocationObj = tempLocationObj;
              etPlace.setText(applyLocationObj.getLocationAddress());
            }

            showLocationOnMap(tempLocationObj);
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

  private void getLocationByAddress(final String address) {
    String getUrl = ParameterFactory.createAddressSearchCoordination(address);

    AsyncHttpGet asyncGet = new AsyncHttpGet(self, new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {
        parseNotStandardResponse(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<LocationObj> list = CommonParser.parseLocations(json);
            if (etSearchLocation.getText().toString().trim().contains(address) || address.contains(etSearchLocation.getText().toString().trim())) {
              arrSearchLocations.clear();
              arrSearchLocations.addAll(list);
              locationAdapter.notifyDataSetChanged();
            }
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, null);

    asyncGet.execute(getUrl);
  }

  private boolean validateItem() {
    boolean isValidate = true;

    if (!isEdit) {
      if (arrUploadFiles[0] == null) {
        DialogUtility.showMessageDialog(self, getString(R.string.cover_photo_alert), null);
        iv1.setBackgroundResource(R.drawable.bg_add_listing_cell_warning);
        isValidate = false;
      }
      if (categoryId.equalsIgnoreCase("")) {
        DialogUtility.showMessageDialog(self, getString(R.string.category_alert), null);
        spCategory.setBackgroundResource(R.drawable.bg_add_listing_cell_warning);
        isValidate = false;
      }
    }
    if (!FormValidator.validate(this, new SimpleErrorPopupCallback(this))) {
      if (etTitle.getText().toString().trim().length() == 0) {
        DialogUtility.showMessageDialog(self, getString(R.string.title_alert), new DialogUtility.DialogListener() {
          @Override
          public void onClose(Dialog dialog) {
            etTitle.requestFocus();
          }
        });
      }

      if ((etPrice.getText().toString().trim().length() > 0)) {
        DialogUtility.showMessageDialog(self, getString(R.string.price_alert), new DialogUtility.DialogListener() {
          @Override
          public void onClose(Dialog dialog) {
            etPrice.requestFocus();
          }
        });
      }

      if (etPlace.getText().toString().trim().length() == 0) {
        DialogUtility.showMessageDialog(self, getString(R.string.meeting_place_alert), new DialogUtility.DialogListener() {
          @Override
          public void onClose(Dialog dialog) {

          }
        });
      }

      isValidate = false;
    }
    if (!isValidate) {

    }
    return isValidate;
  }

  private void postItem() {
    reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

    try {

      reqEntity.addPart("title", new StringBody(etTitle.getText().toString().trim(), Charset.forName("utf-8")));
      reqEntity.addPart("price", new StringBody(etPrice.getText().toString().trim()));
      reqEntity.addPart("description", new StringBody(etDescription.getText().toString().trim(), Charset.forName("utf-8")));
      reqEntity.addPart("location", new StringBody(applyLocationObj.getLocationAddress(), Charset.forName("utf-8")));
      reqEntity.addPart("category_id", new StringBody(arrCategories.get(spCategory.getSelectedItemPosition()).getId()));
      reqEntity.addPart("lat", new StringBody(applyLocationObj.getLatitude() + ""));
      reqEntity.addPart("lng", new StringBody(applyLocationObj.getLongitude() + ""));

      if (arrUploadFiles[0] != null) {
        reqEntity.addPart("main_img", new FileBody(arrUploadFiles[0]));
      }
      if (arrUploadFiles[1] != null) {
        reqEntity.addPart("sub_img_0", new FileBody(arrUploadFiles[1]));
      }
      if (arrUploadFiles[2] != null) {
        reqEntity.addPart("sub_img_1", new FileBody(arrUploadFiles[2]));
      }
      if (arrUploadFiles[3] != null) {
        reqEntity.addPart("sub_img_2", new FileBody(arrUploadFiles[3]));
      }

      if (isEdit) {
        reqEntity.addPart("item_id", new StringBody(currentItem.getId()));
      }
      String url = (isEdit ? WebServiceConfig.URL_UPDATE_ITEM : WebServiceConfig.URL_UPLOAD_ITEM);

      HttpMultiPart httpMultiPart = new HttpMultiPart(self, reqEntity, url, true, myAccount.getToken(), new HttpMultiPart.HttpMultipartResponse() {
        @Override
        public void onSuccess(int statusCode, String json) {
          parseResponseStatus(statusCode, json, new NetBaseListener() {
            @Override
            public void onRequestSuccess(String json) {
              DialogUtility.showMessageDialog(self, (isEdit ? getString(R.string.message_update_item_successfully) : getString(R.string.message_upload_item_successfully)), new DialogUtility.DialogListener() {
                @Override
                public void onClose(Dialog dialog) {
                  if (isEdit) {
                    Intent intent = new Intent();
                    intent.putExtra(GlobalValue.KEY_ITEM, currentItem);
                    setResult(RESULT_OK, intent);
                  }
                  finish();
                }
              });
            }

            @Override
            public void onRequestError(int message) {
              DialogUtility.showMessageDialog(self, getString(R.string.message_upload_item_failed), null);
            }
          });
        }

        @Override
        public void onFailure(int statusCode, String error) {
          DialogUtility.showMessageDialog(self, getString(R.string.message_upload_item_failed), null);
        }
      });

      httpMultiPart.execute();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
