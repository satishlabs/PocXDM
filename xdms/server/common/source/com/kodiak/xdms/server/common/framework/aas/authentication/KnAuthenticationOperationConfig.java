/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnAuthenticationOperationConfig.java
 * Subsystem:   WGP
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Upananda Singha      29-12-2006 5.7
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
package com.kodiak.xdms.server.common.framework.aas.authentication;

import com.kodiak.xdms.server.common.framework.aas.KnAASConstants;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

public class KnAuthenticationOperationConfig {

    //list of rules configured for authentication
    private Map rules;

    //flag for allowing authentication
    private boolean isEnabled = false;

    /**
     * constructor
     */
    public KnAuthenticationOperationConfig() {
        rules = new HashMap();
    }

    /**
     * Returns list of rules configured for authentication
     *
     * @return list of rules configured for authentication
     */
    public Collection getRules() {
        return rules.values();
    }

    /**
     * Sets list of rules configured for authentication
     *
     * @param rules list of rules configured for authentication
     */
    public void setRules(Collection rules) {
        if (rules != null) {
            for (Iterator it = rules.iterator(); it.hasNext();) {
                IAuthenticationRule ruleObj = (IAuthenticationRule) it.next();
                addRule(ruleObj);
            }
        }
    }

    /**
     * Add a rule to the rule list
     *
     * @param rule list of rules configured for authentication
     */
    public void addRule(IAuthenticationRule rule) {
        if (rule != null)
            this.rules.put(rule.getRuleId(), rule);
    }

    /**
     * Check whether the authentication is enabled
     * @return
     */
    public boolean getEnabled() {
        return isEnabled;
    }

    /**
     * set the authentication enabled
     * @param isEnabled
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * set the authentication is required flag
     * @param isEnabled
     */
    public void setEnabled(String isEnabled) {
        if (isEnabled != null && KnAASConstants.TRUE.equals(isEnabled.trim())) {
            this.isEnabled = true;
        } else {
            this.isEnabled = false;
        }
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
        StringBuffer buffer = new StringBuffer(50);
        buffer.append("KnAuthenticationOperationConfig{")
                .append("Enabled: ").append(isEnabled).append(", Rules: ").append(rules)
                .append("}");
        return buffer.toString();
    }
}
