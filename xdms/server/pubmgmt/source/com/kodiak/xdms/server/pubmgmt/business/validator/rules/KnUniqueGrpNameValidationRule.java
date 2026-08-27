/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnUniqueGrpNameValidationRule.java
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


public class KnUniqueGrpNameValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnUniqueGrpNameValidationRule.class);

    private final String CLASS = KnUniqueGrpNameValidationRule.class.getName();


    public void validate() throws KnValidationException {
        String listServiceURI = null;
        String mdn = null;
        int groupNameCount;
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( "validate", "ENTRY : Validating if total group members count exceeds max members " +
                "per group limit. dto passed ->" + persistDTO);

        try {
            KnPubInfoUtil pubInfoUtil = new KnPubInfoUtil();
            if (persistDTO instanceof KnDynamicGroupPersistDTO) {
                KnDynamicGroupPersistDTO groupPersistDTO = (KnDynamicGroupPersistDTO) persistDTO;
                listServiceURI = groupPersistDTO.getListServiceURI();
                mdn = groupPersistDTO.getOwner();
            } else  if (persistDTO instanceof KnPubGroupInfoPersistDTO) {
                KnPubGroupInfoPersistDTO groupPersistDTO = (KnPubGroupInfoPersistDTO) persistDTO;
                listServiceURI = groupPersistDTO.getListServiceURI();
                mdn = groupPersistDTO.getOwner();
            } else {
                knLogger.error( "validate", "Unexpected DTO passed - " + persistDTO.getClass() +
                        ", Expected Dto - KnPubGroupInfoPersistDTO ");
                throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), KEY_DATATYPE_MDN, "");
            }


            groupNameCount = pubInfoUtil.getGroupNameCount(mdn, listServiceURI);

            //checking whether any record is retrieved for the group name, if found indicates an existing
            //group for the group name
            if (groupNameCount > 0) {
                knLogger.error( "validate", "Validation failed. Duplicate Group name exist.");
                throw new KnPubBOValidationException(KnErrorCodes.Validator.GROUP_ALREADY_EXISTS,
                        "Validation Rule Failed, Group Name is already existed.", entityId, operationId,
                        getRuleId(), KEY_DATATYPE_GROUPNAME, listServiceURI, Integer.toString(groupNameCount));
            }
            knLogger.debug( "validate", "KnUniqueGrpNameValidationRule Validated successfully.");
        } catch (KnPubBOValidationException vex) {
            knLogger.error( "validate", "Business Validation Exception occurred. " + vex);
            throw vex;
        } catch (KnPubBOException boe) {
            knLogger.error( "validate", "Business Exception occurred. " + boe);
            throw new KnPubBOValidationException(boe.getErrorCode(),
                    "Could not retrieve group name count :", boe, entityId, operationId, getRuleId(), listServiceURI);
        }
    }
}
