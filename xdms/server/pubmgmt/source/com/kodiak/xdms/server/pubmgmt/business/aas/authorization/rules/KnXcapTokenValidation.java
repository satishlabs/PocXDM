/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.aas.authorization.rules;

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationException;
import com.kodiak.xdms.server.common.framework.aas.authorization.KnAuthorizationRule;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubAuthListDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubGroupDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubGroupInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPTGSSListDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

public class KnXcapTokenValidation extends KnAuthorizationRule {
	
	
	private static final KnLogger knLogger = KnLogger.getLogger(KnXcapTokenValidation.class);

	private static final String CLASS = KnXcapTokenValidation.class.getName();
	@Override
	public void authorize(IPersistenceDTO persistDTO) throws KnAuthorizationException {
		// TODO Auto-generated method stub
		
		final String methodName = "validate()";
		knLogger.debug(methodName, "ENTRY Point");
		IPersistenceDTO performerDTO = persistDTO.getPersistenceDTO();
		KnIPPubGroupDTO ipGroupInfoDTO = null;
		KnSubscriberPersistDTO subsProfile = null;
		KnIPPubAuthListDTO iPPubAuthListDTO=null;
		KnIPPubContactDTO iPPubContactDTO=null;
		KnIPPubGroupInfoDTO groupInfoDTO =null;
		KnIPTGSSListDTO iPTGSSListDTO=null;
		KnIPPubContactInfoDTO ipContactInfoDTO = null;
		String mcpttId_DB = "";
        String mcpttId_Input = "";
        int mcsCompliance = 0;
		 if(performerDTO instanceof KnSubscriberPersistDTO) {
			 
			 IInputDTO inputDTO = performerDTO.getInputDTO();
			 subsProfile = (KnSubscriberPersistDTO) performerDTO;
			 if (inputDTO instanceof KnIPPubGroupDTO) {
				 knLogger.debug("inputDTO"+inputDTO);
				 ipGroupInfoDTO = (KnIPPubGroupDTO) inputDTO;
				 mcpttId_Input = ipGroupInfoDTO.getMcPttId();
				 mcpttId_DB =  subsProfile.getMcpttID();
				 mcsCompliance = subsProfile.getMcpttCompliance();
			 }
			 else if(inputDTO instanceof KnIPPubAuthListDTO)
			 {
				 knLogger.debug("inputDTO"+inputDTO);
				 iPPubAuthListDTO = (KnIPPubAuthListDTO) inputDTO;
				 mcpttId_Input = iPPubAuthListDTO.getMcpttId();
				 mcpttId_DB =  subsProfile.getMcpttID();
				 mcsCompliance = subsProfile.getMcpttCompliance();
			 }			 
			 else if(inputDTO instanceof KnIPPubContactDTO)
			 {
				 knLogger.debug("inputDTO"+inputDTO);
				 iPPubContactDTO = (KnIPPubContactDTO) inputDTO;
				 mcpttId_Input = iPPubContactDTO.getMcpttId();
				 mcpttId_DB =  subsProfile.getMcpttID();
				 mcsCompliance = subsProfile.getMcpttCompliance();
			 }
			 else if(inputDTO instanceof KnIPPubGroupInfoDTO) {
				 knLogger.debug("inputDTO"+inputDTO);
				 groupInfoDTO = (KnIPPubGroupInfoDTO) inputDTO;
				 mcpttId_Input = groupInfoDTO.getMcPttId();
				 mcpttId_DB = subsProfile.getMcpttID();
				 mcsCompliance = subsProfile.getMcpttCompliance();
			 }
			 else if(inputDTO instanceof KnIPTGSSListDTO)
			 {
				 knLogger.debug("inputDTO"+inputDTO);
				iPTGSSListDTO = (KnIPTGSSListDTO) inputDTO;
				mcpttId_Input = iPTGSSListDTO.getMcpttId();
				mcpttId_DB = subsProfile.getMcpttID();
				mcsCompliance = subsProfile.getMcpttCompliance();
			 }
		 }
		 knLogger.debug("Value from database"+" "+"mcsCompliance "+mcsCompliance+" "+"mcpttId_DB "+KnGDPRTemplate.mcpttId(mcpttId_DB));
	     knLogger.debug(" MCPTT Value from the token is "+KnGDPRTemplate.mcpttId(mcpttId_Input));
			if (mcpttId_Input != null && mcpttId_DB != null)
	        {
			if (mcsCompliance == 0) {
				if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty()
						&& !(mcpttId_Input.equals(mcpttId_DB.substring(5).trim()) || (mcpttId_Input.equals(mcpttId_DB.trim())))) {
					throw new KnAuthorizationException(KnErrorCodes.Validator.MCPTTID_NOT_MATCHED,
		                    "MCPTTID NOT matched for MDN", mcpttId_Input, mcpttId_Input, mcpttId_Input);
				}
			} else if (mcsCompliance == 1) {
				if (!mcpttId_Input.isEmpty() && !mcpttId_DB.isEmpty() && !mcpttId_Input.equals(mcpttId_DB.trim())) {
					throw new KnAuthorizationException(KnErrorCodes.Validator.MCPTTID_NOT_MATCHED,
		                    "MCPTTID NOT matched", mcpttId_Input, mcpttId_Input, mcpttId_Input);
				}
			}
	        }
		
	}

}
