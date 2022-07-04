package com.bmore.hyperius.web.repository;

import com.bmore.hyperius.web.dto.HuDTO;
import com.bmore.hyperius.web.dto.HusDTO;
import com.bmore.hyperius.web.dto.ResultDTO;
import com.bmore.hyperius.web.utils.print.Etiquetas;

public interface HUsRepository {
	
	public static final String RST_VALUE = "RST_";

	public Etiquetas obtieneDatosHusLqua(String hus);

	public Etiquetas obtieneDatosHusVekp(String hus);

	public ResultDTO obtieneHuExterna(String hu);

	public HusDTO obtieneHusBCPS(HuDTO huDTO);

	public HusDTO obtieneHusCarrilPorMaterial(HuDTO huDTO);

	public HusDTO obtieneHusVEKP(HuDTO huDTO);

	public HusDTO obtieneHusZPicking(HuDTO huDTO);

	public HusDTO obtieneHusZPickingVidrio(HuDTO huDTO);

	public HusDTO validarHU(String hu, String werks);

	public HusDTO validarHUVidrio(String exidv, String werks);
}
