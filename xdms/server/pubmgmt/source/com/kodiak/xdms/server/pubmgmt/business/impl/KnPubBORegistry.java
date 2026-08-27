/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.impl;

import com.kodiak.xdms.server.pubmgmt.business.*;
import com.kodiak.xdms.server.common.framework.KnFWException;


/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubBORegistry.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 11, 2011           7.0
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
public class KnPubBORegistry {

    /**
     * This is the factory method that returns an instance of KnCLGroupInfoController
     * class. This class is reponsible for implementing all the business logics performing
     * different operations on Groups.
     *
     * @return
     */
    public static IPubGroupInfoController createPubGroupInfoController() {//throws KnFWException {
        return new KnPubGroupInfoController();
    }

    /**
     * This is the factory method that returns an instance of KnPubContactInfoController
     * class. This class is reponsible for implementing all the business logics performing
     * different operations on Public contact Info.
     *
     * @return
     */
    public static IPubContactInfoController createPubContactInfoController() {//throws KnFWException {
        return new KnPubContactInfoController();
    }

    /**
     * This is the factory method that returns an instance of KnPubInfoController
     * class. This class is reponsible for implementing all the business logic performing
     * different operations on Public Info.
     *
     * @return
     */
    public static IPubInfoController createPubInfoController() {//throws KnFWException {
        return new KnPubInfoController();
    }

    public static IPubAuthInfoController createPubAuthInfoController() {//throws KnFWException {
        return new KnPubAuthInfoController();
    }

    public static ITGSSController createTGSSController() { return new KnTGSSController(); }
	
	 public static IPubMCPTTController createMCPTTUEConfig() { return new KnPubMCPTTController(); }

    public static IPubMCPTTController createMCPTTUserProfile() { return new KnPubMCPTTController(); }

    public static IPubMCPTTController createMCPTTServiceConfig() { return new KnPubMCPTTController(); }

    public static IPubMCDATAController createMCDATAController() { return new KnPubMCDATAController(); }

    public static IPubMCVideoController createMCVideoController() { return new KnPubMCVideoController(); }

    public static IPubMCSGroupController createMCSGroupController() { return new KnPubMCSGroupController(); }

    public static IPubMCSUserDirController createMCSUserDirController(){ return new KnPubMCUserDirController();}

}
