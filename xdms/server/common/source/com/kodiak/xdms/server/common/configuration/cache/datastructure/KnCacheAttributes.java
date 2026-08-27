/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnCacheAttributes.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         15-03-2007 6.0
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
package com.kodiak.xdms.server.common.configuration.cache.datastructure;

import com.kodiak.common.dto.IIdentifier;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


/**
 * This represent a cache attribute
 */
public class KnCacheAttributes implements IIdentifier {
    private static final long serialVersionUID = 7526471155622676262L;

    private Map attributeMap;
    private String objectId;

    /**
     * constructor
     */
    public KnCacheAttributes() {
        attributeMap = new HashMap();
    }

    /**
     * put the value to the map
     * @param key    the key
     * @param value  the value
     */
    public void put(String key, String value) {
        attributeMap.put(key, value);
    }

    /**
     * get the value from the map
     * @param key the key
     * @return the value for the key
     */
    public String get(String key) {
        return (String)attributeMap.get(key);
    }

    /**
     * this method will return the getNameIterator object
     * @return the getNameIterator
     */
    public Iterator getNameIterator() {
        return attributeMap.keySet().iterator();
    }

    /**
     * prints as string
     * @return the string representation of the object
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(100);
        buffer.append(attributeMap);
        return buffer.toString();
    }

    /**
     * This method will return the attribute map
     * @return the attribute map
     */
    public Map getAttributesMap() {
        Map retMap = new HashMap();
        retMap.putAll(attributeMap);
        return retMap;
    }

    public String getObjectId() {
        return objectId;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
