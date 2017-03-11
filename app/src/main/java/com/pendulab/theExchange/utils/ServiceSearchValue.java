package com.pendulab.theExchange.utils;

public class ServiceSearchValue {

  public static String name = "";
  public static int minPrice = 0;
  public static int maxPrice = 1000;
  public static int categoryId = 0;
  public static int subCategoryId = 0;
  public static String subCategoryName = "";
  public static int distance = -1;
  public static Double myLat = -1d;
  public static Double myLng = -1d;
  public static String dateRange = "";
  public static String timeFrom = "";
  public static String timeTo = "";
  public static String date = "";
  public static String weekDay = "";

  public static void reset() {
    name = "";
    minPrice = 0;
    maxPrice = 1000;
    categoryId = 0;
    subCategoryId = 0;
    subCategoryName = "";
    distance = -1;
    myLat = -1d;
    myLng = -1d;
    dateRange = "";
    timeFrom = "";
    timeTo = "";
    date = "";
    weekDay = "";

  }

}
