package com.gamesalutes.webcrawler.tools;

import java.net.URI;

import com.gamesalutes.utils.WebUtils;

public final class Link implements Comparable<Link> {
	
	private final URI sourceUrl;
	private final URI targetUrl;
	private final String sourceName;
	private final String targetName;
	
	public Link(String targetUrl,String targetName) {
		this(null,targetUrl,null,targetName);
	}
	public Link(Link base,String targetUrl,String targetName) {
		if(base.getTargetUrl() != null) {
			this.sourceUrl = base.getTargetUrl();
			this.sourceName = base.getTargetName();
		}
		else {
			this.sourceUrl = null;
			this.sourceName = null;
		}
		this.targetUrl = WebUtils.createUri(targetUrl);;
		this.targetName = targetName;
	}
	public Link(String sourceUrl,String targetUrl,String sourceName,String targetName) {
		if(sourceUrl != null) {
			this.sourceUrl = WebUtils.createUri(sourceUrl);
			this.sourceName = sourceName;
		}
		else {
			this.sourceUrl = null;
			this.sourceName = null;
		}
		this.targetUrl = WebUtils.createUri(targetUrl);
		this.targetName = targetName;
	}
	
	
	public boolean isExternal() {
		// single root entry is internal by definition
		if(sourceUrl == null) {
			return false;
		}
		return LinkUtils.resolve(sourceUrl, targetUrl) == null;
	}
	public boolean isInternal() {
		return !isRoot() && !isExternal();
	}
	public boolean isRoot() {
		return sourceUrl == null;
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
		if(sourceUrl != null) {
			builder.append(sourceName).append(":").append(sourceUrl).append(" -> ");
		}
		builder.append(targetName).append(":").append(targetUrl);
		
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
	public int compareTo(Link other) {
		int result = 0;
		if(sourceUrl != null && other.sourceUrl != null) {
			result = sourceUrl.compareTo(other.sourceUrl);
		}
		else if(sourceUrl == null) {
			return -1;
		}
		else if(other.sourceUrl == null) {
			return 1;
		}
		
		if(result == 0) {
			if(targetUrl != null && other.targetUrl != null) {
				result = targetUrl.compareTo(other.targetUrl);
			}
			else if(targetUrl == null) {
				return -1;
			}
			else if(other.targetUrl == null) {
				return 1;
			}
		}
		
		return result;
	}
}
