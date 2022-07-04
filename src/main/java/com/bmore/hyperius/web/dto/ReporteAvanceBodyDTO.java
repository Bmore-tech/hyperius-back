package com.bmore.hyperius.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReporteAvanceBodyDTO {

	String werks;
	String entregasEntrantesRecibidas;
	String entregasEntrantesConfirmadas;
	String entregasEntrantesPendientes;
	String entregasSalientesRecibidas;
	String entregasSalientesConfirmadas;
	String entregasSalientesPendientes;
	String unidadesManipulacionConfirmadasEnvase;
	String unidadesManipulacionConsumidasEnvase;
	String unidadesManipulacionPendienteEnvase;
	String unidadesManipulacionConfirmadasUbicacion;
	String unidadesManipulacionConsumidasUbicacion;
	String unidadesManipulacionPendienteUbicacion;

	public ReporteAvanceBodyDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getWerks() {
		return werks;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getEntregasEntrantesRecibidas() {
		return entregasEntrantesRecibidas;
	}

	public void setEntregasEntrantesRecibidas(String entregasEntrantesRecibidas) {
		this.entregasEntrantesRecibidas = entregasEntrantesRecibidas;
	}

	public String getEntregasEntrantesConfirmadas() {
		return entregasEntrantesConfirmadas;
	}

	public void setEntregasEntrantesConfirmadas(
			String entregasEntrantesConfirmadas) {
		this.entregasEntrantesConfirmadas = entregasEntrantesConfirmadas;
	}

	public String getEntregasEntrantesPendientes() {
		return entregasEntrantesPendientes;
	}

	public void setEntregasEntrantesPendientes(
			String entregasEntrantesPendientes) {
		this.entregasEntrantesPendientes = entregasEntrantesPendientes;
	}

	public String getEntregasSalientesRecibidas() {
		return entregasSalientesRecibidas;
	}

	public void setEntregasSalientesRecibidas(String entregasSalientesRecibidas) {
		this.entregasSalientesRecibidas = entregasSalientesRecibidas;
	}

	public String getEntregasSalientesConfirmadas() {
		return entregasSalientesConfirmadas;
	}

	public void setEntregasSalientesConfirmadas(
			String entregasSalientesConfirmadas) {
		this.entregasSalientesConfirmadas = entregasSalientesConfirmadas;
	}

	public String getEntregasSalientesPendientes() {
		return entregasSalientesPendientes;
	}

	public void setEntregasSalientesPendientes(
			String entregasSalientesPendientes) {
		this.entregasSalientesPendientes = entregasSalientesPendientes;
	}

	public String getUnidadesManipulacionConfirmadasEnvase() {
		return unidadesManipulacionConfirmadasEnvase;
	}

	public void setUnidadesManipulacionConfirmadasEnvase(
			String unidadesManipulacionConfirmadasEnvase) {
		this.unidadesManipulacionConfirmadasEnvase = unidadesManipulacionConfirmadasEnvase;
	}

	public String getUnidadesManipulacionConsumidasEnvase() {
		return unidadesManipulacionConsumidasEnvase;
	}

	public void setUnidadesManipulacionConsumidasEnvase(
			String unidadesManipulacionConsumidasEnvase) {
		this.unidadesManipulacionConsumidasEnvase = unidadesManipulacionConsumidasEnvase;
	}

	public String getUnidadesManipulacionPendienteEnvase() {
		return unidadesManipulacionPendienteEnvase;
	}

	public void setUnidadesManipulacionPendienteEnvase(
			String unidadesManipulacionPendienteEnvase) {
		this.unidadesManipulacionPendienteEnvase = unidadesManipulacionPendienteEnvase;
	}

	public String getUnidadesManipulacionConfirmadasUbicacion() {
		return unidadesManipulacionConfirmadasUbicacion;
	}

	public void setUnidadesManipulacionConfirmadasUbicacion(
			String unidadesManipulacionConfirmadasUbicacion) {
		this.unidadesManipulacionConfirmadasUbicacion = unidadesManipulacionConfirmadasUbicacion;
	}

	public String getUnidadesManipulacionConsumidasUbicacion() {
		return unidadesManipulacionConsumidasUbicacion;
	}

	public void setUnidadesManipulacionConsumidasUbicacion(
			String unidadesManipulacionConsumidasUbicacion) {
		this.unidadesManipulacionConsumidasUbicacion = unidadesManipulacionConsumidasUbicacion;
	}

	public String getUnidadesManipulacionPendienteUbicacion() {
		return unidadesManipulacionPendienteUbicacion;
	}

	public void setUnidadesManipulacionPendienteUbicacion(
			String unidadesManipulacionPendienteUbicacion) {
		this.unidadesManipulacionPendienteUbicacion = unidadesManipulacionPendienteUbicacion;
	}

	public ReporteAvanceBodyDTO(String werks,
			String entregasEntrantesRecibidas,
			String entregasEntrantesConfirmadas,
			String entregasEntrantesPendientes,
			String entregasSalientesRecibidas,
			String entregasSalientesConfirmadas,
			String entregasSalientesPendientes,
			String unidadesManipulacionConfirmadasEnvase,
			String unidadesManipulacionConsumidasEnvase,
			String unidadesManipulacionPendienteEnvase,
			String unidadesManipulacionConfirmadasUbicacion,
			String unidadesManipulacionConsumidasUbicacion,
			String unidadesManipulacionPendienteUbicacion) {
		super();
		this.werks = werks;
		this.entregasEntrantesRecibidas = entregasEntrantesRecibidas;
		this.entregasEntrantesConfirmadas = entregasEntrantesConfirmadas;
		this.entregasEntrantesPendientes = entregasEntrantesPendientes;
		this.entregasSalientesRecibidas = entregasSalientesRecibidas;
		this.entregasSalientesConfirmadas = entregasSalientesConfirmadas;
		this.entregasSalientesPendientes = entregasSalientesPendientes;
		this.unidadesManipulacionConfirmadasEnvase = unidadesManipulacionConfirmadasEnvase;
		this.unidadesManipulacionConsumidasEnvase = unidadesManipulacionConsumidasEnvase;
		this.unidadesManipulacionPendienteEnvase = unidadesManipulacionPendienteEnvase;
		this.unidadesManipulacionConfirmadasUbicacion = unidadesManipulacionConfirmadasUbicacion;
		this.unidadesManipulacionConsumidasUbicacion = unidadesManipulacionConsumidasUbicacion;
		this.unidadesManipulacionPendienteUbicacion = unidadesManipulacionPendienteUbicacion;
	}

	@Override
	public String toString() {
		return "ReporteAvanceBodyDTO [entregasEntrantesConfirmadas="
				+ entregasEntrantesConfirmadas
				+ ", entregasEntrantesPendientes="
				+ entregasEntrantesPendientes + ", entregasEntrantesRecibidas="
				+ entregasEntrantesRecibidas
				+ ", entregasSalientesConfirmadas="
				+ entregasSalientesConfirmadas
				+ ", entregasSalientesPendientes="
				+ entregasSalientesPendientes + ", entregasSalientesRecibidas="
				+ entregasSalientesRecibidas
				+ ", unidadesManipulacionConfirmadasEnvase="
				+ unidadesManipulacionConfirmadasEnvase
				+ ", unidadesManipulacionConfirmadasUbicacion="
				+ unidadesManipulacionConfirmadasUbicacion
				+ ", unidadesManipulacionConsumidasEnvase="
				+ unidadesManipulacionConsumidasEnvase
				+ ", unidadesManipulacionConsumidasUbicacion="
				+ unidadesManipulacionConsumidasUbicacion
				+ ", unidadesManipulacionPendienteEnvase="
				+ unidadesManipulacionPendienteEnvase
				+ ", unidadesManipulacionPendienteUbicacion="
				+ unidadesManipulacionPendienteUbicacion + ", werks=" + werks
				+ "]";
	}

	public String toReport() {
		return werks + "," + entregasEntrantesRecibidas + ","
				+ entregasEntrantesConfirmadas + ","
				+ entregasEntrantesPendientes + ","
				+ entregasSalientesRecibidas + ","
				+ entregasSalientesConfirmadas + ","
				+ entregasSalientesPendientes + ","
				+ unidadesManipulacionConfirmadasEnvase + ","
				+ unidadesManipulacionConsumidasEnvase + ","
				+ unidadesManipulacionPendienteEnvase + ","
				+ unidadesManipulacionConfirmadasUbicacion + ","
				+ unidadesManipulacionConsumidasUbicacion + ","
				+ unidadesManipulacionPendienteUbicacion;
	}

	public String toReportHeader() {
		return "Centro, Entregas Entrantes Recibidas, Entregas Entrantes Confirmadas, Entregas Entrantes Pendientes, Entregas Salientes Recibidas,"
				+ "Entregas Salientes Confirmadas, Entregas Salientes Pendientes, HU's Confirmadas Alimentacion Linea, HU's Consumidas Alimentacion Linea,"
				+ "HU's Pendientes Alimentacion Linea, HU's Confirmadas Ubicacion PT, HU's Consumidas Ubicacion PT, HU's Pendientes Ubicacion PT";
	}

}
