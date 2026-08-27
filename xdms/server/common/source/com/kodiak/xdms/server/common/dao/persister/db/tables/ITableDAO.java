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
 * Ravi Shanker .P       Dec 16, 2010       7.0
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
package com.kodiak.xdms.server.common.dao.persister.db.tables;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.util.Collection;

public interface ITableDAO {

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn)
            throws KnDAOException;

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn)
            throws KnDAOException;

    public void delete(IPersistenceDTO persistencDTO, KnPersisterTxn persistTxn)
            throws KnDAOException;

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persistTxn)
            throws KnDAOException;

}
