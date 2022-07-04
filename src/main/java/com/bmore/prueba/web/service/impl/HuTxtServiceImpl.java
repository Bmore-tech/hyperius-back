package com.bmore.prueba.web.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bmore.prueba.web.dto.HuDTOItem;
import com.bmore.prueba.web.rest.resquest.HuTxtRequest;
import com.bmore.prueba.web.service.HuTxtService;
import com.bmore.prueba.web.utils.Utils;

@Service
public class HuTxtServiceImpl implements HuTxtService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public ResponseEntity<Resource> createEtiquetaReport(HuTxtRequest request, String token) {
		HuDTOItem huDTOItem = new HuDTOItem();
		huDTOItem.setItem(request.getItem());
		HttpHeaders headers = new HttpHeaders();
		String entrega = "";
		String werks = Utils.getWerksFromJwt(token);
		String filePath = entrega + "_" + werks + ".txt";

		String hus = "";

		for (int x = 0; x < huDTOItem.getItem().size(); x++) {
			entrega = huDTOItem.getItem().get(x).getVblen();
			hus += huDTOItem.getItem().get(x).getHu() + "\n";
		}

		File file = new File(filePath);
		FileWriter escribeArchivo;
		try {
			escribeArchivo = new FileWriter(file);

			BufferedWriter bw = new BufferedWriter(escribeArchivo);
			bw.write(hus);
			bw.close();
			escribeArchivo.close();
		} catch (IOException e1) {
			logger.error(e1.toString());
			e1.printStackTrace();
		}

		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		InputStreamResource resource = null;
		try {

			resource = new InputStreamResource(new FileInputStream(file));

			logger.error("TODO OK");

		} catch (IOException e) {
			logger.error(e.toString());
			e.getStackTrace();
		}

		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

}
