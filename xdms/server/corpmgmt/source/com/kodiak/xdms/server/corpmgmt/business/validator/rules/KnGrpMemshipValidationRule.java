/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnGrpMemshipValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        26-03-2011      7.0
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

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpAddlTGInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnMdnDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.formCommaSeperatedIdList;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isNullOrEmpty;

public class KnGrpMemshipValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnGrpMemshipValidationRule.class);

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        Collection<String> dbMdnList = null;
        Collection<String> requestMdnList = null;
        try {
            IPersistenceDTO persistDTO = getDTO();
            knLogger.debug(methodName, "ENTRY Point : ");
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                if (inputDTO instanceof KnIPCorpGroupInfoDTO) {
                    knLogger.debug(methodName, "Object of type KnIPCorpGroupInfoDTO");
                    KnIPCorpGroupInfoDTO corpGroupInfoDTO = (KnIPCorpGroupInfoDTO) inputDTO;
                    requestMdnList = corpGroupInfoDTO.getRemovedMemberMdns();
                    knLogger.debug(methodName, "requestMdnList - ", requestMdnList);
                    knLogger.debug(methodName, "groupPersistDTO.getGroupPrivateList() - ", groupPersistDTO.getGroupPrivateList());
                    KnMdnDetailsPersistDTO contactMdnPersistDto = new KnMdnDetailsPersistDTO();
                    if (requestMdnList != null && !requestMdnList.isEmpty()) {
                        LinkedList<String> validMdnList = new LinkedList<String>();
                        for (String mdn : requestMdnList) {
                            KnCorpSubscriberDTO subs = new KnCorpSubscriberDTO();
                            subs.setMdn(mdn);
                            if (groupPersistDTO.getGroupPrivateList() != null && groupPersistDTO.getGroupPrivateList().contains(subs)) {
                                validMdnList.add(mdn);
                            }
                        }
                        knLogger.debug(methodName, "validMdnList - ", validMdnList);
                        groupPersistDTO.setRemovedMemberList(validMdnList);
                    }
                    knLogger.debug(methodName, "After getSubscriberContactListInfo result obtained Subscribers contact- "
                            , contactMdnPersistDto);
                    dbMdnList = groupPersistDTO.getRemovedMemberList();
                } else {
                    knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                            ", Expected Dto - KnIPCorpContactListDTO/KnIPCorpSubscContactListDTO ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                            "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                            getEntityId(), getOperationType(), getRuleId(), "DataType", "");
                }
                Collection<String> nonGroupMembers = new ArrayList<String>();
                String invalidMdn = null;
                if (dbMdnList != null && !dbMdnList.isEmpty()) {
                    if (requestMdnList != null && !requestMdnList.isEmpty()) {
                        for (String subscMdn : requestMdnList) {
                            if (!dbMdnList.contains(subscMdn)) {
                                nonGroupMembers.add(subscMdn);
                            }
                        }
                    }
                } else {
                    if (requestMdnList != null && !requestMdnList.isEmpty()) {
                        for (String subscMdn : requestMdnList) {
                            nonGroupMembers.add(subscMdn);
                        }
                    }
                }
                invalidMdn = formCommaSeperatedIdList(nonGroupMembers);

                if (!isNullOrEmpty(invalidMdn)) {
                    knLogger.error(methodName, "The Deleted Members in request do not belong to the group - ", invalidMdn
                    );
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBERS_DOES_NOT_EXIST_FOR_GROUP,
                            "Deleted Members are not partb of group.", getRuleId(),
                            KnConstants.KEY_DATATYPE_MDN, "", Arrays.asList(invalidMdn).toString(), "");
                }
            } else if (persistDTO instanceof KnCorpTGSPersistDTO) {
                /**
                 * This validation block is for Assign-Unassign TGS to a subscriber.
                 * If input subscriber does not belong to any one of the group, this will throw
                 * member not a part of group error.
                 */
				List<Integer> failedGrpList = new ArrayList<Integer>();
				KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
				if (inputDTO instanceof KnIPTalkGroupDTO) {
					KnIPTalkGroupDTO ipTalkGroupDTO = (KnIPTalkGroupDTO) inputDTO;
					List<Integer> subsGrpList = tgsPersistDTO.getSubsGroupList();
					List<KnXDMTalkGroupInfoDTO> addedCampGrpList = ipTalkGroupDTO.getAddedCampGrpList();
                    Collection<KnCorpAddlTGInfoDTO> addlTGInfoDTOS = ipTalkGroupDTO.getAddedAddlTgList();
                    if(addedCampGrpList != null){
                        for (KnXDMTalkGroupInfoDTO addedCampGrp : addedCampGrpList) {
                            if (!subsGrpList.contains(addedCampGrp.getGroupId())) {
                                failedGrpList.add(addedCampGrp.getGroupId());
                            }
                        }
                    } else if(addlTGInfoDTOS != null){
                        failedGrpList.addAll(addlTGInfoDTOS.stream().filter(addlTGInfoDTO ->
                                !subsGrpList.contains(addlTGInfoDTO.getGroupId())).map(KnCorpAddlTGInfoDTO::getGroupId)
                                .collect(Collectors.toList()));
                    }

				}
				if (!failedGrpList.isEmpty()) {
					knLogger.error(methodName, "Group does not have subscriber as group member, failed group list-", failedGrpList);
					throw new KnCorpBOValidationException(KnErrorCodes.Validator.MDN_DOESNOT_BELONG_TO_GROUP,
							"Group does not have subscriber as group member", getRuleId(), KnConstants.KEY_DATATYPE_MDN, "", failedGrpList.toString(), "");
				}
            } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
                //This validation block is added for modify broadcast Group call.
                //If the removed member does not exit in the private group member list throw exception
                KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
                IInputDTO inputDTO = bcGrpPersistDTO.getInputDTO();
                List<KnCorpSubscriberDTO> privateMemList = bcGrpPersistDTO.getPrivateMemList();
                knLogger.debug(methodName, "privateMemList - ", privateMemList);
                List<String> invalidMdn = new ArrayList<>();
                if (inputDTO instanceof KnIPCorpGroupInfoDTO) {
                    KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) inputDTO;
                    Collection<String> removedReqMdnLst = groupInfoDTO.getRemovedMemberMdns();
                    knLogger.debug(methodName, "removedReqMdnLst - ", removedReqMdnLst);
                    if (null != removedReqMdnLst) {
                        for (String remMdn : removedReqMdnLst) {
                            KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO(remMdn);
                            if ( privateMemList != null && !privateMemList.contains(subscriberDTO)) {
                                invalidMdn.add(remMdn);
                            }
                        }
                    }
                }

                if (invalidMdn.size() > 0) {
                    knLogger.error(methodName, "The Deleted Members in request do not belong to the group - ", invalidMdn);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.MEMBERS_DOES_NOT_EXIST_FOR_GROUP,
                            "Deleted Members are not part of group.", getRuleId(),
                            KnConstants.KEY_DATATYPE_MDN, "", invalidMdn.toString(), "");
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(), ", Expected Dto - KnCorpGroupInfoPersistDTO ");
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - "
						+ persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
			}
        } finally {
            knLogger.debug(methodName, "EXIT: Validation of data is successful.");
        }
    }
}
