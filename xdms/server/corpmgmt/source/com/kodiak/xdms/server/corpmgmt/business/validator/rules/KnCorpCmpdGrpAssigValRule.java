/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     7/11/13         7.7.0
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
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.List;

public class KnCorpCmpdGrpAssigValRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpExtContValRule.class);
    private static final long serialVersionUID = 7526471176890529833L;

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpTGSPersistDTO) {
            KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            List<Integer> campedgrpList = tgsPersistDTO.getAlradyCampedGrpLst();
            List<Integer> failedGrpList = new ArrayList<Integer>();
            IInputDTO inputDTO = persistDTO.getInputDTO();
            if (inputDTO instanceof KnIPTalkGroupDTO) {
                KnIPTalkGroupDTO ipTalkGroupDTO = (KnIPTalkGroupDTO) inputDTO;
                List<Integer> assignedGrpList = ipTalkGroupDTO.getAssigedGrpList();
                if (null != assignedGrpList) {
                    for (int groupId : assignedGrpList) {
                        if (campedgrpList.contains(groupId)) {
                            failedGrpList.add(groupId);
                        }
                    }
                }
            }
            if (!failedGrpList.isEmpty()) {
                knLogger.error(methodName, "Assigning group id already camped to subscriber." +
                        " Already assigned group list", failedGrpList);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_ALREADY_CAMPED, "Group Id Passed already camped",
                        getEntityId(), getOperationType(), getRuleId(), "Business validation", "");
            }

        } else {
            knLogger.error(methodName, "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpTGSPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "Validation Completed Successfully");
    }
}
