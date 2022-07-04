package com.bmore.hyperius.web.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.bmore.hyperius.web.rest.resquest.CreateEtiquetaReportRequest;

public interface EtiquetaViaEtiquetaService {

	public ResponseEntity<Resource> createEtiquetaReport(CreateEtiquetaReportRequest request, String token);
}
