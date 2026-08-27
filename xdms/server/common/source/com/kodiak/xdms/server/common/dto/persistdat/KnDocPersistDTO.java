/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnDocPersistDTO.java
 * Subsystem:   XDM server Common DTO
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 18, 2010       7.0
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
package com.kodiak.xdms.server.common.dto.persistdat;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

public class KnDocPersistDTO implements IPersistenceDTO {

    private static final long serialVersionUID = 7526471155622676186L;

    private IInputDTO inputDTO;
    private IPersistenceDTO persistenceDTO;
    private String operationType;
    private String entityId;
    private String objectId;
    private String profile;

    private int docId;
    private String mdn;
    private int etag;


    /**
     * getter method for doc Id
     *
     * @return int
     */
    public int getDocId() {
        return docId;
    }

    /**
     * setter method for docId
     *
     * @param docId int
     */
    public void setDocId(int docId) {
        this.docId = docId;
    }

    /**
     * @return
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * @param mdn
     */
    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    /**
     * @return
     */
    public int getEtag() {
        return etag;
    }

    /**
     * @param etag
     */
    public void setEtag(int etag) {
        this.etag = etag;
    }

    /**
     * @param inputDTO input dto object
     */
    public void setInputDTO(IInputDTO inputDTO) {
        setProfile(inputDTO.getProfile());
        setEntityId(inputDTO.getEntityId());
        setOperationType(inputDTO.getOperationType());
        this.inputDTO = inputDTO;
    }

    /**
     * @return
     */
    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    /**
     * @param persistenceDTO
     */
    public void setPersistenceDTO(IPersistenceDTO persistenceDTO) {
        this.persistenceDTO = persistenceDTO;
    }

    /**
     * @return
     */
    public IPersistenceDTO getPersistenceDTO() {
        return persistenceDTO;
    }

    /**
     * @param operationType the operation type
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    /**
     * @return
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * @return
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * @param entityId
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * @return
     */
    public String getObjectId() {
        return objectId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        if (profile != null) {
            profile = profile.trim();
            if (profile.equals("")) {
                profile = null;
            }
        }
        this.profile = profile;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(50);
        strBuffer.append(", DOC_ID - ").append(docId)
                .append(", MDN - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", ETAG -").append(etag);

        return strBuffer.toString();
    }
}