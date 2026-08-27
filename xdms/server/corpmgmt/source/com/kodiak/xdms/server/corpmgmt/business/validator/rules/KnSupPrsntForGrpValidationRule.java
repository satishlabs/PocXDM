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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/5/11
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class KnSupPrsntForGrpValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnSupPrsntForGrpValidationRule.class);
    private final String CLASS = KnSupPrsntForGrpValidationRule.class.getName();

    public void validate() throws KnValidationException {

        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug( methodName, "ENTRY - Validating If the member Properties passed actually exists for the group. DTO passed - " , persistDTO);
        try {
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) groupPersistDTO.getInputDTO();
                Collection<KnCorpSubscriberDTO> groupMembers = groupPersistDTO.getSublistMembers();
                Collection<KnCorpGroupMemberDTO> groupSupervisor = inputDTO.getGroupSupervisor();
                if (groupSupervisor != null && !groupSupervisor.isEmpty()) {
                	List<String> invalidMdnList=new ArrayList<String>(); 
                    if (groupMembers == null || groupMembers.isEmpty()) {
                    	for(KnCorpGroupMemberDTO knCorpGroupMemberDTO:groupSupervisor){
                         	invalidMdnList.add(knCorpGroupMemberDTO.getMdn());
                         }
                        knLogger.debug( methodName, "Member passed in the supervisor list does not exist for the group. - " , invalidMdnList);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUPERVISOR_DOES_NOT_EXIST_FOR_GROUP,
                                "Member passed in the supervisor list does not exist for the group", getEntityId(), getOperationType(), getRuleId(), invalidMdnList.toString(), "");
                    } else {
                        for (KnCorpSubscriberDTO memberDTO : groupSupervisor) {
                            if (!groupMembers.contains(memberDTO)) {
                            	invalidMdnList.add(memberDTO.getMdn());
                            }
                        }
                        if(invalidMdnList.size()>0) {
                        	 knLogger.error( methodName, "Member passed in the supervisor list does not exist for the group. - " , invalidMdnList);
                             throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUPERVISOR_DOES_NOT_EXIST_FOR_GROUP,
                                     "Member passed in the supervisor list does not exist for the group", getEntityId(), getOperationType(), getRuleId(), invalidMdnList.toString(), "");
                        }
                    }
                }
            } else {
                knLogger.error( methodName, "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnCorpGroupInfoPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }

        } finally {
            knLogger.debug( methodName, "EXIT: Validation Completed Successfully for the sublist present in the group");
        }
    }
}
