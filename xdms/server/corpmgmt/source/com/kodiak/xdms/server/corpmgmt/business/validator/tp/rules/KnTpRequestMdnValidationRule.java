/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.tp.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;
import java.util.Map;

/**
 * ************************************************************************
 * <p>
 * File name: KnTpRequestMdnValidationRule.java Subsystem: PoC
 * <p>
 * Name Date Release -------------------- ---------------- ------------------
 * Saurabh Kumar Feb 06, 2018 9.0
 * <p>
 * <p>
 * Manyata Tech Park' Greenheart Phase IV, Nagawara Bangalore - 560 045
 * www.kodiakptt.com All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Kodiak Networks.
 * ************************************************************************
 */

public class KnTpRequestMdnValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnTpRequestMdnValidationRule.class);
	private String CLASS = KnTpRequestMdnValidationRule.class.getName();

	public void validate() throws KnValidationException {
		final String methodName = "validate()";
		knLogger.debug(methodName, "ENTRY. Validating TpRequestMdn.");
		IPersistenceDTO persistDTO = getDTO();
		String tpRequestMdn = null;
		String tpOwnerMdn = null;
		Map<String, Integer> existingGroupModifyMPermMap = null;
		if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
			KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
			tpRequestMdn = corpGroupInfoPersistDTO.getTpRequestMdn();
			tpOwnerMdn = corpGroupInfoPersistDTO.getTpGroupOwner();
			existingGroupModifyMPermMap = corpGroupInfoPersistDTO.getExistingGroupModifyPermMap();
		} else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
			KnCorpBCGrpPersistDTO corpBCGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
			tpRequestMdn = corpBCGrpPersistDTO.getTpRequestMdn();
			tpOwnerMdn = corpBCGrpPersistDTO.getTpGroupOwner();
			existingGroupModifyMPermMap = corpBCGrpPersistDTO.getExistingGroupModifyPermMap();
		} else {
			knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),", Expected Dto - KnCorpGroupInfoPersistDTO ");
			throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
					"Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
		}
		knLogger.debug(methodName, "tpRequestMdn -tpOwnerMdn  - ", tpRequestMdn, tpOwnerMdn);
		knLogger.debug(methodName, "existingGroupModifyMPermMap - ", existingGroupModifyMPermMap);
		boolean notValidMdn = false;
		if (tpRequestMdn != null) {
			// request coming from Dynamic Interface
			if (!tpRequestMdn.equals(tpOwnerMdn)) {
				if (existingGroupModifyMPermMap.containsKey(tpRequestMdn)) {
					if (existingGroupModifyMPermMap.get(tpRequestMdn) != KnConstants.DB_ENABLED_VALUE) {
						notValidMdn = true;
					}
				} else {
					notValidMdn = true;
				}
			}
			if (notValidMdn) {
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_TP_REQUEST_MDN,
						"Invalid TpRequestMDN(tpRequestMDN do not have permission or not a tpGroupOwner) --",
						getEntityId(), getOperationType(), getRuleId(), Arrays.asList(tpRequestMdn).toString(), "");
			}
			knLogger.debug(methodName, "Exit Point: Validation Successfully done for the locWatcher");
		}
	}
}