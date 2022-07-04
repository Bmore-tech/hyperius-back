package com.bmore.prueba.mobile.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.prueba.mobile.dto.EntregaDetalleDTO;
import com.bmore.prueba.mobile.dto.EntregaInput;
import com.bmore.prueba.mobile.repository.impl.EmbarquePTDAO;
import com.bmore.prueba.mobile.utils.ResultDT;
import com.bmore.prueba.mobile.utils.Utils;

public class EmbarquePTBO {
	private static final Logger LOCATION = LoggerFactory.getLogger(EmbarquePTBO.class);
	public EntregaInput validarEntrega(EntregaInput entregaInput) throws ClassNotFoundException{
		EntregaInput entregaInput2 = new EntregaInput();
		EmbarquePTDAO embarquePTDAO = new EmbarquePTDAO();
		entregaInput.setEntrega(Utils.zeroFill(entregaInput.getEntrega(), 10));
		entregaInput2 = embarquePTDAO.validarEntregaPickin(entregaInput);
		ResultDT resultDT = entregaInput2.getResultDT();
		entregaInput.setResultDT(resultDT);
		if (resultDT.getId() == 1) {
			entregaInput.setMateriales(entregaInput2.getMateriales());
			entregaInput = embarquePTDAO.reservaUbicaciones(entregaInput);
			resultDT = entregaInput.getResultDT();
			switch (resultDT.getId()) {
			case 1:
				resultDT.setId(1);
				resultDT.setMsg("Se reservaron dos materiales");
				entregaInput.setNumeroHus(2);
				break;
			case 3:
				resultDT.setId(1);
				resultDT.setMsg("Cuenta con una hu reservada");
				entregaInput.setNumeroHus(1);
				break;
			case 4:
				resultDT.setId(1);
				resultDT.setMsg("Cuenta con dos Hus reservadas");
				entregaInput.setNumeroHus(2);
				break;
			case 5:
				resultDT.setId(10);
				resultDT.setMsg("No hay mas hus para reservar");
				entregaInput.setNumeroHus(0);
				break;
			case 6:
				resultDT.setId(1);
				resultDT.setMsg("Se reservo una Hu");
				entregaInput.setNumeroHus(1);
				break;
			case 10:
				resultDT.setId(2);
				resultDT.setMsg("Error, revisar las reservaciones de Hus para el usuario: " + entregaInput.getUsuarioMontacarga());
				entregaInput.setNumeroHus(0);
				break;
			}
		}
		LOCATION.error("antes de regresar de BO: " + entregaInput.getMatnr());
		return entregaInput;
	}
	public EntregaInput pickearHU(EntregaInput entregaInput, int hu1oHu2) throws ClassNotFoundException{
		EmbarquePTDAO embarquePTDAO = new EmbarquePTDAO();
		ResultDT resultDT = new ResultDT();
		switch (hu1oHu2) {
		case 1:
			// evitar doble pickeo
			resultDT = embarquePTDAO.validaPickeoPrevioHU(entregaInput,entregaInput.getHu1());
			LOCATION.info("Material de la entrega: ->"	+ entregaInput.getMatnr());
			if (resultDT.getId() == 1) {
				// obtener info de hu
				EntregaDetalleDTO infoMaterial = new EntregaDetalleDTO();
				infoMaterial = embarquePTDAO.getDataHU(
						entregaInput.getHu1(),
						entregaInput.getWerks(), 
						entregaInput.getuOrigen1(),
						entregaInput.getuOrigen2());
				resultDT = infoMaterial.getResultDT();
				if (resultDT.getId() == 1) {
					if (infoMaterial.getBestq() == null	|| infoMaterial.getBestq().trim().equals("")) {
						// || entregaInput.getCheckBestq() == null
						// || entregaInput.getCheckBestq().equals("0")) {
						// Libre utilizacion
						// Tomara un material valido del zpicking
						LOCATION.info("InfoMaterial.getMaterial="+infoMaterial.getMaterial());
						if (entregaInput.getMateriales().get(infoMaterial.getMaterial()) != null) {
							if (Utils.zeroFill(entregaInput.getMatnr().trim(),18).equals(infoMaterial.getMaterial())) {
								entregaInput.setMaktx(infoMaterial.getDescripcion());
								entregaInput.setMatnr(Utils.zeroClean(infoMaterial.getMaterial()));
								entregaInput.setCant(infoMaterial.getCajas());
								entregaInput.setCantT(infoMaterial.getMe());
							} else {
								resultDT.setId(2);
								resultDT.setMsg("El material no coincide con el solicitado en pantalla: "+ entregaInput.getMatnr());
								entregaInput.setHu1(null);
							}
						} else {
							resultDT.setId(2);
							resultDT.setMsg("El material no coincide con uno valido en la entrega");
							entregaInput.setHu1(null);
						}
					} else {
						if (infoMaterial.getBestq().equals("Q")) {
							resultDT.setId(2);
							resultDT.setMsg("HU bloqueado, elija otra HU");
							entregaInput.setHu1(null);
						} else if (infoMaterial.getBestq().equals("S")) {
							resultDT.setId(2);
							resultDT.setMsg("HU bloqueado, elija otra HU");
							entregaInput.setHu1(null);
						} else {
							resultDT.setId(2);
							resultDT.setMsg("Error al valida STATUS Q, llamar a War Room");
							entregaInput.setHu1(null);
						}
					}
				} else {// material no encontrado
					entregaInput.setHu1(null);
				}

			} else {//
				entregaInput.setHu1(null);
			}
			break;
		case 2:
			// validar que HUs sean diferentes
			if (entregaInput.getHu1().equalsIgnoreCase(entregaInput.getHu2())) {
				resultDT.setId(3);
				resultDT.setMsg("HU1 y HU2 son iguales, capture Hus diferentes");
				entregaInput.setHu2(null);
			} else {
				EntregaDetalleDTO infoMaterial = new EntregaDetalleDTO();
				infoMaterial = embarquePTDAO.getDataHU(entregaInput.getHu2(),entregaInput.getWerks(), entregaInput.getuOrigen1(),entregaInput.getuOrigen2());
				resultDT = infoMaterial.getResultDT();
				if (resultDT.getId() == 1) {
					if (infoMaterial.getBestq() == null
							|| infoMaterial.getBestq().trim().equals("")) {
						// Libre
						if (infoMaterial.getMaterial().equals(Utils.zeroFill(entregaInput.getMatnr(), 18))) {
							// evitar doble pickeo
							resultDT = embarquePTDAO.validaPickeoPrevioHU(entregaInput, entregaInput.getHu2());
							if (resultDT.getId() != 1) {
								entregaInput.setHu2(null);
							}
						} else {// material no encontrado
							entregaInput.setHu2(null);
							resultDT.setId(2);
							resultDT.setMsg("El material del hu1 no coincide con el material del hu2, debe seleccionar el mismo material");
						}
					} else {
						if (infoMaterial.getBestq().equals("Q")) {		
							resultDT.setId(2);
							resultDT.setMsg("HU bloqueado, elija otra HU");
							entregaInput.setHu2(null);
						} else if (infoMaterial.getBestq().equals("S")) {
							resultDT.setId(2);
							resultDT.setMsg("HU bloqueado, elija otra HU");
							entregaInput.setHu2(null);
						} else {
							resultDT.setId(2);
							resultDT.setMsg("Error al valida STATUS Q, llamar a War Room");
							entregaInput.setHu1(null);
						}
					}
				} else {// material no encontrado
					entregaInput.setHu2(null);
				}
			}
			break;
		}
		entregaInput.setResultDT(resultDT);
		return entregaInput;
	}
	public EntregaInput confirmaHusEnCamionFurgon(EntregaInput entregaInput) throws ClassNotFoundException{
		EmbarquePTDAO embarquePTDAO = new EmbarquePTDAO();
		ResultDT resultDT = new ResultDT();
		if (entregaInput.getHu2() == null || entregaInput.getHu2().equals("")) {
			entregaInput.setHu2("");
		}
		resultDT = embarquePTDAO.confirmaHusEnCamionFurgon(entregaInput);
		LOCATION.info("AFTER EXECUTE2: " + resultDT.getId());
		// 2 Error al consumir HU1 en ZPickingEntregaEntrante
		// 3 Error al ingresar HU1 en ZContingencia
		// 4 Error al consumir HU1 en LQUA
		switch (resultDT.getId()) {
		case 1:
			resultDT.setMsg("Las Hus fueron consumidas");
			break;
		case 2:
			resultDT.setMsg("Las Hus fueron consumidas");
			resultDT.setId(1);
			break;
		case 3:
			resultDT.setMsg("Error al ingresar HU1 en ZContingencia, registro repedito");
			break;
		case 4:
			resultDT.setMsg("Error al ingresar HU a ZContingencia");
			break;
		case 5:
			resultDT.setMsg("Error al procesar la segunda hu, intente nuevamente");
			break;
		case 6:
			resultDT.setMsg("Error al consumir inventario");
			break;
		}
		//resultDT.setMsg("Error al consumir HU1 en ZPickingEntregaEntrante");
		entregaInput.setResultDT(resultDT);
		return entregaInput;
	}
	public ResultDT limpiarPendientesXUsuario(String vbeln, String user) throws ClassNotFoundException{
		EmbarquePTDAO embarquePTDAO = new EmbarquePTDAO();
		return embarquePTDAO.limpiaPendientesXUsuario(vbeln, user);
	}
	public static String getWerks(String entrega) throws ClassNotFoundException{
		LOCATION.info("getWerksBO");
		EmbarquePTDAO embarquePTDAO = new EmbarquePTDAO();
		return embarquePTDAO.getWerks(entrega);
	}
}