/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.util;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnProfileDTO;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnProfileCache.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 12, 2011           7.0
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
 * ************************************************************************
 */

/**
 * This class implements the LRU cache for the profile cache
 */
public class KnProfileCache {
	private static final KnLogger knLogger = KnLogger.getLogger(KnProfileCache.class);

    private static final String CLASS = KnProfileCache.class.getName();
    //stores the cache information using LinkedHashmap
    private Map<String, KnProfileDTO> userVsProfileCache;

    //stores the singleton instance of KnProfileCache
    //private static KnProfileCache instance;
    //default cache size if no configuration value is passed from the client
    private static int MAX_ENTRIES = 400;
    //stores the expiry time (in millisec) for the profile objects in cache
    private static long expiryTime;

    /**
     * This is an empty constructor implementing the LRU algorithm for clearing the cache.
     */
//    private KnProfileCache() {
//        // Create the cache based on LRU replacement algorithm
//        userVsProfileCache = new LinkedHashMap<String, KnProfileDTO>(MAX_ENTRIES + 1, .75F, true) {
//            // This method is called just after a new entry has been added
//            public boolean removeEldestEntry(Map.Entry eldest) {
//                return size() > MAX_ENTRIES;
//            }
//        };
//        // If the cache is to be used by multiple threads,
//        // the cache must be wrapped with code to synchronize the methods
//        userVsProfileCache = Collections.synchronizedMap(userVsProfileCache);
//    }
    public KnProfileCache(long refreshInterval) {
        this(MAX_ENTRIES, refreshInterval);
    }

    public KnProfileCache(int cacheSize, long refreshInterval) {
        MAX_ENTRIES = cacheSize;
        expiryTime = refreshInterval * 1000; //converted into millisec
        knLogger.debug( "getInstance()", "Cache size ->" + cacheSize +
                ", Refreh interval ->" + refreshInterval);
        initCache();
    }

    private void initCache() {
        // Create the cache based on LRU replacement algorithm
        userVsProfileCache = new LinkedHashMap<String, KnProfileDTO>(MAX_ENTRIES + 1, .75F, true) {
            // This method is called just after a new entry has been added
            public boolean removeEldestEntry(Map.Entry eldest) {
                return size() > MAX_ENTRIES;
            }
        };
        // If the cache is to be used by multiple threads,
        // the cache must be wrapped with code to synchronize the methods
        userVsProfileCache = Collections.synchronizedMap(userVsProfileCache);
    }

    /**
     * This method returns the singleton instance of the profileCache
     *
     * @param refreshInterval the expiry time for profile objects
     * @return the singleton instance of the profile cache
     */
//    public static KnProfileCache getInstance(int refreshInterval) {
//        return getInstance(MAX_ENTRIES, refreshInterval);
//    }

    /**
     * /**
     * This method returns the singleton instance of the profileCache
     *
     * @param cacheSize       the size of the profile cache
     * @param refreshInterval the expiry time in mins
     * @return the singleton instance of the profile cache
     */
//    public static KnProfileCache getInstance(int cacheSize, int refreshInterval) {
//        if (instance == null) {
//            MAX_ENTRIES = cacheSize;
//            expiryTime = refreshInterval * 1000; //converted into millisec
//            instance = new KnProfileCache();
//        }//end of null check//
//        knLogger.debug( "getInstance()", "Cache size ->" + cacheSize +
//                ", Refreh interval ->" + refreshInterval);
//        return instance;
//    }

    /**
     * This method returns the expiry time period of the profile objects.
     * This is a global configuration across all the profile objects in the cache.
     *
     * @return the expiryTime in mins
     */
    public static long getExpiryTime() {
        return expiryTime;
    }

    /**
     * This method puts an entry into the profile cache
     *
     * @param profileDTO the profile object
     */
    public void addProfileToCache(KnProfileDTO profileDTO) {
        String methodName = "addProfileToCache(profileDTO)";
        knLogger.debug( methodName, "Adding profile to cache - " + userVsProfileCache);
        userVsProfileCache.put(profileDTO.getMdn(), profileDTO);
        knLogger.debug( methodName, "Added profile to cache - " + userVsProfileCache);
    }

    /**
     * This method returns the profile object from the profile cache
     * against the userName if found else returns null
     *
     * @param userName the userName associated with the profile
     * @return the profile object
     */
    public <P extends KnProfileDTO> P getProfileFromCache(String userName) {
        //noinspection unchecked
        return (P) userVsProfileCache.get(userName);
    }

    /**
     * This method removes the entry of the profile object from the cache
     * if any entry found against the userName
     *
     * @param userName the userName associated with the profile
     * @return the profile object
     */
    public <P extends KnProfileDTO> P deleteProfileFromCache(String userName) {
        //noinspection unchecked
        return (P) userVsProfileCache.remove(userName);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(1000);
        sb.append("CacheMAp - " + userVsProfileCache);
        return sb.toString();
    }

}
