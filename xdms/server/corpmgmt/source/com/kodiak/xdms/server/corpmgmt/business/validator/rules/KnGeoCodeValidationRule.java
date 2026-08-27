package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPAllocateSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnAllocateSubsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;

public class KnGeoCodeValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGeoCodeValidationRule.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validates()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnAllocateSubsPersistDTO) {
            knLogger.debug(methodName, "Bean is instance of KnAllocateSubsPersistDTO");
            KnAllocateSubsPersistDTO allocateSubsPersistDTO = (KnAllocateSubsPersistDTO) persistDTO;
            Collection<String> dbGeoCodes = allocateSubsPersistDTO.getGeoCodes();
            boolean isMapped = allocateSubsPersistDTO.isHierarchyCorpMapped();
            if (allocateSubsPersistDTO.getInputDTO() instanceof KnIPAllocateSubscriberDTO) {
                KnIPAllocateSubscriberDTO inputDTO = (KnIPAllocateSubscriberDTO) allocateSubsPersistDTO.getInputDTO();
                Collection<String> reqGeoCodes = inputDTO.getReqGeoCodes();
                Collection<String> missingGeoCodes = new ArrayList<>(reqGeoCodes);
                missingGeoCodes.removeAll(dbGeoCodes);
                knLogger.debug(methodName, "", " Requested geoCodes ", reqGeoCodes);
                if (!missingGeoCodes.isEmpty()) {
                    knLogger.error(methodName, "Geo codes not mapped in Hierarchy " + missingGeoCodes);
                    throw new KnCorpBOValidationException(
                            KnErrorCodes.Validator.INVALID_GEOCODES,
                            "The following geo codes are not mapped in the hierarchy." + missingGeoCodes,
                            getEntityId(), getOperationType(), getRuleId(), missingGeoCodes.toString(), ""
                    );
                }
            }

        } else {
            knLogger.error("validate()", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnAllocateSubsPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
