/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.business.impl;

import com.kodiak.common.commdto.request.KnXDMPAMAccInfoDTO;
import com.kodiak.common.commdto.request.KnXDMPAMSubsProfInfoDTO;
import com.kodiak.common.commdto.request.KnXDMSubsProvInfoDTO;
import com.kodiak.common.commdto.response.KnXDMRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.ems.base.itf.KnEMSConst;
import com.kodiak.ems.base.utils.KnLicenseInfo;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetException;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.utilities.featuresetupgrade.util.KnUpgardeFSConfig;
import com.kodiak.utilities.generatealarmutil.KnAlarmConstants;
import com.kodiak.utilities.generatealarmutil.KnAlarmGeneratorUtil;
import com.kodiak.utilities.processinvoker.KnProcessInvokerException;
import com.kodiak.utilities.processinvoker.impl.KnProcessInvokerImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDispatchDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnPOCSvcConfigDTO;
import com.kodiak.xdms.server.common.dto.common.KnXDMSServiceConfigDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnContactListPersistDTO;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.business.IBulkSubsProvController;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPBulkSubsProvInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPBulkChgAuthStatusRespDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPBulkDeleteSubsRespDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPBulkRespDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCorpProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCreateSubsInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPProvDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsDispatcherDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPUpdateSubsInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPDeleteSubsRespDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMAccPoolUsageDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnPAMSubsProfInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsAddlInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnBulkSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnCorpProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnPAMAccPersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnPAMAccPoolPersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsAddlInfoPersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.FEATURE_SET.LOCATIONSUBSCRIPTION;
import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_17;


/**
 * *****************************************************************************
 * File name:   KnBulkSubsProvController.java
 * Subsystem:   Provisioning Library
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar           Feb 20 2013       7.4
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
 * *******************************************************************************
 */


public class KnBulkSubsProvController implements IBulkSubsProvController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBulkSubsProvController.class);
    private String xdmPttServerId;
    private KnGenInfoUtil genInfoUtil;
    private KnProvInfoUtil provInfoUtil;
    private KnLicenseInfo licenseInfo;
    private KnFeatureSetUtil featureSetUtil;
    private String currentFsVersion;


    public KnBulkSubsProvController() {
        knLogger.info("Constructor", "in bulk subs prov controller");
        provInfoUtil = new KnProvInfoUtil();
        genInfoUtil = KnGenInfoUtil.getInstance();
        licenseInfo = KnEMSConst.getLicenseInfo();
        featureSetUtil = KnFeatureSetUtil.getInstance();
        currentFsVersion= KnUpgardeFSConfig.getFsCurrentVersion();
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
        } catch (KnBOException e) {
            knLogger.error("Constructor", "failed to retrieve xdm Ptt Sever Id");
            knLogger.error("Constructor", e);
        }
    }

    /**
     * This method will create the Subscriber Profile for the received Input Parameters
     * This method will perform the following activities
     * -> Authenticate the User.
     * -> Validate the received input DTO data.
     * -> populate the subscriber info persistdat DTO's
     * -> Invoke the DAO Layer for DB Population
     *
     * @param bulkSubsProvInfoDTO KnIPBulkSubsProvInfoDTO
     * @return KnProvRespDTO Object
     */
    public KnOPCreateSubsInfoDTO createSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        String methodName = "createSubscribers(KnIPBulkSubsProvInfoDTO, KnPersisterTxn)";
        KnBulkSubsProfilePersistDTO subsProvPersistDTO;
        KnOPCreateSubsInfoDTO respDTO;
        List<Integer> clientTypeList = new ArrayList<Integer>();
        knLogger.info(methodName, "ENTRY: create Subscribers with DTO - ", bulkSubsProvInfoDTO, " Txn - ", persisterTxn);
        try {
            List<String> mdns = bulkSubsProvInfoDTO.getMdns();
            String firstMdn = mdns.get(0);
            //populate the Subscriber Prov Persist DTO.
            subsProvPersistDTO = new KnBulkSubsProfilePersistDTO();
            KnSubsProfileDTO subsProfileDTO = new KnSubsProfileDTO();
            subsProfileDTO.setHierarchyType(bulkSubsProvInfoDTO.getHierarchyType());
            subsProfileDTO.setMdn(firstMdn);
            subsProvPersistDTO.setMdns(bulkSubsProvInfoDTO.getMdns());
            subsProfileDTO.setXDMSHome(xdmPttServerId);
            subsProfileDTO.setFirstNetIndicator(bulkSubsProvInfoDTO.getFirstNetIndicator());
            int serviceStatusAuthUser = KnConstants.SERVICE_STATUS_AUTHUSER.ACTIVATED.value();
            subsProfileDTO.setPoCStatusAU(serviceStatusAuthUser);
            //while subscriber is created the service auth status is always set to Provisioned - 0
            // this is to set serviceauthstatus to suspend/resume(2/3) while creating subscriber in suspend/resume operation.(MDN does not exist case)
            int serviceStatusOp = KnConstants.SERVICE_STATUS_OP.PROVISIONED.value();
            subsProfileDTO.setPoCStatusOP(serviceStatusOp);

            int serviceAuthStatus = bulkSubsProvInfoDTO.getServiceAuthStatus();
            if (serviceAuthStatus != 0) {
            	subsProfileDTO.setServiceAuthStatus(bulkSubsProvInfoDTO.getServiceAuthStatus());
            }

            Integer subsClientType = bulkSubsProvInfoDTO.getClient_Type();
            // check for NNI subsciber type 7/8 and set serviceauth status 2(Activated)
            if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() 
            		|| subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()
            		|| subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value()) {
            	// incase of suspend operation we are not setting ACTIVE state to NNI subscribers
            	if (serviceAuthStatus != KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()) {
            		serviceStatusOp = KnConstants.SERVICE_STATUS_OP.ACTIVATED.value();
            		subsProfileDTO.setPoCStatusOP(serviceStatusOp);
                    subsProfileDTO.setServiceAuthStatus(genInfoUtil.calculateServiceAuthStatus(serviceStatusOp, serviceStatusAuthUser));
            	}
            }else{
                subsProfileDTO.setServiceAuthStatus(genInfoUtil.calculateServiceAuthStatus(serviceStatusOp, serviceStatusAuthUser));
            }


            String tierPackageId=null;
            Map<String,Integer> addonPackageIds=new HashMap<>();
            Map<String, Integer> addPkgIds=new HashMap<String, Integer>();
            if(bulkSubsProvInfoDTO.getPkgIdMap()!=null && bulkSubsProvInfoDTO.getPkgIdMap().get(KnConstants.ADD_ACTION)!=null)
            {
            	addPkgIds=bulkSubsProvInfoDTO.getPkgIdMap().get(KnConstants.ADD_ACTION);
            for (Entry<String, Integer> entry : addPkgIds.entrySet()) {
				if(entry.getValue().intValue()==KnConstants.TIER_PKG_TYPE.intValue())
				{
					tierPackageId=entry.getKey();
				}
				else if(entry.getValue().intValue()==KnConstants.ADDON_PKG_TYPE.intValue())
				{
					addonPackageIds.put(entry.getKey(), entry.getValue());
				}
			}
            }
            
            int publicSubscriptionType = bulkSubsProvInfoDTO.getPubSubsType();
            subsProfileDTO.setPublicSubscriptionType(publicSubscriptionType);
            int corporateSubscriptionType = bulkSubsProvInfoDTO.getCorpSubsType();
            subsProfileDTO.setCorporateSubscriptionType(corporateSubscriptionType);
            //default pariring ind
            boolean pairingIndicator = false;

            if (subsClientType == null) {
                subsClientType = KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value();
            }

            if (subsClientType != KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value()) {
                subsProfileDTO.setPairingInd(pairingIndicator);
            }


            subsProfileDTO.setSubsClientType(subsClientType);
            subsProfileDTO.setLicenseType(bulkSubsProvInfoDTO.getLicenseType());
            subsProfileDTO.setIsDefaultProfile(KnConstants.IS_DEFAULT_PROFILE);
            subsProfileDTO.setUserProfileIndex(KnConstants.USER_PROFILE_INDEX);
            subsProfileDTO.setDispatchGroupMember(KnProvConstants.DISPATCH_GROUP_MEMBER.NOT_MEMBER.value());

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);

            // changes for Corp Account Anchoring
            int corpId = -1;
            KnOPCorpProfileInfoDTO corpProfileInfoDTO = null;
            String extCorpId = bulkSubsProvInfoDTO.getExtCorpId();
            subsProfileDTO.setAccountId(extCorpId);
            subsProfileDTO.setExtCorpId(extCorpId);
            subsProfileDTO.setCorporateName(bulkSubsProvInfoDTO.getCorpName());
            String poCPttServerId = null;
            boolean updateCorpHome = false;
            String presencePttServerId = null;
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                knLogger.debug(methodName, "verifying if corporate profile already exists");
                corpProfileInfoDTO = provXDMServerDAO.retrieveCorporateProfile(extCorpId, persisterTxn);
                corpId = corpProfileInfoDTO.getCorpId();
                knLogger.debug(methodName, "Corporate Id Retrieved - ", corpId);
                corpProfileInfoDTO.setExtCorpId(extCorpId);
                Map<String, String> corpAnchorMap = provInfoUtil.validateAndgetCorpAnchorPocHome(firstMdn, corpProfileInfoDTO, mdns.size(), persisterTxn);
                poCPttServerId = corpAnchorMap.get(KnProvConstants.POC_PTT_ID_KEY);
                updateCorpHome = Boolean.parseBoolean(corpAnchorMap.get(KnProvConstants.UPDATE_CORP_HOME));
            }

            if (poCPttServerId == null) {
                poCPttServerId = provInfoUtil.getSubsPoCHome(firstMdn, persisterTxn);
            }

            presencePttServerId = provInfoUtil.getSubsPresenceHome(firstMdn, poCPttServerId, persisterTxn);
            knLogger.info(methodName, "presencePttServerId :", presencePttServerId);

            KnPOCSvcConfigDTO knPOCSvcConfigDTO = provInfoUtil.retrievePOCSvcConfig(poCPttServerId);
            subsProfileDTO.setPoCHome(poCPttServerId);
            subsProfileDTO.setPresenceHome(presencePttServerId);
            String corpFS2 = null;
            String opsCorpFS2 = null;
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                if (corpProfileInfoDTO == null) {
                    corpProfileInfoDTO = provXDMServerDAO.retrieveCorporateProfile(extCorpId, persisterTxn);
                }
                corpId = corpProfileInfoDTO.getCorpId();
                knLogger.debug(methodName, "Corporate Id Retrieved - ", corpId);
                int svcDispatchType = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getWebDispatchEnabled();
                int corpDispatchType = corpProfileInfoDTO.getWebDispatchEnabled();
                knLogger.debug(methodName, "svcDispatchType & corpDispatchType - ", svcDispatchType, corpDispatchType);
                if (corpId > 0) {
                    knLogger.debug(methodName, "corporate profile already exists with corp id [", corpId, "] ",
                            "for extCorpId ", extCorpId);
                    long corpProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                    KnCorpProfilePersistDTO corpProfilePersistDTO = populateCorpProfileInfoDTO(xdmPttServerId, subsProfileDTO);
                    if (subsProfileDTO.getCorporateName() == null) {
                        corpProfilePersistDTO.setCorporateName(corpProfileInfoDTO.getCorporateName());
                    } else {
                        corpProfilePersistDTO.setCorporateName(subsProfileDTO.getCorporateName());
                    }
                    Collections.addAll(clientTypeList, com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.ALIASMDN.value(),
                            com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.GROUPMDN.value(), com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value());
                    // update the Corp home if poc is full and subscriber moving to new poc (for load based partioning)
                    if (updateCorpHome) {
                        // checks if count for su/sg client is greater than 0 then we will go for alarm generation.
                        if(provXDMServerDAO.getSubsCountOfClientTypeForCorp(corpId,clientTypeList,persisterTxn)>0){
                            String  serviceName=  extCorpId +":"+corpProfileInfoDTO.getPocHome()+":"+poCPttServerId;
                            knLogger.info(methodName, "serviceName",serviceName,"Alarm Id 17608");
                            KnAlarmGeneratorUtil.generateAlarm2(KnAlarmConstants.ALARM_CORPORATE_POC_HOME_RESET, KnAlarmConstants.SEVERITY_MAJOR,serviceName);
                        }
                        knLogger.info(methodName, "update new poc home for corp ");
                        corpProfilePersistDTO.setPocHome(poCPttServerId);
                    } else {
                        //dont update poc home
                        corpProfilePersistDTO.setPocHome(null);
                    }
                    corpProfilePersistDTO.setLastProfileUpdateTime(corpProfileUpdateTime);
                    corpProfilePersistDTO.setCorpId(corpId);
                    provXDMServerDAO.updateCorporateProfile(corpProfilePersistDTO, persisterTxn);
                    subsProfileDTO.setCorpId(corpId);
                    opsCorpFS2 = corpProfileInfoDTO.getOpsCorpFS2();
                    corpFS2 = corpProfileInfoDTO.getCorpFS2();
                    if((KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value() == subsClientType) &&
                            (KnProvConstants.WEB_DISPATCH_ENABLED == svcDispatchType || KnProvConstants.WEB_DISPATCH_ENABLED == corpDispatchType)){
                        subsProfileDTO.setDispatchType(KnProvConstants.WEB_DISPATCH_ENABLED);
                    }
                } else {
                    knLogger.debug(methodName, "creating the corporate Profile");
                    // get final CorpFeatureSet
                    corpFS2 = featureSetUtil.getDefFinalCorpFS();
                    knLogger.debug(methodName, "final corpFS :", corpFS2);

                    // get final OpsCorpFeatureSet
                    opsCorpFS2 = featureSetUtil.getDefFinalOpsCorpFS();
                    knLogger.debug(methodName, "final opsCorpFS :", opsCorpFS2);
                    KnCorpProfilePersistDTO corpProfilePersistDTO = populateCorpProfileInfoDTO(xdmPttServerId, subsProfileDTO);
                    corpProfilePersistDTO.setCorporateName(subsProfileDTO.getCorporateName());
                    corpProfilePersistDTO.setCorpFS2(corpFS2);
                    corpProfilePersistDTO.setOpsCorpFS2(opsCorpFS2);
                    corpProfilePersistDTO.setDynamicQosFlag(knPOCSvcConfigDTO.getDynamicQosFlag());
                    corpProfilePersistDTO.setFeatureRelVersion(currentFsVersion);
                    String xdmCorpFS2Set = KnGeneralUtil.getDefaultXDMCorpFS2Set(com.kodiak.common.resources.KnConstants.XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), com.kodiak.common.resources.KnConstants.TRUE);
                    xdmCorpFS2Set = featureSetUtil.calculateXDMCorpFS2(xdmCorpFS2Set, com.kodiak.common.resources.KnConstants.XDMCORPFS2_SET.EMERGENCY_CONF_TIMER_FEATURE.value(), com.kodiak.common.resources.KnConstants.TRUE);
                    xdmCorpFS2Set = featureSetUtil.calculateXDMCorpFS2(xdmCorpFS2Set, com.kodiak.common.resources.KnConstants.XDMCORPFS2_SET.MC_REACHABILITY_FLAG.value(), com.kodiak.common.resources.KnConstants.TRUE);
                    corpProfilePersistDTO.setXdmCorpFS2Set(xdmCorpFS2Set);
                    provXDMServerDAO.createCorporateProfile(corpProfilePersistDTO, persisterTxn);
                    knLogger.debug(methodName, "created the corporate profile for extCorpId - ", extCorpId);
                    corpProfileInfoDTO = provXDMServerDAO.retrieveCorporateProfile(corpProfilePersistDTO.getExtCorpId(), persisterTxn);
                    corpId = corpProfileInfoDTO.getCorpId();
                    subsProfileDTO.setCorpId(corpId);
                    if((KnProvConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value() == subsClientType) &&
                            (KnProvConstants.WEB_DISPATCH_ENABLED == svcDispatchType)){
                        subsProfileDTO.setDispatchType(KnProvConstants.WEB_DISPATCH_ENABLED);
                    }
                }
            }
            //check if the received subsFeatureSet1 is null or 0 then apply the default subsFeatureSet1.
            String subsFeatureSet2 = bulkSubsProvInfoDTO.getSubscriberFS2();
            // or zero we are checking as the request has zero as subsc fs
            if (subsFeatureSet2 == null ) {
            // get final SubsFeatureSet  based on clientype and subsType
            	BitSet finalFSBitSet = new BitSet(Long.SIZE);
            	Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            	String basePkgCode=paramNameValueMap.get(KnConstants.BASE_PKGCODE);
            	String basePkgDefFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(publicSubscriptionType, corporateSubscriptionType, subsClientType, basePkgCode, xdmPttServerId);
            	knLogger.debug(methodName, "base pkg is applied - ", basePkgDefFS);
            	BitSet basePkgDefFSBitSet =featureSetUtil.convertHexStringToBitSet(basePkgDefFS);
            	finalFSBitSet.or(basePkgDefFSBitSet);
            	if(!addPkgIds.isEmpty())
            	{
            		String pkgCodesFS = featureSetUtil.getDefSubsFeatureSetForPkgCodes(publicSubscriptionType, corporateSubscriptionType, subsClientType,addPkgIds, xdmPttServerId);
            		knLogger.debug(methodName, "addPkgIds is present with subscriberFS - ", pkgCodesFS);
            		BitSet pkgCodes_BiSet =featureSetUtil.convertHexStringToBitSet(pkgCodesFS);
            		finalFSBitSet.or(pkgCodes_BiSet);
            	}
            	subsFeatureSet2=featureSetUtil.convertBitSetToHexString(finalFSBitSet);
            }
            //same values of provFS1 and provFS1BitMask is used for calculation final subsFS after oring with bitmask
            String provFS2 = subsFeatureSet2;
            String provFS2BitMask = subsFeatureSet2;
            BitSet bitset = new BitSet();
            String subsFS2=null;
            //Here we need to calculate the subscriberFS based on value i got
            Map<Integer, Integer> provFSMap = bulkSubsProvInfoDTO.getProvFSMap();
            knLogger.debug(methodName," BulkSubsProvInfoDTO's   ProvFS :", provFSMap);
            if(provFSMap!= null  && !provFSMap.isEmpty()){
                if(isProvFSBitAllowed(provFSMap)){
                	bitset= featureSetUtil.convertHexStringToBitSet(subsFeatureSet2);
                    for(Map.Entry<Integer,Integer> provEntry: provFSMap.entrySet()){
                        if(provEntry.getValue()== com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value()){
                            bitset.set(provEntry.getKey());
                        }else{
                            bitset.clear(provEntry.getKey());
                        }
                    }
                    subsFeatureSet2=featureSetUtil.convertBitSetToHexString(bitset);
                    provFS2= featureSetUtil.convertBitSetToHexString(bitset);
                    knLogger.debug(methodName,"  provFS2 ",provFS2,"bitset :",bitset);
                    provFS2BitMask= provFS2;
                }
                else {
                    knLogger.error(methodName, "Operation not allowed for feature bit(s)");
                    throw new KnProvBOException(KnErrorCodes.Validator.INVALID_PROV_FS, "Requested feature bit(s) is/are not allowed to enable/disable via create/update.");
                }
            }
            knLogger.debug(methodName,"subsFeatureSet2 :",subsFeatureSet2);

            //Generate the SubsFeatureSet by performing the BitMask with License SubsFSBITMASK
            subsFS2 = featureSetUtil.generateSubsFeatureSet(subsFeatureSet2, provFS2, provFS2BitMask);

            knLogger.debug(methodName, "final subsFS : ", subsFS2);
            // get final ClientFeatureSet
            int defaultClientPVMajorVersion=1;
            String clientCapOverrideBitMask=featureSetUtil.getClientCapabilityBitMask(xdmPttServerId,defaultClientPVMajorVersion);

            String clientFS2 = featureSetUtil.getDefFinalClientFS(clientCapOverrideBitMask);
            String opsFS2=null;
            String corpAdminFS2 = featureSetUtil.getDefFinalCorpAdminFS();

            //Generate the ActiveFeatureSet
            String activeFS2=null;
            String xdmsFS2 = featureSetUtil.getDefFinalXdmsFS();
            String userProfileFS2=featureSetUtil.getDefFinalUserProfileFS();
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                opsFS2 = opsCorpFS2;
                activeFS2 = featureSetUtil.generateActiveFeatBitSet(poCPttServerId, presencePttServerId, xdmPttServerId,
                        clientFS2, subsFS2, corpFS2, opsCorpFS2, corpAdminFS2,clientCapOverrideBitMask,xdmsFS2,userProfileFS2);
            } else {
                // get final OpsFeatureSet
                opsFS2 = featureSetUtil.getDefFinalOpsFS();
                knLogger.debug(methodName, "default ops Feature set : ", opsFS2);

                activeFS2 = featureSetUtil.generateActiveFeatBitSet(poCPttServerId, presencePttServerId, xdmPttServerId,
                        clientFS2, subsFS2, opsFS2,clientCapOverrideBitMask,xdmsFS2,userProfileFS2);
            }
            knLogger.debug(methodName, "final activeFS : ", activeFS2);

            subsProfileDTO.setSubsFS2(subsFS2);;
            subsProfileDTO.setClientFS2(clientFS2);
            subsProfileDTO.setActiveFS2(activeFS2);
            subsProfileDTO.setOpsFS2(opsFS2);
            subsProfileDTO.setCorpAdminFS2(corpAdminFS2);
            subsProfileDTO.setXdmsFS2(xdmsFS2);
            subsProfileDTO.setUserProfileFS2(userProfileFS2);

            long profileCreationTime = Calendar.getInstance().getTimeInMillis();
            subsProvPersistDTO.setProfileCreationTime(profileCreationTime);
            //since creation time is same as last profile time while create
            subsProvPersistDTO.setLastProfileUpdateTime(profileCreationTime);

            subsProfileDTO.setPamAccId(bulkSubsProvInfoDTO.getPamAccId());
            //Modified for 8.1.2 Release
            if (KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value() == subsClientType) {
                Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
                subsProfileDTO.setNetworkName(paramNameValueMap.get(KnConstants.XDMS_LMR_SUB_DEFAULT_NAME));
            }
            
			Integer qppPkgId = null;
			Integer profileId = null;
			Integer dataPkgId = null;
			if (!addonPackageIds.isEmpty()) {
				for (String addonPkgCode : addonPackageIds.keySet()) {
					profileId = featureSetUtil.getAddProfIdForPkg(addonPkgCode, xdmPttServerId);
					if (profileId != null) {
						qppPkgId = genInfoUtil.getDataPkgId(KnConstants.QPP_DATA_PKG_TYPE, persisterTxn).get(profileId);
						if (qppPkgId != null) {
							dataPkgId = qppPkgId;
							break;
						} else if (dataPkgId == null) {
							dataPkgId = genInfoUtil.getDataPkgId(KnConstants.ADDON_DATA_PKG_TYPE, persisterTxn)
									.get(profileId);
						}

					}
				}

			}
			if (qppPkgId == null) {
				qppPkgId = KnConstants.DEFAULT_QPP_ID;
			}
			if (dataPkgId == null) {
				dataPkgId = KnConstants.DEFAULT_DATAPKG_ID;
			}
			if (profileId == null) {
				profileId = KnConstants.DEFAULT_PROFILE_ID;
			}

			subsProfileDTO.setQppPkgId(qppPkgId);
            subsProfileDTO.setFeatureRelVersion(currentFsVersion);
            subsProvPersistDTO.setSubsProfile(subsProfileDTO);
            knLogger.debug(methodName, "Subscriber Prov Persist DTO - ", subsProvPersistDTO);
            provXDMServerDAO.createSubscrProfile(subsProvPersistDTO, persisterTxn);
            
            // -->. Creating entry into the pocsubsaddlinfo 
            KnSubsAddlInfoPersistDTO subsProfilePersistDTO=new KnSubsAddlInfoPersistDTO();
            subsProfilePersistDTO.setMdns(subsProvPersistDTO.getMdns());;
            subsProfilePersistDTO.setTimeSlotType(KnConstants.TIME_SLOT_TYPE);
            subsProfilePersistDTO.setTierPkgCode(tierPackageId);
            subsProfilePersistDTO.setDataPkgId(dataPkgId);            
            provXDMServerDAO.createBulkSubscrPkgAddlInfo(subsProfilePersistDTO, persisterTxn);
            //add addon packages
            if(!addonPackageIds.isEmpty())
            {
            	provXDMServerDAO.createBulkSubAddOnPkgs(subsProvPersistDTO.getMdns(), new ArrayList<>(addonPackageIds.keySet()), persisterTxn);
            	
            }
            //create NNI profile for alias and group MDNs
            if (KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() == subsClientType ||
                    KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value() == subsClientType) {
                provInfoUtil.createNNISubscriberProfile(mdns,subsClientType,persisterTxn);
            }

            //creating TP and Mobile clients
            if (KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value() == subsClientType ||
                    KnProvConstants.SUBS_CLIENT_TYPE.MOBILE_CLIENT.value() == subsClientType ||
                    KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value() == subsClientType) {

            	KnPAMAccPoolUsageDTO pamAccPoolUsageDTO = new KnPAMAccPoolUsageDTO();
            	pamAccPoolUsageDTO.setBillingMdn(bulkSubsProvInfoDTO.getExtPamAccId());
            	pamAccPoolUsageDTO.setPamAccId(bulkSubsProvInfoDTO.getPamAccId());
            	pamAccPoolUsageDTO.setUsage(KnConstants.PAMACCOUNT_POOL_USAGE.NOT_IN_USE.value());

            	KnPAMAccPoolPersistDTO pamAccPoolPersistDTO = new KnPAMAccPoolPersistDTO();
            	pamAccPoolPersistDTO.setMdns(bulkSubsProvInfoDTO.getMdns());
            	pamAccPoolPersistDTO.setPamAccPoolUsage(pamAccPoolUsageDTO);

            	provXDMServerDAO.createPAMAccPoolUsage(pamAccPoolPersistDTO, persisterTxn);
            }

            // Interne APN changes ....
            String apnName = genInfoUtil.getDefaultAPNName(persisterTxn);
            // verify apn exists or not ..
            Integer apnId = genInfoUtil.getAPNId(apnName, persisterTxn);
            knLogger.info(methodName, " APNID ", apnId, "APN NAME  :", apnName);
            if (apnId == null) {
                knLogger.error(methodName, " Default APN not exists , APN_INFO_NOT_FOUND ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.APN_INFO_NOT_FOUND, " APN_INFO_NOT_FOUND ");
            }
            provXDMServerDAO.addSubApn(mdns, apnId, persisterTxn);

            // --> Creating an entry into the Subscriber Roaming Profile
            // subs roaming profile is mandatory if feature roaming is enabled
            List<Integer> roamingClusterIdList = new ArrayList<Integer>();

            roamingClusterIdList.add(KnProvConstants.DEFAULT_ROAMING_CLUSTER_ID);

            knLogger.debug(methodName, "adding  entries into Subscriber Roaming Profile");
            provXDMServerDAO.createSubscrRoamingProfile(mdns, roamingClusterIdList, persisterTxn);


            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                // -->. creating entry into the XDM_CorpResourceListIndex Doc
                knLogger.debug(methodName, "Adding entries into the Corp resource List Index Doc");
                xdmServerDAO.addMdnToCorpResourceListIndexDoc(mdns, persisterTxn);
            }

            // -->. creating entry into the XDM_ContactListDocMap
            List<Integer> contactListIds;
            knLogger.debug(methodName, "adding entries into XDM Contact List Doc Map ");
            List<KnContactListPersistDTO> contactListDocPersistDTOs = new ArrayList<KnContactListPersistDTO>(mdns.size());
            KnContactListPersistDTO contactListPersistDTO;
            for (int i = 0; i < mdns.size(); i++) {
                contactListPersistDTO = new KnContactListPersistDTO();
                contactListPersistDTO.setMdn(mdns.get(i));
                contactListDocPersistDTOs.add(contactListPersistDTO);
            }
            contactListIds = xdmServerDAO.addMdnToXDMContactListDocMap(contactListDocPersistDTOs, persisterTxn);

            // -->. creating entry into the XDM_ContactList
            knLogger.debug(methodName, "adding an entry into XDM Contact List ");
            xdmServerDAO.addMdnToXDMContactList(mdns, contactListIds, persisterTxn);

            // -->. Creating entry into the XDM_Directory
            knLogger.debug(methodName, "adding entries into XDM Directory");
            xdmServerDAO.addMdnToXDMDirectory(mdns, persisterTxn);

            // creating Additional subscriber in custom
            Map<String, Object> customReqMap;
            if (bulkSubsProvInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customReqMap = bulkSubsProvInfoDTO.getCustomParamMap();
                knLogger.debug(methodName, "customReqMap ::", customReqMap);
                if (customReqMap == null) {
                    customReqMap = new HashMap<String, Object>();
                    customReqMap.put(KnProvConstants.ACCOUNT_TYPE_INDICATOR, KnConstants.ACCOUNT_TYPE.INDIVIDUAL.value());
                    customReqMap.put(KnProvConstants.RATE_PLAN, String.valueOf(KnConstants.SUBSCRIBERS_CLIENT_TYPE.WIFIONLY.value()));
                } else if (customReqMap.get(KnProvConstants.EXT_BAN_ID) != null && (customReqMap.get(KnProvConstants.EXT_FAN_ID)) != null) {
                    customReqMap.put(KnProvConstants.ACCOUNT_TYPE_INDICATOR, KnConstants.ACCOUNT_TYPE.BUSINESS.value());
                }
                customReqMap.put(KnProvConstants.CORP_ID, String.valueOf(corpId));

                KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();

                KnXDMPAMSubsProfInfoDTO subsProfInfoDTO = new KnXDMPAMSubsProfInfoDTO();

                customReqMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_PROV_ADD_PAMSUBS_OP);
                customReqMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);


                subsProfInfoDTO.setMdns(mdns);
                subsProfInfoDTO.setCustomParamMap(customReqMap);
                subsProfInfoDTO.setPamAccId(bulkSubsProvInfoDTO.getPamAccId());
                pamAccInfoDTO.setProfileDetails(subsProfInfoDTO);
                subsProfInfoDTO.setFirstNetIndicator(bulkSubsProvInfoDTO.getFirstNetIndicator());
                knLogger.debug(methodName, "CUSTOM_PROV_ADD_PAMACCOUNT_OP ");

                Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, pamAccInfoDTO);
                knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);
                if (customResp instanceof KnXDMRespDTO) {
                    KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;

                    if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                        knLogger.debug(methodName, "failed to create PAM  subs profile ");
                        throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());

                    } else {
                        knLogger.debug(methodName, "successfully PAM  subs profile created ");
                    }
                }
            }
            // end custom logic

            respDTO = new KnOPCreateSubsInfoDTO();
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage(KnProvConstants.CREATE_SUBSCRIBER_SUCCESS);
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                respDTO.setCorpId(corpId);
            }
            knLogger.info(methodName, "EXIT: Create Subscribers operation ");

            return respDTO;
        } catch (KnFeatureSetException ex) {
            knLogger.debug(methodName, "Feature Set Exception occured : ");
            knLogger.error(methodName, "Feature Set Exception occured :", ex);
            throw new KnProvBOException(ex.getErrorCode(), ex.getMessage(), ex);
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_ALREADY_EXISTS, "Subscriber already exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_POC.equals(e.getErrorCode())) {
                knLogger.info("raise alarm", "KnProvConstants.XDMS_POC_CAPACITY_REACHED_ALARMCODE :", KnProvConstants.XDMS_POC_CAPACITY_REACHED_ALARMCODE
                        , "KnAlarmConstants.CRITICAL :", KnAlarmConstants.SEVERITY_CRITICAL
                        , "KnProvConstants.MANAGEDOBJECT_CLASSTYPE :", KnProvConstants.XDMMANAGEDOBJECT_CLASSTYPE);
                KnAlarmGeneratorUtil.generateAlarm(KnProvConstants.XDMS_POC_CAPACITY_REACHED_ALARMCODE, KnAlarmConstants.SEVERITY_CRITICAL, KnProvConstants.XDMMANAGEDOBJECT_CLASSTYPE, KnProvConstants.PAM);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Create Subscriber");
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while create Subscriber", e);
        }
    }

    /**
     * method to update the Subscriber Profile.
     * --> populate the Subs profile persist DTO
     * --> Validate the input data
     * --> send the data to the DAO layer for updation of profile
     * --> on success populate the document DTO (for notification)
     * --> populate the response DTO.
     *
     * @param bulkSubsProvInfoDTO KnIPBulkSubsProvInfoDTO
     * @param persisterTxn        KnPersisterTxn
     * @return KnOPProvDTO response DTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException              BO Entity Exception
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException Validation level exception
     */
    public KnOPUpdateSubsInfoDTO updateSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException {
        String methodName = "updateSubscribers(KnIPBulkSubsProvInfoDTO, KnPersisterTxn)";
        return new KnOPUpdateSubsInfoDTO();
    }

    /**
     * method to delete the Subscriber Profile
     *
     * @param bulkSubsProvInfoDTO KnIPSubscriberInfoDTO
     * @param persisterTxn        KnPersisterTxn
     * @return KnOPProvDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException BO Entity Exception
     */
    public KnOPBulkDeleteSubsRespDTO deleteSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, boolean isLastMDN, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "deleteSubscribers(KnIPBulkSubsProvInfoDTO, KnPersisterTxn)";
        KnOPBulkDeleteSubsRespDTO responseDTO = new KnOPBulkDeleteSubsRespDTO();
        Map<String, KnOPSubsProfileInfoDTO> notificationDataMap;
        Collection<KnSubsProfileDTO> subsSpecificDetails = null;
        Map<String, String> activeFSMap2 = new HashMap<>();
        knLogger.entry(methodName, bulkSubsProvInfoDTO, persisterTxn);
        try {

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            knLogger.debug(methodName, "bulkSubsProvInfoDTO.getPamAccId():", bulkSubsProvInfoDTO.getPamAccId());
            knLogger.debug(methodName, "bulkSubsProvInfoDTO.getMdns()():", KnGDPRTemplate.mdnList(bulkSubsProvInfoDTO.getMdns()));

            List<String> mdns;

            if (bulkSubsProvInfoDTO.getMdns() == null) {
                mdns = provXDMServerDAO.retrievePAMAccountMDNs(bulkSubsProvInfoDTO.getPamAccId(), persisterTxn);

            } else {
                mdns = bulkSubsProvInfoDTO.getMdns();

            }
            knLogger.debug(methodName, "mdns :", KnGDPRTemplate.mdnList(mdns) ,"responseDTO :", responseDTO);

            subsSpecificDetails = provXDMServerDAO.fetchSubsSpecificDetailsForBulkMdns(mdns, false, persisterTxn);
            if(subsSpecificDetails != null && !subsSpecificDetails.isEmpty()){
                subsSpecificDetails.forEach(subsDTO -> {
                	activeFSMap2.put(subsDTO.getMdn(), subsDTO.getActiveFS2());
                });
            }
            knLogger.debug(methodName, "activeFSMap2 :", KnGDPRTemplate.mapKeyMdn(activeFSMap2));
            responseDTO.setActiveFSMap2(activeFSMap2);
            if (mdns != null && mdns.size() > 0) {
                knLogger.debug(methodName, "mdns size:", mdns.size());
                notificationDataMap = provXDMServerDAO.retrieveNotificationDetails4mdns(mdns, persisterTxn);
                KnOPSubsProfileInfoDTO existingProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdns.get(0), persisterTxn);


                IXDMServerDAO commonXdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                //getting the prev etag of directory

                commonXdmServerDAO.deleteXDMDirectoryForMdn(mdns, persisterTxn);
                knLogger.debug(methodName, "deleted the directories ");


                //deleting the XDM Contact List
                commonXdmServerDAO.deleteContactListForMdn(mdns, persisterTxn);
                knLogger.debug(methodName, "deleted the Contact Lists ");

                //deleting the XDM Contact List Doc Map
                commonXdmServerDAO.deleteContactListDocMapForMdn(mdns, persisterTxn);
                knLogger.debug(methodName, "deleted the contact lists doc map ");


                //deleting Corp resource List Index Doc
                int corpSubscriptionType = existingProfileInfoDTO.getCorporateSubscriptionType();
                if (KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() == corpSubscriptionType) {
                    IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                    commonXDMServerDAO.deleteCorpResourceListIndexDoc(mdns, persisterTxn);
                    knLogger.debug(methodName, "deleted the Corp resource Lists Index Doc");
                }


                //delete the subscriber roaming profile
                provXDMServerDAO.deleteSubscrRoamingProfile(mdns, persisterTxn);
                knLogger.debug(methodName, "Delete the Subscribers Roaming Profile");
                knLogger.debug(methodName, "Delete the Subscriber Roaming Profile");
                provXDMServerDAO.deleteSubApn(mdns, persisterTxn);

                // Delete PAM profile(PAMADDSUBSCR_PROFILEINFO, PAMSUBSCRPROFILEINFO) if it is last MDN,
                if (isLastMDN) {
                	knLogger.debug(methodName, "Deleting PAM subscriber profile before deleting last MDN");
                	deletePAMProfile(bulkSubsProvInfoDTO, persisterTxn);
                }

                int subsClientType = existingProfileInfoDTO.getSubsClientType();
                if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                    knLogger.info(methodName, "NNI PAM subscriber being Rolledback is - ",  KnGDPRTemplate.mdnList(mdns));
                    provXDMServerDAO.deleteNNISubscrProfile(mdns, persisterTxn);
                }

                if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value() ||
                		subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.MOBILE_CLIENT.value() ||
                		subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()) {
                    knLogger.info(methodName, "TP or Mobile MDN subscribers being Rolledback is - ",   KnGDPRTemplate.mdnList(mdns));
                    provXDMServerDAO.deletePAMAccPoolUsage(mdns, persisterTxn);
                }

                //delete the subscriber profile
                provXDMServerDAO.deleteSubscriberProfile(mdns, persisterTxn);
                knLogger.debug(methodName, "Delete Subscribers Profile");


                //deleting Corp profile Info

                if (KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() == corpSubscriptionType) {
                    int corpId = existingProfileInfoDTO.getCorpId();
                    //deleting the corp Profile.
                    // --> verify if the current MDN is the last member of the Corporation
                    // --> if true then delete corp profile else continue.
                    int corpSubsCount = provXDMServerDAO.retrieveCorpSubscriberWithProfileCnt(corpId, persisterTxn);
                    int corpProfileCleanUp = provXDMServerDAO.retrieveCorpProfileCleanUp(corpId, persisterTxn);
                    int deviceCount = genInfoUtil.getDeviceCountForCorpId(corpId, persisterTxn);
                    knLogger.debug(methodName, "corp Subscriber count - ", corpSubsCount, " corpProfileCleanUp :", corpProfileCleanUp, "deviceCount", deviceCount);
                    if (corpSubsCount == 0 && corpProfileCleanUp != 1 && deviceCount == 0) {
                        knLogger.info(methodName, "Deleting Corporate profile as corpProfileCleanUp = ", corpProfileCleanUp,
                                " and corpSubsCount = ", corpSubsCount);
                        //delete corp profile
                        provXDMServerDAO.deleteCorporateProfile(corpId, persisterTxn);

                        knLogger.info(methodName, "Deleted the corporate profile");
                    } else {
                        knLogger.debug(methodName, "Updating the corp profile etag since mdn for corp is deleted"
                        );
                        long profileUpdateTime = Calendar.getInstance().getTimeInMillis();
                        provXDMServerDAO.updateCorpProfileLastUpdateTime(corpId, profileUpdateTime, persisterTxn);
                        knLogger.debug(methodName, "updated the corp profile etag for corp id - ", corpId);
                    }
                }

                // delete Additional subscriber in custom
                int corpId = existingProfileInfoDTO.getCorpId();
                if (bulkSubsProvInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                    KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();

                    KnXDMPAMSubsProfInfoDTO subsProfInfoDTO = new KnXDMPAMSubsProfInfoDTO();
                    Map<String, Object> customReqMap = new HashMap<String, Object>();
                    customReqMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_PROV_CANCEL_PAMSUBS_OP);
                    customReqMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                    customReqMap.put(KnProvConstants.CORP_ID, String.valueOf(corpId));
                    subsProfInfoDTO.setMdns(mdns);
                    subsProfInfoDTO.setCustomParamMap(customReqMap);
                    pamAccInfoDTO.setProfileDetails(subsProfInfoDTO);

                    knLogger.debug(methodName, "CUSTOM_PROV_CANCEL_PAMACCOUNT_OP ");

                    Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, pamAccInfoDTO);
                    knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);
                    if (customResp instanceof KnXDMRespDTO) {
                        KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;

                        if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                            knLogger.debug(methodName, "failed to  deleted custom subscribers ");
                            throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());
                        } else {
                            knLogger.debug(methodName, "successfully deleted custom subscribers ");

                        }
                    }


                }

                //Populating notification data
                KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
                List<KnOPDeleteSubsRespDTO> deleteSubsRespDTOs = new ArrayList<KnOPDeleteSubsRespDTO>(50);
                for (String mdn : mdns) {
                    KnOPSubsProfileInfoDTO subsProfileDTO = notificationDataMap.get(mdn);
                    KnOPDeleteSubsRespDTO deleteSubsRespDTO = new KnOPDeleteSubsRespDTO();

                    KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                    dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn,persisterTxn));
                    dirChgDTO.setPocHome(subsProfileDTO.getPoCHome());
                    dirChgDTO.setPresenceHome(subsProfileDTO.getPresenceHome());
                    String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                    dirChgDTO.setDirUri(dirDocUri);
                    dirChgDTO.setDirPrevEtag(String.valueOf(subsProfileDTO.getLastProfileUpdateTime()));
                    dirChgDTO.setProtoVersion(subsProfileDTO.getClientPVmajorVer() + "." + subsProfileDTO.getClientPVminorVer());
                    dirChgDTO.setClientType(subsProfileDTO.getSubsClientType());

                    deleteSubsRespDTO.setMdn(mdn);
                    deleteSubsRespDTO.setPocServerHome(subsProfileDTO.getPoCHome());
                    deleteSubsRespDTO.setPresenceServerHome(subsProfileDTO.getPresenceHome());
                    deleteSubsRespDTO.setDirChgDTO(dirChgDTO);
                    deleteSubsRespDTOs.add(deleteSubsRespDTO);
                }

                responseDTO.setDeleteSubsRespDTOs(deleteSubsRespDTOs);
                responseDTO.setResponseMessage(KnProvConstants.DELETE_SUBSCRIBER_SUCCESS);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());


                knLogger.exit(methodName, responseDTO);

                return responseDTO;
            } else {
                knLogger.debug(methodName, "Subscriber does not exists ");

                throw new KnDAOException(com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND, "Subscriber does not exists");
            }

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while delete Subscriber", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while delete Subscriber", e);
        }
    }

    private void deletePAMProfile(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnProcessInvokerException {
    	final String methodName = "deletePAMProfile(KnXDMPAMAccInfoDTO)";
    	knLogger.entry(methodName, bulkSubsProvInfoDTO);

    	try {
    	IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
    	// retrieving PAM profile to get ProfileID.
    	KnPAMSubsProfInfoDTO pamSubsProfInfoDTO = provXDMServerDAO.getPAMSubsProfInfo(bulkSubsProvInfoDTO.getPamAccId(), persisterTxn);

    	KnPAMAccPersistDTO pamAccPersistDTO = new KnPAMAccPersistDTO();
    	pamAccPersistDTO.setProfileDetails(new KnPAMSubsProfInfoDTO());
    	pamAccPersistDTO.getProfileDetails().setPamAccId(bulkSubsProvInfoDTO.getPamAccId());
    	pamAccPersistDTO.getProfileDetails().setProfileId(pamSubsProfInfoDTO.getProfileId());

    	knLogger.debug(methodName, "Deleting PAM profile info", pamAccPersistDTO);
    	provXDMServerDAO.deletePAMSubsProfile(pamAccPersistDTO, persisterTxn);
    	knLogger.debug(methodName, "PAM  profile info is deleted..");
    	knLogger.debug(methodName, "Deleting PAM addon package list", bulkSubsProvInfoDTO.getPamAccId());
    	provXDMServerDAO.deletePAMSubAddlOnPkgs(bulkSubsProvInfoDTO.getPamAccId(), persisterTxn);
    	knLogger.debug(methodName, "PAM  addon package list is deleted..");

    	if (bulkSubsProvInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
			 knLogger.info(methodName, "CUSTOM is ON..  deleting PAM Add subs profile info");

			 KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
			 pamAccInfoDTO.setProfileDetails(new KnXDMPAMSubsProfInfoDTO());
			 pamAccInfoDTO.getProfileDetails().setPamAccId(bulkSubsProvInfoDTO.getPamAccId());
			 pamAccInfoDTO.getProfileDetails().setProfileId(bulkSubsProvInfoDTO.getProfileId());

			 Map<String, Object> customRequestMap = new HashMap<String, Object>();
			 customRequestMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_PROV_CANCEL_PAMACCOUNT_OP);
			 customRequestMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
			 pamAccInfoDTO.getProfileDetails().setCustomParamMap(customRequestMap);

			 knLogger.debug(methodName, "calling CUSTOM - , ", pamAccInfoDTO);
			 Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, pamAccInfoDTO);
			 knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);
			 if (customResp instanceof KnXDMRespDTO) {
				 KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;
				 if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
					 knLogger.debug(methodName, "failed to delete PAM  Add subs profile ");
					 throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());
				 }
				 knLogger.debug(methodName, "successfully PAM  subs profile deleted ");
			 }
		} else {
			 knLogger.info(methodName, "Custom is Not installed !!! skipping addition subs profile deletion  ");
		}

		} catch (KnDAOException e) {
			knLogger.error(methodName, "DAO Exception occurred : ", e);
			throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
		}

    	knLogger.exit(methodName, "Successfully deleted PAM profile");
    }

    /**
     * method for retrieve Subscriber Count
     *
     * @param persisterTxn KnPersisterTxn
     * @return List<String> (Subscriber count)
     * @throws KnDAOException exception DB Layer
     */
    public List<String> retrievePAMAccountMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrievePAMAccountMDNs(int, KnPersisterTxn)";
        boolean ownedTxn = false;
        List<String> mdns;
        knLogger.info(methodName, "ENTRY: Retrieving PAM Account MDNs");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            mdns = provXDMServerDAO.retrievePAMAccountMDNs(pamAccId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

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
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscribers does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get PAM account Subscribers");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get PAM Account Subscribers", e);
        }
        knLogger.info(methodName, "EXIT: get PAM Account Subscribers operation ");

        return mdns;
    }

    public List<String> getPAMAccountMdnsByInsertionTime(int pamAccId, long insertionTime, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrievePAMAccountMDNs(int, KnPersisterTxn)";
        List<String> mdns;
        boolean ownedTxn = false;
        knLogger.info(methodName, "ENTRY: Retrieving PAM Account MDNs");
        try {
        	 if (persisterTxn == null) {
                 persisterTxn = KnPersisterTxn.getPersisterTxn();
                 knLogger.debug(methodName, "Opening the Transaction");
                 persisterTxn.open();
                 ownedTxn = true;
             }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            mdns = provXDMServerDAO.getPAMAccountMdnsByInsertionTime(pamAccId, insertionTime, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);

        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get PAM account Subscribers", e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get PAM Account Subscribers", e);
        }

        knLogger.info(methodName, "EXIT: get PAM Account Subscribers operation ");
        return mdns;
    }


    /**
     * method to retrieve PAM Account MDN Details(list of Pseudomdn & its service auth status)
     *
     * @param persisterTxn KnPersisterTxn
     * @return List<KnOPSubsProfileInfoDTO> (contains mdn and Service Auth status)
     * @throws KnDAOException exception DB Layer
     */
    public List<KnOPSubsProfileInfoDTO> retrievePAMAccountMDNsDetails(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "retrievePAMAccountMDNsDetails(int, KnPersisterTxn)";
        boolean ownedTxn = false;
        List<KnOPSubsProfileInfoDTO> pamProfileDetails;
        knLogger.info(methodName, "ENTRY: Retrieving PAM Account MDNs");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            pamProfileDetails = provXDMServerDAO.retrievePAMAccountMDNsDetails(pamAccId, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }

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
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscribers does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get PAM account Subscribers");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get PAM Account Subscribers", e);
        }
        knLogger.info(methodName, "EXIT: get PAM Account Subscribers operation ");

        return pamProfileDetails;
    }

    public List<String> retrievePAMAccountMDNs(int pamAccId, int fetchSize, String listMdn, KnPersisterTxn persisterTxn) throws KnProvException {
        String methodName = "retrievePAMAccountMDNs(int, int, int, KnPersisterTxn)";
        boolean ownedTxn = false;
        List<String> mdns;
        knLogger.info(methodName, "ENTRY: Retrieving PAM Account MDNs");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            mdns = provXDMServerDAO.retrievePAMAccountMDNs(pamAccId, fetchSize, listMdn, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.info(methodName, "EXIT: get PAM Account Subscribers operation ");

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
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscribers does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get PAM account Subscribers");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get PAM Account Subscribers", e);
        }

        return mdns;
    }

    public List<String> retrievePAMAccountMDNs(int pamAccId, String startMdn, String endMdn, int fetchSize, KnPersisterTxn persisterTxn) throws KnProvException {
        String methodName = "retrievePAMAccountMDNs(int, String, String, int, KnPersisterTxn)";
        boolean ownedTxn = false;
        List<String> mdns;
        knLogger.info(methodName, "ENTRY: Retrieving PAM Account MDNs");
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "Opening the Transaction");
                persisterTxn.open();
                ownedTxn = true;
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            mdns = provXDMServerDAO.retrievePAMAccountMDNs(pamAccId, startMdn, endMdn, fetchSize, persisterTxn);

            if (ownedTxn) {
                knLogger.debug(methodName, "Saving the transaction");
                persisterTxn.save();
            }
            knLogger.info(methodName, "EXIT: get PAM Account Subscribers operation ");

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
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscribers does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get PAM account Subscribers");
            knLogger.error(methodName, e);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get PAM Account Subscribers", e);
        }

        return mdns;
    }

    /**
     * method to update the Service auth status of the Subscriber
     * Re-activate or De-activate the subscriber.
     *
     * @param bulkSubsProvInfoDTO KnIPSubsProvInfoDTO
     * @param persisterTxn        KnPersisterTxn
     * @return KnOPProvDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException BO Entity Exception
     * @throws com.kodiak.xdms.server.common.framework.KnFWException      Validation Exception
     */

    public KnOPBulkChgAuthStatusRespDTO changeServiceAuthStatuses(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException, KnFWException {
        String methodName = "changeServiceAuthStatuses(KnIPBulkSubsProvInfoDTO, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY: change Service Auth status request with DTO - ", bulkSubsProvInfoDTO,
                " with Txn - ", persisterTxn);
        KnOPBulkChgAuthStatusRespDTO responseDTO = new KnOPBulkChgAuthStatusRespDTO();
        Map<String, String> activeFSMap = new HashMap<>();
        Collection<KnSubsProfileDTO> subsSpecificDetails = null;
        Map<String, Integer> clientTypeMap = new HashMap<>();
        try {
            List<String> mdns = bulkSubsProvInfoDTO.getMdns();
            KnSubsProfileDTO subsProfileDTO = new KnSubsProfileDTO();

            int inputSvcAuthStatus = bulkSubsProvInfoDTO.getServiceAuthStatus();
            knLogger.debug(methodName, "Mdns :",  KnGDPRTemplate.mdnList(mdns));

            if (mdns != null && mdns.size() > 0) {
                //retrieve the Service Auth Status of the MDN or Subscriber
                KnOPSubsProfileInfoDTO subsProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdns.get(0), persisterTxn);

                String newClientPassword = subsProfileInfoDTO.getClientPassword();
                String newUserAgent = subsProfileInfoDTO.getUserAgent();
                if (inputSvcAuthStatus == KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value()) {
                    newUserAgent = null;
                    newClientPassword = null;
                }

                //populate the persist DTO for the DB update.
                Integer svcStatusAuthUser = subsProfileInfoDTO.getServiceStatusAuthUser();
                KnBulkSubsProfilePersistDTO bulkSubsProfilePersistDTO = new KnBulkSubsProfilePersistDTO();
                bulkSubsProfilePersistDTO.setMdns(mdns);
                subsProfileDTO.setServiceAuthStatus(KnGenInfoUtil.calculateServiceAuthStatus(inputSvcAuthStatus,svcStatusAuthUser));
                subsProfileDTO.setServiceStatusOp(inputSvcAuthStatus);
                subsProfileDTO.setClientPassword(newClientPassword);
                subsProfileDTO.setUserAgent(newUserAgent);
                long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                bulkSubsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);

                bulkSubsProfilePersistDTO.setSubsProfile(subsProfileDTO);
                IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

                subsSpecificDetails = xdmServerDAO.fetchSubsSpecificDetailsForBulkMdns(mdns, false, persisterTxn);
                if(subsSpecificDetails != null && !subsSpecificDetails.isEmpty()){
                    subsSpecificDetails.forEach(subsDTO -> {
                    	activeFSMap.put(subsDTO.getMdn(), subsDTO.getActiveFS2());
                        clientTypeMap.put(subsDTO.getMdn(), subsDTO.getSubsClientType());
                    });

                }
                knLogger.debug(methodName, "activeFSMap and clientTypeMap :",  KnGDPRTemplate.mapKeyMdn(activeFSMap), KnGDPRTemplate.mapKeyMdn(clientTypeMap));

                if (subsProfileInfoDTO.getCorpId() > 0) {
                    long corpProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                    xdmServerDAO.updateCorpProfileLastUpdateTime(subsProfileInfoDTO.getCorpId(), corpProfileUpdateTime, persisterTxn);
                }

                //changeServiceAuthStatus Logic
                List<String> allMdnList=new ArrayList<>();
                allMdnList= getMdnForUPM(mdns, persisterTxn);
                //Removing Base Mdns from the list
                allMdnList.removeAll(mdns);
                knLogger.debug(methodName, "allMdnList--",KnGDPRTemplate.mdnList(allMdnList));
                for(String mdn:allMdnList)
                {
                    mdns.add(mdn);
                }
                bulkSubsProfilePersistDTO.setMdns(mdns);
                //
                xdmServerDAO.updateServiceAuthStatus(bulkSubsProfilePersistDTO, persisterTxn);
                knLogger.debug(methodName, "updated service auth status successfully");

                IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                //retrieve the previous etag of dir doc
                List<Integer> previousEtags = commonXDMServerDAO.getCurrentEtagsForDirDoc(mdns, persisterTxn);

                //update the dir doc etag
                commonXDMServerDAO.updateEtagForDirDoc(mdns, persisterTxn);
                knLogger.debug(methodName, "Successfully updated the xdm directory");


                //populate the response DTO
                //checking if the request is to deactivate or Provisioned subscriber
                //if the request is deactivate subscriber then populate the deactivation notification dto

                if (inputSvcAuthStatus == KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value()
                        || inputSvcAuthStatus == KnConstants.SERVICE_AUTH_STATUS.PROVISIONED.value()) {
                    responseDTO.setMdn(subsProfileInfoDTO.getMdn());
                    responseDTO.setPocServerHome(subsProfileInfoDTO.getPoCHome());
                    responseDTO.setPresenceServerHome(subsProfileInfoDTO.getPresenceHome());
                }

                responseDTO.setResponseMessage(KnProvConstants.UPDATE_AUTH_STATUS_SUCCESS);
                responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
                responseDTO.setEtag(lastProfileUpdateTime);
                responseDTO.setActiveFSMap2(activeFSMap);
                responseDTO.setCorpId(subsProfileInfoDTO.getCorpId());
                responseDTO.setClientTypeMap(clientTypeMap);
                knLogger.debug(methodName, "responseDTO after setting corpId :",responseDTO);

                //populating the Subscriber config doc DTO

                List<KnOPDirChgDTO> dirChgDTOs = new ArrayList<KnOPDirChgDTO>();
                KnOPDocChgDTO docChgDTO;
                KnOPDirChgDTO dirChgDTO;
                int i = 0;
                genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);

                int clientType = bulkSubsProvInfoDTO.getClient_Type();
                if (clientType != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() && clientType != KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                	for (String mdn : mdns) {
                		docChgDTO = new KnOPDocChgDTO();
                		docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                		String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
                		docChgDTO.setDocUri(subsConfigDocUri);
                		docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));

                		Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                		chgDocList.add(docChgDTO);

                		//populating the XDM Directory DTO
                		dirChgDTO = new KnOPDirChgDTO();
                		dirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                		dirChgDTO.setPocHome(subsProfileInfoDTO.getPoCHome());
                		dirChgDTO.setPresenceHome(subsProfileInfoDTO.getPresenceHome());
                		dirChgDTO.setDocChgDTO(chgDocList);
                		String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                		dirChgDTO.setDirUri(dirDocUri);
                		dirChgDTO.setDirPrevEtag(String.valueOf(previousEtags.get(i)));
                		int newEtag = previousEtags.get(i) + 1;
                		dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                		dirChgDTO.setProtoVersion(subsProfileInfoDTO.getClientPVmajorVer() + "." + subsProfileInfoDTO.getClientPVminorVer());
                        dirChgDTO.setClientType(subsProfileInfoDTO.getSubsClientType());
                        //populating the Dir chg DTO to response
                		dirChgDTOs.add(dirChgDTO);
                		i++;
                	}
                }
                responseDTO.setDirChgDTOs(dirChgDTOs);

            } else {
                knLogger.debug(methodName, "mdn list is 0 hence skipping operation");

            }
            knLogger.debug(methodName, "Response DTO - ", responseDTO);
            knLogger.info(methodName, "EXIT: change service auth status of Subscribers operation ");

            return responseDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscriber already exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while change service auth status of Subscriber");
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while change service auth status of Subscriber", e);
        }

    }

    public List<String> getMdnForUPM(List<String> baseMdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getMdnForUPM(List<String>, KnPersisterTxn)";
        boolean ownedTxn = false;
        //KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
        knLogger.info(methodName, "ENTRY: getMdnForUPM for baseMdn:", KnGDPRTemplate.mdnList(baseMdn));
        List<String> mdnList=new ArrayList<>();
        try {
            xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            // retrieving the Subscriber Info
            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();

            mdnList = provXdmServerDAO.getMdnForUPMList(baseMdn, persisterTxn);

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
    public KnOPCreateSubsInfoDTO validateCreateSubscriber(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, int subsCount,
                                                          KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        String methodName = "validateCreateSubscriber(KnIPBulkSubsProvInfoDTO, KnPersisterTxn)";
        KnOPCreateSubsInfoDTO respDTO;
        knLogger.info(methodName, "ENTRY: validate create Subscribers with DTO - ", bulkSubsProvInfoDTO, " Txn - ", persisterTxn);

        try {
        	 respDTO = new KnOPCreateSubsInfoDTO();
            KnSubsProfileDTO subsProfileDTO = new KnSubsProfileDTO();

            int publicSubscriptionType = bulkSubsProvInfoDTO.getPubSubsType();
            subsProfileDTO.setPublicSubscriptionType(publicSubscriptionType);
            int corporateSubscriptionType = bulkSubsProvInfoDTO.getCorpSubsType();
            subsProfileDTO.setCorporateSubscriptionType(corporateSubscriptionType);
            //Deprecating Existing pairing indicator because of new pairing logic in 8.0.2
            //default pariring ind
           // boolean pairingIndicator = false;

			int licenseType = bulkSubsProvInfoDTO.getLicenseType();
			int clientType = bulkSubsProvInfoDTO.getClient_Type();
			String firstNetIndicator=bulkSubsProvInfoDTO.getFirstNetIndicator();
			
			if (firstNetIndicator != null) {
				String firstNetIndicatorList = KnGenInfoUtil.getInstance().retrieveRTXConfigValues(persisterTxn).get(KnConstants.FIRSTFLAG_CONFIG);
				if (firstNetIndicatorList != null) {
					List<String> firstNetIndicators = Arrays.asList(firstNetIndicatorList.split(","));
					if (!firstNetIndicators.contains(firstNetIndicator)) {
						knLogger.debug(methodName, "Validation failed for firstNetIndicator");
						throw new KnProvBOException(KnErrorCodes.Validator.INVALID_FIRST_NET_INDICATOR,
								"Validation failed for firstNetIndicator ");
					}
				} else {
					knLogger.debug(methodName, "Validation failed for firstNetIndicator");
					throw new KnProvBOException(KnErrorCodes.Validator.INVALID_FIRST_NET_INDICATOR,
							"Validation failed for firstNetIndicator ");
				}
			}
			
			boolean licenseTypeValid = false;

			if (licenseType == KnConstants.USER_LICENSE_TYPE
					&& (clientType == KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value()
							|| clientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()
							|| clientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_WIFIONLY.value()
							|| clientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()
							|| clientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()
							|| clientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value())) {

				licenseTypeValid = true;

			} else if (licenseType != KnConstants.USER_LICENSE_TYPE) {
				licenseTypeValid = true;
			} else {

				licenseTypeValid = false;
			}

			if (!licenseTypeValid) {
				knLogger.debug(methodName, "Validation failed for client type and licenseType");
				throw new KnProvBOException(KnErrorCodes.Validator.INVALID_LICENSE_TYPE,
						"Validation failed for client type and licenseType");
			}

            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                String extCorpId = bulkSubsProvInfoDTO.getExtCorpId();
                if (extCorpId == null) {
                    throw new KnProvBOException(KnErrorCodes.BOEntity.EXTCORPID_CANNOT_BE_NULL_FOR_CORP_SUBS,
                            "Ext Corp ID cannot be null for a corporate subscriber");
                }
            }


            Integer subsClientType = bulkSubsProvInfoDTO.getClient_Type();

            int pocDonorRadioSupport = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getEnablePocDonorRadioSupport();
            if (pocDonorRadioSupport == KnProvConstants.POC_DONOR_RADIO.DISABLED.value()
                    && subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_DONOR_RADIO.value()) {
                knLogger.error(methodName, "POC_DONOR_RADIO is disabled");
                throw new KnProvBOException(KnErrorCodes.BOEntity.POC_DONOR_RADIO_DISABLED,
                        "Poc Donor Radio is disabled");
            }

            //Deprecating Existing pairing indicator because of new pairing logic in 8.0.2
            /*if (pairingIndicator && (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_DONOR_RADIO.value())) {
                knLogger.error(methodName, "Pairing Indicator should not enabled for POC Donor radio client type");
                throw new KnProvBOException(KnErrorCodes.BOEntity.PAIRING_IND_NOT_ALLOWED_POC_DONOR_RADIO,
                        "Pairing Indicator should not enabled for POC Donor radio client type");
            }*/


            //validate the subscription Types.
            if (publicSubscriptionType == KnConstants.PUBLIC_SUBSCRIPTION_TYPE.NONE.value()
                    && corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                knLogger.error(methodName, "Either Public or Corporate Subscription Types are required to be enabled");
                throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid public or Corp Subscription types passed");
            }

            if (publicSubscriptionType == -1 && corporateSubscriptionType == -1) {
                knLogger.error(methodName, "Either Public or Corporate Subscription Types are required to be enabled");
                throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_SUBSCRIPTION_TYPE, "Invalid public and Corp Subscription types passed");
            }

            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value() &&
                    subsClientType != KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value()) {
                knLogger.error(methodName, "Invalid client type for  public subscriber");
                throw new KnProvBOException(KnErrorCodes.BOEntity.INVALID_CLIENT_TYPE_FOR_PUBLIC_SUBS,
                        "Invalid client type set for public subscriber");
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);

            String licenseNoOfSubsCount = licenseInfo.getNoOfSubs();
            int totalSubsLimit = Integer.parseInt(licenseNoOfSubsCount);
            knLogger.debug(methodName, "Max subs count for license - ", totalSubsLimit);
            int subscriberCount = provXDMServerDAO.retrieveSubscriberCount(persisterTxn);
            if ((subscriberCount + subsCount) > totalSubsLimit) {
                knLogger.error(methodName, "Max subscriber limit reached for license ");
                throw new KnProvBOException(KnErrorCodes.BOEntity.MAX_SUBS_LIMIT_REACHED_FOR_LICENSE, "Max Subscriber limit reached for license");
            }

            int corpId = -1;
            KnOPCorpProfileInfoDTO corpProfileInfoDTO = null;
            String extCorpId = bulkSubsProvInfoDTO.getExtCorpId();

            //corp chk
            int existingPairInd = -1;
            int maxSubsCountPerCorp = 0;
            if (corporateSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                corpProfileInfoDTO = provXDMServerDAO.retrieveCorporateProfile(extCorpId, persisterTxn);
                int maxSubsCorporateLimit = corpProfileInfoDTO.getMaxSubscribers();
                // -->. creating Corporate Profile
                knLogger.debug(methodName, "verifying if corporate profile already exists");
                corpId = corpProfileInfoDTO.getCorpId();
                knLogger.debug(methodName, "Corporate Id Retrieved - ", corpId);

                if (corpId > 0) {
                    knLogger.debug(methodName, "corporate profile already exists with corp id [", corpId, "] ", "for extCorpId ", extCorpId);
                    existingPairInd = corpProfileInfoDTO.getPairedContactListId();

                    //Deprecating Existing pairing indicator because of new pairing logic in 8.0.2
                    /*knLogger.debug(methodName, "pairingIndicator:", pairingIndicator," existingPairInd:", existingPairInd, " corpId:", corpId);
                    if ((pairingIndicator ^ (existingPairInd != 0))) {
                        knLogger.error(methodName, "Mismatch in pairing indicator for the provided corporation ");
                        throw new KnProvBOException(KnErrorCodes.BOEntity.MISMATCH_IN_PAIRING_IND, "Mismatch in pairing indicator for the provided corporation");
                    }*/

                    int corpSubsCount = provInfoUtil.retrieveCorpSubsCount(corpId, persisterTxn);
                    int maxSubsPerCorpSystemLevel = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getMaxSubscrPerCorp();
                    if(maxSubsCorporateLimit > 0) {
                        maxSubsCountPerCorp = maxSubsPerCorpSystemLevel > maxSubsCorporateLimit ? maxSubsCorporateLimit : maxSubsPerCorpSystemLevel;
                    }else{
                        maxSubsCountPerCorp = maxSubsPerCorpSystemLevel;
                    }
                    knLogger.debug(methodName, "maxSubsPerCorpSystemLevel: ", maxSubsPerCorpSystemLevel, " ,maxSubsCorporateLimit: ",
                            maxSubsCorporateLimit, "corpSubsCount: ", corpSubsCount, "subsCount: ", subsCount);
                    if (corpSubsCount + subsCount > maxSubsCountPerCorp) {
                        knLogger.error(methodName, "Max Subscriber limit has been reached for corp - ", corpId);
                        throw new KnProvBOException(KnErrorCodes.Validator.MAX_SUBS_LIMIT_REACHED_FOR_CORP,  "Max Subscriber limit for corp has been reached");
                    }

                    Map<String, String> rtxEnvMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
                    if (existingPairInd != 0 && (corpSubsCount + subsCount) > Integer.parseInt(rtxEnvMap.get(KnConstants.CORP_AUTO_PAIRING_SIZE))) {
                    	 respDTO.setCorpAutoPairing(Boolean.FALSE);
                    }
                } else {
                    // setting dummy corp id for validation in custom layer
                    corpId = 2147483647;
                }
            }
            // creating Additional subscriber in custom
            Map<String, Object> customReqMap;
            if (bulkSubsProvInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customReqMap = bulkSubsProvInfoDTO.getCustomParamMap();
                knLogger.debug(methodName, "customReqMap ::", customReqMap);
                if (customReqMap == null) {
                    customReqMap = new HashMap<String, Object>();
                    customReqMap.put(KnProvConstants.ACCOUNT_TYPE_INDICATOR, KnConstants.ACCOUNT_TYPE.INDIVIDUAL.value());
                    customReqMap.put(KnProvConstants.RATE_PLAN, String.valueOf(KnConstants.SUBSCRIBERS_CLIENT_TYPE.WIFIONLY.value()));
                } else if (customReqMap.get(KnProvConstants.EXT_BAN_ID) != null && (customReqMap.get(KnProvConstants.EXT_FAN_ID)) != null) {
                    customReqMap.put(KnProvConstants.ACCOUNT_TYPE_INDICATOR, KnConstants.ACCOUNT_TYPE.BUSINESS.value());
                }
                customReqMap.put(KnProvConstants.CORP_ID, String.valueOf(corpId));

                KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();

                KnXDMPAMSubsProfInfoDTO subsProfInfoDTO = new KnXDMPAMSubsProfInfoDTO();

                customReqMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_PROV_VALIDATE_SUBS_OP);
                customReqMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                subsProfInfoDTO.setCustomParamMap(customReqMap);
                pamAccInfoDTO.setProfileDetails(subsProfInfoDTO);

                knLogger.debug(methodName, "CUSTOM_PROV_VALIDATE_SUBS_OP ");

                Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, pamAccInfoDTO);
                knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);
                if (customResp instanceof KnXDMRespDTO) {
                    KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;

                    if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                        knLogger.debug(methodName, "failed to create PAM  subs profile ");
                        throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());
                    } else {
                        knLogger.debug(methodName, "successfully validated subs profile");
                    }
                }
            }

            // end custom logic


            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage("Vaildated create subscribers");

            knLogger.info(methodName, "EXIT: validate Create Subscribers operation ");

            return respDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            knLogger.error(methodName, ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while validate Create Subscriber");
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while validate create Subscriber", e);
        }

    }


    /**
     * method to populate the Corporate Profile Info
     *
     * @param xdmPttServerId        String
     * @param subsProfilePersistDTO KnSubsProfilePersistDTO
     * @return KnCorpProfilePersistDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException BO Entity Exception
     */
    private KnCorpProfilePersistDTO populateCorpProfileInfoDTO(String xdmPttServerId, KnSubsProfileDTO subsProfilePersistDTO) throws KnProvBOException {
        String methodName = "populateCorpProfileInfoDTO(KnSubsProfilePersistDTO, KnPersisterTxn)";

        knLogger.debug(methodName, "populating the corpProfilePersistDTO");
        KnCorpProfilePersistDTO corpProfilePersistDTO = new KnCorpProfilePersistDTO();
        corpProfilePersistDTO.setExtCorpId(subsProfilePersistDTO.getExtCorpId());
        corpProfilePersistDTO.setXDMSHome(xdmPttServerId);
        corpProfilePersistDTO.setPocHome(subsProfilePersistDTO.getPoCHome());
        corpProfilePersistDTO.setHierarchyType(subsProfilePersistDTO.getHierarchyType());
        return corpProfilePersistDTO;
    }

    public KnOPUpdateSubsInfoDTO validateUpdateSubscriber(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                                          KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        String methodName = "validateUpdateSubscriber(KnIPBulkSubsProvInfoDTO, KnPersisterTxn)";
        KnOPUpdateSubsInfoDTO respDTO;
        knLogger.info(methodName, "ENTRY: validate update Subscribers with DTO - ", bulkSubsProvInfoDTO, " Txn - ", persisterTxn);

        try {


            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            KnPAMSubsProfInfoDTO subsProfInfoDTO = provXDMServerDAO.getPAMSubsProfInfo(bulkSubsProvInfoDTO.getPamAccId(), persisterTxn);
            String inputExtCorpId = bulkSubsProvInfoDTO.getExtCorpId();
            String dbExtCorpID = provXDMServerDAO.retrieveExtCorporationId(subsProfInfoDTO.getCorpID(), persisterTxn);
            int corpId = -1;
            knLogger.info(methodName, ":inputExtCorpId::", inputExtCorpId, "::dbExtCorpID:", dbExtCorpID, ":");
            knLogger.debug(methodName, ":isEqual(trim(inputExtCorpId), trim(subsProfInfoDTO.getExtCorpId()) )::", isEqual(trim(inputExtCorpId), trim(dbExtCorpID)));

            if (inputExtCorpId != null && isEqual(trim(inputExtCorpId), trim(dbExtCorpID))) {
                corpId = subsProfInfoDTO.getCorpID();
            } else {
                knLogger.error(methodName, "Provided corpId is invalid");
                throw new KnProvBOException(KnErrorCodes.BOEntity.CORPID_CHANGE_NOT_ALLOWED,
                        "CORP ID CHANGE NOT ALLOWED");
            }


            // updating subscriber in custom
            Map<String, Object> customReqMap;
            if (bulkSubsProvInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                customReqMap = bulkSubsProvInfoDTO.getCustomParamMap();
                knLogger.debug(methodName, "customReqMap ::", customReqMap);
                if (customReqMap == null) {
                    customReqMap = new HashMap<String, Object>();
                    customReqMap.put(KnProvConstants.ACCOUNT_TYPE_INDICATOR, KnConstants.ACCOUNT_TYPE.INDIVIDUAL.value());
                    customReqMap.put(KnProvConstants.RATE_PLAN, String.valueOf(KnConstants.SUBSCRIBERS_CLIENT_TYPE.WIFIONLY.value()));
                } else {
                    customReqMap.put(KnProvConstants.ACCOUNT_TYPE_INDICATOR, KnConstants.ACCOUNT_TYPE.BUSINESS.value());
                }
                customReqMap.put(KnProvConstants.CORP_ID, String.valueOf(corpId));

                KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
                KnXDMPAMSubsProfInfoDTO xdmSubsProfInfoDTO = new KnXDMPAMSubsProfInfoDTO();
                xdmSubsProfInfoDTO.setPamAccId(bulkSubsProvInfoDTO.getPamAccId());
                customReqMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_PROV_VALIDATE_UPDATE_SUBS_OP);
                customReqMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                xdmSubsProfInfoDTO.setCustomParamMap(customReqMap);
                pamAccInfoDTO.setProfileDetails(xdmSubsProfInfoDTO);

                knLogger.debug(methodName, "CUSTOM_PROV_VALIDATE_UPDATE_SUBS_OP ");

                Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, pamAccInfoDTO);
                knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);
                if (customResp instanceof KnXDMRespDTO) {
                    KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;

                    if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                        knLogger.debug(methodName, "failed to update PAM  subs profile ");
                        throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());
                    } else {
                        knLogger.debug(methodName, "successfully validated subs profile");
                    }
                }
            }

            // end custom logic

            respDTO = new KnOPUpdateSubsInfoDTO();
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setResponseMessage("Vaildated update subscribers");

            knLogger.info(methodName, "EXIT: validate update Subscribers operation ");

            return respDTO;
        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);

        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);

            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);

            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while validate update Subscriber", e);

            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while validate update Subscriber", e);
        }

    }

    /**
     * method for retrieve Subscriber Count
     *
     * @param persisterTxn KnPersisterTxn
     * @return List<String> (Subscriber count)
     * @throws KnDAOException exception DB Layer
     */
    public List<String> retrievePAMAccountProvMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "retrieveProvPAMAccountMDNs(int, KnPersisterTxn)";
        List<String> mdns;
        knLogger.entry(methodName, pamAccId, persisterTxn);
        try {

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            mdns = provXDMServerDAO.retrievePAMAccountProvMDNs(pamAccId, persisterTxn);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscribers does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get PAM account Subscribers", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get PAM Account Subscribers", e);
        }
        knLogger.entry(methodName, KnGDPRTemplate.mdnList(mdns));

        return mdns;
    }

    public List<String> getPamAccLastSequenceMdns(int pamAccId, int start, int end, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "getPamAccLastSequenceMdns(int, int, int,  KnPersisterTxn)";
        List<String> mdns;
        knLogger.entry(methodName, pamAccId, start, end, persisterTxn);
        try {

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            mdns = provXDMServerDAO.getPamAccLastSequenceMdns(pamAccId, start, end, persisterTxn);

        } catch (KnDBConnectionException ex) {
            knLogger.error(methodName, "DAO DBConnection Exception occured :", ex);
            throw new KnProvBOException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occurred : ", ex);
            if (com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnProvBOException(KnErrorCodes.BOEntity.SUBSCRIBER_INFO_NOT_FOUND, "Subscribers does not exists");
            }
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", ex);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while get PAM account Subscribers", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while get PAM Account Subscribers", e);
        }
        knLogger.entry(methodName, KnGDPRTemplate.mdnList(mdns));

        return mdns;
    }

    /**
     * This method is to update the dispatch_grp_member column and 27th bit of subscriberfs in subscrinfo table only.
     * This method will not update the corporate etag though subscriber profile is being modified expecting that the calling method has done thos job.
     *
     * @param subscribers
     * @param persisterTxn
     * @return
     * @throws KnProvException
     */
    @Override
    public KnOPBulkRespDTO updateDispForSubscribers(Map<String, Integer> subscribers, int bitNo, KnPersisterTxn persisterTxn) throws KnProvException {
        String methodName = "updateDispForSubscribers(Map<String, Integer>,KnPersisterTxn)";
        knLogger.info(methodName, "update dispatch column for subscribers ", subscribers.size(), bitNo);
        List<KnOPSubsDispatcherDTO> getListOfSubsPersistDTOs = null;
        List<String> listOfMdns = new ArrayList<String>(subscribers.keySet());
        KnOPCorpProfileInfoDTO corpProfileInfoDTO = null;
        KnOPSubsDispatcherDTO subsDispatcherDTO;
        List<KnOPSubsDispatcherDTO> subsProfilePersistDTOs = new ArrayList<>();
        int newDispatchGrpMember = 0;
        long lastProfileUpdateTime = 0;
        KnOPDispatchDirChgDTO opDispatchDirChgDTO = null;
        KnOPBulkRespDTO respDTO = new KnOPBulkRespDTO();
        Map<String, KnOPDispatchDirChgDTO> mdnDispatchDirChgMap = new HashMap<>();
        Map<String, Boolean> mapOfActiveFsChanges = new HashMap<>();
        try {

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            //retrieve list of DTOs for all the list of mdns
            getListOfSubsPersistDTOs = provXDMServerDAO.retrieveBulkSubscribers(listOfMdns, persisterTxn);
            knLogger.debug(methodName, "Bulk subscribers for dispatch changes are successfully retrieved with DTO", getListOfSubsPersistDTOs);
            if(bitNo == LOCATIONSUBSCRIPTION.value()){
                knLogger.debug(methodName, getListOfSubsPersistDTOs.size());
                getListOfSubsPersistDTOs = getListOfSubsPersistDTOs.stream().filter(subs -> subs.getClientPVmajorVer() >= PROTOCOL_VERSION_17)
                        .collect(Collectors.toList());
            }
            knLogger.debug(methodName, "finalSize ", getListOfSubsPersistDTOs.size());
            Map<String, String> activeFsMap = new HashMap<>();
            for (KnOPSubsDispatcherDTO existingSubsProfileDTO : getListOfSubsPersistDTOs) {
                //get the DispatchGrpMember column value for the mdn from the request map
                String mdn = existingSubsProfileDTO.getMdn();
                newDispatchGrpMember = subscribers.get(mdn);
                knLogger.debug(methodName, "updating dispatch flag for the mdn", KnGDPRTemplate.mdn(mdn), " ,newDispatchGrpMember", newDispatchGrpMember);
                String subsFS2 = existingSubsProfileDTO.getSubsFS2();
                String xdmsFS2=existingSubsProfileDTO.getXdmsFS2();

                //if dispatch group member flag changed then set/clear on demand location bit in subsFS
                //setting on demand location bit in subsFS
                if (newDispatchGrpMember == KnConstants.BIT_TRUE) {
                	xdmsFS2 = featureSetUtil.getSetFeatureSetBits(xdmsFS2, new int[]{bitNo});
                    knLogger.debug(methodName, "on demand location bit set in xdmsFS1:", xdmsFS2);
                } else {
                	xdmsFS2 = featureSetUtil.getClearFeatureSetBits(xdmsFS2, new int[]{bitNo});
                    knLogger.debug(methodName, "on demand location bit clear in xdmsFS1:", xdmsFS2);
                }

                knLogger.debug(methodName, "changed subscriberfs is", subsFS2);
                String pocPttId = existingSubsProfileDTO.getPoCHome();
                String presencePttId = existingSubsProfileDTO.getPresenceHome();
                String xdmsPttId = existingSubsProfileDTO.getXDMSHome();
                int newCorpType = existingSubsProfileDTO.getCorporateSubscriptionType();
                String opsFS2 = existingSubsProfileDTO.getOpsFS2();
                String corpAdminFS2 = existingSubsProfileDTO.getCorpAdminFS2();
                String clientFS2 = existingSubsProfileDTO.getClientFS2();
                int corpId = existingSubsProfileDTO.getCorpId();
                int subsClientType = existingSubsProfileDTO.getSubsClientType();
                String userProfileFS2 = existingSubsProfileDTO.getUserProfileFS2();
                if(userProfileFS2==null)
                {
                	userProfileFS2=featureSetUtil.getDefFinalUserProfileFS();
                }

                String corpFS2 = null;
                if (corpId != 0) {
                    String extCorpId = provXDMServerDAO.retrieveExtCorporationId(existingSubsProfileDTO.getCorpId(), persisterTxn);
                    knLogger.debug(methodName, "extcorpid", extCorpId);
                    corpProfileInfoDTO = provXDMServerDAO.retrieveCorporateProfile(extCorpId, persisterTxn);
                    corpFS2 = corpProfileInfoDTO.getCorpFS2();

                }


                //fetch all pocpttserverid, presencepttid, etc from subs profile
                int clientPVMajorVersion=existingSubsProfileDTO.getClientPVmajorVer();
                String clientCapOverrideBitMask=featureSetUtil.getClientCapabilityBitMask(xdmPttServerId,clientPVMajorVersion);
                String activeFS2;
                if (newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                    activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2, opsFS2,clientCapOverrideBitMask,xdmsFS2,userProfileFS2);
                } else {
                    activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2,clientCapOverrideBitMask,xdmsFS2,userProfileFS2);
                }
                if (!activeFS2.equals(existingSubsProfileDTO.getActiveFS2())) {
                    mapOfActiveFsChanges.put(mdn, true);
                } else {
                    mapOfActiveFsChanges.put(mdn, false);
                }

                lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                activeFsMap.put(mdn, activeFS2);
                if (!activeFS2.equals(existingSubsProfileDTO.getActiveFS2()) || !xdmsFS2.equals(existingSubsProfileDTO.getXdmsFS2())) {
                    subsDispatcherDTO = new KnOPSubsDispatcherDTO();
                    subsDispatcherDTO.setMdn(mdn);
                    subsDispatcherDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
                    subsDispatcherDTO.setDispatchGroupMember(newDispatchGrpMember);
                    subsDispatcherDTO.setSubsFS2(subsFS2);
                    subsDispatcherDTO.setActiveFS2(activeFS2);
                    subsDispatcherDTO.setXdmsFS2(xdmsFS2);
                    subsDispatcherDTO.setSubsClientType(subsClientType);
                    subsDispatcherDTO.setQppPkgId(existingSubsProfileDTO.getQppPkgId());
                    subsDispatcherDTO.setFirstNetIndicator(existingSubsProfileDTO.getFirstNetIndicator());
                    //adds the individual DTO to the list of DTOs
                    subsProfilePersistDTOs.add(subsDispatcherDTO);
                }
            }
            // update all mdns with populated value in dg.pocsubscrinfo table.
            knLogger.debug(methodName, "subsProfilePersistDTOs size ", subsProfilePersistDTOs.size());
            if(!subsProfilePersistDTOs.isEmpty()) {
                provXDMServerDAO.updateBulkSubscrProfile(subsProfilePersistDTOs, persisterTxn);
            }

            List<Integer> previousEtags = new ArrayList<>();
            List<String> fsChanagedListOfMdns = listOfMdns.stream().filter(e -> mapOfActiveFsChanges.getOrDefault(e, true)).collect(Collectors.toList());
            knLogger.info(methodName, "fsChanagedListOfMdns size", fsChanagedListOfMdns.size());
            if (!fsChanagedListOfMdns.isEmpty()) {
                IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                //retrieve the previous etag of dir doc
                previousEtags = commonXDMServerDAO.getCurrentEtagsForDirDoc(fsChanagedListOfMdns, persisterTxn);

                //update the dir doc etag
                commonXDMServerDAO.updateEtagForDirDoc(fsChanagedListOfMdns, persisterTxn);
                knLogger.debug(methodName, "Successfully updated the xdm directory");
            }
            //populating the Subs Config document change DTO
            //populating the Subscriber config doc DTO
            respDTO.setResponseMessage(KnProvConstants.UPDATE_DISP_SUBS_PROFILE_SUCCESS);
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setEtag(lastProfileUpdateTime);
            KnOPDocChgDTO docChgDTO;
            int i = 0;
            KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);

            List<KnOPSubsDispatcherDTO> listOfSubsPersistDTOs = getListOfSubsPersistDTOs.stream()
                .filter(e -> fsChanagedListOfMdns.contains(e.getMdn()))
                .collect(Collectors.toList());
            for (KnOPSubsDispatcherDTO existingSubsProfileDTO : listOfSubsPersistDTOs) {
                String mdn = existingSubsProfileDTO.getMdn();
                docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
                docChgDTO.setDocUri(subsConfigDocUri);
                docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));

                Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                chgDocList.add(docChgDTO);


                opDispatchDirChgDTO = new KnOPDispatchDirChgDTO();
                opDispatchDirChgDTO.setActiveFSChanged(mapOfActiveFsChanges.get(mdn));
                opDispatchDirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn,persisterTxn));
                opDispatchDirChgDTO.setPocHome(existingSubsProfileDTO.getPoCHome());
                opDispatchDirChgDTO.setPresenceHome(existingSubsProfileDTO.getPresenceHome());
                opDispatchDirChgDTO.setDocChgDTO(chgDocList);
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                opDispatchDirChgDTO.setDirUri(dirDocUri);
                opDispatchDirChgDTO.setDirPrevEtag(String.valueOf(previousEtags.get(i)));
                int newEtag = previousEtags.get(i) + 1;
                opDispatchDirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                opDispatchDirChgDTO.setProtoVersion(existingSubsProfileDTO.getClientPVmajorVer() + "." + existingSubsProfileDTO.getClientPVminorVer());
                opDispatchDirChgDTO.setCorpId(existingSubsProfileDTO.getCorpId());
                opDispatchDirChgDTO.setActiveFS2(activeFsMap.get(mdn));
                opDispatchDirChgDTO.setOldActiveFS(existingSubsProfileDTO.getActiveFS2());
                opDispatchDirChgDTO.setLastProfileUpdateTime(existingSubsProfileDTO.getLastProfileUpdateTime());
                i++;
                mdnDispatchDirChgMap.put(mdn, opDispatchDirChgDTO);


            }

            respDTO.setMdnDispatcherChgDTOMap(mdnDispatchDirChgMap);
            knLogger.debug(methodName, "EXIT:update dispatch column for subscriber with respDTO :", respDTO);
        } catch (KnFeatureSetException ex) {
            knLogger.error(methodName, "Feature Set Exception occured :", ex);
            throw new KnProvBOException(ex.getErrorCode(), ex.getMessage(), ex);


        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Update dispatchers Subscriber");
            knLogger.error(methodName, e);

            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while update dispatcher forSubscriber", e);
        }

        return respDTO;
    }


    private String trim(String input) {
        return input != null ? input.trim() : input;
    }

    private static boolean isEqual(String input, String input1) {
        boolean flag = false;
        if (input != null)
            flag = input.equals(input1);
        else if (input1 == null)
            flag = true;
        return flag;
    }


    /**
     * Rollback the transaction
     *
     * @param txn transaction object
     */
    private void rollback(KnPersisterTxn txn) {
        try {
            txn.rollback();
        } catch (Exception e) {
            knLogger.error("rollback(txn)", "Failed to rollback the transaction.", e);
        }
    }


    /**
     * @param bulkSubsProvInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    public List<KnOPUpdateSubsInfoDTO> updateHierarchy(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "updateHierarchy(KnIPBulkSubsProvInfoDTO)";
        knLogger.info(methodName, "ENTRY : bulkSubsProvInfoDTO-", bulkSubsProvInfoDTO);
        KnBulkSubsProfilePersistDTO subsProfilePersistDTO = null;
        List<KnOPUpdateSubsInfoDTO> response = new ArrayList<>();
        KnOPUpdateSubsInfoDTO updateSubsInfoDTO=null;
        KnXDMPAMSubsProfInfoDTO subsProfInfoDTO = new KnXDMPAMSubsProfInfoDTO();
        List<String> mdns=bulkSubsProvInfoDTO.getMdns();
        Map<String, KnOPSubsProfileInfoDTO> notificationDataMap;
        Boolean corpAutoPair = null;
        Boolean isCorpExist = null;
        try {

        	 Map<String, String> rtxEnvMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
        	 knLogger.debug(methodName, "param value map", rtxEnvMap);
        	 int corpAutoPairCnt = Integer.valueOf(rtxEnvMap.get(KnConstants.CORP_AUTO_PAIRING_SIZE));
        	 int corpSubsCount = provInfoUtil.retrieveCorpSubsCount(bulkSubsProvInfoDTO.getCorpID(), persisterTxn);

        	 if (corpSubsCount <= 0 ) {
        		 knLogger.debug(methodName, "Corporate  is newly created.");
        		 int enbaleAutoPair = Integer.valueOf(rtxEnvMap.get(KnConstants.ENABLE_CORP_AUTO_PAIRING));
        		 if (enbaleAutoPair == 1 && bulkSubsProvInfoDTO.isAutoPair()) {
        			 knLogger.info(methodName, "System flag corp auto pairing is enabled and CBE is also configured.");
        			 if (KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value() == bulkSubsProvInfoDTO.getClient_Type()) {
        				// to enable auto pairing
        				 corpAutoPair = true;
        				 isCorpExist = false;
        			 }
        		 }

        	 } else {
        		 knLogger.debug(methodName, "Corporate  is already Exist.");
        		 KnOPCorpProfileInfoDTO corpProfile = provInfoUtil.retrieveCorpProfile(bulkSubsProvInfoDTO.getExtCorpId(), persisterTxn);
        		 if (corpProfile.getPairedContactListId() > 0) {
        			 if ((corpSubsCount <= corpAutoPairCnt) && bulkSubsProvInfoDTO.isAutoPair()) {
        				 if (KnProvConstants.SUBS_CLIENT_TYPE.HANDSET.value() == bulkSubsProvInfoDTO.getClient_Type()) {
        					 corpAutoPair = true;
            				 isCorpExist = true;
        				 }
        			 }
        		 }
        	 }

            //TODO updateBulkSubscr corpid with extcorpid update
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            subsProfilePersistDTO = new KnBulkSubsProfilePersistDTO();
            KnSubsProfileDTO subsProfile = new KnSubsProfileDTO();
            subsProfile.setCorpId(bulkSubsProvInfoDTO.getCorpID());
            subsProfile.setAccountId(bulkSubsProvInfoDTO.getExtCorpId());
            //subsProfile.setMdn(bulkSubsProvInfoDTO.getMdns().get(0));
            subsProfilePersistDTO.setSubsProfile(subsProfile);
            subsProfilePersistDTO.setMdns(bulkSubsProvInfoDTO.getMdns());
            knLogger.info(methodName, "subsProfilePersistDTO-", subsProfilePersistDTO);
            provXDMServerDAO.updateBulkSubscrCorpId(subsProfilePersistDTO, persisterTxn);


            //get current etag directory doc for all mdn
            //TODO get current etag dir doc for all mdn
            IXDMServerDAO commonXdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            knLogger.debug(methodName, "updating the directories ");
            List<Integer> previousEtags = commonXdmServerDAO.getCurrentEtagsForDirDoc(mdns, persisterTxn);

            //retrieve notification details for all mdn
            //TODO retrieve notification details for mdn
            notificationDataMap = provXDMServerDAO.retrieveNotificationDetails4mdns(mdns, persisterTxn);

            //TODO update etag for dir doc
            commonXdmServerDAO.updateEtagForDirDoc(mdns, persisterTxn);


            if (bulkSubsProvInfoDTO.getHierarchyType() == HIERARCHY_TYPE.HIERARCHY) {
                knLogger.info(methodName, "Custom is On.");

                /*customRequestMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_UPDATE_BULK_SUBSCRBAN);
                customRequestMap.put(KnConstants.PERSISTER_TXN, persisterTxn);
                customRequestMap.put(KnProvConstants.EXT_FAN_ID,bulkSubsProvInfoDTO.getCustomParamMap().get(KnProvConstants.EXT_FAN_ID));
                customRequestMap.put(KnProvConstants.EXT_BAN_ID,bulkSubsProvInfoDTO.getCustomParamMap().get(KnProvConstants.EXT_BAN_ID));
                subsProfInfoDTO.setCustomParamMap(customRequestMap);*/
                KnXDMSubsProvInfoDTO subsProvInfoDTO=new KnXDMSubsProvInfoDTO();
                /*KnXDMPAMAccInfoDTO pamAccInfoDTO = new KnXDMPAMAccInfoDTO();
                KnXDMPAMSubsProfInfoDTO xdmSubsProfInfoDTO = new KnXDMPAMSubsProfInfoDTO();*/
                //xdmSubsProfInfoDTO.setPamAccId(bulkSubsProvInfoDTO.getPamAccId());
                Map<String, Object> customRequestMap = new HashMap<String, Object>();
                customRequestMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_UPDATE_BULK_SUBSCRBAN);
                customRequestMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
                customRequestMap.put(KnProvConstants.EXT_FAN_ID,bulkSubsProvInfoDTO.getCustomParamMap().get(KnProvConstants.EXT_FAN_ID));
                customRequestMap.put(KnProvConstants.EXT_BAN_ID,bulkSubsProvInfoDTO.getCustomParamMap().get(KnProvConstants.EXT_BAN_ID));
                subsProvInfoDTO.setCustomParamMap(customRequestMap);
                /*subsProvInfoDTO.setCustomParamMap(customRequestMap);
                pamAccInfoDTO.setProfileDetails(xdmSubsProfInfoDTO);*/


                Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, subsProvInfoDTO);
                knLogger.debug(methodName, "Response from the Custom BulkSubsc update Ban - ", customResp);
                if (customResp instanceof KnXDMRespDTO) {
                    KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;
                    if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                        knLogger.debug(methodName, "failed to Update Bulk Subscr Ban ");
                        throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());
                    }
                    knLogger.debug(methodName, "successfully Updated Bulk Subscr Ban ");
                }
            } else {
                knLogger.info(methodName, "Custom is Not installed");
            }

            knLogger.info(methodName,"After calling BulkSubScr Update Ban");
            //Populating notification data
            //List<KnOPUpdateSubsInfoDTO> updateSubsInfoDTOs = new ArrayList<KnOPUpdateSubsInfoDTO>(50);
            int i=0;
            for (String mdn : mdns) {
                KnOPSubsProfileInfoDTO subsProfileInfoDTO = notificationDataMap.get(mdn);
                knLogger.info(methodName,"inside notification-",subsProfileInfoDTO);
                updateSubsInfoDTO=new KnOPUpdateSubsInfoDTO();
                updateSubsInfoDTO.setResponseMessage(KnProvConstants.UPDATE_SUBS_PROFILE_SUCCESS);
                updateSubsInfoDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
                long currentTime=System.currentTimeMillis();
                updateSubsInfoDTO.setEtag(currentTime);
                //populating the Subs Config document change DTO
                KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
                docChgDTO.setDocUri(subsConfigDocUri);
                docChgDTO.setNewEtag(String.valueOf(currentTime));

                Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
                docChgList.add(docChgDTO);
                String xcapRooturi = genInfoUtil.getXCAPRootURI(mdn, persisterTxn);

                //populating Dir Document change DTO
                KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                dirChgDTO.setXcapRootURI(xcapRooturi);
                dirChgDTO.setPocHome(subsProfileInfoDTO.getPoCHome());
                dirChgDTO.setPresenceHome(subsProfileInfoDTO.getPresenceHome());
                dirChgDTO.setDocChgDTO(docChgList);
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                dirChgDTO.setDirUri(dirDocUri);
                int previousEtag = previousEtags.get(i++);
                dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
                int newEtag = previousEtag + 1;
                dirChgDTO.setDirNewEtag(String.valueOf(newEtag));

                //setting the Document Change DTO the response
                updateSubsInfoDTO.setDirChgDTO(dirChgDTO);
                updateSubsInfoDTO.setResponseMessage(KnProvConstants.CUSTOM_PROV_UPDATEBAN_OP);
                updateSubsInfoDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());

                //setting autopair fields
                updateSubsInfoDTO.setCorpAutoPairing(corpAutoPair);
                updateSubsInfoDTO.setIsOldCorp(isCorpExist);
                response.add(updateSubsInfoDTO);
            }
            knLogger.info(methodName,"After setting notification response");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        } catch (KnProcessInvokerException e) {
            knLogger.error(methodName, "ProcessInvoker Exception occurred :", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while update bulk subscr ban", e);
        } catch (KnBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while validate corp subs limit", e);
        }
        knLogger.info(methodName, "response-", response);
        return response;
    }

   /* *//**
     * //TODO
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     *//*
    @Override
    public List<KnOPProvDTO> sendBulkConfigDocNotification(List<String> mdnList,KnPersisterTxn persisterTxn)throws KnProvBOException{
        final String methodName="sendBulkConfigDocNotification(List<String>)";
        knLogger.debug(methodName,"ENTRY : ",mdnList);


    }*/

    /**
     * @param corpId
     * @param subsCount
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    @Override
    public boolean validateCorpSubsLimitAndPairLimit(int corpId, int subsCount, KnPersisterTxn persisterTxn) throws KnProvBOException {
        final String methodName = "validateCorpSubsLimit(int,int)";
        knLogger.debug(methodName, "ENTRY : corpId", corpId, " ,subsCount", subsCount);
        boolean isAutoPairLimitReached = false;
        int maxSubsCountPerCorp = 0;
        try {
            String extCorpId = provInfoUtil.retrieveExtCorpId(corpId);
            KnOPCorpProfileInfoDTO corpProfileRespDTO = provInfoUtil.retrieveCorpProfile(extCorpId, persisterTxn);
            int maxSubsPerCorpSystemLevel = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn).getMaxSubscrPerCorp();
            int maxSubsCorporateLimit = corpProfileRespDTO.getMaxSubscribers();
            if(maxSubsCorporateLimit > 0) {
                maxSubsCountPerCorp = maxSubsPerCorpSystemLevel > maxSubsCorporateLimit ? maxSubsCorporateLimit : maxSubsPerCorpSystemLevel;
            }else{
                maxSubsCountPerCorp = maxSubsPerCorpSystemLevel;
            }
            int corpSubsCount = provInfoUtil.retrieveCorpSubsCount(corpId, persisterTxn);
            knLogger.debug(methodName, "maxSubsPerCorpSystemLevel: ", maxSubsPerCorpSystemLevel, " ,maxSubsCorporateLimit: ",
                    maxSubsCorporateLimit, "corpSubsCount: ", corpSubsCount);
            if (corpSubsCount + subsCount > maxSubsCountPerCorp) {
                knLogger.error(methodName, "Max Subscriber limit has been reached for corp - ", corpId);
                throw new KnProvBOException(KnErrorCodes.Validator.MAX_SUBS_LIMIT_REACHED_FOR_CORP, "Max Subscriber limit for corp has been reached");
            }

            Map<String, String> rtxEnvMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            int corpAutoPairCnt = Integer.valueOf(rtxEnvMap.get(KnConstants.CORP_AUTO_PAIRING_SIZE));
            if ((corpSubsCount + subsCount) > corpAutoPairCnt) {
            	 knLogger.error(methodName, "corp subscribers exceeds corp auto pairing size - ", corpSubsCount, corpAutoPairCnt);
            	 isAutoPairLimitReached  = true;
            }

        } catch (KnBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while validate corp subs limit", e);

        }
        knLogger.debug(methodName, "EXIT ",isAutoPairLimitReached);
        return isAutoPairLimitReached;
    }


    @Override
    public List<String> retrieveBanMDNs(int banId, HIERARCHY_TYPE hierarchy, KnPersisterTxn persisterTxn) throws KnProvException{
        final String methodName="retrieveBanMDNs(int)";
        knLogger.debug(methodName,"ENTRY : extBanId - ",banId);
        knLogger.debug(methodName,"calling custom retrieve ban mdns");
        KnXDMSubsProvInfoDTO subsProvInfoDTO=new KnXDMSubsProvInfoDTO();
        List<String> banMdnList=new ArrayList<>();
        try {

        	if (hierarchy == HIERARCHY_TYPE.HIERARCHY) {
            knLogger.info(methodName, "Custom is On.");
            Map<String, Object> customRequestMap = new HashMap<String, Object>();
            customRequestMap.put(KnProvConstants.CUSTOM_PROV_ACTION, KnProvConstants.CUSTOM_GET_BANMDNLIST);
            customRequestMap.put(KnProvConstants.INT_BAN_ID,banId);
            customRequestMap.put(com.kodiak.common.resources.KnConstants.PERSISTER_TXN, persisterTxn);
            subsProvInfoDTO.setCustomParamMap(customRequestMap);


            Object customResp = KnProcessInvokerImpl.getInstance().invokeHook(KnProvConstants.CUSTOM_PROV_INVOKER, subsProvInfoDTO);
            knLogger.debug(methodName, "Response from the Custom Prov - ", customResp);
            if (customResp instanceof KnXDMRespDTO) {
                KnXDMRespDTO customResponseDTO = (KnXDMRespDTO) customResp;
                if (customResponseDTO.getResponseStatus() == KnConstants.RESPONSE_STATUS.FAILURE.value()) {
                    knLogger.debug(methodName, "failed to Retrieve Ban mdn list ");
                    throw new KnProvBOException(customResponseDTO.getResponseCode(), customResponseDTO.getResponseMessage());
                }
                if (customResponseDTO.getCustomParamMap().containsKey(KnProvConstants.BAN_MDNLIST)) {
                    banMdnList =(List)  customResponseDTO.getCustomParamMap().get(KnProvConstants.BAN_MDNLIST);
                }

                knLogger.debug(methodName, "successfully retrieved  Ban mdn list-banMdnList ",banMdnList);
            }
        } else {
            knLogger.info(methodName, "Custom is Not installed");
        }
        	} catch (KnProcessInvokerException e) {
            knLogger.error(methodName, "ProcessInvoker Exception occurred :", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while retrieve Ban Mdn list", e);

        }
        knLogger.debug(methodName, "EXIT : banMdnList ",KnGDPRTemplate.mdnList(banMdnList));
        return banMdnList;


    }

    /**
     *
     * @param bulkSubsProvInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     */
    @Override
    public KnOPProvDTO updatePAMSubsProfCorpId(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO,int oldCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException{
        final String methodName="updatePAMSubsProfCorpId(KnPAMAccPersistDTO)";
        knLogger.debug(methodName,"ENTRY  : newCorpId-",bulkSubsProvInfoDTO.getCorpID()," ,oldCorpId-",oldCorpId);
        IProvXDMServerDAO provXDMServerDAO = null;
        KnOPProvDTO response=null;
        try {
            provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnPAMAccPersistDTO pamAccPersistDTO=new KnPAMAccPersistDTO();
            KnPAMSubsProfInfoDTO subsProfInfoDTO=new KnPAMSubsProfInfoDTO();
            subsProfInfoDTO.setCorpID(bulkSubsProvInfoDTO.getCorpID());
            pamAccPersistDTO.setProfileDetails(subsProfInfoDTO);
            provXDMServerDAO.updatePAMSubsProfCorpId(pamAccPersistDTO,oldCorpId,persisterTxn);
            response=new KnOPProvDTO();
            response.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            response.setResponseMessage("Updated Pam Subscriber CorpId");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        }
        knLogger.debug(methodName,"EXIT : ",response);
        return  response;
    }

    /**
     * This method is for updating the feature bit.
     * @param bulkSubsProvInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnProvBOException
     * @throws KnFWException
     */
    public  KnOPBulkRespDTO updateBulkSubsFSAndPkgIds (KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException, KnFWException {
        String methodName ="updateBulkSubsFSAndPkgIds( KnIPBulkSubsProvInfoDTO ,KnPersisterTxn)";
        knLogger.info(methodName, "Enters the Method with DTO :", bulkSubsProvInfoDTO);
        boolean isPkgCodeChanged=false;
        List<KnOPSubsDispatcherDTO> getListOfSubsPersistDTOs = null;
        KnOPCorpProfileInfoDTO corpProfileInfoDTO = null;
        KnOPSubsDispatcherDTO subsDispatcherDTO= null;
        KnOPDispatchDirChgDTO opDispatchDirChgDTO = null;
        long lastProfileUpdateTime = 0;
        List<KnOPSubsDispatcherDTO> subsProfilePersistDTOs = new ArrayList<KnOPSubsDispatcherDTO>();
        Map<String, KnOPDispatchDirChgDTO> mdnDispatchDirChgMap = new HashMap<String, KnOPDispatchDirChgDTO>();
        Map<String, Boolean> mapOfActiveFsChanges = new HashMap<String, Boolean>();
        Map<Integer,Integer> provFSMap= new HashMap<Integer, Integer>();
        KnOPBulkRespDTO respDTO = new KnOPBulkRespDTO();

        try {
            provFSMap=bulkSubsProvInfoDTO.getProvFSMap();
            knLogger.debug(methodName, " provFSMap :", provFSMap);
            Map<String, Integer> finalPkgIdsMap = new HashMap<String, Integer>();
            Map<String, Map<String, Integer>>  pkgIdMap=bulkSubsProvInfoDTO.getPkgIdMap();
            
            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            KnPAMSubsProfInfoDTO existingSubsProf = provXDMServerDAO.getPAMSubsProfInfo(bulkSubsProvInfoDTO.getPamAccId(), persisterTxn);
            List<String> addonPkgList =provXDMServerDAO.selectPAMSubAddOnPkgs(bulkSubsProvInfoDTO.getPamAccId(), persisterTxn);
            existingSubsProf.setAddOnPkgId(addonPkgList);
            
            String existingTierPkg = existingSubsProf.getTierPkgCode();
			List<String> existingAddonPkgs = existingSubsProf.getAddOnPkgId();
			Map<String, Integer> exsitingPkgIds = new HashMap<String, Integer>();
			if (existingTierPkg != null)
				exsitingPkgIds.put(existingTierPkg,
						com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE);

			if (existingAddonPkgs != null) {
				for (String pkgCode : existingAddonPkgs) {
					exsitingPkgIds.put(pkgCode, com.kodiak.xdms.server.common.resources.KnConstants.ADDON_PKG_TYPE);
				}
			}
			knLogger.info(methodName, "PkgIdsMap ", pkgIdMap + " exsitingPkgIds " + exsitingPkgIds);
			Map<String, Integer> exsitingSubsPkg = new HashMap<String, Integer>(exsitingPkgIds);
			knLogger.info(methodName, " exsitingSubsPkg " + exsitingSubsPkg);

			if (pkgIdMap != null) {
				List<String> qppPkgCodes=featureSetUtil.getQPPPkgCodes(xdmPttServerId);
    			if (pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION) != null
    					&& !pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION).isEmpty()) {
    				if (pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION) != null
    						&& !pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION)
    								.isEmpty()) {
    					finalPkgIdsMap = exsitingPkgIds;
    					boolean status = false;
    					Map<String, Integer> removePkgIds = pkgIdMap
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
    						status=true;
    					}

    					Map<String, Integer> addPkgIds = pkgIdMap
    							.get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION);
    					
    					for (String pkgCode : addPkgIds.keySet()) {
    						if (!exsitingPkgIds.containsKey(pkgCode.trim())) {
    							List<String> qppPkgs = qppPkgCodes.stream().filter(x -> exsitingPkgIds.keySet().contains(x))
										.collect(Collectors.toList());
								if(qppPkgs.size() > 0 && qppPkgCodes.contains(pkgCode) )
								{
									throw new KnProvBOException(KnErrorCodes.BOEntity.QPPPKG_ASSIGNED,
											"qpp pkg already assign!!");
								}
    							if (exsitingPkgIds
    									.containsValue(com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue())
    									&& addPkgIds.get(pkgCode)
    											.intValue() == com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue()) {
    								throw new KnProvBOException(KnErrorCodes.BOEntity.TIERPKG_ASSIGNED,
    										"tier pkg already assign!!");
    							}
    							
    							finalPkgIdsMap.put(pkgCode.trim(), addPkgIds.get(pkgCode));
    							knLogger.info(methodName, "Pkg code added  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
    							status = true;
    						}

    						if (exsitingPkgIds.containsKey(pkgCode.trim())
    								&& exsitingPkgIds.get(pkgCode.trim()).intValue() != addPkgIds.get(pkgCode).intValue()) {
    							throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
    									"package id not configured as tier or Addon!!");
    						}
    					}

    					if (status)
    						isPkgCodeChanged = true;
    				} else {
    					finalPkgIdsMap = exsitingPkgIds;
    					Map<String, Integer> newPkgIds = pkgIdMap
    							.get(com.kodiak.xdms.server.common.resources.KnConstants.ADD_ACTION);
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
    							if (exsitingPkgIds
    									.containsValue(com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue())
    									&& newPkgIds.get(pkgCode)
    											.intValue() == com.kodiak.xdms.server.common.resources.KnConstants.TIER_PKG_TYPE.intValue()) {
    								throw new KnProvBOException(KnErrorCodes.BOEntity.TIERPKG_ASSIGNED,
    										"tier pkg already assign!!");
    							}
    							
    							finalPkgIdsMap.put(pkgCode.trim(), newPkgIds.get(pkgCode));
    							knLogger.info(methodName, "Pkg code added  ",pkgCode.trim() + " finalPkgIdsMap " + finalPkgIdsMap);
    							status = true;
    						}

    						if (exsitingPkgIds.containsKey(pkgCode.trim())
    								&& exsitingPkgIds.get(pkgCode.trim()).intValue() != newPkgIds.get(pkgCode).intValue()) {
    							throw new KnProvBOException(KnErrorCodes.BOEntity.PKG_NOT_FOUND,
    									"package id not configured as tier or Addon!!");
    						}
    					}

    					if (status)
    						isPkgCodeChanged = true;
    				}
    			} else {
    				if (pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION) != null
    						&& !pkgIdMap.get(com.kodiak.xdms.server.common.resources.KnConstants.REMOVE_ACTION)
    								.isEmpty()) {
    					finalPkgIdsMap = exsitingPkgIds;
    					Map<String, Integer> newPkgIds = pkgIdMap
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
    					}

    					isPkgCodeChanged = true;
    				}
    			}
      
    		  }
            
            
            KnPAMSubsProfInfoDTO subsProfInfoDTO =(KnPAMSubsProfInfoDTO)bulkSubsProvInfoDTO;
            knLogger.debug(methodName, "  subsProfInfoDTO :", subsProfInfoDTO);
            List<String> listOfMdns = new ArrayList<String>(subsProfInfoDTO.getMdns());
            Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
            
            provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();
            getListOfSubsPersistDTOs = provXDMServerDAO.retrieveBulkSubscribers(listOfMdns, persisterTxn);
            knLogger.debug(methodName, "  List<KnOPSubsDispatcherDTO> getListOfSubsPersistDTOs :", getListOfSubsPersistDTOs);
            int oldSubsClientType = subsProfInfoDTO.getClient_Type();
            knLogger.debug(methodName, "old client type :", oldSubsClientType);
            int newSubsClientType = existingSubsProf.getClient_Type();
            int publicSubscriptionType= existingSubsProf.getPubSubsType();
            int corpSubscriptionType=existingSubsProf.getCorpSubsType();
            String tierPackageId = existingSubsProf.getTierPkgCode();
			Map<String, Integer> addonPackageIds = new HashMap<>();
			String newSubsFS2=null;
			if(isPkgCodeChanged)
            {
            	 
            	BitSet finalFSBitSet = new BitSet(Long.SIZE);
				String basePkgCode = paramNameValueMap.get(KnConstants.BASE_PKGCODE);
				String basePkgFS = featureSetUtil.getDefSubsFeatureSetForBasePkg(publicSubscriptionType,
						corpSubscriptionType, newSubsClientType, basePkgCode, xdmPttServerId);
				knLogger.debug(methodName, "base pkg is applied - ", basePkgFS);
				BitSet basePkgBiSet = featureSetUtil.convertHexStringToBitSet(basePkgFS);
				finalFSBitSet.or(basePkgBiSet);
				String PkgCodeFS  = featureSetUtil.getDefSubsFeatureSetForPkgCodes(publicSubscriptionType,
						corpSubscriptionType, newSubsClientType, finalPkgIdsMap, xdmPttServerId);
				knLogger.debug(methodName, "addPkgIds is present with subscriberFS - ", PkgCodeFS);
				BitSet PkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(PkgCodeFS);
				finalFSBitSet.or(PkgCodeBiSet);
				newSubsFS2 = featureSetUtil.convertBitSetToHexString(finalFSBitSet);
				String updatedTierPkg=null;
				for (Entry<String, Integer> entry : finalPkgIdsMap.entrySet()) {
					if (entry.getValue().intValue() == KnConstants.TIER_PKG_TYPE.intValue()) {
						updatedTierPkg = entry.getKey();
					} else if (entry.getValue() == KnConstants.ADDON_PKG_TYPE.intValue()) {
						addonPackageIds.put(entry.getKey(), entry.getValue());
					}
				}
				
				tierPackageId=updatedTierPkg;
               
            }
            
            BitSet bitset = new BitSet();
            String subsFS2=null;
            String activeFS2=null;
			Integer dataPkgId = existingSubsProf.getDataPkgId();
            for (KnOPSubsDispatcherDTO existingSubsProfileDTO : getListOfSubsPersistDTOs) {
				if (isPkgCodeChanged) {
					BitSet finalFSBitSet = new BitSet(Long.SIZE);
					BitSet PkgCodeBiSet = featureSetUtil.convertHexStringToBitSet(newSubsFS2);
					finalFSBitSet.or(PkgCodeBiSet);
					int oldIntropBitStatus=findInterOPBitStatus(existingSubsProfileDTO, exsitingSubsPkg, persisterTxn);
					if (finalFSBitSet.get(KnConstants.LMR_BIT) && KnConstants.LMR_BIT_STATUS.MANUALLY_DISABLED.Value()==oldIntropBitStatus) {
						finalFSBitSet.clear(KnConstants.LMR_BIT);
					} else if (!finalFSBitSet.get(KnConstants.LMR_BIT) && KnConstants.LMR_BIT_STATUS.MANUALLY_ENABLED.Value()==oldIntropBitStatus) {
						finalFSBitSet.set(KnConstants.LMR_BIT);
					}
					subsFS2=featureSetUtil.convertBitSetToHexString(finalFSBitSet);
					
				} else {
					subsFS2=existingSubsProfileDTO.getSubsFS2();
				}


                if(provFSMap != null) {
                    bitset= featureSetUtil.convertHexStringToBitSet(subsFS2);
                for(Map.Entry<Integer,Integer> provEntry: provFSMap.entrySet()){
                    if(provEntry.getValue()== com.kodiak.common.resources.KnConstants.PROVFS_ENABLE.ENABLED.value()){
                        bitset.set(provEntry.getKey());
                    }else{
                        bitset.clear(provEntry.getKey());
                    }
                }
                    subsFS2=featureSetUtil.convertBitSetToHexString(bitset);
                }

                String provFS2= featureSetUtil.convertBitSetToHexString(bitset);
                String provFS2BitMask= provFS2;
                knLogger.debug(methodName,"  subsFS2 ",subsFS2,"provFS2" ,provFS2);

                //Generate the SubsFeatureSet by performing the BitMask with License SubsFSBITMASK
                subsFS2 = featureSetUtil.generateSubsFeatureSet(subsFS2, provFS2, provFS2BitMask);
                knLogger.debug(methodName, "changed subscriberfs is : ", subsFS2);

                String mdn = existingSubsProfileDTO.getMdn();
                String pocPttId = existingSubsProfileDTO.getPoCHome();
                String presencePttId = existingSubsProfileDTO.getPresenceHome();
                String xdmsPttId = existingSubsProfileDTO.getXDMSHome();
                int newCorpType = existingSubsProfileDTO.getCorporateSubscriptionType();
                String opsFS2 = existingSubsProfileDTO.getOpsFS2();
                String corpAdminFS2 = existingSubsProfileDTO.getCorpAdminFS2();
                String clientFS2 = existingSubsProfileDTO.getClientFS2();
                int corpId = existingSubsProfileDTO.getCorpId();


                String corpFS2 =null;
                if (corpId != 0) {
                    String extCorpId = provXDMServerDAO.retrieveExtCorporationId(existingSubsProfileDTO.getCorpId(), persisterTxn);
                    knLogger.debug(methodName, "extcorpid", extCorpId);
                    corpProfileInfoDTO = provXDMServerDAO.retrieveCorporateProfile(extCorpId, persisterTxn);
                    corpFS2 = corpProfileInfoDTO.getCorpFS2();

                }
                //fetch all pocpttserverid, presencepttid, etc from subs profile
                int clientPVMajorVersion=existingSubsProfileDTO.getClientPVmajorVer();
                String clientCapOverrideBitMask=featureSetUtil.getClientCapabilityBitMask(xdmPttServerId,clientPVMajorVersion);
                knLogger.debug(methodName, "clientCapOverrideBitMask", clientCapOverrideBitMask,
                        "clientPVMajorVersion :",clientPVMajorVersion);

                
                String xdmsFS2 = existingSubsProfileDTO.getXdmsFS2();
                String userProfileFS2 = existingSubsProfileDTO.getUserProfileFS2();
                if(userProfileFS2==null)
                {
                	userProfileFS2=featureSetUtil.getDefFinalUserProfileFS();
                }
                if (newCorpType == KnConstants.CORP_SUBSCRIPTION_TYPE.NONE.value()) {
                    activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2, opsFS2,clientCapOverrideBitMask,xdmsFS2,userProfileFS2);
                } else {
                    activeFS2 = featureSetUtil.generateActiveFeatBitSet(pocPttId, presencePttId, xdmsPttId, clientFS2, subsFS2, corpFS2, opsFS2, corpAdminFS2,clientCapOverrideBitMask,xdmsFS2,userProfileFS2);
                }
                if (!activeFS2.equals(existingSubsProfileDTO.getActiveFS2())) {
                    mapOfActiveFsChanges.put(mdn, true);
                } else {
                    mapOfActiveFsChanges.put(mdn, false);
                }
                knLogger.debug(methodName, "Generated activeFS2 : ", activeFS2);

                lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                subsDispatcherDTO = new KnOPSubsDispatcherDTO();
                subsDispatcherDTO.setMdn(mdn);
                subsDispatcherDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
                subsDispatcherDTO.setDispatchGroupMember(existingSubsProfileDTO.getDispatchGroupMember());
                subsDispatcherDTO.setSubsFS2(subsFS2);
                subsDispatcherDTO.setActiveFS2(activeFS2);
                subsDispatcherDTO.setXdmsFS2(xdmsFS2);;
                subsDispatcherDTO.setSubsClientType(newSubsClientType);
                //adds the individual DTO to the list of DTOs
                Integer qppPkgId = existingSubsProfileDTO.getQppPkgId();
    			Integer profileId = null;
				if (isPkgCodeChanged) {
					
					if (!addonPackageIds.isEmpty()) {
						for (String addonPkgCode : addonPackageIds.keySet()) {
							profileId = featureSetUtil.getAddProfIdForPkg(addonPkgCode, xdmPttServerId);
							if (profileId != null) {
								qppPkgId = genInfoUtil.getDataPkgId(KnConstants.QPP_DATA_PKG_TYPE, persisterTxn)
										.get(profileId);
								if (qppPkgId != null) {
									dataPkgId = qppPkgId;
									break;
								} else if (dataPkgId == null) {
									dataPkgId = genInfoUtil.getDataPkgId(KnConstants.ADDON_DATA_PKG_TYPE, persisterTxn)
											.get(profileId);
								}

							}
						}

					} else {
						qppPkgId = null;
						dataPkgId = null;
					}
					if (qppPkgId == null) {
						qppPkgId = KnConstants.DEFAULT_QPP_ID;
					}
					if (dataPkgId == null) {
						dataPkgId = KnConstants.DEFAULT_DATAPKG_ID;
					}
					if (profileId == null) {
						profileId = KnConstants.DEFAULT_PROFILE_ID;
					}
					subsDispatcherDTO.setQppPkgId(qppPkgId);
				}
				if (subsProfInfoDTO.getFirstNetIndicator() != null) {
					subsDispatcherDTO.setFirstNetIndicator(subsProfInfoDTO.getFirstNetIndicator());
				} else {
					subsDispatcherDTO.setFirstNetIndicator(existingSubsProf.getFirstNetIndicator());
				}
                subsProfilePersistDTOs.add(subsDispatcherDTO);
                knLogger.debug(methodName, "subsDispatcherDTO : ", subsDispatcherDTO);
            }
            knLogger.debug(methodName, "subsProfilePersistDTOs : ", subsProfilePersistDTOs);

            // update all mdns with populated value in dg.pocsubscrinfo table.
            provXDMServerDAO.updateBulkSubscrProfile(subsProfilePersistDTOs, persisterTxn);
            
			if (isPkgCodeChanged) {
				//update addon pkg codes 
				provXDMServerDAO.deleteBulkSubAddlOnPkgs(listOfMdns, persisterTxn);
				

				if (!addonPackageIds.isEmpty()) {
					provXDMServerDAO.createBulkSubAddOnPkgs(listOfMdns, new ArrayList<>(addonPackageIds.keySet()),
							persisterTxn);
				}
                
                // -->. updating entry into the pocsubsaddlinfo 

                //check subscriber addlInfo exists
				List<String> createMdnList = new ArrayList<>();
				List<String> updateMdnList = new ArrayList<>();
                Map<String, KnSubsAddlInfoDTO> subscriberAddlProfilesMap = provXDMServerDAO.retrieveSubscrAddlInfo(listOfMdns, FALSE, persisterTxn);
				knLogger.debug(methodName, "subscriberAddlProfilesMap got for mdns  : ",KnGDPRTemplate.mdnList(subscriberAddlProfilesMap.keySet()));
				for (String mdn : listOfMdns) {
					knLogger.debug(methodName, "subsAddlInfoDTO got   : ", subscriberAddlProfilesMap.keySet().contains(mdn) , "for mdn ",KnGDPRTemplate.mdn(mdn));
					if (subscriberAddlProfilesMap.keySet().contains(mdn)) {
						updateMdnList.add(mdn);
					} else {
						createMdnList.add(mdn);
					}
				}
				KnSubsAddlInfoPersistDTO subsAddlProfilePersistDTO = new KnSubsAddlInfoPersistDTO();

				subsAddlProfilePersistDTO.setTierPkgCode(tierPackageId);
				subsAddlProfilePersistDTO.setDataPkgId(dataPkgId);
				if (!updateMdnList.isEmpty()) {
					knLogger.debug(methodName, "updateMdnList  : ", KnGDPRTemplate.mdnList(updateMdnList));
					subsAddlProfilePersistDTO.setMdns(updateMdnList);
					provXDMServerDAO.updateBulkSubscrPkgAddlInfo(subsAddlProfilePersistDTO, persisterTxn);
				}

				if (!createMdnList.isEmpty()) {
					knLogger.debug(methodName, "createMdnList  : ", KnGDPRTemplate.mdnList(createMdnList));
					subsAddlProfilePersistDTO.setMdns(createMdnList);
					subsAddlProfilePersistDTO.setTimeSlotType(KnConstants.TIME_SLOT_TYPE);
					provXDMServerDAO.createBulkSubscrPkgAddlInfo(subsAddlProfilePersistDTO, persisterTxn);
				}

			}
            
            IXDMServerDAO commonXDMServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);

            //retrieve the previous etag of dir doc
            List<Integer> previousEtags = commonXDMServerDAO.getCurrentEtagsForDirDoc(listOfMdns, persisterTxn);
            knLogger.debug(methodName, "previousEtags : ", previousEtags);

            //update the dir doc etag
            commonXDMServerDAO.updateEtagForDirDoc(listOfMdns, persisterTxn);
            knLogger.debug(methodName, "Successfully updated the xdm directory");

            //populating the Subs Config document change DTO
            //populating the Subscriber config doc DTO
            respDTO.setResponseMessage(KnProvConstants.UPDATE_DISP_SUBS_PROFILE_SUCCESS);
            respDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            respDTO.setEtag(lastProfileUpdateTime);
            knLogger.debug(methodName, "Message and response date and time set",respDTO);
            KnOPDocChgDTO docChgDTO;
            int i = 0;

            KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            knLogger.debug(methodName, "KnXDMSServiceConfigDTO xdmsServiceConfigDTO : ",xdmsServiceConfigDTO);


            for (KnOPSubsDispatcherDTO existingSubsProfileDTO : getListOfSubsPersistDTOs) {
                String mdn = existingSubsProfileDTO.getMdn();
                docChgDTO = new KnOPDocChgDTO();
                docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                String subsConfigDocUri = provInfoUtil.generateSubsConfigSelUri(mdn);
                docChgDTO.setDocUri(subsConfigDocUri);
                docChgDTO.setNewEtag(String.valueOf(lastProfileUpdateTime));
                Collection<KnOPDocChgDTO> chgDocList = new ArrayList<KnOPDocChgDTO>();
                chgDocList.add(docChgDTO);

                opDispatchDirChgDTO = new KnOPDispatchDirChgDTO();
                opDispatchDirChgDTO.setActiveFSChanged(mapOfActiveFsChanges.get(mdn));
                opDispatchDirChgDTO.setXcapRootURI(genInfoUtil.getXCAPRootURI(mdn, persisterTxn));
                opDispatchDirChgDTO.setPocHome(existingSubsProfileDTO.getPoCHome());
                opDispatchDirChgDTO.setPresenceHome(existingSubsProfileDTO.getPresenceHome());
                opDispatchDirChgDTO.setDocChgDTO(chgDocList);
                String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                opDispatchDirChgDTO.setDirUri(dirDocUri);
                opDispatchDirChgDTO.setDirPrevEtag(String.valueOf(previousEtags.get(i)));
                int newEtag = previousEtags.get(i) + 1;
                opDispatchDirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                opDispatchDirChgDTO.setProtoVersion(existingSubsProfileDTO.getClientPVmajorVer() + "." + existingSubsProfileDTO.getClientPVminorVer());
                opDispatchDirChgDTO.setActiveFS2(activeFS2);
                opDispatchDirChgDTO.setOldActiveFS(existingSubsProfileDTO.getActiveFS2());
                opDispatchDirChgDTO.setLastProfileUpdateTime(existingSubsProfileDTO.getLastProfileUpdateTime());
                i++;

                mdnDispatchDirChgMap.put(mdn, opDispatchDirChgDTO);
            }
            knLogger.debug(methodName, "KnOPDispatchDirChgDTO mdnDispatchDirChgMap :",KnGDPRTemplate.mapKeyMdn(mdnDispatchDirChgMap));
            respDTO.setMdnDispatcherChgDTOMap(mdnDispatchDirChgMap);
            knLogger.debug(methodName, "Setting the DTOS which are supposed to be changed :", respDTO);

        } catch (KnFeatureSetException ex) {
            knLogger.error(methodName, "Feature Set Exception occured :", ex);
            throw new KnProvBOException(ex.getErrorCode(), ex.getMessage(), ex);


        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occurred : ", e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "DAO exception occurred : ", e);
        } catch (KnProvBOException e) {
            knLogger.error(methodName, "BO Exception occurred :", e);
            knLogger.error(methodName, e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occurred while Update for Feature bit Subscriber");
            knLogger.error(methodName, e);

            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while update forSubscriber", e);
        }
        knLogger.debug(methodName, "EXIT:updated feature bit for subscriber with respDTO :", respDTO);
        return respDTO;
    }

    /**
     * This  method validate if the request feature bit is avalable in the default
     * feature bit configured in
     * KnConstants
     * @param inputMap
     * @return
     */

    private static boolean isProvFSBitAllowed(Map<Integer, Integer> inputMap){
        String methodName="isProvFSBitAllowed(Map<String, String>";
        knLogger.debug("Entered ", methodName, " inPutMap :", inputMap);
        boolean isProvFSBitValid= true;

        List<com.kodiak.common.resources.KnConstants.PROV_FS_BIT> declaredProvFSList = Arrays.asList(com.kodiak.common.resources.KnConstants.PROV_FS_BIT.values());


        for(Map.Entry<Integer,Integer> reqProvEntry:inputMap.entrySet()){
            for(com.kodiak.common.resources.KnConstants.PROV_FS_BIT declaredProvFS: declaredProvFSList)
            {
                if(!reqProvEntry.getKey().equals(declaredProvFS.value())){
                    isProvFSBitValid= false;
                    break;
                }
            }
        }
        knLogger.debug( methodName, " If correct isProvFSBitValid should be true :", isProvFSBitValid);
        return isProvFSBitValid;
    }
    
    private int findInterOPBitStatus(KnOPSubsDispatcherDTO existingSubsProfileDTO,	Map<String, Integer> existingPkgMap, KnPersisterTxn persisterTxn)
			throws KnFeatureSetException, KnBOException {
		String methodName = "findInterOPBitStatus(KnOPSubsProfileInfoDTO , long ,Map<String,Integer> )";
		knLogger.info(methodName, "Entry ", existingSubsProfileDTO, "existing map ", existingPkgMap,
				"existing subscriberfs ", existingSubsProfileDTO.getSubsFS2());
		BitSet existingFSBitSet = featureSetUtil.convertHexStringToBitSet(existingSubsProfileDTO.getSubsFS2());
		knLogger.debug(methodName, "existing subsFsBitSet - ", existingFSBitSet.toString());

		Map<String, String> paramNameValueMap = genInfoUtil.retrieveRTXConfigValues(persisterTxn);
		String basePkgCode = paramNameValueMap.get(KnConstants.BASE_PKGCODE);
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
}

