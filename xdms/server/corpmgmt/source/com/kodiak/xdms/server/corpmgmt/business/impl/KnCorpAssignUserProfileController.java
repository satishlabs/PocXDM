package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.lieventhandler.dto.KnLIEventDTO;
import com.kodiak.utilities.lieventhandler.handler.KnLIConstants;
import com.kodiak.utilities.lieventhandler.handler.KnLIEventHandler;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnExtProfileDetails;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubscriberDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpAssignUserProfileController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPAuthUserPermissionInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookIPDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.hook.KnCorpHookRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnActions;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.DISABLED;
import static com.kodiak.xdms.server.common.resources.KnConstants.COMMON_CONTACTLIST_PERSUB;
import static com.kodiak.xdms.server.common.resources.KnConstants.COMMON_CONTACTLIST_SIZE;


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT;
import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.PUBLIC_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populateXdmResponseFroomHook;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isObjectNull;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isObjectNullOrEmpty;
import static com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes.CORP_CONTACT_MANAGER;
import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.MODIFY_SUBS_CONTACT_LIST;

public class KnCorpAssignUserProfileController implements ICorpAssignUserProfileController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpAssignUserProfileController.class);

    private KnCorpContactInfoUtil contactInfoUtil;
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnCorpGroupInfoUtil groupInfoUtil;
    private KnGeneralUtil generalUtil;
    private KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private KnGenInfoUtil genInfoUtil;
    private KnCorpUserProfileUtil corpUserProfileUtil;

    public KnCorpAssignUserProfileController() {
        contactInfoUtil = new KnCorpContactInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        commonInfoUtil = new KnCorpCommonInfoUtil();
        groupInfoUtil = new KnCorpGroupInfoUtil();
        corpUserProfileUtil = new KnCorpUserProfileUtil();
        generalUtil = new KnGeneralUtil();
        corpSubsProvInfoUtil = new KnCorpSubsProvInfoUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
    }

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
            if (contactListDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
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
            /*knLogger.info(methodName, "Invoking Validation FW - ", valPersistDTO);
            validatorFW.validate(valPersistDTO);
            knLogger.debug(methodName, "Validation completed successfully");*/

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
                corpSublist.setSublistName((com.kodiak.xdms.server.corpmgmt.resources.KnConstants.PRIVATE_LIST_NAME) + subscProfile.getMdn());
                corpSublist.setSublistType(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBLIST_TYPE_PRIVATE_CONTACTLIST);
                corpSublist.setDistributionPolicy(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DIST_POLICY_PRIVATE_CONTACTLIST);
                //create corpList entry
                KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
                privateListId = genInfoUtil.retrieveIdForTable(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.CORP_LIST_TABLE, xdmsHomePttId, com.kodiak.xdms.server.corpmgmt.resources.KnConstants.CORP_LIST_TABLE_PK, com.kodiak.xdms.server.corpmgmt.resources.KnConstants.FALSE, com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DUAL_DATA_STORE);
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
                String targetMdn = mdn;
                if (isUPMCall) {
                    ArrayList<String> targetProfileMdn = new ArrayList<>();
                    targetProfileMdn.add(mdn);
                    List<String> realMdns = groupInfoUtil.getRealMdns(targetProfileMdn, xdmsHomePttId, persisterTxn);
                    targetMdn = realMdns.get(0);
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
                    liEventDTO.setAddedMembers(KnCorpCommonInfoUtil.removeProfileMdn(addedMembers, xdmsHomePttId, persisterTxn));
                    liEventDTO.setDeletedMembers(KnCorpCommonInfoUtil.removeProfileMdn(doc.getRemovedContactList(), xdmsHomePttId, persisterTxn));
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
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            populate(respDTO, e);
        }
        knLogger.debug(methodName, "EXIT: Returning Response - ", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO setTargetPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, KnPersisterTxn persisterTxn) {
        String methodName = "setTargetPermissions(KnIPAuthUserPermissionInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", ipAuthUserPermissionInfoDTO);
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        try {
            String corpId = String.valueOf(ipAuthUserPermissionInfoDTO.getCorpId());
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(corpId, CORP_PROFILE, false, persisterTxn);
            String xdmsHome = corpProfile.getXdmsHome();
            String authorizedMdn = ipAuthUserPermissionInfoDTO.getAuthorizedMdn();
            KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(authorizedMdn, PUBLIC_PROFILE, false, persisterTxn);
            if (subscProfile.getCorpId() <= 0) {
                knLogger.error(methodName, "Invalid Corporate Subscriber passed. MDN - ", authorizedMdn);
                throw new KnCorpBOException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid Corp Subscriber");
            }
            boolean isUpmCall = ipAuthUserPermissionInfoDTO.isUpmCall();
            boolean isAllAuTask = ipAuthUserPermissionInfoDTO.isAllAuTask();
            //Invoking custom hook
            KnCorpHookRespDTO responseDTO = new KnCorpHookRespDTO();
            Map<String, Object> customParams = ipAuthUserPermissionInfoDTO.getCustomParamMap();
            if (ipAuthUserPermissionInfoDTO.getHierarchyType() == KnConstants.HIERARCHY_TYPE.HIERARCHY) {
                customParams.put(KnConstants.CURRENT_MASTER_LIST_ETAG, corpProfile.getCorpMasterListEtag());
                customParams.put(KnConstants.PERSISTER_TXN, persisterTxn);
                customParams.put(KnConstants.PTT_SERVER_ID, xdmsHome);
                ipAuthUserPermissionInfoDTO.setCustomParamMap(customParams);
                KnCorpHookIPDTO hookIPDTO = new KnCorpHookIPDTO();
                hookIPDTO.setAction(KnActions.ACTIONS.SET_AUTH_USER_PERMISSIONS);
                hookIPDTO.setData(ipAuthUserPermissionInfoDTO);
                KnProcessInvokerImpl processInvoker = KnProcessInvokerImpl.getInstance();
                Object hookResp = processInvoker.invokeHook(KnConstants.CUSTOM_CORP_USER_HOOK, hookIPDTO);
                if (hookResp instanceof KnCorpHookRespDTO) {
                    responseDTO = (KnCorpHookRespDTO) hookResp;
                    if (com.kodiak.xdms.server.common.resources.KnConstants.RESPONSE_STATUS.FAILURE.value() == responseDTO.getStatus()) {
                        knLogger.error(methodName, "Returning Failure response");
                        populateXdmResponseFroomHook(responseDTO, respDTO);
                        return respDTO;
                    }
                }
            }
            KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
            //KnCorpMcpttFeaturePersistDTO authUserListPersistDTO = new KnCorpMcpttFeaturePersistDTO();
            Collection<String> targetMdnList = new ArrayList<>();
            Collection<String> authMdn = new ArrayList<>();
            authMdn.add(authorizedMdn);
            Collection<KnTargetMdnPermBitInfo> targetMdnPermBitInfos = ipAuthUserPermissionInfoDTO.getTargetMdnPermissionBitInfoList();
            targetMdnList.addAll(targetMdnPermBitInfos.stream().map(KnTargetMdnPermBitInfo::getMdn).collect(Collectors.toList()));
            knLogger.debug(methodName, "targetMdnList - ", KnGDPRTemplate.mdnList(targetMdnList));
            Map<String, KnCorpSubscriberDTO> contDetailsMap = contactInfoUtil.getSubsribersCorporateDetails(targetMdnList, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "contDetailsMap - ", KnGDPRTemplate.mapKeyMdn(contDetailsMap));
            Map<String, Collection<String>> authorizedMdnContactList = contactInfoUtil.getSubscribersContactList(authMdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "authorizedMdnContactList - ", KnGDPRTemplate.mapKeyValueListMdn(authorizedMdnContactList));
            Collection<String> targetNotInContactListOfAuthMdn = null;
           /* if (authorizedMdnContactList != null && !authorizedMdnContactList.isEmpty()) {
                Collection<String> authMdnContactList = authorizedMdnContactList.get(authorizedMdn);
                targetNotInContactListOfAuthMdn = targetMdnList.stream().filter(target -> !authMdnContactList.contains(target)).collect(Collectors.toList());
                knLogger.debug(methodName, "targetNotInContactListOfAuthMdn - ", KnGDPRTemplate.mdnList(targetNotInContactListOfAuthMdn));
                Collection<String> targetInContactListOfAuthMdn = targetMdnList.stream().filter(authMdnContactList::contains).collect(Collectors.toList());
                knLogger.debug(methodName, "targetInContactListOfAuthMdn - ", KnGDPRTemplate.mdnList(targetInContactListOfAuthMdn));
                Collection<String> targetMdnDoNotHaveAuthMdnAsContact = new ArrayList<>();
                Map<String, Collection<String>> targetMdnContactList = contactInfoUtil.getSubscribersContactList(targetInContactListOfAuthMdn, xdmsHome, persisterTxn);
                knLogger.debug(methodName, "targetMdnContactList - ", KnGDPRTemplate.mapKeyValueListMdn(targetMdnContactList));
                if (targetMdnContactList != null && !targetMdnContactList.isEmpty()) {
                    targetMdnDoNotHaveAuthMdnAsContact.addAll(targetMdnContactList.entrySet().stream()
                            .filter(map -> !map.getValue().contains(authorizedMdn))
                            .map(Map.Entry::getKey).collect(Collectors.toList()));
                    targetMdnDoNotHaveAuthMdnAsContact.addAll(targetInContactListOfAuthMdn.stream()
                            .filter(target -> !targetMdnContactList.keySet().contains(target)).collect(Collectors.toList()));
                    authUserListPersistDTO.setTargetMdnDoNotHaveAuthMdnAsContact(targetMdnDoNotHaveAuthMdnAsContact);
                } else {
                    authUserListPersistDTO.setTargetMdnDoNotHaveAuthMdnAsContact(targetInContactListOfAuthMdn);
                }
                knLogger.debug(methodName, "targetMdnDoNotHaveAuthMdnAsContact - ", KnGDPRTemplate.mdnList(targetMdnDoNotHaveAuthMdnAsContact));
                authUserListPersistDTO.setTargetNotInContactListOfAuthMdn(targetNotInContactListOfAuthMdn);
            } else {
                authUserListPersistDTO.setTargetNotInContactListOfAuthMdn(authMdn);
            }*/

            String userProfileId = subscProfile.getUserProfileId();
            knLogger.info(methodName, " userProfileId:", userProfileId);
            /*boolean isUPMSharingEnable = false;
            ArrayList<String> upmId = new ArrayList<>();
            upmId.add(userProfileId);
            Map<String, Integer> upmOwnerList = contactInfoUtil.getUserProfileOwnerinfo(upmId, xdmsHome, persisterTxn);
            if(!upmOwnerList.isEmpty()){
                isUPMSharingEnable = true;
            }
            knLogger.debug(methodName,"isUPMSharingEnable :",isUPMSharingEnable);*/

            List<KnUserprofileSharedlistDTO> upmSharedList = corpUserProfileUtil.getUserProfileSharedListByUpmId(userProfileId, xdmsHome, persisterTxn);
            boolean isUPMSharingEnabled = false;
            if (upmSharedList != null) {
                // sharedCorpList should contains ownerCorpId and sharedCorpId then isUPMSharingEnabled is made true.
                for (KnUserprofileSharedlistDTO getUPMSharedDetails : upmSharedList) {
                    if (Objects.equals(getUPMSharedDetails.getOwnerCorpId(), Integer.valueOf(corpId)) && getUPMSharedDetails.getSharedCorpId() == subscProfile.getCorpId()) {
                        isUPMSharingEnabled = true;
                        break;
                    }
                }
            }
            knLogger.info(methodName, "isUPMSharingEnabled :", isUPMSharingEnabled);

            /*authUserListPersistDTO.setAuthorizedMdn(authorizedMdn);
            authUserListPersistDTO.setCorpId(subscProfile.getCorpId());
            authUserListPersistDTO.setSubsFS2(subscProfile.getSubscriberFS2());
            authUserListPersistDTO.setInputDTO(ipAuthUserPermissionInfoDTO);
            authUserListPersistDTO.setSubsClientType(subscProfile.getClientType());
            authUserListPersistDTO.setAmbientListening(corpProfile.getAmbientListening() == ENABLED);
            authUserListPersistDTO.setDiscreteListening(corpProfile.getDiscreteListening() == ENABLED);
            authUserListPersistDTO.setUserCheck(corpProfile.getUserCheck() == ENABLED);
            authUserListPersistDTO.setUserSvcCtrl(corpProfile.getUserSvcCtrl() == ENABLED);
            authUserListPersistDTO.setEmergFeature(corpProfile.getEmergFeature() == ENABLED);
            authUserListPersistDTO.setMcVideoFeature(corpProfile.getMcVideoEnabled() == ENABLED);
            authUserListPersistDTO.setMcVideoUnCfrmPullFeature(corpProfile.getMcVideoUnCfrmPullEnabled() == ENABLED);*/
            Collection<Long> targetBitLong = new ArrayList<>();
            for (KnTargetMdnPermBitInfo targetMdnPermBitInfo : targetMdnPermBitInfos) {
                BitSet bitSet = new BitSet();
                corpSubsProvInfoUtil.integerToBitsConversion(bitSet, targetMdnPermBitInfo);
                targetBitLong.add(featureSetUtil.convertBitSetToLong(bitSet));
            }
            //authUserListPersistDTO.setTargetBits(targetBitLong);
            Map<String, KnCorpSubscriberDTO> targetmdns = new HashMap<>();
            targetmdns.putAll(contDetailsMap);
            if (isUpmCall && ipAuthUserPermissionInfoDTO.getTargetMdnPermissionBitInfoList() != null) {
                for (KnTargetMdnPermBitInfo obj : ipAuthUserPermissionInfoDTO.getTargetMdnPermissionBitInfoList()) {
                    if (ipAuthUserPermissionInfoDTO.getCorpId() != targetmdns.get(obj.getMdn()).getCorpId()) {
                        //hence this is an external subsciber
                        if (!((obj.getAmbientListening() != null && obj.getAmbientListening() == 0) &&
                                (obj.getDiscreteEnabled() != null && obj.getDiscreteEnabled() == 0) &&
                                (obj.getDiscreteListening() != null && obj.getDiscreteListening() == 0) &&
                                (obj.getMcVideoUnConfirmedPull() != null && obj.getMcVideoUnConfirmedPull() == 0) &&
                                (obj.getUserCheck() != null && obj.getUserCheck() == 0) &&
                                (obj.getUserEnable() != null && obj.getUserEnable() == 0))) {
                            targetmdns.remove(obj.getMdn());//removing the external subs which is not having any permissions(KnSubsMemShipValidationRule)
                        }
                    }
                }
            }
            /*authUserListPersistDTO.setTargetInfo(targetmdns);
            authUserListPersistDTO.setUPMSharingEnabled(isUPMSharingEnabled);

            knLogger.debug(methodName, "Invoking ValidationFW. DTO - ", authUserListPersistDTO);*/
            /*validatorFW.validate(authUserListPersistDTO);
            knLogger.info(methodName, "Validation completed Successfully.");*/
            Collection<String> mdnList = new ArrayList<>();
            mdnList.add(authorizedMdn);
            Map<String, Long> authEtagMap = corpSubsProvInfoUtil.seleteFromAuthDoc(mdnList, xdmsHome, persisterTxn);
            // Business Logics:
            Collection<KnMcpttPermissionDTO> insertToMcpttInfo = new ArrayList<>();
            Collection<KnMcpttPermissionDTO> updateToMcpttInfo = new ArrayList<>();
            Collection<String> targetMdnForDelete = new ArrayList<>();
            Map<String, Integer> targetForResourceListUpdate = new HashMap<>();

            Collection<KnMcpttPermissionDTO> insertToMcpttInfoForProfileMdns = new ArrayList<>();
            Collection<KnMcpttPermissionDTO> updateToMcpttInfoForProfileMdns = new ArrayList<>();
            Collection<String> targetMdnForDeleteForProfileMdns = new ArrayList<>();

            List<String> profileMdnList = new ArrayList<>();
            profileMdnList = ipAuthUserPermissionInfoDTO.getTargetProfileMdnPermissionBitInfoList().stream().map(KnTargetMdnPermBitInfo::getMdn).collect(Collectors.toList());

            List<String> baseTuList = new ArrayList<>(targetMdnList);
            Map<String, List<String>> targetBaseMdnsProfileMdnsMapping = corpUserProfileUtil.getProfileMdnListByBaseMdnsList(baseTuList, xdmsHome, false, persisterTxn);


            Map<String, KnMcpttPermissionDTO> mcpttPermissionMap = corpSubsProvInfoUtil.getAuthUserPermissions(authorizedMdn, xdmsHome, persisterTxn);
            List<String> mdns = new ArrayList<>();
            knLogger.debug(methodName, "mcpttPermissionMap - ", mcpttPermissionMap);
            //adding profile mdns skipping au and tu contact validation.
            Collection<KnTargetMdnPermBitInfo> targetProfileMdnPermissionBitInfoList
                    = ipAuthUserPermissionInfoDTO.getTargetProfileMdnPermissionBitInfoList();
            knLogger.debug("targetProfileMdnPermissionBitInfoList :", targetProfileMdnPermissionBitInfoList);
            if (targetProfileMdnPermissionBitInfoList != null && !targetProfileMdnPermissionBitInfoList.isEmpty()) {
                targetMdnPermBitInfos.addAll(targetProfileMdnPermissionBitInfoList);


                targetMdnList.addAll(profileMdnList);
                knLogger.debug(methodName, "profileMdnList :-", KnGDPRTemplate.mdnList(profileMdnList));
                Map<String, KnCorpSubscriberDTO> subsDetailsMap = contactInfoUtil.getSubsribersCorporateDetails(profileMdnList, xdmsHome, persisterTxn);
                contDetailsMap.putAll(subsDetailsMap);
            }
            Collection<String> insertTargetMdnList = new ArrayList<>(targetMdnList);
            knLogger.debug(methodName, "insertTargetMdnList :-", KnGDPRTemplate.mdnList(insertTargetMdnList));
            // Update
            Map<String, Integer> mapListForPrivacy = new HashMap<>();
            knLogger.debug(methodName, "---> corpProfile.getPrivacyAmbDiscListenFlag() - ", corpProfile.getPrivacyAmbDiscListenFlag());
            //update and delete
            List<String> privacyEnabledMdns = new ArrayList<>();

            //fetching only the list of common contact mdns
            List<String> allCommonContactMdns = sublistInfoUtil.getCommonContactListForMdns(authorizedMdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "allCommonContactMdns :-", KnGDPRTemplate.mdnList(allCommonContactMdns));
            //fetching all the contacts of the subscriber duplicate also
            List<String> listOfAllContactMdns = sublistInfoUtil.getAllSublistContactMdns(authorizedMdn, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "listOfAllContactMdns :-", KnGDPRTemplate.mdnList(listOfAllContactMdns));


            List<String> commonContactMdns = new ArrayList<>();
            for (KnTargetMdnPermBitInfo obj : targetMdnPermBitInfos) {
                if (!allCommonContactMdns.isEmpty() && allCommonContactMdns.contains(obj.getMdn())) {
                    commonContactMdns.add(obj.getMdn());
                }
            }
            knLogger.debug(methodName, "common contacts present in the request :-", KnGDPRTemplate.mdnList(commonContactMdns));
            Map<String, Integer> commonContactsAndCommonAUMapping = new HashMap<>();

            ////MINT-14813 - ConcurrentModificationException observed on authMdnsProfileMdns variable hence changed to CopyOnWriteArrayList
            List<String> authMdnProfileMdnList = corpSubsProvInfoUtil.getProfileMdnByBaseMdn(authorizedMdn, xdmsHome,
                    persisterTxn);
            CopyOnWriteArrayList<String> authMdnsProfileMdns = new CopyOnWriteArrayList<>(authMdnProfileMdnList);
            for (KnTargetMdnPermBitInfo target : targetMdnPermBitInfos) {
                if (commonContactMdns.contains(target.getMdn()) && mcpttPermissionMap.get(target.getMdn()) != null) {
                    Integer reqCommonAU = target.getCommonContact();
                    Integer dbCommonAU = mcpttPermissionMap.get(target.getMdn()).getCommonAu();
                    String targetMdn = target.getMdn();
                    if ((DISABLE.equals(reqCommonAU) && ENABLE.equals(dbCommonAU) && Collections.frequency(listOfAllContactMdns, target.getMdn()) > 1)) {
                        commonContactsAndCommonAUMapping.put(target.getMdn(), DISABLE);
                        commonInfoUtil.populateForTheProfileMdns(commonContactsAndCommonAUMapping, targetBaseMdnsProfileMdnsMapping.get(target.getMdn()), DISABLE);
                    } else if ((ENABLE.equals(reqCommonAU) && DISABLE.equals(dbCommonAU)) && commonContactMdns.contains(targetMdn)) {
                        commonContactsAndCommonAUMapping.put(target.getMdn(), ENABLE);
                        commonInfoUtil.populateForTheProfileMdns(commonContactsAndCommonAUMapping, targetBaseMdnsProfileMdnsMapping.get(target.getMdn()), ENABLE);
                    } else if ((ENABLE.equals(reqCommonAU) && ENABLE.equals(dbCommonAU)) || (reqCommonAU == null && ENABLE.equals(dbCommonAU))) {
                        //null
                        commonContactsAndCommonAUMapping.put(target.getMdn(), null);
                        commonInfoUtil.populateForTheProfileMdns(commonContactsAndCommonAUMapping, targetBaseMdnsProfileMdnsMapping.get(target.getMdn()), null);
                    }
                    knLogger.debug(methodName, "after the mapping population ", commonContactsAndCommonAUMapping);
                }
            }
            //update scenario for the upm call
            //fetching the auth permissinos for the aus profile mdns
            List<String> targetMdns = new ArrayList<>(mcpttPermissionMap.keySet());
            Map<String, List<KnMcpttPermissionDTO>> mcpttPermissionAsProfileMdns = corpSubsProvInfoUtil.getTargetMdnListPermissions(authMdnsProfileMdns, xdmsHome, persisterTxn);

            //aus profile
            //if that profile mdn is having permissions
            if (!commonContactMdns.isEmpty()) {
                for (String profileMdn : authMdnsProfileMdns) {
                    if (mcpttPermissionAsProfileMdns.get(profileMdn) != null) {
                        for (KnMcpttPermissionDTO perm : mcpttPermissionAsProfileMdns.get(profileMdn)) {
                            if (perm.getCommonAu() == 0) {
                                authMdnsProfileMdns.remove(profileMdn);
                            }
                        }
                    }
                }
            }


            if (mcpttPermissionMap != null && !mcpttPermissionMap.isEmpty()) {
                targetMdnPermBitInfos.stream().filter(target -> mcpttPermissionMap.get(target.getMdn()) != null && mcpttPermissionMap.keySet().contains(target.getMdn())).forEach(target -> {
                    KnMcpttPermissionDTO dbTargetInfo = mcpttPermissionMap.get(target.getMdn()); // this is the data from the DB
                    mdns.add(target.getMdn());
                    //logic for insertion case of tus
                    Integer reqCommonAU = target.getCommonContact();
                    Integer dbCommonAU = mcpttPermissionMap.get(target.getMdn()).getCommonAu();

                    BitSet targetDBPermsBit = featureSetUtil.convertLongToBitSet(dbTargetInfo.getMcpttPerms());
                    int existingAmbientBit = targetDBPermsBit.get(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) ? 1 : 0;
                    if (target.getAmbientListening() != existingAmbientBit) {
                        targetForResourceListUpdate.put(target.getMdn(), target.getAmbientListening());
                    }
                    corpSubsProvInfoUtil.integerToBitsConversion(targetDBPermsBit, target);
                    if (target.getDiscreteListening() == 0) {
                        dbTargetInfo.setDiscreteEnabled(KnConstants.DISABLED);
                    } else {
                        dbTargetInfo.setDiscreteEnabled(dbTargetInfo.getDiscreteEnabled());
                    }

                    if (commonContactsAndCommonAUMapping.containsKey(target.getMdn())) {
                        if (commonContactsAndCommonAUMapping.get(target.getMdn()) == null) {
                            dbTargetInfo.setCommonAu(dbCommonAU);
                        } else {
                            dbTargetInfo.setCommonAu(commonContactsAndCommonAUMapping.get(target.getMdn()));
                        }
                    }
                    if (isUpmCall && ENABLE.equals(dbCommonAU) && reqCommonAU == null) {

                        dbTargetInfo.setCommonAu(null);
                    }

                    long targetBit = featureSetUtil.convertBitSetToLong(targetDBPermsBit);
                    if (targetBit != 0) {
                        if (dbTargetInfo.getMcpttPerms() != targetBit || (!Objects.equals(reqCommonAU, dbCommonAU))) {
                            dbTargetInfo.setMcpttPerms(targetBit);
                            dbTargetInfo.setServiceAuthUserAU(contDetailsMap.get(target.getMdn()).getServiceAuthStatusAU());
                            updateToMcpttInfo.add(dbTargetInfo);
                            if (commonContactsAndCommonAUMapping.containsKey(target.getMdn()) && dbTargetInfo.getCommonAu() == 1) {
                                updateToMcpttInfoForProfileMdns.add(dbTargetInfo);
                            }
                        }
                    } else {
                        if (!(isUpmCall && ENABLE.equals(dbCommonAU) && reqCommonAU == null)) {
                            targetMdnForDelete.add(target.getMdn());
                            if (commonContactsAndCommonAUMapping.containsKey(target.getMdn())) {
                                targetMdnForDeleteForProfileMdns.add(target.getMdn());
                            }
                        }
                    }
                    if (DISABLE.equals(commonContactsAndCommonAUMapping.get(target.getMdn()))) {
                        targetMdnForDeleteForProfileMdns.add(target.getMdn());
                    }
                    if (ENABLE.equals(commonContactsAndCommonAUMapping.get(target.getMdn()))) {
                        insertToMcpttInfoForProfileMdns.add(dbTargetInfo);
                    }

                    insertTargetMdnList.remove(target.getMdn());
                    if (corpProfile.getPrivacyAmbDiscListenFlag() != null && corpProfile.getPrivacyAmbDiscListenFlag() == 1 && contDetailsMap.get(target.getMdn()) != null && contDetailsMap.get(target.getMdn()).getClientPVmajorVer() >= PROTOCOL_VERSION_16) {
                        knLogger.debug(methodName, "--->target.getAmbientListening() - ", target.getAmbientListening());
                        knLogger.debug(methodName, "--->target.getDiscreteListening() - ", target.getDiscreteListening());
                        mapListForPrivacy.put(target.getMdn(), (target.getAmbientListening() == ENABLED
                                || target.getDiscreteListening() == ENABLED) ? DISABLED : ENABLED);
                        if (mapListForPrivacy.get(target.getMdn()) == DISABLED) {
                            knLogger.debug(methodName, "--->privacyFlag - ", mapListForPrivacy.get(target.getMdn()), "target.getMdn()-" + KnGDPRTemplate.mdn(target.getMdn()));
                            privacyEnabledMdns.add(target.getMdn());
                        }
                    }
                });
                knLogger.debug(methodName, "updateToMcpttInfo - ", updateToMcpttInfo); // update to MCPTT.
                knLogger.debug(methodName, "targetMdnForDelete - ", KnGDPRTemplate.mdnList(targetMdnForDelete)); // delete from MCPTT and Authorization.
                knLogger.debug(methodName, "targetForResourceListUpdate - ", targetForResourceListUpdate); // update to CorpResourceList

                knLogger.debug(methodName, "updateToMcpttInfoForProfileMdns - ", updateToMcpttInfoForProfileMdns); // update to MCPTT.
                knLogger.debug(methodName, "deleteFromMcpttPermInfoForProfileMdns - ", KnGDPRTemplate.mdnList(targetMdnForDeleteForProfileMdns));
                if (!updateToMcpttInfo.isEmpty()) {
                    corpSubsProvInfoUtil.updateToMcpttPermInfo(updateToMcpttInfo, xdmsHome, persisterTxn);
                }
                if (!updateToMcpttInfoForProfileMdns.isEmpty()) {
                    corpSubsProvInfoUtil.updateToMcpttPermInfoForProfileMdns(updateToMcpttInfoForProfileMdns, authMdnsProfileMdns, xdmsHome, persisterTxn);
                }
                if (!targetMdnForDelete.isEmpty()) {
                    corpSubsProvInfoUtil.deleteFromMcpttPermInfo(authorizedMdn, targetMdnForDelete, xdmsHome, persisterTxn);
                }
                if (!targetMdnForDeleteForProfileMdns.isEmpty()) {
                    corpSubsProvInfoUtil.deleteFromMcpttPermInfoForProfileMdns(authMdnsProfileMdns, targetMdnForDeleteForProfileMdns, xdmsHome, persisterTxn);
                }
            }
            // Insert
            commonContactsAndCommonAUMapping.clear();
            for (KnTargetMdnPermBitInfo insertTarget : targetMdnPermBitInfos) {
                if ((ENABLE.equals(insertTarget.getCommonContact()) &&
                        commonContactMdns.contains(insertTarget.getMdn())) ||
                        (commonContactMdns.contains(insertTarget.getMdn()) &&
                                (Collections.frequency(listOfAllContactMdns, insertTarget.getMdn())) == 1)) {
                    commonContactsAndCommonAUMapping.put(insertTarget.getMdn(), 1);
                    commonInfoUtil.populateForTheProfileMdns(commonContactsAndCommonAUMapping, targetBaseMdnsProfileMdnsMapping.get(insertTarget.getMdn()), 1);
                }
            }
            knLogger.debug(methodName, "after the population ", commonContactsAndCommonAUMapping);
            knLogger.debug(methodName, "targetMdnPermBitInfos :", targetMdnPermBitInfos, " insertTargetMdnList :", KnGDPRTemplate.mdnList(insertTargetMdnList));
            targetMdnPermBitInfos.stream().filter(insertTarget -> insertTargetMdnList.contains(insertTarget.getMdn())).forEach(insertTarget -> {
                KnMcpttPermissionDTO insertPermissionDTO = new KnMcpttPermissionDTO();
                insertPermissionDTO.setCorpid(subscProfile.getCorpId()); //setting the authorized mdns corpid //sharing of roles
                insertPermissionDTO.setCorpid(subscProfile.getCorpId()); //setting the authorized mdns corpid //sharig of roles
                insertPermissionDTO.setAuthMdn(authorizedMdn);
                insertPermissionDTO.setTargetMdn(insertTarget.getMdn());
                //write the default value for the column got introduced newly

                if (commonContactsAndCommonAUMapping.containsKey(insertTarget.getMdn())) {
                    insertPermissionDTO.setCommonAu(commonContactsAndCommonAUMapping.get(insertTarget.getMdn()));
                }
                mdns.add(insertTarget.getMdn());
                BitSet targetInsertBitSet = new BitSet();
                corpSubsProvInfoUtil.integerToBitsConversion(targetInsertBitSet, insertTarget);
                if (insertTarget.getDiscreteListening() == 0) {
                    insertPermissionDTO.setDiscreteEnabled(KnConstants.DISABLED);
                }
                long permBit = featureSetUtil.convertBitSetToLong(targetInsertBitSet);
                if (permBit != 0) {
                    targetForResourceListUpdate.put(insertTarget.getMdn(), targetInsertBitSet.get(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) ? 1 : 0);
                    insertPermissionDTO.setMcpttPerms(permBit);
                    KnCorpSubscriberDTO subscriberDTO = contDetailsMap.get(insertTarget.getMdn());
                    if (subscriberDTO != null) {
                        insertPermissionDTO.setServiceAuthUserAU(subscriberDTO.getServiceAuthStatusAU());
                        insertToMcpttInfo.add(insertPermissionDTO);
                        if (commonContactsAndCommonAUMapping.containsKey(insertTarget.getMdn())) {
                            insertToMcpttInfoForProfileMdns.add(insertPermissionDTO);
                        }
                    } else {
                        knLogger.info(methodName, "subscriber info not found for mdn -", KnGDPRTemplate.mdn(insertTarget.getMdn()));
                    }
                }
                if (corpProfile.getPrivacyAmbDiscListenFlag() != null && corpProfile.getPrivacyAmbDiscListenFlag() == 1 && contDetailsMap.get(insertTarget.getMdn()) != null && contDetailsMap.get(insertTarget.getMdn()).getClientPVmajorVer() >= PROTOCOL_VERSION_16) {
                    knLogger.debug(methodName, "--->insertTarget.getAmbientListening() - ", insertTarget.getAmbientListening());
                    knLogger.debug(methodName, "--->insertTarget.getDiscreteListening() - ", insertTarget.getDiscreteListening());
                    mapListForPrivacy.put(insertTarget.getMdn(), (insertTarget.getAmbientListening() == ENABLED
                            || insertTarget.getDiscreteListening() == ENABLED) ? DISABLED : ENABLED);
                    if (mapListForPrivacy.get(insertTarget.getMdn()) == DISABLED) {
                        knLogger.debug(methodName, "--->privacyFlag - ", mapListForPrivacy.get(insertTarget.getMdn()), "insertTarget.getMdn()-" + KnGDPRTemplate.mdn(insertTarget.getMdn()));
                        privacyEnabledMdns.add(insertTarget.getMdn());
                    }
                }
            });
            knLogger.debug(methodName, "insertToMcpttInfo - ", insertToMcpttInfo); // insert to CorpResourceList
            knLogger.debug(methodName, "insertToMcpttInfoForProfileMdns - ", insertToMcpttInfoForProfileMdns);
            if (!insertToMcpttInfo.isEmpty()) {
                corpSubsProvInfoUtil.insertIntoMcpttPermInfo(insertToMcpttInfo, xdmsHome, persisterTxn);
            }
            if (!insertToMcpttInfoForProfileMdns.isEmpty()) {
                corpSubsProvInfoUtil.insertIntoMcpttPermInfoForProfileMdns(insertToMcpttInfoForProfileMdns, authMdnsProfileMdns, xdmsHome, persisterTxn);
            }

            Map<String, Collection<KnMcpttPermissionDTO>> insertTargetMdnListMap = new HashMap<>();
            Map<String, Collection<KnMcpttPermissionDTO>> updateToMcpttInfoMap = new HashMap<>();
            if (isUpmCall && !targetMdnForDelete.isEmpty()) {
                Set<String> emergencyBaseMdn = corpSubsProvInfoUtil.getBaseMdnByProfileMdns(new ArrayList<>(authMdn), xdmsHome, persisterTxn);
                Map<String, KnMcpttPermissionDTO> mcpttPermissions = corpSubsProvInfoUtil.getAuthUserPermissions(emergencyBaseMdn.iterator().next(), xdmsHome, persisterTxn);
                List<KnMcpttPermissionDTO> permissions = new ArrayList<>(mcpttPermissions.values());
                if (!permissions.isEmpty()) {
                    Predicate<KnMcpttPermissionDTO> removeNonCommonMdns = perm -> !(perm.getCommonAu() != null && perm.getCommonAu() == 1);
                    permissions.removeIf(removeNonCommonMdns);
                    Predicate<KnMcpttPermissionDTO> removeMdnsExeptDeletedTu = perm -> !(targetMdnForDelete.contains(perm.getTargetMdn()));
                    permissions.removeIf(removeMdnsExeptDeletedTu);
                    List<String> profileMdn = new ArrayList<>();
                    profileMdn.add(authorizedMdn);
                    corpSubsProvInfoUtil.insertIntoMcpttPermInfoForProfileMdns(permissions, profileMdn, xdmsHome, persisterTxn);
                    //insertTargetMdnListMap.put(authorizedMdn,permissions);
                    if (!permissions.isEmpty()) {
                        updateToMcpttInfoMap.put(authorizedMdn, permissions);
                        permissions.forEach(perm -> targetMdnForDelete.remove(perm.getTargetMdn()));
                    }

                }
                //for notifiy
            }

            Map<String, KnOPDirChgDTO> etagMap = corpSubsProvInfoUtil.updateAuthorizationImpactedTables(xdmsHome, authorizedMdn, null,
                    subscProfile.getCorpId(), persisterTxn, null);
            if (!insertToMcpttInfoForProfileMdns.isEmpty() || !targetMdnForDeleteForProfileMdns.isEmpty() || !updateToMcpttInfoForProfileMdns.isEmpty()) {
                Map<String, KnOPDirChgDTO> etagMapForProfileMdn = corpSubsProvInfoUtil.updateAuthorizationImpactedTablesForProfileMdns(xdmsHome, authMdnsProfileMdns, null,
                        subscProfile.getCorpId(), persisterTxn, null);
                etagMap.putAll(etagMapForProfileMdn);
            }
            knLogger.info(methodName, "mapListForPrivacy:: ", mapListForPrivacy);
            if (corpProfile.getPrivacyAmbDiscListenFlag() != null && corpProfile.getPrivacyAmbDiscListenFlag() == 1) {
                if (isAllAuTask) {
                    //for KnAssignTargetPermsToAllAUTask case when tu profile mdn is set to au
                    //PRIVACY_OPT_STATUS should be copied from base mdn.to keep the flag sync for both.
                    //Find the Real Mdns for the profile Mdns
                    ArrayList<String> permProfileMdnList = new ArrayList<>(mapListForPrivacy.keySet());
                    List<String> realMdns = groupInfoUtil.getRealMdns(permProfileMdnList, xdmsHome, persisterTxn);
                    //getting map of profile to base mdn.
                    //Map<String, List<String>> profileMdnOfBaseMap = corpSubsProvInfoUtil.getMapOfProfileMdnByBaseMdn(realMdns, xdmsHome, persisterTxn);
                    Map<String, Integer> mdnPrivOptsMap = corpSubsProvInfoUtil.getPrivacyOptStatus(realMdns, xdmsHome, persisterTxn);
                    //As only one profile mdn will come in the request in this flow,so get by index and override the opts value for profile mdn.
                    Integer mdnOpts = mdnPrivOptsMap.get(realMdns.get(0));
                    knLogger.debug(methodName, " Base mdn mdnOpts :", mdnOpts);
                    mapListForPrivacy.put(permProfileMdnList.get(0), mdnOpts);
                }
                corpSubsProvInfoUtil.updateToPrivacyOptStatus(mapListForPrivacy, xdmsHome, persisterTxn);
            }
            mdns.add(authorizedMdn);
            Map<String, KnCorpSubsEntitiesDTO> corpSubsEntitiesDTOMap = contactInfoUtil.getSubsEntitiesDetails(mdns, xdmsHome, persisterTxn);
            knLogger.debug(methodName, "etagMap 111 - ", KnGDPRTemplate.mapKeyMdn(etagMap));

            updateToMcpttInfoMap.put(authorizedMdn, updateToMcpttInfo);

            insertTargetMdnListMap.put(authorizedMdn, insertToMcpttInfo);
            Map<String, Collection<String>> targetMdnForDeleteMap = new HashMap<>();
            targetMdnForDeleteMap.put(authorizedMdn, targetMdnForDelete);
            //notifications for the profile mdns
            if (!insertToMcpttInfoForProfileMdns.isEmpty()) {
                authMdnsProfileMdns.forEach(profileMdn -> insertTargetMdnListMap.put(profileMdn, insertToMcpttInfoForProfileMdns));
            }
            if (!updateToMcpttInfoForProfileMdns.isEmpty()) {
                authMdnsProfileMdns.forEach(profileMdn -> updateToMcpttInfoMap.put(profileMdn, updateToMcpttInfoForProfileMdns));
            }
            if (!targetMdnForDeleteForProfileMdns.isEmpty()) {
                authMdnsProfileMdns.forEach(profileMdn -> targetMdnForDeleteMap.put(profileMdn, targetMdnForDeleteForProfileMdns));
            }
            if (!targetForResourceListUpdate.isEmpty())
                etagMap = contactInfoUtil.updateSubcribersImpactedTables(xdmsHome, targetForResourceListUpdate.keySet(), persisterTxn, etagMap);
            knLogger.debug(methodName, "etagMap 222 - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            etagMap = KnCorpCommonInfoUtil.formXcapAuthDiffNotification(etagMap, insertTargetMdnListMap, updateToMcpttInfoMap,
                    targetMdnForDeleteMap, targetForResourceListUpdate, corpSubsEntitiesDTOMap, authorizedMdn, authEtagMap.isEmpty());
            knLogger.debug(methodName, "etagMap 333 - ", KnGDPRTemplate.mapKeyMdn(etagMap));
            // todo: LI Notification Preparation. // no SDD requirements.
            respDTO.setChangeLogMap(etagMap);
            knLogger.debug(methodName, "--->privacyEnabledMdns - ", KnGDPRTemplate.mdnList(privacyEnabledMdns));

            //etag calculation starts for privacy
            Set<String> privacyEnabledMdnsSet = mapListForPrivacy.keySet();
            knLogger.debug(methodName, "--->privacyEnabledMdnsSet - ", KnGDPRTemplate.mdnSet(privacyEnabledMdnsSet));
            Map<String, Integer> etagMapPrivacy = commonInfoUtil.updateAndGetDirecEtag(privacyEnabledMdnsSet, persisterTxn, xdmsHome);
            knLogger.debug(methodName, "--->etagMapPrivacy - ", etagMapPrivacy);
            //etag calculation ends for privacy
            //notification on success starts
            if (corpProfile.getPrivacyAmbDiscListenFlag() != null && corpProfile.getPrivacyAmbDiscListenFlag() == 1) {
                knLogger.info(methodName, "--->notification on success starts in controller");
                List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
                KnOPDocChgDTO docChgDTO;
                KnOPDirChgDTO dirChgDTO;
                int i = 0;
                for (String mdn : mapListForPrivacy.keySet()) {
                    dirChgDTO = new KnOPDirChgDTO();
                    docChgDTO = new KnOPDocChgDTO();
                    Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();

                    if (contDetailsMap.get(mdn) != null) {
                        knLogger.info(methodName, "contDetailsMap.get(mdn)::" + contDetailsMap.get(mdn) + " mdn::" + KnGDPRTemplate.mdn(mdn));
                        docChgDTO.setDocumentChgType(com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                        String subsConfigDocUri = commonInfoUtil.generateSubsConfigSelUri(mdn);
                        docChgDTO.setDocUri(subsConfigDocUri);
                        docChgDTO.setNewEtag(String.valueOf(System.currentTimeMillis()));
                        chgDocList.add(docChgDTO);
                        //populating the XDM Directory DTO
                        dirChgDTO.setDocChgDTO(chgDocList);
                        dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                        dirChgDTO.setPocHome(contDetailsMap.get(mdn).getPocHome());
                        dirChgDTO.setPresenceHome(contDetailsMap.get(mdn).getPresenceHome());
                        String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                        dirChgDTO.setDirUri(dirDocUri);

                        if (privacyEnabledMdns.contains(mdn)) {
                            knLogger.debug(methodName, "--->Privacy Opt In reason code will be appearing for - " + KnGDPRTemplate.mdn(mdn));
                            dirChgDTO.setPrivacyReasonRequired(true);
                            // dirChgDTO.setProfileChanged(true);
                        }

                        // dirChgDTO.setDirPrevEtag(String.valueOf(previousEtags.get(i)));
                        //int newEtag = previousEtags.get(i) + 1;
                        //dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                        //
                        dirChgDTO.setDirPrevEtag(etagMapPrivacy.get(mdn).toString());
                        Integer newTag = etagMapPrivacy.get(mdn) + 1;
                        dirChgDTO.setDirNewEtag(newTag.toString());
                        //
                        dirChgDTO.setProtoVersion(String.valueOf(contDetailsMap.get(mdn).getClientPVmajorVer()));
                        dirChgDTO.setClientType(contDetailsMap.get(mdn).getClientType());
                        knLogger.info(methodName, "--->subsConfigDocUri::" + subsConfigDocUri + " docChgDTO::" + docChgDTO + " dirChgDTO::" + dirChgDTO + " dirDocUri::" + dirDocUri + " etagMapPrivacy::" + etagMapPrivacy);
                        knLogger.debug(methodName, " --->Comparsion==>etagMapPrivacy.get(mdn)::" + etagMapPrivacy.get(mdn) + " newTag::" + newTag);

                    }
                    //populating the Dir chg DTO to response
                    dirChgDTOs.add(dirChgDTO);
                    i++;
                    knLogger.info(methodName, " docChgDTO::" + docChgDTO + " dirChgDTO::" + dirChgDTO + " etagMapPrivacy::" + etagMapPrivacy);
                }
                respDTO.setDirChgDTOs(dirChgDTOs);
                /*if (mapListForPrivacy.size() > 0) {
                    respDTO.setProfileChanged(true);
                }*/
            }

            // notification ends
            populate(respDTO);
            //
            //pv related check at starting of logic functionality


            if (responseDTO.getFailedDataList() != null && !responseDTO.getFailedDataList().isEmpty()) {
                respDTO.setFailedDataList(responseDTO.getFailedDataList());
            }
        } catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured in setTargetPermissions  ", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Exception occured in setTargetPermissions ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        return respDTO;
    }
}
