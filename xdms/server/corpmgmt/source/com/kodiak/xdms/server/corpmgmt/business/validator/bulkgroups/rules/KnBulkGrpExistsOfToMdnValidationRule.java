/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkGrpExistsOfToMdnValidationRule.java
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
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;

public class KnBulkGrpExistsOfToMdnValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkGrpExistsOfToMdnValidationRule.class);
    private String CLASS = KnBulkGrpExistsOfToMdnValidationRule.class.getName();

    /**
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        try {
            String requestMdn = null;
            if (persistDTO instanceof KnCorpBulkGroupInfoPersistDTO) {
                knLogger.debug(methodName, "persistDTO is instanceof KnCorpBulkGroupInfoPersistDTO - ", persistDTO);
                KnCorpBulkGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpBulkGroupInfoPersistDTO) persistDTO;
                IInputDTO inputDTO = persistDTO.getInputDTO();
                if (inputDTO instanceof KnIPSubscriberInfoDTO) {
                    KnIPSubscriberInfoDTO subsResquestDTO = (KnIPSubscriberInfoDTO) inputDTO;
                    requestMdn = subsResquestDTO.getToMdn();
                }
                if(!corpGroupInfoPersistDTO.isGrpNotExistsForToMdn()){
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_EXISTS,
                            "Group exists", getEntityId(), getOperationType(), getRuleId(),
                            Arrays.asList(requestMdn).toString(), "");
                }
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Completed Successfully");
        }
    }
}
