/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnPoCSubscriberValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        21-01-2011      7.0
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
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;

public class KnPoCMembershipValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPoCMembershipValidationRule.class);
    private final String CLASS = KnPoCMembershipValidationRule.class.getName();

    public void validate() throws KnValidationException {

        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY Validating if the subscriber that are to be added are valid PoC subscribers.");
        try {
            Collection<String> inputMdnList;
            Collection<String> dbMdnList;

            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                if (inputDTO instanceof KnIPCorpSubscContactListDTO) {
                    KnIPCorpSubscContactListDTO contactListDTO = (KnIPCorpSubscContactListDTO) inputDTO;
                    inputMdnList = contactListDTO.getAddedMdnList();

                } else if ((inputDTO instanceof KnIPCorpSublistSubscDistDTO)) {
                    KnIPCorpSublistSubscDistDTO contactListDTO = (KnIPCorpSublistSubscDistDTO) inputDTO;
                    inputMdnList = contactListDTO.getMdnList();

                } else if (inputDTO instanceof KnIPCorpContactListDTO) {
                    KnIPCorpContactListDTO contactListDTO = (KnIPCorpContactListDTO) inputDTO;
                    inputMdnList = contactListDTO.getMdnList();
                } else if (inputDTO instanceof KnIPSubscriberInfoDTO) {
                    KnIPSubscriberInfoDTO contactListDTO = (KnIPSubscriberInfoDTO) inputDTO;
                    inputMdnList = contactListDTO.getMdnList();
                } else {
                    knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                            ", Expected Dto - KnIPCorpSublistSubscDistDTO/KnIPCorpSubscContactListDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }
                dbMdnList = ((KnContactDetailsPersistDTO) persistDTO).getPocSubscMdnList();

            } else if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                if ((inputDTO instanceof KnIPCorpSublistInfoDTO)) {
                    KnIPCorpSublistInfoDTO sublistInputDTO = (KnIPCorpSublistInfoDTO) inputDTO;
                    inputMdnList = sublistInputDTO.getAddedMdnList();

                } else {
                    knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                            ", Expected Dto - KnIPCorpSublistSubscDistDTO/KnIPCorpSubscContactListDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }
                dbMdnList = ((KnSublistDetailsPersistDTO) persistDTO).getPocMdnList();

            } else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                if ((inputDTO instanceof KnIPCorpGroupInfoDTO)) {
                    KnIPCorpGroupInfoDTO corpGroupInfoDTO = (KnIPCorpGroupInfoDTO) inputDTO;
                    inputMdnList = corpGroupInfoDTO.getAddedMdnsList();
                } else {
                    knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                            ", Expected Dto - KnIPCorpGroupInfoDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }
                dbMdnList = ((KnCorpGroupInfoPersistDTO) persistDTO).getPoCSubscriberList();

            } else {
                knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnCorpGroupInfoPersistDTO/KnSublistDetailsPersistDTO/KnIPCorpSubscContactListDTO "
                );
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }

            Collection<String> notFoundMDNList = new ArrayList<String>();
            String invalidMdn = "";
            if (!isObjectNull(inputMdnList) && !inputMdnList.isEmpty()) {
                knLogger.debug( methodName, "sublistMemberlist/AddedMdnList passed in request inputDTO is not empty");
                for (String mdn : inputMdnList) {
                    knLogger.debug( methodName, "Checking mdn - " , mdn , " exists in the DB.");
                    if (!dbMdnList.contains(mdn)) {
                        knLogger.debug( methodName, "Mdn - " , mdn , " does not exist in the DB.");
                        notFoundMDNList.add(mdn);
                        knLogger.debug( methodName, "Mdn not found in DB so far are - " , notFoundMDNList);
                    }
                }
                invalidMdn = formCommaSeperatedIdList(notFoundMDNList);
            } else {
                if (!isObjectNull(inputMdnList) && !inputMdnList.isEmpty()) {
                    invalidMdn = formCommaSeperatedIdList(inputMdnList);
                }
            }

            knLogger.debug( methodName, "List of Mdn i.e AddedMdnList not found in DB are - " , invalidMdn);
            if (!isNullOrEmpty(invalidMdn)) {
                knLogger.error( methodName, "Invalid PoC Subscribers in request - " , invalidMdn);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_POC_SUBSCRIBERS,
                        "Invalid PoCSubscriber", getRuleId(),
                        KnConstants.KEY_DATATYPE_MDN, "", invalidMdn, "");
            }         } finally {
            knLogger.debug( methodName, "EXIT: Validation Completed Successfully for the valid POC subscribers");
        }
    }
}
