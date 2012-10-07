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
  private static String remoteHost = "molecus.com"; // "molecus.com"; // "jabberon.ru"; // "molecus.com"; // "jabber.mipt.ru"; // "localhost"; // 
  private static int remotePort = 5222;
  private static String registerHost = "molecus.com"; // "molecus.com"; // "jabberon.ru"; // "molecus.com"; // "jabber.mipt.ru"; // "localhost"; // 
  private static int registerPort = 5222;
  private static String servicesHost = "molecus.com"; // "molecus.com"; // "jabberon.ru"; // "molecus.com"; // "jabber.mipt.ru"; // "localhost"; // 
  private static int priority = 5;
  private static String roomHost = "conference".concat( "." ).concat( "molecus.com" );
  /** Runtime session **/
  private static Session session;
  private static int statusIndex = StatusUtil.offlineIndex;

  /**
   * Initialization of user data
   */
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

  /**
   * Returns session or creates if session doesn't exist
   * @return Session
   */
  public static Session getSession() {
    if ( session == null ) {
      session = new Session( true );
    }
    return session;
  }

  /**
   * Checks for account fields are filled and ready
   * @return boolean
   */
  public static boolean isReady() {
    return ( userName != null && password != null && resource != null
            && remoteHost != null && remotePort != 0 );
  }

  /**
   * Returns account is offline
   * @return 
   */
  public static boolean isOffline() {
    return statusIndex == StatusUtil.offlineIndex;
  }

  /**
   * Returns account user name
   * @return String username
   */
  public static String getUserName() {
    return userName;
  }

  /**
   * Returns account password
   * @return String password
   */
  public static String getPassword() {
    return password;
  }

  /**
   * Returns account nick name
   * @return String nick name
   */
  public static String getNickName() {
    if ( nickName == null ) {
      return userName;
    }
    return nickName;
  }

  /**
   * Returns remote connection host for main stream
   * @return String host
   */
  public static String getRemoteHost() {
    return remoteHost;
  }

  /**
   * Returns remote connection port for main stream
   * @return int port
   */
  public static int getRemotePort() {
    return remotePort;
  }

  /**
   * Returns host for services scan after connection
   * @return String services host
   */
  public static String getServicesHost() {
    return servicesHost;
  }

  /**
   * Returns rooms host to scan and create
   * @return String room host
   */
  public static String getRoomHost() {
    return roomHost;
  }

  /**
   * Returns session resource
   * @return String resource
   */
  public static String getResource() {
    return resource;
  }

  /**
   * Returns clear JID, concatinated from username and remote host
   * @return String clear JId
   */
  public static String getClearJid() {
    return userName.concat( "@" ).concat( remoteHost );
  }

  /**
   * Returns full JID, concatinated from clear JID and resource
   * @return 
   */
  public static String getFullJid() {
    return getClearJid().concat( "/" ).concat( resource );
  }

  /**
   * Generates random cookie for outgoing packets
   * @return 
   */
  public static String generateCookie() {
    return StringUtil.generateString( 8 );
  }

  /**
   * Returns host for registering users
   * @return String register host
   */
  public static String getRegisterHost() {
    return registerHost;
  }

  /**
   * Returns port for registering host
   * @return 
   */
  public static int getRegisterPort() {
    return registerPort;
  }

  /**
   * Returns use SSL on connection option
   * @return boolean SSL option
   */
  public static boolean getUseSsl() {
    return isUseSsl;
  }

  /**
   * Returns use SASL connection method option
   * @return 
   */
  public static boolean getUseSasl() {
    return isUseSasl;
  }

  /**
   * Returns status index of account (according to StatusUtil)
   * @return int status index
   */
  public static int getStatusIndex() {
    return statusIndex;
  }

  /**
   * Returns account priority that configures while connection
   * @return int priority
   */
  public static int getPriority() {
    return priority;
  }
}
