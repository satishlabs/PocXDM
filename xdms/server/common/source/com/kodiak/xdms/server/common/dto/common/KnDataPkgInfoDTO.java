/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

public class KnDataPkgInfoDTO {
	private int dataPkgId;
	private String dataPkgName;
	private int locHistory;
	private int recording;
	private int msgData;

	public int getDataPkgId() {
		return dataPkgId;
	}

	public void setDataPkgId(int dataPkgId) {
		this.dataPkgId = dataPkgId;
	}

	public String getDataPkgName() {
		return dataPkgName;
	}

	public void setDataPkgName(String dataPkgName) {
		this.dataPkgName = dataPkgName;
	}

	public int getLocHistory() {
		return locHistory;
	}

	public void setLocHistory(int locHistory) {
		this.locHistory = locHistory;
	}

	public int getRecording() {
		return recording;
	}

	public void setRecording(int recording) {
		this.recording = recording;
	}

	public int getMsgData() {
		return msgData;
	}

	public void setMsgData(int msgData) {
		this.msgData = msgData;
	}

	@Override
	public String toString() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append(" dataPkgId - ").append(dataPkgId).append(", dataPkgName - ").append(dataPkgName)
				.append(", locHistory - ").append(locHistory).append(", recording - ").append(recording)
				.append(", msgData - ").append(msgData);
		return strBuffer.toString();
	}

}
