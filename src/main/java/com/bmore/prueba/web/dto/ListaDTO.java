package com.bmore.prueba.web.dto;

import java.util.ArrayList;
import java.util.List;

public class ListaDTO {

	private List<String> lista = new ArrayList<String>();
	private ResultDTO resultDT = new ResultDTO();

	public void setLista(List<String> lista) {
		this.lista = lista;
	}

	public List<String> getLista() {
		return lista;
	}

	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}

	public ResultDTO getResultDT() {
		return resultDT;
	}

}
