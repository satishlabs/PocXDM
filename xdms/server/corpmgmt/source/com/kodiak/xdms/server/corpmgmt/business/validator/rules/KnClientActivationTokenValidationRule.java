/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import java.util.Arrays;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPEmergencyInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMcpttFeaturePersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnReverseContactPersistDto;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnClientActivationTokenValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnClientActivationTokenValidationRule.class);

	@Override
	public void validate() throws KnValidationException {
		// TODO Auto-generated method stub
		final String methodName = "validate()";
		knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        
        KnCorpMcpttFeaturePersistDTO mcpttFeaturePersistDTO = null;
        KnReverseContactPersistDto  reverseContactPersistDto=null;
        KnContactDetailsPersistDTO contactDetailsPersistDTO=null;
        KnIPCorpContactDTO ipCorpContactDTO=null;
        KnIPEmergencyInfoDTO ipEmergencyInfoDTO = null;
        KnCorpTGSPersistDTO corpTGSPersistDTO1 = null;
        KnIPTalkGroupDTO ipTalkGroupDTO = null;
        knLogger.debug("validate", "ENTRY : Validating if mcpttID from request and server are same or not " + persistDTO);
        String mcpttId_DB = "";
        String mcpttId_Input = "";
        String mdn="";
        int mcsCompliance = 0;
     
        if(persistDTO instanceof KnCorpMcpttFeaturePersistDTO) {
        	IInputDTO inputDTO = persistDTO.getInputDTO();
        	mcpttFeaturePersistDTO = (KnCorpMcpttFeaturePersistDTO) persistDTO;
        	if (inputDTO instanceof KnIPEmergencyInfoDTO) {
        		 mcpttFeaturePersistDTO = (KnCorpMcpttFeaturePersistDTO) persistDTO;
        		  ipEmergencyInfoDTO = (KnIPEmergencyInfoDTO) inputDTO;
                 knLogger.debug(methodName, "ipEmergencyInfoDTO - ", ipEmergencyInfoDTO);
                   mcpttId_Input = ipEmergencyInfoDTO.getMcptt_id();
                   mcpttId_DB = mcpttFeaturePersistDTO.getMcpttId();
                   mdn= mcpttFeaturePersistDTO.getMdn();
                   mcsCompliance = mcpttFeaturePersistDTO.getMcpttCompliance();
        	}
        }
        
        else if(persistDTO instanceof KnReverseContactPersistDto) {
        	IInputDTO inputDTO = persistDTO.getInputDTO();
        	reverseContactPersistDto = (KnReverseContactPersistDto) persistDTO;
        	if (inputDTO instanceof KnIPCorpContactDTO) {
        		reverseContactPersistDto = (KnReverseContactPersistDto) persistDTO;
        		ipCorpContactDTO = (KnIPCorpContactDTO) inputDTO;
                 knLogger.debug(methodName, "ipCorpContactDTO - ", ipCorpContactDTO);
                   mcpttId_Input = ipCorpContactDTO.getMcpttId();
                   mdn= ipCorpContactDTO.getMdn();
                   mcpttId_DB = reverseContactPersistDto.getMcpttId();
                   mcsCompliance = reverseContactPersistDto.getMcpttCompliance();
        	}
        }
        
        else if(persistDTO instanceof KnContactDetailsPersistDTO) {
        	IInputDTO inputDTO = persistDTO.getInputDTO();
        	contactDetailsPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
        	if (inputDTO instanceof KnIPCorpContactDTO) {
        		contactDetailsPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
        		ipCorpContactDTO = (KnIPCorpContactDTO) inputDTO;
                   mcpttId_Input = ipCorpContactDTO.getMcpttId();
                   mdn= ipCorpContactDTO.getMdn();
                   mcpttId_DB = contactDetailsPersistDTO.getMcpttId();
                   mcsCompliance = contactDetailsPersistDTO.getMcpttCompliance();
        	}
        }
        else if(persistDTO instanceof KnCorpTGSPersistDTO) {
        	IInputDTO inputDTO = persistDTO.getInputDTO();
        	corpTGSPersistDTO1 = (KnCorpTGSPersistDTO) persistDTO;
        	if(inputDTO instanceof KnIPTalkGroupDTO) {
        		corpTGSPersistDTO1 = (KnCorpTGSPersistDTO) persistDTO;
        		ipTalkGroupDTO = (KnIPTalkGroupDTO) inputDTO;
        		mcpttId_Input = ipTalkGroupDTO.getMcPttId();
        		mcpttId_DB = corpTGSPersistDTO1.getMcPttId();
        		mcsCompliance = corpTGSPersistDTO1.getMcpttCompliance();
        		
        		
        	}
        	
        }
        else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        
        knLogger.debug("Value from database"+" "+mcsCompliance+" "+ KnGDPRTemplate.mdn(mdn)+" "+mcpttId_DB);
        knLogger.debug("Value from the token is"+mcpttId_Input);
		if (mcpttId_Input != null && mcpttId_DB != null)
        {
		if (mcsCompliance == 0) {
			if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty()
					&& !(mcpttId_Input.equals(mcpttId_DB.substring(5).trim())|| mcpttId_Input.equals(mcpttId_DB.trim()))) {
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.TOKEN_MCPTTID_NOT_MATCHED,
						"MCPTTD is not matched with the token MDN", getEntityId(), getOperationType(), getRuleId(),
						Arrays.asList(mdn).toString(), "");
			}
		} else if (mcsCompliance == 1) {
			if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty() && !mcpttId_Input.equals(mcpttId_DB.trim())) {
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.TOKEN_MCPTTID_NOT_MATCHED,
						"MCPTTD is not matched with the token", getEntityId(), getOperationType(), getRuleId(),
						Arrays.asList(mdn).toString(), "");
			}
		}
        }
        
	}

}
