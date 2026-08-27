/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnValidatorRule.java
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
 *
 */
public abstract class KnValidatorRule extends KnRuleConfig implements IValidatorRule {
    private static final long serialVersionUID = 7526471155622676278L;

    private boolean consolidateDtos = false;
    private boolean iterator = false;
    private String iterateAttribute = KnValidatorConstants.ITERATE_KEYS;

    /**
     * sets rule id
     * @param ruleId
     */
    public void setRuleId(String ruleId) {
        this.id = ruleId;
    }

    /**
     * This method will return the rule id
     *
     * @return the rule id
     */
    public String getRuleId() {
        return id;
    }

    /**
     * Returns whether to consolidate the execution of all dtos. If its true and any exception occured will be consumed
     * and thrown alltogether.Default false...
     *
     * @return
     */
    public boolean hasToConsolidateDtos() {
        return consolidateDtos;
    }

    /**
     * Sets the boolean whether to consolidate the execution of all dtos. If its true and any exception occured will be consumed
     * and thrown alltogether. Default false..If false, and validation failed for a dto, the rule would not be executed on further dtos
     *
     * @param consolidateDtos
     */
    public void setConsolidateDtos(boolean consolidateDtos) {
        this.consolidateDtos = consolidateDtos;
    }



    /**
     * returns the status of iterate flag
     * @return
     */
    public boolean doIterate() {
        return iterator;
    }

    /**
     * sets the iterator flag
     * @param iterator
     */
    public void setIterate(boolean iterator) {
        this.iterator = iterator;
    }

    /**
     *
     * @return
     */
    public String getObjectId() {
        return operationId + id;
    }

    /**
     *
     * @return
     */
    public String getIterateAttribute() {
        return iterateAttribute;
    }

    /**
     *
     * @param iterateAttribute
     */
    public void setIterateAttribute(String iterateAttribute) {
        this.iterateAttribute = iterateAttribute;
    }

    /**
     * returns the string representation of validator rule object.
     * @return  String
     */
    public String toString() {
        return "{RuleId: " + id + ", enabled: " + enabled + ", errorType: " + errorType + ", attribute : " + attributes + ", entityid : " + entityId + "}";
    }
}
