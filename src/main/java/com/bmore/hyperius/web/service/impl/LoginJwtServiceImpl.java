package com.bmore.hyperius.web.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.config.JwtTokenUtil;
import com.bmore.hyperius.web.dto.LoginUserDetailsDTO;
import com.bmore.hyperius.web.rest.response.JwtLoginResponse;
import com.bmore.hyperius.web.rest.resquest.JwtLoginRequest;
import com.bmore.hyperius.web.service.LoginJwtService;
import com.bmore.hyperius.web.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        claims.put("werks", request.getPassword());
        claims.put("admin", 1);

        response.setToken(TOKEN_PREFIX + jwtTokenUtil.generateToken(null, claims));
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
    log.info("El token es:", token);
    JwtLoginResponse response = new JwtLoginResponse();
    Map<String, Object> claims = new HashMap<>();
    claims.put("usuario", "PC13");
    claims.put("werks", "PC13");
    claims.put("admin", "admin");

    response.setToken(TOKEN_PREFIX + jwtTokenUtil.updateToken(Utils.getUsuarioFromToken(token), claims));
    response.setResponseCode(1);
    response.setMessage("Token válido");
    try {
      if (!jwtTokenUtil.isTokenExpired(token)) {
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
