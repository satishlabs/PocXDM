/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted
 * *************************************************************************************************
 * <p>
 * File name:  KnSelfDndPrivilegeValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Jusbin Mathew             10-Nov-2023                  13.0
 * <p>
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpRecordingFsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;

import static com.kodiak.xdms.server.corpmgmt.resources.KnEntityTypes.CORP_SUBS_PROFILE_MANAGER;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.SYSTEM_LEVEL_SELF_DND_PRIVILEGE_FLAG_DISABLED;

public class KnSelfDndPrivilegeValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSelfDndPrivilegeValidationRule.class);
    public static Integer ENABLED = 1;
    public static Integer DISABLED = 0;

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY Validating selfDndPrivilege flag ");
        boolean sysSelfDndPrivilege = false;
        boolean corpSelfDndPrivilege = false;
        int reqSelfDndPrivilegeVal = 0;

        if (persistDTO instanceof KnContactDetailsPersistDTO) {
            KnContactDetailsPersistDTO contactPersistDto = (KnContactDetailsPersistDTO) persistDTO;
            sysSelfDndPrivilege = contactPersistDto.isSysSelfDndPrivilege();
            corpSelfDndPrivilege = contactPersistDto.isCorpSelfDndPrivilege();
            reqSelfDndPrivilegeVal = contactPersistDto.getReqSelfDndPrivilegeList().contains(ENABLED) ? ENABLED : DISABLED;
        }

        if (persistDTO instanceof KnCorpRecordingFsPersistDTO) {
            KnCorpRecordingFsPersistDTO corpRecordingFsPersistDTO = (KnCorpRecordingFsPersistDTO) persistDTO;
            sysSelfDndPrivilege = corpRecordingFsPersistDTO.getSysSelfDnDPrivilege();
            reqSelfDndPrivilegeVal = null != corpRecordingFsPersistDTO.getReqSelfDnDPrivilege() ? Integer.parseInt(corpRecordingFsPersistDTO.getReqSelfDnDPrivilege()) : DISABLED;
            corpSelfDndPrivilege = true;
        } else if (persistDTO instanceof KnCorpUserProfilePersistDTO) {
            KnCorpUserProfilePersistDTO corpUserProfilePersistDto = (KnCorpUserProfilePersistDTO) persistDTO;
            sysSelfDndPrivilege = corpUserProfilePersistDto.isSysSelfDndPrivilege();
            corpSelfDndPrivilege = corpUserProfilePersistDto.isCorpSelfDndPrivilege();
            reqSelfDndPrivilegeVal = corpUserProfilePersistDto.getReqSelfDndPrivilege();
        }

        if (reqSelfDndPrivilegeVal == ENABLED && (!sysSelfDndPrivilege || !corpSelfDndPrivilege)) {
            knLogger.error(methodName, " Corp/System level selfDndPrivilege flag is disabled");
            throw new KnCorpBOValidationException(SYSTEM_LEVEL_SELF_DND_PRIVILEGE_FLAG_DISABLED,
                    " Corp/System level selfDndPrivilege flag is disabled ", CORP_SUBS_PROFILE_MANAGER,
                    SYSTEM_LEVEL_SELF_DND_PRIVILEGE_FLAG_DISABLED, "", "", "");
        }

        knLogger.debug(methodName, "EXIT: Validation Completed Successfully");
    }
}
