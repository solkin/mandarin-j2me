package com.tomclaw.mandarin.main;

import com.tomclaw.images.Splitter;
import com.tomclaw.mandarin.core.Settings;
import com.tomclaw.mandarin.core.Storage;
import com.tomclaw.tcuilite.Screen;
import com.tomclaw.tcuilite.Theme;
import com.tomclaw.tcuilite.localization.Localization;
import com.tomclaw.utils.LogUtil;
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
