package com.gamesalutes.webcrawler.tools;

import java.net.URI;

import com.gamesalutes.utils.WebUtils;

public class LinkUtils {
	
	public static String resolve(String baseUrl,String url) {
		return resolve(WebUtils.createUri(baseUrl), WebUtils.createUri(url));
	}
	public static String resolve(URI baseUrl,URI inputUri) {
		URI resolved = baseUrl.resolve(inputUri);
		
		String baseUriStr = baseUrl.toString();
		String resolvedStr = resolved.toString();
		
		if(!resolvedStr.startsWith(baseUriStr)) {
			return null;
		}
		
		return resolvedStr;
	}
}
