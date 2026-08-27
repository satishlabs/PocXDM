/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMCSXCAPGroupDocValidationRule.java
 * Subsystem:  XDMS
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Shashank Tewari      02/11/2020      11.0
 * <p/>
 * <p/>
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubMCSXCAPPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.util.ArrayList;
import java.util.List;
import static com.kodiak.xdms.server.pubmgmt.resources.KnConstants.KEY_DATATYPE_MDN;

public class KnMCSXCAPGroupDocValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMCSXCAPGroupDocValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "MCSGroupDoc validate()";
        knLogger.info(methodName, "ENTRY ");
        IPersistenceDTO persistDTO = getDTO();
        KnSubscriberPersistDTO knSubscriberPersistDTO = null;
        KnIPMCSDTO knIPMCSDTO = null;
        List<String> mdnList = null;
        int mcsCompliance = 0;
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO = null;
        List<String> groupMembers = null;
        int isPreConfiguredGroup =0;
        if (persistDTO instanceof KnPubMCSXCAPPersistDTO) {
            pubMCSXCAPPersistDTO = (KnPubMCSXCAPPersistDTO) persistDTO;
            groupMembers = pubMCSXCAPPersistDTO.getGroupMemberList();
            if (pubMCSXCAPPersistDTO.getIsPreConfiguredGroup() != null) {
            isPreConfiguredGroup = Integer.parseInt(pubMCSXCAPPersistDTO.getIsPreConfiguredGroup()); }
            knSubscriberPersistDTO = (KnSubscriberPersistDTO) pubMCSXCAPPersistDTO.getPersistenceDTO();
            mcsCompliance = knSubscriberPersistDTO.getMcpttCompliance();
        } else {
            knLogger.error(methodName, "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnPubMCSXCAPPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KEY_DATATYPE_MDN,"");
        }
        knLogger.debug(methodName, "isPreConfiguredGroup: " +isPreConfiguredGroup);
        knIPMCSDTO = (KnIPMCSDTO) pubMCSXCAPPersistDTO.getInputDTO();
        knLogger.debug(methodName, " MCPTTID or MDN from token :" + KnGDPRTemplate.mcpttId(knIPMCSDTO.getMcpttID()) + " mcsCompliance from Profile   " + mcsCompliance);
        if (mcsCompliance == 0 || mcsCompliance == 1) {
            if (mcsCompliance == 0) {
                mdnList = new ArrayList<>(1);
                mdnList.add(knIPMCSDTO.getMcpttID());
            } else {
                mdnList = pubMCSXCAPPersistDTO.getMdns();
            }
			knLogger.info(methodName, " MDNList = ",
					mcsCompliance == 0 ? KnGDPRTemplate.mcPttIdList(mdnList) : KnGDPRTemplate.mdnList(mdnList));
            if(mdnList == null){
                knLogger.error(methodName, "MDN List is null ");
                throw new KnPubBOValidationException(KnErrorCodes.Validator.OPERATION_NOT_ALLOWED,
                        "Validation Rule Failed , mcsCompliance/mcptt_compliance value is neither 0 nor 1 ",
                        getOperationType(), getRuleId(), KEY_DATATYPE_MDN, pubMCSXCAPPersistDTO.getMdn());
            }
            else if (mdnList.stream().noneMatch(groupMembers::contains) && ((isPreConfiguredGroup) != KnConstants.IS_PRE_CONFIG_GROUP_ENABLE)) {
                knLogger.error(methodName, "Validation failed, MDN(s) present in token is not a group member ");
                throw new KnPubBOValidationException(KnErrorCodes.Validator.MDN_DOESNOT_BELONG_TO_GROUP,
                        "Validation Rule Failed , MDN(s) present in token is not a group member ",
                        getOperationType(), getRuleId(), KEY_DATATYPE_MDN, pubMCSXCAPPersistDTO.getMdn());
            }
        } else {
            knLogger.error(methodName, "Validation Failed as mcsCompliance/mcptt_compliance value is neither 0 nor 1 : ", mcsCompliance);
            throw new KnPubBOValidationException(KnErrorCodes.Validator.OPERATION_NOT_ALLOWED,
                    "Validation Rule Failed , mcsCompliance/mcptt_compliance value is neither 0 nor 1 ",
                    getOperationType(), getRuleId(), KEY_DATATYPE_MDN, pubMCSXCAPPersistDTO.getMdn());
        }
        knLogger.info(methodName, "KnMCSXCAPGroupDocValidationRule Validated successfully.");
    }
}