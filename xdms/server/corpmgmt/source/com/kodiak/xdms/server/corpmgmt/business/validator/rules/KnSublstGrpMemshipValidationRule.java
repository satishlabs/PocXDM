/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnSublstGrpMemshipValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        26-03-2011      7.0
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.kodiak.logger.KnLogger;

public class KnSublstGrpMemshipValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSublstGrpMemshipValidationRule.class);

    /**
     * Validate the max allowed sublist for the corporate
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY. Validating if the sublist exists for the group.");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                knLogger.debug( methodName, "Corporate DTO passed in the request is - " , corpGroupInfoPersistDTO);
                knLogger.debug( methodName, "To validate If the sublist exists for the corporate group.");
                IInputDTO inputDTO = corpGroupInfoPersistDTO.getInputDTO();
                Collection<Integer> requestSublistIds;
                KnIPCorpGroupInfoDTO groupInfoDTO;
                if (inputDTO instanceof KnIPCorpGroupInfoDTO) {
                    /*KnIPCorpGroupInfoDTO*/
                    groupInfoDTO = (KnIPCorpGroupInfoDTO) inputDTO;
                    requestSublistIds = groupInfoDTO.getRemovedSublistIds();
                }
                else {
                    knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                            ", Expected Dto - KnIPCorpGroupInfoDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }
                //checking if the deleted sublist actually exists for the group
                knLogger.debug( methodName, "Populating data of sublist present for the group.");
                Collection<Integer> groupMappedSublistList = new ArrayList<Integer>();
                if (groupInfoDTO.getRemovedSublistIds() != null && !groupInfoDTO.getRemovedSublistIds().isEmpty()) {
                    Collection<Integer> dbSublistList = corpGroupInfoPersistDTO.getMappedSublistIds();
                    for (Integer removeSublistId : groupInfoDTO.getRemovedSublistIds()) {
                        if (dbSublistList.contains(removeSublistId)) {
                            groupMappedSublistList.add(removeSublistId);
                        }
                    }
                    //Removing the private list since the
                    groupMappedSublistList.remove(corpGroupInfoPersistDTO.getGroupMemberListId());
                }
                knLogger.debug( methodName, "The sublist present in db from the request ids are - " , groupMappedSublistList);
                if (requestSublistIds != null && !requestSublistIds.isEmpty() &&
                        groupMappedSublistList != null && !groupMappedSublistList.isEmpty()) {
                    int removedSublistSize = requestSublistIds.size();
                    int groupMappedSublistSize = groupMappedSublistList.size();
                    if (removedSublistSize != groupMappedSublistSize) {
                        knLogger.debug( methodName, "Removed sublist ids were not found in DB for the group");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBLIST_DOES_NOT_BELONG_TO_CORP,
                                "Removed sublist ids were not found in DB for the group", getEntityId(),
                                getOperationType(), getRuleId(), requestSublistIds.toString(), "");
                    }
                } else if (requestSublistIds != null && !requestSublistIds.isEmpty()) {
                    knLogger.debug( methodName, "Removed sublist ids were not found in DB for the group");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBLIST_DOES_NOT_BELONG_TO_CORP,
                            "Removed sublist ids were not found in DB for the group", getEntityId(),
                            getOperationType(), getRuleId(), requestSublistIds.toString(), "");
                }
            } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
                KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
                IInputDTO inputDTO = bcGrpPersistDTO.getInputDTO();
                List<Integer> invalidListId = new ArrayList<>();
                if (inputDTO instanceof KnIPCorpGroupInfoDTO) {
                    KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) inputDTO;
                    Collection<Integer> requestSublistIds = groupInfoDTO.getRemovedSublistIds();
                    List<Integer> dbSublist = bcGrpPersistDTO.getExistingSublistIds();

                    for (int listId : requestSublistIds) {
                        if (!dbSublist.contains(listId)) {
                            invalidListId.add(listId);
                        }
                    }
                }
                if (invalidListId.size() > 0) {
                    knLogger.error(methodName, "Removed sublist ids were not found in DB for the group - ", invalidListId);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBLIST_DOES_NOT_BELONG_TO_CORP,
                            "Removed sublist ids were not found in DB for the group", getEntityId(),
                            getOperationType(), getRuleId(), invalidListId.toString(), "");
                }
            }
            else {
                knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnContactDetailsPersistDTO ");
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
