/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnTGSSListPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import static com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes.Validator.FEATUREBIT_IS_DISABLED;

/**
 Created by venkata sudhakar talluri on 10-01-2019
 */

//validate the bit #48(multi simultaneous session) is enabled in activeFS
public class KnTGSSFeatureValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnTGSSFeatureValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY : Validating Feature Bit 48 enabled for multi simultaneous session in active FS" + persistDTO);
        try {
            if (persistDTO instanceof KnTGSSListPersistDTO) {
                KnTGSSListPersistDTO tgssListPersistDTO = (KnTGSSListPersistDTO) persistDTO;

                boolean msSessionBit = KnGeneralUtil.getFeatureBitValue(tgssListPersistDTO.getFeatureSet2(),
                               com.kodiak.common.resources.KnConstants.FEATURE_SET.MULTISIMULTANEOUSSESSION.value());
                if (!msSessionBit) {
                    knLogger.debug(methodName, "mdn doesn't have Simultainous Session permission" + KnGDPRTemplate.mdn(tgssListPersistDTO.getMdn()));
                    throw new KnValidationException(FEATUREBIT_IS_DISABLED, "mdn doesn't have Simultainous Session permission" + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN", tgssListPersistDTO.getMdn());
                }
            } else {
                throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN", "");

            }
        } finally {
            knLogger.debug(methodName, "Exit Point");
        }


    }
}

