package com.pendulab.theExchange.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anh Ha Nguyen on 11/9/2015.
 */
@Table(name = "Message")
public class Message extends Model implements Parcelable {

  @Column(name = "MessageId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
  private String id;

  @Column(name = "Seen")
  private int seen;

  @Column(name = "UserSend")
  private String userSend;

  @Column(name = "UserReceive")
  private String userReceive;

  @Column(name = "ConversationID")
  private String conversationID;


  private String message, itemID, date, userId, username, avatar, itemTitle, itemImage, offerItems;
  private int status, offerMoney, unseenCount;

  public static final int STATUS_SENT = 1;
  public static final int STATUS_UPLOADING = 2;
  public static final int STATUS_FAILED = 0;

  public String getMessageId() {
    return id;
  }

  public void setMessageId(String id) {
    this.id = id;
  }

  public String getUserSend() {
    return userSend;
  }

  public void setUserSend(String userSend) {
    this.userSend = userSend;
  }

  public String getUserReceive() {
    return userReceive;
  }

  public void setUserReceive(String userReceive) {
    this.userReceive = userReceive;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getItemID() {
    return itemID;
  }

  public void setItemID(String itemID) {
    this.itemID = itemID;
  }

  public String getConversationID() {
    return conversationID;
  }

  public void setConversationID(String conversationID) {
    this.conversationID = conversationID;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public int getSeen() {
    return seen;
  }

  public void setSeen(int seen) {
    this.seen = seen;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getItemTitle() {
    return itemTitle;
  }

  public void setItemTitle(String itemTitle) {
    this.itemTitle = itemTitle;
  }

  public String getItemImage() {
    return itemImage;
  }

  public void setItemImage(String itemImage) {
    this.itemImage = itemImage;
  }

  public String getOfferItems() {
    return offerItems;
  }

  public void setOfferItems(String offerItems) {
    this.offerItems = offerItems;
  }

  public int getOfferMoney() {
    return offerMoney;
  }

  public void setOfferMoney(int offerMoney) {
    this.offerMoney = offerMoney;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public int getUnseenCount() {
    return unseenCount;
  }

  public void setUnseenCount(int unseenCount) {
    this.unseenCount = unseenCount;
  }

  public Message() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeInt(this.seen);
    dest.writeString(this.userSend);
    dest.writeString(this.userReceive);
    dest.writeString(this.conversationID);
    dest.writeString(this.message);
    dest.writeString(this.itemID);
    dest.writeString(this.date);
    dest.writeString(this.userId);
    dest.writeString(this.username);
    dest.writeString(this.avatar);
    dest.writeString(this.itemTitle);
    dest.writeString(this.itemImage);
    dest.writeString(this.offerItems);
    dest.writeInt(this.status);
    dest.writeInt(this.offerMoney);
    dest.writeInt(this.unseenCount);
  }

  protected Message(Parcel in) {
    this.id = in.readString();
    this.seen = in.readInt();
    this.userSend = in.readString();
    this.userReceive = in.readString();
    this.conversationID = in.readString();
    this.message = in.readString();
    this.itemID = in.readString();
    this.date = in.readString();
    this.userId = in.readString();
    this.username = in.readString();
    this.avatar = in.readString();
    this.itemTitle = in.readString();
    this.itemImage = in.readString();
    this.offerItems = in.readString();
    this.status = in.readInt();
    this.offerMoney = in.readInt();
    this.unseenCount = in.readInt();
  }

  public static final Creator<Message> CREATOR = new Creator<Message>() {
    public Message createFromParcel(Parcel source) {
      return new Message(source);
    }

    public Message[] newArray(int size) {
      return new Message[size];
    }
  };
}
