package com.gamesalutes.webcrawler.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamesalutes.utils.MiscUtils;
import com.gamesalutes.utils.html.HTMLSelector;
import com.gamesalutes.utils.html.TagAttributes;
import com.gamesalutes.utils.html.TagListener;

public final class LinkParser {
	
	private static final int BUFFER_SIZE = 4096;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Parsers through all html links on the given stream.
	 * 
	 * @param htmlStream the stream
	 * @return <code>Set</code> of unique links on this page
	 * 
	 * @throws IOException if stream exception occurs
	 */
	public Map<String,String> parseLinks(InputStream htmlStream) throws IOException {
		
		HTMLSelector parser = new HTMLSelector("a", "href",null,true,BUFFER_SIZE);
		
		final Map<String,String> htmlLinks = new LinkedHashMap<String,String>();
		
		parser.process(htmlStream, new TagListener() {

			String href;
			String linkValue;
			
			public void onStartDocument() {}

			public void onEndDocument() {}

			public TagAttributes onStartTag(String tagName) {
				if("a".equalsIgnoreCase(tagName)) {
					if(logger.isDebugEnabled()) {
						logger.debug("Processing new link...");
					}
					return new TagAttributes(tagName).setAttributes(Arrays.asList("href")).setText(true);
				}
				return null;
			}

			public boolean onEndTag(TagAttributes tagAttributes) {
				// add the info
				if(!MiscUtils.isEmpty(href) && !MiscUtils.isEmpty(linkValue)) {
					if(logger.isDebugEnabled()) {
						logger.debug("link found: " + linkValue + ":" + href);
					}
					htmlLinks.put(href,linkValue);
				}
				else if(logger.isDebugEnabled()) {
					logger.debug("Invalid link: href=" + href + ";linkValue=" + linkValue);
				}
				
				return true;
			}

			public void onText(TagAttributes tagAttributes, String text) {
				if(logger.isDebugEnabled()) {
					logger.debug("onLinkText" + text + " - " + tagAttributes);
				}
				linkValue = StringUtils.trimToNull(text);
			}

			public boolean onAttributes(TagAttributes tagAttributes,
					List<String> attributeValues) {
				if(logger.isDebugEnabled()) {
					logger.debug("onLinkAttributes" + attributeValues + " - " + tagAttributes);
				}
				if(!MiscUtils.isEmpty(attributeValues)) {
					href = attributeValues.get(0);
				}
				
				return true;
			}
			
		});
		
		return htmlLinks;

	}
}
