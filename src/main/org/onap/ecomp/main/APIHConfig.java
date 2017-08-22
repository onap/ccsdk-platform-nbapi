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
package org.onap.ecomp.main;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

public class APIHConfig {
	final static EELFLogger logger = EELFManager.getInstance().getLogger(APIHConfig.class);
	private static JSONObject configObject = new JSONObject();
	private static APIHConfig apihConfigObj = null;
	
	
	public APIHConfig () throws Exception {
		configObject = readConfiguration();
	}
	
	public static APIHConfig getInstance() {	
		
		if (apihConfigObj == null) {
			try {
				apihConfigObj = new APIHConfig();
			} catch (Exception e) {
				StringWriter stack = new StringWriter();
				e.printStackTrace(new PrintWriter(stack));
				logger.info(stack.toString());
			}
		}
				
		return apihConfigObj;
	}
	
	public JSONObject getConfigObject() {
		return configObject;
	}
	
	private JSONObject readConfiguration() throws Exception{
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("configuration.txt").getFile());
		StringBuilder configString = new StringBuilder();
		Scanner scanner = new Scanner(file);

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			configString.append(line);
		}
			
		JSONObject configObject = new JSONObject(configString.toString());
		scanner.close();
		return configObject;
	}
	
	 public boolean validateUser(String authString, String userId, String function){
		 
		 if(!configObject.optBoolean("enableAuthetication")){
			 logger.info("Authentication is disabled. Continuing ...");
			 return true;
		 }		
		
		 if(authString ==null || authString.isEmpty()){
			 logger.info("Authentication data missing. Not Authorized");
			 return false;
		 }
		
		 String decodedAuth = "";
		 String[] authParts = authString.split("\\s+");

		 String authInfo = authParts[1];
		 byte[] bytes = null;
		 bytes = DatatypeConverter.parseBase64Binary(authInfo);
			
		 decodedAuth = new String(bytes,StandardCharsets.UTF_8);
		 String[] authen = decodedAuth.split(":");
		 
		 
		 // APIHUtil.getIntance().generate() use this function once the encrytion to work
		 if (authen.length > 1 && configObject.optBoolean("enableAuthetication") && authen[0].equals(configObject.optString("mechid"))
					&& authen[1].equals(configObject.optString("pass"))) {
			 logger.info("Application is authenticated sucessfully");
			 return true;
		 } else {
			 logger.info("Application authentication Failed!!");
			 return false;
		 }
	 }
}


