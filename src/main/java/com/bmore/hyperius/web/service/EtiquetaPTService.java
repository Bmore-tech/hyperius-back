package com.bmore.hyperius.web.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.bmore.hyperius.web.rest.resquest.EtiquetaPtRequest;

public interface EtiquetaPTService {

	ResponseEntity<Resource> etiquetaPt(EtiquetaPtRequest request);
}
