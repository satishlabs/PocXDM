/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMinGrpMemCountValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        23-03-2011      7.0
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

import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.Arrays;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import static com.kodiak.common.resources.KnConstants.AREA_BASED_DYNAMIC_GROUP;

public class KnMinGrpMemCountValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMinGrpMemCountValidationRule.class);
    private String CLASS = KnMinGrpMemCountValidationRule.class.getName();
    private static final String STANDARD_GROUP_MINIMUM_MEMBERS = "standardGroupMinimumMembers";
    private static final String DISPATCH_GROUP_MINIMUM_MEMBERS = "dispatchGroupMinimumMembers";
    private static final String BROADCAST_GROUP_MINIMUM_MEMBERS = "broadcastGroupMinimumMembers";
    private static final String ABDG_GROUP_MINIMUM_MEMBERS = "abdgGroupMinimumMembers";


    /**
     * This method is used to validate the datafor unique sublist Name for the corporation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
            KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
            int mimimunMembers = 0;
            if (AREA_BASED_DYNAMIC_GROUP != groupInfoDTO.getClientType()) {
                if (groupInfoDTO.getGroupType() == KnConstants.STANDARD_GROUP) {
                    mimimunMembers = Integer.parseInt(getAttribute(STANDARD_GROUP_MINIMUM_MEMBERS));
                } else {
                    mimimunMembers = Integer.parseInt(getAttribute(DISPATCH_GROUP_MINIMUM_MEMBERS));
                }
            } else {
                mimimunMembers = Integer.parseInt(getAttribute(ABDG_GROUP_MINIMUM_MEMBERS));
            }
            if (corpGroupInfoPersistDTO.getGroupMemberCount() < mimimunMembers) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MIN_GROUP_MEMBER_LIMIT_ERROR,
                        "Minimun No of mebers for group not present.", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(mimimunMembers).toString(), "");
            }
        }else if(persistDTO instanceof KnCorpBCGrpPersistDTO){
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) bcGrpPersistDTO.getInputDTO();
            int minimunMembers = Integer.parseInt(getAttribute(BROADCAST_GROUP_MINIMUM_MEMBERS));
            int totalMemCount = 0;
            if(groupInfoDTO.getOperationType().equals(KnOperationTypes.CREATE_BROADCAST_GROUP)){
                totalMemCount = bcGrpPersistDTO.getAllDistinctMemebrs().size();
            }else{
                totalMemCount = bcGrpPersistDTO.getGroupMemberCount();
            }
            if(totalMemCount < minimunMembers){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MIN_GROUP_MEMBER_LIMIT_ERROR,
                        "Minimun No of mebers for group not present.", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(minimunMembers).toString(), "");
            }
        }
        knLogger.debug(methodName, "Validation Completed Successfully for the min group member count");
    }
}
