package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.molecus.Command;
import com.tomclaw.mandarin.molecus.Form;
import com.tomclaw.mandarin.molecus.Item;
import com.tomclaw.tcuilite.*;
import com.tomclaw.tcuilite.localization.Localization;
import com.tomclaw.utils.LogUtil;
import java.util.Vector;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class CommandFrame extends Window {

  /** Core **/
  private Item item;
  private Form form;
  /** GUI **/
  private Pane pane;

  /**
   * Constructor to build form with default right "Closed" soft
   * @param item 
   */
  public CommandFrame( Item item ) {
    this( item, null );
  }

  /**
   * Constructor to build form with specified right soft
   * @param item
   * @param rightSoft 
   */
  public CommandFrame( Item item, PopupItem rightSoft ) {
    /** Settings up screen**/
    super( MidletMain.screen );
    /** Setting up left frame **/
    s_prevWindow = MidletMain.mainFrame;
    /** Setting up item **/
    this.item = item;
    /** Header **/
    header = new Header( item.name );
    /** Soft **/
    soft = new Soft( screen );
    /** Checking for right soft is not defined **/
    if ( rightSoft == null ) {
      /** Creating default and setting up right soft **/
      soft.rightSoft = new PopupItem( Localization.getMessage( "CLOSE" ) ) {

        public void actionPerformed() {
          MidletMain.screen.setActiveWindow( MidletMain.mainFrame );
        }
      };
    } else {
      /** Setting up right soft **/
      soft.rightSoft = rightSoft;
    }
    /** Initialize GObject **/
    pane = new Pane( null, false );
    /** Setting up GObject **/
    setGObject( pane );
  }

  public void setFormData( Form form ) {
    /** Updating form object **/
    this.form = form;
    /** Checking for soft existance **/
    if ( form.leftSoft != null && !form.leftSoft.isEmpty() ) {
      LogUtil.outMessage( "Left soft present" );
      /** Setting up item instance **/
      for ( int c = 0; c < form.leftSoft.subPopup.items.size(); c++ ) {
        ( ( Command ) form.leftSoft.subPopup.items.elementAt( c ) ).item = item;
      }
      /** Checking for childs count **/
      if ( form.leftSoft.subPopup.items.size() == 1 ) {
        soft.leftSoft =
                ( Command ) form.leftSoft.subPopup.items.firstElement();
      } else {
        soft.leftSoft = form.leftSoft;
      }
    } else {
      /** There is no any command **/
      soft.leftSoft = null;
    }
    /** Setting up form objects **/
    setFrameObjects( form.objects );
  }

  private void setFrameObjects( Vector objects ) {
    /** Accepting items to current pane **/
    pane.items = objects;
    /** Hiding wait notify **/
    screen.setWaitScreenState( false );
    /** Checking frame state **/
    if ( screen.activeWindow.equals( CommandFrame.this ) ) {
      /** Repainting **/
      screen.repaint( Screen.REPAINT_STATE_PLAIN );
    } else {
      prepareGraphics();
    }
  }
}
