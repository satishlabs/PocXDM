/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMaxAbdgGrpPerOwnerCountValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar        01-03-2018      9.0+
 * <p/>
 * <p/>
 * 
 * 
 * KODIAK, 9th Floor, 'MFar
 * Manyata Tech Park' Greenheart Phase IV,
 * Nagawara Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.business.validator.abdg.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.AREA_BASED_DYNAMIC_GROUP;

public class KnMaxAbdgGrpPerOwnerCountValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxAbdgGrpPerOwnerCountValidationRule.class);
    private final String CLASS = KnMaxAbdgGrpPerOwnerCountValidationRule.class.getName();

    public void validate() throws KnValidationException {
        IPersistenceDTO persistDTO = getDTO();
        String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY : Validating if total group count exceeds max groups ",
                "per group owner limit.");
        int maxAbdgGroupsPerOwner = 0;
        int abdgGroupCountPerOwner = 0;
        KnIPCorpGroupInfoDTO groupIpDTO = null;
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            groupIpDTO = (KnIPCorpGroupInfoDTO) groupPersistDTO.getInputDTO();
            maxAbdgGroupsPerOwner = groupPersistDTO.getMaxAbdgPerGrpOwner();
            abdgGroupCountPerOwner = groupPersistDTO.getAbdgGroupCountPerOwner();
        } else {
            knLogger.error(methodName, "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "", "");
        }
        if (AREA_BASED_DYNAMIC_GROUP == groupIpDTO.getClientType() && groupIpDTO.getGroupType() == KnConstants.DISPATCH_GROUP) {
            if (abdgGroupCountPerOwner >= maxAbdgGroupsPerOwner) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_ABDG_COUNT_EXCEEDED_PER_OWNER,
                        "Abdg Groups limit per Owner has exceeded", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(maxAbdgGroupsPerOwner).toString(), "");
            }
        }
        knLogger.debug(methodName, "KnMaxAbdgGrpPerOwnerCountValidationRule Validated successfully.");
    }
}
