package com.wtintern.pushnotification.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class RequestToFcm {
	
	private String to;
	@JsonProperty("registration_ids")
	private List<String> registrationIds;
	private String condition;
	@JsonProperty("collapse_key")
	private String collapseKey;
	private String priority;
	@JsonProperty("content_available")
	private boolean contentAvailable;
	private Map<String, String> data;
	private NotificationPayload notification;
	
	public RequestToFcm() {
	}

	public RequestToFcm(String to, List<String> registrationIds, String condition, String collapseKey, String priority,
			boolean contentAvailable, Map<String, String> data, NotificationPayload notification) {
		this.to = to;
		this.registrationIds = registrationIds;
		this.condition = condition;
		this.collapseKey = collapseKey;
		this.priority = priority;
		this.contentAvailable = contentAvailable;
		this.data = data;
		this.notification = notification;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public List<String> getRegistrationIds() {
		return registrationIds;
	}

	public void setRegistrationIds(List<String> registrationIds) {
		this.registrationIds = registrationIds;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getCollapseKey() {
		return collapseKey;
	}

	public void setCollapseKey(String collapseKey) {
		this.collapseKey = collapseKey;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public boolean isContentAvailable() {
		return contentAvailable;
	}

	public void setContentAvailable(boolean contentAvailable) {
		this.contentAvailable = contentAvailable;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public NotificationPayload getNotification() {
		return notification;
	}

	public void setNotification(NotificationPayload notification) {
		this.notification = notification;
	}
	
}
