/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;

import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT;
import static com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT;
import static com.kodiak.xdms.server.common.resources.KnConstants.USER_PROFILE_MGMT_BIT;
import static com.kodiak.xdms.server.common.resources.KnConstants.VERY_LARGE_GROUP;

public class KnMcxMcpttTypeValidator extends KnValidatorRule {

    private static final KnLogger knLogger = KnLogger.getLogger(KnMcxMcpttTypeValidator.class);

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating SGMdn and SGMdnPatch availability in same request");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        List<Integer> allowedClientTypes = Arrays.asList(KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value(),
                KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value());


        knLogger.debug(methodName, " allowedClientTypes -", allowedClientTypes);

        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            knLogger.debug(methodName, "DTO passed in the request is - ", groupPersistDTO);
            Collection<KnCorpSubscriberDTO> mdnList = groupPersistDTO.getAddedMdnDTO();
            knLogger.debug(methodName, " MDN List : -- ", mdnList);

            List<String> groupInvaildMdnList = new ArrayList<String>();
            boolean isCriFail = false;
            boolean isMcpttFail = false;
            boolean isLocFlagFail = false;
            if (mdnList != null) {

                for (KnCorpSubscriberDTO subsc : mdnList) {
                    String subsFs2 = subsc.getSubscriberFs2();
                    boolean dispatcher = subsc.getClientType() == DISPATCH_CLIENT.value() || subsc.getClientType() == THIRDPARTYDISPATCHERCLIENT.value();
                    //knLogger.debug(methodName, "subsFs2 - ", subsFs2);
                    knLogger.debug(" mcptt bit ", KnGeneralUtil.getFeatureBitValue(subsFs2, com.kodiak.xdms.server.common.resources.KnConstants.VERY_LARGE_GROUP));
                    if (subsc.isExternalContact()) continue;
                    boolean veryLargeGroup = KnGeneralUtil.getFeatureBitValue(subsFs2, VERY_LARGE_GROUP);
                    knLogger.debug(methodName, " veryLargeGroupBit - ", veryLargeGroup);
                    if (allowedClientTypes.contains(subsc.getClientType()) || (subsc.getMcpttCompliance() == 1 && veryLargeGroup && groupPersistDTO.isCriLocFlag())
                            || (!(subsc.getMcpttCompliance() == 1) && veryLargeGroup && KnGeneralUtil.getFeatureBitValue(subsFs2, USER_PROFILE_MGMT_BIT))
                            || (dispatcher && KnGeneralUtil.getFeatureBitValue(subsFs2, VERY_LARGE_GROUP))) {
                        knLogger.debug(methodName, "Group MDN - ", subsc.getMdn());
                    } else {
                        if (subsc.getMcpttCompliance() != 1 && !dispatcher) {
                            isCriFail = true;
                        } else if (!veryLargeGroup) {
                            isMcpttFail = true;
                        } else if (!groupPersistDTO.isCriLocFlag() && !dispatcher) {
                            isLocFlagFail = true;
                        }
                        groupInvaildMdnList.add(subsc.getMdn());
                    }
                }

            }
            if (!groupInvaildMdnList.isEmpty()) {
                knLogger.debug(methodName, "MCX not MCPTT complance :- ");
                if (isMcpttFail) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_NOT_MCPTT_COMLIANCE,
                            "Groups not MCPTT complance.", getEntityId(), getOperationType(), getRuleId(),
                            groupInvaildMdnList.toString(), "");
                } else if (isCriFail) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_MCX_CRI_MCPTT_FLAG_DISABLED,
                            "Groups not CRI complance.", getEntityId(), getOperationType(), getRuleId(),
                            groupInvaildMdnList.toString(), "");
                } else if (isLocFlagFail) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_MCX_CRI_LOCFLAG_DISABLED,
                            "Groups not CRI Loc Flag disabled.", getEntityId(), getOperationType(), getRuleId(),
                            groupInvaildMdnList.toString(), "");
                }

            }
        }

        if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            KnCorpBCGrpPersistDTO groupPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            knLogger.debug(methodName, "DTO passed in the request is - ", groupPersistDTO);
            Collection<KnCorpSubscriberDTO> mdnList = groupPersistDTO.getPocSubsInfo();
            knLogger.debug(methodName, " MDN List : -- ", mdnList);
            List<String> groupInvaildMdnList = new ArrayList<String>();
            boolean isCriFail = false;
            boolean isMcpttFail = false;
            boolean isLocFlagFail = false;
            if (mdnList != null) {
                for (KnCorpSubscriberDTO subsc : mdnList) {
                    String subsFs2 = subsc.getSubscriberFs2();
                    boolean mcpttComplianceBit = KnGeneralUtil.getFeatureBitValue(subsFs2, com.kodiak.xdms.server.common.resources.KnConstants.MCPTT_COMPLAIANCE_BIT);
                    int mcpttBit = (mcpttComplianceBit) ? 1 : 0;

                    if (allowedClientTypes.contains(subsc.getClientType()) || (subsc.getMcpttCompliance() == 1 && mcpttBit == 1)) {
                        knLogger.debug(methodName, "Group MDN - ", subsc.getMdn());
                    } else {
                        if (subsc.getMcpttCompliance() != 1) {
                            isCriFail = true;
                        } else if (mcpttBit != 1) {
                            isMcpttFail = true;
                        } else if (!groupPersistDTO.isCriLocFlag()) {
                            isLocFlagFail = true;
                        }
                        groupInvaildMdnList.add(subsc.getMdn());
                        break;
                    }

                }
            }
            if (!groupInvaildMdnList.isEmpty()) {
                knLogger.debug(methodName, "MCX not MCPTT complance :- ");

                if (isMcpttFail) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_NOT_MCPTT_COMLIANCE,
                            "Groups not MCPTT complance.", getEntityId(), getOperationType(), getRuleId(),
                            groupInvaildMdnList.toString(), "");
                } else if (isCriFail) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_MCX_CRI_MCPTT_FLAG_DISABLED,
                            "Groups not CRI complance.", getEntityId(), getOperationType(), getRuleId(),
                            groupInvaildMdnList.toString(), "");
                } else if (isLocFlagFail) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_MCX_CRI_LOCFLAG_DISABLED,
                            "Groups not CRI Loc Flag disabled.", getEntityId(), getOperationType(), getRuleId(),
                            groupInvaildMdnList.toString(), "");
                }

            }
        }
    }
}
	   
