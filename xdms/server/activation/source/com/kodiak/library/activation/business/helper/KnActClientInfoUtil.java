/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnActClientInfoUtil.java
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
package com.kodiak.library.activation.business.helper;

import com.kodiak.common.commdto.common.KnAuthDTO;
import com.kodiak.common.commdto.common.KnUserAgentDTO;
import com.kodiak.common.commdto.request.KnXDMActivateInfoDTO;
import com.kodiak.common.commdto.response.KnXDMActivateRespDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.frameworks.messaging.common.KnAsyncConstant;
import com.kodiak.frameworks.messaging.common.dto.KnMessage;
import com.kodiak.frameworks.normalizationfw.KnNormException;
import com.kodiak.frameworks.normalizationfw.clientIntf.impl.KnNormalizeImpl;
import com.kodiak.library.activation.business.KnActBOException;
import com.kodiak.library.activation.dto.clientdat.KnIPClientRegistryDTO;
import com.kodiak.library.activation.dto.common.KnClientInfoDTO;
import com.kodiak.library.activation.resources.KnErrorCodes;
import com.kodiak.logger.KnLogger;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * This utility class which will be used to get license information and configuration releated to clientless
 * subsystem.
 */
public class KnActClientInfoUtil {
	private static final KnLogger knLogger = KnLogger.getLogger(KnActClientInfoUtil.class);

    private static final String className = KnActClientInfoUtil.class.getName();
    //private KnProfileInfoUtil profileHandler = KnProfileInfoUtil.getInstance();
    private String internationalPrefix;
    private String nationalPrefix;
    private String countryCode;
    private int numberingPlanType;
    private static boolean isInitialized;
    private static KnActClientInfoUtil instance;
    KnConfigInfoUtil configInfoUtil = null;
    KnNormalizeImpl normalizeUtil = null;
    //private IJmsMessagingClientIntf msgFrwkInst = null;
    /**
     * constructor
     */
    public KnActClientInfoUtil() {
        String methodName = "KnActClientInfoUtil";

            //*****************************************************************
            // TODO : This Messaging FW initialization needs to be removed
            // TODO : after the Onload Servlet introduction/ after DT.
            //*****************************************************************
            //Collection<String> queues = new ArrayList<String>();
            //queues.add(KnConstants.QUEUE_NAME_WEB_PUB);
            //queues.add(KnConstants.QUEUE_NAME_WEB_PROV);
            //queues.add(KnConstants.QUEUE_NAME_WEB_CORP);
            //KnJmsMessagingClientImpl.initialize(queues, KnMessageConstants.CARD_TYPE_WEBSERVER);
            //****************************************************************

        knLogger.info( methodName, "Msg FW initialized");

        try {
            configInfoUtil = KnConfigInfoUtil.getInstance();
            normalizeUtil = KnNormalizeImpl.getInstance();
            knLogger.debug( methodName, "Normalization FrameWork Initialized ");
        } catch (KnNormException ne) {
            knLogger.error( methodName, "Exception while get Instance of Normalization FW");
        } catch (Exception ex) {
            knLogger.error( methodName, "Request processing failed in RequestResponse FW ! " + ex);
        }
    }

    /**
     * This method returns the singleton instance of the utility class
     *
     * @return the singleton instance
     */
    public static KnActClientInfoUtil getInstance() {
        if (!isInitialized) {
            synchronized (KnActClientInfoUtil.class) {
                if (instance == null) {
                    instance = new KnActClientInfoUtil();
                    isInitialized = true;
                }
            }
        }
        return instance;
    }

    /**
     * Returns Country Code of the Subscriber
     *
     * @return String countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Returns National Prefix of the Subscriber
     *
     * @return String nationalPrefix
     */
    public String getNationalPrefix() {
        return nationalPrefix;
    }

    /**
     * Returns International Prefix of the Subscriber
     *
     * @return String internationalPrefix
     */
    public String getInternationalPrefix() {
        return internationalPrefix;
    }

    /**
     * @param mdn         subscriber mdn
     * @param countryCode counry code
     */
    private static void checkNational(String mdn, String countryCode) {
        String methodName = "checkNational";
        if (mdn.startsWith(countryCode)) {
            knLogger.info( methodName, "MDN is in National  ");
        } else {
            knLogger.info( methodName, "MDN is in International  ");
        }
    }

    /**
     * @param mdn         subscriber mdn
     * @param countryCode countrycode
     * @return String
     */
    private static String addCountryCode(String mdn, String countryCode) {
        return countryCode + mdn;
    }


    public String getXdmServerId() throws KnDAOException {
        String methodName = "getXdmServerId";
        String xdmServerId = null;
        xdmServerId = com.kodiak.common.resources.KnGeneralUtil.getXDMServerPttServerId();
        knLogger.debug( methodName, "XDM Server Id recieved from Norm Frwk: " + xdmServerId);
        return xdmServerId;
    }

    static public String byteToHex
            (
                    byte b) {
        // Returns hex String representation of byte b
        char hexDigit[] = {
                '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        char[] array = {hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f]};
        return new String(array);
    }

    public Timestamp generateExpiryTime(long vasKeyValidity) {
        String methodName = "generateExpiryTime(vasKeyValidity)";
        knLogger.info( methodName, "ENTRY : vasKeyValidity - " + vasKeyValidity);
        Calendar calendar = Calendar.getInstance();
        long currentMilliSeconds = calendar.getTimeInMillis();
        long configuredExpTimeInMilliSec = vasKeyValidity * 60 * 1000l;
        long expiryTimeInMilisec = currentMilliSeconds + configuredExpTimeInMilliSec;
        knLogger.debug( methodName, "ExpiryTime in Milisecond - " + expiryTimeInMilisec);
        Timestamp ts = new Timestamp(expiryTimeInMilisec);
        knLogger.debug( methodName, "ExpiryTime in ts - " + ts);
        return ts;
    }

}