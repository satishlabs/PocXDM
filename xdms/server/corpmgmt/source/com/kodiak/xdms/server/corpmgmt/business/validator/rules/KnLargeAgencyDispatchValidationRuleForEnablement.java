package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpRecordingFsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;

import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DISPATCH_CLIENT;
import static com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes.CORP_SUBS_PROFILE_MANAGER;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.SUBSCRIBER_NOT_A_DISPATCH_CLIENT;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.SYSTEM_LEVEL_LARGE_AGENCY_DISPATCH_FLAG_DISABLED;

public class KnLargeAgencyDispatchValidationRuleForEnablement extends KnValidatorRule{
    private static final KnLogger knLogger = KnLogger.getLogger(KnLargeAgencyDispatchValidationRuleForEnablement.class);
    public static Integer ENABLED = 1;
    public static Integer DISABLED = 0;

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY Validating largeAgencyDispatch flag ");
        boolean sysLargeAgencyDispatch = false;
        boolean corpLargeAgencyDispatch = false;
        int reqLargeAgencyDispatchVal = 0;

        if (persistDTO instanceof KnContactDetailsPersistDTO) {
            //DTO coming from updateCorpAdminFS API
            KnContactDetailsPersistDTO contactPersistDto = (KnContactDetailsPersistDTO) persistDTO;
            reqLargeAgencyDispatchVal = contactPersistDto.getReqLargeAgencyDispatchList().contains(ENABLED) ? ENABLED : DISABLED;
            if (reqLargeAgencyDispatchVal == ENABLED) {
                if ((contactPersistDto.getClientType() == DISPATCH_CLIENT) || (contactPersistDto.getClientType() == THIRDPARTYDISPATCHERCLIENT.value())) {
                    sysLargeAgencyDispatch = contactPersistDto.getSysLargeAgencyDispatch();
                    corpLargeAgencyDispatch = contactPersistDto.getCorpLargeAgencyDispatch();
                } else {
                    knLogger.error(methodName, " Subscriber is not a Dispatch Client");
                    throw new KnCorpBOValidationException(SUBSCRIBER_NOT_A_DISPATCH_CLIENT,
                            " Subscriber is not a Dispatch Client ", CORP_SUBS_PROFILE_MANAGER,
                            SUBSCRIBER_NOT_A_DISPATCH_CLIENT, "", "", "");
                }
            }
        } else if (persistDTO instanceof KnCorpRecordingFsPersistDTO) {
            //DTO coming from updateCorporateFS API
            KnCorpRecordingFsPersistDTO corpRecordingFsPersistDTO = (KnCorpRecordingFsPersistDTO) persistDTO;
            sysLargeAgencyDispatch = corpRecordingFsPersistDTO.getSysLargeAgencyDispatch();
            reqLargeAgencyDispatchVal = (null != corpRecordingFsPersistDTO.getReqLargeAgencyDispatch()) ?
                        Integer.parseInt(corpRecordingFsPersistDTO.getReqLargeAgencyDispatch()) : DISABLED;
            corpLargeAgencyDispatch = true;  // this flag is set to true as this is not required for below validation
        }


        if (reqLargeAgencyDispatchVal == ENABLED && (!sysLargeAgencyDispatch || !corpLargeAgencyDispatch)) {
            knLogger.error(methodName, " Corp/System level largeAgencyDispatch flag is disabled");
            throw new KnCorpBOValidationException(SYSTEM_LEVEL_LARGE_AGENCY_DISPATCH_FLAG_DISABLED,
                    " Corp/System level largeAgencyDispatch flag is disabled ", CORP_SUBS_PROFILE_MANAGER,
                    SYSTEM_LEVEL_LARGE_AGENCY_DISPATCH_FLAG_DISABLED, "", "", "");
        }

        knLogger.debug(methodName, "EXIT: Validation Completed Successfully");
    }
}
