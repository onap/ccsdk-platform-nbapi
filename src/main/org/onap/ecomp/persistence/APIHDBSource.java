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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;
import org.onap.ecomp.main.APIHConfig;
import org.postgresql.ds.PGPoolingDataSource;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

public class APIHDBSource {
	final static EELFLogger logger = EELFManager.getInstance().getLogger(APIHDBSource.class);
    private static final String PG_DRIVER = "org.postgresql.Driver";
    private static JSONObject configObject = new JSONObject();
    private static PGPoolingDataSource dbsource = null;
    private static APIHDBSource apihDBSource = null;
  
    public APIHDBSource() {
		try {
			Class.forName(PG_DRIVER);
			configObject = APIHConfig.getInstance().getConfigObject();
			
			dbsource = new PGPoolingDataSource();
			dbsource.setDataSourceName("postgresql");
			dbsource.setServerName(configObject.optString("postgres_ip"));
			dbsource.setPortNumber(configObject.optInt("postgres_port"));
			dbsource.setDatabaseName(configObject.optString("postgres_db_name"));
			dbsource.setUser(configObject.optString("postgres_db_user"));
			dbsource.setPassword(configObject.optString("postgres_db_passwd"));
			dbsource.setMaxConnections(configObject.optInt("postgres_db_max_conn"));     
		} catch (Exception e) {
			logException(e);
		}
    }

	private void logException(Exception e) {
		StringWriter stack = new StringWriter();
		e.printStackTrace(new PrintWriter(stack));
		logger.info(stack.toString());
	}
	
	public static APIHDBSource getInstance(){
		if(apihDBSource == null)
			apihDBSource=  new APIHDBSource();
	
		return apihDBSource;
	}

    public Connection getConnection() throws Exception {
    	return dbsource.getConnection();          
    }

    public void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            }
            catch (SQLException e) {
            	logException(e);
            }
        }
    }
   
    public void close(Statement st) {
        if (st != null) {
            try {
                st.close();
            }
            catch (SQLException e) {
            	logException(e);
            }
        }
    }
    
    public void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (SQLException e) {
            	logException(e);
            }
        }
    }
   
    public void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e) {
            	logException(e);
            }
        }
    }
}