package com.pendulab.theExchange.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 11/17/2015.
 */
public class Offer {

  private String id, userID, username, avatar, itemTitle, itemID, itemImage, date, ownerID;
  private int money, status, verify;
  private List<Item> arrTradeItems;

  public static int STATUS_ACCEPTED = 1;
  public static int STATUS_REJECT = 2;

  public Offer() {
    arrTradeItems = new ArrayList<>();
  }

  public String getOwnerID() {
    return ownerID;
  }

  public void setOwnerID(String ownerID) {
    this.ownerID = ownerID;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
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

  public String getItemID() {
    return itemID;
  }

  public void setItemID(String itemID) {
    this.itemID = itemID;
  }

  public String getItemImage() {
    return itemImage;
  }

  public void setItemImage(String itemImage) {
    this.itemImage = itemImage;
  }

  public int getMoney() {
    return money;
  }

  public void setMoney(int money) {
    this.money = money;
  }

  public List<Item> getArrTradeItems() {
    return arrTradeItems;
  }

  public void setArrTradeItems(List<Item> arrTradeItems) {
    this.arrTradeItems = arrTradeItems;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getVerify() {
    return verify;
  }

  public void setVerify(int verify) {
    this.verify = verify;
  }
}
