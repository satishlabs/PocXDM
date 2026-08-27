/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnCorpProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicContactPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicGroupPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnConstants;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KnVendorIDValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnVendorIDValidationRule.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     */
    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY : corporate MDN validation ");
        int reqTPid;
        List<String> otherVendorIdMdn = new ArrayList<>();
        if (persistDTO instanceof KnDynamicContactPersistDTO) {
            KnDynamicContactPersistDTO dynamicContPerDto = (KnDynamicContactPersistDTO) persistDTO;
            Map<String, Integer> mdnTPidMap = dynamicContPerDto.getMdnTPidMap();
            reqTPid = dynamicContPerDto.getTpID();
            for (Map.Entry<String, Integer> entry : mdnTPidMap.entrySet()) {
                String mdn = entry.getKey();
                if (reqTPid != entry.getValue()) {
                    otherVendorIdMdn.add(mdn);
                }
            }
        } else if (persistDTO instanceof KnDynamicGroupPersistDTO) {
            KnDynamicGroupPersistDTO dynamicGrpPerDto = (KnDynamicGroupPersistDTO) persistDTO;
            Map<String, Integer> mdnTPidMap = dynamicGrpPerDto.getMdnTPidMap();
            reqTPid = dynamicGrpPerDto.getTpID();
            for (Map.Entry<String, Integer> entry : mdnTPidMap.entrySet()) {
                String mdn = entry.getKey();
                if (reqTPid != entry.getValue()) {
                    otherVendorIdMdn.add(mdn);
                }
            }
        } else {
            knLogger.error(methodName, "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnDynamicContactPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, "");
        }

        if (!otherVendorIdMdn.isEmpty()) {
            knLogger.error(methodName, "MDN not in same Vendor are - ", KnGDPRTemplate.mdnList(otherVendorIdMdn));
            throw new KnPubBOValidationException(KnErrorCodes.Validator.MDN_NOT_OF_VENDORID,
                    "MDN not in same Vendor ID - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MDN, otherVendorIdMdn.toString());
        }

    }
}
