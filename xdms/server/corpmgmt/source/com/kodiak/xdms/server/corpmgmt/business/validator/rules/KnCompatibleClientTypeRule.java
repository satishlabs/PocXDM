/*
 *  Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.
 *  All Rights Reserved
 *  Motorola Solutions Confidential Restricted
 *
 */

package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCloningPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KnCompatibleClientTypeRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCompatibleClientTypeRule.class);
    private final String CLASS = KnCompatibleClientTypeRule.class.getName();

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        try {
            IPersistenceDTO persistDTO = getDTO();
            knLogger.debug(methodName, "ENTRY - Validating the Compatible Client Type Rule  ");
            if (persistDTO instanceof KnCloningPersistDTO contactDTO) {
                if ((contactDTO.getValidationMap().containsKey(KnConstants.CLONING_VALIDATION_BIT.GROUP.value())
                        && contactDTO.getValidationMap().get(KnConstants.CLONING_VALIDATION_BIT.GROUP.value())) ||
                        (contactDTO.getValidationMap().containsKey(KnConstants.CLONING_VALIDATION_BIT.GROUP_MEMBER_PROP.value())
                                && contactDTO.getValidationMap().get(KnConstants.CLONING_VALIDATION_BIT.GROUP_MEMBER_PROP.value())) ||
                        (contactDTO.getValidationMap().containsKey(KnConstants.CLONING_VALIDATION_BIT.SCAN_LIST.value())
                                && contactDTO.getValidationMap().get(KnConstants.CLONING_VALIDATION_BIT.SCAN_LIST.value()))) {
                    List<Integer> dispatcherClientType = Arrays.asList(3, 12);
                    List<Integer> nonDispatcherClientType = Arrays.asList(1, 2, 5, 6, 10, 13, 14, 15, 16);
                    KnCorpSubscriberDTO knCorpSubscriberDTO = contactDTO.getSubscDto();
                    Map<String, Integer> mdnClientTypeMap = contactDTO.getMdnClientTypeMap();
                    int targetMdnClientType = mdnClientTypeMap.get(knCorpSubscriberDTO.getMdn());
                    mdnClientTypeMap.remove(knCorpSubscriberDTO.getMdn());
                    int sourceMdnClientType = mdnClientTypeMap.values().iterator().next();
                    if ((nonDispatcherClientType.contains(sourceMdnClientType) && !nonDispatcherClientType.contains(targetMdnClientType)) ||
                            (dispatcherClientType.contains(sourceMdnClientType) && !dispatcherClientType.contains(targetMdnClientType))) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.SOURCE_TARGET_MDN_DOES_NOT_HAVE_COMPATIBLE_CLIENT_TYPE,
                                "Source and target are not Compatible Client Type", getEntityId(), getOperationType(), getRuleId(), Arrays.asList(knCorpSubscriberDTO.getMdn()).toString(), "");
                    }
                }
            }

        } finally {
            knLogger.debug(methodName, "EXIT: Validation Completed Successfully");
        }
    }
}
