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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnSharedExternalGrpMemberValidation extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSharedExternalGrpMemberValidation.class);
    List<String> invalidMDN = new ArrayList<>();
    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */


    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        Integer requestCorpId=null;
        Integer groupCorpId=null;
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            Collection<KnCorpSubscriberDTO> mdnList=null;
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            knLogger.debug("DTO passed to the validator-->"+groupPersistDTO);
            mdnList = groupPersistDTO.getAddedMdnDTO();
            groupCorpId = groupPersistDTO.getCorpId();
            KnIPCorpGroupInfoDTO inputDTO=null;
            if(groupPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO){
                inputDTO= (KnIPCorpGroupInfoDTO) groupPersistDTO.getInputDTO();
                requestCorpId = inputDTO.getCorpId();
            }
            knLogger.debug("List of Mdns to be added to the group-->"+mdnList);
            if(!inputDTO.isOwnerCorpReq() && mdnList !=null){
                for(KnCorpSubscriberDTO subscriberDTO : mdnList){
                    if(subscriberDTO.isExternalContact())
                        invalidMDN.add(subscriberDTO.getMdn());
                }
            }
            if (invalidMDN.isEmpty() && mdnList != null && inputDTO.getRequestingHierarchyId() != null) {
                if (isSharedCorpByHierarchy(inputDTO.getRequestingHierarchyId(), groupPersistDTO.getOwnerHierarchyId())) {
                    for (KnCorpSubscriberDTO subscriberDTO : mdnList) {
                        if (subscriberDTO.isExternalContact())
                            invalidMDN.add(subscriberDTO.getMdn());
                    }
                }
            }
        } else if(persistDTO instanceof KnCorpBCGrpPersistDTO){
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            knLogger.debug("DTO passed to the validator-->"+bcGrpPersistDTO);
            Collection<KnCorpContactDTO> bcMdnList = bcGrpPersistDTO.getAddedMemberMdnsDTOLst();
            groupCorpId = bcGrpPersistDTO.getCorpId();
            KnIPCorpGroupInfoDTO inputDTO = null;
            if(bcGrpPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO){
                inputDTO = (KnIPCorpGroupInfoDTO) bcGrpPersistDTO.getInputDTO();
                requestCorpId = inputDTO.getCorpId();
            }
            knLogger.debug("List of Mdns to be added to the group-->"+bcMdnList);
            if((!inputDTO.isOwnerCorpReq()) && bcMdnList !=null){
                for(KnCorpContactDTO subscriberDTO : bcMdnList){
                    if(subscriberDTO.isExternalContact())
                        invalidMDN.add(subscriberDTO.getMdn());
                }
            }
            if (invalidMDN.isEmpty() && bcMdnList != null && inputDTO.getRequestingHierarchyId() != null) {
                if (isSharedCorpByHierarchy(inputDTO.getRequestingHierarchyId(), bcGrpPersistDTO.getOwnerHierarchyId())) {
                    for (KnCorpContactDTO subscriberDTO : bcMdnList) {
                        if (subscriberDTO.isExternalContact())
                            invalidMDN.add(subscriberDTO.getMdn());
                    }
                }
            }
        }
        knLogger.debug("invalidMDN-->"+invalidMDN);
        if(!invalidMDN.isEmpty()) {
            knLogger.debug(methodName, "External contact from the shared corp cannot be added to the group");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.EXTERNAL_CONTACT_NOT_ALLOWED_FOR_SHARED_CORP,
                    "Shared corp cannot add external contact to the group", getEntityId(),
                    getOperationType(), getRuleId(), invalidMDN.toString(), "");
        }
    }

    /**
     * Returns true if the requestingHierarchyId does NOT match the group's owner hierarchyId,
     * meaning the requester is a shared corp (not the owner).
     */
    private boolean isSharedCorpByHierarchy(String requestingHierarchyId, String groupHierarchyId) {
        final String methodName = "isSharedCorpByHierarchy()";
        if (groupHierarchyId == null || groupHierarchyId.trim().isEmpty()) {
            knLogger.debug(methodName, "groupHierarchyId is null/empty — skipping shared corp check");
            return false;
        }
        try {
            boolean isShared = Integer.parseInt(requestingHierarchyId.trim()) != Integer.parseInt(groupHierarchyId.trim());
            knLogger.debug(methodName, "requestingHierarchyId=", requestingHierarchyId, " ownerHierarchyId=", groupHierarchyId, " isSharedCorp=", isShared);
            return isShared;
        } catch (NumberFormatException e) {
            knLogger.debug(methodName, "Could not parse hierarchy ID as integer, skipping check");
            return false;
        }
    }

}
