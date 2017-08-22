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

import java.util.Scanner;

import org.onap.ecomp.main.APIHUtil;

public class GeneratePasswordService {

	public static void main(String args[]) {
		
		Scanner reader = new Scanner(System.in); 
		System.out.println("Enter a username: ");
		String username = reader.nextLine();
		System.out.println("Enter a password: ");
		String password = reader.nextLine();
		
		String encodedUsername = APIHUtil.getIntance().generate(username);
		String encodedPassword = APIHUtil.getIntance().generate(password);
		
		System.out.println("Encoded Username : " + encodedUsername);
		System.out.println("Encoded Password : " +  encodedPassword);
		reader.close();
	
	}

}
