/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import java.io.Serializable;

public class KnXDMCorpOSMGroupList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5098795445559565756L;

    private String OSMListId;
    private String groupIds;

    public KnXDMCorpOSMGroupList(){}

    public KnXDMCorpOSMGroupList(String OSMListId, String groupIds) {
        this.OSMListId = OSMListId;
        this.groupIds = groupIds;
    }

    public String getOSMListId() {
        return OSMListId;
    }

    public String getGroupIds() {
        return groupIds;
    }

    @Override
    public String toString() {
        return "KnXDMCorpOSMGroupList{" +
                "OSMListId='" + OSMListId + '\'' +
                ", groupIds='" + groupIds + '\'' +
                '}';
    }
}
