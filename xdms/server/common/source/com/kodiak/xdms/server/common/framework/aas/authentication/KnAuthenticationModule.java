/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/****************************************************************************
 *
 * File name:   KnAuthenticationModule.java
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
package com.kodiak.xdms.server.common.framework.aas.authentication;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerSystemException;
import com.kodiak.xdms.server.common.KnXDMSystemError;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheElement;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheAttributes;
import com.kodiak.xdms.server.common.configuration.cache.KnCacheInvocationException;
import com.kodiak.xdms.server.common.configuration.KnConfigurationException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.common.framework.aas.KnAASConstants;
import com.kodiak.xdms.server.common.util.KnClassLoader;
import com.kodiak.xdms.server.common.util.KnClassLoaderException;

import java.util.*;

public class KnAuthenticationModule {
	private static final KnLogger knLogger = KnLogger.getLogger(KnAuthenticationModule.class);

    public static final String CLASS = KnAuthenticationModule.class.getName();

    private boolean isEnabled = false;
    private Map<String, KnAuthenticationRuleConfig> authenticationRulesConfigMap;
    private Map<String, KnAuthenticationProfileConfig> authenticationProfilesConfigMap;

    public KnAuthenticationModule() {
        authenticationRulesConfigMap = new HashMap<String, KnAuthenticationRuleConfig>();
        authenticationProfilesConfigMap = new HashMap<String, KnAuthenticationProfileConfig>();
    }

    /**
     * initialize the authentication module.
     * @param authenticationElement
     * @throws KnAuthenticationException
     */
    public void init(KnCacheElement authenticationElement) throws KnAuthenticationException {
        String mName = "init";
        try {
            knLogger.info( mName, "Initializing Authentication Module...");
            if (authenticationElement == null) {
                knLogger.error( mName, "Authentication Module Configuration Missing..");
                throw new KnXDMServerSystemException(KnErrorCodes.Authenticator.CONFIG_ERROR, "AAS - Authentication Module Configuration Missing");
            }

            Collection subElements = authenticationElement.getElements();
            knLogger.debug( mName, "Cached Elements retrived for authentication module. Elements : " + subElements);

            String enabledAttribute = authenticationElement.getAttribute(KnAASConstants.ENABLED);
            knLogger.debug( mName, "Framework enabled status : " + enabledAttribute);
            if (KnAASConstants.TRUE.equals(enabledAttribute)) {
                isEnabled = true;
            } else {
                knLogger.info( mName, "Authentication Module disabled");
                isEnabled = false;
                return;
            }
                   
            //hmm.. its the time to populate the rules configurations. it shud be
            //done first since profile have a reference to this configuration
            KnCacheElement rulesConfig = authenticationElement.getElement(KnAASConstants.RULES_CONFIG);
            knLogger.debug( mName, "Populating rules configurations... rules-config : " + rulesConfig);
            populateAuthenticationRulesConfig(rulesConfig);
            knLogger.debug( mName, "Populated rules configurations successfully.");

            //yes.. rules config done. now ball is with profiles. go ahead...
            KnCacheElement authEntity = authenticationElement.getElement(KnAASConstants.AUTH_PROFILES);
            knLogger.debug( mName, "Populating entities... auth-entities : " + rulesConfig);
            populateAuthenticationProfiles(authEntity);
            knLogger.debug( mName, "Populated authentication profiles successfully.");
            //knLogger.debug( mName, "Authentication configurations - " + authenticationProfilesConfigMap);

            knLogger.info( mName, "Authentication Moduile initialized successfully.");
        } catch (KnAuthenticationException e) {
            throw e;
        } catch (KnCacheInvocationException e) {
            knLogger.error( mName, "Cache Invocation error while initializing Authentication Module - " + e);
            throw new KnXDMServerSystemException(KnErrorCodes.Authenticator.INTERNAL_ERROR, "Error while initializing Authentication Module - " + e.getMessage(), e);
        } catch (KnConfigurationException e) {
            knLogger.error( mName, "Configuration error while initializing Authentication Module - " + e);
            throw new KnXDMServerSystemException(KnErrorCodes.Authenticator.INTERNAL_ERROR, "Error while initializing Authentication Module - " + e.getMessage(), e);
        } catch (Exception e) {
            knLogger.error( mName, "Error while initializing Authentication Module - " + e);
            throw new KnXDMServerSystemException(KnErrorCodes.Authenticator.INTERNAL_ERROR, "unexpected error - " + e.getMessage(), e);
        }
    }
    /**
     * populate the authentication profiles
     *
     * @param authEntityElement
     * @throws Exception
     */
    private void populateAuthenticationProfiles(KnCacheElement authEntityElement) throws Exception  {
        String mName = "populateAuthenticationProfiles";
        knLogger.debug( mName, "Reading auth-profiles ...");
        Collection elements = authEntityElement.getElements();
        for (Iterator it = elements.iterator(); it.hasNext();) {
            KnCacheElement profileElement = (KnCacheElement) it.next();
            String id = profileElement.getId();
            this.authenticationProfilesConfigMap.put(id, populateAuthenticationProfileConfig(profileElement));

        }
        knLogger.debug( mName, "Authentication profiles populated successfully.");
    }
    /**
     * populate the authentication profile config
     *
     * @param profileElement
     * @return
     * @throws Exception
     */
    private KnAuthenticationProfileConfig populateAuthenticationProfileConfig(KnCacheElement profileElement) throws Exception  {
        String mName = "populateAuthenticationProfileConfig";
        knLogger.debug( mName, "Reading auth-profile config...");
        Collection elements = profileElement.getElements();
        String profileId = profileElement.getId();
        KnAuthenticationProfileConfig authProfileConfig = new KnAuthenticationProfileConfig();
        Map<String, KnAuthenticationOperationConfig> authenticationOperationConfigMap = new HashMap<String, KnAuthenticationOperationConfig>();
        for (Iterator it = elements.iterator(); it.hasNext();) {
            KnCacheElement operationConfigElement = (KnCacheElement) it.next();
            String id = operationConfigElement.getId();
            String isEnabled = operationConfigElement.getAttribute(KnAASConstants.ENABLED);
            if(!isEnabled.trim().equals(KnAASConstants.TRUE)){
                continue;
            }
            authenticationOperationConfigMap.put(id, populateAuthenticationOperationConfig(operationConfigElement, profileId));
        }
        Map attrs = profileElement.getAttributesMap();
        String allowed = (String)attrs.get(KnAASConstants.ALLOWED);
        authProfileConfig.setAllowed(allowed);
        authProfileConfig.setOperationConfigMap(authenticationOperationConfigMap);
        //this.authenticationProfilesConfigMap.put(profileId, authProfileConfig);
        knLogger.debug( mName, "auth-profile config read successfully.");
        return authProfileConfig;
    }
    /**
     * populate authentication operation config
     *
     * @param operationConfigElement
     * @param profileId
     * @return
     * @throws Exception
     */
    private KnAuthenticationOperationConfig populateAuthenticationOperationConfig(KnCacheElement operationConfigElement, String profileId) throws Exception {
        String mName = "populateAuthenticationOperationConfig";
        knLogger.debug( mName, "Reading login, operation-specific config...");
        Collection elements = operationConfigElement.getElements();
        Map attr = operationConfigElement.getAttributesMap();
        String isEnabled = (String)attr.get(KnAASConstants.ENABLED);
        KnAuthenticationOperationConfig authOperationConfig = new KnAuthenticationOperationConfig();
        authOperationConfig.setEnabled(isEnabled);

        Collection <IAuthenticationRule> ruleList = new ArrayList <IAuthenticationRule>();

        for (Iterator it = elements.iterator(); it.hasNext();) {
            KnCacheElement ruleElement = (KnCacheElement) it.next();
            Map attrs = ruleElement.getAttributesMap();
            String ruleId = ruleElement.getId();
            String authRuleId = (String)attrs.get(KnAASConstants.AUTH_RULE_ID);
            isEnabled = (String)attrs.get(KnAASConstants.ENABLED);
            if(!isEnabled.trim().equals(KnAASConstants.TRUE)){
                continue;
            }
            IAuthenticationRule ruleObj = getAuthRuleObj(profileId, ruleId, authRuleId);
            KnCacheElement parameterElement = ruleElement.getElement(KnAASConstants.PARAMETERS);
            if (parameterElement != null) {
                ruleObj.addRuleParameters(parameterElement.getAttributesMap());
            }
            ruleList.add(ruleObj);
        }
        authOperationConfig.setRules(ruleList);
        knLogger.debug( mName, "login-auth, operation-auth... configs read successfully.");
        return authOperationConfig;
    }
    /**
     * populate rules config 
     *
     * @param rulesConfigElement
     * @throws Exception
     */
    private void populateAuthenticationRulesConfig(KnCacheElement rulesConfigElement) throws Exception {
        String mName = "populateAuthenticationRulesConfig";
        knLogger.debug( mName, "Reading login-auth / operation-auth config rules...");
        Collection elements = rulesConfigElement.getElements();
        for (Iterator it = elements.iterator(); it.hasNext();) {
            KnCacheElement ruleElement = (KnCacheElement) it.next();
            KnCacheAttributes attrs = ruleElement.getAttributes();
            String id = ruleElement.getId();
            String className = attrs.get(KnAASConstants.CLASS_NAME);

            KnCacheElement paramsElement = ruleElement.getElement(KnAASConstants.PARAMETERS);
            KnAuthenticationRuleConfig authenticationRuleConfig = new KnAuthenticationRuleConfig();
            authenticationRuleConfig.setAuthRuleId(id);
            authenticationRuleConfig.setClassName(className);
            authenticationRuleConfig.setRuleClass(KnClassLoader.loadClass(className));
            if (paramsElement != null) {
                authenticationRuleConfig.setRuleParameters(paramsElement.getAttributesMap());
            }
            knLogger.debug( mName, "Rule Config Read. Id : " + id + ", class name : " + className);
            this.authenticationRulesConfigMap.put(id, authenticationRuleConfig);
        }
        knLogger.debug( mName, "login-auth / operation-auth config rules read successfully.");
    }

    /**
     * create a rule object.
     * @param profileId
     * @param opRuleId
     * @param authRuleId
     * @return rule objecy
     */
    private IAuthenticationRule getAuthRuleObj(String profileId, String opRuleId, String authRuleId) {
        String mName = "getAuthRuleObj";
        KnAuthenticationRuleConfig ruleConfig = this.authenticationRulesConfigMap.get(authRuleId);
        knLogger.debug( mName, "Rule Config retireved : " + ruleConfig);
        return getAuthRuleObj(profileId, opRuleId, ruleConfig);
    }

    /**
     * create a rule object
     * @param profileId
     * @param opRuleId
     * @param ruleConfig
     * @return rule object
     */
    private IAuthenticationRule getAuthRuleObj(String profileId, String opRuleId, KnAuthenticationRuleConfig ruleConfig) {
        String mName = "getAuthRuleObj";
        IAuthenticationRule ruleObj;
        try {
            ruleObj = (IAuthenticationRule) KnClassLoader.createInstance(ruleConfig.getRuleClass());
        } catch (KnClassLoaderException e) {
            knLogger.error( mName, "Rule Object creation failed for profile Id : " + profileId + ", ruleId : " + opRuleId + ", ruleConfig : " + ruleConfig + ", exception - " + e);
            throw new KnXDMSystemError(KnErrorCodes.Authenticator.INTERNAL_ERROR, "Authentication Rule Creation Error. Profile : " + profileId + ", RuleId : " + opRuleId, e);
        }
        knLogger.debug( mName, "Populating rule object");
        ruleObj.setProfile(profileId);
        ruleObj.setRuleId(opRuleId);
        // Add default parameters from RuleConfig first
        ruleObj.addRuleParameters(ruleConfig.getRuleParameters());
        knLogger.debug( mName, "Rule Object : " + ruleObj + ", Profile ID => " + ruleObj.getProfile());
        return ruleObj;
    }
    /**
     * Return the enable/disable status of authentication module.
     * @return enable/disable status of authentication module
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    public KnAuthenticationProfileConfig getAuthenticationProfileConfig(String profile)
        throws KnAuthenticationException {
        String mName = "getAuthenticationProfileConfig";
        KnAuthenticationProfileConfig profileConf = authenticationProfilesConfigMap.get(profile);
        if (profileConf == null) {
            knLogger.error( mName, "Cannot find Authentication profile configuration for the profile - " + profile);
            throw new KnAuthenticationException(KnErrorCodes.Authenticator.CONFIG_ERROR, "Cannot find authentication profile configuration", profile, null);
        }
        return profileConf;
    }
}
