package com.pendulab.theExchange.config;


/**
 * Database information
 *
 * @author HUY
 */
public final class DatabaseConfig {

  private static int DB_VERSION = 1;
  private static String DB_NAME = "FruityMediaPlayer.sqlite";
  private static DatabaseConfig instance = null;

  /**
   * constructor
   */
  public DatabaseConfig() {

  }

  /**
   * get database version
   */
  public int getDB_VERSION() {
    return DB_VERSION;
  }

  /**
   * get database name
   */
  public String getDB_NAME() {
    return DB_NAME;
  }

  /**
   * get class instance
   */

  public static DatabaseConfig getInstance() {
    if (instance == null) {
      instance = new DatabaseConfig();
    }
    return instance;
  }

  /**
   * get database path
   */
  public String getDatabasepath() {
//		PacketUtility packetUtility=new PacketUtility();
//		return "data/data/"+packetUtility.getPackageName()+"/databases/";
    return "";
  }

  /**
   * get database full path
   */
  public String getDatabasefullpath() {
    return getDatabasepath() + DB_NAME;

  }
}
