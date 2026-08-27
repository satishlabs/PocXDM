/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.xdms.server.common.dto.clientdat.KnOPDispatchDirChgDTO;

import java.util.Map;

/**
 * Created by Deepak on 9/6/14.
 */
public class KnOPBulkRespDTO extends KnOPProvDTO {

    private static final long serialVersionUID = 7526471155622676907L;

    // private String mdn;
    private String pocServerHome;
    private String presenceServerHome;
    //stores the notifications Object
    // private List<KnOPDirChgDTO> dirChgDTOs;
    private Map<String, KnOPDispatchDirChgDTO> mdnDispatcherChgDTOMap;


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

    public Map<String, KnOPDispatchDirChgDTO> getMdnDispatcherChgDTOMap() {
        return mdnDispatcherChgDTOMap;
    }

    public void setMdnDispatcherChgDTOMap(Map<String, KnOPDispatchDirChgDTO> mdnDispatcherChgDTOMap) {
        this.mdnDispatcherChgDTOMap = mdnDispatcherChgDTOMap;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KnOPBulkRespDTO{");
        sb.append("pocServerHome='").append(pocServerHome).append('\'');
        sb.append(", presenceServerHome='").append(presenceServerHome).append('\'');
        sb.append(", mdnDispatcherChgDTOMap=").append(mdnDispatcherChgDTOMap);
        sb.append('}');
        return sb.toString();
    }
}
