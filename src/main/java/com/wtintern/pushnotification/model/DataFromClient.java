package com.wtintern.pushnotification.model;

public class DataFromClient {
	
	private DataPayload data;
	private String to;
	
	public DataFromClient() {
	}

	public DataFromClient(DataPayload data, String to) {
		this.data = data;
		this.to = to;
	}

	public DataPayload getData() {
		return data;
	}

	public void setData(DataPayload data) {
		this.data = data;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	
}
