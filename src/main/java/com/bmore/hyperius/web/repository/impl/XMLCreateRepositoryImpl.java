package com.bmore.hyperius.web.repository.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.repository.XMLCreateRepository;
import com.bmore.hyperius.web.utils.Utils;
import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLAddressDTO;
import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLAddressWrapperDTO;
import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLCreateDTO;
import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLCustomDTO;
import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLDetailsDTO;
import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLFTPUserDTO;
import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLRootDTO;
import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLTotalDTO;

public class XMLCreateRepositoryImpl implements XMLCreateRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

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

  private final static String EXECFOLIO = "exec sp_bcps_generate_facturas ?,?,?";
  private final static String GETFOLIO = "SELECT RANGE_S FROM TB_BCPS_ZFACT WITH(NOLOCK) WHERE VBELN = ? ";

  private static final String getCustomAdua = "SELECT NAME1 FROM VS_BCPS_ADUANA WHERE VBELN = ?";
  private static final String getCustomA = "SELECT TOP 1 TELF1, NAMEV, KVNAME1, K1NAME1, K1NAME2, BSTNK, KUNNR, TNDR_TRKID, TPBEZ, "
      + "L1NAME1, SORTL, SIGNI, EXTI2, TEXT3, ADNAME1 FROM VS_BCPS_XML_CUSTOM_A WITH(NOLOCK) WHERE VBELN =  ?";
  private static final String exportacionValues = "SELECT SELLO, NOCAJA, TALONEMBARQUE, OPERADOR, SELLO_IMPORTADOR FROM zContingencia WITH(NOLOCK) WHERE ENTREGA = ? AND IDPROC = 12";
  private static final String getCustomB = "SELECT INCO1, VKORG, VKBUR, VKKUNNR, VTWEG, LIFNR, LKKUNNR, KDKG5 FROM VS_BCPS_XML_CUSTOM_B WITH(NOLOCK) WHERE VBELN = ?";
  private static final String getCustomC = "SELECT LIBRAS, GALONES, BRGEW FROM VS_BCPS_XML_CUSTOM_C WITH(NOLOCK) WHERE VBELN = ? ";
  private static final String getCustomRep = "SELECT REPRESENTANTE_LEGAL, STRAS FROM VS_BCPS_XML_LEGAL WHERE VBELN  = ?";

  private static final Logger LOCATION = LoggerFactory.getLogger(XMLCreateRepositoryImpl.class);

  private XMLCustomDTO custDTO = new XMLCustomDTO();
  private XMLAddressDTO remisorExped = new XMLAddressDTO();
  private XMLRootDTO rootDTO = new XMLRootDTO();
  private XMLAddressWrapperDTO wrapperEmisor = new XMLAddressWrapperDTO();
  private XMLAddressWrapperDTO wrapperReceptor = new XMLAddressWrapperDTO();
  private XMLAddressDTO emisorExped = new XMLAddressDTO();
  private XMLAddressDTO remisorFiscal = new XMLAddressDTO();

  @SuppressWarnings("static-access")
  public XMLCreateDTO fillXML(String VBELN, String WERKS,
      XMLCreateDTO createDTO) {

    Object[] args = { VBELN };
    Object[] args1 = { VBELN, WERKS, java.sql.Types.VARCHAR };

    jdbcTemplate.query(getDataRootBOMA, args, new RowMapper<XMLCreateDTO>() {
      @Override
      public XMLCreateDTO mapRow(ResultSet rs, int i) throws SQLException {
        if (rs.next()) {
          rootDTO.setNumeroAprobacion(rs.getString("BKDES"));
          rootDTO.setFechaAprobacion(rs.getString("FIIDT"));
          rootDTO.setFolioInterno(getFolioFact(VBELN, WERKS));

          jdbcTemplate.query(getTermPagoDias, args1, new RowMapper<XMLCreateDTO>() {
            @Override
            public XMLCreateDTO mapRow(ResultSet rs2, int i) throws SQLException {
              rootDTO.setTermPagoDias(Utils.isNull(rs2.getString(3)));
              return null;
            }
          });

          Object[] args2 = { Integer.parseInt(rootDTO.getTermPagoDias()), VBELN };

          jdbcTemplate.query(getDataRootVBAK, args2, new RowMapper<XMLCreateDTO>() {
            @Override
            public XMLCreateDTO mapRow(ResultSet rs3, int i) throws SQLException {
              if (rs.next()) {
                rootDTO.setOrdenCompra(rs.getString("BSTNK"));
                rootDTO.setFechaOrdenCompra(rs.getString("AUDAT"));
                rootDTO.setFechaVencimiento(rs.getString("VDATU"));
                createDTO.setRoot(rootDTO);
                jdbcTemplate.query(getEmisorExpedicion, args, new RowMapper<XMLCreateDTO>() {
                  @Override
                  public XMLCreateDTO mapRow(ResultSet rs4, int i) throws SQLException {
                    if (rs4.next()) {
                      wrapperEmisor.setRfc(rs4.getString("PAVAL"));
                      emisorExped.setGln(rs4.getString("BAHNE") == null ? ""
                          : rs4.getString("BAHNE"));
                      emisorExped.setCalle(rs4.getString("STREET"));
                      emisorExped.setNoExterior(rs4.getString("HOUSE_NUM1"));
                      emisorExped
                          .setNoInterior(rs4.getString("HOUSE_NUM2") == null ? ""
                              : rs4.getString("HOUSE_NUM2"));
                      emisorExped.setColonia(rs4.getString("CITY2"));
                      emisorExped.setLocalidad(rs4.getString("CITY1"));
                      emisorExped.setMunicipio(rs4.getString("CITY1"));
                      emisorExped.setEstado(rs4.getString("BEZEI"));
                      emisorExped.setPais(rs4.getString("LANDX"));
                      emisorExped.setCodigoPostal(rs4.getString("POST_CODE1"));
                      wrapperEmisor.setDomicilioExpedicion(emisorExped);
                      createDTO.setEmisor(wrapperEmisor);
                      jdbcTemplate.query(getPedidoReceptor, args, new RowMapper<XMLCreateDTO>() {
                        @Override
                        public XMLCreateDTO mapRow(ResultSet rs5, int i) throws SQLException {
                          String clientePedido = rs5.getString("KUNNR");
                          Object[] args2 = { clientePedido };
                          jdbcTemplate.query(getReceptorFiscal, args2, new RowMapper<XMLCreateDTO>() {
                            @Override
                            public XMLCreateDTO mapRow(ResultSet rs5, int i) throws SQLException {
                              if (rs5.next()) {
                                wrapperReceptor.setRfc(rs5.getString("STCD1"));
                                wrapperReceptor.setNombre(rs5
                                    .getString("KNA1NAME"));
                                wrapperReceptor.setNumeroCliente(Utils
                                    .zeroClean(clientePedido));
                                wrapperReceptor.setContacto(rs5
                                    .getString("KNVKNAME"));
                                remisorFiscal.setCalle(rs5.getString("STREET"));
                                remisorFiscal.setNoExterior(rs5
                                    .getString("HOUSE_NUM1"));
                                remisorFiscal.setNoInterior(rs5
                                    .getString("HOUSE_NUM2") == null ? ""
                                        : rs5.getString("HOUSE_NUM2"));
                                remisorFiscal.setColonia(rs5.getString("CITY2"));
                                remisorFiscal.setLocalidad(rs5
                                    .getString("CITY1") == null ? ""
                                        : rs5
                                            .getString("CITY1"));
                                remisorFiscal.setMunicipio(rs5
                                    .getString("CITY1") == null ? ""
                                        : rs5
                                            .getString("CITY1"));
                                remisorFiscal.setEstado(rs5.getString("BEZEI"));
                                remisorFiscal.setPais(rs5.getString("LANDX"));
                                remisorFiscal.setCodigoPostal(rs5.getString("POST_CODE1"));
                                wrapperReceptor.setDomicilioFiscal(remisorFiscal);
                                jdbcTemplate.query(getReceptorExpedicion, args, new RowMapper<XMLCreateDTO>() {
                                  @Override
                                  public XMLCreateDTO mapRow(ResultSet rs6, int i) throws SQLException {
                                    if (rs.next()) {
                                      remisorExped.setCalle(rs6.getString("STREET"));
                                      remisorExped.setNoExterior(rs6.getString("HOUSE_NUM1"));
                                      remisorExped.setNoInterior(rs6.getString("HOUSE_NUM2") == null ? ""
                                          : rs6.getString("HOUSE_NUM2"));
                                      remisorExped.setColonia(rs6.getString("CITY2"));
                                      remisorExped.setLocalidad(rs6.getString("CITY1") == null ? ""
                                          : rs6.getString("CITY1"));
                                      remisorExped.setMunicipio(rs6.getString("CITY1") == null ? ""
                                          : rs6.getString("CITY1"));
                                      remisorExped.setEstado(rs6.getString("BEZEI"));
                                      remisorExped.setPais(rs6.getString("LANDX"));
                                      remisorExped.setCodigoPostal(rs6.getString("POST_CODE1"));
                                      wrapperReceptor.setDomicilioExpedicion(remisorExped);
                                      createDTO.setReceptor(wrapperReceptor);
                                    } else {
                                      remisorExped = new XMLAddressDTO().XMLAddressDTOEmpty(remisorExped);
                                    }
                                    return null;
                                  }
                                });
                              } else {
                                wrapperReceptor = new XMLAddressWrapperDTO().XMLAddressWrapperDTOEmpty(wrapperReceptor);
                              }
                              return null;
                            }
                          });
                          return null;
                        }
                      });
                    } else {
                      // empty?
                    }
                    return null;
                  }
                });
              } else {
                // createDTO = null;
              }
              return null;
            }
          });
        } else {
          wrapperEmisor = new XMLAddressWrapperDTO().XMLAddressWrapperDTOEmpty(wrapperEmisor);
        }
        return null;
      }
    });

    LOCATION.error(createDTO.toString());
    return createDTO;
  }

  @Override
  public XMLTotalDTO fillTotal(String VBELN) {
    XMLTotalDTO totDTO = new XMLTotalDTO();

    Object[] args = { VBELN, java.sql.Types.VARCHAR, java.sql.Types.VARCHAR };

    jdbcTemplate.query(getDataFactor, args, new RowMapper<XMLTotalDTO>() {
      @Override
      public XMLTotalDTO mapRow(ResultSet rs, int i) throws SQLException {
        totDTO.setFactorConversion(rs.getString(3));
        totDTO.setMoneda(rs.getString(2));
        return null;
      }
    });
    return totDTO;
  }

  @Override
  public List<XMLDetailsDTO> fillDetail(String VBELN) {
    List<XMLDetailsDTO> listdetailDTO = new ArrayList<XMLDetailsDTO>();
    Object[] args = { VBELN };

    jdbcTemplate.query(getFillDetailPedido, args, new RowMapper<XMLDetailsDTO>() {
      @Override
      public XMLDetailsDTO mapRow(ResultSet rs2, int i) throws SQLException {

        List<XMLDetailsDTO> listdetailDTO2 = new ArrayList<XMLDetailsDTO>();
        Object[] args2 = { VBELN, rs2.getString("POSNR"), "ZPE0" };
        listdetailDTO2 = jdbcTemplate.query(getDataFillDetailPedido, args2, new RowMapper<XMLDetailsDTO>() {
          @Override
          public XMLDetailsDTO mapRow(ResultSet rs, int i) throws SQLException {
            while (rs2.next()) {
              XMLDetailsDTO detailDTOItem = new XMLDetailsDTO();
              if (rs.next()) {
                detailDTOItem.setCantidad(rs.getString("KWMENG").split(
                    "\\.")[0]);
                detailDTOItem.setDescripcion(rs.getString("ARKTX"));
                detailDTOItem.setCodigoProducto(Utils.zeroClean(rs
                    .getString("MATRN")));
                detailDTOItem.setsKU(Utils
                    .zeroClean(rs.getString("MATRN")));
                detailDTOItem.setUnidadMedida(rs.getString("VRKME"));
                detailDTOItem.setPiezasEmpaque(rs.getString("UMREN"));
                detailDTOItem.setAlfaNum1(rs.getString("ALFANUM1"));
                detailDTOItem.setAlfaNum2(rs.getString("LOW"));
                detailDTOItem.setAlfaNum3("");
                detailDTOItem.setAlfaNum4("");
                detailDTOItem.setAlfaNum5("");

                Object[] args3 = { VBELN, rs2.getString("POSNR") };

                jdbcTemplate.query(getDataAmountPedido, args3, new RowMapper<XMLDetailsDTO>() {
                  @Override
                  public XMLDetailsDTO mapRow(ResultSet rs3, int i) throws SQLException {
                    if (rs3.next()) {
                      detailDTOItem.setPrecioBruto(rs3.getString("PRECIO"));
                      detailDTOItem.setMontoBruto(rs3.getString("MONTO"));
                      detailDTOItem.setDescuento(rs3.getString("DESCUENTO"));
                      detailDTOItem.setMontoDescuento(rs3
                          .getString("MONTODESCUENTO"));
                      detailDTOItem.setPrecioNeto(rs3.getString("PRECIO"));
                      detailDTOItem.setMontoNeto(rs3.getString("MONTO"));
                      detailDTOItem.setIva(rs3.getString("IVA"));
                      detailDTOItem.setMontoIVA(rs3.getString("MONTOIVA"));
                    }
                    return null;
                  }

                });

              }
              listdetailDTO.add(detailDTOItem);
            }
            return null;
          }

        });
        return null;
      }
    });
    LOCATION.error(listdetailDTO.toString());
    return listdetailDTO;
  }

  @Override
  public XMLCustomDTO fillCustom(XMLCreateDTO createDTO, String VBELN, String Werks) {

    custDTO = XMLCustomDTO.XMLCustomDTOEmpty(custDTO);
    Object[] args1 = { VBELN };

    jdbcTemplate.query(getCustomAdua, args1, new RowMapper<XMLCustomDTO>() {
      @Override
      public XMLCustomDTO mapRow(ResultSet rs, int i) throws SQLException {
        if (rs.next()) {
          custDTO.setAlfanumerico2(Utils.isNull(rs.getString("NAME1")));
        }
        return custDTO;
      }
    });
    custDTO.setNumerico4(VBELN);
    custDTO.setNumerico7(VBELN);

    jdbcTemplate.query(getCustomA, args1, new RowMapper<XMLCustomDTO>() {
      @Override
      public XMLCustomDTO mapRow(ResultSet rs, int i) throws SQLException {
        if (rs.next()) {
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
        return custDTO;
      }
    });

    jdbcTemplate.query(exportacionValues, args1, new RowMapper<XMLCustomDTO>() {
      @Override
      public XMLCustomDTO mapRow(ResultSet rs, int i) throws SQLException {
        if (rs.next()) {
          custDTO.setAlfanumerico15(Utils.isNull(rs.getString("SELLO")));
          custDTO.setAlfanumerico16(Utils.isNull(rs.getString("SELLO_IMPORTADOR")));
          custDTO.setAlfanumerico14(Utils.isNull(rs.getString("TALONEMBARQUE")));
          custDTO.setAlfanumerico10(Utils.isNull(rs.getString("NOCAJA")));
        }
        return custDTO;
      }
    });

    jdbcTemplate.query(getCustomB, args1, new RowMapper<XMLCustomDTO>() {
      @Override
      public XMLCustomDTO mapRow(ResultSet rs, int i) throws SQLException {
        if (rs.next()) {
          custDTO
              .setAlfanumerico28("NETO A PAGAR " + rs.getString("INCO1") + " /  NET AMOUNT " + rs.getString("INCO1"));
          custDTO.setAlfanumerico30(
              rs.getString("VKORG") + " " + Werks + " " + rs.getString("VKKUNNR") + " " + rs.getString("LIFNR"));
          custDTO.setNumerico4(VBELN);
          custDTO.setNumerico6(rs.getString("LKKUNNR"));
          custDTO.setNumerico7(VBELN);
          custDTO.setNumerico8(rs.getString("LIFNR"));
          custDTO.setNumerico12(rs.getString("VTWEG"));
          custDTO.setNumerico20(rs.getString("KDKG5"));
          custDTO.setNumerico28(rs.getString("VKBUR"));
        }
        return custDTO;
      }
    });

    jdbcTemplate.query(getCustomC, args1, new RowMapper<XMLCustomDTO>() {
      @Override
      public XMLCustomDTO mapRow(ResultSet rs, int i) throws SQLException {
        if (rs.next()) {
          custDTO.setNumerico1(Utils.isNull(rs.getString("LIBRAS")));
          custDTO.setNumerico2(Utils.isNull(rs.getString("GALONES")));
          custDTO.setNumerico3(Utils.isNull(rs.getString("BRGEW")));
        }
        return custDTO;
      }
    });

    jdbcTemplate.query(getCustomC, args1, new RowMapper<XMLCustomDTO>() {
      @Override
      public XMLCustomDTO mapRow(ResultSet rs, int i) throws SQLException {
        if (rs.next()) {
          custDTO.setAlfanumerico22(Utils.isNull(rs.getString("REPRESENTANTE_LEGAL")));
          custDTO.setAlfanumerico25(Utils.isNull(rs.getString("STRAS")));
        }
        return custDTO;
      }
    });

    return custDTO;
  }

  @Override
  public String getFolioFact(String vBeln, String werks) {
    String folio = "";
    Object[] args = { vBeln, werks, java.sql.Types.INTEGER };
    Object[] args2 = { vBeln };

    int numero = jdbcTemplate.queryForObject(EXECFOLIO, args, Integer.class);

    if (numero == 1 || numero == 5) {
      folio = (String) jdbcTemplate.queryForObject(GETFOLIO, args2, String.class);
    }

    LOCATION.error("FOLIO: " + folio);
    return folio;
  }

  @Override
  public XMLFTPUserDTO userAccess() {

    String FTPACCESS = "SELECT IP, PORT, [USER], [PASSWORD],TYPE FROM TB_BCPS_FTP_DATAMART WITH(NOLOCK)";
    XMLFTPUserDTO xmlFTP = new XMLFTPUserDTO();
    jdbcTemplate.query(FTPACCESS, new RowMapper<XMLFTPUserDTO>() {
      @Override
      public XMLFTPUserDTO mapRow(ResultSet rs, int i) throws SQLException {
        LOCATION.error("Coloque type");
        xmlFTP.setServer(rs.getString("IP"));
        xmlFTP.setPort(rs.getInt("PORT"));
        xmlFTP.setUser(rs.getString("USER"));
        xmlFTP.setPassword(rs.getString("PASSWORD"));
        xmlFTP.setType(rs.getString("TYPE"));
        return xmlFTP;
      }

    });

    return xmlFTP;
  }

  // sp_bcps_wm_contabilizar_entrega_salida_factura @ENTREGA, @FOLINT, @FOLEXT
  // , @UUID, @RETURN

  @Override
  public Integer insertValueXML(String vBeln, String UUID, String FolioExt,
      String FolioInt) {

    String XMLZCONT = "exec sp_bcps_wm_contabilizar_entrega_salida_factura ?,?,?,?,?";
    Object[] args = { vBeln, FolioExt, FolioInt, UUID, java.sql.Types.INTEGER };
    int retorno = 0;
    retorno = jdbcTemplate.update(XMLZCONT, args);
    return retorno;
  }

}
