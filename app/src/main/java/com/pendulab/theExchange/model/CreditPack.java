package com.pendulab.theExchange.model;

/**
 * Created by Anh Ha Nguyen on 12/15/2015.
 */
public class CreditPack {

  public static final String KEY_10_CREDITS_PACK = "android.test.purchased";
  public static final String KEY_20_CREDITS_PACK = "android.test.purchased";
  public static final String KEY_50_CREDITS_PACK = "android.test.purchased";
  public static final String KEY_100_CREDITS_PACK = "android.test.purchased";

  private String name;
  private int price;
  private String skuID;

  public CreditPack() {
  }

//    public CreditPack(String name, float price, String skuID){
//        this.name = name;
//        this.price = price;
//        this.skuID = skuID;
//    }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public String getSkuID() {
    return skuID;
  }

  public void setSkuID(String skuID) {
    this.skuID = skuID;
  }
}
