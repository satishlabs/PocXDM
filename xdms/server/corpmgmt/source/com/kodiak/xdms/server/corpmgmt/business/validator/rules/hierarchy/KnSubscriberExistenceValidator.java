/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.hierarchy;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpHierarchyDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpHierarchyPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Map;

public class KnSubscriberExistenceValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnSubscriberExistenceValidator.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {

        final String methodName = "KnSubscriberExistenceValidator.validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();

        // Check if the DTO is of the expected type; if not, skip validation.
        if (!(persistDTO instanceof KnCorpHierarchyPersistDTO hierarchyPersistDTO)) {
            knLogger.debug(methodName, "Not a hierarchy persist DTO. Skipping.");
            return;
        }

        // Retrieve the input DTO and modified ID details.
        KnIPCorpHierarchyDTO knIPCorpHierarchyDTO = (KnIPCorpHierarchyDTO) hierarchyPersistDTO.getInputDTO();
        Map<String, Map<Integer, Integer>> clusterIdSubscriberMap = knIPCorpHierarchyDTO.getClusterIdSubscriberMap();
        Map<String, Integer> systemClusterId = hierarchyPersistDTO.getSystemClusterId();
        if (null == systemClusterId || null == clusterIdSubscriberMap) {
            return;
        }
        // Build reverse map once: clusterId -> geoCode
        Map<Integer, String> clusterIdToGeo = new java.util.HashMap<>(systemClusterId.size());
        for (Map.Entry<String, Integer> e : systemClusterId.entrySet()) {
            clusterIdToGeo.putIfAbsent(e.getValue(), e.getKey());
        }
        knLogger.debug(methodName, "systemClusterId:", systemClusterId);
        knLogger.debug(methodName, "clusterIdSubscriberMap:", clusterIdSubscriberMap);
        Map<String, String> hierarchyIdNameMap = knIPCorpHierarchyDTO.getHierarchyIdNameMap();
        knLogger.info(methodName, "hierarchyIdNameMap:", hierarchyIdNameMap);
        for (Map.Entry<String, Map<Integer, Integer>> entry : clusterIdSubscriberMap.entrySet()) {
            String hierarchyId = entry.getKey();
            Map<Integer, Integer> subscriberMap = entry.getValue();
            if (subscriberMap != null && !subscriberMap.isEmpty()) {
                for (Map.Entry<Integer, Integer> subscriberEntry : subscriberMap.entrySet()) {
                    Integer clusterId = subscriberEntry.getKey();
                    Integer subscriberCount = subscriberEntry.getValue();
                    if (subscriberCount != null && subscriberCount > 0 && clusterIdToGeo.containsKey(clusterId)) {
                        String organizationName = hierarchyIdNameMap.get(hierarchyId);
                        knLogger.error(methodName, "Validation failed: Organization '", organizationName, "' has ", subscriberCount, " subscriber(s) associated with clusterId '", clusterId, "'.");
                        String geoCode = clusterIdToGeo.getOrDefault(clusterId, "");
                        String errorMessage = String.format("GeoCode '%s' cannot be removed because Organization '%s' has %d subscriber%s associated with GeoCode '%s'.",
                                geoCode, organizationName, subscriberCount, subscriberCount > 1 ? "s" : "", geoCode);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_ASSOCIATE_WITH_GEOCODE,
                                errorMessage, getEntityId(), getOperationType(), getRuleId(), null, geoCode);
                    }
                }
            }
        }
        knLogger.info(methodName, "Exit:: Validation passed: No subscribers associated with any hierarchy nodes.");
    }
}
