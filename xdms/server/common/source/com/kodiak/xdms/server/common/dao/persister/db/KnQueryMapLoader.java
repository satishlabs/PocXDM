/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnQueryMapLoader.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        14-02-2011      7.0
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
package com.kodiak.xdms.server.common.dao.persister.db;

import com.kodiak.logger.KnLogger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.Map;

import com.kodiak.logger.KnLogger;

public class KnQueryMapLoader {
	private static final KnLogger knLogger = KnLogger.getLogger(KnQueryMapLoader.class);

    private static final String CLASS = KnQueryMapLoader.class.getName();
    private String queryXml = null;
    private Map<String, KnQueryObject> queryInfoMap;


    public KnQueryMapLoader(String sqlMapFile, Map<String, KnQueryObject> queryObjectMap) {
        this.queryXml = sqlMapFile;
        this.queryInfoMap = queryObjectMap;
    }

    /**
     * This method Loads the validation xml file into the key value pair for the
     * attribute and their respective patterns
     *
     * @throws Exception
     */
    public void load() throws Exception {
        String methodName = "load()";
        knLogger.info( methodName, "Entry");
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(queryXml);
        saxParser.parse(input, new KnQueryMapLoader.KnQueryHandlerHandler(queryInfoMap));
        knLogger.debug( methodName, "queryInfoMap - " + queryInfoMap);
        knLogger.info( methodName, "Exit");
    }

    /**
     * Handler class which will be used by the loader for xml parsing
     */
    class KnQueryHandlerHandler extends DefaultHandler {

        private String queryId;
        private String query;
        private String value;
        private Map<String, KnQueryObject> queryInfoMap;

        public KnQueryHandlerHandler(Map<String, KnQueryObject> queryInfoMap) {
            this.queryInfoMap = queryInfoMap;
        }

        /**
         * Receive notification of the start of an element.
         *
         * @param qName      The element type name.
         * @param attributes The specified or defaulted attributes.
         * @throws org.xml.sax.SAXException Any SAX exception, possibly
         *                                  wrapping another exception.
         * @see org.xml.sax.ContentHandler#startElement
         */
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if ("query".equals(qName)) {
                queryId = attributes.getValue("id");
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
            knLogger.debug( "characters", "Characters : " + val + " Start : " + start
                    + "Length : " + length);
            if ((val.trim().length()) != 0) {
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
         * @throws org.xml.sax.SAXException Any SAX exception, possibly
         *                                  wrapping another exception.
         * @see org.xml.sax.ContentHandler#endElement
         */
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("query-string".equals(qName)) {
                if (queryInfoMap.get(queryId) == null) {
                    query = value;
                    KnQueryObject queryObj = new KnQueryObject(queryId, query);
                    queryInfoMap.put(queryId, queryObj);
                } else {
                    knLogger.error("endElement()", "QueryId already exist in Map");
                }
                value = null;
            }
        }
    }
}
