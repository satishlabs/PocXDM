package com.kodiak.xdms.server.corpmgmt.business.impl;

import com.kodiak.common.commdto.common.KnPTTSettingDocInfoDTO;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.commdto.common.KnXDMPTTSettingHierarchyListDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGeneralCacheUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.corpmgmt.business.ICorpPTTSettingController;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.*;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpPTTSettingDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpPTTSettingDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpPTTSettingDocRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpPTTSettingPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpPTTSettingsUtil;

import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.common.resources.KnProfileTypes.CORP_PROFILE;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.populate;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.*;

public class KnCorpPTTSettingController implements ICorpPTTSettingController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpPTTSettingController.class);

    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnValidatorFramework validatorFW;
    private KnCorpPTTSettingsUtil pttSettingsUtil;
    private KnGeneralCacheUtil generalCacheUtil;
    private KnGenInfoUtil genInfoUtil;
    private KnFeatureSetUtil featureSetUtil;
    private ICorpClientIntf corpClientIntf;

    public KnCorpPTTSettingController(){
        commonInfoUtil = new KnCorpCommonInfoUtil();
        validatorFW = KnValidatorFramework.getInstance(LIBRARY_NAME_CORP_MGMT);
        pttSettingsUtil =new KnCorpPTTSettingsUtil();
        generalCacheUtil = KnGeneralCacheUtil.getInstance();
        genInfoUtil = KnGenInfoUtil.getInstance();
        featureSetUtil = KnFeatureSetUtil.getInstance();
        corpClientIntf = new KnCorpClientImpl();
    }
    @Override
    public KnCorpResponseDTO createPTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "createPTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpPTTSettingPersistDTO corpPTTSettingPersistDTO = new KnCorpPTTSettingPersistDTO();
        corpPTTSettingPersistDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        corpPTTSettingPersistDTO.setOperationType(KnOperationTypes.CREATE_CORP_PTTSETTING_DOC);
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipCorpPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {
            String uuid = commonInfoUtil.getUUID();
            ipCorpPTTSettingDTO.getPttSettingInfo().set_id(uuid);
            String reqPTTSettingName = ipCorpPTTSettingDTO.getPttSettingInfo().getTemplateName();
            knLogger.debug(methodName, "template name - ", reqPTTSettingName);
            //check if template name already exists to maintain template name unique
            String dbPTTSettingName  = pttSettingsUtil.getPTTSettingDocName(reqPTTSettingName, persisterTxn);
            corpPTTSettingPersistDTO.setDbPTTSettingName(dbPTTSettingName);
            corpPTTSettingPersistDTO.setReqPTTSettingName(reqPTTSettingName);
            knLogger.debug(methodName, "Before Validation", corpPTTSettingPersistDTO);
            validatorFW.validate(corpPTTSettingPersistDTO);
            knLogger.info(methodName, "Validation Successfull");

            pttSettingsUtil.createPTTSettingDoc(uuid, ipCorpPTTSettingDTO.getPttSettingInfo(), persisterTxn);
            respDTO.setTemplateId(uuid);
            populate(respDTO);
        } catch (KnValidationException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnValidationException occurred", e);
        } catch (
                KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occurred", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occurred ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO getAllPTTSettingDocList(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getAllPTTSettingDocList(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn)";

        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        knLogger.debug(methodName, "--->ipPTTSettingDTO - ", ipCorpPTTSettingDTO);
        String xdmsHome;
        KnCorpProfileDTO corpProfile=null;
        try {
            if( null != ipCorpPTTSettingDTO.getCorpId()){
                corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipCorpPTTSettingDTO.getCorpId()),
                        CORP_PROFILE, false, persisterTxn);
                knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
                xdmsHome = corpProfile.getXdmsHome();

            }else{
                xdmsHome = genInfoUtil.retrieveLocalXDMPttServerId();
            }
            String systemDefaultPttSettingDoc = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(SYSTEM_DEFAULT_PTTSETTINGDOCID);
            List<KnPTTSettingDocInfoDTO> pttDocList = new ArrayList<>();
            if(ipCorpPTTSettingDTO.getCorpId() == null && ipCorpPTTSettingDTO.getHierarchyId() == null){
                pttDocList = pttSettingsUtil.getAllPTTSettingDocs(PTT_TEMPLATE_DOC_TYPE, persisterTxn);
            }else {
                Set<KnCorpPTTSettingDocRespDTO> pttSettingDocRespDTOSet = pttSettingsUtil.getPTTSettingDocIds(Integer.parseInt(ipCorpPTTSettingDTO.getCorpId()), ipCorpPTTSettingDTO.getHierarchyId() ,xdmsHome, persisterTxn);
                if (pttSettingDocRespDTOSet != null && !pttSettingDocRespDTOSet.isEmpty()) {
                    pttDocList = pttSettingsUtil.getAllPTTSettingDocList(pttSettingDocRespDTOSet, corpProfile.getCorpId(), xdmsHome, persisterTxn);
                }
                if (systemDefaultPttSettingDoc != null && !systemDefaultPttSettingDoc.trim().isEmpty()) {
                    KnPTTSettingDocInfoDTO sysDefaultDoc = pttSettingsUtil.getSystemDefaultTemplateDetails(
                            systemDefaultPttSettingDoc, xdmsHome, persisterTxn);
                    if (sysDefaultDoc != null && sysDefaultDoc.getDocId() != null) {
                        pttDocList.add(sysDefaultDoc);
                    }
                }
            }
            knLogger.debug(methodName, "--->pttDocList - ", pttDocList);
            respDTO.setPttSettingDocList(pttDocList);
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpPTTSettingDTO getPTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO,
                                                             KnPersisterTxn persisterTxn) {
        String methodName = "getPTTSettingDoc(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpPTTSettingDTO respDTO = new KnCorpPTTSettingDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        knLogger.debug(methodName, "--->ipPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {

            respDTO = pttSettingsUtil.getPTTSettingDoc(ipCorpPTTSettingDTO.getPttSettingId(), persisterTxn);
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO setDefaultPttSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "setDefaultPttSettingDoc(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpPTTSettingDTO respDTO = new KnCorpPTTSettingDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        knLogger.debug(methodName, "--->ipPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipCorpPTTSettingDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //
            String sysDefaultPttSettingDoc = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(SYSTEM_DEFAULT_PTTSETTINGDOCID);
            if(ipCorpPTTSettingDTO.getPttSettingId() != null && ipCorpPTTSettingDTO.getPttSettingId().equals(sysDefaultPttSettingDoc)){
                knLogger.info(methodName, "Trying to set System default PTT Setting Doc as default. Skipping update. docId - ", ipCorpPTTSettingDTO.getPttSettingId());
                pttSettingsUtil.resetDefaultSettingDoc(corpProfile.getCorpId(),ipCorpPTTSettingDTO.getHierarchyId(), persisterTxn);
                return populate(respDTO);
            }
            // Check if already set as default
            boolean isDefaultSet = pttSettingsUtil.isPttSettingDocAlreadyDefault(ipCorpPTTSettingDTO.getPttSettingId(),corpProfile.getCorpId(),ipCorpPTTSettingDTO.getHierarchyId(), persisterTxn);
            if (isDefaultSet) {
                knLogger.info(methodName, "PTT Setting Doc is already set as default. Skipping update. docId - ", ipCorpPTTSettingDTO.getPttSettingId());
                throw new KnCorpBOException(PTT_TEMPLATE_ALREADY_SET_AS_DEFAULT, "PTT Template is already assigned to corp");
            }
            knLogger.debug(methodName, "PTT Setting Doc is not default. Proceeding with update.");
            pttSettingsUtil.setDefaultPttSettingDoc(ipCorpPTTSettingDTO.getPttSettingId(),corpProfile.getCorpId(),ipCorpPTTSettingDTO.getHierarchyId(), xdmsHome, persisterTxn);

            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO deletePTTSettingDoc(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "deletePTTSettingDoc(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpPTTSettingDTO respDTO = new KnCorpPTTSettingDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        knLogger.debug(methodName, "--->ipPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {

            if(null!=ipCorpPTTSettingDTO.getPttSettingId()) {
                if (pttSettingsUtil.validatePttSettingCorpMapping(ipCorpPTTSettingDTO.getPttSettingId(),persisterTxn)) {
                    knLogger.error(methodName, "PTT Template deletion not allowed : ", ipCorpPTTSettingDTO.getPttSettingId());
                    throw new KnCorpBOException(DELETE_PTT_TEMPLATE_NOT_ALLOWED, "PTT Template deletion not allowed");
                }
            }
            pttSettingsUtil.deletePTTSettingDocFromCB(ipCorpPTTSettingDTO.getPttSettingId(), persisterTxn);
            knLogger.debug(methodName, "Deleted the pttTemplate from CB", ipCorpPTTSettingDTO.getPttSettingId());
            respDTO.setTemplateId(ipCorpPTTSettingDTO.getPttSettingId());
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO assignPttSettingToHierarchy(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "assignPttSettingToHierarchy(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpPTTSettingDTO respDTO = new KnCorpPTTSettingDTO();
        KnCorpPTTSettingPersistDTO corpPTTSettingPersistDTO = new KnCorpPTTSettingPersistDTO();
        corpPTTSettingPersistDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        corpPTTSettingPersistDTO.setOperationType(KnOperationTypes.ASSIGN_PTTSETTING_DOC_TO_HIERARCHY);
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        knLogger.debug(methodName, "--->ipPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipCorpPTTSettingDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            String sysDefaultPttSettingDoc = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn).get(SYSTEM_DEFAULT_PTTSETTINGDOCID);
            List<KnXDMPTTSettingHierarchyListDTO> hierarchyListDTO = ipCorpPTTSettingDTO.getPttSettingDocList();
            List<String> docList= hierarchyListDTO.stream().flatMap(dto -> dto.getPttSettingDocList().stream())
                    .collect(Collectors.toList());
            corpPTTSettingPersistDTO.setReqPTTdocIds(docList);
            List<KnPTTSettingDocInfoDTO> corpPTTSettingDTOS = pttSettingsUtil.getAllPTTSettingDocs(PTT_TEMPLATE_DOC_TYPE, persisterTxn);
            corpPTTSettingPersistDTO.setPttSettingDTOList(corpPTTSettingDTOS);
            knLogger.debug(methodName, "Before Validation", corpPTTSettingPersistDTO);
            validatorFW.validate(corpPTTSettingPersistDTO);
            knLogger.info(methodName, "Validation Successfull");
            pttSettingsUtil.assignPttSettingToHierarchy(ipCorpPTTSettingDTO.getPttSettingDocList(), corpProfile.getCorpId(),sysDefaultPttSettingDoc, xdmsHome, persisterTxn);
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO unassignPttSettingToHierarchy(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "unassignPttSettingToHierarchy(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpPTTSettingDTO respDTO = new KnCorpPTTSettingDTO();
        KnCorpPTTSettingPersistDTO corpPTTSettingPersistDTO = new KnCorpPTTSettingPersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        corpPTTSettingPersistDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        corpPTTSettingPersistDTO.setOperationType(KnOperationTypes.UNASSIGN_PTTSETTING_DOC_TO_HIERARCHY);
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        knLogger.debug(methodName, "--->ipPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipCorpPTTSettingDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            List<KnXDMPTTSettingHierarchyListDTO> hierarchyListDTO = ipCorpPTTSettingDTO.getPttSettingDocList();
            List<String> docList= hierarchyListDTO.stream().flatMap(dto -> dto.getPttSettingDocList().stream())
                    .collect(Collectors.toList());
            corpPTTSettingPersistDTO.setReqPTTdocIds(docList);
            List<KnPTTSettingDocInfoDTO> corpPTTSettingDTOS = pttSettingsUtil.getAllPTTSettingDocs(PTT_TEMPLATE_DOC_TYPE, persisterTxn);
            corpPTTSettingPersistDTO.setPttSettingDTOList(corpPTTSettingDTOS);
            knLogger.debug(methodName, "Before Validation", corpPTTSettingPersistDTO);
            validatorFW.validate(corpPTTSettingPersistDTO);
            knLogger.info(methodName, "Validation Successfull");
            pttSettingsUtil.unassignPttSettingToHierarchy(ipCorpPTTSettingDTO.getPttSettingDocList(), corpProfile.getCorpId(), xdmsHome, persisterTxn);
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occurred", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occurred ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO assignPttSettingDocToMdns(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "assignPttSettingDocToMdns(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn)";
        KnCorpPTTSettingDTO respDTO = new KnCorpPTTSettingDTO();
        KnCorpPTTSettingPersistDTO corpPTTSettingPersistDTO = new KnCorpPTTSettingPersistDTO();
        corpPTTSettingPersistDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        corpPTTSettingPersistDTO.setOperationType(KnOperationTypes.ASSIGN_PTTSETTING_DOC_TO_MDNLIST);
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        knLogger.debug(methodName, "--->ipCorpPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipCorpPTTSettingDTO.getCorpId()), CORP_PROFILE, false,
                    persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            for(String mdn: ipCorpPTTSettingDTO.getMdnList()) {
                KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                        false, persisterTxn);
                if (null != ipCorpPTTSettingDTO.getCorpId() && Integer.parseInt(ipCorpPTTSettingDTO.getCorpId()) > 0 && subscProfile.getCorpId() != Integer.parseInt(ipCorpPTTSettingDTO.getCorpId())) {
                    throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                            "Subscribers Does not belong to the corporation.");
                }
            }
            //REVIEW IF REQUIRED------------->
            // TODO check the doctype
            List<KnPTTSettingDocInfoDTO> corpPTTSettingDTOS = pttSettingsUtil.getAllPTTSettingDocs("STC", persisterTxn);
            corpPTTSettingPersistDTO.setPttSettingDTOList(corpPTTSettingDTOS);
            corpPTTSettingPersistDTO.setReqPTTdocIds(Collections.singletonList(ipCorpPTTSettingDTO.getPttSettingId()));
            knLogger.debug(methodName, "Before Validation", corpPTTSettingPersistDTO);
            validatorFW.validate(corpPTTSettingPersistDTO);
            knLogger.info(methodName, "Validation Successfull");

            pttSettingsUtil.assignPttSettingDocToMdns(ipCorpPTTSettingDTO.getMdnList(), ipCorpPTTSettingDTO.getPttSettingId(),
                    corpProfile.getCorpId(), ipCorpPTTSettingDTO.getHierarchyId(), xdmsHome, persisterTxn);
            pttSettingsUtil.updateLastProfileUpdateTimeForMdns(ipCorpPTTSettingDTO.getMdnList(), xdmsHome, persisterTxn);

            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO unassignPttSettingDocToMdns(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "unassignPttSettingDocToMdns(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn)";
        KnCorpPTTSettingDTO respDTO = new KnCorpPTTSettingDTO();
        KnCorpPTTSettingPersistDTO corpPTTSettingPersistDTO = new KnCorpPTTSettingPersistDTO();
        corpPTTSettingPersistDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        corpPTTSettingPersistDTO.setOperationType(KnOperationTypes.UNASSIGN_PTTSETTING_DOC_TO_MDNLIST);
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        knLogger.debug(methodName, "--->ipCorpPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipCorpPTTSettingDTO.getCorpId()), CORP_PROFILE, false,
                    persisterTxn);
            knLogger.debug(methodName, "Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            for(String mdn: ipCorpPTTSettingDTO.getMdnList()) {
                KnSubsProfileDTO subscProfile = commonInfoUtil.getProfileDetails(mdn, KnProfileTypes.PUBLIC_PROFILE,
                        false, persisterTxn);
                if (null != ipCorpPTTSettingDTO.getCorpId() && Integer.parseInt(ipCorpPTTSettingDTO.getCorpId()) > 0 && subscProfile.getCorpId() != Integer.parseInt(ipCorpPTTSettingDTO.getCorpId())) {
                    throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                            "Subscribers Does not belong to the corporation.");
                }
            }
            //REVIEW IF REQUIRED------------->
            // TODO check the doctype
            List<KnPTTSettingDocInfoDTO> corpPTTSettingDTOS = pttSettingsUtil.getAllPTTSettingDocs("STC", persisterTxn);
            corpPTTSettingPersistDTO.setPttSettingDTOList(corpPTTSettingDTOS);
            corpPTTSettingPersistDTO.setReqPTTdocIds(Collections.singletonList(ipCorpPTTSettingDTO.getPttSettingId()));
            knLogger.debug(methodName, "Before Validation", corpPTTSettingPersistDTO);
            validatorFW.validate(corpPTTSettingPersistDTO);
            knLogger.info(methodName, "Validation Successfull");

            pttSettingsUtil.unassignPttSettingDocToMdns(ipCorpPTTSettingDTO.getMdnList(), corpProfile.getCorpId(),
                    ipCorpPTTSettingDTO.getHierarchyId(), xdmsHome, persisterTxn);
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO getPttSettingDocMdnList(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getPttSettingDocMdnList(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn)";
        KnCorpPTTSettingDTO respDTO = new KnCorpPTTSettingDTO();
        KnCorpPTTSettingPersistDTO corpPTTSettingPersistDTO = new KnCorpPTTSettingPersistDTO();
        corpPTTSettingPersistDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        corpPTTSettingPersistDTO.setOperationType(KnOperationTypes.GET_MDN_LIST_FOR_PTT_SETTING_DOC);
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        knLogger.debug(methodName, "--->ipCorpPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipCorpPTTSettingDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            //REVIEW IF REQUIRED
            // TODO check the doctype
            List<KnPTTSettingDocInfoDTO> corpPTTSettingDTOS = pttSettingsUtil.getAllPTTSettingDocs("STC", persisterTxn);
            corpPTTSettingPersistDTO.setPttSettingDTOList(corpPTTSettingDTOS);
            corpPTTSettingPersistDTO.setReqPTTdocIds(Collections.singletonList(ipCorpPTTSettingDTO.getPttSettingId()));
            knLogger.debug(methodName, "Before Validation", corpPTTSettingPersistDTO);
            validatorFW.validate(corpPTTSettingPersistDTO);
            knLogger.info(methodName, "Validation Successfull");
            List<KnXDMMdnInfoDTO> pttAssignedMdnList = pttSettingsUtil.getPttSettingDocMdnList(ipCorpPTTSettingDTO.getPttSettingId(), ipCorpPTTSettingDTO.getHierarchyId(),
                    corpProfile.getCorpId(), xdmsHome, persisterTxn);
            respDTO.setPttAssignedMdnList(pttAssignedMdnList);
            respDTO.setExtCorpId(corpProfile.getExtCorpId());
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO getMDNCountForPttSettingDocID(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "getMDNCountForPttSettingDocID(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn)";
        KnCorpPTTSettingDTO respDTO = new KnCorpPTTSettingDTO();
        KnCorpPTTSettingPersistDTO corpPTTSettingPersistDTO = new KnCorpPTTSettingPersistDTO();
        corpPTTSettingPersistDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        corpPTTSettingPersistDTO.setOperationType(KnOperationTypes.GET_MDN_COUNT_FOR_PTT_SETTINGID);
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        knLogger.debug(methodName, "--->ipCorpPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipCorpPTTSettingDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            List<KnPTTSettingDocInfoDTO> corpPTTSettingDTOS = pttSettingsUtil.getAllPTTSettingDocs("STC", persisterTxn);
            corpPTTSettingPersistDTO.setPttSettingDTOList(corpPTTSettingDTOS);
            corpPTTSettingPersistDTO.setReqPTTdocIds(Collections.singletonList(ipCorpPTTSettingDTO.getPttSettingId()));
            knLogger.debug(methodName, "Before Validation", corpPTTSettingPersistDTO);
            validatorFW.validate(corpPTTSettingPersistDTO);
            knLogger.info(methodName, "Validation Successfull");
            int mdnCount = pttSettingsUtil.getMDNCountForPttSettingDocID(ipCorpPTTSettingDTO.getPttSettingId(), corpProfile.getCorpId(), xdmsHome, persisterTxn);
            respDTO.setMdnCount(mdnCount);
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }
    @Override
    public KnCorpResponseDTO assignPttSettingToCorp(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "assignPttSettingToCorp(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpPTTSettingDTO respDTO = new KnCorpPTTSettingDTO();
        KnCorpPTTSettingPersistDTO corpPTTSettingPersistDTO = new KnCorpPTTSettingPersistDTO();
        corpPTTSettingPersistDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        corpPTTSettingPersistDTO.setOperationType(KnOperationTypes.ASSIGN_PTTSETTING_DOC_TO_CORP);
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        knLogger.debug(methodName, "--->ipPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipCorpPTTSettingDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            // TODO add validation if requires
            Set<KnCorpPTTSettingDocRespDTO> pttSettingDocIds = pttSettingsUtil.getPTTSettingDocIds(Integer.parseInt(ipCorpPTTSettingDTO.getCorpId()), ipCorpPTTSettingDTO.getCorpId() ,xdmsHome, persisterTxn);
            String pttSettingId = ipCorpPTTSettingDTO.getPttSettingId();
            // Check- PTT Setting ID assigned to corp
            if (pttSettingDocIds != null && pttSettingDocIds.stream().map(KnCorpPTTSettingDocRespDTO::getPttSettingId).anyMatch(pttSettingId::contains)) {
                knLogger.error(methodName, "PTT Template is already assigned to corp:", ipCorpPTTSettingDTO.getPttSettingId());
                throw new KnCorpBOException(PTT_TEMPLATE_ALREADY_ASSIGNED_TO_CORP, "PTT Template is already assigned to corp");
            }
            pttSettingsUtil.assignPttSettingToCorp(pttSettingId, corpProfile.getCorpId(), xdmsHome, persisterTxn);
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occured", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occured ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO unassignPttSettingToCorp(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "unassignPttSettingToCorp(KnIPUserProfileDTO ipUserProfileDTO, KnPersisterTxn persisterTxn)";

        KnCorpPTTSettingDTO respDTO = new KnCorpPTTSettingDTO();
        KnCorpPTTSettingPersistDTO corpPTTSettingPersistDTO = new KnCorpPTTSettingPersistDTO();
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        knLogger.debug(methodName, "--->clusterId - ", clusterId);
        knLogger.debug(methodName, "--->ipPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {
            KnCorpProfileDTO corpProfile = commonInfoUtil.getProfileDetails(String.valueOf(ipCorpPTTSettingDTO.getCorpId()),
                    CORP_PROFILE, false, persisterTxn);
            knLogger.debug(methodName, "--->Retrieved Corp Profile details - ", corpProfile);
            String xdmsHome = corpProfile.getXdmsHome();
            // TODO add validation if requires

            pttSettingsUtil.unassignPttSettingToCorp(ipCorpPTTSettingDTO.getPttSettingId(), corpProfile.getCorpId(), xdmsHome, persisterTxn);
            populate(respDTO);
            knLogger.debug(methodName, "--->respDTO - ", respDTO);
        }catch (KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occurred", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occurred ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }

    @Override
    public KnCorpResponseDTO modifyPTTSettingTemplate(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn) {
        String methodName = "modifyPTTSettingTemplate(KnIPCorpPTTSettingDTO ipCorpPTTSettingDTO, KnPersisterTxn persisterTxn)";
        KnCorpResponseDTO respDTO = new KnCorpResponseDTO();
        KnCorpPTTSettingPersistDTO corpPTTSettingPersistDTO = new KnCorpPTTSettingPersistDTO();
        corpPTTSettingPersistDTO.setEntityId(KnEntityTypes.CORP_PTT_SETTING_MANAGER);
        corpPTTSettingPersistDTO.setOperationType(KnOperationTypes.CREATE_CORP_PTTSETTING_DOC);
        final int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
        final KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();

        knLogger.debug(methodName, "ipCorpPTTSettingDTO - ", ipCorpPTTSettingDTO);
        try {
            //String uuid = commonInfoUtil.getUUID();
            //ipCorpPTTSettingDTO.getPttSettingInfo().set_id(uuid);
            String reqPTTSettingName = ipCorpPTTSettingDTO.getPttSettingInfo().getTemplateName();
            knLogger.debug(methodName, "template name - ", reqPTTSettingName);
            String dbPTTSettingName  = pttSettingsUtil.getPTTSettingDocName(reqPTTSettingName, persisterTxn);
            corpPTTSettingPersistDTO.setDbPTTSettingName(dbPTTSettingName);
            corpPTTSettingPersistDTO.setReqPTTSettingName(reqPTTSettingName);
            if (reqPTTSettingName.equals(dbPTTSettingName)) {
                pttSettingsUtil.modifyPTTSettingTemplate(ipCorpPTTSettingDTO.getPttSettingId(), ipCorpPTTSettingDTO.getPttSettingInfo(), persisterTxn);
            }
            respDTO.setTemplateId(ipCorpPTTSettingDTO.getPttSettingId());
            populate(respDTO);
        } catch (
                KnCorpBOException e) {
            populate(respDTO, e);
            knLogger.error(methodName, "KnCorpBOException occurred", e);
        } catch (Exception e) {
            populate(respDTO, e);
            knLogger.error(methodName, "Unexpected exception occurred ", new KnException(
                    com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "Success response", respDTO);
        return respDTO;
    }
}
