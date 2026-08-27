/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   ICacheData.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         14-03-2007 6.0
 *
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
 **************************************************************************/
package com.kodiak.xdms.server.common.configuration.cache;


import java.io.Serializable;
import java.util.List;

/**
 * This is the base interface for cache data. All the data to be stored
 * into cache should implement this interface.
 */
public interface ICacheData extends Serializable {

    /**
     * This will add an element to the ICacheData. If the element
     * is already there, then it will just return without adding
     * 
     * @param elementList
     * @throws KnCacheDataException
     */
    void addElement(List elementList) throws KnCacheDataException;

    /**
     * add a key and value to Caching element
     *
     * @param elementIdList    the id list
     * @param value the value
     */
    void addValue(List elementIdList, String value) throws KnCacheDataException;
}
