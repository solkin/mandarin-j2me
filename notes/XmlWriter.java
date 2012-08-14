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
public class XmlWriter {

  private static String[] symbols = new String[]{ "&", "<", ">", "'", "\"" };
  private static String[] replace = new String[]{ "&amp;", "&lt;", "&gt;", "&apos;", "&quot;" };
  private OutputStream writer;
  private java.io.ByteArrayOutputStream baos;
  private Stack tags;
  private boolean inside_tag;
  private final ByteArrayOutputStream enc_baos = new ByteArrayOutputStream();
  private final DataOutputStream enc_dos = new DataOutputStream( enc_baos );

  public XmlWriter( final OutputStream out ) throws UnsupportedEncodingException {
    writer = new DataOutputStream( out/*, "UTF-8"*/ );
    baos = new ByteArrayOutputStream();
    this.tags = new Stack();
    this.inside_tag = false;
  }

  public void close() {
    try {
      writer.close();
      baos.close();
    } catch ( IOException e ) {
    }
  }

  public void flush() throws IOException {
    if ( inside_tag ) {
      baos.write( '>' ); // prevent Invalid XML fatal error
      inside_tag = false;
    }
    baos.flush();
    writer.write( baos.toByteArray() );
    LogUtil.outMessage ( "--> " + StringUtil.byteArrayToString ( baos.toByteArray() ) );
    baos.reset();
    writer.flush();
  }

  public void writeDirect( byte[] data ) throws IOException {
    writer.write( data );
    writer.flush();
  }

  public void startTag( final String tag ) throws IOException {
    if ( inside_tag ) {
      baos.write( '>' );
    }

    baos.write( '<' );
    baos.write( tag.getBytes() );
    tags.push( tag );
    inside_tag = true;
  }

  public void attribute( final String atr, final String value ) throws IOException {
    if ( value == null ) {
      return;
    }
    baos.write( ' ' );
    baos.write( atr.getBytes() );
    baos.write( "=\'".getBytes() );
    writeEscaped( value );
    baos.write( '\'' );
  }

  public void endTag() throws IOException {
    try {
      final String tagname = ( String ) tags.pop();
      if ( inside_tag ) {
        baos.write( "/>".getBytes() );
        inside_tag = false;
      } else {
        baos.write( "</".getBytes() );
        baos.write( tagname.getBytes() );
        baos.write( '>' );
      }
    } catch ( final EmptyStackException e ) {
    }
  }

  public void text( final String str ) throws IOException {
    if ( inside_tag ) {
      baos.write( '>' );
      inside_tag = false;
    }
    writeEscaped( str );
  }

  private void writeEscaped( final String str ) throws IOException {
    baos.write( stringToByteArray( toXmlWellFormed( str ) ) );
  }

  public byte[] stringToByteArray( final String str ) {
    try {
      enc_baos.reset();
      enc_dos.writeUTF( str );
      byte[] result = new byte[ enc_baos.size() - 2 ];
      System.arraycopy( enc_baos.toByteArray(), 2, result, 0, enc_baos.size() - 2 );
      return result;
    } catch ( Exception e ) {
    }
    return null;
  }

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
