package com.tomclaw.xmlgear;

import com.tomclaw.mandarin.core.Handler;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public abstract class XmlSpore extends XmlOutputStream {

  /**
   * Spore mechanism invocation
   */
  public void invoke() {
    try {
      onRun();
    } catch ( Throwable ex ) {
      onError( ex );
    }
    onResult();
  }

  /**
   * Runs first when spore invokes
   * @throws Throwable 
   */
  public abstract void onRun() throws Throwable;

  /**
   * Runs on error in onRun method
   * @param ex 
   */
  public void onError( Throwable ex ) {
    Handler.showError( "IO_EXCEPTION" );
  }

  /**
   * Runs in any case after all.
   */
  public void onResult() {
  }

  /**
   * Returns collected bytes
   * @return 
   */
  public byte[] toByteArray() {
    return super.toByteArray();
  }
}
