package com.bmore.hyperius.web.service;

import com.bmore.hyperius.web.rest.response.JwtLoginResponse;
import com.bmore.hyperius.web.rest.resquest.JwtLoginRequest;

public interface LoginJwtService {

	public JwtLoginResponse loginJwt(JwtLoginRequest authenticationRequest);
	
	public JwtLoginResponse updateJwt(String token);
}
