package com.bmore.prueba.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bmore.prueba.web.dto.CarrilUbicacionDTO;
import com.bmore.prueba.web.dto.EntregaDetalleDTO;
import com.bmore.prueba.web.dto.EntregaInputDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.repository.old.RecepcionEnvaseRepository;
import com.bmore.prueba.web.service.RecepcionEnvaseMService;

@Service
public class RecepcionEnvaseMServiceImpl implements RecepcionEnvaseMService {

	private static final Logger LOCATION = LoggerFactory.getLogger(RecepcionEnvaseMServiceImpl.class);

	@Override
	public EntregaInputDTO validaEntregaEntrante(EntregaInputDTO entregaInput) {

		// // Limpiar para evitar errores con boton next de navegador
		// entregaInput.setHu1(null);
		// entregaInput.setHu2(null);
		// entregaInput.setConfHus(null);

		RecepcionEnvaseRepository recepcionEnvaseDAO = new RecepcionEnvaseRepository();
		EntregaInputDTO entrega = recepcionEnvaseDAO.validarEntregaPickin(entregaInput.getEntrega());

		return entrega;
	}

	@Override
	public EntregaInputDTO pickearHU(EntregaInputDTO entregaInput, int hu1oHu2) {

		RecepcionEnvaseRepository recepcionEnvaseDAO = new RecepcionEnvaseRepository();
		ResultDTO resultDT = new ResultDTO();

		switch (hu1oHu2) {

		case 1:

			// evitar back errores en back de navegador
			entregaInput.setHu2(null);

			// Revisar HU de VKPE, Entrega SAP

			resultDT = recepcionEnvaseDAO.getVBELNFromHuSAP(entregaInput.getHu1(), entregaInput.getWerks());
			// EL

			if (resultDT.getId() != 1) {
				// Y015
				entregaInput.setLfart("13");
				resultDT = recepcionEnvaseDAO.getVBELNFromHuBCPS(entregaInput.getHu1(), entregaInput.getWerks());

			} else {

				if (resultDT.getMsg().equals("EL")) {
					// EL
					entregaInput.setLfart("3");
				} else if (resultDT.getMsg().equals("YD15")) {
					// YD15
					entregaInput.setLfart("13");
				}
			}

			if (resultDT.getId() == 1) {// entrega encontrada
				LOCATION.error("entrega encontrada:");
				entregaInput.setEntrega(resultDT.getTypeS());

				EntregaInputDTO entregaReturn2 = new EntregaInputDTO();

				entregaReturn2 = validaEntregaEntrante(entregaInput);

				resultDT = entregaReturn2.getResultDT();

				if (resultDT.getId() == 1) {// Entrega en picking
					// obtener info de hu

					EntregaDetalleDTO infoMaterial = new EntregaDetalleDTO();
					LOCATION.error("get data hu");

					infoMaterial = recepcionEnvaseDAO.getDataHU(entregaInput.getHu1());
					resultDT = infoMaterial.getResultDT();

					if (resultDT.getId() != 1) {
						infoMaterial = recepcionEnvaseDAO.getDataHU_LQUA(entregaInput.getHu1());
					}
					LOCATION.error("get data hu ok");
					resultDT = infoMaterial.getResultDT();

					if (resultDT.getId() == 1
							&& entregaReturn2.getMateriales().get(infoMaterial.getMaterial()) != null) {

						entregaInput.setMaktx(infoMaterial.getDescripcion());
						entregaInput.setMatnr(infoMaterial.getMaterial());
						entregaInput.setCant(infoMaterial.getCajas());
						entregaInput.setCantT(infoMaterial.getMe());

						// reservar carril
						CarrilUbicacionDTO ubicacionCarril = new CarrilUbicacionDTO();
						LOCATION.error("reservando: " + entregaInput.getEntrega() + " " + entregaInput.getHu1());
						ubicacionCarril = recepcionEnvaseDAO.consultReservaCarrilHu(entregaInput.getEntrega(),
								entregaInput.getHu1());
						resultDT = ubicacionCarril.getResultDT();
						LOCATION.error("primer reserva carril: " + resultDT.getId());
						if (resultDT.getId() == 0) {// consulta reserva carril
							LOCATION.error("no hay reservando carril");

							resultDT = recepcionEnvaseDAO.reservarCarrilHU(entregaInput.getEntrega(),
									entregaInput.getHu1(), entregaInput.getMatnr(),
									entregaInput.getUsuarioMontacarga());

							if (resultDT.getId() == 1) {

								ubicacionCarril = new CarrilUbicacionDTO();
								ubicacionCarril = recepcionEnvaseDAO.consultReservaCarrilHu(entregaInput.getEntrega(),
										entregaInput.getHu1());

								resultDT = ubicacionCarril.getResultDT();
								if (resultDT.getId() == 1) {

									entregaInput.setuDestino0(ubicacionCarril.getLgnum());

									entregaInput.setuDestino1(ubicacionCarril.getLgtyp());
									entregaInput.setuDestino2(ubicacionCarril.getLgpla());
								}

							} else {
								// no fue posible obtener reserva de carril
								entregaInput.setHu1(null);
							}

						} else if (resultDT.getId() == 1) {
							LOCATION.error("status: " + resultDT.getTypeS() + (resultDT.getTypeS() == null));
							if (resultDT.getTypeS() == null) {

								entregaInput.setuDestino0(ubicacionCarril.getLgnum());

								entregaInput.setuDestino1(ubicacionCarril.getLgtyp());
								entregaInput.setuDestino2(ubicacionCarril.getLgpla());
							} else {
								entregaInput.setHu1(null);
								resultDT.setId(2);
								resultDT.setMsg("HU ingresada previamente");
							}

						} else {
							entregaInput.setHu1(null);
						}
					} else {// material no encontrado
						entregaInput.setHu1(null);
						resultDT.setId(2);
						resultDT.setMsg("Material no encontrado");
					}

				} else {// entrega no esta en picking
					entregaInput.setHu1(null);
				}

			} else {// entrega no encontrada
				entregaInput.setHu1(null);
			}

			break;
		case 2:

			if (entregaInput.getHu2() == null || entregaInput.getHu2().trim().equals("")) {
				// Si es nula entonces mandar a capturar ubicacion ya que solo
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

					LOCATION.error("VBLEN:");

					resultDT = recepcionEnvaseDAO.getVBELNFromHuSAP(entregaInput.getHu2(), entregaInput.getWerks());

					if (resultDT.getId() != 1)
						resultDT = recepcionEnvaseDAO.getVBELNFromHuBCPS(entregaInput.getHu1(),
								entregaInput.getWerks());

					if (resultDT.getId() == 1) {// entrega encontrada
						LOCATION.error("entrega encontrada:");

						// validar sean de la misma entrega

						if (resultDT.getTypeS().equals(entregaInput.getEntrega())) {

							// obtener info de hu

							EntregaDetalleDTO infoMaterial = new EntregaDetalleDTO();
							LOCATION.error("get data hu");
							infoMaterial = recepcionEnvaseDAO.getDataHU(entregaInput.getHu2());
							LOCATION.error("get data hu ok");
							resultDT = infoMaterial.getResultDT();

							if (resultDT.getId() != 1) {
								infoMaterial = recepcionEnvaseDAO.getDataHU_LQUA(entregaInput.getHu2());
							}

							resultDT = infoMaterial.getResultDT();

							if (resultDT.getId() == 1) {// Existe material

								if (entregaInput.getMatnr().equals(infoMaterial.getMaterial())) {

									// entregaInput.setMaktx(infoMaterial
									// .getDescripcion());
									// entregaInput.setMatnr(infoMaterial
									// .getMaterial());
									// entregaInput.setCant(infoMaterial
									// .getCajas());
									// entregaInput.setCantT(infoMaterial.getMe());

									CarrilUbicacionDTO ubicacionCarril = new CarrilUbicacionDTO();
									LOCATION.error(
											"reservando: " + entregaInput.getEntrega() + " " + entregaInput.getHu2());

									ubicacionCarril = recepcionEnvaseDAO
											.consultReservaCarrilHu(entregaInput.getEntrega(), entregaInput.getHu2());

									resultDT = ubicacionCarril.getResultDT();
									LOCATION.error("primer reserva carril hu2: " + resultDT.getId());
									if (resultDT.getId() == 0) {// consulta
										// reserva
										// carril
										LOCATION.error("no hay reservando carril");

										resultDT = recepcionEnvaseDAO.reservarCarrilHU(entregaInput.getEntrega(),
												entregaInput.getHu2(), entregaInput.getMatnr(),
												entregaInput.getUsuarioMontacarga());

										if (resultDT.getId() == 1) {

											ubicacionCarril = new CarrilUbicacionDTO();
											ubicacionCarril = recepcionEnvaseDAO.consultReservaCarrilHu(
													entregaInput.getEntrega(), entregaInput.getHu2());

											resultDT = ubicacionCarril.getResultDT();
											if (resultDT.getId() == 1) {

												if (entregaInput.getuDestino1().equals(ubicacionCarril.getLgtyp())
														&& entregaInput.getuDestino2()
																.equals(ubicacionCarril.getLgpla())) {

													// todo OK, continuar a
													// confirmar carril
												} else {
													entregaInput.setHu2(null);
													resultDT.setId(2);
													resultDT.setMsg("Ubicaciones destino no coinciden");

												}
											}

										} else {
											// no fue posible obtener reserva de
											// carril
											entregaInput.setHu2(null);
										}

									} else if (resultDT.getId() == 1) {
										LOCATION.error(
												"status: " + resultDT.getTypeS() + (resultDT.getTypeS() == null));
										if (resultDT.getTypeS() == null) {

											if (entregaInput.getuDestino1().equals(ubicacionCarril.getLgtyp())
													&& entregaInput.getuDestino2().equals(ubicacionCarril.getLgpla())) {

												// todo OK, continuar a
												// confirmar carril
											} else {
												entregaInput.setHu2(null);
												resultDT.setId(2);
												resultDT.setMsg("Ubicaciones destino no coinciden");

											}
										} else {
											entregaInput.setHu2(null);
											resultDT.setId(2);
											resultDT.setMsg("HU ingresada previamente");
										}

									} else {
										entregaInput.setHu2(null);
										resultDT.setId(2);
									}
								} else {// material no coincide
									entregaInput.setHu2(null);
									resultDT.setId(2);
									resultDT.setMsg("El material de hu2 no coincide con el material de la hu1");
								}
							} else {// Material no encontrado
								entregaInput.setHu2(null);
								resultDT.setId(2);
							}

						} else {// entrega no es igual a la entrega de hu1
							entregaInput.setHu2(null);
							resultDT.setId(2);
							resultDT.setMsg("La hu2 no pertenece a la misma entrega");
						}

					} else {// entrega no encontrada
						entregaInput.setHu2(null);
						resultDT.setId(2);
					}

				}
			}
			break;

		}

		entregaInput.setResultDT(resultDT);

		return entregaInput;

	}

	@Override
	public int getFaltantes(String entry) {
		RecepcionEnvaseRepository recepcionEnvaseDAO = new RecepcionEnvaseRepository();
		int codUbicacionesDisponibles = 0;
		int ubicacionesDisponibles = recepcionEnvaseDAO.getFaltantes(entry);
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

	@Override
	public ResultDTO confirmaPickingHU(EntregaInputDTO entregaEntranteInput) {

		RecepcionEnvaseRepository recepcionEnvaseDAO = new RecepcionEnvaseRepository();

		ResultDTO resultDT = new ResultDTO();

		resultDT = recepcionEnvaseDAO.confirmaPickingHU(entregaEntranteInput.getEntrega(),
				entregaEntranteInput.getHu1());

		if (resultDT.getId() == 1) {

			// Valida la parte del almacen

			if (entregaEntranteInput.getuDestino0().trim().equals("ML2")) {
				entregaEntranteInput.setLgort("LV02");
			} else {
				entregaEntranteInput.setLgort("LV01");
			}

			if (entregaEntranteInput.getHu2() != null && !entregaEntranteInput.getHu2().equals("")) {

				// confirma depa en ZPICKING de HU2
				resultDT = recepcionEnvaseDAO.confirmaPickingHU(entregaEntranteInput.getEntrega(),
						entregaEntranteInput.getHu2());

				if (resultDT.getId() == 1) {

					// Si se confirman las dos HU
					// ingresar a ZContingencia las dos HUS

					resultDT = recepcionEnvaseDAO.insertProcesoContingencia_3(entregaEntranteInput,
							entregaEntranteInput.getHu1());

					resultDT = recepcionEnvaseDAO.insertProcesoContingencia_3(entregaEntranteInput,
							entregaEntranteInput.getHu2());

					// Consumir inventario de LQUA

					recepcionEnvaseDAO.aumentaInventario(entregaEntranteInput, entregaEntranteInput.getHu1());

					recepcionEnvaseDAO.aumentaInventario(entregaEntranteInput, entregaEntranteInput.getHu2());

				} // Falta rollback a confirmar hu1
				else {

				}

			} else {

				resultDT = recepcionEnvaseDAO.insertProcesoContingencia_3(entregaEntranteInput,
						entregaEntranteInput.getHu1());

				if (resultDT.getId() == 1) {
					// Se ingreso HU a ZCONTINGENCIA
					resultDT = recepcionEnvaseDAO.aumentaInventario(entregaEntranteInput,
							entregaEntranteInput.getHu1());

				} else {

				}

			}

		}

		return resultDT;
	}
}
