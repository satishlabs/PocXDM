/**
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 * <p>
 * ************************************************************************
 * <p>
 * File name:  KnCommonSublistValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Jusbin Mathew           27-Feb-2023                  12.3
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 * <p>
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2022 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 * <p>
 * ************************************************************************
 * <p>
 * File name:  KnCommonSublistValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Jusbin Mathew           27-Feb-2023                  12.3
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 * <p>
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2022 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 * <p>
 * ************************************************************************
 * <p>
 * File name:  KnCommonSublistValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Jusbin Mathew           27-Feb-2023                  12.3
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 * <p>
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2022 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */
/**
 * ************************************************************************
 * <p>
 * File name:  KnCommonSublistValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Jusbin Mathew           27-Feb-2023                  12.3
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 * <p>
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2022 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCloningPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;

import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT;

public class KnCommonSublistValidationForCloningRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCommonSublistValidationForCloningRule.class);
    final String CLASS = KnMaxSubsContCountValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY Validating the max allowed contacts for the subscribers.");
        try {
            if (persistDTO instanceof KnCloningPersistDTO) {
                KnCloningPersistDTO cloningPersistDTO = (KnCloningPersistDTO) persistDTO;
                knLogger.debug(methodName, "InputDTO passed is - ", inputDTO);
                //if bit is disabled and commonSublist is present in the request
                if (cloningPersistDTO.getValidationMap().containsKey(KnConstants.CLONING_VALIDATION_BIT.CONTACT.value()) && cloningPersistDTO.getValidationMap().get(KnConstants.CLONING_VALIDATION_BIT.CONTACT.value())) {
                    if (cloningPersistDTO.getContactCloningPersistDTO() != null &&
                            !cloningPersistDTO.getContactCloningPersistDTO().isCommonContactEnabled() &&
                            cloningPersistDTO.getContactCloningPersistDTO().getSubListIdInfo() != null &&
                            cloningPersistDTO.getContactCloningPersistDTO().getSubListIdInfo().containsValue(DIST_POLICY_COMMON_CONTACT_LIST_SUPPORT)) {
                        knLogger.error(methodName, " commonContact bit is disabled at the Subscriber/System level");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.COMMON_CONTACT_LIST_SUPPORT_FLAG_DISABLED,
                                "commonContact bit is disabled at the Subscriber/System level", getEntityId(),
                                getOperationType(), getRuleId(), "", "");
                    }
                }
            }
        } finally {
            knLogger.debug(methodName, "EXIT: Validation Completed Successfully for common contact sublist ");
        }
    }
}
