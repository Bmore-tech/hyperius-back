package com.bmore.hyperius.web.repository;

import com.bmore.hyperius.web.utils.export.report.EDIGenerationDTO_Sec_A;
import com.bmore.hyperius.web.utils.export.report.EDIGenerationDTO_Sec_B;

public interface EDIGenerationRepository {
  EDIGenerationDTO_Sec_A Exportacion_EDI_A(String VBELN);

  EDIGenerationDTO_Sec_B Exportacion_EDI_B(String VBELN);

  EDIGenerationDTO_Sec_B Exportacion_EDI_C(String VBELN);

  EDIGenerationDTO_Sec_B Exportacion_EDI_D(String VBELN);

  EDIGenerationDTO_Sec_B Exportacion_EDI_E(String VBELN);

  EDIGenerationDTO_Sec_B Exportacion_EDI_F(String VBELN);

  EDIGenerationDTO_Sec_B Exportacion_EDI_G(String VBELN);

  EDIGenerationDTO_Sec_B Exportacion_EDI_H(String VBELN);
}
