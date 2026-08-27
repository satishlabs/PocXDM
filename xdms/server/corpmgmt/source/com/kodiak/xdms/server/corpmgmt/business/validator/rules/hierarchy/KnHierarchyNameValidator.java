package com.kodiak.xdms.server.corpmgmt.business.validator.rules.hierarchy;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupprofile.KnCorpGroupProfileValidator;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpHierarchyPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;

public class KnHierarchyNameValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupProfileValidator.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        String methodName = "validate()";
        KnCorpHierarchyPersistDTO hierarchyPersistDTO = (KnCorpHierarchyPersistDTO) getDTO();

        List<String> inputIdNameList = hierarchyPersistDTO. getInputHierarchyNameList();
        List<String> corpIdNameList = hierarchyPersistDTO.getCorpHierarchyNameList();
        inputIdNameList = inputIdNameList.stream().map(String::toLowerCase).toList();
        knLogger.info(methodName, "Input Hierarchy Names: ", inputIdNameList);
        corpIdNameList = corpIdNameList.stream().map(String::toLowerCase).toList();
        knLogger.info(methodName, "Corp Hierarchy Names: ", corpIdNameList);
        List<String> invalidHierarchyNameList = new ArrayList<>();
        for (String inputId : inputIdNameList) {
            if (corpIdNameList.contains(inputId)) {
                invalidHierarchyNameList.add(inputId);
            }
        }
        if(!invalidHierarchyNameList.isEmpty()){
            knLogger.error(methodName, "Passed HierarchyNames are present under the corp " + invalidHierarchyNameList);
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.IDNAME_ALREADY_EXISTS,
                    "Passed HierarchyIds are present under the corp", getEntityId(), getOperationType(), getRuleId(),
                    String.join(",", invalidHierarchyNameList), "");
        }
    }
}
