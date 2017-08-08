package com.wtintern.pushnotification.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class FcmResponseResult {
	
	@JsonProperty("message_id")
	private String messageId;
	@JsonProperty("registration_id")
	private String registrationId;
	private String error;
	
	public FcmResponseResult() {
	}

	public FcmResponseResult(String messageId, String registrationId, String error) {
		this.messageId = messageId;
		this.registrationId = registrationId;
		this.error = error;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
}
