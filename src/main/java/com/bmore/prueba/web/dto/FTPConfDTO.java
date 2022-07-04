package com.bmore.prueba.web.dto;

public class FTPConfDTO {

	private String ip;
	private String user;
	private String password;
	private String puerto;
	private String folder;
	private ResultDTO resultDT;

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getFolder() {
		return folder;
	}

}
