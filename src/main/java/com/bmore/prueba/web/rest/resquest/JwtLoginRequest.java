package com.bmore.prueba.web.rest.resquest;

import java.io.Serializable;

public class JwtLoginRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String username;
	private String password;

	// need default constructor for JSON Parsing
	public JwtLoginRequest() {

	}

	public JwtLoginRequest(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "JwtRequest [username=" + username + ", password=" + password + "]";
	}

}