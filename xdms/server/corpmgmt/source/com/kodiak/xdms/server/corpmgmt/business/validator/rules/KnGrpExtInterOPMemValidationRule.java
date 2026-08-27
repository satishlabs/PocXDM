/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

/**
 * ************************************************************************
 * <p/>
 * File name: KnGrpExtInterOPMemValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name Date Release
 * -------------------- ------------ -------------------------------------
 * Namita P Nair 2/9/12 7.2
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

/**
 * This class validates if an external subscrber is passed as a interOP subscriber in request
 */
public class KnGrpExtInterOPMemValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnGrpExtInterOPMemValidationRule.class);
    private String CLASS = KnGrpExtInterOPMemValidationRule.class.getName();

    /**
     * This method is used to validate if an external subscriber is passed as an interop subscriber
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
                    Collection<String> reqInterOPSubsc = corpGroupInfoPersistDTO.getReqInterOpSubs();
                    if (dbExternalContacts != null && !dbExternalContacts.isEmpty()) {
                        if (reqInterOPSubsc != null && !reqInterOPSubsc.isEmpty()) {
                        	List<String> extInteropSubs=new ArrayList<String>();
                            for (String mdn : reqInterOPSubsc) {
                                KnCorpSubscriberDTO subsc = new KnCorpSubscriberDTO();
                                subsc.setMdn(mdn);
                                if (dbExternalContacts.contains(subsc)) {
                                	extInteropSubs.add(mdn);
                                }
                            }
                            if(extInteropSubs.size() > 0){
                            	knLogger.error("External Contact(s) can not be a InterOP client");
                              throw new KnCorpBOValidationException(KnErrorCodes.Validator.EXTERNAL_CONTACT_CANNOT_BE_INTEROP,
                                    "External Contact(s) can not be a InterOP. - ", getEntityId(), getOperationType(), getRuleId(),
                                    extInteropSubs.toString(), "");
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


