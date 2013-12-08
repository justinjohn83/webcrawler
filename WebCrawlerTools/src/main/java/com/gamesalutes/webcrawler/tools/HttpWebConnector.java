package com.gamesalutes.webcrawler.tools;

import java.io.IOException;
import java.io.InputStream;

import com.gamesalutes.httpconnection.HttpConnection;
import com.gamesalutes.httpconnection.HttpResponse;

public class HttpWebConnector extends AbstractWebConnector {

	private final HttpConnection httpConnection;
	
	private static final int TIMEOUT = 10000;
	private static final int RETRY_COUNT = 3;
	
	public HttpWebConnector() {
		httpConnection = new HttpConnection(RETRY_COUNT,TIMEOUT);
		
	}
	@Override
	protected InputStream doGetResource(String absoluteUrl) throws IOException {
		HttpResponse response = httpConnection.get(absoluteUrl, null, null);
		if(response == null || response.getCode() != 200) {
			throw new IOException("Bad http response: absoluteUrl=" + absoluteUrl + ";response=" + response);
		}
		return response.getInputStream();
	}

}
