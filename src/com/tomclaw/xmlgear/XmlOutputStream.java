package com.tomclaw.xmlgear;

import com.tomclaw.utils.LogUtil;
import com.tomclaw.utils.StringUtil;
import java.io.*;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class XmlOutputStream extends ByteArrayOutputStream {

  /** Static strings **/
  private static final String[] symbols = new String[]{ "&", "<", ">", "'", "\"" };
  private static final String[] replace = new String[]{ "&amp;", "&lt;", "&gt;", "&apos;", "&quot;" };
  /** Runtime **/
  private Stack tags;
  private boolean inside_tag;
  private final ByteArrayOutputStream enc_baos = new ByteArrayOutputStream();
  private final DataOutputStream enc_dos = new DataOutputStream( enc_baos );

  /**
   * Creating new Xml stream
   */
  public XmlOutputStream() {
    this.tags = new Stack();
    this.inside_tag = false;
  }

  /**
   * Correcting and flushing stream
   * @throws IOException 
   */
  public void flush() throws IOException {
    if ( inside_tag ) {
      write( '>' ); // prevent Invalid XML fatal error
      inside_tag = false;
    }
    super.flush();
    LogUtil.outMessage( "flushed: ".concat( StringUtil.byteArrayToString( toByteArray() ) ) );
  }

  /**
   * Direct out to stream and write
   * @param data
   * @throws IOException 
   */
  public void writeDirect( byte[] data ) throws IOException {
    write( data );
    flush();
  }

  /**
   * Starting tag
   * @param tag
   * @throws IOException 
   */
  public void startTag( final String tag ) throws IOException {
    if ( inside_tag ) {
      write( '>' );
    }

    write( '<' );
    write( tag.getBytes() );
    tags.push( tag );
    inside_tag = true;
  }

  /**
   * Adding attribute and value
   * @param atr
   * @param value
   * @throws IOException 
   */
  public void attribute( final String atr, final String value ) throws IOException {
    if ( value == null ) {
      return;
    }
    write( ' ' );
    write( atr.getBytes() );
    write( "=\'".getBytes() );
    writeEscaped( value );
    write( '\'' );
  }

  /**
   * Closing opened tag
   * @throws IOException 
   */
  public void endTag() throws IOException {
    try {
      final String tagname = ( String ) tags.pop();
      if ( inside_tag ) {
        write( "/>".getBytes() );
        inside_tag = false;
      } else {
        write( "</".getBytes() );
        write( tagname.getBytes() );
        write( '>' );
      }
    } catch ( final EmptyStackException e ) {
    }
  }

  /**
   * Writing tag body
   * @param str
   * @throws IOException 
   */
  public void text( final String str ) throws IOException {
    if ( inside_tag ) {
      write( '>' );
      inside_tag = false;
    }
    writeEscaped( str );
  }

  /**
   * Escaping and writing string
   * @param str
   * @throws IOException 
   */
  private void writeEscaped( final String str ) throws IOException {
    write( stringToByteArray( toXmlWellFormed( str ) ) );
  }

  /**
   * Encoding string to UTF-8 byte array
   * @param str
   * @return byte[]
   */
  public byte[] stringToByteArray( final String str ) {
    try {
      /** Applying encoding **/
      enc_baos.reset();
      enc_dos.writeUTF( str );
      byte[] result = new byte[ enc_baos.size() - 2 ];
      System.arraycopy( enc_baos.toByteArray(), 2, result, 0, enc_baos.size() - 2 );
      return result;
    } catch ( Exception e ) {
      /** Nothing to do in this case **/
    }
    return null;
  }

  /**
   * Escaping symbols
   * @param string
   * @return String
   */
  public String toXmlWellFormed( String string ) {
    int location = 0;
    for ( int c = 0; c < symbols.length; c++ ) {
      location = string.indexOf( symbols[c], location );
      if ( location >= 0 ) {
        string = string.substring( 0, location ).concat( replace[c] ).concat( string.substring( location + 1 ) );
        location += replace[c].length();
        c--;
        continue;
      }
      location = 0;
    }
    return string;
  }
};
