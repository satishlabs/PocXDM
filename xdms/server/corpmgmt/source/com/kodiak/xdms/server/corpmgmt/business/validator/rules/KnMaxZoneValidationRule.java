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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KnMaxZoneValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxZoneValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpUserProfilePersistDTO){
            KnCorpUserProfilePersistDTO dto=(KnCorpUserProfilePersistDTO)persistDTO;
            Set<KnCorpGroupListInfoDTO> requestUpmGroupList=new HashSet<>();

            if(getOperationType().equals(KnOperationTypes.CREATE_USER_PROFILE)){
                requestUpmGroupList=dto.getUserProfileDTO().getGroupList();
                knLogger.debug(methodName," request create UpmGroupList zone:",requestUpmGroupList);
                int maxSystemZone=dto.getMaxZone();
                int maxRequestedZone = requestUpmGroupList.stream().filter(q->q.getGroupZone()!=null)
                        .map(KnCorpGroupListInfoDTO::getGroupZone)
                        .mapToInt(zone -> zone).max().orElse(0);
                knLogger.debug(methodName," maxSystemZone:",maxSystemZone," maxRequestedZone :",maxRequestedZone);

                if (maxRequestedZone > maxSystemZone) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_ZONE_REACHED,
                            "Max system level Zone reached", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList(maxRequestedZone).toString(), "");
                }
            }else if(getOperationType().equals(KnOperationTypes.UPDATE_USER_PROFILE)){
                if(dto.getModifyUserProfileDTO().getAddedGroupList()!=null)
                    requestUpmGroupList.addAll(dto.getModifyUserProfileDTO().getAddedGroupList());
                if(dto.getModifyUserProfileDTO().getModifiedGroupList()!=null)
                    requestUpmGroupList.addAll(dto.getModifyUserProfileDTO().getModifiedGroupList());

                knLogger.debug(methodName," request modify UpmGroupList zone:",requestUpmGroupList);
                int maxSystemZone=dto.getMaxZone();
                int maxRequestedZone = requestUpmGroupList.stream().filter(q->q.getGroupZone()!=null)
                        .map(KnCorpGroupListInfoDTO::getGroupZone)
                        .mapToInt(zone -> zone).max().orElse(0);
                knLogger.debug(methodName," maxSystemZone:",maxSystemZone," maxRequestedZone :",maxRequestedZone);

                if (maxRequestedZone > maxSystemZone) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_ZONE_REACHED,
                            "Max system level Zone reached", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList(maxRequestedZone).toString(), "");
                }
            }
        }else {
            knLogger.error("validate()", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpUserProfilePersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }

    }
}
