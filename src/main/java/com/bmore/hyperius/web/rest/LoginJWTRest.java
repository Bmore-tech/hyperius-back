package com.bmore.hyperius.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.bmore.hyperius.mobile.rest.response.DefaultResponse;
import com.bmore.hyperius.web.rest.response.JwtLoginResponse;
import com.bmore.hyperius.web.rest.resquest.JwtLoginRequest;
import com.bmore.hyperius.web.service.LoginJwtService;
import com.google.common.net.HttpHeaders;

/**
 * Controlador Rest para Login a través de JWT.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 02-12-2020
 */
@RestController
public class LoginJWTRest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private LoginJwtService loginJwtService;

  /**
   * Post Rest para el Login a través de JWT.
   * 
   * @param authenticationRequest Request con los datos del usuario.
   * @return {@link DefaultResponse} con la información del estado de la
   *         autenticación.
   * @throws Exception Error en caso de que no se haya podido crear la sesión.
   */
  @PostMapping(value = "/login/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
  public JwtLoginResponse createAuthenticationToken(@RequestBody JwtLoginRequest authenticationRequest) {
    log.info("Authenticating user... " + authenticationRequest.getUsername());

    return loginJwtService.loginJwt(authenticationRequest);
  }

  @PostMapping(value = "/login/check-token", produces = MediaType.APPLICATION_JSON_VALUE)
  public JwtLoginResponse checkToken(@RequestHeader("Auth") String token) {
    log.info("CheckTokenTest: " + token);
    return loginJwtService.updateJwt(token);
  }
}
