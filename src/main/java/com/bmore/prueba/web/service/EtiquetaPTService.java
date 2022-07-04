package com.bmore.prueba.web.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.bmore.prueba.web.rest.resquest.EtiquetaPtRequest;

public interface EtiquetaPTService {

	ResponseEntity<Resource> etiquetaPt(EtiquetaPtRequest request);
}
