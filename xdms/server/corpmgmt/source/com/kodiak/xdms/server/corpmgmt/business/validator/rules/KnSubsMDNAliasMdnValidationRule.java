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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ************************************************************************
 * <p/>
 * File name: KnSubsMDNAliasMdnValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name                     Date                    Release
 * -------------------- ------------ -------------------------------------
 * Venkata Sudhakar         OCT 9, 2020            11.0
 *
 * ************************************************************************
 */
public class KnSubsMDNAliasMdnValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsAliasMdnValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating AliasMdn belongs to same subscriber MDN or not.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnContactDetailsPersistDTO) {
            KnContactDetailsPersistDTO subsDetails = (KnContactDetailsPersistDTO) persistDTO;
            KnCorpSubscriberDTO corpSubscriberDTO = subsDetails.getSubscDto();
            Collection<String> aliasMdnList = subsDetails.getAliasMdnList();
            Map<String, String> aliasMdnMap = subsDetails.getAliasMdnMap();
            if(aliasMdnMap == null) aliasMdnMap = new HashMap<>();
            Map<String, String> finalAliasMdnMap = aliasMdnMap;
            Collection<String> notExistsAliasMdn = new ArrayList<>();
            String subscriberMdn = corpSubscriberDTO.getMdn();
            knLogger.debug(methodName, "request MDN ", subscriberMdn);
            if(aliasMdnList != null && !aliasMdnList.isEmpty() && subscriberMdn!=null){
                notExistsAliasMdn.addAll(aliasMdnList.stream().filter(aliasMdn ->
                        !finalAliasMdnMap.containsValue(subscriberMdn)).collect(Collectors.toList()));
            }
            knLogger.debug(methodName, "AliasMdn not belongs to request MDN: ", notExistsAliasMdn);
            if (!notExistsAliasMdn.isEmpty()) {
                knLogger.debug(methodName, "Requested AliasMdn not belongs to request Subscriber MDN");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.ALIAS_MDN_DOES_NOT_BELONGS_TOSUBSMDN,
                        "Requested AliasMdn not belongs to request Subscriber MDN", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(notExistsAliasMdn).toString(), "");
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "Exit Point: Validation Successfully done ");
    }

}
