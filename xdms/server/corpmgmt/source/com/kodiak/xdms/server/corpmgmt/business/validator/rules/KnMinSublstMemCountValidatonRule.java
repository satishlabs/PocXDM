/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import java.util.Arrays;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 10/7/11
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnMinSublstMemCountValidatonRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnMinSublstMemCountValidatonRule.class);
    private final String CLASS = KnMinSublstMemCountValidatonRule.class.getName();
    private static final String MINIMUM_MEMBERS = "minimumMembers";

    public void validate() throws KnValidationException {

        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, "ENTRY - Validating Minimum members in a sublist. DTO passed - " , persistDTO);
        int minimumMembers = Integer.parseInt(getAttribute(MINIMUM_MEMBERS));
        knLogger.debug( methodName, "Minimum Members Configured  - " , minimumMembers);

        try {
            if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnSublistDetailsPersistDTO - " , persistDTO);
                KnSublistDetailsPersistDTO corpSublistPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
                knLogger.debug( methodName, "Corporate DTO passed in the request is - " , corpSublistPersistDTO);
                knLogger.debug( methodName, "To validate If the min size of the corplist members count.");
                int count = corpSublistPersistDTO.getTotalSublistsMembersCount();
                knLogger.debug( methodName, "The sublist member count as of now is - " , count);
                if (count < minimumMembers) {
                    knLogger.debug( methodName, "Sublist minimum member count limit not statisfied.");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MINIMUM_SUBLIST_MEM_LIMIT_NOT_STATISFIED,
                            "Sublist minimum member Limit not statisfied", getEntityId(), getOperationType(), getRuleId(), Arrays.asList(minimumMembers).toString(), "");
                }
            } else {
                knLogger.error( methodName, "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnSublistDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }

            knLogger.debug( methodName, "Validation Completed Successfully");
        } finally {
            knLogger.debug( methodName, "EXIT");
        }
    }
}
