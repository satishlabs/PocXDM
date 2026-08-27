/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

/**
 * ************************************************************************
 * <p/>
 * File name: KnGrpInterOPCountValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name Date Release
 * -------------------- ------------ -------------------------------------
 * Namita P Nair 2/9/12 7.2
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

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.Collection;
import com.kodiak.logger.KnLogger;

/**
 * This calss validates that multiple interop cannot be part of group
 */
public class KnGrpInterOPCountValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnGrpInterOPCountValidationRule.class);
    private String MULTIPLE_INTEROP_ALLOWED = "multipleInterOPAllowed";

    /**
     * This method is used to validate that multiple interop cannot be part of group
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, "persistDTO obtained from the request is - " , persistDTO);
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - " , persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                boolean multipleinterOPAllowed = Boolean.valueOf(getAttribute(MULTIPLE_INTEROP_ALLOWED));
                Collection<String> interOpSubs = corpGroupInfoPersistDTO.getReqInterOpSubs();
                Collection<String> dbInterOpSubs = corpGroupInfoPersistDTO.getDbInterOpSubs();
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
                int interOPCount = 0;
                if (!KnCorpUtil.isObjectNullOrEmpty(dbInterOpSubs)) {
                    if (!KnCorpUtil.isObjectNullOrEmpty(inputDTO.getRemovedMemberMdns())) {
                        for (String mdn : dbInterOpSubs) {
                            if (!inputDTO.getRemovedMemberMdns().contains(mdn)) {
                                interOPCount++;
                            }
                        }
                    } else {
                        interOPCount = dbInterOpSubs.size();
                    }
                }
                if (!KnCorpUtil.isObjectNullOrEmpty(interOpSubs)) {
                    interOPCount = interOPCount + interOpSubs.size();
                }
                if (multipleinterOPAllowed == Boolean.FALSE) {
                    if (interOPCount > 1) {
                    	knLogger.error("Mulitple interOP not allowed for a group....",interOpSubs);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MULTIPLE_INTEROP_NOT_ALLOWED_FOR_GROUP,
                                "Mulitple interOP not allowed for a group.", getEntityId(), getOperationType(), getRuleId(),
                                interOpSubs.toString(), "");
                    }
                }
            }else if(persistDTO instanceof KnCorpBCGrpPersistDTO){
                knLogger.debug( methodName, "persistDTO is instanceof KnCorpBCGrpPersistDTO - " , persistDTO);
                KnCorpBCGrpPersistDTO bcPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
                boolean multipleinterOPAllowed = Boolean.valueOf(getAttribute(MULTIPLE_INTEROP_ALLOWED));
                Collection<String> requestInetrOP = bcPersistDTO.getInterOpMembers();
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) bcPersistDTO.getInputDTO();
                int interOPCount = 0;
                if (!KnCorpUtil.isObjectNullOrEmpty(requestInetrOP)) {
                    interOPCount =  requestInetrOP.size();
                }
                if (multipleinterOPAllowed == Boolean.FALSE) {
                    if (interOPCount > 1) {
                    	knLogger.error("Mulitple interOP not allowed for a group....",requestInetrOP);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MULTIPLE_INTEROP_NOT_ALLOWED_FOR_GROUP,
                                "Mulitple interOP not allowed for a group.", getEntityId(), getOperationType(), getRuleId(),
                                requestInetrOP.toString(), "");
                    }
                }
            }
            knLogger.debug( methodName, "Validation Completed Successfully");
        } finally {
            knLogger.debug( methodName, "Exit Point");
        }
    }
}

