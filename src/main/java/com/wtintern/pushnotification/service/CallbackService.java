package com.wtintern.pushnotification.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.wtintern.pushnotification.model.CallbackBody;
import com.wtintern.pushnotification.model.CallbackPayload;
import com.wtintern.pushnotification.model.FcmResponseResult;

@Service
public class CallbackService {
	
	private static final Logger logger = LoggerFactory.getLogger(CallbackService.class);
	
	public void sendCallback(String callbackUrl, List<String> toIds, List<FcmResponseResult> fcmResponseResults) {
		// Build CallbackPayload List from error results
		List<CallbackPayload> callbackPayload = buildCallbackPayloadList(toIds, fcmResponseResults);
		
		// Send Callback if there is any payload to send
		if(!callbackPayload.isEmpty()) {
			// Prepare RestTemplate
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			// Create request header
			MultiValueMap<String, String> requestHeader = new LinkedMultiValueMap<String, String>();
			requestHeader.add("Content-Type", "application/json");
			
			// Create request body
			CallbackBody callbackBody = new CallbackBody();
			callbackBody.setResponse(callbackPayload);
			
			// Create Request Entity
			HttpEntity<CallbackBody> requestEntity = new HttpEntity<CallbackBody>(callbackBody, requestHeader);

			// Send callback
			restTemplate.exchange(callbackUrl, HttpMethod.POST, requestEntity, String.class);
			
			logger.info("Callback with " + callbackPayload.size() + " payload(s) Sent");
		} else {
			logger.info("No Callback Sent");
		}
		
	}
	
	private List<CallbackPayload> buildCallbackPayloadList(List<String> toIds, List<FcmResponseResult> fcmResponseResults) {
		List<CallbackPayload> callbackPayloadList = new ArrayList<CallbackPayload>();
		
		/**
		 *	Convert each error FcmResponseResult to CallbackPayload 
		 */
		int currentIndex = 0;
		for(String toId : toIds) {
			FcmResponseResult fcmResponseResult = fcmResponseResults.get(currentIndex);
			
			if(!(fcmResponseResult.getError() == null)) {
				CallbackPayload callbackPayload = new CallbackPayload();
				callbackPayload.setToId(toId);
				callbackPayload.setStatus(fcmResponseResult.getError());
				callbackPayloadList.add(callbackPayload);
			} else if(!(fcmResponseResult.getRegistrationId() == null)) {
				CallbackPayload callbackPayload = new CallbackPayload();
				callbackPayload.setToId(toId);
				callbackPayload.setStatus("ChangedRegistrationId");
				callbackPayloadList.add(callbackPayload);
			}
			
			currentIndex++;
		}
		
		return callbackPayloadList;
	}
}
