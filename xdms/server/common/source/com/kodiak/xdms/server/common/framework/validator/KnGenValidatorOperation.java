/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnGenValidatorOperation.java
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

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerSystemException;
import com.kodiak.xdms.server.common.KnXDMSystemError;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheAttributes;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheElement;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.util.KnClassLoader;
import com.kodiak.xdms.server.common.util.KnClassLoaderException;

import java.lang.reflect.Method;
import java.util.*;

/**
 * This class is a part of the validator framework and responsible for creating and caching different validator rule
 * objects as per the entity ID & operationID set and the configuration provided for different IDs in validator XML.The
 * object invokes the validator operation related configurations corresponding to the values set for IConfigCache
 * parameters. The class is also responsible for caching the validator rule class references once it is been loaded.
 */
public class KnGenValidatorOperation extends KnValidatorConfigCache {
	private static final KnLogger knLogger = KnLogger.getLogger(KnGenValidatorOperation.class);

    private static final String CLASS = KnGenValidatorOperation.class.getName();
    private static Map operationalRuleConfig = new HashMap(10);
    KnCacheElement operationElement = null;
    boolean consolidateRules = false;
    KnGenValidatorRules genValidatorRules = null;
    private HashMap ruleResultMap = new HashMap(5);

    /**
     * Constructor with operation ID
     *
     * @param configManager configuration manager
     * @param operationID
     */
    public KnGenValidatorOperation(KnConfigurationsManager configManager, String moduleName, String[] parents, String entityID, String operationID) {

//        super.setLibraryName(KnValidatorConstants.MODULE);
//        super.setParents(new String[]{KnValidatorConstants.BO_VALIDATION_TAG,
//                                      KnValidatorConstants.BO_ENTITIES_TAG, entityID});
        super(configManager);
        super.setModuleName(moduleName);
        super.setParents(new String[]{parents[0], parents[1], entityID});
        //super.setParent(entityID);
        super.setId(operationID);
        genValidatorRules = new KnGenValidatorRules(configManager, moduleName, parents[0]);
        operationElement = (KnCacheElement) getCachedValue(this);
        knLogger.debug( "KnGenValidatorOperation", "Creating instance for  Operation  = " + getOperationKey());
        if (operationElement == null) {
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.CONFIG_ERROR, "Configuration not provided for this operation : operation - " + getOperationKey());
        }
        //Setting consolidate rules boolean
        if (KnValidatorConstants.TRUE.equals(operationElement.getAttribute(KnValidatorConstants.CONSOLIDATE_RULES))) {
            consolidateRules = true;
        };
    }

    /**
     * it will add the rule result to the ruleResultMap with key and value.
     * @param key
     * @param obj
     */
    public void addRuleResult (String key, Object obj){
        ruleResultMap.put(key, obj);
    }

    /**
     * returns the result of the key
     * @param key
     * @return Object
     */
    public Object containsResult (String key){
        return ruleResultMap.get(key);
    }

    /**
     * Returns whether the execution of rules needs to be consolidated and throw exception all together for all rules
     * at the operation level. Default value false. If throw-on has been set to true for a rule, this will be bypassed
     * and the exception will be thrown at the rule level itself.
     *
     * @return
     */
    public boolean hasToConsolidateRules() {
        return consolidateRules;
    }

    /**
     * Gets the operation key for  storing validator rule class instances in local cache.
     *
     * @return String
     */
    protected final String getOperationKey() {
        return getParent() + KnValidatorConstants.DELIM + getId();
    }

    /**
     * This method fetches the corresponding list of validator rule classes for an operation from the local cache, if
     * present and then creates the instance of all validator rules. If the operation specific rules are not present in
     * the local cache, this method initiate the creation of validation rules and cache the loaded validation rule Class
     * instances.
     *
     * @return Collection list of validation rulesf
     * @throws KnValidationException
     */
    public Collection getValidatorRules() throws KnValidationException {
        String methodName = "getValidatorRules()";
        Iterator iterator = null;
        Collection elements = null;
        Collection validatorRules = null;
        //Accessing the cache manager for retrieving the operation  element.
        //Retrieves all validator rule elements under the operation
        elements = operationElement.getElements();
        if (elements.size() == 0) {
            return null;
        }
        knLogger.debug( methodName, "Validator rule size : " + elements.size() + ", Elements : " + elements + ", Operation Element : " + operationElement);
        iterator = elements.iterator();
        //creates the validator rule array
        validatorRules = new ArrayList(elements.size());
        //Iterating through validator elements
        for (; iterator.hasNext();) {
            KnCacheElement validatorRuleElement = (KnCacheElement) iterator.next();
            // checks the validateRuleElement is expression or rule
            if (isExpression(validatorRuleElement)) {
                validatorRules.add(populateExpressionObject(validatorRuleElement));
            } else {
                // populate the validation rule object
                validatorRules.add(populateValidatorRuleObject(validatorRuleElement));
            }
        }
        return validatorRules;
    }

    /**
     * Returns the corresponding method instance of a property configured for operation's validation rule.
     * The method instance will be returned from the local cache if its already loaded, else creates a new instance,
     * puts it in local cache and then returns the  same. The method instance will be created as of a part of the dto type
     * configured for the operation dto type.
     *
     * @param property
     * @return Method
     */
    public Method getMethod(String property) {
        String methodName = "getMethod(String)";

        if (property == null) {
            knLogger.debug( methodName, "Property is not configured : operation - " + getOperationKey() + " : rule id - " + property);
            return null;
        }

        //Fetching the dtotype for operation
        String dtoType = operationElement.getAttribute(KnValidatorConstants.DTO_TYPE);
        if (dtoType == null) {
            knLogger.error( methodName, "DTO type is not configured for operation - " + getOperationKey());
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.CONFIG_ERROR, "Method creation failed : DTO Type is not configured: Operation - " + getOperationKey());
        }

        String key = dtoType + property;
        knLogger.debug( methodName, "Operation element's dto mapping : DTO - " +
                dtoType + " : Validator Property - " + property + " : Key - " + key);
        //Checks whether the method instance is already present in local cache, if true then returns the same
        if (operationalRuleConfig.containsKey(key)) {
            Method method = (Method) operationalRuleConfig.get(key);
            knLogger.debug( methodName,
                    "Returning method from local cache :  Method - " + method);
            return method;
        } else { //creating the Method instance
            String dtoMethodName = KnValidatorConstants.GETTER + property;
            knLogger.debug( methodName, "Method name - " + dtoMethodName);

            try {
                Method method = KnClassLoader.getDeclaredMethod(dtoType, dtoMethodName);
                operationalRuleConfig.put(key, method);
                knLogger.debug( methodName,
                        "Creating & Returning method and puts in local cache :  Method - " + method);
                return method;
            } catch (KnClassLoaderException e) {
                throw new KnXDMSystemError(KnErrorCodes.Validator.INTERNAL_ERROR, "Method creation failed : DTO Type - " + dtoType + " : Method name - " + dtoMethodName, e);
            }
        }

    }

    /**
     * This method returns the iterator data type (keys/values), if the property attribute returns a Map instance
     *
     * @param ruleId
     * @return String
     */
    public String getIteratorDataType(String ruleId) {
        String methodName = "getIteratorDataType(String)";
        KnCacheElement validatorElement = null;
        //gets the validator element
        validatorElement = operationElement.getElement(ruleId);
        //Gets the iterate attribute of validator rule configured under the operation
        String iterateValue = validatorElement.getAttribute(KnValidatorConstants.SUB_PROPERTY);
        knLogger.debug( methodName, "Fetched validation element : Element = " +
                validatorElement.toString() + " : iterate value - " + iterateValue);
        return iterateValue;

    }

    /**
     * this will check the rule element is expression or rule. it returns true if it is expression
     *
     * @param ruleElement
     * @return boolean
     */
    private boolean isExpression(KnCacheElement ruleElement) {
        if (ruleElement.getAttribute(KnValidatorConstants.OPERATOR) == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * This method fetches the validator rule classes for an operation from the local cache, if
     * present and then creates the instance of validator rules. If the operation specific rules are not present in
     * the local cache, this method initiate the creation of validation rules and cache the loaded validation rule Class
     * instances.
     * @param validatorRuleElement
     * @return
     */
    private IValidatorRule populateValidatorRuleObject(KnCacheElement validatorRuleElement) {
        String methodName = "populateValidatorRuleObject(String)";
        IValidatorRule validatorRule = null;
        knLogger.debug( methodName, "Populate Rule Object : Rule = " +
                validatorRuleElement.getId());
        //Retrieves the operation rule attribute list for the validator rule
        KnCacheAttributes ruleAttributes = (KnCacheAttributes) validatorRuleElement.getAttributes();
        // Fetching the validator rule object for the validator id
        validatorRule = (IValidatorRule) populateValidatorConfig(validatorRuleElement, KnValidatorConstants.RULES_CONFIG);

        //sets rule id to the validator rule object.
        validatorRule.setRuleId(validatorRuleElement.getId());

        //Setting whether the rule must be executed for all dtos, though exception thrown for any dto
        String consolidateDtos = ruleAttributes.get(KnValidatorConstants.CONSOLIDATE_DTOS);
        if (consolidateDtos != null && KnValidatorConstants.TRUE.equals(consolidateDtos)) {
            validatorRule.setConsolidateDtos(true);
        }

        String iterate = ruleAttributes.get(KnValidatorConstants.ITERATE_BOOL_KEY);
        if (iterate != null && KnValidatorConstants.TRUE.equals(iterate)) {
            validatorRule.setIterate(true);
        }

        String iterateAttribute = ruleAttributes.get(KnValidatorConstants.ITERATE_ATTR_KEY);
        validatorRule.setIterateAttribute(iterateAttribute);
        knLogger.debug( methodName, "Validator rule object : " + validatorRule.toString());
        return validatorRule;
    }

    /**
     * @param validatorExp
     * @return
     */
    private IExpression populateExpressionObject(KnCacheElement validatorExp) {
        String methodName = "populateExpressionObject(KnCacheElement)";
        String id = validatorExp.getId();
        Collection rules = new ArrayList();
        Collection ruleElements = validatorExp.getElements();
        knLogger.debug( methodName, "Populate Expression Object : Expn -> " +
                validatorExp.getId());
        String operator = validatorExp.getAttribute(KnValidatorConstants.OPERATOR);
        IExpression exprn;
        if (!operator.equals(KnValidatorConstants.OR) && !operator.equals(KnValidatorConstants.AND)) {
            exprn = (IExpression) populateValidatorConfig(validatorExp, KnValidatorConstants.OPERATORS_CONFIG);
        } else {
            exprn = new KnDefaultOperator();
        }
        exprn.setId(id);
        exprn.setOperator(validatorExp.getAttribute(KnValidatorConstants.OPERATOR));

        //set consolidate rules to the expression
        if (KnValidatorConstants.TRUE.equals(validatorExp.getAttribute(KnValidatorConstants.CONSOLIDATE_RULES))) {
            exprn.setConsolidateRules(true);
        }

        //exprn.setErrorType(validatorExp.getAttribute(KnValidatorConstants.ERROR_TYPE));
        for (Iterator it = ruleElements.iterator(); it.hasNext();) {
            KnCacheElement ruleElement = (KnCacheElement) it.next();
            // checks the ruleElement is validatorExpression or validatorRule
            if (isExpression(ruleElement)) {
                // populates the validator expression object and save it as rule in expression
                rules.add(populateExpressionObject(ruleElement));
            } else {
                // populates the validate rule object with the attributes and params and save it as rule in expression
                IValidatorRule rule = populateValidatorRuleObject(ruleElement);
                rules.add(rule);
            }
        }
        // save all rules & nested expressions as rule to the expression
        exprn.setRules(rules);
        knLogger.debug( methodName, "Expression obj after populating : " + exprn);
        return exprn;
    }


    /**
     * This method fetches the validator rule classes for an operation from the local cache, if present
     * and then creates the instance of validator rules. If the operation specific rules/operations are not present in
     * the local cache, this method initiate the creation of validation rules and cache the loaded validation rule Class
     * instances.
     * @param ruleElement
     * @param configRefId
     * @return
     */
    private IRule populateValidatorConfig(KnCacheElement ruleElement, String configRefId) {
        String methodName = "populateValidatorConfig(KnCacheElement, String)";
        IRule validatorConfigObj = null;
        String validatorId;
        knLogger.debug( methodName, "Populate Validator Object : Config Object -> " +
                ruleElement.getId());
        //Retrieves the operation rule attribute list for the validator rule
        KnCacheAttributes ruleAttributes = (KnCacheAttributes) ruleElement.getAttributes();
        //Gets the instance of validator rule with parameters set
        validatorId = ruleAttributes.get(KnValidatorConstants.VALIDATOR);
        if (KnValidatorConstants.OPERATORS_CONFIG.equals(configRefId)) {
            validatorId = ruleAttributes.get(KnValidatorConstants.OPERATOR);
            if (validatorId.startsWith("!"))
                validatorId = validatorId.substring(1);
        }

        if (validatorId == null || validatorId.equals("")) {
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.CONFIG_ERROR, "Relevant Operator-Config or"+"" +
                    " Validator-Config id must be configured : Id : "+validatorId);
        }
        // Fetching the validator rule object for the validator id
        validatorConfigObj = genValidatorRules.getValidatorConfigObject(validatorId, configRefId);
        knLogger.debug( methodName, "Created validator rule object : Rule = " +
                validatorConfigObj.toString());
        //Sets the additional operation specific parameters to the validator rule
        Map validatorAttributes = validatorConfigObj.getAttributes();
        validatorConfigObj.setId(ruleElement.getId());
        //Fetching operational specific params set for validation rule
        KnCacheElement paramElement = ruleElement.getElement(KnValidatorConstants.PARAMS);
        if (paramElement != null) {
            KnCacheAttributes operationRuleParams = paramElement.getAttributes();
            validatorAttributes.putAll(operationRuleParams.getAttributesMap());
            knLogger.debug( methodName,
                    "Setting operation specific parameters : Params = " + paramElement.toString());
        }

        //Setting whether the throw on exception is true for a rule
        String throwOn = ruleAttributes.get(KnValidatorConstants.THROW_ON);
        if (throwOn != null && KnValidatorConstants.TRUE.equals(throwOn)) {
            validatorConfigObj.setThrowOn(true);
        }

        //Setting whether the throw on exception is true for a rule
        String errorType = ruleAttributes.get(KnValidatorConstants.ERROR_TYPE);
        if (errorType != null && !(errorType.equals(""))) {
            validatorConfigObj.setErrorType(errorType);
        }
        String property = ruleAttributes.get(KnValidatorConstants.VALIDATOR_PROPERTY);
        validatorConfigObj.setProperty(property);

        String subProperty = ruleAttributes.get(KnValidatorConstants.SUB_PROPERTY);
        validatorConfigObj.setSubProperty(subProperty);

        //Setting the rule is enabled or not
        String enabled = ruleAttributes.get(KnValidatorConstants.ENABLED);
        if (enabled == null || enabled.equals(""))
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.CONFIG_ERROR, "Attribute \"" + KnValidatorConstants.ENABLED +
                    "\" is not configured for the Operator/Rule config : config id - " +
                    ruleElement.getId() + " : validator id - " + validatorId);
        if (KnValidatorConstants.TRUE.equals(enabled)) {
            validatorConfigObj.setEnabled(true);
        }
        knLogger.debug( methodName, "Validator config object : " +
                validatorConfigObj.toString());
        return validatorConfigObj;
    }

    /**
     * Returns the corresponding method instance of a sub property configured for operation's validation rule.
     * The method instance will be returned from the local cache if its already loaded, else creates a new instance,
     * puts it in local cache and then returns the same. The method instance will be created as of a part of the propertyObj.
     * @param subProperty
     * @param propertyObj
     * @return
     */
    public Method getMethod(String subProperty, Object propertyObj) {
        String methodName = "getMethod(String, Object)";

        if (subProperty == null) {
            knLogger.debug( methodName, "Property is not configured : operation - " + getOperationKey() + " : rule id - " + subProperty);
            return null;
        }
        String dtoType = operationElement.getAttribute(KnValidatorConstants.DTO_TYPE);
        String key = dtoType + propertyObj + subProperty;
        knLogger.debug( methodName, "Operation element's dto mapping : DTO - " +
                propertyObj + " : Validator Property - " + subProperty + " : Key - " + key);
        //Checks whether the method instance is already present in local cache, if true then returns the same
        if (operationalRuleConfig.containsKey(key)) {
            Method method = (Method) operationalRuleConfig.get(key);
            knLogger.debug( methodName,
                    "Returning method from local cache :  Method - " + method);
            return method;
        } else { //creating the Method instance
            String dtoMethodName = KnValidatorConstants.GETTER + subProperty;
            knLogger.debug( methodName, "Method name - " + dtoMethodName);

            try {
                Method method = KnClassLoader.getDeclaredMethod(propertyObj.getClass().getName(), dtoMethodName);
                operationalRuleConfig.put(key, method);
                knLogger.debug( methodName,
                        "Creating & Returning method and puts in local cache :  Method - " + method);
                return method;
            } catch (KnClassLoaderException e) {
                throw new KnXDMSystemError(KnErrorCodes.Validator.INTERNAL_ERROR, "Method creation failed : DTO Type - " + propertyObj + " : Method name - " + dtoMethodName, e);
            }
        }
    }
}