/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpXdmActivationDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Acharyya        30-11-2011      7.2
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
package com.kodiak.xdms.server.corpmgmt.dao.persister;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ICorpXdmActivationDAO {

    public void insetActivationCode(String mdn, String activationCode, Timestamp
            expiryTime, KnPersisterTxn persisterTxn, int clientType, Timestamp currentTimeInUTC) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> insertActivationCode(Collection<KnCorpSubscriberDTO> subsList,String serviceName, KnPersisterTxn persisterTxn) throws KnDAOException;

    public boolean isActivationCodeExist(String mdn, KnPersisterTxn persisterTxn, int clientType) throws KnDAOException;

    public Set<String> isActivationCodeExistInDB(Set<String> activationCode, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> isActivationCodeExistForMDN(Collection<KnCorpSubscriberDTO> subsList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateActivationCode(String mdn, String activationCode, Timestamp
            expiryTime, KnPersisterTxn persisterTxn, int clientType, Timestamp currentTimeInUTC) throws KnDAOException;

    public Collection<KnCorpSubscriberDTO> updateActivationCode(Collection<KnCorpSubscriberDTO> subsList, KnPersisterTxn persisterTxn) throws KnDAOException;


    public void updateSubsAuthStatusToProvisioningStat(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void invalidatePocUserPassword(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpSubscriberDTO getActCodeExtTime(String mdn, int clientType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<String> activationCodeExistForMDNs(Collection<String> mdnList,String serviceName, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteActivationCode(Collection<String> mdns, String serviceName, KnPersisterTxn persisterTxn) throws KnDAOException;

    public String getSubscribersOTP(String mdn, String serviceName, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteActivationCodeForMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteActivationCodeForMDN(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException;

}
