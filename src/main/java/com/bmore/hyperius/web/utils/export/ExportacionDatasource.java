package com.bmore.hyperius.web.utils.export;

import java.util.ArrayList;
import java.util.List;

import com.bmore.hyperius.web.utils.remission.Remision;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ExportacionDatasource implements JRDataSource  {
	private List<Remision> listaRemision = new ArrayList<Remision>();
    private int indiceRemisionActual = -1;

    @Override
    public boolean next() throws JRException {
        return ++indiceRemisionActual < listaRemision.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;
        
    if("clave".equals(jrf.getName())) 
    { 
        valor = listaRemision.get(indiceRemisionActual).getClave(); 
    } 
    else if("descripcion".equals(jrf.getName())) 
    { 
        valor = listaRemision.get(indiceRemisionActual).getDescripcion(); 
    } 
    else if("cantidad".equals(jrf.getName())) 
    { 
        valor = listaRemision.get(indiceRemisionActual).getCantidad(); 
    }
    else if("precioUnitario".equals(jrf.getName()))
    {
    	valor = listaRemision.get(indiceRemisionActual).getPrecioUnitario();
    }
        return valor;
    }
    
    public void addRemision(Remision remision){
        
        this.listaRemision.add(remision);
    }

}
