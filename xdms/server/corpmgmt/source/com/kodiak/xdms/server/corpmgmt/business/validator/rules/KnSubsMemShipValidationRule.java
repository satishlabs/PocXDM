/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSubsMemShipValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        25-03-2011      7.0
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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnUserprofileSharedlistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isNullOrEmpty;

public class KnSubsMemShipValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsMemShipValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        Collection<String> requestMdnList = null;
        Collection<String> dbMdnList = null;
        int subsCorpId = 0;
        int reqCorpId = 0;
        String requestMdn = null;
        boolean isUPMSharingEnable = false;
        try {
            IPersistenceDTO persistDTO = getDTO();
            knLogger.debug(methodName, "ENTRY Point : ");
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactPersistDto = (KnContactDetailsPersistDTO) persistDTO;
                dbMdnList = contactPersistDto.getContactMdnList();
                IInputDTO inputDTO = persistDTO.getInputDTO();
                if (inputDTO instanceof KnIPCorpSublistSubscDistDTO) {
                    KnIPCorpSublistSubscDistDTO inputDto = (KnIPCorpSublistSubscDistDTO) persistDTO.getInputDTO();
                    knLogger.debug(methodName, "InputDTO passed is - ", inputDto);
                    requestMdnList = inputDto.getMdnList();
                }
                if (inputDTO instanceof KnIPCorpContactDTO) {
                    KnIPCorpContactDTO corpContactDTO = (KnIPCorpContactDTO) inputDTO;
                    reqCorpId = corpContactDTO.getCorpId();
                    subsCorpId = contactPersistDto.getSubsCorpId();
                    requestMdn = corpContactDTO.getMdn();
                }
                if (inputDTO instanceof KnIPSubscriberInfoDTO) {
                    KnIPSubscriberInfoDTO corpContactDTO = (KnIPSubscriberInfoDTO) inputDTO;
                    reqCorpId = Integer.parseInt(corpContactDTO.getCorpId());
                    subsCorpId = contactPersistDto.getSubsCorpId();
                    requestMdn = corpContactDTO.getMdn();
                }
            } else if (persistDTO instanceof KnCorpTGSPersistDTO) {
                KnCorpTGSPersistDTO corpTGSPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
                IInputDTO inputDTO = persistDTO.getInputDTO();
                subsCorpId = corpTGSPersistDTO.getSubsCorpId();
                isUPMSharingEnable = corpTGSPersistDTO.isUPMSharingEnabled();
                if (inputDTO instanceof KnIPTalkGroupDTO) {
                    KnIPTalkGroupDTO talkGroupDTO = (KnIPTalkGroupDTO) inputDTO;
                    reqCorpId = Integer.parseInt(talkGroupDTO.getCorpId());
                    requestMdn = talkGroupDTO.getMdn();
                } else if (inputDTO instanceof KnIPSubsProvInfoDTO) {
                    KnIPSubsProvInfoDTO talkGroupDTO = (KnIPSubsProvInfoDTO) inputDTO;
                    reqCorpId = Integer.parseInt(talkGroupDTO.getCorpId());
                    requestMdn = talkGroupDTO.getMdn();
                }
            } else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                IInputDTO inputDTO = persistDTO.getInputDTO();
                subsCorpId = corpGroupInfoPersistDTO.getCorpId();
                if (inputDTO instanceof KnIPCorpGroupDTO) {
                    KnIPCorpGroupDTO groupDTO = (KnIPCorpGroupDTO) inputDTO;
                    reqCorpId = groupDTO.getCorpId();
                    requestMdn = groupDTO.getOwner();
                }
            } else if (persistDTO instanceof KnConvergedClientPersistDTO) {
                KnConvergedClientPersistDTO convergedClientPersistDTO = (KnConvergedClientPersistDTO) persistDTO;
                IInputDTO inputDTO = persistDTO.getInputDTO();
                subsCorpId = convergedClientPersistDTO.getSubsCorpId();
                if (inputDTO instanceof KnIPSubscrFeatureInfoDTO) {
                    KnIPSubscrFeatureInfoDTO convergedClientDTO = (KnIPSubscrFeatureInfoDTO) inputDTO;
                    reqCorpId = convergedClientDTO.getCorpId();
                }
            } else if (persistDTO instanceof KnCorpActivationPersistDTO) {
                KnCorpActivationPersistDTO activationPersistDTO = (KnCorpActivationPersistDTO) persistDTO;
                IInputDTO inputDTO = persistDTO.getInputDTO();
                subsCorpId = activationPersistDTO.getCorpId();
                if (inputDTO instanceof KnIPCorpActivationDTO) {
                    KnIPCorpActivationDTO actDTO = (KnIPCorpActivationDTO) inputDTO;
                    reqCorpId = actDTO.getCorpId();
                }
            } else if (persistDTO instanceof KnCorpMcpttFeaturePersistDTO) {
                KnCorpMcpttFeaturePersistDTO authUserListPersistDTO = (KnCorpMcpttFeaturePersistDTO) persistDTO;
                knLogger.debug(methodName," authUserListPersistDTO: ",authUserListPersistDTO);
                IInputDTO inputDTO = persistDTO.getInputDTO();
                subsCorpId = authUserListPersistDTO.getCorpId();
                isUPMSharingEnable = authUserListPersistDTO.isUPMSharingEnabled();
                Map<String, KnCorpSubscriberDTO> targetInfo = authUserListPersistDTO.getTargetInfo();
                if (inputDTO instanceof KnIPAuthUserPermissionInfoDTO) {
                    KnIPAuthUserPermissionInfoDTO actDTO = (KnIPAuthUserPermissionInfoDTO) inputDTO;
                    knLogger.debug(methodName," actDTO:",actDTO);
                    reqCorpId = actDTO.getCorpId();
                    requestMdn = actDTO.getAuthorizedMdn();
                    if (targetInfo != null && !targetInfo.isEmpty()) {
                        int finalReqCorpId = reqCorpId;
                        requestMdnList = targetInfo.keySet();
                        Collection<String> finalDbMdnList = new ArrayList<>();
                        targetInfo.forEach((target, targetInfoDetails) -> {
                            if (finalReqCorpId == targetInfoDetails.getCorpId()) {
                                finalDbMdnList.add(target);
                            }
                        });
                        dbMdnList = finalDbMdnList;
                    }
                } else if(inputDTO instanceof KnIPEmergencyInfoDTO){
                    KnIPEmergencyInfoDTO emergencyInfoDTO = (KnIPEmergencyInfoDTO) inputDTO;
                    reqCorpId = emergencyInfoDTO.getCorpId();
                }
            } else if(persistDTO instanceof KnCorpBulkGroupInfoPersistDTO) {
                KnCorpBulkGroupInfoPersistDTO corpBulkGroupInfoPersistDTO = (KnCorpBulkGroupInfoPersistDTO) persistDTO;
                IInputDTO inputDTO = persistDTO.getInputDTO();
                subsCorpId = corpBulkGroupInfoPersistDTO.getCorpId();
                if (inputDTO instanceof KnIPSubscriberInfoDTO) {
                    KnIPSubscriberInfoDTO subsResquestDTO = (KnIPSubscriberInfoDTO) inputDTO;
                    reqCorpId = Integer.parseInt(subsResquestDTO.getCorpId());
                    requestMdn = subsResquestDTO.getToMdn();
                    Map<String, KnCorpSubscriberDTO> mdnsProfile = corpBulkGroupInfoPersistDTO.getInputMdnsProfile();
                    if (!mdnsProfile.isEmpty()) {
                        Collection<String> finalDbMdnList = new ArrayList<>();
                        mdnsProfile.forEach((mdn, value) -> {
                            if (!mdn.equals(subsResquestDTO.getToMdn()) && Integer.parseInt(subsResquestDTO.getCorpId()) != value.getCorpId()) {
                                finalDbMdnList.add(mdn);
                            }
                        });
                        dbMdnList = finalDbMdnList;
                    }
                }
            } else {
                knLogger.error("validate()", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
            if (subsCorpId != reqCorpId && !isUPMSharingEnable) {
                knLogger.debug(methodName, "Request CorpID: ", reqCorpId);
                knLogger.debug(methodName, "Database CorpID: ", subsCorpId);
                knLogger.error(methodName, "mdns not in DB for the corporate ");
                if (requestMdn != null) {
                    knLogger.error("Contacts not found in DB for the Corporate");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                            "Contacts not found in DB for the Corporate", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList(requestMdn).toString(), "");
                } else {
                    knLogger.error("Contacts not found in DB for the Corporate");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                            "Contacts not found in DB for the Corporate", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList(requestMdn).toString(), "");
                }
            }
            StringBuffer notFoundMdns = new StringBuffer(50);
            if (requestMdnList != null && !requestMdnList.isEmpty()) {
                if(dbMdnList != null) {
                    for (String mdn : requestMdnList) {
                        if (!dbMdnList.contains(mdn)) {
                            notFoundMdns = notFoundMdns.append(mdn).append(",");
                        }
                    }
                }
                if (!isNullOrEmpty(notFoundMdns.toString())) {
                    String failedMdns = notFoundMdns.substring(0, notFoundMdns.lastIndexOf(","));
                    knLogger.error(methodName, "mdns not in DB for the corporate are - ", failedMdns);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                            "Contacts not found in DB for the Corporate", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList(failedMdns).toString(), "");
                }
            }
        } finally {
            knLogger.debug(methodName, "EXIT: Validation Completed Successfully");
        }
    }
}
