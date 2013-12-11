package com.gamesalutes.webcrawler.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
	private LinkListener linkListener = new DefaultLinkListener();
	
	private final Logger logger  = LoggerFactory.getLogger(getClass());
	private int numWorkerThreads = 10;
	private int depthLimit = -1;
	
	private ExecutorService httpService;
	
	public WebCrawlerClient() {
	}
	
	public void initialize() {
		httpService = Executors.newFixedThreadPool(numWorkerThreads);
	}
	
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
	
	private static class QueueData {
		private final Future<LinkData> linkFuture;
		private final int depth;
		
		public QueueData(Future<LinkData> linkFuture,int depth) {
			this.linkFuture = linkFuture;
			this.depth = depth;
		}

		public Future<LinkData> getLinkFuture() {
			return linkFuture;
		}

		public int getDepth() {
			return depth;
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
		
		Set<URI> visitedUrls  = Collections.synchronizedSet(new LinkedHashSet<URI>());
		Set<Link> visitedLinks = Collections.synchronizedSet(new LinkedHashSet<Link>());
		
		Queue<QueueData> visitQueue = new ConcurrentLinkedQueue<QueueData>();
		
		Link baseLink = new Link(baseUrl,baseName);

		connector.setBaseUrl(baseUrl);
		
		visitQueue.offer(new QueueData(getFutureLink(baseLink),0));
		visitedUrls.add(baseLink.getTargetUrl());
		visitedLinks.add(baseLink);
		
		linkListener.onBegin(baseUrl);
		linkListener.onLink(baseLink);

		// BFS
		while(!visitQueue.isEmpty()) {
			final QueueData queueData = visitQueue.poll();
			if(logger.isDebugEnabled()) {
				logger.debug("Current link depth=" + queueData.getDepth());
			}
			try {
				// grab resources for this url
				final LinkData link = queueData.getLinkFuture().get();
					
				// visit was successful we got data back - 200 response code
				if(link.getPageLinks() != null) {
					linkListener.onVisitSuccess(link.getLink());
					// make sure we haven't exceeded the recursion limit
					if(this.depthLimit < 0 || queueData.getDepth() <= this.depthLimit) {
						Map<String,String> pageLinks = link.getPageLinks();
						for(Map.Entry<String, String> E : pageLinks.entrySet()) {
							Link targetLink = new Link(link.getLink(),E.getKey(),E.getValue());
							// make sure haven't already visited
							if(visitedUrls.add(targetLink.getTargetUrl())) {
								visitedLinks.add(targetLink);
								Link baseToTarget = new Link(baseLink,E.getKey(),E.getValue());
								linkListener.onLink(targetLink);
								// only recurse for internal link
								if(!baseToTarget.isExternal()) {
									visitQueue.offer(new QueueData(getFutureLink(targetLink),queueData.getDepth() + 1));
								}
							}
							
						} // for
					} // if depth limit not exceeded
				}
				// link visit failed
				else {
					logger.warn("Unable to visit link=" + link + ";baseUrl=" + baseUrl);
					this.linkListener.onVisitFailed(link.getLink());
				}
				
			}
			catch(Exception e) {
				logger.error("Unable to visit link : Execution failed",e);
			}
		} // while
		
		this.linkListener.onEnd(baseUrl);
		
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
		if(linkListener == null) {
			linkListener = new DefaultLinkListener();
		}
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

	public int getDepthLimit() {
		return depthLimit;
	}

	public void setDepthLimit(int depthLimit) {
		this.depthLimit = depthLimit;
	}

}
