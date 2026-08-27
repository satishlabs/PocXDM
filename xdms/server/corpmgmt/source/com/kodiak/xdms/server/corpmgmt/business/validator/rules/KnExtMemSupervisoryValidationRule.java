/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Collection;

public class KnExtMemSupervisoryValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnExtMemSupervisoryValidationRule.class);

    public void validate() throws KnValidationException {

        final String methodName = "KnExtMemSupervisoryValidationRule validate()";
        knLogger.info(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
                Collection<KnCorpGroupMemberDTO> groupSupervisor = inputDTO.getGroupSupervisor();
                Collection<KnCorpGroupMemberDTO> addedSupervisor = inputDTO.getAddedMdnSupervisor();
                Collection<KnCorpSubscriberDTO> externalContacts = corpGroupInfoPersistDTO.getExternalContacts();
                knLogger.debug(methodName, "groupSupervisor, addedSupervisor, externalContacts - ", groupSupervisor, addedSupervisor, externalContacts);
                if (externalContacts != null && !externalContacts.isEmpty()) {
                    if (groupSupervisor != null && !groupSupervisor.isEmpty()) {
                        for (KnCorpSubscriberDTO subsc : externalContacts) {
                            knLogger.debug(methodName, "subsc - ", subsc);
                            if (groupSupervisor.contains(subsc)) {
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.EXTERNAL_CONTACT_CANNOT_BE_SUPERVISOR,
                                        "External Subscriber cannot be supervisor. - " + subsc, getEntityId(), getOperationType(), getRuleId(),
                                        "DataType", "");
                            }
                        }
                    }
                    if (addedSupervisor != null && !addedSupervisor.isEmpty()) {
                        for (KnCorpSubscriberDTO subsc : externalContacts) {
                            if (addedSupervisor.contains(subsc)) {
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.EXTERNAL_CONTACT_CANNOT_BE_SUPERVISOR,
                                        "External Subscriber cannot be supervisor. - " + subsc, getEntityId(), getOperationType(), getRuleId(),
                                        "DataType", "");
                            }
                        }
                    }
                }
            }
            knLogger.debug(methodName, "Validation Completed Successfully");
        } finally {
            knLogger.debug(methodName, "Exit Point");
        }
    }
}
