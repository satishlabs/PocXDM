/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/

package com.kodiak.xdms.server.corpmgmt.business.validator.rules.hierarchy;

import com.kodiak.common.commdto.common.KnModifiedIdDetailsListDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpHierarchyDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpHierarchyPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.List;
import java.util.Map;


/**
 * Validator class for ensuring that modifications to the root hierarchy details
 * adhere to specific business rules.
 */
public class KnRootHierarchyDetailsValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnRootHierarchyDetailsValidator.class);

    /**
     * Validates the root hierarchy details based on the provided persistence DTO.
     *
     * @throws KnValidationException if validation fails due to business rules.
     * @throws KnBOException         if a business operation exception occurs.
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();

        // Check if the DTO is of the expected type; if not, skip validation.
        if (!(persistDTO instanceof KnCorpHierarchyPersistDTO hierarchyPersistDTO)) {
            knLogger.debug(methodName, "Not a hierarchy persist DTO. Skipping.");
            return;
        }

        // Retrieve the input DTO and modified ID details.
        KnIPCorpHierarchyDTO knIPCorpHierarchyDTO = (KnIPCorpHierarchyDTO) hierarchyPersistDTO.getInputDTO();
        List<KnModifiedIdDetailsListDTO> modifiedIdDetails = knIPCorpHierarchyDTO.getModifiedIdDetails();
        if (null == modifiedIdDetails || modifiedIdDetails.isEmpty()) {
            knLogger.debug(methodName, "No modified id details provided. Skipping.");
            return;
        }

        // Retrieve custom parameters from the input DTO.
        Map<String, Object> customParamMap = knIPCorpHierarchyDTO.getCustomParamMap();
        if (null != customParamMap && !customParamMap.isEmpty()) {
            // Extract specific flags and root hierarchy details from the custom parameters.
            boolean isAddingChild = (Boolean) customParamMap.getOrDefault(KnConstants.IS_ADDING_CHILD, Boolean.FALSE);
            boolean isRemovingChild = (Boolean) customParamMap.getOrDefault(KnConstants.IS_REMOVING_CHILD, Boolean.FALSE);
            String rootHierarchyId = (String) customParamMap.getOrDefault(KnConstants.ROOT_HIERARCHY_ID, "");
            String rootHierarchyName = (String) customParamMap.getOrDefault(KnConstants.ROOT_HIERARCHY_NAME, "");

            // Iterate through the modified ID details to validate against business rules.
            for (KnModifiedIdDetailsListDTO modifiedIdDetail : modifiedIdDetails) {
                // Check if the root hierarchy is being modified without adding or removing a child.
                if (!isRemovingChild && !isAddingChild && rootHierarchyId.equals(modifiedIdDetail.getIdKey())) {
                    // Log a warning and throw an exception if modification is not allowed.
                    String message = " Modification of root hierarchy " + rootHierarchyName + " is not allowed.";
                    knLogger.warn(methodName, message);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.ROOT_HIERARCHY_MODIFICATION_NOT_ALLOWED,
                            message, getEntityId(), getOperationType(), getRuleId(), null, "");
                }
            }
        }
    }
}
