/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.tp.rules;

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import static com.kodiak.common.resources.KnConstants.DYNAMIC_CGMT_INTF;

import java.util.Map;

import com.kodiak.logger.KnLogger;

public class KnTPGroupOwnerVerifyValidator extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnTPGroupOwnerVerifyValidator.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        KnIPCorpGroupInfoDTO groupInputDTO = null;
        Map<String, KnCorpSubscriberDTO> mdnMap = null;
        
		if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
			KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
			groupInputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();
			mdnMap = corpGroupInfoPersistDTO.getTpRequestMDNMap();
			
        } else if(persistDTO instanceof KnCorpBCGrpPersistDTO){
        	KnCorpBCGrpPersistDTO corpBCGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
        	groupInputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();
        	mdnMap = corpBCGrpPersistDTO.getTpRequestMDNMap();
        	
        } else {
        	 knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),", Expected Dto - KnCorpGroupInfoPersistDTO ");
             throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                     "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        
    	if (DYNAMIC_CGMT_INTF == groupInputDTO.getClientType()) {
    		String tpGroupOwner = groupInputDTO.getTpGroupOwner();
    		knLogger.debug( methodName, "requestMdn - " , tpGroupOwner, "mdnMap-", mdnMap);
    		
    		KnCorpSubscriberDTO subscriberDTO = mdnMap.get(tpGroupOwner);
    		if (subscriberDTO == null) {
    			throw new KnCorpBOValidationException(KnErrorCodes.Validator.TP_INVALID_MDN,
    					"Group owner not present in Pocsubscrinfo", getEntityId(), getOperationType(),
    					getRuleId(), tpGroupOwner, "");
    			
    		}
    		knLogger.debug( methodName, "Exit Point: Validation Completed Successfully");
    	}
    }
}
