/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnOPBulkDeleteSubsRespDTO.java
 * Subsystem:   Prov Library DTO
 * <p/>
 * Name                         Date           Release
 * ---------------------       -----------     -------
 * Shaik Mahaaboob Basha        09/jan/2014       7.7
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

package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;

import java.util.List;
import java.util.Map;


/**
 * DTO object used for Change Service Auth Status response
 * the parameters mdn, pocServerHome, Presence Server Home
 * while sending the de-activation notification
 */
public class KnOPBulkDeleteSubsRespDTO extends KnOPProvDTO {

    private static final long serialVersionUID = 7526471155622676907L;

    /* stores the notifications Object */
    private List<KnOPDeleteSubsRespDTO> deleteSubsRespDTOs;

    public List<KnOPDeleteSubsRespDTO> getDeleteSubsRespDTOs() {
        return deleteSubsRespDTOs;
    }

    public void setDeleteSubsRespDTOs(List<KnOPDeleteSubsRespDTO> deleteSubsRespDTOs) {
        this.deleteSubsRespDTOs = deleteSubsRespDTOs;
    }
    private Map<String,String>activeFSMap2;
    
    public Map<String, String> getActiveFSMap2() {
		return activeFSMap2;
	}

	public void setActiveFSMap2(Map<String, String> activeFSMap2) {
		this.activeFSMap2 = activeFSMap2;
	}

	public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(super.toString());
        strBuffer.append(" [KnOPBulkDeleteSubsRespDTO --> ")
                .append(deleteSubsRespDTOs)
                .append(activeFSMap2)
                .append("]");

        return strBuffer.toString();

    }
}
