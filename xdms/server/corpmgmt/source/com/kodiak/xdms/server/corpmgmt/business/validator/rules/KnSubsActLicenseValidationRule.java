/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.validator.rules;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorRule;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpActivationDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpGenActivationPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * ************************************************************************
 * <p/>
 * File name: KnSubsActLicenseValidationRule.java
 * Subsystem: PoC
 * <p/>
 * Name                     Date                    Release
 * -------------------- ------------ -------------------------------------
 * Saurabh Kumar         March 08, 2018            9.0
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

public class KnSubsActLicenseValidationRule extends KnValidatorRule {
    private static final KnLogger knLogger = KnLogger.getLogger(KnSubsActLicenseValidationRule.class);
    private static final String LICENSE_ENABLED = "licenseEnabled";
    private static final String NOT_ALLOWED_CLIENT_TYPE = "notAllowedTypes";
    private static final int ENABLED = 1;
    private static final int CLIENT_MAJOR_VERSION = 13;
    private static final int PROVISIONED = 0;

    public void validate() throws KnValidationException {
        final String methodName = "validate()";
        knLogger.debug(methodName, "ENTRY. Validating licenseType.");
        IPersistenceDTO persistDTO = getDTO();
        String licenseEnabled = getAttribute(LICENSE_ENABLED);
        Collection<String> licensedMdnsList = null;
        Collection<String> wifiAndCCMdnList = null;
        String tempPwd = null;
        int deviceSharingFlag = 0;
        String notAllowedType = getAttribute(NOT_ALLOWED_CLIENT_TYPE);
        List<String> clientTypeList = Arrays.asList(notAllowedType.split(DELIM));
        knLogger.debug(methodName, "persistDTO obtained from the request is - ", persistDTO);
        if (persistDTO instanceof KnCorpActivationPersistDTO) {
            KnIPCorpActivationDTO subscriberInfoDTO = (KnIPCorpActivationDTO) persistDTO.getInputDTO();
            KnCorpActivationPersistDTO subsDetails = (KnCorpActivationPersistDTO) persistDTO;
            tempPwd = subscriberInfoDTO.getTempPwd();
            deviceSharingFlag = subsDetails.getDeviceSharingFlag();
            licensedMdnsList = new ArrayList<>();
            wifiAndCCMdnList = new ArrayList<>();
            if ((subsDetails.getServiceAuthStatus() == PROVISIONED || subsDetails.getClientPVMajorVersion() > CLIENT_MAJOR_VERSION)
                    && (licenseEnabled.equals(String.valueOf(subsDetails.getLicenseType())))) {
                licensedMdnsList.add(subscriberInfoDTO.getMdn());
            }
            if((subsDetails.getServiceAuthStatus() == PROVISIONED || subsDetails.getClientPVMajorVersion() > CLIENT_MAJOR_VERSION)
                    && clientTypeList.contains(String.valueOf(subsDetails.getClientType())) && (licenseEnabled.equals(String.valueOf(subsDetails.getLicenseType())))){
                wifiAndCCMdnList.add(subscriberInfoDTO.getMdn());
            }
            Collection<KnCorpSubscriberDTO> subsLicenseTypeDto = subsDetails.getSubscriberAuthStatusList();
            if(subsLicenseTypeDto != null){
                licensedMdnsList.addAll(subsLicenseTypeDto.stream().filter(getLicenseMdns(licenseEnabled))
                        .map(KnCorpSubscriberDTO::getMdn).collect(Collectors.toList()));
                wifiAndCCMdnList.addAll(subsLicenseTypeDto.stream().filter(getWifiAndCCMdns(clientTypeList, licenseEnabled)).
                        map(KnCorpSubscriberDTO::getMdn).collect(Collectors.toList()));
            }
        } else if(persistDTO instanceof KnCorpGenActivationPersistDTO){
            KnCorpGenActivationPersistDTO subsDetails = (KnCorpGenActivationPersistDTO) persistDTO;
            deviceSharingFlag = subsDetails.getDeviceSharingFlag();
            Collection<KnCorpSubscriberDTO> subsLicenseTypeDto = subsDetails.getSubscriberAuthStatusList();
            licensedMdnsList = new ArrayList<>();
            wifiAndCCMdnList = new ArrayList<>();
            licensedMdnsList.addAll(subsLicenseTypeDto.stream().filter(getLicenseMdns(licenseEnabled))
                    .map(KnCorpSubscriberDTO::getMdn).collect(Collectors.toList()));
            wifiAndCCMdnList.addAll(subsLicenseTypeDto.stream().filter(getWifiAndCCMdns(clientTypeList, licenseEnabled))
                    .map(KnCorpSubscriberDTO::getMdn).collect(Collectors.toList()));

        } else if(persistDTO instanceof KnContactDetailsPersistDTO){
            KnContactDetailsPersistDTO subsDetails = (KnContactDetailsPersistDTO) persistDTO;
            deviceSharingFlag = subsDetails.getDeviceSharingFlag();
            Collection<KnCorpSubscriberDTO> subsLicenseTypeDto = subsDetails.getAddedMdnDTO();
            licensedMdnsList = new ArrayList<>();
            wifiAndCCMdnList = new ArrayList<>();
            licensedMdnsList.addAll(subsLicenseTypeDto.stream().filter(getLicenseMdns(licenseEnabled))
                    .map(KnCorpSubscriberDTO::getMdn).collect(Collectors.toList()));
            wifiAndCCMdnList.addAll(subsLicenseTypeDto.stream().filter(getWifiAndCCMdns(clientTypeList, licenseEnabled))
                    .map(KnCorpSubscriberDTO::getMdn).collect(Collectors.toList()));
        } else {
            knLogger.error("validate", "Unexpected DTO passed - ", persistDTO.getClass(),
                    ", Expected Dto - KnCorpGroupInfoPersistDTO ");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INTERNAL_ERROR,
                    "Unexpected instance of persistence DTO passed - " + persistDTO.getClass(),
                    getEntityId(), getOperationType(), getRuleId(), "DataType", "");
        }
        if (deviceSharingFlag == ENABLED && !licensedMdnsList.isEmpty() && tempPwd == null) {
            knLogger.debug(methodName, "Requested Mdn belongs to User License Type");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.SUBSCRIBER_IS_USER_LICENSE_TYPE,
                    "Requested Mdn belongs to User License Type",
                    getEntityId(), getOperationType(), getRuleId(), Arrays.asList(licensedMdnsList).toString(), "");

        }
        if (deviceSharingFlag == ENABLED && !wifiAndCCMdnList.isEmpty() && tempPwd == null) {
            knLogger.debug(methodName, "Invalid ClientType");
            throw new KnCorpBOValidationException(KnErrorCodes.Validator.INVALID_CLIENT_TYPE,
                    "InvalidClientType and PV version",
                    getEntityId(), getOperationType(), getRuleId(), Arrays.asList(wifiAndCCMdnList).toString(), "");

        }
        knLogger.debug(methodName, "Exit Point: Validation Successfully done for LicenseType Subscriber");
    }

    private static Predicate<KnCorpSubscriberDTO> getWifiAndCCMdns(List<String> clientTypeList, String licenseEnabled) {
        return subscriberDTO -> (licenseEnabled.equals(String.valueOf(subscriberDTO.getLicenseType())) && clientTypeList.contains(String.valueOf(subscriberDTO.getClientType()))
                && (subscriberDTO.getClientPVmajorVer() > CLIENT_MAJOR_VERSION || subscriberDTO.getServiceAuthStatus() == PROVISIONED));
    }

    private static Predicate<KnCorpSubscriberDTO> getLicenseMdns(String licenseEnabled) {
        return subscriberDTO -> (licenseEnabled.equals(String.valueOf(subscriberDTO.getLicenseType())))
                && (subscriberDTO.getClientPVmajorVer() > CLIENT_MAJOR_VERSION || subscriberDTO.getServiceAuthStatus() == PROVISIONED);
    }
}