/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:   IRule.java
 * Subsystem:   Validator Framework
 * <p/>
 * Name                 Date       Release
 * ------------------ ---------- ---------------------------------------
 * Rama Krishna       06-03-2007 6.0
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


import com.kodiak.xdms.server.common.dto.intf.IEntity;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.util.Map;

public interface IRule extends IEntity {
    /**
     *
     * @return
     */
    public String getId();
    /**
     *
     * @param id
     */
    public void setId(String id);
    /**
     * Sets the parent DTO
     *
     * @param entity
     */
    public void setParentDTO(IEntity entity);

    /**
     * Returns the parent DTO
     *
     * @return IEntity
     */
    public IEntity getParentDTO();

    /**
     * Sets the entity to the validation rule
     *
     * @param DTO
     */
    public void setDTO(IEntity DTO);

    /**
     * Returns the current persistenceDTO on which the validation rule applies.
     *
     * @return IEntity
     */
    public IPersistenceDTO getDTO();

    /**
     * Sets the Object on which the validation rule applies.
     *
     * @param obj
     */
    public void setObject(Object obj);

    /**
     * Returns the current Object on which the validation rule applies.
     *
     * @return Object
     */
    public Object getObject();

    /**
     * Sets whether the validation rule is enabled or not.
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled);

    /**
     * Checks whether validation rule is enabled or not.
     *
     * @return
     */
    public boolean isEnabled();

    /**
     * Sets the attributes configured for validation rule and entity.
     *
     * @param attributes
     */
    void setAttributes(Map attributes);

    /**
     * Returns the attributes configured for the validation rule.
     *
     * @return the attributes
     */
    Map getAttributes();

    /**
     * Sets the profile
     * @param profile
     */
    void setProfile(String profile);

    /**
     * Returns the profile
     * @return
     */
    String getProfile ();

    /**
     * Returns whether the exception must be thrown instantly at the rule level, if validation fails for a particular rule
     * If consolidate-dtos equals to true then it will validate the entire dtos and throws the exceptions all together at the rule level.
     * @return
     */
    public boolean isThrowOn();

    /**
     * Sets whether the exception must be thrown instantly at the rule level, if validation fails for a particular rule.
     * If consolidate-dtos equals to true then it will validate the entire dtos and throws the exceptions all together at the rule level.
     * @param throwOn
     */
    public void setThrowOn (boolean throwOn);
    /**
     * returns the sub-property object
     * @return String
     */
    public String getProperty();

    /**
     * sets sub-property object
     * @param property
     */
    public void setProperty(String property);
    /**
     * returns the sub-property object
     * @return String
     */
    public String getSubProperty();

    /**
     * sets sub-property object
     * @param subProperty
     */
    public void setSubProperty(String subProperty);
    /**
     * return the error data type of the validation rule
     * @return error data type
     */
    public String getErrorType();
    /**
     * sets error data type to the validation rule
     * @param errorType
     */
    public void setErrorType(String errorType);
    /**
     *
     * @param key
     * @return
     */
    public String getAttribute(String key);
}
