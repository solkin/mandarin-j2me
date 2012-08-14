package com.tomclaw.mandarin.molecus;

import com.tomclaw.tcuilite.PopupItem;

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
}
