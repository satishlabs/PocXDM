/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.dto.clientdat;

import com.kodiak.xdms.server.common.KnXDMError;
import com.kodiak.xdms.server.common.dto.intf.IOutputDTO;
import com.kodiak.xdms.server.common.dto.intf.IPopulate;
import com.kodiak.xdms.server.subsmgmt.dto.common.KnSubsAddlInfoDTO;

/**
 * Created by hanwar on 24-02-2017.
 */
public class KnOPSubsAddlInfoProfileDTO extends KnSubsAddlInfoDTO implements IOutputDTO {
    private static final long serialVersionUID = 7526471155678676236L;
    //stores the clientdat code
    private String responseCode;
    //stores the clientdat Status
    private int responseStatus;
    //stores the clientdat Message
    private String responseMessage;
    //stores the DTO Status
    private int DTOStatus;
    //stores the error Object
    private KnXDMError errorObject;


    /**
     * getter method for the Response Code
     *
     * @return String
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * setter method for the Response Code
     *
     * @param responseCode String
     */
    public void setResponseCode(String responseCode) {
        if (responseCode != null) {
            responseCode = responseCode.trim();
            if (responseCode.equals("")) {
                responseCode = null;
            }
        }
        this.responseCode = responseCode;
    }

    /**
     * getter method for the clientdat Status
     *
     * @return int
     */
    public int getResponseStatus() {
        return responseStatus;
    }

    /**
     * setter method for the clientdat status
     *
     * @param responseStatus int
     */
    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    /**
     * getter method for the Response Message
     *
     * @return String
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * setter method for the Response Message
     *
     * @param responseMessage String
     */
    public void setResponseMessage(String responseMessage) {
        if (responseMessage != null) {
            responseMessage = responseMessage.trim();
            if (responseMessage.equals("")) {
                responseMessage = null;
            }
        }
        this.responseMessage = responseMessage;
    }

    /**
     * getter method for the DTO Status
     *
     * @return int
     */
    public int getDTOStatus() {
        return DTOStatus;
    }

    /**
     * setter method for the DTO Status
     *
     * @param dtoStatus int
     */
    public void setDTOStatus(int dtoStatus) {
        this.DTOStatus = dtoStatus;
    }

    /**
     * getter method for the Error Object
     *
     * @return KnErrorObject Object
     */
    public KnXDMError getErrorObject() {
        return errorObject;
    }

    /**
     * setter method for the Error Object
     *
     * @param errorObj KnXDMError Object
     */
    public void setErrorObject(KnXDMError errorObj) {
        this.errorObject = errorObj;
    }

    public String getObjectId() {
        return this.getMdn();
    }

    public void populate(IPopulate dtoObject) {
    }

    public String toString() {
        return "KnOPSubsAddlInfoProfileDTO{" +
                "responseCode='" + responseCode + '\'' +
                ", responseStatus=" + responseStatus +
                ", responseMessage='" + responseMessage + '\'' +
                ", DTOStatus=" + DTOStatus +
                ", errorObject=" + errorObject +
                '}';
    }
}
