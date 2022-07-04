package com.bmore.prueba.web.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.bmore.prueba.web.rest.resquest.ReportesRequest;

public interface ReportesService {

	ResponseEntity<Resource> reportes(ReportesRequest request, String token);
}
