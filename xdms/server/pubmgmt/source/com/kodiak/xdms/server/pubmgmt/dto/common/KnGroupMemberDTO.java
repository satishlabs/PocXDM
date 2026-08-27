/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.dto.common;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnGroupMemberDTO.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 13, 2011           7.0
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
public class KnGroupMemberDTO extends KnMemberDTO {

    private static final long serialVersionUID = 7526471155622676214L;

    public KnGroupMemberDTO(){}

    public KnGroupMemberDTO(String mdn,String displayName,String aliasMdn,String userId,String ufmi){
        super.setMemberMdn(mdn);
        super.setMemberName(displayName);
        super.setAliasMdn(aliasMdn);
        super.setUserId(userId);
        super.setUfmi(ufmi);
    }
}
