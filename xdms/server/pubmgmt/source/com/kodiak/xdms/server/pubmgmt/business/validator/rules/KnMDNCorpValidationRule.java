/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubAuthListPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import java.util.Map;
import java.util.Objects;

import static com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes.Validator.MDNS_DOESNOT_BELONG_TO_SAME_CORP;


/**
 * Created by schandra on 26-12-2017.
 */
public class KnMDNCorpValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMDNCorpValidationRule.class);
    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( "validate", "ENTRY : Validating target and authorization mdns belongs to same corp >" + persistDTO);
        try {
            if (persistDTO instanceof KnPubAuthListPersistDTO){
                KnPubAuthListPersistDTO  authListPersistDTO = (KnPubAuthListPersistDTO) persistDTO;
                Integer corpid = authListPersistDTO.getCorpId();
                knLogger.debug(methodName, "AuthMdn corpid", corpid );
                Map<String, Integer> inputCorpIdMap = authListPersistDTO.getMdnCorpIdMap();
                for (Map.Entry<String,Integer> entry : inputCorpIdMap.entrySet()){
                    if(!Objects.equals(entry.getValue(), corpid)){
                        knLogger.error(methodName, "Auth mdn and target mdn doesnot belog to same corp", entry.getValue(), KnGDPRTemplate.mdn(authListPersistDTO.getMdn()));
                        //throw new KnPubBOValidationException("Validator.KNEC-PM14111", "Auth mdn and target mdn doesnot belog to same corp - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN" , authListPersistDTO.getMdn());
                        throw new KnPubBOValidationException(MDNS_DOESNOT_BELONG_TO_SAME_CORP, "Auth mdn and target mdn doesnot belog to same corp - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN" , authListPersistDTO.getMdn());
                    }
                }
            }else {
                knLogger.error(methodName, "Unexpected DTO passed - " , persistDTO.getClass(), ", Expected Dto - KnPubAuthListPersistDTO ");
                throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN", "");
            }
        }finally {
        knLogger.debug(methodName, "Exit Point");
    }
    }
}
