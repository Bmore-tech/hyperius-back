package com.bmore.prueba.web.service.impl;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bmore.prueba.web.dto.LoginUserDetailsDTO;
import com.bmore.prueba.web.dto.NewSecureLoginDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.repository.old.LoginRepository;
import com.bmore.prueba.web.rest.resquest.JwtLoginRequest;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	
	private int constante = -3;

	private final Logger log = Logger.getLogger(this.getClass().getName());

	public LoginUserDetailsDTO<UserDetails> loadUserByUsernameUme(JwtLoginRequest authenticationRequest)
			throws Exception {
		LoginUserDetailsDTO<UserDetails> response = new LoginUserDetailsDTO<UserDetails>();
		UserDetails details = null;
		// response.setResponseGeneric(result);
		
		NewSecureLoginDTO nsl = new NewSecureLoginDTO();
		
		nsl.setUser(authenticationRequest.getUsername());
		nsl.setPassword(authenticationRequest.getPassword());
		
		ResultDTO resultdto = LoginRepository.newLogin(nsl);
		
		/*
		try {
			
			com.bmore.ume001.beans.User userLdap = new com.bmore.ume001.beans.User();
			userLdap.getEntity().setIdentyId(authenticationRequest.getUsername());
			userLdap.getAccInf().setPassword(authenticationRequest.getPassword());
			userLdap = new UMEDaoE().checkUserLDAP(userLdap);
			if (userLdap != null) {
				if (hasRole(authenticationRequest.getUsername())) {
					details = new User(authenticationRequest.getUsername(), authenticationRequest.getPassword(),
							new ArrayList<>());
					response.setGenResponse(details);
				} else {
					result.setResultId(constante);
					result.setResultMsgAbs("No se encontro el rol correspondiente del usuario");
				}
			} else {
				result.setResultId(constante);
				result.setResultMsgAbs("Usuario no Encontrado");
			}

		} catch (AuthenticationException e) {
			result.setResultId(constante);
			result.setResultMsgAbs("Credenciales Incorrectas");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while trying to execute loadUserByUsernameUme(User user).", e);
			throw e;
		}
		*/
		
		if(resultdto.getId() == 1) {
			details = new User(authenticationRequest.getUsername(), authenticationRequest.getPassword(), new ArrayList<>());
			response.setUserDetails(details);
		}
		response.setResultdto(resultdto);

		return response;
	}

	public boolean hasRole(String username) {
		boolean toReturn = true;
		/*
		List<Role> arRole = new UMEDaoE().getUserRoles(username);
		if (arRole != null && !arRole.isEmpty()) {
			for (Role rol : arRole) {
				if (rol.getRolId().trim().equalsIgnoreCase(Constants.handShakeRole)) {
					toReturn = true;
					break;
				}
			}
		}
		*/
		return toReturn;

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return new User(username, UUID.randomUUID().toString(), new ArrayList<>());
//		Entity entity = new Entity();
//		// com.bmore.ume001.beans.User user = null;
//		// entity.setIdentyId(username);
//		// entity.setIdType(Constants.baseUserType);
//		// entity.setDataSource(Constants.baseUserDataType);
//		try {
//			// user = new UMEDaoE().getUsersUMEPerfectmatch(entity);
////			if (user != null) {
//			
////			} else {
////				throw new UsernameNotFoundException("User not found with username: " + username);
////			}
//		} catch (Exception e) {
//			throw new UsernameNotFoundException("Database UMe error ocurred while searching user: " + username);
//		}
	}
}