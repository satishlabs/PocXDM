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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.kodiak.logger.KnLogger;
import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT;
import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/15/11
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnDispSubsNotAlwdValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDispSubsNotAlwdValidationRule.class);
     private static final long serialVersionUID = 7526471155622676282L;
    private String CLASS = KnDispSubsNotAlwdValidationRule.class.getName();

    /**
     * This method is used to validate the datafor unique sublist Name for the corporation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        try {
        	List<String> dispMdnList=new ArrayList<String>();
            if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnSublistDetailsPersistDTO - " , persistDTO);
                KnSublistDetailsPersistDTO corpSublistInfoPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
                Collection<KnCorpSubscriberDTO> addedMemberList = corpSublistInfoPersistDTO.getAddedMdnDTOList();
                if (!KnCorpUtil.isObjectNullOrEmpty(addedMemberList)) {
                    for (KnCorpSubscriberDTO subsc : addedMemberList) {
                        if (subsc.getClientType() == DISPATCH_CLIENT.value()
                                || subsc.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
                        	dispMdnList.add(subsc.getMdn());
                        }
                    }
                    if(dispMdnList.size() > 0){
                    	knLogger.error("Dispatch subscriber can not be part of Sublist....");
                    	throw new KnCorpBOValidationException(KnErrorCodes.Validator.DISPATCHER_SUBSCRIBER_CANNOT_BE_PART_OF_SUBLIST,
                                "Dispatch subscriber can not be part of Sublist.", getEntityId(), getOperationType(), getRuleId(),
                                dispMdnList.toString(), "");
                    }
                }

            } else if (persistDTO instanceof KnContactDetailsPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnContactDetailsPersistDTO - " , persistDTO);
                KnContactDetailsPersistDTO corpContactPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
                Map<String, KnCorpSubscriberDTO> contactMap = corpContactPersistDTO.getContactCorpDetails();
                if (!KnCorpUtil.isObjectNullOrEmpty(contactMap) && !KnCorpUtil.isObjectNullOrEmpty(contactMap.values())) {
                    for (KnCorpSubscriberDTO subsc : contactMap.values()) {
                        if (subsc.getClientType() == DISPATCH_CLIENT.value()
                                || subsc.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
                        	dispMdnList.add(subsc.getMdn());
                        }
                    }
                    if(dispMdnList.size() > 0){
                    	knLogger.error("Dispatch subscriber can not be part of Group....");
                    	  throw new KnCorpBOValidationException(KnErrorCodes.Validator.DISPATCHER_SUBSCRIBER_CANNOT_BE_ADDED_AS_EXTERNAL_CONTACT,
                                  "Dispatch subscriber can not be part of Group.", getEntityId(), getOperationType(), getRuleId(),
                                  dispMdnList.toString(), "");
                    }
                }
            } 
          } finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully for the diapatch subscribers");
        }
    }
}
