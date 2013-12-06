package com.gamesalutes.webcrawler.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamesalutes.utils.MiscUtils;

public final class WebCrawlerClient {

	private WebConnector connector;
	private LinkParser linkParser;
	private LinkListener linkListener;
	
	private final Logger logger  = LoggerFactory.getLogger(getClass());
	
	
	public WebCrawlerClient() {
	}
	
	public Set<Link> execute(String baseUrl,String baseName) throws IOException {
		
		Set<Link> visitedLinks  = new LinkedHashSet<Link>();
		Queue<Link> visitQueue = new ArrayDeque<Link>();
		
		Link baseLink = new Link(baseUrl,baseName);
		
		visitQueue.offer(baseLink);
		visitedLinks.add(baseLink);
		connector.setBaseUrl(baseUrl);
		
		// BFS
		while(!visitQueue.isEmpty()) {
			Link link = visitQueue.poll();
			
			// grab resources for this url
			try {
				if(this.linkListener != null) {
					linkListener.onVisited(link);
				}
				InputStream htmlStream = connector.getResource(link.getTargetUrl().toString());
				try {
					// visit was successful we got data back - 200 response code
					if(this.linkListener != null) {
						linkListener.onVisitSuccess(link);
					}
					Map<String,String> pageLinks = 
							linkParser.parseLinks(htmlStream);
					for(Map.Entry<String, String> E : pageLinks.entrySet()) {
						Link targetLink = new Link(link,E.getKey(),E.getValue());
						// make sure haven't already visited
						if(visitedLinks.add(targetLink)) {
							Link baseToTarget = new Link(baseLink,E.getKey(),E.getValue());
							if(!baseToTarget.isExternal()) {
								visitQueue.offer(targetLink);
								if(this.linkListener != null) {
									linkListener.onInternalLink(targetLink);
								}
							}
							// external link - stop the recursion but note the link
							else {
								if(this.linkListener != null) {
									linkListener.onExternalLink(targetLink);
								}
							}
						}
						
					}
				}
				finally {
					MiscUtils.closeStream(htmlStream);
				}
				
			}
			catch(IOException e) {
				logger.warn("Unable to visit link=" + link + ";baseUrl=" + baseUrl,e);
				if(this.linkListener != null) {
					this.linkListener.onVisitFailed(link);
				}
			}
		}
		
		return visitedLinks;
	}
	
	


	public WebConnector getConnector() {
		return connector;
	}


	public void setConnector(WebConnector connector) {
		this.connector = connector;
	}

	public LinkParser getLinkParser() {
		return linkParser;
	}

	public void setLinkParser(LinkParser linkParser) {
		this.linkParser = linkParser;
	}

	public LinkListener getLinkListener() {
		return linkListener;
	}

	public void setLinkListener(LinkListener linkListener) {
		this.linkListener = linkListener;
	}

}
