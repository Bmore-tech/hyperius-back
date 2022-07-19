package com.bmore.hyperius.mobile.repository;

import com.bmore.hyperius.mobile.dto.HuDTO;
import com.bmore.hyperius.mobile.dto.HusDTO;

public interface HUsMobileRepository {

  public HuDTO validarHU(String hu);

  public HusDTO validarHUVidrio(String exidv, String werks);

}
