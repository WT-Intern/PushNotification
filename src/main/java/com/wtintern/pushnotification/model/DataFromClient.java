package com.wtintern.pushnotification.model;

import java.util.Map;

public class DataFromClient {
	
	private Map<String, String> data;
	private String to;
	
	public DataFromClient() {
	}

	public DataFromClient(Map<String, String> data, String to) {
		this.data = data;
		this.to = to;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
}
