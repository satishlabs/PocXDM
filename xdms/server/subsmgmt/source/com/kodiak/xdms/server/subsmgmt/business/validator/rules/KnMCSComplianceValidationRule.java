/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubsProvInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnMCSComplianceValidationRule extends KnValidatorRule{
	private static final KnLogger knLogger = KnLogger.getLogger(KnMCSComplianceValidationRule.class);
	private static final String CLASS_NAME = KnMCSComplianceValidationRule.class.getName();

	public void validate() throws KnValidationException {
		String methodName = "validate()";
		boolean mcsComplianceValid = false;
		IPersistenceDTO persistDTO = getDTO();
		knLogger.debug(methodName, "validating if mcsCompliance value is valid", "DTO received - " + persistDTO);

		KnSubsProfilePersistDTO subsProfilePersistDTO;
		int licenseType;
		int clientType;
		int mcsCompliance;
		if (persistDTO instanceof KnSubsProfilePersistDTO) {
			subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
			KnIPSubsProvInfoDTO subsProvInputDTO = (KnIPSubsProvInfoDTO) subsProfilePersistDTO.getInputDTO();
			licenseType = subsProvInputDTO.getLicenseType();
			clientType = subsProvInputDTO.getSubsClientType();
			mcsCompliance=subsProvInputDTO.getMcsCompliance();

		} else {
			knLogger.error(methodName, "Invalid DTO is passed for validation " + persistDTO.getClass());
			throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
					"Un-expected dao DTO is passed - " + persistDTO.getClass(), getEntityId(), getOperationType(),
					getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);

		}

		if (mcsCompliance == KnConstants.MCSCOMPLIANCE
				&& (clientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
						|| clientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value())) {

			mcsComplianceValid = true;

		} else if(mcsCompliance != KnConstants.MCSCOMPLIANCE){
			mcsComplianceValid = true;
		}else {
			mcsComplianceValid = false;
		}

		if (!mcsComplianceValid) {
			knLogger.debug(methodName, "Validation failed for client type and mcsCompliance");
			throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_MCS_COMPLIANCE,
					"Validation failed, Expected values - ", entityId, operationId, getRuleId(),
					KnProvConstants.KEY_DATATYPE_MDN, ((KnSubsProfilePersistDTO) persistDTO).getMdn());
		}

		knLogger.debug(methodName, "Exit: mcsCompliance validated successfully with inputDTO");

	}
}
