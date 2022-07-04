package com.bmore.prueba.mobile.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.prueba.config.DBConnectionMob;
import com.bmore.prueba.mobile.dto.HuDTO;
import com.bmore.prueba.mobile.dto.HuDTOItem;
import com.bmore.prueba.mobile.dto.HusDTO;
import com.bmore.prueba.mobile.utils.ResultDT;

public class HUsDAO {
	private static final Logger LOCATION = LoggerFactory.getLogger(HUsDAO.class);
	String VALIDAR_HU_LQUA = "select WERKS,BESTQ,LGORT,LGNUM,LGTYP,LGPLA,MATNR,VERME,MEINS,SKZUA  from HCMDB.dbo.lqua WITH(NOLOCK) where lenum=?";
	String VALIDAR_HU_VKP = "select VEKP.werks, VEPO.BESTQ, VEPO.matnr, VEPO.vemng, VEPO.vemeh, VEKP.hu_lgort from HCMDB.dbo.VEPO with(nolock) "
			+ "inner join HCMDB.dbo.VEKP with(nolock) on VEPO.VENUM = VEKP.VENUM where VEKP.WERKS = ? and VEKP.EXIDV = ? and VEPO.VELIN = '1'";
	public HuDTO validarHU(String hu) throws ClassNotFoundException{
		ResultDT result = new ResultDT();
		HuDTO huDTO = new HuDTO();
		Connection con = DBConnectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(VALIDAR_HU_LQUA);
			stmn.setString(1, hu);
			rs = stmn.executeQuery();
			if (rs.next()) {
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
				result.setId(1);
				result.setMsg("HU existe");
			} else {
				result.setId(2);
				result.setMsg("No existe el HU");
			}
		} catch (SQLException e) {
			LOCATION.error("SQLException: " + e.toString());
			result.setId(2);
			result.setMsg("No fue posible recuperar status de HU: " + hu + " "+ e.toString());
		} catch (Exception en) {
			LOCATION.error("Exception : " + en.toString());
			result.setId(2);
			result.setMsg("No fue posible recuperar status de HU: " + hu + " " + en.toString());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Finally Exception -> No fue posible recuperar status de HU: "+ e.toString());
				result.setId(2);
				result.setMsg(e.getMessage());
			}
		}
		huDTO.setResultDT(result);
		return huDTO;
	}
	public HusDTO validarHUVidrio(String exidv, String werks) throws ClassNotFoundException{
		HusDTO husDTO = new HusDTO();
		ResultDT result = new ResultDT();
		HuDTOItem items = new HuDTOItem();
		List<HuDTO> itemList = new ArrayList<HuDTO>();
		items.setItem(itemList);
		husDTO.setItems(items);
		Connection con = DBConnectionMob.createConnection();
		PreparedStatement stmn = null;
		ResultSet rs = null;
		try {
			stmn = con.prepareStatement(VALIDAR_HU_VKP);
			LOCATION.error("Validar HU Vidrio WERKS: " + werks + " EXIDV: " + exidv);
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
			result.setMsg("No fue posible recuperar status de HU: " + exidv	+ " " + e.toString());
		} catch (Exception en) {
			LOCATION.error("Exception : " + en.toString());
			result.setId(2);
			result.setMsg("No fue posible recuperar status de HU: " + exidv	+ " " + en.toString());
		} finally {
			try {
				DBConnectionMob.closeConnection(con);
			} catch (Exception e) {
				LOCATION.error("Finally Exception -> No fue posible recuperar status de HU: " + e.toString());
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