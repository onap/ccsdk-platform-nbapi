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
package org.onap.ecomp.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.onap.ecomp.main.APIHUtil;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

public class EcompBlueprintPersistence {
	final static EELFLogger logger = EELFManager.getInstance().getLogger(
			EcompBlueprintPersistence.class);
	private APIHDBSource apihDBSource = null;

	public EcompBlueprintPersistence() {
		apihDBSource = APIHDBSource.getInstance();
	}

	public void saveBlueprint(String id, String fileName,String blueprintURL) {

		String outputLocation = "/home/attcloud/apilayer/tmp";
		String zipLocation = "/home/attcloud/apilayer/tmp/" + id + ".zip";

		try{
			
			APIHUtil.getIntance().deleteDirectory(outputLocation);
			logger.info("Starting to download the zip file from the URL :" + blueprintURL);

			URL url = new URL(blueprintURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			InputStream in = connection.getInputStream();
			FileOutputStream out = new FileOutputStream(zipLocation);
			copy(in, out, 1024);
			out.close();

			logger.info("Downloaded the zip file and saved at  :" + zipLocation);
			logger.info("Extracting the zip file");
			APIHUtil.getIntance().unzip(zipLocation, outputLocation + "/" + id);

			EcompBlueprintPersistence blueprintPersistence = new EcompBlueprintPersistence();
			APIHUtil.getIntance().setFileNameToSearch(fileName);
			blueprintPersistence.addBlueprint( id,fileName,APIHUtil.getIntance().searchBlueprint(new File(outputLocation)));
			APIHUtil.getIntance().deleteDirectory(outputLocation);
			logger.info("Clean up done !!!");
		}catch(Exception e){
			logger.error("Some exception while saving the blueprint in Inventory " + e.getMessage());
			APIHUtil.getIntance().deleteDirectory(outputLocation);
		}
		

	}

	public static void copy(InputStream input, OutputStream output,
			int bufferSize) throws IOException {
		byte[] buf = new byte[bufferSize];
		int n = input.read(buf);
		while (n >= 0) {
			output.write(buf, 0, n);
			n = input.read(buf);
		}
		output.flush();
	}

	public boolean addBlueprint(String blueprintID, final String blueprintName,
			String blueprintLocation) throws Exception {

		logger.info("Blueprint location is " + blueprintLocation);
		logger.info("Blueprint id is " + blueprintID);
		logger.info("Blueprint name is " + blueprintName);
		Connection conn = apihDBSource.getConnection();

		System.out.println(conn.isValid(10));
		if(!conn.isValid(10))
			throw new Exception("Connection is not established");
		
		File f = new File(blueprintLocation);
		FileInputStream fis = new FileInputStream(f);
		PreparedStatement ps = conn
				.prepareStatement("INSERT INTO blueprints VALUES (?, ?, ?)");
		ps.setString(1, blueprintID);
		ps.setString(2, blueprintName);
		ps.setBinaryStream(3, fis, f.length());
		ps.executeUpdate();
		ps.close();
		fis.close();
		logger.info("Saved blueprint in Inventory");
		return true;
	}

	public File fetchBlueprint(String id) throws Exception {

		File file = null;
		Connection conn = apihDBSource.getConnection();
		String blueprintName = "";
		Statement cs = conn.createStatement();
		String sql = "SELECT name,blueprint from blueprints where id='" + id + "'";
		ResultSet rs = cs.executeQuery(sql);
		if (!rs.isBeforeFirst()) {
			return null;
		} else {

			byte[] imgBytes = null;
			while (rs.next()) {
				 blueprintName = rs.getString(1);
				imgBytes = rs.getBytes(2);
			}
			file = new File(
					"/home/attcloud/apilayer/tmp/"+ blueprintName);
			if(file.exists())
				file.delete();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(imgBytes);
			fos.flush();
			fos.close();
		}

		rs.close();
		
		return file;

	}

	public boolean deleteBlueprint(String id) {

	try{
		Connection conn = apihDBSource.getConnection();
		Statement cs = conn.createStatement();
		String sql = "DELETE from blueprints where id='" + id + "'";
		cs.execute(sql);
		return true;
		}catch(Exception E){
			E.printStackTrace();
			logger.info("Exception while deleting the blueprint from inventory " + id + ":" + E.getMessage());
			return false;
		}
	
	}
}
