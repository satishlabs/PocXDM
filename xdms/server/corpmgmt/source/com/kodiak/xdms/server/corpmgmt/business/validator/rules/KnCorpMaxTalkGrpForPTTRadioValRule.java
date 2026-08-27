/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.PROTOCOL_VERSION_13;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes.CREATE_SUBS_ATG_SCAN_LIST;

public class KnCorpMaxTalkGrpForPTTRadioValRule extends KnValidatorRule {

    private static final long serialVersionUID = 1L;
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpMaxTalkGrpForPTTRadioValRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpTGSPersistDTO) {
            KnCorpTGSPersistDTO corpTGSPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
            int addedSize = 0;
            IInputDTO inputDTO = corpTGSPersistDTO.getInputDTO();
            KnIPTalkGroupDTO ipTalkGroupDTO;
            if (inputDTO instanceof KnIPTalkGroupDTO) {
                ipTalkGroupDTO = (KnIPTalkGroupDTO) inputDTO;
                List<KnXDMTalkGroupInfoDTO> addedCampGrpList = ipTalkGroupDTO.getAddedCampGrpList();
                if (addedCampGrpList != null) {
                    addedSize = addedCampGrpList.size();
                } else {
                    addedSize = ipTalkGroupDTO.getAddedAddlTgList().size();
                }
            }
            if ((corpTGSPersistDTO.isPriorityExists() || !CREATE_SUBS_ATG_SCAN_LIST.equals(corpTGSPersistDTO.getOperationType()))
                    && (corpTGSPersistDTO.getSubsClientType() == PTTRADIOHANDSETCLIENT || corpTGSPersistDTO.getSubsClientType() == PTTRADIOCROSSCARRIERCLIENT
                    || corpTGSPersistDTO.getSubsClientType() == PTTRADIOWIFIONLYCLIENT)) {
                int newOnlyCampSize = corpTGSPersistDTO.getNewOnlyCampedGroups().size();
                if (newOnlyCampSize > corpTGSPersistDTO.getMaxPttRadioScanGrpLmt()) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_SCAN_LIST_SIZE_EXCEEDED, "Max Camped Group Size Limit Exceeds fpr PTTRadio clients",
                            getEntityId(), getOperationType(), getRuleId(), Arrays.asList(corpTGSPersistDTO.getMaxPttRadioScanGrpLmt()).toString(), "");
                }
            }
            if (corpTGSPersistDTO.getNewCampedGroups() != null) {
                List<KnXDMTalkGroupInfoDTO> talkGroupInfoDTO = new ArrayList<>(corpTGSPersistDTO.getNewCampedGroups());


//			talkGroupInfoDTO = talkGroupInfoDTO.stream()
//					.collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>( Comparator.comparing(KnXDMTalkGroupInfoDTO::getGroupId).thenComparing(KnXDMTalkGroupInfoDTO::getChannel))),
//							ArrayList::new));
//
//			knLogger.debug(methodName, "TreeSet ::",talkGroupInfoDTO);

                Map<String, KnXDMTalkGroupInfoDTO> map = new HashMap<>();
                Set<Integer> grpIdSet = new HashSet<>();
                for (KnXDMTalkGroupInfoDTO t : talkGroupInfoDTO) {
                    map.put(t.getGroupId() + ":" + t.getChannel(), t);
                    grpIdSet.add(t.getGroupId());
                }
                talkGroupInfoDTO.clear();
                talkGroupInfoDTO.addAll(map.values());
                knLogger.debug(methodName, "Unique list ::", talkGroupInfoDTO);
                int newSize = grpIdSet.size();
                if (corpTGSPersistDTO.getClientMajorVersion() > 0 && corpTGSPersistDTO.getClientMajorVersion() < PROTOCOL_VERSION_13) {
                    if (newSize > corpTGSPersistDTO.getMaxPttRadioChannelGrpLmt() && addedSize > 0) {
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_CHANNEL_LIST_SIZE_EXCEEDED, "Max channel Group Size Limit Exceeds fpr PTTRadio clients",
                                getEntityId(), getOperationType(), getRuleId(), Arrays.asList(corpTGSPersistDTO.getMaxPttRadioChannelGrpLmt()).toString(), "");
                    }
                }
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(), ", Expected Dto - KnCorpTGSPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - "
                    + persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "Validation Completed Successfully");
    }
}
