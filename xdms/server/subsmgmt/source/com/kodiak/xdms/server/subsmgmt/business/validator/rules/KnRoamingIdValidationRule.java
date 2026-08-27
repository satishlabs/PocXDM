/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       1/5/11       7.0
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
 * *******************************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

import java.util.ArrayList;
import java.util.HashMap;

public class KnRoamingIdValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnRoamingIdValidationRule.class);
    private static final String className = KnRoamingIdValidationRule.class.getName();
    private static KnProvInfoUtil provInfoUtil;
    private static final String CHECK_NULL = "checkNull";

    static {
        provInfoUtil = new KnProvInfoUtil();
    }

    public void validate() throws KnValidationException {
        String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();

        String checkNull = getAttribute(CHECK_NULL);

        KnSubsProfilePersistDTO subsProfilePersistDTO;
        if (persistDTO instanceof KnSubsProfilePersistDTO) {
            subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
        } else {
            knLogger.error( methodName, "Invalid DTO is passed for validation " , persistDTO.getClass());
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Un-expected dao DTO is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
        }

        ArrayList<Integer> roamingIdList = subsProfilePersistDTO.getRoamingTypes();
        knLogger.debug( methodName, "Validating the roamingList --> " , roamingIdList);
        try {
            if (roamingIdList != null) {
                HashMap<Integer, String> supportedRoamingList = (HashMap<Integer, String>) provInfoUtil.retrieveSupportedRoamingList();
                for (int roamingId : roamingIdList) {
                    if (roamingId != -1) {
                        if (!supportedRoamingList.containsValue(String.valueOf(roamingId))) {
                            knLogger.debug( methodName, "Invalid roaming type - " , roamingId);
                            throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_ROAMING_ID,
                                    "Invalid Roaming type is passed; Expected - " + supportedRoamingList,
                                    entityId, operationId, getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
                        }
                    }
                }
            } else {
                if (checkNull.equalsIgnoreCase("true")) {
                    knLogger.debug( methodName, "raoming types cannot be null");
                    throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_ROAMING_ID,
                            "Invalid Roaming type is passed; Expected - " + roamingIdList,
                            entityId, operationId, getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
                }
            }


        } catch (KnProvBOException e) {
            knLogger.error( methodName, "Validation failed. Could not retrieve Roaming Cluster Ids.");
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Validation failed. Failed to retrieve Roaming Cluster Ids.",
                    entityId, operationId, getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
        }

        knLogger.debug( methodName, "Validation for roaming type is succesfull");

    }
}
