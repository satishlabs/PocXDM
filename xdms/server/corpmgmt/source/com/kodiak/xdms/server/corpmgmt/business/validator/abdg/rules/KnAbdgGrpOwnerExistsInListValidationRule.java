/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnAbdgGrpOwnerExistsInListValidationRule.java
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
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;
import java.util.Collection;

import static com.kodiak.common.resources.KnConstants.AREA_BASED_DYNAMIC_GROUP;

public class KnAbdgGrpOwnerExistsInListValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAbdgGrpOwnerExistsInListValidationRule.class);
    private String CLASS = KnAbdgGrpOwnerExistsInListValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
            KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
            Collection<String> requestDispatcherSubscribers = corpGroupInfoPersistDTO.getRequestDispatcherSubscribers();
            String grpOwner = corpGroupInfoPersistDTO.getTpGroupOwner();
            knLogger.debug(methodName, "requestDispatcherSubscribers: -- ", requestDispatcherSubscribers, "grpOwner: --", grpOwner);
            if (AREA_BASED_DYNAMIC_GROUP == groupInfoDTO.getClientType() && !requestDispatcherSubscribers.contains(grpOwner)) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_OWNER_NOT_EXISTS_AS_ADDED_MEMBERS,
                        "Group Owner not exists in Added Member List", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(grpOwner).toString(), "");

            }
        }
        knLogger.debug(methodName, "Validation Completed Successfully for the Grp Owner Existence in Added Members");
    }
}
