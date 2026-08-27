/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dao.persister;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnLicensePackDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnLicenseSubDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpXdmLicenseDAO.java
 * Subsystem:  PoC
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     09/10/2014    7.10
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

public interface ICorpXdmLicenseDAO {

    public List<KnLicensePackDTO> getLicenseProfileList(int corpId, String corpName, boolean readOnly
            , KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<KnLicenseSubDTO> getLicenseSubscriber(int pamAccId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public KnCorpSubscriberDTO getPamAccId(String billingNumber, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException;

    public List<String> getMarkForDeletionPseudoMdns(int pamAccId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void  updateUnMarkList(Collection<String> unMarklist,int pamAccId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateMarkList(Collection<String> marklist, int pamAccId,String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public List<Integer> getAllPamAccountIdforCorp(int corpId, String xdmsHomePttId, boolean readonly, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public long updatePamAccountEtag(int pamAccId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnDAOException;
    
    public void updateBillingName(String billingMDN, String billingName, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnDAOException;

    public KnCorpSubscriberDTO getPamAccountId(String billingNumber, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Map<Integer, String> getBillingNumber(Set<Integer> pamAccId, KnPersisterTxn persisterTxn) throws KnDAOException;
    
}
