package com.gamesalutes.webcrawler.tools;

import static org.junit.Assert.assertFalse;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

public class WebCrawlerRealClientTest {
	
	private WebCrawlerClient client;

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
