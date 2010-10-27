package net.ubisoa.push;

public class Topic {
	private String topic, lastFetch, lastPing;
	
	public Topic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getLastFetch() {
		return lastFetch;
	}

	public void setLastFetch(String lastFetch) {
		this.lastFetch = lastFetch;
	}

	public String getLastPing() {
		return lastPing;
	}

	public void setLastPing(String lastPing) {
		this.lastPing = lastPing;
	}
}
