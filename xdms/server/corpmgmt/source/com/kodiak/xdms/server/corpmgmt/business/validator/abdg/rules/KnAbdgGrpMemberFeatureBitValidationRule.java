/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnAbdgGrpMemberFeatureBitValidationRule.java
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;

import static com.kodiak.common.resources.KnConstants.AREA_BASED_DYNAMIC_GROUP;

public class KnAbdgGrpMemberFeatureBitValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnAbdgGrpTypeValidationRule.class);
    private String CLASS = KnAbdgGrpTypeValidationRule.class.getName();
    private static KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY Point");
        IPersistenceDTO persistDTO = getDTO();
        if (persistDTO instanceof KnCorpGroupInfoPersistDTO) {
            knLogger.debug(methodName, "persistDTO is instanceof KnCorpGroupInfoPersistDTO - ", persistDTO);
            KnCorpGroupInfoPersistDTO corpGroupInfoPersistDTO = (KnCorpGroupInfoPersistDTO) persistDTO;
            KnIPCorpGroupInfoDTO groupInfoDTO = (KnIPCorpGroupInfoDTO) corpGroupInfoPersistDTO.getInputDTO();
            Collection<KnCorpSubscriberDTO> addedMdnDto = corpGroupInfoPersistDTO.getAddedMdnDTO();
            String grpOwner = corpGroupInfoPersistDTO.getTpGroupOwner();
            knLogger.debug(methodName, "grpOwner:  - ", grpOwner);
            Collection<String> notAllowedMemberList = new ArrayList<>();
            if (AREA_BASED_DYNAMIC_GROUP == groupInfoDTO.getClientType() && groupInfoDTO.getGroupType() == KnConstants.DISPATCH_GROUP) {
                if(addedMdnDto != null){
                    addedMdnDto.forEach(mdnDto -> {
                        String mdn = mdnDto.getMdn();
                        if (!mdn.equals(grpOwner)) {
                        	BitSet mdnSubsFsBitSet = featureSetUtil.convertHexStringToBitSet(mdnDto.getSubsActiveFS2());
                            if (!mdnSubsFsBitSet.get(com.kodiak.common.resources.KnConstants.FEATURE_SET.ABDG_GROUP_MEMBER.value())) {
                                notAllowedMemberList.add(mdn);
                            }
                        }
                    });
                }
                if (!notAllowedMemberList.isEmpty()) {
                    throw new KnCorpBOValidationException(KnErrorCodes.Validator.GROUP_MEMBER_NOT_ALLOWED_TO_BE_PART_OF_ABDG_GROUP,
                            "GroupMember not allowed to be part of ABDG", getEntityId(), getOperationType(), getRuleId(),
                            Arrays.asList(notAllowedMemberList).toString(), "");
                }
            }
        }
        knLogger.debug(methodName, "Validation Completed Successfully for the Grp Member feature Count");
    }
}
