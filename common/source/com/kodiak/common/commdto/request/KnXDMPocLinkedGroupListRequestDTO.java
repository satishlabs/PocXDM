/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMPocLinkedGroupListRequestDTO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        29-12-2010      7.0
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
package com.kodiak.common.commdto.request;

import com.kodiak.common.commdto.common.IAuthDTO;
import com.kodiak.common.commdto.common.KnXDMCorpInfo;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;

public class KnXDMPocLinkedGroupListRequestDTO extends KnXDMCorpInfo implements IXDMRequestDTO {
    @Override
    public String getOperationType() {
        return null;
    }

    @Override
    public void setOperationType(String operationType) {

    }

    @Override
    public int getClientType() {
        return 0;
    }

    @Override
    public void setClientType(int clientType) {

    }

    @Override
    public IAuthDTO getAuthDTO() {
        return null;
    }

    @Override
    public void setAuthDTO(IAuthDTO authDTO) {

    }

    @Override
    public void setDestPttServerId(String destPttServerId) {

    }

    @Override
    public String getDestPttServerId() {
        return null;
    }

    @Override
    public void setDestQueueName(String destQueueName) {

    }

    @Override
    public String getDestQueueName() {
        return null;
    }

    @Override
    public String getTransactionId() {
        return null;
    }

    @Override
    public void setTransactionId(String transactionId) {

    }

	@Override
	public void setHierarchyType(HIERARCHY_TYPE hierarchyType) {
		// TODO Auto-generated method stub

	}

	@Override
	public HIERARCHY_TYPE getHierarchyType() {
		// TODO Auto-generated method stub
		return null;
	}
}
