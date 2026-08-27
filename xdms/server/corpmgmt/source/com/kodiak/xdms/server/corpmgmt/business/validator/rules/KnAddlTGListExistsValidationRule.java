/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpAddlTGInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_13;

/**
 * ************************************************************************
 * <p>
 * File name:  KnAddlTGListExistsValidationRule.java
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

public class KnAddlTGListExistsValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAddlTGListExistsValidationRule.class);
    private static final String ALLOWED_TYPES = "allowedTypes";

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating Subscriber KnAddlTGListExistsValidationRule Check");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        String allowedClientType = getAttribute(ALLOWED_TYPES);
        knLogger.debug(methodName, "Allowed Client Types - ", allowedClientType);
        if (persistDTO instanceof KnCorpTGSPersistDTO) {
            String[] allowedClient = allowedClientType.split(DELIM);
            Collection<String> clientTypeList = Arrays.asList(allowedClient);
            KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            Collection<KnCorpAddlTGInfoDTO> dbATGList = tgsPersistDTO.getDbAddlTGList();
            List<Integer> dbGrpList = dbATGList.stream().map(KnCorpAddlTGInfoDTO::getGroupId).collect(Collectors.toList());
            List<Integer> subsGroupList = tgsPersistDTO.getSubsGroupList();
            List<Integer> groupIdNotExistsInTGList = new ArrayList<>();
            if (clientTypeList.contains(String.valueOf(tgsPersistDTO.getSubsClientType()))) {
                if (dbGrpList != null)
                    groupIdNotExistsInTGList = subsGroupList.stream().filter(ipTG -> !dbGrpList.contains(ipTG)).collect(Collectors.toList());
                knLogger.debug(methodName, "groupIdNotExistsInTGList-- ", groupIdNotExistsInTGList);
            }
            KnIPTalkGroupDTO ipTalkGroupDTO = (KnIPTalkGroupDTO)tgsPersistDTO.getInputDTO();
            List<KnXDMTalkGroupInfoDTO> campedGroup = new ArrayList<>();
            campedGroup.addAll(ipTalkGroupDTO.getAddedCampGrpList());
            campedGroup.addAll(ipTalkGroupDTO.getModifiedCampGrpList());
            Optional<KnXDMTalkGroupInfoDTO> addedChExists = campedGroup.stream().filter(ch -> ch.getChannel() != null).findAny();
            if ((tgsPersistDTO.getClientMajorVersion() == 0 || tgsPersistDTO.getClientMajorVersion() >= PROTOCOL_VERSION_13)) {
                if (!addedChExists.isPresent() && !groupIdNotExistsInTGList.isEmpty()) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.ADDITIONAL_TALK_GROUP_NOT_EXISTS,
                            "Additional Talk group not exists for the subscriber", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList(groupIdNotExistsInTGList).toString(), "");
                }
            }
            if (addedChExists.isPresent() && tgsPersistDTO.getClientMajorVersion() == 0) {
                List<Integer> invalidChnlGrpList = campedGroup.stream().filter(ch -> ch.getChannel() == null)
                        .map(KnXDMTalkGroupInfoDTO::getGroupId).collect(Collectors.toList());
                if (invalidChnlGrpList != null && invalidChnlGrpList.size() > 0) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CHANNEL_RANGE, "Channel assigned not in range",
                            getEntityId(), getOperationType(), getRuleId(), invalidChnlGrpList.toString(), "");
                }
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpTGSPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "Validation Completed Successfully for the Subscriber ClientType Check");
    }
}
