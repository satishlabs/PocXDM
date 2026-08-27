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

public class KnChannelGroupListsValRule extends KnValidatorRule {

	private static final long serialVersionUID = 1L;
	private static final KnLogger knLogger = KnLogger.getLogger(KnChannelGroupListsValRule.class);

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
			List<KnXDMTalkGroupInfoDTO> dbChannelGroups = corpTGSPersistDTO.getDbChannelGroups();
			
			if(!dbChannelGroups.containsAll(modifiedCampGrpList)){
				 List<Integer> invalidModifyGrpList=new ArrayList<Integer>();
				 for(KnXDMTalkGroupInfoDTO knXDMTalkGroupInfoDTO : modifiedCampGrpList){
					 invalidModifyGrpList.add(knXDMTalkGroupInfoDTO.getGroupId());
				 }
				 knLogger.error("Invalid modified ChannelGrpList");
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_MODIFIED_CHANNEL_GRP, "Invalid modified ChannelGrpList",
						getEntityId(), getOperationType(), getRuleId(), invalidModifyGrpList.toString(), "");
			}
			/*if(!dbChannelGroups.containsAll(removedCampGrpList)){
				List<Integer> invalidGrpList=new ArrayList<Integer>();
				 for(KnXDMTalkGroupInfoDTO knXDMTalkGroupInfoDTO : removedCampGrpList){
					 invalidGrpList.add(knXDMTalkGroupInfoDTO.getGroupId());
				 }
				 knLogger.error("Invalid modified ChannelGrpList");
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_REMOVED_CHANNEL_GRP, "Invalid removed ChannelGrpList",
						getEntityId(), getOperationType(), getRuleId(), invalidGrpList.toString(), "");
			}*/
			List<Integer> channelledGrpList=new ArrayList<Integer>();
			for(KnXDMTalkGroupInfoDTO addedCampGrp: addedCampGrpList){
				if(dbChannelGroups.contains(addedCampGrp)) {
					if (addedCampGrp.getChannel() !=null && !addedCampGrp.getChannel().equals(dbChannelGroups.get(dbChannelGroups.indexOf(addedCampGrp)).getChannel())) {
						channelledGrpList.add(addedCampGrp.getGroupId());
					}
				}
			}
			if(channelledGrpList.size() > 0){
				knLogger.debug(methodName, "Group Already chanelled ",channelledGrpList);
			     throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_ALREADY_CHANNELED, "Invalid added ChannelGrpList",
					getEntityId(), getOperationType(), getRuleId(), channelledGrpList.toString(), "");
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
