/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/***************************************************************************
 *
 * File name:   KnXmlLoader.java
 * Subsystem:   Configuration
 *
 * Name                 Date       Release
 * -------------------- ---------- ---------------------------------------
 * Rama Krishna         15-03-2007 6.0
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
package com.kodiak.xdms.server.common.configuration.loader.xml;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.configuration.cache.ICacheData;
import com.kodiak.xdms.server.common.configuration.cache.KnCacheDataException;
import com.kodiak.xdms.server.common.configuration.loader.KnModuleLoader;
import com.kodiak.xdms.server.common.configuration.loader.IModuleLoaderConfigData;
import com.kodiak.xdms.server.common.configuration.loader.KnLoaderValidationException;
import com.kodiak.xdms.server.common.configuration.KnLoaderException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This will load the XML files.
 */
public class KnXmlLoader extends KnModuleLoader {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXmlLoader.class);

    private static final String CLASS = KnXmlLoader.class.getName();

    /**
     * store the loader configuration data
     */
    KnXmlLoaderConfigData loaderConfigData;

    /**
     * constructor
     */
    public KnXmlLoader() {
        this.loaderConfigData = new KnXmlLoaderConfigData();
    }

    /**
     * This method will load the module.
     *
     * @return the cache element. it will return null if no caching is required.
     */
    public ICacheData load() throws KnLoaderException {
        try {
            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxFactory.newSAXParser();
            String[] xmlFiles = loaderConfigData.getXmlFile();
            for (int i = 0; i < xmlFiles.length; i++) {
                saxParser.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFiles[i].trim()),
                    new KnXmlHandler(getCacheData()));
            }
        } catch (FileNotFoundException e) {
            knLogger.error( "load", "File not found : " + e.getMessage());
            throw new KnLoaderException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                    "File not found " + e.getMessage(), e, loaderConfigData);
        } catch (IOException e) {
            knLogger.error( "load", "IO Error - " + e.getMessage());
            throw new KnLoaderException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                    "IO Error :" + e.getMessage(), e, loaderConfigData);
        } catch (ParserConfigurationException e) {
            knLogger.error( "load", "XML ParserConfiguration error - " + e.getMessage());
            throw new KnLoaderException(KnErrorCodes.ModuleLoader.INTERNAL_ERROR,
                    "Parser Configuration not available", e, loaderConfigData);
        } catch (SAXException e) {
            knLogger.error( "load", "XML Parsing error - " + e.getMessage());
            throw new KnLoaderException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                    "XML Parsing Error", e, loaderConfigData);
        } catch (Exception e) {
            knLogger.error( "load", "Unexpected error - " + e.getMessage());
            throw new KnLoaderException(KnErrorCodes.ModuleLoader.INTERNAL_ERROR,
                    "Unexpected error", e, loaderConfigData);
        }
        return getCacheData();
    }

    /**
     * It will return the Module loader configuration object
     *
     * @return the xml configuration data object
     */
    public IModuleLoaderConfigData getConfigData() {
        return this.loaderConfigData;
    }

    /**
     * handler class for XML parser. this is used by the XMLLoader
     */
    class KnXmlHandler extends DefaultHandler {

        public String tagIdLabel = loaderConfigData.getIdLabel();

        /**
         * store the key list
         */
        private ArrayList keyList = new ArrayList();
        /**
         * store the content
         */
        private String content = null;
        /**
         * store the cache data structire
         */
        private ICacheData cacheData;

        /**
         * constructor
         *
         * @param cacheData the cache data structure
         */
        public KnXmlHandler(ICacheData cacheData) {
            this.cacheData = cacheData;
        }

        /**
         * this method will handle the start element event
         *
         * @param uri
         * @param localName
         * @param qName
         * @param atts
         * @throws SAXException
         */
        public void startElement(String uri, String localName, String qName,
                                 Attributes atts)
                throws SAXException {

            String tagName = qName;
            String id = null;

            try {
                // first get the id since its required for further key generation.
                id = atts.getValue(tagIdLabel);
                if (id != null) {
                    if (id.trim().equals("")) {
                        throw new KnLoaderValidationException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                                "attribute '" + tagIdLabel + "' is empty for tag <" + tagName + ">",
                                loaderConfigData, "id", id);
                    }
                    tagName = id;
                }
                keyList.add(tagName);
                for (int i = 0, len = atts.getLength(); i < len; i++) {
                    String attrName = atts.getQName(i);
                    if (!attrName.equals(tagIdLabel)) {
                        String value = atts.getValue(i);
                        if (value == null || value.trim().equals("")) {
                            throw new KnLoaderValidationException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                                    "attribute '" + attrName + "' is empty for tag <" + tagName + ">",
                                    loaderConfigData, tagIdLabel, id);
                        } else {
                            keyList.add(attrName);
                            doCacheValue(value);
                            keyList.remove(keyList.size() - 1);
                        }
                    }
                }
            } catch (Exception e) {
                throw new SAXException(e);
            }
        }

        /**
         * This method will handle the end element event
         *
         * @param uri
         * @param localName
         * @param qName
         * @throws SAXException
         */
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if (content != null) {
                doCacheValue(content);
                content = null;
            } else {
                doCacheElement();
            }

            int len = keyList.size();
            if (len > 0) {
                keyList.remove(len - 1);
            }
        }

        /**
         * This method will extract the data
         *
         * @param ch
         * @param start
         * @param length
         * @throws SAXException
         */
        public void characters(char[] ch, int start, int length) throws SAXException {
            String value = new String(ch, start, length);
            if (value == null || value.trim().equals("")) {
                ; //do nothing.
            } else {
                if (content != null) {
                    content = content + value.trim();
                } else {
                    content = value.trim();
                }
            }
        }

        /**
         * This method will add the data to cache element
         *
         * @param value
         */
        private void doCacheValue(String value) throws SAXException {
            if (this.cacheData != null) {
                try {
                    knLogger.debug( "doCacheValue", "Key : " + keyList + "   value : " + value);
                    this.cacheData.addValue(this.keyList, value);
                } catch (KnCacheDataException e) {
                    throw new SAXException(e);
                }
            }
        }

        /**
         * This method will add the data to cache element
         *
         */
        private void doCacheElement() throws SAXException {
            if (this.cacheData != null) {
                try {
                    knLogger.debug( "doCacheElement", "Key : " + keyList);
                    this.cacheData.addElement(this.keyList);
                } catch (KnCacheDataException e) {
                    throw new SAXException(e);
                }
            }
        }
    }// end of class KnXmlHandler

}

