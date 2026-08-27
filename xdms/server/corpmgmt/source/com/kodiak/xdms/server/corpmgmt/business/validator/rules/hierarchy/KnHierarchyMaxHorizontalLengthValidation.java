package com.kodiak.xdms.server.corpmgmt.business.validator.rules.hierarchy;

import com.kodiak.common.commdto.common.KnIdDetailsDTO;
import com.kodiak.common.commdto.common.KnIdDetailsListDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpHierarchyDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpHierarchyPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.LinkedList;
import java.util.Queue;

public class KnHierarchyMaxHorizontalLengthValidation extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnHierarchyMaxHorizontalLengthValidation.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpHierarchyPersistDTO) {
            KnCorpHierarchyPersistDTO hierarchyPersistDTO = (KnCorpHierarchyPersistDTO) persistDTO;
            int maxWidth = hierarchyPersistDTO.getMaxHierarchyHorozontalLevel();
            KnIPCorpHierarchyDTO knIPCorpHierarchyDTO = (KnIPCorpHierarchyDTO) hierarchyPersistDTO.getInputDTO();
            KnIdDetailsListDTO rootList = knIPCorpHierarchyDTO.getIdDetailsListDTO();
            KnIdDetailsDTO[] rootNodes = rootList.getIdDetailsDto();
            validateMaxWidth(rootNodes, maxWidth);
        }
        knLogger.debug(methodName, "Exit.");

    }

    private void validateMaxWidth(KnIdDetailsDTO[] rootNodes, int maxWidth) throws KnValidationException {
        if (rootNodes == null || rootNodes.length == 0) return;

        if (rootNodes.length > maxWidth) {
            throw new KnCorpBOValidationException(
                    KnErrorCodes.Validator.HIERARCHY_LENGTH_EXCEEDED,
                    "max children exceeded at root level",
                    getEntityId(),
                    getOperationType(),
                    getRuleId(),
                    "Allowed max children per parent is " + maxWidth + " but found " + rootNodes.length,
                    ""
            );
        }

        Queue<KnIdDetailsDTO> queue = new LinkedList<>();
        java.util.Collections.addAll(queue, rootNodes);

        while (!queue.isEmpty()) {
            KnIdDetailsDTO node = queue.poll();
            if (node == null) continue;
            if (node.getIdDetailsListDto() != null && node.getIdDetailsListDto().getIdDetailsDto() != null) {
                KnIdDetailsDTO[] children = node.getIdDetailsListDto().getIdDetailsDto();
                if (children.length > maxWidth) {
                    throw new KnCorpBOValidationException(
                            KnErrorCodes.Validator.HIERARCHY_LENGTH_EXCEEDED,
                            "max children exceeded for a parent node",
                            getEntityId(),
                            getOperationType(),
                            getRuleId(),
                            "Allowed max children per parent is " + maxWidth + " but found " + children.length,
                            ""
                    );
                }
                java.util.Collections.addAll(queue, children);
            }
        }
    }
}
