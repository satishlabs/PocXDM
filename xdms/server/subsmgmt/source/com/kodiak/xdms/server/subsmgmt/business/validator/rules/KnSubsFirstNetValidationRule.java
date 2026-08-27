/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.business.validator.rules;

import java.util.Arrays;
import java.util.List;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubsProvInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnSubsFirstNetValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSubsFirstNetValidationRule.class);
	private static final String CLASS_NAME = KnSubsFirstNetValidationRule.class.getName();

	public void validate() throws KnValidationException, KnBOException {
		String methodName = "validate()";
		IPersistenceDTO persistDTO = getDTO();
		knLogger.debug(methodName, "validating if firstNetIndicator value is valid", "DTO received - " + persistDTO);
		KnSubsProfilePersistDTO subsProfilePersistDTO;
		String firstNetIndicator;
		if (persistDTO instanceof KnSubsProfilePersistDTO) {
			subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
			KnIPSubsProvInfoDTO subsProvInputDTO = (KnIPSubsProvInfoDTO) subsProfilePersistDTO.getInputDTO();
			firstNetIndicator = subsProvInputDTO.getFirstNetIndicator();

		} else {
			knLogger.error(methodName, "Invalid DTO is passed for validation " + persistDTO.getClass());
			throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
					"Un-expected dao DTO is passed - " + persistDTO.getClass(), getEntityId(), getOperationType(),
					getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);

		}

		if (firstNetIndicator != null) {
			String firstNetIndicatorList = KnGenInfoUtil.getInstance().retrieveRTXConfigValues(null)
					.get(KnConstants.FIRSTFLAG_CONFIG);
			if (firstNetIndicatorList != null) {
				List<String> firstNetIndicators = Arrays.asList(firstNetIndicatorList.split(","));
				if (!firstNetIndicators.contains(firstNetIndicator)) {
					knLogger.debug(methodName, "Validation failed for firstNetIndicator");
					throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_FIRST_NET_INDICATOR,
							"Validation failed for firstNetIndicator, Expected values - ", entityId, operationId, getRuleId(),
							KnProvConstants.KEY_DATATYPE_MDN, ((KnSubsProfilePersistDTO) persistDTO).getMdn());
				}
			} else {
				knLogger.debug(methodName, "Validation failed for firstNetIndicator");
				throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_FIRST_NET_INDICATOR,
						"Validation failed for firstNetIndicator, Expected values - ", entityId, operationId, getRuleId(),
						KnProvConstants.KEY_DATATYPE_MDN, ((KnSubsProfilePersistDTO) persistDTO).getMdn());
			}
		}

		knLogger.debug(methodName, "Exit: firstNetIndicator validated successfully with inputDTO");

	}
}
