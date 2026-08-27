/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/*************************************************************************
 * <p/>
 * File name:   IExpression.java
 * Subsystem:   Validator Framework
 * <p/>
 * Name                 Date       Release
 * ------------------ ---------- ---------------------------------------
 * Rama Krishna       09-03-2007 6.0
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

public interface IExpression extends IRule{
    /**
     * returns the expression id of the rule
     *
     * @return expressionId
     */
    public String getExpressionId();

    /**
     * sets the expression id to the rule
     *
     * @param expressionId
     */
    public void setExpressionId(String expressionId);
    public void setOperator (String operator);
    public String getOperator ();
    public Collection getRules ();
    public void setRules (Collection rules);
    public boolean evaluate ();
    public void setConsolidateRules(boolean consolidateRules);
    public boolean hasToConsolidateRules();
}
