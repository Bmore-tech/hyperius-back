package com.bmore.hyperius.web.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.bmore.hyperius.web.rest.resquest.ReportesRequest;

public interface ReportesService {

	ResponseEntity<Resource> reportes(ReportesRequest request, String token);
}
