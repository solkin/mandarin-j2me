package com.tomclaw.mandarin.molecus;

import com.tomclaw.tcuilite.GroupHeader;
import java.util.Vector;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class GroupItem extends GroupHeader {

  public static final int GROUP_DEFAULT_ID = 0x00;
  public static final int GROUP_GENERAL_ID = 0x01;
  public static final int GROUP_SERVICES_ID = 0x02;
  public static final int GROUP_ROOMS_ID = 0x03;
  public static final int GROUP_TEMP_ID = 0x04;
  public int internalGroupId = GROUP_DEFAULT_ID;

  public GroupItem ( String groupName ) {
    super ( groupName );
  }

  public void setGroupName ( String groupName ) {
    this.title = groupName;
  }

  public String getGroupName () {
    return title;
  }
}
