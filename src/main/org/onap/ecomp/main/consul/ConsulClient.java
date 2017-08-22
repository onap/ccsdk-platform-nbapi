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
	package org.onap.ecomp.main.consul;

	import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.onap.ecomp.main.APIHConfig;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

	public class ConsulClient {

		private static ConsulClient client = null;
		final static EELFLogger logger = EELFManager.getInstance().getLogger(ConsulClient.class);
		HttpURLConnection connection = null;

		public static ConsulClient getInstance() {
			if (client == null)
				return new ConsulClient();
			else
				return client;
		}

		public JSONObject doGET(String urlString) {

			URL url;
			JSONObject returnObj = null;
			try {
				urlString = getManagerID() + urlString;
				url = new URL(urlString);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setConnectTimeout(10000);
				connection.setReadTimeout(10000);

				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				StringBuilder check = new StringBuilder();
				String inputLine;

				while ((inputLine = in.readLine()) != null)
					check.append(inputLine);

				// get the Response code from the HTTP
				int responseCode = connection.getResponseCode();

				returnObj = new JSONObject();
				returnObj.put("responseMsg",check.toString());
				returnObj.put("responseCode", responseCode);
				returnObj.put("timestamp", getCurrentDataAndTime());
				return returnObj;

			} catch (Exception e) {
				logger.error("Exception found : " + e.getLocalizedMessage());
				returnObj = new JSONObject();
				String responseMsg = "";
				int responseCode;
				try {
					responseMsg = connection.getResponseMessage();
					JSONObject errorSteam = getErrorSteam();
					if (errorSteam != null)
						responseMsg = errorSteam.optString("message");
					responseCode = connection.getResponseCode();
					returnObj.put("responseMsg", responseMsg);
					returnObj.put("responseCode", responseCode);
				} catch (Exception e1) {
					responseMsg = "Some Exception while retreiving the response";
					responseCode = 500;
				}
				return returnObj;
			}finally{
				connection.disconnect();
			}
		}

		public JSONObject doPOST(String urlString, JSONObject outputJSON) {
			URL url;
			JSONObject returnObj = null;
			try {
				urlString = getManagerID() + urlString;
				url = new URL(urlString);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setConnectTimeout(10000);
				connection.setReadTimeout(10000);

				connection.setRequestProperty("Content-Type", "application/json");
				OutputStreamWriter out = new OutputStreamWriter(
						connection.getOutputStream());
				out.write(outputJSON.toString());
				out.close();

				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				StringBuilder check = new StringBuilder();
				String inputLine;

				while ((inputLine = in.readLine()) != null)
					check.append(inputLine);

				// get the Response code from the HTTP
				int responseCode = connection.getResponseCode();

				returnObj = new JSONObject();
				returnObj.put("responseMsg",check.toString());
				returnObj.put("responseCode", responseCode);
				returnObj.put("timestamp", getCurrentDataAndTime());

				return returnObj;

			} catch (Exception e) {
				logger.error("Exception found : " + e.getLocalizedMessage());
				returnObj = new JSONObject();
				String responseMsg = "";
				int responseCode;
				try {
					responseMsg = connection.getResponseMessage();
					JSONObject errorSteam = getErrorSteam();
					if (errorSteam != null)
						responseMsg = errorSteam.optString("message");
					responseCode = connection.getResponseCode();
					returnObj.put("responseMsg", responseMsg);
					returnObj.put("responseCode", responseCode);
				} catch (Exception e1) {
					responseMsg = "Some Exception while retreiving the response";
					responseCode = 500;
				}
				return returnObj;
			} finally{
				connection.disconnect();
			}

		}

		public JSONObject doPUT(String urlString, JSONObject outputJson) {

			URL url;
			JSONObject returnObj = null;
			try {
				urlString = getManagerID() + urlString;
				url = new URL(urlString);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("PUT");
				connection.setDoOutput(true);
				connection.setConnectTimeout(10000);
				connection.setReadTimeout(10000);

				if (outputJson != null) {
					connection.setRequestProperty("Content-Type",
							"application/json");
					OutputStreamWriter out = new OutputStreamWriter(
							connection.getOutputStream());
					out.write(outputJson.toString());
					out.close();
				}

				System.out.println(connection.getResponseMessage());
				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				StringBuilder check = new StringBuilder();
				String inputLine;

				while ((inputLine = in.readLine()) != null)
					check.append(inputLine);

				// get the Response code from the HTTP
				int responseCode = connection.getResponseCode();

				returnObj = new JSONObject();
				returnObj.put("responseMsg",check.toString());
				returnObj.put("responseCode", responseCode);
				returnObj.put("timestamp", getCurrentDataAndTime());
				
				return returnObj;

			} catch (Exception e) {
				logger.error("Exception found : " + e.getLocalizedMessage());
				returnObj = new JSONObject();
				String responseMsg = "";
				int responseCode;
				try {
					responseMsg = connection.getResponseMessage();
					JSONObject errorSteam = getErrorSteam();
					if (errorSteam != null)
						responseMsg = errorSteam.optString("message");
					responseCode = connection.getResponseCode();
					returnObj.put("responseMsg", responseMsg);
					returnObj.put("responseCode", responseCode);
				} catch (Exception e1) {
					responseMsg = "Some Exception while retreiving the response";
					responseCode = 500;
				}
				return returnObj;
			}finally{
				connection.disconnect();
			}
		}

		public JSONObject doDELETE(String urlString) {
			URL url;
			JSONObject returnObj = null;
			try {
				urlString = getManagerID() + urlString;
				url = new URL(urlString);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("DELETE");
				connection.setDoOutput(true);
				connection.setConnectTimeout(10000);
				connection.setReadTimeout(10000);

				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				StringBuilder check = new StringBuilder();
				String inputLine;

				while ((inputLine = in.readLine()) != null)
					check.append(inputLine);

				// get the Response code from the HTTP
				int responseCode = connection.getResponseCode();

				returnObj = new JSONObject();
				returnObj.put("responseMsg",check.toString());
				returnObj.put("responseCode", responseCode);
				returnObj.put("timestamp", getCurrentDataAndTime());

				return returnObj;

			} catch (Exception e) {
				logger.error("Exception found : " + e.getLocalizedMessage());
				returnObj = new JSONObject();
				String responseMsg = "";
				int responseCode;
				try {
					responseMsg = connection.getResponseMessage();
					JSONObject errorSteam = getErrorSteam();
					if (errorSteam != null)
						responseMsg = errorSteam.optString("message");
					responseCode = connection.getResponseCode();
					returnObj.put("responseMsg", responseMsg);
					returnObj.put("responseCode", responseCode);
				} catch (Exception e1) {
					responseMsg = "Some Exception while retreiving the response";
					responseCode = 500;
				}
				return returnObj;
			}finally{
				connection.disconnect();
			}
		}

		private String getCurrentDataAndTime() {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			return dateFormat.format(date);
		}

		private String getManagerID() {
			String consul_ip = "";
			String api_version = "";
			String consul_port = "8500";
			
			consul_ip = APIHConfig.getInstance().getConfigObject().optString("consul_ip");
			api_version = APIHConfig.getInstance().getConfigObject().optString("consul_api_version");
			consul_port = APIHConfig.getInstance().getConfigObject().optString("consul_port");
			
			return "http://" + consul_ip + ":" + consul_port+ "/"+ api_version;
		}

		private JSONObject getErrorSteam() {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getErrorStream()));
			StringBuilder check = new StringBuilder();
			try {

				String inputLine;
				while ((inputLine = in.readLine()) != null)
					check.append(inputLine);
			} catch (Exception ex) {

			}

			try {
				return new JSONObject(check.toString());
			} catch (JSONException e) {
				return null;
			}
		}

	}