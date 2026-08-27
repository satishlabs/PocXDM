/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.resources; /**
 * *****************************************************************************
 * File name:   KnJWTUtil.java
 * Subsystem: PoC
 * <p>
 * Name                    Date            Release
 * -----------------    -----------       -------
 * supananda              10/18/2016            1.0
 * <p>
 * <p>
 * 9th Floor, 'MFar', Manyata Tech Park
 * Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * *******************************************************************************
 */

import com.kodiak.logger.KnLogger;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;

/**
 *  Utility class for generating JWT / JWE token string using nimbusds and smart json libraries.
 */
public class KnJWTUtil {

    private static final KnLogger LOGGER = KnLogger.getLogger(KnJWTUtil.class);
    private JWSSigner signer;
    private String keySec = "";

    public KnJWTUtil(String keySec) {
        init(keySec);
    }


    /**
     * Initializing the JWT Util
     */
    private void init(String secKey) {
        String methodName = "init(String)";
        try {
            this.keySec = secKey;
            LOGGER.info(methodName, "Initializing the JWT Util");
            LOGGER.info(methodName, "Creating HMAC Signer");
            signer = new MACSigner(secKey.getBytes("UTF-8"));

        } catch (Exception e) {
            LOGGER.fatal(methodName, "Unexpected Exception occurred during initialization. ", e);
        }
    }

    /**
     * Generating the JWE Token <br/>
     * Step 1: Creating the Signed token </br>
     * Step 2: Encrypting the Signed token.
     *
     * @param tokenInfo the tokenInfo containing details of the token content
     * @return the JWE String token
     */
    public String generateJWEToken(KnTokenInfoDTO tokenInfo) {
        String methodName = "generateJWEToken(KnTokenInfoDTO)";
        SignedJWT signedJWT = createSignedJWT(tokenInfo);
        String jweStr = encryptJWT(signedJWT);
        LOGGER.debug(methodName, "JWE Str token - ", jweStr);
        return jweStr;
    }

    /**
     * Generating the Signed token
     *
     * @param tokenInfo tokenInfo containing details of the token content
     * @return the JWE String token. Returns null if any failure occurs
     */
    private SignedJWT createSignedJWT(KnTokenInfoDTO tokenInfo) {
        String methodName = "createSignedJWT(KnTokenInfoDTO)";
        String userName = "user_name"; // User name of the token holder
        String serviceType = "styp";
        String scope = "scope"; // Scope defining the permission levels/list of the token holder
        SignedJWT signedJWT = null;
        try {
            // Prepare JWT with claims set
            LOGGER.debug(methodName, "Preparing the JWT Claim set");
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(tokenInfo.getSubject()).issuer(tokenInfo.getIssuer()).issueTime(new Date())
                    .expirationTime(tokenInfo.getExp()).claim(userName, tokenInfo.getUserName())
                    .claim(scope, tokenInfo.getScope()).claim(serviceType, tokenInfo.getServiceType()).build();
            LOGGER.debug(methodName, "Generating the JWT");
            signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            // Apply the HMAC
            LOGGER.debug(methodName, "Signing the JWT applying HMAC");
            signedJWT.sign(signer);
            LOGGER.debug(methodName, "Signing Done");
        } catch (JOSEException e) {
            LOGGER.error(methodName, "Unexpected Exception occurred while Signing the JWT", e);
        }
        return signedJWT;
    }

    /**
     * Encrypting the Signed JWT
     *
     * @param signedJWT the Signed JWT token
     * @return the encrypted JWE String. Will return null in case of error
     */
    private String encryptJWT(SignedJWT signedJWT) {
        String methodName = "encryptJWT(SignedJWT)";
        String jweString = null;
        try {
            // Create JWE object with signed JWT as payload
            LOGGER.debug(methodName, "Preparing the JWE object");
            JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                            .contentType("JWT") // required to signal nested JWT
                            .build(),
                    new Payload(signedJWT));
            // Perform encryption
            LOGGER.debug(methodName, "Encrypting the JWE Object");
            jweObject.encrypt(new DirectEncrypter(keySec.getBytes()));
            // Serialise to JWE compact form
            jweString = jweObject.serialize();
        } catch (JOSEException e) {
            LOGGER.error(methodName, "Unexpected Exception occurred while encrypting the JWT", e);
        }
        LOGGER.debug(methodName, "Returning final JWE String");
        return jweString;
    }
}
