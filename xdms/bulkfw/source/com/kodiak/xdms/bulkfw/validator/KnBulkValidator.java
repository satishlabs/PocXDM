/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.bulkfw.validator;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnBulkValidator.java
 * Subsystem:  XDMS-BulkFrameWork
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Chandrashekar H S     08/07/2015    8.0
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
import com.kodiak.common.exception.KnException;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.bulkfw.KnJobStatusObserver;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class KnBulkValidator {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkValidator.class);
    private KnPAMBulkValidator knPAMBulkValidator;
    private KnUpdateBanValidator knUpdateBanValidator;

    private static KnBulkValidator instance;

    public static KnBulkValidator getInstance() {
        if (instance == null)
            instance = new KnBulkValidator();
        return instance;
    }

    private KnBulkValidator() {
        knPAMBulkValidator = KnPAMBulkValidator.getInstance();
        knUpdateBanValidator = KnUpdateBanValidator.getInstance();
    }

    /**
     * Validates the job list against Free slots available and returns the Jobs that can be executed
     * @param jobList
     * @return
     * @throws KnException
     */
    public List<KnBulkDTO> validate(ConcurrentLinkedDeque<KnBulkDTO> jobList) throws KnException {
        List<KnBulkDTO> validatedPamJobs = new ArrayList<>();
        List<KnBulkDTO> combinedJobs = new ArrayList<>();
        List<KnBulkDTO> pamJobs = new ArrayList<>();
        List<KnBulkDTO> updateJobs = new ArrayList<>();
        List<KnBulkDTO> jobsForExcecution = new ArrayList<>();
        List<KnBulkDTO> validatedUpdateBanJobs = new ArrayList<>();
        String methodName = "validate(LinkedList<KnBulkDTO> jobList)";
        Map<Integer, KnBulkDTO> runningJobs = KnJobStatusObserver.getRunningJobs();

        int freeSlots = (KnBulkFwConstants.MAX_ALLOWED_BATCHES - runningJobs.size());
        knLogger.info(methodName, "jobList", jobList.size(), "Running jobs", runningJobs.size(), "availabe freeSlots", freeSlots);
        if (freeSlots > 0 ) {
            for (KnBulkDTO bulkOrderInfo : jobList) {
                int bulkOrderType = bulkOrderInfo.getBulkOrderType();
                knLogger.debug(methodName, "bulkOrderType", bulkOrderType);
                if ((bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_CREATE.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DELETE.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_ROLLBACK.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DOWNGRADE.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_REACTIVATE.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_SUSPEND.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_SUSPEND_ROLLBACK.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_SUSPEND_ROLLBACK.value()))){
                    pamJobs.add(jobList.pollFirst());
                } else if ( (bulkOrderType == KnBulkFwConstants.BULK_ORDER_TYPE.UPDATE_BAN.value())) {
                    updateJobs.add(jobList.pollFirst());
                }
            }
            knLogger.debug(methodName, "PamJobs For Validation", pamJobs.size());
            validatedPamJobs = knPAMBulkValidator.validatePam(pamJobs);
            validatedUpdateBanJobs = knUpdateBanValidator.validate(updateJobs);
        }
        if (validatedPamJobs != null)
            combinedJobs.addAll(validatedPamJobs);
        if (validatedUpdateBanJobs != null)
            combinedJobs.addAll(validatedUpdateBanJobs);
        knLogger.info(methodName, "combinedJobs after validation", combinedJobs.size());
        //Adds all the validated jobs if number of validated jobs are less than freeslots
        if (combinedJobs.size() <= freeSlots) {
            jobsForExcecution.addAll(combinedJobs);
        } else {
            Iterator<KnBulkDTO> iterator = combinedJobs.iterator();
            while (iterator.hasNext()) {
                KnBulkDTO bulkOrderInfo = iterator.next();
                int priority = bulkOrderInfo.getPriority();
                int bulkOrderType = bulkOrderInfo.getBulkOrderType();
                //Add priority jobs to ready for execution list
                if (priority == 1 && jobsForExcecution.size() < freeSlots) {
                    jobsForExcecution.add(bulkOrderInfo);
                }
                if ((jobsForExcecution.size() < freeSlots ) && (bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_CREATE.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DELETE.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_ROLLBACK.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_DOWNGRADE.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_REACTIVATE.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_PAM_SUSPEND.value()))
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_SUSPEND_ROLLBACK.value())
                        || bulkOrderType == (KnBulkFwConstants.BULK_ORDER_TYPE.SUBS_SUSPEND_ROLLBACK.value())){
                    jobsForExcecution.add(bulkOrderInfo);
                    knLogger.debug(methodName, "PAmJobs for Execution Added", bulkOrderInfo.getBulkOrderId());
                    iterator.remove();
                }if(jobsForExcecution.size() == freeSlots){
                    break;
                }
            }
            // If Still free slots available than add remaining jobs to exceution
            Iterator<KnBulkDTO> iterator1 = combinedJobs.iterator();
            while (iterator1.hasNext()) {
                KnBulkDTO bulkOrderInfo = iterator1.next();
                if (jobsForExcecution.size() <= freeSlots) {
                    jobsForExcecution.add(bulkOrderInfo);
                    iterator1.remove();
                }
                if(jobsForExcecution.size() == freeSlots){
                    break;
                }
            }
        }
        knLogger.info(methodName, "The jobs ready for execution after filtering combined jobs from pam and notification", jobsForExcecution.size());
        return jobsForExcecution;
    }
}