package com.bmore.prueba.web.dto;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class UsuarioItemDTO {

	private UsuariosDTO item;
	private ResultDTO result = new ResultDTO();

	public void setResult(ResultDTO result) {
		this.result = result;
	}

	public ResultDTO getResult() {
		return result;
	}

	public void setItem(UsuariosDTO item) {
		this.item = item;
	}

	public UsuariosDTO getItem() {
		return item;
	}

}
