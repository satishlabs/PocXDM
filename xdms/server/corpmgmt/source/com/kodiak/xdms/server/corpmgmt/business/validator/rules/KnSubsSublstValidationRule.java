/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSublistValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        21-01-2011      7.0
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
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isNullOrEmpty;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isObjectNull;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnSubsSublstValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSubsSublstValidationRule.class);
    private final String CLASS = KnSubsSublstValidationRule.class.getName();

    /**
     * This method will validate the CorpId passed in the request is actually present in The DG.CorpInfo table
     * If the Corporate Id is found then its a valid corporate else its a invalid Corporate profile.
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {

        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY Validating the sublist passed to be deleted actually exixt in DB for the subscriber.");
        Collection<Integer> dbSublistIds;
        Collection<Integer> inputSublistIds;
        try {
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO corpContactPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
                IInputDTO inputDTO = corpContactPersistDTO.getInputDTO();
                KnIPCorpSubscContactListDTO subscContactlist = (KnIPCorpSubscContactListDTO) inputDTO;
                dbSublistIds = corpContactPersistDTO.getSubscSublistIds();
                inputSublistIds = subscContactlist.getRemovedSublistIds();

            } else {
                knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }

            //check if all the sublistIds sent are found in DB
            knLogger.debug( methodName, "Check if all the sublistIds passed in request are present in DB.");
            List<Integer> notFoundSublistIds=new ArrayList<Integer>();
            if (!isObjectNull(dbSublistIds) && !dbSublistIds.isEmpty()) {
                if (!isObjectNull(inputSublistIds) && !inputSublistIds.isEmpty()) {
                    knLogger.debug( methodName, "subscContactlist/AddedMdnList passed in request inputDTO is not empty");
                    for (int sublistId : inputSublistIds) {
                        knLogger.debug( methodName, "Checking sublist - " , sublistId , " exists in the DB.");
                        if (!dbSublistIds.contains(sublistId)) {
                        	 knLogger.debug( methodName, "sublist - " , sublistId , " does not exist in the DB.");
                             notFoundSublistIds.add(sublistId);
                             knLogger.debug( methodName, "Sublist not found in DB so far are - " , notFoundSublistIds);
                        }
                    }
                }
            } else if (!isObjectNull(inputSublistIds) && !inputSublistIds.isEmpty()) {
                for (int sublistId : inputSublistIds) {
                	knLogger.debug( methodName, "sublist - " , sublistId , " does not exist in the DB.");
                	notFoundSublistIds.add(sublistId);
                }
            }

            knLogger.debug( methodName, "List of Sublist i.e Sublists not found in DB are - " , notFoundSublistIds);
            if (notFoundSublistIds.size() > 0) {
            	 knLogger.error( methodName, "sublistId found in DB for Subslists are - " , notFoundSublistIds);
                 throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_SUBSCRIBER_SUBLISTS,
                         "Sublist to be deleted not found in the DB for Subscriber", getEntityId(), getOperationType(), getRuleId(), notFoundSublistIds.toString(), "");
            } 
        } finally {
            knLogger.debug( methodName, "EXIT: Validation Completed Successfully for the existence of sublist in DB");
        }
    }
}
