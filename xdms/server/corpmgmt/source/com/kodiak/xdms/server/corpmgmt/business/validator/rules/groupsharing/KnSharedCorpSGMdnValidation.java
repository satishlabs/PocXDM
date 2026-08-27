/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupsharing;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnSharedCorpSGMdnValidation extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSharedCorpSGMdnValidation.class);
    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        int groupCorpId;
        int requestCorpId = 0;
        List<String> invalidMDN = new ArrayList<>();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            Collection<KnCorpSubscriberDTO> mdnList = groupPersistDTO.getAddedMdnDTO();
            groupCorpId = groupPersistDTO.getCorpId();
            if(groupPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO){
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) groupPersistDTO.getInputDTO();
                requestCorpId = inputDTO.getCorpId();
            }
            if(mdnList != null) {
                if (groupCorpId != requestCorpId) {
                    for (KnCorpSubscriberDTO subscriberDTO : mdnList) {
                        if (subscriberDTO.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value() ||
                                subscriberDTO.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
                            invalidMDN.add(subscriberDTO.getMdn());
                        }
                    }
                }
            }
        }else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            Collection<KnCorpSubscriberDTO> mdnList = bcGrpPersistDTO.getValidInternalCont();
            groupCorpId = bcGrpPersistDTO.getCorpId();
            if(bcGrpPersistDTO.getInputDTO() instanceof KnIPCorpGroupInfoDTO){
                KnIPCorpGroupInfoDTO inputDTO = (KnIPCorpGroupInfoDTO) bcGrpPersistDTO.getInputDTO();
                requestCorpId = inputDTO.getCorpId();
            }
            if(groupCorpId != requestCorpId){
                for(KnCorpSubscriberDTO subscriberDTO : mdnList){
                    if(subscriberDTO.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value() ||
                            subscriberDTO.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()){
                        invalidMDN.add(subscriberDTO.getMdn());
                    }
                }
            }
        }
        if(!invalidMDN.isEmpty()){
            knLogger.debug(methodName, "SGMdn and SGMdnPatch cannot be add from shared corp");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBER_MDN_INVALID_CLIENT_TYPE,
                    "SGMdn and SGMdnPatch cannot be add from shared corp", getEntityId(),
                    getOperationType(), getRuleId(), invalidMDN.toString(), "");

        }
    }
}
