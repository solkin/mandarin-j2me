package com.tomclaw.mandarin.core;

import com.tomclaw.bingear.BinGear;
import com.tomclaw.utils.LogUtil;
import com.tomclaw.utils.RecordUtil;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Storage {

  /** Const **/
  public static final String dataPath = "/res/data/";
  public static final String settingsResFile = "settings.ini";
  /** Runtime **/
  public static Class clazz;
  /** Data **/
  public static BinGear settings = new BinGear();

  /**
   * Initialize storage runtime
   */
  public static void init() {
    /** Runtime **/
    clazz = Runtime.getRuntime().getClass();
  }

  /**
   * Loading whole data from storage
   */
  public static void load() {
    try {
      /** Loading data **/
      loadResRmsData( settingsResFile, settings );
    } catch ( Throwable ex ) {
      LogUtil.outMessage( "Storage load error: " + ex.getMessage(), true );
    }
  }

  public static void save() {
    saveRmsData( settingsResFile, settings );
  }

  /**
   * Trying to load RMS data and loading & saving 
   * data from RES even RMS reading failed
   * @param fileName
   * @param dataGear
   * @throws Throwable 
   */
  private static void loadResRmsData( String fileName, BinGear dataGear ) throws Throwable {
    /** Trying to load RMS data **/
    try {
      if ( RecordUtil.getRecordsCount( fileName ) > 0 ) {
        loadRmsData( fileName, dataGear );
        LogUtil.outMessage( fileName.concat( " read" ) );
        return;
      }
    } catch ( Throwable ex ) {
      LogUtil.outMessage( "Rms data does not exist: " + ex.getMessage() + ". Loading Res", true );
    }
    /** RMS data unexist, loading from resources **/
    loadResData( fileName, dataGear );
    saveRmsData( fileName, dataGear );
  }

  /**
   * Trying to save RMS data
   * @param fileName
   * @param dataGear 
   */
  private static void saveRmsData( String fileName, BinGear dataGear ) {
    LogUtil.outMessage( "saveRmsData( " + fileName + " )" );
    /** Removing RMS file **/
    try {
      RecordUtil.removeFile( fileName );
    } catch ( Throwable ex ) {
      LogUtil.outMessage( "RMS IOException: \"" + ex.getMessage() + "\" on write. File: [" + fileName + "]", true );
    }
    /** Trying to save file to RMS **/
    try {
      int rmsIndex = RecordUtil.saveFile( fileName, dataGear, false );
      LogUtil.outMessage( "RMS index: " + rmsIndex );
    } catch ( IOException ex ) {
      LogUtil.outMessage( "RMS IOException: \"" + ex.getMessage() + "\" on write. File: [" + fileName + "]", true );
    }
  }

  /**
   * Loading RMS data
   * @param fileName
   * @param dataGear
   * @throws Throwable 
   */
  private static void loadRmsData( String fileName, BinGear dataGear ) throws Throwable {
    /** Trying to load file data from RMS **/
    RecordUtil.readFile( fileName, dataGear );
  }

  /**
   * Loading RES data
   * @param fileName
   * @param dataGear
   * @throws Throwable 
   */
  private static void loadResData( String fileName, BinGear dataGear ) throws Throwable {
    /** Loading resource INI-formatted data **/
    dataGear.importFromIni( new DataInputStream( clazz.getResourceAsStream( dataPath.concat( fileName ) ) ) );
  }

  /**
   * Obtain required String item from settings object
   * @param binGear
   * @param group
   * @param item
   * @return
   * @throws Throwable 
   */
  public static String getString( BinGear binGear, String group, String item ) throws Throwable {
    return binGear.getValue( group, item );
  }

  /**
   * Obtain required Integer item from settings object
   * @param binGear
   * @param group
   * @param item
   * @return
   * @throws Throwable 
   */
  public static int getInteger( BinGear binGear, String group, String item ) throws Throwable {
    String value = getString( binGear, group, item );
    if ( value == null ) {
      return 0;
    } else {
      return Integer.parseInt( value );
    }
  }

  /**
   * Obtain required Boolean item from settings object
   * @param binGear
   * @param group
   * @param item
   * @return
   * @throws Throwable 
   */
  public static boolean getBoolean( BinGear binGear, String group, String item ) throws Throwable {
    String value = getString( binGear, group, item );
    if ( value == null ) {
      return false;
    } else {
      return value.equals( "true" );
    }
  }

  /**
   * Obtain required non-null String item from settings object
   * @param binGear
   * @param group
   * @param item
   * @return
   * @throws Throwable 
   */
  public static String getNonNull( BinGear binGear, String group, String item ) throws Throwable {
    String value = getString( binGear, group, item );
    if ( value == null ) {
      return "";
    } else {
      return value;
    }
  }

  /**
   * Assign non-null value to storage settings 
   * @param binGear
   * @param group
   * @param item
   * @param value
   * @throws Throwable 
   */
  public static void setNonNull( BinGear binGear, String group, String item, String value ) throws Throwable {
    if ( value != null ) {
      binGear.setValue( group, item, value );
    }
  }
}
