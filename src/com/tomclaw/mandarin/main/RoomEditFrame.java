package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.core.Handler;
import com.tomclaw.mandarin.molecus.Mechanism;
import com.tomclaw.mandarin.molecus.RoomItem;
import com.tomclaw.tcuilite.*;
import com.tomclaw.tcuilite.localization.Localization;
import com.tomclaw.utils.LogUtil;
import com.tomclaw.utils.StringUtil;
import javax.microedition.lcdui.TextField;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class RoomEditFrame extends Window {

  private Field nameField;
  private Check autoJounCheck;
  private Field nickField;
  private Field passwordField;

  public RoomEditFrame( final RoomItem roomItem ) {
    super( MidletMain.screen );
    /** Previous window **/
    s_prevWindow = MidletMain.mainFrame;
    /** Header **/
    header = new Header( Localization.getMessage( "ROOM_EDIT" ).
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
    soft.leftSoft = new PopupItem( Localization.getMessage( "SAVE" ) ) {

      public void actionPerformed() {
        /** Showing wait screen **/
        MidletMain.screen.setWaitScreenState( true );
        updateRoomItemAttempt( roomItem, roomItem.getTemp(), nameField.getText(),
                nickField.getText(), passwordField.getText(),
                autoJounCheck.state, true );
      }
    };
    /** Creating pane object **/
    Pane pane = new Pane( null, false );
    /** Creating pane objects **/
    pane.addItem( new Label( Localization.getMessage( "SET_ROOM_NAME" ) ) );
    nameField = new Field( StringUtil.isNullOrEmpty(
            roomItem.getNickName() ) ? "" : roomItem.getNickName() );
    nameField.setFocused( true );
    pane.addItem( nameField );
    /** Autojoin check **/
    autoJounCheck = new Check(
            Localization.getMessage( "AUTO_JOIN" ), roomItem.getAutoJoin() );
    pane.addItem( autoJounCheck );
    /** Nick name **/
    pane.addItem( new Label( Localization.getMessage( "SET_ROOM_NICK" ) ) );
    nickField = new Field( StringUtil.isNullOrEmpty(
            roomItem.getRoomNick() ) ? "" : roomItem.getRoomNick() );
    pane.addItem( nickField );
    /** Password **/
    pane.addItem( new Label(
            Localization.getMessage( "SET_ROOM_PASSWORD" ) ) );
    passwordField = new Field( StringUtil.isNullOrEmpty(
            roomItem.getRoomPassword() ) ? "" : roomItem.getRoomPassword() );
    passwordField.setConstraints( TextField.PASSWORD );
    pane.addItem( passwordField );
    /** Setting up pane **/
    setGObject( pane );
  }

  public static void updateRoomItemAttempt( RoomItem roomItem, boolean isTemp, String name, String nick, String password, boolean isAutoJoin, boolean isSoloOperation ) {
    /** Checking for room name and nick field is empty **/
    if ( !StringUtil.isEmptyOrNull( name )
            && !StringUtil.isEmptyOrNull( nick ) ) {
      /** Checking for room item is temporary **/
      if ( isTemp ) {
        /** Updating local bookmark information **/
        roomItem.setNickName( name );
        roomItem.setRoomNick( nick );
        roomItem.setRoomPassword( password );
        roomItem.setMinimize( roomItem.getMinimize() );
        roomItem.setAutoJoin( isAutoJoin );
        roomItem.updateUi();
        /** Checking for solo operation **/
        if ( isSoloOperation ) {
          /** Returning to main frame **/
          Handler.showMainFrame();
        }
      } else {
        /** Creating new room instance **/
        RoomItem item = new RoomItem( roomItem.getJid(), name, roomItem.getMinimize(), isAutoJoin );
        /** Updating parameters **/
        item.setRoomNick( nick );
        item.setRoomPassword( password );
        /** Mechanism invocation **/
        Mechanism.sendBookmarksOperation( Mechanism.OPERATION_EDIT, item, roomItem, false, isSoloOperation );
      }
      return;
    }
    LogUtil.outMessage( "Incorrect input" );
    Handler.showError( "INCORRECT_INPUT" );
  }
}
