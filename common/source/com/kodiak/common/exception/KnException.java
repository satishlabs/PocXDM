/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnException.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      Dec 2, 2010  7.0
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
package com.kodiak.common.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class KnException extends Exception {

    // stores the error code
    protected String errorCode;
    // stores the status of error
    private STATUS_CODE statusCode = STATUS_CODE.FAILURE;
    // stores the error message
    private String errorMessage;

    /**
     * stores the parameters which causes the error. these parameters can be used for
     * constructing a valid error message
     */
    private List errorParams;

    /**
     * this will accept error code and error message and constructs an Exception object
     *
     * @param errorCode    - error code for the error
     * @param errorMessage - message for logging
     */
    public KnException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * @param errorCode    - error code
     * @param statusCode   - status code
     * @param errorMessage - message for logging
     */
    public KnException(String errorCode, STATUS_CODE statusCode, String errorMessage) {
        this.errorCode = errorCode;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    /**
     * This will create Exception using error code, error message and another
     * Exception object.
     *
     * @param errorCode    - error code
     * @param errorMessage - message for logging
     * @param root         - root of an another exception
     */
    public KnException(String errorCode, String errorMessage, Throwable root) {
        super(root);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * @param errorCode    - error code
     * @param statusCode   - status Code
     * @param errorMessage - message for logging
     * @param root         - root of an another exception
     */
    public KnException(String errorCode, STATUS_CODE statusCode, String errorMessage, Throwable root) {
        super(root);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    /**
     * this will accept error code and error message and constructs an Exception object
     *
     * @param errorCode    - error code for the error
     * @param errorMessage - message for logging
     */
    public KnException(String errorCode, Throwable root) {
        super(root);
        this.errorCode = errorCode;
        this.errorMessage = root.getMessage();
    }


    /**
     * This method will return the error code
     *
     * @return error code of the exception
     */
    public String  getErrorCode() {
        return this.errorCode;
    }

    /**
     * @return the status code
     */
    public STATUS_CODE getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode status code
     */
    public void setStatusCode(STATUS_CODE statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * This method will return the error message
     *
     * @return error message of the exception
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * This will return the list error parameters. it can be list of MDNs, grou pnames
     * etc.. all the elements in the list will be of type String.
     *
     * @return List  returns list of error parameters
     */
    public List getErrorParameters() {
        return this.errorParams;
    }

    /**
     * This method will set error parameter list to the exception
     *
     * @param params list of error parameters
     */
    public void setErrorParameters(List params) {
        this.errorParams = params;
    }

    /**
     * This method will set the error code
     *
     * @param errorCode error code of the error
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * This method will set the error messages
     *
     * @param errorMessage error message string
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public static enum STATUS_CODE {

        FAILURE(0), TIMEOUT(1);

        private int statusCode;

        STATUS_CODE(int statusCode) {
            this.statusCode = statusCode;
        }

        public int get() {
            return statusCode;
        }
    }

    private static String joinStackTrace(Throwable e) {
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            joinStackTrace(e, writer);
            return writer.toString();
        }
        finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {
                    // ignore
                }
        }
    }

    private static void joinStackTrace(Throwable e, StringWriter writer) {
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(writer);
            while (e != null) {
                printer.println(e);
                StackTraceElement[] trace = e.getStackTrace();
                for (StackTraceElement aTrace : trace) {
                    printer.println("\tat " + aTrace);
                }
                e = e.getCause();
                if (e != null) {
                    printer.println("Caused by:\r\n");
                }
            }
        } finally {
            if (printer != null)
                printer.close();
        }
    }

    /**
     * This method will return the message of the exception
     *
     * @return the message
     */
    public String getMessage() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("Error Code : ").append(errorCode)
                .append(", Error Message : ").append(errorMessage)
                .append(", Cause : ").append(joinStackTrace(getCause()));
        return sb.toString();
    }
}