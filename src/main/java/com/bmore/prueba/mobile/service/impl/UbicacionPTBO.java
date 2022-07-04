package com.bmore.prueba.mobile.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.prueba.mobile.dto.CarrilUbicacionDTO;
import com.bmore.prueba.mobile.dto.OrdenProduccionDetalleDTO;
import com.bmore.prueba.mobile.dto.OrdenProduccionInput;
import com.bmore.prueba.mobile.repository.impl.UbicacionPTDAO;
import com.bmore.prueba.mobile.utils.ResultDT;
import com.bmore.prueba.mobile.utils.Utils;

public class UbicacionPTBO {
	private static final Logger LOCATION = LoggerFactory.getLogger(UbicacionPTBO.class);
	public static OrdenProduccionInput validaOrdenProduccion(OrdenProduccionInput ordenProduccionInput) throws ClassNotFoundException{
		UbicacionPTDAO UbicacionPTDAO = new UbicacionPTDAO();
		OrdenProduccionInput orden = UbicacionPTDAO.validarOrdenEnPickin(ordenProduccionInput.getOrdeProduccion());
		return orden;
	}
	public static OrdenProduccionInput pickearHU(OrdenProduccionInput ordenProduccionInput, int hu1oHu2) throws ClassNotFoundException{
		UbicacionPTDAO UbicacionPTDAO = new UbicacionPTDAO();
		ResultDT resultDT = new ResultDT();
		switch (hu1oHu2) {
		case 1:
			ordenProduccionInput.setHu2(null);
			resultDT = UbicacionPTDAO.getAUFNRFromHu(ordenProduccionInput.getHu1(), ordenProduccionInput.getWerks());
			if (resultDT.getId() == 1) {// entrega encontrada
				ordenProduccionInput.setOrdeProduccion(resultDT.getTypeS());
				OrdenProduccionInput entregaReturn2 = new OrdenProduccionInput();
				entregaReturn2 = validaOrdenProduccion(ordenProduccionInput);
				resultDT = entregaReturn2.getResultDT();
				if (resultDT.getId() == 1) {// Entrega en picking
					// obtener info de hu
					OrdenProduccionDetalleDTO infoMaterial = new OrdenProduccionDetalleDTO();
					infoMaterial = UbicacionPTDAO.getDataHU(ordenProduccionInput.getHu1());
					resultDT = infoMaterial.getResultDT();
					if (resultDT.getId() == 1 && entregaReturn2.getMateriales().get(infoMaterial.getMaterial()) != null) {
						ordenProduccionInput.setMaktx(infoMaterial.getDescripcion());
						ordenProduccionInput.setMatnr(infoMaterial.getMaterial());
						ordenProduccionInput.setCant(infoMaterial.getCajas());
						ordenProduccionInput.setCantT(infoMaterial.getMe());
						ordenProduccionInput.setTarima(infoMaterial.getTarima());
						CarrilUbicacionDTO ubicacionCarril = new CarrilUbicacionDTO();
						ubicacionCarril = UbicacionPTDAO.consultReservaCarrilHu(ordenProduccionInput.getOrdeProduccion(),ordenProduccionInput.getHu1());
						resultDT = ubicacionCarril.getResultDT();
						if (resultDT.getId() == 0) {// consulta reserva carril
							resultDT = UbicacionPTDAO.reservarCarrilHU(ordenProduccionInput.getOrdeProduccion(),ordenProduccionInput.getHu1(),ordenProduccionInput.getMatnr(),ordenProduccionInput.getUsuarioMontacarga());
							if (resultDT.getId() == 1) {
								ubicacionCarril = new CarrilUbicacionDTO();
								ubicacionCarril = UbicacionPTDAO.consultReservaCarrilHu(ordenProduccionInput.getOrdeProduccion(),ordenProduccionInput.getHu1());
								resultDT = ubicacionCarril.getResultDT();
								if (resultDT.getId() == 1) {
									ordenProduccionInput.setuDestino0(ubicacionCarril.getLGNUM());
									ordenProduccionInput.setuDestino1(ubicacionCarril.getLGTYP());
									ordenProduccionInput.setuDestino2(ubicacionCarril.getLGPLA());
								}
							} else {
								// no fue posible obtener reserva de carril
								ordenProduccionInput.setHu1(null);
							}
						} else if (resultDT.getId() == 1) {							
							if (resultDT.getTypeS() == null) {
								ordenProduccionInput.setuDestino0(ubicacionCarril.getLGNUM());
								ordenProduccionInput.setuDestino1(ubicacionCarril.getLGTYP());
								ordenProduccionInput.setuDestino2(ubicacionCarril.getLGPLA());
							} else {
								ordenProduccionInput.setHu1(null);
								resultDT.setId(2);
								resultDT.setMsg("HU ingresada previamente");
							}
						} else {
							ordenProduccionInput.setHu1(null);
						}
					} else {// material no encontrado
						ordenProduccionInput.setHu1(null);
					}

				} else {// entrega no esta en picking
					ordenProduccionInput.setHu1(null);
				}

			} else {// entrega no encontrada
				ordenProduccionInput.setHu1(null);
			}
			break;
		case 2:
			// validar que HUs sean diferentes
			if (ordenProduccionInput.getHu1().equalsIgnoreCase(
					ordenProduccionInput.getHu2())) {
				resultDT.setId(3);
				resultDT.setMsg("HU1 y HU2 son iguales, capture HUs diferentes");
				ordenProduccionInput.setHu2(null);
			} else {
				resultDT = UbicacionPTDAO.getAUFNRFromHu(ordenProduccionInput.getHu2(), ordenProduccionInput.getWerks());
				if (resultDT.getId() == 1) {// entrega encontrada
					// validar sean de la misma entrega
					if (resultDT.getTypeS().equals(
							ordenProduccionInput.getOrdeProduccion())) {
						// obtener info de hu
						OrdenProduccionDetalleDTO infoMaterial = new OrdenProduccionDetalleDTO();
						infoMaterial = UbicacionPTDAO.getDataHU(ordenProduccionInput.getHu2());
						resultDT = infoMaterial.getResultDT();
						if (resultDT.getId() == 1) {// Existe material
							if (ordenProduccionInput.getMatnr().equals(infoMaterial.getMaterial())) {
								CarrilUbicacionDTO ubicacionCarril = new CarrilUbicacionDTO();
								ubicacionCarril = UbicacionPTDAO.consultReservaCarrilHu(ordenProduccionInput.getOrdeProduccion(),ordenProduccionInput.getHu2());
								resultDT = ubicacionCarril.getResultDT();
								if (resultDT.getId() == 0) {// consulta reserva
									// carril
									resultDT = UbicacionPTDAO.reservarCarrilHU(
													ordenProduccionInput.getOrdeProduccion(),
													ordenProduccionInput.getHu2(),
													ordenProduccionInput.getMatnr(),
													ordenProduccionInput.getuDestino1(),
													ordenProduccionInput.getuDestino2(),
													ordenProduccionInput.getUsuarioMontacarga());
									if (resultDT.getId() == 1) {
										ubicacionCarril = new CarrilUbicacionDTO();
										ubicacionCarril = UbicacionPTDAO.consultReservaCarrilHu(ordenProduccionInput.getOrdeProduccion(),ordenProduccionInput.getHu2());
										resultDT = ubicacionCarril.getResultDT();
										if (resultDT.getId() == 1) {
											if (ordenProduccionInput.getuDestino1().equals(	ubicacionCarril.getLGTYP())	&& ordenProduccionInput.getuDestino2().equals(ubicacionCarril.getLGPLA())) {
												// TODO_OK, continuar a
												// confirmar carril
											} else {
												resultDT.setId(2);
												resultDT.setMsg("Ubicaciones destino no coinciden");
											}
										}
									} else {
										// no fue posible obtener reserva de
										// carril
										ordenProduccionInput.setHu2(null);
									}

								} else if (resultDT.getId() == 1) {
									if (resultDT.getTypeS() == null) {
										if (ordenProduccionInput.getuDestino1().equals(ubicacionCarril.getLGTYP()) && ordenProduccionInput.getuDestino2().equals(ubicacionCarril.getLGPLA())) {
											//  continuar a
											// confirmar carril
											resultDT.setMsg("HU2 encontrado");
										} else {
											resultDT.setId(2);
											ordenProduccionInput.setHu2(null);
											resultDT.setMsg("Ubicaciones destino no coinciden");
										}
									} else {
										ordenProduccionInput.setHu2(null);
										resultDT.setId(2);
										resultDT.setMsg("HU ingresada previamente");
									}
								} else {
									ordenProduccionInput.setHu2(null);
									resultDT.setId(2);
								}
							} else {// material no coincide
								ordenProduccionInput.setHu2(null);
								resultDT.setId(2);
								resultDT.setMsg("El material de hu2 no coincide con el material de la hu1");
							}
						} else {// Material no encontrado
							ordenProduccionInput.setHu2(null);
							resultDT.setId(2);
						}
					} else {// entrega no es igual a la entrega de hu1
						ordenProduccionInput.setHu2(null);
						resultDT.setId(2);
						resultDT.setMsg("La hu2 no pertenece a la misma entrega");
					}
				} else {// entrega no encontrada
					ordenProduccionInput.setHu2(null);
					resultDT.setId(2);
				}
			}
			break;
		}
		ordenProduccionInput.setResultDT(resultDT);
		return ordenProduccionInput;
	}
	public static OrdenProduccionInput confirmaPickingHU(OrdenProduccionInput ordenProduccionInput) throws ClassNotFoundException{
		UbicacionPTDAO ubicacionPTDAO = new UbicacionPTDAO();
		ResultDT resultDT = new ResultDT();
		if (ordenProduccionInput.getHu2() == null || ordenProduccionInput.getHu2().equals("")) {
			ordenProduccionInput.setHu2("");
		}
		resultDT = ubicacionPTDAO.confirmaHusEnCarrill(ordenProduccionInput);
		switch (resultDT.getId()) {
		case 1:
			resultDT.setMsg("Las Hus fueron consumidas");
			resultDT.setTypeI(getFaltantes(Utils.zeroFill(ordenProduccionInput.getOrdeProduccion(), 12)));
			break;
		case 2:
			resultDT.setId(1);
			resultDT.setMsg("Las Hus fueron consumidas");
			resultDT.setTypeI(getFaltantes(Utils.zeroFill(ordenProduccionInput.getOrdeProduccion(), 12)));
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
		ordenProduccionInput.setResultDT(resultDT);
		return ordenProduccionInput;
	}
	public static int getFaltantes(String entry) throws ClassNotFoundException{
		UbicacionPTDAO ubicacionPTDAO = new UbicacionPTDAO();
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
	public static ResultDT limpiarPendientesXUsuario(String vbeln, String user) throws ClassNotFoundException{
		UbicacionPTDAO ubicacionPTDAO = new UbicacionPTDAO();
		return ubicacionPTDAO.limpiaPendientesXUsuario(vbeln, user);
	}
	public static String getWerks(String Hu) throws ClassNotFoundException{
		LOCATION.info("Metodo getWerks UbiPTBO");
		String werks=UbicacionPTDAO.getWerks(Hu);
		LOCATION.info("Werks: "+werks);
		return werks;
	}
}