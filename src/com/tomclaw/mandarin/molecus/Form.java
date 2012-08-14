package com.tomclaw.mandarin.molecus;

import com.tomclaw.tcuilite.PaneObject;
import com.tomclaw.tcuilite.PopupItem;
import com.tomclaw.tcuilite.localization.Localization;
import java.util.Vector;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Form {

  /** Parameters **/
  public boolean isFormBased;
  public String status;
  public String sessionId;
  public String node;
  public String formType;
  /** Items **/
  public Vector objects;
  public PopupItem leftSoft;

  public Form() {
    objects = new Vector();
  }

  public void initLeftSoft() {
    leftSoft = new PopupItem( Localization.getMessage( "MENU" ) );
  }

  /**
   * Returns pane object with specified name
   * @param name
   * @return PaneObject
   */
  public PaneObject getObjectByName( String name ) {
    PaneObject paneObject;
    /** Cycling for objects **/
    for ( int c = 0; c < objects.size(); c++ ) {
      /** Objtain pane object **/
      paneObject = ( PaneObject ) objects.elementAt( c );
      /** Checking for pane object name not null and equals **/
      if ( paneObject.getName() != null 
              && paneObject.getName().equals( name ) ) {
        return paneObject;
      }
    }
    return null;
  }
}
