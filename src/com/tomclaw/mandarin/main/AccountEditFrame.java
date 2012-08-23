package com.tomclaw.mandarin.main;

import com.tomclaw.mandarin.core.Queue;
import com.tomclaw.mandarin.core.QueueAction;
import com.tomclaw.mandarin.core.Storage;
import com.tomclaw.mandarin.molecus.*;
import com.tomclaw.tcuilite.*;
import com.tomclaw.tcuilite.localization.Localization;
import com.tomclaw.utils.LogUtil;
import com.tomclaw.xmlgear.XmlSpore;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class AccountEditFrame extends Window {

  /** Own objects **/
  private Pane pane;
  private int stepIndex = 0x00;
  private static final int FRAME_TYPE_REGISTER = 0x00;
  private static final int FRAME_TYPE_ALREXIST = 0x01;
  private Form form;
  private Session session;
  /** Redirection first frame **/
  private RadioGroup methodType;
  private int frameType = 0x00;
  /** Already exist frame **/
  private Field molecusLogin;
  private Field molecusPassword;

  public AccountEditFrame() {
    super( MidletMain.screen );
    s_prevWindow = MidletMain.mainFrame;

    soft = new Soft( MidletMain.screen );
    soft.leftSoft = new PopupItem( Localization.getMessage( "NEXT" ) ) {
      public void actionPerformed() {
        if ( stepIndex == 0x00 ) {
          frameType = methodType.getCombed();
        }
        if ( frameType == FRAME_TYPE_REGISTER ) {
          switch ( stepIndex ) {
            case 0x00: {
              startRegisterAction();
              break;
            }
            case 0x01: {
              sendRegistrationForm();
              break;
            }
          }
        } else {
          if ( frameType == FRAME_TYPE_ALREXIST ) {
            switch ( stepIndex ) {
              case 0x00: {
                showAlrExistAccountPane();
                break;
              }
              case 0x01: {
                new Thread() {
                  public void run() {
                    /** Showing wait screen **/
                    MidletMain.screen.setWaitScreenState( true );
                    /** Saving account info **/
                    if ( saveAccountInfo() ) {
                      /** Switching to main frame **/
                      showResult( false, Localization.getMessage( "ACCOUNT_SAVED" ) );
                    } else {
                      /** Fields username and password not defined **/
                      LogUtil.outMessage( "Fields username and password not found" );
                      showResult( true, Localization.getMessage( "FIELDS_NOT_DEFINED" ) );
                    }
                  }
                }.start();
                break;
              }
            }
          }
        }
        stepIndex++;
      }
    };
    soft.rightSoft = new PopupItem( Localization.getMessage( "CANCEL" ) ) {
      public void actionPerformed() {
        MidletMain.screen.setActiveWindow( s_prevWindow );
      }
    };

    pane = new Pane( null, false );

    pane.addItem( new Label( Localization.getMessage( "SELECT_METHOD_TYPE" ) ) );

    methodType = new RadioGroup();

    Radio method1 = new Radio( Localization.getMessage( "REGISTER_MOLECUS_ACCOUNT" ), true );
    method1.setFocused( true );
    methodType.addRadio( method1 );
    pane.addItem( method1 );

    Radio method2 = new Radio( Localization.getMessage( "ALREADY_HAVE_MOLECUS_ACCOUNT" ), false );
    method2.setFocused( true );
    methodType.addRadio( method2 );
    pane.addItem( method2 );

    setGObject( pane );
  }

  /**
   * Decrimine step
   */
  private void stepOver() {
    stepIndex--;
  }

  /**
   * Searches pane.items for fields "username" and "password"
   * and starts saveAccountInfo( username, password );
   */
  public boolean saveAccountInfo() {
    String username = "";
    String password = "";
    /** Searching for fields named as "username" and "password" **/
    for ( int c = 0; c < pane.items.size(); c++ ) {
      PaneObject object = ( PaneObject ) pane.items.elementAt( c );
      if ( object.getName() != null ) {
        if ( object.getName().equals( "username" ) ) {
          username = object.getStringValue();
        } else if ( object.getName().equals( "password" ) ) {
          password = object.getStringValue();
        }
      }
    }
    /** Checking compliance of username and password **/
    if ( username.length() > 0 && password.length() > 0 ) {
      /** Saving registration info **/
      saveAccountInfo( username, password );
      return true;
    }
    return false;
  }

  /** 
   * Checks and saves current account info
   */
  private void saveAccountInfo( String username, String password ) {
    /** Checking for username and password correct **/
    if ( username.length() > 0
            && password.length() > 0 ) {
      /** Data is correct **/
      AccountRoot.setUserName( username );
      AccountRoot.setPassword( password );
      /** Saving data **/
      com.tomclaw.mandarin.core.Settings.saveAll();
      Storage.save();
    } else {
      /** Showing error popup dialog **/
      LogUtil.outMessage( "Both fields required" );
      showResult( true, Localization.getMessage( "BOTH_FIELDS_REQUIRED" ) );
    }
  }

  /**
   * Starts session, sends stream initialization, 
   * sends registration request
   */
  private void startRegisterAction() {
    /** Showing wait screen **/
    MidletMain.screen.setWaitScreenState( true );
    /** Creating new thread because of new connection start **/
    new Thread() {
      public void run() {
        try {
          /** Creating session instance for registration **/
          if ( session == null ) {
            session = new Session( false );
          } else {
            session.disconnect();
          }
          session.establishConnection( AccountRoot.getRegisterHost(), AccountRoot.getRegisterPort(), AccountRoot.getUseSsl() );
          session.start();
          /** Creating xml spore **/
          XmlSpore xmlSpore = new XmlSpore() {
            public void onRun() throws Throwable {
              /** Starting stream **/
              TemplateCollection.sendStartXmlStream( this, AccountRoot.getRegisterHost(), null );
              /** Sending registration request **/
              String cookie = TemplateCollection.sendRegisterRequest( this, AccountRoot.getRegisterHost(), false );
              QueueAction action = new QueueAction() {
                public void actionPerformed( Hashtable params ) {
                  /** Registration request reply **/
                  String errorCause = ( String ) params.get( "ERROR_CAUSE" );
                  if ( errorCause == null ) {
                    /** Registration form received **/
                    LogUtil.outMessage( "Registration form received!" );
                    Form form = ( Form ) params.get( "FORM" );
                    showRegisterAccountPane( form );
                    return;
                  } else if ( errorCause.equals( "" ) ) {
                    /** Cause unknown **/
                    LogUtil.outMessage( "Cause unknown" );
                    errorCause = Localization.getMessage( "CAUSE_UNKNOWN" );
                  }
                  LogUtil.outMessage( "Error cause: " + errorCause );
                  showResult( true, Localization.getMessage( errorCause ) );
                }
              };
              action.setCookie( cookie );
              Queue.pushQueueAction( action );
            }

            public void onError( Throwable ex ) {
              String errorCause;
              if ( ex instanceof IOException ) {
                errorCause = Localization.getMessage( "IO_EXCEPTION" );
              } else {
                errorCause = Localization.getMessage( "FEEDBACK_THIS" ).concat( ex.getMessage() );
              }
              showResult( true, errorCause );
              LogUtil.outMessage( "Error while register request: " + errorCause, true );
            }
          };
          /** Releasing xml spore **/
          session.getSporedStream().releaseSpore( xmlSpore );
        } catch ( Throwable ex ) {
          showResult( true, Localization.getMessage( "IO_EXCEPTION" ) );
        }
      }
    }.start();
  }

  /**
   * Sends registration form to server
   */
  private void sendRegistrationForm() {
    /** Showing wait screen **/
    MidletMain.screen.setWaitScreenState( true );
    /** Creating xml spore **/
    XmlSpore xmlSpore = new XmlSpore() {
      public void onRun() throws Throwable {
        /** Sending registration form **/
        form.objects = pane.items;
        String cookie = TemplateCollection.sendRegistrationForm(
                this, form, AccountRoot.getRegisterHost() );
        QueueAction action = new QueueAction() {
          public void actionPerformed( Hashtable params ) {
            /** Registration form sent **/
            LogUtil.outMessage( "Registration ack received!" );
            String errorCause = ( String ) params.get( "ERROR_CAUSE" );
            if ( errorCause == null ) {
              /** No errors **/
              LogUtil.outMessage( "No errors" );
              if ( saveAccountInfo() ) {
                showResult( false, Localization.getMessage( "YOU_REGISTERED" ) );
              } else {
                /** Fields username and password not found **/
                LogUtil.outMessage( "Fields username and password not found" );
                showResult( true, Localization.getMessage( "FIELDS_NOT_DEFINED" ) );
              }
              return;
            } else if ( errorCause.equals( "" ) ) {
              /** Cause unknown **/
              LogUtil.outMessage( "Cause unknown" );
              errorCause = Localization.getMessage( "CAUSE_UNKNOWN" );
            }
            LogUtil.outMessage( "Error cause: " + errorCause );
            showResult( true, Localization.getMessage( errorCause ) );
          }
        };
        action.setCookie( cookie );
        Queue.pushQueueAction( action );
      }

      public void onError( Throwable ex ) {
        String errorCause;
        if ( ex instanceof IOException ) {
          errorCause = Localization.getMessage( "IO_EXCEPTION" );
        } else {
          errorCause = Localization.getMessage( "FEEDBACK_THIS" ).concat( ex.getMessage() );
        }
        showResult( true, errorCause );
        LogUtil.outMessage( "Error while register request: " + errorCause, true );
      }
    };
    /** Releasing xml spore **/
    session.getSporedStream().releaseSpore( xmlSpore );
  }

  /**
   * Shows deserialized form, received from server
   * @param objects
   * @param isFormBased 
   */
  private void showRegisterAccountPane( Form form ) {
    /** Accepting items to current pane **/
    this.form = form;
    pane.items = form.objects;
    /** Hiding wait notify **/
    MidletMain.screen.setWaitScreenState( false );
    /** Repainting **/
    MidletMain.screen.repaint( Screen.REPAINT_STATE_PLAIN );
  }

  /**
   * Shows popup window
   * @param isError
   * @param errorCause 
   */
  private void showResult( boolean isError, String message ) {
    final Window window;
    if ( isError ) {
      /** On error step over required **/
      stepOver();
      /** Determing window for following operations **/
      window = this;
    } else {
      /** Determing window for following operations **/
      window = MidletMain.mainFrame;
      /** Update main frame **/
      MidletMain.mainFrame.checkGui();
      /** Switching **/
      MidletMain.screen.setActiveWindow( MidletMain.mainFrame );
    }
    /** Creating requireddialog frame **/
    String title = Localization.getMessage( isError ? "ERROR" : "SUCCESS" );
    Soft dialogSoft = new Soft( MidletMain.screen );
    if ( isError ) {
      dialogSoft.leftSoft = new PopupItem( Localization.getMessage( "CLOSE" ) ) {
        public void actionPerformed() {
          window.closeDialog();
        }
      };
    } else {
      dialogSoft.leftSoft = new PopupItem( Localization.getMessage( "YES" ) ) {
        public void actionPerformed() {
          Mechanism.accountLogin( StatusUtil.onlineIndex );
          window.closeDialog();
        }
      };
      dialogSoft.rightSoft = new PopupItem( Localization.getMessage( "NO" ) ) {
        public void actionPerformed() {
          window.closeDialog();
        }
      };
    }
    Dialog resultDialog = new Dialog( MidletMain.screen, dialogSoft, title, message );
    window.showDialog( resultDialog );
    /** Hiding wait notify **/
    MidletMain.screen.setWaitScreenState( false );
  }

  private void showAlrExistAccountPane() {
    pane.items.removeAllElements();
    Label titleLabel = new Label( Localization.getMessage( "MOLECUS_EXIST_TITLE" ) );
    titleLabel.setTitle( true );
    pane.addItem( titleLabel );
    pane.addItem( new Label( Localization.getMessage( "USERNAME" ) ) );
    molecusLogin = new Field( "" );
    molecusLogin.setName( "username" );
    molecusLogin.setFocused( true );
    pane.addItem( molecusLogin );
    pane.addItem( new Label( Localization.getMessage( "PASSWORD" ) ) );
    molecusPassword = new Field( "" );
    molecusPassword.setName( "password" );
    pane.addItem( molecusPassword );
    /** Repainting **/
    MidletMain.screen.repaint( Screen.REPAINT_STATE_PLAIN );
  }
}
