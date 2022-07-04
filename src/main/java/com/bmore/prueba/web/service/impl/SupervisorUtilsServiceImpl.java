package com.bmore.prueba.web.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bmore.prueba.web.dto.CarrilUbicacionDTO;
import com.bmore.prueba.web.dto.CarrilesBloqueadosDTO;
import com.bmore.prueba.web.dto.CarrilesUbicacionDTO;
import com.bmore.prueba.web.dto.EmbarqueDTO;
import com.bmore.prueba.web.dto.EntregasTransportesDTO;
import com.bmore.prueba.web.dto.FTPConfDTO;
import com.bmore.prueba.web.dto.HUsEnTransporteDetalleDTO;
import com.bmore.prueba.web.dto.HusEnTransporteDTO;
import com.bmore.prueba.web.dto.InventarioDTO;
import com.bmore.prueba.web.dto.ListaDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.dto.TablaSqlDTO;
import com.bmore.prueba.web.dto.TablasSqlDTO;
import com.bmore.prueba.web.dto.TablasSqlItemDTO;
import com.bmore.prueba.web.dto.UsuarioDTO;
import com.bmore.prueba.web.dto.UsuarioItemDTO;
import com.bmore.prueba.web.repository.old.SupervisorUtilsRepository;
import com.bmore.prueba.web.repository.old.ZContingenciaRepository;
import com.bmore.prueba.web.service.SupervisorUtilsService;
import com.bmore.prueba.web.utils.Utils;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Service
public class SupervisorUtilsServiceImpl implements SupervisorUtilsService {

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	private String pathIn = "E:" + File.separator + "RepoSentinel" + File.separator;
	private String pathOut = "E:" + File.separator + "RepoSentinel" + File.separator + "final";

	@Override
	public EntregasTransportesDTO obtieneEntrega(String tknum, String werks) {

		EntregasTransportesDTO entregasTransportes = new EntregasTransportesDTO();
		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		entregasTransportes = supervisorUtilsDAO.obtieneEntrega(tknum, werks);

		LOCATION.error("TKNUM: " + tknum + " WERKs" + werks);

		if (entregasTransportes.getResultDT().getId() == 1) {

			for (int x = 0; x < entregasTransportes.getItems().getItem().size(); x++) {

				if (entregasTransportes.getItems().getItem().get(x).getStatus() != null
						&& !entregasTransportes.getItems().getItem().get(x).getStatus().equals("")) {
					entregasTransportes.getItems().getItem().get(x).setStatus("Contabilizada");
				} else {
					entregasTransportes.getItems().getItem().get(x).setStatus("Sin contabilizar");
				}

				if (entregasTransportes.getItems().getItem().get(x).getVblenEntrante().startsWith("04")
						|| entregasTransportes.getItems().getItem().get(x).getVblenEntrante().startsWith("07")) {

					// Entrega saliente
					entregasTransportes.getItems().getItem().get(x)
							.setVblenSaliente(entregasTransportes.getItems().getItem().get(x).getVblenEntrante());
					entregasTransportes.getItems().getItem().get(x).setVblenEntrante("");

				}

			}

		}

		return entregasTransportes;

	}

	@Override
	public EntregasTransportesDTO obtieneEntregas(String werks) {

		EntregasTransportesDTO entregasTransportes = new EntregasTransportesDTO();
		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		entregasTransportes = supervisorUtilsDAO.obtieneEntregas(werks);

		if (entregasTransportes.getResultDT().getId() == 1) {

			for (int x = 0; x < entregasTransportes.getItems().getItem().size(); x++) {

				if (entregasTransportes.getItems().getItem().get(x).getStatus() != null
						&& !entregasTransportes.getItems().getItem().get(x).getStatus().equals("")) {
					entregasTransportes.getItems().getItem().get(x).setStatus("Contabilizada");
				} else {
					entregasTransportes.getItems().getItem().get(x).setStatus("Sin contabilizar");
				}

				if (entregasTransportes.getItems().getItem().get(x).getEdi().equals("1")) {
					entregasTransportes.getItems().getItem().get(x).setEdi("Generar EDI");
				} else {
					entregasTransportes.getItems().getItem().get(x).setEdi("");
				}

				if (entregasTransportes.getItems().getItem().get(x).getVblenEntrante().startsWith("04")
						|| entregasTransportes.getItems().getItem().get(x).getVblenEntrante().startsWith("07")
						|| entregasTransportes.getItems().getItem().get(x).getVblenEntrante().startsWith("7")) {
					// Entrega saliente
					entregasTransportes.getItems().getItem().get(x)
							.setVblenSaliente(entregasTransportes.getItems().getItem().get(x).getVblenEntrante());
					entregasTransportes.getItems().getItem().get(x).setVblenEntrante("");

				}

			}

		}

		return entregasTransportes;

	}

	@Override
	public InventarioDTO obtieneInventario(String werks, String opc) {

		InventarioDTO inventarioDTO = new InventarioDTO();
		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		int intOpc = 0;

		try {

			intOpc = Integer.parseInt(opc);
			LOCATION.error("Conversion: " + intOpc + " init: " + opc);

		} catch (Exception e) {

			ResultDTO resultDT = new ResultDTO();

			resultDT.setId(2);
			resultDT.setMsg("Error al convertir opc :: obtieneInventario ");

			inventarioDTO.setResultDT(resultDT);
			return inventarioDTO;

		}

		switch (intOpc) {

		case 1:
			inventarioDTO = supervisorUtilsDAO.obtieneInventario(werks);
			break;
		case 2:
			inventarioDTO = supervisorUtilsDAO.obtieneInventarioLotes(werks);
			break;

		}

		if (inventarioDTO.getResultDT().getId() == 1) {

			for (int x = 0; x < inventarioDTO.getItems().getItem().size(); x++) {

				inventarioDTO.getItems().getItem().get(x)
						.setMatnr(Utils.zeroClean(inventarioDTO.getItems().getItem().get(x).getMatnr()));

			}

		}

		return inventarioDTO;

	}

	@Override
	public HusEnTransporteDTO obtieneCarrilesBloqueados(HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO,
			String werks) {

		HusEnTransporteDTO husEnTransporteDTO = new HusEnTransporteDTO();
		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		if (husEnTransporteDetalleDTO.getIdProceso().equals("1") || husEnTransporteDetalleDTO.getIdProceso().equals("4")
				|| husEnTransporteDetalleDTO.getIdProceso().equals("6")) {

			if (husEnTransporteDetalleDTO.getIdProceso().equals("6"))
				husEnTransporteDetalleDTO.setIdProceso("4");
			// Entregas

			husEnTransporteDetalleDTO.setVbeln(Utils.zeroFill(husEnTransporteDetalleDTO.getVbeln(), 10));

		} else if (husEnTransporteDetalleDTO.getIdProceso().equals("2")
				|| husEnTransporteDetalleDTO.getIdProceso().equals("3")
				|| husEnTransporteDetalleDTO.getIdProceso().equals("5")) {

			// Orden de produccion

			husEnTransporteDetalleDTO.setVbeln(Utils.zeroFill(husEnTransporteDetalleDTO.getVbeln(), 12));

		}

		if (husEnTransporteDetalleDTO.getTipoAlmacen().equals("im")) {
			// IM
			husEnTransporteDTO = supervisorUtilsDAO.obtieneMaterialesBloqueados(
					husEnTransporteDetalleDTO.getIdProceso(), husEnTransporteDetalleDTO.getVbeln(), werks);

		} else if (husEnTransporteDetalleDTO.getTipoAlmacen().equals("wm")) {
			// WM

			husEnTransporteDTO = supervisorUtilsDAO.obtieneCarrilesBloqueados(husEnTransporteDetalleDTO.getIdProceso(),
					husEnTransporteDetalleDTO.getVbeln(), werks);
		}

		if (husEnTransporteDTO.getResultDT().getId() == 1) {

			for (int x = 0; x < husEnTransporteDTO.getItems().getItem().size(); x++) {

				HUsEnTransporteDetalleDTO hu = husEnTransporteDTO.getItems().getItem().get(x);

				hu.setMatnr(Utils.zeroClean(hu.getMatnr()));

				hu.setIdCarril(hu.getLgnum() + hu.getLgtyp() + hu.getLgpla() + hu.getMatnr());

				hu.setVbeln(husEnTransporteDetalleDTO.getVbeln());

			}

		}

		return husEnTransporteDTO;

	}

	@Override
	public ResultDTO desbloquearCarril(CarrilesUbicacionDTO carriles, String werks) {

		ResultDTO resultDT = new ResultDTO();
		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		String msg = "";

		for (int x = 0; x < carriles.getItem().size(); x++) {

			CarrilUbicacionDTO carril = carriles.getItem().get(x);

			if (carril.getIdProceso().equals("1")// Entregas
					|| carril.getIdProceso().equals("4") || carril.getIdProceso().equals("6")) {

				if (carril.getIdProceso().equals("6"))
					carril.setIdProceso("4");

				carril.setEntrega(Utils.zeroFill(carril.getEntrega(), 10));

			} else if (carril.getIdProceso().equals("2")// Orden produccion
					|| carril.getIdProceso().equals("3") || carril.getIdProceso().equals("5")) {

				carril.setEntrega(Utils.zeroFill(carril.getEntrega(), 12));

			}

			if (carril.getTipoAlmacen().equals("wm")) {
				msg += "El carrill: " + carril.getLgnum() + "," + carril.getLgtyp() + "," + carril.getLgpla() + " ";
				carril.setTipoAlmacen("1");
			} else if (carril.getTipoAlmacen().equals("im")) {

				msg += "El material: " + carril.getMaterial() + " ";
				carril.setTipoAlmacen("3");

				carril.setMaterial(Utils.zeroFill(carril.getMaterial(), 18));
			}

			LOCATION.error("ENTREGA: " + carril.getEntrega());
			LOCATION.error("MATERIAL: " + carril.getMaterial());
			LOCATION.error("Tipo Almacen: " + carril.getTipoAlmacen());

			resultDT = supervisorUtilsDAO.limpiaCarril(carril);

			// --6, al liberar carrill no es posible porque tiene pendientes
			// --2, no fue posible liberar el carril
			// --3, No tiene pendientes
			// --4, no fue posible liberar material
			// --5, no tiene pendientes
			// --1, OK

			switch (resultDT.getId()) {

			case 1:
				msg += "ha sido desbloqueado exitosamente\n";
				break;
			case 2:
				msg += "no fue posible liberar el carril\n";
				break;
			case 3:
				msg += "no tiene pendientes\n";
				break;
			case 4:
				msg += "no fue posible liberar\n";
				break;
			case 5:
				msg += "no tiene pendientes\n";
				break;
			case 6:
				msg += "no se puede liberar debido a que se tienen HUs en transporte, libere Hus antes\n";
				break;
			default:
				msg += resultDT.getMsg() + "\n";
				break;

			}

		}

		resultDT.setMsg(msg);

		return resultDT;

	}

	@Override
	public HusEnTransporteDTO obtieneHusBloqueados(HUsEnTransporteDetalleDTO husEnTransporteDetalleDTO, String werks) {

		HusEnTransporteDTO husEnTransporteDTO = new HusEnTransporteDTO();
		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		if (husEnTransporteDetalleDTO.getIdProceso().equals("2")) {
			// Orden de produccion
			husEnTransporteDetalleDTO.setVbeln(Utils.zeroFill(husEnTransporteDetalleDTO.getVbeln(), 12));

		} else if (husEnTransporteDetalleDTO.getIdProceso().equals("4")
				|| husEnTransporteDetalleDTO.getIdProceso().equals("6")) {

			if (husEnTransporteDetalleDTO.getIdProceso().equals("6"))
				husEnTransporteDetalleDTO.setIdProceso("4");

			// Entregas
			husEnTransporteDetalleDTO.setVbeln(Utils.zeroFill(husEnTransporteDetalleDTO.getVbeln(), 10));

		}

		if (husEnTransporteDetalleDTO.getTipoAlmacen().equals("im")) {

			// IM
			husEnTransporteDetalleDTO.setMatnr(Utils.zeroFill(husEnTransporteDetalleDTO.getMatnr(), 18));
			husEnTransporteDTO = supervisorUtilsDAO.obtieneHusIMBloqueados(husEnTransporteDetalleDTO, werks);

		} else if (husEnTransporteDetalleDTO.getTipoAlmacen().equals("wm")) {
			// WM

			husEnTransporteDTO = supervisorUtilsDAO.obtieneHusBloqueados(husEnTransporteDetalleDTO, werks);
		}

		if (husEnTransporteDTO.getResultDT().getId() == 1) {

			for (int x = 0; x < husEnTransporteDTO.getItems().getItem().size(); x++) {

				HUsEnTransporteDetalleDTO hu = husEnTransporteDTO.getItems().getItem().get(x);

				hu.setMatnr(Utils.zeroClean(hu.getMatnr()));
				hu.setVbeln(husEnTransporteDetalleDTO.getVbeln());

				if (husEnTransporteDetalleDTO.getIdProceso().equals("2")
						|| husEnTransporteDetalleDTO.getIdProceso().equals("4")) {
					// hus no conocidas

					// hus conocidas
					if (hu.getStatus() == null) {
						hu.setHu("---------");
						hu.setUsuarioMontacarguista("---------");
						hu.setStatus("Programado");
					} else {
						if (hu.getStatus().equals("1")) {
							hu.setHu("---------");
							hu.setStatus("En transporte");
						} else {
							hu.setStatus("Finalizado");
						}
					}

				} else if (husEnTransporteDetalleDTO.getIdProceso().equals("1")
						|| husEnTransporteDetalleDTO.getIdProceso().equals("3")) {

					// hus conocidas
					if (hu.getHu() == null) {
						hu.setHu("---------");
						hu.setUsuarioMontacarguista("---------");
						hu.setStatus("Programado");
					} else {
						if (hu.getStatus() == null) {
							hu.setStatus("En transporte");
						} else {
							hu.setStatus("Finalizado");
						}
					}
				}

			}

		}

		return husEnTransporteDTO;

	}

	@Override
	public ResultDTO liberarHusEnTransporte(CarrilesUbicacionDTO carriles, String werks) {

		ResultDTO resultDT = new ResultDTO();
		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		for (int x = 0; x < carriles.getItem().size(); x++) {

			CarrilUbicacionDTO carril = carriles.getItem().get(x);

			String msg = "";

			if (carril.getIdProceso().equals("4")// Entregas
					|| carril.getIdProceso().equals("6")) {

				if (carril.getIdProceso().equals("6"))
					carril.setIdProceso("4");

				carril.setEntrega(Utils.zeroFill(carril.getEntrega(), 10));

			} else if (carril.getIdProceso().equals("2"))// Orden produccion
			{
				carril.setEntrega(Utils.zeroFill(carril.getEntrega(), 12));
			}

			if (carril.getTipoAlmacen().equals("wm")) {
				carril.setTipoAlmacen("2");
			} else if (carril.getTipoAlmacen().equals("im")) {

				carril.setTipoAlmacen("4");
				carril.setMaterial(Utils.zeroFill(carril.getMaterial(), 18));
			}

			resultDT = supervisorUtilsDAO.limpiaCarril(carril);

			// --6, al liberar carrill no es posible porque tiene pendientes
			// --2, no fue posible liberar el carril
			// --3, No tiene pendientes
			// --4, no fue posible liberar material
			// --5, no tiene pendientes
			// --1, OK

			LOCATION.error("Error al liberar carril: " + resultDT.getId());

			switch (resultDT.getId()) {

			case 1:
				msg += "ha sido desbloqueado exitosamente\n";
				break;
			case 2:
				msg += "no fue posible liberar el carril\n";
				break;
			case 3:
				msg += "No tiene pendientes\n";
				break;
			case 4:
				msg += "No fue posible liberar\n";
				break;
			case 5:
				msg += "No tiene pendientes\n";
				break;
			case 6:
				msg += "no se puede liberar carril debido a que se tienen HUs en transporte, libere Hus antes\n";
				break;
			default:
				msg += resultDT.getMsg() + "\n";
				break;

			}

			resultDT.setMsg(msg);

		}
		return resultDT;
	}

	@Override
	public ResultDTO validaCarril(CarrilUbicacionDTO carril) {

		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		return supervisorUtilsDAO.validarCarril(carril);
	}

	@Override
	public TablasSqlDTO cargaBCPS(String werks, String user) {

		TablasSqlDTO tablasSqlDTO = new TablasSqlDTO();
		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();
		TablasSqlItemDTO tablasSqlItemDTO = new TablasSqlItemDTO();
		List<TablaSqlDTO> listTablasSqlItemDTO = new ArrayList<TablaSqlDTO>();

		FTPConfDTO ftpConf = new FTPConfDTO();
		ResultDTO resultDT = new ResultDTO();

		tablasSqlItemDTO.setItem(listTablasSqlItemDTO);

		resultDT = supervisorUtilsDAO.validaInicioBCPS(werks);

		tablasSqlDTO.setItems(tablasSqlItemDTO);
		tablasSqlDTO.setResultDT(resultDT);

		Calendar calendar = new GregorianCalendar();

		String logId = "LogId-> " + calendar.getTimeInMillis() + " ";
		boolean errorGetTable = false;

		if (resultDT.getId() == 1) {

			ftpConf = supervisorUtilsDAO.getFTPConf();
			resultDT = ftpConf.getResultDT();

			if (resultDT.getId() == 1) {// OK datos FTP

				ListaDTO listaTablas = new ListaDTO();

				listaTablas = supervisorUtilsDAO.getTablas(werks);
				resultDT = listaTablas.getResultDT();

				// Cambio temporal para carga de archivos "1"
				if (resultDT.getId() == 1) {

					JSch jsch = new JSch();
					Session session = null;
					Channel channel = null;
					ChannelSftp sftpChannel = null;

					try {
						/*
						jsch.addIdentity("C:\\SFTP_KEY\\bcps_private.ppk");
						session = jsch.getSession(ftpConf.getUser(), ftpConf.getIp(),
								Integer.parseInt(ftpConf.getPuerto()));

						session.setConfig("StrictHostKeyChecking", "no");

						session.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
						session.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");

						// session.setPassword(ftpConf.getPassword());

						session.connect();

						channel = session.openChannel("sftp");

						channel.connect();
						sftpChannel = (ChannelSftp) channel;
						*/
						// C:/DATAUPLOAD/UTF8/
						File folder = new File(pathOut);
						folder.mkdir();
						
						for (int i = 0; i < listaTablas.getLista().size(); i++) {

							TablaSqlDTO tablaSqlDTO = new TablaSqlDTO();

							tablaSqlDTO.setIdTablaSQL(listaTablas.getLista().get(i));

							try {

								if (listaTablas.getLista().get(i).equals("TB_BCPS_NEW_HU")) {
									tablaSqlDTO.setIdTablaSQL("ZPAITT_CONT_HU");

									listaTablas.getLista().set(i, "ZPAITT_CONT_HU");

								}
								if (listaTablas.getLista().get(i).equals("TB_BCPS_ZFACT")) {
									tablaSqlDTO.setIdTablaSQL("ZPAITT_FACTURA");

									listaTablas.getLista().set(i, "ZPAITT_FACTURA");
								}
								if (listaTablas.getLista().get(i).equals("TB_BCPS_NEW_VBELN")) {
									tablaSqlDTO.setIdTablaSQL("ZPAITT_ENTREGA");

									listaTablas.getLista().set(i, "ZPAITT_ENTREGA");
								}

								/*
								sftpChannel.get(
										"/Datamart/dataBCP/" + ftpConf.getFolder() + "/_"
												+ listaTablas.getLista().get(i) + "_" + werks + ".txt",
										"C:/DATAUPLOAD/UTF8/_" + listaTablas.getLista().get(i) + "_" + werks + ".txt");
								*/
								// C:/DATAUPLOAD/UTF8
								FileInputStream fis = new FileInputStream(pathIn + "_" + listaTablas.getLista().get(i) + "_" + werks + ".txt");
								
								/**
								 * Se cambia encoding UTF-8 para evitar problemas al ejecutar el bulk, abap
								 * debio dejarlo en 1252
								 **/

								InputStreamReader isr = new InputStreamReader(fis, "UTF8");

								Reader in = new BufferedReader(isr);
								StringBuffer buffer = new StringBuffer();

								int ch;
								while ((ch = in.read()) > -1) {
									buffer.append((char) ch);
								}

								in.close();

								FileOutputStream fos = new FileOutputStream(pathOut + "_" + listaTablas.getLista().get(i) + "_" + werks + ".txt");

								Writer out = new OutputStreamWriter(fos, "windows-1252");

								out.write(buffer.toString());
								out.close();

								tablaSqlDTO.setId(1);
								tablaSqlDTO.setMsg(logId + "La tabla se descargó correctamente del SFTP");

								// tablaSqlDTO.setImg("img/error.png");

							/*} catch (SftpException e) {
								errorGetTable = true;
								tablaSqlDTO
										.setMsg(logId + "No fue posible descargar la tabla del SFTP: SftpException ->"
												+ e.toString());

								LOCATION.error(logId + "SftpException: " + e.toString() + e.getMessage() + " "
										+ "/Datamart/dataBCP/" + ftpConf.getFolder() + "/_"
										+ listaTablas.getLista().get(i) + "_" + werks + ".txt" + " --> "
										+ listaTablas.getLista().get(i) + "_" + werks + ".txt ");
							 */
							} catch (FileNotFoundException e) {
								errorGetTable = true;
								tablaSqlDTO.setMsg(
										logId + "No fue posible descargar la tabla del SFTP: FileNotFoundException ->"
												+ e.toString());

								LOCATION.error(logId + "FileNotFoundException: " + e.toString());

							} catch (UnsupportedEncodingException e) {
								errorGetTable = true;
								LOCATION.error(logId + "UnsupportedEncodingException: " + e.toString());

								tablaSqlDTO.setMsg(logId
										+ "No fue posible descargar la tabla del SFTP: UnsupportedEncodingException ->"
										+ e.toString());

							} catch (IOException e) {
								errorGetTable = true;
								LOCATION.error(logId + "IOException: " + e.toString());

								tablaSqlDTO.setMsg(logId + "No fue posible descargar la tabla del SFTP: IOException ->"
										+ e.toString());
							}

							listTablasSqlItemDTO.add(tablaSqlDTO);
						}
						/*
						sftpChannel.exit();
						session.disconnect();
						*/
					/*} catch (JSchException e) {
						errorGetTable = true;
						LOCATION.error(logId + "JSchException: " + e.toString());

						resultDT.setId(2);
						resultDT.setMsg(logId + "Error: JSchException -> " + e.toString());
						*/
					} finally {

						try {
							/*
							sftpChannel.exit();
							session.disconnect();
							*/
						} catch (Exception e) {
							LOCATION.error(logId + "Error al cerrar SFTP: " + e.toString());
						}
					}

				}

			}

			/** Las tablas ya se encuentran en servidor BCPS **/

			/** Las tablas ya se encuentran en servidor BCPS **/
			if (!errorGetTable) {

				resultDT = supervisorUtilsDAO.limpiaTablasCentro(werks);

				if (resultDT.getId() == 1) {

					boolean bulkError = false;

					for (int x = 0; x < listTablasSqlItemDTO.size(); x++) {

						if (listTablasSqlItemDTO.get(x).getId() == 1) {

							resultDT = supervisorUtilsDAO.bulk(listTablasSqlItemDTO.get(x).getIdTablaSQL(), werks);

							if (resultDT.getId() == 1) {

								listTablasSqlItemDTO.get(x).setId(1);
								listTablasSqlItemDTO.get(x).setMsg(

										"Tabla " + listTablasSqlItemDTO.get(x).getIdTablaSQL()
												+ " cargada con éxito para centro " + werks);
								listTablasSqlItemDTO.get(x).setImg("img/ok.png");

							} else {

								bulkError = true;
								listTablasSqlItemDTO.get(x).setId(2);
								listTablasSqlItemDTO.get(x)
										.setMsg("Error al cargar tabla " + listTablasSqlItemDTO.get(x).getIdTablaSQL()
												+ " para centro " + werks + ". " + resultDT.getMsg());
								listTablasSqlItemDTO.get(x).setImg("img/error.png");
								resultDT.setId(2);

							}
						} else {
							bulkError = true;
							listTablasSqlItemDTO.get(x).setImg("img/error.png");
						}
					}
					if (bulkError) {

						resultDT.setMsg(
								"Se presentó un error el realizar la descarga de información a BCPS,  " + logId);
						resultDT.setId(2);
						resultDT.setTypeS("img/error.png");

						if (supervisorUtilsDAO.eliminaDuplicados().getId() == 1) {

							resultDT.setId(2);
							resultDT.setMsg(resultDT.getMsg()
									+ "El proceso de eliminación de duplicados se ejecuto correctamente");

						}

					} else {

						resultDT.setId(1);
						resultDT.setMsg("Los datos del centro " + werks + " se cargaron correctamente en el sistema. ");
						resultDT.setTypeS("img/ok.png");

						// Cambio temporal para carga de archivos "1"
						int fallo = supervisorUtilsDAO.eliminaDuplicados().getId();
						if (fallo == -1) {

							// AGREGAR PROCESO BLOQUEO DE CALIDAD

							resultDT.setId(1);
							resultDT.setMsg(resultDT.getMsg()
									+ "El proceso de eliminación de duplicados se ejecuto correctamente");

							// Borrar archivos BCPs
							// supervisorUtilsDAO.removeQuality(); ELIMINA
							// CALIDAD

							JSch jsch = new JSch();
							Session session = null;
							Channel channel = null;
							ChannelSftp sftpChannel = null;

							try {
								jsch.addIdentity("C:\\SFTP_KEY\\bcps_private.ppk");
								session = jsch.getSession(ftpConf.getUser(), ftpConf.getIp(),
										Integer.parseInt(ftpConf.getPuerto()));

								session.setConfig("StrictHostKeyChecking", "no");
								session.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
								session.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");

								// session.setPassword(ftpConf.getPassword());

								session.connect();

								channel = session.openChannel("sftp");

								channel.connect();
								sftpChannel = (ChannelSftp) channel;

								LOCATION.error("Eliminar SFTP");
								for (int x = 0; x < listTablasSqlItemDTO.size(); x++) {

									TablaSqlDTO tablaSqlDTO = listTablasSqlItemDTO.get(x);

									LOCATION.error("Eliminar SFTP ->" + tablaSqlDTO.getIdTablaSQL());
									try {

										sftpChannel.rm("/Datamart/dataBCP/" + ftpConf.getFolder() + "/_"
												+ tablaSqlDTO.getIdTablaSQL() + "_" + werks + ".txt");

										tablaSqlDTO.setLogDeleteSFTP("Eliminado correctamente");

									} catch (SftpException e) {

										tablaSqlDTO.setLogDeleteSFTP(
												"No fue posible borrar archivo del SFTP: SftpException ->"
														+ e.toString());

										LOCATION.error(logId + "SftpException: " + e.toString() + e.getMessage() + " "
												+ "/Datamart/dataBCP/" + ftpConf.getFolder() + "/_"
												+ listTablasSqlItemDTO.get(x).getIdTablaSQL() + "_" + werks + ".txt");

									}

								}

								sftpChannel.exit();
								session.disconnect();

							} catch (JSchException e) {

								LOCATION.error(logId + "JSchException: " + e.toString());

								resultDT.setId(2);
								resultDT.setMsg(logId + "Error: JSchException -> " + e.toString());

							} finally {

								try {

									sftpChannel.exit();
									session.disconnect();

								} catch (Exception e) {
									LOCATION.error(logId + "Error al cerrar SFTP: " + e.toString());
								}
							}

							// ////////////////////////////////////////////////////////////////////////////////////////
							// DE ULTIMO MIN, INVENTARIO INICIAL
							supervisorUtilsDAO.initialSnapshot(werks);

							// ///////////////

						} else {
							//resultDT.setTypeS("img/error.png");
							resultDT.setMsg(resultDT.getMsg() + "El proceso de eliminación de duplicados FALLO : " + fallo);
						}

					}

				} else {
					resultDT.setId(2);
					resultDT.setTypeS("img/error.png");
					resultDT.setMsg(
							"Error al ejecutar el SP sp_bcps_utils_elimina_datos_por_centro, los datos pueden son inconsistentes");
				}
			} else {
				resultDT.setId(2);
				resultDT.setTypeS("img/error.png");
				resultDT.setMsg(
						"Error al obtener todas las tablas definidas para el centro, imposible realizar carga.");
			}

		}

		tablasSqlDTO.setResultDT(resultDT);

		return tablasSqlDTO;

	}

	@Override
	public ResultDTO cargaSAP(String werks, String user) {

		ZContingenciaRepository zContingencia = new ZContingenciaRepository();

		ResultDTO resultDT = new ResultDTO();

		resultDT = zContingencia.zContingencia(user, werks);

		return resultDT;

	}

	@Override
	public ResultDTO switchUbsBCPS(String werks, String password) {
		ResultDTO resultDT = new ResultDTO();

		try {

			if (password.equals("cr52401")) {

				Runtime.getRuntime().exec("cmd /c start D:/BAT_UBS/BCPS_" + werks + ".bat  exit");

				resultDT.setId(1);
				resultDT.setMsg("El cambio de UBS a BCPS fue exitoso");
			} else {
				resultDT.setId(2);
				resultDT.setMsg("Password invalido");
			}
		} catch (Exception ex) {

			resultDT.setId(2);
			resultDT.setMsg("Error: " + ex.toString());

		}

		return resultDT;
	}

	@Override
	public ResultDTO switchUbsSAP(String werks, String password) {
		ResultDTO resultDT = new ResultDTO();
		try {
			if (password.equals("cr52401")) {
				Runtime.getRuntime().exec("cmd /c start D:/BAT_UBS/SAP_" + werks + ".bat  exit");

				resultDT.setId(1);
				resultDT.setMsg("El cambio de UBS a SAP fue exitoso");
			} else {
				resultDT.setId(2);
				resultDT.setMsg("Password invalido");
			}
		} catch (IOException ex) {

			resultDT.setId(2);
			resultDT.setMsg("Error: " + ex.toString());

		}
		return resultDT;
	}

	@Override
	public UsuarioItemDTO buscarUsuario(String user) {

		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		UsuarioItemDTO result = supervisorUtilsDAO.buscarUsuario(user);

		return result;
	}

	@Override
	public ResultDTO eliminarUsuario(UsuarioDTO user, String werksAdmin) {

		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		ResultDTO result = supervisorUtilsDAO.eliminarUsuario(user.getIdUsuario());

		if (werksAdmin.equals(user.getWerks())) {
			if (result.getId() == 3) {
				result.setId(1);
				result.setMsg("Usuario eliminado con exito");
			} else {
				result.setId(2);
				result.setMsg("Error al elminar el usuario");
			}
		} else {

			result.setId(2);
			result.setMsg("No es posible eliminar un usuario que no sea de tu centro");

		}

		return result;
	}

	@Override
	public ResultDTO crearUsuario(UsuarioDTO user, String werksAdmin) {

		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		ResultDTO result = new ResultDTO();
		UsuarioItemDTO usuarioItemDTO = new UsuarioItemDTO();

		usuarioItemDTO = supervisorUtilsDAO.buscarUsuario(user.getIdUsuario());

		if (usuarioItemDTO.getResult().getId() == 1) {

			result.setId(2);
			result.setMsg("El usuario que intenta crear ya existe");

		} else if (usuarioItemDTO.getResult().getId() == 2) {
			result = supervisorUtilsDAO.crearUsuario(user);

			LOCATION.error("idUsuario: " + user.getIdUsuario());
			LOCATION.error("name: " + user.getName());

			if (result.getId() == 1) {
				result.setId(1);
				result.setMsg("Usuario creado con exito");
			} else {

				result.setMsg("Error al crear el usuario : " + result.getId());
				result.setId(2);
			}
		} else {

			result.setId(2);
			result.setMsg("No fue posible validar si el usuario a crear existe");

		}

		return result;
	}

	@Override
	public ResultDTO modificarUsuario(UsuarioDTO user, String werksAdmin) {

		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		ResultDTO result = supervisorUtilsDAO.crearUsuario(user);

		LOCATION.error("idUsuario: " + user.getIdUsuario());
		LOCATION.error("name: " + user.getName());

		if (werksAdmin.equals(user.getWerks())) {

			if (result.getId() == 2) {
				result.setId(1);
				result.setMsg("Usuario modificado con exito");
			} else {
				result.setId(2);
				result.setMsg("Error al modifcar el usuario");
			}
		} else {

			result.setId(2);
			result.setMsg("No es posible modificar un usuario que no sea de tu centro");
		}

		return result;
	}

	@Override
	public EntregasTransportesDTO obtieneEntregasAgencias(UsuarioDTO usuario) {

		EntregasTransportesDTO entregasTransportes = new EntregasTransportesDTO();
		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		entregasTransportes = supervisorUtilsDAO.obtieneEntregasAgencias(usuario);

		if (entregasTransportes.getResultDT().getId() == 1) {

			for (int x = 0; x < entregasTransportes.getItems().getItem().size(); x++) {

				entregasTransportes.getItems().getItem().get(x).setVblenEntrante(
						Utils.zeroClean(entregasTransportes.getItems().getItem().get(x).getVblenEntrante()));
				entregasTransportes.getItems().getItem().get(x).setVblenSaliente(
						Utils.zeroClean(entregasTransportes.getItems().getItem().get(x).getVblenSaliente()));
//				entregasTransportes.getItems().getItem().get(x)
//						.setLfart(
//								Utils.zeroClean(entregasTransportes.getItems()
//										.getItem().get(x).getLfart()));

			}

		}

		return entregasTransportes;

	}

	@Override
	public CarrilesBloqueadosDTO obtieneCarrilBloqueado(UsuarioDTO usuario) {

		CarrilesBloqueadosDTO carrilBloqueado = new CarrilesBloqueadosDTO();
		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		carrilBloqueado = supervisorUtilsDAO.obtieneCarrilesBloqueados(usuario);

		if (carrilBloqueado.getResultDT().getId() == 1) {

			for (int x = 0; x < carrilBloqueado.getItems().getItem().size(); x++) {

				carrilBloqueado.getItems().getItem().get(x)
						.setVbeln(Utils.zeroClean(carrilBloqueado.getItems().getItem().get(x).getVbeln()));
			}

		}

		return carrilBloqueado;

	}

	@Override
	public EmbarqueDTO obtieneEntregasAgenciasDetalle(String vbeln) {

		SupervisorUtilsRepository supervisorUtilsDAO = new SupervisorUtilsRepository();

		EmbarqueDTO embarqueReturn = new EmbarqueDTO();
		ResultDTO resultDT = new ResultDTO();

		vbeln = vbeln.replaceAll("[^0-9]+", "");
		vbeln = Utils.zeroFill(vbeln, 10);
		embarqueReturn = supervisorUtilsDAO.obtieneEntregasAgenciasDetalle(vbeln);

		if (embarqueReturn.getResultDT().getId() == 1) {

			for (int x = 0; x < embarqueReturn.getItems().getItem().size(); x++) {

				LOCATION.error("Item: " + embarqueReturn.getItems().getItem().get(x).getMaterial());

				embarqueReturn.getItems().getItem().get(x).setMaterial(Utils.zeroClean(

						embarqueReturn.getItems().getItem().get(x).getMaterial()));
			}

		} else {

			resultDT.setId(2);
			resultDT.setMsg(embarqueReturn.getResultDT().getMsg());
			embarqueReturn.setResultDT(resultDT);
		}

		return embarqueReturn;

	}
}
