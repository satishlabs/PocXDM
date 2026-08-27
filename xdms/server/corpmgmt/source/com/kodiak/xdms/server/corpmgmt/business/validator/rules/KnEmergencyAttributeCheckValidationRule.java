/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscCloningListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPEmergencyInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCloningPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMcpttFeaturePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static com.kodiak.common.resources.KnConstants.ENABLED;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.BROADCAST_GROUP;

/**
 * ************************************************************************
 * <p>
 * File name:  KnEmergencyAttributeCheckValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Jan 23, 2018                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnEmergencyAttributeCheckValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnEmergencyAttributeCheckValidationRule.class);

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpMcpttFeaturePersistDTO) {
            IInputDTO inputDTO = persistDTO.getInputDTO();
            if (inputDTO instanceof KnIPEmergencyInfoDTO) {
                KnCorpMcpttFeaturePersistDTO mcpttFeaturePersistDTO = (KnCorpMcpttFeaturePersistDTO) persistDTO;
                KnIPEmergencyInfoDTO ipEmergencyInfoDTO = (KnIPEmergencyInfoDTO) inputDTO;
                knLogger.debug(methodName, "ipEmergencyInfoDTO - ", ipEmergencyInfoDTO);
                Collection<String> destinations = mcpttFeaturePersistDTO.getDestinations();
                if (ipEmergencyInfoDTO.getEmergInitPermission().equals(ENABLED)
                        && KnConstants.DESTINATION_TYPE.CAT_CONFIGURED_DESTINATION.value() == ipEmergencyInfoDTO.getEmergDestType() ) {
                    String primaryDestination = ipEmergencyInfoDTO.getPriDestination();
                    if (primaryDestination == null) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.PRIMARY_DESTINATION_IS_EMPTY,
                                "primary destination must be set for destType 2 --", getEntityId(),
                                getOperationType(), getRuleId(), Arrays.asList("").toString(), "");
                    } else {
                        Map<String, Integer> groupTypeMap = mcpttFeaturePersistDTO.getGroupTypeMap();
                        Map<String, String> mdnExistenceInGroup = mcpttFeaturePersistDTO.getMdnExistenceInGroup();
                        Map<String, Collection<String>> mdnContactList = mcpttFeaturePersistDTO.getSubsContactList();
                        Collection<String> contactList = null;
                        if (mdnContactList != null) {
                            contactList = mdnContactList.get(mcpttFeaturePersistDTO.getMdn());
                        }
                        knLogger.debug(methodName, "contactList -", contactList, "groupTypeMap -", groupTypeMap,
                                "mdnExistenceInGroup -", mdnExistenceInGroup);
                        Collection<String> broadcastGroup = new ArrayList<>();
                        Collection<String> groupsWhereMdnDoesNotExists = new ArrayList<>();
                        Collection<Integer> groupDoesNotExists = new ArrayList<>();
                        Collection<String> contactDoesNotExists = new ArrayList<>();
                        Collection<String> contactNotExists = new ArrayList<>();
                        for (String inputDest : destinations) {
                            if (inputDest != null) {
                                if (groupTypeMap != null && groupTypeMap.containsKey(inputDest)) {
                                    if (groupTypeMap.get(inputDest) == BROADCAST_GROUP) {
                                        broadcastGroup.add(inputDest);
                                    } else {
                                        if (!mdnExistenceInGroup.containsKey(inputDest)) {
                                            groupsWhereMdnDoesNotExists.add(inputDest);
                                        }
                                    }
                                } else if (contactList != null && !contactList.contains(inputDest)) {
                                    contactDoesNotExists.add(inputDest);
                                } else if (contactList == null) {
                                    contactDoesNotExists.add(inputDest);
                                }
                            }
                        }

                        for (String contactNotExist : contactDoesNotExists) {
                            try {
                                int dest = Integer.parseInt(contactNotExist);
                                groupDoesNotExists.add(dest);
                            } catch (NumberFormatException e) {
                                knLogger.debug(methodName, "dest is MDN", contactNotExist);
                                contactNotExists.add(contactNotExist);
                            }
                        }

                        if (!broadcastGroup.isEmpty()) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.BROADCAST_GROUP_NOT_ALLOWED_AS_DESTINATIONS,
                                    "Broadcast group not allowed for destination --", getEntityId(),
                                    getOperationType(), getRuleId(), Arrays.asList(broadcastGroup).toString(), "");
                        }
                        if (!groupsWhereMdnDoesNotExists.isEmpty()) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBS_DOES_NOT_EXISTS_IN_PROVIDED_DESTINATION_GROUP,
                                    "Provided destination do not have requested MDN as a group member --", getEntityId(),
                                    getOperationType(), getRuleId(), Arrays.asList(groupsWhereMdnDoesNotExists).toString(), "");
                        }
                        if (!groupDoesNotExists.isEmpty()) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_DOES_NOT_EXIST,
                                    "Group does not exists --", getEntityId(),
                                    getOperationType(), getRuleId(), Arrays.asList(groupDoesNotExists).toString(), "");
                        }
                        if (!contactNotExists.isEmpty()) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.DESTINATION_NOT_BELONGS_TO_SUBS_CONTACT_LIST,
                                    "Destination does not belongs to subs contact list --", getEntityId(),
                                    getOperationType(), getRuleId(), Arrays.asList(contactNotExists).toString(), "");
                        }
                    }
                }
            }
        } else if (persistDTO instanceof KnCloningPersistDTO) {
            KnCloningPersistDTO cloningPersistDTO = (KnCloningPersistDTO) persistDTO;
            if (cloningPersistDTO.getValidationMap().containsKey(KnConstants.CLONING_VALIDATION_BIT.EMERGENCY_ARRBT.value()) && cloningPersistDTO.getValidationMap().get(KnConstants.CLONING_VALIDATION_BIT.EMERGENCY_ARRBT.value())) {
                IInputDTO inputDTO = cloningPersistDTO.getInputDTO();
                if (inputDTO instanceof KnIPCorpSubscCloningListDTO) {
                    KnCorpMcpttFeaturePersistDTO mcpttFeaturePersistDTO = ((KnCloningPersistDTO) persistDTO).getEmergencyMcpttFeaturePersistDTO();
                    KnIPEmergencyInfoDTO ipEmergencyInfoDTO = ((KnIPCorpSubscCloningListDTO) inputDTO).getIpEmergencyInfoDTO();
                    knLogger.debug(methodName, "ipEmergencyInfoDTO - ", ipEmergencyInfoDTO);
                    Collection<String> destinations = mcpttFeaturePersistDTO.getDestinations();
                    if (ipEmergencyInfoDTO.getEmergInitPermission().equals(ENABLED)
                            && KnConstants.DESTINATION_TYPE.CAT_CONFIGURED_DESTINATION.value() == ipEmergencyInfoDTO.getEmergDestType()) {
                        String primaryDestination = ipEmergencyInfoDTO.getPriDestination();
                        if (primaryDestination == null) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.PRIMARY_DESTINATION_IS_EMPTY,
                                    "primary destination must be set for destType 2 --", getEntityId(),
                                    getOperationType(), getRuleId(), Arrays.asList("").toString(), "");
                        } else {
                            Map<String, Integer> groupTypeMap = mcpttFeaturePersistDTO.getGroupTypeMap();
                            Map<String, String> mdnExistenceInGroup = mcpttFeaturePersistDTO.getMdnExistenceInGroup();
                            Map<String, Collection<String>> mdnContactList = mcpttFeaturePersistDTO.getSubsContactList();
                            Collection<String> contactList = null;
                            if (mdnContactList != null) {
                                contactList = mdnContactList.get(mcpttFeaturePersistDTO.getMdn());
                            }
                            knLogger.debug(methodName, "contactList -", contactList, "groupTypeMap -", groupTypeMap,
                                    "mdnExistenceInGroup -", mdnExistenceInGroup);
                            Collection<String> broadcastGroup = new ArrayList<>();
                            Collection<String> groupsWhereMdnDoesNotExists = new ArrayList<>();
                            Collection<Integer> groupDoesNotExists = new ArrayList<>();
                            Collection<String> contactDoesNotExists = new ArrayList<>();
                            Collection<String> contactNotExists = new ArrayList<>();
                            for (String inputDest : destinations) {
                                if (inputDest != null) {
                                    if (groupTypeMap != null && groupTypeMap.containsKey(inputDest)) {
                                        if (groupTypeMap.get(inputDest) == BROADCAST_GROUP) {
                                            broadcastGroup.add(inputDest);
                                        } else {
                                            if (!mdnExistenceInGroup.containsKey(inputDest)) {
                                                groupsWhereMdnDoesNotExists.add(inputDest);
                                            }
                                        }
                                    } else if (contactList != null && !contactList.contains(inputDest)) {
                                        contactDoesNotExists.add(inputDest);
                                    } else if (contactList == null) {
                                        contactDoesNotExists.add(inputDest);
                                    }
                                }
                            }

                            for (String contactNotExist : contactDoesNotExists) {
                                try {
                                    int dest = Integer.parseInt(contactNotExist);
                                    groupDoesNotExists.add(dest);
                                } catch (NumberFormatException e) {
                                    knLogger.debug(methodName, "dest is MDN", contactNotExist);
                                    contactNotExists.add(contactNotExist);
                                }
                            }

                            if (!broadcastGroup.isEmpty()) {
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.BROADCAST_GROUP_NOT_ALLOWED_AS_DESTINATIONS,
                                        "Broadcast group not allowed for destination --", getEntityId(),
                                        getOperationType(), getRuleId(), Arrays.asList(broadcastGroup).toString(), "");
                            }
                            if (!groupsWhereMdnDoesNotExists.isEmpty()) {
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBS_DOES_NOT_EXISTS_IN_PROVIDED_DESTINATION_GROUP,
                                        "Provided destination do not have requested MDN as a group member --", getEntityId(),
                                        getOperationType(), getRuleId(), Arrays.asList(groupsWhereMdnDoesNotExists).toString(), "");
                            }
                            if (!groupDoesNotExists.isEmpty()) {
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_DOES_NOT_EXIST,
                                        "Group does not exists --", getEntityId(),
                                        getOperationType(), getRuleId(), Arrays.asList(groupDoesNotExists).toString(), "");
                            }
                            if (!contactNotExists.isEmpty()) {
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.DESTINATION_NOT_BELONGS_TO_SUBS_CONTACT_LIST,
                                        "Destination does not belongs to subs contact list --", getEntityId(),
                                        getOperationType(), getRuleId(), Arrays.asList(contactNotExists).toString(), "");
                            }
                        }
                    }
                }
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "EXIT: Validation Completed Successfully for emergency Attributes");
    }
}
