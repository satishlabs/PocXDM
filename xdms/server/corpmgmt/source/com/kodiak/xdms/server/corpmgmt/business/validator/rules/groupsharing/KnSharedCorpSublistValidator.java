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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;

public class KnSharedCorpSublistValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSharedCorpSublistValidator.class);

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
        Collection<Integer> sublistIds = new ArrayList<>();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            if(groupPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO){
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) groupPersistDTO.getInputDTO();
                knLogger.debug(methodName, "inputDTO.isOwnerCorpReq() ", inputDTO.isOwnerCorpReq(), "AddedSublist", inputDTO.getAddedSublistIds());
                knLogger.debug(methodName, "Requesting HierarchyId ", inputDTO.getRequestingHierarchyId(), "OwnerHierarchyId", groupPersistDTO.getOwnerHierarchyId());
                if(!inputDTO.isOwnerCorpReq()){
                    knLogger.debug(methodName, "Added sublist - ", inputDTO.getAddedSublistIds());
                    knLogger.debug(methodName, "removed sublist - ", inputDTO.getRemovedSublistIds());
                    if(inputDTO.getAddedSublistIds() != null) sublistIds.addAll(inputDTO.getAddedSublistIds());
                    if(inputDTO.getRemovedSublistIds() != null) sublistIds.addAll(inputDTO.getRemovedSublistIds());
                } else if (null != groupPersistDTO.getOwnerHierarchyId() && !groupPersistDTO.getOwnerHierarchyId().equals(inputDTO.getRequestingHierarchyId())) {
                    if (inputDTO.getAddedSublistIds() != null) sublistIds.addAll(inputDTO.getAddedSublistIds());
                    if (inputDTO.getRemovedSublistIds() != null) sublistIds.addAll(inputDTO.getRemovedSublistIds());
                }
            }
        }else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            if(bcGrpPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO){
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) bcGrpPersistDTO.getInputDTO();
                knLogger.debug(methodName, "inputDTO.isOwnerCorpReq() ", inputDTO.isOwnerCorpReq());
                knLogger.debug(methodName, "Requesting HierarchyId ", inputDTO.getRequestingHierarchyId(), "OwnerHierarchyId", bcGrpPersistDTO.getOwnerHierarchyId());
                if(!inputDTO.isOwnerCorpReq()){
                    knLogger.debug(methodName, "Added sublist - ", inputDTO.getAddedSublistIds());
                    knLogger.debug(methodName, "removed sublist - ", inputDTO.getRemovedSublistIds());
                    if(inputDTO.getAddedSublistIds() != null) sublistIds.addAll(inputDTO.getAddedSublistIds());
                    if(inputDTO.getRemovedSublistIds() != null) sublistIds.addAll(inputDTO.getRemovedSublistIds());
                } else if (null != bcGrpPersistDTO.getOwnerHierarchyId() && !bcGrpPersistDTO.getOwnerHierarchyId().equals(inputDTO.getRequestingHierarchyId())) {
                    if (inputDTO.getAddedSublistIds() != null) sublistIds.addAll(inputDTO.getAddedSublistIds());
                    if (inputDTO.getRemovedSublistIds() != null) sublistIds.addAll(inputDTO.getRemovedSublistIds());
                }
            }
        }

        knLogger.debug(methodName, "sublistIds - ", sublistIds);
        if(!sublistIds.isEmpty()){
            knLogger.error(methodName, "Sublists not allowed from shared corporate");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_SHARED_SUBLIST_ADD_SHARED_CORP_NOT_ALLOWED,
                    "Sublists not allowed from shared corporate", getEntityId(),
                    getOperationType(), getRuleId(), sublistIds.toString(), "");
        }
    }
}
