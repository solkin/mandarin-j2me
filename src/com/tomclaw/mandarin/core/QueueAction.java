package com.tomclaw.mandarin.core;

import java.util.Hashtable;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public abstract class QueueAction {

  private String cookie;

  public void setCookie( String cookie ) {
    this.cookie = cookie;
  }

  public String getCookie() {
    return cookie;
  }

  public abstract void actionPerformed( Hashtable params );
}
