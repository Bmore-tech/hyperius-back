package com.bmore.prueba.mobile.utils;

public class LoginDTO {
	private String idRed;
	private String name;
	private String password;
	private String sessionId;
	private String lastLogin;
	private String lastOperation;
	private String werks;
	private String logOut;
	private int admin;
	private boolean reloadLogin;
	private String sentinelVersion;
	private ResultDT result;

	public void setIdRed(String idRed) {
		this.idRed = idRed;
	}
	public String getIdRed() {
		return idRed;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setLogOut(String logOut) {
		this.logOut = logOut;
	}
	public String getLogOut() {
		return logOut;
	}
	public void setResult(ResultDT result) {
		this.result = result;
	}
	public ResultDT getResult() {
		return result;
	}
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}
	public String getLastLogin() {
		return lastLogin;
	}
	public void setLastOperation(String lastOperation) {
		this.lastOperation = lastOperation;
	}
	public String getLastOperation() {
		return lastOperation;
	}
	public void setWerks(String werks) {
		this.werks = werks;
	}
	public String getWerks() {
		return werks;
	}
	public void setAdmin(int admin) {
		this.admin = admin;
	}
	public int getAdmin() {
		return admin;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
	public void setReloadLogin(boolean reloadLogin) {
		this.reloadLogin = reloadLogin;
	}
	public boolean isReloadLogin() {
		return reloadLogin;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setBcpsVersion(String sentinelVersion) {
		this.sentinelVersion = sentinelVersion;
	}
	public String getBcpsVersion() {
		return sentinelVersion;
	}
}
