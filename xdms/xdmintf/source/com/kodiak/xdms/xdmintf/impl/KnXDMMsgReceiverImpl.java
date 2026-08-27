/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMMsgReceiverImpl.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Harsha             Dec 29, 2010  7.0
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

package com.kodiak.xdms.xdmintf.impl;


import com.kodiak.logger.KnLogger;
import com.kodiak.frameworks.messaging.callbackHandler.IMessageCallback;
import com.kodiak.frameworks.messaging.common.KnMessageException;
import com.kodiak.frameworks.messaging.jmsclient.intf.impl.KnJmsMessagingClientImpl;
import com.kodiak.frameworks.messaging.jmsclient.intf.IJmsMessagingClientIntf;
import com.kodiak.common.commdto.common.KnMessageConstants;
import com.kodiak.xdms.xdmintf.IXDMMsgReceiverIntf;
import com.kodiak.xdms.xdmintf.receiver.KnMessageCallbackReceiver;
import com.kodiak.xdms.xdmintf.resources.KnXDMIntfConstants;
import com.kodiak.utilities.statusmgr.IStatusMgrNotifyIntf;
import com.kodiak.utilities.statusmgr.KnStatusManagerClient;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class KnXDMMsgReceiverImpl implements IXDMMsgReceiverIntf {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMMsgReceiverImpl.class);

    Collection<IMessageCallback> callbackObjs = new ArrayList<IMessageCallback>();
    IJmsMessagingClientIntf instance = null;

    public KnXDMMsgReceiverImpl() {
        initialize();
    }


    public final boolean initialize() {
        try {
//            KnJmsMessagingClientImpl.initialize(new ArrayList<String>(), KnMessageConstants.CARD_TYPE_XDMSERVER);
            instance = KnJmsMessagingClientImpl.getInstance();
            registerForCallbacks(fetchServices());
        } catch (Exception e) {
            knLogger.error( "intializer", "failed to initialize");
        }
        return true;
    }

    public final void registerForCallbacks(Collection<Integer> features) throws KnMessageException {

        for (Integer feature : features) {
            //todo need to change the Feature id to QueueNames
            KnMessageCallbackReceiver callbackObj = new KnMessageCallbackReceiver(Integer.toString(feature));
            callbackObjs.add(callbackObj);
            List<IStatusMgrNotifyIntf> list = new ArrayList<IStatusMgrNotifyIntf>();
            list.add(callbackObj);
            knLogger.info( "registerForCallbacks", "Register to Status Mgr for object ", feature);
            KnStatusManagerClient.registerObjects(list);
        }

        if (!callbackObjs.isEmpty()) {
            instance.callbackRequest(callbackObjs, KnMessageConstants.CARD_TYPE_XDMSERVER);
        }
    }

    private Collection<Integer> fetchServices() {
        // TODO fetch feature IDs installed from DB

        Collection<Integer> features = new ArrayList<Integer>();
        features.add(Integer.valueOf(KnXDMIntfConstants.FEATURE_ID_PROV));
        features.add(Integer.valueOf(KnXDMIntfConstants.FEATURE_ID_PROV_POST));
        features.add(Integer.valueOf(KnXDMIntfConstants.FEATURE_ID_PUB));
        features.add(Integer.valueOf(KnXDMIntfConstants.FEATURE_ID_CORP));
        features.add(Integer.valueOf(KnXDMIntfConstants.FEATURE_ID_CORP_POST));
        features.add(Integer.valueOf(KnXDMIntfConstants.FEATURE_ID_XDM_DATA_INTF));
        features.add(Integer.valueOf(KnXDMIntfConstants.FEATURE_ID_DROPPED_REQUEST));
        features.add(Integer.valueOf(KnXDMIntfConstants.FEATURE_ID_LOGIN_NOTIFY_EVENTS));
        features.add(Integer.valueOf(KnXDMIntfConstants.FEATURE_ID_LI_NOTIFY_EVENTS));
        return features;
    }

}
