package com.pendulab.theExchange.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anh Ha Nguyen on 10/5/2015.
 */
public class Category implements Parcelable {

  private String id;
  private String name;
  private String image;
  private boolean selected;

  public static final int ALL_CATEGORIES = 0;
  public static final int WISH_LIST = -1;

  public static final int SORT_PRICE_LOWEST = 1;
  public static final int SORT_PRICE_HIGHEST = 2;
  public static final int SORT_POPULAR = 3;
  public static final int SORT_RECENT = 4;
  public static final int SORT_NEAREST = 5;

  public Category(String id, String name, String image) {
    this.id = id;
    this.name = name;
    this.image = image;
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

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeString(this.name);
    dest.writeString(this.image);
  }

  public Category() {
  }

  protected Category(Parcel in) {
    this.id = in.readString();
    this.name = in.readString();
    this.image = in.readString();
  }

  public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
    public Category createFromParcel(Parcel source) {
      return new Category(source);
    }

    public Category[] newArray(int size) {
      return new Category[size];
    }
  };
}
