/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPAuthUserPermissionInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPEmergencyInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnTargetMdnPermBitInfo;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMcpttFeaturePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

/**
 * ************************************************************************
 * <p>
 * File name:  KnMcpttFlagNBitValidationRule.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Dec 07, 2017                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnMcpttFlagNBitValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnMcpttFlagNBitValidationRule.class);

    @Override
    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
        if (persistDTO instanceof KnCorpMcpttFeaturePersistDTO) {
            KnCorpMcpttFeaturePersistDTO mcpttFeaturePersistDTO = (KnCorpMcpttFeaturePersistDTO) persistDTO;
            IInputDTO inputDTO = persistDTO.getInputDTO();
            if (inputDTO instanceof KnIPAuthUserPermissionInfoDTO) {
                KnIPAuthUserPermissionInfoDTO actDTO = (KnIPAuthUserPermissionInfoDTO) inputDTO;
                String authMdn = mcpttFeaturePersistDTO.getAuthorizedMdn();
                BitSet authMdnBitSet = featureSetUtil.convertHexStringToBitSet(mcpttFeaturePersistDTO.getSubsFS2());
                Map<String, KnCorpSubscriberDTO> targetInfo = mcpttFeaturePersistDTO.getTargetInfo();
                Collection<Long> targetBitInfo = null;
                BitSet finalBitSet = null;
                if (targetInfo != null && !targetInfo.isEmpty()) {
                    targetBitInfo = mcpttFeaturePersistDTO.getTargetBits();
                    knLogger.debug(methodName, "targetBitInfo - ", targetBitInfo);
                    finalBitSet = featureSetUtil.convertLongToBitSetOring(targetBitInfo);
                    knLogger.debug(methodName, "finalBitSet - ", finalBitSet);
                    if (finalBitSet.get(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value())) {
                        if (!mcpttFeaturePersistDTO.isAmbientListening()) {
                           /* if (!authMdnBitSet.get(KnConstants.FEATURE_SET_ALLOWED.AMBIENTLISTENING.value())) {
                                knLogger.error(methodName, "Ambient bit is disabled in featureBit - ", authMdn);
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.AMBIENT_LISTENING_DISABLED_FOR_AUTHORIZED_MDN,
                                        "Ambient bit is disabled in featureBit--", getEntityId(),
                                        getOperationType(), getRuleId(), Arrays.asList(authMdn).toString(), "");
                            }
                        } else {*/
                            knLogger.error(methodName, "System Level Ambient Listening is disabled - ", authMdn);
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_AMBIENT_LISTENING_FEATURE_DISABLED,
                                    "System Level Ambient Listening is disabled--", getEntityId(),
                                    getOperationType(), getRuleId(), Arrays.asList("0").toString(), "");
                        }
                    }
                    if (finalBitSet.get(KnConstants.MCPTT_PERMISSION_BIT.DISCRETELISTENING.value())) {
                        if (!mcpttFeaturePersistDTO.isDiscreteListening()) {
                           /* if (!authMdnBitSet.get(KnConstants.FEATURE_SET_ALLOWED.DISCRETELISTENING.value())) {
                                knLogger.error(methodName, "Discrete bit is disabled in featureBit - ", authMdn);
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.DISCRETE_LISTENING_DISABLED_FOR_AUTHORIZED_MDN,
                                        "Discrete bit is disabled in featureBit--", getEntityId(),
                                        getOperationType(), getRuleId(), Arrays.asList(authMdn).toString(), "");
                            }

                        } else {*/
                            knLogger.error(methodName, "System Level Discrete Listening is disabled - ", authMdn);
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_DISCRETE_LISTENING_FEATURE_DISABLED,
                                    "System Level Discrete Listening is disabled--", getEntityId(),
                                    getOperationType(), getRuleId(), Arrays.asList("0").toString(), "");
                        }
                    }
                    if (finalBitSet.get(KnConstants.MCPTT_PERMISSION_BIT.USERCHECK.value())) {
                        if (!mcpttFeaturePersistDTO.isUserCheck()) {
                           /* if (!authMdnBitSet.get(KnConstants.FEATURE_SET_ALLOWED.USERCHECK.value())) {
                                knLogger.error(methodName, "User Check bit is disabled in featureBit - ", authMdn);
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_CHECK_DISABLED_FOR_AUTHORIZED_MDN,
                                        "User Check bit is disabled in featureBit-- ", getEntityId(),
                                        getOperationType(), getRuleId(), Arrays.asList(authMdn).toString(), "");
                            }
                        } else {*/
                            knLogger.error(methodName, "System Level User Check is disabled - ", authMdn);
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_USER_CHECK_FEATURE_DISABLED,
                                    "System Level User Check is disabled--", getEntityId(),
                                    getOperationType(), getRuleId(), Arrays.asList("0").toString(), "");
                        }
                    }
                    if (finalBitSet.get(KnConstants.MCPTT_PERMISSION_BIT.USERENABLE.value())) {
                        if (!mcpttFeaturePersistDTO.isUserSvcCtrl()) {
                           /* if (!authMdnBitSet.get(KnConstants.FEATURE_SET_ALLOWED.USERENABLE.value())) {
                                knLogger.error(methodName, "User enabled/disable bit is disabled in featureBit - ", authMdn);
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_DISABLED_FOR_AUTHORIZED_MDN,
                                        "User enabled/disable bit is disabled in featureBit-- ", getEntityId(),
                                        getOperationType(), getRuleId(), Arrays.asList(authMdn).toString(), "");
                            }
                        } else {*/
                            knLogger.error(methodName, "System Level User Service Control is disabled - ", authMdn);
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_USER_SERVICE_FEATURE_DISABLED,
                                    "System Level User Service Control is disabled--", getEntityId(),
                                    getOperationType(), getRuleId(), Arrays.asList("0").toString(), "");
                        }
                    }
                    if (finalBitSet.get(KnConstants.MCPTT_PERMISSION_BIT.REMOTEEMERGENCYPERMISSION.value())) {
                        if (!mcpttFeaturePersistDTO.isEmergFeature()) {
                           /* if (!authMdnBitSet.get(KnConstants.FEATURE_SET_ALLOWED.REMOTEEMERGENCYPERMISSION.value())) {
                                knLogger.error(methodName, "remote emergency bit is disabled in featureBit - ", authMdn);
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.REMOTE_EMERGENCY_DISABLED_FOR_AUTHORIZED_MDN,
                                        "remote emergency bit is disabled in featureBit-- ", getEntityId(),
                                        getOperationType(), getRuleId(), Arrays.asList(authMdn).toString(), "");
                            }
                        } else {*/
                            knLogger.error(methodName, "System Level Emergency Control Feature is disabled - ", authMdn);
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_EMERGENCY_FEATURE_DISABLED,
                                    "System Level Emergency Control Feature is disabled--", getEntityId(),
                                    getOperationType(), getRuleId(), Arrays.asList("0").toString(), "");
                        }
                    }
                    if (finalBitSet.get(KnConstants.MCPTT_PERMISSION_BIT.MCVIDEOUNCONFIRMEDPULL.value())) {
                        if (!mcpttFeaturePersistDTO.isMcVideoUnCfrmPullFeature()) {
                            /*if (!authMdnBitSet.get(KnConstants.FEATURE_SET_ALLOWED.MCVIDEORX.value())
                                    && authMdnBitSet.get(KnConstants.FEATURE_SET_ALLOWED.MCVIDEOCONFIRMEDPULL.value())) {
                                knLogger.error(methodName, "MC Video RX bit and MCVideo Unconfirmed Pull are disabled in featureBit - ", authMdn);
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MC_VIDEO_UNCONFIRMED_PULL_RX_DISABLED_FOR_AUTHORIZED_MDN,
                                        "MC Video RX bit and MCVideo Unconfirmed Pull are disabled in featureBit-- ", getEntityId(),
                                        getOperationType(), getRuleId(), Arrays.asList(authMdn).toString(), "");
                            }
                            if (!authMdnBitSet.get(KnConstants.FEATURE_SET_ALLOWED.MCVIDEORX.value())) {
                                knLogger.error(methodName, "MC Video RX bit is disabled in featureBit - ", authMdn);
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MC_VIDEO_RX_DISABLED_FOR_AUTHORIZED_MDN,
                                        "MC Video RX bit is disabled in featureBit-- ", getEntityId(),
                                        getOperationType(), getRuleId(), Arrays.asList(authMdn).toString(), "");
                            }
                            if (!authMdnBitSet.get(KnConstants.FEATURE_SET_ALLOWED.MCVIDEOCONFIRMEDPULL.value())) {
                                knLogger.error(methodName, "MCVideo Unconfirmed Pull is disabled in featureBit - ", authMdn);
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.MC_VIDEO_UNCONFIRMED_PULL_DISABLED_FOR_AUTHORIZED_MDN,
                                        "MCVideo Unconfirmed Pull is disabled in featureBit-- ", getEntityId(),
                                        getOperationType(), getRuleId(), Arrays.asList(authMdn).toString(), "");
                            }*/
                        /*} else {*/
                            knLogger.error(methodName, "System Level MC Video Unconfirmed Pull Feature is disabled - ", authMdn);
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_MC_VIDEO_UNCONFIRMED_PULL_FEATURE_DISABLED,
                                    "System Level MC Video Unconfirmed Pull Feature is disabled--", getEntityId(),
                                    getOperationType(), getRuleId(), Arrays.asList("0").toString(), "");
                        }
                    }
                    /*Collection<KnTargetMdnPermBitInfo> inputTargetDto = actDTO.getTargetMdnPermissionBitInfoList();
                    Collection<String> ambientDisabledTargets = new ArrayList<>();
                    Collection<String> userCheckDisabledTargets = new ArrayList<>();
                    Collection<String> remoteEmergPermissionTargets = new ArrayList<>();
                    Collection<String> mcVideoUnCfrmPullTargets = new ArrayList<>();
                    inputTargetDto.forEach(inputTarDto -> {
                        KnCorpSubscriberDTO targetDetails = targetInfo.get(inputTarDto.getMdn());
                        BitSet targetBitSet = featureSetUtil.convertHexStringToBitSet(targetDetails.getSubscriberFs2());
                        if (inputTarDto.getAmbientListening() != 0
                                && !targetBitSet.get(KnConstants.FEATURE_SET_ALLOWED.AMBIENTLISTENING.value())) {
                            ambientDisabledTargets.add(inputTarDto.getMdn());
                        }
                        if (inputTarDto.getUserCheck() != 0
                                && !targetBitSet.get(KnConstants.FEATURE_SET_ALLOWED.USERCHECK.value())) {
                            userCheckDisabledTargets.add(inputTarDto.getMdn());
                        }
                        if (inputTarDto.getEmergPermission() != 0
                                && !targetBitSet.get(KnConstants.FEATURE_SET_ALLOWED.REMOTEEMERGENCYPERMISSION.value())) {
                            remoteEmergPermissionTargets.add(inputTarDto.getMdn());
                        }
                        if (inputTarDto.getMcVideoUnConfirmedPull() != 0
                                && !targetBitSet.get(KnConstants.FEATURE_SET_ALLOWED.MCVIDEOTX.value())) {
                            mcVideoUnCfrmPullTargets.add(inputTarDto.getMdn());
                        }
                    });
                    knLogger.debug(methodName, "ambientDisabledTargets - ", ambientDisabledTargets);
                    knLogger.debug(methodName, "userCheckDisabledTargets - ", userCheckDisabledTargets);
                    knLogger.debug(methodName, "remoteEmergPermissionTargets - ", remoteEmergPermissionTargets);
                    knLogger.debug(methodName, "mcVideoUnCfrmPullTargets - ", mcVideoUnCfrmPullTargets);
                    if (!ambientDisabledTargets.isEmpty()) {
                        knLogger.error(methodName, "Ambient bit is disabled in featureBit - ", ambientDisabledTargets);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.AMBIENT_LISTENING_FOR_TARGET_MDN,
                                "Ambient bit is disabled in featureBit-- ", getEntityId(),
                                getOperationType(), getRuleId(), Arrays.asList(ambientDisabledTargets).toString(), "");
                    }
                    if (!userCheckDisabledTargets.isEmpty()) {
                        knLogger.error(methodName, "User Check bit is disabled in featureBit - ", userCheckDisabledTargets);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.USER_CHECK_DISABLED_FOR_TARGET_MDN,
                                "User Check bit is disabled in featureBit-- ", getEntityId(),
                                getOperationType(), getRuleId(), Arrays.asList(userCheckDisabledTargets).toString(), "");
                    }
                    if (!remoteEmergPermissionTargets.isEmpty()) {
                        knLogger.error(methodName, "remote emergency permission bit is disabled in featureBit - ", remoteEmergPermissionTargets);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.REMOTE_EMERGENCY_DISABLED_FOR_TARGET_MDN,
                                "remote emergency permission bit is disabled in featureBit-- ", getEntityId(),
                                getOperationType(), getRuleId(), Arrays.asList(remoteEmergPermissionTargets).toString(), "");
                    }
                    if (!mcVideoUnCfrmPullTargets.isEmpty()) {
                        knLogger.error(methodName, "Mc Video Tx permission bit is disabled in featureBit - ", mcVideoUnCfrmPullTargets);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.MC_VIDEO_TX_DISABLED_FOR_TARGET_MDN,
                                "Mc Video Tx permission bit is disabled in featureBit-- ", getEntityId(),
                                getOperationType(), getRuleId(), Arrays.asList(remoteEmergPermissionTargets).toString(), "");
                    }*/
                }
            } else if (inputDTO instanceof KnIPEmergencyInfoDTO) {
                String mdn = mcpttFeaturePersistDTO.getMdn();
                BitSet mdnBitSet = featureSetUtil.convertHexStringToBitSet(mcpttFeaturePersistDTO.getSubsFS2());
                if (mcpttFeaturePersistDTO.isEmergFeature()) {
                    if (!mdnBitSet.get(KnConstants.FEATURE_SET_ALLOWED.REMOTEEMERGENCYPERMISSION.value())) {
                        knLogger.error(methodName, "User remote emergency permission bit is disabled in featureBit - ", mdn);
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.EMERGENCY_FEATURE_BIT_DISABLED,
                                "User remote emergency permission bit is disabled in featureBit-- ", getEntityId(),
                                getOperationType(), getRuleId(), Arrays.asList(mdn).toString(), "");
                    }
                } else {
                    knLogger.error(methodName, "System Level Emergency Control Feature is disabled - ", mdn);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_EMERGENCY_FEATURE_DISABLED,
                            "System Level Emergency Control Feature is disabled--", getEntityId(),
                            getOperationType(), getRuleId(), Arrays.asList("0").toString(), "");
                }
            }
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        knLogger.debug(methodName, "EXIT: Validation Completed Successfully for MCPTT bits and system level flag");
    }
}
