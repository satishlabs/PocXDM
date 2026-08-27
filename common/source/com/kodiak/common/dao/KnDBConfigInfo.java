/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.dao;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnEncryptionDecryptionUtil;
import com.kodiak.vault.KnCommonVaultUtil;
import com.kodiak.logger.KnLogger;

import java.util.Properties;

import static com.kodiak.common.resources.KnConstants.T10_PASSWORD_NAME_PATH;
import static com.kodiak.common.resources.KnConstants.T10_USER_NAME_PATH;

public class KnDBConfigInfo {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDBConfigInfo.class);
    private String dbIPAddress = null;
    private int dbPort = 0;
    private String dbUserId = null;
    private String dbPassword = null;
    private String dbDSN = null;
    private int emsDBPort = 0;
    private int pttDBPort = 0;
    private String activeIPAuditInterval = null;
    private int tcpPort = 0;

    public KnDBConfigInfo(Properties props) {
        this.dbDSN = props.getProperty(KnConstants.DBMGR_DBDSN);
        this.dbIPAddress = props.getProperty(KnConstants.DBMGR_IPADDRESS);
        this.dbUserId = KnCommonVaultUtil.getKeyFromVault(T10_USER_NAME_PATH, KnConstants.DBMGR_DBUSERID);
        try {
        	this.dbPassword = KnCommonVaultUtil.getKeyFromVault(T10_PASSWORD_NAME_PATH, KnConstants.DBMGR_DBPASSWORD);
		}catch (Exception e) {
			knLogger.error(" dbPassword decyption is failed. Exception in decrypt the password ",e);
		} 
        
        this.tcpPort = Integer.parseInt(props.getProperty(KnConstants.DBMGR_TCP_PORT));
        this.dbPort = Integer.parseInt(props.getProperty(KnConstants.DBMGR_DBPORT));
        this.activeIPAuditInterval = props.getProperty(KnConstants.DBMGR_AUDIT_INTERVAL);
    }

    public String getDbIPAddress() {
        return dbIPAddress;
    }

    public void setDbIPAddress(String dbIPAddress) {
        this.dbIPAddress = dbIPAddress;
    }

    public int getDbPort() {
        return dbPort;
    }

    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbUserId() {
        return dbUserId;
    }

    public void setDbUserId(String dbUserId) {
        this.dbUserId = dbUserId;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbDSN() {
        return dbDSN;
    }

    public void setDbDSN(String dbDSN) {
        this.dbDSN = dbDSN;
    }

    public String getActiveIPAuditInterval() {
        return activeIPAuditInterval;
    }

    public void setActiveIPAuditInterval(String activeIPAuditInterval) {
        this.activeIPAuditInterval = activeIPAuditInterval;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public int getEmsDBPort() {
        return emsDBPort;
    }

    public void setEmsDBPort(int emsDBPort) {
        this.emsDBPort = emsDBPort;
    }

    public int getPttDBPort() {
        return pttDBPort;
    }

    public void setPttDBPort(int pttDBPort) {
        this.pttDBPort = pttDBPort;
    }

    public String getLocalPttId() {
        return dbDSN.substring(3);
    }

    public String getConnectionURL() {
        return new StringBuffer(50).append("jdbc:timesten:direct:").append("DSN=").append(dbDSN).
                append(";").toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("KnDBConfigInfo");
        sb.append("{dbIPAddress='").append(dbIPAddress).append('\'');
        sb.append(", dbPort=").append(dbPort);
        sb.append(", dbUserId='").append(dbUserId).append('\'');
        sb.append(", dbDSN='").append(dbDSN).append('\'');
        sb.append(", emsDBPort=").append(emsDBPort);
        sb.append(", pttDBPort=").append(pttDBPort);
        sb.append(", activeIPAuditInterval='").append(activeIPAuditInterval).append('\'');
        sb.append(", tcpPort=").append(tcpPort);
        sb.append('}');
        return sb.toString();
    }
}
