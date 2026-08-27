/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.dto.IIdentifier;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMMCSGroupInfoDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * Shashank Tewari        July 01, 2008           9.1
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
public class KnXDMMCSGroupInfoDTO implements IIdentifier {

    private static final long serialVersionUID = 7526471155622676133L;

    private KnXDMMemberDTO gpID;
    private Collection<KnXDMMemberDTO> gmsAppServer;
    private Collection<KnXDMMemberDTO> idmsTokenEndPoints;
    private KnXDMMemberDTO groupKmsuri;
    private KnXDMGroupDTO groupDTO;
    private KnXDMTalkGroupInfoDTO talkGroupInfoDTO;
    private Integer isBroadcasterExtM;
    private Integer isExtCorpGroupExtM;
    private Integer isTgssGroupExtM;

    public Integer getIsExtCorpGroupExtM() {
        return isExtCorpGroupExtM;
    }

    public void setIsExtCorpGroupExtM(Integer isExtCorpGroupExtM) {
        this.isExtCorpGroupExtM = isExtCorpGroupExtM;
    }

    public KnXDMMemberDTO getGpID() {
        return gpID;
    }

    public void setGpID(KnXDMMemberDTO gpID) {
        this.gpID = gpID;
    }

    public Collection<KnXDMMemberDTO> getGmsAppServer() {return gmsAppServer; }

    public void setGmsAppServer(Collection<KnXDMMemberDTO> gmsAppServer) {
        this.gmsAppServer = gmsAppServer;
    }

    public Collection<KnXDMMemberDTO> getIdmsTokenEndPoints() {return idmsTokenEndPoints; }

    public void setIdmsTokenEndPoints(Collection<KnXDMMemberDTO> idmsTokenEndPoints) {
        this.idmsTokenEndPoints = idmsTokenEndPoints;
    }

    public KnXDMMemberDTO getGroupKmsuri() {
        return groupKmsuri;
    }

    public void setGroupKmsuri(KnXDMMemberDTO groupKmsuri) {
        this.groupKmsuri = groupKmsuri;
    }

    public String getObjectId() {
        return null;
    }

    public KnXDMGroupDTO getGroupDTO() {
        return groupDTO;
    }

    public void setGroupDTO(KnXDMGroupDTO groupDTO) {
        this.groupDTO = groupDTO;
    }

    public KnXDMTalkGroupInfoDTO getTalkGroupInfoDTO() {
        return talkGroupInfoDTO;
    }

    public void setTalkGroupInfoDTO(KnXDMTalkGroupInfoDTO talkGroupInfoDTO) {
        this.talkGroupInfoDTO = talkGroupInfoDTO;
    }

    public Integer getIsBroadcasterExtM() {
        return isBroadcasterExtM;
    }

    public void setIsBroadcasterExtM(Integer isBroadcasterExtM) {
        this.isBroadcasterExtM = isBroadcasterExtM;
    }

    public Integer getIsTgssGroupExtM() {
        return isTgssGroupExtM;
    }

    public void setIsTgssGroupExtM(Integer isTgssGroupExtM) {
        this.isTgssGroupExtM = isTgssGroupExtM;
    }

    @Override
    public String toString() {
        return "KnXDMMCSGroupInfoDTO{" +
                "gpID=" + gpID +
                ", gmsAppServer=" + gmsAppServer +
                ", idmsTokenEndPoints=" + idmsTokenEndPoints +
                ", groupKmsuri=" + groupKmsuri +
                ", groupDTO=" + groupDTO +
                ", talkGroupInfoDTO=" + talkGroupInfoDTO +
                ", isBroadcasterExtM=" + isBroadcasterExtM +
                ", isExtCorpGroupExtM=" + isExtCorpGroupExtM +
                ", isTgssGroupExtM=" + isTgssGroupExtM +
                '}';
    }
}
