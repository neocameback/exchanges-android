package com.pendulab.theExchange.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/9/2015.
 */
public class Item implements Parcelable {

  private String id, categoryId, ownerUsername, ownerId, ownerImage, title, date, description, image, location;
  private int price, likeCount, commnetCount, status, verificationStatus;
  private double latitude, longitude, distance, rate;
  private boolean liked, reported, offered, offerAccepted;
  private List<String> arrImages;

  public static int STATUS_AVAILABLE = 0;
  public static int STATUS_OFFER_ACCEPTED = 1;
  public static int STATUS_SOLD = 2; //transaction verified
  public static int STATUS_ADDED_AS_TRADE_ITEM = 3;

  public static int VERIFY_NONE = 0;
  public static int VERIFY_BUY = 1;
  public static int VERIFY_SELL = 2;
  public static int VERIFY_BOTH = 3;

  public Item(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwnerUsername() {
    return ownerUsername;
  }

  public void setOwnerUsername(String ownerUsername) {
    this.ownerUsername = ownerUsername;
  }

  public String getOwnerImage() {
    return ownerImage;
  }

  public void setOwnerImage(String ownerImage) {
    this.ownerImage = ownerImage;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
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

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public boolean isLiked() {
    return liked;
  }

  public void setLiked(boolean liked) {
    this.liked = liked;
  }

  public double getRate() {
    return rate;
  }

  public void setRate(double rate) {
    this.rate = rate;
  }

  public int getCommnetCount() {
    return commnetCount;
  }

  public void setCommnetCount(int commnetCount) {
    this.commnetCount = commnetCount;
  }

  public List<String> getArrImages() {
    return arrImages;
  }

  public void setArrImages(List<String> arrImages) {
    this.arrImages = arrImages;
  }

  public boolean isReported() {
    return reported;
  }

  public void setReported(boolean reported) {
    this.reported = reported;
  }

  public boolean isOffered() {
    return offered;
  }

  public void setOffered(boolean offered) {
    this.offered = offered;
  }

  public Item() {
    this.arrImages = new ArrayList<>();
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getVerificationStatus() {
    return verificationStatus;
  }

  public void setVerificationStatus(int verificationStatus) {
    this.verificationStatus = verificationStatus;
  }

  public boolean isOfferAccepted() {
    return offerAccepted;
  }

  public void setOfferAccepted(boolean offerAccepted) {
    this.offerAccepted = offerAccepted;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeString(this.categoryId);
    dest.writeString(this.ownerUsername);
    dest.writeString(this.ownerId);
    dest.writeString(this.ownerImage);
    dest.writeString(this.title);
    dest.writeString(this.date);
    dest.writeString(this.description);
    dest.writeString(this.image);
    dest.writeString(this.location);
    dest.writeInt(this.price);
    dest.writeInt(this.likeCount);
    dest.writeInt(this.commnetCount);
    dest.writeInt(this.status);
    dest.writeInt(this.verificationStatus);
    dest.writeDouble(this.latitude);
    dest.writeDouble(this.longitude);
    dest.writeDouble(this.distance);
    dest.writeDouble(this.rate);
    dest.writeByte(liked ? (byte) 1 : (byte) 0);
    dest.writeByte(reported ? (byte) 1 : (byte) 0);
    dest.writeByte(offered ? (byte) 1 : (byte) 0);
    dest.writeByte(offerAccepted ? (byte) 1 : (byte) 0);
    dest.writeStringList(this.arrImages);
  }

  protected Item(Parcel in) {
    this.id = in.readString();
    this.categoryId = in.readString();
    this.ownerUsername = in.readString();
    this.ownerId = in.readString();
    this.ownerImage = in.readString();
    this.title = in.readString();
    this.date = in.readString();
    this.description = in.readString();
    this.image = in.readString();
    this.location = in.readString();
    this.price = in.readInt();
    this.likeCount = in.readInt();
    this.commnetCount = in.readInt();
    this.status = in.readInt();
    this.verificationStatus = in.readInt();
    this.latitude = in.readDouble();
    this.longitude = in.readDouble();
    this.distance = in.readDouble();
    this.rate = in.readDouble();
    this.liked = in.readByte() != 0;
    this.reported = in.readByte() != 0;
    this.offered = in.readByte() != 0;
    this.offerAccepted = in.readByte() != 0;
    this.arrImages = in.createStringArrayList();
  }

  public static final Creator<Item> CREATOR = new Creator<Item>() {
    public Item createFromParcel(Parcel source) {
      return new Item(source);
    }

    public Item[] newArray(int size) {
      return new Item[size];
    }
  };
}
