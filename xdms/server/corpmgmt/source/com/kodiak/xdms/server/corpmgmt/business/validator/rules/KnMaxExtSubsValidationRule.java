/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import java.util.Arrays;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

/**
 * *****************************************************************************
 * File name:   KnMaxExtSubsValidationRule.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * ChandraShekar H S       10/4/14      7.8.1
 * <p/>
 * <p/>
 *
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * *******************************************************************************
 */

public class KnMaxExtSubsValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxExtSubsValidationRule.class);


    public  void validate()throws KnValidationException
    {
        final String methodName = "validate()";
        try{
            IPersistenceDTO persistDTO = getDTO();
            knLogger.debug( methodName, "ENTRY : Validating Max External Subscribers Validation Rule");
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactDTO = (KnContactDetailsPersistDTO) persistDTO;
                int maxExtSubsAllowed = contactDTO.getMaxExtSubs();
                int currentExtSubsCount = contactDTO.getCurrentExtSubsCount();
                int newExtSubsCount = contactDTO.getExtSubsList().size();
                if ((currentExtSubsCount + newExtSubsCount) > maxExtSubsAllowed){
                	knLogger.error("Ext Subscriber count for the corporation exceeds max limit configured...");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.EXTERNAL_SUBS_COUNT_EXCEEDED,
                            "Ext Subscriber count for the corporation exceeds max limit configured.",getEntityId(), getOperationType(), getRuleId(), Arrays.asList(maxExtSubsAllowed).toString(), "");
                }
            }
            else{
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Invalid Persist DTO passed.",
                        getRuleId(), KnConstants.KEY_DATATYPE_MAX_EXT_SUBS_LIMIT);
            }
        }finally {
            knLogger.debug( methodName, "Exit Point: Max Exteranl Subscriber Validated successfully.");
        }
    }
}
