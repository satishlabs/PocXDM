/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

public class KnCorpChannelRangeValRule extends KnValidatorRule {

	private static final long serialVersionUID = 1L;
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpChannelRangeValRule.class);

	public void validate() throws KnValidationException {
		final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
		IPersistenceDTO persistDTO = getDTO();
		if (persistDTO instanceof KnCorpTGSPersistDTO) {
			KnCorpTGSPersistDTO corpTGSPersistDTO = (KnCorpTGSPersistDTO)persistDTO;
			List<KnXDMTalkGroupInfoDTO> talkGroupInfoDTOs = corpTGSPersistDTO.getNewCampedGroups();
			List<KnXDMTalkGroupInfoDTO> talkGroupInfoDTOtemp = new ArrayList<>(talkGroupInfoDTOs);

//			talkGroupInfoDTOtemp = talkGroupInfoDTOtemp.stream()
//					.collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>( Comparator.comparing(KnXDMTalkGroupInfoDTO::getGroupId).thenComparing(KnXDMTalkGroupInfoDTO::getChannel))),
//							ArrayList::new));
//			knLogger.debug(methodName, "TreeSet ::",talkGroupInfoDTOtemp);
			Map<String, KnXDMTalkGroupInfoDTO> map = new HashMap<>();
			for (KnXDMTalkGroupInfoDTO t : talkGroupInfoDTOtemp) {
				map.put(t.getGroupId()+":"+t.getChannel(), t);
			}
			talkGroupInfoDTOtemp.clear();
			talkGroupInfoDTOtemp.addAll(map.values());
			knLogger.debug(methodName, "Unique list ::",talkGroupInfoDTOtemp);

			int maxChannel = corpTGSPersistDTO.getMaxPttRadioChannelGrpLmt();
			int clientMJVersion = corpTGSPersistDTO.getClientMajorVersion();
			Set<Integer> channelSet = new HashSet<Integer>();
			List<Integer> channelList = new ArrayList<Integer>();
			List<Integer> invalidChnlGrpList=new ArrayList<Integer>();
			if(clientMJVersion > 0 && clientMJVersion < KnConstants.PROTOCOL_VERSION_13){
				for (KnXDMTalkGroupInfoDTO talkGroupInfoDTO : talkGroupInfoDTOtemp) {
					Integer channel = talkGroupInfoDTO.getChannel();
					if (channel != null) {
						if (channel > 0 && channel <= maxChannel) {
							channelSet.add(channel);
							channelList.add(channel);
						} else {
							invalidChnlGrpList.add(talkGroupInfoDTO.getGroupId());
						}
					}else{
						knLogger.error(methodName, "Channel not assigned BC ",invalidChnlGrpList);
						throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CHANNEL, "Channel not assigned",
								getEntityId(), getOperationType(), getRuleId(),  Arrays.asList(maxChannel).toString(), "");
					}
				}
			}
			if(invalidChnlGrpList.size() > 0){
				
				  throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CHANNEL_RANGE, "Channel assigned not in range",
                        getEntityId(), getOperationType(), getRuleId(), invalidChnlGrpList.toString(), "");
			}
			if (channelList.size() != channelSet.size()) {
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.DUPLICATE_CHANNEL, "Duplicate channel not allowed",
						getEntityId(), getOperationType(), getRuleId(), channelSet.toString(), "");
			}

		} else {
			knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(), ", Expected Dto - KnCorpTGSPersistDTO ");
			throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - "
					+ persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
		}
		knLogger.debug(methodName, "Validation Completed Successfully");
	}
}
