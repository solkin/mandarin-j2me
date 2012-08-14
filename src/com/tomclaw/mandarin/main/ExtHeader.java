package com.tomclaw.mandarin.main;

import com.tomclaw.images.Splitter;
import com.tomclaw.mandarin.core.Settings;
import com.tomclaw.mandarin.molecus.AccountRoot;
import com.tomclaw.tcuilite.Header;
import com.tomclaw.tcuilite.Theme;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class ExtHeader extends Header {

  public int statusHashCode;
  
  public ExtHeader() {
    super( "" );
    statusHashCode = Settings.IMG_STATUS.hashCode();
  }

  public void paint( Graphics g, int paintX, int paintY ) {
    super.paint( g, paintX, paintY );
    /** Painting status icon, time, etc **/
    Splitter.drawImage( g, statusHashCode, AccountRoot.getStatusIndex(), paintX+x+Theme.upSize, paintY +y+ height/2, true);
  }
  
  public int getHeight() {
    height = Splitter.imageMaxSize;
    return height;
  }
}
