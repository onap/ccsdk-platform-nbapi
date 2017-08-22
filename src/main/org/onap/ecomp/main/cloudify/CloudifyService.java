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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.ecomp.main.APIHConfig;
import org.onap.ecomp.persistence.EcompBlueprintPersistence;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;



@Path("/")
public class CloudifyService {

	EELFLogger logger = EELFManager.getInstance().getLogger(CloudifyService.class);
	
	CloudifyHelp cloudifyHelp;

	String errorLine = "Bad Request";

	public CloudifyService() throws Exception {
		cloudifyHelp = new CloudifyHelp();
	}
	
	/**
	 * Return the help for all the available API 
	 * 
	 * @param authString
	 * @param userid
	 * @return JSONObject
	 */	
	
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getdefaultHelp(@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {
		return Response.status(200).entity(" API server is Alive!!")
				.build();
	}
	/**
	 * Return the help for all the available API 
	 * 
	 * @param authString
	 * @param userid
	 * @return JSONObject
	 */	
	
	@GET
	@Path("/help")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHelp(@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {

		/*if(!APIHConfig.getInstance().validateUser(authString,userid,"GET")){
			return Response.status(401).entity("Unauthorized").build();
		}*/
		
		String result = cloudifyHelp.getHelp();
		logger.info("Handled get help API Request");
		return Response.status(200).entity(result)
				.build();

	}

	
	/**
	 * Get the list or a specific blueprint which are already uploaded
	 * @param id
	 * @param authString
	 * @param userid
	 * @param request
	 * @return Response
	 */
	
	@GET
	@Path("/blueprints")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBlueprints(@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid,@Context HttpServletRequest request) {

		String url = "";
		//validate the user and the application for GET request
		
		if (request.getQueryString() == null) {
			logger.info("Received request for all blueprints");
			url = "/blueprints";
		} else {
			logger.info("Received request for blueprint with query parameters = " + request.getQueryString());
			url = "/blueprints?"+ request.getQueryString();
		}
		
		if(!APIHConfig.getInstance().validateUser(authString,userid,"GET")){
			return Response.status(401).entity("Unauthorized").build();
		}

		JSONObject result = CloudifyClient.getInstance().doGET(url);
		logger.info("Handled get blueprints API Request");
		return handleResponse(result);

	}

	/**
	 * DELETE a blueprint
	 * 
	 * @param id
	 * @param authString
	 * @param userid
	 * @return JSONObject
	 */
	
	@DELETE
	@Path("/blueprints/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteBlueprints(@PathParam("id") String id,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {

		String url = "";
		logger.info("Received request for deleting blueprint with ID = " + id);
		//validate the user and application for DELETE operation
		if(!APIHConfig.getInstance().validateUser(authString,userid,"DELETE")){
			return Response.status(401).entity("Unauthorized").build();
		}
		
		url = "/blueprints/" + id;
		
		JSONObject result = CloudifyClient.getInstance().doDELETE(url);
		int responseCode = result.optInt("responseCode");
		if(responseCode == 200 ){
			logger.info("Deleting the blueprint from Inventory");
			if(new EcompBlueprintPersistence().deleteBlueprint(id))
				logger.info("Deleted the blueprint from Inventory");
		}
		logger.info("Handled delete blueprint API Request");
		return handleResponse(result);

	}

	/**
	 * Upload a new Blueprint
	 * @param id
	 * @param blueprintfileName
	 * @param zipFileURL
	 * @param authString
	 * @param userid
	 * @return JSONObject
	 */
	
	@POST
	@Path("/blueprints")
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadBlueprints(InputStream inputStream,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {

		String url = "";
		logger.info("Received request for uploading blueprint");

		//validate the user and application for PUT operation
		if(!APIHConfig.getInstance().validateUser(authString,userid,"PUT")){
			return Response.status(401).entity("Unauthorized").build();
		}
								
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder check = new StringBuilder(); 
		String inputLine;
		

		try {
			while ((inputLine = in.readLine()) != null)
				check.append(inputLine);
			
			JSONObject incomingJSON = new JSONObject(check.toString());
			if(incomingJSON.optString("blueprint_id","").equals(""))
				return Response.status(400).entity("blueprint_id is mandatory in payload").build();
			
			url = "/blueprints/" + incomingJSON.optString("blueprint_id","") + "?application_file_name="
					+ incomingJSON.optString("blueprint_filename","") + "&blueprint_archive_url="
					+ incomingJSON.optString("zip_url","");
			
			JSONObject result = CloudifyClient.getInstance().doPUT(url,null);
			logger.info("Handled uploading blueprint API Request");
			int responseCode = result.optInt("responseCode");
			if(responseCode == 201){
				logger.info("Pushing the blueprint in DB");
				new EcompBlueprintPersistence().saveBlueprint(incomingJSON.optString("blueprint_id",""),incomingJSON.optString("blueprint_filename",""),incomingJSON.optString("zip_url",""));
			}
			return handleResponse(result);
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(400).entity("Error parsing the incoming DATA").build();
		}
			
	}

	@GET
	@Path("/viewblueprints")	
	public Response fetchBlueprintByID(@QueryParam("id") String id,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {
		
		// validate the user and application for PUT operation
		if (!APIHConfig.getInstance().validateUser(authString, userid, "PUT")) {
			return Response.status(401).entity("Unauthorized").build();
		}

		if(id == "")
			return Response.status(400).entity("id parameter is must for fetching the blueprint").build();
		logger.info("Fetching the blueprint with id = " + id);
		EcompBlueprintPersistence blueprintPersistence = new EcompBlueprintPersistence();
		try {
			File returnFile = blueprintPersistence.fetchBlueprint(id);
			if (returnFile == null) {
				ResponseBuilder rbuilder = Response.status(Status.OK);
				String logMessage = "No such blueprint found in the inventory.";
				logger.info(logMessage);
				return rbuilder
						.type(MediaType.TEXT_PLAIN)
						.entity("")
						.build();
			} else {
				ResponseBuilder rbuilder = Response.status(Status.OK);
				logger.info("Blueprint found. Returing the yaml file");
				return rbuilder.type(MediaType.APPLICATION_OCTET_STREAM)
						.entity(returnFile).build();
			}
		} catch (Exception E) {
			logger.info("Exception in handling the fetch command =" + E.getMessage());
			return Response.status(500)
					.entity("Error fetching the blueprint resource").build();
		}	
	}
	

	/**
	 * Get the deployment list or specific deployment
	 * @param id
	 * @param authString
	 * @param userid
	 * @return JSONObject
	 */
	
	@GET
	@Path("/deployments")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeployments(@Context HttpServletRequest request,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {
		
		String url = "";
		if (request.getQueryString() == null) {
			logger.info("Received request for all deployments");
			url = "/deployments";
		} else {
			logger.info("Received request for deployment with query = " + request.getQueryString());
			url = "/deployments?"+request.getQueryString();
		}

		if(!APIHConfig.getInstance().validateUser(authString,userid,"GET")){
			return Response.status(401).entity("Unauthorized").build();
		}
		
		JSONObject result = CloudifyClient.getInstance().doGET(url);
		logger.info("Handled get deployment API Request");
		return handleResponse(result);
	}

	/**
	 * DELETE a deployment
	 * @param id
	 * @param authString
	 * @param userid
	 * @return JSONObject
	 */
	
	@DELETE
	@Path("/deployments/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteDeployments(@Context HttpServletRequest request,@PathParam("id") String id,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {

		logger.info("Received request for deleting deployment with ID = " + id);
		
		if(!APIHConfig.getInstance().validateUser(authString,userid,"DELETE")){
			return Response.status(401).entity("Unauthorized").build();
		}
		
		String url = "";
		url = "/deployments/" + id;
		if(request.getQueryString() != null)
			url = "/deployments/" + id + "?" + request.getQueryString();
		
		JSONObject result = CloudifyClient.getInstance().doDELETE(url);
		logger.info("Handled delete deployment API Request");
		return handleResponse(result);
	}

	/**
	 * Create a new deployment
	 * @param inputStream
	 * @param deploymentID
	 * @param authString
	 * @param userid
	 * @return JSONObject
	 */
	@POST
	@Path("/deployments")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createDeployment(InputStream inputStream,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {

		logger.info("Received request for creating deployment");
		
		if(!APIHConfig.getInstance().validateUser(authString,userid,"PUT")){
			return Response.status(401).entity("Unauthorized").build();
		}
		
		String url = "";
		JSONObject inputJSon;
		JSONObject outputJSON;
		
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder check = new StringBuilder(); 
		String inputLine;

		try {
			while ((inputLine = in.readLine()) != null)
				check.append(inputLine);
			inputJSon = new JSONObject(check.toString());

			if(inputJSon.optString("deployment_id", "").equals(""))
				return Response.status(400).entity("deployment_id is mandatory in payload").build();
			String blueprintID = inputJSon.optString("blueprint_id", "");
			JSONObject parameters = inputJSon.optJSONObject("parameters");
			
			outputJSON = new JSONObject();
			outputJSON.put("blueprint_id", blueprintID);
			outputJSON.put("inputs", parameters);
			url = "/deployments/" + inputJSon.optString("deployment_id", "");
			JSONObject result = CloudifyClient.getInstance().doPUT(url,outputJSON);
			logger.info("Handled create deployment API Request");
			return handleResponse(result);
		} catch (Exception e) {
			return Response.status(400).entity("Error parsing the incoming DATA").build();
		} 
	}

	/**
	 * Get the list of execution for a specific deployment
	 * @param deployment_id
	 * @param authString
	 * @param userid
	 * @return
	 */
	@GET
	@Path("/executions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExecutionForDeployment(
			@Context HttpServletRequest request,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {
		
		String url = "";

		if (request.getQueryString() == null) {
			logger.info("Received request for list execution");
			url = "/executions";
		} else {
			logger.info("Received request for list execution with query paramters = " + request.getQueryString());
			url = "/executions?"+request.getQueryString();
		}
		
		if(!APIHConfig.getInstance().validateUser(authString,userid,"GET")){
			return Response.status(401).entity("Unauthorized").build();
		}
		
		JSONObject result = CloudifyClient.getInstance().doGET(url);
		logger.info("Handled get Execution API Request");
		return handleResponse(result);
	}

	@GET
	@Path("/executions/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExecutionWithID(
			@Context HttpServletRequest request,@PathParam("id") String execution_id,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {
		
		String url = "";
		if(request.getQueryString() == null){
			logger.info("Received request for list execution for Execution id as :" + execution_id);
			url = "/executions/"+ execution_id;
		}
		else{
			url = "/executions/"+ execution_id + "?" + request.getQueryString();
			logger.info("Received request for list execution for query paramters = " + request.getQueryString() + " and Execution id as :" + execution_id);
		}

		if(!APIHConfig.getInstance().validateUser(authString,userid,"GET")){
			return Response.status(401).entity("Unauthorized").build();
		}
		
		JSONObject result = CloudifyClient.getInstance().doGET(url);
		logger.info("Handled get specific execution API Request");
		return handleResponse(result);
	}

	/**
	 * Start an execution
	 * @param inputStream
	 * @param authString
	 * @param userid
	 * @return
	 */
	
	@POST
	@Path("/executions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response startExecution(InputStream inputStream,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {

		logger.info("Received request for starting an execution");
		if(!APIHConfig.getInstance().validateUser(authString,userid,"POST")){
			return Response.status(401).entity("Unauthorized").build();
		}
		
		String url = "";
		JSONObject outputJson = new JSONObject();
		
		
		url = "/executions";
		
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder check = new StringBuilder(); 
		String inputLine;

		try {
			while ((inputLine = in.readLine()) != null)
				check.append(inputLine);
			JSONObject inputJSon = new JSONObject(check.toString());
			String deploymentID = inputJSon.optString("deployment_id","");
			String workflow = inputJSon.optString("workflow_name","");
			String customParameter = inputJSon.optString("allow_custom_parameter","false");
			String force = inputJSon.optString("force","false");
			JSONArray parameters = inputJSon.optJSONArray("parameters");
			
			outputJson.put("deployment_id", deploymentID);
			outputJson.put("workflow_id", workflow);
			outputJson.put("allow_custom_parameters", customParameter);
			outputJson.put("force", force);
			outputJson.put("parameters", parameters);
			
			logger.info("output JSON is " + outputJson.toString());
		JSONObject result = CloudifyClient.getInstance().doPOST(url,outputJson);
		logger.info("Handled start execution API Request");
		return handleResponse(result);
		} catch (Exception e) {
			return Response.status(400).entity("Error parsing the incoming DATA").build();
		} 

	}
	
	/**
	 * 
	 * @param execution_id
	 * @param inputStream
	 * @param authString
	 * @param userid
	 * @return
	 */
	
	@DELETE
	@Path("/executions/{execution-id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancelExecution(@PathParam("execution-id") String execution_id,@Context HttpServletRequest request,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {

		logger.info("Received request for cancel execution for Execution id as :" + execution_id);
		
		if(!APIHConfig.getInstance().validateUser(authString,userid,"POST")){
			return Response.status(401).entity("Unauthorized").build();
		}
		
		String url = "";
		JSONObject outputJson = new JSONObject();
		
		url = "/executions/"+ execution_id;
		
		try {
			String[] query = request.getQueryString().split("&");
			String deployment_id = query[0].split("=")[1];
			String action = "cancel";
			if(query.length>1)
				action = query[1].split("=")[1];
			outputJson.put("deployment_id", deployment_id);
			outputJson.put("action", action);
			JSONObject result = CloudifyClient.getInstance().doPOST(url,outputJson);
			logger.info("Handled cancel execution API Request");
			return handleResponse(result);
		}  catch (Exception e) {
			return Response.status(400).entity("Error parsing the incoming DATA").build();
		}
	}
	
	private Response handleResponse(JSONObject result) {

		if(result == null){
			return Response.status(500).entity("Internal Server Error � We had a problem with our server. Try again later.").build();
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
