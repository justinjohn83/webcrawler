package com.gamesalutes.webcrawler.tools;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class WebCrawlerClientTest {

	private WebCrawlerClient client;
	private Set<Link> visitedLinks;
	private Set<Link> internalLinks;
	private Set<Link> externalLinks;
	
	@Before
	public void before() {
		client = new WebCrawlerClient();
		visitedLinks = new HashSet<Link>();
		internalLinks = new HashSet<Link>();
		externalLinks = new HashSet<Link>();
		
		Map<String,String> resourceLinker = new HashMap<String,String>();
		resourceLinker.put("http://www.page.com", "externalLinks1.html");
		client.setConnector(new StaticResourceWebConnector(resourceLinker));
		client.setLinkParser(new LinkParser());
		client.setLinkListener(new LinkListener() {

			public void onBegin(String baseUrl) {
				// TODO Auto-generated method stub
				
			}

			public void onVisited(Link link) {
				visitedLinks.add(link);
			}

			public void onVisitSuccess(Link link) {
			}

			public void onVisitFailed(Link link) {
				// TODO Auto-generated method stub
				
			}

			public void onEnd(String baseUrl) {
				// TODO Auto-generated method stub
				
			}

			public void onExternalLink(Link link) {
				externalLinks.add(link);
			}

			public void onInternalLink(Link link) {
				internalLinks.add(link);
			}
			
		});
		
	}
	@Test
	public void testAllExternalLinks() throws Exception {
		Set<Link> actual = client.execute("http://www.page.com", "page");
		
		Set<Link> expected = new HashSet<Link>();
		Link home = new Link("http://www.page.com","page");
		
		expected.add(home);
		expected.add(new Link(home,"http://www.external1.com","External 1"));
		expected.add(new Link(home,"http://www.external2.com","External 2"));
		expected.add(new Link(home,"http://www.external3.com","External 3"));
		
		
		assertEquals(new TreeSet<Link>(expected).toString(),new TreeSet<Link>(actual).toString());
		assertEquals(expected,actual);
		
		// check listener
//		assertTrue(internalLinks.toString(),internalLinks.isEmpty());
		assertEquals(new HashSet<Link>(),internalLinks);
		expected.remove(home);
		assertEquals(expected,externalLinks);


	}

}
