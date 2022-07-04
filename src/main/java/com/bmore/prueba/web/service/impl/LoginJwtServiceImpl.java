package com.bmore.prueba.web.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.bmore.prueba.config.JwtTokenUtil;
import com.bmore.prueba.web.dto.LoginUserDetailsDTO;
import com.bmore.prueba.web.rest.response.JwtLoginResponse;
import com.bmore.prueba.web.rest.resquest.JwtLoginRequest;
import com.bmore.prueba.web.service.LoginJwtService;
import com.bmore.prueba.web.utils.Utils;

@Service
public class LoginJwtServiceImpl implements LoginJwtService {

	private final String TOKEN_PREFIX = "Bearer ";

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@Override
	public JwtLoginResponse loginJwt(JwtLoginRequest request) {
		LoginUserDetailsDTO<UserDetails> userDetails = null;
		JwtLoginResponse response = new JwtLoginResponse();

		try {
			// Busca y valida el usuario.
			userDetails = userDetailsService.loadUserByUsernameUme(request);

			// Si usuario y contraseña están bien.
			if (userDetails.getResultdto().getId() == 1) {
				// Carga de datos en Payload
				Map<String, Object> claims = new HashMap<>();
				claims.put("usuario", request.getUsername());
				claims.put("werks", userDetails.getResultdto().getMsg());
				claims.put("admin", userDetails.getResultdto().getTypeI());

				response.setToken(TOKEN_PREFIX + jwtTokenUtil.generateToken(userDetails.getUserDetails(), claims));
				response.setResponseCode(1);
				response.setMessage("Credenciales válidas");
			} else {
				response.setResponseCode(2);
				response.setMessage("Credenciales no válidas");
			}

			return response;
		} catch (Exception e) {
			response.setResponseCode(3);
			response.setMessage("Error de validación");
			e.printStackTrace();
		}

		return response;
	}

	@Override
	public JwtLoginResponse updateJwt(String token) {
		JwtLoginResponse response = new JwtLoginResponse();
		Map<String, Object> claims = new HashMap<>();
		claims.put("usuario", Utils.getUsuarioFromToken(token));
		claims.put("werks", Utils.getWerksFromJwt(token));
		claims.put("admin", Utils.getAdminFromToken(token));
		
		try {
			if(!jwtTokenUtil.isTokenExpired(token)) {
				response.setToken(TOKEN_PREFIX + jwtTokenUtil.updateToken(Utils.getUsuarioFromToken(token), claims));
				response.setResponseCode(1);
				response.setMessage("Token válido");
			}
		} catch (Exception e) {
			response.setResponseCode(2);
			response.setMessage("Token no válido");
			e.printStackTrace();
		}
		
		
		
		
		return response;
	}

	
}
