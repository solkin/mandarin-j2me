package com.tomclaw.mandarin.molecus;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Resource {
  /** Resource params **/
  public int statusIndex = StatusUtil.offlineIndex;
  public String statusText = null;
  public String resource = null;
  public int unreadCount = 0;
  public String caps = null;
  public String ver = null;
  /** Params for muc resource **/
  public int role = RoomUtil.ROLE_NONE;
  public int affiliation = RoomUtil.AFFL_NONE;
  public String jid = null;

  public Resource( String resource ) {
    this.resource = resource;
  }

  public void setStatusIndex( int statusIndex ) {
    this.statusIndex = statusIndex;
  }

  public void setStatusText( String statusText ) {
    this.statusText = statusText;
  }

  public void setResource( String resource ) {
    this.resource = resource;
  }

  public void setUnreadCount( int unreadCount ) {
    this.unreadCount = unreadCount;
  }

  public void setCaps( String caps ) {
    this.caps = caps;
  }

  public void setVer( String ver ) {
    this.ver = ver;
  }

  public void setAffiliation( int affiliation ) {
    this.affiliation = affiliation;
  }

  public void setRole( int role ) {
    this.role = role;
  }

  public void setJid( String jid ) {
    this.jid = jid;
  }
}
