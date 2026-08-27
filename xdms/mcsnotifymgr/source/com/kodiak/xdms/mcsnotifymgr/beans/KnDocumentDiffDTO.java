/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mcsnotifymgr.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kodiak.common.dto.IIdentifier;

import java.util.List;

/**
 * ************************************************************************
 * <p>
 * File name: KnDocumentDiffDTO.java
 * Subsystem:  XDMS
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Venkata Sudhakar            Dec 23, 2019                10.0+
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KnDocumentDiffDTO implements IIdentifier {

    private static final long serialVersionUID = -1661780901131601118L;


    //TODO:Get the type of objects for document diff whenever we are supporting DIff notifications and add them

    @JsonProperty(value ="add")
    private List<String> addDoc;

    @JsonProperty(value ="replace")
    private List<String> replaceDoc;

    @JsonProperty(value ="remove")
    private List<String> removeDoc;


    @Override
    public String getObjectId() {
        return null;
    }
}
