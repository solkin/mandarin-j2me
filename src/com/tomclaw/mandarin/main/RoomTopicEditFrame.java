package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.molecus.RoomItem;
import com.tomclaw.tcuilite.Field;
import com.tomclaw.tcuilite.Header;
import com.tomclaw.tcuilite.Label;
import com.tomclaw.tcuilite.Pane;
import com.tomclaw.tcuilite.PopupItem;
import com.tomclaw.tcuilite.Soft;
import com.tomclaw.tcuilite.Window;
import com.tomclaw.tcuilite.localization.Localization;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class RoomTopicEditFrame extends Window {
  
  private Field updatedTopicField;

  public RoomTopicEditFrame( final RoomItem roomItem ) {
    super( MidletMain.screen );
    /** Previous window **/
    s_prevWindow = MidletMain.mainFrame;
    /** Header **/
    header = new Header( Localization.getMessage( "ROOM_EDIT_TOPIC" ).
            concat( ": " ).concat( roomItem.getUserName() ) );
    /** Soft **/
    soft = new Soft( screen );
    /** Right soft **/
    soft.rightSoft = new PopupItem( Localization.getMessage( "BACK" ) ) {

      public void actionPerformed() {
        /** Returning to the previous frame **/
        MidletMain.screen.setActiveWindow( s_prevWindow );
      }
    };
    /** Left soft **/
    soft.leftSoft = new PopupItem( Localization.getMessage( "SAVE" ) ) {

      public void actionPerformed() {
        /** Showing wait screen **/
        MidletMain.screen.setWaitScreenState( true );
      }
    };
    /** Creating pane object **/
    Pane pane = new Pane( null, false );
    /** Creating pane objects **/
    /** Currecnt nick label **/
    Label topicOfLabel = new Label( Localization.getMessage( "TOPIC_OF" ).concat( " " ).concat( roomItem.getRoomTitle() ) );
    topicOfLabel.setHeader( true );
    pane.addItem( topicOfLabel );
    /** Edit topic **/
    updatedTopicField = new Field( roomItem.getRoomTopic() );
    updatedTopicField.setFocused( true );
    pane.addItem( updatedTopicField );
    /** Setting up pane **/
    setGObject( pane );
  }
}
