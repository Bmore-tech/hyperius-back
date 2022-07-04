package com.bmore.prueba.web.utils.print;
import java.util.ArrayList;
import java.util.List;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;


public class EtiquetaDatasource implements JRDataSource{

    private List<Etiqueta> listaEtiqueta = new ArrayList<Etiqueta>();
    private int indiceEtiquetaActual = -1;
    
    @Override
    public boolean next() throws JRException {
        return ++indiceEtiquetaActual < listaEtiqueta.size();
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object valor = null;  

    if("barCode".equals(jrf.getName())) 
    { 
        valor = listaEtiqueta.get(indiceEtiquetaActual).getBarCode(); 
    } 
    else if("CHARG4".equals(jrf.getName())) 
    { 
        valor = listaEtiqueta.get(indiceEtiquetaActual).getCHARG4(); 
    } 
    else if("EXIDV_HU".equals(jrf.getName())) 
    { 
        valor = listaEtiqueta.get(indiceEtiquetaActual).getEXIDV_HU(); 
    } 
    else if("fechaImpr".equals(jrf.getName())) 
    { 
        valor = listaEtiqueta.get(indiceEtiquetaActual).getFechaImpr(); 
    }
    else if("MAKTX_desc".equals(jrf.getName())) 
    { 
        valor = listaEtiqueta.get(indiceEtiquetaActual).getMAKTX_desc(); 
    }
    else if("MATNR".equals(jrf.getName())) 
    { 
        valor = listaEtiqueta.get(indiceEtiquetaActual).getMATNR(); 
    }
    else if("NAME1".equals(jrf.getName())) 
    { 
        valor = listaEtiqueta.get(indiceEtiquetaActual).getNAME1(); 
    }
    else if("VEMEH".equals(jrf.getName())) 
    { 
        valor = listaEtiqueta.get(indiceEtiquetaActual).getVEMEH(); 
    }
    else if("VEMNG".equals(jrf.getName())) 
    { 
        valor = listaEtiqueta.get(indiceEtiquetaActual).getVEMNG(); 
    }
    else if("WERKS".equals(jrf.getName())) 
    { 
        valor = listaEtiqueta.get(indiceEtiquetaActual).getWERKS(); 
    }
    else if("entrega".equals(jrf.getName())) 
    { 
        valor = listaEtiqueta.get(indiceEtiquetaActual).getEntrega(); 
    }
    else if("ZHUEX_HU".equals(jrf.getName())) 
    { 
        valor = listaEtiqueta.get(indiceEtiquetaActual).getZHUEX_HU(); 
    }
 
    return valor;
    }
    
    public void addEtiqueta(Etiqueta etiqueta){
        
        this.listaEtiqueta.add(etiqueta);
    }
    
}
