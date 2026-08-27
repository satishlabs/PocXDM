/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMdnListPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

/**
 * This validation Rule will verify if the subscribers count is matching the limit required
 */
public class KnSubscriberCountValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubscriberCountValidationRule.class);
    private final String CLASS = KnSubscriberCountValidationRule.class.getName();

    public void validate() throws KnValidationException {
        IPersistenceDTO persistDTO = getDTO();
        String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY : Validating Activation code");
        KnCorpMdnListPersistDTO corpMdnPersistDTO;
        if (persistDTO instanceof KnCorpMdnListPersistDTO) {
            corpMdnPersistDTO = (KnCorpMdnListPersistDTO) persistDTO;
            if (corpMdnPersistDTO.getMdnList() != null && corpMdnPersistDTO.getMdnList().size() <= 0) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.HANDSET_SUBSCRIBERS_MISSING,
                        "The handset guys are not there in the corporation.", getEntityId(), getOperationType(), getRuleId(),
                        "DataType", "");
            }
            knLogger.debug("validate", "KnCorpActivationPersistDTO Validated successfully.");
        }
    }
}
