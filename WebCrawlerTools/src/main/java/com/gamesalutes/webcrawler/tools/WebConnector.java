package com.gamesalutes.webcrawler.tools;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for retrieving source page stream from url.
 * 
 * @author jmontgomery
 *
 */
public interface WebConnector {

	/**
	 * Retrieves <code>InputStream</code> from given absolute or relative <code>url</code>.
	 * <code>url</code> must conform to base url established in <code>setBaseUrl</code>.
	 * 
	 * 
	 * @param url input relative or absolute url
	 * @return the <code>InputStream</code> to get resources from given url
	 * @throws IOException if connection exception occurs
	 * @throws IllegalArgumentException if <code>url</code> does not conform to base
	 * @throws IllegalStateException if base url not established
	 */
	InputStream getResource(String url) throws IOException;
	
	/**
	 * Sets the base url for this connector.
	 * 
	 * @param url the url forming the base
	 */
	void setBaseUrl(String url);
	
	/**
	 * Returns whether given url matches the base url set for this connector.
	 * 
	 * @param url the input url
	 * @return <code>true</code> if matches and <code>false</code> otherwise
	 */
	boolean matchesBaseUrl(String url);
	
}
