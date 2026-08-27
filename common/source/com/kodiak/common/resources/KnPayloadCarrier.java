/*
 * **************************************************************************************************
 *  * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 *  * All Rights Reserved                                                                             *
 *  * Motorola Solutions Confidential Restricted                                                      *
 *  *************************************************************************************************
 */
package com.kodiak.common.resources;

public class KnPayloadCarrier<T> {

    private Class<T> myType;
    private String IdentifierStr;
    private T myObject;

    public T getMyObject() {
        return myObject;
    }

    public void setMyObject(T myObject) {
        this.myObject = myObject;
        this.myType = (Class<T>) myObject.getClass();
    }

    public Class<T> getMyType() {
        return myType;
    }

    public String getIdentifierStr() { return IdentifierStr; }

    public void setIdentifierStr(String identifierStr) { this.IdentifierStr = identifierStr; }
}
