package com.gamesalutes.webcrawler.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class AbstractWebContainerTest {
	
	private AbstractWebConnector webConnector;
	
	@Before
	public void before() {
		webConnector = new AbstractWebConnector() {
			@Override
			protected InputStream doGetResource(String absoluteUrl)
					throws IOException {
				return null;
			}
			
		};
	}
	@Test
	public void testSetBaseUrl() {
		webConnector.setBaseUrl("http://example.com");
	}
	
	@Test
	public void testMatchesBaseUrl() {
		String base = "http://www.example.com";
		webConnector.setBaseUrl(base);
		
		String relative = "/test";
		
		String absoluteUrl = webConnector.getAbsoluteUrl(relative);
		assertEquals(base + relative,absoluteUrl);
		assertTrue(webConnector.matchesBaseUrl(absoluteUrl));
		
		assertNull(webConnector.getAbsoluteUrl("http://www.foreign.com/test"));
		
	}
	
	@Test
	public void testMatchesBaseUrlAbsolute() {
		String base = "http://www.example.com";
		webConnector.setBaseUrl(base);
				
		String absoluteUrl = webConnector.getAbsoluteUrl(base);
		assertEquals(base,absoluteUrl);
		assertTrue(webConnector.matchesBaseUrl(absoluteUrl));
		
	}
	
	@Test
	public void testNotMatchesBaseUrl() {
		String base = "http://www.example.com";
		webConnector.setBaseUrl(base);
		
		String relative = "http://www.foreign.com/test";
		assertNull(webConnector.getAbsoluteUrl(relative));
		assertFalse(webConnector.matchesBaseUrl(relative));
	}

}
