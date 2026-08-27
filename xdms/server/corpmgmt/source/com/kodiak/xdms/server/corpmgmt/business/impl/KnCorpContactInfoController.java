/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
    /**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpContactInfoController.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 11, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.business.impl;


    import com.kodiak.common.dao.KnDAOException;
    import com.kodiak.common.dao.KnPersisterTxn;
    import com.kodiak.common.exception.KnException;
    import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
    import com.kodiak.common.resources.KnGDPRTemplate;
    import com.kodiak.common.resources.KnGeneralUtil;
    import com.kodiak.logger.KnLogger;
    import com.kodiak.utilities.lieventhandler.dto.KnLIEventDTO;
    import com.kodiak.utilities.lieventhandler.handler.KnLIConstants;
    import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
    import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
    import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
    import com.kodiak.xdms.server.common.dao.KnFactorySelector;
    import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
    import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
    import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
    import com.kodiak.xdms.server.common.dto.common.*;
    import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
    import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
    import com.kodiak.xdms.server.common.resources.KnProfileTypes;
    import com.kodiak.xdms.server.corpmgmt.business.ICorpContactInfoController;
    import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
    import com.kodiak.xdms.server.corpmgmt.business.helper.*;
    import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
    import com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants;
    import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
    import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
    import com.kodiak.xdms.server.corpmgmt.dto.common.*;
    import com.kodiak.xdms.server.corpmgmt.dto.impl.*;
    import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
    import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
    import com.kodiak.xdms.server.corpmgmt.dto.persistdat.*;
    import com.kodiak.xdms.server.corpmgmt.resources.*;

    import java.util.*;
    import java.util.stream.Collectors;

    import static com.kodiak.common.resources.KnConstants.*;
    import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT;
    import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;
    import static com.kodiak.xdms.server.common.resources.KnConstants.COMMON_CONTACTLIST_PERSUB;
    import static com.kodiak.xdms.server.common.resources.KnConstants.COMMON_CONTACTLIST_SIZE;
    import static com.kodiak.xdms.server.common.resources.KnConstants.*;
    import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
    import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
    import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populateXdmResponseFroomHook;
    import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;
    import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isObjectNull;
    import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isObjectNullOrEmpty;
    import static com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes.CORP_CONTACT_MANAGER;
    import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity;
    import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.MODIFY_SUBS_CONTACT_LIST;
    import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.REMOVE_SUBSCRIBERS_CONTACTS;

    public class KnCorpContactInfoController implements ICorpContactInfoController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpContactInfoController.class);

    private KnValidatorFramework validatorFW;
    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnGeneralUtil generalUtil;
    private KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private KnGenInfoUtil genInfoUtil;
    private KnCorpUserProfileUtil corpUserProfileUtil;

    public KnCorpContactInfoController() {
        contactInfoUtil = new KnCorpContactInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        commonInfoUtil = new KnCorpCommonInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        corpUserProfileUtil = new KnCorpUserProfileUtil();
        generalUtil = new KnGeneralUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
    }

    //1. Authorization - depends
    //2. Business validation
    // 2.1 Retrieve required data from server to process with the validations
    // 2.2 Retrieved data should be populated into some KnXxxPersisterDTO

    /**
     * 1. Validate if the CorpId is not found in DG.CorpInfo table then following message should be sent
     * Invalid Corporate profile
     * 2. If the Etag in the request and the Db i.e from table ------- are same send 304 response not modified
     * 3. If the Corporate CorpID is found in the DB table then the  Subscribers from the DG.PoCSubscriberInfo and
     * DG.ExtCorpContact table should be fetched.
     *
     * @param corpInfoDTO
     * @param persisterTxn
     * @return
     */
    public KnCorpContactListRespDTO getCorpMasterList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) {

        String methodName = "getCorpMasterList(KnIPCorpInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", corpInfoDTO.getCorpId());

        //Step:
        //creating Response DTO object
        KnCorpContactListRespDTO respDTO = new KnCorpContactListRespDTO();
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

            long etag = contactInfoUtil.getCorporateEtag(corpId, xdmsHomePttId, true, persisterTxn);

            KnCorpInfoPersistDTO persistDTO = new KnCorpInfoPersistDTO();
            int internalSubscriberCount = contactInfoUtil.getInternalSubscriberCount(corpId, xdmsHomePttId, true, persisterTxn);
            int externalSubscriberCount = contactInfoUtil.getExternalSubscriberCount(corpId, KnConstants.ContactType.EXTERNAL_SUBSCRIBER, xdmsHomePttId, true, persisterTxn);
            int nniSubscriberCount = contactInfoUtil.getExternalSubscriberCount(corpId, KnConstants.ContactType.NNI_SUBSCRIBER, xdmsHomePttId, true, persisterTxn);
            // CorpContactSize will be used by validation framework for
            // validating max limit of corporate size, MAX_CORPORATE_SIZE is configured
            // in System Env, This validation applicable only if filterType = 0
            persistDTO.setCorpContactSize(internalSubscriberCount + externalSubscriberCount + nniSubscriberCount);
            persistDTO.setInputDTO(corpInfoDTO);
            String etagStr = String.valueOf(etag);
            persistDTO.setEtag(etagStr);

            //Checking the Blocked Corporate
            Map<String, String> configMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            knLogger.debug(methodName, "Blocked Corporate MAP", configMap);
            String extIds = configMap.get(KnConstants.IDS_BLOCKED_CAT_CORP_LIST);
            List<String> extCorpIdList = new ArrayList<>();
            if (extIds != null) {
                String[] extCorpValue = extIds.split(";");
                extCorpIdList.addAll(Arrays.asList(extCorpValue));
            }
            knLogger.debug(methodName, "Blocked Corporate list ", extCorpIdList);
            knLogger.debug(methodName, "extCorpId ", extCorpID);
            persistDTO.setExtCorpIdList(extCorpIdList);
            persistDTO.setExtCorpId(extCorpID);
            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.info(methodName, "Validation completed Successfully.");

            knLogger.debug(methodName, "Before DB call to get the corporate Master List");
            switch (corpInfoDTO.getFilterType()) {
                case 0:
                    // Return List of Internal/External/NNI Subscriber, Pagination is not applicable in case of FilterType = 0
                    respDTO = contactInfoUtil.getCorpMasterList(corpId, xdmsHomePttId, true, persisterTxn);
                    break;
                case 1:
                    // Return List of Internal
                    respDTO = contactInfoUtil.getInternalSubscriberList(corpId, corpInfoDTO.getFilterType(), corpInfoDTO.getFetchSize(), corpInfoDTO.getNextToken(), corpInfoDTO.getSortType(), xdmsHomePttId, true, persisterTxn);
                    respDTO.setCount(internalSubscriberCount);
                    break;
                case 2:
                    // Return List of ExternalSubscriber
                    respDTO = contactInfoUtil.getExternalSubscriberList(corpId, corpInfoDTO.getFilterType(), corpInfoDTO.getFetchSize(), corpInfoDTO.getNextToken(), corpInfoDTO.getSortType(), xdmsHomePttId, true, persisterTxn);
                    respDTO.setCount(externalSubscriberCount);
                    break;
                case 3:
                    // Return List of NNI Subscriber
                    respDTO = contactInfoUtil.getNniSubscriberList(corpId, corpInfoDTO.getFilterType(), corpInfoDTO.getFetchSize(), corpInfoDTO.getNextToken(), corpInfoDTO.getSortType(), xdmsHomePttId, true, persisterTxn);
                    respDTO.setCount(nniSubscriberCount);
                    break;
                case 4:
                    // Return List of Internal Interop Subscriber
                    respDTO = contactInfoUtil.getInternalInteropSubscriberList(corpId, 4, corpInfoDTO.getFetchSize(), corpInfoDTO.getNextToken(), corpInfoDTO.getSortType(), xdmsHomePttId, true, persisterTxn);
                    respDTO.setCount(internalSubscriberCount);
                    break;
            }
            //get the third party client type
            if(respDTO.getThirdPartySubsc()!= null && !respDTO.getThirdPartySubsc().isEmpty()){
                IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmsHomePttId);
                Map<String, KnTPUserAccountDTO> tpUserAccountDTOs = commonXDMServerDAO.retrieveTPUserAccountForMDNs(respDTO.getThirdPartySubsc(), true, persisterTxn);
                respDTO.setThirdPartyDetails(tpUserAccountDTOs);
            }
            respDTO.setEtag(etagStr);
            populate(respDTO);
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while retrieving MasterList - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving MasterList - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving MasterList - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }


    /**
     * 1. Validate if the CorpId is not found in DG.CorpInfo table then following message should be sent
     * Invalid Corporate profile
     * 2. Verify that the subscriber passed is part of the corpId passed.
     * 3. If no contacts found for the subscriber retuirn No contacts found for Subscriber.
     * The contacts are to fetched from DG.CorpListDistInfo table for the subList distributed to it,
     * and DG.CorpListMemberTable for the private members
     *
     * @param contactDTO
     * @param persisterTxn
     * @return
     */
    public KnCorpSubscContactListRespDTO getCorpSubscContactList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getCorpSubscContactList(KnIPCorpContactDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactDTO.getCorpId(), KnGDPRTemplate.mdn(contactDTO.getMdn()));

        //Step:
        //creating Response DTO object
        KnCorpSubscContactListRespDTO respDTO = new KnCorpSubscContactListRespDTO();
        try {
            int corpId = contactDTO.getCorpId();
            knLogger.debug(methodName, "Corp Id passed in the request is - ", corpId);

            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            knLogger.debug(methodName, "Xdm home retrieved from the coporate profile is - ", xdmsHomePttId);

            knLogger.debug(methodName, "Fetch the subscribers profile if cached or fetch from the DB the details");
            String mdn = contactDTO.getMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    true, persisterTxn);

            if (contactDTO.getCorpId() > 0 && subscProfile.getCorpId() != contactDTO.getCorpId()) {
                throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                        "Subscribers Does not belong to the corporation.");
            }
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
            hookIPDTO.setAction(KnActions.ACTIONS.GET_SUBSC_CONTACT_DETAILS);
            hookIPDTO.setData(contactDTO);
            Map<String, Object> customParams = contactDTO.getCustomParamMap();
            if (contactDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), customParams, xdmsHomePttId, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                contactDTO.setCustomParamMap(customParams);
                knLogger.debug(methodName, "corpProfile.getCorpMasterListEtag() - ", corpProfile.getCorpMasterListEtag());
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                knLogger.debug(methodName, "Call before Hook");
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    knLogger.debug(methodName, "Call After Hook");
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            //Prepare subscriberMDN - clientType Map for validation
            Map<String, Integer> subsMdnClientTypeMap = new HashMap<String, Integer>();
            subsMdnClientTypeMap.put(mdn, subscProfile.getClientType());
            //get the Subscriber profile details from cache
            int subscribersCount = contactInfoUtil.getSubscribersCount(mdn, corpId, true, xdmsHomePttId, persisterTxn);

            int etag = contactInfoUtil.getSubscriberResourceListEtag(mdn, xdmsHomePttId, true, persisterTxn);
            KnContactDetailsPersistDTO valPersistDTO = new KnContactDetailsPersistDTO();
            valPersistDTO.setSubscriberCount(subscribersCount);
            valPersistDTO.setEtag(etag);
            valPersistDTO.setMdnClientTypeMap(subsMdnClientTypeMap);
            valPersistDTO.setInputDTO(contactDTO);
            knLogger.debug(methodName, "Invoking Validation FW - ", valPersistDTO);
            validatorFW.validate(valPersistDTO);
            knLogger.debug(methodName, "Validation completed successfully");
            knLogger.debug(methodName, "Before DB call to get the subscribers contact List.");
            respDTO = contactInfoUtil.getCorpSubscContactList(contactDTO, subscProfile.getContactListId(),
                    corpProfile.getMaxContactsPerSubsc(), xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "After DB call to get the subscribers contact List.");

            Collection<String> mdnList = new ArrayList<String>();
            mdnList.add(mdn);
            Map<String, String> contactCountMap =
                    contactInfoUtil.getAllSubscriberContactCount(mdnList, xdmsHomePttId, true, persisterTxn);
            int contactCount = 0;
            String countStr = contactCountMap.get(mdn);
            if (countStr != null) {
                contactCount = Integer.parseInt(countStr);
            }
            respDTO.setTotalContacts(contactCount);
            respDTO.setMaxContactLimitFlag(KnCorpUtil.determineMaxContactLimitFlag
                    (contactCount, corpProfile.getMaxContactsPerSubsc()));
            respDTO.setEtag(String.valueOf(etag));
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }

            // Map of MDN and its group count
            Map<String, Integer> subscriberGroupList = groupInfoUtil.getSubsScrGroupCountExceptABDG(mdnList.stream().toList(), xdmsHomePttId, persisterTxn, corpId);
            int totalGroups = 0;
            if ((subscriberGroupList != null) && (subscriberGroupList.get(mdn) != null)) {
                totalGroups = subscriberGroupList.get(mdn);
            }

            // Map of USER PROFILE ID to IS DEFAULT PROFILE
            Map<String, Integer> subscriberUserProfileList = corpUserProfileUtil.getSubscriberUserProfileList(subscProfile.getMcId(), xdmsHomePttId, true, persisterTxn);
            int totalUpms = 0;
            if (subscriberUserProfileList != null) {
                totalUpms = (int) subscriberUserProfileList.entrySet().stream().filter(entry -> (entry.getKey() != null)).count();
            }
            knLogger.debug(methodName, "MDN - ", mdn, "Total Groups - ", totalGroups, " Total UPMS - ", totalUpms);

            respDTO.setTotalGroups(totalGroups);
            respDTO.setTotalUpms(totalUpms);
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while retrieving subscribers ",
                    "contact list - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving the ",
                    "subscribers contact list - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while retrieving the subscribers contact list - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT:Returning Response - ", respDTO);
        return respDTO;
    }

    /**
     * This method will be used to get the corp resource list for the subscribers.This API is usd by xcap.
     *
     * @param contactDTO
     * @param persisterTxn
     * @return
     */
    public KnCorpSubscContactListRespDTO getCorpResourceList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getCorpResourceList(KnIPCorpContactDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactDTO.getMdn());

        //Step:
        //creating Response DTO object
        KnCorpSubscContactListRespDTO respDTO = new KnCorpSubscContactListRespDTO();
        try {
            knLogger.debug(methodName, "Fetch the subscribers profile if cached or fetch from the DB the details");
            String mdn = contactDTO.getMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                    false, persisterTxn);

            if (CLIENT_TYPE_CAT_UI == contactDTO.getClientType()) {
                if (contactDTO.getCorpId() > 0 && subscProfile.getCorpId() != contactDTO.getCorpId()) {
                    throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                            "Subscribers Does not belong to the corporation.");
                }
            }
            contactDTO.setProtocolVersion(subscProfile.getProtocolVersion());
            knLogger.debug(methodName, "subscProfile - ", subscProfile);

            int corpId = subscProfile.getCorpId();
            knLogger.debug(methodName, "corpId - ", corpId);

            boolean isProfilemdn = false;
            if(subscProfile.getUserProfileIndex() != 0) {
                isProfilemdn = true;
            }
            if (corpId <= 0) {
                knLogger.error(methodName, "Invalid Corporate Subscriber passed. MDN - ", KnGDPRTemplate.mdn(mdn));
                throw new KnCorpBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
            }
            if (contactDTO.getClientType() != CLIENT_TYPE_CAT_UI) {
                contactDTO.setCorpId(corpId);
            } else {
                corpId = contactDTO.getCorpId();
            }
            String xdmsHomePttId = subscProfile.getXdmsHome();
            knLogger.debug(methodName, "Xdm home retrieved from the coporate profile is - ", xdmsHomePttId);
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Corporate Profile - ", corpProfile);
            //Invoking custom hook if the request comes from CATUI
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            if (contactDTO.getClientType() == CLIENT_TYPE_CAT_UI) {
                Map<String, Object> customParams = contactDTO.getCustomParamMap();
                if (contactDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                    contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), customParams, xdmsHomePttId, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(KnPersisterConstants.PTT_SERVER_ID, xdmsHomePttId);
                    contactDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.GET_CORP_RESOURCE_LIST);
                    hookIPDTO.setData(contactDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, respDTO);
                            return respDTO;
                        }
                    }
                }
            }
            KnContactDetailsPersistDTO valPersistDTO = new KnContactDetailsPersistDTO();
            int etag = contactInfoUtil.getSubscriberResourceListEtag(mdn, xdmsHomePttId, true, persisterTxn);
            valPersistDTO.setEtag(etag);
            valPersistDTO.setInputDTO(contactDTO);
            valPersistDTO.setSubsCorpId(corpId);
            valPersistDTO.setMcpttCompliance(subscProfile.getMcpttCompliance());
            valPersistDTO.setMcpttId(subscProfile.getMcpttId());
            knLogger.debug(methodName, "Invoking Validation FW - ", valPersistDTO);
            validatorFW.validate(valPersistDTO);
            knLogger.debug(methodName, "Validation completed successfully");

            knLogger.debug(methodName, "Before DB call to get the subscribers contact List.");
            respDTO = contactInfoUtil.getCorpResourceList(contactDTO, corpProfile.getMaxContactsPerSubsc(),
                    xdmsHomePttId, persisterTxn, isProfilemdn,subscProfile.getClientMajorVersion());
            knLogger.debug(methodName, "After DB call to get the subscribers contact List.");

            //Check Requesting mdn common contact feature bit 121 is enabled in activefs2
            boolean isCommonContactEnabled = false;
            if(subscProfile.getSubscriberFS2() != null && contactDTO.getClientType() == com.kodiak.common.resources.KnConstants.CLIENT_TYPE_XCAP){
                isCommonContactEnabled = KnGeneralUtil.getFeatureBitValue(subscProfile.getSubscriberFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.COMMON_CONTACT_LIST.value());
            }
            //Remove Common Contacts if feature bit is disabled in activefs2
            if(!isCommonContactEnabled && contactDTO.getClientType() == com.kodiak.common.resources.KnConstants.CLIENT_TYPE_XCAP){
                List<KnCorpSubscriberDTO> contactLists  =respDTO.getContactList();
                knLogger.debug(methodName, "Before removing common contacts:",contactLists);
                if(contactLists != null && !contactLists.isEmpty()) {
                    contactLists.removeIf(contact -> contact.getCommonContact() == ENABLE);
                    knLogger.debug(methodName, "After removing common contacts:", contactLists);
                    respDTO.setContactList(contactLists);
                }
            }

            respDTO.setEtag(String.valueOf(etag));
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }

        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while retrieving subscribers contact list - ", e);
            populate(respDTO, e);

        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving the ",
                    "subscribers contact list - ", e);
            populate(respDTO, e);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while retrieving the subscribers contact list - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT:Returning Response - ", respDTO);
        return respDTO;
    }

    /**
     * This method is used to modify the subscribers contact details based on the input details passed.
     *
     * @param contactListDTO this holds the new contact details for the subscribers.
     * @param persisterTxn
     * @return respDTO
     */
    public KnCorpResponseDTO modifyCorpSubscContacts(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {

        String methodName = "modifyCorpSubscContacts(contactListDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactListDTO);

        //Step: creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            int corpId = contactListDTO.getCorpId();

            //Step:get the Corp profile details from cache

            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //Step: IDTYPEget the Subscriber profile details from cache

            knLogger.debug(methodName, "Fetch the subscProfile profile if cached or fetch from the DB the details");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(contactListDTO.getSubscriberMdn(),
                    KnProfileTypes.PUBLIC_PROFILE, true, persisterTxn);
            knLogger.info(methodName, "subscProfile Profile - ", subscProfile);

            //use the xdms home pttServerId from Corp profile

            String xdmsHomePttId = corpProfile.getXdmsHome();

            String userProfileId = subscProfile.getUserProfileId();
            knLogger.debug(methodName," userProfileId:",userProfileId);
            boolean isUPMSharingEnable = false;
            ArrayList<String> upmId = new ArrayList<>();
            upmId.add(userProfileId);
            Map<String, Integer> upmOwnerList = contactInfoUtil.getUserProfileOwnerinfo(upmId, xdmsHomePttId, persisterTxn);
            if(!upmOwnerList.isEmpty()){
                isUPMSharingEnable = true;
            }
            knLogger.debug(methodName,"isUPMSharingEnable :",isUPMSharingEnable);

            //Invoking custom hook

            knLogger.debug(methodName, "Custom Call");
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = contactListDTO.getCustomParamMap();
            if (contactListDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(contactListDTO.getSubscriberMdn()), customParams, xdmsHomePttId, persisterTxn);
                //TODO: Added/Removed contact list Mdn also in this list
                //TODO: Add/Removed Sublist Mdn also in this list
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                knLogger.debug(methodName, "customParams after the change - ", customParams);
                contactListDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.MODIFY_SUBSC_CONTACT);
                hookIPDTO.setData(contactListDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                knLogger.debug(methodName, "Call before Hook.Process Invoker - ", processInvoker);
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    knLogger.debug(methodName, "Call After Hook");
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }


            int privateListId = subscProfile.getContactListId();
            int subscribersCount = 0;
            if(isUPMSharingEnable){
                knLogger.debug(methodName, "Call to getSubscribersCount when isUPMSharingEnable- ", isUPMSharingEnable);
                subscribersCount = contactInfoUtil.getSubscribersCount(contactListDTO.getSubscriberMdn(),
                        subscProfile.getCorpId(), xdmsHomePttId, persisterTxn);
            } else {
               subscribersCount = contactInfoUtil.getSubscribersCount(contactListDTO.getSubscriberMdn(),
                        corpId, xdmsHomePttId, persisterTxn);
            }

            //Step:
            //preparing persistDto
            KnCorpMdnListPersistDTO corpMdnListPersistDto = new KnCorpMdnListPersistDTO();
            corpMdnListPersistDto.setCorpId(corpId);
            corpMdnListPersistDto.setAddedMdnList(contactListDTO.getAddedMdnList());
            corpMdnListPersistDto.setMdn(contactListDTO.getSubscriberMdn());
            corpMdnListPersistDto.setRemovedMdnList(contactListDTO.getRemovedMdnList());

            String mdn = subscProfile.getMdn();
            int subscriberDocEtag = contactInfoUtil.getSubscribersDocumentEtag(mdn, xdmsHomePttId, persisterTxn);
            //Step:
            //get mdn details for added mdn list, also filters out the external contacts present if any
            knLogger.debug(methodName, "Before getPoCSubscriberInfo for input corpMdnListPersistDto - ",
                    corpMdnListPersistDto);
            KnMdnDetailsPersistDTO pocMdnPersistDto = contactInfoUtil.getPoCSubscriberInfo(corpMdnListPersistDto,
                    xdmsHomePttId, persisterTxn);
           knLogger.debug(methodName, "pocMdnPersistDto - ",pocMdnPersistDto.getAddedMdnDTO());

            knLogger.debug(methodName, "After getPoCSubscriberInfo result obtained PocSubscriberList- ",
                    pocMdnPersistDto);

            //Step:
            //setting the external contacts into input dto
            contactListDTO.setExternalContacts(corpMdnListPersistDto.getAddedExternalMdnList());
            knLogger.debug(methodName, "Before getExternalConatctsInfo.");

            //todo comment
            //Step:
            //
            Collection<KnCorpSubscriberDTO> externalSubsList = corpMdnListPersistDto.getAddedExternalMdnList();
            if (!externalSubsList.isEmpty()) {
                KnMdnDetailsPersistDTO externalPocSubsPersistDto = contactInfoUtil.getExternalConatctsInfo(corpId,
                        externalSubsList, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "After getExternalConatctsInfo result obtained externalPocSubsPersistDto - ",
                        externalPocSubsPersistDto);
                externalSubsList = externalPocSubsPersistDto.getExternalMdnList();
            }

            //todo comment
            KnMdnDetailsPersistDTO contactMdnPersistDto = contactInfoUtil.getSubscriberPrivateContactListInfo(
                    corpMdnListPersistDto, privateListId, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "After getSubscriberContactListInfo result obtained Subscribers contact- "
                    , contactMdnPersistDto);
            KnCorpSublistListPersistDTO corpSublistList = new KnCorpSublistListPersistDTO();
            corpSublistList.setAddedSublistIds(contactListDTO.getAddedSublistIds());
            corpSublistList.setRemovedSublistIds(contactListDTO.getRemovedSublistIds());
            corpSublistList.setMdn(mdn);
            corpSublistList.setCorpId(corpId);

            knLogger.debug(methodName, "Before getSubsMappedSublistId for input corpSublistList - ", corpSublistList);
            KnSublistDetailsPersistDTO subsSublistIds = sublistInfoUtil.getSubsMappedSublistId(corpSublistList,
                    xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "After getSubsMappedSublistId result obtained subsSublistIds- ", subsSublistIds);
            //getting subist info for LISTDISTRIBUTIONPOLICY=3 or 5 for validation.
            KnSublistDetailsPersistDTO pocSublistIds = new KnSublistDetailsPersistDTO();
            Map<Integer, Integer> validSublistTypeInfo = new HashMap<>();
            //for the shared scenario for owner/shared fetching with the ownerid
            int tempCorpid = isUPMSharingEnable ? upmOwnerList.get(userProfileId) : corpSublistList.getCorpId();

            validSublistTypeInfo = sublistInfoUtil.getAllTypePoCSublistIdInfo(
                    corpSublistList.getAddedSublistIds(), tempCorpid, xdmsHomePttId, persisterTxn);

            if (!validSublistTypeInfo.isEmpty()) {
                pocSublistIds.setSublistIds(new ArrayList<>(validSublistTypeInfo.keySet()));
            }
            knLogger.debug(methodName, " valid sublist info ", validSublistTypeInfo);

            knLogger.debug(methodName, "After getPoCSublistIdInfo result obtained pocSublistIds- ", pocSublistIds);
            KnIPCorpContactDTO corpContactDTO = new KnIPCorpContactDTO();
            corpContactDTO.setMdn(contactListDTO.getSubscriberMdn());
            corpContactDTO.setCorpId(contactListDTO.getCorpId());
            Collection<KnCorpSublistDTO> dbSublistListIds = contactInfoUtil.getSubscMappedSublistList
                    (corpContactDTO, subscProfile.getContactListId(), xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "After get Sublist present for the subscriber - ", dbSublistListIds);

            Collection<Integer> sublistids = new ArrayList<Integer>();
            for (KnCorpSublistDTO sublist : dbSublistListIds) {
                sublistids.add(sublist.getSublistId());
            }
            KnSublistDetailsPersistDTO dbSublistList = new KnSublistDetailsPersistDTO();
            dbSublistList.setSublistIds(sublistids);

            knLogger.debug(methodName, "Before filtering the sublist. DB sublist- ", dbSublistList,
                    ", pocSublist - ", pocSublistIds, ", subscribersSublist - ", subsSublistIds);
            Collection<Integer> finalSublistListInDB = KnCorpCommonInfoUtil.filterOutFinalSublistList(dbSublistList,
                    pocSublistIds, subsSublistIds);
            knLogger.debug(methodName, "After filtering the sublist  - ", finalSublistListInDB);

            validSublistTypeInfo.putAll(dbSublistListIds.stream().filter(
                    sublistIds -> finalSublistListInDB.contains(sublistIds.getSublistId())
            ).collect(Collectors.toMap(KnCorpSublistDTO::getSublistId, KnCorpSublistDTO::getListDistribution)));

            knLogger.debug(methodName," Total sublists ", validSublistTypeInfo);

            List<KnCorpSubscriberDTO> privateSubscrMemberList = contactInfoUtil.getSubscPrivateMemberList(
                    subscProfile.getContactListId(), xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "After Fetching subscribers private Members details - ",
                    privateSubscrMemberList);
            Collection<KnCorpSubscriberDTO> subscList = new ArrayList<KnCorpSubscriberDTO>();
            List<String> allInputMDNList = new ArrayList<>(pocMdnPersistDto.getMdnList());  //Adding all input mdn list.
            allInputMDNList.addAll(pocMdnPersistDto.getNniSubscrList());
            for (String mdnStr : allInputMDNList) {
                KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                subsc.setMdn(mdnStr);
                if (externalSubsList.contains(subsc)) {
                    continue;
                } else {
                    subsc.setCorpId(corpId);
                }
                subscList.add(subsc);
            }
            if (externalSubsList != null && !externalSubsList.isEmpty()) {
                subscList.addAll(externalSubsList);
            }
            knLogger.debug(methodName, "subscList - ", subscList);
            knLogger.debug(methodName, "Before filtering the members to be added in db. privateSublistDetails - "
                    , privateSubscrMemberList, " , Poc Mdn List - ", KnGDPRTemplate.mdnList(pocMdnPersistDto.getMdnList()), "external mdn ",
                    pocMdnPersistDto.getExternalMdnList());
            knLogger.debug(methodName, "privateSubscrMemberList - ", privateSubscrMemberList);
            knLogger.debug(methodName, "subscList - ", subscList);
            knLogger.debug(methodName, "externalSubsList - ", externalSubsList);


            Collection<KnCorpSubscriberDTO> mdnsToBeAddedToPrivateList = KnCorpCommonInfoUtil.filterOutTheNewlyAddedMebers(
                    privateSubscrMemberList, subscList, externalSubsList);
            knLogger.debug(methodName, "After filtering the members to be added in db.Members to be added - ",
                    mdnsToBeAddedToPrivateList);

            Collection<String> finalMdnInPrivateList = KnCorpCommonInfoUtil.filterFinalPrivateListMembers(
                    privateSubscrMemberList, mdnsToBeAddedToPrivateList, contactMdnPersistDto);



            Collection<Integer> sublistIdsList = contactInfoUtil.filterFinalSublistToBeAddedInDb(finalSublistListInDB,
                    dbSublistList);

            //Collection<KnCorpSubscriberDTO> membersList = contactInfoUtil.getSubscribersInfo(contactListDTO.getAddedMdnList(), xdmsHomePttId, persisterTxn);
            //Get the distinct members from added sublist Id list
            List<KnCorpSubscriberDTO> distAddedSublistMemLst = sublistInfoUtil.getSublistDistinctMembers(contactListDTO.getAddedSublistIds(), corpId, xdmsHomePttId, persisterTxn);
            //Get the distinct added members among added sublis id list and added member list
            Collection<KnCorpSubscriberDTO> subscriberList =
                    contactInfoUtil.getDistinctMembersList(pocMdnPersistDto.getAddedMdnDTO(), distAddedSublistMemLst, corpId, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "subscriberList determination - ", subscriberList);

             /*
            Get the distinct profileId list for all external contact in the request MDN list
            And get the profile details for those list for validation of featureBit
*/
            List<KnCorpSubscriberDTO> extSubsList = new ArrayList<KnCorpSubscriberDTO>();
            for (KnCorpSubscriberDTO subs : subscriberList) {
                if (subs.getCorpId() != corpId) {
                    subs.setExternalContact(Boolean.TRUE);
                    extSubsList.add(subs);
                }
            }
            List<Integer> profileIdList = contactInfoUtil.getExtSubsrProfilelist(extSubsList, xdmsHomePttId, persisterTxn);
            Map<Integer, KnExtProfileDetails> extProfileDetailsMap = commonInfoUtil.getExtProfileDetails(profileIdList, xdmsHomePttId, persisterTxn);

            int deletedMembersCount = 0;
            if (!isObjectNullOrEmpty(contactListDTO.getRemovedMdnList())) {
                deletedMembersCount = contactListDTO.getRemovedMdnList().size();
            }

            //Prepare subscriberMDN - clientType Map for validation
            Map<String, Integer> subsMdnClientTypeMap = new HashMap<String, Integer>();
            subsMdnClientTypeMap.put(mdn, subscProfile.getClientType());
            KnContactDetailsPersistDTO valPersistDTO = new KnContactDetailsPersistDTO();
            int maxAllowedContacts = corpProfile.getMaxContactsPerSubsc();//getting the max count from DB
            int finalContactCount = corpProfile.getMaxContactsPerSubsc();//setting as default to bypass validation
            Map<String,Integer> countInfo = new HashMap<>();
            boolean isCommonContact = subscProfile.getSubscriberFS2() != null && KnGeneralUtil.getFeatureBitValue(subscProfile.getSubscriberFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.COMMON_CONTACT_LIST.value());
            Boolean isCommonContactListSupport = Boolean.FALSE;
            //skip if the bit is disabled
            if (isCommonContact) {
                //common sublist modification allowed if system and corp flag is enabled
                final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
                Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
                final String systemCmnContactListSupport = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SUPPORT) == null
                        ? "0" : microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SUPPORT);
                final Integer commonContactListSupportFlagOfCorpProfile = corpProfile.getCommonContactListSupport();
                if (systemCmnContactListSupport.equals(BIT_ENABLED) || commonContactListSupportFlagOfCorpProfile == Integer.valueOf(ENABLED)) {
                    isCommonContactListSupport = Boolean.TRUE;
                }
                knLogger.debug(methodName, " isCommonContact supported ", isCommonContactListSupport);
                valPersistDTO.setCommonContactEnabled(isCommonContactListSupport);
                final String sysCmnCntctListPerSubString = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_PERSUB);
                final int sysCmnCntctListPerSub = sysCmnCntctListPerSubString == null ? 0 : Integer.parseInt(sysCmnCntctListPerSubString);
                final String sysCmnCntctListSizePerSubString = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SIZE);
                final int sysCmnCntctListSizePerSub = sysCmnCntctListSizePerSubString == null ? 0 : Integer.parseInt(sysCmnCntctListSizePerSubString);
                valPersistDTO.setMaxAlloedCommonSublistPerSubs(sysCmnCntctListPerSub);
                valPersistDTO.setMaxAllowedCommonContactCount(sysCmnCntctListSizePerSub);
                knLogger.debug(methodName, "maxCommonSublistPerSub ", sysCmnCntctListPerSub, " commonSublistSizePerSub ", sysCmnCntctListSizePerSub);
            }
            valPersistDTO.setSubListIdInfo(validSublistTypeInfo);
            if ((contactListDTO.getAddedMdnList() != null && !contactListDTO.getAddedMdnList().isEmpty()) ||
                    (contactListDTO.getAddedSublistIds() != null && !contactListDTO.getAddedSublistIds().isEmpty())) {
                countInfo = contactInfoUtil.getFinalMemberContactCount(finalSublistListInDB,
                        finalMdnInPrivateList, mdn, xdmsHomePttId, persisterTxn);
                finalContactCount = countInfo.get(NORMAL_CONTACT_COUNT);
                if (isCommonContactListSupport) {
                    valPersistDTO.setCommonContactCount(countInfo.get(COMMON_CONTACT_COUNT));
                }
                knLogger.debug(methodName, " normalContactCount ", finalContactCount, " commonContactCount ", countInfo.get(COMMON_CONTACT_COUNT));
            }

            knLogger.debug(methodName, "Before setting of the validation framework object.");
            //settingof the validation persist DTO
            valPersistDTO.setInputDTO(contactListDTO);
            KnCorpSubscriberDTO subsDTO = new KnCorpSubscriberDTO();
            subsDTO.setMdn(mdn);
            subsDTO.setCorpId(corpId);
            valPersistDTO.setSubscDto(subsDTO);
            valPersistDTO.setSubscriberCount(subscribersCount);
            valPersistDTO.setEtag(subscriberDocEtag);
            valPersistDTO.setContactMdnList(contactMdnPersistDto.getMdnList());
            valPersistDTO.setPocSubscMdnList(pocMdnPersistDto.getMdnList());
            valPersistDTO.setPocSublistIds(pocSublistIds.getSublistIds());
            valPersistDTO.setSubscSublistIds(subsSublistIds.getSublistIds());
            valPersistDTO.setContactCount(finalContactCount);
            valPersistDTO.setExternalContacts(externalSubsList);
            valPersistDTO.setNewlyAddedPrivateMembers(mdnsToBeAddedToPrivateList);
            valPersistDTO.setMaxSubscribersContactLimit(maxAllowedContacts);
            valPersistDTO.setMaxAllowedContactCountPerRequest(corpProfile.getMaxContactsPerRequest());
            valPersistDTO.setContactCountInRequest(subscriberList.size() + deletedMembersCount);
            valPersistDTO.setMdnClientTypeMap(subsMdnClientTypeMap);
            valPersistDTO.setAddedMdnDTO(pocMdnPersistDto.getAddedMdnDTO());
            valPersistDTO.setConfiguredProfileMap(extProfileDetailsMap);
            valPersistDTO.setUPMSharingEnabled(isUPMSharingEnable);
            knLogger.debug(methodName, "After setting of the validation framework object ", "valPersistDTO - ", valPersistDTO);
            knLogger.info(methodName, "Invoking Validation FW - ", valPersistDTO);
            validatorFW.validate(valPersistDTO);
            knLogger.debug(methodName, "Validation completed successfully");

            Collection<KnCorpSubscriberDTO> removedMembersList = contactInfoUtil.getSubscribersInfo(contactListDTO.getRemovedMdnList(), xdmsHomePttId, persisterTxn);

            //Get the distinct members from added sublist Id list
            List<KnCorpSubscriberDTO> distDeletedSublistMemLst = sublistInfoUtil.getSublistDistinctMembers(contactListDTO.getRemovedSublistIds(), corpId, xdmsHomePttId, persisterTxn);
            //Get the distinct added members among added sublis id list and added member list
            Collection<KnCorpSubscriberDTO> removedSubscriberList =
                    contactInfoUtil.getDistinctMembersList(removedMembersList, distDeletedSublistMemLst, corpId, xdmsHomePttId, persisterTxn);

            // Collection<KnCorpSubscriberDTO> removedSubscriberList =
            //       contactInfoUtil.getDistinctMembers(removedMembersList, contactListDTO.getRemovedSublistIds(), corpId, xdmsHomePttId, persisterTxn);
            //moved up for validation rule
            /*    Collection<KnCorpSubscriberDTO> removedSubscriberList =
         contactInfoUtil.getDistinctMembersForSublist(contactListDTO.getRemovedMdnList(), contactListDTO.getRemovedSublistIds(), xdmsHomePttId, persisterTxn);*/
            contactListDTO.setAddedSublistIds(sublistIdsList);

            //go ahead with the business processing for modification

            if (privateListId <= 0 && !isObjectNull(contactListDTO.getAddedMdnList())
                    && !contactListDTO.getAddedMdnList().isEmpty()) {
                KnCorpSublistDTO corpSublist = new KnCorpSublistDTO();
                //insert into the DG.CorpListInfo table and DG.POCSubscribers table
                corpSublist.setCorpId(corpId);
                corpSublist.setSublistName((KnConstants.PRIVATE_LIST_NAME) + subscProfile.getMdn());
                corpSublist.setSublistType(KnConstants.SUBLIST_TYPE_PRIVATE_CONTACTLIST);
                corpSublist.setDistributionPolicy(KnConstants.DIST_POLICY_PRIVATE_CONTACTLIST);
                //create corpList entry
                KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
                privateListId = genInfoUtil.retrieveIdForTable(KnConstants.CORP_LIST_TABLE, xdmsHomePttId, KnConstants.CORP_LIST_TABLE_PK, KnConstants.FALSE, KnConstants.DUAL_DATA_STORE);
                corpSublist.setSublistId(privateListId);
                sublistInfoUtil.createSubListDetails(null, corpSublist, xdmsHomePttId, persisterTxn);
                KnIPCorpSublistSubscDistDTO distDto = new KnIPCorpSublistSubscDistDTO();
                Collection<String> mdnList = new ArrayList<String>();
                Collection<Integer> subliListIds = new ArrayList<Integer>();
                mdnList.add(mdn);
                subliListIds.add(privateListId);
                distDto.setMdnList(mdnList);
                distDto.setSublistIds(subliListIds);
                contactInfoUtil.pushSublistListToSubscriberList(distDto, xdmsHomePttId, persisterTxn);
                contactInfoUtil.updateSubscrinberCorpListId(subscProfile.getMdn(), corpSublist, xdmsHomePttId, persisterTxn);
            }
            //Modifying subscriber contact details
            knLogger.debug(methodName, "Modifying subscriber contact details");
            contactInfoUtil.modifySubscriberContactDetails(valPersistDTO, privateListId, xdmsHomePttId, persisterTxn);
            if ((valPersistDTO.getContactMdnList() != null && !valPersistDTO.getContactMdnList().isEmpty()) ||
                    (valPersistDTO.getNewlyAddedPrivateMembers() != null && !valPersistDTO.getNewlyAddedPrivateMembers().isEmpty())) {
                Collection<Integer> sublistIdList = new ArrayList<Integer>();
                sublistIdList.add(privateListId);
                sublistInfoUtil.fetchAndUpdateSublistEtag(sublistIdList, xdmsHomePttId, persisterTxn);
            }

            //update the dg.corpContactList table for the subscribers the sublist is pushed.
            //Get the sublist members
            Collection<String> mdnList = new ArrayList<String>();
            mdnList.add(mdn);

            Map<String, Collection<KnCorpSubscriberDTO>> finalMissingContacts = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
            if (subscriberList != null && !subscriberList.isEmpty()) {
                if (!externalSubsList.isEmpty()) {
                    //setting the proper corpId for the external subscribers
                    Map<String, KnCorpSubscriberDTO> subscMap = new HashMap<String, KnCorpSubscriberDTO>();
                    for (KnCorpSubscriberDTO subscriber : subscriberList) {
                        subscMap.put(subscriber.getMdn(), subscriber);
                    }
                    for (KnCorpSubscriberDTO subsc : externalSubsList) {
                        String memberMdn = subsc.getMdn();
                        if (subscMap.containsKey(memberMdn)) {
                            KnCorpSubscriberDTO subscriber = subscMap.get(memberMdn);
                            subscriber.setCorpId(subsc.getCorpId());
                            subscriber.setName(subsc.getName());
                        }
                    }
                    knLogger.debug(methodName, "subscriberList determination - ", subscriberList);
                    subscriberList = subscMap.values();
                }
                Map<String, Collection<KnCorpSubscriberDTO>> subscrNewAddedContMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                subscrNewAddedContMap.put(mdn, subscriberList);
                //get the subscriber Current ContactCount
                Map<String, Collection<String>> subsContactMap =
                        contactInfoUtil.getSubscribersContactList(mdnList, xdmsHomePttId, persisterTxn);

                finalMissingContacts = commonInfoUtil.filterMemberToBeAddedToSubscriber(subsContactMap, subscrNewAddedContMap);
                knLogger.debug(methodName, "finalMissingContacts determination 1 - ", finalMissingContacts);

                if (finalMissingContacts != null && !finalMissingContacts.isEmpty()) {
                    contactInfoUtil.insertMembersIntoCorpContactList(finalMissingContacts, xdmsHomePttId, persisterTxn);
                }
            }
            //remove the members from the contact list which are no more in the contact list
            LinkedHashMap<String, LinkedList<String>> mdnContactListMap = new LinkedHashMap<String, LinkedList<String>>();
            LinkedHashMap<String, LinkedList<Integer>> delContactStatus = null;
            if (removedSubscriberList != null && !removedSubscriberList.isEmpty()) {
                LinkedList<String> removedContactMdnList = new LinkedList<String>();
                for (KnCorpSubscriberDTO subscriberDTO : removedSubscriberList) {
                    removedContactMdnList.add(subscriberDTO.getMdn());
                }
                mdnContactListMap.put(mdn, removedContactMdnList);
                delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(mdnContactListMap, xdmsHomePttId, persisterTxn);
            }
            //Contact:
            Map<String, Collection<String>> actualDeletedContactMap = KnCorpCommonInfoUtil.getActualDeletedContacts(mdnContactListMap, delContactStatus);
            if(actualDeletedContactMap != null && !actualDeletedContactMap.isEmpty()){
                Map<String, Collection<String>> emergUserDestMap = corpSubsProvInfoUtil.getEmergUserDestMap(actualDeletedContactMap.keySet(),
                        xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "emergUserDestMap-- ", emergUserDestMap);
                Map<String, Collection<String>> failurePairingMap = new HashMap<>();
                Collection<String> finalFailedRemovedContacts = new ArrayList<>();
                if (emergUserDestMap != null && !emergUserDestMap.isEmpty()) {
                    emergUserDestMap.forEach((emergUser, emergDestinations) -> {
                        Collection<String> mappedDestination = new ArrayList<>();
                        Collection<String> removedContacts = actualDeletedContactMap.get(emergUser);
                        mappedDestination.addAll(emergDestinations.stream().filter(removedContacts::contains).collect(Collectors.toList()));
                        finalFailedRemovedContacts.addAll(mappedDestination);
                        if(!mappedDestination.isEmpty()) failurePairingMap.put(emergUser, mappedDestination);
                    });
                }
                knLogger.debug(methodName, "failurePairingMap-- ", failurePairingMap);
                if(!failurePairingMap.isEmpty()){
                    knLogger.error(methodName, "Emergency Destination Mapping Exists ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGENCY_DESTINATION_MAPPING_EXISTS_FOR_CONTACT,
                            "Emergency Destination Mapping Exists --", CORP_CONTACT_MANAGER,
                            MODIFY_SUBS_CONTACT_LIST, "", Arrays.asList(finalFailedRemovedContacts).toString(), "");
                }
            }
            if ((contactListDTO.getAddedMdnList() == null || contactListDTO.getAddedMdnList().isEmpty()) &&
                    (contactListDTO.getAddedSublistIds() == null || contactListDTO.getAddedSublistIds().isEmpty())) {
                contactInfoUtil.updateSubscribersContactCount(mdnList, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
            } else {
                contactInfoUtil.updateSubscribersContactCount(mdnList, corpProfile.getMaxContactsPerSubsc(), xdmsHomePttId, persisterTxn);
            }
            Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId, mdnList,
                    persisterTxn, null);
            knLogger.debug(methodName, "etagMap - ", etagMap);
            etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, finalMissingContacts, mdnContactListMap,
                    null, null, null, null, null,
                    null, delContactStatus, null, null);
            knLogger.debug(methodName, "etagMap after the form xcpa difff- ", KnGDPRTemplate.mapKeyMdn(etagMap));
            etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
            knLogger.debug(methodName, "etagMap after auth mapping modification- ", KnGDPRTemplate.mapKeyMdn(etagMap));
            //populate Success response
            boolean isUPMCall = contactListDTO.getOperationType().equals(KnOperationTypes.UPM_MODIFY_SUBS_CONTACT_LIST);
            respDTO.setChangeLogMap(etagMap);
            KnOPDirChgDTO directory = etagMap.get(mdn);
            Collection<KnOPDocChgDTO> docList = directory.getDocChgDTO();
            LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();
            Iterator ite = docList.iterator();
            String etag = "";
            if (ite.hasNext()) {
                KnOPDocChgDTO doc = (KnOPDocChgDTO) ite.next();
                etag = doc.getNewEtag();
                String targetMdn=mdn;
                if(isUPMCall){
                    ArrayList<String> targetProfileMdn = new ArrayList<>();
                    targetProfileMdn.add(mdn);
                    List<String> realMdns = groupInfoUtil.getRealMdns(targetProfileMdn, xdmsHomePttId, persisterTxn);
                    targetMdn=realMdns.get(0);
                }
                boolean exists = KnLIEventHandler.isTargetMDN(targetMdn);
                if (exists) {
                    KnLIEventDTO liEventDTO = new KnLIEventDTO();
                    liEventDTO.setMdn(targetMdn);
                    liEventDTO.setMcpttId(subscProfile.getMcpttId());
                    Collection<String> addedMembers = new ArrayList<String>();
                    if (doc.getAddedContactList() != null) {
                        for (KnSubscriberDTO subsc : doc.getAddedContactList()) {
                            addedMembers.add(subsc.getMdn());
                        }
                    }
                    liEventDTO.setAddedMembers(KnCorpCommonInfoUtil.removeProfileMdn(addedMembers,xdmsHomePttId, persisterTxn));
                    liEventDTO.setDeletedMembers(KnCorpCommonInfoUtil.removeProfileMdn(doc.getRemovedContactList(),xdmsHomePttId, persisterTxn));
                    short contact = 1;
                    liEventDTO.setDocumentType(contact);
                    liEventDTO.setPttServerId(xdmsHomePttId);
                    liEventDTO.setAction(KnLIConstants.MODIFY_CONTACTS);
                    liEventDTO.setErrorCode(KnLIConstants.SUCCESS_CODE);
                    liEventList.add(liEventDTO);
                }
            }
            Collection<Integer> sublistIds = corpSublistList.getAddedSublistIds();
            var cclSublistIds = new ArrayList<>(sublistIds);
            var subsListsIds = KnGeneralUtil.splitList(cclSublistIds, BULK_UPDATE_SIZE);
            List<Boolean> commonContactList = new ArrayList<>();
            for (var sublist : subsListsIds) {
                var isCommonContactList = corpSubsProvInfoUtil.isCommonContactList(sublist, xdmsHomePttId, persisterTxn);
                if (Boolean.TRUE == isCommonContactList) {
                    commonContactList.add(isCommonContactList);
                    break;
                }
            }
            if (!commonContactList.isEmpty()) {
                List<String> profileMdns = corpSubsProvInfoUtil.getProfileMdnByBaseMdn(mdn, xdmsHomePttId, persisterTxn);
                Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = corpUserProfileUtil.profileMdnEtagUpdate(
                        null, String.valueOf(corpId), profileMdns, null, xdmsHomePttId, persisterTxn);
                respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
                if (null != profileMdnEtagMap && !profileMdnEtagMap.isEmpty()) {
                    respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
                }
            }
            respDTO.setEtag(etag);
            respDTO.setLiEventList(liEventList);
            respDTO.setMdnCorpId(corpId);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
            //Added for the SDD req
            if (subscProfile.getClientType() == DISPATCH_CLIENT.value()
                    ||subscProfile.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
                knLogger.debug("Its a dispatch client we will send to job");
                respDTO.setEnabledDispatchMemList(KnCorpCommonInfoUtil.convertSubscDTOToStrList(finalMissingContacts.get(mdn)));
                respDTO.setDisabledDispatchMemList(mdnContactListMap.get(mdn));
            }
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while modifying subscribers contact - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while modifying subscribers contact - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while modifying subscribers contact - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }


    /**
     * 1. Validate if the CorpId is not found in DG.CorpInfo table then following message should be sent
     * Invalid Corporate profile
     * 2. Check if the sublist is already pushed to the subscriber from DG.CorpListDistribution table
     * 3. Only the subscriber for the corporate under consideration can have contacts pushed to it
     * 4. Validate the sublist under consideration belong to the corporation.
     *
     * @param distDTO
     * @param persisterTxn
     * @return
     */
    public KnCorpResponseDTO pushSublists(KnIPCorpSublistSubscDistDTO distDTO, KnPersisterTxn persisterTxn) {
        String methodName = "pushSublists(contactListDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", distDTO);

        //Step:
        //creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {

            int corpId = distDTO.getCorpId();

            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();

            //Invoking custom hook if the request comes from CATUI
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = distDTO.getCustomParamMap();
            if (distDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(KnPersisterConstants.PTT_SERVER_ID, xdmsHomePttId);
                distDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.PUSH_SUBLIST);
                hookIPDTO.setData(distDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }

            // Get the MDN list from subscrinfo table based on input MDN list and corpId.
            Collection<String> pocMdnList = contactInfoUtil.getCorporateSpecificPoCSubscribersFrmList
                    (distDTO.getMdnList(), corpId, xdmsHomePttId, persisterTxn);

            //subscriberMDN - clientType Map for validation of MDN client type
            Map<String, Integer> subsMdnClientTypeMap = new HashMap<String, Integer>();
            // Get the MDN-Detail Map from the input MDN list
            Map<String, KnCorpSubscriberDTO> contDetailsMap = contactInfoUtil.getSubsribersCorporateDetails(distDTO.getMdnList(), xdmsHomePttId, persisterTxn);
            Collection<String> mdnListinAllCorp = new ArrayList<String>();
            for (KnCorpSubscriberDTO contDTO : contDetailsMap.values()) {
                mdnListinAllCorp.add(contDTO.getMdn());
                subsMdnClientTypeMap.put(contDTO.getMdn(), contDTO.getClientType());
            }
            //Get all sublist Ids from corplistinfo table based on input sublist list, distribution policy, listtype and corpid
            KnSublistDetailsPersistDTO pocSublistIds = sublistInfoUtil.getPoCSublistIdInfo(distDTO.getSublistIds(),
                    corpId, xdmsHomePttId, persisterTxn);
            //Get the sublist ID pushed to MDN list Map
            //To check if any one of the sublist is already pushed to the subscribers.
            Map<Integer, Collection<String>> subListPushed = sublistInfoUtil.sublistPushedToSubscribersFrmList(distDTO, xdmsHomePttId,
                    persisterTxn);

            //Get the sublist members
            Collection<KnCorpSubscriberDTO> subscriberList =
                    contactInfoUtil.getDistinctMembers(null, distDTO.getSublistIds(), corpId, xdmsHomePttId, persisterTxn);
            /*         Collection<KnCorpSubscriberDTO> subscriberList =
                             contactInfoUtil.getDistinctMembersForSublist(null, distDTO.getSublistIds(), xdmsHomePttId, persisterTxn);
            */
            Map<String, Collection<KnCorpSubscriberDTO>> subscrNewAddedContMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
            if (subscriberList != null && !subscriberList.isEmpty()) {
                for (String mdn : distDTO.getMdnList()) {
                    subscrNewAddedContMap.put(mdn, subscriberList);
                }
            }
            //get the subscriber Current ContactCount
            Map<String, Collection<String>> subsContactMap =
                    contactInfoUtil.getSubscribersContactList(distDTO.getMdnList(), xdmsHomePttId, persisterTxn);
            Map<String, String> contactCountMap = new HashMap<String, String>();
            for (String mdn : distDTO.getMdnList()) {
                int count = 0;
                Collection<String> contactList = subsContactMap.get(mdn);
                if (contactList != null && !contactList.isEmpty()) {
                    count = contactList.size();
                }
                contactCountMap.put(mdn, String.valueOf(count));
            }
            Map<String, Collection<KnCorpSubscriberDTO>> finalMissingContacts = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
            Map<String, Integer> subscAddContactCnt = new HashMap<String, Integer>();
            //Determine the missing contacts for the subscribers.
            if (subscrNewAddedContMap != null && !subscrNewAddedContMap.isEmpty()) {
                finalMissingContacts = commonInfoUtil.filterMemberToBeAddedToSubscriber(subsContactMap, subscrNewAddedContMap);
                for (Map.Entry<String, Collection<KnCorpSubscriberDTO>> entry : finalMissingContacts.entrySet()) {
                    String mdn = entry.getKey();
                    subscAddContactCnt.put(mdn, entry.getValue().size());
                }
            }
            KnContactDetailsPersistDTO contactDetailsPersistDTO = new KnContactDetailsPersistDTO();
            //contactDetailsPersistDTO.setSublistContactCount(totalSublistContactCnt);
            contactDetailsPersistDTO.setPocSublistIds(pocSublistIds.getSublistIds());
            contactDetailsPersistDTO.setPocSubscMdnList(mdnListinAllCorp);
            contactDetailsPersistDTO.setContactMdnList(pocMdnList);
            contactDetailsPersistDTO.setSubscContactCnt(contactCountMap);
            contactDetailsPersistDTO.setSubscAdditonalContactCnt(subscAddContactCnt);
            contactDetailsPersistDTO.setMaxSubscribersContactLimit(corpProfile.getMaxContactsPerSubsc());
            contactDetailsPersistDTO.setSubscSublistIds(subListPushed.keySet());
            contactDetailsPersistDTO.setMaxAllowedContactCountPerRequest(corpProfile.getMaxContactsPerRequest());
            contactDetailsPersistDTO.setContactCountInRequest(distDTO.getMdnList().size());
            contactDetailsPersistDTO.setInputDTO(distDTO);
            contactDetailsPersistDTO.setMdnClientTypeMap(subsMdnClientTypeMap);

            knLogger.debug(methodName, "Invoking ValidatorFW. DTO - ", contactDetailsPersistDTO);
            validatorFW.validate(contactDetailsPersistDTO);
            knLogger.debug(methodName, "Validation completed Successfully");

            knLogger.info(methodName, "Push SUblist for the subscribers as the data is proper. ");
            contactInfoUtil.pushSublistListToSubscriberList(distDTO, xdmsHomePttId, persisterTxn);

            //update the dg.corpContactList table for the subscribers the sublist is pushed.
            if (finalMissingContacts != null && !finalMissingContacts.isEmpty()) {
                contactInfoUtil.insertMembersIntoCorpContactList(finalMissingContacts, xdmsHomePttId, persisterTxn);
            }
            contactInfoUtil.updateSubscribersContactCount(distDTO.getMdnList(), corpProfile.getMaxContactsPerSubsc(), xdmsHomePttId, persisterTxn);
            Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId,
                    distDTO.getMdnList(), persisterTxn, null);
            respDTO.setChangeLogMap(etagMap);
            List<String> targetMdns = KnLIEventHandler.getTagetMdnList(new ArrayList<String>(distDTO.getMdnList()));
            LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();
            if (targetMdns != null) {
                for (String mdn : targetMdns) {
                    KnLIEventDTO liEventDTO = new KnLIEventDTO();
                    liEventDTO.setMdn(mdn);
                    List<String> contacts = new ArrayList<String>();
                    if (finalMissingContacts.get(mdn) != null) {
                        for (KnCorpSubscriberDTO subsc : finalMissingContacts.get(mdn)) {
                            contacts.add(subsc.getMdn());
                        }
                    }
                    liEventDTO.setAddedMembers(contacts);
                    liEventList.add(liEventDTO);
                }
                respDTO.setLiEventList(liEventList);
            }
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }

        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while pushing the sublist to subscriber- ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while pushing the sublist to subscriber - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while pushing sublist to subscriber - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT:Returning Response - ", respDTO);
        return respDTO;
    }

    /**
     * 1. Validate if the CorpId is not found in DG.CorpInfo table then following message should be sent
     * Invalid Corporate profile
     * 2. Check if the sublist is not associated to the subscriber from DG.CorpListDistribution table
     * 3. Only the subscriber for the corporate under consideration can have contacts pushed to it
     * 4. Validate the sublist under consideration belong to the corporation.
     *
     * @param subsRequestDTO
     * @param persisterTxn
     * @return
     */
    public KnCorpResponseDTO removeSublist(KnIPCorpSublistSubscDistDTO subsRequestDTO, KnPersisterTxn persisterTxn) {
        String methodName = "removeSublist(subsRequestDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", subsRequestDTO);
        //Step:
        //creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            int corpId = subsRequestDTO.getCorpId();

            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();

            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = subsRequestDTO.getCustomParamMap();
            if (subsRequestDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                subsRequestDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.REMOVE_SUBLIST);
                hookIPDTO.setData(subsRequestDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }

            Collection<String> pocMdnList = contactInfoUtil.getCorporateSpecificPoCSubscribersFrmList
                    (subsRequestDTO.getMdnList(), corpId, xdmsHomePttId, persisterTxn);

            KnSublistDetailsPersistDTO pocSublistIds = sublistInfoUtil.getPoCSublistIdInfo(subsRequestDTO.getSublistIds(),
                    corpId, xdmsHomePttId, persisterTxn);

            Map<String, String> subsSublistCnt = sublistInfoUtil.sublistPushedSublistCntForSubsc(subsRequestDTO,
                    xdmsHomePttId, persisterTxn);

            //subscriberMDN - clientType Map for validation of MDN client type
            Map<String, Integer> subsMdnClientTypeMap = new HashMap<String, Integer>();
            // Get the MDN-Detail Map from the input MDN list
            Map<String, KnCorpSubscriberDTO> contDetailsMap = contactInfoUtil.getSubsribersCorporateDetails(subsRequestDTO.getMdnList(), xdmsHomePttId, persisterTxn);
            Collection<String> mdnListinAllCorp = new ArrayList<String>();
            for (KnCorpSubscriberDTO contDTO : contDetailsMap.values()) {
                mdnListinAllCorp.add(contDTO.getMdn());
                subsMdnClientTypeMap.put(contDTO.getMdn(), contDTO.getClientType());
            }

            KnContactDetailsPersistDTO contactDetailsPersistDTO = new KnContactDetailsPersistDTO();
            contactDetailsPersistDTO.setPocSublistIds(pocSublistIds.getSublistIds());
            contactDetailsPersistDTO.setPocSubscMdnList(mdnListinAllCorp);
            contactDetailsPersistDTO.setContactMdnList(pocMdnList);
            contactDetailsPersistDTO.setSubscSublistCnt(subsSublistCnt);
            contactDetailsPersistDTO.setInputDTO(subsRequestDTO);
            contactDetailsPersistDTO.setMaxAllowedContactCountPerRequest(corpProfile.getMaxContactsPerRequest());
            contactDetailsPersistDTO.setContactCountInRequest(subsRequestDTO.getMdnList().size());
            contactDetailsPersistDTO.setMdnClientTypeMap(subsMdnClientTypeMap);

            knLogger.debug(methodName, "Invoking ValidatorFW. DTO - ", contactDetailsPersistDTO);
            validatorFW.validate(contactDetailsPersistDTO);
            knLogger.debug(methodName, "Validation completed Successfully");

            knLogger.debug(methodName, "Remove Sublist from subscribers");
            sublistInfoUtil.removeSubscribersSublist(subsRequestDTO, xdmsHomePttId, persisterTxn);

            //update the contact List of the subscribers from which the sublists are removed.
            //getSublistMembers -
            Collection<KnCorpSubscriberDTO> subscriberList =
                    contactInfoUtil.getDistinctMembers(null, subsRequestDTO.getSublistIds(), corpId, xdmsHomePttId, persisterTxn);
            /*  Collection<KnCorpSubscriberDTO> subscriberList =
            contactInfoUtil.getDistinctMembersForSublist(null, subsRequestDTO.getSublistIds(), xdmsHomePttId, persisterTxn);
            */
            if (subscriberList != null && !subscriberList.isEmpty()) {
                LinkedList<String> contactList = new LinkedList<String>();
                for (KnCorpSubscriberDTO subscDTO : subscriberList) {
                    contactList.add(subscDTO.getMdn());
                }
                LinkedHashMap<String, LinkedList<String>> mdnContactListMap = new LinkedHashMap<String, LinkedList<String>>();
                for (String mdn : subsRequestDTO.getMdnList()) {
                    mdnContactListMap.put(mdn, contactList);
                }
                contactInfoUtil.deleteMembersFromCorpContactList(mdnContactListMap, xdmsHomePttId, persisterTxn);
            }

            contactInfoUtil.updateSubscribersContactCount(subsRequestDTO.getMdnList(), corpProfile.getMaxContactsPerSubsc(), xdmsHomePttId, persisterTxn);
            Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId,
                    subsRequestDTO.getMdnList(), persisterTxn, null);
            respDTO.setChangeLogMap(etagMap);
            populate(respDTO);
            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }

        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while removing the sublist from subscriber- ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while removing the sublist from subscriber - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while removing sublist from subscriber - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }


    /**
     * 1. Validate if the CorpId is not found in DG.CorpInfo table then following message should be sent
     * Invalid Corporate profile
     * Validate that the contact does not belong to same corporation by checking corpId for the subscriber
     * is same as passed using DG.PocSubscriber
     * Validate the contact is a valid corporate subscriber
     *
     * @param contactListDTO
     * @param persisterTxn
     * @return
     */
    public KnCorpResponseDTO addExtContacts(KnIPCorpContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        String methodName = "addExtContacts(contactDTO)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactListDTO);
        //Step:
        //creating Response DTO object
        KnCorpSubscContactListRespDTO respDTO = new KnCorpSubscContactListRespDTO();
        try {
            //open a txn if its not already opened
            int corpId = contactListDTO.getCorpId();
            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile =
                    commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //knLogger.debug(methodName, "param value of locWatcherConfig", microServicesParamNameValueMap);
            String bulkExtContAllowed = microServicesParamNameValueMap.get(KnConstants.BLK_EXT_CONT_LMT);
            KnContactDetailsPersistDTO contactDetails = new KnContactDetailsPersistDTO();
            Set<String> mdnList = new HashSet<>();
            Set<KnCorpSubscriberDTO> subscriberDTOS =  new HashSet<>();
            subscriberDTOS.addAll(contactListDTO.getContactList());
            Collection<KnCorpSubscriberDTO> aliasSubscribersList = contactListDTO.getContactAliasMdnList();
            Collection<KnCorpSubscriberDTO> userIdList = contactListDTO.getContactUserIdList();
            knLogger.info(methodName, "aliasSubscribersList:- ", aliasSubscribersList.size(), "userIdList:- ", userIdList.size());
            //Get The MDN Map by giving AliasMdn
            List<String> aliasMdnList = aliasSubscribersList.stream().map(KnCorpSubscriberDTO::getAliasMdn).collect(Collectors.toList());
            Map<String, String> aliasMdnMap = contactInfoUtil.getMdnByUsingAliasMdn(aliasMdnList, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "aliasMdnMap:- ", aliasMdnMap != null ? KnGDPRTemplate.mdnMap(aliasMdnMap).size() : 0);
            //Get The MDN Map by giving UserId
            List<String> userIds = userIdList.stream().map(KnCorpSubscriberDTO::getUserId).collect(Collectors.toList());
            Map<String, String> userIdMap = contactInfoUtil.getMdnByUsingUserId(userIds, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "userIdMap:- ", userIdMap);
            aliasSubscribersList.forEach(aliasMdnDto -> {
                KnCorpSubscriberDTO corpSubscriberDTO = new KnCorpContactDTO();
                String aliasMdn = aliasMdnDto.getAliasMdn();
                if(aliasMdnMap.containsKey(aliasMdn)){
                    corpSubscriberDTO.setMdn(aliasMdnMap.get(aliasMdn));
                    corpSubscriberDTO.setName(aliasMdnDto.getName());
                    corpSubscriberDTO.setSubsType(aliasMdnDto.getSubsType());
                    subscriberDTOS.add(corpSubscriberDTO);
                }
            });
            userIdList.forEach(userIdDto -> {
                KnCorpSubscriberDTO corpSubscriberDTO = new KnCorpContactDTO();
                String userId = userIdDto.getUserId();
                if(userIdMap.containsKey(userId)){
                    corpSubscriberDTO.setMdn(userIdMap.get(userId));
                    corpSubscriberDTO.setName(userIdDto.getName());
                    corpSubscriberDTO.setSubsType(userIdDto.getSubsType());
                    subscriberDTOS.add(corpSubscriberDTO);
                }
            });
            contactListDTO.setContactList(subscriberDTOS);
            List<Integer> inputProfileIdList = new ArrayList<>(subscriberDTOS.size());
            List<KnCorpSubscriberDTO> extSubsList = new ArrayList<>(subscriberDTOS.size());
            for (KnCorpSubscriberDTO subsDTO : subscriberDTOS) {
                mdnList.add(subsDTO.getMdn());
                int subsType = subsDTO.getSubsType();
                inputProfileIdList.add(subsType);
                if (subsType != KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT) {
                    extSubsList.add(subsDTO);
                }
            }
            contactListDTO.setMdnList(mdnList);
            //externalContacts holds the valid PoC subscribers in the request.
            Map<String, KnCorpSubscriberDTO> externalContacts = contactInfoUtil.getPoCSubscriberExistMap(mdnList,
                    xdmsHomePttId, persisterTxn);
            int externalContactCount = contactInfoUtil.getExternalContactCount(corpId, KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT, xdmsHomePttId, persisterTxn);
            int externalSubscriberCount = contactInfoUtil.getExternalContactCount(corpId, CONTACT_TYPE_EXTERNAL_SUBSCRIBER, xdmsHomePttId, persisterTxn);

            Collection<String> mdnsLists = externalContacts.keySet();
            contactDetails.setPocSubscMdnList(mdnsLists);
            contactDetails.setConfiguredProfileMap(commonInfoUtil.getExtProfileDetails(xdmsHomePttId, false, persisterTxn));
            //externalContacts = filterOutInternalSubscribersAndSetSubscCorpId(externalContacts, contactListDTO, corpId);
            contactDetails.setContactCorpDetails(externalContacts);
            long etag = contactInfoUtil.getCorporateEtag(corpId, xdmsHomePttId, persisterTxn);
            contactDetails.setEtag(etag);
            KnMdnDetailsPersistDTO contactsDetailsPersistDTO = contactInfoUtil.getExternalConatctsInfo(corpId, subscriberDTOS,
                    xdmsHomePttId, persisterTxn);
            //Setting the existing external contact list.
            contactDetails.setExternalContacts(contactsDetailsPersistDTO.getExternalMdnList());
            contactDetails.setMaxExtCorporateMembers(commonInfoUtil.getMaxExternalCorpContacts(xdmsHomePttId, persisterTxn));
            contactDetails.setCurrentContactCount(externalContactCount);
            contactDetails.setInputDTO(contactListDTO);
            contactDetails.setMaxAllowedContactCountPerRequest(Integer.parseInt(bulkExtContAllowed));
            contactDetails.setContactCountInRequest(mdnList.size());
            contactDetails.setAddedMdnDTO(externalContacts.values());
            contactDetails.setCurrentExtSubsCount(externalSubscriberCount);
            contactDetails.setMaxExtSubs(corpProfile.getMaxExtSubsPerCorp());
            contactDetails.setInputProfileIdList(inputProfileIdList);
            contactDetails.setExtSubsList(extSubsList);
            contactDetails.setAliasMdnList(aliasMdnList);
            contactDetails.setAliasMdnMap(aliasMdnMap);
            contactDetails.setUserIdList(userIds);
            contactDetails.setUserIdMap(userIdMap);
            knLogger.debug(methodName, "Invoking Validation FW - ", contactDetails);
            validatorFW.validate(contactDetails);
            knLogger.debug(methodName, "Validation completed Successfully");
            knLogger.debug(methodName, "externalContacts - ", externalContacts);
            for (KnCorpSubscriberDTO subsc : contactListDTO.getContactList()) {
                if (externalContacts.get(subsc.getMdn()) != null) {
                    subsc.setCorpId(externalContacts.get(subsc.getMdn()).getCorpId());
                }
            }
            respDTO = contactInfoUtil.addExternalContacts(contactListDTO, xdmsHomePttId, persisterTxn);
            long requestEtag = Long.parseLong(contactListDTO.getETag());
            if (requestEtag != etag) {
                respDTO.setFailedDataList(KnCorpCommonInfoUtil.getFailedDetails(etag));
            }
            //updating the corporate etag
            long corpEtag = contactInfoUtil.updateCorporateEtag(corpId, xdmsHomePttId, persisterTxn);
            respDTO.setEtag(String.valueOf(corpEtag));
            populate(respDTO);

        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while adding external contacts - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while adding external contacts - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while adding external contacts - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "Reponse Returned is - ", respDTO);
        return respDTO;
    }

    /**
     * 1. Validate if the CorpId is not found in DG.CorpInfo table then following message should be sent
     * Invalid Corporate profile
     * Validate the contact is a valid External contact in DG.ExtCorpContact subscriber
     * Validate if newContactName already exists.
     *
     * @param contactDTO
     * @param persisterTxn
     * @return
     */
    public KnCorpResponseDTO modifyExtContacts(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        String methodName = "modifyExtContacts(contactDTO)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactDTO.getCorpId(), " - ", contactDTO.getMdn());
        //Step:
        //creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            //open a txn if its not already opened
            int corpId = contactDTO.getCorpId();
            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            knLogger.debug(methodName, "Before getExternalConatctsInfo for input contactDTO - ", contactDTO);
            //Step:
            //
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            if(contactDTO.getAliasMdn() != null){
                List<String> aliasMdnList = new ArrayList<>();
                aliasMdnList.add(contactDTO.getAliasMdn());
                Map<String, String> aliasMdnMap = contactInfoUtil.getMdnByUsingAliasMdn(aliasMdnList, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "aliasMdnList: - ", KnGDPRTemplate.mdnList(aliasMdnList), "aliasMdnMap: ", KnGDPRTemplate.mdnMap(aliasMdnMap));
                if(!aliasMdnMap.isEmpty()){
                    contactDTO.setMdn(aliasMdnMap.get(contactDTO.getAliasMdn()));
                }
                persistDTO.setAliasMdnList(aliasMdnList);
                persistDTO.setAliasMdnMap(aliasMdnMap);
            } else if(contactDTO.getUserId() != null){
                List<String> userIds = new ArrayList<>();
                userIds.add(contactDTO.getUserId());
                Map<String, String> userIdMap = contactInfoUtil.getMdnByUsingUserId(userIds, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "userIds: - ", userIds, "userIdMap: ", userIdMap);
                if(!userIdMap.isEmpty()){
                    contactDTO.setMdn(userIdMap.get(contactDTO.getUserId()));
                }
                persistDTO.setUserIdList(userIds);
                persistDTO.setUserIdMap(userIdMap);
            }
            Collection<KnCorpSubscriberDTO> subsList = new ArrayList<KnCorpSubscriberDTO>();
            subsList.add(contactDTO);
            KnMdnDetailsPersistDTO externalPocSubsPersistDto = contactInfoUtil.getExternalConatctsInfo(corpId,
                    subsList, xdmsHomePttId, persisterTxn);
            persistDTO.setExternalContacts(externalPocSubsPersistDto.getExternalMdnList());
            long etag = contactInfoUtil.getCorporateEtag(corpId, xdmsHomePttId, persisterTxn);
            persistDTO.setEtag(etag);
            persistDTO.setInputDTO(contactDTO);
            knLogger.debug(methodName, "Invoking Validation FW - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully");
            //updating the corporate etag
            long cuurentEtag = contactInfoUtil.updateCorporateEtag(corpId, xdmsHomePttId, persisterTxn);
            //get the list of contacts for which the external contact will affect the contact List
            Collection<String> requestMdnList = new ArrayList<String>();
            requestMdnList.add(contactDTO.getMdn());
            Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil.getSubcriberSublistMemberShipList(
                    requestMdnList, contactDTO.getCorpId(), xdmsHomePttId, persisterTxn);
            if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
                Collection<Integer> sublistLists = sublistMemberMap.keySet();
                sublistInfoUtil.fetchAndUpdateSublistEtag(sublistLists, xdmsHomePttId, persisterTxn);
            }
            Collection<KnCorpSubscriberDTO> extMemberDto = externalPocSubsPersistDto.getExternalMdnList();
            Collection<String> mdnList = contactInfoUtil.getSubscMdnsHavingExtContact(contactDTO, xdmsHomePttId, persisterTxn);
            Map<String, Collection<KnCorpSubscriberDTO>> modifiedContactMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
            Collection<KnCorpSubscriberDTO> modifiedContactList = new ArrayList<KnCorpSubscriberDTO>();
            KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
            for(KnCorpSubscriberDTO extDto : extMemberDto){
                if(extDto.getMdn().equals(contactDTO.getMdn())){
                    subsc.setMdn(contactDTO.getMdn());
                    subsc.setName(contactDTO.getName());
                    subsc.setContact_type(extDto.getContact_type());
                    break;
                }
            }
            modifiedContactList.add(subsc);
            if (mdnList != null && !mdnList.isEmpty()) {
                for (String mdn : mdnList) {
                    modifiedContactMap.put(mdn, modifiedContactList);
                }
            }
            Map<String, KnOPDirChgDTO> eTags = contactInfoUtil.updateSubcribersResourceListIndexDoc(mdnList,
                    xdmsHomePttId, null, persisterTxn);

            Collection<Integer> groupIdList = groupInfoUtil.getGroupHavingMember(contactDTO, xdmsHomePttId, persisterTxn);
            Map<Integer, Collection<String>> groupDistInfo = new HashMap<Integer, Collection<String>>();
            if (!isObjectNull(groupIdList) && !groupIdList.isEmpty()) {
                Map<Integer, Integer> groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIdList, xdmsHomePttId, persisterTxn);
                groupDistInfo = groupInfoUtil.getGroupSubscriberDistList(groupIdList, xdmsHomePttId, persisterTxn);
                /*
                    Get the group member count and group type for the groupIds and set these values into etag Map
                 */
                Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupIdList, xdmsHomePttId, persisterTxn);
                //Removing the large group from groupDistInfo - not to send the replace notification for large group
                for(Map.Entry<Integer, KnCorpGroupDTO> entry : groupDetailsMap.entrySet()){
                    if(entry.getValue().isLargeGroup()){
                        groupDistInfo.remove(entry.getKey());
                    }
                }
                eTags = commonInfoUtil.formSubscriberNotification(groupDistInfo, groupEtagMap,
                        com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), eTags, groupDetailsMap, null);

            }
            respDTO = contactInfoUtil.modifyExternalContact(contactDTO, xdmsHomePttId, persisterTxn);
            if ((mdnList != null && !mdnList.isEmpty()) || (groupIdList != null && !groupIdList.isEmpty())) {
                eTags = contactInfoUtil.updateDistinctSubcribersDirectory(mdnList, groupIdList, eTags, xdmsHomePttId,
                        persisterTxn);
            }
            Collection<KnCorpGroupMemberDTO> groupMember = new ArrayList<KnCorpGroupMemberDTO>();
            KnCorpGroupMemberDTO grpMem = new KnCorpGroupMemberDTO();
            grpMem.setName(contactDTO.getName());
            grpMem.setMdn(contactDTO.getMdn());
            groupMember.add(grpMem);
            if(corpProfile.getLargeGrpSupported() == 1){
                //If large group is supported, remove the entry from etagMap if the doc change dto list is null or empty
                Set<String> mdnSet = new HashSet<>(eTags.keySet());
                for(String mdn : mdnSet){
                    KnOPDirChgDTO chgDTO = eTags.get(mdn);
                    if(chgDTO.getDocChgDTO() == null || chgDTO.getDocChgDTO().isEmpty()){
                        eTags.remove(mdn);
                    }
                }
            }
            eTags = KnCorpCommonInfoUtil.formXcapDiffNotification(eTags, null, null,
                    modifiedContactMap, null, null, null, groupMember,
                    groupDistInfo, null, null, null);
            long requestEtag = contactDTO.getEtag();
            if (requestEtag != etag) {
                respDTO.setFailedDataList(KnCorpCommonInfoUtil.getFailedDetails(etag));
            }
            respDTO.setEtag(String.valueOf(cuurentEtag));
            respDTO.setChangeLogMap(eTags);
            populate(respDTO);
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while modifying external contacts - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while modifying external contacts - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while modifying external contacts - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "Exit Point. Reponse Returned is - ", respDTO);
        return respDTO;
    }

    public KnCorpResponseDTO removeExtContacts(KnIPCorpContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        String methodName = "removeExtContacts(contactListDTO)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactListDTO);
        //Step:
        //creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            //open a txn if its not already opened
            int corpId = contactListDTO.getCorpId();
            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            String xdmsHomePttId = corpProfile.getXdmsHome();
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //knLogger.debug(methodName, "param value of locWatcherConfig", microServicesParamNameValueMap);
            String bulkExtContAllowed = microServicesParamNameValueMap.get(KnConstants.BLK_EXT_CONT_LMT);
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            Set<KnIPCorpContactDTO> contactList = new HashSet<>();
            LinkedList<String> requestMdnLists = new LinkedList<>();
            requestMdnLists.addAll(contactListDTO.getMdnList());
            LinkedList<String> aliasMdnList = contactListDTO.getAliasMdnList();
            LinkedList<String> userIds = contactListDTO.getUserIdList();
            knLogger.info(methodName, "aliasMdnList:- ", aliasMdnList.size(), "userIds:- ", userIds.size());
            //Get The MDN Map by giving AliasMdn
            Map<String, String> aliasMdnMap = contactInfoUtil.getMdnByUsingAliasMdn(aliasMdnList, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "aliasMdnMap:- ", aliasMdnMap != null ? KnGDPRTemplate.mdnMap(aliasMdnMap).size() : 0);
            //Get The MDN Map by giving UserId
            Map<String, String> userIdMap = contactInfoUtil.getMdnByUsingUserId(userIds, xdmsHomePttId, persisterTxn);
            knLogger.info(methodName, "userIdMap:- ", userIdMap != null ? userIdMap.size() : 0);
            aliasMdnList.forEach(aliasMdn -> {
                if(aliasMdnMap.containsKey(aliasMdn)){
                    requestMdnLists.add(aliasMdnMap.get(aliasMdn));
                }
            });
            userIds.forEach(userId -> {
                if(userIdMap.containsKey(userId)){
                    requestMdnLists.add(userIdMap.get(userId));
                }
            });
            //LinkedList<String> requestMdnList = (LinkedList) contactListDTO.getMdnList();
            for (String mdn : requestMdnLists) {
                KnIPCorpContactDTO contactDTO = new KnIPCorpContactDTO();
                contactDTO.setMdn(mdn);
                contactList.add(contactDTO);
                break;
            }
            KnCorpContactListRespDTO contactListResp = contactInfoUtil.getExternalContactDetails(contactList, corpId,
                    xdmsHomePttId, false, persisterTxn);
            long etag = contactInfoUtil.getCorporateEtag(corpId, xdmsHomePttId, persisterTxn);
            persistDTO.setEtag(etag);
            persistDTO.setInputDTO(contactListDTO);
            persistDTO.setExternalContacts(contactListResp.getContactList());
            persistDTO.setMaxAllowedContactCountPerRequest(Integer.parseInt(bulkExtContAllowed));
            persistDTO.setContactCountInRequest(requestMdnLists.size());
            persistDTO.setAliasMdnList(aliasMdnList);
            persistDTO.setAliasMdnMap(aliasMdnMap);
            persistDTO.setUserIdList(userIds);
            persistDTO.setUserIdMap(userIdMap);
            knLogger.debug(methodName, "Invoking Validation FW - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed Successfully");

            long currentCorpProfileEtag = contactInfoUtil.updateCorporateEtag(corpId, xdmsHomePttId, persisterTxn);

            Map<Integer, Collection<String>> sublistMemberMap = sublistInfoUtil.getSubcriberSublistMemberShipList(
                    requestMdnLists, corpId, xdmsHomePttId, persisterTxn);

            Collection<Integer> sublistLists = sublistMemberMap.keySet();
            Map<Integer, HashMap<String, Collection<String>>> groupMemberMap = groupInfoUtil.getAllSubscribersGroupList(requestMdnLists,
                    corpId, xdmsHomePttId, persisterTxn);
            Collection<Integer> groupIds = groupMemberMap.keySet();

            Collection<Integer> sublistList = sublistMemberMap.keySet();
            Map<Integer, Collection<KnCorpSubscriberDTO>> contactMemberMap = contactInfoUtil.getSublistListDistributionList(sublistList,
                    xdmsHomePttId, persisterTxn);
            if (sublistMemberMap != null && !sublistMemberMap.isEmpty()) {
                sublistInfoUtil.deleteMembersFromAllSublist(sublistMemberMap, xdmsHomePttId, persisterTxn);
                sublistInfoUtil.fetchAndUpdateSublistEtag(sublistLists, xdmsHomePttId, persisterTxn);
            }
            Map<Integer, Integer> groupEtagMap = new HashMap<Integer, Integer>();
            if (groupMemberMap != null && !groupMemberMap.isEmpty()) {
                groupEtagMap = groupInfoUtil.updateGroupListEtag(groupIds, xdmsHomePttId, persisterTxn);
            }
            List<String> contactMemberList = new ArrayList<String>();
            if (sublistList != null && !sublistList.isEmpty()) {
                for (int sublistId : sublistList) {
                    Collection<KnCorpSubscriberDTO> mdnList = contactMemberMap.get(sublistId);
                    if (mdnList != null && !mdnList.isEmpty()) {
                        for (KnCorpSubscriberDTO subscriberDTO : mdnList) {
                            if (!requestMdnLists.contains(subscriberDTO.getMdn())) {
                                contactMemberList.add(subscriberDTO.getMdn());
                            }
                        }
                    }
                }
            }
            LinkedHashMap<String, LinkedList<String>> removeMdnContactListMap = new LinkedHashMap<String, LinkedList<String>>();
            LinkedHashMap<String, LinkedList<Integer>> delContactStatus = null;
            contactInfoUtil.deleteExtMember(requestMdnLists, corpId, xdmsHomePttId, persisterTxn);

            Map<String, KnOPDirChgDTO> etagMap = new HashMap<String, KnOPDirChgDTO>();
            if (contactMemberList != null && !contactMemberList.isEmpty()) {
                //remove the external guy from the contact list of the subscribers
                for (String mdn : contactMemberList) {
                    removeMdnContactListMap.put(mdn, requestMdnLists);
                }
                delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(removeMdnContactListMap, xdmsHomePttId, persisterTxn);
                etagMap = contactInfoUtil.updateSubcribersResourceListIndexDoc(contactMemberList,
                        xdmsHomePttId, null, persisterTxn);
            }
            LinkedHashMap<Integer, LinkedList<String>> groupRemoveMdnListMap = new LinkedHashMap<Integer, LinkedList<String>>();
            LinkedHashMap<Integer, LinkedList<Integer>> delGroupMemStatus = new LinkedHashMap<Integer, LinkedList<Integer>>();
            Map<Integer, Collection<String>> grpDistMap = new HashMap<Integer, Collection<String>>();
            Map<Integer, String> groupNameMap = new HashMap<Integer, String>();
            List<String> delList = new ArrayList<String>();
            if (!groupIds.isEmpty()) {
                //remove the external guy from the group member list
                for (int groupId : groupIds) {
                    groupRemoveMdnListMap.put(groupId, requestMdnLists);
                }
                Map<Integer, KnCorpGroupDTO> groupDetailsMap_beforeUpdate = groupInfoUtil.getGroupBasicDetailsMap(groupIds, xdmsHomePttId, persisterTxn);
                delGroupMemStatus = groupInfoUtil.deleteCorpGroupMemberList(groupRemoveMdnListMap, xdmsHomePttId, persisterTxn);
                sublistInfoUtil.updateSublistsSubscribersContactCount(sublistLists, MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                        MAX_LIMIT_VALIDATION_NOT_REQUIRED, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn,
                        KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED);
                //Determine the deleted and the modified groups
                Map<String, HashMap<Integer, String>> groupListStatus = groupInfoUtil.getGroupListStatus(groupIds, xdmsHomePttId, persisterTxn);
                grpDistMap = groupInfoUtil.getGroupSubscriberDistList(groupIds, xdmsHomePttId, persisterTxn);
                /*
                    Get the group member count and group type for the groupIds and set these values into etag Map
                 */
                Map<Integer, KnCorpGroupDTO> groupDetailsMap = groupInfoUtil.getGroupBasicDetailsMap(groupIds, xdmsHomePttId, persisterTxn);
                //Get group member count after updating the group
                Map<Integer, Integer> updatedGrpMemCount = groupInfoUtil.getGroupMemCount(groupIds, xdmsHomePttId, persisterTxn);
                Map<Integer, Integer> largeGrpInfoMap = new HashMap<>();
                if (groupListStatus.get(KnConstants.MODIFIED) != null) {
                    Set<Integer> modGroupIdList = (groupListStatus.get(KnConstants.MODIFIED)).keySet();
                    if (!modGroupIdList.isEmpty()) {
                        knLogger.debug(methodName, "modGroupIdList is not null ", modGroupIdList);
                        Map<Integer, Collection<String>> modifiedMembersMap = new HashMap<Integer, Collection<String>>();
                        for (Integer grpId : modGroupIdList) {
                            Collection<String> modMembersList = grpDistMap.get(grpId);
                            KnCorpGroupDTO initialGrpInfo = groupDetailsMap_beforeUpdate.get(grpId);
                            knLogger.debug(methodName, "initialGrpInfo - ", initialGrpInfo);
                            Integer finalMemCount = updatedGrpMemCount.get(grpId);
                            knLogger.debug(methodName, "finalMemCount - ", finalMemCount);
                            if(initialGrpInfo != null && !initialGrpInfo.isLargeGroup() && finalMemCount != null){
                                if(initialGrpInfo.getGroupType() == BROADCAST_GROUP && finalMemCount <= corpProfile.getMaxMemPerLrgBGrp()){
                                    modifiedMembersMap.put(grpId, modMembersList);
                                }else if(initialGrpInfo.getGroupType() != BROADCAST_GROUP && finalMemCount <= corpProfile.getMaxMemPerLrgGrp()){
                                    modifiedMembersMap.put(grpId, modMembersList);
                                }
                            }
                            if(initialGrpInfo != null && initialGrpInfo.isLargeGroup() && finalMemCount != null){
                                if(initialGrpInfo.getGroupType() == BROADCAST_GROUP && finalMemCount <= corpProfile.getMaxMemPerBCGrp()){
                                    largeGrpInfoMap.put(grpId, 0);
                                }else if(initialGrpInfo.getGroupType() == STANDARD_GROUP && finalMemCount <= corpProfile.getMaxMemPerCorpGroup()){
                                    largeGrpInfoMap.put(grpId, 0);
                                }else if(initialGrpInfo.getGroupType() == DISPATCH_GROUP && finalMemCount <= corpProfile.getMaxMembersPerDispatchGroup()){
                                    largeGrpInfoMap.put(grpId, 0);
                                }
                            }
                            //for the contact added to update the directory
                            contactMemberList.addAll(modMembersList);
                            groupNameMap.put(grpId, groupListStatus.get(KnConstants.MODIFIED).get(grpId));
                        }
                        etagMap = commonInfoUtil.formSubscriberNotification(modifiedMembersMap, groupEtagMap,
                                com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap, groupDetailsMap, null);
                        //Updating the IS_LARGEGROUP flag
                        knLogger.debug(methodName, "largeGrpInfoMap ", largeGrpInfoMap);
                        if(!largeGrpInfoMap.isEmpty()){
                            groupInfoUtil.updateIsLargeGrpFlag(largeGrpInfoMap, xdmsHomePttId, persisterTxn);
                        }

                    }
                }
                //  REQ=rqPOC_CorpGrp_MinLim_1, Delete corp groups with < 2 members
                if (groupListStatus.get(KnConstants.DELETED) != null) {
                    Set<Integer> delGroupIdList = (groupListStatus.get(KnConstants.DELETED)).keySet();
                    if (!delGroupIdList.isEmpty()) {
                        Map<Integer, Collection<String>> deletedMembersMap = new HashMap<Integer, Collection<String>>();
                        for (Integer grpId : delGroupIdList) {
                            Collection<String> delMembersList = grpDistMap.get(grpId);
                            deletedMembersMap.put(grpId, delMembersList);
                            //for the contact added to update the directory
                            contactMemberList.addAll(delMembersList);
                            delList.addAll(delMembersList);
                        }
                        KnCorpInOutParamDTO corpInOutParamDTO = new KnCorpInOutParamDTO();
                        groupInfoUtil.deleteMDNsFromGroups(delGroupIdList, xdmsHomePttId, corpInOutParamDTO, persisterTxn);
                        respDTO.setTgsModeChgMap(corpInOutParamDTO.getTgsModeChgMap());
                        etagMap = commonInfoUtil.formSubscriberNotification(deletedMembersMap, groupEtagMap,
                                com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REMOVE.value(), etagMap, groupDetailsMap, null);
                        etagMap = commonInfoUtil.formTGSCDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                        etagMap = commonInfoUtil.formTGSSDocumentNotification(corpInOutParamDTO, etagMap, xdmsHomePttId, persisterTxn);
                    }
                }
            }
            List<String> grpMdnList = contactInfoUtil.getGroupMdns(contactMemberList, corpId, xdmsHomePttId, persisterTxn);
            if (sublistLists != null && !sublistLists.isEmpty()) {
                ArrayList<Integer> sharedSublists = sublistInfoUtil.getSharedSublistFromList(sublistLists, xdmsHomePttId, persisterTxn);
                ArrayList<Integer> emptySublist = sublistInfoUtil.getEmptySublistFrmList(sharedSublists, xdmsHomePttId, persisterTxn);
                if (emptySublist != null && !emptySublist.isEmpty()) {
                    sublistInfoUtil.deleteAllSublist(emptySublist, xdmsHomePttId, persisterTxn);
                }
            }
            if (!contactMemberList.isEmpty()) {
                etagMap = contactInfoUtil.updateDistinctSubcribersDirectory(contactMemberList, null, etagMap, xdmsHomePttId, persisterTxn);
                contactInfoUtil.updateSubscribersContactCount(contactMemberList, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
            }

            LinkedList<KnLIEventDTO> liEventList = KnCorpCommonInfoUtil.populateLIData(etagMap, xdmsHomePttId, groupNameMap,
                    null, null,persisterTxn);

            if(corpProfile.getLargeGrpSupported() == 1){
                //If large group is supported, remove the entry from etagMap if the doc change dto list is null or empty
                Set<String> mdnSet = new HashSet<>(etagMap.keySet());
                for(String mdn : mdnSet){
                    KnOPDirChgDTO chgDTO = etagMap.get(mdn);
                    if(chgDTO.getDocChgDTO() == null || chgDTO.getDocChgDTO().isEmpty()){
                        etagMap.remove(mdn);
                    }
                }
            }

            etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, removeMdnContactListMap,
                    null, null, groupRemoveMdnListMap, null, null,
                    grpDistMap, delContactStatus, delGroupMemStatus, null);
            long requestEtag = 0;
            if (contactListDTO.getETag() != null) {
                requestEtag = Long.parseLong(contactListDTO.getETag());
            }
            if (requestEtag != etag) {
                respDTO.setFailedDataList(KnCorpCommonInfoUtil.getFailedDetails(etag));
            }
            respDTO.setEtag(String.valueOf(currentCorpProfileEtag));
            if (grpMdnList != null) {
                for (String grpMdn : grpMdnList) {
                    etagMap.remove(grpMdn);
                }
            }
            respDTO.setChangeLogMap(etagMap);
            respDTO.setLiEventList(liEventList);
            /*if (delList != null && corpProfile.getLinkedGwKey() != null) {
                for (String mdn : delList) {
                    if (grpMdnList.contains(mdn)) {
                        //If the corporate is already linked i.e linked gateway key is not null get the refernce id
                        //get the refernce id from the account info table.
                        KnCorpGWLinkedAccountInfoDTO accountInfo = commonInfoUtil.getCorporateLinkedAccountInfo(corpProfile.getLinkedGwKey(), xdmsHomePttId, persisterTxn);
                        //update etag in the account info table
                        //update etag
                        Map<String, String> asyncInput = new HashMap<>();
                        asyncInput.put(KnDbSyncFwConstants.PTTSERVER_ID, xdmsHomePttId);
                        asyncInput.put(KnDbSyncFwConstants.NNI_REF_ID, accountInfo.getNniRefId());
                        knLogger.debug(methodName, " Async inputs", asyncInput);
                        KnSqlJobCollector collector = KnSqlJobCollector.getInstance();
                        int serviceType = KnDbSyncFwConstants.EXECUTOR.ETAG_UPDATE_NNI.value();
                        collector.collect(serviceType, asyncInput);

                    }

                }
            }*/
            populate(respDTO);
        } catch (KnCorpBOValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while modifying external contacts - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while modifying external contacts - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while modifying external contacts - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Reponse Returned is - ", respDTO);
        return respDTO;
    }

    /**
     * This method is used to get the external contact deatils from DB.
     * <p/>
     * Validate if the CorpId is not found in DG.CorpInfo table then following message should be sent
     * Invalid Corporate profile
     * Validate the contact is a valid External contact in DG.ExtCorpContact subscriber
     * Get the details from DG.PocSubscriberInfo
     *
     * @param contactListDTO
     * @param persisterTxn
     * @return
     */
    public KnCorpContactListRespDTO getExtContactDetails(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getExtContactDetails(KnIPCorpSubscContactListDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactListDTO.getCorpId(), " - ", contactListDTO.getExternalContacts());
        //Step:
        //creating Response DTO object
        KnCorpContactListRespDTO respDTO = new KnCorpContactListRespDTO();
        try {
            //open a txn if its not already opened
            int corpId = contactListDTO.getCorpId();
            //Step:
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false,
                    persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            knLogger.info(methodName, "xdmsHomePttId - ", xdmsHomePttId);
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            //knLogger.debug(methodName, "param value of locWatcherConfig", microServicesParamNameValueMap);
            String bulkExtContAllowed = microServicesParamNameValueMap.get(KnConstants.BLK_EXT_CONT_LMT);
            KnContactDetailsPersistDTO persistDTO = new KnContactDetailsPersistDTO();
            long etag = contactInfoUtil.getCorporateEtag(corpId, xdmsHomePttId, true, persisterTxn);
            persistDTO.setEtag(etag);
            Collection<KnCorpSubscriberDTO> extContSubscriberDTOS = new ArrayList<>();
            Collection<KnCorpSubscriberDTO> extContacts = contactListDTO.getExternalContacts();
            extContSubscriberDTOS.addAll(extContacts);
            Collection<KnCorpSubscriberDTO> extAliasContactList = contactListDTO.getExternalAliasMdnContacts();
            Collection<KnCorpSubscriberDTO> extUserIdList = contactListDTO.getExternalUserIdContacts();
            knLogger.info(methodName, "extAliasContactList:- ", extAliasContactList.size(), "extUserIdList:- ", extUserIdList.size());
            //Get The MDN Map by giving AliasMdn
            List<String> aliasMdnList = extAliasContactList.stream().map(KnCorpSubscriberDTO::getAliasMdn).collect(Collectors.toList());
            Map<String, String> aliasMdnMap = contactInfoUtil.getMdnByUsingAliasMdn(aliasMdnList, xdmsHomePttId, true, persisterTxn);
            knLogger.info(methodName, "aliasMdnMap:- ", aliasMdnMap != null ? KnGDPRTemplate.mdnMap(aliasMdnMap).size() : 0);
            //Get The MDN Map by giving UserId
            List<String> userIds = extUserIdList.stream().map(KnCorpSubscriberDTO::getUserId).collect(Collectors.toList());
            Map<String, String> userIdMap = contactInfoUtil.getMdnByUsingUserId(userIds, xdmsHomePttId, true, persisterTxn);
            knLogger.info(methodName, "userIdMap:- ", userIdMap);
            extAliasContactList.forEach(aliasMdnDto -> {
                KnCorpSubscriberDTO corpSubscriberDTO = new KnCorpContactDTO();
                String aliasMdn = aliasMdnDto.getAliasMdn();
                if(aliasMdnMap.containsKey(aliasMdn)){
                    corpSubscriberDTO.setMdn(aliasMdnMap.get(aliasMdn));
                    extContSubscriberDTOS.add(corpSubscriberDTO);
                }
            });
            extUserIdList.forEach(userIdDto -> {
                KnCorpSubscriberDTO corpSubscriberDTO = new KnCorpContactDTO();
                String userId = userIdDto.getUserId();
                if(userIdMap.containsKey(userId)){
                    corpSubscriberDTO.setMdn(userIdMap.get(userId));
                    extContSubscriberDTOS.add(corpSubscriberDTO);
                }
            });
            persistDTO.setContactCountInRequest(extContSubscriberDTOS.size());
            persistDTO.setMaxAllowedContactCountPerRequest(Integer.parseInt(bulkExtContAllowed));
            persistDTO.setInputDTO(contactListDTO);
            persistDTO.setAliasMdnList(aliasMdnList);
            persistDTO.setAliasMdnMap(aliasMdnMap);
            persistDTO.setUserIdList(userIds);
            persistDTO.setUserIdMap(userIdMap);
            knLogger.debug(methodName, "Invoking Validation FW - ", persistDTO);
            validatorFW.validate(persistDTO);
            knLogger.debug(methodName, "Validation completed successfully");
            Set<KnIPCorpContactDTO> contactList = new HashSet<>();
            for (KnCorpSubscriberDTO contact : extContSubscriberDTOS) {
                KnIPCorpContactDTO corpContactDTO = new KnIPCorpContactDTO();
                corpContactDTO.setMdn(contact.getMdn());
                contactList.add(corpContactDTO);
            }
            //KnCorpContactRespDTO
            respDTO = contactInfoUtil.getExternalContactDetails(contactList, corpId, xdmsHomePttId, true, persisterTxn);
            List<KnCorpSubscriberDTO> allContactList = new ArrayList<>(respDTO.getContactList());
            List<String> extSubsList = new ArrayList<>(respDTO.getExtSubsMap().keySet());
            Map<String, KnCorpSubscriberDTO> extSubsProfileIdMap = contactInfoUtil.getExtSubsMap(extSubsList, xdmsHomePttId, true, persisterTxn);
            Map<String, KnCorpSubscriberDTO> extSubscrMap = respDTO.getExtSubsMap();
            for (Map.Entry<String, KnCorpSubscriberDTO> entry : extSubscrMap.entrySet()) {
                String mdn = entry.getKey();
                KnCorpSubscriberDTO subsDto = entry.getValue();
                subsDto.setSubsType(extSubsProfileIdMap.get(mdn).getSubsType());
                allContactList.add(subsDto);
            }
            List<String> mdns = allContactList.stream().map(KnCorpSubscriberDTO::getMdn).collect(Collectors.toList());
            Map<String, KnCorpSubsEntitiesDTO> subsEntitiesDTOMap = contactInfoUtil.getSubsEntitiesDetails(mdns, xdmsHomePttId, true, persisterTxn);
            allContactList.stream().filter(contList -> subsEntitiesDTOMap.get(contList.getMdn()) != null).forEach(contList -> {
                contList.setAliasMdn(subsEntitiesDTOMap.get(contList.getMdn()).getAliasMdn());
                contList.setUserId(subsEntitiesDTOMap.get(contList.getMdn()).getUserId());
                contList.setSubsActiveFS2(subsEntitiesDTOMap.get(contList.getMdn()).getActiveFs2());
            });
            respDTO.setContactList(allContactList);
            respDTO.setEtag(String.valueOf(etag));
            populate(respDTO);
        } catch (KnCorpBOValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while modifying external contacts - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving external contacts - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving external contacts - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

    /**
     * Method to return the subscriber list where the request MDN exist as contact.
     * 1. Validate if corporate exist.
     * 2. Validate if request MDN is part of corporation (internal or external)
     * If validation successfull
     * 3. Retrieve the list of sublist where MDN exist from DG.CORPLISTMEMBER table.
     * 4. Filter the sublist Ids based on corpid and list type from DG.CORPLISTINFO.
     * 5. Retrieve the MDN and name from DG.POCSUBSCRINFO table for each sublistId.
     *
     * @param contactDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnReverseContactsRespDto getSubscrReverseContacts(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getSubscrReverseContacts()";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactDTO);
        KnReverseContactsRespDto respDTO = new KnReverseContactsRespDto();
        try {
            int corpId = contactDTO.getCorpId();
            knLogger.debug(methodName, "Corp Id passed in the request is - ", corpId);
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "Corporate Profile - ", corpProfile);

            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            knLogger.debug(methodName, "Xdm home retrieved from the coporate profile is - ", xdmsHomePttId);

            knLogger.debug(methodName, "Fetch the subscribers profile if cached or fetch from the DB the details");
            String mdn = contactDTO.getMdn();
            Map<String, Object> customParams = contactDTO.getCustomParamMap();
            if (contactDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(mdn), customParams, xdmsHomePttId, persisterTxn);
            }
            //get the Subscriber profile details from cache
            int subscribersCount = contactInfoUtil.getSubscribersCount(mdn, corpId, true, xdmsHomePttId, persisterTxn);
            boolean isSubscrExternalCont = false;
            if (subscribersCount < 1) {
                isSubscrExternalCont = contactInfoUtil.isExternalContExist(mdn, corpId, xdmsHomePttId, persisterTxn);
            }
            KnReverseContactPersistDto valPersistDTO = new KnReverseContactPersistDto();
            valPersistDTO.setSubscriberCount(subscribersCount);
            valPersistDTO.setExternalContact(isSubscrExternalCont);
            valPersistDTO.setInputDTO(contactDTO);
            knLogger.debug(methodName, "Invoking Validation FW - ", valPersistDTO);
            validatorFW.validate(valPersistDTO);
            knLogger.debug(methodName, "Validation completed successfully");
            knLogger.debug(methodName, "Before DB call to get the subscribers contact List.");
            List<Integer> sublistIds = sublistInfoUtil.getSubscrAllSublists(mdn, xdmsHomePttId, true, persisterTxn);
            Map<Integer, KnCorpSublistDTO> filteredSublists;
            List<KnMDNInfoDto> mdnInfoDtoList = new ArrayList<>();
            if (sublistIds.size() > 0) {
                filteredSublists = sublistInfoUtil.filterSublists(sublistIds, corpId, KnConstants.SUBLIST_TYPE_PRIVATE_CONTACTLIST, xdmsHomePttId, true, persisterTxn);
                if (filteredSublists.size() > 0) {
                    mdnInfoDtoList = contactInfoUtil.getSubscrReverseContacts(new ArrayList<>(filteredSublists.keySet()), xdmsHomePttId, true, persisterTxn);
                }
            }
            knLogger.debug(methodName, "After DB call mdnInfoDtoList", mdnInfoDtoList);
            respDTO.setMdnInfoDtoList(mdnInfoDtoList);
            populate(respDTO);
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while retrieving reverse contacts - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving reverse contacts ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while retrieving reverse contacts - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Response - ", respDTO);
        return respDTO;
    }

    /**
     * Method to remove all the contacts for the subscribers.This includes the private list member removal
     * and the sublist distribution removal.The contact count becomes 0 after this call.
     * 1. Validate if corporate exist.
     * 2. Validate if request MDN is part of corporation (internal)
     * 3. Validate the etag of the requestMDN.
     * If validation successful:
     * 4. Delete the distribution of sublist to the requestMDN from the DG.CORPLISTDISTINFO.
     * 5. Delete the privateList Member from the DG.CORPLISTMEMBER.
     * 6. Update the etag, LastUpdateTime of the PrivateList of requestMDN.
     * 7. First Get the ContactList of RequestMDN then Delete the entries from the DG.CORPCONTACTLIST for requestMDN.
     * 8. Delete the entry from DG.CORPCONTACTCOUNT for requestMDN.
     * 9. In Primary DB update the etag of requestMDN as resourceList and directory.
     * 10. Send notification to the subscriber on the contact removal with the removed MDNs, the list of members from point 7.
     *
     * @param subscriberInfoDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnCorpResponseDTO removeSubscribersContacts(KnIPCorpContactDTO subscriberInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "removeSubscribersContacts(subscriberInfoDTO, persisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", subscriberInfoDTO);
        //Step: creating Response DTO object
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            int corpId = subscriberInfoDTO.getCorpId();
            //Step:get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            //Step: IDTYPEget the Subscriber profile details from cache
            knLogger.debug(methodName, "Fetch the subscProfile profile if cached or fetch from the DB the details");
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(subscriberInfoDTO.getMdn(),
                    KnProfileTypes.PUBLIC_PROFILE, true, persisterTxn);
            knLogger.info(methodName, "subscProfile Profile - ", subscProfile);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();

            //Invoking custom hook
            knLogger.debug(methodName, "Custom Call");
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = subscriberInfoDTO.getCustomParamMap();
            if (subscriberInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                contactInfoUtil.checkValidHierarchySubs(Collections.singletonList(subscriberInfoDTO.getMdn()), customParams, xdmsHomePttId, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                knLogger.debug(methodName, "customParams after the change - ", customParams);
                subscriberInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.REMOVE_SUBSCRIBERS_CONTACTS);
                hookIPDTO.setData(subscriberInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                knLogger.debug(methodName, "Call before Hook.Process Invoker - ", processInvoker);
                Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    knLogger.debug(methodName, "Call After Hook");
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            int privateListId = subscProfile.getContactListId();
            String mdn = subscriberInfoDTO.getMdn();
            LinkedList<String> mdnList = new LinkedList<String>();
            mdnList.add(subscriberInfoDTO.getMdn());
            // Fetching the ResourceList Etag:
            int subscriberDocEtag = contactInfoUtil.getSubscribersDocumentEtag(mdn, xdmsHomePttId, persisterTxn);
            // setting of the validation persist DTO
            KnContactDetailsPersistDTO valPersistDTO = new KnContactDetailsPersistDTO();
            valPersistDTO.setInputDTO(subscriberInfoDTO);
            KnCorpSubscriberDTO subsDTO = new KnCorpSubscriberDTO();
            subsDTO.setMdn(mdn);
            valPersistDTO.setSubscDto(subsDTO);
            valPersistDTO.setEtag(subscriberDocEtag);

            valPersistDTO.setSubsCorpId(subscProfile.getCorpId());
            knLogger.debug(methodName, "After setting of the validation framework object ", "valPersistDTO - ", valPersistDTO);
            knLogger.info(methodName, "Invoking Validation FW - ", valPersistDTO);
            validatorFW.validate(valPersistDTO);
            knLogger.debug(methodName, "Validation completed successfully");

            //Fetching the sublist Ids distributed to requestMDN.
            knLogger.debug(methodName, "privateListId", privateListId);
            if (privateListId != 0) {
                Collection<KnCorpSublistDTO> listOfDistSublistIds = contactInfoUtil.getSubscMappedSublistList(subscriberInfoDTO,
                        privateListId, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "listOfDistSublistIds", listOfDistSublistIds);
                if (listOfDistSublistIds != null && !listOfDistSublistIds.isEmpty()) {
                    Collection<Integer> removeSublistIds = new ArrayList<Integer>();
                    for (KnCorpSublistDTO sublistIds : listOfDistSublistIds) {
                        removeSublistIds.add(sublistIds.getSublistId());
                    }
                    KnCorpSubscriberDTO corpSubsDto = new KnCorpSubscriberDTO();
                    corpSubsDto.setMdn(subscriberInfoDTO.getMdn());
                    //Remove the subscriber from Sublist DistributionList Table not from the private distribution list.
                    knLogger.debug(methodName, "removeSublistIds", removeSublistIds);
                    contactInfoUtil.removeSublistMappingForSubscriber(removeSublistIds, corpSubsDto, xdmsHomePttId, persisterTxn);
                }

                // get the private list members details.
                Collection<KnCorpSubscriberDTO> privateMemberListDetails = contactInfoUtil.getSubscPrivateMemberListDetails(privateListId, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "privateMemberListDetails", privateMemberListDetails);
                Collection<String> removePrivateMemberList = new ArrayList<String>();
                if (privateMemberListDetails != null && !privateMemberListDetails.isEmpty()) {
                    for (KnCorpSubscriberDTO removePrivateMember : privateMemberListDetails) {
                        removePrivateMemberList.add(removePrivateMember.getMdn());
                    }
                }
                knLogger.debug(methodName, "removePrivateMemberList", removePrivateMemberList);
                // deleting the private list members from the DG.CORPLISTMEMBER.
                contactInfoUtil.deleteSubscPrivateContactList(removePrivateMemberList, privateListId, xdmsHomePttId, persisterTxn);

                // update the etag of PrivateList of input Subscriber.
                knLogger.debug(methodName, "Updating the Etag of PrivateList of the subscriber");
                Collection<Integer> corpListId = new ArrayList<Integer>();
                corpListId.add(privateListId);
                sublistInfoUtil.fetchAndUpdateSublistEtag(corpListId, xdmsHomePttId, persisterTxn);
                //get the subscriber Current ContactList
                Map<String, Collection<String>> subsContactMap = contactInfoUtil.getSubscribersContactList(mdnList, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "subsContactMap - ", subsContactMap);
                //remove the members from the contact list which are no more in the contact list
                LinkedHashMap<String, LinkedList<String>> mdnContactListMap = new LinkedHashMap<String, LinkedList<String>>();
                LinkedHashMap<String, LinkedList<Integer>> delContactStatus = null;

                if (subsContactMap != null && !subsContactMap.isEmpty()) {
                    Collection<String> contactMDN = subsContactMap.get(mdn);
                    LinkedList<String> removedContactMdnList = new LinkedList<String>();
                    removedContactMdnList.addAll(contactMDN);
                    mdnContactListMap.put(mdn, removedContactMdnList);
                    // Delete the records from DG.CORPCONTACTLIST for the input MDN.
                    delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(mdnContactListMap, xdmsHomePttId, persisterTxn);
                }
                knLogger.debug(methodName, "mdnContactListMap - ", KnGDPRTemplate.mapKeyMdn(mdnContactListMap));
                //Contact:
                Map<String, Collection<String>> actualDeletedContactMap = KnCorpCommonInfoUtil.getActualDeletedContacts(mdnContactListMap, delContactStatus);
                if(actualDeletedContactMap != null && !actualDeletedContactMap.isEmpty()){
                    Map<String, Collection<String>> emergUserDestMap = corpSubsProvInfoUtil.getEmergUserDestMap(actualDeletedContactMap.keySet(),
                            xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "emergUserDestMap-- ", emergUserDestMap);
                    Map<String, Collection<String>> failurePairingMap = new HashMap<>();
                    Collection<String> finalFailedRemovedContacts = new ArrayList<>();
                    if (emergUserDestMap != null && !emergUserDestMap.isEmpty()) {
                        emergUserDestMap.forEach((emergUser, emergDestinations) -> {
                            Collection<String> mappedDestination = new ArrayList<>();
                            Collection<String> removedContacts = actualDeletedContactMap.get(emergUser);
                            mappedDestination.addAll(emergDestinations.stream().filter(removedContacts::contains).collect(Collectors.toList()));
                            finalFailedRemovedContacts.addAll(mappedDestination);
                            if(!mappedDestination.isEmpty()) failurePairingMap.put(emergUser, mappedDestination);
                        });
                    }
                    knLogger.debug(methodName, "failurePairingMap-- ", failurePairingMap);
                    if(!failurePairingMap.isEmpty()){
                        knLogger.error(methodName, "Emergency Destination Mapping Exists ");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGENCY_DESTINATION_MAPPING_EXISTS_FOR_CONTACT,
                                "Emergency Destination Mapping Exists --", CORP_CONTACT_MANAGER,
                                REMOVE_SUBSCRIBERS_CONTACTS, "", Arrays.asList(finalFailedRemovedContacts).toString(), "");
                    }
                }
                //contactInfoUtil.deleteSubscribersContactList(mdn, xdmsHomePttId, persisterTxn);
                // Updating the Contact Count of input MDN.
                contactInfoUtil.updateSubscribersContactCount(mdnList, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
                // Updating the Impacted tables for input MDN.
                Map<String, KnOPDirChgDTO> etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHomePttId, mdnList, persisterTxn, null);
                knLogger.debug(methodName, "etagMap - ", KnGDPRTemplate.mapKeyMdn(etagMap));
                // Preparing the Notification related with this API.
                etagMap = KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, null, mdnContactListMap, null, null, null, null, null, null, delContactStatus, null, null);
                knLogger.debug(methodName, "etagMap ", etagMap);
                etagMap = corpSubsProvInfoUtil.updateAuthImpactedTablesForRemovedContact(xdmsHomePttId, corpId, persisterTxn, etagMap);
                knLogger.debug(methodName, "etagMap after auth mapping modification- ", etagMap);
                //populate Success response
                respDTO.setChangeLogMap(etagMap);
                KnOPDirChgDTO directory = etagMap.get(mdn);
                Collection<KnOPDocChgDTO> docList = directory.getDocChgDTO();
                LinkedList<KnLIEventDTO> liEventList = new LinkedList<KnLIEventDTO>();
                Iterator ite = docList.iterator();
                // todo LI Notification
                String etag = "";
                if (ite.hasNext()) {
                    KnOPDocChgDTO doc = (KnOPDocChgDTO) ite.next();
                    etag = doc.getNewEtag();
                    boolean exists = KnLIEventHandler.isTargetMDN(mdn);
                    if (exists) {
                        KnLIEventDTO liEventDTO = new KnLIEventDTO();
                        liEventDTO.setMdn(mdn);
                        Collection<String> addedMembers = new ArrayList<String>();
                        if (doc.getAddedContactList() != null) {
                            for (KnSubscriberDTO subsc : doc.getAddedContactList()) {
                                addedMembers.add(subsc.getMdn());
                            }
                        }
                        liEventDTO.setAddedMembers(addedMembers);
                        liEventDTO.setDeletedMembers(doc.getRemovedContactList());
                        short contact = 1;
                        liEventDTO.setDocumentType(contact);
                        liEventDTO.setPttServerId(xdmsHomePttId);
                        liEventDTO.setAction(KnLIConstants.MODIFY_CONTACTS);
                        liEventDTO.setErrorCode(KnLIConstants.SUCCESS_CODE);
                        liEventList.add(liEventDTO);
                    }
                }
                respDTO.setEtag(etag);
                respDTO.setLiEventList(liEventList);
                if(subscProfile.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.DISPATCH_CLIENT.value()
                        || subscProfile.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()){
                    knLogger.debug("Its a dispatch client we will send to job");
                    if(mdnContactListMap.get(mdn) != null){
                        respDTO.setDisabledDispatchMemList(mdnContactListMap.get(mdn));
                    }
                }
            }
            respDTO.setEtag(String.valueOf(subscriberInfoDTO.getEtag()));
            respDTO.setMdnCorpId(subscProfile.getCorpId());
            populate(respDTO);
                if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                    respDTO.setFailedDataList(responseDTO.getFailedDataList());
                }
        } catch (KnValidationException e) {
            knLogger.error(methodName, "KnValidationException occured while removing subscribers contact - ", e);
            populate(respDTO, e);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while removing subscribers contact - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while removing subscribers contact - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

    /**
     * This method is used to get the external contact details from DB for requested corpId present in DG.EXTCORPCONTACT.
     * @param contactListDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnCorpContactListRespDTO getCorpExtContactDetails(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getCorpExtContactDetails(KnIPCorpSubscContactListDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactListDTO);
        //Step:
        //creating Response DTO object
        KnCorpContactListRespDTO respDTO = new KnCorpContactListRespDTO();
        try {
            int corpId = contactListDTO.getCorpId();
            //get the Corp profile details from cache
            knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId), CORP_PROFILE, false, persisterTxn);
            knLogger.info(methodName, "Corporate Profile - ", corpProfile);
            //use the xdms home pttServerId from Corp profile
            String xdmsHomePttId = corpProfile.getXdmsHome();
            knLogger.info(methodName, "xdmsHomePttId - ", xdmsHomePttId);
            List<KnCorpSubscriberDTO> extContactList = sublistInfoUtil.getCorpExtContact(corpId, xdmsHomePttId, persisterTxn);
            respDTO.setContactList(extContactList);
            populate(respDTO);
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while retrieving external contacts - ", e);
            populate(respDTO, e);
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving external contacts - ",
                    new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

        @Override
        public KnCorpResponseDTO modifyBulkCorpSubscContacts(KnIPCorpSubscContactListDTO contactListDTO, KnPersisterTxn persisterTxn) {

            String methodName = "modifyBulkCorpSubscContacts(contactListDTO, persisterTxn)";
            knLogger.info(methodName, "ENTRY : Input DTO Passed - ", contactListDTO);

            //Step: creating Response DTO object
            KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
            try {
                int corpId = contactListDTO.getCorpId();

                //Step:get the Corp profile details from cache

                knLogger.debug(methodName, "Fetch the corpProfile profile if cached or fetch from the DB the details");
                KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                        CORP_PROFILE, false, persisterTxn);
                knLogger.info(methodName, "Corporate Profile - ", corpProfile);

                //Step: IDTYPEget the Subscriber profile details from cache

                knLogger.debug(methodName, "Fetch the subscProfile profile if cached or fetch from the DB the details");
                KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(contactListDTO.getSubscriberMdn(),
                        KnProfileTypes.PUBLIC_PROFILE, true, persisterTxn);
                knLogger.info(methodName, "subscProfile Profile - ", subscProfile);

                //use the xdms home pttServerId from Corp profile

                String xdmsHomePttId = corpProfile.getXdmsHome();

                String userProfileId = subscProfile.getUserProfileId();
                knLogger.debug(methodName, " userProfileId:", userProfileId);
                boolean isUPMSharingEnable = false;
                ArrayList<String> upmId = new ArrayList<>();
                upmId.add(userProfileId);
                Map<String, Integer> upmOwnerList = contactInfoUtil.getUserProfileOwnerinfo(upmId, xdmsHomePttId, persisterTxn);
                if (!upmOwnerList.isEmpty()) {
                    isUPMSharingEnable = true;
                }
                knLogger.debug(methodName, "isUPMSharingEnable :", isUPMSharingEnable);

                //Invoking custom hook

                knLogger.debug(methodName, "Custom Call");
                KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
                Map<String, Object> customParams = contactListDTO.getCustomParamMap();
                if (contactListDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                    customParams.put(com.kodiak.common.resources.KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                    customParams.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customParams.put(com.kodiak.common.resources.KnConstants.PTT_SERVER_ID, xdmsHomePttId);
                    knLogger.debug(methodName, "customParams after the change - ", customParams);
                    contactListDTO.setCustomParamMap(customParams);
                    KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                    hookIPDTO.setAction(KnActions.ACTIONS.MODIFY_SUBSC_CONTACT);
                    hookIPDTO.setData(contactListDTO);
                    KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                    knLogger.debug(methodName, "Call before Hook.Process Invoker - ", processInvoker);
                    Object hookResp = processInvoker.invokeHook(com.kodiak.common.resources.KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                    if (hookResp != null && hookResp instanceof KnCorpHookRespDTO) {
                        responseDTO = (KnCorpHookRespDTO) hookResp;
                        knLogger.debug(methodName, "Call After Hook");
                        if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                            knLogger.error(methodName, "Returning Failure response");
                            populateXdmResponseFroomHook(responseDTO, respDTO);
                            return respDTO;
                        }
                    }
                }


                int privateListId = subscProfile.getContactListId();
                int subscribersCount = 0;
                if (isUPMSharingEnable) {
                    knLogger.debug(methodName, "Call to getSubscribersCount when isUPMSharingEnable- ", isUPMSharingEnable);
                    subscribersCount = contactInfoUtil.getSubscribersCount(contactListDTO.getSubscriberMdn(),
                            subscProfile.getCorpId(), xdmsHomePttId, persisterTxn);
                } else {
                    subscribersCount = contactInfoUtil.getSubscribersCount(contactListDTO.getSubscriberMdn(),
                            corpId, xdmsHomePttId, persisterTxn);
                }

                //Step:
                //preparing persistDto
                KnCorpMdnListPersistDTO corpMdnListPersistDto = new KnCorpMdnListPersistDTO();
                corpMdnListPersistDto.setCorpId(corpId);
                corpMdnListPersistDto.setAddedMdnList(contactListDTO.getAddedMdnList());
                corpMdnListPersistDto.setMdn(contactListDTO.getSubscriberMdn());
                corpMdnListPersistDto.setRemovedMdnList(contactListDTO.getRemovedMdnList());

                String mdn = subscProfile.getMdn();
                int subscriberDocEtag = contactInfoUtil.getSubscribersDocumentEtag(mdn, xdmsHomePttId, persisterTxn);
                //Step:
                //get mdn details for added mdn list, also filters out the external contacts present if any
                knLogger.debug(methodName, "Before getPoCSubscriberInfo for input corpMdnListPersistDto - ",
                        corpMdnListPersistDto);
                KnMdnDetailsPersistDTO pocMdnPersistDto = contactInfoUtil.getPoCSubscriberInfo(corpMdnListPersistDto,
                        xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "pocMdnPersistDto - ", pocMdnPersistDto.getAddedMdnDTO());

                knLogger.debug(methodName, "After getPoCSubscriberInfo result obtained PocSubscriberList- ",
                        pocMdnPersistDto);

                //Step:
                //setting the external contacts into input dto
                contactListDTO.setExternalContacts(corpMdnListPersistDto.getAddedExternalMdnList());
                knLogger.debug(methodName, "Before getExternalConatctsInfo.");

                //todo comment
                //Step:
                //
                Collection<KnCorpSubscriberDTO> externalSubsList = corpMdnListPersistDto.getAddedExternalMdnList();
                if (!externalSubsList.isEmpty()) {
                    KnMdnDetailsPersistDTO externalPocSubsPersistDto = contactInfoUtil.getExternalConatctsInfo(corpId,
                            externalSubsList, xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "After getExternalConatctsInfo result obtained externalPocSubsPersistDto - ",
                            externalPocSubsPersistDto);
                    externalSubsList = externalPocSubsPersistDto.getExternalMdnList();
                }

                //todo comment
                KnMdnDetailsPersistDTO contactMdnPersistDto = contactInfoUtil.getSubscriberPrivateContactListInfo(
                        corpMdnListPersistDto, privateListId, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "After getSubscriberContactListInfo result obtained Subscribers contact- "
                        , contactMdnPersistDto);
                KnCorpSublistListPersistDTO corpSublistList = new KnCorpSublistListPersistDTO();
                corpSublistList.setAddedSublistIds(contactListDTO.getAddedSublistIds());
                corpSublistList.setRemovedSublistIds(contactListDTO.getRemovedSublistIds());
                corpSublistList.setMdn(mdn);
                corpSublistList.setCorpId(corpId);

                knLogger.debug(methodName, "Before getSubsMappedSublistId for input corpSublistList - ", corpSublistList);
                KnSublistDetailsPersistDTO subsSublistIds = sublistInfoUtil.getSubsMappedSublistId(corpSublistList,
                        xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "After getSubsMappedSublistId result obtained subsSublistIds- ", subsSublistIds);
                //getting subist info for LISTDISTRIBUTIONPOLICY=3 or 5 for validation.
                KnSublistDetailsPersistDTO pocSublistIds = new KnSublistDetailsPersistDTO();
                Map<Integer, Integer> validSublistTypeInfo = new HashMap<>();
                //for the shared scenario for owner/shared fetching with the ownerid
                int tempCorpid = isUPMSharingEnable ? upmOwnerList.get(userProfileId) : corpSublistList.getCorpId();

                validSublistTypeInfo = sublistInfoUtil.getAllTypePoCSublistIdInfo(
                        corpSublistList.getAddedSublistIds(), tempCorpid, xdmsHomePttId, persisterTxn);

                if (!validSublistTypeInfo.isEmpty()) {
                    pocSublistIds.setSublistIds(new ArrayList<>(validSublistTypeInfo.keySet()));
                }
                knLogger.debug(methodName, " valid sublist info ", validSublistTypeInfo);

                knLogger.debug(methodName, "After getPoCSublistIdInfo result obtained pocSublistIds- ", pocSublistIds);
                KnIPCorpContactDTO corpContactDTO = new KnIPCorpContactDTO();
                corpContactDTO.setMdn(contactListDTO.getSubscriberMdn());
                corpContactDTO.setCorpId(contactListDTO.getCorpId());
                Collection<KnCorpSublistDTO> dbSublistListIds = contactInfoUtil.getSubscMappedSublistList
                        (corpContactDTO, subscProfile.getContactListId(), xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "After get Sublist present for the subscriber - ", dbSublistListIds);

                Collection<Integer> sublistids = new ArrayList<Integer>();
                for (KnCorpSublistDTO sublist : dbSublistListIds) {
                    sublistids.add(sublist.getSublistId());
                }
                KnSublistDetailsPersistDTO dbSublistList = new KnSublistDetailsPersistDTO();
                dbSublistList.setSublistIds(sublistids);

                knLogger.debug(methodName, "Before filtering the sublist. DB sublist- ", dbSublistList,
                        ", pocSublist - ", pocSublistIds, ", subscribersSublist - ", subsSublistIds);
                Collection<Integer> finalSublistListInDB = KnCorpCommonInfoUtil.filterOutFinalSublistList(dbSublistList,
                        pocSublistIds, subsSublistIds);
                knLogger.debug(methodName, "After filtering the sublist  - ", finalSublistListInDB);

                validSublistTypeInfo.putAll(dbSublistListIds.stream().filter(
                        sublistIds -> finalSublistListInDB.contains(sublistIds.getSublistId())
                ).collect(Collectors.toMap(KnCorpSublistDTO::getSublistId, KnCorpSublistDTO::getListDistribution)));

                knLogger.debug(methodName, " Total sublists ", validSublistTypeInfo);

                List<KnCorpSubscriberDTO> privateSubscrMemberList = contactInfoUtil.getSubscPrivateMemberList(
                        subscProfile.getContactListId(), xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "After Fetching subscribers private Members details - ",
                        privateSubscrMemberList);
                Collection<KnCorpSubscriberDTO> subscList = new ArrayList<KnCorpSubscriberDTO>();
                List<String> allInputMDNList = new ArrayList<>(pocMdnPersistDto.getMdnList());  //Adding all input mdn list.
                allInputMDNList.addAll(pocMdnPersistDto.getNniSubscrList());
                for (String mdnStr : allInputMDNList) {
                    KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                    subsc.setMdn(mdnStr);
                    if (externalSubsList.contains(subsc)) {
                        continue;
                    } else {
                        subsc.setCorpId(corpId);
                    }
                    subscList.add(subsc);
                }
                if (externalSubsList != null && !externalSubsList.isEmpty()) {
                    subscList.addAll(externalSubsList);
                }
                knLogger.debug(methodName, "subscList - ", subscList);
                knLogger.debug(methodName, "Before filtering the members to be added in db. privateSublistDetails - "
                        , privateSubscrMemberList, " , Poc Mdn List - ", KnGDPRTemplate.mdnList(pocMdnPersistDto.getMdnList()), "external mdn ",
                        pocMdnPersistDto.getExternalMdnList());
                knLogger.debug(methodName, "privateSubscrMemberList - ", privateSubscrMemberList);
                knLogger.debug(methodName, "subscList - ", subscList);
                knLogger.debug(methodName, "externalSubsList - ", externalSubsList);


                Collection<KnCorpSubscriberDTO> mdnsToBeAddedToPrivateList = KnCorpCommonInfoUtil.filterOutTheNewlyAddedMebers(
                        privateSubscrMemberList, subscList, externalSubsList);
                knLogger.debug(methodName, "After filtering the members to be added in db.Members to be added - ",
                        mdnsToBeAddedToPrivateList);

                Collection<String> finalMdnInPrivateList = KnCorpCommonInfoUtil.filterFinalPrivateListMembers(
                        privateSubscrMemberList, mdnsToBeAddedToPrivateList, contactMdnPersistDto);


                Collection<Integer> sublistIdsList = contactInfoUtil.filterFinalSublistToBeAddedInDb(finalSublistListInDB,
                        dbSublistList);

                //Collection<KnCorpSubscriberDTO> membersList = contactInfoUtil.getSubscribersInfo(contactListDTO.getAddedMdnList(), xdmsHomePttId, persisterTxn);
                //Get the distinct members from added sublist Id list
                List<KnCorpSubscriberDTO> distAddedSublistMemLst = sublistInfoUtil.getSublistDistinctMembers(contactListDTO.getAddedSublistIds(), corpId, xdmsHomePttId, persisterTxn);
                //Get the distinct added members among added sublis id list and added member list
                Collection<KnCorpSubscriberDTO> subscriberList =
                        contactInfoUtil.getDistinctMembersList(pocMdnPersistDto.getAddedMdnDTO(), distAddedSublistMemLst, corpId, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "subscriberList determination - ", subscriberList);

             /*
            Get the distinct profileId list for all external contact in the request MDN list
            And get the profile details for those list for validation of featureBit
*/
                List<KnCorpSubscriberDTO> extSubsList = new ArrayList<KnCorpSubscriberDTO>();
                for (KnCorpSubscriberDTO subs : subscriberList) {
                    if (subs.getCorpId() != corpId) {
                        subs.setExternalContact(Boolean.TRUE);
                        extSubsList.add(subs);
                    }
                }
                List<Integer> profileIdList = contactInfoUtil.getExtSubsrProfilelist(extSubsList, xdmsHomePttId, persisterTxn);
                Map<Integer, KnExtProfileDetails> extProfileDetailsMap = commonInfoUtil.getExtProfileDetails(profileIdList, xdmsHomePttId, persisterTxn);

                int deletedMembersCount = 0;
                if (!isObjectNullOrEmpty(contactListDTO.getRemovedMdnList())) {
                    deletedMembersCount = contactListDTO.getRemovedMdnList().size();
                }

                //Prepare subscriberMDN - clientType Map for validation
                Map<String, Integer> subsMdnClientTypeMap = new HashMap<String, Integer>();
                subsMdnClientTypeMap.put(mdn, subscProfile.getClientType());
                KnContactDetailsPersistDTO valPersistDTO = new KnContactDetailsPersistDTO();
                int maxAllowedContacts = corpProfile.getMaxContactsPerSubsc();//getting the max count from DB
                int finalContactCount = corpProfile.getMaxContactsPerSubsc();//setting as default to bypass validation
                Map<String, Integer> countInfo = new HashMap<>();
                boolean isCommonContact = subscProfile.getSubscriberFS2() != null && KnGeneralUtil.getFeatureBitValue(subscProfile.getSubscriberFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.COMMON_CONTACT_LIST.value());
                Boolean isCommonContactListSupport = Boolean.FALSE;
                //skip if the bit is disabled
                if (isCommonContact) {
                    //common sublist modification allowed if system and corp flag is enabled
                    final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
                    Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
                    final String systemCmnContactListSupport = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SUPPORT) == null
                            ? "0" : microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SUPPORT);
                    final Integer commonContactListSupportFlagOfCorpProfile = corpProfile.getCommonContactListSupport();
                    if (systemCmnContactListSupport.equals(BIT_ENABLED) || commonContactListSupportFlagOfCorpProfile == Integer.valueOf(ENABLED)) {
                        isCommonContactListSupport = Boolean.TRUE;
                    }
                    knLogger.debug(methodName, " isCommonContact supported ", isCommonContactListSupport);
                    valPersistDTO.setCommonContactEnabled(isCommonContactListSupport);
                    final String sysCmnCntctListPerSubString = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_PERSUB);
                    final int sysCmnCntctListPerSub = sysCmnCntctListPerSubString == null ? 0 : Integer.parseInt(sysCmnCntctListPerSubString);
                    final String sysCmnCntctListSizePerSubString = microServicesParamNameValueMap.get(COMMON_CONTACTLIST_SIZE);
                    final int sysCmnCntctListSizePerSub = sysCmnCntctListSizePerSubString == null ? 0 : Integer.parseInt(sysCmnCntctListSizePerSubString);
                    valPersistDTO.setMaxAlloedCommonSublistPerSubs(sysCmnCntctListPerSub);
                    valPersistDTO.setMaxAllowedCommonContactCount(sysCmnCntctListSizePerSub);
                    knLogger.debug(methodName, "maxCommonSublistPerSub ", sysCmnCntctListPerSub, " commonSublistSizePerSub ", sysCmnCntctListSizePerSub);
                }
                valPersistDTO.setSubListIdInfo(validSublistTypeInfo);
                if ((contactListDTO.getAddedMdnList() != null && !contactListDTO.getAddedMdnList().isEmpty()) ||
                        (contactListDTO.getAddedSublistIds() != null && !contactListDTO.getAddedSublistIds().isEmpty())) {
                    countInfo = contactInfoUtil.getFinalMemberContactCount(finalSublistListInDB,
                            finalMdnInPrivateList, mdn, xdmsHomePttId, persisterTxn);
                    finalContactCount = countInfo.get(NORMAL_CONTACT_COUNT);
                    if (isCommonContactListSupport) {
                        valPersistDTO.setCommonContactCount(countInfo.get(COMMON_CONTACT_COUNT));
                    }
                    knLogger.debug(methodName, " normalContactCount ", finalContactCount, " commonContactCount ", countInfo.get(COMMON_CONTACT_COUNT));
                }

                knLogger.debug(methodName, "Before setting of the validation framework object.");
                //settingof the validation persist DTO
                valPersistDTO.setInputDTO(contactListDTO);
                KnCorpSubscriberDTO subsDTO = new KnCorpSubscriberDTO();
                subsDTO.setMdn(mdn);
                subsDTO.setCorpId(corpId);
                valPersistDTO.setSubscDto(subsDTO);
                valPersistDTO.setSubscriberCount(subscribersCount);
                valPersistDTO.setEtag(subscriberDocEtag);
                valPersistDTO.setContactMdnList(contactMdnPersistDto.getMdnList());
                valPersistDTO.setPocSubscMdnList(pocMdnPersistDto.getMdnList());
                valPersistDTO.setPocSublistIds(pocSublistIds.getSublistIds());
                valPersistDTO.setSubscSublistIds(subsSublistIds.getSublistIds());
                valPersistDTO.setContactCount(finalContactCount);
                valPersistDTO.setExternalContacts(externalSubsList);
                valPersistDTO.setNewlyAddedPrivateMembers(mdnsToBeAddedToPrivateList);
                valPersistDTO.setMaxSubscribersContactLimit(maxAllowedContacts);
                valPersistDTO.setMaxAllowedContactCountPerRequest(corpProfile.getMaxContactsPerRequest());
                valPersistDTO.setContactCountInRequest(subscriberList.size() + deletedMembersCount);
                valPersistDTO.setMdnClientTypeMap(subsMdnClientTypeMap);
                valPersistDTO.setAddedMdnDTO(pocMdnPersistDto.getAddedMdnDTO());
                valPersistDTO.setConfiguredProfileMap(extProfileDetailsMap);
                valPersistDTO.setUPMSharingEnabled(isUPMSharingEnable);
                knLogger.debug(methodName, "After setting of the validation framework object ", "valPersistDTO - ", valPersistDTO);

                Collection<KnCorpSubscriberDTO> removedMembersList = contactInfoUtil.getSubscribersInfo(contactListDTO.getRemovedMdnList(), xdmsHomePttId, persisterTxn);

                //Get the distinct members from added sublist Id list
                List<KnCorpSubscriberDTO> distDeletedSublistMemLst = sublistInfoUtil.getSublistDistinctMembers(contactListDTO.getRemovedSublistIds(), corpId, xdmsHomePttId, persisterTxn);
                //Get the distinct added members among added sublis id list and added member list
                Collection<KnCorpSubscriberDTO> removedSubscriberList =
                        contactInfoUtil.getDistinctMembersList(removedMembersList, distDeletedSublistMemLst, corpId, xdmsHomePttId, persisterTxn);

                contactListDTO.setAddedSublistIds(sublistIdsList);

                //go ahead with the business processing for modification

                if (privateListId <= 0 && !isObjectNull(contactListDTO.getAddedMdnList())
                        && !contactListDTO.getAddedMdnList().isEmpty()) {
                    KnCorpSublistDTO corpSublist = new KnCorpSublistDTO();
                    //insert into the DG.CorpListInfo table and DG.POCSubscribers table
                    corpSublist.setCorpId(corpId);
                    corpSublist.setSublistName((KnConstants.PRIVATE_LIST_NAME) + subscProfile.getMdn());
                    corpSublist.setSublistType(KnConstants.SUBLIST_TYPE_PRIVATE_CONTACTLIST);
                    corpSublist.setDistributionPolicy(KnConstants.DIST_POLICY_PRIVATE_CONTACTLIST);
                    //create corpList entry
                    KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
                    privateListId = genInfoUtil.retrieveIdForTable(KnConstants.CORP_LIST_TABLE, xdmsHomePttId, KnConstants.CORP_LIST_TABLE_PK, KnConstants.FALSE, KnConstants.DUAL_DATA_STORE);
                    corpSublist.setSublistId(privateListId);
                    sublistInfoUtil.createSubListDetails(null, corpSublist, xdmsHomePttId, persisterTxn);
                    KnIPCorpSublistSubscDistDTO distDto = new KnIPCorpSublistSubscDistDTO();
                    Collection<String> mdnList = new ArrayList<String>();
                    Collection<Integer> subliListIds = new ArrayList<Integer>();
                    mdnList.add(mdn);
                    subliListIds.add(privateListId);
                    distDto.setMdnList(mdnList);
                    distDto.setSublistIds(subliListIds);
                    contactInfoUtil.pushSublistListToSubscriberList(distDto, xdmsHomePttId, persisterTxn);
                    contactInfoUtil.updateSubscrinberCorpListId(subscProfile.getMdn(), corpSublist, xdmsHomePttId, persisterTxn);
                }
                //Modifying subscriber contact details
                knLogger.debug(methodName, "Modifying subscriber contact details");
                contactInfoUtil.modifySubscriberContactDetails(valPersistDTO, privateListId, xdmsHomePttId, persisterTxn);

                //update the dg.corpContactList table for the subscribers the sublist is pushed.
                //Get the sublist members
                Collection<String> mdnList = new ArrayList<String>();
                mdnList.add(mdn);

                Map<String, Collection<KnCorpSubscriberDTO>> finalMissingContacts = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                if (subscriberList != null && !subscriberList.isEmpty()) {
                    if (!externalSubsList.isEmpty()) {
                        //setting the proper corpId for the external subscribers
                        Map<String, KnCorpSubscriberDTO> subscMap = new HashMap<String, KnCorpSubscriberDTO>();
                        for (KnCorpSubscriberDTO subscriber : subscriberList) {
                            subscMap.put(subscriber.getMdn(), subscriber);
                        }
                        for (KnCorpSubscriberDTO subsc : externalSubsList) {
                            String memberMdn = subsc.getMdn();
                            if (subscMap.containsKey(memberMdn)) {
                                KnCorpSubscriberDTO subscriber = subscMap.get(memberMdn);
                                subscriber.setCorpId(subsc.getCorpId());
                                subscriber.setName(subsc.getName());
                            }
                        }
                        knLogger.debug(methodName, "subscriberList determination - ", subscriberList);
                        subscriberList = subscMap.values();
                    }
                    Map<String, Collection<KnCorpSubscriberDTO>> subscrNewAddedContMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                    subscrNewAddedContMap.put(mdn, subscriberList);
                    //get the subscriber Current ContactCount
                    Map<String, Collection<String>> subsContactMap =
                            contactInfoUtil.getSubscribersContactList(mdnList, xdmsHomePttId, persisterTxn);

                    finalMissingContacts = commonInfoUtil.filterMemberToBeAddedToSubscriber(subsContactMap, subscrNewAddedContMap);
                    knLogger.debug(methodName, "finalMissingContacts determination 1 - ", finalMissingContacts);

                    if (finalMissingContacts != null && !finalMissingContacts.isEmpty()) {
                        contactInfoUtil.insertMembersIntoCorpContactList(finalMissingContacts, xdmsHomePttId, persisterTxn);
                    }
                }
                //remove the members from the contact list which are no more in the contact list
                LinkedHashMap<String, LinkedList<String>> mdnContactListMap = new LinkedHashMap<String, LinkedList<String>>();
                LinkedHashMap<String, LinkedList<Integer>> delContactStatus = null;
                if (removedSubscriberList != null && !removedSubscriberList.isEmpty()) {
                    LinkedList<String> removedContactMdnList = new LinkedList<String>();
                    for (KnCorpSubscriberDTO subscriberDTO : removedSubscriberList) {
                        removedContactMdnList.add(subscriberDTO.getMdn());
                    }
                    mdnContactListMap.put(mdn, removedContactMdnList);
                    delContactStatus = contactInfoUtil.deleteMembersFromCorpContactList(mdnContactListMap, xdmsHomePttId, persisterTxn);
                }
                //Contact:
                Map<String, Collection<String>> actualDeletedContactMap = KnCorpCommonInfoUtil.getActualDeletedContacts(mdnContactListMap, delContactStatus);
                if (actualDeletedContactMap != null && !actualDeletedContactMap.isEmpty()) {
                    Map<String, Collection<String>> emergUserDestMap = corpSubsProvInfoUtil.getEmergUserDestMap(actualDeletedContactMap.keySet(),
                            xdmsHomePttId, persisterTxn);
                    knLogger.debug(methodName, "emergUserDestMap-- ", emergUserDestMap);
                    Map<String, Collection<String>> failurePairingMap = new HashMap<>();
                    Collection<String> finalFailedRemovedContacts = new ArrayList<>();
                    if (emergUserDestMap != null && !emergUserDestMap.isEmpty()) {
                        emergUserDestMap.forEach((emergUser, emergDestinations) -> {
                            Collection<String> mappedDestination = new ArrayList<>();
                            Collection<String> removedContacts = actualDeletedContactMap.get(emergUser);
                            mappedDestination.addAll(emergDestinations.stream().filter(removedContacts::contains).collect(Collectors.toList()));
                            finalFailedRemovedContacts.addAll(mappedDestination);
                            if (!mappedDestination.isEmpty()) failurePairingMap.put(emergUser, mappedDestination);
                        });
                    }
                    knLogger.debug(methodName, "failurePairingMap-- ", failurePairingMap);
                    if (!failurePairingMap.isEmpty()) {
                        knLogger.error(methodName, "Emergency Destination Mapping Exists ");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGENCY_DESTINATION_MAPPING_EXISTS_FOR_CONTACT,
                                "Emergency Destination Mapping Exists --", CORP_CONTACT_MANAGER,
                                MODIFY_SUBS_CONTACT_LIST, "", Arrays.asList(finalFailedRemovedContacts).toString(), "");
                    }
                }
                if ((contactListDTO.getAddedMdnList() == null || contactListDTO.getAddedMdnList().isEmpty()) &&
                        (contactListDTO.getAddedSublistIds() == null || contactListDTO.getAddedSublistIds().isEmpty())) {
                    contactInfoUtil.updateSubscribersContactCount(mdnList, MAX_LIMIT_VALIDATION_NOT_REQUIRED, xdmsHomePttId, persisterTxn);
                } else {
                    contactInfoUtil.updateSubscribersContactCount(mdnList, corpProfile.getMaxContactsPerSubsc(), xdmsHomePttId, persisterTxn);
                }
                Collection<Integer> sublistIds = corpSublistList.getAddedSublistIds();
                var cclSublistIds = new ArrayList<>(sublistIds);
                var subsListsIds = KnGeneralUtil.splitList(cclSublistIds, BULK_UPDATE_SIZE);
                List<Boolean> commonContactList = new ArrayList<>();
                for (var sublist : subsListsIds) {
                    var isCommonContactList = corpSubsProvInfoUtil.isCommonContactList(sublist, xdmsHomePttId, persisterTxn);
                    if (Boolean.TRUE == isCommonContactList) {
                        commonContactList.add(isCommonContactList);
                        break;
                    }
                }
                if (!commonContactList.isEmpty()) {
                    List<String> profileMdns = corpSubsProvInfoUtil.getProfileMdnByBaseMdn(mdn, xdmsHomePttId, persisterTxn);
                    Map<String, Collection<KnDocChangeListDTO>> profileMdnEtagMap = corpUserProfileUtil.profileMdnEtagUpdate(
                            null, String.valueOf(corpId), profileMdns, null, xdmsHomePttId, persisterTxn);
                    respDTO.setProfileMdnEtagMap(profileMdnEtagMap);
                    if (null != profileMdnEtagMap && !profileMdnEtagMap.isEmpty()) {
                        respDTO.setMcsXcapRootUriMap(genInfoUtil.getXCAPRootURI(new ArrayList<>(profileMdnEtagMap.keySet()), persisterTxn, true));
                    }
                }
                respDTO.setMdnCorpId(corpId);
                populate(respDTO);
                if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                    respDTO.setFailedDataList(responseDTO.getFailedDataList());
                }
                //Added for the SDD req
                if (subscProfile.getClientType() == DISPATCH_CLIENT.value()
                        || subscProfile.getClientType() == THIRDPARTYDISPATCHERCLIENT.value()) {
                    knLogger.debug("Its a dispatch client we will send to job");
                    respDTO.setEnabledDispatchMemList(KnCorpCommonInfoUtil.convertSubscDTOToStrList(finalMissingContacts.get(mdn)));
                    respDTO.setDisabledDispatchMemList(mdnContactListMap.get(mdn));
                }
            } catch (KnCorpBOException e) {
                knLogger.error(methodName, "KnCorpBOException occured while modifying subscribers contact - ", e);
                populate(respDTO, e);
            } catch (Exception e) {
                knLogger.error(methodName, "Exception occured while modifying subscribers contact - ",
                        new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
                populate(respDTO, e);
            }
            knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
            return respDTO;
        }

        public KnCorpResponseDTO getLiEvents(KnIPCorpSubscContactListDTO contactListDTO, List<String> mdns, Integer dbSublistId, KnPersisterTxn persisterTxn) {
            String methodName = "getLiEvents(KnIPCorpSubscContactListDTO, List<String>, Integer, KnPersisterTxn)";
            //Step: creating Response DTO object
            KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
            int corpId = contactListDTO.getCorpId();
            try {
                KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(corpId),
                        CORP_PROFILE, false, persisterTxn);
                knLogger.info(methodName, "Corporate Profile - ", corpProfile);
                String xdmsHomePttId = corpProfile.getXdmsHome();
                KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(contactListDTO.getSubscriberMdn(),
                        KnProfileTypes.PUBLIC_PROFILE, true, persisterTxn);
                String mdn = subscProfile.getMdn();

                KnCorpMdnListPersistDTO corpMdnListPersistDto = new KnCorpMdnListPersistDTO();
                corpMdnListPersistDto.setCorpId(corpId);
                corpMdnListPersistDto.setAddedMdnList(contactListDTO.getAddedMdnList());
                corpMdnListPersistDto.setMdn(contactListDTO.getSubscriberMdn());
                corpMdnListPersistDto.setRemovedMdnList(contactListDTO.getRemovedMdnList());
                //get mdn details for added mdn list, also filters out the external contacts present if any
                knLogger.debug(methodName, "Before getPoCSubscriberInfo for input corpMdnListPersistDto - ",
                        corpMdnListPersistDto);
                KnMdnDetailsPersistDTO pocMdnPersistDto = contactInfoUtil.getPoCSubscriberInfo(corpMdnListPersistDto,
                        xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "pocMdnPersistDto - ", pocMdnPersistDto.getAddedMdnDTO());

                //Get the distinct members from added sublist Id list
                List<KnCorpSubscriberDTO> distAddedSublistMemLst = sublistInfoUtil.getSublistDistinctMembers(contactListDTO.getAddedSublistIds(), corpId, xdmsHomePttId, persisterTxn);
                //Get the distinct added members among added sublis id list and added member list
                Collection<KnCorpSubscriberDTO> subscriberList =
                        contactInfoUtil.getDistinctMembersList(pocMdnPersistDto.getAddedMdnDTO(), distAddedSublistMemLst, corpId, xdmsHomePttId, persisterTxn);
                knLogger.debug(methodName, "subscriberList determination - ", subscriberList);

                Map<String, Collection<String>> subsContactMap =
                        contactInfoUtil.getSubscribersContactList(mdns, xdmsHomePttId, persisterTxn);
                Map<String, Collection<KnCorpSubscriberDTO>> subscrNewAddedContMap = new HashMap<>();
                subscrNewAddedContMap.put(mdn, subscriberList);
                Map<String, Collection<KnCorpSubscriberDTO>> finalMissingContacts = commonInfoUtil.filterMemberToBeAddedToSubscriber(subsContactMap, subscrNewAddedContMap);
                knLogger.debug(methodName, "finalMissingContacts determination 1 - ", finalMissingContacts);
                Collection<KnSubscriberDTO> addedContactList = new ArrayList<>();
                if (finalMissingContacts != null && !finalMissingContacts.isEmpty()) {
                    if (finalMissingContacts.get(mdn) != null) {
                        for (KnCorpSubscriberDTO subscDTO : finalMissingContacts.get(mdn)) {
                            if (!subscDTO.getMdn().equals(mdn)) {
                                KnSubscriberDTO subsc = new KnSubscriberDTO();
                                subsc.setMdn(subscDTO.getMdn());
                                subsc.setAliasMdn(subscDTO.getAliasMdn());
                                subsc.setUserId(subscDTO.getUserId());
                                String activefs2 = subscDTO.getSubsActiveFS2();
                                if (subscProfile.getClientMajorVersion() < PROTOCOL_VERSION_16 && activefs2 != null) {
                                    activefs2 = KnGeneralUtil.convertActiveFs2toHexActiveFs1(activefs2);
                                } else if (activefs2 != null) {
                                    activefs2 = KnGeneralUtil.calculateActiveFeatureSetBasedOnPv(activefs2, subscProfile.getClientMajorVersion());
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
                    }
                }
                knLogger.debug(methodName, "addedContactList- ", addedContactList);

                Collection<KnCorpSubscriberDTO> removedMembersList = contactInfoUtil.getSubscribersInfo(contactListDTO.getRemovedMdnList(), xdmsHomePttId, persisterTxn);

                //Get the distinct members from added sublist Id list
                List<KnCorpSubscriberDTO> distDeletedSublistMemLst = sublistInfoUtil.getSublistDistinctMembers(contactListDTO.getRemovedSublistIds(), corpId, xdmsHomePttId, persisterTxn);
                //Get the distinct added members among added sublis id list and added member list
                Collection<KnCorpSubscriberDTO> removedSubscriberList =
                        contactInfoUtil.getDistinctMembersList(removedMembersList, distDeletedSublistMemLst, corpId, xdmsHomePttId, persisterTxn);

                LinkedList<String> removedContactMdnList = new LinkedList<>();
                if (removedSubscriberList != null && !removedSubscriberList.isEmpty()) {
                    for (KnCorpSubscriberDTO subscriberDTO : removedSubscriberList) {
                        removedContactMdnList.add(subscriberDTO.getMdn());
                    }
                }
                knLogger.debug(methodName, "removedContactMdnList determination - ", removedContactMdnList);
                String targetMdn = mdn;
                ArrayList<String> targetProfileMdn = new ArrayList<>();
                targetProfileMdn.add(targetMdn);

                List<String> realMdns = groupInfoUtil.getRealMdns(targetProfileMdn, xdmsHomePttId, persisterTxn);
                targetMdn = realMdns.get(0);

                LinkedList<KnLIEventDTO> liEventList = new LinkedList<>();
                boolean exists = KnLIEventHandler.isTargetMDN(targetMdn);
                if (exists) {
                    KnLIEventDTO liEventDTO = new KnLIEventDTO();
                    liEventDTO.setMdn(targetMdn);
                    liEventDTO.setMcpttId(subscProfile.getMcpttId());
                    Collection<String> addedMembers = new ArrayList<>();
                    for (KnSubscriberDTO subsc : addedContactList) {
                        addedMembers.add(subsc.getMdn());
                    }
                    liEventDTO.setAddedMembers(KnCorpCommonInfoUtil.removeProfileMdn(addedMembers, xdmsHomePttId, persisterTxn));
                    liEventDTO.setDeletedMembers(KnCorpCommonInfoUtil.removeProfileMdn(removedContactMdnList, xdmsHomePttId, persisterTxn));
                    short contact = 1;
                    liEventDTO.setDocumentType(contact);
                    liEventDTO.setPttServerId(xdmsHomePttId);
                    liEventDTO.setAction(KnLIConstants.MODIFY_CONTACTS);
                    liEventDTO.setErrorCode(KnLIConstants.SUCCESS_CODE);
                    liEventList.add(liEventDTO);
                }
                respDTO.setLiEventList(liEventList);
            } catch (KnDAOException e) {
                knLogger.error(methodName, "KnDAOException occurred while modifying subscribers contact - ", e);
                populate(respDTO, e);
            } catch (KnCorpBOException e) {
                knLogger.error(methodName, "KnCorpBOException occurred while modifying subscribers contact - ", e);
                populate(respDTO, e);
            } catch (Exception e) {
                knLogger.error(methodName, "Exception occurred while modifying subscribers contact - ",
                        new KnException(BOEntity.INTERNAL_ERROR, e.getMessage(), e));
                populate(respDTO, e);
            }
            knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
            return respDTO;
        }
    }
