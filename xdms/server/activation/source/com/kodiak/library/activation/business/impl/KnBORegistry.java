/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBORegistry.java
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
package com.kodiak.library.activation.business.impl;

import com.kodiak.library.activation.business.IClientManagementController;
import com.kodiak.xdms.server.common.framework.KnFWException;

/**
 * Factory for business objects. All the business object initialization,
 * caching etc. will be done inside this class.
 */
public class KnBORegistry {

    /**
     * This is the factory method that returns an instance of KnActivationManagementController
     * class. This class is reponsible for implementing all the business logics performing
     * different operations on client provisioning.
     *
     * @return returns client management controller
     * @throws KnFWException throws framework exception
     */
    public static IClientManagementController createClientManagementController() throws KnFWException {
        return new KnActivationManagementController();
    }
}
