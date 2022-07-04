package com.bmore.prueba.web.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteOperacionesDTO {
	private String material;
	private String recepcion;
	private String alimentacion;
	private String produccion;
	private String embarque;
	private String recepciones;
	private String embarques;
	private String initialStock;
	private String currentStock;

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getRecepcion() {
		return recepcion;
	}

	public void setRecepcion(String recepcion) {
		this.recepcion = recepcion;
	}

	public String getAlimentacion() {
		return alimentacion;
	}

	public void setAlimentacion(String alimentacion) {
		this.alimentacion = alimentacion;
	}

	public String getProduccion() {
		return produccion;
	}

	public void setProduccion(String produccion) {
		this.produccion = produccion;
	}

	public String getEmbarque() {
		return embarque;
	}

	public void setEmbarque(String embarque) {
		this.embarque = embarque;
	}

	public String getRecepciones() {
		return recepciones;
	}

	public void setRecepciones(String recepciones) {
		this.recepciones = recepciones;
	}

	public String getEmbarques() {
		return embarques;
	}

	public void setEmbarques(String embarques) {
		this.embarques = embarques;
	}

	public static List<ReporteOperacionesDTO> ServletReporteOperacionesDTORS(
			ResultSet rs) {
		List<ReporteOperacionesDTO> operacionesDTOs = new ArrayList<ReporteOperacionesDTO>();

		try {

			if (rs != null) {
				while (rs.next()) {
					operacionesDTOs.add(new ReporteOperacionesDTO(rs
							.getString("MATNR"), rs.getString("RECEPCION"), rs
							.getString("ALIMENTACION"), rs
							.getString("PRODUCCION"), rs.getString("EMBARQUE"),
							rs.getString("RECEPCIONES"), rs
									.getString("EMBARQUES")));
				}
			}
		} catch (SQLException e) {
			operacionesDTOs = null;
		}
		return operacionesDTOs;
	}

	public ReporteOperacionesDTO(String material, String recepcion,
			String alimentacion, String produccion, String embarque,
			String recepciones, String embarques) {
		super();
		this.material = material;
		this.recepcion = recepcion;
		this.alimentacion = alimentacion;
		this.produccion = produccion;
		this.embarque = embarque;
		this.recepciones = recepciones;
		this.embarques = embarques;
	}

	public static List<ReporteOperacionesDTO> ServletReporteOperacionesStockDTORS(
			ResultSet rs) {
		List<ReporteOperacionesDTO> operacionesDTOs = new ArrayList<ReporteOperacionesDTO>();

		try {

			if (rs != null) {
				while (rs.next()) {
					operacionesDTOs.add(new ReporteOperacionesDTO(rs
							.getString("MATNR"), rs.getString("STOCK_INICIAL"),
							rs.getString("STOCK_FINAL"), rs
									.getString("RECEPCIONES"), rs
									.getString("EMBARQUES")));
				}
			}
		} catch (SQLException e) {
			operacionesDTOs = null;
		}
		return operacionesDTOs;
	}

	public ReporteOperacionesDTO(String material, String initialStock,
			String currentStock, String recepciones, String embarques) {
		super();

		this.material = material;
		this.initialStock = initialStock;
		this.currentStock = currentStock;
		this.recepciones = recepciones;
		this.embarques = embarques;
		
		this.recepcion = "0";
		this.alimentacion = "0";
		this.produccion = "0";
		this.embarque = "0";


	}

	public ReporteOperacionesDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setInitialStock(String initialStock) {
		this.initialStock = initialStock;
	}

	public String getInitialStock() {
		return initialStock;
	}

	public void setCurrentStock(String currentStock) {
		this.currentStock = currentStock;
	}

	public String getCurrentStock() {
		return currentStock;
	}

}
