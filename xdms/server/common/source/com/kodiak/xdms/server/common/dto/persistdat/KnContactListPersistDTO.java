/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.persistdat;

/**
 * ***************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 18, 2010        7.0
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
public class KnContactListPersistDTO extends KnResourceListPersistDTO {

    private static final long serialVersionUID = 7526471155622676184L;

    private int contactListId;
    private int resourceList_etag;

    /**
     *
     * @return
     */
    public int getContactListId() {
        return contactListId;
    }

    /**
     *
     * @param contactListId
     */
    public void setContactListId(int contactListId) {
        this.contactListId = contactListId;
    }

    /**
     *
     * @return
     */
    public int getResourceList_etag() {
        return resourceList_etag;
    }

    /**
     * 
     * @param resourceList_etag
     */
    public void setResourceList_etag(int resourceList_etag) {
        this.resourceList_etag = resourceList_etag;
    }
}
