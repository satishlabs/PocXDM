/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.request;

public class KnXDMGetAuthorizedUserListDTO extends KnXDMSubsInfoDTO {

    private static final long serialVersionUID = -3055931957204797737L;
    private String targetUser;
    private String mcptt_id;

    public String getMcptt_id() {
		return mcptt_id;
	}

	public void setMcptt_id(String mcptt_id) {
		this.mcptt_id = mcptt_id;
	}

	public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }


}
