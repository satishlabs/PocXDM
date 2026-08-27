/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnOPChgAuthStatusRespDTO.java
 * Subsystem:   Prov Library DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar           21/feb/13       7.5
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

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;

import java.util.List;
import java.util.Map;

/**
 * DTO object used for Change Service Auth Status response
 * the parameters mdn, pocServerHome, Presence Server Home
 * while sending the de-activation notification
 */
public class KnOPBulkChgAuthStatusRespDTO extends KnOPProvDTO {

    private static final long serialVersionUID = 7526471155622676907L;

    private String mdn;
    private String pocServerHome;
    private String presenceServerHome;
    private Long activeFS;
    private int corpId;
    private Map<String, Integer> clientTypeMap;
    private  Map<String,String>activeFSMap2;

    //stores the notifications Object
    private List<KnOPDirChgDTO> dirChgDTOs;

    /**
     * getter method for the Mdn
     *
     * @return String
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * setter method for the Mdn
     *
     * @param mdn String
     */
    public void setMdn(String mdn) {
        if (mdn != null) {
            mdn = mdn.trim();
            if (mdn.equals("")) {
                mdn = null;
            }
        }
        this.mdn = mdn;
    }

    /**
     * getter method for the Poc Server Home
     *
     * @return String
     */
    public String getPocServerHome() {
        return pocServerHome;
    }

    /**
     * setter method for the Presence Server Home
     *
     * @param pocServerHome String
     */
    public void setPocServerHome(String pocServerHome) {
        if (pocServerHome != null) {
            pocServerHome = pocServerHome.trim();
            if (pocServerHome.equals("")) {
                pocServerHome = null;
            }
        }
        this.pocServerHome = pocServerHome;
    }

    /**
     * getter method for the Presence Server Home
     *
     * @return String
     */
    public String getPresenceServerHome() {
        return presenceServerHome;
    }

    /**
     * setter method for the Presence Server Home
     *
     * @param presenceServerHome String
     */
    public void setPresenceServerHome(String presenceServerHome) {
        if (presenceServerHome != null) {
            presenceServerHome = presenceServerHome.trim();
            if (presenceServerHome.equals("")) {
                presenceServerHome = null;
            }
        }
        this.presenceServerHome = presenceServerHome;
    }

    public List<KnOPDirChgDTO> getDirChgDTOs() {
        return dirChgDTOs;
    }

    public void setDirChgDTOs(List<KnOPDirChgDTO> dirChgDTOs) {
        this.dirChgDTOs = dirChgDTOs;
    }

    public Long getActiveFS() {
        return activeFS;
    }

    public void setActiveFS(Long activeFS) {
        this.activeFS = activeFS;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public Map<String, Integer> getClientTypeMap() {
        return clientTypeMap;
    }

    public void setClientTypeMap(Map<String, Integer> clientTypeMap) {
        this.clientTypeMap = clientTypeMap;
    }

    public Map<String, String> getActiveFSMap2() {
		return activeFSMap2;
	}

	public void setActiveFSMap2(Map<String, String> activeFSMap2) {
		this.activeFSMap2 = activeFSMap2;
	}

	public String toString() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(super.toString());
        strBuffer.append(" [KnOPBulkChgAuthStatusRespDTO --> ");
        strBuffer.append(", MDN - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", Poc_Server_Home - ").append(pocServerHome)
                .append(", Presence_Server_Home - ").append(presenceServerHome)
                .append(", activeFS - ").append(activeFS)
                .append(", corpId - ").append(corpId)
                .append(", clientTypeMap - ").append(clientTypeMap)
                .append(", activeFSMap2 - ").append(activeFSMap2)
                .append("]");

        return strBuffer.toString();

    }
}
