package com.tomclaw.mandarin.molecus;

import com.tomclaw.mandarin.main.BuddyList;
import com.tomclaw.tcuilite.ListItem;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Visitor extends ListItem {

  public String jid;
  public String affiliation;
  public String reason;

  public Visitor( String jid, String affiliation ) {
    /** Applying variables **/
    this.jid = jid;
    this.affiliation = affiliation;
    /** Applying title **/
    this.title = BuddyList.getJidUsername( jid );
  }
}
