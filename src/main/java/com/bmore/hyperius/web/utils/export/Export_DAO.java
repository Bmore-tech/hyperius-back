package com.bmore.hyperius.web.utils.export;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.utils.Utils;

@Component
public class Export_DAO {
    
  @Autowired
  private DBConnection dbConnection;

	private static final String getDataT005U = "SELECT SPRAS, LAND1, BLAND, BEZEI from T005U WITH(NOLOCK) WHERE BLAND = ?";
	private static final String getDataT005T = "SELECT SPRAS, LAND1, LANDX FROM T005T WITH(NOLOCK) WHERE LAND1 = ?";
	private static final String getDataVBAK = "SELECT VBELN, BSTNK, AUDAT, VDATU, KUNNR, KNUMV, ZKUNWE from VBAK WHERE VBELN = ?";
	private static final String getDataVBFA = "SELECT VBELV, POSNV, VBELN, XWERKS from VBFA WHERE VBELN = ?";
	private static final String getDataKNA1 = "SELECT KUNNR, LAND1, NAME1, CASE WHEN NAME2 IS NULL THEN '' ELSE NAME2 END AS NAME2, ORT01, PSTLZ, REGIO, SORTL, STRAS, TELF1, TELFX, BRSCH, KTOKD, LIFNR, LZONE, ADRNR, [STCD1], BAHNE, XWERKS FROM KNA1 WITH(NOLOCK) WHERE KUNNR = ?";
	private static final String getDataADRC = "SELECT ADDRNUMBER, NAME1, NAME2, STREET, HOUSE_NUM1, POST_CODE1, CITY1, CITY2, COUNTRY, HOUSE_NUM2, REGION from ADRC WITH(NOLOCK) WHERE ADDRNUMBER = ?";
	private static final String getDataLIPS = "SELECT MATNR, BISMT, LFIMG, KBETR FROM VS_BCPS_REM_QUAN WHERE VBELN = ?";
	private static final String getDataVTTK = "SELECT VSART, SIGNI, TNDR_TRKID, EXTI2, VOLUM, BTGEW, TEXT3, TDLINE FROM VS_BCPS_REM_REFIMP WITH(NOLOCK) WHERE VBELN = ?";
	private static final String getDataAdua =  "SELECT NAME1, BEZEI, ORT01 FROM VS_BCPS_ADUANA WHERE VBELN = ?";
	
	private static final DecimalFormat f = new DecimalFormat("###,###,###,###.00");
	private static final String ZCONTEXPORT = "exec sp_bcps_wm_contabilizar_entrega_salida_exportacion ?,?,?,?,?,?,?";

	private static final String exportacionExist = "SELECT COUNT(*) AS TOTAL FROM zContingencia WITH(NOLOCK) WHERE ENTREGA  = ?  AND IDPROC = 12 AND SELLO IS NOT NULL AND NOCAJA IS NOT NULL AND TALONEMBARQUE IS NOT NULL AND OPERADOR IS NOT NULL";
	private static final String exportacionXmlExist = "SELECT COUNT(*) AS TOTAL FROM zContingencia WITH(NOLOCK) WHERE ENTREGA  = ?  AND IDPROC = 12 AND UUID IS NOT NULL";
	private static final String exportacionValues = "SELECT SELLO, NOCAJA, TALONEMBARQUE, OPERADOR, SELLO_IMPORTADOR FROM zContingencia WITH(NOLOCK) WHERE ENTREGA = ? AND IDPROC = 12";

	private static final Logger LOCATION = LoggerFactory.getLogger(Export_DAO.class);

	// getDataAduana step6
	public Export_DTO getDataAduana(Export_DTO exp_DTO) {
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(getDataAdua);
			stm.setString(1, exp_DTO.getEntrega());
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				exp_DTO.setAduanaSalida(rs.getString("BEZEI"));
				exp_DTO.setAgenteAduanal(rs.getString("NAME1"));
				exp_DTO.setFrontera(rs.getString("ORT01"));
			}
		} catch (SQLException e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} catch (Exception e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
				exp_DTO = null;
			}
		}
		return exp_DTO;
	}	
	
	// getDATAVTTK Step 5
	public Export_DTO getDataVTTK(Export_DTO exp_DTO) {
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(getDataVTTK);
			stm.setString(1, exp_DTO.getEntrega());
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				exp_DTO.setLitrosEmbarque(  f.format( new BigDecimal(rs.getString("VOLUM"))) + " L");
				exp_DTO.setViaEmbarque(rs.getString("VSART"));
				exp_DTO.setPeso( f.format( new BigDecimal(rs.getString("BTGEW")))  + " KGS");
				exp_DTO.setEntarimada(rs.getString("TDLINE"));
			}
		} catch (SQLException e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} catch (Exception e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
				exp_DTO = null;
			}
		}
		return exp_DTO;
	}

	// GetDATALIPS STEP 4
	public Export_DTO getDataLIPS(Export_DTO exp_DTO) {
		Connection con = dbConnection.createConnection();
		List<String> clave = new ArrayList<String>();
		List<String> descripcion = new ArrayList<String>();
		List<String> precioUnitario = new ArrayList<String>();
		List<String> cantidad = new ArrayList<String>();
		try {
			PreparedStatement stm = con.prepareStatement(getDataLIPS);
			stm.setString(1, exp_DTO.getEntrega());
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				clave.add(String.valueOf(Integer
						.parseInt(rs.getString("MATNR"))));
				descripcion.add(rs.getString("BISMT"));
				cantidad.add(rs.getString("LFIMG"));
				precioUnitario.add(rs.getString("KBETR"));
			}
			if (clave != null && descripcion != null && cantidad != null
					&& precioUnitario != null) {
				exp_DTO.setTotal(precioUnitario, cantidad);
				exp_DTO.setClave(Utils.listToString(clave));
				exp_DTO.setDescripcion(Utils.listToString(descripcion));
				exp_DTO.setPrecioUnitario(Utils.listToString(precioUnitario));
				exp_DTO.setCantidad(Utils.listToString(cantidad));
			}
			LOCATION.error(exp_DTO.toString());
		} catch (SQLException e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} catch (Exception e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			}
		}
		return exp_DTO;
	}

	// GetDataKNA1 STEP 3
	public Export_DTO getDataKNA1(Export_DTO exp_DTO, Integer typo) {
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(getDataKNA1);
			switch (typo) {
			case 1:
				stm.setString(1, exp_DTO.getDistribuidor());
				break;

			case 2:
				stm.setString(1, exp_DTO.getNombreDistribuidor());
				break;

			default:
				throw new Exception();
			}
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				switch (typo) {
				case 1:
					exp_DTO.setNombreImportador(rs.getString("NAME1") + " "
							+ rs.getString("NAME2"));
					break;

				case 2:
					exp_DTO.setNombreDistribuidor(rs.getString("NAME1") + " "
							+ rs.getString("NAME2"));
					exp_DTO.setDireccion(rs.getString("STRAS")
							+ rs.getString("PSTLZ"));
					exp_DTO.setCiudad(rs.getString("ORT01"));
					exp_DTO = getDataT005U(rs.getString("REGIO"), exp_DTO);
					exp_DTO = getDataT005T(rs.getString("ADRNR"), exp_DTO);
					break;

				default:
					throw new Exception();
				}
			}
		} catch (SQLException e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} catch (Exception e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			}
		}
		return exp_DTO;
	}

	public Export_DTO getDataT005T(String aDRNR, Export_DTO exp_DTO) {
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(getDataADRC);
			stm.setString(1, aDRNR);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				exp_DTO.setPais(rs.getString("COUNTRY"));
				stm = con.prepareStatement(getDataT005T);
				stm.setString(1, exp_DTO.getPais());
				rs = stm.executeQuery();
				if (rs.next()) {
					exp_DTO.setPais(rs.getString("LANDX"));
				} else {
					exp_DTO.setPais("");
				}
			}
		} catch (SQLException e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} catch (Exception e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			}
		}
		return exp_DTO;
	}

	public Export_DTO getDataT005U(String bLand, Export_DTO exp_DTO) {
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(getDataT005U);
			stm.setString(1, bLand);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				exp_DTO.setEstado(rs.getString("BEZEI"));
			}
		} catch (SQLException e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} catch (Exception e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			}
		}
		return exp_DTO;
	}

	// GET DATA VBACK STEP 2
	public Export_DTO getDataVBAK(Export_DTO exp_DTO) {
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(getDataVBAK);
			stm.setString(1, exp_DTO.getPedido());
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				exp_DTO.setDistribuidor(rs.getString("KUNNR"));
				exp_DTO.setNombreDistribuidor(rs.getString("ZKUNWE"));
				exp_DTO.setPedidoImportador(rs.getString("BSTNK"));
			}
		} catch (SQLException e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} catch (Exception e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			}
		}
		return exp_DTO;
	}

	// GET DATA VBFA STEP 1
	public Export_DTO getDataVBFA(Export_DTO exp_DTO) {
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(getDataVBFA);
			stm.setString(1, exp_DTO.getEntrega());
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				exp_DTO.setPedido(rs.getString("VBELV"));
			}
		} catch (SQLException e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} catch (Exception e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			exp_DTO = null;
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			}
		}
		return exp_DTO;
	}

	public ResultDTO zContExport(Export_DTO exp_DTO) {
		ResultDTO rDt = new ResultDTO();
		Connection con = dbConnection.createConnection();
		try {
			CallableStatement stm = con.prepareCall(ZCONTEXPORT);
			stm.setString(1, Utils.zeroFill(exp_DTO.getEntrega(),10));
			stm.setString(2, exp_DTO.getNumeroSello());
			stm.setString(3, exp_DTO.getNumeroCaja());
			stm.setString(4, exp_DTO.getTalonEmbarque());
			stm.setString(5, exp_DTO.getOperador());
			stm.setString(6, exp_DTO.getSelloImportador());
			stm.registerOutParameter(7, java.sql.Types.INTEGER);
			stm.execute();
			rDt.setId(stm.getInt(7));
			LOCATION.error("EXPORT_DAO: " + rDt.toString());
		} catch (SQLException e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
		} catch (Exception e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			}
		}

		return rDt;
	}

	public ResultDTO zConRemExist(Export_DTO exDto) {
		ResultDTO rDt = new ResultDTO();
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(exportacionExist);
			stm.setString(1, exDto.getEntrega());
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				rDt.setId(rs.getInt("TOTAL"));
			}
		} catch (SQLException e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
		} catch (Exception e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			}
		}

		return rDt;
	}

	public ResultDTO zConXmlExist(Export_DTO exDto) {
		ResultDTO rDt = new ResultDTO();
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(exportacionXmlExist);
			stm.setString(1, exDto.getEntrega());
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				rDt.setId(rs.getInt("TOTAL"));
			}
		} catch (SQLException e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
		} catch (Exception e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			}
		}
		return rDt;
	}

	public Export_DTO zConRemData(Export_DTO exDto) {
		Connection con = dbConnection.createConnection();
		try {
			PreparedStatement stm = con.prepareStatement(exportacionValues);
			stm.setString(1, exDto.getEntrega());
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				exDto.setNumeroSello(Utils.isNull(rs.getString("SELLO")));
				exDto.setNumeroCaja(Utils.isNull(rs.getString("NOCAJA")));
				exDto.setTalonEmbarque(Utils.isNull(rs
						.getString("TALONEMBARQUE")));
				exDto.setOperador(Utils.isNull(rs.getString("OPERADOR")));
				exDto.setSelloImportador(Utils.isNull(rs.getString("SELLO_IMPORTADOR")));
			}
		} catch (SQLException e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
		} catch (Exception e) {
			LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Export_DAO: " + e.getLocalizedMessage());
			}
		}
		return exDto;
	}
}
