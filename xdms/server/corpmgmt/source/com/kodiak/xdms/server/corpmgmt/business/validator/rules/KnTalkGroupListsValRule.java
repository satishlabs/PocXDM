/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KnTalkGroupListsValRule extends KnValidatorRule {

	private static final long serialVersionUID = 1L;
	private static final KnLogger knLogger = KnLogger.getLogger(KnTalkGroupListsValRule.class);

	public void validate() throws KnValidationException {
		final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
		IPersistenceDTO persistDTO = getDTO();
		if (persistDTO instanceof KnCorpTGSPersistDTO) {
			KnIPTalkGroupDTO ipTalkGroupDTO = (KnIPTalkGroupDTO) persistDTO.getInputDTO();
			KnCorpTGSPersistDTO corpTGSPersistDTO = (KnCorpTGSPersistDTO)persistDTO;

			List<KnXDMTalkGroupInfoDTO> addedCampGrpList = ipTalkGroupDTO.getAddedCampGrpList();
			List<KnXDMTalkGroupInfoDTO> modifiedCampGrpList = ipTalkGroupDTO.getModifiedCampGrpList();
			List<KnXDMTalkGroupInfoDTO> removedCampGrpList = ipTalkGroupDTO.getRemovedCammpGrpList();
			List<KnXDMTalkGroupInfoDTO> dbCampedGroups = corpTGSPersistDTO.getDbCampedGroups();
			List<KnXDMTalkGroupInfoDTO> dbChannelGroups = corpTGSPersistDTO.getDbChannelGroups();
			List<KnXDMTalkGroupInfoDTO> dbChannelCampGroups = new ArrayList<>();
			dbChannelCampGroups.addAll(dbCampedGroups);
			dbChannelCampGroups.addAll(dbChannelGroups);


//			if(!dbCampedGroups.containsAll(modifiedCampGrpList)){
//				throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_MODIFIED_CAMPED_GRP, "Invalid modifiedCampGrpList",
//						getEntityId(), getOperationType(), getRuleId(), "Business validation", "");
//			}

//		   for(KnXDMTalkGroupInfoDTO modCampGrp: modifiedCampGrpList){
//				if(!dbCampedGroups.contains(modCampGrp)) {
//					if (modCampGrp.getPriority()!= null){ // && addedCampGrp.getPriority().equals(dbCampedGroups.get(dbCampedGroups.indexOf(addedCampGrp)).getPriority())) {
//						throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_MODIFIED_CAMPED_GRP, "Invalid modifiedCampGrpList",
//								getEntityId(), getOperationType(), getRuleId(), "Business validation", "");
//					}
//				}
//			}
			
			/*if(!dbChannelCampGroups.containsAll(removedCampGrpList)){
				 List<Integer> invalidGrpList=new ArrayList<Integer>();
				 for(KnXDMTalkGroupInfoDTO removedCampGrp:removedCampGrpList){
					 invalidGrpList.add(removedCampGrp.getGroupId());
				 }
				 knLogger.error("Invalid removedCampGrpList ");
				 throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_REMOVED_CAMPED_GRP, "Invalid removedCampGrpList",
						getEntityId(), getOperationType(), getRuleId(),invalidGrpList.toString(), "");
			}*/
			List<Integer> addedcampedGrpIds=new ArrayList<Integer>();
			for(KnXDMTalkGroupInfoDTO addedCampGrp: addedCampGrpList){
				if(dbCampedGroups.contains(addedCampGrp)) {
					if (addedCampGrp.getPriority()!= null){ // && addedCampGrp.getPriority().equals(dbCampedGroups.get(dbCampedGroups.indexOf(addedCampGrp)).getPriority())) {
						addedcampedGrpIds.add(addedCampGrp.getGroupId());
					}
				}
			}
			if(addedcampedGrpIds.size() > 0){
				knLogger.error("Invalid added CampGrpList ");
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_ALREADY_CAMPED, "Invalid addedCampGrpList",
						getEntityId(), getOperationType(), getRuleId(), addedcampedGrpIds.toString(), "");
			}
			Set<Integer> groupIsSet = new HashSet<Integer>();
			for(KnXDMTalkGroupInfoDTO addedCampGrp : addedCampGrpList){
				groupIsSet.add(addedCampGrp.getGroupId());
			}
			for(KnXDMTalkGroupInfoDTO modifiedCampGrp : modifiedCampGrpList){
				groupIsSet.add(modifiedCampGrp.getGroupId());
			}
			for(KnXDMTalkGroupInfoDTO removedCampGrp : removedCampGrpList){
				groupIsSet.add(removedCampGrp.getGroupId());
			}

		} else {
			knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(), ", Expected Dto - KnCorpTGSPersistDTO ");
			throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - "
					+ persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
		}
		knLogger.debug(methodName, "Validation Completed Successfully");
	}
}
