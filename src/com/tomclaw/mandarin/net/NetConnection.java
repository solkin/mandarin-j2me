package com.tomclaw.mandarin.net;

import com.tomclaw.utils.LogUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.SecureConnection;
import javax.microedition.io.SocketConnection;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Игорь
 */
public class NetConnection {

  public String host;
  public int port;
  private SocketConnection socket = null;
  public OutputStream outputStream = null;
  public InputStream inputStream = null;

  public void connectAddress ( String host, int port, boolean isUseSsl ) throws IOException {
    /** Saving host and port **/
    this.host = host;
    this.port = port;
    LogUtil.outMessage ( "SSL: " + isUseSsl );
    /** Checking for SSL usage **/
    if ( isUseSsl ) {
      socket = ( SecureConnection ) Connector.open ( "ssl://" + host + ":" + port, Connector.READ_WRITE );
    } else {
      socket = ( SocketConnection ) Connector.open ( "socket://" + host + ":" + port,
              Connector.READ_WRITE );
    }
    /** Obtain I/O streams **/
    outputStream = socket.openOutputStream ();
    inputStream = socket.openInputStream ();
    LogUtil.outMessage ( "Connected successfully" );
  }

  public void disconnect () throws IOException {
    /** Closing I/O streams **/
    outputStream.close ();
    inputStream.close ();
    socket.close ();
  }

  public void flush () throws IOException {
    outputStream.flush ();
  }

  public byte[] read ( int length ) throws IOException, InterruptedException, java.io.InterruptedIOException, java.lang.IndexOutOfBoundsException {
    byte[] data = new byte[ length ];
    int dataReadSum = 0;
    int dataRead;
    do {
      dataRead = inputStream.read ( data, dataReadSum, data.length - dataReadSum );
      if ( dataRead == -1 ) {
        throw new IOException ();
      }
      dataReadSum += dataRead;
    } while ( dataReadSum < data.length );
    // MidletMain.incrementDataCount( length );
    return data;
  }

  public int getAvailable () throws IOException {
    return inputStream.available ();
  }
}
