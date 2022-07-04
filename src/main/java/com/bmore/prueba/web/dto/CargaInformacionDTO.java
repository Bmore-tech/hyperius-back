package com.bmore.prueba.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CargaInformacionDTO {
	String anio;
	String mes;
	String dia;
	String intervaloIni;
	String intervaloFin;
	List<CargaInformacionBodyDTO> itemDto;
	ResultDTO resultDT;

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public String getIntervaloIni() {
		return intervaloIni;
	}

	public void setIntervaloIni(String intervaloIni) {
		this.intervaloIni = intervaloIni;
	}

	public String getIntervaloFin() {
		return intervaloFin;
	}

	public void setIntervaloFin(String intervaloFin) {
		this.intervaloFin = intervaloFin;
	}

	public List<CargaInformacionBodyDTO> getItemDto() {
		return itemDto;
	}

	public void setItemDto(List<CargaInformacionBodyDTO> itemDto) {
		this.itemDto = itemDto;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	@Override
	public String toString() {
		return "CargaInformacionDTO [anio=" + anio + ", dia=" + dia
				+ ", intervaloFin=" + intervaloFin + ", intervaloIni="
				+ intervaloIni + ", itemDto=" + itemDto + ", mes=" + mes
				+ ", resultDT=" + resultDT + "]";
	}

	public CargaInformacionDTO(String anio, String mes, String dia,
			String intervaloIni, String intervaloFin,
			List<CargaInformacionBodyDTO> itemDto, ResultDTO resultDT) {
		super();
		this.anio = anio;
		this.mes = mes;
		this.dia = dia;
		this.intervaloIni = intervaloIni;
		this.intervaloFin = intervaloFin;
		this.itemDto = itemDto;
		this.resultDT = resultDT;
	}

	public CargaInformacionDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

}
