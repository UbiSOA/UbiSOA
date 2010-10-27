package net.ubisoa.push;

import net.ubisoa.core.Defaults;

public class Subscription {
	private String topic, callback, token, lease;
	private Boolean verified;
	
	public Subscription(String topic, String callback, String token, Boolean verified) {
		this.topic = topic;
		this.callback = callback;
		this.token = token;
		this.lease = Defaults.getDefaultLeaseDateString();
		this.verified = verified;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public String getCallback() {
		return callback;
	}
	
	public void setCallback(String callback) {
		this.callback = callback;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getLease() {
		return lease;
	}
	
	public void setLease(String lease) {
		this.lease = lease;
	}
	
	public Boolean getVerified() {
		return verified;
	}
	
	public void setVerified(Boolean verified) {
		this.verified = verified;
	}	
}
