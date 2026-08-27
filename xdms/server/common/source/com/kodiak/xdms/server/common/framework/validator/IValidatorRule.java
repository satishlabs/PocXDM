/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   IValidatorRule.java
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
 * This interface is for setting different mandatory parameters by the framework to execute the validation rule and its
 * entities. All the new validation rules woulod be created must implement this interface.
 */
public interface IValidatorRule extends IValidator, IRule {
     public static String DELIM = ",";
    /**
     * Sets the rule id
     * @param ruleId
     */
    void setRuleId(String ruleId);
    /**
     * This method will return the rule id
     *
     * @return the rule id
     */
    String getRuleId();
    /**
     * Returns the boolean whether to consolidate the execution of all dtos. If its true and any exception occured will be consumed
     * and thrown alltogether. Default false..If false, and validation failed for a dto, the rule would not be executed on further dtos
     * @return
     */
     public boolean hasToConsolidateDtos();

    /**
     * Sets the boolean whether to consolidate the execution of all dtos. If its true and any exception occured will be consumed
     * and thrown alltogether. Default false..If false, and validation failed for a dto, the rule would not be executed on further dtos
     * @param consolidateDtos
     */
    public void setConsolidateDtos (boolean consolidateDtos);


    /**
     * returns iterate flag
     * @return  boolean
     */
    public boolean doIterate();

    /**
     * set iterate flag
     * @param iterator
     */
    public void setIterate(boolean iterator);

    /**
     *
     * @return
     */
    public String getIterateAttribute();

    /**
     * 
     * @param iterateAttribute
     */
    public void setIterateAttribute(String iterateAttribute);

}
