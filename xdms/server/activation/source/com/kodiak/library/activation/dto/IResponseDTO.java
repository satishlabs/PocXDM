/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  IResponseDTO.java
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
package com.kodiak.library.activation.dto;

import com.kodiak.xdms.server.common.dto.intf.IGenDTO;

import java.util.Map;

/**
 *
 */
public interface IResponseDTO extends IGenDTO {

    /**
     *
     * @param responseCode
     */
    void setResponseCode(int responseCode);

    /**
     *
     * @return
     */
    int getResponseCode();

//    /**
//     *
//     * @param mdn
//     */
//    void setMdn(String mdn);
//
//    /**
//     *
//     * @return
//     */
//    String getMdn();

    /**
     *
     * @param messageId
     */
    void setMessageId(String messageId);

    /**
     *
     * @return
     */
    String getMessageId();

    /**
     *
     * @param responseStatus
     */
    void setResponseStatus(String responseStatus);

    /**
     *
     * @return
     */
    String getResponseStatus();

    /**
     *
     * @param transactionId
     */
    void setTransactionId(String transactionId);

    /**
     *
     * @return
     */
    String getTransactionId();

//    /**
//     *
//     * @param featureId
//     */
//    void setFeatureId(String featureId);
//
//    /**
//     *
//     * @return
//     */
//    String getFeatureId();
//
//    /**
//     *
//     * @param failedMdns
//     */
//    void setFailedMdns(Map<String, String> failedMdns);
//
//    /**
//     *
//     * @return
//     */
//    Map<String, String> getFailedMdns();
}
