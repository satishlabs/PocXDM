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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.kodiak.logger.KnLogger;

import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT;
import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;

/**
 * ************************************************************************
 * <p/>
 * File name: KnMinDispMemCountValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name Date Release
 * -------------------- ------------ -------------------------------------
 * Namita P Nair 12/26/11 7.2
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
public class KnMinDispMemCountValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMinDispMemCountValidationRule.class);
    private String CLASS = KnMinDispMemCountValidationRule.class.getName();

    /**
     * This method is used to validate the datafor unique sublist Name for the corporation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - " , persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupPersistDTO.getInputDTO();
                int groupType = groupInfoDTO.getGroupType();
                boolean groupTypeChanged = groupInfoDTO.isGroupTypeChanged();
                Collection<String> dispatcherList = corpGroupPersistDTO.getRequestDispatcherSubscribers();
                if (groupTypeChanged && groupType == KnConstants.DISPATCH_GROUP) {
                    if (dispatcherList == null || dispatcherList.isEmpty()) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.DISPATCHER_DOES_NOT_EXIST_FOR_GROUP,
                                "Dispatch subscriber Does not exist for the Dispatch Group on Group Type Change.", getEntityId(), getOperationType(), getRuleId(),
                                Arrays.asList(groupType).toString(), "");
                    }
                } else if (groupType == KnConstants.DISPATCH_GROUP) {
                    Collection<String> removeMdnList = groupInfoDTO.getRemovedMemberMdns();
                    knLogger.debug( methodName, "removeMdnList",removeMdnList);
                    Collection<String> dbDispatchSubsc = corpGroupPersistDTO.getDbDispatcherSubscribers();
                    knLogger.debug( methodName, "dbDispatchSubsc",dbDispatchSubsc);
                    knLogger.debug( methodName, "dispatcherList",dispatcherList);
                    int count = 0;
                    if(dbDispatchSubsc != null && !dbDispatchSubsc.isEmpty()){
                        count = dbDispatchSubsc.size();
                    }
                    if (removeMdnList != null) {
                        for (String mdn : removeMdnList) {
                            //i.e dispatcher in the removed list
                            if (dbDispatchSubsc != null && dbDispatchSubsc.contains(mdn)) {
                                count --;
                                //if the DB does not have any more dispatchers and no dipatcher passed in request throw error
                            }
                        }
                    }
                    knLogger.debug( methodName, "count",count);
                    if (count == 0 && (dispatcherList == null || dispatcherList.isEmpty())) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.DISPATCHER_DOES_NOT_EXIST_FOR_GROUP,
                                "Dispatch subscriber Does not exist for the Dispatch Group on Group Type Change.", getEntityId(), getOperationType(), getRuleId(),
                                removeMdnList.toString(), "");
                    }
                }

            } else if (persistDTO instanceof KnContactDetailsPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnContactDetailsPersistDTO - " , persistDTO);
                KnContactDetailsPersistDTO corpContactPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
                Map<String, KnCorpSubscriberDTO> contactMap = corpContactPersistDTO.getContactCorpDetails();
                if (contactMap != null) {
                	List<String> invalidExtSubs=new ArrayList<String>();
                    for (KnCorpSubscriberDTO subsc : contactMap.values()) {
                        if (subsc.getClientType() == DISPATCH_CLIENT.value()
                                || subsc.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
                        	invalidExtSubs.add(subsc.getMdn());
                        }
                    }
                    if(invalidExtSubs.size() > 0){
                    	knLogger.error("Dispatch/3rd Party Dispatcher subscriber can not be part of Group.");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.DISPATCHER_SUBSCRIBER_CANNOT_BE_ADDED_AS_EXTERNAL_CONTACT,
                            "Dispatch/3rd Party Dispatcher subscriber can not be part of Group.", getEntityId(), getOperationType(), getRuleId(),
                            invalidExtSubs.toString(), "");
                    }
                }

            }
        } finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully for the min dispatcher member count");
        }
    }
}
