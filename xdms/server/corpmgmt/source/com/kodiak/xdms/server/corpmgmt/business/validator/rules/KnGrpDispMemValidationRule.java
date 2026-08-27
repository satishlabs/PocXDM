/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kodiak.logger.KnLogger;


import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT;
import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/14/11
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnGrpDispMemValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnGrpDispMemValidationRule.class);
    private String CLASS = KnGrpDispMemValidationRule.class.getName();

    /**
     * This method is used to validate the data for unique sublist Name for the corporation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, "persistDTO obtained from the request is - " , persistDTO);
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - " , persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                knLogger.debug( methodName, "corpGroupInfoPersistDTO - " , corpGroupInfoPersistDTO);
                KnIPCorpGroupInfoDTO groupInputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();
                knLogger.debug( methodName, "groupInputDTO - " , groupInputDTO);
                Collection<String> dispatcherSubscribers = corpGroupInfoPersistDTO.getRequestDispatcherSubscribers();
                knLogger.debug( methodName, "dispatcherSubscribers - " , dispatcherSubscribers);
                Collection<KnCorpSubscriberDTO> pocSubscriberList = corpGroupInfoPersistDTO.getAddedMdnDTO();
                knLogger.debug( methodName, "pocSubscriberList - " , pocSubscriberList);
                int dispatchCLientCount = 0;
                if (groupInputDTO.getGroupType() == KnConstants.DISPATCH_GROUP) {
                    if (pocSubscriberList != null && !pocSubscriberList.isEmpty()) {
                        for (KnCorpSubscriberDTO subsc : pocSubscriberList) {
                            if (subsc.getClientType() == DISPATCH_CLIENT.value()
                                    || subsc.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
                                dispatchCLientCount++;
                            }
                        }
                    }
                    if (dispatcherSubscribers != null && !dispatcherSubscribers.isEmpty()
                            && dispatcherSubscribers.size() != dispatchCLientCount) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.DISPATCHER_NOT_A_DISPATCH_CLIENT,
                                "Dispatch Subcriber passed is not a dispatch client.", getEntityId(), getOperationType(),
                                getRuleId(), dispatcherSubscribers.toString(), "");
                    }
                } else {
                    if (corpGroupInfoPersistDTO.getMcxGrpInd()!=null&&corpGroupInfoPersistDTO.getMcxGrpInd() == KnConstants.MCX_GROUP_INDICATOR) {
                        knLogger.info(methodName,"mcx group hence skipping the validation rule");
                    } else {
                        List<String> dispatchMdnList = new ArrayList<>();
                        if (pocSubscriberList != null && !pocSubscriberList.isEmpty()) {
                            for (KnCorpSubscriberDTO subsc : pocSubscriberList) {
                                if (subsc.getClientType() == DISPATCH_CLIENT.value()
                                        || subsc.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
                                    dispatchCLientCount++;
                                    dispatchMdnList.add(subsc.getMdn());
                                }
                            }
                        }
                        if (dispatchCLientCount > 0) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.DISPATCHER_NOT_A_ALLOWED_FOR_STAND_GROUP,
                                    "Dispatch Subcriber passed is not a dispatch client.", getEntityId(), getOperationType(),
                                    getRuleId(), dispatchMdnList.toString(), "");
                        }
                    }

                }
            }
        } finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully");
        }
    }
}
