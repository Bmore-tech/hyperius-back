package com.bmore.hyperius.web.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.bmore.hyperius.web.rest.resquest.RemisionRequest;

public interface RemisionService {

	ResponseEntity<Resource> etiquetaPt(RemisionRequest request, String token);
}
