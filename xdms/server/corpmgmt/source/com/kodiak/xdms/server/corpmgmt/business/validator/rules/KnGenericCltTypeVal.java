/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.commdto.request.KnCorpSubsResquestDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by nnamita on 4/21/15.
 */
public class KnGenericCltTypeVal extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnGenericCltTypeVal.class);
    private static final String ALLOWED_TYPES = "allowedTypes";


    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        String allowedType = getAttribute(ALLOWED_TYPES);
        int dispatcherClientType = 3;
        try {
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactDetailsPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", contactDetailsPersistDTO);
                String[] subTypes = contactDetailsPersistDTO.getAllowedClientTypes().split(DELIM);
                Collection<String> clientTypeList = Arrays.asList(subTypes);
                Collection<String> invalidClientTypeMdns = new ArrayList<String>();
                Collection<KnCorpSubscriberDTO> mdnDto = contactDetailsPersistDTO.getAddedMdnDTO();
                if (mdnDto != null) {
                    for (KnCorpSubscriberDTO subscriberDTO : mdnDto) {
                        if (!clientTypeList.contains(String.valueOf(subscriberDTO.getClientType()))) {
                            invalidClientTypeMdns.add(subscriberDTO.getMdn());
                        }
                    }
                    if (invalidClientTypeMdns.size() > 0) {
                        knLogger.debug(methodName, "Invalid client type Subscribers :- ", invalidClientTypeMdns);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBER_MDN_INVALID_CLIENT_TYPE,
                                "Subscriber Client type is not valid", getEntityId(),
                                getOperationType(), getRuleId(), invalidClientTypeMdns.toString(), "");
                    }
                    knLogger.debug(methodName, "Validation Completed Successfully");
                }
            } else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO groupDetailsPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", groupDetailsPersistDTO);
                String[] subTypes = allowedType.split(DELIM);
                Collection<String> clientTypeList = Arrays.asList(subTypes);
                Collection<String> invalidClientTypeMdns = new ArrayList<String>();
                Collection<KnCorpSubscriberDTO> mdnDto = groupDetailsPersistDTO.getAddedMdnDTO();
                if (mdnDto != null) {
                    for (KnCorpSubscriberDTO subscriberDTO : mdnDto) {
                        if (!clientTypeList.contains(String.valueOf(subscriberDTO.getClientType()))) {
                            invalidClientTypeMdns.add(subscriberDTO.getMdn());
                        }
                    }
                    Collection<KnCorpSubscriberDTO> sublistMemDto = groupDetailsPersistDTO.getAddedMdnDTO();
                    //added for modify group as the existing private members details present in this varible
                    if (sublistMemDto != null) {
                        for (KnCorpSubscriberDTO subscriberDTO : sublistMemDto) {
                            if (!clientTypeList.contains(String.valueOf(subscriberDTO.getClientType()))) {
                                if (!invalidClientTypeMdns.contains(subscriberDTO.getMdn())) {
                                    invalidClientTypeMdns.add(subscriberDTO.getMdn());
                                }
                            }
                        }
                    }
                    if (invalidClientTypeMdns.size() > 0) {
                        knLogger.debug(methodName, "Invalid client type Subscribers :- ", invalidClientTypeMdns);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBER_MDN_INVALID_CLIENT_TYPE,
                                "Subscriber Client type is not valid", getEntityId(),
                                getOperationType(), getRuleId(), invalidClientTypeMdns.toString(), "");
                    }
                    knLogger.debug(methodName, "Validation Completed Successfully");
                }
            } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
                KnCorpBCGrpPersistDTO bcGroupDetailsPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", bcGroupDetailsPersistDTO);
                String[] subTypes = allowedType.split(DELIM);
                Collection<String> clientTypeList = Arrays.asList(subTypes);
                Collection<String> invalidClientTypeMdns = new ArrayList<String>();
                Collection<KnCorpSubscriberDTO> mdnDto = bcGroupDetailsPersistDTO.getValidInternalCont();
                if (mdnDto != null) {
                    for (KnCorpSubscriberDTO subscriberDTO : mdnDto) {
                        if (!clientTypeList.contains(String.valueOf(subscriberDTO.getClientType()))) {
                            invalidClientTypeMdns.add(subscriberDTO.getMdn());
                        }
                    }
                    if (invalidClientTypeMdns.size() > 0) {
                        knLogger.debug(methodName, "Invalid client type Subscribers :- ", invalidClientTypeMdns);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBER_MDN_INVALID_CLIENT_TYPE,
                                "Subscriber Client type is not valid", getEntityId(),
                                getOperationType(), getRuleId(), invalidClientTypeMdns.toString(), "");
                    }
                    knLogger.debug(methodName, "Validation Completed Successfully");
                }
            } else if (persistDTO instanceof KnCorpGenActivationPersistDTO) {
                KnCorpGenActivationPersistDTO activationPersistDTO = (KnCorpGenActivationPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", activationPersistDTO);
                String[] subTypes = allowedType.split(DELIM);
                Collection<String> clientTypeList = Arrays.asList(subTypes);
                Collection<String> invalidClientTypeMdns = new ArrayList<String>();
                Collection<KnCorpSubscriberDTO> mdnDto = activationPersistDTO.getSubscriberInfoList();
                if (mdnDto != null) {
                    for (KnCorpSubscriberDTO subscriberDTO : mdnDto) {
                        if (!clientTypeList.contains(String.valueOf(subscriberDTO.getClientType()))) {
                            invalidClientTypeMdns.add(subscriberDTO.getMdn());
                        }
                        //Applicable for only webDispatcher
                        // Deprecated.
                        /*if (subscriberDTO.getClientType() == dispatcherClientType && subscriberDTO.getDispatchType() == 1) {
                            invalidClientTypeMdns.add(subscriberDTO.getMdn());
                        }*/
                    }
                    if (invalidClientTypeMdns.size() > 0) {
                        knLogger.debug(methodName, "Invalid client type Subscribers :- ", invalidClientTypeMdns);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBER_MDN_INVALID_CLIENT_TYPE,
                                "Subscriber Client type is not valid", getEntityId(),
                                getOperationType(), getRuleId(), invalidClientTypeMdns.toString(), "");
                    }
                    knLogger.debug(methodName, "Validation Completed Successfully");
                }
            } else if (persistDTO instanceof KnCorpActivationPersistDTO) {
                KnCorpActivationPersistDTO activationPersistDTO = (KnCorpActivationPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", activationPersistDTO);
                String[] subTypes = allowedType.split(DELIM);
                KnIPCorpActivationDTO inputDTO = (KnIPCorpActivationDTO) activationPersistDTO.getInputDTO();
                Collection<String> clientTypeList = Arrays.asList(subTypes);
                Collection<String> invalidClientTypeMdns = new ArrayList<String>();
                int clientType = activationPersistDTO.getClientType();
                if (!clientTypeList.contains(String.valueOf(clientType))) {
                    invalidClientTypeMdns.add(inputDTO.getMdn());
                }
                if (activationPersistDTO.getSubscriberAuthStatusList() != null) {//applicable for get mail Info
                    for (KnCorpSubscriberDTO subsc : activationPersistDTO.getSubscriberAuthStatusList()) {
                        if (!clientTypeList.contains(String.valueOf(subsc.getClientType()))) {
                            if (!invalidClientTypeMdns.contains(subsc.getMdn()))
                                invalidClientTypeMdns.add(subsc.getMdn());
                        }
                    }
                }
                //Applicable for only webDispatcher
                // Deprecated.
               /* if (activationPersistDTO.isDispatchTypeEnabled() && clientType == dispatcherClientType && activationPersistDTO.getDispatchType() == 1) {
                    invalidClientTypeMdns.add(inputDTO.getMdn());
                }*/
                if (invalidClientTypeMdns.size() > 0) {
                    knLogger.debug(methodName, "Invalid client type Subscribers :- ", invalidClientTypeMdns);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBER_MDN_INVALID_CLIENT_TYPE,
                            "Subscriber Client type is not valid", getEntityId(),
                            getOperationType(), getRuleId(), invalidClientTypeMdns.toString(), "");
                }
                knLogger.debug(methodName, "Validation Completed Successfully");
            } else if (persistDTO instanceof KnCorpTGSPersistDTO) {
                KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", tgsPersistDTO);
                String[] subTypes = allowedType.split(DELIM);
                KnIPTalkGroupDTO inputDTO = (KnIPTalkGroupDTO) tgsPersistDTO.getInputDTO();
                Collection<String> clientTypeList = Arrays.asList(subTypes);
                Collection<String> invalidClientTypeMdns = new ArrayList<String>();
                int clientType = tgsPersistDTO.getSubsClientType();
                if (!clientTypeList.contains(String.valueOf(clientType))) {
                    invalidClientTypeMdns.add(inputDTO.getMdn());
                }
                if (invalidClientTypeMdns.size() > 0) {
                    knLogger.debug(methodName, "Invalid client type Subscribers :- ", invalidClientTypeMdns);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBER_MDN_INVALID_CLIENT_TYPE,
                            "Subscriber Client type is not valid", getEntityId(),
                            getOperationType(), getRuleId(), invalidClientTypeMdns.toString(), "");
                }
                knLogger.debug(methodName, "Validation Completed Successfully");
            } else if (persistDTO instanceof KnCorpMcpttFeaturePersistDTO) {
                KnCorpMcpttFeaturePersistDTO authUserListPersistDTO = (KnCorpMcpttFeaturePersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", authUserListPersistDTO);
                IInputDTO inputDTO = persistDTO.getInputDTO();
                String[] subTypes = allowedType.split(DELIM);
                Collection<String> clientTypeList = Arrays.asList(subTypes);
                Collection<String> invalidClientTypeMdns = new ArrayList<String>();
                if (inputDTO instanceof KnIPAuthUserPermissionInfoDTO) {
                    KnIPAuthUserPermissionInfoDTO authUserPermissionInfoDTO = (KnIPAuthUserPermissionInfoDTO) inputDTO;
                    int clientType = authUserListPersistDTO.getSubsClientType();
                    Map<String, KnCorpSubscriberDTO> targetInfo = authUserListPersistDTO.getTargetInfo();
                    if (authUserPermissionInfoDTO.getAuthorizedMdn() != null && !clientTypeList.contains(String.valueOf(clientType))) {
                        invalidClientTypeMdns.add(authUserPermissionInfoDTO.getAuthorizedMdn());
                    } else if (authUserPermissionInfoDTO.getTargetMdn() != null && (!clientTypeList.contains(String.valueOf(clientType))
                            || dispatcherClientType == clientType)) {
                        invalidClientTypeMdns.add(authUserPermissionInfoDTO.getTargetMdn());
                    }
                    if (targetInfo != null && !targetInfo.isEmpty()) {
                        targetInfo.forEach((target, targetInfoDetails) -> {
                            if (!clientTypeList.contains(String.valueOf(targetInfoDetails.getClientType()))) {
                                invalidClientTypeMdns.add(target);
                            }
                            if (dispatcherClientType == targetInfoDetails.getClientType()) {
                                invalidClientTypeMdns.add(target);
                            }
                        });
                    }
                } else if (inputDTO instanceof KnIPEmergencyInfoDTO) {
                    KnIPEmergencyInfoDTO emergencyInfoDTO = (KnIPEmergencyInfoDTO) inputDTO;
                    int clientType = authUserListPersistDTO.getSubsClientType();
                    if (!clientTypeList.contains(String.valueOf(clientType))) {
                        invalidClientTypeMdns.add(emergencyInfoDTO.getMdn());
                    }
                }
                if (invalidClientTypeMdns.size() > 0) {
                    knLogger.debug(methodName, "Invalid client type Subscribers :- ", invalidClientTypeMdns);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBER_MDN_INVALID_CLIENT_TYPE,
                            "Subscriber Client type is not valid", getEntityId(),
                            getOperationType(), getRuleId(), invalidClientTypeMdns.toString(), "");
                }
                knLogger.debug(methodName, "Validation Completed Successfully");
            } else if (persistDTO instanceof KnCorpBulkGroupInfoPersistDTO) {
                KnCorpBulkGroupInfoPersistDTO bulkGroupInfoPersistDTO = (KnCorpBulkGroupInfoPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", bulkGroupInfoPersistDTO);
                String[] subTypes = allowedType.split(DELIM);
                Collection<String> clientTypeList = Arrays.asList(subTypes);
                Map<String, KnCorpSubscriberDTO> mdnsProfile = bulkGroupInfoPersistDTO.getInputMdnsProfile();
                Collection<String> invalidClientTypeMdns = mdnsProfile.entrySet().stream().filter(subsMap -> !clientTypeList
                        .contains(String.valueOf(subsMap.getValue().getClientType()))).map(Map.Entry::getKey).collect(Collectors.toList());
                if (invalidClientTypeMdns.size() > 0) {
                    knLogger.debug(methodName, "Invalid client type Subscribers :- ", invalidClientTypeMdns);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBER_MDN_INVALID_CLIENT_TYPE,
                            "Subscriber Client type is not valid", getEntityId(),
                            getOperationType(), getRuleId(), invalidClientTypeMdns.toString(), "");
                }
                knLogger.debug(methodName, "Validation Completed Successfully");
            } else if(persistDTO instanceof KnCorpUserProfilePersistDTO){
                KnCorpUserProfilePersistDTO upmDto=(KnCorpUserProfilePersistDTO)persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", upmDto);
                int clientType = upmDto.getClientType();
                String[] subTypes = allowedType.split(DELIM);
                Collection<String> clientTypeList = Arrays.asList(subTypes);
                Collection<String> invalidClientTypeMdns = new ArrayList<String>();
                String baseMdn=((KnIPUserProfileDTO)upmDto.getInputDTO()).getMdn();
                if (!clientTypeList.contains(String.valueOf(clientType))) {
                    invalidClientTypeMdns.add(baseMdn);
                }
                if (invalidClientTypeMdns.size() > 0) {
                    knLogger.debug(methodName, "Invalid client type Subscriber :- ", invalidClientTypeMdns);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                            "Subscriber Client type is not valid", getEntityId(),
                            getOperationType(), getRuleId(), invalidClientTypeMdns.toString(), "");
                }
                knLogger.debug(methodName, "Validation Completed Successfully");

            }
        } finally {
            knLogger.debug(methodName, "Exit Point");
        }
    }
}

