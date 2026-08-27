/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnGroupTypeValidator.java
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

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class KnBCGroupValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBCGroupValidator.class);

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpTGSPersistDTO) {
            knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
            KnCorpTGSPersistDTO corpTGSPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            Set<Integer> reqGroupTypes = corpTGSPersistDTO.getReqGroupTypes();

            List<Integer> subsGroupList = corpTGSPersistDTO.getGrpListInCorp();
            Set<Integer> bcGrpIds = corpTGSPersistDTO.getBroadcastGrpIds();

            List<KnXDMTalkGroupInfoDTO> xdmTalkGroupInfoDTOs = corpTGSPersistDTO.getNewCampedGroups();
         //if broadcast group is available in request
           if(reqGroupTypes.contains(3)) {
               List<Integer> validGrpIds = new ArrayList<>();
               for (Integer grpId : bcGrpIds) {
                   if (subsGroupList.contains(grpId)) {
                       validGrpIds.add(grpId);
                   }
               }
               if (validGrpIds.size() == 0) {
                   knLogger.error(methodName, "broadcaster group is not having broadcaster  - ");
                   throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBS_NOT_A_BROADCASTER,
                           "subscriber is not a broadcaster in given group list", getEntityId(), getOperationType(), getRuleId(), Arrays.asList(0).toString(), "");
               }
               List<Integer> brdCastGrpIds=new ArrayList<Integer>();
               for (KnXDMTalkGroupInfoDTO xdmTalkGroupInfoDTO : xdmTalkGroupInfoDTOs) {
                   for (Integer grpId : validGrpIds) {
                       if (xdmTalkGroupInfoDTO.getPriority() != null && grpId.equals(xdmTalkGroupInfoDTO.getGroupId())) {
                    	   brdCastGrpIds.add(grpId);
                       }
                   }
               }
               if(brdCastGrpIds.size()>0){
            	   knLogger.error(methodName, "BroadCaster group not allowed",brdCastGrpIds);
                      throw new KnCorpBOValidationException(KnErrorCodes.Validator.BCGROUP_NOT_ALLOWED_IN_SCANLIST,
                       "Broadcast group is not allowed in scan list", getEntityId(), getOperationType(), getRuleId(), brdCastGrpIds.toString(), "");
               }
           }
        }
        knLogger.debug(methodName, "Validation Completed Successfully");
    }
}
