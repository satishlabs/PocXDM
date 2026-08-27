/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnCacheElement.java
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


import com.kodiak.xdms.server.common.configuration.cache.ICacheData;
import com.kodiak.xdms.server.common.configuration.cache.KnCacheDataException;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheAttributes;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collection;


/**
 * The caching element for XML Loader
 */
public class KnCacheElement implements ICacheData {

    /**
     * store the id of the element
     */
    private String id;

    /**
     * store the atttibutes
     */
    private KnCacheAttributes attributes;

    /**
     * store the sub elements
     */
    private Map elementMap;

    /**
     * constructor
     */
    public KnCacheElement() {
        this.id = "";
        attributes = new KnCacheAttributes();
        elementMap = new LinkedHashMap();
    }

    /**
     * get the attribute list
     *
     * @return the attribute collection
     */
    public KnCacheAttributes getAttributes() {
        return attributes;
    }

    /**
     * return the attributes as a map
     *
     * @return the attribute as map
     */
    public Map getAttributesMap() {
        return attributes.getAttributesMap();
    }

    /**
     * get the attribute list
     *
     * @return the attribute collection
     */
    public String getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * get the element list
     *
     * @return the element list
     */
    public Collection getElements() {
        return elementMap.values();
    }

    /**
     * return the element with an id
     *
     * @param id the id of the element
     * @return the element object
     */
    public KnCacheElement getElement(String id) {
        return (KnCacheElement) elementMap.get(id);
    }

    /**
     * get the id of the element
     *
     * @return the id of the element
     */
    public String getId() {
        return id;
    }

    /**
     * set the id of the element
     *
     * @param id the id of the element
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Add an entry to caching element. The list <code>elementIdList<code> is used to find the position in
     * the caching element where <code>value</code> can be stored.
     * <p/>
     * The list <code>elementIdList</code> will have values in the order [node-1, node-2, node-3,...node-n, key]
     * where node-1 is the parent of node-2, node-2 is the parent of node-3, etc.. key is the identifier
     * used to cache the value under the node node-n.
     * <p/>
     * This method find the cache element using the ids {node-1, node-2 to node-n} and using the
     * key, the value will be stored in the cache element.
     *
     * @param elementIdList   the list of Ids
     * @param value the value to be stored
     */
    public void addValue(List elementIdList, String value) throws KnCacheDataException {
        KnCacheElement cacheElement = this;
        int i, lastElementIndex = elementIdList.size() - 1;
        for (i = 0; i < lastElementIndex; i++) {
            String idName = (String) elementIdList.get(i);
            cacheElement = getElement(cacheElement, idName);
        }
        String id = (String) elementIdList.get(i);
        if (cacheElement.attributes.get(id) != null) {
            throw new KnCacheDataException(KnErrorCodes.CacheManager.CONFIG_ERROR,
                    "key already exist",
                    elementIdList, value);
        }
        cacheElement.attributes.put(id, value);
    }

    /**
     * This will add an element to the ICacheData. If the element
     * is already there, then it will just return without adding
     *
     * @param elementList
     * @throws KnCacheDataException
     *
     */
    public void addElement(List elementList) throws KnCacheDataException {
        KnCacheElement cacheElement = this;
        int i, lastElementIndex = elementList.size();
        for (i = 0; i < lastElementIndex; i++) {
            String idName = (String) elementList.get(i);
            cacheElement = getElement(cacheElement, idName);
        }
    }

    /**
     * This method will get the element object.
     *
     * @param element the lookup map
     * @param id      the id list
     * @return the element object
     */
    private KnCacheElement getElement(KnCacheElement element, String id) {
        KnCacheElement tmpElement = (KnCacheElement) element.elementMap.get(id);
        if (tmpElement == null) {
            tmpElement = new KnCacheElement();
            tmpElement.setId(id);
            element.elementMap.put(id, tmpElement);
        }
        return tmpElement;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(100);
        buffer.append(id).append(attributes).append(elementMap);
        return buffer.toString();
    }
}
