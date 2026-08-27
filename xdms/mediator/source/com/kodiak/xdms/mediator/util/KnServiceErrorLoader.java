/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.util;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.KnMediatorConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnServiceErrorLoader.java
 * Subsystem:  IDS
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        15-10-2010      6.4
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
public class KnServiceErrorLoader {
	private static final KnLogger knLogger = KnLogger.getLogger(KnServiceErrorLoader.class);
    private static String CLASS = KnServiceErrorLoader.class.getName();
    private String configFile = null;
    private Map<String, KnErrorBean> errorConfigMap = null;


    public KnServiceErrorLoader(String configFile, Map errorConfigMap) {
        this.configFile = configFile;
        this.errorConfigMap = errorConfigMap;

    }

    public void load() throws Exception {
        String methodName = "load()";
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        knLogger.info( methodName, "config File - " , configFile
        );

        try {

            knLogger.debug( methodName, "Getting Inputstream");
            InputStream input = getClass().getResourceAsStream(configFile);
            knLogger.debug( methodName, "Got Inputstream-" , input);
            saxParser.parse(input, new KnServiceErrorLoader.KnMappingHandler(errorConfigMap));
            knLogger.info( methodName, "Parsed");
        } catch (IOException e) {
            knLogger.error( methodName, "Parsing inside exception - " , e);
        }
    }

    /**
     * Handler class which will be used by the loader for xml parsing
     */
    class KnMappingHandler extends DefaultHandler {

        private Map errorConfigMap;
        private String errorCode;
        private String majorCode;
        private String majorDescription;
        private String errorMsg;
        private String value;
        private String serviceErrorCode;
        private String httpStatusCode;

        public KnMappingHandler(Map errorConfigMap) {
            this.errorConfigMap = errorConfigMap;
        }

        /**
         * Receive notification of the start of an element.
         *
         * @param qName      The element type name.
         * @param attributes The specified or defaulted attributes.
         * @throws SAXException Any SAX exception, possibly
         *                                  wrapping another exception.
         * @see org.xml.sax.ContentHandler#startElement
         */
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if (KnMediatorConstants.ERROR_CONFIG.equals(qName)) {
                errorCode = attributes.getValue(KnMediatorConstants.ERROR_CODE);

            }
        }


        /**
         * Receive notification of character data inside an element.
         * <p/>
         * <p>By default, do nothing.  Application writers may override this
         * method to take specific actions for each chunk of character data
         * (such as adding the data to a node or buffer, or printing it to
         * a file).</p>
         *
         * @param ch     The characters.
         * @param start  The start position in the character array.
         * @param length The number of characters to use from the
         *               character array.
         */
        public void characters(char ch[], int start, int length) throws SAXException {
            String val = new String(ch, start, length);
            knLogger.debug( "characters", "Characters : "
                    , val , " Start : " , start
                    , "Length : " , length);
            if ((val != null) && (val.trim().length()) != 0) {
                if (value == null || value.length() == 0)
                    value = val;
                else
                    value += val;
            }
        }


        /**
         * Receive notification of the end of an element.
         *
         * @param qName The element type name.
         * @throws SAXException Any SAX exception, possibly
         *                                  wrapping another exception.
         * @see org.xml.sax.ContentHandler#endElement
         */
        public void endElement(String uri, String localName, String qName) throws SAXException {
            String tagName = qName;
            if (KnMediatorConstants.ERROR_CODE.equals(tagName)) {
                errorCode = value;
                value = null;
            } else if (KnMediatorConstants.SERVICE_ERROR_CODE.equals(tagName)) {
                if (value == null || value.trim().length() == 0) {
                    serviceErrorCode = errorCode;
                } else {
                    serviceErrorCode = value;
                }
                value = null;
            } else if (KnMediatorConstants.ERROR_MAJOR_CODE.equals(tagName)) {
                majorCode = value;
                value = null;
            } else if (KnMediatorConstants.ERROR_MSG.equals(tagName)) {
                errorMsg = value;
                value = null;
            } else if (KnMediatorConstants.ERROR_DESCRIPTION.equals(tagName)) {
                majorDescription = value;
                value = null;
            } else if (KnMediatorConstants.HTTP_STATUS_CODE.equals(tagName)) {
                httpStatusCode = value;
                value = null;
            } else if (KnMediatorConstants.ERROR_CONFIG.equals(tagName)) {
                if ( errorMsg != null) {
                    KnErrorBean errorBean = new KnErrorBean(errorCode, serviceErrorCode,
                             errorMsg, majorCode, majorDescription, httpStatusCode);
                    errorConfigMap.put(errorCode, errorBean);
                }

            }
        }
    }
}
