package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnCorpGroupAuthorizedLargeTGValidator extends KnValidatorRule {
    /**
     * 0 - Not accepted for Large Group
     * 1 - Accepted for Large Group
     */
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupAuthorizedLargeTGValidator.class);
    @Override
    public void validate() throws KnValidationException, KnBOException {

        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( "validate", "ENTRY : Validating if large talk group conversion is allowed ",
                "as per authorizedLargeTG flag. ");
        KnCorpGroupInfoPersistDTO groupPersistDTO;
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
        } else {
            knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                    ", Expected Dto - KnCLGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "", "");//KnConstants.KEY_DATATYPE_MDN, "");
        }

        boolean isLargeGroup = groupPersistDTO.isLargeGroup();
        Integer isAuthorizedLargeTG = groupPersistDTO.getAuthorizedLargeTG();
        knLogger.debug( "validate", "isLargeGroup- ",isLargeGroup,"isAuthorizedLargeTG- ",isAuthorizedLargeTG);
        if (isLargeGroup && (isAuthorizedLargeTG == null || isAuthorizedLargeTG == 0)) {
            knLogger.error( "validate", "Large Group conversion is not allowed" );
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.AUTHORIZED_LARGE_TG_FEATURE_DISABLED,
                    "Large Group conversion is not allowed",
                    getEntityId(), getOperationType(), getRuleId(), "", "");
        }
    }
}
