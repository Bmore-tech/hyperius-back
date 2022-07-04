package com.bmore.prueba.web.utils.remission;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class RemisionDatasource implements JRDataSource {

	private List<Remision> listaRemision = new ArrayList<Remision>();
	private int indiceRemisionActual = -1;

	@Override
	public boolean next() throws JRException {
		return ++indiceRemisionActual < listaRemision.size();
	}

	@Override
	public Object getFieldValue(JRField jrf) throws JRException {
		Object valor = null;

		if ("clave".equals(jrf.getName())) {
			valor = listaRemision.get(indiceRemisionActual).getClave();
		} else if ("descripcion".equals(jrf.getName())) {
			valor = listaRemision.get(indiceRemisionActual).getDescripcion();
		} else if ("cantidad".equals(jrf.getName())) {
			valor = listaRemision.get(indiceRemisionActual).getCantidad();
		}

		return valor;
	}

	public void addRemision(Remision remision) {
		this.listaRemision.add(remision);
	}
}
