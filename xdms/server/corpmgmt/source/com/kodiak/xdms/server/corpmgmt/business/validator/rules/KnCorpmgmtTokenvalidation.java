/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;




import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnCorpmgmtTokenvalidation {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpmgmtTokenvalidation.class);
	public void validateToken(KnSubscriberPersistDTO originator) throws KnValidationException {
		
		final String methodName = "validate()";
		knLogger.debug(methodName, "ENTRY Pointcorp");
		String mcpttId_DB = "";
        String mcpttId_Input = "";
        int mcsCompliance = 0;
        KnIPCorpGroupDTO groupDTO = null;
		KnIPTalkGroupDTO talkGroupDTO = null;
        if(originator != null) {
         IInputDTO inputDTO = originator.getInputDTO();
           if(inputDTO instanceof KnIPCorpGroupDTO) {
        	   groupDTO = (KnIPCorpGroupDTO) inputDTO ;       	   
        	   mcpttId_Input = groupDTO.getMcPttId();
        	   mcpttId_DB = originator.getMcpttID();
        	   mcsCompliance = originator.getMcpttCompliance();
			
		}
        else if(inputDTO instanceof KnIPTalkGroupDTO) {
			   talkGroupDTO = (KnIPTalkGroupDTO) inputDTO ;
				mcpttId_Input = talkGroupDTO.getMcPttId();
				mcpttId_DB = originator.getMcpttID();
				mcsCompliance = originator.getMcpttCompliance();

			}
        }
        knLogger.debug("Value from database"+" "+mcsCompliance+" "+ KnGDPRTemplate.mcpttId(mcpttId_DB));
        knLogger.debug("Value from the token is"+mcpttId_Input);
		if (mcpttId_Input != null && mcpttId_DB != null)
        {
		if (mcsCompliance == 0) {
			if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty()
					&& !(mcpttId_Input.equals(mcpttId_DB.substring(5).trim())||mcpttId_Input.equals(mcpttId_DB.trim()))) {
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.TOKEN_MCPTTID_NOT_MATCHED,
						"MCPTTD is not matched with the token", mcpttId_Input);
			}
		} else if (mcsCompliance == 1) {
			if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty() && !mcpttId_Input.equals(mcpttId_DB.trim())) {
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.TOKEN_MCPTTID_NOT_MATCHED,
						"MCPTTD is not matched with the token", mcpttId_Input);
			}
		}
        }
		
	}
	
	
	
}
