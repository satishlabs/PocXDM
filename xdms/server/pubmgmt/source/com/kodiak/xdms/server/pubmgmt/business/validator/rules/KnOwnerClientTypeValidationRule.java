/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicContactPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicGroupPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnConstants;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.Arrays;
import java.util.Collection;

public class KnOwnerClientTypeValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnOwnerClientTypeValidationRule.class);
    private static final String ALLOWED_TYPES = "allowedTypes";

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     */
    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY : corporate Owner client type validation ");

        String allowedClientTypes = getAttribute(ALLOWED_TYPES);
        String[] subTypes = allowedClientTypes.split(DELIM);
        Collection<String> clntTypeList = Arrays.asList(subTypes);
        KnSubsProfileDTO subsProfile;
        if (persistDTO instanceof KnDynamicContactPersistDTO) {
            KnDynamicContactPersistDTO dynamicContPerDto = (KnDynamicContactPersistDTO) persistDTO;
            subsProfile = dynamicContPerDto.getSubsProfileDTO();

        }else if(persistDTO instanceof KnDynamicGroupPersistDTO){
            KnDynamicGroupPersistDTO groupInfoPersistDTO = (KnDynamicGroupPersistDTO) persistDTO;
            subsProfile = groupInfoPersistDTO.getSubsProfile();
        } else{
            knLogger.error(methodName, "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnDynamicContactPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, "");
        }
        if (!clntTypeList.contains(String.valueOf(subsProfile.getClientType()))) {
            knLogger.error(methodName, "Owner MDN is not TP Client - ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.OWNER_MDN_NOT_TP_CLIENT,
                    "Owner MDN is not TP Client - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, "");
        }
    }
}
