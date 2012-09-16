package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.core.Handler;
import com.tomclaw.mandarin.molecus.AccountRoot;
import com.tomclaw.mandarin.molecus.Mechanism;
import com.tomclaw.mandarin.molecus.RoomItem;
import com.tomclaw.tcuilite.Field;
import com.tomclaw.tcuilite.Header;
import com.tomclaw.tcuilite.Label;
import com.tomclaw.tcuilite.Pane;
import com.tomclaw.tcuilite.PopupItem;
import com.tomclaw.tcuilite.Soft;
import com.tomclaw.tcuilite.Window;
import com.tomclaw.tcuilite.localization.Localization;
import java.util.Vector;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class RoomVisitorsAddFrame extends Window {

  private Field userNameField;
  private Field reasonField;

  public RoomVisitorsAddFrame( Window parent, final RoomItem roomItem, final Vector items, final String affiliation ) {
    super( MidletMain.screen );
    /** Previous window **/
    s_prevWindow = parent;
    /** Header **/
    header = new Header( Localization.getMessage( "VISITOR_ADD" ) );
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
    soft.leftSoft = new PopupItem( Localization.getMessage( "ADD" ) ) {
      public void actionPerformed() {
        String jid = userNameField.getText();
        /** Checking for username or JID is correct **/
        if ( jid.length() > 0 ) {
          if ( jid.indexOf( '@' ) != -1 ) {
            /** Manual JID **/
            /** Checking for host is exist **/
            if ( BuddyList.getJidHost( jid ).length() == 0 ) {
              Handler.showError( "EMPTY_JID_HOST" );
              return;
            }
            if ( BuddyList.getJidUsername( jid ).length() == 0 ) {
              Handler.showError( "EMPTY_JID_USERNAME" );
              return;
            }
          } else {
            /** Molecus user **/
            jid += "@".concat( AccountRoot.getRemoteHost() );
          }
          /** Mechanism invocation **/
          Mechanism.roomVisitorsListAddItem( items, roomItem, affiliation,
                  jid, reasonField.getText() );
          /** Returning to the previous frame **/
          MidletMain.screen.setActiveWindow( s_prevWindow );
        } else {
          Handler.showError( "EMPTY_USERNAME_OR_JID" );
        }
      }
    };
    /** Creating pane object **/
    Pane pane = new Pane( null, false );
    /** Creating pane objects **/
    /** Affiliation type label **/
    Label topicOfLabel = new Label( Localization.getMessage( "ADDING_TO" ).
            concat( " " ).concat( Localization.getMessage(
            "TO_".concat( affiliation.toUpperCase() ).concat( "S" ) ) ) );
    topicOfLabel.setHeader( true );
    pane.addItem( topicOfLabel );
    /** Username label **/
    pane.addItem( new Label(
            Localization.getMessage( "ENTER_USERNAME_OR_JID" ).concat( ":" ) ) );
    /** User username **/
    userNameField = new Field( "" );
    userNameField.setFocused( true );
    pane.addItem( userNameField );
    /** Reason label **/
    pane.addItem( new Label( Localization.getMessage( "ENTER_REASON" ).
            concat( ":" ) ) );
    /** Reason field **/
    reasonField = new Field( "" );
    reasonField.setText( Localization.getMessage( "CHANGED_TO_".
            concat( affiliation.toUpperCase() ) ).
            concat( " " ).concat(
            BuddyList.getClearJid( AccountRoot.getFullJid() ) ) );
    pane.addItem( reasonField );
    /** Setting up pane **/
    setGObject( pane );
  }
}
