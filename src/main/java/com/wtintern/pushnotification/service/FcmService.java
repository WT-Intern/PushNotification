package com.wtintern.pushnotification.service;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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
	private static final String SERVER_KEY = "AAA7ccnQhg:APA91bFn4RYCuFFlXPy8bKOIHdNvEuKXNJdHuxu1AcSNTOAVBV6GsTVUlPJVF3Lt5_wtyGNgQXuXkV84hjEfT7PEXFfownoZVX_Ra63FlPhKZXDuFzXe65N1VLnmwZa6ly-hes9AilM2";
	private static final String DEVICE_ID_HEADER = "to_id";
	private static final String TAG_PATTERN = "<([a-zA-Z0-9_]+)>";
	
	private static final Logger logger = LoggerFactory.getLogger(FcmService.class);
	
	@Autowired
	DbService dbService;
	@Autowired
	CallbackService cbService;

	@Async
	public void sendNotificationToSingleDevice(DataFromClient dataFromClient, String callbackUrl) {
		FcmResponseResult fcmResponseResult;
		try
		{// Prepare RestTemplate
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	
			// Create request header
			MultiValueMap<String, String> requestHeader = new LinkedMultiValueMap<String, String>();
			requestHeader.add("Authorization", "key=" + SERVER_KEY); // add server key here
			requestHeader.add("Content-Type", "application/json");
	
			// Create Notification Payload
			NotificationPayload notificationPayload = new NotificationPayload();
			notificationPayload.setTitle(dataFromClient.getData().getTitle());
			notificationPayload.setBody(dataFromClient.getData().getContent());
	
			// Create request body
			RequestToFcm requestBody = new RequestToFcm();
			requestBody.setTo(dataFromClient.getTo());
			requestBody.setNotification(notificationPayload);
			requestBody.setData(dataFromClient.getData());
	
			// Create Request Entity
			HttpEntity<RequestToFcm> requestEntity = new HttpEntity<RequestToFcm>(requestBody, requestHeader);
	
			// Send request and get response
			ResponseEntity<ResponseFromFcm> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, ResponseFromFcm.class);
			fcmResponseResult = responseEntity.getBody().getResults().get(0);
			
			logger.info("Single Device Notification Sent To Fcm");
		}  catch (Exception ex) {
			fcmResponseResult = FcmExceptionHandler.buildFromException(ex);
		}
		// Save results to DB
		dbService.saveFcmReportToDb(dataFromClient.getTo(), fcmResponseResult);
		
		// Send callback if needed
		cbService.sendCallback(callbackUrl, dataFromClient.getTo(), fcmResponseResult);
	}

	@Async
	public void sendNotificationToMultipleDevice(DataFromClient dataFromClient, List<String> toIds, String callbackUrl) {
		
		List<FcmResponseResult> fcmResponseResults = new ArrayList<FcmResponseResult>();
		try
		{// Prepare RestTemplate
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	
			// Create request header
			MultiValueMap<String, String> requestHeader = new LinkedMultiValueMap<String, String>();
			requestHeader.add("Authorization", "key=" + SERVER_KEY); // add server key here
			requestHeader.add("Content-Type", "application/json");
	
			// Create Notification Payload
			NotificationPayload notificationPayload = new NotificationPayload();
			notificationPayload.setTitle(dataFromClient.getData().getTitle());
			notificationPayload.setBody(dataFromClient.getData().getContent());
	
			// Create request body
			RequestToFcm requestBody = new RequestToFcm();
			requestBody.setRegistrationIds(toIds);
			requestBody.setNotification(notificationPayload);
			requestBody.setData(dataFromClient.getData());
	
			// Create Request Entity
			HttpEntity<RequestToFcm> requestEntity = new HttpEntity<RequestToFcm>(requestBody, requestHeader);
	
			// Send request and get response
			ResponseEntity<ResponseFromFcm> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, ResponseFromFcm.class);
			fcmResponseResults = responseEntity.getBody().getResults();
			
			logger.info("Multiple Device Notification Sent To Fcm");
		} catch(Exception ex) {
			fcmResponseResults = FcmExceptionHandler.buildFromException(ex, toIds);
		}
		// Save results to DB
		dbService.saveFcmReportToDb(toIds,fcmResponseResults);
		
		// Send callback if needed
		cbService.sendCallback(callbackUrl, toIds, fcmResponseResults);
		
	}

	@Async
	public void sendNotificationWithFormattedMessage(DataFromClient dataFromClient, CSVParser records, String callbackUrl) {
		
		// Get Content from Data
		String content = dataFromClient.getData().getContent();

		// Prepare RestTemplate
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		// Create request header
		MultiValueMap<String, String> requestHeader = new LinkedMultiValueMap<String, String>();
		requestHeader.add("Authorization", "key=" + SERVER_KEY); // add server key here
		requestHeader.add("Content-Type", "application/json");
		
		// Create List to Track Each toIds & result for report
		List<String> toIds = new ArrayList<String>();
		List<FcmResponseResult> fcmResponseResults = new ArrayList<FcmResponseResult>();
		List<FcmResponseResult> fcmResponseResultsList = new ArrayList<FcmResponseResult>();
		
		// Send Request to FCM for Each Device
		for (CSVRecord record : records) {
			try{
				// Create Notification Payload
				NotificationPayload notificationPayload = new NotificationPayload();
				notificationPayload.setTitle(dataFromClient.getData().getTitle());
				notificationPayload.setBody(replaceTags(content, TAG_PATTERN, record.toMap()));
				
				// Create request body
				RequestToFcm requestBody = new RequestToFcm();
				requestBody.setTo(record.get(DEVICE_ID_HEADER));
				requestBody.setNotification(notificationPayload);
				requestBody.setData(dataFromClient.getData());
	
				// Create Request Entity
				HttpEntity<RequestToFcm> requestEntity = new HttpEntity<RequestToFcm>(requestBody, requestHeader);
				
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
		dbService.saveFcmReportToDb(toIds, fcmResponseResultsList);
		
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
}
