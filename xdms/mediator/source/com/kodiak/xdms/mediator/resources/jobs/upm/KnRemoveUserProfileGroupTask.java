/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpUserProfileUtil;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPUserProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnRemoveUserProfileGroupTask.java
 * Subsystem:  corpmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Venkata Sudhakar Talluri    16 June, 2020           10.0.1
 * <p/>
 * <p/>
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
public class KnRemoveUserProfileGroupTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnRemoveUserProfileGroupTask.class);

    private KnGenInfoUtil genInfoUtil;
    private KnXDMCommonMediator commonMediator;
    private KnCorpUserProfileUtil corpUserProfileUtil;
    private KnCorpCommonInfoUtil commonInfoUtil;

    private String removedGroupId;
    private String corpId;
    private String taskId;
    private String sharedCorpIdStr;

    public KnRemoveUserProfileGroupTask(String removedGroupId, String corpId, String taskId,String sharedCorpIdStr) {

        this.corpId = corpId;
        this.removedGroupId = removedGroupId;
        this.taskId = taskId;
        this.sharedCorpIdStr = sharedCorpIdStr;
        commonMediator = KnXDMCommonMediator.getInstance();
        genInfoUtil = KnGenInfoUtil.getInstance();
        corpUserProfileUtil = new KnCorpUserProfileUtil();
        commonInfoUtil = new KnCorpCommonInfoUtil();

    }

    /**
     * Processing Logic
     * 1.Fetch all userProfileIds for the removedGroupId(input) from couchbase
     * 2.Remove groupId in UserProfile Documents got from step 1
     * 3.Fetch all profileMdns for the userProfileIds from step 1
     * 4.Update profileMdn etag and send user profile notifications to all the profile mdns
     */
    @Override
    public KnTaskResult executeTask() {
        final String methodName = "executeTask()";
        KnPersisterTxn removeGrpUpmTxn = null;
        String xdmsHome = null;
        KnCorpProfileDTO corpProfile = null;
        KnTaskResult taskResult=new KnTaskResult();
        List<Integer> sharedCorpList = null;
        try {
            removeGrpUpmTxn = KnPersisterTxn.getPersisterTxn();
            removeGrpUpmTxn.open();
            knLogger.info(methodName, " removedGroupId - ", removedGroupId, " getCorpProfileInfo - ", corpId , "sharedCorpIdStr - ",sharedCorpIdStr);
            corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, removeGrpUpmTxn);
            knLogger.debug(methodName, " corpProfile - ", corpProfile);
            xdmsHome = corpProfile.getXdmsHome();
            if(sharedCorpIdStr!=null) {
                sharedCorpList = Stream.of(sharedCorpIdStr.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            }
            KnCorpResponseDTO respDto = new KnCorpResponseDTO();
            Integer groupId = Integer.parseInt(removedGroupId);
            Map<Integer, Integer> groupListMap = new HashMap<>();
            groupListMap.put(groupId, KnConstants.CB_MAPPING.NEED_TO_REMOVE.value());
            Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = corpUserProfileUtil.updateImpactedCBDocuments(String.valueOf(corpId), null,
                    groupListMap, null,null, xdmsHome,sharedCorpList,null,null, removeGrpUpmTxn);//
            knLogger.debug(methodName,"profileMdnEtagMap - ", KnGDPRTemplate.mapKeyMdn(profileMdnEtagMap));
            respDto.setProfileMdnEtagMap(profileMdnEtagMap);
            if (profileMdnEtagMap != null && profileMdnEtagMap.isEmpty()) {
                respDto.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), removeGrpUpmTxn, true));
            }
            if (respDto != null) {
                boolean status = commonMediator.prepareMcxNotifyForProfileMdns(respDto);
                knLogger.debug(methodName, "sending Profile notification Status: ", status);
            }
            removeGrpUpmTxn.save();
        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            rollback(removeGrpUpmTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            rollback(removeGrpUpmTxn);
        }
        return taskResult;
    }


    private void rollback(KnPersisterTxn txn) {
        try {
            knLogger.error("rollback()", "Rolling back transaction");
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error("rollback(txn)",
                    "Failed to rollback the transaction.");
        }
    }

}
