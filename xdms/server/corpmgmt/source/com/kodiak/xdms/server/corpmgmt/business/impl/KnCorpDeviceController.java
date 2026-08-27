/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.commdto.common.KnXDMDeviceAddlInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.corpmgmt.business.ICorpDeviceController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPDeviceInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceInfoRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpDeviceListRespDTO;
import com.kodiak.common.commdto.response.KnDeviceDetailsDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.LIBRARY_NAME_CORP_MGMT;
import static com.kodiak.xdms.server.common.resources.KnConstants.MAX_ATTEMPT_TO_DECODE;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;

public class KnCorpDeviceController implements ICorpDeviceController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpDeviceController.class);

    private KnValidatorFramework validatorFW;
    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnGeneralUtil generalUtil;

    public KnCorpDeviceController() {
        contactInfoUtil = new KnCorpContactInfoUtil();
        commonInfoUtil = new KnCorpCommonInfoUtil();
        generalUtil = new KnGeneralUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
    }

    @Override
    public KnCorpDeviceListRespDTO getDeviceList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getDeviceList(KnIPCorpInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", corpInfoDTO);
        //Step:
        //creating Response DTO object
        KnCorpDeviceListRespDTO respDTO = new KnCorpDeviceListRespDTO();
        List<KnDeviceDetailsDTO>  deviceDetails;
        Map<String, KnXDMDeviceAddlInfoDTO> deviceAddInfoMap;
        List<String> deviceIds;
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
            //knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            //validatorFW.validate(persistDTO);
            //knLogger.info(methodName, "Validation completed Successfully.", persistDTO);

            knLogger.debug(methodName, "Before DB call to get the Device List");
            switch (corpInfoDTO.getFilterType()) {
                case 1:
                    // Return List of All Device details
                    deviceDetails = commonInfoUtil.getDeviceList(corpId, corpInfoDTO.getFilterType(), corpInfoDTO.getFetchSize(), corpInfoDTO.getNextToken(), xdmsHomePttId, true, persisterTxn);
                    deviceIds = deviceDetails.stream().map(KnDeviceDetailsDTO::getDeviceId).toList();
                    deviceAddInfoMap = commonInfoUtil.getDeviceAddInfoMap(deviceIds, xdmsHomePttId,  persisterTxn);
                    for (KnDeviceDetailsDTO deviceDetail : deviceDetails) {
                        if (commonInfoUtil.lmrInterFlagValidate(deviceDetail.getSubscriberFs2())) {
                            deviceDetail.setUgwInterop("1");
                        } else if (deviceDetail.getSubscriberFs2() == null || deviceDetail.getSubscriberFs2().isEmpty()) {
                            deviceDetail.setUgwInterop("0");
                        } else {
                            deviceDetail.setUgwInterop("0");
                        }
                        var additionalInfo = deviceAddInfoMap.get(deviceDetail.getDeviceId());
                        if (additionalInfo != null) {
                            deviceDetail.setDeviceAddlInfo(additionalInfo);
                        }
                    }
                    respDTO.setDeviceInfoList(deviceDetails);
                    break;
                case 2:
                    // Return List of Enabled Interop Device Details
                    deviceDetails = commonInfoUtil.getDeviceList(corpId, corpInfoDTO.getFilterType(), corpInfoDTO.getFetchSize(), corpInfoDTO.getNextToken(), xdmsHomePttId, true, persisterTxn);
                    deviceIds = deviceDetails.stream().map(KnDeviceDetailsDTO::getDeviceId).toList();
                    deviceAddInfoMap = commonInfoUtil.getDeviceAddInfoMap(deviceIds, xdmsHomePttId,  persisterTxn);
                    List<KnDeviceDetailsDTO> deviceDtl = new ArrayList<>();
                    for (KnDeviceDetailsDTO deviceDetail : deviceDetails) {
                        if (commonInfoUtil.lmrInterFlagValidate(deviceDetail.getSubscriberFs2())) {
                            deviceDetail.setUgwInterop("1");
                            var additionalInfo = deviceAddInfoMap.get(deviceDetail.getDeviceId());
                            if (additionalInfo != null) {
                                deviceDetail.setDeviceAddlInfo(additionalInfo);
                            }
                            deviceDtl.add(deviceDetail);
                        }
                    }
                    respDTO.setDeviceInfoList(deviceDtl);
                    break;
            }
            int deviceCount = commonInfoUtil.getCorpDeviceCount(corpId, xdmsHomePttId, true, persisterTxn);
            respDTO.setCount(deviceCount);
            populate(respDTO);
        /**} catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while retrieving MasterList - ", e);
            populate(respDTO, e);
         */
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
    public KnCorpDeviceInfoRespDTO getDeviceDetails(KnIPDeviceInfoDTO deviceInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException, KnCorpBOException {
        String methodName = "getDeviceDetails(KnIPCorpInfoDTO, KnPersisterTxn)";

        KnCorpDeviceInfoRespDTO knXDMDeviceProvDTO = new KnCorpDeviceInfoRespDTO();
        String corpId = deviceInfoDTO.getCorpId();
        knLogger.debug(methodName, "Corp Id passed in the request is - ", corpId);

        try {
        //Step:
        //get the Corp profile details from cache
        knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
        KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails((corpId),
                CORP_PROFILE, false, persisterTxn);
        knLogger.info(methodName, "Corporate Profile - ", corpProfile);

        //use the xdms home pttServerId from Corp profile
        String xdmsHomePttId = corpProfile.getXdmsHome();

            knXDMDeviceProvDTO = commonInfoUtil.getDeviceDetails(deviceInfoDTO, xdmsHomePttId, true, persisterTxn);

            if (knXDMDeviceProvDTO.getDeviceAddlInfo() != null && knXDMDeviceProvDTO.getDeviceAddlInfo().getDeviceInfo() != null) {
                String decodedHexDeviceInfo = KnGeneralUtil.decodeHexDeviceInfo(knXDMDeviceProvDTO.getDeviceAddlInfo().
                        getDeviceInfo().trim(), MAX_ATTEMPT_TO_DECODE);
                knXDMDeviceProvDTO.getDeviceAddlInfo().setDeviceInfo(decodedHexDeviceInfo);
            }

            if(knXDMDeviceProvDTO != null && knXDMDeviceProvDTO.getDeviceInfo() != null) {
            if (commonInfoUtil.lmrInterFlagValidate(knXDMDeviceProvDTO.getDeviceInfo().getSubscriberFs2())) {
                knXDMDeviceProvDTO.getDeviceInfo().setUgwInterop("1");
            } else if (knXDMDeviceProvDTO.getDeviceInfo().getSubscriberFs2() == null || knXDMDeviceProvDTO.getDeviceInfo().getSubscriberFs2().isEmpty()) {
                knXDMDeviceProvDTO.getDeviceInfo().setUgwInterop("0");
            } else {
                knXDMDeviceProvDTO.getDeviceInfo().setUgwInterop("0");
            }
        }
            populate(knXDMDeviceProvDTO);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving Device Details - ", e);
            populate(knXDMDeviceProvDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving Device Details - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(knXDMDeviceProvDTO, e);
        }
        return knXDMDeviceProvDTO;
    }
}
