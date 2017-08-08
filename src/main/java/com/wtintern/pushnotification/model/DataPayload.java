package com.wtintern.pushnotification.model;

public class DataPayload {
	
	private String content;
	private String title;
	private String icon_url;
	
	public DataPayload() {
	}

	public DataPayload(String content, String title, String icon_url) {
		this.content = content;
		this.title = title;
		this.icon_url = icon_url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon_url() {
		return icon_url;
	}

	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}
	
}
