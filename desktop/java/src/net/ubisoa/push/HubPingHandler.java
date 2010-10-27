package net.ubisoa.push;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

import net.ubisoa.core.Defaults;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.restlet.service.TaskService;

public class HubPingHandler implements Runnable {
	private HubData data;
	private String topic;
	private Logger logger;
	private TaskService taskService;
	
	public HubPingHandler(HubData data, String topic, TaskService taskService, Logger logger) {
		this.data = data;
		this.topic = topic;
		this.taskService = taskService;
		this.logger = logger;		
	}
	
	@Override
	public void run() {
		// Checking if there are any info for the topic.
		Topic topicObj = null;
		for (Topic tpc : data.getTopics())
			if (tpc.getTopic().equals(topic))
				topicObj = tpc;
		
		// Adding the topic to the database, if it wasn't registered before.
		if (topicObj == null) {
			topicObj = new Topic(topic);
			data.addTopic(topicObj);
		}
		
		// Updating the last fetched and ping dates.
		topicObj.setLastFetch(Defaults.getDateString());
		topicObj.setLastPing(Defaults.getDateString());
		
		// Fetching the topic's new content.
		String mediaType = null, content = null;
		try {
			HttpResponse response = Defaults.getHttpClient().execute(new HttpGet(topic));
			HttpEntity entity = response.getEntity();
			mediaType = entity.getContentType().getValue();
			mediaType = mediaType.substring(0, mediaType.lastIndexOf(';'));
			content = EntityUtils.toString(entity);
			content.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mediaType == null || content == null) {
			data.removeTopic(topicObj);
			data.removeSubsWithTopic(topic);
			logger.warning("Cannot get the topic's content. The topic was removed.");
			return;
		}
		
		logger.info("There are " + data.getSubscriptions().size() + " subscriptions.");
	
		// Looping through all the subscribers of this topic.
		for (Subscription sub : data.getSubscriptions()) {
			if (!sub.getTopic().equals(topic)) continue;
			if (!sub.getVerified()) if (!verifySubscription(sub)) continue;
			taskService.execute(new HubNotifier(sub, content, mediaType, logger));
		}
		
		logger.info("Handling of new content notification completed.");
	}
	
	private Boolean verifySubscription(Subscription sub) {
		logger.info("Verificating a pending subscription.\n\tTopic: " +
			topic + "\n\tCallback: " + sub.getCallback());
		String challenge = UUID.randomUUID().toString();
		String callbackURL = sub.getCallback() + "?hub.mode=subscribe&hub.topic=" + topic +
			"&hub.challenge=" +	challenge + "&hub.verify_token=" + sub.getToken();

		try {
			// Trying to get an echo of the challenge from the callback.
			HttpGet get = new HttpGet(callbackURL);
			HttpResponse response = Defaults.getHttpClient().execute(get);
			String challengeEcho = EntityUtils.toString(response.getEntity());
			
			// Challenge successful, subscription is verified.
			if (challengeEcho.compareTo(challenge) == 0) {
				sub.setVerified(true);
				logger.info("Subscription was successfully verified.");
				return true;
			}

			// Cannot verify the subscription, removing it from the database.
			else data.removeSubscription(sub);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.info("Subscription wasn't valid.");
		return false;
	}
}
