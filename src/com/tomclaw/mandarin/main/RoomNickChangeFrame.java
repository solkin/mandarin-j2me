package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.molecus.Mechanism;
import com.tomclaw.mandarin.molecus.RoomItem;
import com.tomclaw.tcuilite.*;
import com.tomclaw.tcuilite.localization.Localization;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class RoomNickChangeFrame extends Window {

  private Field updatedNickField;

  public RoomNickChangeFrame( final RoomItem roomItem ) {
    super( MidletMain.screen );
    /** Previous window **/
    s_prevWindow = MidletMain.mainFrame;
    /** Header **/
    header = new Header( Localization.getMessage( "ROOM_CHANGENICK" ).
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
        /** Checking for nick is changed **/
        if ( roomItem.getRoomNick().equals( updatedNickField.getText() ) ) {
          /** Returning to the previous frame **/
          MidletMain.screen.setActiveWindow( s_prevWindow );
        } else {
          /** Updating nick in room item 
           * in any case of bookmark state, because 
           * presence will be sent according 
           * to this this nick **/
          roomItem.setRoomNick( updatedNickField.getText() );
          /** Sending updated nick in presence by mechanism **/
          Mechanism.changeRoomNickRequest( roomItem );
        }
      }
    };
    /** Creating pane object **/
    Pane pane = new Pane( null, false );
    /** Creating pane objects **/
    /** Currecnt nick label **/
    Label currentNickLabel = new Label( Localization.getMessage( "CURRENT_NICK_IS" ).concat( " " ).concat( roomItem.getRoomNick() ) );
    currentNickLabel.setHeader( true );
    pane.addItem( currentNickLabel );
    /** Edit nick **/
    pane.addItem( new Label( Localization.getMessage( "SATISFY_NICK" ) ) );
    updatedNickField = new Field( roomItem.getRoomNick() );
    updatedNickField.setFocused( true );
    pane.addItem( updatedNickField );
    /** Warning label **/
    pane.addItem( new Label( Localization.getMessage( "NICK_AS_DEFAULT" ).concat( " " ).concat( roomItem.getRoomTitle() ) ) );
    /** Setting up pane **/
    setGObject( pane );
  }
}
