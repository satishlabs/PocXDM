/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  ICorpAuthManager.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Feb 24, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.clientintf;

import com.kodiak.common.commdto.request.KnXDMCorpInfoDTO;
import com.kodiak.common.commdto.request.KnIPCatPermissionSetDTO;
import com.kodiak.common.commdto.response.KnXDMCorpUserProfileRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.ggcache.dto.KnCorpGrpLmrExtnDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface ICorpGenericManager {

    public KnCorpAuthInfoRespDTO authenticate(KnIPCorpAuthInfoDTO authInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpDirInfoRespDTO getSubsDirectory(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpInfoResDTO updateSubscriber(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO forceSync(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpInfoResDTO deleteSubscriber(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO changeMdn(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO createSubscriber(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn);

    public KnCorpPAMSubsDTO getUnusedSubsList(KnIPCorpPAMSubsDTO pamSubsDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO cleanCorpData(KnIPCorpPAMSubsDTO pamSubsDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO modifySubsCorpFeature(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpProfileInfoRespDTO getCorporateProfile(KnIPSubsProvInfoDTO subsProvInfoDTO, KnPersisterTxn persisterTxn);

    public Long calculateFeatureBit(int corpid,KnPersisterTxn persisterTxn)throws Exception;

    public KnCorpLITargetInfoRespDTO getLITargetInfo(KnPersisterTxn persisterTxn);

    public void subsEtagUpdate(String mdn,String operationType);

    public KnCorpAuthInfoRespDTO getCorpProfileByEntities(KnIPCorpAuthInfoDTO authInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpPoCSvcConfigRespDTO getPoCConfig(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn);

    public KnCorpSharedList getSharedCorpTrustMatrix(KnIPCorpAuthInfoDTO requestDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO updateCorpTrustMatrix(KnIPCorpAuthInfoDTO requestDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO deleteCorpTrustMatrix(KnIPCorpAuthInfoDTO requestDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO updateCorporateFS(KnIPCorpInfoDTO requestDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO deleteCorporateData(KnXDMCorpInfoDTO corpInfo, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO updateSelfEtag(KnCorpResponseDTO userProfileDetails, KnPersisterTxn persisterTxn);

    public void updateSelfEtagForClone(KnCorpResponseDTO assignGroupResp,String corpIdString,String mdn,KnPersisterTxn persisterTxn);

    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTS(Set<String> mdnSet, String corpId, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO setCATAccessPermission(KnIPCatPermissionSetDTO requestDTO, KnPersisterTxn persisterTxn);

    public KnCorpResponseDTO updateEtag(int corpId, String profileMdn, KnCorpResponseDTO userProfileDetails,
                                        KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap);

    KnCorpResponseDTO deleteHierarchy(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnCorpBOException;
}
