package com.gamesalutes.webcrawler.tools;

public interface LinkListener {

	void onBegin(String baseUrl);
	void onVisited(Link link);
	void onEnd(String baseUrl);
}
