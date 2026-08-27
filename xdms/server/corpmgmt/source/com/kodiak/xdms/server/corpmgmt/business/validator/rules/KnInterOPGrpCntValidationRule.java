/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;
/**
 * ************************************************************************
 * <p/>
 * File name: KnInterOPGrpCntValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name Date Release
 * -------------------- ------------ -------------------------------------
 * Namita P Nair 2/9/12 7.2
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


import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This class is used to validate interop cannot be part of multiple groups.
 */
public class KnInterOPGrpCntValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnInterOPGrpCntValidationRule.class);

    private final String CLASS = KnInterOPGrpCntValidationRule.class.getName();
    private static String INTEROPGRPCOUNTALLOWED = "interOPGroupCountAllowed";

    /**
     * This is to validate the the inter op cannot be part of multiple group.
     *
     * @throws KnValidationException
     */
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        try {
            IPersistenceDTO persistDTO = getDTO();
            knLogger.debug(methodName, "ENTRY Point : ");
            knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
            int grpCountAllowed = Integer.valueOf(getAttribute(INTEROPGRPCOUNTALLOWED));
            knLogger.debug(methodName, "grpCountAllowed - ", grpCountAllowed);
            if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                Map<String, Integer> interOpGroupCount = corpGroupInfoPersistDTO.getInterOPSubscGrpCnt();
                knLogger.debug(methodName, "interOpGroupCount - ", interOpGroupCount);
                List<String> assignedIteropList=new ArrayList<String>();
                if (!KnCorpUtil.isObjectNullOrEmpty(interOpGroupCount)) {
                    for (Map.Entry<String, Integer> entry : interOpGroupCount.entrySet()) {
                        int grpCount = entry.getValue();
                        if (grpCount >= grpCountAllowed) {
                        	assignedIteropList.add(entry.getKey());
                        }
                    }
                    if(assignedIteropList.size() > 0){
                    	knLogger.error("The interOP cannot be part of multiple groups...");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTEROP_SUBSC_CANNOT_BE_PART_OF_MULTIPLE_GRP,
                                "The interOP cannot be part of multiple groups.", getEntityId(), getOperationType(), getRuleId(),
                                assignedIteropList.toString(), "");
                    }
                }

                Map<String, Integer> groupMdnGrpCnt = corpGroupInfoPersistDTO.getGroupMdnGrpCnt();
                knLogger.debug(methodName, "groupMdnGrpCnt - ", groupMdnGrpCnt);
                if (!KnCorpUtil.isObjectNullOrEmpty(groupMdnGrpCnt)) {
                	List<String> assignedGrpMds=new ArrayList<String>();
                    for (Map.Entry<String, Integer> entry : groupMdnGrpCnt.entrySet()) {
                        int grpCount = entry.getValue();
                        if (grpCount >= grpCountAllowed) {
                        	assignedGrpMds.add(entry.getKey());
                        }
                    }
                    if(assignedGrpMds.size() > 0){
                    	knLogger.error("The SGMdn/SGMdnPatch cannot be part of multiple groups....");
                    	 throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_MDN_EXISTS_IN_GROUP,
                                 "The SGMdn/SGMdnPatch cannot be part of multiple groups.", getEntityId(), getOperationType(), getRuleId(),
                                 assignedGrpMds.toString(), "");
                    }
                }
            } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
                KnCorpBCGrpPersistDTO corpGroupInfoPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
                Map<String, Integer> groupMdnGrpCnt = corpGroupInfoPersistDTO.getGroupMdnGrpCnt();
                knLogger.debug(methodName, "groupMdnGrpCnt - ", groupMdnGrpCnt);
                if (!KnCorpUtil.isObjectNullOrEmpty(groupMdnGrpCnt)) {
                	List<String> assignedGrpMdsList=new ArrayList<String>();
                    for (Map.Entry<String, Integer> entry : groupMdnGrpCnt.entrySet()) {
                        int grpCount = entry.getValue();
                        if (grpCount >= grpCountAllowed) {
                        	assignedGrpMdsList.add(entry.getKey());
                        }
                    }
                    if(assignedGrpMdsList.size() > 0){
                    	knLogger.error("The SGMdn/SGMdnPatch cannot be part of multiple groups....");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_MDN_EXISTS_IN_GROUP,
                                "The SGMdn/SGMdnPatch cannot be part of multiple groups.", getEntityId(), getOperationType(), getRuleId(),
                                assignedGrpMdsList.toString(), "");
                    }
                }
                //This is to check the no of interOp in the broadcast as inter Op can be present in only one group
                Map<String, Integer> interOpGrpCount = corpGroupInfoPersistDTO.getInterOpGrpCount();
                Set<String> interOPMdns = interOpGrpCount.keySet();
                List<String> assignedInteropMdsList=new ArrayList<String>();
                for (String mdn : interOPMdns) {
                    if (interOpGrpCount.get(mdn) >= grpCountAllowed) {
                    	assignedInteropMdsList.add(mdn);
                    }
                }
                if(assignedInteropMdsList.size() > 0){
                	knLogger.error("The interOP cannot be part of multiple groups....");
                	 throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTEROP_SUBSC_CANNOT_BE_PART_OF_MULTIPLE_GRP,
                             "The interOP cannot be part of multiple groups.", getEntityId(), getOperationType(), getRuleId(),
                             assignedInteropMdsList.toString(), "");
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnCorpGroupInfoPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getEntityId(), getOperationType(), getRuleId(), "DataType", "");
            }
        } finally {
            knLogger.debug(methodName, "EXIT: Validation of data is successful for the interop group count");
        }
    }
}
