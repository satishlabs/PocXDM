/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpValidSubscrValidator.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv Kumar      Aug 14, 2014      7.10
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnCorpValidSubscrValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnBCGrpFeatureValidator.class);

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
            KnCorpBCGrpPersistDTO corpBCGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            IInputDTO inputDTO = corpBCGrpPersistDTO.getInputDTO();
           if(inputDTO instanceof KnIPCorpGroupInfoDTO){
               KnIPCorpGroupInfoDTO corpGroupInfoDTO = (KnIPCorpGroupInfoDTO) inputDTO;
               int reqMemCount = corpGroupInfoDTO.getAddedMemberDTOMdns().size();
               int validMemCount = corpBCGrpPersistDTO.getValidExtContacts().size() +
                       corpBCGrpPersistDTO.getValidInternalCont().size();
               
               Collection<KnCorpContactDTO> addedMdnList=corpGroupInfoDTO.getAddedMemberDTOMdns();
               Collection<KnCorpSubscriberDTO> validIntrnlCntctList=corpBCGrpPersistDTO.getValidInternalCont();
               Collection<KnCorpSubscriberDTO> validExtCntsList=corpBCGrpPersistDTO.getValidExtContacts();
               
               List<String> invalidExtContList=new ArrayList<String>();
               for(KnCorpContactDTO knCorpContactDTO: addedMdnList){
            	   invalidExtContList.add(knCorpContactDTO.getMdn());
               }
               for(KnCorpSubscriberDTO knCorpSubscriberDTO: validExtCntsList){
            	   invalidExtContList.remove(knCorpSubscriberDTO.getMdn());
               }
               for(KnCorpSubscriberDTO knCorpSubscriberDTO: validIntrnlCntctList){
            	   invalidExtContList.remove(knCorpSubscriberDTO.getMdn());
               }
               
               if(validMemCount < reqMemCount){
            	   knLogger.error("External subscriber not mapped to the corporate...");
                   throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_EXTERNAL_SUBSCRIBERS,
                		   "External subscriber not mapped to the corporate", getEntityId(), getOperationType(), getRuleId(), invalidExtContList.toString(), "");
               }
           }

        }else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpBCGrpPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
    }
}
