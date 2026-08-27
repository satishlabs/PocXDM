/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnPoCSublistValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        26-01-2011      7.0
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

import com.kodiak.logger.KnLogger;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;

public class KnSublstMemshipValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSublstMemshipValidationRule.class);
    final String CLASS = KnSublstMemshipValidationRule.class.getName();

    /**
     * This method will validate the CorpId passed in the request is actually present in The DG.CorpInfo table
     * If the Corporate Id is found then its a valid corporate else its a invalid Corporate profile.
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "ENTRY. Validating if sublist belongs to the corporate or not.");
        try {
            Collection<Integer> sublistIds = null;
            Collection<Integer> dbSublistIds = null;
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO corpContactPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
                dbSublistIds = corpContactPersistDTO.getPocSublistIds();
                IInputDTO inputDTO = corpContactPersistDTO.getInputDTO();
                if (inputDTO instanceof KnIPCorpSubscContactListDTO) {
                    KnIPCorpSubscContactListDTO contactListDTO = (KnIPCorpSubscContactListDTO) inputDTO;
                    sublistIds = contactListDTO.getAddedSublistIds();
                } else if ((inputDTO instanceof KnIPCorpSublistSubscDistDTO)) {
                    KnIPCorpSublistSubscDistDTO subscSublistDto = (KnIPCorpSublistSubscDistDTO) inputDTO;
                    sublistIds = subscSublistDto.getSublistIds();
                } else {
                    //todo throw back System error
                }
            } else if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                KnSublistDetailsPersistDTO sublistDetailsPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
                dbSublistIds = sublistDetailsPersistDTO.getSublistIds();
                KnIPCorpSublistInfoDTO sublistInfoDTO = (KnIPCorpSublistInfoDTO) inputDTO;
                sublistIds = sublistInfoDTO.getAddedSublistIds();
            } else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                dbSublistIds = corpGroupInfoPersistDTO.getPoCSublistList();
                KnIPCorpGroupInfoDTO sublistInfoDTO = (KnIPCorpGroupInfoDTO) inputDTO;
                sublistIds = sublistInfoDTO.getAddedSublistIds();
            }else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
                KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
                if(inputDTO instanceof KnIPCorpGroupInfoDTO){
                    KnIPCorpGroupInfoDTO corpGroupInfoDTO = (KnIPCorpGroupInfoDTO) inputDTO;
                    sublistIds = corpGroupInfoDTO.getAddedSublistIds();
                    dbSublistIds = bcGrpPersistDTO.getValidSublistIds();
                }
            }
            else {
                knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
            Collection<Integer> notFoundSublistIds = new ArrayList<Integer>();
            if (!isObjectNull(dbSublistIds) && !dbSublistIds.isEmpty()) {
                knLogger.debug( methodName, "ContactPersistDTO/PocSubscSublistList Containing details " ,
                        "from db about mdn is not empty.");
            } else {
                dbSublistIds = new ArrayList<Integer>();
            }
            if (!isObjectNull(sublistIds) && !sublistIds.isEmpty()) {
                knLogger.debug( methodName, "subscContactlist/SublistsIds passed in request inputDTO is not empty");
                for (int sublistId : sublistIds) {
                    knLogger.debug( methodName, "Checking sublist - " , sublistId , " exists in the DB.");
                    if (!dbSublistIds.contains(sublistId)) {
                        knLogger.debug( methodName, "sublist - " , sublistId , " does not exist in the DB.");
                        notFoundSublistIds.add(sublistId);
                        knLogger.debug( methodName, "SubList not found in DB so far are - " , notFoundSublistIds);
                    }
                }
            }
            if (notFoundSublistIds.size() > 0) {
                knLogger.error( methodName, "sublistId not found in DB for Input Sublistids are - " , notFoundSublistIds);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBLIST_DOES_NOT_EXIST,
                        "Sublist not found in DB", getEntityId(),
                        getOperationType(), getRuleId(), notFoundSublistIds.toString(), "");
            } 
        } finally {
            knLogger.debug( methodName, "EXIT: Validation Completed Successfully means 'sublist belongs to the corporate or not'");
        }
    }
}
