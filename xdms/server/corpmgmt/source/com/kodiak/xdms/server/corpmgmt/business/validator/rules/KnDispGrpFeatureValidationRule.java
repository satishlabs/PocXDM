/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnDispGrpFeatureValidationRule.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita P Nair      Dec 6, 2011      7.2
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
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;

import com.kodiak.logger.KnLogger;

public class KnDispGrpFeatureValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDispGrpFeatureValidationRule.class);
    private static final long serialVersionUID = 7526471155622676280L;
    private String CLASS = KnDispGrpFeatureValidationRule.class.getName();

    /**
     * This method is used to validate the datafor unique sublist Name for the corporation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - " , persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                int dispatchEnabled = corpGroupInfoPersistDTO.getDispatchEnabled();
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
                if (dispatchEnabled == 0) {
                    if (groupInfoDTO.getGroupType() == KnConstants.DISPATCH_GROUP) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.DISPATCH_FEATURE_DISABLED,
                                "Dispatch group cannot b e created for the corporate.", getEntityId(), getOperationType(), getRuleId(),
                                Arrays.asList(dispatchEnabled).toString(), "");
                    }
                }
            }
        } finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully");
        }
    }
}
