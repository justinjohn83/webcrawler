package com.gamesalutes.webcrawler.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.gamesalutes.utils.WebUtils;

public abstract class AbstractWebConnector implements WebConnector {

	protected URI baseUrl;
	
	public final InputStream getResource(String url) throws IOException {
		if(url == null) {
			throw new NullPointerException("url");
		}
		if(baseUrl == null) {
			throw new IllegalStateException("baseUrl=null");
		}

		String resolvedUrl = getAbsoluteUrl(url);
		
		if(resolvedUrl == null) {
			throw new IllegalArgumentException("url=" + url + " does not match base=" + baseUrl);
		}
		
		return doGetResource(resolvedUrl);
	}
	
	protected abstract InputStream doGetResource(String absoluteUrl) throws IOException;

	public final void setBaseUrl(String url) {
		if(url == null) {
			throw new NullPointerException("url");
		}
		baseUrl = WebUtils.createUri(url);
	}

	 String getAbsoluteUrl(String url) {
		 
		return LinkUtils.resolve(baseUrl, WebUtils.createUri(url));
	}
	public final boolean matchesBaseUrl(String url) {
		return getAbsoluteUrl(url) != null;
	}

}
