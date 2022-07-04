package com.bmore.prueba.web.utils.export.xmlgeneration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bmore.prueba.web.repository.old.XMLCreateRepository;

public class XMLCreateBO {

	public static final String BCPS = "BCPS";
	public static final String XML = ".xml";
	public static final String LOCALROUTE = "D:\\FACTURAS\\";
	public static final String LOCALROUTEOUT = "D:\\FACTURAS\\OUT\\";
	public static final String LOCALROUTEERROR = "D:\\FACTURAS\\ERROR\\";
	public static final String PUTREMOTEFILE = "/FACTURAS/";
	public static final String GETREMOTEFILE = "/FACTURAS/out/";
	public static final String GETREMOTEFILEERROR = "/FACTURAS/error/";

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	public int generateXML(String entrega, String werks) {

		XMLCreateDTO createDTO = null;
		createDTO = XMLCreateDTO.XMLCreateDTOEmpty(createDTO);

		List<XMLDetailsDTO> listDtos = new ArrayList<XMLDetailsDTO>();
		XMLCreateRepository creatDAO = new XMLCreateRepository();

		createDTO = creatDAO.fillXML(entrega, werks, createDTO);

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("ns1:Documento");

			doc.appendChild(rootElement);

			rootElement.setAttributeNode(addAtr("xmlns:ns1",
					"http://gmodelo.com/SGN/RecibirFactura", doc));
			rootElement.setAttributeNode(addAtr("version", "1.0", doc));
			rootElement.setAttributeNode(addAtr("fecha", "21.10.2006", doc));

			rootElement.appendChild(appendRoot(doc, "NumeroAprobacion",
					createDTO.getRoot().getNumeroAprobacion())); // NUEVAS
			// TABLAS
			rootElement.appendChild(appendRoot(doc, "FechaAprobacion",
					createDTO.getRoot().getFechaAprobacion())); // NUEVAS
			// TABLAS
			rootElement.appendChild(appendRoot(doc, "Tipo", "33"));
			rootElement.appendChild(appendRoot(doc, "Serie"));
			rootElement.appendChild(appendRoot(doc, "Folio"));
			rootElement.appendChild(appendRoot(doc, "FolioInterno", createDTO
					.getRoot().getFolioInterno())); // GENERAR FOLIO PARA
			// FACTURAS RANGO
			// 0800000000-0899999999
			rootElement.appendChild(appendRoot(doc, "FechaEmision",
					new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
							.format(new Date())));
			rootElement.appendChild(appendRoot(doc, "FormaPago",
					"PAGO EN UNA SOLA EXHIBICION"));
			rootElement.appendChild(appendRoot(doc, "TermPagoDias", createDTO
					.getRoot().getTermPagoDias())); // NUEVAS TABLAS
			rootElement.appendChild(appendRoot(doc, "OrdenCompra", createDTO
					.getRoot().getOrdenCompra())); // NUEVAS TABLAS
			rootElement.appendChild(appendRoot(doc, "FechaOrdenCompra",
					createDTO.getRoot().getFechaOrdenCompra())); // NUEVAS
			// TABLAS
			rootElement.appendChild(appendRoot(doc, "Contrarecibo")); // VACIO
			rootElement.appendChild(appendRoot(doc, "FechaContrarecibo")); // VACIO
			rootElement.appendChild(appendRoot(doc, "FechaVencimiento",
					createDTO.getRoot().getFechaVencimiento())); // NUEVAS
			// TABLAS

			Element emisor = doc.createElement("Emisor");
			emisor.appendChild(appendRoot(doc, "RFC", createDTO.getEmisor()
					.getRfc())); // TABLAS NUEVAS
			emisor.appendChild(appendRoot(doc, "Nombre", "NO APLICA"));
			emisor.appendChild(appendRoot(doc, "NumeroProveedor"));
			emisor.appendChild(appendRoot(doc, "Sucursal", "MATRIZ"));

			Element domFis = doc.createElement("DomicilioFiscal");
			domFis.appendChild(appendRoot(doc, "GLN", "NO APLICA")); // CONSTANTE
			// NO
			// APLICA
			domFis.appendChild(appendRoot(doc, "Calle", "NO APLICA")); // CONSTANTE
			// NO
			// APLICA
			domFis.appendChild(appendRoot(doc, "noExterior", "NO APLICA")); // CONSTANTE
			// NO
			// APLICA
			domFis.appendChild(appendRoot(doc, "noInterior", "NO APLICA")); // CONSTANTE
			// NO
			// APLICA
			domFis.appendChild(appendRoot(doc, "Colonia", "NO APLICA")); // CONSTANTE
			// NO
			// APLICA
			domFis.appendChild(appendRoot(doc, "Localidad", "NO APLICA")); // CONSTANTE
			// NO
			// APLICA
			domFis.appendChild(appendRoot(doc, "Municipio", "NO APLICA")); // CONSTANTE
			// NO
			// APLICA
			domFis.appendChild(appendRoot(doc, "Estado", "NO APLICA")); // CONSTANTE
			// NO
			// APLICA
			domFis.appendChild(appendRoot(doc, "Pais", "NO APLICA")); // CONSTANTE
			// NO
			// APLICA
			domFis.appendChild(appendRoot(doc, "CodigoPostal", "00000")); // CONSTANTE
			// NO
			// APLICA
			emisor.appendChild(domFis);

			Element domExp = doc.createElement("DomicilioExpedicion");
			domExp.appendChild(appendRoot(doc, "GLN", createDTO.getEmisor()
					.getDomicilioExpedicion().getGln())); // KNA1 - BAHNE
			domExp.appendChild(appendRoot(doc, "Calle", createDTO.getEmisor()
					.getDomicilioExpedicion().getCalle())); // ADRC-STREET
			domExp.appendChild(appendRoot(doc, "noExterior", createDTO
					.getEmisor().getDomicilioExpedicion().getNoExterior())); // ADRC-HOUSE_NUM1
			domExp.appendChild(appendRoot(doc, "noInterior", createDTO
					.getEmisor().getDomicilioExpedicion().getNoInterior())); // ADRC-HOUSE_NUM2
			domExp.appendChild(appendRoot(doc, "Colonia", createDTO.getEmisor()
					.getDomicilioExpedicion().getColonia())); // ADRC-CITY2
			domExp.appendChild(appendRoot(doc, "Localidad", createDTO
					.getEmisor().getDomicilioExpedicion().getLocalidad())); // ADRC-CITY1
			domExp.appendChild(appendRoot(doc, "Municipio", createDTO
					.getEmisor().getDomicilioExpedicion().getMunicipio())); // ADRC-CITY1
			domExp.appendChild(appendRoot(doc, "Estado", createDTO.getEmisor()
					.getDomicilioExpedicion().getEstado())); // T005U-BEZEI
			domExp.appendChild(appendRoot(doc, "Pais", createDTO.getEmisor()
					.getDomicilioExpedicion().getPais())); // T005T-LANDX
			domExp.appendChild(appendRoot(doc, "CodigoPostal", createDTO
					.getEmisor().getDomicilioExpedicion().getCodigoPostal())); // ADRC-POST_CODE1
			emisor.appendChild(domExp);
			rootElement.appendChild(emisor);

			LOCATION.error("EMISOR");

			Element receptor = doc.createElement("Receptor");
			receptor.appendChild(appendRoot(doc, "RFC", createDTO.getReceptor()
					.getRfc())); // KNA1-STCD1
			receptor.appendChild(appendRoot(doc, "RIEPS")); // VACIO
			receptor.appendChild(appendRoot(doc, "Nombre", createDTO
					.getReceptor().getNombre())); // Concatenar KNA1-NAME1,
			// KNA1-NAME2, KNA1-NAME3
			receptor.appendChild(appendRoot(doc, "NumeroCliente", createDTO
					.getReceptor().getNumeroCliente())); // KNA1-KUNNR
			receptor.appendChild(appendRoot(doc, "CodigoSucursal")); // VACIO
			receptor.appendChild(appendRoot(doc, "Sucursal")); // VACIO
			receptor.appendChild(appendRoot(doc, "Contacto", createDTO
					.getReceptor().getContacto())); // Concatenar KNVK- NAMEV,
			// KNVK-NAME1 seg�n el
			// cliente (KUNNR)
			// identificado

			domFis = doc.createElement("DomicilioFiscal");
			domFis.appendChild(appendRoot(doc, "GLN")); // VACIO
			domFis.appendChild(appendRoot(doc, "Calle", createDTO.getReceptor()
					.getDomicilioFiscal().getCalle())); // ADRC-STREET
			domFis.appendChild(appendRoot(doc, "noExterior", createDTO
					.getReceptor().getDomicilioFiscal().getNoExterior())); // ADRC-HOUSE_NUM1
			domFis.appendChild(appendRoot(doc, "noInterior", createDTO
					.getReceptor().getDomicilioFiscal().getNoInterior())); // ADRC-HOUSE_NUM2
			domFis.appendChild(appendRoot(doc, "Colonia", createDTO
					.getReceptor().getDomicilioFiscal().getColonia())); // ADRC-CITY2
			domFis.appendChild(appendRoot(doc, "Localidad", createDTO
					.getReceptor().getDomicilioFiscal().getLocalidad())); // ADRC-CITY1
			domFis.appendChild(appendRoot(doc, "Municipio", createDTO
					.getReceptor().getDomicilioFiscal().getMunicipio())); // ADRC-CITY1
			domFis.appendChild(appendRoot(doc, "Estado", createDTO
					.getReceptor().getDomicilioFiscal().getEstado())); // T005U-BEZEI
			domFis.appendChild(appendRoot(doc, "Pais", createDTO.getReceptor()
					.getDomicilioFiscal().getPais())); // T005T-LANDX
			domFis.appendChild(appendRoot(doc, "CodigoPostal", createDTO
					.getReceptor().getDomicilioFiscal().getColonia())); // ADRC-POST_CODE1
			receptor.appendChild(domFis);

			domExp = doc.createElement("DomicilioRecepcion");
			domExp.appendChild(appendRoot(doc, "GLN")); // VACIO
			domExp.appendChild(appendRoot(doc, "Calle", createDTO.getReceptor()
					.getDomicilioExpedicion().getCalle())); // ADRC-STREET
			domExp.appendChild(appendRoot(doc, "noExterior", createDTO
					.getReceptor().getDomicilioExpedicion().getNoExterior())); // ADRC-HOUSE_NUM1
			domExp.appendChild(appendRoot(doc, "noInterior", createDTO
					.getReceptor().getDomicilioExpedicion().getNoInterior())); // ADRC-HOUSE_NUM2
			domExp.appendChild(appendRoot(doc, "Colonia", createDTO
					.getReceptor().getDomicilioExpedicion().getColonia())); // ADRC-CITY2
			domExp.appendChild(appendRoot(doc, "Localidad", createDTO
					.getReceptor().getDomicilioExpedicion().getLocalidad())); // ADRC-CITY1
			domExp.appendChild(appendRoot(doc, "Municipio", createDTO
					.getReceptor().getDomicilioExpedicion().getMunicipio())); // ADRC-CITY1
			domExp.appendChild(appendRoot(doc, "Estado", createDTO
					.getReceptor().getDomicilioExpedicion().getEstado())); // T005U-BEZEI
			domExp.appendChild(appendRoot(doc, "Pais", createDTO.getReceptor()
					.getDomicilioExpedicion().getPais())); // T005T-LANDX
			domExp.appendChild(appendRoot(doc, "CodigoPostal", createDTO
					.getReceptor().getDomicilioExpedicion().getCodigoPostal())); // ADRC-POST_CODE1
			receptor.appendChild(domExp);
			rootElement.appendChild(receptor);

			LOCATION.error("RECEPTOR");

			Element details = doc.createElement("Detalles");
			BigDecimal totalFactura = new BigDecimal("0");
			BigDecimal totalIVA = new BigDecimal("0");
			BigDecimal totalMontoIva = new BigDecimal("0");

			listDtos = creatDAO.fillDetail(entrega);
			for (XMLDetailsDTO xmlDetailsDTO : listDtos) {
				Element line = doc.createElement("Linea");
				createDTO.setDetalles(xmlDetailsDTO);
				try {
					BigDecimal montoIva = new BigDecimal(xmlDetailsDTO
							.getMontoIVA());
					BigDecimal montoNeto = new BigDecimal(xmlDetailsDTO
							.getMontoNeto());

					BigDecimal montoDescuento = new BigDecimal(xmlDetailsDTO
							.getMontoDescuento());

					BigDecimal cantidad = new BigDecimal(xmlDetailsDTO
							.getCantidad());

					BigDecimal precioNeto = montoNeto.divide(cantidad);

					BigDecimal montoTotal = new BigDecimal("0");
					montoTotal = montoTotal.add(montoIva).add(montoDescuento)
							.add(montoNeto);
					totalFactura = totalFactura.add(montoTotal);
					totalMontoIva = totalMontoIva.add(montoIva);
					totalIVA = totalIVA.add(new BigDecimal(xmlDetailsDTO
							.getIva()));
					xmlDetailsDTO.setMontoTotalItem(montoTotal.toString());

					line.appendChild(appendRoot(doc, "Cantidad", xmlDetailsDTO
							.getCantidad())); // VBAP-KWMENG
					line.appendChild(appendRoot(doc, "Descripcion",
							xmlDetailsDTO.getDescripcion()));// VBAP-ARKTX
					line.appendChild(appendRoot(doc, "CodigoProducto",
							xmlDetailsDTO.getCodigoProducto()));// VBAP-MATNR
					line.appendChild(appendRoot(doc, "EAN13"));// VACIO
					line.appendChild(appendRoot(doc, "SKU", xmlDetailsDTO
							.getsKU()));// VBAP-MATNR
					line.appendChild(appendRoot(doc, "UnidadMedida",
							xmlDetailsDTO.getUnidadMedida()));// VBAP-VRKME
					line.appendChild(appendRoot(doc, "PiezasEmpaque",
							xmlDetailsDTO.getPiezasEmpaque()));// MARM-UMREN
					line.appendChild(appendRoot(doc, "OrdenCompra"));// VACIO
					line.appendChild(appendRoot(doc, "NombreAduana"));// VACIO
					line.appendChild(appendRoot(doc, "NumeroPedimiento"));// VACIO
					line.appendChild(appendRoot(doc, "FechaPedimiento"));// VACIO
					line.appendChild(appendRoot(doc, "PrecioBruto", precioNeto
							.toString()));
					line.appendChild(appendRoot(doc, "MontoBruto",
							xmlDetailsDTO.getMontoBruto()));
					line.appendChild(appendRoot(doc, "Descuento", xmlDetailsDTO
							.getDescuento()));
					line.appendChild(appendRoot(doc, "MontoDescuento",
							xmlDetailsDTO.getMontoDescuento()));
					line.appendChild(appendRoot(doc, "PrecioNeto", precioNeto
							.toString()));
					line.appendChild(appendRoot(doc, "MontoNeto", xmlDetailsDTO
							.getMontoNeto()));
					line.appendChild(appendRoot(doc, "IEPS", "00.000"));// CONSTANTE
					// 00.000
					line.appendChild(appendRoot(doc, "MontoIEPS",
							"00000000000.00"));// CONSTANTE
					// 00000000000.00
					line.appendChild(appendRoot(doc, "IVA", xmlDetailsDTO
							.getIva()));
					line.appendChild(appendRoot(doc, "MontoIVA", xmlDetailsDTO
							.getMontoIVA()));
					line.appendChild(appendRoot(doc, "MontoTotalItem",
							xmlDetailsDTO.getMontoTotalItem()));
					line.appendChild(appendRoot(doc, "Alfanum1", xmlDetailsDTO
							.getAlfaNum1()));
					line.appendChild(appendRoot(doc, "Alfanum2", xmlDetailsDTO
							.getAlfaNum2()));
					line.appendChild(appendRoot(doc, "Alfanum3", xmlDetailsDTO
							.getAlfaNum3()));
					line.appendChild(appendRoot(doc, "Alfanum4", xmlDetailsDTO
							.getAlfaNum4()));
					line.appendChild(appendRoot(doc, "Alfanum5", xmlDetailsDTO
							.getAlfaNum5()));
					line.appendChild(appendRoot(doc, "Num1"));// VACIO
					line.appendChild(appendRoot(doc, "Num2"));// VACIO
					line.appendChild(appendRoot(doc, "Num3"));// VACIO
					line.appendChild(appendRoot(doc, "Num4"));// VACIO
					line.appendChild(appendRoot(doc, "Num5"));// VACIO
					line.appendChild(appendRoot(doc, "AgenteAduanal"));// VACIO
					line.appendChild(appendRoot(doc, "TipoDePedimento"));// VACIO
					line.appendChild(appendRoot(doc, "FechaReciboLaredo"));// VACIO
					line.appendChild(appendRoot(doc, "FechaBillOfLading"));// VACIO
					details.appendChild(line);
				} catch (Exception e) {
					LOCATION.error("BigDecimal Convert Error");
				}
			}

			LOCATION.error("DETALLES");
			rootElement.appendChild(details);

			createDTO.setTotales(creatDAO.fillTotal(entrega));

			Element total = doc.createElement("Totales");
			BigDecimal totalPagar = new BigDecimal("0");

			totalPagar = totalPagar.add(totalFactura).add(totalMontoIva).add(
					totalIVA);
			LOCATION.error("TOTALPAGAR: " + totalPagar.toString());
			total.appendChild(appendRoot(doc, "Moneda", createDTO.getTotales()
					.getMoneda())); // Revisar la moneda
			total.appendChild(appendRoot(doc, "FactorConversion", createDTO
					.getTotales().getFactorConversion()));
			total.appendChild(appendRoot(doc, "TotalFactura", totalFactura
					.toString()));
			total.appendChild(appendRoot(doc, "Descuento1"));// VACIO
			total.appendChild(appendRoot(doc, "MontoDescuento1", "0.00"));
			total.appendChild(appendRoot(doc, "Descuento2"));// VACIO
			total.appendChild(appendRoot(doc, "MontoDescuento2"));// VACIO
			total.appendChild(appendRoot(doc, "MontoTotalDescuentos", "0.00"));
			total.appendChild(appendRoot(doc, "Recargo"));// VACIO
			total.appendChild(appendRoot(doc, "MontoRecargo"));// VACIO
			total.appendChild(appendRoot(doc, "Subtotal", totalFactura
					.toString()));
			total.appendChild(appendRoot(doc, "TipoImpTras1"));// VACIO
			total.appendChild(appendRoot(doc, "IEPS"));// VACIO
			total.appendChild(appendRoot(doc, "MontoIEPS"));// VACIO
			total.appendChild(appendRoot(doc, "TipoImpTras2"));// VACIO
			total.appendChild(appendRoot(doc, "IVA", totalIVA.toString()));
			total.appendChild(appendRoot(doc, "MontoIVA", totalMontoIva
					.toString()));
			total.appendChild(appendRoot(doc, "TotalImpTras", "0.00"));
			total.appendChild(appendRoot(doc, "TipoImpuestoRet"));// VACIO
			total.appendChild(appendRoot(doc, "ImpuestoRet"));// VACIO
			total.appendChild(appendRoot(doc, "MontoImpRet"));// VACIO
			total.appendChild(appendRoot(doc, "TotalImpRet"));// VACIO
			total.appendChild(appendRoot(doc, "TotalPagar", totalPagar
					.toString()));
			total.appendChild(appendRoot(doc, "TotalLetra",
					totalPagar == null ? "" : NumberToLetterConverter
							.convertNumberToLetter(totalPagar.toString())));
			total.appendChild(appendRoot(doc, "TipoBulto"));// VACIO
			total.appendChild(appendRoot(doc, "EntregaMercancia"));// VACIO
			total.appendChild(appendRoot(doc, "CantidadBultos"));// VACIO
			total.appendChild(appendRoot(doc, "CantidadPedidos"));// VACIO
			total.appendChild(appendRoot(doc, "EmpaqueCajas"));// VACIO
			total.appendChild(appendRoot(doc, "EmpaqueTarimas"));// VACIO
			total.appendChild(appendRoot(doc, "CantidadCajasTarimas"));// VACIO
			rootElement.appendChild(total);

			LOCATION.error("CUSTOM");
			try {

				createDTO.setCustom(creatDAO.fillCustom(createDTO, entrega,
						werks));
				Element custom = doc.createElement("CamposPersonalizados");
				custom.appendChild(appendRoot(doc, "Alfanumerico1", werks));
				custom.appendChild(appendRoot(doc, "Alfanumerico2", createDTO
						.getCustom().getAlfanumerico2()));
				custom.appendChild(appendRoot(doc, "Alfanumerico3", createDTO
						.getCustom().getAlfanumerico3()));
				custom.appendChild(appendRoot(doc, "Alfanumerico4", createDTO
						.getCustom().getAlfanumerico4()));
				custom.appendChild(appendRoot(doc, "Alfanumerico5", createDTO
						.getCustom().getAlfanumerico5()));
				custom.appendChild(appendRoot(doc, "Alfanumerico6", createDTO
						.getCustom().getAlfanumerico6()));
				custom.appendChild(appendRoot(doc, "Alfanumerico7",
						totalPagar == null ? "" : NumberToLetterConverter
								.convertNumberToLetterInglich(totalPagar
										.toString())));
				custom.appendChild(appendRoot(doc, "Alfanumerico8", createDTO
						.getCustom().getAlfanumerico8()));
				custom.appendChild(appendRoot(doc, "Alfanumerico9", createDTO
						.getCustom().getAlfanumerico9()));
				custom.appendChild(appendRoot(doc, "Alfanumerico10", createDTO
						.getCustom().getAlfanumerico10()));
				custom.appendChild(appendRoot(doc, "Alfanumerico11", createDTO
						.getCustom().getAlfanumerico11()));
				custom.appendChild(appendRoot(doc, "Alfanumerico12", createDTO
						.getCustom().getAlfanumerico12()));
				custom.appendChild(appendRoot(doc, "Alfanumerico13", createDTO
						.getCustom().getAlfanumerico13()));
				custom.appendChild(appendRoot(doc, "Alfanumerico14", createDTO
						.getCustom().getAlfanumerico14()));
				custom.appendChild(appendRoot(doc, "Alfanumerico15", createDTO
						.getCustom().getAlfanumerico15()));
				custom.appendChild(appendRoot(doc, "Alfanumerico16", createDTO
						.getCustom().getAlfanumerico16()));
				custom.appendChild(appendRoot(doc, "Alfanumerico17", createDTO
						.getCustom().getAlfanumerico17()));
				custom.appendChild(appendRoot(doc, "Alfanumerico18", createDTO
						.getCustom().getAlfanumerico18()));
				custom.appendChild(appendRoot(doc, "Alfanumerico19", createDTO
						.getCustom().getAlfanumerico19()));
				custom.appendChild(appendRoot(doc, "Alfanumerico20"));
				custom.appendChild(appendRoot(doc, "Alfanumerico21", createDTO
						.getCustom().getAlfanumerico21()));
				custom.appendChild(appendRoot(doc, "Alfanumerico22", createDTO
						.getCustom().getAlfanumerico22()));
				custom.appendChild(appendRoot(doc, "Alfanumerico23", createDTO
						.getCustom().getAlfanumerico23()));
				custom.appendChild(appendRoot(doc, "Alfanumerico24", createDTO
						.getCustom().getAlfanumerico24()));
				custom.appendChild(appendRoot(doc, "Alfanumerico25", createDTO
						.getCustom().getAlfanumerico25()));
				custom.appendChild(appendRoot(doc, "Alfanumerico26", createDTO
						.getCustom().getAlfanumerico26()));
				custom.appendChild(appendRoot(doc, "Alfanumerico27",
						"TERMS OF PAYMENT: "
								+ createDTO.getRoot().getTermPagoDias()
								+ " DAYS CREDIT"));
				custom.appendChild(appendRoot(doc, "Alfanumerico28", createDTO
						.getCustom().getAlfanumerico28()));
				custom.appendChild(appendRoot(doc, "Alfanumerico29", createDTO
						.getCustom().getAlfanumerico29()));
				custom.appendChild(appendRoot(doc, "Alfanumerico30", createDTO
						.getCustom().getAlfanumerico30()));
				custom.appendChild(appendRoot(doc, "Alfanumerico31", "NA"));// CONSTANTE
				// NA
				custom.appendChild(appendRoot(doc, "Alfanumerico32", "NA"));// CONSTANTE
				// NA
				custom.appendChild(appendRoot(doc, "Alfanumerico33", ""));
				custom.appendChild(appendRoot(doc, "Alfanumerico34", ""));
				custom.appendChild(appendRoot(doc, "Alfanumerico35", ""));
				custom.appendChild(appendRoot(doc, "Alfanumerico36", ""));
				custom.appendChild(appendRoot(doc, "Alfanumerico37", ""));
				custom.appendChild(appendRoot(doc, "Alfanumerico38", ""));
				custom.appendChild(appendRoot(doc, "Alfanumerico39", createDTO
						.getCustom().getAlfanumerico39()));
				custom.appendChild(appendRoot(doc, "Alfanumerico40", "CDFI"));
				custom.appendChild(appendRoot(doc, "Numerico1", createDTO
						.getCustom().getNumerico1()));
				custom.appendChild(appendRoot(doc, "Numerico2", createDTO
						.getCustom().getNumerico2()));
				custom.appendChild(appendRoot(doc, "Numerico3", createDTO
						.getCustom().getNumerico3()));
				custom.appendChild(appendRoot(doc, "Numerico4", createDTO
						.getCustom().getNumerico4()));
				custom.appendChild(appendRoot(doc, "Numerico5", createDTO
						.getCustom().getNumerico5()));
				custom.appendChild(appendRoot(doc, "Numerico6", createDTO
						.getCustom().getNumerico6()));
				custom.appendChild(appendRoot(doc, "Numerico7", createDTO
						.getCustom().getNumerico7()));
				custom.appendChild(appendRoot(doc, "Numerico8", createDTO
						.getCustom().getNumerico8()));
				custom.appendChild(appendRoot(doc, "Numerico9", createDTO
						.getCustom().getNumerico9()));
				custom.appendChild(appendRoot(doc, "Numerico10", createDTO
						.getCustom().getNumerico10()));
				custom.appendChild(appendRoot(doc, "Numerico11", "1"));
				custom.appendChild(appendRoot(doc, "Numerico12", "04"));
				custom.appendChild(appendRoot(doc, "Numerico13", createDTO
						.getCustom().getNumerico13()));
				custom.appendChild(appendRoot(doc, "Numerico14", createDTO
						.getCustom().getNumerico14()));
				custom.appendChild(appendRoot(doc, "Numerico15", "8"));
				custom.appendChild(appendRoot(doc, "Numerico16", ""));
				custom.appendChild(appendRoot(doc, "Numerico17", ""));
				custom.appendChild(appendRoot(doc, "Numerico18", ""));
				custom.appendChild(appendRoot(doc, "Numerico19", ""));
				custom.appendChild(appendRoot(doc, "Numerico20", createDTO
						.getCustom().getNumerico20()));
				custom.appendChild(appendRoot(doc, "Numerico21", ""));
				custom.appendChild(appendRoot(doc, "Numerico22", ""));
				custom.appendChild(appendRoot(doc, "Numerico23", ""));
				custom.appendChild(appendRoot(doc, "Numerico24", ""));
				custom.appendChild(appendRoot(doc, "Numerico25", ""));
				custom.appendChild(appendRoot(doc, "Numerico26", ""));
				custom.appendChild(appendRoot(doc, "Numerico27", ""));
				custom.appendChild(appendRoot(doc, "Numerico28", createDTO
						.getCustom().getNumerico28()));
				custom.appendChild(appendRoot(doc, "Numerico29", "0000"));// CONSTANTE
				// NA
				custom.appendChild(appendRoot(doc, "Numerico30", ""));
				custom.appendChild(appendRoot(doc, "BCPS", "BCPS"));
				rootElement.appendChild(custom);
			} catch (Exception e) {
				LOCATION.error("Custom GetValues Error...");
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			// NOMBRE DE DOCUMENTO ENTREGA + CENTRO + CONSECUTIVO DE EL XML
			StreamResult result = new StreamResult(new File(LOCALROUTE + BCPS
					+ entrega + XML));
			transformer.transform(source, result);
			uploadFileto(BCPS + entrega + XML);
			 getValuesFromXML(BCPS + entrega + XML, entrega,
			 createDTO.getRoot().getFolioInterno());

		} catch (ParserConfigurationException pce) {
			LOCATION.error(pce.getLocalizedMessage());
		} catch (TransformerException tfe) {
			LOCATION.error(tfe.getLocalizedMessage());
		}

		return 1;
	}

	public static Element appendRoot(Document doc, String nameElement) {
		Element genericElement = doc.createElement(nameElement);
		return genericElement;
	}

	public static Element appendRoot(Document doc, String nameElement,
			String vtext) {
		Element genericElement = doc.createElement(nameElement);
		genericElement.appendChild(doc.createTextNode(vtext));
		return genericElement;
	}

	public static Attr addAtr(String attr, String value, Document doc) {
		Attr genericAttr = doc.createAttribute(attr);
		genericAttr.setValue(value);
		return genericAttr;
	}

	public static XMLCreateDTO xmlValues() {
		XMLCreateDTO createDTO = new XMLCreateDTO();

		return createDTO;
	}

	@SuppressWarnings("static-access")
	public static int getValuesFromXML(String fileName, String vBeln,
			String folioExt) {
		int nvalue = 0;
		String getResponse = LOCALROUTEOUT + fileName;
		XMLCreateRepository xmlDAO = new XMLCreateRepository();
		try {
			File fXmlFile = new File(getResponse);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :"
					+ doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("cfdi:Comprobante");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					Element eElement2 = (Element) eElement
							.getElementsByTagName("cfdi:Complemento").item(0);
					Element eElem3 = (Element) eElement2.getElementsByTagName(
							"tfd:TimbreFiscalDigital").item(0);
					xmlDAO.insertValueXML(vBeln, eElem3.getAttribute("UUID"),
							folioExt, eElement.getAttribute("serie")
									+ eElement.getAttribute("folio"));
				}
			}
			nvalue = 1;
		} catch (Exception e) {
			nvalue = 0;
		}
		return nvalue;
	}

	public static int uploadFiletoFTP(String fileName, XMLFTPUserDTO ftpAccess) {

		int nvalue = 0;
		FTPClient ftpClient = new FTPClient();

		try {

			ftpClient.connect(ftpAccess.getServer(), ftpAccess.getPort());
			ftpClient.login(ftpAccess.getUser(), ftpAccess.getPassword());
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			File firstLocalFile = new File(LOCALROUTE + fileName);

			String firstRemoteFile = PUTREMOTEFILE + fileName;
			String lastRemoteFile = GETREMOTEFILE + fileName;
			InputStream inputStream = new FileInputStream(firstLocalFile);

			System.out.println("Start uploading first file");
			System.out.println(ftpClient.getReplyCode());
			boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
			inputStream.close();
			if (done) {
				System.out.println("The first file is uploaded successfully.");
				nvalue = 1;
				String getResponse = LOCALROUTEOUT + fileName;

				OutputStream outputStream = new FileOutputStream(getResponse);
				boolean redone = false;

				while (redone == false) {
					System.out.println("Actual: " + redone);
					redone = ftpClient.retrieveFile(lastRemoteFile,
							outputStream);
				}
			}

		} catch (IOException ex) {
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
			nvalue = 2;
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return nvalue;
	}

	public static int uploadFiletoFTPS(String fileName, XMLFTPUserDTO ftpAccess) {

		int nvalue = 0;
		FTPSClient ftpClient = new FTPSClient();

		try {

			ftpClient.connect(ftpAccess.getServer(), ftpAccess.getPort());
			ftpClient.login(ftpAccess.getUser(), ftpAccess.getPassword());
			ftpClient.execPBSZ(0);
			ftpClient.execPROT("P");
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			File firstLocalFile = new File(LOCALROUTE + fileName);

			String firstRemoteFile = PUTREMOTEFILE + fileName;
			String lastRemoteFile = GETREMOTEFILE + fileName;
			InputStream inputStream = new FileInputStream(firstLocalFile);

			System.out.println("Start uploading first file");
			System.out.println(ftpClient.getReplyCode());
			boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
			inputStream.close();
			if (done) {
				System.out.println("The first file is uploaded successfully.");
				nvalue = 1;
				String getResponse = LOCALROUTEOUT + fileName;

				OutputStream outputStream = new FileOutputStream(getResponse);
				boolean redone = false;

				while (redone == false) {
					System.out.println("Actual: " + redone);
					redone = ftpClient.retrieveFile(lastRemoteFile,
							outputStream);
				}
			}

		} catch (IOException ex) {
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
			nvalue = 2;
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return nvalue;
	}

	public static int uploadFileto(String fileName) {
		XMLCreateRepository xml_DAO = new XMLCreateRepository();
		XMLFTPUserDTO ftpAccess = xml_DAO.userAccess();
		if (ftpAccess.getType().equals("FTPS")) {
			return uploadFiletoFTPS(fileName, ftpAccess);
		} else {
			return uploadFiletoFTP(fileName, ftpAccess);
		}
	}

}
