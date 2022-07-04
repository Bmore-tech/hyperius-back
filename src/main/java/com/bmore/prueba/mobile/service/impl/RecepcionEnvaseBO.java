package com.bmore.prueba.mobile.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.prueba.mobile.dto.CarrilUbicacionDTO;
import com.bmore.prueba.mobile.dto.EntregaDetalleDTO;
import com.bmore.prueba.mobile.dto.EntregaInput;
import com.bmore.prueba.mobile.repository.impl.RecepcionEnvaseDAO;
import com.bmore.prueba.mobile.utils.ResultDT;
import com.bmore.prueba.mobile.utils.Utils;

public class RecepcionEnvaseBO {
	@SuppressWarnings("unused")
	private static final Logger LOCATION = LoggerFactory.getLogger(RecepcionEnvaseBO.class);
	public static EntregaInput validaEntregaEntrante(EntregaInput entregaInput) throws ClassNotFoundException {
		RecepcionEnvaseDAO recepcionEnvaseDAO = new RecepcionEnvaseDAO();
		EntregaInput entrega =new EntregaInput();
		entrega= recepcionEnvaseDAO.validarEntregaPickin(entregaInput.getEntrega());
		return entrega;
	}
	public static EntregaInput pickearHU(EntregaInput entregaInput, int hu1oHu2) throws ClassNotFoundException {
		RecepcionEnvaseDAO recepcionEnvaseDAO = new RecepcionEnvaseDAO();
		ResultDT resultDT = new ResultDT();
		switch (hu1oHu2) {
		case 1:
			// evitar doble pickeo
			resultDT = recepcionEnvaseDAO.validaPickeoPrevioHU(entregaInput,entregaInput.getHu1());
			if (resultDT.getId() == 1) {
				entregaInput.setHu2(null);
				// Revisar HU de VKPE, Entrega SAP
				resultDT = recepcionEnvaseDAO.getVBELNFromHuSAP(entregaInput.getHu1(), entregaInput.getWerks());
				if (resultDT.getId() != 1) {
					// Revisar HU de ZCONTINGENCIA, Entrega BCPS
					resultDT = recepcionEnvaseDAO.getVBELNFromHuBCPS(entregaInput.getHu1(), entregaInput.getWerks());
					if (resultDT.getId() == 1) {
						// Y015
						entregaInput.setLfart("13");
					}
				} else {
					if (resultDT.getMsg().equals("EL") || resultDT.getMsg().equals("YD06")) {
						// EL
						entregaInput.setLfart("3");
					} else if (resultDT.getMsg().equals("YD15")) {
						// YD15
						entregaInput.setLfart("13");
					}
				}
				if (resultDT.getId() == 1) {// entrega encontrada
					EntregaInput entregaReturn2 = new EntregaInput();
					entregaInput.setEntrega(resultDT.getTypeS());
					entregaReturn2 = validaEntregaEntrante(entregaInput);
					resultDT = entregaReturn2.getResultDT();
					if (resultDT.getId() == 1) {// Entrega en picking
						// obtener info de hu
						EntregaDetalleDTO infoMaterial = new EntregaDetalleDTO();
						// Datos HU VEKP
						infoMaterial = recepcionEnvaseDAO.getDataHU(entregaInput.getHu1());
						resultDT = infoMaterial.getResultDT();
						if (resultDT.getId() != 1) {
							// Datos HU LQUA
							infoMaterial = recepcionEnvaseDAO.getDataHU_LQUA(entregaInput.getHu1());
						}
						resultDT = infoMaterial.getResultDT();
						if (resultDT.getId() == 1 && entregaReturn2.getMateriales().get(infoMaterial.getMaterial()) != null) {
							entregaInput.setMaktx(infoMaterial.getDescripcion());
							entregaInput.setMatnr(infoMaterial.getMaterial());
							entregaInput.setCant(infoMaterial.getCajas());
							entregaInput.setCantT(infoMaterial.getMe());
							// reservar carril
							CarrilUbicacionDTO ubicacionCarril = new CarrilUbicacionDTO();
							ubicacionCarril = recepcionEnvaseDAO.consultReservaCarrilHu(entregaInput.getEntrega(), entregaInput.getHu1());
							resultDT = ubicacionCarril.getResultDT();
							if (resultDT.getId() == 0) {// consulta reserva carril
								resultDT = recepcionEnvaseDAO.reservarCarrilHU(
										entregaInput.getEntrega(), entregaInput.getHu1(), entregaInput.getMatnr(), entregaInput.getUsuarioMontacarga());
								if (resultDT.getId() == 1) {
									ubicacionCarril = new CarrilUbicacionDTO();
									ubicacionCarril = recepcionEnvaseDAO.consultReservaCarrilHu(entregaInput.getEntrega(),entregaInput.getHu1());
									resultDT = ubicacionCarril.getResultDT();
									if (resultDT.getId() == 1) {
										entregaInput.setuDestino0(ubicacionCarril.getLGNUM());
										entregaInput.setuDestino1(ubicacionCarril.getLGTYP());
										entregaInput.setuDestino2(ubicacionCarril.getLGPLA());
									}
								} else {
									// no fue posible obtener reserva de carril
									entregaInput.setHu1(null);
									}
							} else if (resultDT.getId() == 1) {
								if (resultDT.getTypeS() == null) {
									entregaInput.setuDestino0(ubicacionCarril.getLGNUM());
									entregaInput.setuDestino1(ubicacionCarril.getLGTYP());
									entregaInput.setuDestino2(ubicacionCarril.getLGPLA());
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
			} else {// hu pickeada previamente
				entregaInput.setHu1(null);
			}
			break;
		case 2:
			// validar que HUs sean diferentes
			if (entregaInput.getHu1().equalsIgnoreCase(entregaInput.getHu2())) {
				entregaInput.setHu2(null);
				resultDT.setId(3);
				resultDT.setMsg("HU1 y HU2 son iguales, capture HUs diferentes");
			} else {
				// Revisar HU de VKPE, Entrega SAP
				resultDT = recepcionEnvaseDAO.getVBELNFromHuSAP(entregaInput.getHu2(), entregaInput.getWerks());
				if (resultDT.getId() != 1) {
					// Revisar HU de ZCONTINGENCIA, Entrega BCPS
					resultDT = recepcionEnvaseDAO.getVBELNFromHuBCPS(entregaInput.getHu1(), entregaInput.getWerks());
					}
				if (resultDT.getId() == 1) {
					// entrega encontrada
					// validar sean de la misma entrega
					if (resultDT.getTypeS().equals(entregaInput.getEntrega().trim())) {
						// obtener info de hu
						EntregaDetalleDTO infoMaterial = new EntregaDetalleDTO();
						// Datos HU VEKP
						infoMaterial = recepcionEnvaseDAO.getDataHU(entregaInput.getHu2());
						resultDT = infoMaterial.getResultDT();
						// Datos HU LQUA
						if (resultDT.getId() != 1) {
							infoMaterial = recepcionEnvaseDAO.getDataHU_LQUA(entregaInput.getHu2());
							}
						resultDT = infoMaterial.getResultDT();
						if (resultDT.getId() == 1) {// Existe material
							if (entregaInput.getMatnr().equals(infoMaterial.getMaterial())) {
								CarrilUbicacionDTO ubicacionCarril = new CarrilUbicacionDTO();
								// consulta reserva
								ubicacionCarril = recepcionEnvaseDAO.consultReservaCarrilHu(entregaInput.getEntrega(), entregaInput.getHu2());
								resultDT = ubicacionCarril.getResultDT();
								if (resultDT.getId() == 0) {
									// no existe reserva de carril, realizar
									// reserva
									resultDT = recepcionEnvaseDAO.reservarCarrilHU(entregaInput.getEntrega(),entregaInput.getHu2(),entregaInput.getMatnr(),
															  entregaInput.getUsuarioMontacarga(),entregaInput.getuDestino1(),entregaInput.getuDestino2());
									if (resultDT.getId() == 1) {
										ubicacionCarril = new CarrilUbicacionDTO();
										ubicacionCarril = recepcionEnvaseDAO
												.consultReservaCarrilHu(entregaInput.getEntrega(),entregaInput.getHu2());

										resultDT = ubicacionCarril.getResultDT();
										if (resultDT.getId() == 1) {
											if (!(entregaInput.getuDestino1().equals(ubicacionCarril.getLGTYP()) && entregaInput.getuDestino2().equals(ubicacionCarril.getLGPLA()))) {
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
									if (resultDT.getTypeS() == null) {
										if (entregaInput.getuDestino1().equals(ubicacionCarril.getLGTYP())	&& 
												entregaInput.getuDestino2().equals(ubicacionCarril.getLGPLA())) {
											// todo OK, continuar a
											// confirmar carril
											resultDT.setMsg("HU2 encontrado");
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
			break;
		}
		entregaInput.setResultDT(resultDT);
		return entregaInput;
	}
	public static int getFaltantes(String entrega) throws ClassNotFoundException {
		RecepcionEnvaseDAO recepcionEnvaseDAO = new RecepcionEnvaseDAO();
		int codUbicacionesDisponibles = 0;
		int ubicacionesDisponibles = recepcionEnvaseDAO.getFaltantes(entrega);

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
	public static EntregaInput confirmaPickingHU(EntregaInput entregaEntranteInput) throws ClassNotFoundException {
		RecepcionEnvaseDAO recepcionEnvaseDAO = new RecepcionEnvaseDAO();
		ResultDT resultDT = new ResultDT();
		if (entregaEntranteInput.getuDestino0().trim().equals("ML2")) {
			entregaEntranteInput.setLgort("LV02");
		} else {
			entregaEntranteInput.setLgort("LV01");
		}
		if (entregaEntranteInput.getHu2() == null|| entregaEntranteInput.getHu2().equals("")) {
			entregaEntranteInput.setHu2("");
		}
		resultDT = recepcionEnvaseDAO.confirmaHusEnCarrill(entregaEntranteInput);
		switch (resultDT.getId()) {
		case 1:
			resultDT.setMsg("Las Hus fueron consumidas");
			resultDT.setTypeI(getFaltantes(Utils.zeroFill(entregaEntranteInput.getEntrega(), 10)));
			break;
		case 2:
			resultDT.setId(1);
			resultDT.setMsg("Las Hus fueron consumidas");
			resultDT.setTypeI(getFaltantes(Utils.zeroFill(entregaEntranteInput.getEntrega(), 10)));
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
		case 9:
			resultDT.setMsg("Error al ingresar hu a Zcontingencia");
			break;
		}
		entregaEntranteInput.setResultDT(resultDT);
		return entregaEntranteInput;
	}
	public static ResultDT limpiarPendientesXUsuario(String vbeln, String user) throws ClassNotFoundException {
		RecepcionEnvaseDAO recepcionEnvaseDAO = new RecepcionEnvaseDAO();
		return recepcionEnvaseDAO.limpiaPendientesXUsuario(vbeln, user);
	}
	public static String getWerks(String Hu) throws ClassNotFoundException{
		return RecepcionEnvaseDAO.getWerks(Hu);
	}
}