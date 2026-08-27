/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBORegistry.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 11, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.business.impl;


import com.kodiak.xdms.server.corpmgmt.business.*;

public class KnCorpBORegistry {

    /**
     * This is the factory method that returns an instance of KnCorpAuthInfoController
     * class. This class is reponsible for implementing all the business logics performing
     * different operations on Corporate authentication.
     *
     * @return an object of KnCorpAuthInfoController class
     */
    public static ICorpGenericInfoController createCorpAuthInfoController() {
        return new KnCorpGenericInfoController();
    }

    /**
     * This is the factory method that returns an instance of KnCorpContactInfoController
     * class. This class is reponsible for implementing all the business logics performing
     * different operations on Corporate contacts.
     *
     * @return an object of KnCorpContactInfoController class
     */
    public static ICorpContactInfoController createCorpContactInfoController() {
        return new KnCorpContactInfoController();
    }

    public static ICorpContactCloningController createCorpContactCloningController() {
        return new KnCorpContactGroupFeatureCloningController();
    }

    /**
     * This is the factory method that returns an instance of KnCorpGroupInfoController
     * class. This class is reponsible for implementing all the business logics performing
     * different operations on Corporate groups.
     *
     * @return an object of KnCorpGroupInfoController class
     */
    public static ICorpGroupInfoController createCorpGroupInfoController() {
        return new KnCorpGroupInfoController();
    }

    /**
     * This is the factory method that returns an instance of KnCorpSublistInfoController
     * class. This class is reponsible for implementing all the business logics performing
     * different operations on Corporate Sublists.
     *
     * @return an object of KnCorpSublistInfoController class
     */
    public static ICorpSublistInfoController createCorpSublistInfoController() {
        return new KnCorpSublistInfoController();
    }

    /**
     * This is the factory method that returns an instance of KnCorpSublistInfoController
     * class. This class is reponsible for implementing all the business logics performing
     * different operations on Corporate Sublists.
     *
     * @return an object of KnCorpSublistInfoController class
     */
    public static ICorpActivationInfoController createCorpActivationInfoController() {
        return new KnCorpActivationController();
    }

    /**
     * This is the factory method that returns an instance of KnCorpAuthInfoController
     * class. This class is reponsible for implementing all the business logics performing
     * different operations on Corporate license authentication.
     *
     * @return an object of KnCorpAuthInfoController class
     */
    public static ICorpLicenseInfoController createCorpLicenseInfoController() {
        return new KnCorpLicenseInfoController();
    }

    public static ICorpSubscrProfileController createCorpSubscrProfileController() {
        return new KnCorpSubscrProfileController();
    }

    public static ICorpOSMController createCorpOSMController() {
        return new KnCorpOSMController();
    }

    public static ICorpUserProfileController createCorpUserProfileController() {
        return new KnCorpUserProfileController();
    }

    public static ICorpGroupProfileController createCorpGroupProfileController() {
        return new KnCorpGroupProfileController();
    }

    public static ICorpDeviceController createCorpDeviceController() {
        return new KnCorpDeviceController();
    }

    public static ICorpStatsController createCorpStatsController() {
        return new KnCorpStatsController();
    }

    public static ICorpAssignUserProfileController createAssignUserprofileController() {
        return new KnCorpAssignUserProfileController();
    }


    public static ICorpHierarchyController  createCorpHierarchyController() {
        return new KnCorpHierarchyController();
    }

    public static ICorpPTTSettingController createCorpPTTSettingController() {
        return new KnCorpPTTSettingController();
    }


}