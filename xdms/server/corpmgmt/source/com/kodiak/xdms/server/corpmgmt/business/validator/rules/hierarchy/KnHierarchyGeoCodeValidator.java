package com.kodiak.xdms.server.corpmgmt.business.validator.rules.hierarchy;

import com.kodiak.common.commdto.common.KnIdDetailsDTO;
import com.kodiak.common.commdto.common.KnIdDetailsListDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupprofile.KnCorpGroupProfileValidator;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpHierarchyDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpHierarchyPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;

public class KnHierarchyGeoCodeValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupProfileValidator.class);
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();

        if (!(persistDTO instanceof KnCorpHierarchyPersistDTO)) {
            knLogger.debug(methodName, "Not a hierarchy persist DTO. Skipping.");
            return;
        }

        KnCorpHierarchyPersistDTO hierarchyPersistDTO = (KnCorpHierarchyPersistDTO) persistDTO;
        KnIPCorpHierarchyDTO knIPCorpHierarchyDTO = (KnIPCorpHierarchyDTO) hierarchyPersistDTO.getInputDTO();

        List<String> sysGeoCodeList = hierarchyPersistDTO.getGeoCodeList();
        KnIdDetailsListDTO idDetailsListDTO = knIPCorpHierarchyDTO.getIdDetailsListDTO();

        List<String> allGeoCodes = new ArrayList<>();
        collectGeoCodes(idDetailsListDTO, allGeoCodes);
        List<String> invalidGeoCodes = new ArrayList<>();
        for (String geoCode : allGeoCodes) {
            if (!sysGeoCodeList.contains(geoCode)) {
                invalidGeoCodes.add(geoCode);
            }
        }
        knLogger.debug(methodName, "Collected GeoCodes from hierarchy: " + allGeoCodes);
        knLogger.debug(methodName, "System GeoCodes: " + sysGeoCodeList);
        if(!invalidGeoCodes.isEmpty()){
            knLogger.error(methodName, "Input geoCode is not configured as per system " + invalidGeoCodes);
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.GEOCODE_NOT_CONFIGURED_IN_THE_SYSTEM,
                    "Invalid GeoCodes found in hierarchy.", getEntityId(), getOperationType(), getRuleId(),
                    String.join(",", invalidGeoCodes), "");
        }



    }

    private void collectGeoCodes(KnIdDetailsListDTO node, List<String> collectedGeoCodes) {
        if (node == null) return;
        for (KnIdDetailsDTO detail : node.getIdDetailsDto()) {
            if (detail.getGeoCode() != null) {
                collectedGeoCodes.addAll(detail.getGeoCode());
            }
            if (detail.getAddedGeoCode() != null && !detail.getAddedGeoCode().isEmpty()) {
                collectedGeoCodes.addAll(detail.getAddedGeoCode());
            }
            // Recursively collect from children
            collectGeoCodes(detail.getIdDetailsListDto(), collectedGeoCodes);
        }
    }

}
