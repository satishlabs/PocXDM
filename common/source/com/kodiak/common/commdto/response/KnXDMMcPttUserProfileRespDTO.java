/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.resources.KnGDPRTemplate;

import java.util.Collection;
import java.util.List;

public class KnXDMMcPttUserProfileRespDTO extends KnXDMRespDTO {
	private static final long serialVersionUID = 7526471155622676158L;

	private String mdn;
	private String mcsDomainName;
	private String mcPttUEConfigname;
	private Integer maxSimDynSession;
	private Integer maxSimDedSession;
	private Integer ipPrefOnCellIntf;
	//private String eTag;


	private String responseCode;
	private int responseStatus;
	private String responseMessage;
	private Collection responseDetails;
	private String objectId;
	private String destPttServerId;
	private String destQueueName;
	private String transactionId;

	//UserProfile specific
	private String groupUri;
	private String contactUri;
	private String xuiUri;
	private String name;
	private boolean status;
	private String profileName;
	private String userAlias;
	private String activeFS;
	private Boolean campModeCap;
	private String aliasMdn;
	private String mcPttUserId;
	private String mcPttUserIdDispName;
	private String missionCriticalOrganization;
	private String onNetworkMCPTTGroupInfoUri;
	private String groupName;
	private String onNetworkMcPttGroupInfoDispName;
	private List<KnXDMMdnInfoDTO> listOfXdmMdnInfoDTOs;
	List<KnEmergencyMdnDTOForMCSXCAP> knEmergencyMdnDTOsList;
	private Integer allowEmergencyGroupCall;
	private Integer allowEmergencyPrivateCall;
	private Integer allowActivateEmergencyAlert;
	private Integer allowCancelEmergencyAlert;
	private Integer allowCancelGroupEmergency;
	private Integer allowCancelPrivateEmergencyCall;
	private Integer emergTermAlertIndExtM;
	private String oldActiveFS;
	private int corpId;
	private Integer userProfileIndex;
    private String pttSettingUri;
    private long pttEtag;

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	public String getMcsDomainName() {
		return mcsDomainName;
	}

	public void setMcsDomainName(String mcsDomainName) {
		this.mcsDomainName = mcsDomainName;
	}

	public String getMcPttUEConfigname() {
		return mcPttUEConfigname;
	}

	public void setMcPttUEConfigname(String mcPttUEConfigname) {
		this.mcPttUEConfigname = mcPttUEConfigname;
	}

	public Integer getMaxSimDynSession() {
		return maxSimDynSession;
	}

	public void setMaxSimDynSession(Integer maxSimDynSession) {
		this.maxSimDynSession = maxSimDynSession;
	}

	public Integer getMaxSimDedSession() {
		return maxSimDedSession;
	}

	public void setMaxSimDedSession(Integer maxSimDedSession) {
		this.maxSimDedSession = maxSimDedSession;
	}

	public Integer getIpPrefOnCellIntf() {
		return ipPrefOnCellIntf;
	}

	public void setIpPrefOnCellIntf(Integer ipPrefOnCellIntf) {
		this.ipPrefOnCellIntf = ipPrefOnCellIntf;
	}

	/*public String geteTag() {
		return eTag;
	}

	public void seteTag(String eTag) {
		this.eTag = eTag;
	}*/

	@Override
	public String getResponseCode() {
		return responseCode;
	}

	@Override
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	@Override
	public int getResponseStatus() {
		return responseStatus;
	}

	@Override
	public void setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
	}

	@Override
	public String getResponseMessage() {
		return responseMessage;
	}

	@Override
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	@Override
	public Collection getResponseDetails() {
		return responseDetails;
	}

	@Override
	public void setResponseDetails(Collection responseDetails) {
		this.responseDetails = responseDetails;
	}

	@Override
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	@Override
	public String getDestPttServerId() {
		return destPttServerId;
	}

	@Override
	public void setDestPttServerId(String destPttServerId) {
		this.destPttServerId = destPttServerId;
	}

	@Override
	public String getDestQueueName() {
		return destQueueName;
	}

	@Override
	public void setDestQueueName(String destQueueName) {
		this.destQueueName = destQueueName;
	}

	@Override
	public String getTransactionId() {
		return transactionId;
	}

	@Override
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}


	public String getGroupUri() {
		return groupUri;
	}

	public void setGroupUri(String groupUri) {
		this.groupUri = groupUri;
	}

	public String getContactUri() {
		return contactUri;
	}

	public void setContactUri(String contactUri) {
		this.contactUri = contactUri;
	}

	public String getXuiUri() {
		return xuiUri;
	}

	public void setXuiUri(String xuiUri) {
		this.xuiUri = xuiUri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getUserAlias() {
		return userAlias;
	}

	public void setUserAlias(String userAlias) {
		this.userAlias = userAlias;
	}

	public String getActiveFS() {
		return activeFS;
	}

	public void setActiveFS(String activeFS) {
		this.activeFS = activeFS;
	}

	public Boolean getCampModeCap() {return campModeCap; }

	public void setCampModeCap(Boolean campModeCap) {this.campModeCap = campModeCap; }

	public String getAliasMdn() {
		return aliasMdn;
	}

	public void setAliasMdn(String aliasMdn) {
		this.aliasMdn = aliasMdn;
	}

	public String getMcPttUserId() {
		return mcPttUserId;
	}

	public void setMcPttUserId(String mcPttUserId) {
		this.mcPttUserId = mcPttUserId;
	}

	public String getMcPttUserIdDispName() {
		return mcPttUserIdDispName;
	}

	public void setMcPttUserIdDispName(String mcPttUserIdDispName) {
		this.mcPttUserIdDispName = mcPttUserIdDispName;
	}

	public String getMissionCriticalOrganization() {
		return missionCriticalOrganization;
	}

	public void setMissionCriticalOrganization(String missionCriticalOrganization) {
		this.missionCriticalOrganization = missionCriticalOrganization;
	}

	public String getOnNetworkMCPTTGroupInfoUri() {
		return onNetworkMCPTTGroupInfoUri;
	}

	public void setOnNetworkMCPTTGroupInfoUri(String onNetworkMCPTTGroupInfoUri) {
		this.onNetworkMCPTTGroupInfoUri = onNetworkMCPTTGroupInfoUri;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getOnNetworkMcPttGroupInfoDispName() {
		return onNetworkMcPttGroupInfoDispName;
	}

	public void setOnNetworkMcPttGroupInfoDispName(String onNetworkMcPttGroupInfoDispName) {
		this.onNetworkMcPttGroupInfoDispName = onNetworkMcPttGroupInfoDispName;
	}

	public List<KnXDMMdnInfoDTO> getListOfXdmMdnInfoDTOs() {
		return listOfXdmMdnInfoDTOs;
	}

	public void setListOfXdmMdnInfoDTOs(List<KnXDMMdnInfoDTO> listOfXdmMdnInfoDTOs) {
		this.listOfXdmMdnInfoDTOs = listOfXdmMdnInfoDTOs;
	}

	public List<KnEmergencyMdnDTOForMCSXCAP> getKnEmergencyMdnDTOsList() {
		return knEmergencyMdnDTOsList;
	}

	public void setKnEmergencyMdnDTOsList(List<KnEmergencyMdnDTOForMCSXCAP> knEmergencyMdnDTOsList) {
		this.knEmergencyMdnDTOsList = knEmergencyMdnDTOsList;
	}
	
	 public Integer getAllowEmergencyGroupCall() {
			return allowEmergencyGroupCall;
		}

		public void setAllowEmergencyGroupCall(Integer allowEmergencyGroupCall) {
			this.allowEmergencyGroupCall = allowEmergencyGroupCall;
		}

		public Integer getAllowEmergencyPrivateCall() {
			return allowEmergencyPrivateCall;
		}

		public void setAllowEmergencyPrivateCall(Integer allowEmergencyPrivateCall) {
			this.allowEmergencyPrivateCall = allowEmergencyPrivateCall;
		}

		public Integer getAllowActivateEmergencyAlert() {
			return allowActivateEmergencyAlert;
		}

		public void setAllowActivateEmergencyAlert(Integer allowActivateEmergencyAlert) {
			this.allowActivateEmergencyAlert = allowActivateEmergencyAlert;
		}

		public Integer getAllowCancelEmergencyAlert() {
			return allowCancelEmergencyAlert;
		}

		public void setAllowCancelEmergencyAlert(Integer allowCancelEmergencyAlert) {
			this.allowCancelEmergencyAlert = allowCancelEmergencyAlert;
		}

		public Integer getAllowCancelGroupEmergency() {
			return allowCancelGroupEmergency;
		}

		public void setAllowCancelGroupEmergency(Integer allowCancelGroupEmergency) {
			this.allowCancelGroupEmergency = allowCancelGroupEmergency;
		}

		public Integer getAllowCancelPrivateEmergencyCall() {
			return allowCancelPrivateEmergencyCall;
		}

		public void setAllowCancelPrivateEmergencyCall(Integer allowCancelPrivateEmergencyCall) {
			this.allowCancelPrivateEmergencyCall = allowCancelPrivateEmergencyCall;
		}

	public Integer getEmergTermAlertIndExtM() { return emergTermAlertIndExtM; }

	public void setEmergTermAlertIndExtM(Integer emergTermAlertIndExtM) { this.emergTermAlertIndExtM = emergTermAlertIndExtM; }

	public String getOldActiveFS() {
		return oldActiveFS;
	}

	public void setOldActiveFS(String oldActiveFS) {
		this.oldActiveFS = oldActiveFS;
	}

	public int getCorpId() {
		return corpId;
	}

	public void setCorpId(int corpId) {
		this.corpId = corpId;
	}

	public Integer getUserProfileIndex() { return userProfileIndex;	}

	public void setUserProfileIndex(Integer userProfileIndex) { this.userProfileIndex = userProfileIndex; }

    public String getPttSettingUri() {
        return pttSettingUri;
    }

    public void setPttSettingUri(String pttSettingUri) {
        this.pttSettingUri = pttSettingUri;
    }

    public long getPttEtag() {
        return pttEtag;
    }

    public void setPttEtag(long pttEtag) {
        this.pttEtag = pttEtag;
    }

    @Override
	public String toString() {
		return "KnXDMMcPttUserProfileRespDTO{" +
				"mdn='" + KnGDPRTemplate.mdn(mdn) + '\'' +
				", mcsDomainName='" + mcsDomainName + '\'' +
				", mcPttUEConfigname='" + mcPttUEConfigname + '\'' +
				", maxSimDynSession=" + maxSimDynSession +
				", maxSimDedSession=" + maxSimDedSession +
				", ipPrefOnCellIntf=" + ipPrefOnCellIntf +
				", activeFS=" + activeFS +
				", campModeCap=" + campModeCap +
				", aliasMdn=" + KnGDPRTemplate.mdn(aliasMdn) +
				//", eTag='" + eTag + '\'' +
				", responseCode='" + responseCode + '\'' +
				", responseStatus=" + responseStatus +
				", responseMessage='" + responseMessage + '\'' +
				", responseDetails=" + responseDetails +
				", objectId='" + objectId + '\'' +
				", destPttServerId='" + destPttServerId + '\'' +
				", destQueueName='" + destQueueName + '\'' +
				", transactionId='" + transactionId + '\'' +
				", allowEmergencyGroupCall='" + allowEmergencyGroupCall + '\'' +
				", allowEmergencyPrivateCall='" + allowEmergencyPrivateCall + '\'' +
				", allowActivateEmergencyAlert='" + allowActivateEmergencyAlert + '\'' +
				", allowCancelEmergencyAlert='" + allowCancelEmergencyAlert + '\'' +
				", allowCancelGroupEmergency='" + allowCancelGroupEmergency + '\'' +
				", allowCancelPrivateEmergencyCall='" + allowCancelPrivateEmergencyCall + '\'' +
				", emergencyMdnDTOsList='" + knEmergencyMdnDTOsList + '\'' +
				", emergTermAlertIndExtM='" + emergTermAlertIndExtM + '\'' +
				", oldActiveFS='" + oldActiveFS + '\'' +
				", corpId='" + corpId + '\'' +
				", userProfileIndex='" + userProfileIndex + '\'' +

				'}';
	}
}
