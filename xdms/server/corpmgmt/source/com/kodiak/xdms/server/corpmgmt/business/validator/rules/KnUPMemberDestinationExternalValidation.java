/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.commdto.common.KnXDMEmergencyDestAttributes;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDestinationAttributeDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

public class KnUPMemberDestinationExternalValidation extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnUPMemberDestinationExternalValidation.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            Set<KnDestinationAttributeDTO> attributes = new HashSet<>();
            if (userProfilePersistDTO.getUserProfileDTO() != null &&
                    userProfilePersistDTO.getUserProfileDTO().getEmergencyConfig() != null &&
                    userProfilePersistDTO.getUserProfileDTO().getEmergencyConfig().getDestAttributes() != null &&
                    !userProfilePersistDTO.getUserProfileDTO().getEmergencyConfig().getDestAttributes().isEmpty()) {

                attributes = userProfilePersistDTO.getUserProfileDTO().getEmergencyConfig().getDestAttributes();

            } else if (userProfilePersistDTO.getModifyUserProfileDTO() != null &&
                    userProfilePersistDTO.getModifyUserProfileDTO().getEmergencyConfig() != null &&
                    userProfilePersistDTO.getModifyUserProfileDTO().getEmergencyConfig().getDestAttributes() != null &&
                    !userProfilePersistDTO.getModifyUserProfileDTO().getEmergencyConfig().getDestAttributes().isEmpty()) {

                attributes = userProfilePersistDTO.getModifyUserProfileDTO().getEmergencyConfig().getDestAttributes();
            }
            boolean externalDestination = false;
            if (attributes != null && !attributes.isEmpty()) {
                for (KnDestinationAttributeDTO destAttr : attributes) {
                    if(destAttr.getDestType() == null) {
                        continue;
                    }
                    if (destAttr.getDestType() == 2 && null != destAttr.getDestURI()) {
                        Map<String, Integer> subslistMemCorpInfo = userProfilePersistDTO.getSubslistMemCorpInfo();
                        String destURI = destAttr.getDestURI();
                        if (null != subslistMemCorpInfo && !subslistMemCorpInfo.isEmpty()) {
                            if (subslistMemCorpInfo.containsKey(destURI.trim()) &&
                                    subslistMemCorpInfo.get(destURI) != Integer.parseInt(userProfilePersistDTO.getCorpId())) {
                                externalDestination = Boolean.TRUE;
                                break;
                            }
                        }
                    }
                    if (destAttr.getDestType() == 1 && null != destAttr.getDestURI()) {
                        Collection<KnCorpGroupInfoPersistDTO> groupList = userProfilePersistDTO.getCorpGroupList();
                        if(groupList!=null && !groupList.isEmpty()) {
                            for (KnCorpGroupInfoPersistDTO group : groupList) {
                                if (destAttr.getDestURI().equals(String.valueOf(group.getGroupId())) &&
                                        group.getCorpId() != Integer.parseInt(userProfilePersistDTO.getCorpId())) {
                                    externalDestination = Boolean.TRUE;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (externalDestination) {
                knLogger.error(methodName, "External member or Shared Group are not allowed as destination ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.EXTERNAL_GROUP_CONTACT_DESTINATION_NOT_ALLOWED,
                        "", getEntityId(), getOperationType(), getRuleId(), "", "");
            }
        }
    }
}
