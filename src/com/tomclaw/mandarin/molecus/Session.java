package com.tomclaw.mandarin.molecus;

import com.tomclaw.mandarin.core.Handler;
import com.tomclaw.mandarin.core.Settings;
import com.tomclaw.mandarin.net.NetConnection;
import com.tomclaw.utils.LogUtil;
import com.tomclaw.xmlgear.SporedStream;
import com.tomclaw.xmlgear.XmlInputStream;
import java.io.IOException;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Session {

  /** General **/
  private boolean isMain = false;
  /** Network connection **/
  private NetConnection netConnection;
  /** XmlGear **/
  private XmlInputStream xmlInputStream;
  private SporedStream sporedStream;
  /** Runtime **/
  private Thread thread;
  private Thread ping;
  private boolean isAlive;

  /**
   * Constructs session class instance for main or not role
   * @param isMain 
   */
  public Session( boolean isMain ) {
    this.isMain = isMain;
  }

  /**
   * Returns net connection
   * @return NetConnection
   */
  public NetConnection getNetConnection() {
    return netConnection;
  }

  /**
   * Establishing connection to specified host and port
   * @param host
   * @param port
   * @param isUseSsl
   * @throws IOException 
   */
  public void establishConnection( String host, int port, boolean isUseSsl ) throws IOException {
    /** Trying to stop previous connection **/
    disconnect();
    /** Creating new connection **/
    netConnection = new NetConnection();
    netConnection.connectAddress( host, port, isUseSsl );
    xmlInputStream = new XmlInputStream( netConnection.inputStream );
    sporedStream = new SporedStream( netConnection.outputStream );
  }

  public void disconnect() {
    /** Disconnecting from server **/
    if ( netConnection != null ) {
      try {
        /** Flushing data **/
        netConnection.flush();
        netConnection.disconnect();
        /** Closing streams **/
        xmlInputStream.close();
        sporedStream.close();
      } catch ( Throwable ex ) {
        LogUtil.outMessage( "Exception while disconnecting: " + ex.getMessage(), true );
      }
    }
    netConnection = null;
    /** Trying to stop active thread **/
    stop();
    /** Checking for main session role**/
    if ( isMain ) {
      /** Sending disconnect event to handler **/
      LogUtil.outMessage( "Disconnected" );
      Handler.disconnectEvent();
    }
  }

  /**
   * Starting listener thread
   */
  public void start() {
    /** Stopping thread if it is already running **/
    stop();
    /** Checking for netConnection initialized **/
    if ( netConnection != null ) {
      /** Creating thread instance **/
      thread = new Thread() {
        public void run() {
          /** Setting up isAlive to true value **/
          isAlive = true;
          LogUtil.outMessage( "Session now alive. " );
          try {
            while ( isAlive && xmlInputStream.nextTag() ) {
              Parser.process( Session.this, xmlInputStream );
            }
            /** We are normally disconnected **/
            LogUtil.outMessage( "Cycle exit (main). " );
          } catch ( Throwable ex ) {
            /** Something strange in stream **/
            LogUtil.outMessage( "Exception in main session stream thread: " + ex.getMessage(), true );
          }
          /** Destroying thread **/
          isAlive = false;
          disconnect();
          LogUtil.outMessage( "Connection destroyed (main). " );
        }
      };
      /** Setting up new thread **/
      thread.setPriority( Thread.MIN_PRIORITY );
      /** Thread start **/
      thread.start();
      /** Creating ping thread to keep connection alive **/
      ping = new Thread() {
        public void run() {
          try {
            /** Sending ping every delay seconds **/
            while ( isAlive ) {
              sleep( Settings.pingDelay );
              /** Checking for the session is main **/
              if ( isMain ) {
                /** Sending ping data directly **/
                LogUtil.outMessage( "Sending ping" );
                Mechanism.sendPing( Session.this, netConnection.host );
              }
            }
            /** Normally ping cycle exit **/
            LogUtil.outMessage( "Ping cycle exit. " );
          } catch ( Throwable ex ) {
            /** Something strange in ping stream **/
            LogUtil.outMessage( "Exception in ping thread thread: " + ex.getMessage(), true );
          }
        }
      };
      /** Setting up minimum priority to the ping thread **/
      ping.setPriority( Thread.MIN_PRIORITY );
      /** Staring ping thread **/
      ping.start();
    }
  }

  /**
   * Stopping listener thread
   */
  private void stop() {
    /** Checking thread for non-null **/
    if ( thread != null ) {
      LogUtil.outMessage( "Thread stopping (session)..." );
      if ( isAlive ) {
        /** Stopping parser cycle **/
        isAlive = false;
        try {
          /** Waiting for thread to stop **/
          thread.join();
          ping.join();
        } catch ( InterruptedException ex ) {
          LogUtil.outMessage( "Exception while stopping main session thread: " + ex.getMessage(), true );
        }
      }
      thread = null;
      ping = null;
      LogUtil.outMessage( "Thread stopped (session)." );
    } else {
      /** Stopping parser cycle **/
      isAlive = false;
    }
  }

  /** 
   * Returns XmlWriter 
   * @return xmlWriter
   */
  public SporedStream getSporedStream() {
    return sporedStream;
  }

  /**
   * Returns XmlInputStream
   * @return xmlInputStream
   */
  public XmlInputStream getXmlReader() {
    return xmlInputStream;
  }
}
