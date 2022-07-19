package com.bmore.hyperius.web.repository;

import java.util.List;

import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLCreateDTO;
import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLCustomDTO;
import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLDetailsDTO;
import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLFTPUserDTO;
import com.bmore.hyperius.web.utils.export.xmlgeneration.XMLTotalDTO;

public interface XMLCreateRepository {

  public XMLCreateDTO fillXML(String VBELN, String WERKS, XMLCreateDTO createDTO);

  public XMLTotalDTO fillTotal(String VBELN);

  public List<XMLDetailsDTO> fillDetail(String VBELN);

  public XMLCustomDTO fillCustom(XMLCreateDTO createDTO, String VBELN, String Werks);

  // previously private
  public String getFolioFact(String vBeln, String werks);

  public XMLFTPUserDTO userAccess();

  public Integer insertValueXML(String vBeln, String UUID, String FolioExt, String FolioInt);
}
