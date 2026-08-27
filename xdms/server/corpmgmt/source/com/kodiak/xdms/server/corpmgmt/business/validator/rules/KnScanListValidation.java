/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnScanListValidation.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar      Jan 10, 2019      9.03
 * <p/>
 * <p/>
 * 9th Floor, MFar Manyata Tech Park
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;
import java.util.List;

public class KnScanListValidation extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnScanListValidation.class);
    private static final long serialVersionUID = 7526471155622234233L;

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpTGSPersistDTO) {
            KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            List<Integer> groupsPresentInScanList = tgsPersistDTO.getDbGrpsInScanList();
            if(groupsPresentInScanList != null  && !groupsPresentInScanList.isEmpty()){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.SCAN_LIST_EXISTS,
                        "Groups present in scan list", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(groupsPresentInScanList).toString(), "");
            }

        } else{
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpTGSPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug( methodName, "Validation Completed Successfully");
    }
}
