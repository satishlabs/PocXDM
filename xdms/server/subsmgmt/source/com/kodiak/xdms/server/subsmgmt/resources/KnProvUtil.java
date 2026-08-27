/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.resources;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.kodiak.common.commdto.request.KnXDMCorpProfileInfoDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.common.KnXDMSServiceConfigDTO;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.subsmgmt.business.KnProvBOException;
import com.kodiak.xdms.server.subsmgmt.business.helper.KnProvInfoUtil;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.common.resources.KnConstants;


/**
 * Created by Deepak on 10/4/14.
 */
public class KnProvUtil {
    private static final String COMMA = ",";
    private static final String SINGLE_QUOTE = "'";
    private static final KnLogger knLogger = KnLogger.getLogger(KnProvUtil.class);
    private static final int MIN_ASSIGN_PROFILES=1;
    private static final int MIN_MAX_CHANNELS_PER_ZONE=8;
    private static final int MIN_MAX_CHANNELS_ALLOWED=8;
    private static final int MIN_MAXMEM_PER_BC_GROUP=2;
    private KnGenInfoUtil genInfoUtil;

    public KnProvUtil() {
        genInfoUtil=KnGenInfoUtil.getInstance();
    }

    public static String getComSepList(Collection<String> collectionStr) {
        StringBuffer buffer = new StringBuffer(200);
        for (String mdn : collectionStr) {
            buffer = buffer.append(SINGLE_QUOTE).append(mdn).append(SINGLE_QUOTE).append(COMMA);
        }
        return buffer.substring(0, buffer.length() - 1);
    }

    public static int getMappedSubscriptionType(int publicSubscType, int corpSubscType) {

        String methodName = "getMappedSubscriptionType(publicSubscType, corpSubscType)";
        int subscriptionType = 0;
        knLogger.entry( methodName, "PiublicSubscType - " , publicSubscType , ", CorpSubscType - " , corpSubscType);
        if (publicSubscType == 1 && corpSubscType == 0) {
            subscriptionType = 0;
        } else if (publicSubscType == 0 && corpSubscType == 1) {
            subscriptionType = 1;
        } else if (publicSubscType == 1 && corpSubscType == 1) {
            subscriptionType = 2;
        } else {
            knLogger.debug( methodName, "Unknown type, mapping to default type - 0");
        }
        knLogger.exit(methodName, subscriptionType);
        return subscriptionType;
    }

    public void validateCorpProfile(KnXDMCorpProfileInfoDTO corpProfileDTO, String xdmPttServerId, KnPersisterTxn persisterTxn) throws KnBOException, KnProvBOException {

        try {
            KnXDMSServiceConfigDTO xdmsServiceConfigDTO = genInfoUtil.retrieveXDMSServiceConfig(xdmPttServerId, persisterTxn);
            knLogger.debug("Validate Corp Account xdmsServiceConfigDTO- :", xdmsServiceConfigDTO);
            Optional<KnXDMCorpProfileInfoDTO> corpProfile = Optional.ofNullable(corpProfileDTO);
            Optional<String> maxSubscribers = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxSubscribers);
            if ((maxSubscribers.isPresent() && !maxSubscribers.get().trim().equals("")) &&
                    (!maxSubscribers.get().matches("^[0-9]*$") || Integer.parseInt(maxSubscribers.get()) > xdmsServiceConfigDTO.getMaxSubscrPerCorp())) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_SUBSCRIBER,
                        "Max Subscribers not in range");
            }


            Optional<String> maxCorpLists = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxCorpLists);
            if ((maxCorpLists.isPresent() && !maxCorpLists.get().trim().equals("") && (!maxCorpLists.get().matches("^[0-9]*$") || Integer.parseInt(maxCorpLists.get()) > xdmsServiceConfigDTO.getMaxSublistsPerCorp()))) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_CORP_LISTS,
                        "Max Corp Lists not in range");
            }
            Optional<String> maxMemPerCorpList = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxMemPerCorpList);
            if ((maxMemPerCorpList.isPresent() && !maxMemPerCorpList.get().trim().equals("") && (!maxMemPerCorpList.get().matches("^[0-9]*$") || Integer.parseInt(maxMemPerCorpList.get()) > xdmsServiceConfigDTO.getMaxMembersPerCorpSublist()))) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_MEM_PER_CORPLIST,
                        "Max Members Per CorpList not in range");
            }

            Optional<String> maxCorpGroups = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxCorpGroups);
            if ((maxCorpGroups.isPresent() && !maxCorpGroups.get().trim().equals("") && (!maxCorpGroups.get().matches("^[0-9]*$") || Integer.parseInt(maxCorpGroups.get()) > xdmsServiceConfigDTO.getMaxPOCGrpsPerCorp()))) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_CORP_GROUPS,
                        "Max Corp Groups not in range");
            }

            Optional<String> maxMemPerCorpGroup = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxMemPerCorpGroup);

            if ((maxMemPerCorpGroup.isPresent() && !maxMemPerCorpGroup.get().trim().equals("") && (!maxMemPerCorpGroup.get().matches("^[0-9]*$") || Integer.parseInt(maxMemPerCorpGroup.get()) > xdmsServiceConfigDTO.getMaxMembersPerCorpPOCGrp()))) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_MEM_PER_CORPGROUP,
                        "Max Member Per CorpGroup not in range");
            }


            Optional<String> maxExtSubsPerCorp = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxExtSubsPerCorp);
            if ((maxExtSubsPerCorp.isPresent() && !maxExtSubsPerCorp.get().trim().equals("")) && (!maxExtSubsPerCorp.get().matches("^[0-9]*$") || Integer.parseInt(maxExtSubsPerCorp.get()) > xdmsServiceConfigDTO.getMaxExtSubsPerCorp())) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_EXT_SUBS_PER_GROUP,
                        "Max External Subscriber Per Group not in range");
            }


            Optional<String> maxDispatchGroups = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxDispatchGroups);
            if ((maxDispatchGroups.isPresent() && !maxDispatchGroups.get().trim().equals("")) && (!maxDispatchGroups.get().matches("^[0-9]*$") || Integer.parseInt(maxDispatchGroups.get()) > xdmsServiceConfigDTO.getMaxDispatchGroup())) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_DISPATCHER_GROUPS,
                        "Max DispatcherGroups not in range");
            }


            Optional<String> maxMemPerDispatchGroup = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxMemPerDispatchGroup);

            if ((maxMemPerDispatchGroup.isPresent() && !maxMemPerDispatchGroup.get().trim().equals("")) && (!maxMemPerDispatchGroup.get().matches("^[0-9]*$") || Integer.parseInt(maxMemPerDispatchGroup.get()) > xdmsServiceConfigDTO.getMaxMembersPerDispatchGroup())) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_MEM_PER_DISPATCHER_GROUP,
                        "Max Member Per Dispatcher Group not in range");
            }

            Optional<String> dispatchEnabled = corpProfile.map(KnXDMCorpProfileInfoDTO::getDispatchEnabled);
            if ((dispatchEnabled.isPresent() && !dispatchEnabled.get().trim().equals("")) && (!dispatchEnabled.get().equalsIgnoreCase("true") && !dispatchEnabled.get().equalsIgnoreCase("false"))) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.DISPATCHER_ENABLED,
                        "Input validation failed for Dispatcher Enabled");
            }

            Optional<String> isInterOpEnabled = corpProfile.map(KnXDMCorpProfileInfoDTO::getIsInterOpEnabled);
            if ((isInterOpEnabled.isPresent() && !isInterOpEnabled.get().trim().equals("")) && (!isInterOpEnabled.get().equalsIgnoreCase("true") && !isInterOpEnabled.get().equalsIgnoreCase("false"))) {
                throw new KnProvBOException(
                        KnErrorCodes.BOEntity.IS_INTER_OP_ENABLED,
                        "Input Validation failed for Is InterOpEnabled");
            }
            Optional<String> maxMemPerBCGrp = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxMemPerBCGrp);

            if ((maxMemPerBCGrp.isPresent() && !maxMemPerBCGrp.get().trim().equals("")) && (!maxMemPerBCGrp.get().matches("^[0-9]*$") || Integer.parseInt(maxMemPerBCGrp.get()) < MIN_MAXMEM_PER_BC_GROUP || Integer.parseInt(maxMemPerBCGrp.get()) > xdmsServiceConfigDTO.getMaxMemPerBCGrp())) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_MEM_PER_BC_GROUP,
                        "Max members Per BC Group is not in range");
            }

            Optional<String> maxChannelAllowed = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxChannelAllowed);
            if ((maxChannelAllowed.isPresent() && !maxChannelAllowed.get().trim().equals("")) && (!maxChannelAllowed.get().matches("^[0-9]*$") || Integer.parseInt(maxChannelAllowed.get()) < MIN_MAX_CHANNELS_ALLOWED || Integer.parseInt(maxChannelAllowed.get()) > xdmsServiceConfigDTO.getMaxRadioChannels())) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_CHANNEL_ALLOWED,
                        "Max ChannelAllowed is not in range");
            }

            Optional<String> maxZoneAllowed = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxZoneAllowed);

            if ((maxZoneAllowed.isPresent() && !maxZoneAllowed.get().trim().equals("")) && (!maxZoneAllowed.get().matches("^[0-9]*$") || Integer.parseInt(maxZoneAllowed.get()) > xdmsServiceConfigDTO.getMaxZones())) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_ZONE_ALLOWED,
                        "Max Zones Allowed are not in range");
            }
            Optional<String> maxChannelsPerZone = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxChannelsPerZone);

            if ((maxChannelsPerZone.isPresent() && !maxChannelsPerZone.get().trim().equals("")) && (!maxChannelsPerZone.get().matches("^[0-9]*$") || Integer.parseInt(maxChannelsPerZone.get()) < MIN_MAX_CHANNELS_PER_ZONE || Integer.parseInt(maxChannelsPerZone.get()) > xdmsServiceConfigDTO.getMaxChannelsPerZone())) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_CHALLENS_PER_ZONE,
                        "Max Channels Per are Zone not in range");
            }

            Optional<String> maxLgrGrp = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxLgrGrp);

            if ((maxLgrGrp.isPresent() && !maxLgrGrp.get().trim().equals("")) && (!maxLgrGrp.get().matches("^[0-9]*$") || Integer.parseInt(maxLgrGrp.get()) > xdmsServiceConfigDTO.getMaxLrgGrpPerCorp())) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_LGR_GROUP,
                        "Max Large Group is not in range");
            }
            int clusterId = Integer.parseInt(System.getenv(com.kodiak.common.resources.KnConstants.CLUSTERID_ENV_NAME));
            Map<String, String> microServicesParamNameValueMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            knLogger.debug("Validation microServicesParamNameValueMap :", microServicesParamNameValueMap);
            Optional<String> maxGrpProfiles = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxGrpProfiles);

            if ((maxGrpProfiles.isPresent() && !maxGrpProfiles.get().trim().equals(""))
                    && (!maxGrpProfiles.get().matches("^[0-9]*$")
                    || (Integer.parseInt(maxGrpProfiles.get()) > Integer.parseInt(microServicesParamNameValueMap.get(KnProvConstants.MAX_GROUP_PROFILES))))) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_GRP_PROFILE,
                        "Max Group Profile not in range");
            }

            Optional<String> maxUserProfiles = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxUserProfiles);

            if ((maxUserProfiles.isPresent() && !maxUserProfiles.get().trim().equals(""))
                    && (!maxUserProfiles.get().matches("^[0-9]*$")
                    || (Integer.parseInt(maxUserProfiles.get()) > Integer.parseInt(microServicesParamNameValueMap.get(KnConstants.MAX_USER_PROFILES))))) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_USER_PROFILE,
                        "Max User Profile not in range");
            }

            Optional<String> maxAssignProfiles = corpProfile.map(KnXDMCorpProfileInfoDTO::getMaxAssignProfiles);

            if ((maxAssignProfiles.isPresent() && !maxAssignProfiles.get().trim().equals(""))
                    && (!maxAssignProfiles.get().matches("^[0-9]*$")
                    || (Integer.parseInt(maxAssignProfiles.get()) < MIN_ASSIGN_PROFILES)
                    || (Integer.parseInt(maxAssignProfiles.get()) > Integer.parseInt(microServicesParamNameValueMap.get(KnConstants.MAX_USERPROFILES_PERSUB1))))) {
                throw new KnProvBOException(
                        com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.MAX_ASSIGN_PROFILES,
                        "Max Assign Profile is not in range");
            }

            Optional<String> isLargeAgencyDispatchFeature = corpProfile.map(KnXDMCorpProfileInfoDTO::getLargeAgencyDispatchFeature);
            if (isLargeAgencyDispatchFeature.isPresent() && (!isLargeAgencyDispatchFeature.get().trim().isEmpty())) {
                boolean isValid = isLargeAgencyDispatchFeature.get().trim().equalsIgnoreCase(String.valueOf(Boolean.TRUE));
                isValid = isValid || isLargeAgencyDispatchFeature.get().trim().equalsIgnoreCase(String.valueOf(Boolean.FALSE));
                if (!isValid) {
                    throw new KnProvBOException(
                            com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity.LARGE_AGENCY_DISPATCH_FEATURE,
                            "Input parameter validation failed. Parameter: [largeAgencyDispatchFeature: '" + isLargeAgencyDispatchFeature.get() + "']");
                }
            }

        } catch (KnBOException e) {
            throw e;
        } catch (KnProvBOException e) {
            throw e;
        }

    }


    /**
     * Method to check activation subscriber profile value with existing DB value.
     *
     * @param activateReqInfo
     * @param subsProfileDBInfo
     * @return
     */
    public static boolean isDBUpdateRequired(KnSubsProfilePersistDTO activateReqInfo, KnOPSubsProfileInfoDTO subsProfileDBInfo) {
        String methodName = "isDBUpdateRequired()";
        boolean flag = true;
        try {
            knLogger.debug(methodName,"activateReqInfo::",activateReqInfo);
            knLogger.debug(methodName,"subsProfileDBInfo::",subsProfileDBInfo);
            flag = !(Objects.equals(activateReqInfo.getMdn(), subsProfileDBInfo.getMdn())
                    && Objects.equals(activateReqInfo.getServiceAuthStatus(), subsProfileDBInfo.getServiceAuthStatus())
                    && Objects.equals(activateReqInfo.getClientPassword(), subsProfileDBInfo.getClientPassword())
                    && Objects.equals(activateReqInfo.getUserAgent(), subsProfileDBInfo.getUserAgent())
                    && Objects.equals(activateReqInfo.getVocoderId(), subsProfileDBInfo.getVocoderId())
                    && Objects.equals(activateReqInfo.getClientPVmajorVer(), subsProfileDBInfo.getClientPVmajorVer())
                    && Objects.equals(activateReqInfo.getClientPVminorVer(), subsProfileDBInfo.getClientPVminorVer())
                    && Objects.equals(activateReqInfo.getSwType(), subsProfileDBInfo.getSwType())
                    && Objects.equals(activateReqInfo.getPlatformType(), subsProfileDBInfo.getPlatformType())
                    && Objects.equals(activateReqInfo.getDynamicQosFlag(), subsProfileDBInfo.getDynamicQosFlag())
                    && Objects.nonNull(activateReqInfo.getDerivedKey())
                    && Objects.equals(KnGeneralUtil.convertAsciiToHex(activateReqInfo.getDerivedKey()), subsProfileDBInfo.getDerivedKey())
                    && Objects.equals(activateReqInfo.getServiceStatusOp(), subsProfileDBInfo.getServiceStatusOp())
                    && Objects.equals(activateReqInfo.getSubsClientType(), subsProfileDBInfo.getSubsClientType())
                    && Objects.equals(KnGeneralUtil.convertHexStringToBitSet(activateReqInfo.getSubsFS2()), KnGeneralUtil.convertHexStringToBitSet(subsProfileDBInfo.getSubsFS2()))
                    && Objects.equals(KnGeneralUtil.convertHexStringToBitSet(activateReqInfo.getClientFS2()), KnGeneralUtil.convertHexStringToBitSet(subsProfileDBInfo.getClientFS2()))
                    && Objects.equals(KnGeneralUtil.convertHexStringToBitSet(activateReqInfo.getActiveFS2()), KnGeneralUtil.convertHexStringToBitSet(subsProfileDBInfo.getActiveFS2()))
                    && Objects.equals(KnGeneralUtil.convertHexStringToBitSet(activateReqInfo.getOpsFS2()), KnGeneralUtil.convertHexStringToBitSet(subsProfileDBInfo.getOpsFS2()))
                    && Objects.equals(KnGeneralUtil.convertHexStringToBitSet(activateReqInfo.getXdmsFS2()), KnGeneralUtil.convertHexStringToBitSet(subsProfileDBInfo.getXdmsFS2()))
                    && Objects.equals(activateReqInfo.getLicenseType(), subsProfileDBInfo.getLicenseType())
                    && Objects.equals(activateReqInfo.getPrivacyExecutorBasedonFlag(), subsProfileDBInfo.getPrivacyOptStatus()));
            if (!flag && null != activateReqInfo.getIMEI()) {
                flag = !Objects.equals(activateReqInfo.getIMEI(), subsProfileDBInfo.getIMEI());
            }
            if (!flag && null != activateReqInfo.getPoCHome()) {
                flag = !Objects.equals(activateReqInfo.getPoCHome(), subsProfileDBInfo.getPoCHome());
            }

        } catch (Exception e) {
            knLogger.error(methodName, "Excpetion occured ", e);
        }
        knLogger.info(methodName, "Flag - ", flag);
        return flag;
    }

    public static int getBitValueInInt(String activeFS2, int bitNumber) {
        boolean bitEnabled = KnGeneralUtil.getFeatureBitValue(activeFS2, bitNumber);
        if(bitEnabled){
            return 1;
        }
        return 0;
    }
}
