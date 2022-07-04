package com.bmore.prueba.mobile.rest.response;

import com.bmore.prueba.mobile.utils.LoginDTO;

public class LoginResponse extends DefaultResponse {

	private LoginDTO data;

	public LoginDTO getData() {
		return data;
	}

	public void setData(LoginDTO data) {
		this.data = data;
	}
}
