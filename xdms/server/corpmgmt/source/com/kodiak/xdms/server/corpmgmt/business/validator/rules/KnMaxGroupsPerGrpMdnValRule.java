/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

/**
 * Created by nnamita on 4/21/15.
 */
public class KnMaxGroupsPerGrpMdnValRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxGroupsPerGrpMdnValRule.class);
    private String CLASS = KnMaxGroupsPerGrpMdnValRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating SG/SGPatchMdn Count.");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        try {
            Set<String> groupMdnList = new HashSet<String>();
            Set<String> groupMdnPatchList = new HashSet<String>();
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                knLogger.debug(methodName, "DTO passed in the request is - ", groupPersistDTO);
                Collection<KnCorpSubscriberDTO> mdnList = groupPersistDTO.getAddedMdnDTO();
                knLogger.debug(methodName, " MDN List : -- ", mdnList);

                // 1. If AddedMDN is not null then only need to go for validation, otherwise only for deletion no validation required.
                // 2. grpMdns is common for SGMdn and SGMdnPatch, because of sql query. But both can not be present in
                // the same group. so we took advantage of minimal changes.

                if (mdnList != null && !mdnList.isEmpty()) {
                    mdnList.forEach(subsc -> {
                        if (subsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()) {
                            knLogger.debug(methodName, "Group MDN - ", subsc.getMdn());
                            groupMdnList.add(subsc.getMdn());
                        } else if (subsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
                            knLogger.debug(methodName, "Group MDN Patch- ", subsc.getMdn());
                            groupMdnPatchList.add(subsc.getMdn());
                        }
                    });
                    Collection<String> grpMdns = groupPersistDTO.getExistingGrpmdns();
                    //added for modify group as the existing private members details present in this varible
                    //Incase of create group it will be executed but will never fail here as group mdn cannot be part of sublist
                    if (grpMdns != null && !grpMdns.isEmpty()) {
                        if (!groupMdnList.isEmpty()) {
                            groupMdnList.addAll(grpMdns);
                        } else {
                            groupMdnPatchList.addAll(grpMdns);
                        }
                    }
                    knLogger.debug(methodName, "SG MDN List :--", groupMdnList.size(), "Max SG Per Group : -- ",
                            groupPersistDTO.getMaxSGPerGrp());
                    knLogger.debug(methodName, "SG MDN Patch List :--", groupMdnPatchList.size(), "Max SG Patch Per Group " +
                            ": -- ", groupPersistDTO.getMaxSGMdnPatchPerGroup());
                    if (!groupMdnList.isEmpty() && groupMdnList.size() > groupPersistDTO.getMaxSGPerGrp()) {
                        knLogger.debug(methodName, "Group Mdn can not exceed from MAX SG MDN GRP ", groupMdnList);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_MDN_LIMIT_EXCEEDED,
                                "Group Mdn can not exceed from MAX SG MDN GRP ", getEntityId(),
                                getOperationType(), getRuleId(), groupMdnList.toString(), "");
                    } else if (!groupMdnPatchList.isEmpty() && groupMdnPatchList.size() > groupPersistDTO.getMaxSGMdnPatchPerGroup()) {
                        knLogger.debug(methodName, "Group Mdn Patch can not exceed from MAX SG MDN Patch per GRP ", groupMdnPatchList);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_SG_PATCH_MDN_PER_GROUP_LIMIT_REACHED,
                                "Group Mdn can not exceed from MAX SG MDN Patch per GRP ", getEntityId(),
                                getOperationType(), getRuleId(), groupMdnPatchList.toString(), "");

                    }
                }
            } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
                KnCorpBCGrpPersistDTO bcGroupPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
                knLogger.debug(methodName, "BroadCast DTO passed in the request is - ", bcGroupPersistDTO);
                //executed for addedn and modify bc group
                Collection<KnCorpSubscriberDTO> members = bcGroupPersistDTO.getValidInternalCont();
                if (members != null && !members.isEmpty()) {
                    members.forEach(subsc -> {
                        if (subsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()) {
                            knLogger.debug(methodName, "Group MDN - ", subsc.getMdn());
                            groupMdnList.add(subsc.getMdn());
                        } else if (subsc.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
                            knLogger.debug(methodName, "Group MDN Patch- ", subsc.getMdn());
                            groupMdnPatchList.add(subsc.getMdn());
                        }
                    });
                    //executed for modify BC group only
                    Collection<String> existPrivateMemList = bcGroupPersistDTO.getGroupMdnList();
                    //added for modify group as the existing private members details present in this varible
                    if (existPrivateMemList != null && !existPrivateMemList.isEmpty()) {
                        if (!groupMdnList.isEmpty()) {
                            groupMdnList.addAll(existPrivateMemList);
                        } else {
                            groupMdnPatchList.addAll(existPrivateMemList);
                        }
                    }
                    knLogger.debug(methodName, "SG MDN List :--", groupMdnList.size(), "Max SG Per Group : -- ",
                            bcGroupPersistDTO.getMaxSGPerGrp());
                    knLogger.debug(methodName, "SG MDN Patch List :--", groupMdnPatchList.size(), "Max SG Patch Per Group " +
                            ": -- ", bcGroupPersistDTO.getMaxSGMdnPatchPerGroup());
                    if (!groupMdnList.isEmpty() && groupMdnList.size() > bcGroupPersistDTO.getMaxSGPerGrp()) {
                        knLogger.debug(methodName, "Group Mdn can not exceed from MAX SG MDN GRP ", groupMdnList);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_MDN_LIMIT_EXCEEDED,
                                "Group Mdn can not exceed from MAX SG MDN GRP ", getEntityId(),
                                getOperationType(), getRuleId(), groupMdnList.toString(), "");
                    } else if (!groupMdnPatchList.isEmpty() && groupMdnPatchList.size() > bcGroupPersistDTO.getMaxSGMdnPatchPerGroup()) {
                        knLogger.debug(methodName, "Group Mdn Patch can not exceed from MAX SG MDN Patch per GRP ", groupMdnPatchList);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_SG_PATCH_MDN_PER_GROUP_LIMIT_REACHED,
                                "Group Mdn can not exceed from MAX SG MDN Patch per GRP ", getEntityId(),
                                getOperationType(), getRuleId(), groupMdnPatchList.toString(), "");
                    }
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnCorpGroupInfoPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Successfully done for Max SGMdn/SGMdnPatch");
        }
    }
}
