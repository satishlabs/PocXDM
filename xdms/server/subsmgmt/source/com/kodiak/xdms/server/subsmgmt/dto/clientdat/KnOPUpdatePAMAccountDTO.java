/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;


/**
 * ***************************************************************************
 * File name:   KnOPUpdatePAMAccountDTO.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar          Feb 09, 2013        7.4
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
 * ******************************************************************************
 */


public class KnOPUpdatePAMAccountDTO extends KnOPProvDTO {
    private static final long serialVersionUID = 7526471155622678006L;

    private int pamAccId;
    private int pamAccState;
    private boolean isSubsFSUpdated;

    public int getPamAccState() {
		return pamAccState;
	}

	public void setPamAccState(int pamAccState) {
		this.pamAccState = pamAccState;
	}

	public int getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(int pamAccId) {
        this.pamAccId = pamAccId;
    }

    public boolean isSubsFSUpdated() {
        return isSubsFSUpdated;
    }

    public void setSubsFSUpdated(boolean isSubsFSUpdated) {
        this.isSubsFSUpdated = isSubsFSUpdated;
    }

    public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(super.toString()).
    		append(" [KnOPUpdatePAMAccountDTO --> ").
    		append("  PAM_ACC_ID -").append(pamAccId).
    		append(" PAM_ACC_STATE - ").append(pamAccState).
            append(" isSubsFSUpdated ").append(isSubsFSUpdated).
    		append("] ");
     return builder.toString();
    }
}
