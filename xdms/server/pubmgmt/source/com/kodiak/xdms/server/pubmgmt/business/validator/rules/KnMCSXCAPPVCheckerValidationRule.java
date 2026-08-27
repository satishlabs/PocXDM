/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMCSXCAPPVCheckerValidationRule.java
 * Subsystem:  XDMS-BulkFrameWork
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Shashank Tewari      26/06/2019    9.1
 * <p/>
 * <p/>
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubMCSXCAPPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import static com.kodiak.xdms.server.pubmgmt.resources.KnConstants.KEY_DATATYPE_MDN;

public class KnMCSXCAPPVCheckerValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMCSXCAPPVCheckerValidationRule.class);
    private static final String MINPVSUPPORT = "minPVSupport";

    public void validate() throws KnValidationException {
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug("validate", "ENTRY : Validating if protocol version is supported or not " + persistDTO);
        int minPVSupport;
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO =null;
        if (persistDTO instanceof KnPubMCSXCAPPersistDTO) {
            pubMCSXCAPPersistDTO=  (KnPubMCSXCAPPersistDTO) persistDTO;
            // current group members - 1 instead of current group members for validation.
            minPVSupport= Integer.parseInt(getAttribute(MINPVSUPPORT));
        } else {
            knLogger.error("validate", "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnPubMCSXCAPPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KEY_DATATYPE_MDN, pubMCSXCAPPersistDTO.getMdn());
        }
        //checking for pv from subs profile and our current pv support
        if ( ((KnPubMCSXCAPPersistDTO) persistDTO).getProtocolVersion() < minPVSupport) {
            knLogger.error("validate", "Validation failed Minimum Protocol version allowed : " + minPVSupport );
            throw new KnPubBOValidationException(KnErrorCodes.Validator.MCSXCAP_LOWER_PV,
                    "Validation Rule Failed, Min PV Allowed : " + minPVSupport, getEntityId(),
                    getOperationType(), getRuleId(), KEY_DATATYPE_MDN, pubMCSXCAPPersistDTO.getMdn());
        }
        knLogger.info("validate", "KnMCSXCAPPVCheckerValidationRule Validated successfully.");
    }
}