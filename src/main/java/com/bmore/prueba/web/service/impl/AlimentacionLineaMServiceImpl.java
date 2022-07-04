package com.bmore.prueba.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmore.prueba.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.prueba.web.dto.OrdenProduccionInputDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.repository.AlimentacionLineaRepository;
import com.bmore.prueba.web.service.AlimentacionLineaMService;
import com.bmore.prueba.web.utils.Utils;

@Service
public class AlimentacionLineaMServiceImpl implements AlimentacionLineaMService {

	private final static Logger LOCATION = LoggerFactory.getLogger(AlimentacionLineaMServiceImpl.class);

	@Autowired
	private AlimentacionLineaRepository alimentacionLineaRepository;

	@Override
	public OrdenProduccionInputDTO validaOrdenProduccion(OrdenProduccionInputDTO ordenProduccionInput) {

		// Limpiar para evitar errores con boton next de navegador
		ordenProduccionInput.setHu1(null);
		ordenProduccionInput.setHu2(null);
		ordenProduccionInput.setConfUbicacionDestino(null);

		OrdenProduccionInputDTO ordenProduccionInput2 = new OrdenProduccionInputDTO();

		ordenProduccionInput.setOrdeProduccion(Utils.zeroFill(ordenProduccionInput.getOrdeProduccion(), 12));

		ResultDTO resultDT = alimentacionLineaRepository.validarEntregaPickin(ordenProduccionInput);

		if (resultDT.getId() == 1) {
			ordenProduccionInput.setMatnr(resultDT.getTypeS());
			LOCATION.error("Material despues de DAO: " + ordenProduccionInput.getMatnr());

			int i = alimentacionLineaRepository.reservaUbicacionHU1(ordenProduccionInput);

			if (i == 1) {

				ordenProduccionInput2 = alimentacionLineaRepository.obtieneReservaUbicacionHU1(ordenProduccionInput);

				resultDT = ordenProduccionInput2.getResultDT();

				if (i == 1) {// Se obtuvo la ubicacion 1

					ordenProduccionInput.setuOrigen0(ordenProduccionInput2.getuOrigen0());

					ordenProduccionInput.setuOrigen1(ordenProduccionInput2.getuOrigen1());
					ordenProduccionInput.setuOrigen2(ordenProduccionInput2.getuOrigen2());
				}
			}
		}

		LOCATION.error("antes de regresar de BO: " + ordenProduccionInput.getMatnr());
		ordenProduccionInput.setResultDT(resultDT);
		return ordenProduccionInput;
	}

	@Override
	public OrdenProduccionInputDTO pickearHU(OrdenProduccionInputDTO ordenProduccionInput, int hu1oHu2) {
		ResultDTO resultDT = new ResultDTO();

		switch (hu1oHu2) {

		case 1:

			// evitar back errores en back de navegador
			ordenProduccionInput.setHu2(null);

			// evitar doble pickeo
			resultDT = alimentacionLineaRepository.validaPickeoPrevioHU(ordenProduccionInput,
					ordenProduccionInput.getHu1());

			if (resultDT.getId() == 1) {

				// obtener info de hu

				OrdenProduccionDetalleDTO infoMaterial = new OrdenProduccionDetalleDTO();

				infoMaterial = alimentacionLineaRepository.getDataHU(ordenProduccionInput.getHu1(),
						ordenProduccionInput.getWerks(), ordenProduccionInput.getuOrigen1(),
						ordenProduccionInput.getuOrigen2());

				resultDT = infoMaterial.getResultDT();

				if (resultDT.getId() == 1) {

					// Verificar caldidad o bloqueo

					if (infoMaterial.getBestq() == null || ordenProduccionInput.getCheckBestq().equals("0")) {// Libre
						// utilizacion

						LOCATION.error(
								"MAterial: " + infoMaterial.getMaterial() + "---" + ordenProduccionInput.getMatnr());
						if (infoMaterial.getMaterial().equals(ordenProduccionInput.getMatnr())) {

							ordenProduccionInput.setMaktx(infoMaterial.getDescripcion());
							ordenProduccionInput.setMatnr(infoMaterial.getMaterial());
							ordenProduccionInput.setCant(infoMaterial.getCajas());
							ordenProduccionInput.setCantT(infoMaterial.getMe());

							// trata de obtener depa

							OrdenProduccionInputDTO ordenProduccionInput2 = new OrdenProduccionInputDTO();

							ordenProduccionInput2 = alimentacionLineaRepository
									.obtieneDepaletizadora(ordenProduccionInput);

							resultDT = ordenProduccionInput2.getResultDT();

							if (resultDT.getId() == 1) {// depa encontrada

								// if (ordenProduccionInput.getWerks().equals(
								// "PC01")) {
								// ordenProduccionInput.setuDestino2("");
								if (ordenProduccionInput2.getuDestino2() == null) {
									ordenProduccionInput.setuDestino2("DPL");
								} else {
									ordenProduccionInput.setuDestino2(ordenProduccionInput2.getuDestino2());
								}
							} else {
								ordenProduccionInput.setHu1(null);
							}

						} else {
							resultDT.setId(2);
							resultDT.setMsg("El envase no coincide con el de la orden de producción");
							ordenProduccionInput.setHu1(null);
						}

					} else {
						if (infoMaterial.getBestq().equals("Q")) {

							resultDT.setId(10);
							resultDT.setMsg("HU en QA, ¿Desea alimentar este envase?");

						} else if (infoMaterial.getBestq().equals("S")) {
							resultDT.setId(2);
							resultDT.setMsg("HU bloqueado, elija otra HU");
							ordenProduccionInput.setHu1(null);
						}
					}

				} else {// material no encontrado
					ordenProduccionInput.setHu1(null);
				}

			} else {// material no encontrado
				ordenProduccionInput.setHu1(null);
			}

			break;
		case 2:

			if (ordenProduccionInput.getHu2() == null || ordenProduccionInput.getHu2().trim().equals("")) {
				// Si es nula entonces mandar a capturar ubicacion final ya que
				// solo
				// se estara pickeando una hu

				ordenProduccionInput.setHu2("");
				resultDT.setId(1);
				resultDT.setMsg("Solo se pickeara una HU");

			} else {
				// validar que HUs sean diferentes
				if (ordenProduccionInput.getHu1().equalsIgnoreCase(ordenProduccionInput.getHu2())) {
					resultDT.setId(3);
					resultDT.setMsg("HU1 y HU2 son iguales, capture Hus diferentes");
					ordenProduccionInput.setHu2(null);

				} else {

					// ///////7

					OrdenProduccionDetalleDTO infoMaterial = new OrdenProduccionDetalleDTO();

					infoMaterial = alimentacionLineaRepository.getDataHU(ordenProduccionInput.getHu2(),
							ordenProduccionInput.getWerks(), ordenProduccionInput.getuOrigen1(),
							ordenProduccionInput.getuOrigen2());

					resultDT = infoMaterial.getResultDT();

					if (resultDT.getId() == 1) {

						if (infoMaterial.getBestq() == null || ordenProduccionInput.getCheckBestq().equals("0")) {// Libre
							// utilizacion

							LOCATION.error("MAterial: " + infoMaterial.getMaterial() + "---"
									+ ordenProduccionInput.getMatnr());
							if (infoMaterial.getMaterial().equals(ordenProduccionInput.getMatnr())) {

								// //////////

								// tratar de reservar otro carril

								// evitar doble pickeo
								resultDT = alimentacionLineaRepository.validaPickeoPrevioHU(ordenProduccionInput,
										ordenProduccionInput.getHu2());

								if (resultDT.getId() == 1) {

									if (alimentacionLineaRepository.reservaUbicacionHU2(ordenProduccionInput) == 2) {
										// Capturar ubicacion destino unicamente
										// de
										// HU1
										ordenProduccionInput.setHu2(null);
										resultDT.setId(2);
										resultDT.setMsg("Continúe solo con una HU");
									}

								} else {
									ordenProduccionInput.setHu2(null);
								}

							} else {// material no encontrado
								ordenProduccionInput.setHu2(null);
							}

						} else {
							if (infoMaterial.getBestq().equals("Q")) {

								resultDT.setId(10);
								resultDT.setMsg("HU en QA, ¿Desea alimentar este envase?");

							} else if (infoMaterial.getBestq().equals("S")) {
								resultDT.setId(2);
								resultDT.setMsg("HU bloqueado, elija otra HU");
								ordenProduccionInput.setHu2(null);
							}
						}

					} else {// material no encontrado
						ordenProduccionInput.setHu2(null);
					}

				}
			}

			break;

		}

		ordenProduccionInput.setResultDT(resultDT);

		return ordenProduccionInput;

	}

	@Override
	public OrdenProduccionInputDTO confirmaHusEnDepa(OrdenProduccionInputDTO ordenProduccionInput) {
		ResultDTO resultDT = new ResultDTO();

		resultDT.setId(alimentacionLineaRepository.confirmaHUenDepa(ordenProduccionInput, ordenProduccionInput.getHu1()));

		if (ordenProduccionInput.getuOrigen0().trim().equals("ML2")) {
			ordenProduccionInput.setLgort("LV02");
		} else {
			ordenProduccionInput.setLgort("LV01");
		}

		if (alimentacionLineaRepository.confirmaHUenDepa(ordenProduccionInput, ordenProduccionInput.getHu1()) == 1) {

			if (ordenProduccionInput.getHu2() != null && !ordenProduccionInput.getHu2().equals("")) {

				// confirma depa en ZPICKING de HU2
				resultDT.setId(alimentacionLineaRepository.confirmaHUenDepa(ordenProduccionInput,
						ordenProduccionInput.getHu2()));

				if (resultDT.getId() == 1) {

					// Si se confirman las dos HU
					// ingresar a ZContingencia las dos HUS

					resultDT.setId(alimentacionLineaRepository.insertProcesoContingencia_5(ordenProduccionInput,
							ordenProduccionInput.getHu1()));

					resultDT.setId(alimentacionLineaRepository.insertProcesoContingencia_5(ordenProduccionInput,
							ordenProduccionInput.getHu2()));

					// Consumir inventario de LQUA

					alimentacionLineaRepository.consumeInventario(ordenProduccionInput.getHu1(), ordenProduccionInput);

					alimentacionLineaRepository.consumeInventario(ordenProduccionInput.getHu2(), ordenProduccionInput);

				} // Falta rollback a confirmar depa hu1
			} else {
				resultDT.setId(alimentacionLineaRepository.insertProcesoContingencia_5(ordenProduccionInput,
						ordenProduccionInput.getHu1()));

				if (resultDT.getId() == 1) {
					// Se ingreso HU a ZCONTINGENCIA
					alimentacionLineaRepository.consumeInventario(ordenProduccionInput.getHu1(), ordenProduccionInput);
				}
			}
		}
		ordenProduccionInput.setResultDT(resultDT);

		return ordenProduccionInput;
	}

	@Override
	public int limpiarPendientesXUsuario(String vbeln, String user) {
		return alimentacionLineaRepository.limpiaPendientesXUsuario(vbeln, user);
	}
}
