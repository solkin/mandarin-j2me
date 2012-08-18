package com.tomclaw.mandarin.molecus;

import com.tomclaw.mandarin.core.Handler;
import com.tomclaw.mandarin.core.Queue;
import com.tomclaw.mandarin.main.BuddyList;
import com.tomclaw.mandarin.main.MidletMain;
import com.tomclaw.tcuilite.*;
import com.tomclaw.tcuilite.localization.Localization;
import com.tomclaw.utils.LogUtil;
import com.tomclaw.xmlgear.XmlInputStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.TextField;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Parser {

  public static void process( Session session, XmlInputStream xmlReader ) throws Throwable {
    /** Redirecting thread to specified method **/
    if ( xmlReader.tagName.equals( "iq" ) ) {
      /** Tag type is IQ **/
      processIq( session, xmlReader );
    } else if ( xmlReader.tagName.equals( "presence" ) ) {
      /** Tag type is Presence **/
      processPresence( session, xmlReader );
    } else if ( xmlReader.tagName.equals( "message" ) ) {
      /** Tag type is Message **/
      processMessage( session, xmlReader );
    } else if ( xmlReader.tagName.equals( "stream:stream" ) ) {
      /** Tag type is stream:stream **/
      processStream( session, xmlReader );
    } else if ( xmlReader.tagName.equals( "stream:error" ) ) {
      /** Tag type is stream:error **/
      processStream( session, xmlReader );
    }
  }

  private static void processIq( Session session, XmlInputStream xmlReader ) throws Throwable {
    String iqType = xmlReader.getAttrValue( "type", false );
    String iqId = xmlReader.getAttrValue( "id", false );
    String iqFrom = xmlReader.getAttrValue( "from", false );
    Hashtable params = new Hashtable();
    if ( iqType.equals( "result" ) ) {
      /** Result type of an IQ request **/
      if ( xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING ) {
        /** This is enclosed tag and it may have queue action **/
        Queue.runQueueAction( iqId, params );
      } else {
        xmlReader.nextTag();
        if ( xmlReader.tagName.equals( "query" ) ) {
          /** Tag type is Query **/
          String xmlns = xmlReader.getAttrValue( "xmlns", false );
          if ( xmlns.equals( "jabber:iq:register" ) ) {
            /** Checking for tag type **/
            if ( xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
              /** Registation form **/
              Form form = processForm( xmlReader, "query" );
              params.put( "FORM", form );
            }
            Queue.runQueueAction( iqId, params );
          } else if ( xmlns.equals( "jabber:iq:auth" ) && xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
            /** Authorization form **/
            Hashtable fields = new Hashtable();
            while ( xmlReader.nextTag() && !( xmlReader.tagName.equals( "query" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
              fields.put( xmlReader.tagName, "" );
            }
            Queue.runQueueAction( iqId, fields );
          } else if ( xmlns.equals( "jabber:iq:roster" ) ) {
            /** Roster **/
            /** Creating roster vector **/
            Vector roster = new Vector();
            params.put( "ROSTER", roster );
            /** Creating empty group Services **/
            GroupItem servicesGroupItem = new GroupItem( Localization.getMessage( "SERVICES" ) );
            servicesGroupItem.isGroupVisible = false;
            params.put( "SERVICES", servicesGroupItem );
            /** Creating empty group General **/
            GroupItem generalGroupItem = new GroupItem( Localization.getMessage( "GENERAL" ) );
            generalGroupItem.internalGroupId = GroupItem.GROUP_GENERAL_ID;
            params.put( "GENERAL", generalGroupItem );
            if ( xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
              /** Temporary variables **/
              boolean isItemStartedFlag = false;
              boolean isItemInGroupFlag = false;
              boolean isItemServiceFlag = false;
              BuddyItem buddyItem = null;
              while ( xmlReader.nextTag() && !( xmlReader.tagName.equals( "query" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
                if ( xmlReader.tagName.equals( "item" ) ) {
                  /** Item tag */
                  if ( xmlReader.tagType == XmlInputStream.TAG_PLAIN
                          || xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING ) {
                    String jid = xmlReader.getAttrValue( "jid", false );
                    String name = xmlReader.getAttrValue( "name", true );
                    String subscription = xmlReader.getAttrValue( "subscription", true );
                    isItemInGroupFlag = false;
                    isItemServiceFlag = ( jid.indexOf( '@' ) == -1 );
                    if ( isItemServiceFlag ) {
                      buddyItem = new ServiceItem( jid, name, subscription, false );
                    } else {
                      buddyItem = new BuddyItem( jid, name, subscription, false );
                    }
                    buddyItem.updateUi();
                    LogUtil.outMessage( "Buddy created: " + jid );
                  }
                  /** Checking for item added to any group **/
                  if ( xmlReader.tagType == XmlInputStream.TAG_CLOSING
                          || xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING ) {
                    if ( !isItemInGroupFlag && !isItemServiceFlag ) {
                      generalGroupItem.addChild( buddyItem );
                      LogUtil.outMessage( "Child added to general group" );
                    }
                    if ( isItemServiceFlag ) {
                      servicesGroupItem.addChild( buddyItem );
                    }
                  }
                  isItemStartedFlag = ( xmlReader.tagType == XmlInputStream.TAG_PLAIN );
                  continue;
                }
                if ( isItemStartedFlag && !isItemServiceFlag ) {
                  /** This is body of item tag **/
                  if ( xmlReader.tagName.equals( "group" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
                    String groupName = xmlReader.body;
                    if ( groupName != null && groupName.length() > 0 ) {
                      GroupItem groupItem = null;
                      /** Searching for the same-named group **/
                      for ( int c = 0; c < roster.size(); c++ ) {
                        if ( ( ( GroupItem ) roster.elementAt( c ) ).getGroupName().equals( groupName ) ) {
                          /** Found **/
                          groupItem = ( GroupItem ) roster.elementAt( c );
                          break;
                        }
                      }
                      if ( groupItem == null ) {
                        groupItem = new GroupItem( groupName );
                        roster.addElement( groupItem );
                        LogUtil.outMessage( "Group created: " + groupName );
                      }
                      groupItem.addChild( buddyItem );
                      isItemInGroupFlag = true;
                      LogUtil.outMessage( "Child added to group: " + groupName );
                    }
                  }
                }
              }
            }
            Queue.runQueueAction( iqId, params );
          } else if ( xmlns.equals( "jabber:iq:private" ) ) {
            if ( xmlReader.tagType == XmlInputStream.TAG_PLAIN ) {
              /** Creating bookmarks vector **/
              Vector bookmarks = new Vector();
              boolean isBookmarks = false;
              /** Appending bookmarks vector to params **/
              params.put( "BOOKMARKS", bookmarks );
              /** Checking for tag type **/
              /** Cycling all tags until the query closing **/
              while ( xmlReader.nextTag()
                      && !( xmlReader.tagName.equals( "query" )
                      && ( xmlReader.tagType == XmlInputStream.TAG_CLOSING
                      || xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING ) ) ) {
                /** Checking for storage tag type **/
                if ( xmlReader.tagName.equals( "storage" )
                        && xmlReader.getAttrValue( "xmlns", false ).
                        equals( "storage:bookmarks" ) ) {
                  isBookmarks = true;
                  continue;
                }
                /** Checking for bookmarks xmlns **/
                if ( isBookmarks ) {
                  if ( xmlReader.tagName.equals( "conference" ) ) {
                    /** Creating room instance */
                    RoomItem bookmark = new RoomItem(
                            xmlReader.getAttrValue( "jid", false ),
                            xmlReader.getAttrValue( "name", false ),
                            xmlReader.getAttrBoolean( "minimize" ),
                            xmlReader.getAttrBoolean( "autojoin" ) );
                    /** Checking for item type **/
                    if ( xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
                      /** Cycling until conference tag closing **/
                      while ( xmlReader.nextTag()
                              && !( xmlReader.tagName.equals( "conference" )
                              && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
                        /** Checking for tag type is nick or password **/
                        if ( xmlReader.tagName.equals( "nick" ) ) {
                          /** Applying room nick for user */
                          bookmark.setRoomNick( xmlReader.body );
                        } else if ( xmlReader.tagName.equals( "password" ) ) {
                          /** Applying password to enter room **/
                          bookmark.setRoomPassword( xmlReader.body );
                        }
                      }
                    }
                    bookmark.updateUi();
                    bookmarks.addElement( bookmark );
                  }
                }
              }
            }
            Queue.runQueueAction( iqId, params );
          } else if ( xmlns.equals( "http://jabber.org/protocol/disco#items" ) ) {
            /** This is service items discovery response **/
            String queryNode = xmlReader.getAttrValue( "node", true );
            if ( queryNode != null ) {
              params.put( "NODE", queryNode );
            }
            Vector items = new Vector();
            if ( xmlReader.tagType == XmlInputStream.TAG_PLAIN ) {
              while ( xmlReader.nextTag()
                      && !( xmlReader.tagName.equals( "query" )
                      && ( xmlReader.tagType == XmlInputStream.TAG_CLOSING
                      || xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING ) ) ) {
                if ( xmlReader.tagName.equals( "item" )
                        && ( xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING
                        || xmlReader.tagType == XmlInputStream.TAG_PLAIN ) ) {
                  /** Item tag **/
                  Item item = new Item( xmlReader.getAttrValue( "jid", true ),
                          xmlReader.getAttrValue( "node", true ),
                          xmlReader.getAttrValue( "name", true ) );
                  items.addElement( item );
                }
              }
              params.put( "ITEMS", items );
            }
            /** Run queue **/
            Queue.runQueueAction( iqId, params );
          } else if ( xmlns.equals( "http://jabber.org/protocol/disco#info" ) ) {
            /** This is service info discovery response **/
            if ( xmlReader.tagType == XmlInputStream.TAG_PLAIN ) {
              while ( xmlReader.nextTag() && !( xmlReader.tagName.equals( "query" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
                if ( xmlReader.tagName.equals( "feature" )
                        && ( xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING
                        || xmlReader.tagType == XmlInputStream.TAG_PLAIN ) ) {
                  /** Feature tag **/
                  String var = xmlReader.getAttrValue( "var", true );
                  if ( var != null ) {
                    /** For fast search **/
                    params.put( var, "" );
                  }
                }
                if ( xmlReader.tagName.equals( "identity" )
                        && ( xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING
                        || xmlReader.tagType == XmlInputStream.TAG_PLAIN ) ) {
                  /** Identity tag **/
                  String category = xmlReader.getAttrValue( "category", false );
                  String type = xmlReader.getAttrValue( "type", false );
                  String name = xmlReader.getAttrValue( "name", false );
                  params.put( "IDENT_CATG", category );
                  params.put( "IDENT_TYPE", type );
                  params.put( "IDENT_NAME", name );
                }
                if ( xmlReader.tagName.equals( "x" )
                        && xmlReader.tagType == XmlInputStream.TAG_PLAIN ) {
                  /** Reading x:form data **/
                  Form form = processForm( xmlReader, "x" );
                  params.put( "FORM", form );
                }
              }
            }
            /** Run queue **/
            Queue.runQueueAction( iqId, params );
          } else if ( xmlns.equals( "jabber:iq:gateway" ) ) {
            /** Gateway interaction **/
            if ( xmlReader.tagType == XmlInputStream.TAG_PLAIN ) {
              String desc = "";
              String prompt = "";
              String jid = "";
              while ( xmlReader.nextTag()
                      && !( xmlReader.tagName.equals( "query" )
                      && ( xmlReader.tagType == XmlInputStream.TAG_CLOSING
                      || xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING ) ) ) {
                if ( xmlReader.tagName.equals( "desc" )
                        && ( xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
                  /** Descr tag **/
                  desc = xmlReader.body;
                }
                if ( xmlReader.tagName.equals( "prompt" )
                        && ( xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
                  /** Descr tag **/
                  prompt = xmlReader.body;
                }
                if ( xmlReader.tagName.equals( "jid" )
                        && ( xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
                  /** Descr tag **/
                  jid = xmlReader.body;
                }
              }
              /** Saving params **/
              params.put( "DESC", desc );
              params.put( "PROMPT", prompt );
              params.put( "JID", jid );
              /** Run queue **/
              Queue.runQueueAction( iqId, params );
            }
          } else if ( xmlns.equals( "http://jabber.org/protocol/muc#owner" ) ) {
            /** Checking for tag type **/
            if ( xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
              /** Command form **/
              Form form = processForm( xmlReader, "query" );
              params.put( "FORM", form );
            }
            Queue.runQueueAction( iqId, params );
          }
        } else if ( xmlReader.tagName.equals( "command" ) ) {
          /** Tag type is Command **/
          String xmlns = xmlReader.getAttrValue( "xmlns", false );
          if ( xmlns.equals( "http://jabber.org/protocol/commands" ) ) {
            /** Command form **/
            Form form = processForm( xmlReader, "command" );
            params.put( "FORM", form );
            Queue.runQueueAction( iqId, params );
          }
        }
      }
    } else if ( iqType.equals( "get" ) ) {
      if ( xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
        xmlReader.nextTag();
        if ( xmlReader.tagName.equals( "query" ) ) {
          /** Tag type is Query **/
          String xmlns = xmlReader.getAttrValue( "xmlns", false );
          if ( xmlns.equals( "http://jabber.org/protocol/disco#info" ) ) {
            /** This is service info discovery request **/
          } else if ( xmlns.equals( "jabber:iq:last" ) ) {
            /** Last activity request **/
            Handler.sendLastActivity( iqId, iqFrom);
          } else if ( xmlns.equals( "jabber:iq:version" ) ) {
            /** Client version request **/
            Handler.sendVersion( iqId, iqFrom);
          } else if ( xmlns.equals( "urn:xmpp:time" ) ) {
            /** Client time request **/
          }
        } else if ( xmlReader.tagName.equals( "ping" ) ) {
          /** Tag type is Ping **/
          String xmlns = xmlReader.getAttrValue( "xmlns", false );
          if ( xmlns.equals( "urn:xmpp:ping" ) ) {
            /** This is ping request **/
            Handler.sendPong( session, iqFrom, iqId );
          }
        } else if ( xmlReader.tagName.equals( "time" ) ) {
          /** Tag type is Time **/
          String xmlns = xmlReader.getAttrValue( "xmlns", false );
          if ( xmlns.equals( "urn:xmpp:time" ) ) {
            /** This is time info discovery request **/
            Handler.sendEntityTime(iqId, iqFrom);
          }
        }
      }
    } else if ( iqType.equals( "error" ) ) {
      if ( xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
        /** Skipping all tags until error block or iq closing **/
        while ( xmlReader.nextTag()
                && !( xmlReader.tagName.equals( "error" )
                || ( xmlReader.tagName.equals( "iq" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) ) {
        }
        if ( xmlReader.tagName.equals( "error" ) ) {
          processError( xmlReader, params );
          Queue.runQueueAction( iqId, params );
        }
      }
    } else if ( iqType.equals( "set" ) ) {
      if ( xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
        xmlReader.nextTag();
        if ( xmlReader.tagName.equals( "query" ) ) {
          /** Tag type is Query **/
          String xmlns = xmlReader.getAttrValue( "xmlns", false );
          if ( xmlns.equals( "jabber:iq:roster" ) ) {
            /** This is roster push **/
            if ( xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
              /** Temporary variables **/
              boolean isItemStartedFlag = false;
              boolean isItemInGroupFlag = false;
              boolean isItemServiceFlag = false;
              BuddyList buddyList = Handler.getBuddyList();
              BuddyItem buddyItem = null;
              while ( xmlReader.nextTag() && !( xmlReader.tagName.equals( "query" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
                if ( xmlReader.tagName.equals( "item" ) ) {
                  /** Item tag */
                  if ( xmlReader.tagType == XmlInputStream.TAG_PLAIN
                          || xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING ) {
                    String jid = xmlReader.getAttrValue( "jid", false );
                    String name = xmlReader.getAttrValue( "name", true );
                    String subscription = xmlReader.getAttrValue( "subscription", true );
                    /** Obtain buddy item from list **/
                    buddyItem = buddyList.getBuddyItem( jid );
                    /** Checkign name and buddy item for null-type **/
                    if ( name == null && buddyItem != null ) {
                      /** Applying exist nick name to the new item **/
                      name = buddyItem.getNickName();
                    }
                    /** Service flag **/
                    isItemServiceFlag = ( jid.indexOf( '@' ) == -1 );
                    /** Checking for buddy existance **/
                    if ( buddyItem == null ) {
                      /** Checking for unexisted buddy remove **/
                      if ( subscription.equals( "remove" ) ) {
                        LogUtil.outMessage( "Removing unexisting buddy" );
                        continue;
                      }
                      /** Creating buddy instance **/
                      if ( isItemServiceFlag ) {
                        buddyItem = new ServiceItem( jid, name, subscription, false );
                      } else {
                        buddyItem = new BuddyItem( jid, name, subscription, false );
                      }
                      /** Buddy created **/
                      LogUtil.outMessage( "Buddy created: " + jid );
                    } else {
                      /** Checking for remove subscription type **/
                      if ( subscription.equals( "remove" ) ) {
                        /** Checking for buddy item or third-part service type **/
                        if ( jid.indexOf( '@' ) != -1
                                || ( jid.indexOf( '@' ) == -1
                                && !jid.endsWith( ".".concat( AccountRoot.getServicesHost() ) ) ) ) {
                          /** Removing buddy from any groups **/
                          LogUtil.outMessage( "Removing buddy item: ".concat( buddyItem.getJid() ) );
                          buddyList.removeBuddyFromGroups( buddyItem );
                          Handler.checkBuddyUsage( buddyItem );
                          continue;
                        }
                        /** Item is removed from server roster **/
                        buddyItem.setTemp( true );
                      } else {
                        /** This is not removing push **/
                        buddyItem.setTemp( false );
                      }
                      /** Configuring existing buddy **/
                      buddyItem.setNickName( name );
                      buddyItem.setSubscription( subscription );
                      /** Removing buddy from any groups **/
                      buddyList.removeBuddyFromGroups( buddyItem );
                      LogUtil.outMessage( "Buddy configured: " + jid );
                    }
                    /** Updating buddy item UI **/
                    buddyItem.updateUi();
                  }
                  /** Checking for item added to any group **/
                  if ( xmlReader.tagType == XmlInputStream.TAG_CLOSING
                          || xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING ) {
                    if ( !isItemInGroupFlag && !isItemServiceFlag ) {
                      buddyList.generalGroupItem.addChild( buddyItem );
                      LogUtil.outMessage( "Child added to general group" );
                    }
                    if ( isItemServiceFlag ) {
                      buddyList.servicesGroupItem.addChild( buddyItem );
                    }
                  }
                  isItemStartedFlag = ( xmlReader.tagType == XmlInputStream.TAG_PLAIN );
                  continue;
                }
                if ( isItemStartedFlag && !isItemServiceFlag ) {
                  /** This is body of item tag **/
                  if ( xmlReader.tagName.equals( "group" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
                    String groupName = xmlReader.body;
                    if ( groupName != null && groupName.length() > 0 ) {
                      GroupItem groupItem = null;
                      /** Searching for the same-named group **/
                      for ( int c = 0; c < buddyList.items.size(); c++ ) {
                        if ( ( ( GroupItem ) buddyList.items.elementAt( c ) ).getGroupName().equals( groupName ) ) {
                          /** Found **/
                          groupItem = ( GroupItem ) buddyList.items.elementAt( c );
                          break;
                        }
                      }
                      if ( groupItem == null ) {
                        groupItem = new GroupItem( groupName );
                        buddyList.items.addElement( groupItem );
                        LogUtil.outMessage( "Group created: " + groupName );
                      }
                      groupItem.addChild( buddyItem );
                      isItemInGroupFlag = true;
                      LogUtil.outMessage( "Child added to group: " + groupName );
                    }
                  }
                }
              }
              /** Repaint main frame **/
              Handler.repaintMainFrame();
              /** Sending result **/
              Handler.sendPushResult( iqId );
            }
          }
        }
      }
    }
  }

  private static void processPresence( Session session, XmlInputStream xmlReader ) throws Throwable {
    LogUtil.outMessage( "Presence tag" );
    String presenceFrom = xmlReader.getAttrValue( "from", false );
    String presenceId = xmlReader.getAttrValue( "id", false );
    String presenceType = xmlReader.getAttrValue( "type", false );
    LogUtil.outMessage( "from = " + presenceFrom + " id = " + presenceId + " type = " + presenceType );
    boolean plainPresence = false;
    Hashtable params = new Hashtable();
    if ( presenceType.equals( "subscribe" )
            || presenceType.equals( "subscribed" )
            || presenceType.equals( "unsubscribed" ) ) {
      /** Subscription request **/
      LogUtil.outMessage( "Subscription request, approve, reject" );
      Handler.showSubscriptionAction( presenceFrom, presenceType.toUpperCase() );
    } else {
      plainPresence = true;
    }
    String show = StatusUtil.getStatus( StatusUtil.onlineIndex );
    int priority = 0;
    String status = null;
    String caps = null;
    String ver = null;
    String nick = null;
    /** Checking even presence tag selfclosed **/
    if ( xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
      /** Cycling all the tags **/
      while ( xmlReader.nextTag()
              && !( xmlReader.tagName.equals( "presence" )
              && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
        if ( xmlReader.tagName.equals( "show" )
                && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
          show = xmlReader.body;
        }
        if ( xmlReader.tagName.equals( "status" )
                && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
          status = xmlReader.body;
        }
        if ( xmlReader.tagName.equals( "priority" )
                && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
          priority = Integer.parseInt( xmlReader.body );
        }
        if ( xmlReader.tagName.equals( "c" )
                && ( xmlReader.tagType == XmlInputStream.TAG_PLAIN
                || xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING ) ) {
          /** Checking for c xml namespace **/
          if ( xmlReader.getAttrValue( "xmlns", false ).equals( "http://jabber.org/protocol/caps" ) ) {
            /** Caps received **/
            caps = xmlReader.getAttrValue( "node", true );
            ver = xmlReader.getAttrValue( "ver", true );
          }
        }
        if ( xmlReader.tagName.equals( "nick" )
                && xmlReader.tagType == XmlInputStream.TAG_PLAIN
                && xmlReader.getAttrValue( "xmlns", false ).equals( "http://jabber.org/protocol/nick" ) ) {
          /** Nick name will be read **/
          nick = "";
        }
        if ( xmlReader.tagName.equals( "nick" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
          /** Checking for correct nick name xmlns **/
          if ( nick != null && nick.length() == 0 ) {
            /** Obtain nick name **/
            nick = xmlReader.body;
          }
        }
        if ( xmlReader.tagName.equals( "error" ) ) {
          /** Processing error **/
          processError( xmlReader, params );
        }
        if ( xmlReader.tagName.equals( "x" ) ) {
          /** Processing error **/
          processX( xmlReader, params );
        }
      }
    }
    /** Checking status value **/
    boolean isInvalidBuddy = presenceType.equals( "error" );
    show = ( presenceType.equals( "unavailable" ) || isInvalidBuddy )
            ? StatusUtil.getStatus( StatusUtil.offlineIndex ) : show;
    /** Checkign for presence type **/
    if ( plainPresence ) {
      LogUtil.outMessage( "Presence received: from = " + presenceFrom
              + " show = " + show + " priority = " + priority + " status = " + status );
      /** Sending event to Handler **/
      Handler.setPresence( presenceFrom, show, priority, status, caps, ver, isInvalidBuddy, params );
    }
    /** Update nick name if available **/
    if ( nick != null ) {
      Handler.setNickName( presenceFrom, nick );
    }
    /** Check even QueueAction is present **/
    Queue.runQueueAction( presenceId, params );
  }

  private static void processMessage( Session session, XmlInputStream xmlReader ) throws Throwable {
    String messageFrom = xmlReader.getAttrValue( "from", false );
    String messageId = xmlReader.getAttrValue( "id", false );
    String messageType = xmlReader.getAttrValue( "type", false );
    String body = null;
    /** Checking even presence tag selfclosed **/
    if ( xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
      /** Cycling all the tags **/
      while ( xmlReader.nextTag()
              && !( xmlReader.tagName.equals( "message" )
              && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
        if ( xmlReader.tagName.equals( "body" )
                && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
          body = xmlReader.body;
        }
      }
      if ( body != null ) {
        Handler.setMessage( messageFrom, messageType, messageId, body );
      }
    }
  }

  private static void processStream( Session session, XmlInputStream xmlReader ) throws Throwable {
    String iqId = xmlReader.getAttrValue( "id", false );
    /** Checking for stream tag name and type **/
    if ( xmlReader.tagName.equals( "stream:stream" ) ) {
      if ( xmlReader.tagType != XmlInputStream.TAG_CLOSING
              && xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
        /** Stream starting **/
        LogUtil.outMessage( "Stream starts" );
      } else {
        /** Stream closed **/
        LogUtil.outMessage( "Stream closed" );
      }
    } else if ( xmlReader.tagName.equals( "stream:error" ) ) {
      if ( xmlReader.tagName.equals( "stream:error" )
              && ( xmlReader.tagType == XmlInputStream.TAG_PLAIN
              || xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING ) ) {
        /** Stream error **/
        LogUtil.outMessage( "Stream error" );
        Handler.showError( "STREAM_ERROR" );
      }
    }
  }

  /**
   * Deserialize form from xmlReader, create objects and put them into Vector
   * @param xmlReader
   * @param objects
   * @return boolean isFormBased ( x:form )
   * @throws Throwable 
   */
  public static Form processForm( XmlInputStream xmlReader, String parentTag ) throws Throwable {
    boolean isFirstFocusable = true;
    Form form = new Form();
    /** Checking for command sessionId **/
    form.sessionId = xmlReader.getAttrValue( "sessionid", true );
    form.status = xmlReader.getAttrValue( "status", true );
    /** Checking fo tag type **/
    if ( xmlReader.tagType == XmlInputStream.TAG_PLAIN ) {
      /** Cycling until parentTag closing **/
      do {
        if ( form.isFormBased ) {
          /** Root tag types **/
          if ( xmlReader.tagName.equals( "title" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
            /** Appending title label **/
            Label title = new Label( xmlReader.body );
            title.isTitle = true;
            form.objects.addElement( title );
          } else if ( xmlReader.tagName.equals( "instructions" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
            form.objects.addElement( new Label( xmlReader.body ) );
          } else if ( xmlReader.tagName.equals( "field" ) ) {
            /** Resetting all objects **/
            PaneObject paneObject = null;
            ObjectGroup checkGroup = null;
            RadioGroup radioGroup = null;
            String radioValue = "";
            /** Reading main field attributes **/
            String fieldType = xmlReader.getAttrValue( "type", false );
            String fieldVar = xmlReader.getAttrValue( "var", true );
            String fieldLabel = xmlReader.getAttrValue( "label", true );
            /** Checking for field type and 
             * create same instance of pane object **/
            if ( fieldType.equals( "boolean" ) ) {
              paneObject = new Check( fieldLabel, false );
            } else if ( fieldType.equals( "fixed" ) ) {
              paneObject = new Label( "" );
            } else if ( fieldType.equals( "hidden" ) ) {
              paneObject = new Field( "" );
              paneObject.setVisible( false );
            } else if ( fieldType.equals( "jid-multi" )
                    || fieldType.equals( "jid-single" )
                    || fieldType.equals( "text-multi" )
                    || fieldType.equals( "text-single" )
                    || fieldType.equals( "text-private" )
                    || fieldType.equals( "" ) ) {
              /** Text field of any type **/
              paneObject = new Field( "" );
              if ( fieldType.equals( "text-private" ) ) {
                ( ( Field ) paneObject ).constraints = TextField.PASSWORD;
              }
              /** Adding text label **/
              if ( fieldLabel != null ) {
                ( ( Field ) paneObject ).setTitle( fieldLabel );
                form.objects.addElement( new Label( fieldLabel ) );
              }
            } else if ( fieldType.equals( "list-multi" ) ) {
              /** Multi-check list **/
              checkGroup = new ObjectGroup();
              checkGroup.setName( fieldVar );
              /** Adding text label **/
              if ( fieldLabel != null ) {
                form.objects.addElement( new Label( fieldLabel ) );
              }
            } else if ( fieldType.equals( "list-single" ) ) {
              /** Single-radio list **/
              radioGroup = new RadioGroup();
              radioGroup.setName( fieldVar );
              /** Adding text label **/
              if ( fieldLabel != null ) {
                form.objects.addElement( new Label( fieldLabel ) );
              }
            }
            /** Setting up name **/
            if ( paneObject != null && fieldVar != null ) {
              paneObject.setName( fieldVar );
            }
            /** Cycling for field tag till it closed **/
            do {
              /** List's option tag **/
              if ( xmlReader.tagName.equals( "option" ) && xmlReader.tagType == XmlInputStream.TAG_PLAIN ) {
                String optionLabel = xmlReader.getAttrValue( "label", false );
                /** Scanning all option's values **/
                while ( xmlReader.nextTag()
                        && !( xmlReader.tagName.equals( "option" )
                        && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
                  if ( xmlReader.tagName.equals( "value" )
                          && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
                    /** This is option's value **/
                    if ( checkGroup != null ) {
                      Check check = new Check( optionLabel, false );
                      check.setName( xmlReader.body );
                      checkGroup.placeObject( check );
                      form.objects.addElement( check );
                      /** Checking for anything already focused **/
                      if ( isFirstFocusable ) {
                        check.setFocused( isFirstFocusable );
                        isFirstFocusable = false;
                      }
                    } else if ( radioGroup != null ) {
                      Radio radio = new Radio( optionLabel, false );
                      radio.setName( xmlReader.body );
                      radioGroup.addRadio( radio );
                      form.objects.addElement( radio );
                      /** Checking selection **/
                      if ( radioValue != null && xmlReader.body.equals( radioValue ) ) {
                        radioGroup.setCombed( radio );
                      }
                      /** Checking for anything already focused **/
                      if ( isFirstFocusable ) {
                        radio.setFocused( isFirstFocusable );
                        isFirstFocusable = false;
                      }
                    }
                  }
                }
              }
              if ( xmlReader.tagName.equals( "value" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
                if ( paneObject != null ) {
                  /** Obtain pane object value for labels and fields **/
                  String value = paneObject.getStringValue();
                  /** Checking for multi-line **/
                  if ( value != null && value.length() > 0 ) {
                    value += "\n";
                  }
                  /** Checking for pane object type **/
                  if ( paneObject instanceof Check ) {
                    ( ( Check ) paneObject ).state = ( xmlReader.body.equals( "true" ) || xmlReader.body.equals( "1" ) );
                  } else if ( paneObject instanceof Label ) {
                    ( ( Label ) paneObject ).setCaption( value + xmlReader.body );
                  } else if ( paneObject instanceof Field ) {
                    ( ( Field ) paneObject ).setText( value + xmlReader.body );
                  }
                } else if ( checkGroup != null ) {
                  /** This is value for check group **/
                  ( ( Check ) checkGroup.getObjectByName( xmlReader.body ) ).state = true;
                } else if ( radioGroup != null ) {
                  /** This is value for radio group **/
                  radioValue = xmlReader.body;
                  /** Obtain radio by name **/
                  Radio radio = radioGroup.getObjectByName( radioValue );
                  /** Checking for radio already present **/
                  if ( radio != null ) {
                    radioGroup.setCombed( radio );
                  }
                }
              }
            } while ( !( xmlReader.tagName.equals( "field" ) && ( xmlReader.tagType == XmlInputStream.TAG_CLOSING
                    || xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING ) ) && xmlReader.nextTag() );
            /** Field tag is closed **/
            if ( paneObject != null ) {
              /** Checking for something already focused **/
              if ( paneObject.getFocusable() && paneObject.getVisible() && isFirstFocusable ) {
                paneObject.setFocused( isFirstFocusable );
                isFirstFocusable = false;
              }
              form.objects.addElement( paneObject );
            }
          }
        } else {
          if ( xmlReader.tagName.equals( "instructions" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
            Label instructions_label = new Label( xmlReader.body );
            instructions_label.isTitle = true;
            form.objects.addElement( instructions_label );
          } else if ( xmlReader.tagName.equals( "username" )
                  || xmlReader.tagName.equals( "password" )
                  || xmlReader.tagName.equals( "email" )
                  || xmlReader.tagName.equals( "registered" ) ) {
            String field = "";
            if ( xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING ) {
            } else if ( xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
              field = xmlReader.body;
            } else {
              continue;
            }
            Label field_label = new Label( Localization.getMessage( xmlReader.tagName.toUpperCase() ) );
            form.objects.addElement( field_label );
            if ( !xmlReader.tagName.equals( "registered" ) ) {
              Field field_field = new Field( field );
              /** Configuring field **/
              field_field.setName( xmlReader.tagName );
              field_field.setConstraints( ( xmlReader.tagName.equals( "password" ) ? TextField.PASSWORD
                      : ( xmlReader.tagName.equals( "email" ) ? TextField.EMAILADDR : TextField.ANY ) ) );
              field_field.setFocused( isFirstFocusable );
              /** Appending field to objects array **/
              form.objects.addElement( field_field );
              isFirstFocusable = false;
            }
          } else if ( xmlReader.tagName.equals( "x" )
                  && xmlReader.tagType == XmlInputStream.TAG_PLAIN
                  && xmlReader.getAttrValue( "xmlns", false ).equals( "jabber:x:data" ) ) {
            /** X-Form tag **/
            form.isFormBased = true;
            /** Resetting any instructions before **/
            form.objects.removeAllElements();
          } else if ( xmlReader.tagName.equals( "actions" )
                  && xmlReader.tagType == XmlInputStream.TAG_PLAIN ) {
            form.initLeftSoft();
            String execute = xmlReader.getAttrValue( "execute", true );
            /** Parsing until actions tag end **/
            while ( xmlReader.nextTag()
                    && !( xmlReader.tagName.equals( "actions" )
                    && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
              /** Checking for tag type **/
              if ( xmlReader.tagType == XmlInputStream.TAG_SELFCLOSING
                      || xmlReader.tagType == XmlInputStream.TAG_CLOSING ) {
                LogUtil.outMessage( "Command found: " + xmlReader.tagName );
                /** Checking for default action **/
                Command command = new Command( Localization.getMessage( xmlReader.tagName.toUpperCase() ) ) {

                  public void actionPerformed() {
                    /** Showing wait screen **/
                    MidletMain.screen.setWaitScreenState( true );
                    /** Command invokation **/
                    LogUtil.outMessage( "Command invokation: " + item.jid + ", " + name );
                    Mechanism.executeCommand( item, form, name );
                  }
                };
                command.name = xmlReader.tagName;
                command.form = form;
                form.leftSoft.addSubItem( command );
              }
            }
          }
        }
      } while ( xmlReader.nextTag() && !( xmlReader.tagName.equals( parentTag ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) );
    }
    return form;
  }

  private static void processError( XmlInputStream xmlReader, Hashtable params ) throws Throwable {
    /** Error code and type **/
    String error_code = xmlReader.getAttrValue( "code", false );
    String error_type = xmlReader.getAttrValue( "type", false );
    /** Reading error block to detect error cause **/
    String error_cause = "";
    if ( xmlReader.tagType != XmlInputStream.TAG_SELFCLOSING ) {
      while ( xmlReader.nextTag() && !( xmlReader.tagName.equals( "error" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
        if ( xmlReader.tagName.equals( "conflict" ) && error_cause.length() == 0 ) {
          error_cause = "USERNAME_CONFLICT";
        } else if ( xmlReader.tagName.equals( "not-acceptable" ) && error_cause.length() == 0 ) {
          error_cause = "NOT_ACCEPTABLE";
        } else if ( xmlReader.tagName.equals( "forbidden" ) && error_cause.length() == 0 ) {
          error_cause = "FORBIDDEN";
        } else if ( xmlReader.tagName.equals( "resource-constraint" ) && error_cause.length() == 0 ) {
          error_cause = "RESOURCE_CONSTRAINT";
        } else if ( xmlReader.tagName.equals( "bad-request" ) && error_cause.length() == 0 ) {
          error_cause = "BAD_REQUEST";
        } else if ( xmlReader.tagName.equals( "not-authorized" ) && error_cause.length() == 0 ) {
          error_cause = "NOT_AUTHORIZED";
        } else if ( xmlReader.tagName.equals( "service-unavailable" ) && error_cause.length() == 0 ) {
          error_cause = "SERVICE_UNAVAILABLE";
        } else if ( xmlReader.tagName.equals( "not-allowed" ) && error_cause.length() == 0 ) {
          error_cause = "NOT_ALLOWED";
        } else if ( xmlReader.tagName.equals( "internal-server-error" ) && error_cause.length() == 0 ) {
          error_cause = "INTERNAL_SERVER_ERROR";
        } else if ( xmlReader.tagName.equals( "jid-malformed" ) && error_cause.length() == 0 ) {
          error_cause = "JID_MALFORMED";
        } else if ( xmlReader.tagName.equals( "registration-required" ) && error_cause.length() == 0 ) {
          error_cause = "REGISTRATION_REQUIRED";
        } else if ( xmlReader.tagName.equals( "item-not-found" ) && error_cause.length() == 0 ) {
          error_cause = "ITEM_NOT_FOUND";
        } else if ( xmlReader.tagName.equals( "remote-server-not-found" ) && error_cause.length() == 0 ) {
          error_cause = "REMOTE_SERVER_NOT_FOUND";
        }
      }
    }
    params.put( "ERROR_CAUSE", error_cause );
    params.put( "ERROR_СODE", error_code );
    params.put( "ERROR_TYPE", error_type );
  }

  private static void processX( XmlInputStream xmlReader, Hashtable params ) throws Throwable {
    if ( xmlReader.getAttrValue( "xmlns", false ).equals( "http://jabber.org/protocol/muc#user" )
            && xmlReader.tagType == XmlInputStream.TAG_PLAIN ) {
      while ( xmlReader.nextTag() && !( xmlReader.tagName.equals( "x" ) && xmlReader.tagType == XmlInputStream.TAG_CLOSING ) ) {
        if ( xmlReader.tagName.equals( "item" ) ) {
          params.put( "AFFILIATION", xmlReader.getAttrValue( "affiliation", false ) );
          params.put( "JID", xmlReader.getAttrValue( "jid", false ) );
          params.put( "ROLE", xmlReader.getAttrValue( "role", false ) );
        }
        if ( xmlReader.tagName.equals( "status" ) ) {
          String code = xmlReader.getAttrValue( "code", false );
          params.put( "STATUS_".concat( code ), code );
        }
      }
    }
  }
}
