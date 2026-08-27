/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

@SuppressWarnings("serial")
public class KnDGMDNCorpAdminFSValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDGMDNCorpAdminFSValidationRule.class);
	private static final String NOT_ALLOWED_CLIENT_TYPE = "notAllowedTypes";

	public void validate() throws KnValidationException {
		final String methodName = "validate()";
		knLogger.debug(methodName, "ENTRY. Validating DGMDN.");
		IPersistenceDTO persistDTO = getDTO();
		String notAllowedType = getAttribute(NOT_ALLOWED_CLIENT_TYPE);
		List<String> clientTypeList = Arrays.asList(notAllowedType.split(DELIM));
		KnContactDetailsPersistDTO subscriberInfoDTO = null;

		knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
		if (persistDTO instanceof KnContactDetailsPersistDTO) {
			subscriberInfoDTO = (KnContactDetailsPersistDTO) persistDTO;

		} else {
			knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
					", Expected Dto - KnCorpGroupInfoPersistDTO ");
			throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
					"Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(),
					getOperationType(), getRuleId(), "DataType", "");
		}

		Map<String, KnCorpSubscriberDTO> filteredMap = subscriberInfoDTO.getContactCorpDetails().entrySet().stream().filter(m ->
		clientTypeList.contains(String.valueOf(m.getValue().getClientType()))).collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));

		if (filteredMap.size() > 0) {
			knLogger.debug(methodName, "Invalid Client Type");
			throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE, "Invalid Client Type ",
					getEntityId(), getOperationType(), getRuleId(), filteredMap.keySet().toString(), "");

		}
		knLogger.debug(methodName, "Exit Point: Validation Successfully done for DGMDN Subscriber");
	}

}
