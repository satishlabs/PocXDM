/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.xdms.xdmintf.resources;

import com.kodiak.common.commdto.request.IXDMRequestDTO;
import com.kodiak.common.commdto.request.KnXDMChangeMDNInfoDTO;
import com.kodiak.common.commdto.request.KnXDMSubsInfoDTO;
import com.kodiak.common.commdto.request.KnXDMSubsProvInfoDTO;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.xdmintf.IXDMFacadeIntf;
import com.kodiak.xdms.xdmintf.impl.KnXDMFacadeImplV2;
import com.kodiak.xdms.xdmintf.impl.KnXDMFacadeImpl;

import java.util.Map;

import static com.kodiak.common.resources.KnConstants.*;

public class KnXdmIntfUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXdmIntfUtil.class);
    public static int num = 0;
    final static String FROM_RELEASE_TAG_VERSION = "FROM_RELEASE_TAG_VERSION";
    final static String TO_RELEASE_TAG_VERSION = "TO_RELEASE_TAG_VERSION";
    final static String UPGRADE_STATUS = "UPGRADE_STATUS";
    final static String ASYNC_FLOW_CREATE_SUBSCRIBER_FLAG = "ASYNC_FLOW_CREATE_SUBSCRIBER_FLAG";
    final static String ASYNC_FLOW_MODIFY_SUBSCRIBER_FLAG = "ASYNC_FLOW_MODIFY_SUBSCRIBER_FLAG";
    final static String ASYNC_FLOW_DELETE_SUBSCRIBER_FLAG = "ASYNC_FLOW_DELETE_SUBSCRIBER_FLAG";
    final static String ASYNC_FLOW_CHANGE_MDN_FLAG = "ASYNC_FLOW_CHANGE_MDN_FLAG";
    final static String ASYNC_FLOW_UPDATE_USER_ID_FLAG = "ASYNC_FLOW_UPDATE_USER_ID_FLAG";
    final static String ASYNC_FLOW_UPDATE_MC_ID_FLAG = "ASYNC_FLOW_UPDATE_MC_ID_FLAG";
    private static String ENABLED = "1";

    public static IXDMFacadeIntf getFacadeObj(int operationId, IXDMRequestDTO inputDTO) {
        String methodName = "getFacadeObj()";
        IXDMFacadeIntf facadeObj = new KnXDMFacadeImpl();
        try {
            KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            Map<String, String> mcsCommonConfig = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId);
            facadeObj = new KnXDMFacadeImpl();
            boolean upgradeCompleted = isUpgradeCompleted(mcsCommonConfig);
            boolean operationFlag = getConfigOperationFlag(operationId, mcsCommonConfig);
            if (upgradeCompleted && operationFlag) {
                knLogger.debug(methodName, "Operation is in V2 path ", " For - ", operationId);
                facadeObj = new KnXDMFacadeImplV2();
            }

        } catch (KnBOException e) {
            throw new RuntimeException(e);
        }
        knLogger.debug(methodName, "Returning facade object.", facadeObj.getClass(), " For - ", operationId);
        return facadeObj;
    }

    private static boolean getConfigOperationFlag(int operationId, Map<String, String> mcsCommonConfig) {
        String createSubscriber = (mcsCommonConfig.get(ASYNC_FLOW_CREATE_SUBSCRIBER_FLAG) != null) ? mcsCommonConfig.get(ASYNC_FLOW_CREATE_SUBSCRIBER_FLAG) : ENABLED;
        String modifySubscriber = (mcsCommonConfig.get(ASYNC_FLOW_MODIFY_SUBSCRIBER_FLAG) != null) ? mcsCommonConfig.get(ASYNC_FLOW_MODIFY_SUBSCRIBER_FLAG) : ENABLED;
        String deletSubscriber = (mcsCommonConfig.get(ASYNC_FLOW_DELETE_SUBSCRIBER_FLAG) != null) ? mcsCommonConfig.get(ASYNC_FLOW_DELETE_SUBSCRIBER_FLAG) : ENABLED;
        String changeMdn = (mcsCommonConfig.get(ASYNC_FLOW_CHANGE_MDN_FLAG) != null) ? mcsCommonConfig.get(ASYNC_FLOW_CHANGE_MDN_FLAG) : ENABLED;
        String updateUserId = (mcsCommonConfig.get(ASYNC_FLOW_UPDATE_USER_ID_FLAG) != null) ? mcsCommonConfig.get(ASYNC_FLOW_UPDATE_USER_ID_FLAG) : ENABLED;
        String updateMcId = (mcsCommonConfig.get(ASYNC_FLOW_UPDATE_MC_ID_FLAG) != null) ? mcsCommonConfig.get(ASYNC_FLOW_UPDATE_MC_ID_FLAG) : ENABLED;
        boolean flag = false;
        if (OP_ID_CREATE_SUBS == operationId && createSubscriber.equals(ENABLED)) {
            flag = true;
        }
        if (OP_ID_DELETE_SUBS == operationId && deletSubscriber.equals(ENABLED)) {
            flag = true;
        }
        if (OP_ID_UPDATE_SUBS == operationId && modifySubscriber.equals(ENABLED)) {
            flag = true;
        }

        if (OP_ID_CHANGE_MDN == operationId && changeMdn.equals(ENABLED)) {
            flag = true;
        }
        if (OP_ID_UPDATE_MCSIDS == operationId && updateMcId.equals(ENABLED)) {
            flag = true;
        }
        if (OP_ID_UPDATE_USERID == operationId && updateUserId.equals(ENABLED)) {
            flag = true;
        }
        return flag;
    }

    private static boolean isUpgradeCompleted(Map<String, String> mcsCommonConfig) {
        String fromReleaseTag = mcsCommonConfig.get(FROM_RELEASE_TAG_VERSION);
        String toReleaseTag = mcsCommonConfig.get(TO_RELEASE_TAG_VERSION);
        String upgradeStatus = mcsCommonConfig.get(UPGRADE_STATUS);
        float fromRelTagInt = 0.0F;
        float toRelTagInt = 0.0F;
        int upgradeStatusInt = 0;
        if (null != fromReleaseTag) {
            fromRelTagInt = Float.parseFloat(fromReleaseTag);
        }
        if (null != toReleaseTag) {
            toRelTagInt = Float.parseFloat(fromReleaseTag);
        }
        if (null != upgradeStatus) {
            upgradeStatusInt = Integer.parseInt(upgradeStatus);
        }
        if(toRelTagInt > 13.1) {
            return true;
        }
        if (toRelTagInt == 13.1) {
            if (fromRelTagInt < toRelTagInt && upgradeStatusInt == 7)
                return true;

            if (fromRelTagInt == toRelTagInt && upgradeStatusInt >= 5) {
                return true;
            }
        }
        return false;
    }
}
