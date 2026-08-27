package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.commdto.common.KnPTTSettingDocInfoDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpPTTSettingPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.List;

public class KnPTTemplateIdValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnPTTemplateIdValidationRule.class);
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpPTTSettingPersistDTO) {
            KnCorpPTTSettingPersistDTO dto = (KnCorpPTTSettingPersistDTO) persistDTO;
            List<String> reqPTTdocIds = dto.getReqPTTdocIds();
            List<KnPTTSettingDocInfoDTO> pttSettingDTOList =  dto.getPttSettingDTOList();

            knLogger.debug("Profile name in DB ", pttSettingDTOList);
            knLogger.debug("Profile name in req ", reqPTTdocIds);

            if (reqPTTdocIds != null && !reqPTTdocIds.isEmpty() && pttSettingDTOList != null && !pttSettingDTOList.isEmpty()) {
                List<String> dbDocIds = pttSettingDTOList.stream()
                        .map(KnPTTSettingDocInfoDTO::getDocId)
                        .toList();
                for (String reqDocId : reqPTTdocIds) {
                    if (!dbDocIds.contains(reqDocId)) {
                        throw new KnCorpBOValidationException(
                                KnErrorCodes.Validator.PTT_SETTING_DOC_NOT_VALID,
                                "Requested PTT docId " + reqDocId + " not found in DB",
                                getEntityId(), getOperationType(), getRuleId(), reqDocId, ""
                        );
                    }
                }
            }
        } else {
            knLogger.error("validate()", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpPTTSettingPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
