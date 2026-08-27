/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

/**
 * ************************************************************************
 * <p>
 * File name:  KnLocWatcherValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Sept 17, 2016                8.1.2
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

 /*If user sent Dispatcher/Interop/SG member in group memberList and as well as locWatcher enabled for these clientType;
         then server will validate it. Because Dispatcher/Interop/SG cannot publish/watch its location.*/

public class KnLocWatcherValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnLocWatcherValidationRule.class);
    private String CLASS = KnLocWatcherValidationRule.class.getName();
    private static final String ALLOWED_TYPES = "allowedTypes";

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating locWatcher.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        String allowedClientType = getAttribute(ALLOWED_TYPES);
        knLogger.debug(methodName, "Allowed Client Types - ", allowedClientType);
        try {
            String[] allowedClient = allowedClientType.split(DELIM);
            Collection<String> clientTypeList = Arrays.asList(allowedClient);
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                Collection<KnCorpGroupMemberDTO> locWatcherList = corpGroupInfoPersistDTO.getLocWatcherList();
                // LocWatcher Validation as per systemLevelConfig
                if(corpGroupInfoPersistDTO.isLocWatcherExists() &&
                        !corpGroupInfoPersistDTO.getLocWatcherConfigValue().equals(KnConstants.LOC_WATCHER_ENABLED_CONFIG_VALUE)){
                    knLogger.debug(methodName, "LocWatcher is not allowed to add/modify in group because system level flag is disabled");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.LOCWATCHER_DISABLED_SYSTEM_LEVEL,
                            "LocWatcher is not allowed to add/modify in group because system level flag is disabled",
                            getEntityId(), getOperationType(), getRuleId(), corpGroupInfoPersistDTO.getLocWatcherConfigValue(), "");
                }
                // LocWatcher Validation as per to client level
                if(locWatcherList != null) {
                	List<String> invalidMdns=new ArrayList<String>();
                    List<String> allLocWatchers = new ArrayList<String>();
                    for (KnCorpGroupMemberDTO memDetails : locWatcherList) {
                        if (corpGroupInfoPersistDTO.isLocWatcherExists() && !clientTypeList.contains(String.valueOf(memDetails.getClientType()))) {
                            knLogger.debug(methodName, "LocWatcher is not applicable for interop/SU SG MDN/Dispatcher/3rdPartyDispatcher ");
                            invalidMdns.add(memDetails.getMdn());
                        }
                        allLocWatchers.add(memDetails.getMdn());
                    }
                    if(!corpGroupInfoPersistDTO.isLargeGroup() && invalidMdns.size() > 0){
                       knLogger.error("LocWatcher cannot be enabled for interop/SU SG MDN/Dispatcher/3rdPartyDispatcher ",invalidMdns);
                       throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE_FOR_LOCWATCHER,
                            "LocWatcher cannot be enabled for interop/SU SG MDN/Dispatcher/3rdPartyDispatcher",
                            getEntityId(), getOperationType(), getRuleId(), invalidMdns.toString(), "");
                    }
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnCorpGroupInfoPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Successfully done for the locWatcher");
        }
    }
}