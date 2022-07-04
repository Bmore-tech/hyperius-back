package com.bmore.prueba.web.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.bmore.prueba.web.rest.resquest.RemisionRequest;

public interface RemisionService {

	ResponseEntity<Resource> etiquetaPt(RemisionRequest request, String token);
}
