package com.gamesalutes.webcrawler.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class StaticResourceWebConnector extends AbstractWebConnector {

	private final Map<String,String> resourceMap;
	
	public StaticResourceWebConnector(Map<String,String> resourceMap) {
		this.resourceMap = resourceMap;
	}
	@Override
	protected InputStream doGetResource(String absoluteUrl) throws IOException {
		if(!resourceMap.containsKey(absoluteUrl)) {
			throw new IOException("absoluteUrl=" + absoluteUrl);
		}
		InputStream stream = this.getResource(resourceMap.get(absoluteUrl));
		
		if(stream == null) {
			throw new IOException("No stream for resource=" + resourceMap.get(absoluteUrl));
		}
		
		return stream;
	}

}
