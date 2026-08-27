/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSubsGroupDistPersistDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 29, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.persistdat;

import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubsGroupDistInfoDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;


public class KnCorpSubsGroupDistPersistDTO extends KnCorpSubsGroupDistInfoDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676204L;

    public void setInputDTO(IInputDTO inputDTO) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public IInputDTO getInputDTO() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public IPersistenceDTO getPersistenceDTO() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setOperationType(String operationType) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getOperationType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getEntityId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setEntityId(String entityId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getProfile() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setProfile(String profile) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getObjectId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}