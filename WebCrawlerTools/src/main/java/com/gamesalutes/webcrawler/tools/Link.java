package com.gamesalutes.webcrawler.tools;

import java.net.URI;

import com.gamesalutes.utils.WebUtils;

public final class Link {
	
	private final URI sourceUrl;
	private final URI targetUrl;
	private final String sourceName;
	private final String targetName;
	
	public Link(Link base,String targetUrl,String targetName) {
		this.sourceUrl = base.getSourceUrl();
		this.sourceName = base.getSourceName();
		this.targetUrl = WebUtils.createUri(targetUrl);;
		this.targetName = targetName;
	}
	public Link(String sourceUrl,String targetUrl,String sourceName,String targetName) {
		this.sourceUrl = WebUtils.createUri(sourceUrl);
		this.targetUrl = WebUtils.createUri(targetUrl);
		this.sourceName = sourceName;
		this.targetName = targetName;
	}
	
	
	public boolean isExternal() {
		return LinkUtils.resolve(sourceUrl, targetUrl) != null;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sourceUrl == null) ? 0 : sourceUrl.hashCode());
		result = prime * result
				+ ((targetUrl == null) ? 0 : targetUrl.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Link))
			return false;
		Link other = (Link) obj;
		if (sourceUrl == null) {
			if (other.sourceUrl != null)
				return false;
		} else if (!sourceUrl.equals(other.sourceUrl))
			return false;
		if (targetUrl == null) {
			if (other.targetUrl != null)
				return false;
		} else if (!targetUrl.equals(other.targetUrl))
			return false;
		return true;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(sourceName).append(":").append(sourceUrl).append(" -> ").append(targetName).append(":").append(targetUrl);
		
		return builder.toString();
	}
	public URI getSourceUrl() {
		return sourceUrl;
	}
	public URI getTargetUrl() {
		return targetUrl;
	}
	public String getSourceName() {
		return sourceName;
	}
	public String getTargetName() {
		return targetName;
	}
}
