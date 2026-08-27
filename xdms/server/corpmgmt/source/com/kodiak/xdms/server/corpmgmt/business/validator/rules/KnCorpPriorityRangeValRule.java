/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.CREATE_SUBS_ATG_SCAN_LIST;

public class KnCorpPriorityRangeValRule extends KnValidatorRule {

	private static final long serialVersionUID = 1L;
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpPriorityRangeValRule.class);

	public void validate() throws KnValidationException {
		final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
		IPersistenceDTO persistDTO = getDTO();
		if (persistDTO instanceof KnCorpTGSPersistDTO) {
			KnCorpTGSPersistDTO corpTGSPersistDTO = (KnCorpTGSPersistDTO)persistDTO;
			List<KnXDMTalkGroupInfoDTO> talkGroupInfoDTOs = corpTGSPersistDTO.getNewCampedGroups();
			knLogger.debug("talkGroupInfoDTOs :",talkGroupInfoDTOs);
			if (corpTGSPersistDTO.isPriorityExists() || !CREATE_SUBS_ATG_SCAN_LIST.equals(corpTGSPersistDTO.getOperationType())) {
				int maxPriority = corpTGSPersistDTO.getMaxPriority();
				Set<Integer> prioritySet = new HashSet<Integer>();
				List<Integer> priorityList = new ArrayList<Integer>();
				for (KnXDMTalkGroupInfoDTO talkGroupInfoDTO : talkGroupInfoDTOs) {
					Integer priority = talkGroupInfoDTO.getPriority();
					if (priority != null && priority != 99) {
						if (priority > 0 && priority <= maxPriority) {
							prioritySet.add(priority);
							priorityList.add(priority);
						} else {
							knLogger.error("Priority assigned not in range..");
							throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_PRIORITY_RANGE, "Priority assigned not in range",
									getEntityId(), getOperationType(), getRuleId(), Arrays.asList(maxPriority).toString(), "");
						}
					}

				}

				if (priorityList.size() != prioritySet.size()) {
					knLogger.error("Duplicate priority not allowed..");
					throw new KnCorpBOValidationException(KnErrorCodes.Validator.DUBLICATE_PRIORITY, "Duplicate priority not allowed",
							getEntityId(), getOperationType(), getRuleId(), prioritySet.toString(), "");
				}
			}
		} else {
			knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(), ", Expected Dto - KnCorpTGSPersistDTO ");
			throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - "
					+ persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
		}
		knLogger.debug(methodName, "Validation Completed Successfully");
	}
}
