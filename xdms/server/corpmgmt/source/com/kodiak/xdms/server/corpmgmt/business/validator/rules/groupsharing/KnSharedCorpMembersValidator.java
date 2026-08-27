/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupsharing;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KnSharedCorpMembersValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSharedCorpMembersValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        List<String> invalidMdn = new ArrayList<String>();
        List<String> removedMdn = new ArrayList<String>();
        KnIPCorpGroupInfoDTO groupInfoDTO = null;
        Map<String, KnCorpGroupMemberDTO> existingMembers = null;
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            knLogger.debug(methodName, "DTO passed in the request is - ", groupPersistDTO);
            if (groupPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO) {
                groupInfoDTO = (KnIPCorpGroupInfoDTO) groupPersistDTO.getInputDTO();
            }
            existingMembers = groupPersistDTO.getExistingGrpMemDetail().stream().
                    collect(Collectors.toMap(KnCorpGroupMemberDTO::getMdn, v -> v));
        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            knLogger.debug(methodName, "DTO passed in the request is - ", bcGrpPersistDTO);
            if (bcGrpPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO) {
                groupInfoDTO = (KnIPCorpGroupInfoDTO) bcGrpPersistDTO.getInputDTO();
            }
            existingMembers = bcGrpPersistDTO.getExistingGrpMemDetail().stream().
                    collect(Collectors.toMap(KnCorpGroupMemberDTO::getMdn, v -> v));
        }

        if (groupInfoDTO !=null && !groupInfoDTO.isOwnerCorpReq()) {
            knLogger.debug(methodName, "groupInfoDTO.getRemovedMemberMdns() - ", groupInfoDTO.getRemovedMemberMdns());
            if (groupInfoDTO.getRemovedMemberMdns() != null) {
                removedMdn = new ArrayList<>(groupInfoDTO.getRemovedMemberMdns());
            }
            knLogger.debug(methodName, "existingMembers - ", existingMembers);
            for (String mdn : removedMdn) {
                if (existingMembers.get(mdn) != null && existingMembers.get(mdn).getCorpId() != groupInfoDTO.getCorpId()) {
                    invalidMdn.add(mdn);
                }
            }
        }

        knLogger.debug(methodName, "invalidMdn - ", invalidMdn);
        if (!invalidMdn.isEmpty()) {
            knLogger.error(methodName, "Members not part of corporate - ", invalidMdn);
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                    "MDN not part of corporate", getEntityId(), getOperationType(), getRuleId(),
                    invalidMdn.toString(), "");
        }
    }
}
