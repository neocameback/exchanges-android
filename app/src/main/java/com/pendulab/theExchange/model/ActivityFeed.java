package com.pendulab.theExchange.model;

/**
 * Created by Anh Ha Nguyen on 12/2/2015.
 */
public class ActivityFeed {

  private String id, userId, username, userAvatar, recordId, itemId, itemImage, date, timeAgo;
  private int type, seen;

  public static final int ACTIVITY_TYPE_MAKE_OFFER = 2;
  public static final int ACTIVITY_TYPE_COMMENT = 3;
  public static final int ACTIVITY_TYPE_ACCEPT_OFFER = 4;
  public static final int ACTIVITY_TYPE_ADD_WISH_LIST = 5;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRecordId() {
    return recordId;
  }

  public void setRecordId(String recordId) {
    this.recordId = recordId;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getItemImage() {
    return itemImage;
  }

  public void setItemImage(String itemImage) {
    this.itemImage = itemImage;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getSeen() {
    return seen;
  }

  public void setSeen(int seen) {
    this.seen = seen;
  }

  public String getUserAvatar() {
    return userAvatar;
  }

  public void setUserAvatar(String userAvatar) {
    this.userAvatar = userAvatar;
  }

  public String getTimeAgo() {
    return timeAgo;
  }

  public void setTimeAgo(String timeAgo) {
    this.timeAgo = timeAgo;
  }
}
