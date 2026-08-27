/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.tool.memorymonitor;

import com.kodiak.tool.memorymonitor.db.KnRaiseAlarm;
import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.MemoryUsage;
import java.util.Map;
import java.util.logging.Level;

public class KnGCNotificationListener implements NotificationListener {
    long totalGcDuration = 0;
    GcInfo previousGcInfo = null;
    GcInfo currenGcInfo = null;

    @Override
    public void handleNotification(Notification notification, Object paramObject) {
        if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
            //get the information associated with this notification
            GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
            //get all the info and pretty print it
            long duration = info.getGcInfo().getDuration();
            String gctype = info.getGcAction();
            if ("end of minor GC".equals(gctype)) {
                gctype = "Young Gen GC";
            } else if ("end of major GC".equals(gctype)) {
                gctype = "Old Gen GC";
            }
            KnMemoryMonitor.logger.log(Level.INFO, gctype + ": - " + info.getGcInfo().getId() + " " + info.getGcName() + " (from " + info.getGcCause() + ") " + duration + " microseconds; start-end times " + info.getGcInfo().getStartTime() + "-" + info.getGcInfo().getEndTime());
            Map<String, MemoryUsage> membefore = info.getGcInfo().getMemoryUsageBeforeGc();
            Map<String, MemoryUsage> mem = info.getGcInfo().getMemoryUsageAfterGc();
            /* for (Entry<String, MemoryUsage> entry : mem.entrySet()) {
            String name = entry.getKey();
            MemoryUsage memdetail = entry.getValue();
            long memInit = memdetail.getInit();
            long memCommitted = memdetail.getCommitted();
            long memMax = memdetail.getMax();
            long memUsed = memdetail.getUsed();
            MemoryUsage before = membefore.get(name);
            long beforepercent = ((before.getUsed()*1000L)/before.getCommitted());
            long percent = ((memUsed*1000L)/before.getCommitted()); //>100% when it gets expanded
            KnMemoryMonitor.logger.log(Level.INFO, name + (memCommitted==memMax?"(fully expanded)":"(still expandable)") +"used: "+(beforepercent/10)+"."+(beforepercent%10)+"%->"+(percent/10)+"."+(percent%10)+"%("+((memUsed/1048576)+1)+"MB) / ");
            }
          totalGcDuration += info.getGcInfo().getDuration();
          long percent = totalGcDuration*1000L/info.getGcInfo().getEndTime();
          System.out.println();
          KnMemoryMonitor.logger.log(Level.INFO, "GC cumulated overhead "+(percent/10)+"."+(percent%10)+"%");
            System.out.println("GC cumulated overhead "+(percent/10)+"."+(percent%10)+"%");*/
            monitorGC(info.getGcInfo());
        }
    }

    private void monitorGC(GcInfo currentGC) {
        if (previousGcInfo == null) {
            previousGcInfo = currentGC;
        } else {
            long previousendTime = previousGcInfo.getEndTime();
            long currentstartTime = currentGC.getStartTime();
            KnMemoryMonitor.logger.log(Level.INFO, "previousendTime :" + previousendTime);
            KnMemoryMonitor.logger.log(Level.INFO, "currentstartTime :" + currentstartTime);
            KnMemoryMonitor.logger.log(Level.INFO, "diff :" + (currentstartTime - previousendTime));
            if (3000000 >= (currentstartTime - previousendTime)) {
                KnMemoryMonitor.logger.log(Level.INFO, "raise an alaram");
                KnRaiseAlarm.getInstance().generateAlarmUtil(KnRaiseAlarm.OUT_OF_MEMORY, KnRaiseAlarm.ALARM_SEVERITY.CRITICAL);
            } else {
                KnMemoryMonitor.logger.log(Level.INFO, "Assaigning to previous GC");
                previousGcInfo = currentGC;
            }
        }
    }

}
