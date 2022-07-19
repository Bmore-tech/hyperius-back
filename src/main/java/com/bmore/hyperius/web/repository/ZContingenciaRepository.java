package com.bmore.hyperius.web.repository;

import java.sql.Connection;

import com.bmore.hyperius.web.dto.ResultDTO;

public interface ZContingenciaRepository {

  public ResultDTO zContingencia(String usuario, String centro);

  // prev. private
  public int SFTPUP(Connection con, String werks, String mytimeStamp);

}
