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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnConvergedClientPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * ************************************************************************
 * <p>
 * File name:  KnConvergedClientValidationRule.java
 * Subsystem:  PoCXDM
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Dec 15, 2016                8.1.2
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnConvergedClientValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnConvergedClientValidationRule.class);
    private static final String PTT_RADIO_CLIENT = "pttRadioClient";
    private static final String NON_PTT_RADIO_CLIENT = "nonPttRadioClient";
    private static final String CONV_CLIENT_ENEBLED = "allowedConvClientEnabled";
    private static final String CLIENT_CONFIG_ENEBLED = "allowedClientConfigEnabled";

    public void validate() throws KnValidationException {
        final String methodName = "validate() KnConvergedClientValidationRule";
        try {
            IPersistenceDTO persistDTO = getDTO();
            int allowedConvClientEnabled = Integer.valueOf(getAttribute(CONV_CLIENT_ENEBLED));
            int allowedClientConfigEnabled = Integer.valueOf(getAttribute(CLIENT_CONFIG_ENEBLED));
            knLogger.debug(methodName, "allowedConvCLientEnabled - allowedClientConfigEnabled -  ", allowedConvClientEnabled, allowedClientConfigEnabled);
            String pttAllowedType = getAttribute(PTT_RADIO_CLIENT);
            String[] pttAllowedTypes = pttAllowedType.split(DELIM);
            Collection<String> pttClientTypeList = Arrays.asList(pttAllowedTypes);
            String nonPttAllowedType = getAttribute(NON_PTT_RADIO_CLIENT);
            String[] nonPttAllowedTypes = nonPttAllowedType.split(DELIM);
            Collection<String> nonPttClientTypeList = Arrays.asList(nonPttAllowedTypes);
            if (persistDTO instanceof KnConvergedClientPersistDTO) {
                KnConvergedClientPersistDTO convergedClientPersistDTO = (KnConvergedClientPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", convergedClientPersistDTO);
                int convClientEnabled = convergedClientPersistDTO.getConvClientEnabled();
                int clientConfigEnabled = convergedClientPersistDTO.getClientConfigEnabled();
                String subsClientType = String.valueOf(convergedClientPersistDTO.getSubsClientType());
                if (pttClientTypeList.contains(subsClientType) && clientConfigEnabled != allowedClientConfigEnabled) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.CLIENT_TYPE_DISABLED,
                            "Client type is disabled", getEntityId(), getOperationType(), getRuleId(),
                            "DataType", "");
                }
                if (convClientEnabled != allowedConvClientEnabled) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_CONVERGED_CLIENT_FEATURE_DISABLED,
                            "System Level Converged Client Feature is disabled in DG.RTXENVVARIABLEINFO table", getEntityId(), getOperationType(), getRuleId(),
                            "DataType", "");
                } else {
                    Collection<String> allowedClientType = new ArrayList<>();
                    allowedClientType.addAll(pttClientTypeList);
                    allowedClientType.addAll(nonPttClientTypeList);
                    boolean enabledPttRadio = convergedClientPersistDTO.isEnabledPttRadio();
                    knLogger.debug(methodName, "allowedClientType and subsClientType and enabledPttRadio",
                            allowedClientType, subsClientType, enabledPttRadio);
                    if (!allowedClientType.contains(subsClientType)) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                                "Invalid Client Type because subscriberClientType neither belongs to PTT client " +
                                        "nor handset, crosscarrier, wifi", getEntityId(), getOperationType(), getRuleId(),
                                "DataType", "");
                    }
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(), ", Expected Dto - KnConvergedClientPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - "
                        + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
        } finally {
            knLogger.info(methodName, "EXIT: Validation Completed Successfully");
        }
    }
}