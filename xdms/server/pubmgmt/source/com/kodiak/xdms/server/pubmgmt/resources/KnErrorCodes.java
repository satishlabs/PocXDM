/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.resources;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnErrorCodes.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 11, 2011           7.0
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
public class KnErrorCodes {

    public static final String MODULE_NAME = "ERRORCODES";

    /**
     * Initializer class.
     */
    public static final class Initializer extends com.kodiak.xdms.server.common.resources.KnErrorCodes.ConfigInitializer {
        public static final String PUB_INIT_FAILED = "PUB.Initializer.KNEC-PM14200";
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
//
        public static final String CONTACT_ALREADY_EXISTS = "PUBLIB.BOEntity.KNEC-PM14000";
        public static final String CONTACT_DOES_NOT_EXISTS = "PUBLIB.BOEntity.KNEC-PM14001";
        public static final String CONTACT_LIST_ALREADY_EXISTS = "PUBLIB.BOEntity.KNEC-PM14002";
        public static final String CONTACT_LIST_DOES_NOT_EXISTS = "PUBLIB.BOEntity.KNEC-PM14003";        

        public static final String GROUP_ALREADY_EXISTS = "PUBLIB.BOEntity.KNEC-PM14100";
        public static final String GROUP_DOES_NOT_EXISTS = "PUBLIB.BOEntity.KNEC-PM14101";
        public static final String GROUP_NAME_ALREADY_EXISTS = "PUBLIB.BOEntity.KNEC-PM14102";
        public static final String GROUP_MEMBER_ALREADY_EXISTS = "PUBLIB.BOEntity.KNEC-PM14103";
        public static final String GROUP_MEMBER_DOES_NOT_EXISTS = "PUBLIB.BOEntity.KNEC-PM14104";

        public static final String NO_GROUPS_FOUND = "PUBLIB.BOEntity.KNEC-PM14105";
        public static final String DOC_NOT_MODIFIED = "PUBLIB.BOEntity.KNEC-PM14108";
        public static final String DOC_MODIFIED = "PUBLIB.BOEntity.KNEC-PM14109";
        public static final String AUTH_DOC_NOT_EXISTS = "PUBLIB.BOEntity.KNEC-PM14116";
        public static final String GROUP_USAGE_DOC_DOESNOT_EXITS = "PUBLIB.BOEntity.KNEC-PM14117";
        public static final String EMERGENCY_DOC_NOT_EXISTS = "PUBLIB.BOEntity.KNEC-PM14118";
        public static final String TGSS_DOC_NOT_EXISTS = "PUBLIB.BOEntity.KNEC-PM14119";

    }

    /**
     * Validator class.
     */
    public static final class Validator extends com.kodiak.xdms.server.common.resources.KnErrorCodes.Validator {
        public static final String GROUP_ALREADY_EXISTS = "Validator.KNEC-PM14100";        //Group name entered is duplicate
        public static final String MAX_GROUPS_REACHED = "Validator.KNEC-PM14106";    //Reached Maximum groups allowed limit
        public static final String MAX_GROUP_SIZE_REACHED = "Validator.KNEC-PM14107";  //Reached Maximum group size allowed limit
        public static final String MAX_CONTACT_SIZE_REACHED = "Validator.KNEC-PM14004";
        public static final String MEMBER_CLIENT_TYPE_NOT_ALLOWED ="Validator.KNEC-PM14110";
        public static final String MDNS_DOESNOT_BELONG_TO_SAME_CORP ="Validator.KNEC-PM14111";
        public static final String MDN_DOESNOT_HAVE_DISCREETE_PERM ="Validator.KNEC-PM14112";
        public static final String MDN_DOESNOT_HAVE_PERMISSIONS ="Validator.KNEC-PM14113";
        public static final String MDN_DOESNOT_HAVE_USERENABLE_PERM ="Validator.KNEC-PM14114";
        public static final String MDN_DOESNOT_HAVE_EMERGENCY_PERM ="Validator.KNEC-PM14115";
        public static final String FEATUREBIT_IS_DISABLED="Validator.KNEC-PM14116";
        public static final String OPERATION_NOT_ALLOWED ="Validator.KNEC-PM14120";
        public static final String MCSXCAP_LOWER_PV="Validator.KNEC-PM14121";
        public static final String MCPTTID_NOT_MATCHED="Validator.KNEC-PM14122";
        public static final String CORPID_NOT_MATCHED="Validator.KNEC-PM14123";
        public static final String INVALID_AUTH_SATUS="Validator.KNEC-PM14124";
        public static final String MDN_DOESNOT_BELONG_TO_GROUP="Validator.KNEC-PM14125";
        public static final String MDN_IS_IN_NON_ACTIVATED_STATE ="Validator.KNEC-PM16294";
        public static final String MDN_IS_NOT_PART_OF_THE_GROUP = "Validator.KNEC-PM16295";
        public static final String USER_AGENT_NOT_AVAILABLE = "Validator.KNEC-PM14126";

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
    }

    /**
     * DTO class.
     */
    public static final class DTO {
    }

}
