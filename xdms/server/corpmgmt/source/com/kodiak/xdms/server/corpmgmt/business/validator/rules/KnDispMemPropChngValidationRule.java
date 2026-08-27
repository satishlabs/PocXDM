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
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kodiak.logger.KnLogger;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12/9/11
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnDispMemPropChngValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnDispMemPropChngValidationRule.class);
     private static final long serialVersionUID = 7526471155622676281L;
    private String CLASS = KnDispMemPropChngValidationRule.class.getName();

    /**
     * This method is used to validate the If any dispatch members property is changed to
     * anything other than 2 i.e trying to  assign hima a normal subsc or a supervisor
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     *
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                knLogger.debug( methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - " , persistDTO);
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
                knLogger.debug( methodName, "groupInfoDTO - " , groupInfoDTO);
                if (groupInfoDTO.getGroupType() == KnConstants.DISPATCH_GROUP) {
                    Collection<KnCorpGroupMemberDTO>  supervisorMembers = groupInfoDTO.getGroupSupervisor();
                    Collection<String> dispatchers = corpGroupInfoPersistDTO.getDbDispatcherSubscribers();
                    if (dispatchers != null && !dispatchers.isEmpty()) {
                        if (supervisorMembers != null && !supervisorMembers.isEmpty()) {
                        	List<String> supervisorUptdList=new ArrayList<String>();
                            for (KnCorpGroupMemberDTO memberDTO : supervisorMembers) {
                                if (dispatchers.contains(memberDTO.getMdn())) {
                                    if (memberDTO.getSupervisory() != 2) {
                                    	supervisorUptdList.add(memberDTO.getMdn());
                                    }
                                }
                            }
                            if(supervisorUptdList.size() > 0){
                            	knLogger.error("Dispatch group members property cannot be changed ");
                            	throw new KnCorpBOValidationException(KnErrorCodes.Validator.DISPATCH_MEMBER_PROPERTY_CANNOT_CHANGE,
                                        "Dispatch group members property cannot be changed.", getEntityId(), getOperationType(), getRuleId(),
                                        supervisorUptdList.toString(), "");
                            }
                        }
                    }
                }
            }    
         } finally {
            knLogger.debug( methodName, "Exit Point: Validation Completed Successfully");
        }
    }
}
