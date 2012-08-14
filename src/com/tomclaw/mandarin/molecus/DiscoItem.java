package com.tomclaw.mandarin.molecus;

import com.tomclaw.tcuilite.ListItem;
import com.tomclaw.utils.StringUtil;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class DiscoItem extends ListItem {

  private String jid;

  /**
   * Disco item constructor
   * @param name
   * @param jid 
   */
  public DiscoItem ( String name, String jid ) {
    super ( StringUtil.isEmptyOrNull ( name ) ? jid : name );
    /** Setting up main parameters **/
    this.jid = jid;
  }

  /**
   * Returns disco item jid
   * @return String
   */
  public String getJid () {
    return jid;
  }
}
