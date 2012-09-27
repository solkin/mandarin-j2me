package com.tomclaw.xmlgear;

import com.tomclaw.utils.LogUtil;
import java.io.IOException;
import java.io.InputStream;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class XmlInputStream {

  public static final int TAG_UNKNOWN = 0xff;
  public static final int TAG_PLAIN = 0x00;
  public static final int TAG_QUESTION = 0x01;
  public static final int TAG_SELFCLOSING = 0x02;
  public static final int TAG_CLOSING = 0x03;
  public static final int TAG_COMMENT = 0x04;
  private static String[] symbols = new String[]{ "&amp;", "&lt;", "&gt;", "&apos;", "&quot;" };
  private static String[] replace = new String[]{ "&", "<", ">", "'", "\"" };
  public InputStream inputStream;
  public String tagName = null;
  public int tagType = TAG_UNKNOWN;
  public String body;
  public java.util.Hashtable attributes = new java.util.Hashtable();
  private int location;
  private String str = "";
  private int code;
  private int code2;
  private int code3;
  private int code4;
  private String mb_body;
  private StringBuffer buffer = new StringBuffer();
  private boolean isReadTagName;
  private boolean isCommentTag;
  private boolean isReadAttributes;
  private boolean isReadingAttrName;
  private boolean isReadingAttrValue;
  private boolean isAwaitingAttrName;
  private boolean isAwaitingAttrValue;
  private int read;
  private int p_read;
  private String attribute;
  private int tagSize;
  private int utf_offset;
  public StringBuffer debug_full_tag = new StringBuffer();

  public XmlInputStream( InputStream inputStream ) {
    this.inputStream = inputStream;
  }

  /**
   * Parsing next tag from input stream
   * @return boolean document end
   * @throws Throwable 
   */
  public boolean nextTag() throws Throwable {
    tagName = null;
    tagType = TAG_UNKNOWN;
    p_read = 0;
    buffer.setLength( 0 );
    isReadTagName = false;
    isCommentTag = false;
    isReadAttributes = false;
    isReadingAttrName = false;
    isReadingAttrValue = false;
    isAwaitingAttrName = false;
    isAwaitingAttrValue = false;
    mb_body = "";
    body = "";
    attributes.clear();
    attribute = null;
    tagSize = 0;
    debug_full_tag.setLength( 0 );
    while ( ( read = inputStream.read() ) != -1 ) {
      /** Debug tracing **/
      debug_full_tag.append( ( char ) read );
      /** Tag size increasing **/
      tagSize++;
      buffer.append( ( char ) read );
      if ( isReadTagName ) {
        if ( read == '?' ) {
          tagType = TAG_QUESTION;
          isReadAttributes = true;
          buffer.delete( 0, 1 );
        } else {
          if ( read == '!' ) {
            tagType = TAG_COMMENT;
            isCommentTag = true;
            isReadTagName = false;
          } else {
            if ( read == '/' ) {
              if ( buffer.length() > 1 ) {
                tagName = subChars( buffer, 0, buffer.length() - 1 );
                tagType = TAG_SELFCLOSING;
              } else {
                tagType = TAG_CLOSING;
              }
              buffer.setLength( 0 );
              isReadTagName = false;
              isReadAttributes = true;
              body = mb_body;
            } else {
              if ( read == '>' ) {
                tagType = TAG_PLAIN;
                tagName = subChars( buffer, 0, buffer.length() - 1 );
                isReadTagName = false;
                isReadAttributes = false;
                LogUtil.outMessage( "<-- " + debug_full_tag.toString() );
                return true;
              } else {
                if ( read == ' ' ) {
                  tagType = TAG_PLAIN;
                  tagName = subChars( buffer, 0, buffer.length() - 1 );
                  isReadTagName = false;
                  isReadAttributes = true;
                  isAwaitingAttrName = true;
                }
              }
            }
          }
        }
      } else {
        if ( isReadAttributes ) {
          if ( read == '>' ) {
            if ( tagType == TAG_CLOSING ) {
              tagName = subChars( buffer, 0, buffer.length() - 1 );
            } else {
              if ( buffer.length() >= 2 && buffer.charAt( buffer.length() - 2 ) == '/' ) {
                tagType = TAG_SELFCLOSING;
              } else {
                if ( buffer.length() >= 2 && buffer.charAt( buffer.length() - 2 ) == '?' ) {
                  tagType = TAG_QUESTION;
                }
              }
            }
            isAwaitingAttrName = false;
            LogUtil.outMessage( "<-- " + debug_full_tag.toString() );
            return true;
          } else {
            if ( isAwaitingAttrName && read != ' ' && read != '\t' && read != '\n' ) {
              isAwaitingAttrName = false;
              isReadingAttrName = true;
              buffer.setLength( 0 );
              buffer.append( ( char ) read );
            }
            if ( isReadingAttrName && ( read == '=' || read == ' ' || read == '\t' || read == '\n' ) ) {
              isReadingAttrName = false;
              isAwaitingAttrValue = true;
              buffer.setLength( buffer.length() - 1 );
              attribute = buffer.toString();
            } else {
              if ( isAwaitingAttrValue && ( read == '\"' || read == '\'' ) ) {
                isAwaitingAttrValue = false;
                isReadingAttrValue = true;
                buffer.setLength( 0 );
              } else {
                if ( isReadingAttrValue && ( read == '\'' || read == '\"' ) && p_read != '\\' ) {
                  isReadingAttrValue = false;
                  isAwaitingAttrName = true;
                  attributes.put( attribute, subChars( buffer, 0, buffer.length() - 1 ) );
                }
              }
            }
          }
        } else {
          if ( isCommentTag ) {
            if ( read == '>' ) {
              isCommentTag = false;
              LogUtil.outMessage( "<-- " + debug_full_tag.toString() );
              return true;
            }
          } else {
            if ( read == '<' ) {
              mb_body = subChars( buffer, 0, buffer.length() - 1 );
              buffer.setLength( 0 );
              isReadTagName = true;
            }
          }
        }
      }
      p_read = read;
    }
    LogUtil.outMessage( "<-- " + debug_full_tag.toString() );
    return false;
  }
  
  /**
   * Checks for an attribute is present
   * @param attribute
   * @return boolean
   */
  public boolean checkAttr(String attribute) {
    return attributes.containsKey( attribute );
  }

  /**
   * Returns attribute for specified key
   * @param attribute
   * @param isNullMayBe
   * @return String
   */
  public String getAttrValue( String attribute, boolean isNullMayBe ) {
    /* Obtain value from hashtable **/
    String value = ( String ) attributes.get( attribute );
    /** Checking for value is null-type **/
    return isNullMayBe ? value : ( value == null ? "" : value );
  }

  /**
   * Returns boolean attribute value
   * @param attribute
   * @return boolean
   */
  public boolean getAttrBoolean( String attribute ) {
    /** Obtain value string for attribute **/
    String value = getAttrValue( attribute, false );
    /** Checking for value is true **/
    if ( value.equals( "true" ) || value.equals( "1" ) ) {
      return true;
    }
    return false;
  }

  /**
   * Returns before current tag body
   * @return String body
   */
  public String getBody() {
    return body == null ? "" : body;
  }

  /**
   * Closing input stream
   * @throws IOException 
   */
  public void close() throws IOException {
    inputStream.close();
  }
  
  private String subChars( StringBuffer sb, int start, int end ) {
    return toStringFromXmlWellFormed( byteArrayToString( sb, start, end ) );
  }
  
  private String byteArrayToString( StringBuffer sb, int start, int end ) {
    str = "";
    utf_offset = start;
    while ( utf_offset < end ) {
      code2 = ( ( ( code = ( sb.charAt( utf_offset++ ) & 0xff ) ) > 127 ) ? ( sb.charAt( utf_offset++ ) & 0xff ) : 0 );
      code3 = ( code > 223 ) ? ( sb.charAt( utf_offset++ ) & 0xff ) : 0;
      code4 = ( code > 239 ) ? ( sb.charAt( utf_offset++ ) & 0xff ) : 0;
      str += ( ( char ) ( ( code < 128 ) ? code
              : ( code < 224 ) ? ( ( ( code - 192 ) << 6 ) + ( code2 - 128 ) )
              : ( code < 240 ) ? ( ( ( code - 224 ) << 12 ) + ( ( code2 - 128 ) << 6 ) + ( code3 - 128 ) )
              : ( ( ( code - 240 ) << 18 ) + ( ( code2 - 128 ) << 12 ) + ( ( code3 - 128 ) << 6 ) + ( code4 - 128 ) ) ) );
    }
    return str;
  }

  private String toStringFromXmlWellFormed( String string ) {
    for ( int c = 0; c < symbols.length; c++ ) {
      location = string.indexOf( symbols[c], 0 );
      if ( location >= 0 ) {
        string = string.substring( 0, location ).concat( replace[c] ).concat( string.substring( location + symbols[c].length() ) );
        c--;
      }
    }
    return string;
  }
}
