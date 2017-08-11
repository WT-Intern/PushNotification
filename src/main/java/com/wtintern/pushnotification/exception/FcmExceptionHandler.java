package com.wtintern.pushnotification.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.wtintern.pushnotification.model.FcmResponseResult;

public class FcmExceptionHandler {

private static final Map<Integer,String> CUSTOM_MESSAGE = new HashMap<>();
	
	static{
		CUSTOM_MESSAGE.put(400, "Invalid Parameter");
		CUSTOM_MESSAGE.put(401, "Authentication Error");
		CUSTOM_MESSAGE.put(403, "Forbidden");
		CUSTOM_MESSAGE.put(404, "Not Found");
		CUSTOM_MESSAGE.put(406, "Not Accepted");
		CUSTOM_MESSAGE.put(415, "Unsupported Media Type");
		
		CUSTOM_MESSAGE.put(500, "Internal Server Error");
		CUSTOM_MESSAGE.put(501, "Not Implemented");
		CUSTOM_MESSAGE.put(502, "Bad Gateway");
		CUSTOM_MESSAGE.put(503, "Service Unavaible");
		CUSTOM_MESSAGE.put(504, "Gateway Timeout");
	}
	
	public static List<FcmResponseResult> buildFromException(Exception ex, List<String> toIds) {
		int statusCode = -1;
		List<FcmResponseResult> fcmResponseResults;
		
		if (ex instanceof HttpClientErrorException) {
			statusCode = ((HttpClientErrorException)ex).getRawStatusCode();
		} else if (ex instanceof HttpServerErrorException) {
			statusCode = ((HttpClientErrorException)ex).getRawStatusCode();
		} else {
			fcmResponseResults = buildFcmResponseFromExceptionList(toIds, ex.getMessage());
		}

		String statusMessages = CUSTOM_MESSAGE.containsKey(statusCode) ? CUSTOM_MESSAGE.get(statusCode) : ex.getMessage();
				
		fcmResponseResults = buildFcmResponseFromExceptionList(toIds, statusMessages);
		
		return fcmResponseResults;
	}
	
	private static FcmResponseResult buildFcmResponseFromException(String error) {
		return new FcmResponseResult(null, null, error);
	}
	
	private static List<FcmResponseResult>buildFcmResponseFromExceptionList(List<String> toIds, String error)
	{	
		int size = toIds.size();
		List<FcmResponseResult> fcmResponseResultsList = new ArrayList<FcmResponseResult>(size);
		
		for (int i = 0 ;i < size ; i ++) {
			fcmResponseResultsList.add(buildFcmResponseFromException(error));
		}

		return fcmResponseResultsList;
	}

	public static FcmResponseResult buildFromException(Exception ex) {
		int statusCode = -1;
		FcmResponseResult fcmResponseResult;
		
		if (ex instanceof HttpClientErrorException) {
			statusCode = ((HttpClientErrorException)ex).getRawStatusCode();
		} else if (ex instanceof HttpServerErrorException) {
			statusCode = ((HttpClientErrorException)ex).getRawStatusCode();
		} else {
			fcmResponseResult = buildFcmResponseFromException(ex.getMessage());
		}

		String statusMessages = CUSTOM_MESSAGE.containsKey(statusCode) ? CUSTOM_MESSAGE.get(statusCode) : ex.getMessage();
				
		fcmResponseResult = buildFcmResponseFromException(statusMessages);
		
		return fcmResponseResult;
	}
}
