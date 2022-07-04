package com.bmore.prueba.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bmore.prueba.web.dto.CarrilUbicacionDTO;
import com.bmore.prueba.web.dto.OrdenProduccionDetalleDTO;
import com.bmore.prueba.web.dto.OrdenProduccionInputDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.repository.old.UbicacionPTRepository;
import com.bmore.prueba.web.service.EntradaPTMService;

@Service
public class EntradaPTMServiceImpl implements EntradaPTMService {

	private static final Logger LOCATION = LoggerFactory.getLogger(EntradaPTMServiceImpl.class);

	@Override
	public OrdenProduccionInputDTO validaOrdenProduccion(OrdenProduccionInputDTO OrdenProduccionInput) {

		// // Limpiar para evitar errores con boton next de navegador
		// OrdenProduccionInput.setHu1(null);
		// OrdenProduccionInput.setHu2(null);
		// OrdenProduccionInput.setConfHus(null);

		UbicacionPTRepository UbicacionPTDAO = new UbicacionPTRepository();
		OrdenProduccionInputDTO orden = UbicacionPTDAO.validarOrdenEnPickin(OrdenProduccionInput.getOrdeProduccion());

		return orden;
	}

	@Override
	public OrdenProduccionInputDTO pickearHU(OrdenProduccionInputDTO OrdenProduccionInput, int hu1oHu2) {

		UbicacionPTRepository UbicacionPTDAO = new UbicacionPTRepository();
		ResultDTO resultDT = new ResultDTO();

		switch (hu1oHu2) {

		case 1:

			// evitar back errores en back de navegador
			OrdenProduccionInput.setHu2(null);

			LOCATION.error("Werks en EntraPtBO:" + OrdenProduccionInput.getWerks());

			resultDT = UbicacionPTDAO.getAUFNRFromHu(OrdenProduccionInput.getHu1(), OrdenProduccionInput.getWerks());
			LOCATION.error("despues:");
			if (resultDT.getId() == 1) {// entrega encontrada
				LOCATION.error("orden encontrada:");
				OrdenProduccionInput.setOrdeProduccion(resultDT.getTypeS());

				OrdenProduccionInputDTO entregaReturn2 = new OrdenProduccionInputDTO();

				entregaReturn2 = validaOrdenProduccion(OrdenProduccionInput);

				resultDT = entregaReturn2.getResultDT();

				if (resultDT.getId() == 1) {// Entrega en picking
					// obtener info de hu

					OrdenProduccionDetalleDTO infoMaterial = new OrdenProduccionDetalleDTO();
					LOCATION.error("get data hu");
					infoMaterial = UbicacionPTDAO.getDataHU(OrdenProduccionInput.getHu1());
					LOCATION.error("get data hu ok");
					resultDT = infoMaterial.getResultDT();

					if (resultDT.getId() == 1
							&& entregaReturn2.getMateriales().get(infoMaterial.getMaterial()) != null) {

						OrdenProduccionInput.setMaktx(infoMaterial.getDescripcion());
						OrdenProduccionInput.setMatnr(infoMaterial.getMaterial());
						OrdenProduccionInput.setCant(infoMaterial.getCajas());
						OrdenProduccionInput.setCantT(infoMaterial.getMe());
						OrdenProduccionInput.setTarima(infoMaterial.getTarima());

						// reservar carril
						CarrilUbicacionDTO ubicacionCarril = new CarrilUbicacionDTO();
						LOCATION.error("reservando: " + OrdenProduccionInput.getOrdeProduccion() + " "
								+ OrdenProduccionInput.getHu1());
						ubicacionCarril = UbicacionPTDAO.consultReservaCarrilHu(
								OrdenProduccionInput.getOrdeProduccion(), OrdenProduccionInput.getHu1());
						resultDT = ubicacionCarril.getResultDT();
						LOCATION.error("primer reserva carril: " + resultDT.getId());
						if (resultDT.getId() == 0) {// consulta reserva carril
							LOCATION.error("no hay reservando carril");

							resultDT = UbicacionPTDAO.reservarCarrilHU(OrdenProduccionInput.getOrdeProduccion(),
									OrdenProduccionInput.getHu1(), OrdenProduccionInput.getMatnr());

							if (resultDT.getId() == 1) {

								ubicacionCarril = new CarrilUbicacionDTO();
								ubicacionCarril = UbicacionPTDAO.consultReservaCarrilHu(
										OrdenProduccionInput.getOrdeProduccion(), OrdenProduccionInput.getHu1());

								resultDT = ubicacionCarril.getResultDT();
								if (resultDT.getId() == 1) {

									OrdenProduccionInput.setuDestino0(ubicacionCarril.getLgnum());

									OrdenProduccionInput.setuDestino1(ubicacionCarril.getLgtyp());
									OrdenProduccionInput.setuDestino2(ubicacionCarril.getLgpla());
								}

							} else {
								// no fue posible obtener reserva de carril
								OrdenProduccionInput.setHu1(null);
							}

						} else if (resultDT.getId() == 1) {
							LOCATION.error("status: " + resultDT.getTypeS() + (resultDT.getTypeS() == null));
							if (resultDT.getTypeS() == null) {

								OrdenProduccionInput.setuDestino0(ubicacionCarril.getLgnum());

								OrdenProduccionInput.setuDestino1(ubicacionCarril.getLgtyp());
								OrdenProduccionInput.setuDestino2(ubicacionCarril.getLgpla());
							} else {
								OrdenProduccionInput.setHu1(null);
								resultDT.setId(2);
								resultDT.setMsg("HU ingresada previamente");
							}

						} else {
							OrdenProduccionInput.setHu1(null);
						}
					} else {// material no encontrado
						OrdenProduccionInput.setHu1(null);
					}

				} else {// entrega no esta en picking
					OrdenProduccionInput.setHu1(null);
				}

			} else {// entrega no encontrada
				OrdenProduccionInput.setHu1(null);
			}

			break;
		case 2:

			if (OrdenProduccionInput.getHu2() == null || OrdenProduccionInput.getHu2().trim().equals("")) {
				// Si es nula entonces mandar a capturar ubicacion ya que solo
				// se estara pickeando una hu

				OrdenProduccionInput.setHu2("");
				resultDT.setId(1);
				resultDT.setMsg("Solo se pickeara una HU");

			} else {
				// validar que HUs sean diferentes
				if (OrdenProduccionInput.getHu1().equalsIgnoreCase(OrdenProduccionInput.getHu2())) {
					resultDT.setId(3);
					resultDT.setMsg("HU1 y HU2 SON IGUALES, CAPTURE HUs DIFERENTES");
					OrdenProduccionInput.setHu2(null);

				} else {

					LOCATION.error("VBLEN:");

					resultDT = UbicacionPTDAO.getAUFNRFromHu(OrdenProduccionInput.getHu2(),
							OrdenProduccionInput.getWerks());
					LOCATION.error("despues:");
					if (resultDT.getId() == 1) {// entrega encontrada
						LOCATION.error("entrega encontrada:");

						// validar sean de la misma entrega

						if (resultDT.getTypeS().equals(OrdenProduccionInput.getOrdeProduccion())) {

							// obtener info de hu

							OrdenProduccionDetalleDTO infoMaterial = new OrdenProduccionDetalleDTO();
							LOCATION.error("get data hu");
							infoMaterial = UbicacionPTDAO.getDataHU(OrdenProduccionInput.getHu2());
							LOCATION.error("get data hu ok");
							resultDT = infoMaterial.getResultDT();

							if (resultDT.getId() == 1) {// Existe material

								if (OrdenProduccionInput.getMatnr().equals(infoMaterial.getMaterial())) {

									// OrdenProduccionInput.setMaktx(infoMaterial
									// .getDescripcion());
									// OrdenProduccionInput.setMatnr(infoMaterial
									// .getMaterial());
									// OrdenProduccionInput.setCant(infoMaterial
									// .getCajas());
									// OrdenProduccionInput.setCantT(infoMaterial.getMe());

									CarrilUbicacionDTO ubicacionCarril = new CarrilUbicacionDTO();
									LOCATION.error("reservando: " + OrdenProduccionInput.getOrdeProduccion() + " "
											+ OrdenProduccionInput.getHu2());

									ubicacionCarril = UbicacionPTDAO.consultReservaCarrilHu(
											OrdenProduccionInput.getOrdeProduccion(), OrdenProduccionInput.getHu2());

									resultDT = ubicacionCarril.getResultDT();
									LOCATION.error("primer reserva carril hu2: " + resultDT.getId());
									if (resultDT.getId() == 0) {// consulta
										// reserva
										// carril
										LOCATION.error("no hay reservando carril");

										resultDT = UbicacionPTDAO.reservarCarrilHU(
												OrdenProduccionInput.getOrdeProduccion(), OrdenProduccionInput.getHu2(),
												OrdenProduccionInput.getMatnr());

										if (resultDT.getId() == 1) {

											ubicacionCarril = new CarrilUbicacionDTO();
											ubicacionCarril = UbicacionPTDAO.consultReservaCarrilHu(
													OrdenProduccionInput.getOrdeProduccion(),
													OrdenProduccionInput.getHu2());

											resultDT = ubicacionCarril.getResultDT();
											if (resultDT.getId() == 1) {

												if (OrdenProduccionInput.getuDestino1()
														.equals(ubicacionCarril.getLgtyp())
														&& OrdenProduccionInput.getuDestino2()
																.equals(ubicacionCarril.getLgpla())) {

													// TODO OK, continuar a
													// confirmar carril
												} else {
													resultDT.setId(2);
													resultDT.setMsg("Ubicaciones destino no coinciden");

												}
											}

										} else {
											// no fue posible obtener reserva de
											// carril
											OrdenProduccionInput.setHu2(null);
										}

									} else if (resultDT.getId() == 1) {
										LOCATION.error(
												"status: " + resultDT.getTypeS() + (resultDT.getTypeS() == null));
										if (resultDT.getTypeS() == null) {

											if (OrdenProduccionInput.getuDestino1().equals(ubicacionCarril.getLgtyp())
													&& OrdenProduccionInput.getuDestino2()
															.equals(ubicacionCarril.getLgpla())) {

												// TODO OK, continuar a
												// confirmar carril
											} else {
												resultDT.setId(2);
												OrdenProduccionInput.setHu2(null);
												resultDT.setMsg("Ubicaciones destino no coinciden");

											}
										} else {
											OrdenProduccionInput.setHu2(null);
											resultDT.setId(2);
											resultDT.setMsg("HU ingresada previamente");
										}

									} else {
										OrdenProduccionInput.setHu2(null);
										resultDT.setId(2);
									}
								} else {// material no coincide
									OrdenProduccionInput.setHu2(null);
									resultDT.setId(2);
									resultDT.setMsg("El material de hu2 no coincide con el material de la hu1");
								}
							} else {// Material no encontrado
								OrdenProduccionInput.setHu2(null);
								resultDT.setId(2);
							}

						} else {// entrega no es igual a la entrega de hu1
							OrdenProduccionInput.setHu2(null);
							resultDT.setId(2);
							resultDT.setMsg("La hu2 no pertenece a la misma entrega");
						}

					} else {// entrega no encontrada
						OrdenProduccionInput.setHu2(null);
						resultDT.setId(2);
					}

					//
					//
					//
					//
					//
					//
					//
					//
					//
					// resultDT =
					// UbicacionPTDAO.getVBELNFromHu(OrdenProduccionInput
					// .getHu2());
					//
					// if (resultDT.getId() == 1) {// entrega encontrada
					//
					// // validar sean de la misma entrega
					//
					// if (resultDT.getTypeS().equals(
					// OrdenProduccionInput.getEntrega())) {
					//
					// OrdenProduccionInput.setEntrega(resultDT.getTypeS());
					//
					// // obtener info de hu
					//
					// EntregaDetalleDTO infoMaterial = new EntregaDetalleDTO();
					//
					// infoMaterial = UbicacionPTDAO
					// .getDataHU(OrdenProduccionInput.getHu2());
					// resultDT = infoMaterial.getResultDT();
					//
					// if (resultDT.getId() == 1
					// && infoMaterial.getMaterial().equals(
					// OrdenProduccionInput.getMatnr())) {
					//
					// // reservar carril
					// resultDT = UbicacionPTDAO.reservarCarrilHU(
					// OrdenProduccionInput.getEntrega(), OrdenProduccionInput
					// .getHu2(), OrdenProduccionInput
					// .getMatnr());
					//
					// CarrilUbicacionDTO ubicacionCarril = new
					// CarrilUbicacionDTO();
					//
					// if (resultDT.getId() != 1) {// consulta reserva
					// // carril
					//
					// ubicacionCarril = UbicacionPTDAO
					// .consultReservaCarrilHu(
					// OrdenProduccionInput.getEntrega(),
					// OrdenProduccionInput.getHu2());
					//
					// resultDT = ubicacionCarril.getResultDT();
					//
					// if (resultDT.getId() != 1) {
					// // no fue posible obtener reserva de
					// // carril
					// OrdenProduccionInput.setHu2(null);
					// }
					//
					// }// TODO OK si es 0 ya que se reservo lugar para
					// // HU
					//
					// // Obtener ubicacion y validar si son iguales
					//
					// CarrilesUbicacionDTO carriles = new
					// CarrilesUbicacionDTO();
					//
					// carriles = UbicacionPTDAO
					// .compararUbicacionesHUs(OrdenProduccionInput
					// .getHu1(), OrdenProduccionInput
					// .getHu2());
					//
					// if (carriles.getItem().get(0).getLGNUM()
					// .equals(
					// carriles.getItem().get(1)
					// .getLGNUM())
					// && carriles.getItem().get(0).getLGPLA()
					// .equals(
					// carriles.getItem().get(
					// 1).getLGPLA())
					// && carriles.getItem().get(0).getLGTYP()
					// .equals(
					// carriles.getItem().get(
					// 1).getLGTYP())) {
					//
					// resultDT.setId(1);
					// resultDT.setMsg("Ubicaciones iguales");
					//
					// OrdenProduccionInput.setuDestino1(carriles
					// .getItem().get(0).getLGTYP());
					// OrdenProduccionInput.setuDestino2(carriles
					// .getItem().get(0).getLGPLA());
					// } else {
					// resultDT.setId(2);
					// resultDT
					// .setMsg("LAS UBICACIONES DESTINO NO COINCIDEN, UNICAMENTE LLEVE LA HU1");
					// OrdenProduccionInput.setHu2(null);
					// }
					//
					// } else {// Desc Material no encontrado, no permitir
					// // picking
					// OrdenProduccionInput.setHu2(null);
					// }
					//
					// } else {// entregas no coinciden
					// OrdenProduccionInput.setHu2(null);
					// }
					//
					// } else {// entrega no encontrada
					// OrdenProduccionInput.setHu2(null);
					// }

				}
			}
			break;

		}

		OrdenProduccionInput.setResultDT(resultDT);

		return OrdenProduccionInput;

	}

	@Override
	public ResultDTO confirmaPickingHU(OrdenProduccionInputDTO ordenProduccionInput) {

		UbicacionPTRepository UbicacionPTDAO = new UbicacionPTRepository();

		ResultDTO resultDT = new ResultDTO();

		resultDT = UbicacionPTDAO.confirmaPickingHU(ordenProduccionInput.getOrdeProduccion(),
				ordenProduccionInput.getHu1());

		if (resultDT.getId() == 1) {

			if (ordenProduccionInput.getHu2() != null && !ordenProduccionInput.getHu2().equals("")) {

				// confirma depa en ZPICKING de HU2
				resultDT = UbicacionPTDAO.confirmaPickingHU(ordenProduccionInput.getOrdeProduccion(),
						ordenProduccionInput.getHu2());

				if (resultDT.getId() == 1) {

					// Si se confirman las dos HU
					// ingresar a ZContingencia las dos HUS

					resultDT = UbicacionPTDAO.insertProcesoContingencia_7(ordenProduccionInput,
							ordenProduccionInput.getHu1());

					resultDT = UbicacionPTDAO.insertProcesoContingencia_7(ordenProduccionInput,
							ordenProduccionInput.getHu2());

					// Consumir inventario de LQUA

					UbicacionPTDAO.aumentaInventario(ordenProduccionInput, ordenProduccionInput.getHu1());

					UbicacionPTDAO.aumentaInventario(ordenProduccionInput, ordenProduccionInput.getHu2());

				} // Falta rollback a confirmar hu1
				else {

				}

			} else {

				resultDT = UbicacionPTDAO.insertProcesoContingencia_7(ordenProduccionInput,
						ordenProduccionInput.getHu1());

				if (resultDT.getId() == 1) {
					// Se ingreso HU a ZCONTINGENCIA
					resultDT = UbicacionPTDAO.aumentaInventario(ordenProduccionInput, ordenProduccionInput.getHu1());

				} else {

				}

			}

		}

		// ordenProduccionInput.setResultDT(resultDT);
		// return ordenProduccionInput;

		// /////////////////////////////////////////////////////////////////////////////////

		// UbicacionPTDAO UbicacionPTDAO = new UbicacionPTDAO();
		//
		// ResultDT resultDT = new ResultDT();
		//
		// if (ordenProduccionInput.getHu2().equals("")) {// Solo una HU
		// resultDT = UbicacionPTDAO.confirmaPickingHU(
		// ordenProduccionInput.getEntrega(), ordenProduccionInput
		// .getHu1());
		//
		// if (resultDT.getId() == 1) {
		// //
		// UbicacionPTDAO.insertProcesoContingencia_3(ordenProduccionInput.,ordenProduccionInput.getHu1());
		//
		// }
		//
		// } else {
		//
		// resultDT = UbicacionPTDAO.confirmaPickingHU(
		// ordenProduccionInput.getEntrega(), ordenProduccionInput
		// .getHu1());
		//
		// if (resultDT.getId() == 1) {
		// resultDT = UbicacionPTDAO.confirmaPickingHU(
		// ordenProduccionInput.getEntrega(), ordenProduccionInput
		// .getHu2());
		//
		// if (resultDT.getId() == 1) {
		// resultDT.setId(1);
		// resultDT.setMsg("HUs confirmadas en ubicación");
		//
		// //
		// UbicacionPTDAO.insertProcesoContingencia_3(ordenProduccionInput.,ordenProduccionInput.getHu1());
		// //
		// UbicacionPTDAO.insertProcesoContingencia_3(ordenProduccionInput.,ordenProduccionInput.getHu2());
		// //
		//
		// } else {// Rollback a HU1
		//
		// resultDT = UbicacionPTDAO.rollBackPickingHU(
		// ordenProduccionInput.getEntrega(),
		// ordenProduccionInput.getHu1());
		//
		// resultDT.setId(2);
		// resultDT.setMsg("Las Hus no fueron confirmadas");
		//
		// }
		// }
		//
		// }

		return resultDT;
	}

	@Override
	public int getFaltantes(String entry) {
		UbicacionPTRepository ubicacionPTDAO = new UbicacionPTRepository();
		int codUbicacionesDisponibles = 0;
		int ubicacionesDisponibles = ubicacionPTDAO.getFaltantes(entry);
		// 999999. error
		// 0. ya no hay ubicaciones
		// cualquier numero mayor a cero. Ubicaciones disponibles
		if (ubicacionesDisponibles == 0) {
			codUbicacionesDisponibles = 2; // ya NO existen mas HUs por
			// confirmar
		} else {
			if (ubicacionesDisponibles > 0 && ubicacionesDisponibles != 999999) {
				codUbicacionesDisponibles = 1; // aun existen mas HUs por
				// confirmar
			} else {
				if (ubicacionesDisponibles == 999999) {
					codUbicacionesDisponibles = 999999;
				}
			}
		}
		return codUbicacionesDisponibles;
	}
}
