/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnAbdgGrpOwnerClientTypeValidationRule.java
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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.AREA_BASED_DYNAMIC_GROUP;

public class KnAbdgGrpOwnerClientTypeValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAbdgGrpOwnerClientTypeValidationRule.class);
    private String CLASS = KnAbdgGrpOwnerClientTypeValidationRule.class.getName();
    private static final String ALLOWED_TYPES = "allowedTypes";

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        String allowedType = getAttribute(ALLOWED_TYPES);
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
            KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
            String grpOwner = corpGroupInfoPersistDTO.getTpGroupOwner();
            knLogger.debug(methodName, "grpOwner:  - ", grpOwner);
            Map<String, KnCorpSubscriberDTO> mdnMap = corpGroupInfoPersistDTO.getTpRequestMDNMap();
            if (AREA_BASED_DYNAMIC_GROUP == groupInfoDTO.getClientType() && groupInfoDTO.getGroupType() == KnConstants.DISPATCH_GROUP) {
                knLogger.debug( methodName, "grpOwner-" , grpOwner, "mdnMap-", mdnMap);
                KnCorpSubscriberDTO subscriberDTO = mdnMap.get(grpOwner);
                // in db profile might not exist. hence this null check
                if (subscriberDTO != null) {
                    int clientType = subscriberDTO.getClientType();
                    List<String> clientTypeList = Arrays.asList(allowedType.split(DELIM));
                    if (!clientTypeList.contains(String.valueOf(clientType))) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_OWNER_IS_NOT_A_DISPATCH_CLIENT,
                                "Owner MDN is not a dispatch client", getEntityId(), getOperationType(), getRuleId(), grpOwner, "");
                    }
                }
                knLogger.debug( methodName, "Exit Point: Validation Completed Successfully");
            }
        }
    }
}
