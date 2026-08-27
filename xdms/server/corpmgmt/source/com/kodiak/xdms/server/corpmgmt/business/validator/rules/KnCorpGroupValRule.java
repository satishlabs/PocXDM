/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:
 * Subsystem:  POC
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Sanjiv K Acharyya     7/11/13         7.7.0
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

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpAddlTGInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnCorpGroupValRule extends KnValidatorRule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupValRule.class);
	private static final long serialVersionUID = 7526471176890529111L;

	public void validate() throws KnValidationException {
		final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
		IPersistenceDTO persistDTO = getDTO();
		IInputDTO iInputDTO = persistDTO.getInputDTO();
		if (persistDTO instanceof KnCorpTGSPersistDTO && iInputDTO instanceof KnIPTalkGroupDTO ) {
			KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
			KnIPTalkGroupDTO ipTalkGroupDTO = (KnIPTalkGroupDTO) iInputDTO;

			List<Integer> grpListInCorp = tgsPersistDTO.getGrpListInCorp();
			List<KnXDMTalkGroupInfoDTO> addedCampGrpList = ipTalkGroupDTO.getAddedCampGrpList();
			Collection<KnCorpAddlTGInfoDTO> addlTGInfoDTOS = ipTalkGroupDTO.getAddedAddlTgList();
			List<Integer> failedGrpIdList = new ArrayList<Integer>();

			if(addedCampGrpList != null){
				for (KnXDMTalkGroupInfoDTO addedCampGrp : addedCampGrpList) {
					int grpId = addedCampGrp.getGroupId();
					if (!grpListInCorp.contains(grpId)) {
						failedGrpIdList.add(grpId);
					}
				}
			} else if(addlTGInfoDTOS != null){
				failedGrpIdList.addAll(addlTGInfoDTOS.stream().filter(addlTGInfoDTO ->
						!grpListInCorp.contains(addlTGInfoDTO.getGroupId())).map(KnCorpAddlTGInfoDTO::getGroupId)
						.collect(Collectors.toList()));
			}

			if (!failedGrpIdList.isEmpty()) {
				knLogger.error(methodName, "Group ID passed does not exist for Corporation" , "Failed group list", failedGrpIdList);
				throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_DOES_NOT_EXIST, "Group ID passed does not exist for Corporation",
						getEntityId(), getOperationType(), getRuleId(), failedGrpIdList.toString(), "");
			}
		} else {
			knLogger.error(methodName, "Unexpected DTO passed - ", persistDTO.getClass(), ", Expected Dto - KnCorpTGSPersistDTO ");
			throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - "
					+ persistDTO.getClass(), getEntityId(), getOperationType(), getRuleId(), "DataType", "");
		}
		knLogger.debug(methodName, "Validation Completed Successfully");
	}
}
