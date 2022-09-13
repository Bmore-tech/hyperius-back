package com.bmore.hyperius.mobile.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.mobile.dto.OrdenProduccionDetalleDTO;
import com.bmore.hyperius.mobile.dto.OrdenProduccionInput;
import com.bmore.hyperius.mobile.repository.impl.AlimentacionLineaDAO;
import com.bmore.hyperius.mobile.utils.ResultDT;
import com.bmore.hyperius.mobile.utils.Utils;

@Service
public class AlimentacionLineaBO {
	private static final Logger LOCATION = LoggerFactory.getLogger(AlimentacionLineaBO.class);

  @Autowired
  private AlimentacionLineaDAO alimentacionLineaDAO;

	public OrdenProduccionInput validaOrdenProduccion(OrdenProduccionInput ordenProduccionInput) throws ClassNotFoundException {
		// AlimentacionLineaDAO alimentacionLineaDAO = new AlimentacionLineaDAO();
		ResultDT resultDT = new ResultDT();
		ordenProduccionInput.setOrdeProduccion(Utils.zeroFill(ordenProduccionInput.getOrdeProduccion(), 12).trim());
		ordenProduccionInput.setWerks(alimentacionLineaDAO.getWerks(ordenProduccionInput.getOrdeProduccion().trim()));
		resultDT = alimentacionLineaDAO.validaOrden(ordenProduccionInput);
		if (resultDT.getId() == 1) {
			ordenProduccionInput.setMatnr(resultDT.getTypeS());
			LOCATION.info("Material despues de DAO: " + ordenProduccionInput.getMatnr());
			LOCATION.info("Orden " + ordenProduccionInput.getOrdeProduccion());
			LOCATION.info("Usuario "+ ordenProduccionInput.getUsuarioMontacarga());
			LOCATION.info("Werk " + ordenProduccionInput.getWerks());
			ordenProduccionInput = alimentacionLineaDAO.reservaUbicaciones(ordenProduccionInput);
			resultDT = ordenProduccionInput.getResultDT();
			switch (resultDT.getId()) {
			case 1:
				resultDT.setId(1);
				resultDT.setMsg("Se reservaron dos materiales");
				ordenProduccionInput.setNumeroHus(2);
				break;
			case 3:
				resultDT.setId(1);
				resultDT.setMsg("Cuenta con una hu reservada");
				ordenProduccionInput.setNumeroHus(1);
				break;
			case 4:
				resultDT.setId(1);
				resultDT.setMsg("Cuenta con dos Hus reservadas");
				ordenProduccionInput.setNumeroHus(2);
				break;
			case 5:
				resultDT.setId(10);
				resultDT.setMsg("No hay mas hus para reservar");
				ordenProduccionInput.setNumeroHus(0);
				break;
			case 6:
				resultDT.setId(1);
				resultDT.setMsg("Se reservo una Hu");
				ordenProduccionInput.setNumeroHus(1);
				break;
			case 10:
				resultDT.setId(2);
				resultDT.setMsg("Error, revisar las reservaciones de Hus para el usuario: "	+ ordenProduccionInput.getUsuarioMontacarga());
				ordenProduccionInput.setNumeroHus(0);
				break;
			}
		}
		ordenProduccionInput.setResultDT(resultDT);
		LOCATION.error("antes de regresar de BO: "	+ ordenProduccionInput.getMatnr());
		return ordenProduccionInput;
	}
	public static OrdenProduccionInput pickearHU(OrdenProduccionInput ordenProduccionInput, int hu1oHu2) throws ClassNotFoundException {
		AlimentacionLineaDAO alimentacionEnvaseDAO = new AlimentacionLineaDAO();
		ResultDT resultDT = new ResultDT();
		LOCATION.info("PickearHu");
		switch (hu1oHu2) {
		case 1:
			LOCATION.info("PickearHuCaso1hu");
			// evitar doble pickeo
			resultDT = alimentacionEnvaseDAO.validaPickeoPrevioHU(ordenProduccionInput, ordenProduccionInput.getHu1());
			if (resultDT.getId() == 1) {
				// obtener info de hu
				OrdenProduccionDetalleDTO infoMaterial = new OrdenProduccionDetalleDTO();
				infoMaterial = alimentacionEnvaseDAO.getDataHU(ordenProduccionInput.getHu1(), ordenProduccionInput.getWerks(),
														ordenProduccionInput.getuOrigen1(),ordenProduccionInput.getuOrigen2());
				resultDT = infoMaterial.getResultDT();
				if (resultDT.getId() == 1) {
					// Verificar caldidad o bloqueo
				//LOCATION.error("HU1: " + infoMaterial.toString());
					if (infoMaterial.getMaterial().equals(Utils.zeroFill(ordenProduccionInput.getMatnr(), 18))) {
						ordenProduccionInput.setMaktx(infoMaterial.getDescripcion());
						ordenProduccionInput.setMatnr(infoMaterial.getMaterial());
						ordenProduccionInput.setCant(infoMaterial.getCajas());
						ordenProduccionInput.setCantT(infoMaterial.getMe());
						ordenProduccionInput.setOrdeProduccion(Utils.zeroFill(ordenProduccionInput.getOrdeProduccion(), 12));
						// trata de obtener depa
						OrdenProduccionInput ordenProduccionInput2 = new OrdenProduccionInput();
						ordenProduccionInput2 = alimentacionEnvaseDAO.obtieneDepaletizadora(ordenProduccionInput);
						resultDT = ordenProduccionInput2.getResultDT();
						if (resultDT.getId() == 1) {// depa encontrada
							if (ordenProduccionInput2.getuDestino2() == null) {
								ordenProduccionInput.setuDestino2("DPL");
							} else {
								ordenProduccionInput.setuDestino2(ordenProduccionInput2.getuDestino2());
							}
							if (!(infoMaterial.getBestq() == null || (ordenProduccionInput.getCheckBestq() != null && 
									ordenProduccionInput.getCheckBestq().equals("0")))) {
								if (infoMaterial.getBestq().equals("Q")) {
//									resultDT.setId(10);
//									resultDT.setMsg("HU en QA, ¿Desea alimentar este material?");
									// resultDT.setId(2);
									// resultDT.setMsg("HU bloqueado, elija otra HU");
								} else if (infoMaterial.getBestq().equals("S")) {
									resultDT.setId(2);
									resultDT.setMsg("HU bloqueado, elija otra HU");
								}
							}
						}
					} else {
						resultDT.setId(2);
						resultDT.setMsg("El material no coincide con el de la orden de producción");
					}
				} else {// material no encontrado
					ordenProduccionInput.setHu1(null);
				}
			} else {// hu pickeada previamente
				ordenProduccionInput.setHu1(null);
			}
			break;
		case 2:
			LOCATION.info("PickearHuCaso1hu");
			if (ordenProduccionInput.getHu1().equalsIgnoreCase(ordenProduccionInput.getHu2())) {
				resultDT.setId(2);
				resultDT.setMsg("HU1 y HU2 son iguales, capture Hus diferentes");
				ordenProduccionInput.setHu2(null);
			} else {
				// evitar doble pickeo
				resultDT = alimentacionEnvaseDAO.validaPickeoPrevioHU(ordenProduccionInput, ordenProduccionInput.getHu1());
				if (resultDT.getId() == 1) {
					OrdenProduccionDetalleDTO infoMaterial = new OrdenProduccionDetalleDTO();
					infoMaterial = alimentacionEnvaseDAO.getDataHU(ordenProduccionInput.getHu2(), ordenProduccionInput.getWerks(),
															ordenProduccionInput.getuOrigen1(), ordenProduccionInput.getuOrigen2());
					resultDT = infoMaterial.getResultDT();
					LOCATION.info("HU2: " + infoMaterial.toString());
					if (resultDT.getId() == 1) {
						if (infoMaterial.getMaterial().equals(Utils.zeroFill(ordenProduccionInput.getMatnr(),18))) {
							// evitar doble pickeo
							resultDT = alimentacionEnvaseDAO.validaPickeoPrevioHU(ordenProduccionInput,ordenProduccionInput.getHu2());
							if (resultDT.getId() != 1) {
								ordenProduccionInput.setHu2(null);
							}
							if (!(infoMaterial.getBestq() == null || (ordenProduccionInput.getCheckBestq() != null && 
									ordenProduccionInput.getCheckBestq().equals("0")))) {
								if (infoMaterial.getBestq().equals("Q")) {
//									resultDT.setId(10);
//									resultDT.setMsg("HU en QA, Â¿Desea alimentar este envase?");
									// resultDT.setId(2);
									// resultDT.setMsg("HU bloqueado, elija otra HU");
									// ordenProduccionInput.setHu2(null);
								} else if (infoMaterial.getBestq().equals("S")) {
									resultDT.setId(2);
									resultDT.setMsg("HU bloqueado, elija otra HU");
									ordenProduccionInput.setHu2(null);
								}
							}
						} else {// material no encontrado
							ordenProduccionInput.setHu2(null);
							resultDT.setId(2);
							resultDT.setMsg("El material no coincide con el de la orden de producción");
						}
					} else {// material no encontrado
						ordenProduccionInput.setHu2(null);
					}

				} else {// hu pickeada previamente
					ordenProduccionInput.setHu1(null);
				}
			}
			break;
		}
		ordenProduccionInput.setResultDT(resultDT);
		return ordenProduccionInput;
	}
	public static OrdenProduccionInput confirmaHusEnDepa(OrdenProduccionInput ordenProduccionInput) throws ClassNotFoundException {
		AlimentacionLineaDAO alimentacionLineaDAO = new AlimentacionLineaDAO();
		ResultDT resultDT = new ResultDT();
		if (ordenProduccionInput.getuOrigen0().trim().equals("ML2")) {
			ordenProduccionInput.setLgort("LV02");
		} else {
			ordenProduccionInput.setLgort("LV01");
		}
		if (ordenProduccionInput.getHu2() == null  || ordenProduccionInput.getHu2().equals("")) {
			ordenProduccionInput.setHu2("");
		}
		resultDT = alimentacionLineaDAO.confirmaHUsenDepa(ordenProduccionInput);
		//LOCATION.error("AFTER EXECUTE2: " + resultDT.getId());
		switch (resultDT.getId()) {
		case 1:
			resultDT.setMsg("Las Hus fueron consumidas");
			break;
		case 2:
			resultDT.setId(1);
			resultDT.setMsg("Las Hus fueron consumidas");
			break;
		case 3:
			resultDT.setMsg("Error al ingresar hu a Zcontingencia, registro repetido");
			break;
		case 4:
			resultDT.setMsg("Error al ingresar hu a Zcontingencia");
			break;

		case 5:
			resultDT.setMsg("Error al consumir la HU2, intente nuevamente");
			break;
		case 6:
			resultDT.setMsg("Error al consumir inventario");
			break;
		}
		ordenProduccionInput.setResultDT(resultDT);
		return ordenProduccionInput;
	}
	public static ResultDT limpiarPendientesXUsuario(String vbeln, String user) throws ClassNotFoundException {
		LOCATION.error("Vblen: " + vbeln);
		LOCATION.error("User: " + user);
		AlimentacionLineaDAO alimentacionLineaDAO = new AlimentacionLineaDAO();
		return alimentacionLineaDAO.limpiaPendientesXUsuario(vbeln, user);
	}
	public String getLGPLA(String hu) {
		return alimentacionLineaDAO.getLGPLA(hu);
	}
}