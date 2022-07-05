package com.bmore.hyperius.web.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRest {
  
  @GetMapping(path = "/")
  public String hola() {
    return "Hola XD";
  }
}
