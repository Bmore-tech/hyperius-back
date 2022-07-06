package com.bmore.hyperius.web.repository.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.dto.ReporteOperacionesDTO;
import com.bmore.hyperius.web.dto.ReporteShippingDTO;
import com.bmore.hyperius.web.dto.ServletReporteProformaDTO;
import com.bmore.hyperius.web.utils.Utils;

public class ServletReporteRepository {

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	private static final String getTknum = "SELECT DISTINCT TKNUM AS TKNUM FROM VTTP WHERE VBELN = ?";

	private static final String getShipping = "SELECT CONTENEDOR, SELLO, BOOKING, DESTINO, PESO, NAVIERA1, NAVIERA2, MEDIDA, "
			+ "AA1, AA2, SKU FROM VS_BCPS_REPORTE_SHIPPING WHERE CENTRO = ?";

	// private static final String getProforma = "SELECT * FROM VS_BCPS_PROFORMA WITH(NOLOCK)";

	public static final String GET_REPORTE_OPERACIONES = "SELECT MATNR, RECEPCION, ALIMENTACION, PRODUCCION, EMBARQUE, RECEPCIONES, EMBARQUES FROM VS_BCPS_UTILS_SUMMARY_BY_MATNR WHERE CENTRO = ?";

	// se le agregó TOP (1000) BORRAR!!!!!!!!!!!!!!
	private static final String GET_INITIAL_STOCK = "SELECT TOP (100) * FROM VS_BCPS_UTILS_SUMMARY_REPORTE_OPERACIONES WITH(NOLOCK) where WERKS= ?  ORDER BY MATNR";

	public List<ServletReporteProformaDTO> getDatosProforma() {
		List<ServletReporteProformaDTO> proformaList = new ArrayList<ServletReporteProformaDTO>();
		Connection con = new DBConnection().createConnection();
		try {
			PreparedStatement stm = con.prepareStatement("");
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				ServletReporteProformaDTO proformaDto = new ServletReporteProformaDTO();

				proformaList.add(proformaDto);
			}
		} catch (SQLException e) {
			LOCATION.error("SQLException getDatosShipping: " + e.getMessage());
		} catch (Exception e) {
			LOCATION.error("Exception getDatosShipping: " + e.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION
						.error("Exception getDatosShipping: " + e.getMessage());
			}
		}
		return proformaList;
	}

	public List<ReporteOperacionesDTO> getReporteOperacionesDAO(
			String werks) {
		List<ReporteOperacionesDTO> proformaList = new ArrayList<ReporteOperacionesDTO>();
		Connection con = new DBConnection().createConnection();
		try {
			PreparedStatement stm = con
					.prepareStatement(GET_REPORTE_OPERACIONES);
			stm.setString(1, werks);
			ResultSet rs = stm.executeQuery();
			proformaList = ReporteOperacionesDTO
					.ServletReporteOperacionesDTORS(rs);
		} catch (SQLException e) {
			LOCATION.error("SQLException getDatosShipping: " + e.getMessage());
		} catch (Exception e) {
			LOCATION.error("Exception getDatosShipping: " + e.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION
						.error("Exception getDatosShipping: " + e.getMessage());
			}
		}
		return proformaList;
	}

	public List<ReporteOperacionesDTO> getReporteOperacionesInitialStockDAO(
			String werks) {
		List<ReporteOperacionesDTO> stock = new ArrayList<ReporteOperacionesDTO>();
		Connection con = new DBConnection().createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(GET_INITIAL_STOCK);
			stm.setString(1, werks);
			ResultSet rs = stm.executeQuery();

			stock = ReporteOperacionesDTO
					.ServletReporteOperacionesStockDTORS(rs);
		} catch (SQLException e) {
			LOCATION.error("SQLException getDatosShipping: " + e.getMessage());
		} catch (Exception e) {
			LOCATION.error("Exception getDatosShipping: " + e.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION
						.error("Exception getDatosShipping: " + e.getMessage());
			}
		}
		return stock;
	}

	public String getTknum(String Vbeln) {
		Connection con = new DBConnection().createConnection();
		String tknum = "";
		try {
			PreparedStatement stm = con.prepareStatement(getTknum);
			stm.setString(1, Vbeln);
			ResultSet rs = stm.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					tknum = rs.getString("TKNUM");
				}
			}
		} catch (SQLException e) {
			LOCATION.error("SQLException getDatosShipping: " + e.getMessage());
		} catch (Exception e) {
			LOCATION.error("Exception getDatosShipping: " + e.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION
						.error("Exception getDatosShipping: " + e.getMessage());
			}
		}
		return tknum;
	}

	public List<ReporteShippingDTO> getDatosShipping(String werks) {
		List<ReporteShippingDTO> shippingList = new ArrayList<ReporteShippingDTO>();
		Connection con = new DBConnection().createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(getShipping);
			stm.setString(1, werks);
			ResultSet rs = stm.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					ReporteShippingDTO shippingDto = new ReporteShippingDTO();
					shippingDto.setContenedor(Utils.isNull(rs
							.getString("CONTENEDOR")));
					shippingDto.setSello(Utils.isNull(rs.getString("SELLO")));
					shippingDto.setBooking(Utils
							.isNull(rs.getString("BOOKING")));
					shippingDto.setDestino(Utils
							.isNull(rs.getString("DESTINO")));
					shippingDto.setPeso(Utils.isNull(rs.getString("PESO")));
					shippingDto.setNaviera(Utils.isNull(rs
							.getString("NAVIERA1"))
							+ Utils.isNull(rs.getString("NAVIERA2")));
					shippingDto.setMedida(Utils.isNull(rs.getString("MEDIDA")));
					shippingDto.setAa(Utils.isNull(rs.getString("AA1"))
							+ Utils.isNull(rs.getString("AA2")));
					shippingDto.setSku(Utils.isNull(rs.getString("SKU")));
					shippingList.add(shippingDto);
				}
			}
		} catch (SQLException e) {
			LOCATION.error("SQLException getDatosShipping: " + e.getMessage());
		} catch (Exception e) {
			LOCATION.error("Exception getDatosShipping: " + e.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION
						.error("Exception getDatosShipping: " + e.getMessage());
			}
		}
		return shippingList;
	}
}