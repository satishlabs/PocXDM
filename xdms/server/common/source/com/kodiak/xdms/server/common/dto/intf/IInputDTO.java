/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/************************************************************************
 * File name:   IInputDTO.java
 * Subsystem:   COMMON DTO
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       Dec 22, 2010       7.0
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
 ***************************************************************************/
package com.kodiak.xdms.server.common.dto.intf;


public interface IInputDTO extends IEntity {

    /**
     * sets performer to the subscriber
     *
     * @param performer sets performer to subscriber
     */
    public void setPerformer(String performer);

    /**
     * returns the performer of the subscriber
     *
     * @return returns performer
     */
    public String getPerformer();

    /**
     * this method will set the Auth DTO
     *
     * @param authDTO
     */
    public void setAuthDTO(IAuthDTO authDTO);

    /**
     * this method will retun the authDTO
     *
     * @return returns authDTO
     */
    public IAuthDTO getAuthDTO();

    /**
     *
     * @param clientType
     */
    public void setClientType(int clientType);

    /**
     *
     * @return
     */
    public int getClientType();
}

