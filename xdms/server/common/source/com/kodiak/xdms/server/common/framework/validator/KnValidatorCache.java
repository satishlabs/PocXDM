/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnValidatorCache.java
 * Subsystem:   Validator Framework
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         14-03-2007 6.0
 *
 *
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 **************************************************************************/
package com.kodiak.xdms.server.common.framework.validator;

import com.kodiak.common.dto.IIdentifier;


/**
 * Validatoe cache
 */
public class KnValidatorCache implements IIdentifier {
    private static final long serialVersionUID = 7526471155622676264L;

    /**
     * Class instance of validator rule
     */
    private Class classObject = null;
    /**
     * Validator ID
     */
    private String id = null;
    /**
     * validator rule is enabled or not.
     */
    private boolean enabled = false;

    /**
     * Gets the class instance of validator rule
     *
     * @return
     */
    public Class getClassObject() {
        return classObject;
    }

    /**
     * Sets the class instance of validator rule
     *
     * @param classObject
     */
    public void setClassObject(Class classObject) {
        this.classObject = classObject;
    }

    /**
     * Gets the ID of validator rule
     *
     * @return
     */
    public String getObjectId() {
        return id;
    }

    /**
     * Sets the ID of validator rule
     *
     * @param ID
     */
    public void setId(String ID) {
        this.id = ID;
    }

    /**
     * Whether the  validator rule is enabled or not
     *
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the validator rule is enaled or not
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
