/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.xdms.server.subsmgmt.processor;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.frameworks.statisticalmgr.KnOMConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnDeviceInfoPersistDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.business.impl.KnSubsProvController;
import com.kodiak.xdms.server.subsmgmt.dao.KnProvFactorySelector;
import com.kodiak.xdms.server.subsmgmt.dao.persister.IProvXDMServerDAO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPDeleteSubsRespDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnTPUserPersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;

import java.util.*;

import static com.kodiak.xdms.server.common.resources.KnConstants.*;

public class KnProvDeleteSubscProcessor {

    private static final KnLogger knLogger = KnLogger.getLogger(KnProvDeleteSubscProcessor.class);
    private KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();


    public KnOPDeleteSubsRespDTO processDeleteSub(KnIPSubscriberInfoDTO subscriberDTO,
                                                  KnPersisterTxn persisterTxn, BitSet taskBitSet, KnOPSubsProfileInfoDTO existingProfileInfoDTO) throws KnProvBOException {  // DEL - Delete Operation D3
        String methodName = "deleteSubscriber(KnIPSubscriberInfoDTO, KnPersisterTxn)";
        boolean ownedTxn = false;
        KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
        KnOPDeleteSubsRespDTO responseDTO= new KnOPDeleteSubsRespDTO();
        knLogger.info(methodName, "ENTRY: Mdn ", subscriberDTO.getMdn());
        Collection<Integer> successPegs = new ArrayList<Integer>();
        try {
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            String mdn = subscriberDTO.getMdn();
            knLogger.debug(methodName, "existingProfileInfoDTO :", existingProfileInfoDTO);
            KnProvInfoUtil provInfoUtil = new KnProvInfoUtil();
            existingProfileInfoDTO = provInfoUtil.retrieveSubscriberInfo(mdn, persisterTxn);
            int subsClientType = existingProfileInfoDTO.getSubsClientType();
            if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DESKTOP.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_DESKTOP_CLIENTS_DELETED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.DISPATCH.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_DISPATCH_CLIENTS_DELETED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.POCDONORRADIO.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_LMR_INTEROP_CLIENTS_DELETED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_3RD_PARTY_POC_CLIENTS_DELETED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_MOBILE_API_CLIENTS_DELETED);
            } else if (subsClientType == KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_3RDPARTY_DISPATCHER_CLIENTS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_NNI_ALIAS_SUBSCRIBERS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_NNI_GROUP_SUBSCRIBERS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.CROSS_CARRIER_PTT_CLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_CROSS_CARRIER_PTT_CLIENTS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOCROSSCARRIERCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_CROSSCARRIER_CLIENTS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOHANDSETCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_HANDSET_CLIENTS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.PTTRADIOWIFIONLYCLIENT.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_PTTRADIO_WIFIONLY_CLIENTS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.SGMDNPATCH.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_DELETED);
            } else if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.DATAGROUPMDN.value()) {
                successPegs.add(KnOMConstants.XDM_NUM_SG_MDN_PATCH_CLIENTS_DELETED);
            }
            subsProfilePersistDTO.setMdn(mdn);

            IXDMServerDAO commonXdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
            //getting the prev etag of directory
            int previousEtag = commonXdmServerDAO.getCurrentEtagForDirDoc(mdn, persisterTxn);

            commonXdmServerDAO.deleteXDMDirectoryForMdn(mdn, persisterTxn);
            knLogger.debug(methodName, "deleted the directory for Mdn - ", KnGDPRTemplate.mdn(mdn));

            //deleting the XDM Contact List
            commonXdmServerDAO.deleteContactListForMdn(mdn, persisterTxn);
            knLogger.debug(methodName, "deleted the Contact List for mdn - ", KnGDPRTemplate.mdn(mdn));

            //deleting the XDM Contact List Doc Map
            commonXdmServerDAO.deleteContactListDocMapForMdn(mdn, persisterTxn);
            knLogger.debug(methodName, "deleted the contact list doc map for mdn - ", KnGDPRTemplate.mdn(mdn));

            //deleting Corp resource List Index Doc
            int corpSubscriptionType = existingProfileInfoDTO.getCorporateSubscriptionType();
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                commonXdmServerDAO.deleteCorpResourceListIndexDoc(mdn, persisterTxn);
                knLogger.debug(methodName, "deleted the Corp resource List Index Doc");
            }

            IProvXDMServerDAO provXDMServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB).createProvXDMServerDAO();

            //delete client vocoder ID from DG.ClientSuppVocoders
            provXDMServerDAO.deleteClientSuppVocoder(mdn, persisterTxn);
            String xcapRooturi = genInfoUtil.getXCAPRootURI(mdn, persisterTxn);
            //delete the subscriber roaming profile
            provXDMServerDAO.deleteSubscrRoamingProfile(mdn, persisterTxn);
            knLogger.debug(methodName, "Delete the Subscriber Roaming Profile");
            ////provXDMServerDAO.deleteSubApn(mdn, persisterTxn);
            //delete nni subscriber profile

            if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Alias_MDN.value() || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.POC_NNI_Group_MDN.value()) {
                knLogger.debug(methodName, "Deleting NNI Subscriber profile");
                List<String> mdns = new ArrayList<>();
                mdns.add(mdn);
                provXDMServerDAO.deleteNNISubscrProfile(mdns, persisterTxn);
            }
            boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(existingProfileInfoDTO.getSubsFS2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.MCDEVICE.value());
            String deviceCreatedAs = commonXdmServerDAO.getDeviceInfo(mdn, persisterTxn);
            boolean impliciteDevice = KnProvConstants.IMPLICIT_DEVICE.equals(deviceCreatedAs);
            knLogger.debug(methodName, "values of deviceCreatedAs", deviceCreatedAs, " impliciteDevice:", impliciteDevice);
            if (MCSCOMPLIANCE == existingProfileInfoDTO.getMcpttCompliance()
                    || bitEnabled
                    || existingProfileInfoDTO.getLicenseType() == KnProvConstants.LICENSEN_TYPE_STANDARD
                    || impliciteDevice) {
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                KnDeviceInfoPersistDTO deviceInfoPersistDTO = new KnDeviceInfoPersistDTO();
                deviceInfoPersistDTO.setDeviceId(mdn);
                xdmServerDAO.deleteDeviceImpiInfo(deviceInfoPersistDTO, persisterTxn);
                xdmServerDAO.deleteDeviceInfo(deviceInfoPersistDTO, persisterTxn);
                if (!impliciteDevice) {
                    responseDTO.setDeleteDeviceNotify(true);
                }
            }
            if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.STANDALONECAMERA.value()) {
                knLogger.debug(methodName, "Deleting subscriber camera info");
                IXDMServerDAO xdmServerDAO = KnFactorySelector.getDAOFactory(KnFactorySelector.DB).createXDMServerDAO(xdmPttServerId);
                xdmServerDAO.deleteSubscriberCameraInfo(mdn, persisterTxn);
            }

            if (subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value() || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.MOBILE_CLIENT.value()
                    || subsClientType == KnProvConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()) {
                knLogger.debug(methodName, "Deleting TP or Mobile Subscriber profile");
                List<String> mdns = new ArrayList<>();
                mdns.add(mdn);
                // deleting pal pool usage table entry
                provXDMServerDAO.deletePAMAccPoolUsage(mdns, persisterTxn);

                //delete TP user mdn map for this mdn
                KnTPUserPersistDTO tpUserPersistDTO = new KnTPUserPersistDTO();
                tpUserPersistDTO.setMdn(mdn);
                provXDMServerDAO.deleteTPUserMDNMap(tpUserPersistDTO, persisterTxn);
            }

            String extCorpId = null;
            //if the subscriber is a corp or corp-public type then we will retirve the extcorpid of the corp
            int corpIdFromSubscr = existingProfileInfoDTO.getCorpId();
            if (corpIdFromSubscr != 0) {
                knLogger.debug(methodName, "Subscriber is corp or corp-public type,Retrive the extCorpId for the corp=", corpIdFromSubscr);
                extCorpId = provXDMServerDAO.retrieveExtCorporationId(corpIdFromSubscr, persisterTxn);
            }
            knLogger.debug(methodName, "Ext Corp ID - ", extCorpId);

            //delete Subscriber MCPTT profile
            //provXDMServerDAO.deleteMCPTTProfile(mdn, persisterTxn);
            knLogger.debug(methodName, "Delete Subscriber MCPTT Profile");

            //Delete subscriber Authorization Doc profile
            //provXDMServerDAO.deleteAuthorizationDocProfile(mdn, persisterTxn);
            knLogger.debug(methodName, "Delete Subscriber Authorization Doc Profile");

            //Delete addon Packages
            knLogger.debug(methodName, "Deleted the subscriber addon pkgs");
            provXDMServerDAO.deleteSubAddlOnPkgs(mdn, persisterTxn);

            //Delete subscriber TGSS Doc  & SSgroupinfo profile

            knLogger.debug(methodName, "Delete TGSS SSChannel Info Doc Profile");

            provXDMServerDAO.deleteSSChannelGrpInfo(mdn, persisterTxn);

            knLogger.debug(methodName, "Delete Subscriber TGSS Doc Profile");
            provXDMServerDAO.deleteTGSSDoc(mdn, persisterTxn);

            knLogger.debug(methodName, "Delete Subscriber AliasId Info");
            provXDMServerDAO.deleteSubsAliasId(mdn, persisterTxn);

            //Get All profile MDNs for given Real MDN
            List<String> UserProfileMdns = new ArrayList<String>();
            //check if it is a Real mdn
            if (existingProfileInfoDTO.getUserProfileIndex() == null || existingProfileInfoDTO.getUserProfileIndex().equals(USER_PROFILE_INDEX)) {
                UserProfileMdns = getMdnForUPM(mdn, persisterTxn);
                UserProfileMdns.remove(mdn);
                if (!UserProfileMdns.isEmpty()) {
                    IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                            .createProvXDMServerDAO();
                    subsProfilePersistDTO.setMdn(mdn);
                    subsProfilePersistDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.DEACTIVATED.value());
                    subsProfilePersistDTO.setServiceStatusOp(KnConstants.SERVICE_STATUS_OP.DEACTIVATED.value());
                    long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                    subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
                    subsProfilePersistDTO.setMdnList(UserProfileMdns);
                    xdmServerDAO.updateServiceAuthStatusForUPM(subsProfilePersistDTO, persisterTxn);
                    knLogger.debug(methodName, "updated service auth status successfully for ProfileMdns");
                }
            }
            //delete the subscriber profile
            //provXDMServerDAO.deleteSubscriberProfile(mdn, persisterTxn); //todo cheange the doa
            //todo update serviceauth status
            knLogger.debug(methodName, "Delete Subscriber Profile");
           /* if (corpIdFromSubscr != 0) {
                knLogger.debug(methodName, "Calling Update Etag for Corp", extCorpId);
                updateEtagForNNISubscr(extCorpId, persisterTxn);
            }*/

            //deleting Corp profile Info
            //Handled below at the async path
            /*if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value()) {
                int corpId = existingProfileInfoDTO.getCorpId();
                //deleting the corp Profile.
                // --> verify if the current MDN is the last member of the Corporation
                // --> if true then delete corp profile else continue.
                int corpSubsCount = provXDMServerDAO.retrieveCorpSubscriberWithProfileCnt(corpId, persisterTxn);
                int corpProfileCleanUp = provXDMServerDAO.retrieveCorpProfileCleanUp(corpId, persisterTxn);
                int deviceCount = genInfoUtil.getDeviceCountForCorpId(corpId, persisterTxn);
                knLogger.debug(methodName, "corp Subscriber count - ", corpSubsCount, " corpProfileCleanUp :", corpProfileCleanUp, "deviceCount", deviceCount);
                if (corpSubsCount == 1 && corpProfileCleanUp != 1 && deviceCount == 0) {
                    taskBitSet.set(KnGeneralUtil.ASYNC_TASK_ID.deleteCorporateProfile.get(),true);
                    successPegs.add(KnOMConstants.XDM_NUM_CORP_PROFILE_DELETED);
                    knLogger.debug(methodName, " Corporate profile will be cleaned at the async path, success peg is created ");
                } else {
                    long profileUpdateTime = Calendar.getInstance().getTimeInMillis();
                    provXDMServerDAO.updateCorpProfileLastUpdateTime(corpId, profileUpdateTime, persisterTxn);
                }
            }*/

            //updating the service auth status for self mdn
            if (corpSubscriptionType == KnConstants.CORP_SUBSCRIPTION_TYPE.CORPORATE.value() || Optional.ofNullable(subscriberDTO.getOperationType()).filter(s -> !s.isEmpty()).isPresent()) {
                IProvXDMServerDAO xdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                        .createProvXDMServerDAO();
                subsProfilePersistDTO.setServiceAuthStatus(KnConstants.SERVICE_AUTH_STATUS.MARKED_FOR_ASYNC_DELETION.value());
                subsProfilePersistDTO.setServiceStatusOp(KnConstants.SERVICE_STATUS_OP.MARKED_FOR_ASYNC_DELETION.value());
                long lastProfileUpdateTime = Calendar.getInstance().getTimeInMillis();
                subsProfilePersistDTO.setLastProfileUpdateTime(lastProfileUpdateTime);
                subsProfilePersistDTO.setMdnList(Collections.singletonList(mdn));
                xdmServerDAO.updateServiceAuthStatusForUPM(subsProfilePersistDTO, persisterTxn);
                knLogger.debug(methodName, "updated service auth status successfully for SELF MDN ");
            }

            responseDTO.setMdn(existingProfileInfoDTO.getMdn());
            responseDTO.setPocServerHome(existingProfileInfoDTO.getPoCHome());
            responseDTO.setPresenceServerHome(existingProfileInfoDTO.getPresenceHome());

            responseDTO.setResponseMessage(KnProvConstants.DELETE_SUBSCRIBER_SUCCESS);
            responseDTO.setResponseStatus(KnConstants.RESPONSE_STATUS.SUCCESS.value());
            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            /*KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            dirChgDTO.setXcapRootURI(xdmsServiceConfigDTO.getXcapRootUri());
            */
            dirChgDTO.setXcapRootURI(xcapRooturi);
            dirChgDTO.setPocHome(existingProfileInfoDTO.getPoCHome());
            dirChgDTO.setPresenceHome(existingProfileInfoDTO.getPresenceHome());
            String dirDocUri = genInfoUtil.generateDirDocUri(mdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(previousEtag));
            dirChgDTO.setProtoVersion(String.valueOf(existingProfileInfoDTO.getClientPVmajorVer()));
            dirChgDTO.setClientType(existingProfileInfoDTO.getSubsClientType());
            responseDTO.setDirChgDTO(dirChgDTO);
            //setting the activeFS for the request.
            responseDTO.setActiveFS2(existingProfileInfoDTO.getActiveFS2());
            responseDTO.setCorpId(existingProfileInfoDTO.getCorpId());
            responseDTO.setPassword(existingProfileInfoDTO.getClientPassword());
            responseDTO.setUserProfileMdns(UserProfileMdns);
            responseDTO.setSuccessPegs(successPegs);
            knLogger.debug(methodName, "Response DTO - ", responseDTO);
            knLogger.info(methodName, "EXIT: delete Subscriber operation ");

            return responseDTO;
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
            knLogger.error(methodName, "Exception occurred while delete Subscriber");
            knLogger.error(methodName, e);
            throw new KnProvBOException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Exception occurred while delete Subscriber", e);
        }
    }

    public List<String> getMdnForUPM(String baseMdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        String methodName = "getMdnForUPM(String, boolean, KnPersisterTxn)";
        boolean ownedTxn = false;
        //KnSubsProfilePersistDTO subsProfilePersistDTO = new KnSubsProfilePersistDTO();
        knLogger.debug(methodName, "ENTRY: getMdnForUPM for baseMdn:", KnGDPRTemplate.mdn(baseMdn));
        List<String> mdnList=new ArrayList<>();
        Map<String,KnOPSubsProfileInfoDTO> mdnUpmFsMap = new HashMap<>();
        try {
            String xdmPttServerId = genInfoUtil.retrieveLocalXDMPttServerId();
            // retrieving the Subscriber Info
            IProvXDMServerDAO provXdmServerDAO = KnProvFactorySelector.getProvDAOFactory(KnProvFactorySelector.DB)
                    .createProvXDMServerDAO();

            mdnUpmFsMap = provXdmServerDAO.getProfileMdnNupmfsByBaseMdn(baseMdn, true, persisterTxn);
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
        knLogger.debug(methodName, "EXIT:  getMdnForUPM - ", KnGDPRTemplate.mdnList(mdnList));

        return mdnList;
    }
}
