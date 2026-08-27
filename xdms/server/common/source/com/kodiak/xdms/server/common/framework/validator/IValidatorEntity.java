/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   IValidatorEntity.java
 * Subsystem:   Validator Framework
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         09-03-2007 6.0
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
 * This validator entity interface is responsible for setting different parameters required for different validator
 * entities by the framework. This interface is inheriting form validation rule and any new validator entity would be
 * created must implement this interface.
 */
public interface IValidatorEntity extends IValidatorRule {

    /**
     * Sets validator rule to imply the validation logic on current DTO set to the validator entity object by Framework
     *
     * @param validatorRule
     */
    public void setValidatorRule(IValidatorRule validatorRule);

    /**
     * Returns the current validator rule set by the Framework.
     *
     * @return IValidatorRule object
     */
    public IValidatorRule getValidatorRule();
}
