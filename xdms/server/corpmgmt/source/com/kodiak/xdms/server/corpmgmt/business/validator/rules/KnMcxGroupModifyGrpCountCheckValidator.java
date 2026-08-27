/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class KnMcxGroupModifyGrpCountCheckValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMcxGroupModifyGrpCountCheckValidator.class);

	@Override
	public void validate() throws KnValidationException, KnBOException {
		final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpGroupInfoPersistDTO) {
        	KnCorpGroupInfoPersistDTO knCorpGroupInfoPersistDTO=(KnCorpGroupInfoPersistDTO) persistDTO;
        	if(!knCorpGroupInfoPersistDTO.isUPMCall()){
        	String addedMember=null;
        	String deleteMember=null;
            Collection<KnCorpSubscriberDTO> addMdnList = knCorpGroupInfoPersistDTO.getAddedMdnDTO();
            Collection<String> mdnList = new ArrayList<>();
            if(addMdnList!=null && !addMdnList.isEmpty()){
            	addMdnList.forEach(corpSubscriberDTO -> {mdnList.add(corpSubscriberDTO.getMdn());});
			}
			Collection<KnCorpGroupMemberDTO> modifiedMembers = knCorpGroupInfoPersistDTO.getModifiedMembers();
			Collection<String> modifiedMdnList = new ArrayList<>();
			if(modifiedMembers!=null && !modifiedMembers.isEmpty()){
				modifiedMembers.forEach(corpGroupMemberDTO -> {modifiedMdnList.add(corpGroupMemberDTO.getMdn());});
			}

    		if(!KnCorpUtil.isObjectNullOrEmpty(knCorpGroupInfoPersistDTO.getRemovedMemberList())) {
    			
    			for(String remvMdn:knCorpGroupInfoPersistDTO.getRemovedMemberList()) {
    				deleteMember=remvMdn;
    			}
    		}

    		if(deleteMember!=null && !knCorpGroupInfoPersistDTO.getExistingmdns().contains(deleteMember)) {
    			throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBERS_DOES_NOT_EXIST_FOR_GROUP,
                        "removed members are not present .", getEntityId(), getOperationType(), getRuleId(),
						knCorpGroupInfoPersistDTO.getRemovedMemberList().toString(), "");
    		}}
        	
        }else {
            knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                    ", Expected Dto - KnCLGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "", "");//KnConstants.KEY_DATATYPE_MDN, "");
        }
		
	}

}
