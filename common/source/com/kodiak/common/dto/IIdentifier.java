/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   IIdentifier.java
 * Subsystem:   Common DTO
 *
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 22, 2010     7.0
 *
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 ***************************************************************************/
package com.kodiak.common.dto;

import java.io.Serializable;

/**
 * This root interface in the DTO hierarchy. DTO stands for Data Transfer Objects used
 * for communication between different layers in the WGP Architecture. All DTOs implement
 * these interface directly or indirectly through one of its subinterfaces.
 */
public interface IIdentifier extends Serializable {

    /**
     * returns the id
     *
     * @return id
     */
    public String getObjectId();
}