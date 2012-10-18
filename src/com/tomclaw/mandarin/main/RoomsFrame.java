package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.core.Handler;
import com.tomclaw.mandarin.molecus.DiscoItem;
import com.tomclaw.mandarin.molecus.Mechanism;
import com.tomclaw.tcuilite.*;
import com.tomclaw.tcuilite.localization.Localization;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class RoomsFrame extends Window {

  private List list;
  public long updateTime;

  /**
   * Rooms frame constructor
   */
  public RoomsFrame() {
    super( MidletMain.screen );
    /** Previous window **/
    s_prevWindow = MidletMain.mainFrame;
    /** Header **/
    header = new Header( Localization.getMessage( "BROWSE_ROOMS" ) );
    /** Soft **/
    soft = new Soft( screen );
    /** Right soft **/
    soft.rightSoft = new PopupItem( Localization.getMessage( "BACK" ) ) {

      public void actionPerformed() {
        MidletMain.screen.setActiveWindow( s_prevWindow );
      }
    };
    /** Left soft **/
    soft.leftSoft = new PopupItem( Localization.getMessage( "ROOM_MORE" ) ) {

      public void actionPerformed() {
        invokeRoomMore();
      }
    };
    /** Creating pane object **/
    list = new List();
    list.listEvent = new ListEvent() {

      public void actionPerformed( ListItem listItem ) {
        invokeRoomMore();
      }
    };
    /** Setting up pane **/
    setGObject( list );
  }

  /**
   * Checking selected index and sending room info request
   */
  private void invokeRoomMore() {
    /** Checking for item range **/
    if ( list.selectedIndex > 0 && list.selectedIndex < list.items.size() ) {
      /** Obtain disco item **/
      DiscoItem discoItem = ( DiscoItem ) list.items.elementAt( list.selectedIndex );
      /** Checking for disco item is not online and user is online **/
      if ( discoItem != null && Handler.sureIsOnline() ) {
        /** Mechanism invocation **/
        Mechanism.sendRoomsInfoDiscoveryRequest( discoItem );
      }
    } else if ( list.selectedIndex == 0 ) {
      /** Create new room request **/
      Mechanism.occupyRoomRequest();
      /** Setting up to update rooms list **/
      resetRoomsCache();
    }
  }

  /**
   * Returns rooms list
   * @return List
   */
  public List getList() {
    return list;
  }

  /**
   * Resetting rooms cache by resetting update time
   */
  public void resetRoomsCache() {
    updateTime = 0;
  }
}
