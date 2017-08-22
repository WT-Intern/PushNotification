package com.wtintern.pushnotification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.wtintern.pushnotification.exception.FcmExceptionHandler;
import com.wtintern.pushnotification.model.DataFromClient;
import com.wtintern.pushnotification.model.FcmResponseResult;
import com.wtintern.pushnotification.model.NotificationPayload;
import com.wtintern.pushnotification.model.RequestToFcm;
import com.wtintern.pushnotification.model.ResponseFromFcm;

@Service
public class FcmService {
	
	private static final String URL = "https://fcm.googleapis.com/fcm/send";
	private static final String DEVICE_ID_HEADER = "to_id";
	private static final String TAG_PATTERN = "<([a-zA-Z0-9_]+)>";
	
	private static final String DATA_CLIENT_ATT_TITLE = "title";
	private static final String DATA_CLIENT_ATT_CONTENT = "content";
	
	private static final Logger logger = LoggerFactory.getLogger(FcmService.class);
	
	@Autowired
	DbService dbService;
	@Autowired
	CallbackService cbService;

	@Async
	public void sendNotificationToSingleDevice(DataFromClient dataFromClient, String callbackUrl, String serverKey) {
		FcmResponseResult fcmResponseResult;
		try	{
			// Get Title & Content from Data
			String title = dataFromClient.getData().get(DATA_CLIENT_ATT_TITLE);
			String content = dataFromClient.getData().get(DATA_CLIENT_ATT_CONTENT);		
			
			// Build Request Entity
			HttpEntity<RequestToFcm> requestEntity = buildFcmRequestEntity(serverKey, title, content, dataFromClient.getData(), dataFromClient.getTo(), null);
			
			// Prepare RestTemplate
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			
			// Send request and get response
			ResponseEntity<ResponseFromFcm> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, ResponseFromFcm.class);
			fcmResponseResult = responseEntity.getBody().getResults().get(0);
			
			logger.info("Single Device Notification Sent To Fcm");
		}  catch (Exception ex) {
			fcmResponseResult = FcmExceptionHandler.buildFromException(ex);
		}
		// Save results to DB
		dbService.saveFcmReportToDb(dataFromClient.getTo(), dataFromClient.getData().get(DATA_CLIENT_ATT_CONTENT), fcmResponseResult);
		
		// Send callback if needed
		cbService.sendCallback(callbackUrl, dataFromClient.getTo(), fcmResponseResult);
	}

	@Async
	public void sendNotificationToMultipleDevice(DataFromClient dataFromClient, List<String> toIds, String callbackUrl, String serverKey) {
		
		List<FcmResponseResult> fcmResponseResults = new ArrayList<FcmResponseResult>();
		try	{
			// Get Title & Content from Data
			String title = dataFromClient.getData().get(DATA_CLIENT_ATT_TITLE);
			String content = dataFromClient.getData().get(DATA_CLIENT_ATT_CONTENT);		
	
			// Build Request Entity
			HttpEntity<RequestToFcm> requestEntity = buildFcmRequestEntity(serverKey, title, content, dataFromClient.getData(), null, toIds);
			
			// Prepare RestTemplate
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	
			// Send request and get response
			ResponseEntity<ResponseFromFcm> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, ResponseFromFcm.class);
			fcmResponseResults = responseEntity.getBody().getResults();
			
			logger.info("Multiple Device Notification Sent To Fcm");
		} catch(Exception ex) {
			fcmResponseResults = FcmExceptionHandler.buildFromException(ex, toIds);
		}
		// Save results to DB
		dbService.saveFcmReportToDb(toIds, dataFromClient.getData().get(DATA_CLIENT_ATT_CONTENT), fcmResponseResults);
		
		// Send callback if needed
		cbService.sendCallback(callbackUrl, toIds, fcmResponseResults);
		
	}

	@Async
	public void sendNotificationWithFormattedMessage(DataFromClient dataFromClient, CSVParser records, String callbackUrl, String serverKey) {
		
		// Get Title & Content from Data
		String title = dataFromClient.getData().get(DATA_CLIENT_ATT_TITLE);
		String content = dataFromClient.getData().get(DATA_CLIENT_ATT_CONTENT);

		// Prepare RestTemplate
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		
		// Create List to Track Each toIds & result for report
		List<String> toIds = new ArrayList<String>();
		List<FcmResponseResult> fcmResponseResults = new ArrayList<FcmResponseResult>();
		List<FcmResponseResult> fcmResponseResultsList = new ArrayList<FcmResponseResult>();
		
		// Send Request to FCM for Each Device
		for (CSVRecord record : records) {
			try {
				// Format Content
				String formattedContent = replaceTags(content, TAG_PATTERN, record.toMap());
	
				// Build Request Entity
				HttpEntity<RequestToFcm> requestEntity = buildFcmRequestEntity(serverKey, title, formattedContent, dataFromClient.getData(), record.get(DEVICE_ID_HEADER), null);
				
				// Send request and get response
				ResponseEntity<ResponseFromFcm> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, requestEntity,	ResponseFromFcm.class);
				fcmResponseResults = responseEntity.getBody().getResults();
				
				logger.info("Formatted Message Notification Sent To Fcm");
			} catch (Exception e) {
				fcmResponseResults = FcmExceptionHandler.buildFromException(e, toIds);
			}	
			
			// Save toId and result
			toIds.add(record.get(DEVICE_ID_HEADER));
			fcmResponseResultsList.addAll(fcmResponseResults);
		}
		
		// Save results to DB
		dbService.saveFcmReportToDb(toIds, content, fcmResponseResultsList);
		
		// Send callback if needed
		cbService.sendCallback(callbackUrl, toIds, fcmResponseResultsList);
	}
	
	private String replaceTags(String stringSource, String tagPattern, Map<String, String> replacement) {
		Pattern pattern = Pattern.compile(tagPattern);
		Matcher matcher = pattern.matcher(stringSource);
		StringBuffer result = new StringBuffer();
		
		while (matcher.find()) {
			matcher.appendReplacement(result, replacement.get(matcher.group(1)));
		} 
		matcher.appendTail(result);
		
		return result.toString();
	}
	
	private HttpEntity<RequestToFcm> buildFcmRequestEntity(String serverKey, String title, String content, Map<String, String> dataPayload, String toId, List<String> toIds) {	
		// Create request header
		MultiValueMap<String, String> requestHeader = new LinkedMultiValueMap<String, String>();
		requestHeader.add("Authorization", "key=" + serverKey); // add server key here
		requestHeader.add("Content-Type", "application/json");

		// Create Notification Payload
		NotificationPayload notificationPayload = new NotificationPayload();
		notificationPayload.setTitle(title);
		notificationPayload.setBody(content);

		// Create request body
		RequestToFcm requestBody = new RequestToFcm();
		requestBody.setTo(toId);
		requestBody.setRegistrationIds(toIds);
		requestBody.setNotification(notificationPayload);
		requestBody.setData(dataPayload);

		// Create Request Entity
		HttpEntity<RequestToFcm> requestEntity = new HttpEntity<RequestToFcm>(requestBody, requestHeader);
		
		return requestEntity;
	}
}
