package com.wtintern.pushnotification.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "result_report")
public class ResultReport {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	@Column(name = "to_id")
	private String toId;
	@Column(name = "message_id")
	private String messageId;
	@Column(name = "error")
	private String error;
	@Column(name = "new_to_id")
	private String newToId;
	
	public ResultReport() {
	}
	
	public ResultReport(Long id, String toId, String messageId, String error, String newToId) {
		this.id = id;
		this.toId = toId;
		this.messageId = messageId;
		this.error = error;
		this.newToId = newToId;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getToId() {
		return toId;
	}
	public void setToId(String toId) {
		this.toId = toId;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getNewToId() {
		return newToId;
	}
	public void setNewToId(String newToId) {
		this.newToId = newToId;
	}
		
}
