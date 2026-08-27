/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupprofile;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

/**
 * @author venkata.talluri
 * Release : 10.0.1
 *
 * Mcx Group indicator is not allowed by changes via modifyGroupProfile operation if mcxGroupInd is changed in modifyGroupProfile then we are rejecting
 * request with appropriate error
 *
 */
public class KnGrpProfileMcxGrpValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnGrpProfileMcxGrpValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupProfilePersistDTO) {
            KnCorpGroupProfilePersistDTO grpPersistDTO = (KnCorpGroupProfilePersistDTO) persistDTO;
            knLogger.debug(methodName, "isMcxGrpIndChanged -", grpPersistDTO.isMcxGrpIndChanged(),"isGrpTypeChanged - ",grpPersistDTO.isGrpTypeChanged());
            if (grpPersistDTO.isMcxGrpIndChanged() || grpPersistDTO.isGrpTypeChanged()) {
                knLogger.error(methodName, "Mcx Grp Indicator/Grp Type is not allowed by changed ", grpPersistDTO.isMcxGrpIndChanged());
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MODIFY_GRPPROFILE_DATA_NOT_ALLOWED,
                        "Mcx Grp Indicator/GrpType is not allowed by changed.", getEntityId(), getOperationType(), getRuleId(),
                        String.valueOf(grpPersistDTO.isMcxGrpIndChanged())+","+String.valueOf(grpPersistDTO.isGrpTypeChanged()), "");
            }
        }
        knLogger.debug(methodName, "Validation success.");
    }
}
