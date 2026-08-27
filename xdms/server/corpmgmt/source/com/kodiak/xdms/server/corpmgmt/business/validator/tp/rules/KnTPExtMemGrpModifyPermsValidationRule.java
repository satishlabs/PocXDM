/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnTPExtMemGrpModifyPermsValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar        30-07-2018      9.0+
 * <p/>
 * <p/>
 * 
 * 
 * KODIAK, 9th Floor, 'MFar
 * Manyata Tech Park' Greenheart Phase IV,
 * Nagawara Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

package com.kodiak.xdms.server.corpmgmt.business.validator.tp.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.DYNAMIC_CGMT_INTF;
import static com.kodiak.common.resources.KnConstants.ENABLED;

public class KnTPExtMemGrpModifyPermsValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnTPExtMemGrpModifyPermsValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        Collection<String> extContactWithModifyPerms = new ArrayList<>();
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
                KnCorpGroupInfoPersistDTO groupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                Collection<KnCorpSubscriberDTO> dbExternalContacts = groupInfoPersistDTO.getExternalContacts();
                KnIPCorpGroupInfoDTO ipCorpGroupInfoDTO = (KnIPCorpGroupInfoDTO) groupInfoPersistDTO.getInputDTO();
                if (DYNAMIC_CGMT_INTF == ipCorpGroupInfoDTO.getClientType()) {
                    Collection<KnCorpContactDTO> ipAddedMembers = ipCorpGroupInfoDTO.getAddedMemberDTOMdns();
                    Collection<KnCorpGroupMemberDTO> ipModifyMembers = ipCorpGroupInfoDTO.getModifiedMembers();
                    Map<String, Integer> inputExtSubsGrpModifyPerEnabled = new HashMap<>();
                    if (ipAddedMembers != null) {
                        inputExtSubsGrpModifyPerEnabled.putAll(ipAddedMembers.stream()
                                .filter(addedMembers -> (addedMembers.getGroupModifyPerm() != null && ENABLED == addedMembers.getGroupModifyPerm()))
                                .collect(Collectors.toMap(KnCorpContactDTO::getMdn, KnCorpContactDTO::getGroupModifyPerm)));
                    }
                    if (ipModifyMembers != null) {
                        inputExtSubsGrpModifyPerEnabled.putAll(ipModifyMembers.stream()
                                .filter(modifiedMembers -> (modifiedMembers.getGroupModifyPerm() != null && ENABLED == modifiedMembers.getGroupModifyPerm()))
                                .collect(Collectors.toMap(KnCorpGroupMemberDTO::getMdn, KnCorpGroupMemberDTO::getGroupModifyPerm)));
                    }
                    knLogger.debug(methodName, "ipAddedMembers-- ", ipAddedMembers, "ipModifyMembers--", ipModifyMembers);
                    if (dbExternalContacts != null && !dbExternalContacts.isEmpty()) {
                        extContactWithModifyPerms = dbExternalContacts.stream()
                                .filter(dbExt -> inputExtSubsGrpModifyPerEnabled.containsKey(dbExt.getMdn()))
                                .map(KnCorpSubscriberDTO::getMdn).collect(Collectors.toList());
                    }
                    knLogger.debug(methodName, "extContactWithModifyPerms-- ", extContactWithModifyPerms);
                }
            } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
                knLogger.debug(methodName, "persistDTO is instanceof KnCorpBCGrpPersistDTO - ", persistDTO);
                KnCorpBCGrpPersistDTO groupInfoPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
                Collection<KnCorpSubscriberDTO> dbExternalContacts = groupInfoPersistDTO.getValidExtContacts();
                KnIPCorpGroupInfoDTO ipCorpGroupInfoDTO = (KnIPCorpGroupInfoDTO) groupInfoPersistDTO.getInputDTO();
                if (DYNAMIC_CGMT_INTF == ipCorpGroupInfoDTO.getClientType()) {
                    Collection<KnCorpContactDTO> ipAddedMembers = ipCorpGroupInfoDTO.getAddedMemberDTOMdns();
                    Collection<KnCorpGroupMemberDTO> ipModifyMembers = ipCorpGroupInfoDTO.getModifiedMembers();
                    Map<String, Integer> inputExtSubsGrpModifyPerEnabled = new HashMap<>();
                    if (ipAddedMembers != null) {
                        inputExtSubsGrpModifyPerEnabled.putAll(ipAddedMembers.stream()
                                .filter(addedMembers -> (addedMembers.getGroupModifyPerm() != null && ENABLED == addedMembers.getGroupModifyPerm()))
                                .collect(Collectors.toMap(KnCorpContactDTO::getMdn, KnCorpContactDTO::getGroupModifyPerm)));
                    }
                    if (ipModifyMembers != null) {
                        inputExtSubsGrpModifyPerEnabled.putAll(ipModifyMembers.stream()
                                .filter(modifiedMembers -> (modifiedMembers.getGroupModifyPerm() != null && ENABLED == modifiedMembers.getGroupModifyPerm()))
                                .collect(Collectors.toMap(KnCorpGroupMemberDTO::getMdn, KnCorpGroupMemberDTO::getGroupModifyPerm)));
                    }
                    knLogger.debug(methodName, "ipAddedMembers-- ", ipAddedMembers, "ipModifyMembers--", ipModifyMembers);
                    if (dbExternalContacts != null && !dbExternalContacts.isEmpty()) {
                        extContactWithModifyPerms = dbExternalContacts.stream()
                                .filter(dbExt -> inputExtSubsGrpModifyPerEnabled.containsKey(dbExt.getMdn()))
                                .map(KnCorpSubscriberDTO::getMdn).collect(Collectors.toList());
                    }
                    knLogger.debug(methodName, "extContactWithModifyPerms-- ", extContactWithModifyPerms);
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnIPCorpGroupInfoDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
            if (!extContactWithModifyPerms.isEmpty()) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_MODIFY_PERMISSION_ENABLED_FOR_EXTERNAL_CONTACT,
                        "Group Modify permission is enabled for external contact", getEntityId(),
                        getOperationType(), getRuleId(), extContactWithModifyPerms.toString(), "");
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Completed Successfully");
        }
    }
}


