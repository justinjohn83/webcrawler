package com.gamesalutes.webcrawler.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LinkTest {

	@Test
	public void testSingleLink() {
		Link l = new Link("http://www.example.com","example");
		assertEquals("http://www.example.com",l.getTargetUrl().toString());
		assertEquals("example",l.getTargetName());
		assertNull(l.getSourceUrl());
		assertNull(l.getSourceName());
		assertFalse(l.isExternal());

	}
	
	@Test
	public void testInternalLinkRelative() {
		Link l = new Link("http://www.example.com","/test","example","example test");
		assertEquals("http://www.example.com",l.getSourceUrl().toString());
		assertEquals("example",l.getSourceName());
		assertEquals("/test",l.getTargetUrl().toString());
		assertEquals("example test",l.getTargetName());
		assertFalse(l.isExternal());
	}
	
	@Test
	public void testInternalLinkAbsolute() {
		Link l = new Link("http://www.example.com","http://www.example.com/test","example","example test");
		assertFalse(l.isExternal());
	}
	
	@Test
	public void testExternalLink() {
		Link l = new Link("http://www.example.com","http://www.foo.com/test","example","foo test");
		assertTrue(l.isExternal());
	}
}
