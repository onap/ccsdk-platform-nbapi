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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

public class APIHUtil {

	final static EELFLogger logger = EELFManager.getInstance().getLogger(
			APIHUtil.class);
	private static APIHUtil apihUtil = null;
	private String fileNameToSearch;
	private List<String> result = null;
	private static MessageDigest md;
	
	public String getFileNameToSearch() {
		return fileNameToSearch;
	  }

	  public void setFileNameToSearch(String fileNameToSearch) {
		this.fileNameToSearch = fileNameToSearch;
	  }

	private APIHUtil() {

	}

	public static APIHUtil getIntance() {
		if (apihUtil == null) {
			apihUtil = new APIHUtil();
			return apihUtil;
		} else
			return apihUtil;
	}

	public void unzip(String zipFilePath, String destDir)
			throws IOException {
		File dir = new File(destDir);

		if (!dir.exists())
			dir.mkdirs();
		FileInputStream fis;

		byte[] buffer = new byte[1024];
		fis = new FileInputStream(zipFilePath);
		ZipInputStream zis = new ZipInputStream(fis);
		ZipEntry ze = zis.getNextEntry();
		while (ze != null) {
			String fileName = ze.getName();
			File newFile = new File(destDir + File.separator + fileName);
			logger.info("Unzipping to " + newFile.getAbsolutePath());
			new File(newFile.getParent()).mkdirs();
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			zis.closeEntry();
			ze = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
		fis.close();
	}

	public String searchBlueprint(File fileDirectory) {
		
		result = new ArrayList<String>();
		if (fileDirectory.isDirectory()) {
			System.out.println("Searching directory ... " + fileDirectory.getAbsoluteFile());
				for (File temp : fileDirectory.listFiles()) {
					if (temp.isDirectory()) {
						searchBlueprint(temp);
					} else {
						if (fileNameToSearch.toLowerCase().equals(temp.getName().toLowerCase())) {
							logger.info("Found the file  " + (temp.getAbsoluteFile().toString()));
							result.add(temp.getAbsoluteFile().toString());
						}
					}
				}
		}
		return result.get(0);
	}

	public void deleteDirectory(String outputLocation) {
		File dir = new File(outputLocation);
		for (File file: dir.listFiles()) {
	        if (file.isDirectory()) deleteDirectory(file.getAbsolutePath());
	        file.delete();
	    }
		
	}
	
	public String generate(String pass) {
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] passBytes = pass.getBytes();
			md.reset();
			byte[] digested = md.digest(passBytes);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digested.length; i++) {
				sb.append(Integer.toHexString(0xff & digested[i]));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException ex) {

		}
		return null;

	}
	
}
