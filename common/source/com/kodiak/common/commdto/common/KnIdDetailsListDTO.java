/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.dto.IIdentifier;

import java.util.Arrays;


public class KnIdDetailsListDTO implements IIdentifier{

    private static final long serialVersionUID = 7526471155622676157L;
    private KnIdDetailsDTO[] idDetailsDto;
    private String objectId;

    public KnIdDetailsDTO[] getIdDetailsDto() {
        return idDetailsDto;
    }

    public void setIdDetailsDto(KnIdDetailsDTO[] idDetsDto) {
        if (idDetsDto != null) {
            this.idDetailsDto = Arrays.copyOf(idDetsDto, idDetsDto.length);
    }
    }

    public String toString() {
        StringBuilder strBuffer = new StringBuilder(50);
        strBuffer.append(" idDetailsDto - ").append(Arrays.toString(idDetailsDto));
        return strBuffer.toString();
    }

    public String getObjectId() {
        return objectId;
    }
}
