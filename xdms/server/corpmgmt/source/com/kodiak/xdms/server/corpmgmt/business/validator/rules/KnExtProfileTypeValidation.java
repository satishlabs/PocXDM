/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnExtProfileDetails;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * *****************************************************************************
 * File name:   KnExtProfileTypeValidation.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * ChandraShekar H S       10/4/14      7.8.1
 * <p/>
 * <p/>
 *
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
 * *******************************************************************************
 */

public class KnExtProfileTypeValidation extends KnValidatorRule {

    private static final long serialVersionUID = 7274278869982679795L;
    private static final KnLogger knLogger = KnLogger.getLogger(KnExtProfileTypeValidation.class);
    String methodName = "validate(IPersistenceDTO persistDTO)";
    public static final String KODIAK_EXT_TYPE = "kodiakExtType";
    @Override
    public void validate() throws KnValidationException {
        try {
            IPersistenceDTO persistDTO = getDTO();

            knLogger.debug(methodName,
                    "ENTRY : Validating External Subscribers profile type Validation Rule");
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactDTO = (KnContactDetailsPersistDTO) persistDTO;
                Map<Integer, KnExtProfileDetails> configuredProfileMap = contactDTO
                        .getConfiguredProfileMap();
                List<Integer> inputProfileIdList = contactDTO
                        .getInputProfileIdList();
                knLogger.debug(methodName, "Validation ", "inputProfileIdList", inputProfileIdList, "Input profile IDs passed.");
                if (inputProfileIdList != null && !inputProfileIdList.isEmpty()) {
                    for (Integer profile : inputProfileIdList){
                        knLogger.debug(methodName, "Validation ", "profile", profile, " Configured profile Type ");
                        if (!configuredProfileMap.containsKey(profile)){
                            knLogger.error(methodName, "Validation failure", "profile", profile, "Invalid Subscriber profile Type passed.");
                            throw new KnCorpBOValidationException(
                                    KnErrorCodes.Validator.INVALID_EXTERNAL_SUBS_TYPE,
                                    "Invalid Subscriber profile Type passed.",
                                    getRuleId(), KnConstants.KEY_DATATYPE_MAX_EXT_PROFILE_TYPE);
                        }
                    }
                }
                IInputDTO inputDTO = persistDTO.getInputDTO();
                if (inputDTO instanceof KnIPCorpContactListDTO) {
                    KnIPCorpContactListDTO corpContactListDTO = (KnIPCorpContactListDTO) inputDTO;
                    Collection<KnCorpSubscriberDTO> contactList = corpContactListDTO.getContactList();
                    List<String> invalidPocMdns=new ArrayList<String>();
                    List<String> invalidNNIMdns=new ArrayList<String>();
                    Collection<String> pocSubscMdnLis = contactDTO.getPocSubscMdnList();
                    for (KnCorpSubscriberDTO subscriberDTO : contactList) {
                        String mdn = subscriberDTO.getMdn();
                        Integer subsType = subscriberDTO.getSubsType();
                        String kodiakExtType = getAttribute(KODIAK_EXT_TYPE);
                        
                        knLogger.error(methodName, "Validating pocMDNs", "kodiak MDNs", pocSubscMdnLis, "");
                        if (pocSubscMdnLis.contains(mdn)) {
                            if (subsType != Integer.parseInt(kodiakExtType)) {
                                invalidPocMdns.add(mdn);
                            }
                        } else {
                            if (subsType == Integer.parseInt(kodiakExtType)) {
                            	invalidNNIMdns.add(mdn);
                            }
                        }
                        if(invalidPocMdns.size() > 0){
                       	     knLogger.error(methodName, "Validation failure", "kodiak MDN", invalidPocMdns, "Substype is not 1");
                         	 throw new KnCorpBOValidationException(
                                    KnErrorCodes.Validator.INVALID_EXTERNAL_SUBS_TYPE,
                                    "Invalid Subscriber Type passed.",
                                    getEntityId(), getOperationType(), getRuleId(), invalidPocMdns.toString(), "");
                       }
                       if(invalidNNIMdns.size() > 0){
                       	     knLogger.error(methodName, "Validation failure", "MDN", invalidNNIMdns, "Substype is invalid");
                       	     throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_EXTERNAL_SUBS_TYPE,
                                    "Invalid Subscriber Type passed.",
                                    getEntityId(), getOperationType(), getRuleId(), invalidNNIMdns.toString(), "");
                       }
                    }
                 }
            }else{
                throw new KnCorpBOValidationException(
                        KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Invalid Persist DTO passed.", getRuleId(),
                        KnConstants.KEY_DATATYPE_MAX_EXT_PROFILE_TYPE);
            }
        }finally {
            knLogger.debug("External Subscribers profile type Validated successfully.");
            knLogger.debug(methodName, "Exit Point");
        }

    }
}
