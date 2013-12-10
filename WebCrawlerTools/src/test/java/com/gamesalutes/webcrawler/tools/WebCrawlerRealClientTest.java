package com.gamesalutes.webcrawler.tools;

import static org.junit.Assert.assertFalse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import com.gamesalutes.utils.LoggingUtils;
import com.gamesalutes.webcrawler.tools.model.LinkGraph;
import com.gamesalutes.webcrawler.tools.model.LinkModelLinkListener;

public class WebCrawlerRealClientTest {
	
	private WebCrawlerClient client;

	
	// write out link graph to json
	public static void main(String [] args) throws Exception {
		
		LoggingUtils.initializeLogging("log4j.properties");
		LinkModelLinkListener linkListener = new LinkModelLinkListener();
		
		WebCrawlerClient client = new WebCrawlerClient();
		client.setLinkParser(new LinkParser());
		client.setConnector(new HttpWebConnector());
		client.setLinkListener(linkListener);
		
		client.initialize();
		
		if(args.length < 3) {
			System.err.println("USAGE: <baseurl> <basename> <jsonFile>");
			return;
		}
		client.execute(args[0], args[1]);
		
		LinkGraph linkGraph = linkListener.getLinkModel();
		
		JsonUtils.toJson(linkGraph, new BufferedWriter(new FileWriter(args[2])));
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
