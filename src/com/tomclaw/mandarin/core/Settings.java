package com.tomclaw.mandarin.core;

import com.tomclaw.mandarin.molecus.AccountRoot;
import com.tomclaw.utils.LogUtil;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Settings {

  /** Constants **/
  public static final int ROOM_NAME_MAX_SIZE = 40;
  public static final int ROOM_DESC_MAX_SIZE = 120;
  /** Icons **/
  public static String IMG_CHAT = "/res/groups/img_chat.png";
  public static String IMG_STATUS = "/res/groups/img_status.png";
  public static String IMG_SUBSCRIPTION = "/res/groups/img_subscription.png";
  public static String IMG_DOTIMAGE = "/res/label_dot.png";
  /** RMS files **/
  public static String buddyListFile = "/mandarin/roster.dat";
  /** Appearance **/
  public static boolean hideEmptyGroups = true;
  public static boolean showGroups = true;
  public static boolean showOffline = true;
  public static boolean isSortOnline = true; // TODO
  public static boolean isRaiseUnread = true; // TODO
  public static int textBoxMaxSize = 2048; // TODO
  public static boolean isHideUnsupportedServices = false; // TODO
  public static String themeOfflineResPath = "/res/themes/tcuilite_defg.tt2";
  public static String themeOnlineResPath = "/res/themes/tcuilite_defc.tt2";
  public static boolean isAutomatedSubscriptionApprove = true; // TODO
  public static boolean isAutomatedSubscriptionRequests = true; // TODO
  public static boolean isHideSubscriptionApproveMessage = true; // TODO
  public static int pingDelay = 20000; // TODO
  public static boolean isRemoveOfflineResources = true; // TODO
  public static long roomsUpdateDelay = 300000; // TODO
  public static boolean isMucHistoryMaxStanzas = true; // TODO
  public static int mucHistoryMaxStanzas = 5; // TODO
  public static boolean isMucHistorySeconds = false; // TODO
  public static int mucHistorySeconds = 180; // TODO
  public static int mucHistoryMaxChars = 2000; // TODO

  /**
   * Updating all settings from Storage data
   */
  public static void loadAll() {
    loadAccountData();
    loadAppearanceData();
  }

  /**
   * Loading account data from storage settings
   */
  private static void loadAccountData() {
    try {
      AccountRoot.setUserName( Storage.getString( Storage.settings, "account", "username" ) );
      AccountRoot.setPassword( Storage.getString( Storage.settings, "account", "password" ) );
      AccountRoot.setNickName( Storage.getString( Storage.settings, "account", "nickname" ) );
      AccountRoot.setResource( Storage.getString( Storage.settings, "account", "resource" ) );
      AccountRoot.setUseSsl( Storage.getBoolean( Storage.settings, "account", "isusessl" ) );
      AccountRoot.setRemoteHost( Storage.getString( Storage.settings, "account", "remotehost" ) );
      AccountRoot.setRemotePort( Storage.getInteger( Storage.settings, "account", "remoteport" ) );
      AccountRoot.setRegisterHost( Storage.getString( Storage.settings, "account", "registerhost" ) );
      AccountRoot.setRegisterPort( Storage.getInteger( Storage.settings, "account", "registerport" ) );
    } catch ( Throwable ex ) {
      LogUtil.outMessage( "Error while loading settings: " + ex.getMessage(), true );
    }
  }

  /**
   * Loading appearance data from storage settings
   */
  private static void loadAppearanceData() {
    try {
      hideEmptyGroups = Storage.getBoolean( Storage.settings, "appearance", "hide_empty_groups" );
      showGroups = Storage.getBoolean( Storage.settings, "appearance", "show_groups" );
      showOffline = Storage.getBoolean( Storage.settings, "appearance", "show_offline" );
    } catch ( Throwable ex ) {
      LogUtil.outMessage( "Error while loading settings: " + ex.getMessage(), true );
    }
  }

  /**
   * Saves all settings to Storage data
   */
  public static void saveAll() {
    saveAccountData();
    saveAppearanceData();
  }

  /**
   * Saving account data to storage settings
   */
  private static void saveAccountData() {
    try {
      Storage.settings.addGroup( "account" );
      Storage.setNonNull( Storage.settings, "account", "username", AccountRoot.getUserName() );
      Storage.setNonNull( Storage.settings, "account", "password", AccountRoot.getPassword() );
      Storage.setNonNull( Storage.settings, "account", "nickname", AccountRoot.getNickName() );
      Storage.setNonNull( Storage.settings, "account", "resource", AccountRoot.getResource() );
      Storage.setNonNull( Storage.settings, "account", "isusessl", AccountRoot.getUseSsl() ? "true" : "false" );
      Storage.setNonNull( Storage.settings, "account", "remotehost", AccountRoot.getRemoteHost() );
      Storage.setNonNull( Storage.settings, "account", "remoteport", String.valueOf( AccountRoot.getRemotePort() ) );
      Storage.setNonNull( Storage.settings, "account", "registerhost", AccountRoot.getRegisterHost() );
      Storage.setNonNull( Storage.settings, "account", "registerport", String.valueOf( AccountRoot.getRegisterPort() ) );
    } catch ( Throwable ex ) {
      LogUtil.outMessage( "Error while saving settings: " + ex.getMessage(), true );
    }
  }

  /**
   * Saving appearance data to storage settings
   */
  private static void saveAppearanceData() {
    try {
      Storage.settings.addGroup( "appearance" );
      Storage.setNonNull( Storage.settings, "appearance", "hide_empty_groups", hideEmptyGroups ? "true" : "false" );
      Storage.setNonNull( Storage.settings, "appearance", "show_groups", showGroups ? "true" : "false" );
      Storage.setNonNull( Storage.settings, "appearance", "show_offline", showOffline ? "true" : "false" );
    } catch ( Throwable ex ) {
      LogUtil.outMessage( "Error while saving settings: " + ex.getMessage(), true );
    }
  }
}
