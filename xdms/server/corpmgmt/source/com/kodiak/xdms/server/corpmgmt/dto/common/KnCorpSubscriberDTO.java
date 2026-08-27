/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpSubscriberDTO.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Jan 11, 2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dto.common;

import com.kodiak.common.resources.KnGDPRTemplate;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnCorpSubscriberDTO implements Comparable<KnCorpSubscriberDTO> , Cloneable{

    private int corpId;
    private String mdn;
    private String name;
    private String oldName;
    private int subscriptionType;
    private int serviceAuthStatus;
    private boolean externalContact;

    private int maxContactLimitFlag;
    private int groupETag;
    private long etag;
    private int contactCount;
    private int clientType;
    private int parentId;

    private String activationCode;
    private Timestamp activationTimestamp;
    private Timestamp expiryTime;
    //Notification Capability bit
    private boolean notfnCapabiliy;

    //stores the PoC Home
    private String pocHome;
    //stores the presence Home
    private String presenceHome;
    
    private String xdmsHome;

    private String subscEmail;
    private int pamAccId;
    private String subsActiveFS1;
    private String subsActiveFS2;
    private String corpAdminFS1;
    private String corpAdminFS2;
    
    private long lastProfileUpdateTime;
    private int clientPVmajorVer;
    private int clientPVminorVer;
    private Integer dispatchGrpmember;
    // POCHOME,PRESENCEHOME,XDMSHOME,CLIENTFS1,SUBSCRIBERFS1, OPSFS1
    
    private String clientFs2;
    private String subscriberFs2;
    private String opsFs2;
    
    private int subsType;
    //This variable tells about the external contact type.
    private int contact_type;

    private Map<String,Object> customMap;

    //Modified for 8.1.1 changes
    private int callInitiatePermission;
    private int callReceivePermission;
    private int inCallPermission;
    private int videoCallReceivePermission;
    private int videoCallInitiatePermission;
    private int videoInCallPermission;
    private int dispatchType;
    private String userId;
    private String userAgent;
    private String ua;
    private int serviceAuthStatusAU;
    private Integer groupModifyPerm;
    private	String xdmsFs2;
    private String aliasMdn;
    private int licenseType;
    private int isAuthUser;
    private String commandPackageCode;
    private int grpSublistId;
    private int isOSMAuthorize;
    private int mcpttCompliance;
    private String mcId;
    private String mcpttId;
    private String mcDataId;
    private String mcVideoId;
	private String userProfileId;
    private Integer userProfileIndex;
    private String userProfileFS2;
    private Long lastActivationTime;
    private String IMEI;
    private String clientPassword;
    private int vocoderId;
    private int swType;
    private int platformType;
    private int dynamicQosFlag;
    private String derivedKey;
    private Integer serviceStatusOp;
    private boolean upmCall;
    private Integer cameraType;
    public Integer clusterId;

    private String oldActiveFs;

    private Integer isDefaultProfile;

    private Integer commonContact;

    private String deviceId;

    private Map<String,Object> packageInfo;

    private String extGatewayId;
    private String hierarchyId;

    private List<String> mdnList;

    public String getOldActiveFs() {
        return oldActiveFs;
    }

    private String billingMDN;

    public void setOldActiveFs(String oldActiveFs) {
        this.oldActiveFs = oldActiveFs;
    }

    public KnCorpSubscriberDTO(String mdn) {
        this.mdn = mdn;
    }


    public KnCorpSubscriberDTO(String mdn, String name, int corpId) {
        this.mdn = mdn;
        this.name = name;
        this.corpId = corpId;
    }

    public KnCorpSubscriberDTO(String mdn, int grpSublistId, int corpId) {
        this.mdn = mdn;
        this.grpSublistId = grpSublistId;
        this.corpId = corpId;
    }

    public KnCorpSubscriberDTO(String mdn, String name) {
        this.mdn = mdn;
        this.name = name;
    }

    public KnCorpSubscriberDTO(String name,int contact_type) {
        this.name = name;
        this.contact_type = contact_type;
    }
    public KnCorpSubscriberDTO() {

    }


    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public int getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(int subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public boolean isExternalContact() {
        return externalContact;
    }

    public void setExternalContact(boolean externalContact) {
        this.externalContact = externalContact;
    }

    public int getGroupETag() {
        return groupETag;
    }

    public void setGroupETag(int groupETag) {
        this.groupETag = groupETag;
    }

    public int getMaxContactLimitFlag() {
        return maxContactLimitFlag;
    }

    public void setMaxContactLimitFlag(int maxContactLimitFlag) {
        this.maxContactLimitFlag = maxContactLimitFlag;
    }

    public long getEtag() {
        return etag;
    }

    public void setEtag(long etag) {
        this.etag = etag;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Timestamp getActivationTimestamp() {
        return activationTimestamp;
    }

    public void setActivationTimestamp(Timestamp activationTimestamp) {
        this.activationTimestamp = activationTimestamp;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public boolean isNotfnCapabiliy() {
        return notfnCapabiliy;
    }

    public void setNotfnCapabiliy(boolean notfnCapabiliy) {
        this.notfnCapabiliy = notfnCapabiliy;
    }

    public String getPocHome() {
        return pocHome;
    }

    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }

    public String getPresenceHome() {
        return presenceHome;
    }

    public void setPresenceHome(String presenceHome) {
        this.presenceHome = presenceHome;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public Timestamp getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Timestamp expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getSubscEmail() {
        return subscEmail;
    }

    public void setSubscEmail(String subscEmail) {
        this.subscEmail = subscEmail;
    }

    public int getClientPVmajorVer() {
        return clientPVmajorVer;
    }

    public void setClientPVmajorVer(int clientPVmajorVer) {
        this.clientPVmajorVer = clientPVmajorVer;
    }

    public int getVideoCallReceivePermission() {
        return videoCallReceivePermission;
    }

    public void setVideoCallReceivePermission(int videoCallReceivePermission) {
        this.videoCallReceivePermission = videoCallReceivePermission;
    }

    public int getVideoCallInitiatePermission() {
        return videoCallInitiatePermission;
    }

    public void setVideoCallInitiatePermission(int videoCallInitiatePermission) {
        this.videoCallInitiatePermission = videoCallInitiatePermission;
    }

    public int getVideoInCallPermission() {
        return videoInCallPermission;
    }

    public void setVideoInCallPermission(int videoInCallPermission) {
        this.videoInCallPermission = videoInCallPermission;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof KnCorpSubscriberDTO)) {
            return false;
        }

        KnCorpSubscriberDTO that = (KnCorpSubscriberDTO) o;

        if (!mdn.equals(that.mdn)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return mdn.hashCode();
    }

    public int getPamAccId() {
        return pamAccId;
    }

    public void setPamAccId(int pamAccId) {
        this.pamAccId = pamAccId;
    }

    public int compareTo(KnCorpSubscriberDTO subsDto) {
        String compMdn = subsDto.getMdn();
        if (mdn.equals(compMdn)) {
            return 0;
        }
        return -1;
    }

	public String getXdmsHome() {
		return xdmsHome;
	}


	public void setXdmsHome(String xdmsHome) {
		this.xdmsHome = xdmsHome;
	}

	public long getLastProfileUpdateTime() {
		return lastProfileUpdateTime;
	}


	public void setLastProfileUpdateTime(long lastProfileUpdateTime) {
		this.lastProfileUpdateTime = lastProfileUpdateTime;
	}

    public Integer getDispatchGrpmember() {
        return dispatchGrpmember;
    }

    public void setDispatchGrpmember(Integer dispatchGrpmember) {
        this.dispatchGrpmember = dispatchGrpmember;
    }

    public int getSubsType() {
        return subsType;
    }

    public void setSubsType(int subsType) {
        this.subsType = subsType;
    }

    public int getContact_type() {
        return contact_type;
    }

    public void setContact_type(int contact_type) {
        this.contact_type = contact_type;
    }

    public Map<String, Object> getCustomMap() {
        return customMap;
    }

    public void setCustomMap(Map<String, Object> customMap) {
        this.customMap = customMap;
    }

    public int getCallInitiatePermission() {
        return callInitiatePermission;
    }

    public void setCallInitiatePermission(int callInitiatePermission) {
        this.callInitiatePermission = callInitiatePermission;
    }

    public int getCallReceivePermission() {
        return callReceivePermission;
    }

    public void setCallReceivePermission(int callReceivePermission) {
        this.callReceivePermission = callReceivePermission;
    }

    public int getInCallPermission() {
        return inCallPermission;
    }

    public void setInCallPermission(int inCallPermission) {
        this.inCallPermission = inCallPermission;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(int dispatchType) {
        this.dispatchType = dispatchType;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public int getServiceAuthStatusAU() {
        return serviceAuthStatusAU;
    }

    public void setServiceAuthStatusAU(int serviceAuthStatusAU) {
        this.serviceAuthStatusAU = serviceAuthStatusAU;
    }

    public Integer getGroupModifyPerm() {
        return groupModifyPerm;
    }

    public void setGroupModifyPerm(Integer groupModifyPerm) {
        this.groupModifyPerm = groupModifyPerm;
    }

    public String getAliasMdn() {
        return aliasMdn;
    }

    public void setAliasMdn(String aliasMdn) {
        this.aliasMdn = aliasMdn;
    }

    public int getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    public Integer getCameraType() {
        return cameraType;
    }

    public void setCameraType(Integer cameraType) {
        this.cameraType = cameraType;
    }

    public int getIsAuthUser() {
        return isAuthUser;
    }

    public void setIsAuthUser(int isAuthUser) {
        this.isAuthUser = isAuthUser;
    }

    public String getCommandPackageCode() {
        return commandPackageCode;
    }

    public void setCommandPackageCode(String commandPackageCode) {
        this.commandPackageCode = commandPackageCode;
    }

    public int getGrpSublistId() {
        return grpSublistId;
    }

    public void setGrpSublistId(int grpSublistId) {
        this.grpSublistId = grpSublistId;
    }

    public String getSubsActiveFS2() {
		return subsActiveFS2;
	}


	public void setSubsActiveFS2(String subsActiveFS2) {
		this.subsActiveFS2 = subsActiveFS2;
	}


	public String getCorpAdminFS2() {
		return corpAdminFS2;
	}


	public void setCorpAdminFS2(String corpAdminFS2) {
		this.corpAdminFS2 = corpAdminFS2;
	}

    public String getCorpAdminFS1() {
        return corpAdminFS1;
    }

    public void setCorpAdminFS1(String corpAdminFS1) {
        this.corpAdminFS1 = corpAdminFS1;
    }

	public String getClientFs2() {
		return clientFs2;
	}


	public void setClientFs2(String clientFs2) {
		this.clientFs2 = clientFs2;
	}


	public String getSubscriberFs2() {
		return subscriberFs2;
	}


	public void setSubscriberFs2(String subscriberFs2) {
		this.subscriberFs2 = subscriberFs2;
	}


	public String getOpsFs2() {
		return opsFs2;
	}


	public void setOpsFs2(String opsFs2) {
		this.opsFs2 = opsFs2;
	}


	public String getXdmsFs2() {
		return xdmsFs2;
	}


	public void setXdmsFs2(String xdmsFs2) {
		this.xdmsFs2 = xdmsFs2;
	}


    public int getIsOSMAuthorize() {
        return isOSMAuthorize;
    }

    public void setIsOSMAuthorize(int isOSMAuthorize) {
        this.isOSMAuthorize = isOSMAuthorize;
    }

    public int getMcpttCompliance() { return mcpttCompliance; }

    public void setMcpttCompliance(int mcpttCompliance) { this.mcpttCompliance = mcpttCompliance; }

    public String getMcId() { return mcId; }

    public void setMcId(String mcId) { this.mcId = mcId; }

    public String getMcpttId() { return mcpttId; }

    public void setMcpttId(String mcpttId) { this.mcpttId = mcpttId; }

    public String getMcDataId() { return mcDataId; }

    public void setMcDataId(String mcDataId) { this.mcDataId = mcDataId; }

    public String getMcVideoId() { return mcVideoId; }

    public void setMcVideoId(String mcVideoId) { this.mcVideoId = mcVideoId; }

    public String getUserProfileId() { return userProfileId; }

    public void setUserProfileId(String userProfileId) { this.userProfileId = userProfileId; }

    public Integer getUserProfileIndex() { return userProfileIndex; }

    public void setUserProfileIndex(Integer userProfileIndex) { this.userProfileIndex = userProfileIndex; }

    public String getUserProfileFS2() {
		return userProfileFS2;
	}


	public void setUserProfileFS2(String userProfileFS2) {
		this.userProfileFS2 = userProfileFS2;
	}


	public int getClientPVminorVer() {
		return clientPVminorVer;
	}


	public void setClientPVminorVer(int clientPVminorVer) {
		this.clientPVminorVer = clientPVminorVer;
	}

	 public Long getLastActivationTime() {
	        return lastActivationTime;
	    }

	    public void setLastActivationTime(Long lastActivationTime) {
	        this.lastActivationTime = lastActivationTime;
	    }
	    
	    public String getIMEI() {
	        return IMEI;
	    }

	    public void setIMEI(String IMEI) {
	        this.IMEI = IMEI;
	    }
	    
	    public String getClientPassword() {
	        return clientPassword;
	    }

	   public void setClientPassword(String clientPassword) {
	        if (clientPassword != null) {
	            clientPassword = clientPassword.trim();
	            if (clientPassword.equals("")) {
	                clientPassword = null;
	            }
	        }
	        this.clientPassword = clientPassword;
	    }
	   
	   public int getVocoderId() {
	       return vocoderId;
	   }

	   public void setVocoderId(int vocoderId) {
	       this.vocoderId = vocoderId;
	   }
	   
	   public int getSwType() {
	       return swType;
	   }

	   public void setSwType(int swType) {
	       this.swType = swType;
	   }
	   
	   public int getPlatformType() {
	        return platformType;
	    }

    public void setPlatformType(int platformType) {
        this.platformType = platformType;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }
	    
	    public int getDynamicQosFlag() {
	        return dynamicQosFlag;
	    }

	    public void setDynamicQosFlag(int dynamicQosFlag) {
	        this.dynamicQosFlag = dynamicQosFlag;
	    }
	    
	    public String getDerivedKey() {
	        return derivedKey;
	    }

	    public void setDerivedKey(String derivedKey) {
	        this.derivedKey = derivedKey;
	    }
	    
	    public Integer getServiceStatusOp() {
	        return serviceStatusOp;
	    }

	    public void setServiceStatusOp(Integer serviceStatusOp) {
	        this.serviceStatusOp = serviceStatusOp;
	    }

    public boolean isUpmCall() {
        return upmCall;
    }

    public void setUpmCall(boolean upmCall) {
        this.upmCall = upmCall;
    }

    public Integer getIsDefaultProfile() { return isDefaultProfile; }

    public void setIsDefaultProfile(Integer isDefaultProfile) { this.isDefaultProfile = isDefaultProfile; }

    public Integer getCommonContact() {
        return commonContact;
    }

    public void setCommonContact(Integer commonContact) {
        this.commonContact = commonContact;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getBillingMDN() {
        return billingMDN;
    }

    public void setBillingMDN(String billingMDN) {
        this.billingMDN = billingMDN;
    }

    public String getSubsActiveFS1() {
        return subsActiveFS1;
    }

    public void setSubsActiveFS1(String subsActiveFS1) {
        this.subsActiveFS1 = subsActiveFS1;
    }

    public Map<String, Object> getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(Map<String, Object> packageInfo) {
        this.packageInfo = packageInfo;
    }

    public String getExtGatewayId() {
        return extGatewayId;
    }

    public void setExtGatewayId(String extGatewayId) {
        this.extGatewayId = extGatewayId;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }
    
    public List<String> getMdnList() {
        return mdnList;
    }

    public void setMdnList(List<String> mdnList) {
        this.mdnList = mdnList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append("CorpId - ").append(corpId)
                .append(", MDN - ").append(KnGDPRTemplate.mdn(mdn))
                .append(", grpSublistId - ").append(grpSublistId)
                .append(", Name - ").append(KnGDPRTemplate.name(name))
                .append(", clientType - ").append(clientType)
                .append(", isOSMAuthorize - ").append(isOSMAuthorize)
                .append(", contact_type - ").append(contact_type)
                .append(", mcpttCompliance - ").append(mcpttCompliance)
                .append(", SubscriptionType - ").append(subscriptionType)
                .append(", ServiceAuthStatus - ").append(serviceAuthStatus)
                .append(", IsExternal - ").append(externalContact)
                .append(", MaxContactLimitFlag - ").append(maxContactLimitFlag)
                .append(", GroupETag - ").append(groupETag)
                .append(", etag - ").append(etag)
                .append(", contactCount - ").append(contactCount)
                .append(", clientType - ").append(clientType)
                .append(", parentId - ").append(parentId)
                .append(", activationCode - ").append(activationCode)
                .append(", activationTimestamp - ").append(activationTimestamp)
                .append(", oldName - ").append(KnGDPRTemplate.name(oldName))
                .append(", notfnCapabiliy - ").append(notfnCapabiliy)
                .append(", clientPVmajorVer - ").append(clientPVmajorVer)
                .append(", clientPVminorVer - ").append(clientPVminorVer)
                .append(", userProfileId - ").append(userProfileId)
                .append(", userProfileIndex - ").append(userProfileIndex)
                .append(", userProfileFS2 - ").append(userProfileFS2)
                .append(", lastActivationTime - ").append(lastActivationTime)
        		.append(", IMEI - ").append(IMEI)
        		.append(", clientPassword - ").append(clientPassword)
        		.append(", vocoderId - ").append(vocoderId)
        		.append(", swType - ").append(swType)
        		.append(", platformType - ").append(platformType)
        		.append(", dynamicQosFlag - ").append(dynamicQosFlag)
        		.append(", derivedKey - ").append(derivedKey)
                .append(", userAgent - ").append(userAgent)
                .append(", upmCall - ").append(upmCall)
                .append(", cameraType - ").append(cameraType)
                .append(", oldActiveFs - ").append(oldActiveFs)
        		.append(", serviceStatusOp - ").append(serviceStatusOp)
                .append(", isDefaultProfile - ").append(isDefaultProfile)
                .append(", commonContact - ").append(commonContact)
                .append(", mcpttId - ").append(mcpttId)
                .append(", billingMDN - ").append(billingMDN)
                .append(", subsActiveFS1 - ").append(subsActiveFS1)
                .append(", subsActiveFS2 - ").append(subsActiveFS2)
                .append(", deviceId - ").append(deviceId)
                .append(", packageInfo - ").append(packageInfo)
                .append(", corpAdminFS1 - ").append(corpAdminFS1)
                .append(", corpAdminFS2 - ").append(corpAdminFS2)
                .append(", extGatewayId - ").append(extGatewayId)
                .append(", clusterId - ").append(clusterId)
                .append("pocHome-").append(pocHome)
                .append(", videoCallInitiatePermission - ").append(videoCallInitiatePermission)
                .append(", videoCallReceivePermission - ").append(videoCallReceivePermission)
                .append(", videoInCallPermission - ").append(videoInCallPermission);             
        return sb.toString();
    }
}
