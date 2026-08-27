/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnAbdgGrpOwnerFeatureBitValidationRule.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar        01-03-2018      9.0+
 * <p/>
 * <p/>
 * 
 * 
 * KODIAK, 9th Floor, 'MFar
 * Manyata Tech Park' Greenheart Phase IV,
 * Nagawara Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.xdms.server.corpmgmt.business.validator.abdg.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpGroupInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;

import static com.kodiak.common.resources.KnConstants.AREA_BASED_DYNAMIC_GROUP;

public class KnAbdgGrpOwnerFeatureBitValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAbdgGrpTypeValidationRule.class);
    private String CLASS = KnAbdgGrpTypeValidationRule.class.getName();
    private static KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point ");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
            KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
            Collection<KnCorpSubscriberDTO> addedMdnDto = corpGroupInfoPersistDTO.getAddedMdnDTO();
            String grpOwner = corpGroupInfoPersistDTO.getTpGroupOwner();
            knLogger.debug(methodName, "grpOwner:  - ", grpOwner);
            if (AREA_BASED_DYNAMIC_GROUP == groupInfoDTO.getClientType() && groupInfoDTO.getGroupType() == KnConstants.DISPATCH_GROUP) {
                knLogger.debug(methodName, "addedMdnDto:  - ", addedMdnDto);
                if (addedMdnDto != null) {
                    Optional<KnCorpSubscriberDTO> grpOwnerDto = addedMdnDto.stream().filter(mdnDto -> mdnDto.getMdn().equals(grpOwner)).findFirst();
                    knLogger.debug(methodName, "grpOwnerDto:  - ", grpOwnerDto.isPresent());
                    if (grpOwnerDto.isPresent()) {
                    	BitSet mdnSubsFsBitSet = featureSetUtil.convertHexStringToBitSet(grpOwnerDto.get().getSubsActiveFS2());
                        if (!mdnSubsFsBitSet.get(com.kodiak.common.resources.KnConstants.FEATURE_SET.ABDG_GROUP_OWNER.value())) {
                            throw new KnCorpBOValidationException(KnErrorCodes.Validator.MDN_DO_NOT_HAVE_PERMISSIONS_TO_BECOME_GROUP_OWNER,
                                    "MDN do not have permissions to become groupOwner", getEntityId(), getOperationType(), getRuleId(),
                                    Arrays.asList(grpOwnerDto.get().getMdn()).toString(), "");
                        }
                    }
                }
            }
        }
        knLogger.debug(methodName, "Validation Completed Successfully for the Grp Member feature Count");
    }
}
