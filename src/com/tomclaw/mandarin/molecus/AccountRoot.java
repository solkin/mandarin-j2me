package com.tomclaw.mandarin.molecus;

import com.tomclaw.utils.StringUtil;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class AccountRoot {

  /** User data **/
  private static String userName;
  private static String password;
  private static String nickName;
  private static String resource = "Mandarin";
  private static boolean isUseSsl = false;
  private static boolean isUseSasl = false;
  private static String remoteHost = "localhost"; // "molecus.com"; // "jabberon.ru"; // "molecus.com"; // "jabber.mipt.ru"; // "localhost"; // 
  private static int remotePort = 5222;
  private static String registerHost = "localhost"; // "molecus.com"; // "jabberon.ru"; // "molecus.com"; // "jabber.mipt.ru"; // "localhost"; // 
  private static int registerPort = 5222;
  private static String servicesHost = "localhost"; // "molecus.com"; // "jabberon.ru"; // "molecus.com"; // "jabber.mipt.ru"; // "localhost"; // 
  private static int priority = 5;
  private static String roomHost = "conference".concat( "." ).concat( "localhost" );
  /** Runtime session **/
  private static Session session;
  private static int statusIndex = StatusUtil.offlineIndex;

  public static void init() {
    /** User data is loaded **/
  }

  public static void setUserName( String userName ) {
    AccountRoot.userName = userName;
  }

  public static void setPassword( String password ) {
    AccountRoot.password = password;
  }

  public static void setNickName( String nickName ) {
    AccountRoot.nickName = nickName;
  }

  public static void setRemoteHost( String hostAddr ) {
    AccountRoot.remoteHost = hostAddr;
  }

  public static void setRemotePort( int remotePort ) {
    AccountRoot.remotePort = remotePort;
  }

  public static void setResource( String resource ) {
    AccountRoot.resource = resource;
  }

  public static void setRegisterHost( String registerHost ) {
    AccountRoot.registerHost = registerHost;
  }

  public static void setRegisterPort( int registerPort ) {
    AccountRoot.registerPort = registerPort;
  }

  public static void setServicesHost( String servicesHost ) {
    AccountRoot.servicesHost = servicesHost;
  }

  public static void setRoomHost( String roomHost ) {
    AccountRoot.roomHost = roomHost;
  }

  public static void setUseSsl( boolean isUseSsl ) {
    AccountRoot.isUseSsl = isUseSsl;
  }

  public static void setUseSasl( boolean isUseSasl ) {
    AccountRoot.isUseSasl = isUseSasl;
  }

  public static void setStatusIndex( int statusIndex ) {
    AccountRoot.statusIndex = statusIndex;
  }

  public static void setPriority( int priority ) {
    AccountRoot.priority = priority;
  }

  public static Session getSession() {
    if ( session == null ) {
      session = new Session( true );
    }
    return session;
  }

  /** 
   * Checks for account fields are filled and ready
   */
  public static boolean isReady() {
    return ( userName != null && password != null && resource != null
            && remoteHost != null && remotePort != 0 );
  }

  public static boolean isOffline() {
    return statusIndex == StatusUtil.offlineIndex;
  }

  public static String getUserName() {
    return userName;
  }

  public static String getPassword() {
    return password;
  }

  public static String getNickName() {
    if ( nickName == null ) {
      return userName;
    }
    return nickName;
  }

  public static String getRemoteHost() {
    return remoteHost;
  }

  public static int getRemotePort() {
    return remotePort;
  }

  public static String getServicesHost() {
    return servicesHost;
  }

  public static String getRoomHost() {
    return roomHost;
  }

  public static String getResource() {
    return resource;
  }

  public static String getClearJid() {
    return userName.concat( "@" ).concat( remoteHost );
  }

  public static String getFullJid() {
    return getClearJid().concat( "/" ).concat( resource );
  }

  public static String generateCookie() {
    return StringUtil.generateString( 8 );
  }

  public static String getRegisterHost() {
    return registerHost;
  }

  public static int getRegisterPort() {
    return registerPort;
  }

  public static boolean getUseSsl() {
    return isUseSsl;
  }

  public static boolean getUseSasl() {
    return isUseSasl;
  }

  public static int getStatusIndex() {
    return statusIndex;
  }

  public static int getPriority() {
    return priority;
  }
}
