package com.bmore.prueba.web.utils.remission;

public class Remision {
	
	String clave;
    String descripcion;
    String cantidad;
    String precioUnitario;

    public Remision(){
        
    }
    
    public Remision(String clave, String descripcion, String cantidad) {
        this.clave = clave;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
    }

    public String getClave() {
        return clave;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

	public String getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(String precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
    
    

}
