package com.tomclaw.mandarin.core;

import com.tomclaw.utils.LogUtil;
import java.util.Hashtable;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Queue {

  private static Hashtable actions = new Hashtable();

  public static void pushQueueAction( QueueAction action ) {
    actions.put( action.getCookie(), action );
  }

  public static QueueAction popQueueAction( String cookie ) {
    LogUtil.outMessage( "Actions count: " + actions.size() );
    QueueAction queueAction = ( QueueAction ) actions.get( cookie );
    if ( queueAction != null ) {
      LogUtil.outMessage( "QueueAction found!" );
      actions.remove( cookie );
    }
    return queueAction;
  }

  public static void runQueueAction( String cookie, Hashtable params ) {
    QueueAction queueAction = ( QueueAction ) actions.get( cookie );
    if ( queueAction != null ) {
      LogUtil.outMessage( "QueueAction not null" );
      try {
        queueAction.actionPerformed( params );
      } catch ( Throwable ex ) {
        LogUtil.outMessage( ex );
      }
      actions.remove( cookie );
    }
  }
}
