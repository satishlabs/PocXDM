/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/14/11
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnGrpExtDisprMemValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnGrpExtDisprMemValidationRule.class);
    private String CLASS = KnGrpExtDisprMemValidationRule.class.getName();

    /**
     * This method is used to validate the datafor unique sublist Name for the corporation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, "persistDTO obtained from the request is - " , persistDTO);
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - " , persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnCorpGroupInfoPersistDTO groupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                Collection<KnCorpSubscriberDTO> dbExternalContacts = groupInfoPersistDTO.getExternalContacts();
                if (inputDTO instanceof KnIPCorpGroupInfoDTO) {
                    Collection<String> requestDispatcherSubscriber = corpGroupInfoPersistDTO.getRequestDispatcherSubscribers();
                    if (dbExternalContacts != null && !dbExternalContacts.isEmpty()) {
                        if (requestDispatcherSubscriber != null && !requestDispatcherSubscriber.isEmpty()) {
                        	List<String> extDispMdn=new ArrayList<String>();
                            for (String mdn : requestDispatcherSubscriber) {
                                KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                                subsc.setMdn(mdn);
                                if (dbExternalContacts.contains(subsc)) {
                                	extDispMdn.add(mdn);
                                }
                            }
                            if(extDispMdn.size() > 0){
                            	knLogger.error("External Contact(s) can not be a Dispatcher.");
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.EXTERNAL_CONTACT_CANNOT_BE_DISPATCHER,
                                    "External Contact(s) can not be a Dispatcher. -", getEntityId(), getOperationType(), getRuleId(),
                                    "DataType", "");
                            }
                        }
                    }
                } else {
                    knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                            ", Expected Dto - KnIPCorpGroupInfoDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }
            }         } finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully");
        }
    }
}


