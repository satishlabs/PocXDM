/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnGenValidatorRules.java
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

import com.kodiak.common.exception.KnSystemError;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerSystemException;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheElement;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheAttributes;
import com.kodiak.xdms.server.common.util.KnClassLoader;
import com.kodiak.xdms.server.common.util.KnClassLoaderException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a part of the validator framework and responsible for creating different validator rule objects as per
 * the operationID set and the configuration for different validator rules provided under each operation in the
 * validator XML.The object invokes the validator rule related configurations corresponding to the values set for
 * IConfigCache parameters for a validator rule.
 */
public class KnGenValidatorRules extends KnValidatorConfigCache {
	private static final KnLogger knLogger = KnLogger.getLogger(KnGenValidatorRules.class);

    private static final String CLASS = KnGenValidatorRules.class.getName();
    /**
     * The local cache for storing a set of validator rule Class instances for an operation
     */
    private static HashMap validatorRuleCache = new HashMap();

    private String parent;
    /**
     * Constructor with configuration manager object, module name, parent elements, operation id
     */
    public KnGenValidatorRules(KnConfigurationsManager configManager, String moduleName, String parent) {
//        super.setModuleName(KnValidatorConstants.MODULE);
//        super.setParents(new String[]{KnValidatorConstants.BO_VALIDATION_TAG,
//                                      KnValidatorConstants.RULES_CONFIG});
        super(configManager);
        super.setModuleName(moduleName);
        this.parent = parent;
        //super.setParents(new String[]{parent, KnValidatorConstants.RULES_CONFIG});
        //super.setParents(parents);
        //this.operationID = operationID;
    }


    /**
     * Creates the instances of validation rules for the given rule class instances and sets the parameters and
     * attributes configured in validator XML. The attributes will be accessed from the cache manager as per the
     * parameters set for the IConfigCache object and the validator rule ID provided in local cache object.
     *
     * @param elemId
     * @return  rule object
     */
    public IRule getValidatorConfigObject(String elemId, String configRefId) {
        String methodName = "getValidatorConfigObject(String elemId)";
        Class objClass = null;
        String ruleObjClassName = null;
        KnValidatorCache validatorCache = null;
        KnCacheElement validatorElement = null;
        KnCacheAttributes validatorAttributes = null;
        try {
            //Fetching validaor-config
            validatorElement = getValidatorElement(elemId, configRefId);
            validatorAttributes = validatorElement.getAttributes();
            //Try to get the class object from the local cache
            if (validatorRuleCache.containsKey(elemId)) {
                validatorCache = (KnValidatorCache) validatorRuleCache.get(elemId);
                objClass = validatorCache.getClassObject();
                knLogger.debug( methodName,
                        "Fetched class object from cache : Validator ID = " + elemId +
                                ": Class name = " + objClass.getName());
            }
            else {
                //Creates the class & cache object
                ruleObjClassName = validatorAttributes.get(KnValidatorConstants.CLASS_NAME);
                //Loads the validator rule class as per the class ruleAttributes of validator rule
                objClass = KnClassLoader.loadClass(ruleObjClassName);
                //Creating the local cache object
                validatorCache = new KnValidatorCache();
                validatorCache.setClassObject(objClass);
                validatorCache.setId(elemId);
                //Putting the cache object in local cache
                validatorRuleCache.put(elemId, validatorCache);
                knLogger.debug( methodName, "Loaded class and cached in locale cache" +
                                                                      " : Validator ID = " + elemId + ": Class name = " + objClass.getName());
            }
            //Returning the object of the validator rule
            return getValidatorRuleObject(objClass, validatorElement);
        } catch (KnClassLoaderException e) {
            throw new KnSystemError(KnErrorCodes.Validator.INTERNAL_ERROR, "Class not found for " + ruleObjClassName +
                                            " : elemId - " + elemId, e);
        }
    }

    /**
     * Returns a validator rule instance for the given rule class instance and the rule attributes. The method Creates
     * the instance and sets all parameters configured for a validation rule.
     *
     * @param objClass
     * @param element
     * @return
     */
    public IRule getValidatorRuleObject(Class objClass,
                                                 KnCacheElement element) {
        String methodName = "getValidatorConfigObject(Class, KnCacheElement)";
        IRule rule = null;
        Map validatorAttributes = null;
        try {
            rule = (IRule) objClass.newInstance();
            knLogger.debug( methodName,
                           "Validator rule object instantiated : ID -  " + element.getId());
            //Setting the attributes configured for validator
            validatorAttributes = element.getAttributesMap();
            //Adding the default parametes configured for the validator
            KnCacheElement paramElement = element.getElement(KnValidatorConstants.PARAMS);
            if (paramElement != null) {
                validatorAttributes.putAll(paramElement.getAttributesMap());
                knLogger.debug( methodName,
                               "Setting default parameters : " + paramElement.toString());
            }
            //Setting the attributes map to the validator rule instance
            rule.setAttributes(validatorAttributes);
            rule.setEntityId(element.getId());
            knLogger.debug( methodName,
                           "Validator rule object : " + rule.toString());
        } catch (InstantiationException e) {
            throw new KnSystemError(KnErrorCodes.Validator.INTERNAL_ERROR, "Could not instantiate object for validator rule : ID - " +
                                            element.getId(), e);
        } catch (IllegalAccessException e) {
            throw new KnSystemError(KnErrorCodes.Validator.INTERNAL_ERROR, "Could not instantiate object for validator rule : ID - " +
                                            element.getId(), e);
        }
        return rule;
    }

    /**
     * This method returns the validator config element object for a given  validator ID.
     *
     * @param validatorID
     * @return
     */
    public KnCacheElement getValidatorElement(String validatorID, String configRefId) {
        super.setParents(new String[]{parent, configRefId});
        super.setId(validatorID);
        //Fetching validator-config configuration
        KnCacheElement element = (KnCacheElement) getCachedValue(this);
        if (element == null)
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.CONFIG_ERROR, "validator-config configuration is not provided for validator id  - " + validatorID);

        knLogger.debug( "getValidatorElement(String, String)", "Validator rule element : " + element.toString());
        return element;

    }
}
