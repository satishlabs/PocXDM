/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

/**
 * ************************************************************************
 * <p/>
 * File name: KnGrpInteOPMemValidationRule.java
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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.Collection;
import com.kodiak.logger.KnLogger;

/**
 * This class is used to valuidate if any subscribers in request being passed as a Inter Op Subscriber for the group
 * actually is a interOP subscriber
 */
public class KnGrpInteOPMemValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnGrpInteOPMemValidationRule.class);
    private String CLASS = KnGrpInteOPMemValidationRule.class.getName();

    /**
     * This method is used to validate if any of the inter
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, "persistDTO obtained from the request is - " , persistDTO);
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - " , persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                knLogger.debug( methodName, "corpGroupInfoPersistDTO - " , corpGroupInfoPersistDTO);
//                KnIPCorpGroupInfoDTO groupInputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();
                Collection<String> reqInterOpSubsc = corpGroupInfoPersistDTO.getReqInterOpSubs();
                knLogger.debug( methodName, "reqInterOpSubsc - " , reqInterOpSubsc);
                Collection<KnCorpSubscriberDTO> pocSubscriberList = corpGroupInfoPersistDTO.getAddedMdnDTO();
                knLogger.debug( methodName, "pocSubscriberList - " , pocSubscriberList);
                int dbInterOpClientCount = 0;
                if (pocSubscriberList != null && !pocSubscriberList.isEmpty()) {
                    for (KnCorpSubscriberDTO subsc : pocSubscriberList) {
                        if (subsc.getClientType() == KnConstants.INTER_OP_CLIENT_TYPE) {
                            dbInterOpClientCount++;
                        }
                    }
                }
                if (!KnCorpUtil.isObjectNullOrEmpty(reqInterOpSubsc) && reqInterOpSubsc.size() > dbInterOpClientCount) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTEROP_SUBSC_NOT_A_INTEROP_CLIENT,
                            "InterOp Subcriber passed is not a interOp client.", getEntityId(), getOperationType(),
                            getRuleId(), reqInterOpSubsc.toString(), "");
                }
            }         } finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully for the group Interop Members");
        }
    }
}

