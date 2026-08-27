/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMemshipValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        16-03-2011      7.0
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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;

public class KnMemshipValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMemshipValidationRule.class);

    private final String CLASS = KnMemshipValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        int corpId = 0;
        Collection<KnCorpSubscriberDTO> dbMdnList;
        try {
            IPersistenceDTO persistDTO = getDTO();
            knLogger.debug( methodName, "ENTRY Point : ");
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
                dbMdnList = (contactPersistDTO.getContactCorpDetails()).values();
                if (inputDTO instanceof KnIPCorpContactListDTO) {
                    KnIPCorpContactListDTO contactListDTO = (KnIPCorpContactListDTO) inputDTO;
                    corpId = contactListDTO.getCorpId();

                } else if (inputDTO instanceof KnIPCorpContactDTO) {
                    KnIPCorpContactDTO contactDTO = (KnIPCorpContactDTO) inputDTO;
                    corpId = contactDTO.getCorpId();

                } else {
                    knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                            ", Expected Dto - KnIPCorpContactListDTO/KnIPCorpSubscContactListDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }
            } else {
                knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
            Collection<String> sameCorpMdnList = new ArrayList<String>();
            if (dbMdnList != null) {
                for (KnCorpSubscriberDTO subsc : dbMdnList) {
                    if (subsc.getCorpId() == corpId) {
                    	sameCorpMdnList.add(subsc.getMdn());
                    }
                }
            }

            if (sameCorpMdnList.size() > 0) {
                knLogger.error( methodName, "External Contacts List Contains some current corporate members - " , sameCorpMdnList
                );
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.SAME_CORPORATE_MEMBERS_FOUND,
                        "Same Corporate Members Found", getEntityId(), getOperationType(), getRuleId(), sameCorpMdnList.toString(), "");
            }
        } finally {
            knLogger.debug( methodName, "EXIT: Membership validation Successfully");
        }
    }
}
