/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubsProvInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnLicenseTypeValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnLicenseTypeValidationRule.class);
	private static final String CLASS_NAME = KnLicenseTypeValidationRule.class.getName();

	public void validate() throws KnValidationException {
		String methodName = "validate()";
		boolean licenseTypeValid = false;
		IPersistenceDTO persistDTO = getDTO();
		knLogger.debug(methodName, "validating if license type value is valid", "DTO received - " + persistDTO);

		KnSubsProfilePersistDTO subsProfilePersistDTO;
		int licenseType;
		int clientType;
		if (persistDTO instanceof KnSubsProfilePersistDTO) {
			subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
			KnIPSubsProvInfoDTO subsProvInputDTO = (KnIPSubsProvInfoDTO) subsProfilePersistDTO.getInputDTO();
			licenseType = subsProvInputDTO.getLicenseType();
			clientType = subsProvInputDTO.getSubsClientType();

		} else {
			knLogger.error(methodName, "Invalid DTO is passed for validation " + persistDTO.getClass());
			throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
					"Un-expected dao DTO is passed - " + persistDTO.getClass(), getEntityId(), getOperationType(),
					getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);

		}

		if (licenseType == KnConstants.USER_LICENSE_TYPE
				&& (clientType == KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value()
						|| clientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
						|| clientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
						|| clientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
						|| clientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()
						|| clientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
						|| clientType == KnProvConstants.SUBS_CLIENT_TYPE.DATAGROUPMDN.value())) {

			licenseTypeValid = true;

		} else if (licenseType != KnConstants.USER_LICENSE_TYPE) {
			licenseTypeValid = true;
		} else {
			licenseTypeValid = false;
		}

		if (!licenseTypeValid) {
			knLogger.debug(methodName, "Validation failed for client type and licenseType");
			throw new KnProvBOValidationException(KnErrorCodes.Validator.INVALID_LICENSE_TYPE,
					"Validation failed, Expected values - ", entityId, operationId, getRuleId(),
					KnProvConstants.KEY_DATATYPE_MDN, ((KnSubsProfilePersistDTO) persistDTO).getMdn());
		}

		knLogger.debug(methodName, "Exit: License Type validated successfully with inputDTO");

	}
}
