package com.tomclaw.mandarin.molecus;

import com.tomclaw.mandarin.core.Handler;
import com.tomclaw.mandarin.core.Queue;
import com.tomclaw.mandarin.core.QueueAction;
import com.tomclaw.mandarin.main.MidletMain;
import com.tomclaw.tcuilite.PopupItem;
import com.tomclaw.tcuilite.localization.Localization;
import com.tomclaw.utils.LogUtil;
import com.tomclaw.xmlgear.XmlSpore;
import java.util.Hashtable;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class ServiceItem extends BuddyItem {

  private PopupItem popupItem;
  private PopupItem regstPopup;
  private PopupItem unregPopup;
  private boolean isConnected;
  private boolean isTempPopup;

  /**
   * Creates temporary service item
   * @param jid
   * @param itemName 
   */
  public ServiceItem( String jid, String itemName ) {
    this( jid, itemName, null, true );
  }

  /**
   * Creates configured service item
   * @param jid
   * @param itemName
   * @param isTemp 
   */
  public ServiceItem( String jid, String itemName, String subscription,
          boolean isTemp ) {
    super( jid, itemName, subscription, isTemp );
    popupItem = null;
    regstPopup = null;
  }

  /**
   * Checks for popup item cached
   * @return boolean
   */
  public boolean isNoCachedPopup() {
    return ( popupItem == null || isTempPopup );
  }

  /**
   * Returns popup item
   * @return PopupItem
   */
  public PopupItem getCachedPopup() {
    return popupItem;
  }

  /**
   * Creates empty popup item
   * @return PopupItem
   */
  public PopupItem getNewPopupItem( boolean isTempPopup ) {
    this.isTempPopup = isTempPopup;
    popupItem = new PopupItem( Localization.getMessage( "ELEMENT" ) ) {
      public void actionPerformed() {
        MidletMain.mainFrame.updatePopup();
      }
    };
    return popupItem;
  }

  /**
   * Returns new register popup item
   * @return PopupItem
   */
  public PopupItem getRegisterPopup() {
    /** Checking for no cached register popup **/
    if ( regstPopup == null ) {
      /** Creating register popup item instance **/
      regstPopup = new PopupItem( Localization.getMessage( "REGISTER" ) ) {
        public void actionPerformed() {
          /** Checking for online **/
          if ( Handler.sureIsOnline() ) {
            /** Showing wait screen **/
            MidletMain.screen.setWaitScreenState( true );
            Session session = AccountRoot.getSession();
            /** Creating xml spore **/
            XmlSpore xmlSpore = new XmlSpore() {
              public void onRun() throws Throwable {
                LogUtil.outMessage( "Register in: " + name );
                String cookie = TemplateCollection.sendRegisterRequest(
                        this, name, false );
                QueueAction action = new QueueAction() {
                  public void actionPerformed( Hashtable params ) {
                    /** Info received **/
                    String errorCause = ( String ) params.get( "ERROR_CAUSE" );
                    /** Checking for error **/
                    if ( errorCause == null ) {
                      LogUtil.outMessage( "Command ack received" );
                      /** Preparing and configuring form object **/
                      Form form = ( Form ) params.get( "FORM" );
                      form.status = "executing";
                      /** Creating and configuring item object **/
                      Item item = new Item( name, null,
                              Localization.getMessage( "REGISTER" ) );
                      /** Creating register command **/
                      Command command = new Command(
                              Localization.getMessage( "REGISTER" ) ) {
                        public void actionAttempt() {
                          /** Showing wait screen **/
                          MidletMain.screen.setWaitScreenState( true );
                          /** Command invokation **/
                          LogUtil.outMessage( "Register command invokation: "
                                  + item.jid + ", " + name + ", temp: " + getTemp() );
                          Mechanism.invokeRegistration( item.jid, form, getTemp() );
                        }
                      };
                      /** Setting up command fields **/
                      command.form = form;
                      command.item = item;
                      /** Initialize left soft **/
                      form.initLeftSoft();
                      /** Adding customized command **/
                      form.leftSoft.addSubItem( command );
                      /** Sending handler evnt **/
                      Handler.showCommandFrame( item, form, null );
                      LogUtil.outMessage( "Objects count: " + params.size() );
                    }
                  }
                };
                action.setCookie( cookie );
                Queue.pushQueueAction( action );
              }
            };
            /** Releasing xml spore **/
            session.getSporedStream().releaseSpore( xmlSpore );
          }
        }
      };
    }
    return regstPopup;
  }

  /**
   * Returns new register popup item
   * @return PopupItem
   */
  public PopupItem getUnRegisterPopup() {
    /** Checking for no cached unregister popup **/
    if ( unregPopup == null ) {
      /** Creating instance for unregister popup item **/
      unregPopup = new PopupItem( Localization.getMessage( "UNREGISTER" ) ) {
        public void actionPerformed() {
          /** Mechanism invocation **/
          Mechanism.sendRemoveRegistration( getJid() );
        }
      };
    }
    return unregPopup;
  }

  /**
   * Setting up subscription value
   * @param subscription 
   */
  public void setSubscription( String subscription ) {
    super.setSubscription( subscription );
    /** Removing cached menu **/
    popupItem = null;
    /** Checking for service connection **/
    if ( subscription != null && !subscription.equals( "none" )
            && !getTemp() ) {
      /** Serivce is connected **/
      isConnected = true;
    } else {
      isConnected = false;
    }
  }

  /**
   * Checking for service is connected
   * @return boolean
   */
  public boolean isConnected() {
    return isConnected;
  }

  /**
   * Returns internal type of item
   * @return Integer index
   */
  public int getInternalType() {
    return TYPE_SERVICE_ITEM;
  }
}
