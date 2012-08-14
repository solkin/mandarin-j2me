package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.core.Handler;
import com.tomclaw.mandarin.molecus.AccountRoot;
import com.tomclaw.mandarin.molecus.GroupItem;
import com.tomclaw.mandarin.molecus.Mechanism;
import com.tomclaw.tcuilite.*;
import com.tomclaw.tcuilite.localization.Localization;
import com.tomclaw.utils.LogUtil;
import com.tomclaw.utils.StringUtil;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class BuddyAddFrame extends Window {

  private Field jidField;
  private Field nameField;
  private ObjectGroup groupsCheck;
  private Field groupField;

  public BuddyAddFrame ( final String serviceHost, String descr, String prompt ) {
    super ( MidletMain.screen );
    /** Previous window **/
    s_prevWindow = MidletMain.mainFrame;
    /** Header **/
    header = new Header ( Localization.getMessage ( "BUDDY_ADD" ) );
    /** Soft **/
    soft = new Soft ( screen );
    /** Right soft **/
    soft.rightSoft = new PopupItem ( Localization.getMessage ( "BACK" ) ) {

      public void actionPerformed () {
        MidletMain.screen.setActiveWindow ( s_prevWindow );
      }
    };
    /** Left soft **/
    soft.leftSoft = new PopupItem ( Localization.getMessage ( "ADD" ) ) {

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
        /** Running mechanism method **/
        if ( serviceHost.equals ( "SERVICE_MOLECUS" ) ) {
          /** Checking molecus username **/
          if ( !StringUtil.isEmptyOrNull ( jidField.getText () )
                  && jidField.getText ().indexOf ( '@' ) == -1
                  && jidField.getText ().indexOf ( " " ) == -1
                  && !jidField.getText ().equals ( AccountRoot.getUserName () ) ) {
            Mechanism.rosterAddRequest ( jidField.getText ().concat ( "@" ).
                    concat ( AccountRoot.getRemoteHost () ),
                    nameField.getText (), groups );
            return;
          }
        } else if ( serviceHost.equals ( "SERVICE_XMPP" ) ) {
          if ( !StringUtil.isEmptyOrNull ( jidField.getText () )
                  && jidField.getText ().indexOf ( '@' ) != -1
                  && jidField.getText ().indexOf ( " " ) == -1
                  && !jidField.getText ().equals ( AccountRoot.getFullJid () ) ) {
            Mechanism.rosterAddRequest ( jidField.getText (),
                    nameField.getText (), groups );
            return;
          }
        } else {
          Mechanism.invokePromptSendAddBuddy ( serviceHost, jidField.getText (),
                  nameField.getText (), groups );
          return;
        }
        LogUtil.outMessage ( "Incorrect input" );
        Handler.showError ( "INCORRECT_INPUT" );
      }
    };
    /** Creating pane object **/
    Pane pane = new Pane ( null, false );
    /** Creating pane objects **/
    pane.addItem ( new Label ( descr ) );
    if ( prompt.length () > 0 ) {
      pane.addItem ( new Label ( prompt ) );
    }
    jidField = new Field ( "" );
    jidField.setFocused ( true );
    pane.addItem ( jidField );
    pane.addItem ( new Label ( Localization.getMessage ( "SET_BUDDY_NAME" ) ) );
    nameField = new Field ( "" );
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
        Check check = new Check ( groupItem.getGroupName (), false );
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
