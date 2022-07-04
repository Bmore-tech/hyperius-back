package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NewSecureLoginDTO {

	String user;
	String password;

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

	@Override
	public String toString() {
		return "newSecureLogin [password=" + password + ", user=" + user + "]";
	}

	public NewSecureLoginDTO(String user, String password) {
		super();
		this.user = user;
		this.password = password;
	}

	public NewSecureLoginDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

}
