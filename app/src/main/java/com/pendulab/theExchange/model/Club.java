package com.pendulab.theExchange.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anh Ha Nguyen on 6/12/2015.
 */
public class Club implements Parcelable {

  private String id = "", name = "", logo = "", address = "", city = "", state = "", country = "";
  private double latitude = 0d, longitude = 0d;

  public Club() {
  }

  public Club(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLogo() {
    return logo;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeString(this.name);
    dest.writeString(this.logo);
    dest.writeString(this.address);
    dest.writeString(this.city);
    dest.writeString(this.state);
    dest.writeString(this.country);
    dest.writeDouble(this.latitude);
    dest.writeDouble(this.longitude);
  }

  protected Club(Parcel in) {
    this.id = in.readString();
    this.name = in.readString();
    this.logo = in.readString();
    this.address = in.readString();
    this.city = in.readString();
    this.state = in.readString();
    this.country = in.readString();
    this.latitude = in.readDouble();
    this.longitude = in.readDouble();
  }

  public static final Creator<Club> CREATOR = new Creator<Club>() {
    public Club createFromParcel(Parcel source) {
      return new Club(source);
    }

    public Club[] newArray(int size) {
      return new Club[size];
    }
  };
}
