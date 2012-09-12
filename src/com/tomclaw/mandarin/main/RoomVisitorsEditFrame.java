package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.core.Handler;
import com.tomclaw.mandarin.molecus.Mechanism;
import com.tomclaw.mandarin.molecus.RoomItem;
import com.tomclaw.mandarin.molecus.Visitor;
import com.tomclaw.tcuilite.*;
import com.tomclaw.tcuilite.localization.Localization;
import java.util.Vector;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class RoomVisitorsEditFrame extends Window {

  private final List list;

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
        /** Checking for selected item is in real range **/
        if ( list.selectedIndex >= 0 && list.selectedIndex < list.items.size() ) {
          /** Obtain list item **/
          final Visitor visitor = ( Visitor ) list.getElement( list.selectedIndex );
          final Soft dialogSoft = new Soft( screen );
          dialogSoft.leftSoft = new PopupItem( Localization.getMessage( "YES" ) ) {
            public void actionPerformed() {
              dialogSoft.rightSoft.actionPerformed();
              /** Mechanism invocation **/
              Mechanism.roomVisitorsListRemoveItem( list.items, roomItem, visitor.jid );
            }
          };
          dialogSoft.rightSoft = new PopupItem( Localization.getMessage( "NO" ) ) {
            public void actionPerformed() {
              RoomVisitorsEditFrame.this.closeDialog();
            }
          };
          Handler.showDialog( RoomVisitorsEditFrame.this, dialogSoft,
                  "REMOVING",
                  Localization.getMessage( "SURE" ).concat( " " ).
                  concat( Localization.getMessage( "REMOVE_".
                  concat( affiliation.toUpperCase() ) ) ).concat( " " ).
                  concat( roomItem.getRoomTitle() ).
                  concat( ", " ).concat( visitor.jid ).concat( ", " ).
                  concat( Localization.getMessage( "FROM_ROOM_LISTS" ) ) );
        }
      }
    } );
    soft.leftSoft.addSubItem( new PopupItem( Localization.getMessage( "MORE_INFO" ) ) {
      public void actionPerformed() {
      }
    } );
    /** Creating list object **/
    list = new List();
    /** Setting up items **/
    list.items = items;
    /** Setting up pane **/
    setGObject( list );
  }
}
