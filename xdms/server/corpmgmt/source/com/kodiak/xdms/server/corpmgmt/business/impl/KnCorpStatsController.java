/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.commdto.response.KnCORPGroupStatsRespDTO;
import com.kodiak.common.commdto.response.KnDeviceDetailsDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.corpmgmt.business.ICorpStatsController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpContactInfoUtil;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceStatsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSubsStatsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnGroupStatsRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.List;

import static com.kodiak.xdms.server.common.resources.KnConstants.LIBRARY_NAME_CORP_MGMT;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;

//import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSubsStatsRespDTO;


public class KnCorpStatsController implements ICorpStatsController {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpStatsController.class);

    private KnValidatorFramework validatorFW;
    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnGeneralUtil generalUtil;

    public KnCorpStatsController() {
        contactInfoUtil = new KnCorpContactInfoUtil();
        commonInfoUtil = new KnCorpCommonInfoUtil();
        generalUtil = new KnGeneralUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
    }

    @Override
    public KnGroupStatsRespDTO getGroupStats(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getGroupStats(KnIPCorpInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", corpInfoDTO);
        //Step:
        //creating Response DTO object
        KnGroupStatsRespDTO respDTO = new KnGroupStatsRespDTO();

        List<KnCORPGroupStatsRespDTO> respDTO1 = null;

        try {
            int corpId = corpInfoDTO.getCorpId();
            knLogger.debug(methodName, "Corp Id passed in the request is - ", corpId);

            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            String extCorpID = corpProfile.getExtCorpId();

            KnCorpInfoPersistDTO persistDTO = new KnCorpInfoPersistDTO();
            persistDTO.setInputDTO(corpInfoDTO);
            persistDTO.setExtCorpId(extCorpID);
            knLogger.debug(methodName, "Before DB call to get the Device List");

            respDTO1   = commonInfoUtil.getGroupStats(corpId, xdmsHomePttId, true, persisterTxn);

            respDTO.setGroupStats(respDTO1);

            populate(respDTO);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving MasterList - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving MasterList - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }
    @Override
    public KnCorpSubsStatsRespDTO getSubscriberStats(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getSubscriberStats(KnIPCorpInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", corpInfoDTO);
        //Step:
        //creating Response DTO object
        KnCorpSubsStatsRespDTO respDTO = new KnCorpSubsStatsRespDTO();
        try {
            int corpId = corpInfoDTO.getCorpId();
            knLogger.debug(methodName, "Corp Id passed in the request is - ", corpId);

            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            String extCorpID = corpProfile.getExtCorpId();

            KnCorpInfoPersistDTO persistDTO = new KnCorpInfoPersistDTO();
            persistDTO.setInputDTO(corpInfoDTO);
            persistDTO.setExtCorpId(extCorpID);

            knLogger.debug(methodName, "Before DB call to get the Subscriber Stats of Corp");
            // Return Subscribers Stats Information of the Corporate
            respDTO = commonInfoUtil.getSubscriberStats(corpInfoDTO, xdmsHomePttId, true, persisterTxn);
            populate(respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving Subscribers Statistics - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving Subscribers Statistics - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpDeviceStatsRespDTO getDeviceStats(String corpId, KnPersisterTxn persisterTxn) {
        String methodName = "getDeviceStats(corpId, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input Passed - ", corpId);
        //creating Response DTO object
        KnCorpDeviceStatsRespDTO respDTO = new KnCorpDeviceStatsRespDTO();
        try {
            //String corpId = String.valueOf(corpInfoDTO.getCorpId());
            //knLogger.debug(methodName, "Corp Id passed in the request is - ", corpId);

            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile if cached (or) fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId,
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            //String extCorpID = corpProfile.getExtCorpId();

            /*KnCorpInfoPersistDTO persistDTO = new KnCorpInfoPersistDTO();
            persistDTO.setInputDTO(corpId);
            persistDTO.setExtCorpId(extCorpID);*/

            knLogger.debug(methodName, "Before DB call to get the Subscriber Stats of Corp");
            // Return Device Stats Information of the Corporate
            respDTO = commonInfoUtil.getDeviceStats(corpId, xdmsHomePttId, true, persisterTxn);
            populate(respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving Device Statistics - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving Device Statistics - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }
}
