package com.bmore.prueba.web.service;

import com.bmore.prueba.web.rest.response.JwtLoginResponse;
import com.bmore.prueba.web.rest.resquest.JwtLoginRequest;

public interface LoginJwtService {

	public JwtLoginResponse loginJwt(JwtLoginRequest authenticationRequest);
	
	public JwtLoginResponse updateJwt(String token);
}
