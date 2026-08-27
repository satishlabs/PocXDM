/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.commdto.common;

import com.kodiak.common.commdto.common.KnIdDetailsListDTO;

import java.io.Serial;

public class KnAddedChildRelationDTO implements com.kodiak.common.dto.IIdentifier {

    @Serial
    private static final long serialVersionUID = 7526471155611776197L;
    private KnIdDetailsListDTO addedChildDetailsList;

    public KnAddedChildRelationDTO() {

    }

    public KnAddedChildRelationDTO(KnIdDetailsListDTO addedChildDetailsList) {
        this.addedChildDetailsList = addedChildDetailsList;
    }

    @Override
    public String getObjectId() {
        return "";
    }

    public KnIdDetailsListDTO getAddedChildDetailsList() {
        return addedChildDetailsList;
    }

    public void setAddedChildDetailsList(KnIdDetailsListDTO addedChildDetailsList) {
        this.addedChildDetailsList = addedChildDetailsList;
    }

    @Override
    public String toString() {
        return "KnAddedChildRelationDTO{" +
                "idDetailsList=" + addedChildDetailsList +
                '}';
    }
}
