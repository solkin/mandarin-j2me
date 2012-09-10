package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.molecus.RoomItem;
import com.tomclaw.tcuilite.*;
import com.tomclaw.tcuilite.localization.Localization;
import java.util.Vector;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class RoomVisitorsEditFrame extends Window {

  public RoomVisitorsEditFrame( final RoomItem roomItem,
          final String affiliation, Vector items ) {
    super( MidletMain.screen );
    /** Previous window **/
    s_prevWindow = MidletMain.mainFrame;
    /** Header **/
    header = new Header( Localization.getMessage( "ROOM_".concat( affiliation.toUpperCase() ).concat( "_LIST" ) ).
            concat( " " ).concat( roomItem.getRoomTitle() ) );
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
    soft.leftSoft = new PopupItem( Localization.getMessage( "MENU" ) );
    soft.leftSoft.addSubItem( new PopupItem( Localization.getMessage( "ADD" ) ) {
      public void actionPerformed() {
      }
    } );
    soft.leftSoft.addSubItem( new PopupItem( Localization.getMessage( "REMOVE" ) ) {
      public void actionPerformed() {
      }
    } );
    soft.leftSoft.addSubItem( new PopupItem( Localization.getMessage( "MORE_INFO" ) ) {
      public void actionPerformed() {
      }
    } );
    /** Creating list object **/
    List list = new List();
    /** Setting up items **/
    list.items = items;
    /** Setting up pane **/
    setGObject( list );
  }
}
