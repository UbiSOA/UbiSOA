package net.ubisoa.push;

import java.util.Vector;

public final class HubData {
	private static HubData instance;
	private Vector<Topic> topics = new Vector<Topic>(20, 5);
	private Vector<Subscription> subscriptions = new Vector<Subscription>(20, 5);
	
	public synchronized static HubData getInstance() {
		if (instance == null) instance = new HubData();
		return instance;
	}
	
	public Vector<Topic> getTopics() {
		return topics;
	}
	
	public Vector<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void addTopic(Topic topic) {
		topics.add(topic);
	}
	
	public void addSubscription(Subscription subscription) {
		subscriptions.add(subscription);
	}

	public void removeTopic(Topic topic) {
		int index = topics.indexOf(topic);
		topics.remove(index);
	}
	
	public void removeSubscription(Subscription subscription) {
		int index = subscriptions.indexOf(subscription);
		subscriptions.remove(index);
	}
	
	public void removeSubsWithTopic(String topicURL) {
		Vector<Integer> toRemoveSubs = new Vector<Integer>(10, 5);
		for (Subscription sub : subscriptions)
			if (sub.getTopic().equals(topicURL))
				toRemoveSubs.add(subscriptions.indexOf(sub));
		for (int index : toRemoveSubs) subscriptions.remove(index);
	}
	
	public void removeSubsWithTopicAndCallback(String topicURL, String callback) {
		Vector<Integer> toRemoveSubs = new Vector<Integer>(10, 5);
		for (Subscription sub : subscriptions)
			if (sub.getTopic().equals(topicURL) && sub.getCallback().equals(callback))
				toRemoveSubs.add(subscriptions.indexOf(sub));
		for (int index : toRemoveSubs) subscriptions.remove(index);
	}
}
