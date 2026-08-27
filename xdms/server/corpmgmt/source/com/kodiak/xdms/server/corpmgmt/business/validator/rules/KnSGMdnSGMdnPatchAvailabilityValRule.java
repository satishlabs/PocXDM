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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * ************************************************************************
 * <p>
 * File name:  KnSGMdnSGMdnPatchAvailabilityValRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             April 03, 2017                8.3
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

public class KnSGMdnSGMdnPatchAvailabilityValRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSGMdnSGMdnPatchAvailabilityValRule.class);
    private String CLASS = KnSGMdnSGMdnPatchAvailabilityValRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating SGMdn and SGMdnPatch availability in same request");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        try {
            Set<String> groupMdnList = new HashSet<String>();
            Set<String> groupMdnPatchList = new HashSet<String>();
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", groupPersistDTO);
                Collection<KnCorpSubscriberDTO> mdnList = groupPersistDTO.getAddedMdnDTO();
                Collection<KnCorpSubscriberDTO> existingMdnList = groupPersistDTO.getExistingGrpMdnDto();
                knLogger.debug(methodName, " MDN List : -- ", mdnList);
                if (mdnList != null) {
                    mdnList.forEach(subsc -> {
                        if (subsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()) {
                            knLogger.debug(methodName, "Group MDN - ", subsc.getMdn());
                            groupMdnList.add(subsc.getMdn());
                        } else if (subsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
                            knLogger.debug(methodName, "Group MDN Patch- ", subsc.getMdn());
                            groupMdnPatchList.add(subsc.getMdn());
                        }
                    });
                }
                if (existingMdnList != null) {
                    existingMdnList.forEach(existingSubsc -> {
                        if (existingSubsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()) {
                            knLogger.debug(methodName, "Existing Group MDN - ", existingSubsc.getMdn());
                            groupMdnList.add(existingSubsc.getMdn());
                        } else if (existingSubsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
                            knLogger.debug(methodName, "Existing Group MDN Patch- ", existingSubsc.getMdn());
                            groupMdnPatchList.add(existingSubsc.getMdn());
                        }
                    });
                }
            } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
                KnCorpBCGrpPersistDTO bcGroupPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", bcGroupPersistDTO);
                Collection<KnCorpSubscriberDTO> members = bcGroupPersistDTO.getValidInternalCont();
                Collection<KnCorpSubscriberDTO> existingMdnList = bcGroupPersistDTO.getExistingGrpMdnDto();
                if (members != null) {
                    members.forEach(subsc -> {
                        if (subsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()) {
                            knLogger.debug(methodName, "Group MDN - ", subsc.getMdn());
                            groupMdnList.add(subsc.getMdn());
                        } else if (subsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
                            knLogger.debug(methodName, "Group MDN Patch- ", subsc.getMdn());
                            groupMdnPatchList.add(subsc.getMdn());
                        }
                    });
                }
                if (existingMdnList != null) {
                    existingMdnList.forEach(existingSubsc -> {
                        if (existingSubsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()) {
                            knLogger.debug(methodName, "Existing Group MDN - ", existingSubsc.getMdn());
                            groupMdnList.add(existingSubsc.getMdn());
                        } else if (existingSubsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
                            knLogger.debug(methodName, "Existing Group MDN Patch- ", existingSubsc.getMdn());
                            groupMdnPatchList.add(existingSubsc.getMdn());
                        }
                    });
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnCorpGroupInfoPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
            knLogger.debug(methodName, "groupMdnList: -- ", groupMdnList, "groupMdnPatchList: -- ", groupMdnPatchList);
            if (!groupMdnList.isEmpty() && !groupMdnPatchList.isEmpty()) {
                knLogger.debug(methodName, "SGMdn and SGMdnPatch cannot be part of same group ");
                groupMdnList.addAll(groupMdnPatchList);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.SG_AND_SG_PATCH_MDN_CANNOT_BE_PART_OF_SAME_GROUP,
                        "SGMdn and SGMdnPatch cannot be part of same group ", getEntityId(),
                        getOperationType(), getRuleId(), groupMdnList.toString(), "");
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Successfully done for the SGMdn and SGMdnPatch availability");
        }
    }
}