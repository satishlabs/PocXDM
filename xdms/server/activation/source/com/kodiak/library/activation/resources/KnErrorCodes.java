/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnErrorCodes.java
 * Subsystem:  Activation Library
 * <p/>
 * Name                   Date         Release
 * -------------------- ------------ -------------------------------------
 * Rashmi Kamat         29-Oct-2010       6.4
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
 * ************************************************************************
 */
package com.kodiak.library.activation.resources;

public class KnErrorCodes {
    public static final String MODULE_NAME = "ERRORCODES";

    /**
     * Initializer class.
     */
    public static final class Initializer extends com.kodiak.xdms.server.common.resources.KnErrorCodes.ConfigInitializer {
        public static final String CA_INIT_FAILED = "CALIB.Initializer.KNEC-CA10001";
    }

    /**
     * DAO class.
     */
    public static final class DAO extends com.kodiak.xdms.server.common.resources.KnErrorCodes.DAO {
    }

    /**
     * BOEntity class.
     */
    public static final class BOEntity extends com.kodiak.xdms.server.common.resources.KnErrorCodes.BOEntity {
        public static final String RTX_RESPONSE_400 = "BOEntity.KNEC-CA12000";
        public static final String RTX_RESPONSE_402 = "BOEntity.KNEC-CA12001";
        public static final String RTX_RESPONSE_500 = "BOEntity.KNEC-CA12002";
        public static final String RTX_RESPONSE_501 = "BOEntity.KNEC-CA12003";
        public static final String RTX_RESPONSE_502 = "BOEntity.KNEC-CA12004";
        public static final String RTX_RESPONSE_503 = "BOEntity.KNEC-CA12005";
        public static final String RTX_RESPONSE_504 = "BOEntity.KNEC-CA12006";
        public static final String RTX_RESPONSE_206 = "BOEntity.KNEC-CA12007";
        public static final String SUPPORTED_DEVICES_NOT_FOUND = "BOENTITY.KNEC-SP12086";
        public static final String UNSUPPORTED_APN = "BOENTITY.KNEC-SP12101";
        public static final String BLACKLISTED_DEVICE = "BOENTITY.KNEC-SP12102";
        public static final String UNAUTHORIZED_SUBS_CLIENT_TYPE = "BOENTITY.KNEC-SP12083";
        public static final String VOCODER_ID_MISMATCH = "BOEntity.KNEC-SP12123";
        public static final String ERROR_USER_DOC_NOT_CREATED = "BOEntity.KNEC-MD10525";
        public static final String PTTRADIO_BIT_DISABLE = "BOENTITY.KNEC-SP12124";
        public static final String INVALID_CLIENT = "BOENTITY.KNEC-SP12125";
        public static final String SUBSCRIBER_INFO_NOT_FOUND = "BOENTITY.KNEC-SP12057";
    }

    /**
     * Validator class.
     */
    public static final class Validator extends com.kodiak.xdms.server.common.resources.KnErrorCodes.Validator {
        //public static final String VALIDATION_FAILED = "Validator.KNEC-CA1251";
        public static final String SUBSCRIBER_IS_IN_DEACTIVATED_STATE = "Validator.KNEC-SP12509";
    }

    /**
     * Authenticator class
     */
    public static final class Authenticator extends com.kodiak.xdms.server.common.resources.KnErrorCodes.Authenticator {
    }

    /**
     * Authorizer class.
     */
    public static final class Authorizer extends com.kodiak.xdms.server.common.resources.KnErrorCodes.Authorizer {
        public static final String INVALID_ACTIVATION_KEY = "Authorizer.KNEC-CA13001";
    }

    /**
     * DTO class.
     */
    public static final class DTO {
    }
}
