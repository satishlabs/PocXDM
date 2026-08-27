/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf.impl;

import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCSXCAPRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.pubmgmt.business.IPubMCPTTController;
import com.kodiak.xdms.server.pubmgmt.business.impl.KnPubBORegistry;
import com.kodiak.xdms.server.pubmgmt.clientintf.IPubMCPTTManager;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnOperationTypes;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubMCPTTManager.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Puneet Singhania       June 27,2019       9.1.1
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

public class KnPubMCPTTManager implements IPubMCPTTManager {

    /**
     *
     */
    IPubMCPTTController iPubMCPTTController;


    /**
     *
     */
    KnPubMCPTTManager() {//throws KnFWException {
        iPubMCPTTController = KnPubBORegistry.createMCPTTUEConfig();
    }



    /**
     *
     * @param ipmcsdto
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnMCSXCAPRespDTO getMCPTTUEConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException{//, KnFWException {

        ipmcsdto.setEntityId(KnEntityTypes.MCPTT_MANAGER);
        ipmcsdto.setOperationType(KnOperationTypes.GET_MCPTT_UE_CONFIG);
        ipmcsdto.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return iPubMCPTTController.getMCPTTUEConfig(ipmcsdto,  persisterTxn);
    }

    public KnMCSXCAPRespDTO getMCPTTUserProfile(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException{//, KnFWException {

        ipmcsdto.setEntityId(KnEntityTypes.MCPTT_MANAGER);
        ipmcsdto.setOperationType(KnOperationTypes.GET_MCPTT_USER_PROFILE);
        ipmcsdto.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return iPubMCPTTController.getMCPTTUserProfile(ipmcsdto,  persisterTxn);
    }

    public KnMCSXCAPRespDTO getMCPTTServiceConfig(KnIPMCSDTO ipmcsdto, KnPersisterTxn persisterTxn) throws KnXDMServerException{//, KnFWException {

        ipmcsdto.setEntityId(KnEntityTypes.MCPTT_MANAGER);
        ipmcsdto.setOperationType(KnOperationTypes.GET_MCPTT_SERVICE_CONFIG);
        ipmcsdto.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return iPubMCPTTController.getMCPTTServiceConfig(ipmcsdto,  persisterTxn);
    }

}
