/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import java.util.Arrays;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.CREATE_SUBS_ATG_SCAN_LIST;

public class KnCorpMaxTalkGrpValRule extends KnValidatorRule {

	private static final long serialVersionUID = 1L;
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpMaxTalkGrpValRule.class);

	public void validate() throws KnValidationException {
		final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
		IPersistenceDTO persistDTO = getDTO();
		if (persistDTO instanceof KnCorpTGSPersistDTO) {
			KnCorpTGSPersistDTO corpTGSPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
			if(corpTGSPersistDTO.isPriorityExists() || !CREATE_SUBS_ATG_SCAN_LIST.equals(corpTGSPersistDTO.getOperationType())){
			int newSize = corpTGSPersistDTO.getNewCampedGroups().size();
				if (newSize > corpTGSPersistDTO.getMaxCampedGrpLmt()) {
					throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_SCAN_LIST_SIZE_EXCEEDED, "Max Camped Group Size Limit Exceeds",
							getEntityId(), getOperationType(), getRuleId(),  Arrays.asList(corpTGSPersistDTO.getMaxCampedGrpLmt()).toString(), "");
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
