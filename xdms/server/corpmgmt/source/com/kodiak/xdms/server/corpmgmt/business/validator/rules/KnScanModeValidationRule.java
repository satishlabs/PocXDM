/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

/**
 * *****************************************************************************
 * File name:   KnExtProfileTypeValidation.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * ChandraShekar H S       12/8/14      7.10
 * <p/>
 * <p/>
 *
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
 * *******************************************************************************
 */

public class KnScanModeValidationRule extends KnValidatorRule {

	private static final long serialVersionUID = 1L;
	private static final KnLogger knLogger = KnLogger.getLogger(KnScanModeValidationRule.class);
	public static final String SCAN_MODE = "scanMode";
	public void validate() throws KnValidationException {
		final String methodName = "validate()";
		knLogger.debug(methodName, "ENTRY Point");
		IPersistenceDTO persistDTO = getDTO();
		if (persistDTO instanceof KnCorpTGSPersistDTO) {
			String scanMode = getAttribute(SCAN_MODE);
			if (((KnCorpTGSPersistDTO) persistDTO).getScanMode()!= Integer.parseInt(scanMode)) {
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.TGS_SCAN_MODE_DISABLED, "TGSC scan mode disabled",
						getEntityId(), getOperationType(), getRuleId(), "Business validation", "");
			}
		} else {
			knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(), ", Expected Dto - KnCorpTGSPersistDTO ");
			throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - "
					+ persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
		}
        knLogger.debug(methodName, "Validation Completed Successfully");
	}
}
