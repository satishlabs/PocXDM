/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnErrorBean.java
 * Subsystem:  IDS
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Ajit Kumar           18-Dec-2011  7.2
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
package com.kodiak.xdms.mediator.util;


import com.kodiak.xdms.mediator.KnMediatorConstants;

public class KnErrorBean {

    private String errorCode;
    private String majorCode;
    private String majorDescription;
    private String serviceErrorCode;
    private String errMsg;
    private String httpStatusCode;


    public KnErrorBean(String errorCode, String errMsg) {
        this.errorCode = errorCode;
        this.errMsg = errMsg;

    }


    public KnErrorBean(String errorCode, String serviceErrorCode, String errMsg) {
        this(errorCode, errMsg);
        this.serviceErrorCode = serviceErrorCode;
    }

    public KnErrorBean(String errorCode, String serviceErrorCode,
                       String errMsg, String majorCode, String desc, String httpStatusCode) {
        this(errorCode, serviceErrorCode, errMsg);
        this.majorCode = majorCode;
        this.majorDescription = desc;
        this.httpStatusCode = httpStatusCode;
    }

    public KnErrorBean(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        if (errorCode != null) {
            errorCode = errorCode.trim();
            if (isNullOrEmpty(errorCode)) {
                errorCode = null;
            }
        }
        this.errorCode = errorCode;
    }


    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        if (errMsg != null) {
            errMsg = errMsg.trim();
            if (isNullOrEmpty(errMsg)) {
                errMsg = null;
            }
        }
        this.errMsg = errMsg;
    }

    public String getServiceErrorCode() {
        return serviceErrorCode;
    }

    public void setServiceErrorCode(String serviceErrorCode) {
        if (serviceErrorCode != null) {
            serviceErrorCode = serviceErrorCode.trim();
            if (isNullOrEmpty(serviceErrorCode)) {
                serviceErrorCode = null;
            }
        }
        this.serviceErrorCode = serviceErrorCode;
    }


    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        if (majorCode != null) {
            majorCode = majorCode.trim();
            if (isNullOrEmpty(majorCode)) {
                majorCode = null;
            }
        }
        this.majorCode = majorCode;
    }

    public String getMajorDescription() {
        return majorDescription;
    }

    public void setMajorDescription(String majorDescription) {

        if (majorDescription != null) {
            majorDescription = majorDescription.trim();
            if (isNullOrEmpty(majorDescription)) {
                majorDescription = null;
            }
        }
        this.majorDescription = majorDescription;
    }


    public String getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(String httpStatusCode) {
        if (httpStatusCode != null) {
            httpStatusCode = httpStatusCode.trim();
            if (isNullOrEmpty(httpStatusCode)) {
                httpStatusCode = null;
            }
        }
        this.httpStatusCode = httpStatusCode;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(200);
        sb.append("Error Bean => ")
                .append("Error Code : ").append(errorCode)
                .append("Service Error Code : ").append(serviceErrorCode)
                .append(KnMediatorConstants.EMPTY_SPACING)
                .append("Error MSG : ").append(errMsg).append(" ")
                .append(KnMediatorConstants.EMPTY_SPACING)
                .append("Major Code : ").append(majorCode)
                .append(KnMediatorConstants.EMPTY_SPACING)
                .append("Description : ").append(majorDescription);
        return sb.toString();
    }

    public static boolean isNullOrEmpty(String value) {
        String methodName = "isNullOrEmpty(String)";
        boolean result = false;
        if (null == value) {
            result = Boolean.TRUE;
        } else if (KnMediatorConstants.EMPTY_STRING.equalsIgnoreCase(value.trim())) {
            result = Boolean.TRUE;
        }
        return result;
    }
}
