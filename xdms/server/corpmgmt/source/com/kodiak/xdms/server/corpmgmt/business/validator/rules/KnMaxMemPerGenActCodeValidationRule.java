/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * Created by IntelliJ IDEA.
 * User: kodiak
 * Date: 28/5/13
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */

package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import java.util.Arrays;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGenActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnMaxMemPerGenActCodeValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMaxMemPerGenActCodeValidationRule.class);
    private static final long serialVersionUID = 7526471155622676327L;
    private final String CLASS = KnMaxMemPerGenActCodeValidationRule.class.getName();

    /**
     * This method implements the actual logic for validation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        IPersistenceDTO persistDTO = getDTO();
        String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY : Validating if total members exceeds thresh count exceeds limit");
        KnCorpGenActivationPersistDTO corpActPersistDTO;
        if (persistDTO instanceof KnCorpGenActivationPersistDTO) {
            corpActPersistDTO = (KnCorpGenActivationPersistDTO) persistDTO;
            if (corpActPersistDTO.getSubsCount() > corpActPersistDTO.getMaxMembersAllowed()) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.REQUEST_THRESHOLD_LIMIT_REACHED,
                        "Threshold limit reached.", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(corpActPersistDTO.getMaxMembersAllowed()).toString(), "");
            }
        } else {
            knLogger.error( "validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGenActivationPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug( "validate", "KnMaxMemPerGenActCodeValidationRule Validated successfully.");

    }
}
