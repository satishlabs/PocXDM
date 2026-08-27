/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.clientdat;

/**
 * Created by Deepak on 11/6/14.
 */
public class KnOPDispatchDirChgDTO extends KnOPDirChgDTO {

    private boolean activeFSChanged;
    private long activeFS1;
    private int corpId;
    private String activeFS2;
    private Long lastProfileUpdateTime;

    //Only to get the oldactiveFs in notify
    private String oldActiveFS;

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }


    public boolean isActiveFSChanged() {
        return activeFSChanged;
    }

    public void setActiveFSChanged(boolean activeFSChanged) {
        this.activeFSChanged = activeFSChanged;
    }


    public long getActiveFS1() {
        return activeFS1;
    }

    public void setActiveFS1(long activeFS1) {
        this.activeFS1 = activeFS1;
    }

    public String getActiveFS2() {
		return activeFS2;
	}

	public void setActiveFS2(String activeFS2) {
		this.activeFS2 = activeFS2;
	}

    public Long getLastProfileUpdateTime() {
        return lastProfileUpdateTime;
    }

    public void setLastProfileUpdateTime(Long lastProfileUpdateTime) {
        this.lastProfileUpdateTime = lastProfileUpdateTime;
    }

    public String getOldActiveFS() {
        return oldActiveFS;
    }

    public void setOldActiveFS(String oldActiveFS) {
        this.oldActiveFS = oldActiveFS;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KnOPDispatchDirChgDTO{");
        sb.append(super.toString());
        sb.append("activeFSChanged=").append(activeFSChanged);
        sb.append("corpId=").append(corpId);
        sb.append("activeFS=").append(activeFS1);
        sb.append("activeFS2=").append(activeFS2);
        sb.append("lastProfileUpdateTime=").append(lastProfileUpdateTime);
        sb.append("oldActiveFS=").append(oldActiveFS);
        sb.append('}');
        return sb.toString();
    }
}
