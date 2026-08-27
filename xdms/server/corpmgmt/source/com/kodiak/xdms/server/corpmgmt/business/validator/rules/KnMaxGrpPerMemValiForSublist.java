/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.kodiak.logger.KnLogger;

/**
 * This validation rule validated the Max allowed group per subscribers in the corporation.
 * This is same as "KnMaxGrpPerMemCountValidationRule" but is will be used for modify sublist request only as existing rule
 * does not supports this requirement because of >= check.
 */
public class KnMaxGrpPerMemValiForSublist extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxGrpPerMemValiForSublist.class);
    private final String CLASS = KnMaxGrpPerMemValiForSublist.class.getName();

    /**
     * This method implements the actual logic for validation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     */
    public void validate() throws KnValidationException {
        IPersistenceDTO persistDTO = getDTO();
        String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY : Validating if total members group count exceeds max groups ",
                "per member limit.");
        Map<String, Integer> groupMemberMap = null;
        int maxGroupsPerMemberCount = 0;
        int maxGrpsPerCriClient = 0;
        List<String> criClientList = new ArrayList<>();
        KnSublistDetailsPersistDTO sublistDetailsPersistDTO;
        if (persistDTO instanceof KnSublistDetailsPersistDTO) {
            sublistDetailsPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
            groupMemberMap = sublistDetailsPersistDTO.getSubsGroupCountMap();
            maxGroupsPerMemberCount = sublistDetailsPersistDTO.getMaxGroupsPerMemberCount();
            maxGrpsPerCriClient = sublistDetailsPersistDTO.getMaxGroupsPerCRIClient();
            criClientList = sublistDetailsPersistDTO.getCriClientList();
        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO){
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            groupMemberMap = bcGrpPersistDTO.getSubsGroupCountMap();
            maxGroupsPerMemberCount = bcGrpPersistDTO.getMaxGroupsPerMemberCount();
        }else {
            knLogger.error(methodName, "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "", "");
        }

        knLogger.debug(methodName, "Max groups per Members allowed - ", maxGroupsPerMemberCount);
        if (groupMemberMap != null && !groupMemberMap.isEmpty()) {
            Collection<String> memberMdnList = groupMemberMap.keySet();
            List<String> limitExceededMdns=new ArrayList<String>();
            for (String mdn : memberMdnList) {
                int mdnGroupLimitCount = groupMemberMap.get(mdn);
                knLogger.debug(methodName, "mdnGroupLimitCount - ", mdnGroupLimitCount);
                knLogger.debug(methodName, "maxGrpsPerCriClient - ", maxGrpsPerCriClient);
                if(criClientList.contains(mdn)){
                    if(mdnGroupLimitCount > maxGrpsPerCriClient){
                        limitExceededMdns.add(mdn);
                    }
                }else if (mdnGroupLimitCount > maxGroupsPerMemberCount) {
                	limitExceededMdns.add(mdn);
                }
            }

            if (limitExceededMdns.size() > 0) {
                knLogger.error(methodName, "Validation rule failed. ", "Members Group Limit exceeded", limitExceededMdns
                        , ".Actual allowed limit is - ", maxGroupsPerMemberCount, " limit.");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GROUPS_PER_MEM_REACHED, "Validation Rule Failed. " +
                        "Subscribers Count exceeds : " + maxGroupsPerMemberCount, getEntityId(),
                        getOperationType(), getRuleId(),limitExceededMdns.toString() ,"");

            }
        }
        knLogger.debug(methodName, "KnMaxGrpPerMemValiForSublist Validated successfully.");

    }
}