/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name: KnProvSMSUtil.java
 * Subsystem:  Prov Welcome SMS
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * 			             17-Aug-2012       7.4
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
package com.kodiak.xdms.server.subsmgmt.business.helper;

import com.kodiak.common.dao.KnDBConfigInfo;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersistenceException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnEncryptionDecryptionUtil;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.vault.KnCommonVaultUtil;
import com.kodiak.dbmgr.KnTimedConnection;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.tlvgenerator.KnTLVMsgGenerator;
import com.kodiak.utilities.tlvgenerator.dto.KnTLVMsgReqDTO;
import com.kodiak.utilities.tlvgenerator.resources.KnTLVException;
import com.kodiak.utilities.tlvgenerator.tlvdatatypes.KnTLVDatatypeInteger4;
import com.kodiak.utilities.tlvgenerator.tlvdatatypes.KnTLVDatatypeVString;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnProvSMSDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kodiak.common.resources.KnConstants.ENTT_PASSWORD_NAME_PATH;

public class KnProvSMSUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnProvSMSUtil.class);
    private final String className = KnProvSMSUtil.class.getName();
    private static final Object lock = new Object();
    private static AtomicInteger sequeceNum;
    private static String ssl_flag = null;
    private static String wallet = null;
    private static String encryption = null;
    private static String cipherSuites = null;
    private static String clientAuthentication = null;
    private static final String COMMONCONFIGFILE = "CommonConfig.properties";
    private static String commonConfigFile = "/DG/activeRelease/dat" + File.separator + COMMONCONFIGFILE;
    private static Properties lDbProps = new Properties();

    public KnProvSMSUtil() {
        sequeceNum = new AtomicInteger(0);
    }

    /**
     * This method constructs payload and inserts the payload along with the other values in to  the db.
     *
     * @param provSMSDTO   DTO which contains
     * @param persisterTxn Transaction to get DB connection
     * @return boolean value true or false.
     */
    public boolean sendProvSMS(KnProvSMSDTO provSMSDTO, KnPersisterTxn persisterTxn) {
        String methodName = "sendProvSMS(KnProvSMSDTO,KnPersisterTxn)";
        knLogger.debug( methodName, "proSMS DTO ", provSMSDTO);
        boolean tlvInsertStatus = false;
        boolean ownedTxn = false;
        Connection conn = null;
        Connection conn2 = null;
        ResultSet rs = null;
        PreparedStatement pStmt = null;
        try {
            int srcSubsystem = KnProvConstants.SRC_SUB_SYSTEM;
            int recipientType = provSMSDTO.getRecipientType();
            String recpMDN = provSMSDTO.getMdn();
            int deliveryStatus = 0; // indicates ready to pick from DB
            int deliveryReport = KnProvConstants.DELIVERY_REPORT;//false
            byte[] payLoad = generatePayLoad(provSMSDTO);
            long insertionTimeStamp = System.currentTimeMillis() / 1000;
            int sequeceNum = 0;
            KnDBConfigInfo dbConfigInfo = KnDbUtil.getDBConfigInfo();
            String localPttId = dbConfigInfo.getLocalPttId();

            String query = "INSERT INTO DG.PendingUserMsgNotification VALUES(?, ?, ?, ?, ?, ?,?,?)";
            synchronized (lock) {
                sequeceNum = generateSequenceNumber();
                knLogger.debug( methodName, " sequeceNum. ", sequeceNum);
            }
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug( methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            conn = persisterTxn.getDBConnection(localPttId, false);
			String remoteSMPPDbURL = getRemoteSMPPDBURL(conn);
			if (remoteSMPPDbURL != null) {
				conn2 = KnTimedConnection.getConnection(remoteSMPPDbURL, null, null, 0);
				pStmt = conn2.prepareStatement(query);
			} else {
				pStmt = conn.prepareStatement(query);
			}
            InputStream is = new ByteArrayInputStream(payLoad);
            pStmt.setInt(1, srcSubsystem);
            pStmt.setInt(2, sequeceNum);
            pStmt.setString(3, recpMDN);
            pStmt.setInt(4, recipientType);
            pStmt.setInt(5, deliveryStatus);
            pStmt.setInt(6, deliveryReport);
            pStmt.setBinaryStream(7, is, is.available());
            pStmt.setLong(8, insertionTimeStamp);

            knLogger.debug( methodName, "Query: Executing - ", query);
            knLogger.debug( methodName, "srcSubsystem - " + srcSubsystem, " sequnceNum " + sequeceNum, " recpMDN " + KnGDPRTemplate.mdn(recpMDN), " recipientType " + recipientType, " payLoad " + payLoad, " insertionTimeStamp " + insertionTimeStamp);

            int result = pStmt.executeUpdate();
            tlvInsertStatus = true;
            knLogger.debug( methodName, "Query: Executed with result ", result);
			if (conn2 != null) {
				conn2.close();
			}
            if (ownedTxn) {
                knLogger.debug( methodName, "Saving the transaction ");
                persisterTxn.save();
            }

        } catch (KnPersistenceException e) {
            knLogger.error( methodName, "KnPersistenceException occurred - ", e.getErrorMessage());
            
            if (ownedTxn) {
               rollback(persisterTxn);
            }
        } catch (KnProvBOException pe) {
            knLogger.error( methodName, "KnPersistenceException occurred - ", pe.getErrorMessage());
            
            if (ownedTxn) {
               rollback(persisterTxn);
            }
        } catch (Exception e) {
            knLogger.error( methodName, "Unexpected Exception occurred -  ", e.getMessage());
            
            if (ownedTxn) {
              rollback(persisterTxn);
            }
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            knLogger.info( methodName, "EXIT: PROV-SMS Send Notification ");
        }
        return tlvInsertStatus;

    }


    /**
     * This method generates payload by the values provided as of parameter.
     *
     * @param provSMSDTO
     * @return byte array of payload
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *
     */
    private byte[] generatePayLoad(KnProvSMSDTO provSMSDTO) throws KnProvBOException {
        String methodName = "generatePayLoad";

        KnTLVMsgGenerator msgGenerator = new KnTLVMsgGenerator();
        byte[] payLoad;
        try {
            List<KnTLVMsgReqDTO> reqLIst = new ArrayList<KnTLVMsgReqDTO>();
            //orginator mdn
            KnTLVMsgReqDTO msgReqDTO = new KnTLVMsgReqDTO();
            msgReqDTO.setTagId(0);
            msgReqDTO.setDataType(new KnTLVDatatypeVString());
            msgReqDTO.setValue(provSMSDTO.getMdn());
            reqLIst.add(msgReqDTO);
            //
            msgReqDTO = new KnTLVMsgReqDTO();
            msgReqDTO.setTagId(2);
            msgReqDTO.setDataType(new KnTLVDatatypeInteger4());
            msgReqDTO.setValue(provSMSDTO.getMsgNotificationId());
            reqLIst.add(msgReqDTO);
            payLoad = msgGenerator.generateTLVMsg(KnProvConstants.NTLV_MESSAGE_TYPE, KnProvConstants.VERSION, reqLIst);
            knLogger.debug( methodName, "Payload ", payLoad);

        } catch (KnTLVException e) {
            knLogger.error( methodName, "KnTlvException  occurred - ", e);
            knLogger.error( methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Failed to generate PayLoad " + e.getErrorMessage());
        }
        return payLoad;

    }

    private void rollback(KnPersisterTxn txn) {
        try {
            if (txn != null) {
                txn.rollback();
            }
        } catch (Exception e) {
            knLogger.error( "rollback(txn)", "Failed to rollback the transaction.");
        }
    }

    private int generateSequenceNumber() {
        String methodName = "generateSequenceNumber";
        Integer generatedId = sequeceNum.incrementAndGet();
        if (generatedId < 0) {
            sequeceNum = new AtomicInteger(0);
            generatedId = sequeceNum.incrementAndGet();
        }
        knLogger.debug( methodName, " genearteId ", generatedId);
        return generatedId;
    }

    public String getRemoteSMPPDBURL(Connection conn) throws Exception {
		String methodName = "getRemoteSMPPDBURL(Connection conn)";
		String remoteSMPPDbURL = null;
		
		String remoteSMPPServerId = System.getenv(KnConstants.REMOTE_SMPP_SERVER_PTTSERVERID);
		String useRemoteSMPP = System.getenv(KnConstants.USE_REMOTE_SMPP);
		knLogger.debug(methodName, "remoteSMPPServerId - " + remoteSMPPServerId + " useRemoteSMPP - " + useRemoteSMPP);

		if ("1".equals(useRemoteSMPP)) {
			String query = "SELECT FIRST 1 PSII.IPADDRESS,PSI.TTUID,PSI.EN_TTPWD,PSI.TTDATASOURCENAME FROM DG.PTTSERVERIPINFO PSII,DG.PTTSERVERINFO PSI WHERE PSII.SUBNETID=0 AND PSII.CARDREDNSTATE='A' AND PSII.IPADDRESS_TYPE=1 AND PSII.PTTSERVERID=PSI.PTTSERVERID AND PSII.PTTSERVERID=?";
				PreparedStatement pStmt = null;
				ResultSet rs = null;
				try {
					pStmt = conn.prepareStatement(query);
					pStmt.setString(1, remoteSMPPServerId);
					knLogger.debug(methodName, "Query: Executing - ", query);
					rs = pStmt.executeQuery();
					if (rs.next()) {
						String enttPwd = KnCommonVaultUtil.getKeyFromVault(ENTT_PASSWORD_NAME_PATH, KnConstants.ENTT_PASSWORD);
						String password = KnEncryptionDecryptionUtil.getInstance().decrypt(KnConstants.SALT, rs.getString("EN_TTPWD"), KnConstants.SECRETKEY, KnConstants.IVSTRING, KnConstants.KEYSIZE, KnConstants.ITERATION);
						getPropertyValue();
						if (Objects.equals(ssl_flag, "1")) {
							remoteSMPPDbURL = "jdbc:timesten:client:TTC_Server=" + rs.getString("IPADDRESS") + ";TTC_Server_DSN=" + rs.getString("TTDATASOURCENAME") + ";uid=" + rs.getString("TTUID") + ";pwd=" + enttPwd + ";TCP_PORT=53389"
									+ ";Wallet=" + wallet
									+ ";Encryption=" + encryption
									+ ";CipherSuites=" + cipherSuites
									+ ";SSLClientAuthentication=" + clientAuthentication
									+ ";TTC_Timeout=180";
						} else {
							remoteSMPPDbURL = "jdbc:timesten:client:TTC_Server=" + rs.getString("IPADDRESS") + ";TTC_Server_DSN=" + rs.getString("TTDATASOURCENAME") + ";uid=" + rs.getString("TTUID") + ";pwd=" + enttPwd + ";TCP_PORT=53389;TTC_Timeout=180";
						}
					}
				} finally {
					KnDbUtil.closeResultSet(rs);
					KnDbUtil.closeStatement(pStmt);
				}
			}
		knLogger.debug(methodName, "remoteSMPPDbURL - " + remoteSMPPDbURL);
		return remoteSMPPDbURL;
	}

    public static void getPropertyValue() throws Exception {
        FileInputStream lFStream = null;
        try {
            File configFile = new File(commonConfigFile);
            if (!configFile.exists()) {
                throw new IllegalArgumentException("CommonConfig.properties file is missing");
            }

            lFStream = new FileInputStream(commonConfigFile);
            lDbProps.load(lFStream);
            ssl_flag = lDbProps.getProperty("IS_TIMESTEN_SSL_ENABLED", "0");
            wallet = lDbProps.getProperty("TIMESTEN_SSL_CLIENTWALLET", null);
            encryption = lDbProps.getProperty("TIMESTEN_SSL_ENCRYPTIONTYPE", null);
            cipherSuites = lDbProps.getProperty("TIMESTEN_SSL_CIPHERSUITES", null);
            clientAuthentication = lDbProps.getProperty("TIMESTEN_SSL_CLIENTAUTH", null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (lFStream != null) {
                lFStream.close();
            }
        }
    }

}
