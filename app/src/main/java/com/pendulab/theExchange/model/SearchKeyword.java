package com.pendulab.theExchange.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Anh Ha Nguyen on 10/15/2015.
 */

@Table(name = "SearchKeyword")
public class SearchKeyword extends Model {


  @Column(name = "_id")
  private String userId;
  @Column(name = "Keyword")
  private String keyword;
  @Column(name = "Type")
  private int type;

  public static final int TYPE_SEARCH_ITEM = 1;
  public static final int TYPE_SEARCH_USER = 2;

  public SearchKeyword(String userId, String keyword, int type) {
    this.userId = userId;
    this.keyword = keyword;
    this.type = type;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}


