/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnEtagMatchValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        09-02-2011      7.0
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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isNullOrEmpty;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnEtagMatchValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnEtagMatchValidationRule.class);
    private static final long serialVersionUID = 7526471155622676286L;

    private String CLASS = KnEtagMatchValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY Validating the etag passed in request and the one present ind db are same or not.");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                KnSublistDetailsPersistDTO sublistDetails = (KnSublistDetailsPersistDTO) persistDTO;
                KnIPCorpSublistInfoDTO sublistInfo = (KnIPCorpSublistInfoDTO) sublistDetails.getInputDTO();
                if (sublistInfo.getETag() != sublistDetails.getCurrentEtag()) {
                    throw new KnValidationException("", "VersionId Mismatch", "");
                }
            } else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO groupPersistDto = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) groupPersistDto.getInputDTO();
                if (inputDTO.getETag() != groupPersistDto.getETag()) {
                    throw new KnValidationException("", "VersionId Mismatch", "");
                }
            } else if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
                KnIPCorpSubscContactListDTO inputDTO = (KnIPCorpSubscContactListDTO) contactPersistDTO.getInputDTO();
                if (!isNullOrEmpty(inputDTO.getETag()) &&
                        Integer.valueOf(inputDTO.getETag()) != contactPersistDTO.getEtag()) {
                    throw new KnValidationException("", "VersionId Mismatch", "");
                }
            } else {
                knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }

        }finally {
            knLogger.debug( methodName, "EXIT: Validation successful for the etag passed in request and the one present ind db are same or not.");
        }
    }
}
