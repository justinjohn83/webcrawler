package com.gamesalutes.webcrawler.tools.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamesalutes.webcrawler.tools.Link;
import com.gamesalutes.webcrawler.tools.LinkListener;
import com.gamesalutes.webcrawler.tools.model.LinkGraph.LinkModel;

public class LinkModelLinkListener implements LinkListener {

	// map of url's to the node
	private Map<String,LinkModel> nodeMap;
	private LinkGraph linkModel;
	private AtomicInteger counter = new AtomicInteger(1);
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	public synchronized void onBegin(String baseUrl) {
		logger.info("OnBegin...");
		
		nodeMap = new HashMap<String,LinkModel>();
		linkModel = new LinkGraph();
		counter.set(0);
	}

	public synchronized void onLink(Link link) {
		// node exists - let's see if external or internal
		logger.info("onLink: " + link);
		createLinkModel(link);
		
		
	}
	
	private synchronized LinkModel createLinkModel(Link link) {
		
		String toName = link.getTargetName();
		String toValue = link.getTargetUrl().toString();
		String fromName = link.getSourceName();
		String fromValue = link.getSourceUrl() != null ? link.getSourceUrl().toString() : null;
		String key = toValue;
		String name = toName;
		
		LinkModel model;
		model = nodeMap.get(key);
		// create it
		if(model == null) {
			model = new LinkModel().setName(name).setValue(key).setIsExternal(
					Boolean.valueOf(link.isExternal())).setId(String.valueOf(counter.incrementAndGet()));
			nodeMap.put(key, model);
			linkModel.addNode(model);
			logger.info("Created linkModel=" + model);
		}
		LinkModel sourceModel;
		if(!link.isRoot()) {
			sourceModel = nodeMap.get(fromValue);
			if(sourceModel == null) {
				throw new IllegalStateException("sourceModel undefined: link=" + link);
			}
			if(sourceModel != model) {
				sourceModel.addEdge(model);
			}
			else {
				logger.warn("Self-loop attempted: link=" + link);
			}
		}
		else {
			logger.info("Processing root link=" + link);
		}
		
		return  model;
		
	}

	public synchronized void onVisitSuccess(Link link) {
		logger.info("onVisitSuccess:" + link);

		LinkModel model = nodeMap.get(link.getTargetUrl().toString());
		if(model != null) {
			model.setExists(Boolean.TRUE);
		}
		else {
			logger.warn("Model not found for link=" + link);
		}
	}

	public synchronized void onVisitFailed(Link link) {
		logger.info("onVisitFailed:" + link);

		LinkModel model = nodeMap.get(link.getTargetUrl().toString());
		if(model != null) {
			model.setExists(Boolean.FALSE);
		}
		else {
			logger.warn("Model not found for link=" + link);
		}
	}

	public synchronized void onEnd(String baseUrl) {
		logger.info("onEnd: " + baseUrl);
	}

	public LinkGraph getLinkModel() {
		return linkModel;
	}

}
