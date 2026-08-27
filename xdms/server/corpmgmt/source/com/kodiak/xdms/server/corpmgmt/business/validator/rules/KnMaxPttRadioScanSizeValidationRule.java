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
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpUserProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class KnMaxPttRadioScanSizeValidationRule extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnMaxPttRadioScanSizeValidationRule.class);

    private static final int LOW_PRIORITY=99;

    @Override
    public void validate() throws KnValidationException, KnBOException {

        final String methodName = "validate()";
        IPersistenceDTO persistDTO = getDTO();
        if(persistDTO instanceof KnCorpUserProfilePersistDTO){
            KnCorpUserProfilePersistDTO dto=(KnCorpUserProfilePersistDTO)persistDTO;

            if(getOperationType().equals(KnOperationTypes.CREATE_USER_PROFILE)){
                Set<KnCorpGroupListInfoDTO> requestUpmGroupList=new HashSet<>();
                requestUpmGroupList=dto.getUserProfileDTO().getGroupList();
                knLogger.debug(methodName," request create UpmGroupList scan group size:",requestUpmGroupList);
                int maxSystemPttRadioScanSize=dto.getMaxPttRadioScanSize();
                knLogger.debug(methodName," request group list after removing null priority:",requestUpmGroupList);
                long maxPttRadioScanGroupSize = requestUpmGroupList.stream()
                        .filter(e->e.getGroupPriority()!=null)
                        .map(KnCorpGroupListInfoDTO::getGroupPriority).count();
                knLogger.debug(methodName," maxSystemPttRadioScanSize:",maxSystemPttRadioScanSize," maxPttRadioScanGroupSize :",maxPttRadioScanGroupSize);

                if (maxPttRadioScanGroupSize > maxSystemPttRadioScanSize) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_SCAN_LIST_SIZE_EXCEEDED, "Max Camped Group Size Limit Exceeds for PTTRadio clients",
                            getEntityId(), getOperationType(), getRuleId(),  Arrays.asList(maxSystemPttRadioScanSize).toString(), "");
                }

            }else if(getOperationType().equals(KnOperationTypes.UPDATE_USER_PROFILE)){
                Set<KnCorpGroupListInfoDTO> requestUpmGroupList=new HashSet<>();
                Set<KnCorpGroupListInfoDTO> cbsDBGroupList = dto.getDbUserProfileDTO().getGroupList();
                Set<String> removedUpmGroupReq = dto.getModifyUserProfileDTO().getRemovedGroupIdsList();
                Set<KnCorpGroupListInfoDTO> addedUpmGroupReq = dto.getModifyUserProfileDTO().getAddedGroupList();
                Set<KnCorpGroupListInfoDTO> modifiedUpmGroupReq = dto.getModifyUserProfileDTO().getModifiedGroupList();

                if(cbsDBGroupList!=null) {
                    requestUpmGroupList.addAll(cbsDBGroupList);
                    knLogger.debug(methodName, "After adding record in db:", requestUpmGroupList);
                }
                if(addedUpmGroupReq!=null) {
                    knLogger.debug(methodName," Added UpmGroupReq :",addedUpmGroupReq);
                    requestUpmGroupList.addAll(addedUpmGroupReq);
                    knLogger.debug(methodName, "After adding record in add request:", requestUpmGroupList);
                }
                if(modifiedUpmGroupReq!=null){
                    knLogger.debug(methodName," Modified UpmGroupReq :",modifiedUpmGroupReq);
                    Set<KnCorpGroupListInfoDTO> commonDbNModReq=new HashSet<>();
                    modifiedUpmGroupReq.forEach(p ->
                            cbsDBGroupList.stream()
                                    .filter(p1 -> p.getGroupID().equals(p1.getGroupID()))
                                    .forEach(commonDbNModReq::add));
                    knLogger.debug(methodName,"record common in db and modify request:",requestUpmGroupList);
                    requestUpmGroupList.removeAll(commonDbNModReq);
                    requestUpmGroupList.addAll(modifiedUpmGroupReq);
                    knLogger.debug(methodName,"After adding record in modify request:",requestUpmGroupList);
                }

                if(removedUpmGroupReq!=null&&cbsDBGroupList!=null) {
                    knLogger.debug(methodName," removed group in modify upm:",removedUpmGroupReq);
                    Set<KnCorpGroupListInfoDTO> removedGroups=new HashSet<>();
                    removedUpmGroupReq.forEach(p ->
                            cbsDBGroupList.stream().filter(p1 -> p1.getGroupID().intValue()==Integer.parseInt(p))
                                    .forEach(removedGroups::add));
                    requestUpmGroupList.removeAll(removedGroups);
                    knLogger.debug(methodName," After removal of removed group in modify upm:",requestUpmGroupList);
                }

                knLogger.debug(methodName," request modify UpmGroupList scan group size:",requestUpmGroupList);
                //Set<KnCorpGroupListInfoDTO> removedLowPriority = requestUpmGroupList.stream().filter(e->e.getGroupPriority().equals(LOW_PRIORITY)).collect(Collectors.toSet());
                //requestUpmGroupList.removeAll(removedLowPriority);
                //knLogger.debug(methodName," after removing low priority UpmGroupList scan group size:",requestUpmGroupList);
                int maxSystemPttRadioScanSize=dto.getMaxPttRadioScanSize();
                long maxPttRadioScanGroupSize = requestUpmGroupList.stream()
                        .filter(e->e.getGroupPriority()!=null)
                        .map(KnCorpGroupListInfoDTO::getGroupPriority).count();
                knLogger.debug(methodName," maxSystemPttRadioScanSize:",maxSystemPttRadioScanSize," maxPttRadioScanGroupSize :",maxPttRadioScanGroupSize);

                if (maxPttRadioScanGroupSize > maxSystemPttRadioScanSize) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MAX_SCAN_LIST_SIZE_EXCEEDED, "Max Camped Group Size Limit Exceeds for PTTRadio clients",
                            getEntityId(), getOperationType(), getRuleId(),  Arrays.asList(maxSystemPttRadioScanSize).toString(), "");
                }
            }
        }else {
            knLogger.error("validate()", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpUserProfilePersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }

    }
}
