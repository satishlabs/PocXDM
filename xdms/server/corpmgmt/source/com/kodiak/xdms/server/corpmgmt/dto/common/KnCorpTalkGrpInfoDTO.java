/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

public class KnCorpTalkGrpInfoDTO {
	int groupId;
	int priority;
    String groupName;
	int channel;

	public KnCorpTalkGrpInfoDTO() {

	}

	public KnCorpTalkGrpInfoDTO(int groupId, int priority) {
		this.groupId = groupId;
		this.priority = priority;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KnCorpTalkGrpInfoDTO [groupId=").append(groupId).append(", groupName=").append(groupName).append(", priority=").append(priority).append(", channel=").append(channel).append("]");
		return builder.toString();
	}

}
