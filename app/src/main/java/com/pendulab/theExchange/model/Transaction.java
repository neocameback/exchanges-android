package com.pendulab.theExchange.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 12/8/2015.
 */
public class Transaction {

  private String id, itemID, itemTitle, itemImage, date, price;
  private int money;
  private List<Item> arrTradeItem;

  public Transaction() {
    this.arrTradeItem = new ArrayList<>();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getItemID() {
    return itemID;
  }

  public void setItemID(String itemID) {
    this.itemID = itemID;
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

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }


  public int getMoney() {
    return money;
  }

  public void setMoney(int money) {
    this.money = money;
  }

  public List<Item> getArrTradeItem() {
    return arrTradeItem;
  }

  public void setArrTradeItem(List<Item> arrTradeItem) {
    this.arrTradeItem = arrTradeItem;
  }
}
