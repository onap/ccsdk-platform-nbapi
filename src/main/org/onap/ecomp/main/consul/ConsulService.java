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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onap.ecomp.main.APIHConfig;
import org.onap.ecomp.persistence.APIHDBSource;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;


@Path("/healthservices")
public class ConsulService {

	final static EELFLogger logger = EELFManager.getInstance().getLogger(ConsulService.class);
	
	ConsulHelp consulHelp;

	String errorLine = "Bad Request";

	public ConsulService() {
		consulHelp = new ConsulHelp();

	}

	/**
	 * Return the help for all the available API 
	 * 
	 * @param id
	 * @param authString
	 * @param userid
	 * @return JSONObject
	 */	
	@GET
	@Path("/help")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHelp(@QueryParam("id") String id,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {


		logger.info("Received get help API Request");
		String result = consulHelp.getHelp();
		logger.info("Handled get help API Request");
		return Response.status(200).entity(result)
				.build();
	}
	
	
	@GET
	@Path("/services/{service_name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServiceHealth(@Context HttpServletRequest request,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid,@PathParam("service_name") String service_name) {

		String url = "";
		logger.info("Health request for service with service name  = "
				+ service_name);

		// validate the user and the application for GET request
		if (!APIHConfig.getInstance().validateUser(authString, userid, "GET")) {
			return Response.status(401).entity("Unauthorized").build();
		}

		url = "/health/checks/" + service_name;
		if (request.getQueryString() != null)
			url = "/health/checks/" + service_name + "?"
					+ request.getQueryString();

		JSONObject result = ConsulClient.getInstance().doGET(url);
		logger.info("Handled Health service name API Request");
		return handleResponse(result);

	}
	
	@GET
	@Path("/nodes/{node_name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodeHealth(@Context HttpServletRequest request,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid,@PathParam("node_name") String node_name) {

		String url = "";
		logger.info("Health request for node with node name  = " + node_name);

		// validate the user and the application for GET request
		if (!APIHConfig.getInstance().validateUser(authString, userid, "GET")) {
			return Response.status(401).entity("Unauthorized").build();
		}

		url = "/health/node/" + node_name;
		if (request.getQueryString() != null)
			url = "/health/node/" + node_name + "?" + request.getQueryString();

		JSONObject result = ConsulClient.getInstance().doGET(url);
		logger.info("Handled Health node name API Request");
		return handleResponse(result);

	}
	
	@GET
	@Path("/nodes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodes(@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid,@Context HttpServletRequest request) {

		String url = "";
		logger.info("Health request for all the nodes");
		// validate the user and the application for GET request

		if (!APIHConfig.getInstance().validateUser(authString, userid, "GET")) {
			return Response.status(401).entity("Unauthorized").build();
		}

		url = "/catalog/nodes";
		if (request.getQueryString() != null)
			url = "/catalog/nodes?" + request.getQueryString();

		JSONObject result = ConsulClient.getInstance().doGET(url);
		logger.info("Handled Health for all node  API Request");
		return handleResponse(result);

	}
	
	@GET
	@Path("/services")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServices(@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid,@Context HttpServletRequest request) {

		String url = "";
		logger.info("Health request for all the services");
		// validate the user and the application for GET request

		if (!APIHConfig.getInstance().validateUser(authString, userid, "GET")) {
			return Response.status(401).entity("Unauthorized").build();
		}

		url = "/catalog/services";
		if (request.getQueryString() != null)
			url = "/catalog/services?" + request.getQueryString();

		JSONObject result = ConsulClient.getInstance().doGET(url);
		logger.info("Handled Health for all services  API Request");
		return handleResponse(result);

	}
	
	@GET
	@Path("/datacenters")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDatacenters(@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {

		String url = "";
		logger.info("Health request for datacenters");
		// validate the user and the application for GET request

		if (!APIHConfig.getInstance().validateUser(authString, userid, "GET")) {
			return Response.status(401).entity("Unauthorized").build();
		}

		url = "/catalog/datacenters";

		JSONObject result = ConsulClient.getInstance().doGET(url);
		logger.info("Handled Health for datacenters  API Request");
		return handleResponse(result);

	}
	
	@GET
	@Path("/svchist/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHistoricalData(@PathParam("id") String id,@Context HttpServletRequest request,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {

		if(!APIHConfig.getInstance().validateUser(authString,userid,"PUT")){
			return Response.status(401).entity("Unauthorized").build();
		}
		
		String utc_startDate = "";
		String utc_endDate = "";
		JSONArray resultArray = new JSONArray();
		logger.info("Historical data request received for id " + id);

		if (request.getQueryString() == null)
			return Response.status(400).entity("start date and end date is mandatory").build();

		String[] dates = request.getQueryString().split("&");
		String start_date = "";
		String end_date = "";
		if(dates[0].split("=").length  ==2 && dates[0].split("=")[0].equals("start") && !dates[0].split("=")[1].isEmpty()){
				start_date = dates[0].split("=")[1];
				if (dates[1].split("=").length  ==2 && dates[1].split("=")[0].equals("end") && !dates[1].split("=")[1].isEmpty())
					end_date = dates[1].split("=")[1];
				else
					return Response.status(400).entity("start or end tag/value not found").build();
		}
		else
			return Response.status(400).entity("start or end tag/value not found").build();
		
		try {
			
			logger.info("Health Request received  for start date :" + start_date + " and end date as : " + end_date);
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
			Calendar s = javax.xml.bind.DatatypeConverter.parseDateTime(start_date);
			Calendar e = javax.xml.bind.DatatypeConverter.parseDateTime(end_date);
			Date sDate = s.getTime();
			Date eDate = e.getTime();
			
			
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			utc_startDate = formatter.format(sDate);
			utc_endDate = formatter.format(eDate);
			logger.info("Fetching the health date for start date :" + utc_startDate + " and end date as : " + utc_endDate);
			Connection conn = APIHDBSource.getInstance().getConnection();
			Statement cs = conn.createStatement();
			/**
			String sql = "select date,status,output from healthcheck where id='"
					+ id
					+ "' and date between '"
					+ utc_startDate
					+ "' and '"
					+ utc_endDate + "'";
					*/
			String sql = "select date,status,output from healthcheck h, service s where s.id = h.id " 
					+ " and s.name ='"
					+ id
					+ "' and date between '"
					+ utc_startDate
					+ "' and '"
					+ utc_endDate + "'";
			logger.info("Sending query : " + sql);
			ResultSet rs = cs.executeQuery(sql);
			if (!rs.isBeforeFirst()) {
				return Response
						.status(200)
						//.entity("No health Data found for the selected dates or service")
						.entity(resultArray.toString())
						.build();
			} else {
				while (rs.next()) {
					JSONObject tmp = new JSONObject();
					String returnDate = rs.getString(1).substring(0,19)+ "+0000";
				//	System.out.println("DB return " + returnDate);
					formatter.setTimeZone(TimeZone.getTimeZone(s.getTimeZone().getID()));
					Date ret = formatter.parse(returnDate);
					//System.out.println("After parsing " + ret);
					//System.out.println("After formatting " + formatter.format(ret));

					tmp.put("Date", formatter.format(ret));
					tmp.put("Status", rs.getString(2));
					tmp.put("Output", rs.getString(3));
					resultArray.put(tmp);
				}
				logger.info("Handled historical health date request sucessfully with total records: " + resultArray.length());
				return Response.status(200).entity(resultArray.toString())
						.build();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return Response.status(400)
					.entity("start_date or end_date is not parsable. Please check documentation for correct format")
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500)
					.entity("Error fetching the health historical data")
					.build();
		} 
	}
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerService(InputStream inputStream, @HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {

		logger.info("Register request for a service");
	
		JSONArray services = new JSONArray();
		JSONObject outputJson = new JSONObject();
		
		//validate the user and the application for POST request
			
		if(!APIHConfig.getInstance().validateUser(authString,userid,"PUT")){
			return Response.status(401).entity("Unauthorized").build();
		}
		
		BufferedReader reader =  new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder check = new StringBuilder();
		String inputLine;
		try {
			while ((inputLine = reader.readLine()) != null)
				check.append(inputLine);
			
			JSONObject incoming = new JSONObject(check.toString());
			services = incoming.optJSONArray("services");
			if(services == null)
				return Response.status(400).entity("services[] tag is mandatory").build();
			
			int service_length = services.length();
			if(service_length == 1){
				outputJson = createServiceObject(services.getJSONObject(0));
			}
			else if(service_length > 1){
				for(int i = 0; i< service_length; i++){
					outputJson = createServiceObject(services.getJSONObject(i));
				}
			}
			else{
				return Response.status(400).entity("no service is defined inside the services tag").build();
			}
			
			logger.info("Handled service register Request");
			return handleResponse(outputJson);				
			
		} catch (Exception e) {
			logger.error("some exception" + e.getMessage());
			return Response.status(400).entity("Error parsing the incoming DATA").build();
		}

		

	}
	
	
	private JSONObject createServiceObject(JSONObject serviceObject) throws JSONException {

		String url = "/agent/service/register";
		JSONObject checkObject = new JSONObject();
		JSONArray checks = new JSONArray();
		JSONArray tags = new JSONArray();
		JSONObject outputJson = new JSONObject();
		
		checks = serviceObject.optJSONArray("checks");
		tags = serviceObject.optJSONArray("tags");
		String service_name = serviceObject.optString("name","");
		String service_port = serviceObject.optString("port","");
		String service_address = serviceObject.optString("address","");
		
		if(checks == null || service_port.isEmpty() || service_address.isEmpty() || service_name.isEmpty())
			return new JSONObject("{\"responseCode\":\"400\",\"responseMsg\":\"Required fields : [checks[], port, address, name]\"}");
		
		outputJson.put("Name",service_name);
		outputJson.put("ID",service_name);
		outputJson.put("Port",Integer.parseInt(service_port));
		outputJson.put("Address",service_address);
		outputJson.put("Tags", tags);

		if(checks.length() == 1){
			if(checks.getJSONObject(0).optString("endpoint", "").isEmpty() || checks.getJSONObject(0).optString("interval", "").isEmpty())
				return new JSONObject("{\"responseCode\":\"400\",\"responseMsg\":\"Required fields : [endpoint, interval] in checks\"}");
			
			checkObject.put("HTTP", checks.getJSONObject(0).optString("endpoint", ""));
			checkObject.put("Interval", checks.getJSONObject(0).optString("interval", ""));			
			if(!checks.getJSONObject(0).optString("description", "").isEmpty())
				checkObject.put("Notes", checks.getJSONObject(0).optString("description", ""));
			checkObject.put("ServiceID", service_name);
			
			outputJson.put("Check", checkObject);
		}
		else{
			JSONArray checks_new = new JSONArray();
			for (int i = 0; i < checks.length(); i++) {
				JSONObject o = checks.getJSONObject(i);
				checkObject = new JSONObject();
				if(o.optString("endpoint", "").isEmpty() || o.optString("interval", "").isEmpty())
					return new JSONObject("{\"responseCode\":\"400\",\"responseMsg\":\"Required fields : [endpoint, interval] in checks\"}");
				
				checkObject.put("HTTP", o.optString("endpoint", ""));
				checkObject.put("Interval", o.optString("interval", ""));
				if(!o.optString("description", "").isEmpty())
					checkObject.put("Notes", o.optString("description", ""));
				checkObject.put("ServiceID", service_name);
				checks_new.put(checkObject);
			}
			outputJson.put("Checks", checks_new);
		}
	
		JSONObject result = ConsulClient.getInstance().doPUT(url, outputJson);
		return result;
		
	}

	/**
	 *  De register the service from consul
	 * @param inputStream
	 * @param authString
	 * @param userid
	 * @return
	 */
	@POST
	@Path("/deregister/{serviceID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deregisterService(@PathParam("serviceID") String serviceId,
			@HeaderParam("authorization") String authString,
			@HeaderParam("userid") String userid) {

		String url = "";
		logger.info("Deregister request for a service");
		// validate the user and the application for POST request

		if (!APIHConfig.getInstance().validateUser(authString, userid, "POST")) {
			return Response.status(401).entity("Unauthorized").build();
		}

		url = "/agent/service/deregister/" + serviceId;
		JSONObject result = ConsulClient.getInstance().doPUT(url, null);
		logger.info("Handled service de register Request");
		return handleResponse(result);

	}
	
	private Response handleResponse(JSONObject result) {

		if(result == null){
			return Response.status(500).entity("Internal Server Error – We had a problem with our server. Try again later.").build();
		}
		int responseCode = result.optInt("responseCode");
		String responseMsg = result.optString("responseMsg", "");
		if(responseCode >= 300)
		{
			logger.info("Response code is: " + responseCode + " and Error msg is :" + responseMsg);
		}
		return Response.status(responseCode).entity(responseMsg).build();
	}
}
