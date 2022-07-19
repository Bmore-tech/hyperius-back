package com.bmore.hyperius.web.repository;

import com.bmore.hyperius.web.dto.LoginDTO;
import com.bmore.hyperius.web.dto.NewSecureLoginDTO;
import com.bmore.hyperius.web.dto.ResultDTO;

public interface LoginRepository {
  ResultDTO login(String entry);

  ResultDTO newLogin(NewSecureLoginDTO entry);

  ResultDTO loginAppWeb(String entry);

  LoginDTO existeRegistroUsuario(String entry);

  ResultDTO ingresaRegistroUsuario(LoginDTO loginDTO);

  ResultDTO actualizaHoraUltimaOperacion(String idRed);

  ResultDTO actualizaRegistroUsuario(LoginDTO loginDTO);

  ResultDTO logOut(String idRed);
}
