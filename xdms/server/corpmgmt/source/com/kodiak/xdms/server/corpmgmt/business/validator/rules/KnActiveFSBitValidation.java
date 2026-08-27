/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                 Date       Release
 * -------------------- ------------ -------------------------------------
 * Gunjan Kumar         03/06/14     7.9
 * <p/>
 * <p/>
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
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import java.util.Arrays;

import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.resources.KnConstants;

import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.CREATE_SUBS_ATG_SCAN_LIST;

public class KnActiveFSBitValidation extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnValidatorRule.class);
    private static final long serialVersionUID = 7526471155622234233L;
    private static final String BIT_NUMBER = "bitNumber";

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        String bitNumberStr = getAttribute(BIT_NUMBER);
        String[] bitNumberStrArray = bitNumberStr.split(DELIM);
        int bitNumber = Integer.parseInt(bitNumberStrArray[0]);
        String activeFS;
        boolean bit;
        boolean priorityExists = false;
        String operationType = null;
        if (persistDTO instanceof KnCorpTGSPersistDTO) {
            KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            activeFS = tgsPersistDTO.getActiveFS2();
            bit = KnGeneralUtil.getFeatureBitValue(activeFS, bitNumber);
            knLogger.debug(methodName,"First active fs and bit no ",activeFS,bitNumber);
            operationType = tgsPersistDTO.getOperationType();
            priorityExists = tgsPersistDTO.isPriorityExists();
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(), ", Expected Dto - KnCorpTGSPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - "
                    + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        if(priorityExists || !CREATE_SUBS_ATG_SCAN_LIST.equals(operationType)){
            switch (bitNumber) {
                case KnConstants.SUBS_TGS_SERVER_BIT:
                case KnConstants.SUBS_TGS_CLIENT_BIT:
                    knLogger.debug(methodName,"second active fs and bit no ",activeFS,bitNumber);
                    if (bit == false) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBS_TGS_SERVER_BIT_DISABLE,
                                "Subscribers TGS feature bit is disabled", getEntityId(), getOperationType(), getRuleId(), Arrays.asList(0).toString(), "");
                    }
                    break;
                case KnConstants.SUBS_TGSC_CLIENT_BIT:
                    knLogger.debug(methodName,"third active fs and bit no ",activeFS,bitNumber);
                    Boolean  pttRadiobit = KnGeneralUtil.getFeatureBitValue(activeFS, KnConstants.SUBS_PTTRADIOCLIENT_SERVER_BIT);
                    if (!bit  && !pttRadiobit) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBS_TGSC_CLIENT_BIT_DISABLE,
                                "Subscribers TGSc feature bit is disabled", getEntityId(), getOperationType(), getRuleId(),  Arrays.asList(0).toString(), "");
                    }
            }
        }
        knLogger.debug(methodName, "Validation Completed Successfully");
    }
}
