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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
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
 * File name:  KnAddlTGListReqValidationRule.java
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

public class KnAddlTGListReqValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAddlTGListReqValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating Group Check");
        IPersistenceDTO persistDTO = getDTO();
        Collection<Integer> notExistsGroupId = new ArrayList<>();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpTGSPersistDTO) {
            KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            KnIPTalkGroupDTO talkGroupDTO = (KnIPTalkGroupDTO) tgsPersistDTO.getInputDTO();
            Collection<KnCorpAddlTGInfoDTO> dbATGList = tgsPersistDTO.getDbAddlTGList();
            Collection<Integer> dbGroupIds = dbATGList.stream().map(KnCorpAddlTGInfoDTO::getGroupId).collect(Collectors.toList());
            Collection<KnCorpAddlTGInfoDTO> reqDelSubsAddlTGList = talkGroupDTO.getRemovedAddlTgList();
            Collection<KnCorpAddlTGInfoDTO> reqModSubsAddlTGList = talkGroupDTO.getModifiedAddlTgList();
            if (dbGroupIds != null) {
                if (reqDelSubsAddlTGList != null && !reqDelSubsAddlTGList.isEmpty()) {
                    notExistsGroupId.addAll(reqDelSubsAddlTGList.stream().filter(reqGrpId -> !dbGroupIds.contains(reqGrpId.getGroupId()))
                            .map(KnCorpAddlTGInfoDTO::getGroupId).collect(Collectors.toList()));
                }
                if (reqModSubsAddlTGList != null && !reqModSubsAddlTGList.isEmpty()&&!tgsPersistDTO.isCalledFromModifyUPM()) {
                    notExistsGroupId.addAll(reqModSubsAddlTGList.stream().filter(reqGrpId -> !dbGroupIds.contains(reqGrpId.getGroupId()))
                            .map(KnCorpAddlTGInfoDTO::getGroupId).collect(Collectors.toList()));
                }
            }
            knLogger.debug(methodName, "notExistsGroupId-- - ", notExistsGroupId);
            if (!notExistsGroupId.isEmpty()) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.ADDITIONAL_TALK_GROUP_NOT_EXISTS,
                        "Additional Talk group not exists for the subscriber", getEntityId(),
                        getOperationType(), getRuleId(), Arrays.asList(notExistsGroupId).toString(), "");
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
