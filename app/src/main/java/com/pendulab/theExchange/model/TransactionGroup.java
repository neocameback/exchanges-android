package com.pendulab.theExchange.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 12/8/2015.
 */
public class TransactionGroup {

  private String date;
  private List<Transaction> arrTransaction;
  private boolean isExpand;

  public TransactionGroup() {
    this.arrTransaction = new ArrayList<>();
    isExpand = true;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public List<Transaction> getArrTransaction() {
    return arrTransaction;
  }

  public void setArrTransaction(List<Transaction> arrTransaction) {
    this.arrTransaction = arrTransaction;
  }

  public boolean isExpand() {
    return isExpand;
  }

  public void setIsExpand(boolean isExpand) {
    this.isExpand = isExpand;
  }
}
