package com.bmore.hyperius.web.rest;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @GetMapping(path = "/test")
  public String hola() throws SQLException {
    Connection con = jdbcTemplate.getDataSource().getConnection();
    if (con != null)
      return "Con no es null";
    else
      return "Con si es null";
  }
}
