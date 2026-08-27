/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * File name:   KnDefaultOperator.java
 * Subsystem:   Validator Framework
 * <p/>
 * Name                 Date       Release
 * ------------------ ---------- ---------------------------------------
 * Rama Krishna K     02-01-2007   6.0
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
 *************************************************************************/
package com.kodiak.xdms.server.common.framework.validator;

import java.util.Collection;

public class KnDefaultOperator extends KnRuleConfig implements IExpression {
     private static final long serialVersionUID = 7526471155622676274L;

    //String expressionId;
    String operator;
    Collection rules;
    private boolean consolidateRules = false;

    /**
     * returns the expressionid
     * @return String
     */
    public String getExpressionId() {
        return id;
    }

    /**
     * sets the expression-id
     * @param expressionId
     */
    public void setExpressionId(String expressionId) {
        this.id = expressionId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Collection getRules() {
        return rules;
    }

    public void setRules(Collection rules) {
        this.rules = rules;
    }

    public boolean evaluate() {
        return true;
    }

    public void setConsolidateRules(boolean consolidateRules) {
        this.consolidateRules = consolidateRules;
    }

    public boolean hasToConsolidateRules() {
        return consolidateRules;  
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p/>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "{ExprnId: " + id + ", operator: " + operator + ", rules : " + rules + ", errorType: " + errorType + "}";
    }


    /**
     * returns the id
     *
     * @return id
     */
    public String getObjectId() {
        return operationId + id;
    }
}
