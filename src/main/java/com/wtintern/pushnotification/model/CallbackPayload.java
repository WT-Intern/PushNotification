package com.wtintern.pushnotification.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CallbackPayload {
	
	@JsonProperty("to_id")
	private String toId;
	private String status;
	@JsonProperty("new_to_id")
	private String newToId;
	
	public CallbackPayload() {
	}

	public CallbackPayload(String toId, String status, String newToId) {
		this.toId = toId;
		this.status = status;
		this.newToId = newToId;
	}

	public String getToId() {
		return toId;
	}

	public void setToId(String toId) {
		this.toId = toId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNewToId() {
		return newToId;
	}

	public void setNewToId(String newToId) {
		this.newToId = newToId;
	}
}
