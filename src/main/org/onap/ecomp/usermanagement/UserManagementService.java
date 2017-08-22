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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onap.ecomp.main.APIHConfig;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

@Path("usermanagement")
public class UserManagementService {

	final static EELFLogger logger = EELFManager.getInstance().getLogger(UserManagementService.class);
	
	public UserManagementService() {
	}
	
	@GET
	@Path("/roles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRoles(@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid ) {
		logger.info("Received request: getRoles");
		if(!APIHConfig.getInstance().validateUser(authString,userid,"GET")){
			return Response.status(401).entity("Unauthorized").build();
		}
	
		EcompUserManagementDao ecompUserManagementDao = new EcompUserManagementDao();
		List<EcompRole> roles = ecompUserManagementDao.getRoles();
		if (roles == null) {
			return Response.status(500).entity("Internal Server Error").build();
		}

		return Response.ok().entity(roles).build();
	}
	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsers(@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {
		logger.info("Received request: getUsers");
		if(!APIHConfig.getInstance().validateUser(authString,userid,"GET")){
			return Response.status(401).entity("Unauthorized").build();
		}
	
		EcompUserManagementDao ecompUserManagementDao = new EcompUserManagementDao();
		List<EcompUser> users = ecompUserManagementDao.getUsers();
		if (users == null) {
			return Response.status(500).entity("Internal Server Error").build();
		}

		return Response.ok().entity(users).build();
	}
	 @GET 
	 @Path("/user/{uid}") 
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response getUser(@PathParam("uid") String uid,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {
		 logger.info("Received request: getUser:" + uid);
		 if(!APIHConfig.getInstance().validateUser(authString,userid,"GET")){
			 return Response.status(401).entity("Unauthorized").build();
		 }
		
		 EcompUserManagementDao ecompUserManagementDao = new EcompUserManagementDao();
		 List<EcompUser> user = ecompUserManagementDao.getUser(uid);
		 if (user == null) {
			 return Response.status(500).entity("Internal Server Error").build();
		 }

		 return Response.ok().entity(user).build();		 
	 }
	 @GET 
	 @Path("/user/role/{uid}") 
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response getUserRole(@PathParam("uid") String uid,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {
		 logger.info("Received request: getUserRole:" + uid);
		 if(!APIHConfig.getInstance().validateUser(authString,userid,"GET")){
			 return Response.status(401).entity("Unauthorized").build();
		 }
		
		 EcompUserManagementDao ecompUserManagementDao = new EcompUserManagementDao();
		 List<EcompRole> roles = ecompUserManagementDao.getUserRole(uid);
		 if (roles == null) {
			 return Response.status(500).entity("Internal Server Error").build();
		 }

		 return Response.ok().entity(roles).build();
	 }
	 
	 @POST
	 @Path("/users")
	 @Consumes(MediaType.APPLICATION_JSON)
	 public Response addUser(EcompUser[] userInfo,@HeaderParam("authorization") String authString,@HeaderParam("userid") String userid) {
		 logger.info("Received request: addUser");
		 
		 if(!APIHConfig.getInstance().validateUser(authString,userid,"POST")){
			 return Response.status(401).entity("Unauthorized").build();
		 }
	 	
		 EcompUserManagementDao ecompUserManagementDao = new EcompUserManagementDao();
		 int addedUserCnt = ecompUserManagementDao.adduser(userInfo);
		 if (addedUserCnt < 0) {
			 return Response.status(500).entity("Internal Server Error").build();
		 }
		 // return HTTP response 201 in case of success
		 return Response.status(201).entity("User Added:" + addedUserCnt).build();
	 }
}
