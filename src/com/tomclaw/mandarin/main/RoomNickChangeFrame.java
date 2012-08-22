package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.molecus.RoomItem;
import com.tomclaw.tcuilite.Check;
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
public class RoomNickChangeFrame extends Window {
  
  private Field updatedNickField;
  private Check updateBookmarkNick;

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

      }
    };
    /** Creating pane object **/
    Pane pane = new Pane( null, false );
    /** Creating pane objects **/
    pane.addItem( new Label( Localization.getMessage( "CURRENT_NICK_IS" ) ) );
    /** Current nick **/
    Field currentNickField = new Field(roomItem.getRoomNick());
    currentNickField.setFocusable( false );
    pane.addItem( currentNickField );
    /** Edit nick **/
    pane.addItem( new Label( Localization.getMessage( "SATISFY_NICK" ) ) );
    updatedNickField = new Field(roomItem.getRoomNick());
    updatedNickField.setFocused( true );
    pane.addItem( updatedNickField );
    /** Update bookmark checkbox **/
    updateBookmarkNick = new Check(Localization.getMessage( "UPDATE_BOOKMARK_NICK" ), true);
    pane.addItem( updateBookmarkNick );
    /** Setting up pane **/
    setGObject( pane );
  }
}
