/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.List;

public class KnPreConfigGroupAllowedUpm extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnPreConfigGroupAllowedUpm.class);
    private String CLASS = KnPreConfigGroupAllowedUpm.class.getName();


    /**
     * This method is used to validate whether preconfigured groups are added while creating/modifying userprofile.
     * @throws KnValidationException
     * @throws KnBOException
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO userProfilePersistDTO = (KnCorpUserProfilePersistDTO) persistDTO;
            List<Integer> isPreConfiguredGroupList = userProfilePersistDTO.getIsPreConfiguredGroupList();
            knLogger.debug(methodName, "ENTRY isPreConfiguredGroupList:",isPreConfiguredGroupList);
            if(isPreConfiguredGroupList!=null && isPreConfiguredGroupList.contains(1)){
                knLogger.error(methodName, "PreConfigured group is not allowed for UserProfile");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.PRECONFIG_GROUP_IS_NOT_ALLOWED
                        ,"Preconfigured group is not allowed for UserProfile", getEntityId(), getOperationType(), getRuleId(), "", "");
            }

        }
    }

    }

