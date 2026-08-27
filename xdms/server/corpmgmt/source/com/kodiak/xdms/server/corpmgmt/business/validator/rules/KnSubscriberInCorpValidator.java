/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * Created by asanjiv on 11/3/15.
 */
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import java.util.Arrays;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnReverseContactPersistDto;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnSubscriberInCorpValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubscriberInCorpValidator.class);

    @Override
    public void validate() throws KnValidationException {
        String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnReverseContactPersistDto) {
            KnReverseContactPersistDto reverseContactPersistDto = (KnReverseContactPersistDto) persistDTO;
            if (reverseContactPersistDto.getSubscriberCount() < 1 && !reverseContactPersistDto.isExternalContact()) {
                knLogger.error(methodName, "mdns does not belong to corporatipon ");
                KnIPCorpContactDTO contactDto=(KnIPCorpContactDTO) reverseContactPersistDto.getInputDTO();
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_NOT_PART_CORP_NOR_EXTERNAL,
                		"MDN does not belong to the corp", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList(contactDto.getMdn()).toString(), "");
            }
        }
    }
}
