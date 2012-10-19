package com.tomclaw.mandarin.main;

import com.tomclaw.images.Splitter;
import com.tomclaw.mandarin.core.Handler;
import com.tomclaw.mandarin.core.Settings;
import com.tomclaw.mandarin.core.Storage;
import com.tomclaw.tcuilite.Screen;
import com.tomclaw.tcuilite.Theme;
import com.tomclaw.tcuilite.localization.Localization;
import com.tomclaw.utils.LogUtil;
import com.tomclaw.utils.TimeUtil;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class MidletMain extends MIDlet {

  /** Core **/
  public static String version;
  public static String type;
  public static String build;
  /** Main **/
  public static MidletMain midletMain;
  /** GUI **/
  public static Screen screen;
  public static MainFrame mainFrame;
  public static ChatFrame chatFrame;
  public static RoomsFrame roomsFrame;

  public void startApp() {
    LogUtil.outMessage( "startApp invoked" );
    /** Core **/
    version = getAppProperty( "MIDlet-Version" );
    type = getAppProperty( "Type" );
    build = getAppProperty( "Build" );
    /** Main **/
    midletMain = this;
    /** Logger for debugging **/
    LogUtil.initLogger( true, false, "92.36.93.99", 2000, false, "" );
    /** Initialize localization support **/
    Localization.initLocalizationSupport();
    /** GUI**/
    screen = new Screen( this );
    /** Loading storage data **/
    Storage.init();
    Storage.load();
    /** Loading settings **/
    Settings.loadAll();
    /** Loading icons **/
    Splitter.splitImage( Settings.IMG_CHAT );
    Splitter.splitImage( Settings.IMG_STATUS );
    Splitter.splitImage( Settings.IMG_SUBSCRIPTION );
    /** Loading theme **/
    Theme.checkForUpSize();
    int[] data = Theme.loadTheme( Settings.themeOfflineResPath );
    if ( data != null ) {
      Theme.applyData( data );
    }
    /** GUI **/
    mainFrame = new MainFrame();
    chatFrame = new ChatFrame();
    roomsFrame = null;
    /** Frames chain **/
    mainFrame.s_nextWindow = chatFrame;
    chatFrame.s_prevWindow = mainFrame;
    /** Setting up active window **/
    screen.activeWindow = mainFrame;
    /** Showing **/
    screen.show();
    /** Checking for main frame object **/
    if ( mainFrame.getGObject().equals( mainFrame.blank ) ) {
      /** Showing warning **/
      Handler.showDialog( "WARNING", "TEST_VERSION" );
    }
    //  final String URL_SYMBOLS = "%/?&=$-_.+!*'(),0123456789abcdefghij"
    //          + "klmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // final char[] URL_SYMBOLS = new char[]{ '%', '/', '?', '&', '=', '$', '-', '_', '.', '+', '!', '*', '\'', '(', ')', ',', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    final String URL_SYMBOLS = "%/?&=$-_.+!*'(),0123456789abcdefghij"
          + "klmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String message = "";
    long time = System.currentTimeMillis();
    for ( int i = 0; i < 100; i++ ) {
      message = "Текст, в котором есть ссылка на http://google.ru и "
              + "www.mail.ru без всякой на то рекламы. https://www.molecus.com тоже не реклама. "
              + "Текст, в котором есть ссылка на http://google.ru и "
              + "www.mail.ru без всякой на то рекламы. https://www.molecus.com тоже не реклама. "
              + "Текст, в котором есть ссылка на http://google.ru и "
              + "www.mail.ru без всякой на то рекламы. https://www.molecus.com тоже не реклама. "
              + "Текст, в котором есть ссылка на http://google.ru и "
              + "www.mail.ru без всякой на то рекламы. https://www.molecus.com тоже не реклама. "
              + "Текст, в котором есть ссылка на http://google.ru и "
              + "www.mail.ru без всякой на то рекламы. https://www.molecus.com тоже не реклама. "
              + "Текст, в котором есть ссылка на http://google.ru и "
              + "www.mail.ru без всякой на то рекламы. https://www.molecus.com тоже не реклама. "
              + "Текст, в котором есть ссылка на http://google.ru и "
              + "www.mail.ru без всякой на то рекламы. https://www.molecus.com тоже не реклама. "
              + "Текст, в котором есть ссылка на http://google.ru и "
              + "www.mail.ru без всякой на то рекламы. https://www.molecus.com тоже не реклама. ";
      int linkStartIndex = -1;
      if ( message.indexOf( "http://" ) != -1
              || message.indexOf( "https://" ) != -1
              || message.indexOf( "www." ) != -1 ) {
        for ( int c = 0; c < message.length(); c++ ) {
          if ( linkStartIndex == -1 ) {
            if ( message.startsWith( "http://", c ) ) {
              linkStartIndex = c;
              c += 7;
              continue;
            } else if ( message.startsWith( "https://", c ) ) {
              linkStartIndex = c;
              c += 8;
              continue;
            } else if ( message.startsWith( "www.", c ) ) {
              linkStartIndex = c;
              c += 4;
              continue;
            }
          }
          if ( linkStartIndex != -1 ) {
            if ( URL_SYMBOLS.indexOf( message.charAt( c ) ) == -1 ) {
              message = message.substring( 0, linkStartIndex ).
                      concat( "[c=blue][i][u]" ).concat( message.substring( linkStartIndex, c ) )
                      .concat( "[/u][/i][/c]" ).concat( message.substring( c ) );
              c += 26;
              linkStartIndex = -1;
            }
          }
        }
      }
    }
    System.out.println( "time: " + ( System.currentTimeMillis() - time ) );
    System.out.println( message );
  }

  public static long getLongOfStamp( String stamp, boolean isLocalized ) {
    /** Time from stamp by XEP-0082 **/
    int tIndex = stamp.indexOf( 'T' );
    /** Checking for date and time **/
    if ( tIndex != -1 ) {
      String date = stamp.substring( 0, tIndex );
      /** Calculating time **/
      int yIndex = date.indexOf( '-' );
      int rYears = Integer.parseInt( date.substring( 0, yIndex ) );
      int mIndex = date.indexOf( '-', yIndex + 1 );
      int rMonths = Integer.parseInt( date.substring( yIndex + 1, mIndex ) );
      int rDays = Integer.parseInt( date.substring( mIndex + 1 ) );
      LogUtil.outMessage( "Real years: " + rYears );
      LogUtil.outMessage( "Real months: " + rMonths );
      LogUtil.outMessage( "Real days: " + rDays );
      String time = stamp.substring( tIndex + 1 );
      LogUtil.outMessage( "Date: " + date );
      LogUtil.outMessage( "Time: " + time );
      int zIndex = Math.max( time.indexOf( '+' ), time.indexOf( '-' ) );
      zIndex = Math.max( zIndex, time.indexOf( 'Z' ) );
      /** Checking for time zone **/
      if ( zIndex != -1 ) {
        String clearTime = time.substring( 0, zIndex );
        String timeZone = time.substring( zIndex );
        LogUtil.outMessage( "Clear time: " + clearTime );
        LogUtil.outMessage( "Time zone: " + timeZone );
        /** Calculating time **/
        int hIndex = clearTime.indexOf( ':' );
        int rHours = Integer.parseInt( clearTime.substring( 0, hIndex ) );
        mIndex = clearTime.indexOf( ':', hIndex + 1 );
        int rMinutes = Integer.parseInt( clearTime.substring( hIndex + 1, mIndex ) );
        int rSeconds = Integer.parseInt( clearTime.substring( mIndex + 1 ) );
        LogUtil.outMessage( "Real hours: " + rHours );
        LogUtil.outMessage( "Real minutes: " + rMinutes );
        LogUtil.outMessage( "Real seconds: " + rSeconds );
        long timeLong = TimeUtil.createTimeLong( rYears, rMonths, rDays, rHours, rMinutes, rSeconds );
        /** Checking for UTC **/
        if ( !timeZone.equals( "Z" ) && !isLocalized ) {
          boolean isPlus = ( timeZone.charAt( 0 ) == '+' );
          int dIndex = timeZone.indexOf( ':' );
          /** Checking for delimiter **/
          if ( dIndex != -1 ) {
            timeLong -= ( ( ( isPlus ? 1 : -1 )
                    * Integer.parseInt( timeZone.substring( 1, dIndex ) ) ) * 60
                    + Integer.parseInt( timeZone.substring( dIndex + 1 ) ) ) * 60;
          }
        } else {
          timeLong += TimeUtil.getGmtOffset();
        }
        LogUtil.outMessage( "timeLong: " + timeLong );
        LogUtil.outMessage( "Time real: " + TimeUtil.getUtcTimeString( timeLong ) );
      }
    }
    return 0;
  }

  public void pauseApp() {
    LogUtil.outMessage( "pauseApp invoked" );
    notifyPaused();
  }

  public void destroyApp( boolean unconditional ) {
    LogUtil.outMessage( "destroyApp invoked" );
  }

  /**
   * Exit application event
   */
  public static void exitApp() {
    /** Checking for undefined account **/
    if ( mainFrame.buddyList != null ) {
      mainFrame.buddyList.updateOfflineBuddylist();
    }
    /** Destroying **/
    midletMain.notifyDestroyed();
  }

  /**
   * Minimizing application event
   */
  public static void minimizeApp() {
    Display.getDisplay( midletMain ).setCurrent( null );
  }
}
