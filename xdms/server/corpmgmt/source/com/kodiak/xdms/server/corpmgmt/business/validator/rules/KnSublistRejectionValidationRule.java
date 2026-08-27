/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSublistRejectionValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjeev Harohalli        27-03-2020     10.0.1
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class KnSublistRejectionValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSublistRejectionValidationRule.class);
	final String CLASS = KnSublistRejectionValidationRule.class.getName();

	public void validate() throws KnValidationException {
		final String methodName = "validate()";
		knLogger.debug(methodName, "ENTRY. Validating if sublist and sublist members are present in the request");
		try {
			Collection<Integer> sublistIds = null;
			Collection<KnCorpGroupMemberDTO> sublistMemberList = null;
			Collection<Integer> removedSublistIds = null;
			IPersistenceDTO persistDTO = getDTO();
			KnIPCorpGroupInfoDTO groupInputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();

			/*
			 * if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
			 * KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO =
			 * (KnCorpGroupInfoPersistDTO) persistDTO;
			 *
			 * } else if(persistDTO instanceof KnCorpBCGrpPersistDTO){ groupInputDTO =
			 * (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO(); }
			 */

			sublistIds = groupInputDTO.getAddedSublistIds();
			sublistMemberList = groupInputDTO.getGroupSupervisor();
			removedSublistIds = groupInputDTO.getRemovedSublistIds();

			if (sublistIds != null && sublistIds.size() != 0) {
				knLogger.error(methodName, "sublistId or  sublistMemberList are not empty sublistIds- ", sublistIds,
						", sublistMemberList- ", sublistMemberList);
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.VLG_SUBLIST_OPERATION_NOT_SUPPORTED,
						"Invalid request passed (sublistIds)", getEntityId(), getOperationType(), getRuleId(),
						sublistIds.toString(), "");

			} /*
				 * else if (sublistMemberList != null && sublistMemberList.size() != 0) {
				 * List<String> failureList = new ArrayList<String>(); for (KnCorpGroupMemberDTO
				 * knCorpGroupMemberDTO : sublistMemberList) {
				 * failureList.add(knCorpGroupMemberDTO.getMdn()); } throw new
				 * KnCorpBOValidationException(KnErrorCodes.Validator.
				 * VLG_SUBLIST_OPERATION_NOT_SUPPORTED,
				 * "Invalid request passed (sublistMemberProperties)", getEntityId(),
				 * getOperationType(), getRuleId(), failureList.toString(), ""); }
				 */

			if(removedSublistIds!=null && removedSublistIds.size()>0){
				knLogger.error(methodName, "sublistId or  sublistMemberList are not empty sublistIds- ", sublistIds,
						", sublistMemberList- ", removedSublistIds);
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.VLG_SUBLIST_OPERATION_NOT_SUPPORTED,
						"Invalid request passed (sublistIds)", getEntityId(), getOperationType(), getRuleId(),
						removedSublistIds.toString(), "");
			}




		} finally {
			knLogger.debug(methodName, "EXIT: Validation Completed Successfully");
		}
	}
}
