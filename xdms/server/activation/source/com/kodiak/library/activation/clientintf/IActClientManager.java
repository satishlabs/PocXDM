/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  IActClientManager.java
 * Subsystem:  Activation Library
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Rashmi Kamat         29-Oct-2010       6.4
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
package com.kodiak.library.activation.clientintf;

import com.kodiak.common.commdto.response.KnRadioDeviceActResponseDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.library.activation.KnActException;
//import com.kodiak.xdms.server.common.dto.common.KnAuthInfoDTO;
//import com.kodiak.xdms.server.common.dto.common.KnDialPlanConfigDTO;
import com.kodiak.library.activation.dto.clientdat.KnIPRadioDeviceClientDTO;
import com.kodiak.library.activation.dto.common.KnDeviceInfoDTO;
import com.kodiak.library.activation.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.library.activation.dto.clientdat.KnIPClientRegistryDTO;
import com.kodiak.common.commdto.response.KnXDMActivateRespDTO;

/**
 * IActClientManager Interface - this interface declares all the methods exposed to the
 * client using which the clients can register / deregister group connected client.
 * All the methods declared in this interface throws KnActException
 */
public interface IActClientManager {

    /**
     * this method register group connected client
     *
     * @param clientRegistryDTO client registry dto object
     * @return returns KnXDMActivateRespDTO object
     * @throws com.kodiak.library.activation.KnActException throws business exception
     * @throws KnFWException  throws authorization / business validation exception
     */
    //public KnXDMActivateRespDTO registerClient(KnIPClientRegistryDTO clientRegistryDTO) throws KnActException, KnFWException;

//    /**
//     * this method
//     *
//     * @throws com.kodiak.library.activation.KnActException throws business exception
//     * @throws KnFWException  throws authorization / business validation exception
//     */
//    public Map<String, KnDialPlanConfigDTO> getDialPlanConfig() throws KnActException, KnFWException;
    
    public String retrieveMDN(KnIPClientRegistryDTO ipClientRegistryDTO)  throws KnActException, KnFWException ;

    /**
     * Interface for device activation
     * @param ipClientRegistryDTO
     * @return
     * @throws KnActException
     */
    KnDeviceInfoDTO deviceActivation(KnIPClientRegistryDTO ipClientRegistryDTO) throws KnActException, KnFWException;

    KnRadioDeviceActResponseDTO radioDeviceActivation(KnIPRadioDeviceClientDTO ipRadioDeviceClientDTO) throws KnActException, KnFWException;

    void updateDeviceInfo(String mdn, String clientPassword, KnPersisterTxn persisterTxn) throws KnActException, KnFWException;

    KnDeviceInfoPersistDTO getDeviceInfo(String mdn, KnPersisterTxn persisterTxn) throws KnActException, KnFWException;

}
