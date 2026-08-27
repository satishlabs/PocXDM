/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.upm;

import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.helper.KnXDMCommonMediator;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnProfileDTO;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpUserProfileUtil;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;

public class KnDeletedSharedGroupUserProfileNotificationTask extends KnAbstractTask {

    private static final KnLogger knLogger = KnLogger.getLogger(KnDeletedSharedGroupUserProfileNotificationTask.class);

    private KnCorpUserProfileUtil corpUserProfileUtil;
    private KnGenInfoUtil genInfoUtil;
    private KnXDMCommonMediator commonMediator;
    private KnCorpCommonInfoUtil commonInfoUtil;
    private String payLoad;
    
    public KnDeletedSharedGroupUserProfileNotificationTask(String payLoad){
        this.payLoad=payLoad;
        corpUserProfileUtil = new KnCorpUserProfileUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
        commonMediator = KnXDMCommonMediator.getInstance();
        commonInfoUtil = new KnCorpCommonInfoUtil();
    }

    @Override
    public KnTaskResult executeTask() {
        final String methodName = "executeTask()";
        KnPersisterTxn deleteSharedGroupTxn = null;
        KnTaskResult taskResult=new KnTaskResult();
        try{

            deleteSharedGroupTxn = KnPersisterTxn.getPersisterTxn();
            deleteSharedGroupTxn.open();
            KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
            ArrayList<Integer> deletedSharedGroups = KnCorpCommonInfoUtil.jsonToObject(payLoad, ArrayList.class);
            knLogger.debug(methodName," deletedSharedGroups :",deletedSharedGroups);

            Map<String, Integer> groupsUpmIds = corpUserProfileUtil.getAndDeleteGroupIdsFromUserProfile(deletedSharedGroups, null);
            Map<Integer, List<String>> corpIdNUpmIdMap =  groupsUpmIds.entrySet().stream().collect(Collectors.groupingBy(
                    Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
            knLogger.debug(methodName," corpIdNUpmIdMap :",corpIdNUpmIdMap);

            for(Map.Entry<Integer, List<String>> elements:corpIdNUpmIdMap.entrySet()){
                Integer corpId =elements.getKey();
                KnProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, deleteSharedGroupTxn);
                Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = corpUserProfileUtil.profileMdnEtagUpdate(
                        elements.getValue(),String.valueOf(corpId) ,null
                        ,null, corpProfile.getXdmsHome(), deleteSharedGroupTxn);
                respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
                if(profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()){
                    respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), deleteSharedGroupTxn, true));
                }

                boolean status = commonMediator.prepareMcxNotifyForProfileMdns(respDTO);
                knLogger.debug(methodName, "sending Profile notification Status: ", status);
            }


            deleteSharedGroupTxn.save();
            knLogger.debug(methodName,"Done sending notify to profile mdn of deleted shared groups");

        } catch (KnPersistenceException e) {
            knLogger.error(methodName, "Failed to get the Transaction", e);
            rollback(deleteSharedGroupTxn);
        } catch (Exception e) {
            knLogger.error(methodName, "Un-Expected Exception occurred ", e);
            rollback(deleteSharedGroupTxn);
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
