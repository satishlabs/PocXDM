/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister.couchbase;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.query.QueryResult;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.cb.util.KnCBSRepository;
import com.kodiak.xdms.server.common.cb.util.KnCouchDbManager;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;

import java.util.*;
import java.util.concurrent.RejectedExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.couchbase.client.java.kv.ReplaceOptions.replaceOptions;
import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formIntegerCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes.XDM_CORP_USERPROFILE;

public class KnUserProfileDAO {

    public static final KnLogger knLogger = KnLogger.getLogger(KnUserProfileDAO.class);

    private KnCouchDbManager knCouchDbManager;
    private String pttServerId;
    private KnCorpCommonInfoUtil commonInfoUtil;

    private static final String CB_BUCKET_NAME = "pocdata";
    private static final String USER_PROFILE_INDEX = "userProfileIndex";
    private static final String USER_PROFILE_NAME = "userProfileName";
    private static final String USER_PROFILE_NAME_LOWER = "LOWER(userProfileName)";
    private static final String CORPORATE_ID = "corporateID";
    private static final String CONTACT_LIST_ID = "contactListID";
    private static final String SHARING_ENABLED = "sharingEnabled";
    private static final String META_ID = "META().id";
    private static final String DELETE_USER_PROFILE = "DELETE FROM `" + CB_BUCKET_NAME + "` where " + CORPORATE_ID + "=$corpId and "+META_ID+"=$profileId ";
    private static final String GET_USERPROFILELIST_BY_CORPID = "select " + META_ID + KnConstants.COMMA + USER_PROFILE_NAME + KnConstants.COMMA + USER_PROFILE_INDEX + KnConstants.COMMA + CORPORATE_ID + KnConstants.COMMA + SHARING_ENABLED +  " from `" + CB_BUCKET_NAME + "` where " + CORPORATE_ID + "=$corpId order by userProfileName ASC limit $limit offset $offset";
    private static final String GET_USERPROFILE_DETAILS = "select * from `" + CB_BUCKET_NAME + "` where " + CORPORATE_ID + "=$corpId and "+META_ID+"=$profileId";
    private static final String GET_USERPROFILE_DETAILS_BY_UP_ID = "select * from `" + CB_BUCKET_NAME + "` where "+META_ID+"=$profileId";
    private static final String GET_USERPROFILE_DETAILS_BY_UP_ID_LIST = "select * from `" + CB_BUCKET_NAME + "` where "+META_ID+" IN [ $profileIdList ]";
    private static final String GET_USERPROFILE_DETAILS_BY_UP_ID_LIST_PAGINATION = "select * from `" + CB_BUCKET_NAME + "` where "+META_ID+" IN [ $profileIdList ] "
                        + "order by userProfileName limit $limit offset $offset";
    private static final String IS_PROFILENAME_EXSISTS = "select " + USER_PROFILE_NAME + " from `" + CB_BUCKET_NAME + "` where " + CORPORATE_ID + "=$corpId and " + USER_PROFILE_NAME + "=$profileName";
    private static final String GET_MAX_TOTALCOUNT_BY_CORPID = "select count("+META_ID+") from `pocdata` where corporateID=$corpId;";
    private static final String GET_ALL_DISTINCT_USERPROFILES_FROM_CBS_PER_CORP = "select meta().id, d.corporateID, d.userProfileName from pocdata AS d WHERE userProfileName is not null and corporateID=$corporateID;";
    private static final String GET_USERPROFILELIST_BY_CORPID_AND_UP_NAME_PATTERN = "select " + META_ID + KnConstants.COMMA
            + CORPORATE_ID + KnConstants.COMMA + USER_PROFILE_NAME + KnConstants.COMMA + USER_PROFILE_INDEX + KnConstants.COMMA + SHARING_ENABLED + " from `" + CB_BUCKET_NAME + "` where "
            + CORPORATE_ID + "=$corpId and " + USER_PROFILE_NAME + " LIKE $userProfileName "
            + "order by userProfileName ASC limit $limit offset $offset";
    private static final String GET_USERPROFILELIST_BY_CORPID_AND_UP_NAME_PATTERN_FOR_CASESENSITIVE = "select " + META_ID + KnConstants.COMMA
            + CORPORATE_ID + KnConstants.COMMA + USER_PROFILE_NAME + KnConstants.COMMA + USER_PROFILE_INDEX + KnConstants.COMMA + SHARING_ENABLED + " from `" + CB_BUCKET_NAME + "` where "
            + CORPORATE_ID + "=$corpId and " + USER_PROFILE_NAME_LOWER + " LIKE $userProfileName "
            + "order by userProfileName ASC limit $limit offset $offset";
    private static final String GET_MAX_TOTALCOUNT_BY_CORPID_AND_PROFILE_NAME_PATTERN = "select count(" + META_ID + ") from `pocdata` where corporateID=$corpId and "
            + "REGEX_CONTAINS(" + USER_PROFILE_NAME + " ," + "$userProfileName" + ")";
    private static final String GET_USERPROFILELIST_BY_CORP_AND_HIERARCHY = "select " + META_ID + KnConstants.COMMA + USER_PROFILE_NAME + KnConstants.COMMA + USER_PROFILE_INDEX + KnConstants.COMMA + CORPORATE_ID + KnConstants.COMMA + SHARING_ENABLED + KnConstants.COMMA + "hierarchyId"
            + " from `" + CB_BUCKET_NAME + "` where " + CORPORATE_ID + "=$corpId and hierarchyId=$hierarchyId order by userProfileName ASC limit $limit offset $offset";
    private static final String GET_MAX_TOTALCOUNT_BY_CORP_AND_HIERARCHY = "select count(" + META_ID + ") from `" + CB_BUCKET_NAME + "` where " + CORPORATE_ID + "=$corpId and hierarchyId=$hierarchyId";
    private static final String GET_USERPROFILE_IDS_BY_HIERARCHY_ID =
            "SELECT meta().id FROM `" + CB_BUCKET_NAME + "` AS d WHERE d." + CORPORATE_ID + " IN $corpIds AND d.hierarchyId=$hierarchyId";
    private static final String GET_USERPROFILELIST_BY_USER_PROFILE_INDEX_LIST = "select " + META_ID + KnConstants.COMMA
            + USER_PROFILE_NAME + KnConstants.COMMA + USER_PROFILE_INDEX + " from `pocdata` where corporateID=$corpId and userProfileIndex IN "
            + "$userProfileIndexList";
    /*private static final String GET_USERPROFILELIST_BY_USER_PROFILE_INDEX_LIST = "select " + _ID + KnConstants.COMMA
            + USER_PROFILE_NAME + KnConstants.COMMA + USER_PROFILE_INDEX + " from `pocdata` where userProfileIndex IN "
            + "$userProfileIndexList";*/
    private static final String GET_USERPROFILELIST_BY_PROFILEID_AND_UP_NAME_PATTERN = "select " + META_ID + KnConstants.COMMA
            + CORPORATE_ID + KnConstants.COMMA + USER_PROFILE_NAME + KnConstants.COMMA + USER_PROFILE_INDEX + KnConstants.COMMA + SHARING_ENABLED + " from `" + CB_BUCKET_NAME + "` where "
            + USER_PROFILE_NAME + " LIKE $userProfileName and " + META_ID + " IN " + "$profileIdList";

    private static final String GET_USERPROFILELIST_BY_PROFILEID_AND_UP_NAME_PATTERN_FOR_CAETSENSITIVE = "select " + META_ID + KnConstants.COMMA
            + CORPORATE_ID + KnConstants.COMMA + USER_PROFILE_NAME + KnConstants.COMMA + USER_PROFILE_INDEX + KnConstants.COMMA + SHARING_ENABLED + " from `" + CB_BUCKET_NAME + "` where "
            + USER_PROFILE_NAME_LOWER + " LIKE $userProfileName and " + META_ID + " IN " + "$profileIdList";

    private static final String GET_ALL_USERPROFILES_NAME_PATTERN = "select " + META_ID + KnConstants.COMMA
            + CORPORATE_ID + KnConstants.COMMA + USER_PROFILE_NAME + KnConstants.COMMA + USER_PROFILE_INDEX + KnConstants.COMMA + SHARING_ENABLED + " from `" + CB_BUCKET_NAME + "` where "
            + CORPORATE_ID + "=$corpId and " + USER_PROFILE_NAME + " LIKE $userProfileName and " + META_ID + " IN "
            + "$userProfileIdList "
            + "order by userProfileName ASC";
    private static final String GET_USERPROFILES_BY_NAME_PATTERN = GET_ALL_USERPROFILES_NAME_PATTERN +" limit $limit offset $offset";

    private static final String GET_ALL_USERPROFILES_NAME_PATTERN_CASESENSITIVE = "select " + META_ID + KnConstants.COMMA
            + CORPORATE_ID + KnConstants.COMMA + USER_PROFILE_NAME + KnConstants.COMMA + USER_PROFILE_INDEX + KnConstants.COMMA + SHARING_ENABLED + " from `" + CB_BUCKET_NAME + "` where "
            + CORPORATE_ID + "=$corpId and " + USER_PROFILE_NAME_LOWER + " LIKE $userProfileName and " + META_ID + " IN "
            + "$userProfileIdList "
            + "order by userProfileName ASC ";
    private static final String GET_USERPROFILES_BY_NAME_PATTERN_CASESENSITIVE = GET_ALL_USERPROFILES_NAME_PATTERN_CASESENSITIVE +" limit $limit offset $offset";

    private static final String COUNT_ALL_USERPROFILES_BY_NAME_PATTERN_FOR_CASESENSITIVE = "select count(" + META_ID + ") from `" + CB_BUCKET_NAME + "` where "
            + CORPORATE_ID + "=$corpId and " + USER_PROFILE_NAME_LOWER + " LIKE $userProfileName and " + META_ID + " IN "
            + "$userProfileIdList ";
    private static final String COUNT_ALL_USERPROFILES_BY_NAME_PATTERN = "select count(" + META_ID + ") from `" + CB_BUCKET_NAME + "` where "
            + CORPORATE_ID + "=$corpId and " + USER_PROFILE_NAME + " LIKE $userProfileName and " + META_ID + " IN "
            + "$userProfileIdList ";

    private static final String GET_USERPROFILE_IDS_BY_USER_SUBLIST_DS = "";

    private static final String GET_USERPROFILE_IDS_BY_USER_GROUP_DS = "";

    private static final String DELETE_USER_PROFILE_BY_CORPID = "DELETE FROM `" + CB_BUCKET_NAME + "` where " + CORPORATE_ID + "=$corpId ";

    private static final String GET_USERPROFILEID_LIST_BY_CORPID = "select "+META_ID+" from `" + CB_BUCKET_NAME + "` where " + CORPORATE_ID + "=$corpId ";

    private static final String REMOVE_MDN_FROM_MCPTT_CONFIG = "UPDATE pocdata As p SET p.mcpttPermissionsConfig = ARRAY g FOR g  IN p.mcpttPermissionsConfig WHEN g.mdn NOT IN ["
            +"$mdn"+"] END  WHERE corporateID=$corpId";

    private static final String REMOVE_MDNLIST_FROM_MCPTT_CONFIG =
            "UPDATE pocdata AS p " +
                    "SET p.mcpttPermissionsConfig = " +
                    "ARRAY g FOR g IN p.mcpttPermissionsConfig " +
                    "WHEN g.mdn NOT IN $mdnList END " +
                    "WHERE corporateID = $corpId";

    private static final String GET_USERPROFILELIST_BY_USER_PROFILE_ID = "select " + META_ID + KnConstants.COMMA
            + USER_PROFILE_NAME + KnConstants.COMMA + USER_PROFILE_INDEX + " from `pocdata` where "+META_ID+" IN "
            + "$userProfileIdList";

    public KnUserProfileDAO(String pttServerId) {
        this.knCouchDbManager = KnCouchDbManager.getInstance();
        this.pttServerId = pttServerId;
        commonInfoUtil = new KnCorpCommonInfoUtil();
    }

    /**
     * @param userProfile
     * @param userProfileId
     * @throws KnDAOException
     */
    public void createUserProfile(String userProfileId, KnCorpUserProfileDTO userProfile) throws KnDAOException {
        final String methodName = "createUserProfile()";
        knLogger.info(methodName, "profileId - ", userProfileId, " userProfile ", userProfile);
        try {
            userProfile.setCreateTimeStamp(System.currentTimeMillis());
            String jsonString = commonInfoUtil.ObjToJson(userProfile);
            JsonObject upmDoc = JsonObject.fromJson(jsonString);
            MutationResult result =  KnCBSRepository.getInstance().saveDocument(userProfileId,userProfile);
            knLogger.debug(methodName, "Done creating userProfile.");
            knLogger.debug(methodName,"user profile saved in CBS with id - ", userProfileId,", UPM doc -> ",upmDoc);
            knLogger.info(methodName,"Response from CBSDB"+result.toString());
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException =", e);
            throw KnDbUtil.processException(e, "Failed to create User Profile", pttServerId, XDM_CORP_USERPROFILE, "CREATE_USER_PROFILE");
        } catch (Exception e) {
            knLogger.error(methodName, "InterruptedException =", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to Create User Profile", pttServerId, XDM_CORP_USERPROFILE, "CREATE_USER_PROFILE");
        }
    }

    /**
     * @param corpId
     * @param userProfileId
     * @throws KnDAOException
     */

    public void updateUserProfile(String corpId, String userProfileId, KnCorpModifyUserProfileDTO modifiedUserProfile) throws KnDAOException {
        final String methodName = "updateUserProfile()";
        knLogger.info(methodName, "corpId -", corpId, "userProfileId - ", userProfileId, "modifiedUserProfile -", modifiedUserProfile);
        try {
            com.couchbase.client.java.Collection cbsCollection=KnCBSRepository.getInstance().getCollection();
            Cluster cbsCluster=KnCBSRepository.getInstance().getCluster();

            //JsonDocument userProfileDetails = knCouchDbManager.getBucket().get(userProfileId);
            GetResult cbsUPMInfo = cbsCollection.get(userProfileId);
            JsonObject userProfileDetails = cbsUPMInfo.contentAsObject();

            KnCorpUserProfileDTO profileObj = KnCorpCommonInfoUtil.jsonToObject(userProfileDetails.toString(), KnCorpUserProfileDTO.class);
            profileObj.setUpdateTimeStamp(System.currentTimeMillis());
            if(modifiedUserProfile.getSharingEnabled() != null)
                profileObj.setSharingEnabled(modifiedUserProfile.getSharingEnabled());

            if (modifiedUserProfile.getUserProfileName() != null)
                profileObj.setUserProfileName(modifiedUserProfile.getUserProfileName());
            if (modifiedUserProfile.getContactListID() != null && !modifiedUserProfile.getContactListID().equals(-1)) {
                profileObj.setContactListID(modifiedUserProfile.getContactListID());
            } else if (modifiedUserProfile.getContactListID() != null && modifiedUserProfile.getContactListID().equals(-1)) {
                profileObj.setContactListID(null);
            }
            if (modifiedUserProfile.getFeatureBS() != null)
                profileObj.setFeatureBS(modifiedUserProfile.getFeatureBS());
            if (modifiedUserProfile.getTgscMode() != null)
                profileObj.setTgscMode(modifiedUserProfile.getTgscMode());
            if (modifiedUserProfile.getSelfDnDPrivilege() != null) {
                profileObj.setSelfDnDPrivilege(modifiedUserProfile.getSelfDnDPrivilege());
            }
            if (modifiedUserProfile.getEmergencyConfig() != null) {
                KnSubsEmergencyConfigDTO emergencyConfig = modifiedUserProfile.getEmergencyConfig();
                if (emergencyConfig.getCallType() != null)
                    profileObj.getEmergencyConfig().setCallType(emergencyConfig.getCallType());
                if (emergencyConfig.getCancelPermission() != null)
                    profileObj.getEmergencyConfig().setCancelPermission(emergencyConfig.getCancelPermission());
                if (emergencyConfig.getDestType() != null)
                    profileObj.getEmergencyConfig().setDestType(emergencyConfig.getDestType());
                if (emergencyConfig.getLmrBehavior() != null)
                    profileObj.getEmergencyConfig().setLmrBehavior(emergencyConfig.getLmrBehavior());
                if (emergencyConfig.getOrigBitset() != null)
                    profileObj.getEmergencyConfig().setOrigBitset(emergencyConfig.getOrigBitset());
                if (emergencyConfig.getPermission() != null)
                    profileObj.getEmergencyConfig().setPermission(emergencyConfig.getPermission());
                if (emergencyConfig.getTermBitset() != null)
                    profileObj.getEmergencyConfig().setTermBitset(emergencyConfig.getTermBitset());
                if (emergencyConfig.getDestAttributes() != null) {
                    Set<KnDestinationAttributeDTO> destAttributes = new HashSet<>();
                    for (KnDestinationAttributeDTO attr : emergencyConfig.getDestAttributes()) {
                        destAttributes.add(new KnDestinationAttributeDTO(attr.getDestType(), attr.getDestURI(), attr.getDestCat()));
                    }
                    profileObj.getEmergencyConfig().setDestAttributes(destAttributes);
                }
                if (profileObj.getEmergencyConfig() != null && profileObj.getEmergencyConfig().getPermission() != null) {
                    if (profileObj.getEmergencyConfig().getPermission() == 0) {
                        profileObj.getEmergencyConfig().setEmergConfigTimer(null);
                    } else if (null != emergencyConfig.getEmergConfigTimer()) {
                        profileObj.getEmergencyConfig().setEmergConfigTimer(emergencyConfig.getEmergConfigTimer());
                    }
                }
            }
            /**
            String upmJsonString = KnCorpCommonInfoUtil.ObjToJson(profileObj);
            JsonObject content = JsonObject.fromJson(upmJsonString);
            JsonDocument upmDoc = JsonDocument.create(userProfileId, content);
            JsonDocument updated = knCouchDbManager.getBucket().replace(upmDoc);
            */
            String upmJsonString = KnCorpCommonInfoUtil.ObjToJson(profileObj);
            JsonObject content = JsonObject.fromJson(upmJsonString);
            cbsCollection.replace(userProfileId, content, replaceOptions().cas(cbsUPMInfo.cas()));

            knLogger.debug(methodName, "Done updating UPM name,tgroupScanMode ,sublist,emegency config -", userProfileId);
            //Group
            JsonObject jsonProfileId = JsonObject.create()
                    .put("pid", userProfileId);
            Set<KnCorpGroupListInfoDTO> addedGroupList = modifiedUserProfile.getAddedGroupList();
            Set<KnCorpGroupListInfoDTO> updatedGroupList = modifiedUserProfile.getModifiedGroupList();
            Set<String> removedGroupids = modifiedUserProfile.getRemovedGroupIdsList();

            if (addedGroupList != null && !addedGroupList.isEmpty()) {
                int count = 0;
                StringBuilder strQuery = new StringBuilder();
                strQuery.append("UPDATE pocdata AS d SET d.groupList = ARRAY_APPEND(d.groupList, ");
                for (KnCorpGroupListInfoDTO groupList : addedGroupList) {
                    if (count > 0) {
                        strQuery.append(",");
                    }
                    strQuery.append("{ \"groupChannel\": ");
                    strQuery.append(groupList.getGroupChannel());
                    strQuery.append(",");
                    strQuery.append("\"groupID\": ");
                    strQuery.append(groupList.getGroupID());
                    strQuery.append(",");
                    strQuery.append("\"groupPriority\": ");
                    strQuery.append(groupList.getGroupPriority());
                    strQuery.append(",");
                    strQuery.append("\"groupZone\": ");
                    strQuery.append(groupList.getGroupZone());
                    strQuery.append(",");
                    strQuery.append("\"grpMemProps\":");
                    strQuery.append("{ \"callInitiateAllowed\":");
                    strQuery.append(groupList.getGrpMemProps().getCallInitiateAllowed());
                    strQuery.append(",");
                    strQuery.append("\"callTerminateAllowed\":");
                    strQuery.append(groupList.getGrpMemProps().getCallTerminateAllowed());
                    strQuery.append(",");
                    strQuery.append("\"incallAllowed\":");
                    strQuery.append(groupList.getGrpMemProps().getIncallAllowed());
                    strQuery.append(",");
                    strQuery.append("\"videoCallInitiateAllowed\":");
                    strQuery.append(groupList.getGrpMemProps().getVideoCallInitiateAllowed() != null ? groupList.getGrpMemProps().getVideoCallInitiateAllowed() : 0);
                    strQuery.append(",");
                    strQuery.append("\"videoCallReceiveAllowed\":");
                    strQuery.append(groupList.getGrpMemProps().getVideoCallReceiveAllowed() != null ? groupList.getGrpMemProps().getVideoCallReceiveAllowed() : 0);
                    strQuery.append(",");
                    strQuery.append("\"videoInCallAllowed\":");
                    strQuery.append(groupList.getGrpMemProps().getVideoInCallAllowed() != null ? groupList.getGrpMemProps().getVideoInCallAllowed() : 0);
                    strQuery.append(",");
                    strQuery.append("\"isBroadcaster\":");
                    strQuery.append(groupList.getGrpMemProps().getIsBroadcaster());
                    strQuery.append(",");
                    strQuery.append("\"isLocSupervisor\":");
                    strQuery.append(groupList.getGrpMemProps().getIsLocSupervisor());
                    strQuery.append(",");
                    strQuery.append("\"isOSMAuthorized\":");
                    strQuery.append(groupList.getGrpMemProps().getIsOSMAuthorized());
                    strQuery.append(",");
                    strQuery.append("\"isSupervisor\":");
                    strQuery.append(groupList.getGrpMemProps().getIsSupervisor());
                    strQuery.append("} }");
                    count++;
                }
                strQuery.append(")");
                strQuery.append(" WHERE ");
                strQuery.append(META_ID);
                strQuery.append("=$pid ");
                knLogger.debug(methodName, "addedGroupList strQuery :", strQuery.toString());
                //ParameterizedN1qlQuery paramQuery = N1qlQuery.parameterized(strQuery.toString(), jsonProfileId);
                //knCouchDbManager.getBucket().query(paramQuery);
                cbsCluster.query(
                        strQuery.toString(),
                        queryOptions().parameters(jsonProfileId));

            }

            if (updatedGroupList != null && !updatedGroupList.isEmpty()) {
                int count = 0;
                StringBuilder strQuery = new StringBuilder();
                strQuery.append("UPDATE pocdata SET ");
                for (KnCorpGroupListInfoDTO groupList : updatedGroupList) {
                    if (count > 0) {
                        strQuery.append(",");
                    }
                    strQuery.append("groupList[pos] = OBJECT_PUT(OBJECT_PUT(OBJECT_PUT(OBJECT_PUT( ");
                    strQuery.append("u, \"groupChannel\",");
                    strQuery.append(groupList.getGroupChannel());
                    strQuery.append(" ),");
                    strQuery.append("\"groupPriority\",");
                    strQuery.append(groupList.getGroupPriority());
                    strQuery.append(" ),");
                    strQuery.append("\"groupZone\",");
                    strQuery.append(groupList.getGroupZone());
                    strQuery.append(" ),");
                    strQuery.append("\"grpMemProps\",");
                    strQuery.append("OBJECT_PUT(OBJECT_PUT(OBJECT_PUT(OBJECT_PUT(OBJECT_PUT(OBJECT_PUT(OBJECT_PUT(OBJECT_PUT(OBJECT_PUT(OBJECT_PUT(u.grpMemProps,");
                    strQuery.append("\"callInitiateAllowed\",");
                    strQuery.append(groupList.getGrpMemProps().getCallInitiateAllowed());
                    strQuery.append(" ),");
                    strQuery.append("\"callTerminateAllowed\",");
                    strQuery.append(groupList.getGrpMemProps().getCallTerminateAllowed());
                    strQuery.append(" ),");
                    strQuery.append("\"incallAllowed\",");
                    strQuery.append(groupList.getGrpMemProps().getIncallAllowed());
                    strQuery.append(" ),");
                    strQuery.append("\"videoCallInitiateAllowed\",");
                    strQuery.append(groupList.getGrpMemProps().getVideoCallInitiateAllowed() != null ? groupList.getGrpMemProps().getVideoCallInitiateAllowed() : 0);
                    strQuery.append(" ),");
                    strQuery.append("\"videoCallReceiveAllowed\",");
                    strQuery.append(groupList.getGrpMemProps().getVideoCallReceiveAllowed() != null ? groupList.getGrpMemProps().getVideoCallReceiveAllowed() : 0);
                    strQuery.append(" ),");
                    strQuery.append("\"videoInCallAllowed\",");
                    strQuery.append(groupList.getGrpMemProps().getVideoInCallAllowed() != null ? groupList.getGrpMemProps().getVideoInCallAllowed() : 0);
                    strQuery.append(" ),");
                    strQuery.append("\"isBroadcaster\",");
                    strQuery.append(groupList.getGrpMemProps().getIsBroadcaster());
                    strQuery.append(" ),");
                    strQuery.append("\"isLocSupervisor\",");
                    strQuery.append(groupList.getGrpMemProps().getIsLocSupervisor());
                    strQuery.append(" ),");
                    strQuery.append("\"isOSMAuthorized\",");
                    strQuery.append(groupList.getGrpMemProps().getIsOSMAuthorized());
                    strQuery.append(" ),");
                    strQuery.append("\"isSupervisor\",");
                    strQuery.append(groupList.getGrpMemProps().getIsSupervisor());
                    strQuery.append(" ))");
                    strQuery.append(" FOR pos:u IN groupList WHEN ");
                    strQuery.append(" u.groupID = ");
                    strQuery.append(groupList.getGroupID());
                    strQuery.append(" END ");
                    count++;
                }
                strQuery.append(" WHERE ");
                strQuery.append(META_ID);
                strQuery.append("=$pid ");
                knLogger.debug(methodName, "updatedGroupList strQuery :", strQuery.toString());
                //ParameterizedN1qlQuery paramQuery = N1qlQuery.parameterized(strQuery.toString(), jsonProfileId);
                //knCouchDbManager.getBucket().query(paramQuery);
                cbsCluster.query(
                        strQuery.toString(),
                        queryOptions().parameters(jsonProfileId));
            }

            if (removedGroupids != null && !removedGroupids.isEmpty()) {
                //converting string to integer ids
                Set<Integer> removedGroupidsInt = removedGroupids.stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet());

                String removeGroupCommaSeperated = formIntegerCommaSeperatedIdList(removedGroupidsInt);
                String query = "UPDATE pocdata As p SET p.groupList = ARRAY g FOR g IN p.groupList WHEN g.groupID NOT IN [" + removeGroupCommaSeperated + "] END" +
                        " WHERE "+META_ID+"=$pid";
                knLogger.debug(methodName, "removedGroupids query:", query);
                //ParameterizedN1qlQuery paramQuery = N1qlQuery.parameterized(query, jsonProfileId);
                //knCouchDbManager.getBucket().query(paramQuery);
                cbsCluster.query(
                        query,
                        queryOptions().parameters(jsonProfileId));
            }
            //mcptt permission
            Set<KnCorpUserProfileMCPTTConfig> addedPerms = modifiedUserProfile.getAddedMcpttPermissionsConfig();
            Set<KnCorpUserProfileMCPTTConfig> updatedPerms = modifiedUserProfile.getModifiedPermissionsConfig();
            Set<KnCorpUserProfileMCPTTConfig> removedPerms = modifiedUserProfile.getRemovedMcpttPermissionsConfig();

            if (addedPerms != null && !addedPerms.isEmpty()) {
                StringBuilder query = new StringBuilder();
                query.append("UPDATE pocdata AS d SET d.mcpttPermissionsConfig = ARRAY_APPEND(d.mcpttPermissionsConfig, ");
                int count = 0;
                for (KnCorpUserProfileMCPTTConfig perms : addedPerms) {
                    if (count > 0) {
                        query.append(",");
                    }
                    query.append("{ \"mdn\": \"" + perms.getMdn() + "\", \"permBitSet\":" + perms.getPermBitSet() + "}");
                    count++;
                }
                query.append(")");
                query.append(" WHERE ");
                query.append(META_ID);
                query.append("=$pid");

                knLogger.debug(methodName, "addedPerms query:", query.toString());
                //ParameterizedN1qlQuery paramQuery = N1qlQuery.parameterized(query.toString(), jsonProfileId);
                //knCouchDbManager.getBucket().query(paramQuery);
                cbsCluster.query(
                        query.toString(),
                        queryOptions().parameters(jsonProfileId));
            }

            if (updatedPerms != null && !updatedPerms.isEmpty()) {
                StringBuilder query = new StringBuilder();
                query.append("UPDATE pocdata AS d SET ");
                int count = 0;
                for (KnCorpUserProfileMCPTTConfig perms : updatedPerms) {
                    if (count > 0) {
                        query.append(",");
                    }
                    query.append(" u.permBitSet = " + perms.getPermBitSet() + " FOR u IN d.mcpttPermissionsConfig WHEN u.mdn =\"" + perms.getMdn() + "\" END ");
                    count++;
                }
                query.append(" WHERE ");
                query.append(META_ID);
                query.append("=$pid");
                knLogger.debug(methodName, "updatedPerms query :", query.toString());
                //ParameterizedN1qlQuery paramQuery = N1qlQuery.parameterized(query.toString(), jsonProfileId);
                //knCouchDbManager.getBucket().query(paramQuery);
                cbsCluster.query(
                        query.toString(),
                        queryOptions().parameters(jsonProfileId));
            }

            if (removedPerms != null && !removedPerms.isEmpty()) {
                // Convert Set of String
                Set<String> removedPermsInt = removedPerms.stream()
                        .map(s -> s.getMdn())
                        .collect(Collectors.toSet());

                String removePermCommaSeperated = formCommaSeperatedIdList(removedPermsInt);
                String query = "UPDATE pocdata As p SET p.mcpttPermissionsConfig = ARRAY g FOR g " +
                        " IN p.mcpttPermissionsConfig WHEN g.mdn NOT IN  [" + removePermCommaSeperated + "] END " +
                        " WHERE "+META_ID+"=$pid";
                knLogger.debug(methodName, "removedPerms query :", query);
                //ParameterizedN1qlQuery paramQuery = N1qlQuery.parameterized(query, jsonProfileId);
                //knCouchDbManager.getBucket().query(paramQuery);
                cbsCluster.query(
                        query,
                        queryOptions().parameters(jsonProfileId));
            }
        } catch (DocumentNotFoundException ex) {
            knLogger.error(methodName, "DocumentNotFoundException :", ex);
            throw ex;
        } catch (RejectedExecutionException ex) {
            knLogger.error(methodName, "DocumentNotFoundException | RejectedExecutionException:", ex);
            throw KnDbUtil.processException(ex, "Failed to update User Profile", pttServerId, XDM_CORP_USERPROFILE, "UPDATE_USER_PROFILE");
        } catch (Exception e) {
            knLogger.error(methodName, "Exception:", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to update User Profile", pttServerId, XDM_CORP_USERPROFILE, "UPDATE_USER_PROFILE");
        }
    }

    /**
     * @param corpId
     * @param userProfileId
     * @throws KnDAOException
     */

    public void deleteUserProfile(String corpId, String userProfileId) throws KnDAOException {
        final String methodName = "deleteUserProfile(String,String)";
        knLogger.info(methodName, "corpId -", corpId, "profileId - ", userProfileId);
        try {
            JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("profileId", userProfileId);

            KnCBSRepository.getInstance().getQueryResult(DELETE_USER_PROFILE,values);

        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to Delete User Profile", pttServerId, XDM_CORP_USERPROFILE, DELETE_USER_PROFILE);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to Delete User Profile", pttServerId, XDM_CORP_USERPROFILE, DELETE_USER_PROFILE);
        }
    }

    /**
     * @param corpId
     * @param startIndex
     * @param limit
     * @throws KnDAOException
     */
    public Collection<KnCorpUserProfileDTO> getUserProfileListByCorpId(String corpId, String startIndex, String limit) throws KnDAOException {

        final String methodName = "getUserProfileListByCorpId(String)";

        knLogger.info(methodName, "corpId -", corpId, "startIndex - ", startIndex, "limit - ", limit);
        KnCorpUserProfileDTO userProfileDTO = new KnCorpUserProfileDTO();
        List<KnCorpUserProfileDTO> userProfileList = new ArrayList<>();
        try {
            JsonObject values = JsonObject.create()
                    .put("corpId", Integer.parseInt(corpId))
                    .put("offset", Integer.parseInt(startIndex))
                    .put("limit", Integer.parseInt(limit));

            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILELIST_BY_CORPID,values);
            for(JsonObject object : queryResult.rowsAsObject()){
                userProfileDTO = commonInfoUtil.jsonToObject(object.toString(), KnCorpUserProfileDTO.class);
                userProfileList.add(userProfileDTO);
            }
            knLogger.debug(methodName, "queryResult-", userProfileList);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILELIST_BY_CORPID);
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILELIST_BY_CORPID);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILELIST_BY_CORPID);
        }
        return userProfileList;

    }

    /**
     * @param corpId
     * @param userProfileId
     * @return KnCorpUserProfileDTO
     * @throws KnDAOException
     */
    public KnCorpUserProfileDTO getUserProfile(String corpId, String userProfileId) throws KnDAOException {

        final String methodName = "getUserProfile(String,String)";

        knLogger.info(methodName, "corpId -", corpId, "profileId - ", userProfileId);
        KnCorpUserProfileDTO userProfile = new KnCorpUserProfileDTO();
        try {

            JsonObject placeholderValues = JsonObject.create();
            placeholderValues.put("corpId", Integer.parseInt(corpId));
            placeholderValues.put("profileId", userProfileId);

            QueryResult result=KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILE_DETAILS,placeholderValues);

            userProfile =commonInfoUtil.jsonToObject(result.rowsAsObject().get(0).get(CB_BUCKET_NAME).toString(), KnCorpUserProfileDTO.class);

            knLogger.debug(methodName, "queryResult-", result.rowsAsObject().get(0).toString());

            knLogger.debug(methodName, "queryResult-", userProfile);
        }catch(RuntimeException r){
            knLogger.error(methodName, "getQueryResult failed query:",GET_USERPROFILE_DETAILS," Exception:",r);
            throw KnDbUtil.processException(r, "Operation was interrupted", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILE_DETAILS);
        }catch (Exception e) {
            knLogger.error(methodName," Exception ",e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILE_DETAILS);
        }
        return userProfile;
    }

    /**
     * @param userProfileId
     * @return KnCorpUserProfileDTO
     * @throws KnDAOException
     */
    public KnCorpUserProfileDTO getUserProfile(String userProfileId) throws KnDAOException {

        final String methodName = "getUserProfile(String,String)";

        knLogger.info(methodName, "profileId - ", userProfileId);
        KnCorpUserProfileDTO userProfile = new KnCorpUserProfileDTO();
        try {

            JsonObject placeholderValues = JsonObject.create();
            placeholderValues.put("profileId", userProfileId);

            QueryResult result=KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILE_DETAILS_BY_UP_ID,placeholderValues);

            userProfile =commonInfoUtil.jsonToObject(result.rowsAsObject().get(0).get(CB_BUCKET_NAME).toString(), KnCorpUserProfileDTO.class);

            knLogger.debug(methodName, "queryResult-", result.rowsAsObject().get(0).toString());

            knLogger.debug(methodName, "queryResult-", userProfile);
        }catch(RuntimeException r){
            knLogger.error(methodName, "getQueryResult failed query:",GET_USERPROFILE_DETAILS_BY_UP_ID," Exception:",r);
            throw KnDbUtil.processException(r, "Operation was interrupted", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILE_DETAILS_BY_UP_ID);
        }catch (Exception e) {
            knLogger.error(methodName, " Exception :",e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILE_DETAILS_BY_UP_ID);
        }
        return userProfile;
    }
    /**
     * @param userProfileIds
     * @return KnCorpUserProfileDTO
     * @throws KnDAOException
     */
    public  List<KnCorpUserProfileDTO> getUserProfile(List<String> userProfileIds) throws KnDAOException {

        final String methodName = "getUserProfile(List<String>)";

        knLogger.debug(methodName,  "userProfileIds - ", userProfileIds);
        List<KnCorpUserProfileDTO> userProfileList = new ArrayList<>();
        try {
            KnCorpUserProfileDTO userProfile = null;
            for(String userProfileId : userProfileIds)
            {
                userProfile = getUserProfile(userProfileId);
                if (userProfile != null) userProfileList.add(userProfile);
            }
           /* String userProfileIdsString = formCommaSeperatedIdList(userProfileIds);
            JsonObject values = JsonObject.create().put("profileIdList", userProfileIdsString);
            knLogger.debug(methodName, "values-" , values);

            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILE_DETAILS_BY_UP_ID_LIST,values);
            KnCorpUserProfileDTO userProfile = null;
            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfile = KnCorpCommonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                if (userProfile != null) userProfileList.add(userProfile);
            }*/
            knLogger.debug(methodName, "queryResult-", userProfileList);

        } catch (RuntimeException e) {
            knLogger.error(methodName, "RuntimeException ", e);
            throw KnDbUtil.processException(e, "Operation was interrupted", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILE_DETAILS_BY_UP_ID_LIST);
        }catch (Exception e) {
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILE_DETAILS_BY_UP_ID_LIST);
        }
        return userProfileList;
    }

    /**
     +     * @param userProfileIds
     +     * @return KnCorpUserProfileDTO
     +     * @throws KnDAOException
     +     */
    public  List<KnCorpUserProfileDTO> getUserProfile(List<String> userProfileIds, String fetchSize, String startIndex) throws KnDAOException {

        final String methodName = "getUserProfile(List<String>, String, String)";

        knLogger.debug(methodName,  "userProfileIds - ", userProfileIds, "fetchSize - ", fetchSize, "startIndex - ", startIndex);

        List<KnCorpUserProfileDTO> userProfileList = new ArrayList<>();
        try {
            KnCorpUserProfileDTO userProfile = null;
            String userProfileIdsString = formCommaSeperatedIdList(userProfileIds);

            JsonObject placeholderValues = JsonObject.create();
            placeholderValues.put("limit", Integer.parseInt(fetchSize));
            placeholderValues.put("offset", Integer.parseInt(startIndex));

            String query = "select * from pocdata where META().id IN [ " + userProfileIdsString + " ] order by userProfileName ASC limit $limit offset $offset";
            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(query,placeholderValues);

            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfile = commonInfoUtil.jsonToObject(row.get(CB_BUCKET_NAME).toString(), KnCorpUserProfileDTO.class);
                knLogger.debug(methodName, "queryResult UserProfile-", userProfile);
                if (userProfile != null) {
                    userProfileList.add(userProfile);
                }
            }
            knLogger.debug(methodName, "queryResult-", userProfileList);
        }catch (Exception e) {
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILE_DETAILS_BY_UP_ID_LIST_PAGINATION);
        }
        return userProfileList;
    }

    /**
     * @param corpId
     * @param userProfileName
     * @return boolean
     * @throws KnDAOException
     */

    public String getProfileName(int corpId, String userProfileName) throws KnDAOException {

        final String methodName = "getProfileName(int,String)";
        String result=null;
        knLogger.debug(methodName, "corpId - ", corpId, " userProfileName - ", userProfileName);
        try {
            JsonObject values = JsonObject.create().put("corpId", corpId).put("profileName", userProfileName);
            QueryResult queryResult =  KnCBSRepository.getInstance().getQueryResult(IS_PROFILENAME_EXSISTS,values);
            Map<String, Object> queryResultMap=new HashMap<String, Object>();
            for (JsonObject row : queryResult.rowsAsObject()) {
                queryResultMap = row.toMap();
            }
            result=(String) queryResultMap.get(USER_PROFILE_NAME);
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to check the ProfileName for the given corpId", pttServerId, XDM_CORP_USERPROFILE, "getProfileName");
        } catch (Exception e) {
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            knLogger.error(methodName," Exception :",e );
            throw KnDbUtil.processException(e, "Failed to check the ProfileName for the given corpId", pttServerId, XDM_CORP_USERPROFILE, "getProfileName");
        }
        knLogger.info(methodName," Exit :",result);
        return result;
    }

    /**
     * Method to get max of UPM index.
     *
     * @return String
     */
    public String getUserProfileIndexSeqence(int corpId) throws KnDAOException {
        final String methodName = "getUserProfileIndexSeqence()";
        String maxUPMIndex = null;
        knLogger.debug(methodName, "Entry  - corpId:",corpId);
        try {

           QueryResult queryResult= KnCBSRepository.getInstance().getPOCCluster().query("select max(userProfileIndex)  from `pocdata` where corporateID="+corpId);
            String resultStr =queryResult.rowsAsObject().get(0).toString();
            int colonIndex = resultStr.indexOf(":");
            int bracesIndex = resultStr.indexOf("}");
            maxUPMIndex = resultStr.substring(colonIndex + 1, bracesIndex);
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName,"RejectedExecutionException ",e);
            throw KnDbUtil.processException(e, "Failed to get max UPM index ", pttServerId, XDM_CORP_USERPROFILE, " max(userProfileIndex) ");
        } catch (Exception e) {
            knLogger.error(methodName,"Exception ",e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get max UPM index ", pttServerId, XDM_CORP_USERPROFILE, " max(userProfileIndex) ");
        }
        if (maxUPMIndex == null || maxUPMIndex.equalsIgnoreCase("null")) {
            maxUPMIndex = "0";
        }
        knLogger.debug(methodName, "Exit  maxUPMIndex - ", maxUPMIndex);
        return maxUPMIndex;
    }

    public Integer getMaxTotalCountByCorpId(String corpId) throws KnDAOException {

        final String methodName = "getMaxTotalCountByCorpId(String)";
        Integer result = null;
        knLogger.debug(methodName, "corpId - ", corpId);
        try {
            JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId));

            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(GET_MAX_TOTALCOUNT_BY_CORPID,values);

            String resultStr = queryResult.rowsAsObject().get(0).toString();

            int colonIndex = resultStr.indexOf(":");
            int bracesIndex = resultStr.indexOf("}");
            if (resultStr.substring(colonIndex + 1, bracesIndex) != null)
                result = Integer.parseInt(resultStr.substring(colonIndex + 1, bracesIndex));
            else
                result = null;

            knLogger.debug(methodName, "--->Exit  result - ", result);
        } catch (NoSuchElementException e) {
            result = null;
            knLogger.error(methodName, "NoSuchElementException msg", e.getMessage());
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to check the maxTotalCount for the given corpId", pttServerId, XDM_CORP_USERPROFILE, GET_MAX_TOTALCOUNT_BY_CORPID);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to check the maxTotalCount for the given corpId", pttServerId, XDM_CORP_USERPROFILE, GET_MAX_TOTALCOUNT_BY_CORPID);
        }
        return result;
    }

    /**
     * @param corpId
     * @param userProfileNamePattern
     * @param startIndex
     * @param limit
     * @return
     * @throws KnDAOException
     */
    public Collection<KnCorpUserProfileDTO> getUserProfileListByUserProfileNamePattern(String corpId,
                                                                                       String userProfileNamePattern, String startIndex, String limit, String isCaseSensitiveSearch) throws KnDAOException {

        final String methodName = "getUserProfileListByUserProfileNamePattern(String,String,String,String)";
        knLogger.info(methodName, "corpId -", corpId, "userProfileNamePattern -", userProfileNamePattern, "startIndex - ", startIndex, "limit - ", limit,"isCaseSensitiveSearch", isCaseSensitiveSearch);
        KnCorpUserProfileDTO userProfileDTO;
        List<KnCorpUserProfileDTO> userProfileList = new ArrayList<>();
        QueryResult queryResult = null;
        try {
        	 if(userProfileNamePattern!=null) {
        		 if(userProfileNamePattern.contains("%")) {
        			 userProfileNamePattern=userProfileNamePattern.replace("%","\\%");
        		 }
        		 if(userProfileNamePattern.contains("_")) {
        			 userProfileNamePattern=userProfileNamePattern.replace("_","\\_");
        		 }
        	 }

            if (null == isCaseSensitiveSearch || isCaseSensitiveSearch.isEmpty() || isCaseSensitiveSearch.equals("0")) {
                JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("userProfileName", "%" + userProfileNamePattern.toLowerCase() + "%")
                        .put("offset", Integer.parseInt(startIndex)).put("limit", Integer.parseInt(limit));
                knLogger.debug(methodName, "values-", values);
                queryResult = KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILELIST_BY_CORPID_AND_UP_NAME_PATTERN_FOR_CASESENSITIVE, values);
            } else {
                JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("userProfileName", "%" + userProfileNamePattern + "%")
                        .put("offset", Integer.parseInt(startIndex)).put("limit", Integer.parseInt(limit));
                knLogger.debug(methodName, "values-", values);
                queryResult = KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILELIST_BY_CORPID_AND_UP_NAME_PATTERN, values);
            }


            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfileDTO = commonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                userProfileList.add(userProfileDTO);
            }
            knLogger.debug(methodName, "queryResult-", userProfileList);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_CORPID_AND_UP_NAME_PATTERN);
        } catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_CORPID_AND_UP_NAME_PATTERN);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_CORPID_AND_UP_NAME_PATTERN);
        }
        return userProfileList;

    }

    /**
     * @param corpId
     * @param userProfileNamePattern
     * @param startIndex
     * @param limit
     * @param isCaseSensitiveSearch
     * @param userprofileIds
     * @return
     * @throws KnDAOException
     */
    public Collection<KnCorpUserProfileDTO> getUserProfileListForNamePattern(String corpId, String userProfileNamePattern, String startIndex, String limit, String isCaseSensitiveSearch, List<String> userprofileIds) throws KnDAOException {
        final String methodName = "getUserProfileListForNamePattern(String,String,String,String,List<>)";
        knLogger.info(methodName, "corpId -", corpId, "userProfileNamePattern -", userProfileNamePattern, "startIndex - ", startIndex, "limit - ", limit, "isCaseSensitiveSearch", isCaseSensitiveSearch);
        KnCorpUserProfileDTO userProfileDTO;
        List<KnCorpUserProfileDTO> userProfileList = new ArrayList<>();
        QueryResult queryResult = null;
        try {
            if (userProfileNamePattern != null) {
                if (userProfileNamePattern.contains("%")) {
                    userProfileNamePattern = userProfileNamePattern.replace("%", "\\%");
                }
                if (userProfileNamePattern.contains("_")) {
                    userProfileNamePattern = userProfileNamePattern.replace("_", "\\_");
                }
            }

            if (null == isCaseSensitiveSearch || isCaseSensitiveSearch.isEmpty() || isCaseSensitiveSearch.equals("0")) {
                JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("userProfileName", "%" + userProfileNamePattern.toLowerCase() + "%")
                        .put("$userProfileIdList", userprofileIds)
                        .put("offset", Integer.parseInt(startIndex)).put("limit", Integer.parseInt(limit));
                knLogger.debug(methodName, "values-", values);
                queryResult = KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILES_BY_NAME_PATTERN_CASESENSITIVE, values);
            } else {
                JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("userProfileName", "%" + userProfileNamePattern + "%")
                        .put("$userProfileIdList", userprofileIds)
                        .put("offset", Integer.parseInt(startIndex)).put("limit", Integer.parseInt(limit));
                knLogger.debug(methodName, "values-", values);
                queryResult = KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILES_BY_NAME_PATTERN, values);
            }


            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfileDTO = commonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                userProfileList.add(userProfileDTO);
            }
            knLogger.info(methodName, "queryResult-", userProfileList);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILES_BY_NAME_PATTERN);
        } catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILES_BY_NAME_PATTERN);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILES_BY_NAME_PATTERN);
        }
        return userProfileList;

    }

    /**
     *
     * @param corpId
     * @param userProfileNamePattern
     * @param isCaseSensitiveSearch
     * @param userprofileIds
     * @return
     * @throws KnDAOException
     */
    public Collection<KnCorpUserProfileDTO> getUserProfileListForNamePattern(String corpId, String userProfileNamePattern, String isCaseSensitiveSearch, List<String> userprofileIds) throws KnDAOException {
        final String methodName = "getUserProfileListForNamePattern(String,String,String,String,List<>)";
        knLogger.info(methodName, "corpId -", corpId, "userProfileNamePattern -", userProfileNamePattern, "isCaseSensitiveSearch", isCaseSensitiveSearch);
        KnCorpUserProfileDTO userProfileDTO;
        List<KnCorpUserProfileDTO> userProfileList = new ArrayList<>();
        QueryResult queryResult = null;
        try {
            if (userProfileNamePattern != null) {
                if (userProfileNamePattern.contains("%")) {
                    userProfileNamePattern = userProfileNamePattern.replace("%", "\\%");
                }
                if (userProfileNamePattern.contains("_")) {
                    userProfileNamePattern = userProfileNamePattern.replace("_", "\\_");
                }
            }

            if (null == isCaseSensitiveSearch || isCaseSensitiveSearch.isEmpty() || isCaseSensitiveSearch.equals("0")) {
                JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("userProfileName", "%" + userProfileNamePattern.toLowerCase() + "%")
                        .put("$userProfileIdList", userprofileIds);
                knLogger.debug(methodName, "values-", values);
                queryResult = KnCBSRepository.getInstance().getQueryResult(GET_ALL_USERPROFILES_NAME_PATTERN_CASESENSITIVE, values);
            } else {
                JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("userProfileName", "%" + userProfileNamePattern + "%")
                        .put("$userProfileIdList", userprofileIds);
                knLogger.debug(methodName, "values-", values);
                queryResult = KnCBSRepository.getInstance().getQueryResult(GET_ALL_USERPROFILES_NAME_PATTERN, values);
            }


            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfileDTO = commonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                userProfileList.add(userProfileDTO);
            }
            knLogger.info(methodName, "queryResult userProfileList.size() -", userProfileList.size());
            knLogger.debug(methodName, "queryResult-", userProfileList);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE,
                    GET_ALL_USERPROFILES_NAME_PATTERN);
        } catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ",e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_ALL_USERPROFILES_NAME_PATTERN);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ",e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_ALL_USERPROFILES_NAME_PATTERN);
        }
        return userProfileList;

    }

    /**
     *
     * @param corpId
     * @param userProfileNamePattern
     * @param isCaseSensitiveSearch
     * @param userprofileIds
     * @return
     * @throws KnDAOException
     */
    public Integer getUserProfileCountByUserProfileNamePatternAndUserprofileIds(String corpId, String userProfileNamePattern, String isCaseSensitiveSearch, List<String> userprofileIds) throws KnDAOException {

        final String methodName = "getUserProfileCountByUserProfileNamePatternAndUserprofileIds(String,String,String,List<>)";
        knLogger.info(methodName, "corpId -", corpId, "userProfileNamePattern -", userProfileNamePattern, "isCaseSensitiveSearch", isCaseSensitiveSearch);
        KnCorpUserProfileDTO userProfileDTO;
        Integer result = null;
        QueryResult queryResult = null;
        try {
            if (userProfileNamePattern != null) {
                if (userProfileNamePattern.contains("%")) {
                    userProfileNamePattern = userProfileNamePattern.replace("%", "\\%");
                }
                if (userProfileNamePattern.contains("_")) {
                    userProfileNamePattern = userProfileNamePattern.replace("_", "\\_");
                }
            }

            if (null == isCaseSensitiveSearch || isCaseSensitiveSearch.isEmpty() || isCaseSensitiveSearch.equals("0")) {
                JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("userProfileName", "%" + userProfileNamePattern.toLowerCase() + "%")
                        .put("$userProfileIdList", userprofileIds);
                knLogger.debug(methodName, "values-", values);
                queryResult = KnCBSRepository.getInstance().getQueryResult(COUNT_ALL_USERPROFILES_BY_NAME_PATTERN_FOR_CASESENSITIVE, values);
            } else {
                JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("userProfileName", "%" + userProfileNamePattern + "%")
                        .put("$userProfileIdList", userprofileIds);
                knLogger.debug(methodName, "values-", values);
                queryResult = KnCBSRepository.getInstance().getQueryResult(COUNT_ALL_USERPROFILES_BY_NAME_PATTERN, values);
            }


            Map<String, Object> queryResultMap = new HashMap<String, Object>();
            for (JsonObject row : queryResult.rowsAsObject()) {
                queryResultMap = row.toMap();
            }

            if (queryResultMap.containsKey("$1")) {
                result = (Integer) queryResultMap.get("$1");
                if (result == 0) {
                    result = null;
                }
            }
            knLogger.info(methodName, "queryResult-", result);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException =", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE,
                    COUNT_ALL_USERPROFILES_BY_NAME_PATTERN);
        } catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    COUNT_ALL_USERPROFILES_BY_NAME_PATTERN);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    COUNT_ALL_USERPROFILES_BY_NAME_PATTERN);
        }
        return result;

    }

    public List<KnCorpUserProfileDTO> getUserProfileListByUserProfileNamePatternSharedCorp(List<String> userProfileIds,
                                                                                           String userProfileNamePattern, String startIndex, String limit, String isCaseSensitiveSearch) throws KnDAOException {

        final String methodName = "getUserProfileListByUserProfileNamePatternSharedCorp(String,String,String,String)";
        knLogger.info(methodName, "userProfileIds - ", userProfileIds , "userProfileNamePattern -", userProfileNamePattern, "startIndex - ", startIndex, "limit - ", limit);
        KnCorpUserProfileDTO userProfileDTO;
        List<KnCorpUserProfileDTO> userProfileList = new ArrayList<>();
        QueryResult queryResult = null;
        try {
            if(userProfileNamePattern!=null) {
                if(userProfileNamePattern.contains("%")) {
                    userProfileNamePattern=userProfileNamePattern.replace("%","\\%");
                }
                if(userProfileNamePattern.contains("_")) {
                    userProfileNamePattern=userProfileNamePattern.replace("_","\\_");
                }
            }

            if (null == isCaseSensitiveSearch || isCaseSensitiveSearch.isEmpty() || isCaseSensitiveSearch.equals("0")) {
                JsonObject values = JsonObject.create().put("profileIdList", userProfileIds).put("userProfileName", "%" + userProfileNamePattern.toLowerCase() + "%")
                        .put("offset", Integer.parseInt(startIndex)).put("limit", Integer.parseInt(limit));
                knLogger.debug(methodName, "values-", values);
                queryResult = KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILELIST_BY_PROFILEID_AND_UP_NAME_PATTERN_FOR_CAETSENSITIVE, values);
            } else {
                JsonObject values = JsonObject.create().put("profileIdList", userProfileIds).put("userProfileName", "%" + userProfileNamePattern + "%")
                        .put("offset", Integer.parseInt(startIndex)).put("limit", Integer.parseInt(limit));
                knLogger.debug(methodName, "values-", values);
                queryResult = KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILELIST_BY_PROFILEID_AND_UP_NAME_PATTERN, values);
            }

            knLogger.debug(methodName, "resultqueryadded-", queryResult);
            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfileDTO = commonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                userProfileList.add(userProfileDTO);
            }
            knLogger.debug(methodName, "queryResult-", userProfileList);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_PROFILEID_AND_UP_NAME_PATTERN);
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_PROFILEID_AND_UP_NAME_PATTERN);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_PROFILEID_AND_UP_NAME_PATTERN);
        }
        return userProfileList;

    }

    public Integer getMaxTotalCountByCorpIdAndProfileNamePattern(String corpId, String userProfileNamePattern) throws KnDAOException {
        final String methodName = "getMaxTotalCountByCorpIdAndProfileNamePattern()";
        Integer result = null;
        knLogger.debug(methodName, "corpId - ", corpId," userProfileNamePattern :",userProfileNamePattern);
        try {
            String escapedProfileName = Pattern.quote(userProfileNamePattern);
            JsonObject jsonProfileId = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("userProfileName", "(?i)(" + escapedProfileName + ")");
            QueryResult queryResult = KnCBSRepository.getInstance().getQueryResult(GET_MAX_TOTALCOUNT_BY_CORPID_AND_PROFILE_NAME_PATTERN,jsonProfileId);
            Map<String, Object> queryResultMap=new HashMap<String, Object>();
            for (JsonObject row : queryResult.rowsAsObject()) {
                queryResultMap = row.toMap();
            }
            result=(Integer)queryResultMap.get("$1");
            if(result==0) {
                result=null;
            }
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to check the maxTotalCount for the given corpId", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_CORPID_AND_UP_NAME_PATTERN);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to check the maxTotalCount for the given corpId", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_CORPID_AND_UP_NAME_PATTERN);
        }
        knLogger.debug(methodName," Exit :",result);
        return result;
    }

    /**
     * @param corpId
     * @param userProfileIndex
     * @return
     * @throws KnDAOException
     */
    public Collection<KnSubscriberUserProfileDTO> getUserProfileListByUserProfileIndex(String corpId,
                                                                                       List<Integer> userProfileIndex) throws KnDAOException {

        final String methodName = "getUserProfileListByUserProfileIndex(String,List<Integer>)";
        knLogger.debug(methodName, "corpId -", corpId, "userProfileIndex -", userProfileIndex);
        KnCorpUserProfileDTO userProfileDTO;
        List<KnSubscriberUserProfileDTO> userProfileList = new ArrayList<>();
        try {
           JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("userProfileIndexList", userProfileIndex);
           //JsonObject values = JsonObject.create().put("userProfileIndexList", userProfileIndex);
            knLogger.debug(methodName, "values-", values);
           QueryResult queryResult= KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILELIST_BY_USER_PROFILE_INDEX_LIST,values);
            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfileDTO = commonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                KnSubscriberUserProfileDTO subscrriberUserProfileDTO = new KnSubscriberUserProfileDTO();
                subscrriberUserProfileDTO.setUserProfileIndex(userProfileDTO.getUserProfileIndex());
                subscrriberUserProfileDTO.setUserProfileName(userProfileDTO.getUserProfileName());
                subscrriberUserProfileDTO.set_id(userProfileDTO.get_id());
                userProfileList.add(subscrriberUserProfileDTO);
            }
            knLogger.debug(methodName, "queryResult-", userProfileList);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_USER_PROFILE_INDEX_LIST);
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_USER_PROFILE_INDEX_LIST);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_USER_PROFILE_INDEX_LIST);
        }
        return userProfileList;

    }

    public Map<String, Integer> getUserProfileIdFromSublistIds(String corpId, Collection<Integer> subListIds) throws KnDAOException {
        final String methodName = "getUserProfileIdFromSublistIds(String, Collection<Integer>)";
        knLogger.debug(methodName, "corpId -", corpId, "subListIds -", subListIds);
        Map<String, Integer> userProfileIdListMap = new HashMap<>();
        try {
            List<Integer> sIds = new ArrayList<>(subListIds);
            String subListIdList = formIntegerCommaSeperatedIdList(sIds);
            String GET_USERPROFILE_IDS_BY_USER_SUBLIST_DS = "select " + META_ID + KnConstants.COMMA + CONTACT_LIST_ID + " from `pocdata` where " +
                    "corporateID=$corpId and contactListID IN [" + subListIdList + "]" ;
            JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId));
            knLogger.debug(methodName, "values-", values);
            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILE_IDS_BY_USER_SUBLIST_DS,values);
            KnCorpUserProfileDTO userProfileDTO = null;
            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfileDTO = KnCorpCommonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                userProfileIdListMap.put(userProfileDTO.get_id(), userProfileDTO.getContactListID());
            }
            knLogger.debug(methodName, "userProfileIdListMap-", userProfileIdListMap);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile Ids", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_IDS_BY_USER_SUBLIST_DS);
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get User Profile Ids", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_IDS_BY_USER_SUBLIST_DS);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_IDS_BY_USER_SUBLIST_DS);
        }
        return userProfileIdListMap;
    }

    public Map<String, String> getUserProfileIdNameBySublistId(Integer corpId, Integer subListId) throws KnDAOException {
        final String methodName = "getUserProfileIdNameBySublistId()";
        knLogger.debug(methodName, " corpId -", corpId, " subListId -", subListId);
        Map<String, String> userProfileIdListMap = new HashMap<>();
        String GET_USERPROFILE_IDS_NAME_BY_USER_SUBLIST_DS = "select " + META_ID + KnConstants.COMMA + USER_PROFILE_NAME + " from `pocdata` where " +
                "corporateID=$corpId and contactListID=$subList " ;
        try {
            JsonObject values = JsonObject.create().put("corpId", corpId).put("subList",subListId);
            knLogger.debug(methodName, " result -", values);
            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILE_IDS_NAME_BY_USER_SUBLIST_DS,values);
            knLogger.debug(methodName, "paramQuery-", queryResult);
            KnCorpUserProfileDTO userProfileDTO = null;
            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfileDTO = KnCorpCommonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                userProfileIdListMap.put(userProfileDTO.get_id(), userProfileDTO.getUserProfileName());
            }
            knLogger.debug(methodName, " userProfileIdListMap - ", userProfileIdListMap);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile Ids", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_IDS_NAME_BY_USER_SUBLIST_DS);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ",e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile Ids", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_IDS_NAME_BY_USER_SUBLIST_DS);
        }
        return userProfileIdListMap;
    }

    public Map<String, Collection<String>> getUserProfileIdFromGroupIds(Collection<Integer> corpIds, Collection<Integer> groupIds) throws KnDAOException {
        final String methodName = "getUserProfileIdFromGroupIds(Collection<String>, Collection<Integer>)";
        knLogger.debug(methodName, "corpIds -", corpIds, "groupIds -", groupIds);
        Map<String, Collection<String>> userProfileIdListMap = new HashMap<>();
        try {
            String groupIdList = formIntegerCommaSeperatedIdList(groupIds);
            String corpIdListStr = formIntegerCommaSeperatedIdList(corpIds);
            final String GET_USERPROFILE_IDS_BY_USER_GROUP_DS = "select meta().id, d.groupList from `" + CB_BUCKET_NAME + "` AS d where " + CORPORATE_ID +
                    " IN [" + corpIdListStr + "] and ANY e IN d.groupList SATISFIES e.groupID IN [" + groupIdList + "] END";
            QueryResult queryResult=KnCBSRepository.getInstance().getPOCCluster().query(GET_USERPROFILE_IDS_BY_USER_GROUP_DS);
            KnCorpUserProfileDTO userProfileDTO = null;
            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfileDTO = KnCorpCommonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                userProfileIdListMap.put(userProfileDTO.get_id(), userProfileDTO.getGroupList().stream()
                        .filter(g -> g.getGroupID() != null && groupIds.contains(g.getGroupID())).map(g -> String.valueOf(g.getGroupID()))
                        .collect(Collectors.toSet()));
            }
            knLogger.debug(methodName, "userProfileIdListMap-", userProfileIdListMap);

        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile Ids", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_IDS_BY_USER_GROUP_DS);
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ",e);
            throw KnDbUtil.processException(e, "Failed to get User Profile Ids", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_IDS_BY_USER_GROUP_DS);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile Ids", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_IDS_BY_USER_GROUP_DS);
        }
        return userProfileIdListMap;
    }

    public void updateImpactedTuPerms(int corpId,Collection<String> addTuMdns,Collection<String> removeTuMdns, Collection<String> upmIds) throws KnDAOException {
        final String methodName = "DAO.updateImpactedTuPerms()";
        knLogger.debug(methodName,"addTuMdns :", addTuMdns == null ? addTuMdns : KnGDPRTemplate.mdnList(addTuMdns),
                " removeTuMdns ",removeTuMdns == null ? removeTuMdns : KnGDPRTemplate.mdnList(removeTuMdns)," upmIds :",upmIds," corpId :",corpId);
        try{
            int defaultCount=0;
            JsonObject values = JsonObject.create().put("id", corpId);
            String commaSepUpmIds = formCommaSeperatedIdList(upmIds);
            //adding tu's
            if (addTuMdns != null && !addTuMdns.isEmpty()) {
                StringBuilder query = new StringBuilder();
                query.append("UPDATE pocdata AS d SET d.mcpttPermissionsConfig = ARRAY_APPEND(d.mcpttPermissionsConfig, ");
                int count = 0;
                for (String mdn : addTuMdns) {
                    if (count > 0) {
                        query.append(",");
                    }
                    query.append("{ \"mdn\": \"" + mdn + "\", \"permBitSet\":" + defaultCount + "}");
                    count++;
                }
                query.append(")");
                query.append(" WHERE "+META_ID+" IN  ["+commaSepUpmIds+"] AND corporateID=$id ");
                KnCBSRepository.getInstance().getQueryResult(query.toString(),values);
            }
            //removing tu's
            if (removeTuMdns != null && !removeTuMdns.isEmpty()) {
                // Convert Set of String
                String removePermCommaSeperated = formCommaSeperatedIdList(removeTuMdns);
                String query = "UPDATE pocdata As p SET p.mcpttPermissionsConfig = ARRAY g FOR g " +
                        " IN p.mcpttPermissionsConfig WHEN g.mdn NOT IN  [" + removePermCommaSeperated + "] END " +
                        " WHERE "+META_ID+" IN  ["+commaSepUpmIds+"] AND corporateID=$id ";
                KnCBSRepository.getInstance().getQueryResult(query,values);
            }
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to update impacted TU", pttServerId, XDM_CORP_USERPROFILE, " UPDATE pocdata ");
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to update impacted TU", pttServerId, XDM_CORP_USERPROFILE, " UPDATE pocdata ");
        }
    }

    public void deleteUserProfilesByCorpId(String corpId) throws KnDAOException {
        final String methodName = "deleteUserProfilesByCorpId(corpId)";
        knLogger.debug(methodName, "corpId -", corpId);
        try {
            JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId));
            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(DELETE_USER_PROFILE_BY_CORPID,values);
            knLogger.debug(methodName, "queryResult-", queryResult.toString());
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to Delete User Profile by corpId", pttServerId, XDM_CORP_USERPROFILE,
                    DELETE_USER_PROFILE_BY_CORPID);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to Delete User Profile by corpId", pttServerId, XDM_CORP_USERPROFILE,
                    DELETE_USER_PROFILE_BY_CORPID);
        }
    }

    public Collection<String> getUserProfileIdsByCorpId(Integer corpId) throws KnDAOException {
        final String methodName = "getUserProfileIdsByCorpId(String)";
        knLogger.debug(methodName, "corpId -", corpId);
        List<String> userProfileList = new ArrayList<>();
        try {
            JsonObject values = JsonObject.create()
                    .put("corpId", corpId);
            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILEID_LIST_BY_CORPID,values);
            for (JsonObject row : queryResult.rowsAsObject()) {
                KnCorpUserProfileDTO userProfileDTO = KnCorpCommonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                userProfileList.add(userProfileDTO.get_id());
            }
            knLogger.debug(methodName, "queryResult-", userProfileList);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILEID_LIST_BY_CORPID);
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILEID_LIST_BY_CORPID);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILEID_LIST_BY_CORPID);
        }
        return userProfileList;
    }

    public Map<String, Integer> getAndDeleteGroupIdsFromUserProfile(Collection<Integer> groupIds) throws KnDAOException {
        final String methodName = "getAndDeleteGroupIdsFromUserProfile()";
        knLogger.debug(methodName,  "groupIds -", groupIds);
        Map<String, Integer> userProfileIdNCorpIdMap = new HashMap<>();
        final StringBuilder GET_USERPROFILE_IDS_BY_GROUPIDS=new StringBuilder();
        try {
            String groupIdList = formIntegerCommaSeperatedIdList(groupIds);
            GET_USERPROFILE_IDS_BY_GROUPIDS.append("select meta().id, corporateID from `" + CB_BUCKET_NAME + "` AS d where ");
            GET_USERPROFILE_IDS_BY_GROUPIDS.append(" ANY e IN d.groupList SATISFIES e.groupID IN [ ");
            GET_USERPROFILE_IDS_BY_GROUPIDS.append(groupIdList);
            GET_USERPROFILE_IDS_BY_GROUPIDS.append(" ] END ");
            QueryResult queryResult=KnCBSRepository.getInstance().getPOCCluster().query(GET_USERPROFILE_IDS_BY_GROUPIDS.toString());
            knLogger.debug(methodName, "query-", GET_USERPROFILE_IDS_BY_GROUPIDS.toString());
            KnCorpUserProfileDTO userProfileDTO = null;
            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfileDTO = KnCorpCommonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                userProfileIdNCorpIdMap.put(userProfileDTO.get_id(), userProfileDTO.getCorporateID());
            }
            knLogger.debug(methodName, " userProfileIdNCorpIdMap- ", userProfileIdNCorpIdMap);
            JsonObject jsonProfileId = JsonObject.create()
                    .put("pid", groupIdList);
            String REMOVE_GROUPS_FROM_USERPROFILES = "UPDATE pocdata As p SET p.groupList = ARRAY g FOR g IN p.groupList WHEN g.groupID NOT IN [" + groupIdList + "] END ";
            knLogger.debug(methodName, "removedGroupids query:", REMOVE_GROUPS_FROM_USERPROFILES);

            KnCBSRepository.getInstance().getQueryResult(REMOVE_GROUPS_FROM_USERPROFILES,jsonProfileId);

            knLogger.debug(methodName,"Done removing group ids from upm :",groupIdList);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile Ids", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_IDS_BY_GROUPIDS.toString());
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get User Profile Ids", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_IDS_BY_GROUPIDS.toString());
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile Ids", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_IDS_BY_GROUPIDS.toString());
        }
        return userProfileIdNCorpIdMap;
    }

    public void deleteMcpttPermConfig(String mdn,String corpId) throws KnDAOException {
        final String methodName = "deleteMcpttPermConfig()";
        knLogger.debug(methodName, "corpId -", corpId, "mdn - ", KnGDPRTemplate.mdn(mdn));
        try {
            JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("mdn", mdn);
            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(REMOVE_MDN_FROM_MCPTT_CONFIG,values);
            knLogger.debug(methodName, "queryResult-", queryResult.toString());
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to remove mdn from mcptt config", pttServerId, XDM_CORP_USERPROFILE,
                    REMOVE_MDN_FROM_MCPTT_CONFIG);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to remove mdn from mcptt config", pttServerId, XDM_CORP_USERPROFILE,
                    REMOVE_MDN_FROM_MCPTT_CONFIG);
        }
    }

    public void deleteMcpttPermConfig(List<String> mdnList, String corpId) throws KnDAOException {
        final String methodName = "deleteMcpttPermConfig()";
        knLogger.debug(methodName, "corpId -", corpId, "mdnList - ", KnGDPRTemplate.mdnList(mdnList));

        try {
            JsonArray mdnJsonArray = JsonArray.from(mdnList);

            JsonObject values = JsonObject.create()
                    .put("corpId", Integer.parseInt(corpId))
                    .put("mdnList", mdnJsonArray);

            QueryResult queryResult = KnCBSRepository.getInstance()
                    .getQueryResult(REMOVE_MDNLIST_FROM_MCPTT_CONFIG, values);

            knLogger.debug(methodName, "queryResult - ", queryResult.toString());

        } catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(
                    e,
                    "Failed to remove mdns from mcptt config",
                    pttServerId,
                    XDM_CORP_USERPROFILE,
                    REMOVE_MDN_FROM_MCPTT_CONFIG
            );
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(
                    e,
                    "Failed to remove mdns from mcptt config",
                    pttServerId,
                    XDM_CORP_USERPROFILE,
                    REMOVE_MDN_FROM_MCPTT_CONFIG
            );
        }
    }


    /**
     * @param corpId
     * @param userProfileIndex
     * @return
     * @throws KnDAOException
     */
    public Collection<KnSubscriberUserProfileDTO> getUserProfileListByProfileIds(List<String> userProfileIds) throws KnDAOException {

        final String methodName = "getUserProfileListByProfileIds(List<String>)";
        knLogger.debug(methodName, "user profile ids", userProfileIds);
        KnCorpUserProfileDTO userProfileDTO;
        List<KnSubscriberUserProfileDTO> userProfileList = new ArrayList<>();
        try {
            // JsonObject values = JsonObject.create().put("corpId", Integer.parseInt(corpId)).put("userProfileIndexList", userProfileIndex);
            JsonObject values = JsonObject.create().put("userProfileIdList", userProfileIds);
            knLogger.debug(methodName, "values-", values);
            QueryResult queryResult= KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILELIST_BY_USER_PROFILE_ID,values);
            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfileDTO = commonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                KnSubscriberUserProfileDTO subscrriberUserProfileDTO = new KnSubscriberUserProfileDTO();
                subscrriberUserProfileDTO.setUserProfileIndex(userProfileDTO.getUserProfileIndex());
                subscrriberUserProfileDTO.setUserProfileName(userProfileDTO.getUserProfileName());
                subscrriberUserProfileDTO.set_id(userProfileDTO.get_id());
                userProfileList.add(subscrriberUserProfileDTO);
            }
            knLogger.debug(methodName, "queryResult-", userProfileList);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_USER_PROFILE_INDEX_LIST);
        }catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_USER_PROFILE_INDEX_LIST);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILELIST_BY_USER_PROFILE_INDEX_LIST);
        }
        return userProfileList;

    }

    public List<KnCorpUserProfileDTO> getAllDistinctUPMByCorp(String corpId) {
        final String methodName = "getAllDistinctUPMByCorp()";
        knLogger.debug(methodName, "corpId -", corpId);
        QueryResult queryResult = null;
        KnCorpUserProfileDTO userProfileDTO;
        List<KnCorpUserProfileDTO> userProfileList = new ArrayList<>();
        try {
            JsonObject values = JsonObject.create().put("corporateID", Integer.parseInt(corpId));
            queryResult = KnCBSRepository.getInstance().getQueryResult(GET_ALL_DISTINCT_USERPROFILES_FROM_CBS_PER_CORP, values);
            for (JsonObject row : queryResult.rowsAsObject()) {
                userProfileDTO = commonInfoUtil.jsonToObject(row.toString(), KnCorpUserProfileDTO.class);
                userProfileList.add(userProfileDTO);
            }
        } catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException =", e.getMessage());
        } catch (Exception e) {
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            knLogger.error(methodName, "Exception =", e.getMessage());
        }
        return userProfileList;
    }

    public Collection<KnCorpUserProfileDTO> getUserProfileListByCorpAndHierarchyId(String corpId, String hierarchyId, String startIndex, String limit) throws KnDAOException {
        final String methodName = "getUserProfileListByCorpAndHierarchyId()";
        knLogger.info(methodName, "corpId -", corpId, "hierarchyId -", hierarchyId, "startIndex - ", startIndex, "limit - ", limit);
        List<KnCorpUserProfileDTO> userProfileList = new ArrayList<>();
        try {
            JsonObject values = JsonObject.create()
                    .put("corpId", Integer.parseInt(corpId))
                    .put("hierarchyId", hierarchyId)
                    .put("offset", Integer.parseInt(startIndex))
                    .put("limit", Integer.parseInt(limit));
            QueryResult queryResult = KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILELIST_BY_CORP_AND_HIERARCHY, values);
            for (JsonObject object : queryResult.rowsAsObject()) {
                KnCorpUserProfileDTO userProfileDTO = commonInfoUtil.jsonToObject(object.toString(), KnCorpUserProfileDTO.class);
                userProfileList.add(userProfileDTO);
            }
            knLogger.debug(methodName, "queryResult-", userProfileList);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile List by hierarchy", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILELIST_BY_CORP_AND_HIERARCHY);
        } catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List by hierarchy", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILELIST_BY_CORP_AND_HIERARCHY);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List by hierarchy", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILELIST_BY_CORP_AND_HIERARCHY);
        }
        return userProfileList;
    }

    public Integer getMaxTotalCountByCorpAndHierarchyId(String corpId, String hierarchyId) throws KnDAOException {
        final String methodName = "getMaxTotalCountByCorpAndHierarchyId()";
        Integer result = null;
        knLogger.debug(methodName, "corpId - ", corpId, "hierarchyId - ", hierarchyId);
        try {
            JsonObject values = JsonObject.create()
                    .put("corpId", Integer.parseInt(corpId))
                    .put("hierarchyId", hierarchyId);
            QueryResult queryResult = KnCBSRepository.getInstance().getQueryResult(GET_MAX_TOTALCOUNT_BY_CORP_AND_HIERARCHY, values);
            String resultStr = queryResult.rowsAsObject().get(0).toString();
            int colonIndex = resultStr.indexOf(":");
            int bracesIndex = resultStr.indexOf("}");
            if (resultStr.substring(colonIndex + 1, bracesIndex) != null)
                result = Integer.parseInt(resultStr.substring(colonIndex + 1, bracesIndex));
            else
                result = null;
            knLogger.debug(methodName, "--->Exit  result - ", result);
        } catch (NoSuchElementException e) {
            result = null;
            knLogger.error(methodName, "NoSuchElementException msg", e.getMessage());
        } catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get maxTotalCount by corp and hierarchy", pttServerId, XDM_CORP_USERPROFILE, GET_MAX_TOTALCOUNT_BY_CORP_AND_HIERARCHY);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get maxTotalCount by corp and hierarchy", pttServerId, XDM_CORP_USERPROFILE, GET_MAX_TOTALCOUNT_BY_CORP_AND_HIERARCHY);
        }
        return result;
    }

    public Set<String> getUserProfileIdsByHierarchyId(Collection<Integer> corpIds, String hierarchyId) throws KnDAOException {
        final String methodName = "getUserProfileIdsByHierarchyId()";
        knLogger.debug(methodName, "corpIds -", corpIds, " hierarchyId -", hierarchyId);
        Set<String> profileIds = new HashSet<>();
        try {
            JsonArray corpIdArray = JsonArray.from(new ArrayList<>(corpIds));
            JsonObject values = JsonObject.create()
                    .put("corpIds", corpIdArray)
                    .put("hierarchyId", hierarchyId);
            QueryResult queryResult = KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILE_IDS_BY_HIERARCHY_ID, values);
            for (JsonObject row : queryResult.rowsAsObject()) {
                String id = row.getString("id");
                if (id != null) {
                    profileIds.add(id);
                }
            }
            knLogger.debug(methodName, "profileIds found -", profileIds);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get profileIds by hierarchyId", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILE_IDS_BY_HIERARCHY_ID);
        } catch (RejectedExecutionException e) {
            knLogger.error(methodName, "RejectedExecutionException ", e);
            throw KnDbUtil.processException(e, "Failed to get profileIds by hierarchyId", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILE_IDS_BY_HIERARCHY_ID);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get profileIds by hierarchyId", pttServerId, XDM_CORP_USERPROFILE, GET_USERPROFILE_IDS_BY_HIERARCHY_ID);
        }
        return profileIds;
    }

}
