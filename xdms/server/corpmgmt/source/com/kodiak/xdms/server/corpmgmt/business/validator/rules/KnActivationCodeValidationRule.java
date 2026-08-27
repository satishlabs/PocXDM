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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpActivationDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Calendar;
/**
 * Created by IntelliJ IDEA.
 * User: kodiak
 * Date: 30/5/13
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnActivationCodeValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnActivationCodeValidationRule.class);
    private static final long serialVersionUID = 7526471155622676325L;
    private final String CLASS = KnActivationCodeValidationRule.class.getName();

    /**
     * This method implements the actual logic for validation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        IPersistenceDTO persistDTO = getDTO();
        String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY : Validating Activation code");
        KnCorpActivationPersistDTO corpActPersistDTO;
        if (persistDTO instanceof KnCorpActivationPersistDTO) {
            corpActPersistDTO = (KnCorpActivationPersistDTO) persistDTO;
            KnIPCorpActivationDTO inputDto=(KnIPCorpActivationDTO) corpActPersistDTO.getInputDTO();
            Calendar calendar = Calendar.getInstance();
            long currentMilliSeconds = calendar.getTimeInMillis();
            if (corpActPersistDTO.getActivationCode() == null) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.ACTIVATION_CODE_IS_INVALID,
                        "Activation code is null.", getEntityId(), getOperationType(), getRuleId(),
                        inputDto.getMdnList().toString(), "");
            }
            if (corpActPersistDTO.getActivationCode() != null) {
                long expTime = corpActPersistDTO.getExpTimeStamp().getTime();
                if (currentMilliSeconds > expTime) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.ACTIVATION_CODE_IS_INVALID,
                            "Activation code is expired.", getEntityId(), getOperationType(), getRuleId(),
                            inputDto.getMdnList().toString(), "");
                }
            } else {
                knLogger.error( "validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnCorpActivationPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
            knLogger.debug( "validate", "KnCorpActivationPersistDTO Validated successfully.");

        }
    }
}
