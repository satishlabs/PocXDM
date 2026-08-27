/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.common.dto.common;

import com.kodiak.common.dto.IIdentifier;
import com.kodiak.common.resources.KnGDPRTemplate;

/**
 * ************************************************************************
 * <p>
 * File name:  KnRecordingTargetInfoDTO.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Sudhendu K Nayak           25-january-2023              12.3
 * <p>
 * <p>
 * The information disclosed herein is confidential and proprietary to Kodiak and/or Motorola Solutions, Inc.,
 * and is being furnished under a valid license agreement. This information may not be disclosed to third parties
 * without the prior written consent of Kodiak and/or Motorola Solutions, Inc. The recipient of this information
 * shall respect the security status of the information.
 * <p>
 * MOTOROLA, MOTO, MOTOROLA SOLUTIONS, and the Stylized M Logo are trademarks or registered trademarks of Motorola
 * Trademark Holdings, LLC and are used under license. All other trademarks are the property of their respective owners.
 * © 2022 Motorola Solutions, Inc. All rights reserved.
 * ************************************************************************
 */

public class KnRecordingTargetInfoDTO implements IIdentifier {

    private static final long serialVersionUID = -2007559526898103976L;

    private String target;
    private String priRecIp;
    private Integer priRecPort;
    private String secRecIp;
    private Integer secRecPort;
    private Integer recType;

    public String getTarget() {
        return target;
    }

    public KnRecordingTargetInfoDTO setTarget(String target) {
        this.target = target;
        return this;
    }

    public String getPriRecIp() {
        return priRecIp;
    }

    public KnRecordingTargetInfoDTO setPriRecIp(String priRecIp) {
        this.priRecIp = priRecIp;
        return this;
    }

    public Integer getPriRecPort() {
        return priRecPort;
    }

    public KnRecordingTargetInfoDTO setPriRecPort(Integer priRecPort) {
        this.priRecPort = priRecPort;
        return this;
    }

    public String getSecRecIp() {
        return secRecIp;
    }

    public KnRecordingTargetInfoDTO setSecRecIp(String secRecIp) {
        this.secRecIp = secRecIp;
        return this;
    }

    public Integer getSecRecPort() {
        return secRecPort;
    }

    public KnRecordingTargetInfoDTO setSecRecPort(Integer secRecPort) {
        this.secRecPort = secRecPort;
        return this;
    }

    public Integer getRecType() {
        return recType;
    }

    public KnRecordingTargetInfoDTO setRecType(Integer recType) {
        this.recType = recType;
        return this;
    }

    @Override
    public String toString() {
        return "KnRecordingTargetInfoDTO{" +
                ", target='" + KnGDPRTemplate.mdn(target) + '\'' +
                ", priRecIp='" + priRecIp + '\'' +
                ", priRecPort=" + priRecPort +
                ", secRecIp='" + secRecIp + '\'' +
                ", secRecPort=" + secRecPort +
                ", recType=" + recType +
                '}';
    }

    @Override
    public String getObjectId() {
        return null;
    }
}
