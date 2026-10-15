/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *************************************************************************
 * File name:   KnDBManagerDAO.java
 * Subsystem:
 * <p/>
 * Name                 Date          Release
 * -----------------    -----------   -------
 * WebApps              29/04/14      7.9
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
 * *************************************************************************
 */
package com.kodiak.dbmgr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kodiak.logger.KnLogger;
import com.kodiak.vault.KnCommonVaultUtil;

import static com.kodiak.dbmgr.KnDBConst.*;


class KnDBManagerDAO {

	private static final KnLogger knLogger = KnLogger.getLogger(KnDBManagerDAO.class);

	private static KnDBManagerDAO dbManagerDAO;

	private KnDBManagerDAO() {

	}

	public synchronized static KnDBManagerDAO getInstance() {
		if (dbManagerDAO == null) {
			dbManagerDAO = new KnDBManagerDAO();
		}
		return dbManagerDAO;
	}

	public KnConnectionDetailDTO getEMSConnDetail() throws Exception {
		KnConnectionDetailDTO connectionDetailDTO = new KnConnectionDetailDTO();
		String methodName = "getEMSConnDetail()";
		knLogger.entry(methodName);
		ResultSet resultSet = null;
		Statement statement = null;
		Connection connection = null;
		try {
			connection = KnDBUtil.getLocalDBConenction();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(KnDBConst.EMS_CONNECTION_DETAIL_SQL);
			knLogger.debug(methodName, "QUERY=" + KnDBConst.EMS_CONNECTION_DETAIL_SQL);
			if (resultSet.next()) {
				connectionDetailDTO.setIpAddress(resultSet.getString(1));
				connectionDetailDTO.setDsn(resultSet.getString(2));
				//connectionDetailDTO.setUserName(resultSet.getString(3));
				//connectionDetailDTO.setPassword(KnEncryptionDecryptionUtil.getInstance().decrypt(KnDBConst.SALT, resultSet.getString(4), KnDBConst.SECRETKEY, KnDBConst.IVSTRING, KnDBConst.KEYSIZE, KnDBConst.ITERATION));
				connectionDetailDTO.setPort(resultSet.getInt(5));
			}
			String dbUserId = KnCommonVaultUtil.getKeyFromVault(T10_USER_NAME_PATH, DBMGR_DBUSERID);
			String dbPassword = KnCommonVaultUtil.getKeyFromVault(T10_PASSWORD_NAME_PATH, DBMGR_DBPASSWORD);
			connectionDetailDTO.setUserName(dbUserId);
			connectionDetailDTO.setPassword(dbPassword);
		} catch (Exception ex) {
			knLogger.error(methodName, "Exception while fetching EMS Connection detail", ex);
			throw ex;
		} finally {
			KnDBUtil.closeResource(resultSet, statement, null, connection);
		}
		knLogger.exit(methodName);
		return connectionDetailDTO;
	}

	public List<KnConnectionDetailDTO> getPttServerConnDetails() throws Exception {
		String methodName = "getPttServerConnDetails()";
		knLogger.entry(methodName);
		List<KnConnectionDetailDTO> connectionDetailDTOs = new ArrayList<KnConnectionDetailDTO>();
		ResultSet resultSet = null;
		Statement statement = null;
		Connection connection = null;
		try {
			connection = KnDBUtil.getLocalDBConenction();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(KnDBConst.PTTSERVER_CONNECTION_DETAILS_SQL);
			knLogger.debug(methodName, "QUERY=" + KnDBConst.PTTSERVER_CONNECTION_DETAILS_SQL);
			String dbUserId = KnCommonVaultUtil.getKeyFromVault(T10_USER_NAME_PATH, DBMGR_DBUSERID);
			String dbPassword = KnCommonVaultUtil.getKeyFromVault(T10_PASSWORD_NAME_PATH, DBMGR_DBPASSWORD);
			while (resultSet.next()) {
				KnConnectionDetailDTO connectionDetailDTO = new KnConnectionDetailDTO();
				connectionDetailDTO.setPttserverId(resultSet.getString(1));
				connectionDetailDTO.setIpAddress(resultSet.getString(2));
				connectionDetailDTO.setDsn(resultSet.getString(3));
				//connectionDetailDTO.setUserName(resultSet.getString(4));
				//connectionDetailDTO.setPassword(KnEncryptionDecryptionUtil.getInstance().decrypt(KnDBConst.SALT, resultSet.getString(5), KnDBConst.SECRETKEY, KnDBConst.IVSTRING, KnDBConst.KEYSIZE, KnDBConst.ITERATION));;
				connectionDetailDTO.setUserName(dbUserId);
				connectionDetailDTO.setPassword(dbPassword);
				connectionDetailDTOs.add(connectionDetailDTO);
			}
		} catch (Exception ex) {
			knLogger.error(methodName, "Exception while fetching PTT Servers Connection detail", ex);
			throw ex;
		} finally {
			KnDBUtil.closeResource(resultSet, statement, null, connection);
		}
		knLogger.exit(methodName);
		return connectionDetailDTOs;
	}

	public KnConnectionDetailDTO getPttServerConnDetail(String pttServerId) throws Exception {
		String methodName = "getPttServerConnDetail()";
		knLogger.entry(methodName, "pttServerId", pttServerId);
		KnConnectionDetailDTO connectionDetailDTO = new KnConnectionDetailDTO();
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		try {
			connection = KnDBUtil.getLocalDBConenction();
			preparedStatement = connection.prepareStatement(KnDBConst.PTTSERVER_CONNECTION_DETAIL_SQL);
			preparedStatement.setString(1, pttServerId);
			resultSet = preparedStatement.executeQuery();
			knLogger.debug(methodName, "QUERY=" + KnDBConst.PTTSERVER_CONNECTION_DETAIL_SQL);
			if (resultSet.next()) {
				connectionDetailDTO.setPttserverId(resultSet.getString(1));
				connectionDetailDTO.setIpAddress(resultSet.getString(2));
				connectionDetailDTO.setDsn(resultSet.getString(3));
				//connectionDetailDTO.setUserName(resultSet.getString(4));
				//connectionDetailDTO.setPassword(KnEncryptionDecryptionUtil.getInstance().decrypt(KnDBConst.SALT, resultSet.getString(5), KnDBConst.SECRETKEY, KnDBConst.IVSTRING, KnDBConst.KEYSIZE, KnDBConst.ITERATION));;
			}
			String dbUserId = KnCommonVaultUtil.getKeyFromVault(T10_USER_NAME_PATH, DBMGR_DBUSERID);
			String dbPassword = KnCommonVaultUtil.getKeyFromVault(T10_PASSWORD_NAME_PATH, DBMGR_DBPASSWORD);
			connectionDetailDTO.setUserName(dbUserId);
			connectionDetailDTO.setPassword(dbPassword);
		} catch (Exception ex) {
			knLogger.error(methodName, "Exception while fetching PTT Servers Connection detail", ex);
			throw ex;
		} finally {
			KnDBUtil.closeResource(resultSet, null, preparedStatement, connection);
		}
		knLogger.exit(methodName, "connectionDetailDTO", connectionDetailDTO);
		return connectionDetailDTO;
	}
}
