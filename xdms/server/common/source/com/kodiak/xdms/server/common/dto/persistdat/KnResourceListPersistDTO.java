/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnResourceListPersistDTO.java
 * Subsystem:   XDM Server Common DTO
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

public class KnResourceListPersistDTO extends KnDocPersistDTO {

    private static final long serialVersionUID = 7526471155622676187L;

    private String xmlDoc;
    private int resourceListId;
    private String listName;
    private String listDisplayName;
    private int resourceListType;

    /**
     * @return
     */
    public String getXmlDoc() {
        return xmlDoc;
    }

    /**
     * @param xmlDoc
     */
    public void setXmlDoc(String xmlDoc) {
        this.xmlDoc = xmlDoc;
    }

    /**
     * @return
     */
    public int getResourceListId() {
        return resourceListId;
    }

    /**
     * @param resourceListId
     */
    public void setResourceListId(int resourceListId) {
        this.resourceListId = resourceListId;
    }

    /**
     * @return
     */
    public String getListName() {
        return listName;
    }

    /**
     * @param listName
     */
    public void setListName(String listName) {
        this.listName = listName;
    }

    /**
     * @return
     */
    public String getListDisplayName() {
        return listDisplayName;
    }

    /**
     * @param listDisplayName
     */
    public void setListDisplayName(String listDisplayName) {
        this.listDisplayName = listDisplayName;
    }

    /**
     * @return
     */
    public int getResourceListType() {
        return resourceListType;
    }

    /**
     * @param resourceListType
     */
    public void setResourceListType(int resourceListType) {
        this.resourceListType = resourceListType;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", XML_DOC - ").append(xmlDoc)
                .append(", RESOURCE_LIST_DOC_ID - ").append(resourceListId)
                .append(", LIST_NAME - ").append(listName)
                .append(", LIST_DISPLAY_NAME - ").append(listDisplayName)
                .append(", LIST_TYPE - ").append(resourceListType);

        return strBuffer.toString();
    }
}
