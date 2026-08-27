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

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class KnGroupTypeValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGroupTypeValidator.class);
    private static final String ALLOWED_TYPES = "allowedTypes";

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        String allowedGroupTypes = getAttribute(ALLOWED_TYPES);
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpTGSPersistDTO) {
            knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
            KnCorpTGSPersistDTO corpTGSPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            List<Integer> allowedGrpTypes = KnCorpUtil.convertStringToIntList(allowedGroupTypes, DELIM);
            List<Integer> invalidGrpTypeIds = new ArrayList<>();
            Map<Integer,Integer> grpListMap=corpTGSPersistDTO.getGrpIdTypeMap();
            if(grpListMap != null ){
                Iterator<Entry<Integer, Integer>> grpIdListIterator=grpListMap.entrySet().iterator();
                while(grpIdListIterator.hasNext()){
               	Entry<Integer, Integer> grpListEntry=grpIdListIterator.next();
                	if(!allowedGrpTypes.contains(grpListEntry.getValue())){
               		   invalidGrpTypeIds.add(grpListEntry.getKey());
                	}
                }
               }
               if (invalidGrpTypeIds.size() > 0) {
                   knLogger.error(methodName, "Invalid group types - ", invalidGrpTypeIds);
                   throw new KnCorpBOValidationException(KnErrorCodes.Validator.BROADCAST_GROUP_NOT_ALLOWED,
                           "Broadcast group not allowed", getEntityId(), getOperationType(), getRuleId(),invalidGrpTypeIds.toString(), "");
               }
           }
           knLogger.debug(methodName, "Validation Completed Successfully");
       }
   }
