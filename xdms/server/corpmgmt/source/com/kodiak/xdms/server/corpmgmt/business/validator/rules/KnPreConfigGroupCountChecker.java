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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;

import static com.kodiak.common.resources.KnConstants.AREA_BASED_DYNAMIC_GROUP;

public class KnPreConfigGroupCountChecker extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPreConfigGroupCountChecker.class);
    private String CLASS = KnMaxGrpCountValidationRule.class.getName();
    private static final int IS_PRE_CONFIG_GROUP_ENABLE = 1;
    /**
     * This method is used to validate the datafor unique sublist Name for the corporation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     */
    public void validate() throws KnValidationException {


        final String methodName = "validates()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
            KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
           if(corpGroupInfoPersistDTO.getIsPreConfiguredGroup() != null && corpGroupInfoPersistDTO.getIsPreConfiguredGroup() == IS_PRE_CONFIG_GROUP_ENABLE) {
               Integer systemMaxPerConfigGroup = corpGroupInfoPersistDTO.getSystemMaxAllowedPreConfigGroup() == null ? 0 : Integer.parseInt(corpGroupInfoPersistDTO.getSystemMaxAllowedPreConfigGroup());
               Integer dbReqPreConfigGroupMax = corpGroupInfoPersistDTO.getDbPreConfiguredGroupCount();
               knLogger.debug("maxCount in the system ", systemMaxPerConfigGroup, "....Numebr of groups in the DB +requets-->", dbReqPreConfigGroupMax);

               if (dbReqPreConfigGroupMax >= systemMaxPerConfigGroup) {
                   throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_PRE_CONFIG_GROUPS_REACHED,
                           "Groups limit for coporation has exceeded.", getEntityId(), getOperationType(), getRuleId(),
                           Arrays.asList(corpGroupInfoPersistDTO.getMaxGroups()).toString(), "");
               }
           }
        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            if (bcGrpPersistDTO.getIsPreConfiguredGroup() != null && bcGrpPersistDTO.getIsPreConfiguredGroup() == IS_PRE_CONFIG_GROUP_ENABLE) {
                Integer systemMaxPerConfigGroup = bcGrpPersistDTO.getSystemMaxAllowedPreConfigGroup() == null ? 0 : Integer.parseInt(bcGrpPersistDTO.getSystemMaxAllowedPreConfigGroup());
                Integer dbReqPreConfigGroupMax = bcGrpPersistDTO.getDbPreConfiguredGroupCount();
                knLogger.debug("maxCount in the system ", systemMaxPerConfigGroup, "....Numebr of groups in the DB +requets-->", dbReqPreConfigGroupMax);
                if (dbReqPreConfigGroupMax >= systemMaxPerConfigGroup) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_PRE_CONFIG_GROUPS_REACHED,
                            "Groups limit for coporation has exceeded.", getEntityId(), getOperationType(), getRuleId(),
                            Arrays.asList(bcGrpPersistDTO.getMaxGroups()).toString(), "");
                }
            }
        }
    }
}
