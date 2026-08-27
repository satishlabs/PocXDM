/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   KnRuleConfig.java
 * Subsystem:   Validator Framework
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         14-03-2007 6.0
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
 ***************************************************************************/
package com.kodiak.xdms.server.common.framework.validator;

import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.dto.intf.IEntity;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.util.Map;

/**
 * 
 */
public class KnRuleConfig implements IRule {
    private static final long serialVersionUID = 7526471155622676275L;

    protected IPersistenceDTO persistenceDTO;
    protected IInputDTO inputDTO;

    protected IEntity parentDTO;

    protected String entityId;
    protected String operationId;
    protected String errorType;
    protected String id;
    protected Map attributes;
    private String profile = null;
    private boolean throwOn = false;
    private Object object;
    protected boolean enabled;
    private String property;
    private String subProperty;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map getAttributes() {
        return attributes;
    }

    public void setAttributes(Map attributes) {
        this.attributes = attributes;
    }

    public IPersistenceDTO getDTO() {
        return persistenceDTO;
    }

    public IInputDTO getInputDTO() {
        return inputDTO;
    }

    /**
     * returns the errorType
     *
     * @return String
     */
    public String getErrorType() {
        return errorType;
    }

    /**
     * sets the errorType to the validtion rule object
     *
     * @param errorType
     */
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    /**
     * Sets the parent DTO
     *
     * @param entity
     */
    public void setParentDTO(IEntity entity) {
        this.parentDTO = entity;
    }

    /**
     * Returns the parent DTO
     *
     * @return IEntity
     */
    public IEntity getParentDTO() {
        return parentDTO;
    }

    /**
     * @param inputDTO
     */
    public void setDTO(IInputDTO inputDTO) {
        this.inputDTO = inputDTO;
        this.object = inputDTO;
    }

    /**
     * @param persistDTO public void setDTO(IEntity persistDTO) {
     *                   this.persistenceDTO = persistDTO;
     *                   if (persistDTO != null)
     *                   this.inputDTO = persistDTO.getInputDTO();
     *                   object = persistenceDTO;
     *                   }
     */

    /**
     *
     * @param dto
     */
    public void setDTO(IEntity dto) {
        if (dto instanceof IPersistenceDTO) {
            inputDTO = ((IPersistenceDTO) dto).getInputDTO();
            this.persistenceDTO = (IPersistenceDTO) dto;
        } else if (dto instanceof IInputDTO) {
            inputDTO = (IInputDTO) dto;
        }
        this.object = dto;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEntityId(String id) {
        this.entityId = id;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public String getOperationType() {
        return operationId;
    }

    public void setOperationType(String operationId) {
        this.operationId = operationId;
    }

    public String getAttribute(String key) {
        return (String) attributes.get(key);
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getProfile() {
        return profile;
    }

    /**
     * Returns whether the exception must be thrown instantly at the rule level, if validation fails for a particular rule
     * If consolidate-dtos equals to true then it will validate the entire dtos and throws the exceptions all together at the rule level.
     *
     * @return
     */
    public boolean isThrowOn() {
        return throwOn;
    }

    /**
     * Sets whether the exception must be thrown instantly at the rule level, if validation fails for a particular rule.
     * If consolidate-dtos equals to true then it will validate the entire dtos and throws the exceptions all together at the rule level.
     *
     * @param throwOn
     */
    public void setThrowOn(boolean throwOn) {
        this.throwOn = throwOn;
    }

    /**
     * returns the property of dto object
     *
     * @return String
     */
    public String getProperty() {
        return property;
    }

    /**
     * sets the property
     *
     * @param property
     */
    public void setProperty(String property) {
        this.property = property;
    }


    /**
     * returns the sub-property
     *
     * @return String
     */
    public String getSubProperty() {
        return subProperty;
    }

    /**
     * sets the sub-property attribute
     *
     * @param subProperty
     */
    public void setSubProperty(String subProperty) {
        this.subProperty = subProperty;
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
