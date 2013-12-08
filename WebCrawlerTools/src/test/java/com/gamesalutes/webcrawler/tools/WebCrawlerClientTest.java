package com.gamesalutes.webcrawler.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WebCrawlerClientTest {

	private WebCrawlerClient client;
	private Set<Link> visitedLinks;
	private Set<Link> internalLinks;
	private Set<Link> externalLinks;
	private Set<Link> failedLinks;
	
	@After
	public void after() {
		client.dispose();
	}
	@Before
	public void before() {
		client = new WebCrawlerClient();
		client.initialize();
		visitedLinks = Collections.synchronizedSet(new HashSet<Link>());
		internalLinks = Collections.synchronizedSet(new HashSet<Link>());
		externalLinks = Collections.synchronizedSet(new HashSet<Link>());
		failedLinks = Collections.synchronizedSet(new HashSet<Link>());
		
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
				failedLinks.add(link);
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
		
		Map<String,String> resourceLinker = new HashMap<String,String>();
		resourceLinker.put("http://www.page.com", "externalLinks1.html");
		client.setConnector(new StaticResourceWebConnector(resourceLinker));
		
		Set<Link> actual = client.execute("http://www.page.com", "page");
		
		Set<Link> expected = new HashSet<Link>();
		Link home = new Link("http://www.page.com","page");
		
		expected.add(home);
		expected.add(new Link(home,"http://www.external1.com","External 1"));
		expected.add(new Link(home,"http://www.external2.com","External 2"));
		expected.add(new Link(home,"http://www.external3.com","External 3"));
		
		
		assertEquals(new TreeSet<Link>(expected).toString(),new TreeSet<Link>(actual).toString());
		assertEquals(expected,actual);
		
		assertTrue(failedLinks.isEmpty());
		
		// check listener
//		assertTrue(internalLinks.toString(),internalLinks.isEmpty());
		assertEquals(new HashSet<Link>(),internalLinks);
		expected.remove(home);
		assertEquals(expected,externalLinks);


	}
	
	@Test
	public void testMixedRoot() throws Exception {
		Map<String,String> resourceLinker = new HashMap<String,String>();
		resourceLinker.put("http://www.page.com", "mixedRoot.html");
		resourceLinker.put("http://www.page.com/internal1", "internal1.html");
		resourceLinker.put("http://www.page.com/internal2", "internal2.html");
		resourceLinker.put("http://www.page.com/internal1/internal3", "internal3.html");
		
		client.setConnector(new StaticResourceWebConnector(resourceLinker));
		
		Set<Link> actual = client.execute("http://www.page.com", "page");

		assertFalse(actual.isEmpty());
		assertTrue(failedLinks.isEmpty());
		
		Link root = new Link("http://www.page.com","page");
		Link ri1 = new Link(root,"/internal1","Internal 1");
		Link ri2 = new Link(root,"/internal2","Internal 2");
		Link re1 = new Link(root,"http://www.external1.com","External 1");
		Link i1e2 = new Link(ri1,"http://www.external2.com","External 2");
		Link i1e3 = new Link(ri1,"http://www.external3.com","External 3");
		Link i1i3 = new Link(ri1,"/internal1/internal3","Internal 3");
		Link i2e2 = new Link(ri2,"http://www.external4.com","External 4");
		Link i2e3 = new Link(ri2,"http://www.external5.com","External 5");
		Link i3e5 = new Link(i1i3,"http://www.external6.com","External 6");
		
		Set<Link> expectedInternal = new HashSet<Link>(
				Arrays.asList(ri1,ri2,i1i3));
		Set<Link> expectedExternal = new HashSet<Link>(
				Arrays.asList(re1,i1e2,i1e3,i2e2,i2e3,i3e5));
		
		assertEquals(new TreeSet<Link>(expectedInternal),new TreeSet<Link>(internalLinks));
		assertEquals(new TreeSet<Link>(expectedExternal),new TreeSet<Link>(externalLinks));


		
	}

}
