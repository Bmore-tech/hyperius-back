package com.bmore.hyperius.web.controller;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bmore.hyperius.web.dto.HuDTOItem;
import com.bmore.hyperius.web.rest.resquest.HuTxtRequest;
import com.bmore.hyperius.web.service.HuTxtService;
import com.bmore.hyperius.web.utils.Utils;

/**
 * Controller encargado de Hu TXT.
 * 
 * @author Eduardo Chombo - eduardo.chombo@b-more.tech
 * @version 1.0
 * @since 07-08-2020
 */
@Controller
public class HuTxtController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private HuTxtService huTxtService;

	@RequestMapping(value = "/hu-txt", method = RequestMethod.POST)
	protected ResponseEntity<Resource> huTxtReport(@RequestHeader("Authorization") String token,
			@RequestBody HuTxtRequest request) {
		return huTxtService.createEtiquetaReport(request, token);
	}

	@RequestMapping(value = "/hu-txt2", method = RequestMethod.POST)
	protected void huTxt(@RequestHeader("Authorization") String token, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession();

		String werks = Utils.getWerksFromJwt(token);
		HuDTOItem huDTOItem = (HuDTOItem) session.getAttribute("etiquetas");
		String entrega = "";

		if (huDTOItem != null) {

			String hus = "";

			for (int x = 0; x < huDTOItem.getItem().size(); x++) {
				entrega = huDTOItem.getItem().get(x).getVblen();
				hus += huDTOItem.getItem().get(x).getHu() + "\n";
			}

			try {

				File E;
				E = new File(entrega + "_" + werks + ".txt");
				FileWriter escribeArchivo;
				escribeArchivo = new FileWriter(E);
				BufferedWriter bw = new BufferedWriter(escribeArchivo);
				bw.write(hus);
				bw.close();
				escribeArchivo.close();

				response.setContentType("application/pdf");

				ServletOutputStream outputStream = response.getOutputStream();

				String mimetype = "";

				mimetype = "application/octet-stream";
				response.setHeader("Content-Disposition", "attachment; filename=\"" + E.getName() + "\"");

				response.setContentType(mimetype);
				response.setContentLength((int) E.length());
				int length = 0;
				byte[] byteBuffer = new byte[4096];

				DataInputStream dataInputStream = new DataInputStream(new FileInputStream(E));

				while ((dataInputStream != null) && ((length = dataInputStream.read(byteBuffer)) != -1)) {
					outputStream.write(byteBuffer, 0, length);
				}

				dataInputStream.close();
				outputStream.close();

				session.removeAttribute("etiquetas");

				try {

					E.delete();

				} catch (Exception e) {

				}
				logger.error("TODO OK");

			} catch (IOException e) {
				response.getWriter().write("Error: " + e.getMessage());
			}

		} else {
			response.getWriter().write("No se recibieron HUs");
		}

	}
}
