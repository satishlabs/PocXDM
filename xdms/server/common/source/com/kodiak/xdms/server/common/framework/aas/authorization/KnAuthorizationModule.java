/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   KnAuthorizationModule.java
 * Subsystem:   WGP
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Jiji Sasidharan      19/10/2006 5.7
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
package com.kodiak.xdms.server.common.framework.aas.authorization;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerSystemException;
import com.kodiak.xdms.server.common.KnXDMSystemError;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheElement;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheAttributes;
import com.kodiak.xdms.server.common.configuration.cache.KnCacheInvocationException;
import com.kodiak.xdms.server.common.configuration.KnConfigurationException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.framework.aas.KnAASConstants;
import com.kodiak.xdms.server.common.framework.aas.KnAASException;
import com.kodiak.xdms.server.common.util.KnClassLoader;
import com.kodiak.xdms.server.common.util.KnClassLoaderException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class KnAuthorizationModule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnAuthorizationModule.class);

    public static final String CLASS = KnAuthorizationModule.class.getName();
    private boolean isAuthorizationEnabled = false;

    // store rules config map
    private Map <String, KnAuthorizationRuleConfig> authorizationRulesConfigMap;
    // store entities map
    private Map <String, KnAuthorizationProfileConfig> authorizationProfilesMap;

    /**
     * initialize the authorization module.
     * @param authorizationElement
     * @throws KnAuthorizationException
     */
    public void init(KnCacheElement authorizationElement) throws KnAuthorizationException {
        String mName = "init";
        try {
            knLogger.info( mName, "Initializing Authorization Framework...");
            if (authorizationElement == null) {
                knLogger.error( mName, "Authorization Framework Configuration Missing..");
                throw new KnXDMServerSystemException(KnErrorCodes.Authorizer.CONFIG_ERROR, "Authorization Framework Configuration Missing");
            }

            Collection subElements = authorizationElement.getElements();
            knLogger.debug( mName, "Cached Elements retrived for authorization module. Elements : " + subElements);

            String enabledAttribute = authorizationElement.getAttribute(KnAASConstants.ENABLED);
            knLogger.debug( mName, "Framework enabled status : " + enabledAttribute);
            if (KnAASConstants.TRUE.equals(enabledAttribute)) {
                isAuthorizationEnabled = true;
            } else {
                knLogger.info( mName, "Authorization Framework disabled");
                isAuthorizationEnabled = false;
                return;
            }

            //hmm.. its the time to populate the rules configurations. it shud be
            //done first since profile have a reference to this configuration
            KnCacheElement rulesConfig = authorizationElement.getElement(KnAASConstants.RULES_CONFIG);
            knLogger.debug( mName, "Populating rules configurations... rules-config : " + rulesConfig);
            populateAuthorizationRulesConfig(rulesConfig);
            knLogger.debug( mName, "Populated rules configurations successfully.");

            //yes.. rules config done. now ball is with profiles. go ahead...
            KnCacheElement authEntity = authorizationElement.getElement(KnAASConstants.AUTH_PROFILES);
            knLogger.debug( mName, "Populating entities... auth-entities : " + rulesConfig);
            populateAuthorizationProfiles(authEntity);
            knLogger.debug( mName, "Populated authorization profiles successfully.");

            knLogger.info( mName, "Authorization Framework initialized successfully.");
        } catch (KnAuthorizationException e) {
            throw e;
        } catch (KnCacheInvocationException e) {
            knLogger.error( mName, "Cache Invocation error while initializing Aurthorization framework - " + e);
            throw new KnXDMServerSystemException(KnErrorCodes.Authorizer.INTERNAL_ERROR, "Error while initializing Authorization framework - " + e.getMessage(), e);
        } catch (KnConfigurationException e) {
            knLogger.error( mName, "Configuration error while initializing Aurthorization framework - " + e);
            throw new KnXDMServerSystemException(KnErrorCodes.Authorizer.INTERNAL_ERROR, "Error while initializing Authorization framework - " + e.getMessage(), e);
        } catch (Exception e) {
            knLogger.error( mName, "Error while initializing Aurthorization framework - " + e);
            throw new KnXDMServerSystemException(KnErrorCodes.Authorizer.INTERNAL_ERROR, "unexpected error - " + e.getMessage(), e);
        }
    }

    /**
     * Populate the rule configurations. All the configurations will be stored in authorizationRulesConfigMap
     * with rule id as the key and KnAuthorizationRuleConfig as the value.
     *
     * @param rulesConfigElement
     * @throws Exception
     */
    private void populateAuthorizationRulesConfig(KnCacheElement rulesConfigElement) throws Exception {
        String mName = "populateAuthorizationRulesConfig";
        knLogger.debug( mName, "Reading auth-rules config rules...");
        Collection elements = rulesConfigElement.getElements();
        for (Object element : elements) {
            KnCacheElement ruleElement = (KnCacheElement) element;
            KnCacheAttributes attrs = ruleElement.getAttributes();
            String id = ruleElement.getId();
            String className = attrs.get(KnAASConstants.CLASS_NAME);

            KnCacheElement paramsElement = ruleElement.getElement(KnAASConstants.PARAMETERS);
            KnAuthorizationRuleConfig authorizationRuleConfig = new KnAuthorizationRuleConfig();
            authorizationRuleConfig.setAuthRuleId(id);
            authorizationRuleConfig.setClassName(className);
            authorizationRuleConfig.setRuleClass(KnClassLoader.loadClass(className));
            if (paramsElement != null) {
                authorizationRuleConfig.setRuleParameters(paramsElement.getAttributesMap());
            }
            knLogger.debug( mName, "Rule Config Read. Id : " + id + ", class name : " + className);
            this.authorizationRulesConfigMap.put(id, authorizationRuleConfig);
        }
        knLogger.debug( mName, "auth-rules config rules read successfully.");
    }

    /**
     * Populate the profiles and the operations associated with it. The profile configurations
     * will be stored in authorizationProfilesMap with profile id as key and KnAuthorizationProfileConfig as value.
     * Each KnAuthorizationProfileConfig will have a IProfileAuthorizer instance which will take care
     * all the profile level preprocessing.
     *
     * @param authProfilesElement
     */
    private void populateAuthorizationProfiles(KnCacheElement authProfilesElement) {
        String mName = "populateAuthorizationProfiles";
        knLogger.debug( mName, "Reading Profile configurations...");
        Collection entities = authProfilesElement.getElements();
        for (Iterator it = entities.iterator(); it.hasNext();) {
            KnCacheElement profile = (KnCacheElement) it.next();
            KnAuthorizationProfileConfig authorizationProfileConf = populateAuthorizationProfileConfig(profile);
            knLogger.debug( mName, "Profile config read. Id : " + profile + ", ProfConfig : " + authorizationProfileConf);
            this.authorizationProfilesMap.put(profile.getId(), authorizationProfileConf);
        }
        knLogger.debug( mName, "entity configurations read successfully.");
    }

    /**
     * Populate the profile configuration. It will read the profile configuration and populate
     * KnAuthorizationProfileConfig. This method is executed like
     *
     * ..for each operation in profile
     * ....read the list of rules.
     * ....for each rule in the rules list
     * ......create and populate IAuthorizationRule and add to rule object list
     * ....create KnAuthorizationOperationConfig, set rule object list and add to operation config list
     * ..create KnAuthorizationProfileConfig and set operation config list
     *
     * @param profileElement the profile element
     * @return the authorization profilr class.
     */
    private KnAuthorizationProfileConfig populateAuthorizationProfileConfig(KnCacheElement profileElement) {
        String mName = "populateAuthorizationProfileConfig";
        String profileId = profileElement.getId();
        knLogger.debug( mName, "Auth profile -> " + profileId);
        KnCacheAttributes attributes = profileElement.getAttributes();

        // if profile auth class is configured, then take that class.
        // otherwise take the default implementation
        IProfileAuthorizer profAuthorizer = null;
        if (attributes == null || attributes.get(KnAASConstants.CLASS_NAME) == null) {
            knLogger.debug( mName, "Taking default Profile Authorizer");
            profAuthorizer = new KnProfileAuthorizer();
        } else {
            String profAuthClass = attributes.get(KnAASConstants.CLASS_NAME);
            try {
                profAuthorizer = (IProfileAuthorizer) KnClassLoader.createInstance(profAuthClass);
            } catch (KnClassLoaderException e) {
                knLogger.error( mName, "Cannot create profile authorizer for " + profileId);
                throw new KnXDMSystemError(KnErrorCodes.Authorizer.INTERNAL_ERROR, "Cannot create profile authorizer for " + profileId, e);
            }
        }

        // Read Global rules for the profile first.
        knLogger.debug( mName, "Reading global rules configurations...");
        Map globalRuleConfMap = getGlobalRulesOfProfile(profileElement);

        // Now iterate the operations list and create operation config for each operation.
        // map for storing the operations
        Map operationMap = new HashMap();

        // Get the list of operations.
        Collection opElements = profileElement.getElements();
        // Iterate operation elements and get the rules for each operation
        for (Iterator opIt = opElements.iterator(); opIt.hasNext();) {
            KnCacheElement operation = (KnCacheElement) opIt.next();
            String operationId = operation.getId();
            knLogger.debug( mName, "Populating Operation Config for operation '" + operationId + "'...");

            KnAuthorizationOperationConfig operationConfig = new KnAuthorizationOperationConfig();
            operationConfig.setProfile(profileId);
            operationConfig.setId(operationId);
            operationConfig.setAuthenticationRequired(operation.getAttribute(KnAASConstants.AUTHENTICATION_REQUIRED));

            knLogger.debug( mName, "Setting global rules to operation config...");
            for (Iterator globalRuleIt = globalRuleConfMap.keySet().iterator(); globalRuleIt.hasNext();) {
                String globalRuleId = (String) globalRuleIt.next();
                KnAuthorizationRuleConfig globalRuleConfig = (KnAuthorizationRuleConfig) globalRuleConfMap.get(globalRuleId);
                IAuthorizationRule ruleObj = getAuthRuleObj(profileId, operationId, globalRuleId, globalRuleConfig);
                knLogger.debug( mName, "Adding global rule to operation. rule Obj " + ruleObj);
                operationConfig.addRule(ruleObj);
            }

            //Get All rules configured for the operation
            Collection ruleElementList = operation.getElements();
            // Iterate rule config list
            for (Iterator ruleIt = ruleElementList.iterator(); ruleIt.hasNext();) {
                KnCacheElement ruleElement = (KnCacheElement) ruleIt.next();
                KnCacheAttributes ruleAttributes = ruleElement.getAttributes();

                String opRuleId = ruleElement.getId();
                String enabled = ruleAttributes.get(KnAASConstants.ENABLED);
                // leave the rule if its not enabled
                if (!KnAASConstants.TRUE.equals(enabled)) {
                    knLogger.debug( mName, "Leaving Rule since it is disabled. operation : " + operationId + ", ruleId : " + opRuleId + ", enabled : " + enabled);
                    continue;
                }
                String authRuleId = ruleAttributes.get(KnAASConstants.AUTH_RULE_ID);
                KnCacheElement parameters = ruleElement.getElement(KnAASConstants.PARAMETERS);

                knLogger.debug( mName, "Creating Rule Config - operation : " + operationId + ", RuleId : " + opRuleId + ", Auth RuleId : " + authRuleId + ", enabled : " + enabled);

                if (authRuleId == null) {
                    KnAuthorizationRuleConfig globalRuleConfig = (KnAuthorizationRuleConfig) globalRuleConfMap.get(opRuleId);
                    if (globalRuleConfig == null) {
                        knLogger.error( mName, "Configuration for " + KnAASConstants.AUTH_RULE_ID + " is missing or invalid. profile : "
                                + profileId + ", operation : " + operation + ", rule : " + opRuleId);
                        throw new KnXDMServerSystemException(KnErrorCodes.Authorizer.CONFIG_ERROR, "Configuraion for " + KnAASConstants.AUTH_RULE_ID + " is missing or invalid. profile : "
                                + profileId + ", operation : " + operation + ", rule : " + opRuleId);
                    } else {
                        knLogger.debug( mName, "Geting " + KnAASConstants.AUTH_RULE_ID + " from Global Operation rule configurations");
                        authRuleId = globalRuleConfig.getAuthRuleId();
                    }
                }

                IAuthorizationRule ruleObj = getAuthRuleObj(profileId, operationId, opRuleId, authRuleId);

                // Now, Add overidden rule parameters
                if (parameters != null) {
                    ruleObj.addRuleParameters(parameters.getAttributesMap());
                }
                knLogger.debug( mName, "Rule Object created : " + ruleObj);
                operationConfig.addRule(ruleObj);
            }
            knLogger.debug( mName, "Operations config successfully read for " + operationId + ", OperationConfig : " + operationConfig);
            operationMap.put(operationId, operationConfig);
        }
        profAuthorizer.setOperations(operationMap);

        //create a configuration for profile.
        KnAuthorizationProfileConfig profileConfig = new KnAuthorizationProfileConfig();
        profileConfig.setProfileAuthorizer(profAuthorizer);
        return profileConfig;
    }

    /**
     * Read the global rule configurations
     * @param profileElement
     * @return map of all gloabl rules
     */
    private Map getGlobalRulesOfProfile(KnCacheElement profileElement) {
        String mName = "getGlobalRulesOfProfile";
        Map globalRuleConfMap = new HashMap();
        KnCacheElement globalRulesElement = profileElement.getElement(KnAASConstants.GLOBAL_RULES);
        // if global rules are not configured, return an empty map.
        if (globalRulesElement == null) {
            return globalRuleConfMap;
        }

        Collection globalRules = globalRulesElement.getElements();
        for (Iterator it = globalRules.iterator(); it.hasNext();) {
            KnCacheElement ruleElement = (KnCacheElement) it.next();
            String ruleId = ruleElement.getId();
            String authRuleId = ruleElement.getAttribute(KnAASConstants.AUTH_RULE_ID);
            String enabled = ruleElement.getAttribute(KnAASConstants.ENABLED);
            knLogger.debug( mName, "Global Rule Id : " + ruleId + ", Auth Rule Id : " + authRuleId + ", enabled : " + enabled);
            if (!KnAASConstants.TRUE.equals(enabled)){
                knLogger.info( mName, "Global Rule Id : " + ruleId + " disabled. Processing Next Rule...");
                continue;
            }

            // Get the rule configuration for this rule
            KnAuthorizationRuleConfig defRuleConfig = authorizationRulesConfigMap.get(authRuleId);
            KnAuthorizationRuleConfig globalRuleConfig;
            try {
                globalRuleConfig = (KnAuthorizationRuleConfig)defRuleConfig.clone();
            } catch (Exception e) {
                knLogger.warn( mName, "Exception while cloning. Creating new instance for global rule configuration");
                globalRuleConfig = new KnAuthorizationRuleConfig();
                globalRuleConfig.setClassName(defRuleConfig.getClassName());
                globalRuleConfig.setAuthRuleId(defRuleConfig.getAuthRuleId());
                globalRuleConfig.setRuleClass(defRuleConfig.getRuleClass());
                globalRuleConfig.setRuleParameters(defRuleConfig.getRuleParameters());
            }
            //set the parameters in the global rule if any.
            KnCacheElement parameterElement = ruleElement.getElement(KnAASConstants.PARAMETERS);
            if (parameterElement != null) {
                globalRuleConfig.setRuleParameters(parameterElement.getAttributesMap());
            }

            knLogger.debug( mName, "Global Rule Config " + globalRuleConfig);
            globalRuleConfMap.put(ruleId, globalRuleConfig);
        }
        knLogger.debug( mName, "Enabled Global Rules -> " + globalRuleConfMap);
        return globalRuleConfMap;
    }

    /**
     * create a rule object.
     * @param profileId
     * @param operationId
     * @param opRuleId
     * @param authRuleId
     * @return rule objecy
     */
    private IAuthorizationRule getAuthRuleObj(String profileId, String operationId,
                                              String opRuleId, String authRuleId) {
        String mName = "getAuthRuleObj";
        KnAuthorizationRuleConfig ruleConfig = authorizationRulesConfigMap.get(authRuleId);
        knLogger.debug( mName, "Rule Config retireved : " + ruleConfig);
        return getAuthRuleObj(profileId, operationId, opRuleId, ruleConfig);
    }

    /**
     * create a rule object
     * @param profileId
     * @param operationId
     * @param opRuleId
     * @param ruleConfig
     * @return rule object
     */
    private IAuthorizationRule getAuthRuleObj(String profileId, String operationId,
                                              String opRuleId, KnAuthorizationRuleConfig ruleConfig) {
        String mName = "getAuthRuleObj";
        IAuthorizationRule ruleObj;
        try {
            ruleObj = (IAuthorizationRule) KnClassLoader.createInstance(ruleConfig.getRuleClass());
        } catch (KnClassLoaderException e) {
            knLogger.error( mName, "Rule Object creation failed for Operation Id : " + operationId + ", ruleId : " + opRuleId + ", ruleConfig : " + ruleConfig + ", exception - " + e);
            throw new KnXDMSystemError(KnErrorCodes.Authorizer.INTERNAL_ERROR, "Rule Creation Error. Operation : " + operationId + ", RuleId : " + opRuleId, e);
        }
        knLogger.debug( mName, "Populating rule object");
        ruleObj.setOperationId(operationId);
        ruleObj.setProfile(profileId);
        ruleObj.setRuleId(opRuleId);
        // Add default parameters from RuleConfig first
        ruleObj.addRuleParameters(ruleConfig.getRuleParameters());
        knLogger.debug( mName, "Rule Object : " + ruleObj);
        return ruleObj;
    }

    /**
     * Return the authorization profile configuration object for the profile
     * @param profile the profile name
     * @return the profile configuration objec
     * @throws KnAuthorizationException if the profile is not found
     */
    public KnAuthorizationProfileConfig getAuthorizationProfileConfig(String profile) throws KnAuthorizationException {
        String mName = "getAuthorizationProfileConfig";
        KnAuthorizationProfileConfig profileConf = (KnAuthorizationProfileConfig)authorizationProfilesMap.get(profile);
        if (profileConf == null) {
            knLogger.error( mName, "Cannot find Authorizer configuration for the profile - " + profile);
            throw new KnAuthorizationException(KnErrorCodes.Authorizer.CONFIG_ERROR, "Cannot find authorization configuration", profile, null, null);
        }
        return profileConf; 
    }

    /**
     * Return the enable/disable status of authorization framework.
     *
     * @return true if framework is enabled, false if its disabled
     * @throws KnAASException
     */
    public boolean isEnabled() throws KnAASException {
        return isAuthorizationEnabled;
    }

    /**
     * constructor
     */
    public KnAuthorizationModule() {
        authorizationRulesConfigMap = new HashMap <String, KnAuthorizationRuleConfig>();
        authorizationProfilesMap = new HashMap <String, KnAuthorizationProfileConfig>();
    }

}
