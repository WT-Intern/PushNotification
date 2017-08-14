package com.wtintern.pushnotification.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.wtintern.pushnotification.model.ResultReport;

public interface ResultReportRepository extends CrudRepository<ResultReport, Long> {
	
	List<ResultReport> findByToId(String toId);
	
}
