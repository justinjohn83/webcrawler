package com.gamesalutes.webcrawler.tools;

public interface LinkListener {

	void onBegin(String baseUrl);
	void onVisited(Link link);
	void onVisitSuccess(Link link);
	void onVisitFailed(Link link);
	void onInternalLink(Link link);
	void onExternalLink(Link link);
	void onEnd(String baseUrl);
}
