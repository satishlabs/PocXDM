/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   Kn.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       12/25/10       7.0
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

package com.kodiak.xdms.server.subsmgmt.clientIntf.impl;

import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.commdto.request.KnXDMCorpInfoDTO;
import com.kodiak.common.commdto.request.KnXDMCorpProfileInfoDTO;
import com.kodiak.common.commdto.request.KnXDMDeviceProvInfoDTO;
import com.kodiak.common.commdto.request.KnXDMLoginNotifyEventReqDTO;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.commdto.request.KnXDMActivateInfoDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.xdms.server.common.dto.clientdat.KnIPChangeMDNInfoDTO;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.business.IBulkSubsProvController;
import com.kodiak.xdms.server.subsmgmt.business.IPAMAccController;
import com.kodiak.xdms.server.subsmgmt.business.ISubsProvController;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBORegistry;
import com.kodiak.xdms.server.subsmgmt.clientIntf.ISubsProvManager;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvEntityTypes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvOperationTypes;

import java.util.List;
import java.util.Map;

public class KnSubsProvManager implements ISubsProvManager {
    private ISubsProvController subsProvController;
    private IPAMAccController pamAccController;
    private IBulkSubsProvController bulkSubsProvController;

    public KnSubsProvManager() {
        subsProvController = KnProvBORegistry.createSubsProvController();
        pamAccController = KnProvBORegistry.createPAMAccController();
        bulkSubsProvController = KnProvBORegistry.createBulkSubsProvController();
    }

    public KnOPCreateSubsInfoDTO createSubscriber(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subsProfileInputDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subsProfileInputDTO.setOperationType(KnProvOperationTypes.CREATE_SUBSCRIBER);
        return subsProvController.createSubscriber(subsProfileInputDTO, persisterTxn);
    }

    public KnOPUpdateSubsInfoDTO updateSubscriber(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subsProfileInputDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subsProfileInputDTO.setOperationType(KnProvOperationTypes.UPDATE_SUBSCRIBER);
        return subsProvController.updateSubscriber(subsProfileInputDTO, persisterTxn);
    }
    public KnOPUpdateSubsInfoDTO updateSubscriberFSAndPkgCodes(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subsProfileInputDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subsProfileInputDTO.setOperationType(KnProvOperationTypes.UPDATE_SUBSCRIBER);
        return subsProvController.updateSubscriberFSAndPkgCodes(subsProfileInputDTO, persisterTxn);
    }

    public KnOPUpdateSubsInfoDTO updateSubscriberUserAgent(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subsProfileInputDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subsProfileInputDTO.setOperationType(KnProvOperationTypes.UPDATE_SUBSCRIBER);
        return subsProvController.updateSubscriberUserAgent(subsProfileInputDTO, persisterTxn);
    }


    public KnOPDeleteSubsRespDTO deleteSubscriber(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        subscriberDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subscriberDTO.setOperationType(KnProvOperationTypes.DELETE_SUBSCRIBER);
        return subsProvController.deleteSubscriber(subscriberDTO, persisterTxn);
    }

    public KnOPSubsProfileInfoDTO getSubscriberDetails(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        subscriberDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subscriberDTO.setOperationType(KnProvOperationTypes.GET_SUBSCRIBER_PROFILE);
        return subsProvController.getSubscriberDetails(subscriberDTO, persisterTxn);
    }

    public KnOPSubsProfileInfoDTO getSubscriberDetailsBasic(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        subscriberDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subscriberDTO.setOperationType(KnProvOperationTypes.GET_SUBSCRIBER_PROFILE);
        return subsProvController.getSubscriberDetailsBasic(subscriberDTO, persisterTxn);
    }

    public KnOPSubsProfileInfoDTO getSubscriberIfExist(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        return subsProvController.getSubscriberIfExist(mdn, persisterTxn);
    }

    public KnOPSubsProfileInfoDTO getSubsDetails(KnIPSubscriberInfoDTO subscriberDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnProvException {
        subscriberDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subscriberDTO.setOperationType(KnProvOperationTypes.GET_SUBSCRIBER_PROFILE_XDMDATA_INTF);
        return subsProvController.getSubsDetails(subscriberDTO, readOnly, persisterTxn);
    }

    public KnOPSubsProfileInfoDTO changeMDN(KnIPChangeMDNInfoDTO changeMDNInfoDTO,boolean isAsyncCall, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        changeMDNInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        changeMDNInfoDTO.setOperationType(KnProvOperationTypes.CHANGE_MDN);
        return subsProvController.changeMdn(changeMDNInfoDTO,isAsyncCall, persisterTxn);
    }

    public KnOPSubsProfileInfoDTO forceSync(KnIPSubscriberInfoDTO subscriberInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subscriberInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subscriberInfoDTO.setOperationType(KnProvOperationTypes.FORCE_SYNC);
        return subsProvController.forceSync(subscriberInfoDTO, persisterTxn);
    }

    public KnOPProvDTO sendConfigDocNotification(String mdn, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return subsProvController.sendConfigDocNotification(mdn, persisterTxn);
    }

    public KnOPActivationInfoDTO activateSubscriber(KnIPActivateMDNInfoDTO activateMdnInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        activateMdnInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        activateMdnInfoDTO.setOperationType(KnProvOperationTypes.ACTIVATE);
        return subsProvController.activateSubscriber(activateMdnInfoDTO, persisterTxn);
    }

    public KnOPActivationInfoDTO selectProfileMdn(KnIPSelectProfileMdnDTO selectProfileMdnDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        selectProfileMdnDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        selectProfileMdnDTO.setOperationType(KnProvOperationTypes.SELECTPROFILEMDN);
        return subsProvController.selectProfileMdn(selectProfileMdnDTO, persisterTxn);
    }

    public KnOPSubsConfigDocInfoDTO getSubscriberConfigDocument(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subscriberDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subscriberDTO.setOperationType(KnProvOperationTypes.GET_SUBSCRIBER_CONFIG_DOC);
        return subsProvController.getSubscriberConfigDoc(subscriberDTO, persisterTxn);
    }

    public KnOPChgAuthStatusRespDTO changeServiceAuthStatus(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subsProvInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subsProvInfoDTO.setOperationType(KnProvOperationTypes.CHANGE_SERVICE_AUTH_STATUS);
        return subsProvController.changeServiceAuthStatus(subsProvInfoDTO, persisterTxn);
    }

    public KnOPProvDTO removeSubsInfo(KnIPChangeMDNInfoDTO changeMDNInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        changeMDNInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        changeMDNInfoDTO.setOperationType(KnProvOperationTypes.REMOVE_SUBS_INFO);
        return subsProvController.removeSubsInfo(changeMDNInfoDTO, persisterTxn);
    }

    public KnOPSubsProfileInfoDTO getDefaultSubscriberProfile(KnIPSubscriberInfoDTO subscriberInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        subscriberInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subscriberInfoDTO.setOperationType(KnProvOperationTypes.GET_DEFAULT_SUBSCRIBER_PROFILE);
        return subsProvController.getDefaultSubscriberProfile(subscriberInfoDTO, persisterTxn);
    }

    public KnOPProvDTO deleteCorporateProfile(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        corpProfileInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        corpProfileInfoDTO.setOperationType(KnProvOperationTypes.DELETE_CORP_PROFILE);
        return subsProvController.deleteCorpProfile(corpProfileInfoDTO, persisterTxn);
    }

    public KnOPProvDTO updateExtCorporateID(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        corpProfileInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        //corpProfileInfoDTO.setOperationType(KnProvOperationTypes.UPDATE_SUBSCRIBER);
        return subsProvController.updateExtCorpID(corpProfileInfoDTO, persisterTxn);
    }

    public KnOPCreatePAMAccountDTO createPAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        pamAccInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        pamAccInfoDTO.setOperationType(KnProvOperationTypes.CREATE_PAM_ACCOUNT);
        return pamAccController.createPAMAccount(pamAccInfoDTO, persisterTxn);
    }

    public void validateUpgradePAMAccount(int maxSub, KnPersisterTxn persisterTxn) throws KnProvException {
        pamAccController.validateUpgradePAMAccount(maxSub, persisterTxn);
    }

    public KnOPUpdatePAMAccountDTO validateUpdatePamAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
       return  pamAccController.validateUpdatePamAccount(pamAccInfoDTO, persisterTxn);
    }

    public void updatePAMEtag(int pamAccID, KnPersisterTxn persisterTxn) throws KnProvException {
        pamAccController.updatePAMEtag(pamAccID, persisterTxn);
    }

    @Override
    public KnOPProvDTO createSubscrRoamingProfiles(String mdn, KnPersisterTxn persisterTxn) throws KnProvException {
        return subsProvController.createSubscrRoamingProfiles(mdn, persisterTxn);
    }

    @Override
    public KnOPProvDTO deleteSubscrRoamingProfiles(String mdn, KnPersisterTxn persisterTxn) throws KnProvException {
        return subsProvController.deleteSubscrRoamingProfiles(mdn, persisterTxn);
    }

    @Override
    public KnOPCorpProfileInfoDTO retrieveCorporateProfile(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        return subsProvController.retrieveCorporateProfile(extCorpId, persisterTxn);
    }


    @Override
    public List<Integer> retrievePAMAccIdForCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnProvException {
        return pamAccController.retrievePAMAccIdForCorpId(corpId, persisterTxn);
    }


    @Override
    public KnOPCorpProfileInfoDTO createCorpProfile(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return subsProvController.createCorpProfile(corpProfileInfoDTO, persisterTxn);
    }

    public KnOPUpdatePAMAccountDTO migratePAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        pamAccInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        pamAccInfoDTO.setOperationType(KnProvOperationTypes.MIGRATE_PAM_ACCOUNT);
        return pamAccController.migratePAMAccount(pamAccInfoDTO, persisterTxn);
    }

    public int retrieveCorporationId(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvException {
        return pamAccController.retrieveCorporationId(extCorpId, persisterTxn);
    }

    public KnOPDeletePAMAccountDTO deletePAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        pamAccInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        pamAccInfoDTO.setOperationType(KnProvOperationTypes.DELETE_PAM_ACCOUNT);
        return pamAccController.deletePAMAccount(pamAccInfoDTO, persisterTxn);
    }

    public KnOPProvDTO createPAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        pamAccInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        pamAccInfoDTO.setOperationType(KnProvOperationTypes.CREATE_PAM_SUBS_PROFILE);
        return pamAccController.createPAMSubsProfile(pamAccInfoDTO, persisterTxn);
    }

    public KnOPProvDTO updatePAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        pamAccInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        pamAccInfoDTO.setOperationType(KnProvOperationTypes.UPDATE_PAM_SUBS_PROFILE);
        return pamAccController.updatePAMSubsProfile(pamAccInfoDTO, persisterTxn);
    }

    public KnOPProvDTO deletePAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        pamAccInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        pamAccInfoDTO.setOperationType(KnProvOperationTypes.DELETE_PAM_SUBS_PROFILE);
        return pamAccController.deletePAMSubsProfile(pamAccInfoDTO, persisterTxn);
    }

    public KnOPPAMAccInfoDTO getPAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        pamAccInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        pamAccInfoDTO.setOperationType(KnProvOperationTypes.GET_PAM_SUBS_PROFILE);
        return pamAccController.getPAMSubsProfile(pamAccInfoDTO, persisterTxn);
    }

    public KnOPPAMAccInfoDTO retrievePAMSubsProfInfo(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        pamAccInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        pamAccInfoDTO.setOperationType(KnProvOperationTypes.GET_PAM_SUBS_PROFILE);
        return pamAccController.retrievePAMSubsProfInfo(pamAccInfoDTO, persisterTxn);
    }

    public KnOPCreateSubsInfoDTO createSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        bulkSubsProvInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        bulkSubsProvInfoDTO.setOperationType(KnProvOperationTypes.CREATE_BULK_SUBSCRIBER);
        return bulkSubsProvController.createSubscribers(bulkSubsProvInfoDTO, persisterTxn);
    }

    public KnOPUpdateSubsInfoDTO updateSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        bulkSubsProvInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        bulkSubsProvInfoDTO.setOperationType(KnProvOperationTypes.UPDATE_BULK_SUBSCRIBER);
        return bulkSubsProvController.updateSubscribers(bulkSubsProvInfoDTO, persisterTxn);
    }

    public KnOPBulkDeleteSubsRespDTO deleteSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, boolean isLastMDN, KnPersisterTxn persisterTxn) throws KnProvException {
        bulkSubsProvInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        bulkSubsProvInfoDTO.setOperationType(KnProvOperationTypes.DELETE_BULK_SUBSCRIBER);
        return bulkSubsProvController.deleteSubscribers(bulkSubsProvInfoDTO, isLastMDN, persisterTxn);
    }

    public KnOPBulkChgAuthStatusRespDTO changeServiceAuthStatuses(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        bulkSubsProvInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        bulkSubsProvInfoDTO.setOperationType(KnProvOperationTypes.CHANGE_BULK_SERVICE_AUTH_STATUS);
        return bulkSubsProvController.changeServiceAuthStatuses(bulkSubsProvInfoDTO, persisterTxn);
    }

    public List<String> retrievePAMAccountMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvException {
        return bulkSubsProvController.retrievePAMAccountMDNs(pamAccId, persisterTxn);
    }

    public List<String> getPAMAccountMdnsByInsertionTime(int pamAccId, long insertionTime, KnPersisterTxn persisterTxn) throws KnProvException {
        return bulkSubsProvController.getPAMAccountMdnsByInsertionTime(pamAccId, insertionTime, persisterTxn);
    }

    public List<KnOPSubsProfileInfoDTO> retrievePAMAccountMDNsDetails(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvException {
        return bulkSubsProvController.retrievePAMAccountMDNsDetails(pamAccId, persisterTxn);
    }

    public List<String> retrievePAMAccountMDNs(int pamAccId, int fetchSize, String listMdn, KnPersisterTxn persisterTxn) throws KnProvException {
        return bulkSubsProvController.retrievePAMAccountMDNs(pamAccId, fetchSize, listMdn, persisterTxn);
    }

    public List<String> retrievePAMAccountMDNs(int pamAccId, String startMdn, String endMdn, int fetchSize, KnPersisterTxn persisterTxn) throws KnProvException {
        return bulkSubsProvController.retrievePAMAccountMDNs(pamAccId, startMdn, endMdn, fetchSize, persisterTxn);
    }

    public KnOPUpdatePAMAccountDTO updatePAMAccState(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return pamAccController.updatePAMAccState(pamAccInfoDTO, persisterTxn);
    }

    public KnOPCreateSubsInfoDTO validateCreateSubscriber(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, int subsCount,
                                                          KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return bulkSubsProvController.validateCreateSubscriber(bulkSubsProvInfoDTO, subsCount, persisterTxn);

    }

    public KnOPPAMAccInfoDTO getPAMAccountInfo(String extPAMAccId, KnPersisterTxn persisterTxn) throws KnProvException {
        return pamAccController.getPAMAccountInfo(extPAMAccId, persisterTxn);

    }

    public KnOPPAMAccInfoDTO getPAMAccInfoFromId(int pamAccId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnProvException {
        return pamAccController.getPAMAccInfoFromId(pamAccId, readOnly, persisterTxn);

    }

    public KnOPUpdatePAMAccountDTO updateCorpName(String extCorpId, String corpName, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException {
        return pamAccController.updateCorpName(extCorpId, corpName, persisterTxn);

    }

    @Override
    public KnOPUpdateSubsInfoDTO validateUpdateSubscriber(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return bulkSubsProvController.validateUpdateSubscriber(bulkSubsProvInfoDTO, persisterTxn);
    }

    public KnOPUpdatePAMAccountDTO updatePAMAccMaxSub(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return pamAccController.updatePAMAccMaxSub(pamAccInfoDTO, persisterTxn);
    }

    public KnOPUpdatePAMAccountDTO updatePAMAccName(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return pamAccController.updatePAMAccName(pamAccInfoDTO, persisterTxn);
    }

    public List<String> retrievePAMAccountProvMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvException {
        return bulkSubsProvController.retrievePAMAccountProvMDNs(pamAccId, persisterTxn);
    }

    public List<String> getPamAccLastSequenceMdns(int pamAccId, int start, int end, KnPersisterTxn persisterTxn) throws KnProvException {
        return bulkSubsProvController.getPamAccLastSequenceMdns(pamAccId, start, end, persisterTxn);
    }

    @Override
    public KnOPBulkRespDTO updateDispForSubscribers(Map<String, Integer> subscribers, int bitNo, KnPersisterTxn persisterTxn) throws KnProvException {
        return bulkSubsProvController.updateDispForSubscribers(subscribers, bitNo, persisterTxn);
    }

    @Override
    public KnOPProvDTO createExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        extSubsInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        extSubsInfoDTO.setOperationType(KnProvOperationTypes.CREATE_EXT_SUBSCRIBER);
        return subsProvController.createExtSubscriber(extSubsInfoDTO, persisterTxn);
    }

    @Override
    public KnOPProvDTO deleteExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        extSubsInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        extSubsInfoDTO.setOperationType(KnProvOperationTypes.DELETE_EXT_SUBSCRIBER);
        return subsProvController.deleteExtSubscriber(extSubsInfoDTO, persisterTxn);
    }

    @Override
    public KnOPProvDTO updateExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        extSubsInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        extSubsInfoDTO.setOperationType(KnProvOperationTypes.UPDATE_EXT_SUBSCRIBER);
        return subsProvController.updateExtSubscriber(extSubsInfoDTO, persisterTxn);
    }

    @Override
    public KnExtSubscriberInfoDTO getExtSubscriberInfo(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        extSubsInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        extSubsInfoDTO.setOperationType(KnProvOperationTypes.GET_EXT_SUBSCRIBER);
        return subsProvController.getExtSubscriberInfo(extSubsInfoDTO, persisterTxn);
    }

    @Override
    public List<Integer> getCorpIdsForExtSubscriber(String mdn, KnPersisterTxn persisterTxn) throws KnProvException {
        return subsProvController.getCorpIdsForExtSubscriber(mdn, persisterTxn);
    }

    @Override
    public KnOPProvDTO addOrModifyExtSubscribers(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        extSubsInfoDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        extSubsInfoDTO.setOperationType(KnProvOperationTypes.ADD_MODIFY_EXT_SUBSCRIBER);
        return subsProvController.addOrModifyExtSubscribers(extSubsInfoDTO, persisterTxn);

    }

   /* @Override
    public List<KnOPProvDTO> sendBulkConfigDocNotification(List<String> mdnList,KnPersisterTxn persisterTxn)throws KnProvBOException{
        return bulkSubsProvController.sendBulkConfigDocNotification(mdnList,persisterTxn);
    }*/

    @Override
    public List<KnOPUpdateSubsInfoDTO> updateHierarchy(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        return bulkSubsProvController.updateHierarchy(bulkSubsProvInfoDTO, persisterTxn);
    }

    @Override
    public boolean validateCorpSubsLimitAndPairLimit(int corpId, int subsCount, KnPersisterTxn persisterTxn) throws KnProvBOException {
        return bulkSubsProvController.validateCorpSubsLimitAndPairLimit(corpId, subsCount, persisterTxn);
    }

    @Override
    public List<String> retrieveBanMDNs(int banId, HIERARCHY_TYPE hierarchyType, KnPersisterTxn persisterTxn) throws KnProvException {
        return bulkSubsProvController.retrieveBanMDNs(banId, hierarchyType, persisterTxn);
    }


    @Override
    public KnOPProvDTO updatePAMSubsProfCorpId(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, int oldCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        return bulkSubsProvController.updatePAMSubsProfCorpId(bulkSubsProvInfoDTO, oldCorpId, persisterTxn);
    }

    @Override
    public KnOPProvDTO updateCorpProfileLastUpdateTime(int corpId, KnPersisterTxn persisterTxn) throws KnProvBOException {
        return subsProvController.updateCorpProfileLastUpdateTime(corpId, persisterTxn);
    }

    @Override
    public KnOPProvDTO updateEtagForNNISubscr(String extCorpId, int clientType, KnPersisterTxn persisterTxn) throws KnProvBOException {
        return pamAccController.updateEtagForNNISubscr(extCorpId, clientType, persisterTxn);
    }

    @Override
    public boolean isMdnExistsInPseudoPoolNPamAccInfo(String mdn, KnPersisterTxn persisterTxn) throws KnProvBOException {
        return subsProvController.isMdnExistsInPseudoPoolNPamAccInfo(mdn, persisterTxn);
    }

    @Override
	public KnOPUpdateSubsInfoDTO updateAutoPairing(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException {
		 subsProfileInputDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
	     subsProfileInputDTO.setOperationType(KnProvOperationTypes.UPDATE_AUTO_PAIRING);
	     return subsProvController.updateAutoPairing(subsProfileInputDTO, persisterTxn);
	}

	@Override
	public KnOPSubsProfileInfoDTO createTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
		 return subsProvController.createTPUser(tpUserInfoDTO, persisterTxn);
	}

	@Override
	public KnOPSubsProfileInfoDTO updateTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
		 return subsProvController.updateTPUser(tpUserInfoDTO, persisterTxn);
	}

	@Override
	public KnOPProvDTO deleteTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
		return subsProvController.deleteTPUser(tpUserInfoDTO, persisterTxn);
	}

	@Override
	public void verifyTPAccount(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
		subsProvController.verifyTPAccount(tpUserInfoDTO, persisterTxn);
	}


    public Map<String ,String> fetchActiveFSForBulkMdns(List<String> mdns, KnPersisterTxn persisterTxn) throws KnProvException {
        return subsProvController.fetchActiveFSForBulkMdns(mdns, persisterTxn);
    }

    @Override
    public KnOPUpdateSubsInfoDTO switchConvergedClient(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvBOException {
        subsProvInputDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subsProvInputDTO.setOperationType(KnProvOperationTypes.SWITCH_CONVERGED_CLIENT);
        return subsProvController.switchConvergedClient(subsProvInputDTO, persisterTxn);
    }

    @Override
    public KnSysConfigRespDTO getSysConfig(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subsProfileInputDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subsProfileInputDTO.setOperationType(KnProvOperationTypes.GETSYSCONFIG);
        return subsProvController.getSysConfig(subsProfileInputDTO, persisterTxn);
    }

    @Override
    public KnOPBulkRespDTO updateBulkSubsFSAndPkgIds(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException {

        return bulkSubsProvController.updateBulkSubsFSAndPkgIds(bulkSubsProvInfoDTO,persisterTxn);
    }

    @Override
    public KnOPProvDTO actionOnTGSSDoc(String mdn, String newActiveFS, String oldActiveFS, KnPersisterTxn persisterTxn) throws KnProvException {
        return subsProvController.actionOnTGSSDoc(mdn, newActiveFS,oldActiveFS,persisterTxn );
    }

    @Override
    public KnOPProvDTO actionOnTGSSDocCust(String mdn, String newActiveFS, String oldActiveFS, KnPersisterTxn persisterTxn) throws KnProvException {
        return subsProvController.actionOnTGSSDocCust(mdn, newActiveFS,oldActiveFS,persisterTxn );
    }

	@Override
	public KnOPSubsProfileInfoDTO searchCorpAddressBook(KnIPSubscriberInfoDTO subscriberDTO,
			KnPersisterTxn persisterTxn) throws KnProvException {
		subscriberDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subscriberDTO.setOperationType(KnProvOperationTypes.SEARCH_ADD_BOOK);
        return subsProvController.searchCorpAddressBook(subscriberDTO, persisterTxn);
	}

    @Override
    public KnOPSubsProfileInfoDTO updatePrivacyOptStatus(KnIPSubscriberInfoDTO subscriberDTO,
                                                        KnPersisterTxn persisterTxn) throws KnProvException {
        subscriberDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subscriberDTO.setOperationType(KnProvOperationTypes.PRIVACY_OPT_STATUS);
        return subsProvController.updatePrivacyOptStatus(subscriberDTO, persisterTxn);
    }
    
    @Override
    public void updateMCSIds(KnPersisterTxn persisterTxn, KnSubsProfilePersistDTO subsProfilePersistDTO) throws KnDAOException, KnProvBOException
    {
    	subsProvController.updateMCSIds(persisterTxn, subsProfilePersistDTO);
    }

    @Override
    public KnXDMProfileIdMdnMapRespDTO getMdnProfileIdsForMcPttIds(KnIPSubscriberInfoDTO subscriberDTO, boolean readOnly,
                                                                   KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        return subsProvController.getMdnProfileIdsForMcPttIds(subscriberDTO, readOnly, persisterTxn);
    }

    @Override
    public String getMdnForUnassign(String baseMdn, String userProfileId, KnPersisterTxn persisterTxn) throws KnProvException {
        return subsProvController.getMdnForUnassign(baseMdn, userProfileId, persisterTxn);
    }

    @Override
    public KnOPCreateSubsInfoDTO createSubscriberForAssignUserProfile(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subsProfileInputDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subsProfileInputDTO.setOperationType(KnProvOperationTypes.CREATE_SUBSCRIBER);
        return subsProvController.createSubscriberForAssignUserProfile(subsProfileInputDTO, persisterTxn);
    }

    @Override
    public KnOPUpdateSubsInfoDTO updateSubscriberUserProfileFS(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subsProfileInputDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subsProfileInputDTO.setOperationType(KnProvOperationTypes.UPDATE_SUBSCRIBER_USER_PROFILE_FS);
        return subsProvController.updateSubscriberUserProfileFS(subsProfileInputDTO, persisterTxn);
    }

    @Override
    public KnOPUpdateSubsInfoDTO updateSubscrTS(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subsProfileInputDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subsProfileInputDTO.setOperationType(KnProvOperationTypes.UPDATE_SUBSCRIBER_USER_PROFILE_FS);
        return subsProvController.updateSubscrTS(subsProfileInputDTO, persisterTxn);
    }
	
	@Override
	public KnOPCreateSubsInfoDTO createDevice(KnXDMDeviceProvInfoDTO deviceProvInfoInputDTO, KnPersisterTxn persisterTxn)
			throws KnDAOException, KnProvException {
    	 return subsProvController.createDevice(deviceProvInfoInputDTO, persisterTxn);
	}
    
	@Override
	public KnXDMDeviceProvDTO getDeviceInfo(String deviceId, KnPersisterTxn persisterTxn)
			throws KnDAOException, KnProvException {
		return subsProvController.getDeviceInfo(deviceId, persisterTxn);
	}

	@Override
	public void deleteDeviceInfo(KnXDMDeviceProvDTO deviceId, KnPersisterTxn persisterTxn)
			throws KnDAOException, KnProvException {
																															   
		 subsProvController.deleteDeviceInfo(deviceId, persisterTxn);
	}

    @Override
    public KnOPUpdateSubsInfoDTO modifyDevice(KnXDMDeviceProvInfoDTO deviceProvInfoInputDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvException {
        return subsProvController.modifyDevice(deviceProvInfoInputDTO, persisterTxn);
    }

	@Override
    public List<String> getMdnForUPM(String baseMdn, KnPersisterTxn persisterTxn) throws KnProvBOException ,KnProvException
	{
		return subsProvController.getMdnForUPM(baseMdn, persisterTxn);
	}
	
	@Override
	public void loginNotifyEvent(KnXDMLoginNotifyEventReqDTO loginNotifyEventReqDTO, KnPersisterTxn persisterTxn)
			throws KnProvException, KnFWException {
        subsProvController.loginNotifyEvent(loginNotifyEventReqDTO, persisterTxn);

	}

    @Override
    public KnOPMCPTTPermissionDTO getAuthorizedUserList(KnIPMCPTTPermissionDTO tuInfo, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        tuInfo.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        tuInfo.setOperationType(KnProvOperationTypes.GETAUTHORIZEDUSERLIST);
        return subsProvController.getAuthorizedUserList(tuInfo, persisterTxn);
    }

    @Override
    public KnOPSubsProfileInfoDTO getUserprofileidsByProfileMdns(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        subscriberDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subscriberDTO.setOperationType(KnProvOperationTypes.GET_USERPROFILEIDS_BY_PROFILEMDNS);
        return subsProvController.getUserprofileidsByProfileMdns(subscriberDTO, persisterTxn);
    }

    @Override
    public KnXDMSubsProfileRespDTO getSubscrClientSettings(KnIPSubsProvInfoDTO subscrClientSettings, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subscrClientSettings.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subscrClientSettings.setOperationType(KnProvOperationTypes.GET_SUBSCRIBERCLINET_SETTINGS);
        return subsProvController.getSubscrClientSettings(subscrClientSettings,persisterTxn);
    }

    @Override
    public KnOPUpdateSubsInfoDTO setSubscrClientSettings(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        subsProfileInputDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        subsProfileInputDTO.setOperationType(KnProvOperationTypes.SET_SUBSCRIBERCLIENT_SETTINGS);
        return subsProvController.setSubscrClientSettings(subsProfileInputDTO, persisterTxn);
    }

    public KnOPCreateSubsInfoDTO createCorpAccount(KnXDMCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvException {
        return subsProvController.createCorpAccount(corpProfileInfoDTO, persisterTxn);
    }

    @Override
    public KnOPCreateSubsInfoDTO  updateCorpAccount(KnXDMCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        return subsProvController.updateCorpAccount(corpProfileInfoDTO, persisterTxn);
    }

    public KnOPDeleteSubsRespDTO deleteCorpAccount(KnXDMCorpInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvException {
        return subsProvController.deleteCorpAccount(corpProfileInfoDTO, persisterTxn);
    }

    @Override
    public KnCorporateProfilepersistDTO1 getCorporateAccountDetails(KnXDMDeviceProvInfoDTO xdmRequestDTO, KnPersisterTxn persisterTxn) throws KnException {
        xdmRequestDTO.setEntityId(KnProvEntityTypes.SUBS_PROV_MANAGER);
        xdmRequestDTO.setOperationType(KnProvOperationTypes.GET_CORPORATE_ACCOUNT_DETAILS);
        return subsProvController.getCorporateAccountDetails(xdmRequestDTO, persisterTxn);
    }
    @Override
    public KnXDMCorpAccountsListDTO retrieveCorporationAccountsList(KnPersisterTxn persisterTxn,String fetchSize, String nextToken)
            throws KnDAOException, KnProvException {
        return subsProvController.retrieveCorporationAccountsList(persisterTxn,fetchSize,nextToken);
    }

    public String getBaseMdnByProfileMdn(String mdn, KnPersisterTxn persisterTxn) throws KnProvException {
        return subsProvController.getBaseMdnByProfileMdn(mdn, persisterTxn);
    }

    @Override
    public KnUserLoginResponseDTO lockRequestErrorProcessor(KnOPSubsProfileInfoDTO subscriberDTO, KnXDMActivateInfoDTO activateInfoDTO,KnPersisterTxn persisterTxn) throws KnProvException {
        return subsProvController.lockRequestErrorProcessor(subscriberDTO,activateInfoDTO,persisterTxn);
    }


    @Override
    public Map<String, Integer> getSubscriberServiceAuthStatus(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        return subsProvController.getSubscriberServiceAuthStatus(mdns, persisterTxn);
    }

    @Override
    public KnXDMExtGWProfileListDTO retrieveExtGWProfileList() throws KnDAOException, KnProvException {
        return subsProvController.retrieveExtGWProfileList();
    }

    @Override
    public int getSubscriberServiceAuthStatusByUserId(String userId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        return subsProvController.getSubscriberServiceAuthStatusByUserId(userId, persisterTxn);
    }

}
