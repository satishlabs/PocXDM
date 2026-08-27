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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.Arrays;

public class KnMcxGroupValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMcxGroupValidator.class);

	@Override
	public void validate() throws KnValidationException, KnBOException {
		final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpGroupInfoPersistDTO) {
        	KnCorpGroupInfoPersistDTO knCorpGroupInfoPersistDTO=(KnCorpGroupInfoPersistDTO) persistDTO;
        	String addedMember=null;
        	String deleteMember=null;
        	if(knCorpGroupInfoPersistDTO.getAddedMemberMdnsDTOLst()!=null && knCorpGroupInfoPersistDTO.getAddedMemberMdnsDTOLst().size()>1) {
        		throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_MCX_GROUP_NOT_VALID_MODIFY_REQUEST,
                        "Groups not SGMDN .", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList("MCX_ERROR").toString(), "");
    		}
    		if(knCorpGroupInfoPersistDTO.getRemovedMemberList()!=null && knCorpGroupInfoPersistDTO.getRemovedMemberList().size()>1) {
    			throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_MCX_GROUP_NOT_VALID_MODIFY_REQUEST,
                        "Groups not SGMDN .", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList("MCX_ERROR").toString(), "");
    		}
    		
    		if(!KnCorpUtil.isObjectNullOrEmpty(knCorpGroupInfoPersistDTO.getAddedMemberMdnsDTOLst())){
    			for(KnCorpContactDTO knCorpContactDTO:knCorpGroupInfoPersistDTO.getAddedMemberMdnsDTOLst()) {
    				addedMember=knCorpContactDTO.getMdn();
    				if(knCorpContactDTO.getMemberType()!= KnConstants.SG_MDN_MEMBER_TYPE) {
        				throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_MCX_GROUP_NOT_SG_MDN,
                                "Groups not SGMDN .", getEntityId(), getOperationType(), getRuleId(),
                                Arrays.asList(knCorpContactDTO.getMemberType()).toString(), "");
        			}
    			}
    		}
    		if(!KnCorpUtil.isObjectNullOrEmpty(knCorpGroupInfoPersistDTO.getRemovedMemberList())) {
    			
    			for(String remvMdn:knCorpGroupInfoPersistDTO.getRemovedMemberList()) {
    				deleteMember=remvMdn;
    			}
    		}
    		if(addedMember!=null && knCorpGroupInfoPersistDTO.getGroupMembersList().contains(addedMember)) {
    			throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_MCX_GROUP_NOT_VALID_MODIFY_REQUEST,
                        "Groups not SGMDN .", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList("MCX_ERROR").toString(), "");
    		}
    		if(deleteMember!=null && !knCorpGroupInfoPersistDTO.getGroupMembersList().contains(deleteMember)) {
    			throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_MCX_GROUP_NOT_VALID_MODIFY_REQUEST,
                        "Groups not SGMDN .", getEntityId(), getOperationType(), getRuleId(),
                        Arrays.asList("MCX_ERROR").toString(), "");
    		}
        	
        }else {
            knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                    ", Expected Dto - KnCLGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "", "");//KnConstants.KEY_DATATYPE_MDN, "");
        }
		
	}

}
