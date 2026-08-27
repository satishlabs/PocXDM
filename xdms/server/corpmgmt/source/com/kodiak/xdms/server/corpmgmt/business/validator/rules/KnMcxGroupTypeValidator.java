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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class KnMcxGroupTypeValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMcxGroupTypeValidator.class);
    private static final String ALLOWED_TYPES = "allowedTypes";

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        String allowedGroupTypes = getAttribute(ALLOWED_TYPES);
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
    		KnIPCorpGroupInfoDTO groupInputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();
            List<Integer> allowedGrpTypes = KnCorpUtil.convertStringToIntList(allowedGroupTypes, DELIM);
            List<Integer> invalidGrpTypeIds = new ArrayList<>();
            knLogger.debug(methodName, "obtained group type, ", groupInputDTO.getGroupType());
            knLogger.debug(methodName, "allowed type, ", allowedGrpTypes);

            if(allowedGrpTypes != null ){
               if(allowedGrpTypes.contains(groupInputDTO.getGroupType())) {
            	   
                   knLogger.debug(methodName, "validGroup ");

               }else {
            	   throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_TYPE_NOT_ALLOWED,
                           "Other than dispatch group not allowed ", getEntityId(), getOperationType(), getRuleId(),
                           "", "");

               }
             
           }
           knLogger.debug(methodName, "Validation Completed Successfully");
       }
   }
}
