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
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.kodiak.logger.KnLogger;

import static com.kodiak.common.resources.KnConstants.DYNAMIC_CGMT_INTF;

public class KnTPReqVendorIdAndMemVendorIdValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnTPReqVendorIdAndMemVendorIdValidator.class);
    private static final int GROUP_CREATED_BY_DYNAMIC_INTF = 1;

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();

        KnIPCorpGroupInfoDTO groupInputDTO = null;
        Map<String, String> vendorMdnMap = null;
        Set<String> allTPMdnList = null;
        int grpCreatedBy = 0;
        String existingGroupOwner = null;

        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            groupInputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();
            vendorMdnMap = corpGroupInfoPersistDTO.getVendorMdnMap();
            if (DYNAMIC_CGMT_INTF == groupInputDTO.getClientType()) {
                allTPMdnList = new HashSet<>(corpGroupInfoPersistDTO.getTpDbMDNList());
            }
            existingGroupOwner = corpGroupInfoPersistDTO.getTpGroupOwner();
            grpCreatedBy = corpGroupInfoPersistDTO.getGroupCreatedBy();

        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO corpBCGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            groupInputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();
            vendorMdnMap = corpBCGrpPersistDTO.getVendorMdnMap();
            if (DYNAMIC_CGMT_INTF == groupInputDTO.getClientType()) {
                allTPMdnList = new HashSet<>(corpBCGrpPersistDTO.getTpDbMDNList());
            }
            grpCreatedBy = corpBCGrpPersistDTO.getGroupCreatedBy();
            existingGroupOwner = corpBCGrpPersistDTO.getTpGroupOwner();

        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(), ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }

        knLogger.debug(methodName, " mdnMap- ", vendorMdnMap, "allTPMdnList-", allTPMdnList);

        Set<String> failedMdnList = new HashSet<>();
        if (DYNAMIC_CGMT_INTF == groupInputDTO.getClientType()) {
            String tpreqVenodrID = groupInputDTO.getTpVendorID();
            knLogger.debug(methodName, "requestMdn - ", tpreqVenodrID);

            for (Map.Entry<String, String> entry : vendorMdnMap.entrySet()) {
                if (tpreqVenodrID.equals(entry.getValue())) {
                    if (allTPMdnList != null) allTPMdnList.remove(entry.getKey());
                }
            }
        } else {
            // modified through CAT interface:
            if (grpCreatedBy == GROUP_CREATED_BY_DYNAMIC_INTF) {
                if (vendorMdnMap != null && vendorMdnMap.containsKey(existingGroupOwner)) {
                    String vendorId = vendorMdnMap.get(existingGroupOwner);
                    vendorMdnMap.forEach((mdn, venId) -> {
                        if (!vendorId.equals(venId)) {
                            failedMdnList.add(mdn);
                        }
                    });
                }
                allTPMdnList = new HashSet<>();
            }
        }

        knLogger.debug(methodName, "allTPMdnList before - ", allTPMdnList);
        if (!failedMdnList.isEmpty() || (allTPMdnList != null && !allTPMdnList.isEmpty())) {
            knLogger.debug(methodName, "allTPMdnList after - ", allTPMdnList);
            Set<String> failedList = !failedMdnList.isEmpty() ? failedMdnList : allTPMdnList;
            knLogger.debug(methodName, "allTPMdnList final - ", failedList);
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.TP_VEDNOR_ID_MISMATCH,
                    "TP Request Vendor ID and Member vendor ID not matching", getEntityId(), getOperationType(),
                    getRuleId(), failedList.toString(), "");
        }
        knLogger.debug(methodName, "Exit Point: Validation Completed Successfully");
    }
}
