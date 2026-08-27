/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;


import com.kodiak.common.commdto.response.*;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;


/**
 * ************************************************************************
 * <p/>
 * File name:  KnMCSXCAPRespDTO.java
 * Subsystem:  XDM
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Shashank Tewari       OCT 27,2020     11.0
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
public class KnMCSXCAPRespDTO {

    private KnSubsProfileDTO subsProfileDTO;

    private KnXDMMcPttUEConfigRespDTO pttUEConfigRespDTO;

    private KnXDMMcPttUserProfileRespDTO pttUserProfileRespDTO;

    private KnXDMMcPttServiceConfigRespDTO pttServiceConfigRespDTO;
	
	private KnXDMMCDataUEConfigRespDTO dataUEConfigRespDTO;

    private KnXDMMCDATAUserProfileRespDTO dataUserProfileRespDTO;

    private KnXDMMCDATAServiceConfigRespDTO dataServiceConfigRespDTO;

    private KnXDMMCVideoUEConfigRespDTO videoUEConfigRespDTO;

    private KnXDMMCVideoUserProfileRespDTO videoUserProfileRespDTO;

    private KnXDMMCVideoServiceConfigRespDTO videoServiceConfigRespDTO;

    private KnXDMMCSGroupDocRespDTO groupDocRespDTO;

    private KnXDMMCUserDirRespDTO userDirRespDTO;


    public KnSubsProfileDTO getSubsProfileDTO() {
        return subsProfileDTO;
    }

    public void setSubsProfileDTO(KnSubsProfileDTO subsProfileDTO) {
        this.subsProfileDTO = subsProfileDTO;
    }

    public KnXDMMcPttUEConfigRespDTO getPttUEConfigRespDTO() {
        return pttUEConfigRespDTO;
    }

    public void setPttUEConfigRespDTO(KnXDMMcPttUEConfigRespDTO pttUEConfigRespDTO) {
        this.pttUEConfigRespDTO = pttUEConfigRespDTO;
    }

    public KnXDMMcPttUserProfileRespDTO getPttUserProfileRespDTO() {
        return pttUserProfileRespDTO;
    }

    public void setPttUserProfileRespDTO(KnXDMMcPttUserProfileRespDTO pttUserProfileRespDTO) {
        this.pttUserProfileRespDTO = pttUserProfileRespDTO;
    }

    public KnXDMMcPttServiceConfigRespDTO getPttServiceConfigRespDTO() {
        return pttServiceConfigRespDTO;
    }

    public void setPttServiceConfigRespDTO(KnXDMMcPttServiceConfigRespDTO pttServiceConfigRespDTO) {
        this.pttServiceConfigRespDTO = pttServiceConfigRespDTO;
    }

    public KnXDMMCDataUEConfigRespDTO getDataUEConfigRespDTO() {
        return dataUEConfigRespDTO;
    }

    public void setDataUEConfigRespDTO(KnXDMMCDataUEConfigRespDTO dataUEConfigRespDTO) {
        this.dataUEConfigRespDTO = dataUEConfigRespDTO;
    }

    public KnXDMMCDATAUserProfileRespDTO getDataUserProfileRespDTO() {
        return dataUserProfileRespDTO;
    }

    public void setDataUserProfileRespDTO(KnXDMMCDATAUserProfileRespDTO dataUserProfileRespDTO) {
        this.dataUserProfileRespDTO = dataUserProfileRespDTO;
    }

    public KnXDMMCDATAServiceConfigRespDTO getDataServiceConfigRespDTO() {
        return dataServiceConfigRespDTO;
    }

    public void setDataServiceConfigRespDTO(KnXDMMCDATAServiceConfigRespDTO dataServiceConfigRespDTO) {
        this.dataServiceConfigRespDTO = dataServiceConfigRespDTO;
    }

    public KnXDMMCVideoUEConfigRespDTO getVideoUEConfigRespDTO() {
        return videoUEConfigRespDTO;
    }

    public void setVideoUEConfigRespDTO(KnXDMMCVideoUEConfigRespDTO videoUEConfigRespDTO) {
        this.videoUEConfigRespDTO = videoUEConfigRespDTO;
    }

    public KnXDMMCVideoUserProfileRespDTO getVideoUserProfileRespDTO() {
        return videoUserProfileRespDTO;
    }

    public void setVideoUserProfileRespDTO(KnXDMMCVideoUserProfileRespDTO videoUserProfileRespDTO) {
        this.videoUserProfileRespDTO = videoUserProfileRespDTO;
    }

    public KnXDMMCVideoServiceConfigRespDTO getVideoServiceConfigRespDTO() {
        return videoServiceConfigRespDTO;
    }

    public void setVideoServiceConfigRespDTO(KnXDMMCVideoServiceConfigRespDTO videoServiceConfigRespDTO) {
        this.videoServiceConfigRespDTO = videoServiceConfigRespDTO;
    }

    public KnXDMMCSGroupDocRespDTO getGroupDocRespDTO() {
        return groupDocRespDTO;
    }

    public void setGroupDocRespDTO(KnXDMMCSGroupDocRespDTO groupDocRespDTO) {
        this.groupDocRespDTO = groupDocRespDTO;
    }

    public KnXDMMCUserDirRespDTO getUserDirRespDTO() {
        return userDirRespDTO;
    }

    public void setUserDirRespDTO(KnXDMMCUserDirRespDTO userDirRespDTO) {
        this.userDirRespDTO = userDirRespDTO;
    }

    @Override
    public String toString() {

        StringBuffer strBuffer = new StringBuffer(800);
        return  strBuffer.append(super.toString()).append(
                "KnMCSXCAPRespDTO{" +
                        "pttUEConfigRespDTO=" + pttUEConfigRespDTO +
                        ", pttUserProfileRespDTO=" + pttUserProfileRespDTO +
                        ", pttServiceConfigRespDTO=" + pttServiceConfigRespDTO +
                        ", dataUEConfigRespDTO=" + dataUEConfigRespDTO +
                        ", dataUserProfileRespDTO=" + dataUserProfileRespDTO +
                        ", dataServiceConfigRespDTO=" + dataServiceConfigRespDTO +
                        ", videoUEConfigRespDTO=" + videoUEConfigRespDTO +
                        ", videoUserProfileRespDTO=" + videoUserProfileRespDTO +
                        ", videoServiceConfigRespDTO=" + videoServiceConfigRespDTO +
                        ", groupDocRespDTO=" + groupDocRespDTO +
                        ", userDirRespDTO=" + userDirRespDTO +
                        '}').toString();
    }

}
