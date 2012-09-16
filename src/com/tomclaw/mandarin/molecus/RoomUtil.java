package com.tomclaw.mandarin.molecus;

import com.tomclaw.utils.LogUtil;
import com.tomclaw.utils.StringUtil;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class RoomUtil {

  /** Roles **/
  public static final int ROLE_NONE = 0x00;
  public static final int ROLE_VISITOR = 0x01;
  public static final int ROLE_PARTICIPANT = 0x02;
  public static final int ROLE_MODERATOR = 0x03;
  /** Affiliations **/
  public static final int AFFL_OUTCAST = 0x00;
  public static final int AFFL_NONE = 0x01;
  public static final int AFFL_MEMBER = 0x02;
  public static final int AFFL_ADMIN = 0x03;
  public static final int AFFL_OWNER = 0x04;
  /** Roles privileges **/
  public static final int PRESENT_IN_ROOM = 0x00;
  public static final int RECEIVE_MESSAGES = 0x01;
  public static final int RECEIVE_OCCUPANT_PRESENCE = 0x02;
  public static final int BROADCAST_PRESENCE_TO_ALL_OCCUPANTS = 0x03;
  public static final int CHANGE_AVAILABILITY_STATUS = 0x04;
  public static final int CHANGE_ROOM_NICKNAME = 0x05;
  public static final int SEND_PRIVATE_MESSAGES = 0x06;
  public static final int INVITE_OTHER_USERS = 0x07;
  public static final int SEND_MESSAGES_TO_ALL = 0x08;
  public static final int MODIFY_SUBJECT = 0x09;
  public static final int KICK_PARTICIPANTS_AND_VISITORS = 0x0a;
  public static final int GRANT_VOICE = 0x0b;
  public static final int REVOKE_VOICE = 0x0c;
  public static final boolean[][] ROLES = new boolean[][]{
    { false, false, false, false, false, false, false, false, false, false, false, false, false },
    { true, true, true, true, true, true, true, true, false, false, false, false, false },
    { true, true, true, true, true, true, true, true, true, true, false, false, false },
    { true, true, true, true, true, true, true, true, true, true, true, true, true }
  };
  /** Affiliation privileges **/
  public static final int ENTER_OPEN_ROOM = 0x0d;
  public static final int REGISTER_WITH_OPEN_ROOM = 0x0e;
  public static final int RETRIEVE_MEMBER_LIST = 0x0f;
  public static final int ENTER_MEMBERS_ONLY_ROOM = 0x10;
  public static final int BAN_MEMBERS_AND_UNAFFILIATED_USERS = 0x11;
  public static final int EDIT_MEMBER_LIST = 0x12;
  public static final int ASSIGN_AND_REMOVE_MODERATOR_ROLE = 0x13;
  public static final int EDIT_ADMIN_LIST = 0x14;
  public static final int EDIT_OWNER_LIST = 0x15;
  public static final int CHANGE_ROOM_CONFIGURATION = 0x16;
  public static final int DESTROY_ROOM = 0x17;
  public static final boolean[][] AFFL = new boolean[][]{
    { false, false, false, false, false, false, false, false, false, false, false },
    { true, true, false, false, false, false, false, false, false, false, false },
    { true, false, true, true, false, false, false, false, false, false, false },
    { true, false, true, true, true, true, true, false, false, false, false },
    { true, false, true, true, true, true, true, true, true, true, true }
  };

  /**
   * Returns role index for human-type sting affiliation type
   * @param role
   * @return Integer index
   */
  public static int getRoleIndex( String role ) {
    /** Checking for role is null or empty **/
    if ( StringUtil.isEmptyOrNull( role )
            || role.toLowerCase().equals( "none" ) ) {
      /** Returning role none **/
    } else if ( role.toLowerCase().equals( "visitor" ) ) {
      return ROLE_VISITOR;
    } else if ( role.toLowerCase().equals( "participant" ) ) {
      return ROLE_PARTICIPANT;
    } else if ( role.toLowerCase().equals( "moderator" ) ) {
      return ROLE_MODERATOR;
    }
    return ROLE_NONE;
  }

  /**
   * Returns affiliation index for human-type string affiliation type
   * @param affiliation
   * @return Integer index
   */
  public static int getAffiliationIndex( String affiliation ) {
    /** Checking for role is null or empty **/
    if ( StringUtil.isEmptyOrNull( affiliation )
            || affiliation.toLowerCase().equals( "none" ) ) {
      /** Returning affiliation none **/
    } else if ( affiliation.toLowerCase().equals( "outcast" ) ) {
      return AFFL_OUTCAST;
    } else if ( affiliation.toLowerCase().equals( "member" ) ) {
      return AFFL_MEMBER;
    } else if ( affiliation.toLowerCase().equals( "admin" ) ) {
      return AFFL_ADMIN;
    } else if ( affiliation.toLowerCase().equals( "owner" ) ) {
      return AFFL_OWNER;
    }
    return AFFL_NONE;
  }

  /**
   * Checking privilege for specified role, affiliation and privilege index
   * @param roleIndex
   * @param afflIndex
   * @param privilegeIndex
   * @return boolean
   */
  public static boolean checkPrivilege( int roleIndex, int afflIndex, int privilegeIndex ) {
    /** Checking for role / affiliation dependency **/
    if ( privilegeIndex < ROLES[0].length ) {
      LogUtil.outMessage( "Role dependency for: " + privilegeIndex );
      return ROLES[roleIndex][privilegeIndex];
    } else {
      LogUtil.outMessage( "Affiliation dependency for: " + ( privilegeIndex - ROLES[0].length ) );
      return AFFL[afflIndex][privilegeIndex - ROLES[0].length];
    }
  }
}
