/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   KnProvClientImpl.java
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

import java.util.List;
import java.util.Map;

import com.kodiak.common.commdto.common.KnXDMDeviceProvDTO;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMSystemError;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.dto.clientdat.KnIPChangeMDNInfoDTO;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.clientIntf.IProvClientIntf;
import com.kodiak.xdms.server.subsmgmt.clientIntf.ISubsProvManager;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;


public class KnProvClientImpl implements IProvClientIntf {
    private static final KnLogger knLogger = KnLogger.getLogger(KnProvClientImpl.class);
    public static final String className = KnProvClientImpl.class.getName();

    private ISubsProvManager subsProvManager;

    private static boolean isInitialized = false;

    //System property name that needs to be set at start-up
    //This is the file-name (full path) that contains the information
    //required for initializing the logger
    private static Exception exception;
    private static KnProvClientImpl instance = null;


    static {
        try {
//            KnLogger.init();

            //Now, initialize the configuration manager
            knLogger.debug("static", "Initializing config manager!");
            KnConfigurationsManager configManager = KnConfigurationsManager.getInstance(KnProvConstants.LIBRARY_NAME);
            knLogger.debug("static", "Completed Initializing config manager!");
            isInitialized = configManager.isInitialized();
        } catch (Exception e) {
            knLogger.fatal("static", "Initialization Failed", e);
            exception = e;
            e.printStackTrace();
        }
    }

    /**
     * Constructor. All the clients will invoke this constructor.
     * This constructor may throw a KnXDMSystemError if the initialization
     * is not done properly.
     */
    private KnProvClientImpl() {
        if (!isInitialized) {
            throw new KnXDMSystemError(KnErrorCodes.Initializer.PROV_INIT_FAILED, "Library Initialization Failed - " + exception,
                    exception);
        }
    }

    public static synchronized KnProvClientImpl getInstance() {
        if (instance == null) {
            instance = new KnProvClientImpl();
        }
        return instance;
    }

    /**
     * this method returns the singleton instance of KnSubsProvManager Class
     * This method will provide access to all the SubsProv Manager API's
     *
     * @return an instance of SubsProvManager
     */
    public ISubsProvManager getSubsProvManager() throws KnProvException {
        if (this.subsProvManager == null) {
            this.subsProvManager = new KnSubsProvManager();
        }
        return this.subsProvManager;
    }


    public KnOPCreateSubsInfoDTO createSubscriber(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().createSubscriber(subsProfileInputDTO, persisterTxn);
    }


    public KnOPUpdateSubsInfoDTO updateSubscriber(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().updateSubscriber(subsProfileInputDTO, persisterTxn);
    }

    public KnOPUpdateSubsInfoDTO updateSubscriberUserAgent(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().updateSubscriberUserAgent(subsProfileInputDTO, persisterTxn);
    }

    public KnOPDeleteSubsRespDTO deleteSubscriber(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().deleteSubscriber(subscriberDTO, persisterTxn);
    }

    public KnOPSubsProfileInfoDTO getSubscriberDetails(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().getSubscriberDetails(subscriberDTO, persisterTxn);
    }

    public KnOPSubsProfileInfoDTO getSubscriberDetailsBasic(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().getSubscriberDetailsBasic(subscriberDTO, persisterTxn);
    }

    @Override
    public KnOPSubsProfileInfoDTO getSubscriberIfExist(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        return this.getSubsProvManager().getSubscriberIfExist(mdn, persisterTxn);
    }

    @Override
    public KnOPSubsProfileInfoDTO getSubsDetails(KnIPSubscriberInfoDTO subscriberDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().getSubsDetails(subscriberDTO, readOnly, persisterTxn);
    }

    public KnOPSubsProfileInfoDTO changeMDN(KnIPChangeMDNInfoDTO changeMDNInfoDTO,boolean isAsyncCall, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().changeMDN(changeMDNInfoDTO,isAsyncCall, persisterTxn);
    }

    public KnOPSubsProfileInfoDTO forceSync(KnIPSubscriberInfoDTO subscriberInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().forceSync(subscriberInfoDTO, persisterTxn);
    }

    public KnOPProvDTO sendConfigDocNotification(String mdn, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().sendConfigDocNotification(mdn, persisterTxn);
    }

    public KnOPActivationInfoDTO activateSubscriber(KnIPActivateMDNInfoDTO activateMdnInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().activateSubscriber(activateMdnInfoDTO, persisterTxn);
    }

    @Override
    public KnOPActivationInfoDTO selectProfileMdn(KnIPSelectProfileMdnDTO selectProfileMdnDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().selectProfileMdn(selectProfileMdnDTO, persisterTxn);
    }

    public KnOPSubsConfigDocInfoDTO getSubscriberConfigDocument(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().getSubscriberConfigDocument(subscriberDTO, persisterTxn);
    }

    public KnOPChgAuthStatusRespDTO changeServiceAuthStatus(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().changeServiceAuthStatus(subsProvInfoDTO, persisterTxn);
    }

    public KnOPProvDTO removeSubsInfo(KnIPChangeMDNInfoDTO changeMDNInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().removeSubsInfo(changeMDNInfoDTO, persisterTxn);
    }

    public KnOPSubsProfileInfoDTO getDefaultSubscriberProfile(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().getSubscriberDetails(subscriberDTO, persisterTxn);
    }

    public KnOPProvDTO deleteCorporateProfile(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().deleteCorporateProfile(corpProfileInfoDTO, persisterTxn);
    }

    public KnOPProvDTO updateExtCorporateID(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().updateExtCorporateID(corpProfileInfoDTO, persisterTxn);
    }

    public KnOPCreatePAMAccountDTO createPAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().createPAMAccount(pamAccInfoDTO, persisterTxn);
    }

    public KnOPDeletePAMAccountDTO deletePAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().deletePAMAccount(pamAccInfoDTO, persisterTxn);
    }

    public KnOPProvDTO createPAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().createPAMSubsProfile(pamAccInfoDTO, persisterTxn);
    }

    public KnOPProvDTO updatePAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().updatePAMSubsProfile(pamAccInfoDTO, persisterTxn);
    }

    public KnOPProvDTO deletePAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().deletePAMSubsProfile(pamAccInfoDTO, persisterTxn);
    }

    public KnOPPAMAccInfoDTO getPAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().getPAMSubsProfile(pamAccInfoDTO, persisterTxn);

    }

    public KnOPCreateSubsInfoDTO createSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().createSubscribers(bulkSubsProvInfoDTO, persisterTxn);
    }


    public KnOPUpdateSubsInfoDTO updateSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().updateSubscribers(bulkSubsProvInfoDTO, persisterTxn);
    }

    public KnOPBulkDeleteSubsRespDTO deleteSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, boolean isLastMDN, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().deleteSubscribers(bulkSubsProvInfoDTO, isLastMDN, persisterTxn);
    }

    public KnOPBulkChgAuthStatusRespDTO changeServiceAuthStatuses(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().changeServiceAuthStatuses(bulkSubsProvInfoDTO, persisterTxn);
    }

    public List<String> retrievePAMAccountMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().retrievePAMAccountMDNs(pamAccId, persisterTxn);
    }

    public List<KnOPSubsProfileInfoDTO> retrievePAMAccountMDNsDetails(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().retrievePAMAccountMDNsDetails(pamAccId, persisterTxn);
    }

    public List<String> retrievePAMAccountMDNs(int pamAccId, int fetchSize, String listMdn, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().retrievePAMAccountMDNs(pamAccId, fetchSize, listMdn, persisterTxn);
    }

    public List<String> retrievePAMAccountMDNs(int pamAccId, String startMdn, String endMdn, int fetchSize, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().retrievePAMAccountMDNs(pamAccId, startMdn, endMdn, fetchSize, persisterTxn);
    }

    public KnOPUpdatePAMAccountDTO updatePAMAccState(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().updatePAMAccState(pamAccInfoDTO, persisterTxn);

    }

    public KnOPCreateSubsInfoDTO validateCreateSubscriber(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, int subsCount, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().validateCreateSubscriber(bulkSubsProvInfoDTO, subsCount, persisterTxn);
    }

    public KnOPPAMAccInfoDTO getPAMAccountInfo(String extPAMAccId, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().getPAMAccountInfo(extPAMAccId, persisterTxn);

    }

    public KnOPPAMAccInfoDTO getPAMAccInfoFromId(int pamAccId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().getPAMAccInfoFromId(pamAccId, readOnly, persisterTxn);

    }

    public KnOPUpdatePAMAccountDTO updateCorpName(String extCorpId, String corpName, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().updateCorpName(extCorpId, corpName, persisterTxn);
    }

    @Override
    public KnOPUpdateSubsInfoDTO validateUpdateSubscriber(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().validateUpdateSubscriber(bulkSubsProvInfoDTO, persisterTxn);
    }

    @Override
    public KnOPUpdatePAMAccountDTO updatePAMAccMaxSub(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().updatePAMAccMaxSub(pamAccInfoDTO, persisterTxn);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> retrievePAMAccountProvMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().retrievePAMAccountProvMDNs(pamAccId, persisterTxn);
    }

    @Override
    public List<String> getPamAccLastSequenceMdns(int pamAccId, int start, int end, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().getPamAccLastSequenceMdns(pamAccId, start, end, persisterTxn);
    }

    @Override
    public KnOPBulkRespDTO updateDispForSubscribers(Map<String, Integer> subscribers, int bitNo, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().updateDispForSubscribers(subscribers, bitNo, persisterTxn);
    }


    @Override
    public KnOPProvDTO createExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().createExtSubscriber(extSubsInfoDTO, persisterTxn);
    }

    @Override
    public KnOPProvDTO deleteExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().deleteExtSubscriber(extSubsInfoDTO, persisterTxn);
    }

    @Override
    public KnOPProvDTO updateExtSubscriber(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().updateExtSubscriber(extSubsInfoDTO, persisterTxn);
    }

    @Override
    public KnExtSubscriberInfoDTO getExtSubscriberInfo(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().getExtSubscriberInfo(extSubsInfoDTO, persisterTxn);
    }

    @Override
    public List<Integer> getCorpIdsForExtSubscriber(String mdn, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().getCorpIdsForExtSubscriber(mdn, persisterTxn);
    }

    @Override
    public KnOPProvDTO addOrModifyExtSubscribers(KnExtSubscriberInfoDTO extSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().addOrModifyExtSubscribers(extSubsInfoDTO, persisterTxn);
    }

    @Override
    public void validateUpgradePAMAccount(int maxSub, KnPersisterTxn persisterTxn) throws KnProvException {
        this.getSubsProvManager().validateUpgradePAMAccount(maxSub, persisterTxn);
    }

    @Override
    public KnOPUpdatePAMAccountDTO migratePAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvException, KnFWException {
        return this.getSubsProvManager().migratePAMAccount(pamAccInfoDTO, persisterTxn);
    }

    @Override
    public int retrieveCorporationId(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().retrieveCorporationId(extCorpId, persisterTxn);
    }

    @Override
    public KnOPUpdatePAMAccountDTO validateUpdatePamAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
       // this.getSubsProvManager().validateUpdatePamAccount(pamAccInfoDTO, persisterTxn);
        return  this.getSubsProvManager().validateUpdatePamAccount(pamAccInfoDTO, persisterTxn);
    }

    @Override
    public void updatePAMEtag(int pamAccID, KnPersisterTxn persisterTxn) throws KnProvException {
        this.getSubsProvManager().updatePAMEtag(pamAccID, persisterTxn);
    }

    @Override
    public KnOPProvDTO createSubscrRoamingProfiles(String mdn, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().createSubscrRoamingProfiles(mdn, persisterTxn);
    }

    @Override
    public KnOPProvDTO deleteSubscrRoamingProfiles(String mdn, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().deleteSubscrRoamingProfiles(mdn, persisterTxn);
    }

    @Override
    public KnOPCorpProfileInfoDTO retrieveCorporateProfile(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().retrieveCorporateProfile(extCorpId, persisterTxn);
    }

    @Override
    public KnOPCorpProfileInfoDTO createCorpProfile(KnIPCorpProfileInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().createCorpProfile(corpProfileInfoDTO, persisterTxn);
    }

    @Override
    public List<Integer> retrievePAMAccIdForCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().retrievePAMAccIdForCorpId(corpId, persisterTxn);
    }

    @Override
    public List<KnOPUpdateSubsInfoDTO> updateHierarchy(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().updateHierarchy(bulkSubsProvInfoDTO, persisterTxn);
    }

    /*@Override
    public List<KnOPProvDTO> sendBulkConfigDocNotification(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().sendBulkConfigDocNotification(mdnList,persisterTxn);
    }*/

    @Override
    public boolean validateCorpSubsLimitAndPairLimit(int corpId, int subsCount, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().validateCorpSubsLimitAndPairLimit(corpId, subsCount, persisterTxn);
    }

    @Override
    public List<String> retrieveBanMDNs(int banId, HIERARCHY_TYPE hierarchyType, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().retrieveBanMDNs(banId, hierarchyType, persisterTxn);
    }

    @Override
    public KnOPProvDTO updatePAMSubsProfCorpId(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, int oldCorpId, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().updatePAMSubsProfCorpId(bulkSubsProvInfoDTO, oldCorpId, persisterTxn);
    }

    @Override
    public KnOPProvDTO updateCorpProfileLastUpdateTime(int corpId, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().updateCorpProfileLastUpdateTime(corpId, persisterTxn);
    }

    @Override
    public KnOPPAMAccInfoDTO retrievePAMSubsProfInfo(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().retrievePAMSubsProfInfo(pamAccInfoDTO, persisterTxn);
    }

    @Override
    public KnOPUpdatePAMAccountDTO updatePAMAccName(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().updatePAMAccName(pamAccInfoDTO, persisterTxn);
    }

    @Override
    public KnOPProvDTO updateEtagForNNISubscr(String extCorpId, int clientType, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().updateEtagForNNISubscr(extCorpId, clientType, persisterTxn);
    }

    @Override
    public boolean isMdnExistsInPseudoPoolNPamAccInfo(String mdn, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().isMdnExistsInPseudoPoolNPamAccInfo(mdn, persisterTxn);
    }

	@Override
	public KnOPUpdateSubsInfoDTO updateAutoPairing(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException {
		  return this.getSubsProvManager().updateAutoPairing(subsProfileInputDTO, persisterTxn);
	}

	@Override
	public KnOPSubsProfileInfoDTO createTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
		return this.getSubsProvManager().createTPUser(tpUserInfoDTO, persisterTxn);
	}

	@Override
	public KnOPSubsProfileInfoDTO updateTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
		return this.getSubsProvManager().updateTPUser(tpUserInfoDTO, persisterTxn);
	}

	@Override
	public KnOPProvDTO deleteTPUser(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException {
		return this.getSubsProvManager().deleteTPUser(tpUserInfoDTO, persisterTxn);
	}

    @Override
	public List<String> getPAMAccountMdnsByInsertionTime(int pamAccId, long insertionTime, KnPersisterTxn persisterTxn) throws KnProvException {
		  return this.getSubsProvManager().getPAMAccountMdnsByInsertionTime(pamAccId, insertionTime, persisterTxn);
    }
    @Override
    public Map<String, String> fetchActiveFSForBulkMdns(List<String> mdns, KnPersisterTxn persisterTxn) throws KnProvException {
         return this.getSubsProvManager().fetchActiveFSForBulkMdns(mdns, persisterTxn);
    }

    @Override
    public KnOPUpdateSubsInfoDTO switchConvergedClient(KnIPSubsProvInfoDTO subsProvInputDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().switchConvergedClient(subsProvInputDTO, persisterTxn);
    }

    @Override
    public KnSysConfigRespDTO getSysConfig(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().getSysConfig(subsProfileInputDTO, persisterTxn);
    }

    @Override
    public KnOPBulkRespDTO updateBulkSubsFSAndPkgIds(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().updateBulkSubsFSAndPkgIds(bulkSubsProvInfoDTO,persisterTxn);
    }

	@Override
	public KnOPUpdateSubsInfoDTO updateSubscriberFSAndPkgCodes(KnIPSubsProvInfoDTO subsProfileInputDTO,
			KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
		// TODO Auto-generated method stub
		return this.getSubsProvManager().updateSubscriberFSAndPkgCodes(subsProfileInputDTO, persisterTxn);
	}

	@Override
	public void verifyTPAccount(KnTPUserInfoDTO tpUserInfoDTO, KnPersisterTxn persisterTxn)
			throws KnProvException {
		 this.getSubsProvManager().verifyTPAccount(tpUserInfoDTO, persisterTxn);
	}

    @Override
    public KnOPProvDTO actionOnTGSSDoc(String mdn, String newActiveFS, String oldActiveFS,KnPersisterTxn persisterTxn) throws KnProvException{
        return this.getSubsProvManager().actionOnTGSSDoc(mdn, newActiveFS, oldActiveFS, persisterTxn);
    }

    @Override
    public KnOPProvDTO actionOnTGSSDocCust(String mdn, String newActiveFS, String oldActiveFS,KnPersisterTxn persisterTxn) throws KnProvException{
        return this.getSubsProvManager().actionOnTGSSDocCust(mdn, newActiveFS, oldActiveFS, persisterTxn);
    }

	@Override
	public KnOPSubsProfileInfoDTO searchCorpAddressBook(KnIPSubscriberInfoDTO subscriberDTO,
			KnPersisterTxn persisterTxn) throws KnProvException {
		return this.getSubsProvManager().searchCorpAddressBook(subscriberDTO, persisterTxn);
	}

    @Override
    public KnOPSubsProfileInfoDTO updatePrivacyOptStatus(KnIPSubscriberInfoDTO subscriberDTO,
                                                        KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().updatePrivacyOptStatus(subscriberDTO, persisterTxn);
    }
    @Override
    public void updateMCSIds(KnPersisterTxn persisterTxn, KnSubsProfilePersistDTO subsProfilePersistDTO) throws KnProvBOException, KnProvException, KnDAOException
    {
    	this.getSubsProvManager().updateMCSIds(persisterTxn, subsProfilePersistDTO);
    }

    @Override
    public KnXDMProfileIdMdnMapRespDTO getMdnProfileIdsForMcPttIds(KnIPSubscriberInfoDTO subscriberDTO, boolean readOnly,
                                                                   KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        return this.getSubsProvManager().getMdnProfileIdsForMcPttIds(subscriberDTO, readOnly, persisterTxn);

    }

    @Override
    public String getMdnForUnassign(String baseMdn, String userProfileId, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().getMdnForUnassign(baseMdn,userProfileId, persisterTxn);
    }

    @Override
    public KnOPCreateSubsInfoDTO createSubscriberForAssignUserProfile(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().createSubscriberForAssignUserProfile(subsProfileInputDTO, persisterTxn);
    }

    @Override
    public KnOPUpdateSubsInfoDTO updateSubscriberUserProfileFS(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().updateSubscriberUserProfileFS(subsProfileInputDTO, persisterTxn);
    }

    @Override
    public KnOPUpdateSubsInfoDTO updateSubscrTS(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().updateSubscrTS(subsProfileInputDTO, persisterTxn);
    }
	
	@Override
	public KnOPCreateSubsInfoDTO createDevice(KnXDMDeviceProvInfoDTO deviceProfileInfoInputDTO,
			KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
		 return this.getSubsProvManager().createDevice(deviceProfileInfoInputDTO, persisterTxn);
	}

	@Override
	public KnXDMDeviceProvDTO getDeviceInfo(String deviceId, KnPersisterTxn persisterTxn)
			throws KnDAOException, KnProvException {
		 return this.getSubsProvManager().getDeviceInfo(deviceId, persisterTxn);
	}

	@Override
	public void deleteDeviceInfo(KnXDMDeviceProvDTO deviceId, KnPersisterTxn persisterTxn)
			throws KnDAOException, KnProvException {
		 this.getSubsProvManager().deleteDeviceInfo(deviceId, persisterTxn);
    }

    @Override
    public KnOPUpdateSubsInfoDTO modifyDevice(KnXDMDeviceProvInfoDTO deviceProfileInfoInputDTO,
                                              KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        return this.getSubsProvManager().modifyDevice(deviceProfileInfoInputDTO, persisterTxn);
    }

	@Override
	public List<String> getMdnForUPM(String baseMdn, KnPersisterTxn persisterTxn) throws KnProvBOException,KnProvException{
		 return this.getSubsProvManager().getMdnForUPM(baseMdn, persisterTxn);
	}
	
	@Override
	public void loginNotifyEvent(KnXDMLoginNotifyEventReqDTO loginNotifyEventReqDTO, KnPersisterTxn persisterTxn)
			throws KnProvException, KnFWException {
		 this.getSubsProvManager().loginNotifyEvent(loginNotifyEventReqDTO, persisterTxn);
	}

    @Override
    public KnOPMCPTTPermissionDTO getAuthorizedUserList(KnIPMCPTTPermissionDTO tuInfo, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().getAuthorizedUserList(tuInfo, persisterTxn);
    }

    @Override
    public KnOPSubsProfileInfoDTO getUserprofileidsByProfileMdns(KnIPSubscriberInfoDTO subscriberDTO, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().getUserprofileidsByProfileMdns(subscriberDTO,persisterTxn);
    }

    @Override
    public KnXDMSubsProfileRespDTO getSubscrClientSettings(KnIPSubsProvInfoDTO subscrClientSettings, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException  {
        return this.getSubsProvManager().getSubscrClientSettings(subscrClientSettings,persisterTxn);
    }

    @Override
    public KnOPUpdateSubsInfoDTO setSubscrClientSettings(KnIPSubsProvInfoDTO subsProfileInputDTO, KnPersisterTxn persisterTxn) throws KnProvException, KnFWException {
        return this.getSubsProvManager().setSubscrClientSettings(subsProfileInputDTO, persisterTxn);
    }
    public KnOPCreateSubsInfoDTO createCorpAccount(KnXDMCorpProfileInfoDTO corpAccountDto,
                                                   KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        return this.getSubsProvManager().createCorpAccount(corpAccountDto, persisterTxn);
    }

    @Override
    public KnOPCreateSubsInfoDTO updateCorpAccount(KnXDMCorpProfileInfoDTO corpAccountDto, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        return this.getSubsProvManager().updateCorpAccount(corpAccountDto, persisterTxn);
    }

    @Override
    public KnOPDeleteSubsRespDTO deleteCorpAccount(KnXDMCorpInfoDTO corpProfileInfoDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException, KnProvException{
        return this.getSubsProvManager().deleteCorpAccount(corpProfileInfoDTO, persisterTxn);
    }

    @Override
    public KnCorporateProfilepersistDTO1 getCorporateAccountDetails(KnXDMDeviceProvInfoDTO xdmRequestDTO, KnPersisterTxn persisterTxn) throws KnException {
        return this.getSubsProvManager().getCorporateAccountDetails(xdmRequestDTO, persisterTxn);
    }
    @Override
    public KnXDMCorpAccountsListDTO retrieveCorporationAccountsList(KnPersisterTxn persisterTxn,String fetchSize, String nextToken) throws KnDAOException, KnProvException {
        return this.getSubsProvManager().retrieveCorporationAccountsList(persisterTxn,fetchSize,nextToken);
    }

    public String getBaseMdnByProfileMdn(String mdn, KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().getBaseMdnByProfileMdn(mdn, persisterTxn);
    }

    @Override
    public KnUserLoginResponseDTO lockRequestErrorProcessor(KnOPSubsProfileInfoDTO subscriberDTO, KnXDMActivateInfoDTO activateInfoDTO,KnPersisterTxn persisterTxn) throws KnProvException {
        return this.getSubsProvManager().lockRequestErrorProcessor(subscriberDTO,activateInfoDTO,persisterTxn);
    }


    @Override
    public Map<String, Integer> getSubscriberServiceAuthStatus(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        return this.getSubsProvManager().getSubscriberServiceAuthStatus(mdns, persisterTxn);
    }
    @Override
    public KnXDMExtGWProfileListDTO retrieveExtGWProfileList() throws KnDAOException, KnProvException {
        return this.getSubsProvManager().retrieveExtGWProfileList();
    }
    @Override
    public int getSubscriberServiceAuthStatusByUserId(String userId, KnPersisterTxn persisterTxn) throws KnDAOException, KnProvException {
        return this.getSubsProvManager().getSubscriberServiceAuthStatusByUserId(userId, persisterTxn);
    }

}
