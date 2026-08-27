/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

public class KnMCXGroupAddedMemberCountvalidator extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMCXGroupAddedMemberCountvalidator.class);

	@Override
	public void validate() throws KnValidationException, KnBOException {
		final String methodName = "validate()";
		knLogger.debug(methodName, "ENTRY. Validating mcx added member count");
		IPersistenceDTO persistDTO = getDTO();
		Set<String> groupMdnList = new HashSet<String>();
		Set<String> groupMdnPatchList = new HashSet<String>();

		KnIPCorpGroupInfoDTO groupInputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();
		KnCorpGroupInfoDTO knCorpGroupInfoDTO=(KnCorpGroupInfoDTO) persistDTO;
		Collection<KnCorpContactDTO> addedContactList = groupInputDTO.getAddedMemberDTOMdns();
		knLogger.debug(methodName, "ENTRY. maxMember",knCorpGroupInfoDTO.getMaxVLGMdnPerGroup());
		
		if (addedContactList != null && addedContactList.size() > knCorpGroupInfoDTO.getMaxVLGMdnPerGroup()) {
			List<String> addedMdns = new ArrayList<String>();
			for (KnCorpContactDTO knCorpContactDTO : addedContactList) {
				if (knCorpContactDTO != null) {
					addedMdns.add(knCorpContactDTO.getMdn());
				}

			}
			knLogger.error(methodName, "Added member count cannot exceed more than one", addedContactList);
			throw new KnCorpBOValidationException(KnErrorCodes.Validator.VLG_GROUP_MEMBER_COUNT_EXCEED,
					"Group Mdn can not exceed more than 1 ", getEntityId(), getOperationType(), getRuleId(),
					addedMdns.toString(), "");

		} else if (addedContactList != null && !addedContactList.isEmpty()) {
			knLogger.debug(methodName, "Only one member mdn present. Checking client type");
			if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
				KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
				knLogger.debug(methodName, "DTO passed in the request is - ", groupPersistDTO);
				Collection<KnCorpSubscriberDTO> mdnList = groupPersistDTO.getAddedMdnDTO();
				knLogger.debug(methodName, "added mdnList - ", mdnList);
				if (mdnList != null) {
					mdnList.forEach(subsc -> {
						if (subsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()) {
							knLogger.debug(methodName, "Group MDN - ", subsc.getMdn());
							groupMdnList.add(subsc.getMdn());
						} else if (subsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
							knLogger.debug(methodName, "Group MDN Patch- ", subsc.getMdn());
							groupMdnPatchList.add(subsc.getMdn());
						}
					});
				}

			} else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
				KnCorpBCGrpPersistDTO bcGroupPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
				knLogger.debug(methodName, "DTO passed in the request is - ", bcGroupPersistDTO);
				Collection<KnCorpSubscriberDTO> members = bcGroupPersistDTO.getValidInternalCont();
				if (members != null) {
					members.forEach(subsc -> {
						if (subsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()) {
							knLogger.debug(methodName, "Group MDN - ", subsc.getMdn());
							groupMdnList.add(subsc.getMdn());
						} else if (subsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
							knLogger.debug(methodName, "Group MDN Patch- ", subsc.getMdn());
							groupMdnPatchList.add(subsc.getMdn());
						}
					});
				}
			}

			knLogger.debug(methodName, "groupMdnList: -- ", groupMdnList, "groupMdnPatchList: -- ", groupMdnPatchList);
			/*
			 * if (groupMdnList.isEmpty() && groupMdnPatchList.isEmpty()) {
			 * knLogger.error(methodName, "Added member must be SG mdn / sg patch mdn");
			 * throw new KnCorpBOValidationException(KnErrorCodes.Validator.
			 * VLG_GROUP_MEMBER_MUST_BE_SG_OR_SGPATCH_MDN,
			 * "Added group member must be SGMdn and SGMdnPatch", getEntityId(),
			 * getOperationType(), getRuleId(), groupMdnList.toString(), ""); }
			 */
		}
	}
}
