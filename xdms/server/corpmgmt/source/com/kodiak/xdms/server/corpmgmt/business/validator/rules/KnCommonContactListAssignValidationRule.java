/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.BOEntity.SUBLISTS_LIST_DOES_NOT_EXIST;

public class KnCommonContactListAssignValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCommonContactListAssignValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnSublistDetailsPersistDTO) {
            KnSublistDetailsPersistDTO corpSublistPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
            knLogger.debug(methodName, " corpSublistPersistDTO:", corpSublistPersistDTO);

            //assign validation
            if (corpSublistPersistDTO.getOperationType().equals(KnOperationTypes.ASSIGN_COMMON_CONTACT_LIST)) {
                boolean isCommonContactListEnabled = corpSublistPersistDTO.isCommContactListSupp();
                if (!isCommonContactListEnabled) {
                    knLogger.error(methodName, " Common sublist feature is disabled at system or corporate level:", isCommonContactListEnabled);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.COMMON_CONTACT_LIST_SUPPORT_FLAG_DISABLED,
                            "Common sublist feature is disabled at system or corporate level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
            }

            //common validation mdn not part of corporate
            int reqCorpId = corpSublistPersistDTO.getCorpId();
            int subListCorpId = corpSublistPersistDTO.getSubListCorpId();
            Set<Integer> subscrsCorpIds = corpSublistPersistDTO.getSubscrsCorpIds();
            Set<Integer> reqCorpIdSet = new HashSet<>();
            reqCorpIdSet.add(reqCorpId);
            //boolean containAllSameCorpIds = subscrsCorpIds.stream().allMatch(subscrsCorpIdsSet::contains);
            boolean containAllSameCorpIds = reqCorpIdSet.containsAll(subscrsCorpIds);
            knLogger.debug(methodName, " subscrsCorpIds:", subscrsCorpIds
                    , "subListCorpId ", subListCorpId, " containAllSameCorpIds:", containAllSameCorpIds);
            if (!containAllSameCorpIds) {
                knLogger.error(methodName, " Common sublist and contact doesn't belong to same group : subscrsCorpIds:"
                        , subscrsCorpIds, " reqCorpIdSet:", reqCorpIdSet);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                        "Common sublist and contact doesn't belong to same group", getEntityId(), getOperationType(), getRuleId(), "", "");
            }


            if (corpSublistPersistDTO.getOperationType().equals(KnOperationTypes.ASSIGN_COMMON_CONTACT_LIST)) {
                int sysMaxAllowedCommCnctListPerSub = corpSublistPersistDTO.getSysMaxAllowedCommCnctListPerSub();
                Map<String, Integer> commCnctListCountPerSub = corpSublistPersistDTO.getCommCnctListCountPerSub();
                boolean sizeExceeded = false;
                knLogger.debug(methodName, "commCnctListCountPerSub:", commCnctListCountPerSub, " commCnctListCountPerSub ", commCnctListCountPerSub);
                if (commCnctListCountPerSub != null && !commCnctListCountPerSub.isEmpty()) {
                    sizeExceeded = commCnctListCountPerSub.values().stream().filter(size -> size >= sysMaxAllowedCommCnctListPerSub).findFirst().isPresent();
                }

                if (sizeExceeded) {
                    knLogger.error(methodName, " Common sublist contact mdn size exceeded :");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.COMMON_CONTACT_LIST_LIMIT_PER_SUB_EXCEEDED,
                            "Common sublist contact mdn size exceeded", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
            }

            //Unassign validation
            if (corpSublistPersistDTO.getOperationType().equals(KnOperationTypes.UNASSIGN_COMMON_CONTACT_LIST)) {
                //sublist not part of corporate
                if (reqCorpId != subListCorpId) {
                    knLogger.error(methodName, " Common sublist not belonging to the request corporate"
                            , reqCorpId, " subListCorpId:", subListCorpId);
                    throw new KnCorpBOValidationException(SUBLISTS_LIST_DOES_NOT_EXIST,
                            "Common sublist not belonging to the request corporate", getEntityId(), getOperationType(), getRuleId(), "", "");
                }

                //mdn not part of sublist
                Collection<String> unAssignedSublistMdns = corpSublistPersistDTO.getUnAssignedSublistMdns();
                if (unAssignedSublistMdns != null && !unAssignedSublistMdns.isEmpty()) {
                    knLogger.error(methodName, " unassign mdn is not part of the common sublist:"
                            , " unAssignedSublistMdns:", unAssignedSublistMdns);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.UNASSIGN_MDN_NOT_ASSGNED_TO_SUBLIST,
                            "unassign mdn is not part of the common sublist", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
            }
        }
    }
}
