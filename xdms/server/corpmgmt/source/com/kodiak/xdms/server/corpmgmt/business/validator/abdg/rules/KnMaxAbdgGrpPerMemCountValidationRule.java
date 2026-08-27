/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMaxAbdgGrpPerMemCountValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar        01-03-2018      9.0+
 * <p/>
 * <p/>
 * 
 * 
 * KODIAK, 9th Floor, 'MFar
 * Manyata Tech Park' Greenheart Phase IV,
 * Nagawara Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.business.validator.abdg.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.AREA_BASED_DYNAMIC_GROUP;

public class KnMaxAbdgGrpPerMemCountValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxAbdgGrpPerMemCountValidationRule.class);
    private final String CLASS = KnMaxAbdgGrpPerMemCountValidationRule.class.getName();

    public void validate() throws KnValidationException {
        IPersistenceDTO persistDTO = getDTO();
        String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY : Validating if total group count exceeds max groups ",
                "per member limit.");
        int maxAbdgGroupsPerMemberCount = 0;
        int clientIntf = 0;
        Map<String, Integer> groupMemberMap = null;
        KnIPCorpGroupInfoDTO groupIpDTO = null;
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            groupIpDTO = (KnIPCorpGroupInfoDTO) groupPersistDTO.getInputDTO();
            clientIntf = groupIpDTO.getClientType();
            maxAbdgGroupsPerMemberCount = groupPersistDTO.getMaxAbdgPerGrpMem();
            groupMemberMap = groupPersistDTO.getAbdgGroupCountPerMemberMap();
        } else {
            knLogger.error(methodName, "Unexpected DTO passed - ", persistDTO.getClass(), ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "", "");
        }
        knLogger.debug(methodName, "Max Abdg groups per Members allowed - ", maxAbdgGroupsPerMemberCount);
        if (AREA_BASED_DYNAMIC_GROUP == clientIntf && groupIpDTO.getGroupType() == KnConstants.DISPATCH_GROUP &&
                (groupMemberMap != null && !groupMemberMap.isEmpty())) {
            Collection<String> memberMdnList = groupMemberMap.keySet();
            StringBuffer mdnBuffer = new StringBuffer(50);
            boolean groupLimitExceeded = false;
            for (String mdn : memberMdnList) {
                int mdnGroupLimitCount = groupMemberMap.get(mdn);
                knLogger.debug(methodName, "maxAbdgGroupsPerMemberCount - ", maxAbdgGroupsPerMemberCount);
                if (mdnGroupLimitCount >= maxAbdgGroupsPerMemberCount) {
                    groupLimitExceeded = true;
                    mdnBuffer.append(mdn).append(",");
                }
            }
            String mdnStr = null;
            if (!mdnBuffer.toString().equals("")) {
                mdnStr = mdnBuffer.deleteCharAt(mdnBuffer.lastIndexOf(",")).toString();
            }
            if (groupLimitExceeded) {
                knLogger.error(methodName, "Validation rule failed. ", "Members Abdg Group Limit exceeded", mdnStr
                        , ".Actual allowed limit is - ", maxAbdgGroupsPerMemberCount, " limit.");
                List<String> failedData = new ArrayList<>();
                if (mdnStr != null) {
                    failedData = Arrays.asList(mdnStr.split(","));
                }
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_ABDG_GROUPS_PER_MEM_REACHED,
                        "Validation Rule Failed. " + "Subscribers Count exceeds : " + maxAbdgGroupsPerMemberCount,
                        getEntityId(), getOperationType(), getRuleId(), failedData.toString(),
                        Integer.toString(maxAbdgGroupsPerMemberCount));
            }
        }
        knLogger.debug(methodName, "KnMaxAbdgGrpPerMemCountValidationRule Validated successfully.");
    }
}
