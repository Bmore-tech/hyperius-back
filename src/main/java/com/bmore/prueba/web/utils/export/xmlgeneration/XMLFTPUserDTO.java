package com.bmore.prueba.web.utils.export.xmlgeneration;

public class XMLFTPUserDTO {

	String server;
	Integer port;
	String user;
	String password;
	String type;
	
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "XMLFTPUserDTO [password=" + password + ", port=" + port
				+ ", server=" + server + ", type=" + type + ", user=" + user
				+ "]";
	}
	
	public XMLFTPUserDTO(String server, Integer port, String user,
			String password, String type) {
		super();
		this.server = server;
		this.port = port;
		this.user = user;
		this.password = password;
		this.type = type;
	}
	public XMLFTPUserDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
