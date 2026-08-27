/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * Created by asanjiv on 9/3/15.
 */
package com.kodiak.common.commdto.response;

import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;

import java.util.List;

public class KnReverseContactResponseDto extends KnXDMCorpRespDTO {
    private static final long serialVersionUID = 7526471155622676150L;

    private List<KnXDMMdnInfoDTO> reverseContacts;

    public List<KnXDMMdnInfoDTO> getReverseContacts() {
        return reverseContacts;
    }

    public void setReverseContacts(List<KnXDMMdnInfoDTO> reverseContacts) {
        this.reverseContacts = reverseContacts;
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append(" reverseContacts - ").append(reverseContacts);
        return strBuffer.toString();

    }
}
