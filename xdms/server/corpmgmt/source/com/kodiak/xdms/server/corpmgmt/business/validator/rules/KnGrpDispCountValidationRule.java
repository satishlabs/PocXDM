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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.Arrays;
import java.util.Collection;
import com.kodiak.logger.KnLogger;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/14/11
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnGrpDispCountValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnGrpDispCountValidationRule.class);
    private String CLASS = KnGrpDispCountValidationRule.class.getName();


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
        knLogger.debug( methodName, "persistDTO obtained from the request is - " , persistDTO);
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - " , persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO groupInputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();
                boolean multipleDispatcherAllowed = corpGroupInfoPersistDTO.isMultipleDispatcherAllowed();
                int maxDispatcherInDispatchGroup = corpGroupInfoPersistDTO.getMaxDispatchMembersPerDispatchGroup();
                knLogger.debug( methodName, "maxDispatcherInDispatchGroup  " , maxDispatcherInDispatchGroup);
                Collection<String> requestDispatcherSubscribers = corpGroupInfoPersistDTO.getRequestDispatcherSubscribers();
                Collection<String> dbDispatcherSubscribers = corpGroupInfoPersistDTO.getDbDispatcherSubscribers();
                Collection<String> removeMdnList = corpGroupInfoPersistDTO.getRemovedMemberList();

                int removedDispatcherAsPerRequest = 0;
                int sameDispMemAsOfRequestAndDB = 0;
                int requestDispMembCount = 0;
                int dbDispatchMemCount = 0;

                if(requestDispatcherSubscribers != null){
                    requestDispMembCount = requestDispatcherSubscribers.size();
                }
                knLogger.debug( methodName, "requestDispMembCount  " , requestDispMembCount);
                if(dbDispatcherSubscribers != null){
                    dbDispatchMemCount = dbDispatcherSubscribers.size();
                }
                knLogger.debug( methodName, "dbDispatchMemCount  " , dbDispatchMemCount);
                if (removeMdnList != null) {
                    for (String mdn : removeMdnList) {
                        //i.e dispatcher in the removed list
                        if (dbDispatcherSubscribers != null && dbDispatcherSubscribers.contains(mdn)) {
                            removedDispatcherAsPerRequest ++;
                        }
                    }
                }
                knLogger.debug( methodName, "removedDispatcherAsPerRequest  " , removedDispatcherAsPerRequest);
                if (requestDispatcherSubscribers != null) {
                    for (String mdn : requestDispatcherSubscribers) {
                        //i.e same db dispatcher in the added list
                        if (dbDispatcherSubscribers != null && dbDispatcherSubscribers.contains(mdn)) {
                            sameDispMemAsOfRequestAndDB ++;
                        }
                    }
                }
                knLogger.debug( methodName, "sameDispMemAsOfRequestAndDB  " , sameDispMemAsOfRequestAndDB);
                int dispatcherSubscriberCount = ((requestDispMembCount + dbDispatchMemCount) - (removedDispatcherAsPerRequest + sameDispMemAsOfRequestAndDB));
                knLogger.debug( methodName, "dispatcherSubscriberCount  " , dispatcherSubscriberCount);
                if (groupInputDTO.getGroupType() == KnConstants.DISPATCH_GROUP && requestDispatcherSubscribers != null) {
                    if (multipleDispatcherAllowed == Boolean.TRUE) {
                        if (dispatcherSubscriberCount > maxDispatcherInDispatchGroup) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAXIMUM_DISPATCHER_EXCEEDED_FOR_DISPATCH_GROUP,
                                    "Mulitple dispatcher not allowed for a group.", getEntityId(), getOperationType(), getRuleId(),
                                    Arrays.asList(maxDispatcherInDispatchGroup).toString(), "");
                        }
                    }
                }
            }
        } finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully");
        }
    }
}
