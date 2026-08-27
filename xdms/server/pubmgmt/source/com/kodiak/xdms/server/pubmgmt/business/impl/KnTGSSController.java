/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.impl;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnSystemException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.xmlmodifier.impl.KnXMLProcessor;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.aas.KnAASException;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.pubmgmt.business.ITGSSController;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.dao.KnPubFactorySelector;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPTGSSListDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOpPubResponse;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnTGSSDocDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnTGSSListPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.*;

import static com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes.BOEntity.GROUP_ALREADY_EXISTS;
import static com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes.BOEntity.GROUP_DOES_NOT_EXISTS;
import static com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes.BOEntity.TGSS_DOC_NOT_EXISTS;


/**
 * Created by venkata sudhakar talluri on 28-12-2018
 */
public class KnTGSSController implements ITGSSController {
    private static final KnLogger knLogger = KnLogger.getLogger(KnTGSSController.class);
    private KnValidatorFramework validatorFwk = null;
    private KnAASFramework authorizationFwk = null;
    private KnPubInfoUtil pubInfoUtil = new KnPubInfoUtil();
    private static KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    private static KnXMLProcessor xmlProcessor = KnXMLProcessor.getInstance();

    public KnTGSSController() {
        validatorFwk = KnValidatorFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        authorizationFwk = KnAASFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
    }

    /**
     * @param tgssListDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */

    public KnIPTGSSListDTO getTGSSList(KnIPTGSSListDTO tgssListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getTGSSList(KnIPTGSSListDTO, persisterTxn)";
        KnIPTGSSListDTO respDto = new KnIPTGSSListDTO();
        List<Integer> groupIds = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + tgssListDTO);
        KnTGSSListPersistDTO tgssListPersistDTO = null;
        KnTGSSDocDTO tgssDocDTO = null;
        try {
            /*
                get subscriber profile
                        -authorize
                        -getTGSSDoc
                validate not DOC_NOT_MODIFIED
                        -validateFW(FSbit chk)
                        -getSSGroupIds
                        -set docEtag,and corpid and list of groupids in response
                        */
            knLogger.info(methodName, "retriving Talk Group Simultainous Session document");
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(tgssListDTO.getMdn(), tgssListDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(tgssListDTO.getMdn());
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setInputDTO(tgssListDTO);
            tgssListPersistDTO = new KnTGSSListPersistDTO();
            tgssListPersistDTO.setInputDTO(tgssListDTO);
            tgssListPersistDTO.setPersistenceDTO(originator);
            tgssListPersistDTO.setCorpId(subsProfile.getCorpId());
            tgssListPersistDTO.setFeatureSet2(subsProfile.getActiveFS2());

            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(tgssListPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");
            String xdmServerId = subsProfile.getXdmsHome();
            int indexDocEtag = tgssListDTO.getIfNoneMatch();
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            tgssDocDTO = xdmServerDAO.getTGSSDoc(tgssListDTO.getMdn(), true, persisterTxn);
            if (indexDocEtag > 0 && indexDocEtag == tgssDocDTO.getEtag()) {
                knLogger.info(methodName, "tgss Document not modifed :", tgssDocDTO.getEtag());
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_NOT_MODIFIED, "tgss Document not modifed");
            }
            groupIds = xdmServerDAO.getSSGroupIds(tgssDocDTO.getMdn(), true, persisterTxn);
            Map<Integer, Integer> groupIdCorpIdInfo = xdmServerDAO.getSsGroupIdsCorpInfo(tgssListDTO.getMdn(), true, persisterTxn);
            knLogger.debug(methodName, "validationg dto ", tgssListPersistDTO);
            validatorFwk.validate(tgssListPersistDTO);
            knLogger.debug(methodName, "Validated successfully!");

            respDto.setDocEtag(String.valueOf(tgssDocDTO.getEtag()));
            respDto.setGroupIds(groupIds);
            respDto.setGroupIdCorpInfo(groupIdCorpIdInfo);
            respDto.setMdn(tgssDocDTO.getMdn());
            respDto.setCorpId(subsProfile.getCorpId());
            knLogger.debug(methodName, "Returning response :", respDto);
        } catch (KnAASException aex) {
            knLogger.error(methodName, "Authorization Exception occurred :" + aex);
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occurred : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.TGSS_DOC_NOT_EXISTS, "No SS Channel Group Info Found", ex);
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occurred :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while while retrieving Talk Group Simultaneous Session doc info: " + ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occurred while retrieving groupId info ", ex);
        }

        return respDto;
    }

    public KnOpPubResponse updateTGSSList(KnIPTGSSListDTO tgssListDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
  /*          - get subscriber profile
            -authorize
                    -getTGSSDoc
            validate not DOC_MODIFIED
                    -validateFW((for Fsbit check, corporateId chk , groupIs limit chk)
            -getSSGroupIds
            chk if reques group id not exists in DB
		else addSSChannelGrpInfo
                    -updateEtagTGSSDoc
                    -getCurrentEtagsForDirDoc
                    -updateEtagForDirDoc
            construct xcap-diff notification for TGSSListSelUri and set in response
                    -set docEtag

    */

        String methodName = "updateTGSSList(KnIPTGSSListDTO, persisterTxn)";
        KnOpPubResponse response = new KnOpPubResponse();
        List<Integer> groupIds = null;
        List<Integer> inputGrpIds = tgssListDTO.getGroupIds();
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + tgssListDTO);
        KnTGSSListPersistDTO tgssListPersistDTO = null;
        String mdn = tgssListDTO.getMdn();
        long etag = 0;
        int dirDocEtag = 0;
        long newDocEtag = 0;
        int corpGrpSSCnt = 0;
        int ssDdGrpCnt = 0;
        try {
            knLogger.info(methodName, "updating Talk Group Simultaneous Session doc info");
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(mdn, tgssListDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            if (subsProfile.getMaxSDDSession() > 0) {
                ssDdGrpCnt = subsProfile.getMaxSDDSession();
            } else {
                corpGrpSSCnt = pubInfoUtil.getCorpMaxSSDDCount(subsProfile.getCorpId(), persisterTxn);
                if (corpGrpSSCnt > 0) {
                    ssDdGrpCnt = corpGrpSSCnt;
                } else {
                    ssDdGrpCnt = pubInfoUtil.getPocMaxSSDDCount(persisterTxn, subsProfile.getPocHome());
                }
            }
            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(mdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setInputDTO(tgssListDTO);
            tgssListPersistDTO = new KnTGSSListPersistDTO();
            tgssListPersistDTO.setInputDTO(tgssListDTO);
            tgssListPersistDTO.setPersistenceDTO(originator);
            tgssListPersistDTO.setCorpId(subsProfile.getCorpId());
            tgssListPersistDTO.setMdn(mdn);
            tgssListPersistDTO.setFeatureSet2(subsProfile.getActiveFS2());
            tgssListPersistDTO.setMaxSDDSession(ssDdGrpCnt);
            tgssListPersistDTO.setMaxSDYSession(subsProfile.getMaxSDYSession());
            Integer corpGroupId = tgssListDTO.getGroupIds().stream().findFirst().get(); //single groupId will be set from the prev layer
            String xdmServerId = subsProfile.getXdmsHome();
            IXDMServerDAO commonXdmDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmServerId);
            Map<String, Boolean> memberMdns = commonXdmDAO.getGroupMemberList(corpGroupId, persisterTxn);
            tgssListPersistDTO.setMemberList(new ArrayList<>(memberMdns.keySet()));
            if (subsProfile.getCorpId() != tgssListDTO.getCorpId()) {
                Map<Integer, List<Integer>> sharedCorpList = commonXdmDAO.selectGroupSharedCorpId(tgssListDTO.getCorpId(), tgssListDTO.getGroupIds(), persisterTxn);
                tgssListPersistDTO.setSharedCorpList(sharedCorpList.get(corpGroupId));
            }
            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(tgssListPersistDTO);

            knLogger.debug(methodName, "Authorized successfully.");

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            int indexDocEtag = tgssListDTO.getIfNoneMatch();
            KnTGSSDocDTO tgssDocDTO = xdmServerDAO.getTGSSDoc(mdn, persisterTxn);
            if (indexDocEtag > 0 && indexDocEtag != tgssDocDTO.getEtag()) {
                knLogger.info(methodName, "tgss Document  modifed :", tgssDocDTO.getEtag());
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_MODIFIED, "tgss Document modifed");
            }
            groupIds = xdmServerDAO.getSSGroupIds(tgssDocDTO.getMdn(), true, persisterTxn);

            tgssListPersistDTO.setGroupIdList(groupIds);

            knLogger.debug(methodName, "validationg dto ", tgssListPersistDTO);
            validatorFwk.validate(tgssListPersistDTO);
            knLogger.debug(methodName, "Validated successfully!");
            //As of today only one group ID is allowed for one request however wrting logic gernically to re-use down the line
            //cross check if it is fine to pull intputgroupId{0]
            for (Integer groupId : inputGrpIds) {
                if ((groupIds.size() == 0) || !(groupIds.contains(groupId))) {
                    knLogger.debug(methodName, "groupId: " + groupId + "is not avilable for mdn:" + KnGDPRTemplate.mdn(mdn) + " calling addSSChannelGrpInfo method");
                    xdmServerDAO.addSSChannelGrpInfo(mdn, groupId, persisterTxn);
                    knLogger.debug(methodName, "added groupId: " + groupId + "is not avilable for mdn:" + KnGDPRTemplate.mdn(tgssDocDTO.getMdn()) + " to SChannelGrpInfo");
                    dirDocEtag = commonXdmDAO.getCurrentDirEtagForUpdate(mdn, persisterTxn);

                    // update etag of directory
                    commonXdmDAO.updateEtagForDirDoc(mdn, persisterTxn);

                    KnTGSSDocDTO docDTO =  xdmServerDAO.getTGSSDoc(mdn, persisterTxn);
                    etag = docDTO.getEtag();

                    newDocEtag = etag + 1;
                    xdmServerDAO.updateEtagTGSSDoc(mdn, newDocEtag, persisterTxn);

                    String xcapRootUti = genInfoUtil.getXCAPRootURI(mdn,persisterTxn );

                    KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                    KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                    docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    String contactDocUri = pubInfoUtil.generateTGSSListSelUri(mdn, KnConstants.DOC_CHANGE_TYPE.REPLACE);
                    docChgDTO.setDocUri(contactDocUri);
                    docChgDTO.setNewEtag(String.valueOf(etag + 1));
                    Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
                    docChgList.add(docChgDTO);
                    dirChgDTO.setDocChgDTO(docChgList);
                    String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                    dirChgDTO.setDirUri(dirDocUri);
                    dirChgDTO.setDirPrevEtag(String.valueOf(dirDocEtag));
                    int newEtag = dirDocEtag + 1;
                    dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                    dirChgDTO.setPocHome(subsProfile.getPocHome());
                    dirChgDTO.setPresenceHome(subsProfile.getPresenceHome());
                    dirChgDTO.setXcapRootURI(xcapRootUti);

                    response = pubInfoUtil.populateSuccessResponse();
                    //            construct xcap-diff notification for TGSSListSelUri and set in response

                    response.setDirChgDTO(dirChgDTO);
                    response.setDocEtag(String.valueOf(newDocEtag));

                }else {
                    knLogger.error(methodName, "Group Already Exists");
                    throw new KnPubBOException(GROUP_ALREADY_EXISTS, "Group Already Exists");
                }
            }
            knLogger.debug(methodName, "Returning response :", response);
        } catch (KnAASException aex) {
            knLogger.error(methodName, "Authorization Exception occured :" + aex);
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(TGSS_DOC_NOT_EXISTS, "TGSS doc doesnot exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occured :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while while updating tgss doc info: " + ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while retriving groupId info ", ex);
        }

        return response;
    }

    public KnOpPubResponse deleteTGSSList(KnIPTGSSListDTO tgssListDTO, KnPersisterTxn persisterTxn) throws
            KnXDMServerException {
  /*              - get subscriber profile
                -authorize
                        -getTGSSDoc
                validate not DOC_MODIFIED
                        -validateFW (for Fsbit check, corporateId chk )
                -getSSGroupIds
                chk if request group id exists in DB
		else deleteSSChannelGrpInfo
                        -updateEtagTGSSDoc
                        -getCurrentEtagsForDirDoc
                        -updateEtagForDirDoc
                construct xcap-diff notification for TGSSListSelUri and set in response
                        -set docEtag

*/
        String methodName = "deleteTGSSList(KnIPTGSSListDTO, persisterTxn)";
        KnOpPubResponse response = new KnOpPubResponse();
        List<Integer> groupIds = null;
        List<Integer> inputGrpIds = tgssListDTO.getGroupIds();
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + tgssListDTO);
        KnTGSSListPersistDTO tgssListPersistDTO = null;
        String mdn = tgssListDTO.getMdn();
        long etag = 0;
        int dirDocEtag=0;
        long newDocEtag=0;
        try {
            knLogger.info(methodName, "delete Talk Group Simultaneous Session doc info");
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(mdn, tgssListDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(mdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setInputDTO(tgssListDTO);
            tgssListPersistDTO = new KnTGSSListPersistDTO();
            tgssListPersistDTO.setInputDTO(tgssListDTO);
            tgssListPersistDTO.setPersistenceDTO(originator);
            tgssListPersistDTO.setCorpId(tgssListDTO.getCorpId());
            tgssListPersistDTO.setMdn(mdn);
            tgssListPersistDTO.setFeatureSet2(subsProfile.getActiveFS2());
            tgssListPersistDTO.setMaxSDDSession(subsProfile.getMaxSDDSession());
            tgssListPersistDTO.setMaxSDYSession(subsProfile.getMaxSDYSession());


            String xdmServerId = subsProfile.getXdmsHome();
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(tgssListPersistDTO);
            knLogger.debug(methodName, "Authorized successfully.");

            int indexDocEtag = tgssListDTO.getIfNoneMatch();

            KnTGSSDocDTO tgssDocDTO = xdmServerDAO.getTGSSDoc(mdn, persisterTxn);
            IXDMServerDAO commonXdmDAO = KnFactorySelector.getDAOFactory( KnFactorySelector.DB).createXDMServerDAO(xdmServerId);
            if (indexDocEtag > 0 && indexDocEtag != tgssDocDTO.getEtag()) {
                knLogger.info(methodName, "tgss Document  modifed :", tgssDocDTO.getEtag());
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_MODIFIED, "tgss Document  modifed");
            }
            groupIds = xdmServerDAO.getSSGroupIds(tgssDocDTO.getMdn(), false, persisterTxn);

            tgssListPersistDTO.setGroupIdList(groupIds);

            knLogger.debug(methodName, "validationg dto ", tgssListPersistDTO);
            validatorFwk.validate(tgssListPersistDTO);
            knLogger.debug(methodName, "Validated successfully!");

            //As of today only one group ID is allowed for one request however wrting logic gernically to re-use down the line
            //cross check if it is fine to pull intputgroupId{0]
            for(Integer groupId:inputGrpIds) {
                if ((groupIds.size() > 0) && (groupIds.contains(groupId))) {
                    knLogger.debug(methodName, "groupId: " + groupId + "is avilable for mdn:" + KnGDPRTemplate.mdn(mdn) + " calling deleteSSChannelGrpInfo method");
                    xdmServerDAO.deleteSSChannelGrpInfo(mdn, groupId, persisterTxn);
                    knLogger.debug(methodName, "delete groupId: " + groupId + "is  avilable for mdn:" + KnGDPRTemplate.mdn(tgssDocDTO.getMdn()) + " to deleteSSChannelGrpInfo");
                    dirDocEtag = commonXdmDAO.getCurrentDirEtagForUpdate(mdn, persisterTxn);
                    // update etag of directory
                    commonXdmDAO.updateEtagForDirDoc(mdn, persisterTxn);
                    KnTGSSDocDTO docDTO =  xdmServerDAO.getTGSSDoc(mdn, persisterTxn);
                    etag = docDTO.getEtag();
                    newDocEtag = etag + 1;
                    xdmServerDAO.updateEtagTGSSDoc(mdn, newDocEtag, persisterTxn);

                    String xcapRootUti = genInfoUtil.getXCAPRootURI(mdn,persisterTxn );

                    KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
                    KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
                    docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                    String contactDocUri = pubInfoUtil.generateTGSSListSelUri(mdn, KnConstants.DOC_CHANGE_TYPE.REPLACE);
                    docChgDTO.setDocUri(contactDocUri);
                    docChgDTO.setNewEtag(String.valueOf(etag + 1));
                    Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
                    docChgList.add(docChgDTO);
                    dirChgDTO.setDocChgDTO(docChgList);
                    String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
                    dirChgDTO.setDirUri(dirDocUri);
                    dirChgDTO.setDirPrevEtag(String.valueOf(dirDocEtag));
                    int newEtag = dirDocEtag + 1;
                    dirChgDTO.setDirNewEtag(String.valueOf(newEtag));
                    dirChgDTO.setPocHome(subsProfile.getPocHome());
                    dirChgDTO.setPresenceHome(subsProfile.getPresenceHome());
                    dirChgDTO.setXcapRootURI(xcapRootUti);
                    dirChgDTO.setProtoVersion(subsProfile.getProtocolVersion());

                    response = pubInfoUtil.populateSuccessResponse();
                    //            construct xcap-diff notification for TGSSListSelUri and set in response

                    response.setDirChgDTO(dirChgDTO);
                    response.setDocEtag(String.valueOf(newDocEtag));

                } else {
                    knLogger.error(methodName, "Group not Exists");
                    throw new KnPubBOException(GROUP_DOES_NOT_EXISTS, "Group not Exists");
                }
            }
        } catch (KnAASException aex) {
            knLogger.error(methodName, "Authorization Exception occured :" + aex);
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(TGSS_DOC_NOT_EXISTS, "TGSS doc doesnot exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error(methodName, "Validation Exception occured :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while while deleting tgss doc info: " + ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while deleting groupId info ", ex);
        }

        return response;

    }
}