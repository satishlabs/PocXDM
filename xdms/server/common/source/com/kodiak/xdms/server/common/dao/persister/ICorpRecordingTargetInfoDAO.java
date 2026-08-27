/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dao.persister;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.dto.common.KnRecordingTargetInfoDTO;

import java.util.Collection;
import java.util.Map;

/**
 * ************************************************************************
 * <p>
 * File name:  ICorpRecordingTargetInfoDAO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Sudhendu K Nayak           25-january-2023              12.3
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 * <p>
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2022 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */

public interface ICorpRecordingTargetInfoDAO {

    public void createRecordingInfoForTarget(Collection<KnRecordingTargetInfoDTO> recordingTargetInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void updateRecordingInfoTargetByTarget(Map<String, Integer> targetRecTypeMap, KnPersisterTxn persisterTxn) throws KnDAOException;

    public void deleteRecordingInfoTargetByTarget(Collection<String> target, KnPersisterTxn persisterTxn) throws KnDAOException;

    public Collection<KnRecordingTargetInfoDTO> getRecordingInfoTargetByTarget(Collection<String> targetList, KnPersisterTxn persisterTxn) throws KnDAOException;
}
