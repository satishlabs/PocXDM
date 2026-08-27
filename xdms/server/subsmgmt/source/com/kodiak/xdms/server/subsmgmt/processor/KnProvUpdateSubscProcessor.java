/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.processor;

import com.kodiak.common.commdto.common.KnSubsCameraInfo;
import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetException;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.common.dto.common.KnPOCSvcConfigDTO;
import com.kodiak.xdms.server.common.dto.common.KnSuppVocoderProfileDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDeviceImpiInfoPersistDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvSMSUtil;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnMCPTTPermInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnProvSMSDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsAliasInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnClientVocoderProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsAddlInfoPersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.common.dao.KnDbUtil.rollback;
import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;

public class KnProvUpdateSubscProcessor {
    private static final KnLogger knLogger = KnLogger.getLogger(KnProvUpdateSubscProcessor.class);
    private KnGenInfoUtil genInfoUtil;
    private String xdmPttServerId;
    KnProvInfoUtil provInfoUtil = null;
    private KnValidatorFramework validatorFwk;
    private KnFeatureSetUtil featureSetUtil;
    private KnGeneralCacheUtil generalCacheUtil;
    private KnProvSMSUtil provSMSUtil;

    public KnProvUpdateSubscProcessor(){
        final String methodName = "KnProvUpdateSubscProcessor Constructor";
        knLogger.info(methodName, "in subs prov update controller");
        genInfoUtil = KnGenInfoUtil.getInstance();
        provInfoUtil = new KnProvInfoUtil();
        validatorFwk = KnValidatorFramework.getInstance(KnProvConstants.LIBRARY_NAME);
        featureSetUtil = KnFeatureSetUtil.getInstance();
        provSMSUtil = new KnProvSMSUtil();
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
    }
    public KnOPUpdateSubsInfoDTO processUpdateSubscriber(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        String methodName = "processUpdateSubscriberMCSIds(KnIPSubsProvInfoDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        KnOPUpdateSubsInfoDTO responseDTO = new KnOPUpdateSubsInfoDTO();
        KnSubsProfilePersistDTO subsProfilePersistDTO;
        Integer isprovFSEnabled= KnConstants.PROVFS_ENABLE.DISABLED.value();
        //same values of provFS1 and provFS1BitMask is used for calculation final subsFS after oring with bitmask
        knLogger.info(methodName, "ENTRY: Update Subscriber with DTO - ", subsProvInputDTO, ", Txn - ", persisterTxn
        );
        Collection<Integer> successPegs = new ArrayList<Integer>();
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            try {
                xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            } catch (KnBOException e) {
                knLogger.error(methodName, "failed to retrieve xdm Ptt Sever Id");
                knLogger.error(methodName, e);
                if (ownedTxn) {
                    rollback(persisterTxn);
                }
                throw new KnProvBOException(KnErrorCodes.BOEntity.XDMS_PTT_ID_NOT_FOUND, "Failed to retrieve XDM PTT Server ID", e);
            }

            KnSubsProfilePersistDTO validatePersistDTO = new KnSubsProfilePersistDTO();
            String mdn = subsProvInputDTO.getMdn();
            Integer reqSubsClientType = subsProvInputDTO.getSubsClientType();
            String reqUserId = subsProvInputDTO.getUserId();

            //retrieve the Subscriber Profile.
            //retrieved Profile will used further for validation input data of the against to the input DTO
            KnOPSubsProfileInfoDTO existingSubsProfileDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            Integer dbSubsClienType = existingSubsProfileDTO.getSubsClientType();
            String dbUserId = existingSubsProfileDTO.getUserId();

            if((reqSubsClientType!=null&& KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value()==reqSubsClientType&&reqUserId!=null)
                    ||(dbSubsClienType!=null&&KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value()==dbSubsClienType&&(dbUserId==null||dbUserId.isEmpty()))){
                subsProvInputDTO.setUserId(reqUserId);
                knLogger.debug(methodName,"setting userid null for dispatcher client :",subsProvInputDTO.getUserId());
            }

            String mcpttId_DB = existingSubsProfileDTO.getMcpttId();
            String mcpttId_Input = subsProvInputDTO.getMcpttIdFromToken();
            knLogger.debug( methodName, "mcpttId_DB::"+ KnGDPRTemplate.mcpttId(mcpttId_DB));
            knLogger.debug( methodName, "mcpttId_Input::"+KnGDPRTemplate.mcpttId(mcpttId_Input));


            if (mcpttId_Input != null && mcpttId_DB != null)
            {
                if (existingSubsProfileDTO.getMcpttCompliance() == 0) {
                    if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty()
                            && !(mcpttId_Input.trim().equals(mcpttId_DB.substring(5).trim()) ||mcpttId_Input.trim().equals(mcpttId_DB.trim()))) {
                        knLogger.debug(methodName, "MCPTTD is not matched with the token");
                        throw new KnProvBOException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
                                "MCPTTD is not matched with the token");
                    }
                } else if (existingSubsProfileDTO.getMcpttCompliance() == 1) {
                    if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty() && !mcpttId_Input.trim().equals(mcpttId_DB.trim())) {
                        knLogger.debug(methodName, "MCPTTD is not matched with the token");
                        throw new KnProvBOException(KnErrorCodes.Validator.INVALID_TOKEN_MCPTTID,
                                "MCPTTD is not matched with the token");
                    }
                }
            }
            if (existingSubsProfileDTO.getSubsClientType() == 11) {
                knLogger.error(methodName, "CLient Type of PDV will not support");
                throw new KnProvBOException(KnErrorCodes.Validator.INVALID_SUBS_CLIENT_TYPE, "subsClientType," + existingSubsProfileDTO.getSubsClientType());
            }
            KnSubsCameraInfo cameraInfo = subsProvInputDTO.getCameraInfo();
            if (existingSubsProfileDTO.getSubsClientType() != KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value()) {
                if(subsProvInputDTO.getCameraType() != null||cameraInfo!=null){
                    knLogger.error(methodName, " Not a STANDALONECAMERA client ");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.NOT_STANDALONECAMERA_CLIENT, "subsClientType," + existingSubsProfileDTO.getSubsClientType());
                }
            }

            validatePersistDTO.setInputDTO(subsProvInputDTO);
            validatePersistDTO.setMdn(existingSubsProfileDTO.getMdn());
            validatePersistDTO.setIMEI(existingSubsProfileDTO.getIMEI());
            validatePersistDTO.setPublicSubscriptionType(subsProvInputDTO.getPublicSubscriptionType());
            validatePersistDTO.setCorporateSubscriptionType(subsProvInputDTO.getCorporateSubscriptionType());
            validatePersistDTO.setPayType(subsProvInputDTO.getPayType());
            validatePersistDTO.setExtCorpId(subsProvInputDTO.getExtCorpId());
            validatePersistDTO.setPairingInd(existingSubsProfileDTO.getPairingInd());
            validatePersistDTO.setServiceAuthStatus(existingSubsProfileDTO.getServiceAuthStatus());
            validatePersistDTO.setRoamingTypes(subsProvInputDTO.getRoamingTypes());
            validatePersistDTO.setSubsClientType(subsProvInputDTO.getSubsClientType());
            validatePersistDTO.setFirstNetIndicator(subsProvInputDTO.getFirstNetIndicator());
            //invoking the validation FW
            validatorFwk.validate(validatePersistDTO);
            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            //validate the proper values while updating the subscription Type
            int oldCorpType = existingSubsProfileDTO.getCorporateSubscriptionType();
            int newCorpType = subsProvInputDTO.getCorporateSubscriptionType();

            int oldPublicType = existingSubsProfileDTO.getPublicSubscriptionType();
            int newPublicType = subsProvInputDTO.getPublicSubscriptionType();

            //variables to identify the profile level changes
            boolean isProfileUpdated = false;
            boolean isActiveFSUpdated = false;
            boolean isNameUpdated = false;
            boolean isSubsTypeUpdated = false;
            boolean isExtCorpIdUpdated = false;
            boolean isCorpNameUpdated = false;
            boolean isClientTypeUpdated = false;
            boolean isDispGrpMemUpdated = false;
            boolean isUFMIChange = false;
            boolean isPkgCodeUpdated = false;
            IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnFactorySelector.DB).createProvXDMServerDAO();
            // retrieve tier pkg & addon pkg
            Map<String, Integer> exsitingPkgIds = new HashMap<String, Integer>();
            Map<String, Integer> exsitingSubsPkgs = new HashMap<String, Integer>();
            String existingTierPkg = null;
            KnOPSubsAddlInfoProfileDTO allProfileDto = xdmServerDAO.retrieveSubscrAddlInfo(subsProvInputDTO.getMdn(),
                    persisterTxn);
            if (allProfileDto != null)
                existingTierPkg = allProfileDto.getTierPkgCode();
            if (existingTierPkg != null)
                exsitingPkgIds.put(existingTierPkg, com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE);
            List<String> existingAddonPkgs = xdmServerDAO.selectSubAddOnPkgs(subsProvInputDTO.getMdn(), persisterTxn);
            if (existingAddonPkgs != null) {
                for (String pkgCode : existingAddonPkgs) {
                    exsitingPkgIds.put(pkgCode, com.kodiak.xdms.server.common.resources.KnConstants.ADDON_PKG_TYPE);
                }
            }
            knLogger.info(methodName, "PkgIdsMap ",
                    subsProvInputDTO.getPkgIdMap() + " exsitingPkgIds " + exsitingPkgIds);
            exsitingSubsPkgs=new HashMap<>(exsitingPkgIds);
            knLogger.info(methodName, " exsitingSubsPkgs " + exsitingSubsPkgs);
            Map<String, Integer> finalPkgIdsMap = new HashMap<String, Integer>();
            List<String> qppPkgCodes=featureSetUtil.getQPPPkgCodes(xdmPttServerId);
            if (subsProvInputDTO.getPkgIdMap() != null) {
                if (subsProvInputDTO.getPkgIdMap().get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION) != null
                        && !subsProvInputDTO.getPkgIdMap().get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION).isEmpty()) {
                    if (subsProvInputDTO.getPkgIdMap().get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION) != null
                            && !subsProvInputDTO.getPkgIdMap().get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION).isEmpty()) {
                        finalPkgIdsMap = exsitingPkgIds;
                        boolean status = false;
                        Map<String, Integer> removePkgIds = subsProvInputDTO.getPkgIdMap()
                                .get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION);
                        for (String pkgCode : removePkgIds.keySet()) {
                            if (!exsitingPkgIds.containsKey(pkgCode.trim())
                                    || (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
                                    .intValue() != removePkgIds.get(pkgCode).intValue())) {
                                throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
                                        "package id not configured as tier or Addon!!");
                            }

                            finalPkgIdsMap.remove(pkgCode.trim(), removePkgIds.get(pkgCode));
                            knLogger.info(methodName, "Pkg code removed  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
                            status = true;
                        }

                        Map<String, Integer> addPkgIds = subsProvInputDTO.getPkgIdMap().get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION);

                        for (String pkgCode : addPkgIds.keySet()) {
                            if (!exsitingPkgIds.containsKey(pkgCode.trim())) {
                                List<String> qppPkgs = qppPkgCodes.stream().filter(x -> exsitingPkgIds.keySet().contains(x))
                                        .collect(Collectors.toList());
                                if(qppPkgs.size() > 0 && qppPkgCodes.contains(pkgCode) )
                                {
                                    throw new KnProvBOException(KnErrorCodes.BOEntity.QPPPKG_ASSIGNED,
                                            "qpp pkg already assign!!");
                                }
                                if (exsitingPkgIds.containsValue(com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE)
                                        && addPkgIds.get(pkgCode).intValue() == com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE) {
                                    throw new KnProvBOException(KnErrorCodes.BOEntity.TIERPKG_ASSIGNED,
                                            "tier pkg already assign!!");
                                }

                                finalPkgIdsMap.put(pkgCode.trim(), addPkgIds.get(pkgCode));
                                knLogger.info(methodName, "Pkg code added  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
                                status = true;
                            }

                            if (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
                                    .intValue() != addPkgIds.get(pkgCode).intValue()) {
                                throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
                                        "package id not configured as tier or Addon!!");
                            }
                        }

                        if (status)
                            isPkgCodeUpdated = true;
                    } else {
                        finalPkgIdsMap = exsitingPkgIds;
                        Map<String, Integer> newPkgIds = subsProvInputDTO.getPkgIdMap().get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION);
                        boolean status = false;
                        for (String pkgCode : newPkgIds.keySet()) {
                            if (!exsitingPkgIds.containsKey(pkgCode.trim())) {
                                List<String> qppPkgs = qppPkgCodes.stream().filter(x -> exsitingPkgIds.keySet().contains(x))
                                        .collect(Collectors.toList());
                                if(qppPkgs.size() > 0 && qppPkgCodes.contains(pkgCode) )
                                {
                                    throw new KnProvBOException(KnErrorCodes.BOEntity.QPPPKG_ASSIGNED,
                                            "qpp pkg already assign!!");
                                }
                                if (exsitingPkgIds.containsValue(com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE)
                                        && newPkgIds.get(pkgCode).intValue() == com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE) {
                                    throw new KnProvBOException(KnErrorCodes.BOEntity.TIERPKG_ASSIGNED,
                                            "tier pkg already assign!!");
                                }

                                finalPkgIdsMap.put(pkgCode.trim(), newPkgIds.get(pkgCode));
                                knLogger.info(methodName, "Pkg code added  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
                                status = true;
                            }

                            if (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
                                    .intValue() != newPkgIds.get(pkgCode).intValue()) {
                                throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
                                        "package id not configured as tier or Addon!!");
                            }
                        }

                        if (status)
                            isPkgCodeUpdated = true;
                    }
                } else {
                    if (subsProvInputDTO.getPkgIdMap().get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION) != null
                            && !subsProvInputDTO.getPkgIdMap().get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION).isEmpty()) {
                        finalPkgIdsMap = exsitingPkgIds;
                        Map<String, Integer> newPkgIds = subsProvInputDTO.getPkgIdMap()
                                .get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION);
                        for (String pkgCode : newPkgIds.keySet()) {
                            if (!exsitingPkgIds.containsKey(pkgCode.trim())
                                    || (exsitingPkgIds.containsKey(pkgCode.trim()) && exsitingPkgIds.get(pkgCode.trim())
                                    .intValue() != newPkgIds.get(pkgCode).intValue())) {
                                throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
                                        "package id not configured as tier or Addon!!");
                            }

                            finalPkgIdsMap.remove(pkgCode.trim(), newPkgIds.get(pkgCode));
                            knLogger.info(methodName, "Pkg code removed  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
                            isPkgCodeUpdated = true;
                        }

                    }
                }

            }
            if (isPkgCodeUpdated) {
                knLogger.info(methodName, "change in pkgCode");
                isProfileUpdated = true;
            }
            //verify if there is a change in the Subscriber Profile if not return an error
            // parameters to be verified are as follows
            // 1. NetworkName, 2. PayType 3. RoamingType 4. SubscriptionType 5. Affiliate Id
            //6. EXT CORP ID, 7. SUBS CLIENT TYPE, 8. EMAIL  9.IMEI

            //verifying if IMEI  is changed
            String newImei = null;
            if (subsProvInputDTO.getIMEI() != null) {
                newImei = subsProvInputDTO.getIMEI().trim();
            }
            String oldImei = null;
            if (existingSubsProfileDTO.getIMEI() != null) {
                oldImei = existingSubsProfileDTO.getIMEI().trim();
            }

            if (newImei != null && (!newImei.equalsIgnoreCase(oldImei))) {
                knLogger.debug(methodName, "change in IMEI");
                isProfileUpdated = true;
            }

            //verifying if firstNetIndicator  is changed
            String newFirstNetIndicator = null;
            if (subsProvInputDTO.getFirstNetIndicator() != null) {
                newFirstNetIndicator = subsProvInputDTO.getFirstNetIndicator().trim();
            }
            String oldFirstNetIndicator = null;
            if (existingSubsProfileDTO.getFirstNetIndicator() != null) {
                oldFirstNetIndicator = existingSubsProfileDTO.getFirstNetIndicator().trim();
            }

            if (newFirstNetIndicator != null && (!newFirstNetIndicator.equalsIgnoreCase(oldFirstNetIndicator))) {
                knLogger.debug(methodName, "change in oldFirstNetIndicator");
                isProfileUpdated = true;
            }

            //verifying if network name is changed
            String oldNetworkName = existingSubsProfileDTO.getNetworkName().trim();
            String newNetworkName = subsProvInputDTO.getNetworkName();
            if (oldNetworkName != null && newNetworkName != null) {
                if (!oldNetworkName.equals(newNetworkName)) {
                    knLogger.debug(methodName, "change in network name");
                    successPegs.add(KnOMConstants.XDM_NUM_SUBSCR_NAME_CHANGE);
                    isNameUpdated = true;
                    isProfileUpdated = true;
                }
            }

            //verify if UFMI has got changed

            String oldUFMI = existingSubsProfileDTO.getUfmi();
            String newUFMI = subsProvInputDTO.getUfmi();

            if (newUFMI != null) {
                if (!newUFMI.equals(oldUFMI)) {
                    knLogger.debug(methodName, "Change in UFMI");
                    isUFMIChange = true;
                }
            }

            String oldIdenUserName = existingSubsProfileDTO.getiDenUserName();
            String newIdenUserName = subsProvInputDTO.getiDenUserName();

            if (newIdenUserName != null) {
                if (!newIdenUserName.equals(oldIdenUserName)) {
                    knLogger.debug(methodName, "Change in idenUserName");
                    isUFMIChange = true;
                }
            }

            String oldIdenPassword = existingSubsProfileDTO.getiDenPassword();
            String newIdenPassword = subsProvInputDTO.getiDenPassword();

            if (newIdenPassword != null) {
                if (!newIdenPassword.equals(oldIdenPassword)) {
                    knLogger.debug(methodName, "Change in idenPassword");
                    isUFMIChange = true;
                }
            }

            String oldIdenBusUnitId = existingSubsProfileDTO.getiDenBusUnitId();
            String newIdenBusUnitId = subsProvInputDTO.getiDenBusUnitId();

            if (newIdenBusUnitId != null) {
                if (!newIdenBusUnitId.equals(oldIdenBusUnitId)) {
                    knLogger.debug(methodName, "Change in idenBusUnitId");
                    isUFMIChange = true;
                }
            }
            //verify if pay Type has got changed
            int oldPayType = existingSubsProfileDTO.getPayType();
            int newPayType = subsProvInputDTO.getPayType();
            if (newPayType != -1) {
                if (oldPayType != newPayType) {
                    knLogger.debug(methodName, "Change in Pay Type");
                    isProfileUpdated = true;
                }
            }

            //verify if roaming Type has got changed
            ArrayList<Integer> newRoamingTypeList = subsProvInputDTO.getRoamingTypes();
            List<Integer> oldRoamingTypes = provInfoUtil.retrieveSubscrRoamingProfile(mdn, persisterTxn);

            Map<Integer, String> roamingTypeInfo = provInfoUtil.retrieveSupportedRoamingList();
            ArrayList<Integer> oldRoamingTypeList = new ArrayList<Integer>();
            for (int oldRoamingId : oldRoamingTypes) {
                if (roamingTypeInfo.get(oldRoamingId) != null) {
                    oldRoamingTypeList.add(Integer.valueOf(roamingTypeInfo.get(oldRoamingId)));
                }
            }


            if (newRoamingTypeList != null) {
                if (!newRoamingTypeList.contains(-1)) {
                    for (int newRoamingType : newRoamingTypeList) {
                        if (!oldRoamingTypeList.contains(newRoamingType) || (oldRoamingTypeList.contains(newRoamingType) && oldRoamingTypeList.size() != newRoamingTypeList.size())) {
                            knLogger.debug(methodName, "Roaming Type change is observed");
                            isProfileUpdated = true;
                        }
                    }
                } else {
                    knLogger.debug(methodName, "Roaming Type change is observed");
                    isProfileUpdated = true;
                }
            }

            //verify if there is a change in the subscription Type

            if ((newPublicType == 0 && newCorpType == 0) || (newPublicType == -1 && newCorpType == -1)) {
                newPublicType = oldPublicType;
                newCorpType = oldCorpType;
            }
            String newExtCorpId = subsProvInputDTO.getExtCorpId();
            String oldAccountId = null;
            String newAccountId = null;
            if (subsProvInputDTO.getAccountId() != null) {
                newAccountId = subsProvInputDTO.getAccountId().trim();
            }
            if (existingSubsProfileDTO.getAccountId() != null) {
                oldAccountId = existingSubsProfileDTO.getAccountId().trim();
            }

            if (newAccountId != null && (!newAccountId.equalsIgnoreCase(oldAccountId))) {
                knLogger.error(methodName, "Account Id cannot be changed ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.ACCOUNT_ID_CHANGE_NOT_ALLOWED,
                        "Account Id Change not Allowed");
            }
            if (newPublicType != -1) {
                if (oldPublicType != newPublicType) {
                    knLogger.debug(methodName, "Public Subscription type has got changed");
                    isSubsTypeUpdated = true;
                    isProfileUpdated = true;
                }
            }

            if (newCorpType != -1) {
                if (oldCorpType != newCorpType) {
                    knLogger.debug(methodName, "Corp Subscription type has got changed");
                    if (newExtCorpId == null && oldAccountId == null) {
                        knLogger.error(methodName, "Ext Corp ID cannot be null for a corporate subscriber");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.EXTCORPID_CANNOT_BE_NULL_FOR_CORP_SUBS,
                                "Ext Corp ID cannot be null for a corporate subscriber");
                    }
                    isSubsTypeUpdated = true;
                    isProfileUpdated = true;
                }
            }
            if ((oldCorpType == newCorpType && oldPublicType != newPublicType)){
                isSubsTypeUpdated = true;
                knLogger.debug(methodName, "Subscription type has got changed");
            }
            else if(!(oldCorpType == newCorpType && oldPublicType == newPublicType))
            {
                isSubsTypeUpdated = false;
                knLogger.error(methodName, "Subscription type change not allowed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIPTION_TYPE_CHANGE_NOT_ALLOWED,
                        "SubscriptionType change is not allowed");
            }



            //verify if there is a change in the Affiliate Id
            String newAffiliateId = null;
            if (subsProvInputDTO.getAffiliateId() != null) {
                newAffiliateId = subsProvInputDTO.getAffiliateId().trim();
            }
            String oldAffiliateId = null;
            if (existingSubsProfileDTO.getAffiliateId() != null) {
                oldAffiliateId = existingSubsProfileDTO.getAffiliateId().trim();
            }

            if (newAffiliateId != null && (!newAffiliateId.equalsIgnoreCase(oldAffiliateId))) {
                knLogger.debug(methodName, "change in affiliate id");
                isProfileUpdated = true;
            }

            String newEMail = subsProvInputDTO.getEmailAddress();
            String oldEMail = existingSubsProfileDTO.getEmailAddress();
            if (newEMail != null && (!newEMail.equalsIgnoreCase(oldEMail))) {
                knLogger.debug(methodName, "change in eMail");
                isProfileUpdated = true;
            }

            int newSubsClientType = existingSubsProfileDTO.getSubsClientType();
            int oldSubsClientType = existingSubsProfileDTO.getSubsClientType();

            if (subsProvInputDTO.getSubsClientType() != null) {
                newSubsClientType = subsProvInputDTO.getSubsClientType();
            }

            int newDispatchGrpMember = existingSubsProfileDTO.getDispatchGroupMember();
            //value will be there whenever there is change
            if (subsProvInputDTO.getDispatchGroupMember() != null) {
                isDispGrpMemUpdated = true;
                newDispatchGrpMember = subsProvInputDTO.getDispatchGroupMember();
                isProfileUpdated = true;
            }


            String newClientPassword = existingSubsProfileDTO.getClientPassword();
            // verify that if the client Type has been changed form HANDSET to PTTRADIOHANDSETCLIENT and vice versa
            boolean isPTTRadioClientTypeChange = true;
            if (oldSubsClientType != newSubsClientType)
            {
                String str = ""+ KnConstants.SUBS_CLIENT_TYPE.HANDSET.value()+"|"+ KnConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()+","+ KnConstants.SUBS_CLIENT_TYPE.CROSSCARRIER.value()+"|"+ KnConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()+","+ KnConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()+"|"+ KnConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()+"";
                String[] regEx1 = str.split(",");
                for(int i=0;i<regEx1.length;i++) {
                    if(String.valueOf(oldSubsClientType).matches(regEx1[i]) && String.valueOf(newSubsClientType).matches(regEx1[i])){
                        isPTTRadioClientTypeChange = false;
                        knLogger.info(methodName, "Client type change skipped");
                    }
                }

            }

            // verify if the client Type has been changed.
            if (oldSubsClientType != newSubsClientType && isPTTRadioClientTypeChange) {
                knLogger.error(methodName, "Client type cannot be changed ");
                throw new KnProvBOException(KnErrorCodes.Validator.INVALID_SUBS_CLIENT_TYPE,"Client type cannot be changed ");
            }

            String corpFS2 = null;
            String pocPttId = existingSubsProfileDTO.getPoCHome();
            String presencePttId = existingSubsProfileDTO.getPresenceHome();
            String xdmsPttId = existingSubsProfileDTO.getXDMSHome();
            String clientFS2=null;
            if (subsProvInputDTO.getClientFS2() != null) {
                clientFS2 = subsProvInputDTO.getClientFS2();
                knLogger.debug(methodName, "change in ClientFS1. generateClientFeatureSet1 :", clientFS2);
                isProfileUpdated = true;
            }
            else {
                clientFS2 = existingSubsProfileDTO.getClientFS2();
            }
            Map<Integer, Integer> provFSMap = subsProvInputDTO.getProvFSMap();
            String existingSubsFs=existingSubsProfileDTO.getSubsFS2();
            knLogger.debug("update :provFSMap ",provFSMap);

            if(provFSMap!= null  && !provFSMap.isEmpty()){
                if(isProvFSBitAllowed(provFSMap)){
                    List<com.kodiak.common.resources.KnConstants.PROV_FS_BIT> availableProvFSList = Arrays.asList(com.kodiak.common.resources.KnConstants.PROV_FS_BIT.values());
                    for(com.kodiak.common.resources.KnConstants.PROV_FS_BIT availableProvFS : availableProvFSList)
                    {
                        boolean featureIsEnabled= KnGeneralUtil.getFeatureBitValue(existingSubsFs,availableProvFS.value());
                        if (featureIsEnabled )
                            isprovFSEnabled= com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value();

                        if(provFSMap !=null ){
                            for (Integer provFsKey: provFSMap.keySet()) {
                                // if not equals to 1 that is enabled then
                                if (! provFSMap.get(provFsKey).equals(isprovFSEnabled)) {
                                    isProfileUpdated = true;
                                    break;
                                }
                            }
                        }
                    }
                }else {
                    knLogger.error(methodName, "Operation not allowed for feature bit(s)!");
                    throw new KnProvBOException(KnErrorCodes.Validator.INVALID_PROV_FS, "Requested feature bit(s) is/are not allowed to enable/disable via create/update.");
                }
            }




            String oldExtCorpId = null;
            try {
                oldExtCorpId = xdmServerDAO.retrieveExtCorporationId(existingSubsProfileDTO.getCorpId(), persisterTxn);
                knLogger.debug(methodName, "Old Ext Corp ID - ", oldExtCorpId);
            } catch (KnDAOException e) {
                if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.SERVER_ERROR.equals(e.getErrorCode())) {
                    knLogger.debug(methodName, "No Old Corporate Profile exists");
                }
            }

            if (newCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() ||
                    oldCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                //--> check if the corp subscription Type has been updated.
                if (oldCorpType != newCorpType) {
                    isSubsTypeUpdated = true;

                    // isNameOrStateUpdated = true;
                    isProfileUpdated = true;
                }
                // check if the new Ext Corp ID is passed when subscription type changed from non-corp to corp
                if (oldCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value() &&
                        newCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                    knLogger.debug(methodName, "Corp subscription type of subs changed from NONE to CORPORATE"
                    );
                    if (newExtCorpId == null && oldAccountId == null) {
                        knLogger.error(methodName, "Ext Corp ID cannot be null for a corporate subscriber"
                        );
                        throw new KnProvBOException(KnErrorCodes.BOEntity.EXTCORPID_CANNOT_BE_NULL_FOR_CORP_SUBS,
                                "Ext Corp ID cannot be null for a corporate subscriber");
                    } else if (newExtCorpId == null) {
                        newExtCorpId = oldAccountId.trim();
                        knLogger.debug(methodName, "assiging public account id to ext corpid");
                        isExtCorpIdUpdated = true;
                    } else {
                        knLogger.debug(methodName, "Corp subs type change from none to corp ",
                                "hence ext corp id has been updated");
                        isExtCorpIdUpdated = true;
                    }
                }

                if (oldCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() &&
                        newCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                    isExtCorpIdUpdated = true;
                    newExtCorpId = null;      //PR INT52567 Fix:
                    if (newSubsClientType != KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value()) {
                        knLogger.debug(methodName, "Invalid client type passed for public subscriber");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CLIENT_TYPE_FOR_PUBLIC_SUBS,
                                "Invalid client type passed for public subscriber");

                    }
                } else {
                    if (newExtCorpId == null) {
                        newExtCorpId = oldExtCorpId;
                    }
                }

                knLogger.debug(methodName, "oldExtCorpId :", oldExtCorpId, "  newExtCorpId:", newExtCorpId);

                if (oldExtCorpId != null) {
                    if (!oldExtCorpId.equals(newExtCorpId)) {
                        knLogger.debug(methodName, "EXT CORP ID change detected");
                        isProfileUpdated = true;
                        isExtCorpIdUpdated = true;
                    }
                } else if (newExtCorpId != null) {
                    if (!newExtCorpId.equals(oldExtCorpId)) {
                        knLogger.debug(methodName, "EXT CORP ID change detected");
                        isProfileUpdated = true;
                        isExtCorpIdUpdated = true;
                    }
                }
            }

            // etag check for update in state or subscriber name
            if (isNameUpdated || isSubsTypeUpdated) {
                long etag = subsProvInputDTO.getIfMatch();
                // Get current etag
                long currentEtag = existingSubsProfileDTO.getLastProfileUpdateTime();
                if (etag > 0 && currentEtag != etag) {
                    knLogger.error(methodName, "Etag mismatches  ", etag);
                    throw new KnProvBOException(KnErrorCodes.BOEntity.MISMATCH_IN_SUBS_DOC_ETAG, "Etag mismatch");
                }
            }
            //populate the subscriber profile persist DTO
            subsProfilePersistDTO = new KnSubsProfilePersistDTO();
            subsProfilePersistDTO.setMdn(subsProvInputDTO.getMdn());
            subsProfilePersistDTO.setHierarchyType(subsProvInputDTO.getHierarchyType());
            if (subsProvInputDTO.getFirstNetIndicator() != null) {
                subsProfilePersistDTO.setFirstNetIndicator(subsProvInputDTO.getFirstNetIndicator());
            }
            if (subsProvInputDTO.getIMEI() != null) {
                subsProfilePersistDTO.setIMEI(subsProvInputDTO.getIMEI());
            }
            if (subsProvInputDTO.getAffiliateId() != null) {
                subsProfilePersistDTO.setAffiliateId(subsProvInputDTO.getAffiliateId());
            }
            if (null != newNetworkName && !newNetworkName.isEmpty()) {
                subsProfilePersistDTO.setNetworkName(newNetworkName);
            }

            if (isUFMIChange) {
                int iDenInterOp = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getiDenInterOp();
                if (iDenInterOp == KnProvConstants.IDEN_INTEROP.DISABLED.value()) {
                    knLogger.error(methodName, "IDenInterOp is disabled");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.IDEN_INTEROP_DISABLED,
                            "iDen InterOp is disabled");
                }

                if(newUFMI !=null && !newUFMI.isEmpty()  && !newUFMI.equals(oldUFMI) ) {
                    KnOPSubsProfileInfoDTO subsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfoForUFMI(newUFMI, persisterTxn);
                    if(subsProfileInfoDTO!=null){
                        throw new KnProvBOException(KnErrorCodes.BOEntity.DUPLICATE_UFMI, "UFMI already Exists");
                    }
                }
                subsProfilePersistDTO.setUfmi(newUFMI);
                subsProfilePersistDTO.setiDenUserName(newIdenUserName);
                subsProfilePersistDTO.setiDenPassword(newIdenPassword);
                subsProfilePersistDTO.setiDenBusUnitId(newIdenBusUnitId);
                isProfileUpdated=true;
            }

            subsProfilePersistDTO.setPayType(subsProvInputDTO.getPayType());
            if (newCorpType != -1) {
                if (oldCorpType != newCorpType) {
                    subsProfilePersistDTO.setCorporateSubscriptionType(newCorpType);
                } else {
                    subsProfilePersistDTO.setCorporateSubscriptionType(oldCorpType);
                }
            } else {
                subsProfilePersistDTO.setCorporateSubscriptionType(oldCorpType);
            }

            if (newPublicType != -1) {
                if (oldPublicType != newPublicType) {
                    subsProfilePersistDTO.setPublicSubscriptionType(newPublicType);
                } else {
                    subsProfilePersistDTO.setPublicSubscriptionType(oldPublicType);
                }
            } else {
                subsProfilePersistDTO.setPublicSubscriptionType(oldPublicType);
            }

            // check if the subscriber client type is only Public subscriber and client type is not handset
            // if it true then send error.

            if (subsProfilePersistDTO.getPublicSubscriptionType() == com.kodiak.xdms.server.common.resources.KnConstants.PUBLIC_SUBSCRIPTION_TYPE.PUBLIC.value() &&
                    subsProfilePersistDTO.getCorporateSubscriptionType() == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                if (newSubsClientType != com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value() || oldSubsClientType== com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) {
                    knLogger.error(methodName, "Invalid Client Type for public subscriber");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CLIENT_TYPE_FOR_PUBLIC_SUBS,
                            "Invalid client type passed for public subscriber");
                }
            }


            subsProfilePersistDTO.setRoamingTypes(subsProvInputDTO.getRoamingTypes());

            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            // updating the corporate level data
            KnOPCorpProfileInfoDTO corpProfileInfoDTO = null;
            if (newExtCorpId != null && (newCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() ||
                    oldCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value())) {
                corpProfileInfoDTO = xdmServerDAO.retrieveCorporateProfile(newExtCorpId, false, persisterTxn);
                corpFS2 = corpProfileInfoDTO.getCorpFS2();
                String oldCorpName = corpProfileInfoDTO.getCorporateName();
                String newCorpName = subsProvInputDTO.getCorporateName();
                if (newCorpName != null) {
                    if (!newCorpName.equals(oldCorpName)) {
                        // isProfileUpdated = true;
                        isCorpNameUpdated = true;
                        knLogger.debug(methodName, "Change in Corporate name observed - ");
                    }
                }
            }
            subsProfilePersistDTO.setClientPVmajorVer(existingSubsProfileDTO.getClientPVmajorVer());
            subsProfilePersistDTO.setClientPVminorVer(existingSubsProfileDTO.getClientPVminorVer());
            subsProfilePersistDTO.setUserAgent(existingSubsProfileDTO.getUserAgent());
            String method = subsProvInputDTO.getMethod();
            if (com.kodiak.xdms.server.common.resources.KnConstants.UPDATE_CLIENT_FS.equals(method)) {
                knLogger.debug(methodName, "Call from updateClientFS user Agent :", method);
                KnUserAgentDTO userAgentDTO = subsProvInputDTO.getUserAgentDTO();
                String newUserAgent = subsProvInputDTO.getUserAgent();

                Integer subsClientType = existingSubsProfileDTO.getSubsClientType();
                boolean pttRadioClient = featureSetUtil.getFeatureBitValue(clientFS2, com.kodiak.common.resources.KnConstants.FEATURE_SET.PTTRADIOCLIENT.value());
                if (subsClientType.equals(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) ||
                        subsClientType.equals(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) ||
                        subsClientType.equals(com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value())){
                    if (!pttRadioClient){
                        knLogger.error(methodName,"PTT Radio client feature bit #44 is disable");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.PTTRADIOCLIENTBIT_DISABLED,"PTT Radio client feature bit is disable");
                    }
                }

                //Reject request for client if app name is not knwds
                if (existingSubsProfileDTO.getDispatchType() == com.kodiak.common.resources.KnConstants.DISPATCH_TYPE){
                    if (!(userAgentDTO.getAppName().equals(KnProvConstants.KNWDS))){
                        throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CLIENT, "Invalid client for dispatch subscriber app name not matching");
                    }
                }

                if (newUserAgent != null) {
                    if (existingSubsProfileDTO.getUserAgent() == null || (!existingSubsProfileDTO.getUserAgent().equals(newUserAgent))) {
                        knLogger.debug(methodName, "new user Agent :", newUserAgent);
                        knLogger.debug(methodName, "change in User Agent ");
                        subsProfilePersistDTO.setUserAgent(newUserAgent);
                        isProfileUpdated = true;
                    }
                }

                if (userAgentDTO != null) {
                    //Device Onboarding validation
                    boolean supportedDevice = provInfoUtil.isSupportedDevice(userAgentDTO, xdmPttServerId, persisterTxn, existingSubsProfileDTO);
                    if (!supportedDevice) {
                        knLogger.error(methodName, "BLOCK_UNSUPPORTED_DEVICES is enabled and device not supported");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.UNSUPPORTED_DEVICE, "UNSUPPORTED DEVICE");
                    }
                    String protocolVersion = userAgentDTO.getProtocolVersion();
                    if (protocolVersion != null) {
                        try {
                            String[] pv = protocolVersion.split("\\.");
                            if (pv != null && pv[0] != null) {
                                int majorPV = Integer.parseInt(pv[0]);
                                if (existingSubsProfileDTO.getClientPVmajorVer() != majorPV) {
                                    knLogger.debug(methodName, "majorPV version :", majorPV);
                                    knLogger.debug(methodName, "change in major PV version ");
                                    subsProfilePersistDTO.setClientPVmajorVer(majorPV);
                                    isProfileUpdated = true;
                                }
                            }

                            if (pv != null && pv[1] != null) {
                                int minorPV = Integer.parseInt(pv[1]);
                                if (existingSubsProfileDTO.getClientPVminorVer() != minorPV) {
                                    knLogger.debug(methodName, "minorPV version :", minorPV);
                                    knLogger.debug(methodName, "change in minor PV version");
                                    subsProfilePersistDTO.setClientPVminorVer(minorPV);
                                    isProfileUpdated = true;
                                }
                            }


                            Map<Integer,Integer> vocoderIdMap1 = subsProvInputDTO.getVocoderIdMap();
                            if(vocoderIdMap1 != null){
                                if ((protocolVersion.split("\\.")[0]).matches(com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_9_0)){
                                    knLogger.debug(methodName, "majorPV version : 9 and above");
                                    List<KnClientVocoderProfilePersistDTO> clientVocoderProfilePersistDTOs = new ArrayList<>();

                                    for (Integer priority : vocoderIdMap1.keySet()){
                                        KnClientVocoderProfilePersistDTO clientVocoderProfilePersistDTO = new KnClientVocoderProfilePersistDTO();
                                        clientVocoderProfilePersistDTO.setVocoderId(vocoderIdMap1.get(priority));
                                        clientVocoderProfilePersistDTO.setPriority(priority);
                                        clientVocoderProfilePersistDTO.setMdn(subsProvInputDTO.getMdn());
                                        clientVocoderProfilePersistDTOs.add(clientVocoderProfilePersistDTO);
                                    }
                                    int newVocoderId = createClientSuppVocoder(clientVocoderProfilePersistDTOs,persisterTxn);
                                    knLogger.debug(methodName,"Change is vocoder ID old ID :",existingSubsProfileDTO.getVocoderId()," New vocoder Id :",newVocoderId);
                                    subsProfilePersistDTO.setVocoderId(newVocoderId);
                                    isProfileUpdated = true;
                                }else if(Integer.parseInt(protocolVersion.split("\\.")[0]) < com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_9_X && "1".equals(paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.SPECIFIC_CODEC_SUPPPORT_OLDER_BREW_CLIENTS))
                                        && newUserAgent.contains(paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.OLDER_CLIENTS_CODEC_SUPPORTED_BREW_UA))){
                                    knLogger.debug(methodName, "Older brew client");
                                    subsProfilePersistDTO.setVocoderId(com.kodiak.xdms.server.common.resources.KnConstants.AMR_HALF_RATE_PROFILEID);
                                }
                            }


                        } catch (NumberFormatException nfe) {
                            knLogger.debug(methodName, "Invalid PV version :", nfe);
                            knLogger.debug(methodName, "setting PV as 1.0:");
                            subsProfilePersistDTO.setClientPVmajorVer(1);
                            subsProfilePersistDTO.setClientPVminorVer(0);
                        }
                    } else {
                        knLogger.debug(methodName, "setting pv as 1.0 as PV is null or improper");
                        subsProfilePersistDTO.setClientPVmajorVer(1);
                        subsProfilePersistDTO.setClientPVminorVer(0);
                    }
                } else {
                    knLogger.debug(methodName, "setting Pv as 1.0 as PV is null or not proper");
                    subsProfilePersistDTO.setClientPVmajorVer(1);
                    subsProfilePersistDTO.setClientPVminorVer(0);
                }

                int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
                String deviceSharingFlag = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(com.kodiak.xdms.server.common.resources.KnConstants.DEVICE_SHARING_FEATURE_FLAG);

                int deviceSharing = 0;
                if(deviceSharingFlag != null) deviceSharing = Integer.parseInt(deviceSharingFlag);
                int deviceSharewifiFlag=Integer.parseInt(genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(com.kodiak.xdms.server.common.resources.KnConstants.AUTO_DEVICESHARE_WIFI_CC));
                knLogger.debug("deviceSharewifiFlag-->"+deviceSharewifiFlag);
                knLogger.debug("getClientPVmajorVer"+subsProfilePersistDTO.getClientPVmajorVer()+"getClientPVmajorVer"+existingSubsProfileDTO.getClientPVmajorVer()
                        +"getLicenseType"+existingSubsProfileDTO.getLicenseType()+"deviceSharing"+deviceSharing+"existingSubsProfileDTO.getSubsClientType()"+existingSubsProfileDTO.getSubsClientType()
                        +"");
                if(subsProfilePersistDTO.getClientPVmajorVer() != existingSubsProfileDTO.getClientPVmajorVer() && subsProfilePersistDTO.getClientPVmajorVer() >= PROTOCOL_VERSION_14
                        && existingSubsProfileDTO.getLicenseType() != ENABLED && deviceSharing == ENABLED && (String.valueOf(existingSubsProfileDTO.getSubsClientType()).matches(CLIENT_TYPE_WIFI_CROSS_NORMAL_RADIO))
                        && deviceSharewifiFlag== ENABLED){
                    subsProfilePersistDTO.setLicenseType(ENABLED);
                }else{
                    knLogger.debug("setting the else val-->"+existingSubsProfileDTO.getLicenseType());
                    subsProfilePersistDTO.setLicenseType(existingSubsProfileDTO.getLicenseType());
                }

            }
            if (com.kodiak.xdms.server.common.resources.KnConstants.MCSCOMPLIANCE != existingSubsProfileDTO.getMcpttCompliance() && (subsProvInputDTO.getMcId() != null
                    || subsProvInputDTO.getMcDataId() != null || subsProvInputDTO.getMcpttId() != null
                    || subsProvInputDTO.getMcVideoId() != null)) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.MCXIDS_NOT_ALLOWED_FOR_KODIAK_CLIENTS,
                        "MCX Id(s) are not allowed for kodiak clients.");
            }
            validateMCXIdsUriFormat(subsProvInputDTO, persisterTxn);
            validateIfUserIdAlredyExistsInSystem(subsProvInputDTO.getUserId(), persisterTxn);
            Set<String> mdnSet= new HashSet<>();
            if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcId(), subsProvInputDTO.getMdn())) {
                mdnSet.add(subsProvInputDTO.getMcId().substring(TELURI.length()));
            }
            if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcpttId(), subsProvInputDTO.getMdn())) {
                mdnSet.add(subsProvInputDTO.getMcpttId().substring(TELURI.length()));
            }
            if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcVideoId(), subsProvInputDTO.getMdn())) {
                mdnSet.add(subsProvInputDTO.getMcVideoId().substring(TELURI.length()));
            }
            if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcDataId(), subsProvInputDTO.getMdn())) {
                mdnSet.add(subsProvInputDTO.getMcDataId().substring(TELURI.length()));
            }
            List<String> mdns = new ArrayList<String>();
            mdns.addAll(mdnSet);
            valiadteIfMCXIdsExistsAsMdnOrAliasMdn(mdns, persisterTxn);
            validateIfMdnOrMcidsPresentInKUIDPOOL(mdns);
            valiadteIfMCXIdsExistsInSystemForUpdate(subsProvInputDTO, persisterTxn);
            populateDefaultMCXIdsForUpdate(subsProvInputDTO);

            if (subsProvInputDTO.getUserId() != null) {
                if (!(subsProvInputDTO.getUserId().isEmpty() && null == existingSubsProfileDTO.getUserId())
                        && !subsProvInputDTO.getUserId().equalsIgnoreCase(existingSubsProfileDTO.getUserId())) {
                    subsProfilePersistDTO.setUserId(subsProvInputDTO.getUserId());
                    isProfileUpdated = true;
                }

            }
            if (subsProvInputDTO.getMcId() != null) {
                if (!subsProvInputDTO.getMcId().equals(existingSubsProfileDTO.getMcId())) {
                    subsProfilePersistDTO.setMcId(subsProvInputDTO.getMcId());
                    isProfileUpdated = true;
                }
            }
            if (subsProvInputDTO.getMcpttId() != null) {
                if (!subsProvInputDTO.getMcpttId().equals(existingSubsProfileDTO.getMcpttId())) {
                    subsProfilePersistDTO.setMcpttId(subsProvInputDTO.getMcpttId());
                    isProfileUpdated = true;
                }
            }
            if (subsProvInputDTO.getMcVideoId() != null) {
                if (!subsProvInputDTO.getMcVideoId().equals(existingSubsProfileDTO.getMcVideoId())) {
                    subsProfilePersistDTO.setMcVideoId(subsProvInputDTO.getMcVideoId());
                    isProfileUpdated = true;
                }
            }
            if (subsProvInputDTO.getMcDataId() != null) {
                if (!subsProvInputDTO.getMcDataId().equals(existingSubsProfileDTO.getMcDataId())) {
                    subsProfilePersistDTO.setMcDataId(subsProvInputDTO.getMcDataId());
                    isProfileUpdated = true;
                }
            }
            if(subsProvInputDTO.getOnBoardingEmailReqd()!=null) {
                if (allProfileDto != null && allProfileDto.getOnBoardingMailReqd() != null && !allProfileDto.getOnBoardingMailReqd().equals(subsProvInputDTO.getOnBoardingEmailReqd())) {
                    isProfileUpdated = true;
                }
            }

            if (subsProvInputDTO.getCameraType() != null) {
                if (!subsProvInputDTO.getCameraType().equals(existingSubsProfileDTO.getCameraType())) {
                    subsProfilePersistDTO.setCameraType(subsProvInputDTO.getCameraType());
                    isProfileUpdated = true;
                }
            }

            if(cameraInfo!=null){
                isProfileUpdated = true;
            }
            boolean  aliasInfoAlreadyExist = true;
            if(subsProvInputDTO.getAddAliasInfoList()!=null) {
                knLogger.debug("check for input AliaInfoList");
                List<KnSubsAliasInfoDTO> alisasInfoList=subsProvInputDTO.getAddAliasInfoList();
                Map<String, String> aliasIdIssuerMap= new HashMap<>();
                for(KnSubsAliasInfoDTO aliasInfoDTO: alisasInfoList){
                    aliasIdIssuerMap.put(aliasInfoDTO.getAliasId(),aliasInfoDTO.getAliasIdIssuer());
                }
                Map<String, List<KnSubsAliasInfoDTO>> subsAliasIdInfoMap = xdmServerDAO.selectSubsAliasIdInfoByAliasId(aliasIdIssuerMap, persisterTxn);
                aliasInfoAlreadyExist =provInfoUtil.checkAliasInfoCombinationExist(alisasInfoList,subsAliasIdInfoMap);
                knLogger.debug("new AliasInfoList already exist :: ",aliasInfoAlreadyExist);
                if(aliasInfoAlreadyExist){
                    knLogger.debug("AliasInfoList cannot be modified");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.DUPLICATE_ALIAS_INFO_NOT_ALLOWED,
                            "Input ALIAS INFO combination (AliasId,AliasIssuer,AliasType) already exist");
                }else
                {
                    isProfileUpdated=true;
                    knLogger.debug("AliasInfoList for modify validated");

                }

            }
            boolean removeFromList=false;
            if(subsProvInputDTO.getRemoveAliasInfoList()!=null) {
                knLogger.debug("check for input AliaInfoList");
                List<KnSubsAliasInfoDTO> alisasInfoList=subsProvInputDTO.getRemoveAliasInfoList();
                Map<String, String> aliasIdIssuerMap= new HashMap<>();
                for(KnSubsAliasInfoDTO aliasInfoDTO: alisasInfoList){
                    aliasIdIssuerMap.put(aliasInfoDTO.getAliasId(),aliasInfoDTO.getAliasIdIssuer());
                }
                Map<String, List<KnSubsAliasInfoDTO>> subsAliasIdInfoMap = xdmServerDAO.selectSubsAliasIdInfoByAliasId(aliasIdIssuerMap, persisterTxn);
                removeFromList =provInfoUtil.checkAliasInfoCombinationExist(subsProvInputDTO.getMdn(),alisasInfoList,subsAliasIdInfoMap);
                knLogger.debug("new AliasInfoList already exist :: ",removeFromList);
                if(!removeFromList){
                    knLogger.debug("AliasInfoList to be removed doesn't exist");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.REMOVE_ALIAS_INFO_NOT_EXIST,
                            "Input ALIAS INFO combination (AliasId,AliasIssuer,AliasType) to remove doesn't exist");
                }else
                {
                    isProfileUpdated=true;
                    knLogger.debug("AliasInfoList for modify validated");

                }

            }

            //send a failure response if profile is not updated.
            if (!isProfileUpdated && !isCorpNameUpdated) {
                knLogger.error(methodName, "No change in profile observed");
                throw new KnProvBOException(KnErrorCodes.BOEntity.NO_CHG_IN_PROFILE, "No change in profile observed");
            }
            if (isExtCorpIdUpdated && (newCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() ||
                    oldCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value())) {
                if (newExtCorpId != null) {
                    // check if the new Ext Corp ID exists in DB.
                    // if exists assign the INT CORP ID of the new Ext Corp ID to the Subscriber profile
                    // and update the corporate profile etag
                    // else create the NEW EXT CORP ID profile and assign newly created ID to subscriber profile
                    knLogger.debug(methodName, "verifying if corporate profile already exists");
                    int corpId = corpProfileInfoDTO.getCorpId();
                    knLogger.debug(methodName, "Corporate Id Retrieved - ", corpId);
                    int maxSubsCountPerCorp = 0;
                    if (corpId > 0) {
                        knLogger.debug(methodName, "corporate profile already exists with corp id ", "[", corpId, "] for extCorpId ", newExtCorpId);
                        int pairedContactListID = corpProfileInfoDTO.getPairedContactListId();
                        boolean existingPairInd = false;
                        if (pairedContactListID > 0) {
                            existingPairInd = true;
                        }
                        int maxSubsCorporateLimit = corpProfileInfoDTO.getMaxSubscribers();
                        int corpSubsCount = provInfoUtil.retrieveCorpSubsCount(corpId, persisterTxn);
                        int maxSubsPerCorpSystemLevel = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getMaxSubscrPerCorp();
                        if(maxSubsCorporateLimit > 0) {
                            maxSubsCountPerCorp = maxSubsPerCorpSystemLevel > maxSubsCorporateLimit ? maxSubsCorporateLimit : maxSubsPerCorpSystemLevel;
                        }else{
                            maxSubsCountPerCorp = maxSubsPerCorpSystemLevel;
                        }
                        knLogger.debug(methodName, "maxSubsPerCorpSystemLevel: ", maxSubsPerCorpSystemLevel, " ,maxSubsCorporateLimit: ",
                                maxSubsCorporateLimit, "corpSubsCount: ", corpSubsCount);
                        if (corpSubsCount + 1 > maxSubsCountPerCorp) {
                            knLogger.error(methodName, "Max Subscriber limit has been reached for corp - ", corpId);
                            throw new KnProvBOException(KnErrorCodes.Validator.MAX_SUBS_LIMIT_REACHED_FOR_CORP,
                                    "Max Subscriber limit for corp has been reached");
                        }

                        subsProfilePersistDTO.setPairingInd(existingPairInd);

                        //New autopair implementation
                        if (pairedContactListID > 0) {
                            knLogger.debug(methodName, "param value map", paramNameValueMap);
                            int corpAutoPairingCnt = Integer.valueOf(paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.CORP_AUTO_PAIRING_SIZE));
                            knLogger.info(methodName, "existing subs count - ", corpSubsCount, "autopair limit - ", corpAutoPairingCnt);
                            if (corpSubsCount >= corpAutoPairingCnt) {
                                // to disable auto pairing
                                responseDTO.setCorpAutoPairing(false);
                                responseDTO.setIsOldCorp(true);
                            } else {
                                // to enable auto pairing
                                if (KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value() == newSubsClientType) {
                                    responseDTO.setCorpAutoPairing(true);
                                    responseDTO.setIsOldCorp(true);
                                }
                            }
                        }

                        KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
                        corpProfilePersistDTO.setCorpId(corpId);
                        if (subsProvInputDTO.getCorporateName() != null) {
                            corpProfilePersistDTO.setCorporateName(subsProvInputDTO.getCorporateName());
                        } else {
                            corpProfilePersistDTO.setCorporateName(corpProfileInfoDTO.getCorporateName());
                        }
                        xdmServerDAO.updateCorporateProfile(corpProfilePersistDTO, persisterTxn);
                        subsProfilePersistDTO.setCorpId(corpId);

                    } else {
                        knLogger.debug(methodName, "creating the corporate Profile");

                        //NEW AUtoPair Logic Implementation
                        knLogger.debug(methodName, "param value map", paramNameValueMap);
                        int enbaleAutoPair = Integer.valueOf(paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.ENABLE_CORP_AUTO_PAIRING));
                        if (enbaleAutoPair == KnProvConstants.AUTO_PAIR_ENABLED && (subsProvInputDTO.isAutoPair() != null && subsProvInputDTO.isAutoPair())) {
                            knLogger.info(methodName, "System flag corp auto pairing is enabled and CBE is also configured.");
                            if (KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value() == newSubsClientType) {
                                // to enable auto pairing
                                responseDTO.setCorpAutoPairing(true);
                                responseDTO.setIsOldCorp(false);
                            }
                        }

                        //get final CorpFeatureSet
                        corpFS2 = featureSetUtil.getDefFinalCorpFS();
                        knLogger.debug(methodName, "final corpFS :", corpFS2);

                        //get final OpsCorpFeatureSet
                        String opsCorpFS2 = featureSetUtil.getDefFinalOpsCorpFS();
                        knLogger.debug(methodName, "final opsCorpFS :", opsCorpFS2);
                        subsProfilePersistDTO.setExtCorpId(newExtCorpId);
                        subsProfilePersistDTO.setPoCHome(existingSubsProfileDTO.getPoCHome());
                        KnCorpProfilePersistDTO corpProfilePersistDTO = populateCorpProfileInfoDTO(xdmPttServerId,
                                subsProfilePersistDTO, persisterTxn);
                        if (subsProvInputDTO.getCorporateName() != null) {
                            corpProfilePersistDTO.setCorporateName(subsProvInputDTO.getCorporateName());
                        }
                        corpProfilePersistDTO.setCorpFS2(corpFS2);
                        corpProfilePersistDTO.setOpsCorpFS2(opsCorpFS2);
                        String pocPttServerId = corpProfilePersistDTO.getPocHome();
                        KnPOCSvcConfigDTO knPOCSvcConfigDTO = provInfoUtil.retrievePOCSvcConfig(pocPttServerId);
                        //added new  parameter for dynamic Qos
                        corpProfilePersistDTO.setDynamicQosFlag(knPOCSvcConfigDTO.getDynamicQosFlag());
                        String xdmCorpFS2Set = KnGeneralUtil.getDefaultXDMCorpFS2Set(XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), com.kodiak.common.resources.KnConstants.TRUE);
                        xdmCorpFS2Set = featureSetUtil.calculateXDMCorpFS2(xdmCorpFS2Set, XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), com.kodiak.common.resources.KnConstants.TRUE);
                        corpProfilePersistDTO.setXdmCorpFS2Set(xdmCorpFS2Set);
                        corpId = xdmServerDAO.createCorporateProfile(corpProfilePersistDTO, persisterTxn);
                        successPegs.add(KnOMConstants.XDM_NUM_CORP_PROFILE_CREATED);
                        knLogger.debug(methodName, "created the corporate profile for extCorpId - ",
                                newExtCorpId);
                        subsProfilePersistDTO.setCorpId(corpId);
                        subsProfilePersistDTO.setPairingInd(subsProvInputDTO.getPairingInd());
                    }

                }

                if (oldExtCorpId != null) {
                    // if Corporate subscription type is moved Corp to non-corp
                    if (newExtCorpId == null) {
                        subsProfilePersistDTO.setCorpId(0);
                    }
                }
            } else if (newCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() ||
                    oldCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                int corpId = existingSubsProfileDTO.getCorpId();
                subsProfilePersistDTO.setCorpId(corpId);
                if (corpId > 0) {
                    String oldCorpName = corpProfileInfoDTO.getCorporateName();
                    String newCorpName = subsProvInputDTO.getCorporateName();
                    KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
                    corpProfilePersistDTO.setCorpId(corpId);
                    if (newCorpName != null) {
                        if (!newCorpName.equals(oldCorpName)) {
                            knLogger.debug(methodName, "Change in Corporate name observed - ");

                            corpProfilePersistDTO.setCorporateName(newCorpName);
                        } else {
                            corpProfilePersistDTO.setCorporateName(oldCorpName);

                        }
                    }
                    xdmServerDAO.updateCorporateProfile(corpProfilePersistDTO, persisterTxn);
                }
            }
            if (newEMail != null) {
                subsProfilePersistDTO.setEmailAddress(newEMail);
            }
            if (newCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                subsProfilePersistDTO.setCorpId(0);
            }

            String newSubsFS2 = existingSubsProfileDTO.getSubsFS2();
            //same values of provFS1 and provFS1BitMask is used for calculation final subsFS after oring with bitmask
            String provFS2 = newSubsFS2;
            String provFS2BitMask = newSubsFS2;
            Boolean isSubsFS2Updated=false;

            if (isClientTypeUpdated || isSubsTypeUpdated ||isPkgCodeUpdated) {

                BitSet finalFSBitSet = new BitSet(Long.SIZE);
                String basePkgCode = paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.BASE_PKGCODE);
                String basePkgCodeFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(newPublicType, newCorpType,
                        oldSubsClientType, basePkgCode, xdmPttServerId);
                knLogger.debug(methodName, "base pkg is applied - ", basePkgCodeFS);
                BitSet basePkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(basePkgCodeFS);
                finalFSBitSet.or(basePkgCodeBiSet);
                Map<String, Integer> pkgIdsMap = null;
                if (isPkgCodeUpdated) {
                    pkgIdsMap = finalPkgIdsMap;
                    knLogger.debug(methodName, "updated pkg is applied - ", pkgIdsMap);
                } else {
                    pkgIdsMap = exsitingSubsPkgs;
                    knLogger.debug(methodName, "existing pkg is applied - ", pkgIdsMap);
                }

                String pkgCodeFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(newPublicType, newCorpType,
                        oldSubsClientType, pkgIdsMap, xdmPttServerId);
                knLogger.debug(methodName, "addPkgIds is present with subscriberFS - ", pkgCodeFS);
                BitSet pkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(pkgCodeFS);
                finalFSBitSet.or(pkgCodeBiSet);
                int oldIntropBitStatus=findInterOPBitStatus(existingSubsProfileDTO, exsitingSubsPkgs, persisterTxn);

                if (finalFSBitSet.get(LMR_BIT) && com.kodiak.xdms.server.common.resources.KnConstants.LMR_BIT_STATUS.MANUALLY_DISABLED.Value()==oldIntropBitStatus) {
                    finalFSBitSet.clear(LMR_BIT);
                } else if (!finalFSBitSet.get(LMR_BIT) && com.kodiak.xdms.server.common.resources.KnConstants.LMR_BIT_STATUS.MANUALLY_ENABLED.Value()==oldIntropBitStatus) {
                    finalFSBitSet.set(LMR_BIT);
                }
                newSubsFS2 = featureSetUtil.convertBitSetToHexString(finalFSBitSet);
                provFS2 = newSubsFS2;
                provFS2BitMask = newSubsFS2;
                knLogger.debug(methodName, "clientType or subsType or pkg code changed.. got default SubsFS :", newSubsFS2);
                isSubsFS2Updated=true;
            }

            String subsFS2 = featureSetUtil.generateSubsFeatureSet(newSubsFS2, provFS2, provFS2BitMask);
            //if dispatch group member flag changed then set/clear on demand location bit in subsFS

            //setting on demand location bit in subsFS
            String xdmsFs2=existingSubsProfileDTO.getXdmsFS2();
            if (newDispatchGrpMember == com.kodiak.xdms.server.common.resources.KnConstants.BIT_TRUE) {
                xdmsFs2 = featureSetUtil.getSetFeatureSetBits(xdmsFs2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value()});
                knLogger.debug(methodName, "on demand location bit set in xdmsFs:", xdmsFs2);
            } else {
                xdmsFs2 = featureSetUtil.getClearFeatureSetBits(xdmsFs2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.ONDEMLOCATION.value()});
                knLogger.debug(methodName, "on demand location bit clear in xdmsFs:", xdmsFs2);
            }

            int clientPVMajorVersion = subsProfilePersistDTO.getClientPVmajorVer();
            String clientCapOverrideBitMask = featureSetUtil.getClientCapabilityBitMask(xdmPttServerId, clientPVMajorVersion);
            if (subsProvInputDTO.getClientFS2() != null) {
                clientFS2 = featureSetUtil.generateClientFeatureSet(subsProvInputDTO.getClientFS2(), clientCapOverrideBitMask);
            }
            boolean talkGrpSelClient = featureSetUtil.getFeatureBitValue(clientFS2, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value());
            boolean talkGrpScanClient = featureSetUtil.getFeatureBitValue(clientFS2, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value());

            if (!((newSubsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value() || newSubsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value() || newSubsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()))) {
                //When talkGrpSelClient bit is disable in clientFS then enabling talkgrpserver bit of clientFS for
                // corporate subs and which are not of clienttype pocdonor or dispatch
                if ((!(talkGrpSelClient || talkGrpScanClient)) && newCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() && !(newSubsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_DONOR_RADIO.value() || newSubsClientType == KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value())) {
                    knLogger.debug(methodName, "corp type setting talk grp server bit since client bit is disable in clientFS ");
                    clientFS2 = featureSetUtil.getSetFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value()});
                } else {
                    //When talkGrpSelClient bit is enabled in clientFS then disabling talkgrpserver bit of clientFS for
                    // public subs or which are of clienttype pocdonor or dispatch
                    knLogger.debug(methodName, "public to corp/candp or talkGrpSelClient is enabled. clearing talk grp server bit since client bit enable in clientFS");                 // subsFS1 = featureSetUtil.getClearFeatureSetBits(subsFS1, new int[]{KnConstants.FEATURE_SET.TLKGRPSELSERVER.value()});
                    clientFS2 = featureSetUtil.getClearFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value()});
                }
            }else{
                clientFS2 = featureSetUtil.getSetFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value()});
                clientFS2 = featureSetUtil.getClearFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value()});
                clientFS2 = featureSetUtil.getClearFeatureSetBits(clientFS2, new int[]{com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value()});
                knLogger.debug(methodName, "setting talk grp server bit since client bit is disable in clientFS & clearing talk grp server bit in clientFS fpr PTT Radio clients :",clientFS2);
            }
            String opsFS2 = existingSubsProfileDTO.getOpsFS2();
            String activeFS2;
            String corpAdminFS2 = existingSubsProfileDTO.getCorpAdminFS2();
            knLogger.debug(methodName, "clientPVMajorVersion ", clientPVMajorVersion, " ,clientCapOverrideBitMask=", clientCapOverrideBitMask);

            boolean cleanUpTGSData = false;

            knLogger.debug(methodName," updated subsProvPersistDTO's   ProvFS :", provFSMap);

            BitSet bitset= new BitSet();
            if(provFSMap!=null && ! provFSMap.isEmpty()){
                bitset= featureSetUtil.convertHexStringToBitSet(subsFS2);
                for(Map.Entry<Integer,Integer> provEntry: provFSMap.entrySet()){
                    if(provEntry.getValue()== com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value()){
                        bitset.set(provEntry.getKey());
                    }else{
                        bitset.clear(provEntry.getKey());
                    }
                }
                subsFS2= featureSetUtil.convertBitSetToHexString(bitset);
                provFS2 = subsFS2;
                provFS2BitMask= provFS2;
                knLogger.debug(methodName,"  subsFS2 ",subsFS2,"provFS2" ,provFS2);
                subsFS2 = featureSetUtil.generateSubsFeatureSet(subsFS2, provFS2, provFS2BitMask);


            }
            String userProfileFS2 = existingSubsProfileDTO.getUserProfileFS2();
            if(userProfileFS2==null)
            {
                userProfileFS2=featureSetUtil.getDefFinalUserProfileFS();
            }
            if (newCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId,
                        clientFS2, subsFS2, opsFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFS2);
            } else {
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId,
                        clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask,xdmsFs2,userProfileFS2);
            }

            if (com.kodiak.xdms.server.common.resources.KnConstants.UPDATE_CLIENT_FS.equals(method)) {
                knLogger.debug(methodName, "Call from updateClientFS for derivedKey :", method);
                boolean remotePushNotification = featureSetUtil.getFeatureBitValue(activeFS2, FEATURE_SET.REMOTEPUSHNOTIFICATION.value());
                boolean gcmPushNotify = featureSetUtil.getFeatureBitValue(activeFS2, FEATURE_SET.GCMPUSHNOTIFICATION.value());
                if (remotePushNotification || gcmPushNotify){
                    byte[] salt = mdn.getBytes();
                    byte[] encryptedPwd = KnGeneralUtil.getEncryptedPassword(existingSubsProfileDTO.getClientPassword(),salt, com.kodiak.xdms.server.common.resources.KnConstants.ITERATIONS_10_000, com.kodiak.xdms.server.common.resources.KnConstants.DERIVEDKEYLENGTH);
                    String derivedKey = provInfoUtil.convertToHex(encryptedPwd);
                    knLogger.debug(methodName,"Derived key after generation and convertion: ",derivedKey);
                    subsProfilePersistDTO.setDerivedKey(KnGeneralUtil.convertHexToAscii(derivedKey));
                }
            }

            if (!activeFS2.equals(existingSubsProfileDTO.getActiveFS2())) {
                isActiveFSUpdated = true;
                cleanUpTGSData = cleanUpTGSData(activeFS2, existingSubsProfileDTO.getActiveFS2());
                //Checking MCPTT feature permission
                setMCPTTFeatureDisable(mdn, activeFS2, persisterTxn, xdmServerDAO);
                //adding to mcxGroup clean up job on 81 bit disable
                com.kodiak.xdms.server.common.util.KnGeneralUtil.veryLargeGroupBitChanged(existingSubsProfileDTO.getActiveFS2()
                        ,activeFS2
                        ,subsProfilePersistDTO.getMdn()
                        ,String.valueOf(subsProfilePersistDTO.getCorpId()),false);
            }
            knLogger.debug(methodName, "cleanUp TGS ", cleanUpTGSData);
            subsProfilePersistDTO.setDispatchGroupMember(newDispatchGrpMember);
            subsProfilePersistDTO.setClientPassword(newClientPassword);
            if(isPTTRadioClientTypeChange)
            {
                subsProfilePersistDTO.setSubsClientType(newSubsClientType);
            }
            else
            {
                subsProfilePersistDTO.setSubsClientType(oldSubsClientType);
            }
            subsProfilePersistDTO.setSubsFS2(subsFS2);
            subsProfilePersistDTO.setActiveFS2(activeFS2);
            subsProfilePersistDTO.setClientFS2(clientFS2);
            subsProfilePersistDTO.setOpsFS2(opsFS2);
            subsProfilePersistDTO.setXdmsFS2(xdmsFs2);
            subsProfilePersistDTO.setCorpAdminFS2(corpAdminFS2);

            if (newAccountId != null) {
                subsProfilePersistDTO.setAccountId(newAccountId.trim());
            }
            Integer qppPkgId = existingSubsProfileDTO.getQppPkgId();
            Integer profileId = null;
            String tierPackageId = allProfileDto!=null?allProfileDto.getTierPkgCode():null;
            Integer dataPkgId=allProfileDto!=null?allProfileDto.getDataPkgId():null;

            Map<String, Integer> addonPackageIds=null;

            if (isPkgCodeUpdated) {
                String updatedTierPkg=null;
                addonPackageIds = new HashMap<>();
                knLogger.debug(methodName, "Updated pkg map in  pocsubsaddlinfo", finalPkgIdsMap);
                for (Map.Entry<String, Integer> entry : finalPkgIdsMap.entrySet()) {
                    if (entry.getValue().intValue() == com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue()) {
                        updatedTierPkg = entry.getKey();
                        knLogger.debug(methodName, "tier pkg is present ", updatedTierPkg);
                    } else if (entry.getValue() == com.kodiak.xdms.server.common.resources.KnConstants.ADDON_PKG_TYPE.intValue()) {
                        addonPackageIds.put(entry.getKey(), entry.getValue());

                    }
                }
                tierPackageId=updatedTierPkg;
                if (!addonPackageIds.isEmpty()) {
                    for (String addonPkgCode : addonPackageIds.keySet()) {
                        profileId = featureSetUtil.getAddProfIdForPkg(addonPkgCode, xdmPttServerId);
                        if (profileId != null) {
                            qppPkgId = genInfoUtil.getDataPkgId(com.kodiak.xdms.server.common.resources.KnConstants.QPP_DATA_PKG_TYPE, persisterTxn)
                                    .get(profileId);
                            if (qppPkgId != null) {
                                dataPkgId = qppPkgId;
                                break;
                            } else if (dataPkgId == null)
                                dataPkgId = genInfoUtil.getDataPkgId(com.kodiak.xdms.server.common.resources.KnConstants.ADDON_DATA_PKG_TYPE, persisterTxn)
                                        .get(profileId);

                        }
                    }

                } else {
                    qppPkgId=null;
                    dataPkgId=null;
                }
            }

            if (qppPkgId == null)
                qppPkgId = com.kodiak.xdms.server.common.resources.KnConstants.DEFAULT_QPP_ID;
            if (dataPkgId == null)
                dataPkgId = com.kodiak.xdms.server.common.resources.KnConstants.DEFAULT_DATAPKG_ID;
            if (profileId == null)
                profileId = com.kodiak.xdms.server.common.resources.KnConstants.DEFAULT_PROFILE_ID;
            subsProfilePersistDTO.setQppPkgId(qppPkgId);
            //Update the Subscriber DB
            // --> subscriber info table
            if (isProfileUpdated || !isCorpNameUpdated) {
                knLogger.debug(methodName, "Updated Subscriber Profile");
                xdmServerDAO.updateSubscrProfile(subsProfilePersistDTO, persisterTxn);

            }

            if(existingSubsProfileDTO.getSubsClientType() == KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value()
                    && cameraInfo != null){
                Collection<String> reqMdns=new ArrayList<>();
                reqMdns.add(mdn);
                Map<String, KnSubsCameraInfo> dbCameraInfo = xdmServerDAO.getSubscriberCameraInfo(reqMdns, false, persisterTxn);
                if(cameraInfo!=null
                        &&cameraInfo.getCameraSerialId()==null&&cameraInfo.getIpIdentifier()==null){
                    xdmServerDAO.deleteSubscriberCameraInfo(mdn,persisterTxn);
                }else if(!dbCameraInfo.isEmpty()&&cameraInfo!=null&&cameraInfo.getIpIdentifier()!=null){
                    xdmServerDAO.updateSubscriberCameraInfo(mdn,cameraInfo,persisterTxn);
                }else{
                    subsProfilePersistDTO.setCameraInfo(cameraInfo);
                    xdmServerDAO.createSubscriberCameraInfo(subsProfilePersistDTO,persisterTxn);
                }
            }

            if(removeFromList)
            {
                knLogger.debug("removing AliasIdInfoList change ");
                xdmServerDAO.removeSubsAliasId(subsProvInputDTO.getMdn(),subsProvInputDTO.getRemoveAliasInfoList(),persisterTxn);
                knLogger.debug("Deleted existing AliasIdInfo");
            }
            if(!aliasInfoAlreadyExist)
            {
                knLogger.debug("Adding AliasIdInfoList change ");
                xdmServerDAO.insertIntoSubsAliasId(subsProvInputDTO.getAddAliasInfoList(),subsProvInputDTO.getMdn(),persisterTxn);
                knLogger.debug("Inserted New AliasIdInfo");
            }

            subsProfilePersistDTO.setLicenseType(existingSubsProfileDTO.getLicenseType());
            subsProfilePersistDTO.setMcpttCompliance(existingSubsProfileDTO.getMcpttCompliance());
            updateMCDevice(subsProfilePersistDTO, lastProfileUpdateTime,responseDTO, persisterTxn);

            String subsFs2 = subsProfilePersistDTO.getSubsFS2();
            boolean upmBit = KnGeneralUtil.getFeatureBitValue(subsFs2, USER_PROFILE_MGMT_BIT);
            knLogger.debug("upmBit ", upmBit, "subsFs2 ", subsFs2);
            List<String> UserProfileMdns = new ArrayList<String>();
            // check if it is a Real mdn
            if (existingSubsProfileDTO.getUserProfileIndex() == null
                    || existingSubsProfileDTO.getUserProfileIndex().equals(USER_PROFILE_INDEX)) {
                // Get All profile MDNs for given Real MDN
                UserProfileMdns = getMdnForUPM(mdn, persisterTxn);
                if (!UserProfileMdns.isEmpty()) {
                    UserProfileMdns.remove(mdn.toString().trim());
                }
            }

            // --> update XDM directory.
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);

            //check if UPM bit is enabled and if SubsFS2/SubscriberName/PublicOrCoprSubsriptionType is updated then update the
            //fields in profile MDNs as well if UPM bit is disabled no need to update since the profile MDN's will be deleted asynchronously
            Map<String, KnOPSubsProfileInfoDTO> mdnUpmFsMap=null;
            Map<String, String> profileMdnActivsFsMap=null;
            if(upmBit&&existingSubsProfileDTO.getUserProfileIndex() == null
                    || existingSubsProfileDTO.getUserProfileIndex().equals(USER_PROFILE_INDEX))
            {
                knLogger.debug("updateProfileMdnDetails ", "isNameUpdated--", isNameUpdated, "isSubsTypeUpdated-- ",
                        isSubsTypeUpdated, "isSubsFS2Updated--", isSubsFS2Updated);
                //updating fields for profileMdn.
                mdnUpmFsMap = xdmServerDAO.getProfileMdnNupmfsByBaseMdn(mdn, false, persisterTxn);
                mdnUpmFsMap.remove(mdn);

                if (mdnUpmFsMap.size() > 0) {

                    profileMdnActivsFsMap = provInfoUtil.calculateActiveFS2WithUPMFS(
                            subsProfilePersistDTO.getCorporateSubscriptionType(), pocPttId, presencePttId, xdmsPttId,
                            clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2, clientCapOverrideBitMask,
                            mdnUpmFsMap);

                    Collection<KnSubsProfileDTO> subsSpecificDetails = xdmServerDAO
                            .fetchSubsSpecificDetailsForBulkMdns(new ArrayList<>(profileMdnActivsFsMap.keySet()), false, persisterTxn);
                    Map<String, String> mdnUpmIdMap =new HashMap<>();
                    Map<String, String> mdnActiveFsMap =new HashMap<>();
                    for(KnSubsProfileDTO subsInfo:subsSpecificDetails){
                        mdnUpmIdMap.put(subsInfo.getMdn(),subsInfo.getUserProfileId());
                        mdnActiveFsMap.put(subsInfo.getMdn(),subsInfo.getActiveFS2());
                    }
                    subsProfilePersistDTO.setServiceAuthStatus(existingSubsProfileDTO.getServiceAuthStatus());
                    subsProfilePersistDTO.setLastActivationTime(existingSubsProfileDTO.getLastActivationTime());
                    subsProfilePersistDTO.setSwType(existingSubsProfileDTO.getSwType());
                    subsProfilePersistDTO.setPlatformType(existingSubsProfileDTO.getPlatformType());
                    subsProfilePersistDTO.setDynamicQosFlag(existingSubsProfileDTO.getDynamicQosFlag());
                    subsProfilePersistDTO.setServiceStatusOp(existingSubsProfileDTO.getServiceStatusOp());
                    xdmServerDAO.updateMdnFiledsNActiveFS(subsProfilePersistDTO, profileMdnActivsFsMap, persisterTxn);

                    Map<String,List<Integer>> upmIdGroupMap=provInfoUtil.retriveUpmIdGroupMapByUserProfileId(new ArrayList<>(mdnUpmIdMap.values()),persisterTxn);
                    knLogger.debug(methodName," mdnUpmIdMap :",mdnUpmIdMap," existingActiveFS2 :",mdnActiveFsMap);
                    knLogger.debug(methodName," profileMdnActivsFsMap :",profileMdnActivsFsMap);

                    //Updating isAffiliationEnabled flag for Profile Mdns
                    Map<String, String> mdnIsAffiliationEnabledMap = new HashMap<>();
                    for (Map.Entry<String, String> mdnFsMap : profileMdnActivsFsMap.entrySet()) {
                        Integer isAffiliationEnabledForProfileMdns = DISABLED;
                        boolean bitEnabledForProfileMdns = KnGeneralUtil.getFeatureBitValue(
                                mdnFsMap.getValue(),
                                com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value());
                        if (subsProfilePersistDTO
                                .getClientPVmajorVer() >= com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18
                                && bitEnabledForProfileMdns
                                && checkClientType(subsProfilePersistDTO.getSubsClientType(),
                                existingSubsProfileDTO.getMcpttCompliance())) {
                            isAffiliationEnabledForProfileMdns = ENABLED;
                        }
                        mdnIsAffiliationEnabledMap.put(mdnFsMap.getKey(),
                                isAffiliationEnabledForProfileMdns.toString());
                        //mcxGroup clean job
                        String profileMdnActiveFs = mdnFsMap.getValue();
                        boolean veryLargeGroupBit = KnGeneralUtil.getFeatureBitValue(profileMdnActiveFs, VERY_LARGE_GROUP);
                        String profileMdn = mdnFsMap.getKey();
                        String upmId = mdnUpmIdMap.get(profileMdn);
                        String existingActiveFS2 = mdnActiveFsMap.get(profileMdn);
                        List<Integer> mcxGroups = upmIdGroupMap.get(upmId);
                        if (!veryLargeGroupBit && mcxGroups != null && !mcxGroups.isEmpty()) {
                            //adding to mcxGroup clean up job on profile mdn 81 bit disable
                            com.kodiak.xdms.server.common.util.KnGeneralUtil.veryLargeGroupBitChanged(existingActiveFS2
                                    , profileMdnActiveFs
                                    , profileMdn
                                    , String.valueOf(subsProfilePersistDTO.getCorpId()), true);
                        }
                    }
                    commonXDMServerDAO.updateAffForCorpGrpMemList(mdnIsAffiliationEnabledMap, persisterTxn);
                }
            }

            if (isProfileUpdated && isPkgCodeUpdated) {
                xdmServerDAO.deleteSubAddlOnPkgs(mdn, persisterTxn);

                if (!addonPackageIds.isEmpty()) {
                    knLogger.debug(methodName, "addon pkg is present ", addonPackageIds);
                    xdmServerDAO.createSubAddOnPkgs(mdn, new ArrayList<>(addonPackageIds.keySet()), persisterTxn);
                }

                List<String> profileMdn = new ArrayList<>();
                if (null != profileMdnActivsFsMap && null != profileMdnActivsFsMap.entrySet()) {
                    for (Map.Entry<String, String> profieMdnActiveFS : profileMdnActivsFsMap.entrySet()) {
                        profileMdn.add(profieMdnActiveFS.getKey());
                    }
                }
                // -->. updating entry into the pocsubsaddlinfo

                KnSubsAddlInfoPersistDTO subsAddlProfilePersistDTO = new KnSubsAddlInfoPersistDTO();
                subsAddlProfilePersistDTO.setMdn(mdn);
                subsAddlProfilePersistDTO.setProfilemdn(profileMdn);
                subsAddlProfilePersistDTO.setTierPkgCode(tierPackageId);
                subsAddlProfilePersistDTO.setDataPkgId(dataPkgId);
                Integer onBoardingMail = subsProvInputDTO.getOnBoardingEmailReqd();
                if (allProfileDto != null && allProfileDto.getOnBoardingMailReqd() != null
                        && allProfileDto.getOnBoardingMailReqd().equals(subsProvInputDTO.getOnBoardingEmailReqd())) {
                    onBoardingMail = allProfileDto.getOnBoardingMailReqd();
                }
                subsAddlProfilePersistDTO.setOnBoardingMailReqd(onBoardingMail);
                // check subscriber addlInfo exists
                if (allProfileDto != null) {
                    xdmServerDAO.updateSubscrPkgAddlInfo(subsAddlProfilePersistDTO, persisterTxn);
                } else {
                    subsAddlProfilePersistDTO.setTimeSlotType(com.kodiak.xdms.server.common.resources.KnConstants.TIME_SLOT_TYPE);
                    xdmServerDAO.createSubscrPkgAddlInfo(subsAddlProfilePersistDTO, persisterTxn);
                }
                //updating the pkg info for profilemdn
                knLogger.debug(methodName, "profilemdns", profileMdn);
                if (null != profileMdn) {
                    xdmServerDAO.updateSubscrPkgAddlInfoforProfileMdn(subsAddlProfilePersistDTO, persisterTxn);
                }
            } else if((allProfileDto != null && allProfileDto.getOnBoardingMailReqd() != null
                    && !allProfileDto.getOnBoardingMailReqd().equals(subsProvInputDTO.getOnBoardingEmailReqd()))){
                KnSubsAddlInfoPersistDTO subsAddlProfilePersistDTO = new KnSubsAddlInfoPersistDTO();
                subsAddlProfilePersistDTO.setMdn(mdn);
                subsAddlProfilePersistDTO.setOnBoardingMailReqd(subsProvInputDTO.getOnBoardingEmailReqd());
                xdmServerDAO.updateSubscrOnBoardingMail(subsAddlProfilePersistDTO, persisterTxn);
            }
            //newExtCorpId will be coming from the request
            //oldExtCorpId will be from the DB
            //oldSub
            knLogger.debug(methodName, "Before Calling Update Etag for Corp:-oldExtCorpId=", oldExtCorpId, " ,newExtCorpId=", newExtCorpId);
            // --> subscriber roaming profile table
            ArrayList<Integer> receivedRoamingTypeList = subsProfilePersistDTO.getRoamingTypes();

            //Update roaming profile as per below
            // 1. if passed null no change in the existing profile
            // 2. if passed with -1 as input clean up the roaming profile and create roaming profile with default roaming Id
            // 3. if any values are passed clean up the roaming profile and create roaming profile with new data
            if (receivedRoamingTypeList != null && !receivedRoamingTypeList.isEmpty()) {
                ArrayList<Integer> roamingTypeList = new ArrayList<Integer>();

                if (receivedRoamingTypeList.contains(-1)) {
                    knLogger.debug(methodName, "creating the roaming profile with default value");
                    roamingTypeList.add(KnProvConstants.DEFAULT_ROAMING_CLUSTER_ID);
                } else {
                    HashMap<Integer, String> supportedRoamingClusterInfo = (HashMap<Integer, String>) provInfoUtil.retrieveSupportedRoamingList();
                    for (int clusterName : receivedRoamingTypeList) {
                        for (int roamingID : supportedRoamingClusterInfo.keySet()) {
                            if (String.valueOf(clusterName).equalsIgnoreCase(supportedRoamingClusterInfo.get(roamingID)) && !roamingTypeList.contains(roamingID)) {
                                roamingTypeList.add(roamingID);
                                break;
                            }
                        }
                    }
                }

                knLogger.debug(methodName, "Removing the subscriber Roaming profile");
                xdmServerDAO.deleteSubscrRoamingProfile(mdn, persisterTxn);

                //Client_type check for roam_na list
                if (null == paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.ROAM_NA_CLIENT_TYPES)) {
                    throw new KnProvBOException(KnErrorCodes.BOEntity.SERVER_CONFIGURATION_FAILURE, "ROAM_NA_CLIENT_TYPE parameter not found");
                }
                String paramValue = (String) paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.ROAM_NA_CLIENT_TYPES);
                List<String> roamNAClientTypes = new ArrayList<>();
                if (null != paramValue) {
                    roamNAClientTypes = Arrays.asList(paramValue.split(","));
                    knLogger.debug(methodName, "Roaming not applicable client type ", roamNAClientTypes);
                }
                if (roamNAClientTypes.contains(String.valueOf(newSubsClientType))) {
                    knLogger.debug(methodName, "MDN new client type present in ROAMING NOT APPLICABLE CLIENT TYPE ", newSubsClientType);
                    ArrayList<Integer> roamingAllowed = new ArrayList<>();
                    roamingAllowed.add(KnProvConstants.DEFAULT_ROAMING_CLUSTER_ID);
                    knLogger.debug(methodName, "adding the subscriber roaming profile with default values");
                    xdmServerDAO.createSubscrRoamingProfile(mdn, roamingAllowed, persisterTxn);

                } else {
                    knLogger.debug(methodName, "adding the subscriber roaming profile");
                    xdmServerDAO.createSubscrRoamingProfile(mdn, roamingTypeList, persisterTxn);
                }

            }


            if (oldCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() &&
                    newCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                //Remove the entry from DG.XDM_CorpResourceListIndexDoc
                knLogger.debug(methodName, "change in corp subs type to none");
                knLogger.debug(methodName, "deleting an entry into corp resource list index doc");
                commonXDMServerDAO.deleteCorpResourceListIndexDoc(mdn, persisterTxn);
                knLogger.debug(methodName, "deleted entry -");

            } else if (oldCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value() &&
                    newCorpType == com.kodiak.xdms.server.common.resources.KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                //Create an entry to DG.XDM_CorpResourceListIndexDoc
                knLogger.debug(methodName, "change in corp subs type to corporate from none");
                knLogger.debug(methodName, "adding an entry into corp resource list index doc");
                commonXDMServerDAO.addMdnToCorpResourceListIndexDoc(mdn, persisterTxn);
                knLogger.debug(methodName, "added entry -");

            }
            int isAffiliationEnabled;
            boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subsProfilePersistDTO.getActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value());
            if (subsProfilePersistDTO.getClientPVmajorVer() >= com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18
                    && bitEnabled && checkClientType(subsProfilePersistDTO.getSubsClientType(),existingSubsProfileDTO.getMcpttCompliance()))
            {
                isAffiliationEnabled = ENABLED;
            } else {
                isAffiliationEnabled = DISABLED;
            }

            commonXDMServerDAO.updateAffForCorpGrpMemList(subsProfilePersistDTO.getMdn(),isAffiliationEnabled,persisterTxn);

            int previousEtag = commonXDMServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);
            commonXDMServerDAO.updateEtagForDirDoc(mdn, persisterTxn);

            //IAPN changes
            String xcapRooturi = genInfoUtil.getXCAPRootURI(mdn, persisterTxn);

            int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
            String oneMessageEnabled = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(com.kodiak.xdms.server.common.resources.KnConstants.ONE_MESSAGE_SERVICE_ENABLED);
            knLogger.debug(methodName, "oneMessageEnabled", oneMessageEnabled);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

            //populate the response DTO
            //profile Mdns are set if upmBit is disabled to delete them asynchronously in the Mediator
            if(!upmBit) {
                responseDTO.setUserProfileMdns(UserProfileMdns);
            }
            responseDTO.setCorpId(subsProfilePersistDTO.getCorpId());
            responseDTO.setActiveFSChanged(isActiveFSUpdated);
            responseDTO.setSubsNameChanged(isNameUpdated);
            responseDTO.setSubsTypeChanged(isSubsTypeUpdated);
            responseDTO.setActiveFS2(activeFS2);
            responseDTO.setSubsFS2(subsFS2);
            responseDTO.setCleanUpTGSData(cleanUpTGSData);
            responseDTO.setPocPttId(pocPttId);
            responseDTO.setPresPttId(presencePttId);
            responseDTO.setMdnUpmFsMap(mdnUpmFsMap);
            responseDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
            responseDTO.setProfileMdnActivsFsMap(profileMdnActivsFsMap);
            responseDTO.setMcId(existingSubsProfileDTO.getMcId());
            responseDTO.setMcDataId(existingSubsProfileDTO.getMcDataId());
            responseDTO.setMcPttId(existingSubsProfileDTO.getMcpttId());
            responseDTO.setMcVideoId(existingSubsProfileDTO.getMcVideoId());
            responseDTO.setServiceAuthStatus(existingSubsProfileDTO.getServiceAuthStatus());
            responseDTO.setNetworkName(existingSubsProfileDTO.getNetworkName());
            responseDTO.setSubsClientType(existingSubsProfileDTO.getSubsClientType());
            if (oldPublicType == com.kodiak.xdms.server.common.resources.KnConstants.PUBLIC_SUBSCRIPTION_TYPE.PUBLIC.value() &&
                    newPublicType == com.kodiak.xdms.server.common.resources.KnConstants.PUBLIC_SUBSCRIPTION_TYPE.NONE.value()) {
                responseDTO.setResponseMessage(KnProvConstants.UPDATE_SUBS_PROFILE_SUCCESS);
                responseDTO.setResponseStatus(com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.SUCCESS.value());
                KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                // KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                dirChgDTO.setXcapRootURI(xcapRooturi);
                dirChgDTO.setPocHome(pocPttId);
                dirChgDTO.setPresenceHome(presencePttId);
                KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                dirChgDTO.setDirUri(dirDocUri);
                int newEtag = previousEtag + 1;
                dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                responseDTO.setDirChgDTO(dirChgDTO);
            } else {
                responseDTO.setResponseMessage(KnProvConstants.UPDATE_SUBS_PROFILE_SUCCESS);
                responseDTO.setResponseStatus(com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.SUCCESS.value());
                responseDTO.setEtag(lastProfileUpdateTime);
                //populating the Subs Config document change DTO
                KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
                docChgDTO.setDocUri(subsConfigDocUri);
                docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));

                Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
                docChgList.add(docChgDTO);

                //populating Dir Document change DTO
                KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                /*  KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                  //dirChgDTO.setXcapRootURI(xdmsServiceConfigDTO.getXcapRootUri());
                */
                dirChgDTO.setXcapRootURI(xcapRooturi);
                dirChgDTO.setPocHome(pocPttId);
                dirChgDTO.setPresenceHome(presencePttId);
                dirChgDTO.setDocChgDTO(docChgList);
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                dirChgDTO.setDirUri(dirDocUri);
                dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
                int newEtag = previousEtag + 1;
                dirChgDTO.setDirNewEtag(String.valueOf(newEtag));

                //setting the Document Change DTO the response
                responseDTO.setDirChgDTO(dirChgDTO);
                if(!UserProfileMdns.isEmpty())
                {
                    List<KnOPDirChgDTO> dirChgDTOs= new ArrayList<KnOPDirChgDTO>();
                    for (String profileMdn : UserProfileMdns) {
                        KnOPDocChgDTO profileMdnDocChgDTO = new KnOPDocChgDTO();
                        profileMdnDocChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                        String profileMdnSubsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(profileMdn);
                        profileMdnDocChgDTO.setDocUri(profileMdnSubsConfigDocUri);
                        profileMdnDocChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));

                        Collection<KnOPDocChgDTO> profileMdnDocChgList = new ArrayList<KnOPDocChgDTO>();
                        profileMdnDocChgList.add(profileMdnDocChgDTO);
                        //populating Dir Document change DTO
                        KnOPDirChgDTO profileMdnDirChgDTO = new KnOPDirChgDTO();
                        profileMdnDirChgDTO.setXcapRootURI(xcapRooturi);
                        profileMdnDirChgDTO.setPocHome(pocPttId);
                        profileMdnDirChgDTO.setPresenceHome(presencePttId);
                        profileMdnDirChgDTO.setDocChgDTO(profileMdnDocChgList);
                        String profileMdnDirDocUri = genInfoUtil.generateDirDocUri(profileMdn);
                        profileMdnDirChgDTO.setDirUri(profileMdnDirDocUri);
                        profileMdnDirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
                        int profileMdnNewEtag = previousEtag + 1;
                        profileMdnDirChgDTO.setDirNewEtag(String.valueOf(profileMdnNewEtag));

                        dirChgDTOs.add(profileMdnDirChgDTO);
                    }
                    responseDTO.setDirChgDTOs(dirChgDTOs);
                }
            }

            //updating LASTPROFILEUPDATETIME in POCSUBSCRINFO profile mdn notiy
            if(mdnUpmFsMap!=null&&mdnUpmFsMap.size()>0)
            {
                IProvXDMServerDAO xdmServerDAONew = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                        .createProvXDMServerDAO();
                Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = xdmServerDAONew
                        .profileMdnEtagUpdate(UserProfileMdns, persisterTxn);
                responseDTO.setProfileMdnEtagMap(profileMdnEtagMap);
                if (profileMdnEtagMap != null && !profileMdnEtagMap.isEmpty()) {
                    responseDTO.setMcsXcapRootUriMap(
                            genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
                }
            }
            knLogger.debug(methodName, "responseDTO from Controller :",responseDTO.getServiceAuthStatus());
            //updating the success pegs
            responseDTO.setSuccessPegs(successPegs);

            //welcome sms notification..
            if (oldSubsClientType != newSubsClientType) {
                //if one message is disabled then go in old flow
                if (null != oneMessageEnabled && oneMessageEnabled.equals(ONE_MSG_STATUS.ENABLED.value())) {
                    if (newSubsClientType == com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.HANDSET.value() ||
                            newSubsClientType == com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value()){
                        sendSMSNotification(newSubsClientType, mdn, KnProvConstants.WELCOME_SMS_ID);
                    }
                }
            }

            knLogger.debug(methodName,"responseDTO: ",responseDTO ,"[ subsProvInputDTO  ]",subsProvInputDTO);

            knLogger.info(methodName, "EXIT: Update Subscriber operation ");

            return responseDTO;
        } catch (KnFeatureSetException ex) {
            knLogger.error(methodName, "Feature Set Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(ex.getErrorCode(), ex.getMessage(), ex);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :", vex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw vex;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Update Subscriber");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while update Subscriber", e);
        }

    }
    private static boolean isProvFSBitAllowed(Map<Integer, Integer> inputMap){
        String methodName="isProvFSBitAllowed(Map<String, String>";
        knLogger.debug("Entered ", methodName, " inPutMap :", inputMap);
        boolean isProvFSBitValid= true;

        List<com.kodiak.common.resources.KnConstants.PROV_FS_BIT> declaredProvFSList = Arrays.asList(com.kodiak.common.resources.KnConstants.PROV_FS_BIT.values());
        if(inputMap != null) {
            for(Map.Entry<Integer,Integer> reqProvEntry:inputMap.entrySet()){
                for(com.kodiak.common.resources.KnConstants.PROV_FS_BIT declaredProvFS: declaredProvFSList)
                {
                    if(!reqProvEntry.getKey().equals(declaredProvFS.value())){
                        isProvFSBitValid= false;
                        break;
                    }
                }
            }
        }
        knLogger.debug( methodName, " If correct isProvFSBitValid should be true :", isProvFSBitValid);
        return isProvFSBitValid;
    }
    private int createClientSuppVocoder(List<KnClientVocoderProfilePersistDTO> mdnInfoDTO, KnPersisterTxn persisterTxn)throws KnProvBOException{
        final String methodName = "createClientSuppVocoder(KnIPActivateMDNInfoDTO, KnPersisterTxn)";
        Map<Integer, KnSuppVocoderProfileDTO> serverSuppVocoders;
        int finalSelectedVocoderId = 0;
        try {
            serverSuppVocoders = genInfoUtil.retrieveSuppVocoderProfile(persisterTxn);
            knLogger.debug(methodName," retrieve Supp Vocoder file :",serverSuppVocoders);
            Map<Integer,Integer> clientVocoderId = new TreeMap<>();
            for (KnClientVocoderProfilePersistDTO vocoderProfilePersistDTO : mdnInfoDTO){
                // vocoder id is now profileId variable name kept same
                clientVocoderId.put(vocoderProfilePersistDTO.getPriority(),vocoderProfilePersistDTO.getVocoderId());
            }
            knLogger.debug(methodName," client sent profile id map :",clientVocoderId);
            for(Map.Entry<Integer, Integer> entry : clientVocoderId.entrySet())
            {
                if( serverSuppVocoders.containsKey(entry.getValue())){
                    finalSelectedVocoderId =  serverSuppVocoders.get(entry.getValue()).getProfileId();
                    break;
                }

            }
            if (finalSelectedVocoderId == 0){
                throw new KnProvBOException(KnErrorCodes.BOEntity.VOCODERID_MISMATCH, "Vocoder ID Mismatch");
            }
            String mdn = mdnInfoDTO.get(0).getMdn();
            List<KnClientVocoderProfilePersistDTO> clientVocoderProfilePersistDTOs = new ArrayList<>();
            for (Integer priority : clientVocoderId.keySet()){
                KnClientVocoderProfilePersistDTO clientVocoderProfilePersistDTO = new KnClientVocoderProfilePersistDTO();
                clientVocoderProfilePersistDTO.setVocoderId(clientVocoderId.get(priority));
                clientVocoderProfilePersistDTO.setPriority(priority);
                clientVocoderProfilePersistDTO.setMdn(mdn);
                clientVocoderProfilePersistDTOs.add(clientVocoderProfilePersistDTO);
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            provXDMServerDAO.deleteClientSuppVocoder(mdn,persisterTxn);
            knLogger.debug(methodName,"Deleted existing vocoder from clientSuppVocoder DB");
            provXDMServerDAO.createClientSuppVocoder(clientVocoderProfilePersistDTOs,persisterTxn);
            knLogger.debug(methodName,"Created vocoder from clientSuppVocoder in DB for mdn:",KnGDPRTemplate.mdn(mdn)," List :",clientVocoderId);
            knLogger.debug(methodName," final selected vocoder ID :",finalSelectedVocoderId);

        }catch (KnDAOException ex){
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        }catch (KnBOException e){
            knLogger.error(methodName,"Vocoder ID sent by client not matched");
            throw new KnProvBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return finalSelectedVocoderId;

    }
    private void validateMCXIdsUriFormat(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn)
            throws KnBOException, KnProvBOException {
        // checking if the MCX IDs starts with the Configured URI Schemes
        String methodName = "validateMCXIdsUriFormat(KnIPSubsProvInfoDTO, KnPersisterTxn)";
        int clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
        String mcxUriScheme = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn)
                .get(com.kodiak.xdms.server.common.resources.KnConstants.MCX_URI_SCHEMES);
        knLogger.debug(methodName, "Configured URI Schemes :", mcxUriScheme);
        List<String> mcxUriScemeList = new ArrayList<String>();
        if (mcxUriScheme != null) {
            String[] mcxUriSchemes = mcxUriScheme.split(com.kodiak.xdms.server.common.resources.KnConstants.COMMA);
            mcxUriScemeList = Arrays.asList(mcxUriSchemes);
        }
        if (!mcxUriScemeList.contains(STAR)) {
            validateMcsId(subsProvInputDTO.getMcId(), mcxUriScemeList);
            validateMcsId(subsProvInputDTO.getMcpttId(), mcxUriScemeList);
            validateMcsId(subsProvInputDTO.getMcVideoId(), mcxUriScemeList);
            validateMcsId(subsProvInputDTO.getMcDataId(), mcxUriScemeList);
        }
    }
    private void validateMcsId(String mcsID, List<String> mcxUriScemeList) throws KnProvBOException {
        Boolean flag = false;
        if (mcsID!=null && !mcsID.isEmpty()) {
            for (String mcxUriSceme : mcxUriScemeList) {
                if (mcsID.startsWith(mcxUriSceme)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_MCSIDS, "Invalid MCS Id(s).");
            }
        }
    }
    private void validateIfUserIdAlredyExistsInSystem(String userId, KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvBOException {
        String methodName = "validateIfUserIdAlredyExistsInSystem(KnIPSubsProvInfoDTO, KnPersisterTxn)";
        if (userId != null && !userId.isEmpty()) {
            KnOPSubsProfileInfoDTO subscriberProfileByUserId = null;
            try {
                IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                        .createProvXDMServerDAO();
                subscriberProfileByUserId = provXDMServerDAO.selectSubscriberProfileByUserId(userId, persisterTxn);
            } catch (KnDAOException e) {
                if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode()))
                    knLogger.error(methodName, e);
                else {
                    throw e;
                }
            }
            if (subscriberProfileByUserId != null) {
                knLogger.error(methodName, "User Id already exists.");
                throw new KnProvBOException(KnErrorCodes.BOEntity.USER_ID_ALREADY_EXISTS, "User Id already exists.");
            }
        }
    }
    private boolean checkIfMcxIdIsNotEqualToRequestMdn(String mcxId, String mdn) {
        return null != mcxId && mcxId.startsWith(TELURI)
                && !mcxId.substring(TELURI.length()).equals(mdn);
    }
    private void valiadteIfMCXIdsExistsAsMdnOrAliasMdn(List<String> mcsIds,
                                                       KnPersisterTxn persisterTxn) throws KnProvBOException, KnDAOException {
        String methodName = "valiadteIfMCXIdsExistsAsMdnOrAliasMdn(List<String>, KnPersisterTxn)";
        if (!mcsIds.isEmpty()) {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = provXDMServerDAO
                    .selectSubscriberProfileByMDNorAliasMdn(mcsIds, persisterTxn);
            if (!subsProfileInfoDTOList.isEmpty()) {
                knLogger.error(methodName, "MCS Id(s) already exists as MDN or ALIAS MDN.");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MCSIDS_ALREADY_EXISTS, "MCS Id(s) already exists.");
            }

        }
    }
    private void validateIfMdnOrMcidsPresentInKUIDPOOL(List<String> mcsIds) throws KnDAOException, KnProvBOException {
        String methodName = "validateIfMdnOrMcidsPresentInKUIDPOOL(List<String>)";
        String kuidPrefix = generalCacheUtil.retrieveKUIDPrefix();
        if (kuidPrefix != null) {
            for (String telMdn : mcsIds) {
                if (telMdn.startsWith(kuidPrefix)) {
                    knLogger.error(methodName, "Operation not allowed for billing/pseudo number");
                    throw new KnProvBOException(KnErrorCodes.BOEntity.MDN_PRESENT_IN_KUIDPPOOL,
                            "Operation not allowed for billing/pseudo number");
                }
            }
        }
    }
    private void valiadteIfMCXIdsExistsInSystemForUpdate(KnIPSubsProvInfoDTO subsProvInputDTO,
                                                         KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvBOException {
        String methodName = "valiadteIfMCXIdsExistsInSystemForUpdate(KnIPSubsProvInfoDTO, KnPersisterTxn)";
        Set<String> mcsIdSet= new HashSet<String>();
        if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcId(), subsProvInputDTO.getMdn())
                || checkIfMcxIdIsNotTELURI(subsProvInputDTO.getMcId())) {
            mcsIdSet.add(subsProvInputDTO.getMcId());
        }
        if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcpttId(), subsProvInputDTO.getMdn())
                || checkIfMcxIdIsNotTELURI(subsProvInputDTO.getMcpttId())) {
            mcsIdSet.add(subsProvInputDTO.getMcpttId());
        }
        if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcVideoId(), subsProvInputDTO.getMdn())
                || checkIfMcxIdIsNotTELURI(subsProvInputDTO.getMcVideoId())) {
            mcsIdSet.add(subsProvInputDTO.getMcVideoId());
        }
        if (checkIfMcxIdIsNotEqualToRequestMdn(subsProvInputDTO.getMcDataId(), subsProvInputDTO.getMdn())
                || checkIfMcxIdIsNotTELURI(subsProvInputDTO.getMcDataId())) {
            mcsIdSet.add(subsProvInputDTO.getMcDataId());
        }
        List<String> mcsIds = new ArrayList<String>();
        mcsIds.addAll(mcsIdSet);
        if (!mcsIds.isEmpty()) {
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();
            List<KnOPSubsProfileInfoDTO> subsProfileInfoDTOList = provXDMServerDAO
                    .selectSubscriberProfileByMCSIds(mcsIds, persisterTxn);
            if (!subsProfileInfoDTOList.isEmpty()) {
                for (KnOPSubsProfileInfoDTO knOPSubsProfileInfoDTO : subsProfileInfoDTOList) {
                    if (!knOPSubsProfileInfoDTO.getMdn().equals(subsProvInputDTO.getMdn())) {
                        knLogger.error(methodName, "MCS Id(s) already exists.");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.MCSIDS_ALREADY_EXISTS,
                                "MCS Id(s) already exists.");
                    }
                }
            }
        }
    }
    private boolean checkIfMcxIdIsNotTELURI(String mcxId) {
        return null != mcxId && !mcxId.isEmpty() && !mcxId.startsWith(TELURI);
    }
    private void populateDefaultMCXIdsForUpdate(KnIPSubsProvInfoDTO subsProvInputDTO) {
        if (subsProvInputDTO.getMcId() != null && subsProvInputDTO.getMcId().isEmpty()) {
            subsProvInputDTO.setMcId(TELURI + subsProvInputDTO.getMdn());
        }
        if (subsProvInputDTO.getMcpttId() != null && subsProvInputDTO.getMcpttId().isEmpty()) {
            subsProvInputDTO.setMcpttId(TELURI + subsProvInputDTO.getMdn());
        }
        if (subsProvInputDTO.getMcVideoId() != null && subsProvInputDTO.getMcVideoId().isEmpty()) {
            subsProvInputDTO.setMcVideoId(TELURI + subsProvInputDTO.getMdn());
        }

        if (subsProvInputDTO.getMcDataId() != null && subsProvInputDTO.getMcDataId().isEmpty()) {
            subsProvInputDTO.setMcDataId(TELURI + subsProvInputDTO.getMdn());
        }
    }
    private KnCorpProfilePersistDTO populateCorpProfileInfoDTO(String xdmPttServerId, KnSubsProfilePersistDTO subsProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "populateCorpProfileInfoDTO(KnSubsProfilePersistDTO, KnPersisterTxn)";

        knLogger.debug(methodName, "populating the corpProfilePersistDTO");
        KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
        corpProfilePersistDTO.setExtCorpId(subsProfilePersistDTO.getExtCorpId());
        corpProfilePersistDTO.setXDMSHome(xdmPttServerId);
        corpProfilePersistDTO.setPocHome(subsProfilePersistDTO.getPoCHome());
        corpProfilePersistDTO.setHierarchyType(subsProfilePersistDTO.getHierarchyType());
        return corpProfilePersistDTO;
    }
    private int findInterOPBitStatus(KnOPSubsProfileInfoDTO existingSubsProfileDTO,	Map<String, Integer> existingPkgMap, KnPersisterTxn persisterTxn)
            throws KnFeatureSetException, KnBOException {
        String methodName = "findInterOPBitStatus(KnOPSubsProfileInfoDTO , long ,Map<String,Integer> )";
        knLogger.info(methodName, "Entry ", existingSubsProfileDTO, "existing map ", existingPkgMap,
                "existing subscriberfs ", existingSubsProfileDTO.getSubsFS2());
        BitSet existingFSBitSet = featureSetUtil.convertHexStringToBitSet(existingSubsProfileDTO.getSubsFS2());
        knLogger.debug(methodName, "existing subsFsBitSet - ", existingFSBitSet.toString());

        Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
        String basePkgCode = paramNameValueMap.get(com.kodiak.xdms.server.common.resources.KnConstants.BASE_PKGCODE);
        String basePkgCodeFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(
                existingSubsProfileDTO.getPublicSubscriptionType(),
                existingSubsProfileDTO.getCorporateSubscriptionType(), existingSubsProfileDTO.getSubsClientType(),
                basePkgCode, xdmPttServerId);
        BitSet finalFSBitSet = new BitSet(Long.SIZE);
        BitSet basePkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(basePkgCodeFS);
        finalFSBitSet.or(basePkgCodeBiSet);
        knLogger.info(methodName, "base pkg bitset - ", finalFSBitSet.toString());
        if (!existingPkgMap.isEmpty()) {
            String pkgCodeFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(
                    existingSubsProfileDTO.getPublicSubscriptionType(),
                    existingSubsProfileDTO.getCorporateSubscriptionType(), existingSubsProfileDTO.getSubsClientType(),
                    existingPkgMap, xdmPttServerId);
            BitSet pkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(pkgCodeFS);
            knLogger.debug(methodName, "PkgIds bitset - ", pkgCodeBiSet);
            finalFSBitSet.or(pkgCodeBiSet);
        }
        knLogger.debug(methodName, "final PkgIds bitset  - ", finalFSBitSet.toString());

        int status = LMR_BIT_STATUS.NO_CHANGE.Value();
        if (existingFSBitSet.get(LMR_BIT) && !finalFSBitSet.get(LMR_BIT)) {
            status = LMR_BIT_STATUS.MANUALLY_ENABLED.Value();
        } else if (!existingFSBitSet.get(LMR_BIT) && finalFSBitSet.get(LMR_BIT)) {
            status = LMR_BIT_STATUS.MANUALLY_DISABLED.Value();
        }

        knLogger.info(methodName, "LMR bit status :", status);
        return status;
    }
    private Boolean cleanUpTGSData(String activeFS, String existingActiveFS) {
        boolean cleanUpTGSData = false;
        //get the 20th and 31st bit from the existing DB activefs
        boolean talkGrpSelServerExstActFS = featureSetUtil.getFeatureBitValue(existingActiveFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value());
        boolean talkGrpSelClientExstActFS = featureSetUtil.getFeatureBitValue(existingActiveFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value());

        //get the 20th,28th and 31st bit from the recalculated activefs
        boolean talkGrpScanServerActFS = featureSetUtil.getFeatureBitValue(activeFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELSERVER.value());
        boolean talkGrpScanClientActFS = featureSetUtil.getFeatureBitValue(activeFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSCANCLIENT.value());
        boolean talkGrpSelClientActFS = featureSetUtil.getFeatureBitValue(activeFS, com.kodiak.common.resources.KnConstants.FEATURE_SET.TLKGRPSELCLIENT.value());

        if (!talkGrpScanServerActFS && !talkGrpScanClientActFS) {
            if (talkGrpSelClientActFS) {
                if ((talkGrpSelServerExstActFS && !talkGrpSelClientExstActFS) || (!talkGrpSelServerExstActFS && !talkGrpSelClientExstActFS)) {
                    cleanUpTGSData = true;
                }
            } else {
                if ((talkGrpSelServerExstActFS && !talkGrpSelClientExstActFS) || (!talkGrpSelServerExstActFS && talkGrpSelClientExstActFS)) {
                    cleanUpTGSData = true;
                }
            }
        }


        return cleanUpTGSData;
    }
    private void setMCPTTFeatureDisable(String mdn, String activeFS1, KnPersisterTxn persisterTxn, IProvXDMServerDAO xdmServerDAO)throws KnDAOException {

        String methodName = "setMCPTTFeatureDisable(mdn activefs persisterTxn)";
        knLogger.debug(methodName, "activeFS1 for MCPTT long value -", activeFS1);
        boolean ambientListenerBit = featureSetUtil.getFeatureBitValue(activeFS1, FEATURE_SET.AMBIENTLISTENING.value());
        boolean discreetListenerBit = featureSetUtil.getFeatureBitValue(activeFS1, FEATURE_SET.DISCRETELISTENING.value());
        boolean userCheckBit = featureSetUtil.getFeatureBitValue(activeFS1, FEATURE_SET.USERCHECK.value());
        boolean userEnableDisableBit = featureSetUtil.getFeatureBitValue(activeFS1, FEATURE_SET.USERENABLEDISABLE.value());

        List<KnMCPTTPermInfoDTO> mcpttPermInfoDTOS = xdmServerDAO.getMCPTTPermInfo(mdn, persisterTxn);
        for (KnMCPTTPermInfoDTO permInfoDTO : mcpttPermInfoDTOS) {
            if (permInfoDTO.getPermBitset().equals(0L)) {
                knLogger.debug(methodName, "All MCPTT bits are disable deleting entry for mdn -", KnGDPRTemplate.mdn(mdn), "and for target- ", KnGDPRTemplate.mdn(permInfoDTO.getTargetMdn()));
                xdmServerDAO.deleteTargetEntry(mdn, permInfoDTO.getTargetMdn(), persisterTxn);
            }
        }
        BitSet permBit = new BitSet();
        permBit.set(0, ambientListenerBit);
        permBit.set(1, discreetListenerBit);
        permBit.set(2, userCheckBit);
        permBit.set(3, userEnableDisableBit);
        if (!discreetListenerBit) {
            knLogger.debug(methodName, "Discreet listener bit is disabled hence disabling discreet_enable in DG.MCPTT_PROFILE for mdn -", KnGDPRTemplate.mdn(mdn));
            xdmServerDAO.disableDiscreetEnabled(mdn, persisterTxn);
            knLogger.debug(methodName, "Disabled Success");
            long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
            xdmServerDAO.updateAuthorizationDocEtag(mdn, lastProfileUpdateTime, persisterTxn);
            knLogger.debug(methodName, "Auth doc Etag updated for mdn");
        }
    }
    private void updateMCDevice(KnSubsProfilePersistDTO persistDTO, Long lastProfileUpdateTime, KnOPProvDTO responseDTO,
                                KnPersisterTxn persisterTxn) throws KnDAOException, KnProvBOException {

        KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
        deviceInfoPersistDTO.setDeviceId(persistDTO.getMdn());
        IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB)
                .createXDMServerDAO(xdmPttServerId);
        KnXDMDeviceProvDTO deviceProfile = xdmServerDAO.selectDeviceProfileByDeviceId(persistDTO.getMdn(), persisterTxn);
        if (KnGeneralUtil.getFeatureBitValue(persistDTO.getSubsFS2(), FEATURE_SET.MCDEVICE.value())
                && !(com.kodiak.xdms.server.common.resources.KnConstants.MCSCOMPLIANCE == persistDTO.getMcpttCompliance())) {
            if (deviceProfile != null) {
                deviceInfoPersistDTO.setDeviceshared(com.kodiak.xdms.server.common.resources.KnConstants.MCDEVICESHARED_TYPE.SHARED.Value());
                deviceInfoPersistDTO.setReqDeviceId(persistDTO.getMdn());
                deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.MC_DEVICE.Value());
                deviceInfoPersistDTO.setCorpId(persistDTO.getCorpId());
                xdmServerDAO.updateDeviceInfo(deviceInfoPersistDTO, persisterTxn);
            } else {

                if (lastProfileUpdateTime == null) {
                    lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                }
                deviceInfoPersistDTO.setDeviceStatus(com.kodiak.xdms.server.common.resources.KnConstants.DEVICE_STATUS_OP.ACTIVATED.value());
                deviceInfoPersistDTO.setDeviceActTimeStamp(lastProfileUpdateTime);
                deviceInfoPersistDTO.setDeviceLastUsed(lastProfileUpdateTime);
                deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.MC_DEVICE.Value());
                deviceInfoPersistDTO.setDeviceIMPI(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + persistDTO.getMdn());
                deviceInfoPersistDTO.setDeviceshared(com.kodiak.xdms.server.common.resources.KnConstants.MCDEVICESHARED_TYPE.SHARED.Value());
                deviceInfoPersistDTO.setReqDeviceId(persistDTO.getMdn());
                deviceInfoPersistDTO.setCorpId(persistDTO.getCorpId());
                deviceInfoPersistDTO.setDeviceCreatedAs(com.kodiak.xdms.server.common.resources.KnConstants.DEVICECREATEDAS);

                xdmServerDAO.createDeviceInfo(deviceInfoPersistDTO, persisterTxn);

                KnDeviceImpiInfoPersistDTO deviceImpiInfo = new KnDeviceImpiInfoPersistDTO();
                deviceImpiInfo.setDeviceImpi(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + persistDTO.getMdn());
                deviceImpiInfo.setDeviceImpu(com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + persistDTO.getMdn());
                xdmServerDAO.createDeviceImpiInfo(deviceImpiInfo, persisterTxn);
            }

        } else if (!KnGeneralUtil.getFeatureBitValue(persistDTO.getSubsFS2(), FEATURE_SET.MCDEVICE.value())
                && deviceProfile != null && deviceProfile.getDeviceType()==DEVICE_TYPE.MC_DEVICE.Value()
                && !(com.kodiak.xdms.server.common.resources.KnConstants.MCSCOMPLIANCE == persistDTO.getMcpttCompliance())) {
            deviceInfoPersistDTO.setDeviceType(DEVICE_TYPE.RADIO_NEXT_DEVICE.Value());
            deviceInfoPersistDTO.setDeviceId(deviceProfile.getDeviceId());
            xdmServerDAO.updateDeviceType(deviceInfoPersistDTO, persisterTxn);
        }
    }
    public List<String> getMdnForUPM(String baseMdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        return getMdnForUPM(baseMdn, false, persisterTxn);
    }
    private boolean checkClientType(Integer clientType,int mcsCompliance)
    {
        return clientType == com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
                || clientType == com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
                || clientType == com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
                || clientType == com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value()
                || clientType == com.kodiak.xdms.server.common.resources.KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                || mcsCompliance == com.kodiak.xdms.server.common.resources.KnConstants.MCSCOMPLIANCE;
    }
    private void sendSMSNotification(Integer subsClientType, String mdn, int messageId) {
        String methodName = "sendSMSNotification()";
        knLogger.info(methodName, "Sending WELCOME SMS NOTIFICATION ", subsClientType, KnGDPRTemplate.mdn(mdn), messageId);
        KnProvSMSDTO provSMSDTO = new KnProvSMSDTO();
        provSMSDTO.setMdn(mdn);
        provSMSDTO.setMsgNotificationId(messageId);
        boolean status = provSMSUtil.sendProvSMS(provSMSDTO, null);
        knLogger.info(methodName, "Sent WELCOME SMS NOTIFICATION status ", status);
    }
    public List<String> getMdnForUPM(String baseMdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getMdnForUPM(String, boolean, KnPersisterTxn)";
        boolean ownedTxn = false;
        //KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
        knLogger.info(methodName, "ENTRY: getMdnForUPM for baseMdn:", KnGDPRTemplate.mdn(baseMdn));
        List<String> mdnList=new ArrayList<>();
        Map<String,KnOPSubsProfileInfoDTO> mdnUpmFsMap = new HashMap<>();
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            // retrieving the Subscriber Info
            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();

            mdnUpmFsMap = provXdmServerDAO.getProfileMdnNupmfsByBaseMdn(baseMdn, readOnly, persisterTxn);
            mdnList.addAll(mdnUpmFsMap.keySet());
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : " + ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "getMdnForUPM doesn't exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :" + e.getErrorCode());
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while getMdnForUPM" + e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while getMdnForUPM", e);
        }
        knLogger.info(methodName, "EXIT:  getMdnForUPM - ", KnGDPRTemplate.mdnList(mdnList));

        return mdnList;
    }


}