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

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

/**
 * KnMasterListEtagEquValidationRule to validate Etag, If DB and passed etag is same this validation will fail
 * Applicable only for filterType = 0
 */
public class KnMasterListEtagEquValidationRule extends KnValidatorRule {
	
	private static final KnLogger knLogger = KnLogger.getLogger(KnMasterListEtagEquValidationRule.class);
    private static final long serialVersionUID = 7526471155622676285L;

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.entry( methodName);
        IPersistenceDTO persistDTO = getDTO();
        String inputEtag = null;
        String dbEtag = null;
        boolean flag = false;
        try {
            if (persistDTO instanceof KnCorpInfoPersistDTO) {
                KnCorpInfoPersistDTO corpInfopersistDTO = (KnCorpInfoPersistDTO) persistDTO;
                KnIPCorpInfoDTO inputDTO = (KnIPCorpInfoDTO) persistDTO.getInputDTO();
                inputEtag = inputDTO.getETag();
                dbEtag = corpInfopersistDTO.getEtag();
                if(inputDTO.getFilterType() == 0){
                	flag = dbEtag.equals(inputEtag);
                }
            } 
            if (flag) {
                knLogger.error( methodName, "ValidationRule failed. Resource Not modified");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.NO_CHANGE_IN_DOCUMENT_SINCE_LAST_FETCH,
                        "Resource Not modified",  getEntityId(), getOperationType(), getRuleId(),inputEtag, "");
            }

            knLogger.debug( methodName, "Validation Completed Successfully");

        } finally {
            knLogger.exit( methodName);
        }
    }
}
