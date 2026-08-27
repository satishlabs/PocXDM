/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ***************************************************************************
 * File name:   KnMdnAuthValidationRule.java
 * Subsystem:   Provisioning Library.
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Jan 11, 2011        7.0
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
 * ******************************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.business.validator.rules;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

public class KnMdnAuthValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMdnAuthValidationRule.class);
    private static final String className = KnMdnAuthValidationRule.class.getName();

    public void validate() throws KnValidationException {
        String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();

        knLogger.debug( methodName, "Validating mdn status with DTO - " , persistDTO);
        String mdn = null;
        KnSubsProfilePersistDTO subsProfilePersistDTO = null;
        if (persistDTO instanceof KnSubsProfilePersistDTO) {
            subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
            mdn = subsProfilePersistDTO.getMdn();
        } else {
            knLogger.error( methodName, "Invalid DTO is passed for validation " , persistDTO.getClass());
            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Un-expected dao DTO is passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
        }

        int serviceAuthStatus = subsProfilePersistDTO.getServiceAuthStatus();

        if (serviceAuthStatus == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()) {

            if(subsProfilePersistDTO.getServiceStatusAuthUser() == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value() ){
                knLogger.error( methodName, "For MDN [" , KnGDPRTemplate.mdn(mdn) , "],ServiceAuthStatusAuth received - " , subsProfilePersistDTO.getServiceStatusAuthUser() ,
                        ", Expected - " , KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value() ,
                        " or - " , KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value());
                throw new KnProvBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_IN_DEACTIVATED_STATE_AUTH_USER,
                        "Invalid Service Auth Status", getEntityId(), getOperationType(), getRuleId(),
                        KnProvConstants.KEY_DATATYPE_MDN);
            }
            knLogger.error( methodName, "For MDN [" , KnGDPRTemplate.mdn(mdn) , "],ServiceAuthStatus received - " , serviceAuthStatus ,
                    ", Expected - " , KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value() ,
                    " or - " , KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value());
            throw new KnProvBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_IN_DEACTIVATED_STATE,
                    "Invalid Service Auth Status", getEntityId(), getOperationType(), getRuleId(),
                    KnProvConstants.KEY_DATATYPE_MDN);
        }

        knLogger.info( methodName, "Successfully validated the Mdn Status for Activation");
    }
}
