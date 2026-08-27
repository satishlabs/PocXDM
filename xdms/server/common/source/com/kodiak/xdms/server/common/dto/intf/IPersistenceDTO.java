/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/******************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 16, 2010       7.0
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
 * *******************************************************************************/
package com.kodiak.xdms.server.common.dto.intf;


public interface IPersistenceDTO extends IEntity {
    /**
     * This method is used for retrieving the input DTO data into the dao layer.
     * It will take  input dto as parameter for getting the values from input dtos.
     *
     * @param inputDTO input dto object
     */
    public void setInputDTO(IInputDTO inputDTO);

    /**
     * Return the input dto
     *
     * @return the IInput DTO
     */
    public IInputDTO getInputDTO();

    /**
     * Sets the performer's dao details of the performer
     *
     * @param persistenceDTO
     */
    public void setPersistenceDTO(IPersistenceDTO persistenceDTO);

    /**
     * Returns the dao DTO of the performer
     *
     * @return Persistence DTO
     */
    public IPersistenceDTO getPersistenceDTO();
}
