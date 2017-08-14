package com.wtintern.pushnotification.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wtintern.pushnotification.model.FcmResponseResult;
import com.wtintern.pushnotification.model.ResultReport;
import com.wtintern.pushnotification.repository.ResultReportRepository;

@Service
public class DbService {
	
	private static final Logger logger = LoggerFactory.getLogger(DbService.class);
	
	@Autowired
	ResultReportRepository repository;
	
	public void saveFcmReportToDb(String toId, FcmResponseResult fcmResponseResult) {
		repository.save(buildResultReport(toId, fcmResponseResult));
		
		logger.info("Report Saved to DB");
	}

	public void saveFcmReportToDb(List<String> toIds, List<FcmResponseResult> fcmResponseResults) {
		
		int currentIndex = 0;
		for(String toId : toIds) {
			saveFcmReportToDb(toId, fcmResponseResults.get(currentIndex));
			currentIndex++;
		}
	}
	
	private ResultReport buildResultReport(String toId, FcmResponseResult fcmResponseResult) {
		ResultReport report = new ResultReport();
		report.setToId(toId);
		report.setMessageId(fcmResponseResult.getMessageId());
		report.setError(fcmResponseResult.getError());
		report.setNewToId(fcmResponseResult.getRegistrationId());
		
		return report;
	}
}
