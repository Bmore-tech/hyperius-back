package com.bmore.hyperius.web.repository.old;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bmore.hyperius.config.DBConnection;
import com.bmore.hyperius.web.dto.CreacionEntregaItemDTO;
import com.bmore.hyperius.web.dto.CreacionEntregasDTO;
import com.bmore.hyperius.web.dto.CrecionEntregaDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.utils.Utils;

@Repository
public class CreacionEntregasRepository {
  
  @Autowired
  private DBConnection dbConnection;

	private static final String getDataMateriales = "SELECT MATNR, MAKTX FROM VS_BCPS_ENTREGAS_MATERIALES WITH(NOLOCK)";
	private static final String getDataTarimas = "SELECT TARIMA, MAKTX FROM  VS_BCPS_ENTREGAS_NORMA_EMBALAJE WITH(NOLOCK) WHERE MATERIAL = ?";
	private static final String getDataCentros = "SELECT WERKS, DESCRIPCION FROM zCentrosBCPS WITH(NOLOCK) WHERE WERKS LIKE 'PC%' order by WERKS";
	private static final String getDataAgencias = "SELECT T001W.WERKS, T001W.NAME1,LFA1.LIFNR,LFA1.NAME1 as NAME_LIFNR FROM T001W WITH(NOLOCK) inner join LFA1 LFA1 on dbo. T001W.WERKS=LFA1.WERKS WHERE T001W.WERKS NOT LIKE 'PC%' ";
	private static final String getDataTransportes = "SELECT VBELN, ORIGEN, NOMBRE_ORIGEN, DESTINO, NOMBRE_DESTINO, TRANSPORTISTA FROM VS_BCPS_ENTREGAS_TRANSPORTES WHERE ORIGEN = ? AND DESTINO = ?";

	private static final String GENERAR_ENTREGA = "exec sp_bcps_generate_entregas ?, ?, ?, ?, ?,?";

	private static final String GENERAR_LIPS_ZCONT_ENTREGA = "exec SP_BCPS_ENTREGA_POSICIONES ?, ?, ?, ?, ?, ?, ? ,? ,? ,?,?,?,?,?";

	private static final String UPDATE_VTTK_VTTP = "exec SP_BCPS_ALTER_TRANSPORTE ?, ?, ?, ?,?,?";

	private static final String GET_ENTREGAS = "SELECT ENTREGA,VTTP.TKNUM,LIKP.LIFNR,LFA1.WERKS,LFA1.NAME1,LFA1.NAME2,ZCENTROSBCPS.WERKS as WERKSD,ZCENTROSBCPS.DESCRIPCION FROM ZCONTINGENCIA ZCONTINGENCIA "
			+ "INNER JOIN VTTP VTTP ON VTTP.VBELN= ZCONTINGENCIA.ENTREGA  "
			+ "LEFT JOIN LIKP LIKP ON ZCONTINGENCIA.ENTREGA= LIKP.VBELN "
			+ "LEFT JOIN LFA1 LFA1 ON LIKP.LIFNR= LFA1.LIFNR "
			+ "LEFT JOIN ZCENTROSBCPS ZCENTROSBCPS  ON ZCENTROSBCPS.KUNNR = LIKP.KUNNR "
			+ "WHERE IDPROC='31'";

	private static final String GET_DATA_POS = "select POSNR, MATNR, ARKTX , LFIMG  from LIPS with(nolock) "
			+ "where VBELN= ?";

	private static final String GET_DATA_POS_2 = " select top 1 VEKP.VHILM,MAKT.MAKTX  from VEKP  VEKP "
			+ "inner join VEPO VEPO on VEKP.VENUM = VEPO.VENUM "
			+ "inner join MAKT MAKT on VEKP.VHILM=MAKT.MATNR "
			+ "where VEPO.MATNR= ? and VEKP.VPOBJKEY= ?";

	private static final String GET_DATA_POS_TOT = " select count(*) as totalHU from VEPO VEPO "
			+ "inner join VEKP VEKP on VEKP.VENUM=VEPO.VENUM "
			+ "where MATNR= ? and VEKP.VPOBJKEY= ?";

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	public CreacionEntregasDTO obtieneMaterialDAO() {
		CreacionEntregasDTO materialesDTO = new CreacionEntregasDTO();
		Connection con = dbConnection.createConnection();
		ResultDTO resultDT = new ResultDTO();
		CreacionEntregaItemDTO materialItemDTO = new CreacionEntregaItemDTO();
		List<CrecionEntregaDTO> listMaterialDTOs = new ArrayList<CrecionEntregaDTO>();
		try {
			PreparedStatement stm = con.prepareStatement(getDataMateriales);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				CrecionEntregaDTO materialDTO = new CrecionEntregaDTO();
				materialDTO.setMatnr(rs.getString("MATNR"));
				materialDTO.setMaktx(rs.getString("MAKTX"));
				listMaterialDTOs.add(materialDTO);
			}
			if (listMaterialDTOs.size() > 0) {
				LOCATION
						.error("CreacionEntrega Materiales Recuperados con exito");
				resultDT.setId(1);
				resultDT
						.setMsg("CreacionEntrega Materiales Recuperados con exito");
			} else {
				LOCATION.error("CreacionEntrega Materiales no Recuperados");
				resultDT.setId(3);
				resultDT.setMsg("CreacionEntrega Materiales no Recuperados");
			}

			materialItemDTO.setItem(listMaterialDTOs);
			materialesDTO.setItems(materialItemDTO);

		} catch (SQLException e) {
			LOCATION.error("obtieneMaterialDAO SQLException: "
					+ e.getMessage());
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} catch (Exception e) {
			LOCATION.error("obtieneMaterialDAO Exception: " + e.getMessage());
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("obtieneMaterialDAO Exception: "
						+ e.getMessage());
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}
		materialesDTO.setResultDT(resultDT);
		return materialesDTO;
	}

	public CreacionEntregasDTO obtieneTarimasDAO(CrecionEntregaDTO materialDTO) {
		CreacionEntregasDTO materialesDTO = new CreacionEntregasDTO();
		Connection con = dbConnection.createConnection();
		ResultDTO resultDT = new ResultDTO();
		CreacionEntregaItemDTO materialItemDTO = new CreacionEntregaItemDTO();
		List<CrecionEntregaDTO> listMaterialDTOs = new ArrayList<CrecionEntregaDTO>();
		try {
			PreparedStatement stm = con.prepareStatement(getDataTarimas);
			stm.setString(1, Utils.zeroFill(materialDTO.getMatnr(), 18));
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				CrecionEntregaDTO tarimasDTO = new CrecionEntregaDTO();
				tarimasDTO.setPacknr(rs.getString("TARIMA"));
				tarimasDTO.setPacknrTxt(rs.getString("MAKTX"));
				listMaterialDTOs.add(tarimasDTO);
			}
			if (listMaterialDTOs.size() > 0) {
				LOCATION
						.error("CreacionEntrega Tarimas Recuperadas con exito");
				resultDT.setId(1);
				resultDT
						.setMsg("CreacionEntrega Tarimas Recuperadas con exito");
			} else {
				LOCATION.error("CreacionEntrega Tarimas no Recuperadas");
				resultDT.setId(3);
				resultDT.setMsg("CreacionEntrega Tarimas no Recuperadas");
			}

			materialItemDTO.setItem(listMaterialDTOs);
			materialesDTO.setItems(materialItemDTO);

		} catch (SQLException e) {
			LOCATION.error("obtieneMaterialDAO SQLException: "
					+ e.getMessage());
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} catch (Exception e) {
			LOCATION.error("obtieneMaterialDAO Exception: " + e.getMessage());
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("obtieneMaterialDAO Exception: "
						+ e.getMessage());
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}
		materialesDTO.setResultDT(resultDT);
		return materialesDTO;
	}

	public CreacionEntregasDTO obtieneCentrosDAO() {

		CreacionEntregasDTO centrosDTO = new CreacionEntregasDTO();
		Connection con = dbConnection.createConnection();
		ResultDTO resultDT = new ResultDTO();
		CreacionEntregaItemDTO centrosItemDTO = new CreacionEntregaItemDTO();
		List<CrecionEntregaDTO> listaCentrosDTO = new ArrayList<CrecionEntregaDTO>();
		try {
			PreparedStatement stm = con.prepareStatement(getDataCentros);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				CrecionEntregaDTO tarimasDTO = new CrecionEntregaDTO();
				tarimasDTO.setWerks(rs.getString("WERKS"));
				tarimasDTO.setWerksDesc(rs.getString("DESCRIPCION"));
				listaCentrosDTO.add(tarimasDTO);
			}

			if (listaCentrosDTO.size() > 0) {

				CrecionEntregaDTO tarimasDTO = new CrecionEntregaDTO();
				tarimasDTO.setWerks("");
				tarimasDTO.setWerksDesc("");
				listaCentrosDTO.add(0, tarimasDTO);

				LOCATION
						.error("CreacionEntrega Centros Recuperados con exito");
				resultDT.setId(1);
				resultDT
						.setMsg("CreacionEntrega Centros Recuperados con exito");
			} else {
				LOCATION.error("CreacionEntrega Centros No Recuperados");
				resultDT.setId(3);
				resultDT.setMsg("CreacionEntrega Centros No Recuperados");
			}

			centrosItemDTO.setItem(listaCentrosDTO);
			centrosDTO.setItems(centrosItemDTO);

		} catch (SQLException e) {
			LOCATION.error("obtieneMaterialDAO SQLException: "
					+ e.getMessage());
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} catch (Exception e) {
			LOCATION.error("obtieneMaterialDAO Exception: " + e.getMessage());
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("obtieneMaterialDAO Exception: "
						+ e.getMessage());
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}
		centrosDTO.setResultDT(resultDT);
		return centrosDTO;
	}

	public CreacionEntregasDTO obtieneAgenciasDAO() {
		CreacionEntregasDTO agenciasDTO = new CreacionEntregasDTO();

		Connection con = dbConnection.createConnection();
		ResultDTO resultDT = new ResultDTO();
		CreacionEntregaItemDTO agenciasItemDTO = new CreacionEntregaItemDTO();
		List<CrecionEntregaDTO> listAgenciasItemDTO = new ArrayList<CrecionEntregaDTO>();

		try {
			PreparedStatement stm = con.prepareStatement(getDataAgencias);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				CrecionEntregaDTO agenciaDTO = new CrecionEntregaDTO();
				agenciaDTO.setWerks(rs.getString("WERKS"));
				agenciaDTO.setWerksDesc(rs.getString("NAME1"));
				agenciaDTO.setLifnr(rs.getString("LIFNR"));
				agenciaDTO.setLifnrDesc(rs.getString("NAME_LIFNR"));
				listAgenciasItemDTO.add(agenciaDTO);
			}

			if (listAgenciasItemDTO.size() > 0) {

				LOCATION
						.error("CreacionEntrega Agencias Recuperadas con exito: "
								+ listAgenciasItemDTO.size());
				resultDT.setId(1);
				resultDT
						.setMsg("CreacionEntrega Agencias Recuperadas con exito");
			} else {

				LOCATION.error("CreacionEntrega Agencias no Recuperadas");
				resultDT.setId(3);
				resultDT.setMsg("CreacionEntrega Agencias no Recuperadas");
			}

			agenciasItemDTO.setItem(listAgenciasItemDTO);
			agenciasDTO.setItems(agenciasItemDTO);

		} catch (SQLException e) {
			LOCATION.error("obtieneMaterialDAO SQLException: "
					+ e.getMessage());
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} catch (Exception e) {
			LOCATION.error("obtieneMaterialDAO Exception: " + e.getMessage());
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("obtieneMaterialDAO Exception: "
						+ e.getMessage());
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}
		agenciasDTO.setResultDT(resultDT);
		agenciasDTO.setItems(agenciasItemDTO);
		return agenciasDTO;
	}

	public CreacionEntregasDTO obtieneTransportesDAO(
			CrecionEntregaDTO transporteDTO) {
		CreacionEntregasDTO materialesDTO = new CreacionEntregasDTO();
		Connection con = dbConnection.createConnection();
		ResultDTO resultDT = new ResultDTO();
		CreacionEntregaItemDTO materialItemDTO = new CreacionEntregaItemDTO();
		List<CrecionEntregaDTO> listMaterialDTOs = new ArrayList<CrecionEntregaDTO>();
		try {
			PreparedStatement stm = con.prepareStatement(getDataTransportes);
			stm.setString(1, transporteDTO.getWerks());
			stm.setString(2, transporteDTO.getWerksD());
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				CrecionEntregaDTO materialDTO = new CrecionEntregaDTO();
				materialDTO.setTknum(rs.getString("VBELN"));
				materialDTO.setTknumTransport(rs.getString("TRANSPORTISTA"));
				materialDTO.setWerks(rs.getString("ORIGEN"));
				materialDTO.setWerksDesc(rs.getString("NOMBRE_ORIGEN"));
				materialDTO.setWerksD(rs.getString("DESTINO"));
				materialDTO.setWerksDDesc(rs.getString("NOMBRE_DESTINO"));

				listMaterialDTOs.add(materialDTO);
			}
			if (listMaterialDTOs.size() > 0) {

				resultDT.setId(1);
				resultDT
						.setMsg("CreacionEntrega Transportes Recuperados con exito");
			} else {

				resultDT.setId(3);
				resultDT.setMsg("No existen transportes con origen en "
						+ transporteDTO.getWerks() + " y destino en "
						+ transporteDTO.getWerksD());
			}

			materialItemDTO.setItem(listMaterialDTOs);
			materialesDTO.setItems(materialItemDTO);

		} catch (SQLException e) {

			LOCATION.error("obtieneMaterialDAO SQLException: "
					+ e.getMessage());
			resultDT.setId(2);
			resultDT.setMsg("Error SQLException: " + e.getMessage());

		} catch (Exception e) {

			LOCATION.error("obtieneMaterialDAO Exception: " + e.getMessage());
			resultDT.setId(2);
			resultDT.setMsg("Exception SQLException: " + e.getMessage());

		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("obtieneMaterialDAO Exception: "
						+ e.getMessage());
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
			}
		}
		materialesDTO.setResultDT(resultDT);
		return materialesDTO;
	}

	public ResultDTO creacionEntregaBCPS(CrecionEntregaDTO item, String user) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = dbConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			callableStatement = con.prepareCall(GENERAR_ENTREGA);
			// AUFNR,MATNR,VHILM,VEMNG,MEINS,PACKNR

			callableStatement.setString(1, item.getWerks());
			callableStatement.setString(2, item.getWerksD());
			callableStatement.setString(3, user);
			callableStatement.setString(4, item.getLifnr());
			callableStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
			callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);

			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(6);
			LOCATION.error("ID:" + id);

			result.setId(id);
			result.setMsg(callableStatement.getString(5));

		} catch (SQLException e) {
			result.setId(200);
			result.setMsg(e.getMessage());

			LOCATION.error("SQLException1 in creacionEntregaBCPS:"
					+ e.getMessage() + " " + e.toString());
		} catch (Exception en) {
			result.setId(200);
			result.setMsg(en.getMessage());
			LOCATION
					.error("Exception in creacionEntregaBCPS:" + en.toString());

		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
				LOCATION.error("Exception in creacionEntregaBCPS:"
						+ e.toString());

			}
		}

		return result;

	}

	public ResultDTO creacionLipsZcontingenciaEntregaBCPS(
			CrecionEntregaDTO item, String user, String entrega) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = dbConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			callableStatement = con.prepareCall(GENERAR_LIPS_ZCONT_ENTREGA);

			// @VBELN NVARCHAR(10),
			// @POSNR NVARCHAR(7),
			// @USER NVARCHAR(10),
			// @MATNR NVARCHAR(18),
			// @WERKS NVARCHAR(6),
			// @LGORT NVARCHAR(8),
			// @LFIMG NVARCHAR(15),
			// @MEINS NVARCHAR(4),
			// @ARKTX NVARCHAR(40),
			// @TOTHU INT,
			// @TKNUM INT,
			// @LIFNR INT,
			// @WERKD
			// @RETURN INT OUT

			callableStatement.setString(1, entrega);
			callableStatement.setString(2, item.getPos());
			callableStatement.setString(3, user);
			callableStatement.setString(4, item.getMatnr());
			callableStatement.setString(5, item.getWerks());
			callableStatement.setString(6, "LV01");
			callableStatement.setString(7, item.getLfimg());
			callableStatement.setString(8, item.getUnidadMedida());
			callableStatement.setString(9, item.getMaktx());
			callableStatement.setString(10, item.getQytHus());
			callableStatement.setString(11, item.getTknum());
			callableStatement.setString(12, item.getLifnr());
			callableStatement.setString(13, item.getWerksD());

			callableStatement.registerOutParameter(14, java.sql.Types.INTEGER);

			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(14);
			LOCATION.error("ID:" + id);

			result.setId(id);

		} catch (SQLException e) {
			result.setId(200);
			result.setMsg(e.getMessage());

			LOCATION
					.error("creacionLipsZcontingenciaEntregaBCPS SQLException1 in creacionEntregaBCPS:"
							+ e.toString());
		} catch (Exception en) {
			result.setId(200);
			result.setMsg(en.getMessage());
			LOCATION
					.error("creacionLipsZcontingenciaEntregaBCPS Exception in creacionEntregaBCPS:"
							+ en.toString());

		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
				LOCATION
						.error("creacionLipsZcontingenciaEntregaBCPS Exception in creacionEntregaBCPS:"
								+ e.toString());

			}
		}

		return result;

	}

	public ResultDTO updateVTTP(String transporte, String entrega, String werks,
			String user) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = dbConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			callableStatement = con.prepareCall(UPDATE_VTTK_VTTP);
			// @TKNUM NVARCHAR(12), @VBELN NVARCHAR(10), @TYPE INT, @RETURN INT
			// OUT

			callableStatement.setString(1, transporte);
			callableStatement.setString(2, entrega);
			callableStatement.setInt(3, 1);
			callableStatement.setString(4, werks);
			callableStatement.setString(5, user);

			callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);

			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(6);
			LOCATION.error("ID:" + id);
			result.setId(id);

		} catch (SQLException e) {
			result.setId(200);
			result.setMsg(e.getMessage());

			LOCATION.error("updateVTTP SQLException1 in creacionEntregaBCPS:"
					+ e.toString());
		} catch (Exception en) {
			result.setId(200);
			result.setMsg(en.getMessage());
			LOCATION.error("updateVTTP Exception in creacionEntregaBCPS:"
					+ en.toString());

		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
				LOCATION.error("updateVTTP Exception in creacionEntregaBCPS:"
						+ e.toString());

			}
		}

		return result;

	}

	public CreacionEntregasDTO getEntregas() {

		ResultDTO result = new ResultDTO();

		CreacionEntregasDTO materialesDTO = new CreacionEntregasDTO();

		Connection con = dbConnection.createConnection();

		List<CrecionEntregaDTO> entregasDTO = new ArrayList<CrecionEntregaDTO>();
		CreacionEntregaItemDTO items = new CreacionEntregaItemDTO();

		try {

			LOCATION.error("getEntregas DAO");
			PreparedStatement stm = con.prepareStatement(GET_ENTREGAS);

			ResultSet rs = stm.executeQuery();

			while (rs.next()) {
				LOCATION.error("While");
				CrecionEntregaDTO entrega = new CrecionEntregaDTO();

				entrega.setTknum(rs.getString("TKNUM"));
				entrega.setVbeln(rs.getString("ENTREGA"));
				entrega.setWerks(rs.getString("WERKS"));
				entrega.setLifnr(rs.getString("LIFNR"));
				entrega.setWerksDesc(rs.getString("NAME1") + " "
						+ rs.getString("NAME2"));
				entrega.setWerksD(rs.getString("WERKSD"));
				entrega.setWerksDDesc(rs.getString("DESCRIPCION"));

				entregasDTO.add(entrega);
			}

			items.setItem(entregasDTO);
			materialesDTO.setItems(items);

			if (entregasDTO.size() > 0) {
				LOCATION.error("Size >0");
				result.setId(1);
				result.setMsg("Entregas encontradas");

			} else {
				LOCATION.error("else <0");
				result.setId(2);
				result
						.setMsg("Aun no se han generado entregas por CSC Logística en el sistema BCPS");
			}

		} catch (SQLException e) {

			result.setId(200);
			result.setMsg(e.getMessage());

			LOCATION.error("SQLException1 in getEntregas:" + e.toString());
		} catch (Exception en) {
			result.setId(200);
			result.setMsg(en.getMessage());
			LOCATION.error("Exception1 in getEntregas:" + en.toString());

		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
				LOCATION.error("Exception2 in getEntregas:" + e.toString());

			}
		}

		materialesDTO.setResultDT(result);

		return materialesDTO;

	}

	public CreacionEntregasDTO getEntrega(String vbeln) {

		ResultDTO result = new ResultDTO();

		CreacionEntregasDTO materialesDTO = new CreacionEntregasDTO();

		Connection con = dbConnection.createConnection();

		List<CrecionEntregaDTO> entregasDTO = new ArrayList<CrecionEntregaDTO>();
		CreacionEntregaItemDTO items = new CreacionEntregaItemDTO();

		try {

			PreparedStatement stm = con.prepareStatement(GET_DATA_POS);

			stm.setString(1, vbeln);

			ResultSet rs = stm.executeQuery();

			while (rs.next()) {

				CrecionEntregaDTO entrega = new CrecionEntregaDTO();

				entrega.setPos(rs.getString("POSNR"));
				entrega.setMatnr(rs.getString("MATNR"));
				entrega.setMaktx(rs.getString("ARKTX"));
				entrega.setLfimg(rs.getString("LFIMG"));

				PreparedStatement stm2 = con.prepareStatement(GET_DATA_POS_2);

				stm2.setString(1, entrega.getMatnr());
				stm2.setString(2, vbeln);

				ResultSet rs2 = stm2.executeQuery();

				if (rs2.next()) {
					entrega.setPacknr(rs2.getString("VHILM"));
					entrega.setPacknrTxt(rs2.getString("MAKTX"));
				}

				PreparedStatement stm3 = con.prepareStatement(GET_DATA_POS_TOT);

				stm3.setString(1, entrega.getMatnr());
				stm3.setString(2, vbeln);

				ResultSet rs3 = stm3.executeQuery();

				if (rs3.next()) {
					entrega.setQytHus(rs3.getString("totalHU"));
				}

				entregasDTO.add(entrega);
			}

			items.setItem(entregasDTO);
			materialesDTO.setItems(items);

			if (entregasDTO.size() > 0) {
				LOCATION.error("Size >0");
				result.setId(1);
				result.setMsg("Entrega encontrada");

			} else {
				LOCATION.error("else <0");
				result.setId(2);
				result
						.setMsg("No se encontro el detalle de la entrega solicitada: "
								+ vbeln);
			}

		} catch (SQLException e) {

			result.setId(200);
			result.setMsg(e.getMessage());

			LOCATION.error("SQLException1 in getEntregas:" + e.toString());
		} catch (Exception en) {
			result.setId(200);
			result.setMsg(en.getMessage());
			LOCATION.error("Exception1 in getEntregas:" + en.toString());

		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
				LOCATION.error("Exception2 in getEntregas:" + e.toString());

			}
		}

		materialesDTO.setResultDT(result);

		return materialesDTO;

	}

	public ResultDTO eliminarEntrega(String vbeln, String transporte,
			String werks, String usuario) {

		ResultDTO result = new ResultDTO();
		result.setId(0);

		Connection con = dbConnection.createConnection();

		CallableStatement callableStatement = null;

		try {

			callableStatement = con.prepareCall(UPDATE_VTTK_VTTP);

			callableStatement.setString(1, transporte);
			callableStatement.setString(2, vbeln);
			callableStatement.setInt(3, 2);
			callableStatement.setString(4, werks);
			callableStatement.setString(5, usuario);

			callableStatement.registerOutParameter(6, java.sql.Types.INTEGER);

			callableStatement.execute();

			int id = 0;
			id = callableStatement.getInt(6);
			LOCATION.error("ID:" + id);

			result.setId(id);

			if (id == 1) {
				result.setMsg("Entrega eliminada");
			} else {
				result.setMsg("Error al eliminar la entrega");
			}

		} catch (SQLException e) {
			result.setId(200);
			result.setMsg(e.getMessage());

			LOCATION.error("SQLException1 in creacionEntregaBCPS:"
					+ e.getMessage() + " " + e.toString());
		} catch (Exception en) {
			result.setId(200);
			result.setMsg(en.getMessage());
			LOCATION
					.error("Exception in creacionEntregaBCPS:" + en.toString());

		} finally {
			try {
				// DBConnection.closeConnection(con);
			} catch (Exception e) {
				result.setId(200);
				result.setMsg(e.getMessage());
				LOCATION.error("Exception in creacionEntregaBCPS:"
						+ e.toString());

			}
		}

		return result;

	}
}
