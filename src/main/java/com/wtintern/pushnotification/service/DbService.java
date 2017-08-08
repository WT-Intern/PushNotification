package com.wtintern.pushnotification.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wtintern.pushnotification.model.FcmResponseResult;

@Service
public class DbService {
	
	private static final Logger logger = LoggerFactory.getLogger(DbService.class);
	
	public void saveFcmReportToDb(String toId, FcmResponseResult fcmResponseResult) {
		System.out.println(
				"==========================\n" +
				"SAVED TO DB\n" +
				"==========================\n" + 
				"to_id\t\t: " + toId + "\n" +
				"message_id\t: " + fcmResponseResult.getMessageId() + "\n" +
				"error\t\t: " + fcmResponseResult.getError() + "\n" +
				"new_to_id\t: " + fcmResponseResult.getRegistrationId() + "\n" +
				"==========================\n"
				);
		
		logger.info("Report Saved to DB");
	}

	public void saveFcmReportToDb(List<String> toIds, List<FcmResponseResult> fcmResponseResults) {
		
		int currentIndex = 0;
		for(String toId : toIds) {
			saveFcmReportToDb(toId, fcmResponseResults.get(currentIndex));
		}
	}

}
