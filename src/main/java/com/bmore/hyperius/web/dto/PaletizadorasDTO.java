package com.bmore.hyperius.web.dto;


import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class PaletizadorasDTO {

	private PaletizadoraItemsDTO paletizadoras;
	private ResultDTO resultDT;
	
	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

	public void setPaletizadoras(PaletizadoraItemsDTO paletizadoras) {
		this.paletizadoras = paletizadoras;
	}

	public PaletizadoraItemsDTO getPaletizadoras() {
		return paletizadoras;
	}

}
