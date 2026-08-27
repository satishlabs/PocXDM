 /*
  ************************************************************************************************
  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
  * All Rights Reserved
  * Motorola Solutions Confidential Restricted
  ************************************************************************************************
  * File name:   KnRamJobStaleJobMonitor.java
  * Subsystem:   POC
  * Description: This script is used to check the stale Ram jobs and clean them if they have missed all the triggers
  * Name                     Date             Release
  * --------------------   ----------      ---------------------------------------------
  * Sunil Biradar          21/11/2024         12.3.1.2
  *
  * This software is the confidential and proprietary information of Kodiak Networks, Inc.
  *
  * You shall not disclose such confidential information and shall use it only in accordance with
  * the terms of the license agreement you entered into with Kodiak Networks.
  ************************************************************************************************
  */
 package com.kodiak.xdms.mediator.resources.jobs;

 import com.kodiak.frameworks.jobscheduler.impl.KnJobSchedulerImpl;
 import com.kodiak.logger.KnLogger;

 /**
  * @author Sunil Biradar
  * Below class defines the way of RAMJob store monitoring
  */
 public class KnRamJobStaleJobMonitor implements Runnable {

     private static final KnLogger knLogger = KnLogger.getLogger(KnRamJobStaleJobMonitor.class);
     public static KnRamJobStaleJobMonitor instance;
     //The job scheduler
     private static KnJobSchedulerImpl scheduler;

     public static KnRamJobStaleJobMonitor getInstance() {
         if (instance == null) {
             instance = new KnRamJobStaleJobMonitor();
         }
         scheduler = KnJobSchedulerImpl.getInstance();
         return instance;
     }

     @Override
     public void run() {
         doMonitor();
     }

     private void doMonitor() {
         String methodName = "doMonitor()";
         knLogger.info(methodName, "RAM Job Store Monitor");
         try {
             scheduler.getStaleAndClean();
         } catch (Exception e) {
             knLogger.error(methodName, "Error in checking stale jobs", e);
         } catch (Throwable t) {
             knLogger.error(methodName, "Throwable Error while checking stale jobs", t);
         }
     }

 }