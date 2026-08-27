/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnResourceNotModifiedValidationRule.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Gunjan Kumar      April 10, 2014      7.9
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

import java.util.Arrays;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

/**
 * KnFetchSizeValRule is a Rule for validating fetch Size, Max limit of contacts
 * can be fetch from DB, Applicable only when filterType != 0
 *
 * @author kodiak
 */
public class KnFetchSizeValRule extends KnValidatorRule {

    private static final long serialVersionUID = -1068706630238114891L;
    private static final KnLogger knLogger = KnLogger.getLogger(KnFetchSizeValRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        int maxfetchSize = getMaxFetchSize("MAX_PAGE_SIZE");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnCorpInfoPersistDTO) {
                KnIPCorpInfoDTO inputDTO = (KnIPCorpInfoDTO) persistDTO.getInputDTO();
                if (inputDTO.getFilterType() != 0 && inputDTO.getFetchSize() > maxfetchSize) {
                	knLogger.error("Fetch Size can not be greater then  the configured value ",maxfetchSize);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_FETCH_SIZE_EXCEED,
                            "Fetch Size can not be greater then " + maxfetchSize, getEntityId(), getOperationType(), getRuleId(), Arrays.asList(maxfetchSize).toString(), "");
                }

            }
            knLogger.debug(methodName, "Validation Completed Successfully");
        } finally {
            knLogger.debug(methodName, "Exit Point");
        }
    }

    private int getMaxFetchSize(String key) {
        final String methodName = "getMaxFetchSize(key)";
        int maxfetchSize;
        try {
            maxfetchSize = Integer.parseInt(System.getenv(key));
        } catch (Exception e) {
            maxfetchSize = 1000;
            knLogger.warn(methodName, "MAX_PAGE_SIZE IS NOT CONFIGURED IN SYSTEM ENV, USING DEFAULT VALUE(1000) ", e);
        }
        return maxfetchSize;
    }
}
