/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  IClientManagementController.java
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
package com.kodiak.library.activation.business;

import com.kodiak.common.commdto.response.KnRadioDeviceActResponseDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.library.activation.dto.clientdat.KnIPClientRegistryDTO;
import com.kodiak.library.activation.dto.clientdat.KnIPRadioDeviceClientDTO;
import com.kodiak.library.activation.dto.common.KnDeviceInfoDTO;
import com.kodiak.library.activation.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASException;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
//import com.kodiak.xdms.server.common.dto.common.KnAuthInfoDTO;
//import com.kodiak.xdms.server.common.dto.common.KnDialPlanConfigDTO;
import com.kodiak.common.commdto.response.KnXDMActivateRespDTO;

//import com.kodiak.xdms.server.common.dto.common.KnAuthInfoDTO;
//import com.kodiak.xdms.server.common.dto.common.KnDialPlanConfigDTO;


/**
 * The client management library supports multiple opertions to provision group connected
 * client irrespective of the client type. The entity supports register/deregister/forceSync
 * operations to the group connected client.
 */
public interface IClientManagementController {

    /**
     * this method register group connected client
     *
     * @param clientRegistryDTO client registry dto
     * @return returns KnXDMActivateRespDTO object
     * @throws KnActBOException throws bo exception
     * @throws KnAASException        throws authorization exception
     * @throws KnValidationException throws business validation exception
     */
    //public KnXDMActivateRespDTO registerClient(KnIPClientRegistryDTO clientRegistryDTO) throws KnActBOException, KnAASException, KnValidationException;

//    /**
//     * this method
//     *
//     * @throws KnActBOException throws bo exeption
//     * @throws KnValidationException throws business validation exception
//     */
//    public Map<String, KnDialPlanConfigDTO> getDialPlanConfig() throws KnActBOException, KnValidationException;
    
    public String retrieveMDN(KnIPClientRegistryDTO ipClientRegistryDTO) throws KnActBOException, KnAASException;

    KnDeviceInfoDTO deviceActivation(KnIPClientRegistryDTO ipClientRegistryDTO) throws KnActBOException, KnAASException;

    KnRadioDeviceActResponseDTO radioDeviceActivation(KnIPRadioDeviceClientDTO ipRadioDeviceClientDTO) throws KnActBOException, KnAASException;

    void updateDeviceInfo(String mdn, String clientPassword, KnPersisterTxn persisterTxn) throws KnActBOException;

    KnDeviceInfoPersistDTO getDeviceInfo(String mdn, KnPersisterTxn persisterTxn) throws KnActBOException;
}
