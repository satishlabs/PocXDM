/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.cache;

/**
 * Created by kajit on 16-05-2018.
 */

public interface ICache {

    void add(String key, Object value, long periodInMillis);

    void remove(String key);

    Object get(String key);

    void clear();

    long size();
}