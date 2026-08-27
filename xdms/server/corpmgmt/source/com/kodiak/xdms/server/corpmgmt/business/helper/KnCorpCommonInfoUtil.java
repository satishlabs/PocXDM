/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpCommonInfoUtil.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        24-01-2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.business.helper;

import com.couchbase.client.core.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.DeserializationFeature;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.kodiak.common.commdto.common.*;
import com.kodiak.common.commdto.request.KnCorpSubsResquestDTO;
import com.kodiak.common.commdto.request.KnXDMCorpUserProfileRequestDTO;
import com.kodiak.common.commdto.request.KnXDMGroupPropertyInfoDTO;
import com.kodiak.common.commdto.response.KnCORPGroupStatsRespDTO;
import com.kodiak.common.commdto.response.KnDeviceDetailsDTO;
import com.kodiak.common.commdto.response.KnXDMFailureRespDTO;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.normalizationfw.dto.KnDialPlanConfigDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetException;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.lieventhandler.dto.KnLIEventDTO;
import com.kodiak.utilities.lieventhandler.handler.KnLIConstants;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.business.helper.KnProfileInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnTGSModeChgDTO;
import com.kodiak.xdms.server.common.dto.common.*;
import com.kodiak.xdms.server.common.framework.validator.KnConsolidateValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidationErrorObject;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm.KnLicenseInfoDAO;
import com.kodiak.xdms.server.corpmgmt.dto.ICorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpUserProfileMCPTTConfig;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDestinationAttributeDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubsEmergencyConfigDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.MCPTT_PERMISSION_BIT.*;
import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.common.resources.KnConstants.GG_BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.PUBLIC_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.DEFAULT_EMERGENCY_BIT_SET;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SELF_DND_FEATURE;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isObjectNull;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.GROUP_DOES_NOT_EXIST;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.NON_FIRSTNET_FAN;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.SYSTEM_LEVEL_SELF_DND_PRIVILEGE_FLAG_DISABLED;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.JsonNode;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.node.ObjectNode;
/**
 *
 */
public class KnCorpCommonInfoUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpCommonInfoUtil.class);

    private KnProfileInfoUtil corpProfileUtil;
    private KnProfileInfoUtil pubProfileUtil;
    private KnGenInfoUtil genInfoUtil;
    private KnFeatureSetUtil featureSetUtil;
    private static KnCorpGroupInfoUtil groupInfoUtil;
    private static KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private static String xcapDirGroupFolder = "xcap-directory/folder%5B@auid=%22kn-corp-groups%22%5D";
    private static final String MASTERLIST = "MasterListEtag";
    private static final String MASTERLISTETAG = "Master List is not updated";
    public static final String MCDATAUSERPROFILE_AUID = "org.3gpp.mcdata.user-profile";
    public static final String MCPTT_UE_PROFILE_AUID = "org.3gpp.mcptt.user-profile";
    public static final String MCVIDEOUSERPROFILE_AUID = "org.3gpp.mcvideo.user-profile";
    private static Map<String,String> auidMap=new HashMap<>();
    private KnCorpSublistInfoUtil sublistInfoUtil;

    public KnCorpCommonInfoUtil() {
        KnProfileInfoUtil.initialize(CORP_PROFILE, 400, 1000);
        KnProfileInfoUtil.initialize(PUBLIC_PROFILE, 400, 600);
        corpProfileUtil = KnProfileInfoUtil.getInstance(CORP_PROFILE);
        pubProfileUtil = KnProfileInfoUtil.getInstance(PUBLIC_PROFILE);
        genInfoUtil = KnGenInfoUtil.getInstance();
        featureSetUtil = KnFeatureSetUtil.getInstance();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        //auid list
        auidMap.put("mcvideo",MCVIDEOUSERPROFILE_AUID);
        auidMap.put("mcdata",MCDATAUSERPROFILE_AUID);
        auidMap.put("mcptt",MCPTT_UE_PROFILE_AUID);
    }

    public static <T> T populate(ICorpResponseDTO respDto, Exception e) {
        String methodName = "populate(ICorpResponseDTO, Exception)";
        respDto.setStatus(KnConstants.FAILURE);
        /**
         * removed the instanceof check of other exception class
         * respDto.setFailedDataList is not in used once start using this,
         * instanceof check for  KnValidationException will be required
         */
        if (e instanceof KnException) {
            if (e instanceof KnValidationException) {
                // consolidate exception
                if (e instanceof KnConsolidateValidationException) {
                    knLogger.info(methodName, " inside cosolidating validation errors" );
                    KnConsolidateValidationException consexp = (KnConsolidateValidationException) e;
                    List<KnXDMFailureRespDTO> failureList = new ArrayList<>();
                    for (Object obj : consexp.getConsolidatedErrorObject().getAllNestedErrors()) {
                        KnValidationErrorObject errorObj = (KnValidationErrorObject) obj;
                        KnXDMFailureRespDTO xdmResp = new KnXDMFailureRespDTO();
                        xdmResp.setFcode(errorObj.getErrorCode());
                        xdmResp.setMsg(errorObj.getErrorMessage());
                        if (errorObj.getFailedDatas() != null && !errorObj.getFailedDatas().isEmpty()
                                && ((errorObj.getFailedDatas()).get(errorObj.getFailedData()) != null)) {
                            xdmResp.setAttribute(errorObj.getFailedData());
                            xdmResp.setValue((String) errorObj.getFailedDatas().get(errorObj.getFailedData()));
                        }
                        failureList.add(xdmResp);
                    }
                    respDto.setFailureDetails(failureList);
                    respDto.setStatusCode(consexp.getErrorCode());
                    respDto.setMessage("Multiple error scenario occured");

                } else {
                    //single throw on exception
                    KnValidationException valExp = (KnValidationException) e;
                    List<KnXDMFailureRespDTO> failureList = new ArrayList<>();

                    KnXDMFailureRespDTO xdmResp = new KnXDMFailureRespDTO();
                    xdmResp.setFcode(valExp.getErrorCode());
                    xdmResp.setMsg(valExp.getErrorMessage());
                    xdmResp.setAttribute(valExp.getKeyDataValue());
                    xdmResp.setValue(valExp.getFailedRuleValue());
                    failureList.add(xdmResp);
                    respDto.setFailureDetails(failureList);
                    respDto.setStatusCode(valExp.getErrorCode());
                    respDto.setMessage(valExp.getErrorMessage());
                }

            } else {
            knLogger.debug(methodName, "Inside KnCorpBOException");
            KnException ke = (KnException) e;
            respDto.setStatusCode(ke.getErrorCode());
            respDto.setMessage(ke.getErrorMessage());
            }

        } else {
            respDto.setStatusCode(BOEntity.INTERNAL_ERROR);
            respDto.setMessage("Internal Server Error");
        }
        return (T) respDto;
    }


    /**
     * This method populate the response object with error code and error message sent.
     *
     * @param respDto
     * @param errorCode
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> T populate(ICorpResponseDTO respDto, String errorCode, String errorMessage) {
        respDto.setStatus(KnConstants.FAILURE);
        respDto.setStatusCode(errorCode);
        respDto.setMessage(errorMessage);
        return (T) respDto;
    }

    public static void removeOtherCorpMembers(Map<String, KnOPDirChgDTO> etagMap, List<String> sharedReqCorpMemList) {
        final String methodName = "removeOtherCorpMembers()";
        knLogger.debug(methodName, "ENTRY - ", "etagMap.keySet()", etagMap.keySet(), "sharedReqCorpMemList", sharedReqCorpMemList);
        Set<String> allMdn = new HashSet<>(etagMap.keySet());
        knLogger.debug(methodName, " Size before - ", etagMap.size());
        for(String mdn : allMdn){
            if(!sharedReqCorpMemList.contains(mdn)){
                etagMap.remove(mdn);
            }
        }
        knLogger.debug(methodName, " Size after - ", etagMap.size());
    }

    public static void resetNotiCapabilityOnwedCorp(Map<String, KnOPDirChgDTO> etagMap) {
        final String methodName = "resetNotiCapabilityOnwedCorp()";
        knLogger.info(methodName,"Resetting subscribers notification capability to directory diff capable");
        for (String mdn : etagMap.keySet()) {
            KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
            dirChgDTO.setNotfnCapability(Boolean.FALSE);
        }
    }

    /**
     * This method returns the xcap root Uri configured in xdm_svc table.
     *
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
  /*  public String getXcapUri(String xdmPttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getXcapUri(String, KnPersisterTxn)";
        knLogger.info( methodName, "ENTRY :");
        try {
            KnXDMSServiceConfigDTO configDto = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            return configDto.getXcapRootUri();
        } catch (KnBOException e) {
            knLogger.error( methodName, "KnBOException occured while retrieving Xcap uri", e);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage(), e);
        }
    }*/
    public String getXcapUri(String mdn, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getXcapUri(String, KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdn(mdn), persisterTxn);
        try {
            return genInfoUtil.getXCAPRootURI(mdn, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "KnBOException occured while retrieving Xcap uri", e);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage(), e);
        }
    }

    public int getMaxExternalCorpContacts(String xdmPttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getMaxExternalCorpContacts(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : xdmPttServerId - ", xdmPttServerId, " , persisterTxn - ", persisterTxn);
        try {
            KnXDMSServiceConfigDTO configDto = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            return configDto.getMaxExtContactsPerCorp();
        } catch (KnBOException e) {
            knLogger.error(methodName, "KnBOException occured while retrieving max external contacts allowed - ", e);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage(), e);
        }
    }

    public int getCorpId(String extCorpId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getCorpId(extCorpId, false, persisterTxn);
    }

    /**
     * Method to retrieve corp Id. This is read only method.
     */
    public int getCorpId(String extCorpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getCorpId(String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY - readOnly :", readOnly);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            return xdmDAO.selectCorpId(extCorpId, readOnly, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "KnBOException occured while retrieving corpId", e);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage(), e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving corpId", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                knLogger.error(methodName, "CorpId Doesn't exist. Rethrowing Exception - ", e);
                throw new KnCorpBOException(KnErrorCodes.BOEntity.PROFILE_DOES_NOT_EXIST, "Profile Doesn't exist", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public <P extends KnProfileDTO> P getProfileDetails(String userName, String profileType, boolean isOverride,
                                                        KnPersisterTxn persisterTxn)
            throws KnCorpBOException {

        String methodName = "getProfileDetails(String, String, boolean, KnPersisterTxn)";
        try {
            if (PUBLIC_PROFILE.equals(profileType)) {
                return (P) pubProfileUtil.getProfileDetails(userName, profileType, isOverride, KnConstants.FALSE, persisterTxn);
            } else {
                return (P) corpProfileUtil.getProfileDetails(userName, profileType, isOverride, KnConstants.FALSE, persisterTxn);
            }
        } catch (KnBOException e) {
            String errorCode = e.getErrorCode();
            if (BOEntity.SUBSC_NOT_REGISTERED.equals(errorCode)) {
                if (CORP_PROFILE.equals(profileType)) {
                    knLogger.error(methodName, "Invalid Corp Profile - ", userName);
                    throw new KnCorpBOException(KnErrorCodes.BOEntity.PROFILE_DOES_NOT_EXIST, "Corp Profile Doesn't Exist", e);
                }
                knLogger.error(methodName, "Invalid Subs Profile - ", userName);
                throw new KnCorpBOException(KnErrorCodes.BOEntity.INVALID_POC_SUBSCRIBERS, "Subs Profile Doesn't exist", e);
            }
            throw new KnCorpBOException(BOEntity.INTERNAL_ERROR, "Internal Server Error", e);
        }
    }

    public static KnCorpResponseDTO populate(KnCorpResponseDTO respDTO) {

        respDTO.setStatus(KnConstants.SUCCESS);
        respDTO.setMessage("Success");
        respDTO.setStatusCode("00000");
        return respDTO;
    }


    public static Collection<KnCorpSubscriberDTO> filterOutTheNewlyAddedMebers
            (Collection<KnCorpSubscriberDTO> alreadyPrivateContactList, Collection<KnCorpSubscriberDTO> addedMdnList,
             Collection<KnCorpSubscriberDTO> externalContacts) {
        //Collection<KnCorpSubscriberDTO> contactsDTO = new ArrayList<KnCorpSubscriberDTO>();
        String methodName = "filterOutTheNewlyAddedMebers(Collection<KnCorpSubscriberDTO>, Collection<KnCorpSubscriberDTO>" +
                ",Collection<KnCorpSubscriberDTO>)";
        knLogger.info(methodName, "ENTRY------>");
        knLogger.debug(methodName, "Input Data passed alreadyPrivateContactList - ", alreadyPrivateContactList);
        knLogger.debug(methodName, "Input Data passed addedMdnList - ", addedMdnList);
        knLogger.debug(methodName, "Input Data passed externalContacts - ", externalContacts);
        Map<String, KnCorpSubscriberDTO> contactsMapDTO = new HashMap<String, KnCorpSubscriberDTO>();
        if (!KnCorpUtil.isObjectNull(addedMdnList)) {
            for (KnCorpSubscriberDTO subsc : addedMdnList) {
                knLogger.debug(methodName, "subsc ------> ", subsc);
                String mdn = subsc.getMdn();
                boolean mdnFound = false;
                KnCorpSubscriberDTO contact = new KnCorpSubscriberDTO();
                contact.setCorpId(subsc.getCorpId());
                contact.setMdn(mdn);
                //added setting of the name
                contact.setName(subsc.getName());
                contact.setClientType(subsc.getClientType());
                contact.setClientPVmajorVer(subsc.getClientPVmajorVer());
                contact.setContact_type(subsc.getContact_type());
                contact.setCallInitiatePermission(subsc.getCallInitiatePermission());
                contact.setCallReceivePermission(subsc.getCallReceivePermission());
                contact.setInCallPermission(subsc.getInCallPermission());
                contact.setVideoCallInitiatePermission(subsc.getVideoCallInitiatePermission());
                contact.setVideoCallReceivePermission(subsc.getVideoCallReceivePermission());
                contact.setVideoInCallPermission(subsc.getVideoInCallPermission());
                contact.setMcpttCompliance(subsc.getMcpttCompliance());
                contact.setCorpId(subsc.getCorpId());
                contact.setUa(subsc.getUa());
                if (!KnCorpUtil.isObjectNull(alreadyPrivateContactList)) {
                    /* for (KnCorpSubscriberDTO corpSubs : alreadyPrivateContactList) {
                        knLogger.debug( methodName, "corpSubs - " , corpSubs);
                        if (corpSubs.getMdn().equalsIgnoreCase(mdn)) {
                            knLogger.debug( methodName, "MDN found - " , mdn);
                            mdnFound = true;
                            break;
                        }
                    }*/
                    if (alreadyPrivateContactList.contains(contact)) {
                        //knLogger.debug( methodName, "corpSubs - " , corpSubs);
                        // if (corpSubs.getMdn().equalsIgnoreCase(mdn)) {
                        mdnFound = true;
                        //}
                    }
                }
                if (!mdnFound) {
                    knLogger.debug(methodName, "contact - ", contact);
                    contactsMapDTO.put(mdn, contact);
                }
            }
        }
        if (!KnCorpUtil.isObjectNull(externalContacts)) {
            for (KnCorpSubscriberDTO externalContact : externalContacts) {
                if (!KnCorpUtil.isObjectNull(externalContact)) {
                    knLogger.debug(methodName, "externalContact - ", externalContact);
                    KnCorpSubscriberDTO subsc = contactsMapDTO.get(externalContact.getMdn());
                    if (!KnCorpUtil.isObjectNull(subsc)) {
                        subsc.setCorpId(externalContact.getCorpId());
                        if(externalContact.getContact_type() == KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT){
                            subsc.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_CONTACT);
                        }else{
                            subsc.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                        }
                        knLogger.debug(methodName, "subsc - ", subsc);
                        contactsMapDTO.put(subsc.getMdn(), subsc);
                    }
                }
            }
        }
        knLogger.debug(methodName, "members to be added  - ", contactsMapDTO.values());
        return contactsMapDTO.values();
    }

    public static Collection<Integer> filterOutFinalSublistList(KnSublistDetailsPersistDTO dbSublistList,
                                                                KnSublistDetailsPersistDTO pocSublistIds,
                                                                KnSublistDetailsPersistDTO removedSublistList) {
        String methodName = "filterOutFinalSublistList(KnSublistDetailsPersistDTO, KnSublistDetailsPersistDTO,KnSublistDetailsPersistDTO)";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "Input Data passed dbSublistList - ", dbSublistList);
        knLogger.debug(methodName, "Input Data passed pocSublistIds - ", pocSublistIds);
        knLogger.debug(methodName, "Input Data passed removedSublistList - ", removedSublistList);
        Collection<Integer> mappedSubLists = new ArrayList<Integer>();
        mappedSubLists.addAll(dbSublistList.getSublistIds());
        if (null != pocSublistIds.getSublistIds() && !pocSublistIds.getSublistIds().isEmpty()) {
            for (Integer subList : pocSublistIds.getSublistIds()) {
                if (!mappedSubLists.contains(subList)) {
                    mappedSubLists.add(subList);
                }
            }
        }
        if (removedSublistList.getSublistIds() != null && !removedSublistList.getSublistIds().isEmpty()) {
            for (Integer subList : removedSublistList.getSublistIds()) {
                if (mappedSubLists.contains(subList)) {
                    mappedSubLists.remove(subList);
                }
            }
        }
        knLogger.debug(methodName, "EXIT : List size returned - ", mappedSubLists.size());
        return mappedSubLists;
    }


    public static Collection<String> filterFinalPrivateListMembers(Collection<KnCorpSubscriberDTO>
                                                                           privateSubscrMemberList, Collection<KnCorpSubscriberDTO> mdnsToBeAddedToPrivateList,
                                                                   KnMdnDetailsPersistDTO contactMdnPersistDto) {
        String methodName = "filterFinalPrivateListMembers(Collection<KnCorpSubscriberDTO>, " +
                "Collection<KnCorpSubscriberDTO>,KnMdnDetailsPersistDTO)";
        knLogger.info(methodName, "ENTRY");
        knLogger.debug(methodName, "Input Data passed privateSubscrMemberList - ", privateSubscrMemberList);
        knLogger.debug(methodName, "Input Data passed mdnsToBeAddedToPrivateList - ", mdnsToBeAddedToPrivateList);
        knLogger.debug(methodName, "Input Data passed mdnToBeRemoved - ", contactMdnPersistDto);
        Collection<String> mdnList = new ArrayList<String>();
        for (KnCorpSubscriberDTO subscr : privateSubscrMemberList) {
            mdnList.add(subscr.getMdn());
        }
        for (String mdn : contactMdnPersistDto.getMdnList()) {
            mdnList.remove(mdn);
        }
        for (KnCorpSubscriberDTO subscr : mdnsToBeAddedToPrivateList) {
            mdnList.add(subscr.getMdn());
        }
        knLogger.debug(methodName, "EXIT : List returned - ", KnGDPRTemplate.mdnList(mdnList));
        return mdnList;
    }

    public static String getDirectoryURI(String mdn) {
        String methodName = "getDirectoryURI(String)";
        String documentURI;

        String appUId = com.kodiak.xdms.server.common.resources.KnConstants.DIR_DOC_AUID;
        String documentType = com.kodiak.xdms.server.common.resources.KnConstants.DIR_DOC_TYPE;
        String documentName = com.kodiak.xdms.server.common.resources.KnConstants.DIR_DOC_NAME;
        String xui = generateXUI(mdn);

        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append(appUId).append("/");
        strBuffer.append(documentType).append("/");
        strBuffer.append(xui).append("/");
        strBuffer.append(documentName);

        documentURI = strBuffer.toString();

        knLogger.debug(methodName, "document uri - ", KnGDPRTemplate.mdnPart(documentURI));
        return documentURI;
    }

    /**
     * method to generate the XUI
     *
     * @param mdn String
     * @return String XUI
     */
    public static String generateXUI(String mdn) {
        String methodName = "generateXUI(String)";
        knLogger.debug(methodName, "generating XUI for mdn - ", KnGDPRTemplate.mdn(mdn));
        String xui = com.kodiak.xdms.server.common.resources.KnConstants.TEL_URI_TEMPLATE + mdn;
        knLogger.debug(methodName, "XUI for the MDN - ", KnGDPRTemplate.mdnPart(xui));
        return xui;
    }


    /**
     * @param mdn
     * @return
     */
    public static String getResourceListDocumentURI(String mdn) {
        String methodName = "getDocumentURI(String)";
        knLogger.debug(methodName, "generating SEL uri of Notification for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append("xcap-directory/folder%5B@auid=%22kn-corp-resource-lists%22%5D")
                .append("/entry%5B@uri=%22kn-corp-resource-lists/users/tel:+")
                .append(mdn)
                .append("/index%22%5D/@etag");
        knLogger.debug(methodName, "generated SEL uri of Notification for mdn - ",
                KnGDPRTemplate.mdn(mdn), " is - ", KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
    }

    public static String getAuthDocumentURI(String mdn) {
        String methodName = "getAuthDocumentURI(String)";
        knLogger.debug(methodName, "generating SEL uri of Notification for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append("xcap-directory/folder%5B@auid=%22kn-authorization-list%22%5D")
                .append("/entry%5B@uri=%22kn-authorization-list/users/tel:+")
                .append(mdn)
                .append("/index%22%5D/@etag");
        knLogger.debug(methodName, "generated SEL uri of Notification for mdn - ",
                KnGDPRTemplate.mdn(mdn), " is - ", KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
    }

    public static String getEmergDocumentURI(String mdn) {
        String methodName = "getEmergDocumentURI(String)";
        knLogger.debug(methodName, "generating SEL uri of Notification for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append("xcap-directory/folder%5B@auid=%22kn-emergency-config%22%5D")
                .append("/entry%5B@uri=%22kn-emergency-config/users/tel:+")
                .append(mdn)
                .append("/index%22%5D/@etag");
        knLogger.debug(methodName, "generated SEL uri of Notification for mdn - ",
                KnGDPRTemplate.mdn(mdn), " is - ", KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
    }

    public static String getAddlTGDocumentURI(String mdn) {
        String methodName = "getAddlTGDocumentURI(String)";
        knLogger.debug(methodName, "generating SEL uri of Notification for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append("xcap-directory/folder%5B@auid=%22org.openmobilealliance.group-usage-list%22%5D")
                .append("/entry%5B@uri=%22org.openmobilealliance.group-usage-list/users/tel:+")
                .append(mdn)
                .append("/index%22%5D/@etag");
        knLogger.debug(methodName, "generated SEL uri of Notification for mdn - ",
                KnGDPRTemplate.mdn(mdn), " is - ", KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
    }

    public static Map<String, KnOPDirChgDTO> addDeletedMemberGroupDoc(Map<String, KnOPDirChgDTO>
                                                                              etagMap, Collection<String> removedMdnList
            , int groupId, int groupEtag,Collection<String> externalMdnList,boolean isPreConfigGroup) {
        for (String mdn : removedMdnList) {
            KnOPDirChgDTO dir = etagMap.get(mdn);
            if (dir == null) {
                dir = new KnOPDirChgDTO();
            }
            Collection<KnOPDocChgDTO> docList = dir.getDocChgDTO();
            if (docList == null) {
                docList = new ArrayList<KnOPDocChgDTO>();
            }
            KnOPDocChgDTO doc = new KnOPDocChgDTO();
            doc.setDocUri(getGroupDocumentURI(mdn, groupId, com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value()));
            doc.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
            doc.setNewEtag(String.valueOf(groupEtag));
            if(!externalMdnList.isEmpty()&&externalMdnList.contains(mdn)){
                doc.setExternalCorpGroup(KnConstants.EXTERNAL_SUBSCRIBER);
            }
            if(isPreConfigGroup){
                doc.setIsPreConfigGroup(ENABLED);
            }
            docList.add(doc);
            dir.setDocChgDTO(docList);
            etagMap.put(mdn, dir);
        }
        return etagMap;
    }

    /**
     * @param mdn
     * @param groupId
     * @param documentChangeType
     * @return
     */
    public static String getGroupDocumentURI(String mdn, int groupId, int documentChangeType) {

        String methodName = "getGroupDocumentURI(String, int, int)";

        knLogger.debug(methodName, "generating DOC uri of Notification for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(100);
        if (documentChangeType == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
            strBuffer.append(xcapDirGroupFolder);
            strBuffer.append("/entry%5B@uri=%22kn-corp-groups/users/tel:+");
            strBuffer.append(mdn);
            strBuffer.append("/").append(groupId).append(".xml%22%5D/@etag");
        } else if (documentChangeType == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.ADD.value()) {
            strBuffer.append(xcapDirGroupFolder);
        } else if (documentChangeType == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value()) {
            strBuffer.append(xcapDirGroupFolder);
            strBuffer.append("/entry%5B@uri=%22kn-corp-groups/users/tel:+");
            strBuffer.append(mdn);
            strBuffer.append("/").append(groupId).append(".xml%22%5D");
        }
        knLogger.debug(methodName, "generated SEL uri of Notification for mdn - ",
                KnGDPRTemplate.mdn(mdn), " is - ", KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
    }

    public Map<String, KnOPDirChgDTO> setXapRootUri(Map<String, KnOPDirChgDTO> notificationMap,
                                                    String pttSreverId,
                                                    KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "setXapRootUri(Map<String, KnOPDirChgDTO>, String, KnPersisterTxn)";
        knLogger.entry(methodName, notificationMap != null ? KnGDPRTemplate.mapKeyMdn(notificationMap).size() : 0, pttSreverId, persisterTxn);
        List<String> mdnList = new ArrayList<String>();
        mdnList.addAll(notificationMap.keySet());
        knLogger.debug(methodName, "mdnList are ", KnGDPRTemplate.mdnList(mdnList));

        try {
            Map<String, String> xcapUriMap = genInfoUtil.getXCAPRootURI(mdnList, persisterTxn, false);
            for (String mdn : mdnList) {
                KnOPDirChgDTO dir = notificationMap.get(mdn);
                dir.setXcapRootURI(xcapUriMap.get(mdn));
                notificationMap.put(mdn, dir);
            }
        } catch (KnBOException e) {
            knLogger.error(methodName, "BO Exception", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage());
        }
        knLogger.exit(methodName, KnGDPRTemplate.mapKeyMdn(notificationMap));
        return notificationMap;
    }

    public static String getAddGroupEntryUri(String mdn, int groupId) {
        String methodName = "getAddGroupEntryUri(String ,int )";
        knLogger.debug(methodName, "generating ENTRY uri of Notification for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(100);
        strBuffer.append("kn-corp-groups/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/").append(groupId).append(".xml");
        knLogger.debug(methodName, "generated ENTRY uri of Notification for mdn - ",
                KnGDPRTemplate.mdn(mdn) + " is - " + KnGDPRTemplate.mdnUriTemplate(strBuffer.toString()));
        return strBuffer.toString();
}

    public Map<String, Collection<KnCorpSubscriberDTO>> filterMemberToBeAddedToSubscriber(Map<String, Collection<String>> currentContactListMap,
                                                                                          Map<String, Collection<KnCorpSubscriberDTO>> addedMembersMap) {
        String methodName = "filterMemberToBeAddedToSubscriber(Map<String, Collection<String>>,Map<String, Collection<KnCorpSubscriberDTO>>)";
        knLogger.debug(methodName, "ENTRY :");
        knLogger.debug(methodName, "currentContactListMap - ", currentContactListMap == null ? currentContactListMap:  KnGDPRTemplate.mapKeyValueListMdn(currentContactListMap),
                " addedMembersMap - ", addedMembersMap);
        Map<String, Collection<KnCorpSubscriberDTO>> subscriberNewMemberMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
        if (currentContactListMap == null || currentContactListMap.isEmpty()) {
            knLogger.debug(methodName, "Exit :  Subscriber Contact List Map :- ", KnGDPRTemplate.mapKeyMdn(addedMembersMap));
            return addedMembersMap;
        }
        for (Map.Entry<String, Collection<KnCorpSubscriberDTO>> entry : addedMembersMap.entrySet()) {
            String mdn = entry.getKey();
            Set<KnCorpSubscriberDTO> memberToBeAddedToDB = new HashSet<KnCorpSubscriberDTO>();
            Collection<KnCorpSubscriberDTO> addedMdnList = entry.getValue();
            Set<KnCorpSubscriberDTO> addedMdnSet = new HashSet<>();
            if (addedMdnList != null) {
                addedMdnSet = new HashSet(addedMdnList);
            }
            Collection<String> currContactList = currentContactListMap.get(mdn);
            if (currContactList == null || currContactList.isEmpty()) {
                for (KnCorpSubscriberDTO subsc : addedMdnList) {
                    if (!subsc.getMdn().equals(mdn)) {
                        memberToBeAddedToDB.add(subsc);
                    }
                }
            } else if (addedMdnList != null && !addedMdnList.isEmpty()) {
                for (KnCorpSubscriberDTO subscriberDTO : addedMdnSet) {
                    String contactMdn = subscriberDTO.getMdn();
                    if (contactMdn.equals(mdn)) {
                        continue;
                    }
                    if (!currContactList.contains(contactMdn)) {
                        memberToBeAddedToDB.add(subscriberDTO);
                    }
                }
            }
            subscriberNewMemberMap.put(mdn, memberToBeAddedToDB);
        }

        knLogger.debug(methodName, "Exit :  Subscriber Contact List Map :- ", subscriberNewMemberMap.size());
        knLogger.debug(methodName, "Exit :  Subscriber Contact List Map :- ", KnGDPRTemplate.mapKeyMdn(subscriberNewMemberMap));
        return subscriberNewMemberMap;
    }

    /**
     * This method accept the currect group members and members to be added to the group
     * and filters out the the added members in request which are not present in current member list.
     *
     * @param currectGroupMemberMap
     * @param groupMembersInRequestMap
     * @return Map<Integer, Collection<String>>, for each groupIds members to added in group member list table
     */
    public Map<Integer, Collection<KnCorpSubscriberDTO>> filterGroupMembersTobeAdded(Map<Integer, Collection<String>> currectGroupMemberMap,
                                                                                     Map<Integer, Collection<KnCorpSubscriberDTO>> groupMembersInRequestMap) {

        String methodName = "filterGroupMembersTobeAdded(Map<Integer, Collection<String>>, Map<Integer, Collection<String>>)";
        knLogger.debug(methodName, "ENTRY : currectGroupMemberMap :- ", KnGDPRTemplate.mapMdnAsValue(currectGroupMemberMap), " groupMembersInRequestMap :- ", groupMembersInRequestMap);

        Map<Integer, Collection<KnCorpSubscriberDTO>> groupMembersMap = new HashMap<Integer, Collection<KnCorpSubscriberDTO>>();
        Collection<Integer> groupIds = groupMembersInRequestMap.keySet();

        if (currectGroupMemberMap == null || currectGroupMemberMap.isEmpty()) {
            return groupMembersInRequestMap;
        }
        for (Integer groupId : groupIds) {
            Collection<String> currentMembers = currectGroupMemberMap.get(groupId);
            Collection<KnCorpSubscriberDTO> membersInRequest = groupMembersInRequestMap.get(groupId);
            Collection<KnCorpSubscriberDTO> membersToBeAdded = new ArrayList<KnCorpSubscriberDTO>();
            for (KnCorpSubscriberDTO member : membersInRequest) {
                if (currentMembers == null || currentMembers.isEmpty()) {
                    membersToBeAdded.addAll(membersInRequest);
                    break;
                } else if (!currentMembers.contains(member.getMdn())) {
                    membersToBeAdded.add(member);
                }
            }
            groupMembersMap.put(groupId, membersToBeAdded);
        }
        knLogger.debug(methodName, "Exit :  groupMembersMap :- ", groupMembersMap);
        return groupMembersMap;
    }

    public Map<String, Collection<KnCorpSubscriberDTO>> filterMemberToBeAddedToPrivateList(Map<String, Collection<KnCorpSubscriberDTO>> subscPvtContactList,
                                                                                           Collection<KnCorpSubscriberDTO> dispatcherSubscriber) {
        String methodName = "filterMemberToBeAddedToPrivateList(Map<String, Collection<KnCorpSubscriberDTO>>, Collection<KnCorpSubscriberDTO>)";
        knLogger.debug(methodName, "subscPvtContactList :- ", KnGDPRTemplate.mapKeyMdn(subscPvtContactList));
        knLogger.debug(methodName, "dispatcherSubscriber :- ", dispatcherSubscriber);
        Map<String, Collection<KnCorpSubscriberDTO>> memberAddMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
        for (Map.Entry<String, Collection<KnCorpSubscriberDTO>> entry : subscPvtContactList.entrySet()) {
            Collection<KnCorpSubscriberDTO> mdnList = entry.getValue();
            String mdn = entry.getKey();
            knLogger.debug(methodName, "mdndasgvas :- ", KnGDPRTemplate.mdn(mdn));
            KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
            subsc.setMdn(mdn);
            if (mdnList.contains(subsc)) {
                mdnList.remove(subsc);
            }
            knLogger.debug(methodName, "mdnListdgvsa :- ", mdnList);
            Collection<KnCorpSubscriberDTO> mdnToBeAdded = new ArrayList<KnCorpSubscriberDTO>();
            for (KnCorpSubscriberDTO dispatchSub : dispatcherSubscriber) {
                if (!mdnList.contains(dispatchSub) && !dispatchSub.getMdn().equals(mdn)) {
                    mdnToBeAdded.add(dispatchSub);
                }
                //
              /*  if (dispatchSub.getMdn().equals(mdn)) {
                    mdnToBeAdded.remove(dispatchSub);
                }*/
            }

            knLogger.debug(methodName, "mdnToBeAdded :- ", mdnToBeAdded);
            if (mdnToBeAdded != null && !mdnToBeAdded.isEmpty()) {
                memberAddMap.put(mdn, mdnToBeAdded);
            }


        }
        knLogger.debug(methodName, "Exit :  memberAddMap :- ", KnGDPRTemplate.mapKeyMdn(memberAddMap));
        return memberAddMap;
    }

    public Map<String, Collection<KnCorpSubscriberDTO>> filterMemberToBeAddedToLocWatcherPrivateList(Map<String, Collection<KnCorpSubscriberDTO>> locWatcherPvtContactList,
                                                                                                     Map<String, Collection<KnCorpSubscriberDTO>> locWatcherContactListMapDTO) {
        String methodName = "filterMemberToBeAddedToLocWatcherPrivateList(Map<String, Collection<KnCorpSubscriberDTO>>, Map<String, Collection<KnCorpSubscriberDTO>>)";
        knLogger.debug(methodName, "locWatcherPvtContactList :- ", locWatcherPvtContactList);
        knLogger.debug(methodName, "locWatcherContactListMapDTO :- ", locWatcherContactListMapDTO);

        /*Here is the much awaited example:

        locWatcherPvtContactList(M1 - m2, m3, m4; M2 - m4, m5, m7; M3 - m5, m8, m9; M4 - )
        locWatcherContactListMapDTO(M1 - m2, m3, m7; M2 - m4, m6, m9; M3 - m5, m8, m9; M4 - m5, m8, m9)

        for(locWatcherPvtContactList){
            for(locWatcherContactListMapDTO){
                M1: if(!(m2, m3, m4).contains(m2)) : No new contacts, m2 already exists in DB.
                M1: if(!(m2, m3, m4).contains(m3)) : No new contacts, m3 already exists in DB.
                M1: if(!(m2, m3, m4).contains(m7)) : Yes, got it. m7 new contact, so will put m7 as a contact for M1 locWatcher. Result: Map<M1, m7>
                Same thing for M2, M3.

                Now will see for M4.
                M4: if(!().contains(m5)) : Yes, got it. m5 new contact, so will put m5 as a contact for M1 locWatcher. Result: Map<M4, (m5)>
                M4: if(!().contains(m8)) : Yes, got it. m5 new contact, so will put m8 as a contact for M1 locWatcher. Result: Map<M4, (m5, m8)>
                M4: if(!().contains(m9)) : Yes, got it. m5 new contact, so will put m9 as a contact for M1 locWatcher. Result: Map<M4, (m5, m8, m9)>

                Final Result after all the iteration:
                Map<(M1,(m7));(M2,(m6, m9));(M4,(m5, m8, m9))>

                Note: No contact for M3; because we are doing empty check.
            }
        }*/

        Map<String, Collection<KnCorpSubscriberDTO>> memberAddMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
        for (Map.Entry<String, Collection<KnCorpSubscriberDTO>> entry : locWatcherPvtContactList.entrySet()) {
            Collection<KnCorpSubscriberDTO> mdnToBeAdded = new ArrayList<>();
            String locMdn = entry.getKey();
            Collection<KnCorpSubscriberDTO> locWatcherPvtContact = entry.getValue();
            Collection<KnCorpSubscriberDTO> locWatcherContactDto = locWatcherContactListMapDTO.get(locMdn);
            if (locWatcherContactDto != null && !locWatcherContactDto.isEmpty()) {
                for (KnCorpSubscriberDTO subsDetails : locWatcherContactDto) {
                    if (!locWatcherPvtContact.contains(subsDetails) && !subsDetails.getMdn().equals(locMdn)) {
                        mdnToBeAdded.add(subsDetails);
                    }
                }
            }
            //knLogger.debug(methodName, "mdnToBeAdded :- ", mdnToBeAdded);
            if (!mdnToBeAdded.isEmpty()) {
                memberAddMap.put(locMdn, mdnToBeAdded);
            }
        }
        knLogger.debug(methodName, "Exit :  memberAddMap :- ", KnGDPRTemplate.mapKeyMdn(memberAddMap));
        return memberAddMap;
    }

    public Map<Integer, Map<String, Collection<String>>> filterAddedRemovedMembers(Map<Integer, Collection<String>> groupDistList,
                                                                                   Collection<KnCorpSubscriberDTO> uniqueMdnToBeAddedToGroup,
                                                                                   Collection<KnCorpSubscriberDTO> uniqueMdnToBeDeletedToGroup,
                                                                                   int corpId) {
        String methodName = "filterAddedRemovedMembers(Map<Integer, Collection<String>>, Collection<KnCorpSubscriberDTO>," +
                " Collection<KnCorpSubscriberDTO>, int)";
        Map<Integer, Collection<String>> grpAddMemberList = new HashMap<Integer, Collection<String>>();
        Map<Integer, Collection<String>> deleteMemberlist = new HashMap<Integer, Collection<String>>();
        Map<Integer, Map<String, Collection<String>>> grpMemberDetailsMap = new HashMap<Integer, Map<String, Collection<String>>>();
        for (int grpId : groupDistList.keySet()) {
            Collection<String> currentGrpMembers = groupDistList.get(grpId);
            Collection<String> memberMdns = new ArrayList<String>();
            if (uniqueMdnToBeAddedToGroup != null && !uniqueMdnToBeAddedToGroup.isEmpty()) {
                for (KnCorpSubscriberDTO subsc : uniqueMdnToBeAddedToGroup) {
                    if (subsc.getCorpId() == corpId) {
                        memberMdns.add(subsc.getMdn());
                    }
                }
            }
            Collection<String> memberToAdd = new ArrayList<String>();
            if (!currentGrpMembers.isEmpty()) {
                for (String mdn : memberMdns) {
                    if (!currentGrpMembers.contains(mdn)) {
                        memberToAdd.add(mdn);
                    }
                }
            } else {
                memberToAdd.addAll(memberMdns);
            }
            grpAddMemberList.put(grpId, memberToAdd);

            Collection<String> deletedMembersList = new ArrayList<String>();
            if (uniqueMdnToBeDeletedToGroup != null && !uniqueMdnToBeDeletedToGroup.isEmpty()) {
                for (KnCorpSubscriberDTO subsc : uniqueMdnToBeDeletedToGroup) {
                    if (subsc.getCorpId() == corpId) {
                        deletedMembersList.add(subsc.getMdn());
                    }
                }
                deleteMemberlist.put(grpId, deletedMembersList);
            }
            knLogger.debug(methodName, "groupMemberMap to be deleted- ", KnGDPRTemplate.mdnList(deletedMembersList));

            Map<String, Collection<String>> addDeleteMemberMap = new HashMap<String, Collection<String>>();
            addDeleteMemberMap.put(KnConstants.ADDED_MEMBERS, grpAddMemberList.get(grpId));
            addDeleteMemberMap.put(KnConstants.DELTED_MEMBERS, deleteMemberlist.get(grpId));
            grpMemberDetailsMap.put(grpId, addDeleteMemberMap);
            grpMemberDetailsMap.put(grpId, addDeleteMemberMap);

        }
        knLogger.debug(methodName, "grpMemberDetailsMap- ", grpMemberDetailsMap);
        return grpMemberDetailsMap;
    }

    public Map<Integer, Map<String, Collection<String>>> filterAddedRemovedMembersForAllSubscribers(Map<Integer, Collection<String>> groupDistList,
                                                                                   Collection<KnCorpSubscriberDTO> uniqueMdnToBeAddedToGroup,
                                                                                   Collection<KnCorpSubscriberDTO> uniqueMdnToBeDeletedToGroup,
                                                                                   int corpId) {
        String methodName = "filterAddedRemovedMembersForAllSubscribers(Map<Integer, Collection<String>>, Collection<KnCorpSubscriberDTO>," +
                " Collection<KnCorpSubscriberDTO>, int)";
        //copied above method and changed corpid checks
        Map<Integer, Collection<String>> grpAddMemberList = new HashMap<Integer, Collection<String>>();
        Map<Integer, Collection<String>> deleteMemberlist = new HashMap<Integer, Collection<String>>();
        Map<Integer, Map<String, Collection<String>>> grpMemberDetailsMap = new HashMap<Integer, Map<String, Collection<String>>>();
        for (int grpId : groupDistList.keySet()) {
            Collection<String> currentGrpMembers = groupDistList.get(grpId);
            Collection<String> memberMdns = new ArrayList<String>();
            if (uniqueMdnToBeAddedToGroup != null && !uniqueMdnToBeAddedToGroup.isEmpty()) {
                for (KnCorpSubscriberDTO subsc : uniqueMdnToBeAddedToGroup) {
                        memberMdns.add(subsc.getMdn());
                }
            }
            Collection<String> memberToAdd = new ArrayList<String>();
            if (!currentGrpMembers.isEmpty()) {
                for (String mdn : memberMdns) {
                    if (!currentGrpMembers.contains(mdn)) {
                        memberToAdd.add(mdn);
                    }
                }
            } else {
                memberToAdd.addAll(memberMdns);
            }
            grpAddMemberList.put(grpId, memberToAdd);

            Collection<String> deletedMembersList = new ArrayList<String>();
            if (uniqueMdnToBeDeletedToGroup != null && !uniqueMdnToBeDeletedToGroup.isEmpty()) {
                for (KnCorpSubscriberDTO subsc : uniqueMdnToBeDeletedToGroup) {
                        deletedMembersList.add(subsc.getMdn());
                }
                deleteMemberlist.put(grpId, deletedMembersList);
            }
            knLogger.debug(methodName, "groupMemberMap to be deleted- ", KnGDPRTemplate.mdnList(deletedMembersList));

            Map<String, Collection<String>> addDeleteMemberMap = new HashMap<String, Collection<String>>();
            addDeleteMemberMap.put(KnConstants.ADDED_MEMBERS, grpAddMemberList.get(grpId));
            addDeleteMemberMap.put(KnConstants.DELTED_MEMBERS, deleteMemberlist.get(grpId));
            grpMemberDetailsMap.put(grpId, addDeleteMemberMap);
        }
        knLogger.debug(methodName, "grpMemberDetailsMap- ", grpMemberDetailsMap);
        return grpMemberDetailsMap;
    }


    public Map<String, KnOPDirChgDTO> formSubscriberNotification(Map<Integer, Collection<String>> currentGroupMemberListMap, Map<Integer, Integer>
            groupEtagMap, int documntChngType, Map<String, KnOPDirChgDTO> eTags, Map<Integer, KnCorpGroupDTO> groupDetailsMap, Integer groupCreatedBy) {
        String methodName = "formSubscriberNotification(Map<Integer, Collection<String>> ,  Map<Integer, Integer>, int, " +
                "Map<String, KnOPDirChgDTO>)";

        knLogger.debug(methodName, "currentGroupMemberListMap - ", KnGDPRTemplate.mapMdnAsValue(currentGroupMemberListMap), " , groupEtagMap - ", groupEtagMap,
                ", documntChngType - ", documntChngType, " , eTags - ", KnGDPRTemplate.mapKeyMdn(eTags), " groupDetailsMap - ", groupDetailsMap);

        if (eTags == null) {
            eTags = new HashMap<String, KnOPDirChgDTO>();
        }
        Collection<Integer> groupIdList = groupEtagMap.keySet();
        for (int grpId : groupIdList) {
            int etag = groupEtagMap.get(grpId);
            int prevEtag = etag - 1;
            Collection<String> currntMembers = currentGroupMemberListMap.get(grpId);
            if (currntMembers != null && !currntMembers.isEmpty()) {
                Set<String> currentMembers = new HashSet<>(currntMembers);
                for (String mdn : currentMembers) {
                    KnOPDirChgDTO dirChgDto = eTags.get(mdn);
                    if (isObjectNull(dirChgDto)) {
                        dirChgDto = new KnOPDirChgDTO();
                        dirChgDto.setDirUri(getDirectoryURI(mdn));
                    }
                    KnOPDocChgDTO docChgDto = new KnOPDocChgDTO();
                    docChgDto.setDocType(documntChngType);
                    docChgDto.setDocumentChgType(documntChngType);
                    docChgDto.setNewEtag(String.valueOf(etag));
                    docChgDto.setPrevEtag(String.valueOf(prevEtag));
                    docChgDto.setDocUri(KnCorpCommonInfoUtil.getGroupDocumentURI(mdn, grpId, documntChngType));
                    docChgDto.setGroupId(grpId);
                    docChgDto.setOsmListChanged(true);
                    if(null != groupCreatedBy && CREATED_BY.ABDG.value()== groupCreatedBy)
                    {
                    docChgDto.setIsAbdgGroup(KnConstants.IS_ABDG_GROUP);
                    }

                    knLogger.debug("documntChngType :",documntChngType," mdn :",mdn," groupDetailsMap ",groupDetailsMap);
                    if (documntChngType == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.ADD.value()) {
                        if (groupDetailsMap != null && !groupDetailsMap.isEmpty()) {
                            KnCorpGroupDTO corpGroupDTO = groupDetailsMap.get(grpId);
                            if (null != corpGroupDTO.getMcxGrpInd() && KnConstants.MCX_GROUP_INDICATOR == corpGroupDTO.getMcxGrpInd()) {
                                docChgDto.setIsMcxGroup(corpGroupDTO.getMcxGrpInd());
                            }
                            // Set videoPermission - always set it (even if null, set default)
                            Integer videoPermission = corpGroupDTO.getVideoPermission();
                            if (videoPermission != null) {
                                docChgDto.setVideoPermission(videoPermission);
                                knLogger.debug(methodName, "ADD: Setting videoPermission from corpGroupDTO - groupId:", grpId, ", videoPermission:", videoPermission);
                            } else {
                                // If null, set default value to ensure it flows through
                                docChgDto.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                                knLogger.debug(methodName, "ADD: videoPermission is NULL, setting default - groupId:", grpId, ", default:", KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                            }
                        Collection<String> groupExternalMdns = corpGroupDTO.getExternalMdnList();
                        knLogger.debug(methodName, " groupExternalMdns :", groupExternalMdns, " mdn :", mdn);
                        if (groupExternalMdns!=null&&!groupExternalMdns.isEmpty() && groupExternalMdns.contains(mdn)) {
                            docChgDto.setExternalCorpGroup(KnConstants.EXTERNAL_SUBSCRIBER);
                        }
                    }
                    }
                    if (null != groupDetailsMap) {
                        KnCorpGroupDTO groupDTO = groupDetailsMap.get(grpId);
                        if(null != groupDTO && groupDTO.getIsPreConfiguredGroup()!=null&&groupDTO.getIsPreConfiguredGroup().equals(ENABLED)){
                            docChgDto.setIsPreConfigGroup(PRECONFIG_GROUP);
                        }
                        if (null != groupDTO && documntChngType == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                            if (groupDTO.getGroupType() == KnConstants.BROADCAST_GROUP) {
                                docChgDto.setGroupType(groupDTO.getGroupType());
                                if (null != groupDTO.getGroupMemCount()) {
                                    docChgDto.setGroupMemCount(groupDTO.getGroupMemCount());
                                }
                            }
                            // Set videoPermission - always set it (even if null, set default)
                            Integer videoPermission = groupDTO.getVideoPermission();
                            if (videoPermission != null) {
                                docChgDto.setVideoPermission(videoPermission);
                                knLogger.debug(methodName, "REPLACE: Setting videoPermission from groupDTO - groupId:", grpId, ", videoPermission:", videoPermission);
                            } else {
                                // If null, set default value to ensure it flows through
                                docChgDto.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                                knLogger.debug(methodName, "REPLACE: videoPermission is NULL, setting default - groupId:", grpId, ", default:", KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                            }
                            Collection<String> groupExternalMdns = groupDTO.getExternalMdnList();
                            knLogger.debug(methodName, " Mod groupExternalMdns :", groupExternalMdns, " mdn :", mdn);
                            if (groupExternalMdns != null && !groupExternalMdns.isEmpty() && groupExternalMdns.contains(mdn)) {
                                docChgDto.setExternalCorpGroup(KnConstants.EXTERNAL_SUBSCRIBER);
                            }
                        }
                        if (null != groupDTO && null != groupDTO.getGroupDisplayName()) {
                            docChgDto.setGroupDisplayName(groupDTO.getGroupDisplayName());
                        }
                    }

                    if (docChgDto.getVideoPermission() == null) {
                        docChgDto.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                        knLogger.debug(methodName, "FALLBACK: videoPermission was not set, using default - groupId:", grpId, ", default:", KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                    }

                    docChgDto.setEntryUri(KnCorpCommonInfoUtil.getAddGroupEntryUri(mdn, grpId));
                    Collection<KnOPDocChgDTO> dirDocLst = dirChgDto.getDocChgDTO();
                    if (isObjectNull(dirDocLst)) {
                        dirDocLst = new ArrayList<KnOPDocChgDTO>();
                    }
                    dirDocLst.add(docChgDto);
                    dirChgDto.setDocChgDTO(dirDocLst);
                    eTags.put(mdn, dirChgDto);
                }
            }

        }
        knLogger.debug(methodName, "eTags - ", eTags);
        return eTags;
    }

    public Map<String, KnOPDirChgDTO> formSubscriberNotification(String mdn, Map<Integer, Integer> groupEtagMap,
                                                                 int documntChngType, Map<String, KnOPDirChgDTO> eTags) {
        String methodName = "formSubscriberNotification(Map<Integer, Collection<String>> ,  Map<Integer, Integer>, int, " +
                "Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, " , groupEtagMap - ", groupEtagMap, ", documntChngType - ", documntChngType, " , eTags - ", eTags);
        if (eTags == null) {
            eTags = new HashMap<String, KnOPDirChgDTO>();
        }
        Collection<Integer> groupIdList = groupEtagMap.keySet();
        for (int grpId : groupIdList) {
            int etag = groupEtagMap.get(grpId);
            int prevEtag = etag - 1;
            KnOPDirChgDTO dirChgDto = eTags.get(mdn);
            if (isObjectNull(dirChgDto)) {
                dirChgDto = new KnOPDirChgDTO();
                dirChgDto.setDirUri(getDirectoryURI(mdn));
            }
            KnOPDocChgDTO docChgDto = new KnOPDocChgDTO();
            docChgDto.setDocType(documntChngType);
            docChgDto.setDocumentChgType(documntChngType);
            docChgDto.setNewEtag(String.valueOf(etag));
            docChgDto.setPrevEtag(String.valueOf(prevEtag));
            docChgDto.setDocUri(KnCorpCommonInfoUtil.getGroupDocumentURI(mdn, grpId, documntChngType));
            docChgDto.setGroupId(grpId);
            docChgDto.setEntryUri(KnCorpCommonInfoUtil.getAddGroupEntryUri(mdn, grpId));
            Collection<KnOPDocChgDTO> dirDocLst = dirChgDto.getDocChgDTO();
            if (isObjectNull(dirDocLst)) {
                dirDocLst = new ArrayList<KnOPDocChgDTO>();
            }
            dirDocLst.add(docChgDto);
            dirChgDto.setDocChgDTO(dirDocLst);
            eTags.put(mdn, dirChgDto);
        }
        knLogger.debug(methodName, "eTags - ", eTags);
        return eTags;
    }

    public Map<Integer, Collection<String>> filterDeletedGroupMembers(Map<Integer, Collection<String>> currentGroupMembers,
                                                                      Collection<String> intMemInSublist) {
        String methodName = "filterDeletedGroupMembers(Map<Integer, Collection<String>>, Collection<String>)";
        knLogger.debug(methodName, "currentGroupMembers - ", currentGroupMembers, " , intMemInSublist - ",
                intMemInSublist == null ? intMemInSublist : KnGDPRTemplate.mdnList(intMemInSublist));

        Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();
        for (int groupId : currentGroupMembers.keySet()) {
            Collection<String> memberList = currentGroupMembers.get(groupId);
            Collection<String> deletedMembers = new ArrayList<String>();
            if (memberList != null) {
                for (String mdn : intMemInSublist) {
                    if (!memberList.contains(mdn)) {
                        deletedMembers.add(mdn);
                    }
                }
            } else {
                deletedMembers.addAll(intMemInSublist);
            }
            deletedMembersMap.put(groupId, deletedMembers);
        }
        knLogger.debug(methodName, "deletedMembersMap - ", deletedMembersMap);

        return deletedMembersMap;
    }

    /**
     * This method will iretare through the current group distribution list and initial group distribution list to find the
     * actual deleted members for each groupid.
     *
     * @param currentGroupDistMap
     * @param initialGrpDistMap
     * @return
     */
    public Map<Integer, Collection<String>> filterDeletedGroupMembers(Map<Integer, Collection<String>> currentGroupDistMap,
                                                                      Map<Integer, Collection<String>> initialGrpDistMap) {
        String methodName = "filterDeletedGroupMembers(Map, Map)";
        knLogger.debug(methodName, "currentGroupDistMap - ", currentGroupDistMap, " , initialGrpDistMap - ", initialGrpDistMap);
        Map<Integer, Collection<String>> deletedMembersMap = new HashMap<>();
        for (int groupId : currentGroupDistMap.keySet()) {
            Collection<String> currentMemList = currentGroupDistMap.get(groupId);
            Collection<String> initialMemList = initialGrpDistMap.get(groupId);
            Collection<String> deletedMembers = new ArrayList<>();
            for (String mdn : initialMemList) {
                if (!currentMemList.contains(mdn)) {
                    deletedMembers.add(mdn);
                }
            }
            deletedMembersMap.put(groupId, deletedMembers);
        }
        knLogger.debug(methodName, "deletedMembersMap - ", deletedMembersMap);
        return deletedMembersMap;
    }


    public static void populateXdmResponseFroomHook(KnCorpHookRespDTO hookResponseDTO, KnCorpResponseDTO corpResponseDTO) {
        int status = hookResponseDTO.getStatus();
        corpResponseDTO.setStatus(status);
        corpResponseDTO.setStatusCode(hookResponseDTO.getStatusCode());
        corpResponseDTO.setMessage(hookResponseDTO.getMessage());
        /////- populate response info //hookResponseDTO.getCustomParamMap();
        Collection<KnCorpFailedData> failedDataList = hookResponseDTO.getFailedDataList();
        if (status != com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.SUCCESS.value() || failedDataList != null && failedDataList.isEmpty()) {
            corpResponseDTO.setFailedDataList(failedDataList);
        }
    }


    public Map<String, Collection<KnCorpSubscriberDTO>> filterMemberForSubscriber(Map<String, Collection<KnCorpSubscriberDTO>> currentContactListMap,
                                                                                  Map<String, Collection<KnCorpSubscriberDTO>> addedMembersMap) {
        String methodName = "filterMemberForSubscriber(Map<String, Collection<KnCorpSubscriberDTO>>,Map<String, Collection<KnCorpSubscriberDTO>>)";
        knLogger.debug(methodName, "ENTRY :");
        knLogger.debug(methodName, "currentContactListMap - ", currentContactListMap == null ? currentContactListMap : KnGDPRTemplate.mapKeyMdn(currentContactListMap),
                " addedMembersMap - ", addedMembersMap == null ? addedMembersMap : KnGDPRTemplate.mapKeyMdn(addedMembersMap));
        Map<String, Collection<KnCorpSubscriberDTO>> subscriberNewMemberMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
        if (currentContactListMap == null || currentContactListMap.isEmpty()) {
            return addedMembersMap;
        }

        for (Map.Entry<String, Collection<KnCorpSubscriberDTO>> entry : addedMembersMap.entrySet()) {
            Collection<KnCorpSubscriberDTO> addedMdnList = entry.getValue();
            String mdn = entry.getKey();
            KnCorpSubscriberDTO subscriber = new KnCorpSubscriberDTO();
            subscriber.setMdn(mdn);
            Collection<KnCorpSubscriberDTO> memberToBeAddedToDB = new ArrayList<KnCorpSubscriberDTO>();
            Collection<KnCorpSubscriberDTO> currContactList = currentContactListMap.get(mdn);
            if (currContactList == null || currContactList.isEmpty()) {
                memberToBeAddedToDB.addAll(addedMdnList);
            } else if (addedMdnList != null && !addedMdnList.isEmpty()) {
                for (KnCorpSubscriberDTO subsc : addedMdnList) {
                    if (subsc.equals(subscriber)) {
                        continue;
                    }
                    if (!currContactList.contains(subsc)) {
                        memberToBeAddedToDB.add(subsc);
                    }
                }
            }
            if (memberToBeAddedToDB != null && !memberToBeAddedToDB.isEmpty()) {
                subscriberNewMemberMap.put(mdn, memberToBeAddedToDB);
            }
        }

        knLogger.debug(methodName, "Exit :  Subscriber Contact List Map :- ", KnGDPRTemplate.mapKeyMdn(subscriberNewMemberMap));

        return subscriberNewMemberMap;
    }

    /**
     * This method is called to get the actually deleted members from the DB
     *
     * @param delGrmMemMap    members that might be deleted from the group
     * @param delGrpMemStatus determines the actual members deleted from the group   1 - deleted, 0 - not deleted
     * @return
     */
    public static Map<String, Collection<String>> getDeletedMembers(LinkedHashMap<Integer, LinkedList<String>> delGrmMemMap,
                                                                    LinkedHashMap<Integer, LinkedList<Integer>> delGrpMemStatus) {
        Map<String, Collection<String>> delGrpMemMap =  new HashMap<>();
        if (delGrmMemMap != null && !delGrmMemMap.isEmpty()) {
            for (Map.Entry<Integer, LinkedList<String>> entry : delGrmMemMap.entrySet()) {
                List<String> delGrpMemList = new ArrayList<String>();
                int grpId = entry.getKey();
                LinkedList<String> delMdnList = entry.getValue();
                if (delGrpMemStatus != null) {
                    LinkedList<Integer> delStatus = delGrpMemStatus.get(grpId);
                    if (delStatus != null) {
                        Iterator ite = delMdnList.iterator();
                        for (Integer result : delStatus) {
                            String memberMdn = (String) ite.next();
                            if (result > 0) {
                                delGrpMemList.add(memberMdn);
                            }
                        }
                    }
                }
                delGrpMemMap.put(String.valueOf(grpId), delGrpMemList);
            }
        }
        return delGrpMemMap;
    }

    public static Map<String, KnOPDirChgDTO> formXcapDiffNotification(Map<String, KnOPDirChgDTO> etagMap,
                                                                      Map<String, Collection<KnCorpSubscriberDTO>> finalMissingContacts,
                                                                      LinkedHashMap<String, LinkedList<String>> deletedContacts,
                                                                      Map<String, Collection<KnCorpSubscriberDTO>> modifiedContactMap,
                                                                      Map<Integer, Collection<KnCorpContactDTO>> addedGroupMembers,
                                                                      LinkedHashMap<Integer, LinkedList<String>> deletedGrpMembers, String groupName,
                                                                      Collection<KnCorpGroupMemberDTO> modifiedGrpMembers,
                                                                      Map<Integer, Collection<String>> distInfo,
                                                                      LinkedHashMap<String, LinkedList<Integer>> delContactStatus,
                                                                      LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus, Collection<String> currentGroupMemberList) {

        return formXcapDiffNotification(etagMap, finalMissingContacts, deletedContacts, modifiedContactMap, addedGroupMembers,
                deletedGrpMembers, groupName, modifiedGrpMembers, distInfo, delContactStatus, delGroupMemStatus, null, false, false, currentGroupMemberList, null);

    }
    /**
     * Method forms the new notification DTO from the data passed after every operation.
     * This forms the xcap-doc diff notifications.
     *
     * @param etagMap
     * @param finalMissingContacts
     * @param deletedContacts
     * @param modifiedContactMap
     * @param addedGroupMembers
     * @param deletedGrpMembers
     * @param groupName
     * @param modifiedGrpMembers
     * @param distInfo
     * @param delContactStatus
     * @param delGroupMemStatus    @return
     */
    public static Map<String, KnOPDirChgDTO> formXcapDiffNotification(Map<String, KnOPDirChgDTO> etagMap,
                                                                      Map<String, Collection<KnCorpSubscriberDTO>> finalMissingContacts,
                                                                      LinkedHashMap<String, LinkedList<String>> deletedContacts,
                                                                      Map<String, Collection<KnCorpSubscriberDTO>> modifiedContactMap,
                                                                      Map<Integer, Collection<KnCorpContactDTO>> addedGroupMembers,
                                                                      LinkedHashMap<Integer, LinkedList<String>> deletedGrpMembers, String groupName,
                                                                      Collection<KnCorpGroupMemberDTO> modifiedGrpMembers, Map<Integer, Collection<String>> distInfo,
                                                                      LinkedHashMap<String, LinkedList<Integer>> delContactStatus,
                                                                      LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus, Integer avatar,
                                                                      boolean osmlistChanged, boolean isEmptyGroup, Collection<String> currentGroupMemberList,
                                                                      List<String> profileMdns) {
        String methodName = "formXcapDiffNotification()";

        knLogger.debug(methodName, "Entry :  - etagMap - ", etagMap == null ? etagMap : KnGDPRTemplate.mapKeyMdn(etagMap), " - finalMissingContacts -", finalMissingContacts, " - deletedContacts -", deletedContacts,
                " - addedGroupMembers -", addedGroupMembers, " - deletedGrpMembers -", deletedGrpMembers, " - groupName -", groupName
                , " - modifiedGrpMembers -", modifiedGrpMembers, " - distInfo - ", distInfo," delContactStatus :",delContactStatus, " - delGroupMemStatus - ", delGroupMemStatus," - avatar -", avatar,
                " osmlistChanged -",osmlistChanged," isEmptyGroup ",isEmptyGroup);
        Collection<KnCorpGroupMemberDTO> modifiedGrpMembersList = new ArrayList<KnCorpGroupMemberDTO>();
        if (modifiedGrpMembers != null && !modifiedGrpMembers.isEmpty()) {
            modifiedGrpMembersList.addAll(modifiedGrpMembers);
        }
        //get the actually deleted members
        Map<String, Collection<String>> delGrpMemMap = getDeletedMembers(deletedGrpMembers, delGroupMemStatus);
        for (String mdn : etagMap.keySet()) {
            KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
            int subsPV = dirChgDTO.getProtoVersion() != null ? Integer.parseInt(dirChgDTO.getProtoVersion()) : 0;
            Collection<KnOPDocChgDTO> docChgDTOS = dirChgDTO.getDocChgDTO();
            for (KnOPDocChgDTO docChgDTO : docChgDTOS) {
                if (docChgDTO.getDocUri().contains(APP_UID_CORP_GROUP)) {
                    Collection<KnCorpContactDTO> addedContactList = new ArrayList<KnCorpContactDTO>();
                    if (addedGroupMembers != null
                            && !addedGroupMembers.isEmpty()) {
                        addedContactList = addedGroupMembers.get(docChgDTO.getGroupId());
                    }

                    Collection<String> delGrpMembeList = new ArrayList<String>();
                    if (delGrpMemMap != null && !delGrpMemMap.isEmpty()) {
                        delGrpMembeList = delGrpMemMap.get(String.valueOf(docChgDTO.getGroupId()));
                    }
                    // setting groupname for the existing members
                    if (groupName != null) {
                        if ((delGrpMembeList != null && delGrpMembeList.contains(mdn)) || (addedContactList != null && addedContactList.contains(new KnCorpContactDTO(mdn)))) {
                        } else {
                            docChgDTO.setGroupName(groupName);
                        }
                    }
                    //tells if osm list changed if true will send group tag else member etag.
                    docChgDTO.setOsmListChanged(osmlistChanged);
                    if (avatar != null) {
                        if ((delGrpMembeList != null && delGrpMembeList.contains(mdn)) || (addedContactList != null && addedContactList.contains(new KnCorpContactDTO(mdn)))) {
                        } else {
                            docChgDTO.setAvatar(avatar);
                        }
                    }

                    Collection<KnCorpGroupMemberDTO> modifiedMembers = new ArrayList<KnCorpGroupMemberDTO>();
                    if (addedGroupMembers != null && !addedGroupMembers.isEmpty()) {
                        Collection<KnSubscriberDTO> addedgrpMembers = new ArrayList<KnSubscriberDTO>();
                        for (Map.Entry<Integer, Collection<KnCorpContactDTO>> entry : addedGroupMembers.entrySet()) {
                            Integer groupId = entry.getKey();
                            if (docChgDTO.getDocUri().contains(groupId + ".xml")) {
                                if (null != distInfo) {
                                    Collection<String> grpMemeColl = distInfo.get(groupId);
                                    if (null != grpMemeColl) {
                                        if (grpMemeColl.contains(mdn)) {
                                            Collection<KnCorpContactDTO> contactDTO = addedGroupMembers.get(groupId);
                                            if (!contactDTO.contains(new KnCorpContactDTO(mdn))) {
                                                for (KnCorpContactDTO contact : contactDTO) {
                                                    if (null != profileMdns && profileMdns.contains(contact.getMdn())) {
                                                        continue;
                                                    }
                                                    knLogger.debug(methodName,"diff- ", contact);
                                                    KnSubscriberDTO subscriberDTO = new KnSubscriberDTO();
                                                    subscriberDTO.setMdn(contact.getMdn());
                                                    subscriberDTO.setAliasMdn(contact.getAliasMdn());
                                                    subscriberDTO.setUserId(contact.getUserId());
                                                    String activefs2=contact.getSubsActiveFS2();
                                                    if (subsPV < PROTOCOL_VERSION_16 && activefs2 != null) {
                                                        //converting to activefs1
                                                        activefs2 = KnGeneralUtil.convertActiveFs2toHexActiveFs1(activefs2);
                                                        subscriberDTO.setActiveFS2(activefs2);
                                                    } else if (subsPV >= PROTOCOL_VERSION_16 && null != activefs2) {
                                                        activefs2 = KnGeneralUtil.calculateActiveFeatureSetBasedOnPv(activefs2, subsPV);
                                                    }
                                                    subscriberDTO.setActiveFS2(activefs2);
                                                    subscriberDTO.setSupervisory(contact.getSupervisory());
                                                    subscriberDTO.setLocWatcher(contact.getLocWatcher());
                                                    subscriberDTO.setClientType(contact.getClientType());
                                                    subscriberDTO.setContact_type(contact.getContact_type());
                                                    subscriberDTO.setMemberCorpId(contact.getCorpId());
                                                    subscriberDTO.setCallPermission(contact.getCallInitiatePermission()+","+contact.getCallReceivePermission()+","+contact.getInCallPermission());
                                                    subscriberDTO.setVideoCallPermission(contact.getVideoCallInitiatePermission()+","+contact.getVideoCallReceivePermission()+","+contact.getVideoInCallPermission());
                                                    subscriberDTO.setVideoCallInitiatePermission(contact.getVideoCallInitiatePermission());
                                                    subscriberDTO.setVideoCallReceivePermission(contact.getVideoCallReceivePermission());
                                                    subscriberDTO.setVideoInCallPermission(contact.getVideoInCallPermission());
                                                    subscriberDTO.setIsAffiliationEnabled(contact.getIsAffiliationEnabled());
                                                    // get the supervisory for the newly added members from the modifiedGrpMemmbersList & set the supervisory
                                                    if (modifiedGrpMembersList != null) {
                                                        for (KnCorpGroupMemberDTO groupMemberDTO : modifiedGrpMembersList) {
                                                            if (contact.getMdn().equals(groupMemberDTO.getMdn())) {
                                                                subscriberDTO.setSupervisory(groupMemberDTO.getSupervisory());
                                                                subscriberDTO.setLocWatcher(groupMemberDTO.getLocWatcher());
                                                                subscriberDTO.setCallPermission(groupMemberDTO.getCallInitiatePermission()+","+groupMemberDTO.getCallReceivePermission()+","+groupMemberDTO.getInCallPermission());
                                                                subscriberDTO.setVideoCallPermission(groupMemberDTO.getVideoCallInitiatePermission()+","+groupMemberDTO.getVideoCallReceivePermission()+","+groupMemberDTO.getVideoInCallPermission());
                                                                subscriberDTO.setVideoCallInitiatePermission(groupMemberDTO.getVideoCallInitiatePermission());
                                                                subscriberDTO.setVideoCallReceivePermission(groupMemberDTO.getVideoCallReceivePermission());
                                                                subscriberDTO.setVideoInCallPermission(groupMemberDTO.getVideoInCallPermission());
                                                                subscriberDTO.setUa(groupMemberDTO.getUa());
                                                                subscriberDTO.setIsOSMAuthorize(groupMemberDTO.getIsOSMAuthorize());
                                                                KnCorpGroupMemberDTO memberDTO = new KnCorpGroupMemberDTO(groupMemberDTO.getMdn());
                                                                modifiedMembers.add(memberDTO);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    subscriberDTO.setGroupId(groupId);
                                                    subscriberDTO.setNetworkName(contact.getName());
                                                    knLogger.debug(methodName,"diff- ", contact.getUa());
                                                    subscriberDTO.setUa(contact.getUa());
                                                    // Check the group id of the newly added members
                                                    if (groupId == docChgDTO.getGroupId()) {
                                                        addedgrpMembers.add(subscriberDTO);
                                                    }
                                                }
                                                //set the newly added members to only existing members .
                                                if (delGrpMembeList == null || !delGrpMembeList.contains(mdn)) {
                                                    docChgDTO.setAddedGroupMembers(addedgrpMembers);
                                                } /*else {
                                                    docChgDTO.setAddedGroupMembers(addedgrpMembers);
                                                }*/
                                                //commenting above because if block is empty. As per PMD Report.

                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //setting added members for empty groups
                        if(isEmptyGroup){
                            Collection<KnSubscriberDTO> addedEmptyGrpMembers = new ArrayList<>();
                            for (Map.Entry<Integer, Collection<KnCorpContactDTO>> entry : addedGroupMembers.entrySet()) {
                                Integer groupId = entry.getKey();
                                Collection<KnCorpContactDTO> contactDTO = addedGroupMembers.get(groupId);
                                for (KnCorpContactDTO contact : contactDTO) {
                                    KnSubscriberDTO subscriberDTO = new KnSubscriberDTO();
                                    //subscriberDTO.setAliasMdn(contact.getAliasMdn());
                                    //subscriberDTO.setUserId(contact.getUserId());
                                    //subscriberDTO.setActiveFS2(contact.getSubsActiveFS2());
                                    //subscriberDTO.setGroupId(groupId);
                                    //subscriberDTO.setUa(contact.getUa());
                                    subscriberDTO.setMdn(contact.getMdn());
                                    subscriberDTO.setSupervisory(contact.getSupervisory());
                                    subscriberDTO.setLocWatcher(contact.getLocWatcher());
                                    subscriberDTO.setIsOSMAuthorize(contact.getIsOSMAuthorize());
                                    subscriberDTO.setClientType(contact.getClientType());
                                    subscriberDTO.setContact_type(contact.getContact_type());
                                    subscriberDTO.setCallPermission(contact.getCallInitiatePermission()+","+contact.getCallReceivePermission()+","+contact.getInCallPermission());
                                    subscriberDTO.setNetworkName(contact.getName());
                                    addedEmptyGrpMembers.add(subscriberDTO);
                                }
                            }
                            knLogger.debug(methodName," addedEmptyGrpMembers-",addedEmptyGrpMembers);
                            docChgDTO.setAddedEmptyGroupMembers(addedEmptyGrpMembers);
                        }
                    }
                    //remove the members from the list if any newly added  members are exist
                    if (modifiedGrpMembers != null && !modifiedGrpMembers.isEmpty()) {
                        modifiedGrpMembers.removeAll(modifiedMembers);
                    }
                    //get the supervisory
                    if (modifiedGrpMembers != null && !modifiedGrpMembers.isEmpty()) {
                        Collection<KnSubscriberDTO> modifiedMemberList = new ArrayList<KnSubscriberDTO>();
                        for (KnCorpGroupMemberDTO groupMemberDTO : modifiedGrpMembers) {
                            KnSubscriberDTO subscriberDTO = new KnSubscriberDTO();
                            subscriberDTO.setMdn(groupMemberDTO.getMdn());
                            subscriberDTO.setAliasMdn(groupMemberDTO.getAliasMdn());
                            String activefs2=groupMemberDTO.getSubsActiveFS2();
                            if (subsPV < PROTOCOL_VERSION_16 && activefs2 != null) {
                                //converting to activefs1
                                activefs2 = KnGeneralUtil.convertActiveFs2toHexActiveFs1(activefs2);
                            } else if (subsPV >= PROTOCOL_VERSION_16 && null != activefs2) {
                                activefs2 = KnGeneralUtil.calculateActiveFeatureSetBasedOnPv(activefs2, subsPV);
                            }
                            subscriberDTO.setActiveFS2(activefs2);
                            subscriberDTO.setUserId(groupMemberDTO.getUserId());
                            subscriberDTO.setSupervisory(groupMemberDTO.getSupervisory());
                            subscriberDTO.setLocWatcher(groupMemberDTO.getLocWatcher());
                            subscriberDTO.setNetworkName(groupMemberDTO.getName());
                            subscriberDTO.setClientType(groupMemberDTO.getClientType());
                            subscriberDTO.setContact_type(groupMemberDTO.getContact_type());
                            subscriberDTO.setGroupId(docChgDTO.getGroupId());
                            subscriberDTO.setCallPermission(groupMemberDTO.getCallInitiatePermission()+","+groupMemberDTO.getCallReceivePermission()+","+groupMemberDTO.getInCallPermission());
                            subscriberDTO.setUa(groupMemberDTO.getUa());
                            subscriberDTO.setIsOSMAuthorize(groupMemberDTO.getIsOSMAuthorize());
                            subscriberDTO.setVideoCallPermission(groupMemberDTO.getVideoCallInitiatePermission()+","+groupMemberDTO.getVideoCallReceivePermission()+","+groupMemberDTO.getVideoInCallPermission());
                            subscriberDTO.setVideoCallInitiatePermission(groupMemberDTO.getVideoCallInitiatePermission());
                            subscriberDTO.setVideoCallReceivePermission(groupMemberDTO.getVideoCallReceivePermission());
                            subscriberDTO.setVideoInCallPermission(groupMemberDTO.getVideoInCallPermission());
                            knLogger.debug(methodName, "[VIDEO-PERM] formXcapDiffNotification MODIFY member - mdn:", subscriberDTO.getMdn(), ", callPermission:", subscriberDTO.getCallPermission(), ", videoCallPermission:", subscriberDTO.getVideoCallPermission());
                            modifiedMemberList.add(subscriberDTO);
                        }
                        if ((delGrpMembeList != null && delGrpMembeList.contains(mdn)) || (addedContactList != null && addedContactList.contains(new KnCorpContactDTO(mdn)))) {
                              knLogger.debug(methodName);
                        } else {
                            docChgDTO.setModifiedGrpMembers(modifiedMemberList);
                        }
                    }
                    //Iterating through the status of the deleted member & if he gets deleted from the DB set deleted members
                    if (deletedGrpMembers != null && !deletedGrpMembers.isEmpty()) {
                        for (Map.Entry<Integer, LinkedList<String>> entry : deletedGrpMembers.entrySet()) {
                            Integer groupId = entry.getKey();
                            if (docChgDTO.getDocUri().contains(groupId + ".xml")) {
                                if(null != distInfo){
                                    Collection<String> grpMembers = distInfo.get(groupId);
                                    if (grpMembers != null && !grpMembers.isEmpty()) {
                                        if (grpMembers.contains(mdn)) {
                                            LinkedList<String> deletedGrpMembersList = deletedGrpMembers.get(groupId);
                                            if (delGroupMemStatus != null) {
                                                LinkedList<Integer> delStatus = delGroupMemStatus.get(groupId);
                                                List<String> actualDelMemList = new ArrayList<String>();
                                                if (delStatus != null) {
                                                    Iterator ite = deletedGrpMembersList.iterator();
                                                    for (Integer result : delStatus) {
                                                        String memberMdn = (String) ite.next();
                                                        if (!memberMdn.equals(mdn)) {
                                                            if (result > 0) {
                                                                actualDelMemList.add(memberMdn);
                                                            }
                                                        }
                                                    }
                                                    if (addedContactList != null && addedContactList.contains(new KnCorpContactDTO(mdn))) {
                                                    } else if (actualDelMemList != null && !actualDelMemList.isEmpty()) {
                                                        docChgDTO.setRemovedGroupMembers(actualDelMemList);
                                                    }
                                                    //commenting this else because this block is empty. As per PMD Report.
                                                    //Reverted back since it is breaking the group notification
                                                    // - removed members are not get set into the dto

                                              /*  if (addedContactList != null && addedContactList.contains(new KnCorpContactDTO(mdn))) {
                                                    if (actualDelMemList != null && !actualDelMemList.isEmpty()) {
                                                        docChgDTO.setRemovedGroupMembers(actualDelMemList);
                                                    }
                                                }*/
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }

                    knLogger.debug(methodName, "deletedGrpMembers, addedGroupMembers, currentGroupMemberList", deletedGrpMembers, addedGroupMembers, currentGroupMemberList);
                    if (null != deletedGrpMembers && !deletedGrpMembers.isEmpty() &&
                            (null == addedGroupMembers || addedGroupMembers.isEmpty()) &&
                            (null != currentGroupMemberList && currentGroupMemberList.isEmpty())) {
                        for (Map.Entry<Integer, LinkedList<String>> entry : deletedGrpMembers.entrySet()) {
                            Integer groupId = entry.getKey();
                            if (docChgDTO.getDocUri().contains(groupId + ".xml")) {
                                if (null != distInfo) {
                                    List<String> deletedGrpMembersList = deletedGrpMembers.get(groupId);
                                    if (null != delGroupMemStatus) {
                                        List<Integer> delStatus = delGroupMemStatus.get(groupId);
                                        List<String> actualDelMemList = new ArrayList<>();
                                        if (null != delStatus) {
                                            Iterator ite = deletedGrpMembersList.iterator();
                                            for (Integer result : delStatus) {
                                                String memberMdn = (String) ite.next();
                                                if (0 < result) {
                                                    actualDelMemList.add(memberMdn);
                                                }
                                            }
                                            if (null != actualDelMemList && !actualDelMemList.isEmpty()) {
                                                docChgDTO.setRemovedGroupMembers(actualDelMemList);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (docChgDTO.getDocUri().contains(APP_UID_CORP_RESOURCE_LIST)) {

                    if (finalMissingContacts != null && !finalMissingContacts.isEmpty()) {
                        Collection<KnSubscriberDTO> addedContactList = new ArrayList<KnSubscriberDTO>();
                        if (finalMissingContacts.get(mdn) != null) {
                            for (KnCorpSubscriberDTO subscDTO : finalMissingContacts.get(mdn)) {
                                if (!subscDTO.getMdn().equals(mdn)) {
                                    KnSubscriberDTO subsc = new KnSubscriberDTO();
                                    subsc.setMdn(subscDTO.getMdn());
                                    subsc.setAliasMdn(subscDTO.getAliasMdn());
                                    subsc.setUserId(subscDTO.getUserId());
                                    String activefs2=subscDTO.getSubsActiveFS2();
                                    if (subsPV < PROTOCOL_VERSION_16 && activefs2 != null) {
                                        activefs2 = KnGeneralUtil.convertActiveFs2toHexActiveFs1(activefs2);
                                    } else if (activefs2 != null) {
                                        activefs2 = KnGeneralUtil.calculateActiveFeatureSetBasedOnPv(activefs2, subsPV);
                                    }
                                    subsc.setActiveFS2(activefs2);
                                    subsc.setNetworkName(subscDTO.getName());
                                    subsc.setClientType(subscDTO.getClientType());
                                    subsc.setContact_type(subscDTO.getContact_type());
                                    subsc.setUa(subscDTO.getUa());
                                    knLogger.debug(methodName, "subscDTO in the form xcpa difff- ", subsc);
                                    addedContactList.add(subsc);
                                }
                            }
                            knLogger.debug(methodName, "addedContactList- ", addedContactList);
                            if(docChgDTO.getAddedContactList() != null && !docChgDTO.getAddedContactList().isEmpty()){
                                addedContactList.addAll(docChgDTO.getAddedContactList());
                            }
                            docChgDTO.setAddedContactList(addedContactList);
                        }
                    }
                    if (modifiedContactMap != null && !modifiedContactMap.isEmpty()) {
                        Collection<KnSubscriberDTO> modifiedContactList = new ArrayList<KnSubscriberDTO>();
                        if (modifiedContactMap.get(mdn) != null) {
                            for (KnCorpSubscriberDTO subscDTO : modifiedContactMap.get(mdn)) {
                                if (!subscDTO.getMdn().equals(mdn)) {
                                    KnSubscriberDTO subsc = new KnSubscriberDTO();
                                    subsc.setMdn(subscDTO.getMdn());
                                    subsc.setNetworkName(subscDTO.getName());
                                    subsc.setContact_type(subscDTO.getContact_type());
                                    modifiedContactList.add(subsc);
                                }
                            }
                        }
                        docChgDTO.setModifiedContactMembers(modifiedContactList);
                    }
                    if (deletedContacts != null && !deletedContacts.isEmpty()) {
                        int i = 0;
                        LinkedList<String> delMdnList = deletedContacts.get(mdn);
                        if (delContactStatus != null) {
                            LinkedList<Integer> delStatus = delContactStatus.get(mdn);
                            List<String> actualDelMemList = new ArrayList<String>();
                            if (delStatus != null) {
                                Iterator ite = delMdnList.iterator();
                                for (Integer result : delStatus) {
                                    String memberMdn = (String) ite.next();
                                    if (!memberMdn.equals(mdn)) {
                                        if (result > 0) {
                                            actualDelMemList.add(memberMdn);
                                        }
                                    }
                                    i++;
                                }
                                if (actualDelMemList != null && !actualDelMemList.isEmpty()) {
                                    docChgDTO.setRemovedContactList(actualDelMemList);
                                }
                            }
                        }
                    }
                }
            }
        }
        knLogger.debug(methodName, "etagMap EXIT");
        return etagMap;
    }

    public static Map<String, KnOPDirChgDTO> formXcapDiffNotificationMcx(Map<String, KnOPDirChgDTO> etagMap,
                                                                         Map<Integer, Collection<KnCorpContactDTO>> addedGroupMembers,
                                                                         LinkedHashMap<Integer, LinkedList<String>> deletedGrpMembers, String groupName,
                                                                         Collection<KnCorpGroupMemberDTO> modifiedGrpMembers, LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus,
                                                                         Integer avatar, Boolean sendDirectoryDiffNotify,boolean osmlistChanged,List<String> membersInDB) {
        String methodName = "formXcapDiffNotificationMcx()";
        knLogger.debug(methodName, "Entry :  - etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap),
                " - addedGroupMembers -", addedGroupMembers, " - deletedGrpMembers -", deletedGrpMembers, " - groupName -", groupName
                , " - modifiedGrpMembers -", modifiedGrpMembers, "delGroupMemStatus ", delGroupMemStatus," osmlistChanged - ",osmlistChanged," membersInDB - ",membersInDB);
        Collection<KnCorpGroupMemberDTO> modifiedGrpMembersList = new ArrayList<KnCorpGroupMemberDTO>();
        if (modifiedGrpMembers != null && !modifiedGrpMembers.isEmpty()) {
            modifiedGrpMembersList.addAll(modifiedGrpMembers);
        }
        //get the actually deleted members
        Map<String, Collection<String>> delGrpMemMap = getDeletedMembers(deletedGrpMembers, delGroupMemStatus);
        for (String mdn : etagMap.keySet()) {
            KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
            int subsPV = dirChgDTO.getProtoVersion()!=null?Integer.parseInt(dirChgDTO.getProtoVersion()):0;
            if(sendDirectoryDiffNotify !=null && sendDirectoryDiffNotify){
                dirChgDTO.setNotfnCapability(Boolean.FALSE);
            }
            Collection<KnOPDocChgDTO> docChgDTOS = dirChgDTO.getDocChgDTO();
            for (KnOPDocChgDTO docChgDTO : docChgDTOS) {
                if (docChgDTO.getDocUri().contains(APP_UID_CORP_GROUP)) {
                    Collection<String> delGrpMembeList = new ArrayList<String>();
                    if (!delGrpMemMap.isEmpty()) {
                        delGrpMembeList = delGrpMemMap.get(String.valueOf(docChgDTO.getGroupId()));
                    }
                    // setting groupname for the existing members
                    if (groupName != null) {
                        if ((delGrpMembeList != null && delGrpMembeList.contains(mdn))) {
                        } else {
                            docChgDTO.setGroupName(groupName);
                        }
                    }

                    if (avatar != null) {
                        if ((delGrpMembeList != null && delGrpMembeList.contains(mdn))) {
                        } else {
                            docChgDTO.setAvatar(avatar);
                        }
                    }

                    //tells if osm list changed if true will send group tag else member etag.
                    docChgDTO.setOsmListChanged(osmlistChanged);

                    Collection<KnCorpGroupMemberDTO> modifiedMembers = new ArrayList<KnCorpGroupMemberDTO>();
                    if (addedGroupMembers != null && !addedGroupMembers.isEmpty()) {
                        Collection<KnSubscriberDTO> addedgrpMembers = new ArrayList<KnSubscriberDTO>();
                        for (Map.Entry<Integer, Collection<KnCorpContactDTO>> entry : addedGroupMembers.entrySet()) {
                            Integer groupId = entry.getKey();
                            if (docChgDTO.getDocUri().contains(groupId + ".xml")) {
                                Collection<KnCorpContactDTO> contactDTO = addedGroupMembers.get(groupId);
                                if (contactDTO!=null && !membersInDB.contains(mdn)) {
                                    for (KnCorpContactDTO contact : contactDTO) {
                                        knLogger.debug(methodName, "diff- ", contact);
                                        KnSubscriberDTO subscriberDTO = new KnSubscriberDTO();
                                        subscriberDTO.setMdn(contact.getMdn());
                                        subscriberDTO.setAliasMdn(contact.getAliasMdn());
                                        subscriberDTO.setUserId(contact.getUserId());
                                        String activefs2=contact.getSubsActiveFS2();
                                        if (subsPV < PROTOCOL_VERSION_16 && activefs2 != null) {
                                            //converting to activefs1
                                            activefs2 = KnGeneralUtil.convertActiveFs2toHexActiveFs1(activefs2);
                                        } else if (subsPV >= PROTOCOL_VERSION_16 && null != activefs2) {
                                            activefs2 = KnGeneralUtil.calculateActiveFeatureSetBasedOnPv(activefs2, subsPV);
                                        }
                                        subscriberDTO.setActiveFS2(activefs2);
                                        subscriberDTO.setSupervisory(contact.getSupervisory());
                                        subscriberDTO.setLocWatcher(contact.getLocWatcher());
                                        subscriberDTO.setClientType(contact.getClientType());
                                        subscriberDTO.setContact_type(contact.getContact_type());
                                        subscriberDTO.setMemberCorpId(contact.getCorpId());
                                        subscriberDTO.setIsOSMAuthorize(contact.getIsOSMAuthorize());
                                        subscriberDTO.setCallPermission(contact.getCallInitiatePermission() + "," + contact.getCallReceivePermission() + "," + contact.getInCallPermission());
                                        subscriberDTO.setVideoCallPermission(contact.getVideoCallInitiatePermission()+","+contact.getVideoCallReceivePermission()+","+contact.getVideoInCallPermission());
                                        subscriberDTO.setVideoCallInitiatePermission(contact.getVideoCallInitiatePermission());
                                        subscriberDTO.setVideoCallReceivePermission(contact.getVideoCallReceivePermission());
                                        subscriberDTO.setVideoInCallPermission(contact.getVideoInCallPermission());
                                        subscriberDTO.setIsAffiliationEnabled(contact.getIsAffiliationEnabled());
                                        // get the supervisory for the newly added members from the modifiedGrpMemmbersList & set the supervisory
                                        if (modifiedGrpMembersList != null) {
                                            for (KnCorpGroupMemberDTO groupMemberDTO : modifiedGrpMembersList) {
                                                if (contact.getMdn().equals(groupMemberDTO.getMdn())) {
                                                    subscriberDTO.setSupervisory(groupMemberDTO.getSupervisory());
                                                    subscriberDTO.setLocWatcher(groupMemberDTO.getLocWatcher());
                                                    subscriberDTO.setCallPermission(groupMemberDTO.getCallInitiatePermission() + "," + groupMemberDTO.getCallReceivePermission() + "," + groupMemberDTO.getInCallPermission());
                                                    subscriberDTO.setVideoCallPermission(groupMemberDTO.getVideoCallInitiatePermission()+","+groupMemberDTO.getVideoCallReceivePermission()+","+groupMemberDTO.getVideoInCallPermission());
                                                    subscriberDTO.setVideoCallInitiatePermission(groupMemberDTO.getVideoCallInitiatePermission());
                                                    subscriberDTO.setVideoCallReceivePermission(groupMemberDTO.getVideoCallReceivePermission());
                                                    subscriberDTO.setVideoInCallPermission(groupMemberDTO.getVideoInCallPermission());
                                                    subscriberDTO.setUa(groupMemberDTO.getUa());
                                                    subscriberDTO.setIsOSMAuthorize(groupMemberDTO.getIsOSMAuthorize());
                                                    KnCorpGroupMemberDTO memberDTO = new KnCorpGroupMemberDTO(groupMemberDTO.getMdn());
                                                    modifiedMembers.add(memberDTO);
                                                    break;
                                                }
                                            }
                                        }
                                        subscriberDTO.setGroupId(groupId);
                                        subscriberDTO.setNetworkName(contact.getName());
                                        knLogger.debug(methodName, "diff- ", contact.getUa());
                                        subscriberDTO.setUa(contact.getUa());
                                        // Check the group id of the newly added members
                                        //removing self mdn from getting added to itself
                                        if (!contact.getMdn().equals(mdn)&&groupId == docChgDTO.getGroupId()) {
                                            knLogger.debug(methodName,"Entry6");
                                            addedgrpMembers.add(subscriberDTO);
                                        }
                                    }
                                    //set the newly added members to only existing members .
                                    if (delGrpMembeList == null || !delGrpMembeList.contains(mdn)) {
                                        docChgDTO.setAddedGroupMembers(addedgrpMembers);
                                    }
                                }


                            }
                        }
                    }
                    //remove the members from the list if any newly added  members are exist
                    if (modifiedGrpMembers != null && !modifiedGrpMembers.isEmpty()) {
                        modifiedGrpMembers.removeAll(modifiedMembers);
                    }
                    //get the supervisory
                    if (modifiedGrpMembers != null && !modifiedGrpMembers.isEmpty()) {
                        Collection<KnSubscriberDTO> modifiedMemberList = new ArrayList<KnSubscriberDTO>();
                        for (KnCorpGroupMemberDTO groupMemberDTO : modifiedGrpMembers) {
                            KnSubscriberDTO subscriberDTO = new KnSubscriberDTO();
                            subscriberDTO.setMdn(groupMemberDTO.getMdn());
                            subscriberDTO.setAliasMdn(groupMemberDTO.getAliasMdn());
                            String activefs2=groupMemberDTO.getSubsActiveFS2();
                            if (subsPV < PROTOCOL_VERSION_16 && activefs2 != null) {
                                //converting to activefs1
                                activefs2 = KnGeneralUtil.convertActiveFs2toHexActiveFs1(activefs2);
                            } else if (subsPV >= PROTOCOL_VERSION_16 && null != activefs2) {
                                activefs2 = KnGeneralUtil.calculateActiveFeatureSetBasedOnPv(activefs2, subsPV);
                                //activefs2 = KnGeneralUtil.convertActiveFs2toHexActiveFs1(String.valueOf(newActiveFS2));
                            }
                            subscriberDTO.setActiveFS2(activefs2);
                            subscriberDTO.setUserId(groupMemberDTO.getUserId());
                            subscriberDTO.setSupervisory(groupMemberDTO.getSupervisory());
                            subscriberDTO.setLocWatcher(groupMemberDTO.getLocWatcher());
                            subscriberDTO.setNetworkName(groupMemberDTO.getName());
                            subscriberDTO.setClientType(groupMemberDTO.getClientType());
                            subscriberDTO.setContact_type(groupMemberDTO.getContact_type());
                            subscriberDTO.setGroupId(docChgDTO.getGroupId());
                            subscriberDTO.setCallPermission(groupMemberDTO.getCallInitiatePermission() + "," + groupMemberDTO.getCallReceivePermission() + "," + groupMemberDTO.getInCallPermission());
                            subscriberDTO.setUa(groupMemberDTO.getUa());
                            subscriberDTO.setIsOSMAuthorize(groupMemberDTO.getIsOSMAuthorize());
                            subscriberDTO.setVideoCallPermission(groupMemberDTO.getVideoCallInitiatePermission()+","+groupMemberDTO.getVideoCallReceivePermission()+","+groupMemberDTO.getVideoInCallPermission());
                            subscriberDTO.setVideoCallInitiatePermission(groupMemberDTO.getVideoCallInitiatePermission());
                            subscriberDTO.setVideoCallReceivePermission(groupMemberDTO.getVideoCallReceivePermission());
                            subscriberDTO.setVideoInCallPermission(groupMemberDTO.getVideoInCallPermission());
                            knLogger.debug(methodName, "[VIDEO-PERM] formXcapDiffNotificationMcx MODIFY member - mdn:", subscriberDTO.getMdn(), ", callPermission:", subscriberDTO.getCallPermission(), ", videoCallPermission:", subscriberDTO.getVideoCallPermission());
                            modifiedMemberList.add(subscriberDTO);
                        }
                        if ((delGrpMembeList != null && delGrpMembeList.contains(mdn))) {
                            knLogger.debug(methodName);
                        } else {
                            docChgDTO.setModifiedGrpMembers(modifiedMemberList);
                        }
                    }
                    //Iterating through the status of the deleted member & if he gets deleted from the DB set deleted members
                    if (deletedGrpMembers != null && !deletedGrpMembers.isEmpty()) {
                        for (Map.Entry<Integer, LinkedList<String>> entry : deletedGrpMembers.entrySet()) {
                            Integer groupId = entry.getKey();
                            if (docChgDTO.getDocUri().contains(groupId + ".xml")) {
                                LinkedList<String> deletedGrpMembersList = deletedGrpMembers.get(groupId);
                                if (delGroupMemStatus != null) {
                                    LinkedList<Integer> delStatus = delGroupMemStatus.get(groupId);
                                    List<String> actualDelMemList = new ArrayList<String>();
                                    if (delStatus != null) {
                                        Iterator ite = deletedGrpMembersList.iterator();
                                        for (Integer result : delStatus) {
                                            String memberMdn = (String) ite.next();
                                            if (!memberMdn.equals(mdn)) {
                                                if (result > 0) {
                                                    actualDelMemList.add(memberMdn);
                                                }
                                            }
                                        }
                                        if (actualDelMemList != null && !actualDelMemList.isEmpty()) {
                                            docChgDTO.setRemovedGroupMembers(actualDelMemList);
                                        }
                                    }
                                }


                            }
                        }
                    }
                }
            }
        }

        knLogger.debug(methodName, "etagMap EXIT",KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }


    public static Map<String, KnOPDirChgDTO> formXcapDiffNotification(Map<String, KnOPDirChgDTO> etagMap,
                                                                      Map<String, Collection<KnCorpSubscriberDTO>> modifiedContactMap,
                                                                      Map<Integer, Collection<KnCorpGroupMemberDTO>> modifiedGrpMembers) {
        String methodName = "formXcapDiffNotification(etagMap,modifiedContactMap,modifiedGrpMembers)";
        knLogger.debug(methodName, "Entry :  - etagMap - ", etagMap == null ? etagMap : KnGDPRTemplate.mapKeyMdn(etagMap), " - modifiedContactMap -", modifiedContactMap, " - modifiedGrpMembers -", modifiedGrpMembers);

        for (String mdn : etagMap.keySet()) {
            KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
            if(dirChgDTO == null || dirChgDTO.getDocChgDTO() == null)
                continue;
            Collection<KnOPDocChgDTO> docChgDTOS = dirChgDTO.getDocChgDTO();
            for (KnOPDocChgDTO docChgDTO : docChgDTOS) {
                if (docChgDTO.getDocUri().contains(APP_UID_CORP_GROUP)) {
                    if (modifiedGrpMembers != null && !modifiedGrpMembers.isEmpty()) {
                        Collection<KnSubscriberDTO> modifiedMemberList = new ArrayList<KnSubscriberDTO>();
                        for (Map.Entry<Integer, Collection<KnCorpGroupMemberDTO>> entry : modifiedGrpMembers.entrySet()) {
                            Integer grpId = entry.getKey();
                            Collection<KnCorpGroupMemberDTO> groupMemberList = entry.getValue();
                            KnSubscriberDTO subscriberDTO = new KnSubscriberDTO();
                            for (KnCorpGroupMemberDTO groupMemberDTO : groupMemberList) {
                                subscriberDTO.setMdn(groupMemberDTO.getMdn());
                                subscriberDTO.setAliasMdn(groupMemberDTO.getAliasMdn());
                                subscriberDTO.setUserId(groupMemberDTO.getUserId());
                                subscriberDTO.setActiveFS2(groupMemberDTO.getSubsActiveFS2());
                                subscriberDTO.setSupervisory(groupMemberDTO.getSupervisory());
                                subscriberDTO.setLocWatcher(groupMemberDTO.getLocWatcher());
                                subscriberDTO.setNetworkName(groupMemberDTO.getName());
                                subscriberDTO.setClientType(groupMemberDTO.getClientType());
                                subscriberDTO.setContact_type(groupMemberDTO.getContact_type());
                                subscriberDTO.setGroupId(docChgDTO.getGroupId());
                                subscriberDTO.setUa(groupMemberDTO.getUa());
                                if (docChgDTO.getGroupId() == grpId) {
                                    modifiedMemberList.add(subscriberDTO);
                                }
                            }
                        }
                        docChgDTO.setModifiedGrpMembers(modifiedMemberList);
                    }
                } else if (docChgDTO.getDocUri().contains(APP_UID_CORP_RESOURCE_LIST)) {
                    if (modifiedContactMap != null && !modifiedContactMap.isEmpty()) {
                        Collection<KnSubscriberDTO> modifiedContactList = new ArrayList<KnSubscriberDTO>();
                        if (modifiedContactMap.get(mdn) != null) {
                            for (KnCorpSubscriberDTO subscDTO : modifiedContactMap.get(mdn)) {
                                if (!subscDTO.getMdn().equals(mdn)) {
                                    KnSubscriberDTO subsc = new KnSubscriberDTO();
                                    subsc.setMdn(subscDTO.getMdn());
                                    subsc.setAliasMdn(subscDTO.getAliasMdn());
                                    subsc.setUserId(subscDTO.getUserId());
                                    subsc.setActiveFS2(subscDTO.getSubsActiveFS2());
                                    subsc.setNetworkName(subscDTO.getName());
                                    subsc.setClientType(subscDTO.getClientType());
                                    subsc.setContact_type(subscDTO.getContact_type());
                                    subsc.setUa(subscDTO.getUa());
                                    modifiedContactList.add(subsc);
                                }
                            }
                        }
                        docChgDTO.setModifiedContactMembers(modifiedContactList);
                    }
                }
            }
        }
        return etagMap;
    }


    public static Map<String, KnOPDirChgDTO> formXcapDiffNotificationForActivefs(Map<String, KnOPDirChgDTO> etagMap, KnSubsProfileDTO mdnProfile) {
        String methodName = "formXcapDiffNotificationForActivefs(etagMap, String ,String)";
        knLogger.debug(methodName, "Entry :  - etagMap - ", etagMap == null ? etagMap : KnGDPRTemplate.mapKeyMdn(etagMap), " - mdnProfile -", mdnProfile);
        for (String mdn : etagMap.keySet()) {
            KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
            Collection<KnOPDocChgDTO> docChgDTOS = dirChgDTO.getDocChgDTO();
            for (KnOPDocChgDTO docChgDTO : docChgDTOS) {
                if (docChgDTO.getDocUri().contains(APP_UID_CORP_GROUP)) {
                    Collection<KnSubscriberDTO> modifiedMemberList = new ArrayList<KnSubscriberDTO>();
                    KnSubscriberDTO subscriberDTO = new KnSubscriberDTO();
                    subscriberDTO.setMdn(mdnProfile.getMdn());
                    subscriberDTO.setActiveFS2(mdnProfile.getActiveFS2());
                    subscriberDTO.setUa(KnGeneralUtil.getUA(mdnProfile.getUserAgent(), mdnProfile.getClientMajorVersion()));
                    subscriberDTO.setGroupId(docChgDTO.getGroupId());
                    subscriberDTO.setNetworkName(mdnProfile.getNetworkName());
                    modifiedMemberList.add(subscriberDTO);
                    docChgDTO.setModifiedGrpMembers(modifiedMemberList);
                } else if (docChgDTO.getDocUri().contains(APP_UID_CORP_RESOURCE_LIST)) {
                    Collection<KnSubscriberDTO> modifiedContactList = new ArrayList<KnSubscriberDTO>();
                    KnSubscriberDTO subsc = new KnSubscriberDTO();
                    subsc.setMdn(mdnProfile.getMdn());
                    subsc.setActiveFS2(mdnProfile.getActiveFS2());
                    subsc.setUa(KnGeneralUtil.getUA(mdnProfile.getUserAgent(), mdnProfile.getClientMajorVersion()));
                    subsc.setNetworkName(mdnProfile.getNetworkName());
                    modifiedContactList.add(subsc);
                    docChgDTO.setModifiedContactMembers(modifiedContactList);
                }
            }
        }
        return etagMap;
    }



    /**
     * This method sets the deleted contact list and deleted group member list in in respective DTO for xcap  diff notification
     *
     * @param etagMap
     * @param removeContactMap
     * @param deletedGrpMemMap
     * @return
     */
    public static Map<String, KnOPDirChgDTO> formXcapDiffNotiRemovedMembers(Map<String, KnOPDirChgDTO> etagMap, Map<String,
            List<String>> removeContactMap, Map<Integer, List<String>> deletedGrpMemMap) {
        String methodName = "formXcapDiffNotiRemovedMembers((Map<String, KnOPDirChgDTO>, Map<String, List<String>>, Map<Integer, List<String>>)";
        knLogger.debug(methodName, "Entry :  - etagMap - ", etagMap== null ? etagMap : KnGDPRTemplate.mapKeyMdn(etagMap), " removeContactMap -", removeContactMap, " - deletedGrpMemMap -", deletedGrpMemMap);
        for (String mdn : etagMap.keySet()) {
            KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
            Collection<KnOPDocChgDTO> docChgDTOS = dirChgDTO.getDocChgDTO();
            for (KnOPDocChgDTO docChgDTO : docChgDTOS) {
                if (docChgDTO.getDocUri().contains(APP_UID_CORP_GROUP)) {
                    int groupId = docChgDTO.getGroupId();
                    List<String> removeMemList = new ArrayList<String>(deletedGrpMemMap.get(groupId));
                    docChgDTO.setRemovedGroupMembers(removeMemList);

                } else if (docChgDTO.getDocUri().contains(APP_UID_CORP_RESOURCE_LIST)) {
                    docChgDTO.setRemovedContactList(removeContactMap.get(mdn));
                }
            }
        }
        return etagMap;
    }

    public static Collection<KnCorpFailedData> getFailedDetails(long reqMasterListEtag) {
        Collection<KnCorpFailedData> failedDataList = new ArrayList<KnCorpFailedData>();
        KnCorpFailedData corpFailedData = new KnCorpFailedData();
        corpFailedData.setAttribute(MASTERLIST);
        Collection<String> values = new ArrayList<String>();
        String masterListEtag = String.valueOf(reqMasterListEtag);
        values.add(masterListEtag);
        corpFailedData.setValues(values);
        corpFailedData.setMsg(MASTERLISTETAG);
        failedDataList.add(corpFailedData);
        return failedDataList;
    }

    public boolean getFeatureBitValue(long clientFeatureSet, int bitNumber) {
        String methodName = "getFeatureBit(long,int )";
        knLogger.debug(methodName, "ENTRY: get Feature Bit data ");
        BitSet bitSet = convertLongToBitSet(clientFeatureSet);
        boolean bitValue = bitSet.get(bitNumber);
        knLogger.debug(methodName, "EXIT: Feature Bit Value - ", bitValue);
        return bitValue;
    }

    private BitSet convertLongToBitSet(long longValue) {
        String methodName = "convertLongToBitSet(long)";
        knLogger.debug(methodName, "ENTRY: Received long value to convert bit set is - ", longValue);

        long value = longValue;

        BitSet bitSet = new BitSet(Long.SIZE);
        int index = 0;
        while (value != 0) {
            if (value % 2L != 0) {
                bitSet.set(index);
            }
            ++index;
            value = value >>> 1;
        }
        knLogger.debug(methodName, "EXIT: Generated BitSet - ", bitSet);

        return bitSet;
    }

    public Map<Integer, List<String>> prepareGrpMemPam(Map<Integer, Collection<String>> grpMemMap) {
        Map<Integer, List<String>> groupMemberMap = new HashMap<Integer, List<String>>(grpMemMap.size());
        for (int id : grpMemMap.keySet()) {
            groupMemberMap.put(id, (List) grpMemMap.get(id));
        }
        return groupMemberMap;
    }

    /**
     * This method is to populate the the KnDialPlanConfigDTO object into  KnDialPlanInfoDTO and return a list.
     *
     * @param dialPlanInfo
     * @return
     */
    public List<KnDialPlanInfoDTO> getDialPlanDto(KnDialPlanConfigDTO dialPlanInfo) {
        List<KnDialPlanInfoDTO> dialPlanLst = new ArrayList<KnDialPlanInfoDTO>(1);
        KnDialPlanInfoDTO dialPlanInfoDTO = new KnDialPlanInfoDTO();
        dialPlanInfoDTO.setPttServerId(dialPlanInfo.getPttServerId());
        dialPlanInfoDTO.setCountryCode(dialPlanInfo.getCountryCode());
        dialPlanInfoDTO.setInternationalDialPrefix(dialPlanInfo.getIntlPrefix());
        dialPlanInfoDTO.setNationalDialPrefix(dialPlanInfo.getNatlPrefix());
        dialPlanInfoDTO.setNetworkNumberingPlan(dialPlanInfo.getNumPlanType());
        dialPlanLst.add(dialPlanInfoDTO);
        return dialPlanLst;
    }

    /**
     * Return the CorpAdminFs1Bitmask from licence info table
     *
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public String getCorpAdminFS1BitMask(KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getCorpAdminFS1BitMask(KnPersisterTxn persisterTxn)";
        knLogger.info(methodName, "ENTRY : ");
        try {
            KnLicenseInfoDAO xdmDAO = new KnLicenseInfoDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            return xdmDAO.getCorpAdminFS2BitMask(persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "KnBOException occured while getCorpAdminFS1BitMask", e);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage(), e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getCorpAdminFS1BitMask", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public static List<String> convertSubscDTOToStrList(Collection<KnCorpSubscriberDTO> subscribersList) {
        ArrayList<String> mdnList = new ArrayList<String>();
        if (subscribersList != null) {
            for (KnCorpSubscriberDTO subsc : subscribersList) {
                mdnList.add(subsc.getMdn());
            }
        }
        return mdnList;
    }

    /**
     * his method returns the external profile details for a profile ID.
     *
     * @param pttServerId
     * @param overrodeCache
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<Integer, KnExtProfileDetails> getExtProfileDetails(String pttServerId, boolean
            overrodeCache, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getExtProfileDetails(int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        try {
            return corpProfileUtil.getExtProfileDetails(overrodeCache, pttServerId, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "KnBOException occured while retrieving max external contacts allowed - ", e);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns the map of Ext subscr profileId and its details.
     *
     * @param idList
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<Integer, KnExtProfileDetails> getExtProfileDetails(List<Integer> idList, String
            pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getExtProfileDetails(List<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        Map<Integer, KnExtProfileDetails> validProfileMap = new HashMap<>();
        Map<Integer, KnExtProfileDetails> allProfileMap = getExtProfileDetails(pttServerId, false, persisterTxn);
        for (int id : idList) {
            validProfileMap.put(id, allProfileMap.get(id));
        }
        knLogger.debug(methodName, "EXIT : ", validProfileMap);
        return validProfileMap;
    }


    public Map<Integer, KnExtProfileDetails> getExtProfileDetails(Collection<String> mdnList, String pttServerId, int corpId,Set<Integer> sharedCorpIds,
                                                                  KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getExtProfileDetails(Collection<String>, int, Set<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            Collection<KnCorpSubscriberDTO> pocSubscrList = corpXdmDao.getSubscribersInfo(mdnList, persisterTxn);
            knLogger.debug(methodName, "pocSubscrList: ", pocSubscrList.size());
            List<String> nniSubscrList = new ArrayList<>(mdnList);
            boolean isExtContExist = false;
            for (KnCorpSubscriberDTO corpSubscriberDTO : pocSubscrList) {
                String mdn = corpSubscriberDTO.getMdn();
                nniSubscrList.remove(mdn);
                if (corpSubscriberDTO.getCorpId() != corpId && (sharedCorpIds!=null && !sharedCorpIds.contains(corpSubscriberDTO.getCorpId()))) {
                    isExtContExist = true;
                }
            }
            List<Integer> profileIdList = corpXdmDao.getExtSubsrProfilelist(nniSubscrList, persisterTxn);
            if (isExtContExist) {
                profileIdList.add(KnConstants.PROFILE_ID_KODIAK_EXTERNAL_CONTACT);
            }
            return getExtProfileDetails(profileIdList, pttServerId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnBOException occured while retrieving max external contacts allowed - ", e);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage(), e);
        }
    }


    public Map<String, Map<Integer, KnExtProfileDetails>> getExtProfileMaps(List<String> mdnList, String pttServerId, int
            corpId, KnPersisterTxn persisterTxn, KnCorpBCGrpPersistDTO bcGrpPersistDTO, List<Integer> sharedList) throws KnCorpBOException {
        String methodName = "getExtProfileMaps(List<String>, List<String>, String, int, KnPersisterTxn, KnCorpBCGrpPersistDTO)";
        knLogger.debug(methodName, "ENTRY: ");
        Map<String, Map<Integer, KnExtProfileDetails>> extProileMaps = new HashMap<>();
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            List<String> allMdnList = new ArrayList<>(mdnList);
            List<String> broadcasteList = bcGrpPersistDTO.getGrpBroadcasters();
            allMdnList.addAll(broadcasteList);
            Collection<KnCorpSubscriberDTO> pocSubscrList = corpXdmDao.getSubscribersInfo(allMdnList, persisterTxn);
            knLogger.debug(methodName, "pocSubscrList: ", pocSubscrList.size());
            List<String> nniSubscrList = new ArrayList<>(mdnList);
            List<String> nniBroadcasterList = new ArrayList<>(broadcasteList);
            boolean isExtContExist = false;
            boolean isExtContExistBroadcaster = false;
            for (KnCorpSubscriberDTO corpSubscriberDTO : pocSubscrList) {
                String mdn = corpSubscriberDTO.getMdn();
                nniSubscrList.remove(mdn);
                nniBroadcasterList.remove(mdn);
                if ( !(sharedList!=null&&sharedList.contains(corpSubscriberDTO.getCorpId()))
                && corpSubscriberDTO.getCorpId() != corpId) {
                    if (mdnList.contains(mdn)) {
                        isExtContExist = true;
                    }
                    if (broadcasteList.contains(mdn)) {
                        isExtContExistBroadcaster = true;
                    }
                }
            }
            knLogger.debug(methodName, "nniSubscrList : ", KnGDPRTemplate.mdnList(nniSubscrList));
            knLogger.debug(methodName, "nniBroadcasterList : ", KnGDPRTemplate.mdnList(nniBroadcasterList));
            List<Integer> profileIdList = corpXdmDao.getExtSubsrProfilelist(nniSubscrList, persisterTxn);
            if (isExtContExist) {
                profileIdList.add(KnConstants.PROFILE_ID_KODIAK_EXTERNAL_CONTACT);
            }

            List<Integer> profileIdBrdstrList = corpXdmDao.getExtSubsrProfilelist(nniBroadcasterList, persisterTxn);
            if (isExtContExistBroadcaster) {
                profileIdBrdstrList.add(KnConstants.PROFILE_ID_KODIAK_EXTERNAL_CONTACT);
            }
            knLogger.debug(methodName, "profileIdList : ", profileIdList);
            knLogger.debug(methodName, "profileIdBrdstrList : ", profileIdBrdstrList);
            extProileMaps.put(KnConstants.CONFIGUREDPROFILEMAP, getExtProfileDetails(profileIdList, pttServerId, persisterTxn));
            extProileMaps.put(KnConstants.EXTBROADCASTERFEATUREMAP, getExtProfileDetails(profileIdBrdstrList, pttServerId, persisterTxn));
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnBOException occured while retrieving max external contacts allowed - ", e);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage(), e);
        }
        return extProileMaps;
    }

    public Map<Integer, KnExtProfileDetails> getExtProfileDetails(Collection<KnCorpSubscriberDTO> mdnList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getExtProfileDetails(int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            List<String> nniMdnList = new ArrayList<>();
            boolean isExtContExist = false;
            for (KnCorpSubscriberDTO subscriberDTO : mdnList) {
                if (subscriberDTO.isExternalContact()) {
                    if (subscriberDTO.getContact_type() == KnConstants.CONTACT_TYPE_EXTERNAL_SUBSCRIBER) {
                        nniMdnList.add(subscriberDTO.getMdn());
                    } else {
                        isExtContExist = true;
                    }
                }
            }
            List<Integer> profileIdList = corpXdmDao.getExtSubsrProfilelist(nniMdnList, persisterTxn);
            if (isExtContExist) {
                profileIdList.add(KnConstants.PROFILE_ID_KODIAK_EXTERNAL_CONTACT);
            }
            knLogger.debug(methodName, "profileIdList - ", profileIdList);
            return getExtProfileDetails(profileIdList, pttServerId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnBOException occured while retrieving max external contacts allowed - ", e);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage(), e);
        }
    }

    public boolean isExtContSupervisor(List<String> grpSupervisorList, KnMdnDetailsPersistDTO pocMdnPersistDto) {
        String methodName = "isExtContSupervisor(List<String>, KnMdnDetailsPersistDTO)";
        knLogger.debug(methodName, "ENTRY: ");
        boolean isExist = false;
        Collection<KnCorpSubscriberDTO> extContList = pocMdnPersistDto.getExternalMdnList();
        for (KnCorpSubscriberDTO subscriberDTO : extContList) {
            String mdn = subscriberDTO.getMdn();
            // Here made it reverse because, some pr checkin. Client is expecting opposit value. This is temp fix. Later we have to correct it.
            if (subscriberDTO.getContact_type() == KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_CONTACT && grpSupervisorList.contains(mdn)) {
                isExist = true;
            }

        }
        return isExist;
    }


    /**
     * This method is used to populate the LI realted data from the etagMap for all the API
     *
     * @param eTags
     * @param pttServerId
     * @param groupNameMap
     * @param groupMembersMap
     * @param initialGroupmembers
     * @return
     */
    public static LinkedList<KnLIEventDTO> populateLIData(Map<String, KnOPDirChgDTO> eTags, String pttServerId,
                                                          Map<Integer, String> groupNameMap, Map<Integer,
            Collection<String>> groupMembersMap, Map<Integer, Collection<String>> initialGroupmembers
            ,KnPersisterTxn persisterTxn) {
        final String methodName="populateLIData()";
        knLogger.entry(methodName, " Entry:- populateLIData");
        knLogger.debug(methodName, "etagMap - ", eTags, "groupNameMap - ", groupNameMap, "groupMemberMap", groupMembersMap,
                "initialGroupmembers", initialGroupmembers);
        List<String> targetMdnList = new ArrayList<>();
        Map<String, KnOPDirChgDTO> baseMdnETags=new HashMap<>();
        Map<String, String> mdnMcpttMap = new HashMap<>();
        Map<String, List<String>> profileMdnOfBaseMap =null;
        try {
            List<String> realMdns = groupInfoUtil.getRealMdns(new ArrayList<>(eTags.keySet()), pttServerId, persisterTxn);
            targetMdnList = KnLIEventHandler.getTagetMdnList(realMdns);
            profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(realMdns,
                    pttServerId, persisterTxn);
            //profile with no base mdn in group
            Map<String, KnOPDirChgDTO> profileWithNoBaseInGroup = profileWithNoBaseInGroup(targetMdnList,
                    groupMembersMap, convertEtagTOBaseMdn(eTags, profileMdnOfBaseMap));
            if(!profileWithNoBaseInGroup.isEmpty()) {
                baseMdnETags.putAll(profileWithNoBaseInGroup);
            }
            //base mdn with profile mdn in group
            Map<String, KnOPDirChgDTO> etagWithBaseMdn = removeProfileMdnEtags(targetMdnList,
                    profileMdnOfBaseMap, eTags);
            if(!etagWithBaseMdn.isEmpty()){
                baseMdnETags.putAll(etagWithBaseMdn);
            }
            mdnMcpttMap = KnCorpSubsProvInfoUtil.getMdnMcpttIdMap(targetMdnList, persisterTxn,pttServerId);
        } catch (KnDAOException | KnCorpBOException e) {
            knLogger.error(methodName," getMdn details error",e);
        }
        LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();
        for (String mdn : targetMdnList) {
            KnOPDirChgDTO directory = baseMdnETags.get(mdn);
            if (directory != null) {
                Collection<KnOPDocChgDTO> docList = directory.getDocChgDTO();
                Iterator ite = docList.iterator();
                while (ite.hasNext()) {
                    KnOPDocChgDTO doc = (KnOPDocChgDTO) ite.next();
                    short contact = 1;
                    KnLIEventDTO liEventDTO = new KnLIEventDTO();
                    liEventDTO.setMdn(mdn);
                    liEventDTO.setMcpttId(mdnMcpttMap.get(mdn));
                    if (doc.getDocUri().contains("resource")) {
                        Collection<String> addedMembers = new ArrayList<String>();
                        if (doc.getAddedContactList() != null) {
                            for (KnSubscriberDTO subsc : doc.getAddedContactList()) {
                                addedMembers.add(subsc.getMdn());
                            }
                        }
                        liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                        liEventDTO.setDeletedMembers(getBaseMdnList(doc.getRemovedContactList(), profileMdnOfBaseMap));
                        liEventDTO.setAction(KnLIConstants.MODIFY_CONTACTS);
                    } else if (doc.getDocUri().contains("kn-corp-groups")) {
                        int couter = doc.getDocUri().lastIndexOf(".xml");
                        String grpId;
                        if (couter > 0) {
                            grpId = doc.getDocUri().substring(0, couter);
                        } else {
                            couter = doc.getEntryUri().lastIndexOf(".xml");
                            grpId = doc.getEntryUri().substring(0, couter);
                        }
                        int couter1 = grpId.lastIndexOf("/");
                        grpId = grpId.substring(couter1 + 1).trim();
                        String groupURI = "tel:+".concat(mdn).concat(";kn-corp-group=");
                        if (groupNameMap != null && groupNameMap.get(Integer.valueOf(grpId)) != null) {
                            groupURI = groupURI.concat(groupNameMap.get(Integer.valueOf(grpId)));
                        }
                        liEventDTO.setGroupURI(groupURI);
                        Collection<String> addedMembers = new ArrayList<String>();
                        if (doc.getAddedGroupMembers() != null) {
                            for (KnSubscriberDTO subsc : doc.getAddedGroupMembers()) {
                                addedMembers.add(subsc.getMdn());
                            }
                        }
                        liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                        contact = 2;
                        liEventDTO.setDeletedMembers(getBaseMdnList(doc.getRemovedGroupMembers(), profileMdnOfBaseMap));
                        if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.ADD.value()) {
                            liEventDTO.setAction(KnLIConstants.CREATE_GROUP_ACTION);
                            if (groupMembersMap.get(Integer.valueOf(grpId)) != null && groupMembersMap.get(Integer.valueOf(grpId)).size() > 0) {
                                addedMembers = new ArrayList<String>();
                                for (String memberMDN : groupMembersMap.get(Integer.valueOf(grpId))) {
                                    if (!memberMDN.equalsIgnoreCase(mdn)) {
                                        addedMembers.add(memberMDN);
                                    }
                                }
                                liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                            }
                        } else if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value()) {
                            liEventDTO.setAction(KnLIConstants.DELETE_GROUP_ACTION);
                        } else if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                            if ((addedMembers == null || addedMembers.size() == 0) && (liEventDTO.getDeletedMembers() == null || liEventDTO.getDeletedMembers().size() == 0)
                                    && (initialGroupmembers != null && initialGroupmembers.get(Integer.valueOf(grpId)) != null && !initialGroupmembers.get(Integer.valueOf(grpId)).contains(mdn))) {
                                liEventDTO.setAction(KnLIConstants.CREATE_GROUP_ACTION);
                                if (groupMembersMap != null && groupMembersMap.get(Integer.valueOf(grpId)) != null && groupMembersMap.get(Integer.valueOf(grpId)).size() > 0) {
                                    addedMembers = new ArrayList<String>();
                                    for (String memberMDN : groupMembersMap.get(Integer.valueOf(grpId))) {
                                        if (!memberMDN.equalsIgnoreCase(mdn)) {
                                            addedMembers.add(memberMDN);
                                        }
                                    }
                                    liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                                }
                            } else {
                                liEventDTO.setAction(KnLIConstants.MODIFY_GROUP_ACTION);
                            }
                        }
                    }
                    //in case of profile mdn already assigned,we need to remove the target mdn getting added to added members
                    if (liEventDTO.getAddedMembers() != null && !liEventDTO.getAddedMembers().isEmpty()) {
                        liEventDTO.getAddedMembers().remove(mdn);
                    }
                    liEventDTO.setDocumentType(contact);
                    liEventDTO.setPttServerId(pttServerId);
                    liEventDTO.setErrorCode(KnLIConstants.SUCCESS_CODE);
                    liEventList.add(liEventDTO);
                }
            }
        }
        knLogger.debug(methodName,"Exit liEventList :",liEventList);
        knLogger.exit(methodName, " Entry:- populateLIData");
        return liEventList;
    }

    public static LinkedList<KnLIEventDTO> upmPopulateLIData(Map<String, KnOPDirChgDTO> eTags, String pttServerId,
                                                             Map<Integer, String> groupNameMap, Map<Integer,
            Collection<String>> groupMembersMap, Map<Integer, Collection<String>> initialGroupmembers,Map<Integer, Collection<KnCorpContactDTO>> addedGroupMember
            ,LinkedHashMap<Integer, LinkedList<String>> deletedGroupMember,KnPersisterTxn persisterTxn) {
        final String methodName="upmPopulateLIData()";
        knLogger.debug(methodName, "etagMap - ", eTags, "groupNameMap - ", groupNameMap, "groupMemberMap", groupMembersMap,
                "initialGroupmembers", initialGroupmembers,"deletedGroupMember - ",deletedGroupMember,"addedGroupMember - ",addedGroupMember);

        List<String> targetMdnList =new ArrayList<>();
        Map<String, List<String>> profileMdnOfBaseMap=new HashMap<>();
        Map<String, KnOPDirChgDTO> baseMdnETags=new HashMap<>();
        Map<String, String> mdnMcpttMap = new HashMap<>();
        Set<String> profileMdns=new HashSet<>();
        try {
            List<String> realMdns = groupInfoUtil.getRealMdns(new ArrayList<String>(eTags.keySet()), pttServerId, null);
            profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(realMdns, pttServerId, null);
            targetMdnList = KnLIEventHandler.getTagetMdnList(realMdns);
            if(addedGroupMember!=null){
                for(Collection<KnCorpContactDTO> addMem:addedGroupMember.values()){
                    for(KnCorpContactDTO mdnMember:addMem){
                        profileMdns.add(mdnMember.getMdn());
                    }
                }
            }
            if(deletedGroupMember!=null){
                for(LinkedList<String> delMem:deletedGroupMember.values()){
                    profileMdns.addAll(delMem);
                }
            }
            if(!profileMdns.isEmpty()){
                removeBaseMdnOfProfileMdnFromEtag(new ArrayList<>(profileMdns),eTags,profileMdnOfBaseMap);
            }
            baseMdnETags=convertEtagTOBaseMdn(eTags,profileMdnOfBaseMap);
            mdnMcpttMap = KnCorpSubsProvInfoUtil.getMdnMcpttIdMap(targetMdnList, null, pttServerId);
        } catch (KnDAOException | KnCorpBOException e) {
            knLogger.error(methodName," getRealMdns details error",e);
        }

        LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();
        for (String mdn : targetMdnList) {
            KnOPDirChgDTO directory = baseMdnETags.get(mdn);
            if(directory!=null) {
                Collection<KnOPDocChgDTO> docList = directory.getDocChgDTO();
                Iterator ite = docList.iterator();
                while (ite.hasNext()) {
                    KnOPDocChgDTO doc = (KnOPDocChgDTO) ite.next();
                    short contact = 1;
                    KnLIEventDTO liEventDTO = new KnLIEventDTO();
                    liEventDTO.setMdn(mdn);
                    liEventDTO.setMcpttId(mdnMcpttMap.get(mdn));
                    if (doc.getDocUri().contains("resource")) {
                        Collection<String> addedMembers = new ArrayList<String>();
                        if (doc.getAddedContactList() != null) {
                            for (KnSubscriberDTO subsc : doc.getAddedContactList()) {
                                addedMembers.add(subsc.getMdn());
                            }
                        }
                        liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                        liEventDTO.setDeletedMembers(getBaseMdnList(doc.getRemovedContactList(), profileMdnOfBaseMap));
                        liEventDTO.setAction(KnLIConstants.MODIFY_CONTACTS);
                    } else if (doc.getDocUri().contains("kn-corp-groups")) {
                        int couter = doc.getDocUri().lastIndexOf(".xml");
                        String grpId;
                        if (couter > 0) {
                            grpId = doc.getDocUri().substring(0, couter);
                        } else {
                            couter = doc.getEntryUri().lastIndexOf(".xml");
                            grpId = doc.getEntryUri().substring(0, couter);
                        }
                        int couter1 = grpId.lastIndexOf("/");
                        grpId = grpId.substring(couter1 + 1).trim();
                        String groupURI = "tel:+".concat(mdn).concat(";kn-corp-group=");
                        if (groupNameMap != null && groupNameMap.get(Integer.valueOf(grpId)) != null) {
                            groupURI = groupURI.concat(groupNameMap.get(Integer.valueOf(grpId)));
                        }
                        liEventDTO.setGroupURI(groupURI);
                        Collection<String> addedMembers = new ArrayList<>();
                        knLogger.debug(methodName, "doc.getAddedGroupMembers(): ", doc.getAddedGroupMembers());
                        knLogger.debug(methodName, "doc.getRemovedGroupMembers(): ", doc.getRemovedGroupMembers());
                        if (doc.getAddedGroupMembers() != null) {
                            for (KnSubscriberDTO subsc : doc.getAddedGroupMembers()) {
                                addedMembers.add(subsc.getMdn());
                            }
                        }
                        boolean profileBaseExists = false;
                        //Target base & profile exist in the group
                        if (doc.getAddedGroupMembers() == null && addedMembers.isEmpty()
                                && doc.getRemovedGroupMembers() == null
                                && doc.getDocumentChgType() == DOC_CHANGE_TYPE.REPLACE.value()) {
                            for (String memberMDN : groupMembersMap.get(Integer.valueOf(grpId))) {
                                if (!memberMDN.equalsIgnoreCase(mdn)) {
                                    addedMembers.add(memberMDN);
                                }
                            }
                            profileBaseExists = true;
                            liEventDTO.setAction(KnLIConstants.CREATE_GROUP_ACTION);
                        }
                        liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                        contact = 2;
                        liEventDTO.setDeletedMembers(getBaseMdnList(doc.getRemovedGroupMembers(), profileMdnOfBaseMap));
                        knLogger.debug(methodName, "liEventDTO: ", liEventDTO, "doc.getDocumentChgType() : ", doc.getDocumentChgType(), "doc.getRemovedGroupMembers() :", doc.getRemovedGroupMembers());
                        if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.ADD.value()) {
                            liEventDTO.setAction(KnLIConstants.CREATE_GROUP_ACTION);
                            if (groupMembersMap.get(Integer.valueOf(grpId)) != null && groupMembersMap.get(Integer.valueOf(grpId)).size() > 0) {
                                addedMembers = new ArrayList<>();
                                for (String memberMDN : groupMembersMap.get(Integer.valueOf(grpId))) {
                                    if (!memberMDN.equalsIgnoreCase(mdn)) {
                                        addedMembers.add(memberMDN);
                                    }
                                }
                                liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                            }
                        } else if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value()) {
                            liEventDTO.setAction(KnLIConstants.DELETE_GROUP_ACTION);
                        } else if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                            if ((addedMembers == null || addedMembers.size() == 0) && (liEventDTO.getDeletedMembers() == null || liEventDTO.getDeletedMembers().size() == 0)
                                    && (initialGroupmembers != null && initialGroupmembers.get(Integer.valueOf(grpId)) != null && !initialGroupmembers.get(Integer.valueOf(grpId)).contains(mdn))) {
                                liEventDTO.setAction(KnLIConstants.CREATE_GROUP_ACTION);
                                if (groupMembersMap != null && groupMembersMap.get(Integer.valueOf(grpId)) != null && groupMembersMap.get(Integer.valueOf(grpId)).size() > 0) {
                                    addedMembers = new ArrayList<String>();
                                    for (String memberMDN : groupMembersMap.get(Integer.valueOf(grpId))) {
                                        if (!memberMDN.equalsIgnoreCase(mdn)) {
                                            addedMembers.add(memberMDN);
                                        }
                                    }
                                    liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                                }
                            } else {
                                if (!profileBaseExists)
                                    liEventDTO.setAction(KnLIConstants.MODIFY_GROUP_ACTION);
                            }
                        }
                    }
                    //in case of profile mdn already assigned,we need to remove the target mdn getting added to added members
                    if (null != liEventDTO.getAddedMembers() && !liEventDTO.getAddedMembers().isEmpty()) {
                        liEventDTO.getAddedMembers().remove(mdn);
                    }
                    liEventDTO.setDocumentType(contact);
                    liEventDTO.setPttServerId(pttServerId);
                    liEventDTO.setErrorCode(KnLIConstants.SUCCESS_CODE);
                    knLogger.debug(methodName, "before liEventDTO :", liEventDTO);
                    liEventList.add(liEventDTO);
                }
            }
        }
        knLogger.debug(methodName,"Exit liEventList :",liEventList);
        return liEventList;
    }

    public static LinkedList<KnLIEventDTO> upmPopulateMCXLIData(Map<String, KnOPDirChgDTO> eTags, String pttServerId,
                                                                Map<Integer, String> groupNameMap, Map<Integer,
            Collection<String>> groupMembersMap, Map<Integer, Collection<String>> initialGroupmembers,
                                                                Map<Integer, Collection<KnCorpContactDTO>> addedGroupMember
            , LinkedHashMap<Integer, LinkedList<String>> deletedGroupMember, KnPersisterTxn persisterTxn) {
        final String methodName = "upmPopulateMCXLIData()";
        knLogger.debug(methodName, "etagMap - ", eTags, "groupNameMap - ", groupNameMap, "groupMemberMap", groupMembersMap,
                "initialGroupmembers", initialGroupmembers, "deletedGroupMember - ", deletedGroupMember, "addedGroupMember - ", addedGroupMember);

        List<String> targetMdnList = new ArrayList<>();
        Map<String, List<String>> profileMdnOfBaseMap = new HashMap<>();
        Map<String, KnOPDirChgDTO> baseMdnETags = new HashMap<>();
        Map<String, String> mdnMcpttMap = new HashMap<>();
        Set<String> profileMdns = new HashSet<>();
        try {
            List<String> realMdns = groupInfoUtil.getRealMdns(new ArrayList<String>(eTags.keySet()), pttServerId, persisterTxn);
            knLogger.debug(methodName, " realMdns :", KnGDPRTemplate.mdnList(realMdns));
            profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(realMdns, pttServerId, persisterTxn);
            knLogger.debug(methodName, " profileMdnOfBaseMap :", profileMdnOfBaseMap);
            targetMdnList = KnLIEventHandler.getTagetMdnList(realMdns);
            if (addedGroupMember != null) {
                for (Collection<KnCorpContactDTO> addMem : addedGroupMember.values()) {
                    for (KnCorpContactDTO mdnMember : addMem) {
                        profileMdns.add(mdnMember.getMdn());
                    }
                }
            }
            if (deletedGroupMember != null) {
                for (LinkedList<String> delMem : deletedGroupMember.values()) {
                    profileMdns.addAll(delMem);
                }
            }
            if (!profileMdns.isEmpty()) {
                removeBaseMdnOfMcxGroupProfileMdnFromEtag(new ArrayList<>(profileMdns), eTags, profileMdnOfBaseMap);
            }
            baseMdnETags = convertEtagTOBaseMdn(eTags, profileMdnOfBaseMap);
            mdnMcpttMap = KnCorpSubsProvInfoUtil.getMdnMcpttIdMap(targetMdnList, persisterTxn, pttServerId);
        } catch (KnDAOException | KnCorpBOException e) {
            knLogger.error(methodName, " getRealMdns details error", e);
        }

        LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();
        for (String mdn : targetMdnList) {
            KnOPDirChgDTO directory = baseMdnETags.get(mdn);
            if (directory != null) {
                Collection<KnOPDocChgDTO> docList = directory.getDocChgDTO();
                Iterator ite = docList.iterator();
                while (ite.hasNext()) {
                    KnOPDocChgDTO doc = (KnOPDocChgDTO) ite.next();
                    short contact = 1;
                    KnLIEventDTO liEventDTO = new KnLIEventDTO();
                    liEventDTO.setMdn(mdn);
                    liEventDTO.setMcpttId(mdnMcpttMap.get(mdn));
                    if (doc.getDocUri().contains("resource")) {
                        Collection<String> addedMembers = new ArrayList<String>();
                        if (doc.getAddedContactList() != null) {
                            for (KnSubscriberDTO subsc : doc.getAddedContactList()) {
                                addedMembers.add(subsc.getMdn());
                            }
                        }
                        liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                        liEventDTO.setDeletedMembers(getBaseMdnList(doc.getRemovedContactList(), profileMdnOfBaseMap));
                        liEventDTO.setAction(KnLIConstants.MODIFY_CONTACTS);
                    } else if (doc.getDocUri().contains("kn-corp-groups")) {
                        int couter = doc.getDocUri().lastIndexOf(".xml");
                        String grpId;
                        if (couter > 0) {
                            grpId = doc.getDocUri().substring(0, couter);
                        } else {
                            couter = doc.getEntryUri().lastIndexOf(".xml");
                            grpId = doc.getEntryUri().substring(0, couter);
                        }
                        int couter1 = grpId.lastIndexOf("/");
                        grpId = grpId.substring(couter1 + 1).trim();
                        String groupURI = "tel:+".concat(mdn).concat(";kn-corp-group=");
                        if (groupNameMap != null && groupNameMap.get(Integer.valueOf(grpId)) != null) {
                            groupURI = groupURI.concat(groupNameMap.get(Integer.valueOf(grpId)));
                        }
                        liEventDTO.setGroupURI(groupURI);
                        Collection<String> addedMembers = new ArrayList<>();
                        if (doc.getAddedGroupMembers() != null) {
                            for (KnSubscriberDTO subsc : doc.getAddedGroupMembers()) {
                                addedMembers.add(subsc.getMdn());
                            }
                        }
                        boolean profileBaseExists = false;
                        //Target base & profile exist in the group
                        if (doc.getAddedGroupMembers() == null && addedMembers.isEmpty()
                                && doc.getRemovedGroupMembers() == null
                                && doc.getDocumentChgType() == DOC_CHANGE_TYPE.REPLACE.value()) {
                            for (String memberMDN : groupMembersMap.get(Integer.valueOf(grpId))) {
                                if (!memberMDN.equalsIgnoreCase(mdn)) {
                                    addedMembers.add(memberMDN);
                                }
                            }
                            profileBaseExists = true;
                            liEventDTO.setAction(KnLIConstants.CREATE_GROUP_ACTION);
                        }

                        liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                        contact = 2;
                        liEventDTO.setDeletedMembers(getBaseMdnList(doc.getRemovedGroupMembers(), profileMdnOfBaseMap));
                        if (doc.getAddedGroupMembers() == null
                                && doc.getRemovedGroupMembers() == null
                                && doc.getDocumentChgType() == DOC_CHANGE_TYPE.REPLACE.value()
                                && (deletedGroupMember != null
                                && deletedGroupMember.get(Integer.valueOf(grpId)) != null
                                && !deletedGroupMember.get(Integer.valueOf(grpId)).isEmpty())) {
                            Collection<String> existingGroupMdn = initialGroupmembers.get(Integer.valueOf(grpId));
                            existingGroupMdn.removeAll(profileMdns);
                            liEventDTO.setDeletedMembers(getBaseMdnList(existingGroupMdn, profileMdnOfBaseMap));
                            liEventDTO.setAction(KnLIConstants.MODIFY_GROUP_ACTION);
                            //clearing the added members from above
                            liEventDTO.getAddedMembers().clear();
                        }
                        //self added target mdn will not have added members
                        if (doc.getAddedGroupMembers() == null
                                && doc.getRemovedGroupMembers() == null
                                && doc.getDocumentChgType() == DOC_CHANGE_TYPE.REPLACE.value()
                                && (addedGroupMember != null
                                && addedGroupMember.get(Integer.valueOf(grpId)) != null
                                && !addedGroupMember.get(Integer.valueOf(grpId)).isEmpty())) {
                            Collection<String> existingGroupMdn = initialGroupmembers.get(Integer.valueOf(grpId));
                            existingGroupMdn.removeAll(profileMdns);
                            liEventDTO.setAddedMembers(getBaseMdnList(existingGroupMdn, profileMdnOfBaseMap));
                            liEventDTO.setAction(KnLIConstants.MODIFY_GROUP_ACTION);
                        }

                        if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.ADD.value()) {
                            liEventDTO.setAction(KnLIConstants.CREATE_GROUP_ACTION);
                            if (groupMembersMap.get(Integer.valueOf(grpId)) != null
                                    && groupMembersMap.get(Integer.valueOf(grpId)).size() > 0) {
                                addedMembers = new ArrayList<>();
                                for (String memberMDN : groupMembersMap.get(Integer.valueOf(grpId))) {
                                    if (!memberMDN.equalsIgnoreCase(mdn)) {
                                        addedMembers.add(memberMDN);
                                    }
                                }
                                liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                            }
                        } else if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value()) {
                            liEventDTO.setAction(KnLIConstants.DELETE_GROUP_ACTION);
                        } else if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                            if ((addedMembers == null || addedMembers.size() == 0)
                                    && (liEventDTO.getDeletedMembers() == null || liEventDTO.getDeletedMembers().size() == 0)
                                    && (initialGroupmembers != null && initialGroupmembers.get(Integer.valueOf(grpId)) != null
                                    && !initialGroupmembers.get(Integer.valueOf(grpId)).isEmpty()
                                    && !initialGroupmembers.get(Integer.valueOf(grpId)).contains(mdn))) {
                                liEventDTO.setAction(KnLIConstants.CREATE_GROUP_ACTION);
                                if (groupMembersMap != null && groupMembersMap.get(Integer.valueOf(grpId)) != null
                                        && groupMembersMap.get(Integer.valueOf(grpId)).size() > 0) {
                                    addedMembers = new ArrayList<String>();
                                    for (String memberMDN : groupMembersMap.get(Integer.valueOf(grpId))) {
                                        if (!memberMDN.equalsIgnoreCase(mdn)) {
                                            addedMembers.add(memberMDN);
                                        }
                                    }
                                    liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                                }
                            } else {
                                if (!profileBaseExists)
                                    liEventDTO.setAction(KnLIConstants.MODIFY_GROUP_ACTION);
                            }
                        }
                    }
                    //in case of profile mdn already assigned,we need to remove the target mdn getting added to added members
                    if (null != liEventDTO.getAddedMembers() && !liEventDTO.getAddedMembers().isEmpty()) {
                        liEventDTO.getAddedMembers().remove(mdn);
                    }
                    liEventDTO.setDocumentType(contact);
                    liEventDTO.setPttServerId(pttServerId);
                    liEventDTO.setErrorCode(KnLIConstants.SUCCESS_CODE);
                    liEventList.add(liEventDTO);
                }
            }
        }
        knLogger.info(methodName, "Exit liEventList :", liEventList);
        return liEventList;
    }

    public static LinkedList<KnLIEventDTO> upmBGCPopulateLIData(Map<String, KnOPDirChgDTO> eTags, String
            pttServerId, Map<Integer, String> groupNameMap, List<String> allMembers
            , List<KnCorpContactDTO> addMembers, List<String> deletedMems
            , KnPersisterTxn persisterTxn) {

        final String methodName = "upmBGCPopulateLIData()";
        knLogger.debug(methodName, "etagMap - ", eTags, "groupNameMap - ", groupNameMap, "allMembers - ", allMembers,
                "addMembers - ", addMembers, "deletedMems - ", deletedMems);

        List<String> targetMdnList = new ArrayList<>();
        Map<String, List<String>> profileMdnOfBaseMap = new HashMap<>();
        Map<String, KnOPDirChgDTO> baseMdnETags = new HashMap<>();
        Map<String, String> mdnMcpttMap = new HashMap<>();
        List<String> addedMember=null;
        try {
            Collection<String> allGroupMembers=new HashSet<>();
            if(deletedMems!=null)
                allGroupMembers.addAll(deletedMems);
            if(addMembers!=null){
                addedMember=new ArrayList<>(addMembers.stream().map(KnCorpContactDTO::getMdn).collect(Collectors.toSet()));
                allGroupMembers.addAll(addedMember);
            }

            List<String> realMdns = groupInfoUtil.getRealMdns(new ArrayList<>(eTags.keySet()), pttServerId, persisterTxn);
            profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(realMdns, pttServerId, persisterTxn);
            targetMdnList = KnLIEventHandler.getTagetMdnList(realMdns);
            //removeBaseMdnOfProfileMdnFromEtag(new ArrayList<>(allGroupMembers),eTags,profileMdnOfBaseMap);
            List<String> baseMdns = removeProfileMdn(allGroupMembers, pttServerId, persisterTxn);
            if(baseMdns!=null){
                eTags.keySet().removeAll(new HashSet<>(baseMdns));
            }
            baseMdnETags=convertEtagTOBaseMdn(eTags,profileMdnOfBaseMap);
            mdnMcpttMap = KnCorpSubsProvInfoUtil.getMdnMcpttIdMap(targetMdnList, persisterTxn,pttServerId);
        } catch (KnDAOException | KnCorpBOException e) {
            knLogger.error(methodName, " getRealMdns details error", e);
        }

        LinkedList<KnLIEventDTO> liEventList = new LinkedList<>();
        for (String mdn : targetMdnList) {
            KnOPDirChgDTO directory = baseMdnETags.get(mdn);
            if (directory != null) {
                Collection<KnOPDocChgDTO> docList = directory.getDocChgDTO();
                Iterator ite = docList.iterator();
                knLogger.debug("populateLIData", "mdn ", KnGDPRTemplate.mdn(mdn));
                while (ite.hasNext()) {
                    KnOPDocChgDTO doc = (KnOPDocChgDTO) ite.next();
                    short contact = 1;
                    KnLIEventDTO liEventDTO = new KnLIEventDTO();
                    liEventDTO.setMdn(mdn);
                    liEventDTO.setMcpttId(mdnMcpttMap.get(mdn));
                    if (doc.getDocUri().contains("resource")) {
                        Collection<String> addedMembers = new ArrayList<String>();
                        if (doc.getAddedContactList() != null) {
                            for (KnSubscriberDTO subsc : doc.getAddedContactList()) {
                                addedMembers.add(subsc.getMdn());
                            }
                        }
                        liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                        liEventDTO.setDeletedMembers(getBaseMdnList(doc.getRemovedContactList(), profileMdnOfBaseMap));
                        liEventDTO.setAction(KnLIConstants.MODIFY_CONTACTS);
                    } else {
                        contact = 2;
                        int couter = doc.getDocUri().lastIndexOf(".xml");
                        String grpId;
                        if (couter > 0) {
                            grpId = doc.getDocUri().substring(0, couter);
                        } else {
                            couter = doc.getEntryUri().lastIndexOf(".xml");
                            grpId = doc.getEntryUri().substring(0, couter);
                        }
                        int couter1 = grpId.lastIndexOf("/");
                        grpId = grpId.substring(couter1 + 1).trim();
                        String groupURI = "tel:+".concat(mdn).concat(";kn-corp-group=");
                        if (groupNameMap != null && groupNameMap.get(Integer.valueOf(grpId)) != null) {
                            groupURI = groupURI.concat(groupNameMap.get(Integer.valueOf(grpId)));
                        }
                        liEventDTO.setGroupURI(groupURI);
                        List<String> addedMembers = new ArrayList<>();
                        for (KnCorpContactDTO subscriberDTO : addMembers) {
                            addedMembers.add(subscriberDTO.getMdn());
                        }

                        if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.ADD.value()) {
                            liEventDTO.setAction(KnLIConstants.CREATE_GROUP_ACTION);
                            liEventDTO.setAddedMembers(getBaseMdnList(allMembers, profileMdnOfBaseMap));
                        } else if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value()) {
                            liEventDTO.setAction(KnLIConstants.DELETE_GROUP_ACTION);
                        } else if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                            liEventDTO.setAction(KnLIConstants.MODIFY_GROUP_ACTION);
                            liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                            liEventDTO.setDeletedMembers(getBaseMdnList(deletedMems, profileMdnOfBaseMap));
                        }
                    }
                    //in case of profile mdn already assigned,we need to remove the target mdn getting added to added members
                    if (liEventDTO.getAddedMembers() != null && !liEventDTO.getAddedMembers().isEmpty()) {
                        liEventDTO.getAddedMembers().remove(mdn);
                    }
                    liEventDTO.setDocumentType(contact);
                    liEventDTO.setPttServerId(pttServerId);
                    liEventDTO.setErrorCode(KnLIConstants.SUCCESS_CODE);
                    liEventList.add(liEventDTO);
                }
            }
        }
        knLogger.debug(methodName, " Exit liEventList :", liEventList);
        return liEventList;
    }

    /**
     * This method is used to create the LI data for logging in case of the create group API.
     *
     * @param eTags
     * @param pttServerId
     * @param groupMembers
     * @param groupInfo
     * @param externalMember
     * @return
     */
    public static LinkedList<KnLIEventDTO> populateAddGroupLIData(Map<String, KnOPDirChgDTO> eTags, String pttServerId,
                                                                  Collection<KnCorpSubscriberDTO> groupMembers,
                                                                  KnIPCorpGroupInfoDTO groupInfo
            , Collection<String> externalMember,KnPersisterTxn persisterTxn) {
        final String methodName="populateAddGroupLIData()";
        knLogger.debug(methodName, "etagMap", eTags);
        List<String> targetMdnList = new ArrayList<>();
        Map<String, String> mdnMcpttMap = new HashMap<>();
        Map<String, List<String>> profileMdnOfBaseMap =null;
        Map<String, KnOPDirChgDTO> baseMdnETags=new HashMap<>();
        try {
            List<String> realMdns = groupInfoUtil.getRealMdns(new ArrayList<>(eTags.keySet()), pttServerId, persisterTxn);
            targetMdnList = KnLIEventHandler.getTagetMdnList(realMdns);
            profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(realMdns,
                    pttServerId, persisterTxn);
            baseMdnETags=convertEtagTOBaseMdn(eTags,profileMdnOfBaseMap);
            mdnMcpttMap = KnCorpSubsProvInfoUtil.getMdnMcpttIdMap(targetMdnList, persisterTxn,pttServerId);
        } catch (KnDAOException | KnCorpBOException e) {
            knLogger.error(methodName," getMdnMcpttIdMap error",e);
        }
        LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();
        for (String targetMdn : targetMdnList) {
            KnOPDirChgDTO directory = baseMdnETags.get(targetMdn);
            if (directory != null) {
                Collection<KnOPDocChgDTO> docList = directory.getDocChgDTO();
                Iterator ite = docList.iterator();
                while (ite.hasNext()) {
                    KnOPDocChgDTO doc = (KnOPDocChgDTO) ite.next();
                    short contact = 1;
                    KnLIEventDTO liEventDTO = new KnLIEventDTO();
                    liEventDTO.setMdn(targetMdn);
                    liEventDTO.setMcpttId(mdnMcpttMap.get(targetMdn));
                    ArrayList<String> addedMemberList = new ArrayList<String>();
                    if (doc.getDocUri().contains("group")) {
                        for (KnCorpSubscriberDTO subsc : groupMembers) {
                            if (!targetMdn.equalsIgnoreCase(subsc.getMdn())) {
                                addedMemberList.add(subsc.getMdn());
                            }
                        }
                        if (externalMember != null && !externalMember.isEmpty()) {
                            addedMemberList.addAll(externalMember);
                        }
                        liEventDTO.setAddedMembers(getBaseMdnList(addedMemberList, profileMdnOfBaseMap));
                        contact = 2;
                        liEventDTO.setAction(KnLIConstants.CREATE_GROUP_ACTION);
                        liEventDTO.setGroupURI("tel:+" + targetMdn + ";kn-corp-groups=" + groupInfo.getCorpId() + "_" + groupInfo.getGroupDisplayName());
                    } else {
                        if (doc.getAddedContactList() != null) {
                            for (KnSubscriberDTO subsc : doc.getAddedContactList()) {
                                addedMemberList.add(subsc.getMdn());
                            }
                        }
                        liEventDTO.setAddedMembers(getBaseMdnList(addedMemberList, profileMdnOfBaseMap));
                    }
                    liEventDTO.setDocumentType(contact);
                    liEventDTO.setPttServerId(pttServerId);
                    liEventDTO.setErrorCode(KnLIConstants.SUCCESS_CODE);
                    liEventList.add(liEventDTO);
                }
            }
        }
        knLogger.debug(methodName," Exit liEventList ",liEventList);
        return liEventList;
    }

    /**
     * This method is used to create the LI data for logging in case of the create BC group API.
     *
     * @param eTags
     * @param pttServerId
     * @param groupInfo
     * @param allGrpMembers
     * @return
     */
    public static LinkedList<KnLIEventDTO> populateAddGroupLIData(Map<String, KnOPDirChgDTO> eTags, String pttServerId,
                                                                  KnIPCorpGroupInfoDTO groupInfo
            , Collection<String> allGrpMembers,KnPersisterTxn persisterTxn) {
        final String methodName="populateAddGroupLIData()";
        knLogger.debug(methodName, "etagMap", eTags);
        List<String> targetMdnList = new ArrayList<>();
        Map<String, String> mdnMcpttMap = new HashMap<>();
        Map<String, KnOPDirChgDTO> baseMdnETags=new HashMap<>();
        Map<String, List<String>> profileMdnOfBaseMap =null;
        try {
            List<String> realMdns = groupInfoUtil.getRealMdns(new ArrayList<>(eTags.keySet()), pttServerId, persisterTxn);
            targetMdnList = KnLIEventHandler.getTagetMdnList(realMdns);
            profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(realMdns,
                    pttServerId, persisterTxn);
            baseMdnETags=convertEtagTOBaseMdn(eTags,profileMdnOfBaseMap);
            mdnMcpttMap = KnCorpSubsProvInfoUtil.getMdnMcpttIdMap(targetMdnList, persisterTxn,pttServerId);
        } catch (KnDAOException | KnCorpBOException e) {
            knLogger.error(methodName," getMdnMcpttIdMap error",e);
        }
        LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();
        for (String targetMdn : targetMdnList) {
            KnOPDirChgDTO directory = baseMdnETags.get(targetMdn);
            if (directory != null) {
                Collection<KnOPDocChgDTO> docList = directory.getDocChgDTO();
                Iterator ite = docList.iterator();
                while (ite.hasNext()) {
                    KnOPDocChgDTO doc = (KnOPDocChgDTO) ite.next();
                    short contact = 1;
                    KnLIEventDTO liEventDTO = new KnLIEventDTO();
                    liEventDTO.setMdn(targetMdn);
                    liEventDTO.setMcpttId(mdnMcpttMap.get(targetMdn));
                    if (doc.getDocUri().contains("group")) {
                        liEventDTO.setAddedMembers(getBaseMdnList(allGrpMembers, profileMdnOfBaseMap));
                        contact = 2;
                        liEventDTO.setAction(KnLIConstants.CREATE_GROUP_ACTION);
                        liEventDTO.setGroupURI("tel:+" + targetMdn + ";kn-corp-groups=" + groupInfo.getCorpId() + "_" + groupInfo.getGroupDisplayName());
                    } else {
                        if (doc.getAddedContactList() != null) {
                            for (KnSubscriberDTO subsc : doc.getAddedContactList()) {
                                allGrpMembers.add(subsc.getMdn());
                            }
                        }
                        liEventDTO.setAddedMembers(getBaseMdnList(allGrpMembers, profileMdnOfBaseMap));
                    }
                    liEventDTO.setDocumentType(contact);
                    liEventDTO.setPttServerId(pttServerId);
                    liEventDTO.setErrorCode("00000");
                    liEventList.add(liEventDTO);
                }
            }
        }
        knLogger.debug(methodName," Exit liEventList:",liEventList);
        return liEventList;
    }

    /**
     * Method called when the group deleted then LI has to be logged for the delete group operation
     *
     * @param eTags
     * @param pttServerId
     * @param groupDTO
     * @return
     */
    public static LinkedList<KnLIEventDTO> populateDeleteGroupLIData(Map<String, KnOPDirChgDTO> eTags, String pttServerId
            , KnCorpGroupInfoPersistDTO groupDTO,KnPersisterTxn persisterTxn) {
        final String methodName="populateDeleteGroupLIData()";
        knLogger.debug(methodName, "etagMap", eTags);
        LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();
        if (null != eTags) {
            List<String> targetMdnList = new ArrayList<>();
            Map<String, String> mdnMcpttMap = new HashMap<>();
            Map<String, List<String>> profileMdnOfBaseMap =null;
            Map<String, KnOPDirChgDTO> baseMdnETags=new HashMap<>();
            try {
                List<String> realMdns = groupInfoUtil.getRealMdns(new ArrayList<>(eTags.keySet()), pttServerId, persisterTxn);
                targetMdnList = KnLIEventHandler.getTagetMdnList(realMdns);
                profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(realMdns,
                        pttServerId, persisterTxn);
                baseMdnETags=convertEtagTOBaseMdn(eTags,profileMdnOfBaseMap);
                mdnMcpttMap = KnCorpSubsProvInfoUtil.getMdnMcpttIdMap(targetMdnList, persisterTxn,pttServerId);
            } catch (KnDAOException | KnCorpBOException e) {
                knLogger.error(methodName," getMdnMcpttIdMap error",e);
            }
            for (String targetMdn : targetMdnList) {
                KnOPDirChgDTO directory = baseMdnETags.get(targetMdn);
                if(directory != null) {
                Collection<KnOPDocChgDTO> docList = directory.getDocChgDTO();
                Iterator ite = docList.iterator();
                while (ite.hasNext()) {
                    KnOPDocChgDTO doc = (KnOPDocChgDTO) ite.next();
                    KnLIEventDTO liEventDTO = new KnLIEventDTO();
                    liEventDTO.setMdn(targetMdn);
                    liEventDTO.setMcpttId(mdnMcpttMap.get(targetMdn));
                    short contact = 2;
                    if (doc.getDocUri().contains("group")) {
                        liEventDTO.setAction(KnLIConstants.DELETE_GROUP_ACTION);
                        liEventDTO.setGroupURI("tel:+" + targetMdn + ";kn-corp-groups=" + groupDTO.getCorpId() + "_" + groupDTO.getGroupDisplayName());
                    } else {
                        liEventDTO.setAction(KnLIConstants.OTHER_GROUP_ACTION);
                        liEventDTO.setDeletedMembers(getBaseMdnList(doc.getRemovedContactList(), profileMdnOfBaseMap));
                        contact = 1;
                    }
                    liEventDTO.setDocumentType(contact);
                    liEventDTO.setPttServerId(pttServerId);
                    liEventDTO.setErrorCode("00000");
                    liEventList.add(liEventDTO);
                }
            }
            }
        }
        knLogger.debug(methodName,"Exit liEventList:",liEventList);
        return liEventList;
    }


    /**
     * @param groupMemMap
     * @param groupEtagMap
     * @param groupNameMap
     * @param documntChngType
     * @param eTags
     * @param grpMemCountMap
     * @return
     */
    public Map<String, KnOPDirChgDTO> formSubscriberNotification(Map<Integer, List<String>> groupMemMap, Map<Integer, Integer>
            groupEtagMap, Map<Integer, String> groupNameMap, int documntChngType, Map<String, KnOPDirChgDTO>
                                                                         eTags, Map<Integer, Integer> grpMemCountMap
            , Map<Integer, Integer> avatarMap) {


            String methodName = "formSubscriberNotification(Map, Map, Map, int, Map, Map)";
        knLogger.debug(methodName, "groupMemMap - ", groupMemMap, " , groupEtagMap - ", groupEtagMap,
                ", documntChngType - ", documntChngType, " , eTags - ", eTags
                , "groupNameMap -", groupNameMap, "grpMemCountMap- ", grpMemCountMap);
        if (eTags == null) {
            eTags = new HashMap<>();
        }
        Collection<Integer> groupIdList = groupEtagMap.keySet();
        for (int grpId : groupIdList) {
            int etag = groupEtagMap.get(grpId);
            int prevEtag = etag - 1;
            Collection<String> currntMembers = groupMemMap.get(grpId);
            if (currntMembers != null && !currntMembers.isEmpty()) {
                for (String mdn : currntMembers) {
                    KnOPDirChgDTO dirChgDto = eTags.get(mdn);
                    if (isObjectNull(dirChgDto)) {
                        dirChgDto = new KnOPDirChgDTO();
                        dirChgDto.setDirUri(getDirectoryURI(mdn));
                    }
                    KnOPDocChgDTO docChgDto = new KnOPDocChgDTO();
                    docChgDto.setDocType(documntChngType);
                    docChgDto.setDocumentChgType(documntChngType);
                    docChgDto.setNewEtag(String.valueOf(etag));
                    docChgDto.setPrevEtag(String.valueOf(prevEtag));
                    docChgDto.setDocUri(KnCorpCommonInfoUtil.getGroupDocumentURI(mdn, grpId, documntChngType));
                    docChgDto.setGroupId(grpId);
                    if (null != groupNameMap) {
                        docChgDto.setGroupName(groupNameMap.get(grpId));
                    }
                    if (null != grpMemCountMap && null != grpMemCountMap.get(grpId)) {
                        docChgDto.setGroupMemCount(grpMemCountMap.get(grpId));
                    }
                    if (null != avatarMap && null != avatarMap.get(grpId)) {
                        docChgDto.setAvatar(avatarMap.get(grpId));
                    }

                    docChgDto.setEntryUri(KnCorpCommonInfoUtil.getAddGroupEntryUri(mdn, grpId));
                    Collection<KnOPDocChgDTO> dirDocLst = dirChgDto.getDocChgDTO();
                    if (isObjectNull(dirDocLst)) {
                        dirDocLst = new ArrayList<>();
                    }
                    dirDocLst.add(docChgDto);
                    dirChgDto.setDocChgDTO(dirDocLst);
                    eTags.put(mdn, dirChgDto);
                }
            }
        }
        knLogger.debug(methodName, "eTags - ", eTags);
        return eTags;
    }


    /**
     * @param eTags
     * @param pttServerId
     * @param groupNameMap
     * @param allMembers
     * @param addMembers
     * @param deletedMems
     * @return
     */
    public static LinkedList<KnLIEventDTO> populateLIData(Map<String, KnOPDirChgDTO> eTags
            ,String pttServerId, Map<Integer, String> groupNameMap
            ,List<String> allMembers, List<KnCorpContactDTO> addMembers, List<String> deletedMems
            ,KnPersisterTxn persisterTxn) {

        final String methodName="populateLIData()";
        knLogger.debug(methodName, "etagMap - ", eTags, "groupNameMap - ", groupNameMap, "allMembers - ", allMembers,
                "addMembers - ", addMembers, "deletedMems - ", deletedMems);

        List<String> targetMdnList = new ArrayList<>();
        Map<String, String> mdnMcpttMap = new HashMap<>();
        Map<String, List<String>> profileMdnOfBaseMap=new HashMap<>();
        Map<String, KnOPDirChgDTO> baseMdnETags=new HashMap<>();
        try {
            Set<String> allGroupMembers=new HashSet<>();
            if(allMembers!=null)
            allGroupMembers.addAll(allMembers);
            if(deletedMems!=null)
            allGroupMembers.addAll(deletedMems);
            if(addMembers!=null){
                allGroupMembers.addAll(addMembers.stream().map(KnCorpContactDTO::getMdn).collect(Collectors.toSet()));
            }

            List<String> realMdns = groupInfoUtil.getRealMdns(new ArrayList<>(eTags.keySet()), pttServerId, persisterTxn);
            targetMdnList = KnLIEventHandler.getTagetMdnList(realMdns);
            profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(realMdns,
                    pttServerId, persisterTxn);
            //profile with no base mdn in group
            Map<String, KnOPDirChgDTO> profileWithNoBaseInGroup = profileWithNoBaseInBGCGroup(targetMdnList,
                    allMembers, convertEtagTOBaseMdn(eTags, profileMdnOfBaseMap));
            if(!profileWithNoBaseInGroup.isEmpty()) {
                baseMdnETags.putAll(profileWithNoBaseInGroup);
            }
            //base mdn with profile mdn in group
            Map<String, KnOPDirChgDTO> etagWithBaseMdn = removeProfileMdnEtags(targetMdnList,
                    profileMdnOfBaseMap, eTags);
            if(!etagWithBaseMdn.isEmpty()){
                baseMdnETags.putAll(etagWithBaseMdn);
            }
            mdnMcpttMap = KnCorpSubsProvInfoUtil.getMdnMcpttIdMap(targetMdnList, persisterTxn,pttServerId);

        } catch (KnCorpBOException | KnDAOException e) {
            knLogger.error(methodName," getMdnMcpttIdMap error",e);
        }
        LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();
        for (String mdn : targetMdnList) {
            KnOPDirChgDTO directory = baseMdnETags.get(mdn);
            if(directory!=null) {
                Collection<KnOPDocChgDTO> docList = directory.getDocChgDTO();
                Iterator ite = docList.iterator();
                knLogger.debug("populateLIData", "mdn ", KnGDPRTemplate.mdn(mdn));
                while (ite.hasNext()) {
                    KnOPDocChgDTO doc = (KnOPDocChgDTO) ite.next();
                    short contact = 1;
                    KnLIEventDTO liEventDTO = new KnLIEventDTO();
                    liEventDTO.setMdn(mdn);
                    liEventDTO.setMcpttId(mdnMcpttMap.get(mdn));
                    if (doc.getDocUri().contains("resource")) {
                        Collection<String> addedMembers = new ArrayList<String>();
                        if (doc.getAddedContactList() != null) {
                            for (KnSubscriberDTO subsc : doc.getAddedContactList()) {
                                addedMembers.add(subsc.getMdn());
                            }
                        }
                        liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                        liEventDTO.setDeletedMembers(getBaseMdnList(doc.getRemovedContactList(), profileMdnOfBaseMap));
                        liEventDTO.setAction(KnLIConstants.MODIFY_CONTACTS);
                    } else {
                        contact = 2;
                        int couter = doc.getDocUri().lastIndexOf(".xml");
                        String grpId;
                        if (couter > 0) {
                            grpId = doc.getDocUri().substring(0, couter);
                        } else {
                            couter = doc.getEntryUri().lastIndexOf(".xml");
                            grpId = doc.getEntryUri().substring(0, couter);
                        }
                        int couter1 = grpId.lastIndexOf("/");
                        grpId = grpId.substring(couter1 + 1).trim();
                        String groupURI = "tel:+".concat(mdn).concat(";kn-corp-group=");
                        if (groupNameMap != null && groupNameMap.get(Integer.valueOf(grpId)) != null) {
                            groupURI = groupURI.concat(groupNameMap.get(Integer.valueOf(grpId)));
                        }
                        liEventDTO.setGroupURI(groupURI);
                        List<String> addedMembers = new ArrayList<>();
                        for (KnCorpContactDTO subscriberDTO : addMembers) {
                            addedMembers.add(subscriberDTO.getMdn());
                        }

                        if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.ADD.value()) {
                            liEventDTO.setAction(KnLIConstants.CREATE_GROUP_ACTION);
                            liEventDTO.setAddedMembers(getBaseMdnList(allMembers, profileMdnOfBaseMap));
                        } else if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value()) {
                            liEventDTO.setAction(KnLIConstants.DELETE_GROUP_ACTION);
                        } else if (doc.getDocumentChgType() == com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                            liEventDTO.setAction(KnLIConstants.MODIFY_GROUP_ACTION);
                            liEventDTO.setAddedMembers(getBaseMdnList(addedMembers, profileMdnOfBaseMap));
                            liEventDTO.setDeletedMembers(getBaseMdnList(deletedMems, profileMdnOfBaseMap));
                        }
                    }
                    //in case of profile mdn already assigned,we need to remove the target mdn getting added to added members
                    if (liEventDTO.getAddedMembers() != null && !liEventDTO.getAddedMembers().isEmpty()) {
                        liEventDTO.getAddedMembers().remove(mdn);
                    }
                    liEventDTO.setDocumentType(contact);
                    liEventDTO.setPttServerId(pttServerId);
                    liEventDTO.setErrorCode(KnLIConstants.SUCCESS_CODE);
                    liEventList.add(liEventDTO);
                }
            }
        }
        knLogger.debug(methodName," Exit liEventList :",liEventList);
        return liEventList;
    }

    public Map<String, KnTGSModeChgDTO> consrtuctTgsModeChgMap(KnSubsProfileDTO subsProfile) {
        Map<String, KnTGSModeChgDTO> tgsModeChgMap = new HashMap<String, KnTGSModeChgDTO>();
        KnTGSModeChgDTO value = new KnTGSModeChgDTO();
        value.setPocHome(subsProfile.getPocHome());
        value.setPresenceHome(subsProfile.getPresenceHome());
        value.setTgsMode(0);
        tgsModeChgMap.put(subsProfile.getMdn(), value);
        return tgsModeChgMap;
    }

    public KnCorpGWLinkedAccountInfoDTO getCorporateLinkedAccountInfo(String refId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getCorporateLinkedAccountInfo(refId, xdmsHome, false, persisterTxn);
    }
    /**
     * Get the poc nni generic configuration
     *
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public KnCorpGWLinkedAccountInfoDTO getCorporateLinkedAccountInfo(String refId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getCorporateLinkedAccountInfo(String, String,boolean, KnPersisterTxn)";
        // retrieve group list
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getCorporateLinkedAccountInfo(refId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, String> getVendorID(List<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getVendorID(String, KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdnList(mdnList));
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getVendorID(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnTPVendorDetailsPersistDTO getVendorDetails(String vendorID, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getVendorDetails(String, KnPersisterTxn)";
        knLogger.debug(methodName, vendorID);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getVendorDetails(vendorID, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting group list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> formTGSCDocumentNotification(KnCorpInOutParamDTO corpInOutParamDTO,
                                                                   Map<String, KnOPDirChgDTO> etagMap, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "formTGSCDocumentNotification";
        try {
            Map<String, Integer> mdnTGSCEtag = corpInOutParamDTO.getMdnTgscEtag();
            Map<String, KnTGSModeChgDTO> mdnTGSCMode = corpInOutParamDTO.getTgsModeChgMap();

            knLogger.debug(methodName, "mdnTGSCMode", mdnTGSCMode);
            knLogger.debug(methodName, "mdnTGSCEtag", mdnTGSCEtag);
            Map<String, Boolean> tgscModeMap = new HashMap<String, Boolean>();
            Set<String> mdnList = new HashSet<String>();
            if (mdnTGSCEtag != null) {
                mdnList = new HashSet<String>(mdnTGSCEtag.keySet());
            }
            if (mdnTGSCMode != null) {
                mdnList.addAll(new HashSet<String>(mdnTGSCMode.keySet()));
            }
            //get the tgsc feature bit for the subsc fetature bit
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            tgscModeMap = xdmDAO.getTgscFeatureBit(mdnList, persisterTxn);
            if (mdnTGSCEtag != null) {
                for (String mdn : mdnTGSCEtag.keySet()) {
                    if (tgscModeMap != null && tgscModeMap.get(mdn)) {
                        KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
                        if ((mdnTGSCMode == null) || !mdnTGSCMode.containsKey(mdn)) {
                            Collection<KnOPDocChgDTO> documents = new ArrayList<KnOPDocChgDTO>();
                            if (dirChgDTO == null) {
                                dirChgDTO = new KnOPDirChgDTO();
                                dirChgDTO.setDirUri(getDirectoryURI(mdn));
                            } else {
                                documents = dirChgDTO.getDocChgDTO();
                            }
                            KnOPDocChgDTO doc = new KnOPDocChgDTO();
                            String docURI = "xcap-directory/folder%5B@auid=%22kn-tgsc-list%22%5D/entry%5B@uri=%22kn-tgsc-list/users/tel:+";
                            docURI = docURI.concat(mdn);
                            docURI = docURI.concat("/index%22%5D/@etag");
                            doc.setDocUri(docURI);
                            doc.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                            doc.setNewEtag(String.valueOf(mdnTGSCEtag.get(mdn)));
                            documents.add(doc);
                            dirChgDTO.setDocChgDTO(documents);
                            etagMap.put(mdn, dirChgDTO);
                        }
                    }
                }
            }
            if (mdnTGSCMode != null && mdnTGSCEtag != null) {
                for (String mdn : mdnTGSCMode.keySet()) {
                    if (tgscModeMap.get(mdn)) {
                        KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
                        Collection<KnOPDocChgDTO> documents = new ArrayList<KnOPDocChgDTO>();
                        if (dirChgDTO == null) {
                            dirChgDTO = new KnOPDirChgDTO();
                            dirChgDTO.setDirUri(getDirectoryURI(mdn));
                        } else {
                            documents = dirChgDTO.getDocChgDTO();
                        }
                        KnOPDocChgDTO doc = new KnOPDocChgDTO();
                        String docURI = "xcap-directory/folder%5B@auid=%22kn-tgsc-list%22%5D/entry%5B@uri=%22kn-tgsc-list/users/tel:+";
                        docURI = docURI.concat(mdn);
                        docURI = docURI.concat("/index%22%5D");
                        doc.setDocUri(docURI);
                        doc.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
                        doc.setNewEtag(String.valueOf(mdnTGSCEtag.get(mdn)));
                        documents.add(doc);
                        dirChgDTO.setDocChgDTO(documents);
                        etagMap.put(mdn, dirChgDTO);
                    }
                }
            }
            return etagMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while forming the tgsc notification - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method is used to form the delete tgsc notification for the subscriber
     *
     * @param mdn
     * @param etagMap
     * @return
     */
    public Map<String, KnOPDirChgDTO> formMdnTGSCDeleteNotification(String mdn, Map<String, KnOPDirChgDTO> etagMap,
                                                                    String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "formMdnTGSCDeleteNotification";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Set<String> mdnlist = new HashSet<String>();
            mdn = mdn.trim();
            mdnlist.add(mdn);
            Map<String, Boolean> tgscEnabledMap = xdmDAO.getTgscFeatureBit(mdnlist, persisterTxn);
            if (tgscEnabledMap.get(mdn)) {
                KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
                Collection<KnOPDocChgDTO> documents = new ArrayList<KnOPDocChgDTO>();
                if (dirChgDTO == null) {
                    dirChgDTO = new KnOPDirChgDTO();
                    dirChgDTO.setDirUri(getDirectoryURI(mdn));
                } else {
                    documents = dirChgDTO.getDocChgDTO();
                }
                KnOPDocChgDTO doc = new KnOPDocChgDTO();
                String docURI = "xcap-directory/folder%5B@auid=%22kn-tgsc-list%22%5D/entry%5B@uri=%22kn-tgsc-list/users/tel:+";
                docURI = docURI.concat(mdn);
                docURI = docURI.concat("/index%22%5D");
                doc.setDocUri(docURI);
                doc.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
                doc.setNewEtag("0");
                documents.add(doc);
                dirChgDTO.setDocChgDTO(documents);
                etagMap.put(mdn, dirChgDTO);
            }
            return etagMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while forming tgsc notification - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method is used to form the delete tgsc notification for the subscriber
     *
     * @param mdn
     * @param etagMap
     * @return
     */
    public Map<String, KnOPDirChgDTO> formMdnTGSCReplaceNotification(String mdn, Map<String, KnOPDirChgDTO> etagMap,
                                                                     String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "formMdnTGSCReplaceNotification";
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Set<String> mdnlist = new HashSet<String>();
            mdn = mdn.trim();
            mdnlist.add(mdn);
            Map<String, Boolean> tgscEnabledMap = xdmDAO.getTgscFeatureBit(mdnlist, persisterTxn);
            Map<String, Integer> tgscEtagMap = xdmDAO.getSubscTgscDocEtag(new ArrayList<String>(mdnlist), persisterTxn);
            if (tgscEnabledMap.get(mdn)) {
                KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
                Collection<KnOPDocChgDTO> documents = new ArrayList<KnOPDocChgDTO>();
                if (dirChgDTO == null) {
                    dirChgDTO = new KnOPDirChgDTO();
                    dirChgDTO.setDirUri(getDirectoryURI(mdn));
                } else {
                    documents = dirChgDTO.getDocChgDTO();
                }
                KnOPDocChgDTO doc = new KnOPDocChgDTO();
                String docURI = "xcap-directory/folder%5B@auid=%22kn-tgsc-list%22%5D/entry%5B@uri=%22kn-tgsc-list/users/tel:+";
                docURI = docURI.concat(mdn);
                docURI = docURI.concat("/index%22%5D");
                doc.setDocUri(docURI);
                doc.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                doc.setNewEtag(tgscEtagMap.get(mdn).toString());
                documents.add(doc);
                dirChgDTO.setDocChgDTO(documents);
                etagMap.put(mdn, dirChgDTO);
            }
            return etagMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while forming tgsc notification - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public static Map<String, KnOPDirChgDTO> formXcapAuthDiffNotification(Map<String, KnOPDirChgDTO> etagMap,
                                                                          Map<String, Collection<KnMcpttPermissionDTO>> addedTargetMap,
                                                                          Map<String, Collection<KnMcpttPermissionDTO>> modifiedTargetMap,
                                                                          Map<String, Collection<String>> removedTargetList,
                                                                          Map<String, Integer> resourceListUpdateForTarget,
                                                                          Map<String, KnCorpSubsEntitiesDTO> corpSubsEntitiesDTOMap,
                                                                          String authMdn, Boolean directoryDiff) {
        String methodName = "formXcapAuthDiffNotification(etagMap,modifiedContactMap,modifiedGrpMembers)";
        knLogger.debug(methodName, "Entry :  - etagMap - ", etagMap== null ? etagMap : KnGDPRTemplate.mapKeyMdn(etagMap), " - modifiedTargetMap -", modifiedTargetMap,
                " - addedTargetMap -", addedTargetMap, "- removedTargetList -", removedTargetList, "- resourceListUpdateForTarget - ",
                resourceListUpdateForTarget, "- authMdn - ", authMdn, "directoryDiff -", directoryDiff);
        etagMap.forEach((mdn, dirChgDTO) -> { dirChgDTO.getDocChgDTO().forEach(docChgDTO -> {
                if (docChgDTO.getDocUri().contains(APP_UID_AUTH_LIST) && docChgDTO.getDocumentChgType() != DOC_CHANGE_TYPE.REMOVE.value()) {
                    if (modifiedTargetMap != null && !modifiedTargetMap.isEmpty()) {
                        Collection<KnTargetPermsInfoDTO> modifiedTargetList = new ArrayList<KnTargetPermsInfoDTO>();
                        modifiedTargetMap.forEach((authorizedMdn, modTargetList) -> {
                            modTargetList.forEach(targetDtoList -> {
                                if(!targetDtoList.getTargetMdn().equals(mdn)){
                                    KnTargetPermsInfoDTO targetDto = new KnTargetPermsInfoDTO();
                                    targetDto.setMdn(targetDtoList.getTargetMdn());
                                    targetDto.setAliasMdn(corpSubsEntitiesDTOMap.get(targetDtoList.getTargetMdn()).getAliasMdn());
                                    targetDto.setUserId(corpSubsEntitiesDTOMap.get(targetDtoList.getTargetMdn()).getUserId());
                                    targetDto.setFper(fPermsValue(targetDtoList.getMcpttPerms()));
                                    targetDto.setFsts(fstsValue(targetDtoList.getServiceAuthUserAU(), targetDtoList.getDiscreteEnabled()));
                                    modifiedTargetList.add(targetDto);
                                }
                            });
                        });
                        docChgDTO.setModifiedTargetList(modifiedTargetList);
                    }
                    // If Authorization is creating first time, then we will send directoryDiff. So this will be taken
                    // care in KnXcapDiffNotify classw while preparing notification.
                    if (addedTargetMap != null && !addedTargetMap.isEmpty() && !directoryDiff) {
                        Collection<KnTargetPermsInfoDTO> addedTargetList = new ArrayList<KnTargetPermsInfoDTO>();
                        addedTargetMap.forEach((authorizedMdn, addTargetList) -> {
                            addTargetList.forEach(targetDtoList -> {
                                knLogger.debug("targetDtoList.getTargetMdn()",targetDtoList.getTargetMdn());
                                knLogger.debug("mdn  ",mdn);
                                if(!targetDtoList.getTargetMdn().equals(mdn)) {
                                    KnTargetPermsInfoDTO targetDto = new KnTargetPermsInfoDTO();
                                    targetDto.setMdn(targetDtoList.getTargetMdn());
                                    targetDto.setAliasMdn(corpSubsEntitiesDTOMap.get(targetDtoList.getTargetMdn()).getAliasMdn());
                                    targetDto.setUserId(corpSubsEntitiesDTOMap.get(targetDtoList.getTargetMdn()).getUserId());
                                    targetDto.setFper(fPermsValue(targetDtoList.getMcpttPerms()));
                                    targetDto.setFsts(fstsValue(targetDtoList.getServiceAuthUserAU(), targetDtoList.getDiscreteEnabled()));
                                    addedTargetList.add(targetDto);
                                }
                            });

                        });
                        docChgDTO.setAddedTargetList(addedTargetList);
                    }
                    if (removedTargetList != null && !removedTargetList.isEmpty()) {
                        docChgDTO.setRemovedTargetList(removedTargetList.get(mdn));
                    }
                } else if (docChgDTO.getDocUri().contains(APP_UID_CORP_RESOURCE_LIST)) {
                    if (resourceListUpdateForTarget != null && !resourceListUpdateForTarget.isEmpty()) {
                        Collection<KnSubscriberDTO> modifiedResourceListMap = new ArrayList<KnSubscriberDTO>();
                        resourceListUpdateForTarget.forEach((targetMdn, ambientBit) -> {
                            if(mdn.equals(targetMdn)){
                                KnSubscriberDTO authDto = new KnSubscriberDTO();
                                authDto.setMdn(authMdn);
                                authDto.setClientType(corpSubsEntitiesDTOMap.get(authMdn).getClientType());
                                authDto.setNetworkName(corpSubsEntitiesDTOMap.get(authMdn).getNetworkName());
                                authDto.setAliasMdn(corpSubsEntitiesDTOMap.get(authMdn).getAliasMdn());
                                authDto.setUserId(corpSubsEntitiesDTOMap.get(authMdn).getUserId());
                                authDto.setActiveFS2(corpSubsEntitiesDTOMap.get(authMdn).getActiveFs2());
                                authDto.setIsAuthUser(String.valueOf(ambientBit));
                                modifiedResourceListMap.add(authDto);
                            }
                        });
                        docChgDTO.setModifiedContactMembers(modifiedResourceListMap);
                    }
                }
            });
        });
        knLogger.debug(methodName, "Exit: etagMap: ",KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }

    public static Map<String, KnOPDirChgDTO> formXcapEmergDiffNotification(Map<String, KnOPDirChgDTO> etagMap,
                                                                           Collection<KnSubsDestEmergencyAttributes> addedDestinations,
                                                                           Collection<KnSubsDestEmergencyAttributes> dbExistingDestinations,
                                                                           Map<String, Integer> oldPriority, Map<String, Integer> newPriority,
                                                                           KnSubsEmergencyAttributes updateEmergAttributes,
                                                                           KnSubsEmergencyAttributes existingEmerAttributes,
                                                                           String subsMdn, String corpId, Boolean directoryDiff,
                                                                           Map<Integer, Integer> groupCorpIdMap) {
        String methodName = "formXcapEmergDiffNotification(etagMap, addedDestinations, oldPriority, newPriority)";
        knLogger.debug(methodName, "Entry :  - etagMap - ", etagMap==null ? etagMap : KnGDPRTemplate.mapKeyMdn(etagMap), " - addedDestinations -", addedDestinations,
                " dbExistingDestinations -", dbExistingDestinations, " - oldPriority -", oldPriority, "- newPriority -",
                newPriority, "subsMdn -", KnGDPRTemplate.mdn(subsMdn), "corpId -", corpId, "updateEmergAttributes - ", updateEmergAttributes,
                "existingEmerAttributes- ", existingEmerAttributes, "directoryDiff -", directoryDiff);
        etagMap.forEach((mdn, dirChgDTO) -> {
            dirChgDTO.getDocChgDTO().forEach(docChgDTO -> {
                if (docChgDTO.getDocUri().contains(APP_UID_EMERG_CONFIG)) {
                    Collection<String> removedDestList = new ArrayList<>();
                    Collection<String> updatedDestList = new ArrayList<>();
                    Collection<String> addedDestList = new ArrayList<>();
                    oldPriority.forEach((oldDest, priority) -> {
                        if (newPriority.containsKey(oldDest)) {
                            if(!priority.equals(newPriority.get(oldDest))) {
                                updatedDestList.add(oldDest);
                            }
                        } else {
                            removedDestList.add(oldDest);
                        }
                    });
                    newPriority.forEach((newDest, priority) -> {
                        if (!oldPriority.containsKey(newDest)) {
                            addedDestList.add(newDest);
                        }
                    });
                    knLogger.debug(methodName, "removedDestList - ", removedDestList, " updatedDestList -",
                            updatedDestList, " addedDestList -", addedDestList);
                    Collection<KnEmergencyInfoDTO> addedDestDTO = new ArrayList<>();
                    Collection<KnEmergencyInfoDTO> updatedDestDTO = new ArrayList<>();
                    KnEmergencyAttributesDTO addedEmergAttributes = new KnEmergencyAttributesDTO();
                    KnEmergencyAttributesDTO modifiedEmergAttributes = new KnEmergencyAttributesDTO();
                    KnEmergencyAttributesDTO removedEmergAttributes = new KnEmergencyAttributesDTO();
                    Collection<String> deletedDest = new ArrayList<>();
                    KnEmergencyInfoDTO emergencyInfoDTO = null;
                    for (KnSubsDestEmergencyAttributes subsDest : addedDestinations) {
                        if (addedDestList.contains(subsDest.getEmergDest())) {
                            emergencyInfoDTO = new KnEmergencyInfoDTO();
                            if(subsDest.getEmergDestTypeMgmt() == DESTINATION_TYPE_MGMT.CONTACT.value()){
                                emergencyInfoDTO.setDestination(subsDest.getEmergDest());
                            } else {
                                emergencyInfoDTO.setDestination(destValue(subsMdn, String.valueOf(groupCorpIdMap.getOrDefault(Integer.parseInt(subsDest.getEmergDest()), Integer.parseInt(corpId))), subsDest.getEmergDest()));
                            }
                            emergencyInfoDTO.setDestType(subsDest.getEmergDestTypeMgmt());
                            emergencyInfoDTO.setDestPriority(subsDest.getEmergDestPriority());
                            addedDestDTO.add(emergencyInfoDTO);
                        } else if (updatedDestList.contains(subsDest.getEmergDest())) {
                            emergencyInfoDTO = new KnEmergencyInfoDTO();
                            emergencyInfoDTO.setDestination(subsDest.getEmergDest());
                            if(subsDest.getEmergDestTypeMgmt() == DESTINATION_TYPE_MGMT.CONTACT.value()){
                                emergencyInfoDTO.setDestination(subsDest.getEmergDest());
                            } else {
                                emergencyInfoDTO.setDestination(destValue(subsMdn, String.valueOf(groupCorpIdMap.getOrDefault(Integer.parseInt(subsDest.getEmergDest()), Integer.parseInt(corpId))), subsDest.getEmergDest()));
                            }
                            emergencyInfoDTO.setDestPriority(subsDest.getEmergDestPriority());
                            updatedDestDTO.add(emergencyInfoDTO);
                        }
                    }
                    knLogger.debug(methodName, "updatedDestDTO - ", updatedDestDTO, " addedDestDTO -", addedDestDTO);
                    for(KnSubsDestEmergencyAttributes subsDest : dbExistingDestinations){
                        if (removedDestList.contains(subsDest.getEmergDest())) {
                            if(subsDest.getEmergDestTypeMgmt() == DESTINATION_TYPE_MGMT.CONTACT.value()){
                                deletedDest.add(subsDest.getEmergDest());
                            } else {
                                deletedDest.add(destValue(subsMdn, String.valueOf(groupCorpIdMap.getOrDefault(Integer.parseInt(subsDest.getEmergDest()), Integer.parseInt(corpId))), subsDest.getEmergDest()));
                            }
                        }
                    }
                    knLogger.debug(methodName, "deletedDest - ", deletedDest);
                    if(existingEmerAttributes != null){
                        if(updateEmergAttributes.getEmergInitPermission() != null && !updateEmergAttributes.getEmergInitPermission()
                                .equals(existingEmerAttributes.getEmergInitPermission())){
                            modifiedEmergAttributes.setEmergInitPerm(updateEmergAttributes.getEmergInitPermission());
                        }
                        if(updateEmergAttributes.getEmergCnclPermission() != null && !updateEmergAttributes.getEmergCnclPermission()
                                .equals(existingEmerAttributes.getEmergCnclPermission())){
                            modifiedEmergAttributes.setEmergCancelPerm(updateEmergAttributes.getEmergCnclPermission());
                        } else if(updateEmergAttributes.getEmergCnclPermission() == null){
                            removedEmergAttributes.setEmergCancelPerm(existingEmerAttributes.getEmergCnclPermission());
                        }
                        if(updateEmergAttributes.getEmergCallType() != null && !updateEmergAttributes.getEmergCallType()
                                .equals(existingEmerAttributes.getEmergCallType())){
                            modifiedEmergAttributes.setEmergCallType(updateEmergAttributes.getEmergCallType());
                        } else if(updateEmergAttributes.getEmergCallType() == null){
                            removedEmergAttributes.setEmergCallType(existingEmerAttributes.getEmergCallType());
                        }
                        if(updateEmergAttributes.getEmergOriginBitSet() != null && !updateEmergAttributes.getEmergOriginBitSet()
                                .equals(existingEmerAttributes.getEmergOriginBitSet())){
                            modifiedEmergAttributes.setEmergOrigBitSet(updateEmergAttributes.getEmergOriginBitSet());
                        } else if(updateEmergAttributes.getEmergOriginBitSet() == null){
                            modifiedEmergAttributes.setEmergOrigBitSet(DEFAULT_EMERGENCY_BIT_SET);
                        }
                        if(updateEmergAttributes.getEmergDestTypeIntf() != null && !updateEmergAttributes.getEmergDestTypeIntf()
                                .equals(existingEmerAttributes.getEmergDestTypeIntf())){
                            modifiedEmergAttributes.setEmergDestType(updateEmergAttributes.getEmergDestTypeIntf());
                        } else if(updateEmergAttributes.getEmergDestTypeIntf() == null){
                            removedEmergAttributes.setEmergDestType(existingEmerAttributes.getEmergDestTypeIntf());
                        }

                        if(Integer.parseInt(dirChgDTO.getProtoVersion()) >= PROTOCOL_VERSION_28) {
                            if (updateEmergAttributes.getEmergConfigTimer() != null && existingEmerAttributes.getEmergConfigTimer() != null &&
                                    Float.compare(Float.parseFloat(updateEmergAttributes.getEmergConfigTimer()),
                                            Float.parseFloat(existingEmerAttributes.getEmergConfigTimer())) != 0) {
                                modifiedEmergAttributes.setEmergConfigTimer(updateEmergAttributes.getEmergConfigTimer());
                            } else if (updateEmergAttributes.getEmergConfigTimer() == null) {
                                removedEmergAttributes.setEmergConfigTimer(existingEmerAttributes.getEmergConfigTimer());
                            } else if (existingEmerAttributes.getEmergConfigTimer() == null) {
                                modifiedEmergAttributes.setEmergConfigTimer(updateEmergAttributes.getEmergConfigTimer());
                            }
                        }
                    } else {
                        addedEmergAttributes.setEmergInitPerm(updateEmergAttributes.getEmergInitPermission());
                        addedEmergAttributes.setEmergCancelPerm(updateEmergAttributes.getEmergCnclPermission());
                        addedEmergAttributes.setEmergCallType(updateEmergAttributes.getEmergCallType());
                        addedEmergAttributes.setEmergOrigBitSet(updateEmergAttributes.getEmergOriginBitSet());
                        addedEmergAttributes.setEmergDestType(updateEmergAttributes.getEmergDestTypeIntf());
                        if(Integer.parseInt(dirChgDTO.getProtoVersion()) >= PROTOCOL_VERSION_28)
                            addedEmergAttributes.setEmergConfigTimer(updateEmergAttributes.getEmergConfigTimer());
                    }
                    knLogger.debug(methodName, "addedEmergAttributes: ", addedEmergAttributes, "modifiedEmergAttributes -",
                            modifiedEmergAttributes, "removedEmergAttributes -", removedEmergAttributes);
                    // If EmergencyDoc is creating first time, then we will send directoryDiff. So this will be taken
                    // care in KnXcapDiffNotify classw while preparing notification.
                    if(!directoryDiff) {
                        docChgDTO.setAddedDestList(addedDestDTO);
                        docChgDTO.setModifiedDestList(updatedDestDTO);
                        docChgDTO.setRemovedDestList(deletedDest);
                        docChgDTO.setAddedEmerAttributes(addedEmergAttributes);
                        docChgDTO.setModifiedEmerAttributes(modifiedEmergAttributes);
                        docChgDTO.setRemovedEmerAttributes(removedEmergAttributes);
                    }
                }
            });
        });
        knLogger.debug(methodName, "Exit: etagMap: ",KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }

    public static Map<String, KnOPDirChgDTO> formXcapAddlTGDiffNotification(Map<String, KnOPDirChgDTO> etagMap,
                                                                            Collection<KnCorpAddlTGInfoDTO> addedAddlTGList,
                                                                            Collection<KnCorpAddlTGInfoDTO> modifyAddlTGList,
                                                                            Collection<KnCorpAddlTGInfoDTO> deletedAddlTGList,
                                                                            Map<Integer, Integer> groupCorpIdMap) {
        String methodName = "formXcapAddlTGDiffNotification(etagMap, addedAddlTGList, modifyAddlTGList, deletedAddlTGList, corpId)";
        knLogger.debug(methodName, "Entry :  - etagMap - ", etagMap==null ? etagMap : KnGDPRTemplate.mapKeyMdn(etagMap), " - addedAddlTGList -", addedAddlTGList,
                " modifyAddlTGList -", modifyAddlTGList, " - deletedAddlTGList -", deletedAddlTGList, "groupCorpIdMap -", groupCorpIdMap);
        etagMap.forEach((mdn, dirChgDTO) -> {
            dirChgDTO.getDocChgDTO().forEach(docChgDTO -> {
                if (docChgDTO.getDocUri().contains(APP_UID_ADDL_TG_LIST)) {
                    Collection<KnXDMAddlTalkGroupInfoDTO> addAddlTGList = new ArrayList<>();
                    Collection<KnXDMAddlTalkGroupInfoDTO> modAddlTGList = new ArrayList<>();
                    Collection<KnXDMAddlTalkGroupInfoDTO> delAddTGList = new ArrayList<>();
                    KnXDMAddlTalkGroupInfoDTO addlTalkGroupInfoDTO = null;

                    if(addedAddlTGList != null && !addedAddlTGList.isEmpty()) {
                        for (KnCorpAddlTGInfoDTO addedAddlTGInfoDTO : addedAddlTGList) {
                            if(mdn.equals(addedAddlTGInfoDTO.getMdn())) {
                                if (null == groupCorpIdMap.get(addedAddlTGInfoDTO.getGroupId())) {
                                    knLogger.warn(methodName, "Corp Id is missing for Group Id - ", addedAddlTGInfoDTO.getGroupId());
                                    continue;
                                }
                                addlTalkGroupInfoDTO = new KnXDMAddlTalkGroupInfoDTO();
                                addlTalkGroupInfoDTO.setGroupId(addedAddlTGInfoDTO.getGroupId());
                                addlTalkGroupInfoDTO.setZoneId(addedAddlTGInfoDTO.getZoneId());
                                addlTalkGroupInfoDTO.setZoneName(addedAddlTGInfoDTO.getZoneName());
                                addlTalkGroupInfoDTO.setChannelId(addedAddlTGInfoDTO.getChannelId());
                                addlTalkGroupInfoDTO.setGroupUri(groupUri(addedAddlTGInfoDTO.getMdn(), groupCorpIdMap.get(addedAddlTGInfoDTO.getGroupId()), addedAddlTGInfoDTO.getGroupId()));
                                addlTalkGroupInfoDTO.setGroupType(addedAddlTGInfoDTO.getGroupType());
                                addlTalkGroupInfoDTO.setMemberCount(addedAddlTGInfoDTO.getGroupMemCount());
                                addlTalkGroupInfoDTO.setAvatar(addedAddlTGInfoDTO.getAvatar());
                                addlTalkGroupInfoDTO.setCreatedBy(addedAddlTGInfoDTO.getGroupCreatedBy());
                                addAddlTGList.add(addlTalkGroupInfoDTO);
                            }
                        }
                    }

                    if(modifyAddlTGList != null && !modifyAddlTGList.isEmpty()) {
                        List<KnCorpAddlTGInfoDTO> newList = new ArrayList<>(modifyAddlTGList);
                        Collections.sort(newList);
                        Map<Integer, KnXDMAddlTalkGroupInfoDTO> modifyMapForGroup =  new HashMap<>();
                        for (KnCorpAddlTGInfoDTO modifiedAddlTGInfoDTO : newList) {
                            if(mdn.equals(modifiedAddlTGInfoDTO.getMdn())) {
                                if (modifyMapForGroup.get(modifiedAddlTGInfoDTO.getGroupId()) != null) {
                                    addlTalkGroupInfoDTO = modifyMapForGroup.get(modifiedAddlTGInfoDTO.getGroupId());
                                    addlTalkGroupInfoDTO.setZones(addlTalkGroupInfoDTO.getZones() + COMMA + modifiedAddlTGInfoDTO.getZoneId());
                                    addlTalkGroupInfoDTO.setChannels(addlTalkGroupInfoDTO.getChannels() + COMMA + modifiedAddlTGInfoDTO.getChannelId());
                                } else {
                                    if (null == groupCorpIdMap.get(modifiedAddlTGInfoDTO.getGroupId())) {
                                        knLogger.warn(methodName, "Corp Id is missing for Group Id - ", modifiedAddlTGInfoDTO.getGroupId());
                                        continue;
                                    }
                                    addlTalkGroupInfoDTO = new KnXDMAddlTalkGroupInfoDTO();
                                    addlTalkGroupInfoDTO.setGroupId(modifiedAddlTGInfoDTO.getGroupId());
                                    addlTalkGroupInfoDTO.setZones(String.valueOf(modifiedAddlTGInfoDTO.getZoneId()));
                                    addlTalkGroupInfoDTO.setZoneName(modifiedAddlTGInfoDTO.getZoneName());
                                    addlTalkGroupInfoDTO.setChannels(String.valueOf(modifiedAddlTGInfoDTO.getChannelId()));
                                    addlTalkGroupInfoDTO.setGroupUri(groupUri(modifiedAddlTGInfoDTO.getMdn(), groupCorpIdMap.get(modifiedAddlTGInfoDTO.getGroupId()), modifiedAddlTGInfoDTO.getGroupId()));
                                    addlTalkGroupInfoDTO.setGroupType(modifiedAddlTGInfoDTO.getGroupType());
                                    addlTalkGroupInfoDTO.setMemberCount(modifiedAddlTGInfoDTO.getGroupMemCount());
                                    addlTalkGroupInfoDTO.setAvatar(modifiedAddlTGInfoDTO.getAvatar());
                                    addlTalkGroupInfoDTO.setCreatedBy(modifiedAddlTGInfoDTO.getGroupCreatedBy());
                                }
                                modifyMapForGroup.put(modifiedAddlTGInfoDTO.getGroupId(), addlTalkGroupInfoDTO);
                            }
                        }
                        if(!modifyMapForGroup.isEmpty()) modAddlTGList.addAll(modifyMapForGroup.values());
                    }
                    if(deletedAddlTGList != null && !deletedAddlTGList.isEmpty()){
                        Collection<Integer> existingGps = new ArrayList<>();
                        for (KnCorpAddlTGInfoDTO deleteAddlTGInfoDTO : deletedAddlTGList) {
                            if(mdn.equals(deleteAddlTGInfoDTO.getMdn()) && !existingGps.contains(deleteAddlTGInfoDTO.getGroupId())) {
                                if (null == groupCorpIdMap.get(deleteAddlTGInfoDTO.getGroupId())) {
                                    knLogger.warn(methodName, "Corp Id is missing for Group Id - ", deleteAddlTGInfoDTO.getGroupId());
                                    continue;
                                }
                                addlTalkGroupInfoDTO = new KnXDMAddlTalkGroupInfoDTO();
                                existingGps.add(deleteAddlTGInfoDTO.getGroupId());
                                addlTalkGroupInfoDTO.setGroupUri(groupUri(deleteAddlTGInfoDTO.getMdn(), groupCorpIdMap.get(deleteAddlTGInfoDTO.getGroupId()), deleteAddlTGInfoDTO.getGroupId()));
                                delAddTGList.add(addlTalkGroupInfoDTO);
                            }
                        }
                    }
                    knLogger.debug(methodName, "addAddlTGList - ", addAddlTGList, "modAddlTGList - ", modAddlTGList, "delAddTGList - ", delAddTGList);
                    docChgDTO.setAddedAddlTGList(addAddlTGList);
                    docChgDTO.setModifiedAddlTGList(modAddlTGList);
                    docChgDTO.setRemovedAddlTGList(delAddTGList);
                }
            });
        });
        knLogger.debug(methodName, "Exit: etagMap: ", KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }


    public static String fPermsValue(long mcpttBit) {
        String methodName = "fPermsValue(long)";
        knLogger.debug(methodName, "ENTRY: Received bit set to convert long value is - ", mcpttBit);
        String fPerm = null;
        KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
        BitSet targetDBPermsBit = featureSetUtil.convertLongToBitSet(mcpttBit);
        StringBuffer fPerms = new StringBuffer();
        //user-check permission, user-service permission, ambient-listener permission, discreet-listener permission
        fPerms.append(targetDBPermsBit.get(MCPTT_PERMISSION_BIT.USERCHECK.value()) ? 1 : 0).append(COMMA)
                .append(targetDBPermsBit.get(MCPTT_PERMISSION_BIT.USERENABLE.value()) ? 1 : 0).append(COMMA)
                .append(targetDBPermsBit.get(MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) ? 1 : 0).append(COMMA)
                .append(targetDBPermsBit.get(MCPTT_PERMISSION_BIT.DISCRETELISTENING.value()) ? 1 : 0).append(COMMA)
                .append(targetDBPermsBit.get(MCPTT_PERMISSION_BIT.REMOTEEMERGENCYPERMISSION.value()) ? 1 : 0).append(COMMA)
                .append(targetDBPermsBit.get(MCPTT_PERMISSION_BIT.MCVIDEOUNCONFIRMEDPULL.value()) ? 1 : 0);
        fPerm = fPerms.toString();
        knLogger.debug(methodName, "EXIT: Generated Long Value - ", fPerm);
        return fPerm;
    }

    public Map<String, Integer> isAuthUserMap(Map<String, KnMcpttPermissionDTO> targetMap) {
        String methodName = "isAuthUserMap(Map<String, KnMcpttPermissionDTO>)";
        knLogger.debug(methodName, " - ", targetMap.size());
        Map<String, Integer> isAuthUserMap = new HashMap<>();
        KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
        targetMap.forEach((target, value) -> {
            BitSet targetDBPermsBit = featureSetUtil.convertLongToBitSet(value.getMcpttPerms());
            isAuthUserMap.put(target, targetDBPermsBit.get(MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) ? 1 : 0);
        });
        knLogger.debug(methodName, "EXIT: ", isAuthUserMap);
        return isAuthUserMap;
    }

    public static String fstsValue(int serviceAuthStatusAU, Integer discreteEnabled) {
        String methodName = "fstsValue(int, int)";
        knLogger.debug(methodName, "ENTRY: Received value is - ", serviceAuthStatusAU, serviceAuthStatusAU);
        String fst = null;
        StringBuffer fsts = new StringBuffer();
        //user-check permission, user-service permission, ambient-listener permission, discreet-listener permission
        fsts.append(serviceAuthStatusAU == DEFAULT_AU ? 1 : 0).append(COMMA).append(discreteEnabled != null ? discreteEnabled : 0);
        fst = fsts.toString();
        knLogger.debug(methodName, "EXIT: Generated Value - ", fst);
        return fst;
    }

    public static String destValue(String mdn, String corpId, String dest) {
        String methodName = "destValue(String)";
        knLogger.debug(methodName, "ENTRY: Received value is - ", dest);
        String destination = null;
        StringBuffer destBuff = new StringBuffer();
        destBuff.append(mdn).append(SEMICOLON).append(APP_UID_CORP_GROUP).append(EQUALS).append(corpId).append(LINE_SAPERATOR).append(dest);
        destination = destBuff.toString();
        knLogger.debug(methodName, "EXIT: Generated Value - ", destination);
        return destination;
    }

    public static String groupUri(String mdn, int corpId, int groupId) {
        String methodName = "groupUri(String)";
        knLogger.debug(methodName, "ENTRY: Received value is - ", corpId, groupId);
        String grpUri = null;
        StringBuffer destBuff = new StringBuffer();
        destBuff.append(mdn).append(SEMICOLON).append(APP_UID_CORP_GROUP).append(EQUALS).append(String.valueOf(corpId))
                .append(LINE_SAPERATOR).append(String.valueOf(groupId));
        grpUri = destBuff.toString();
        knLogger.debug(methodName, "EXIT: Generated Value - ", grpUri);
        return grpUri;
    }


    /**
     * method to convert the Bit Set to Long Value
     *
     * @param bitSet BitSet
     * @return Long value
     */
    public static long convertBitSetToLong(BitSet bitSet) {
        String methodName = "convertBitSetToLong(BitSet)";
        knLogger.debug(methodName, "ENTRY: Received bit set to convert long value is - ", bitSet);

        long longValue = 0L;
        for (int i = 0; i < bitSet.size(); ++i) {
            longValue += bitSet.get(i) ? (1L << i) : 0L;
        }

        knLogger.debug(methodName, "EXIT: Generated Long Value - ", longValue);
        return longValue;
    }

    public Map<String, Integer> updateAndGetDirecEtag(Set<String> mdnList, KnPersisterTxn persisterTxn, String
            xdmsHomePttId) throws KnCorpBOException{
        final String methodName = "getAndUpdateDirectoryEtag()";
        knLogger.debug(methodName,"ENTRY");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            Map<String, Integer> updateAndGetDirecEtagMap = new HashMap<>();
            var mdnListArray = new ArrayList<>(mdnList);
            var mdnSplitList = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var mdnBatchList : mdnSplitList) {
                var updateAndGetDirecEtagRes = corpXdmDao.updateAndGetDirecEtag(new HashSet<>(mdnBatchList), persisterTxn);
                if (null != updateAndGetDirecEtagRes && !updateAndGetDirecEtagRes.isEmpty()) {
                    updateAndGetDirecEtagMap.putAll(updateAndGetDirecEtagRes);
                }
            }
            return updateAndGetDirecEtagMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured -  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    /**
     * method to generate the Subscriber config doc Sel Uri as per ICD
     *
     * @param mdn String
     * @return String sel uri
     */
    public String generateSubsConfigSelUri(String mdn) {
        String methodName = "generateSubsConfigSelUri";

        knLogger.debug(methodName, "generating sel uri for Notification for mdn - ", KnGDPRTemplate.mdn(mdn));
        StringBuffer strBuffer = new StringBuffer(150);
        strBuffer.append("xcap-directory/folder%5B@auid=%22kn-subscriber-config%22%5D/entry%5B@uri=%22kn-subscriber-config/users/tel:+");
        strBuffer.append(mdn);
        strBuffer.append("/index%22%5D/@etag");

        knLogger.debug(methodName, "generated sel uri for Notification for mdn - ", KnGDPRTemplate.mdn(mdn), " is - ", strBuffer.toString()
        );
        return strBuffer.toString();
    }

    public static Predicate<KnCorpSubscriberDTO> isThirdPartClient(int corpId) {
       return subscriberDTO -> (corpId == subscriberDTO.getCorpId() &&
               (subscriberDTO.getClientType() == SUBSCR_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()
               || subscriberDTO.getClientType() == SUBSCR_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
               || subscriberDTO.getClientType() == SUBSCR_CLIENT_TYPE.MOBILEAPI.value()));
    }

    public static Predicate<Map.Entry<String, Integer>> isThirdPartClientMap() {
        return map -> map.getValue() == SUBSCR_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()
                || map.getValue() == SUBSCR_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                || map.getValue() == SUBSCR_CLIENT_TYPE.MOBILEAPI.value();
    }

    /**
     * This method is used to form the delete authorization notification for the subscriber
     *
     * @param mdn
     * @param etagMap
     * @return
     */
    public Map<String, KnOPDirChgDTO> formMdnAuthDeleteNotification(String mdn, Map<String, KnOPDirChgDTO> etagMap) {
        String methodName = "formMdnAuthDeleteNotification";
        mdn = mdn.trim();
        KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
        Collection<KnOPDocChgDTO> documents = new ArrayList<KnOPDocChgDTO>();
        if (dirChgDTO == null) {
            dirChgDTO = new KnOPDirChgDTO();
            dirChgDTO.setDirUri(getDirectoryURI(mdn));
        } else {
            documents = dirChgDTO.getDocChgDTO();
        }
        KnOPDocChgDTO doc = new KnOPDocChgDTO();
        doc.setDocUri(getAuthDocumentURI(mdn));
        doc.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
        doc.setNewEtag("0");
        documents.add(doc);
        dirChgDTO.setDocChgDTO(documents);
        etagMap.put(mdn, dirChgDTO);
        knLogger.debug(methodName, "eTagMap for Auth ",KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }

    /**
     * This method is used to form the delete authorization notification for the subscriber
     *
     * @param mdn
     * @param etagMap
     * @return
     */
    public Map<String, KnOPDirChgDTO> formMdnEmergDeleteNotification(String mdn, Map<String, KnOPDirChgDTO> etagMap) {
        String methodName = "formMdnEmergDeleteNotification";
        mdn = mdn.trim();
        KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
        Collection<KnOPDocChgDTO> documents = new ArrayList<KnOPDocChgDTO>();
        if (dirChgDTO == null) {
            dirChgDTO = new KnOPDirChgDTO();
            dirChgDTO.setDirUri(getDirectoryURI(mdn));
        } else {
            documents = dirChgDTO.getDocChgDTO();
        }
        KnOPDocChgDTO doc = new KnOPDocChgDTO();
        doc.setDocUri(getEmergDocumentURI(mdn));
        doc.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
        doc.setNewEtag("0");
        documents.add(doc);
        dirChgDTO.setDocChgDTO(documents);
        etagMap.put(mdn, dirChgDTO);
        knLogger.debug(methodName, "eTagMap for Emerg ",KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }

    /**
     * This method is used to form the delete Additional TG List notification for the subscriber
     *
     * @param mdn
     * @param etagMap
     * @return
     */
    public Map<String, KnOPDirChgDTO> formMdnAddlTGListDeleteNotification(String mdn, Map<String, KnOPDirChgDTO> etagMap) {
        String methodName = "formMdnAddlTGListDeleteNotification";
        mdn = mdn.trim();
        KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
        Collection<KnOPDocChgDTO> documents = new ArrayList<KnOPDocChgDTO>();
        if (dirChgDTO == null) {
            dirChgDTO = new KnOPDirChgDTO();
            dirChgDTO.setDirUri(getDirectoryURI(mdn));
        } else {
            documents = dirChgDTO.getDocChgDTO();
        }
        KnOPDocChgDTO doc = new KnOPDocChgDTO();
        doc.setDocUri(getAddlTGDocumentURI(mdn));
        doc.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
        doc.setNewEtag("0");
        documents.add(doc);
        dirChgDTO.setDocChgDTO(documents);
        etagMap.put(mdn, dirChgDTO);
        knLogger.debug(methodName, "eTagMap for Emerg ",KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }

    public static Map<String, Collection<String>> getActualDeletedContacts(LinkedHashMap<String, LinkedList<String>> deletedContacts,
                                                                           LinkedHashMap<String, LinkedList<Integer>> delContactStatus) {
        String methodName = "getActualDeletedContacts";
        knLogger.debug(methodName, "deletedContacts-- ", deletedContacts, "delContactStatus --", delContactStatus);
        Map<String, Collection<String>> actualDeletedContact = new HashMap<>();
        if (deletedContacts != null && !deletedContacts.isEmpty()) {
            deletedContacts.forEach((mdn, removedMembers) -> {
                if (delContactStatus != null) {
                    LinkedList<Integer> delStatus = delContactStatus.get(mdn);
                    List<String> actualDelMemList = new ArrayList<String>();
                    if (delStatus != null) {
                        Iterator ite = removedMembers.iterator();
                        for (Integer result : delStatus) {
                            String memberMdn = (String) ite.next();
                            if (!memberMdn.equals(mdn)) {
                                if (result > 0) {
                                    actualDelMemList.add(memberMdn);
                                }
                            }
                        }
                        actualDeletedContact.put(mdn, actualDelMemList);
                    }
                }
            });
        }
        knLogger.debug(methodName, "actualDeletedContact-- ", actualDeletedContact);
        return actualDeletedContact;
    }

    public Map<String, KnCorpGroupMemberDTO> trancateMembers(Map<String, KnCorpGroupMemberDTO> grpMemberMap, Map<String, KnSubscriberDTO>
            groupMemberNameMap, int count, String ownerMdn) {
        String methodName = "trancateMembers()";
        knLogger.debug(methodName, "grpMemberMap -- ", grpMemberMap, "count --", count);
        Map<String, KnCorpGroupMemberDTO> newMap = new HashMap<>(count);
        newMap.put(ownerMdn, grpMemberMap.get(ownerMdn));
        grpMemberMap.remove(ownerMdn);
        for (Map.Entry<String, KnSubscriberDTO> entry : groupMemberNameMap.entrySet()) {
            KnSubscriberDTO value = entry.getValue();
            String key = entry.getKey();
            if (value.getClientType() == SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value() ||
                    value.getClientType() == SUBS_CLIENT_TYPE.GROUPMDN.value() ||
                    value.getClientType() == SUBS_CLIENT_TYPE.SGMDNPATCH.value()) {
                if(grpMemberMap.get(key) != null) newMap.put(key, grpMemberMap.get(key));
                grpMemberMap.remove(key);
            }
        }
        int index = newMap.size();
        for (String key : grpMemberMap.keySet()) {
            if (index >= count)
                break;
            newMap.put(key, grpMemberMap.get(key));
            index++;
        }
        knLogger.info(methodName, "newMap count ", newMap.size());
        return newMap;
    }

    /**
     * Method to form TGSS Notification
     * @param corpInOutParamDTO
     * @param etagMap
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */

    public Map<String, KnOPDirChgDTO> formTGSSDocumentNotification(KnCorpInOutParamDTO corpInOutParamDTO,Map<String, KnOPDirChgDTO> etagMap, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "formTGSSDocumentNotification";
        try {
            Map<String, Integer> mdnTGSSEtag = corpInOutParamDTO.getMdnTgssEtag();
            knLogger.debug(methodName, "mdnTGSSEtag", mdnTGSSEtag);
            Map<String, Boolean> tgssMdnEtagMap = new HashMap<String, Boolean>();
            Set<String> mdnList = new HashSet<String>();
            if (mdnTGSSEtag != null) {
                mdnList = new HashSet<String>(mdnTGSSEtag.keySet());
            }
            //get the tgss feature bit for the subsc fetature bit
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            tgssMdnEtagMap = xdmDAO.getTgssFeatureBit(mdnList, persisterTxn);
            if (mdnTGSSEtag != null) {
                for (String mdn : mdnTGSSEtag.keySet()) {
                    if (tgssMdnEtagMap.get(mdn)) {
                        KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
                        Collection<KnOPDocChgDTO> documents = new ArrayList<KnOPDocChgDTO>();
                        if (dirChgDTO == null) {
                            dirChgDTO = new KnOPDirChgDTO();
                            dirChgDTO.setDirUri(getDirectoryURI(mdn));
                        } else {
                            documents = dirChgDTO.getDocChgDTO();
                        }
                        KnOPDocChgDTO doc = new KnOPDocChgDTO();
                        String docURI = "xcap-directory/folder%5B@auid=%22kn-tgss-list%22%5D/entry%5B@uri=%22kn-tgss-list/users/tel:+";
                        docURI = docURI.concat(mdn);
                        docURI = docURI.concat("/index%22%5D/@etag");
                        doc.setDocUri(docURI);
                        doc.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                        doc.setNewEtag(String.valueOf(mdnTGSSEtag.get(mdn)));
                        documents.add(doc);
                        dirChgDTO.setDocChgDTO(documents);
                        etagMap.put(mdn, dirChgDTO);
                    }

                }
            }

            return etagMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while forming the tgss notification - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public String mcpttGroupUri(int groupId, int corpId, String sipProxyUri){
        StringBuilder builder = new StringBuilder();
        builder.append(SIP).append(corpId)
                .append(DOT).append(groupId)
                .append(AT).append(sipProxyUri);
        return builder.toString();
    }

    public Collection<KnSIPProxySvcConfigDTO> getSipProxySvcConfig(String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getSipProxySvcConfig(xdmsHome, false, persisterTxn);
    }

    /**
     * This is read only method.
     */
    public Collection<KnSIPProxySvcConfigDTO> getSipProxySvcConfig(String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSipProxySvcConfig(String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getSipProxySvcConfig(readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting SIP Proxy list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnAPNConfigDTO> getAPNProfileConfig(String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getAPNProfileConfig(String, KnPersisterTxn)";
        knLogger.debug(methodName);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            return xdmDAO.getAPNProfileConfig(persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting APN Profile List - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public static <T> T jsonToObject(String str, Class<T> clazz) {
        String methodName = "jsonToObject()";
        T obj = null;
        try {
            knLogger.debug(methodName, "result json: ", str);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(str);
            //Checking if _id is present if yes then replacing it with id
            if (jsonNode.has("_id")) {
                String idValue = jsonNode.get("_id").asText();
                ((ObjectNode) jsonNode).remove("_id");
                ((ObjectNode) jsonNode).put("id", idValue);
                knLogger.debug(methodName, "_id field found and replaced with id: ", idValue);
            }
            str = objectMapper.writeValueAsString(jsonNode);
            obj = objectMapper
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(str, clazz);
        } catch (IOException ex) {
            knLogger.error(methodName, "Json Str: {}", str);
            knLogger.error(methodName, "Exception occurred: ", ex.getMessage());
        }
        return obj;
    }

    public static String ObjToJson(Object obj) {
        String methodName="ObjToJson()";
        String mapAsJson = null;
        try {
            mapAsJson = new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            knLogger.error(methodName,"Exception- ",ex.getMessage());
        }
        return mapAsJson;
    }

    public static String getUUID() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String methodName="getUUID()";
        MessageDigest salt = MessageDigest.getInstance("SHA-256");
        salt.update(UUID.randomUUID().toString().getBytes("UTF-8"));
        String digest = bytesToHex(salt.digest());
        return digest;
    }

    public static String getUUID32() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String methodName = "getUUID32()";
        MessageDigest salt = MessageDigest.getInstance("MD5");
        salt.update(UUID.randomUUID().toString().getBytes("UTF-8"));
        String digest = bytesToHex(salt.digest());
        return digest;
    }


    private static String bytesToHex(byte[] hashInBytes) {
        String methodName="bytesToHex()";
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }

    public static KnCorpModifyUserProfileDTO modifyUpmTransform(KnXDMCorpUserProfileRequestDTO userProfileRequestDTO) {
        final String methodName = "modifyUpmTransform";

        Function<KnXDMUserProfileInfoDTO, KnCorpModifyUserProfileDTO> transformModifyUPM = new Function<KnXDMUserProfileInfoDTO, KnCorpModifyUserProfileDTO>() {
            public KnCorpModifyUserProfileDTO apply(KnXDMUserProfileInfoDTO type) {
                KnCorpModifyUserProfileDTO modifyUpm = new KnCorpModifyUserProfileDTO();
                KnSubsEmergencyConfigDTO emergencyConfig = new KnSubsEmergencyConfigDTO();
                Set<KnDestinationAttributeDTO> destAttributes = new HashSet<>();
                Set<KnCorpGroupListInfoDTO> addedGroupListInfo = new HashSet<>();
                Set<KnCorpGroupListInfoDTO> modifiedGroupListInfo = new HashSet<>();
                Set<String> removedGroupIds = new HashSet<>();
                //modifyUpm.setCorporateID(Integer.valueOf(userProfileRequestDTO.getCorpId()));
                //modifyUpm.set_id(type.getProfileId());
                if (type.getProfileName() != null)
                    modifyUpm.setUserProfileName(type.getProfileName());
                if (type.getContactListId() != null && !type.getContactListId().isEmpty()) {
                    modifyUpm.setContactListID(Integer.valueOf(type.getContactListId()));
                } else if (type.getContactListId() != null && type.getContactListId().isEmpty()) {
                    modifyUpm.setContactListID(-1);
                }
                modifyUpm.setTgscMode(type.getTgscMode());
                if (null != type.getUserProfileFS() && null != type.getUserProfileFS().getSelfDnDPrivilege()) {
                    KnUserProfileFSDTO userProfileFs = new KnUserProfileFSDTO();
                    userProfileFs.setSelfDnDPrivilege(type.getUserProfileFS().getSelfDnDPrivilege());
                    modifyUpm.setUserProfileFSDto(userProfileFs);
                }
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
                    modifyUpm.setEmergencyConfig(emergencyConfig);
                }

                if (type.getAddedGroupListInfo() != null && !type.getAddedGroupListInfo().isEmpty()) {
                    for (KnXDMGroupListInfoDTO groups : type.getAddedGroupListInfo()) {
                        KnXDMGroupMdnInfoDTO memberPr = groups.getGroupMemProp();
                        addedGroupListInfo.add(new KnCorpGroupListInfoDTO(Integer.valueOf(groups.getGroupId())
                                ,groups.getZoneId()!=null?Integer.valueOf(groups.getZoneId()):null
                                ,groups.getChannelId()!=null?Integer.valueOf(groups.getChannelId()):null
                                ,groups.getPriority()!=null?Integer.valueOf(groups.getPriority()):null,
                                new com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupContactDTO(memberPr.getSupervisor(), memberPr.getBroadcaster()
                                        , memberPr.getLocWatcher(), memberPr.getIsOSMAuthorize(),
                                        memberPr.getCallInitiatePermission(),
                                        memberPr.getCallReceivePermission(), memberPr.getInCallPermission(),memberPr.getVideoCallInitiatePermission(),
                                        memberPr.getVideoCallReceivePermission(),memberPr.getVideoInCallPermission())
                        ));
                    }
                    modifyUpm.setAddedGroupList(addedGroupListInfo);
                }
                if (type.getModifiedGroupListInfo() != null && !type.getModifiedGroupListInfo().isEmpty()) {
                    for (KnXDMGroupListInfoDTO groups : type.getModifiedGroupListInfo()) {
                        KnXDMGroupMdnInfoDTO memberPr = groups.getGroupMemProp();
                        modifiedGroupListInfo.add(new KnCorpGroupListInfoDTO(Integer.valueOf(groups.getGroupId())
                                ,groups.getZoneId()!=null?Integer.valueOf(groups.getZoneId()):null
                                ,groups.getChannelId()!=null?Integer.valueOf(groups.getChannelId()):null
                                ,groups.getPriority()!=null?Integer.valueOf(groups.getPriority()):null,
                                new com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupContactDTO(memberPr.getSupervisor(), memberPr.getBroadcaster()
                                        , memberPr.getLocWatcher(), memberPr.getIsOSMAuthorize(),
                                        memberPr.getCallInitiatePermission(),
                                        memberPr.getCallReceivePermission(), memberPr.getInCallPermission(),memberPr.getVideoCallInitiatePermission(),
                                        memberPr.getVideoCallReceivePermission(),memberPr.getVideoInCallPermission())
                        ));
                    }
                    modifyUpm.setModifiedGroupList(modifiedGroupListInfo);
                }
                if (type.getRemovedGroupIds() != null && !type.getRemovedGroupIds().isEmpty()) {
                    for (String groupIds : type.getRemovedGroupIds()) {
                        removedGroupIds.add(groupIds);
                    }
                    modifyUpm.setRemovedGroupIdsList(removedGroupIds);
                }
                if(type.getSharingEnabled() != null){
                    modifyUpm.setUserProfileSharingEnabled(type.getSharingEnabled());
                }
                if(type.getAddUserProfileSharedCorpList() != null){
                    modifyUpm.setAddUserProfileSharedCorpList(type.getAddUserProfileSharedCorpList());
                }
                if(type.getRemoveUserProfileSharedCorpList() != null){
                    modifyUpm.setRemoveUserProfileSharedCorpList(type.getRemoveUserProfileSharedCorpList());
                }

                //if (type.getUserProfileFS() != null)
                 //   modifyUpm.setFeatureBS(type.getUserProfileFS());

                Collection<KnTargetMdnPermissionBitInfo> addedtargetMdnPermissionBitInfos = type.getAddedMdnPerms();
                Function<Collection<KnTargetMdnPermissionBitInfo>, Set<KnCorpUserProfileMCPTTConfig>> addedTransformToPermBitSet
                        = new Function<Collection<KnTargetMdnPermissionBitInfo>, Set<KnCorpUserProfileMCPTTConfig>>() {
                    @Override
                    public Set<KnCorpUserProfileMCPTTConfig> apply(Collection<KnTargetMdnPermissionBitInfo> knTargetMdnPermBitInfos) {
                        Set<KnCorpUserProfileMCPTTConfig> corpUserProfileMCPTTConfigSet = new HashSet<>();
                        if (null != addedtargetMdnPermissionBitInfos) {
                            addedtargetMdnPermissionBitInfos.forEach(targetMdnPermissionBitInfo -> {
                                BitSet bitSet = new BitSet();
                                bitSet.set(AMBIENTLISTENING.value(), targetMdnPermissionBitInfo.getAmbientListening() == 1);
                                bitSet.set(DISCRETELISTENING.value(), targetMdnPermissionBitInfo.getDiscreteListening() == 1);
                                bitSet.set(USERCHECK.value(), targetMdnPermissionBitInfo.getUserCheck() == 1);
                                bitSet.set(USERENABLE.value(), targetMdnPermissionBitInfo.getUserEnable() == 1);
                                bitSet.set(REMOTEEMERGENCYPERMISSION.value(), targetMdnPermissionBitInfo.getEmergPermission() == 1);
                                bitSet.set(MCVIDEOUNCONFIRMEDPULL.value(), targetMdnPermissionBitInfo.getMcVideoUnConfirmedPull() == 1);

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
                modifyUpm.setAddedMcpttPermissionsConfig(addedTransformToPermBitSet.apply(addedtargetMdnPermissionBitInfos));

                Collection<KnTargetMdnPermissionBitInfo> modifiedTargetMdnPermissionBitInfos = type.getModifiedMdnPerms();
                Function<Collection<KnTargetMdnPermissionBitInfo>, Set<KnCorpUserProfileMCPTTConfig>> modifiedTransformToPermBitSet
                        = new Function<Collection<KnTargetMdnPermissionBitInfo>, Set<KnCorpUserProfileMCPTTConfig>>() {
                    @Override
                    public Set<KnCorpUserProfileMCPTTConfig> apply(Collection<KnTargetMdnPermissionBitInfo> knTargetMdnPermBitInfos) {
                        Set<KnCorpUserProfileMCPTTConfig> corpUserProfileMCPTTConfigSet = new HashSet<>();
                        if (null != modifiedTargetMdnPermissionBitInfos) {
                            modifiedTargetMdnPermissionBitInfos.forEach(targetMdnPermissionBitInfo -> {
                                BitSet bitSet = new BitSet();
                                bitSet.set(AMBIENTLISTENING.value(), targetMdnPermissionBitInfo.getAmbientListening() == 1);
                                bitSet.set(DISCRETELISTENING.value(), targetMdnPermissionBitInfo.getDiscreteListening() == 1);
                                bitSet.set(USERCHECK.value(), targetMdnPermissionBitInfo.getUserCheck() == 1);
                                bitSet.set(USERENABLE.value(), targetMdnPermissionBitInfo.getUserEnable() == 1);
                                bitSet.set(REMOTEEMERGENCYPERMISSION.value(), targetMdnPermissionBitInfo.getEmergPermission() == 1);
                                bitSet.set(MCVIDEOUNCONFIRMEDPULL.value(), targetMdnPermissionBitInfo.getMcVideoUnConfirmedPull() == 1);

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
                modifyUpm.setModifiedPermissionsConfig(modifiedTransformToPermBitSet.apply(modifiedTargetMdnPermissionBitInfos));

                Collection<KnTargetMdnPermissionBitInfo> removedTargetMdnPermissionBitInfos = type.getRemovedMdnPerms();
                Function<Collection<KnTargetMdnPermissionBitInfo>, Set<KnCorpUserProfileMCPTTConfig>> removedTransformToPermBitSet
                        = new Function<Collection<KnTargetMdnPermissionBitInfo>, Set<KnCorpUserProfileMCPTTConfig>>() {
                    @Override
                    public Set<KnCorpUserProfileMCPTTConfig> apply(Collection<KnTargetMdnPermissionBitInfo> knTargetMdnPermBitInfos) {
                        Set<KnCorpUserProfileMCPTTConfig> corpUserProfileMCPTTConfigSet = new HashSet<>();
                        if (null != removedTargetMdnPermissionBitInfos) {
                            removedTargetMdnPermissionBitInfos.forEach(targetMdnPermissionBitInfo -> {
                                KnCorpUserProfileMCPTTConfig userProfileMCPTTConfig = new KnCorpUserProfileMCPTTConfig();
                                userProfileMCPTTConfig.setMdn(targetMdnPermissionBitInfo.getMdn());
                                //Setting the permbitset value as 0 to remove the targets when the sublist is removed from UserProfile
                                userProfileMCPTTConfig.setPermBitSet(0);
                                corpUserProfileMCPTTConfigSet.add(userProfileMCPTTConfig);

                            });
                            knLogger.debug(methodName, "userProfileMCPTTConfig-", corpUserProfileMCPTTConfigSet);
                        }
                        return corpUserProfileMCPTTConfigSet;
                    }
                };
                modifyUpm.setRemovedMcpttPermissionsConfig(removedTransformToPermBitSet.apply(removedTargetMdnPermissionBitInfos));
                if (null != type.getSharingEnabled()) {
                    modifyUpm.setSharingEnabled(Boolean.valueOf(type.getSharingEnabled()));
                }
                return modifyUpm;
            }
        };
        knLogger.debug(methodName, "userProfileRequestDTO.getSharingEnabled() " , userProfileRequestDTO.getSharingEnabled() , " userProfileRequestDTO.getUserProfileInfo()" ,
                userProfileRequestDTO.getUserProfileInfo().getSharingEnabled());
        if (null != userProfileRequestDTO.getSharingEnabled()) {
            userProfileRequestDTO.getUserProfileInfo().setSharingEnabled(userProfileRequestDTO.getSharingEnabled());
        }
        return transformModifyUPM.apply(userProfileRequestDTO.getUserProfileInfo());
    }

    public KnPocSubsAddlInfoDTO getSubsAddlDetails(String mdn, KnPersisterTxn persisterTxn) {
        String methodName = "getSubsAddlDetails(String, persisterTxn)";
        KnPocSubsAddlInfoDTO pocSubsAddlInfoDTO = null;
        try {
            pocSubsAddlInfoDTO = pubProfileUtil.getSubsAddlDetails(mdn,persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "Failed to retrieve user profile addl details.");
        }
        return pocSubsAddlInfoDTO;
    }

    public static List<KnDocChangeListDTO> buildMCSDOC(String mcId
            ,String mdn,String upmIndex,String newEtag,String previousEtag,int mcsCompliance,String exists) {
        List<KnDocChangeListDTO> docList = new ArrayList<>();
        if (mcsCompliance == 0) {
            //for kodiak clients
            for (Map.Entry auid : auidMap.entrySet()) {
                StringBuilder mcsDocUri = new StringBuilder();
                mcsDocUri.append(auid.getValue())
                        .append("/users/")
                        .append(mcId)
                        .append("/")
                        .append(auid.getKey())
                        .append("-user-profile-")
                        .append(upmIndex)
                        .append(".xml");
                if(exists != null && exists.equals("0")){
                    docList.add(new KnDocChangeListDTO(mdn, mcsDocUri.toString(), null, null,"0"));
                }else{
                    docList.add(new KnDocChangeListDTO(mdn, mcsDocUri.toString(), newEtag, previousEtag,null));
                }
            }
        } else {
            for (Map.Entry auid : auidMap.entrySet()) {
                StringBuilder mcsDocUri = new StringBuilder();
                mcsDocUri.append(auid.getValue())
                        .append("/users/")
                        .append(mcId)
                        .append("/user-profile-")
                        .append(upmIndex)
                        .append(".xml");
                if(exists != null && exists.equals("0")){
                    docList.add(new KnDocChangeListDTO(mdn, mcsDocUri.toString(), null, null,"0"));
                }else{
                    docList.add(new KnDocChangeListDTO(mdn, mcsDocUri.toString(), newEtag, previousEtag,null));
                }
            }
        }
        return docList;
    }

    public static LinkedList<KnLIEventDTO> populateBulkDeleteGroupLIData(Map<String, KnOPDirChgDTO> eTags, String
            xdmsHome, Map<String, List<Integer>> subscriberGrpListMap, Map<Integer, KnCorpGroupDTO> allGroupMap) {
        knLogger.debug("populateBulkDeleteGroupLIData");
        LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();
        if (null != eTags) {
            List<String> targetMdnList = KnLIEventHandler.getTagetMdnList(new ArrayList<>(eTags.keySet()));
            for (String targetMdn : targetMdnList) {
                List<Integer> groups = subscriberGrpListMap.get(targetMdn);
                if (groups != null) {
                    for (int grpId : groups) {
                        KnLIEventDTO liEventDTO = new KnLIEventDTO();
                        liEventDTO.setMdn(targetMdn);
                        liEventDTO.setAction(KnLIConstants.DELETE_GROUP_ACTION);
                        liEventDTO.setGroupURI("tel:+" + targetMdn + ";kn-corp-groups=" + grpId + "_" + allGroupMap.get(grpId).getGroupDisplayName());
                        liEventDTO.setDocumentType(2);
                        liEventDTO.setPttServerId(xdmsHome);
                        liEventDTO.setErrorCode("00000");
                        liEventList.add(liEventDTO);
                    }
                }
            }
        }
        return liEventList;
    }

    public Map<String, Integer> getCorpIdMap(Set<String> extCorpIds, Set<Integer> corpIds, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getCorpIdMap(extCorpIds, corpIds, false, persisterTxn);
    }

    /**
     * Method to retrieve corpId Map. This is read only method.
     * @param extCorpIds
     * @param corpIds
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<String, Integer> getCorpIdMap(Set<String> extCorpIds, Set<Integer> corpIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getCorpIdMap(Set<String>,Set<Integer>,boolean readOnly, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY - readOnly:", readOnly);
        Map<String, Integer> map = new HashMap<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            if (extCorpIds != null && !extCorpIds.isEmpty()) {
                map = xdmDAO.getCorpIdMapByExtCorpIds(extCorpIds, readOnly, persisterTxn);
            } else if (corpIds != null && !corpIds.isEmpty()) {
                map = xdmDAO.getCorpIdMap(corpIds, readOnly, persisterTxn);
            }
            return map;
        } catch (KnBOException e) {
            knLogger.error(methodName, "KnBOException occured while retrieving corpId", e);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage(), e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving corpId", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String,String> calculateActiveFS2WithUPMFS(int subscriptionType,String pocPttId,
                                                          String presencePttId,String xdmsPttId,String clientFS2
            , String subsFS2, String corpFS2, String opsFS2, String clientCapOverrideBitMask,
                                                          Map<String, KnCorpSubscriberDTO> mdnUpmFsMap) {
        String methodName = "calculateActiveFS2WithUPMFS()";
        String activeFS2 = null;
        Map<String, String> mdnActiveFsMap = new HashMap<>();
        int CORPORATE_TYPE_SUBS = 1;
        knLogger.debug(methodName, "mdnUpmFsMap :", mdnUpmFsMap, " subscriptionType ", subscriptionType, "pocPttId ",
                pocPttId);

        try {
            for (Entry mdnFsMap : mdnUpmFsMap.entrySet()) {
                KnCorpSubscriberDTO value = (KnCorpSubscriberDTO) mdnFsMap.getValue();
                if (subscriptionType == CORPORATE_TYPE_SUBS) {
                    activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2,
                            corpFS2, opsFS2, value.getCorpAdminFS2(), clientCapOverrideBitMask, value.getXdmsFs2(),
                            value.getUserProfileFS2());
                } else {
                    activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2, opsFS2,
                            clientCapOverrideBitMask, value.getXdmsFs2(), value.getUserProfileFS2());
                }
                mdnActiveFsMap.put(mdnFsMap.getKey().toString(), activeFS2);
            }

        } catch (KnFeatureSetException e) {
            knLogger.error(methodName, "Exception occured :", e.getMessage());
        }
        knLogger.debug(methodName, " mdnActiveFsMap : ", mdnActiveFsMap);
        return mdnActiveFsMap;
    }

    public Map<Integer, Map<Integer, KnExtProfileDetails>> getBulkExtProfileDetails(HashMap<Integer, List<Integer>> allProfileIdListGroupMap,
                                                                                    String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        Map<Integer, Map<Integer, KnExtProfileDetails>> extProfileDetailsMap = new HashMap<>();
        for (var each : allProfileIdListGroupMap.entrySet()) {
            var profileId = each.getValue();

            var extProfileDetail = getExtProfileDetails(profileId, xdmsHome, persisterTxn);
            extProfileDetailsMap.put(each.getKey(), extProfileDetail);
        }
        return extProfileDetailsMap;
    }

    public boolean isBitPositionSet(int cloningBitSet, int position) {
        return (cloningBitSet & (1 << position)) != 0;
    }

    public enum TXN_STATE {
        NEW, STARTED, COMMITED, ROLLBACK, COMMIT_FAIL, ROLLBACK_FAIL
    }

    /**
     * Utility method to populate modifyCorpGroupResponse from deleGroupResponse
     * @param delGroupResponseDTO
     * @param modifyGrpResponseDTO
     */
    public static void populateXdmResponseFroomDelGrop(KnCorpResponseDTO delGroupResponseDTO, KnCorpResponseDTO modifyGrpResponseDTO) {
        if (delGroupResponseDTO != null) {
            int status = delGroupResponseDTO.getStatus();
            modifyGrpResponseDTO.setStatus(status);
            modifyGrpResponseDTO.setStatusCode(delGroupResponseDTO.getStatusCode());
            modifyGrpResponseDTO.setMessage(delGroupResponseDTO.getMessage());
            /////- populate response info //hookResponseDTO.getCustomParamMap();
            Collection<KnCorpFailedData> failedDataList = delGroupResponseDTO.getFailedDataList();
            if (status != com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.SUCCESS.value() || failedDataList != null && failedDataList.isEmpty()) {
                modifyGrpResponseDTO.setFailedDataList(failedDataList);
            }
        }else {
            populate(modifyGrpResponseDTO);
        }
    }

    public static String getBaseMdn(String reqestingMdn,Map<String, List<String>> profileMdnOfBaseMap) {
        final String methodName="getBaseMdn()";
        knLogger.debug(methodName," reqestingMdn :",KnGDPRTemplate.mdn(reqestingMdn));
        String baseMdn=null;
        //getting base mdn for profile mdn
        for(Map.Entry<String, List<String>> baseMap:profileMdnOfBaseMap.entrySet()){
            if(baseMap.getKey().equals(reqestingMdn)) {
                baseMdn=baseMap.getKey();
            }else if(baseMap.getValue().contains(reqestingMdn)) {
                baseMdn=baseMap.getKey();
            }
        }
        knLogger.debug(methodName," baseMdn :",KnGDPRTemplate.mdn(baseMdn));
        return baseMdn;
    }

    private static Collection<String> getBaseMdnList(Collection<String> reqestingMdnList,Map<String, List<String>> profileMdnOfBaseMap) {
        final String methodName="getBaseMdnList()";
        knLogger.debug(methodName," reqestingMdnList :",KnGDPRTemplate.mdnList(reqestingMdnList));
        Collection<String> baseForProfileMdn=null;
        //getting base mdn for profile mdn
        if(reqestingMdnList!=null){
            baseForProfileMdn=new HashSet<>();
            for(Map.Entry<String, List<String>> baseMap:profileMdnOfBaseMap.entrySet()){
                for(String reqestingMdn:reqestingMdnList) {
                    if(baseMap.getKey().equals(reqestingMdn)) {
                        baseForProfileMdn.add(baseMap.getKey());
                    }else if(baseMap.getValue().contains(reqestingMdn)) {
                        baseForProfileMdn.add(baseMap.getKey());
                    }
                }
            }
        }
        knLogger.debug(methodName," baseMdns",KnGDPRTemplate.mdnList(baseForProfileMdn));
        return baseForProfileMdn;
    }

    private static Map<String, KnOPDirChgDTO> convertEtagTOBaseMdn(Map<String, KnOPDirChgDTO> eTags,
                                                                  Map<String, List<String>> profileMdnOfBaseMap){
       String methodname= "convertEtagTOBaseMdn";
        knLogger.debug(methodname,"etags :",eTags,profileMdnOfBaseMap);
        Map<String, KnOPDirChgDTO> baseMdneTags=new HashMap<>();
        for(Map.Entry<String, KnOPDirChgDTO> dir:eTags.entrySet()){
            baseMdneTags.put(getBaseMdn(dir.getKey(),profileMdnOfBaseMap),dir.getValue());
        }
        knLogger.debug(methodname,baseMdneTags);
        return baseMdneTags;
    }

    public static LinkedList<KnLIEventDTO> upmContactPopulateLIData(String mdn
            ,Collection<String> deletedMembers,String xdmsHomePttId
            ,KnPersisterTxn persisterTxn) {
        final String methodName="upmContactPopulateLIData()";
        knLogger.debug(methodName," mdn ",KnGDPRTemplate.mdn(mdn)," deletedMembers ",KnGDPRTemplate.mdnList(deletedMembers));
        ArrayList<String> realMdn = new ArrayList<>();
        realMdn.add(mdn);
        String realTargetMdn=null;
        List<String> realMdns = null;
        try {
            realMdns = groupInfoUtil.getRealMdns(realMdn, xdmsHomePttId, persisterTxn);
            realTargetMdn=realMdns.get(0);
        } catch (Exception e) {
            knLogger.error(methodName," ERROR :",e.getMessage());
        }
        LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();
        boolean exists = KnLIEventHandler.isTargetMDN(realTargetMdn);
        if (exists) {
            List<String> targetMdnList = new ArrayList<>();
            targetMdnList.add(realTargetMdn);
            Map<String, String> mdnMcpttMap =new HashMap<>();
            try{
                mdnMcpttMap = KnCorpSubsProvInfoUtil.getMdnMcpttIdMap(targetMdnList, persisterTxn, xdmsHomePttId);
            } catch (KnCorpBOException e) {
                knLogger.error(methodName," getMdnMcpttIdMap error",e);
            }
            KnLIEventDTO liEventDTO = new KnLIEventDTO();
            liEventDTO.setMdn(realTargetMdn);
            liEventDTO.setMcpttId(mdnMcpttMap.get(realTargetMdn));
            liEventDTO.setDeletedMembers(deletedMembers);
            short contact = 1;
            liEventDTO.setDocumentType(contact);
            liEventDTO.setPttServerId(xdmsHomePttId);
            liEventDTO.setAction(KnLIConstants.MODIFY_CONTACTS);
            liEventDTO.setErrorCode(KnLIConstants.SUCCESS_CODE);
            liEventList.add(liEventDTO);
        }
        knLogger.debug(methodName," Exit liEventList ",liEventList);
        return liEventList;
    }

    private static Map<String, KnOPDirChgDTO> removeProfileMdnEtags(List<String> targetMdnList
            , Map<String, List<String>> profileMdnOfBaseMap
            , Map<String, KnOPDirChgDTO> eTags) {
        final String methodName="removeProfileMdnEtags()";
        List<String> profileMdnList = new ArrayList<>();
        for (String mdn : targetMdnList) {
            if (profileMdnOfBaseMap.get(mdn) != null) {
                profileMdnList.addAll(profileMdnOfBaseMap.get(mdn));
            }
        }
        eTags.keySet().removeAll(profileMdnList);
        knLogger.debug(methodName," eTags :",eTags);
        return eTags;
    }

    private static Map<String, KnOPDirChgDTO> profileWithNoBaseInGroup(List<String> targetMdnList
            , Map<Integer, Collection<String>> groupMembersMap
            , Map<String, KnOPDirChgDTO> baseMdnETags) {
        final String methodName = "profileWithNoBaseInGroup()";
        Map<String, KnOPDirChgDTO> baseETags = new HashMap<>();
        List<String> baseNotInGroup = new ArrayList<>();
        if(groupMembersMap!=null) {
            for (String GM : targetMdnList) {
                for (Collection<String> val : groupMembersMap.values()) {
                    if (!val.contains(GM)) {
                        baseNotInGroup.add(GM);
                    }
                }
            }
        }
        for(String notIngroupBase:baseNotInGroup){
            baseETags.put(notIngroupBase, baseMdnETags.get(notIngroupBase));
        }
        knLogger.debug(methodName, " baseETags :", baseETags);
        return baseETags;
    }

    private static Map<String, KnOPDirChgDTO> profileWithNoBaseInBGCGroup(List<String> targetMdnList
            , List<String> allMembers
            , Map<String, KnOPDirChgDTO> baseMdnETags) {
        final String methodName = "profileWithNoBaseInBGCGroup()";
        Map<String, KnOPDirChgDTO> baseETags = new HashMap<>();
        List<String> baseNotInGroup = new ArrayList<>();
        for (String GM : targetMdnList) {
            for (String val : allMembers) {
                if (!val.contains(GM)) {
                    baseNotInGroup.add(GM);
                }
            }
        }
        for(String notIngroupBase:baseNotInGroup){
            baseETags.put(notIngroupBase, baseMdnETags.get(notIngroupBase));
        }
        knLogger.debug(methodName, " baseETags :", baseETags);
        return baseETags;
    }

    private static void removedBaseMdnForProfileMdn(Collection<Collection<String>> dbGrpMdn
            , Map<String, KnOPDirChgDTO> eTags
            , Map<String, List<String>> profileMdnOfBaseMap) {
        final String methodName = "removedBaseMdnForProfileMdn()";
        List<String> dbMdn = new ArrayList<String>();
        for (Collection<String> gr : dbGrpMdn) {
            for (String mdn : gr) {
                dbMdn.add(mdn);
            }
        }
        List<String> differences = new ArrayList<String>(eTags.keySet());
        differences.removeAll(dbMdn);
        List<String> baseMdnRemove = new ArrayList<String>();
        for (Entry<String, List<String>> ent : profileMdnOfBaseMap.entrySet()) {
            for (String va : ent.getValue()) {
                if (differences.contains(va)) {
                    baseMdnRemove.add(ent.getKey());
                }
            }
        }
        eTags.keySet().removeAll(baseMdnRemove);
        knLogger.debug(methodName, " Etags after removal of base mdn of profile mdn :", eTags);
    }

    private static void removedBGCBaseMdnForProfileMdn(List<String> allMembers
            , Map<String, KnOPDirChgDTO> eTags
            , Map<String, List<String>> profileMdnOfBaseMap) {
        final String methodName = "removedBGCBaseMdnForProfileMdn()";
        List<String> differences = new ArrayList<String>(eTags.keySet());
        differences.removeAll(allMembers);
        List<String> baseMdnRemove = new ArrayList<String>();
        for (Entry<String, List<String>> ent : profileMdnOfBaseMap.entrySet()) {
            for (String va : ent.getValue()) {
                if (differences.contains(va)) {
                    baseMdnRemove.add(ent.getKey());
                }
            }
        }
        eTags.keySet().removeAll(baseMdnRemove);
        knLogger.debug(methodName, " Etas after removal of base mdn of profile mdn :", eTags);
    }

  public static LinkedList<KnLIEventDTO> upmUnassignUserProfilePopulateLIData(Map<String, KnOPDirChgDTO> eTags
          ,String pttServerId,Map<Integer, String> groupNameMap, String deletedMember
          ,KnPersisterTxn persisterTxn) {
        final String methodName="upmUnassignUserProfilePopulateLIData()";
        knLogger.debug(methodName, "etagMap - ", eTags, "groupNameMap - ", groupNameMap,
                "deletedMember", deletedMember);

        List<String> targetMdnList =new ArrayList<>();
        List groupIdList;
        Map<String, List<String>> profileMdnOfBaseMap=new HashMap<>();
        Map<String, KnOPDirChgDTO> baseMdnETags=new HashMap<>();
        Map<String, String> mdnMcpttMap = new HashMap<>();
        try {
            List<String> realMdns = groupInfoUtil.getRealMdns(new ArrayList<String>(eTags.keySet()), pttServerId, persisterTxn);
            profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(realMdns, pttServerId, persisterTxn);
            targetMdnList = KnLIEventHandler.getTagetMdnList(realMdns);
            List<String> profileMdns=new ArrayList<>();
            profileMdns.add(deletedMember);
            removeBaseMdnOfProfileMdnFromEtag(profileMdns,eTags,profileMdnOfBaseMap);
            baseMdnETags=convertEtagTOBaseMdn(eTags,profileMdnOfBaseMap);
            mdnMcpttMap = KnCorpSubsProvInfoUtil.getMdnMcpttIdMap(targetMdnList, persisterTxn,pttServerId);
        } catch (KnDAOException | KnCorpBOException e) {
            knLogger.error(methodName," getRealMdns details error",e);
        }

        LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();

        for (String mdn : targetMdnList) {
            groupIdList = new ArrayList(groupNameMap.keySet());
            KnOPDirChgDTO directory = baseMdnETags.get(mdn);
            if(directory!=null) {
                Collection<KnOPDocChgDTO> docList = directory.getDocChgDTO();
                Iterator ite = docList.iterator();
                Iterator itg = groupIdList.iterator();

                while (ite.hasNext() && itg.hasNext()) {
                    List<String> removedGroupMembersMdnList = new ArrayList<>();
                    KnOPDocChgDTO doc = (KnOPDocChgDTO) ite.next();
                    short contact = 1;
                    KnLIEventDTO liEventDTO = new KnLIEventDTO();
                    liEventDTO.setMdn(mdn);
                    liEventDTO.setMcpttId(mdnMcpttMap.get(mdn));
                    int grpId;
                    grpId = (Integer) itg.next();
                    String groupURI = "tel:+".concat(mdn).concat(";kn-corp-group=");
                    if (groupNameMap != null && groupNameMap.get(grpId) != null) {
                        groupURI = groupURI.concat(groupNameMap.get(grpId));
                    }
                    liEventDTO.setGroupURI(groupURI);
                    contact = 2;
                    if (doc.getRemovedGroupMembers() != null && !doc.getRemovedGroupMembers().isEmpty()) {
                        liEventDTO.setAction(KnLIConstants.MODIFY_GROUP_ACTION);
                        removedGroupMembersMdnList.addAll(doc.getRemovedGroupMembers());
                    } else {
                        liEventDTO.setAction(KnLIConstants.DELETE_GROUP_ACTION);
                        removedGroupMembersMdnList = null;
                    }
                    liEventDTO.setDeletedMembers(getBaseMdnList(removedGroupMembersMdnList, profileMdnOfBaseMap));
                    //liEventDTO.setAction(KnLIConstants.DELETE_GROUP_ACTION);
                    //in case of profile mdn already assigned,we need to remove the target mdn getting added to added members
                    if (liEventDTO.getAddedMembers() != null && !liEventDTO.getAddedMembers().isEmpty()) {
                        liEventDTO.getAddedMembers().remove(mdn);
                    }
                    liEventDTO.setDocumentType(contact);
                    liEventDTO.setPttServerId(pttServerId);
                    liEventDTO.setErrorCode(KnLIConstants.SUCCESS_CODE);
                    liEventList.add(liEventDTO);
                }
            }
        }
        knLogger.debug(methodName,"Exit liEventList :",liEventList);
        return liEventList;
    }

    private static void removeBaseMdnOfProfileMdnFromEtag(List<String> profileMdns
            ,Map<String, KnOPDirChgDTO> eTags
            , Map<String, List<String>> profileMdnOfBaseMap) {
        final String methodName="removeBaseMdnOfProfileMdnFromEtag";
        String baseMdnRemove=null;
        for (Entry<String, List<String>> ent : profileMdnOfBaseMap.entrySet()) {
            for (String va : ent.getValue()) {
                if (profileMdns.contains(va)) {
                    baseMdnRemove=ent.getKey();
                }
                else{
                    eTags.keySet().remove(va);
                }
            }
        }
        eTags.keySet().remove(baseMdnRemove);
        knLogger.debug(methodName," ETags after base mdn and other profile mdn removal ",eTags);
    }

    private static void removeBaseMdnOfMcxGroupProfileMdnFromEtag(List<String> profileMdns
            ,Map<String, KnOPDirChgDTO> eTags
            , Map<String, List<String>> profileMdnOfBaseMap) {
        final String methodName="removeBaseMdnOfProfileMdnFromEtag";
        String baseMdnRemove=null;
        for (Entry<String, List<String>> ent : profileMdnOfBaseMap.entrySet()) {
            for (String va : ent.getValue()) {
                if (profileMdns.contains(va)) {
                    baseMdnRemove=ent.getKey();
                }

            }
        }
        eTags.keySet().remove(baseMdnRemove);
        knLogger.debug(methodName," ETags after base mdn and other profile mdn removal ",eTags);
    }

    public static List<String> removeProfileMdn(Collection<String> addedMembers,String xdmsHome,KnPersisterTxn persisterTxn){
        final String methodName="removeProfileMdn()";
        Set<String> mdnsList=null;
        try {
            if(addedMembers!=null){
                List<String> reqMdn=new ArrayList<>(addedMembers);
                mdnsList=corpSubsProvInfoUtil.getBaseMdnByProfileMdns(reqMdn,xdmsHome,persisterTxn);
            }
        } catch (KnCorpBOException e) {
            knLogger.error(methodName,e.getMessage());
        }
        knLogger.debug(methodName," mdnsList :",mdnsList);
        return mdnsList==null?null:new ArrayList<>(mdnsList);
    }

    public void getAndUpdateBulkDirectoryEtag(Set<String> mdnList, KnPersisterTxn persisterTxn, String
            xdmsHomePttId) throws KnCorpBOException{
        final String methodName = "getAndUpdateBulkDirectoryEtag()";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO( xdmsHomePttId);
            Map<String, Integer> subsContactCountMap = new HashMap<>();
            var mdnListArray = new ArrayList<>(mdnList);
            var mdnSplitList = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var mdnBatchList : mdnSplitList) {
               corpXdmDao.getAndUpdateBulkDirectoryEtag(new HashSet<>(mdnBatchList), persisterTxn);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured -  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateBulkSubsTS(Set<String> mdnList, KnPersisterTxn persisterTxn, String
            xdmsHomePttId) throws KnCorpBOException{
        final String methodName = "updateBulkSubsTS()";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO( xdmsHomePttId);
            var mdnListArray = new ArrayList<>(mdnList);
            var mdnSplitList = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var mdnBatchList : mdnSplitList) {
                corpXdmDao.updateBulkSubsTS(new HashSet<>(mdnBatchList), persisterTxn);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured -  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     *
     * @param corpId => Owner corpId
     * @param grpShared => Grp is shared or not
     * @param intSharedCorpIds => if grp is shared then shared Corp Id List
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<String> getAllPreConfigGroupMdns(int corpId,Integer grpShared,List<Integer> intSharedCorpIds,
                                                 String xdmsHome,KnPersisterTxn persisterTxn)throws KnCorpBOException{
        final String methodName = "getAllPreConfigGroupMdns()";
        List<String> preConfigGroupMdns=new ArrayList<>();
        knLogger.debug(methodName," corpId :",corpId," grpShared ",grpShared," intSharedCorpIds :",intSharedCorpIds);
        try {
            List<String> regroupMdns=getOwnCorpRegroupMembers(corpId,xdmsHome, persisterTxn);
            if(grpShared!=null&&grpShared.equals(ENABLED)){
                List<String> reGroupSharedMdn=new ArrayList<>();
                //shared group corp members
                List<String> shardGrpmdn=groupInfoUtil.getSharedGroupMemberBySharedAndOwnCorpids(corpId,intSharedCorpIds,xdmsHome, persisterTxn);
                //shared upm members
                List<String> shardUPMmdn=getMdnBySharedUserProfileCorpIds(corpId,intSharedCorpIds,xdmsHome, persisterTxn);
                reGroupSharedMdn.addAll(shardGrpmdn);
                reGroupSharedMdn.addAll(shardUPMmdn);
                Collection<KnCorpSubscriberDTO> reGroupSharedMdnInfo = getSubscribersInfo(reGroupSharedMdn, xdmsHome, persisterTxn);
                for(KnCorpSubscriberDTO corpSubInfo:reGroupSharedMdnInfo){
                    boolean groupRegroupEnabled = KnGeneralUtil.getFeatureBitValue(corpSubInfo.getSubsActiveFS2(), GROUP_REGROUP);
                    if(groupRegroupEnabled){
                        preConfigGroupMdns.add(corpSubInfo.getMdn());
                    }
                }
            }
            preConfigGroupMdns.addAll(regroupMdns);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured -  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName," EXIT :",KnGDPRTemplate.mdnList(preConfigGroupMdns));
        return preConfigGroupMdns;
    }

    public List<String> getOwnCorpRegroupMembers(int corpId,String xdmsHomePttId,KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="getOwnCorpRegroupMembers()";
        List<String> regroupMdns =new ArrayList<>();
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            regroupMdns = corpXdmDao.getOwnCorpRegroupMembers(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return regroupMdns;
    }

    public List<String> getMdnBySharedUserProfileCorpIds(int ownCorpId,List<Integer> sharedCorpids, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException  {
        List<String> result=new ArrayList<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result = xdmDAO.getMdnBySharedUserProfileCorpIds(ownCorpId,sharedCorpids,persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }
    public Collection<KnCorpSubscriberDTO> getSubscribersInfo(Collection<String> addedMdnList,
                                                              String pttServerId,
                                                              KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscriberInfo(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        Collection<KnCorpSubscriberDTO> pocSubscrList = null;
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            pocSubscrList = corpXdmDao.getSubscribersInfo(addedMdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscriberInfo - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return pocSubscrList;
    }

    public void populateForTheProfileMdns(Map<String,Integer> mapping,List<String> profileMdns,Integer common_Au){
        final String methodName = "populateForTheProfileMdns(Map<String,Integer>, List<String>, Integer)";
        knLogger.debug(methodName, "ENTRY :");
        for(String profileMdn : profileMdns){
            mapping.put(profileMdn,common_Au);
        }
    }

    public boolean isRequiredCorpNotified(String subFS2, int corpId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "isRequiredCorpNotified(String, int,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : subFS2 ", subFS2 , " corpId ", corpId);
        boolean requireCrpNotifyFlag = Boolean.FALSE;
        try {
            if (corpId == 0) {
                knLogger.debug(methodName, " Not a Valid corpId ", corpId);
                return requireCrpNotifyFlag;
            }

            if (lmrInterFlagValidate(subFS2)) {
                IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                        createXDMServerDAO(genInfoUtil.retrieveLocalXDMPttServerId());
                KnCorpConfigInfoDto corpConfigInfoDto  = xdmDAO.selectCorpConfigureInfo(corpId,UGWINTEROP,persisterTxn);
                if((corpConfigInfoDto == null) || (corpConfigInfoDto.getParamValue() == null)
                        || (corpConfigInfoDto.getParamValue().equalsIgnoreCase(String.valueOf(DISABLED)))){
                    knLogger.debug(methodName, " UGWInterop flag is disabled ");
                     requireCrpNotifyFlag = Boolean.TRUE;
                }
            }
            knLogger.debug(methodName, "ENTRY : requireCrpNotifyFlag ", requireCrpNotifyFlag);

        } catch (KnDAOException | KnBOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the LmrInterOp - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return requireCrpNotifyFlag;
    }

    /**
     *  API to update the UGWINTEROP in POCCORPCONFIGINFO
     * @param corpId
     * @param paramValue
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void updateLmrInterOpInDB(int corpId, int paramValue, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateLmrInterOpInDB(int , int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId", corpId, " paramValue", paramValue);
        try {

            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                    createXDMServerDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            KnCorpConfigInfoDto corpConfigInfoDto = xdmDAO.selectCorpConfigureInfo(corpId, UGWINTEROP, persisterTxn);
            if (corpConfigInfoDto == null) {
                knLogger.debug(methodName, " ugwInterop flag is NULL then Insert ");
                corpConfigInfoDto = new KnCorpConfigInfoDto();
                corpConfigInfoDto.setCorpId(corpId);
                corpConfigInfoDto.setParamName(UGWINTEROP);
                corpConfigInfoDto.setParamValue(String.valueOf(paramValue));
                xdmDAO.insertCorpConfigureInfo(corpConfigInfoDto, persisterTxn);
            } else {
                knLogger.debug(methodName, " ugwInterop flag is Not NULL then Update ");
                corpConfigInfoDto.setParamValue(String.valueOf(paramValue));
                xdmDAO.updateCorpConfigureInfo(corpConfigInfoDto, persisterTxn);
            }
        } catch (KnDAOException | KnBOException e) {
            knLogger.error(methodName, "KnDAOException occurred while updating - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteLmrInterOpInDB(int corpId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteLmrInterOpInDB(int , int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId", corpId);
        try {

            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                    createXDMServerDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            KnCorpConfigInfoDto corpConfigInfoDto = new KnCorpConfigInfoDto();
            corpConfigInfoDto.setCorpId(corpId);
            corpConfigInfoDto.setParamName(UGWINTEROP);
            xdmDAO.deleteCorpConfigureInfo(corpConfigInfoDto, persisterTxn);

        } catch (KnDAOException | KnBOException e) {
            knLogger.error(methodName, "KnDAOException occurred while updating - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpConfigInfoDto selectLmrInterOpInDB(int corpId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "selectLmrInterOpInDB(int , int, KnPersisterTxn)";
        KnCorpConfigInfoDto corpConfigInfoDto = null;
        knLogger.debug(methodName, "ENTRY : corpId", corpId);
        try {

            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                    createXDMServerDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            corpConfigInfoDto = xdmDAO.selectCorpConfigureInfo(corpId, UGWINTEROP, persisterTxn);
        } catch (KnDAOException | KnBOException e) {
            knLogger.error(methodName, "KnDAOException occurred while updating - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return corpConfigInfoDto;
    }

    /**
     * Generci API to form the payload for Corp
     * @param eventType
     * @param corpProfile
     * @param lmrInterOpFlag
     * @param notifyEventType
     * @return
     */
    public KnCorporateExdmsNotifyDto formCorpNotifyPayload(String eventType, KnCorpProfileDTO corpProfile, int lmrInterOpFlag, int notifyEventType) {
        KnCorporateExdmsNotifyDto corporateExdmsNotifyDto = new KnCorporateExdmsNotifyDto();
        corporateExdmsNotifyDto.setId(eventType + com.kodiak.xdms.server.common.resources.KnConstants.LINE_SAPERATOR + corpProfile.getCorpId());
        corporateExdmsNotifyDto.setType(eventType);
        corporateExdmsNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
        corporateExdmsNotifyDto.setNotifyEventType(notifyEventType);
        corporateExdmsNotifyDto.setCorpid(corpProfile.getCorpId());
        corporateExdmsNotifyDto.setExtCorpId(corpProfile.getExtCorpId() == null ? null : corpProfile.getExtCorpId().trim());
        corporateExdmsNotifyDto.setCorpName(corpProfile.getNetworkName());
        corporateExdmsNotifyDto.setLmrInteropFlag(lmrInterOpFlag);
        return corporateExdmsNotifyDto;
    }


    public static Map<String, KnOPDirChgDTO> formXcapAddGroupMemberDiffNotification(Map<String, KnOPDirChgDTO> etagMap,
                                                                                    Map<Integer, Collection<KnCorpContactDTO>> addedGroupMembers) {
        final String methodName="formXcapAddGroupMemberDiffNotification()";
        knLogger.debug(methodName," Entry etagMap:",etagMap," addedGroupMembers:",addedGroupMembers);
        for (String mdn : etagMap.keySet()) {
            KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
            Collection<KnOPDocChgDTO> docChgDTOS = dirChgDTO.getDocChgDTO();
            for (KnOPDocChgDTO docChgDTO : docChgDTOS) {
                if (docChgDTO.getDocUri().contains(APP_UID_CORP_GROUP)) {
                    if (addedGroupMembers != null && !addedGroupMembers.isEmpty()) {
                        Collection<KnSubscriberDTO> addedgrpMembers = new ArrayList<>();
                        for (Map.Entry<Integer, Collection<KnCorpContactDTO>> entry : addedGroupMembers.entrySet()) {
                            Integer groupId = entry.getKey();
                            Collection<KnCorpContactDTO> contactDTO = addedGroupMembers.get(groupId);
                            for (KnCorpContactDTO contact : contactDTO) {
                                KnSubscriberDTO subscriberDTO = new KnSubscriberDTO();
                                subscriberDTO.setMdn(contact.getMdn());
                                subscriberDTO.setMemberCorpId(contact.getCorpId());
                                subscriberDTO.setGroupId(groupId);
                                if (groupId == docChgDTO.getGroupId()) {
                                    addedgrpMembers.add(subscriberDTO);
                                }
                            }
                            if (!addedgrpMembers.isEmpty()) {
                                docChgDTO.setAddedGroupMembers(addedgrpMembers);
                            }

                        }
                    }
                    docChgDTO.setOsmListChanged(false);

                }
            }
        }
        knLogger.debug(methodName," Exit etagMap:",etagMap);
        return etagMap;
    }

    public static Map<String, KnOPDirChgDTO> formXcapDelGroupMemberDiffNotification(Map<String, KnOPDirChgDTO> etagMap,
                                                                                    LinkedHashMap<Integer, LinkedList<String>> deletedGrpMembers,
                                                                                    LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus) {
        final String methodName="formXcapDelGroupMemberDiffNotification()";
        knLogger.debug(methodName," Entry : etagMap",etagMap," deletedGrpMembers :",deletedGrpMembers
                ," delGroupMemStatus :",delGroupMemStatus);
        Map<String, Collection<String>> delGrpMemMap = getDeletedMembers(deletedGrpMembers, delGroupMemStatus);
        for (String mdn : etagMap.keySet()) {
            KnOPDirChgDTO dirChgDTO = etagMap.get(mdn);
            Collection<KnOPDocChgDTO> docChgDTOS = dirChgDTO.getDocChgDTO();
            for (KnOPDocChgDTO docChgDTO : docChgDTOS) {
                if (docChgDTO.getDocUri().contains(APP_UID_CORP_GROUP)) {
                    Collection<String> delGrpMembeList = new ArrayList<String>();
                    if (delGrpMemMap != null && !delGrpMemMap.isEmpty()) {
                        delGrpMembeList = delGrpMemMap.get(String.valueOf(docChgDTO.getGroupId()));
                    }
                    if (deletedGrpMembers != null && !deletedGrpMembers.isEmpty()) {
                        docChgDTO.setRemovedGroupMembers(delGrpMembeList);
                    }
                    docChgDTO.setOsmListChanged(false);
                }
            }
        }
        knLogger.debug(methodName," Exit:",etagMap);
        return etagMap;
    }


    /**
     * This method checks UGWINTEROP flag is enabled or not, if its not enabled then it enables the flag
     *
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public boolean isGroupProfileNotifyRequired(int corpId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "isGroupProfileNotifyRequired(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId ", corpId);
        boolean requireGroupProfileNotifyFlag = Boolean.FALSE;
        try {
            if (corpId == 0) {
                knLogger.info(methodName, " Not a Valid corpId ", corpId);
                return requireGroupProfileNotifyFlag;
            }
            IXDMServerDAO xdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).
                    createXDMServerDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            KnCorpConfigInfoDto corpConfigInfoDto = xdmDAO.selectCorpConfigureInfo(corpId, UGWINTEROP, persisterTxn);
            if ((corpConfigInfoDto == null) || (corpConfigInfoDto.getParamValue() == null)
                    || (corpConfigInfoDto.getParamValue().equalsIgnoreCase(String.valueOf(DISABLED)))) {
                knLogger.debug(methodName, " ugwInterop flag is disabled ");
                requireGroupProfileNotifyFlag = Boolean.TRUE;
            }
            knLogger.info(methodName, "Exit : requireGroupProfileNotifyFlag ", requireGroupProfileNotifyFlag);

        } catch (KnDAOException | KnBOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the ugwInterOp - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return requireGroupProfileNotifyFlag;

    }

    /**
     * Generci API to form the payload for Device
     * @param eventType
     * @param deviceProfileInfoDTO
     * @param lmrInterOpFlag
     * @param notifyEventType
     * @return
     */
    public KnDeviceExdmsNotifyDto formDeviceNotifyPayload(String eventType,  KnXDMDeviceProvDTO deviceProfileInfoDTO, int lmrInterOpFlag, int notifyEventType, String reqDeviceId) {
        KnDeviceExdmsNotifyDto devicexdmsNotifyDto = new KnDeviceExdmsNotifyDto();
        devicexdmsNotifyDto.setId(eventType + com.kodiak.xdms.server.common.resources.KnConstants.LINE_SAPERATOR + reqDeviceId);
        devicexdmsNotifyDto.setType(eventType);
        devicexdmsNotifyDto.setVer(MICROSERVICE_NOTIFY_DOC_VER);
        devicexdmsNotifyDto.setNotifyEventType(notifyEventType);
        devicexdmsNotifyDto.setCorpid(deviceProfileInfoDTO.getCorpId());
        devicexdmsNotifyDto.setDeviceId(reqDeviceId);
        devicexdmsNotifyDto.setDeviceSubscrMdn(deviceProfileInfoDTO.getDeviceSubscrMdn());
        devicexdmsNotifyDto.setDeviceType(deviceProfileInfoDTO.getDeviceType() + "");
        devicexdmsNotifyDto.setLastUpdateTime(deviceProfileInfoDTO.getDeviceLastUsed());
        devicexdmsNotifyDto.setLmrInteropFlag(lmrInterOpFlag);
        return devicexdmsNotifyDto;
    }

    public boolean lmrInterFlagValidate(String subFS2) {
        final String methodName = "lmrInterFlagValidate(String,KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY : subFS2 ", subFS2);
        if (subFS2 == null) {
            return Boolean.FALSE;
        }
        boolean returnData = Boolean.FALSE;
        boolean interOpFlag = KnGeneralUtil.getFeatureBitValue(subFS2, FEATURE_SET.INTEROPFEATURE.value());
        boolean dataInterOp = KnGeneralUtil.getFeatureBitValue(subFS2, FEATURE_SET.DATA_INTER_OP.value());
        if (interOpFlag || dataInterOp) {
            returnData = Boolean.TRUE;
        }
        knLogger.debug(methodName, "ENTRY : returnData ", returnData);
        return returnData;
    }

    public List<KnDeviceDetailsDTO> getDeviceList(int corpId, Integer filterType,
                                                  Integer fetchSize, Integer nextToken,
                                                  String xdmsHomePttId, boolean readOnly,
                                                  KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "getDeviceList(int corpId,Integer filterType,Integer fetchSize, Integer nextToken,String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "corpId", corpId, "filterType", filterType, "fetchSize", fetchSize, "nextToken", nextToken, "xdmsHomePttId", xdmsHomePttId);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getDeviceList(corpId, filterType, fetchSize, nextToken, readOnly, persisterTxn);
        }catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving master list", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnXDMDeviceAddlInfoDTO> getDeviceAddInfoMap(List<String> deviceList, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getDeviceAddInfoMap(List<String> deviceList, String xdmsHomePttId, KnPersisterTxn persisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getDeviceAddInfoMap(deviceList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving DeviceAddInfoMap", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpDeviceInfoRespDTO getDeviceDetails(KnIPDeviceInfoDTO deviceInfoDTO, String xdmsHomePttId, boolean readOnly,
                                                    KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getDeviceDetails(KnIPDeviceInfoDTO deviceInfoDTO,String xdmsHomePttId,  boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "deviceInfoDTO", deviceInfoDTO, "xdmsHomePttId", xdmsHomePttId);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getDeviceDetails(deviceInfoDTO, readOnly, persisterTxn);
        }catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving master list", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<KnCORPGroupStatsRespDTO> getGroupStats(int corpId, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getGroupStats(int corpId,String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "corpId", corpId, "xdmsHomePttId", xdmsHomePttId);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getGroupStats(corpId, readOnly, persisterTxn);
        }catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving master list", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpSubsStatsRespDTO getSubscriberStats(KnIPCorpInfoDTO corpInfoDTO, String xdmsHomePttId, boolean readOnly,
                                                     KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getDeviceList(int corpId, String xdmsHomePttId,  boolean readOnly,KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "corpId", corpInfoDTO.getCorpId(), "xdmsHomePttId", xdmsHomePttId);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getSubscriberStats(corpInfoDTO, readOnly, persisterTxn);
        }catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving master list", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public KnCorpDeviceStatsRespDTO getDeviceStats(String corpId, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getDeviceStats(int corpId, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "corpId", corpId, "xdmsHomePttId", xdmsHomePttId);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getDeviceStats(corpId, readOnly, persisterTxn);
            //KnCorpStatsDAO xdmDao = new KnCorpStatsDAO(xdmsHomePttId);
            //return xdmDao.getDeviceStats(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving master list", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public String getCorporateFS(int corpId, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getCorporateFS(int corpId, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "corpId", corpId, "xdmsHomePttId", xdmsHomePttId);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getCorporateFS(corpId, readOnly, persisterTxn);
        }catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving master list", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public static void updateCorpFs(String corpFs,String corpId,String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateCorpFs(int corpId,String xdmsHomePttId,KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "corpId", corpId, "xdmsHomePttId", xdmsHomePttId);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.updateCorpFs(corpFs,corpId,persisterTxn);
        }catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating corpFs ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getCorpDeviceCount(int corpId, String xdmsHomePttId, boolean readOnly,
                                  KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getCorpDeviceCount()";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getCorpDeviceCount(corpId, readOnly, persisterTxn);
        }catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getActiveMdns(List<String> mdns, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getActiveMdns(List<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ", KnGDPRTemplate.mdnList(mdns));
        List<String> memberList = groupInfoUtil.getGrpMemDetails(mdns, pttServerId, persisterTxn);
        List<String> listActiveMdns = new ArrayList<>();
        try {
            var mdnListArray = new ArrayList<>(memberList);
            var subsLists = KnGeneralUtil.splitList(mdnListArray, GG_BULK_UPDATE_SIZE);
            for (var subsList : subsLists) {
                Map<String, String> onlineSubscribersListByMdn = KnGeneralCacheUtil.getInstance().getOnlineSubcribersListByMdn(subsList);
                if (!onlineSubscribersListByMdn.isEmpty()) {
                    listActiveMdns.addAll(onlineSubscribersListByMdn.keySet());
                }
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "Exit : ", KnGDPRTemplate.mdnList(listActiveMdns));
        return listActiveMdns;
    }

    public int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    public Map<Integer, Integer> getCorpIdAndLargeGroupFlagMap(Collection<Integer> corpIds, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getCorpIdAndLargeGroupFlagMap(Collection<Integer>, KnPersisterTxn)";
        Map<Integer, Integer> map = new HashMap<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            return xdmDAO.getCorpIdAndLargeGroupFlagMap(corpIds, persisterTxn);
        } catch (KnBOException e) {
            knLogger.error(methodName, "KnBOException occured while retrieving", e);
            throw new KnCorpBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getErrorMessage(), e);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getMdnsLessThanThirteenPv(List<String> mdns, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getMdnsLessThanThirteenPv(List<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ", KnGDPRTemplate.mdnList(mdns));
        return groupInfoUtil.getMdnsLessThanThirteenPv(mdns, pttServerId, persisterTxn);
    }

    public KnIPUserProfileDTO selfDndPrivilegeCheck(KnIPUserProfileDTO ipUserProfileDTO, KnCorpProfileDTO corpProfile, Map<String, String> microServicesParamNameValueMap) throws KnCorpBOValidationException {
        final String methodName = "selfDndPrivilegeCheck(KnIPUserProfileDTO, KnCorpProfileDTO, Map<String, String>)";
        final String sysSelfDndFeature = microServicesParamNameValueMap.get(SELF_DND_FEATURE);
        String corpFS2 = corpProfile.getCorpFS2();
        knLogger.info(methodName, " SELF_DND_FEATURE and CORPFS2 from DB ", sysSelfDndFeature, " :: ", corpFS2);
        BitSet corpAdminBitSet = KnGeneralUtil.convertHexStringToBitSet(corpFS2);
        boolean selfDndPrivilegeBit = corpAdminBitSet.get(com.kodiak.common.resources.KnConstants.FEATURE_SET.SELF_DND_PRIVILEGE.value());
        if (null != ipUserProfileDTO.getUserProfileFSDto()) {
            if (null != sysSelfDndFeature && sysSelfDndFeature.equalsIgnoreCase(ENABLED_STRING) && selfDndPrivilegeBit) {
                if (null != ipUserProfileDTO.getUserProfileFSDto().getSelfDnDPrivilege() &&
                        ipUserProfileDTO.getUserProfileFSDto().getSelfDnDPrivilege().equalsIgnoreCase(DISABLED_STRING)) {
                    ipUserProfileDTO.getUserProfileFSDto().setSelfDnDPrivilege(DISABLED_STRING);
                    if (null != ipUserProfileDTO.getUserProfileDTO()) {
                        ipUserProfileDTO.getUserProfileDTO().setSelfDnDPrivilege(DISABLED_STRING);
                    } else if (null != ipUserProfileDTO.getModifiedUserProfileDTO()) {
                        ipUserProfileDTO.getModifiedUserProfileDTO().setSelfDnDPrivilege(DISABLED_STRING);
                    }
                } else {
                    ipUserProfileDTO.getUserProfileFSDto().setSelfDnDPrivilege(ENABLED_STRING);
                    if (null != ipUserProfileDTO.getUserProfileDTO()) {
                        ipUserProfileDTO.getUserProfileDTO().setSelfDnDPrivilege(ENABLED_STRING);
                    } else if (null != ipUserProfileDTO.getModifiedUserProfileDTO()) {
                        ipUserProfileDTO.getModifiedUserProfileDTO().setSelfDnDPrivilege(ENABLED_STRING);
                    }
                }
            } else if (null != ipUserProfileDTO.getUserProfileFSDto().getSelfDnDPrivilege() &&
                    ipUserProfileDTO.getUserProfileFSDto().getSelfDnDPrivilege().equalsIgnoreCase(ENABLED_STRING)) {
                knLogger.error(methodName, " Self DND Privilege configurations are not done correctly.");
                throw new KnCorpBOValidationException(SYSTEM_LEVEL_SELF_DND_PRIVILEGE_FLAG_DISABLED,
                        " Self DND Privilege configuration are not done correctly", "Configurations missing in DB or CORPFS2");
            }
        } else if (null != sysSelfDndFeature && sysSelfDndFeature.equalsIgnoreCase(ENABLED_STRING) && selfDndPrivilegeBit) {
            KnUserProfileFSDTO ipUpmFsSelfDndPrivilege = new KnUserProfileFSDTO();
            ipUpmFsSelfDndPrivilege.setSelfDnDPrivilege(ENABLED_STRING);
            ipUserProfileDTO.setUserProfileFSDto(ipUpmFsSelfDndPrivilege);
        }
        return ipUserProfileDTO;
    }

    public Set<String> getProfileMdnList(int groupId, KnPersisterTxn persisterTxn, String xdmsHomePttId) throws KnCorpBOException {
        final String methodName = "getProfileMdnList(int groupId, KnPersisterTxn persisterTxn, String xdmsHomePttId)";
        Map<Integer, Integer> map = new HashMap<>();
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getProfileMdnList(groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getGroupMemCountAndList(int groupId, KnPersisterTxn persisterTxn, String xdmsHomePttId) throws KnCorpBOException {
        final String methodName = "getGroupMemCountAndList(int groupId, KnPersisterTxn persisterTxn, String xdmsHomePttId)";
        Map<Integer, Integer> map = new HashMap<>();
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getGroupMemCountAndList(groupId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Integer> getGroupMemCount(Set<Integer> groupIds, KnPersisterTxn persisterTxn, String xdmsHomePttId) throws KnCorpBOException {
        final String methodName = "getGroupMemCount(List<Integer> groupIds, KnPersisterTxn persisterTxn, String xdmsHomePttId)";
        Map<Integer, Integer> map = new HashMap<>();
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getGroupMemberCount(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Integer> getGroupAllMemCount(Set<Integer> groupIds, KnPersisterTxn persisterTxn, String xdmsHomePttId) throws KnCorpBOException {
        final String methodName = "getGroupAllMemCount(List<Integer> groupIds, KnPersisterTxn persisterTxn, String xdmsHomePttId)";
        Map<Integer, Integer> map = new HashMap<>();
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getGroupAllMemCount(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getSubsDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn, String xdmsHomePttId) throws KnCorpBOException {
        final String methodName = "getSubsDetails(List<String> mdnList, KnPersisterTxn persisterTxn, String xdmsHomePttId)";
        Map<Integer, Integer> map = new HashMap<>();
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            Map<String, Integer> getSubsDetailsRes = new HashMap<>();
            var mdnListArray = new ArrayList<>(mdnList);
            var mdnSplitList = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var mdnBatchList : mdnSplitList) {
                var getSubsDetails = corpXdmDao.getSubsDetails(mdnBatchList, persisterTxn);
                if (null != getSubsDetails && !getSubsDetails.isEmpty()) {
                    getSubsDetailsRes.putAll(getSubsDetails);
                }
            }
            return getSubsDetailsRes;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getUserPfofileIdCount(int grpId, KnPersisterTxn persisterTxn, String xdmsHome) throws KnCorpBOException {
        final String methodName = "getUserPfofileIdCount(int grpId, KnPersisterTxn persisterTxn, String xdmsHome)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getUserPfofileIdCount(grpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, Integer> getProfileIdCount(Set<Integer> groupIds, KnPersisterTxn persisterTxn, String xdmsHome) throws KnCorpBOException {
        final String methodName = "getProfileIdCount(List<Integer> groupIds, KnPersisterTxn persisterTxn, String xdmsHome)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getProfileIdCount(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getMemCountBasedOnLocWatcher(int grpId, KnPersisterTxn persisterTxn, String xdmsHome) throws KnCorpBOException {
        final String methodName = "getMemCountBasedOnLocWatcher(int grpId, KnPersisterTxn persisterTxn, String xdmsHome)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getMemCountBasedOnLocWatcher(grpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Set<Integer> getGroupIdBasedOnMdn(String mdn, KnPersisterTxn persisterTxn, String xdmsHome) throws KnCorpBOException {
        final String methodName = "getGroupIdBasedOnMdn(String mdn, KnPersisterTxn persisterTxn, String xdmsHome)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getGroupIdBasedOnMdn(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getMdnCountBasedOnUPMID(List<Integer> grpId, KnPersisterTxn persisterTxn, String xdmsHome) throws KnCorpBOException {
        final String methodName = "getMdnCountBasedOnUPMID(List<Integer> grpId, KnPersisterTxn persisterTxn, String xdmsHome)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getMdnCountBasedOnUPMID(grpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public long getCorporateEtagOnCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getCorporateEtagOnCorpId(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId" , corpId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(genInfoUtil.retrieveLocalXDMPttServerId());
            return xdmDAO.getCorporateEtagOnCorpId(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getCorporateEtag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (KnBOException e) {
            throw new RuntimeException(e);
        }
    }

    public String updateCatAccessPermSet(String extCorpId, String catAccessPermSet, KnPersisterTxn persisterTxn) throws KnCorpBOException, KnDAOException {
        final String methodName = "updateCatAccessPermSet()";
        knLogger.debug(methodName, "ENTRY - extCorpId :", extCorpId);
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(genInfoUtil.retrieveLocalXDMPttServerId());

            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String catAccessControllFeature = microServicesParamNameValueMap.get(KnConstants.CAT_ACCESS_CONTROL_FEATURE);


            if (null != catAccessControllFeature && catAccessPermSet.equals("0") && catAccessControllFeature.equals("1")) {
                return xdmDAO.updateCatAccessPermSet(extCorpId, catAccessPermSet, persisterTxn);
            } else if (null != catAccessControllFeature && (catAccessPermSet.equals("1") && (catAccessControllFeature.equals("1") ||
                    catAccessControllFeature.equals("0")))) {
                return xdmDAO.updateCatAccessPermSet(extCorpId, catAccessPermSet, persisterTxn);
            } else {
                knLogger.error(methodName, "CAT_ACCESS_CONTROL_FEATURE is not enabled");
                throw new KnCorpBOException(KnErrorCodes.BOEntity.CAT_ACCESS_CONTROL_FEATURE_NOT_ENABLED, "CAT_ACCESS_CONTROL_FEATURE is not enabled");
            }
        } catch (KnBOException e) {
            knLogger.error(methodName, "KnDAOException occured -  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Set<String> getProfileMdns(Set<Integer> groupIds, KnPersisterTxn persisterTxn, String xdmsHomePttId) throws KnCorpBOException {
        final String methodName = "getProfileMdns(Set<Integer> groupIds, KnPersisterTxn persisterTxn, String xdmsHomePttId)";
        Map<Integer, Integer> map = new HashMap<>();
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getProfileMdns(groupIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnIPCorpSubscContactListDTO getFilteredSubscContactListDTO(KnCorpSubsResquestDTO xdmRequestDTO, KnPersisterTxn persisterTxn) throws KnDAOException, KnBOException {
        String methodName = "getFilteredSubscContactListDTO(String, String, KnPersisterTxn)";

        long startTime = System.nanoTime();
        KnIPCorpSubscContactListDTO contactListDTO = new KnIPCorpSubscContactListDTO();

        String sourceMdn = xdmRequestDTO.getFromMdn();
        String targetMdn = xdmRequestDTO.getToMdn();

        contactListDTO.setCorpId(Integer.parseInt(xdmRequestDTO.getCorpId()));
        contactListDTO.setSubscriberMdn(targetMdn);
        contactListDTO.setSourceMdn(sourceMdn);


        Map<String, Map<Integer, Integer>> sublistMap = genInfoUtil.getSublistDetailByProfileMdns(Arrays.asList(sourceMdn, targetMdn), persisterTxn);
        knLogger.debug("sublistMap : " + sublistMap);


        //private list for source mdn
        List<Integer> sourcePrivateList = sublistMap.get(sourceMdn) != null ? sublistMap.get(sourceMdn).entrySet().stream().filter(entry -> entry.getValue() == 1).map(Map.Entry::getKey).collect(Collectors.toList()) : new ArrayList<>();
        knLogger.debug("sourcePrivateList : " + sourcePrivateList);

        //private list for target mdn
        List<Integer> targetPrivateList = sublistMap.get(targetMdn) != null ? sublistMap.get(targetMdn).entrySet().stream().filter(entry -> entry.getValue() == 1).map(Map.Entry::getKey).collect(Collectors.toList()) : new ArrayList<>();
        knLogger.debug("targetPrivateList : " + targetPrivateList);


        //sublist and member mdn map
        List<Integer> tempListofSublist = new ArrayList<>(sourcePrivateList);
        tempListofSublist.addAll(targetPrivateList);
        Map<Integer, List<String>> sourceSublistMemberMap = genInfoUtil.getSublistMemberBySublistId(tempListofSublist, persisterTxn);

        knLogger.debug("sourceSublistMemberMap : " + sourceSublistMemberMap);

        //private's contact for source mdn

        List<String> sourceMdnContactList = new ArrayList<>();
        if (!sourcePrivateList.isEmpty()) {
            for (Integer sublistId : sourcePrivateList) {
                if (sourceSublistMemberMap.containsKey(sublistId)) {
                    sourceMdnContactList.addAll(sourceSublistMemberMap.get(sublistId));
                }
            }
        }
        knLogger.debug("source Mdn Contact List : " + sourceMdnContactList);

        //private's contact for target mdn
        List<String> targetMdnContactList = new ArrayList<>();
        if (!targetPrivateList.isEmpty()) {
            for (Integer sublistId : targetPrivateList) {
                if (sourceSublistMemberMap.containsKey(sublistId)) {
                    targetMdnContactList.addAll(sourceSublistMemberMap.get(sublistId));
                }
            }
        }
        knLogger.debug("target Mdn Contact List : " + targetMdnContactList);

        List<String> sourceMdnContactListCopy = new ArrayList<>(sourceMdnContactList);
        List<String> targetMdnContactListCopy = new ArrayList<>(targetMdnContactList);
        sourceMdnContactList.removeAll(targetMdnContactListCopy);
        targetMdnContactList.removeAll(sourceMdnContactListCopy);


        List<Integer> sourceCCLList = sublistMap.get(sourceMdn) != null ? sublistMap.get(sourceMdn).entrySet().stream().filter(entry -> entry.getValue() == 6).map(Map.Entry::getKey).collect(Collectors.toList()) : new ArrayList<>();
        knLogger.debug("sourceCCLList : " + sourceCCLList);
        List<Integer> targetCCLList = sublistMap.get(targetMdn) != null ? sublistMap.get(targetMdn).entrySet().stream().filter(entry -> entry.getValue() == 6).map(Map.Entry::getKey).collect(Collectors.toList()) : new ArrayList<>();
        knLogger.debug("targetCCLList : " + targetCCLList);
        sourceCCLList.removeAll(targetCCLList);
        knLogger.debug("sourceCCLList  after remove: " + sourceCCLList);
        List<Integer> tempSourceCCLList = sublistMap.get(sourceMdn) != null ? sublistMap.get(sourceMdn).entrySet().stream().filter(entry -> entry.getValue() == 6).map(Map.Entry::getKey).collect(Collectors.toList()) : new ArrayList<>();
        knLogger.debug("tempSourceCCLList : " + tempSourceCCLList);
        targetCCLList.removeAll(tempSourceCCLList);

        //remove target mdn from list
        sourceMdnContactList.remove(xdmRequestDTO.getToMdn().trim());
        contactListDTO.setAddedMdnList(sourceMdnContactList);
        contactListDTO.setRemovedMdnList(targetMdnContactList);


        List<Integer> targetSublistList = sublistMap.get(targetMdn) != null ? sublistMap.get(targetMdn).entrySet().stream().filter(entry -> (entry.getValue() != 6 && entry.getValue() != 1)).map(Map.Entry::getKey).collect(Collectors.toList()) : new ArrayList<>();
        knLogger.debug("targetSublistList : " + targetSublistList);
        List<Integer> sourceSublistList = sublistMap.get(sourceMdn) != null ? sublistMap.get(sourceMdn).entrySet().stream().filter(entry -> (entry.getValue() != 6 && entry.getValue() != 1)).map(Map.Entry::getKey).collect(Collectors.toList()) : new ArrayList<>();
        knLogger.debug("sourceSublistList : " + sourceSublistList);
        List<Integer> copySourceSublistList = new ArrayList<>(sourceSublistList);
        List<Integer> copyTargetSublistList = new ArrayList<>(targetSublistList);
        sourceSublistList.removeAll(copyTargetSublistList);
        targetSublistList.removeAll(copySourceSublistList);
        sourceSublistList.addAll(sourceCCLList);
        contactListDTO.setAddedSublistIds(sourceSublistList);
        contactListDTO.setRemovedSublistIds(targetSublistList);


        contactListDTO.setRemoveCommonContactList(targetCCLList.stream().map(String::valueOf).collect(Collectors.toList()));
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        knLogger.debug(methodName, "Time taken to get contact list in nanoseconds: " + elapsedTime);
        knLogger.debug(methodName, "Time taken to get contact list in milliseconds: " + elapsedTime / 1000000);
        knLogger.exit(methodName, "contactListDTO : " + contactListDTO);
        return contactListDTO;
    }


    public static Map<Integer, Collection<KnCorpContactDTO>> getSubsDetails( int groupId , Collection<KnCorpSubscriberDTO> mdnList){
        Map<Integer, Collection<KnCorpContactDTO>> groupMemberInsertList = new HashMap<Integer, Collection<KnCorpContactDTO>>();
            Collection<KnCorpContactDTO> grpMemberList = new ArrayList<KnCorpContactDTO>();
            for (KnCorpSubscriberDTO subscriberDTO : mdnList) {
                KnCorpContactDTO contact = new KnCorpContactDTO();
                contact.setCorpId(subscriberDTO.getCorpId());
                contact.setMdn(subscriberDTO.getMdn());
               /* if (supervisiorMap.containsKey(subscriberDTO.getMdn())) {
                    contact.setSupervisory(supervisiorMap.get(subscriberDTO.getMdn()));
                }
                if (locWatcherMap.containsKey(subscriberDTO.getMdn())) {
                    contact.setLocWatcher(locWatcherMap.get(subscriberDTO.getMdn()));
                }*/
                contact.setName(subscriberDTO.getName());
                contact.setContact_type(subscriberDTO.getContact_type());
                contact.setClientType(subscriberDTO.getClientType());
                if (subscriberDTO.getClientType() == SUBSCR_CLIENT_TYPE.GROUPMDN.value()) {
                    contact.setMemberType(SG_MDN_MEMBER_TYPE);
                  /*  groupInfoDTO.setUgwInterop(UGWINTEROP_DBVALUE);
                    groupPersistDTO.setUgwInterop(UGWINTEROP_DBVALUE);*/
                } else if (subscriberDTO.getClientType() == SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
                    contact.setMemberType(SG_MDN_PATCH_MEMBER_TYPE);
                    /*groupInfoDTO.setUgwInterop(UGWINTEROP_DBVALUE);
                    groupPersistDTO.setUgwInterop(UGWINTEROP_DBVALUE);*/
                }
                contact.setCallInitiatePermission(subscriberDTO.getCallInitiatePermission());
                contact.setCallReceivePermission(subscriberDTO.getCallReceivePermission());
                contact.setInCallPermission(subscriberDTO.getInCallPermission());
                contact.setUa(subscriberDTO.getUa());
                contact.setAliasMdn(subscriberDTO.getAliasMdn());
                contact.setUserId(subscriberDTO.getUserId());
                contact.setSubsActiveFS2(subscriberDTO.getSubsActiveFS2());
                contact.setIsOSMAuthorize(subscriberDTO.getIsOSMAuthorize());

                boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(subscriberDTO.getSubsActiveFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.AFFILIATIONFEATURE.value());
                if (subscriberDTO.getClientPVmajorVer() >= com.kodiak.xdms.server.common.resources.KnConstants.PROTOCOL_VERSION_18
                        && bitEnabled && checkClientType(subscriberDTO.getClientType(), subscriberDTO.getMcpttCompliance()))
                {
                    contact.setIsAffiliationEnabled(ENABLED);
                } else {
                    contact.setIsAffiliationEnabled(DISABLED);
                }

                grpMemberList.add(contact);
            }
            groupMemberInsertList.put(groupId, grpMemberList);
            return groupMemberInsertList;
    }

    private static boolean checkClientType(Integer clientType, int mcsCompliance)
    {
        return clientType == KnConstants.SUBSCR_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
                || clientType == KnConstants.SUBSCR_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()
                || clientType == KnConstants.SUBSCR_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
                || clientType == KnConstants.SUBSCR_CLIENT_TYPE.DISPATCH_CLIENT.value()
                || clientType == KnConstants.SUBSCR_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                || mcsCompliance == KnConstants.MCPTT_COMPLIANCE;
    }

    public KnBulkGroupPropertiesDTO formBulkGroupNotifyPayload(String eventType, List<KnXDMGroupPropertyInfoDTO> groupProperty, int notifyEventType) {
        KnBulkGroupPropertiesDTO bulkGroupProperty = new KnBulkGroupPropertiesDTO();
        bulkGroupProperty.setId(eventType + com.kodiak.xdms.server.common.resources.KnConstants.LINE_SAPERATOR + System.currentTimeMillis());
        bulkGroupProperty.setType(eventType);
        bulkGroupProperty.setVer(MICROSERVICE_NOTIFY_DOC_VER);
        bulkGroupProperty.setNotifyEventType(notifyEventType);
        List<KnGroupPropertiesDTO> corpGpInfoList=new ArrayList<>();
        for(KnXDMGroupPropertyInfoDTO grpProp:groupProperty){
            KnGroupPropertiesDTO groupDto=new KnGroupPropertiesDTO();
            groupDto.setGrpId(Integer.valueOf(grpProp.getGroupId()));
            groupDto.setRecordingFs(grpProp.getRecordingFS());
            groupDto.setUgwInterop(grpProp.getUgwInterop());
            corpGpInfoList.add(groupDto);
        }
        bulkGroupProperty.setCorpGpInfoList(corpGpInfoList);
        return bulkGroupProperty;
    }

    public void validateCorpAndGroupParams(List<String> groupIdList, String corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "validateCorpAndGroupParams(List<String>,String,List<Integer>,String,int,KnPersisterTxn)";
        knLogger.info(methodName, " Validating the Group params ", "groupIdList-->", groupIdList, "corpId-->", corpId);
        ICorpXdmDAO xdmServerDAO = new KnCorpXdmDAO(xdmsHome);
        for (String groupId : groupIdList) {
            //Getting the group details to verify if the group belongs to the same corporate.
            KnCorpGroupDTO ifGroupExistsForCorpId = xdmServerDAO.ifGroupExistsForCorpId(groupId, corpId);
            String dbCorpid = String.valueOf(ifGroupExistsForCorpId.getCorpId());

            //If the Group doesnot belong to either shared or owned corp then throw error.
            if (!corpId.equals(dbCorpid)) {
                knLogger.info(methodName, "Checking if the corporate is a shared corporate or not: ");
                String sharedCorpId = corpId;
                String ownerCorpId = dbCorpid;
                boolean isSharedCorp = xdmServerDAO.sharedCorpCheck(groupId, sharedCorpId, ownerCorpId);
                if (!isSharedCorp) {
                    knLogger.error(methodName, "Group doesn't belong to the corporate", "groupId::", groupId, "corpId::", corpId);
                    throw new KnCorpBOException(GROUP_DOES_NOT_EXIST,
                            "Group doesn't belong to the corporate");
                }
            }
        }
    }

    public void validateGroupParams(List<String> groupIdList, String corpId, List<Integer> inContextIds, String xdmsHome, int idType, KnPersisterTxn persisterTxn) throws Exception {
        String methodName = "validateGroupParams(List<String>,String,List<Integer>,String,int,KnPersisterTxn)";
        knLogger.info(methodName, " Validating the Group params ", "groupIdList-->", groupIdList, "corpId-->", corpId,
                "inContextIds-->", inContextIds, "idType-->", idType);
        ICorpXdmDAO xdmServerDAO = new KnCorpXdmDAO(xdmsHome);
        for (String groupId : groupIdList) {
            int groupIdInt = Integer.parseInt(groupId);
            //Getting the group details to verify if the group belongs to the same corporate.
            KnCorpGroupDTO ifGroupExistsForCorpId = xdmServerDAO.ifGroupExistsForCorpId(groupId, corpId);
            String dbCorpid = String.valueOf(ifGroupExistsForCorpId.getCorpId());

            //incontext ID check for the sharedGroup
            //1. Select the FAN/BAN from group hirarcy map if any request idlist present then success
            //This map will get the incontext groupid mapping only if it is shared group.
            Boolean isValidGroup = false;

            Map<Integer, List<Integer>> idIncontextIdMap = new HashMap<>();
            idIncontextIdMap.put(idType, inContextIds);

            //getting the group member list
            List<String> existingMemList = sublistInfoUtil.selectGroupMemberList(groupIdInt, xdmsHome, persisterTxn);

            if (idType == 2 && existingMemList.isEmpty()) {
                List<KnIdDetailsDTO> banDetails = xdmServerDAO.getBanDetailsByBanFanId(inContextIds, idType, persisterTxn);
                if (!banDetails.isEmpty()) {
                    List<Integer> banIds = banDetails.stream()
                            .map(KnIdDetailsDTO::getIdKey)
                            .collect(Collectors.toList());
                    idIncontextIdMap.put(1, banIds);
                }
            }
            var sharedGroupIdContextIdMap = xdmServerDAO.sharedGroupIdContextIdMap(groupIdInt, idIncontextIdMap, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "sharedGroupIdContextIdMap-->", sharedGroupIdContextIdMap, "requestCorpId:", corpId, "dbCorpId: ", dbCorpid);
            if (!sharedGroupIdContextIdMap.isEmpty()) {
                List<KnCorpGroupDTO> groupDTOList = sharedGroupIdContextIdMap.get(idType);
                for (KnCorpGroupDTO groupDTO : groupDTOList) {
                    int idValue = groupDTO.getIdValue();
                    int idTypeValue = groupDTO.getIdType();
                    // we are returning idvalues either it is FAN / BAN
                    if (idIncontextIdMap.get(idTypeValue).contains(idValue)) {
                        isValidGroup = true;
                        break;
                    }
                }
            }
            knLogger.debug(methodName, "isValidGroup: ", isValidGroup);

            //2.Check the group membership from subscriber additional table based on group member and inList
            if (!isValidGroup) {
                //incontext id validation with members and if members not present then isEmpty is setting as true .
                int validMemCount = 0;
                int memCount = 0;
                var existingMemListBatch = KnGeneralUtil.splitList(existingMemList, BULK_UPDATE_SIZE);
                for (var existingMemLists : existingMemListBatch) {
                    validMemCount = xdmServerDAO.isGroupValidRequest(Integer.parseInt(groupId), inContextIds, idType, existingMemLists, persisterTxn);
                    memCount = memCount + validMemCount;
                }
                if (memCount > 0) {
                    isValidGroup = true;
                }
            }

            if (!isValidGroup) {
                knLogger.error(methodName, "Group doesn't belong to the inContext", "groupId::", groupId, "corpId::", corpId);
                throw new KnCorpBOValidationException(NON_FIRSTNET_FAN,
                        "Invalid owner context ids", "", "", "", "DataType", "");
            }
        }
    }

    public String selectCorpFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "selectCorpFS(int corpId, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn)";
        try {
            String xdmsHome = KnGeneralUtil.getXDMServerPttServerId();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.selectCorpFS(corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving master list", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateDispMem(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateDispMem(mdnList, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn)";
        try {
            String xdmsHome = KnGeneralUtil.getXDMServerPttServerId();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            corpXdmDao.updateDispMem(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving master list", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void getHierarchyDetails(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        String xdmsHome = KnGeneralUtil.getXDMServerPttServerId();
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        corpXdmDao.getHierarchyDetails(corpId, removedHierarchy, persisterTxn);
    }

    public void getSubscriberCountForHierarchyDeletion(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        String xdmsHome = KnGeneralUtil.getXDMServerPttServerId();
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        corpXdmDao.getSubscriberCountForHierarchyDeletion(corpId, removedHierarchy, persisterTxn);
    }

    public void getGroupCountForHierarchyId(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException, KnXDMServerException {
        String xdmsHome = KnGeneralUtil.getXDMServerPttServerId();
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        corpXdmDao.getGroupCountForHierarchyId(corpId, removedHierarchy, persisterTxn);
    }

    public void deleteHierarchyDetails(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        String xdmsHome = KnGeneralUtil.getXDMServerPttServerId();
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        corpXdmDao.deleteHierarchyDetails(corpId, removedHierarchy, persisterTxn);
    }

    public void deleteHierarchyDepthDetails(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        String xdmsHome = KnGeneralUtil.getXDMServerPttServerId();
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        corpXdmDao.deleteHierarchyDepthDetails(corpId, removedHierarchy, persisterTxn);
    }

    public void deleteAnchorPocInfo(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        String xdmsHome = KnGeneralUtil.getXDMServerPttServerId();
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        corpXdmDao.deleteAnchorPocInfo(corpId, removedHierarchy, persisterTxn);
    }

    public void deleteHierarchyGeocodeMapping(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        String xdmsHome = KnGeneralUtil.getXDMServerPttServerId();
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        corpXdmDao.deleteHierarchyGeocodeMapping(corpId, removedHierarchy, persisterTxn);
    }

    public boolean isCorpHierarchyMapped(int corpId, String hierarchyId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "isCorpHierarchyMapped(int,String, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.isCorpHierarchyMapped(corpId, hierarchyId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving clusterID", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> fetchAllHierarchyWithDepth(String corpId, String hierarchyId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "fetchAllHierarchyWithDepth(String, String, String, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.fetchAllHierarchyWithDepth(corpId, hierarchyId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while fetching ancestor hierarchy map", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getClusterId(Set<String> geoCodes, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getClusterID(String groCode , KnPersisterTxn persisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getClusterId(geoCodes, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving clusterID", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, String> getPocHome(int corpId, String hierarchyId, List<Integer> clusterId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getClusterID(String groCode , KnPersisterTxn persisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getPocHome(corpId, hierarchyId, clusterId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving getPocHome", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertAnchorPocInfo(String corpId, String hierarchyId, int clusterId, String fetchedPocHome, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getClusterID(String groCode , KnPersisterTxn persisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.insertAnchorPocInfo(corpId, hierarchyId, clusterId, fetchedPocHome, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving getPocHome", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateAnchorPocInfo(String corpId, String hierarchyId, int clusterId, String fetchedPocHome, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateAnchorPocInfo(String, String, int, String, String, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.updateAnchorPocInfo(corpId, hierarchyId, clusterId, fetchedPocHome, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving getPocHome", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }


    public void updatePocSubsInfoBatch(List<KnAllocatePocSubsUpdateDTO> updatePocSubs, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        String methodName = "updatePocSubsInfoBatch(List<KnAllocatePocSubsUpdateDTO> , String,KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.updatePocSubsInfoBatch(updatePocSubs, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while updating the subscribers", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public String getOpsCorpFS(int corpId, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getOpsCorpFS(int , String , boolean , KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY :", "corpId", corpId, "xdmsHomePttId:", xdmsHomePttId);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.selectOpsCorpFS(corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving master list", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnCorpSubscriberDTO> getSubscriberDetails(List<String> mdnList, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        String methodName = "getSubscriberDetails(List<String>, String,boolean, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscriberDetails(mdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving the subscriber ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteHierarchyInfo(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        String methodName = "deleteHierarchyInfo(String corpId, List<String> removedHierarchy, KnCorpCommonInfoUtil commonInfoUtil, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY - corpId:", corpId, "removedHierarchy:", removedHierarchy);
        deleteHierarchyDepthDetails(corpId, removedHierarchy, persisterTxn);
        deleteAnchorPocInfo(corpId, removedHierarchy, persisterTxn);
        deleteHierarchyGeocodeMapping(corpId, removedHierarchy, persisterTxn);
        deleteHierarchyDetails(corpId, removedHierarchy, persisterTxn);
        knLogger.info(methodName, " Hierarchy info deleted successfully for corpId:", corpId, "removedHierarchy:", removedHierarchy);
    }


    public static String getHierachyRoot(int corpId, String hierarchyId, KnPersisterTxn persisterTxn, String xdmPttServerId) throws KnCorpBOException {
        String methodName = "getHierachyRoot(int corpId, String hierarchyId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "==>ENTRY :", "corpId", corpId, "hierarchyId:", hierarchyId);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmPttServerId);
            return corpXdmDao.selectAnsestorID(corpId, hierarchyId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving getHierachyRoot", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void validateAdditionalHierarchyResources(String corpId, List<String> hierarchyIds,
            Map<String, String> hierarchyNameMap, List<Integer> corpIds,
            String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        String methodName = "validateAdditionalHierarchyResources()";
        knLogger.info(methodName, "ENTRY : corpId=", corpId, " hierarchyIds count=", hierarchyIds.size());

        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);

        Map<String, Integer> groupProfileViolations = corpXdmDao.checkGroupProfilesForHierarchyDeletion(corpId, hierarchyIds, persisterTxn);
        if (!groupProfileViolations.isEmpty()) {
            String hierarchyNames = groupProfileViolations.keySet().stream()
                    .map(id -> hierarchyNameMap.getOrDefault(id, id))
                    .collect(java.util.stream.Collectors.joining(", "));
            knLogger.error(methodName, "Group profile violation: hierarchyIds=", groupProfileViolations.keySet());
            throw new KnXDMServerException(
                    com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.HIERARCHY_HAS_ASSOCIATED_GROUP_PROFILES,
                    "Cannot delete the following organization(s) [" + hierarchyNames + "] as they have associated group profiles. Remove or reassign the group profiles before deleting.");
        }


        List<String> userProfileViolatingNames = new java.util.ArrayList<>();
        for (int i = 0; i < hierarchyIds.size(); i += 100) {
            List<String> batch = hierarchyIds.subList(i, Math.min(i + 100, hierarchyIds.size()));
            for (String hierarchyId : batch) {
                if (hierarchyId == null) continue;
                Set<String> userProfileIds = corpXdmDao.getUserProfileIdsByHierarchyId(corpIds, hierarchyId);
                if (userProfileIds != null && !userProfileIds.isEmpty()) {
                    knLogger.error(methodName, "User profile violation: hierarchyId=", hierarchyId, " count=", userProfileIds.size());
                    userProfileViolatingNames.add(hierarchyNameMap.getOrDefault(hierarchyId, hierarchyId));
                }
            }
        }
        if (!userProfileViolatingNames.isEmpty()) {
            String hierarchyNames = String.join(", ", userProfileViolatingNames);
            throw new KnXDMServerException(
                    com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.HIERARCHY_HAS_ASSOCIATED_USER_PROFILES,
                    "Cannot delete the following organization(s) [" + hierarchyNames + "] as they have associated user profiles. Remove or reassign the user profiles before deleting.");
        }


        Map<String, Integer> subListViolations = corpXdmDao.checkSubListsForHierarchyDeletion(corpId, hierarchyIds, persisterTxn);
        if (!subListViolations.isEmpty()) {
            String hierarchyNames = subListViolations.keySet().stream()
                    .map(id -> hierarchyNameMap.getOrDefault(id, id))
                    .collect(java.util.stream.Collectors.joining(", "));
            knLogger.error(methodName, "SubList violation: hierarchyIds=", subListViolations.keySet());
            throw new KnXDMServerException(
                    com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.HIERARCHY_HAS_ASSOCIATED_SUBLISTS,
                    "Cannot delete the following organization(s) [" + hierarchyNames + "] as they have associated sublists. Remove or reassign the sublists before deleting.");
        }


        Map<String, Integer> osmListInfoViolations = corpXdmDao.checkOSMListInfoForHierarchyDeletion(hierarchyIds, persisterTxn);
        if (!osmListInfoViolations.isEmpty()) {
            String hierarchyNames = osmListInfoViolations.keySet().stream()
                    .map(id -> hierarchyNameMap.getOrDefault(id, id))
                    .collect(java.util.stream.Collectors.joining(", "));
            knLogger.error(methodName, "OSMListInfo violation: hierarchyIds=", osmListInfoViolations.keySet());
            throw new KnXDMServerException(
                    com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.HIERARCHY_HAS_ASSOCIATED_OSM_LIST_INFO,
                    "Cannot delete the following organization(s) [" + hierarchyNames + "] as they have associated OSM list info. Remove or reassign the OSM list info before deleting.");
        }


        Map<String, Integer> osmListViolations = corpXdmDao.checkOSMListForHierarchyDeletion(corpId, hierarchyIds, persisterTxn);
        if (!osmListViolations.isEmpty()) {
            String hierarchyNames = osmListViolations.keySet().stream()
                    .map(id -> hierarchyNameMap.getOrDefault(id, id))
                    .collect(java.util.stream.Collectors.joining(", "));
            knLogger.error(methodName, "OSMList violation: hierarchyIds=", osmListViolations.keySet());
            throw new KnXDMServerException(
                    com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.HIERARCHY_HAS_ASSOCIATED_OSM_LIST,
                    "Cannot delete the following organization(s) [" + hierarchyNames + "] as they have associated OSM lists. Remove or reassign the OSM lists before deleting.");
        }

        knLogger.info(methodName, "EXIT : all additional hierarchy resource checks passed");
    }

    public Map<String,Set<String>> getHierarchyGeoMappedGeocode(int corpId, String hierarchyId, KnPersisterTxn persisterTxn, String xdmPttServerId) throws KnCorpBOException {
        var methodName = "getHierarchyMappedGeocode(int corpId, String hierarchyId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "==>ENTRY :", "corpId", corpId, "hierarchyId:", hierarchyId);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmPttServerId);
            return corpXdmDao.getHierarchyMappedGeocode(corpId, hierarchyId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving getHierarchyMappedGeocode", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

}
