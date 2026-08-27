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
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnCommonContactListValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCommonContactListValidationRule.class);


    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnSublistDetailsPersistDTO) {
            KnSublistDetailsPersistDTO corpSublistPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;

            boolean isNonAutoPair = corpSublistPersistDTO.isNonAutoPair();
            boolean isListDistribution = corpSublistPersistDTO.isListDistribution();

            boolean sysCommonContactEnabled = corpSublistPersistDTO.isCommContactListSupp();
            boolean isCommonSublist = corpSublistPersistDTO.isCommonContactList();
            int distPolicy = corpSublistPersistDTO.getDistributionPolicy();

            int sysCommonContactListSize = corpSublistPersistDTO.getCmnContactListSize();
            int totalCommonContactListSize = corpSublistPersistDTO.getTotalCommonContactListMemCount();
            knLogger.debug(methodName, "sysCommonContactEnabled: ", sysCommonContactEnabled," isCommonSublist:",isCommonSublist,
                    " sysCommonContactListSize:",sysCommonContactListSize,
                    " totalCommonContactListSize:",totalCommonContactListSize);
            knLogger.debug(methodName," isNonAutoPair:",isNonAutoPair," isListDistribution:",isListDistribution);

            if(!(distPolicy == KnConstants.DIST_POLICY_USER_PROFILE) && isListDistribution && !isNonAutoPair){
                knLogger.debug(methodName, "AutoPairing is enabled");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.AUTO_PAIRING_ENABLED,
                        "Auto paring should be disabled for common sublist", getEntityId(), getOperationType(), getRuleId(), "", "");
            }
            if (isCommonSublist && !sysCommonContactEnabled) {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.COMMON_CONTACT_LIST_SUPPORT_FLAG_DISABLED,
                        "Common sublist feature is disabled at system or corporate level", getEntityId(), getOperationType(), getRuleId(), "", "");
            }

            if((isCommonSublist && sysCommonContactEnabled)&&(totalCommonContactListSize>sysCommonContactListSize)){
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.COMMON_CONTACT_LIST_LIMIT_EXCEEDED,
                        "Common sublist contact mdn size exceeded", getEntityId(), getOperationType(), getRuleId(), "", "");
            }

        }
    }
}
