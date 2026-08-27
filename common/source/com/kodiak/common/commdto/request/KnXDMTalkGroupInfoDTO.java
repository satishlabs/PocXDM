/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

import java.io.Serializable;

public class KnXDMTalkGroupInfoDTO implements Serializable {

	private static final long serialVersionUID = 2765996681123741538L;

	private Integer groupId;

    private String groupName;

	private Integer priority;

	private Integer channel;

	private Integer zone;

	private String csvZone;
	private String csvChannel;

	private Integer corpId;

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getZone() {
		return zone;
	}

	public void setZone(Integer zone) {
		this.zone = zone;
	}

	public String getCsvZone() {
		return csvZone;
	}

	public void setCsvZone(String csvZone) {
		this.csvZone = csvZone;
	}

	public String getCsvChannel() {
		return csvChannel;
	}

	public void setCsvChannel(String csvChannel) {
		this.csvChannel = csvChannel;
	}

	public Integer getCorpId() { return corpId;	}

	public void setCorpId(Integer corpId) { this.corpId = corpId; }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
        result = prime * result + ((priority == null) ? 0 : priority.hashCode());
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KnXDMTalkGroupInfoDTO other = (KnXDMTalkGroupInfoDTO) obj;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "KnXDMTalkGroupInfoDTO{" +
				"groupId=" + groupId +
				", groupName='" + groupName + '\'' +
				", priority=" + priority +
				", channel=" + channel +
				", zone=" + zone +
				", csvZone=" + csvZone +
				", csvChannel=" + csvChannel +
				", corpId=" + corpId +
				'}';
	}
}
