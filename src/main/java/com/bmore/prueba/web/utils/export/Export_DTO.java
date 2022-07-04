package com.bmore.prueba.web.utils.export;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bmore.prueba.web.utils.NumberToLetterConverter;

public class Export_DTO {

	private String fecha;
	private String distribuidor;
	private String pedido;
	private String entrega;
	private String nombreImportador;
	private String nombreDistribuidor;
	private String direccion;
	private String ciudad;
	private String estado;
	private String pais;
	private String frontera;
	private String clave;
	private String descripcion;
	private String precioUnitario;
	private String cantidad;
	private String referenciaImportador;
	private String pedidoImportador;
	private String viaEmbarque;
	private String aduanaSalida;
	private String agenteAduanal;
	private String talonEmbarque;
	private String numeroCaja;
	private String numeroSello;
	private String selloImportador;
	private String litrosEmbarque;
	private String peso;
	private String operador;
	private String total;
	private String totalPrecio;
	private String totalCantidad;
	private String entarimada;
	private String logo;
	private String plantaHeader;
	private String plantaBody;

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getDistribuidor() {
		return distribuidor;
	}

	public void setDistribuidor(String distribuidor) {
		this.distribuidor = distribuidor;
	}

	public String getPedido() {
		return pedido;
	}

	public void setPedido(String pedido) {
		this.pedido = pedido;
	}

	public String getEntrega() {
		return entrega;
	}

	public void setEntrega(String entrega) {
		this.entrega = entrega;
	}

	public String getNombreImportador() {
		return nombreImportador;
	}

	public void setNombreImportador(String nombreImportador) {
		this.nombreImportador = nombreImportador;
	}

	public String getNombreDistribuidor() {
		return nombreDistribuidor;
	}

	public void setNombreDistribuidor(String nombreDistribuidor) {
		this.nombreDistribuidor = nombreDistribuidor;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getFrontera() {
		return frontera;
	}

	public void setFrontera(String frontera) {
		this.frontera = frontera;
	}

	public String getReferenciaImportador() {
		return referenciaImportador;
	}

	public void setReferenciaImportador(String referenciaImportador) {
		this.referenciaImportador = referenciaImportador;
	}

	public String getPedidoImportador() {
		return pedidoImportador;
	}

	public void setPedidoImportador(String pedidoImportador) {
		this.pedidoImportador = pedidoImportador;
	}

	public String getViaEmbarque() {
		return viaEmbarque;
	}

	public void setViaEmbarque(String viaEmbarque) {
		this.viaEmbarque = viaEmbarque;
	}

	public String getAduanaSalida() {
		return aduanaSalida;
	}

	public void setAduanaSalida(String aduanaSalida) {
		this.aduanaSalida = aduanaSalida;
	}

	public String getAgenteAduanal() {
		return agenteAduanal;
	}

	public void setAgenteAduanal(String agenteAduanal) {
		this.agenteAduanal = agenteAduanal;
	}

	public String getTalonEmbarque() {
		return talonEmbarque;
	}

	public void setTalonEmbarque(String talonEmbarque) {
		this.talonEmbarque = talonEmbarque;
	}

	public String getNumeroCaja() {
		return numeroCaja;
	}

	public void setNumeroCaja(String numeroCaja) {
		this.numeroCaja = numeroCaja;
	}

	public String getNumeroSello() {
		return numeroSello;
	}

	public void setNumeroSello(String numeroSello) {
		this.numeroSello = numeroSello;
	}

	public String getSelloImportador() {
		return selloImportador;
	}

	public void setSelloImportador(String selloImportador) {
		this.selloImportador = selloImportador;
	}

	public String getLitrosEmbarque() {
		return litrosEmbarque;
	}

	public void setLitrosEmbarque(String litrosEmbarque) {
		this.litrosEmbarque = litrosEmbarque;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public String getOperador() {
		return operador;
	}

	public void setOperador(String operador) {
		this.operador = operador;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getTotalPrecio() {
		return totalPrecio;
	}

	public void setTotalPrecio(String totalPrecio) {
		this.totalPrecio = totalPrecio;
	}

	public String getTotalCantidad() {
		return totalCantidad;
	}

	public void setTotalCantidad(String totalCantidad) {
		this.totalCantidad = totalCantidad;
	}

	public String getEntarimada() {
		return entarimada;
	}

	public void setEntarimada(String entarimada) {
		this.entarimada = entarimada;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getPlantaHeader() {
		return plantaHeader;
	}

	public void setPlantaHeader(String plantaHeader) {
		this.plantaHeader = plantaHeader;
	}

	public String getPlantaBody() {
		return plantaBody;
	}

	public void setPlantaBody(String plantaBody) {
		this.plantaBody = plantaBody;
	}

	public Export_DTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setTotal(List<String> precioUnitario, List<String> cantidad) {
		BigDecimal resTot = BigDecimal.ZERO;
		BigDecimal resPre = BigDecimal.ZERO;
		for (int i = 0; i < precioUnitario.size() && i < cantidad.size(); i++) {
			BigDecimal precUn = new BigDecimal(precioUnitario.get(i));
			BigDecimal quan = new BigDecimal(cantidad.get(i));
			resTot = resTot.add(precUn.multiply(quan));
			resPre = resPre.add(quan);
		}
		DecimalFormat f = new DecimalFormat("###,###,###,###.00");

		this.total = "$ " + f.format(resTot); 
			//resTot.toString().substring(0,
			//	resTot.toString().indexOf(".") + 3);
		this.totalPrecio =  f.format(resPre); 
			//resPre.toString().substring(0,
			//	resPre.toString().indexOf(".") + 3);
		this.totalCantidad = NumberToLetterConverter
				.convertNumberToLetter(resTot.toString().substring(0,
						resTot.toString().indexOf(".") + 3));
	}

	public Export_DTO(String fecha, String distribuidor, String pedido,
			String entrega, String nombreImportador, String nombreDistribuidor,
			String direccion, String ciudad, String estado, String pais,
			String frontera, String clave, String descripcion,
			String precioUnitario, String cantidad,
			String referenciaImportador, String pedidoImportador,
			String viaEmbarque, String aduanaSalida, String agenteAduanal,
			String talonEmbarque, String numeroCaja, String numeroSello,
			String selloImportador, String litrosEmbarque, String peso,
			String operador, String total, String totalPrecio,
			String totalCantidad, String entarimada, String logo,
			String plantaHeader, String plantaBody) {
		super();
		this.fecha = fecha;
		this.distribuidor = distribuidor;
		this.pedido = pedido;
		this.entrega = entrega;
		this.nombreImportador = nombreImportador;
		this.nombreDistribuidor = nombreDistribuidor;
		this.direccion = direccion;
		this.ciudad = ciudad;
		this.estado = estado;
		this.pais = pais;
		this.frontera = frontera;
		this.clave = clave;
		this.descripcion = descripcion;
		this.precioUnitario = precioUnitario;
		this.cantidad = cantidad;
		this.referenciaImportador = referenciaImportador;
		this.pedidoImportador = pedidoImportador;
		this.viaEmbarque = viaEmbarque;
		this.aduanaSalida = aduanaSalida;
		this.agenteAduanal = agenteAduanal;
		this.talonEmbarque = talonEmbarque;
		this.numeroCaja = numeroCaja;
		this.numeroSello = numeroSello;
		this.selloImportador = selloImportador;
		this.litrosEmbarque = litrosEmbarque;
		this.peso = peso;
		this.operador = operador;
		this.total = total;
		this.totalPrecio = totalPrecio;
		this.totalCantidad = totalCantidad;
		this.entarimada = entarimada;
		this.logo = logo;
		this.plantaHeader = plantaHeader;
		this.plantaBody = plantaBody;
	}

	public Export_DTO(Export_DTO exDto) {
		super();
		List<String> stVoid = new ArrayList<String>();
		List<String> stNVoid = new ArrayList<String>();
		stVoid.add("N/D");
		stNVoid.add("0.00");
		this.fecha = exDto.getFecha() == null ? "" : exDto.getFecha();
		this.distribuidor = exDto.getDistribuidor() == null ? "" : exDto
				.getDistribuidor();
		this.pedido = exDto.getPedido() == null ? "" : exDto.getPedido();
		this.entrega = exDto.getEntrega() == null ? "" : exDto.getEntrega();
		this.nombreImportador = exDto.getNombreImportador() == null ? ""
				: exDto.getNombreImportador();
		this.nombreDistribuidor = exDto.getNombreDistribuidor() == null ? ""
				: exDto.getNombreDistribuidor();
		this.direccion = exDto.getDireccion() == null ? "" : exDto
				.getDireccion();
		this.ciudad = exDto.getCiudad() == null ? "" : exDto.getCiudad();
		this.estado = exDto.getEstado() == null ? "" : exDto.getEstado();
		this.pais = exDto.getPais() == null ? "" : exDto.getPais();
		this.frontera = exDto.getFrontera() == null ? "" : exDto.getFrontera();
		this.clave = exDto.getClave() == null ? "" : exDto.getClave();
		this.descripcion = exDto.getDescripcion() == null ? "" : exDto
				.getDescripcion();
		this.precioUnitario = exDto.getPrecioUnitario() == null ? ""
				: exDto.getPrecioUnitario();
		this.cantidad = exDto.getCantidad() == null ? "" : exDto
				.getCantidad();
		this.referenciaImportador = exDto.getReferenciaImportador() == null ? ""
				: exDto.getReferenciaImportador();
		this.pedidoImportador = exDto.getPedidoImportador() == null ? ""
				: exDto.getPedidoImportador();
		this.viaEmbarque = exDto.getViaEmbarque() == null ? "" : exDto
				.getViaEmbarque();
		this.aduanaSalida = exDto.getAduanaSalida() == null ? "" : exDto
				.getAduanaSalida();
		this.agenteAduanal = exDto.getAgenteAduanal() == null ? "" : exDto
				.getAgenteAduanal();
		this.talonEmbarque = exDto.getTalonEmbarque() == null ? "" : exDto
				.getTalonEmbarque();
		this.numeroCaja = exDto.getNumeroCaja() == null ? "" : exDto
				.getNumeroCaja();
		this.numeroSello = exDto.getNumeroSello() == null ? "" : exDto
				.getNumeroSello();
		this.selloImportador = exDto.getSelloImportador() == null ? "" : exDto
				.getSelloImportador();
		this.litrosEmbarque = exDto.getLitrosEmbarque() == null ? "" : exDto
				.getLitrosEmbarque();
		this.peso = exDto.getPeso() == null ? "" : exDto.getPeso();
		this.operador = exDto.getOperador() == null ? "" : exDto.getOperador();
		this.total = exDto.getTotal() == null ? "" : exDto.getTotal();
		this.totalPrecio = exDto.getTotalPrecio() == null ? "" : exDto
				.getTotalPrecio();
		this.totalCantidad = exDto.getTotalCantidad() == null ? "" : exDto
				.getTotalCantidad();
		this.entarimada = exDto.getEntarimada() == null ? "" : exDto
				.getEntarimada();
		this.logo = exDto.getLogo() == null ? "" : exDto.getLogo();
		this.plantaHeader = exDto.getPlantaHeader() == null ? "" : exDto
				.getPlantaHeader();
		this.plantaBody = exDto.getPlantaBody() == null ? "" : exDto
				.getPlantaBody();
	}

	public static Map<String, Object> Export_DTO_MAP(Export_DTO getExpDto) {
		Export_DTO expDto = new Export_DTO(getExpDto);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("fecha", expDto.getFecha());
		param.put("distribuidor", expDto.getDistribuidor());
		param.put("pedido", expDto.getPedido());
		param.put("entrega", expDto.getEntrega());
		param.put("nombreImportador", expDto.getNombreImportador());
		param.put("nombreDistribuidor", expDto.getNombreDistribuidor());
		param.put("direccion", expDto.getDireccion());
		param.put("ciudad", expDto.getCiudad());
		param.put("estado", expDto.getEstado());
		param.put("pais", expDto.getPais());
		param.put("frontera", expDto.getFrontera());
		param.put("clave", expDto.getClave());
		param.put("descripcion", expDto.getDescripcion());
		param.put("precioUnitario", expDto.getPrecioUnitario());
		param.put("cantidad", expDto.getCantidad());
		param.put("referenciaImportador", expDto.getReferenciaImportador());
		param.put("pedidoImportador", expDto.getPedidoImportador());
		param.put("viaEmbarque", expDto.getViaEmbarque());
		param.put("aduanaSalida", expDto.getAduanaSalida());
		param.put("agenteAduanal", expDto.getAgenteAduanal());
		param.put("talonEmbarque", expDto.getTalonEmbarque());
		param.put("numeroCaja", expDto.getNumeroCaja());
		param.put("numeroSello", expDto.getNumeroSello());
		param.put("selloImportador", expDto.getSelloImportador());
		param.put("litrosEmbarque", expDto.getLitrosEmbarque());
		param.put("peso", expDto.getPeso());
		param.put("operador", expDto.getOperador());
		param.put("total", expDto.getTotal());
		param.put("totalPrecio", expDto.getTotalPrecio());
		param.put("totalCantidad", expDto.getTotalCantidad());
		param.put("entarimada", expDto.getEntarimada());
		param.put("logo", expDto.getLogo());
		param.put("plantaHeader", expDto.getPlantaHeader());
		param.put("plantaBody", expDto.getPlantaBody());

		return param;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(String precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public String getCantidad() {
		return cantidad;
	}

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}

	@Override
	public String toString() {
		return "Export_DTO [fecha=" + fecha + ", distribuidor=" + distribuidor
				+ ", pedido=" + pedido + ", entrega=" + entrega
				+ ", nombreImportador=" + nombreImportador
				+ ", nombreDistribuidor=" + nombreDistribuidor + ", direccion="
				+ direccion + ", ciudad=" + ciudad + ", estado=" + estado
				+ ", pais=" + pais + ", frontera=" + frontera + ", clave="
				+ clave + ", descripcion=" + descripcion + ", precioUnitario="
				+ precioUnitario + ", cantidad=" + cantidad
				+ ", referenciaImportador=" + referenciaImportador
				+ ", pedidoImportador=" + pedidoImportador + ", viaEmbarque="
				+ viaEmbarque + ", aduanaSalida=" + aduanaSalida
				+ ", agenteAduanal=" + agenteAduanal + ", talonEmbarque="
				+ talonEmbarque + ", numeroCaja=" + numeroCaja
				+ ", numeroSello=" + numeroSello + ", selloImportador="
				+ selloImportador + ", litrosEmbarque=" + litrosEmbarque
				+ ", peso=" + peso + ", operador=" + operador + ", total="
				+ total + ", totalPrecio=" + totalPrecio + ", totalCantidad="
				+ totalCantidad + ", entarimada=" + entarimada + ", logo="
				+ logo + ", plantaHeader=" + plantaHeader + ", plantaBody="
				+ plantaBody + "]";
	}

}
