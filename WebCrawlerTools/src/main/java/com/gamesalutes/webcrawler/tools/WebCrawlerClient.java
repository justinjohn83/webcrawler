package com.gamesalutes.webcrawler.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamesalutes.utils.Disposable;
import com.gamesalutes.utils.MiscUtils;

public final class WebCrawlerClient implements Disposable {

	private WebConnector connector;
	private LinkParser linkParser;
	private LinkListener linkListener;
	
	private final Logger logger  = LoggerFactory.getLogger(getClass());
	private int numWorkerThreads = 10;
	private ExecutorService httpService;
	
	public WebCrawlerClient() {
	}
	
	public void initialize() {
		httpService = Executors.newFixedThreadPool(numWorkerThreads);
	}
	
	// TODO: Need to multi-thread the connections for improved performance - 

	private static class LinkData {
		private final Link link;
		private final Map<String,String> pageLinks;
		
		public LinkData(Link link,Map<String,String> pageLinks) {
			this.link = link;
			this.pageLinks = pageLinks;
		}

		public Link getLink() {
			return link;
		}

		public Map<String, String> getPageLinks() {
			return pageLinks;
		}
	}
	
	private Future<LinkData> getFutureLink(final Link link) {
		return httpService.submit(new Callable<LinkData>() {

			public LinkData call() throws Exception {
				
				InputStream htmlStream = null;
				try {
					htmlStream = connector.getResource(link.getTargetUrl().toString());
				

					Map<String,String> pageLinks = 
							linkParser.parseLinks(htmlStream);
					
					return new LinkData(link,pageLinks);
				}
				catch(Exception e) {
					logger.warn("Unable to visit link=" + link,e);
					return new LinkData(link,null);
				}
				finally {
					MiscUtils.closeStream(htmlStream);
				}
			}
			
		});
	}
	public Set<Link> execute(String baseUrl,String baseName) throws IOException {
		
		if(httpService == null) {
			throw new IllegalStateException("initialize not called");
		}
		
		Set<Link> visitedLinks  = Collections.synchronizedSet(new LinkedHashSet<Link>());
		Queue<Future<LinkData>> visitQueue = new ConcurrentLinkedQueue<Future<LinkData>>();
		
		Link baseLink = new Link(baseUrl,baseName);

		connector.setBaseUrl(baseUrl);
		
		visitQueue.offer(getFutureLink(baseLink));
		visitedLinks.add(baseLink);
		
		// BFS
		while(!visitQueue.isEmpty()) {
			Future<LinkData> linkFuture = visitQueue.poll();
			
			// grab resources for this url
			try {
				
				LinkData link = linkFuture.get();
				if(this.linkListener != null) {
					linkListener.onVisited(link.getLink());
				}
					
				// visit was successful we got data back - 200 response code
				if(link.getPageLinks() != null) {
					if(this.linkListener != null) {
						linkListener.onVisitSuccess(link.getLink());
					}
					Map<String,String> pageLinks = link.getPageLinks();
					for(Map.Entry<String, String> E : pageLinks.entrySet()) {
						Link targetLink = new Link(link.getLink(),E.getKey(),E.getValue());
						// make sure haven't already visited
						if(visitedLinks.add(targetLink)) {
							Link baseToTarget = new Link(baseLink,E.getKey(),E.getValue());
							if(!baseToTarget.isExternal()) {
								visitQueue.offer(getFutureLink(targetLink));
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
				else {
					logger.warn("Unable to visit link=" + link + ";baseUrl=" + baseUrl);
					if(this.linkListener != null) {
						this.linkListener.onVisitFailed(link.getLink());
					}
				}
				
			}
			catch(Exception e) {
				logger.error("Unable to visit link : Execution failed",e);
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

	public void dispose() {
		httpService.shutdown();
	}

	public int getNumWorkerThreads() {
		return numWorkerThreads;
	}

	public void setNumWorkerThreads(int numWorkerThreads) {
		this.numWorkerThreads = numWorkerThreads;
	}

}
