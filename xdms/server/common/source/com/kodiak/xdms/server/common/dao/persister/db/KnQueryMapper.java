/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnQueryMapper.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        25-01-2011      7.0
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
package com.kodiak.xdms.server.common.dao.persister.db;

import com.kodiak.logger.KnLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class KnQueryMapper implements IQueryMapper {

    private static KnQueryMap queryMap;
    private static KnQueryMapper queryMapper = new KnQueryMapper();

    private KnQueryMapper() {
        queryMap = new KnQueryMap();
    }

    public static KnQueryMapper getInstance() {
        return queryMapper;
    }

    public String getQuery(String id) {
        return queryMap.getQuery(id);
    }
}

class KnQueryMap {
    private static final KnLogger knLogger = KnLogger.getLogger(KnQueryMap.class);

    private static final String CLASS = KnQueryMap.class.getName();
    private static Map<String, KnQueryObject> queryMap;
    private static String sqlMapFile = "sql-map.xml";
    private KnQueryMapLoader loader = null;

    KnQueryMap() {
        queryMap = new ConcurrentHashMap<String, KnQueryObject>();
        loadSqlMapFile();
    }

    /**
     * Private method to load the configuration file required
     * for this class to perform the validation.
     */
    private synchronized void loadSqlMapFile() {
        String methodName = "loadSqlMapFile()";
        knLogger.info( methodName, "Initializing SQL Mappper with config file: "
                + sqlMapFile);
        if (loader == null) {
            knLogger.info( methodName, "Loader is null " + queryMap);
            loader = new KnQueryMapLoader(sqlMapFile, queryMap);
        }
        try {
            knLogger.debug( methodName, "Load the config file " + queryMap);
            loader.load();
            //now, we are ready ...
            knLogger.info( methodName, "Initialized Sql Mapper with contents: " + queryMap);
        } catch (Exception e) {
            knLogger.fatal( methodName, "Exception occured: " + e);
        }
    }


    public String getQuery(String id) {
        KnQueryObject obj = queryMap.get(id);
        if (obj != null) {
            return obj.getQuery();
        }
        return null;
    }
}

class KnQueryObject {

    private String id;
    private String query;

    KnQueryObject(String id, String query) {
        this.id = id;
        this.query = query;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}