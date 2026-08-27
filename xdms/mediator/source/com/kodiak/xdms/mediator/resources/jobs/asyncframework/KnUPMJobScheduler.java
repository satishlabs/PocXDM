/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.resources.jobs.asyncframework;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class KnUPMJobScheduler {

    private static final KnLogger knLogger = KnLogger.getLogger(KnUPMJobScheduler.class);

    public static KnUPMJobScheduler INSTANCE = new KnUPMJobScheduler();

    private KnGeneralCacheUtil generalCacheUtil;


    private KnUPMJobScheduler(){
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
    }


    public boolean addJob(KnAsyncJobDTO asyncJobDTO) {
        String methodName = "addJob(KnAsyncJobDTO)";
        boolean result = false;
        knLogger.info(methodName,"Entry:-");
        try {
            generalCacheUtil.createAsyncJob(asyncJobDTO);
            knLogger.info(methodName,"Job Created Successfully:-");
            result = true;
        } catch (KnDAOException e) {
           knLogger.error(methodName,"exception while creating job - ",e.getMessage());
        }

        return result;
    }

    public boolean removeJob(Collection<String> txnIds) {
        String methodName = "removeJob(Collection<String> )";
        boolean result = false;
        knLogger.info(methodName,"Entry:-",txnIds);
        try {
            generalCacheUtil.deleteAsyncJob(txnIds);
            knLogger.info(methodName,"Job Deleted Successfully:-");
            result = true;
        } catch (KnDAOException e) {
            knLogger.error(methodName,"exception while deleting job - ",e.getMessage());
        }

        return result;
    }

    public static synchronized KnUPMJobScheduler getInstance(){
        knLogger.debug("getInstance","returning UPMJobScheduler Instance");
        return INSTANCE;
    }

}
