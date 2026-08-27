/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnValidatorEntity.java
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

/**
 * This is the base class for all the validator entities.
 */
public abstract class KnValidatorEntity extends KnValidatorRule
        implements IValidatorEntity {
    private static final long serialVersionUID = 7526471155622676276L;

    private IValidatorRule validatorRule;

    /**
     * Sets validator rule to imply the validation logic on current DTO set to the validator entity object by Framework
     *
     * @param validatorRule
     */
    public void setValidatorRule(IValidatorRule validatorRule) {
        this.validatorRule = validatorRule;
    }

    /**
     * Returns the current validator rule set by the Framework.
     *
     * @return IValidatorRule object
     */
    public IValidatorRule getValidatorRule() {
        return this.validatorRule;
    }
}
