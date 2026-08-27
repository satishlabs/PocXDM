/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   KnActClientManager.java
 * Subsystem:   Activation Library
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         05/10/2010  6.2
 *
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 ***************************************************************************/
package com.kodiak.library.activation.clientintf.impl;

import com.kodiak.common.commdto.response.KnRadioDeviceActResponseDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.library.activation.clientintf.IActClientManager;
//import com.kodiak.xdms.server.common.dto.common.KnDialPlanConfigDTO;
import com.kodiak.library.activation.dto.clientdat.KnIPRadioDeviceClientDTO;
import com.kodiak.library.activation.dto.common.KnDeviceInfoDTO;
import com.kodiak.library.activation.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.library.activation.KnActException;
import com.kodiak.library.activation.business.IClientManagementController;
import com.kodiak.library.activation.business.impl.KnBORegistry;
import com.kodiak.library.activation.dto.clientdat.KnIPClientRegistryDTO;
import com.kodiak.library.activation.resources.KnEntityTypes;
import com.kodiak.library.activation.resources.KnOperationTypes;
import com.kodiak.library.activation.resources.KnProfileTypes;
import com.kodiak.library.activation.resources.KnConstants;
import com.kodiak.common.commdto.response.KnXDMActivateRespDTO;

/**
 * This class gets the instance of the KnActivationManagementController which defines the business implementation
 * methods of all the Activation client provision related activities. The KnActivationClientIntf class must
 * get an instance of this class in order to access the business methods defined in the KnActivationManagementController
 * class.
 * <BR>The methods of this class also set the entity and operation mapping settings into the corresponding input DTOs
 * which is used for authentication and validation.
 */
public class KnActClientManager implements IActClientManager {

    private IClientManagementController clientMgmtController;

    /**
     * @throws KnFWException throws framework initialization exception
     */
    KnActClientManager() throws KnFWException {
        clientMgmtController = KnBORegistry.createClientManagementController();
    }

    /**
     * this method register group connected client
     *
     * @param clientRegistryDTO client registry dto object
     * @return returns KnXDMActivateRespDTO object
     * @throws com.kodiak.library.activation.KnActException throws business exception
     * @throws KnFWException  throws authorization / business validation exception
     */
	/*
	 * public KnXDMActivateRespDTO registerClient(KnIPClientRegistryDTO
	 * clientRegistryDTO) throws KnActException, KnFWException {
	 * clientRegistryDTO.setEntityId(KnEntityTypes.CLIENT_MANAGER);
	 * clientRegistryDTO.setOperationType(KnOperationTypes.REGISTER_CLIENT);
	 * clientRegistryDTO.setFeatureId(KnConstants.ACT_OPERATION_TYPE);
	 * clientRegistryDTO.setProfile(KnProfileTypes.WEBSERVICE_PROFILE); return
	 * clientMgmtController.registerClient(clientRegistryDTO); }
	 */
    
    @Override
   	public String retrieveMDN(KnIPClientRegistryDTO ipClientRegistryDTO) throws KnActException, KnFWException {
       	ipClientRegistryDTO.setEntityId(KnEntityTypes.CLIENT_MANAGER);
       	ipClientRegistryDTO.setOperationType(KnOperationTypes.RETRIEVE_MDN);
       	ipClientRegistryDTO.setFeatureId(KnConstants.ACT_OPERATION_TYPE);
       	ipClientRegistryDTO.setProfile(KnProfileTypes.WEBSERVICE_PROFILE);
   		return clientMgmtController.retrieveMDN(ipClientRegistryDTO);
   	}

    /**
     * Interface for device activation
     *
     * @param ipClientRegistryDTO
     * @return
     * @throws KnActException
     */
    @Override
    public KnDeviceInfoDTO deviceActivation(KnIPClientRegistryDTO ipClientRegistryDTO) throws KnActException, KnFWException {
        ipClientRegistryDTO.setEntityId(KnEntityTypes.CLIENT_MANAGER);
        ipClientRegistryDTO.setOperationType(KnOperationTypes.DEVICE_ACTIVATION);
        ipClientRegistryDTO.setFeatureId(KnConstants.DEVICE_ACTIVATION_OPERATION_TYPE);
        ipClientRegistryDTO.setProfile(KnProfileTypes.WEBSERVICE_PROFILE);
        return clientMgmtController.deviceActivation(ipClientRegistryDTO);
    }

    /**
     * Interface for Radio Device activation
     *
     * @param ipRadioDeviceClientDTO
     * @return
     * @throws KnActException
     */
    @Override
    public KnRadioDeviceActResponseDTO radioDeviceActivation(KnIPRadioDeviceClientDTO ipRadioDeviceClientDTO) throws KnActException, KnFWException {
        ipRadioDeviceClientDTO.setEntityId(KnEntityTypes.CLIENT_MANAGER);
        ipRadioDeviceClientDTO.setOperationType(KnOperationTypes.RADIO_DEVICE_ACTIVATION);
        ipRadioDeviceClientDTO.setFeatureId(KnConstants.RADIO_DEVICE_ACTIVATION);
        ipRadioDeviceClientDTO.setProfile(KnProfileTypes.WEBSERVICE_PROFILE);
        return clientMgmtController.radioDeviceActivation(ipRadioDeviceClientDTO);
    }


    @Override
    public void updateDeviceInfo(String mdn, String clientPassword, KnPersisterTxn persisterTxn) throws KnActException, KnFWException {
        clientMgmtController.updateDeviceInfo(mdn, clientPassword, persisterTxn);
    }

    @Override
    public KnDeviceInfoPersistDTO getDeviceInfo(String mdn, KnPersisterTxn persisterTxn) throws KnActException, KnFWException {
        return clientMgmtController.getDeviceInfo(mdn, persisterTxn);
    }


//    /**
//     * this method
//     *
//     * @throws com.kodiak.library.activation.KnActException throws business exception
//     * @throws KnFWException  throws authorization / business validation exception
//     */
//    public Map<String, KnDialPlanConfigDTO> getDialPlanConfig() throws KnActException, KnFWException {
//        return clientMgmtController.getDialPlanConfig();
//    }

}