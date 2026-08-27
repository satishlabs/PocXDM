/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/25/10       7.0
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
 * *******************************************************************************
 */

package com.kodiak.xdms.server.subsmgmt.business;

import com.kodiak.xdms.server.subsmgmt.business.impl.KnBulkSubsProvController;
import com.kodiak.xdms.server.subsmgmt.business.impl.KnPAMAccController;
import com.kodiak.xdms.server.subsmgmt.business.impl.KnSubsProvController;

public class KnProvBORegistry {

    /**
     * This is a factory method that returns an instance of KnSubsProvController
     * class. This class is responsible for implementing all the business logic's performing
     * different operations on Subscriber Profile management
     *
     * @return ans instance of KnSubsProvController
     */
    public static ISubsProvController createSubsProvController() {
        return new KnSubsProvController();
    }

    /**
     * This is a factory method that returns an instance of KnPAMAccController
     * class. This class is responsible for implementing all the business logic's performing
     * different operations on PAM account management
     *
     * @return ans instance of KnPAMAccController
     */
    public static IPAMAccController createPAMAccController() {
        return new KnPAMAccController();
    }
    /**
     * This is a factory method that returns an instance of KnBulkSubsProvController
     * class. This class is responsible for implementing all the business logic's performing
     * different operations on Bulk subscriber management
     *
     * @return ans instance of KnBulkSubsProvController
     */
    public static IBulkSubsProvController createBulkSubsProvController() {
        return new KnBulkSubsProvController();
}
}
