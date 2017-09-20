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
package org.onap.ecomp.main.cloudify;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.onap.ecomp.main.APIHConfig;

public class CloudifyClient {

    private static final String MESSAGE = "message";
    private static final String AUTHORIZATION = "Authorization";
    private static final String RESPONSE_MSG = "responseMsg";
    private static final String RESPONSE_CODE = "responseCode";
    private static final String TIME_STAMP = "timestamp";
    private static final String EXCEPTION_FOUND = "Exception found : ";
    private static final String ERROR_RESPONSE_MSG = "Some Exception while retrieving the response ";
    private static final int ERROR_RESPONSE_CODE = 500;

    private static CloudifyClient client = null;
    private static final EELFLogger logger = EELFManager.getInstance().getLogger(CloudifyClient.class);
    private HttpURLConnection connection = null;

    public static CloudifyClient getInstance() {
        if (client == null) {
            return new CloudifyClient();
        } else {
            return client;
        }
    }

    public JSONObject doGET(String urlString) {

        URL url;
        JSONObject returnObj;
        try {
            url = new URL(getManagerID() + urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty(AUTHORIZATION, getAuthString());

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder check = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                check.append(inputLine);
            }

            // get the Response code from the HTTP
            int responseCode = connection.getResponseCode();

            returnObj = new JSONObject();
            returnObj.put(RESPONSE_MSG, check.toString());
            returnObj.put(RESPONSE_CODE, responseCode);
            returnObj.put(TIME_STAMP, getCurrentDataAndTime());
            in.close();
            return returnObj;
        } catch (Exception e) {
            logger.error(EXCEPTION_FOUND + e.getLocalizedMessage(), e);
            return buildReturnObjectWhenExceptionOccurred();
        } finally {
            connection.disconnect();
        }
    }

    public JSONObject doPOST(String urlString, JSONObject outputJSON) {
        URL url;
        JSONObject returnObj;
        try {
            url = new URL(getManagerID() + urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty(AUTHORIZATION, getAuthString());

            connection.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(outputJSON.toString());
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder check = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                check.append(inputLine);
            }

            // get the Response code from the HTTP
            int responseCode = connection.getResponseCode();

            returnObj = new JSONObject();
            returnObj.put(RESPONSE_MSG, check.toString());
            returnObj.put(RESPONSE_CODE, responseCode);
            returnObj.put(TIME_STAMP, getCurrentDataAndTime());
            in.close();

            return returnObj;
        } catch (Exception e) {
            logger.error(EXCEPTION_FOUND + e.getLocalizedMessage());
            return buildReturnObjectWhenExceptionOccurred();
        } finally {
            connection.disconnect();
        }
    }

    public JSONObject doPUT(String urlString, JSONObject outputJson) {

        URL url;
        JSONObject returnObj;
        try {
            url = new URL(getManagerID() + urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty(AUTHORIZATION, getAuthString());

            if (outputJson != null) {
                connection.setRequestProperty("Content-Type",
                    "application/json");
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(outputJson.toString());
                out.close();
            }

            logger.info(connection.getResponseMessage());
            BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
            StringBuilder check = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                check.append(inputLine);
            }

            // get the Response code from the HTTP
            int responseCode = connection.getResponseCode();

            returnObj = new JSONObject();
            returnObj.put(RESPONSE_MSG, check.toString());
            returnObj.put(RESPONSE_CODE, responseCode);
            returnObj.put(TIME_STAMP, getCurrentDataAndTime());
            in.close();

            return returnObj;
        } catch (Exception e) {
            logger.error(EXCEPTION_FOUND + e.getLocalizedMessage());
            return buildReturnObjectWhenExceptionOccurred();
        } finally {
            connection.disconnect();
        }
    }

    public JSONObject doDELETE(String urlString) {
        URL url;
        JSONObject returnObj;
        try {
            url = new URL(getManagerID() + urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty(AUTHORIZATION, getAuthString());

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder check = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                check.append(inputLine);
            }

            // get the Response code from the HTTP
            int responseCode = connection.getResponseCode();

            returnObj = new JSONObject();
            returnObj.put(RESPONSE_MSG, check.toString());
            returnObj.put(RESPONSE_CODE, responseCode);
            returnObj.put(TIME_STAMP, getCurrentDataAndTime());

            in.close();
            return returnObj;
        } catch (Exception e) {
            logger.error(EXCEPTION_FOUND + e.getLocalizedMessage());
            return buildReturnObjectWhenExceptionOccurred();
        } finally {
            connection.disconnect();
        }
    }

    private JSONObject buildReturnObjectWhenExceptionOccurred() {
        JSONObject returnObj = new JSONObject();
        String responseMsg;
        int responseCode;
        try {
            responseMsg = connection.getResponseMessage();
            JSONObject errorSteam = getErrorSteam();
            if (errorSteam != null) {
                responseMsg = errorSteam.optString(MESSAGE);
            }
            responseCode = connection.getResponseCode();
            returnObj.put(RESPONSE_MSG, responseMsg);
            returnObj.put(RESPONSE_CODE, responseCode);
        } catch (Exception e1) {
            responseMsg = ERROR_RESPONSE_MSG;
            responseCode = ERROR_RESPONSE_CODE;
            logger.error(responseMsg + responseCode, e1);
        }
        return returnObj;
    }

    private String getCurrentDataAndTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getManagerID() {
        String managerIp;
        String apiVersion;
        managerIp = APIHConfig.getInstance().getConfigObject().optString("manager_ip");
        apiVersion = APIHConfig.getInstance().getConfigObject().optString("api_version");
        return "http://" + managerIp + "/api/" + apiVersion;
    }

    private String getManagerUsername() {
        String managerUsername;
        managerUsername = APIHConfig.getInstance().getConfigObject().optString("manager_username");
        return managerUsername;
    }

    private String getManagerPassword() {
        String managerPassword;
        managerPassword = APIHConfig.getInstance().getConfigObject().optString("manager_password");
        return managerPassword;
    }

    private String getAuthString() {
        String username = getManagerUsername();
        String password = getManagerPassword();
        String authString = username + ":" + password;
        byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
        String authStringEnc = new String(authEncBytes);
        return "Basic " + authStringEnc;
    }

    private JSONObject getErrorSteam() {
        StringBuilder check = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
            connection.getErrorStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                check.append(inputLine);
            }
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }

        try {
            return new JSONObject(check.toString());
        } catch (JSONException e) {
            return null;
        }
    }
}