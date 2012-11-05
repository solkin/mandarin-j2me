package com.tomclaw.mandarin.molecus;

import com.tomclaw.mandarin.main.MidletMain;
import com.tomclaw.tcuilite.*;
import com.tomclaw.tcuilite.localization.Localization;
import com.tomclaw.utils.StringUtil;
import com.tomclaw.utils.TimeUtil;
import com.tomclaw.xmlgear.XmlOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class TemplateCollection {

  /** Constants **/
  public static String ROOM_NAME = "muc#roomconfig_roomname";
  public static String ROOM_DESC = "muc#roomconfig_roomdesc";
  public static String ROOM_PERSISTENT = "muc#roomconfig_persistentroom";
  public static String ROOM_PASS_PROT = "muc#roomconfig_passwordprotectedroom";
  public static String ROOM_PASSWORD = "muc#roomconfig_roomsecret";
  /** Tags **/
  public static final String TAG_IQ = "iq";
  public static final String TAG_QUERY = "query";
  public static final String TAG_PRESENCE = "presence";
  public static final String TAG_COMMAND = "command";
  public static final String TAG_X = "x";
  public static final String TAG_ITEM = "item";
  public static final String TAG_GROUP = "group";
  public static final String TAG_PROMPT = "prompt";
  public static final String TAG_REMOVE = "remove";
  public static final String TAG_CONFERENCE = "conference";
  public static final String TAG_PASSWORD = "password";
  public static final String TAG_REASON = "reason";
  public static final String TAG_SHOW = "show";
  /** Attributes **/
  public static final String ATT_TYPE = "type";
  public static final String ATT_ID = "id";
  public static final String ATT_TO = "to";
  public static final String ATT_FROM = "from";
  public static final String ATT_XMLNS = "xmlns";
  public static final String ATT_NODE = "node";
  public static final String ATT_ACTION = "action";
  public static final String ATT_SESSIONID = "sessionid";
  public static final String ATT_JID = "jid";
  public static final String ATT_NAME = "name";
  public static final String ATT_SUBSCRIPTION = "subscription";
  public static final String ATT_AFFILIATION = "affiliation";
  /** Values **/
  public static final String VAL_GET = "get";
  public static final String VAL_SET = "set";
  public static final String VAL_RESULT = "result";
  public static final String VAL_OWNER = "owner";
  public static final String VAL_ADMIN = "admin";
  public static final String VAL_MEMBER = "member";
  public static final String VAL_OUTCAST = "outcast";
  public static final String VAL_NONE = "none";
  public static final String[] FEATURES = new String[]{
    "http://jabber.org/protocol/disco#info",
    "http://jabber.org/protocol/caps",
    "http://jabber.org/protocol/muc",
    "jabber:iq:version",
    "jabber:x:data",
    "jabber:iq:last",
    "jabber:iq:time",
    "urn:xmpp:time",
    "urn:xmpp:ping",
    "urn:xmpp:delay",
    "jabber:iq:private"
  };

  /**
   * Serialize form and write it into xmlWriter
   * @param xmlWriter
   * @param objects
   * @param isFormBased
   * @throws IOException 
   */
  private static void sendFormObjects( XmlOutputStream xmlWriter, Form form ) throws IOException {
    /** Redirecting request **/
    sendFormObjects( xmlWriter, form, "submit" );
  }

  /**
   * Serialize form and write it into xmlWriter
   * @param xmlWriter
   * @param objects
   * @param isFormBased
   * @param xFormType
   * @throws IOException 
   */
  private static void sendFormObjects( XmlOutputStream xmlWriter, Form form, String xFormType ) throws IOException {
    /** Checking for form type **/
    if ( form.isFormBased ) {
      /** If the form was received by x-form data **/
      xmlWriter.startTag( TAG_X );
      xmlWriter.attribute( ATT_XMLNS, "jabber:x:data" );
      xmlWriter.attribute( ATT_TYPE, xFormType );
      /** Cycling objects **/
      for ( int c = 0; c < form.objects.size(); c++ ) {
        PaneObject paneObject = ( PaneObject ) form.objects.elementAt( c );
        /** Detecting pane object type **/
        if ( paneObject.getName() != null ) {
          /** Object is field of any type **/
          if ( paneObject instanceof Field ) {
            xmlWriter.startTag( "field" );
            xmlWriter.attribute( "var", paneObject.getName() );
            xmlWriter.startTag( "value" );
            xmlWriter.text( ( ( Field ) paneObject ).getText() );
            xmlWriter.endTag();
            xmlWriter.endTag();
          }
          /** Field is check or multi-list **/
          if ( paneObject instanceof Check ) {
            /** Checking for check type **/
            if ( ( ( Check ) paneObject ).getObjectGroup() == null ) {
              /** Object is plain check **/
              xmlWriter.startTag( "field" );
              xmlWriter.attribute( "var", paneObject.getName() );
              xmlWriter.startTag( "value" );
              xmlWriter.text( ( ( Check ) paneObject ).state ? "1" : "0" );
              xmlWriter.endTag();
              xmlWriter.endTag();
            } else {
              /** Obtain check group object **/
              ObjectGroup checkGroup =
                      ( ( Check ) paneObject ).getObjectGroup();
              if ( checkGroup.getName() != null ) {
                xmlWriter.startTag( "field" );
                xmlWriter.attribute( "var", checkGroup.getName() );
                for ( int i = 0; i < checkGroup.items.size(); i++ ) {
                  xmlWriter.startTag( "value" );
                  xmlWriter.text(
                          ( ( Check ) checkGroup.items.elementAt( i ) ).getName() );
                  xmlWriter.endTag();
                }
                xmlWriter.endTag();
                /** Abnull check group name **/
                checkGroup.setName( null );
              }
            }
          }
          /** Object is single-list **/
          if ( paneObject instanceof Radio ) {
            /** Checking for radio is selected **/
            if ( ( ( Radio ) paneObject ).radioState ) {
              /** Creating field tag **/
              xmlWriter.startTag( "field" );
              xmlWriter.attribute( "var",
                      ( ( Radio ) paneObject ).radioGroup.getName() );
              xmlWriter.startTag( "value" );
              xmlWriter.text( ( ( Radio ) paneObject ).getName() );
              xmlWriter.endTag();
              xmlWriter.endTag();
            }
          }
        }
      }
      xmlWriter.endTag();
    } else {
      /** If the form was received by instructions **/
      for ( int c = 0; c < form.objects.size(); c++ ) {
        /** If item have name, we must send it value **/
        PaneObject field = ( PaneObject ) form.objects.elementAt( c );
        if ( field.getName() != null ) {
          xmlWriter.startTag( field.getName() );
          xmlWriter.text( field.getStringValue() );
          xmlWriter.endTag();
        }
      }
    }
  }

  public static void sendStartXmlStream(
          XmlOutputStream xmlWriter, String hostAddr, String from )
          throws IOException {
    xmlWriter.startTag( "stream:stream" );
    xmlWriter.attribute( ATT_XMLNS, "jabber:client" );
    xmlWriter.attribute( "xmlns:stream", "http://etherx.jabber.org/streams" );
    xmlWriter.attribute( ATT_TO, hostAddr );
    if ( from != null ) {
      xmlWriter.attribute( ATT_FROM, from );
    }
//     xmlWriter.attribute( "version", "1.0" ); // Causes stream error if from attribute is present
    xmlWriter.attribute( "xml:lang", Localization.getMessage( "LOCALE" ) );
    xmlWriter.text( "" );
    xmlWriter.flush();
  }

  public static String sendRegisterRequest(
          XmlOutputStream xmlWriter, String regToAddr, boolean isRemove ) throws IOException {
    final String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, isRemove ? VAL_SET : VAL_GET );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.attribute( ATT_TO, regToAddr );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "jabber:iq:register" );
    /** Checking for remove registration request **/
    if ( isRemove ) {
      xmlWriter.startTag( TAG_REMOVE );
      xmlWriter.endTag();
    }
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  /**
   * Sends registration information or remove registration request
   * in case of form is null
   * @param xmlWriter
   * @param form
   * @param regToAddr
   * @return
   * @throws IOException 
   */
  public static String sendRegistrationForm(
          XmlOutputStream xmlWriter, Form form, String regToAddr )
          throws IOException {
    final String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TO, regToAddr );
    xmlWriter.attribute( ATT_TYPE, VAL_SET );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "jabber:iq:register" );
    /** Checking for form null-type **/
    if ( form != null ) {
      /** Sending deserialized form data **/
      sendFormObjects( xmlWriter, form );
    }
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String sendLoginFormRequest(
          XmlOutputStream xmlWriter, String authToAddr ) throws IOException {
    final String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, VAL_GET );
    xmlWriter.attribute( ATT_TO, authToAddr );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "jabber:iq:auth" );
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String sendLoginForm( XmlOutputStream xmlWriter, Hashtable fields )
          throws IOException {
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, VAL_SET );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "jabber:iq:auth" );
    /** This fields may be received from servar as form **/
    Enumeration fieldsEnum = fields.keys();
    while ( fieldsEnum.hasMoreElements() ) {
      String tag = ( String ) fieldsEnum.nextElement();
      String value = ( String ) fields.get( tag );
      if ( !StringUtil.isEmptyOrNull( value ) ) {
        xmlWriter.startTag( tag );
        xmlWriter.text( value );
        xmlWriter.endTag();
      }
    }
    xmlWriter.endTag(); // query
    xmlWriter.endTag(); // iq
    xmlWriter.flush();
    return cookie;
  }

  public static String sendRosterRequest( XmlOutputStream xmlWriter, String from )
          throws IOException {
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_FROM, from );
    xmlWriter.attribute( ATT_TYPE, VAL_GET );
    xmlWriter.attribute( ATT_ID, cookie );

    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "jabber:iq:roster" );
    xmlWriter.endTag();

    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String sendPresence(
          XmlOutputStream xmlWriter, String from, String to, String type,
          String statusId, String status, int priority, boolean isSendCaps ) throws IOException {
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_PRESENCE );
    xmlWriter.attribute( ATT_ID, cookie );
    if ( from != null ) {
      xmlWriter.attribute( ATT_FROM, from );
    }
    if ( to != null ) {
      xmlWriter.attribute( ATT_TO, to );
    }
    if ( type != null ) {
      xmlWriter.attribute( ATT_TYPE, type );
    }
    if ( statusId != null ) {
      xmlWriter.startTag( TAG_SHOW );
      xmlWriter.text( statusId );
      xmlWriter.endTag();
    }
    if ( status != null ) {
      xmlWriter.startTag( "status" );
      xmlWriter.text( status );
      xmlWriter.endTag();
    }
    if ( priority != 0 ) {
      xmlWriter.startTag( "priority" );
      xmlWriter.text( Integer.toString( priority ) );
      xmlWriter.endTag();
    }
    if ( isSendCaps ) {
      String version = MidletMain.version + " "
              + MidletMain.type + "-build " + MidletMain.build;
      xmlWriter.startTag( "c" );
      xmlWriter.attribute( ATT_XMLNS, "http://jabber.org/protocol/caps" );
      xmlWriter.attribute( "node", "http://tomclaw.com/mandarin_im/caps" );
      xmlWriter.attribute( "ver", version );
      xmlWriter.endTag();
    }
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String sendMessage( XmlOutputStream xmlWriter, String to,
          String type, String body, String subject ) throws IOException {
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( "message" );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.attribute( ATT_TYPE, type );
    xmlWriter.attribute( ATT_TO, to );
    /** Checking for subject is not null **/
    if ( subject != null ) {
      xmlWriter.startTag( "subject" );
      xmlWriter.text( subject );
      xmlWriter.endTag();
    }
    /** Checking for body is not null **/
    if ( body != null ) {
      xmlWriter.startTag( "body" );
      xmlWriter.text( body );
      xmlWriter.endTag();
    }
    /** Ending tag **/
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String sendServiceItemsDiscoveryRequest( XmlOutputStream xmlWriter,
          String servicesHost, String fullJid, String node ) throws IOException {
    return sendDiscoveryRequest( xmlWriter, servicesHost, fullJid, node,
            "http://jabber.org/protocol/disco#items" );
  }

  public static String sendServiceInfoDiscoveryRequest( XmlOutputStream xmlWriter,
          String servicesHost, String fullJid, String node ) throws IOException {
    return sendDiscoveryRequest( xmlWriter, servicesHost, fullJid, node,
            "http://jabber.org/protocol/disco#info" );
  }

  public static String sendRoomConfigurationRequest( XmlOutputStream xmlWriter,
          String roomJid ) throws IOException {
    return sendDiscoveryRequest( xmlWriter, roomJid, null, null,
            "http://jabber.org/protocol/muc#owner" );
  }

  private static String sendDiscoveryRequest( XmlOutputStream xmlWriter,
          String servicesHost, String fullJid, String node, String xmlns )
          throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, VAL_GET );
    // xmlWriter.attribute( ATT_XMLNS, "jabber:client" );
    if ( fullJid != null ) {
      xmlWriter.attribute( ATT_FROM, fullJid );
    }
    xmlWriter.attribute( ATT_TO, servicesHost );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, xmlns );
    if ( node != null ) {
      xmlWriter.attribute( "node", node );
    }
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  /**
   * Sends command specified action
   * @param xmlWriter
   * @param jid
   * @param node
   * @param action
   * @param sessionId
   * @return cookie
   * @throws IOException 
   */
  public static String sendCommandAction( XmlOutputStream xmlWriter,
          String jid, String node, String action )
          throws IOException {
    return sendCommandAction( xmlWriter, jid, node, action, null );
  }

  /**
   * Sends command specified action
   * @param xmlWriter
   * @param jid
   * @param node
   * @param action
   * @param sessionId
   * @param objects
   * @param isFormBased
   * @return cookie
   * @throws IOException 
   */
  public static String sendCommandAction( XmlOutputStream xmlWriter,
          String jid, String node, String action, Form form )
          throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, VAL_SET );
    xmlWriter.attribute( ATT_TO, jid );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_COMMAND );
    xmlWriter.attribute( ATT_XMLNS, "http://jabber.org/protocol/commands" );
    xmlWriter.attribute( ATT_NODE, node );
    /** Checking for action is null **/
    if ( action != null ) {
      xmlWriter.attribute( ATT_ACTION, action );
    }
    if ( form != null ) {
      /** Checking for sessionId **/
      if ( form.sessionId != null ) {
        xmlWriter.attribute( ATT_SESSIONID, form.sessionId );
      }
      /** Checking for form existance **/
      if ( form.objects != null && !form.objects.isEmpty() ) {
        TemplateCollection.sendFormObjects( xmlWriter, form );
      }
    }
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  /**
   * Adding buddy to roster
   * All null-group strings will be skipped
   * @param xmlWriter
   * @param from
   * @param jid
   * @param name
   * @param subscription
   * @param groups
   * @param isFlush
   * @return
   * @throws IOException 
   */
  public static String sendRosterSet( XmlOutputStream xmlWriter, String from,
          String jid, String name, String subscription, String[] groups,
          boolean isFlush ) throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_FROM, from );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.attribute( ATT_TYPE, VAL_SET );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "jabber:iq:roster" );
    xmlWriter.startTag( TAG_ITEM );
    xmlWriter.attribute( ATT_JID, jid );
    /** Checking and adding name attribute **/
    if ( name != null ) {
      xmlWriter.attribute( ATT_NAME, name );
    }
    /** Checking and adding subscription attribute **/
    if ( subscription != null ) {
      xmlWriter.attribute( ATT_SUBSCRIPTION, subscription );
    }
    /** Checking for groups **/
    if ( groups != null ) {
      /** Appending groups **/
      for ( int c = 0; c < groups.length; c++ ) {
        /** Checking for null-item group **/
        if ( groups[c] != null ) {
          xmlWriter.startTag( TAG_GROUP );
          xmlWriter.text( groups[c] );
          xmlWriter.endTag();
        }
      }
    }
    /** Closing tags **/
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.endTag();
    /** Checking for flush value **/
    if ( isFlush ) {
      xmlWriter.flush();
    }
    return cookie;
  }

  public static void sendMassRosterRemove( XmlOutputStream xmlWriter, String from,
          String[] jid ) throws IOException {
    /** Cycling all items **/
    for ( int c = 0; c < jid.length; c++ ) {
      /** Generating request cookie **/
      String cookie = AccountRoot.generateCookie();
      xmlWriter.startTag( TAG_IQ );
      xmlWriter.attribute( ATT_FROM, from );
      xmlWriter.attribute( ATT_ID, cookie );
      xmlWriter.attribute( ATT_TYPE, VAL_SET );
      xmlWriter.startTag( TAG_QUERY );
      xmlWriter.attribute( ATT_XMLNS, "jabber:iq:roster" );
      xmlWriter.startTag( TAG_ITEM );
      xmlWriter.attribute( ATT_JID, jid[c] );
      xmlWriter.attribute( ATT_SUBSCRIPTION, "remove" );
      /** Closing tags **/
      xmlWriter.endTag();
      xmlWriter.endTag();
      xmlWriter.endTag();
    }
    /** Flushing stream **/
    xmlWriter.flush();
  }

  public static void sendIqResult( XmlOutputStream xmlWriter, String id, String toHost ) throws IOException {
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_FROM, AccountRoot.getFullJid() );
    xmlWriter.attribute( ATT_ID, id );
    /** Checking for toHost is null **/
    if ( toHost != null ) {
      xmlWriter.attribute( ATT_TO, toHost );
    }
    xmlWriter.attribute( ATT_TYPE, VAL_RESULT );
    xmlWriter.endTag();
    xmlWriter.flush();
  }

  /**
   * Client requests gateway prompt to add buddy
   * @param xmlWriter
   * @param serviceHost 
   */
  public static String sendPromptRequest( XmlOutputStream xmlWriter, String serviceHost ) throws IOException {
    return sendPromptResponse( xmlWriter, serviceHost, null );
  }

  /**
   * Client requests gateway prompt to add buddy
   * @param xmlWriter
   * @param serviceHost 
   */
  public static String sendPromptResponse( XmlOutputStream xmlWriter, String serviceHost, String prompt ) throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, ( ( prompt == null ) ? VAL_GET : VAL_SET ) );
    xmlWriter.attribute( ATT_TO, serviceHost );
    xmlWriter.attribute( ATT_FROM, AccountRoot.getFullJid() );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "jabber:iq:gateway" );
    if ( prompt != null ) {
      xmlWriter.startTag( TAG_PROMPT );
      xmlWriter.text( prompt );
      xmlWriter.endTag();
    }
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String sendSubscriptionRequest(
          XmlOutputStream xmlWriter, String to ) throws IOException {
    return sendPresence( xmlWriter, null, to, "subscribe", null, null, 0, false );
  }

  public static String sendSubscriptionApprove(
          XmlOutputStream xmlWriter, String to ) throws IOException {
    return sendPresence( xmlWriter, null, to, "subscribed", null, null, 0, false );
  }

  public static String sendSubscriptionReject(
          XmlOutputStream xmlWriter, String to ) throws IOException {
    return sendPresence( xmlWriter, null, to, "unsubscribed", null, null, 0, false );
  }

  /**
   * Sending ping signal to server
   * @param xmlWriter
   * @param toHost
   * @return
   * @throws IOException 
   */
  public static String sendPing( XmlOutputStream xmlWriter, String toHost ) throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_FROM, AccountRoot.getFullJid() );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.attribute( ATT_TO, toHost );
    xmlWriter.attribute( ATT_TYPE, VAL_GET );
    xmlWriter.startTag( "ping" );
    xmlWriter.attribute( ATT_XMLNS, "urn:xmpp:ping" );
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  /**
   * Sending bookmarks request
   * @param xmlWriter
   * @return
   * @throws IOException 
   */
  public static String sendBookmarksRequest( XmlOutputStream xmlWriter ) throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_XMLNS, "jabber:client" );
    xmlWriter.attribute( ATT_TYPE, "get" );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "jabber:iq:private" );
    xmlWriter.startTag( "storage" );
    xmlWriter.attribute( ATT_XMLNS, "storage:bookmarks" );
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  /**
   * Sending bookmarks request
   * @param xmlWriter
   * @param items 
   * @return
   * @throws IOException 
   */
  public static String sendBookmarksDump( XmlOutputStream xmlWriter, Vector items, int operation, RoomItem item, RoomItem editItem ) throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_XMLNS, "jabber:client" );
    xmlWriter.attribute( ATT_TYPE, "set" );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "jabber:iq:private" );
    xmlWriter.startTag( "storage" );
    xmlWriter.attribute( ATT_XMLNS, "storage:bookmarks" );
    /** Checking for items existance **/
    if ( items != null ) {
      /** Creating bookmarks dump **/
      RoomItem roomItem;
      /** On adding room cycle size must be greater **/
      int size = items.size() + ( operation == Mechanism.OPERATION_ADD ? 1 : 0 );
      for ( int c = 0; c < size; c++ ) {
        /** Checking for overfull on ading room **/
        if ( c == items.size() ) {
          roomItem = item;
        } else {
          roomItem = ( RoomItem ) items.elementAt( c );
          /** Checking item for equals **/
          if ( roomItem.getJid().equals( item.getJid() ) ) {
            /** Switching for operation **/
            switch ( operation ) {
              case Mechanism.OPERATION_REMOVE: {
                /** Checking for the same instance to remove **/
                if ( roomItem.equals( item ) ) {
                  /** Do not include item in outgoing dump **/
                  continue;
                }
              }
              case Mechanism.OPERATION_EDIT: {
                /** Checking for the same instance to edit **/
                if ( roomItem.equals( editItem ) ) {
                  /** Change item in outgoing dump **/
                  roomItem = item;
                }
                break;
              }
            }
          }
        }
        xmlWriter.startTag( TAG_CONFERENCE );
        xmlWriter.attribute( ATT_JID, roomItem.getJid() );
        xmlWriter.attribute( ATT_NAME, roomItem.getNickName() );
        xmlWriter.attribute( "minimize", roomItem.getMinimize() ? "1" : "0" );
        xmlWriter.attribute( "autojoin", roomItem.getAutoJoin() ? "1" : "0" );
        /** Checking nick name for empty **/
        if ( !StringUtil.isEmptyOrNull( roomItem.getRoomNick() ) ) {
          xmlWriter.startTag( "nick" );
          xmlWriter.text( roomItem.getRoomNick() );
          xmlWriter.endTag();
        }
        if ( !StringUtil.isEmptyOrNull( roomItem.getRoomPassword() ) ) {
          xmlWriter.startTag( "password" );
          xmlWriter.text( roomItem.getRoomPassword() );
          xmlWriter.endTag();
        }
        xmlWriter.endTag();
      }
    }
    /** Closing tags **/
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String enterRoom(
          XmlOutputStream xmlWriter, String from, RoomItem roomItem,
          String show ) throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_PRESENCE );
    xmlWriter.attribute( ATT_FROM, from );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.attribute( ATT_TO, roomItem.getJid().concat( "/" ).concat( roomItem.getRoomNick() ) );
    /** Checking for show status is not null **/
    if ( !StringUtil.isEmptyOrNull( show ) ) {
      xmlWriter.startTag( TAG_SHOW );
      xmlWriter.text( show );
      xmlWriter.endTag();
    }
    /** MUC xmlns **/
    xmlWriter.startTag( TAG_X );
    xmlWriter.attribute( ATT_XMLNS, "http://jabber.org/protocol/muc" );
    /** Checking for password is not null **/
    if ( !StringUtil.isEmptyOrNull( roomItem.getRoomPassword() ) ) {
      xmlWriter.startTag( TAG_PASSWORD );
      xmlWriter.text( roomItem.getRoomPassword() );
      xmlWriter.endTag();
    }
    /** History size **/
    xmlWriter.startTag( "history" );
    /** Checking for history receiving by count **/
    if ( com.tomclaw.mandarin.core.Settings.isMucHistoryMaxStanzas ) {
      xmlWriter.attribute( "maxstanzas",
              String.valueOf(
              com.tomclaw.mandarin.core.Settings.mucHistoryMaxStanzas ) );
    } else /** Checking for history receiving by time **/
    if ( com.tomclaw.mandarin.core.Settings.isMucHistorySeconds ) {
      xmlWriter.attribute( "seconds",
              String.valueOf(
              com.tomclaw.mandarin.core.Settings.mucHistorySeconds ) );
    } else {
      /** Receiving history by time **/
      xmlWriter.attribute( "maxchars",
              String.valueOf(
              com.tomclaw.mandarin.core.Settings.mucHistoryMaxChars ) );
    }
    xmlWriter.endTag();
    /** Ending tags **/
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String leaveRoom( XmlOutputStream xmlWriter, String from, RoomItem roomItem ) throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_PRESENCE );
    xmlWriter.attribute( ATT_FROM, from );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.attribute( ATT_TO, roomItem.getJid().concat( "/" ).concat( roomItem.getRoomNick() ) );
    xmlWriter.attribute( ATT_TYPE, "unavailable" );
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String changeRoomNick( XmlOutputStream xmlWriter, String from, RoomItem roomItem ) throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_PRESENCE );
    xmlWriter.attribute( ATT_FROM, from );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.attribute( ATT_TO, roomItem.getJid().concat( "/" ).concat( roomItem.getRoomNick() ) );
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String sendRoomConfigurationForm( XmlOutputStream xmlWriter,
          String roomJid, Form form )
          throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, VAL_SET );
    xmlWriter.attribute( ATT_TO, roomJid );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "http://jabber.org/protocol/muc#owner" );
    if ( form != null ) {
      /** Checking for form existance **/
      if ( form.objects != null && !form.objects.isEmpty() ) {
        TemplateCollection.sendFormObjects( xmlWriter, form );
      } else {
        /** Cancelling room configuration **/
        xmlWriter.startTag( TAG_X );
        xmlWriter.attribute( ATT_XMLNS, "jabber:x:data" );
        xmlWriter.attribute( ATT_TYPE, "cancel" );
      }
    }
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String sendRoomVisitorsListOperation( XmlOutputStream xmlWriter,
          String roomJid, String affiliation, String jid, String reason, int operation ) throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, operation == Mechanism.OPERATION_GET
            ? VAL_GET : VAL_SET );
    xmlWriter.attribute( ATT_TO, roomJid );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "http://jabber.org/protocol/muc#admin" );
    /** Destroying room tag **/
    xmlWriter.startTag( "item" );
    xmlWriter.attribute( ATT_AFFILIATION, affiliation );
    /** Checking for operation type to append JID **/
    if ( operation != Mechanism.OPERATION_GET ) {
      xmlWriter.attribute( ATT_JID, jid );
    }
    /** Checking for operation to add reason tag **/
    if ( operation == Mechanism.OPERATION_ADD && reason != null ) {
      xmlWriter.startTag( TAG_REASON );
      xmlWriter.text( reason );
      xmlWriter.endTag();
    }
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String sendRoomDestroyRequest( XmlOutputStream xmlWriter,
          String roomJid, String reason ) throws IOException {
    /** Generating request cookie **/
    String cookie = AccountRoot.generateCookie();
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, VAL_SET );
    xmlWriter.attribute( ATT_TO, roomJid );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "http://jabber.org/protocol/muc#owner" );
    /** Destroying room tag **/
    xmlWriter.startTag( "destroy" );
    xmlWriter.attribute( ATT_JID, roomJid );
    /** Checking for reason is not null-type **/
    if ( reason != null ) {
      xmlWriter.startTag( "reason" );
      xmlWriter.text( reason );
      xmlWriter.endTag();
    }
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String sendDiscoInfo(
          XmlOutputStream xmlWriter, String cookie, String jid ) throws IOException {
    /** Request with specified cookie **/
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, VAL_RESULT );
    xmlWriter.attribute( ATT_TO, jid );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "http://jabber.org/protocol/disco#info" );
    /** Identity **/
    xmlWriter.startTag( "identity" );
    xmlWriter.attribute( "category", "client" );
    xmlWriter.attribute( "type", "j2me" );
    xmlWriter.attribute( "name", "Mandarin" );
    xmlWriter.endTag();
    /** Features **/
    for ( int c = 0; c < FEATURES.length; c++ ) {
      xmlWriter.startTag( "feature" );
      xmlWriter.attribute( "var", FEATURES[c] );
      xmlWriter.endTag();
    }
    /** X data **/
    String platform = "J2ME";
    String configuration = "CLDC-1.0";
    try {
      platform = System.getProperty( "microedition.platform" );
      configuration = System.getProperty( "microedition.configuration" );
    } catch ( Throwable ex1 ) {
    }
    String version = MidletMain.version + " "
            + MidletMain.type + "-build " + MidletMain.build;
    String[][] fields = new String[][]{
      { "FORM_TYPE", "urn:xmpp:dataforms:softwareinfo" },
      { "os", platform },
      { "os_version", configuration },
      { "software", "Mandarin IM" },
      { "software_version", version }
    };
    /** Writing X fields **/
    xmlWriter.startTag( TAG_X );
    xmlWriter.attribute( ATT_XMLNS, "jabber:x:data" );
    xmlWriter.attribute( ATT_TYPE, "result" );
    /** Cycling fields **/
    for ( int c = 0; c < fields.length; c++ ) {
      /** Starting field **/
      xmlWriter.startTag( "field" );
      xmlWriter.attribute( "var", fields[c][0] );
      /** Checking for field is FORM_TYPE, that must be hidden **/
      if ( fields[c][0].equals( "FORM_TYPE" ) ) {
        xmlWriter.attribute( ATT_TYPE, "hidden" );
      }
      /** Writting value **/
      xmlWriter.startTag( "value" );
      xmlWriter.text( fields[c][1] );
      xmlWriter.endTag();
      /** Closing field tag **/
      xmlWriter.endTag();
    }
    xmlWriter.endTag();
    /** Closing tags **/
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  /**
   * Sending last activity for specified jid, request cookie
   * @param xmlWriter
   * @param cookie
   * @param jid
   * @param lastActivity
   * @return
   * @throws IOException 
   */
  public static String sendLastActivity(
          XmlOutputStream xmlWriter, String cookie, String jid, long lastActivity ) throws IOException {
    /** Request with specified cookie **/
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, VAL_RESULT );
    xmlWriter.attribute( ATT_TO, jid );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "jabber:iq:last" );
    xmlWriter.attribute( "seconds", String.valueOf( lastActivity ) );
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }

  public static String sendEntityTime( XmlOutputStream xmlWriter,
          String cookie, String jid ) throws IOException {
    /** Generating time offset and time string **/
    String utcTime = TimeUtil.getUtcTimeString( TimeUtil.getCurrentTime() );
    String tzoTime = ( ( TimeUtil.getGmtOffset() / 3600 ) > 0 ? "+" : "-" )
            .concat( ( TimeUtil.getGmtOffset() / 3600 ) >= 10 ? "" : "0" ).
            concat( String.valueOf( TimeUtil.getGmtOffset() / 3600 ) ).
            concat( ":00" );
    /** Iq tag **/
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, VAL_RESULT );
    xmlWriter.attribute( ATT_XMLNS, "jabber:client" );
    xmlWriter.attribute( ATT_TO, jid );
    xmlWriter.attribute( ATT_ID, cookie );
    /** Time **/
    xmlWriter.startTag( "time" );
    xmlWriter.attribute( ATT_XMLNS, "urn:xmpp:time" );
    xmlWriter.startTag( "utc" );
    xmlWriter.text( utcTime );
    xmlWriter.endTag();
    /** Time offset **/
    xmlWriter.startTag( "tzo" );
    xmlWriter.text( tzoTime );
    xmlWriter.endTag();
    /** Ending tags **/
    xmlWriter.endTag();
    xmlWriter.endTag();
    /** Flushing stream **/
    xmlWriter.flush();
    return cookie;
  }

  public static String sendVersion( XmlOutputStream xmlWriter, String cookie, String jid, String device, String version ) throws IOException {
    /** Request with specified cookie **/
    xmlWriter.startTag( TAG_IQ );
    xmlWriter.attribute( ATT_TYPE, VAL_RESULT );
    xmlWriter.attribute( ATT_TO, jid );
    xmlWriter.attribute( ATT_ID, cookie );
    xmlWriter.startTag( TAG_QUERY );
    xmlWriter.attribute( ATT_XMLNS, "jabber:iq:version" );
    xmlWriter.startTag( "name" );
    xmlWriter.text( "Mandarin IM" );
    xmlWriter.endTag();
    xmlWriter.startTag( "version" );
    xmlWriter.text( version );
    xmlWriter.endTag();
    xmlWriter.startTag( "os" );
    xmlWriter.text( device );
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.endTag();
    xmlWriter.flush();
    return cookie;
  }
}
