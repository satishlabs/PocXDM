/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**************************************************************************
 * <p/>
 * File name:   KnLibraryInitConfigData.java
 * Subsystem:   Configuration
 * <p/>
 * Name                 Date       Release
 * ------------------ ---------- ---------------------------------------
 * Rama Krishna       10-05-2007  6.0
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
 *************************************************************************/
package com.kodiak.xdms.server.common.configuration;

import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.configuration.init.KnInitConfigException;
import com.kodiak.xdms.server.common.configuration.init.KnInitLoaderConfigException;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class KnLibraryInitConfigData implements IConfigData {
	private static final KnLogger knLogger = KnLogger.getLogger(KnLibraryInitConfigData.class);

    private static final String CLASS = KnLibraryInitConfigData.class.getName();
    private static final String LIBRARY_INIT_TAG = "library-init";
    private static final String XML_FILE_TAG = "xml-file";

    private String xmlFile;
    private String libraryName;

    /**
     * This method will populateDto the fields from the dom element.
     * The element object passed to this method will be a dom
     * representation of the tag <library-config-data>
     *
     * @param element dom object of the tag '<library-config-data>'
     * @throws KnInitConfigException
     *          if the loader configuration data
     *          is missing or invalid
     */
    public void populate(Element element) throws KnInitConfigException {
        NodeList xmlNodeList = null;
        Element xmlElement = null;
        NodeList nodeList;
        Node node;
        String value;

        knLogger.debug( "populateDto",
                "populating loader config data...");
        try {
            xmlNodeList = element.getElementsByTagName(LIBRARY_INIT_TAG);
            if (xmlNodeList.getLength() == 1) {
                xmlElement = (Element) xmlNodeList.item(0);
            } else if (xmlNodeList.getLength() == 0) {
                knLogger.error( "populateDto",
                        "The <" + LIBRARY_INIT_TAG + "> tag is missing for library " + libraryName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "<" + LIBRARY_INIT_TAG + "> tag is missing",
                        LIBRARY_INIT_TAG, null, libraryName);
            } else {
                knLogger.error( "populateDto",
                        "The <" + LIBRARY_INIT_TAG + "> tag is repeating for library " + libraryName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "<" + LIBRARY_INIT_TAG + "> tag is repeating",
                        LIBRARY_INIT_TAG, null, libraryName);
            }

            nodeList = xmlElement.getElementsByTagName(XML_FILE_TAG);
            if (nodeList.getLength() == 1) {
                node = nodeList.item(0);
                value = node.getFirstChild().getNodeValue();
                if (value == null || value.trim().equals("")) {
                    knLogger.error( "populateDto",
                            "The <" + XML_FILE_TAG + "> tag under <" + LIBRARY_INIT_TAG + "> tag is empty for library " + libraryName);
                    throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                            "<" + XML_FILE_TAG + "> tag is empty",
                            XML_FILE_TAG, value, libraryName);
                } else {
                    this.xmlFile = value;
                }
            } else if (nodeList.getLength() == 0) {
                knLogger.error( "populateDto",
                        "The <" + XML_FILE_TAG + "> tag under <" + LIBRARY_INIT_TAG + "> tag is missing for library " + libraryName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "<" + XML_FILE_TAG + "> tag missing", XML_FILE_TAG, null, libraryName);
            } else {
                knLogger.error( "populateDto",
                        "The <" + XML_FILE_TAG + "> tag under <" + LIBRARY_INIT_TAG + "> tag is repeating for library " + libraryName);
                throw new KnInitLoaderConfigException(KnErrorCodes.ModuleLoader.CONFIG_ERROR,
                        "More than one <" + XML_FILE_TAG + "> tag", XML_FILE_TAG, null, libraryName);
            }

            knLogger.info( "populateDto",
                    "Loader config data populated successfully. Config : " + this);
        } finally {
            xmlNodeList = null;
            xmlElement = null;
            nodeList = null;
            node = null;
            value = null;
        }
    }

    public String getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(String xmlFile) {
        this.xmlFile = xmlFile;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }
}
