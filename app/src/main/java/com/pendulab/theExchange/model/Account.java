package com.pendulab.theExchange.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {
  private String userID = "";
  private String socialID = "";
  private String password = "";
  private String email = "";
  private String username = "";
  private String firstname = "";
  private String lastname = "";
  private String country = "";
  private String city = "";
  private String userType = "";
  private String bio = "";
  private String gender = "";
  private String image = "";
  private String phoneNumber = "";
  private String registerDate = "";
  private String birthday = "";
  private String verificationCode = "";
  private String token = "";
  private int tradeCredit, inventoryCount, inboxCount, wishlistCount, numberRate;
  private double latitude, longitude;
  private boolean blocked;
  private float rate;

  public static String GENDER_MALE = "1";
  public static String GENDER_FEMALE = "0";

  public static String USER_TYPE_NORMAL = "0";
  public static String USER_TYPE_FACEBOOK = "1";
  public static String USER_TYPE_GOOGLE = "2";

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getUserType() {
    return userType;
  }

  public void setUserType(String userType) {
    this.userType = userType;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
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

  public String getSocialID() {
    return socialID;
  }

  public void setSocialID(String socialID) {
    this.socialID = socialID;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public String getRegisterDate() {
    return registerDate;
  }

  public void setRegisterDate(String registerDate) {
    this.registerDate = registerDate;
  }

  public int getTradeCredit() {
    return tradeCredit;
  }

  public void setTradeCredit(int tradeCredit) {
    this.tradeCredit = tradeCredit;
  }

  public String getVerificationCode() {
    return verificationCode;
  }

  public void setVerificationCode(String verficationCode) {
    this.verificationCode = verficationCode;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public int getInventoryCount() {
    return inventoryCount;
  }

  public void setInventoryCount(int inventoryCount) {
    this.inventoryCount = inventoryCount;
  }

  public int getInboxCount() {
    return inboxCount;
  }

  public void setInboxCount(int inboxCount) {
    this.inboxCount = inboxCount;
  }

  public int getWishlistCount() {
    return wishlistCount;
  }

  public void setWishlistCount(int wishlistCount) {
    this.wishlistCount = wishlistCount;
  }

  public boolean isBlocked() {
    return blocked;
  }

  public void setBlocked(boolean blocked) {
    this.blocked = blocked;
  }

  public int getNumberRate() {
    return numberRate;
  }

  public void setNumberRate(int numberRate) {
    this.numberRate = numberRate;
  }

  public float getRate() {
    return rate;
  }

  public void setRate(float rate) {
    this.rate = rate;
  }

  public Account() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.userID);
    dest.writeString(this.socialID);
    dest.writeString(this.password);
    dest.writeString(this.email);
    dest.writeString(this.username);
    dest.writeString(this.firstname);
    dest.writeString(this.lastname);
    dest.writeString(this.country);
    dest.writeString(this.city);
    dest.writeString(this.userType);
    dest.writeString(this.bio);
    dest.writeString(this.gender);
    dest.writeString(this.image);
    dest.writeString(this.phoneNumber);
    dest.writeString(this.registerDate);
    dest.writeString(this.birthday);
    dest.writeString(this.verificationCode);
    dest.writeString(this.token);
    dest.writeInt(this.tradeCredit);
    dest.writeInt(this.inventoryCount);
    dest.writeInt(this.inboxCount);
    dest.writeInt(this.wishlistCount);
    dest.writeInt(this.numberRate);
    dest.writeDouble(this.latitude);
    dest.writeDouble(this.longitude);
    dest.writeByte(blocked ? (byte) 1 : (byte) 0);
    dest.writeFloat(this.rate);
  }

  protected Account(Parcel in) {
    this.userID = in.readString();
    this.socialID = in.readString();
    this.password = in.readString();
    this.email = in.readString();
    this.username = in.readString();
    this.firstname = in.readString();
    this.lastname = in.readString();
    this.country = in.readString();
    this.city = in.readString();
    this.userType = in.readString();
    this.bio = in.readString();
    this.gender = in.readString();
    this.image = in.readString();
    this.phoneNumber = in.readString();
    this.registerDate = in.readString();
    this.birthday = in.readString();
    this.verificationCode = in.readString();
    this.token = in.readString();
    this.tradeCredit = in.readInt();
    this.inventoryCount = in.readInt();
    this.inboxCount = in.readInt();
    this.wishlistCount = in.readInt();
    this.numberRate = in.readInt();
    this.latitude = in.readDouble();
    this.longitude = in.readDouble();
    this.blocked = in.readByte() != 0;
    this.rate = in.readFloat();
  }

  public static final Creator<Account> CREATOR = new Creator<Account>() {
    public Account createFromParcel(Parcel source) {
      return new Account(source);
    }

    public Account[] newArray(int size) {
      return new Account[size];
    }
  };
}
