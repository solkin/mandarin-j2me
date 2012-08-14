package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.molecus.BuddyItem;
import com.tomclaw.mandarin.molecus.GroupItem;
import com.tomclaw.mandarin.molecus.Mechanism;
import com.tomclaw.tcuilite.*;
import com.tomclaw.tcuilite.localization.Localization;
import com.tomclaw.utils.StringUtil;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class BuddyEditFrame extends Window {

  private Field nameField;
  private ObjectGroup groupsCheck;
  private Field groupField;

  public BuddyEditFrame ( final BuddyItem buddyItem ) {
    super ( MidletMain.screen );
    /** Previous window **/
    s_prevWindow = MidletMain.mainFrame;
    /** Header **/
    header = new Header ( Localization.getMessage ( "BUDDY_EDIT" ).
            concat ( ": " ).concat ( buddyItem.getUserName () ) );
    /** Soft **/
    soft = new Soft ( screen );
    /** Right soft **/
    soft.rightSoft = new PopupItem ( Localization.getMessage ( "BACK" ) ) {

      public void actionPerformed () {
        /** Returning to the previous frame **/
        MidletMain.screen.setActiveWindow ( s_prevWindow );
      }
    };
    /** Left soft **/
    soft.leftSoft = new PopupItem ( Localization.getMessage ( "SAVE" ) ) {

      public void actionPerformed () {
        /** Showing wait screen **/
        MidletMain.screen.setWaitScreenState ( true );
        /** Creating selected groups array **/
        String[] groups = new String[ groupsCheck.items.size () + 1 ];
        for ( int c = 0; c < groupsCheck.items.size (); c++ ) {
          if ( ( ( Check ) groupsCheck.items.elementAt ( c ) ).state ) {
            groups[c] = ( ( Check ) groupsCheck.items.elementAt ( c ) ).caption;
          }
        }
        /** Custom group **/
        if ( !StringUtil.isEmptyOrNull ( groupField.getText () ) ) {
          groups[groups.length - 1] = groupField.getText ();
        }
        String nickName = nameField.getText ();
        /** Checking for buddy name field is empty **/
        if ( StringUtil.isEmptyOrNull ( nickName ) ) {
          /** Applying user name as nick name **/
          nickName = buddyItem.getUserName ();
        }
        /** Running mechanism method **/
        Mechanism.rosterEditRequest ( buddyItem.getJid (), nickName, null, groups, false );
      }
    };
    /** Creating pane object **/
    Pane pane = new Pane ( null, false );
    /** Creating pane objects **/
    pane.addItem ( new Label ( Localization.getMessage ( "SET_BUDDY_NAME" ) ) );
    nameField = new Field ( buddyItem.getNickName () );
    nameField.setFocused ( true );
    pane.addItem ( nameField );
    /** Object group **/
    Label label = new Label ( Localization.getMessage ( "SET_GROUPS" ) );
    pane.addItem ( label );
    groupsCheck = new ObjectGroup ();
    for ( int c = 0; c < MidletMain.mainFrame.buddyList.items.size (); c++ ) {
      /** Obtain group from roster **/
      GroupItem groupItem = ( GroupItem ) MidletMain.mainFrame.buddyList.items.elementAt ( c );
      /** Checking for default group id **/
      if ( groupItem.internalGroupId == GroupItem.GROUP_DEFAULT_ID ) {
        /** Creating check object **/
        Check check = new Check ( groupItem.getGroupName (), groupItem.isContainBuddy ( buddyItem ) );
        /** Adding check to object group and pane **/
        groupsCheck.placeObject ( check );
        pane.addItem ( check );
      }
    }
    /** Checking for zero group count **/
    if ( groupsCheck.items.isEmpty () ) {
      /** Replcaing label caption **/
      label.setCaption ( Localization.getMessage ( "ENT_CUST_GROUP" ) );
    } else {
      /** Custom group field **/
      pane.addItem ( new Label (
              Localization.getMessage ( "SET_CUSTOM_GROUP" ) ) );
    }
    groupField = new Field ( "" );
    pane.addItem ( groupField );
    /** Setting up pane **/
    setGObject ( pane );
  }
}
