/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.dto.common;

public class KnSubscriberUserProfileDTO extends KnCorpUserProfileDTO {
	private Integer isDefaultProfile;
	
    public Integer getIsDefaultProfile() {
		return isDefaultProfile;
	}


	public void setIsDefaultProfile(Integer isDefaultProfile) {
		this.isDefaultProfile = isDefaultProfile;
	}

    @Override
    public String toString() {
        return "KnCorpUserProfileDTO{" +
                ", isDefaultProfile=" + isDefaultProfile +
                '}';
    }
}
