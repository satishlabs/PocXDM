/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMContactListDTO;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMContactListDetailsDTO.java
 * Subsystem:  common
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Feb 3, 2011           7.0
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
public class KnXDMContactListDetailsDTO extends KnXDMContactListDTO {

    private static final long serialVersionUID = 7526471155622676150L;

    private Collection<KnXDMMdnInfoDTO> members;

    
    public Collection<KnXDMMdnInfoDTO> getMembers() {
        return members;
    }

    public void setMembers(Collection<KnXDMMdnInfoDTO> members) {
        this.members = members;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(100);
        strBuffer.append(super.toString());

        strBuffer.append(", Members : ").append(members);

        return strBuffer.toString();

    }
}
