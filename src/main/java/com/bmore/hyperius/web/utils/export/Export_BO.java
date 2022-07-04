package com.bmore.hyperius.web.utils.export;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmore.hyperius.web.dto.ResultDTO;

public class Export_BO {

	private final Logger LOCATION = LoggerFactory.getLogger(getClass());
	
	public Export_DTO remisionFill(String VBELN) throws Export_Exception{
		Export_DTO exp_DTO = new  Export_DTO();
		Export_DAO exp_DAO =  new Export_DAO();
		exp_DTO.setFecha((new SimpleDateFormat("MMM dd yyyy").format(new Date()) ).toUpperCase());
		exp_DTO.setEntrega(VBELN);
		
		exp_DTO =  exp_DAO.getDataVBFA(exp_DTO);
		if(exp_DTO == null){
			LOCATION.error("Exception: Fail to load getDataVBFA");
		}
		exp_DTO =  exp_DAO.getDataVBAK(exp_DTO);
		if(exp_DTO == null){
			LOCATION.error("Exception: Fail to load getDataVBAK");
		}
		exp_DTO =  exp_DAO.getDataKNA1(exp_DTO, 1);
		if(exp_DTO == null){
			LOCATION.error("Exception: Fail to load getDataKNA1, Option: 1");
		}
		exp_DTO =  exp_DAO.getDataKNA1(exp_DTO, 2);
		if(exp_DTO == null){
			LOCATION.error("Exception: Fail to load getDataKNA1, Option: 2");
		}		
		
		exp_DTO = exp_DAO.getDataLIPS(exp_DTO);
		if(exp_DTO == null){
			LOCATION.error("Exception: Fail to load getDataLIPS");
		}
		//if(exp_DTO.getPrecioUnitario()!=null ){
		//	exp_DTO.setTotal(exp_DTO.getPrecioUnitario(), exp_DTO.getCantidad());
		//}
		
		exp_DTO = exp_DAO.getDataVTTK(exp_DTO);
		if(exp_DTO == null){
			LOCATION.error("Exception: Fail to load getDataVTTK");
		}
		
		exp_DTO = exp_DAO.getDataAduana(exp_DTO);
		if(exp_DTO == null){
			LOCATION.error("Exception: Fail to load getDataAduana");
		}		
		
		return exp_DTO;
	}
	
	public static ResultDTO remisionZcont(Export_DTO exDto){
		ResultDTO rDt = new ResultDTO();
		Export_DAO exp_DAO =  new Export_DAO();
		rDt = exp_DAO.zContExport(exDto);
		return rDt;
	}
	
	public static ResultDTO remisionExist(Export_DTO exDto){
		ResultDTO rDt = new ResultDTO();
		Export_DAO exp_DAO =  new Export_DAO();
		rDt = exp_DAO.zConRemExist(exDto);
		return rDt;
	}
	
	public static ResultDTO remisionXMlExist(Export_DTO exDto){
		ResultDTO rDt = new ResultDTO();
		Export_DAO exp_DAO =  new Export_DAO();
		rDt = exp_DAO.zConXmlExist(exDto);
		return rDt;
	}
	
	public static Export_DTO remisionData(Export_DTO exDto){
		Export_DAO exp_DAO =  new Export_DAO();
		exDto = exp_DAO.zConRemData(exDto);
		return exDto;
	}

}
