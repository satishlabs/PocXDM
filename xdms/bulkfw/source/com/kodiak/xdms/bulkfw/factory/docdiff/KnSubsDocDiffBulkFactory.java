/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:  KnSubsDocDiffBulkFactory.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ravi Shanker P       Oct 1, 2012   7.4
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
 * ************************************************************************/
package com.kodiak.xdms.bulkfw.factory.docdiff;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.statusmgr.utils.KnStatusMgrConstants.CARD_STATES;
import com.kodiak.xdms.bulkfw.dao.KnXDMBulkOrderInfoDAO;
import com.kodiak.xdms.bulkfw.dto.KnBulkDTO;
import com.kodiak.xdms.bulkfw.dto.KnBulkOrderDTO;
import com.kodiak.xdms.bulkfw.dto.KnBulkOrderRespDTO;
import com.kodiak.xdms.bulkfw.factory.IBulkOrderFactory;
import com.kodiak.xdms.bulkfw.resources.KnBulkDocDiffConstants;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwDocDiffException;
import com.kodiak.xdms.bulkfw.resources.KnBulkFwUtil;
import com.kodiak.xdms.bulkfw.util.KnBulkFwNotifyStatus;

import java.util.Calendar;

/**
 * Class that will receives the Request for the performing the bulk operations
 * for Subscriber config doc diff notifications
 *
 * @author Ravi Shanker .P
 */
public class KnSubsDocDiffBulkFactory implements IBulkOrderFactory {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsDocDiffBulkFactory.class);
    private final String className = KnSubsDocDiffBulkFactory.class.getName();
    private String pttServerId = null;
    private KnBulkFwUtil bulkFwUtil = null;
    private CARD_STATES redundancyStatus;

    public KnSubsDocDiffBulkFactory() {
        init();
    }

    private void init() {
        pttServerId = KnDbUtil.getDBConfigInfo().getLocalPttId();
        bulkFwUtil = KnBulkFwUtil.getInstance();
        redundancyStatus = KnBulkFwNotifyStatus.getInstance().getCurrentRedundancyStatus();
    }

	/*
     * (non-Javadoc)
	 * 
	 * @see
	 * com.kodiak.frameworks.bulkfw.factory.IBulkOrderFactory#performBulkOp(
	 * com.kodiak.frameworks.bulkfw.dto.KnBulkOrderDTO)
	 */

    public KnBulkOrderRespDTO performBulkOp(KnBulkOrderDTO bulkOrderDTO) {
        String methodName = "performBulkOp(KnBulkOrderDTO)";
        knLogger.debug(methodName, "ENTRY: perform Bulk Op ", bulkOrderDTO);
        KnPersisterTxn persisterTxn = null;
        KnBulkOrderRespDTO respDTO = new KnBulkOrderRespDTO();
        try {

            if (redundancyStatus != CARD_STATES.ACTIVE) {
                knLogger.error(methodName, "Card is not in Active State");
                throw new KnException(KnBulkDocDiffConstants.ErrorCodes.INTERNAL_SERVER_ERROR, "Card is not Active");
            }

            persisterTxn = KnPersisterTxn.getPersisterTxn();
            knLogger.debug(methodName, "opening the transaction");
            persisterTxn.open();

            // validating the input data;
            if (bulkOrderDTO.getChangeLevel() == KnBulkDocDiffConstants.CHANGE_LEVEL.CORPORATE.value() &&
                    (bulkOrderDTO.getCorpId() == null)) {
                knLogger.error(methodName, "Invalid Input received");
                throw new KnBulkFwDocDiffException(KnBulkDocDiffConstants.ErrorCodes.INVALID_INPUT_DATA, "Invalid Input Data");
            }

            // store the received order details into XDM BULK ORDER INFO TABLE
            KnXDMBulkOrderInfoDAO bulkOrderInfoDao = new KnXDMBulkOrderInfoDAO(this.pttServerId);

            KnBulkDTO persistDTO = new KnBulkDTO();

            // Use ID Generator to get the Bulk Order ID.
            int bulkOrderId = bulkFwUtil.retrieveIdForTable(KnBulkDocDiffConstants.BO_INFO_TABLE_NAME, this.pttServerId,
                    KnBulkDocDiffConstants.BULK_ORDER_ID, false);

            persistDTO.setBulkOrderId(bulkOrderId);
            persistDTO.setBulkOrderType(KnBulkDocDiffConstants.BULK_ORDER_TYPE.CONFIG_DOC_DIFF.value());
            persistDTO.setChangeLevel(bulkOrderDTO.getChangeLevel());
            persistDTO.setCorpId(bulkOrderDTO.getCorpId());
            persistDTO.setBulkOrderObj(bulkOrderDTO.getBulkOrderObj());
            persistDTO.setBulkOrderObjVersion(bulkOrderDTO.getBulkOrderObjVersion());
            Long insertionTime = Calendar.getInstance().getTimeInMillis();
            persistDTO.setInsertionTime(insertionTime);
            persistDTO.setStatus(KnBulkDocDiffConstants.STATUS.NOT_STARTED.value());

            knLogger.debug(methodName, "calling dao with DTO ", persistDTO);
            bulkOrderInfoDao.insert(persistDTO, persisterTxn);
            knLogger.debug(methodName, "Created bulk Order");

//			// update the data in the BO_CONFIG_INFO Table
//			KnBoSubsconfigDocDiffDTO subsConfigDTO = new KnBoSubsconfigDocDiffDTO();
//			subsConfigDTO.setBulkOrderId(bulkOrderId);
//			// TODO need to generate the delta xml object and set it here.
//			subsConfigDTO.setBulkOrderObj(bulkOrderDTO.getBulkOrderObj());
//			subsConfigDTO.setStatus(KnBulkDocDiffConstants.STATUS.NOT_STARTED.value());
//
//			KnXDMBOConfigDocDAO configDocDao = new KnXDMBOConfigDocDAO(pttServerId);
//			configDocDao.insert(subsConfigDTO, persisterTxn);

            knLogger.debug(methodName, "saving the transaction");
            persisterTxn.save();

            respDTO.setReqStatus(KnBulkDocDiffConstants.REQUEST_STATUS.SUCCESS);
            respDTO.setBulkOrderId(bulkOrderId);
            respDTO.setResponseMsg("Bulk Order Request is received successfully");

        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception Occurred");
            knLogger.error(methodName, e);
            KnDbUtil.rollback(persisterTxn);
            populateErrorRespDTO(respDTO, e);
        } catch (KnException e) {
            knLogger.error(methodName, "Exception Occurred");
            knLogger.error(methodName, e);
            KnDbUtil.rollback(persisterTxn);
            populateErrorRespDTO(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception Occurred");
            knLogger.error(methodName, e);
            KnDbUtil.rollback(persisterTxn);
            populateErrorRespDTO(respDTO, e);
        }

        return respDTO;
    }

    /**
     * @param respDTO
     * @param e
     */
    private void populateErrorRespDTO(KnBulkOrderRespDTO respDTO, Exception e) {
        respDTO.setReqStatus(KnBulkDocDiffConstants.REQUEST_STATUS.FAILURE);
        if (e instanceof KnException) {
            respDTO.setResponseMsg(((KnException) e).getErrorMessage());
        } else {
            respDTO.setResponseMsg("Internal Server Error");
        }
    }
}
