/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.ggcache.dto.KnCorpTrustMatrixDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupsharing.KnCorpSharedCorpTrustMatrixValidator;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

public class KnCorpGroupMemberExternalValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupMemberExternalValidator.class);
    final String methodName = "validate()";

    @Override
    public void validate() throws KnValidationException, KnBOException {
        knLogger.debug("Inside the external validator code");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "Bean is instance of KnCorpGroupInfoPersistDTO");
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            Collection<KnCorpSubscriberDTO> pocSUbsInfo = groupPersistDTO.getPocSubsInfo();
            List<KnCorpSubscriberDTO> externalMembersToBeAdded = groupPersistDTO.getAddedExternalMembers();
            if (null != externalMembersToBeAdded && !externalMembersToBeAdded.isEmpty()) {
                Set<Integer> trustMatrix = new HashSet<>();
                if (groupPersistDTO.getTrustMatrixMap() != null) {
                    trustMatrix = groupPersistDTO.getTrustMatrixMap().keySet();
                }
                List<String> externalSubsList = groupPersistDTO.getMdnsPresentInExternalInfo();
                List<String> invalidExternalMdns = new ArrayList<>();
                Set<Integer> finalTustMatrix = trustMatrix;
                externalMembersToBeAdded.stream().filter(subs -> !(finalTustMatrix.contains(subs.getCorpId()) ||
                        externalSubsList.contains(subs.getMdn()))).forEach(e -> invalidExternalMdns.add(e.getMdn()));
                knLogger.debug(methodName,"Invalid mdn(s) present in the request ",invalidExternalMdns);
                if (null != invalidExternalMdns && !invalidExternalMdns.isEmpty()) {
                    knLogger.debug(methodName, "Mdn(s) not external to the corp and not part of shared corp are ", invalidExternalMdns);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_NOT_PART_CORP_NOR_EXTERNAL,
                            "Mdn(s) not external to the corp and not part of shared corp are", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
            }

        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            knLogger.debug(methodName, "Bean is instance of KnCorpBCGrpPersistDTO");
            KnCorpBCGrpPersistDTO groupPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            Collection<KnCorpSubscriberDTO> pocSUbsInfo = groupPersistDTO.getPocSubsInfo();
            List<KnCorpSubscriberDTO> externalMembersToBeAdded = groupPersistDTO.getAddedExternalMembers();
            if (null != externalMembersToBeAdded && !externalMembersToBeAdded.isEmpty()) {
                Set<Integer> trustMatrix = new HashSet<>();
                if (groupPersistDTO.getTrustMatrixMap() != null) {
                    trustMatrix = groupPersistDTO.getTrustMatrixMap().keySet();
                }
                List<String> externalSubsList = groupPersistDTO.getMdnsPresentInExternalInfo();
                List<String> invalidExternalMdns = new ArrayList<>();
                Set<Integer> finalTustMatrix = trustMatrix;
                externalMembersToBeAdded.stream().filter(subs -> !(finalTustMatrix.contains(subs.getCorpId()) ||
                        externalSubsList.contains(subs.getMdn()))).forEach(e -> invalidExternalMdns.add(e.getMdn()));
                knLogger.debug(methodName,"Invalid mdn(s) present in the request ",invalidExternalMdns);
                if (null != invalidExternalMdns && !invalidExternalMdns.isEmpty()) {
                    knLogger.debug(methodName, "Mdn(s) not external to the corp and not part of shared corp are ", invalidExternalMdns);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_NOT_PART_CORP_NOR_EXTERNAL,
                            "Mdn(s) not external to the corp and not part of shared corp are", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
            }
        }

    }
}
