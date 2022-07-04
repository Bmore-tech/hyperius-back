package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CargaInformacionBodyDTO {
	String werks;
	String folioRecibidos;
	String folioEmbarcados;
	String embarquesContabilizados;
	String recepcionesContabilizadas;
	String ordenesProcesadas;
	String unidadesManipulacionProd;
	String unidadesManipulacionCons;
	String unidadesManipulacionTran;

	public String getWerks() {
		return werks;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getFolioRecibidos() {
		return folioRecibidos;
	}

	public void setFolioRecibidos(String folioRecibidos) {
		this.folioRecibidos = folioRecibidos;
	}

	public String getFolioEmbarcados() {
		return folioEmbarcados;
	}

	public void setFolioEmbarcados(String folioEmbarcados) {
		this.folioEmbarcados = folioEmbarcados;
	}

	public String getEmbarquesContabilizados() {
		return embarquesContabilizados;
	}

	public void setEmbarquesContabilizados(String embarquesContabilizados) {
		this.embarquesContabilizados = embarquesContabilizados;
	}

	public String getRecepcionesContabilizadas() {
		return recepcionesContabilizadas;
	}

	public void setRecepcionesContabilizadas(String recepcionesContabilizadas) {
		this.recepcionesContabilizadas = recepcionesContabilizadas;
	}

	public String getOrdenesProcesadas() {
		return ordenesProcesadas;
	}

	public void setOrdenesProcesadas(String ordenesProcesadas) {
		this.ordenesProcesadas = ordenesProcesadas;
	}

	public String getUnidadesManipulacionProd() {
		return unidadesManipulacionProd;
	}

	public void setUnidadesManipulacionProd(String unidadesManipulacionProd) {
		this.unidadesManipulacionProd = unidadesManipulacionProd;
	}

	public String getUnidadesManipulacionCons() {
		return unidadesManipulacionCons;
	}

	public void setUnidadesManipulacionCons(String unidadesManipulacionCons) {
		this.unidadesManipulacionCons = unidadesManipulacionCons;
	}

	public String getUnidadesManipulacionTran() {
		return unidadesManipulacionTran;
	}

	public void setUnidadesManipulacionTran(String unidadesManipulacionTran) {
		this.unidadesManipulacionTran = unidadesManipulacionTran;
	}

	@Override
	public String toString() {
		return "CargaInformacionBodyDTO [embarquesContabilizados="
				+ embarquesContabilizados + ", folioEmbarcados="
				+ folioEmbarcados + ", folioRecibidos=" + folioRecibidos
				+ ", ordenesProcesadas=" + ordenesProcesadas
				+ ", recepcionesContabilizadas=" + recepcionesContabilizadas
				+ ", unidadesManipulacionCons=" + unidadesManipulacionCons
				+ ", unidadesManipulacionProd=" + unidadesManipulacionProd
				+ ", unidadesManipulacionTran=" + unidadesManipulacionTran
				+ ", werks=" + werks + "]";
	}

	public String toReport() {
		return folioRecibidos + "," + folioEmbarcados + ","
				+ embarquesContabilizados + "," + recepcionesContabilizadas
				+ "," + ordenesProcesadas + "," + unidadesManipulacionProd
				+ "," + unidadesManipulacionCons + ","
				+ unidadesManipulacionTran;
	}

	public String headerReport() {
		return "FOLIOS RECIBIDOS,FOLIOS EMBARCADOS,"
				+ "EMBARQUES CONTABILIZADOS,RECEPCIONES CONTABILIZADAS,"
				+ "ORD. PRODUCCION MANEJADAS,HUS PRODUCIDAS,HUS CONSUMIDAS,HUS TRASLADADAS";
	}

	public CargaInformacionBodyDTO(String werks, String folioRecibidos,
			String folioEmbarcados, String embarquesContabilizados,
			String recepcionesContabilizadas, String ordenesProcesadas,
			String unidadesManipulacionProd, String unidadesManipulacionCons,
			String unidadesManipulacionTran) {
		super();
		this.werks = werks;
		this.folioRecibidos = folioRecibidos;
		this.folioEmbarcados = folioEmbarcados;
		this.embarquesContabilizados = embarquesContabilizados;
		this.recepcionesContabilizadas = recepcionesContabilizadas;
		this.ordenesProcesadas = ordenesProcesadas;
		this.unidadesManipulacionProd = unidadesManipulacionProd;
		this.unidadesManipulacionCons = unidadesManipulacionCons;
		this.unidadesManipulacionTran = unidadesManipulacionTran;
	}

	public CargaInformacionBodyDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

}
