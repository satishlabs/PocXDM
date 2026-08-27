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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public class KnBroadcasterValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBCGrpFeatureValidator.class);

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            List<String> grpBroadcasters;
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            KnIPCorpGroupInfoDTO groupIpDTO = (KnIPCorpGroupInfoDTO) bcGrpPersistDTO.getInputDTO();
            grpBroadcasters = new ArrayList<>(bcGrpPersistDTO.getGrpBroadcasters());
            if (groupIpDTO.getOperationType().equals(KnOperationTypes.CREATE_BROADCAST_GROUP) || groupIpDTO.getOperationType().equals(KnOperationTypes.CREATE_VLG_BROADCAST_GROUP)) {
                Set<String> allDistMemebs = bcGrpPersistDTO.getAllDistinctMemebrs();
                grpBroadcasters.removeAll(allDistMemebs);

                if (grpBroadcasters.size() > 0) {
                    knLogger.error(methodName, "Broadcasters not exist in the group  - ", grpBroadcasters);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_BROADCASTER_NOT_EXIST,
                            "Subscribers Broadcaster bit disabled", getEntityId(), getOperationType(), getRuleId(), grpBroadcasters.toString(), "");
                }

            } else {
                List<KnCorpSubscriberDTO> finalMemList = bcGrpPersistDTO.getFinalGroupMemList();
                Collection<KnCorpGroupMemberDTO> modifiedMem = groupIpDTO.getModifiedMembers();
                List<String> invalidMdnList = new ArrayList<>();
                for (KnCorpGroupMemberDTO groupMemberDTO : modifiedMem) {
                    KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                    subscriberDTO.setMdn(groupMemberDTO.getMdn());
                    if(!finalMemList.contains(subscriberDTO)){
                        invalidMdnList.add(groupMemberDTO.getMdn());
                    }
                }
                if (invalidMdnList.size() > 0) {
                    knLogger.error(methodName, " Modified member Mdn does not exist - ", invalidMdnList);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MODIFIED_MEMBER_NOT_EXIST,
                            "Modifying member does not exist", getEntityId(), getOperationType(), getRuleId(), invalidMdnList.toString(), "");
                }
            }

        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass());
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
