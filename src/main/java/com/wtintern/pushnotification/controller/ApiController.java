package com.wtintern.pushnotification.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wtintern.pushnotification.model.DataFromClient;
import com.wtintern.pushnotification.model.ResponseToClient;
import com.wtintern.pushnotification.service.FcmService;

@RestController
@RequestMapping("/send")
public class ApiController {
	
	private static final String STATUS_SUCCESS = "600";
	private static final String STATUS_DATA_ERROR = "601";
	private static final String STATUS_FILE_ERROR = "602";
	
	@Autowired
	FcmService fcmService;

	@RequestMapping(value = "/single_device", method = RequestMethod.POST)
	public ResponseToClient notifySingleDevice(@RequestParam("data") String data,
			@RequestParam("callback_url") String callbackUrl) {
		// Prepare Response to Client
		ResponseToClient responseToClient = new ResponseToClient();

		// Convert data from client to object
		ObjectMapper mapper = new ObjectMapper();
		DataFromClient dataFromClient;
		try {
			dataFromClient = mapper.readValue(data, DataFromClient.class);
		} catch (Exception e) {
			// If data can't be converted
			e.printStackTrace();
			responseToClient.setStatus(STATUS_DATA_ERROR);

			return responseToClient;
		}
		
		// Send Notification
		fcmService.sendNotificationToSingleDevice(dataFromClient, callbackUrl);
		
		// Set Response for Client
		responseToClient.setStatus(STATUS_SUCCESS);

		return responseToClient;
	}

	@RequestMapping(value = "/multiple_device", method = RequestMethod.POST)
	public ResponseToClient notifyMultipleDevice(@RequestParam("data") String data,
			@RequestParam("to_ids") List<String> toIds, @RequestParam("callback_url") String callbackUrl) {
		// Prepare Response to Client
		ResponseToClient responseToClient = new ResponseToClient();

		// Convert data from client to object
		ObjectMapper mapper = new ObjectMapper();
		DataFromClient dataFromClient;
		try {
			dataFromClient = mapper.readValue(data, DataFromClient.class);
		} catch (Exception e) {
			// If data can't be converted
			e.printStackTrace();
			responseToClient.setStatus(STATUS_DATA_ERROR);

			return responseToClient;
		}
		
		// Send Notification
		fcmService.sendNotificationToMultipleDevice(dataFromClient, toIds, callbackUrl);
		
		// Set response to client
		responseToClient.setStatus(STATUS_SUCCESS);

		return responseToClient;
	}

	@RequestMapping(value = "/formatted_message", method = RequestMethod.POST)
	public ResponseToClient notifyWithFormattedMessage(@RequestParam("data") String data,
			@RequestParam("file") MultipartFile file, @RequestParam("callback_url") String callbackUrl) {
		// Prepare Response to Client
		ResponseToClient responseToClient = new ResponseToClient();

		// Parse File
		CSVParser records;
		try {
			records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(new InputStreamReader(file.getInputStream()));
		} catch (IOException e1) {
			// If File can't be parsed
			e1.printStackTrace();

			responseToClient.setStatus(STATUS_FILE_ERROR);

			return responseToClient;
		}
		
		// Convert data from client to object
		ObjectMapper mapper = new ObjectMapper();
		DataFromClient dataFromClient;
		try {
			dataFromClient = mapper.readValue(data, DataFromClient.class);
		} catch (Exception e) {
			// If data can't be converted
			e.printStackTrace();
			responseToClient.setStatus(STATUS_DATA_ERROR);

			return responseToClient;
		}
		
		// Send Notification
		fcmService.sendNotificationWithFormattedMessage(dataFromClient, records, callbackUrl);
		
		// Set response to client
		responseToClient.setStatus(STATUS_SUCCESS);
		
		return responseToClient;
	}

}
