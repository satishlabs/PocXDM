/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnValidatorFramework.java
 * Subsystem:   Validator Framework
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna        14-03-2007  6.0
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
import com.kodiak.xdms.server.common.business.KnBOException;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheElement;
import com.kodiak.xdms.server.common.dto.intf.IEntity;
import com.kodiak.xdms.server.common.dto.intf.IInputDTO;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.util.KnClassLoader;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This validator framework class is responsible  for creating or invoking the necessary validator objects
 * as and when required and as configured in validator framework's XML file, then delegates to corresponding
 * functional validation entities for the execution of different validation rules. There will be multiple
 * operations configured for different validation entities and each operation contains multiple validation
 * rules. The framework invokes the corresponding operation and its validation rules as perthe DTO passed
 * to it and as the configuration provided in validator XML file and execute the rules by sending the
 * it to the entities one by one.
 */
public class KnValidatorFramework {
	private static final KnLogger knLogger = KnLogger.getLogger(KnValidatorFramework.class);

    private static final String CLASS = KnValidatorFramework.class.getName();
    public static KnValidatorFramework validatorFramework;
    private boolean dataValidationEnabled = true;
    private boolean businessValiationEnabled = true;
    private boolean isBValidationsInitialized = false;
    private boolean isDValidationsInitialized = false;
    // holds the configuration manager object
    KnConfigurationsManager configManager = null;
    // stores the libraryName
    String libraryName = null;
    // instance map which holds the validator framework instances with the libraryName
    static Map<String, KnValidatorFramework> fwkInstanceMap;
    KnValidatorConfigCache configCache = null;

    static {
        // initializes the instance map
        fwkInstanceMap = new ConcurrentHashMap<String, KnValidatorFramework>();
    }

    /**
     * constructor
     * @param configMgr configuration manager object
     */
    private KnValidatorFramework(KnConfigurationsManager configMgr) {
        this.configManager = configMgr;
        this.configCache = new KnValidatorConfigCache(this.configManager);
    }

    /**
     * this method wil give an instance of the validator framework
     *
     * @return the validator framework object
     * @param libraryName   library name
     */
    public static KnValidatorFramework getInstance(String libraryName) {
//        knLogger.debug( "getInstance()", " Fwk Instance Map : " + fwkInstanceMap);
        KnValidatorFramework validatorFwk = fwkInstanceMap.get(libraryName);
        if (validatorFwk == null) {
            validatorFwk = new KnValidatorFramework(KnConfigurationsManager.getInstance(libraryName));
            fwkInstanceMap.put(libraryName, validatorFwk);
        }
        return validatorFwk;
    }

    /**
     * The framework's static validate method is responsible for initiating the execution
     * of different validation.
     *
     * @param persistDTO The DTO object which inherits IPersistenceDTO
     * @throws KnValidationException    throws bo validation exception for any failures in validation rules
     */
    public void validate(IPersistenceDTO persistDTO) throws
            KnValidationException {
        if (!isBValidationsInitialized) {
            configCache.setModuleName(KnValidatorConstants.MODULE);
            configCache.setParents(null);
            configCache.setId(KnValidatorConstants.BO_VALIDATION_TAG);
            KnCacheElement element = (KnCacheElement) configCache.getCachedValue(configCache);
            String enabled = element.getAttribute(KnValidatorConstants.ENABLED);
            if (KnValidatorConstants.FALSE.equals(enabled)) {
                this.businessValiationEnabled = false;
            }
            this.isBValidationsInitialized = true;
        }
        if (businessValiationEnabled) {
            validateDTO(persistDTO);
        }
    }

    /**
     * The framework's static validate method is responsible for initiating the execution
     * of different validation.
     *
     * @param inputDTO The DTO object which inherits IInputDTO
     * @throws KnValidationException    throws data validation exception
     */
    public void validate(IInputDTO inputDTO) throws
            KnValidationException {
        if (!isDValidationsInitialized) {
            configCache.setModuleName(KnValidatorConstants.DATA_MODULE);
            configCache.setParents(null);
            configCache.setId(KnValidatorConstants.DATA_VALIDATION_TAG);
            KnCacheElement element = (KnCacheElement) configCache.getCachedValue(configCache);
            String enabled = element.getAttribute(KnValidatorConstants.ENABLED);
            if (KnValidatorConstants.FALSE.equals(enabled)) {
                this.dataValidationEnabled = false;
            }
            this.isDValidationsInitialized = true;
        }
        if (dataValidationEnabled) {
            validateDTO(inputDTO);
        }
    }

    /**
     * This method invokes interfaces to get the validator entities for the ID provided in DTO,
     * fetch the corresponding operation and its configured rules as per the operation type
     * given in DTO.
     *
     * @param DTO The DTO object which inherits IEntity
     * @throws KnValidationException    throws validation Exception
     */
    private void validateDTO(IEntity DTO) throws
            KnValidationException {
        String methodName = "validateDTO (IEntity)";
        KnGenValidatorOperation operation;
        Collection rules;
        String entityId = DTO.getEntityId();
        String operationID = DTO.getOperationType();
        KnConsolidateValidationException cvex = null;
        knLogger.debug( methodName, "DTO for Validation : " + DTO);

        if (entityId == null || entityId.equals("")) {
            knLogger.error( methodName, "The DTO ID is null: ID =  " + entityId);
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.INTERNAL_ERROR, "The DTO entityId is null or empty");
        }
        if (operationID == null || operationID.equals("")) {
            knLogger.error( methodName, "The DTO Operation ID is null: ID =  " + operationID);
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.INTERNAL_ERROR, "The operation id in DTO is empty or null");
        }

        try {
            //checks the dto is instance of persistenceDTO. If it is dao dto then make the module as business validator
            // and call the business valiadations rule to validate.
            if (DTO instanceof IPersistenceDTO) {
                String moduleName = KnValidatorConstants.MODULE;
                String parents[] = {KnValidatorConstants.BO_VALIDATION_TAG, KnValidatorConstants.BO_ENTITIES_TAG};
                operation = getValidatorOperation(moduleName, parents, entityId, operationID);
                rules = operation.getValidatorRules();
            } else {
                //If DTO is not instance of persistenceDTO, then make the module as data validator and call data validation
                //rules for a input dto to validate.
                String moduleName = KnValidatorConstants.DATA_MODULE;
                String parents[] = {KnValidatorConstants.DATA_VALIDATION_TAG, KnValidatorConstants.DATA_ENTITIES_TAG};
                //Gets the validation rules by passing entity id and operation entityId
                operation = getValidatorOperation(moduleName, parents, entityId, operationID);
                rules = operation.getValidatorRules();
            }
            //Gets the boolean for consolidating all rules
            boolean consolidateRules = operation.hasToConsolidateRules();
            if (rules != null) {
                knLogger.debug( methodName, "Fetched Validation rules :" + rules.size());
                for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
                    IRule ruleObj = (IRule) iterator.next();
                    //Gets boolean for throw-on
                    boolean throwOn = ruleObj.isThrowOn();
                    // checks the ruleObj is of validator expression. If it so, validate the expression
                    try {
                        if (ruleObj instanceof IExpression) {
                            validateExpression((IExpression) ruleObj, operation, DTO);
                        } else { // validate the ruleObj
                            validateRule((IValidatorRule) ruleObj, operation, DTO);
                        }
                    } catch (KnValidationException vex) {
                        if (throwOn) throw vex;
                        if (consolidateRules)
                            cvex = addToConsolidateList(cvex, vex, entityId, operationID, KnValidatorConstants.CONSOLIDATE_RULES, null);
                        else
                            throw vex;
                    }
                } //End of  rules iteraion
                //CVEX will be thrown if any consolidated ex occured and the entire rule iterations happened.
                if (cvex != null && cvex.isErrorOccured()) throw cvex;
            } 
        } catch (KnValidationException vex) {
            throw vex;
        } catch (Exception ex) {
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.INTERNAL_ERROR, "exception : " + ex.getMessage(), ex);
        } finally {
            entityId = null;
            operationID = null;
            rules = null;
        }
    }

    /**
     * Creates a new consolidate exception if not present and adds the validation exception to the consolidated list
     * and returns the same cosolidate validation exception.
     *
     * @param cvex        consolidate validation exception
     * @param vex         validation exception
     * @param entityId
     * @param operationID
     * @param errorType
     * @return throws KnConsolidationException
     */
    private KnConsolidateValidationException addToConsolidateList(KnConsolidateValidationException cvex,
                                                                  KnValidationException vex, String entityId,
                                                                  String operationID, String rule, String errorType) {
        knLogger.debug( "addToConsolidateList", " Validation Exception : " + vex + ", CVEX : " + cvex);
        if (cvex == null) {
            String errorCode = KnErrorCodes.Validator.CONS_BO_VAL_ERRORS;

            if (vex instanceof KnConsolidateValidationException) { //Processing a consolidate Exception (Can be con-exprn or cons-rules)
                //If consolidate exception, assigning the same error code for CONSOLIDATE_BO_VAl_ERRORS
                errorCode = vex.getErrorCode();
                //If Consolidate Ex is of expression, and this cons-expn-errror needs to be added as a part of main rules,
                //then assigning the error code with  CONS_DATA_VAL_ERRORS Or CONS_BO_VAL_ERRORS (Rule level errors)
                //appropriately (data val error or bo val error respectively).
                if (KnValidatorConstants.CONSOLIDATE_RULES.equals(rule)) {
                    if (KnErrorCodes.Validator.CONS_DATA_VAL_EXPRN_ERRORS.equals(errorCode))
                        errorCode = KnErrorCodes.Validator.CONS_DATA_VAL_ERRORS;
                    else if (KnErrorCodes.Validator.CONS_BO_VAL_EXPRN_ERRORS.equals(errorCode))
                        errorCode = KnErrorCodes.Validator.CONS_BO_VAL_ERRORS;
                }
                //If Consolidate Ex is of rules or dtos, and this cons-rule-errror needs to be added as a part of expression ex,
                //then assigning the error code with  CONS_DATA_VAL_EXPRN_ERRORS Or CONS_BO_VAL_EXPRN_ERRORS (Exprn level errors)
                //  appropriately (data val error or bo val error respectively).
                else if (KnValidatorConstants.CONSOLIDATE_EXPRESSION.equals(rule)) {
                    if (KnErrorCodes.Validator.CONS_DATA_VAL_ERRORS.equals(errorCode))
                        errorCode = KnErrorCodes.Validator.CONS_DATA_VAL_EXPRN_ERRORS;
                    else if (KnErrorCodes.Validator.CONS_BO_VAL_ERRORS.equals(errorCode))
                        errorCode = KnErrorCodes.Validator.CONS_BO_VAL_EXPRN_ERRORS;
                } // For all other scenarios set the error code as it is present in the consolidate EX.
//            } else if (vex instanceof KnSCDataValidationException) {  //Processing Data Validation Exception
                //Assigning CONS_DATA_VAL_ERRORS as default assuming it as a rule level consolidation
                errorCode = KnErrorCodes.Validator.CONS_DATA_VAL_ERRORS;
                //Assigning CONS_DATA_VAL_EXPRN_ERRORS if its an expression level consolidation
                if (KnValidatorConstants.CONSOLIDATE_EXPRESSION.equals(rule))
                    errorCode = KnErrorCodes.Validator.CONS_DATA_VAL_EXPRN_ERRORS;
            } else {//Processing Validation Exception
                //Assigning CONS_BO_VAL_EXPRN_ERRORS if its an expression level consolidation :
                //  If not CONS_BO_VAL_ERRORS is already been assigned as error code.
                if (KnValidatorConstants.CONSOLIDATE_EXPRESSION.equals(rule))
                    errorCode = KnErrorCodes.Validator.CONS_BO_VAL_EXPRN_ERRORS;
            }
            //Creating new consolidate exception with appropriate error ciode.
            cvex = new KnConsolidateValidationException(errorCode,
                    "Multiple validation failures occured for operation  - " + entityId + "." + operationID, rule, errorType);
        }
        //Adding the exception to the consolidated ex.
        cvex.addValidationError(vex);
        return cvex;
    }

    /**
     * This method provides the reference of a particular operation in turn can be used for accessing different
     * validation rules configured under each operation. It identifies each operation by using using the entityID and
     * the operationID passed to it and creates the instance of the operation object.
     *
     * @param entityID
     * @param operationID
     * @return KnGenValidatorOperation
     */
    private KnGenValidatorOperation getValidatorOperation(String moduleName, String[] parents, String entityID,
                                                         String operationID)
            throws KnValidationException {
        return new KnGenValidatorOperation(this.configManager, moduleName, parents, entityID, operationID);
    }

    /**
     * @param rule
     * @param DTO
     * @return
     */
    private IRule setRuleProperties(IRule rule, IEntity DTO) {
        rule.setEntityId(DTO.getEntityId());
        rule.setOperationType(DTO.getOperationType());
        rule.setProfile(DTO.getProfile());
        return rule;
    }

    /**
     * This method is used to validating rule object.
     *
     * @param rule
     * @param operation
     * @param DTO
     * @throws KnValidationException
     */
    private void validateRule(IValidatorRule rule, KnGenValidatorOperation operation, IEntity DTO) throws KnValidationException {
        String methodName = "validateRule(rule, operation, DTO)";
        knLogger.debug( methodName, "Validating Rule - " + rule.getRuleId() +
                ", Operation - " + operation.getOperationKey());

        try {
            if (rule != null && rule.isEnabled()) {

                String ruleId = rule.getRuleId();
                rule = (IValidatorRule) setRuleProperties(rule, DTO);

                Method method = operation.getMethod(rule.getProperty());

                Object propertyObj = null;
                //Assigning parent dto reference
                rule.setParentDTO(DTO);
                knLogger.debug( methodName, "Setting parent DTO object to the validation rule : Parent DTO: " + DTO);
                //Gets boolean for fetching sub property during iteration
                boolean subPropDurIterate = false;
                //Checks any sub property configured
                String subProperty = rule.getSubProperty();
                if (method == null) {
                    propertyObj = DTO;
                    knLogger.debug( methodName, "Setting DTO property object defaulted to given DTO for validating : " + propertyObj);
                } else {
                    //Assigning the child property reference
                    propertyObj = KnClassLoader.invoke(method, DTO);
                    knLogger.debug( methodName, "Sub Property config value retrieved : " + subProperty);
                    if (subProperty != null) {
                        //If property obj is an instance of Map, assuming that sub-property must be keys/values else throws exception
                        if (propertyObj instanceof Collection || propertyObj instanceof Map) {
                            //Skipping subproperty invokation and sets to invoke sub property while iteration
                            subPropDurIterate = true;
                        } else {//Other than map | collection, it will try to invoke the getter method of the sub-property
                            //Assigning the child property reference
                            propertyObj = getSubProperty(operation, subProperty, propertyObj);
                        }
                    }
                } //End  of method invokation

                //Setting appropriate objects in rule & validates
                if (propertyObj instanceof IEntity) {
                    knLogger.debug( methodName, "Invoking validate method of rule, Rule Id : " + ruleId + " : Sets DTO : " + propertyObj);
                    rule.setDTO((IEntity) propertyObj);
                    try {
                        rule.validate();
                    } catch (KnValidationException vex) {
                        knLogger.error( methodName, "Validation failed : Rule - " + rule.getObjectId());
                        throw vex;
                    }
                }
                //Checks dto Property object is of type Collection
                else if (propertyObj instanceof Collection && rule.doIterate()) {
                    Iterator iterator = ((Collection) propertyObj).iterator();

                    //Iterating through dtos
                    iterateCollection(operation, rule, iterator, subPropDurIterate);
                }
                //Checks Property object is of type Map
                else if (propertyObj instanceof Map && rule.doIterate()) {
                    Iterator iterator = ((Map) propertyObj).keySet().iterator();
                    String iteratorAttr = rule.getIterateAttribute();
                    knLogger.debug( methodName, "Iterate thru the map based on the iterator key : " + iteratorAttr);
                    if (KnValidatorConstants.ITERATE_KEYS.equals(iteratorAttr))
                        iterator = ((Map) propertyObj).keySet().iterator();
                    else if (KnValidatorConstants.ITERATE_VALUES.equals(iteratorAttr))
                        iterator = ((Map) propertyObj).values().iterator();
                    else if (iteratorAttr != null && !(iteratorAttr.equals(""))) {
                        throw new KnXDMServerSystemException(KnErrorCodes.Validator.CONFIG_ERROR, "Attribute " +
                                KnValidatorConstants.ITERATE_ATTR_KEY + " must be set with " +
                                KnValidatorConstants.ITERATE_VALUES + " OR " + KnValidatorConstants.ITERATE_KEYS + "for Map.");
                    }

                    //Iterating through dtos
                    iterateCollection(operation, rule, iterator, subPropDurIterate);
                } //End of map check
                else {
                    //Checks the dto property object. If instance of dao dto setting it as it is to  the rule.
                    knLogger.debug( methodName, "Invoking validate method of rule, Rule Id : " + ruleId + " : Sets Object : " + propertyObj);
                    rule.setObject(propertyObj);
                    try {
                        rule.validate();
                    } catch (KnValidationException vex) {
                        knLogger.warn( methodName, "Validation failed : Rule - " + rule.getObjectId());
                        throw vex;
                    }
                }
            } // end of check rule enabled or not
        } catch (KnValidationException vex) {
            throw vex;
        } catch (Exception ex) {
            if (ex instanceof KnXDMServerSystemException) throw (KnXDMServerSystemException) ex;
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.INTERNAL_ERROR, "UnExpected Exception occured : ", ex);
        } finally {
            rule = null;
        }
    }

    /**
     * This method is used to invoke the sub-property of the properyObj.
     *
     * @param operation   validator operation
     * @param subProperty sub-property object
     * @param propertyObj property object
     * @return Object      returns the value of sub-property object
     */
    private Object getSubProperty(KnGenValidatorOperation operation, String subProperty, Object propertyObj) {
        String methodName = "getSubProperty(KnGenValidatorOperation,String,String)";
        Object subPropObject = propertyObj;
        try {
            Method subMethod = operation.getMethod(subProperty, propertyObj);
            if (subMethod != null) {
                //Assigning the child property reference
                subPropObject = KnClassLoader.invoke(subMethod, propertyObj);
                knLogger.debug( methodName, "Sub Property object retrieved : " + subPropObject + ", Property Obj: " + propertyObj);
            }
            return subPropObject;
        } catch (Exception ex) {
            if (ex instanceof KnXDMServerSystemException) throw (KnXDMServerSystemException) ex;
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.INTERNAL_ERROR, "UnExpected Exception occured : ", ex);
        }
    }

    /**
     * This method is used to validate expression based on the operator.
     *
     * @param validatorExp validating expression object
     * @param operation    operation object
     * @param DTO entity object
     * @throws KnValidationException throws approporiate validation exceptions.
     */
    private void validateExpression(IExpression validatorExp, KnGenValidatorOperation operation,
                                    IEntity DTO) throws KnValidationException {
        String methodName = "validateExpression(IExpression, KnGenValidatorOperation, IEntity)";
        boolean statusFlag = true;
        Collection rules = null;
        boolean consolidateRules = validatorExp.hasToConsolidateRules();
        try {
            String operator = validatorExp.getOperator();
            knLogger.debug( methodName, "Validation of Expression : " + validatorExp.getId() +
                    ", Operator : " + validatorExp.getOperator());
            boolean evaluateRules = true;
            if (!operator.equals(KnValidatorConstants.OR) && !operator.equals(KnValidatorConstants.AND)) {
                //Evaluate expression
                evaluateRules = evaluateExpression(operation, validatorExp, DTO);
            }

            //Evaluating rules under expression
            rules = validatorExp.getRules();
            KnConsolidateValidationException cvex = null;
            boolean throwOn = false;
            if (rules != null && evaluateRules) {
                knLogger.debug( methodName, "Fetched Validation rules :" + rules.size());
                for (Iterator it = rules.iterator(); it.hasNext();) {
                    IRule ruleObj = (IRule) it.next();
                    throwOn = ruleObj.isThrowOn();
                    try {
                        // checks the ruleObj is instanceof validator expression
                        if (ruleObj instanceof IExpression) {
                            // validate expression
                            validateExpression((IExpression) ruleObj, operation, DTO);
                        } else {
                            //Gets boolean for throw-on
                            // validate rule with persistenceDTO
                            validateRule((IValidatorRule) ruleObj, operation, DTO);
                        }
                        // if the operator is "or" then any of the expression / rule is passed then it get it out...
                        if (KnValidatorConstants.OR.equals(operator)) return;
                    } catch (KnValidationException vex) {
                        knLogger.debug( methodName, "Exception : " + vex);
                        statusFlag = false;
                        // if operator is "and" then throw the expection
                        if (throwOn) throw vex;
                        if (KnValidatorConstants.OR.equals(operator) || ((KnValidatorConstants.AND.equals(operator)) && (consolidateRules))) {
                            cvex = addToConsolidateList(cvex, vex, DTO.getEntityId(), DTO.getOperationType(), KnValidatorConstants.CONSOLIDATE_EXPRESSION, validatorExp.getErrorType());
                        } else
                            throw vex;
                    }
                }
            } else {
                knLogger.debug( methodName, "No Rules configured for Expression " + validatorExp.getId());
            }

            // check any expression is caught in the expression by status flag and if it so throw exception
            if (!statusFlag) {
                throw cvex;
            }
        } catch (KnValidationException vex) {
            throw vex;
        } catch (Exception ex) {
            if (ex instanceof KnXDMServerSystemException) throw (KnXDMServerSystemException) ex;
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.INTERNAL_ERROR, "UnExpected Exception occured : ", ex);
        }
    }


    /**
     * This method is used to generate the key of expression.
     * It will generate key will be opeartor-id + dto-type + property of dto-type which if it is there + sub-property of
     * property obj. (key = operator + dto-type + property + sub-property)
     *
     * @param operation validator operation
     * @param rule      rule obj
     * @return String    returns key of expression
     */
    private String generateKey(KnGenValidatorOperation operation, IRule rule) {
        String key = null;
        String dtoType = operation.operationElement.getAttribute(KnValidatorConstants.DTO_TYPE);
        String operator = ((IExpression) rule).getOperator();
        if (operator.startsWith("!"))
            operator = operator.substring(1);
        key = operator + dtoType;
        String property = rule.getProperty();
        if (property != null) {
            key = key + property;
            String subProperty = rule.getSubProperty();
            if (subProperty != null)
                key = key + subProperty;
        }
        return key;
    }

    /**
     * This is helper method is used to iterate thru the iterator and invoke the corresponding validation rule.
     * if any validation rule failed, then it will throws the appropriate exception. Based on the configuration,
     * exception will be consolidate otherwise throw it to upper level.
     *
     * @param operation         validator operation object
     * @param rule              rule object
     * @param iterator          iterator
     * @param subPropDurIterate flag for checking whether to invoke the sub-property object or not.
     * @throws KnValidationException validation exception
     * @throws KnBOException 
     */
    private void iterateCollection(KnGenValidatorOperation operation, IValidatorRule rule, Iterator iterator, boolean subPropDurIterate) throws KnValidationException, KnBOException {
        String methodName = "iterateCollection(KnGenValidatorOperation, IValidatorRule, Iterator, KnConsolidate, boolean)";
        String subProperty = rule.getSubProperty();
        boolean consolidateDtos = rule.hasToConsolidateDtos();
        String entityId = rule.getEntityId();
        KnConsolidateValidationException cvex = null;
        knLogger.debug( methodName, "Iterate thru Collection of dtos for rule : " + rule.getRuleId());
        for (; iterator.hasNext();) {
            IEntity dto = null;
            Object subObject = iterator.next();
            //checks the flag for invocation of sub-property object
            if (subPropDurIterate) {
                subObject = getSubProperty(operation, subProperty, subObject);
            }
            //checks subObject is instance of IEntity to set it to DTO otherwise set it to object
            if (subObject instanceof IEntity) {
                dto = (IEntity) subObject;
                knLogger.debug( methodName, "Invoking validate method of rule, Rule Id : " + rule.getRuleId() + " : Sets DTO : " + dto);
                rule.setDTO(dto);
            } else {
                knLogger.debug( methodName, "Invoking validate method of rule, Rule Id : " + rule.getRuleId() + " : Sets Object : " + subObject);
                //set the object to the rule object.
                rule.setObject(subObject);
            }
            try {
                //validate the rule obj
                rule.validate();
            } catch (KnValidationException vex) {
                knLogger.warn( methodName, "Validation failed : Rule - " + rule.getObjectId());
                //If consolidate dto exceptions required then consolidates the vex else breaking the dto iteration and throws the vex.
                if (consolidateDtos)
                    cvex = addToConsolidateList(cvex, vex, entityId, rule.getOperationType(), rule.getRuleId(), rule.getErrorType());
                else
                    throw vex;
            }

            knLogger.debug( methodName, "Invoked validate method of rule, Rule Id : " + rule.getRuleId() + " : Sets DTO : " + dto);
        } // End of dto iterations

        //If consolidate-dto exceptions is true and exceptions have been consolidated, then
        // the cvex will be thrown after the dto iteration & at the rule level if throw-on is true
        //  or consolidate all rules is false
        if (cvex != null && cvex.isErrorOccured()) {
            throw cvex;
        }
    }

    /**
     * Returns the status of expression as true / false.
     * These status will be returned from the local cache if its already loaded, else evaluate expression and
     * puts status it in local cache and then returns the same.
     *
     * @param operation    operation
     * @param validatorExp validator expression
     * @param DTO entity
     * @return boolean     returns the status of expression as true / false.
     */
    private boolean evaluateExpression(KnGenValidatorOperation operation, IExpression validatorExp, IEntity DTO) {
        String methodName = "evaluateExpression(KnGenValidatorOperation, IExpression, IEntity)";
        String operator = validatorExp.getOperator();
        String key = generateKey(operation, validatorExp);
        //Checks whether the expression is already evaluated & cached.
        //If not evaluating the exprn for same dtotype+operator+property+subproperty
        Object ruleResultCache = operation.containsResult(key);
        boolean expnResult = false;

        try {
            //if resultCache is not there, then evaluate the expression and add the result to the ruleResultMap.
            if (ruleResultCache == null) {
                validatorExp = (IExpression) setRuleProperties(validatorExp, DTO);
                //get the method of the property in grpProvDTO object.
                Method method = operation.getMethod(validatorExp.getProperty());

                Object propertyObj = null;
                //Assigning parent dto reference
                validatorExp.setParentDTO(DTO);
                knLogger.debug( methodName, "Setting parent DTO object to the validation rule : Parent DTO: " + DTO);

                if (method == null) {
                    propertyObj = DTO;
                    knLogger.debug( methodName, "Setting DTO property object defaulted to given DTO for validating : " + propertyObj);
                } else {
                    //Assigning the child property reference
                    propertyObj = KnClassLoader.invoke(method, DTO);
                    String subProperty = validatorExp.getSubProperty();
                    knLogger.debug( methodName, "Sub Property config value retrieved : " + subProperty);
                    if (subProperty != null) {
                        //Assigning the child property reference
                        propertyObj = getSubProperty(operation, subProperty, propertyObj);
                    }
                }
                if (propertyObj instanceof IEntity) {
                    validatorExp.setDTO((IEntity) propertyObj);
                } else
                    validatorExp.setObject(propertyObj);
                expnResult = validatorExp.evaluate();
                //Adding to the cache
                operation.addRuleResult(key, new Boolean(expnResult));
            } //End  of method invokation
            else {//taking exprn result from the cache
                expnResult = ((Boolean) ruleResultCache).booleanValue();
            }
            //Negating result if its a NOT operator
            if (operator.startsWith("!"))
                expnResult = !expnResult;
            return expnResult;
        } catch (Exception ex) {
            if (ex instanceof KnXDMServerSystemException) throw (KnXDMServerSystemException) ex;
            throw new KnXDMServerSystemException(KnErrorCodes.Validator.INTERNAL_ERROR, "UnExpected Exception occured : ", ex);
        }
    }
}
