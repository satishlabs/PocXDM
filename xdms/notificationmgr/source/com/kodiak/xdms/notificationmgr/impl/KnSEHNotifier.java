/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     28/5/14         7.7.0
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
package com.kodiak.xdms.notificationmgr.impl;

import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.tlvgenerator.generator.KnTLVDocDiffGenerator;
import com.kodiak.utilities.tlvgenerator.generator.dto.KnPoCSubsTGSModeChanegDTO;
import com.kodiak.utilities.tlvgenerator.generator.dto.KnRequestObject;
import com.kodiak.xdms.notificationmgr.beans.KnNotifyPayloadDTO;
import com.kodiak.xdms.notificationmgr.beans.KnSEHNotifyDTO;
import com.kodiak.xdms.notificationmgr.resources.KnXcapNotifyConstants;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.resources.KnConstants;

import java.util.ArrayList;
import java.util.List;

public class KnSEHNotifier {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSEHNotifier.class);
    private static KnSEHNotifier sehNotifier = null;
    private KnGenInfoUtil genInfoUtil = null;
    private String homeRtxId = null;

    /**
     * constructor
     */
    private KnSEHNotifier() {
        final String methodName = "Inside KnXcapDiffNotifier constructor";
        genInfoUtil = KnGenInfoUtil.getInstance();
        try {
            homeRtxId = genInfoUtil.retrieveLocalXDMPttServerId();
        } catch (KnBOException e) {
            knLogger.error(methodName, "exception occured in getting local xdm pttserver id ", e);
        }
    }

    /**
     * function to obtain the instance of the xcapDiffNotifier
     *
     * @return
     */
    public static synchronized KnSEHNotifier getInstance() {
        final String methodName = "getInstance()";
        if (sehNotifier == null) {
            sehNotifier = new KnSEHNotifier();
        }
        knLogger.debug(methodName, "Instance obtained");
        return sehNotifier;
    }

    public boolean generateSEHNotification(List<KnSEHNotifyDTO> sehNotifyDTOList) {
        final String methodName = "generateSEHNotification(List<KnSEHNotifyDTO>)";
        knLogger.debug(methodName);
        boolean notifyStatus = false;
        List<KnNotifyPayloadDTO> listOfNotifyPayLoads = generateNotifications(sehNotifyDTOList);
        //send feature id as -1 (feature id will be taken from KnNotifyPayloadDTO)
        int featureId = -1;
        if (listOfNotifyPayLoads != null && !listOfNotifyPayLoads.isEmpty()) {
            notifyStatus =KnXcapDiffNotifier.getInstance().sendBulkNotifications(listOfNotifyPayLoads, featureId, null, null, null);
        } else {
            knLogger.error(methodName, "Failed to send the notifications. Payload is empty - ", listOfNotifyPayLoads);
        }
        knLogger.exit(methodName);
        return notifyStatus;
    }

    /**
     * function to generate the List of SEH notification paylod for sending notification.
     *
     * @param sehNotifyDTOList
     * @return
     */
    private List<KnNotifyPayloadDTO> generateNotifications(List<KnSEHNotifyDTO> sehNotifyDTOList) {
        final String methodName = "generateNotifications(List<KnXcapDiffNotifyDTO>)";
        knLogger.debug(methodName);
        List<KnNotifyPayloadDTO> listOfNotifyPayload = new ArrayList<KnNotifyPayloadDTO>();
        for (KnSEHNotifyDTO xcapDiffNotifyObj : sehNotifyDTOList) {

            try {
                //todo need to add all seh notification here
                // send tgs mode change notification if tge flag is true
                if (xcapDiffNotifyObj.isTgsModeNotify()) {
                	knLogger.debug(methodName);
                    KnNotifyPayloadDTO notifyPayloadDTO = new KnNotifyPayloadDTO();
                    notifyPayloadDTO.setPayload(generateTGSModeNotification(xcapDiffNotifyObj));
                    notifyPayloadDTO.setPttServerId(xcapDiffNotifyObj.getTgsModeNotifyDTO().getPocHome());
                    notifyPayloadDTO.setFeatureId(KnXcapNotifyConstants.SUBS_PROFILE_CHANGE_FEATURE_ID);
                    listOfNotifyPayload.add(notifyPayloadDTO);
                }
            } catch (Exception ex) {
                knLogger.error(methodName, " Notification message construction failed:", ex);
            }

        }
        knLogger.info(methodName, " listOfNotifyPayload:", listOfNotifyPayload.size());
        knLogger.exit(methodName);
        return listOfNotifyPayload;
    }

    /**
     * function to send the subscribers tgs mode change notification XML.
     *
     * @param sehNotifyObj
     * @return
     */
    private byte[] generateTGSModeNotification(KnSEHNotifyDTO sehNotifyObj) {
        String methodName = "generateTGSModeNotification";
        KnTLVDocDiffGenerator tlvDocDiffGenerator = new KnTLVDocDiffGenerator();
        KnRequestObject requestObj = new KnRequestObject();
        int action = sehNotifyObj.getTgsModeNotifyDTO().getAction();
        byte[] payLoad = null;
        if (action == 0) {
            action = KnConstants.MESSAGE_TYPE.XDM_DIFF_NOTIFY.value();
        }
        try {
            int featureId = KnXcapNotifyConstants.SUBS_PROFILE_CHANGE_FEATURE_ID;
            requestObj.setUserMDN(sehNotifyObj.getTgsModeNotifyDTO().getMdn());
            requestObj.setFeatureID(featureId);
            requestObj.setHomeRTXId(homeRtxId);
            requestObj.setMessageID(action);
            requestObj.setRTXVersion(KnXcapNotifyConstants.RTX_VERSION);
            //We need to set protocol Version to 1 ,  else decoding will fail
            requestObj.setProtocolVersion(1);
            KnPoCSubsTGSModeChanegDTO tgsModeChanegDTO = new KnPoCSubsTGSModeChanegDTO();
            tgsModeChanegDTO.setMdn(sehNotifyObj.getTgsModeNotifyDTO().getMdn());
         // GG Check for registeredHome
            String registeredHome=KnXcapDiffNotifier.getRegisterPOCHomeByMDN(sehNotifyObj.getTgsModeNotifyDTO().getMdn());
            knLogger.debug(methodName, "registeredHome :", registeredHome);
            if(registeredHome!=null&& !registeredHome.isEmpty())
            {
            	sehNotifyObj.getTgsModeNotifyDTO().setPocHome(registeredHome);
            	sehNotifyObj.getTgsModeNotifyDTO().setPresenceHome(registeredHome);
            }
            tgsModeChanegDTO.setPresenceHome(sehNotifyObj.getTgsModeNotifyDTO().getPresenceHome());
            tgsModeChanegDTO.setPocServerHome(sehNotifyObj.getTgsModeNotifyDTO().getPocHome());
            tgsModeChanegDTO.setTsgMode(sehNotifyObj.getTgsModeNotifyDTO().getTgsMode());
            requestObj.setRequestData(tgsModeChanegDTO);
            knLogger.debug(methodName, "TLV Request Object - ", requestObj);
            payLoad = tlvDocDiffGenerator.generateTLV(requestObj);
        } catch (Exception e) {
            knLogger.error(methodName, "Sending SEH Notification failed:", e);
        }
        return payLoad;
    }
}
