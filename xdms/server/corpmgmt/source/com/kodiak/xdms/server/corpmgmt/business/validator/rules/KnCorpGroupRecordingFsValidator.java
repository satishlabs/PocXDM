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
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscrFeatureInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupPropertiesDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnSubscrFeatureInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupPropertyInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpRecordingFsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.List;

public class KnCorpGroupRecordingFsValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpGroupRecordingFsValidator.class);
    private static final int ENABLED = 1;

    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug("Inside the recordingFs validator code");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "Bean is instance of KnCorpGroupInfoPersistDTO");
            KnCorpGroupInfoPersistDTO groupPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            String recordingFsBitSetValue = groupPersistDTO.getRecordingFs();
            boolean pttRecordingFlag = groupPersistDTO.isPttRecordingFlag();
            boolean dataRecordingFlag = groupPersistDTO.isDataRecordingFlag();
            boolean videoRecordingFlag = groupPersistDTO.isVideoRecordingFlag();

            knLogger.debug(methodName, "recordingFsBitSetValue : ", recordingFsBitSetValue);
            knLogger.debug(methodName, "pttRecordingFlag: ", pttRecordingFlag, " dataRecordingFlag: ", dataRecordingFlag,
                    " videoRecordingFlag: ", videoRecordingFlag);
            if (null != recordingFsBitSetValue) {
                if (recordingFsBitSetValue.charAt(2) == '1' && !pttRecordingFlag) {
                    knLogger.error(methodName, "PTT_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                            "PTT_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
                if (recordingFsBitSetValue.charAt(1) == '1' && !dataRecordingFlag) {
                    knLogger.error(methodName, "DATA_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                            "DATA_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
                if (recordingFsBitSetValue.charAt(0) == '1' && !videoRecordingFlag) {
                    knLogger.error(methodName, "VIDEO_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                            "VIDEO_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
            }
        } else if (persistDTO instanceof KnContactDetailsPersistDTO) {
            KnContactDetailsPersistDTO contactDetailsPersistDTO = (KnContactDetailsPersistDTO) persistDTO;
            boolean pttRecordingFlag = contactDetailsPersistDTO.isPttRecordingFlag();
            boolean dataRecordingFlag = contactDetailsPersistDTO.isDataRecordingFlag();
            boolean videoRecordingFlag = contactDetailsPersistDTO.isVideoRecordingFlag();
            knLogger.debug(methodName, "pttRecordingFlag: ", pttRecordingFlag, " dataRecordingFlag: ", dataRecordingFlag,
                    " videoRecordingFlag: ", videoRecordingFlag);
            KnIPSubscrFeatureInfoDTO ipSubscrFeatureInfoDTO = (KnIPSubscrFeatureInfoDTO) contactDetailsPersistDTO.getInputDTO();
            List<KnSubscrFeatureInfoDTO> resSubscrList = ipSubscrFeatureInfoDTO.getSubscrFeatureInfoDTOList();

            Integer subscrDtoPttRecording;
            Integer subscrDtoDataRecording;
            Integer subscrDtoVideoRecording;
            for (KnSubscrFeatureInfoDTO subscrDto : resSubscrList) {
                subscrDtoPttRecording = subscrDto.getPttRecording();
                subscrDtoDataRecording = subscrDto.getDataRecording();
                subscrDtoVideoRecording = subscrDto.getVideoRecording();
                knLogger.debug(methodName, "subscrDto - pttRecordingFlag: ", subscrDtoPttRecording,
                        " dataRecordingFlag: ", subscrDtoDataRecording, " videoRecordingFlag: ", subscrDtoVideoRecording);

                if (subscrDtoPttRecording != null && subscrDtoPttRecording == ENABLED && !pttRecordingFlag) {
                    knLogger.error(methodName, "PTT_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                            "PTT_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
                if (subscrDtoDataRecording != null && subscrDtoDataRecording == ENABLED && !dataRecordingFlag) {
                    knLogger.error(methodName, "DATA_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                            "DATA_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
                if (subscrDtoVideoRecording != null && subscrDtoVideoRecording == ENABLED && !videoRecordingFlag) {
                    knLogger.error(methodName, "VIDEO_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                            "VIDEO_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
            }
        } else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            knLogger.debug(methodName, "Bean is instance of KnCorpBCGrpPersistDTO");
            KnCorpBCGrpPersistDTO bcGroupPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            String recordingFsBitSetValue = bcGroupPersistDTO.getRecordingFs();
            boolean pttRecordingFlag = bcGroupPersistDTO.isPttRecordingFlag();
            boolean dataRecordingFlag = bcGroupPersistDTO.isDataRecordingFlag();
            boolean videoRecordingFlag = bcGroupPersistDTO.isVideoRecordingFlag();

            knLogger.debug(methodName, "recordingFsBitSetValue : ", recordingFsBitSetValue);
            knLogger.debug(methodName, "pttRecordingFlag: ", pttRecordingFlag, " dataRecordingFlag: ", dataRecordingFlag,
                    " videoRecordingFlag: ", videoRecordingFlag);
            if (null != recordingFsBitSetValue) {
                if (recordingFsBitSetValue.charAt(2) == '1' && !pttRecordingFlag) {
                    knLogger.error(methodName, "PTT_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                            "PTT_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
                if (recordingFsBitSetValue.charAt(1) == '1' && !dataRecordingFlag) {
                    knLogger.error(methodName, "DATA_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                            "DATA_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
                if (recordingFsBitSetValue.charAt(0) == '1' && !videoRecordingFlag) {
                    knLogger.error(methodName, "VIDEO_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                            "VIDEO_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
            }
        }else if (persistDTO instanceof KnCorpRecordingFsPersistDTO){
            knLogger.debug(methodName, "Bean is instance of KnCorpRecordingFsPersistDTO");
            KnCorpRecordingFsPersistDTO recordingFsPersistDTO = (KnCorpRecordingFsPersistDTO) persistDTO;
            String recordingFsBitSetValue = recordingFsPersistDTO.getReqRecordingFs();
            boolean pttRecordingFlag = recordingFsPersistDTO.getSysPttRecordingFlag();
            boolean dataRecordingFlag = recordingFsPersistDTO.getSysDataRecordingFlag();
            boolean videoRecordingFlag = recordingFsPersistDTO.getSysVideoRecordingFlag();

            knLogger.debug(methodName, "recordingFsBitSetValue : ", recordingFsBitSetValue);
            knLogger.debug(methodName, "pttRecordingFlag: ", pttRecordingFlag, " dataRecordingFlag: ", dataRecordingFlag,
                    " videoRecordingFlag: ", videoRecordingFlag);
            if (null != recordingFsBitSetValue) {
                if (recordingFsBitSetValue.charAt(2) == '1' && !pttRecordingFlag) {
                    knLogger.error(methodName, "PTT_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                            "PTT_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
                if (recordingFsBitSetValue.charAt(1) == '1' && !dataRecordingFlag) {
                    knLogger.error(methodName, "DATA_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                            "DATA_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
                if (recordingFsBitSetValue.charAt(0) == '1' && !videoRecordingFlag) {
                    knLogger.error(methodName, "VIDEO_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                            "VIDEO_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                }
            }
        } else if (persistDTO instanceof KnCorpBulkGroupPropertyInfoPersistDTO) {
            knLogger.debug(methodName, "Bean is instance of KnCorpBCGrpPersistDTO");
            KnCorpBulkGroupPropertyInfoPersistDTO groupPersistDTO = (KnCorpBulkGroupPropertyInfoPersistDTO) persistDTO;
            List<KnCorpGroupPropertiesDTO> corproupPersistDTO = groupPersistDTO.getGroupPropertyInfoDTOS();
            List<Integer> groupIdDBList = groupPersistDTO.getDbGroupIdList();
            knLogger.debug(methodName, "groupIdDBList : ", groupIdDBList);
            if (groupIdDBList.isEmpty()) {
                knLogger.error(methodName, "Groups are not exist for the corporation :  ", Boolean.FALSE);
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                        "Groups are not exist for the corporation ", getEntityId(), getOperationType(), getRuleId(), "", "");
            }

            for (KnCorpGroupPropertiesDTO bcGroupPersistDTO : corproupPersistDTO) {
                String recordingFsBitSetValue = bcGroupPersistDTO.getRecordingFS();
                String pttRecordingFlag = bcGroupPersistDTO.getPttRecording();
                String dataRecordingFlag = bcGroupPersistDTO.getDataRecording();
                String videoRecordingFlag = bcGroupPersistDTO.getVideoRecording();

                knLogger.debug(methodName, "recordingFsBitSetValue : ", recordingFsBitSetValue);
                knLogger.debug(methodName, "pttRecordingFlag: ", pttRecordingFlag, " dataRecordingFlag: ", dataRecordingFlag,
                        " videoRecordingFlag: ", videoRecordingFlag, "for groupId", bcGroupPersistDTO.getGroupId());

                if (!groupIdDBList.contains(Integer.valueOf(bcGroupPersistDTO.getGroupId()))) {
                    knLogger.error(methodName, "Group is not exist in the corporation :  ", Boolean.FALSE);
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_NOT_EXIST_CORP,
                            "Group is not exist in the corporation ", getEntityId(), getOperationType(), getRuleId(), "", "");
                }

                if (null != recordingFsBitSetValue) {
                    if (recordingFsBitSetValue.charAt(2) == '1' && !pttRecordingFlag.equals("1")) {
                        knLogger.error(methodName, "PTT_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE, "for groupId", bcGroupPersistDTO.getGroupId());
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                                "PTT_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                    }
                    if (recordingFsBitSetValue.charAt(1) == '1' && !dataRecordingFlag.equals("1")) {
                        knLogger.error(methodName, "DATA_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE, "for groupId", bcGroupPersistDTO.getGroupId());
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                                "DATA_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                    }
                    if (recordingFsBitSetValue.charAt(0) == '1' && !videoRecordingFlag.equals("1")) {
                        knLogger.error(methodName, "VIDEO_RECORDING_FLAG Feature is disabled at Server and the value is :  ", Boolean.FALSE, "for groupId", bcGroupPersistDTO.getGroupId());
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.SYSTEM_LEVEL_RECORDING_FLAG_DISABLED,
                                "VIDEO_RECORDING_FLAG Feature is disabled at system level", getEntityId(), getOperationType(), getRuleId(), "", "");
                    }
                }
            }
        }
        knLogger.info(methodName, " KnCorpGroupRecordingFsValidator validation is success.");
    }
}

