/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMaxGroupsValidationRule.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Harsha A             Feb 2, 2011           7.0
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
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicGroupPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import static com.kodiak.xdms.server.pubmgmt.resources.KnConstants.*;

import com.kodiak.logger.KnLogger;


public class KnMaxGroupsValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxGroupsValidationRule.class);

    private final String CLASS = KnMaxGroupsValidationRule.class.getName();
    //stores current groups count
    private int groupsCount = 0;


    public void validate() throws KnValidationException {
        String mdn = null;
        int maxGroupsAllowed = 0;
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug("validate", "ENTRY : Validating if total group members count exceeds max members " +
                "per group limit. dto passed ->" + persistDTO);

        try {
            if (persistDTO instanceof KnDynamicGroupPersistDTO) {
                KnPubInfoUtil groupInfoUtil = new KnPubInfoUtil();
                KnDynamicGroupPersistDTO groupDTO = (KnDynamicGroupPersistDTO) persistDTO;
                mdn = groupDTO.getOwner();
                //retrieving the current groups count for the subscriber
                groupsCount = groupInfoUtil.getGroupNameCount(groupDTO.getOwner(), null);
                //retrieveing the maximum groups allowed per subscriber
                maxGroupsAllowed = groupInfoUtil.getXdmsServiceConfig().getMaxPublicPOCGrpsPerSubs();
            } else if (persistDTO instanceof KnPubGroupInfoPersistDTO) {
                KnPubInfoUtil groupInfoUtil = new KnPubInfoUtil();
                KnPubGroupInfoPersistDTO groupDTO = (KnPubGroupInfoPersistDTO) persistDTO;
                mdn = groupDTO.getOwner();
                //retrieving the current groups count for the subscriber
                groupsCount = groupInfoUtil.getGroupNameCount(groupDTO.getOwner(), null);
                //retrieveing the maximum groups allowed per subscriber
                maxGroupsAllowed = groupInfoUtil.getXdmsServiceConfig().getMaxPublicPOCGrpsPerSubs();
            } else {
                knLogger.error("validate", "Unexpected DTO passed - " + persistDTO.getClass() +
                        ", Expected Dto - KnCLGroupInfoPersistDTO ");
                throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), KEY_DATATYPE_MDN, "");
            }
            //comparing current groups count with maximum count allowed
            if (groupsCount >= maxGroupsAllowed) {
                knLogger.error("validate", "Validation failed. Max Groups allowed : " + maxGroupsAllowed +
                        " , No. of Groups Subscribed : " + Integer.toString(groupsCount));
                throw new KnPubBOValidationException(KnErrorCodes.Validator.MAX_GROUPS_REACHED,
                        "Validation Rule Failed, Max groups limit : " + maxGroupsAllowed, getEntityId(),
                        getOperationType(), getRuleId(), KEY_DATATYPE_MDN, mdn, Integer.toString(groupsCount));
            }
            knLogger.info("validate", "KnMaxGroupsValidationRule validated successfully.");

        } catch (KnPubBOValidationException vex) {
            knLogger.error("validate", "Business Validation Exception occurred. " + vex);
            throw vex;
        } catch (KnPubBOException boe) {
            knLogger.error("validate", "Business Exception occurred. " + boe);
            throw new KnPubBOValidationException(boe.getErrorCode(), "Could not retrieve group count : " +
                    "Max groups limit : " + maxGroupsAllowed, boe, getEntityId(), getOperationType(),
                    getRuleId(), Integer.toString(groupsCount));
        }
    }
}
