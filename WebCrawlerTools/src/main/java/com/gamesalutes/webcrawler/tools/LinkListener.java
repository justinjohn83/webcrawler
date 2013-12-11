package com.gamesalutes.webcrawler.tools;

public interface LinkListener {

	void onBegin(String baseUrl);
	void onLink(Link link);
	void onVisitSuccess(Link link);
	void onVisitFailed(Link link);
	void onEnd(String baseUrl);
}
