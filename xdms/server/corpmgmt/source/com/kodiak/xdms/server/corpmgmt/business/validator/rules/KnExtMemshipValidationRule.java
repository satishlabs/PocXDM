/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnExternalContactValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        25-01-2011      7.0
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

import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;

import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.determineMaxContactLimitFlag;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isNullOrEmpty;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class KnExtMemshipValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnExtMemshipValidationRule.class);

    private String CLASS = KnExtMemshipValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        IInputDTO inputDTO = getInputDTO();
        knLogger.debug(methodName, "ENTRY Validating if the external contact is part of corporate as external contact.");
        try {
            Collection<String> dbExternalContactList = new ArrayList<String>();
            Collection<String> requestExternalContactsList = new ArrayList<String>();
            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactPersistDto = (KnContactDetailsPersistDTO) persistDTO;
                Collection<KnCorpSubscriberDTO> externalContacts;
                Collection<KnCorpSubscriberDTO> reqExternalContacts;
                if (inputDTO instanceof KnIPCorpSubscContactListDTO) {
                    KnIPCorpSubscContactListDTO inputDto = (KnIPCorpSubscContactListDTO) persistDTO.getInputDTO();
                    externalContacts = contactPersistDto.getExternalContacts();
                    reqExternalContacts = inputDto.getExternalContacts();
                    populate(externalContacts, dbExternalContactList);
                    populate(reqExternalContacts, requestExternalContactsList);

                } else if (inputDTO instanceof KnIPCorpContactDTO) {
                    KnIPCorpContactDTO inputDto = (KnIPCorpContactDTO) persistDTO.getInputDTO();
                    externalContacts = contactPersistDto.getExternalContacts();
                    reqExternalContacts = new ArrayList<KnCorpSubscriberDTO>();
                    reqExternalContacts.add(inputDto);
                    populate(externalContacts, dbExternalContactList);
                    populate(reqExternalContacts, requestExternalContactsList);

                } else if (inputDTO instanceof KnIPCorpContactListDTO) {
                    KnIPCorpContactListDTO inputDto = (KnIPCorpContactListDTO) persistDTO.getInputDTO();
                    externalContacts = contactPersistDto.getExternalContacts();
                    reqExternalContacts = inputDto.getContactList();
                    populate(externalContacts, dbExternalContactList);
                    populate(reqExternalContacts, requestExternalContactsList);

                } else {
                    knLogger.error( "validate", "Unexpected InputDTO passed - " , inputDTO.getClass() ,
                            ", Expected Dto - KnIPCorpContactDTO/KnIPCorpSubscContactListDTO/KnIPCorpContactListDTO "
                    );
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of input DTO passed", getRuleId(), KnConstants.KEY_DATATYPE_MDNLIST,
                            "", "", "");
                }
            } else if (persistDTO instanceof KnSublistDetailsPersistDTO) {

                KnSublistDetailsPersistDTO sublistPersistDto = (KnSublistDetailsPersistDTO) persistDTO;
                Collection<KnCorpSubscriberDTO> dbExternalContacts = sublistPersistDto.getCorpExtContacts();
                if (inputDTO instanceof KnIPCorpSublistInfoDTO) {
                    KnIPCorpSublistInfoDTO inputDto = (KnIPCorpSublistInfoDTO) inputDTO;
                    Collection<KnCorpSubscriberDTO> reqExternalContacts = inputDto.getExternalContacts();
                    knLogger.debug( methodName, "inputDTO/RemovedMdnList Containing details " ,
                            "from db about mdn is not empty.");
                    populate(dbExternalContacts, dbExternalContactList);
                    populate(reqExternalContacts, requestExternalContactsList);
                } else {
                    knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                            ", Expected Dto - KnIPCorpSublistInfoDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }

            } else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO groupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                Collection<KnCorpSubscriberDTO> dbExternalContacts = groupInfoPersistDTO.getExternalContacts();
                if (inputDTO instanceof KnIPCorpGroupInfoDTO) {
                    KnIPCorpGroupInfoDTO inputDto = (KnIPCorpGroupInfoDTO) inputDTO;
                    Collection<KnCorpSubscriberDTO> reqExternalContacts = inputDto.getExternalContacts();
                    knLogger.debug( methodName, "inputDTO/RemovedMdnList Containing details " ,
                            "from db about mdn is not empty.");
                    populate(dbExternalContacts, dbExternalContactList);
                    populate(reqExternalContacts, requestExternalContactsList);
                } else {
                    knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                            ", Expected Dto - KnIPCorpGroupInfoDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }
            } else {
                knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnContactDetailsPersistDTO/KnSublistDetailsPersistDTO/KnCorpGroupInfoPersistDTO"
                );
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }

            List<String> notFoundMdns = new ArrayList<String>();
            knLogger.debug( methodName, "requestExternalContactsList- " , KnGDPRTemplate.mdnList(requestExternalContactsList) ,
                    ", dbExternalContactList - " + KnGDPRTemplate.mdnList(dbExternalContactList));
            if (requestExternalContactsList != null) {
                for (String mdn : requestExternalContactsList) {
                    knLogger.debug( methodName, "Checking mdn - " , KnGDPRTemplate.mdn(mdn) , " exists in the DB.");
                    if (!isNullOrEmpty(mdn) && !dbExternalContactList.contains(mdn)) {
                        notFoundMdns.add(mdn);
                    }

                }
            }
            knLogger.debug( methodName, "List of Mdn i.e External Contacts not found in DB are - " , KnGDPRTemplate.mdnList(notFoundMdns));
            if (notFoundMdns.size() > 0) {
                knLogger.error( methodName, "mdns not in DB found for External Contacts  are - " , KnGDPRTemplate.mdnList(notFoundMdns));
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_EXTERNAL_SUBSCRIBERS,
                        "External subscriber not mapped to the corporate",   getEntityId(), getOperationType(), getRuleId(),
                        notFoundMdns.toString(), "");
            }         } finally {
            knLogger.debug ( methodName, "EXIT: Validation Completed Successfully for the external contact is part of corporate as external contact. ");
        }
    }

    private void populate(Collection<KnCorpSubscriberDTO> subsList, Collection<String> mdnList) {
        if (subsList != null && !subsList.isEmpty()) {
            for (KnCorpSubscriberDTO subscDTO : subsList) {
                mdnList.add(subscDTO.getMdn());
            }
        }
    }
}
