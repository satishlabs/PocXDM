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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.Arrays;
import java.util.Collection;
import com.kodiak.logger.KnLogger;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnGrpLocWatcherCountValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar        01.11.2016      8.1.2
 * <p/>
 * <p/>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnGrpLocWatcherCountValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGrpLocWatcherCountValidationRule.class);
    private String CLASS = KnGrpLocWatcherCountValidationRule.class.getName();


    /**
     * This method is used to validate the data for locWatcher per Group.
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO groupInputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();
                if (KnConstants.STANDARD_GROUP == groupInputDTO.getGroupType()) {
                    int maxLocWatcherPerGroup = corpGroupInfoPersistDTO.getMaxLocWatcherPerGroup();
                    knLogger.debug(methodName, "maxLocWatcherPerGroup  ", maxLocWatcherPerGroup);
                    Collection<String> requestLocWatcherSubscribers = corpGroupInfoPersistDTO.getRequestLocWatcherSubscribers();
                    Collection<String> dbLocWatcherSubscribers = corpGroupInfoPersistDTO.getDbLocWatcherSubscribers();
                    Collection<String> removeMdnList = corpGroupInfoPersistDTO.getRemovedMemberList();
                    Collection<KnCorpGroupMemberDTO> modifiedInputMembers = groupInputDTO.getModifiedMembers();

                    int removedLocWatcherAsPerRequest = 0;
                    int sameLocWatcherMemAsOfRequestAndDB = 0;
                    int requestLocWatcherMembCount = 0;
                    int dbLocWatcherMemCount = 0;
                    int modifiedLocWatcherMemCount = 0;

                    if (requestLocWatcherSubscribers != null) {
                        requestLocWatcherMembCount = requestLocWatcherSubscribers.size();
                    }
                    knLogger.debug(methodName, "requestLocWatcherMembCount  ", requestLocWatcherMembCount);
                    if (dbLocWatcherSubscribers != null) {
                        dbLocWatcherMemCount = dbLocWatcherSubscribers.size();
                    }
                    knLogger.debug(methodName, "dbLocWatcherMemCount  ", dbLocWatcherMemCount);
                    if (removeMdnList != null) {
                        for (String mdn : removeMdnList) {
                            //i.e locWatcher in the removed list
                            if (dbLocWatcherSubscribers != null && dbLocWatcherSubscribers.contains(mdn)) {
                                removedLocWatcherAsPerRequest++;
                            }
                        }
                    }
                    knLogger.debug(methodName, "removedLocWatcherAsPerRequest  ", removedLocWatcherAsPerRequest);
                    if (requestLocWatcherSubscribers != null) {
                        for (String mdn : requestLocWatcherSubscribers) {
                            //i.e same db LocWatcher in the added list
                            if (dbLocWatcherSubscribers != null && dbLocWatcherSubscribers.contains(mdn)) {
                                sameLocWatcherMemAsOfRequestAndDB++;
                            }
                        }
                    }

                    if (modifiedInputMembers != null) {
                        for (KnCorpGroupMemberDTO memDto : modifiedInputMembers) {
                            if (memDto.getLocWatcher() != KnConstants.LOCWATCHER &&
                                    (dbLocWatcherSubscribers != null && dbLocWatcherSubscribers.contains(memDto.getMdn()))) {
                                modifiedLocWatcherMemCount++;
                            }
                        }
                    }

                    knLogger.debug(methodName, "sameDispMemAsOfRequestAndDB  ", sameLocWatcherMemAsOfRequestAndDB);
                    int dispatcherSubscriberCount = ((requestLocWatcherMembCount + dbLocWatcherMemCount) - (removedLocWatcherAsPerRequest + sameLocWatcherMemAsOfRequestAndDB + modifiedLocWatcherMemCount));
                    knLogger.debug(methodName, "dispatcherSubscriberCount  ", dispatcherSubscriberCount);
                    if (groupInputDTO.getGroupType() != KnConstants.BROADCAST_GROUP && requestLocWatcherSubscribers != null) {
                        if (dispatcherSubscriberCount > maxLocWatcherPerGroup) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAXIMUM_LOCWATCHER_EXCEEDED_FOR_GROUP,
                                    "Mulitple LocWatcher not allowed for a group.", getEntityId(), getOperationType(), getRuleId(),
                                    Arrays.asList(maxLocWatcherPerGroup).toString(), "");
                        }
                    }
                }
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Completed Successfully");
        }
    }
}
