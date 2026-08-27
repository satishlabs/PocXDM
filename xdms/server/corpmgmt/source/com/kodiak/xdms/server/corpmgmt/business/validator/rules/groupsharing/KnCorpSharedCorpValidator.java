/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules.groupsharing;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSharedCorpInfo;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupProfilePersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class KnCorpSharedCorpValidator extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpSharedCorpValidator.class);

    /**
     * validate method. all the validators should implement this method
     *
     * @throws KnValidationException
     * @throws KnBOException
     */
    @Override
    public void validate() throws KnValidationException, KnBOException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY.");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupProfilePersistDTO) {
            KnCorpGroupProfilePersistDTO grpGersistDTO = (KnCorpGroupProfilePersistDTO) persistDTO;
            List<KnCorpSharedCorpInfo> sharedCorpInfoList = grpGersistDTO.getCorpSharedCorpInfoList();
            Set<String> extCorpIds = new HashSet<>();
            Set<Integer> intCorpIds = new HashSet<>();
            if(sharedCorpInfoList!=null && !sharedCorpInfoList.isEmpty()){
                Map<String,Integer> extIntCorpIdMap = grpGersistDTO.getExtIntCorpIdMap();
                if(extIntCorpIdMap!=null && !extIntCorpIdMap.isEmpty()){
                    knLogger.debug(methodName,"extIntCorpIdMap - ",extIntCorpIdMap," sharedCorpInfoList - ",sharedCorpInfoList);
                    extCorpIds.addAll(sharedCorpInfoList.stream().map(KnCorpSharedCorpInfo::getExtCorpId).collect(Collectors.toSet()));
                    intCorpIds.addAll(sharedCorpInfoList.stream().map(KnCorpSharedCorpInfo::getCorpId).collect(Collectors.toSet()));
                    knLogger.debug(methodName,"extCorpIds - ",extCorpIds," intCorpIds -",intCorpIds);
                    if(extCorpIds!=null && !extCorpIds.isEmpty()){
                        knLogger.debug(methodName,"removing all  extIntCorpId Map key set from extCorpIds",extIntCorpIdMap.keySet(),extCorpIds.toString());
                        extIntCorpIdMap.entrySet().forEach(e->extCorpIds.remove(e.getKey()));
                    }

                    if(intCorpIds!=null && !intCorpIds.isEmpty()){
                        knLogger.debug(methodName,"removing all  extIntCorpId Map values from intCorpIds",extIntCorpIdMap.values(),intCorpIds.toString());
                        extIntCorpIdMap.entrySet().forEach(e->intCorpIds.remove(e.getValue()));
                    }

                    knLogger.debug(methodName,"extIntCorpIdMap - ",extIntCorpIdMap,"extCorpIds - ",extCorpIds," intCorpIds -",intCorpIds);
                }

                if(extCorpIds!=null && !extCorpIds.isEmpty()){
                    knLogger.error(methodName, "shared external CorpId's not present in system ");
                    throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.PROFILE_DOES_NOT_EXIST,
                            "shared CorpId's not present in system", getEntityId(), getOperationType(), getRuleId(), extCorpIds.toString(), "");
                }else if(intCorpIds!=null && !intCorpIds.isEmpty()){
                    knLogger.error(methodName, "shared internal CorpId's not present in system ");
                    throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.PROFILE_DOES_NOT_EXIST,
                            "shared CorpId's not present in system", getEntityId(), getOperationType(), getRuleId(), intCorpIds.toString(), "");
                }
            }
        }
       else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
           knLogger.debug(methodName," Bean is Instance of KnCorpGroupInfoPersistDTO");
            KnCorpGroupInfoPersistDTO grpGersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            List<KnCorpSharedCorpInfo> sharedCorpInfoList = grpGersistDTO.getCorpSharedCorpInfoList();
            Set<String> extCorpIds = new HashSet<>();
            Set<Integer> intCorpIds = new HashSet<>();
            if(sharedCorpInfoList!=null && !sharedCorpInfoList.isEmpty()){
                Map<String,Integer> extIntCorpIdMap = grpGersistDTO.getExtIntCorpIdMap();
                if(extIntCorpIdMap!=null && !extIntCorpIdMap.isEmpty()){
                    knLogger.debug(methodName,"extIntCorpIdMap - ",extIntCorpIdMap," sharedCorpInfoList - ",sharedCorpInfoList);
                    extCorpIds.addAll(sharedCorpInfoList.stream().map(KnCorpSharedCorpInfo::getExtCorpId).collect(Collectors.toSet()));
                    intCorpIds.addAll(sharedCorpInfoList.stream().map(KnCorpSharedCorpInfo::getCorpId).collect(Collectors.toSet()));
                    knLogger.debug(methodName,"extCorpIds - ",extCorpIds," intCorpIds -",intCorpIds);
                    if(extCorpIds!=null && !extCorpIds.isEmpty()){
                        knLogger.debug(methodName,"removing all  extIntCorpId Map key set from extCorpIds",extIntCorpIdMap.keySet(),extCorpIds.toString());
                        extIntCorpIdMap.entrySet().forEach(e->extCorpIds.remove(e.getKey()));
                    }

                    if(intCorpIds!=null && !intCorpIds.isEmpty()){
                        knLogger.debug(methodName,"removing all  extIntCorpId Map values from intCorpIds",extIntCorpIdMap.values(),intCorpIds.toString());
                        extIntCorpIdMap.entrySet().forEach(e->intCorpIds.remove(e.getValue()));
                    }

                    knLogger.debug(methodName,"extIntCorpIdMap - ",extIntCorpIdMap,"extCorpIds - ",extCorpIds," intCorpIds -",intCorpIds);
                }

                if(extCorpIds!=null && !extCorpIds.isEmpty()){
                    knLogger.error(methodName, "shared external CorpId's not present in system ");
                    throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.PROFILE_DOES_NOT_EXIST,
                            "shared CorpId's not present in system", getEntityId(), getOperationType(), getRuleId(), extCorpIds.toString(), "");
                }else if(intCorpIds!=null && !intCorpIds.isEmpty()){
                    knLogger.error(methodName, "shared internal CorpId's not present in system ");
                    throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.PROFILE_DOES_NOT_EXIST,
                            "shared CorpId's not present in system", getEntityId(), getOperationType(), getRuleId(), intCorpIds.toString(), "");
                }
            }
        }
        else if (persistDTO instanceof KnCorpBCGrpPersistDTO) {
            knLogger.debug(methodName," Bean is Instance of KnCorpBCGrpPersistDTO");
            KnCorpBCGrpPersistDTO grpGersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
            List<KnCorpSharedCorpInfo> sharedCorpInfoList = grpGersistDTO.getCorpSharedCorpInfoList();
            Set<String> extCorpIds = new HashSet<>();
            Set<Integer> intCorpIds = new HashSet<>();
            if(sharedCorpInfoList!=null && !sharedCorpInfoList.isEmpty()){
                Map<String,Integer> extIntCorpIdMap = grpGersistDTO.getExtIntCorpIdMap();
                if(extIntCorpIdMap!=null && !extIntCorpIdMap.isEmpty()){
                    knLogger.debug(methodName,"extIntCorpIdMap - ",extIntCorpIdMap," sharedCorpInfoList - ",sharedCorpInfoList);
                    extCorpIds.addAll(sharedCorpInfoList.stream().map(KnCorpSharedCorpInfo::getExtCorpId).collect(Collectors.toSet()));
                    intCorpIds.addAll(sharedCorpInfoList.stream().map(KnCorpSharedCorpInfo::getCorpId).collect(Collectors.toSet()));
                    knLogger.debug(methodName,"extCorpIds - ",extCorpIds," intCorpIds -",intCorpIds);
                    if(extCorpIds!=null && !extCorpIds.isEmpty()){
                        knLogger.debug(methodName,"removing all  extIntCorpId Map key set from extCorpIds",extIntCorpIdMap.keySet(),extCorpIds.toString());
                        extIntCorpIdMap.entrySet().forEach(e->extCorpIds.remove(e.getKey()));
                    }

                    if(intCorpIds!=null && !intCorpIds.isEmpty()){
                        knLogger.debug(methodName,"removing all  extIntCorpId Map values from intCorpIds",extIntCorpIdMap.values(),intCorpIds.toString());
                        extIntCorpIdMap.entrySet().forEach(e->intCorpIds.remove(e.getValue()));
                    }

                    knLogger.debug(methodName,"extIntCorpIdMap - ",extIntCorpIdMap,"extCorpIds - ",extCorpIds," intCorpIds -",intCorpIds);
                }

                if(extCorpIds!=null && !extCorpIds.isEmpty()){
                    knLogger.error(methodName, "shared external CorpId's not present in system ");
                    throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.PROFILE_DOES_NOT_EXIST,
                            "shared CorpId's not present in system", getEntityId(), getOperationType(), getRuleId(), extCorpIds.toString(), "");
                }else if(intCorpIds!=null && !intCorpIds.isEmpty()){
                    knLogger.error(methodName, "shared internal CorpId's not present in system ");
                    throw new KnCorpBOValidationException(KnErrorCodes.BOEntity.PROFILE_DOES_NOT_EXIST,
                            "shared CorpId's not present in system", getEntityId(), getOperationType(), getRuleId(), intCorpIds.toString(), "");
                }
            }
        }

    }

}
