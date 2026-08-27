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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ************************************************************************
 * <p/>
 * File name: KnSubsAliasMdnValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name                     Date                    Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar         March 08, 2018            9.0
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

public class KnSubsAliasMdnValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsAliasMdnValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating AliasMdn.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnContactDetailsPersistDTO) {
            KnContactDetailsPersistDTO subsDetails = (KnContactDetailsPersistDTO) persistDTO;
            Collection<String> aliasMdnList = subsDetails.getAliasMdnList();
            Map<String, String> aliasMdnMap = subsDetails.getAliasMdnMap();
            if(aliasMdnMap == null) aliasMdnMap = new HashMap<>();
            Map<String, String> finalAliasMdnMap = aliasMdnMap;
            Collection<String> notExistsAliasMdn = new ArrayList<>();
            if(aliasMdnList != null && !aliasMdnList.isEmpty()){
                notExistsAliasMdn.addAll(aliasMdnList.stream().filter(aliasMdn ->
                        !finalAliasMdnMap.containsKey(aliasMdn)).collect(Collectors.toList()));
            }
            knLogger.debug(methodName, "notExistsAliasMdn: ", notExistsAliasMdn);
            if (!notExistsAliasMdn.isEmpty()) {
                knLogger.debug(methodName, "Requested alias MDN does not exists in DB");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.ALIAS_MDN_DOES_NOT_EXISTS,
                        "Requested alias MDN does not exists in DB", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(notExistsAliasMdn).toString(), "");
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "Exit Point: Validation Successfully done for aliasMdn existence");
    }
}