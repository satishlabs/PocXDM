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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpAddlTGInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * ************************************************************************
 * <p>
 * File name:  KnAddlTGListZoneChannelValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             April 26, 2018                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnAddlTGListZoneChannelValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAddlTGListZoneChannelValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating Group Check");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpTGSPersistDTO) {
            KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            Collection<Integer> zoneList = new ArrayList<>();
            Collection<Integer> channelLIst = new ArrayList<>();
            Collection<KnCorpAddlTGInfoDTO> zoneChannelList = tgsPersistDTO.getSameZoneChannelList();
            if (zoneChannelList != null && !zoneChannelList.isEmpty()) {
                zoneList = zoneChannelList.stream().map(KnCorpAddlTGInfoDTO::getZoneId).collect(Collectors.toList());
                channelLIst = zoneChannelList.stream().map(KnCorpAddlTGInfoDTO::getChannelId).collect(Collectors.toList());
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.SAME_ZONE_CHANNEL_CAN_NOT_BE_USED_TWICE,
                        "Same Zone and channel can not used twice", getEntityId(),
                        getOperationType(), getRuleId(),Arrays.asList(zoneList, channelLIst).toString(), "");
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpTGSPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "Validation Completed Successfully for the GroupId Check");
    }
}
