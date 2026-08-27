/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpActivationDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGenActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/29/11
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnSubsAuthStatValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSubsAuthStatValidationRule.class);
    private String CLASS = KnEmailValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating Subscriber auth status.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, "persistDTO obtained from the request is - ", persistDTO);
        try {
            if (persistDTO instanceof KnCorpActivationPersistDTO) {
                KnCorpActivationPersistDTO activationPersistDTO = (KnCorpActivationPersistDTO) persistDTO;
                knLogger.debug( methodName, "DTO passed in the request is - ", activationPersistDTO);
                knLogger.debug( methodName, "Validation the service auth status of the subscriber.");
//                IInputDTO inputDTO = activationPersistDTO.getInputDTO();
                int serviceAuthStatus = activationPersistDTO.getServiceAuthStatus();
                KnIPCorpActivationDTO inputDto=(KnIPCorpActivationDTO) activationPersistDTO.getInputDTO();
                knLogger.debug( methodName, "Subscribers serviceAuthStatus is - ", serviceAuthStatus);
                if (serviceAuthStatus == com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()) {
                    knLogger.debug( methodName, "Subscriber is deactivated");
                    if(inputDto.getMdn() != null){
                    	throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_DEACTIVATED,
                                "Subscriber is deactivated", getEntityId(),
                                getOperationType(), getRuleId(), Arrays.asList(inputDto.getMdn()).toString(), "");
                    }else if(inputDto.getMdnList() != null && inputDto.getMdnList().size() > 0){
                    	throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_DEACTIVATED,
                                "Subscriber is deactivated", getEntityId(),
                                getOperationType(), getRuleId(), inputDto.getMdnList().toString(), "");
                    }
                }
            } else if (persistDTO instanceof KnCorpGenActivationPersistDTO) {
                KnCorpGenActivationPersistDTO activationPersistDTO = (KnCorpGenActivationPersistDTO) persistDTO;
                Collection<KnCorpSubscriberDTO> corpSubscriberList = activationPersistDTO.getSubscriberInfoList();
                List<String> deactivatedMdnList=new ArrayList<String>();
                for (KnCorpSubscriberDTO corpSubscriber : corpSubscriberList) {
                    int serviceAuthStatus = corpSubscriber.getServiceAuthStatus();
                    knLogger.debug( methodName, "Subscribers serviceAuthStatus is - ", serviceAuthStatus);
                    if (serviceAuthStatus == com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()) {
                        knLogger.debug( methodName, "Subscriber is deactivated");
                        deactivatedMdnList.add(corpSubscriber.getMdn());
                    }
                }
                if(deactivatedMdnList.size() > 0){
                	knLogger.error("Subscriber is deactivated ",deactivatedMdnList);
                	throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_DEACTIVATED,
                            "Subscriber is deactivated", getEntityId(),
                            getOperationType(), getRuleId(),deactivatedMdnList.toString(), "");
                }
            } else if (persistDTO instanceof KnCorpTGSPersistDTO) {
            	KnCorpTGSPersistDTO activationPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
                knLogger.debug( methodName, "DTO passed in the request is - ", activationPersistDTO);
                knLogger.debug( methodName, "Validation the service auth status of the subscriber.");
                int serviceAuthStatus = activationPersistDTO.getServiceAuthStatus();
                knLogger.debug( methodName, "Subscribers serviceAuthStatus is - ", serviceAuthStatus);
                if (serviceAuthStatus != com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.ACTIVATED.value()) {
                    knLogger.debug( methodName, "Subscriber is deactivated");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_SERVICE_STATUS,
                    		getOperationType(), getRuleId(), Arrays.asList(3).toString(), "");
                }
            } else if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO subsDetails = (KnContactDetailsPersistDTO) persistDTO;
                IInputDTO inputDTO = persistDTO.getInputDTO();
                KnIPSubscriberInfoDTO subscriberInfoDTO = new KnIPSubscriberInfoDTO();
                if (inputDTO instanceof KnIPSubscriberInfoDTO) {
                    subscriberInfoDTO = (KnIPSubscriberInfoDTO) inputDTO;
                }
                if (subscriberInfoDTO.getUserId() != null &&
                        subsDetails.getServiceAuthStatus() == com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()) {
                    knLogger.debug(methodName, "Requested UserId is suspended");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_DEACTIVATED,
                            "Requested UserId is suspended",
                            getEntityId(), getOperationType(), getRuleId(), Arrays.asList(subscriberInfoDTO.getUserId()).toString(), "");
                }
            } else {
                knLogger.error( "validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnCorpActivationPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }  
            } finally {
            knLogger.debug(methodName, "EXIT: Validation Completed Successfully for the Subscribers Auth Status");
        }

    }
}
