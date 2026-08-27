package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpPTTSettingPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnUniquePTTTemplateNameValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnUniquePTTTemplateNameValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName,"Entry  KnUniquePTTTemplateNameValidationRule");
        if (persistDTO instanceof KnCorpPTTSettingPersistDTO) {
            KnCorpPTTSettingPersistDTO dto = (KnCorpPTTSettingPersistDTO) persistDTO;
            String dbPTTSettingName = dto.getDbPTTSettingName();
            String reqPTTSettingName = dto.getReqPTTSettingName();
            knLogger.debug(methodName,"Profile name in DB ", dbPTTSettingName);
            knLogger.debug(methodName,"Profile name in req ", reqPTTSettingName);

            if (reqPTTSettingName != null && dbPTTSettingName != null) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.UNIQUE_PTT_SETTING_NAME,
                        "PTTSettingName exists in db", getEntityId(), getOperationType(), getRuleId(), reqPTTSettingName, "");
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
