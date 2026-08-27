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

public class KnCorpHierarchyValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpHierarchyValidationRule.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnAllocateSubsPersistDTO) {
            knLogger.debug(methodName, "Bean is instance of KnAllocateSubsPersistDTO");
            KnAllocateSubsPersistDTO allocateSubsPersistDTO = (KnAllocateSubsPersistDTO) persistDTO;
            boolean isMapped = allocateSubsPersistDTO.isHierarchyCorpMapped();
            if (allocateSubsPersistDTO.getInputDTO() instanceof KnIPAllocateSubscriberDTO) {
                KnIPAllocateSubscriberDTO inputDTO = (KnIPAllocateSubscriberDTO) allocateSubsPersistDTO.getInputDTO();
                String reqCorpId = inputDTO.getCorpId();
                String reqHierarchyId = inputDTO.getHierarchyId();

                knLogger.debug(methodName, "Requested corpId ", reqCorpId, " requestedHierarchyId ", reqHierarchyId);

                // Existing hierarchy-corp mapping validation
                if (!isMapped) {
                    knLogger.error(methodName, "validation failed provided corpId-hierarchyId Map does not exist", reqCorpId, reqHierarchyId);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORPID_HIERARCHYID_MAP_NOT_EXISTS,
                            "validation failed corpId-hierarchyId provided in request is not present ", getEntityId(), getOperationType(), getRuleId(), reqCorpId + "", "");
                }

                // New hierarchy ID validation - check if hierarchy IDs match using simple boolean flag
                boolean isHierarchyIdMatching = allocateSubsPersistDTO.isHierarchyIdMatching();
                if (!isHierarchyIdMatching) {
                    String errorMsg = "HierarchyId validation failed. Database hierarchyId does not match request hierarchyId: " + reqHierarchyId;
                    knLogger.error(methodName, errorMsg);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.HIERARCHY_ID_MISMATCH, errorMsg,
                            getEntityId(), getOperationType(), getRuleId(), reqHierarchyId, "");
                }
                knLogger.debug(methodName, "HierarchyId validation completed successfully");
            }

        } else {
            knLogger.error("validate()", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnAllocateSubsPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "EXIT.");
    }
}
