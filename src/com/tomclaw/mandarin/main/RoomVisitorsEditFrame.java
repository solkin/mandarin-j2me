package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.core.Handler;
import com.tomclaw.mandarin.molecus.AccountRoot;
import com.tomclaw.mandarin.molecus.Mechanism;
import com.tomclaw.mandarin.molecus.RoomItem;
import com.tomclaw.mandarin.molecus.Visitor;
import com.tomclaw.tcuilite.*;
import com.tomclaw.tcuilite.localization.Localization;
import com.tomclaw.utils.StringUtil;
import java.util.Vector;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class RoomVisitorsEditFrame extends Window {

  private final List list;
  private final RoomItem roomItem;
  private final String affiliation;

  public RoomVisitorsEditFrame( final RoomItem roomItem,
          final String affiliation, Vector items ) {
    super( MidletMain.screen );
    /** Main variables **/
    this.roomItem = roomItem;
    this.affiliation = affiliation;
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
    PopupItem addPopupItem = new PopupItem( Localization.getMessage( "ADD" ) );
    /** Adding all users from room item, that have real JID **/
    int statusFileHash = com.tomclaw.mandarin.core.Settings.IMG_STATUS.hashCode();
    for ( int c = 0; c < roomItem.resources.length; c++ ) {
      /** Checking for JID exists **/
      if ( !StringUtil.isNullOrEmpty( roomItem.resources[c].jid ) ) {
        /** Resource's JID **/
        final String jid = BuddyList.getClearJid( roomItem.resources[c].jid );
        /** Creating buddy popup item instance **/
        PopupItem buddyPopupItem = new PopupItem( roomItem.resources[c].resource ) {
          public void actionPerformed() {
            /** To prevent memory overload we use external method **/
            visitorAddConfirmationDialog( jid );
          }
        };
        /** Configuring popup item **/
        buddyPopupItem.imageFileHash = statusFileHash;
        buddyPopupItem.imageIndex = roomItem.resources[c].statusIndex;
        /** Adding popup item to the parent popup item **/
        addPopupItem.addSubItem( buddyPopupItem );
      }
    }
    addPopupItem.addSubItem( new PopupItem( Localization.getMessage( "OTHER" ) ) {
      public void actionPerformed() {
        MidletMain.screen.setActiveWindow( new RoomVisitorsAddFrame(
                RoomVisitorsEditFrame.this, roomItem, list.items, affiliation ) );
      }
    } );
    soft.leftSoft.addSubItem( addPopupItem );
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
        /** Checking for selected item is in real range **/
        if ( list.selectedIndex >= 0 && list.selectedIndex < list.items.size() ) {
          /** Obtain list item **/
          final Visitor visitor = ( Visitor ) list.getElement( list.selectedIndex );
          final Soft dialogSoft = new Soft( screen );
          dialogSoft.leftSoft = new PopupItem( Localization.getMessage( "CLOSE" ) ) {
            public void actionPerformed() {
              RoomVisitorsEditFrame.this.closeDialog();
            }
          };
          String moreInfoMessage = "JID: ".concat( visitor.jid ).concat( "\n" ).
                  concat( Localization.getMessage( "AFFILIATION" ) ).concat( " " ).
                  concat( Localization.getMessage( "TO_".
                  concat( visitor.affiliation.toUpperCase() ).concat( "S" ) ) );
          if ( visitor.reason != null && visitor.reason.length() > 0 ) {
            moreInfoMessage += "\n".concat( Localization.getMessage( "REASON" ) ).
                    concat( ": " ).concat( visitor.reason );
          }
          Handler.showDialog( RoomVisitorsEditFrame.this, dialogSoft,
                  "MORE_INFO", moreInfoMessage );
        }
      }
    } );
    /** Creating list object **/
    list = new List();
    /** Setting up items **/
    list.items = items;
    /** Setting up pane **/
    setGObject( list );
  }

  private void visitorAddConfirmationDialog( final String jid ) {
    final Soft dialogSoft = new Soft( screen );
    dialogSoft.leftSoft = new PopupItem( Localization.getMessage( "YES" ) ) {
      public void actionPerformed() {
        dialogSoft.rightSoft.actionPerformed();
        /** Mechanism invocation **/
        Mechanism.roomVisitorsListAddItem( list.items, roomItem, affiliation,
                jid, Localization.getMessage( "CHANGED_TO_".
                concat( affiliation.toUpperCase() ) ).
                concat( " " ).concat(
                BuddyList.getClearJid( AccountRoot.getFullJid() ) ) );
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
            concat( Localization.getMessage( "ADD_".
            concat( affiliation.toUpperCase() ) ) ).concat( " " ).
            concat( roomItem.getRoomTitle() ).
            concat( " " ).concat( Localization.getMessage( "FOR_".
            concat( affiliation.toUpperCase() ) ) ).concat( " " ).
            concat( jid ).concat( "?" ) );
  }
}
