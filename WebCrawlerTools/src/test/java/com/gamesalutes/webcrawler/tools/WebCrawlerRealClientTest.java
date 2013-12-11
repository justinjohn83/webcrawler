package com.gamesalutes.webcrawler.tools;

import static org.junit.Assert.assertFalse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamesalutes.utils.LoggingUtils;
import com.gamesalutes.webcrawler.tools.model.LinkGraph;
import com.gamesalutes.webcrawler.tools.model.LinkModelLinkListener;

public class WebCrawlerRealClientTest {
	
	private WebCrawlerClient client;

	private static final Logger logger = LoggerFactory.getLogger(WebCrawlerRealClientTest.class);
	
	// write out link graph to json
	public static void main(String [] args) throws Exception {
		
		LoggingUtils.initializeLogging("log4j.properties");
		LinkModelLinkListener linkListener = new LinkModelLinkListener();
		
		WebCrawlerClient client = new WebCrawlerClient();
		client.setLinkParser(new LinkParser());
		client.setConnector(new HttpWebConnector());
		client.setLinkListener(linkListener);
		// set recursion limit to 4
		client.setDepthLimit(4);
		
		client.initialize();
		
		if(args.length < 3) {
			System.err.println("USAGE: <baseurl> <basename> <jsonFile>");
			return;
		}
		try {
			client.execute(args[0], args[1]);
			
			LinkGraph linkGraph = linkListener.getLinkModel();
			
			logger.info("Writing data to json...");
			JsonUtils.toJson(linkGraph, new BufferedWriter(new FileWriter(args[2])));
			logger.info("Data written successfully.");
		}
		finally {
			client.dispose();
		}
	}
	
	
	@Before
	public void before() {
		client = new WebCrawlerClient();
		client.setLinkParser(new LinkParser());
		client.setConnector(new HttpWebConnector());
		client.initialize();
	}
	@Test
	public void testWebsite() throws Exception {
		
		Set<Link> links = client.execute("http://www.gamesalutes.com", "Game Salutes");
		assertFalse(CollectionUtils.isEmpty(links));
		System.out.println("links: " + links.size() + ";links=" + links);
		
	}

}
