/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnResourceNotModifiedValidationRule.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Gunjan Kumar      April 10, 2014      7.9
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

import java.util.Arrays;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

/**
 * KnLargerCorpSizeValRule Rule validate max fetch size[Includes Internal/External/NNI Subscriber] when filterType = 0
 */
public class KnLargerCorpSizeValRule extends KnValidatorRule {
	
	private static final long serialVersionUID = -4224164450096286596L;
	private static final KnLogger knLogger = KnLogger.getLogger(KnLargerCorpSizeValRule.class);
   
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY Point");
        int maxCorporateSize;
		try {
			maxCorporateSize = Integer.parseInt(System.getenv("LARGE_CORP_SIZE"));
		} catch (Exception e) {
			maxCorporateSize = 5000;
			knLogger.warn(methodName, "MAX_CORPORATE_SIZE IS NOT CONFIGURED IN SYSTEM ENV, USING DEFAULT VALUE(5000) ", e);
		}
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnCorpInfoPersistDTO) {
            	KnIPCorpInfoDTO inputDTO = (KnIPCorpInfoDTO) persistDTO.getInputDTO();
            	KnCorpInfoPersistDTO corpInfoPersistDTO = (KnCorpInfoPersistDTO)persistDTO;
                if (inputDTO.getFilterType() == 0 &&  corpInfoPersistDTO.getCorpContactSize() > maxCorporateSize) {
                	knLogger.error("Corporate contact size can not be greater....");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_CORPORATE_SIZE_EXCEED,
                            "Corporate contact size can not be greater then "+maxCorporateSize, getEntityId(), getOperationType(), getRuleId(), Arrays.asList(maxCorporateSize).toString(), "");
                }

            }
            knLogger.debug( methodName, "Validation Completed Successfully");
        } finally {
            knLogger.debug( methodName, "Exit Point");
        }
    }
}

