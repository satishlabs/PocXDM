/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupsharing;

import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;

public class KnSharedCorpGrpMemberPropValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSharedCorpGrpMemberPropValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        Set<Integer> invalidGrpIds = new HashSet<>();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            KnIPUserProfileDTO inputDTO = (KnIPUserProfileDTO) userProfilePersistDTO.getInputDTO();
            Set<KnCorpGroupListInfoDTO> allRequestGroupList = new HashSet<>();
            if (inputDTO.getUserProfileDTO() != null && inputDTO.getUserProfileDTO().getGroupList() != null) {
                allRequestGroupList.addAll(inputDTO.getUserProfileDTO().getGroupList());
            }
            if (inputDTO.getModifiedUserProfileDTO() != null && inputDTO.getModifiedUserProfileDTO().getAddedGroupList() != null) {
                allRequestGroupList.addAll(inputDTO.getModifiedUserProfileDTO().getAddedGroupList());
            }
            if (inputDTO.getModifiedUserProfileDTO() != null && inputDTO.getModifiedUserProfileDTO().getModifiedGroupList() != null) {
                allRequestGroupList.addAll(inputDTO.getModifiedUserProfileDTO().getModifiedGroupList());
            }

            Map<Integer, KnCorpGroupInfoPersistDTO> allGroupMap = userProfilePersistDTO.getCorpGroupList().stream().
                    collect(Collectors.toMap(KnCorpGroupDTO::getGroupId, v -> v, (newVal, oldVal) -> oldVal));
            Map<Integer, List<KnCorpSharedCorpInfo>> sharedGroupInfoMap = userProfilePersistDTO.getSharedCorpGrpInfoMap();
            Map<Integer, KnCorpTrustMatrixDTO> trustMatrixMap = userProfilePersistDTO.getTrustMatrixMap();
            for (KnCorpGroupListInfoDTO group : allRequestGroupList) {
                if (sharedGroupInfoMap.containsKey(group.getGroupID())) {
                    knLogger.debug(methodName, group.getGroupID(), " - Shared group in req, checking for member properties");
                    Long allowedMemProp = sharedGroupInfoMap.get(group.getGroupID()).get(0).getMemFeaturesAllowed();
                    if (allowedMemProp == null)
                        allowedMemProp = trustMatrixMap.get(allGroupMap.get(group.getGroupID()).getCorpId()).getMemFeaturesAllowed();
                    knLogger.debug(methodName, "trust matrix allowedMemProp - ", allowedMemProp);
                    if (!isMemberPropAllowed(allowedMemProp, group)) {
                        invalidGrpIds.add(group.getGroupID());
                    }
                }
            }
        }else if (persistDTO instanceof KnCorpGroupInfoPersistDTO){
            KnCorpGroupInfoPersistDTO groupPersistDto = (KnCorpGroupInfoPersistDTO) persistDTO;
            KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) groupPersistDto.getInputDTO();
            if(inputDTO.getGrpShared() != null && inputDTO.getGrpShared() == 1){
            if(!inputDTO.isOwnerCorpReq()){
                Collection<KnCorpGroupMemberDTO> modifiedList = inputDTO.getModifiedMembers();
                Collection<KnCorpContactDTO> addedList = inputDTO.getAddedMemberDTOMdns();
                Set<KnCorpGroupListInfoDTO> allRequestGroupList = getAllMemberProps(addedList, modifiedList, inputDTO.getGroupId());
                Map<Integer, KnCorpTrustMatrixDTO> trustMatrixMap = groupPersistDto.getTrustMatrixMap();
                knLogger.debug(methodName, "trustMatrixMap - ", trustMatrixMap);
                int groupOwnerCorpId = groupPersistDto.getCorpId();
                knLogger.debug(methodName, "groupOwnerCorpId - ", groupOwnerCorpId);
                KnCorpTrustMatrixDTO trustMatrixDto = trustMatrixMap.get(groupOwnerCorpId);
                Long allowedMemProp = trustMatrixDto != null ? trustMatrixDto.getMemFeaturesAllowed() : null;
                knLogger.debug(methodName, "trust matrix allowedMemProp - ", allowedMemProp);
                for (KnCorpGroupListInfoDTO group : allRequestGroupList) {
                    if (!isMemberPropAllowed(allowedMemProp, group)) {
                        invalidGrpIds.add(group.getGroupID());
                    }
                }}
            }
        }else if (persistDTO instanceof KnCorpBCGrpPersistDTO){
            knLogger.debug(methodName, "Instance of KnCorpBCGrpPersistDTO");
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) bcGrpPersistDTO.getInputDTO();
            if(!inputDTO.isOwnerCorpReq()){
                Collection<KnCorpGroupMemberDTO> modifiedList = inputDTO.getModifiedMembers();
                Collection<KnCorpContactDTO> addedList = inputDTO.getAddedMemberDTOMdns();
                Set<KnCorpGroupListInfoDTO> allRequestGroupList = getAllMemberProps(addedList, modifiedList, inputDTO.getGroupId());
                Map<Integer, KnCorpTrustMatrixDTO> trustMatrixMap = bcGrpPersistDTO.getTrustMatrixMap();
                knLogger.debug(methodName, "trustMatrixMap - ", trustMatrixMap);
                int groupOwnerCorpId = bcGrpPersistDTO.getCorpId();
                knLogger.debug(methodName, "groupOwnerCorpId - ", groupOwnerCorpId);
                KnCorpTrustMatrixDTO trustMatrixDto = trustMatrixMap.get(groupOwnerCorpId);
                Long allowedMemProp = trustMatrixDto != null ? trustMatrixDto.getMemFeaturesAllowed() : null;
                knLogger.debug(methodName, "trust matrix allowedMemProp - ", allowedMemProp);
                for (KnCorpGroupListInfoDTO group : allRequestGroupList) {
                    if (!isMemberPropAllowed(allowedMemProp, group)) {
                        invalidGrpIds.add(group.getGroupID());
                    }
                }
            }
        }
        if (!invalidGrpIds.isEmpty()) {
            knLogger.error(methodName, "Group Member property not allowed ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_MEMBER_PROPERTY_NOT_ALLOWED,
                    "Group Member property not allowed", getEntityId(), getOperationType(), getRuleId(), invalidGrpIds.toString(), "");
        }
    }

    private Set<KnCorpGroupListInfoDTO> getAllMemberProps(Collection<KnCorpContactDTO> addedList,
                                                          Collection<KnCorpGroupMemberDTO> modifiedList, Integer groupId) {
        Set<KnCorpGroupListInfoDTO> allRequestGroupList = new HashSet<>();
        if(null != addedList){
            for(KnCorpContactDTO member : addedList){
                KnCorpGroupListInfoDTO memberInfo = new KnCorpGroupListInfoDTO();
                memberInfo.setGroupID(groupId);
                KnCorpGroupContactDTO grpMemProps = new KnCorpGroupContactDTO();
                grpMemProps.setIsSupervisor(member.getSupervisory());
                grpMemProps.setIsLocSupervisor(member.getLocWatcher());
                grpMemProps.setIsBroadcaster(member.getBroadcaster());
                grpMemProps.setIsOSMAuthorized(member.getIsOSMAuthorize());
                grpMemProps.setCallInitiateAllowed(member.getCallInitiatePermission());
                grpMemProps.setCallTerminateAllowed(member.getCallReceivePermission());
                grpMemProps.setIncallAllowed(member.getInCallPermission());
                memberInfo.setGrpMemProps(grpMemProps);
                allRequestGroupList.add(memberInfo);
            }
        }

        if(null != modifiedList){
            for(KnCorpGroupMemberDTO member : modifiedList){
                KnCorpGroupListInfoDTO memberInfo = new KnCorpGroupListInfoDTO();
                memberInfo.setGroupID(groupId);
                KnCorpGroupContactDTO grpMemProps = new KnCorpGroupContactDTO();
                grpMemProps.setIsSupervisor(member.getSupervisory());
                grpMemProps.setIsLocSupervisor(member.getLocWatcher());
                grpMemProps.setIsBroadcaster(member.getBroadcaster());
                grpMemProps.setIsOSMAuthorized(member.getIsOSMAuthorize());
                grpMemProps.setCallInitiateAllowed(member.getCallInitiatePermission());
                grpMemProps.setCallTerminateAllowed(member.getCallReceivePermission());
                grpMemProps.setIncallAllowed(member.getInCallPermission());
                memberInfo.setGrpMemProps(grpMemProps);
                allRequestGroupList.add(memberInfo);
            }
        }
        knLogger.debug("getAllMemberProps() allRequestGroupList -", allRequestGroupList);
        return allRequestGroupList;
    }

    private boolean isMemberPropAllowed(Long allowedMemProp, KnCorpGroupListInfoDTO group) {
        final String methodName = "isMemberPropAllowed()";
        knLogger.debug(methodName, allowedMemProp);
        if (allowedMemProp == null)
            return false;
        KnCorpGroupContactDTO grpMemProps = group.getGrpMemProps();
        knLogger.debug(methodName, "request grpMemProps - ", grpMemProps);
        BitSet bitSet = KnGeneralUtil.convertLongToBitSet(allowedMemProp);
        Integer defaultAllowedValue=0;
        if ((!grpMemProps.getIsSupervisor().equals(defaultAllowedValue))
                &&!isBitEnabled_1(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.SUPERVISOR.value()), grpMemProps.getIsSupervisor())){
            return false;
        }

        if ((!grpMemProps.getIsBroadcaster().equals(defaultAllowedValue))
                &&!isBitEnabled_1(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.IS_BROADCASTER.value()), grpMemProps.getIsBroadcaster())){
            return false;
        }

        if ((!grpMemProps.getIsLocSupervisor().equals(defaultAllowedValue))
                &&!isBitEnabled_1(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.IS_LOCWATCHER.value()), grpMemProps.getIsLocSupervisor())){
            return false;
        }

        if ((!grpMemProps.getIsOSMAuthorized().equals(defaultAllowedValue))
                &&!isBitEnabled_1(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.IS_OSMAUTHORIZED.value()), grpMemProps.getIsOSMAuthorized())){
            return false;
        }

        if ((!grpMemProps.getCallInitiateAllowed().equals(defaultAllowedValue))
                &&!isBitEnabled_0(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.CALL_INITIATE_PERMISSION.value()), grpMemProps.getCallInitiateAllowed())){
            return false;
        }

        if ((!grpMemProps.getCallTerminateAllowed().equals(defaultAllowedValue))
                &&!isBitEnabled_0(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.CALL_RECEIVE_PERMISSION.value()), grpMemProps.getCallTerminateAllowed())){
            return false;
        }

        if((!grpMemProps.getIncallAllowed().equals(defaultAllowedValue))
                &&!isBitEnabled_0(bitSet.get(KnConstants.GRP_MEMBER_PROPS_BITSET.INCALL_PERMISSION.value()), grpMemProps.getIncallAllowed())){
            return false;
        }
        return true;
    }

    private boolean isBitEnabled_1(boolean permission, Integer value) {
        return null == value || value == 0 || permission;
    }

    private boolean isBitEnabled_0(boolean permission, Integer value) {
        return null == value || value == 1 || permission;
    }
}
