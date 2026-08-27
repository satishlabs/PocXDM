/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
///**
// * *********************************************************************
// * File name:   KnExtCorpIdValidationRule.java
// * Subsystem:   Provisioning Library
// * <p/>
// * Name                  Date         Release
// * -----------------    -----------   -------
// * Ravi Shanker .P       1/13/11   7.0
// * <p/>
// * <p/>
// * #401, 4th Floor, 'Prestige Sigma'
// * No.3, Vittal Mallya Road
// * Bangalore - 560 001
// * www.kodiaknetworks.com
// * All Rights Reserved.
// * <p/>
// * This software is the confidential and proprietary information of Kodiak
// * Networks, Inc. You shall not disclose such confidential information and
// * shall use it only in accordance with the terms of the license agreement
// * you entered into with Kodiak Networks.
// * *************************************************************************
// */
//package com.kodiak.xdms.server.subsmgmt.business.validator.rules;
//
//import com.kodiak.logger.KnLogger;
//import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
//import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
//import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
//import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
//import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
//import com.kodiak.xdms.server.subsmgmt.business.validator.KnProvBOValidationException;
//import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPSubsProvInfoDTO;
//import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
//import com.kodiak.xdms.server.subsmgmt.resources.KnErrorCodes;
//import com.kodiak.xdms.server.subsmgmt.resources.KnProvConstants;
//
//public class KnExtCorpIdValidationRule extends KnValidatorRule {
//	private static final KnLogger knLogger = KnLogger.getLogger(KnExtCorpIdValidationRule.class);
//    private static final String className = KnExtCorpIdValidationRule.class.getName();
//    private static KnProvInfoUtil provInfoUtil;
//
//    static {
//        provInfoUtil = new KnProvInfoUtil();
//    }
//
//    public void validate() throws KnValidationException {
//        String methodName = "validate()";
//        IPersistenceDTO persistDTO = getDTO();
//
//        knLogger.info( methodName, "Validating Ext CorpId with DTO - " + persistDTO);
//        KnSubsProfilePersistDTO subsProfilePersistDTO = null;
//        if (persistDTO instanceof KnSubsProfilePersistDTO) {
//            subsProfilePersistDTO = (KnSubsProfilePersistDTO) persistDTO;
//        } else {
//            knLogger.error( methodName, "Invalid DTO is passed for validation " + persistDTO.getClass());
//            throw new KnProvBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
//                    "Un-expected dao DTO is passed - " + persistDTO.getClass(),
//                    getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
//        }
//
//        String oldExtCorpId = subsProfilePersistDTO.getExtCorpId();
//        KnIPSubsProvInfoDTO subsProvInfoDTO = (KnIPSubsProvInfoDTO) subsProfilePersistDTO.getInputDTO();
//        String newExtCorpId = subsProvInfoDTO.getExtCorpId();
//        knLogger.debug( methodName, "old Ext Corp ID - " + oldExtCorpId);
//        knLogger.debug( methodName, "new Ext Corp ID - " + newExtCorpId);
//        if (newExtCorpId != null && oldExtCorpId != null) {
//            if (!newExtCorpId.equals(oldExtCorpId)) {
//                knLogger.error( methodName, "Ext Corp Id cannot be updated ");
//                throw new KnProvBOValidationException(KnErrorCodes.Validator.UPDATE_EXTCORPID_NOT_ALLOWED,
//                        "Validation Failed. Ext CorpID cannot be updated",
//                        getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
//            }
//        } else if (oldExtCorpId == null) {
//            if (newExtCorpId != null) {
//                knLogger.error( methodName, "Ext Corp Id cannot be updated ");
//                throw new KnProvBOValidationException(KnErrorCodes.Validator.UPDATE_EXTCORPID_NOT_ALLOWED,
//                        "Validation Failed. Ext CorpID cannot be updated",
//                        getEntityId(), getOperationType(), getRuleId(), KnProvConstants.KEY_DATATYPE_MDN);
//            }
//
//        } else {
//            knLogger.debug( methodName, "ext corp id validation is success");
//        }
//    }
//}
