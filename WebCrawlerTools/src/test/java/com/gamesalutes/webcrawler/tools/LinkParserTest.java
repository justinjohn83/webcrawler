package com.gamesalutes.webcrawler.tools;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class LinkParserTest {
	
	private LinkParser parser;
	
	@Before
	public void before() {
		parser = new LinkParser();
	}
	@Test
	public void testParseSimpleExternalLinks() throws Exception{
		Map<String,String> expected = 
				new HashMap<String,String>();
		expected.put("http://www.external1.com","External 1");
		expected.put("http://www.external2.com","External 2");
		expected.put("http://www.external3.com","External 3");


		Map<String,String> actual = parser.parseLinks(
				this.getClass().getResourceAsStream("externalLinks1.html"));
		
		assertEquals(expected,actual);
	}
}
