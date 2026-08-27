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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupMemberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 11/30/11
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class KnMaxContPerReqValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxContPerReqValidationRule.class);

    /**
     * This method is used to validate the datafor unique sublist Name for the corporation
     *
     * @throws com.kodiak.xdms.server.common.framework.validator.KnValidationException
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        int maxAllowedContactsPerRequest = 0;
        int currentContactLimit = 0;
        if (persistDTO instanceof KnContactDetailsPersistDTO) {
            KnContactDetailsPersistDTO contactPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
            maxAllowedContactsPerRequest = contactPersistDTO.getMaxAllowedContactCountPerRequest();
            currentContactLimit = contactPersistDTO.getContactCountInRequest();
        } else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            maxAllowedContactsPerRequest = groupInfoPersistDTO.getMaxAllowedContactCountPerRequest();
            currentContactLimit = groupInfoPersistDTO.getContactCountInRequest();
        } else if (persistDTO instanceof KnSublistDetailsPersistDTO) {
            KnSublistDetailsPersistDTO sublistDetailsPersistDTO = (KnSublistDetailsPersistDTO) persistDTO;
            maxAllowedContactsPerRequest = sublistDetailsPersistDTO.getMaxAllowedContactCountPerRequest();
            currentContactLimit = sublistDetailsPersistDTO.getContactCountInRequest();
        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            if (inputDTO instanceof KnIPCorpGroupInfoDTO) {
                KnIPCorpGroupInfoDTO groupInputDTO = (KnIPCorpGroupInfoDTO) inputDTO;
                if (groupInputDTO.getOperationType().equals(KnOperationTypes.CREATE_BROADCAST_GROUP)) {
                    currentContactLimit = groupInputDTO.getAddedMemberDTOMdns().size();
                } else {
                    Collection<KnCorpContactDTO> addedMemberDTOLst = groupInputDTO.getAddedMemberDTOMdns();
                    List<String> removedMemberList = groupInputDTO.getRemovedMemberMdns();
                    Collection<KnCorpGroupMemberDTO> modifiedMembers = groupInputDTO.getModifiedMembers();
                    if (null != addedMemberDTOLst) {
                        currentContactLimit = currentContactLimit + addedMemberDTOLst.size();
                    }
                    if (null != removedMemberList) {
                        currentContactLimit = currentContactLimit + removedMemberList.size();
                    }
                    if (null != modifiedMembers) {
                        currentContactLimit = currentContactLimit + modifiedMembers.size();
                    }
                }

                maxAllowedContactsPerRequest = bcGrpPersistDTO.getMaxContactsPerRequest();
            }
        }
        if (currentContactLimit > maxAllowedContactsPerRequest) {
        	knLogger.error("Contacts/Members in the request exceeds the maximum contacts allowed per request...");
        	throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_CONTACT_PER_REQUEST_EXCEEDED,
                    "Contacts/Members in the request exceeds the maximum contacts allowed per request. currentContactLimit - "
                            + currentContactLimit, getEntityId(), getOperationType(), getRuleId(),
                            Arrays.asList(maxAllowedContactsPerRequest).toString(), "");
        }
    }
}
