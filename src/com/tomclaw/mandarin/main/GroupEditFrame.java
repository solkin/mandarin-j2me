package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.core.Handler;
import com.tomclaw.mandarin.molecus.GroupItem;
import com.tomclaw.mandarin.molecus.Mechanism;
import com.tomclaw.tcuilite.Check;
import com.tomclaw.tcuilite.Field;
import com.tomclaw.tcuilite.Header;
import com.tomclaw.tcuilite.Label;
import com.tomclaw.tcuilite.ObjectGroup;
import com.tomclaw.tcuilite.Pane;
import com.tomclaw.tcuilite.PopupItem;
import com.tomclaw.tcuilite.Soft;
import com.tomclaw.tcuilite.Window;
import com.tomclaw.tcuilite.localization.Localization;
import com.tomclaw.utils.StringUtil;

/**
 *
 * @author solkin
 */
public class GroupEditFrame extends Window {

  private Field nameField;
  private ObjectGroup groupsCheck;

  public GroupEditFrame( final GroupItem groupItem ) {
    super( MidletMain.screen );

    /** Previous window **/
    s_prevWindow = MidletMain.mainFrame;
    /** Header **/
    header = new Header( Localization.getMessage( "GROUP_RENAME" ).
            concat( ": " ).concat( groupItem.getGroupName() ) );
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
        boolean isSomethingChanged = false;
        /** Creating selected groups array **/
        String[] groups = new String[ groupsCheck.items.size() + 1 ];
        for ( int c = 0; c < groupsCheck.items.size(); c++ ) {
          if ( ( ( Check ) groupsCheck.items.elementAt( c ) ).state ) {
            groups[c] = ( ( Check ) groupsCheck.items.elementAt( c ) ).caption;
            isSomethingChanged = true;
          } else {
            groups[c] = null;
          }
        }
        if ( !nameField.getText().equals( groupItem.getGroupName() ) ) {
          /** Renamed group **/
          groups[groups.length - 1] = nameField.getText();
          isSomethingChanged = true;
        }
        /** Checking for something changed **/
        if ( isSomethingChanged ) {
          /** Checking for new name is General 
           * or Temporary and cannot be used **/
          if ( nameField.getText().equals( Localization.getMessage( "GENERAL" ) )
                  || nameField.getText().equals( Localization.getMessage( "TEMPORARY" ) ) ) {
            Handler.showError( "GROUP_NAME_USED" );
            return;
          } else if ( StringUtil.isNullOrEmpty( nameField.getText() ) ) {
            Handler.showError( "GROUP_NAME_IS_EMPTY" );
            return;
          } else {
            /** Checking for group is empty **/
            if ( groupItem.getChildsCount() == 0 ) {
              /** Group is empty **/
              groupItem.setGroupName( name );
            } else {
              /** Group is not empty **/
              /** Mechanism invocation **/
              Mechanism.rosterRenameRequest( groupItem.getGroupName(), groups, groupItem.getChilds() );
            }
          }
        }
        /** Returning to the previous frame **/
        MidletMain.screen.setActiveWindow( s_prevWindow );
      }
    };
    /** Creating pane object **/
    Pane pane = new Pane( null, false );
    /** Creating pane objects **/
    pane.addItem( new Label( Localization.getMessage( "SET_GROUP_NAME" ) ) );
    nameField = new Field( groupItem.getGroupName() );
    nameField.setFocused( true );
    pane.addItem( nameField );
    /** Object group **/
    groupsCheck = new ObjectGroup();
    boolean isFirstGroup = true;
    for ( int c = 0; c < MidletMain.mainFrame.buddyList.items.size(); c++ ) {
      /** Obtain group from roster **/
      GroupItem t_groupItem = ( GroupItem ) MidletMain.mainFrame.buddyList.items.elementAt( c );
      /** Checking for default group id **/
      if ( ( t_groupItem.internalGroupId == GroupItem.GROUP_DEFAULT_ID )
              && !t_groupItem.getGroupName().equals( groupItem.getGroupName() ) ) {
        /** Checking for this is first group will be added **/
        if ( isFirstGroup ) {
          /** Adding comment label **/
          Label label = new Label( Localization.getMessage( "MERGE_GROUPS" ) );
          pane.addItem( label );
          /** Inverting flag **/
          isFirstGroup = false;
        }
        /** Creating check object **/
        Check check = new Check( t_groupItem.getGroupName(), false );
        /** Adding check to object group and pane **/
        groupsCheck.placeObject( check );
        pane.addItem( check );
      }
    }
    /** Note **/
    String noteText = null;
    switch ( groupItem.internalGroupId ) {
      case GroupItem.GROUP_DEFAULT_ID: {
        /** Normal rename **/
        break;
      }
      case GroupItem.GROUP_GENERAL_ID: {
        /** Items will have group attribute **/
        noteText = "GROUP_GENERAL_NOTE";
        break;
      }
      case GroupItem.GROUP_TEMP_ID: {
        /** Items will be added into server's list **/
        noteText = "GROUP_TEMP_NOTE";
        break;
      }
    }
    if ( !StringUtil.isNullOrEmpty( noteText ) ) {
      Label noteLabel = new Label( Localization.getMessage( noteText ) );
      noteLabel.setItalic( true );
      pane.addItem( noteLabel );
    }
    /** Setting up pane **/
    setGObject( pane );
  }
}
