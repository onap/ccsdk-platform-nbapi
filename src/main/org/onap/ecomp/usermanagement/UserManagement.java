/*******************************************************************************
 * =============LICENSE_START=========================================================
 *
 * =================================================================================
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 *******************************************************************************/
package org.onap.ecomp.usermanagement;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
 

 
public class UserManagement {
	
	private static UserManagement client = null;
	final static EELFLogger logger = EELFManager.getInstance().getLogger(UserManagement.class);
	HttpURLConnection connection = null;
	
	public static UserManagement getInstance(){
		if(client == null)
			return new UserManagement();
		else
			return client;
	}
	
	public  JSONObject doGET(String urlString){

		URL url;
		JSONObject returnObj = null;
		try {			
			urlString = getPostGresIP() + urlString;
			url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder check = new StringBuilder(); 
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				check.append(inputLine);
			
			// get the Response code from the HTTP 
			int responseCode = connection.getResponseCode();
			
			
			returnObj = new JSONObject(check.toString());
			returnObj.put("responseCode", responseCode);
			returnObj.put("timestamp", getCurrentDataAndTime());
			
			return returnObj;
			
		} catch (Exception e) {
			logger.error("Some Exception found" + e.getLocalizedMessage());
			returnObj = new JSONObject();
			String responseMsg = "";
			int responseCode;
			try {
				responseMsg = connection.getResponseMessage();
				responseCode = connection.getResponseCode();
			} catch (IOException e1) {
				responseMsg = "Some Exception while retreiving the response";
				responseCode = 500;
			}
			try {
				returnObj.put("responseMsg", responseMsg);
				returnObj.put("responseCode", responseCode);

			} catch (JSONException e1) {
					return null;
			}
			return returnObj;
		}
	}	
	
	public JSONObject doPATCH(String urlString, Map<String, Object> map, String methodName){
		URL url;
		try {
			
			url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("PATCH");
			connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			
			if(methodName.equals("patchNodeInstance")){
				connection.setRequestProperty("Content-Type", "application/json");
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(new JSONObject(map).toString());
				out.close();
				
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder check = new StringBuilder(); 
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				check.append(inputLine);
			
			// get the Response code from the HTTP 
			int responseCode = connection.getResponseCode();
			
			JSONObject returnObj = new JSONObject(check.toString());
			returnObj.put("responseCode", responseCode);
			returnObj.put("timestamp", getCurrentDataAndTime());
			
			return returnObj;
			
		} catch (MalformedURLException e) {
			logger.error("Malformed URL found");
			return null;
		} catch (IOException e) {
			logger.error("IO exception while reading the StringBuilder");
			return null;
		} catch (JSONException e) {
			logger.error("JSON parsing error");
			return null;
		}

		
	}
	
	public JSONObject doPOST(String urlString, JSONObject outputJSON) {
		URL url;
		JSONObject returnObj = null;
		try {			
			urlString = getPostGresIP() + urlString;
			url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			
			
			connection.setRequestProperty("Content-Type", "application/json");
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			out.write(outputJSON.toString());
			out.close();
			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder check = new StringBuilder(); 
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				check.append(inputLine);
			
			// get the Response code from the HTTP 
			int responseCode = connection.getResponseCode();
			
			
			returnObj = new JSONObject(check.toString());
			returnObj.put("responseCode", responseCode);
			returnObj.put("timestamp", getCurrentDataAndTime());
			
			return returnObj;
			
		} catch (Exception e) {
			logger.error("Exception found");
			returnObj = new JSONObject();
			String responseMsg = "";
			int responseCode;
			try {
				responseMsg = connection.getResponseMessage();
				responseCode = connection.getResponseCode();
			} catch (IOException e1) {
				responseMsg = "Some Exception while retreiving the response";
				responseCode = 500;
			}
			try {
				returnObj.put("responseMsg", responseMsg);
				returnObj.put("responseCode", responseCode);
			} catch (JSONException e1) {
					return null;
			}
			return returnObj;
		}

		
	}
	
	public JSONObject doPUT(String urlString,JSONObject outputJson){
		
		URL url;
		JSONObject returnObj = null;
		try {			
			urlString = getPostGresIP() + urlString;
			url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("PUT");
			connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			
			
			if(outputJson != null){
				connection.setRequestProperty("Content-Type", "application/json");
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(outputJson.toString());
				out.close();
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder check = new StringBuilder(); 
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				check.append(inputLine);
			
			// get the Response code from the HTTP 
			int responseCode = connection.getResponseCode();
			
			
			returnObj = new JSONObject(check.toString());
			returnObj.put("responseCode", responseCode);
			returnObj.put("timestamp", getCurrentDataAndTime());
			
			return returnObj;
			
		} catch (Exception e) {
			logger.error("Exception found");
			returnObj = new JSONObject();
			String responseMsg = "";
			int responseCode;
			try {
				responseMsg = connection.getResponseMessage();
				responseCode = connection.getResponseCode();
			} catch (IOException e1) {
				responseMsg = "Some Exception while retreiving the response";
				responseCode = 500;
			}
			try {
				returnObj.put("responseMsg", responseMsg);
				returnObj.put("responseCode", responseCode);
			} catch (JSONException e1) {
					return null;
			}
			return returnObj;
		}
		
	}
	
	public JSONObject doDELETE(String urlString){
		URL url;
		JSONObject returnObj = null;
		try {			
			urlString = getPostGresIP() + urlString;
			url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("DELETE");
			connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder check = new StringBuilder(); 
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				check.append(inputLine);
			
			// get the Response code from the HTTP 
			int responseCode = connection.getResponseCode();
			
			
			returnObj = new JSONObject(check.toString());
			returnObj.put("responseCode", responseCode);
			returnObj.put("timestamp", getCurrentDataAndTime());
			
			return returnObj;
			
		} catch (Exception e) {
			logger.error("Exception found");
			returnObj = new JSONObject();
			String responseMsg = "";
			int responseCode;
			try {
				responseMsg = connection.getResponseMessage();
				responseCode = connection.getResponseCode();
			} catch (IOException e1) {
				responseMsg = "Some Exception while retreiving the response";
				responseCode = 500;
			}
			try {
				returnObj.put("responseMsg", responseMsg);
				returnObj.put("responseCode", responseCode);
			} catch (JSONException e1) {
					return null;
			}
			return returnObj;
		}

	}
	
	private String getCurrentDataAndTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	private String getPostGresIP() {
		String postgres_ip = "";
		String api_version = "";
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("files/configuration.txt")
				.getFile());
		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.split("=")[0].equals("postgres_ip"))
					postgres_ip = line.split("=")[1];
				if (line.split("=")[0].equals("api_version"))
					api_version = line.split("=")[1];
			}
			scanner.close();
		} catch (IOException e) {
			logger.error("Not able to find the manager ip for REST call");
			return null;
		}
		return "http://" + postgres_ip + "/api/" + api_version;
	}

	
		
	}