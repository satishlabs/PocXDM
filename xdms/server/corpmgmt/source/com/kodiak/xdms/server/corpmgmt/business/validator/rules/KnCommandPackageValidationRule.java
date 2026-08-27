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

import java.util.Arrays;

/**
 * ************************************************************************
 * <p>
 * File name:  KnCommandPackageValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Nov 2, 2018                9.0
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


public class KnCommandPackageValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCommandPackageValidationRule.class);
    private static final int ENABLED = 1;

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnConvergedClientPersistDTO) {
            KnConvergedClientPersistDTO subsConvergedClientPersistDTO = (KnConvergedClientPersistDTO) persistDTO;
            int licenseType = subsConvergedClientPersistDTO.getLicenseType();
            String commandPackage = subsConvergedClientPersistDTO.getCommandPackageCode();
            knLogger.debug(methodName, "commandPackage is - ", commandPackage, "And LicenseType is - ", licenseType);
            if(licenseType == ENABLED && com.kodiak.xdms.server.common.resources.KnConstants.COMMAND_TIER_PKG.equals(commandPackage)){
                knLogger.error(methodName, "CommandPackage Assigned ", subsConvergedClientPersistDTO.getSubsMdn());
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.COMMAND_PACKAGE_ASSIGNED,
                        "Command Package assigned to subscriber", getEntityId(),
                        getOperationType(), getRuleId(), Arrays.asList(commandPackage).toString(), "");
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnConvergedClientPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "EXIT: Validation Completed Successfully for SwitchConvergedClient - ");
    }
}
