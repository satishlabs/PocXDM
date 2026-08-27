/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.cache;

/**
 * Created by kajit on 16-05-2018.
 */
public class KnCache {
    
    private static KnCache instance=null;
    private ICache cacheManager;
    public static synchronized KnCache getInstance()
    {
        if (instance == null)
        {
            instance = new KnCache();
        }
        return instance;
    }
    
    private KnCache(){
        cacheManager = new KnCacheManager();
    }
    public ICache getCacheManager(){
           return cacheManager;
    }
}
