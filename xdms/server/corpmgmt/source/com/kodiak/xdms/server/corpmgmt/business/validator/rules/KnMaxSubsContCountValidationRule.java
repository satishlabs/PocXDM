/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMaxContactPerSubscriber.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        25-01-2011      7.0
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

import java.util.Arrays;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnMaxSubsContCountValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMaxSubsContCountValidationRule.class);
    final String CLASS = KnMaxSubsContCountValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY Validating the max allowed contacts for the subscribers.");
        int maxContactCount = 0;
        int currentContactCount = 0;
        try {
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactPersistDto = (KnContactDetailsPersistDTO) persistDTO;
                KnIPCorpSubscContactListDTO inputDTO = (KnIPCorpSubscContactListDTO) persistDTO.getInputDTO();
                knLogger.debug( methodName, "InputDTO passed is - " , inputDTO);
                maxContactCount = contactPersistDto.getMaxSubscribersContactLimit();
                currentContactCount = contactPersistDto.getContactCount();
            } else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO groupPersistDto = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();
                knLogger.debug( methodName, "InputDTO passed is - " , inputDTO);
                maxContactCount = groupPersistDto.getMaxNumberOfMembers();
                currentContactCount = groupPersistDto.getGroupMemberCount();
            } else {
                knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }

            if (currentContactCount > maxContactCount) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_SUBSCRIBER_CONTACT_EXCEEDED,
                        "The contact count for the subscriber has exceeded", getEntityId(),
                        getOperationType(), getRuleId(), Arrays.asList(maxContactCount).toString(), "");
            }         } finally {
            knLogger.debug( methodName, "EXIT: Validation Completed Successfully for the max allowed contacts for the subscribers");
        }
    }
}
