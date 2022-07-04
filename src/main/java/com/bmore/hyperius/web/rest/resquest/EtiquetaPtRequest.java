package com.bmore.hyperius.web.rest.resquest;

public class EtiquetaPtRequest {

	private String aufnr;
	private String key;
	private String cantidadEtiquetasAImprimir;

	public String getAufnr() {
		return aufnr;
	}

	public void setAufnr(String aufnr) {
		this.aufnr = aufnr;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCantidadEtiquetasAImprimir() {
		return cantidadEtiquetasAImprimir;
	}

	public void setCantidadEtiquetasAImprimir(String cantidadEtiquetasAImprimir) {
		this.cantidadEtiquetasAImprimir = cantidadEtiquetasAImprimir;
	}

	@Override
	public String toString() {
		return "EtiquetaPtRequest [aufnr=" + aufnr + ", key=" + key + ", cantidadEtiquetasAImprimir="
				+ cantidadEtiquetasAImprimir + "]";
	}
}
