/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnEtagMatchValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        09-02-2011      7.0
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

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPLicenseSubsListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isNullOrEmpty;

import java.util.Arrays;

import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

public class KnEtagNotMatchValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnEtagNotMatchValidationRule.class);
    private static final long serialVersionUID = 7526471155622676287L;

    private String CLASS = KnEtagNotMatchValidationRule.class.getName();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Validating the etag passed in request and the one present ind db are same or not. ");
        IPersistenceDTO persistDTO = getDTO();
        long requestEtag = 0;
        long currentEtag = 0;
        try {
            if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                KnSublistDetailsPersistDTO sublistDetails = (KnSublistDetailsPersistDTO) persistDTO;
                IInputDTO inputDTO = sublistDetails.getInputDTO();
                if (inputDTO instanceof KnIPCorpSublistInfoDTO) {
                    KnIPCorpSublistInfoDTO sublistInfo = (KnIPCorpSublistInfoDTO) inputDTO;
                    requestEtag = sublistInfo.getETag();
                    currentEtag = sublistDetails.getCurrentEtag();
                } else if (inputDTO instanceof KnIPCorpSublistDTO) {
                    KnIPCorpSublistDTO sublistDTO = (KnIPCorpSublistDTO) inputDTO;
                    requestEtag = sublistDTO.getETag();
                    currentEtag = sublistDetails.getCurrentEtag();
                }
            } else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO groupPersistDto = (KnCorpGroupInfoPersistDTO) persistDTO;
                if (groupPersistDto.getGroupCreatedBy() == KnConstants.GROUP_CREATED_BY.CAT.value()) {
                    IInputDTO inputDTO = groupPersistDto.getInputDTO();
                    if (inputDTO instanceof KnIPCorpGroupInfoDTO) {
                        KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) groupPersistDto.getInputDTO();
                        requestEtag = groupInfoDTO.getETag();
                    } else if (inputDTO instanceof KnIPCorpGroupDTO) {
                        KnIPCorpGroupDTO corpGroupDTO = (KnIPCorpGroupDTO) groupPersistDto.getInputDTO();
                        requestEtag = corpGroupDTO.getETag();
                    }

                    currentEtag = groupPersistDto.getETag();
                }

            } else if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
                if (contactPersistDTO.getInputDTO() instanceof KnIPCorpSubscContactListDTO) {
                    KnIPCorpSubscContactListDTO inputDTO = (KnIPCorpSubscContactListDTO) contactPersistDTO.getInputDTO();
                    if (!isNullOrEmpty(inputDTO.getETag())) {
                        requestEtag = Long.parseLong(inputDTO.getETag());
                    }
                    currentEtag = contactPersistDTO.getEtag();
                } else if (contactPersistDTO.getInputDTO() instanceof KnIPCorpContactDTO) {
                    KnIPCorpContactDTO inputDTO = (KnIPCorpContactDTO) contactPersistDTO.getInputDTO();
                    currentEtag = contactPersistDTO.getEtag();
                    requestEtag = inputDTO.getEtag();
                } else if (contactPersistDTO.getInputDTO() instanceof KnIPCorpContactListDTO) {
                    KnIPCorpContactListDTO inputDTO = (KnIPCorpContactListDTO) contactPersistDTO.getInputDTO();
                    currentEtag = contactPersistDTO.getEtag();
                    requestEtag = Long.parseLong(inputDTO.getETag());
                }
            } else if (persistDTO instanceof KnCorpTGSPersistDTO) {
                KnCorpTGSPersistDTO corpTGSPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
                KnIPTalkGroupDTO inputDTO = (KnIPTalkGroupDTO) corpTGSPersistDTO.getInputDTO();
                if (inputDTO.getEtag() != null && inputDTO.getEtag().length() > 0) {
                    requestEtag = Long.parseLong(inputDTO.getEtag());
                }
                if (corpTGSPersistDTO.getEtag() != null && corpTGSPersistDTO.getEtag().length() > 0) {
                    currentEtag = Long.parseLong(corpTGSPersistDTO.getEtag());
                } else {
                    requestEtag = 0;
                }
            } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
                KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
                if (bcGrpPersistDTO.getGroupCreatedBy() == KnConstants.GROUP_CREATED_BY.CAT.value()) {
                    currentEtag = bcGrpPersistDTO.getETag();
                    IInputDTO inputDTO = bcGrpPersistDTO.getInputDTO();
                    if (inputDTO instanceof KnIPCorpGroupInfoDTO) {
                        KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) inputDTO;
                        requestEtag = groupInfoDTO.getETag();
                    }
                }
            } else if (persistDTO instanceof KnCorpInfoPersistDTO) {
                KnCorpInfoPersistDTO corpInfoPersistDTO = (KnCorpInfoPersistDTO) persistDTO;
                currentEtag = Long.parseLong(corpInfoPersistDTO.getEtag());
                IInputDTO inputDTO = corpInfoPersistDTO.getInputDTO();
                if (inputDTO instanceof KnIPCorpInfoDTO) {
                    KnIPLicenseSubsListDTO ipLicenseSubsListDTO = (KnIPLicenseSubsListDTO) inputDTO;
                    requestEtag = Long.parseLong(ipLicenseSubsListDTO.getETag());
                }
            } else {
                knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                        ", Expected Dto - KnContactDetailsPersistDTO ");
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                        "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                        getRuleId(), "Etag", "" + currentEtag, "" + requestEtag, "");
            }
            if (currentEtag != requestEtag) {
                if (requestEtag == -9999) { //do nothing
                } else{
                    knLogger.error("VersionId Mismatch ");
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.DOCUMENT_DOES_NOT_MATCH,
                            "VersionId Mismatch", getRuleId(), "Etag", "CurrentEtag " + currentEtag, Arrays.asList(requestEtag).toString(), "");
                }
            }
        } finally {
            knLogger.debug(methodName, "Exit Point: Validation Completed Successfully for the etag passed");
        }
    }
}
