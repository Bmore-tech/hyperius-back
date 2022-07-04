package com.bmore.prueba.web.repository.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.prueba.config.DBConnection;
import com.bmore.prueba.web.dto.HuDTO;
import com.bmore.prueba.web.dto.HuDTOItem;
import com.bmore.prueba.web.dto.HusDTO;
import com.bmore.prueba.web.dto.ResultDTO;
import com.bmore.prueba.web.utils.Utils;
import com.bmore.prueba.web.utils.print.Etiqueta;
import com.bmore.prueba.web.utils.print.Etiquetas;

public class HUsRepositoryOld {

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());

	public static final String RST_VALUE = "RST_";

	static String OBTENER_STATUS_HUS_VEKP = "select DISTINCT VEKP.EXIDV,VEKP.LGNUM, ZPE.Status "
			+ " from HCMDB.dbo.VEKP LEFT JOIN HCMDB.dbo.ZPickingEntregaEntrante ZPE  WITH(NOLOCK) on VEKP.EXIDV = ZPE.EXIDV "
			+ " where VEKP.VPOBJKEY = ?  order by LGNUM,Status DESC";

	static String OBTENER_STATUS_HUS_BCPS = "select hu,ZPE.Status from HCMDB.dbo.zContingencia WITH(NOLOCK) LEFT JOIN "
			+ " HCMDB.dbo.ZPickingEntregaEntrante ZPE WITH(NOLOCK) on zContingencia.HU = ZPE.EXIDV and ZPE.idProceso='1' "
			+ " where ENTREGA= ? and ((IDPROC=28) or (IDPROC = 8))  and hu is not null order by Status DESC";

	static String OBTENER_STATUS_HUS_ZPICKING_ENTREGA = "select EXIDV, Status from HCMDB.dbo.ZPickingEntregaEntrante WITH(NOLOCK) "
			+ "where VBELN = ? and EXIDV is not null and idProceso = ? order by Status desc";

	static String OBTENER_STATUS_HUS_ZPICKING_VIDRIO = "select EXIDV, Status from HCMDB.dbo.ZPickingVidrio WITH(NOLOCK) "
			+ "where VBELN = ? and EXIDV is not null and idProceso = ? order by Status desc";

	String OBTENER_DATOS_HUS_LQUA = "SELECT LQUA.LENUM as HU, LQUA.MATNR as MATERIAL, LQUA.WERKS as CENTRO, MAKT.MAKTX, "
			+ "CASE WHEN LFA1.NAME1+' '+LFA1.NAME2 IS NULL THEN '' ELSE LFA1.NAME1+' '+LFA1.NAME2 END as DescPlanta, LQUA.VERME as VEMNG, LQUA.MEINS as VEMEH, SUBSTRING(LQUA.CHARG,len(LQUA.CHARG) - 3 ,len(LQUA.CHARG)) as CHARG, "
			+ " CONVERT(VARCHAR(10),CONVERT(date,GETDATE()),103) as FECHA, REPLACE(CONVERT(varchar(10), GETDATE(),102),'.','') as FECHA2 "
			+ "FROM HCMDB.dbo.LQUA LQUA WITH(NOLOCK)  "
			+ "INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) on LQUA.MATNR = MAKT.MATNR  "
			+ "LEFT JOIN HCMDB.dbo.zCentrosBCPS ZCBC WITH(NOLOCK) on LQUA.WERKS = ZCBC.werks "
			+ "LEFT JOIN HCMDB.dbo.LFA1 LFA1 WITH(NOLOCK) on ZCBC.LIFNR = LFA1.LIFNR "
			+ "WHERE LQUA.LENUM in(";

	String OBTENER_DATOS_HUS_VEKP = "SELECT VEKP.EXIDV as HU, VEPO.MATNR as MATERIAL, CASE WHEN VEPO.WERKS IS NULL THEN '' ELSE VEPO.WERKS END as CENTRO, "
			+ "MAKT.MAKTX as DESCRIPCION, CASE WHEN LFA1.NAME1+' '+LFA1.NAME2 IS NULL THEN '' ELSE LFA1.NAME1+' '+LFA1.NAME2 END as DescPlanta, VEPO.VEMNG as VEMNG, VEPO.VEMEH as VEMEH, SUBSTRING(VEPO.CHARG,len(VEPO.CHARG) - 3 ,len(VEPO.CHARG)) as CHARG, "
			+ "CONVERT(VARCHAR(10),CONVERT(date,GETDATE()),103) as FECHA, REPLACE(CONVERT(varchar(10), GETDATE(),102),'.','') as FECHA2, VEKP.VHILM as TARIMA "
			+ "FROM HCMDB.dbo.VEKP VEKP WITH(NOLOCK)  "
			+ "INNER JOIN HCMDB.dbo.VEPO VEPO WITH(NOLOCK) ON VEKP.VENUM = VEPO.VENUM AND VEPO.VELIN='1' "
			+ "INNER JOIN HCMDB.dbo.MAKT MAKT WITH(NOLOCK) on VEPO.MATNR = MAKT.MATNR   "
			+ "LEFT JOIN HCMDB.dbo.zCentrosBCPS ZCBC WITH(NOLOCK) on VEPO.WERKS = ZCBC.werks "
			+ "LEFT JOIN HCMDB.dbo.LFA1 LFA1 WITH(NOLOCK) on ZCBC.LIFNR = LFA1.LIFNR "
			+ "WHERE VEKP.EXIDV in(";

	String OBTENER_HUS_CARRIL = "select distinct(lenum),matnr from HCMDB.dbo.LQUA WITH(NOLOCK) where LGNUM = ? and LGTYP = ? and LGPLA = ? and matnr =? and SKZUA is null";

	String OBTENER_HU_EXTERNA = "select ZHUEX from HCMDB.dbo.ZPAITT_HU_EXT WITH(NOLOCK) where ZEXIDV = ? ";

	String VALIDAR_HU_LQUA = "select WERKS,BESTQ,LGORT,LGNUM,LGTYP,LGPLA,MATNR,VERME,MEINS,SKZUA  from HCMDB.dbo.lqua WITH(NOLOCK) where lenum=? and werks = ?";

	String VALIDAR_HU_VKP = "select VEKP.werks, VEPO.BESTQ, VEPO.matnr, VEPO.vemng, VEPO.vemeh, VEKP.hu_lgort from HCMDB.dbo.VEPO with(nolock) inner join HCMDB.dbo.VEKP with(nolock) on VEPO.VENUM = VEKP.VENUM where VEKP.WERKS = ? and VEKP.EXIDV = ? and VEPO.VELIN = '1'";

	public HusDTO obtieneHusVEKP(HuDTO huDTO) {

		HusDTO husDTO = new HusDTO();
		ResultDTO result = new ResultDTO();
		HuDTOItem items = new HuDTOItem();
		List<HuDTO> itemList = new ArrayList<HuDTO>();

		items.setItem(itemList);
		husDTO.setItems(items);

		Connection con = DBConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(OBTENER_STATUS_HUS_VEKP);
			stmn.setString(1, huDTO.getVblen());

			rs = stmn.executeQuery();
			while (rs.next()) {

				HuDTO huDTOItem = new HuDTO();

				huDTOItem.setHu(rs.getString("EXIDV"));
				huDTOItem.setStatus(rs.getString("Status"));
				huDTOItem.setStatusVEKP(rs.getString("LGNUM"));

				itemList.add(huDTOItem);

			}

			if (itemList.size() > 0) {
				result.setId(1);
				result.setMsg("Se recuperaron HU´s de entrega entrante");
			} else {
				result.setId(0);
				result
						.setMsg("No fue posible recuperar HU´s de entrega entrante");
			}

			items.setItem(itemList);
			husDTO.setItems(items);

		} catch (SQLException e) {
			LOCATION.error("Error: " + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("Error: " + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Error: " + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		husDTO.setResultDT(result);
		return husDTO;
	}

	public HusDTO obtieneHusBCPS(HuDTO huDTO) {

		HusDTO husDTO = new HusDTO();
		ResultDTO result = new ResultDTO();
		HuDTOItem items = new HuDTOItem();
		List<HuDTO> itemList = new ArrayList<HuDTO>();

		items.setItem(itemList);
		husDTO.setItems(items);

		Connection con = DBConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;
		LOCATION.error("obtieneHusBCPS");
		try {
			stmn = con.prepareStatement(OBTENER_STATUS_HUS_BCPS);
			stmn.setString(1, huDTO.getVblen());

			rs = stmn.executeQuery();
			while (rs.next()) {

				HuDTO huDTOItem = new HuDTO();

				huDTOItem.setHu(rs.getString("hu"));
				huDTOItem.setStatus(rs.getString("Status"));
				huDTOItem.setStatusVEKP(rs.getString("Status"));

				itemList.add(huDTOItem);

			}

			if (itemList.size() > 0) {
				result.setId(1);
				result.setMsg("Se recuperaron HU´s de entrega entrante");
			} else {
				result.setId(0);
				result
						.setMsg("No fue posible recuperar HU´s de entrega entrante");
			}

			items.setItem(itemList);
			husDTO.setItems(items);

		} catch (SQLException e) {
			LOCATION.error("Error: " + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("Error: " + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Error: " + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		husDTO.setResultDT(result);
		return husDTO;
	}

	public Etiquetas obtieneDatosHusVekp(String hus) {

		ResultDTO resultDT = new ResultDTO();

		Connection con = DBConnection.createConnection();

		ResultSet rs = null;

		Etiquetas etiquetas = new Etiquetas();
		List<Etiqueta> listaEtiquetas = new ArrayList<Etiqueta>();

		try {

			OBTENER_DATOS_HUS_VEKP += hus + ")";

			Statement statement = con.createStatement();

			String HU = "VACIO";

			rs = statement.executeQuery(OBTENER_DATOS_HUS_VEKP);

			while (rs.next()) {

				HU = rs.getString("HU");
				Etiqueta e = new Etiqueta();
				e.setEXIDV_HU(HU);
				e.setFechaImpr(rs.getString("FECHA"));
				e.setBarCode(HU + "," + Utils.zeroClean(rs.getString("TARIMA"))
						+ "," + Utils.zeroClean(rs.getString("MATERIAL")) + ","
						+ rs.getString("VEMNG") + "," + rs.getString("VEMEH")
						+ "," + rs.getString("CHARG") + ","
						+ rs.getString("FECHA2"));
				e.setCHARG4(rs.getString("CHARG"));
				e.setMATNR(Utils.zeroClean(rs.getString("MATERIAL")));
				e.setMAKTX_desc(rs.getString("DESCRIPCION"));
				e.setVEMEH(rs.getString("VEMEH"));
				e.setVEMNG(rs.getString("VEMNG"));
				e.setWERKS(rs.getString("CENTRO"));
				e.setNAME1(rs.getString("DescPlanta"));
				listaEtiquetas.add(e);

			}

			if (listaEtiquetas.size() > 0) {
				resultDT.setId(1);
				resultDT.setMsg("HU's recuperadas");
			} else {
				resultDT.setId(2);
				resultDT.setMsg("No fue posible recuperar HU's");
			}

			etiquetas.setItems(listaEtiquetas);
			etiquetas.setResultDT(resultDT);

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
			LOCATION.error("SQLException in obtieneHusParaImprimir:"
					+ e.toString());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
			LOCATION.error("Exception in obtieneHusParaImprimir:"
					+ en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
				LOCATION.error("Exception in obtieneHusParaImprimir:"
						+ e.toString());
			}
		}

		return etiquetas;

	}

	public HusDTO obtieneHusZPicking(HuDTO huDTO) {

		HusDTO husDTO = new HusDTO();
		ResultDTO result = new ResultDTO();
		HuDTOItem items = new HuDTOItem();
		List<HuDTO> itemList = new ArrayList<HuDTO>();

		items.setItem(itemList);
		husDTO.setItems(items);

		Connection con = DBConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(OBTENER_STATUS_HUS_ZPICKING_ENTREGA);
			stmn.setString(1, huDTO.getVblen());
			stmn.setString(2, huDTO.getId());

			rs = stmn.executeQuery();
			while (rs.next()) {

				HuDTO huDTOItem = new HuDTO();

				huDTOItem.setHu(rs.getString("EXIDV"));
				huDTOItem.setStatus(rs.getString("Status"));

				itemList.add(huDTOItem);

			}

			result.setId(1);
			result.setMsg("Se recuperaron HU´s de Zpicking");

			items.setItem(itemList);
			husDTO.setItems(items);

		} catch (SQLException e) {
			LOCATION.error("Error: " + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("Error: " + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Error: " + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		husDTO.setResultDT(result);
		return husDTO;
	}

	public HusDTO obtieneHusZPickingVidrio(HuDTO huDTO) {

		HusDTO husDTO = new HusDTO();
		ResultDTO result = new ResultDTO();
		HuDTOItem items = new HuDTOItem();
		List<HuDTO> itemList = new ArrayList<HuDTO>();

		items.setItem(itemList);
		husDTO.setItems(items);

		Connection con = DBConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(OBTENER_STATUS_HUS_ZPICKING_VIDRIO);
			stmn.setString(1, huDTO.getVblen());
			stmn.setString(2, huDTO.getId());

			rs = stmn.executeQuery();
			while (rs.next()) {

				HuDTO huDTOItem = new HuDTO();

				huDTOItem.setHu(rs.getString("EXIDV"));
				huDTOItem.setStatus(rs.getString("Status"));

				itemList.add(huDTOItem);

			}

			result.setId(1);
			result.setMsg("Se recuperaron HU´s de Zpicking");

			items.setItem(itemList);
			husDTO.setItems(items);

		} catch (SQLException e) {
			LOCATION.error("Error: " + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("Error: " + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Error: " + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		husDTO.setResultDT(result);
		return husDTO;
	}

	public Etiquetas obtieneDatosHusLqua(String hus) {

		ResultDTO resultDT = new ResultDTO();

		Connection con = DBConnection.createConnection();

		ResultSet rs = null;

		Etiquetas etiquetas = new Etiquetas();
		List<Etiqueta> listaEtiquetas = new ArrayList<Etiqueta>();

		try {

			OBTENER_DATOS_HUS_LQUA += hus + ")";

			// LOCATION.error("QUERY: " + OBTENER_DATOS_HUS_ZPICKING_ENTREGA);

			Statement statement = con.createStatement();

			String HU = "VACIO";

			rs = statement.executeQuery(OBTENER_DATOS_HUS_LQUA);

			while (rs.next()) {

				HU = rs.getString("HU");
				Etiqueta e = new Etiqueta();
				e.setEXIDV_HU(HU);
				e.setFechaImpr(rs.getString("FECHA"));
				e.setBarCode(HU + ","
						+ Utils.zeroClean(rs.getString("MATERIAL")) + ","
						+ rs.getString("VEMNG") + "," + rs.getString("VEMEH")
						+ "," + rs.getString("CHARG") + ","
						+ rs.getString("FECHA2"));
				e.setCHARG4(rs.getString("CHARG"));
				e.setMATNR(Utils.zeroClean(rs.getString("MATERIAL")));
				e.setMAKTX_desc(rs.getString("MAKTX"));
				e.setVEMEH(rs.getString("VEMEH"));
				e.setVEMNG(rs.getString("VEMNG"));
				e.setWERKS(rs.getString("CENTRO"));
				e.setNAME1(rs.getString("DescPlanta"));
				listaEtiquetas.add(e);

			}

			if (listaEtiquetas.size() > 0) {
				resultDT.setId(1);
				resultDT.setMsg("HU's recuperadas");
			} else {
				resultDT.setId(2);
				resultDT.setMsg("No fue posible recuperar HU's");
			}

			etiquetas.setItems(listaEtiquetas);
			etiquetas.setResultDT(resultDT);

		} catch (SQLException e) {
			resultDT.setId(2);
			resultDT.setMsg(e.getMessage());
			LOCATION.error("SQLException in obtieneHusParaImprimir:"
					+ e.toString());
		} catch (Exception en) {
			resultDT.setId(2);
			resultDT.setMsg(en.getMessage());
			LOCATION.error("Exception in obtieneHusParaImprimir:"
					+ en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				resultDT.setId(2);
				resultDT.setMsg(e.getMessage());
				LOCATION.error("Exception in obtieneHusParaImprimir:"
						+ e.toString());
			}
		}

		return etiquetas;

	}

	public HusDTO obtieneHusCarrilPorMaterial(HuDTO huDTO) {

		HusDTO husDTO = new HusDTO();
		ResultDTO result = new ResultDTO();
		HuDTOItem items = new HuDTOItem();
		List<HuDTO> itemList = new ArrayList<HuDTO>();

		items.setItem(itemList);
		husDTO.setItems(items);

		Connection con = DBConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(OBTENER_HUS_CARRIL);
			stmn.setString(1, huDTO.getLgnum());
			stmn.setString(2, huDTO.getLgtyp());
			stmn.setString(3, huDTO.getLgpla());
			stmn.setString(4, huDTO.getMatnr());

			rs = stmn.executeQuery();
			while (rs.next()) {

				HuDTO huDTOItem = new HuDTO();

				huDTOItem.setHu(rs.getString("lenum"));
				huDTOItem.setStatus("En Stock");

				itemList.add(huDTOItem);

			}

			result.setId(1);
			result.setMsg("Se recuperaron HU´s de entrega entrante");

			items.setItem(itemList);
			husDTO.setItems(items);

		} catch (SQLException e) {
			LOCATION.error("Error: " + e.toString());
			result.setId(2);
			result.setMsg(e.getMessage());
		} catch (Exception en) {
			LOCATION.error("Error: " + en.toString());
			result.setId(2);
			result.setMsg(en.getMessage());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Error: " + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		husDTO.setResultDT(result);
		return husDTO;
	}

	public ResultDTO obtieneHuExterna(String hu) {

		ResultDTO result = new ResultDTO();

		Connection con = DBConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(OBTENER_HU_EXTERNA);
			stmn.setString(1, hu);

			rs = stmn.executeQuery();
			if (rs.next()) {
				result.setId(1);
				result.setMsg(rs.getString("ZHUEX"));
			} else {
				result.setId(2);
				result
						.setMsg("No fue posible recuperar HU externa para HU interna: "
								+ hu);
			}

		} catch (SQLException e) {
			LOCATION.error("Error: " + e.toString());
			result.setId(2);
			result
					.setMsg("No fue posible recuperar HU externa para HU interna: "
							+ hu + " " + e.toString());
		} catch (Exception en) {
			LOCATION.error("Error: " + en.toString());
			result.setId(2);
			result
					.setMsg("No fue posible recuperar HU externa para HU interna: "
							+ hu + " " + en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Error: " + e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		return result;
	}

	public HusDTO validarHU(String hu, String werks) {

		HusDTO husDTO = new HusDTO();
		ResultDTO result = new ResultDTO();
		HuDTOItem items = new HuDTOItem();
		List<HuDTO> itemList = new ArrayList<HuDTO>();

		items.setItem(itemList);
		husDTO.setItems(items);

		Connection con = DBConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(VALIDAR_HU_LQUA);
			stmn.setString(1, hu);
			stmn.setString(2, werks);

			rs = stmn.executeQuery();
			if (rs.next()) {

				HuDTO huDTO = new HuDTO();

				huDTO.setWerks(rs.getString("werks"));
				huDTO.setBestq(rs.getString("bestq"));
				huDTO.setLgort(rs.getString("lgort"));
				huDTO.setLgnum(rs.getString("lgnum"));
				huDTO.setLgtyp(rs.getString("lgtyp"));
				huDTO.setLgpla(rs.getString("lgpla"));
				huDTO.setVerme(rs.getString("verme"));
				huDTO.setMeins(rs.getString("meins"));
				huDTO.setSkzua(rs.getString("skzua"));
				huDTO.setMatnr(rs.getString("matnr"));

				itemList.add(huDTO);
				result.setId(1);
				result.setMsg("HU existe");

			} else {
				result.setId(2);
				result.setMsg("No existe el HU para el centro:" + werks);
			}

		} catch (SQLException e) {
			LOCATION.error("SQLException: " + e.toString());
			result.setId(2);
			result.setMsg("No fue posible recuperar status de HU: " + hu + " "
					+ e.toString());
		} catch (Exception en) {
			LOCATION.error("Exception : " + en.toString());
			result.setId(2);
			result.setMsg("No fue posible recuperar status de HU: " + hu + " "
					+ en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION
						.error("Finally Exception -> No fue posible recuperar status de HU: "
								+ e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		items.setItem(itemList);
		husDTO.setItems(items);

		husDTO.setResultDT(result);
		return husDTO;
	}

	public HusDTO validarHUVidrio(String exidv, String werks) {

		HusDTO husDTO = new HusDTO();
		ResultDTO result = new ResultDTO();
		HuDTOItem items = new HuDTOItem();
		List<HuDTO> itemList = new ArrayList<HuDTO>();

		items.setItem(itemList);
		husDTO.setItems(items);

		Connection con = DBConnection.createConnection();

		PreparedStatement stmn = null;
		ResultSet rs = null;

		try {
			stmn = con.prepareStatement(VALIDAR_HU_VKP);
			LOCATION.error("Validar HU Vidrio WERKS: " + werks + " EXIDV: "
					+ exidv);
			stmn.setString(1, werks);
			stmn.setString(2, exidv);

			rs = stmn.executeQuery();
			if (rs.next()) {

				HuDTO huDTO = new HuDTO();

				huDTO.setWerks(rs.getString("werks"));
				huDTO.setBestq(rs.getString("bestq"));
				huDTO.setVerme(rs.getString("vemng"));
				huDTO.setMeins(rs.getString("vemeh"));
				huDTO.setSkzua(rs.getString("hu_lgort"));
				huDTO.setMatnr(rs.getString("matnr"));

				itemList.add(huDTO);
				result.setId(1);
				result.setMsg("HU existe");

			} else {
				result.setId(2);
				result.setMsg("No existe el HU para el centro:" + werks);
			}

		} catch (SQLException e) {
			LOCATION.error("SQLException: " + e.toString());
			result.setId(2);
			result.setMsg("No fue posible recuperar status de HU: " + exidv
					+ " " + e.toString());
		} catch (Exception en) {
			LOCATION.error("Exception : " + en.toString());
			result.setId(2);
			result.setMsg("No fue posible recuperar status de HU: " + exidv
					+ " " + en.toString());
		} finally {
			try {
				DBConnection.closeConnection(con);
			} catch (Exception e) {
				LOCATION
						.error("Finally Exception -> No fue posible recuperar status de HU: "
								+ e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}

		items.setItem(itemList);
		husDTO.setItems(items);

		husDTO.setResultDT(result);
		return husDTO;
	}
}
