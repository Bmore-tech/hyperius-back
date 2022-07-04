package com.bmore.prueba.web.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DescargaInformacionDTO {

	String anio;
	String mes;
	String dia;
	String intervaloIni;
	String intervaloFin;
	List<DescargaInformacionBodyDTO> itemDto;
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

	public List<DescargaInformacionBodyDTO> getItemDto() {
		return itemDto;
	}

	public void setItemDto(List<DescargaInformacionBodyDTO> itemDto) {
		this.itemDto = itemDto;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public DescargaInformacionDTO(String anio, String mes, String dia,
			String intervaloIni, String intervaloFin,
			List<DescargaInformacionBodyDTO> itemDto, ResultDTO resultDT) {
		super();
		this.anio = anio;
		this.mes = mes;
		this.dia = dia;
		this.intervaloIni = intervaloIni;
		this.intervaloFin = intervaloFin;
		this.itemDto = itemDto;
		this.resultDT = resultDT;
	}

	public DescargaInformacionDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "DescargaInformacionDTO [anio=" + anio + ", dia=" + dia
				+ ", intervaloFin=" + intervaloFin + ", intervaloIni="
				+ intervaloIni + ", itemDto=" + itemDto + ", mes=" + mes
				+ ", resultDT=" + resultDT + "]";
	}

}
