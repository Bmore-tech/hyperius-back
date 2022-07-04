package com.bmore.hyperius.web.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.bmore.hyperius.web.rest.resquest.HuTxtRequest;

public interface HuTxtService {

	public ResponseEntity<Resource> createEtiquetaReport(HuTxtRequest request, String token);
}
