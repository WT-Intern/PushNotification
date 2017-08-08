package com.wtintern.pushnotification.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class ResponseFromFcm {
	
	@JsonProperty("multicast_id")
	private String multicastId;
	private String success;
	private String failure;
	@JsonProperty("canonical_ids")
	private String canonicalIds;
	private List<FcmResponseResult> results;
	
	public ResponseFromFcm() {
	}

	public ResponseFromFcm(String multicastId, String success, String failure, String canonicalIds,
			List<FcmResponseResult> results) {
		this.multicastId = multicastId;
		this.success = success;
		this.failure = failure;
		this.canonicalIds = canonicalIds;
		this.results = results;
	}

	public String getMulticastId() {
		return multicastId;
	}

	public void setMulticastId(String multicastId) {
		this.multicastId = multicastId;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getFailure() {
		return failure;
	}

	public void setFailure(String failure) {
		this.failure = failure;
	}

	public String getCanonicalIds() {
		return canonicalIds;
	}

	public void setCanonicalIds(String canonicalIds) {
		this.canonicalIds = canonicalIds;
	}

	public List<FcmResponseResult> getResults() {
		return results;
	}

	public void setResults(List<FcmResponseResult> results) {
		this.results = results;
	}
	
}
