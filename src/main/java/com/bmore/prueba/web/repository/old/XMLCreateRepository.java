package com.bmore.prueba.web.repository.old;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.prueba.config.DBConnection;
import com.bmore.prueba.web.utils.Utils;
import com.bmore.prueba.web.utils.export.xmlgeneration.XMLAddressDTO;
import com.bmore.prueba.web.utils.export.xmlgeneration.XMLAddressWrapperDTO;
import com.bmore.prueba.web.utils.export.xmlgeneration.XMLCreateDTO;
import com.bmore.prueba.web.utils.export.xmlgeneration.XMLCustomDTO;
import com.bmore.prueba.web.utils.export.xmlgeneration.XMLDetailsDTO;
import com.bmore.prueba.web.utils.export.xmlgeneration.XMLFTPUserDTO;
import com.bmore.prueba.web.utils.export.xmlgeneration.XMLRootDTO;
import com.bmore.prueba.web.utils.export.xmlgeneration.XMLTotalDTO;

public class XMLCreateRepository {

	private static String getDataRootBOMA = "SELECT BKDES, FIIDT FROM VS_BCPS_GET_APROV WHERE VBELN = ?";
	private static String getDataRootVBAK = "SELECT BSTNK, AUDAT, CONVERT(VARCHAR(10),DATEADD(DAY, ?, CONVERT(DATE, VDATU, 112)), 112) AS VDATU FROM VS_BCPS_XML_ORDEN_COMPRA WITH(NOLOCK) WHERE VBELN = ?";
	private static String getTermPagoDias = "EXEC SP_BCPS_GET_TERM_PAGO_DIAS ?,?,?";
	
	private static String getEmisorExpedicion = "SELECT PAVAL, BAHNE, STREET, HOUSE_NUM1, HOUSE_NUM2, CITY2, CITY1, POST_CODE1, BEZEI, LANDX FROM VS_BCPS_XML_EMISOR WHERE VBELN = ?";

	private static String getPedidoReceptor = "SELECT DISTINCT KUNNR FROM VBAK WHERE VBELN = (SELECT DISTINCT VBELV FROM VBFA WITH(NOLOCK) WHERE VBELN = ?)";
	private static String getReceptorFiscal = "SELECT KUNNR, STCD1, KNA1NAME, KNVKNAME, STREET, HOUSE_NUM1, HOUSE_NUM2, CITY2, CITY1, POST_CODE1, BEZEI, LANDX FROM VS_BCPS_RECEPTOR_FISCAL WHERE KUNNR =  ? ";
	private static String getReceptorExpedicion = "SELECT BAHNE, STREET, HOUSE_NUM1, HOUSE_NUM2, CITY2, CITY1, POST_CODE1, BEZEI, LANDX FROM VS_BCPS_RECEPTOR_EXPEDICION WHERE VBELN = ?";

	private static String getFillDetailPedido = "SELECT POSNR FROM LIPS WITH(NOLOCK) WHERE VBELN = ?";
	private static String getDataFillDetailPedido = "SELECT KWMENG, ARKTX, MATRN, KNUMV, VRKME, UMREN,  KBETR, KWERT, KSCHL, ALFANUM1, WAERS, LOW FROM VS_BCPS_XML_DETAILS WHERE VBELN = ? AND POSNV = ? AND KSCHL = ? ";
	private static String getDataAmountPedido = "SELECT PRECIO, MONTO, DESCUENTO, MONTODESCUENTO, IVA, MONTOIVA FROM VS_BCPS_XML_DETAILS_AMOUNT WITH(NOLOCK) WHERE VBELN = ? AND POSNR = ?";

	private static String getDataFactor = "EXEC SP_BCPS_XML_UTIL_FACTOR_CONVERCION ?,?,?";

	private static final String FTPACCESS = "SELECT IP, PORT, [USER], [PASSWORD],TYPE FROM TB_BCPS_FTP_DATAMART WITH(NOLOCK)";

	private final static String EXECFOLIO = "exec sp_bcps_generate_facturas ?,?,?";
	private final static String GETFOLIO = "SELECT RANGE_S FROM TB_BCPS_ZFACT WITH(NOLOCK) WHERE VBELN = ? ";

	private static final String XMLZCONT = "exec sp_bcps_wm_contabilizar_entrega_salida_factura ?,?,?,?,?";

	private static final String getCustomAdua = "SELECT NAME1 FROM VS_BCPS_ADUANA WHERE VBELN = ?";
	private static final String getCustomA = "SELECT TOP 1 TELF1, NAMEV, KVNAME1, K1NAME1, K1NAME2, BSTNK, KUNNR, TNDR_TRKID, TPBEZ, "
			+ "L1NAME1, SORTL, SIGNI, EXTI2, TEXT3, ADNAME1 FROM VS_BCPS_XML_CUSTOM_A WITH(NOLOCK) WHERE VBELN =  ?";
	private static final String exportacionValues = "SELECT SELLO, NOCAJA, TALONEMBARQUE, OPERADOR, SELLO_IMPORTADOR FROM zContingencia WITH(NOLOCK) WHERE ENTREGA = ? AND IDPROC = 12";
	private static final String getCustomB = "SELECT INCO1, VKORG, VKBUR, VKKUNNR, VTWEG, LIFNR, LKKUNNR, KDKG5 FROM VS_BCPS_XML_CUSTOM_B WITH(NOLOCK) WHERE VBELN = ?";
	private static final String getCustomC = "SELECT LIBRAS, GALONES, BRGEW FROM VS_BCPS_XML_CUSTOM_C WITH(NOLOCK) WHERE VBELN = ? ";
	private static final String getCustomRep = "SELECT REPRESENTANTE_LEGAL, STRAS FROM VS_BCPS_XML_LEGAL WHERE VBELN  = ?";
	
	private static final Logger LOCATION = LoggerFactory.getLogger(XMLCreateRepository.class);

	@SuppressWarnings("static-access")
	public XMLCreateDTO fillXML(String VBELN, String WERKS,
			XMLCreateDTO createDTO) {
		XMLRootDTO rootDTO = new XMLRootDTO();
		XMLAddressWrapperDTO wrapperEmisor = new XMLAddressWrapperDTO();
		XMLAddressWrapperDTO wrapperReceptor = new XMLAddressWrapperDTO();
		XMLAddressDTO emisorExped = new XMLAddressDTO();
		XMLAddressDTO remisorExped = new XMLAddressDTO();
		XMLAddressDTO remisorFiscal = new XMLAddressDTO();

		Connection con = DBConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(getDataRootBOMA);
			stm.setString(1, VBELN);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				rootDTO.setNumeroAprobacion(rs.getString("BKDES"));
				rootDTO.setFechaAprobacion(rs.getString("FIIDT"));
				
				rootDTO.setFolioInterno(getFolioFact(VBELN, WERKS)); 
				CallableStatement cst = con.prepareCall(getTermPagoDias);
				cst.setString(1, VBELN);
				cst.setString(2, WERKS);
				cst.registerOutParameter(3, java.sql.Types.VARCHAR);
				cst.execute();
				rootDTO.setTermPagoDias(Utils.isNull(cst.getString(3)));
				stm = con.prepareStatement(getDataRootVBAK);
				stm.setInt(1, Integer.parseInt( rootDTO.getTermPagoDias()) );
				stm.setString(2, VBELN);
				rs = stm.executeQuery();
				if (rs.next()) {
					rootDTO.setOrdenCompra(rs.getString("BSTNK"));
					rootDTO.setFechaOrdenCompra(rs.getString("AUDAT"));
					rootDTO.setFechaVencimiento(rs.getString("VDATU"));
					createDTO.setRoot(rootDTO);
					stm = con.prepareStatement(getEmisorExpedicion);
					stm.setString(1, VBELN);
					rs = stm.executeQuery();
					if (rs.next()) {
						wrapperEmisor.setRfc(rs.getString("PAVAL"));
						emisorExped.setGln(rs.getString("BAHNE") == null ? ""
								: rs.getString("BAHNE"));
						emisorExped.setCalle(rs.getString("STREET"));
						emisorExped.setNoExterior(rs.getString("HOUSE_NUM1"));
						emisorExped
								.setNoInterior(rs.getString("HOUSE_NUM2") == null ? ""
										: rs.getString("HOUSE_NUM2"));
						emisorExped.setColonia(rs.getString("CITY2"));
						emisorExped.setLocalidad(rs.getString("CITY1"));
						emisorExped.setMunicipio(rs.getString("CITY1"));
						emisorExped.setEstado(rs.getString("BEZEI"));
						emisorExped.setPais(rs.getString("LANDX"));
						emisorExped.setCodigoPostal(rs.getString("POST_CODE1"));
						wrapperEmisor.setDomicilioExpedicion(emisorExped);
						createDTO.setEmisor(wrapperEmisor);
						stm = con.prepareStatement(getPedidoReceptor);
						stm.setString(1, VBELN);
						rs = stm.executeQuery();
						if (rs.next()) {
							String clientePedido = rs.getString("KUNNR");
							stm = con.prepareStatement(getReceptorFiscal);
							stm.setString(1, clientePedido);
							rs = stm.executeQuery();
							if (rs.next()) {
								wrapperReceptor.setRfc(rs.getString("STCD1"));
								wrapperReceptor.setNombre(rs
										.getString("KNA1NAME"));
								wrapperReceptor.setNumeroCliente(Utils
										.zeroClean(clientePedido));
								wrapperReceptor.setContacto(rs
										.getString("KNVKNAME"));
								remisorFiscal.setCalle(rs.getString("STREET"));
								remisorFiscal.setNoExterior(rs
										.getString("HOUSE_NUM1"));
								remisorFiscal.setNoInterior(rs
										.getString("HOUSE_NUM2") == null ? ""
										: rs.getString("HOUSE_NUM2"));
								remisorFiscal.setColonia(rs.getString("CITY2"));
								remisorFiscal.setLocalidad(rs
										.getString("CITY1") == null ? "" : rs
										.getString("CITY1"));
								remisorFiscal.setMunicipio(rs
										.getString("CITY1") == null ? "" : rs
										.getString("CITY1"));
								remisorFiscal.setEstado(rs.getString("BEZEI"));
								remisorFiscal.setPais(rs.getString("LANDX"));
								remisorFiscal.setCodigoPostal(rs
										.getString("POST_CODE1"));
								wrapperReceptor
										.setDomicilioFiscal(remisorFiscal);
								stm = con
										.prepareStatement(getReceptorExpedicion);
								stm.setString(1, VBELN);
								rs = stm.executeQuery();
								if (rs.next()) {
									remisorExped.setCalle(rs
											.getString("STREET"));
									remisorExped.setNoExterior(rs
											.getString("HOUSE_NUM1"));
									remisorExped
											.setNoInterior(rs
													.getString("HOUSE_NUM2") == null ? ""
													: rs
															.getString("HOUSE_NUM2"));
									remisorExped.setColonia(rs
											.getString("CITY2"));
									remisorExped.setLocalidad(rs
											.getString("CITY1") == null ? ""
											: rs.getString("CITY1"));
									remisorExped.setMunicipio(rs
											.getString("CITY1") == null ? ""
											: rs.getString("CITY1"));
									remisorExped.setEstado(rs
											.getString("BEZEI"));
									remisorExped.setPais(rs.getString("LANDX"));
									remisorExped.setCodigoPostal(rs
											.getString("POST_CODE1"));
									wrapperReceptor
											.setDomicilioExpedicion(remisorExped);
									createDTO.setReceptor(wrapperReceptor);
								} else {
									remisorExped = new XMLAddressDTO()
											.XMLAddressDTOEmpty(remisorExped);
								}
							} else {
								wrapperReceptor = new XMLAddressWrapperDTO()
										.XMLAddressWrapperDTOEmpty(wrapperReceptor);
							}
						} else {
							//createDTO = null;
						}
					} else {
						wrapperEmisor = new XMLAddressWrapperDTO()
								.XMLAddressWrapperDTOEmpty(wrapperEmisor);
					}
				} else {
					//createDTO = null;
				}
			} else {
				//createDTO = null;
			}
		} catch (SQLException e) {
			LOCATION.error(e.getLocalizedMessage());
		} catch (Exception e) {
			LOCATION.error(e.getLocalizedMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error(e.getLocalizedMessage());
			}
		}
		LOCATION.error(createDTO.toString());
		return createDTO;
	}

	public XMLTotalDTO fillTotal(String VBELN) {
		XMLTotalDTO totDTO = new XMLTotalDTO();
		Connection con = DBConnection.createConnection();
		try {
			CallableStatement stm = con.prepareCall(getDataFactor);
			stm.setString(1, VBELN);
			stm.registerOutParameter(2, java.sql.Types.VARCHAR);
			stm.registerOutParameter(3, java.sql.Types.VARCHAR);
			stm.execute();
			totDTO.setFactorConversion(stm.getString(3));
			totDTO.setMoneda(stm.getString(2));
		} catch (SQLException e) {
			LOCATION.error(e.getMessage());
		} catch (Exception e) {
			LOCATION.error(e.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error(e.getMessage());
			}
		}

		return totDTO;
	}

	public List<XMLDetailsDTO> fillDetail(String VBELN) {
		List<XMLDetailsDTO> listdetailDTO = new ArrayList<XMLDetailsDTO>();
		Connection con = DBConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(getFillDetailPedido);
			stm.setString(1, VBELN);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				XMLDetailsDTO detailDTOItem = new XMLDetailsDTO();
				stm = con.prepareStatement(getDataFillDetailPedido);
				stm.setString(1, VBELN);
				stm.setString(2, rs.getString("POSNR"));
				stm.setString(3, "ZPE0");
				ResultSet rs1 = stm.executeQuery();
				if (rs1.next()) {
					detailDTOItem.setCantidad(rs1.getString("KWMENG").split(
							"\\.")[0]);
					detailDTOItem.setDescripcion(rs1.getString("ARKTX")); 
					detailDTOItem.setCodigoProducto(Utils.zeroClean(rs1
							.getString("MATRN")));
					detailDTOItem.setsKU(Utils
							.zeroClean(rs1.getString("MATRN")));
					detailDTOItem.setUnidadMedida(rs1.getString("VRKME"));
					detailDTOItem.setPiezasEmpaque(rs1.getString("UMREN"));
					detailDTOItem.setAlfaNum1(rs1.getString("ALFANUM1"));
					detailDTOItem.setAlfaNum2(rs1.getString("LOW"));
					detailDTOItem.setAlfaNum3("");
					detailDTOItem.setAlfaNum4("");
					detailDTOItem.setAlfaNum5("");
					stm = con.prepareStatement(getDataAmountPedido);
					stm.setString(1, VBELN);
					stm.setString(2, rs.getString("POSNR"));
					ResultSet rs2 = stm.executeQuery();
					if (rs2.next()) {
						detailDTOItem.setPrecioBruto(rs2.getString("PRECIO"));
						detailDTOItem.setMontoBruto(rs2.getString("MONTO"));
						detailDTOItem.setDescuento(rs2.getString("DESCUENTO"));
						detailDTOItem.setMontoDescuento(rs2
								.getString("MONTODESCUENTO"));
						detailDTOItem.setPrecioNeto(rs2.getString("PRECIO"));
						detailDTOItem.setMontoNeto(rs2.getString("MONTO"));
						detailDTOItem.setIva(rs2.getString("IVA"));
						detailDTOItem.setMontoIVA(rs2.getString("MONTOIVA"));
					}
				}
				listdetailDTO.add(detailDTOItem);
			}
		} catch (SQLException e) {
			LOCATION.error(e.getLocalizedMessage());
		} catch (Exception e) {
			LOCATION.error(e.getLocalizedMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error(e.getLocalizedMessage());
			}
		}
		LOCATION.error(listdetailDTO.toString());
		return listdetailDTO;
	}


	
	public XMLCustomDTO fillCustom(XMLCreateDTO createDTO, String VBELN, String Werks) {
		XMLCustomDTO custDTO = null;
		custDTO = XMLCustomDTO.XMLCustomDTOEmpty(custDTO);
		Connection con = DBConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(getCustomAdua);
			stm.setString(1, VBELN);
			ResultSet rs = stm.executeQuery();
			custDTO.setNumerico4(VBELN);
			custDTO.setNumerico7(VBELN);
			if (rs.next()) {
				custDTO.setAlfanumerico2(Utils.isNull(rs.getString("NAME1")));
			}

			stm = con.prepareStatement(getCustomA);
			stm.setString(1, VBELN);
			rs = stm.executeQuery();
			if(rs.next()){
				custDTO.setAlfanumerico3(Utils.isNull(rs.getString("TELF1")));
				custDTO.setAlfanumerico6(Utils.isNull(rs.getString("TELF1")));
				custDTO.setAlfanumerico4(Utils.isNull(rs.getString("K1NAME1")) + " " + Utils.isNull(rs.getString("K1NAME2")));
				custDTO.setAlfanumerico5(Utils.isNull(rs.getString("NAMEV")) + " " + Utils.isNull(rs.getString("KVNAME1")));
				custDTO.setAlfanumerico8(Utils.isNull(rs.getString("BSTNK")));
				custDTO.setAlfanumerico9(Utils.isNull(rs.getString("KUNNR")));
				custDTO.setAlfanumerico11(Utils.isNull(rs.getString("TPBEZ")));
				custDTO.setAlfanumerico13(Utils.isNull(rs.getString("L1NAME1")));
				custDTO.setAlfanumerico17(Utils.isNull(rs.getString("SORTL")));
				custDTO.setAlfanumerico19(Utils.isNull(rs.getString("ADNAME1")));
			}
			stm = con.prepareStatement(exportacionValues);
			stm.setString(1, VBELN);
			rs = stm.executeQuery();
			if (rs.next()) {
				custDTO.setAlfanumerico15(Utils.isNull(rs.getString("SELLO")));
				custDTO.setAlfanumerico16(Utils.isNull(rs.getString("SELLO_IMPORTADOR")));
				custDTO.setAlfanumerico14(Utils.isNull(rs.getString("TALONEMBARQUE")));
				custDTO.setAlfanumerico10(Utils.isNull(rs.getString("NOCAJA")));
			}
	
			stm  = con.prepareStatement(getCustomB);
			stm.setString(1, VBELN);
			rs = stm.executeQuery();
			if(rs.next()){
				custDTO.setAlfanumerico28("NETO A PAGAR " + rs.getString("INCO1")+ " /  NET AMOUNT " + rs.getString("INCO1"));
				custDTO.setAlfanumerico30(rs.getString("VKORG") + " " + Werks + " " + rs.getString("VKKUNNR") + " " + rs.getString("LIFNR"));
				custDTO.setNumerico4(VBELN);
				custDTO.setNumerico6(rs.getString("LKKUNNR"));
				custDTO.setNumerico7(VBELN);
				custDTO.setNumerico8(rs.getString("LIFNR"));
				custDTO.setNumerico12(rs.getString("VTWEG"));
				custDTO.setNumerico20(rs.getString("KDKG5"));
				custDTO.setNumerico28(rs.getString("VKBUR"));
			}
			
			stm = con.prepareStatement(getCustomC);
			stm.setString(1, VBELN);
			rs = stm.executeQuery();
			if(rs.next()){
				custDTO.setNumerico1(Utils.isNull(rs.getString("LIBRAS")));
				custDTO.setNumerico2(Utils.isNull(rs.getString("GALONES")));
				custDTO.setNumerico3(Utils.isNull(rs.getString("BRGEW")));
			}
			
			stm = con.prepareStatement(getCustomRep);
			stm.setString(1, VBELN);
			rs = stm.executeQuery();
			if(rs.next()){
				custDTO.setAlfanumerico22(Utils.isNull(rs.getString("REPRESENTANTE_LEGAL")));
				custDTO.setAlfanumerico25(Utils.isNull(rs.getString("STRAS")));
			}
			
		} catch (SQLException e) {
			LOCATION.error(e.getMessage());
		} catch (Exception e) {
			LOCATION.error(e.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error(e.getMessage());
			}
		}
		return custDTO;
	}

	private static String getFolioFact(String vBeln, String werks) {
		Connection con = DBConnection.createConnection();
		String folio = "";
		try {
			CallableStatement cst = con.prepareCall(EXECFOLIO);
			cst.setString(1, vBeln);
			cst.setString(2, werks);
			cst.registerOutParameter(3, java.sql.Types.INTEGER);
			cst.execute();

			if (cst.getInt(3) == 1 || cst.getInt(3) == 5) {
				PreparedStatement stm = con.prepareStatement(GETFOLIO);
				stm.setString(1, vBeln);
				ResultSet rs = stm.executeQuery();
				if (rs.next()) {
					folio = rs.getString("RANGE_S");
				}
			}
		} catch (SQLException e) {
			LOCATION.error(e.getLocalizedMessage());
		} catch (Exception e) {
			LOCATION.error(e.getLocalizedMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error(e.getLocalizedMessage());
			}
		}
		LOCATION.error("FOLIO: " + folio);
		return folio;
	}

	public XMLFTPUserDTO userAccess() {
		XMLFTPUserDTO xmlFTP = new XMLFTPUserDTO();
		Connection con = DBConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(FTPACCESS);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				LOCATION.error("Coloque type");
				xmlFTP.setServer(rs.getString("IP"));
				xmlFTP.setPort(rs.getInt("PORT"));
				xmlFTP.setUser(rs.getString("USER"));
				xmlFTP.setPassword(rs.getString("PASSWORD"));
				xmlFTP.setType(rs.getString("TYPE"));
			}
		} catch (SQLException e) {
			LOCATION.error(e.getLocalizedMessage());
		} catch (Exception e) {
			LOCATION.error(e.getLocalizedMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error(e.getLocalizedMessage());
			}
		}
		return xmlFTP;
	}

	// sp_bcps_wm_contabilizar_entrega_salida_factura @ENTREGA, @FOLINT, @FOLEXT
	// , @UUID, @RETURN

	public static Integer insertValueXML(String vBeln, String UUID, String FolioExt,
			String FolioInt) {
		int retorno = 0;
		Connection con = DBConnection.createConnection();
		try {
			CallableStatement cst = con.prepareCall(XMLZCONT);
			cst.setString(1, vBeln);
			cst.setString(2, FolioExt);
			cst.setString(3, FolioInt);
			cst.setString(4, UUID);
			cst.registerOutParameter(5, java.sql.Types.INTEGER);
			cst.execute();
			retorno = cst.getInt(5);
		} catch (SQLException e) {
			LOCATION.error(e.getLocalizedMessage());
		} catch (Exception e) {
			LOCATION.error(e.getLocalizedMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error(e.getLocalizedMessage());
			}
		}
		return retorno;
	}

}
