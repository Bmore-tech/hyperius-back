package com.bmore.hyperius.web.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.CreacionEntregasDTO;
import com.bmore.hyperius.web.dto.CrecionEntregaDTO;
import com.bmore.hyperius.web.dto.ReporteOperacionesDTO;
import com.bmore.hyperius.web.dto.ReporteShippingDTO;
import com.bmore.hyperius.web.dto.ServletReporteProformaDTO;
import com.bmore.hyperius.web.repository.old.CreacionEntregasRepository;
import com.bmore.hyperius.web.repository.old.ServletReporteRepository;
import com.bmore.hyperius.web.service.ReporteService;
import com.bmore.hyperius.web.utils.export.report.EDIGenerationBO;

@Service
public class ReporteServiceImpl implements ReporteService {

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	private static final String ext = ".csv";
	private static final String ext2 = ".txt";

	@Override
	public File getDocument(String reporte, String myTimeStamp, String werks, String vbeln) {
		File file = null;
		String filePath = filePath(reporte, myTimeStamp, werks, ext);
		ServletReporteRepository srvDAO = new ServletReporteRepository();
		CreacionEntregasRepository entregasDAO = new CreacionEntregasRepository();
		EDIGenerationBO ediBO = new EDIGenerationBO();
		if (reporte.equals("PROFORMA")) {

		} else if (reporte.equals("SHIPPING")) {
			file = generateShipping(reporte, filePath, srvDAO.getDatosShipping(werks));
		} else if (reporte.equals("REPORTE3")) {

		} else if (reporte.equals("EDI")) {
			String tknum = srvDAO.getTknum(vbeln);
			filePath = tknum.equals("") ? filePath(reporte + vbeln, myTimeStamp, werks, ext2)
					: filePath(tknum, "", "", ext2);
			String EDI = ediBO.generateEDI(vbeln);
			file = generateEdi(EDI, filePath);
		} else if (reporte.equals("ENTREGAS_CSC")) {
			file = generateEntregasCSC(reporte, filePath, entregasDAO.getEntregas());
		} else if (reporte.equals("RESUMEN_OPERACIONES")) {
			file = generateReporteOperaciones(filePath, srvDAO.getReporteOperacionesDAO(werks),
					srvDAO.getReporteOperacionesInitialStockDAO(werks));
		}

		return file;
	}

	@Override
	public File generateProforma(String type, String filepath, List<ServletReporteProformaDTO> profDTO) {
		File file = new File(filepath);

		return file;
	}

	@Override
	public File generateEdi(String EDI, String filepath) {

		File file = new File(filepath);
		try {
			PrintWriter writer = new PrintWriter(
					new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), "UTF-8"));
			writer.append(EDI);
			writer.close();
		} catch (FileNotFoundException e) {
			LOCATION.error("ServletReporteBO generateShipping fileNotFound:" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			LOCATION.error("ServletReporteBO generateShipping unsupported:" + e.getMessage());
		}
		return file;
	}

	@Override
	public File generateShipping(String type, String filepath, List<ReporteShippingDTO> profDTO) {

		File file = new File(filepath);
		try {
			PrintWriter writer = new PrintWriter(
					new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), "UTF-8"));

			writer.append("Contenedor,Sello,Booking,Destino,Buque,Peso,Naviera,Folio,Medida,Tipo,AA,SKU").println();
			if (profDTO.size() > 0) {
				for (ReporteShippingDTO serDTO : profDTO) {
					writer.append(serDTO.getContenedor()).append(",");
					writer.append(serDTO.getSello()).append(",");
					writer.append(serDTO.getBooking()).append(",");
					writer.append(serDTO.getDestino()).append(",");
					writer.append(serDTO.getBuque()).append(",");
					writer.append(serDTO.getPeso()).append(",");
					writer.append(serDTO.getNaviera()).append(",");
					writer.append(serDTO.getFolio()).append(",");
					writer.append(serDTO.getMedida()).append(",");
					writer.append(serDTO.getTipo()).append(",");
					writer.append(serDTO.getAa()).append(",");
					writer.append(serDTO.getSku()).println();
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			LOCATION.error("ServletReporteBO generateShipping fileNotFound:" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			LOCATION.error("ServletReporteBO generateShipping unsupported:" + e.getMessage());
		}
		return file;
	}

	@Override
	public File generateReporteOperaciones(String filepath, List<ReporteOperacionesDTO> profDTO,
			List<ReporteOperacionesDTO> profDTO2) {

		File file = new File(filepath);

		LOCATION.error("filePath" + filepath);
		try {
			PrintWriter writer = new PrintWriter(
					new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), "UTF-8"));
			writer.append(
					"Material,Stock Inicial,Stock Actual,Recepcion,Alimentacion,Produccion,Embarque,,,,Recepciones,Embarques")
					.println();

			HashMap<String, ReporteOperacionesDTO> materiales = new HashMap<String, ReporteOperacionesDTO>();

			for (int x = 0; x < profDTO2.size(); x++) {
				materiales.put(profDTO2.get(x).getMaterial(), profDTO2.get(x));
			}

			for (int x = 0; x < profDTO.size(); x++) {

				if (materiales.get(profDTO.get(x).getMaterial()) == null) {
					materiales.put(profDTO.get(x).getMaterial(), profDTO.get(x));
				} else {

					ReporteOperacionesDTO material = materiales.get(profDTO.get(x).getMaterial());

					material.setRecepcion(profDTO.get(x).getRecepcion());
					material.setAlimentacion(profDTO.get(x).getAlimentacion());
					material.setProduccion(profDTO.get(x).getProduccion());
					material.setEmbarque(profDTO.get(x).getEmbarque());

				}

			}
			LOCATION.error("materiales");
			Map<String, ReporteOperacionesDTO> materialesSorted = new TreeMap<String, ReporteOperacionesDTO>(
					materiales);

			// loop HashMap
			int x = 0;
			LOCATION.error("forEach");
			for (Entry<String, ReporteOperacionesDTO> entry : materialesSorted.entrySet()) {

				ReporteOperacionesDTO item = entry.getValue();

				writer.append(item.getMaterial()).append(",");
				writer.append(item.getInitialStock()).append(",");
				writer.append(item.getCurrentStock()).append(",");
				writer.append(item.getRecepcion()).append(",");
				writer.append(item.getAlimentacion()).append(",");
				writer.append(item.getProduccion()).append(",");
				writer.append(item.getEmbarque()).append(",");

				if (x == 0) {
					writer.append(",,,");
					writer.append(item.getRecepciones()).append(",");
					writer.append(item.getEmbarques()).println();

				} else {
					writer.append(",,,,").println();
				}

				x++;

			}

			writer.close();
		} catch (FileNotFoundException e) {
			LOCATION.error("ServletReporteBO generateShipping fileNotFound:" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			LOCATION.error("ServletReporteBO generateShipping unsupported:" + e.getMessage());
		}
		return file;
	}

	@Override
	public File generateEntregasCSC(String type, String filepath, CreacionEntregasDTO creacionEntregasDTO) {

		File file = new File(filepath);
		try {
			PrintWriter writer = new PrintWriter(
					new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), "UTF-8"));
			writer.append("Transporte,Entrega,Origen Entrega,Destino Entrega").println();

			if (creacionEntregasDTO.getResultDT().getId() == 1) {

				if (creacionEntregasDTO.getItems().getItem().size() > 0) {

					for (CrecionEntregaDTO crecionEntregaDTO : creacionEntregasDTO.getItems().getItem()) {
						writer.append(crecionEntregaDTO.getTknum()).append(",");
						writer.append(crecionEntregaDTO.getVbeln()).append(",");

						try {
							writer.append(crecionEntregaDTO.getWerksDesc().replace(",", " ")).append(",");
						} catch (Exception e) {
							writer.append(",");
						}

						try {
							writer.append(crecionEntregaDTO.getWerksDDesc().replace(",", " ")).println();

						} catch (Exception e) {
							writer.append(",");
						}
					}
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			LOCATION.error("ServletReporteBO generateEntregasCSC fileNotFound:" + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			LOCATION.error("ServletReporteBO generateEntregasCSC unsupported:" + e.getMessage());
		}
		return file;
	}

	@Override
	public String filePath(String reporte, String myTimeStamp, String werks, String ext) {

		return reporte + myTimeStamp + werks + ext;
	}
}
