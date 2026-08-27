/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.kodiak.logger.KnLogger;

/**
 * ************************************************************************
 * <p/>
 * File name: KnInterOPMemPropChngValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name Date Release
 * -------------------- ------------ -------------------------------------
 * Namita P Nair 2/13/12 7.2
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
public class KnInterOPMemPropChngValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnInterOPMemPropChngValidationRule.class);
    private String CLASS = KnInterOPMemPropChngValidationRule.class.getName();

    /**
     * This method is used to validate the If any InterOP members property is changed to
     * anything other than 3 i.e trying to  assign him as a normal subsc or a supervisor or a dispatch
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug( methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - " , persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
                knLogger.debug( methodName, "groupInfoDTO - " , groupInfoDTO);
                Collection<KnCorpGroupMemberDTO> supervisorMembers = groupInfoDTO.getGroupSupervisor();
                Collection<String> interOpSubsc = corpGroupInfoPersistDTO.getDbInterOpSubs();
                if (interOpSubsc != null && !interOpSubsc.isEmpty()) {
                    if (supervisorMembers != null && !supervisorMembers.isEmpty()) {
                    	List<String> invalidIteropMdns=new ArrayList<String>();
                        for (KnCorpGroupMemberDTO memberDTO : supervisorMembers) {
                            if (interOpSubsc.contains(memberDTO.getMdn())) {
                                if (memberDTO.getSupervisory() != 3) {
                                	invalidIteropMdns.add(memberDTO.getMdn());
                                }
                            }
                        }
                        if(invalidIteropMdns.size() > 0){
                        	knLogger.error("InterOP group members property cannot be changed... ");
                              throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTEROP_MEMBER_PROPERTY_CANNOT_CHANGE,
                                 "InterOP group members property cannot be changed.", getEntityId(), getOperationType(), getRuleId(),
                                invalidIteropMdns.toString(), "");
                        }
                    }
                }
            }
        } finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully");
        }
    }
}
