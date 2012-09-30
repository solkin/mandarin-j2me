package com.tomclaw.mandarin.molecus;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class StatusUtil {

  public static String[] statuses = new String[]{ "offline", "online", "away", "chat", "invisible", "xa", "dnd" };
  public static int offlineIndex = 0;
  public static int onlineIndex = 1;
  /** Protocol offsets **/
  public static int xmppOffset = 0;
  public static int roomOffset = 7;
  public static int icqOffset = 9;
  public static int mmpOffset = 16;

  public static int getStatusIndex( String status ) {
    for ( int c = 0; c < statuses.length; c++ ) {
      if ( statuses[c].equals( status ) ) {
        return c;
      }
    }
    return 0;
  }

  public static int getStatusCount() {
    return statuses.length;
  }

  public static String getStatusDescr( int index ) {
    return "STATUS_" + statuses[index].toUpperCase();
  }

  public static String getStatus( int index ) {
    return statuses[index];
  }
}
