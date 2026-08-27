/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMCorpMediator.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 28, 2011      7.0
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
package com.kodiak.xdms.mediator.helper;

import com.kodiak.common.commdto.common.*;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.commdto.response.KnXDMGetRegionsRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnDbUtil;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.ggcache.KnGGCache;
import com.kodiak.common.ggcache.dto.KnAsyncJobDTO;
import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.common.ggcache.dto.KnSharedTrustMatrixHierarchyDTO;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.logger.KnAuditHelper;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.xdms.mediator.KnMediatorConstants;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDirChgNotifyDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDocDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.dto.common.KnSIPProxySvcConfigDTO;
import com.kodiak.xdms.server.common.framework.KnFWException;

import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpGroupInfoUtil;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm.KnXDMHierarchyDAO;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileMCPTTConfig;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDestinationAttributeDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubsEmergencyConfigDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants.CREATED_BY;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.GROUP_SIZE_TYPE.VLARGE_GROUP;
import static com.kodiak.xdms.mediator.KnMediatorConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.EXTERNAL_SUBSCRIBER;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isNullOrEmpty;
import static com.kodiak.xdms.server.common.resources.KnConstants.IS_OSM_AUTHORIZE_BIT;
import static com.kodiak.xdms.server.common.resources.KnConstants.DISPATCHER_CLIENT;
import static com.kodiak.xdms.server.common.resources.KnConstants.THIRD_PARTY_DISPATCHERS_CLIENT;


public class KnXDMCorpMediator {
    private static final KnLogger knLogger = KnLogger.getLogger(KnXDMCorpMediator.class);

    private static volatile KnXDMCorpMediator mediator;
    private ICorpClientIntf corpClientIntf;
    private IXcapDiffNotifierIntf notifier;
    private KnXDMCommonMediator commonMediator;
    private KnAuditHelper audit;
    private static final int DIFF_SIZE = 40;
    private static final String CORP_AUDIT = "4002";

    private KnXDMCorpMediator() {
        init();
    }

    private void init() {
        corpClientIntf = new KnCorpClientImpl();
        notifier = new KnXcapDiffNotifierImpl();
        commonMediator = KnXDMCommonMediator.getInstance();
        audit = KnAuditHelper.getAuditLogger(CORP_AUDIT);
    }

    public static synchronized KnXDMCorpMediator getInstance() {
        if (mediator == null) {
            knLogger.info("getInstance()", "Initializing Corp Mediator");
            mediator = new KnXDMCorpMediator();
        }
        return mediator;
    }


    public KnXDMCorpProfileInfoRespDTO authenticate(IXDMRequestDTO authRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "authenticate(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(authRequestDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    authRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDTO = (KnXDMCorpInfoRequestDTO) authRequestDTO;
        KnIPCorpAuthInfoDTO authInfoDto = new KnIPCorpAuthInfoDTO();
        authInfoDto.setExtCorpId(xdmRequestDTO.getExtCorpId());
        authInfoDto.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        authInfoDto.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Retrieving Authenticate - ", authInfoDto);
        KnCorpAuthInfoRespDTO respDto = corpClientIntf.authenticate(authInfoDto, persisterTxn);
        knLogger.debug(methodName, "Retrieved CorpProfie - ", respDto);
        KnXDMCorpProfileInfoRespDTO xdmRespDto = new KnXDMCorpProfileInfoRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        knLogger.debug(methodName, "respDto.getCustomParamMap() - ", respDto.getCustomParamMap());
        xdmRespDto.setCustomParamMap(respDto.getCustomParamMap());
        KnCorpProfileInfoDTO corpProfile = respDto.getProfileInfoDTO();
        if (corpProfile != null) {
            xdmRespDto.setCorpId(corpProfile.getCorpId());
            xdmRespDto.setExtCorpId(corpProfile.getExtCorpId());
            xdmRespDto.setMaxContactsPerSubsc(corpProfile.getMaxContactsPerSubsc());
            xdmRespDto.setMaxCorpGroups(corpProfile.getMaxCorpGroups());
            xdmRespDto.setMaxCorpLists(corpProfile.getMaxCorpLists());
            xdmRespDto.setMaxMemPerCorpGroup(corpProfile.getMaxMemPerCorpGroup());
            xdmRespDto.setMaxMemPerCorpList(corpProfile.getMaxMemPerCorpList());
            xdmRespDto.setMaxExtContactsPerCorp(corpProfile.getMaxExtContactsPerCorp());
            xdmRespDto.setCorpName(corpProfile.getCorpName());
            if (corpProfile.getSupervisoryOverrideEnabled() == 1) {
                xdmRespDto.setSupervisoryOverrideEnabled(KnPersisterConstants.TRUE);
            } else {
                xdmRespDto.setSupervisoryOverrideEnabled(KnPersisterConstants.FALSE);
            }
            xdmRespDto.setMaxDispatchGroup(corpProfile.getMaxDispatchGroup());
            xdmRespDto.setMaxMembersPerDispatchGroup(corpProfile.getMaxMembersPerDispatchGroup());
            Collection<KnDialPlanInfoDTO> dialPlanInfoList = corpProfile.getDialPlanList();
            Collection<KnDialPlanDTO> dialPlanList = new ArrayList<KnDialPlanDTO>();
            if (dialPlanInfoList != null) {
                for (KnDialPlanInfoDTO dialPlanInfoDTO : dialPlanInfoList) {
                    KnDialPlanDTO dialPlanDTO = new KnDialPlanDTO();
                    dialPlanDTO.setCountryCode(dialPlanInfoDTO.getCountryCode());
                    dialPlanDTO.setInternationalDialPrefix(dialPlanInfoDTO.getInternationalDialPrefix());
                    dialPlanDTO.setNationalDialPrefix(dialPlanInfoDTO.getNationalDialPrefix());
                    dialPlanList.add(dialPlanDTO);
                }
            }
            xdmRespDto.setDialPlanList(dialPlanList);
            if (corpProfile.getDispatchEnabled() == 1) {
                xdmRespDto.setDispatchEnabled(KnPersisterConstants.TRUE);
            }
            xdmRespDto.setMaxContactsPerRequest(corpProfile.getMaxContactsPerRequest());
            if (corpProfile.getEnablePocDonorRadioSupport() == 1) {
                xdmRespDto.setPocDonorRadioSupport(KnPersisterConstants.TRUE);
            } else {
                xdmRespDto.setPocDonorRadioSupport(KnPersisterConstants.FALSE);
            }
            xdmRespDto.setMaxSubsAllowedGenActvReq(corpProfile.getMaxSubsAllowedGenActvReq());
            if (1 == corpProfile.getEnableTalkGroup()) {
                xdmRespDto.setEnableTalkGroup(KnPersisterConstants.TRUE);
            }
            xdmRespDto.setMaxNniSubscrPerCorp(corpProfile.getMaxNniSubscrPerCorp());
            xdmRespDto.setMaxPriority(corpProfile.getMaxPriority());
            xdmRespDto.setMaxScanListSize(corpProfile.getMaxScanListSize());
            if (1 == corpProfile.getEnableBCGFeature()) {
                xdmRespDto.setBCGrpEnabled(KnPersisterConstants.TRUE);
            }
            xdmRespDto.setMaxMemPerBCGrp(corpProfile.getMaxMemPerBCGrp());
            xdmRespDto.setPocSysId(corpProfile.getPocSysId());
            xdmRespDto.setGWEnabled(corpProfile.isGWEnabled());
            xdmRespDto.setAllowSUContact(corpProfile.getAllowSUContact());
            xdmRespDto.setMaxDispatchersPerDispatchGrp(corpProfile.getMaxDisptcherPerDispatchGrp());
            xdmRespDto.setConfigFeatureSet(corpProfile.getConfigFeatureSet());
            xdmRespDto.setMaxSGPerGrp(corpProfile.getMaxSGPerGrp());
            //LMR client type changes
            xdmRespDto.setPttRadioScanListSize(corpProfile.getPttRadioScanListSize());
            xdmRespDto.setPttRadioChannelListSize(corpProfile.getPttRadioChannelListSize());
            xdmRespDto.setPttRadioDefScanMode(corpProfile.getPttRadioDefScanMode());
            xdmRespDto.setMaxLocWatcherGrp(corpProfile.getMaxLocWatcherPerGrp());
            xdmRespDto.setMaxSGPatchPerGrp(corpProfile.getMaxSGPatchPerGrp());
            xdmRespDto.setBulkExtContAllowed(corpProfile.getBulkExtContAllowed());
            xdmRespDto.setMaxChannelsPerZone(String.valueOf(corpProfile.getMaxChannelsPerZone()));
            xdmRespDto.setMaxRadioChannels(String.valueOf(corpProfile.getMaxRadioChannels()));
            xdmRespDto.setMaxZones(String.valueOf(corpProfile.getMaxZones()));
            xdmRespDto.setMaxLrgGrpPerCorp(corpProfile.getMaxLrgGrpPerCorp());
            xdmRespDto.setMaxMemPerLrgGrp(corpProfile.getMaxMemPerLrgGrp());
            xdmRespDto.setMaxLrgBGrpPerCorp(corpProfile.getMaxLrgBGrpPerCorp());
            xdmRespDto.setMaxMemPerLrgBGrp(corpProfile.getMaxMemPerLrgBGrp());
            xdmRespDto.setMaxStatusMsgPerOsmList(corpProfile.getMaxStatusMsgPerOsmList());
            xdmRespDto.setMaxStatusShortTextLength(corpProfile.getMaxStatusShortTextLength());
            xdmRespDto.setMaxStatusMsgLength(corpProfile.getMaxStatusMsgLength());
            xdmRespDto.setMaxBulkCorpAdminFsUpdateAllowed(corpProfile.getMaxBulkCorpAdminFsUpdateAllowed());
            xdmRespDto.setMaxUserProfiles(corpProfile.getMaxUserProfiles());
            xdmRespDto.setMaxMemNonAPSubList(corpProfile.getMaxMemNonAPSubList());
            xdmRespDto.setMaxAssignProfiles(corpProfile.getMaxAssignProfiles());
            xdmRespDto.setMaxGrpProfiles(corpProfile.getMaxGrpProfiles());
            xdmRespDto.setMaxGrpsPerCriClient(corpProfile.getMaxGrpsPerCriClient());
            xdmRespDto.setUpmSharingFeature(corpProfile.getUpmSharingFlag());
            xdmRespDto.setMaxCommonContactLisSize(corpProfile.getMaxCommonContactLisSize());
            xdmRespDto.setMaxCommonContactlistPerSub(corpProfile.getMaxCommonContactlistPerSub());
            xdmRespDto.setSelfDnDPrivilege(String.valueOf(corpProfile.getSelfDnDPrivilege()));
            xdmRespDto.setCatAccessPermSet(String.valueOf(corpProfile.getCatAccessPermSet()));
            xdmRespDto.setCatAccessPermUpdateTS(String.valueOf(corpProfile.getCatAccessPermUpdateTS()));
            xdmRespDto.setLargeAgencyDispatch(String.valueOf(corpProfile.getLargeAgencyDispatch()));
            xdmRespDto.setEmergConfigTimerFeature(String.valueOf(corpProfile.getEmergConfigTimerFeature()));
            if (KnPersisterConstants.HIERARCHY == corpProfile.getHierarchyType()) {
                xdmRespDto.setHierarchyEnabled(KnPersisterConstants.TRUE);
            } else {
                xdmRespDto.setHierarchyEnabled(KnPersisterConstants.FALSE);
            }
        }
        knLogger.debug(methodName, "Returning CorpProfile - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpDirRespDTO getCorpDirectory(KnXDMCorpSubscInfoRequestDTO xdmRequestDTO, KnPersisterTxn persisterTxn, int protocolVersion)
            throws KnXDMServerException {

        String methodName = "getCorpDirectory(IXDMRequestDTO, KnPersisterTxn)";
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        contactDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        contactDTO.setUserProfileId(xdmRequestDTO.getUserProfileId());
        contactDTO.setXdmsHome(xdmRequestDTO.getXdmshome());
        contactDTO.setClientPVmajorVer(xdmRequestDTO.getPvMajorVersion());
        contactDTO.setSubsActiveFS2(xdmRequestDTO.getActiveFs2());
        contactDTO.setSubscriberFs2(xdmRequestDTO.getSubscriberFS2());
        contactDTO.setClientType(xdmRequestDTO.getClientType());

        knLogger.debug(methodName, "Retrieving Subscriber diretory list - ", contactDTO);
        KnCorpDirInfoRespDTO respDto = corpClientIntf.getSubsDirectory(contactDTO, persisterTxn);
        KnXDMCorpDirRespDTO xdmRespDto = new KnXDMCorpDirRespDTO();
        populateXdmResponse(xdmRespDto, respDto);

        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        Collection<KnCorpFolderInfoDTO> folders = respDto.getFolders();
        String currEtag = respDto.getEtag();
        Collection<KnAppInfoDTO> appInfoList = new ArrayList<KnAppInfoDTO>();
        if (folders != null) {
            for (KnCorpFolderInfoDTO folder : folders) {
                KnAppInfoDTO appInfoDTO = new KnAppInfoDTO();
                String auid = folder.getAuid();
                appInfoDTO.setAuid(auid);
                Collection<KnCorpDocInfoDTO> corpDocList = folder.getEntries();
                Collection<KnAppDetailsDTO> appDetailsList = new ArrayList<KnAppDetailsDTO>();
                if (corpDocList != null) {
                    for (KnCorpDocInfoDTO doc : corpDocList) {
                        KnAppDetailsDTO appDetailsDTO = new KnAppDetailsDTO();
                        appDetailsDTO.setAuid(auid);
                        appDetailsDTO.setDocName(doc.getDocName());
                        appDetailsDTO.setXcapRoot(respDto.getXcapRoot());
                        appDetailsDTO.setEtag(Long.parseLong(doc.getEtag()));
                        if (protocolVersion >= PROTOCOL_VERSION_18 && doc.getGroupCreatedBy() == CREATED_BY.ABDG.value()) {
                            appDetailsDTO.setIsAbdgGroup(IS_ABDG_GROUP);
                        }
                        if (protocolVersion >= PROTOCOL_VERSION_19 && doc.getMcxGroupInd() == 1) {
                            appDetailsDTO.setMcxGroupInd(doc.getMcxGroupInd());
                        }
                        if (protocolVersion >= PROTOCOL_VERSION_23 && doc.getExternalCorpGroup() == EXTERNAL_SUBSCRIBER) {
                            appDetailsDTO.setExternalCorpGroup(doc.getExternalCorpGroup());
                        }
                        boolean MCXGRPREGRP = KnGeneralUtil.getFeatureBitValue(contactDTO.getSubsActiveFS2(), FEATURE_SET.MCX_GROUP_REGROUP_FLAG_BIT.value());
                        knLogger.debug(methodName, "doc.getIsPreConfiguredGroup(): " + doc.getIsPreConfiguredGroup(), "MCXGRPREGRP: " + MCXGRPREGRP);
                        if (MCXGRPREGRP && doc.getIsPreConfiguredGroup() == EXTERNAL_SUBSCRIBER) {
                            appDetailsDTO.setIsPreConfiguredGroup(doc.getIsPreConfiguredGroup());
                        }
                        appDetailsList.add(appDetailsDTO);
                    }
                }
                appInfoDTO.setAppDetailsList(appDetailsList);
                appInfoList.add(appInfoDTO);
            }
        }

        xdmRespDto.setAppInfoList(appInfoList);
        xdmRespDto.setEtag(currEtag);
        knLogger.debug(methodName, "Returning Subscriber DirList", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO updateSubscriber(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {

        String methodName = "updateSubscriber(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) contactRequestDTO;
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        contactDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        contactDTO.setNewCorpId(xdmRequestDTO.getNewCorpId());
        contactDTO.setClientType(xdmRequestDTO.getClientType());
        contactDTO.setNewClientType(xdmRequestDTO.getNewClientType());
        contactDTO.setPublicSubscriptionType(xdmRequestDTO.getPublicSubscriptionType());
        contactDTO.setNewPublicSubscriptionType(xdmRequestDTO.getNewPublicSubscriptionType());
        contactDTO.setCorpSubscriptionType(xdmRequestDTO.getCorpSubscriptionType());
        contactDTO.setNewCorpSubscriptionType(xdmRequestDTO.getNewCorpSubscriptionType());
        contactDTO.setAutoPairingFlag(xdmRequestDTO.isAutoPairingFlag());
        contactDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        contactDTO.setName(xdmRequestDTO.getName());
        contactDTO.setOldName(xdmRequestDTO.getOldName());
        contactDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        contactDTO.setSubsActiveFS2(xdmRequestDTO.getActiveFs2());
        knLogger.debug(methodName, "Updating Subscriber change impacts - ", contactDTO);
        KnCorpInfoResDTO respDto = corpClientIntf.updateSubscriber(contactDTO, persisterTxn);
        KnXDMCorpDirRespDTO xdmRespDto = new KnXDMCorpDirRespDTO();
        xdmRespDto.setDisabledDispatchMemList(respDto.getDisabledDispatchMemList());
        populateXdmResponse(xdmRespDto, respDto);

        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return respDto;
        }
        Boolean pairingFlag = xdmRequestDTO.isAutoPairingFlag();
        int pairingContactListId = respDto.getPairedContactListId();
        knLogger.info(methodName, "pairingFlag ", pairingFlag);
        knLogger.info(methodName, "pairingContactListId ", pairingContactListId);
        if ((null != pairingFlag && pairingFlag) || (pairingContactListId > 0)) {
            KnXDMCorpSubscInfoRequestDTO corpSubscInfoRequestDTO = new KnXDMCorpSubscInfoRequestDTO();
            corpSubscInfoRequestDTO.setCorpId(String.valueOf(xdmRequestDTO.getNewCorpId()));
            corpSubscInfoRequestDTO.setSubscriberMdn(xdmRequestDTO.getSubscriberMdn());
            corpSubscInfoRequestDTO.setClientType(xdmRequestDTO.getClientType());
            corpSubscInfoRequestDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
            knLogger.info(methodName, "Auto pairing enabled. Invoking for auto pairing");
            pairCorpContact(corpSubscInfoRequestDTO, persisterTxn);
            knLogger.debug(methodName, "After invoking auto pairing...");
        }

        // Notification sending logic moved to KnXDMMediator.java

        Collection<String> deletedMemberList = respDto.getDisabledDispatchMemList();
        knLogger.debug(methodName, "deletedMemberList - ", KnGDPRTemplate.mdnList(deletedMemberList));
        knLogger.debug(methodName, "Returning Response - ", xdmRespDto);
        return respDto;
    }

    public KnCorpResponseDTO pairCorpContact(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {

        String methodName = "pairCorpContact(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) contactRequestDTO;
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        contactDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        contactDTO.setName(xdmRequestDTO.getName());
        contactDTO.setClientType(xdmRequestDTO.getClientType());
        contactDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Pairing Corp contact - ", contactDTO);
        KnCorpResponseDTO respDto = corpClientIntf.pairCorpContact(contactDTO, persisterTxn);
        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return respDto;
        }

        // Notification sending logic moved to KnXDMMediator.java

        return respDto;
    }

    public KnXDMCorpContactListRespDTO getCorpMasterList(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getCorpMasterList(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDTO = (KnXDMCorpInfoRequestDTO) contactRequestDTO;
        KnIPCorpInfoDTO corpInfoDto = new KnIPCorpInfoDTO();
        corpInfoDto.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        corpInfoDto.setETag(xdmRequestDTO.getETag());
        corpInfoDto.setFilterType(xdmRequestDTO.getFilterType());
        corpInfoDto.setFetchSize(xdmRequestDTO.getFetchSize());
        corpInfoDto.setNextToken(xdmRequestDTO.getNextToken());
        corpInfoDto.setSortType(xdmRequestDTO.getSortType());
        corpInfoDto.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "Retrieving MasterList - ", corpInfoDto);
        KnCorpContactListRespDTO respDto = corpClientIntf.getCorpMasterList(corpInfoDto, persisterTxn);
        KnXDMCorpContactListRespDTO xdmRespDto = new KnXDMCorpContactListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        xdmRespDto.setEtag(respDto.getEtag());
        List<KnCorpSubscriberDTO> contactList = respDto.getContactList();
        Map<String, KnTPUserAccountDTO> thirdPartyDetails = respDto.getThirdPartyDetails();
        //contactList population
        if (contactList != null) {
            List<KnXDMCorpContactDTO> contactInfoList = new ArrayList<KnXDMCorpContactDTO>();
            for (KnCorpSubscriberDTO contactDTO : contactList) {
                KnXDMCorpContactDTO xdmContact = new KnXDMCorpContactDTO();
                xdmContact.setMdn(contactDTO.getMdn());
                xdmContact.setName(contactDTO.getName());
                xdmContact.setContactType(String.valueOf(contactDTO.getSubscriptionType()));
                xdmContact.setServiceAuthStatus(String.valueOf(contactDTO.getServiceAuthStatus()));
                xdmContact.setMaxContactsLimitFlag(String.valueOf(contactDTO.getMaxContactLimitFlag()));
                xdmContact.setContactCount(contactDTO.getContactCount());
                int clientType = contactDTO.getClientType();
                xdmContact.setClientType(clientType);
                if (clientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()
                        || clientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()
                        || clientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()) {
                    KnTPUserAccountDTO tpAccInfo = null;
                    if (thirdPartyDetails != null) {
                        tpAccInfo = thirdPartyDetails.get(contactDTO.getMdn());
                    }
                    if (tpAccInfo != null) {
                        xdmContact.setTpUser(tpAccInfo.getTpUser());
                        xdmContact.setTpAccount(tpAccInfo.getTpAccount());
                    }
                }
                xdmContact.setActivationCode(contactDTO.getActivationCode());
                Timestamp activationTime = contactDTO.getActivationTimestamp();
                Timestamp expiryTime = contactDTO.getExpiryTime();
                if (activationTime != null) {
                    xdmContact.setActivationTimestamp(String.valueOf(activationTime.getTime()));
                }
                if (expiryTime != null) {
                    xdmContact.setExpiryTime(String.valueOf(expiryTime.getTime()));
                }
                xdmContact.setActiveFs1(KnGeneralUtil.convertHexStringToLong(contactDTO.getSubsActiveFS2()));
                xdmContact.setActiveFs2(contactDTO.getSubsActiveFS2());
                xdmContact.setUserId(contactDTO.getUserId());
                xdmContact.setDispatchType(contactDTO.getDispatchType());
                xdmContact.setAliasMdn(contactDTO.getAliasMdn());
                xdmContact.setLicenseType(contactDTO.getLicenseType());
                xdmContact.setMcpttCompliance(contactDTO.getMcpttCompliance());
                xdmContact.setCameraTypeExtM(contactDTO.getCameraType());
                if (null != contactDTO.getMcpttId()) {
                    xdmContact.setMcpttId(contactDTO.getMcpttId());
                }
                if (null != contactDTO.getDeviceId()) {
                    xdmContact.setDeviceId(contactDTO.getDeviceId());
                }
                //product flow XDM-5999 discussed with cat, taking the reference of getSubscriberDetails and setting the billingMDN
                xdmContact.setBillingMDN(contactDTO.getMdn());
                xdmContact.setSubscriberFs2(contactDTO.getSubscriberFs2());
                xdmContact.setPackageInfo(contactDTO.getPackageInfo());
                xdmContact.setExtGatewayId(contactDTO.getExtGatewayId());
                contactInfoList.add(xdmContact);
            }
            xdmRespDto.setContactInfoList(contactInfoList);
        }

        List<KnExtSubsDetailsDTO> extContactList = respDto.getExtSubsList();
        if (extContactList != null) {
            List<KnExternalSubsDetailsDTO> externalContactList = new ArrayList<KnExternalSubsDetailsDTO>();
            for (KnExtSubsDetailsDTO contactDTO : extContactList) {
                KnExternalSubsDetailsDTO xdmExtContact = new KnExternalSubsDetailsDTO();
                xdmExtContact.setMdn(contactDTO.getMdn());
                xdmExtContact.setName(contactDTO.getName());
                xdmExtContact.setType(contactDTO.getType());
                externalContactList.add(xdmExtContact);
            }
            xdmRespDto.setExtSubsList(externalContactList);
        }
        if (xdmRequestDTO.getFilterType() != 0) {
            xdmRespDto.setCount(respDto.getCount());
        }
        knLogger.debug(methodName, "Returning MasterList - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpContactListRespDTO getCorpResourceList(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {

        String methodName = "getCorpResourceList(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) contactRequestDTO;
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        contactDTO.setClientType(xdmRequestDTO.getClientType());
        contactDTO.setMcpttId(xdmRequestDTO.getMcpttId());
        if (xdmRequestDTO.getCorpId() != null) {
            contactDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        String etag = xdmRequestDTO.getETag();
        if (etag != null && !etag.isEmpty()) {
            contactDTO.setEtag(Long.parseLong(etag));
        }
        contactDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        contactDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Retrieving Subscriber ResourceList - ", contactDTO);
        KnCorpSubscContactListRespDTO respDto = corpClientIntf.getCorpResourceList(contactDTO, persisterTxn);
        KnXDMCorpContactListRespDTO xdmRespDto = new KnXDMCorpContactListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnCorpSubscriberDTO> contactList = respDto.getContactList();

        knLogger.debug(methodName, "contactList - ", contactList);
        String protocolVersion = contactDTO.getProtocolVersion();
        Integer majorPV = Integer.parseInt(protocolVersion.substring(0, protocolVersion.indexOf('.')));

        List<KnXDMCorpContactDTO> xdmContactList = null;
        if (contactList != null) {
            xdmContactList = new ArrayList<>();
            for (KnCorpSubscriberDTO contact : contactList) {
                KnXDMCorpContactDTO xdmContact = new KnXDMCorpContactDTO();
                xdmContact.setMdn(contact.getMdn());
                xdmContact.setName(contact.getName());
                if (null != contact.getCommonContact() && contact.getCommonContact().equals(ENABLED)) {
                    xdmContact.setCommonContact(contact.getCommonContact());
                }
                if (xdmRequestDTO.getClientType() == PTX_XDMDATA_INTF || majorPV > KnConstants.PROTOCOL_VERSION_7_X) {
                    xdmContact.setContactType(String.valueOf(contact.getContact_type()));
                    xdmContact.setClientType(contact.getClientType());
                }
                if (majorPV > KnConstants.PROTOCOL_VERSION_10_X) {
                    xdmContact.setUa(contact.getUa());
                }
                if (xdmRequestDTO.getClientType() == CLIENT_TYPE_XCAP || majorPV >= KnConstants.PROTOCOL_VERSION_13_X) {
                    xdmContact.setActiveFs1(KnGeneralUtil.convertHexStringToLong(contact.getSubsActiveFS2()));
                    xdmContact.setActiveFs2(contact.getSubsActiveFS2());
                    xdmContact.setAliasMdn(contact.getAliasMdn());
                    xdmContact.setUserId(contact.getUserId());
                    xdmContact.setIsAuthUser(contact.getIsAuthUser());
                }
                if ((xdmRequestDTO.getClientType() == CLIENT_TYPE_XCAP && majorPV >= KnConstants.PROTOCOL_VERSION_22) && contact.getCameraType() != null) {
                    xdmContact.setCameraTypeExtM(contact.getCameraType());
                }
                xdmContactList.add(xdmContact);
            }
        }
        xdmRespDto.setContactInfoList(xdmContactList);
        knLogger.debug(methodName, "Returning CorpSubscriber ResourceList", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpContactListRespDTO getCorpSubscContactList(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getCorpSubscContactList(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) contactRequestDTO;
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        contactDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        String etag = xdmRequestDTO.getETag();
        if (etag != null && !etag.isEmpty()) {
            long etagLong = Long.valueOf(etag);
            if (etagLong > intRange) {
                contactDTO.setEtag(Integer.valueOf(-1));
            } else {
                contactDTO.setEtag(Integer.valueOf(etag));
            }
        }
        contactDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        contactDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "Retrieving Subscriber ContactList - ", contactDTO);
        KnCorpSubscContactListRespDTO respDto = corpClientIntf.getCorpSubscContactList(contactDTO, persisterTxn);
        KnXDMCorpContactListRespDTO xdmRespDto = new KnXDMCorpContactListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnCorpSubscriberDTO> contactList = respDto.getContactList();
        Collection<KnCorpSublistDTO> sublistList = respDto.getSublistList();

        knLogger.debug(methodName, "contactList - ", contactList);
        knLogger.debug(methodName, "sublistList - ", sublistList);

        List<KnXDMCorpContactDTO> xdmContactList = new ArrayList<KnXDMCorpContactDTO>();
        if (contactList != null) {
            for (KnCorpSubscriberDTO contact : contactList) {
                KnXDMCorpContactDTO xdmContact = new KnXDMCorpContactDTO();
                xdmContact.setMdn(contact.getMdn());
                xdmContact.setName(contact.getName());
                xdmContact.setContactType(String.valueOf(contact.getSubscriptionType()));
                xdmContact.setMaxContactsLimitFlag(String.valueOf(contact.getMaxContactLimitFlag()));
                xdmContact.setServiceAuthStatus(String.valueOf(contact.getServiceAuthStatus()));
                xdmContact.setContactCount(contact.getContactCount());
                xdmContact.setClientType(contact.getClientType());
                xdmContact.setClientMajorVersion(contact.getClientPVmajorVer());
                xdmContactList.add(xdmContact);
            }
        }

        Collection<KnXDMCorpSublistDTO> xdmSublistList = new ArrayList<KnXDMCorpSublistDTO>();
        if (sublistList != null) {
            for (KnCorpSublistDTO sublistDTO : sublistList) {
                String corpId = String.valueOf(sublistDTO.getCorpId());
                String sublistId = String.valueOf(sublistDTO.getSublistId());
                String sublistName = sublistDTO.getSublistName();
                String sublistType = String.valueOf(sublistDTO.getSublistType());
                String sublistContactCount = String.valueOf(sublistDTO.getMemberCount());
                KnXDMCorpSublistDTO xdmSublistDTO = new KnXDMCorpSublistDTO(corpId, sublistId, sublistName, sublistType);
                xdmSublistDTO.setMemberCount(sublistContactCount);
                if (null != sublistDTO.getUserProfileListType()) {
                    xdmSublistDTO.setUserProfileListType(sublistDTO.getUserProfileListType());
                }
                if (null != sublistDTO.getListDistribution()) {
                    xdmSublistDTO.setListDistribution(sublistDTO.getListDistribution());
                }
                xdmSublistList.add(xdmSublistDTO);
            }
        }
        xdmRespDto.setContactInfoList(xdmContactList);
        xdmRespDto.setSublistLists(xdmSublistList);
        xdmRespDto.setTotalContactsCount(respDto.getTotalContacts());
        xdmRespDto.setMaxContactsLimitFlag(respDto.getMaxContactLimitFlag());
        xdmRespDto.setTotalGroups(respDto.getTotalGroups());
        xdmRespDto.setTotalUpms(respDto.getTotalUpms());

        knLogger.debug(methodName, "Returning Subscriber ContactList", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO modifyCorpSubscContacts(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {

        String methodName = "modifyCorpSubscContacts(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpContactListRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpContactListRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpContactListRequestDTO xdmRequestDTO = (KnXDMCorpContactListRequestDTO) contactRequestDTO;
        KnIPCorpSubscContactListDTO contactListDTO = new KnIPCorpSubscContactListDTO();
        contactListDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        contactListDTO.setSubscriberMdn(xdmRequestDTO.getOwnerMdn());
        contactListDTO.setETag(String.valueOf(xdmRequestDTO.getEtag()));
        contactListDTO.setAddedMdnList(xdmRequestDTO.getAddedMdnList());
        contactListDTO.setRemovedMdnList(xdmRequestDTO.getRemovedMdnList());
        contactListDTO.setAddedSublistIds(xdmRequestDTO.getAddedSublistIds());
        contactListDTO.setRemovedSublistIds(xdmRequestDTO.getRemovedSublistIds());
        contactListDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        contactListDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        KnCorpResponseDTO respDto = null;
        knLogger.debug(methodName, "Modifying Subscriber ContactList - ", contactListDTO);
        if (!xdmRequestDTO.isUpmCall()) {
            respDto = corpClientIntf.modifyCorpSubscContacts(contactListDTO, persisterTxn);
        } else {
            respDto = corpClientIntf.modifyCorpSubscContactsUpmCall(contactListDTO, persisterTxn);
        }

        if (((contactListDTO.getAddedMdnList() != null && !contactListDTO.getAddedMdnList().isEmpty())
                && (contactListDTO.getRemovedMdnList().isEmpty()))
                || ((contactListDTO.getAddedSublistIds() != null && !contactListDTO.getAddedSublistIds().isEmpty())
                && (contactListDTO.getRemovedSublistIds().isEmpty()))) {
            respDto.setPeg(KnOMConstants.XDM_NUM_CORP_CONTACTS_ADDED);
            knLogger.debug(methodName, "Only Added Peg ", KnOMConstants.XDM_NUM_CORP_CONTACTS_ADDED);
        } else if (((contactListDTO.getRemovedMdnList() != null && !contactListDTO.getRemovedMdnList().isEmpty())
                && (contactListDTO.getAddedMdnList().isEmpty()))
                || ((contactListDTO.getRemovedSublistIds() != null && !contactListDTO.getRemovedSublistIds().isEmpty())
                && (contactListDTO.getAddedSublistIds().isEmpty()))) {
            respDto.setPeg(KnOMConstants.XDM_NUM_CORP_CONTACTS_DELETED);
            knLogger.debug(methodName, "Only Deleted Peg ", KnOMConstants.XDM_NUM_CORP_CONTACTS_DELETED);
        } else {
            respDto.setPeg(KnOMConstants.XDM_NUM_CORP_CONTACTS_UPDATED);
            knLogger.debug(methodName, "Only Added&Deleted Peg ", KnOMConstants.XDM_NUM_CORP_CONTACTS_UPDATED);
        }
        knLogger.debug(methodName, "After the call to modify subscribers contact ", "details. Response - " + respDto);
        return respDto;
    }

    public KnCorpResponseDTO modifyBulkCorpSubscContacts(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {

        String methodName = "modifyBulkCorpSubscContacts(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpContactListRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpContactListRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpContactListRequestDTO xdmRequestDTO = (KnXDMCorpContactListRequestDTO) contactRequestDTO;
        KnIPCorpSubscContactListDTO contactListDTO = new KnIPCorpSubscContactListDTO();
        contactListDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        contactListDTO.setSubscriberMdn(xdmRequestDTO.getOwnerMdn());
        contactListDTO.setETag(String.valueOf(xdmRequestDTO.getEtag()));
        contactListDTO.setAddedMdnList(xdmRequestDTO.getAddedMdnList());
        contactListDTO.setRemovedMdnList(xdmRequestDTO.getRemovedMdnList());
        contactListDTO.setAddedSublistIds(xdmRequestDTO.getAddedSublistIds());
        contactListDTO.setRemovedSublistIds(xdmRequestDTO.getRemovedSublistIds());
        contactListDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        contactListDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        KnCorpResponseDTO respDto = null;
        knLogger.debug(methodName, "Modifying Subscriber ContactList - ", contactListDTO);
        respDto = corpClientIntf.modifyBulkCorpSubscContacts(contactListDTO, persisterTxn);
        knLogger.debug(methodName, "After the call to modify subscribers contact ", "details. Response - " + respDto);
        return respDto;
    }

    public KnCorpResponseDTO pushSublists(IXDMRequestDTO sublistRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "pushSublists(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(sublistRequestDTO instanceof KnXDMCorpDistRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpDistRequestDTO, requestDTO - ",
                    sublistRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpDistRequestDTO xdmRequestDTO = (KnXDMCorpDistRequestDTO) sublistRequestDTO;
        KnIPCorpSublistSubscDistDTO subscDistDTO = new KnIPCorpSublistSubscDistDTO();
        subscDistDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        subscDistDTO.setMdnList(xdmRequestDTO.getMdnList());
        subscDistDTO.setSublistIds(xdmRequestDTO.getSublistIds());
        subscDistDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        subscDistDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Push Sublist to Subscribers - ", subscDistDTO);
        KnCorpResponseDTO respDto = corpClientIntf.pushSublists(subscDistDTO, persisterTxn);
        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return respDto;
        }
        knLogger.debug(methodName, "After the call to push Sublist to subscribers. Response - ", xdmRespDto);

        // Notification sending logic moved to KnXDMMediator.java
        return respDto;
    }

    public KnCorpResponseDTO removeSublist(IXDMRequestDTO subsRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "removeSublist(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(subsRequestDTO instanceof KnXDMCorpDistRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpDistRequestDTO, requestDTO - ",
                    subsRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpDistRequestDTO xdmRequestDTO = (KnXDMCorpDistRequestDTO) subsRequestDTO;
        KnIPCorpSublistSubscDistDTO subscDistDTO = new KnIPCorpSublistSubscDistDTO();
        subscDistDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        subscDistDTO.setMdnList(xdmRequestDTO.getMdnList());
        subscDistDTO.setSublistIds(xdmRequestDTO.getSublistIds());
        subscDistDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        subscDistDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Remove Sublist from Subscribers - ", subscDistDTO);
        KnCorpResponseDTO respDto = corpClientIntf.removeSublist(subscDistDTO, persisterTxn);
        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return respDto;
        }
        knLogger.debug(methodName, "After the Sublist removal from subscribers. Response - ", xdmRespDto);

        // Notification sending logic moved to KnXDMMediator.java
        return respDto;
    }

    public KnCorpResponseDTO addCorpContacts(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "addCorpContacts(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpExtContactListRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpExtContactListRequestDTO - xdmRequestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpExtContactListRequestDTO xdmRequestDTO = (KnXDMCorpExtContactListRequestDTO) contactRequestDTO;
        KnIPCorpContactListDTO contactList = new KnIPCorpContactListDTO();
        Collection<KnXDMMdnInfoDTO> xdmContactList = xdmRequestDTO.getExternalContactList();
        Collection<KnXDMMdnInfoDTO> xdmAliasMdnContactList = xdmRequestDTO.getExternalAliasMdnContactList();
        Collection<KnXDMMdnInfoDTO> xdmUserIdContactList = xdmRequestDTO.getExternalUserIdContactList();

        Collection<KnCorpSubscriberDTO> extContactList = new ArrayList<KnCorpSubscriberDTO>();
        if (xdmContactList != null) {
            for (KnXDMMdnInfoDTO extContact : xdmContactList) {
                KnCorpSubscriberDTO contact = new KnCorpSubscriberDTO(extContact.getMdn(), extContact.getName());
                contact.setSubsType(extContact.getSubsType());
                extContactList.add(contact);
            }
        }
        Collection<KnCorpSubscriberDTO> extAliasMdnContactList = new ArrayList<KnCorpSubscriberDTO>();
        if (xdmAliasMdnContactList != null) {
            for (KnXDMMdnInfoDTO extContact : xdmAliasMdnContactList) {
                KnCorpSubscriberDTO contact = new KnCorpSubscriberDTO();
                contact.setAliasMdn(extContact.getAliasMdn());
                contact.setName(extContact.getName());
                contact.setSubsType(extContact.getSubsType());
                extAliasMdnContactList.add(contact);
            }
        }
        Collection<KnCorpSubscriberDTO> extUserIdContactList = new ArrayList<KnCorpSubscriberDTO>();
        if (xdmUserIdContactList != null) {
            for (KnXDMMdnInfoDTO extContact : xdmUserIdContactList) {
                KnCorpSubscriberDTO contact = new KnCorpSubscriberDTO();
                contact.setUserId(extContact.getUserId());
                contact.setName(extContact.getName());
                contact.setSubsType(extContact.getSubsType());
                extUserIdContactList.add(contact);
            }
        }
        contactList.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        contactList.setContactList(extContactList);
        contactList.setETag(xdmRequestDTO.getETag());
        contactList.setHierarchyType(xdmRequestDTO.getHierarchyType());
        contactList.setContactAliasMdnList(extAliasMdnContactList);
        contactList.setContactUserIdList(extUserIdContactList);
        knLogger.debug(methodName, "Add External Contacts request - ", contactList);
        KnCorpResponseDTO respDto = corpClientIntf.addExtContacts(contactList, persisterTxn);
        knLogger.debug(methodName, "Add External Contacts response - ", contactList);
        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return respDto;
        }
        // Notification sending logic moved to KnXDMMediator.java
        return respDto;
    }

    public KnCorpResponseDTO modifyCorpContacts(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "modifyCorpContacts(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMContactRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMContactRequestDTO xdmRequestDTO = (KnXDMContactRequestDTO) contactRequestDTO;
        KnIPCorpContactDTO subscCntactDetails = new KnIPCorpContactDTO();
        subscCntactDetails.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        subscCntactDetails.setMdn(xdmRequestDTO.getMdn());
        subscCntactDetails.setAliasMdn(xdmRequestDTO.getAliasMdn());
        subscCntactDetails.setUserId(xdmRequestDTO.getUserId());
        subscCntactDetails.setName(xdmRequestDTO.getName());
        subscCntactDetails.setEtag(Long.valueOf(xdmRequestDTO.getEtag()));
        subscCntactDetails.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Modify Subscribers contact Details.");
        KnCorpResponseDTO respDto = corpClientIntf.modifyExtContacts(subscCntactDetails, persisterTxn);
        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        knLogger.debug(methodName, "After the call to the modify subscribers contact details. Response - ", xdmRespDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return respDto;
        }
        // Notification sending logic moved to KnXDMMediator.java
        return respDto;
    }

    public KnCorpResponseDTO removeCorpContacts(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "removeCorpContacts(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpExtContactListRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpExtContactListRequestDTO - xdmRequestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpExtContactListRequestDTO xdmRequestDTO = (KnXDMCorpExtContactListRequestDTO) contactRequestDTO;
        KnIPCorpContactListDTO contactList = new KnIPCorpContactListDTO();
        Collection<KnXDMMdnInfoDTO> xdmContactList = xdmRequestDTO.getExternalContactList();
        Collection<KnXDMMdnInfoDTO> xdmAliasMdnContactList = xdmRequestDTO.getExternalAliasMdnContactList();
        Collection<KnXDMMdnInfoDTO> xdmUserIdContactList = xdmRequestDTO.getExternalUserIdContactList();
        Set<String> mdnList = new HashSet<>();
        LinkedList<String> aliasMdnList = new LinkedList<String>();
        LinkedList<String> userIdList = new LinkedList<String>();
        if (xdmContactList != null)
            mdnList.addAll(xdmContactList.stream().map(KnXDMMdnInfoDTO::getMdn).collect(Collectors.toList()));
        if (xdmAliasMdnContactList != null)
            aliasMdnList.addAll(xdmAliasMdnContactList.stream().map(KnXDMMdnInfoDTO::getAliasMdn).collect(Collectors.toList()));
        if (xdmUserIdContactList != null)
            userIdList.addAll(xdmUserIdContactList.stream().map(KnXDMMdnInfoDTO::getUserId).collect(Collectors.toList()));
        contactList.setMdnList(mdnList);
        contactList.setAliasMdnList(aliasMdnList);
        contactList.setUserIdList(userIdList);
        contactList.setETag(xdmRequestDTO.getETag());
        contactList.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        contactList.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "Delete External Contact For the Corporate.");
        return corpClientIntf.removeExtContacts(contactList, persisterTxn);
    }

    public KnXDMCorpContactListRespDTO getCorpContactDetails(IXDMRequestDTO conactRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getCorpContactDetails(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(conactRequestDTO instanceof KnXDMCorpExtContactListRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMContactRequestDTO - requestDTO - ",
                    conactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpExtContactListRequestDTO xdmRequestDTO = (KnXDMCorpExtContactListRequestDTO) conactRequestDTO;
        KnIPCorpSubscContactListDTO contactListDTO = new KnIPCorpSubscContactListDTO();
        Collection<KnCorpSubscriberDTO> externalContacts = new ArrayList<KnCorpSubscriberDTO>();
        Collection<KnCorpSubscriberDTO> externalAliasMdnContacts = new ArrayList<KnCorpSubscriberDTO>();
        Collection<KnCorpSubscriberDTO> externalUserIdContacts = new ArrayList<KnCorpSubscriberDTO>();

        Collection<KnXDMMdnInfoDTO> externalContactList = xdmRequestDTO.getExternalContactList();
        if (externalContactList != null) {
            for (KnXDMMdnInfoDTO mdnInfoDTO : externalContactList) {
                KnCorpSubscriberDTO subs = new KnCorpSubscriberDTO(mdnInfoDTO.getMdn(), null);
                externalContacts.add(subs);
            }
        }
        Collection<KnXDMMdnInfoDTO> externalAliasMdnContactList = xdmRequestDTO.getExternalAliasMdnContactList();
        if (externalAliasMdnContactList != null) {
            for (KnXDMMdnInfoDTO aliasMdnInfoDTO : externalAliasMdnContactList) {
                KnCorpSubscriberDTO aliasMdnSubs = new KnCorpSubscriberDTO();
                aliasMdnSubs.setAliasMdn(aliasMdnInfoDTO.getAliasMdn());
                externalAliasMdnContacts.add(aliasMdnSubs);
            }
        }
        Collection<KnXDMMdnInfoDTO> externalUserIdContactList = xdmRequestDTO.getExternalUserIdContactList();
        if (externalUserIdContactList != null) {
            for (KnXDMMdnInfoDTO userIdInfoDTO : externalUserIdContactList) {
                KnCorpSubscriberDTO userIdSubs = new KnCorpSubscriberDTO();
                userIdSubs.setUserId(userIdInfoDTO.getUserId());
                externalUserIdContacts.add(userIdSubs);
            }
        }

        contactListDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        contactListDTO.setExternalContacts(externalContacts);
        contactListDTO.setExternalAliasMdnContacts(externalAliasMdnContacts);
        contactListDTO.setExternalUserIdContacts(externalUserIdContacts);
        contactListDTO.setETag(xdmRequestDTO.getETag());
        contactListDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Retrieve External Contact For the Corporate.");
        KnCorpContactListRespDTO respDto = corpClientIntf.getExtContactDetails(contactListDTO, persisterTxn);
        KnXDMCorpContactListRespDTO xdmRespDto = new KnXDMCorpContactListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        Collection<KnCorpSubscriberDTO> contactList = respDto.getContactList();
        if (contactList != null) {
            List<KnXDMCorpContactDTO> xdmContactList = new ArrayList<KnXDMCorpContactDTO>();
            for (KnCorpSubscriberDTO subs : contactList) {
                KnXDMCorpContactDTO xdmContact = new KnXDMCorpContactDTO();
                xdmContact.setMdn(subs.getMdn());
                xdmContact.setAliasMdn(subs.getAliasMdn());
                xdmContact.setUserId(subs.getUserId());
                xdmContact.setName(subs.getName());
                xdmContact.setSubsType(subs.getSubsType());
                xdmContactList.add(xdmContact);
            }
            xdmRespDto.setContactInfoList(xdmContactList);
        }
        xdmRespDto.setEtag(respDto.getEtag());
        knLogger.debug(methodName, "After Retrieval of external contact. Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpSublistRespDTO createSublist(IXDMRequestDTO sublistReqDto, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "createSublist(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(sublistReqDto instanceof KnXDMCorpSublistInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSublistInfoRequestDTO - requestDTO - ",
                    sublistReqDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSublistInfoRequestDTO xdmRequestDTO = (KnXDMCorpSublistInfoRequestDTO) sublistReqDto;
        KnIPCorpSublistInfoDTO sublistInfo = new KnIPCorpSublistInfoDTO();
        sublistInfo.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        sublistInfo.setSublistName(xdmRequestDTO.getSublistName());
        sublistInfo.setDistribution(xdmRequestDTO.isDistribution());
        sublistInfo.setAddedMdnList(xdmRequestDTO.getMdnList());
        Collection<Integer> addedSublistIds = new ArrayList<Integer>();
        Collection<String> sublistIds = xdmRequestDTO.getSublistIds();
        if (sublistIds != null) {
            for (String sublistId : sublistIds) {
                addedSublistIds.add(Integer.valueOf(sublistId));
            }
        }
        sublistInfo.setAddedSublistIds(addedSublistIds);
        sublistInfo.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        sublistInfo.setHierarchyType(xdmRequestDTO.getHierarchyType());
        sublistInfo.setUserProfileListType(xdmRequestDTO.getUserProfileListType());
        sublistInfo.setListDistribution(xdmRequestDTO.getListDistribution());
        sublistInfo.setHierarchyId(xdmRequestDTO.getHierarchyId());
        knLogger.debug(methodName, "Create Sublist - ", sublistInfo);
        knLogger.debug(methodName, "--->xdmRequestDTO.getUserProfileListType() - ", xdmRequestDTO.getUserProfileListType());
        KnCorpSublistRespDTO respDto = corpClientIntf.createSublist(sublistInfo, persisterTxn);
        knLogger.debug(methodName, "After call to create sublist. Response - ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO modifySublist(IXDMRequestDTO sublistReqDto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "modifySublist(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(sublistReqDto instanceof KnXDMCorpSublistInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSublistInfoRequestDTO - requestDTO - ",
                    sublistReqDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSublistInfoRequestDTO xdmRequestDTO = (KnXDMCorpSublistInfoRequestDTO) sublistReqDto;
        KnIPCorpSublistInfoDTO sublistInfo = new KnIPCorpSublistInfoDTO();
        sublistInfo.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        sublistInfo.setETag(Long.valueOf(xdmRequestDTO.getETag()));
        sublistInfo.setSublistName(xdmRequestDTO.getSublistName());
        sublistInfo.setDistribution(xdmRequestDTO.isDistribution());
        sublistInfo.setAddedMdnList(xdmRequestDTO.getMdnList());
        sublistInfo.setSublistId(Integer.valueOf(xdmRequestDTO.getSublistId()));
        Collection<Integer> addedSublistIds = new ArrayList<Integer>();
        Collection<String> sublistIds = xdmRequestDTO.getSublistIds();
        if (sublistIds != null) {
            for (String sublistId : sublistIds) {
                addedSublistIds.add(Integer.valueOf(sublistId));
            }
        }
        sublistInfo.setAddedSublistIds(addedSublistIds);
        sublistInfo.setRemovedMdnList(xdmRequestDTO.getRemovedMdnList());
        sublistInfo.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        sublistInfo.setHierarchyType(xdmRequestDTO.getHierarchyType());

        //KnIPCorpSublistInfoDTO contactList = (KnIPCorpSublistInfoDTO) sublistReqDto;
        knLogger.debug(methodName, "Modify Sublist - ", sublistInfo);
        KnCorpResponseDTO respDto = corpClientIntf.modifySublist(sublistInfo, persisterTxn);
        knLogger.debug(methodName, "After the call to modify sublist. Response - ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO deleteSublist(IXDMRequestDTO sublistReqDto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteSublist(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(sublistReqDto instanceof KnXDMCorpSublistInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSublistInfoRequestDTO - requestDTO - ",
                    sublistReqDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSublistInfoRequestDTO xdmRequestDTO = (KnXDMCorpSublistInfoRequestDTO) sublistReqDto;
        KnIPCorpSublistDTO contactList = new KnIPCorpSublistDTO();

        contactList.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        contactList.setClientType(xdmRequestDTO.getClientType());
        contactList.setSublistId(Integer.valueOf(xdmRequestDTO.getSublistId()));
        contactList.setETag(Long.parseLong(xdmRequestDTO.getETag()));
        contactList.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        contactList.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Delete Sublist - ", contactList);
        KnCorpResponseDTO respDto = corpClientIntf.deleteSublist(contactList, persisterTxn);
        knLogger.debug(methodName, "After the call to delete sublist. Response - ", respDto);
        return respDto;
    }

    public KnXDMCorpContactListRespDTO getSublistDetails(IXDMRequestDTO sublistReqDto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getSublistDetails(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(sublistReqDto instanceof KnXDMCorpSublistInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSublistInfoRequestDTO - requestDTO - ",
                    sublistReqDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSublistInfoRequestDTO xdmRequestDTO = (KnXDMCorpSublistInfoRequestDTO) sublistReqDto;
        KnIPCorpSublistDTO sublistInfo = new KnIPCorpSublistDTO();

        sublistInfo.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        sublistInfo.setClientType(xdmRequestDTO.getClientType());
        sublistInfo.setSublistId(Integer.valueOf(xdmRequestDTO.getSublistId()));
        String etag = xdmRequestDTO.getETag();
        if (etag != null && !"".equals(etag.trim())) {
            sublistInfo.setETag(Long.parseLong(etag.trim()));
        }
        sublistInfo.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        sublistInfo.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Get Sublist Details request - ", sublistInfo);
        KnCorpSublistRespDTO respDto = corpClientIntf.getSublistDetails(sublistInfo, persisterTxn);
        knLogger.debug(methodName, "Get Sublist Details response- ", respDto);
        KnXDMCorpContactListRespDTO xdmRespDto = new KnXDMCorpContactListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        KnCorpSublistDTO sublistDTO = respDto.getSublistDTO();
        KnXDMCorpSublistInfoDTO xdmSublistInfoDTO = new KnXDMCorpSublistInfoDTO();
        xdmSublistInfoDTO.setCorpId(String.valueOf(sublistDTO.getCorpId()));
        xdmSublistInfoDTO.setSublistId(String.valueOf(sublistDTO.getSublistId()));
        xdmSublistInfoDTO.setETag(String.valueOf(sublistDTO.getETag()));
        xdmSublistInfoDTO.setSublistName(sublistDTO.getSublistName());
        xdmSublistInfoDTO.setSublistType(String.valueOf(sublistDTO.getSublistType()));
        //Puneet changes starts
        Integer distPolicy = sublistDTO.getDistributionPolicy();
        knLogger.debug(methodName, "--->distPolicy - ", distPolicy);
        if (distPolicy == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DIST_POLICY_USER_PROFILE) {
            xdmSublistInfoDTO.setUserProfileListType(1);
            knLogger.debug(methodName, "--->distPolicy - ", distPolicy);
        }
        if (distPolicy == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT) {
            xdmSublistInfoDTO.setUserProfileListType(1);
            xdmSublistInfoDTO.setListDistribution(ENABLED);
            knLogger.debug(methodName, "--->distPolicy - ", distPolicy);
        }
        //Puneet changes ends
        Collection<KnCorpContactDTO> memberList = respDto.getMemberList();
        Collection<KnXDMCorpContactDTO> xdmMemberList = new ArrayList<KnXDMCorpContactDTO>();
        if (memberList != null) {
            for (KnCorpContactDTO contact : memberList) {
                KnXDMCorpContactDTO xdmContact = new KnXDMCorpContactDTO();
                xdmContact.setMdn(contact.getMdn());
                xdmContact.setName(contact.getName());
                xdmContact.setContactType(String.valueOf(contact.getSubscriptionType()));
                xdmContact.setMaxContactsLimitFlag(String.valueOf(contact.getMaxContactLimitFlag()));
                xdmContact.setServiceAuthStatus(String.valueOf(contact.getServiceAuthStatus()));
                xdmContact.setContactCount(contact.getContactCount());
                xdmContact.setClientType(contact.getClientType());
                xdmContact.setClientMajorVersion(contact.getClientPVmajorVer());
                xdmMemberList.add(xdmContact);
            }
        }
        xdmSublistInfoDTO.setMemberList(xdmMemberList);
        xdmSublistInfoDTO.setAssignedSubsList(respDto.getAssignedSubsList());
        xdmRespDto.setContactListInfo(xdmSublistInfoDTO);
        knLogger.debug(methodName, "After call to get sublist details. Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpSublistListRespDTO getAllSublist(IXDMRequestDTO corpInfoDto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getAllSublist(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(corpInfoDto instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    corpInfoDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDto = (KnXDMCorpInfoRequestDTO) corpInfoDto;
        KnIPCorpInfoDTO corpInfoDTO = new KnIPCorpInfoDTO();
        corpInfoDTO.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        corpInfoDTO.setClientType(xdmRequestDto.getClientType());
        corpInfoDTO.setEnableAutoPair(xdmRequestDto.getEnableAutoPair());
        corpInfoDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
        corpInfoDTO.setNextToken(xdmRequestDto.getNextToken());
        corpInfoDTO.setFetchSize(xdmRequestDto.getFetchSize());
        corpInfoDTO.setHierarchyId(xdmRequestDto.getHierarchyId());
        knLogger.debug(methodName, "Get All Sublist.");
        KnCorpSublistListRespDTO respDto = corpClientIntf.getAllSublist(corpInfoDTO, persisterTxn);
        knLogger.debug(methodName, "After call to get all sublist list - ", respDto);
        KnXDMCorpSublistListRespDTO xdmRespDto = new KnXDMCorpSublistListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnXDMCorpSublistDTO> xdmSublistList = new ArrayList<KnXDMCorpSublistDTO>();
        Collection<KnCorpSublistDTO> sublistList = respDto.getSublistList();
        if (sublistList != null) {
            for (KnCorpSublistDTO sublist : sublistList) {
                KnXDMCorpSublistDTO sublistDTO = new KnXDMCorpSublistDTO();
                sublistDTO.setCorpId(String.valueOf(sublist.getCorpId()));
                sublistDTO.setSublistId(String.valueOf(sublist.getSublistId()));
                sublistDTO.setSublistName(sublist.getSublistName().trim());
                sublistDTO.setSublistType(String.valueOf(sublist.getSublistType()));
                sublistDTO.setMemberCount(String.valueOf(sublist.getMemberCount()));
                knLogger.debug(methodName, "--->sublist.getDistributionPolicy() - ", sublist.getDistributionPolicy());
                if (sublist.getDistributionPolicy() == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DIST_POLICY_USER_PROFILE) {
                    sublistDTO.setUserProfileListType(1);
                }
                if (com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT == sublist.getDistributionPolicy()) {
                    sublistDTO.setUserProfileListType(ENABLED);
                    sublistDTO.setListDistribution(ENABLED);
                }
                xdmSublistList.add(sublistDTO);
            }
        }
        xdmRespDto.setSublistLists(xdmSublistList);
        xdmRespDto.setCount(respDto.getCount());
        knLogger.debug(methodName, "Returning all sublist list - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpDistRespDTO getDistributionList(IXDMRequestDTO sublistInfoDto, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getDistributionList(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(sublistInfoDto instanceof KnXDMCorpSublistInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSublistInfoRequestDTO - requestDTO - ",
                    sublistInfoDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSublistInfoRequestDTO xdmRequestDto = (KnXDMCorpSublistInfoRequestDTO) sublistInfoDto;
        knLogger.debug(methodName, "Get Distribution List - ", xdmRequestDto);
        KnIPCorpSublistDistDTO distRequestDto = new KnIPCorpSublistDistDTO();
        distRequestDto.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        distRequestDto.setClientType(xdmRequestDto.getClientType());
        distRequestDto.setFilterType(Integer.valueOf(xdmRequestDto.getFilterType()));
        distRequestDto.setSublistId(Integer.valueOf(xdmRequestDto.getSublistId()));
        distRequestDto.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        distRequestDto.setHierarchyType(xdmRequestDto.getHierarchyType());

        KnCorpSublistDistributionRespDTO respDto = corpClientIntf.getDistributionList(distRequestDto, persisterTxn);
        knLogger.debug(methodName, "Distribution List - ", respDto);
        KnXDMCorpDistRespDTO xdmRespDto = new KnXDMCorpDistRespDTO();

        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnCorpContactDTO> contactList = respDto.getContactList();
        Collection<KnCorpGroupInfoDTO> groupList = respDto.getGroupList();

        Collection<KnXDMCorpContactDTO> xdmContactList = new ArrayList<KnXDMCorpContactDTO>();
        if (contactList != null) {
            for (KnCorpContactDTO contact : contactList) {
                KnXDMCorpContactDTO xdmContact = new KnXDMCorpContactDTO();
                xdmContact.setMdn(contact.getMdn());
                xdmContact.setName(contact.getName());
                xdmContact.setContactType(String.valueOf(contact.getSubscriptionType()));
                xdmContact.setMaxContactsLimitFlag(String.valueOf(contact.getMaxContactLimitFlag()));
                xdmContact.setServiceAuthStatus(String.valueOf(contact.getServiceAuthStatus()));
                xdmContact.setDistributionType(contact.getDistributionType());
                xdmContact.setContactCount(contact.getContactCount());
                xdmContact.setClientType(contact.getClientType());
                xdmContactList.add(xdmContact);
            }
            xdmRespDto.setContactList(xdmContactList);
        }
        Collection<KnXDMCorpGroupDTO> xdmGroupList = new ArrayList<KnXDMCorpGroupDTO>();
        if (groupList != null) {
            for (KnCorpGroupInfoDTO group : groupList) {
                KnXDMCorpGroupDTO xdmGroupDTO = new KnXDMCorpGroupDTO();
                xdmGroupDTO.setGroupId(String.valueOf(group.getGroupId()));
                xdmGroupDTO.setGroupName(group.getGroupDisplayName());
                xdmGroupDTO.setMaxGroupMemberLimitFlag(group.getMaxGroupMemLimitFlag());
                xdmGroupDTO.setMemberCount(group.getGroupMemberCount());
                xdmGroupDTO.setGroupType(group.getGroupType());
                xdmGroupDTO.setAvatar(group.getAvatar());
                xdmGroupList.add(xdmGroupDTO);
            }
            xdmRespDto.setGroupList(xdmGroupList);
        }
        knLogger.debug(methodName, "After call to get distribution list. Response - ", xdmRespDto);
        return xdmRespDto;
    }


    public KnCorpGroupInfoRespDTO createCorpGroup(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "createCorpGroup(IXDMRequestDTO, KnPersisterTxn)";

        KnCorpGroupInfoRespDTO respDto;
        if (CORP_BROADCAST_GROUP_TYPE == groupInfoDTO.getGroupType()) {
            knLogger.debug(methodName, "Create broadcast group request - ", groupInfoDTO);
            respDto = corpClientIntf.createBCGroup(groupInfoDTO, persisterTxn);
        } else {
            knLogger.debug(methodName, "Create non-broadcast group request - ", groupInfoDTO);
            respDto = corpClientIntf.createGroup(groupInfoDTO, persisterTxn);
        }
        knLogger.debug(methodName, "Create group response - ", respDto);
        return respDto;
    }


    public KnCorpResponseDTO modifyCorpGroup(IXDMRequestDTO groupRequestDto, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "modifyCorpGroup(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(groupRequestDto instanceof KnXDMCorpGroupInfoRequestDTO)) {
            knLogger.error(methodName, "groupRequestDto not of type KnXDMCorpGroupInfoRequestDTO - requestDTO - ",
                    groupRequestDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpGroupInfoRequestDTO xdmRequestDto = (KnXDMCorpGroupInfoRequestDTO) groupRequestDto;
        KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
        if (xdmRequestDto.getCorpId() != null) groupInfoDTO.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        groupInfoDTO.setClientType(xdmRequestDto.getClientType());
        groupInfoDTO.setOSMListId(xdmRequestDto.getOSMListId());
        groupInfoDTO.setUseProfileId(xdmRequestDto.getUserProfileId());
        if (xdmRequestDto.getGroupId() != null) groupInfoDTO.setGroupId(Integer.valueOf(xdmRequestDto.getGroupId()));
        groupInfoDTO.setGroupType(xdmRequestDto.getGroupType());
        //groupInfoDTO.setETag(Integer.valueOf(xdmRequestDto.getETag()));
        String etag = xdmRequestDto.getETag();
        if (etag != null && !etag.isEmpty()) {
            long etagLong = Long.valueOf(etag);
            if (etagLong > intRange) {
                groupInfoDTO.setETag(Integer.valueOf(-1));
            } else {
                groupInfoDTO.setETag(Integer.valueOf(etag));
            }
        }
        groupInfoDTO.setGroupDisplayName(xdmRequestDto.getGroupName());
        //groupInfoDTO.setAddedMemberMdns(xdmRequestDto.getGroupMembers());
        LinkedList<String> removedMdnList = new LinkedList<>();
        if (xdmRequestDto.getRemovedMdnList() != null) {
            for (String mdn : xdmRequestDto.getRemovedMdnList()) {
                removedMdnList.add(mdn);
            }
        }
        groupInfoDTO.setRemovedMemberMdns(removedMdnList);
        Collection<String> addedSublistIdStrs = xdmRequestDto.getAddedSublistIds();
        Collection<String> removedSublistIdStrs = xdmRequestDto.getRemovedSublistIds();

        Collection<KnXDMGroupMdnInfoDTO> members = xdmRequestDto.getGroupMembers();
        Set<KnCorpContactDTO> addedMdnList = new HashSet<KnCorpContactDTO>();
        if (members != null) {
            for (KnXDMGroupMdnInfoDTO mem : members) {
                KnCorpContactDTO contact = new KnCorpContactDTO();
                contact.setMdn(mem.getMdn());
                contact.setSupervisory(mem.getSupervisor());
                contact.setBroadcaster(mem.getBroadcaster());
                contact.setCallInitiatePermission(mem.getCallInitiatePermission());
                contact.setCallReceivePermission(mem.getCallReceivePermission());
                contact.setInCallPermission(mem.getInCallPermission());
                contact.setVideoCallInitiatePermission(mem.getVideoCallInitiatePermission());
                contact.setVideoCallReceivePermission(mem.getVideoCallReceivePermission());
                contact.setVideoInCallPermission(mem.getVideoInCallPermission());
                contact.setLocWatcher(mem.getLocWatcher());
                contact.setIsOSMAuthorize(mem.getIsOSMAuthorize());
                if (xdmRequestDto.getClientType() != CLIENT_TYPE_CAT_UI) {
                    contact.setGroupModifyPerm(mem.getGrpModifyPerm());
                } else {
                    contact.setGroupModifyPerm(DISABLED);
                }
                addedMdnList.add(contact);
            }
        }
        Collection<String> addedMemberStrList = xdmRequestDto.getAddedMdnList();
        if (addedMemberStrList != null) {
            for (String mdn : addedMemberStrList) {
                KnCorpContactDTO subsc = new KnCorpContactDTO();
                subsc.setMdn(mdn);
                addedMdnList.add(subsc);
            }
        }

        knLogger.debug(methodName, "addedMdnList :: -- ", addedMdnList);
        groupInfoDTO.setAddedMemberDTOMdns(addedMdnList);

        Collection<KnXDMGroupMdnInfoDTO> modifiedMembers = xdmRequestDto.getModifiedMembers();
        Collection<KnCorpGroupMemberDTO> modifiedMembersList = new ArrayList<KnCorpGroupMemberDTO>();
        if (modifiedMembers != null) {
            for (KnXDMGroupMdnInfoDTO mem : modifiedMembers) {
                KnCorpGroupMemberDTO contact = new KnCorpGroupMemberDTO();
                contact.setMdn(mem.getMdn());
                contact.setSupervisory(mem.getSupervisor());
                contact.setBroadcaster(mem.getBroadcaster());
                contact.setCallInitiatePermission(mem.getCallInitiatePermission());
                contact.setCallReceivePermission(mem.getCallReceivePermission());
                contact.setInCallPermission(mem.getInCallPermission());
                contact.setVideoCallInitiatePermission(mem.getVideoCallInitiatePermission());
                contact.setVideoCallReceivePermission(mem.getVideoCallReceivePermission());
                contact.setVideoInCallPermission(mem.getVideoInCallPermission());
                contact.setLocWatcher(mem.getLocWatcher());
                contact.setIsOSMAuthorize(mem.getIsOSMAuthorize());
                if (xdmRequestDto.getClientType() != CLIENT_TYPE_CAT_UI) {
                    contact.setGroupModifyPerm(mem.getGrpModifyPerm());
                } else {
                    contact.setGroupModifyPerm(DISABLED);
                }
                modifiedMembersList.add(contact);
            }
        }
        knLogger.debug(methodName, "modifiedMembersList :: -- ", modifiedMembersList);
        groupInfoDTO.setModifiedMembers(modifiedMembersList);

        Collection<Integer> addedSublistIds = new ArrayList<Integer>();
        if (addedSublistIdStrs != null) {
            for (String sublistId : addedSublistIdStrs) {
                addedSublistIds.add(Integer.valueOf(sublistId));
            }
        }
        Collection<Integer> removedSublistIds = new ArrayList<Integer>();
        if (removedSublistIdStrs != null) {
            for (String sublistId : removedSublistIdStrs) {
                removedSublistIds.add(Integer.valueOf(sublistId));
            }
        }

        List<KnXDMCorpGrpSharedCorpListDTO> sharedList = xdmRequestDto.getGrpSharedCopList();
        if (sharedList != null) {
            knLogger.debug(methodName, "sharedList - ", sharedList);
            List<KnCorpSharedCorpInfo> list = new ArrayList<>(sharedList.size());
            for (KnXDMCorpGrpSharedCorpListDTO reqSharedList : sharedList) {
                KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                sharedCorpInfo.setExtCorpId(reqSharedList.getExtCorpID());
                sharedCorpInfo.setCorpId(reqSharedList.getIntCorpID());
                // Map hierarchyList from request. Each KnXDMHierarchyInfo carries ownerHierarchyId and
                // sharedHierarchyId; both are used — ownerHierarchyId drives trust matrix pair validation
                // (sourceHierarchyId is injected by the controller from the group's hierarchy context).
                sharedCorpInfo.setHierarchyList(reqSharedList.getHierarchyList());
                KnCorpGroupContactDTO grpMemProps = new KnCorpGroupContactDTO();
                if (reqSharedList.getGroupMemberProps() != null) {
                    grpMemProps.setIsSupervisor(reqSharedList.getGroupMemberProps().getSupervisor());
                    grpMemProps.setIsLocSupervisor(reqSharedList.getGroupMemberProps().getLocWatcher());
                    grpMemProps.setIsBroadcaster(reqSharedList.getGroupMemberProps().getBroadcaster());
                    grpMemProps.setIsOSMAuthorized(reqSharedList.getGroupMemberProps().getIsOSMAuthorize());
                    grpMemProps.setCallInitiateAllowed(reqSharedList.getGroupMemberProps().getCallInitiatePermission());
                    grpMemProps.setCallTerminateAllowed(reqSharedList.getGroupMemberProps().getCallReceivePermission());
                    grpMemProps.setIncallAllowed(reqSharedList.getGroupMemberProps().getInCallPermission());
                    grpMemProps.setVideoCallInitiateAllowed(reqSharedList.getGroupMemberProps().getVideoCallInitiatePermission());
                    grpMemProps.setVideoCallReceiveAllowed(reqSharedList.getGroupMemberProps().getVideoCallReceivePermission() );
                    grpMemProps.setVideoInCallAllowed(reqSharedList.getGroupMemberProps().getVideoInCallPermission());
                    sharedCorpInfo.setGrpMemProps(grpMemProps);
                }
                list.add(sharedCorpInfo);
            }
            groupInfoDTO.setCorpSharedCorpInfoList(list);
        }

        // P7-2: Map addedGrpSharedCorpList and removedGrpSharedCorpList from request DTO
        List<KnXDMCorpGrpSharedCorpListDTO> addedSharedDtoList = xdmRequestDto.getAddedGrpSharedCorpList();
        if (addedSharedDtoList != null) {
            List<KnCorpSharedCorpInfo> addedList = new ArrayList<>(addedSharedDtoList.size());
            for (KnXDMCorpGrpSharedCorpListDTO reqEntry : addedSharedDtoList) {
                KnCorpSharedCorpInfo info = new KnCorpSharedCorpInfo();
                info.setExtCorpId(reqEntry.getExtCorpID());
                info.setCorpId(reqEntry.getIntCorpID());
                info.setHierarchyList(reqEntry.getHierarchyList());
                KnCorpGroupContactDTO grpMemProps = new KnCorpGroupContactDTO();
                if (reqEntry.getGroupMemberProps() != null) {
                    grpMemProps.setIsSupervisor(reqEntry.getGroupMemberProps().getSupervisor());
                    grpMemProps.setIsLocSupervisor(reqEntry.getGroupMemberProps().getLocWatcher());
                    grpMemProps.setIsBroadcaster(reqEntry.getGroupMemberProps().getBroadcaster());
                    grpMemProps.setIsOSMAuthorized(reqEntry.getGroupMemberProps().getIsOSMAuthorize());
                    grpMemProps.setCallInitiateAllowed(reqEntry.getGroupMemberProps().getCallInitiatePermission());
                    grpMemProps.setCallTerminateAllowed(reqEntry.getGroupMemberProps().getCallReceivePermission());
                    grpMemProps.setIncallAllowed(reqEntry.getGroupMemberProps().getInCallPermission());
                    info.setGrpMemProps(grpMemProps);
                }
                addedList.add(info);
            }
            groupInfoDTO.setAddedCorpSharedCorpInfoList(addedList);
        }
        List<KnXDMCorpGrpSharedCorpListDTO> removedSharedDtoList = xdmRequestDto.getRemovedGrpSharedCorpList();
        if (removedSharedDtoList != null) {
            List<KnCorpSharedCorpInfo> removedList = new ArrayList<>(removedSharedDtoList.size());
            for (KnXDMCorpGrpSharedCorpListDTO reqEntry : removedSharedDtoList) {
                KnCorpSharedCorpInfo info = new KnCorpSharedCorpInfo();
                info.setExtCorpId(reqEntry.getExtCorpID());
                info.setCorpId(reqEntry.getIntCorpID());
                info.setHierarchyList(reqEntry.getHierarchyList());
                removedList.add(info);
            }
            groupInfoDTO.setRemovedCorpSharedCorpInfoList(removedList);
        }

        groupInfoDTO.setAddedSublistIds(addedSublistIds);
        groupInfoDTO.setRemovedSublistIds(removedSublistIds);
        groupInfoDTO.setContactPairing(xdmRequestDto.isContactPairing());
        groupInfoDTO.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        groupInfoDTO.setOverrideDnd(xdmRequestDto.getOverrideDND());
        groupInfoDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
        groupInfoDTO.setAvatar(xdmRequestDto.getAvatar());
        groupInfoDTO.setVideoPermission(xdmRequestDto.getVideoPermission());
        groupInfoDTO.setHangTimeOut(xdmRequestDto.getHangTimeOut());
        groupInfoDTO.setEmergHangTimeAddOn(xdmRequestDto.getEmergHangTimeAddOn());
        groupInfoDTO.setEmergOverrideDND(xdmRequestDto.getEmergOverrideDND());
        groupInfoDTO.setEmergAutoFloorTimer(xdmRequestDto.getEmergAutoFloorTimer());
        groupInfoDTO.setNewGroupDisplayName(xdmRequestDto.getNewGroupDisplayName());
        groupInfoDTO.setGrpShared(xdmRequestDto.getGrpShared());
        groupInfoDTO.setIsPreConfiguredGroup(xdmRequestDto.getIsPreConfiguredGroup());
        groupInfoDTO.setUgwInterop(xdmRequestDto.getUgwInterop());
        groupInfoDTO.setRecordingFs(xdmRequestDto.getRecordingFS());
        boolean isUpmCall = xdmRequestDto.isUpmCall();
        groupInfoDTO.setAddedOwnerIdList(xdmRequestDto.getAddedOwnerIdList());
        groupInfoDTO.setRemovedOwnerIdList(xdmRequestDto.getRemovedOwnerIdList());
        groupInfoDTO.setAuthorizedLargeTG(xdmRequestDto.getAuthorizedLargeTG());
        KnCorpGrpBasicInfoRespDto grpBasicInfoDto = null;
        if (xdmRequestDto.getClientType() == DYNAMIC_CGMT_INTF || xdmRequestDto.getClientType() == AREA_BASED_DYNAMIC_GROUP) {
            groupInfoDTO.setTpVendorID(xdmRequestDto.getTpVendorID());
            groupInfoDTO.setTpRequestMdn(xdmRequestDto.getTpRequestMdn());
            grpBasicInfoDto = corpClientIntf.getBasicGrpInfoByGrpName(groupInfoDTO, persisterTxn);
        } else {
            grpBasicInfoDto = corpClientIntf.getBasicGrpInfo(groupInfoDTO, persisterTxn);
        }
        KnCorpResponseDTO respDto = null;
        if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == grpBasicInfoDto.getStatus()) {
            KnCorpGrpBasicInfoDTO grpBasicInfo = new KnCorpGrpBasicInfoDTO();
            grpBasicInfo.setGroupId(grpBasicInfoDto.getGroupId());
            grpBasicInfo.setGrpDisplayName(grpBasicInfoDto.getGrpDisplayName());
            grpBasicInfo.setGrpType(grpBasicInfoDto.getGrpType());
            grpBasicInfo.setGroupListId(grpBasicInfoDto.getGroupListId());
            grpBasicInfo.setGrpEtag(grpBasicInfoDto.getGrpEtag());
            grpBasicInfo.setCorpId(grpBasicInfoDto.getCorpId());
            grpBasicInfo.setLmrInteropCapable(grpBasicInfoDto.getLmrInteropCapable());
            grpBasicInfo.setGrpOwner(grpBasicInfoDto.getGrpOwner());
            grpBasicInfo.setGroupCreatedBy(grpBasicInfoDto.getGroupCreateBy());
            grpBasicInfo.setLargeGroup(grpBasicInfoDto.isLargeGroup());
            grpBasicInfo.setGroupProfileId(grpBasicInfoDto.getGroupProfileId());
            grpBasicInfo.setGrpShared(grpBasicInfoDto.getGrpShared());
            grpBasicInfo.setGroupCorpId(grpBasicInfoDto.getGroupCorpId());
            grpBasicInfo.setOverrideDnd(grpBasicInfoDto.getOverrideDnd());
            grpBasicInfo.setIsPreConfiguredGroup(grpBasicInfoDto.getIsPreConfiguredGroup());
            grpBasicInfo.setUgwInterop(grpBasicInfoDto.getUgwInterop());
            grpBasicInfo.setRecordingFs(xdmRequestDto.getRecordingFS());
            groupInfoDTO.setOwnerCorpReq(grpBasicInfoDto.isOwnerCorpReq());
            groupInfoDTO.setUseProfileId(xdmRequestDto.getUserProfileId());
            groupInfoDTO.setGrpLocWatcherMap(xdmRequestDto.getGrpLocWatcherMap());
            groupInfoDTO.setGroupCreatedBy(grpBasicInfoDto.getGroupCreateBy());
            grpBasicInfo.setAuthorizedLargeTG(grpBasicInfoDto.getAuthorizedLargeTG());
            grpBasicInfo.setVideoPermission(grpBasicInfoDto.getVideoPermission());
            grpBasicInfo.setHierarchyId(grpBasicInfoDto.getHierarchyId());
            knLogger.debug(methodName, " isUpmCall:", isUpmCall);
            groupInfoDTO.setUpmCall(isUpmCall);
            if (isUpmCall) {
                groupInfoDTO.setGroupDisplayName(grpBasicInfoDto.getGrpDisplayName());
            }
            if (grpBasicInfoDto.getMcxGrpInd() == VLARGE_GROUP.value()) {
                knLogger.debug(methodName, "Modify MCX Group request - ", groupInfoDTO);
                groupInfoDTO.setUpmCall(isUpmCall);
                respDto = corpClientIntf.modifyMCXGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
                respDto.setMcxGrpInd(1);
                List<Integer> groupIds = new ArrayList<>();
                groupIds.add(grpBasicInfoDto.getGroupId());
                respDto.setGroupIds(groupIds);
            } else if (CORP_BROADCAST_GROUP_TYPE == grpBasicInfoDto.getGrpType()) {
                knLogger.debug(methodName, "Modify Broadcast Group request - ", groupInfoDTO);
                if (!isUpmCall) {
                    respDto = corpClientIntf.modifyBCGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
                } else {
                    groupInfoDTO.setUpmCall(true);
                    respDto = corpClientIntf.upmModifyBCGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
                }
                List<Integer> groupIds = new ArrayList<>();
                groupIds.add(grpBasicInfoDto.getGroupId());
                respDto.setGroupIds(groupIds);
            } else {
                knLogger.debug(methodName, "Modify Standard/Dispatch Group request - ", groupInfoDTO);
                if (!isUpmCall) {
                    respDto = corpClientIntf.modifyGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
                } else {
                    groupInfoDTO.setUpmCall(true);
                    respDto = corpClientIntf.upmModifyGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
                }
                List<Integer> groupIds = new ArrayList<>();
                groupIds.add(grpBasicInfoDto.getGroupId());
                respDto.setGroupIds(groupIds);
            }
        } else {
            respDto = new KnCorpResponseDTO();
            respDto.setStatus(grpBasicInfoDto.getStatus());
            respDto.setStatusCode(grpBasicInfoDto.getStatusCode());
            respDto.setMessage(grpBasicInfoDto.getMessage());
        }
        knLogger.info(methodName, " Exit: ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO deleteCorpGroup(IXDMRequestDTO groupRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {

        String methodName = "deleteCorpGroup(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(groupRequestDTO instanceof KnXDMCorpGroupInfoRequestDTO)) {
            knLogger.error(methodName, "groupDTO not of type KnXDMCorpGroupInfoRequestDTO - requestDTO - ",
                    groupRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpGroupInfoRequestDTO xdmRequestDto = (KnXDMCorpGroupInfoRequestDTO) groupRequestDTO;
        KnCorpSharedCorpInfo corpSharedCorpInfo;
        KnIPCorpGroupDTO groupDTO = new KnIPCorpGroupDTO();
        if (xdmRequestDto.getCorpId() != null) groupDTO.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        if (xdmRequestDto.getGroupId() != null) groupDTO.setGroupId(Integer.valueOf(xdmRequestDto.getGroupId()));
        if (xdmRequestDto.getETag() != null) groupDTO.setETag(Integer.valueOf(xdmRequestDto.getETag()));
        groupDTO.setContactPairing(xdmRequestDto.isContactPairing());
        groupDTO.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        groupDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
        groupDTO.setClientType(xdmRequestDto.getClientType());
        groupDTO.setTpGroupOwner(xdmRequestDto.getOwnerMdn());
        groupDTO.setGroupDisplayName(xdmRequestDto.getGroupName());
        //groupDTO.setVideoPermission(xdmRequestDto.getVideoPermission());

        knLogger.debug(methodName, "Delete Corp Group request - ", groupDTO);
        KnCorpResponseDTO respDto = corpClientIntf.deleteGroup(groupDTO, persisterTxn);
        knLogger.debug(methodName, "Delete Corp Group response - ", respDto);
        return respDto;
    }

    public KnXDMCorpGroupInfoRespDTO getCorpGroupDetails(IXDMRequestDTO groupRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getCorpGroupDetails(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(groupRequestDTO instanceof KnXDMCorpGroupInfoRequestDTO)) {
            knLogger.error(methodName, "groupDTO not of type KnXDMCorpGroupInfoRequestDTO - requestDTO - ",
                    groupRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpGroupInfoRequestDTO xdmRequestDto = (KnXDMCorpGroupInfoRequestDTO) groupRequestDTO;
        List<KnCorpSharedCorpInfo> corpSharedCorpInfos;
        KnIPCorpGroupDTO groupDTO = new KnIPCorpGroupDTO();
        if (!isNullOrEmpty(xdmRequestDto.getCorpId())) {
            groupDTO.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        }
        groupDTO.setClientType(xdmRequestDto.getClientType());
        groupDTO.setGroupId(Integer.valueOf(xdmRequestDto.getGroupId()));
        String etag = xdmRequestDto.getETag();
        if (etag != null && !etag.isEmpty()) {
            long etagLong = Long.valueOf(etag);
            if (etagLong > intRange) {
                groupDTO.setETag(Integer.valueOf(-1));
            } else {
                groupDTO.setETag(Integer.valueOf(etag));
            }
        }
        groupDTO.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        groupDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
        if(xdmRequestDto.getHierarchyId()!=null){
            groupDTO.setHierarchyId(xdmRequestDto.getHierarchyId());
        }

        knLogger.debug(methodName, "Get Corp Group Details request - ", groupDTO);
        KnCorpGroupInfoRespDTO respDto = corpClientIntf.getGroupDetails(groupDTO, persisterTxn);
        knLogger.debug(methodName, "Get Corp Group Details response - ", respDto);

        KnXDMCorpGroupInfoRespDTO xdmRespDto = new KnXDMCorpGroupInfoRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        KnCorpGroupInfoDTO grouInfoDto = respDto.getGroupInfoDTO();
        xdmRespDto.setCorpId(String.valueOf(grouInfoDto.getCorpId()));
        xdmRespDto.setGroupId(String.valueOf(grouInfoDto.getGroupId()));
        xdmRespDto.setGroupName(grouInfoDto.getGroupDisplayName());
        xdmRespDto.setGroupMemberCount(grouInfoDto.getGroupMemberCount());
        xdmRespDto.setGroupType(grouInfoDto.getGroupType());
        xdmRespDto.setOverrideDND(grouInfoDto.getOverrideDnd());
        xdmRespDto.setAvatar(grouInfoDto.getAvatar());
        xdmRespDto.setVideoPermission(grouInfoDto.getVideoPermission());
        //setting OSM Details
        if (respDto.getOSMListDetails() != null) {
            KnCorpOperationStatusMesssageInfoDTO osmDetails = respDto.getOSMListDetails();
            KnXDMCorpOSMInfoRespDTO OSMListDetails = new KnXDMCorpOSMInfoRespDTO();
            Set<KnXDMOSMInfoRespDTO> OSMMsgInfoList = new HashSet<>();
            OSMListDetails.setOSMListName(osmDetails.getOSMListName());
            OSMListDetails.setOSMListId(osmDetails.getOSMListId());
            OSMListDetails.setDefaultValue(osmDetails.getIsDefault());
            for (KnXDMOSMInfoRequestDTO dto : osmDetails.getOSMMsgInfo()) {
                OSMMsgInfoList.add(new KnXDMOSMInfoRespDTO(dto.getMsgId(), dto.getMsgOrderId(), dto.getMsgType(), dto.getMsgShortText(), dto.getMsg()));
            }
            OSMListDetails.setOSMMsgInfo(OSMMsgInfoList);
            xdmRespDto.setOSMListDetails(OSMListDetails);
        }

        int maxMembersAllowed = grouInfoDto.getMaxNumberOfMembers();
        int memberCount = grouInfoDto.getGroupMemberCount();
        int memLimitFlag = -1;
        if (memberCount == maxMembersAllowed) {
            memLimitFlag = 0;
        } else if (memberCount > maxMembersAllowed) {
            memLimitFlag = 1;
        }
        xdmRespDto.setMaxGroupMemberLimitFlag(memLimitFlag);
        xdmRespDto.setEtag(String.valueOf(grouInfoDto.getETag()));
        Collection<KnCorpSublistDTO> sublistList = grouInfoDto.getSublistList();
        Collection<KnXDMCorpSublistDTO> xdmSublistList = null;
        if (sublistList != null) {
            xdmSublistList = new ArrayList<KnXDMCorpSublistDTO>();
            for (KnCorpSublistDTO sublist : sublistList) {
                KnXDMCorpSublistDTO xdmSublistDTO = new KnXDMCorpSublistDTO();
                xdmSublistDTO.setSublistId(String.valueOf(sublist.getSublistId()));
                xdmSublistDTO.setSublistName(sublist.getSublistName());
                xdmSublistDTO.setSublistType(String.valueOf(sublist.getSublistType()));
                xdmSublistDTO.setMemberCount(String.valueOf(sublist.getMemberCount()));
                xdmSublistList.add(xdmSublistDTO);
            }
        }
        Collection<KnCorpGroupMemberDTO> groupMembers = grouInfoDto.getGroupMembers();
        Collection<KnXDMCorpContactDTO> xdmGroupMembers = null;
        if (groupMembers != null) {
            xdmGroupMembers = new ArrayList<KnXDMCorpContactDTO>();
            for (KnCorpGroupMemberDTO groupMember : groupMembers) {
                KnXDMCorpContactDTO xdmGroupMember = new KnXDMCorpContactDTO();
                xdmGroupMember.setMdn(groupMember.getMdn());
                xdmGroupMember.setCorpId(groupMember.getCorpId());
                xdmGroupMember.setName(groupMember.getName());
                xdmGroupMember.setContactType(String.valueOf(groupMember.getSubscriptionType()));
                xdmGroupMember.setMaxContactsLimitFlag(String.valueOf(groupMember.getMaxContactLimitFlag()));
                xdmGroupMember.setServiceAuthStatus(String.valueOf(groupMember.getServiceAuthStatus()));
                xdmGroupMember.setContactCount(groupMember.getContactCount());
                xdmGroupMember.setSupervisor(groupMember.getSupervisory());
                xdmGroupMember.setLocWatcher(groupMember.getLocWatcher());
                xdmGroupMember.setBroadcaster(groupMember.getBroadcaster());
                xdmGroupMember.setClientType(groupMember.getClientType());
                xdmGroupMember.setCallInitiatePermission(groupMember.getCallInitiatePermission());
                xdmGroupMember.setCallReceivePermission(groupMember.getCallReceivePermission());
                xdmGroupMember.setInCallPermission(groupMember.getInCallPermission());
                xdmGroupMember.setVideoCallInitiatePermission(groupMember.getVideoCallInitiatePermission());
                xdmGroupMember.setVideoCallReceivePermission(groupMember.getVideoCallReceivePermission());
                xdmGroupMember.setVideoInCallPermission(groupMember.getVideoInCallPermission());
                xdmGroupMember.setIsOSMAuthorize(groupMember.getIsOSMAuthorize());
                xdmGroupMember.setIsAffiliationEnabled(groupMember.getIsAffiliationEnabled());
                if (groupMember.getGroupModifyPerm() != null)
                    xdmGroupMember.setGroupModifyPerm(groupMember.getGroupModifyPerm());
                xdmGroupMember.setMemberType(groupMember.getMemberType());
                xdmGroupMembers.add(xdmGroupMember);
            }
        }
        knLogger.debug(methodName, "xdmGroupMembers :: ", xdmGroupMembers);
        Collection<KnCorpGroupMemberDTO> groupSupervisor = grouInfoDto.getGroupSupervisor();
        Collection<KnXDMCorpContactDTO> xdmGroupSupervisorMembers = null;
        if (groupSupervisor != null) {
            xdmGroupSupervisorMembers = new ArrayList<KnXDMCorpContactDTO>();
            for (KnCorpGroupMemberDTO groupMember : groupSupervisor) {
                KnXDMCorpContactDTO xdmGroupMember = new KnXDMCorpContactDTO();
                xdmGroupMember.setMdn(groupMember.getMdn());
                xdmGroupMember.setSupervisor(groupMember.getSupervisory());
                xdmGroupMember.setLocWatcher(groupMember.getLocWatcher());
                xdmGroupMember.setBroadcaster(groupMember.getBroadcaster());
                xdmGroupMember.setClientType(groupMember.getClientType());
                xdmGroupMember.setContactType(String.valueOf(groupMember.getContact_type()));
                xdmGroupMember.setName(groupMember.getName());
                xdmGroupMember.setCallInitiatePermission(groupMember.getCallInitiatePermission());
                xdmGroupMember.setCallReceivePermission(groupMember.getCallReceivePermission());
                xdmGroupMember.setInCallPermission(groupMember.getInCallPermission());
                xdmGroupMember.setVideoCallInitiatePermission(groupMember.getVideoCallInitiatePermission());
                xdmGroupMember.setVideoCallReceivePermission(groupMember.getVideoCallReceivePermission());
                xdmGroupMember.setVideoInCallPermission(groupMember.getVideoInCallPermission());
                xdmGroupMember.setIsAffiliationEnabled(groupMember.getIsAffiliationEnabled());
                xdmGroupMember.setIsOSMAuthorize(groupMember.getIsOSMAuthorize());
                if (groupMember.getGroupModifyPerm() != null)
                    xdmGroupMember.setGroupModifyPerm(groupMember.getGroupModifyPerm());
                xdmGroupMember.setMemberType(groupMember.getMemberType());
                xdmGroupMember.setServiceAuthStatus(String.valueOf(groupMember.getServiceAuthStatus()));
                xdmGroupSupervisorMembers.add(xdmGroupMember);
            }
        }
        knLogger.debug(methodName, "xdmGroupSupervisorMembers :", xdmGroupSupervisorMembers);
        xdmRespDto.setGroupMembers(xdmGroupMembers);
        xdmRespDto.setGroupSupervisorMembers(xdmGroupSupervisorMembers);
        xdmRespDto.setSublistList(xdmSublistList);
        xdmRespDto.setEmergAutoFloorTimer(grouInfoDto.getEmergAutoFloorTimer());
        xdmRespDto.setEmergHangTimeAddOn(grouInfoDto.getEmergHangTimeAddOn());
        xdmRespDto.setEmergOverrideDND(grouInfoDto.getEmergOverrideDND());
        xdmRespDto.setHangTimeOut(grouInfoDto.getHangTimeOut());
        xdmRespDto.setEmergGrp(grouInfoDto.isEmergGrp());
        xdmRespDto.setLargeGroup(grouInfoDto.isLargeGroup());
        xdmRespDto.setGroupUri(grouInfoDto.getGroupUri());
        xdmRespDto.setMcxGrpInd(grouInfoDto.getMcxGrpInd());
        xdmRespDto.setAudioCutIn(grouInfoDto.getAudioCutIn());
        xdmRespDto.setGroupProfileId(grouInfoDto.getGroupProfileId());
        xdmRespDto.setGroupServiceType(grouInfoDto.getGroupServiceType());
        xdmRespDto.setIsPreConfiguredGroup(grouInfoDto.getIsPreConfiguredGroup());
        xdmRespDto.setUgwInterop(grouInfoDto.getUgwInterop());
        xdmRespDto.setRecordingFsVal(grouInfoDto.getRecordingFs());
        corpSharedCorpInfos = grouInfoDto.getCorpSharedCorpInfoList();
        List<KnXDMCorpGrpSharedCorpListDTO> xdmCorpGrpSharedCorpListDTOS = new ArrayList<>();
        if (corpSharedCorpInfos != null && !corpSharedCorpInfos.isEmpty()) {
            // Batch-fetch hierarchy names for all sharedHierarchyIds in one DB call
            Map<String, String> hierarchyIdToNameMap = new HashMap<>();
            try {
                Set<String> allSharedHierarchyIds = new HashSet<>();
                for (KnCorpSharedCorpInfo info : corpSharedCorpInfos) {
                    if (info.getHierarchyList() != null) {
                        for (KnXDMHierarchyInfo h : info.getHierarchyList()) {
                            if (h.getSharedHierarchyId() != null && !h.getSharedHierarchyId().isEmpty()) {
                                allSharedHierarchyIds.add(h.getSharedHierarchyId());
                            }
                        }
                    }
                }
                if (!allSharedHierarchyIds.isEmpty()) {
                    String pttServerId = KnGenInfoUtil.getInstance().retrieveLocalXDMPttServerId();
                    KnXDMHierarchyDAO hierarchyDAO = new KnXDMHierarchyDAO(pttServerId);
                    hierarchyIdToNameMap = hierarchyDAO.getHierarchyNamesByIds(new ArrayList<>(allSharedHierarchyIds), persisterTxn);
                    knLogger.debug(methodName, "Fetched hierarchy names for getGroupDetails, count=", hierarchyIdToNameMap.size());
                }
            } catch (Exception e) {
                knLogger.warn(methodName, "Failed to fetch hierarchy names for getGroupDetails - ", e.getMessage());
            }
            for (KnCorpSharedCorpInfo corpSharedCorpInfo : corpSharedCorpInfos) {
                KnXDMCorpGrpSharedCorpListDTO xdmCorpGrpSharedCorpListDTO = new KnXDMCorpGrpSharedCorpListDTO();
                xdmCorpGrpSharedCorpListDTO.setIntCorpID(corpSharedCorpInfo.getCorpId());
                xdmCorpGrpSharedCorpListDTO.setExtCorpID(corpSharedCorpInfo.getExtCorpId());
                if (corpSharedCorpInfo.getGrpMemProps() != null) {
                    KnXDMGroupMdnInfoDTO groupMemberProp = new KnXDMGroupMdnInfoDTO();
                    groupMemberProp.setCallInitiatePermission(corpSharedCorpInfo.getGrpMemProps().getCallInitiateAllowed());
                    groupMemberProp.setCallReceivePermission(corpSharedCorpInfo.getGrpMemProps().getCallTerminateAllowed());
                    groupMemberProp.setInCallPermission(corpSharedCorpInfo.getGrpMemProps().getIncallAllowed());
                    groupMemberProp.setVideoCallInitiatePermission(corpSharedCorpInfo.getGrpMemProps().getVideoCallInitiateAllowed() != null ? corpSharedCorpInfo.getGrpMemProps().getVideoCallInitiateAllowed() : 0);
                    groupMemberProp.setVideoCallReceivePermission(corpSharedCorpInfo.getGrpMemProps().getVideoCallReceiveAllowed() != null ? corpSharedCorpInfo.getGrpMemProps().getVideoCallReceiveAllowed() : 0);
                    groupMemberProp.setVideoInCallPermission(corpSharedCorpInfo.getGrpMemProps().getVideoInCallAllowed() != null ? corpSharedCorpInfo.getGrpMemProps().getVideoInCallAllowed() : 0);
                    groupMemberProp.setIsOSMAuthorize(corpSharedCorpInfo.getGrpMemProps().getIsOSMAuthorized());
                    groupMemberProp.setSupervisor(corpSharedCorpInfo.getGrpMemProps().getIsSupervisor());
                    groupMemberProp.setBroadcaster(corpSharedCorpInfo.getGrpMemProps().getIsBroadcaster());
                    groupMemberProp.setLocWatcher(corpSharedCorpInfo.getGrpMemProps().getIsLocSupervisor());
                    xdmCorpGrpSharedCorpListDTO.setGroupMemberProps(groupMemberProp);
                }
                // UCSPROVCONFIG-3162: Pass hierarchyList from controller, enriched with names fetched from DB
                if (corpSharedCorpInfo.getHierarchyList() != null) {
                    if (!hierarchyIdToNameMap.isEmpty()) {
                        for (KnXDMHierarchyInfo h : corpSharedCorpInfo.getHierarchyList()) {
                            if (h.getSharedHierarchyId() != null && !h.getSharedHierarchyId().isEmpty()) {
                                h.setSharedHierarchyName(hierarchyIdToNameMap.getOrDefault(h.getSharedHierarchyId(), ""));
                            }
                        }
                    }
                    xdmCorpGrpSharedCorpListDTO.setHierarchyList(corpSharedCorpInfo.getHierarchyList());
                }
                xdmCorpGrpSharedCorpListDTOS.add(xdmCorpGrpSharedCorpListDTO);
            }
            xdmRespDto.setGrpOwnerCorpId(grouInfoDto.getGrpOwnerCorpId());
            xdmRespDto.setGrpOwnerExtCorpId(grouInfoDto.getGrpOwnerExtCorpId());
            if (null != grouInfoDto.getGrpShared()) {
                xdmRespDto.setGrpShared(String.valueOf(grouInfoDto.getGrpShared()));
            }
            xdmRespDto.setGrpSharedCopList(xdmCorpGrpSharedCorpListDTOS);
        }
        xdmRespDto.setOwnerIdList(grouInfoDto.getOwnerIdList());
        xdmRespDto.setSharedIdList(grouInfoDto.getSharedIdList());
        xdmRespDto.setAuthorizedLargeTG(grouInfoDto.getAuthorizedLargeTG());
        xdmRespDto.setVideoPermission(grouInfoDto.getVideoPermission());
        xdmRespDto.setOwnerAgencyName(grouInfoDto.getOwnerAgencyName());
        xdmRespDto.setOwnerOrganizationName(grouInfoDto.getOwnerOrganizationName());
        knLogger.debug(methodName, "Returning get corp Group Details response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpGroupInfoRespDTO getSubsGroupDetails(IXDMRequestDTO groupRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getSubsGroupDetails(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(groupRequestDTO instanceof KnXDMCorpGroupInfoRequestDTO)) {
            knLogger.error(methodName, "groupRequestDTO not of type KnXDMCorpGroupInfoRequestDTO - requestDTO - ",
                    groupRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpGroupInfoRequestDTO xdmRequestDto = (KnXDMCorpGroupInfoRequestDTO) groupRequestDTO;
        KnIPCorpGroupDTO groupDTO = new KnIPCorpGroupDTO();
        if (xdmRequestDto.getGroupId() != null) groupDTO.setGroupId(Integer.valueOf(xdmRequestDto.getGroupId()));
        if (xdmRequestDto.getCorpId() != null) groupDTO.setCorpId(Integer.parseInt(xdmRequestDto.getCorpId()));
        groupDTO.setClientType(xdmRequestDto.getClientType());
        groupDTO.setOwner(xdmRequestDto.getOwnerMdn());
        String etag = xdmRequestDto.getETag();
        if (etag != null && !etag.isEmpty()) {
            long etagLong = Long.valueOf(etag);
            if (etagLong > intRange) {
                groupDTO.setETag(Integer.valueOf(-1));
            } else {
                groupDTO.setETag(Integer.valueOf(etag));
            }
        }
        knLogger.debug(methodName, "KnXDMCorpGroupInfoRequestDTO mcpttid ", xdmRequestDto.getMcPttId());
        groupDTO.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        groupDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
        groupDTO.setMcPttId(xdmRequestDto.getMcPttId());
        knLogger.debug(methodName, "Get Corp Group Details request - ", groupDTO);
        KnCorpGroupInfoRespDTO respDto = null;
        if (groupDTO.getClientType() == CLIENT_TYPE_WCSR || groupDTO.getClientType() == CLIENT_TYPE_XCAP
                || groupDTO.getClientType() == PTX_XDMDATA_INTF) {
            respDto = corpClientIntf.getSubsCorpGroupDetails(groupDTO, persisterTxn);
        } else if (groupDTO.getClientType() == DYNAMIC_CGMT_INTF || groupDTO.getClientType() == AREA_BASED_DYNAMIC_GROUP) {
            groupDTO.setTpGroupOwner(xdmRequestDto.getOwnerMdn());
            groupDTO.setGroupDisplayName(xdmRequestDto.getGroupName());
            respDto = corpClientIntf.getTpSubsCorpGroupDetails(groupDTO, persisterTxn);
        } else {
            respDto = corpClientIntf.getSubsGroupDetails(groupDTO, persisterTxn);
        }
        knLogger.debug(methodName, "Get Corp Group Details response - ", respDto);
        /*int clientType = xdmRequestDto.getClientType();
        KnCorpGroupInfoRespDTO respDto;
        knLogger.debug( methodName, "Get Corp Group Details request - ", groupDTO);
        if(KnConstants.CLIENT_TYPE_XCAP == clientType){
           respDto = corpClientIntf.getSubsGrpDetsXcap(groupDTO, persisterTxn);
        }
        else{
          respDto = corpClientIntf.getSubsGroupDetails(groupDTO, persisterTxn);
        } */
        knLogger.debug(methodName, "Get Corp Group Details response - ", respDto);

        KnXDMCorpGroupInfoRespDTO xdmRespDto = new KnXDMCorpGroupInfoRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        KnCorpGroupInfoDTO grouInfoDto = respDto.getGroupInfoDTO();
        xdmRespDto.setGroupId(String.valueOf(grouInfoDto.getGroupId()));
        xdmRespDto.setGroupName(grouInfoDto.getGroupDisplayName());
        xdmRespDto.setEtag(String.valueOf(grouInfoDto.getETag()));
        xdmRespDto.setCorpId(String.valueOf(grouInfoDto.getCorpId()));
        xdmRespDto.setMaxGroupMemberLimitFlag(grouInfoDto.getMaxNumberOfMembers());
        xdmRespDto.setGroupType(grouInfoDto.getGroupType());
        xdmRespDto.setAvatar(grouInfoDto.getAvatar());
        xdmRespDto.setOverrideDND(grouInfoDto.getOverrideDnd());
        xdmRespDto.setProtocolVersion(grouInfoDto.getSubscrProtocolversion());
        xdmRespDto.setEmergAutoFloorTimer(grouInfoDto.getEmergAutoFloorTimer());
        xdmRespDto.setEmergHangTimeAddOn(grouInfoDto.getEmergHangTimeAddOn());
        xdmRespDto.setEmergOverrideDND(grouInfoDto.getEmergOverrideDND());
        xdmRespDto.setHangTimeOut(grouInfoDto.getHangTimeOut());
        xdmRespDto.setEmergGrp(grouInfoDto.isEmergGrp());
        xdmRespDto.setGroupCreatedBy(grouInfoDto.getGroupCreatedBy());
        xdmRespDto.setLmrInterpCapable(grouInfoDto.getLmrInteropCapable());
        xdmRespDto.setLargeGroup(grouInfoDto.isLargeGroup());
        xdmRespDto.setMemberListCount(grouInfoDto.getMemberListCount());
        xdmRespDto.setPocHome(grouInfoDto.getPocHome());
        xdmRespDto.setExternalCorpGroup(grouInfoDto.getExternalCorpGroup());
        if (grouInfoDto.getIsPreConfiguredGroup() != null) {
            xdmRespDto.setIsPreConfiguredGroup(grouInfoDto.getIsPreConfiguredGroup());
        }
        if (grouInfoDto.getGrpShared() != null) {
            xdmRespDto.setGrpShared(String.valueOf(grouInfoDto.getGrpShared()));
        }
        if (respDto.getOSMListDetails() != null) {
            KnCorpOperationStatusMesssageInfoDTO osmDetails = respDto.getOSMListDetails();
            KnXDMCorpOSMInfoRespDTO OSMListDetails = new KnXDMCorpOSMInfoRespDTO();
            Set<KnXDMOSMInfoRespDTO> OSMMsgInfoList = new HashSet<>();
            OSMListDetails.setOSMListName(osmDetails.getOSMListName());
            OSMListDetails.setOSMListId(osmDetails.getOSMListId());
            OSMListDetails.setDefaultValue(osmDetails.getIsDefault());
            for (KnXDMOSMInfoRequestDTO dto : osmDetails.getOSMMsgInfo()) {
                OSMMsgInfoList.add(new KnXDMOSMInfoRespDTO(dto.getMsgId(), dto.getMsgOrderId(), dto.getMsgType(), dto.getMsgShortText(), dto.getMsg()));
            }
            OSMListDetails.setOSMMsgInfo(OSMMsgInfoList);
            xdmRespDto.setOSMListDetails(OSMListDetails);
        }
        int protocol = 0;
        String protocolVersion = null;
        if (groupDTO.getClientType() != PTX_XDMDATA_INTF && grouInfoDto.getSubscrProtocolversion() != null) {
            protocolVersion = grouInfoDto.getSubscrProtocolversion();
            protocol = Integer.parseInt(protocolVersion.substring(0, protocolVersion.indexOf('.')));
            if (protocol >= PROTOCOL_VERSION_7) {
                xdmRespDto.setGroupMemberCount(grouInfoDto.getGroupMemCount());
            }
        } else {
            xdmRespDto.setGroupMemberCount(grouInfoDto.getGroupMemCount());
        }
        if (protocol >= PROTOCOL_VERSION_13 && groupDTO.getClientType() == CLIENT_TYPE_XCAP
                && grouInfoDto.getGroupCreatedBy() == GROUP_CREATED_BY_ABDG_INTF) {
            xdmRespDto.setGroupOwner(grouInfoDto.getTpGroupOwner());
        } else if (groupDTO.getClientType() == CLIENT_TYPE_WCSR && grouInfoDto.getGroupCreatedBy() == GROUP_CREATED_BY_ABDG_INTF) {
            xdmRespDto.setGroupOwner(grouInfoDto.getTpGroupOwner());
        }
        Collection<KnCorpGroupMemberDTO> groupMembers = grouInfoDto.getGroupMembers();
        Collection<KnXDMCorpContactDTO> xdmGroupMembers = null;
        if (groupMembers != null) {
            xdmGroupMembers = new ArrayList<KnXDMCorpContactDTO>();
            for (KnCorpGroupMemberDTO groupMember : groupMembers) {
                KnXDMCorpContactDTO xdmGroupMember = new KnXDMCorpContactDTO();
                xdmGroupMember.setMdn(groupMember.getMdn());
                xdmGroupMember.setName(groupMember.getName());
                xdmGroupMember.setContactType(String.valueOf(groupMember.getSubscriptionType()));
                xdmGroupMember.setMaxContactsLimitFlag(String.valueOf(groupMember.getMaxContactLimitFlag()));
                xdmGroupMember.setServiceAuthStatus(String.valueOf(groupMember.getServiceAuthStatus()));
                xdmGroupMember.setSupervisor(groupMember.getSupervisory());
                xdmGroupMember.setBroadcaster(groupMember.getBroadcaster());
                xdmGroupMember.setCallInitiatePermission(groupMember.getCallInitiatePermission());
                xdmGroupMember.setCallReceivePermission(groupMember.getCallReceivePermission());
                xdmGroupMember.setInCallPermission(groupMember.getInCallPermission());
                xdmGroupMember.setVideoCallInitiatePermission(groupMember.getVideoCallInitiatePermission());
                xdmGroupMember.setVideoCallReceivePermission(groupMember.getVideoCallReceivePermission());
                xdmGroupMember.setVideoInCallPermission(groupMember.getVideoInCallPermission());
                if (groupMember.getGroupModifyPerm() != null)
                    xdmGroupMember.setGroupModifyPerm(groupMember.getGroupModifyPerm());
                xdmGroupMember.setIsOSMAuthorize(groupMember.getIsOSMAuthorize());
                xdmGroupMember.setCorpId(groupMember.getCorpId());
                if ((protocol >= PROTOCOL_VERSION_7 && groupRequestDTO.getClientType() == CLIENT_TYPE_XCAP)
                        || (groupDTO.getClientType() == PTX_XDMDATA_INTF) || groupDTO.getClientType() == CLIENT_TYPE_WCSR) {

                    if (groupDTO.getClientType() == CLIENT_TYPE_WCSR
                            && groupMember.getClientType() == 1
                            && groupMember.getLicenseType() == 1) {
                        //for user license in wcsr.
                        xdmGroupMember.setClientType(0);
                    } else {
                        xdmGroupMember.setClientType(groupMember.getClientType());
                    }

                    xdmGroupMember.setContactType(String.valueOf(groupMember.getContact_type()));
                }
                if ((protocol >= PROTOCOL_VERSION_10 && groupRequestDTO.getClientType() == CLIENT_TYPE_XCAP)) {
                    xdmGroupMember.setLocWatcher(groupMember.getLocWatcher());
                } else if (groupRequestDTO.getClientType() != CLIENT_TYPE_XCAP) {
                    xdmGroupMember.setLocWatcher(groupMember.getLocWatcher());
                }
                if (protocol > KnConstants.PROTOCOL_VERSION_10_X && groupRequestDTO.getClientType() == CLIENT_TYPE_XCAP) {
                    xdmGroupMember.setUa(groupMember.getUa());
                }
                if (protocol >= KnConstants.PROTOCOL_VERSION_13_X && groupRequestDTO.getClientType() == CLIENT_TYPE_XCAP) {
                    if (groupMember.getSubsActiveFS2() != null)
                        xdmGroupMember.setActiveFs1(KnGeneralUtil.convertHexStringToLong(groupMember.getSubsActiveFS2()));
                    xdmGroupMember.setActiveFs2(groupMember.getSubsActiveFS2());
                    xdmGroupMember.setAliasMdn(groupMember.getAliasMdn());
                    xdmGroupMember.setUserId(groupMember.getUserId());
                }
                xdmGroupMember.setIsAffiliationEnabled(groupMember.getIsAffiliationEnabled());
                xdmGroupMembers.add(xdmGroupMember);
            }
        }
        xdmRespDto.setGroupMembers(xdmGroupMembers);
        if ((protocol >= KnConstants.PROTOCOL_VERSION_19_X && groupRequestDTO.getClientType() == CLIENT_TYPE_XCAP) || (groupRequestDTO.getClientType() == PTX_XDMDATA_INTF)) {
            if (grouInfoDto.getMcxGrpInd() != null) {
                xdmRespDto.setMcxGrpInd(grouInfoDto.getMcxGrpInd());
            }
        }

        xdmRespDto.setGroupProfileId(grouInfoDto.getGroupProfileId());
        xdmRespDto.setUgwConfig(grouInfoDto.getUgwConfig());
        xdmRespDto.setUgwInterop(grouInfoDto.getUgwInterop());
        xdmRespDto.setRecordingFsVal(grouInfoDto.getRecordingFs());
        xdmRespDto.setAuthorizedLargeTG(grouInfoDto.getAuthorizedLargeTG());
        xdmRespDto.setVideoPermission(grouInfoDto.getVideoPermission());
        knLogger.debug(methodName, "Returning get corp Group Details response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpGroupListRespDTO getCorpGroupList(IXDMRequestDTO corpInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getCorpGroupList(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(corpInfo instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    corpInfo.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpInfoRequestDTO xdmRequestDto = (KnXDMCorpInfoRequestDTO) corpInfo;
        List<KnCorpSharedCorpInfo> corpSharedCorpInfos;
        KnIPCorpInfoDTO corpInfoDTO = new KnIPCorpInfoDTO();
        corpInfoDTO.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        corpInfoDTO.setClientType(xdmRequestDto.getClientType());
        corpInfoDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
        corpInfoDTO.setFetchSize(xdmRequestDto.getFetchSize());
        corpInfoDTO.setNextToken(xdmRequestDto.getNextToken());
        corpInfoDTO.setHierarchyId(xdmRequestDto.getHierarchyId());

        knLogger.debug(methodName, "Get Corp Group List request - ", corpInfoDTO);
        KnCorpGroupListRespDTO respDto = corpClientIntf.getGroupList(corpInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Get Corp Group List response - ", respDto);

        KnXDMCorpGroupListRespDTO xdmRespDto = new KnXDMCorpGroupListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnCorpGroupInfoDTO> groupList = respDto.getGroupList();
        Collection<KnXDMCorpGroupDTO> xdmGroupList = new ArrayList<KnXDMCorpGroupDTO>();
        if (groupList != null) {
            for (KnCorpGroupInfoDTO group : groupList) {
                KnXDMCorpGroupDTO xdmGroupdto = new KnXDMCorpGroupDTO();
                xdmGroupdto.setCorpId(String.valueOf(group.getCorpId()));
                xdmGroupdto.setGroupId(String.valueOf(group.getGroupId()));
                xdmGroupdto.setGroupName(group.getGroupDisplayName());
                xdmGroupdto.setMaxGroupMemberLimitFlag(group.getMaxGroupMemLimitFlag());
                xdmGroupdto.setMemberCount(group.getMemberCount());
                xdmGroupdto.setGroupType(group.getGroupType());
                xdmGroupdto.setAvatar(group.getAvatar());
                xdmGroupdto.setVideoPermission(group.getVideoPermission());
                xdmGroupdto.setLargeGrp(group.isLargeGroup());
                xdmGroupdto.setGroupUri(group.getGroupUri());
                xdmGroupdto.setMcxGrpInd(group.getMcxGrpInd());
                xdmGroupdto.setOwnerIdList(group.getOwnerIdList());
                if (group.getIsPreConfiguredGroup() != null) {
                    xdmGroupdto.setIsPreConfiguredGroup(group.getIsPreConfiguredGroup());
                }
                if (group.getUgwInterop() != null) {
                    xdmGroupdto.setUgwInterop(group.getUgwInterop());
                }
                if (group.getGroupProfileId() != null) {
                    xdmGroupdto.setGrpProfileId(Integer.parseInt(group.getGroupProfileId()));
                }

                Collection<KnXDMCorpContactDTO> members = new ArrayList<KnXDMCorpContactDTO>();
                if (group.getGroupSupervisor() != null) {
                    for (KnCorpGroupMemberDTO grpMember : group.getGroupSupervisor()) {
                        KnXDMCorpContactDTO contact = new KnXDMCorpContactDTO();
                        contact.setMdn(grpMember.getMdn());
                        contact.setName(grpMember.getName());
                        contact.setSupervisor(grpMember.getSupervisory());
                        contact.setLocWatcher(grpMember.getLocWatcher());
                        contact.setBroadcaster(grpMember.getBroadcaster());
                        contact.setClientType(grpMember.getClientType());
                        contact.setCallInitiatePermission(grpMember.getCallInitiatePermission());
                        contact.setCallReceivePermission(grpMember.getCallReceivePermission());
                        contact.setInCallPermission(grpMember.getInCallPermission());
                        contact.setVideoCallInitiatePermission(grpMember.getVideoCallInitiatePermission());
                        contact.setVideoCallReceivePermission(grpMember.getVideoCallReceivePermission());
                        contact.setVideoInCallPermission(grpMember.getVideoInCallPermission());
                        members.add(contact);
                    }
                }
                xdmGroupdto.setSupervisorList(members);
                corpSharedCorpInfos = group.getCorpSharedCorpInfoList();
                List<KnXDMCorpGrpSharedCorpListDTO> xdmCorpGrpSharedCorpListDTOS = new ArrayList<>();
                if (corpSharedCorpInfos != null && !corpSharedCorpInfos.isEmpty()) {
                    for (KnCorpSharedCorpInfo corpSharedCorpInfo : corpSharedCorpInfos) {
                        KnXDMCorpGrpSharedCorpListDTO xdmCorpGrpSharedCorpListDTO = new KnXDMCorpGrpSharedCorpListDTO();
                        xdmCorpGrpSharedCorpListDTO.setIntCorpID(corpSharedCorpInfo.getCorpId());
                        xdmCorpGrpSharedCorpListDTO.setExtCorpID(corpSharedCorpInfo.getExtCorpId());
                        if (corpSharedCorpInfo.getGrpMemProps() != null) {
                            KnXDMGroupMdnInfoDTO groupMemberProp = new KnXDMGroupMdnInfoDTO();
                            groupMemberProp.setCallInitiatePermission(corpSharedCorpInfo.getGrpMemProps().getCallInitiateAllowed());
                            groupMemberProp.setCallReceivePermission(corpSharedCorpInfo.getGrpMemProps().getCallTerminateAllowed());
                            groupMemberProp.setInCallPermission(corpSharedCorpInfo.getGrpMemProps().getIncallAllowed());
                            groupMemberProp.setVideoCallInitiatePermission(corpSharedCorpInfo.getGrpMemProps().getVideoCallInitiateAllowed() != null ? corpSharedCorpInfo.getGrpMemProps().getVideoCallInitiateAllowed() : 0);
                            groupMemberProp.setVideoCallReceivePermission(corpSharedCorpInfo.getGrpMemProps().getVideoCallReceiveAllowed() != null ? corpSharedCorpInfo.getGrpMemProps().getVideoCallReceiveAllowed() : 0);
                            groupMemberProp.setVideoInCallPermission(corpSharedCorpInfo.getGrpMemProps().getVideoInCallAllowed() != null ? corpSharedCorpInfo.getGrpMemProps().getVideoInCallAllowed() : 0);
                            groupMemberProp.setIsOSMAuthorize(corpSharedCorpInfo.getGrpMemProps().getIsOSMAuthorized());
                            groupMemberProp.setSupervisor(corpSharedCorpInfo.getGrpMemProps().getIsSupervisor());
                            groupMemberProp.setBroadcaster(corpSharedCorpInfo.getGrpMemProps().getIsBroadcaster());
                            groupMemberProp.setLocWatcher(corpSharedCorpInfo.getGrpMemProps().getIsLocSupervisor());
                            xdmCorpGrpSharedCorpListDTO.setGroupMemberProps(groupMemberProp);
                        }
                        xdmCorpGrpSharedCorpListDTOS.add(xdmCorpGrpSharedCorpListDTO);
                    }
                    xdmGroupdto.setGrpShared(group.getGrpShared());
                    xdmGroupdto.setGrpOwnerCorpId(group.getGrpOwnerCorpId());
                    xdmGroupdto.setGrpOwnerExtCorpId(group.getGrpOwnerExtCorpId());
                    xdmGroupdto.setGrpSharedCopList(xdmCorpGrpSharedCorpListDTOS);
                }
                xdmGroupdto.setGrpShared(group.getGrpShared());
                xdmGroupdto.setRecordingFS(group.getRecordingFs());
                xdmGroupdto.setAuthorizedLargeTG(group.getAuthorizedLargeTG());
                xdmGroupList.add(xdmGroupdto);
            }
        }
        xdmRespDto.setGroupList(xdmGroupList);
        xdmRespDto.setCount(respDto.getCount());
        knLogger.debug(methodName, "Returning Get Corp Group List response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpGroupListRespDTO getCorpSubscriberGroupList(IXDMRequestDTO subscriberCorpInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getCorpSubscriberGroupList(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(subscriberCorpInfo instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    subscriberCorpInfo.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDto = (KnXDMCorpSubscInfoRequestDTO) subscriberCorpInfo;
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        List<KnCorpSharedCorpInfo> corpSharedCorpInfos;
        KnCorpGroupListRespDTO respDto = null;
        contactDTO.setNextToken(xdmRequestDto.getNextToken());
        contactDTO.setFetchSize(xdmRequestDto.getFetchSize());

        if (xdmRequestDto.getClientType() == DYNAMIC_CGMT_INTF || xdmRequestDto.getClientType() == AREA_BASED_DYNAMIC_GROUP) {
            contactDTO.setMdn(xdmRequestDto.getSubscriberMdn());
            knLogger.debug(methodName, "Get getTpSubscriberGroupList Group List - ", contactDTO);
            respDto = corpClientIntf.getTpSubscriberGroupList(contactDTO, persisterTxn);
        } else if (xdmRequestDto.getClientType() == PTX_XDMDATA_INTF) {
            contactDTO.setMdn(xdmRequestDto.getSubscriberMdn());
            if (KnConstants.VERSION_2X.equals(xdmRequestDto.getVersion())) {
                contactDTO.setClientType(xdmRequestDto.getClientType());
                knLogger.debug(methodName, "Get getSubscriberGroupList Group List - ", contactDTO);
                respDto = corpClientIntf.getSubscriberGroupList(contactDTO, persisterTxn);
            } else {
                knLogger.debug(methodName, "Get getSubscriberLocWatcherGroupList Group List - ", contactDTO);
                respDto = corpClientIntf.getSubscriberLocWatcherGroupList(contactDTO, persisterTxn);
            }
        } else if (xdmRequestDto.getClientType() == CLIENT_TYPE_WCSR) {
            contactDTO.setMdn(xdmRequestDto.getSubscriberMdn());
            contactDTO.setClientType(xdmRequestDto.getClientType());
            knLogger.debug(methodName, "Get getSubscriberGroupList Group List - ", contactDTO);
            respDto = corpClientIntf.getSubscriberGroupList(contactDTO, persisterTxn);
        } else {
            contactDTO.setCorpId(Integer.parseInt(xdmRequestDto.getCorpId()));
            contactDTO.setMdn(xdmRequestDto.getSubscriberMdn());
            contactDTO.setClientType(xdmRequestDto.getClientType());
            contactDTO.setCustomParamMap(xdmRequestDto.getCustomParamMap());
            contactDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
            knLogger.debug(methodName, "Get CorpSubs Group List - ", contactDTO);
            respDto = corpClientIntf.getSubscriberGroupList(contactDTO, persisterTxn);
        }
        KnXDMCorpGroupListRespDTO xdmRespDto = new KnXDMCorpGroupListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnXDMCorpGroupDTO> xdmGroupList = new ArrayList<KnXDMCorpGroupDTO>();
        Collection<KnCorpGroupInfoDTO> groupList = respDto.getGroupList();
        if (groupList != null) {
            for (KnCorpGroupInfoDTO group : groupList) {
                KnXDMCorpGroupDTO xdmGroupdto = new KnXDMCorpGroupDTO();
                xdmGroupdto.setCorpId(String.valueOf(group.getCorpId()));
                xdmGroupdto.setGroupId(String.valueOf(group.getGroupId()));
                xdmGroupdto.setETag(String.valueOf(group.getETag()));
                xdmGroupdto.setGroupName(group.getGroupDisplayName());
                xdmGroupdto.setMaxGroupMemberLimitFlag(group.getMaxGroupMemLimitFlag());
                xdmGroupdto.setMemberCount(group.getGroupMemberCount());
                xdmGroupdto.setGroupType(group.getGroupType());
                xdmGroupdto.setAvatar(group.getAvatar());
                xdmGroupdto.setVideoPermission(group.getVideoPermission());
                xdmGroupdto.setCamped(group.isCamped());
                xdmGroupdto.setOverrideDND(group.getOverrideDnd());
                xdmGroupdto.setLargeGrp(group.isLargeGroup());
                xdmGroupdto.setMcxGrpInd(group.getMcxGrpInd());
                if (group.getGroupProfileId() != null) {
                    xdmGroupdto.setGrpProfileId(Integer.parseInt(group.getGroupProfileId()));
                }
                Collection<KnCorpGroupMemberDTO> subscList = group.getGroupSupervisor();
                Collection<KnXDMCorpContactDTO> supervisorList = new ArrayList<KnXDMCorpContactDTO>();
                if (subscList != null) {
                    for (KnCorpGroupMemberDTO memberDTO : subscList) {
                        KnXDMCorpContactDTO contactdto = new KnXDMCorpContactDTO();
                        contactdto.setMdn(memberDTO.getMdn());
                        contactdto.setName(memberDTO.getName());
                        contactdto.setBroadcaster(memberDTO.getBroadcaster());
                        contactdto.setSupervisor(memberDTO.getSupervisory());
                        contactdto.setLocWatcher(memberDTO.getLocWatcher());
                        contactdto.setClientType(memberDTO.getClientType());
                        contactdto.setCallInitiatePermission(memberDTO.getCallInitiatePermission());
                        contactdto.setCallReceivePermission(memberDTO.getCallReceivePermission());
                        contactdto.setInCallPermission(memberDTO.getInCallPermission());
                        contactdto.setVideoCallInitiatePermission(memberDTO.getVideoCallInitiatePermission());
                        contactdto.setVideoCallReceivePermission(memberDTO.getVideoCallReceivePermission());
                        contactdto.setVideoInCallPermission(memberDTO.getVideoInCallPermission());
                        supervisorList.add(contactdto);
                    }
                }
                xdmGroupdto.setSupervisorList(supervisorList);
                xdmGroupdto.setGrpShared(group.getGrpShared());
                corpSharedCorpInfos = group.getCorpSharedCorpInfoList();
                List<KnXDMCorpGrpSharedCorpListDTO> xdmCorpGrpSharedCorpListDTOS = new ArrayList<>();
                if (corpSharedCorpInfos != null && !corpSharedCorpInfos.isEmpty()) {
                    for (KnCorpSharedCorpInfo corpSharedCorpInfo : corpSharedCorpInfos) {
                        KnXDMCorpGrpSharedCorpListDTO xdmCorpGrpSharedCorpListDTO = new KnXDMCorpGrpSharedCorpListDTO();
                        xdmCorpGrpSharedCorpListDTO.setIntCorpID(corpSharedCorpInfo.getCorpId());
                        xdmCorpGrpSharedCorpListDTO.setExtCorpID(corpSharedCorpInfo.getExtCorpId());
                        if (corpSharedCorpInfo.getGrpMemProps() != null) {
                            KnXDMGroupMdnInfoDTO groupMemberProp = new KnXDMGroupMdnInfoDTO();
                            groupMemberProp.setCallInitiatePermission(corpSharedCorpInfo.getGrpMemProps().getCallInitiateAllowed());
                            groupMemberProp.setCallReceivePermission(corpSharedCorpInfo.getGrpMemProps().getCallTerminateAllowed());
                            groupMemberProp.setInCallPermission(corpSharedCorpInfo.getGrpMemProps().getIncallAllowed());
                            groupMemberProp.setVideoCallInitiatePermission(corpSharedCorpInfo.getGrpMemProps().getVideoCallInitiateAllowed() != null ? corpSharedCorpInfo.getGrpMemProps().getVideoCallInitiateAllowed() : 0);
                            groupMemberProp.setVideoCallReceivePermission(corpSharedCorpInfo.getGrpMemProps().getVideoCallReceiveAllowed() != null ? corpSharedCorpInfo.getGrpMemProps().getVideoCallReceiveAllowed() : 0);
                            groupMemberProp.setVideoInCallPermission(corpSharedCorpInfo.getGrpMemProps().getVideoInCallAllowed() != null ? corpSharedCorpInfo.getGrpMemProps().getVideoInCallAllowed() : 0);
                            groupMemberProp.setIsOSMAuthorize(corpSharedCorpInfo.getGrpMemProps().getIsOSMAuthorized());
                            groupMemberProp.setSupervisor(corpSharedCorpInfo.getGrpMemProps().getIsSupervisor());
                            groupMemberProp.setBroadcaster(corpSharedCorpInfo.getGrpMemProps().getIsBroadcaster());
                            groupMemberProp.setLocWatcher(corpSharedCorpInfo.getGrpMemProps().getIsLocSupervisor());
                            xdmCorpGrpSharedCorpListDTO.setGroupMemberProps(groupMemberProp);
                        }
                        xdmCorpGrpSharedCorpListDTOS.add(xdmCorpGrpSharedCorpListDTO);
                    }
                    xdmGroupdto.setGrpOwnerCorpId(group.getGrpOwnerCorpId());
                    xdmGroupdto.setGrpOwnerExtCorpId(group.getGrpOwnerExtCorpId());
                    xdmGroupdto.setGrpSharedCopList(xdmCorpGrpSharedCorpListDTOS);
                }

                xdmGroupList.add(xdmGroupdto);
            }
        }
        xdmRespDto.setGroupList(xdmGroupList);
        xdmRespDto.setCount(respDto.getCount());
        knLogger.debug(methodName, "After the call to get corp Group Details. Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpRespDTO forceSync(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {

        String methodName = "forceSync(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMSubsInfoDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMSubsInfoDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMSubsInfoDTO xdmRequestDTO = (KnXDMSubsInfoDTO) contactRequestDTO;
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setMdn(xdmRequestDTO.getMdn());
        contactDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        contactDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        contactDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        knLogger.debug(methodName, "forceSync contact DTO :- ", contactDTO);
        KnCorpResponseDTO respDto = corpClientIntf.forceSync(contactDTO, persisterTxn);
        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        knLogger.debug(methodName, "Returning ForceSync - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO deleteSubscriber(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {

        String methodName = "deleteSubscriber(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) contactRequestDTO;
        knLogger.debug(methodName, " xdmRequestDTO :", xdmRequestDTO);
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        contactDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        contactDTO.setMcpttCompliance(xdmRequestDTO.getMcpttCompliance());
        contactDTO.setUpmCall(xdmRequestDTO.isUpmCall());
        knLogger.debug(methodName, "deleteSubscriber contact DTO :- ", contactDTO);
        KnCorpResponseDTO respDto = corpClientIntf.deleteSubscriber(contactDTO, persisterTxn);
        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        xdmRespDto.setDisabledDispatchMemList(respDto.getDisabledDispatchMemList());
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return respDto;
        }
        // Notification sending logic moved to KnXDMMediator.java
        return respDto;
    }

    public KnCorpResponseDTO changeMdn(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {

        String methodName = "changeMdn(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) contactRequestDTO;
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        contactDTO.setNewMdn(xdmRequestDTO.getNewMdn());
        contactDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        contactDTO.setMcpttCompliance(xdmRequestDTO.getMcpttCompliance());

        knLogger.debug(methodName, "changeMdn contact DTO :- ", contactDTO);
        KnCorpResponseDTO respDto = corpClientIntf.changeMdn(contactDTO, persisterTxn);
        /*KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);*/
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return respDto;
        }

        // Notification sending logic moved to KnXDMMediator.java
        return respDto;
    }

    @Deprecated
    public KnXDMCorpRespDTO createSubscriber(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {

        String methodName = "createSubscriber(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) contactRequestDTO;
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        contactDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "createSubscriber contact DTO :- ", contactDTO);
        KnCorpResponseDTO respDto = corpClientIntf.createSubscriber(contactDTO, persisterTxn);
        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        // Notification sending logic moved to KnXDMMediator.java

        commonMediator.sendTGSModeChangeNotification(respDto.getTgsModeChgMap());

        knLogger.debug(methodName, "Sending LI notifications - ");
        KnLIEventHandler.logLI(respDto.getLiEventList());
        knLogger.debug(methodName, "Li Notification Send status - ");

        return xdmRespDto;
    }

    public KnXDMCorpClientActResponseDTO getSubscriberEmailId(IXDMRequestDTO clientActRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "generateActivationCode(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(clientActRequestDTO instanceof KnXDMCorpClientActRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpDesktopClientActRequestDTO - requestDTO - ",
                    clientActRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpClientActRequestDTO xdmRequestDTO = (KnXDMCorpClientActRequestDTO) clientActRequestDTO;
        KnIPCorpActivationDTO activationDTO = new KnIPCorpActivationDTO();
        activationDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        activationDTO.setMdn(xdmRequestDTO.getMdn());
        activationDTO.setLang(xdmRequestDTO.getLang());
        activationDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        activationDTO.setClientType(clientActRequestDTO.getClientType());
        activationDTO.setHierarchyType(clientActRequestDTO.getHierarchyType());
        activationDTO.setTempPwd(xdmRequestDTO.getTmpPwd());
        knLogger.debug(methodName, "Calling generateActivationCode - ", activationDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.getSubscriberEmailId(activationDTO, persisterTxn);
        knLogger.debug(methodName, "After generateActivationCode - ", respDto);
        KnXDMCorpClientActResponseDTO xdmRespDto = new KnXDMCorpClientActResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        xdmRespDto.setReActivationCount(respDto.getReActivationCount());
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        xdmRespDto.setClientType(respDto.getClientType());
        xdmRespDto.setCustomParamMap(respDto.getCustomParamMap());
        xdmRespDto.setActivationCode(respDto.getActivationCode());
        xdmRespDto.setExpiryTimestamp(respDto.getExpiryTimestamp());
        xdmRespDto.setActivationTimestamp(respDto.getActivationTimestamp());
        xdmRespDto.setSubscrName(respDto.getSubscrName());
        xdmRespDto.setpAMEmailAccId(respDto.getPamAccId());
        KnCorpMailInfoDTO mailInfoDTO = respDto.getMailInfoDTO();
        if (mailInfoDTO != null) {
            KnMailInfoDTO mailDto = new KnMailInfoDTO();
            mailDto.setTo(mailInfoDTO.getTo());
            xdmRespDto.setMailInfoDTO(mailDto);
        }

        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpClientActResponseDTO generateActivationCodes(KnMessage message, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "generateActivationCodes(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        Object payLoad = message.getPayLoad();
        IXDMRequestDTO clientActRequestDTO = null;
        if (payLoad instanceof IXDMRequestDTO) {
            clientActRequestDTO = (IXDMRequestDTO) payLoad;
        }
        if (!(clientActRequestDTO instanceof KnXDMCorpClientActRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpDesktopClientActRequestDTO - requestDTO - ",
                    clientActRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpClientActRequestDTO xdmRequestDTO = (KnXDMCorpClientActRequestDTO) clientActRequestDTO;
        KnIPCorpActivationDTO activationDTO = new KnIPCorpActivationDTO();
        activationDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        activationDTO.setLang(xdmRequestDTO.getLang());
        activationDTO.setMdnList(xdmRequestDTO.getMdnList());
        activationDTO.setClientType(clientActRequestDTO.getClientType());

        activationDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        activationDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Calling generateActivationCodes - ", activationDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.generateActivationCodes(activationDTO, persisterTxn);
        knLogger.debug(methodName, "After generateActivationCodes - ", respDto);
        KnXDMCorpClientActResponseDTO xdmRespDto = new KnXDMCorpClientActResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        xdmRespDto.setReActivationCount(respDto.getReActivationCount());
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        Collection<KnXDMCorpActivationDTO> actCodeList = respDto.getActivationCodeList();
        knLogger.debug(methodName, "actCodeList - ", actCodeList);
        xdmRespDto.setActCodeList(actCodeList);
        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }


    public KnXDMCorpClientActResponseDTO saveClientActivationMail(IXDMRequestDTO clientActRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "saveClientActivationMail(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(clientActRequestDTO instanceof KnXDMCorpClientActRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpDesktopClientActRequestDTO - requestDTO - ",
                    clientActRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpClientActRequestDTO xdmRequestDTO = (KnXDMCorpClientActRequestDTO) clientActRequestDTO;
        KnIPCorpActivationDTO activationDTO = new KnIPCorpActivationDTO();
        activationDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        activationDTO.setMdn(xdmRequestDTO.getMdn());
        activationDTO.setLang(xdmRequestDTO.getLang());
        activationDTO.setActivationCode(xdmRequestDTO.getActivationCode());
        activationDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        activationDTO.setClientType(clientActRequestDTO.getClientType());
        activationDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Calling saveClientActivationMail - ", activationDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.saveClientActivationCode(activationDTO, persisterTxn);
        knLogger.debug(methodName, "After saveClientActivationMail - ", respDto);
        KnXDMCorpClientActResponseDTO xdmRespDto = new KnXDMCorpClientActResponseDTO();
        xdmRespDto.setpAMEmailAccId(respDto.getPamAccId());
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpClientActResponseDTO getMailInfo(IXDMRequestDTO clientActRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMailInfo(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(clientActRequestDTO instanceof KnXDMCorpClientActRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpDesktopClientActRequestDTO - requestDTO - ",
                    clientActRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpClientActRequestDTO xdmRequestDTO = (KnXDMCorpClientActRequestDTO) clientActRequestDTO;
        KnIPCorpActivationDTO activationDTO = new KnIPCorpActivationDTO();
        activationDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        activationDTO.setMdnList(xdmRequestDTO.getMdnList());
        activationDTO.setLang(xdmRequestDTO.getLang());
        activationDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        activationDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Calling getMilInfo - ", activationDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.getMailInfo(activationDTO, persisterTxn);
        knLogger.debug(methodName, "After getMailInfo - ", respDto);
        KnXDMCorpClientActResponseDTO xdmRespDto = new KnXDMCorpClientActResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        xdmRespDto.setClientType(respDto.getClientType());
        xdmRespDto.setCustomParamMap(respDto.getCustomParamMap());
        xdmRespDto.setActivationCode(respDto.getActivationCode());
        xdmRespDto.setExpiryTimestamp(respDto.getExpiryTimestamp());
        xdmRespDto.setSubscrName(respDto.getSubscrName());
        xdmRespDto.setpAMEmailAccId(respDto.getPamAccId());
        KnCorpMailInfoDTO mailInfoDTO = respDto.getMailInfoDTO();
        if (mailInfoDTO != null) {
            KnMailInfoDTO mailDto = new KnMailInfoDTO();
            mailDto.setTo(mailInfoDTO.getTo());
            xdmRespDto.setMailInfoDTO(mailDto);
        }

        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }


    public KnXDMCorpClientActResponseDTO sendActivationMail(IXDMRequestDTO clientActRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "sendActivationMail(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(clientActRequestDTO instanceof KnXDMCorpClientActRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpDesktopClientActRequestDTO - requestDTO - ",
                    clientActRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpClientActRequestDTO xdmRequestDTO = (KnXDMCorpClientActRequestDTO) clientActRequestDTO;
        KnIPCorpActivationDTO activationDTO = new KnIPCorpActivationDTO();
        activationDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        activationDTO.setMdn(xdmRequestDTO.getMdn());
        activationDTO.setLang(xdmRequestDTO.getLang());
        activationDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        activationDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        knLogger.debug(methodName, "Calling sendActivationMail - ", activationDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.sendActivationMail(activationDTO, persisterTxn);
        knLogger.debug(methodName, "After sendActivationMail - ", respDto);
        KnXDMCorpClientActResponseDTO xdmRespDto = new KnXDMCorpClientActResponseDTO();
        xdmRespDto.setpAMEmailAccId(respDto.getPamAccId());
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpRespDTO sendMail(IXDMRequestDTO clientActRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "sendMail(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(clientActRequestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnCorpSubsResquestDTO - requestDTO - ",
                    clientActRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpSubsResquestDTO xdmRequestDTO = (KnCorpSubsResquestDTO) clientActRequestDTO;
        KnIPCorpActivationDTO activationDTO = new KnIPCorpActivationDTO();
        activationDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        activationDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        activationDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        activationDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        knLogger.debug(methodName, "Calling sendMail - ", activationDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.sendMail(activationDTO, persisterTxn);
        knLogger.debug(methodName, "After sendMail - ", respDto);
        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        xdmRespDto.setpamEmailAccId(respDto.getPamAccId());
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }

    /**
     * This method will be invoked by job to get MDN list who are not member of dispatch group
     *
     * @param groupRequestDto
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMCorpDispatchGrpMemberJobRespDTO getSubscDetailsToUpdateIsDispatchMem(IXDMRequestDTO groupRequestDto, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {

        String methodName = "getSubscDetailsToUpdateIsDispatchMem(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        //
        if (!(groupRequestDto instanceof KnCorpDispMemReqDTO)) {
            knLogger.error(methodName, "groupRequestDto not of type KnXDMCorpGroupInfoRequestDTO - requestDTO - ",
                    groupRequestDto.getClass());
            //todo throw back exception - incompatible types
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpDispMemReqDTO xdmRequestDto = (KnCorpDispMemReqDTO) groupRequestDto;
        KnIPCorpDispatchGrpMemInfoDto groupDGMInfoDTO = new KnIPCorpDispatchGrpMemInfoDto();
        Collection<KnXDMCorpContactDTO> memberListToCheck = new ArrayList<KnXDMCorpContactDTO>();
        if (null != xdmRequestDto.getDeletedgroupMemberList()) {
            memberListToCheck.addAll(xdmRequestDto.getDeletedgroupMemberList());
        }
        if (null != xdmRequestDto.getAddedgroupMemberList()) {
            memberListToCheck.addAll(xdmRequestDto.getAddedgroupMemberList());
        }
        groupDGMInfoDTO.setGroupMemberListToChk(memberListToCheck);
        groupDGMInfoDTO.setAddedGroupMembers(xdmRequestDto.getAddedgroupMemberList());
        groupDGMInfoDTO.setDeletedGroupMembers(xdmRequestDto.getDeletedgroupMemberList());
        groupDGMInfoDTO.setCorpId(xdmRequestDto.getCorpId());
        groupDGMInfoDTO.setUpdatedMdn(xdmRequestDto.getUpdatedMdn());
        groupDGMInfoDTO.setHierarchyType(xdmRequestDto.getHierarchyType());

        knLogger.debug(methodName, "getSubscDetailsToUpdateIsDispatchMem  - ", groupDGMInfoDTO);
        KnCorpDispatchGrpMemInfoRespDTO respDto = corpClientIntf.getSubscDetailsToUpdateIsDispatchMem(groupDGMInfoDTO, persisterTxn);
        knLogger.debug(methodName, "getSubscDetailsToUpdateIsDispatchMem response - ", respDto);

        KnXDMCorpDispatchGrpMemberJobRespDTO xdmRespDto = new KnXDMCorpDispatchGrpMemberJobRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }


        xdmRespDto.setCorpId(respDto.getCorpId());
        xdmRespDto.setSubscUpdateList(respDto.getSubscIsDispMemDetailsList());
        // Notification sending logic moved to KnXDMMediator.java
        knLogger.debug(methodName, "Returning from Get non dispatch group members - ", xdmRespDto);
        return xdmRespDto;
    }

    /**
     * This method returns the unused MDN list in a PAM account ID.
     *
     * @param xdmRequestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnCorpPAMSubsRespDTO getUnusedSubsList(KnCorpPAMSubsReqDTO xdmRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "getUnusedSubsList(IXDMRequestDTO, KnPersisterTxn)";
        KnCorpPAMSubsRespDTO xdmRespDto = new KnCorpPAMSubsRespDTO();
        try {
            KnIPCorpPAMSubsDTO corpIPPamSubsDTO = new KnIPCorpPAMSubsDTO();
            corpIPPamSubsDTO.setPamAccId(xdmRequestDTO.getPamAccId());
            corpIPPamSubsDTO.setClientType(xdmRequestDTO.getClientType());
            corpIPPamSubsDTO.setUnUsedMdnCount(xdmRequestDTO.getUnUsedMdnCount());
            corpIPPamSubsDTO.setXdmsHome(xdmRequestDTO.getXdmsHome());
            corpIPPamSubsDTO.setExtCorpId(xdmRequestDTO.getExtCorpId());
            knLogger.debug(methodName, "corpIPPamSubsDTO contact DTO :- ", corpIPPamSubsDTO);
            KnCorpPAMSubsDTO respDto = corpClientIntf.getUnusedSubsList(corpIPPamSubsDTO, persisterTxn);
            xdmRespDto.setFreePAMSubsList(respDto.getFreePAMSubsList());
            populateXdmResponse(xdmRespDto, respDto);
            if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
                knLogger.error(methodName, "Returning Failure response");
                return xdmRespDto;
            }
        } catch (Exception e) {
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, "Internal Server Error");
        }
        knLogger.debug(methodName, "Returning  - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpRespDTO cleanCorpData(KnCorpPAMSubsReqDTO xdmRequestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "cleanCorpData(IXDMRequestDTO, KnPersisterTxn)";
        KnCorpPAMSubsRespDTO xdmRespDto = new KnCorpPAMSubsRespDTO();
        try {
            KnIPCorpPAMSubsDTO corpIPPamSubsDTO = new KnIPCorpPAMSubsDTO();
            corpIPPamSubsDTO.setPamAccId(xdmRequestDTO.getPamAccId());
            corpIPPamSubsDTO.setClientType(xdmRequestDTO.getClientType());
            corpIPPamSubsDTO.setUnUsedMdnCount(xdmRequestDTO.getUnUsedMdnCount());
            corpIPPamSubsDTO.setCleanUpMdnLst(xdmRequestDTO.getCleanUpMdnLst());
            corpIPPamSubsDTO.setXdmsHome(xdmRequestDTO.getXdmsHome());
            corpIPPamSubsDTO.setExtCorpId(xdmRequestDTO.getExtCorpId());
            knLogger.debug(methodName, "corpIPPamSubsDTO contact DTO :- ", corpIPPamSubsDTO);
            KnCorpResponseDTO respDto = corpClientIntf.cleanCorpData(corpIPPamSubsDTO, persisterTxn);
            populateXdmResponse(xdmRespDto, respDto);
            if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
                knLogger.error(methodName, "Returning Failure response");
                return xdmRespDto;
            }

            // Notification sending logic moved to KnXDMMediator.java

            commonMediator.sendTGSModeChangeNotification(respDto.getTgsModeChgMap());

        } catch (Exception e) {
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, "Internal Server Error");
        }
        knLogger.debug(methodName, "Returning  - ", xdmRespDto);
        return xdmRespDto;
    }

    //Populating XDM response
    private void populateXdmResponse(KnXDMCorpRespDTO xdmRespDto, KnCorpResponseDTO respDto) {
        String methodName = "populateXdmResponse";
        knLogger.debug(methodName);
        int status = respDto.getStatus();
        xdmRespDto.setResponseStatus(status);
        xdmRespDto.setResponseCode(respDto.getStatusCode());
        xdmRespDto.setResponseMessage(respDto.getMessage());
        Collection<KnCorpFailedData> failedDataList = respDto.getFailedDataList();
        Collection<KnXDMFailureRespDTO> failureDetails = respDto.getFailureDetails();
        String etag = respDto.getEtag();
        if (status == KnConstants.RESPONSE_STATUS.SUCCESS.value()) {
            if (failedDataList != null && !failedDataList.isEmpty()) {
                xdmRespDto.setFailureDetails(populateFailureDetails(failedDataList));
            }
            if (etag != null) {
                xdmRespDto.setEtag(etag);
            }
        } else {
            if (failedDataList != null && failedDataList.isEmpty()) {
                xdmRespDto.setFailureDetails(populateFailureDetails(failedDataList));
            }
            // consolidation validation failure changes
            if (failureDetails != null && !failureDetails.isEmpty()) {
                Collection<KnXDMFailureRespDTO> finalFailureDetails = xdmRespDto.getFailureDetails();
                if (finalFailureDetails == null) {
                    xdmRespDto.setFailureDetails(failureDetails);
                } else {
                    finalFailureDetails.addAll(failureDetails);
                }
            }
        }
    }

    private Collection<KnXDMFailureRespDTO> populateFailureDetails(Collection<KnCorpFailedData> failedDataList) {

        Collection<KnXDMFailureRespDTO> failedList = null;

        if (failedDataList == null || failedDataList.isEmpty()) {
            return failedList;
        }
        failedList = new ArrayList<KnXDMFailureRespDTO>(failedDataList.size());
        for (KnCorpFailedData failedData : failedDataList) {
            KnXDMFailureRespDTO failureDTO = new KnXDMFailureRespDTO(failedData.getAttribute(),
                    getString(failedData.getValues()), failedData.getCode(), failedData.getMsg());
            failedList.add(failureDTO);
        }
        return failedList;
    }

    private String getString(Collection<String> collectionStr) {
        StringBuffer sb = new StringBuffer(100);
        for (String str : collectionStr) {
            sb.append(str).append(",");
        }
        int indx = sb.lastIndexOf(",");
        if (indx > 0) {
            sb.deleteCharAt(indx);
        }
        return sb.toString();
    }

    public KnCorpResponseDTO modifySubscCorpFeature(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "modifySubscCorpFeature(IXDMRequestDTO, KnPersisterTxn)";
        KnCorpResponseDTO xdmCorpRespDTO = new KnCorpResponseDTO();
        if (!(requestDTO instanceof KnXDMSubsProvInfoDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMSubsProvInfoDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMSubsProvInfoDTO subsProvInfoDTO = (KnXDMSubsProvInfoDTO) requestDTO;
        KnIPSubsProvInfoDTO ipSubsProvInfoDTO = new KnIPSubsProvInfoDTO();
        ipSubsProvInfoDTO.setMdn(subsProvInfoDTO.getMdn());
        ipSubsProvInfoDTO.setCorpId(subsProvInfoDTO.getCorpId());
        String subsFs = subsProvInfoDTO.getSubsFS2() != null ? subsProvInfoDTO.getSubsFS2() : KnGeneralUtil.convertLongToHexString(subsProvInfoDTO.getSubsFeatureSet());
        ipSubsProvInfoDTO.setSubFS2(subsFs);
        ipSubsProvInfoDTO.setCustomParamMap(subsProvInfoDTO.getCustomParamMap());
        ipSubsProvInfoDTO.setHierarchyType(subsProvInfoDTO.getHierarchyType());
        knLogger.debug(methodName, "Calling corp library for modify Subsc Corp Feature");
        KnCorpResponseDTO respDto = corpClientIntf.modifySubsCorpFeature(ipSubsProvInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Returned from corporate library");
        //populateXdmResponse(xdmCorpRespDTO, respDto);
        return respDto;
    }

    public KnXDMTalkGroupRespDTO getSubscriberScanList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getSubscriberScanList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMTalkGroupRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMTalkGrpReqestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMTalkGroupRespDTO respDto = new KnXDMTalkGroupRespDTO();
        KnXDMTalkGroupRequestDTO talkGrpDTO = (KnXDMTalkGroupRequestDTO) requestDTO;
        KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
        if (talkGrpDTO.getClientType() != CLIENT_TYPE_WCSR) {
            ipTalkGroupDTO.setCustomParamMap(talkGrpDTO.getCustomParamMap());
        }
        ipTalkGroupDTO.setCorpId(talkGrpDTO.getCorpId());
        ipTalkGroupDTO.setMdn(talkGrpDTO.getMdn());
        ipTalkGroupDTO.setEtag(talkGrpDTO.getETag());
        ipTalkGroupDTO.setHierarchyType(talkGrpDTO.getHierarchyType());
        knLogger.debug(methodName, "Calling corp library for getSubscriberScanList");
        KnXDMTalkGroupServerRespDTO xdmRespDto = corpClientIntf.getSubscriberScanList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug(methodName, "Returned from corporate library");
        populateXdmResponse(respDto, xdmRespDto);
        if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == xdmRespDto.getStatus()) {
            for (KnXDMTalkGroupInfoDTO groupServerRespDTO : xdmRespDto.getCampGrpList()) {
                if (groupServerRespDTO.getPriority() != null && groupServerRespDTO.getPriority() == KnConstants.NO_PRIORITY) {
                    groupServerRespDTO.setPriority(null);
                }
            }
            respDto.setCampGrpList(xdmRespDto.getCampGrpList());
            respDto.setMode(xdmRespDto.getMode());
            respDto.setScanningEnabled(xdmRespDto.getScanningEnabled());
            respDto.setEtag(xdmRespDto.getEtag());
            knLogger.debug(methodName, "Returning Success response after assign/deassign talk group");
        }
        return respDto;
    }

    public KnCorpResponseDTO modifyBulkSubscriberScanList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "modifyBulkSubscriberScanList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMTalkGroupRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMTalkGroupRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMTalkGroupRequestDTO talkGrpDTO = (KnXDMTalkGroupRequestDTO) requestDTO;
        knLogger.debug(methodName, "talkGrpDTO :", talkGrpDTO);
        KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
        ipTalkGroupDTO.setCustomParamMap(talkGrpDTO.getCustomParamMap());
        ipTalkGroupDTO.setCorpId(talkGrpDTO.getCorpId());
        ipTalkGroupDTO.setMdn(talkGrpDTO.getMdn());
        ipTalkGroupDTO.setMcPttId(talkGrpDTO.getMcPttId());
        ipTalkGroupDTO.setAddedCampGrpList(talkGrpDTO.getAddedCampGrpList());
        ipTalkGroupDTO.setModifiedCampGrpList(talkGrpDTO.getModifiedCampGrpList());
        ipTalkGroupDTO.setRemovedCammpGrpList(talkGrpDTO.getRemovedCampGrpList());
        ipTalkGroupDTO.setMode(talkGrpDTO.getMode());
        ipTalkGroupDTO.setEtag(talkGrpDTO.getETag());
        ipTalkGroupDTO.setClientType(talkGrpDTO.getClientType());
        ipTalkGroupDTO.setHierarchyType(talkGrpDTO.getHierarchyType());
        boolean isUpmCall = talkGrpDTO.isUpmCall();
        ipTalkGroupDTO.setUpmCall(isUpmCall);
        KnCorpResponseDTO respDto = null;
        knLogger.debug(methodName, " isUpmCall:", isUpmCall);
        knLogger.debug(methodName, "Calling corp library for modify Subscriber Scan List");
        if (talkGrpDTO.getClientType() == com.kodiak.common.resources.KnConstants.CLIENT_TYPE_XCAP) {
            respDto = corpClientIntf.modifySubscriberScanListXcap(ipTalkGroupDTO, persisterTxn);
        } else {
            ipTalkGroupDTO.setOperationType(KnOperationTypes.UPM_MODIFY_SUBSCRIBER_SCAN_LIST);
            respDto = corpClientIntf.upmModifyBulkSubscriberScanList(ipTalkGroupDTO, persisterTxn);
        }
        // Notification sending logic moved to KnXDMMediator.java

        knLogger.debug(methodName, "Returned from corporate library");
        return respDto;
    }

    public KnCorpResponseDTO modifySubscriberScanList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "modifySubscriberScanList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMTalkGroupRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMTalkGroupRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMTalkGroupRequestDTO talkGrpDTO = (KnXDMTalkGroupRequestDTO) requestDTO;
        knLogger.debug(methodName, "talkGrpDTO :", talkGrpDTO);
        KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
        ipTalkGroupDTO.setCustomParamMap(talkGrpDTO.getCustomParamMap());
        ipTalkGroupDTO.setCorpId(talkGrpDTO.getCorpId());
        ipTalkGroupDTO.setMdn(talkGrpDTO.getMdn());
        ipTalkGroupDTO.setMcPttId(talkGrpDTO.getMcPttId());
        ipTalkGroupDTO.setAddedCampGrpList(talkGrpDTO.getAddedCampGrpList());
        ipTalkGroupDTO.setModifiedCampGrpList(talkGrpDTO.getModifiedCampGrpList());
        ipTalkGroupDTO.setRemovedCammpGrpList(talkGrpDTO.getRemovedCampGrpList());
        ipTalkGroupDTO.setMode(talkGrpDTO.getMode());
        ipTalkGroupDTO.setEtag(talkGrpDTO.getETag());
        ipTalkGroupDTO.setClientType(talkGrpDTO.getClientType());
        ipTalkGroupDTO.setHierarchyType(talkGrpDTO.getHierarchyType());
        boolean isUpmCall = talkGrpDTO.isUpmCall();
        ipTalkGroupDTO.setUpmCall(isUpmCall);
        KnCorpResponseDTO respDto = null;
        knLogger.debug(methodName, " isUpmCall:", isUpmCall);
        knLogger.debug(methodName, "Calling corp library for modify Subscriber Scan List");
        if (talkGrpDTO.getClientType() == com.kodiak.common.resources.KnConstants.CLIENT_TYPE_XCAP) {
            respDto = corpClientIntf.modifySubscriberScanListXcap(ipTalkGroupDTO, persisterTxn);
        } else {
            if (!isUpmCall) {
                respDto = corpClientIntf.modifySubscriberScanList(ipTalkGroupDTO, persisterTxn);
            } else {
                respDto = corpClientIntf.upmModifySubscriberScanList(ipTalkGroupDTO, persisterTxn);
            }

        }
        // Notification sending logic moved to KnXDMMediator.java

        knLogger.debug(methodName, "Returned from corporate library");
        return respDto;
    }

    public KnCorpTalkGroupRespDTO cleanUpSubsCampedGrps(String mdn, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "cleanUpSubsCampedGrps(mdn, persisterTxn)";
        knLogger.debug(methodName, "Calling corp library cleanUpSubsCampedGrps");
        KnIPSubsDTO iPSubsDTO = new KnIPSubsDTO();
        iPSubsDTO.setMdn(mdn);
        KnCorpTalkGroupRespDTO respDto = corpClientIntf.cleanUpSubsCampedGrps(iPSubsDTO, persisterTxn);
        knLogger.debug(methodName, "Returning Success response after cleanUpSubsCampedGrps");
        return respDto;
    }

    public KnCorpResponseDTO updateGrpBroadcasters(String mdn, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        KnCorpResponseDTO respDto;
        try {
            String methodName = "updateGrpBroadcasters(IXDMRequestDTO, KnPersisterTxn)";
            knLogger.debug(methodName, "updateGrpBroadcasters Req received for - ", KnGDPRTemplate.mdn(mdn));
            respDto = corpClientIntf.updateGrpBroadcasters(mdn, persisterTxn);
            knLogger.debug(methodName, "updateGrpBroadcasters response - ", respDto);
        } catch (Exception e) {
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, "Internal Server Error");
        }
        return respDto;
    }

    public KnXDMCorpLicensePackListRespDTO getAllBillingMdns(IXDMRequestDTO licenseRequestDTO,
                                                             KnPersisterTxn persisterTxn) throws KnXDMServerException {

        final String methodName = "getAllBillingMdns(IXDMRequestDTO, KnPersisterTxn)";
        knLogger.debug
                (methodName, licenseRequestDTO, persisterTxn);
        //Step:
        if (!(licenseRequestDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    licenseRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDTO = (KnXDMCorpInfoRequestDTO) licenseRequestDTO;
        KnIPCorpInfoDTO corpInfoDto = new KnIPCorpInfoDTO();
        corpInfoDto.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        corpInfoDto.setETag(xdmRequestDTO.getETag());
        corpInfoDto.setHierarchyType(xdmRequestDTO.getHierarchyType());
        corpInfoDto.setFilterType(LICENSE_FILTER_TYPE);
        corpInfoDto.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        knLogger.debug(methodName, "Retrieving getAllBillingMdns - ", corpInfoDto);
        KnCorpLicensePackListRespDTO respDto = corpClientIntf.getAllBillingMdns(corpInfoDto, persisterTxn);
        KnXDMCorpLicensePackListRespDTO xdmRespDto = new KnXDMCorpLicensePackListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == respDto.getStatus()) {
            xdmRespDto.setEtag(respDto.getEtag());
            Collection<KnLicensePackDTO> licensePackList = respDto.getLicensePackList();
            //contactList population
            Collection<KnXDMLicensePackDTO> xdmLicensePackDTOs = new ArrayList<KnXDMLicensePackDTO>();
            for (KnLicensePackDTO licensePackDTO : licensePackList) {
                KnXDMLicensePackDTO xdmLicensePack = new KnXDMLicensePackDTO();
                xdmLicensePack.setBillingNumber(licensePackDTO.getBillingNumber());
                xdmLicensePack.setBillingName(licensePackDTO.getBillingName());
                xdmLicensePack.setCorpId(licensePackDTO.getCorpId());
                xdmLicensePack.setCorpName(licensePackDTO.getCorpName());
                xdmLicensePack.setSubsClientType(licensePackDTO.getSubsClientType());
                xdmLicensePack.setSubscriptionType(licensePackDTO.getSubscriptionType());
                xdmLicensePack.setTotalNoOfLines(licensePackDTO.getTotalNoOfLines());
                xdmLicensePack.seteTag(licensePackDTO.geteTag());
                xdmLicensePack.setCustomParamMap(licensePackDTO.getCustomParamMap());
                xdmLicensePack.setPamAccState(String.valueOf(licensePackDTO.getAccountStatus()));
                xdmLicensePack.setSubscriberFs2(licensePackDTO.getSubscriberFs2());
                xdmLicensePack.setSubscriberFs(KnGeneralUtil.convertHexStringToLong(licensePackDTO.getSubscriberFs2()));

                xdmLicensePackDTOs.add(xdmLicensePack);
            }
            xdmRespDto.setLicensePackProfileList(xdmLicensePackDTOs);
        }
        knLogger.debug(methodName, "Returning AllBillingMdns - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpLicenseSubListRespDTO getLicenseSubs(IXDMRequestDTO licenseSubRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        final String methodName = "getLicenseSubs(IXDMRequestDTO, KnPersisterTxn)";
        knLogger.debug(methodName, licenseSubRequestDTO, persisterTxn);
        //Step:
        if (!(licenseSubRequestDTO instanceof KnXDMLicenseSubsListReqDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMLicenseSubsListReqDTO - requestDTO - ",
                    licenseSubRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMLicenseSubsListReqDTO xdmRequestDTO = (KnXDMLicenseSubsListReqDTO) licenseSubRequestDTO;
        KnIPLicenseSubsListDTO ipLicenseSubsListDTO = new KnIPLicenseSubsListDTO();
        ipLicenseSubsListDTO.setCorpId(xdmRequestDTO.getCorpId());
        ipLicenseSubsListDTO.setETag(String.valueOf(xdmRequestDTO.getEtag()));
        ipLicenseSubsListDTO.setBillingNumber(xdmRequestDTO.getBillingNumber());
        ipLicenseSubsListDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        ipLicenseSubsListDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());


        knLogger.debug(methodName, "Retrieving License Subscribers - ", xdmRequestDTO.getCorpId(), xdmRequestDTO.getBillingNumber());
        KnCorpLicenseSubsListRespDTO respDto = null;
        if (CLIENT_TYPE_PAM_SUBS_WCSR == xdmRequestDTO.getClientType()) {
            respDto = corpClientIntf.getLicenseSubscribersForCSR(ipLicenseSubsListDTO, persisterTxn);
        } else {
            respDto = corpClientIntf.getLicenseSubscribers(ipLicenseSubsListDTO, persisterTxn);
        }

        KnXDMCorpLicenseSubListRespDTO xdmRespDto = new KnXDMCorpLicenseSubListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == respDto.getStatus()) {
            xdmRespDto.setEtag(respDto.getEtag());
            Collection<KnLicenseSubDTO> licenseSubList = respDto.getLicenseSubsList();
            //contactList population
            Collection<KnXDMLicenseSubsDTO> xdmLicenseSubDTOs = new ArrayList<KnXDMLicenseSubsDTO>();
            for (KnLicenseSubDTO licensePackDTO : licenseSubList) {
                KnXDMLicenseSubsDTO xdmLicenseSub = new KnXDMLicenseSubsDTO();
                xdmLicenseSub.setMdn(licensePackDTO.getMdn());
                xdmLicenseSub.setName(licensePackDTO.getName());
                xdmLicenseSub.setServiceAuthStatus(licensePackDTO.getServiceAuthStatus());
                xdmLicenseSub.setMarkStatus(licensePackDTO.getMarkStatus());
                xdmLicenseSub.setTpUser(licensePackDTO.getTpUser());
                xdmLicenseSub.setTpAccount(licensePackDTO.getTpAccount());
                xdmLicenseSub.setPublicSubsType(licensePackDTO.getPublicSubsType());
                xdmLicenseSub.setCorpSubsType(licensePackDTO.getCorpSubsType());
                knLogger.debug(methodName, "Retrieved License Subscribers - ", xdmLicenseSub);
                xdmLicenseSubDTOs.add(xdmLicenseSub);
            }
            xdmRespDto.setLicenseSubsProfileList(xdmLicenseSubDTOs);
            knLogger.debug(methodName, "Returning License Subscribers - ", xdmRespDto);

        }
        return xdmRespDto;
    }

    public KnXDMCorpRespDTO updateBillingName(IXDMRequestDTO licenseSubRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        final String methodName = "updateBillingName(IXDMRequestDTO, KnPersisterTxn)";
        knLogger.debug(methodName, licenseSubRequestDTO, persisterTxn);
        //Step:
        if (!(licenseSubRequestDTO instanceof KnXDMLicenseSubsListReqDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMLicenseSubsListReqDTO - requestDTO - ", licenseSubRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMLicenseSubsListReqDTO xdmRequestDTO = (KnXDMLicenseSubsListReqDTO) licenseSubRequestDTO;
        KnIPLicenseSubsListDTO ipLicenseSubsListDTO = new KnIPLicenseSubsListDTO();
        ipLicenseSubsListDTO.setCorpId(xdmRequestDTO.getCorpId());
        ipLicenseSubsListDTO.setETag(String.valueOf(xdmRequestDTO.getEtag()));
        ipLicenseSubsListDTO.setBillingNumber(xdmRequestDTO.getBillingNumber());
        ipLicenseSubsListDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        ipLicenseSubsListDTO.setBillingName(xdmRequestDTO.getBillingName());
        ipLicenseSubsListDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "updating Billing Name - ", xdmRequestDTO.getCorpId(), xdmRequestDTO.getBillingNumber(), xdmRequestDTO.getBillingName());
        KnCorpResponseDTO respDto = corpClientIntf.updateBillingName(ipLicenseSubsListDTO, persisterTxn);

        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        knLogger.debug(methodName, "After the call to the updateBillingName Response - ", xdmRespDto);

        return xdmRespDto;
    }


    public KnXDMCorpRespDTO markSubsForDeletion(IXDMRequestDTO licenseSubRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "markSubsForDeletion(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(licenseSubRequestDTO instanceof KnXDMLicenseSubsListReqDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMLicenseSubsListReqDTO - requestDTO - ",
                    licenseSubRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMLicenseSubsListReqDTO xdmRequestDTO = (KnXDMLicenseSubsListReqDTO) licenseSubRequestDTO;
        KnIPLicenseSubsListDTO ipLicenseSubsListDTO = new KnIPLicenseSubsListDTO();
        ipLicenseSubsListDTO.setCorpId(xdmRequestDTO.getCorpId());
        ipLicenseSubsListDTO.setETag(String.valueOf(xdmRequestDTO.getEtag()));
        ipLicenseSubsListDTO.setBillingNumber(xdmRequestDTO.getBillingNumber());
        ipLicenseSubsListDTO.setMarkList(xdmRequestDTO.getMarkList());
        ipLicenseSubsListDTO.setUnmarkList(xdmRequestDTO.getUnmarkList());
        ipLicenseSubsListDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        ipLicenseSubsListDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "markSubsForDeletion Details.");
        KnCorpResponseDTO respDto = corpClientIntf.markForDelete(ipLicenseSubsListDTO, persisterTxn);
        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        knLogger.debug(methodName, "After the call to the markSubsForDeletion Response - ", xdmRespDto);
        return xdmRespDto;
    }

    /**
     * Method to return the subscriber list where the request MDN exist as contact.
     *
     * @param contactRequestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnReverseContactResponseDto getSubscrReverseContacts(IXDMRequestDTO contactRequestDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {
        String methodName = "getSubscrReverseContacts()";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) contactRequestDTO;
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        contactDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        contactDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        contactDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "Retrieving Subscriber Reverse Contacts - ", contactDTO);
        KnReverseContactsRespDto respDto = corpClientIntf.getSubscrReverseContacts(contactDTO, persisterTxn);
        KnReverseContactResponseDto xdmRespDto = new KnReverseContactResponseDto();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        List<KnMDNInfoDto> mdnInfoDtoList = respDto.getMdnInfoDtoList();
        knLogger.debug(methodName, "mdnInfoDtoList - ", mdnInfoDtoList);
        List<KnXDMMdnInfoDTO> mdnInfoDTOs = new ArrayList<>();
        if (mdnInfoDtoList != null) {
            for (KnMDNInfoDto contact : mdnInfoDtoList) {
                KnXDMMdnInfoDTO mdnInfoDTO = new KnXDMMdnInfoDTO();
                mdnInfoDTO.setMdn(contact.getMdn());
                mdnInfoDTO.setName(contact.getName());
                mdnInfoDTOs.add(mdnInfoDTO);
            }
        }
        xdmRespDto.setReverseContacts(mdnInfoDTOs);
        knLogger.debug(methodName, "Reverse Contacts", xdmRespDto);
        return xdmRespDto;
    }

    /**
     * This method is to retrieve the list of sublists where the request MDN exist as member.
     *
     * @param subscriberCorpInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMCorpSublistListRespDTO getSubscrSublists(IXDMRequestDTO subscriberCorpInfo, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {
        String methodName = "getSubscrSublists()";
        //Step:
        if (!(subscriberCorpInfo instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - ",
                    subscriberCorpInfo.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) subscriberCorpInfo;
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        contactDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        contactDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        contactDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        knLogger.debug(methodName, "Retrieving Subscriber Sublists - ", contactDTO);
        KnCorpSublistListRespDTO respDto = corpClientIntf.getSubscrSublists(contactDTO, persisterTxn);
        knLogger.debug(methodName, "Library response - ", respDto);
        KnXDMCorpSublistListRespDTO xdmRespDto = new KnXDMCorpSublistListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnXDMCorpSublistDTO> xdmSublistList = new ArrayList<KnXDMCorpSublistDTO>();
        Collection<KnCorpSublistDTO> sublistList = respDto.getSublistList();
        if (sublistList != null) {
            for (KnCorpSublistDTO sublist : sublistList) {
                KnXDMCorpSublistDTO sublistDTO = new KnXDMCorpSublistDTO();
                sublistDTO.setCorpId(String.valueOf(sublist.getCorpId()));
                sublistDTO.setSublistId(String.valueOf(sublist.getSublistId()));
                sublistDTO.setSublistName(sublist.getSublistName());
                sublistDTO.setSublistType(String.valueOf(sublist.getSublistType()));
                xdmSublistList.add(sublistDTO);
            }
        }
        xdmRespDto.setSublistLists(xdmSublistList);
        knLogger.debug(methodName, "Returning sublists - ", xdmRespDto);
        return xdmRespDto;
    }

    /**
     * This method is used to get the poc corporates Group MDNs group List
     *
     * @param pocCorpInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMLinkedGroupListRespDTO getPocLinkedGroupList(IXDMRequestDTO pocCorpInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getPocLinkedGroupList()";
        KnXDMLinkedGroupListRespDTO xdmRespDto = new KnXDMLinkedGroupListRespDTO();

        //Step:
        if (!(pocCorpInfo instanceof KnXDMLinkedGroupInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - ", pocCorpInfo.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMLinkedGroupInfoRequestDTO xdmRequestDTO = (KnXDMLinkedGroupInfoRequestDTO) pocCorpInfo;
        KnIPLinkedGroupInfoDTO pocGrpListDTO = new KnIPLinkedGroupInfoDTO();
        pocGrpListDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        pocGrpListDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());

        if (xdmRequestDTO.getETag() != null && !xdmRequestDTO.getETag().isEmpty()) {
            pocGrpListDTO.setGwETag(Long.valueOf(xdmRequestDTO.getETag()));
        }
        pocGrpListDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "Retrieving Poc corporates group mdns Group List- ", pocGrpListDTO);

        KnLinkedGroupInfoRespDTO respDto = corpClientIntf.getPocLinkedGroupList(pocGrpListDTO, persisterTxn);
        knLogger.debug(methodName, "Library response - ", respDto);
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
        } else {
            List<KnLinkedGroupInfo> groupList = respDto.getLinkedGroupList();
            List<KnXDMCorpGroupDTO> subsGroupList = new ArrayList<KnXDMCorpGroupDTO>();
            if (groupList != null) {
                for (KnLinkedGroupInfo groupInfo : groupList) {
                    KnXDMCorpGroupDTO subsCorpDTO = new KnXDMCorpGroupDTO();
                    subsCorpDTO.setOwnerMdn(groupInfo.getGroupMdn());
                    subsCorpDTO.setGroupName(groupInfo.getGroupName());
                    subsCorpDTO.setGroupId(String.valueOf(groupInfo.getGroupId()));
                    subsCorpDTO.setGroupType(groupInfo.getGroupType());
                    subsGroupList.add(subsCorpDTO);
                }
            }
            xdmRespDto.setGroupList(subsGroupList);
        }
        knLogger.debug(methodName, "Returning linked groupList - ", xdmRespDto);
        return xdmRespDto;

    }

    /**
     * This method is used to remove all the contacts for the subscribers.     *
     *
     * @param corpSubsInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnCorpResponseDTO removeSubscribersContacts(IXDMRequestDTO corpSubsInfo, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "removeSubscribersContacts(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(corpSubsInfo instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - xdmRequestDTO - ",
                    corpSubsInfo.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) corpSubsInfo;

        KnIPCorpContactDTO subscriberInfoDTO = new KnIPCorpContactDTO();
        subscriberInfoDTO.setClientType(xdmRequestDTO.getClientType());
        subscriberInfoDTO.setMdn(xdmRequestDTO.getSubscriberMdn());

        if (xdmRequestDTO.getCorpId() != null) {
            subscriberInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        String etag = xdmRequestDTO.getETag();
        if (etag != null && !etag.isEmpty()) {
            long etagLong = Long.valueOf(etag);
            if (etagLong > intRange) {
                subscriberInfoDTO.setEtag(-1);
            } else {
                subscriberInfoDTO.setEtag(Integer.valueOf(etag));
            }
        }
        subscriberInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        subscriberInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "Remove Subscribers Contacts request - ", subscriberInfoDTO);
        KnCorpResponseDTO respDto = corpClientIntf.removeSubscribersContacts(subscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Remove Subscribers Contacts response - ", respDto);
        return respDto;
    }

    /**
     * This method is used to remove the subscriber from all the shared sublist where he is a member.
     *
     * @param corpSubsInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnCorpResponseDTO removeSubscribersAllSublist(IXDMRequestDTO corpSubsInfo, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "removeSubscribersAllSublist(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(corpSubsInfo instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - xdmRequestDTO - ",
                    corpSubsInfo.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) corpSubsInfo;

        KnIPCorpContactDTO subscriberInfoDTO = new KnIPCorpContactDTO();
        subscriberInfoDTO.setClientType(xdmRequestDTO.getClientType());
        subscriberInfoDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        if (xdmRequestDTO.getCorpId() != null) {
            subscriberInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        String etag = xdmRequestDTO.getETag();
        if (etag != null && !etag.isEmpty()) {
            long etagLong = Long.valueOf(etag);
            if (etagLong > intRange) {
                subscriberInfoDTO.setEtag(-1);
            } else {
                subscriberInfoDTO.setEtag(Integer.valueOf(etag));
            }
        }
        subscriberInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        subscriberInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "Remove Subscribers Sublists request - ", subscriberInfoDTO);
        KnCorpResponseDTO respDto = corpClientIntf.removeSubscribersAllSublist(subscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Remove Subscribers Sublists response - ", respDto);
        return respDto;
    }

    /**
     * This method is used to remove him from all the groups private list.
     *
     * @param corpSubsInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnCorpResponseDTO removeSubscribersAllGroups(IXDMRequestDTO corpSubsInfo, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "removeSubscribersAllGroups(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(corpSubsInfo instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - xdmRequestDTO - ",
                    corpSubsInfo.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) corpSubsInfo;

        KnIPCorpContactDTO subscriberInfoDTO = new KnIPCorpContactDTO();
        subscriberInfoDTO.setClientType(xdmRequestDTO.getClientType());
        subscriberInfoDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        if (xdmRequestDTO.getCorpId() != null) {
            subscriberInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        String etag = xdmRequestDTO.getETag();
        if (etag != null && !etag.isEmpty()) {
            long etagLong = Long.valueOf(etag);
            if (etagLong > intRange) {
                subscriberInfoDTO.setEtag(-1);
            } else {
                subscriberInfoDTO.setEtag(Integer.valueOf(etag));
            }
        }
        subscriberInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        subscriberInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "Remove Subscribers All Groups request - ", subscriberInfoDTO);
        KnCorpResponseDTO respDto = corpClientIntf.removeSubscribersAllGroups(subscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Remove Subscribers All Groups Response - ", respDto);
        return respDto;
    }

    /**
     * This  method is called by the OP CLI and the audit to eanble the corporate auto pairing feature.
     *
     * @param corpInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMCorpRespDTO updateCorpAutoPairing(IXDMRequestDTO corpInfoDTO, KnPersisterTxn persisterTxn) throws KnException {
        String methodName = "updateCorpAutoPairing(int,KnPersisterTxn)";
        knLogger.debug(methodName, "Auto pairing corporate - ", corpInfoDTO);
        if (!(corpInfoDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    corpInfoDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDto = (KnXDMCorpInfoRequestDTO) corpInfoDTO;
        String auditStr = "Request recieved for Corporate-".concat(xdmRequestDto.getCorpId()).concat("with auto pair flag-") + xdmRequestDto.getEnableAutoPair();
        audit.writeAuditMessage(KnConstants.UPDATE_AUTO_PAIR_CID, KnConstants.UPDATE_AUTO_PAIR_OPN, KnAuditHelper.STATUS.REQUEST, auditStr);
        KnIPCorpInfoDTO ipCorpDTO = new KnIPCorpInfoDTO();
        ipCorpDTO.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        ipCorpDTO.setEnableAutoPair(xdmRequestDto.getEnableAutoPair());
        ipCorpDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
        KnCorpAutoPairingResponse respDto = corpClientIntf.updateCorpAutoPairing(ipCorpDTO, persisterTxn);
        knLogger.debug(methodName, "updatedcorpautopairing sublist operation map- ", KnGDPRTemplate.mapKeyMdn(respDto.getChangeLogMap()));
        int pairedContactListId = respDto.getPairedContactListId();
        KnCorpResponseDTO grpResp = null;
        knLogger.debug(methodName, "respDto obtained- ", respDto);
        if (ipCorpDTO.getEnableAutoPair()) {
            if (respDto != null && respDto.getStatus() == KnConstants.STATUS_SUCCESS) {
                if (respDto.getGroupPairing() != null) {
                    knLogger.debug(methodName, "respDto.isCreateGroup()is not null- ", respDto.getGroupPairing());
                    if (respDto.getGroupPairing()) {
                        //call the create group
                        knLogger.debug(methodName, "Create Group Flow");
                        KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
                        groupInfoDTO.setGroupDisplayName(respDto.getGroupName());
                        groupInfoDTO.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
                        List<Integer> sublistIds = new ArrayList<Integer>();
                        sublistIds.add(pairedContactListId);
                        groupInfoDTO.setAddedSublistIds(sublistIds);
                        groupInfoDTO.setAddedMemberDTOMdns(new ArrayList<KnCorpContactDTO>());
                        groupInfoDTO.setGroupType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP);
                        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
                        groupInfoDTO.setOperationType(KnOperationTypes.CREATE_GROUP);
                        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
                        groupInfoDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
                        knLogger.debug(methodName, "Invoke create Group from add to pairing list", groupInfoDTO);
                        grpResp = corpClientIntf.createGroup(groupInfoDTO, persisterTxn);
                        knLogger.debug(methodName, "updatedcorpautopairing corpGroupResp map- ", KnGDPRTemplate.mapKeyMdn(grpResp.getChangeLogMap()));
                        knLogger.debug(methodName, "updatedcorpautopairing corpGroupResp map- ", grpResp);
                    } else if (!respDto.getGroupPairing()) {
                        //call the modify group
                        knLogger.debug(methodName, "Modify Group Flow");
                        KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
                        groupInfoDTO.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
                        groupInfoDTO.setGroupId(respDto.getGroupId());
                        groupInfoDTO.setETag(Integer.valueOf(respDto.getEtag()));
                        List<Integer> sublistIds = new ArrayList<Integer>();
                        sublistIds.add(pairedContactListId);
                        groupInfoDTO.setAddedSublistIds(sublistIds);
                        groupInfoDTO.setRemovedMemberMdns(new LinkedList<String>());
                        groupInfoDTO.setRemovedSublistIds(new ArrayList<Integer>());
                        groupInfoDTO.setAddedMemberDTOMdns(new ArrayList<KnCorpContactDTO>());
                        groupInfoDTO.setGroupType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP);
                        groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
                        groupInfoDTO.setOperationType(KnOperationTypes.CREATE_GROUP);
                        groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
                        groupInfoDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
                        knLogger.debug(methodName, "Invoke modify Group from add to pairing list", groupInfoDTO);
                        KnCorpGrpBasicInfoRespDto grpBasicInfoDto = corpClientIntf.getBasicGrpInfo(groupInfoDTO, persisterTxn);
                        KnCorpGrpBasicInfoDTO grpBasicInfo = new KnCorpGrpBasicInfoDTO();
                        grpBasicInfo.setGroupId(grpBasicInfoDto.getGroupId());
                        grpBasicInfo.setGrpDisplayName(grpBasicInfoDto.getGrpDisplayName());
                        grpBasicInfo.setGrpType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP);
                        grpBasicInfo.setGroupListId(grpBasicInfoDto.getGroupListId());
                        grpBasicInfo.setGrpEtag(grpBasicInfoDto.getGrpEtag());
                        grpBasicInfo.setCorpId(grpBasicInfoDto.getCorpId());
                        grpBasicInfo.setHierarchyId(grpBasicInfoDto.getHierarchyId());
                        knLogger.debug(methodName, "Invoke modify Group with grpBasicInfo", grpBasicInfo);
                        grpResp = corpClientIntf.modifyGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
                        knLogger.debug(methodName, "updatedcorpautopairing modyGrpResp map- ", KnGDPRTemplate.mapKeyMdn(grpResp.getChangeLogMap()));
                        knLogger.debug(methodName, "updatedcorpautopairing modyGrpResp map- ", grpResp);
                    }
                }

                if (grpResp != null && grpResp.getStatus() == KnConstants.STATUS_SUCCESS) {
                    knLogger.debug(methodName, "grpResp not null ", grpResp);
                    Map<String, KnOPDirChgDTO> changeLog = grpResp.getChangeLogMap();
                    if (respDto != null) {
                        Map<String, KnOPDirChgDTO> addpairChangeLog = respDto.getChangeLogMap();
                        if (changeLog != null) {
                            for (String mdn : changeLog.keySet()) {
                                KnOPDirChgDTO directory = changeLog.get(mdn);
                                if (directory != null) {
                                    Collection<KnOPDocChgDTO> documentsList = directory.getDocChgDTO();
                                    if (documentsList == null) {
                                        documentsList = new ArrayList<KnOPDocChgDTO>();
                                    }
                                    if (addpairChangeLog != null) {
                                        KnOPDirChgDTO dir = addpairChangeLog.get(mdn);
                                        if (dir != null) {
                                            Collection<KnOPDocChgDTO> docs = dir.getDocChgDTO();
                                            if (docs != null) {
                                                documentsList.addAll(docs);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        respDto.setChangeLogMap(grpResp.getChangeLogMap());
                    } else {
                        knLogger.debug(methodName, "failure scenario", grpResp);
                        //respDto = grpResp;
                        populateResponse(respDto, grpResp);
                    }
                } else if (grpResp != null && grpResp.getStatus() == KnConstants.STATUS_FAILURE) {
                    //respDto = grpResp;
                    populateResponse(respDto, grpResp);
                }
            }
        }


        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        knLogger.debug(methodName, "xdmRespDto- ", xdmRespDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            audit.writeAuditMessage(KnConstants.UPDATE_AUTO_PAIR_CID, KnConstants.UPDATE_AUTO_PAIR_OPN, KnAuditHelper.STATUS.FAILURE, KnConstants.FAILURE_MSG);
            return xdmRespDto;
        }
        //Step:
        //prepare notification and send to notification mgr
        Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = prepareNotification(respDto);
        knLogger.debug(methodName, "Sending notifications - ", xcapDiffList);
        boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, persisterTxn);
        knLogger.debug(methodName, "Notification status - ", isNotified);
        xdmRespDto.setEnabledDispatchMemList(respDto.getEnabledDispatchMemList());
        knLogger.debug(methodName, "Returning Response - ", xdmRespDto);

        knLogger.debug(methodName, "Sending LI notifications - ");
        KnLIEventHandler.logLI(respDto.getLiEventList());
        knLogger.debug(methodName, "Li Notification Send status - ");
        //Get the xcap mobile sync flag
        boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(persisterTxn);
        knLogger.debug(methodName, "xcapMobileSync - ", xcapMobileSync);
        if (xcapMobileSync) {
            knLogger.debug(methodName, "Publishing micro service notify for Group event - ");
            commonMediator.startNotifyMicroServicesJob(respDto.getChangeLogMap(), Integer.parseInt(xdmRequestDto.getCorpId()));
        }
        populateXdmResponse(xdmRespDto, respDto);
        audit.writeAuditMessage(KnConstants.UPDATE_AUTO_PAIR_CID, KnConstants.UPDATE_AUTO_PAIR_OPN, KnAuditHelper.STATUS.SUCCESS, KnConstants.SUCCESS_MSG);
        return xdmRespDto;
    }

    private void populateResponse(KnCorpAutoPairingResponse respDto, KnCorpResponseDTO grpResp) {
        respDto.setChangeLogMap(grpResp.getChangeLogMap());
        respDto.setDisabledDispatchMemList(grpResp.getDisabledDispatchMemList());
        respDto.setEnabledDispatchMemList(grpResp.getEnabledDispatchMemList());
        respDto.setStatus(grpResp.getStatus());
        respDto.setMessage(grpResp.getMessage());
        respDto.setFailedDataList(grpResp.getFailedDataList());
        respDto.setStatusCode(grpResp.getStatusCode());
        respDto.setLiEventList(grpResp.getLiEventList());
        respDto.setEtag(grpResp.getEtag());
        respDto.setTgsModeChgMap(grpResp.getTgsModeChgMap());
    }


    /**
     * This method is used to add the sent subscriber to the paird list of the corporate
     *
     * @param contactRequestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMCorpRespDTO addToPairingList(IXDMRequestDTO contactRequestDTO, KnPersisterTxn persisterTxn)
            throws KnException {

        String methodName = "addToPairingList(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(contactRequestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    contactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) contactRequestDTO;
        audit.writeAuditMessage(KnConstants.ADD_TO_PAIRING_CID, KnConstants.ADD_TO_PAIRING_OPN, KnAuditHelper.STATUS.REQUEST, "Request recieved");
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        contactDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        contactDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        contactDTO.setName(xdmRequestDTO.getName());
        contactDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());

        knLogger.debug(methodName, "Add Corp contact - ", contactDTO);
        KnCorpAutoPairingResponse respDto = corpClientIntf.addToPairingList(contactDTO, persisterTxn);
        knLogger.debug(methodName, "Sublist operation etag map - ", respDto.getChangeLogMap());
        knLogger.debug(methodName, "Response - ", respDto.getStatus());
        int pairedContactListId = respDto.getPairedContactListId();
        KnCorpResponseDTO grpResp = new KnCorpResponseDTO();
        Map<String, Object> customParamMap = new HashMap<>();
        Collection<String> idListCollection = new ArrayList<>();
        if (respDto.getStatus() == KnConstants.STATUS_SUCCESS) {
            if (respDto.getGroupPairing() != null) {
                if (respDto.getGroupPairing()) {
                    //call the create group
                    KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
                    groupInfoDTO.setGroupDisplayName(respDto.getGroupName());
                    List<Integer> sublistIds = new ArrayList<Integer>();
                    groupInfoDTO.setCorpId(contactDTO.getCorpId());
                    sublistIds.add(pairedContactListId);
                    groupInfoDTO.setAddedSublistIds(sublistIds);
                    groupInfoDTO.setAddedMemberDTOMdns(new ArrayList<KnCorpContactDTO>());
                    groupInfoDTO.setGroupType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP);
                    groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
                    groupInfoDTO.setOperationType(KnOperationTypes.CREATE_GROUP);
                    groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
                    groupInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
                    idListCollection.add(xdmRequestDTO.getFanId());
                    customParamMap.put(IDLIST, idListCollection);
                    customParamMap.put(IDTYPE, FAN_TYPE);
                    customParamMap.put(MASTERLISTVERSIONID, MASTER_LIST_VERSION_ID_1);
                    groupInfoDTO.setCustomParamMap(customParamMap);
                    knLogger.debug(methodName, "customParamMap: ", customParamMap);
                    grpResp = corpClientIntf.createGroup(groupInfoDTO, persisterTxn);
                    knLogger.debug(methodName, "Group operation etag map - ", KnGDPRTemplate.mapKeyMdn(grpResp.getChangeLogMap()));
                } else if (!respDto.getGroupPairing()) {
                    //call the modify group
                    knLogger.debug(methodName, "Modify Group Flow");
                    KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
                    groupInfoDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
                    groupInfoDTO.setGroupId(respDto.getGroupId());
                    groupInfoDTO.setETag(Integer.valueOf(respDto.getEtag()));
                    List<Integer> sublistIds = new ArrayList<Integer>();
                    sublistIds.add(pairedContactListId);
                    groupInfoDTO.setAddedSublistIds(sublistIds);
                    groupInfoDTO.setRemovedMemberMdns(new LinkedList<String>());
                    groupInfoDTO.setRemovedSublistIds(new ArrayList<Integer>());
                    groupInfoDTO.setAddedMemberDTOMdns(new ArrayList<KnCorpContactDTO>());
                    groupInfoDTO.setGroupType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP);
                    groupInfoDTO.setEntityId(KnEntityTypes.CORP_GROUP_MANAGER);
                    groupInfoDTO.setOperationType(KnOperationTypes.CREATE_GROUP);
                    groupInfoDTO.setProfile(KnProfileTypes.CORP_PROFILE);
                    groupInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
                    knLogger.debug(methodName, "Invoke modify Group from add to pairing list", groupInfoDTO);
                    KnCorpGrpBasicInfoRespDto grpBasicInfoDto = corpClientIntf.getBasicGrpInfo(groupInfoDTO, persisterTxn);
                    KnCorpGrpBasicInfoDTO grpBasicInfo = new KnCorpGrpBasicInfoDTO();
                    grpBasicInfo.setGroupId(grpBasicInfoDto.getGroupId());
                    grpBasicInfo.setGrpDisplayName(grpBasicInfoDto.getGrpDisplayName());
                    grpBasicInfo.setGrpType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.STANDARD_GROUP);
                    grpBasicInfo.setGroupListId(grpBasicInfoDto.getGroupListId());
                    grpBasicInfo.setGrpEtag(grpBasicInfoDto.getGrpEtag());
                    grpBasicInfo.setCorpId(grpBasicInfoDto.getCorpId());
                    grpBasicInfo.setHierarchyId(grpBasicInfoDto.getHierarchyId());
                    groupInfoDTO.setOwnerCorpReq(grpBasicInfoDto.isOwnerCorpReq());
                    knLogger.debug(methodName, "Invoke modify Group with grpBasicInfo", grpBasicInfo);
                    grpResp = corpClientIntf.modifyGroup(groupInfoDTO, grpBasicInfo, persisterTxn);
                    knLogger.debug(methodName, "updatedcorpautopairing modyGrpResp map- ", KnGDPRTemplate.mapKeyMdn(grpResp.getChangeLogMap()));
                    knLogger.debug(methodName, "updatedcorpautopairing modyGrpResp map- ", grpResp);
                }
                if (grpResp != null && grpResp.getStatus() == KnConstants.STATUS_SUCCESS) {
                    knLogger.debug(methodName, "grpResp not null ", grpResp);
                    Map<String, KnOPDirChgDTO> changeLog = grpResp.getChangeLogMap();
                    if (respDto != null) {
                        Map<String, KnOPDirChgDTO> addpairChangeLog = respDto.getChangeLogMap();
                        if (changeLog != null) {
                            for (String mdn : changeLog.keySet()) {
                                KnOPDirChgDTO directory = changeLog.get(mdn);
                                if (directory != null) {
                                    Collection<KnOPDocChgDTO> documentsList = directory.getDocChgDTO();
                                    if (documentsList == null) {
                                        documentsList = new ArrayList<KnOPDocChgDTO>();
                                    }
                                    if (addpairChangeLog != null) {
                                        KnOPDirChgDTO dir = addpairChangeLog.get(mdn);
                                        if (dir != null) {
                                            Collection<KnOPDocChgDTO> docs = dir.getDocChgDTO();
                                            if (docs != null) {
                                                documentsList.addAll(docs);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        respDto.setChangeLogMap(grpResp.getChangeLogMap());
                    } else {
                        knLogger.debug(methodName, "failure scenario", grpResp);
                        //respDto = grpResp;
                        populateResponse(respDto, grpResp);
                    }
                } else if (grpResp != null && grpResp.getStatus() == KnConstants.STATUS_FAILURE) {
                    //respDto = grpResp;
                    populateResponse(respDto, grpResp);
                }
            }
        }

        knLogger.debug(methodName, "final operation etag map - ", KnGDPRTemplate.mapKeyMdn(respDto.getChangeLogMap()));
        knLogger.debug(methodName, "final operation etag map - ", respDto);


        KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            audit.writeAuditMessage(KnConstants.ADD_TO_PAIRING_CID, KnConstants.ADD_TO_PAIRING_OPN, KnAuditHelper.STATUS.FAILURE, KnConstants.FAILURE_MSG);
            return xdmRespDto;
        }
        //Step:
        //prepare notification and send to notification mgr
        Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = prepareNotification(respDto);
        knLogger.debug(methodName, "Sending notifications - ", xcapDiffList);
        boolean isNotified = notifier.sendXcapDiffNotifications(xcapDiffList, persisterTxn);
        knLogger.debug(methodName, "Notification status - ", isNotified);
        knLogger.debug(methodName, "Returning Response - ", xdmRespDto);

        knLogger.debug(methodName, "Sending LI notifications - ");
        KnLIEventHandler.logLI(respDto.getLiEventList());
        knLogger.debug(methodName, "Li Notification Send status - ");
        //Get the xcap mobile sync flag
        boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(persisterTxn);
        knLogger.debug(methodName, "xcapMobileSync - ", xcapMobileSync);
        //Get the RMQ configurations
        // KnMqServiceConfig rmqInfoDto = commonMediator.retrieveServerConfDetails(persisterTxn);
        //knLogger.debug(methodName, "rmqInfoDto - ", rmqInfoDto);
        if (xcapMobileSync) {
            knLogger.debug(methodName, "Publishing micro service notify for Group event - ");
            commonMediator.startNotifyMicroServicesJob(respDto.getChangeLogMap(), Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        audit.writeAuditMessage(KnConstants.ADD_TO_PAIRING_CID, KnConstants.ADD_TO_PAIRING_OPN, KnAuditHelper.STATUS.SUCCESS, KnConstants.SUCCESS_MSG);
        return xdmRespDto;
    }

    private Collection<KnXcapDiffDirChgNotifyDTO> prepareNotification(KnCorpResponseDTO respDto) {
        String methodName = "prepareNotifications(KnCorpResponseDTO)";
        Collection<KnXcapDiffDirChgNotifyDTO> xcapDiffList = new ArrayList<KnXcapDiffDirChgNotifyDTO>();
        Map<String, KnOPDirChgDTO> changeLogMap = respDto.getChangeLogMap();
        if (changeLogMap != null) {
            for (Map.Entry<String, KnOPDirChgDTO> entry : changeLogMap.entrySet()) {
                KnOPDirChgDTO dirChgDTO = entry.getValue();
                List<KnXcapDiffDocDTO> diffDocList = new ArrayList<KnXcapDiffDocDTO>();
                Collection<KnOPDocChgDTO> doclist = dirChgDTO.getDocChgDTO();
                if (doclist != null) {
                    for (KnOPDocChgDTO docDto : doclist) {
                        KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
                        xcapDiffDocDTO.setDocumentSelector(docDto.getDocUri());
                        xcapDiffDocDTO.setDocUri(docDto.getEntryUri());
                        xcapDiffDocDTO.setDocChangeType(docDto.getDocumentChgType());
                        xcapDiffDocDTO.setDocEtag(docDto.getNewEtag());
                        //Added the setting of the parameters needed for the xcap doc diff notifications
                        Collection<KnSubscriberDTO> addedContactList = docDto.getAddedContactList();
                        int addContLstSize = 0;
                        if (addedContactList != null) {
                            addContLstSize = addedContactList.size();
                        }
                        Collection<String> deletedContactList = docDto.getRemovedContactList();
                        int delContLstSize = 0;
                        if (deletedContactList != null) {
                            delContLstSize = deletedContactList.size();
                        }
                        if (addContLstSize + delContLstSize <= DIFF_SIZE) {
                            if (KnMediatorConstants.CORP_BROADCAST_GROUP_TYPE != docDto.getGroupType()) {
                                xcapDiffDocDTO.setAddedContactList(addedContactList);
                                xcapDiffDocDTO.setRemovedContactList(deletedContactList);
                            }
                        }
                        xcapDiffDocDTO.setGroupName(docDto.getGroupName());
                        xcapDiffDocDTO.setGroupMemCount(docDto.getGroupMemCount());
                        if (KnMediatorConstants.CORP_BROADCAST_GROUP_TYPE == docDto.getGroupType()) {
                            xcapDiffDocDTO.setGroupMemCount(docDto.getGroupMemCount());
                        }
                        Collection<KnSubscriberDTO> addedGrpMemList = docDto.getAddedGroupMembers();
                        int addGrpMemLstSize = 0;
                        if (addedGrpMemList != null) {
                            addGrpMemLstSize = addedGrpMemList.size();
                        }
                        Collection<String> deletedGrpMemList = docDto.getRemovedGroupMembers();
                        int delGrpMemLstSize = 0;
                        if (deletedGrpMemList != null) {
                            delGrpMemLstSize = deletedGrpMemList.size();
                        }
                        Collection<KnSubscriberDTO> modGrpMemList = docDto.getModifiedGrpMembers();
                        int modGrpMemLstSize = 0;
                        if (modGrpMemList != null) {
                            modGrpMemLstSize = modGrpMemList.size();
                        }
                        if (addGrpMemLstSize + delGrpMemLstSize + modGrpMemLstSize <= DIFF_SIZE) {
                            if (KnMediatorConstants.CORP_BROADCAST_GROUP_TYPE != docDto.getGroupType()) {
                                xcapDiffDocDTO.setAddedGroupMembers(addedGrpMemList);
                                xcapDiffDocDTO.setRemovedGroupMembers(deletedGrpMemList);
                                xcapDiffDocDTO.setModifiedGrpMembers(modGrpMemList);
                            }
                        }
                        Collection<KnSubscriberDTO> modContactList = docDto.getModifiedContactMembers();
                        int modContLstSize = 0;
                        if (modContactList != null) {
                            modContLstSize = modContactList.size();
                        }
                        if (modContLstSize <= DIFF_SIZE) {
                            if (KnMediatorConstants.CORP_BROADCAST_GROUP_TYPE != docDto.getGroupType()) {
                                xcapDiffDocDTO.setModifiedContactList(docDto.getModifiedContactMembers());
                            }
                        }
                        xcapDiffDocDTO.setPrevDocEtag(docDto.getPrevEtag());
                        xcapDiffDocDTO.setVideoPermission(docDto.getVideoPermission());
                        diffDocList.add(xcapDiffDocDTO);
                    }
                }
                KnXcapDiffDirChgNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffDirChgNotifyDTO();
                xcapDiffNotifyDTO.setXcapRootUri(dirChgDTO.getXcapRootURI());
                Collections.sort(diffDocList);
                xcapDiffNotifyDTO.setDocDiffObj(diffDocList);
                xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
                xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
                xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
                xcapDiffNotifyDTO.setPocHome(dirChgDTO.getPocHome());
                xcapDiffNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());
                xcapDiffNotifyDTO.setNotfnCapability(dirChgDTO.isNotfnCapability());
                xcapDiffNotifyDTO.setProtocolVersion(dirChgDTO.getProtoVersion());
                xcapDiffList.add(xcapDiffNotifyDTO);
            }
        }
        knLogger.debug(methodName, "xcapNotifications - ", xcapDiffList);
        return xcapDiffList;
    }

    public KnCorpSubscrFeatureSetRespDto getAllCorpSubscrFeatureSets(IXDMRequestDTO requestDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {
        String methodName = "getAllCorpSubscrFeatureSets()";
        //Step:
        if (!(requestDTO instanceof KnCorpSubbscrFeatureInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - xdmRequestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpSubbscrFeatureInfoRequestDTO xdmRequestDTO = (KnCorpSubbscrFeatureInfoRequestDTO) requestDTO;

        KnIPCorpAuthInfoDTO ipCorpAuthInfoDTO = new KnIPCorpAuthInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipCorpAuthInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipCorpAuthInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());

        knLogger.debug(methodName, "Get All Corp subscriber feature sets - ", ipCorpAuthInfoDTO);
        KnSubscrFeatureSetRespDTO respDto = corpClientIntf.getAllCorpSubscrFeatureSets(ipCorpAuthInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Get All Corp subscriber feature sets Response - ", respDto);

        KnCorpSubscrFeatureSetRespDto xdmRespDto = new KnCorpSubscrFeatureSetRespDto();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        List<KnSubscriberFeatureBitInfo> subsFeatureInfoList = new ArrayList<>();
        List<KnCorpSubscrInfoDTO> xdmLibsubscrList = respDto.getSubscrInfoDTOList();
        knLogger.debug(methodName, "xdmLibsubscrList - ", xdmLibsubscrList);
        if (xdmLibsubscrList != null) {
            for (KnCorpSubscrInfoDTO corpSubscrInfoDTO : xdmLibsubscrList) {
                KnSubscriberFeatureBitInfo subscriberFeatureBitInfo = new KnSubscriberFeatureBitInfo();
                subscriberFeatureBitInfo.setMdn(corpSubscrInfoDTO.getMdn());
                subscriberFeatureBitInfo.setClientFS2(corpSubscrInfoDTO.getClientFS2());
                subscriberFeatureBitInfo.setClientFS(KnGeneralUtil.convertHexStringToLong(corpSubscrInfoDTO.getClientFS2()));
                subscriberFeatureBitInfo.setCorpAdminFS2(corpSubscrInfoDTO.getCorpAdminFS2());
                subscriberFeatureBitInfo.setCorpAdminFS(KnGeneralUtil.convertHexStringToLong(corpSubscrInfoDTO.getCorpAdminFS2()));
                subsFeatureInfoList.add(subscriberFeatureBitInfo);
            }
        }
        knLogger.debug(methodName, "subsFeatureInfoList - ", subsFeatureInfoList);
        xdmRespDto.setSubsFeatureInfoList(subsFeatureInfoList);
        knLogger.debug(methodName, "Returning get corp Group Details response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO updateCorpAdminFS(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateCorpAdminFS()";
        //Step:
        if (!(requestDTO instanceof KnCorpSubbscrFeatureInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - xdmRequestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpSubbscrFeatureInfoRequestDTO xdmRequestDTO = (KnCorpSubbscrFeatureInfoRequestDTO) requestDTO;
        List<String> mdnList = new ArrayList<>();
        KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO = new KnIPSubscrFeatureInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipSubscrFeatureInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipSubscrFeatureInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        ipSubscrFeatureInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        List<KnSubscriberFeatureBitInfo> reqFeatureBitDetaislList = xdmRequestDTO.getSubsFeatureInfoList();
        List<KnSubscrFeatureInfoDTO> subscrFeatureInfoDTOList = new ArrayList<>();
        for (KnSubscriberFeatureBitInfo featureBitInfo : reqFeatureBitDetaislList) {
            KnSubscrFeatureInfoDTO subscrFeatureInfoDTO = new KnSubscrFeatureInfoDTO();
            mdnList.add(featureBitInfo.getMdn());
            subscrFeatureInfoDTO.setMdn(featureBitInfo.getMdn());
            subscrFeatureInfoDTO.setPtxBit(featureBitInfo.getPtxCorpAdminFS());
            subscrFeatureInfoDTO.setPtmdBit(featureBitInfo.getPtmdCorpAdminFS());
            subscrFeatureInfoDTO.setPtlocBit(featureBitInfo.getPtlocCorpAdminFS());
            subscrFeatureInfoDTO.setTgscClientBit(featureBitInfo.getTgsclntCorpAdminFS());
            subscrFeatureInfoDTO.setBrdcrmbBit(featureBitInfo.getBrdcrmbCorpAdminFS());
            subscrFeatureInfoDTO.setGeofncBit(featureBitInfo.getGeofncCorpAdminFS());
            subscrFeatureInfoDTO.setAmbientListeningBit(featureBitInfo.getAmbientListeningCorpAdminFS());
            subscrFeatureInfoDTO.setDiscreteListeningBit(featureBitInfo.getDiscreteListeningCorpAdminFS());
            subscrFeatureInfoDTO.setUserCheckCorpBit(featureBitInfo.getUserCheckCorpAdminFS());
            subscrFeatureInfoDTO.setUserEnableCorpBit(featureBitInfo.getUserEnableCorpAdminFS());
            subscrFeatureInfoDTO.setLocPublishCorpBit(featureBitInfo.getLocPublishCorpAdminFS());
            subscrFeatureInfoDTO.setMcVideoTx(featureBitInfo.getMcVideoTxCorpAdminFS());
            subscrFeatureInfoDTO.setMcVideoRx(featureBitInfo.getMcVideoRxCorpAdminFS());
            subscrFeatureInfoDTO.setMcVideoGroupRx(featureBitInfo.getMcVideoGroupRxCorpAdminFS());
            subscrFeatureInfoDTO.setMcVideoConfirmedPull(featureBitInfo.getMcVideoConfirmedPullCorpAdminFS());
            subscrFeatureInfoDTO.setMcDevice(featureBitInfo.getMcDeviceAdminFS());
            subscrFeatureInfoDTO.setWdsPatching(featureBitInfo.getWdsPatchingAdminS());
            subscrFeatureInfoDTO.setWdsRecording(featureBitInfo.getWdsRecordingAdminFS());
            subscrFeatureInfoDTO.setPttRecording(featureBitInfo.getPttRecordingAdminFS());
            subscrFeatureInfoDTO.setDataRecording(featureBitInfo.getDataRecordingAdminFS());
            subscrFeatureInfoDTO.setVideoRecording(featureBitInfo.getVideoRecordingAdminFS());
            subscrFeatureInfoDTO.setSelfDnDPrivilege(featureBitInfo.getSelfDnDPrivilegeFS());
            subscrFeatureInfoDTO.setLargeAgencyDispatch(featureBitInfo.getLargeAgencyDispatchFS());
            subscrFeatureInfoDTOList.add(subscrFeatureInfoDTO);
        }
        ipSubscrFeatureInfoDTO.setSubscrFeatureInfoDTOList(subscrFeatureInfoDTOList);
        knLogger.debug(methodName, "Update subscriber corp admin feature sets - ", ipSubscrFeatureInfoDTO);
        KnCorpResponseDTO respDto = corpClientIntf.updateCorpAdminFS(ipSubscrFeatureInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Update subscriber corp admin feature sets Response - ", respDto);
       /* KnXDMCorpRespDTO xdmRespDto = new KnXDMCorpRespDTO();
        xdmRespDto.setDirChgDTOs(respDto.getDirChgDTOs());
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }*/
        respDto.setMdnList(mdnList);
        knLogger.debug(methodName, "Returning get corp Group Details response - ", respDto);
        return respDto;
    }

    public KnXDMCorpClientActResponseDTO getActivationCode(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getActivationCode(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnCorpSubsResquestDTO - requestDTO - ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnCorpSubsResquestDTO xdmRequestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPSubscriberInfoDTO activationDTO = new KnIPSubscriberInfoDTO();
        activationDTO.setCorpId(xdmRequestDTO.getCorpId());
        activationDTO.setMdnList(xdmRequestDTO.getMdnList());
        activationDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        activationDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());

        knLogger.debug(methodName, "Calling getActivationCode - ", activationDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.getActivationCode(activationDTO, persisterTxn);
        knLogger.debug(methodName, "After getActivationCode - ", respDto);
        KnXDMCorpClientActResponseDTO xdmRespDto = new KnXDMCorpClientActResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnXDMCorpActivationDTO> actCodeList = respDto.getActivationCodeList();
        knLogger.debug(methodName, "actCodeList - ", actCodeList);
        xdmRespDto.setActCodeList(actCodeList);
        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpRespDTO getCorpSubscriberDetails(IXDMRequestDTO requestDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getCorpSubscriberDetails(IXDMRequestDTO, boolean, KnPersisterTxn)";
        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnCorpSubsResquestDTO - requestDTO - ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        KnCorpSubsResquestDTO subsResquestDTO = (KnCorpSubsResquestDTO) requestDTO;
        ipSubscriberInfoDTO.setMdn(subsResquestDTO.getSubscriberMdn());
        ipSubscriberInfoDTO.setAliasMdn(subsResquestDTO.getAliasMdn());
        ipSubscriberInfoDTO.setUserId(subsResquestDTO.getUserId());
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setCustomParamMap(subsResquestDTO.getCustomParamMap());
        ipSubscriberInfoDTO.setHierarchyType(subsResquestDTO.getHierarchyType());
        knLogger.debug(methodName, "Calling corp library for modify Subsc name");
        KnCorpResponseDTO subsProfileInfoDTO = corpClientIntf.getCorpSubscriberDetails(ipSubscriberInfoDTO, readOnly, persisterTxn);
        knLogger.debug(methodName, "Returned from corporate library");
        KnXDMCorpRespDTO responseDTO = new KnXDMCorpRespDTO();
        populateXdmResponse(responseDTO, subsProfileInfoDTO);
        return responseDTO;
    }

    public KnXDMCorpContactListRespDTO getCorpExtContactDetails(IXDMRequestDTO conactRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getCorpExtContactDetails(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(conactRequestDTO instanceof KnXDMCorpExtContactListRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpExtContactListRequestDTO - requestDTO - ",
                    conactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpExtContactListRequestDTO xdmRequestDTO = (KnXDMCorpExtContactListRequestDTO) conactRequestDTO;
        KnIPCorpSubscContactListDTO contactListDTO = new KnIPCorpSubscContactListDTO();
        contactListDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        knLogger.debug(methodName, "Retrieve External Contact For the Corporate.");
        KnCorpContactListRespDTO respDto = corpClientIntf.getCorpExtContactDetails(contactListDTO, persisterTxn);
        KnXDMCorpContactListRespDTO xdmRespDto = new KnXDMCorpContactListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        Collection<KnCorpSubscriberDTO> contactList = respDto.getContactList();
        if (contactList != null) {
            List<KnXDMCorpContactDTO> xdmContactList = new ArrayList<KnXDMCorpContactDTO>();
            for (KnCorpSubscriberDTO subs : contactList) {
                KnXDMCorpContactDTO xdmContact = new KnXDMCorpContactDTO();
                xdmContact.setMdn(subs.getMdn());
                xdmContact.setName(subs.getName());
                xdmContact.setContactType(String.valueOf(subs.getContact_type()));
                xdmContact.setCorpId(subs.getCorpId());
                xdmContactList.add(xdmContact);
            }
            xdmRespDto.setContactInfoList(xdmContactList);
        }
        xdmRespDto.setCorpId(xdmRequestDTO.getCorpId());
        knLogger.debug(methodName, "After Retrieval of external contact. Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO switchConvergedClient(KnXDMSubsProvInfoDTO xdmRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "switchConvergedClient()";
        KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO = new KnIPSubscrFeatureInfoDTO();
        KnSubscrFeatureInfoDTO subscrFeatureInfoDTO = new KnSubscrFeatureInfoDTO();
        ipSubscrFeatureInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        ipSubscrFeatureInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        subscrFeatureInfoDTO.setMdn(xdmRequestDTO.getMdn());
        subscrFeatureInfoDTO.setEnabledPttRadio(xdmRequestDTO.isEnablePttRadio());
        ipSubscrFeatureInfoDTO.setSubsDetailsDTO(subscrFeatureInfoDTO);
        ipSubscrFeatureInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "Switch Subscriber Client Profile - ", ipSubscrFeatureInfoDTO);
        KnCorpResponseDTO respDto = corpClientIntf.switchConvergedClient(ipSubscrFeatureInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Switch Subscriber Client Profile Response - ", respDto);
        return respDto;
    }

    public KnXDMCorpProfileInfoRespDTO getCorporateProfile(IXDMRequestDTO conactRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getCorporateProfile(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(conactRequestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    conactRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) conactRequestDTO;
        KnIPSubsProvInfoDTO contactListDTO = new KnIPSubsProvInfoDTO();
        contactListDTO.setCorpId(xdmRequestDTO.getCorpId());
        knLogger.debug(methodName, "Retrieve corporate profile for the requested Corporate.", xdmRequestDTO.getCorpId());
        KnCorpProfileInfoRespDTO respDto = corpClientIntf.getCorporateProfile(contactListDTO, persisterTxn);
        KnXDMCorpProfileInfoRespDTO xdmRespDto = new KnXDMCorpProfileInfoRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        xdmRespDto.setCorpId(respDto.getCorpId());
        xdmRespDto.setExtCorpId(respDto.getExtCorpId());
        xdmRespDto.setCorpName(respDto.getCorpName());
        xdmRespDto.setMaxTextMsgSize(respDto.getMaxTextMsgSize());
        xdmRespDto.setMaxMmmsgSizeCell(respDto.getMaxMmmsgSizeCell());
        xdmRespDto.setMaxMmmsgSizeWifi(respDto.getMaxMmmsgSizeWifi());
        xdmRespDto.setDeliveryReceiptFlag(respDto.getDeliveryReceiptFlag());
        xdmRespDto.setReadReportFlag(respDto.getReadReportFlag());
        xdmRespDto.setMsgTtl(respDto.getMsgTtl());
        xdmRespDto.setMaxPredefinedMsgCnt(respDto.getMaxPredefinedMsgCnt());
        xdmRespDto.setMaxPredefinedTmpltCnt(respDto.getMaxPredefinedTmpltCnt());
        xdmRespDto.setMaxUserDefinedMsgCnt(respDto.getMaxUserDefinedMsgCnt());
        xdmRespDto.setFleetMemberGeoTagFlag(respDto.getFleetMemberGeoTagFlag());
        xdmRespDto.setMaxAbdgTalkGroup(respDto.getMaxAbdgTalkGroup());
        xdmRespDto.setMaxLrGabTalkGroup(respDto.getMaxLrGabTalkGroup());
        xdmRespDto.setMaxUsrLrGabGroup(respDto.getMaxUsrLrGabGroup());
        xdmRespDto.setMaxAbdgGrpPerOwner(respDto.getMaxAbdgGrpPerOwner());
        xdmRespDto.setMaxAbdgGrpPerMem(respDto.getMaxAbdgGrpPerMem());
        xdmRespDto.setLastProfileUpdateTime(respDto.getLastProfileUpdateTime());
        xdmRespDto.setLmrDataIntropFlag(respDto.getLmrIntropFlag());
        xdmRespDto.setUgwInteropFlag(respDto.getUgwInteropFlag());
        xdmRespDto.setXdmCorpFs2Set(respDto.getXdmCorpFs2Set());
        knLogger.debug(methodName, "After Retrieval of Corporate profile. Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpClientActResponseDTO getSubscrActivationCode(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "generateActivationCodes(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpClientActRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpClientActRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpClientActRequestDTO xdmRequestDTO = (KnXDMCorpClientActRequestDTO) requestDTO;
        KnIPCorpActivationDTO activationDTO = new KnIPCorpActivationDTO();
        activationDTO.setMdn(xdmRequestDTO.getMdn());
        knLogger.debug(methodName, "Calling getSubscrActivationCode - ", activationDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.getSubscrActivationCode(activationDTO, persisterTxn);
        knLogger.debug(methodName, "After generateActivationCodes - ", respDto);
        KnXDMCorpClientActResponseDTO xdmRespDto = new KnXDMCorpClientActResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            xdmRespDto.setResponseMap(respDto.getResponseMap());
            return xdmRespDto;
        }
        String actCode = respDto.getActivationCode();
        knLogger.debug(methodName, "actCode - ", actCode);
        xdmRespDto.setActivationCode(actCode);
        xdmRespDto.setExpiryTimestamp(respDto.getExpiryTimestamp());
        xdmRespDto.setActivationTimestamp(respDto.getActivationTimestamp());
        xdmRespDto.setResponseMap(respDto.getResponseMap());
        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }

    public IXDMResponseDTO generateOTP(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "generateOTP(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpClientActRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpClientActRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpClientActRequestDTO xdmRequestDTO = (KnXDMCorpClientActRequestDTO) requestDTO;
        KnIPCorpActivationDTO activationDTO = new KnIPCorpActivationDTO();
        activationDTO.setMdn(xdmRequestDTO.getMdn());
        activationDTO.setExtCorpId(xdmRequestDTO.getExtCorpId());
        knLogger.debug(methodName, "Calling generateOTP - ", activationDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.generateOTP(activationDTO, persisterTxn);
        knLogger.debug(methodName, "After generateOTP - ", respDto);
        KnXDMCorpClientActResponseDTO xdmRespDto = new KnXDMCorpClientActResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            xdmRespDto.setResponseMap(respDto.getResponseMap());
            return xdmRespDto;
        }
        xdmRespDto.setResponseMap(respDto.getResponseMap());
        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }

    public IXDMResponseDTO validateOTP(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "validateOTP(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpClientActRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpClientActRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpClientActRequestDTO xdmRequestDTO = (KnXDMCorpClientActRequestDTO) requestDTO;
        KnIPCorpActivationDTO activationDTO = new KnIPCorpActivationDTO();
        activationDTO.setMdn(xdmRequestDTO.getMdn());
        activationDTO.setActivationCode(xdmRequestDTO.getOtp());
        knLogger.debug(methodName, "Calling generateOTP - ", activationDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.validateOTP(activationDTO, persisterTxn);
        knLogger.debug(methodName, "After generateOTP - ", respDto);
        KnXDMCorpClientActResponseDTO xdmRespDto = new KnXDMCorpClientActResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            xdmRespDto.setResponseMap(respDto.getResponseMap());
            return xdmRespDto;
        }
        xdmRespDto.setResponseMap(respDto.getResponseMap());
        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }

    public IXDMResponseDTO getCorpBanFanDetails(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getCorpBanFanDetails(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpClientActRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpClientActRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpClientActRequestDTO xdmRequestDTO = (KnXDMCorpClientActRequestDTO) requestDTO;
        KnIPCorpActivationDTO activationDTO = new KnIPCorpActivationDTO();
        activationDTO.setExtCorpId(xdmRequestDTO.getExtCorpId());
        knLogger.debug(methodName, "Calling getCorpBanFanDetails - ", activationDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.getCorpBanFanDetails(activationDTO, persisterTxn);
        knLogger.debug(methodName, "After getCorpBanFanDetails - ", respDto);
        KnXDMCorpClientActResponseDTO xdmRespDto = new KnXDMCorpClientActResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            xdmRespDto.setResponseMap(respDto.getResponseMap());
            return xdmRespDto;
        }
        String actCode = respDto.getActivationCode();
        knLogger.debug(methodName, "actCode - ", actCode);
        xdmRespDto.setResponseMap(respDto.getResponseMap());
        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO updateCorpSubscriber(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateCorpSubscriber(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnCorpSubsResquestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpSubsResquestDTO subsResquestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        ipSubscriberInfoDTO.setHierarchyType(subsResquestDTO.getHierarchyType());
        ipSubscriberInfoDTO.setMdn(subsResquestDTO.getSubscriberMdn());
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setSubscrName(subsResquestDTO.getSubscriberName());
        if (subsResquestDTO.getSubscriptionType() != null) {
            ipSubscriberInfoDTO.setSubscriptionType(Integer.parseInt(subsResquestDTO.getSubscriptionType()));
        }
        ipSubscriberInfoDTO.setSubscriberEmail(subsResquestDTO.getEmail());
        ipSubscriberInfoDTO.setCustomParamMap(subsResquestDTO.getCustomParamMap());
        ipSubscriberInfoDTO.setDispatchType(subsResquestDTO.getDispatchType());
        ipSubscriberInfoDTO.setUserId(subsResquestDTO.getUserId());
        ipSubscriberInfoDTO.setAliasMdn(subsResquestDTO.getAliasMdn());
        ipSubscriberInfoDTO.setTmpPwdMode(subsResquestDTO.getTmpPwdMode());
        ipSubscriberInfoDTO.setSendAccountMail(subsResquestDTO.getSendAccountMail());
        knLogger.debug(methodName, "subsResquestDTO.getServiceAuthStatus()", subsResquestDTO.getServiceAuthStatus());
        if (subsResquestDTO.getServiceAuthStatus() != null) {
            ipSubscriberInfoDTO.setServiceAuthStatusAU(Integer.parseInt(subsResquestDTO.getServiceAuthStatus()));
        }
        knLogger.debug(methodName, "subsResquestDTO.getPttSettingDocId()", subsResquestDTO.getPttSettingDocId());
        if (subsResquestDTO.getPttSettingDocId() != null) {
            ipSubscriberInfoDTO.setPttSettingDocId(subsResquestDTO.getPttSettingDocId());
        }
        knLogger.debug(methodName, "Calling corp library for updateCorpSubscriber");
        KnCorpResponseDTO respDto = corpClientIntf.updateCorpSubscriber(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Returned from corporate library");
        KnXDMCorpUserDetailsRespDTO idmSubsDetails = new KnXDMCorpUserDetailsRespDTO();
        Map<String, Object> responseMap = respDto.getResponseMap();
        idmSubsDetails.setCorpId((String) responseMap.get(com.kodiak.common.resources.KnConstants.CORP_ID));
        idmSubsDetails.setUserId((String) responseMap.get(com.kodiak.common.resources.KnConstants.USER_ID));
        idmSubsDetails.setMdn((String) responseMap.get(com.kodiak.common.resources.KnConstants.MDN_IDM));
        idmSubsDetails.setUserType((String) responseMap.get(com.kodiak.common.resources.KnConstants.USER_TYPE));
        respDto.setIdmSubscriberDTO(idmSubsDetails);
        KnXDMSubsAliasDetailsReqDTO oidcSubsDetails = new KnXDMSubsAliasDetailsReqDTO();
        Map<String, Object> oidcAttributes = new HashMap<>();
        oidcSubsDetails.setUserid((String) responseMap.get(com.kodiak.common.resources.KnConstants.MDN_AS_USER_ID));
        oidcSubsDetails.setTemppwd(Boolean.TRUE);
        oidcSubsDetails.setEmail((String) responseMap.get(com.kodiak.common.resources.KnConstants.USER_ID));
        oidcAttributes.put(MCPTT_ID_OIDC, (String) responseMap.get(com.kodiak.common.resources.KnConstants.MCPTT_ID_OIDC));
        oidcAttributes.put(MCVIDEO_ID_OIDC, (String) responseMap.get(com.kodiak.common.resources.KnConstants.MCVIDEO_ID_OIDC));
        oidcAttributes.put(MCDATA_ID_OIDC, (String) responseMap.get(com.kodiak.common.resources.KnConstants.MCDATA_ID_OIDC));
        List<String> actionsAsTempPwdMode = new ArrayList<>();
        if (subsResquestDTO.getTmpPwdMode() == SEND_PWD_MODE.MAIL.value()) {
            actionsAsTempPwdMode.add(TMP_PWD_MODE.PASSWORD_INFO_MAIL.value());
        } else if (subsResquestDTO.getTmpPwdMode() == SEND_PWD_MODE.SMS.value()) {
            actionsAsTempPwdMode.add(TMP_PWD_MODE.PASSWORD_INFO_SMS.value());
        } else if (subsResquestDTO.getTmpPwdMode() == SEND_PWD_MODE.BOTH.value()) {
            actionsAsTempPwdMode.add(TMP_PWD_MODE.PASSWORD_INFO_MAIL.value());
            actionsAsTempPwdMode.add(TMP_PWD_MODE.PASSWORD_INFO_SMS.value());
        }
        oidcAttributes.put(ACTIONS, actionsAsTempPwdMode);
        oidcSubsDetails.setAttributes(oidcAttributes);
        respDto.setOidcSubscriberDTO(oidcSubsDetails);
        return respDto;
    }

    public IXDMResponseDTO generateActivationCodeIDMIntf(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "generateActivationCodeIDMIntf(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpClientActRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpClientActRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpClientActRequestDTO xdmRequestDTO = (KnXDMCorpClientActRequestDTO) requestDTO;
        KnIPCorpActivationDTO activationDTO = new KnIPCorpActivationDTO();
        activationDTO.setMdn(xdmRequestDTO.getMdn());
        knLogger.debug(methodName, "Calling generateActivationCodeIDMIntf - ", activationDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.generateActivationCodeIDMIntf(activationDTO, persisterTxn);
        knLogger.debug(methodName, "After generateActivationCodeIDMIntf - ", respDto);
        KnXDMCorpClientActResponseDTO xdmRespDto = new KnXDMCorpClientActResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            xdmRespDto.setResponseMap(respDto.getResponseMap());
            return xdmRespDto;
        }
        xdmRespDto.setResponseMap(respDto.getResponseMap());
        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnLITargetProfileInfoRespDTO getLITargetInfo(KnPersisterTxn persisterTxn) {
        String methodName = "getLITargetInfo(KnPersisterTxn)";
        knLogger.debug(methodName, "Retrieve LI Target profile");
        KnCorpLITargetInfoRespDTO respDto = corpClientIntf.getLITargetInfo(persisterTxn);
        KnLITargetProfileInfoRespDTO xdmRespDto = new KnLITargetProfileInfoRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        Collection<KnLITargetProfileInfo> liTargetProfileInfos = new ArrayList<>();
        Collection<KnCorpLITargetProfile> corpLiTargetProfileInfos = respDto.getLiTargetProfileList();
        KnLITargetProfileInfo targetInfo = null;
        if (corpLiTargetProfileInfos != null && !corpLiTargetProfileInfos.isEmpty()) {
            for (KnCorpLITargetProfile liTargetProfile : corpLiTargetProfileInfos) {
                targetInfo = new KnLITargetProfileInfo();
                targetInfo.setMdn(liTargetProfile.getMdn());
                targetInfo.setInsertionTime(liTargetProfile.getInsertionTime());
                targetInfo.setLIID(liTargetProfile.getLIID());
                targetInfo.setLIPttServerId(liTargetProfile.getLIPttServerId());
                targetInfo.setLICCCPttServerId(liTargetProfile.getLICCCPttServerId());
                targetInfo.setDFCCIPAddress(liTargetProfile.getDFCCIPAddress());
                targetInfo.setDFCCPort(liTargetProfile.getDFCCPort());
                targetInfo.setIPAddressType(liTargetProfile.getIPAddressType());
                liTargetProfileInfos.add(targetInfo);
            }
            xdmRespDto.setLiTargetProfileInfoDTO(liTargetProfileInfos);
        }
        knLogger.debug(methodName, "After Retrieval of Corporate profile. Response - ", xdmRespDto);
        return xdmRespDto;
    }


    public KnCorpSubsInfoRespDTO populateRespSubscriberProfile(KnCorpSubsInfoRespDTO responseDTO, KnOPSubsProfileInfoDTO subsProfileInfoDTO) {
        String methodName = "populateRespSubscriberProfile(KnCorpSubsInfoRespDTO, KnOPSubsProfileInfoDTO)";
        knLogger.debug(methodName, "populating the XDM Response DTO ");
        if (subsProfileInfoDTO != null) {
            if (subsProfileInfoDTO.getCameraType() != null) {
                responseDTO.setCameraType(subsProfileInfoDTO.getCameraType());
            }
            responseDTO.setCameraInfo(subsProfileInfoDTO.getCameraInfo());
            responseDTO.setMdn(subsProfileInfoDTO.getMdn());
            responseDTO.setIMEI(subsProfileInfoDTO.getIMEI());
            responseDTO.setPoCHome(subsProfileInfoDTO.getPoCHome());
            responseDTO.setPresenceHome(subsProfileInfoDTO.getPresenceHome());
            responseDTO.setXDMSHome(subsProfileInfoDTO.getXDMSHome());
            responseDTO.setNetworkName(subsProfileInfoDTO.getNetworkName());
            responseDTO.setServiceAuthStatus(subsProfileInfoDTO.getServiceAuthStatus());
            responseDTO.setPublicSubscriptionType(subsProfileInfoDTO.getPublicSubscriptionType());
            responseDTO.setCorporateSubscriptionType(subsProfileInfoDTO.getCorporateSubscriptionType());
            responseDTO.setPairingInd(subsProfileInfoDTO.getPairingInd());
            responseDTO.setExtCorpId(subsProfileInfoDTO.getExtCorpId());
            responseDTO.setPayType(subsProfileInfoDTO.getPayType());
            responseDTO.setAffiliateId(subsProfileInfoDTO.getAffiliateId());
            responseDTO.setRoamingType(subsProfileInfoDTO.getRoamingTypes());
            responseDTO.setEmailAddress(subsProfileInfoDTO.getEmailAddress());
            responseDTO.setSubscriberClientType(subsProfileInfoDTO.getSubsClientType());
            responseDTO.setDispatchGroupMember(subsProfileInfoDTO.getDispatchGroupMember());
            responseDTO.setCorpId(String.valueOf(subsProfileInfoDTO.getCorpId()));
            responseDTO.setCorporateName(subsProfileInfoDTO.getCorporateName());
            responseDTO.setAccountId(subsProfileInfoDTO.getAccountId());
            responseDTO.setUserAgent(subsProfileInfoDTO.getUserAgent());
            responseDTO.setSubsCreationTime(subsProfileInfoDTO.getSubsCreationTime());
            responseDTO.setLastActivationTime(subsProfileInfoDTO.getLastActivationTime());
            responseDTO.setBillingMDN(subsProfileInfoDTO.getBillingMDN());
            responseDTO.setRoamingAllowed(subsProfileInfoDTO.getRoamingAllowed());
            responseDTO.setTpUser(subsProfileInfoDTO.getTpUser());
            responseDTO.setTpAccount(subsProfileInfoDTO.getTpAccount());
            responseDTO.setDispatchType(subsProfileInfoDTO.getDispatchType());
            responseDTO.setUserId(subsProfileInfoDTO.getUserId());
            responseDTO.setPoCStatusAU(subsProfileInfoDTO.getPoCStatusAU());
            responseDTO.setPoCStatusOP(subsProfileInfoDTO.getPoCStatusOP());
            responseDTO.setPkgIdMap(subsProfileInfoDTO.getPkgIdMap());
            responseDTO.setAliasMdn(subsProfileInfoDTO.getAliasMdn());
            responseDTO.setLicenseType(subsProfileInfoDTO.getLicenseType());
            responseDTO.setSegmtIndc(subsProfileInfoDTO.getFirstNetIndicator());
            responseDTO.setActiveFS(KnGeneralUtil.convertHexStringToLong(subsProfileInfoDTO.getActiveFS2()));
            responseDTO.setActiveFS2(subsProfileInfoDTO.getActiveFS2());
            responseDTO.setCorpAdminFS(KnGeneralUtil.convertHexStringToLong(subsProfileInfoDTO.getCorpAdminFS2()));
            responseDTO.setCorpAdminFS2(subsProfileInfoDTO.getCorpAdminFS2());
            responseDTO.setClientFS1(KnGeneralUtil.convertHexStringToLong(subsProfileInfoDTO.getClientFS2()));
            responseDTO.setClientFS2(subsProfileInfoDTO.getClientFS2());
            responseDTO.setSubsFeatureSet(KnGeneralUtil.convertHexStringToLong(subsProfileInfoDTO.getSubsFS2()));
            responseDTO.setSubsFeatureSet2(subsProfileInfoDTO.getSubsFS2());
            responseDTO.setMcId(subsProfileInfoDTO.getMcId());
            responseDTO.setMcpttId(subsProfileInfoDTO.getMcpttId());
            responseDTO.setMcVideoId(subsProfileInfoDTO.getMcVideoId());
            responseDTO.setMcDataId(subsProfileInfoDTO.getMcDataId());
            responseDTO.setMcpttCompliance(subsProfileInfoDTO.getMcpttCompliance());
            responseDTO.setIsDefaultProfile(subsProfileInfoDTO.getIsDefaultProfile());
            responseDTO.setUserProfileIndex(subsProfileInfoDTO.getUserProfileIndex());
            responseDTO.setUserProfileName(subsProfileInfoDTO.getUserProfileName());
            responseDTO.setOnBoardingMailReq(subsProfileInfoDTO.getOnBoardingEmailReqd());
            responseDTO.setDeviceId(subsProfileInfoDTO.getDeviceId());
            responseDTO.setExtGatewayId(subsProfileInfoDTO.getExtGatewayId());
            responseDTO.setPttSettingDocId(subsProfileInfoDTO.getPttSettingDocId());
        }
        knLogger.debug(methodName, "subs profile ", responseDTO);
        return responseDTO;
    }

    public KnCorpResponseDTO getCorpSubsUserProfile(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getCorpSubsUserProfile(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnCorpSubsResquestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpResponseDTO responseDTO = new KnCorpResponseDTO();
        KnCorpSubsResquestDTO subsResquestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setMdn(subsResquestDTO.getSubscriberMdn());
        ipSubscriberInfoDTO.setAliasMdn(subsResquestDTO.getAliasMdn());
        ipSubscriberInfoDTO.setUserId(subsResquestDTO.getUserId());
        ipSubscriberInfoDTO.setHierarchyType(subsResquestDTO.getHierarchyType());
        ipSubscriberInfoDTO.setCustomParamMap(subsResquestDTO.getCustomParamMap());
        knLogger.debug(methodName, "Calling corp library for getCorpSubsUserProfile");
        responseDTO = corpClientIntf.getCorpSubsUserProfile(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "responseDTO- ", responseDTO);
        return responseDTO;
    }

    public KnCorpResponseDTO resetCorpSubsUserPassword(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "resetCorpSubsUserPassword(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnCorpSubsResquestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpResponseDTO responseDTO = new KnCorpResponseDTO();
        KnCorpSubsResquestDTO subsResquestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setAppId(Integer.parseInt(subsResquestDTO.getAppId()));
        ipSubscriberInfoDTO.setUserId(subsResquestDTO.getUserId());
        ipSubscriberInfoDTO.setHierarchyType(subsResquestDTO.getHierarchyType());
        ipSubscriberInfoDTO.setCustomParamMap(subsResquestDTO.getCustomParamMap());
        knLogger.debug(methodName, "Calling corp library for resetCorpSubsUserPassword");
        responseDTO = corpClientIntf.resetCorpSubsUserPassword(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "responseDTO- ", responseDTO);
        return responseDTO;
    }

    public KnCorpResponseDTO resendCorpSubsVerificationEmail(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "resendCorpSubsVerificationEmail(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnCorpSubsResquestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpResponseDTO responseDTO = new KnCorpResponseDTO();
        KnCorpSubsResquestDTO subsResquestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setAppId(Integer.parseInt(subsResquestDTO.getAppId()));
        ipSubscriberInfoDTO.setUserId(subsResquestDTO.getUserId());
        ipSubscriberInfoDTO.setHierarchyType(subsResquestDTO.getHierarchyType());
        ipSubscriberInfoDTO.setCustomParamMap(subsResquestDTO.getCustomParamMap());
        knLogger.debug(methodName, "Calling corp library for resendCorpSubsVerificationEmail");
        responseDTO = corpClientIntf.resendCorpSubsVerificationEmail(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "responseDTO- ", responseDTO);
        return responseDTO;
    }

    public KnCorpResponseDTO setTargetPermissions(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "setTargetPermissions()";
        KnCorpSubMCPTTInfoRequestDTO xdmRequestDTO = (KnCorpSubMCPTTInfoRequestDTO) requestDTO;
        KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO = new KnIPAuthUserPermissionInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipAuthUserPermissionInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipAuthUserPermissionInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        //base mdn target perm
        Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfos = xdmRequestDTO.getTargetMdnPermissionBitInfoList();
        Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = new ArrayList<>();
        targetMdnPermissionBitInfos.forEach(targetMdnPermissionBitInfo -> {
            KnTargetMdnPermBitInfo permBitInfo = new KnTargetMdnPermBitInfo();
            permBitInfo.setMdn(targetMdnPermissionBitInfo.getMdn());
            permBitInfo.setAmbientListening(targetMdnPermissionBitInfo.getAmbientListening());
            permBitInfo.setDiscreteListening(targetMdnPermissionBitInfo.getDiscreteListening());
            permBitInfo.setUserCheck(targetMdnPermissionBitInfo.getUserCheck());
            permBitInfo.setUserEnable(targetMdnPermissionBitInfo.getUserEnable());
            permBitInfo.setEmergPermission(targetMdnPermissionBitInfo.getEmergPermission());
            permBitInfo.setMcVideoUnConfirmedPull(targetMdnPermissionBitInfo.getMcVideoUnConfirmedPull());
            permBitInfo.setCommonContact(targetMdnPermissionBitInfo.getCommonContact());
            targetMdnPermBitInfos.add(permBitInfo);
        });
        ipAuthUserPermissionInfoDTO.setTargetMdnPermissionBitInfoList(targetMdnPermBitInfos);
        //profile mdn target perm
        Collection<KnTargetMdnPermissionBitInfo> targetProfileMdnPermissionBitInfos
                = xdmRequestDTO.getTargetProfileMdnPermissionBitInfoList();
        Collection<KnTargetMdnPermBitInfo> targetProfileMdnPermBitInfos = new ArrayList<>();
        if (targetProfileMdnPermissionBitInfos != null && !targetProfileMdnPermissionBitInfos.isEmpty()) {
            for (KnTargetMdnPermissionBitInfo profileMdns : targetProfileMdnPermissionBitInfos) {
                targetProfileMdnPermBitInfos.add(new KnTargetMdnPermBitInfo(profileMdns.getMdn()
                        , profileMdns.getSubsName()
                        , profileMdns.getAmbientListening(), profileMdns.getDiscreteListening()
                        , profileMdns.getUserCheck(), profileMdns.getUserEnable()
                        , profileMdns.getEmergPermission(), profileMdns.getMcVideoUnConfirmedPull()
                        , profileMdns.getPermBitSet(), profileMdns.getDiscreteEnabled()));
            }
        }
        ipAuthUserPermissionInfoDTO.setTargetProfileMdnPermissionBitInfoList(targetProfileMdnPermBitInfos);
        ipAuthUserPermissionInfoDTO.setAuthorizedMdn(xdmRequestDTO.getAuthorizedMdn());
        ipAuthUserPermissionInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        boolean isUpmCall = xdmRequestDTO.isUpmCall();
        ipAuthUserPermissionInfoDTO.setAllAuTask(xdmRequestDTO.isAllAuTask());
        knLogger.debug(methodName, "setTargetPermissions input dto - ", ipAuthUserPermissionInfoDTO, " isUpmCall:", isUpmCall);
        KnCorpResponseDTO respDto = null;
        if (!isUpmCall) {
            respDto = corpClientIntf.setTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        } else {
            ipAuthUserPermissionInfoDTO.setUpmCall(isUpmCall);
            respDto = corpClientIntf.upmSetTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        }
        knLogger.debug(methodName, "Returning setTargetPermissions response - ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO setBulkTargetPermissions(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "setBulkTargetPermissions(IXDMRequestDTO,KnPersisterTxn)";
        KnCorpSubMCPTTInfoRequestDTO xdmRequestDTO = (KnCorpSubMCPTTInfoRequestDTO) requestDTO;
        KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO = new KnIPAuthUserPermissionInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipAuthUserPermissionInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipAuthUserPermissionInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        //base mdn target perm
        Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfos = xdmRequestDTO.getTargetMdnPermissionBitInfoList();
        Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = new ArrayList<>();
        targetMdnPermissionBitInfos.forEach(targetMdnPermissionBitInfo -> {
            KnTargetMdnPermBitInfo permBitInfo = new KnTargetMdnPermBitInfo();
            permBitInfo.setMdn(targetMdnPermissionBitInfo.getMdn());
            permBitInfo.setAmbientListening(targetMdnPermissionBitInfo.getAmbientListening());
            permBitInfo.setDiscreteListening(targetMdnPermissionBitInfo.getDiscreteListening());
            permBitInfo.setUserCheck(targetMdnPermissionBitInfo.getUserCheck());
            permBitInfo.setUserEnable(targetMdnPermissionBitInfo.getUserEnable());
            permBitInfo.setEmergPermission(targetMdnPermissionBitInfo.getEmergPermission());
            permBitInfo.setMcVideoUnConfirmedPull(targetMdnPermissionBitInfo.getMcVideoUnConfirmedPull());
            permBitInfo.setCommonContact(targetMdnPermissionBitInfo.getCommonContact());
            targetMdnPermBitInfos.add(permBitInfo);
        });
        ipAuthUserPermissionInfoDTO.setTargetMdnPermissionBitInfoList(targetMdnPermBitInfos);
        //profile mdn target perm
        Collection<KnTargetMdnPermissionBitInfo> targetProfileMdnPermissionBitInfos
                = xdmRequestDTO.getTargetProfileMdnPermissionBitInfoList();
        Collection<KnTargetMdnPermBitInfo> targetProfileMdnPermBitInfos = new ArrayList<>();
        if (targetProfileMdnPermissionBitInfos != null && !targetProfileMdnPermissionBitInfos.isEmpty()) {
            for (KnTargetMdnPermissionBitInfo profileMdns : targetProfileMdnPermissionBitInfos) {
                targetProfileMdnPermBitInfos.add(new KnTargetMdnPermBitInfo(profileMdns.getMdn()
                        , profileMdns.getSubsName()
                        , profileMdns.getAmbientListening(), profileMdns.getDiscreteListening()
                        , profileMdns.getUserCheck(), profileMdns.getUserEnable()
                        , profileMdns.getEmergPermission(), profileMdns.getMcVideoUnConfirmedPull()
                        , profileMdns.getPermBitSet(), profileMdns.getDiscreteEnabled()));
            }
        }
        ipAuthUserPermissionInfoDTO.setTargetProfileMdnPermissionBitInfoList(targetProfileMdnPermBitInfos);
        ipAuthUserPermissionInfoDTO.setAuthorizedMdn(xdmRequestDTO.getAuthorizedMdn());
        ipAuthUserPermissionInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        boolean isUpmCall = xdmRequestDTO.isUpmCall();
        ipAuthUserPermissionInfoDTO.setAllAuTask(xdmRequestDTO.isAllAuTask());
        knLogger.debug(methodName, "setTargetPermissions input dto - ", ipAuthUserPermissionInfoDTO, " isUpmCall:", isUpmCall);
        KnCorpResponseDTO respDto = null;
        ipAuthUserPermissionInfoDTO.setUpmCall(isUpmCall);
        respDto = corpClientIntf.setBulkTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Returning setTargetPermissions response - ", respDto);
        return respDto;
    }

    public KnXDMCorpAuthUserPermissionRespDTO getTargetPermissions(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getTargetPermissions()";
        KnCorpSubsResquestDTO xdmRequestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO = new KnIPAuthUserPermissionInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipAuthUserPermissionInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipAuthUserPermissionInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        ipAuthUserPermissionInfoDTO.setAuthorizedMdn(xdmRequestDTO.getSubscriberMdn());
        ipAuthUserPermissionInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "getTargetPermissions input dto - ", ipAuthUserPermissionInfoDTO);
        KnCorpUserPermissionRespDTO respDto = null;
        if (CLIENT_TYPE_SOAP == xdmRequestDTO.getClientType()) {
            respDto = corpClientIntf.getTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        } else {
            respDto = corpClientIntf.getSubsTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        }
        knLogger.debug(methodName, "Returning getTargetPermissions response - ", respDto);
        KnXDMCorpAuthUserPermissionRespDTO xdmRespDto = new KnXDMCorpAuthUserPermissionRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfos = new ArrayList<>();
        Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = respDto.getTargetMdnPermissionBitInfoList();
        if (targetMdnPermBitInfos != null && !targetMdnPermBitInfos.isEmpty()) {
            targetMdnPermBitInfos.forEach(targetMdnPermBitInfo -> {
                KnTargetMdnPermissionBitInfo targetInfo = new KnTargetMdnPermissionBitInfo();
                targetInfo.setMdn(targetMdnPermBitInfo.getMdn());
                targetInfo.setAmbientListening(targetMdnPermBitInfo.getAmbientListening());
                targetInfo.setDiscreteListening(targetMdnPermBitInfo.getDiscreteListening());
                targetInfo.setUserCheck(targetMdnPermBitInfo.getUserCheck());
                targetInfo.setUserEnable(targetMdnPermBitInfo.getUserEnable());
                targetInfo.setEmergPermission(targetMdnPermBitInfo.getEmergPermission());
                targetInfo.setMcVideoUnConfirmedPull(targetMdnPermBitInfo.getMcVideoUnConfirmedPull());
                targetInfo.setCommonContact(targetMdnPermBitInfo.getCommonContact());
                if (PTX_XDMDATA_INTF == xdmRequestDTO.getClientType()) {
                    targetInfo.setPermBitSet(targetMdnPermBitInfo.getPermBitSet());
                    targetInfo.setDiscreteEnabled(targetMdnPermBitInfo.getDiscreteEnabled());
                }
                targetMdnPermissionBitInfos.add(targetInfo);
            });
            xdmRespDto.setTargetMdnPermissionBitInfo(targetMdnPermissionBitInfos);
        }
        knLogger.debug(methodName, "After Retrieval of getTargetPermissions. Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpAuthUserPermissionRespDTO getAuthorizedMdnList(IXDMRequestDTO requestDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getAuthorizedMdnList(IXDMRequestDTO,boolean,KnPersisterTxn)";
        KnCorpSubsResquestDTO xdmRequestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO = new KnIPAuthUserPermissionInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipAuthUserPermissionInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipAuthUserPermissionInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        ipAuthUserPermissionInfoDTO.setTargetMdn(xdmRequestDTO.getSubscriberMdn());
        ipAuthUserPermissionInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "getAuthorizedMdnList input dto - ", ipAuthUserPermissionInfoDTO);
        KnCorpUserPermissionRespDTO respDto = corpClientIntf.getAuthorizedMdnList(ipAuthUserPermissionInfoDTO, readOnly, persisterTxn);
        knLogger.debug(methodName, "Returning getAuthorizedMdnList response - ", respDto);
        KnXDMCorpAuthUserPermissionRespDTO xdmRespDto = new KnXDMCorpAuthUserPermissionRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfos = new ArrayList<>();
        Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = respDto.getTargetMdnPermissionBitInfoList();
        if (targetMdnPermBitInfos != null && !targetMdnPermBitInfos.isEmpty()) {
            targetMdnPermBitInfos.forEach(targetMdnPermBitInfo -> {
                KnTargetMdnPermissionBitInfo targetInfo = new KnTargetMdnPermissionBitInfo();
                targetInfo.setMdn(targetMdnPermBitInfo.getMdn());
                targetInfo.setSubsName(targetMdnPermBitInfo.getSubsName());
                targetInfo.setAmbientListening(targetMdnPermBitInfo.getAmbientListening());
                targetInfo.setDiscreteListening(targetMdnPermBitInfo.getDiscreteListening());
                targetInfo.setUserCheck(targetMdnPermBitInfo.getUserCheck());
                targetInfo.setUserEnable(targetMdnPermBitInfo.getUserEnable());
                targetInfo.setEmergPermission(targetMdnPermBitInfo.getEmergPermission());
                targetMdnPermissionBitInfos.add(targetInfo);
            });
            xdmRespDto.setTargetMdnPermissionBitInfo(targetMdnPermissionBitInfos);
        }
        knLogger.debug(methodName, "After Retrieval of getAuthorizedMdnList. Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO setSubsEmergencyAttributes(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "setSubsEmergencyAttributes()";
        KnCorpSubsEmergencyRequestDTO xdmRequestDTO = (KnCorpSubsEmergencyRequestDTO) requestDTO;
        KnIPEmergencyInfoDTO ipEmergencyInfoDTO = new KnIPEmergencyInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipEmergencyInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipEmergencyInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        KnUserEmergencyAttributes userEmergencyAttributes = xdmRequestDTO.getEmergencyAttributes();
        ipEmergencyInfoDTO.setMdn(userEmergencyAttributes.getMdn());
        ipEmergencyInfoDTO.setEmergInitPermission(userEmergencyAttributes.getEmergInitPermission());
        ipEmergencyInfoDTO.setEmergCallType(userEmergencyAttributes.getEmergCallType());
        ipEmergencyInfoDTO.setEmergDestType(userEmergencyAttributes.getEmergDestType());
        ipEmergencyInfoDTO.setPriDestination(userEmergencyAttributes.getPriDestination());
        ipEmergencyInfoDTO.setSecDestination(userEmergencyAttributes.getSecDestination());
        ipEmergencyInfoDTO.setEmergCancelPermission(userEmergencyAttributes.getEmergCancelPermission());
        ipEmergencyInfoDTO.setEmergOriginBitSet(userEmergencyAttributes.getEmergOriginBitSet());
        ipEmergencyInfoDTO.setEmergTermBitSet(userEmergencyAttributes.getEmergTermBitSet());
        ipEmergencyInfoDTO.setEmergLMRBehavior(userEmergencyAttributes.getEmergLMRBehavior());
        ipEmergencyInfoDTO.setEmergConfigTimer(userEmergencyAttributes.getEmergConfigTimer());
        ipEmergencyInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        boolean isUpmCall = xdmRequestDTO.isUpmCall();
        knLogger.debug(methodName, "setSubsEmergencyAttributes input dto - ", ipEmergencyInfoDTO, " isUpmCall:", isUpmCall);
        KnCorpResponseDTO respDto = null;
        if (!isUpmCall) {
            respDto = corpClientIntf.setSubsEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
        } else {
            respDto = corpClientIntf.upmSetSubsEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
        }
        knLogger.debug(methodName, "Returning setSubsEmergencyAttributes response - ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO setBulkSubsEmergencyAttributes(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "setBulkSubsEmergencyAttributes(IXDMRequestDTO,KnPersisterTxn)";
        KnCorpSubsEmergencyRequestDTO xdmRequestDTO = (KnCorpSubsEmergencyRequestDTO) requestDTO;
        KnIPEmergencyInfoDTO ipEmergencyInfoDTO = new KnIPEmergencyInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipEmergencyInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipEmergencyInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        KnUserEmergencyAttributes userEmergencyAttributes = xdmRequestDTO.getEmergencyAttributes();
        ipEmergencyInfoDTO.setMdn(userEmergencyAttributes.getMdn());
        ipEmergencyInfoDTO.setEmergInitPermission(userEmergencyAttributes.getEmergInitPermission());
        ipEmergencyInfoDTO.setEmergCallType(userEmergencyAttributes.getEmergCallType());
        ipEmergencyInfoDTO.setEmergDestType(userEmergencyAttributes.getEmergDestType());
        ipEmergencyInfoDTO.setPriDestination(userEmergencyAttributes.getPriDestination());
        ipEmergencyInfoDTO.setSecDestination(userEmergencyAttributes.getSecDestination());
        ipEmergencyInfoDTO.setEmergCancelPermission(userEmergencyAttributes.getEmergCancelPermission());
        ipEmergencyInfoDTO.setEmergOriginBitSet(userEmergencyAttributes.getEmergOriginBitSet());
        ipEmergencyInfoDTO.setEmergTermBitSet(userEmergencyAttributes.getEmergTermBitSet());
        ipEmergencyInfoDTO.setEmergLMRBehavior(userEmergencyAttributes.getEmergLMRBehavior());
        ipEmergencyInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        boolean isUpmCall = xdmRequestDTO.isUpmCall();
        knLogger.debug(methodName, "setSubsEmergencyAttributes input dto - ", ipEmergencyInfoDTO, " isUpmCall:", isUpmCall);
        KnCorpResponseDTO respDto = null;
        respDto = corpClientIntf.setUserProfileEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);

        knLogger.debug(methodName, "Returning setSubsEmergencyAttributes response - ", respDto);
        return respDto;
    }

    public KnUserEmergDestResponseDTO getUserEmergDest(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getUserEmergDest()";
        KnCorpSubsEmergencyRequestDTO xdmRequestDTO = (KnCorpSubsEmergencyRequestDTO) requestDTO;
        KnIPEmergencyInfoDTO ipEmergencyInfoDTO = new KnIPEmergencyInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipEmergencyInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipEmergencyInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        KnUserEmergencyAttributes userEmergencyAttributes = xdmRequestDTO.getEmergencyAttributes();
        ipEmergencyInfoDTO.setEmergDestType(userEmergencyAttributes.getEmergDestType());
        ipEmergencyInfoDTO.setPriDestination(userEmergencyAttributes.getPriDestination());
        ipEmergencyInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "getUserEmergDest input dto - ", ipEmergencyInfoDTO);
        KnEmergUserDestRespDTO respDto = corpClientIntf.getUserEmergDest(ipEmergencyInfoDTO, persisterTxn);
        KnUserEmergDestResponseDTO xdmRespDto = new KnUserEmergDestResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        List<KnMDNInfoDto> mdnInfoDtoList = respDto.getMdnInfoDtoList();
        knLogger.debug(methodName, "mdnInfoDtoList - ", mdnInfoDtoList);
        List<KnXDMMdnInfoDTO> mdnInfoDTOs = new ArrayList<>();
        if (mdnInfoDtoList != null) {
            for (KnMDNInfoDto contact : mdnInfoDtoList) {
                KnXDMMdnInfoDTO mdnInfoDTO = new KnXDMMdnInfoDTO();
                mdnInfoDTO.setMdn(contact.getMdn());
                mdnInfoDTOs.add(mdnInfoDTO);
            }
        }
        xdmRespDto.setEmergencyUsers(mdnInfoDTOs);
        knLogger.debug(methodName, "Emergency User", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpEmergencyRespDTO getSubsEmergencyAttributes(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getSubsEmergencyAttributes()";
        KnCorpSubsResquestDTO xdmRequestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPEmergencyInfoDTO ipEmergencyInfoDTO = new KnIPEmergencyInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipEmergencyInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipEmergencyInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        ipEmergencyInfoDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        ipEmergencyInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "getSubsEmergencyAttributes input dto - ", ipEmergencyInfoDTO);
        KnCorpUserEmergencyAttributesRespDTO respDto = corpClientIntf.getSubsEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Returning getSubsEmergencyAttributes response - ", respDto);
        KnXDMCorpEmergencyRespDTO xdmRespDto = new KnXDMCorpEmergencyRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        KnUserEmergencyAttributes userEmergencyAttributes = new KnUserEmergencyAttributes();
        KnCorpUserEmergencyAttributes corpUserEmergencyAttributes = respDto.getCorpUserEmergencyAttributes();
        if (corpUserEmergencyAttributes != null) {
            userEmergencyAttributes.setEmergInitPermission(corpUserEmergencyAttributes.getEmergInitPermission());
            userEmergencyAttributes.setEmergCancelPermission(corpUserEmergencyAttributes.getEmergCancelPermission());
            userEmergencyAttributes.setEmergCallType(corpUserEmergencyAttributes.getEmergCallType());
            userEmergencyAttributes.setEmergOriginBitSet(corpUserEmergencyAttributes.getEmergOriginBitSet());
            userEmergencyAttributes.setEmergDestType(corpUserEmergencyAttributes.getEmergDestType());
            userEmergencyAttributes.setPriDestination(corpUserEmergencyAttributes.getPriDestination());
            userEmergencyAttributes.setSecDestination(corpUserEmergencyAttributes.getSecDestination());
            userEmergencyAttributes.setMdn(corpUserEmergencyAttributes.getMdn());
            userEmergencyAttributes.setEmergLMRBehavior(corpUserEmergencyAttributes.getEmergLMRBehavior());
            userEmergencyAttributes.setEmergTermBitSet(corpUserEmergencyAttributes.getEmergTermBitSet());
            userEmergencyAttributes.setEmergConfigTimer(corpUserEmergencyAttributes.getEmergConfigTimer());
            xdmRespDto.setCorpUserEmergencyAttributes(userEmergencyAttributes);
        }
        knLogger.debug(methodName, "After Retrieval of getSubsEmergencyAttributes. Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO updateSubsAliasEntities(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateSubsAliasEntities(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnCorpSubsResquestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpSubsResquestDTO subsResquestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        ipSubscriberInfoDTO.setMdn(subsResquestDTO.getSubscriberMdn());
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setAliasMdn(subsResquestDTO.getAliasMdn());
        ipSubscriberInfoDTO.setUserId(subsResquestDTO.getUserId());
        knLogger.debug(methodName, "Calling corp library for updateSubsAliasEntities");
        KnCorpResponseDTO respDto = corpClientIntf.updateSubsAliasEntities(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Returned from corporate library");
        return respDto;
    }

    public KnCorpResponseDTO generateTempPassword(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "generateTempPassword(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnCorpSubsResquestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpResponseDTO responseDTO = new KnCorpResponseDTO();
        KnCorpSubsResquestDTO subsResquestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setMdn(subsResquestDTO.getSubscriberMdn());
        ipSubscriberInfoDTO.setAliasMdn(subsResquestDTO.getAliasMdn());
        ipSubscriberInfoDTO.setUserId(subsResquestDTO.getUserId());
        ipSubscriberInfoDTO.setHierarchyType(subsResquestDTO.getHierarchyType());
        ipSubscriberInfoDTO.setCustomParamMap(subsResquestDTO.getCustomParamMap());
        knLogger.debug(methodName, "Calling corp library for generateTempPassword");
        responseDTO = corpClientIntf.generateTempPassword(ipSubscriberInfoDTO, persisterTxn);
        return responseDTO;
    }

    public KnCorpResponseDTO sendTempPassword(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "sendTempPassword(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnCorpSubsResquestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpResponseDTO responseDTO = new KnCorpResponseDTO();
        KnCorpSubsResquestDTO subsResquestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setMdn(subsResquestDTO.getSubscriberMdn());
        ipSubscriberInfoDTO.setAliasMdn(subsResquestDTO.getAliasMdn());
        ipSubscriberInfoDTO.setHierarchyType(subsResquestDTO.getHierarchyType());
        ipSubscriberInfoDTO.setCustomParamMap(subsResquestDTO.getCustomParamMap());
        knLogger.debug(methodName, "Calling corp library for sendTempPassword");
        responseDTO = corpClientIntf.sendTempPassword(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "responseDTO- ", responseDTO);
        return responseDTO;
    }

    public KnCorpResponseDTO modifySubscriberTGList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "modifySubscriberTGList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMAddlTalkGroupRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMAddlTalkGroupRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMAddlTalkGroupRequestDTO addlTalkGrpDTO = (KnXDMAddlTalkGroupRequestDTO) requestDTO;
        knLogger.debug(methodName, "addlTalkGrpDTO ", addlTalkGrpDTO);
        KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
        ipTalkGroupDTO.setCustomParamMap(addlTalkGrpDTO.getCustomParamMap());
        ipTalkGroupDTO.setCorpId(addlTalkGrpDTO.getCorpId());
        ipTalkGroupDTO.setMdn(addlTalkGrpDTO.getMdn());
        if (addlTalkGrpDTO.getAddedAddlTgList() != null) {
            Collection<KnCorpAddlTGInfoDTO> addedAddlTgList = new ArrayList<>();
            Collection<KnXDMAddlTalkGroupInfoDTO> addlTalkGroupInfoDTOS = addlTalkGrpDTO.getAddedAddlTgList();
            addlTalkGroupInfoDTOS.forEach(addTG -> {
                KnCorpAddlTGInfoDTO addlTGInfoDTO = new KnCorpAddlTGInfoDTO();
                addlTGInfoDTO.setMdn(addlTalkGrpDTO.getMdn());
                addlTGInfoDTO.setGroupId(addTG.getGroupId());
                addlTGInfoDTO.setZoneId(addTG.getZoneId());
                addlTGInfoDTO.setZoneName(addTG.getZoneName());
                addlTGInfoDTO.setChannelId(addTG.getChannelId());
                addedAddlTgList.add(addlTGInfoDTO);
            });
            ipTalkGroupDTO.setAddedAddlTgList(addedAddlTgList);
        }
        if (addlTalkGrpDTO.getModifiedAddlTgList() != null) {
            Collection<KnCorpAddlTGInfoDTO> modifiedAddlTgList = new ArrayList<>();
            Collection<KnXDMAddlTalkGroupInfoDTO> modifyTalkGroupInfoDTOS = addlTalkGrpDTO.getModifiedAddlTgList();
            modifyTalkGroupInfoDTOS.forEach(modTG -> {
                KnCorpAddlTGInfoDTO modifyTGInfoDTO = new KnCorpAddlTGInfoDTO();
                modifyTGInfoDTO.setMdn(addlTalkGrpDTO.getMdn());
                modifyTGInfoDTO.setGroupId(modTG.getGroupId());
                modifyTGInfoDTO.setZoneId(modTG.getZoneId());
                modifyTGInfoDTO.setZoneName(modTG.getZoneName());
                modifyTGInfoDTO.setChannelId(modTG.getChannelId());
                modifiedAddlTgList.add(modifyTGInfoDTO);
            });
            ipTalkGroupDTO.setModifiedAddlTgList(modifiedAddlTgList);
        }
        if (addlTalkGrpDTO.getRemovedAddlTgList() != null) {
            Collection<KnCorpAddlTGInfoDTO> removedAddlTgList = new ArrayList<>();
            Collection<KnXDMAddlTalkGroupInfoDTO> removeTalkGroupInfoDTOS = addlTalkGrpDTO.getRemovedAddlTgList();
            removeTalkGroupInfoDTOS.forEach(remTG -> {
                KnCorpAddlTGInfoDTO removeTGInfoDTO = new KnCorpAddlTGInfoDTO();
                removeTGInfoDTO.setGroupId(remTG.getGroupId());
                removedAddlTgList.add(removeTGInfoDTO);
            });
            ipTalkGroupDTO.setRemovedAddlTgList(removedAddlTgList);
        }
        ipTalkGroupDTO.setClientType(addlTalkGrpDTO.getClientType());
        ipTalkGroupDTO.setHierarchyType(addlTalkGrpDTO.getHierarchyType());
        ipTalkGroupDTO.setCalledFromModifyUPM(addlTalkGrpDTO.isCalledFromModifyUPM());
        boolean upmCall = addlTalkGrpDTO.isUpmCall();
        knLogger.debug(methodName, " upmCall :", upmCall);
        KnCorpResponseDTO respDto = null;
        knLogger.debug(methodName, "Calling corp library for modify Subscriber TG List");
        if (!upmCall) {
            respDto = corpClientIntf.modifySubscriberTGList(ipTalkGroupDTO, persisterTxn);
        } else {
            respDto = corpClientIntf.upmModifySubscriberTGList(ipTalkGroupDTO, persisterTxn);
        }

        knLogger.debug(methodName, "Returned from corporate library");
        return respDto;
    }

    public KnCorpResponseDTO modifyBulkSubscriberTGList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "modifyBulkSubscriberTGList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMAddlTalkGroupRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMAddlTalkGroupRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMAddlTalkGroupRequestDTO addlTalkGrpDTO = (KnXDMAddlTalkGroupRequestDTO) requestDTO;
        knLogger.debug(methodName, "addlTalkGrpDTO ", addlTalkGrpDTO);
        KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
        ipTalkGroupDTO.setCustomParamMap(addlTalkGrpDTO.getCustomParamMap());
        ipTalkGroupDTO.setCorpId(addlTalkGrpDTO.getCorpId());
        ipTalkGroupDTO.setMdn(addlTalkGrpDTO.getMdn());
        if (addlTalkGrpDTO.getAddedAddlTgList() != null) {
            Collection<KnCorpAddlTGInfoDTO> addedAddlTgList = new ArrayList<>();
            Collection<KnXDMAddlTalkGroupInfoDTO> addlTalkGroupInfoDTOS = addlTalkGrpDTO.getAddedAddlTgList();
            addlTalkGroupInfoDTOS.forEach(addTG -> {
                KnCorpAddlTGInfoDTO addlTGInfoDTO = new KnCorpAddlTGInfoDTO();
                addlTGInfoDTO.setMdn(addlTalkGrpDTO.getMdn());
                addlTGInfoDTO.setGroupId(addTG.getGroupId());
                addlTGInfoDTO.setZoneId(addTG.getZoneId());
                addlTGInfoDTO.setZoneName(addTG.getZoneName());
                addlTGInfoDTO.setChannelId(addTG.getChannelId());
                addedAddlTgList.add(addlTGInfoDTO);
            });
            ipTalkGroupDTO.setAddedAddlTgList(addedAddlTgList);
        }
        if (addlTalkGrpDTO.getModifiedAddlTgList() != null) {
            Collection<KnCorpAddlTGInfoDTO> modifiedAddlTgList = new ArrayList<>();
            Collection<KnXDMAddlTalkGroupInfoDTO> modifyTalkGroupInfoDTOS = addlTalkGrpDTO.getModifiedAddlTgList();
            modifyTalkGroupInfoDTOS.forEach(modTG -> {
                KnCorpAddlTGInfoDTO modifyTGInfoDTO = new KnCorpAddlTGInfoDTO();
                modifyTGInfoDTO.setMdn(addlTalkGrpDTO.getMdn());
                modifyTGInfoDTO.setGroupId(modTG.getGroupId());
                modifyTGInfoDTO.setZoneId(modTG.getZoneId());
                modifyTGInfoDTO.setZoneName(modTG.getZoneName());
                modifyTGInfoDTO.setChannelId(modTG.getChannelId());
                modifiedAddlTgList.add(modifyTGInfoDTO);
            });
            ipTalkGroupDTO.setModifiedAddlTgList(modifiedAddlTgList);
        }
        if (addlTalkGrpDTO.getRemovedAddlTgList() != null) {
            Collection<KnCorpAddlTGInfoDTO> removedAddlTgList = new ArrayList<>();
            Collection<KnXDMAddlTalkGroupInfoDTO> removeTalkGroupInfoDTOS = addlTalkGrpDTO.getRemovedAddlTgList();
            removeTalkGroupInfoDTOS.forEach(remTG -> {
                KnCorpAddlTGInfoDTO removeTGInfoDTO = new KnCorpAddlTGInfoDTO();
                removeTGInfoDTO.setGroupId(remTG.getGroupId());
                removedAddlTgList.add(removeTGInfoDTO);
            });
            ipTalkGroupDTO.setRemovedAddlTgList(removedAddlTgList);
        }
        ipTalkGroupDTO.setClientType(addlTalkGrpDTO.getClientType());
        ipTalkGroupDTO.setHierarchyType(addlTalkGrpDTO.getHierarchyType());
        ipTalkGroupDTO.setCalledFromModifyUPM(addlTalkGrpDTO.isCalledFromModifyUPM());
        boolean upmCall = addlTalkGrpDTO.isUpmCall();
        knLogger.debug(methodName, " upmCall :", upmCall);
        KnCorpResponseDTO respDto = null;
        knLogger.debug(methodName, "Calling corp library for modify Subscriber TG List");
        respDto = corpClientIntf.upmModifyBulkSubscriberTGList(ipTalkGroupDTO, persisterTxn);

        knLogger.debug(methodName, "Returned from corporate library");
        return respDto;
    }


    public KnXDMTalkGroupRespDTO getSubscriberTGList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getSubscriberTGList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMAddlTalkGroupRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMAddlTalkGroupRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMAddlTalkGroupRequestDTO talkGrpDTO = (KnXDMAddlTalkGroupRequestDTO) requestDTO;
        KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
        ipTalkGroupDTO.setCorpId(talkGrpDTO.getCorpId());
        ipTalkGroupDTO.setMdn(talkGrpDTO.getMdn());
        ipTalkGroupDTO.setMcPttId(talkGrpDTO.getMcpttId());
        ipTalkGroupDTO.setHierarchyType(talkGrpDTO.getHierarchyType());
        ipTalkGroupDTO.setIfMatch(talkGrpDTO.getIfMatch());
        ipTalkGroupDTO.setIfNoneMatch(talkGrpDTO.getIfNoneMatch());
        ipTalkGroupDTO.setHierarchyType(talkGrpDTO.getHierarchyType());
        ipTalkGroupDTO.setCustomParamMap(talkGrpDTO.getCustomParamMap());
        KnXDMTalkGroupServerRespDTO respDto = null;
        knLogger.debug(methodName, "Calling corp library for getSubscriberTGList");
        if (talkGrpDTO.getClientType() != CLIENT_TYPE_XCAP) {
            respDto = corpClientIntf.getSubscriberTGList(ipTalkGroupDTO, persisterTxn);
        } else {
            respDto = corpClientIntf.getSubsTGList(ipTalkGroupDTO, persisterTxn);
        }
        knLogger.debug(methodName, "Returned from corporate library", respDto);
        KnXDMTalkGroupRespDTO xdmRespDto = new KnXDMTalkGroupRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnXDMAddlTalkGroupInfoDTO> respAddlTGInfoDTOS = new ArrayList<>();
        Collection<KnCorpAddlTGInfoDTO> addlTGInfoDTOS = respDto.getAddlTGList();
        int pvVersion = respDto.getSubscrProtocolversion();
        if (addlTGInfoDTOS != null) {
            addlTGInfoDTOS.forEach(addTG -> {
                KnXDMAddlTalkGroupInfoDTO addlTGInfoDTO = new KnXDMAddlTalkGroupInfoDTO();
                addlTGInfoDTO.setMdn(addTG.getMdn());
                addlTGInfoDTO.setGroupId(addTG.getGroupId());
                if (addTG.getZoneId() != null) addlTGInfoDTO.setZoneId(addTG.getZoneId());
                addlTGInfoDTO.setZoneName(addTG.getZoneName());
                if (addTG.getChannelId() != null) addlTGInfoDTO.setChannelId(addTG.getChannelId());
                if (talkGrpDTO.getClientType() == CLIENT_TYPE_XCAP) {
                    if (addTG.getGroupType() != 0) {
                        addlTGInfoDTO.setGroupType(addTG.getGroupType());
                        addlTGInfoDTO.setAvatar(addTG.getAvatar());
                        addlTGInfoDTO.setMemberCount(addTG.getGroupMemCount());
                        if (pvVersion >= PROTOCOL_VERSION_16) {
                            addlTGInfoDTO.setOSMListId(addTG.getOSMListId());
                        }
                        if (pvVersion > PROTOCOL_VERSION_13) {
                            addlTGInfoDTO.setCreatedBy(addTG.getGroupCreatedBy());
                        }
                        if (pvVersion > PROTOCOL_VERSION_18) {
                            addlTGInfoDTO.setMcxGroupInd(addTG.getMcxGrpInd());
                        }

                    }
                    xdmRespDto.setPvVersion(pvVersion);
                }
                respAddlTGInfoDTOS.add(addlTGInfoDTO);
            });
            xdmRespDto.setAddlTalkGroupInfo(respAddlTGInfoDTOS);
            if (talkGrpDTO.getClientType() == CLIENT_TYPE_XCAP) {
                //xdmRespDto.setCorpId(respDto.getMdnCorpId());
                xdmRespDto.setGroupsIDcorpIDMap(respDto.getGroupIDcorpIDMap());
                xdmRespDto.setDocEtag(respDto.getEtag());
            }
        }
        knLogger.debug(methodName, "xdmRespDto:: ", xdmRespDto);
        return xdmRespDto;
    }


    public KnCorpResponseDTO deleteTGList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteTGList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMAddlTalkGroupRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMAddlTalkGroupRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMAddlTalkGroupRequestDTO addlTalkGrpDTO = (KnXDMAddlTalkGroupRequestDTO) requestDTO;
        KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
        ipTalkGroupDTO.setCustomParamMap(addlTalkGrpDTO.getCustomParamMap());
        ipTalkGroupDTO.setCorpId(addlTalkGrpDTO.getCorpId());
        ipTalkGroupDTO.setMdn(addlTalkGrpDTO.getMdn());
        ipTalkGroupDTO.setClientType(addlTalkGrpDTO.getClientType());
        ipTalkGroupDTO.setHierarchyType(addlTalkGrpDTO.getHierarchyType());
        KnCorpResponseDTO respDto = null;
        knLogger.debug(methodName, "Calling corp library for modify deleteTGList");
        respDto = corpClientIntf.deleteTGList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug(methodName, "Returned from corporate library");
        return respDto;
    }

    public KnXDMCorpEmergencyRespDTO getSubsEmergencyDetails(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getSubsEmergencyDetails()";
        KnXDMActivateInfoDTO xdmRequestDTO = (KnXDMActivateInfoDTO) requestDTO;
        KnIPEmergencyInfoDTO ipEmergencyInfoDTO = new KnIPEmergencyInfoDTO();
        ipEmergencyInfoDTO.setMdn(xdmRequestDTO.getMdn());
        ipEmergencyInfoDTO.setMcptt_id(xdmRequestDTO.getMcptt_id());
        knLogger.debug(methodName, "`", ipEmergencyInfoDTO);
        KnCorpResponseDTO respDto = corpClientIntf.getSubsEmergencyDetails(ipEmergencyInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Returning getSubsEmergencyAttributes response - ", respDto);
        KnXDMCorpEmergencyRespDTO xdmRespDto = new KnXDMCorpEmergencyRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        knLogger.debug(methodName, "After Retrieval of getSubsEmergencyAttributes. Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO addBulkGroupsToSubscriber(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "addBulkGroupsToSubscriber(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnCorpSubsResquestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpResponseDTO responseDTO = new KnCorpResponseDTO();
        KnCorpSubsResquestDTO subsResquestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setToMdn(subsResquestDTO.getToMdn());
        ipSubscriberInfoDTO.setFromMdn(subsResquestDTO.getFromMdn());
        ipSubscriberInfoDTO.setGroupIds(subsResquestDTO.getGroupIds());
        ipSubscriberInfoDTO.setHierarchyType(subsResquestDTO.getHierarchyType());
        ipSubscriberInfoDTO.setCustomParamMap(subsResquestDTO.getCustomParamMap());
        knLogger.debug(methodName, "Calling corp library for addBulkGroupsToSubscriber");
        responseDTO = corpClientIntf.addBulkGroupsToSubscriber(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "responseDTO- ", responseDTO);
        return responseDTO;
    }

    public KnCorpResponseDTO cloneScanListToSubscriber(KnPersisterTxn persisterTxn,
                                                       KnIPCorpSubscCloningListDTO corpSubscCloningListDTO) throws KnXDMServerException {
        String methodName = "cloneScanListToSubscriber( KnPersisterTxn)";
        KnCorpResponseDTO responseDTO = new KnCorpResponseDTO();
        KnIPTalkGroupDTO modifySubscriberTGListRequest = corpSubscCloningListDTO.getModifySubscriberTGListRequest();
        KnIPTalkGroupDTO modifySubscriberScanListRequest = corpSubscCloningListDTO.getModifySubscriberScanListRequest();
        knLogger.debug(methodName, "modifySubscriberTGListRequest", modifySubscriberTGListRequest);
        knLogger.debug(methodName, "modifySubscriberScanListRequest", modifySubscriberScanListRequest);


        if (modifySubscriberTGListRequest.getAddedAddlTgList().isEmpty()
                && modifySubscriberTGListRequest.getRemovedAddlTgList().isEmpty()
                && modifySubscriberTGListRequest.getModifiedAddlTgList().isEmpty()
                && modifySubscriberScanListRequest.getAddedCampGrpList().isEmpty()
                && modifySubscriberScanListRequest.getRemovedCammpGrpList().isEmpty()
                && modifySubscriberScanListRequest.getModifiedCampGrpList().isEmpty()) {
            return populate(responseDTO);
        }

        if (modifySubscriberTGListRequest.getAddedAddlTgList().isEmpty()
                && modifySubscriberTGListRequest.getRemovedAddlTgList().isEmpty()
                && modifySubscriberTGListRequest.getModifiedAddlTgList().isEmpty()) {
            knLogger.debug(methodName, "No added or removed campgrp list to process");
        } else {
            responseDTO = corpClientIntf.upmModifyBulkSubscriberTGList(modifySubscriberTGListRequest, persisterTxn);
            knLogger.debug(methodName, "Calling corp library for modify Subscriber TG List -respDto", responseDTO);
        }

        if (modifySubscriberScanListRequest.getAddedCampGrpList().isEmpty()
                && modifySubscriberScanListRequest.getRemovedCammpGrpList().isEmpty()
                && modifySubscriberScanListRequest.getModifiedCampGrpList().isEmpty()) {
            knLogger.debug(methodName, "No added or removed campgrp list to process");
        } else {
            modifySubscriberScanListRequest.setOperationType(KnOperationTypes.CLONE_CONTACTS_GROUP_FEATURES_VALIDATION);
            responseDTO = corpClientIntf.upmModifyBulkSubscriberScanList(modifySubscriberScanListRequest, persisterTxn);
            knLogger.debug(methodName, "modifySubscriberScanListRequest - responseDTO", responseDTO);
        }

        return responseDTO;
    }

    public void getScanListRequests(KnCorpSubsResquestDTO xdmRequestDTO,
                                    KnPersisterTxn persisterTxn,
                                    KnIPTalkGroupDTO modifySubscriberTGListRequest,
                                    KnIPTalkGroupDTO modifySubscriberScanListRequest) {
        String methodName = "cloneScanListToSubscriber(IXDMRequestDTO, KnPersisterTxn)";

        KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
        ipTalkGroupDTO.setCorpId(xdmRequestDTO.getCorpId());
        ipTalkGroupDTO.setMdn(xdmRequestDTO.getFromMdn());
        ipTalkGroupDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "Calling corp library for getSubscriberTGList");

        KnXDMTalkGroupServerRespDTO subsTGListForSource = corpClientIntf.getSubscriberTGList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug(methodName, "subsTGListForSource", subsTGListForSource);

        Map<Integer, Collection<KnCorpAddlTGInfoDTO>> sourceCorpAddlTGInfoMap = new HashMap<>();
        Collection<KnCorpAddlTGInfoDTO> addlTGListForSource = subsTGListForSource.getAddlTGList();
        if (null != addlTGListForSource && !addlTGListForSource.isEmpty()) {
            addlTGListForSource.forEach(addlTG -> {
                sourceCorpAddlTGInfoMap.put(addlTG.getGroupId(),
                        addlTGListForSource.stream().filter(x -> x.getGroupId() == addlTG.getGroupId()).collect(Collectors.toList()));
            });
            knLogger.debug(methodName, "sourceCorpAddlTGInfoMap", sourceCorpAddlTGInfoMap);
        }

        ipTalkGroupDTO.setMdn(xdmRequestDTO.getToMdn());
        KnXDMTalkGroupServerRespDTO subsTGListForTarget = corpClientIntf.getSubscriberTGList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug(methodName, "subsTGListForTarget", subsTGListForTarget);

        Map<Integer, Collection<KnCorpAddlTGInfoDTO>> targetCorpAddlTGInfoMap = new HashMap<>();
        Collection<KnCorpAddlTGInfoDTO> addlTGListForTarget = subsTGListForTarget.getAddlTGList();
        if (null != addlTGListForTarget && !addlTGListForTarget.isEmpty()) {
            addlTGListForTarget.forEach(addlTG -> {
                targetCorpAddlTGInfoMap.put(addlTG.getGroupId(),
                        addlTGListForTarget.stream().filter(x -> x.getGroupId() == addlTG.getGroupId()).collect(Collectors.toList()));
            });
            knLogger.debug(methodName, "targetCorpAddlTGInfoMap", targetCorpAddlTGInfoMap);
        }

        knLogger.debug(methodName, "Common elements between two maps");
        Collection<KnCorpAddlTGInfoDTO> modifiedAddlTgList = new ArrayList<>();
        Collection<KnCorpAddlTGInfoDTO> addedAddlTgList = new ArrayList<>();
        if (!sourceCorpAddlTGInfoMap.isEmpty()) {
            sourceCorpAddlTGInfoMap.entrySet().stream().filter(e -> targetCorpAddlTGInfoMap.containsKey(e.getKey())).forEach(e -> {
                Collection<KnCorpAddlTGInfoDTO> value = e.getValue();
                value.forEach(val -> {
                    KnCorpAddlTGInfoDTO addlTGInfoDTO = new KnCorpAddlTGInfoDTO();
                    addlTGInfoDTO.setMdn(xdmRequestDTO.getToMdn());
                    addlTGInfoDTO.setGroupId(e.getKey());
                    addlTGInfoDTO.setZoneId(val.getZoneId());
                    addlTGInfoDTO.setZoneName(val.getZoneName());
                    addlTGInfoDTO.setChannelId(val.getChannelId());
                    modifiedAddlTgList.add(addlTGInfoDTO);
                });
            });
            knLogger.debug(methodName, "Common elements between two maps", modifiedAddlTgList);

            knLogger.debug(methodName, "Elements only in source but absent in target");

            List<Integer> groupIds = new ArrayList<>(sourceCorpAddlTGInfoMap.keySet());
            Map<Integer, KnCorpGroupDTO> groupDetailsMap = corpClientIntf.getGroupDetailsMap(groupIds, KnDbUtil.getDBConfigInfo().getLocalPttId(), persisterTxn);
            sourceCorpAddlTGInfoMap.entrySet().stream().filter(e -> !targetCorpAddlTGInfoMap.containsKey(e.getKey())).forEach(e -> {
                Collection<KnCorpAddlTGInfoDTO> value = e.getValue();
                value.forEach(val -> {
                    KnCorpAddlTGInfoDTO addlTGInfoDTO = new KnCorpAddlTGInfoDTO();
                    addlTGInfoDTO.setMdn(xdmRequestDTO.getToMdn());
                    addlTGInfoDTO.setGroupId(e.getKey());
                    addlTGInfoDTO.setZoneId(val.getZoneId());
                    addlTGInfoDTO.setZoneName(val.getZoneName());
                    addlTGInfoDTO.setChannelId(val.getChannelId());
                    if (!groupDetailsMap.containsKey(e.getKey()) || groupDetailsMap.get(e.getKey()).getGroupType() != 3) {
                        addedAddlTgList.add(addlTGInfoDTO);
                    }
                });
            });
            knLogger.debug(methodName, "Elements only in source but absent in target", addedAddlTgList);
        }

        knLogger.debug(methodName, "Elements only in target but absent in source");
        Collection<KnCorpAddlTGInfoDTO> removedAddlTgList = new ArrayList<>();
        targetCorpAddlTGInfoMap.entrySet().stream().filter(e -> !sourceCorpAddlTGInfoMap.containsKey(e.getKey())).forEach(e -> {
            Collection<KnCorpAddlTGInfoDTO> value = e.getValue();
            value.forEach(val -> {
                KnCorpAddlTGInfoDTO addlTGInfoDTO = new KnCorpAddlTGInfoDTO();
                addlTGInfoDTO.setMdn(xdmRequestDTO.getToMdn());
                addlTGInfoDTO.setGroupId(e.getKey());
                addlTGInfoDTO.setZoneId(val.getZoneId());
                addlTGInfoDTO.setZoneName(val.getZoneName());
                addlTGInfoDTO.setChannelId(val.getChannelId());
                removedAddlTgList.add(addlTGInfoDTO);
            });
        });
        knLogger.debug(methodName, "Elements only in target but absent in source", removedAddlTgList);

        // KnIPTalkGroupDTO modifySubscriberTGListRequest = new KnIPTalkGroupDTO();
        modifySubscriberTGListRequest.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        modifySubscriberTGListRequest.setCorpId(xdmRequestDTO.getCorpId());
        modifySubscriberTGListRequest.setMdn(xdmRequestDTO.getToMdn());
        modifySubscriberTGListRequest.setAddedAddlTgList(addedAddlTgList);
        modifySubscriberTGListRequest.setModifiedAddlTgList(modifiedAddlTgList);
        modifySubscriberTGListRequest.setRemovedAddlTgList(removedAddlTgList);
        modifySubscriberTGListRequest.setClientType(xdmRequestDTO.getClientType());
        modifySubscriberTGListRequest.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "finalSubscriberTGListRequest", modifySubscriberTGListRequest);
//=====================================================================================

        ipTalkGroupDTO.setMdn(xdmRequestDTO.getFromMdn());
        KnXDMTalkGroupServerRespDTO subscriberScanListForSource = corpClientIntf.getSubscriberScanList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug(methodName, "subscriberScanListForSource", subscriberScanListForSource);

        Map<Integer, List<KnXDMTalkGroupInfoDTO>> sourceXDMTalkGroupInfo = new HashMap<>();
        List<KnXDMTalkGroupInfoDTO> scanListForSource = subscriberScanListForSource.getCampGrpList();
        if (null != scanListForSource && !scanListForSource.isEmpty()) {
            sourceXDMTalkGroupInfo.putAll(
                    scanListForSource.stream()
                            .filter(campGrp -> !Objects.equals(campGrp.getPriority(), com.kodiak.xdms.server.common.resources.KnConstants.NO_PRIORITY))
                            .collect(Collectors.groupingBy(KnXDMTalkGroupInfoDTO::getGroupId))
            );
            knLogger.debug(methodName, "sourceXDMTalkGroupInfo", sourceXDMTalkGroupInfo);
        }

        ipTalkGroupDTO.setMdn(xdmRequestDTO.getToMdn());
        KnXDMTalkGroupServerRespDTO subscriberScanListForTarget = corpClientIntf.getSubscriberScanList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug(methodName, "subscriberScanListForTarget", subscriberScanListForTarget);
        Map<Integer, List<KnXDMTalkGroupInfoDTO>> targetXDMTalkGroupInfo = new HashMap<>();
        List<KnXDMTalkGroupInfoDTO> scanListForTarget = subscriberScanListForTarget.getCampGrpList();
        if (null != scanListForTarget && !scanListForTarget.isEmpty()) {
            targetXDMTalkGroupInfo.putAll(
                    scanListForTarget.stream()
                            .filter(campGrp -> !Objects.equals(campGrp.getPriority(), com.kodiak.xdms.server.common.resources.KnConstants.NO_PRIORITY))
                            .collect(Collectors.groupingBy(KnXDMTalkGroupInfoDTO::getGroupId))
            );
            knLogger.debug(methodName, "targetXDMTalkGroupInfo", targetXDMTalkGroupInfo);
        }

        List<KnXDMTalkGroupInfoDTO> modifiedScanList = new ArrayList<>();
        List<KnXDMTalkGroupInfoDTO> addedScanList = new ArrayList<>();

        if (!sourceXDMTalkGroupInfo.isEmpty()) {
            knLogger.debug(methodName, "Common elements between two maps");
            sourceXDMTalkGroupInfo.entrySet().stream().filter(e -> targetXDMTalkGroupInfo.containsKey(e.getKey())).forEach(e -> {
                List<KnXDMTalkGroupInfoDTO> value = e.getValue();
                value.forEach(val -> {
                    KnXDMTalkGroupInfoDTO xdmTalkGroupInfoDTO = new KnXDMTalkGroupInfoDTO();
                    xdmTalkGroupInfoDTO.setGroupId(e.getKey());
                    xdmTalkGroupInfoDTO.setGroupName(val.getGroupName());
                    xdmTalkGroupInfoDTO.setPriority(val.getPriority());
                    xdmTalkGroupInfoDTO.setChannel(val.getChannel());
                    xdmTalkGroupInfoDTO.setZone(val.getZone());
                    xdmTalkGroupInfoDTO.setCsvZone(val.getCsvZone());
                    xdmTalkGroupInfoDTO.setCsvChannel(val.getCsvChannel());
                    xdmTalkGroupInfoDTO.setCorpId(val.getCorpId());
                    modifiedScanList.add(xdmTalkGroupInfoDTO);
                });
            });
            knLogger.debug(methodName, "modifiedScanList", modifiedScanList);

            knLogger.debug(methodName, "Elements only in source but absent in target");
            List<Integer> groupIds = new ArrayList<>(sourceXDMTalkGroupInfo.keySet());
            Map<Integer, KnCorpGroupDTO> groupDetailsMap2 = corpClientIntf.getGroupDetailsMap(groupIds, KnDbUtil.getDBConfigInfo().getLocalPttId(), persisterTxn);
            sourceXDMTalkGroupInfo.entrySet().stream().filter(e -> !targetXDMTalkGroupInfo.containsKey(e.getKey())).forEach(e -> {
                List<KnXDMTalkGroupInfoDTO> value = e.getValue();
                value.forEach(val -> {
                    KnXDMTalkGroupInfoDTO xdmTalkGroupInfoDTO = new KnXDMTalkGroupInfoDTO();
                    xdmTalkGroupInfoDTO.setGroupId(e.getKey());
                    xdmTalkGroupInfoDTO.setGroupName(val.getGroupName());
                    xdmTalkGroupInfoDTO.setPriority(val.getPriority());
                    xdmTalkGroupInfoDTO.setChannel(val.getChannel());
                    xdmTalkGroupInfoDTO.setZone(val.getZone());
                    xdmTalkGroupInfoDTO.setCsvZone(val.getCsvZone());
                    xdmTalkGroupInfoDTO.setCsvChannel(val.getCsvChannel());
                    xdmTalkGroupInfoDTO.setCorpId(val.getCorpId());
                    if (!groupDetailsMap2.containsKey(e.getKey()) || groupDetailsMap2.get(e.getKey()).getGroupType() != 3) {
                        addedScanList.add(xdmTalkGroupInfoDTO);
                    }
                });
            });
            knLogger.debug(methodName, "addedScanList", addedScanList);
        }
        knLogger.debug(methodName, "Elements only in target but absent in source");
        List<KnXDMTalkGroupInfoDTO> removedScanList = new ArrayList<>();
        targetXDMTalkGroupInfo.entrySet().stream().filter(e -> !sourceXDMTalkGroupInfo.containsKey(e.getKey())).forEach(e -> {
            List<KnXDMTalkGroupInfoDTO> value = e.getValue();
            value.forEach(val -> {
                KnXDMTalkGroupInfoDTO xdmTalkGroupInfoDTO = new KnXDMTalkGroupInfoDTO();
                xdmTalkGroupInfoDTO.setGroupId(e.getKey());
                xdmTalkGroupInfoDTO.setGroupName(val.getGroupName());
                xdmTalkGroupInfoDTO.setPriority(val.getPriority());
                xdmTalkGroupInfoDTO.setChannel(val.getChannel());
                xdmTalkGroupInfoDTO.setZone(val.getZone());
                xdmTalkGroupInfoDTO.setCsvZone(val.getCsvZone());
                xdmTalkGroupInfoDTO.setCsvChannel(val.getCsvChannel());
                xdmTalkGroupInfoDTO.setCorpId(val.getCorpId());
                removedScanList.add(xdmTalkGroupInfoDTO);
            });
        });
        knLogger.debug(methodName, "removedScanList", removedScanList);

        //KnIPTalkGroupDTO modifySubscriberScanListRequest = new KnIPTalkGroupDTO();
        modifySubscriberScanListRequest.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        modifySubscriberScanListRequest.setCorpId(xdmRequestDTO.getCorpId());
        modifySubscriberScanListRequest.setMdn(xdmRequestDTO.getToMdn());
        modifySubscriberScanListRequest.setMcPttId(xdmRequestDTO.getMcPttId());
        modifySubscriberScanListRequest.setAddedCampGrpList(addedScanList);
        modifySubscriberScanListRequest.setModifiedCampGrpList(modifiedScanList);
        modifySubscriberScanListRequest.setRemovedCammpGrpList(removedScanList);
        modifySubscriberScanListRequest.setMode(subscriberScanListForSource.getMode());
        modifySubscriberScanListRequest.setEtag("-9999");
        modifySubscriberScanListRequest.setClientType(xdmRequestDTO.getClientType());
        modifySubscriberScanListRequest.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "finalSubscriberScanListRequest", modifySubscriberScanListRequest);
    }


    /**
     * This method is used to clone bulk groups to a subscriber.
     * It takes a KnCorpSubsResquestDTO object and a KnPersisterTxn object as parameters.
     *
     * @param subsResquestDTO An instance of KnCorpSubsResquestDTO containing the details required for the cloning operation.
     *                        The DTO should contain the details of the source subscriber (whose groups are to be cloned)
     *                        and the target subscriber (who will receive the cloned groups).
     * @param persisterTxn    An instance of KnPersisterTxn for managing the database transaction.
     * @return An instance of KnCorpResponseDTO containing the response of the operation.
     * If the operation is successful, the response DTO will contain the details of the updated target subscriber.
     * If the operation fails, the response DTO will contain an error message.
     * @throws KnXDMServerException If there is an error during the operation, a KnXDMServerException is thrown.
     */
    public KnBulkGroupCloningDTO cloneBulkGroupsToSubscriberDataPrepration(KnCorpSubsResquestDTO subsResquestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "cloneBulkGroupsToSubscriberDataPrepration(IXDMRequestDTO, KnPersisterTxn)";
        KnBulkGroupCloningDTO bulkGroupCloningDTO;
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setToMdn(subsResquestDTO.getToMdn());
        ipSubscriberInfoDTO.setFromMdn(subsResquestDTO.getFromMdn());
        ipSubscriberInfoDTO.setGroupIds(subsResquestDTO.getGroupIds());
        ipSubscriberInfoDTO.setCloningBitset(subsResquestDTO.getCloningBitset());
        knLogger.debug(methodName, "Calling corp library for cloneBulkGroupsToSubscriberDataPrepration", ipSubscriberInfoDTO);
        bulkGroupCloningDTO = corpClientIntf.cloneBulkGroupsToSubscriberDataPrepration(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "bulkGroupCloningDTO : ", bulkGroupCloningDTO);
        return bulkGroupCloningDTO;
    }

    public KnCorpResponseDTO cloneBulkGroupsToSubscriberProcessing(KnBulkGroupCloningDTO bulkGroupCloningDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "cloneBulkGroupsToSubscriberDataPrepration(IXDMRequestDTO, KnPersisterTxn)";
        KnCorpResponseDTO responseDTO;
        responseDTO = corpClientIntf.cloneBulkGroupsToSubscriberProcessing(bulkGroupCloningDTO, persisterTxn);
        knLogger.debug(methodName, "responseDTO- ", responseDTO);
        return responseDTO;
    }

    public KnCorpResponseDTO createSubsATGScanList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "createSubsATGScanList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMAddlTalkGroupRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMAddlTalkGroupRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMAddlTalkGroupRequestDTO addlTalkGrpDTO = (KnXDMAddlTalkGroupRequestDTO) requestDTO;
        KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
        ipTalkGroupDTO.setCustomParamMap(addlTalkGrpDTO.getCustomParamMap());
        ipTalkGroupDTO.setCorpId(addlTalkGrpDTO.getCorpId());
        ipTalkGroupDTO.setMdn(addlTalkGrpDTO.getMdn());
        if (addlTalkGrpDTO.getAddedAddlTgList() != null) {
            Collection<KnCorpAddlTGInfoDTO> addedAddlTgList = new ArrayList<>();
            Collection<KnXDMAddlTalkGroupInfoDTO> addlTalkGroupInfoDTOS = addlTalkGrpDTO.getAddedAddlTgList();
            addlTalkGroupInfoDTOS.forEach(addTG -> {
                KnCorpAddlTGInfoDTO addlTGInfoDTO = new KnCorpAddlTGInfoDTO();
                addlTGInfoDTO.setMdn(addlTalkGrpDTO.getMdn());
                addlTGInfoDTO.setGroupId(addTG.getGroupId());
                addlTGInfoDTO.setZoneId(addTG.getZoneId());
                addlTGInfoDTO.setZoneName(addTG.getZoneName());
                addlTGInfoDTO.setChannelId(addTG.getChannelId());
                addlTGInfoDTO.setPriority(addTG.getPriority());
                addedAddlTgList.add(addlTGInfoDTO);
            });
            ipTalkGroupDTO.setAddedAddlTgList(addedAddlTgList);
        }
        ipTalkGroupDTO.setClientType(addlTalkGrpDTO.getClientType());
        ipTalkGroupDTO.setHierarchyType(addlTalkGrpDTO.getHierarchyType());
        ipTalkGroupDTO.setMode(addlTalkGrpDTO.getMode());
        KnCorpResponseDTO respDto = null;
        knLogger.debug(methodName, "Calling corp library for createSubsATGScanList");
        respDto = corpClientIntf.createSubsATGScanList(ipTalkGroupDTO, persisterTxn);
        knLogger.debug(methodName, "Returned from corporate library");
        return respDto;
    }

    public KnCorpBulkGroupJobRespDTO contactPairingForBulkGroupProcess(IXDMRequestDTO groupRequestDto, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "contactPairingForBulkGroupProcess(IXDMRequestDTO, KnPersisterTxn)";
        if (!(groupRequestDto instanceof KnCorpDispMemReqDTO)) {
            knLogger.error(methodName, "groupRequestDto not of type KnXDMCorpGroupInfoRequestDTO - requestDTO - ",
                    groupRequestDto.getClass());
            //todo throw back exception - incompatible types
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpDispMemReqDTO xdmRequestDto = (KnCorpDispMemReqDTO) groupRequestDto;
        KnIPCorpDispatchGrpMemInfoDto groupDGMInfoDTO = new KnIPCorpDispatchGrpMemInfoDto();
        groupDGMInfoDTO.setCorpId(xdmRequestDto.getCorpId());
        groupDGMInfoDTO.setToMdn(xdmRequestDto.getToMdn());
        groupDGMInfoDTO.setGrpIds(xdmRequestDto.getGrpIds());
        knLogger.debug(methodName, "contactPairingForBulkGroupProcess  - ", groupDGMInfoDTO);
        KnCorpBulkGroupJobRespDTO respDto = corpClientIntf.contactPairingForBulkGroupProcess(groupDGMInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Returning from Get non dispatch group members - ", respDto);
        return respDto;
    }

    public Long calculateConfigFeatureBit(int corpid, KnPersisterTxn persisterTxn) throws Exception {
        Long bitsLongVal = corpClientIntf.calculateFeatureBit(corpid, persisterTxn);
        return bitsLongVal;
    }

    public KnXDMGroupUsageListResponseDTO getGroupUsageListDoc(KnXDMAuthListRequestDTO authListRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getGroupUsageListDoc(KnXDMAuthListRequestDTO, KnPersisterTxn)";
        KnXDMGroupUsageListResponseDTO respDto = new KnXDMGroupUsageListResponseDTO();
        knLogger.info(methodName, "input dto ", authListRequestDTO);
        KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
        ipTalkGroupDTO.setMdn(authListRequestDTO.getAuthMdn());
        ipTalkGroupDTO.setHierarchyType(authListRequestDTO.getHierarchyType());
        ipTalkGroupDTO.setIfMatch(authListRequestDTO.getIfMatch());
        ipTalkGroupDTO.setIfNoneMatch(authListRequestDTO.getIfNoneMatch());
        KnXDMTalkGroupServerRespDTO groupUsageListDoc = corpClientIntf.getSubsTGList(ipTalkGroupDTO, persisterTxn);
        knLogger.info(methodName, "Returned from public  library ", groupUsageListDoc);
        List<KnGroupUsageDTO> entryDTOS = new ArrayList<>();
        if (groupUsageListDoc.getAddlTGList() != null) {
            for (KnCorpAddlTGInfoDTO entryDTO : groupUsageListDoc.getAddlTGList()) {
                KnGroupUsageDTO entry = new KnGroupUsageDTO();
                if (entryDTO.getChannelId() != null) entry.setChannelId(entryDTO.getChannelId());
                if (entryDTO.getZoneId() != null) entry.setZoneId(entryDTO.getZoneId());
                entry.setZoneName(entryDTO.getZoneName());
                entry.setGroupId(entryDTO.getGroupId());
                entryDTOS.add(entry);
            }
        }
        respDto.setCorpId(groupUsageListDoc.getMdnCorpId());
        respDto.setList(entryDTOS);
        respDto.setDocEtag(groupUsageListDoc.getEtag());
        knLogger.info(methodName, "response from pub mediator", respDto);
        return respDto;
    }

    public KnXDMCorpClientActResponseDTO getTempPwdForLegacy(KnIPCorpActivationDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getTempPwdForLegacy(IXDMRequestDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Calling getTempPwdForLegacy - ", requestDTO);
        KnCorpActivationRespDTO respDto = corpClientIntf.getTempPwdForLegacy(requestDTO, persisterTxn);
        knLogger.debug(methodName, "After getTempPwdForLegacy - ", respDto);
        KnXDMCorpClientActResponseDTO xdmRespDto = new KnXDMCorpClientActResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            xdmRespDto.setResponseMap(respDto.getResponseMap());
            return xdmRespDto;
        }
        String actCode = respDto.getActivationCode();
        knLogger.debug(methodName, "actCode - ", actCode);
        xdmRespDto.setActivationCode(actCode);
        xdmRespDto.setExpiryTimestamp(respDto.getExpiryTimestamp());
        knLogger.debug(methodName, "Returning xdmRespDto - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpGroupListRespDTO getCorpSubsGroupList(IXDMRequestDTO subscriberCorpInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getCorpSubsGroupList(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(subscriberCorpInfo instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    subscriberCorpInfo.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDto = (KnXDMCorpSubscInfoRequestDTO) subscriberCorpInfo;
        KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
        KnCorpGroupListRespDTO respDto = null;
        contactDTO.setCorpId(Integer.parseInt(xdmRequestDto.getCorpId()));
        contactDTO.setMdn(xdmRequestDto.getSubscriberMdn());
        contactDTO.setMcpttId(xdmRequestDto.getMcpttId());
        contactDTO.setClientType(xdmRequestDto.getClientType());
        contactDTO.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        contactDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
        knLogger.debug(methodName, "Get CorpSubs Group List - ", contactDTO);
        respDto = corpClientIntf.getSubsGroupList(contactDTO, persisterTxn);
        KnXDMCorpGroupListRespDTO xdmRespDto = new KnXDMCorpGroupListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnXDMCorpGroupDTO> xdmGroupList = new ArrayList<KnXDMCorpGroupDTO>();
        Collection<KnCorpGroupInfoDTO> groupList = respDto.getGroupList();
        if (groupList != null) {
            for (KnCorpGroupInfoDTO group : groupList) {
                KnXDMCorpGroupDTO xdmGroupdto = new KnXDMCorpGroupDTO();
                xdmGroupdto.setCorpId(String.valueOf(group.getCorpId()));
                xdmGroupdto.setGroupId(String.valueOf(group.getGroupId()));
                xdmGroupdto.setETag(String.valueOf(group.getETag()));
                xdmGroupdto.setGroupName(group.getGroupDisplayName());
                xdmGroupdto.setMaxGroupMemberLimitFlag(group.getMaxGroupMemLimitFlag());
                xdmGroupdto.setMemberCount(group.getGroupMemberCount());
                xdmGroupdto.setGroupType(group.getGroupType());
                xdmGroupdto.setAvatar(group.getAvatar());
                xdmGroupdto.setVideoPermission(group.getVideoPermission());
                xdmGroupdto.setCamped(group.isCamped());
                xdmGroupdto.setOverrideDND(group.getOverrideDnd());
                xdmGroupdto.setLargeGrp(group.isLargeGroup());
                Collection<KnCorpGroupMemberDTO> subscList = group.getGroupSupervisor();
                Collection<KnXDMCorpContactDTO> supervisorList = new ArrayList<KnXDMCorpContactDTO>();
                if (subscList != null) {
                    for (KnCorpGroupMemberDTO memberDTO : subscList) {
                        KnXDMCorpContactDTO contactdto = new KnXDMCorpContactDTO();
                        contactdto.setMdn(memberDTO.getMdn());
                        contactdto.setName(memberDTO.getName());
                        contactdto.setBroadcaster(memberDTO.getBroadcaster());
                        contactdto.setSupervisor(memberDTO.getSupervisory());
                        contactdto.setLocWatcher(memberDTO.getLocWatcher());
                        contactdto.setClientType(memberDTO.getClientType());
                        contactdto.setCallInitiatePermission(memberDTO.getCallInitiatePermission());
                        contactdto.setCallReceivePermission(memberDTO.getCallReceivePermission());
                        contactdto.setInCallPermission(memberDTO.getInCallPermission());
                        supervisorList.add(contactdto);
                    }
                }
                xdmGroupdto.setSupervisorList(supervisorList);
                xdmGroupList.add(xdmGroupdto);
            }
        }
        xdmRespDto.setGroupList(xdmGroupList);
        knLogger.debug(methodName, "After the call to get corp Group Details. Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO createOSMList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "createOSMList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpOSMRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpOSMRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpOSMRequestDTO corpOSMRequestDTO = (KnXDMCorpOSMRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", corpOSMRequestDTO);
        KnIPOsmDTO ipOsmDTO = new KnIPOsmDTO();

        ipOsmDTO.setHierarchyType(corpOSMRequestDTO.getHierarchyType());
        ipOsmDTO.setCustomParamMap(corpOSMRequestDTO.getCustomParamMap());
        ipOsmDTO.setCorpId(corpOSMRequestDTO.getCorpId());
        ipOsmDTO.setIsDefault(corpOSMRequestDTO.getIsDefault());
        ipOsmDTO.setAddedOSMMsgList(corpOSMRequestDTO.getAddedOSMMsgList());
        ipOsmDTO.setOSMListName(corpOSMRequestDTO.getOSMListName());
        ipOsmDTO.setHierarchyId(corpOSMRequestDTO.getHierarchyId());

        KnCorpResponseDTO respDto = null;
        respDto = corpClientIntf.createOSMList(ipOsmDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO updateOSMList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateOSMList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpOSMRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpOSMRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpOSMRequestDTO corpOSMRequestDTO = (KnXDMCorpOSMRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", corpOSMRequestDTO);
        KnIPOsmDTO ipOsmDTO = new KnIPOsmDTO();

        ipOsmDTO.setHierarchyType(corpOSMRequestDTO.getHierarchyType());
        ipOsmDTO.setCustomParamMap(corpOSMRequestDTO.getCustomParamMap());
        ipOsmDTO.setAddedOSMMsgList(corpOSMRequestDTO.getAddedOSMMsgList());
        ipOsmDTO.setModifiedOSMMsgList(corpOSMRequestDTO.getModifiedOSMMsgList());
        ipOsmDTO.setRemovedOSMMsgList(corpOSMRequestDTO.getRemovedOSMMsgList());
        ipOsmDTO.setOSMListName(corpOSMRequestDTO.getOSMListName());
        ipOsmDTO.setOSMListId(corpOSMRequestDTO.getOSMListId());
        ipOsmDTO.setIsDefault(corpOSMRequestDTO.getIsDefault());
        ipOsmDTO.setCorpId(corpOSMRequestDTO.getCorpId());
        ipOsmDTO.setHierarchyId(corpOSMRequestDTO.getHierarchyId());

        KnCorpResponseDTO respDto = null;
        respDto = corpClientIntf.updateOSMList(ipOsmDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO deleteOSMList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteOSMList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpOSMRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpOSMRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpOSMRequestDTO corpOSMRequestDTO = (KnXDMCorpOSMRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", corpOSMRequestDTO);
        KnIPOsmDTO ipOsmDTO = new KnIPOsmDTO();

        ipOsmDTO.setHierarchyType(corpOSMRequestDTO.getHierarchyType());
        ipOsmDTO.setCustomParamMap(corpOSMRequestDTO.getCustomParamMap());
        ipOsmDTO.setCorpId(corpOSMRequestDTO.getCorpId());
        ipOsmDTO.setOSMListId(corpOSMRequestDTO.getOSMListId());

        KnCorpResponseDTO respDto = null;
        respDto = corpClientIntf.deleteOSMList(ipOsmDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpOSMInfoListRespDTO getOSMList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getOSMList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpOSMRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpOSMRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpOSMRequestDTO corpOSMRequestDTO = (KnXDMCorpOSMRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", corpOSMRequestDTO);
        KnIPOsmDTO ipOsmDTO = new KnIPOsmDTO();

        ipOsmDTO.setHierarchyType(corpOSMRequestDTO.getHierarchyType());
        ipOsmDTO.setCustomParamMap(corpOSMRequestDTO.getCustomParamMap());
        ipOsmDTO.setCorpId(corpOSMRequestDTO.getCorpId());
        ipOsmDTO.setHierarchyId(corpOSMRequestDTO.getHierarchyId());

        KnCorpOSMInfoListRespDTO response = corpClientIntf.getOSMList(ipOsmDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", response);
        return response;
    }

    public KnCorpOSMInfoListDetailsRespDTO getOSMListDetails(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getOSMListDetails(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpOSMRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpOSMRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpOSMRequestDTO corpOSMRequestDTO = (KnXDMCorpOSMRequestDTO) requestDTO;
        KnIPOsmDTO ipOsmDTO = new KnIPOsmDTO();

        knLogger.debug(methodName, "Entry ", corpOSMRequestDTO);
        ipOsmDTO.setHierarchyType(corpOSMRequestDTO.getHierarchyType());
        ipOsmDTO.setCustomParamMap(corpOSMRequestDTO.getCustomParamMap());
        ipOsmDTO.setCorpId(corpOSMRequestDTO.getCorpId());
        ipOsmDTO.setOSMListId(corpOSMRequestDTO.getOSMListId());

        KnCorpOSMInfoListDetailsRespDTO daoResponse = corpClientIntf.getOSMListDetails(ipOsmDTO, persisterTxn);

        knLogger.debug(methodName, "Exit ", daoResponse);
        return daoResponse;
    }

    public KnCorpResponseDTO assignOSMIdToGroup(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "assignOSMIdToGroup(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpOSMRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpOSMRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpOSMRequestDTO corpOSMRequestDTO = (KnXDMCorpOSMRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", corpOSMRequestDTO);
        KnIPOsmDTO ipOsmDTO = new KnIPOsmDTO();

        ipOsmDTO.setHierarchyType(corpOSMRequestDTO.getHierarchyType());
        ipOsmDTO.setCustomParamMap(corpOSMRequestDTO.getCustomParamMap());

        ipOsmDTO.setCorpId(corpOSMRequestDTO.getCorpId());
        ipOsmDTO.setOSMListId(corpOSMRequestDTO.getOSMListId());
        ipOsmDTO.setAssignedOSMIdToGroupIds(corpOSMRequestDTO.getAssignedOSMIdToGroupIds());
        ipOsmDTO.setRemovedOSMIdFromGroupIds(corpOSMRequestDTO.getRemovedOSMIdFromGroupIds());

        KnCorpResponseDTO respDto = corpClientIntf.assignOSMIdToGroup(ipOsmDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpOSMGroupListRespDTO getOSMGroupList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getOSMGroupList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpOSMRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpOSMRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpOSMRequestDTO corpOSMRequestDTO = (KnXDMCorpOSMRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", corpOSMRequestDTO);
        KnIPOsmDTO ipOsmDTO = new KnIPOsmDTO();

        ipOsmDTO.setHierarchyType(corpOSMRequestDTO.getHierarchyType());
        ipOsmDTO.setCustomParamMap(corpOSMRequestDTO.getCustomParamMap());

        ipOsmDTO.setCorpId(corpOSMRequestDTO.getCorpId());
        ipOsmDTO.setOSMListIds(corpOSMRequestDTO.getOSMListIds());

        KnCorpOSMGroupListRespDTO respDto = corpClientIntf.getOSMGroupList(ipOsmDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnXDMCorpProfileInfoRespDTO getCorpProfileByEntities(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getCorpProfileByEntities()";
        if (!(requestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) requestDTO;
        KnIPCorpAuthInfoDTO corpInfoDto = new KnIPCorpAuthInfoDTO();
        corpInfoDto.setExtCorpId(xdmRequestDTO.getExtCorpId());
        corpInfoDto.setCustomParamMap(xdmRequestDTO.getCustomParamMap());

        if (xdmRequestDTO.getExtCorpId() == null || xdmRequestDTO.getExtCorpId().isEmpty()) {
            corpInfoDto.setHierarchyType(HIERARCHY_TYPE.HIERARCHY); //hierarchy
        } else {
            corpInfoDto.setHierarchyType(HIERARCHY_TYPE.NON_HIERARCHY); //non_hierarchy
        }

        KnCorpAuthInfoRespDTO respDto = corpClientIntf.getCorpProfileByEntities(corpInfoDto, persisterTxn);
        knLogger.debug(methodName, "Retrieved CorpProfie - ", respDto);
        KnXDMCorpProfileInfoRespDTO xdmRespDto = new KnXDMCorpProfileInfoRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        knLogger.debug(methodName, "respDto.getCustomParamMap() - ", respDto.getCustomParamMap());
        KnCorpProfileInfoDTO corpProfile = respDto.getProfileInfoDTO();
        if (corpProfile != null) {
            xdmRespDto.setCorpId(corpProfile.getCorpId());
            xdmRespDto.setExtCorpId(corpProfile.getExtCorpId());
        }
        knLogger.debug(methodName, "Returning CorpProfile - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO updateCorpSubscriberMCSIds(IXDMRequestDTO requestDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateCorpSubscriberMCSIds(IXDMRequestDTO, boolean, KnPersisterTxn)";
        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnCorpSubsResquestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        KnCorpSubsResquestDTO subsResquestDTO = (KnCorpSubsResquestDTO) requestDTO;
        ipSubscriberInfoDTO.setMdn(subsResquestDTO.getSubscriberMdn());
        ipSubscriberInfoDTO.setAliasMdn(subsResquestDTO.getAliasMdn());
        ipSubscriberInfoDTO.setUserId(subsResquestDTO.getUserId());
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setHierarchyType(subsResquestDTO.getHierarchyType());
        ipSubscriberInfoDTO.setCustomParamMap(subsResquestDTO.getCustomParamMap());
        knLogger.debug(methodName, "Calling corp library for modify Subsc name");
        KnCorpResponseDTO respDto = corpClientIntf.getCorpSubscriberDetails(ipSubscriberInfoDTO, readOnly, persisterTxn);
        knLogger.debug(methodName, "Returned from corporate library");
        KnXDMCorpRespDTO responseDTO = new KnXDMCorpRespDTO();
        populateXdmResponse(responseDTO, respDto);
        return respDto;
    }

    public KnPoCConfigInfoRespDTO getPoCConfig(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getPoCConfig(KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) requestDTO;
        KnIPCorpInfoDTO corpInfoDTO = new KnIPCorpInfoDTO();
        corpInfoDTO.setPttServerIds(xdmRequestDTO.getPttServerIds());
        KnCorpPoCSvcConfigRespDTO respDto = corpClientIntf.getPoCConfig(corpInfoDTO, persisterTxn);
        KnPoCConfigInfoRespDTO xdmRespDto = new KnPoCConfigInfoRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        Collection<KnSIPProxySvcConfigDTO> sipProxySvcConfig = respDto.getSipProxySvcConfig();
        Collection<KnAPNConfigDTO> apnConfigDTOS = respDto.getApnConfig();
        xdmRespDto.setSipProxySvcConfig(sipProxySvcConfig != null ? sipProxySvcConfig.stream().map(sipProxy ->
                new KnSipProxySvcConfigDTO(sipProxy.getPttServerId(), sipProxy.getSipProxyURI(), sipProxy.getSipProxyUriWifi(),
                        sipProxy.getGeoSipProxyUri(), sipProxy.getGeoSipProxyUriWifi())).collect(Collectors.toList()) : null);
        xdmRespDto.setApnProfileConfig(apnConfigDTOS != null ? apnConfigDTOS.stream().map(apnConfig ->
                        new KnAPNProfileConfigDTO(apnConfig.getApnId(), apnConfig.getApnName(), apnConfig.getIsDefault(),
                                apnConfig.getPttServerId(), apnConfig.getSipProxyUri(), apnConfig.getGeoSipProxyUri(),
                                apnConfig.getGeoRegPrimF5Uri(), apnConfig.getGeoRegGeoF5Uri(), apnConfig.getFdServiceUriCell()))
                .collect(Collectors.toList()) : null);
        knLogger.debug(methodName, "After Retrieval of PoCConfig profile. Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpGroupListRespDTO getMobileSyncLocSupervisors(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMobileSyncLocSupervisors(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnXDMSubsInfoDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMSubsInfoDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMSubsInfoDTO xdmRequestDTO = (KnXDMSubsInfoDTO) requestDTO;
        KnIPSubscriberInfoDTO contactDTO = new KnIPSubscriberInfoDTO();
        contactDTO.setMdnList(xdmRequestDTO.getMdnList());
        contactDTO.setClientType(xdmRequestDTO.getClientType());
        knLogger.debug(methodName, "Get CorpSubs Group List - ", contactDTO);
        KnCorpGroupListRespDTO respDto = corpClientIntf.getMobileSyncLocSupervisors(contactDTO, persisterTxn);
        KnXDMCorpGroupListRespDTO xdmRespDto = new KnXDMCorpGroupListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Map<Integer, List<KnXDMCorpContactDTO>> xdmGroupList = new HashMap<>();
        Map<Integer, List<KnCorpGroupMemberDTO>> groupList = respDto.getGroupMembers();
        Map<String, Collection<Integer>> fleetMemGroupIds = respDto.getFleetMemGroupIds();
        if (groupList != null) {
            groupList.forEach((grpId, grpMems) -> {
                if (grpMems != null) {
                    List<KnXDMCorpContactDTO> grpMemDtos = new ArrayList<>();
                    grpMems.forEach(mem -> {
                        KnXDMCorpContactDTO corpContactDTO = new KnXDMCorpContactDTO();
                        corpContactDTO.setMdn(mem.getMdn());
                        corpContactDTO.setActiveFs2(mem.getSubsActiveFS2());
                        grpMemDtos.add(corpContactDTO);
                    });
                    xdmGroupList.put(grpId, grpMemDtos);
                }
            });
        }
        xdmRespDto.setFleetMemsGroups(fleetMemGroupIds);
        xdmRespDto.setFleetMemList(xdmGroupList);
        knLogger.debug(methodName, "After the call to get mobile sync locWatcher. Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO createUserProfile(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "createUserProfile(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpUserProfileRequestDTO userProfileRequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", userProfileRequestDTO);
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
        ipUserProfileDTO.setHierarchyType(userProfileRequestDTO.getHierarchyType());
        ipUserProfileDTO.setCustomParamMap(userProfileRequestDTO.getCustomParamMap());
        ipUserProfileDTO.setCorpId(userProfileRequestDTO.getCorpId());
        ipUserProfileDTO.setOwnerIdList(userProfileRequestDTO.getOwnerIdList());
        ipUserProfileDTO.setSharingEnabled(userProfileRequestDTO.getSharingEnabled());
        ipUserProfileDTO.setUserProfileSharedCorpList(userProfileRequestDTO.getUserProfileSharedCorpList());

        Function<KnXDMUserProfileInfoDTO, KnCorpUserProfileDTO> transformUPM = new Function<KnXDMUserProfileInfoDTO, KnCorpUserProfileDTO>() {
            public KnCorpUserProfileDTO apply(KnXDMUserProfileInfoDTO type) {
                KnCorpUserProfileDTO upm = new KnCorpUserProfileDTO();
                KnSubsEmergencyConfigDTO emergencyConfig = new KnSubsEmergencyConfigDTO();
                Set<KnDestinationAttributeDTO> destAttributes = new HashSet<>();
                Set<KnCorpGroupListInfoDTO> groupList = new HashSet<>();
                upm.setCorporateID(Integer.valueOf(userProfileRequestDTO.getCorpId()));
                upm.setUserProfileName(type.getProfileName());
                if (type.getTgscMode() != null) {
                    upm.setTgscMode(type.getTgscMode());
                }
                if (type.getContactListId() != null)
                    upm.setContactListID(Integer.valueOf(type.getContactListId()));

                KnXDMEmergencyConfig emergencyConfigAttr = type.getEmergencyAttributes();
                if (emergencyConfigAttr != null) {
                    if (emergencyConfigAttr.getEmergCallType() != null) {
                        emergencyConfig.setCallType(Integer.valueOf(emergencyConfigAttr.getEmergCallType()));
                    }
                    if (emergencyConfigAttr.getEmergCancelPermission() != null) {
                        emergencyConfig.setCancelPermission(Integer.valueOf(emergencyConfigAttr.getEmergCancelPermission()));
                    }
                    if (emergencyConfigAttr.getEmergDestType() != null) {
                        emergencyConfig.setDestType(Integer.valueOf(emergencyConfigAttr.getEmergDestType()));
                    }
                    if (emergencyConfigAttr.getEmergLMRBehavior() != null) {
                        emergencyConfig.setLmrBehavior(Integer.valueOf(emergencyConfigAttr.getEmergLMRBehavior()));
                    }
                    if (emergencyConfigAttr.getEmergOriginBitSet() != null) {
                        emergencyConfig.setOrigBitset(Integer.valueOf(emergencyConfigAttr.getEmergOriginBitSet()));
                    }
                    if (emergencyConfigAttr.getEmergInitPermission() != null) {
                        emergencyConfig.setPermission(Integer.valueOf(emergencyConfigAttr.getEmergInitPermission()));
                    }
                    if (emergencyConfigAttr.getEmergTermBitSet() != null) {
                        emergencyConfig.setTermBitset(Integer.valueOf(emergencyConfigAttr.getEmergTermBitSet()));
                    }
                    if (emergencyConfigAttr.getEmergDestAttributes() != null) {
                        for (KnXDMEmergencyDestAttributes destAttr : emergencyConfigAttr.getEmergDestAttributes()) {
                            KnDestinationAttributeDTO destAttrbute = new KnDestinationAttributeDTO();
                            if (destAttr.getDestAttributeType() != null)
                                destAttrbute.setDestType(Integer.valueOf(destAttr.getDestAttributeType()));
                            if (destAttr.getDestAttributeCategory() != null)
                                destAttrbute.setDestCat(Integer.valueOf(destAttr.getDestAttributeCategory()));
                            if (destAttr.getDestAttributeUri() != null)
                                destAttrbute.setDestURI(destAttr.getDestAttributeUri());
                            destAttributes.add(destAttrbute);
                        }
                        emergencyConfig.setDestAttributes(destAttributes);
                    }
                    if (null != emergencyConfigAttr.getEmergConfigTimer()) {
                        emergencyConfig.setEmergConfigTimer(emergencyConfigAttr.getEmergConfigTimer());
                    }
                    upm.setEmergencyConfig(emergencyConfig);
                }
                Set<KnXDMGroupListInfoDTO> groupListInfo = type.getGroupListInfo();
                if (groupListInfo != null) {
                    for (KnXDMGroupListInfoDTO groups : groupListInfo) {
                        KnXDMGroupMdnInfoDTO memberPr = groups.getGroupMemProp();

                        KnCorpGroupListInfoDTO corpGroupListInfoDTO = new KnCorpGroupListInfoDTO();

                        if (groups.getGroupId() != null)
                            corpGroupListInfoDTO.setGroupID(Integer.valueOf(groups.getGroupId()));
                        if (groups.getZoneId() != null)
                            corpGroupListInfoDTO.setGroupZone(Integer.valueOf(groups.getZoneId()));
                        if (groups.getChannelId() != null)
                            corpGroupListInfoDTO.setGroupChannel(Integer.valueOf(groups.getChannelId()));
                        if (groups.getPriority() != null)
                            corpGroupListInfoDTO.setGroupPriority(Integer.valueOf(groups.getPriority()));

                        corpGroupListInfoDTO.setGrpMemProps(new KnCorpGroupContactDTO(memberPr.getSupervisor(), memberPr.getBroadcaster()
                                , memberPr.getLocWatcher(), memberPr.getIsOSMAuthorize(),
                                memberPr.getCallInitiatePermission(),
                                memberPr.getCallReceivePermission(), memberPr.getInCallPermission(),memberPr.getVideoCallInitiatePermission(),
                                memberPr.getVideoCallReceivePermission(),memberPr.getVideoInCallPermission()));


                        groupList.add(corpGroupListInfoDTO);
                    }
                    upm.setGroupList(groupList);
                }
                //upm.setCreateTimeStamp(System.currentTimeMillis());
                Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfos = type.getTargetMdnPermissionBitInfoList();
                Function<Collection<KnTargetMdnPermissionBitInfo>, Set<KnCorpUserProfileMCPTTConfig>> transformToPermBitSet
                        = new Function<Collection<KnTargetMdnPermissionBitInfo>, Set<KnCorpUserProfileMCPTTConfig>>() {
                    @Override
                    public Set<KnCorpUserProfileMCPTTConfig> apply(Collection<KnTargetMdnPermissionBitInfo> knTargetMdnPermBitInfos) {
                        Set<KnCorpUserProfileMCPTTConfig> corpUserProfileMCPTTConfigSet = new HashSet<>();
                        if (null != targetMdnPermissionBitInfos) {
                            targetMdnPermissionBitInfos.forEach(targetMdnPermissionBitInfo -> {
                                //TODO:MOVE into common method
                                BitSet bitSet = new BitSet();
                                bitSet.set(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value(), targetMdnPermissionBitInfo.getAmbientListening() == 1);
                                bitSet.set(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.DISCRETELISTENING.value(), targetMdnPermissionBitInfo.getDiscreteListening() == 1);
                                bitSet.set(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.USERCHECK.value(), targetMdnPermissionBitInfo.getUserCheck() == 1);
                                bitSet.set(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.USERENABLE.value(), targetMdnPermissionBitInfo.getUserEnable() == 1);
                                bitSet.set(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.REMOTEEMERGENCYPERMISSION.value(), targetMdnPermissionBitInfo.getEmergPermission() == 1);
                                bitSet.set(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.MCVIDEOUNCONFIRMEDPULL.value(), targetMdnPermissionBitInfo.getMcVideoUnConfirmedPull() == 1);

                                long bitSetLongVal = KnGeneralUtil.convertBitSetToLong(bitSet);

                                KnCorpUserProfileMCPTTConfig userProfileMCPTTConfig = new KnCorpUserProfileMCPTTConfig();
                                userProfileMCPTTConfig.setMdn(targetMdnPermissionBitInfo.getMdn());
                                userProfileMCPTTConfig.setPermBitSet(bitSetLongVal);
                                corpUserProfileMCPTTConfigSet.add(userProfileMCPTTConfig);

                            });

                            knLogger.debug(methodName, "userProfileMCPTTConfig-", corpUserProfileMCPTTConfigSet);

                        }

                        return corpUserProfileMCPTTConfigSet;
                    }
                };

                upm.setMcpttPermissionsConfig(transformToPermBitSet.apply(targetMdnPermissionBitInfos));
                upm.setUserProfileStatus(KnConstants.USERPROFILESTATUS.ACTIVE.Value());
                return upm;
            }
        };
        ipUserProfileDTO.setUserProfileDTO(transformUPM.apply(userProfileRequestDTO.getUserProfileInfo()));
        //seeting upmfs
        if (userProfileRequestDTO.getUserProfileInfo().getUserProfileFS() != null) {
            KnXDMUserProfileFSDTO xdmUpmFs = userProfileRequestDTO.getUserProfileInfo().getUserProfileFS();
            KnUserProfileFSDTO ipUpmFs = new KnUserProfileFSDTO();
            ipUpmFs.setAmbientListening(xdmUpmFs.getAmbientListening());
            ipUpmFs.setBrdcrmb(xdmUpmFs.getBrdcrmb());
            ipUpmFs.setDiscreteListening(xdmUpmFs.getDiscreteListening());
            ipUpmFs.setGeofnc(xdmUpmFs.getGeofnc());
            ipUpmFs.setLocPublish(xdmUpmFs.getLocPublish());
            ipUpmFs.setMcVideoConfirmedPull(xdmUpmFs.getMcVideoConfirmedPull());
            ipUpmFs.setMcVideoGroupRx(xdmUpmFs.getMcVideoGroupRx());
            ipUpmFs.setMcVideoRx(xdmUpmFs.getMcVideoRx());
            ipUpmFs.setMcVideoTx(xdmUpmFs.getMcVideoTx());
            ipUpmFs.setPtloc(xdmUpmFs.getPtloc());
            ipUpmFs.setPtmd(xdmUpmFs.getPtmd());
            ipUpmFs.setPtx(xdmUpmFs.getPtx());
            ipUpmFs.setTgsclnt(xdmUpmFs.getTgsclnt());
            ipUpmFs.setUserCheck(xdmUpmFs.getUserCheck());
            ipUpmFs.setUserEnable(xdmUpmFs.getUserEnable());
            ipUpmFs.setOsm(xdmUpmFs.getOsm());
            ipUpmFs.setEmergency(xdmUpmFs.getEmergency());
            ipUpmFs.setSelfDnDPrivilege(xdmUpmFs.getSelfDnDPrivilege());
            ipUserProfileDTO.setUserProfileFSDto(ipUpmFs);
        }

        String sharingEnable = userProfileRequestDTO.getSharingEnabled();
        boolean sharingEnableBoolean = sharingEnable == null || sharingEnable.isBlank() ?
                Boolean.FALSE : sharingEnable.trim().equalsIgnoreCase(ENABLED + "") ? Boolean.TRUE : Boolean.FALSE;
        if (ipUserProfileDTO.getUserProfileDTO() != null)
            ipUserProfileDTO.getUserProfileDTO().setSharingEnabled(sharingEnableBoolean);
        if (ipUserProfileDTO.getUserProfileDTO() != null && userProfileRequestDTO.getHierarchyId() != null)
            ipUserProfileDTO.getUserProfileDTO().setHierarchyId(userProfileRequestDTO.getHierarchyId());
        knLogger.debug(methodName, "userProfile ", ipUserProfileDTO.getUserProfileDTO());

        KnCorpResponseDTO respDto = corpClientIntf.createUserProfile(ipUserProfileDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO updateUserProfile(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateUserProfile(IXDMRequestDTO , KnPersisterTxn)";
        KnXDMCorpUserProfileRequestDTO userProfileRequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", userProfileRequestDTO);
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();

        ipUserProfileDTO.setHierarchyType(userProfileRequestDTO.getHierarchyType());
        ipUserProfileDTO.setCustomParamMap(userProfileRequestDTO.getCustomParamMap());
        ipUserProfileDTO.setCorpId(userProfileRequestDTO.getCorpId());
        ipUserProfileDTO.setProfileId(userProfileRequestDTO.getUserProfileId());
        ipUserProfileDTO.setAddedOwnerIDList(userProfileRequestDTO.getAddedOwnerIdList());
        ipUserProfileDTO.setRemovedOwnerIDList(userProfileRequestDTO.getRemovedOwnerIdList());


        KnCorpModifyUserProfileDTO modifyUpmReq = KnCorpCommonInfoUtil.modifyUpmTransform(userProfileRequestDTO);
        ipUserProfileDTO.setModifiedUserProfileDTO(modifyUpmReq);

        KnCorpResponseDTO respDto = corpClientIntf.updateUserProfile(ipUserProfileDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO deleteUserProfile(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteUserProfile(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpUserProfileRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpUserProfileRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpUserProfileRequestDTO userProfileRequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", userProfileRequestDTO);
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
        ipUserProfileDTO.setHierarchyType(userProfileRequestDTO.getHierarchyType());
        ipUserProfileDTO.setCustomParamMap(userProfileRequestDTO.getCustomParamMap());
        ipUserProfileDTO.setCorpId(userProfileRequestDTO.getCorpId());
        ipUserProfileDTO.setProfileId(userProfileRequestDTO.getUserProfileId());

        KnCorpResponseDTO respDto = corpClientIntf.deleteUserProfile(ipUserProfileDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnXDMCorpUserProfileRespDTO  getUserProfileDetails(IXDMRequestDTO requestDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getUserProfileDetails(IXDMRequestDTO, boolean, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpUserProfileRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpUserProfileRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpUserProfileRequestDTO userProfileRequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", userProfileRequestDTO);
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();

        ipUserProfileDTO.setHierarchyType(userProfileRequestDTO.getHierarchyType());
        ipUserProfileDTO.setCustomParamMap(userProfileRequestDTO.getCustomParamMap());
        ipUserProfileDTO.setCorpId(userProfileRequestDTO.getCorpId());
        ipUserProfileDTO.setProfileId(userProfileRequestDTO.getUserProfileId());
        //ipUserProfileDTO.getUserProfileDTO().setUserProfileName(userProfileRequestDTO.getUserProfileInfo().getProfileName());
        //ipUserProfileDTO.getUserProfileDTO().setContactListID(Integer.valueOf(userProfileRequestDTO.getUserProfileInfo().getContactListId()));
        //ipUserProfileDTO.getUserProfileDTO().setEmergencyAttributes(userProfileRequestDTO.getUserProfileInfo().getEmergencyAttributes());

        KnCorpResponseDTO respDto = null;
        respDto = corpClientIntf.getUserProfileDetails(ipUserProfileDTO, readOnly, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        KnXDMCorpUserProfileRespDTO xdmRespDto = new KnXDMCorpUserProfileRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        //xdmRespDto.setContactListInfo(xdmSublistInfoDTO);
        KnXDMCorpUserProfileDTO xdmUserProfile = new KnXDMCorpUserProfileDTO();
        KnCorpUserProfileDTO userProfile = respDto.getUserProfile();
        knLogger.debug(methodName, "--->userProfile - ", userProfile);
        //parsing starts
        com.kodiak.common.commdto.response.KnCorpGroupListInfoDTO upmGroupListDTO = new com.kodiak.common.commdto.response.KnCorpGroupListInfoDTO();
        Set<com.kodiak.common.commdto.response.KnCorpGroupListInfoDTO> upmGroupList = new HashSet<>();
        if (userProfile != null) {
            xdmUserProfile.setProfileName(userProfile.getUserProfileName());
            if (userProfile.getContactListID() != null) {
                xdmUserProfile.setContactListId(String.valueOf(userProfile.getContactListID()));
            }

            Collection<KnCorpGroupListInfoDTO> groupMembers = userProfile.getGroupList();
            for (var itr : userProfile.getGroupList()) {
                upmGroupListDTO.setGroupID(itr.getGroupID());
                upmGroupListDTO.setGroupZone(itr.getGroupZone());
                upmGroupListDTO.setGroupChannel(itr.getGroupChannel());
                upmGroupListDTO.setGroupPriority(itr.getGroupPriority());
                upmGroupListDTO.setGroupName(itr.getGroupName());
                upmGroupListDTO.setGroupType(itr.getGroupType());
                com.kodiak.common.commdto.response.KnCorpGroupContactDTO convertedGrpMemProps = convertToCommonDto(itr.getGrpMemProps());
                upmGroupListDTO.setGrpMemProps(convertedGrpMemProps);
                upmGroupList.add(upmGroupListDTO);
            }
            Collection<KnXDMGroupListInfoDTO> xdmGroupMembers = null;
            if (groupMembers != null) {
                xdmGroupMembers = new ArrayList<KnXDMGroupListInfoDTO>();
                for (KnCorpGroupListInfoDTO groupMember : groupMembers) {
                    KnXDMGroupListInfoDTO xdmGroupMember = new KnXDMGroupListInfoDTO();
                    if (groupMember.getGroupID() != null)
                        xdmGroupMember.setGroupId(String.valueOf(groupMember.getGroupID()));
                    if (groupMember.getGroupZone() != null)
                        xdmGroupMember.setZoneId(String.valueOf(groupMember.getGroupZone()));
                    if (groupMember.getGroupChannel() != null)
                        xdmGroupMember.setChannelId(String.valueOf(groupMember.getGroupChannel()));
                    if (groupMember.getGroupPriority() != null)
                        xdmGroupMember.setPriority(String.valueOf(groupMember.getGroupPriority()));
                    if (groupMember.getGroupName() != null)
                        xdmGroupMember.setGroupName(groupMember.getGroupName());
                    if (groupMember.getGroupType() != null)
                        xdmGroupMember.setGroupType(String.valueOf(groupMember.getGroupType()));

                    KnXDMGroupMdnInfoDTO xdmgroupmemberprops = new KnXDMGroupMdnInfoDTO();
                    KnCorpGroupContactDTO grpMemProps = groupMember.getGrpMemProps();
                    xdmgroupmemberprops.setBroadcaster(grpMemProps.getIsBroadcaster());
                    xdmgroupmemberprops.setSupervisor(grpMemProps.getIsSupervisor());
                    xdmgroupmemberprops.setCallInitiatePermission(grpMemProps.getCallInitiateAllowed());
                    xdmgroupmemberprops.setInCallPermission(grpMemProps.getIncallAllowed());
                    xdmgroupmemberprops.setCallReceivePermission(grpMemProps.getCallTerminateAllowed());
                    xdmgroupmemberprops.setVideoCallInitiatePermission(grpMemProps.getVideoCallInitiateAllowed() != null ? grpMemProps.getVideoCallInitiateAllowed() : 0);
                    xdmgroupmemberprops.setVideoInCallPermission(grpMemProps.getVideoCallReceiveAllowed() != null ? grpMemProps.getVideoCallReceiveAllowed() : 0);
                    xdmgroupmemberprops.setVideoCallReceivePermission(grpMemProps.getVideoInCallAllowed() != null ? grpMemProps.getVideoInCallAllowed() : 0);
                    xdmgroupmemberprops.setIsOSMAuthorize(grpMemProps.getIsOSMAuthorized() != null ? grpMemProps.getIsOSMAuthorized() : 0);
                    xdmgroupmemberprops.setLocWatcher(grpMemProps.getIsLocSupervisor() != null ? grpMemProps.getIsLocSupervisor() : 0);
                    xdmGroupMember.setGroupMemProp(xdmgroupmemberprops);
                    xdmGroupMembers.add(xdmGroupMember);
                }
            }
            Set<KnXDMGroupListInfoDTO> xdmGroupMembersSet = new HashSet<KnXDMGroupListInfoDTO>(xdmGroupMembers);
            xdmUserProfile.setGroupListInfo(xdmGroupMembersSet);

            KnXDMEmergencyConfig xdmEmergencyConfig = new KnXDMEmergencyConfig();
            KnSubsEmergencyConfigDTO knSubsEmergencyConfigDTO = userProfile.getEmergencyConfig();
            if (knSubsEmergencyConfigDTO != null) {
                if (knSubsEmergencyConfigDTO.getCallType() != null)
                    xdmEmergencyConfig.setEmergCallType(String.valueOf(knSubsEmergencyConfigDTO.getCallType()));
                if (knSubsEmergencyConfigDTO.getCancelPermission() != null)
                    xdmEmergencyConfig.setEmergCancelPermission(String.valueOf(knSubsEmergencyConfigDTO.getCancelPermission()));
                if (knSubsEmergencyConfigDTO.getDestType() != null)
                    xdmEmergencyConfig.setEmergDestType(String.valueOf(knSubsEmergencyConfigDTO.getDestType()));
                if (knSubsEmergencyConfigDTO.getPermission() != null)
                    xdmEmergencyConfig.setEmergInitPermission(String.valueOf(knSubsEmergencyConfigDTO.getPermission()));
                if (knSubsEmergencyConfigDTO.getLmrBehavior() != null)
                    xdmEmergencyConfig.setEmergLMRBehavior(String.valueOf(knSubsEmergencyConfigDTO.getLmrBehavior()));
                if (knSubsEmergencyConfigDTO.getOrigBitset() != null)
                    xdmEmergencyConfig.setEmergOriginBitSet(String.valueOf(knSubsEmergencyConfigDTO.getOrigBitset()));
                if (knSubsEmergencyConfigDTO.getTermBitset() != null)
                    xdmEmergencyConfig.setEmergTermBitSet(String.valueOf(knSubsEmergencyConfigDTO.getTermBitset()));
                if (knSubsEmergencyConfigDTO.getEmergConfigTimer() != null) {
                    xdmEmergencyConfig.setEmergConfigTimer(knSubsEmergencyConfigDTO.getEmergConfigTimer());
                }

                Set<KnXDMEmergencyDestAttributes> xdmemergDestAttributes = null;
                Set<KnDestinationAttributeDTO> destAttributes = userProfile.getEmergencyConfig().getDestAttributes();
                if (destAttributes != null) {
                    xdmemergDestAttributes = new HashSet<>();
                    for (KnDestinationAttributeDTO destAttribute : destAttributes) {
                        KnXDMEmergencyDestAttributes xdmEmergencyDestAttribute = new KnXDMEmergencyDestAttributes();
                        if (destAttribute.getDestCat() != null)
                            xdmEmergencyDestAttribute.setDestAttributeCategory(String.valueOf(destAttribute.getDestCat()));
                        if (destAttribute.getDestType() != null)
                            xdmEmergencyDestAttribute.setDestAttributeType(String.valueOf(destAttribute.getDestType()));
                        if (destAttribute.getDestURI() != null)
                            xdmEmergencyDestAttribute.setDestAttributeUri(String.valueOf(destAttribute.getDestURI()));
                        xdmemergDestAttributes.add(xdmEmergencyDestAttribute);
                    }
                }

                xdmEmergencyConfig.setEmergDestAttributes(xdmemergDestAttributes);
                if (knSubsEmergencyConfigDTO.getDestType() != null)
                    xdmEmergencyConfig.setEmergDestType(String.valueOf(knSubsEmergencyConfigDTO.getDestType()));
            }
            xdmUserProfile.setEmergencyAttributes(xdmEmergencyConfig);
            xdmUserProfile.setUserProfileFS(userProfile.getFeatureBS());

            Set<KnCorpUserProfileMCPTTConfig> mcpttPermissionsConfig = userProfile.getMcpttPermissionsConfig();
            Set<KnTargetMdnPermissionBitInfo> knTargetMdnPermissionBitInfos = new HashSet<>();
            if (mcpttPermissionsConfig != null) {

                mcpttPermissionsConfig.forEach(userProfileMCPTTConfig -> {
                    if (userProfileMCPTTConfig != null) {
                        KnTargetMdnPermissionBitInfo targetMdnPermissionBitInfo = new KnTargetMdnPermissionBitInfo();
                        BitSet bitSet = KnGeneralUtil.convertLongToBitSet(userProfileMCPTTConfig.getPermBitSet());
                        targetMdnPermissionBitInfo.setAmbientListening(bitSet.get(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) ? 1 : 0);
                        targetMdnPermissionBitInfo.setDiscreteListening(bitSet.get(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.DISCRETELISTENING.value()) ? 1 : 0);
                        targetMdnPermissionBitInfo.setUserCheck(bitSet.get(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.USERCHECK.value()) ? 1 : 0);
                        targetMdnPermissionBitInfo.setUserEnable(bitSet.get(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.USERENABLE.value()) ? 1 : 0);
                        targetMdnPermissionBitInfo.setEmergPermission(bitSet.get(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.REMOTEEMERGENCYPERMISSION.value()) ? 1 : 0);
                        targetMdnPermissionBitInfo.setMcVideoUnConfirmedPull(bitSet.get(com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.MCVIDEOUNCONFIRMEDPULL.value()) ? 1 : 0);
                        targetMdnPermissionBitInfo.setMdn(userProfileMCPTTConfig.getMdn());
                        knTargetMdnPermissionBitInfos.add(targetMdnPermissionBitInfo);
                    }
                });
            }
            xdmUserProfile.setTargetMdnPermissionBitInfoList(knTargetMdnPermissionBitInfos);
            xdmUserProfile.setProfileId(userProfile.get_id());
            xdmUserProfile.setUserProfileIndex(String.valueOf(userProfile.getUserProfileIndex()));
            xdmUserProfile.setUserProfileStatus(userProfile.getUserProfileStatus());
            xdmUserProfile.setTgscMode(userProfile.getTgscMode());

            xdmUserProfile.setSharingEnabled(userProfile.getSharingEnabled());
            if (userProfile.getSharingEnabled() != null && userProfile.getSharingEnabled() == Boolean.TRUE)
                xdmUserProfile.setOwnerCorpId("" + userProfile.getCorporateID());
        }

        //parsing ends
        knLogger.debug(methodName, "--->xdmUserProfile ", xdmUserProfile);
        xdmRespDto.setUserProfileInfo(xdmUserProfile);
        if (respDto.getOwnerIDList() != null) {
            xdmRespDto.setOwnerIDList(respDto.getOwnerIDList());
        }
        if (respDto.getSharingEnabled() != null)
            xdmRespDto.setSharingEnabled(respDto.getSharingEnabled());

        List<String> userProfileSharedCorpIdList = respDto.getUserProfileSharedCorpList();
        if (userProfileSharedCorpIdList != null && !userProfileSharedCorpIdList.isEmpty()) {
            xdmRespDto.setUserProfileSharedCorpList(userProfileSharedCorpIdList);
        }
        xdmRespDto.setOwnerCorpId(respDto.getOwnerCorpId());
        xdmRespDto.setOwnerExtCorpId(respDto.getOwnerExtCorpId());
        xdmRespDto.setUpmGroupList(upmGroupList);
        knLogger.debug(methodName, "--->After call to getUserProfile details. Response - ", xdmRespDto);
        return xdmRespDto;

    }

    public KnXDMCorpUserProfileRespDTO getUserProfileList(IXDMRequestDTO requestDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getUserProfileList(IXDMRequestDTO, boolean, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpUserProfileRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpUserProfileRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpUserProfileRequestDTO userProfileRequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", userProfileRequestDTO);
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();

        ipUserProfileDTO.setHierarchyType(userProfileRequestDTO.getHierarchyType());
        ipUserProfileDTO.setCustomParamMap(userProfileRequestDTO.getCustomParamMap());
        ipUserProfileDTO.setCorpId(userProfileRequestDTO.getCorpId());
        //ipUserProfileDTO.setUserProfileDTO(userProfileRequestDTO.getUserProfileInfo());
        //   ipUserProfileDTO.getUserProfileDTO().setUserProfileName(userProfileRequestDTO.getUserProfileInfo().getProfileName());
        // ipUserProfileDTO.getUserProfileDTO().setContactListID(Integer.valueOf(userProfileRequestDTO.getUserProfileInfo().getContactListId()));
        ipUserProfileDTO.setFetchSize(userProfileRequestDTO.getFetchSize());
        ipUserProfileDTO.setStartIndex(userProfileRequestDTO.getStartIndex());
        ipUserProfileDTO.setHierarchyId(userProfileRequestDTO.getHierarchyId());

        KnCorpUserProfileListRespDTO respDto = null;
        respDto = corpClientIntf.getUserProfileList(ipUserProfileDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);

/*        StringBuilder namesStr = new StringBuilder();
        for(KnCorpUserProfileDTO userProfile : respDto.getUserProfileList())
        {
            namesStr = namesStr.length() > 0 ? namesStr.append(",").append(userProfile.getUserProfileIndex()) : namesStr.append(userProfile.getUserProfileIndex());
        }
        String commaSeparatedProfileIndexes=namesStr.toString();
        knLogger.debug(methodName, "--->commaSeparatedProfileIndexes ", commaSeparatedProfileIndexes); */
        //call timesten to get assigned mdn count per profileIndex


        Map<String, Integer> userProfileSubsCount = new HashMap<>();
        if (respDto.getUserProfileList() != null) {
            Map<String, Integer> userprofileIdToOwnerCorp = new HashMap<>();
            for (KnCorpUserProfileDTO userProfile : respDto.getUserProfileList()) {
                userprofileIdToOwnerCorp.put(userProfile.get_id(), userProfile.getCorporateID() == null ? 0 : userProfile.getCorporateID());
            }
            knLogger.debug(methodName, "--->userprofileIds ", userprofileIdToOwnerCorp);
            ///upIndexCountMap = corpClientIntf.getUpIndexCountMap(userprofileIndexes, ipUserProfileDTO.getCorpId(), persisterTxn);

            List<KnUserProfileAssignedDTO> userProfileAssignedDTOMap = corpClientIntf.getUserProfileSubsCount(userprofileIdToOwnerCorp.keySet(), ipUserProfileDTO.getCorpId(), readOnly, persisterTxn);
            knLogger.debug(methodName, "--->userProfileAssignedDTOMap ", userProfileAssignedDTOMap);

            String userPrfileId = null;
            int count = 0;
            for (KnUserProfileAssignedDTO userProfileAssignedDTO : userProfileAssignedDTOMap) {
                userPrfileId = userProfileAssignedDTO.getUserProfileId();
                if ((userprofileIdToOwnerCorp.get(userProfileAssignedDTO.getUserProfileId()) != Integer.parseInt(ipUserProfileDTO.getCorpId()))
                        && (userProfileAssignedDTO.getCorpId() != Integer.parseInt(ipUserProfileDTO.getCorpId()))) {
                    knLogger.debug(methodName, " Not assigned by the requested corpId or not a owner corpId");
                    continue;
                }

                //Add the subsc count in userProfileSubsCount

                if (userProfileSubsCount.containsKey(userPrfileId)) {
                    count = userProfileSubsCount.get(userPrfileId);
                } else {
                    count = 0;
                }
                userProfileSubsCount.put(userPrfileId, ++count);
            }
            knLogger.debug(methodName, " userProfileSubsCount :- ", userProfileSubsCount);
        }

        KnXDMCorpUserProfileRespDTO xdmRespDto = new KnXDMCorpUserProfileRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        Collection<KnXDMCorpUserProfileDTO> xdmUserProfilelistList = new ArrayList<>();
        Collection<KnCorpUserProfileDTO> userProfilelistList = respDto.getUserProfileList();
        knLogger.debug(methodName, "--->userProfilelistList ", userProfilelistList);
        if (userProfilelistList != null) {
            for (KnCorpUserProfileDTO userProfileList : userProfilelistList) {
                KnXDMCorpUserProfileDTO userProfileListDTO = new KnXDMCorpUserProfileDTO();
                //    userProfileListDTO.setCorpId(String.valueOf(userProfileList.getCorpId()));
                //  userProfileListDTO.setContactListId(String.valueOf(userProfileList.getContactListID()));
                userProfileListDTO.setProfileName(userProfileList.getUserProfileName());
                userProfileListDTO.setUserProfileIndex(String.valueOf(userProfileList.getUserProfileIndex()));
                userProfileListDTO.setProfileId(userProfileList.get_id());
                userProfileListDTO.setOwnerIdList(userProfileList.getOwnerIdList());
                if (userProfileSubsCount.containsKey(userProfileList.get_id()))
                    userProfileListDTO.setMaxCountPerProfileIndex(userProfileSubsCount.get(userProfileList.get_id()));
                else
                    userProfileListDTO.setMaxCountPerProfileIndex(0);
                //  userProfileListDTO.setUserProfileType(String.valueOf(userProfileList.getUserProfileType()));
                // knLogger.debug(methodName, "--->sublist.getDistributionPolicy() - ", sublist.getDistributionPolicy());
                //if(sublist.getDistributionPolicy() == com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DIST_POLICY_USER_PROFILE)
                //   sublistDTO.setUserProfileListType(1);
                userProfileListDTO.setSharingEnabled(userProfileList.getSharingEnabled());
                // if (userProfileList.getSharingEnabled() != null && userProfileList.getSharingEnabled() == Boolean.TRUE)
                String extCorpId = respDto.getCorpIdMap().containsKey(userProfileList.getCorporateID())
                        ? respDto.getCorpIdMap().get(userProfileList.getCorporateID()) : null;

                userProfileListDTO.setOwnerCorpId(extCorpId);
                xdmUserProfilelistList.add(userProfileListDTO);
            }
        }
        knLogger.debug(methodName, "--->xdmUserProfilelistList ", xdmUserProfilelistList);
        xdmRespDto.setUserProfilelistLists(xdmUserProfilelistList);
        xdmRespDto.setMaxTotalCount(respDto.getMaxtotalcount());
        knLogger.debug(methodName, "--->Returning all userProfile list - ", xdmRespDto);
        return xdmRespDto;

    }

    public KnXDMCorpUserProfileRespDTO getUserProfileListByName(IXDMRequestDTO requestDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getUserProfileListByName(IXDMRequestDTO, boolean, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpUserProfileRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpUserProfileRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpUserProfileRequestDTO userProfileRequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", userProfileRequestDTO);
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();

        ipUserProfileDTO.setHierarchyType(userProfileRequestDTO.getHierarchyType());
        ipUserProfileDTO.setCustomParamMap(userProfileRequestDTO.getCustomParamMap());
        ipUserProfileDTO.setCorpId(userProfileRequestDTO.getCorpId());
        ipUserProfileDTO.setUserProfileName(userProfileRequestDTO.getUserProfileName());
        ipUserProfileDTO.setFetchSize(userProfileRequestDTO.getFetchSize());
        ipUserProfileDTO.setStartIndex(userProfileRequestDTO.getStartIndex());
        ipUserProfileDTO.setIsCaseSensitiveSearch(userProfileRequestDTO.getIsCaseSensitiveSearch());
        knLogger.debug(methodName, "--->userProfileRequestDTO.getIsCase - ", userProfileRequestDTO.getIsCaseSensitiveSearch());

        KnCorpUserProfileListRespDTO respDto = null;
        respDto = corpClientIntf.getUserProfileListByName(ipUserProfileDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);

        Map<String, Integer> userProfileSubsCount = new HashMap<>();
        if (respDto.getUserProfileList() != null) {
            Map<String, Integer> userprofileIdToOwnerCorp = new HashMap<>();
            for (KnCorpUserProfileDTO userProfile : respDto.getUserProfileList()) {
                userprofileIdToOwnerCorp.put(userProfile.get_id(), userProfile.getCorporateID() == null ? 0 : userProfile.getCorporateID());
            }
            knLogger.debug(methodName, "--->userprofileIds ", userprofileIdToOwnerCorp);
            // upIndexCountMap = corpClientIntf.getUpIndexCountMap(userprofileIndexes, ipUserProfileDTO.getCorpId(), persisterTxn);

            List<KnUserProfileAssignedDTO> userProfileAssignedDTOMap = corpClientIntf.getUserProfileSubsCount(userprofileIdToOwnerCorp.keySet(), ipUserProfileDTO.getCorpId(), readOnly, persisterTxn);
            knLogger.debug(methodName, "--->userProfileAssignedDTOMap ", userProfileAssignedDTOMap);

            String userProfileId = null;
            int count = 0;
            for (KnUserProfileAssignedDTO userProfileAssignedDTO : userProfileAssignedDTOMap) {
                userProfileId = userProfileAssignedDTO.getUserProfileId();
                if ((userprofileIdToOwnerCorp.get(userProfileAssignedDTO.getUserProfileId()) != Integer.parseInt(ipUserProfileDTO.getCorpId()))
                        && (userProfileAssignedDTO.getCorpId() != Integer.parseInt(ipUserProfileDTO.getCorpId()))) {
                    knLogger.debug(methodName, " Not assigned by the requested corpId or not a owner corpId");
                    continue;
                }

                //Add the subsc count in userProfileSubsCount

                if (userProfileSubsCount.containsKey(userProfileId)) {
                    count = userProfileSubsCount.get(userProfileId);
                } else {
                    count = 0;
                }
                userProfileSubsCount.put(userProfileId, ++count);
            }
            knLogger.debug(methodName, " userProfileSubsCount :- ", userProfileSubsCount);
        }
        KnXDMCorpUserProfileRespDTO xdmRespDto = new KnXDMCorpUserProfileRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        Collection<KnXDMCorpUserProfileDTO> xdmUserProfilelistList = new ArrayList<>();
        Collection<KnCorpUserProfileDTO> userProfilelistList = respDto.getUserProfileList();
        knLogger.debug(methodName, "--->userProfilelistList ", userProfilelistList);
        if (userProfilelistList != null) {
            for (KnCorpUserProfileDTO userProfileList : userProfilelistList) {
                KnXDMCorpUserProfileDTO userProfileListDTO = new KnXDMCorpUserProfileDTO();
                userProfileListDTO.setProfileName(userProfileList.getUserProfileName());
                userProfileListDTO.setUserProfileIndex(String.valueOf(userProfileList.getUserProfileIndex()));
                userProfileListDTO.setProfileId(userProfileList.get_id());
                userProfileListDTO.setSharingEnabled(userProfileList.getSharingEnabled());
                String extCorpId = respDto.getCorpIdMap().containsKey(userProfileList.getCorporateID())
                        ? respDto.getCorpIdMap().get(userProfileList.getCorporateID()) : null;
                userProfileListDTO.setOwnerCorpId(extCorpId);
                if (userProfileSubsCount.containsKey(userProfileList.get_id()))
                    userProfileListDTO.setMaxCountPerProfileIndex(userProfileSubsCount.get(userProfileList.get_id()));
                else
                    userProfileListDTO.setMaxCountPerProfileIndex(0);
                if (ipUserProfileDTO.getHierarchyType() == com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.HIERARCHY)
                    userProfileListDTO.setOwnerIdList(userProfileList.getOwnerIdList());
                xdmUserProfilelistList.add(userProfileListDTO);
            }
        }
        knLogger.debug(methodName, "--->xdmUserProfilelistList ", xdmUserProfilelistList);
        xdmRespDto.setUserProfilelistLists(xdmUserProfilelistList);
        xdmRespDto.setMaxTotalCount(respDto.getMaxtotalcount());
        knLogger.debug(methodName, "--->Returning all userProfile list - ", xdmRespDto);
        return xdmRespDto;

    }

    public KnXDMCorpUserProfileRespDTO getSubscriberUserProfileList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getSubscriberUserProfileList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpUserProfileRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpUserProfileRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpUserProfileRequestDTO userProfileRequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", userProfileRequestDTO);
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();

        ipUserProfileDTO.setHierarchyType(userProfileRequestDTO.getHierarchyType());
        ipUserProfileDTO.setCustomParamMap(userProfileRequestDTO.getCustomParamMap());
        ipUserProfileDTO.setCorpId(userProfileRequestDTO.getCorpId());
        ipUserProfileDTO.setMdn(userProfileRequestDTO.getMdn());

        KnCorpUserProfileListRespDTO respDto = null;
        respDto = corpClientIntf.getSubscriberUserProfileList(ipUserProfileDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);

        KnXDMCorpUserProfileRespDTO xdmRespDto = new KnXDMCorpUserProfileRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        Collection<KnXDMCorpUserProfileDTO> xdmUserProfilelistList = new ArrayList<>();
        Collection<KnSubscriberUserProfileDTO> userProfilelistList = respDto.getSubscriberUserProfileList();
        knLogger.debug(methodName, "--->userProfilelistList ", userProfilelistList);
        if (userProfilelistList != null) {
            for (KnSubscriberUserProfileDTO userProfileList : userProfilelistList) {
                KnXDMCorpUserProfileDTO userProfileListDTO = new KnXDMCorpUserProfileDTO();
                userProfileListDTO.setProfileName(userProfileList.getUserProfileName());
                userProfileListDTO.setUserProfileIndex(String.valueOf(userProfileList.getUserProfileIndex()));
                userProfileListDTO.setProfileId(userProfileList.get_id());
                userProfileListDTO.setIsDefaultProfile(String.valueOf(userProfileList.getIsDefaultProfile()));
                if (ipUserProfileDTO.getHierarchyType() == com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                    userProfileListDTO.setOwnerIdList(userProfileList.getOwnerIdList());
                }
                xdmUserProfilelistList.add(userProfileListDTO);
            }
        }
        knLogger.debug(methodName, "--->xdmUserProfilelistList ", xdmUserProfilelistList);
        xdmRespDto.setUserProfilelistLists(xdmUserProfilelistList);
        knLogger.debug(methodName, "--->Returning all userProfile list - ", xdmRespDto);
        return xdmRespDto;

    }

    public KnCorpResponseDTO assignUserProfile(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "assignUserProfile(IXDMRequestDTO, KnPersisterTxn)";

        KnXDMCorpUserProfileRequestDTO userProfileRequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry userProfileRequestDTO ", userProfileRequestDTO);
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();

        ipUserProfileDTO.setHierarchyType(userProfileRequestDTO.getHierarchyType());
        ipUserProfileDTO.setCustomParamMap(userProfileRequestDTO.getCustomParamMap());
        ipUserProfileDTO.setCorpId(userProfileRequestDTO.getCorpId());
        ipUserProfileDTO.setMdn(userProfileRequestDTO.getMdn());
        ipUserProfileDTO.setProfileId(userProfileRequestDTO.getUserProfileId());

        KnCorpResponseDTO respDto = corpClientIntf.assignUserProfile(ipUserProfileDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }


    public KnXDMCorpUserProfileRespDTO getUserProfileSubscriberList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getUserProfileSubscriberList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpUserProfileRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpUserProfileRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpUserProfileRequestDTO userProfileRequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", userProfileRequestDTO);
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();

        ipUserProfileDTO.setHierarchyType(userProfileRequestDTO.getHierarchyType());
        ipUserProfileDTO.setCustomParamMap(userProfileRequestDTO.getCustomParamMap());
        ipUserProfileDTO.setCorpId(userProfileRequestDTO.getCorpId());
        ipUserProfileDTO.setProfileId(userProfileRequestDTO.getUserProfileId());
        ipUserProfileDTO.setStartIndex(userProfileRequestDTO.getStartIndex());
        ipUserProfileDTO.setFetchSize(userProfileRequestDTO.getFetchSize());

        KnCorpUserProfileListRespDTO respDto = corpClientIntf.getUserProfileSubscriberList(ipUserProfileDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);

        KnXDMCorpUserProfileRespDTO xdmRespDto = new KnXDMCorpUserProfileRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        Collection<KnMDNInfoDto> mdnList = respDto.getMdnInfoCollection();
        knLogger.debug(methodName, "--->mdnList ", mdnList);
        List<KnXDMMdnInfoDTO> xdmMdnInfoDTOS = null;
        if (mdnList != null) {
            xdmMdnInfoDTOS = new ArrayList<>(mdnList.size());
            for (KnMDNInfoDto mdn : mdnList) {
                KnXDMMdnInfoDTO mdnInfoDTO = new KnXDMMdnInfoDTO();
                mdnInfoDTO.setMdn(mdn.getMdn());
                mdnInfoDTO.setName(mdn.getName());
                mdnInfoDTO.setCorpID(mdn.getCorpID());
                xdmMdnInfoDTOS.add(mdnInfoDTO);
            }
            knLogger.debug(methodName, "--->xdmMdnInfoDTOS ", xdmMdnInfoDTOS);
        }
        xdmRespDto.setMdnList(xdmMdnInfoDTOS);
        // xdmRespDto.setCorpId(userProfileRequestDTO.getCorpId());
        knLogger.debug(methodName, "--->Returning all mdn List - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO updateDefaultProfile(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateDefaultProfile(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpUserProfileRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpUserProfileRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpUserProfileRequestDTO userProfileRequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", userProfileRequestDTO);
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();

        ipUserProfileDTO.setHierarchyType(userProfileRequestDTO.getHierarchyType());
        ipUserProfileDTO.setCustomParamMap(userProfileRequestDTO.getCustomParamMap());
        ipUserProfileDTO.setCorpId(userProfileRequestDTO.getCorpId());
        ipUserProfileDTO.setProfileId(userProfileRequestDTO.getUserProfileId());
        ipUserProfileDTO.setMdn(userProfileRequestDTO.getMdn());
        ipUserProfileDTO.setIsDefaultProfile(userProfileRequestDTO.getIsDefaultProfile());

        KnCorpResponseDTO respDto = corpClientIntf.updateDefaultProfile(ipUserProfileDTO, persisterTxn);

        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO unassignUserProfile(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "unassignUserProfile(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpUserProfileRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpUserProfileRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpUserProfileRequestDTO userProfileRequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", userProfileRequestDTO);
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
        KnCorpResponseDTO respDto = null;
        ipUserProfileDTO.setHierarchyType(userProfileRequestDTO.getHierarchyType());
        ipUserProfileDTO.setCustomParamMap(userProfileRequestDTO.getCustomParamMap());
        ipUserProfileDTO.setCorpId(userProfileRequestDTO.getCorpId());
        ipUserProfileDTO.setProfileId(userProfileRequestDTO.getUserProfileId());
        ipUserProfileDTO.setMdn(userProfileRequestDTO.getMdn());
        respDto = corpClientIntf.unassignUserProfile(ipUserProfileDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnXDMGroupMemberShipRespDTO getSubsGroupMemberShipDetails(KnIPSubscriberInfoDTO ipSubscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getSubsGroupMemberShipDetails(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        KnCorpResponseDTO respDto = corpClientIntf.getSubsGroupMemberShipDetails(ipSubscriberInfoDTO, persisterTxn);
        KnXDMGroupMemberShipRespDTO xdmRespDto = new KnXDMGroupMemberShipRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        Map<Integer, Integer> grpMemberShipMap = respDto.getGrpMemberShipMap();
        Map<Integer, Integer> respGrpMemberShipMap = new HashMap<Integer, Integer>();
        Map<Integer, com.kodiak.common.commdto.common.KnCorpGroupContactDTO> mcxGrpMemberShipMap = respDto.getMcxGrpMemberShipMap();


        if (grpMemberShipMap != null && !grpMemberShipMap.isEmpty()) {
            grpMemberShipMap.forEach((grpId, memberShipVal) -> {
                respGrpMemberShipMap.put(grpId, memberShipVal);
            });
        }
        xdmRespDto.setGrpMemberShipMap(respGrpMemberShipMap);
        xdmRespDto.setMcxGrpMemberShipMap(mcxGrpMemberShipMap);

        knLogger.debug(methodName, "EXIT ", xdmRespDto);

        return xdmRespDto;
    }

    /**
     * This method returns all AU including profile mdn also.
     *
     * @param requestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMCorpAuthUserPermissionRespDTO getAllAuthorizedMdnList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getAllAuthorizedMdnList()";
        KnCorpSubsResquestDTO xdmRequestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO = new KnIPAuthUserPermissionInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipAuthUserPermissionInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipAuthUserPermissionInfoDTO.setTargetMdn(xdmRequestDTO.getSubscriberMdn());
        knLogger.debug(methodName, "getAuthorizedMdnList input dto - ", ipAuthUserPermissionInfoDTO);
        KnCorpUserPermissionRespDTO respDto = corpClientIntf.getAllAuthorizedMdnList(ipAuthUserPermissionInfoDTO, persisterTxn, xdmRequestDTO.isUpmFlag());
        knLogger.debug(methodName, "Returning getAuthorizedMdnList response - ", respDto);
        KnXDMCorpAuthUserPermissionRespDTO xdmRespDto = new KnXDMCorpAuthUserPermissionRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfos = new ArrayList<>();
        Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = respDto.getTargetMdnPermissionBitInfoList();
        if (targetMdnPermBitInfos != null && !targetMdnPermBitInfos.isEmpty()) {
            targetMdnPermBitInfos.forEach(targetMdnPermBitInfo -> {
                KnTargetMdnPermissionBitInfo targetInfo = new KnTargetMdnPermissionBitInfo();
                targetInfo.setMdn(targetMdnPermBitInfo.getMdn());
                targetInfo.setSubsName(targetMdnPermBitInfo.getSubsName());
                targetInfo.setAmbientListening(targetMdnPermBitInfo.getAmbientListening());
                targetInfo.setDiscreteListening(targetMdnPermBitInfo.getDiscreteListening());
                targetInfo.setUserCheck(targetMdnPermBitInfo.getUserCheck());
                targetInfo.setUserEnable(targetMdnPermBitInfo.getUserEnable());
                targetInfo.setEmergPermission(targetMdnPermBitInfo.getEmergPermission());
                targetInfo.setMcVideoUnConfirmedPull(targetMdnPermBitInfo.getMcVideoUnConfirmedPull());
                targetMdnPermissionBitInfos.add(targetInfo);
            });
            xdmRespDto.setTargetMdnPermissionBitInfo(targetMdnPermissionBitInfos);
        }
        knLogger.debug(methodName, "After Retrieval, Response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO updateImpactedTuPerms(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateImpactedTuPerms(IXDMRequestDTO , KnPersisterTxn)";

        KnXDMCorpUserProfileRequestDTO userProfileRequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry :", userProfileRequestDTO);

        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();
        ipUserProfileDTO.setCorpId(userProfileRequestDTO.getCorpId());

        KnCorpModifyUserProfileDTO modifyUpmReq = KnCorpCommonInfoUtil.modifyUpmTransform(userProfileRequestDTO);
        ipUserProfileDTO.setModifiedUserProfileDTO(modifyUpmReq);

        KnCorpResponseDTO respDto = corpClientIntf.updateImpactedTuPerms(ipUserProfileDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    /**
     * Method to create bulk corporate groups from the request group profile.
     *
     * @param requestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnCorpGroupInfoRespDTO createBulkCorpGroup(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "createBulkCorpGroup()";

        KnIPCorpBulkGroupDTO bulkGroupIPDto = new KnIPCorpBulkGroupDTO();

        if (!(requestDTO instanceof KnXDMCreateBulkGroupReqDTO)) {
            knLogger.error(methodName, "Request dto is not of type KnXDMCreateBulkGroupReqDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCreateBulkGroupReqDTO xdmRequestDto = (KnXDMCreateBulkGroupReqDTO) requestDTO;
        knLogger.debug(methodName, "xdmRequestDto :: ", xdmRequestDto);
        bulkGroupIPDto.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        bulkGroupIPDto.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        bulkGroupIPDto.setHierarchyType(xdmRequestDto.getHierarchyType());
        bulkGroupIPDto.setClientType(xdmRequestDto.getClientType());
        bulkGroupIPDto.setProfileId(xdmRequestDto.getProfileId());
        bulkGroupIPDto.setProfileName(xdmRequestDto.getProfileName());
        bulkGroupIPDto.setGrpNameList(xdmRequestDto.getGroupNameList());
        bulkGroupIPDto.setHierarchyId(xdmRequestDto.getHierarchyId());
        knLogger.debug(methodName, "bulkGroupDTO :: ", bulkGroupIPDto);
        KnCorpGroupInfoRespDTO respDto = corpClientIntf.createBulkCorpGroup(bulkGroupIPDto, persisterTxn);

        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    /**
     * Method to retrieve the list of groups which are associated with the requested profile ID/name.
     *
     * @param requestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMCorpGroupListRespDTO getProfileGroupList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "getProfileGroupList()";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpGroupProfileRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpGroupProfileRequestDTO xdmRerquestDto = (KnXDMCorpGroupProfileRequestDTO) requestDTO;
        KnIPCorpGroupProfileDTO groupProfileDTO = new KnIPCorpGroupProfileDTO();
        groupProfileDTO.setCorpId(Integer.valueOf(xdmRerquestDto.getCorpId()));
        groupProfileDTO.setClientType(xdmRerquestDto.getClientType());
        groupProfileDTO.setHierarchyType(xdmRerquestDto.getHierarchyType());
        groupProfileDTO.setCustomParamMap(xdmRerquestDto.getCustomParamMap());
        groupProfileDTO.setProfileId(xdmRerquestDto.getGrpProfileId());
        groupProfileDTO.setProfileName(xdmRerquestDto.getGrpProfileName());
        groupProfileDTO.setStartIndex(xdmRerquestDto.getStartIndex());
        groupProfileDTO.setFetchSize(xdmRerquestDto.getFetchSize());

        knLogger.debug(methodName, "Get Profile Corp Group List request - ", groupProfileDTO);
        KnCorpGroupListRespDTO respDto = corpClientIntf.getProfileGroupList(groupProfileDTO, persisterTxn);
        knLogger.debug(methodName, "Get Profile Corp Group List response - ", respDto);

        KnXDMCorpGroupListRespDTO xdmRespDto = new KnXDMCorpGroupListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        Collection<KnCorpGroupInfoDTO> groupList = respDto.getGroupList();
        Collection<KnXDMCorpGroupDTO> xdmGroupList = new ArrayList<KnXDMCorpGroupDTO>();
        if (groupList != null) {
            for (KnCorpGroupInfoDTO group : groupList) {
                KnXDMCorpGroupDTO xdmGroupdto = new KnXDMCorpGroupDTO();
                xdmGroupdto.setCorpId(String.valueOf(group.getCorpId()));
                xdmGroupdto.setGroupId(String.valueOf(group.getGroupId()));
                xdmGroupdto.setGroupName(group.getGroupDisplayName());
                xdmGroupdto.setMaxGroupMemberLimitFlag(group.getMaxGroupMemLimitFlag());
                xdmGroupdto.setMemberCount(group.getMemberCount());
                xdmGroupdto.setGroupType(group.getGroupType());
                xdmGroupdto.setAvatar(group.getAvatar());
                xdmGroupdto.setLargeGrp(group.isLargeGroup());
                xdmGroupdto.setGroupUri(group.getGroupUri());
                xdmGroupdto.setMcxGrpInd(group.getMcxGrpInd());
                xdmGroupdto.setGrpProfileId(Integer.parseInt(group.getGroupProfileId()));
                xdmGroupdto.setGrpShared(group.getGrpShared());
                xdmGroupdto.setGrpOwnerCorpId(group.getGrpOwnerCorpId());
                xdmGroupdto.setGrpOwnerExtCorpId(group.getGrpOwnerExtCorpId());

                List<KnCorpSharedCorpInfo> sharedCorpInfoList = group.getCorpSharedCorpInfoList();
                if (sharedCorpInfoList != null && !sharedCorpInfoList.isEmpty()) {
                    List<KnXDMCorpGrpSharedCorpListDTO> xdmCorpGrpSharedCorpList = new ArrayList<>();
                    sharedCorpInfoList.forEach(
                            sharedCorpInfo -> {
                                KnXDMCorpGrpSharedCorpListDTO xdmDto = new KnXDMCorpGrpSharedCorpListDTO();
                                xdmDto.setExtCorpID(sharedCorpInfo.getExtCorpId());
                                xdmDto.setIntCorpID(sharedCorpInfo.getCorpId());
                                xdmCorpGrpSharedCorpList.add(xdmDto);
                            }

                    );
                    xdmGroupdto.setGrpSharedCopList(xdmCorpGrpSharedCorpList);
                }

                Collection<KnXDMCorpContactDTO> members = new ArrayList<KnXDMCorpContactDTO>();
                if (group.getGroupSupervisor() != null) {
                    for (KnCorpGroupMemberDTO grpMember : group.getGroupSupervisor()) {
                        KnXDMCorpContactDTO contact = new KnXDMCorpContactDTO();
                        contact.setMdn(grpMember.getMdn());
                        contact.setName(grpMember.getName());
                        contact.setSupervisor(grpMember.getSupervisory());
                        contact.setLocWatcher(grpMember.getLocWatcher());
                        contact.setBroadcaster(grpMember.getBroadcaster());
                        contact.setClientType(grpMember.getClientType());
                        contact.setCallInitiatePermission(grpMember.getCallInitiatePermission());
                        contact.setCallReceivePermission(grpMember.getCallReceivePermission());
                        contact.setInCallPermission(grpMember.getInCallPermission());
                        members.add(contact);
                    }
                }
                xdmGroupdto.setSupervisorList(members);
                xdmGroupList.add(xdmGroupdto);
            }
        }
        xdmRespDto.setGroupList(xdmGroupList);
        xdmRespDto.setTotalGrpCount(respDto.getTotalGrpCount());
        knLogger.debug(methodName, "Returning Get Corp Group List response - ", xdmRespDto);
        return xdmRespDto;

    }

    public KnCorpResponseDTO createGroupProfile(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "createGroupProfile()";

        KnIPCorpGorupProfileDTO groupProfileIPDto = new KnIPCorpGorupProfileDTO();

        if (!(requestDTO instanceof KnXDMCorpGroupProfileRequestDTO)) {
            knLogger.error(methodName, "Request dto is not of type KnXDMCorpGroupProfileRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpGroupProfileRequestDTO xdmRequestDto = (KnXDMCorpGroupProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "xdmRequestDto :: ", xdmRequestDto);

        groupProfileIPDto.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        groupProfileIPDto.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        groupProfileIPDto.setHierarchyType(xdmRequestDto.getHierarchyType());
        groupProfileIPDto.setClientType(xdmRequestDto.getClientType());
        groupProfileIPDto.setGrpProfileName(xdmRequestDto.getGrpProfileName());
        groupProfileIPDto.setGrpType(xdmRequestDto.getGrpType());
        groupProfileIPDto.setGrpAvatar(xdmRequestDto.getGrpAvatar());
        groupProfileIPDto.setGrpServiceType(xdmRequestDto.getGrpServiceType());
        groupProfileIPDto.setGrpOSMListId(xdmRequestDto.getGrpOSMListId());
        groupProfileIPDto.setAudioCutIn(xdmRequestDto.getAudioCutIn());
        groupProfileIPDto.setMcxGroup(xdmRequestDto.getMcxGroup());
        groupProfileIPDto.setOverrideDND(xdmRequestDto.getOverrideDND());
        groupProfileIPDto.setGrpShared(xdmRequestDto.getGrpShared());
        groupProfileIPDto.setUgwInterop(xdmRequestDto.getUgwInterop());
        groupProfileIPDto.setHierarchyId(xdmRequestDto.getHierarchyId());
        if (xdmRequestDto.getSharedCorpList() != null && !xdmRequestDto.getSharedCorpList().isEmpty()) {
            List<KnCorpSharedCorpInfo> sharedCorpInfoDTOs = new ArrayList<>();
            for (KnXDMCorpGrpSharedCorpListDTO xdmSharedCorpInfoDTO : xdmRequestDto.getSharedCorpList()) {
                KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                sharedCorpInfo.setCorpId(xdmSharedCorpInfoDTO.getIntCorpID());
                sharedCorpInfo.setExtCorpId(xdmSharedCorpInfoDTO.getExtCorpID());
                sharedCorpInfo.setHierarchyList(xdmSharedCorpInfoDTO.getHierarchyList());
                //TODO:set group member properties
                //sharedCorpInfo.setGrpMemProps();
                sharedCorpInfoDTOs.add(sharedCorpInfo);
            }

            groupProfileIPDto.setSharedCorpList(sharedCorpInfoDTOs);
        }
        knLogger.debug(methodName, "groupProfileIPDto :: ", groupProfileIPDto);
        KnCorpResponseDTO respDto = corpClientIntf.createGroupProfile(groupProfileIPDto, persisterTxn);

        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnXDMCorpGroupProfileResponseDTO getGroupProfileList(IXDMRequestDTO requestDTO,
                                                                KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getGroupProfileList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpGroupProfileRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpGroupProfileRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpGroupProfileRequestDTO xdmRequestDto = (KnXDMCorpGroupProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "xdmRequestDto :: ", xdmRequestDto);
        KnIPCorpGorupProfileDTO groupProfileIPDto = new KnIPCorpGorupProfileDTO();
        groupProfileIPDto.setHierarchyType(xdmRequestDto.getHierarchyType());
        groupProfileIPDto.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        groupProfileIPDto.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        groupProfileIPDto.setFetchSize(xdmRequestDto.getFetchSize());
        groupProfileIPDto.setStartIndex(xdmRequestDto.getStartIndex());
        groupProfileIPDto.setHierarchyId(xdmRequestDto.getHierarchyId());
        KnCorpGroupProfileResponseDTO respDto = corpClientIntf.getGroupProfileList(groupProfileIPDto, persisterTxn);
        knLogger.debug(methodName, "respDto ", respDto);
        KnXDMCorpGroupProfileResponseDTO xdmRespDto = new KnXDMCorpGroupProfileResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        if (respDto.getGroupProfileList() != null) {
            List<KnXDMCorpGroupProfileDTO> xDMCorpGroupProfileDTO = new ArrayList<KnXDMCorpGroupProfileDTO>();
            for (KnCorpGroupProfileInfo corpGrpProfileDTO : respDto.getGroupProfileList()) {

                KnXDMCorpGroupProfileDTO groupProfileDTO = new KnXDMCorpGroupProfileDTO();
                groupProfileDTO.setGrpProfileName(corpGrpProfileDTO.getProfileName());
                groupProfileDTO.setGrpType(corpGrpProfileDTO.getGrpType());
                groupProfileDTO.setGrpAvatar(corpGrpProfileDTO.getAvatar());
                groupProfileDTO.setGrpServiceType(corpGrpProfileDTO.getServiceType());
                groupProfileDTO.setGrpOSMListId(String.valueOf(corpGrpProfileDTO.getOsmListId()));
                groupProfileDTO.setAudioCutIn(corpGrpProfileDTO.getAudioCutIn());
                groupProfileDTO.setMcxGroup(corpGrpProfileDTO.getMcxGrp());
                groupProfileDTO.setGrpProfileId(corpGrpProfileDTO.getProfileId());
                Integer grpCnt = respDto.getGroupProfileCountMap().get(String.valueOf(corpGrpProfileDTO.getProfileId()));
                groupProfileDTO.setGrpCount(grpCnt != null ? grpCnt : 0);
                groupProfileDTO.setCreateTimeStamp(corpGrpProfileDTO.getCreateTimeStamp());
                groupProfileDTO.setUpdateTimeStamp(corpGrpProfileDTO.getUpdateTimeStamp());
                groupProfileDTO.setGrpProfileStatus(corpGrpProfileDTO.getGrpProfileStatus());
                groupProfileDTO.setFeatureAllowed(corpGrpProfileDTO.getFeatureAllowed());
                groupProfileDTO.setOverrideDND(corpGrpProfileDTO.getOverrideDnd());
                groupProfileDTO.setGrpShared(corpGrpProfileDTO.getGrpShared());
                groupProfileDTO.setUgwInterop(corpGrpProfileDTO.getUgwInterop());
                List<KnCorpSharedCorpInfo> sharedCorpInfoList = corpGrpProfileDTO.getSharedCorpList();
                if (sharedCorpInfoList != null && !sharedCorpInfoList.isEmpty()) {
                    List<KnXDMCorpGrpSharedCorpListDTO> xdmCorpGrpSharedCorpList = new ArrayList<>();
                    sharedCorpInfoList.forEach(
                            sharedCorpInfo -> {
                                KnXDMCorpGrpSharedCorpListDTO xdmDto = new KnXDMCorpGrpSharedCorpListDTO();
                                xdmDto.setExtCorpID(sharedCorpInfo.getExtCorpId());
                                xdmDto.setIntCorpID(sharedCorpInfo.getCorpId());
                                xdmCorpGrpSharedCorpList.add(xdmDto);
                            }

                    );
                    groupProfileDTO.setSharedCorpList(xdmCorpGrpSharedCorpList);
                }
                xDMCorpGroupProfileDTO.add(groupProfileDTO);
            }

            xdmRespDto.setGroupProfile(xDMCorpGroupProfileDTO);
            xdmRespDto.setTotalGroupProfilesCount(respDto.getTotalGroupProfilesCount());
        }

        xdmRespDto.setCorpId(xdmRequestDto.getCorpId());

        knLogger.debug(methodName, "EXIT - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMCorpGroupProfileResponseDTO getGroupProfileDetails(IXDMRequestDTO requestDTO,
                                                                   KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getGroupProfileDetails(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpGroupProfileRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpGroupProfileRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpGroupProfileRequestDTO xdmRequestDto = (KnXDMCorpGroupProfileRequestDTO) requestDTO;

        knLogger.debug(methodName, "xdmRequestDto :: ", xdmRequestDto);
        KnIPCorpGorupProfileDTO groupProfileIPDto = new KnIPCorpGorupProfileDTO();
        groupProfileIPDto.setHierarchyType(xdmRequestDto.getHierarchyType());
        groupProfileIPDto.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        groupProfileIPDto.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        groupProfileIPDto.setGrpProfileName(xdmRequestDto.getGrpProfileName());
        groupProfileIPDto.setGrpProfileId(xdmRequestDto.getGrpProfileId());
        if (xdmRequestDto.getHierarchyId() != null) {
            groupProfileIPDto.setHierarchyId(xdmRequestDto.getHierarchyId());
        }
        KnCorpGroupProfileResponseDTO respDto = corpClientIntf.getGroupProfileDetails(groupProfileIPDto, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        KnXDMCorpGroupProfileResponseDTO xdmRespDto = new KnXDMCorpGroupProfileResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        if (respDto.getGroupProfileList() != null) {
            List<KnXDMCorpGroupProfileDTO> xDMCorpGroupProfileDTO = new ArrayList<KnXDMCorpGroupProfileDTO>();
            for (KnCorpGroupProfileInfo corpGrpProfileDTO : respDto.getGroupProfileList()) {

                KnXDMCorpGroupProfileDTO groupProfileDTO = new KnXDMCorpGroupProfileDTO();
                groupProfileDTO.setGrpProfileName(corpGrpProfileDTO.getProfileName());
                groupProfileDTO.setGrpType(corpGrpProfileDTO.getGrpType());
                groupProfileDTO.setGrpAvatar(corpGrpProfileDTO.getAvatar());
                groupProfileDTO.setGrpServiceType(corpGrpProfileDTO.getServiceType());
                if (corpGrpProfileDTO.getOsmListId() != null) {
                    groupProfileDTO.setGrpOSMListId(String.valueOf(corpGrpProfileDTO.getOsmListId()));
                }
                groupProfileDTO.setAudioCutIn(corpGrpProfileDTO.getAudioCutIn());
                groupProfileDTO.setMcxGroup(corpGrpProfileDTO.getMcxGrp());
                groupProfileDTO.setGrpProfileId(corpGrpProfileDTO.getProfileId());
                Integer grpCnt = respDto.getGroupProfileCountMap().get(String.valueOf(corpGrpProfileDTO.getProfileId()));
                groupProfileDTO.setGrpCount(grpCnt != null ? grpCnt : 0);
                groupProfileDTO.setCreateTimeStamp(corpGrpProfileDTO.getCreateTimeStamp());
                groupProfileDTO.setUpdateTimeStamp(corpGrpProfileDTO.getUpdateTimeStamp());
                groupProfileDTO.setGrpProfileStatus(corpGrpProfileDTO.getGrpProfileStatus());
                groupProfileDTO.setFeatureAllowed(corpGrpProfileDTO.getFeatureAllowed());
                groupProfileDTO.setOverrideDND(corpGrpProfileDTO.getOverrideDnd());
                groupProfileDTO.setGrpShared(corpGrpProfileDTO.getGrpShared());
                groupProfileDTO.setGrpOwnerCorpId(corpGrpProfileDTO.getGrpOwnerCorpId());
                groupProfileDTO.setGrpOwnerExtCorpId(corpGrpProfileDTO.getGrpOwnerExtCorpId());
                groupProfileDTO.setUgwInterop(corpGrpProfileDTO.getUgwInterop());
                List<KnCorpSharedCorpInfo> sharedCorpInfoList = corpGrpProfileDTO.getSharedCorpList();
                if (sharedCorpInfoList != null && !sharedCorpInfoList.isEmpty()) {
                    List<KnXDMCorpGrpSharedCorpListDTO> xdmCorpGrpSharedCorpList = new ArrayList<>();
                    sharedCorpInfoList.forEach(
                            sharedCorpInfo -> {
                                KnXDMCorpGrpSharedCorpListDTO xdmDto = new KnXDMCorpGrpSharedCorpListDTO();
                                xdmDto.setExtCorpID(sharedCorpInfo.getExtCorpId());
                                xdmDto.setIntCorpID(sharedCorpInfo.getCorpId());
                                if (sharedCorpInfo.getGrpMemProps() != null) {
                                    KnXDMGroupMdnInfoDTO groupMdnInfoDTO = new KnXDMGroupMdnInfoDTO();
                                    groupMdnInfoDTO.setBroadcaster(sharedCorpInfo.getGrpMemProps().getIsBroadcaster());
                                    groupMdnInfoDTO.setSupervisor(sharedCorpInfo.getGrpMemProps().getIsSupervisor());
                                    groupMdnInfoDTO.setCallInitiatePermission(sharedCorpInfo.getGrpMemProps().getCallInitiateAllowed());
                                    groupMdnInfoDTO.setCallReceivePermission(sharedCorpInfo.getGrpMemProps().getCallTerminateAllowed());
                                    groupMdnInfoDTO.setInCallPermission(sharedCorpInfo.getGrpMemProps().getIncallAllowed());
                                    groupMdnInfoDTO.setIsOSMAuthorize(sharedCorpInfo.getGrpMemProps().getIsOSMAuthorized());
                                    groupMdnInfoDTO.setLocWatcher(sharedCorpInfo.getGrpMemProps().getIsLocSupervisor());
                                    xdmDto.setGroupMemberProps(groupMdnInfoDTO);
                                }
                                xdmCorpGrpSharedCorpList.add(xdmDto);
                            }

                    );
                    groupProfileDTO.setSharedCorpList(xdmCorpGrpSharedCorpList);
                }
                xDMCorpGroupProfileDTO.add(groupProfileDTO);
            }

            xdmRespDto.setGroupProfile(xDMCorpGroupProfileDTO);
        }

        xdmRespDto.setCorpId(xdmRequestDto.getCorpId());
        return xdmRespDto;

    }

    public KnXDMCorpGroupProfileResponseDTO searchGroupProfile(IXDMRequestDTO requestDTO,
                                                               KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "searchGroupProfile(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpGroupProfileRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpGroupProfileRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpGroupProfileRequestDTO xdmRequestDto = (KnXDMCorpGroupProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "xdmRequestDto :: ", xdmRequestDto);
        KnIPCorpGorupProfileDTO groupProfileIPDto = new KnIPCorpGorupProfileDTO();
        groupProfileIPDto.setHierarchyType(xdmRequestDto.getHierarchyType());
        groupProfileIPDto.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        groupProfileIPDto.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        groupProfileIPDto.setGrpProfileName(xdmRequestDto.getGrpProfileName());
        groupProfileIPDto.setGrpType(xdmRequestDto.getGrpType());
        groupProfileIPDto.setFetchSize(xdmRequestDto.getFetchSize());
        groupProfileIPDto.setStartIndex(xdmRequestDto.getStartIndex());
        knLogger.debug(methodName, "search group profile request - ", groupProfileIPDto);
        KnCorpGroupProfileResponseDTO respDto = corpClientIntf.searchGroupProfile(groupProfileIPDto, persisterTxn);
        knLogger.debug(methodName, "search group profile response - ", respDto);
        KnXDMCorpGroupProfileResponseDTO xdmRespDto = new KnXDMCorpGroupProfileResponseDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        if (respDto.getGroupProfileList() != null) {
            List<KnXDMCorpGroupProfileDTO> xdmCorpGroupProfileDTOList = new ArrayList<KnXDMCorpGroupProfileDTO>();
            for (KnCorpGroupProfileInfo corpGrpProfileDTO : respDto.getGroupProfileList()) {

                KnXDMCorpGroupProfileDTO groupProfileDTO = new KnXDMCorpGroupProfileDTO();
                groupProfileDTO.setGrpProfileName(corpGrpProfileDTO.getProfileName());
                groupProfileDTO.setGrpType(corpGrpProfileDTO.getGrpType());
                groupProfileDTO.setGrpAvatar(corpGrpProfileDTO.getAvatar());
                groupProfileDTO.setGrpServiceType(corpGrpProfileDTO.getServiceType());
                groupProfileDTO.setGrpOSMListId(String.valueOf(corpGrpProfileDTO.getOsmListId()));
                groupProfileDTO.setAudioCutIn(corpGrpProfileDTO.getAudioCutIn());
                groupProfileDTO.setMcxGroup(corpGrpProfileDTO.getMcxGrp());
                groupProfileDTO.setGrpProfileId(corpGrpProfileDTO.getProfileId());
                Integer grpCnt = respDto.getGroupProfileCountMap().get(String.valueOf(corpGrpProfileDTO.getProfileId()));
                groupProfileDTO.setGrpCount(grpCnt != null ? grpCnt : 0);
                groupProfileDTO.setCreateTimeStamp(corpGrpProfileDTO.getCreateTimeStamp());
                groupProfileDTO.setUpdateTimeStamp(corpGrpProfileDTO.getUpdateTimeStamp());
                groupProfileDTO.setGrpProfileStatus(corpGrpProfileDTO.getGrpProfileStatus());
                groupProfileDTO.setFeatureAllowed(corpGrpProfileDTO.getFeatureAllowed());
                groupProfileDTO.setOverrideDND(corpGrpProfileDTO.getOverrideDnd());
                groupProfileDTO.setGrpShared(corpGrpProfileDTO.getGrpShared());
                List<KnCorpSharedCorpInfo> sharedCorpInfoList = corpGrpProfileDTO.getSharedCorpList();
                if (sharedCorpInfoList != null && !sharedCorpInfoList.isEmpty()) {
                    List<KnXDMCorpGrpSharedCorpListDTO> xdmCorpGrpSharedCorpList = new ArrayList<>();
                    sharedCorpInfoList.forEach(
                            sharedCorpInfo -> {
                                KnXDMCorpGrpSharedCorpListDTO xdmDto = new KnXDMCorpGrpSharedCorpListDTO();
                                xdmDto.setExtCorpID(sharedCorpInfo.getExtCorpId());
                                xdmDto.setIntCorpID(sharedCorpInfo.getCorpId());
                                xdmCorpGrpSharedCorpList.add(xdmDto);
                            }

                    );
                    groupProfileDTO.setSharedCorpList(xdmCorpGrpSharedCorpList);
                }

                xdmCorpGroupProfileDTOList.add(groupProfileDTO);
            }

            xdmRespDto.setGroupProfile(xdmCorpGroupProfileDTOList);
            xdmRespDto.setTotalGroupProfilesCount(respDto.getTotalGroupProfilesCount());
            xdmRespDto.setTotalGroupProfilesUnfilteredCount(respDto.getTotalGroupProfilesUnfilteredCount());
        }

        xdmRespDto.setCorpId(xdmRequestDto.getCorpId());

        knLogger.debug(methodName, "respDto - ", respDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO modifyGroupProfile(KnXDMCorpGroupProfileRequestDTO xdmRequestDto, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "modifyGroupProfile()";
        KnIPCorpGorupProfileDTO groupProfileIPDto = new KnIPCorpGorupProfileDTO();
        knLogger.debug(methodName, "xdmRequestDto :: ", xdmRequestDto);
        groupProfileIPDto.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        groupProfileIPDto.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        groupProfileIPDto.setHierarchyType(xdmRequestDto.getHierarchyType());
        groupProfileIPDto.setClientType(xdmRequestDto.getClientType());
        groupProfileIPDto.setGrpProfileName(xdmRequestDto.getGrpProfileName());
        groupProfileIPDto.setGrpProfileId(xdmRequestDto.getGrpProfileId());
        groupProfileIPDto.setNewGrpProfileName(xdmRequestDto.getNewGrpProfileName());
        groupProfileIPDto.setGrpAvatar(xdmRequestDto.getGrpAvatar());
        groupProfileIPDto.setGrpServiceType(xdmRequestDto.getGrpServiceType());
        groupProfileIPDto.setGrpOSMListId(xdmRequestDto.getGrpOSMListId());
        groupProfileIPDto.setAudioCutIn(xdmRequestDto.getAudioCutIn());
        groupProfileIPDto.setMcxGroup(xdmRequestDto.getMcxGroup());
        groupProfileIPDto.setGrpType(xdmRequestDto.getGrpType());
        groupProfileIPDto.setOverrideDND(xdmRequestDto.getOverrideDND());
        groupProfileIPDto.setGrpShared(xdmRequestDto.getGrpShared());
        groupProfileIPDto.setUgwInterop(xdmRequestDto.getUgwInterop());

        List<KnXDMCorpGrpSharedCorpListDTO> sharedList = xdmRequestDto.getSharedCorpList();
        if (sharedList != null) {
            knLogger.debug(methodName, "sharedList - ", sharedList);
            List<KnCorpSharedCorpInfo> sharedCorpInfoDTOs = new ArrayList<>(sharedList.size());
            for (KnXDMCorpGrpSharedCorpListDTO xdmSharedCorpInfoDTO : sharedList) {
                KnCorpSharedCorpInfo sharedCorpInfo = new KnCorpSharedCorpInfo();
                sharedCorpInfo.setCorpId(xdmSharedCorpInfoDTO.getIntCorpID());
                sharedCorpInfo.setExtCorpId(xdmSharedCorpInfoDTO.getExtCorpID());
                //GroupMember propertiees are not supported via this api hence skipping them
                sharedCorpInfoDTOs.add(sharedCorpInfo);
            }
            groupProfileIPDto.setSharedCorpList(sharedCorpInfoDTOs);
        }
        knLogger.debug(methodName, "groupProfileIPDto :: ", groupProfileIPDto);
        KnCorpResponseDTO respDto = corpClientIntf.modifyGroupProfile(groupProfileIPDto, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO deleteBulkCorpGroup(KnXDMDeleteBulkGroupReqDTO xdmRequestDto, KnPersisterTxn persisterTxn) {
        String methodName = "deleteBulkCorpGroup()";
        KnIPDeleteBulkCorpGrpDTO groupProfileIPDto = new KnIPDeleteBulkCorpGrpDTO();
        knLogger.debug(methodName, "xdmRequestDto :: ", xdmRequestDto);
        groupProfileIPDto.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        groupProfileIPDto.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        groupProfileIPDto.setHierarchyType(xdmRequestDto.getHierarchyType());
        groupProfileIPDto.setClientType(xdmRequestDto.getClientType());
        groupProfileIPDto.setGrpProfileName(xdmRequestDto.getProfileName());
        groupProfileIPDto.setGrpProfileId(xdmRequestDto.getProfileId());
        groupProfileIPDto.setGroupIdList(xdmRequestDto.getGroupIdList());
        knLogger.debug(methodName, "groupProfileIPDto :: ", groupProfileIPDto);
        KnCorpResponseDTO respDto = corpClientIntf.deleteGrpProfileGroupList(groupProfileIPDto, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    /**
     * Method to create bulk corporate groups from the request group profile.
     *
     * @param requestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnCorpResponseDTO deleteGrouProfile(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteGrouProfile()";

        KnIPCorpGorupProfileDTO groupProfileIPDto = new KnIPCorpGorupProfileDTO();

        if (!(requestDTO instanceof KnXDMCorpGroupProfileRequestDTO)) {
            knLogger.error(methodName, "Request dto is not of type KnXDMCorpGroupProfileRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpGroupProfileRequestDTO xdmRequestDto = (KnXDMCorpGroupProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "xdmRequestDto :: ", xdmRequestDto);
        groupProfileIPDto.setCorpId(Integer.valueOf(xdmRequestDto.getCorpId()));
        groupProfileIPDto.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        groupProfileIPDto.setHierarchyType(xdmRequestDto.getHierarchyType());
        groupProfileIPDto.setClientType(xdmRequestDto.getClientType());
        groupProfileIPDto.setGrpProfileId(xdmRequestDto.getGrpProfileId());
        groupProfileIPDto.setGrpProfileName(xdmRequestDto.getGrpProfileName());

        knLogger.debug(methodName, "groupProfileIPDto :: ", groupProfileIPDto);
        KnCorpResponseDTO respDto = corpClientIntf.deleteGroupProfile(groupProfileIPDto, persisterTxn);

        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnXDMCorpTrustMatrixRespDTO getSharedCorpTrustMatrix(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        knLogger.debug("Inside the corp mediator layer");
        String methodName = "getSharedCorpTrustMatrix(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDTO = (KnXDMCorpInfoRequestDTO) requestDTO;
        KnIPCorpAuthInfoDTO authInfoDto = new KnIPCorpAuthInfoDTO();
        authInfoDto.setExtCorpId(xdmRequestDTO.getExtCorpId());
        // UCSPROVCONFIG-3193: Extract optional sourceHierarchy filter from request
        String sourceHierarchyFilter = xdmRequestDTO.getHierarchyId();
        if (sourceHierarchyFilter == null && xdmRequestDTO.getCustomParamMap() != null) {
            Object filterVal = xdmRequestDTO.getCustomParamMap().get("sourceHierarchy");
            if (filterVal instanceof String) {
                sourceHierarchyFilter = (String) filterVal;
            }
        }
        // Treat empty string as no filter
        if (sourceHierarchyFilter != null && sourceHierarchyFilter.trim().isEmpty()) {
            sourceHierarchyFilter = null;
        }
        knLogger.debug(methodName, "Retrieving getSharedCorpTrustMatrix - ", authInfoDto,
                " sourceHierarchyFilter=", sourceHierarchyFilter);
        KnCorpSharedList respDto = corpClientIntf.getSharedCorpTrustMatrix(authInfoDto, persisterTxn);
        knLogger.debug(methodName, "Returning shareMatrix details - ", respDto);
        KnXDMCorpTrustMatrixRespDTO response = new KnXDMCorpTrustMatrixRespDTO();
        populateXdmResponse(response, respDto);
        // UCSPROVCONFIG-3193: Echo the sourceHierarchy filter in the response
        response.setSourceHierarchy(sourceHierarchyFilter);
        List<KnCorpTrustMatrixInfo> sharedList = new ArrayList<>();
        if (respDto.getList() != null) {
            knLogger.debug("Data is not null so adding to the DTO");
            for (KnCorpTrustMatrixDTO dto : respDto.getList()) // iterating through the DAO response
            {
                KnCorpTrustMatrixInfo info = new KnCorpTrustMatrixInfo(); //object in the response list
                info.setExtCorpId(dto.getExtCorpId());
                info.setSharedExtCorpId(dto.getSharedExtCorpId());
                info.setMemFeaturesAllowed(dto.getMemFeaturesAllowed());
                info.setSharingFeatureAllowed(dto.getSharingFeatureAllowed());//
                info.setRecId(dto.getRecId());
                if (dto.getRecId() != null) {
                    try {
                        List<KnSharedTrustMatrixHierarchyDTO> childRows =
                                KnGGCache.sharedTrustMatrixHierarchyDAO.getAllHierarchiesByRecId(dto.getRecId());

                        // Batch-fetch hierarchy names for all IDs in one DB call
                        Set<String> allHierarchyIds = new HashSet<>();
                        for (KnSharedTrustMatrixHierarchyDTO child : childRows) {
                            if (child.getOwnerHierarchyId() != null) allHierarchyIds.add(child.getOwnerHierarchyId());
                            if (child.getSharedHierarchyId() != null) allHierarchyIds.add(child.getSharedHierarchyId());
                        }
                        Map<String, String> idToNameMap = new HashMap<>();
                        if (!allHierarchyIds.isEmpty()) {
                            try {
                                String pttServerId = KnGenInfoUtil.getInstance().retrieveLocalXDMPttServerId();
                                KnXDMHierarchyDAO hierarchyDAO = new KnXDMHierarchyDAO(pttServerId);
                                idToNameMap = hierarchyDAO.getHierarchyNamesByIds(new ArrayList<>(allHierarchyIds), persisterTxn);
                            } catch (Exception ex) {
                                knLogger.warn(methodName, "Failed to fetch hierarchy names — names will be empty: ", ex.getMessage());
                            }
                        }

                        // UCSPROVCONFIG-3193: Build grouped hierarchyMap structure directly
                        // Group by ownerHierarchyId (sourceHieracy) → list of sharedHierarchyIds (targetHierachylist)
                        Map<String, List<KnTargetHierarchyInfo>> groupedMap = new LinkedHashMap<>();
                        for (KnSharedTrustMatrixHierarchyDTO child : childRows) {
                            // If sourceHierarchy filter is provided, only include matching owner hierarchies
                            if (sourceHierarchyFilter != null
                                    && !sourceHierarchyFilter.equals(child.getOwnerHierarchyId())) {
                                continue;
                            }
                            String srcId = child.getOwnerHierarchyId() != null ? child.getOwnerHierarchyId() : "";
                            String sharedId = child.getSharedHierarchyId() != null ? child.getSharedHierarchyId() : "";
                            String sharedName = idToNameMap.getOrDefault(sharedId, "");
                            groupedMap.computeIfAbsent(srcId, k -> new ArrayList<>())
                                    .add(new KnTargetHierarchyInfo(sharedId, sharedName));
                        }
                        List<KnSourceHierarchyMapInfo> hierarchyMapList = new ArrayList<>();
                        for (Map.Entry<String, List<KnTargetHierarchyInfo>> entry : groupedMap.entrySet()) {
                            KnSourceHierarchyMapInfo srcMap = new KnSourceHierarchyMapInfo();
                            srcMap.setSourceHieracy(entry.getKey());
                            srcMap.setTargetHierachylist(entry.getValue());
                            hierarchyMapList.add(srcMap);
                        }
                        info.setHierarchyMap(hierarchyMapList);
                    } catch (Exception e) {
                        knLogger.error(methodName, "Failed to fetch hierarchy mappings for recId=", dto.getRecId(), e);
                        info.setHierarchyMap(new ArrayList<>());
                    }
                } else {
                    info.setHierarchyMap(new ArrayList<>());
                }
                // If a sourceHierarchy filter was applied and no matching entries remain, skip this corp entry
                if (sourceHierarchyFilter != null && info.getHierarchyMap().isEmpty()) {
                    continue;
                }
                sharedList.add(info);
            }
            knLogger.debug("The final List is", sharedList);
            response.setSharedList(sharedList);
        }
        return response;
    }

    public KnCorpResponseDTO updateCorpTrustMatrix(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateCorpTrustMatrix(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDTO = (KnXDMCorpInfoRequestDTO) requestDTO;
        KnIPCorpAuthInfoDTO authInfoDto = new KnIPCorpAuthInfoDTO();
        authInfoDto.setExtCorpId(xdmRequestDTO.getExtCorpId());
        authInfoDto.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        knLogger.debug(methodName, "updateCorpTrustMatrix - ", authInfoDto);
        return corpClientIntf.updateCorpTrustMatrix(authInfoDto, persisterTxn);
    }

    public KnCorpResponseDTO deleteCorpTrustMatrix(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteCorpTrustMatrix(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDTO = (KnXDMCorpInfoRequestDTO) requestDTO;
        KnIPCorpAuthInfoDTO authInfoDto = new KnIPCorpAuthInfoDTO();
        authInfoDto.setExtCorpId(xdmRequestDTO.getExtCorpId());
        authInfoDto.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        knLogger.debug(methodName, "deleteCorpTrustMatrix - ", authInfoDto);
        return corpClientIntf.deleteCorpTrustMatrix(authInfoDto, persisterTxn);
    }

    /**
     * @param requestDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */

    public KnCorpSubsInfoRespDTO sendTrkMaterial(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "sendTrkMaterial(IXDMRequestDTO, KnPersisterTxn)";

        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnCorpSubsResquestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnCorpSubsResquestDTO subsInfoDTO = null;
        KnCorpSubsInfoRespDTO subsInfoRespDTO = new KnCorpSubsInfoRespDTO();
        KnCorpResponseDTO respDto = new KnCorpResponseDTO();
        knLogger.debug(methodName, "Received input DTO - ", requestDTO);
        subsInfoDTO = (KnCorpSubsResquestDTO) requestDTO;
        knLogger.debug(methodName, "received DTO for sendTrkMaterial - ", subsInfoDTO);
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        ipSubscriberInfoDTO.setAliasMdn(subsInfoDTO.getAliasMdn());
        ipSubscriberInfoDTO.setMdn(subsInfoDTO.getSubscriberMdn());
        ipSubscriberInfoDTO.setCorpId(subsInfoDTO.getCorpId());
        ipSubscriberInfoDTO.setHierarchyType(subsInfoDTO.getHierarchyType());
        ipSubscriberInfoDTO.setCustomParamMap(subsInfoDTO.getCustomParamMap());
        respDto = corpClientIntf.sendTrkMaterial(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "respDto - ", respDto);
        populateXdmResponse(subsInfoRespDTO, respDto);
        if (respDto != null) {
            subsInfoRespDTO.setMdn(respDto.getMdn());
            subsInfoRespDTO.setEmailAddress(respDto.getSubscriberEmailId());
            subsInfoRespDTO.setAliasMdn(respDto.getAliasMdn());
            subsInfoRespDTO.setUserId(respDto.getSubscriberEmailId());
        }

        knLogger.debug(methodName, "Exit ", subsInfoRespDTO);
        return subsInfoRespDTO;
    }

    public KnXDMCorpUserProfileRespDTO getAsyncOpStatus(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getAsyncOpStatus(IXDMRequestDTO, KnPersisterTxn)";
        if (!(requestDTO instanceof KnXDMCorpUserProfileRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpUserProfileRequestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        int corpid = Integer.parseInt(((KnXDMCorpUserProfileRequestDTO) requestDTO).getCorpId());
        KnXDMCorpUserProfileRespDTO xdmRespDto = new KnXDMCorpUserProfileRespDTO();
        KnXDMCorpUserProfileRequestDTO RequestDTO = (KnXDMCorpUserProfileRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", RequestDTO);
        KnIPUserProfileDTO ipUserProfileDTO = new KnIPUserProfileDTO();

        ipUserProfileDTO.setHierarchyType(RequestDTO.getHierarchyType());
        ipUserProfileDTO.setCustomParamMap(RequestDTO.getCustomParamMap());
        ipUserProfileDTO.setCorpId(RequestDTO.getCorpId());
        ipUserProfileDTO.setTxnList(RequestDTO.getTxnList());
        KnCorpResponseDTO respDto = corpClientIntf.getAsyncOpStatus(ipUserProfileDTO, persisterTxn);
        populateXdmResponse(xdmRespDto, respDto);
        knLogger.debug(methodName, "Exit ", xdmRespDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        if (respDto.getJobStatus() != null) {
            List<KnCorpAsyncJobStatusInfo> jobStatus = new ArrayList<>();
            for (KnAsyncJobDTO obj : respDto.getJobStatus()) {
                if (obj.getCorpId() != corpid) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.TRANSACTION_ID_DOES_NOT_BELONGS_TO_CORP,
                            "Shared corp cannot add external contact to the group", obj.getTxnId(), "");
                }
                KnCorpAsyncJobStatusInfo status = new KnCorpAsyncJobStatusInfo();
                status.setCreateTS(obj.getCreationTime());
                status.setOperationType(String.valueOf(obj.getOpType()));
                status.setTxnId(obj.getTxnId());
                status.setUpdateTS(obj.getUpdationTime());
                status.setTxnStatus(String.valueOf(obj.getOpStatus()));
                jobStatus.add(status);
            }
            xdmRespDto.setJobStatus(jobStatus);
        }
        knLogger.debug("returning the obj" + xdmRespDto.getJobStatus());
        return xdmRespDto;
    }

    public IXDMResponseDTO validateSubscrClient(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException, KnException {
        String methodName = "validateSubscrClient(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpClientActResponseDTO resp = new KnXDMCorpClientActResponseDTO();
        IXDMResponseDTO responseDTO = new KnXDMCorpRespDTO();

        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnCorpSubsResquestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        knLogger.debug("input to  the validator" + (requestDTO instanceof KnCorpSubsResquestDTO));
        KnCorpSubsResquestDTO subsResquestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setMdnList(subsResquestDTO.getMdnList());

        ipSubscriberInfoDTO.setCustomParamMap(subsResquestDTO.getCustomParamMap());
        knLogger.debug(methodName, "subsResquestDTO.getServiceAuthStatus()", subsResquestDTO.getServiceAuthStatus());
        knLogger.debug(methodName, "Calling corp library for updateCorpSubscriber");
        KnCorpResponseDTO respDTO = corpClientIntf.validateSubscrClient(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Calling corp library for updateCorpSubscriber" + respDTO);
        if (null != respDTO) {
            responseDTO.setResponseCode(respDTO.getStatusCode());
            responseDTO.setResponseStatus(respDTO.getStatus());
            responseDTO.setResponseDetails(respDTO.getFailureDetails());
        }
        return responseDTO;
    }

    public IXDMResponseDTO validateGetSubscrClient(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException, KnException {
        String methodName = "validateGetSubscrClient(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpClientActResponseDTO resp = new KnXDMCorpClientActResponseDTO();
        IXDMResponseDTO responseDTO = new KnXDMCorpRespDTO();

        if (!(requestDTO instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnCorpSubsResquestDTO, requestDTO ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        knLogger.debug("input to  the validator" + (requestDTO instanceof KnCorpSubsResquestDTO));
        KnCorpSubsResquestDTO subsResquestDTO = (KnCorpSubsResquestDTO) requestDTO;
        KnIPSubscriberInfoDTO ipSubscriberInfoDTO = new KnIPSubscriberInfoDTO();
        ipSubscriberInfoDTO.setCorpId(subsResquestDTO.getCorpId());
        ipSubscriberInfoDTO.setMdnList(subsResquestDTO.getMdnList());

        ipSubscriberInfoDTO.setCustomParamMap(subsResquestDTO.getCustomParamMap());
        knLogger.debug(methodName, "subsResquestDTO.getServiceAuthStatus()", subsResquestDTO.getServiceAuthStatus());
        knLogger.debug(methodName, "Calling corp library for validateGetSubscrClient");
        KnCorpResponseDTO respDTO = corpClientIntf.validateGetSubscrClient(ipSubscriberInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Calling corp library for validateGetSubscrClient" + respDTO);
        if (respDTO != null) {
            responseDTO.setResponseCode(respDTO.getStatusCode());
            responseDTO.setResponseStatus(respDTO.getStatus());
            responseDTO.setResponseDetails(respDTO.getFailureDetails());
        }
        return responseDTO;
    }

    public KnCorpResponseDTO groupNotifyOnActiveFsChange(int corpId, String mdn
            , String newActiveFS, String oldActiveFS, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        KnCorpResponseDTO respDto;
        try {
            String methodName = "groupNotifyOnActiveFsChange()";
            knLogger.debug(methodName, "Req received for - ", KnGDPRTemplate.mdn(mdn));
            respDto = corpClientIntf.groupNotifyOnActiveFsChange(corpId, mdn, newActiveFS, oldActiveFS, persisterTxn);
            knLogger.debug(methodName, "response - ", respDto);
        } catch (Exception e) {
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INTERNAL_ERROR, "Internal Server Error");
        }
        return respDto;
    }

    public KnCorpGroupInfoRespDTO getCorpGrpLmrExtn(KnIPCorpGroupInfoDTO requestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "getCorpGrpLmrExtn(KnIPCorpGroupDTO, KnPersisterTxn)";

        KnCorpGroupInfoRespDTO respDto = corpClientIntf.getCorpGrpLmrExtn(requestDTO, persisterTxn);
        knLogger.debug(methodName, "get grp lmr response - ", respDto);
        return respDto;
    }


    public KnCorpResponseDTO modifyGroupsUGWConfig(IXDMRequestDTO groupRequestDto, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "modifyGroupsUGWConfig(IXDMRequestDTO, KnPersisterTxn)";
        if (!(groupRequestDto instanceof KnXDMCorpGroupInfoRequestDTO)) {
            knLogger.error(methodName, "groupRequestDto not of type KnXDMCorpGroupInfoRequestDTO - requestDTO - ",
                    groupRequestDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpResponseDTO respDto = null;
        KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
        KnXDMCorpGroupInfoRequestDTO xdmRequestDto = (KnXDMCorpGroupInfoRequestDTO) groupRequestDto;

        groupInfoDTO.setCorpId(Integer.parseInt(xdmRequestDto.getCorpId()));
        groupInfoDTO.setGroupId(Integer.parseInt(xdmRequestDto.getGroupId()));
        groupInfoDTO.setUgwConfig(xdmRequestDto.getUgwConfig());

        respDto = corpClientIntf.modifyGroupsUGWConfig(groupInfoDTO, persisterTxn);
        knLogger.debug(methodName, " respDto:", respDto);
        return respDto;
    }

    public KnXDMCorpGroupInfoRespDTO getMdnAuthorizationForGroupId(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMdnAuthorizationForGroupId(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpSubscInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - requestDTO - ");
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpGroupInfoRespDTO xdmRespDto = new KnXDMCorpGroupInfoRespDTO();
        KnXDMCorpSubscInfoRequestDTO xdmRequestDTO = (KnXDMCorpSubscInfoRequestDTO) requestDTO;
        knLogger.debug(methodName, "DTO Passed : ", xdmRequestDTO);
        KnIPCorpContactDTO contactListDTO = new KnIPCorpContactDTO();
        contactListDTO.setMdn(xdmRequestDTO.getSubscriberMdn());
        contactListDTO.setMcDataId(xdmRequestDTO.getMcDataId());
        contactListDTO.setMcVideoId(xdmRequestDTO.getMcVideoId());
        contactListDTO.setMcpttId(xdmRequestDTO.getMcpttId());
        contactListDTO.setGroupId(Integer.parseInt(xdmRequestDTO.getGroupId()));
        if (null != xdmRequestDTO.getGroupURIList() && !xdmRequestDTO.getGroupURIList().isEmpty())
            contactListDTO.setGroupURIList(xdmRequestDTO.getGroupURIList());
        //contactListDTO.setGroupURIList(xdmRequestDTO.getGroupURIList().stream().map(KnGeneralUtil::getGroupIdFromGroupURI).collect(Collectors.toList()));
        KnCorpGroupInfoRespDTO respDto = corpClientIntf.getMdnAuthorizationForGroupId(contactListDTO, persisterTxn);
        populateXdmResponse(xdmRespDto, respDto);
        if (respDto != null && respDto.getMdnAuthorized() != null && respDto.getMdnAuthorized()) {
            xdmRespDto.setMdnAuthorized(Boolean.TRUE);
        } else {
            xdmRespDto.setMdnAuthorized(Boolean.FALSE);
            xdmRespDto.setUnAuthorizedGroupURIList(respDto.getUnAuthorizedGroupURIList());
        }
        knLogger.debug(methodName, " mdn authorization : ", xdmRespDto.getMdnAuthorized());
        knLogger.debug(methodName, " Failed GroupIds : ", xdmRespDto.getUnAuthorizedGroupURIList());
        return xdmRespDto;
    }

    public KnCorpResponseDTO assignCommonContactList(IXDMRequestDTO sublistReqDto, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "assignCommonContactList(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(sublistReqDto instanceof KnXDMCorpCommonContactListRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSublistInfoRequestDTO - requestDTO - ",
                    sublistReqDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpCommonContactListRequestDTO xdmRequestDTO = (KnXDMCorpCommonContactListRequestDTO) sublistReqDto;
        KnIPCorpSublistSubscDistDTO subscDistDTO = new KnIPCorpSublistSubscDistDTO();
        subscDistDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        subscDistDTO.setMdnList(xdmRequestDTO.getAddedMdnList());
        subscDistDTO.setSublistIds(List.of(Integer.parseInt(xdmRequestDTO.getCommonSublistId())));
        subscDistDTO.setCommonSublistId(xdmRequestDTO.getCommonSublistId());
        knLogger.debug(methodName, "Create Sublist - ", sublistReqDto);
        KnCorpResponseDTO respDto = corpClientIntf.assignCommonContactList(subscDistDTO, persisterTxn);
        knLogger.debug(methodName, "After call to create sublist. Response - ", respDto);
        return respDto;
    }

    public KnXDMDeviceListRespDTO getDeviceList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getDeviceList(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDTO = (KnXDMCorpInfoRequestDTO) requestDTO;
        KnIPCorpInfoDTO infoDto = new KnIPCorpInfoDTO();
        infoDto.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        infoDto.setFilterType(xdmRequestDTO.getFilterType());
        infoDto.setFetchSize(xdmRequestDTO.getFetchSize());
        infoDto.setNextToken(xdmRequestDTO.getNextToken());
        knLogger.debug(methodName, "Retrieving DeviceList - ", infoDto);
        KnCorpDeviceListRespDTO respDto = corpClientIntf.getDeviceList(infoDto, persisterTxn);
        KnXDMDeviceListRespDTO xdmRespDto = new KnXDMDeviceListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        xdmRespDto.setDeviceInfoList(respDto.getDeviceInfoList());
        xdmRespDto.setCount(respDto.getCount());
        knLogger.debug(methodName, "Returning DeviceList - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMSubsStatsRespDTO getSubscriberStats(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getSubscriberStats(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDTO = (KnXDMCorpInfoRequestDTO) requestDTO;
        KnIPCorpInfoDTO infoDto = new KnIPCorpInfoDTO();
        infoDto.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        infoDto.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        infoDto.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "Retrieving Subscribers Statistics for infoDto - ", infoDto);
        KnCorpSubsStatsRespDTO respDto = corpClientIntf.getSubscriberStats(infoDto, persisterTxn);
        KnXDMSubsStatsRespDTO xdmRespDto = new KnXDMSubsStatsRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        xdmRespDto.setActivatedSubscribers(respDto.getActivatedSubscribers());
        xdmRespDto.setProvisionedSubscribers(respDto.getProvisionedSubscribers());
        xdmRespDto.setDeactivateSubscribers(respDto.getDeactivateSubscribers());
        xdmRespDto.setClientTypeSubscribers(respDto.getClientTypeSubscribers());

        knLogger.debug(methodName, "Returning Subscribers Statistics - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO unAssignCommonContactList(IXDMRequestDTO sublistReqDto, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "unAssignCommonContactList(IXDMRequestDTO, KnPersisterTxn)";
        if (!(sublistReqDto instanceof KnXDMCorpCommonContactListRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSublistInfoRequestDTO - requestDTO - ",
                    sublistReqDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpCommonContactListRequestDTO xdmRequestDTO = (KnXDMCorpCommonContactListRequestDTO) sublistReqDto;
        knLogger.info(methodName, " xdmRequestDTO:", xdmRequestDTO);
        KnIPCorpSublistSubscDistDTO subscDistDTO = new KnIPCorpSublistSubscDistDTO();
        subscDistDTO.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        subscDistDTO.setMdnList(xdmRequestDTO.getAddedMdnList());
        subscDistDTO.setCommonSublistId(xdmRequestDTO.getCommonSublistId());
        KnCorpResponseDTO respDto = corpClientIntf.unAssignCommonContactList(subscDistDTO, persisterTxn);
        knLogger.debug(methodName, "After call to create sublist. Response - ", respDto);
        return respDto;
    }

    public KnXDMDeviceDetailsRespDTO getDeviceDetails(IXDMRequestDTO sublistReqDto, KnPersisterTxn persisterTxn)
            throws KnXDMServerException, KnDAOException {
        String methodName = "getDeviceDetails(IXDMRequestDTO, KnPersisterTxn)";
        if (!(sublistReqDto instanceof KnCorpSubsResquestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSublistInfoRequestDTO - requestDTO - ",
                    sublistReqDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnCorpSubsResquestDTO xdmRequestDTO = (KnCorpSubsResquestDTO) sublistReqDto;
        knLogger.info(methodName, " xdmRequestDTO:", xdmRequestDTO);
        KnIPDeviceInfoDTO subscDistDTO = new KnIPDeviceInfoDTO();
        subscDistDTO.setCorpId(xdmRequestDTO.getCorpId());
        subscDistDTO.setDeviceId(xdmRequestDTO.getDeviceId());
        KnCorpDeviceInfoRespDTO respDto = corpClientIntf.getDeviceDetails(subscDistDTO, persisterTxn);
        KnXDMDeviceDetailsRespDTO xdmRespDto = new KnXDMDeviceDetailsRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        xdmRespDto.setDeviceInfo(respDto.getDeviceInfo());
        xdmRespDto.setDeviceAddlInfo(respDto.getDeviceAddlInfo());
        knLogger.debug(methodName, "After call to getDeviceDetails. Response - ", respDto);
        knLogger.debug(methodName, "Returning DeviceDetails - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMGroupStatsRespDTO getGroupStats(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getGroupStats(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDTO = (KnXDMCorpInfoRequestDTO) requestDTO;
        KnIPCorpInfoDTO infoDto = new KnIPCorpInfoDTO();
        infoDto.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        infoDto.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        knLogger.debug(methodName, "Retrieving getGroupStats - ", infoDto);
        KnGroupStatsRespDTO respDto = corpClientIntf.getGroupStats(infoDto, persisterTxn);
        KnXDMGroupStatsRespDTO xdmRespDto = new KnXDMGroupStatsRespDTO(); //KnXDMGroupStatsRespDTO
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == xdmRespDto.getResponseStatus()) {
            knLogger.debug(methodName, "Operation Failed");
            throw new KnXDMServerException(xdmRespDto.getResponseCode(),
                    xdmRespDto.getResponseMessage());
        }
        xdmRespDto.setGroupStats(respDto.getGroupStats());
        xdmRespDto.setGroupType(respDto.getGroupType());

        knLogger.debug(methodName, "Returning DeviceList - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnXDMDevStatsRespDTO getDeviceStats(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getDeviceStats(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDTO = (KnXDMCorpInfoRequestDTO) requestDTO;
        //KnIPCorpInfoDTO infoDto = new KnIPCorpInfoDTO();
        //infoDto.setCorpId(Integer.valueOf(xdmRequestDTO.getCorpId()));
        //knLogger.debug(methodName, "Retrieving DeviceList - ", infoDto);
        KnCorpDeviceStatsRespDTO respDto = corpClientIntf.getDeviceStats(xdmRequestDTO.getCorpId(), persisterTxn);
        KnXDMDevStatsRespDTO xdmRespDto = new KnXDMDevStatsRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        xdmRespDto.setDeviceCountByDeviceType(respDto.getDeviceCountByDeviceType());
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }

        knLogger.debug(methodName, "Returning DeviceList - ", xdmRespDto);
        return xdmRespDto;
    }


    public KnXDMGetCorpFSResponse getCorporateFS(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateCorpAdminFS()";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpSubscInfoRequestDTO - xdmRequestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpInfoRequestDTO xdmRequestDTO = (KnXDMCorpInfoRequestDTO) requestDTO;
        KnIPSubscriberInfoDTO ipSubscrFeatureInfoDTO = new KnIPSubscriberInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipSubscrFeatureInfoDTO.setCorpId(xdmRequestDTO.getCorpId());
        }
        knLogger.debug(methodName, "getCorporateFS for corpId ", xdmRequestDTO.getCorpId());
        KnCorpGetCorpFSResponse respDto = corpClientIntf.getCorporateFS(ipSubscrFeatureInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Update subscriber corp admin feature sets Response - ", respDto);
        KnXDMGetCorpFSResponse knXDMGetCorpFSResponse = new KnXDMGetCorpFSResponse();
        populateXdmResponse(knXDMGetCorpFSResponse, respDto);
        knXDMGetCorpFSResponse.setCorporateFS(respDto);

        knLogger.debug(methodName, "Returning get corp Group Details response - ", respDto);
        return knXDMGetCorpFSResponse;
    }

    public KnCorpResponseDTO updateCorporateFS(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "KnXDMCorpInfoRequestDTO()";
        if (!(requestDTO instanceof KnXDMCorporateFSRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - xdmRequestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorporateFSRequestDTO xdmRequestDTO = (KnXDMCorporateFSRequestDTO) requestDTO;
        KnIPCorpInfoDTO reqDTO = new KnIPCorpInfoDTO();
        reqDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        reqDTO.setPttRecording(xdmRequestDTO.getPttRecording());
        reqDTO.setDataRecording(xdmRequestDTO.getDataRecording());
        reqDTO.setVideoRecording(xdmRequestDTO.getVideoRecording());
        reqDTO.setSelfDnDPrivilege(xdmRequestDTO.getSelfDnDPrivilege());
        reqDTO.setLargeAgencyDispatch(xdmRequestDTO.getLargeAgencyDispatch());
        KnCorpResponseDTO respDto = corpClientIntf.updateCorporateFS(reqDTO, persisterTxn);
        knLogger.debug(methodName, "Update  corp admin feature sets Response - ", respDto);
        KnXDMGetCorpFSResponse knXDMGetCorpFSResponse = new KnXDMGetCorpFSResponse();
        populateXdmResponse(knXDMGetCorpFSResponse, respDto);

        knLogger.debug(methodName, "Returning get corp Group Details response - ", respDto);
        return respDto;
    }

    public KnXDMCorpGroupInfoRespDTO modifyBulkGroupProperties(IXDMRequestDTO groupRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "modifyBulkGroupProperties(IXDMRequestDTO, KnPersisterTxn)";
        knLogger.info(methodName, " groupRequestDTO: ", groupRequestDTO);
        //Step:
        if (!(groupRequestDTO instanceof KnXDMCorpGroupInfoRequestDTO)) {
            knLogger.error(methodName, "groupRequestDTO not of type KnXDMCorpGroupInfoRequestDTO - requestDTO - ",
                    groupRequestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpGroupInfoRequestDTO xdmRequestDto = (KnXDMCorpGroupInfoRequestDTO) groupRequestDTO;
        KnIPCorpGroupInfoDTO groupDTO = new KnIPCorpGroupInfoDTO();

        if (null != xdmRequestDto.getCorpId()) {
            groupDTO.setCorpId(Integer.parseInt(xdmRequestDto.getCorpId()));
        }
        groupDTO.setClientType(xdmRequestDto.getClientType());
        groupDTO.setGroupPropertyList(xdmRequestDto.getGroupPropertyList());

        knLogger.debug(methodName, "KnXDMCorpGroupInfoRequestDTO corpId ", xdmRequestDto.getCorpId());
        groupDTO.setCustomParamMap(xdmRequestDto.getCustomParamMap());
        groupDTO.setHierarchyType(xdmRequestDto.getHierarchyType());

        knLogger.debug(methodName, "Get Corp Group Details request - ", groupDTO);
        KnCorpResponseDTO respDto = null;
        respDto = corpClientIntf.modifyBulkGroupProperties(groupDTO, persisterTxn);
        knLogger.debug(methodName, "Get Corp Group Details response - ", respDto);

        KnXDMCorpGroupInfoRespDTO xdmRespDto = new KnXDMCorpGroupInfoRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        knLogger.debug(methodName, "Returning modify Bulk Group Properties response - ", xdmRespDto);
        return xdmRespDto;
    }

    public KnCorpResponseDTO deleteCorporateData(KnXDMCorpInfoDTO corpInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteCorporateData(IXDMRequestDTO, KnPersisterTxn)";
        knLogger.debug(methodName, " xdmRequestDTO :", corpInfo);
        return corpClientIntf.deleteCorporateData(corpInfo, persisterTxn);
    }


    public KnXDMCorpGroupListRespDTO getGroupsDetailsWithoutMembers(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "getGroupsDetailsWithoutMembers(IXDMRequestDTO, KnPersisterTxn)";
        //Step:
        if (!(requestDTO instanceof KnXDMCorpGroupDetailsListReqDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpGroupDetailsListReqDTO - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpGroupDetailsListReqDTO xdmRequestDTO = (KnXDMCorpGroupDetailsListReqDTO) requestDTO;
        KnIPCorpBulkGroupDTO corpBulkGroupDTO = new KnIPCorpBulkGroupDTO();

        var groupIds = new ArrayList<Integer>();
        if (xdmRequestDTO.getGroupDetailsReqList() != null) {
            for (var knXDMGroupDetailsReqDTO : xdmRequestDTO.getGroupDetailsReqList()) {
                Integer groupId = knXDMGroupDetailsReqDTO.getGroupId();
                groupIds.add(groupId);
            }
        }
        corpBulkGroupDTO.setGrpIdList(groupIds);
        KnCorpGroupListRespDTO respDto = corpClientIntf.getGroupsDetailsWithoutMembers(corpBulkGroupDTO, persisterTxn);
        knLogger.debug(methodName, "After call to Group Details - ", respDto);
        var xdmRespDto = new KnXDMCorpGroupListRespDTO();
        populateXdmResponse(xdmRespDto, respDto);
        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
            return xdmRespDto;
        }
        var groupList = respDto.getGroupList();
        var xdmGroupList = new ArrayList<KnXDMCorpGroupDTO>();
        if (groupList != null) {
            for (KnCorpGroupInfoDTO group : groupList) {
                KnXDMCorpGroupDTO xdmGroupdto = new KnXDMCorpGroupDTO();
                xdmGroupdto.setGroupId(String.valueOf(group.getGroupId()));
                xdmGroupdto.setETag(String.valueOf(group.getETag()));
                xdmGroupList.add(xdmGroupdto);
            }
        }
        xdmRespDto.setGroupList(xdmGroupList);
        return xdmRespDto;
    }

    public KnCorpResponseDTO setCATAccessPermission(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "setCATAccessPermission()";
        if (!(requestDTO instanceof KnXDMCatPermissionSetRequestDTO)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMCorpInfoRequestDTO - xdmRequestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCatPermissionSetRequestDTO xdmRequestDTO = (KnXDMCatPermissionSetRequestDTO) requestDTO;
        KnIPCatPermissionSetDTO reqDTO = new KnIPCatPermissionSetDTO();
        reqDTO.setExtCorpId(xdmRequestDTO.getExtCorpId());
        reqDTO.setCatAccessPermSet(xdmRequestDTO.getCatAccessPermSet());
        reqDTO.setCatAccessPermUpdateTS(xdmRequestDTO.getCatAccessPermUpdateTS());
        Map<String, Object> customParams = xdmRequestDTO.getCustomParamMap();
        if (null != customParams && null != xdmRequestDTO.getCustomParamMap().get(com.kodiak.common.resources.KnConstants.IDTYPE)
                && customParams.containsKey(com.kodiak.common.resources.KnConstants.IDLIST) &&
                null != customParams.get(com.kodiak.common.resources.KnConstants.IDLIST)) {
            int idType = Integer.parseInt((String) xdmRequestDTO.getCustomParamMap().get(com.kodiak.common.resources.KnConstants.IDTYPE));
            List<String> idListStr = (List<String>) customParams.get(com.kodiak.common.resources.KnConstants.IDLIST);
            reqDTO.setIdType(idType);
            reqDTO.setExtIdList(idListStr);
        }
        KnCorpResponseDTO respDto = corpClientIntf.setCATAccessPermission(reqDTO, persisterTxn);
        knLogger.debug(methodName, "Update  corp admin feature sets Response - ", respDto);
        KnXDMGetCorpFSResponse knXDMGetCorpFSResponse = new KnXDMGetCorpFSResponse();
        populateXdmResponse(knXDMGetCorpFSResponse, respDto);
        knLogger.debug(methodName, "Returning get corp Group Details response - ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO setUserProfileEmergencyAttributes(KnXDMCorpUserProfileRespDTO userProfile, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "setSubsEmergencyAttributes()";
        KnXDMEmergencyConfig emergencyConfig = userProfile.getUserProfileInfo().getEmergencyAttributes();
        knLogger.info(methodName, "emergencyConfig cb:", emergencyConfig);
        KnIPEmergencyInfoDTO ipEmergencyInfoDTO = new KnIPEmergencyInfoDTO();

        if (userProfile.getCorpId() != null) {
            ipEmergencyInfoDTO.setCorpId(Integer.parseInt(userProfile.getCorpId()));
        }
        ipEmergencyInfoDTO.setMdn(userProfile.getProfileMdn());
        ipEmergencyInfoDTO.setEmergInitPermission(Integer.parseInt(emergencyConfig.getEmergInitPermission()));
        ipEmergencyInfoDTO.setEmergCallType(Integer.parseInt(emergencyConfig.getEmergCallType()));
        ipEmergencyInfoDTO.setEmergDestType(Integer.parseInt(emergencyConfig.getEmergDestType()));
        Set<KnXDMEmergencyDestAttributes> destAttributes = new HashSet<>();
        destAttributes = emergencyConfig.getEmergDestAttributes();
        for (KnXDMEmergencyDestAttributes emgDestAttr : destAttributes) {
            if (emgDestAttr != null && emgDestAttr.getDestAttributeCategory() != null) {
                //DestCategory 1 - Primary, 2 - Secondary
                if (emgDestAttr.getDestAttributeCategory().equals("1")) {
                    //DestURI GroupId or ContactId
                    ipEmergencyInfoDTO.setPriDestination(emgDestAttr.getDestAttributeUri());
                } else {
                    ipEmergencyInfoDTO.setSecDestination(emgDestAttr.getDestAttributeUri());
                }
                //DestType 1 - Group, 2 - Contact
                //emgDestAttr.getDestType();
            }
        }
        ipEmergencyInfoDTO.setEmergCancelPermission(Integer.parseInt(emergencyConfig.getEmergCancelPermission()));
        if (null != emergencyConfig.getEmergOriginBitSet()) {
            ipEmergencyInfoDTO.setEmergOriginBitSet(Integer.parseInt(emergencyConfig.getEmergOriginBitSet()));
        }
        if (null != emergencyConfig.getEmergTermBitSet()) {
            ipEmergencyInfoDTO.setEmergTermBitSet(Integer.parseInt(emergencyConfig.getEmergTermBitSet()));
        }
        if (null != emergencyConfig.getEmergLMRBehavior()) {
            ipEmergencyInfoDTO.setEmergLMRBehavior(Integer.parseInt(emergencyConfig.getEmergLMRBehavior()));
        }
        if (null != emergencyConfig.getEmergConfigTimer()) {
            ipEmergencyInfoDTO.setEmergConfigTimer(emergencyConfig.getEmergConfigTimer());
        }
        //ipEmergencyInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "setSubsEmergencyAttributes input dto - ", ipEmergencyInfoDTO);
        return corpClientIntf.setUserProfileEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
    }

    public KnCorpResponseDTO setUserProfileAssignTargetPermsToAllAU(KnXDMCorpUserProfileRespDTO userProfile, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "setUserProfileAssignTargetPermsToAllAU()";
        KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO = new KnIPAuthUserPermissionInfoDTO();
        if (userProfile.getCorpId() != null) {
            ipAuthUserPermissionInfoDTO.setCorpId(Integer.parseInt(userProfile.getCorpId()));
        }
        KnCorpResponseDTO respDto = null;
        KnCorpSubsResquestDTO xdmRequestDTO = new KnCorpSubsResquestDTO();
        xdmRequestDTO.setCorpId(userProfile.getCorpId());
        xdmRequestDTO.setSubscriberMdn(userProfile.getMdn());
        xdmRequestDTO.setUpmFlag(false);
        KnXDMCorpAuthUserPermissionRespDTO auPerm = getAllAuthorizedMdnList(xdmRequestDTO, persisterTxn);
        Collection<KnTargetMdnPermissionBitInfo> auList = auPerm.getTargetMdnPermissionBitInfo();

        if (auList != null && !auList.isEmpty()) {
            for (KnTargetMdnPermissionBitInfo au : auList) {
                KnCorpSubMCPTTInfoRequestDTO knCorpSubMCPTTInfoRequestDTO = new KnCorpSubMCPTTInfoRequestDTO();
                Collection<KnTargetMdnPermissionBitInfo> targetMdnPermBitInfos = new ArrayList<>();
                KnTargetMdnPermissionBitInfo knTargetMdnPermBitInfo = new KnTargetMdnPermissionBitInfo();
                knTargetMdnPermBitInfo.setAmbientListening(au.getAmbientListening());
                knTargetMdnPermBitInfo.setDiscreteListening(au.getDiscreteListening());
                knTargetMdnPermBitInfo.setUserCheck(au.getUserCheck());
                knTargetMdnPermBitInfo.setUserEnable(au.getUserEnable());
                knTargetMdnPermBitInfo.setEmergPermission(au.getEmergPermission());
                knTargetMdnPermBitInfo.setMcVideoUnConfirmedPull(au.getMcVideoUnConfirmedPull());
                knTargetMdnPermBitInfo.setMdn(userProfile.getProfileMdn());
                targetMdnPermBitInfos.add(knTargetMdnPermBitInfo);

                knCorpSubMCPTTInfoRequestDTO.setCorpId(userProfile.getCorpId());
                knCorpSubMCPTTInfoRequestDTO.setAuthorizedMdn(au.getMdn());
                knCorpSubMCPTTInfoRequestDTO.setTargetMdnPermissionBitInfoList(new ArrayList<>());
                knCorpSubMCPTTInfoRequestDTO.setTargetProfileMdnPermissionBitInfoList(targetMdnPermBitInfos);
                knCorpSubMCPTTInfoRequestDTO.setUpmCall(true);
                knCorpSubMCPTTInfoRequestDTO.setAllAuTask(true);
                knLogger.debug(methodName, "setting all AU with profile mdn as TU:", knCorpSubMCPTTInfoRequestDTO);
                respDto = userProfileAssignTargetPermsToAllAU(knCorpSubMCPTTInfoRequestDTO, persisterTxn);
                if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
                    knLogger.debug(methodName, "assign permission to all AU Operation Failed");
                    throw new KnXDMServerException(respDto.getStatusCode(), respDto.getMessage());
                }
            }
            knLogger.debug(methodName, "Done with Set Target Permission for all AU's");
        }
        knLogger.debug(methodName, "Returning setTargetPermissions response - ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO setUserProfileContact(KnXDMCorpUserProfileRespDTO userProfile, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "setUserProfileContact(KnXDMCorpUserProfileRespDTO,persisterTxn)";
        knLogger.debug(methodName, " userProfileDetails in assignCall: ", userProfile);
        KnIPCorpSubscContactListDTO contactListDTO = new KnIPCorpSubscContactListDTO();
        KnCorpResponseDTO respDto = null;
        if (userProfile != null) {
            Collection<String> addedMdnList = new ArrayList<>();
            Collection<String> removedMdnList = new ArrayList<>();
            //profile mdn to be set from mediator
            contactListDTO.setSubscriberMdn(userProfile.getProfileMdn());
            contactListDTO.setCorpId(Integer.parseInt(userProfile.getCorpId()));
            //contactListDTO.setETag(String.valueOf(-9999));
            contactListDTO.setAddedMdnList(addedMdnList);
            contactListDTO.setRemovedMdnList(removedMdnList);
            if (userProfile.getAddedSublistIds() != null) {
                contactListDTO.setAddedSublistIds(userProfile.getAddedSublistIds());
            }
            if (userProfile.getRemovedSublistIds() != null) {
                contactListDTO.setRemovedSublistIds(userProfile.getRemovedSublistIds());
            }
            knLogger.debug(methodName, "assignContact request:", contactListDTO);
            respDto = corpClientIntf.modifyBulkCorpSubscContacts(contactListDTO, persisterTxn);
            knLogger.debug("assignContact response:", respDto);
            if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
                knLogger.error(methodName, "assignContact Operation Failed");
                throw new KnXDMServerException(respDto.getStatusCode(), respDto.getMessage());
            }
        }
        knLogger.debug(methodName, "Returning setTargetPermissions response - ", respDto);
        return respDto;

    }


    public KnCorpResponseDTO modifyBulkCorpGroup(IXDMRequestDTO groupRequestDto, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "modifyBulkCorpGroup(IXDMRequestDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "groupRequestDto  - ", groupRequestDto);
        //Step:
        if (!(groupRequestDto instanceof KnXDMBulkCorpGroupInfoRequestDTO)) {
            knLogger.error(methodName, "groupRequestDto not of type KnXDMCorpGroupInfoRequestDTO - requestDTO - ",
                    groupRequestDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpMediatorHelper corpMediatorHelper = new KnXDMCorpMediatorHelper();

        KnXDMBulkCorpGroupInfoRequestDTO xdmRequestDto = (KnXDMBulkCorpGroupInfoRequestDTO) groupRequestDto;
        if (xdmRequestDto.getGroupInfoMap() == null || xdmRequestDto.getGroupInfoMap().isEmpty()) {
            knLogger.error(methodName, "invalid request  - ", xdmRequestDto);
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        var bulkGroupInfoDTO = new HashMap<Integer, KnIPCorpGroupInfoDTO>();

        KnIPCorpGroupInfoDTO ipCorpGroupInfoDTO = null;
        Set<KnCorpContactDTO> addedMdnList = null;
        Collection<KnCorpGroupMemberDTO> modifiedMembersList = new ArrayList<KnCorpGroupMemberDTO>();

        for (var entry : xdmRequestDto.getGroupInfoMap().entrySet()) {
            Integer groupId = entry.getKey();
            KnXDMCorpGroupInfoDTO corpGroupInfoDTO = entry.getValue();
            ipCorpGroupInfoDTO = corpMediatorHelper.getIpCorpGroupInfoDTO(corpGroupInfoDTO, xdmRequestDto, groupId);

            addedMdnList = corpMediatorHelper.prepareContactList(corpGroupInfoDTO.getGroupMembers(), xdmRequestDto.getClientType());
            ipCorpGroupInfoDTO.setAddedMemberDTOMdns(addedMdnList);
            modifiedMembersList = corpMediatorHelper.prepareModifiedMemList(corpGroupInfoDTO.getModifiedMembers(), xdmRequestDto.getClientType());
            ipCorpGroupInfoDTO.setModifiedMembers(modifiedMembersList);
            LinkedList<String> removedMdnList = new LinkedList<>();
            if (entry.getValue().getRemovedMdnList() != null) {
                for (String mdn : entry.getValue().getRemovedMdnList()) {
                    removedMdnList.add(mdn);
                }
            }
            ipCorpGroupInfoDTO.setRemovedMemberMdns(removedMdnList);
            Collection<String> addedSublistIdStrs = entry.getValue().getAddedSublistIds();
            Collection<String> removedSublistIdStrs = entry.getValue().getRemovedSublistIds();
            Collection<Integer> addedSublistIds = new ArrayList<Integer>();
            if (addedSublistIdStrs != null) {
                for (String sublistId : addedSublistIdStrs) {
                    addedSublistIds.add(Integer.valueOf(sublistId));
                }
            }
            Collection<Integer> removedSublistIds = new ArrayList<Integer>();
            if (removedSublistIdStrs != null) {
                for (String sublistId : removedSublistIdStrs) {
                    removedSublistIds.add(Integer.valueOf(sublistId));
                }
            }
            ipCorpGroupInfoDTO.setAddedSublistIds(addedSublistIds);
            ipCorpGroupInfoDTO.setRemovedSublistIds(removedSublistIds);
            bulkGroupInfoDTO.put(groupId, ipCorpGroupInfoDTO);
        }

        KnIPCorpBulkGroupDTO ipCorpBulkGroupDTO = new KnIPCorpBulkGroupDTO();
        ipCorpBulkGroupDTO.setHierarchyType(xdmRequestDto.getHierarchyType());
        ipCorpBulkGroupDTO.setCorpId(Integer.parseInt(xdmRequestDto.getCorpId()));
        ipCorpBulkGroupDTO.setGrpIdList(new ArrayList<>(xdmRequestDto.getGroupInfoMap().keySet()));

        KnCorpBulkGrpBasicInfoRespDto bulkGrpBasicInfoDtoMap = corpClientIntf.getBulkBasicGrpInfo(ipCorpBulkGroupDTO,
                xdmRequestDto.getClientType(), xdmRequestDto.getUserProfileId(), persisterTxn);

        KnCorpResponseDTO respDto = new KnCorpResponseDTO();
        if (KnConstants.RESPONSE_STATUS.SUCCESS.value() != bulkGrpBasicInfoDtoMap.getStatus()
                || bulkGrpBasicInfoDtoMap.getCorpGrpBasicInfoRespDtoMap() == null || bulkGrpBasicInfoDtoMap.getCorpGrpBasicInfoRespDtoMap().isEmpty()) {
            respDto = new KnCorpResponseDTO();
            respDto.setStatus(bulkGrpBasicInfoDtoMap.getStatus());
            respDto.setStatusCode(bulkGrpBasicInfoDtoMap.getStatusCode());
            respDto.setMessage(bulkGrpBasicInfoDtoMap.getMessage());
            return respDto;
        }

        //Collecting group info based on group type
        var veryLargeGrpBasicInfoMap = new HashMap<Integer, KnCorpGrpBasicInfoDTO>();
        var broadCastGrpBasicInfoMap = new HashMap<Integer, KnCorpGrpBasicInfoDTO>();
        var otherGrpBasicInfoMap = new HashMap<Integer, KnCorpGrpBasicInfoDTO>();
        KnCorpResponseDTO mcxGroupResp = new KnCorpResponseDTO();
        knLogger.debug(methodName, "TODO:- ", bulkGrpBasicInfoDtoMap.getCorpGrpBasicInfoRespDtoMap());

        for (var entry : bulkGrpBasicInfoDtoMap.getCorpGrpBasicInfoRespDtoMap().entrySet()) {
            Integer grpId = entry.getKey();
            KnCorpGrpBasicInfoRespDto grpBasicInfoDto = entry.getValue();
            KnCorpGrpBasicInfoDTO grpBasicInfo = corpMediatorHelper.getCopGrpBasicInfoDTO(grpBasicInfoDto);

            bulkGroupInfoDTO.get(grpId).setOwnerCorpReq(grpBasicInfoDto.isOwnerCorpReq());
            bulkGroupInfoDTO.get(grpId).setUseProfileId(xdmRequestDto.getUserProfileId());
            bulkGroupInfoDTO.get(grpId).setGrpLocWatcherMap(xdmRequestDto.getGrpLocWatcherMap());

            if (VLARGE_GROUP.value() == grpBasicInfoDto.getMcxGrpInd()) {
                veryLargeGrpBasicInfoMap.put(grpId, grpBasicInfo);
            } else if (CORP_BROADCAST_GROUP_TYPE == grpBasicInfoDto.getGrpType()) {
                broadCastGrpBasicInfoMap.put(grpId, grpBasicInfo);
            } else {
                otherGrpBasicInfoMap.put(grpId, grpBasicInfo);
            }
        }

        if (!veryLargeGrpBasicInfoMap.isEmpty()) {
            var mcxGrpInfo = bulkGroupInfoDTO.entrySet().stream().filter(entry -> veryLargeGrpBasicInfoMap.containsKey(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            ipCorpBulkGroupDTO.setIpCorpGroupInfoDTOMap(mcxGrpInfo);
            knLogger.debug(methodName, "Modify MCX Group request - ", veryLargeGrpBasicInfoMap.keySet());
            mcxGroupResp = corpClientIntf.modifyBulkMCXGroup(ipCorpBulkGroupDTO, veryLargeGrpBasicInfoMap, persisterTxn);
            mcxGroupResp.setMcxGrpInd(1);
        }
        if (!broadCastGrpBasicInfoMap.isEmpty()) {
            var bcGrpInfo = bulkGroupInfoDTO.entrySet().stream().filter(entry -> broadCastGrpBasicInfoMap.containsKey(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            ipCorpBulkGroupDTO.setIpCorpGroupInfoDTOMap(bcGrpInfo);
            knLogger.debug(methodName, "Modify Broadcast Group request - ", broadCastGrpBasicInfoMap, "bcGrpInfo: ", bcGrpInfo);
            respDto = corpClientIntf.upmBulkModifyBCGroup(ipCorpBulkGroupDTO, broadCastGrpBasicInfoMap, persisterTxn);
        }
        if (!otherGrpBasicInfoMap.isEmpty()) {
            ipCorpBulkGroupDTO.setIpCorpGroupInfoDTOMap(bulkGroupInfoDTO);
            knLogger.debug(methodName, "Modify Standard/Dispatch Group request - ", otherGrpBasicInfoMap.keySet());
            ipCorpBulkGroupDTO.getIpCorpGroupInfoDTOMap().entrySet().removeIf(entry -> !otherGrpBasicInfoMap.containsKey(entry.getKey()));
            respDto = corpClientIntf.upmBulkModifyGroup(ipCorpBulkGroupDTO, otherGrpBasicInfoMap, persisterTxn);
        }
        if (!veryLargeGrpBasicInfoMap.isEmpty()) {
            for (var itr : veryLargeGrpBasicInfoMap.entrySet()) {
                respDto.setIsDispacherPresent(mcxGroupResp.getIsDispacherPresent());
                respDto.setProfileMDNs(mcxGroupResp.getProfileMDNs());
                respDto.setLocwatcherCount(mcxGroupResp.getLocwatcherCount());
                respDto.setUserProfileCount(mcxGroupResp.getUserProfileCount());
                respDto.setIsLocationDisabled(mcxGroupResp.getIsLocationDisabled());
                respDto.setMcxGrpInd(mcxGroupResp.getMcxGrpInd());
            }
        }
        respDto.setGroupIds(bulkGroupInfoDTO.keySet());
        respDto.setStatus(bulkGrpBasicInfoDtoMap.getStatus());
        respDto.setStatusCode(bulkGrpBasicInfoDtoMap.getStatusCode());
        respDto.setMessage(bulkGrpBasicInfoDtoMap.getMessage());
        knLogger.info(methodName, " Exit: ");
        return respDto;
    }

    public KnCorpResponseDTO userProfileAssignTargetPermsToAllAU(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "userProfileAssignTargetPermsToAllAU(IXDMRequestDTO,KnPersisterTxn)";
        KnCorpSubMCPTTInfoRequestDTO xdmRequestDTO = (KnCorpSubMCPTTInfoRequestDTO) requestDTO;
        KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO = new KnIPAuthUserPermissionInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipAuthUserPermissionInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipAuthUserPermissionInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        //base mdn target perm
        Collection<KnTargetMdnPermissionBitInfo> targetMdnPermissionBitInfos = xdmRequestDTO.getTargetMdnPermissionBitInfoList();
        Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = new ArrayList<>();
        targetMdnPermissionBitInfos.forEach(targetMdnPermissionBitInfo -> {
            KnTargetMdnPermBitInfo permBitInfo = new KnTargetMdnPermBitInfo();
            permBitInfo.setMdn(targetMdnPermissionBitInfo.getMdn());
            permBitInfo.setAmbientListening(targetMdnPermissionBitInfo.getAmbientListening());
            permBitInfo.setDiscreteListening(targetMdnPermissionBitInfo.getDiscreteListening());
            permBitInfo.setUserCheck(targetMdnPermissionBitInfo.getUserCheck());
            permBitInfo.setUserEnable(targetMdnPermissionBitInfo.getUserEnable());
            permBitInfo.setEmergPermission(targetMdnPermissionBitInfo.getEmergPermission());
            permBitInfo.setMcVideoUnConfirmedPull(targetMdnPermissionBitInfo.getMcVideoUnConfirmedPull());
            permBitInfo.setCommonContact(targetMdnPermissionBitInfo.getCommonContact());
            targetMdnPermBitInfos.add(permBitInfo);
        });
        ipAuthUserPermissionInfoDTO.setTargetMdnPermissionBitInfoList(targetMdnPermBitInfos);
        //profile mdn target perm
        Collection<KnTargetMdnPermissionBitInfo> targetProfileMdnPermissionBitInfos
                = xdmRequestDTO.getTargetProfileMdnPermissionBitInfoList();
        Collection<KnTargetMdnPermBitInfo> targetProfileMdnPermBitInfos = new ArrayList<>();
        if (targetProfileMdnPermissionBitInfos != null && !targetProfileMdnPermissionBitInfos.isEmpty()) {
            for (KnTargetMdnPermissionBitInfo profileMdns : targetProfileMdnPermissionBitInfos) {
                targetProfileMdnPermBitInfos.add(new KnTargetMdnPermBitInfo(profileMdns.getMdn()
                        , profileMdns.getSubsName()
                        , profileMdns.getAmbientListening(), profileMdns.getDiscreteListening()
                        , profileMdns.getUserCheck(), profileMdns.getUserEnable()
                        , profileMdns.getEmergPermission(), profileMdns.getMcVideoUnConfirmedPull()
                        , profileMdns.getPermBitSet(), profileMdns.getDiscreteEnabled()));
            }
        }
        ipAuthUserPermissionInfoDTO.setTargetProfileMdnPermissionBitInfoList(targetProfileMdnPermBitInfos);
        ipAuthUserPermissionInfoDTO.setAuthorizedMdn(xdmRequestDTO.getAuthorizedMdn());
        ipAuthUserPermissionInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        boolean isUpmCall = xdmRequestDTO.isUpmCall();
        ipAuthUserPermissionInfoDTO.setAllAuTask(xdmRequestDTO.isAllAuTask());
        knLogger.debug(methodName, "setTargetPermissions input dto - ", ipAuthUserPermissionInfoDTO, " isUpmCall:", isUpmCall);
        KnCorpResponseDTO respDto = corpClientIntf.setBulkTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Returning setTargetPermissions response - ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO cloneValidation(KnIPCorpSubscCloningListDTO contactListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "cloneContactsValidation(IXDMRequestDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "testing inside cloneContactsValidation- ", contactListDTO);
        KnCorpResponseDTO responseDTO = new KnCorpResponseDTO();
        responseDTO = corpClientIntf.cloneValidation(contactListDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", responseDTO);
        return responseDTO;
    }


    /**
     * This method is used to clone the contacts of a subscriber.
     * It takes a KnIPCorpSubscContactListDTO object as a parameter, which contains the necessary information for the operation.
     *
     * @param contactListDTO An instance of KnIPCorpSubscContactListDTO containing the details required for the cloning operation.
     *                       The DTO should contain the details of the source subscriber (whose contacts are to be cloned)
     *                       and the target subscriber (who will receive the cloned contacts).
     * @param persisterTxn   An instance of KnPersisterTxn for managing the database transaction.
     * @return An instance of KnCorpResponseDTO containing the response of the operation.
     * If the operation is successful, the response DTO will contain the details of the updated target subscriber.
     * If the operation fails, the response DTO will contain an error message.
     * @throws KnXDMServerException If there is an error during the operation, a KnXDMServerException is thrown.
     */
    public KnCorpResponseDTO cloneSubscriberContact(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "cloneSubscriberContact(KnXDMCorpUserProfileRespDTO,persisterTxn)";
        knLogger.debug(methodName, "contactListDTO: ", contactListDTO);
        KnCorpResponseDTO respDto = null;
        contactListDTO.setETag(String.valueOf(-9999));
        knLogger.debug("assignContact request:", contactListDTO);
        if (!contactListDTO.getRemoveCommonContactList().isEmpty()) {
            KnIPCorpSublistSubscDistDTO subscDistDTO = new KnIPCorpSublistSubscDistDTO();
            subscDistDTO.setCorpId(contactListDTO.getCorpId());
            subscDistDTO.setMdnList(Collections.singletonList(contactListDTO.getSubscriberMdn()));
            for (String itr : contactListDTO.getRemoveCommonContactList()) {
                subscDistDTO.setCommonSublistId(itr);
                knLogger.debug("unassignCommonContact request:", subscDistDTO);
                corpClientIntf.unAssignCommonContactListForCloningContact(subscDistDTO, persisterTxn);
            }
        }
        respDto = corpClientIntf.cloneCorpSubscContacts(contactListDTO, persisterTxn);
        knLogger.debug(methodName, "Returning modifyCorpSubscContactsAssignUpmCall response - ", respDto);
        return respDto;
    }

    /**
     * This method is used to clone the emergency attributes of a subscriber.
     * It takes a request DTO containing the details of the source and target subscribers,
     * and a transaction object for managing the database transaction.
     *
     * @param requestDTO   An instance of KnCorpSubsResquestDTO containing the details of the source and target subscribers.
     *                     The source subscriber is the one whose emergency attributes are to be cloned,
     *                     and the target subscriber is the one to receive the cloned attributes.
     * @param persisterTxn An instance of KnPersisterTxn for managing the database transaction.
     * @return An instance of KnCorpResponseDTO containing the response of the operation.
     * If the operation is successful, the response DTO will contain the details of the updated target subscriber.
     * If the operation fails, the response DTO will contain an error message.
     * @throws KnXDMServerException If there is an error during the operation, a KnXDMServerException is thrown.
     */
    public KnCorpResponseDTO cloneSubsEmergencyAttributes(KnCorpSubsResquestDTO requestDTO, KnPersisterTxn persisterTxn, KnIPCorpSubscCloningListDTO corpSubscCloningListDTO) throws KnXDMServerException {
        String methodName = "setSubsEmergencyAttributes()";
        KnCorpResponseDTO corpResponseDTO = new KnCorpResponseDTO();
        KnIPEmergencyInfoDTO ipEmergencyInfoDTO = corpSubscCloningListDTO.getIpEmergencyInfoDTO();
        if (ipEmergencyInfoDTO.getEmergInitPermission().equals(-99))
            return populate(corpResponseDTO);
        corpResponseDTO = corpClientIntf.setUserProfileEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
        knLogger.debug(methodName, "Returning setSubsEmergencyAttributes response - ", corpResponseDTO);
        return corpResponseDTO;
    }

    public KnIPEmergencyInfoDTO getKnIPEmergencyInfoDTO(KnCorpSubsResquestDTO requestDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getKnIPEmergencyInfoDTO()";
        knLogger.entry(methodName);
        KnIPEmergencyInfoDTO ipEmergencyInfoDTO = new KnIPEmergencyInfoDTO();
        if (requestDTO.getCorpId() != null) {
            ipEmergencyInfoDTO.setCorpId(Integer.parseInt(requestDTO.getCorpId()));
        }
        ipEmergencyInfoDTO.setCustomParamMap(requestDTO.getCustomParamMap());
        ipEmergencyInfoDTO.setHierarchyType(requestDTO.getHierarchyType());
        ipEmergencyInfoDTO.setMdn(requestDTO.getFromMdn());
        knLogger.debug(methodName, "getSubsEmergencyAttributes input dto - ", ipEmergencyInfoDTO);
        KnCorpUserEmergencyAttributesRespDTO respDtoForFromMdn = corpClientIntf.getSubsEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
        knLogger.debug(methodName, "KnCorpUserEmergencyAttributesRespDTO - respDtoForFromMdn", respDtoForFromMdn);

        ipEmergencyInfoDTO.setMdn(requestDTO.getToMdn());
        KnCorpUserEmergencyAttributesRespDTO respDtoForToMdn = corpClientIntf.getSubsEmergencyAttributes(ipEmergencyInfoDTO, persisterTxn);
        knLogger.debug(methodName, "KnCorpUserEmergencyAttributesRespDTO - respDtoForToMdn ", respDtoForToMdn);

        //calculate finalRespDto based on respDtoForFromMdn and respDtoForToMdn
        KnCorpUserEmergencyAttributesRespDTO finalRespDto = getFinalCorpUserEmergencyAttributesResp(respDtoForFromMdn, respDtoForToMdn);
        knLogger.debug(methodName, "KnCorpUserEmergencyAttributesRespDTO - finalRespDto ", finalRespDto);

        //set target mdn EmergencyAttributes
        ipEmergencyInfoDTO = new KnIPEmergencyInfoDTO();
        if (requestDTO.getCorpId() != null) {
            ipEmergencyInfoDTO.setCorpId(Integer.parseInt(requestDTO.getCorpId()));
        }
        ipEmergencyInfoDTO.setCustomParamMap(requestDTO.getCustomParamMap());
        KnCorpUserEmergencyAttributes userEmergencyAttributes = finalRespDto.getCorpUserEmergencyAttributes();
        ipEmergencyInfoDTO.setMdn(requestDTO.getToMdn());
        ipEmergencyInfoDTO.setEmergInitPermission(userEmergencyAttributes.getEmergInitPermission());
        ipEmergencyInfoDTO.setEmergCallType(userEmergencyAttributes.getEmergCallType());
        ipEmergencyInfoDTO.setEmergDestType(userEmergencyAttributes.getEmergDestType());
        ipEmergencyInfoDTO.setPriDestination(userEmergencyAttributes.getPriDestination());
        ipEmergencyInfoDTO.setSecDestination(userEmergencyAttributes.getSecDestination());
        ipEmergencyInfoDTO.setEmergCancelPermission(userEmergencyAttributes.getEmergCancelPermission());
        ipEmergencyInfoDTO.setEmergOriginBitSet(userEmergencyAttributes.getEmergOriginBitSet());
        ipEmergencyInfoDTO.setEmergTermBitSet(userEmergencyAttributes.getEmergTermBitSet());
        ipEmergencyInfoDTO.setEmergLMRBehavior(userEmergencyAttributes.getEmergLMRBehavior());
        ipEmergencyInfoDTO.setHierarchyType(requestDTO.getHierarchyType());
        knLogger.exit(methodName, "ipEmergencyInfoDTO : ", ipEmergencyInfoDTO);
        return ipEmergencyInfoDTO;
    }

    private KnCorpUserEmergencyAttributesRespDTO getFinalCorpUserEmergencyAttributesResp(KnCorpUserEmergencyAttributesRespDTO respDtoForFromMdn,
                                                                                         KnCorpUserEmergencyAttributesRespDTO respDtoForToMdn) {

        String methodName = "getFinalCorpUserEmergencyAttributesResp()";
        KnCorpUserEmergencyAttributes corpUserEmergencyAttributesForSource = respDtoForFromMdn.getCorpUserEmergencyAttributes();
        knLogger.debug(methodName, "corpUserEmergencyAttributesForSource  - ", corpUserEmergencyAttributesForSource);
        KnCorpUserEmergencyAttributes corpUserEmergencyAttributesForTarget = respDtoForToMdn.getCorpUserEmergencyAttributes();
        knLogger.debug(methodName, "corpUserEmergencyAttributesForTarget - ", corpUserEmergencyAttributesForTarget);

        if ((null == corpUserEmergencyAttributesForSource || null == corpUserEmergencyAttributesForSource.getEmergInitPermission() || corpUserEmergencyAttributesForSource.getEmergInitPermission() == 0) &&
                (null == corpUserEmergencyAttributesForTarget || null == corpUserEmergencyAttributesForTarget.getEmergInitPermission() || corpUserEmergencyAttributesForTarget.getEmergInitPermission() == 0)) {
            if (null == corpUserEmergencyAttributesForTarget) {
                corpUserEmergencyAttributesForTarget = new KnCorpUserEmergencyAttributes();
            }
            corpUserEmergencyAttributesForTarget.setEmergInitPermission(-99);
        } else {
            //compare whole object and keep in the list
            /*if (null != corpUserEmergencyAttributesForSource.getMdn()
                    && !corpUserEmergencyAttributesForSource.getMdn().equals(corpUserEmergencyAttributesForTarget.getMdn())) {
                corpUserEmergencyAttributesForTarget.setMdn(corpUserEmergencyAttributesForSource.getMdn());
            }*/
            if (null != corpUserEmergencyAttributesForSource.getPriDestination()
                    && !corpUserEmergencyAttributesForSource.getPriDestination().equals(corpUserEmergencyAttributesForTarget.getPriDestination())) {
                corpUserEmergencyAttributesForTarget.setPriDestination(corpUserEmergencyAttributesForSource.getPriDestination());
            } else {
                corpUserEmergencyAttributesForTarget.setPriDestination(corpUserEmergencyAttributesForSource.getPriDestination());
            }
            if (null != corpUserEmergencyAttributesForSource.getSecDestination()
                    && !corpUserEmergencyAttributesForSource.getSecDestination().equals(corpUserEmergencyAttributesForTarget.getSecDestination())) {
                corpUserEmergencyAttributesForTarget.setSecDestination(corpUserEmergencyAttributesForSource.getSecDestination());
            } else {
                corpUserEmergencyAttributesForTarget.setSecDestination(corpUserEmergencyAttributesForSource.getSecDestination());
            }
            if (null == corpUserEmergencyAttributesForSource.getEmergInitPermission()) {
                corpUserEmergencyAttributesForTarget.setEmergInitPermission(DISABLED);
            } else if (!Objects.equals(corpUserEmergencyAttributesForSource.getEmergInitPermission(), corpUserEmergencyAttributesForTarget.getEmergInitPermission())) {
                corpUserEmergencyAttributesForTarget.setEmergInitPermission(corpUserEmergencyAttributesForSource.getEmergInitPermission());
            }
            if (corpUserEmergencyAttributesForSource.getEmergCallType() != corpUserEmergencyAttributesForTarget.getEmergCallType()) {
                corpUserEmergencyAttributesForTarget.setEmergCallType(corpUserEmergencyAttributesForSource.getEmergCallType());
            }
            if (corpUserEmergencyAttributesForSource.getEmergCancelPermission() != corpUserEmergencyAttributesForTarget.getEmergCancelPermission()) {
                corpUserEmergencyAttributesForTarget.setEmergCancelPermission(corpUserEmergencyAttributesForSource.getEmergCancelPermission());
            }
            if (corpUserEmergencyAttributesForSource.getEmergOriginBitSet() != corpUserEmergencyAttributesForTarget.getEmergOriginBitSet()) {
                corpUserEmergencyAttributesForTarget.setEmergOriginBitSet(corpUserEmergencyAttributesForSource.getEmergOriginBitSet());
            }
            if (corpUserEmergencyAttributesForSource.getEmergTermBitSet() != corpUserEmergencyAttributesForTarget.getEmergTermBitSet()) {
                corpUserEmergencyAttributesForTarget.setEmergTermBitSet(corpUserEmergencyAttributesForSource.getEmergTermBitSet());
            }
            if (corpUserEmergencyAttributesForSource.getEmergLMRBehavior() != corpUserEmergencyAttributesForTarget.getEmergLMRBehavior()) {
                corpUserEmergencyAttributesForTarget.setEmergLMRBehavior(corpUserEmergencyAttributesForSource.getEmergLMRBehavior());
            }
            if (null != corpUserEmergencyAttributesForSource.getEmergDestType() &&
                    corpUserEmergencyAttributesForSource.getEmergDestType() != corpUserEmergencyAttributesForTarget.getEmergDestType()) {
                corpUserEmergencyAttributesForTarget.setEmergDestType(corpUserEmergencyAttributesForSource.getEmergDestType());
            }
            knLogger.debug(methodName, "in else corpUserEmergencyAttributesForTarget - ", corpUserEmergencyAttributesForTarget);
        }
        knLogger.info(methodName, "corpUserEmergencyAttributesForTarget - ", corpUserEmergencyAttributesForTarget);
        respDtoForToMdn.setCorpUserEmergencyAttributes(corpUserEmergencyAttributesForTarget);
        return respDtoForToMdn;
    }

    public KnCorpResponseDTO cloneCorpAdminFS(KnCorpSubsResquestDTO xdmRequestDTO, KnPersisterTxn persisterTxn, KnIPCorpSubscCloningListDTO corpSubscCloningListDTO) throws KnXDMServerException {
        String methodName = "updateCorpAdminFS()";
        KnCorpResponseDTO respDto = new KnCorpResponseDTO();
        KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO = corpSubscCloningListDTO.getIpSubscrFeatureInfoDTO();
        if (null != ipSubscrFeatureInfoDTO.getSubscrFeatureInfoDTOList()) {
            respDto = corpClientIntf.cloneCorpAdminFS(ipSubscrFeatureInfoDTO, persisterTxn);
        } else {
            populate(respDto);
        }
        knLogger.debug(methodName, "Update subscriber corp admin feature sets Response - ", respDto);
        return respDto;
    }

    public static KnCorpResponseDTO populate(KnCorpResponseDTO respDTO) {
        respDTO.setStatus(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUCCESS);
        respDTO.setMessage("Success");
        respDTO.setStatusCode("00000");
        return respDTO;
    }

    public int getCurrentEtag(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        return corpClientIntf.getCurrentEtag(mdn, persisterTxn);
    }

    public Collection<KnTargetMdnPermBitInfo> getTargetMdnPermBitInfoList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn, Map<String, Boolean> mapOfFeature) throws KnXDMServerException {
        String methodName = "getTargetMdnPermBitInfoList()";
        //KnCorpSubMCPTTInfoRequestDTO xdmRequestDTO = (KnCorpSubMCPTTInfoRequestDTO) requestDTO;

        KnCorpSubsResquestDTO xdmRequestDTO = (KnCorpSubsResquestDTO) requestDTO;
        //Get existing permissions for the source
        KnCorpUserPermissionRespDTO respDtoForSourceMDN = getExistingTargetPermissions(persisterTxn, xdmRequestDTO, xdmRequestDTO.getFromMdn());
        knLogger.debug(methodName, "Returning getTargetPermissions response for source- ", respDtoForSourceMDN);

        //Get existing permissions for the target
        KnCorpUserPermissionRespDTO respDtoForTargetMDN = getExistingTargetPermissions(persisterTxn, xdmRequestDTO, xdmRequestDTO.getToMdn());
        knLogger.debug(methodName, "Returning getTargetPermissions response for target - ", respDtoForTargetMDN);

        //compare and get targetMdnPermissionBitInfoList to be set on target
        Collection<KnTargetMdnPermBitInfo> finalTargetMdnPermissionBitInfoList = getFinalTargetMdnPermissionBitInfoList(respDtoForSourceMDN, respDtoForTargetMDN, mapOfFeature);
        knLogger.debug(methodName, "Returning setTargetPermissions response finalTargetMdnPermissionBitInfoList- ", finalTargetMdnPermissionBitInfoList);

        return finalTargetMdnPermissionBitInfoList;
    }

    private KnCorpUserPermissionRespDTO getExistingTargetPermissions(KnPersisterTxn persisterTxn, KnCorpSubsResquestDTO xdmRequestDTO, String sourceMDN) {
        String methodName = "getExistingTargetPermissions()";
        KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO = new KnIPAuthUserPermissionInfoDTO();
        if (xdmRequestDTO.getCorpId() != null) {
            ipAuthUserPermissionInfoDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        }
        ipAuthUserPermissionInfoDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        ipAuthUserPermissionInfoDTO.setAuthorizedMdn(sourceMDN);
        ipAuthUserPermissionInfoDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        knLogger.debug(methodName, "getTargetPermissions input dto - ", ipAuthUserPermissionInfoDTO);
        KnCorpUserPermissionRespDTO respDto = null;
        if (CLIENT_TYPE_SOAP == xdmRequestDTO.getClientType()) {
            respDto = corpClientIntf.getTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        } else {
            respDto = corpClientIntf.getSubsTargetPermissions(ipAuthUserPermissionInfoDTO, persisterTxn);
        }

        if (KnConstants.RESPONSE_STATUS.FAILURE.value() == respDto.getStatus()) {
            knLogger.error(methodName, "Returning Failure response");
        }
        return respDto;
    }


    private Collection<KnTargetMdnPermBitInfo> getFinalTargetMdnPermissionBitInfoList(KnCorpUserPermissionRespDTO respDtoForSourceMDN,
                                                                                      KnCorpUserPermissionRespDTO respDtoForTargetMDN,
                                                                                      Map<String, Boolean> mapOfFeature) {
        //creating map of mdn and KnTargetMdnPermBitInfo for source
        String methodName = "getFinalTargetMdnPermissionBitInfoList()";
        knLogger.debug(methodName, "mapOfFeature: ", mapOfFeature);

        knLogger.debug(methodName, "respDtoForSourceMDN - ", respDtoForSourceMDN);
        knLogger.debug(methodName, "respDtoForTargetMDN - ", respDtoForTargetMDN);


        Map<String, KnTargetMdnPermBitInfo> targetMdnPermBitInfoMapForSource = new HashMap<>();
        Collection<KnTargetMdnPermBitInfo> targetMdnPermissionBitInfoSourceList = respDtoForSourceMDN.getTargetMdnPermissionBitInfoList();
        knLogger.debug(methodName, "targetMdnPermissionBitInfoSourceList - ", targetMdnPermissionBitInfoSourceList);
        targetMdnPermissionBitInfoSourceList.forEach(targetMdnPermissionBitInfo -> {
            targetMdnPermBitInfoMapForSource.put(targetMdnPermissionBitInfo.getMdn(), targetMdnPermissionBitInfo);
        });

        //creating map of mdn and KnTargetMdnPermBitInfo for target
        Map<String, KnTargetMdnPermBitInfo> targetMdnPermBitInfoMapForDestination = new HashMap<>();
        Collection<KnTargetMdnPermBitInfo> targetMdnPermissionBitInfoTargetList = respDtoForTargetMDN.getTargetMdnPermissionBitInfoList();
        knLogger.debug(methodName, "targetMdnPermissionBitInfoTargetList - ", targetMdnPermissionBitInfoTargetList);
        targetMdnPermissionBitInfoTargetList.forEach(targetMdnPermissionBitInfo -> {
            targetMdnPermBitInfoMapForDestination.put(targetMdnPermissionBitInfo.getMdn(), targetMdnPermissionBitInfo);
        });

        knLogger.debug(methodName, "targetMdnPermBitInfoMapForSource - ", targetMdnPermBitInfoMapForSource);
        knLogger.debug(methodName, "targetMdnPermBitInfoMapForDestination - ", targetMdnPermBitInfoMapForDestination);


        Collection<KnTargetMdnPermBitInfo> finalTargetMdnPermissionBitInfoList = new ArrayList<>();

        //for TU which is in Source and Target both do update And if only in source then copy
        targetMdnPermBitInfoMapForSource.forEach((mdn, sourceMdnPermBitInfo) -> {
            KnTargetMdnPermBitInfo targetMdnPermBitInfo = targetMdnPermBitInfoMapForDestination.get(mdn);
            if (targetMdnPermBitInfo == null) {
                targetMdnPermBitInfo = new KnTargetMdnPermBitInfo();
                targetMdnPermBitInfo.setMdn(sourceMdnPermBitInfo.getMdn());
                finalTargetMdnPermissionBitInfoList.add(targetMdnPermBitInfo);
            }
            if (sourceMdnPermBitInfo.getSubsName() != null && !sourceMdnPermBitInfo.getSubsName().equals(targetMdnPermBitInfo.getSubsName())) {
                targetMdnPermBitInfo.setSubsName(sourceMdnPermBitInfo.getSubsName());
            }
            targetMdnPermBitInfo.setAmbientListening(mapOfFeature.getOrDefault(FEATURE_SET.AMBIENTLISTENING.name(), false) ? sourceMdnPermBitInfo.getAmbientListening() : 0);
            targetMdnPermBitInfo.setDiscreteListening(mapOfFeature.getOrDefault(FEATURE_SET.DISCRETELISTENING.name(), false) ? sourceMdnPermBitInfo.getDiscreteListening() : 0);
            targetMdnPermBitInfo.setUserCheck(mapOfFeature.getOrDefault(FEATURE_SET.USERCHECK.name(), false) ? sourceMdnPermBitInfo.getUserCheck() : 0);
            targetMdnPermBitInfo.setUserEnable(mapOfFeature.getOrDefault(FEATURE_SET.USERENABLEDISABLE.name(), false) ? sourceMdnPermBitInfo.getUserEnable() : 0);
            targetMdnPermBitInfo.setEmergPermission(mapOfFeature.getOrDefault(FEATURE_SET.EMERGENCY.name(), false) ? sourceMdnPermBitInfo.getEmergPermission() : 0);
            targetMdnPermBitInfo.setMcVideoUnConfirmedPull(mapOfFeature.getOrDefault(FEATURE_SET.MCVIDEOUNCONFIRMEDPULL.name(), false) ? sourceMdnPermBitInfo.getMcVideoUnConfirmedPull() : 0);
            targetMdnPermBitInfo.setPermBitSet(0);
            targetMdnPermBitInfo.setDiscreteEnabled(sourceMdnPermBitInfo.getDiscreteEnabled());
            targetMdnPermBitInfo.setCommonContact(sourceMdnPermBitInfo.getCommonContact());
        });

        //for TU which is not in source , remove from target
        targetMdnPermBitInfoMapForDestination.forEach((mdn, targetMdnPermBitInfo) -> {
            if (!targetMdnPermBitInfoMapForSource.containsKey(mdn)) {
                KnTargetMdnPermBitInfo newPermBitInfo = new KnTargetMdnPermBitInfo();
                newPermBitInfo.setMdn(targetMdnPermBitInfo.getMdn());
                newPermBitInfo.setSubsName(targetMdnPermBitInfo.getSubsName());
                newPermBitInfo.setAmbientListening(0);
                newPermBitInfo.setDiscreteListening(0);
                newPermBitInfo.setUserCheck(0);
                newPermBitInfo.setUserEnable(0);
                newPermBitInfo.setEmergPermission(0);
                newPermBitInfo.setMcVideoUnConfirmedPull(0);
                newPermBitInfo.setPermBitSet(0);
                newPermBitInfo.setDiscreteEnabled(0);
                newPermBitInfo.setCommonContact(0);
                finalTargetMdnPermissionBitInfoList.add(newPermBitInfo);
            }
        });
        knLogger.exit(methodName, "finalTargetMdnPermissionBitInfoList - ", finalTargetMdnPermissionBitInfoList);
        return finalTargetMdnPermissionBitInfoList;
    }

    public KnCorpResponseDTO updateSelfEtag(KnCorpResponseDTO userProfileDetails, KnPersisterTxn persisterTxn) {
        String methodName = "updateSelfEtag(KnCorpResponseDTO,KnPersisterTxn)";
        knLogger.debug(methodName, " userProfileDetails :", userProfileDetails);
        return corpClientIntf.updateSelfEtag(userProfileDetails, persisterTxn);
    }

    public void updateSelfEtagForClone(KnCorpResponseDTO assignGroupResp, String corpIdString, String mdn, KnPersisterTxn persisterTxn) {
        String methodName = "updateSelfEtag(KnCorpResponseDTO, KnXDMCorpUserProfileRespDTO, KnPersisterTxn)";
        knLogger.debug(methodName, " assignGroupResp :", assignGroupResp);
        corpClientIntf.updateSelfEtagForClone(assignGroupResp, corpIdString, mdn, persisterTxn);
    }

    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTS(Set<String> mdnSet, String corpId, KnPersisterTxn persisterTxn) {
        return corpClientIntf.updateSubsTS(mdnSet, corpId, persisterTxn);
    }

    public KnCorpResponseDTO groupWatcherNotifyForAddGroup(KnAsyncJobDTO knAsyncJobDTO) throws KnXDMServerException {
        return corpClientIntf.groupWatcherNotifyForAddGroup(knAsyncJobDTO);
    }

    public KnCorpResponseDTO groupWatcherNotifyForModifyGroup(KnAsyncJobDTO knAsyncJobDTO) throws KnXDMServerException {
        return corpClientIntf.groupWatcherNotifyForModifyGroup(knAsyncJobDTO);
    }

    public KnCorpResponseDTO groupWatcherNotifyforRemoveGroup(KnAsyncJobDTO knAsyncJobDTO) throws KnXDMServerException {
        return corpClientIntf.groupWatcherNotifyforRemoveGroup(knAsyncJobDTO);
    }

    public KnCorpResponseDTO updateEtag(int corpId, String profileMdn, KnCorpResponseDTO userProfileDetails, KnPersisterTxn persisterTxn) {
        String methodName = "updateEtag(String, Collection<String>,KnPersisterTxn , Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, " updateEtag :");
        return corpClientIntf.updateEtag(corpId, profileMdn, userProfileDetails, persisterTxn, null);

    }

    public static com.kodiak.common.commdto.response.KnCorpGroupContactDTO convertToCommonDto(com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupContactDTO grpMemProps) {
        com.kodiak.common.commdto.response.KnCorpGroupContactDTO knCorpGroupContactDTO = new com.kodiak.common.commdto.response.KnCorpGroupContactDTO();
        knCorpGroupContactDTO.setCallInitiateAllowed(grpMemProps.getCallInitiateAllowed());
        knCorpGroupContactDTO.setCallTerminateAllowed(grpMemProps.getCallTerminateAllowed());
        knCorpGroupContactDTO.setIncallAllowed(grpMemProps.getIncallAllowed());
        knCorpGroupContactDTO.setIsBroadcaster(grpMemProps.getIsBroadcaster());
        knCorpGroupContactDTO.setIsLocSupervisor(grpMemProps.getIsLocSupervisor());
        knCorpGroupContactDTO.setIsOSMAuthorized(grpMemProps.getIsOSMAuthorized());
        knCorpGroupContactDTO.setIsSupervisor(grpMemProps.getIsSupervisor());
        return knCorpGroupContactDTO;
    }

    public static void updateGroupMemOsmBit(String mdn, int clientType, String featureSet, String xdmsHomePttId, KnPersisterTxn persisterTxn) {
        String methodName = "updateGroupMemOsmBit(String,int,String)";
        try {
            /*
            Removed update of Contact and Group Etag updates.
             */
            boolean isOSMAuthorizeBit = KnGeneralUtil.getFeatureBitValue(featureSet, IS_OSM_AUTHORIZE_BIT);
            KnCorpGroupInfoUtil corpGroupInfoUtil = new KnCorpGroupInfoUtil();
            if ((clientType == DISPATCHER_CLIENT || clientType == THIRD_PARTY_DISPATCHERS_CLIENT)
                    && isOSMAuthorizeBit) {
                knLogger.debug(methodName, "isOsmAuth FB enabled and CT(3,12)");
                corpGroupInfoUtil.updateSubsOsmAuthorizeInAllGroups(mdn, ENABLED_STRING, xdmsHomePttId, persisterTxn);
            } else if (!isOSMAuthorizeBit) {
                knLogger.debug(methodName, "isOsmAuth FB disabled.");
                corpGroupInfoUtil.updateSubsOsmAuthorizeInAllGroups(mdn, DISABLED_STRING, xdmsHomePttId, persisterTxn);
            } else {
                knLogger.debug(methodName, "neither isOsmAuth FB enabled and CT(3,12) nor isOsmAuth FB disabled.");
            }
        } catch (Exception e) { //Not breaking the operation if any exception occurs
            knLogger.warn(methodName, "Exception occurred while updating OSM Auth ", e);
        }
    }

    public KnCorpResponseDTO createPTTSettingDoc(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "createPTTSettingDoc(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        KnCorpResponseDTO respDto =null;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        try {
            KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
            ipCorpPTTSettingDTO.setCorpId(pttSettingRequestDTO.getCorpId());
            ipCorpPTTSettingDTO.getPttSettingInfo().setTemplateName(pttSettingRequestDTO.getPttSettingName());
            ipCorpPTTSettingDTO.getPttSettingInfo().setPttSettingDocDetail(pttSettingRequestDTO.getPttSettingInfo());

            knLogger.debug(methodName, "pttSetting ", ipCorpPTTSettingDTO.getPttSettingInfo());

            respDto = corpClientIntf.createPTTSettingDoc(ipCorpPTTSettingDTO, persisterTxn);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while createPTTSettingDoc ", e);
        }
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO getPTTSettingDocList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getPTTSettingDocList(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();

        ipCorpPTTSettingDTO.setCorpId(pttSettingRequestDTO.getCorpId());
        ipCorpPTTSettingDTO.setHierarchyId(pttSettingRequestDTO.getHierarchyId());

        knLogger.debug(methodName, "pttSetting corpId:", ipCorpPTTSettingDTO.getCorpId());

        KnCorpResponseDTO respDto = corpClientIntf.getPTTSettingDocList(ipCorpPTTSettingDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpPTTSettingDTO getPTTSettingDoc(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getPTTSettingDoc(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
        ipCorpPTTSettingDTO.setPttSettingId(pttSettingRequestDTO.getPttSettingId());
        knLogger.debug(methodName, "ptt docId ", ipCorpPTTSettingDTO.getPttSettingId());

        KnCorpPTTSettingDTO respDto = corpClientIntf.getPTTSettingDoc(ipCorpPTTSettingDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }
    public KnCorpResponseDTO setDefaultPttSettingDoc(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "setDefaultPttSettingDoc(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
        ipCorpPTTSettingDTO.setCorpId(pttSettingRequestDTO.getCorpId());
        ipCorpPTTSettingDTO.setHierarchyId(pttSettingRequestDTO.getHierarchyId());
        ipCorpPTTSettingDTO.setPttSettingId(pttSettingRequestDTO.getPttSettingId());
        knLogger.debug(methodName, "Request: ", ipCorpPTTSettingDTO);

        KnCorpResponseDTO respDto = corpClientIntf.setDefaultPttSettingDoc(ipCorpPTTSettingDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO deletePTTSettingDoc(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deletePTTSettingDoc(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
        ipCorpPTTSettingDTO.setPttSettingId(pttSettingRequestDTO.getPttSettingId());
        knLogger.debug(methodName, "Request: ", ipCorpPTTSettingDTO);
        KnCorpResponseDTO respDto = corpClientIntf.deletePTTSettingDoc(ipCorpPTTSettingDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }
    public KnCorpResponseDTO assignPttSettingToHierarchy(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "assignPttSettingToHierarchy(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
        ipCorpPTTSettingDTO.setCorpId(pttSettingRequestDTO.getCorpId());
        ipCorpPTTSettingDTO.setPttSettingDocList(pttSettingRequestDTO.getPttSettingDocList());
        knLogger.debug(methodName, "Request: ", ipCorpPTTSettingDTO);

        KnCorpResponseDTO respDto = corpClientIntf.assignPttSettingToHierarchy(ipCorpPTTSettingDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }
    public KnCorpResponseDTO unassignPttSettingToHierarchy(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "unassignPttSettingToHierarchy(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
        ipCorpPTTSettingDTO.setCorpId(pttSettingRequestDTO.getCorpId());
        ipCorpPTTSettingDTO.setPttSettingDocList(pttSettingRequestDTO.getPttSettingDocList());
        knLogger.debug(methodName, "Request: ", ipCorpPTTSettingDTO);

        KnCorpResponseDTO respDto = corpClientIntf.unassignPttSettingToHierarchy(ipCorpPTTSettingDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO assignPttSettingDocToMdns(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) {
        String methodName = "assignPttSettingDocToMdns(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
        ipCorpPTTSettingDTO.setCorpId(pttSettingRequestDTO.getCorpId());
        ipCorpPTTSettingDTO.setMdnList(pttSettingRequestDTO.getMdnList());
        ipCorpPTTSettingDTO.setPttSettingId(pttSettingRequestDTO.getPttSettingId());
        ipCorpPTTSettingDTO.setHierarchyId(pttSettingRequestDTO.getHierarchyId());
        knLogger.debug(methodName, "Request: ", ipCorpPTTSettingDTO);
        KnCorpResponseDTO respDto = corpClientIntf.assignPttSettingDocToMdns(ipCorpPTTSettingDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO unassignPttSettingDocToMdns(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) {
        String methodName = "unassignPttSettingDocToMdns(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
        ipCorpPTTSettingDTO.setCorpId(pttSettingRequestDTO.getCorpId());
        ipCorpPTTSettingDTO.setMdnList(pttSettingRequestDTO.getMdnList());
        ipCorpPTTSettingDTO.setPttSettingId(pttSettingRequestDTO.getPttSettingId());
        ipCorpPTTSettingDTO.setHierarchyId(pttSettingRequestDTO.getHierarchyId());
        knLogger.debug(methodName, "Request: ", ipCorpPTTSettingDTO);
        KnCorpResponseDTO respDto = corpClientIntf.unassignPttSettingDocToMdns(ipCorpPTTSettingDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO getPttSettingDocMdnList(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getPttSettingDocMdnList(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
        ipCorpPTTSettingDTO.setCorpId(pttSettingRequestDTO.getCorpId());
        ipCorpPTTSettingDTO.setHierarchyId(pttSettingRequestDTO.getHierarchyId());
        ipCorpPTTSettingDTO.setPttSettingId(pttSettingRequestDTO.getPttSettingId());
        knLogger.debug(methodName, "Request: ", ipCorpPTTSettingDTO);
        KnCorpResponseDTO respDto = corpClientIntf.getPttSettingDocMdnList(ipCorpPTTSettingDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO getMDNCountForPttSettingDocID(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getMDNCountForPttSettingDocID(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
        ipCorpPTTSettingDTO.setCorpId(pttSettingRequestDTO.getCorpId());
        ipCorpPTTSettingDTO.setPttSettingId(pttSettingRequestDTO.getPttSettingId());
        ipCorpPTTSettingDTO.setHierarchyId(pttSettingRequestDTO.getHierarchyId());
        knLogger.debug(methodName, "Request: ", ipCorpPTTSettingDTO);
        KnCorpResponseDTO respDto = corpClientIntf.getMDNCountForPttSettingDocID(ipCorpPTTSettingDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }


    public KnCorpResponseDTO deleteHierarchy(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "deleteHierarchy()";
        knLogger.debug(methodName, "corpId: ", corpId, " removedHierarchy: ", removedHierarchy);
        KnCorpResponseDTO responseDto = corpClientIntf.deleteHierarchy(corpId, removedHierarchy, persisterTxn);
        knLogger.debug(methodName, "responseDto: ", responseDto);
        return responseDto;
    }

    public KnCorpResponseDTO createHierarchy(KnCreateHirarchyRequestDTO createHirarchyRequestDTO) {
        String methodName = "createHierarchy(IXDMRequestDTO, KnPersisterTxn)";
        try {
            KnIPCorpHierarchyDTO knIPCorpHierarchyDTO = new KnIPCorpHierarchyDTO();
            knIPCorpHierarchyDTO.setCorpId(Integer.parseInt(createHirarchyRequestDTO.getCorpId()));
            knIPCorpHierarchyDTO.setIdDetailsListDTO(createHirarchyRequestDTO.getIdDetailsListDTO());
            knIPCorpHierarchyDTO.setTransactionId(createHirarchyRequestDTO.getTransactionId());
            KnCorpResponseDTO respDto;
            respDto = corpClientIntf.createHierarchy(knIPCorpHierarchyDTO);
            knLogger.debug(methodName, "Create group response - ", respDto);
            return respDto;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while creating hierarchy ", e);
            throw e;
        }
    }

    public KnCorpResponseDTO modifyHierarchy(KnModifyHierarchyRequestDTO modifyHirarchyRequestDTO) {
        String methodName = "modifyHierarchy(IXDMRequestDTO, KnPersisterTxn)";
        try {
            KnIPCorpHierarchyDTO knIPCorpHierarchyDTO = new KnIPCorpHierarchyDTO();
            knIPCorpHierarchyDTO.setCorpId(Integer.parseInt(modifyHirarchyRequestDTO.getCorpId()));
            knIPCorpHierarchyDTO.setModifiedIdDetails(modifyHirarchyRequestDTO.getModifiedIdDetails());
            KnCorpResponseDTO respDto;
            respDto = corpClientIntf.modifyHierarchy(knIPCorpHierarchyDTO);
            knLogger.debug(methodName, "modify hierarchy - ", respDto);
            return respDto;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while creating hierarchy ", e);
            throw e;
        }
    }

    public KnCorpResponseDTO allocateSubs(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "allocateSubs(requestDTO, persisterTxn)";
        if (!(requestDTO instanceof KnXDMAllocateSubscriberRequestDto)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMAllocateSubscriberRequestDto - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMAllocateSubscriberRequestDto xdmRequestDTO = (KnXDMAllocateSubscriberRequestDto) requestDTO;
        KnIPAllocateSubscriberDTO allocateSubscriberDTO = new KnIPAllocateSubscriberDTO();
        allocateSubscriberDTO.setCorpId(xdmRequestDTO.getCorpId());
        allocateSubscriberDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        allocateSubscriberDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        allocateSubscriberDTO.setHierarchyId(xdmRequestDTO.getHierarchyId());
        List<KnIPAllocateSubscriberDTO.KnMdnDetailsDTO> mdnDetailsList = xdmRequestDTO.getMdnList().stream()
                .map(requestMdnDto -> {
                    KnIPAllocateSubscriberDTO.KnMdnDetailsDTO mdnDetailsDto = new KnIPAllocateSubscriberDTO.KnMdnDetailsDTO();
                    mdnDetailsDto.setMdn(requestMdnDto.getMdn());
                    mdnDetailsDto.setGeoCode(requestMdnDto.getGeoCode());
                    return mdnDetailsDto;
                })
                .collect(Collectors.toList());

        allocateSubscriberDTO.setMdnList(mdnDetailsList);


        knLogger.debug(methodName, "Retrieving allocateSubscriber - ", allocateSubscriberDTO);
        KnCorpResponseDTO respDto = corpClientIntf.allocateSubs(allocateSubscriberDTO, persisterTxn);
        knLogger.debug(methodName, "Returning allocateSubs response - ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO unAllocateSubs(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "unAllocateSubs(requestDTO, persisterTxn)";
        if (!(requestDTO instanceof KnXDMUnAllocateSubscriberRequestDto)) {
            knLogger.error(methodName, "xdmRequestDTO not of type KnXDMUnAllocateSubscriberRequestDto - requestDTO - ",
                    requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMUnAllocateSubscriberRequestDto xdmRequestDTO = (KnXDMUnAllocateSubscriberRequestDto) requestDTO;
        KnIPUnAllocateSubscriberDTO unAllocateSubscriberDTO = new KnIPUnAllocateSubscriberDTO();
        unAllocateSubscriberDTO.setCorpId(xdmRequestDTO.getCorpId());
        unAllocateSubscriberDTO.setCustomParamMap(xdmRequestDTO.getCustomParamMap());
        unAllocateSubscriberDTO.setHierarchyType(xdmRequestDTO.getHierarchyType());
        unAllocateSubscriberDTO.setHierarchyId(xdmRequestDTO.getHierarchyId());
        unAllocateSubscriberDTO.setMdnList(xdmRequestDTO.getMdnList());

        knLogger.debug(methodName, "Retrieving unAllocateSubscriber - ", unAllocateSubscriberDTO);
        KnCorpResponseDTO respDto = corpClientIntf.unAllocateSubs(unAllocateSubscriberDTO, persisterTxn);
        knLogger.debug(methodName, "Returning unAllocateSubscriber response - ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO assignPttSettingToCorp(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "assignPttSettingToCorp(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
        ipCorpPTTSettingDTO.setCorpId(pttSettingRequestDTO.getCorpId());
        ipCorpPTTSettingDTO.setPttSettingId(pttSettingRequestDTO.getPttSettingId());
        knLogger.debug(methodName, "Request: ", ipCorpPTTSettingDTO);

        KnCorpResponseDTO respDto = corpClientIntf.assignPttSettingToCorp(ipCorpPTTSettingDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO unassignPttSettingToCorp(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "unassignPttSettingToCorp(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
        ipCorpPTTSettingDTO.setCorpId(pttSettingRequestDTO.getCorpId());
        ipCorpPTTSettingDTO.setPttSettingId(pttSettingRequestDTO.getPttSettingId());
        knLogger.debug(methodName, "Request: ", ipCorpPTTSettingDTO);

        KnCorpResponseDTO respDto = corpClientIntf.unassignPttSettingToCorp(ipCorpPTTSettingDTO, persisterTxn);
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }

    /**
     * Rehomes the specified corporate groups to a new PoC Home.
     * Extracts GROUP_IDS and POC_HOME_ID from the customParamMap in the XDM request DTO,
     * builds the business DTO, and delegates to the business layer.
     *
     * @param groupRequestDto the XDM request DTO
     * @param persisterTxn    the transaction object
     * @return KnCorpResponseDTO the response
     * @throws KnXDMServerException if the operation fails
     */
    public KnCorpResponseDTO groupRehome(IXDMRequestDTO groupRequestDto, KnPersisterTxn persisterTxn)
            throws KnXDMServerException {
        String methodName = "groupRehome(IXDMRequestDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY");

        if (!(groupRequestDto instanceof KnXDMCorpGroupInfoRequestDTO)) {
            knLogger.error(methodName, "groupRequestDto not of type KnXDMCorpGroupInfoRequestDTO - requestDTO - ",
                    groupRequestDto.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }
        KnXDMCorpGroupInfoRequestDTO xdmRequestDto = (KnXDMCorpGroupInfoRequestDTO) groupRequestDto;

        KnIPCorpGroupInfoDTO groupInfoDTO = new KnIPCorpGroupInfoDTO();
        if (xdmRequestDto.getCorpId() != null) {
            groupInfoDTO.setExtCorpId(xdmRequestDto.getCorpId());
        }
        groupInfoDTO.setClientType(xdmRequestDto.getClientType());
        groupInfoDTO.setCustomParamMap(xdmRequestDto.getCustomParamMap());

        knLogger.debug(methodName, "Calling corpClientIntf.groupRehome with groupInfoDTO - ", groupInfoDTO);
        KnCorpResponseDTO respDto = corpClientIntf.groupRehome(groupInfoDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT - respDto - ", respDto);
        return respDto;
    }

    public KnCorpResponseDTO modifyPTTSettingTemplate(IXDMRequestDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "modifyPTTSettingTemplate(IXDMRequestDTO, KnPersisterTxn)";
        KnXDMCorpPTTSettingRequestDTO pttSettingRequestDTO = (KnXDMCorpPTTSettingRequestDTO) requestDTO;
        KnCorpResponseDTO respDto =null;
        knLogger.debug(methodName, "Entry ", pttSettingRequestDTO);
        try {
            KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO = new KnIPCorpPTTSettingDTO();
            ipCorpPTTSettingDTO.setCorpId(pttSettingRequestDTO.getCorpId());
            ipCorpPTTSettingDTO.getPttSettingInfo().setTemplateName(pttSettingRequestDTO.getPttSettingName());
            ipCorpPTTSettingDTO.getPttSettingInfo().set_id(pttSettingRequestDTO.getPttSettingId());
            ipCorpPTTSettingDTO.getPttSettingInfo().setPttSettingDocDetail(pttSettingRequestDTO.getPttSettingInfo());

            knLogger.debug(methodName, "pttSetting ", ipCorpPTTSettingDTO.getPttSettingInfo());

            respDto = corpClientIntf.modifyPTTSettingTemplate(ipCorpPTTSettingDTO, persisterTxn);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while modifyPTTSettingTemplate ", e);
        }
        knLogger.debug(methodName, "Exit ", respDto);
        return respDto;
    }
    /**
     * Retrieves regions for a given corp and optional hierarchyId.
     * If hierarchyId is provided, queries DG.CORP_HIERARCHY_GEOCODE_MAPPING.
     * If hierarchyId is null/empty, queries DG.DEPLOY_SITE_INFO for all regions.
     *
     * @param requestDTO The XDM request DTO (KnXDMCorpInfoRequestDTO)
     * @return KnXDMGetRegionsRespDTO with geoCodeList populated
     */
    public KnXDMGetRegionsRespDTO getRegions(IXDMRequestDTO requestDTO) throws KnXDMServerException {
        final String methodName = "getRegions(IXDMRequestDTO)";
        knLogger.info(methodName, "Entry with request - ", requestDTO);

        if (!(requestDTO instanceof KnXDMCorpInfoRequestDTO)) {
            knLogger.error(methodName, "requestDTO not of type KnXDMCorpInfoRequestDTO, class - ", requestDTO.getClass());
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_INVALID_DTO_PASSED, "Invalid DTO");
        }

        KnXDMCorpInfoRequestDTO reqDTO = (KnXDMCorpInfoRequestDTO) requestDTO;
        knLogger.debug(methodName, "reqDTO - ", reqDTO);

        KnIPCorpHierarchyDTO hierarchyDTO = new KnIPCorpHierarchyDTO();
        hierarchyDTO.setCorpId(Integer.parseInt(reqDTO.getCorpId()));
        hierarchyDTO.setTransactionId(reqDTO.getTransactionId());
        hierarchyDTO.setHierarchyId(reqDTO.getHierarchyId());

        knLogger.debug(methodName, "Calling corpClientIntf.getRegions with hierarchyDTO - ", hierarchyDTO);

        KnRegionsCorpRespDTO respDto = corpClientIntf.getRegions(hierarchyDTO);

        KnXDMGetRegionsRespDTO xdmRespDto = new KnXDMGetRegionsRespDTO();
        populateXdmResponse(xdmRespDto, respDto);

        if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == respDto.getStatus()) {
            xdmRespDto.setGeoCodeList(respDto.getGeoCodeList());
            knLogger.info(methodName, "Exit - successfully retrieved regions, count - ",
                    (respDto.getGeoCodeList() != null ? respDto.getGeoCodeList().size() : 0));
        } else {
            knLogger.error(methodName, "getRegions operation failed - ", respDto.getMessage());
        }

        return xdmRespDto;
    }

}