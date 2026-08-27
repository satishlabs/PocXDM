/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;
/**
 * ************************************************************************
 * <p/>
 * File name:  KnMCSXCAPTokenValidationRule.java
 * Subsystem:  XDMS-BulkFrameWork
 * <p/>
 * Name                  Date          Release
 * --------------------  ------------  -------------------------------------
 * Shashank Tewari      26/06/2019    9.1
 * <p/>
 * <p/>
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPMCSDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubMCSXCAPPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import static com.kodiak.xdms.server.pubmgmt.resources.KnConstants.KEY_DATATYPE_MDN;

import com.kodiak.common.resources.KnGDPRTemplate;

public class KnMCSXCAPTokenValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMCSXCAPTokenValidationRule.class);

    public void validate() throws KnValidationException {
        IPersistenceDTO persistDTO = getDTO();
        KnSubscriberPersistDTO knSubscriberPersistDTO = null;
        KnIPMCSDTO knIPMCSDTO = null;
        knLogger.debug("validate", "ENTRY : Validating if mcpttID from request and server are same or not " + persistDTO);
        String mcpttId = "";
        String mdn="";
        int mcsCompliance;
        KnPubMCSXCAPPersistDTO pubMCSXCAPPersistDTO = null;
        if (persistDTO instanceof KnPubMCSXCAPPersistDTO) {
            pubMCSXCAPPersistDTO = (KnPubMCSXCAPPersistDTO) persistDTO;
            knSubscriberPersistDTO = (KnSubscriberPersistDTO) pubMCSXCAPPersistDTO.getPersistenceDTO();
            mcpttId = knSubscriberPersistDTO.getMcpttID();
            mdn=knSubscriberPersistDTO.getMdn();
            mcsCompliance=knSubscriberPersistDTO.getMcpttCompliance();
        } else {
            knLogger.error("validate", "Unexpected DTO passed - " + persistDTO.getClass() +
                    ", Expected Dto - KnPubMCSXCAPPersistDTO ");
            throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), KEY_DATATYPE_MDN, pubMCSXCAPPersistDTO.getMdn());
        }
        knIPMCSDTO = (KnIPMCSDTO) pubMCSXCAPPersistDTO.getInputDTO();
        knLogger.debug("Validate " ," MCPTTID from token :" + KnGDPRTemplate.mcpttId(knIPMCSDTO.getMcpttID()) +" mdn from Profile "+KnGDPRTemplate.mdn(mdn) + " mcsCompliance from Profile   "+ mcsCompliance);
       // if profile is kodiak client then matching mcpttid with mdn
        if(mcsCompliance==0){
            if (mdn.isEmpty() || (knIPMCSDTO.getMcpttID() !=null && !(knIPMCSDTO.getMcpttID().equals(mdn.trim()) || knIPMCSDTO.getMcpttID().substring(5).trim().equals(mdn.trim())))) {
                knLogger.error("validate", "Validation failed MCPTTID not matched. Request MDN : " +KnGDPRTemplate.mcpttId(knIPMCSDTO.getMcpttID()) + " and server MDN : " + KnGDPRTemplate.mdn(mdn));
                throw new KnPubBOValidationException(KnErrorCodes.Validator.MCPTTID_NOT_MATCHED,
                        "Validation Rule Failed , MCPTTID not matched ",
                        getOperationType(), getRuleId(), KEY_DATATYPE_MDN, pubMCSXCAPPersistDTO.getMdn());
            }
        }
        else if(mcsCompliance==1){
            if (mcpttId.isEmpty() || !knIPMCSDTO.getMcpttID().equals(mcpttId.trim())) {
                knLogger.error("validate", "Validation failed MCPTTID not matched. Request MCPTTID : " + KnGDPRTemplate.mcpttId(knIPMCSDTO.getMcpttID()) + " and server MCPTTID : " + KnGDPRTemplate.mcpttId(mcpttId));
                throw new KnPubBOValidationException(KnErrorCodes.Validator.MCPTTID_NOT_MATCHED,
                        "Validation Rule Failed , MCPTTID not matched ",
                        getOperationType(), getRuleId(), KEY_DATATYPE_MDN, pubMCSXCAPPersistDTO.getMdn());
            }
        }
        else{
            knLogger.error("Validation Failed as mcsCompliance/mcptt_compliance value is neither 0 nor 1 : ",mcsCompliance);
            throw new KnPubBOValidationException(KnErrorCodes.Validator.MCPTTID_NOT_MATCHED,
                    "Validation Rule Failed , MCPTTID not matched ",
                    getOperationType(), getRuleId(), KEY_DATATYPE_MDN, pubMCSXCAPPersistDTO.getMdn());
        }
        knLogger.info("validate", "KnMCSXCAPTokenValidationRule Validated successfully.");
    }
}