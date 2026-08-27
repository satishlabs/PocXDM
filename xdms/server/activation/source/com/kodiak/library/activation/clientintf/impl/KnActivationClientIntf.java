/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 /**
 * ************************************************************************
 * <p/>
 * File name:  KnActivationClientIntf.java
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
package com.kodiak.library.activation.clientintf.impl;

import com.kodiak.common.commdto.response.KnRadioDeviceActResponseDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.library.activation.dto.clientdat.KnIPRadioDeviceClientDTO;
import com.kodiak.library.activation.dto.common.KnDeviceInfoDTO;
import com.kodiak.common.commdto.response.KnXDMActivateRespDTO;
import com.kodiak.library.activation.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.library.activation.KnActException;
import com.kodiak.library.activation.clientintf.IActClientManager;
import com.kodiak.library.activation.clientintf.IActivationClientIntf;
import com.kodiak.library.activation.dto.clientdat.KnIPClientRegistryDTO;
import com.kodiak.library.activation.resources.KnConstants;
import com.kodiak.library.activation.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.KnXDMSystemError;
import com.kodiak.xdms.server.common.business.helper.KnProfileInfoUtil;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.framework.KnFWException;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.PUBLIC_PROFILE;

import java.util.Calendar;

/**
 * This class is the interface to the Activation Client Management library. This class has a
 * super set of methods from all the client interfaces. This class will deligate
 * the method calls to corresponding implementations.  <BR>To access on-net detection
 * library get a reference of this interface which in turn responsible for initializing
 * all the relevant resources.
 * <BR>The client can have an access to all the APIs through a direct reference of API with this interface or
 * by accessing individual client interfaces which is been segragated as per the different client functionalities.
 * To get an access to individual client interfaces, use the getter methods for different manager classes which in
 * turn provides the reference of APIs corresponds to that particular client interface.
 */
public class KnActivationClientIntf implements IActivationClientIntf {
	private static final KnLogger knLogger = KnLogger.getLogger(KnActivationClientIntf.class);

    private static String className = KnActivationClientIntf.class.getName();
    private IActClientManager clientManager;

    /**
     * initialize the Logger and configuration manager.
     */
    private static boolean isInitialized = false;
    private static Exception exception;

    //System property name that needs to be set at start-up
    //This is the file-name (full path) that contains the information
    //required for initializing the logger
    private static String LOG_INIT_FILE = "log4jInitFile";

    static {
        try {
            //Initialize the Logger first, as other initialization
            //information will be written to logs
            //NB: The Logger initialization is handled as a special
            //case. All other initialization is done by the Initializer
            //module within the configuration manager
            KnLogger.init();

            //Now, initialize the configuration manager
            knLogger.debug( "static", "Initializing config manager!");
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance(KnConstants.LIBRARY_NAME);
            knLogger.debug( "static", "Completed Initializing config manager!");

            isInitialized = configManager.isInitialized();
            KnProfileInfoUtil.initialize(PUBLIC_PROFILE, 400, 600);
            KnProfileInfoUtil.getInstance(PUBLIC_PROFILE);

        } catch (Exception e) {
            knLogger.fatal( "static",
                    "Initialization failed : " + e);
            exception = e;
        }
    }//end of static block//

    /**
     * Constructor. All the clients will invoke this constructor.
     * This constructor may throw a KnSystemError if the initialization
     * of Configuration Manager is not done properly.
     */
    public KnActivationClientIntf() {
        if (!isInitialized) {
            throw new KnXDMSystemError(KnErrorCodes.Initializer.CA_INIT_FAILED, "Library Initalization Failed - " + exception,
                    exception);
        }
    }

    /**
     * This method returns the singleton instance of the KnActClientManager class.
     * To get an access only to the IActClientManager APIs, the client can use this getter which in turn
     * gives the reference to the IActClientManager interface.
     * <p/>
     * <BR>The client can have an access to the same IActClientManager APIs by directly refering to the
     * IActivationClientIntf interface also.
     *
     * @return an instance of KnActClientManager
     * @throws KnFWException throws framework exception
     */
    public IActClientManager getActClientManager() throws KnFWException {
        if (this.clientManager == null) {
            this.clientManager = new KnActClientManager();
        }
        return this.clientManager;
    }

    /**
     * this method register group connected client
     *
     * @param clientRegistryDTO client registry dto object
     * @return returns KnXDMActivateRespDTO object
     * @throws com.kodiak.library.activation.KnActException
     *                       throws business exception
     * @throws KnFWException throws authorization / business validation exception
     */
	/*
	 * public KnXDMActivateRespDTO registerClient(KnIPClientRegistryDTO
	 * clientRegistryDTO) throws KnActException, KnFWException { String methodName =
	 * "registerClient(KnIPClientRegistryDTO)"; String uniqueId =
	 * generateUniqueId("registerClient"); knLogger.debug( methodName,
	 * "Generated unique id : " + uniqueId); return
	 * getActClientManager().registerClient(clientRegistryDTO); }
	 */

//    /**
//     * this method
//     *
//     * @throws com.kodiak.library.activation.KnActException throws business exception
//     * @throws KnFWException  throws authorization / business validation exception
//     */
//    public Map<String, KnDialPlanConfigDTO> getDialPlanConfig() throws KnActException, KnFWException {
//        return getActClientManager().getDialPlanConfig();
//    }

    /**
     * This method is used to generate a unique Id based on the action performed, Time stamp and ThreadId.
     * This unique id is for use in setting an unique id for the current request.
     *
     * @param action string
     * @return return unique-id string
     */
    private String generateUniqueId(String action) {
        StringBuffer buffer = new StringBuffer(100);
        buffer.append(action).append("-")
                .append(Calendar.getInstance().getTimeInMillis()).append("-")
                .append(Thread.currentThread().getId());
        return buffer.toString();
    }
    
    @Override
	public String retrieveMDN(KnIPClientRegistryDTO ipClientRegistryDTO) throws KnActException, KnFWException {
		return getActClientManager().retrieveMDN(ipClientRegistryDTO);
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
        return getActClientManager().deviceActivation(ipClientRegistryDTO);
    }

    @Override
    public KnRadioDeviceActResponseDTO radioDeviceActivation(KnIPRadioDeviceClientDTO ipRadioDeviceClientDTO) throws KnActException, KnFWException {
        return getActClientManager().radioDeviceActivation(ipRadioDeviceClientDTO);
    }

    @Override
    public void updateDeviceInfo(String mdn, String clientPassword, KnPersisterTxn persisterTxn) throws KnActException, KnFWException {
        getActClientManager().updateDeviceInfo(mdn, clientPassword, persisterTxn);
    }

    @Override
    public KnDeviceInfoPersistDTO getDeviceInfo(String mdn, KnPersisterTxn persisterTxn) throws KnActException, KnFWException {
        return getActClientManager().getDeviceInfo(mdn, persisterTxn);
    }
}
