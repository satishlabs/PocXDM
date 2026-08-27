/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dao.persister.db.tables.xdm;

import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static com.couchbase.client.java.kv.ReplaceOptions.replaceOptions;
import com.couchbase.client.core.deps.io.netty.buffer.ByteBuf;
import com.couchbase.client.core.deps.io.netty.buffer.Unpooled;
import com.couchbase.client.java.json.*;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.kv.UpsertOptions;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.kv.GetResult;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.cb.util.KnCBSRepository;



import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.xdms.server.common.dto.persistdat.KnCorpUserProfileDTO;

import static com.kodiak.xdms.server.common.resources.KnDAOSourceTypes.XDM_CORP_USERPROFILE;

public class KnXDMCBUserProfileMgmtDAO {
    public static final KnLogger knLogger = KnLogger.getLogger(KnXDMCBUserProfileMgmtDAO.class);
    private KnCBSRepository knCouchDbManager;
    private String pttServerId;
    private static final String CB_BUCKET_NAME = "pocdata";
    private static final String USER_PROFILE_INDEX = "userProfileIndex";
    private static final String USER_PROFILE_NAME = "userProfileName";
    private static final String CORPORATE_ID = "corporateID";
    private static final String META_ID = "META().id";

    private static final String GET_USERPROFILE_NAME_BY_INDEX = "SELECT "+ USER_PROFILE_NAME
            +" from "+CB_BUCKET_NAME+" WHERE corporateID=$corpId AND userProfileIndex=$userProfileIndexList";

    private static final String GET_USERPROFILE_DETAILS = "select * from `" + CB_BUCKET_NAME + "` where "+META_ID+"=$profileId";

    public KnXDMCBUserProfileMgmtDAO(){}

    public KnXDMCBUserProfileMgmtDAO(String pttServerId) {
        this.knCouchDbManager = KnCBSRepository.getInstance();
        this.pttServerId = pttServerId;
    }


    public KnCorpUserProfileDTO getUserProfileNameByIndex(int corpId,Integer userProfileIndex) throws KnDAOException {

        final String methodName = "getUserProfileNameByIndex(String,Integer)";
        knLogger.debug(methodName, "corpId -", corpId,"userProfileIndex -",userProfileIndex);
        KnCorpUserProfileDTO userProfile=null;
        try {
            JsonObject values = JsonObject.create().put("corpId", corpId).put("userProfileIndexList",userProfileIndex);
            knLogger.debug(methodName, "values-", values);

            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILE_NAME_BY_INDEX,values);
            for (JsonObject object : queryResult.rowsAsObject()) {
                userProfile = jsonToObject(object.toString(), KnCorpUserProfileDTO.class);
            }
            knLogger.debug(methodName, "queryResult userProfileName -", userProfile);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            throw KnDbUtil.processException(ex, "Failed to get User Profile", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_NAME_BY_INDEX);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile List", pttServerId, XDM_CORP_USERPROFILE,
                    GET_USERPROFILE_NAME_BY_INDEX);
        }
        return userProfile;

    }

    private static <T> T jsonToObject(String str,Class<T> clazz) {
        String methodName="jsonToObject()";
        T obj = null;
        try {
            knLogger.debug(methodName,"result json: ",str);

            obj = (T) new ObjectMapper().readValue(str, clazz);
        } catch (IOException ex) {
            knLogger.error(methodName,"Exception- ",ex.getMessage());
        }
        return obj;
    }


    /**
     * @param corpId
     * @param userProfileId
     * @return KnCorpUserProfileDTO
     * @throws KnDAOException
     */
    public KnCorpUserProfileDTO getUserProfileById(String userProfileId) throws KnDAOException {

        final String methodName = "getUserProfileById(String)";

        knLogger.debug(methodName, "profileId - ", userProfileId);
        KnCorpUserProfileDTO userProfile = new KnCorpUserProfileDTO();
        try {
            JsonObject values = JsonObject.create().put("profileId", userProfileId);
            QueryResult queryResult=KnCBSRepository.getInstance().getQueryResult(GET_USERPROFILE_DETAILS,values);
            userProfile = jsonToObject(queryResult.rowsAsObject().get(0).get(CB_BUCKET_NAME).toString(), KnCorpUserProfileDTO.class);
            knLogger.debug(methodName, "queryResult-", userProfile);
        } catch (NoSuchElementException ex) {
            knLogger.error(methodName, "NoSuchElementException ", ex.getMessage());
            userProfile = null;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception ", e);
            KnCBSRepository.generateCBSTimeoutAlarm(e);
            throw KnDbUtil.processException(e, "Failed to get User Profile", pttServerId, "XDM_CORP_USERPROFILE", GET_USERPROFILE_DETAILS);
        }
        return userProfile;
    }
}