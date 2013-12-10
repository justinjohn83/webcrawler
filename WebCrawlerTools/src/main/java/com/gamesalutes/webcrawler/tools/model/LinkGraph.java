package com.gamesalutes.webcrawler.tools.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class LinkGraph {

	private final Map<String,LinkModel> nodes = new HashMap<String,LinkModel>();
	
	public static class LinkModel {
		private String id;
		private String value;
		private String name;
		
		private final Set<String> edges = new HashSet<String>();
		
		private Boolean isExternal;
		private Boolean exists;
		
		public String getValue() {
			return value;
		}
		public LinkModel setValue(String value) {
			this.value = value;
			return this;
		}
		public Boolean getIsExternal() {
			return isExternal;
		}
		public LinkModel setIsExternal(Boolean isExternal) {
			this.isExternal = isExternal;
			return this;
		}
		public Boolean getExists() {
			return exists;
		}
		public LinkModel setExists(Boolean exists) {
			this.exists = exists;
			return this;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("LinkModel [id=");
			builder.append(id);
			builder.append(", value=");
			builder.append(value);
			builder.append(", name=");
			builder.append(name);
			builder.append(", isExternal=");
			builder.append(isExternal);
			builder.append(", exists=");
			builder.append(exists);
			builder.append("]");
			return builder.toString();
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((edges == null) ? 0 : edges.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof LinkModel))
				return false;
			LinkModel other = (LinkModel) obj;
			if (edges == null) {
				if (other.edges != null)
					return false;
			} else if (!edges.equals(other.edges))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
		public String getName() {
			return name;
		}
		public LinkModel setName(String name) {
			this.name = name;
			return this;
		}
		public String getId() {
			return id;
		}
		public LinkModel setId(String id) {
			this.id = id;
			return this;
		}
		public Set<String> getEdges() {
			return edges;
		}
		public void addEdge(LinkModel toNode) {
			// TODO:
			getEdges().add(toNode.getId());
		}
	}

	public Map<String, LinkModel> getNodes() {
		return nodes;
	}
	public void addNode(LinkModel node) {
		// TODO:
		getNodes().put(node.getId(),node);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LinkGraph [nodes=");
		builder.append(nodes);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LinkGraph))
			return false;
		LinkGraph other = (LinkGraph) obj;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;
		return true;
	}

}
