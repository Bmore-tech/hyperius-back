package com.bmore.hyperius.web.dto;

public class LoginDTO {

	private String idRed;
	private String sessionId;
	private String lastLogin;
	private String lastOperation;
	private String logOut;

	private ResultDTO result;

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

	public void setResult(ResultDTO result) {
		this.result = result;
	}

	public ResultDTO getResult() {
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

}
