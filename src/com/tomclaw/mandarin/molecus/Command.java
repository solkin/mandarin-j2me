package com.tomclaw.mandarin.molecus;

import com.tomclaw.mandarin.core.Handler;
import com.tomclaw.tcuilite.PopupItem;
import com.tomclaw.utils.LogUtil;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Command extends PopupItem {

  public Item item;
  public Form form;

  public Command( String title ) {
    super( title );
  }

  /**
   * Action performing
   */
  public void actionPerformed() {
    /** Checking for user is online **/
    LogUtil.outMessage( "Checking for user is online" );
    if ( Handler.sureIsOnline() ) {
      /** Performing an action **/
      LogUtil.outMessage( "Performing an action" );
      actionAttempt();
    }
  }

  /**
   * Network action attempt
   */
  public void actionAttempt() {
  }
}
