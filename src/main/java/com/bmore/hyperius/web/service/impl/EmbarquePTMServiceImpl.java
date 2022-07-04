package com.bmore.hyperius.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.hyperius.web.dto.EntregaDetalleDTO;
import com.bmore.hyperius.web.dto.EntregaInputDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.repository.EmbarquePTRepository;
import com.bmore.hyperius.web.service.EmbarquePTMService;
import com.bmore.hyperius.web.utils.Utils;

@Service
public class EmbarquePTMServiceImpl implements EmbarquePTMService {

	private static final Logger LOCATION = LoggerFactory.getLogger(EmbarquePTMServiceImpl.class);

	@Autowired
	private EmbarquePTRepository embarquePTRepository;

	@Override
	public EntregaInputDTO validarEntrega(EntregaInputDTO entregaInput) {

		// Limpiar para evitar errores con boton next de navegador
		entregaInput.setHu1(null);
		entregaInput.setHu2(null);
		entregaInput.setConfHus(null);

		EntregaInputDTO entregaInput2 = new EntregaInputDTO();
		//EmbarquePTRepositoryOld embarquePTDAO = new EmbarquePTRepositoryOld();

		// entregaInput.setEntrega(Utils.zeroFill(entregaInput.getEntrega(),
		// 10));

		entregaInput2 = embarquePTRepository.validarEntregaPickin(entregaInput);

		ResultDTO resultDT = entregaInput2.getResultDT();

		if (resultDT.getId() == 1) {

			entregaInput.setMateriales(entregaInput2.getMateriales());
			entregaInput = embarquePTRepository.reservaUbicaciones(entregaInput);

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
				resultDT.setId(2);
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
				resultDT.setMsg("Error, revisar las reservaciones de Hus para el usuario: "
						+ entregaInput.getUsuarioMontacarga());
				entregaInput.setNumeroHus(0);
				break;

			}

			// resultDT = embarquePTDAO.reservaUbicacionHU1(entregaInput);

			// if (resultDT.getId() == 1) {
			//
			// entregaInput2 = embarquePTDAO
			// .obtieneReservaUbicacionHU1(entregaInput);
			//
			// resultDT = entregaInput2.getResultDT();
			//
			// if (resultDT.getId() == 1) {// Se obtuvo la ubicacion 1
			//
			// entregaInput.setuOrigen1(entregaInput2.getuOrigen1());
			// entregaInput.setuOrigen2(entregaInput2.getuOrigen2());
			//
			// entregaInput.setMatnr(Utils.zeroClean(entregaInput2
			// .getMatnr()));
			//
			// }
			// }

		}

		LOCATION.error("antes de regresar de BO: " + entregaInput.getMatnr());
		// entregaInput.setResultDT(resultDT);
		return entregaInput;

	}

	@Override
	public EntregaInputDTO pickearHU(EntregaInputDTO entregaInput, int hu1oHu2) {

		//EmbarquePTRepositoryOld embarquePTDAO = new EmbarquePTRepositoryOld();
		ResultDTO resultDT = new ResultDTO();

		switch (hu1oHu2) {

		case 1:

			// evitar back errores en back de navegador
			entregaInput.setHu2(null);

			// evitar doble pickeo
			resultDT = embarquePTRepository.validaPickeoPrevioHU(entregaInput, entregaInput.getHu1());

			if (resultDT.getId() == 1) {

				// obtener info de hu

				EntregaDetalleDTO infoMaterial = new EntregaDetalleDTO();

				infoMaterial = embarquePTRepository.getDataHU(entregaInput.getHu1(), entregaInput.getWerks(),
						entregaInput.getuOrigen1(), entregaInput.getuOrigen2());

				resultDT = infoMaterial.getResultDT();

				if (resultDT.getId() == 1) {
					LOCATION.error("bestq");
					if (infoMaterial.getBestq() == null || entregaInput.getCheckBestq().equals("0")) {// Libre
						// utilizacion

						// Tomara un material valido del zpicking
						LOCATION.error("tomo el material");
						if (entregaInput.getMateriales().get(infoMaterial.getMaterial()) != null) {
							LOCATION.error("!=null");
							entregaInput.setMaktx(infoMaterial.getDescripcion());
							entregaInput.setMatnr(Utils.zeroClean(infoMaterial.getMaterial()));
							entregaInput.setCant(infoMaterial.getCajas());
							entregaInput.setCantT(infoMaterial.getMe());

						} else {
							resultDT.setId(2);
							resultDT.setMsg("El envase no coincide con uno valido en la entrega de salida");
							entregaInput.setHu1(null);
						}
					} else {

						if (infoMaterial.getBestq().equals("Q")) {

							LOCATION.error("ENTRGA EN Q");
							resultDT.setId(10);
							resultDT.setMsg("HU en QA, ¿Desea alimentar este envase?");

						} else if (infoMaterial.getBestq().equals("S")) {

							LOCATION.error("ENTRGA EN S");
							resultDT.setId(2);
							resultDT.setMsg("HU bloqueado, elija otra HU");
							entregaInput.setHu1(null);
						}
					}
				} else {// material no encontrado
					entregaInput.setHu1(null);
				}

			} else {// pickeo previo
				entregaInput.setHu1(null);
			}

			break;
		case 2:

			if (entregaInput.getHu2() == null || entregaInput.getHu2().trim().equals("")) {
				// Si es nula entonces mandar a capturar ubicacion final ya que
				// solo
				// se estara pickeando una hu

				entregaInput.setHu2("");
				resultDT.setId(1);
				resultDT.setMsg("Solo se pickeara una HU");

			} else {
				// validar que HUs sean diferentes
				if (entregaInput.getHu1().equalsIgnoreCase(entregaInput.getHu2())) {
					resultDT.setId(3);
					resultDT.setMsg("HU1 y HU2 SON IGUALES, CAPTURE HUs DIFERENTES");
					entregaInput.setHu2(null);

				} else {

					// ///////7

					EntregaDetalleDTO infoMaterial = new EntregaDetalleDTO();

					infoMaterial = embarquePTRepository.getDataHU(entregaInput.getHu2(), entregaInput.getWerks(),
							entregaInput.getuOrigen1(), entregaInput.getuOrigen2());

					resultDT = infoMaterial.getResultDT();

					if (resultDT.getId() == 1) {

						if (infoMaterial.getBestq() == null || entregaInput.getCheckBestq().equals("0")) {// Libre

							LOCATION.error("MAterial: " + infoMaterial.getMaterial() + "---" + entregaInput.getMatnr());
							if (infoMaterial.getMaterial().equals(Utils.zeroFill(entregaInput.getMatnr(), 18))) {

								// //////////

								// tratar de reservar otro carril

								// evitar doble pickeo
								resultDT = embarquePTRepository.validaPickeoPrevioHU(entregaInput, entregaInput.getHu2());

								if (resultDT.getId() == 1) {
									// resultDT = embarquePTDAO
									// .reservaUbicacionHU2(entregaInput);
									//
									// if (resultDT.getId() == 1) {
									//
									// } else if (resultDT.getId() == 2) {
									// // Capturar ubicacion destino unicamente
									// // de
									// // HU1
									// entregaInput.setHu2(null);
									// resultDT.setId(2);
									// resultDT
									// .setMsg("Continúe solo con una HU");
									// }

								} else {
									entregaInput.setHu2(null);
								}

							} else {// material no encontrado
								entregaInput.setHu2(null);
							}

						} else {
							if (infoMaterial.getBestq().equals("Q")) {
								LOCATION.error("ENTRGA EN Q");
								resultDT.setId(10);
								resultDT.setMsg("HU en QA, ¿Desea alimentar este envase?");

							} else if (infoMaterial.getBestq().equals("S")) {
								LOCATION.error("ENTRGA EN S");
								resultDT.setId(2);
								resultDT.setMsg("HU bloqueado, elija otra HU");
								entregaInput.setHu2(null);
							}
						}

					} else {// material no encontrado
						entregaInput.setHu2(null);
					}

				}
			}

			break;

		}

		entregaInput.setResultDT(resultDT);

		return entregaInput;

	}

	@Override
	public EntregaInputDTO confirmaHusEnCamionFurgon(EntregaInputDTO entregaInput) {

		// EmbarquePTRepositoryOld embarquePTDAO = new EmbarquePTRepositoryOld();

		ResultDTO resultDT = new ResultDTO();

		if (entregaInput.getHu2() == null || entregaInput.getHu2().equals("")) {
			entregaInput.setHu2("");
		}

		resultDT = embarquePTRepository.confirmaHusEnCamionFurgon(entregaInput);

		LOCATION.error("AFTER EXECUTE2: " + resultDT.getId());
		// 2 Error al consumir HU1 en ZPickingEntregaEntrante
		// 3 Error al ingresar HU1 en ZContingencia
		// 4 Error al consumir HU1 en LQUA
		switch (resultDT.getId()) {

		case 1:
			resultDT.setMsg("Las Hus fueron consumidas");
			break;
		case 2:
			resultDT.setMsg("Error al consumir HU1 en ZPickingEntregaEntrante");
			break;
		case 3:
			resultDT.setMsg("Error al ingresar HU1 en ZContingencia");
			break;
		case 4:
			resultDT.setMsg("Error al consumir HU1 en LQUA");
			break;
		}

		// resultDT = embarquePTDAO.confirmaHusEnCamionFurgon(entregaInput,
		// entregaInput.getHu1());
		//
		// if (resultDT.getId() == 1) {
		//
		// if (entregaInput.getHu2() != null
		// && !entregaInput.getHu2().equals("")) {
		//
		// // confirma depa en ZPICKING de HU2
		// resultDT = embarquePTDAO.confirmaHusEnCamionFurgon(
		// entregaInput, entregaInput.getHu2());
		//
		// if (resultDT.getId() == 1) {
		//
		// // Si se confirman las dos HU
		// // ingresar a ZContingencia las dos HUS
		//
		// resultDT = embarquePTDAO.insertProcesoContingencia_8(
		// entregaInput, entregaInput.getHu1());
		//
		// resultDT = embarquePTDAO.insertProcesoContingencia_8(
		// entregaInput, entregaInput.getHu2());
		//
		// // Consumir inventario de LQUA
		//
		// embarquePTDAO.consumeInventario(entregaInput.getHu1(),
		// entregaInput);
		//
		// embarquePTDAO.consumeInventario(entregaInput.getHu2(),
		// entregaInput);
		//
		// }// Falta rollback a confirmar depa hu1
		// else {
		//
		// }
		//
		// } else {
		//
		// resultDT = embarquePTDAO.insertProcesoContingencia_8(
		// entregaInput, entregaInput.getHu1());
		//
		// if (resultDT.getId() == 1) {
		// // Se ingreso HU a ZCONTINGENCIA
		// embarquePTDAO.consumeInventario(entregaInput.getHu1(),
		// entregaInput);
		//
		// } else {
		//
		// }
		//
		// }
		//
		// }

		entregaInput.setResultDT(resultDT);
		return entregaInput;

	}

	@Override
	public ResultDTO limpiarPendientesXUsuario(String vbeln, String user) {

		//EmbarquePTRepositoryOld embarquePTDAO = new EmbarquePTRepositoryOld();

		return embarquePTRepository.limpiaPendientesXUsuario(vbeln, user);
	}
}
