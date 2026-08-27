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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import static com.kodiak.common.resources.KnConstants.DYNAMIC_CGMT_INTF;
import static com.kodiak.common.resources.KnConstants.GROUP_CREATED_BY_DYNAMIC_INTF;
import com.kodiak.logger.KnLogger;

public class KnTPDeleteGrpPermissionValidator extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnTPDeleteGrpPermissionValidator.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        
        KnIPCorpGroupDTO groupInputDTO = null;
        
		if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
			KnCorpGroupInfoPersistDTO CorpPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
			groupInputDTO = (KnIPCorpGroupDTO) persistDTO.getInputDTO();

			if (DYNAMIC_CGMT_INTF == groupInputDTO.getClientType()) {
				
				String reqGrpOwner = groupInputDTO.getTpGroupOwner();
				String dbGrpOwner = CorpPersistDTO.getTpGroupOwner();
				knLogger.info(methodName,  "DB and request values-", "reqGrpOwner-",reqGrpOwner,"dbGrpOwner-",dbGrpOwner,
						"grp creadted by-", CorpPersistDTO.getGroupCreatedBy());
				
				if (!reqGrpOwner.equals(dbGrpOwner) || GROUP_CREATED_BY_DYNAMIC_INTF != CorpPersistDTO.getGroupCreatedBy()) {
					knLogger.warn(methodName,  "DB and request values are not matching");
					throw new KnCorpBOValidationException(KnErrorCodes.Validator.TP_DELETE_GRP_PERMISSION_DENIED,
							"Either group mdn is not matching or group not created by dynamic interface", getEntityId(), getOperationType(),
							getRuleId(), groupInputDTO.getTpGroupOwner(), "");
				}
				
				knLogger.debug( methodName, "Exit Point: Validation Completed Successfully");
			}
			
        } else {
        	 knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),", Expected Dto - KnCorpGroupInfoPersistDTO ");
             throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                     "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
		
    }
}
