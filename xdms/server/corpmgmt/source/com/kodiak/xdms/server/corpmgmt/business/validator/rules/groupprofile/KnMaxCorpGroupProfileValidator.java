/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupprofile;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGorupProfileDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.MAX_GROUP_PROFILES;

public class KnMaxCorpGroupProfileValidator extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMaxCorpGroupProfileValidator.class);
	@Override
	public void validate() throws KnValidationException, KnBOException {
		  final String methodName = "validate()";
	        knLogger.debug(methodName, "ENTRY.");
	        IPersistenceDTO persistDTO = getDTO();
	        if (persistDTO instanceof KnCorpGroupProfilePersistDTO) {
	        	KnCorpGroupProfilePersistDTO grpPersistDTO = (KnCorpGroupProfilePersistDTO) persistDTO;
	        	KnIPCorpGorupProfileDTO inputDto = (KnIPCorpGorupProfileDTO) grpPersistDTO.getInputDTO();
	            String value = inputDto.getGrpProfileName();
	            if(inputDto.getGrpProfileName() != null ){
	                value = inputDto.getGrpProfileName();
	            }
	            if(inputDto.getGrpProfileId() != null){
	                value = String.valueOf(inputDto.getGrpProfileId());
	            }
	            
	            int maxGrpProfileCount=grpPersistDTO.getCorpProfile().getMaxgGroupProfiles()==null?Integer.valueOf(grpPersistDTO.getParamNameValueMapCommon().get(MAX_GROUP_PROFILES)):grpPersistDTO.getCorpProfile().getMaxgGroupProfiles();
	            if (maxGrpProfileCount <= grpPersistDTO.getGrpProfileCount().intValue()) {
	                knLogger.error(methodName, "max group profile size  exceed ");
	                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_GROUP_PROFILE_SIZE_EXCEED,
	                        "max group profile size  exceed.", getEntityId(), getOperationType(), getRuleId(), value, "");
	            }
	        }
	       
	    }
}
