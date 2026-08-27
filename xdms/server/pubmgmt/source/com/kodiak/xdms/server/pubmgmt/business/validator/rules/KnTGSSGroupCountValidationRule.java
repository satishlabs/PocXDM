/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.business.validator.KnPubBOValidationException;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnTGSSListPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;

import static com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes.Validator.MAX_GROUP_SIZE_REACHED;

import com.kodiak.common.resources.KnGDPRTemplate;
/**
Created by venkata sudhakar talluri on 10-01-2019
*/
public class KnTGSSGroupCountValidationRule extends KnValidatorRule {
    //validate group count should not excceed the configured value.(Using Subscriber/Corp/System level Dedicated Simultaneous Session configuration preference order)
    private static final KnLogger knLogger = KnLogger.getLogger(KnTGSSGroupCountValidationRule.class);

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        int grpSSCnt = 0;
        int subsGrpSScnt = 0;


        knLogger.debug(methodName, "ENTRY : Validating max group count for simultanous session" + persistDTO);
        try {

            if (persistDTO instanceof KnTGSSListPersistDTO) {
                KnTGSSListPersistDTO tgssListPersistDTO = (KnTGSSListPersistDTO) persistDTO;
                grpSSCnt = tgssListPersistDTO.getGroupIdList().size();
                grpSSCnt = grpSSCnt+1;
                subsGrpSScnt = tgssListPersistDTO.getMaxSDDSession();
                if(grpSSCnt>subsGrpSScnt) {
                    knLogger.error(methodName, "maximum simultainous session group count is execeding ", KnGDPRTemplate.mdn(tgssListPersistDTO.getMdn()));
                    throw new KnPubBOValidationException(MAX_GROUP_SIZE_REACHED, "maximum simultainous session group count is execeding- " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN", tgssListPersistDTO.getMdn());
                }
            } else {
                throw new KnPubBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "MDN", "");
            }
        } finally {
            knLogger.debug(methodName, "Exit Point");
        }
    }
    }