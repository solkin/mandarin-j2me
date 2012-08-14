package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.molecus.BuddyItem;
import com.tomclaw.mandarin.molecus.GroupItem;
import com.tomclaw.mandarin.molecus.RoomItem;
import com.tomclaw.mandarin.molecus.ServiceItem;
import com.tomclaw.utils.DataUtil;
import com.tomclaw.utils.LogUtil;
import com.tomclaw.utils.StringUtil;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class RmsRenderer {

  public static GroupItem getRmsGroupHeader ( byte[] data, BuddyList buddyList ) {
    int offset;
    GroupItem groupHeader = new GroupItem ( StringUtil.byteArrayToString ( data, 2, offset = DataUtil.get16 ( data, 0 ), true ) );
    offset += 2;
    int childsCount = DataUtil.get16 ( data, offset );
    offset += 2;
    groupHeader.internalGroupId = DataUtil.get16 ( data, offset );
    offset += 2;
    groupHeader.isGroupVisible = DataUtil.get8 ( data, offset ) == 1;
    offset += 1;
    groupHeader.isItemsVisible = DataUtil.get8 ( data, offset ) == 1;
    offset += 1;
    BuddyItem groupChild;
    int t_Int;
    String t_Id;
    try {
      for ( int c = 0; c < childsCount; c++ ) {
        t_Int = DataUtil.get16 ( data, offset );
        offset += 2;
        t_Id = StringUtil.byteArrayToString ( data, offset, t_Int, true );
        offset += t_Int;
        t_Int = DataUtil.get16 ( data, offset );
        offset += 2;
        /** Checking for item type **/
        switch ( t_Int ) {
          case BuddyItem.TYPE_BUDDY_ITEM: {
            groupChild = new BuddyItem ( t_Id );
            break;
          }
          case BuddyItem.TYPE_SERVICE_ITEM: {
            groupChild = new ServiceItem ( t_Id, t_Id );
            break;
          }
          case BuddyItem.TYPE_ROOM_ITEM: {
            groupChild = new RoomItem ( t_Id, t_Id, false, false );
            break;
          }
          default: {
            groupChild = null;
          }
        }
        t_Int = DataUtil.get16 ( data, offset );
        offset += 2;
        groupChild.setNickName ( StringUtil.byteArrayToString ( data, offset, t_Int, true ) );
        offset += t_Int;
        t_Int = DataUtil.get16 ( data, offset );
        offset += 2;
        groupChild.setSubscription ( StringUtil.byteArrayToString ( data, offset, t_Int, true ) );
        offset += t_Int;
        groupChild.setProtocolOffset ( DataUtil.get16 ( data, offset ) );
        offset += 2;
        groupChild.setBuddyInvalid ( DataUtil.get8int ( data, offset ) == 1 );
        offset++;
        groupChild.updateUi ();
        groupHeader.addChild ( groupChild );
      }
      /** Checking for specified group type **/
      switch ( groupHeader.internalGroupId ) {
        case GroupItem.GROUP_DEFAULT_ID: {
          break;
        }
        case GroupItem.GROUP_GENERAL_ID: {
          LogUtil.outMessage ( "General group found" );
          buddyList.generalGroupItem = groupHeader;
          break;
        }
        case GroupItem.GROUP_SERVICES_ID: {
          LogUtil.outMessage ( "Services group found" );
          buddyList.servicesGroupItem = groupHeader;
          break;
        }
        case GroupItem.GROUP_TEMP_ID: {
          LogUtil.outMessage ( "Temp group found" );
          buddyList.tempGroupItem = groupHeader;
          break;
        }
        case GroupItem.GROUP_ROOMS_ID: {
          LogUtil.outMessage ( "Rooms group found" );
          buddyList.roomsGroupItem = groupHeader;
          break;
        }
      }
    } catch ( Throwable ex ) {
      LogUtil.outMessage ( "Error while loading RMS list: " + ex.getMessage () );
    }
    return groupHeader;
  }

  public static byte[] getRmsData ( GroupItem groupHeader ) {
    byte[] data;
    byte[] titleData = StringUtil.stringToByteArray ( groupHeader.title, true );
    data = new byte[ 8 + titleData.length ];
    DataUtil.put16 ( data, 0, titleData.length );
    DataUtil.putArray ( data, 2, titleData );
    DataUtil.put16 ( data, 2 + titleData.length, groupHeader.getChildsCount () );
    DataUtil.put16 ( data, 4 + titleData.length, groupHeader.internalGroupId );
    DataUtil.put8 ( data, 6 + titleData.length, groupHeader.isGroupVisible ? 1 : 0 );
    DataUtil.put8 ( data, 7 + titleData.length, groupHeader.isItemsVisible ? 1 : 0 );
    byte[] itemData;
    byte[] t_Byte;
    int offset;
    BuddyItem groupChild;
    try {
      for ( int c = 0; c < groupHeader.getChildsCount (); c++ ) {
        // offset = 0;
        groupChild = ( BuddyItem ) groupHeader.elementAt ( c );
        titleData = StringUtil.stringToByteArray ( groupChild.getJid (), true );
        itemData = new byte[ 11 + titleData.length + StringUtil.stringToByteArray ( groupChild.getNickName (), true ).length + StringUtil.stringToByteArray ( groupChild.getSubscription (), true ).length ];
        /** Title **/
        DataUtil.put16 ( itemData, 0, titleData.length );
        DataUtil.putArray ( itemData, 2, titleData );
        offset = 2 + titleData.length;
        /** Item type **/
        DataUtil.put16 ( itemData, offset, groupChild.getInternalType () );
        offset += 2;
        /** userNick **/
        titleData = StringUtil.stringToByteArray ( groupChild.getNickName (), true );
        DataUtil.put16 ( itemData, offset, titleData.length );
        offset += 2;
        DataUtil.putArray ( itemData, offset, titleData );
        offset += titleData.length;
        /** userPhone **/
        titleData = StringUtil.stringToByteArray ( groupChild.getSubscription (), true );
        DataUtil.put16 ( itemData, offset, titleData.length );
        offset += 2;
        DataUtil.putArray ( itemData, offset, titleData );
        offset += titleData.length;
        /** itemType **/
        DataUtil.put16 ( itemData, offset, groupChild.getProtocolOffset () );
        offset += 2;
        /** isPhone **/
        DataUtil.put8 ( itemData, offset, groupChild.isBuddyInvalid () ? 1 : 0 );
        offset++;
        /** Glueing array **/
        t_Byte = new byte[ data.length + itemData.length ];
        System.arraycopy ( data, 0, t_Byte, 0, data.length );
        System.arraycopy ( itemData, 0, t_Byte, data.length, itemData.length );
        data = t_Byte;
      }
    } catch ( Throwable ex ) {
      ex.printStackTrace ();
      LogUtil.outMessage ( "Error while serializing buddy: " + ex.getMessage () );
    }
    return data;
  }
}
