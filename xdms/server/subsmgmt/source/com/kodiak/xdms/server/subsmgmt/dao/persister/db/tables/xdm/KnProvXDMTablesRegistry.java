/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/******************************************************************************
 * File name:   KnProvXDMTablesRegistry.java
 * Subsystem:
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ravi Shanker .P       Dec 15, 2010       7.0
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
 * *******************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dao.persister.db.tables.xdm;

public final class KnProvXDMTablesRegistry {


    public KnProvXDMTablesRegistry() {
    }


    public KnCorpInfoDAO createCorpInfoDAO(String pttServerId) {
        return new KnCorpInfoDAO(pttServerId);
    }

    public KnCorpAddlInfoDAO createCorpAddlInfoDAO(String pttServerId) {
        return new KnCorpAddlInfoDAO(pttServerId);
    }

    public KnPOCSubscrInfoDAO createPOCSubscrInfoDA0(String pttServerId) {
        return new KnPOCSubscrInfoDAO(pttServerId);
    }

    public KnPOCSubscrAddlInfoDAO createPOCSubscrAddlInfoDA0(String pttServerId) {
        return new KnPOCSubscrAddlInfoDAO(pttServerId);
    }

    public KnPOCSubscrRoamingProfileDAO createPOCSubscrRoaminProfileDAO(String pttServerId) {
        return new KnPOCSubscrRoamingProfileDAO(pttServerId);
    }

    public KnDialPlanInfoDAO createDialPlanInfoDAO(String pttServerId) {
        return new KnDialPlanInfoDAO(pttServerId);
    }

    public KnInstaPOCSrvcCfgDAO createInstaPOCSrvcCfgDAO(String pttServerId) {
        return new KnInstaPOCSrvcCfgDAO(pttServerId);
    }

    public KnPOCRegistrarSrvcConfigDAO createPOCRegistrarSrvcConfigDAO(String pttServerId) {
        return new KnPOCRegistrarSrvcConfigDAO(pttServerId);
    }

    public KnPOCSvcConfigDAO createPOCSvcConfigDAO(String pttServerId) {
        return new KnPOCSvcConfigDAO(pttServerId);
    }

    public KnPresenceServiceConfigDAO createrPresenceServerConfigDAO(String pttServerId) {
        return new KnPresenceServiceConfigDAO(pttServerId);
    }

    public KnXDMSDocSubPrxConfigDAO createXDMSDocSubPrxConfigDAO(String pttServerId) {
        return new KnXDMSDocSubPrxConfigDAO(pttServerId);
    }

    public KnXDMSSvcConfigDAO createXDMSSvcConfigDAO(String pttServerId) {
        return new KnXDMSSvcConfigDAO(pttServerId);
    }

    public KnSWPkgConfigDAO createSWPkgConfigDAO(String pttServerId) {
        return new KnSWPkgConfigDAO(pttServerId);
    }

    public KnFeatureAccessNumberInfoDAO createFeatureAccessNumberInfoDAO(String pttServerId) {
        return new KnFeatureAccessNumberInfoDAO(pttServerId);
    }

    public KnFeatureAccessInfoDAO createFeatureAccessInfoDAO(String pttServerId) {
        return new KnFeatureAccessInfoDAO(pttServerId);
    }

    public KnSIPProxySvcConfigDAO createSIPProxySvcConfigDAO(String pttServerId) {
        return new KnSIPProxySvcConfigDAO(pttServerId);
    }

    public KnPAMAccInfoDAO createPAMAccountInfoDAO(String pttServerId) {
        return new KnPAMAccInfoDAO(pttServerId);
}

    public KnPAMSubscrProfInfoDAO createPAMSubsProfInfoDAO(String pttServerId) {
        return new KnPAMSubscrProfInfoDAO(pttServerId);
    }


    public KnPAMSvcConfigDAO createPAMSvcConfigDAO(String pttServerId) {
        return new KnPAMSvcConfigDAO(pttServerId);
    }

    public KnExtSubscriberInfoDAO createExtSubsInfoDA0(String pttServerId) {
        return new KnExtSubscriberInfoDAO(pttServerId);
    }

    public KnSubsAPNInfoDAO createSubsAPNInfoDA0(String pttServerId) {
        return new KnSubsAPNInfoDAO(pttServerId);
    }

    public KnPOCNNISubscrInfoDAO createPOCNNISubscrInfoDA0(String pttServerId) {
        return new KnPOCNNISubscrInfoDAO(pttServerId);
    }

    public KnCorpGwLinkedAccInfoDAO createCorpGwLinkedAccInfoDAO(String pttServerId){
        return new KnCorpGwLinkedAccInfoDAO(pttServerId);
    }

    public KnPAMAccPoolUsageDAO createPAMAccPoolUsageDAO(String pttServerId){
    	return new KnPAMAccPoolUsageDAO(pttServerId);
    }

    public KnTPUserMDNMapDAO createTPUserMDNMapDAO(String pttServerId){
    	return new KnTPUserMDNMapDAO(pttServerId);
    }

    public KnTPAccountDAO createKnTPAccountDAO(String pttServerId){
    	return new KnTPAccountDAO(pttServerId);
    }

    public KnClientSuppVocodersDAO clientSuppVocoderDAO(String pttServerId) {
        return new KnClientSuppVocodersDAO(pttServerId);
    }


    public KnMCPTTPermInfoDAO deleteMCPTTInfoDAO(String pttServerId){
        return new KnMCPTTPermInfoDAO(pttServerId);
    }

    public KnAuthorizationDocDAO deleteAuthorizationDocDAO(String pttServerId){
        return new KnAuthorizationDocDAO(pttServerId);
    }

    public KnMCPTTPermInfoDAO updateMCPTTTargetMdn(String pttServerId){
        return new KnMCPTTPermInfoDAO(pttServerId);
    }

    public KnMCPTTPermInfoDAO fetchMCPTTAuthorizedMdn(String pttServerId){
        return new KnMCPTTPermInfoDAO(pttServerId);
    }

    public KnAuthorizationDocDAO updateAuthorizedDocEtag(String pttServerId){
        return new KnAuthorizationDocDAO(pttServerId);
    }

    public KnAuthorizationDocDAO createNewMdnInAuthorizationDoc(String pttServerId){
        return new KnAuthorizationDocDAO(pttServerId);
    }

    public KnMCPTTPermInfoDAO updateMCPTTAuthorizedMdn(String pttServerId){
        return new KnMCPTTPermInfoDAO(pttServerId);
    }
	public KnSubsAddOnPkgInfoDAO createSubsAddOnPkgInfoDAO(String pttServerId) {

        return new KnSubsAddOnPkgInfoDAO(pttServerId);
    }
    public KnPAMSubsAddOnPkgInfoDAO createPAMSubsAddOnPkgInfoDAO(String pttServerId) {
        return new KnPAMSubsAddOnPkgInfoDAO(pttServerId);
    }

    public KnDeviceInfoDAO createDeviceInfoDAO(String pttServerId) {
        return new KnDeviceInfoDAO(pttServerId);
    }

    public KnSubscrPTTRadioGrpListDocDAO createPTTRadioGrpListDAO(String pttServerId) {
        return new KnSubscrPTTRadioGrpListDocDAO(pttServerId);
    }
    public KnSimulSessionDocDAO createTGSSDocDAO(String pttServerId) {
        return new KnSimulSessionDocDAO(pttServerId);
    }

    public KnSSChannelGroupInfoDAO createSSChannelGroupInfoDAO(String pttServerId) { return new KnSSChannelGroupInfoDAO(pttServerId); }
    
    public KnUserProfileMdnMapDAO createUserProfileMdnMapDAO(String pttServerId) {
        return new KnUserProfileMdnMapDAO(pttServerId);
    }

	public KnSubsCameraInfoDAO createCameraInfoDAO(String pttServerId) {
        return new KnSubsCameraInfoDAO(pttServerId);
    }

    public KnSubsAliasIdInfoDAO createAliasIdInfoDAO(String pttServerId) {
        return new KnSubsAliasIdInfoDAO(pttServerId);
    }
    public KnAmperGatewayDAO createAmperGatewayDAO(String pttServerId) {
        return new KnAmperGatewayDAO(pttServerId);
    }

    public KnEmergencyDocDAO createEmergencyDocDAO(String pttServerId) {
        return new KnEmergencyDocDAO(pttServerId);
    }
}
