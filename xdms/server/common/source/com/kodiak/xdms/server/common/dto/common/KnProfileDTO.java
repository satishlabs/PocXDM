/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.xdms.server.common.dto.intf.IGenDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;
import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.common.util.KnProfileCache;

import java.util.Calendar;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnProfileDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 11, 2011           7.0
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
public class KnProfileDTO implements IGenDTO {

    private static final long serialVersionUID = 7526471155622676178L;

    //stores the dto status
    protected int dtoStatus = -1;
    //stores the object Id
    private String objectId;
    //stores the errorObject
    protected KnXDMError errorObject;
    //stores CorpId
    private int corpId;
    //stores the userId of the user
    private String mdn; //todo not relevant
    //stores the user network name
    private String networkName; //todo should be "name"
    //stores the pocHome id
    private String pocHome; //todo not relevant
    //stores the presence Home id
    private String presenceHome; //todo not relevant
    //stores the XDMS Home id
    private String xdmsHome;
    //stores the serviceauthstatus
    private int serviceAuthStatus;
    //stores the creationTimestamp value of the profile
    private long creationTimestamp;
    //stores the profile last updated in DB
    private long profileLastUpdated;

    private String protocolVersion;

    private int serviceAuthStatusOP;

    private int serviceAuthStatusAU;

    public KnProfileDTO() {
        creationTimestamp = Calendar.getInstance().getTimeInMillis();
    }


    /**
     * This method will return true if the profile is upto date with DB within the
     * refresh time limit.
     *
     * @return boolean value true / false
     */
    public boolean isActive() {
        long currentTimestamp = Calendar.getInstance().getTimeInMillis();
        return (currentTimestamp - creationTimestamp) <= KnProfileCache.getExpiryTime();
    }

    public int getDTOStatus() {
        return dtoStatus;
    }

    public void setDTOStatus(int dtoStatus) {
        this.dtoStatus = dtoStatus;
    }

    public KnXDMError getErrorObject() {
        return errorObject;
    }

    public void setErrorObject(KnXDMError errorObject) {
        this.errorObject = errorObject;
    }

    public int getCorpId() {
        return corpId;
    }

    public void setCorpId(int corpId) {
        this.corpId = corpId;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getPocHome() {
        return pocHome;
    }

    public void setPocHome(String pocHome) {
        this.pocHome = pocHome;
    }

    public String getPresenceHome() {
        return presenceHome;
    }

    public void setPresenceHome(String presenceHome) {
        this.presenceHome = presenceHome;
    }

    public String getXdmsHome() {
        return xdmsHome;
    }

    public void setXdmsHome(String xdmsHome) {
        this.xdmsHome = xdmsHome;
    }

//    public long getCreationTimestamp() {
//        return creationTimestamp;
//    }

//    public void setCreationTimestamp(long creationTimestamp) {
//        this.creationTimestamp = creationTimestamp;
//    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void populate(IPopulate dtoObject) {

    }

    public int getServiceAuthStatus() {
        return serviceAuthStatus;
    }

    public void setServiceAuthStatus(int serviceAuthStatus) {
        this.serviceAuthStatus = serviceAuthStatus;
    }

    public long getProfileLastUpdated() {
        return profileLastUpdated;
    }

    public void setProfileLastUpdated(long profileLastUpdated) {
        this.profileLastUpdated = profileLastUpdated;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getServiceAuthStatusOP() {
        return serviceAuthStatusOP;
    }

    public void setServiceAuthStatusOP(int serviceAuthStatusOP) {
        this.serviceAuthStatusOP = serviceAuthStatusOP;
    }

    public int getServiceAuthStatusAU() {
        return serviceAuthStatusAU;
    }

    public void setServiceAuthStatusAU(int serviceAuthStatusAU) {
        this.serviceAuthStatusAU = serviceAuthStatusAU;
    }

    /* public String toString(){
        StringBuffer strBuffer = new StringBuffer(200);
        strBuffer.append(super.toString());
        strBuffer.append(", DtoStatus - ").append(dtoStatus);
        strBuffer.append(", ObjectId - ").append(objectId);
        strBuffer.append(", ErrorObject - ").append(errorObject);
        strBuffer.append(", CorpId - ").append(corpId);
        strBuffer.append(", Mdn - ").append(mdn);
        strBuffer.append(", NetworkName - ").append(networkName);
        strBuffer.append(", PocHome - ").append(pocHome);
        strBuffer.append(", PresenceHome - ").append(presenceHome);
        strBuffer.append(", XdmsHome - ").append(xdmsHome);
        strBuffer.append(", ServiceAuthStatus - ").append(serviceAuthStatus);
        strBuffer.append(", CreationTimestamp - ").append(creationTimestamp);
        strBuffer.append(", ProfileLast Updated - ").append(profileLastUpdated);

        return strBuffer.toString();
    }*/
}
