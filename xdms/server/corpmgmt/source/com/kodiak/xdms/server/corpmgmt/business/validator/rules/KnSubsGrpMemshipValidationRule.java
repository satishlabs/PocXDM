/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSubsGrpMemshipValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        04-04-2011      7.0
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

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnMdnDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;

import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Collection;

public class KnSubsGrpMemshipValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsGrpMemshipValidationRule.class);


    /**
     * Validate the max allowed sublist for the corporate
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        String requesterMdn = null;
        Collection<String> dbInternalMdnList;
        Collection<KnCorpSubscriberDTO> dbExternalMdnList;
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY Point : ");
        if (persistDTO instanceof KnMdnDetailsPersistDTO) {
            KnMdnDetailsPersistDTO contactPersistDto = (KnMdnDetailsPersistDTO) persistDTO;
            contactPersistDto.getAddedMdnDTO();
            dbInternalMdnList = contactPersistDto.getMdnList();
            dbExternalMdnList = contactPersistDto.getExternalMdnList();
            IInputDTO inputDTO = persistDTO.getInputDTO();
            if (inputDTO instanceof KnIPCorpContactDTO) {
                KnIPCorpContactDTO inputDto = (KnIPCorpContactDTO) persistDTO.getInputDTO();
                knLogger.debug(methodName, "InputDTO passed is - ", inputDto);
                requesterMdn = inputDto.getMdn();
                knLogger.debug(methodName, "requesterMdn -  ", requesterMdn);
                knLogger.debug(methodName, "dbInternalMdnList -  ", dbInternalMdnList);
                knLogger.debug(methodName, "dbExternalMdnList -  ", dbExternalMdnList);
                if (dbInternalMdnList == null || dbInternalMdnList.isEmpty()) {
                    knLogger.error(methodName, "mdn does not present in same corporation i.e requester mdn - ", requesterMdn);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                            "requesterMdn not found in DB for the Corporate as internal or external", getRuleId(),
                            KnConstants.KEY_DATATYPE_MDN, null, requesterMdn, null);
                }
            } else {
                knLogger.error("validate()", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnMdnDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
            knLogger.debug(methodName, "Validation Completed Successfully");
        }

    }

}

