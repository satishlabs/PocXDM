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
 * Upananda Singha      Feb 21, 2011      7.0
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
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPLicenseSubsListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;


public class KnEtagEquValidationRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnEtagEquValidationRule.class);
     private static final long serialVersionUID = 7526471155622676285L;

    private String CLASS = KnEtagEquValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Validating the etag passed in request and the one present in the db are same or not.");
        IPersistenceDTO persistDTO = getDTO();
        String inputEtag = null;
        String dbEtag = null;
        try {
            if (persistDTO instanceof KnCorpInfoPersistDTO) {
                KnCorpInfoPersistDTO corpInfoPersistDTO = (KnCorpInfoPersistDTO) persistDTO;
                KnIPCorpInfoDTO corpInfoDTO = (KnIPCorpInfoDTO) corpInfoPersistDTO.getInputDTO();
                inputEtag = corpInfoDTO.getETag();
                dbEtag = corpInfoPersistDTO.getEtag();

            } else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO groupPersistDto = (KnCorpGroupInfoPersistDTO) persistDTO;
                dbEtag = String.valueOf(groupPersistDto.getETag());
                if (groupPersistDto.getInputDTO() instanceof KnIPCorpGroupInfoDTO) {
                    KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) groupPersistDto.getInputDTO();
                    inputEtag = String.valueOf(inputDTO.getETag());
                } else if (groupPersistDto.getInputDTO() instanceof KnIPCorpGroupDTO) {
                    KnIPCorpGroupDTO inputDTO = (KnIPCorpGroupDTO) groupPersistDto.getInputDTO();
                    inputEtag = String.valueOf(inputDTO.getETag());
                }


            } else if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
                if (contactPersistDTO.getInputDTO() instanceof KnIPCorpSubscContactListDTO) {
                    KnIPCorpSubscContactListDTO inputDTO = (KnIPCorpSubscContactListDTO) contactPersistDTO.getInputDTO();
                    inputEtag = inputDTO.getETag();
                    dbEtag = String.valueOf(contactPersistDTO.getEtag());
                } else if (contactPersistDTO.getInputDTO() instanceof KnIPCorpContactDTO) {
                    KnIPCorpContactDTO inputDTO = (KnIPCorpContactDTO) contactPersistDTO.getInputDTO();
                    inputEtag = String.valueOf(inputDTO.getEtag());
                    dbEtag = String.valueOf(contactPersistDTO.getEtag());
                }

            } else if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                KnSublistDetailsPersistDTO sublistDetailsPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
                KnIPCorpSublistDTO inputDTO = (KnIPCorpSublistDTO) sublistDetailsPersistDTO.getInputDTO();
                inputEtag = String.valueOf(inputDTO.getETag());
                dbEtag = String.valueOf(sublistDetailsPersistDTO.getCurrentEtag());
            } else if(persistDTO instanceof  KnCorpTGSPersistDTO){
            	KnCorpTGSPersistDTO corpTGSPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            	KnIPTalkGroupDTO inputDTO = (KnIPTalkGroupDTO) corpTGSPersistDTO.getInputDTO();
            	inputEtag = inputDTO.getEtag();
                dbEtag = corpTGSPersistDTO.getEtag();
            }else if(persistDTO instanceof KnCorpInfoPersistDTO){
            	KnCorpInfoPersistDTO knCorpInfoPersistDTO = (KnCorpInfoPersistDTO) persistDTO;
            	dbEtag = knCorpInfoPersistDTO.getEtag();
                IInputDTO inputDTO = knCorpInfoPersistDTO.getInputDTO();
                if (inputDTO instanceof KnIPLicenseSubsListDTO) {
                	KnIPLicenseSubsListDTO ipLicenseSubsListDTO = (KnIPLicenseSubsListDTO) inputDTO;
                	inputEtag = ipLicenseSubsListDTO.getETag();
                }
            }

            else {
                knLogger.error( "validate", "Unexpected DTO passed - " , persistDTO.getClass() ,
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getRuleId(), KnConstants.KEY_DATATYPE_ETAG, null, inputEtag, null);
            }

            if ( dbEtag!= null && dbEtag.equals(inputEtag)) {
                knLogger.error( methodName, "ValidationRule failed. Resource Not modified");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.NO_CHANGE_IN_DOCUMENT_SINCE_LAST_FETCH,
                		 "Resource Not modified", getEntityId(), getOperationType(), getRuleId(), Arrays.asList(dbEtag).toString(), "");
            }
        } finally {
            knLogger.debug( methodName, "EXIT: Validation Completed Successfully means 'Validating the etag passed in request and the one present in the db are same or not.' ");
        }
    }
}
