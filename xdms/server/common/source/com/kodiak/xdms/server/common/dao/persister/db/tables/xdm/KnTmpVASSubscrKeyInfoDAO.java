/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnTmpVASSubscriptionKeyInfoDAO.java
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

package com.kodiak.xdms.server.common.dao.persister.db.tables.xdm;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dao.persister.db.tables.ITableDAO;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnDAOSourceTypes;
import com.kodiak.logger.KnLogger;

import java.sql.*;
import java.util.Collection;

public class KnTmpVASSubscrKeyInfoDAO implements ITableDAO {
	private static final KnLogger knLogger = KnLogger.getLogger(KnTmpVASSubscrKeyInfoDAO.class);

    public String pttServerId = null;

    // Table related constants
    private static final String TABLENAME = "DG.TMPVASSUBSCRIPTIONKEYINFO";
    private static final String MDN = "MDN";
    private static final String SUBSCRIPTIONKEY = "SUBSCRIPTIONKEY";
    private static final String EXPIRYTIME = "ExpiryTime";
    private static final String SERVICENAME = "ServiceName";

    private static final String QRY_SEL_BY_MDN = "SELECT SUBSCRIPTIONKEY FROM DG.TMPVASSUBSCRIPTIONKEYINFO WHERE MDN=?";
    
    private static final String QRY_DELETE_ACT_CODE = "DELETE FROM DG.TMPVASSUBSCRIPTIONKEYINFO WHERE MDN=?";


    public KnTmpVASSubscrKeyInfoDAO(String pttserverId) {
        this.pttServerId = pttserverId;
    }

    public void insert(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "insert", "Not Implemented");
    }

    public void update(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "update", "Not Implemented");
    }

    public void delete(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "delete", "Not Implemented");
    }

    public Collection select(IPersistenceDTO persistenceDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        knLogger.warn( "select", "Not Implemented");
        return null;
    }
  
  public String retrieveActivationCode(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
      final String methodName = "retrieveActivationCode(String, KnPersisterTxn)";
      Connection conn;
      PreparedStatement pStatement = null;
      ResultSet rs = null;
      String activationCode = null;
      knLogger.entry(methodName, "mdn-", KnGDPRTemplate.mdn(mdn));
      try {
    	  
          conn = persisterTxn.getDBConnection(pttServerId, true);
          pStatement = conn.prepareStatement(QRY_SEL_BY_MDN);
          pStatement.setString(1, mdn);

          knLogger.debug(methodName, "QUERY : Executing ");
          rs = pStatement.executeQuery();
          knLogger.debug( methodName, "QUERY : Completed.");

          if (rs.next()) {
        	  activationCode = rs.getString(SUBSCRIPTIONKEY);
          }
          
      } catch (SQLException e) {
          knLogger.error( methodName, "SQL Exception - ", e);
          throw KnDbUtil.processException(e, "Failed to - " + e.getMessage(), pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, QRY_SEL_BY_MDN);
          
      } finally {
          KnDbUtil.closeResultSet(rs);
          KnDbUtil.closePreparedStatement(pStatement);
      }
      
      knLogger.exit(methodName, "Activation code-", activationCode);
      return activationCode;
  }
  
  public void removeActivationCode(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
	  final String methodName = "removeActivationCode(String, KnPersisterTxn)";
	  Connection conn;
	  PreparedStatement pStatement = null;
	  int res;
	  knLogger.entry(methodName, "mdn-", KnGDPRTemplate.mdn(mdn));
	  try {
		  
		  conn = persisterTxn.getDBConnection(pttServerId, true);
		  pStatement = conn.prepareStatement(QRY_DELETE_ACT_CODE);
		  pStatement.setString(1, mdn);
		  
		  knLogger.debug(methodName, "QUERY : Executing ");
		  res = pStatement.executeUpdate();
		  knLogger.debug( methodName, "QUERY : Completed.");
		  
	  } catch (SQLException e) {
		  knLogger.error( methodName, "SQL Exception - ", e);
		  throw KnDbUtil.processException(e, "Failed to - " + e.getMessage(), pttServerId, KnDAOSourceTypes.TMPVASSUBSCRIPTIONKEYINFO, QRY_DELETE_ACT_CODE);
	  } finally {
          KnDbUtil.closePreparedStatement(pStatement);
      }
	  
	  knLogger.exit(methodName,res);
  }
      

}