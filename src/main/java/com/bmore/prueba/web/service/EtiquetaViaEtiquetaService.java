package com.bmore.prueba.web.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.bmore.prueba.web.rest.resquest.CreateEtiquetaReportRequest;

public interface EtiquetaViaEtiquetaService {

	public ResponseEntity<Resource> createEtiquetaReport(CreateEtiquetaReportRequest request, String token);
}
