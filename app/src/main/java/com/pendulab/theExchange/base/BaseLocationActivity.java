package com.pendulab.theExchange.base;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.utils.DialogUtility;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Anh Ha Nguyen on 10/7/2015.
 */
public abstract class BaseLocationActivity extends BaseShareActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

  private GoogleApiClient mGoogleApiClient;

  // boolean flag to toggle periodic location updates
  private boolean mRequestingLocationUpdates = false;

  public Location mLastLocation;

  private LocationRequest mLocationRequest;

  private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

  // Location updates intervals in sec
  private static int UPDATE_INTERVAL = 10000; // 10 sec
  private static int FATEST_INTERVAL = 5000; // 5 sec
  private static int DISPLACEMENT = 10; // 10 meters


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (checkPlayServices()) {

      // Building the GoogleApi client
      buildGoogleApiClient();
      createLocationRequest();
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (mGoogleApiClient != null) {
      mGoogleApiClient.connect();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (checkPlayServices())
    // Resuming the periodic location updates
    {
      if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
        startLocationUpdates();
      }
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    stopLocationUpdates();
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  private void displayLocation() {

    mLastLocation = LocationServices.FusedLocationApi
        .getLastLocation(mGoogleApiClient);

    if (mLastLocation != null) {
      onObtainLocation();
    }

  }

  /**
   * Creating google api client object
   */
  protected synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API).build();
  }

  /**
   * Creating location request object
   */
  protected void createLocationRequest() {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(UPDATE_INTERVAL);
    mLocationRequest.setFastestInterval(FATEST_INTERVAL);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
  }

  /**
   * Method to verify google play services on the device
   */
  private boolean checkPlayServices() {
    int resultCode = GooglePlayServicesUtil
        .isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
        GooglePlayServicesUtil.getErrorDialog(resultCode, this,
            PLAY_SERVICES_RESOLUTION_REQUEST).show();
      } else {
        Toast.makeText(getApplicationContext(),
            "This device is not supported by Google Play Service.", Toast.LENGTH_LONG)
            .show();
        finish();
      }
      return false;
    }
    return true;
  }

  /**
   * Starting the location updates
   */
  protected void startLocationUpdates() {

    Log.i(TAG, "start location update...");
    LocationServices.FusedLocationApi.requestLocationUpdates(
        mGoogleApiClient, mLocationRequest, this);
    mRequestingLocationUpdates = true;

  }

  /**
   * Stopping location updates
   */
  protected void stopLocationUpdates() {
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
      Log.i(TAG, "Stop location update");
      LocationServices.FusedLocationApi.removeLocationUpdates(
          mGoogleApiClient, this);
      mRequestingLocationUpdates = false;
    }
  }

  /**
   * Google api callback methods
   */
  @Override
  public void onConnectionFailed(ConnectionResult result) {
    Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
        + result.getErrorCode());
  }

  @Override
  public void onConnected(Bundle arg0) {

    if (!isLocationEnable()) {
      onLocationUnavailable();
    } else {
      displayLocation();

      if (!mRequestingLocationUpdates) {
        startLocationUpdates();
      }
    }
  }

  @Override
  public void onConnectionSuspended(int arg0) {
    if (mGoogleApiClient != null) {
      mGoogleApiClient.connect();
    }
  }

  @Override
  public void onLocationChanged(Location location) {
    // Assign the new location
    if (mLastLocation == null) {
      displayLocation();
    } else {
      mLastLocation = location;
//        Toast.makeText(getApplicationContext(), "Location changed!",
//                Toast.LENGTH_SHORT).show();
      // Displaying the new location on UI
//        displayLocation();
      onChangeLocation();
    }
  }

  public void showLocationAlertDialog() {
    DialogUtility.showOptionDialog(self, getString(R.string.location_alert), getString(R.string.text_yes), getString(R.string.text_no), false, new DialogUtility.DialogOptionListener() {
      @Override
      public void onPositive(Dialog dialog) {
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        self.startActivity(intent);
      }

      @Override
      public void onNegative(Dialog dialog) {
        finish();
      }
    });
  }

  public abstract void onObtainLocation();

  public abstract void onChangeLocation();

  public abstract void onLocationUnavailable();

  private boolean isLocationEnable() {
    LocationManager locationManager = (LocationManager) self
        .getSystemService(LOCATION_SERVICE);

    // getting GPS status
    boolean isGPSEnabled = locationManager
        .isProviderEnabled(LocationManager.GPS_PROVIDER);

    // getting network status
    boolean isNetworkEnabled = locationManager
        .isProviderEnabled(LocationManager.NETWORK_PROVIDER) && isNetworkAvailable();

    if (!isGPSEnabled && !isNetworkEnabled) {
      return false;
    }
    return true;
  }

  protected boolean isNetworkAvailable() {
    ConnectivityManager conMgr = (ConnectivityManager) self.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo i = conMgr.getActiveNetworkInfo();
    if (i == null)
      return false;
    if (!i.isConnected())
      return false;
    if (!i.isAvailable())
      return false;
    return true;
  }
}
