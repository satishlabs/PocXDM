/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.resources;

import com.kodiak.logger.KnLogger;
import java.security.SecureRandom;
/**
 * ************************************************************************
 * <p>
 * File name:  KnGeneralPasswordUtil.java
 * Subsystem:  PoC
 * <p>
 * Name                         Date                     Release
 * --------------------     ----------------        ------------------
 * Saurabh Kumar             Nov 19, 2018                9.0
 * <p>
 * <p>
 * KODIAK, 9th Floor, 'MFar Manyata Tech Park'
 * Greenheart Phase IV, Nagawara
 * Bangalore - 560 045
 * www.kodiakptt.com
 * All Rights Reserved.
 * <p>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */

public class KnGeneralPasswordUtil {

    private static final KnLogger knLogger = KnLogger.getLogger(KnGeneralPasswordUtil.class);
    private static KnGeneralPasswordUtil instance;
    /**
     * The random number generator.
     */
    private static java.util.Random r = new java.util.Random();
    //Creating instance of SecureRandom class
    SecureRandom rand = new SecureRandom();
    /**
     * I, L and O are good to leave out as are numeric zero and one.
     */
    private static final String DIGITS = "0123456789";
    private static final String LOCASE_CHARACTERS = "abcdefghjkmnpqrstuvwxyz";
    private static final String UPCASE_CHARACTERS = "ABCDEFGHJKMNPQRSTUVWXYZ";
    private static final String SYMBOLS = "@#&%$^+=";
    private static final String ALL = DIGITS + LOCASE_CHARACTERS + UPCASE_CHARACTERS + SYMBOLS;
    private static final char[] upcaseArray = UPCASE_CHARACTERS.toCharArray();
    private static final char[] locaseArray = LOCASE_CHARACTERS.toCharArray();
    private static final char[] digitsArray = DIGITS.toCharArray();
    private static final char[] symbolsArray = SYMBOLS.toCharArray();
    private static final char[] allArray = ALL.toCharArray();

    public static synchronized KnGeneralPasswordUtil getInstance() {
        if (instance == null) {
            instance = new KnGeneralPasswordUtil();
        }
        return instance;
    }

    // Regex:
    // ^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$)[0-9a-zA-Z@#$%^&+=]{6,}$

    /**
     * Generate a random password based on security rules
     * <p>
     * - at least 6 characters
     * - at least one uppercase
     * - at least one lowercase
     * - at least one number
     * - at least one symbol
     *
     * @return
     */
    private String genPwd(int passwordLength) {
        StringBuilder sb = new StringBuilder();
        // get at least one lowercase letter
        sb.append(locaseArray[rand.nextInt(locaseArray.length)]);
        // get at least one uppercase letter
        sb.append(upcaseArray[rand.nextInt(upcaseArray.length)]);
        // get at least one digit
        sb.append(digitsArray[rand.nextInt(digitsArray.length)]);
        // get at least one symbol
        sb.append(symbolsArray[rand.nextInt(symbolsArray.length)]);
        // fill in remaining with random letters
        for (int i = 0; i < passwordLength - 4; i++) {
            sb.append(allArray[rand.nextInt(allArray.length)]);
        }
        return sb.toString();
    }

    public String generatePassword(int passwordLength) {
        return genPwd(passwordLength);
    }

    public String generatePassword(int passwordLength, String appId) {
        if(KnConstants.APP_ID.DISPATCHER.value().equals(appId) || KnConstants.APP_ID.USERMCSCLIENTS.value().equals(appId)){
            return null;
        }
        return genPwd(passwordLength);
    }

    public String generatePassword(String password, String appId) {
        if(KnConstants.APP_ID.DISPATCHER.value().equals(appId) || KnConstants.APP_ID.USERMCSCLIENTS.value().equals(appId)){
            return null;
        }
        return password;
    }

    /**
     * Utility method for generating Random password based on length
     * @param passwordLength
     * @return
     */
    public String generateRandomPassword(int passwordLength) {

        return genPwd(passwordLength);
    }

}