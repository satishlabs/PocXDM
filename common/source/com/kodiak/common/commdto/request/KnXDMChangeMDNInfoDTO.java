/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnXDMChangeMDNInfoDTO.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/22/10       7.0
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

package com.kodiak.common.commdto.request;


import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.resources.KnGDPRTemplate;

public class KnXDMChangeMDNInfoDTO extends KnXDMSubsProvInfoDTO implements IXDMRequestDTO {

    private static final long serialVersionUID = 7526471155622676137L;
    //stores new MDN value
    private String newMdn;

    //stores the new IMEI value
    private String newIMEI;

    private String oldMDN;

    private String oldIMEI;

    private String operationType;

    private int clientType;

    private IAuthDTO authDTO;

    /**
     * getter method for new MDN
     *
     * @return String
     */
    public String getNewMdn() {
        return newMdn;
    }

    /**
     * setter method for new MDN
     *
     * @param newMdn String
     */
    public void setNewMdn(String newMdn) {
        if (newMdn != null) {
            newMdn = newMdn.trim();
            if (newMdn.equals("")) {
                newMdn = null;
            }
        }
        this.newMdn = newMdn;
    }

    /**
     * getter method for new IMEI
     *
     * @return String
     */
    public String getNewIMEI() {
        return newIMEI;
    }

    /**
     * setter method for new IMEI
     *
     * @param newIMEI String
     */
    public void setNewIMEI(String newIMEI) {
        if (newIMEI != null) {
            newIMEI = newIMEI.trim();
            if (newIMEI.equals("")) {
                newIMEI = null;
            }
        }
        this.newIMEI = newIMEI;
    }

    /**
     * getter method for Old MDN
     *
     * @return String
     */
    public String getOldMDN() {
        return oldMDN;
    }

    /**
     * setter method for the Old MDN
     *
     * @param oldMDN String
     */
    public void setOldMDN(String oldMDN) {
        this.oldMDN = oldMDN;
    }

    /**
     * getter method for the OLD IMEI
     *
     * @return String
     */
    public String getOldIMEI() {
        return oldIMEI;
    }

    /**
     * setter method for the Old IMEI
     *
     * @param oldIMEI String
     */
    public void setOldIMEI(String oldIMEI) {
        this.oldIMEI = oldIMEI;
    }

    /**
     * getter method for Operation Type
     *
     * @return String
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * setter method for Operation Type
     *
     * @param operationType String
     */
    public void setOperationType(String operationType) {
        if (operationType != null) {
            operationType = operationType.trim();
            if (operationType.equals("")) {
                operationType = null;
            }
        }
        this.operationType = operationType;
    }

    /**
     * getter method for client Type
     *
     * @return int
     */
    public int getClientType() {
        return clientType;
    }

    /**
     * setter method for client Type
     *
     * @param clientType int
     */
    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    /**
     * getter method for the Auth DTO
     *
     * @return IAuthDTO
     */
    public IAuthDTO getAuthDTO() {
        return authDTO;
    }

    /**
     * setter method for the Auth DTO
     *
     * @param authDTO IAuthDTO
     */
    public void setAuthDTO(IAuthDTO authDTO) {
        this.authDTO = authDTO;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(200);
        strBuffer.append(super.toString());
        strBuffer.append(" [KnXDMChangeMDNInfoDTO -> ");
        strBuffer.append(" OLD_MDN - ").append(KnGDPRTemplate.mdn(oldMDN))
                .append(", OLD_IMEI - ").append(oldIMEI)
                .append(", NEW_MDN - ").append(KnGDPRTemplate.mdn(newMdn))
                .append(", NEW_IMEI - ").append(newIMEI)
                .append(", Auth_DTO - ").append(authDTO)
                .append(", Operation_Type - ").append(operationType)
                .append(", Client_Type - ").append(clientType)
                .append("]");
        return strBuffer.toString();
    }

    public String getObjectId() {
        return this.getTransactionId();
    }
}
