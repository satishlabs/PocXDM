/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *********************************************************************
 * File name:   IXcapDiffNotifierIntf.java
 * Subsystem:   XCAP Notifier
 * <p/>
 * Name                  Date         Release
 * -----------------    -----------   -------
 * Ravi Shanker .P       2/20/11   7.0
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
 * *************************************************************************
 */
package com.kodiak.xdms.notificationmgr;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.notificationmgr.beans.KnSEHNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.common.commdto.common.KnNotificationParamDTO;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public interface IXcapDiffNotifierIntf {

    /**
     * This method sets the max notifications thresold per Job
     *
     * @param maxNotfnsPerJob the max notifications thresold per Job
     */
    public void setMaxNotfnsPerJob(int maxNotfnsPerJob);

    /**
     * This method sets the thresold for processing notifications in main thread
     *
     * @param syncNotfyThresold the thresold for processing notifications in main thread
     */
    public void setSyncNotfyThresold(int syncNotfyThresold);

    /**
     * method to send the list of Directory change notifications
     *
     * @param xcapDiffNotifyDTOs Collection of KnXcapDiffNotifyDTO
     * @param persisterTxn       KnPersisterTxn DB Transaction Object
     * @return boolean value which indicates the notification status.
     */
    public boolean sendXcapDiffNotifications(Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffNotifyDTOs,
                                             KnPersisterTxn persisterTxn);

    public boolean sendXcapDiffNotifications(List<KnXcapDiffNotifyDTO> xcapDiffNotifyDTOs);

    /**
     * This method is to send the subscriber SEH Notification
     * @param sehNotifyDTOs  List of KnSEHNotifyDTO
     * @return  boolean value which indicates the notification status.
     */
    public boolean sendSEHNotifications(List<KnSEHNotifyDTO> sehNotifyDTOs);

    public boolean sendXcapDiffNotifications(KnXcapDiffNotifyDTO xcapDiffNotifyDTO);

    public boolean sendXcapDiffNotifications(KnXcapDiffNotifyDTO xcapDiffNotifyDTO, KnNotificationParamDTO notificationParamDTO);

    boolean sendXcapDiffNotifications(Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList, KnPersisterTxn persisterTxn, KnNotificationParamDTO notificationParamDTO);

    public boolean sendXcapDiffNotifications(List<KnXcapDiffNotifyDTO> xcapDiffNotifyDTOs, KnNotificationParamDTO notificationParamDTO);

    public void sendEtagMcsNotifications(Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffDirChgNotifyDTOS);

    public void sendEtagMcsNotifications(LinkedHashSet<String> mdnSet);

}
