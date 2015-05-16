package cz.utb.fai.cudaonlineide.client.utils;

/**
 * Browser client-side console class.
 * 
 * @author Belanec
 *
 */
public class COIBrowserConsole {
	  
	  /**
	   * Logging to browser console.
	   * 
	   * @param text Text to write to browser console.
	   */
	  public void log(String text) {
		  _log(text);
	  }
	  
	  /**
	   * Native method which implements logging to browser console.
	   * 
	   * @param text Text to write to browser console.
	   */
	  protected native void _log(String text)
	  /*-{
	      console.log(text);
	  }-*/;
}
