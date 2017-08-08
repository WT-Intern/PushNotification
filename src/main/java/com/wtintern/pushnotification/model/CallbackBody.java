package com.wtintern.pushnotification.model;

import java.util.List;

public class CallbackBody {
	
	private List<CallbackPayload> response;

	public CallbackBody() {
	}

	public CallbackBody(List<CallbackPayload> response) {
		this.response = response;
	}

	public List<CallbackPayload> getResponse() {
		return response;
	}

	public void setResponse(List<CallbackPayload> response) {
		this.response = response;
	}
}
