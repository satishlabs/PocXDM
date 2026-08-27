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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpActivationDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGenActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnAllocateSubsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPAllocateSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;

import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnPoCMemshipValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPoCMemshipValidationRule.class);
    private final String CLASS = KnPoCMemshipValidationRule.class.getName();

    public void validate() throws KnValidationException {

        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        Collection<String> notFoundMDNList = new ArrayList<String>();
        List<String> validMdnsFromSharedCorp = new ArrayList<>();
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
                }  else if ((inputDTO instanceof KnIPSubscriberInfoDTO)) {
                    KnIPSubscriberInfoDTO corpActInfoDTO = (KnIPSubscriberInfoDTO) inputDTO;
                    inputMdnList = corpActInfoDTO.getMdnList();
                } else {
                    knLogger.error( "validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                            ", Expected Dto - KnIPCorpSublistSubscDistDTO/KnIPCorpSubscContactListDTO/KnIPSubscriberInfoDTO ");
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
                    knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
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
                    knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                            ", Expected Dto - KnIPCorpGroupInfoDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }
                dbMdnList = ((KnCorpGroupInfoPersistDTO) persistDTO).getPoCSubscriberList();
                validMdnsFromSharedCorp = ((KnCorpGroupInfoPersistDTO) persistDTO).getValidSharedMdnsFromCorp();

            } else if (persistDTO instanceof KnCorpGenActivationPersistDTO) {
                if ((inputDTO instanceof KnIPCorpActivationDTO)) {
                    KnIPCorpActivationDTO corpActInfoDTO = (KnIPCorpActivationDTO) inputDTO;
                    inputMdnList = corpActInfoDTO.getMdnList();
                } else {
                    knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                            ", Expected Dto - KnIPCorpActivationDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }
                dbMdnList = ((KnCorpGenActivationPersistDTO) persistDTO).getPocMdnList();

            } else if (persistDTO instanceof KnCorpActivationPersistDTO) {
                if ((inputDTO instanceof KnIPCorpActivationDTO)) {
                    KnIPCorpActivationDTO corpActInfoDTO = (KnIPCorpActivationDTO) inputDTO;
                    inputMdnList = corpActInfoDTO.getMdnList();
                } else {
                    knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                            ", Expected Dto - KnIPCorpActivationDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }
                dbMdnList = ((KnCorpActivationPersistDTO) persistDTO).getPocMdnList();
            } else if (persistDTO instanceof KnAllocateSubsPersistDTO) {
                if ((inputDTO instanceof KnIPAllocateSubscriberDTO)) {
                    KnIPAllocateSubscriberDTO allocateSubscriberDTO = (KnIPAllocateSubscriberDTO) inputDTO;
                    inputMdnList = allocateSubscriberDTO.getReqMdnList();
                } else {
                    knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                            ", Expected Dto - KnIPAllocateSubscriberDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }
                dbMdnList = ((KnAllocateSubsPersistDTO) persistDTO).getPocSubscMdnList();
            }else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnCorpGroupInfoPersistDTO/KnSublistDetailsPersistDTO/KnIPCorpSubscContactListDTO/KnCorpGenActivationPersistDTO "
                );
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
            if(persistDTO instanceof KnCorpGroupInfoPersistDTO && !isObjectNull(inputMdnList) && !inputMdnList.isEmpty() && validMdnsFromSharedCorp != null ){
                knLogger.debug(methodName, "sublistMemberlist/AddedMdnList passed in request inputDTO is not empty");
                for (String mdn : inputMdnList) {
                    if (!dbMdnList.contains(mdn) && !validMdnsFromSharedCorp.contains(mdn)) {
                        knLogger.debug(methodName, "Mdn - ", mdn, " does not exist in the DB.");
                        notFoundMDNList.add(mdn);
                    }
                }
            }
            else if (!isObjectNull(inputMdnList) && !inputMdnList.isEmpty()) {
                knLogger.debug(methodName, "sublistMemberlist/AddedMdnList passed in request inputDTO is not empty");
                for (String mdn : inputMdnList) {
                    if (!dbMdnList.contains(mdn)) {
                        knLogger.debug(methodName, "Mdn - ", mdn, " does not exist in the DB.");
                        notFoundMDNList.add(mdn);
                    }
                }
            } else {
                if (!isObjectNull(inputMdnList) && !inputMdnList.isEmpty()) {
                	notFoundMDNList.addAll(inputMdnList);
                }
            }
            if (notFoundMDNList.size() > 0 ) {
            	 knLogger.error(methodName, "Invalid PoC Subscribers in request - ", notFoundMDNList);
                 throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_POC_SUBSCRIBERS,
                		  "Invalid PoCSubscriber", getEntityId(), getOperationType(), getRuleId(), notFoundMDNList.toString(), "");
            }
        } finally {
            knLogger.debug(methodName, "EXIT: Validation Completed Successfully for the valid POC subscribers");
        }
    }
}
