/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBCGrpBroadstrBitValidator.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Kumar      Aug 12, 2014      7.10
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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KnBCGrpBroadstrBitValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBCGrpFeatureValidator.class);

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        List<String> disabledMdn = new ArrayList<>();
        if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            Map<String, Boolean> interBroadcasterBitMap = bcGrpPersistDTO.getInterBroadcasterBitMap();
            for(Map.Entry<String, Boolean> entry : interBroadcasterBitMap.entrySet()){
                if(!entry.getValue()){
                    disabledMdn.add(entry.getKey());
                }
            }
        }else{
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass());
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        if(disabledMdn.size() > 0){
            knLogger.error(methodName, "Broadcasters BCG feature disabled  - ", disabledMdn);
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBERS_BROADCASTER_FEATURE_DISABLED,
                    "Subscribers Broadcaster bit disabled", getEntityId(), getOperationType(), getRuleId(),disabledMdn.toString(), "");
        }
    }
}
