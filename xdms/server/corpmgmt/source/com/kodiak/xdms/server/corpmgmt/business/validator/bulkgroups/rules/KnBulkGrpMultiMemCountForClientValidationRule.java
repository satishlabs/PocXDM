/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkGrpMultiMemCountForClientValidationRule.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      Jan 23, 2019      9.03
 * <p/>
 * <p/>
 * 9th Floor, MFar Manyata Tech Park
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.business.validator.bulkgroups.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCloningPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;

public class KnBulkGrpMultiMemCountForClientValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkGrpMultiMemCountForClientValidationRule.class);
    private String CLASS = KnBulkGrpMultiMemCountForClientValidationRule.class.getName();
    private static String INTEROPGRPCOUNTALLOWED = "interOPGroupCountAllowed";

    /**
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        try {
            if (persistDTO instanceof KnCorpBulkGroupInfoPersistDTO) {
                knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
                KnCorpBulkGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpBulkGroupInfoPersistDTO) persistDTO;
                String clientType = corpGroupInfoPersistDTO.getAllowedClientTypes();
                int grpCountAllowed = Integer.valueOf(getAttribute(INTEROPGRPCOUNTALLOWED));
                int grpSize = corpGroupInfoPersistDTO.getGroupDetailsMap().size();
                if (KnConstants.SUBSCR_CLIENT_TYPE.POC_DONOR_RADIO.value() == Integer.parseInt(clientType) && grpSize > grpCountAllowed) {
                    knLogger.error("The interOP cannot be part of multiple groups...");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTEROP_SUBSC_CANNOT_BE_PART_OF_MULTIPLE_GRP,
                            "The interOP cannot be part of multiple groups.", getEntityId(), getOperationType(), getRuleId(),
                            corpGroupInfoPersistDTO.getGroupDetailsMap().keySet().toString(), "");
                } else if ((KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value() == Integer.parseInt(clientType)
                        || KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value() == Integer.parseInt(clientType)) && grpSize > grpCountAllowed) {
                    knLogger.error("The SGMdn/SGMdnPatch cannot be part of multiple groups....");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_MDN_EXISTS_IN_GROUP,
                            "The SGMdn/SGMdnPatch cannot be part of multiple groups.", getEntityId(), getOperationType(), getRuleId(),
                            corpGroupInfoPersistDTO.getGroupDetailsMap().keySet().toString(), "");
                }
            } else if (persistDTO instanceof KnCloningPersistDTO) {
                knLogger.debug("validate", "persistDTO is instanceof KnCorpBulkGroupInfoPersistDTO - ", persistDTO);
                KnCloningPersistDTO cloningPersistDTO = (KnCloningPersistDTO) persistDTO;
                if (cloningPersistDTO.getValidationMap().containsKey(com.kodiak.common.resources.KnConstants.CLONING_VALIDATION_BIT.GROUP.value()) && cloningPersistDTO.getValidationMap().get(com.kodiak.common.resources.KnConstants.CLONING_VALIDATION_BIT.GROUP.value())) {
                    KnCorpBulkGroupInfoPersistDTO corpGroupInfoPersistDTO = cloningPersistDTO.getGroupInfoPersistDTO();
                    String clientType = corpGroupInfoPersistDTO.getAllowedClientTypes();
                    int grpCountAllowed = Integer.valueOf(getAttribute(INTEROPGRPCOUNTALLOWED));
                    int grpSize = corpGroupInfoPersistDTO.getGroupDetailsMap().size();
                    if (KnConstants.SUBSCR_CLIENT_TYPE.POC_DONOR_RADIO.value() == Integer.parseInt(clientType) && grpSize > grpCountAllowed) {
                        knLogger.error("The interOP cannot be part of multiple groups...");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTEROP_SUBSC_CANNOT_BE_PART_OF_MULTIPLE_GRP,
                                "The interOP cannot be part of multiple groups.", getEntityId(), getOperationType(), getRuleId(),
                                corpGroupInfoPersistDTO.getGroupDetailsMap().keySet().toString(), "");
                    } else if ((KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value() == Integer.parseInt(clientType)
                            || KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value() == Integer.parseInt(clientType)) && grpSize > grpCountAllowed) {
                        knLogger.error("The SGMdn/SGMdnPatch cannot be part of multiple groups....");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_MDN_EXISTS_IN_GROUP,
                                "The SGMdn/SGMdnPatch cannot be part of multiple groups.", getEntityId(), getOperationType(), getRuleId(),
                                corpGroupInfoPersistDTO.getGroupDetailsMap().keySet().toString(), "");
                    }
                }
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Completed Successfully");
        }
    }
}
