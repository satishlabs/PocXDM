/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnAASFramework.java
 * Subsystem:   Framework
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Jiji Sasidharan      04-05-2006 5.7
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
package com.kodiak.xdms.server.common.framework.aas;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerSystemException;
import com.kodiak.xdms.server.common.configuration.cache.ICacheClientIntf;
import com.kodiak.xdms.server.common.configuration.cache.ICacheManager;
import com.kodiak.xdms.server.common.configuration.cache.KnCacheInvocationException;
import com.kodiak.xdms.server.common.configuration.cache.datastructure.KnCacheElement;
import com.kodiak.xdms.server.common.configuration.manager.KnConfigurationsManager;
import com.kodiak.xdms.server.common.configuration.KnConfigurationException;
import com.kodiak.xdms.server.common.framework.aas.authorization.*;
import com.kodiak.xdms.server.common.framework.aas.authentication.*;
import com.kodiak.xdms.server.common.dto.intf.IPersistenceDTO;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsible for enforcing the authentication and autherization on
 * different library operations. This framework provides mainly two functionalities
 * <ul>
 * <li>1. authentication<li>
 * <li>2. authorization<li>
 * </ul>
 * <p/>
 * The method corresponds to these functionalities are <strong>authenticate</strong
 * and <strong>authorize</strong>. Authorization may in turn call authentication if
 * authentication is enabled for the respective operation.
 * <p/>
 * <h3>Authorization</h3>
 * Business entities will fetch data for authorization and then call KnAASFramework.authorize(dto);
 * If the authentication is enabled
 * Based on the configurations in the authorization.xml and the input value,
 * this class will find out the appropriate profile authorizers. Profile authorizers
 * will do the preprocessing if any required and delegate to the authorization rules.
 */
public class KnAASFramework implements ICacheClientIntf {
	private static final KnLogger knLogger = KnLogger.getLogger(KnAASFramework.class);

    private static KnAASFramework authFw = null;

    // store module name
    private String moduleName;
    // store parents
    private String[] parents;
    // store id
    private String id;

    private static final String CLASS = KnAASFramework.class.getName();

    private KnAuthorizationModule authorizationModule;
    private KnAuthenticationModule authenticationModule;
    // configuration manager object
    private KnConfigurationsManager configManager;
    // stores the instances of fwk objects with the libraryName
    static Map instanceMap;

    /**
     * constructor
     */
    private KnAASFramework() {
        authorizationModule = new KnAuthorizationModule();
        authenticationModule = new KnAuthenticationModule();
    }

    static {
        instanceMap = new ConcurrentHashMap();
    }

    /**
     * Returns an instance of AAS Framework.
     * First It will check whether instance is already available in instanceMap. If it so,
     * then it will reuse it otherwise it will create the instance specific to the lbirary
     * and store it in instanceMap for re-use.
     *
     * @param libraryName   library name
     * @return  KnAASFramework instance
     */
    public static KnAASFramework getInstance(String libraryName) {
        KnAASFramework aasFwk = (KnAASFramework) instanceMap.get(libraryName);
        if (aasFwk == null) {
            try {
                aasFwk = new KnAASFramework();
                aasFwk.configManager = KnConfigurationsManager.getInstance(libraryName);
                aasFwk.setModuleName(KnAASConstants.AAS_MODULE_NAME);
                aasFwk.setParents(null);
                aasFwk.setId(KnAASConstants.AAS_MODULE_ROOT);
                aasFwk.init();
                instanceMap.put(libraryName, aasFwk);
            } catch (KnAASException aas) {
                knLogger.error( "getInstance()", "Error while doing Authorization - " + aas.getMessage());
                //throw aas;
            }
        }
        return aasFwk;
    }

    /**
     * Authorize the DTO. All the clients of Authorization framework will call this method and
     * pass the Persistence DTO to this method. This method inturn get an instance of
     * authorization framework and authorize the DTO.
     *
     * @param dto
     * @throws KnAASException
     */
    public void authorize(IPersistenceDTO dto) throws KnAASException {
        String mName = "authorize";
        try {
//            KnAASFramework fw = getInstance();
            if (isAuthorizationEnabled()) {
                knLogger.debug( mName, "Authorizing DTO", dto);
                this.authorizeDTO(dto);
                knLogger.debug( mName, "Authorization Completed.");
            } else {
                knLogger.warn( mName, "Authorization framework is DISABLED.");
            }
        } catch (KnAASException e) {
            knLogger.error( mName, "Error while doing Authorization - " , e.getMessage());
            throw e;
        }
    }

    /**
     * This will do authentication
     *
     * @param dto persistdat DTO
     * @throws KnAASException
     */
    public void authenticate(IPersistenceDTO dto) throws KnAASException {
        String mName = "authenticate";
        try {
            //KnAASFramework fw = getInstance();
            this.authenticateDTO(dto, KnAASConstants.LOGIN);
        } catch (KnAASException e) {
            knLogger.error( mName, "Error while doing Authentication - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Authorize the dto. This method will take the Authorization Profile configuration for the
     * given profile and invoke the profile authorizer.
     *
     * @param dto the dto
     * @throws KnAASException
     */
    private void authorizeDTO(IPersistenceDTO dto) throws KnAASException {
        String mName = "authorizeDTO";
        if (dto == null) {
            knLogger.error( mName, "DTO Passed for Authorization is null");
            throw new KnXDMServerSystemException(KnErrorCodes.Authorizer.CONFIG_ERROR, "Null DTO is passed for authorization");
        }
        String profile = dto.getProfile();
        KnAuthorizationProfileConfig profileConf = authorizationModule.getAuthorizationProfileConfig(profile);
        IProfileAuthorizer profileAuthorizer = profileConf.getProfileAuthorizer();
        KnAuthorizationOperationConfig operationConfig = profileAuthorizer.getOperationConfig(dto.getOperationType());
        if (operationConfig != null) {
            if (operationConfig.isAuthenticationRequired()) {
                authenticateDTO(dto, KnAASConstants.GENERAL);
            } else {
                knLogger.debug( mName, "Authentication disabled for " , dto.getOperationType());
            }
        }
        profileAuthorizer.authorize(dto);
    }

    /**
     * Authenticate the dto.
     *
     * @param dto the dto
     * @throws com.kodiak.xdms.server.common.framework.aas.authentication.KnAuthenticationException
     *
     */
    private void authenticateDTO(IPersistenceDTO dto, String operation) throws KnAuthenticationException, KnAASException {
        String mName = "authenticateDTO";
        String profile = "";
        if (dto == null) {
            knLogger.error( mName, "DTO Passed for Authentication is null");
            throw new KnXDMServerSystemException(KnErrorCodes.Authorizer.CONFIG_ERROR, "Null DTO is passed for authorization");
        }
        if (isAuthenticationEnabled()) {
            knLogger.debug( mName, "Authenticating " , dto , "...");
            profile = dto.getProfile();
            knLogger.debug( mName, "Authenticating for profile -> " , profile);
            KnAuthenticationProfileConfig profileConf = authenticationModule.getAuthenticationProfileConfig(profile);
            if (profileConf != null) {
                if(profileConf.getAllowed().equals(KnAASConstants.TRUE)){
                    KnAuthenticationOperationConfig operationConfig = profileConf.getOperationConfig(operation);
                    if(operationConfig != null){
                        Collection rules = operationConfig.getRules();
                        for (Iterator it = rules.iterator(); it.hasNext(); ) {
                            IAuthenticationRule authRule = (IAuthenticationRule) it.next();
                            knLogger.debug( mName, "Before authRule.authenticate(dto). authRule -> " , authRule);
                            authRule.authenticate(dto);
                            knLogger.debug( mName, "After authRule.authenticate(dto). authRule -> ");
                        }
                    }
                    else{
                        knLogger.debug( mName, "Operation Config is null");
                    }
                }
                else{
                    knLogger.error( mName, "Authentication is not allowed for profle :" , profile);
                    throw new KnAuthenticationException(KnErrorCodes.Authenticator.NOT_ALLOWED, "Authentication is not allowed for profle :", profile, null);
                }
            }//if (profileConf != null)
        }
        else if(!isAuthenticationEnabled()){
                knLogger.debug( mName, "Authentication framework is DISABLED.");
        }
        else {
            knLogger.error( mName, "Authentication profile is not configured. profile :" , profile);
            throw new KnXDMServerSystemException(KnErrorCodes.Authorizer.CONFIG_ERROR, "Profile is not configured.");
        }
    }

    /**
     * Return the enable/disable status of authorization framework.
     *
     * @return true if framework is enabled, false if its disabled
     * @throws KnAASException
     */
    public boolean isAuthorizationEnabled() throws KnAASException{
        return this.authorizationModule.isEnabled();
    }

    /**
     * Return the enable/disable status of authorization framework.
     *
     * @return true if framework is enabled, false if its disabled
     * @throws KnAASException
     */
    public boolean isAuthenticationEnabled() throws KnAASException{
        return this.authenticationModule.isEnabled();
    }

    /**
     * Initialize authorization framework. This method read the authorization.xml file and cache
     * the rule configurations and Profile configurations. The rule configurations will be
     * stored in ruleConfigMap and the profile configurations will be stored in authorizationProfilesMap.
     */
    private void init() throws KnAASException {
        String mName = "init";
        knLogger.info( mName, "Initializing Authentication & Authorization Framework...");
        try {
            //KnConfigurationsManager configManager = KnConfigurationsManager.getInstance();
            ICacheManager cacheManager = this.configManager.getCacheManager();
            KnCacheElement aasElement = (KnCacheElement) cacheManager.get(this);

            //initialize authentication module
            authenticationModule.init(aasElement.getElement(KnAASConstants.AUTHENTICATION));
            //initialize authroization module
            authorizationModule.init(aasElement.getElement(KnAASConstants.AUTHORIZATION));
        } catch (KnAuthorizationException e) {
            throw e;
        } catch (KnAuthenticationException e) {
            throw e;
        } catch (KnCacheInvocationException e) {
            knLogger.error( mName, "Cache Invocation error while initializing Aurthorization framework - " , e);
            throw new KnXDMServerSystemException(KnErrorCodes.Authorizer.INTERNAL_ERROR, "Error while initializing Authorization framework - " + e.getMessage(), e);
        } catch (KnConfigurationException e) {
            knLogger.error( mName, "Configuration error while initializing Aurthorization framework - " , e);
            throw new KnXDMServerSystemException(KnErrorCodes.Authorizer.INTERNAL_ERROR, "Error while initializing Authorization framework - " + e.getMessage(), e);
        } catch (Exception e) {
            knLogger.error( mName, "Error while initializing Aurthorization framework - " , e);
            throw new KnXDMServerSystemException(KnErrorCodes.Authorizer.INTERNAL_ERROR, "unexpected error - " + e.getMessage(), e);
        }
        //initAuthentication();
        knLogger.debug( mName, "Successfully Initialized Authentication & Authorization Framework...");
    }

    /**
     * This method will set the moduleName
     *
     * @param moduleName
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * this method will return the name of the module
     *
     * @return the name of the module
     */
    public String getModuleName() {
        return this.moduleName;
    }

    /**
     * this method will set the parents
     *
     * @param parent the parents array
     */
    public void setParents(String[] parent) {
        if (parent != null) {
            this.parents = Arrays.copyOf(parent, parent.length);
    }
    }

    /**
     * this method will return the parents array
     *
     * @return the parents
     */
    public String[] getParents() {
        return parents;
    }

    /**
     * This will set the id
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * this method will return the id
     *
     * @return the id
     */
    public String getId() {
        return id;
    }
}
