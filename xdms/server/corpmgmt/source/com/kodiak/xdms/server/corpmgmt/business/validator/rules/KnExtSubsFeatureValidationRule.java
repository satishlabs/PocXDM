/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.common.KnExtProfileDetails;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpTGSPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * *****************************************************************************
 * File name:   KnExtSubsFeatureValidationRule.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * ChandraShekar H S       10/4/14      7.8.1
 * <p/>
 * <p/>
 * <p>
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
 * *******************************************************************************
 */
public class KnExtSubsFeatureValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnExtSubsFeatureValidationRule.class);
    private String methodName = "validate(IPersistenceDTO persistDTO)";
    private int CONTACT_MANEGEMENT_FEATURE_BIT = 9;
    private int STANDARD_GROUP_MANAGEMENT_FEATURE_BIT = 5;
    private int SUPERVISORY_FEATURE_BIT = 15;
    private int DISPATCH_GROUP_MANAGEMENT_FEATURE_BIT = 14;
    private int TGS_FEATURE_BIT = 22;
    private int SUBLIST_MANAGEMENT_FEATURE_BIT = 23;
    private int LOC_WATCHER_FEATURE_BIT = 26;

    @Override
    public void validate() throws KnValidationException {
        try {
            IPersistenceDTO persistDTO = getDTO();

            knLogger.debug(methodName, "ENTRY : Validating External Subscriber Feature Validation Rule");

            if (persistDTO instanceof KnContactDetailsPersistDTO) {
                KnContactDetailsPersistDTO contactDTO = (KnContactDetailsPersistDTO) persistDTO;
                Map<Integer, KnExtProfileDetails> configuredProfileMap = contactDTO.getConfiguredProfileMap();
                KnExtProfileDetails extProfile = new KnExtProfileDetails();
                if (contactDTO.getOperationType().equals(KnOperationTypes.MODIFY_SUBS_CONTACT_LIST)) {
                    Set<Map.Entry<Integer, KnExtProfileDetails>> configured = configuredProfileMap.entrySet();
                    Iterator<Map.Entry<Integer, KnExtProfileDetails>> contactIterator = configured.iterator();
                    while (contactIterator.hasNext()) {
                        Map.Entry<Integer, KnExtProfileDetails> pairs = contactIterator.next();
                        extProfile = pairs.getValue();
                        knLogger.debug(methodName, "Feature set Map", extProfile);
                        if (!extProfile.getFeatureSetMap().get(CONTACT_MANEGEMENT_FEATURE_BIT)) {
                            knLogger.error(methodName, "Validation failure", "profile", extProfile, "Add ext contact  Subscribers feature bit is disabled");
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_EXT_CONTACT_BIT_DISABLED,
                                    "Add ext contact  Subscribers feature bit is disabled", getEntityId(),
                                    getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MAX_EXT_PROFILE_FEATURESET, "");
                        }
                    }
                }
            } else if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
                KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
                KnIPCorpGroupInfoDTO groupInputDTO = (KnIPCorpGroupInfoDTO) persistDTO.getInputDTO();
                Map<Integer, KnExtProfileDetails> configuredProfileMap = corpGroupInfoPersistDTO.getConfiguredProfileMap();
                Map<Integer, KnExtProfileDetails> supervisorProfileMap = corpGroupInfoPersistDTO.getSupervisorProfileMap();
                Map<Integer, KnExtProfileDetails> locWatcherProfileMap = corpGroupInfoPersistDTO.getLocWatcherMdnProfileMap();
                KnExtProfileDetails extProfile = new KnExtProfileDetails();
                if (groupInputDTO.getOperationType().equals(KnOperationTypes.CREATE_GROUP) |
                        groupInputDTO.getOperationType().equals(KnOperationTypes.MODIFY_GROUP)) {
                    Set<Map.Entry<Integer, KnExtProfileDetails>> configured = configuredProfileMap.entrySet();
                    Iterator groupIterator = configured.iterator();
                    while (groupIterator.hasNext()) {
                        Map.Entry pairs = (Map.Entry) groupIterator.next();
                        extProfile = (KnExtProfileDetails) pairs.getValue();
                        knLogger.debug(methodName, "Feature set Map", extProfile);
                        if (!extProfile.getFeatureSetMap().get(STANDARD_GROUP_MANAGEMENT_FEATURE_BIT)) {
                            knLogger.error(methodName, "Validation failure", "profile", extProfile, "PreArranged Corp Grp feature bit is disabled");
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_EXT_CONTACT_PRE_ARRANGD_GROUP_BIT_DISABLED,
                                    "PreArranged Corp Grp feature bit is disabled", getEntityId(),
                                    getOperationType(), getRuleId(), groupInputDTO.getExternalContacts().toString(), "");
                        }
                    }
                    if (supervisorProfileMap != null) {
                        Set<Map.Entry<Integer, KnExtProfileDetails>> supervisor = supervisorProfileMap.entrySet();
                        Iterator supervisorIterator = supervisor.iterator();
                        while (supervisorIterator.hasNext()) {
                            Map.Entry pairs = (Map.Entry) supervisorIterator.next();
                            extProfile = (KnExtProfileDetails) pairs.getValue();
                            knLogger.debug(methodName, "Feature set Map", extProfile);
                            if (!extProfile.getFeatureSetMap().get(SUPERVISORY_FEATURE_BIT)) {
                                knLogger.error(methodName, "Validation failure", "profile", extProfile, "SuperVisor feature bit is disabled");
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_EXT_CONTACT_SUPERVISOR_BIT_DISABLED,
                                        "SuperVisor feature bit is disabled", getEntityId(),
                                        getOperationType(), getRuleId(),Arrays.asList(0).toString(), "");
                            }
                        }
                    }

                    if (locWatcherProfileMap != null) {
                        Set<Map.Entry<Integer, KnExtProfileDetails>> locWatcher = locWatcherProfileMap.entrySet();
                        Iterator locWatcherIterator = locWatcher.iterator();
                        while (locWatcherIterator.hasNext()) {
                            Map.Entry pairs = (Map.Entry) locWatcherIterator.next();
                            extProfile = (KnExtProfileDetails) pairs.getValue();
                            knLogger.debug(methodName, "Feature set Map", extProfile);
                            if (!extProfile.getFeatureSetMap().get(LOC_WATCHER_FEATURE_BIT)) {
                                knLogger.error(methodName, "Validation failure", "profile", extProfile, "locWatcher feature bit is disabled");
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_EXT_CONTACT_LOCWATCHER_BIT_DISABLED,
                                        "locWatcher feature bit is disabled", getEntityId(),
                                        getOperationType(), getRuleId(), groupInputDTO.getExternalContacts().toString(), "");
                            }
                        }
                    }
                }
                if (groupInputDTO.getGroupType() == KnConstants.DISPATCH_GROUP) {
                    if (groupInputDTO.getOperationType().equals(KnOperationTypes.CREATE_GROUP) |
                            groupInputDTO.getOperationType().equals(KnOperationTypes.MODIFY_GROUP)) {
                        Set<Map.Entry<Integer, KnExtProfileDetails>> configured = configuredProfileMap.entrySet();
                        Iterator groupIterator = configured.iterator();
                        while (groupIterator.hasNext()) {
                            Map.Entry pairs = (Map.Entry) groupIterator.next();
                            extProfile = (KnExtProfileDetails) pairs.getValue();
                            knLogger.debug(methodName, "Feature set Map", extProfile);
                            if (!extProfile.getFeatureSetMap().get(DISPATCH_GROUP_MANAGEMENT_FEATURE_BIT)) {
                                knLogger.error(methodName, "Validation failure", "profile", extProfile, "Dispatch Grp Member feature bit is disabled");
                                throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_EXT_CONTACT_DISP_MEMBER_BIT_DISABLED,
                                        "Dispatch Grp Member feature bit is disabled", getEntityId(),
                                        getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MAX_EXT_PROFILE_FEATURESET, "");
                            }
                        }
                        if (groupInputDTO.isContactPairing()) {
                            Iterator contactPairIterator = configured.iterator();
                            while (contactPairIterator.hasNext()) {
                                Map.Entry pairs = (Map.Entry) contactPairIterator.next();
                                extProfile = (KnExtProfileDetails) pairs.getValue();
                                knLogger.debug(methodName, "Feature set Map", extProfile);
                                if (!extProfile.getFeatureSetMap().get(9)) {
                                    knLogger.error(methodName, "Validation failure", "profile", extProfile, "Add ext contact  Subscribers feature bit is disabled");
                                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_EXT_CONTACT_BIT_DISABLED,
                                            "Add ext contact  Subscribers feature bit is disabled", getEntityId(),
                                            getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MAX_EXT_PROFILE_FEATURESET, "");
                                }
                            }
                        }
                    }
                }
            } else if (persistDTO instanceof KnCorpTGSPersistDTO) {
                KnCorpTGSPersistDTO tgsPersistDTO = (KnCorpTGSPersistDTO) persistDTO;
                Map<Integer, KnExtProfileDetails> configuredProfileMap = tgsPersistDTO.getConfiguredProfileMap();
                KnExtProfileDetails extProfile = new KnExtProfileDetails();
                if (tgsPersistDTO.getOperationType().equals(KnOperationTypes.MODIFY_TALK_GROUP_SELECT)) {
                    Map<Integer, Boolean> subsFeatureSetMap = extProfile.getFeatureSetMap();
                    Set<Map.Entry<Integer, KnExtProfileDetails>> configured = configuredProfileMap.entrySet();
                    Iterator tgsIterator = configured.iterator();
                    while (tgsIterator.hasNext()) {
                        Map.Entry pairs = (Map.Entry) tgsIterator.next();
                        extProfile = (KnExtProfileDetails) pairs.getValue();
                        knLogger.debug(methodName, "Feature set Map", extProfile);
                        if (!extProfile.getFeatureSetMap().get(TGS_FEATURE_BIT)) {
                            knLogger.error(methodName, "Validation failure", "profile", extProfile, "modify Subscribers TGS feature bit is disabled");
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_CONTACT_TGS_BIT_DISABLED,
                                    "modify Subscribers TGS feature bit is disabled", getEntityId(),
                                    getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MAX_EXT_PROFILE_FEATURESET, "");
                        }
                    }
                }
            } else if (persistDTO instanceof KnSublistDetailsPersistDTO) {
                knLogger.debug(methodName, "ENTRY : KnSublistDetailsPersistDTO");
                KnSublistDetailsPersistDTO contactDTO = (KnSublistDetailsPersistDTO) persistDTO;
                Map<Integer, KnExtProfileDetails> configuredProfileMap = contactDTO
                        .getConfiguredProfileMap();
                KnIPCorpSublistInfoDTO corpSublistInfoDTO = (KnIPCorpSublistInfoDTO) persistDTO.getInputDTO();
                KnExtProfileDetails extProfile = new KnExtProfileDetails();
                Set<Map.Entry<Integer, KnExtProfileDetails>> configured = configuredProfileMap.entrySet();
                Iterator sublistIterator = configured.iterator();
                if (corpSublistInfoDTO.isDistribution() || contactDTO.isSublistDistributed()) {
                    while (sublistIterator.hasNext()) {
                        Map.Entry pairs = (Map.Entry) sublistIterator.next();
                        extProfile = (KnExtProfileDetails) pairs.getValue();
                        if (!extProfile.getFeatureSetMap().get(9)) {
                            knLogger.error(methodName, "Validation failure", "profile", extProfile, "Add ext contact  Subscribers feature bit is disabled");
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_EXT_CONTACT_BIT_DISABLED,
                                    "Add ext contact  Subscribers feature bit is disabled", getEntityId(),
                                    getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MAX_EXT_PROFILE_FEATURESET, "");
                        }
                    }
                }
                sublistIterator=configured.iterator();

                while (sublistIterator.hasNext()){
                    Map.Entry pairs = (Map.Entry)sublistIterator.next();
                    extProfile=(KnExtProfileDetails)pairs.getValue();
                    knLogger.error(methodName, "extProfile", extProfile);
                    knLogger.error(methodName, "extProfile.getFeatureSetMap()", extProfile.getFeatureSetMap());
                    knLogger.error(methodName, "extProfile.getFeatureSetMap().get(23)", extProfile.getFeatureSetMap().get(23));
                    if(!extProfile.getFeatureSetMap().get(SUBLIST_MANAGEMENT_FEATURE_BIT)){
                        knLogger.error(methodName, "Validation failure", "profile", extProfile, "Adding external to sublist is restricted");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_EXT_SUBLIST_ADD_BIT_DISABLED,
                                "Adding external to sublist is restricted", getEntityId(),
                                getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MAX_EXT_PROFILE_FEATURESET, "");
                    }
                }

                if(contactDTO.isSublstInDispGrp()){
                    Iterator sublistInDispIterator=configured.iterator();
                    while (sublistInDispIterator.hasNext()){
                        Map.Entry pairs = (Map.Entry)sublistInDispIterator.next();
                        extProfile=(KnExtProfileDetails)pairs.getValue();
                        if(!extProfile.getFeatureSetMap().get(DISPATCH_GROUP_MANAGEMENT_FEATURE_BIT)){
                            knLogger.error(methodName, "Validation failure", "profile", extProfile, "Dispatch Grp Member feature bit is disabled");
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_EXT_CONTACT_DISP_MEMBER_BIT_DISABLED,
                                    "Dispatch Grp Member feature bit is disabled", getEntityId(),
                                    getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MAX_EXT_PROFILE_FEATURESET, "");
                        }
                    }
                }
                if(contactDTO.isSublistInGroup()){
                    Iterator sublistInGroupIterator=configured.iterator();
                    while (sublistInGroupIterator.hasNext()){
                        Map.Entry pairs = (Map.Entry)sublistInGroupIterator.next();
                        extProfile=(KnExtProfileDetails)pairs.getValue();
                        if(!extProfile.getFeatureSetMap().get(STANDARD_GROUP_MANAGEMENT_FEATURE_BIT)){
                            knLogger.error(methodName, "Validation failure", "profile", extProfile, "PreArranged Corp Grp feature bit is disabled");
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_EXT_CONTACT_PRE_ARRANGD_GROUP_BIT_DISABLED,
                                    "PreArranged Corp Grp feature bit is disabled", getEntityId(),
                                    getOperationType(), getRuleId(), KnConstants.KEY_DATATYPE_MAX_EXT_PROFILE_FEATURESET, "");
                        }
                    }
                }
            }else if(persistDTO instanceof KnCorpBCGrpPersistDTO){
                KnCorpBCGrpPersistDTO bcGrpPersistDTO = (KnCorpBCGrpPersistDTO) persistDTO;
                Map<Integer,KnExtProfileDetails> preArragFeauteMap = bcGrpPersistDTO.getConfiguredProfileMap();
                Map<Integer,KnExtProfileDetails> broadcasterFeauteMap = bcGrpPersistDTO.getExtBroadcasterFeatureMap();
                for(Map.Entry<Integer,KnExtProfileDetails> entry : preArragFeauteMap.entrySet()){
                    KnExtProfileDetails extProfileDetails = entry.getValue();
                    if(!extProfileDetails.getFeatureSetMap().get(5)){
                        knLogger.error(methodName, "Validation failure PreArranged Corp Grp feature bit is disabled");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_EXT_CONTACT_PRE_ARRANGD_GROUP_BIT_DISABLED,
                                "PreArranged Corp Grp feature bit is disabled", getEntityId(),
                                getOperationType(), getRuleId(),Arrays.asList(0).toString(), "");
                    }
                }

                for(Map.Entry<Integer,KnExtProfileDetails> entry : broadcasterFeauteMap.entrySet()){
                    KnExtProfileDetails extProfileDetails = entry.getValue();
                    if(!extProfileDetails.getFeatureSetMap().get(24)){
                        knLogger.error(methodName, "Validation failure PreArranged Corp Grp feature bit is disabled");
                        throw new KnCorpBOValidationException(KnErrorCodes.Validator.CORP_EXT_CONTACT_BROADCASTER_GROUP_BIT_DISABLED,
                                "PreArranged Corp Grp feature bit is disabled", getEntityId(),
                                getOperationType(), getRuleId(), Arrays.asList(0).toString(), "");
                    }
                }
            } else {
                throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR, "Invalid Persist DTO passed.",
                        getRuleId(), KnConstants.KEY_DATATYPE_MAX_EXT_PROFILE_FEATURESET);
            }
        }
        finally {
            knLogger.debug( methodName, "Exit Point: Validating successfull");
        }
    }

}
