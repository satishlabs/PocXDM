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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;

import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DISPATCH_CLIENT;
import static com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes.CORP_SUBS_PROFILE_MANAGER;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.MORE_THAN_MAX_ALLOWED_CORP_GROUP;


public class KnLargeAgencyDispatchValidationRuleForDisablement extends KnValidatorRule{
    private static final KnLogger knLogger = KnLogger.getLogger(KnLargeAgencyDispatchValidationRuleForDisablement.class);
    public static Integer ENABLED = 1;
    public static Integer DISABLED = 0;

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY Validating largeAgencyDispatch flag ");
        int reqLargeAgencyDispatchVal = 0;
        boolean isSubsCorpGrpCountExceeded = false;
        int maxCorpGroupPerSubs = 0;

        if (persistDTO instanceof KnContactDetailsPersistDTO) {
            //DTO coming from updateCorpAdminFS API
            KnContactDetailsPersistDTO contactPersistDto = (KnContactDetailsPersistDTO) persistDTO;
            if (!contactPersistDto.getReqLargeAgencyDispatchList().contains(null)) {
                if ((contactPersistDto.getClientType() == DISPATCH_CLIENT) || (contactPersistDto.getClientType() == THIRDPARTYDISPATCHERCLIENT.value())) {
                    reqLargeAgencyDispatchVal = contactPersistDto.getReqLargeAgencyDispatchList().contains(ENABLED) ? ENABLED : DISABLED;
                    isSubsCorpGrpCountExceeded = contactPersistDto.getSubsCorpGrpCount() > contactPersistDto.getMaxCorpGroupPerSubs();
                    maxCorpGroupPerSubs = contactPersistDto.getMaxCorpGroupPerSubs();
                }
            }
        }

        if (reqLargeAgencyDispatchVal == DISABLED && (isSubsCorpGrpCountExceeded)) {
            knLogger.error(methodName, "The dispatcher can have only " + maxCorpGroupPerSubs +" groups." +
                    " Please delete the remaining groups for you to disable the large agency dispatch functionality");
            throw new KnCorpBOValidationException(MORE_THAN_MAX_ALLOWED_CORP_GROUP,
                    "The dispatcher can have only " + maxCorpGroupPerSubs + " groups. " +
                            "Please delete the remaining groups for you to disable the large agency dispatch functionality", CORP_SUBS_PROFILE_MANAGER,
                    MORE_THAN_MAX_ALLOWED_CORP_GROUP, "", "", "");
        }

        knLogger.debug(methodName, "EXIT: Validation Completed Successfully");
    }
}
