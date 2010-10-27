package net.ubisoa.push;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import net.ubisoa.core.Defaults;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

public class HubNotifier implements Runnable {
	private Subscription sub;
	private String content, mediaType;
	private Logger logger;

	public HubNotifier(Subscription sub, String content, String mediaType, Logger logger) {
		this.sub = sub;
		this.content = content;
		this.mediaType = mediaType;
		this.logger = logger;
	}

	@Override
	public void run() {
		try {
			HttpPost post = new HttpPost(sub.getCallback());
			StringEntity postEntity = new StringEntity(content, "UTF-8");
			post.setEntity(postEntity);
			post.setHeader("Content-Type", mediaType);
			Defaults.getHttpClient().execute(post);
			
			logger.info("New topic content was successfully sent to a callback." +
				"\n\tTopic: " + sub.getTopic() + "\n\tCallback: " + sub.getCallback());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
