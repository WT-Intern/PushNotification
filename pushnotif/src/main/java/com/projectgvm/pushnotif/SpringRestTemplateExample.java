package com.projectgvm.pushnotif;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.RestTemplate;

import com.projectgvm.pushnotif.Model.FcmResponse;
import com.projectgvm.pushnotif.Model.Result;

@SpringBootApplication
public class SpringRestTemplateExample {

	public static void main(String[] args) throws JSONException {
		
		RestTemplate restTemplate = new RestTemplate();
		String fooResourceUrl = "https://fcm.googleapis.com/fcm/send";
//		ArrayList<Result> rs = new ArrayList<Result>();
//		ResponseEntity<FcmResponse> response = restTemplate.getForEntity(fooResourceUrl + "/1", FcmResponse.class);
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization","key=AIzaSyD_fj_kAvyL8v7zG50JqE9SGbNnjmD6iLE");
		headers.set("Content-Type", "application/json");
	
		HttpEntity<String> request = new HttpEntity<String>("{\"registration_ids\":[\"ABC\"]}", headers);
		ResponseEntity<FcmResponse> fcmResponseEntity = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, request, FcmResponse.class);
		System.out.println(fcmResponseEntity.getBody());
		
		
			

	}	
	
}
