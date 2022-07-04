package com.bmore.hyperius.web.utils.print;

import java.util.ArrayList;
import java.util.List;

import com.bmore.hyperius.web.dto.ResultDTO;

public class Etiquetas {

	private List<Etiqueta> items = new ArrayList<Etiqueta>();
	private ResultDTO resultDT= new ResultDTO();
	
	
	public void setItems(List<Etiqueta> items) {
		this.items = items;
	}
	public List<Etiqueta> getItems() {
		return items;
	}
	public void setResultDT(ResultDTO resultDT) {
		this.resultDT = resultDT;
	}
	public ResultDTO getResultDT() {
		return resultDT;
	}
	

}
