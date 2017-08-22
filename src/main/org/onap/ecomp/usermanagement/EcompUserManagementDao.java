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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.onap.ecomp.persistence.APIHDBSource;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
/*
 * 
 */
public class EcompUserManagementDao {
	final static EELFLogger logger = EELFManager.getInstance().getLogger(EcompUserManagementDao.class);
	private APIHDBSource apihDBSource = null;
	
	public EcompUserManagementDao() {
		apihDBSource = APIHDBSource.getInstance();
	}
	/*
	 * 
	 */
	private void logException(Exception e) {
		StringWriter stack = new StringWriter();
		e.printStackTrace(new PrintWriter(stack));
		logger.info(stack.toString());
	}
	
	private void close(Connection conn, Statement st, ResultSet rs ) {
		apihDBSource.close(conn);
		apihDBSource.close(st);
		apihDBSource.close(rs);
	}
	
	public List<EcompRole> getRoles() {
		logger.info("Received request: getRoles");
		List<EcompRole> roles = null; 
		ResultSet rs = null;
		Statement st = null;
		Connection conn = null;
		String sql = "SELECT role_id, role_name FROM fn_role where active_yn='Y';";
		logger.info(sql);
		try {
			conn = apihDBSource.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			roles = new ArrayList<EcompRole>();
			while (rs.next()) {
				String roleName = rs.getString("role_name");
				Long id = rs.getLong("role_id");
				EcompRole role = new EcompRole();
				role.setId(id);
				role.setName(roleName);
				roles.add(role);
			}
			logger.info("found " + roles.size() + " record(s).");
		}  
		catch (Exception e ) {
			this.logException(e);
		}
		finally {
			this.close(conn, st, rs );
		}
		return roles;
	}
	/*
	 * 
	 */
	public List<EcompRole> getRoles(String userId) {
		logger.info("Received request: getRoles/userId:" + userId);
		List<EcompRole> roles = null; 
		ResultSet rs = null;
		Statement st = null;
		Connection conn = null;
		String sql = "select r.role_id, r.role_name " +
					" from fn_user_role ur, fn_role r, fn_user u " +
					" where u.org_user_id=" + "'" + userId + "'" +
					" and ur.role_id=r.role_id " +
					" and u.user_id = ur.user_id; ";
		logger.info(sql);
		try {
			conn = apihDBSource.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			roles = new ArrayList<EcompRole>();
			while (rs.next()) {
				String roleName = rs.getString("role_name");
				Long id = rs.getLong("role_id");
				EcompRole role = new EcompRole();
				role.setId(id);
				role.setName(roleName);
				roles.add(role);
			}
			logger.info("found " + roles.size() + " record(s).");
		}  
		catch (Exception e ) {
			this.logException(e);
		}
		finally {
			this.close(conn, st, rs );
		}
		return roles;
	}
	/*
	 * 
	 */
	public List<EcompUser> getUsers() {
		logger.info("Received request: getUsers");
		List<EcompUser> ecompUsers = null; 
		ResultSet rs = null;
		Statement st = null;
		Connection conn = null;
		String sql = "SELECT org_id, manager_id, first_name, middle_name, last_name, phone, " +
					" email, hrid, org_user_id, org_code, org_manager_userid, job_title, " +
					" login_id, active_yn FROM fn_user;";
		logger.info(sql);
		try {
			conn = apihDBSource.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			ecompUsers = new ArrayList<EcompUser>();
			while (rs.next()) {
				Long orgId = rs.getLong("org_id");
				String managerId = rs.getString("manager_id");
				String firstName = rs.getString("first_name");;
				String middleInitial = rs.getString("middle_name");
				String lastName = rs.getString("last_name");
				String phone = rs.getString("phone");
				String email = rs.getString("email");
				String hrid = rs.getString("hrid");
				String orgUserId = rs.getString("org_user_id");
				String orgCode = rs.getString("org_code");
				String orgManagerUserId = rs.getString("org_manager_userid");
				String jobTitle = rs.getString("job_title");
				String loginId = rs.getString("login_id");
				boolean active = rs.getBoolean("active_yn");
				
				EcompUser ecompUser = new EcompUser();
				ecompUser.setOrgId(orgId);
				ecompUser.setManagerId(managerId);
				ecompUser.setFirstName(firstName);
				ecompUser.setMiddleInitial(middleInitial);
				ecompUser.setLastName(lastName);
				ecompUser.setPhone(phone);
				ecompUser.setEmail(email);
				ecompUser.setHrid(hrid);
				ecompUser.setOrgUserId(orgUserId);
				ecompUser.setOrgCode(orgCode);
				ecompUser.setOrgManagerUserId(orgManagerUserId);
				ecompUser.setJobTitle(jobTitle);
				ecompUser.setLoginId(loginId);
				ecompUser.setActive(active);
			
				ecompUsers.add(ecompUser);
			}
			logger.info("found " + ecompUsers.size() + " record(s).");
			
			for (EcompUser user : ecompUsers ) {
				String userId = user.getOrgUserId();
				List<EcompRole> roles = getRoles(userId);
				Set<EcompRole> setRoles = new HashSet<EcompRole>(roles);
				user.setRoles(setRoles);
			}	
		}  
		catch (Exception e ) {
			this.logException(e);
		}
		finally {
			this.close(conn, st, rs );
		}
		return ecompUsers;
	}
	/*
	 * 
	 */
	public List<EcompUser> getUser(String uid) {
		logger.info("Received request: getUser:" + uid);
		List<EcompUser> ecompUsers = null; 
		ResultSet rs = null;
		Statement st = null;
		Connection conn = null;
		String sql = "SELECT org_id, manager_id, first_name, " +
					" middle_name, last_name, phone, email, hrid, " +
					" org_user_id, org_code, org_manager_userid, job_title, " +
					" login_id, active_yn " + 
					" FROM fn_user " +
					" where org_user_id = " + "'" + uid + "'";
		logger.info(sql);
		try {
			conn = apihDBSource.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			ecompUsers = new ArrayList<EcompUser>();
			while (rs.next()) {
				Long orgId = rs.getLong("org_id");
				String managerId = rs.getString("manager_id");
				String firstName = rs.getString("first_name");;
				String middleInitial = rs.getString("middle_name");
				String lastName = rs.getString("last_name");
				String phone = rs.getString("phone");
				String email = rs.getString("email");
				String hrid = rs.getString("hrid");
				String orgUserId = rs.getString("org_user_id");
				String orgCode = rs.getString("org_code");
				String orgManagerUserId = rs.getString("org_manager_userid");
				String jobTitle = rs.getString("job_title");
				String loginId = rs.getString("login_id");
				boolean active = rs.getBoolean("active_yn");
				
				EcompUser ecompUser = new EcompUser();
				ecompUser.setOrgId(orgId);
				ecompUser.setManagerId(managerId);
				ecompUser.setFirstName(firstName);
				ecompUser.setMiddleInitial(middleInitial);
				ecompUser.setLastName(lastName);
				ecompUser.setPhone(phone);
				ecompUser.setEmail(email);
				ecompUser.setHrid(hrid);
				ecompUser.setOrgUserId(orgUserId);
				ecompUser.setOrgCode(orgCode);
				ecompUser.setOrgManagerUserId(orgManagerUserId);
				ecompUser.setJobTitle(jobTitle);
				ecompUser.setLoginId(loginId);
				ecompUser.setActive(active);
			
				ecompUsers.add(ecompUser);
			}
			logger.info("found " + ecompUsers.size() + " record(s).");
			
			for (EcompUser user : ecompUsers ) {
				logger.info("+++++");
				String userId = user.getOrgUserId();
				List<EcompRole> roles = getRoles(userId);
				Set<EcompRole> setRoles = new HashSet<EcompRole>(roles);
				user.setRoles(setRoles);
			}	
		}  
		catch (Exception e ) {
			this.logException(e);
		}
		finally {
			this.close(conn, st, rs );
		}
		return ecompUsers;
	}

	/**
	 * 
	 * @param uid
	 * @return
	 */
	public List<EcompRole> getUserRole(String uid ) {
		logger.info("Received request: getUserRole:" + uid);
		return getRoles(uid);
	}
	
	public int adduser(EcompUser[] userInfo) {
		logger.info("Received request: adduser");
		int users = -1;
		/*
		PreparedStatement pst = null;
		Connection conn = null; 
		ResultSet rs = null;
		String sql = "INSERT INTO fn_user (	first_name, " +
										" 	last_name, " +
										" 	phone, " +
										"	email, " +
										" 	hrid, " +
										"	org_user_id, " +
										" 	org_code, " +
										"	login_id, " +
										" 	active_yn, " +
										"	state_cd, " +
										" 	country_cd ) " +
										" 	VALUES " + 
										" 	( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); "; 
		logger.info(sql);
		try {
			conn = apihDBSource.getConnection();
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);

			for (EcompUser ui:userInfo) {	 
				logger.info(ui.toString());
				String orgUserId = ui.getOrgUserId();
				if (orgUserId==null || orgUserId.length()==0) {
					continue;
				}
				LdapUtil ldap= new LdapUtil(orgUserId);
				if (ldap.getAll().equals("")) 
				continue;
				logger.info(ldap.getAll());
			
				String orgId=ldap.getOrgid();
				String managerId=ldap.getMngr();
				String firstName=ldap.getFirstName();
				//String middleInitial=;
				String lastName=ldap.getLastName();
				String phone=ldap.getTeln();
				String email=ldap.getMail();
				String hrid=ldap.getAttuid();
				//String orgUserId=;
				String orgCode=ldap.getBorg();
				String orgManagerUserId=ldap.getManagerId();
				String jobTitle=ldap.getJttl();
				String loginId=ldap.getHndl();
				String state = ldap.getStat();
				String country = ldap.getCountry();
				String active="N";		
				logger.info("User info:" + ui.toString());
			
				pst.setString(1,firstName);
				pst.setString(2,lastName);
				pst.setString(3,phone);
				pst.setString(4,email);
				pst.setString(5,hrid);
				pst.setString(6,orgUserId);
				pst.setString(7,orgCode);
				pst.setString(8,loginId);
				pst.setString(9,active);
				pst.setString(10,state);
				pst.setString(11,country);
				
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				int user_id = -1;
	            if(rs != null && rs.next()){
	            	user_id = rs.getInt(1);
	            }

				logger.info("User ID:" + user_id);
				
				Set<EcompRole> roles = ui.getRoles();
				if (roles != null && !roles.isEmpty()) {
					addUserRole(conn, user_id, roles);
				}
				conn.commit();
				users++;
			} // end of for loop
		} catch (Exception e) {
			apihDBSource.rollback(conn);
			this.logException(e);
		}
		finally {
			this.close(conn, pst, rs );
		}
		logger.info("User Added:" + (users + 1));
		*/
		return users+1;
	}
	
	private void addUserRole(Connection conn, int userId, Set<EcompRole> roles) throws Exception {
		logger.info("Received request: addUserRole");
		PreparedStatement pst = null;
		String sql = "INSERT INTO fn_user_role ( user_id, " +
										" 	role_id, " +
										" 	app_id ) " +
										" 	VALUES " + 
										" 	( ?, ?, ? ); "; 
		logger.info(sql);	
		List<EcompRole> roleInfo = getRoles();
		try {
			pst = conn.prepareStatement(sql);
			for (EcompRole er : roles ){
				for (EcompRole ei : roleInfo) {
					if (er.getName().equals(ei.getName())) {
						long roleId = ei.getId();
						pst.setInt(1,userId);
						pst.setLong(2,roleId);
						pst.setInt(3,1);
						pst.executeUpdate();
						logger.info("Added role:" + ei.getName() + " for user_id " + userId);
						break;
					}	
				}
			}			
		} catch ( Exception e) {
			this.logException(e);
			throw e;
		} finally {
			apihDBSource.close(pst);
		}
	}
}